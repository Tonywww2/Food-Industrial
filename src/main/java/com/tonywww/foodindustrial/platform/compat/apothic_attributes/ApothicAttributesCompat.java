package com.tonywww.foodindustrial.platform.compat.apothic_attributes;

import com.tonywww.foodindustrial.platform.PlatformAttributes;
import com.tonywww.foodindustrial.platform.PlatformIdentifier;
import net.minecraft.client.gui.Font;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
//? if forge {
import dev.shadowsoffire.attributeslib.client.ModifierSource;
import dev.shadowsoffire.attributeslib.client.ModifierSourceType;
//?} else {
/*import dev.shadowsoffire.apothic_attributes.client.ModifierSource;
import dev.shadowsoffire.apothic_attributes.client.ModifierSourceType;
*///?}
//? if <26.1 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
*///?}

/**
 * Apothic Attributes (I35 §七): its attribute panel draws a source icon before every modifier and a question mark
 * for modifiers it cannot place. This source type claims the mod's declared modifiers ({@link PlatformAttributes})
 * and draws the icon the business gives for each id. Client only; loaded only when Apothic Attributes is.
 */
public final class ApothicAttributesCompat {
    /** After equipment (0) and mob effects (100). */
    private static final int PRIORITY = 200;

    private ApothicAttributesCompat() {
    }

    /** Registers the source type. {@code iconOf} gives a declared id's 16 × 16 icon, or {@code null} for none. */
    public static void register(Function<String, String> iconOf) {
        List<String> order = new ArrayList<>(PlatformAttributes.declared());
        ModifierSourceType.register(new Type(iconOf, order));
    }

    private static final class Type extends ModifierSourceType<String> {
        private final Function<String, String> iconOf;
        private final Comparator<String> comparator;

        Type(Function<String, String> iconOf, List<String> order) {
            this.iconOf = iconOf;
            this.comparator = Comparator.comparingInt(order::indexOf);
        }

        @Override
        public void extract(LivingEntity entity, BiConsumer<AttributeModifier, ModifierSource<?>> map) {
            if (!(entity instanceof Player player)) {
                return;
            }
            for (PlatformAttributes.Stat stat : PlatformAttributes.Stat.values()) {
                AttributeInstance instance = PlatformAttributes.instance(player, stat);
                if (instance == null) {
                    continue;
                }
                for (AttributeModifier modifier : instance.getModifiers()) {
                    String id = PlatformAttributes.idOf(modifier);
                    String icon = id == null ? null : iconOf.apply(id);
                    if (icon != null) {
                        map.accept(modifier, new Source(this, comparator, id, icon));
                    }
                }
            }
        }

        @Override
        public int getPriority() {
            return PRIORITY;
        }
    }

    private static final class Source extends ModifierSource<String> {
        private final String icon;

        Source(Type type, Comparator<String> comparator, String id, String icon) {
            super(type, comparator, id);
            this.icon = icon;
        }

        //? if <26.1 {
        @Override
        public void render(GuiGraphics graphics, Font font, int x, int y) {
            graphics.blit(PlatformIdentifier.parse(icon), x, y, 8, 8, 0.0F, 0.0F, 16, 16, 16, 16);
        }
        //?} else {
        /*@Override
        public void render(GuiGraphicsExtractor graphics, Font font, int x, int y) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, PlatformIdentifier.parse(icon), x, y, 0.0F, 0.0F, 8, 8, 16, 16, 16, 16);
        }
        *///?}
    }
}
