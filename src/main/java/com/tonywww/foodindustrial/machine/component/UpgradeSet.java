package com.tonywww.foodindustrial.machine.component;

import com.tonywww.foodindustrial.machine.definition.MachineModule;
import com.tonywww.foodindustrial.machine.definition.Upgrade;
import com.tonywww.foodindustrial.recipe.Capabilities;
import net.minecraft.world.item.ItemStack;

/**
 * Effects of the installed upgrades and modules (machines/01 §1 rule 4, §4.3), recomputed only when
 * those slots change: speed and energy multipliers (per mille), parallel runs, standardisation
 * (machines/01 §1.2) and the capabilities the modules add. Modules never change purity; the
 * standardisation upgrade is the only upgrade that does.
 */
public final class UpgradeSet {
    private int speedPermille = 1000;
    private int energyPermille = 1000;
    private int parallel = 1;
    private boolean normalizing;
    private Capabilities moduleCapabilities = Capabilities.NONE;

    /** Recomputes from the upgrade and module slots. */
    public void recompute(MachineInventory inventory) {
        long speed = 1000;
        long energy = 1000;
        int runs = 1;
        boolean normalize = false;
        for (int i = 0; i < inventory.count(SlotRole.UPGRADE); i++) {
            ItemStack stack = inventory.get(SlotRole.UPGRADE, i);
            Upgrade upgrade = stack.isEmpty() ? null : Upgrade.of(stack.getItem());
            if (upgrade != null) {
                for (int n = 0; n < stack.getCount(); n++) {
                    speed = speed * upgrade.speedPermille() / 1000;
                    energy = energy * upgrade.energyPermille() / 1000;
                    runs += upgrade.parallel();
                }
                normalize |= upgrade.normalizing();
            }
        }
        Capabilities capabilities = Capabilities.NONE;
        for (int i = 0; i < inventory.count(SlotRole.MODULE); i++) {
            ItemStack stack = inventory.get(SlotRole.MODULE, i);
            MachineModule module = stack.isEmpty() ? null : MachineModule.of(stack.getItem());
            if (module != null) {
                capabilities = capabilities.with(module.capabilities());
                energy = energy * module.energyPermille() / 1000;
            }
        }
        speedPermille = (int) Math.max(1, Math.min(Integer.MAX_VALUE, speed));
        energyPermille = (int) Math.max(0, Math.min(Integer.MAX_VALUE, energy));
        parallel = Math.max(1, runs);
        normalizing = normalize;
        moduleCapabilities = capabilities;
    }

    public int speedPermille() {
        return speedPermille;
    }

    public int energyPermille() {
        return energyPermille;
    }

    public int parallel() {
        return parallel;
    }

    /** A standardisation module is installed (machines/01 §1.2). */
    public boolean normalizing() {
        return normalizing;
    }

    public Capabilities moduleCapabilities() {
        return moduleCapabilities;
    }
}
