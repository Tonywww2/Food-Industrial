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
 */
public final class StructurePattern {
    /** What a pattern cell accepts. */
    public record Member(String description, Predicate<BlockState> test, boolean portAllowed) {
    }

    /**
     * Validation result: formed, the wrong positions with the description of the expected block (for
     * the structure page) and the ports found.
     */
    public record Result(boolean formed, List<BlockPos> wrong, List<String> expected, List<BlockPos> ports, boolean unloaded) {
    }

    private final char[][][] cells;
    private final Map<Character, Member> members;
    private final int cx;
    private final int cy;
    private final int cz;

    private StructurePattern(char[][][] cells, Map<Character, Member> members, int cx, int cy, int cz) {
        this.cells = cells;
        this.members = members;
        this.cx = cx;
        this.cy = cy;
        this.cz = cz;
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

    /** Corners of the box the structure occupies. */
    public BlockPos[] bounds(BlockPos origin, Direction facing) {
        BlockPos a = position(origin, facing, 0, 0, 0);
        BlockPos b = position(origin, facing, cells[0][0].length - 1, cells.length - 1, cells[0].length - 1);
        return new BlockPos[]{
            new BlockPos(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ())),
            new BlockPos(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()))};
    }

    /** Checks the structure; positions in unloaded chunks make it unformed for now. */
    public Result check(Level level, BlockPos origin, Direction facing, Predicate<BlockState> isPort) {
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
        return new Result(wrong.isEmpty() && !unloaded, wrong, expected, ports, unloaded);
    }

    /** Pattern builder. */
    public static final class Builder {
        private final List<String[]> layers = new ArrayList<>();
        private final Map<Character, Member> members = new HashMap<>();

        /** Adds a layer above the previous ones: rows from back to front. */
        public Builder layer(String... rows) {
            layers.add(rows);
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
            }, portAllowed));
            return this;
        }

        public StructurePattern build() {
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
