package com.tonywww.foodindustrial.machine.entity;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.definition.MachineMode;
import com.tonywww.foodindustrial.machine.processing.MachineStatus;
import com.tonywww.foodindustrial.machine.processing.RecipeProcessor;
import com.tonywww.foodindustrial.recipe.DeviceProfile;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import com.tonywww.foodindustrial.recipe.RecipeIndex;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Recipe-driven machine (machines/02 §4): every single-block machine except M21 and M30 is this class;
 * all differences come from its {@link MachineDefinition}.
 */
public class ProcessingMachineBlockEntity extends MachineBlockEntity {
    /**
     * The work in progress as the screen shows it (machines/04 §6): ticks done and needed at speed ×1,
     * FE per tick, the main product and the yield factor.
     */
    public record Job(int progress, int total, long energyPerTick, ItemStack item, FluidAmount fluid, int yieldPermille) {
    }

    protected final RecipeProcessor processor = new RecipeProcessor();

    public ProcessingMachineBlockEntity(MachineDefinition definition, BlockPos pos, BlockState state) {
        super(definition, pos, state);
    }

    public RecipeProcessor processor() {
        return processor;
    }

    /** The current work, or {@code null} without one: the running recipe (with its parallel copies). */
    @Nullable
    public Job job() {
        RecipeIndex.Entry entry = processor.current();
        if (entry == null) {
            return null;
        }
        ProcessRecipe recipe = entry.recipe();
        DeviceProfile profile = definition.profile();
        long energy = profile.energyPerTick(recipe) * upgrades().energyPermille() / 1000L * Math.max(1, processor.runs());
        return new Job(processor.progressTicks(), processor.totalTicks(), energy, processor.plannedItem(), processor.plannedFluid(),
            recipe.type().yieldScalable() ? profile.yieldPermille() : 1000);
    }

    @Override
    protected MachineStatus tickBatch(int delta, long gameTime) {
        lacking = definition.lacking(this);
        if (lacking == null) {
            lacking = modeLacking();
        }
        if (lacking != null) {
            return MachineStatus.MISSING;
        }
        return processor.tick(this, delta);
    }

    /** Translation key of a capability the current mode needs and the machine lacks (air), or {@code null}. */
    @Nullable
    protected final String modeLacking() {
        MachineMode current = mode();
        if (current == null) {
            return null;
        }
        // every batch: the bit test first, the key set only when something is missing (rule 10)
        if (capabilities().covers(current.needs())) {
            return null;
        }
        Set<String> missing = capabilities().missing(current.needs());
        return missing.isEmpty() ? null : Lang.condition(missing.iterator().next());
    }

    @Override
    protected boolean outputFluidsBlocked() {
        return processor.fluidsBlocked();
    }

    @Override
    protected void inputsChanged() {
        processor.inputsChanged();
    }

    @Override
    protected void outputsChanged() {
        processor.outputsChanged();
    }

    @Override
    protected void modeChanged() {
        processor.inputsChanged();
    }

    @Override
    protected void saveData(DataWriter out) {
        super.saveData(out);
        processor.save(out);
    }

    @Override
    protected void loadData(DataReader in) {
        super.loadData(in);
        processor.load(in);
    }
}
