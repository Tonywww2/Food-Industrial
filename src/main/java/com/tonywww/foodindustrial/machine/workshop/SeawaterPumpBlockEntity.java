package com.tonywww.foodindustrial.machine.workshop;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.entity.MachineBlockEntity;
import com.tonywww.foodindustrial.machine.processing.MachineStatus;
import com.tonywww.foodindustrial.recipe.Capabilities;
import com.tonywww.foodindustrial.recipe.Environment;
import com.tonywww.foodindustrial.recipe.ProcessTypes;
import com.tonywww.foodindustrial.world.interaction.PumpingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * M54 electric seawater pump (I43, additives/07 §M54): no inputs; on the coast it pumps one W03 batch
 * (1,000 mB of seawater) every 40 ticks into its 4,000 mB tank, taking 10 FE/t. Elsewhere it stops and the status
 * shows the missing "coastal".
 */
public class SeawaterPumpBlockEntity extends MachineBlockEntity {
    /** Ticks per batch at speed ×1. */
    public static final int BATCH_TICKS = 40;
    private static final Capabilities COASTAL = Capabilities.of("environment.coastal");

    private long progress;

    public SeawaterPumpBlockEntity(MachineDefinition definition, BlockPos pos, BlockState state) {
        super(definition, pos, state);
    }

    @Override
    protected Capabilities extraCapabilities() {
        return level != null && Environment.coastal(level, worldPosition) ? COASTAL : Capabilities.NONE;
    }

    @Override
    protected void onNeighborBlockChanged() {
        super.onNeighborBlockChanged();
        recomputeCapabilities();
        wake();
    }

    @Override
    protected MachineStatus tickBatch(int delta, long gameTime) {
        if (!redstone().allows(isPowered())) {
            return MachineStatus.REDSTONE;
        }
        FluidAmount batch = PumpingRecipe.batch(level, capabilities());
        if (batch == null || batch.isEmpty()) {
            lacking = Lang.condition("environment.coastal");
            progress = 0;
            return MachineStatus.MISSING;
        }
        lacking = null;
        if (!tanks().outputsFit(new FluidAmount[]{batch}, 1)) {
            return MachineStatus.OUTPUT_FULL;
        }
        long need = (long) ProcessTypes.PUMPING.defaultEnergy() * definition.profile().energyPermille() / 1000L
            * upgrades().energyPermille() / 1000L * delta;
        if (!energy().consume(need)) {
            return MachineStatus.NO_POWER;
        }
        progress += (long) delta * definition.profile().speed() * speedPermille();
        // as RecipeProcessor: speed is the tier multiplier, speedPermille the upgrades
        long goal = BATCH_TICKS * 1000L;
        if (progress >= goal) {
            progress -= goal;
            tanks().fillOutput(batch, false);
            outputsChanged();
        }
        markUnsaved();
        return MachineStatus.RUNNING;
    }

    /** Progress of the batch, 0–1000. */
    public int progressPermille() {
        return (int) Math.min(1000, progress / BATCH_TICKS);
    }

    @Override
    protected void saveData(DataWriter out) {
        super.saveData(out);
        out.putLong("PumpProgress", progress);
    }

    @Override
    protected void loadData(DataReader in) {
        super.loadData(in);
        progress = in.getLong("PumpProgress", 0);
    }
}
