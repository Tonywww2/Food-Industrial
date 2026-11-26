package com.tonywww.foodindustrial.world.source;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.platform.PlatformEnchantments;
import com.tonywww.foodindustrial.platform.PlatformEvents;
import com.tonywww.foodindustrial.platform.PlatformTags;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * “肢解” (dismemberment, foods/17 §1.1, additives/11 问题 69): an adult cow, mooshroom, pig, chicken or sheep killed with
 * a weapon that has it drops its organs and parts as the generation list says, on every such kill (I44 draft).
 */
final class SourceOrgans {
    private SourceOrgans() {
    }

    static void bootstrap() {
        PlatformEnchantments.bootstrap();
        String key = "enchantment." + FoodIndustrial.MOD_ID + "." + PlatformEnchantments.DISMEMBERMENT;
        Lang.add(key, "肢解", "Dismemberment");
        Lang.add(key + ".desc", "击杀成年的牛、猪、鸡、羊时另掉落器官与部位", "Adult cows, pigs, chickens and sheep also drop organs and cuts");
        Map<String, List<SourceTable.Drop>> organs = SourceTable.get().organs();
        PlatformEvents.onLivingDrops((victim, attacker, drop) -> {
            if (!(attacker instanceof LivingEntity killer) || victim instanceof AgeableMob ageable && ageable.isBaby()) {
                return;
            }
            List<SourceTable.Drop> parts = null;
            for (Map.Entry<String, List<SourceTable.Drop>> entry : organs.entrySet()) {
                if (PlatformTags.entityMatches(victim, entry.getKey())) {
                    parts = entry.getValue();
                    break;
                }
            }
            if (parts == null || PlatformEnchantments.dismemberment(killer.getMainHandItem(), victim.level()) <= 0) {
                return;
            }
            for (SourceTable.Drop part : parts) {
                drop.accept(new ItemStack(SourceBlocks.item(part.item()), part.min()));
            }
        });
    }
}
