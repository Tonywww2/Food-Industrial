package com.tonywww.foodindustrial.machine.definition;

import com.tonywww.foodindustrial.recipe.Capabilities;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * A machine module (machines/01 §4.3) installed in a module slot (X): only adds capabilities (vacuum,
 * corrosion-resistant lining …) and may change energy use; never purity. Module items are content of
 * the machine plans; they register here.
 *
 * @param id             module id
 * @param capabilities   capabilities the module adds
 * @param energyPermille energy multiplier, 1000 = unchanged (multiple effect: 500)
 */
public record MachineModule(String id, Capabilities capabilities, int energyPermille) {
    private static final Map<String, MachineModule> BY_ID = new ConcurrentHashMap<>();
    private static final Map<Supplier<? extends Item>, MachineModule> ITEMS = new ConcurrentHashMap<>();

    public static MachineModule register(MachineModule module, Supplier<? extends Item> item) {
        if (BY_ID.putIfAbsent(module.id, module) != null) {
            throw new IllegalStateException("Duplicate module " + module.id);
        }
        ITEMS.put(item, module);
        return module;
    }

    /** Module of an item in a module slot, or {@code null}. */
    @Nullable
    public static MachineModule of(Item item) {
        for (Map.Entry<Supplier<? extends Item>, MachineModule> entry : ITEMS.entrySet()) {
            if (entry.getKey().get() == item) {
                return entry.getValue();
            }
        }
        return null;
    }
}
