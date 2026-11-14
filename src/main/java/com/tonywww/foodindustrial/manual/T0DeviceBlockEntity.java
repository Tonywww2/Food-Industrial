package com.tonywww.foodindustrial.manual;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.data.DataWriter;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.storage.FluidTransfer;
import com.tonywww.foodindustrial.gui.GuiText;
import com.tonywww.foodindustrial.machine.entity.FIBlockEntity;
import com.tonywww.foodindustrial.platform.PlatformFluidContainers;
import com.tonywww.foodindustrial.platform.PlatformFluids;
import com.tonywww.foodindustrial.platform.PlatformPlayers;
import com.tonywww.foodindustrial.platform.machine.PlatformEntityBlock.UseResult;
import com.tonywww.foodindustrial.recipe.Capabilities;
import com.tonywww.foodindustrial.recipe.Catalyst;
import com.tonywww.foodindustrial.recipe.Environment;
import com.tonywww.foodindustrial.recipe.ProcessInput;
import com.tonywww.foodindustrial.recipe.ProcessOutput;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.RecipeIndex;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A placed T0 device (additives/07 §操作与实现规格, 2026-11-10): contents that change in place, one
 * {@link InPlaceProcess} per unit. Passive devices run one batch every 20 ticks at a phase spread by
 * position (machines/02 §6.1 rule 3); the pots advance one step per stir. No screen and no automation,
 * except that the leaching barrel drains its liquor into the block below.
 */
public class T0DeviceBlockEntity extends FIBlockEntity {
    /** Largest group of joined salt pans. */
    private static final int MAX_PANS = 64;

    private final T0Kind kind;
    private final DeviceContents contents;
    private final InPlaceProcess[] units;
    private final int phase;
    private boolean pendingSync;
    /** Fumigation box: ticks of the current fumigation. */
    private int fumigation;

    public T0DeviceBlockEntity(BlockEntityType<?> type, T0Kind kind, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.kind = kind;
        this.contents = kind.newContents();
        InPlaceProcess.Unit[] layout = kind.units();
        this.units = new InPlaceProcess[layout.length];
        for (int i = 0; i < layout.length; i++) {
            units[i] = new InPlaceProcess(layout[i]);
        }
        this.phase = Math.floorMod((int) (pos.asLong() ^ (pos.asLong() >>> 32)), T0Kind.BATCH);
    }

    public T0Kind kind() {
        return kind;
    }

    public DeviceContents contents() {
        return contents;
    }

    // ---- capabilities ----

    /** What the device offers right now: its state, and for passive devices the environment. */
    public Capabilities capabilities() {
        return switch (kind) {
            case CLAY_POT, IRON_POT -> heated() ? kind.ownCapabilities() : Capabilities.NONE;
            case CROCK -> Capabilities.of(sealed() ? "atmosphere.airtight" : "atmosphere.air", "method.settling")
                .with(Environment.at(level, worldPosition));
            default -> kind.ownCapabilities().with(Environment.at(level, worldPosition));
        };
    }

    /** Whether a lit heat source sits right below (the pots). */
    public boolean heated() {
        return level != null && T0Devices.isHeatSource(level.getBlockState(worldPosition.below()));
    }

    public boolean sealed() {
        BlockState state = getBlockState();
        return state.hasProperty(T0DeviceBlock.SEALED) && state.getValue(T0DeviceBlock.SEALED);
    }

    // ---- ticking ----

    @Override
    public void serverTick() {
        if (pendingSync) {
            pendingSync = false;
            syncToClient();
        }
        if (kind.manual() || (level.getGameTime() + phase) % T0Kind.BATCH != 0 || contents.isEmpty()) {
            return;
        }
        runBatch(T0Kind.BATCH);
    }

    /** One batch of a passive device covering {@code delta} ticks; development commands pass more to skip time. */
    public void runBatch(int delta) {
        if (kind.manual() || contents.isEmpty()) {
            return;
        }
        boolean changed = false;
        if (kind == T0Kind.FUMIGATION_BOX) {
            changed = fumigate(delta);
        } else {
            Capabilities current = capabilities();
            boolean rain = current.keys().contains("environment.rain");
            for (InPlaceProcess unit : units) {
                if (kind == T0Kind.SALT_PAN && rain) {
                    unit.resetProgress();
                }
                InPlaceProcess.Step step = unit.tick(level, kind.spec(), contents, current, delta);
                if (step == InPlaceProcess.Step.DONE) {
                    changed = true;
                } else if (step == InPlaceProcess.Step.PROGRESS) {
                    markUnsaved();
                }
            }
        }
        if (kind == T0Kind.LEACH_BARREL && drainBelow()) {
            changed = true;
        }
        if (changed) {
            contentsChanged();
        } else {
            flushDirty();
        }
    }

