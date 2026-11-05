package com.tonywww.foodindustrial.machine.entity;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.processing.MachineStatus;
import com.tonywww.foodindustrial.machine.processing.RecipeProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Recipe-driven machine (machines/02 §4): every single-block machine except M21 and M30 is this class;
 * all differences come from its {@link MachineDefinition}.
 */
public class ProcessingMachineBlockEntity extends MachineBlockEntity {
    protected final RecipeProcessor processor = new RecipeProcessor();

    public ProcessingMachineBlockEntity(MachineDefinition definition, BlockPos pos, BlockState state) {
        super(definition, pos, state);
    }

    public RecipeProcessor processor() {
        return processor;
    }

    @Override
    protected MachineStatus tickBatch(int delta, long gameTime) {
        return processor.tick(this, delta);
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
