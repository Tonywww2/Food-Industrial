package com.tonywww.foodindustrial.devtest;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.purity.Purity;
import com.tonywww.foodindustrial.core.purity.PurityRules;
import com.tonywww.foodindustrial.core.purity.PurityScale;
import com.tonywww.foodindustrial.core.storage.FluidAmount;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.core.text.Tooltips;
import com.tonywww.foodindustrial.material.ModMaterials;
import com.tonywww.foodindustrial.platform.PlatformTags;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Development-only: logs effective purity and material tooltip lines whenever the purity rules change
 * on the physical client, to compare a client connected to a dedicated server with the server's
 * {@code /fidev purity} output.
 */
final class ClientPurityDump {
    private ClientPurityDump() {
    }

    static void dump() {
        FoodIndustrial.LOGGER.info("[purity dump] {} rules", PurityRules.rules().size());
        ItemStack plain = new ItemStack(DevTestContent.PURITY_TEST_ITEM.get());
        log("sugar", new ItemStack(Items.SUGAR));
        log("honey", new ItemStack(Items.HONEY_BOTTLE));
        log("stone", new ItemStack(Items.STONE));
        log("purity_test", plain);
        log("purity_test 80", Purity.with(plain, PurityScale.ofPercent(80)));
        log("purity_test 55.5", Purity.with(plain, PurityScale.ofPercent(55.5)));
        log("purity_test 99", Purity.with(plain, PurityScale.ofPercent(99)));
        FluidAmount water = FluidAmount.of(Fluids.WATER, 1000);
        FoodIndustrial.LOGGER.info("[purity dump] water effective={}", DevCommands.describe(null, Purity.get(water)));
        FluidAmount fluid = Purity.with(FluidAmount.of(DevTestContent.PURITY_TEST_FLUID.source().get(), 1000), PurityScale.ofPercent(95));
        List<Component> lines = new ArrayList<>();
        Tooltips.material(fluid, "H2SO4", Lang.description("purity_test_fluid"), lines, false);
        FoodIndustrial.LOGGER.info("[purity dump] fluid 95 -> {}", lines.stream().map(Component::getString).toList());
        for (String id : new String[]{"salt", "sucrose", "acetobacter", "bittern", "acetic_acid", "resin"}) {
            ModMaterials.Entry entry = ModMaterials.get(id);
            Supplier<? extends Item> item = entry.item() != null ? entry.item() : entry.fluid().bucket();
            String material = ModMaterials.materialId(entry.def());
            List<Integer> values = new ArrayList<>(Purity.listedValues(material));
            values.add(0, null);
            for (Integer b : values) {
                ItemStack stack = new ItemStack(item.get());
                if (b != null) {
                    stack = Purity.with(stack, b);
                }
                List<Component> tooltip = new ArrayList<>();
                Tooltips.material(stack, entry.def().formula(), Lang.description(id), tooltip, false);
                FoodIndustrial.LOGGER.info("[purity dump] {} {} -> {}", id, b == null ? "unset" : "b=" + b,
                    tooltip.stream().map(line -> line.getString() + " (" + line.getStyle().getColor() + ")").toList());
            }
        }
    }

    private static void log(String label, ItemStack stack) {
        List<Component> lines = new ArrayList<>();
        Tooltips.material(stack, "CaSO4·2H2O", Lang.description("purity_test"), lines, true);
        FoodIndustrial.LOGGER.info("[purity dump] {} ({}) effective={} tooltip={}", label, PlatformTags.itemId(stack.getItem()),
            DevCommands.describe(Purity.materialOf(stack.getItem()), Purity.get(stack)), lines.stream().map(Component::getString).toList());
    }
}
