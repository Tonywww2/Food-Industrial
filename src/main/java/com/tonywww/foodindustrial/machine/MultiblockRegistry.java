package com.tonywww.foodindustrial.machine;

import com.tonywww.foodindustrial.machine.entity.MultiblockControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Loaded multiblock controllers per level, so a member block that changes can tell the controllers whose
 * structure box contains it (machines/02 §6.3 rule 12). Server thread only.
 */
public final class MultiblockRegistry {
    private static final Map<Level, Map<BlockPos, MultiblockControllerBlockEntity>> CONTROLLERS = new WeakHashMap<>();

    private MultiblockRegistry() {
    }

    public static void add(MultiblockControllerBlockEntity controller) {
        CONTROLLERS.computeIfAbsent(controller.getLevel(), level -> new HashMap<>()).put(controller.getBlockPos(), controller);
    }

    public static void remove(MultiblockControllerBlockEntity controller) {
        Map<BlockPos, MultiblockControllerBlockEntity> map = CONTROLLERS.get(controller.getLevel());
        if (map != null) {
            map.remove(controller.getBlockPos(), controller);
        }
    }

    /** A member block at {@code pos} was placed, removed or (ports) loaded: controllers around it re-validate. */
    public static void memberChanged(Level level, BlockPos pos) {
        Map<BlockPos, MultiblockControllerBlockEntity> map = CONTROLLERS.get(level);
        if (map == null || map.isEmpty()) {
            return;
        }
        List<MultiblockControllerBlockEntity> hit = new ArrayList<>();
        for (MultiblockControllerBlockEntity controller : map.values()) {
            if (controller.covers(pos)) {
                hit.add(controller);
            }
        }
        hit.forEach(MultiblockControllerBlockEntity::structureChanged);
    }
}