    private void contentsChanged() {
        markDirty();
        flushDirty();
        pendingSync = true;
    }

    /** The leaching barrel pushes up to 1,000 mB of liquor into the block below each batch. */
    private boolean drainBelow() {
        FluidAmount liquor = contents.tank(1);
        if (liquor.isEmpty()) {
            return false;
        }
        FluidTransfer below = neighbors().fluids(Direction.DOWN);
        if (below == null) {
            return false;
        }
        int filled = below.fill(liquor.withAmount(Math.min(liquor.amount(), 1000)), false);
        if (filled <= 0) {
            return false;
        }
        contents.setTank(1, liquor.withAmount(liquor.amount() - filled));
        return true;
    }

    /** One batch of the fumigation box: sulfur burns under food that can be fumigated and is not yet. */
    private boolean fumigate(int delta) {
        boolean food = false;
        for (int i = 0; i < contents.itemSlots(); i++) {
            if (Fumigation.canFumigate(contents.item(i))) {
                food = true;
            }
        }
        if (!food || !Fumigation.isFuel(contents.catalyst(0))) {
            fumigation = 0;
            return false;
        }
        fumigation += delta;
        if (fumigation < Fumigation.TIME) {
            markUnsaved();
            return false;
        }
        fumigation = 0;
        for (int i = 0; i < contents.itemSlots(); i++) {
            if (Fumigation.canFumigate(contents.item(i))) {
                contents.setItem(i, Fumigation.mark(contents.item(i), level.registryAccess()));
            }
        }
        ItemStack fuel = contents.catalyst(0).copy();
        fuel.shrink(1);
        contents.setCatalyst(0, fuel);
        return true;
    }

    // ---- interaction ----

