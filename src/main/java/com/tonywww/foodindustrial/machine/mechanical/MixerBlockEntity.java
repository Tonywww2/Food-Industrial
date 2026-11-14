package com.tonywww.foodindustrial.machine.mechanical;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityData;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.machine.component.SlotRole;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.definition.MachineMode;
import com.tonywww.foodindustrial.machine.entity.ProcessingMachineBlockEntity;
import com.tonywww.foodindustrial.machine.processing.MachineStatus;
import com.tonywww.foodindustrial.platform.PlatformItems;
import com.tonywww.foodindustrial.recipe.OutcomeResolver;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * M21 mixer (machines/02 §4, additives/06 §T2 机械化：操作与实现规格): blending runs R19 recipes like any
 * machine; homogenizing and normalizing are built-in jobs worked out from the input slots
 * (additives/10 §2.2 rule 3, §8):
 * <ul>
 *   <li>homogenizing: every stack of the first purity-bearing item in the inputs (same item, any
 *   purity) becomes one stack of at most 64, its purity the count-weighted average rounded down;
 *   100 ticks, 10 FE/t;</li>
 *   <li>normalizing: another mod's item in a material's tag becomes this mod's item 1:1, at the foreign
 *   item's effective purity (external default or rule); 20 ticks, 10 FE/t, one item per parallel run.</li>
 * </ul>
 * With the standardisation module both products drop to their grade's lower bound (machines/01 §1.2).
 */
public class MixerBlockEntity extends ProcessingMachineBlockEntity {
    public static final String HOMOGENIZING = "homogenizing";
    public static final String NORMALIZING = "normalizing";
    static final int HOMOGENIZE_TICKS = 100;
    static final int NORMALIZE_TICKS = 20;
    static final int JOB_ENERGY = 10;
    private static final int HOMOGENIZE_MAX = 64;

    /** A planned job: how many items to take from which input slots, and the product. */
    private record Plan(int[] slots, int[] counts, ItemStack product, int ticks) {
    }

    @Nullable
    private Plan plan;
    private long jobProgress;
    private boolean jobDirty = true;
    private boolean jobOutputsDirty = true;
    private boolean jobFits;

    public MixerBlockEntity(MachineDefinition definition, BlockPos pos, BlockState state) {
        super(definition, pos, state);
    }

    /** Whether the current mode is one of the built-in jobs. */
    private boolean jobMode() {
        MachineMode current = mode();
        return current != null && (current.id().equals(HOMOGENIZING) || current.id().equals(NORMALIZING));
    }

    @Override
    protected MachineStatus tickBatch(int delta, long gameTime) {
        if (!jobMode()) {
            return super.tickBatch(delta, gameTime);
        }
        lacking = null;
        if (!redstone().allows(isPowered())) {
            return MachineStatus.REDSTONE;
        }
        if (jobDirty) {
            jobDirty = false;
            Plan next = planJob();
            // progress carries on for the same product (also after loading, when there is no plan yet)
            if (next == null || plan != null && !PlatformItems.sameItemSameData(next.product(), plan.product())) {
                jobProgress = 0;
            }
            plan = next;
            jobOutputsDirty = true;
        }
        if (plan == null) {
            jobProgress = 0;
            return MachineStatus.WAITING;
        }
        if (jobOutputsDirty) {
            jobOutputsDirty = false;
            jobFits = inventory().outputsFit(new ItemStack[]{plan.product()}, 1, 0);
        }
        if (!jobFits) {
            return MachineStatus.OUTPUT_FULL;
        }
        if (!energy().consume(jobEnergyPerTick() * delta)) {
            return MachineStatus.NO_POWER;
        }
        jobProgress += (long) delta * definition.profile().speed() * speedPermille();
        long goal = plan.ticks() * 1000L;
        if (jobProgress >= goal) {
            jobProgress -= goal;
            finishJob(plan);
        }
        markUnsaved();
        return MachineStatus.RUNNING;
    }

    private long jobEnergyPerTick() {
        return (long) JOB_ENERGY * definition.profile().energyPermille() / 1000L * upgrades().energyPermille() / 1000L;
    }

    @Nullable
    private Plan planJob() {
        MachineMode current = mode();
        if (current == null) {
            return null;
        }
        return current.id().equals(HOMOGENIZING) ? planHomogenizing() : planNormalizing();
    }

