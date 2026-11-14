package com.tonywww.foodindustrial.gui.client.recipe;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.definition.PowerKind;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.recipe.DeviceSpec;
import com.tonywww.foodindustrial.recipe.ProcessRecipe;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.VanillaDevice;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Devices as JEI shows them (additives/10 §5): the item standing for each registered device, the
 * devices of a recipe type (catalysts) and how long a device takes for a recipe. Development devices
 * and devices without an item are left out.
 */
public final class RecipeDevices {
    private RecipeDevices() {
    }

    /** Item standing for {@code spec}, or empty for devices JEI does not show. */
    public static ItemStack icon(DeviceSpec spec) {
        if (spec.vanilla() != null) {
            return new ItemStack(switch (spec.vanilla()) {
                case CAMPFIRE -> Items.CAMPFIRE;
                case FURNACE -> Items.FURNACE;
                case SMOKER -> Items.SMOKER;
                case BLAST_FURNACE -> Items.BLAST_FURNACE;
            });
        }
        MachineDefinition machine = machine(spec);
        if (machine != null && machine.development()) {
            return ItemStack.EMPTY;
        }
        return PlatformTags.item(spec.id()).map(ItemStack::new).orElse(ItemStack.EMPTY);
    }

    /** Extra catalyst items of a device (the soul campfire runs what the campfire runs). */
    public static List<ItemStack> icons(DeviceSpec spec) {
        List<ItemStack> result = new ArrayList<>();
        ItemStack icon = icon(spec);
        if (!icon.isEmpty()) {
            result.add(icon);
            if (spec.vanilla() == VanillaDevice.CAMPFIRE) {
                result.add(new ItemStack(Items.SOUL_CAMPFIRE));
            }
        }
        return result;
    }

    /** The machine behind a device, or {@code null} for tools, passive devices and vanilla blocks. */
    @Nullable
    public static MachineDefinition machine(DeviceSpec spec) {
        String prefix = FoodIndustrial.MOD_ID + ":";
        return spec.id().startsWith(prefix) ? MachineDefinition.byId(spec.id().substring(prefix.length())) : null;
    }

    /** Devices running recipes of {@code type} that JEI shows, lowest tier first. */
    public static List<DeviceSpec> of(ProcessType type) {
        List<DeviceSpec> result = new ArrayList<>();
        for (DeviceSpec spec : DeviceSpec.all()) {
            if (spec.types().contains(type) && !icon(spec).isEmpty()) {
                result.add(spec);
            }
        }
        result.sort((a, b) -> Integer.compare(a.tier().ordinal(), b.tier().ordinal()));
        return result;
    }

    /** Devices that can run {@code recipe} and that JEI shows, lowest tier first. */
    public static List<DeviceSpec> runners(ProcessRecipe recipe) {
        List<DeviceSpec> result = new ArrayList<>();
        for (DeviceSpec spec : DeviceSpec.runnersOf(recipe)) {
            if (!icon(spec).isEmpty()) {
                result.add(spec);
            }
        }
        return result;
    }

    /** Ticks a run takes on {@code spec}, or 0 when the device counts actions instead. */
    public static int ticks(DeviceSpec spec, ProcessRecipe recipe) {
        if (spec.vanilla() != null) {
            return spec.vanilla().cookingTime(recipe);
        }
        if (spec.manual() && !spec.timed()) {
            return 0;
        }
        return recipe.time() > 0 ? spec.profile().ticks(recipe) : 0;
    }

    /** Hand actions a run takes on {@code spec}, or 0. */
    public static int actions(DeviceSpec spec, ProcessRecipe recipe) {
        return spec.manual() ? recipe.manualActions() : 0;
    }

    /** FE per tick on {@code spec}, or 0 for devices without FE. */
    public static long energy(DeviceSpec spec, ProcessRecipe recipe) {
        MachineDefinition machine = machine(spec);
        return machine != null && machine.power() == PowerKind.ENERGY ? spec.profile().energyPerTick(recipe) : 0;
    }
}
