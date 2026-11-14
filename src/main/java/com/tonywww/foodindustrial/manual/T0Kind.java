package com.tonywww.foodindustrial.manual;

import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.recipe.Capabilities;
import com.tonywww.foodindustrial.recipe.DeviceProfile;
import com.tonywww.foodindustrial.recipe.DeviceSpec;
import com.tonywww.foodindustrial.recipe.Environment;
import com.tonywww.foodindustrial.recipe.ProcessType;
import com.tonywww.foodindustrial.recipe.ProcessTypes;

import java.util.Set;

/**
 * The placed T0 devices (additives/07 §操作与实现规格, 2026-11-10): the pots H03 (worked by hand) and
 * the passive devices H04–H09 (20-tick batches). Each kind fixes its contents, its process units and
 * what it can run.
 */
public enum T0Kind {
    CLAY_POT("clay_pot", "陶锅", "Clay Pot", "放在点燃的营火上熬煮，空手右键搅拌", "Boils over a lit campfire; stir with an empty hand",
        2, 1, 4000, 0, false, true,
        Set.of(ProcessTypes.EXTRACTING, ProcessTypes.BOILING, ProcessTypes.CRYSTALLIZING, ProcessTypes.KILN, ProcessTypes.REACTING),
        Capabilities.of("temperature.heated")),
    IRON_POT("iron_pot", "铁锅", "Iron Pot", "放在点燃的营火上熬煮、焦化，空手右键搅拌", "Boils and chars over a lit campfire; stir with an empty hand",
        2, 1, 4000, 0, false, true,
        Set.of(ProcessTypes.EXTRACTING, ProcessTypes.BOILING, ProcessTypes.CRYSTALLIZING, ProcessTypes.KILN, ProcessTypes.REACTING),
        Capabilities.of("temperature.heated", "method.open_flame")),
    CROCK("crock", "陶坛", "Crock", "静置发酵、腌渍、浸提，空手右键开口或封口", "Ferments, pickles and steeps; right-click to open or seal",
        2, 2, 4000, 1, false, false,
        Set.of(ProcessTypes.SEPARATING, ProcessTypes.EXTRACTING, ProcessTypes.REACTING, ProcessTypes.FERMENTING, ProcessTypes.CULTURING),
        Capabilities.of("atmosphere.air", "atmosphere.airtight", "method.settling")),
    LEACH_BARREL("leach_barrel", "淋灰桶", "Leaching Barrel", "灰或硝土加水，浸出液从底部流出", "Water poured over ash or nitre soil; the liquor drains below",
        1, 2, 4000, 1, false, false,
        Set.of(ProcessTypes.EXTRACTING, ProcessTypes.PURIFYING), Capabilities.NONE),
    SALT_PAN("salt_pan", "晒盐池", "Salt Pan", "倒入海水，露天晴天晒出盐，相邻的池相连", "Sun-dries seawater into salt; neighbouring pans join",
        1, 1, 1000, 0, false, false,
        Set.of(ProcessTypes.CRYSTALLIZING), Capabilities.NONE),
    DRYING_RACK("drying_rack", "晾晒架", "Drying Rack", "摆上四份物料晾晒或冻融", "Holds four batches to dry or freeze-thaw",
        4, 0, 0, 0, true, false,
        Set.of(ProcessTypes.DRYING, ProcessTypes.CRYSTALLIZING), Capabilities.NONE),
    KOJI_TRAY("koji_tray", "曲盘", "Koji Tray", "摊放四份谷物发芽、长曲", "Spreads four batches of grain to sprout or grow koji",
        4, 0, 0, 0, true, false,
        Set.of(ProcessTypes.SOLID_FERMENTING, ProcessTypes.CULTURING), Capabilities.of("atmosphere.air")),
    FUMIGATION_BOX("fumigation_box", "熏蒸箱", "Fumigation Box", "底部燃硫，给果干等食品加上硫熏标记", "Burns sulfur below to fumigate dried fruit and other food",
        4, 0, 0, 1, false, false,
        Set.of(ProcessTypes.FUMIGATING), Capabilities.NONE);

    /** Batch length of passive devices, ticks (machines/02 §6.1 rule 3). */
    public static final int BATCH = 20;

    private final String id;
    private final String zh;
    private final String en;
    private final String descriptionZh;
    private final String descriptionEn;
    private final int itemSlots;
    private final int tanks;
    private final int tankCapacity;
    private final int catalystSlots;
    private final boolean positions;
    private final boolean manual;
    private final Set<ProcessType> types;
    private final Capabilities own;
    private DeviceSpec spec;

    T0Kind(String id, String zh, String en, String descriptionZh, String descriptionEn, int itemSlots, int tanks, int tankCapacity,
           int catalystSlots, boolean positions, boolean manual, Set<ProcessType> types, Capabilities own) {
        this.id = id;
        this.zh = zh;
        this.en = en;
        this.descriptionZh = descriptionZh;
        this.descriptionEn = descriptionEn;
        this.itemSlots = itemSlots;
        this.tanks = tanks;
        this.tankCapacity = tankCapacity;
        this.catalystSlots = catalystSlots;
        this.positions = positions;
        this.manual = manual;
        this.types = types;
        this.own = own;
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

    /** Whether the stir of a hand advances it (the pots); otherwise it runs on time. */
    public boolean manual() {
        return manual;
    }

    /** Whether each item slot is a position of its own (rack, tray): one batch per position. */
    public boolean positions() {
        return positions;
    }

    public boolean isPot() {
        return this == CLAY_POT || this == IRON_POT;
    }

    /** A fresh, empty contents of this kind. */
    public DeviceContents newContents() {
        return new DeviceContents(itemSlots, tanks, tankCapacity, catalystSlots);
    }

    /** The process units: one per position, or one for the whole device. */
    public InPlaceProcess.Unit[] units() {
        if (positions) {
            InPlaceProcess.Unit[] units = new InPlaceProcess.Unit[itemSlots];
            for (int i = 0; i < itemSlots; i++) {
                units[i] = new InPlaceProcess.Unit(new int[]{i}, new int[0]);
            }
            return units;
        }
        int[] slots = new int[itemSlots];
        for (int i = 0; i < itemSlots; i++) {
            slots[i] = i;
        }
        // the leaching barrel puts its liquor into the outlet tank (1) before the inlet tank (0)
        int[] tankOrder = this == LEACH_BARREL ? new int[]{1, 0} : tanks == 2 ? new int[]{0, 1} : tanks == 1 ? new int[]{0} : new int[0];
        return new InPlaceProcess.Unit[]{new InPlaceProcess.Unit(slots, tankOrder)};
    }

    /** What the device runs; registered once. */
    public DeviceSpec spec() {
        return spec;
    }

    /** Capabilities of the device itself, any state (the environment is added for passive devices). */
    public Capabilities ownCapabilities() {
        return own;
    }

    /** Registers the device specs. Called once during mod construction. */
    static void registerSpecs() {
        for (T0Kind kind : values()) {
            int perUnitSlots = kind.positions ? 1 : kind.itemSlots;
            Capabilities capabilities = kind.manual ? kind.own : kind.own.with(Environment.ALL);
            kind.spec = DeviceSpec.register(new DeviceSpec(FoodIndustrial.MOD_ID + ":" + kind.id, DeviceProfile.standard(Tier.T0), kind.types,
                capabilities, new DeviceSpec.Slots(perUnitSlots, perUnitSlots, kind.tanks, kind.tanks, kind.catalystSlots),
                kind.manual, !kind.manual, null));
        }
    }
}
