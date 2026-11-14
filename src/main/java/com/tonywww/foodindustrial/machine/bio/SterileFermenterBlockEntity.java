package com.tonywww.foodindustrial.machine.bio;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.entity.MultiblockControllerBlockEntity;
import com.tonywww.foodindustrial.machine.processing.MachineStatus;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * M29 aseptic fermenter (I17, additives/06 §T4 生物 / 精细：操作与实现规格): every vessel layer adds one
 * parallel copy (times the parallel upgrades); each batch starts with {@link #STERILIZE_TICKS} of
 * sterilization in place. A batch that stopped on the way (no power, no air, redstone, structure broken)
 * is contaminated with {@link #CONTAMINATION_PERCENT}% chance when it completes: the main product comes
 * out halved, its purity unchanged (2026-11-11). Batches that run through are never contaminated.
 */
public class SterileFermenterBlockEntity extends MultiblockControllerBlockEntity {
    public static final int STERILIZE_TICKS = 100;
    public static final int CONTAMINATION_PERCENT = 30;

    /** The running batch stopped on the way. */
    private boolean interrupted;
    /** The completion being made is contaminated. */
    private boolean contaminated;
    /** The last batch was contaminated (info panel). */
    private boolean lastContaminated;

    public SterileFermenterBlockEntity(MachineDefinition definition, BlockPos pos, BlockState state) {
        super(definition, pos, state);
    }

    /** Vessel layers of the formed fermenter (1–4). */
    public int vesselLayers() {
        return formed() ? Math.max(1, layers() - 2) : 1;
    }

    @Override
    public int structureParallel() {
        return vesselLayers();
    }

    @Override
    public int setupTicks() {
        return STERILIZE_TICKS;
    }

    @Override
    public boolean lastBatchContaminated() {
        return lastContaminated;
    }

    /** Whether the running batch stopped on the way and may come out contaminated (Jade). */
    public boolean interrupted() {
        return interrupted;
    }

    @Override
    protected MachineStatus tickBatch(int delta, long gameTime) {
        MachineStatus status = super.tickBatch(delta, gameTime);
        if (processor.current() == null) {
            interrupted = false;
        } else if (processor.progressTicks() > 0 && !interrupted && (status == MachineStatus.NO_POWER || status == MachineStatus.MISSING
            || status == MachineStatus.REDSTONE || status == MachineStatus.UNFORMED)) {
            interrupted = true;
            markUnsaved();
        }
        if (status == MachineStatus.RUNNING && processor.current() != null && processor.progressTicks() < STERILIZE_TICKS) {
            return MachineStatus.STERILIZING;
        }
        return status;
    }

    @Override
    public void completing(ProcessRecipe recipe) {
        contaminated = interrupted && level.getRandom().nextInt(100) < CONTAMINATION_PERCENT;
    }

    @Override
    public int outputPermille(ProcessRecipe recipe, int index) {
        return index == 0 && contaminated ? 500 : 1000;
    }

    @Override
    public void completed(ProcessRecipe recipe, int[] catalystSlots) {
        if (lastContaminated != contaminated) {
            lastContaminated = contaminated;
            markDirty();
        }
        contaminated = false;
        interrupted = false;
    }

    @Override
    protected void saveData(DataWriter out) {
        super.saveData(out);
        out.putBoolean("Interrupted", interrupted);
        out.putBoolean("Contaminated", lastContaminated);
    }

    @Override
    protected void loadData(DataReader in) {
        super.loadData(in);
        interrupted = in.getBoolean("Interrupted", false);
        lastContaminated = in.getBoolean("Contaminated", false);
    }
}
