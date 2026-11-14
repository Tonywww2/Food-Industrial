package com.tonywww.foodindustrial.gui.client.recipe;

import com.tonywww.foodindustrial.gui.RecipeText;
import com.tonywww.foodindustrial.gui.client.Widgets;
import com.tonywww.foodindustrial.machine.ModMachines;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.definition.StructurePattern;
import com.tonywww.foodindustrial.platform.client.gui.Canvas;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

/**
 * How to build one multiblock machine, layer by layer (I22, 2026-11-11 user decision): a top-down grid
 * of the current layer of the smallest shape with the front row at the bottom, the positions that may
 * be ports framed in blue and the controller in gold, buttons to change the layer, and the blocks with
 * their counts as JEI slots on the right (the controller is the category's catalyst).
 */
public final class StructurePage {
    public static final int WIDTH = 160;
    /** Width of the layer grid area; the "next layer" button sits at its right edge. */
    private static final int GRID_WIDTH = 78;
    public static final int NEXT_X = GRID_WIDTH - 11;
    public static final int HEIGHT = 116;
    private static final int CELL = 18;
    private static final int GRID_Y = 14;
    private static final int LIST_X = 82;
    private static final int TEXT = 0xFF404040;
    private static final Map<MachineDefinition, StructurePage> PAGES = Collections.synchronizedMap(new WeakHashMap<>());

    /** One kind of block the structure needs. */
    public record Need(ItemStack stack, int x, int y) {
    }

    private final MachineDefinition machine;
    private final StructurePattern pattern;
    private final List<Need> needs = new ArrayList<>();
    private final int portsY;
    private int layer;

    private StructurePage(MachineDefinition machine) {
        this.machine = machine;
        this.pattern = machine.structure().smallest();
        Map<Block, Integer> counts = new LinkedHashMap<>();
        for (int y = 0; y < pattern.height(); y++) {
            for (int z = 0; z < pattern.depth(); z++) {
                for (int x = 0; x < pattern.width(); x++) {
                    StructurePattern.Member member = pattern.member(pattern.cell(x, y, z));
                    if (member != null && !member.blocks().isEmpty()) {
                        counts.merge(member.blocks().get(0).get(), 1, Integer::sum);
                    }
                }
            }
        }
        int index = 0;
        for (Map.Entry<Block, Integer> entry : counts.entrySet()) {
            needs.add(new Need(new ItemStack(entry.getKey().asItem(), entry.getValue()), LIST_X + (index % 4) * CELL, GRID_Y + 10 + (index / 4) * CELL));
            index++;
        }
        portsY = GRID_Y + 10 + ((index + 3) / 4) * CELL + 14;
        int port = 0;
        for (ModMachines.Port kind : ModMachines.ports()) {
            needs.add(new Need(new ItemStack(kind.item().get()), LIST_X + port * CELL, portsY));
            port++;
        }
    }

    public static StructurePage of(MachineDefinition machine) {
        return PAGES.computeIfAbsent(machine, StructurePage::new);
    }

    public MachineDefinition machine() {
        return machine;
    }

    /** Blocks and counts (the casing blocks first, then the three ports, each as one item). */
    public List<Need> needs() {
        return needs;
    }

    /** Number of block kinds before the ports in {@link #needs}. */
    public int casingKinds() {
        return needs.size() - ModMachines.ports().size();
    }

    public boolean click(double mouseX, double mouseY) {
        if (mouseY < 0 || mouseY >= 11) {
            return false;
        }
        if (mouseX >= 0 && mouseX < 11) {
            layer = (layer + pattern.height() - 1) % pattern.height();
            return true;
        }
        if (mouseX >= NEXT_X && mouseX < NEXT_X + 11) {
            layer = (layer + 1) % pattern.height();
            return true;
        }
        return false;
    }

