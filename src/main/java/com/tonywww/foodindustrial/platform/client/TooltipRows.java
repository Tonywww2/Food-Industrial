package com.tonywww.foodindustrial.platform.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A tooltip part of rows made of icons, text and bars (I35): item tooltips add it through
 * {@link PlatformClient#onTooltipRows}, and the inventory panel shows it as its hover card. Text rows are 9 pixels
 * high and 1 apart, like vanilla tooltip lines; the cells of a row sit side by side, centred on the row's height.
 * A row may wrap: it then breaks before a cell that starts a group when the group would pass the width.
 */
public final class TooltipRows implements TooltipComponent {
    static final int ICON = 0;
    static final int TEXT = 1;
    static final int BAR = 2;
    private static final int DEFAULT_GAP = 2;

    final List<Row> rows;

    private TooltipRows(List<Row> rows) {
        this.rows = rows;
    }

    /**
     * One cell: an icon ({@code size} square, drawn from a {@code textureSize} square texture), stacked text lines
     * at least {@code minWidth} wide, or a bar {@code width} × {@code height}; {@code gap} pixels follow it.
     */
    record Cell(int kind, @Nullable String icon, int size, int textureSize, @Nullable List<Component> lines, int minWidth,
                @Nullable GuiBar bar, int width, int height, int gap, boolean group) {
        Cell withGap(int value) {
            return new Cell(kind, icon, size, textureSize, lines, minWidth, bar, width, height, value, group);
        }
    }

    /** A row: its cells, the width it wraps at (0: never) and the space above it. */
    record Row(List<Cell> cells, int wrap, int marginTop) {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<Row> rows = new ArrayList<>();
        private List<Cell> cells;
        private int wrap;
        private int marginTop;
        private boolean group;

        private Builder() {
        }

        /** Starts a new row. */
        public Builder row() {
            return row(0);
        }

        /** Starts a new row {@code marginTop} pixels below the previous one. */
        public Builder row(int marginTop) {
            flush();
            cells = new ArrayList<>();
            wrap = 0;
            this.marginTop = marginTop;
            return this;
        }

        /** Lets the current row wrap at {@code width} pixels, between groups. */
        public Builder wrap(int width) {
            wrap = width;
            return this;
        }

        /** The next cell starts a group: a wrapping row may break before it. */
        public Builder group() {
            group = true;
            return this;
        }

        /** A 16 × 16 texture ({@code namespace:textures/....png}) drawn {@code size} square. */
        public Builder icon(String texture, int size) {
            return icon(texture, size, 16);
        }

        /** A {@code textureSize} square texture drawn {@code size} square. */
        public Builder icon(String texture, int size, int textureSize) {
            return add(new Cell(ICON, texture, size, textureSize, null, 0, null, size, size, DEFAULT_GAP, takeGroup()));
        }

        public Builder text(Component text) {
            return text(text, 0);
        }

        /** One line of text, at least {@code minWidth} pixels wide. */
        public Builder text(Component text, int minWidth) {
            return lines(List.of(text), minWidth);
        }

        /** Text lines stacked in one cell, at least {@code minWidth} pixels wide. */
        public Builder lines(List<Component> lines, int minWidth) {
            return add(new Cell(TEXT, null, 0, 0, List.copyOf(lines), minWidth, null, 0, 0, DEFAULT_GAP, takeGroup()));
        }

        /** A bar {@code width} × {@code height} (its edge adds a pixel on every side). */
        public Builder bar(GuiBar bar, int width, int height) {
            return add(new Cell(BAR, null, 0, 0, null, 0, bar, width, height, DEFAULT_GAP, takeGroup()));
        }

        /** The space after the last cell (2 by default). */
        public Builder gap(int gap) {
            if (cells != null && !cells.isEmpty()) {
                cells.set(cells.size() - 1, cells.get(cells.size() - 1).withGap(gap));
            }
            return this;
        }

        public TooltipRows build() {
            flush();
            return new TooltipRows(List.copyOf(rows));
        }

        private Builder add(Cell cell) {
            if (cells == null) {
                row();
            }
            cells.add(cell);
            return this;
        }

        private boolean takeGroup() {
            boolean value = group;
            group = false;
            return value;
        }

        private void flush() {
            if (cells != null && !cells.isEmpty()) {
                rows.add(new Row(List.copyOf(cells), wrap, marginTop));
            }
            cells = null;
        }
    }
}