    /** The first purity-bearing item of the inputs, every stack of it, up to 64 items. */
    @Nullable
    private Plan planHomogenizing() {
        int inputs = inventory().count(SlotRole.INPUT);
        for (int first = 0; first < inputs; first++) {
            ItemStack stack = inventory().get(SlotRole.INPUT, first);
            if (stack.isEmpty() || !Purity.bears(stack.getItem())) {
                continue;
            }
            ItemStack key = unpure(stack);
            int limit = Math.min(HOMOGENIZE_MAX, stack.getMaxStackSize());
            List<Integer> slots = new ArrayList<>();
            List<Integer> counts = new ArrayList<>();
            List<Integer> values = new ArrayList<>();
            int total = 0;
            for (int i = first; i < inputs && total < limit; i++) {
                ItemStack other = inventory().get(SlotRole.INPUT, i);
                if (other.isEmpty() || other.getItem() != stack.getItem() || !PlatformItems.sameItemSameData(key, unpure(other))) {
                    continue;
                }
                int take = Math.min(other.getCount(), limit - total);
                slots.add(i);
                counts.add(take);
                values.add(Purity.get(other));
                total += take;
            }
            int[] b = values.stream().mapToInt(Integer::intValue).toArray();
            long[] weights = counts.stream().mapToLong(Integer::longValue).toArray();
            int average = PurityScale.floorOf(PurityScale.weightedAverage(b, weights));
            ItemStack product = Purity.with(stack.copyWithCount(total), standardised(average, Purity.materialOf(stack.getItem())));
            return new Plan(slots.stream().mapToInt(Integer::intValue).toArray(), counts.stream().mapToInt(Integer::intValue).toArray(),
                product, HOMOGENIZE_TICKS);
        }
        return null;
    }

    /** The first foreign item of the inputs that belongs to a material with an item of this mod. */
    @Nullable
    private Plan planNormalizing() {
        int inputs = inventory().count(SlotRole.INPUT);
        for (int i = 0; i < inputs; i++) {
            ItemStack stack = inventory().get(SlotRole.INPUT, i);
            if (stack.isEmpty() || Purity.isOwn(stack.getItem())) {
                continue;
            }
            String material = Purity.materialOf(stack.getItem());
            Item own = Purity.itemOf(material);
            if (own == null) {
                continue;
            }
            int count = Math.min(stack.getCount(), upgrades().parallel());
            int b = Purity.get(stack);
            ItemStack product = new ItemStack(own, count);
            if (b != Purity.NONE) {
                product = Purity.with(product, standardised(b, material));
            }
            return new Plan(new int[]{i}, new int[]{count}, product, NORMALIZE_TICKS);
        }
        return null;
    }

    private int standardised(int b, @Nullable String material) {
        return upgrades().normalizing() ? OutcomeResolver.normalize(b, PurityData.current().material(material)) : b;
    }

    /** {@code stack} as one item without its stored purity, to compare items regardless of purity. */
    private static ItemStack unpure(ItemStack stack) {
        return Purity.with(stack.copyWithCount(1), 0);
    }

    private void finishJob(Plan done) {
        for (int i = 0; i < done.slots().length; i++) {
            inventory().shrink(inventory().slot(SlotRole.INPUT, done.slots()[i]), done.counts()[i]);
        }
        inventory().insertOutput(done.product(), true, false);
        jobDirty = true;
        jobOutputsDirty = true;
    }

    @Override
    public @Nullable Job job() {
        if (!jobMode()) {
            return super.job();
        }
        if (plan == null) {
            return null;
        }
        return new Job((int) (jobProgress / 1000), plan.ticks(), jobEnergyPerTick(), plan.product(), FluidAmount.EMPTY, 1000);
    }

    @Override
    protected void inputsChanged() {
        super.inputsChanged();
        jobDirty = true;
    }

    @Override
    protected void outputsChanged() {
        super.outputsChanged();
        jobOutputsDirty = true;
    }

    @Override
    protected void modeChanged() {
        super.modeChanged();
        jobDirty = true;
    }

    @Override
    protected void saveData(DataWriter out) {
        super.saveData(out);
        out.putLong("JobProgress", jobProgress);
    }

    @Override
    protected void loadData(DataReader in) {
        super.loadData(in);
        jobProgress = Math.max(0, in.getLong("JobProgress", 0));
        jobDirty = true;
        jobOutputsDirty = true;
    }
}
