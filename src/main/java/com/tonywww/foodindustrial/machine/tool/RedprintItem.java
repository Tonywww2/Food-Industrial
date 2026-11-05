package com.tonywww.foodindustrial.machine.tool;

import com.tonywww.foodindustrial.core.data.DataReader;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.core.text.Tooltips;
import com.tonywww.foodindustrial.gui.GuiText;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.entity.MachineBlockEntity;
import com.tonywww.foodindustrial.platform.PlatformItem;
import com.tonywww.foodindustrial.platform.PlatformItemData;
import com.tonywww.foodindustrial.platform.PlatformPlayers;
import com.tonywww.foodindustrial.platform.machine.PlatformEntityBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Redprint (machines/04 §4, 2026-11-05): Shift + right click on a machine records its sides with their
 * advanced options, redstone control, automatic input and output, mode and locks; right click on a
 * machine of the same type writes them, relative to that machine's front; Shift + right click in the
 * air clears the redprint.
 */
public class RedprintItem extends PlatformItem implements MachineTool {
    public static final String ID = "redprint";
    private static final String KEY = "FoodIndustrialRedprint";

    public RedprintItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return MachineTool.useOn(this, context);
    }

    @Override
    public PlatformEntityBlock.UseResult useOnMachine(Level level, MachineBlockEntity machine, Player player, ItemStack stack, boolean secondary) {
        if (!secondary && !MachineTool.mayEdit(player)) {
            return PlatformEntityBlock.UseResult.PASS;
        }
        if (level.isClientSide()) {
            return PlatformEntityBlock.UseResult.SUCCESS;
        }
        if (secondary) {
            PlatformItemData.write(stack, KEY, level.registryAccess(), out -> {
                out.putString("Machine", machine.definition().id());
                machine.saveConfig(out.child("Config"));
            });
            message(player, "copied");
            return PlatformEntityBlock.UseResult.SUCCESS;
        }
        DataReader data = PlatformItemData.read(stack, KEY, level.registryAccess());
        if (data == null) {
            message(player, "empty");
            return PlatformEntityBlock.UseResult.SUCCESS;
        }
        if (!data.getString("Machine", "").equals(machine.definition().id())) {
            message(player, "mismatch");
            return PlatformEntityBlock.UseResult.SUCCESS;
        }
        machine.applyConfig(data.child("Config"));
        machine.settingsApplied();
        message(player, "pasted");
        return PlatformEntityBlock.UseResult.SUCCESS;
    }

    @Override
    protected boolean onUseInAir(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isSecondaryUseActive() || !PlatformItemData.has(stack, KEY)) {
            return false;
        }
        if (!level.isClientSide()) {
            PlatformItemData.remove(stack, KEY);
            message(player, "cleared");
        }
        return true;
    }

    private static void message(Player player, String key) {
        PlatformPlayers.actionBar(player, Component.translatable(GuiText.key("redprint", key)));
    }

    @Override
    protected void appendTooltip(ItemStack stack, List<Component> lines, boolean shiftDown) {
        String machine = PlatformItemData.getString(stack, KEY, "Machine");
        MachineDefinition definition = machine.isEmpty() ? null : MachineDefinition.byId(machine);
        if (definition != null) {
            lines.add(Component.translatable(GuiText.key("redprint", "recorded"), definition.block().getName()).withStyle(ChatFormatting.GRAY));
        }
        Tooltips.machine(Lang.description(ID), lines, shiftDown);
    }
}
