package com.tonywww.foodindustrial.platform.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
//? if <26.1 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?}

/** Lays out and draws a {@link TooltipRows} part (I35); made by the tooltip factory registered in {@link PlatformClient}. */
final class TooltipRowsRenderer implements ClientTooltipComponent {
    private static final int LINE = 10;
    private static final int TEXT_HEIGHT = 9;
    private static final int ROW_GAP = 1;
    private static final int WHITE = 0xFFFFFFFF;

    /** A cell placed at {@code x} (from the left) on a line whose top is {@code y} and height {@code height}. */
    private record Placed(TooltipRows.Cell cell, int x, int y, int width, int height, int lineHeight) {
    }

    private final List<Placed> placed = new ArrayList<>();
    private final int width;
    private final int height;

    TooltipRowsRenderer(TooltipRows rows) {
        Font font = Minecraft.getInstance().font;
        int y = 0;
        int maxWidth = 0;
        List<TooltipRows.Cell> line = new ArrayList<>();
        for (TooltipRows.Row row : rows.rows) {
            y += row.marginTop();
            List<TooltipRows.Cell> cells = row.cells();
            int x = 0;
            int i = 0;
            while (i < cells.size()) {
                int end = groupEnd(cells, i);
                int groupWidth = 0;
                for (int k = i; k < end; k++) {
                    groupWidth += width(font, cells.get(k)) + (k + 1 < end ? cells.get(k).gap() : 0);
                }
                if (row.wrap() > 0 && !line.isEmpty() && x + groupWidth > row.wrap()) {
                    int lineHeight = place(font, line, y);
                    maxWidth = Math.max(maxWidth, lineWidth(font, line));
                    line.clear();
                    y += lineHeight + ROW_GAP;
                    x = 0;
                }
                for (int k = i; k < end; k++) {
                    line.add(cells.get(k));
                    x += width(font, cells.get(k)) + cells.get(k).gap();
                }
                i = end;
            }
            if (!line.isEmpty()) {
                int lineHeight = place(font, line, y);
                maxWidth = Math.max(maxWidth, lineWidth(font, line));
                line.clear();
                y += lineHeight + ROW_GAP;
            }
        }
        this.width = maxWidth;
        this.height = y;
    }

    /** The end (exclusive) of the group that starts at {@code start}. */
    private static int groupEnd(List<TooltipRows.Cell> cells, int start) {
        int end = start + 1;
        while (end < cells.size() && !cells.get(end).group()) {
            end++;
        }
        return end;
    }

    private static int width(Font font, TooltipRows.Cell cell) {
        return switch (cell.kind()) {
            case TooltipRows.ICON -> cell.size();
            case TooltipRows.TEXT -> {
                int widest = cell.minWidth();
                for (Component text : cell.lines()) {
                    widest = Math.max(widest, font.width(text));
                }
                yield widest;
            }
            default -> cell.width() + 2;
        };
    }

    private static int height(TooltipRows.Cell cell) {
        return switch (cell.kind()) {
            case TooltipRows.ICON -> cell.size();
            case TooltipRows.TEXT -> cell.lines().size() * LINE - (LINE - TEXT_HEIGHT);
            default -> cell.height() + 2;
        };
    }

    private static int lineWidth(Font font, List<TooltipRows.Cell> line) {
        int total = 0;
        for (int k = 0; k < line.size(); k++) {
            total += width(font, line.get(k)) + (k + 1 < line.size() ? line.get(k).gap() : 0);
        }
        return total;
    }

    /** Places one laid-out line at {@code y}; returns its height. */
    private int place(Font font, List<TooltipRows.Cell> line, int y) {
        int lineHeight = 0;
        for (TooltipRows.Cell cell : line) {
            lineHeight = Math.max(lineHeight, height(cell));
        }
        int x = 0;
        for (TooltipRows.Cell cell : line) {
            int w = width(font, cell);
            placed.add(new Placed(cell, x, y, w, height(cell), lineHeight));
            x += w + cell.gap();
        }
        return lineHeight;
    }

    private void draw(GuiCanvas canvas, Font font, int left, int top) {
        for (Placed p : placed) {
            int x = left + p.x();
            int y = top + p.y() + (p.lineHeight() - p.height()) / 2;
            TooltipRows.Cell cell = p.cell();
            switch (cell.kind()) {
                case TooltipRows.ICON -> canvas.icon(cell.icon(), x, y, cell.size(), cell.textureSize());
                case TooltipRows.TEXT -> {
                    List<Component> lines = cell.lines();
                    for (int i = 0; i < lines.size(); i++) {
                        canvas.text(font, lines.get(i), x, y + i * LINE, WHITE, true);
                    }
                }
                default -> canvas.bar(cell.bar(), x + 1, y + 1, cell.width(), cell.height());
            }
        }
    }

    @Override
    public int getWidth(Font font) {
        return width;
    }

    //? if <26.1 {
    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        draw(GuiCanvas.of(graphics), font, x, y);
    }
    //?} else {
    /*@Override
    public int getHeight(Font font) {
        return height;
    }

    @Override
    public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
        draw(GuiCanvas.of(graphics), font, x, y);
    }
    *///?}
}