    /** A right click on the block with {@code held} in the main hand. */
    public UseResult use(Player player, InteractionHand hand, ItemStack held, BlockHitResult hit) {
        if (level.isClientSide()) {
            return UseResult.SUCCESS;
        }
        if (held.isEmpty()) {
            if (player.isSecondaryUseActive()) {
                takeOut(player);
            } else if (kind.isPot()) {
                stir(player);
            } else {
                if (kind == T0Kind.CROCK) {
                    level.setBlockAndUpdate(worldPosition, getBlockState().setValue(T0DeviceBlock.SEALED, !sealed()));
                    level.playSound(null, worldPosition, sealed() ? SoundEvents.WOODEN_TRAPDOOR_CLOSE : SoundEvents.WOODEN_TRAPDOOR_OPEN,
                        SoundSource.BLOCKS, 0.6F, 1.0F);
                }
                PlatformPlayers.actionBar(player, describe());
            }
            return UseResult.SUCCESS;
        }
        if (contents.tankCount() > 0 && isFluidContainer(held)) {
            return useContainer(player, hand, held) ? UseResult.SUCCESS : UseResult.PASS_TO_ITEM;
        }
        if (insert(player, hand, held)) {
            level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.5F, 1.0F);
            contentsChanged();
            return UseResult.SUCCESS;
        }
        PlatformPlayers.actionBar(player, Component.translatable(GuiText.key("manual", "cannot_hold")));
        return UseResult.CONSUME;
    }

    /** A bucket or another item that holds or can take a liquid. */
    private static boolean isFluidContainer(ItemStack held) {
        if (!PlatformFluidContainers.contained(held).isEmpty()) {
            return true;
        }
        for (int size : CONTAINER_SIZES) {
            if (!PlatformFluidContainers.filled(held.copyWithCount(1), FluidAmount.of(Fluids.WATER, size)).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /** One stir of a pot. */
    private void stir(Player player) {
        if (!heated()) {
            PlatformPlayers.actionBar(player, Component.translatable(GuiText.key("manual", "not_heated")));
        }
        InPlaceProcess unit = units[0];
        InPlaceProcess.Step step = unit.act(level, kind.spec(), contents, capabilities());
        if (step == InPlaceProcess.Step.IDLE) {
            if (heated()) {
                PlatformPlayers.actionBar(player, describe());
            }
            return;
        }
        level.playSound(null, worldPosition, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.3F, 1.4F);
        if (step == InPlaceProcess.Step.DONE) {
            contentsChanged();
            PlatformPlayers.actionBar(player, Component.translatable(GuiText.key("manual", "done")));
        } else {
            markUnsaved();
            flushDirty();
            PlatformPlayers.actionBar(player, Component.translatable(GuiText.key("manual", "stir"), unit.progress(),
                unit.current().recipe().manualActions()));
        }
    }

    /** Puts the held items in: catalysts into the catalyst slot, one batch per free position, else as many as fit. */
    private boolean insert(Player player, InteractionHand hand, ItemStack held) {
        if (contents.catalystSlots() > 0 && isCatalyst(held)) {
            for (int i = 0; i < contents.catalystSlots(); i++) {
                ItemStack here = contents.catalyst(i);
                if (here.isEmpty()) {
                    contents.setCatalyst(i, held.copyWithCount(held.getCount()));
                    consume(player, hand, held, held.getCount());
                    return true;
                }
            }
            return false;
        }
        if (kind == T0Kind.FUMIGATION_BOX ? !Fumigation.canFumigate(held) && !Fumigation.isMarked(held) : !isInput(held)) {
            return false;
        }
        if (kind.positions()) {
            int batch = batchSize(held);
            if (batch <= 0 || held.getCount() < batch) {
                return false;
            }
            for (int i = 0; i < contents.itemSlots(); i++) {
                if (contents.item(i).isEmpty()) {
                    contents.setItem(i, held.copyWithCount(batch));
                    consume(player, hand, held, batch);
                    return true;
                }
            }
            return false;
        }
        int[] all = new int[contents.itemSlots()];
        for (int i = 0; i < all.length; i++) {
            all[i] = i;
        }
        ItemStack rest = contents.insertItem(all, held);
        int moved = held.getCount() - rest.getCount();
        if (moved <= 0) {
            return false;
        }
        consume(player, hand, held, moved);
        return true;
    }

    private static void consume(Player player, InteractionHand hand, ItemStack held, int count) {
        if (!player.getAbilities().instabuild) {
            held.shrink(count);
            if (held.isEmpty()) {
                player.setItemInHand(hand, ItemStack.EMPTY);
            }
        }
    }

    /** Items of one batch on a position: the count of the most specific recipe the device runs on them. */
    private int batchSize(ItemStack held) {
        RecipeIndex index = RecipeIndex.get(level);
        ProcessRecipe best = null;
        for (ProcessType type : kind.spec().types()) {
            for (RecipeIndex.Entry entry : index.candidates(type, held.getItem())) {
                ProcessRecipe recipe = entry.recipe();
                if (entry.runsOn(kind.spec()) && recipe.inputs().get(0) instanceof ProcessInput.ItemInput item && item.ingredient().test(held)
                    && (best == null || recipe.specificity() > best.specificity())) {
                    best = recipe;
                }
            }
        }
        return best == null ? 0 : ((ProcessInput.ItemInput) best.inputs().get(0)).count();
    }

    /** Whether {@code stack} is an item input of a recipe the device runs: other items stay out. */
    private boolean isInput(ItemStack stack) {
        RecipeIndex index = RecipeIndex.get(level);
        for (ProcessType type : kind.spec().types()) {
            for (RecipeIndex.Entry entry : index.all(type)) {
                if (!entry.runsOn(kind.spec())) {
                    continue;
                }
                for (ProcessInput input : entry.recipe().inputs()) {
                    if (input instanceof ProcessInput.ItemInput item && item.ingredient().test(stack)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Whether {@code stack} is a catalyst of a recipe the device runs (the fumigation box burns sulfur). */
    private boolean isCatalyst(ItemStack stack) {
        if (kind == T0Kind.FUMIGATION_BOX) {
            return Fumigation.isFuel(stack);
        }
        RecipeIndex index = RecipeIndex.get(level);
        for (ProcessType type : kind.spec().types()) {
            for (RecipeIndex.Entry entry : index.all(type)) {
                for (Catalyst catalyst : entry.recipe().catalysts()) {
                    if (entry.runsOn(kind.spec()) && catalyst.test(stack)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Takes out one stack: products and contents first (last slot first), then the catalyst. */
    private void takeOut(Player player) {
        if (kind == T0Kind.SALT_PAN) {
            boolean any = false;
            for (T0DeviceBlockEntity pan : group()) {
                ItemStack salt = pan.contents.item(0);
                if (!salt.isEmpty()) {
                    give(player, salt);
                    pan.contents.setItem(0, ItemStack.EMPTY);
                    pan.contentsChanged();
                    any = true;
                }
            }
            if (!any) {
                PlatformPlayers.actionBar(player, describe());
            }
            return;
        }
        for (int i = contents.itemSlots() - 1; i >= 0; i--) {
            if (!contents.item(i).isEmpty()) {
                give(player, contents.item(i));
                contents.setItem(i, ItemStack.EMPTY);
                contentsChanged();
                return;
            }
        }
        for (int i = 0; i < contents.catalystSlots(); i++) {
            if (!contents.catalyst(i).isEmpty()) {
                give(player, contents.catalyst(i));
                contents.setCatalyst(i, ItemStack.EMPTY);
                contentsChanged();
                return;
            }
        }
        PlatformPlayers.actionBar(player, describe());
    }

    private static void give(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack.copy())) {
            player.drop(stack.copy(), false);
        }
    }

    /** A bucket pours into the inlet tank, an empty one takes from the outlet first. */
    private boolean useContainer(Player player, InteractionHand hand, ItemStack held) {
        FluidAmount inside = PlatformFluidContainers.contained(held);
        List<T0DeviceBlockEntity> group = kind == T0Kind.SALT_PAN ? group() : List.of(this);
        ItemStack result;
        if (!inside.isEmpty()) {
            boolean fits = kind == T0Kind.SALT_PAN ? pour(group, inside, true) : pourOwn(inside, true);
            if (!fits) {
                PlatformPlayers.actionBar(player, Component.translatable(GuiText.key("manual", "cannot_hold")));
                return true;
            }
            result = PlatformFluidContainers.drained(held);
            if (result.isEmpty() && held.getCount() > 1) {
                return false;
            }
            if (kind == T0Kind.SALT_PAN) {
                pour(group, inside, false);
            } else {
                pourOwn(inside, false);
            }
        } else {
            result = ItemStack.EMPTY;
            for (int unit : CONTAINER_SIZES) {
                FluidAmount taken = take(group, unit, true);
                if (taken.amount() == unit) {
                    result = PlatformFluidContainers.filled(held, taken);
                    if (!result.isEmpty()) {
                        take(group, unit, false);
                        break;
                    }
                }
            }
            if (result.isEmpty()) {
                return false;
            }
        }
        if (!player.getAbilities().instabuild) {
            if (held.getCount() == 1) {
                player.setItemInHand(hand, result);
            } else {
                held.shrink(1);
                player.getInventory().placeItemBackInInventory(result);
            }
        }
        level.playSound(null, worldPosition, inside.isEmpty() ? SoundEvents.BUCKET_FILL : SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.6F, 1.0F);
        for (T0DeviceBlockEntity device : group) {
            device.contentsChanged();
        }
        return true;
    }

    /** Amounts a container takes: a bucket, then a liquid bottle. */
    private static final int[] CONTAINER_SIZES = {1000, 250};

    /**
     * Pours {@code fluid} into one tank of this device: the tank holding the same fluid, else an empty
     * one (the leaching barrel only into its inlet); whole or nothing.
     */
    private boolean pourOwn(FluidAmount fluid, boolean simulate) {
        int count = kind == T0Kind.LEACH_BARREL ? 1 : contents.tankCount();
        int target = -1;
        for (int i = 0; i < count && target < 0; i++) {
            FluidAmount here = contents.tank(i);
            if (!here.isEmpty() && here.fluid() == fluid.fluid() && here.amount() + fluid.amount() <= contents.tankCapacity()) {
                target = i;
            }
        }
        for (int i = 0; i < count && target < 0; i++) {
            if (contents.tank(i).isEmpty() && fluid.amount() <= contents.tankCapacity()) {
                target = i;
            }
        }
        if (target < 0) {
            return false;
        }
        if (!simulate) {
            FluidAmount here = contents.tank(target);
            contents.setTank(target, here.isEmpty() ? fluid : Purity.merge(here, fluid));
        }
        return true;
    }

    /** Pours {@code fluid} into the inlet tanks of joined salt pans, spread evenly; whole or nothing. */
    private static boolean pour(List<T0DeviceBlockEntity> group, FluidAmount fluid, boolean simulate) {
        List<T0DeviceBlockEntity> open = new ArrayList<>();
        int room = 0;
        for (T0DeviceBlockEntity device : group) {
            FluidAmount here = device.contents.tank(0);
            if (here.isEmpty() || here.fluid() == fluid.fluid()) {
                open.add(device);
                room += device.contents.tankCapacity() - here.amount();
            }
        }
        if (room < fluid.amount()) {
            return false;
        }
        if (simulate) {
            return true;
        }
        int left = fluid.amount();
        open.sort((a, b) -> Integer.compare(a.contents.tank(0).amount(), b.contents.tank(0).amount()));
        for (int i = 0; i < open.size() && left > 0; i++) {
            T0DeviceBlockEntity device = open.get(i);
            FluidAmount here = device.contents.tank(0);
            int share = Math.min(device.contents.tankCapacity() - here.amount(), (left + open.size() - i - 1) / (open.size() - i));
            FluidAmount added = fluid.withAmount(share);
            device.contents.setTank(0, here.isEmpty() ? added : Purity.merge(here, added));
            left -= share;
        }
        return left == 0;
    }

    /**
     * Takes up to {@code amount} of one fluid from the group: the leaching barrel's outlet first, then
     * the inlets; joined pans give from every pan holding the fluid of this one.
     */
    private FluidAmount take(List<T0DeviceBlockEntity> group, int amount, boolean simulate) {
        int[] order = kind == T0Kind.LEACH_BARREL ? new int[]{1, 0} : contents.tankCount() == 2 ? new int[]{0, 1} : new int[]{0};
        for (int tank : order) {
            FluidAmount first = FluidAmount.EMPTY;
            for (T0DeviceBlockEntity device : group) {
                if (!device.contents.tank(tank).isEmpty()) {
                    first = device.contents.tank(tank);
                    break;
                }
            }
            if (first.isEmpty()) {
                continue;
            }
            FluidAmount taken = FluidAmount.EMPTY;
            int left = amount;
            for (T0DeviceBlockEntity device : group) {
                FluidAmount here = device.contents.tank(tank);
                if (left <= 0 || here.isEmpty() || here.fluid() != first.fluid()) {
                    continue;
                }
                int part = Math.min(left, here.amount());
                FluidAmount piece = here.withAmount(part);
                taken = taken.isEmpty() ? piece : Purity.merge(taken, piece);
                left -= part;
                if (!simulate) {
                    device.contents.setTank(tank, here.withAmount(here.amount() - part));
                }
            }
            return taken;
        }
        return FluidAmount.EMPTY;
    }

    /** Salt pans joined to this one through horizontal neighbours (this one first). */
    public List<T0DeviceBlockEntity> group() {
        List<T0DeviceBlockEntity> group = new ArrayList<>();
        Set<BlockPos> seen = new HashSet<>();
        Deque<BlockPos> open = new ArrayDeque<>();
        open.add(worldPosition);
        seen.add(worldPosition);
        while (!open.isEmpty() && group.size() < MAX_PANS) {
            BlockPos pos = open.poll();
            if (!(level.getBlockEntity(pos) instanceof T0DeviceBlockEntity pan) || pan.kind != T0Kind.SALT_PAN) {
                continue;
            }
            group.add(pan);
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos next = pos.relative(direction);
                if (seen.add(next)) {
                    open.add(next);
                }
            }
        }
        return group;
    }

    /** Contents, progress and missing conditions for the action bar. */
    public Component describe() {
        MutableComponent line = Component.empty();
        boolean any = false;
        for (int i = 0; i < contents.itemSlots(); i++) {
            ItemStack stack = contents.item(i);
            if (!stack.isEmpty()) {
                line.append(any ? Component.literal("  ") : Component.empty()).append(stack.getHoverName()).append(" ×" + stack.getCount());
                any = true;
            }
        }
        for (int i = 0; i < contents.tankCount(); i++) {
            FluidAmount fluid = contents.tank(i);
            if (!fluid.isEmpty()) {
                line.append(any ? Component.literal("  ") : Component.empty()).append(PlatformFluids.displayName(fluid))
                    .append(" " + fluid.amount() + " mB");
                any = true;
            }
        }
        for (int i = 0; i < contents.catalystSlots(); i++) {
            ItemStack stack = contents.catalyst(i);
            if (!stack.isEmpty()) {
                line.append(any ? Component.literal("  ") : Component.empty()).append(stack.getHoverName()).append(" ×" + stack.getCount());
                any = true;
            }
        }
        if (!any) {
            line.append(Component.translatable(GuiText.key("tank", "empty")));
        }
        if (kind == T0Kind.CROCK) {
            line.append("  ").append(Component.translatable(GuiText.key("manual", sealed() ? "sealed" : "open")));
        }
        Capabilities current = capabilities();
        for (InPlaceProcess unit : units) {
            RecipeIndex.Entry next = unit.preview(level, kind.spec(), contents, current);
            long progress = next != null && next == unit.current() ? unit.progress() : 0;
            if (next != null && kind.manual()) {
                line.append("  ").append(Component.translatable(GuiText.key("manual", "stir"), progress, next.recipe().manualActions()));
            } else if (next != null) {
                long goal = InPlaceProcess.goalTicks(next, current);
                line.append("  ").append(Component.translatable(GuiText.key("manual", "progress"), Math.min(99, progress * 100 / goal)));
            } else if (unit.missing() != null && !unit.missing().isEmpty()) {
                line.append("  ").append(Component.translatable(GuiText.key("manual", "missing"), T0Devices.conditionNames(unit.missing())));
            }
        }
        return line;
    }

    /**
     * What one unit is doing (server side, for overlays such as Jade): a stirred pot counts stirs against
     * {@code goal} actions, a passive unit running ticks against {@code goal} ticks; {@code goal} is 0
     * when nothing runs, and {@code missing} lists the conditions a recipe for the contents lacks.
     */
    public record UnitState(boolean manual, long progress, long goal, Set<String> missing) {
    }

    /** The state of every unit, as {@link #describe} reads it. */
    public List<UnitState> unitStates() {
        Capabilities current = capabilities();
        List<UnitState> states = new ArrayList<>(units.length);
        for (InPlaceProcess unit : units) {
            RecipeIndex.Entry next = unit.preview(level, kind.spec(), contents, current);
            long progress = next != null && next == unit.current() ? unit.progress() : 0;
            if (next != null && kind.manual()) {
                states.add(new UnitState(true, progress, next.recipe().manualActions(), Set.of()));
            } else if (next != null) {
                states.add(new UnitState(false, progress, InPlaceProcess.goalTicks(next, current), Set.of()));
            } else {
                Set<String> missing = unit.missing();
                states.add(new UnitState(kind.manual(), 0, 0, missing == null ? Set.of() : Set.copyOf(missing)));
            }
        }
        return states;
    }

    /** Everything the device holds, dropped when it is broken. */
    List<ItemStack> dropContents() {
        List<ItemStack> drops = new ArrayList<>();
        for (int i = 0; i < contents.itemSlots(); i++) {
            if (!contents.item(i).isEmpty()) {
                drops.add(contents.item(i).copy());
            }
        }
        for (int i = 0; i < contents.catalystSlots(); i++) {
            if (!contents.catalyst(i).isEmpty()) {
                drops.add(contents.catalyst(i).copy());
            }
        }
        return drops;
    }

    /** Outputs of the recipe running on unit {@code index}, for the client view; empty when idle. */
    @Nullable
    public ProcessOutput runningOutput(int index) {
        RecipeIndex.Entry entry = units[index].current();
        return entry == null ? null : entry.recipe().outputs().get(0);
    }

    // ---- persistence and client data ----

    @Override
    protected void saveData(DataWriter out) {
        contents.save(out);
        for (int i = 0; i < units.length; i++) {
            units[i].save(out, "Unit" + i);
        }
        if (fumigation > 0) {
            out.putInt("Fumigation", fumigation);
        }
    }

    @Override
    protected void loadData(DataReader in) {
        contents.load(in);
        for (int i = 0; i < units.length; i++) {
            units[i].load(in, "Unit" + i);
        }
        fumigation = in.getInt("Fumigation", 0);
    }

    @Override
    protected void saveSyncData(DataWriter out) {
        contents.save(out);
    }

    @Override
    protected void loadSyncData(DataReader in) {
        contents.load(in);
    }
}