    public void draw(Canvas canvas, double mouseX, double mouseY) {
        Widgets.textButton(canvas, 0, 0, 11, 11, Component.literal("<"), hovered(mouseX, mouseY, 0), false);
        Widgets.textButton(canvas, NEXT_X, 0, 11, 11, Component.literal(">"), hovered(mouseX, mouseY, NEXT_X), false);
        Component label = Component.translatable(RecipeText.structure("layer"), layer + 1, pattern.height());
        canvas.text(label, GRID_WIDTH / 2 - canvas.width(label) / 2, 2, TEXT, false);
        int left = (GRID_WIDTH - pattern.width() * CELL) / 2;
        for (int z = 0; z < pattern.depth(); z++) {
            for (int x = 0; x < pattern.width(); x++) {
                int px = left + x * CELL;
                int py = GRID_Y + z * CELL;
                char c = pattern.cell(x, layer, z);
                if (c == ' ') {
                    canvas.outline(px, py, CELL, CELL, 0x40000000);
                    continue;
                }
                Widgets.slot(canvas, Widgets.SlotKind.NORMAL, px, py);
                ItemStack shown = cellItem(c);
                canvas.item(shown, px + 1, py + 1);
                if (c == 'C') {
                    RecipeTextures.controller(canvas, px, py);
                } else {
                    StructurePattern.Member member = pattern.member(c);
                    if (member != null && member.portAllowed()) {
                        RecipeTextures.port(canvas, px, py);
                    }
                }
            }
        }
        canvas.text(Component.translatable(RecipeText.structure("needs")), LIST_X, GRID_Y, TEXT, false);
        Widgets.fitText(canvas, Component.translatable(RecipeText.structure("port")), LIST_X, portsY - 10, WIDTH - LIST_X, TEXT, false);
        for (int i = 0; i < needs.size(); i++) {
            Need need = needs.get(i);
            Widgets.slot(canvas, i < casingKinds() ? Widgets.SlotKind.NORMAL : Widgets.SlotKind.CATALYST, need.x, need.y);
        }
        int[] repeat = machine.structure().repeat();
        if (repeat != null) {
            Widgets.fitText(canvas, Component.translatable(RecipeText.structure("repeat"), repeat[0], repeat[1]), 0, HEIGHT - 9, WIDTH, TEXT, false);
        }
    }

    private static boolean hovered(double mouseX, double mouseY, int x) {
        return mouseY >= 0 && mouseY < 11 && mouseX >= x && mouseX < x + 11;
    }

    private ItemStack cellItem(char c) {
        if (c == 'C') {
            return new ItemStack(machine.item());
        }
        StructurePattern.Member member = pattern.member(c);
        if (member == null || member.blocks().isEmpty()) {
            return ItemStack.EMPTY;
        }
        List<Supplier<? extends Block>> blocks = member.blocks();
        int index = blocks.size() == 1 ? 0 : (int) ((System.currentTimeMillis() / 1000L) % blocks.size());
        return new ItemStack(blocks.get(index).get().asItem());
    }

    /** Tooltip of a grid cell or a button. */
    public List<Component> tooltip(double mouseX, double mouseY) {
        List<Component> lines = new ArrayList<>();
        if (mouseY >= 0 && mouseY < 11) {
            if (mouseX >= 0 && mouseX < 11) {
                lines.add(Component.translatable(RecipeText.structure("previous")));
            } else if (mouseX >= NEXT_X && mouseX < NEXT_X + 11) {
                lines.add(Component.translatable(RecipeText.structure("next")));
            }
            return lines;
        }
        int left = (GRID_WIDTH - pattern.width() * CELL) / 2;
        int x = (int) Math.floor((mouseX - left) / CELL);
        int z = (int) Math.floor((mouseY - GRID_Y) / CELL);
        if (x < 0 || z < 0 || x >= pattern.width() || z >= pattern.depth()) {
            return lines;
        }
        char c = pattern.cell(x, layer, z);
        if (c == ' ') {
            lines.add(Component.translatable(RecipeText.structure("air")));
            return lines;
        }
        if (c == 'C') {
            lines.add(new ItemStack(machine.item()).getHoverName());
            lines.add(Component.translatable(RecipeText.structure("controller")).withStyle(ChatFormatting.GOLD));
            return lines;
        }
        StructurePattern.Member member = pattern.member(c);
        if (member != null) {
            lines.add(Component.translatable(member.description()));
            if (member.portAllowed()) {
                lines.add(Component.translatable(RecipeText.structure("port")).withStyle(ChatFormatting.BLUE));
            }
        }
        return lines;
    }

    /** The structure category's controller items, for {@code machine}. */
    @Nullable
    public static ItemStack controller(MachineDefinition machine) {
        return machine.isMultiblock() ? new ItemStack(machine.item()) : null;
    }
}
