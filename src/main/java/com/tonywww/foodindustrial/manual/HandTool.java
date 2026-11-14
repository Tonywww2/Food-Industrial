package com.tonywww.foodindustrial.manual;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.recipe.Capabilities;
import com.tonywww.foodindustrial.recipe.DeviceProfile;
import com.tonywww.foodindustrial.recipe.DeviceSpec;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.ProcessTypes;

import java.util.Set;

/**
 * Hand tools used in the inventory by stacking (additives/07 §操作与实现规格, 2026-11-10): H01 mortar,
 * H02 sieve and cloth bag.
 */
public enum HandTool {
    MORTAR("mortar", "研钵", "Mortar", "拿起后右键点原料研磨，或拿着原料右键点研钵",
        "Held, right-click an ingredient (or the reverse) to grind",
        "grind", Set.of(ProcessTypes.GRINDING), Capabilities.NONE),
    SIEVE("sieve", "筛子", "Sieve", "拿起后右键点原料或桶装液体过滤，或反过来",
        "Held, right-click an ingredient or bucket (or the reverse)",
        "filter", Set.of(ProcessTypes.SEPARATING), Capabilities.of("method.filtration")),
    CLOTH_BAG("cloth_bag", "布袋", "Cloth Bag", "拿起后右键点原料拧压或过滤，或反过来",
        "Held, right-click an ingredient (or the reverse) to press",
        "press", Set.of(ProcessTypes.PRESSING, ProcessTypes.SEPARATING), Capabilities.of("method.filtration"));

    private final String id;
    private final String zh;
    private final String en;
    private final String descriptionZh;
    private final String descriptionEn;
    private final String verb;
    private final Set<ProcessType> types;
    private final Capabilities capabilities;
    private DeviceSpec spec;

    HandTool(String id, String zh, String en, String descriptionZh, String descriptionEn, String verb, Set<ProcessType> types,
             Capabilities capabilities) {
        this.id = id;
        this.zh = zh;
        this.en = en;
        this.descriptionZh = descriptionZh;
        this.descriptionEn = descriptionEn;
        this.verb = verb;
        this.types = types;
        this.capabilities = capabilities;
    }

    public String id() {
        return id;
    }

    public String zh() {
        return zh;
    }

    public String en() {
        return en;
    }

    public String descriptionZh() {
        return descriptionZh;
    }

    public String descriptionEn() {
        return descriptionEn;
    }

    /** Word of the progress line ({@code gui.foodindustrial.manual.<verb>}). */
    public String verb() {
        return verb;
    }

    public DeviceSpec spec() {
        return spec;
    }

    /** Registers the device specs. Called once during mod construction. */
    static void registerSpecs() {
        for (HandTool tool : values()) {
            tool.spec = DeviceSpec.register(new DeviceSpec(FoodIndustrial.MOD_ID + ":" + tool.id, DeviceProfile.standard(Tier.T0), tool.types,
                tool.capabilities, new DeviceSpec.Slots(1, 9, 1, 1), true, false, null, true));
        }
    }
}
