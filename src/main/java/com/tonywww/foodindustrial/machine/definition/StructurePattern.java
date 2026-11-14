package com.tonywww.foodindustrial.machine.definition;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Shape of a multiblock (machines/02 §4, §6.3 rule 12). Members are this mod's blocks only
 * (2026-11-04): casings, ports and functional blocks, which report their placement and removal to the
 * controllers around them; nothing is scanned periodically.
 * <p>
 * Layers go bottom to top; within a layer, rows go from the back of the structure to its front and
 * characters from left to right as seen when looking at the controller's front. {@code C} is the
 * controller; a space accepts anything; other characters are declared with {@link Builder#where}.
 * One layer may repeat ({@link Builder#repeat}, M19's tower, 2026-11-11): the structure then has a height
 * between its smallest and largest variant, and the check reports the variant that formed.
 */
public final class StructurePattern {
    /** What a pattern cell accepts: {@code blocks} for display (JEI structure pages, I22). */
    public record Member(String description, Predicate<BlockState> test, boolean portAllowed, List<Supplier<? extends Block>> blocks) {
    }

    /**
     * Validation result: formed, the wrong positions with the description of the expected block (for
     * the structure page) and the ports found.
     */
    public record Result(boolean formed, List<BlockPos> wrong, List<String> expected, List<BlockPos> ports, boolean unloaded, int layers) {
    }

    private final char[][][] cells;
    private final Map<Character, Member> members;
    private final int cx;
    private final int cy;
    private final int cz;
    /** Variants of a structure with a repeating layer, smallest first; empty for a fixed shape. */
    private final List<StructurePattern> variants;
    private final int repeatMin;
    private final int repeatMax;

    private StructurePattern(char[][][] cells, Map<Character, Member> members, int cx, int cy, int cz) {
        this.cells = cells;
        this.members = members;
        this.cx = cx;
        this.cy = cy;
        this.cz = cz;
        this.variants = List.of();
        this.repeatMin = 0;
        this.repeatMax = 0;
    }

    private StructurePattern(List<StructurePattern> variants, int repeatMin, int repeatMax) {
        StructurePattern largest = variants.get(variants.size() - 1);
        this.cells = largest.cells;
        this.members = largest.members;
        this.cx = largest.cx;
        this.cy = largest.cy;
        this.cz = largest.cz;
        this.variants = List.copyOf(variants);
        this.repeatMin = repeatMin;
        this.repeatMax = repeatMax;
    }

    /** The smallest shape; the pattern itself when the shape is fixed. */
    public StructurePattern smallest() {
        return variants.isEmpty() ? this : variants.get(0);
    }

    /** How often the repeating layer may repeat ({@code [min, max]}), or {@code null} for a fixed shape. */
    @org.jetbrains.annotations.Nullable
    public int[] repeat() {
        return variants.isEmpty() ? null : new int[]{repeatMin, repeatMax};
    }

    /** Number of layers, bottom first. */
    public int height() {
        return cells.length;
    }

    /** Rows per layer, back to front. */
    public int depth() {
        return cells[0].length;
    }

    /** Columns per row, left to right as seen from the front. */
    public int width() {
        return cells[0][0].length;
    }

    /** Character of a cell: {@code ' '} for any block, {@code 'C'} for the controller. */
    public char cell(int x, int y, int z) {
        return cells[y][z][x];
    }

    /** What cell character {@code c} accepts, or {@code null} for {@code ' '} and {@code 'C'}. */
    @org.jetbrains.annotations.Nullable
    public Member member(char c) {
        return members.get(c);
    }

    /** Layers of the smallest shape. */
    public int minLayers() {
        return variants.isEmpty() ? cells.length : variants.get(0).cells.length;
    }

    /** Layers of the largest shape. */
    public int maxLayers() {
        return cells.length;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** World position of cell ({@code x}, {@code y}, {@code z}) for a controller at {@code origin} facing {@code facing}. */
    public BlockPos position(BlockPos origin, Direction facing, int x, int y, int z) {
        Direction right = facing.getCounterClockWise();
        int dx = x - cx;
        int dz = z - cz;
        return origin.offset(right.getStepX() * dx + facing.getStepX() * dz, y - cy, right.getStepZ() * dx + facing.getStepZ() * dz);
    }

    /** Corners of the box the structure occupies (the largest shape of a variable one). */
    public BlockPos[] bounds(BlockPos origin, Direction facing) {
        if (!variants.isEmpty()) {
            BlockPos[] box = null;
            for (StructurePattern variant : variants) {
                BlockPos[] own = variant.bounds(origin, facing);
                box = box == null ? own : new BlockPos[]{
                    new BlockPos(Math.min(box[0].getX(), own[0].getX()), Math.min(box[0].getY(), own[0].getY()), Math.min(box[0].getZ(), own[0].getZ())),
                    new BlockPos(Math.max(box[1].getX(), own[1].getX()), Math.max(box[1].getY(), own[1].getY()), Math.max(box[1].getZ(), own[1].getZ()))};
            }
            return box;
        }
        BlockPos a = position(origin, facing, 0, 0, 0);
        BlockPos b = position(origin, facing, cells[0][0].length - 1, cells.length - 1, cells[0].length - 1);
        return new BlockPos[]{
            new BlockPos(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ())),
            new BlockPos(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()))};
    }

    /** Corners of the shape with {@code layers} layers (a variable structure's formed variant). */
    public BlockPos[] bounds(BlockPos origin, Direction facing, int layers) {
        for (StructurePattern variant : variants) {
            if (variant.cells.length == layers) {
                return variant.bounds(origin, facing);
            }
        }
        return bounds(origin, facing);
    }

    /**
     * Checks the structure; positions in unloaded chunks make it unformed for now. A variable structure
     * takes its tallest variant that forms; if none does, it reports the one with the fewest wrong blocks.
     */
    public Result check(Level level, BlockPos origin, Direction facing, Predicate<BlockState> isPort) {
        if (!variants.isEmpty()) {
            Result best = null;
            for (int i = variants.size() - 1; i >= 0; i--) {
                Result result = variants.get(i).check(level, origin, facing, isPort);
                if (result.formed()) {
                    return result;
                }
                if (best == null || result.wrong().size() <= best.wrong().size()) {
                    best = result;
                }
            }
            return best;
        }
        List<BlockPos> wrong = new ArrayList<>();
        List<String> expected = new ArrayList<>();
        List<BlockPos> ports = new ArrayList<>();
        boolean unloaded = false;
        for (int y = 0; y < cells.length; y++) {
            for (int z = 0; z < cells[y].length; z++) {
                for (int x = 0; x < cells[y][z].length; x++) {
                    char c = cells[y][z][x];
                    if (c == ' ' || c == 'C') {
                        continue;
                    }
                    BlockPos pos = position(origin, facing, x, y, z);
                    if (!level.isLoaded(pos)) {
                        unloaded = true;
                        continue;
                    }
                    Member member = members.get(c);
                    BlockState state = level.getBlockState(pos);
                    if (member.portAllowed() && isPort.test(state)) {
                        ports.add(pos.immutable());
                    } else if (!member.test().test(state)) {
                        wrong.add(pos.immutable());
                        expected.add(member.description());
                    }
                }
            }
        }
        return new Result(wrong.isEmpty() && !unloaded, wrong, expected, ports, unloaded, cells.length);
    }

    /** Pattern builder. */
    public static final class Builder {
        private final List<String[]> layers = new ArrayList<>();
        private final Map<Character, Member> members = new HashMap<>();
        private int repeatLayer = -1;
        private int repeatMin;
        private int repeatMax;

        /** Adds a layer above the previous ones: rows from back to front. */
        public Builder layer(String... rows) {
            layers.add(rows);
            return this;
        }

        /**
         * The layer added last repeats {@code min}–{@code max} times (a tower whose height may vary).
         * Only one layer may repeat.
         */
        public Builder repeat(int min, int max) {
            if (layers.isEmpty() || repeatLayer >= 0 || min < 1 || max < min) {
                throw new IllegalStateException("Invalid repeating layer");
            }
            repeatLayer = layers.size() - 1;
            repeatMin = min;
            repeatMax = max;
            return this;
        }

        /** Declares a cell character that accepts {@code blocks}. */
        @SafeVarargs
        public final Builder where(char c, String description, boolean portAllowed, Supplier<? extends Block>... blocks) {
            members.put(c, new Member(description, state -> {
                for (Supplier<? extends Block> block : blocks) {
                    if (state.is(block.get())) {
                        return true;
                    }
                }
                return false;
            }, portAllowed, List.of(blocks)));
            return this;
        }

        public StructurePattern build() {
            if (repeatLayer >= 0) {
                List<StructurePattern> variants = new ArrayList<>();
                for (int n = repeatMin; n <= repeatMax; n++) {
                    List<String[]> expanded = new ArrayList<>();
                    for (int i = 0; i < layers.size(); i++) {
                        for (int copy = 0; copy < (i == repeatLayer ? n : 1); copy++) {
                            expanded.add(layers.get(i));
                        }
                    }
                    variants.add(build(expanded));
                }
                return new StructurePattern(variants, repeatMin, repeatMax);
            }
            return build(layers);
        }

        private StructurePattern build(List<String[]> layers) {
            if (layers.isEmpty()) {
                throw new IllegalStateException("Empty structure pattern");
            }
            int depth = layers.get(0).length;
            int width = layers.get(0)[0].length();
            char[][][] cells = new char[layers.size()][depth][width];
            int[] controller = null;
            for (int y = 0; y < layers.size(); y++) {
                String[] rows = layers.get(y);
                if (rows.length != depth) {
                    throw new IllegalStateException("Structure layers differ in depth");
                }
                for (int z = 0; z < depth; z++) {
                    if (rows[z].length() != width) {
                        throw new IllegalStateException("Structure rows differ in width");
                    }
                    for (int x = 0; x < width; x++) {
                        char c = rows[z].charAt(x);
                        cells[y][z][x] = c;
                        if (c == 'C') {
                            if (controller != null) {
                                throw new IllegalStateException("Structure has two controllers");
                            }
                            controller = new int[]{x, y, z};
                        } else if (c != ' ' && !members.containsKey(c)) {
                            throw new IllegalStateException("Undeclared structure character '" + c + "'");
                        }
                    }
                }
            }
            if (controller == null) {
                throw new IllegalStateException("Structure has no controller");
            }
            return new StructurePattern(cells, Map.copyOf(members), controller[0], controller[1], controller[2]);
        }
    }
}
