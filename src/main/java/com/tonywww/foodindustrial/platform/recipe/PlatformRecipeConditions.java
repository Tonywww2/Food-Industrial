package com.tonywww.foodindustrial.platform.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.platform.PlatformIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
//? if forge {
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
//?} else {
/*import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
*///?}

/**
 * Load conditions of the mod's recipe files (I25): {@code foodindustrial:integration} with {@code mod}
 * and optional {@code tiers} loads a recipe for another mod's machine only while that integration is
 * enabled in the config and its device tier is one of {@code tiers}. Data generation adds it, after the
 * loader's {@code mod_loaded} condition, with {@link #conditional}.
 */
public final class PlatformRecipeConditions {
    private static volatile BiPredicate<String, List<String>> integration = (mod, tiers) -> true;

    private PlatformRecipeConditions() {
    }

    /** Sets what decides the integration condition: the integration's id and the listed tiers (empty: any). */
    public static void setIntegrationTest(BiPredicate<String, List<String>> test) {
        integration = test;
    }

    /**
     * {@code recipe} wrapped with the node's condition list: {@code modid} is loaded and the integration
     * {@code integrationId} is enabled at one of {@code tiers} (empty: any tier).
     */
    public static JsonObject conditional(JsonObject recipe, String modid, String integrationId, List<String> tiers) {
        return conditional(recipe, modid, integrationId, tiers, java.util.Set.of(), true);
    }

    /**
     * As {@link #conditional(JsonObject, String, String, List)}, and in addition the {@code others} mods
     * are all loaded ({@code loaded}) or not all loaded (I25, 2026-11-11: an integration recipe narrowed
     * only while another mod's recipe exists).
     */
    public static JsonObject conditional(JsonObject recipe, String modid, String integrationId, List<String> tiers,
                                         java.util.Set<String> others, boolean loaded) {
        JsonArray conditions = new JsonArray();
        conditions.add(modLoaded(modid));
        if (!others.isEmpty()) {
            JsonArray all = new JsonArray();
            others.stream().sorted().forEach(other -> all.add(modLoaded(other)));
            if (loaded) {
                all.forEach(conditions::add);
            } else {
                JsonObject inner;
                if (all.size() == 1) {
                    inner = all.get(0).getAsJsonObject();
                } else {
                    inner = new JsonObject();
                    inner.addProperty("type", NS + ":and");
                    inner.add("values", all);
                }
                JsonObject not = new JsonObject();
                not.addProperty("type", NS + ":not");
                not.add("value", inner);
                conditions.add(not);
            }
        }
        JsonObject own = new JsonObject();
        own.addProperty("type", PlatformIdentifier.id("integration").toString());
        own.addProperty("mod", integrationId);
        if (!tiers.isEmpty()) {
            JsonArray list = new JsonArray();
            tiers.forEach(list::add);
            own.add("tiers", list);
        }
        conditions.add(own);
        JsonObject result = new JsonObject();
        //? if forge {
        result.add("conditions", conditions);
        //?} else {
        /*result.add("neoforge:conditions", conditions);
        *///?}
        for (Map.Entry<String, com.google.gson.JsonElement> entry : recipe.entrySet()) {
            result.add(entry.getKey(), entry.getValue());
        }
        return result;
    }

    //? if forge {
    private static final String NS = "forge";
    //?} else {
    /*private static final String NS = "neoforge";
    *///?}

    private static JsonObject modLoaded(String modid) {
        JsonObject json = new JsonObject();
        json.addProperty("type", NS + ":mod_loaded");
        json.addProperty("modid", modid);
        return json;
    }

    /** Registers the condition type. Called by the mod entrypoint during construction. */
    public static void bind() {
        //? if forge {
        CraftingHelper.register(IntegrationCondition.SERIALIZER);
        //?} else {
        /*PlatformRegistry.register(NeoForgeRegistries.Keys.CONDITION_CODECS, "integration", () -> IntegrationCondition.CODEC);
        *///?}
    }

    //? if forge {
    private record IntegrationCondition(String mod, List<String> tiers) implements ICondition {
        private static final ResourceLocation ID = PlatformIdentifier.id("integration");
        private static final IConditionSerializer<IntegrationCondition> SERIALIZER = new IConditionSerializer<>() {
            @Override
            public void write(JsonObject json, IntegrationCondition value) {
                json.addProperty("mod", value.mod());
                if (!value.tiers().isEmpty()) {
                    JsonArray list = new JsonArray();
                    value.tiers().forEach(list::add);
                    json.add("tiers", list);
                }
            }

            @Override
            public IntegrationCondition read(JsonObject json) {
                List<String> tiers = new ArrayList<>();
                if (json.has("tiers")) {
                    for (JsonElement tier : json.getAsJsonArray("tiers")) {
                        tiers.add(tier.getAsString());
                    }
                }
                return new IntegrationCondition(json.get("mod").getAsString(), List.copyOf(tiers));
            }

            @Override
            public ResourceLocation getID() {
                return ID;
            }
        };

        @Override
        public ResourceLocation getID() {
            return ID;
        }

        @Override
        public boolean test(IContext context) {
            return integration.test(mod, tiers);
        }
    }
    //?} else {
    /*private record IntegrationCondition(String mod, List<String> tiers) implements ICondition {
        private static final MapCodec<IntegrationCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("mod").forGetter(IntegrationCondition::mod),
            Codec.STRING.listOf().optionalFieldOf("tiers", List.of()).forGetter(IntegrationCondition::tiers)
        ).apply(instance, IntegrationCondition::new));

        @Override
        public boolean test(IContext context) {
            return integration.test(mod, tiers);
        }

        @Override
        public MapCodec<? extends ICondition> codec() {
            return CODEC;
        }
    }
    *///?}
}
