package com.tonywww.foodindustrial.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tonywww.foodindustrial.FoodIndustrial;
import com.tonywww.foodindustrial.core.Tier;
import com.tonywww.foodindustrial.core.text.Lang;
import com.tonywww.foodindustrial.machine.definition.MachineDefinition;
import com.tonywww.foodindustrial.machine.definition.MachineMode;
import com.tonywww.foodindustrial.machine.definition.MachineModule;
import com.tonywww.foodindustrial.machine.definition.PowerKind;
import com.tonywww.foodindustrial.machine.definition.StructurePattern;
import com.tonywww.foodindustrial.manual.HandTool;
import com.tonywww.foodindustrial.manual.T0Kind;
import com.tonywww.foodindustrial.registry.ModCreativeTabs;
import com.tonywww.foodindustrial.platform.PlatformItem;
import com.tonywww.foodindustrial.platform.PlatformRegistry;
import com.tonywww.foodindustrial.platform.PlatformTags;
import com.tonywww.foodindustrial.platform.compat.patchouli.PatchouliBooks;
import com.tonywww.foodindustrial.platform.datagen.PlatformDatagen;
import com.tonywww.foodindustrial.recipe.ProcessType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The handbook (I23, additives/10 §5, additives/07 §历史原型速览): a Patchouli book with one chapter per
 * tier T0–T4, an entry for every hand tool, passive device, world interaction and machine, the
 * structure preview of every multiblock, and a chapter of historical prototypes. Crafted from a book
 * and wheat (2026-11-11, user decision); never given automatically.
 * <p>
 * Entry texts are written in full sentences from the machine data (names, descriptions, recipe
 * types, power, modes, modules, structure) when the mod is constructed, as language entries
 * ({@code book.foodindustrial.manual.*}); the book uses Patchouli's i18n mode, so one set of entry
 * files serves both languages. English texts are drafts awaiting review (2026-11-11).
 */
public final class ManualBook {
    public static final String ID = "manual";
    private static final String NS = FoodIndustrial.MOD_ID;
    private static final String BOOK = "book." + NS + "." + ID;

    public static final Supplier<ManualItem> ITEM = PlatformRegistry.item(ID, ManualItem::new, new Item.Properties().stacksTo(1));

    private static final List<Chapter> CHAPTERS = new ArrayList<>();
    private static final List<Entry> ENTRIES = new ArrayList<>();

    private ManualBook() {
    }

    /** A Patchouli category. */
    private record Chapter(String id, String icon, int sort) {
    }

    /** A Patchouli entry: its pages as JSON objects (texts are language keys). */
    private record Entry(String id, String chapter, String icon, List<Supplier<JsonObject>> pages, int sort) {
    }

    /** The book item: right-click opens the handbook (needs Patchouli). */
    public static final class ManualItem extends PlatformItem {
        public ManualItem(Item.Properties properties) {
            super(properties);
        }

        @Override
        protected boolean onUseInAir(Level level, Player player, InteractionHand hand) {
            if (!level.isClientSide() && player instanceof ServerPlayer server && !PatchouliBooks.open(server, ID)) {
                com.tonywww.foodindustrial.platform.PlatformPlayers.actionBar(server, Component.translatable(BOOK + ".needs_patchouli"));
            }
            return true;
        }

        @Override
        protected void appendTooltip(ItemStack stack, List<Component> lines, boolean shiftDown) {
            com.tonywww.foodindustrial.core.text.Tooltips.machine(Lang.description(ID), lines, shiftDown);
        }
    }

    // ---- text helpers ----

    private static String key(String path) {
        return BOOK + "." + path;
    }

    private static String add(String path, String zh, String en) {
        Lang.add(key(path), zh, en);
        return key(path);
    }

    private static String zh(String key) {
        return Lang.zh().getOrDefault(key, key);
    }

    private static String en(String key) {
        return Lang.en().getOrDefault(key, key);
    }

    private static String typeZh(ProcessType type) {
        return zh(Lang.jeiCategory(type.id()));
    }

    private static String typeEn(ProcessType type) {
        return en(Lang.jeiCategory(type.id()));
    }

    private static String modeZh(String mode) {
        return zh(Lang.condition("mode." + mode));
    }

    private static String modeEn(String mode) {
        return en(Lang.condition("mode." + mode));
    }

    private static JsonObject text(String key) {
        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:text");
        page.addProperty("text", key);
        return page;
    }

    private static JsonObject spotlight(String item, String key) {
        JsonObject page = new JsonObject();
        page.addProperty("type", "patchouli:spotlight");
        page.addProperty("item", item);
        page.addProperty("text", key);
        return page;
    }

    private static Supplier<JsonObject> constant(JsonObject page) {
        return () -> page;
    }

    // ---- content ----

    /**
     * Writes the language entries and registers the item, the book files and the recipe. Called at the
     * end of mod construction, after every machine, tool and device has its names.
     */
    public static void bootstrap() {
        Lang.add(Lang.item(ID), "工艺手册", "Process Handbook");
        Lang.add(Lang.description(ID), "按阶段介绍工具、机器与工艺", "Tools, machines and processes by stage");
        ModCreativeTabs.add(ModCreativeTabs.Tab.MACHINES, output -> output.accept(new ItemStack(ITEM.get())));
        Lang.add(key("name"), "工艺手册", "Process Handbook");
        Lang.add(key("subtitle"), "食品工业", "Food Industrial");
        // the handbook names stages, never tiers or 档次 (2026-11-11)
        Lang.add(key("landing"),
            "这本手册按阶段介绍食品工业的工具、机器和工艺。$(br2)每一章是一个阶段：" + stage(Tier.T0, true) + "用手和世界，"
                + stage(Tier.T1, true) + "烧燃料或用人力，" + stage(Tier.T2, true) + "起用电，" + stage(Tier.T3, true) + "做化学合成，"
                + stage(Tier.T4, true) + "做发酵与精细化学品。在 JEI 里对机器按 R、U 就能查到配方。",
            "This handbook walks through the tools, machines and processes of food industry, stage by stage.$(br2)Each chapter is one stage: "
                + stage(Tier.T0, false) + " works by hand and with the world, " + stage(Tier.T1, false) + " burns fuel or turns by hand, "
                + stage(Tier.T2, false) + " runs on power, " + stage(Tier.T3, false) + " does chemistry and " + stage(Tier.T4, false)
                + " does fermentation and fine chemicals. Press R or U on any machine in JEI to see its recipes.");
        Lang.add(key("needs_patchouli"), "需要安装 Patchouli 才能阅读", "Patchouli is needed to read it");

        chapter("t0", "minecraft:campfire", 0, STAGE_ZH[0], STAGE_EN[0],
            "不用机器：手持工具、放在世界里的被动设备、原版的营火和熔炉，以及世界里的自然变化。",
            "No machines: hand tools, passive devices placed in the world, vanilla campfires and furnaces, and what the world does by itself.");
        chapter("t1", NS + ":stone_mill", 1, STAGE_ZH[1], STAGE_EN[1],
            "烧燃料或用手摇柄的作坊机器，其中三台是多方块的房间。",
            "Workshop machines that burn fuel or turn on a hand crank; three of them are rooms built as multiblocks.");
        chapter("t2", NS + ":crusher", 2, STAGE_ZH[2], STAGE_EN[2],
            "用 FE 能量的单方块机器，比作坊快一倍，产率更高。",
            "Single-block machines on FE, twice as fast as the workshop and with better yields.");
        chapter("t3", NS + ":high_pressure_reactor", 3, STAGE_ZH[3], STAGE_EN[3],
            "高压、高温、电解和精馏，多数是多方块。",
            "High pressure, high heat, electrolysis and rectification, mostly multiblocks.");
        chapter("t4", NS + ":culture_bench", 4, STAGE_ZH[4], STAGE_EN[4],
            "无菌发酵、菌种操作和膜分离。",
            "Sterile fermentation, culture work and membranes.");
        chapter("history", "minecraft:writable_book", 5, "历史原型", "Historical Prototypes",
            "游戏里的做法大多有真实的历史原型，这里简述它们的来历。",
            "Most methods in the game have real historical prototypes; this chapter tells where they come from.");

        int sort = 0;
        for (HandTool tool : HandTool.values()) {
            simple("t0", tool.id(), NS + ":" + tool.id(), sort++, tool.zh(), tool.en(), tool.descriptionZh(), tool.descriptionEn(),
                new ArrayList<>(tool.spec().types()));
        }
        for (T0Kind kind : T0Kind.values()) {
            simple("t0", kind.id(), NS + ":" + kind.id(), sort++, kind.zh(), kind.en(), kind.descriptionZh(), kind.descriptionEn(),
                new ArrayList<>(kind.spec().types()));
        }
        world(sort);
        for (Tier tier : List.of(Tier.T1, Tier.T2, Tier.T3, Tier.T4)) {
            int order = 0;
            for (MachineDefinition machine : MachineDefinition.all()) {
                if (machine.tier() == tier && !machine.development()) {
                    machine(machine, order++);
                }
            }
        }
        history();
        PlatformDatagen.register("manual_book", ManualBook::generate);
    }

    /** Stage names of the chapters by tier: the handbook names stages, never tiers (2026-11-11). */
    private static final String[] STAGE_ZH = {"手工与世界", "作坊", "工业", "化工", "生物与精细"};
    private static final String[] STAGE_EN = {"By Hand", "Workshop", "Industry", "Chemistry", "Biology and Fine Chemicals"};

    /** The stage of {@code tier} in the tier's colour (docs/text-standard.md §3.4). */
    private static String stage(Tier tier, boolean chinese) {
        String name = (chinese ? STAGE_ZH : STAGE_EN)[tier.ordinal()];
        return "$(#" + String.format(java.util.Locale.ROOT, "%06x", tier.color()) + ")" + name + "$()";
    }

    private static void chapter(String id, String icon, int sort, String zh, String en, String descZh, String descEn) {
        add("category." + id, zh, en);
        add("category." + id + ".desc", descZh, descEn);
        CHAPTERS.add(new Chapter(id, icon, sort));
    }

    /** Entry of a hand tool or passive device: what it is and which recipe types it runs. */
    private static void simple(String chapter, String id, String item, int sort, String zh, String en, String descZh, String descEn,
                               List<ProcessType> types) {
        List<String> typesZh = new ArrayList<>();
        List<String> typesEn = new ArrayList<>();
        types.sort((a, b) -> a.code().compareTo(b.code()));
        for (ProcessType type : types) {
            typesZh.add(typeZh(type));
            typesEn.add(typeEn(type));
        }
        String name = add("entry." + id, zh, en);
        String body = add("entry." + id + ".text",
            zh + "是" + stage(Tier.T0, true) + "阶段的设备。" + descZh + "。$(br2)可做：" + String.join("、", typesZh)
                + "。产物纯度有手工上限，用后面阶段的设备能做得更纯。",
            "The " + en + " is a device of the " + stage(Tier.T0, false) + " stage. " + descEn + ".$(br2)Recipe types: "
                + String.join(", ", typesEn) + ". Products made by hand have a purity cap; later stages make them purer.");
        List<Supplier<JsonObject>> pages = new ArrayList<>();
        JsonObject spotlight = spotlight(item, body);
        spotlight.addProperty("title", name);
        pages.add(() -> spotlight);
        ENTRIES.add(new Entry(id, chapter, item, pages, sort));
    }

    private static void world(int sort) {
        List<Supplier<JsonObject>> pages = new ArrayList<>();
        pages.add(constant(spotlight("minecraft:furnace", add("entry.vanilla_devices.text",
            "营火、熔炉、烟熏炉和高炉也能做本模组的干燥、烟熏和窑炉配方，只收一个物品、出一个物品。原版自己的配方优先。$(br2)营火最慢，烟熏炉和高炉最快；高炉算" + stage(Tier.T1, true) + "阶段。",
            "Campfires, furnaces, smokers and blast furnaces also run the mod's drying, smoking and kiln recipes, one item in and one item out. Vanilla recipes come first.$(br2)The campfire is slowest, the smoker and blast furnace fastest; the blast furnace counts as the " + stage(Tier.T1, false) + " stage."))));
        add("entry.vanilla_devices", "原版设备", "Vanilla Devices");
        ENTRIES.add(new Entry("vanilla_devices", "t0", "minecraft:furnace", pages, sort++));

        pages = new ArrayList<>();
        pages.add(constant(spotlight(NS + ":flint_knife", add("entry.harvesting.text",
            "主手拿刀、副手拿空瓶，右键橡胶木原木割取乳胶，右键云杉原木割取松脂。割过的木头要休息一段时间。",
            "Hold a knife in the main hand and a bottle in the other, then right-click a rubber log for latex or a spruce log for resin. A cut log rests for a while."))));
        add("entry.harvesting", "割取", "Harvesting");
        ENTRIES.add(new Entry("harvesting", "t0", NS + ":flint_knife", pages, sort++));

        pages = new ArrayList<>();
        pages.add(constant(spotlight(NS + ":nitre_soil", add("entry.nitre_soil.text",
            "不露天的泥土在牲畜附近会慢慢变成硝土，随机刻也会让它变化。硝土加水淋出硝水，熬煮结晶得到硝石。",
            "Sheltered dirt slowly turns into nitre soil near livestock or by random ticks. Leach it with water and boil the liquor to crystallise saltpetre."))));
        add("entry.nitre_soil", "硝土", "Nitre Soil");
        ENTRIES.add(new Entry("nitre_soil", "t0", NS + ":nitre_soil", pages, sort++));

        pages = new ArrayList<>();
        pages.add(constant(spotlight(NS + ":seawater_bucket", add("entry.seawater.text",
            "在海洋或沙滩群系用水桶装水，得到的是海水。倒进晒盐池，露天晴天晒出盐，剩下苦卤。",
            "A water bucket filled in an ocean or beach biome holds seawater. Pour it into salt pans in the open and clear weather to get salt, with bittern left over."))));
        add("entry.seawater", "海水", "Seawater");
        ENTRIES.add(new Entry("seawater", "t0", NS + ":seawater_bucket", pages, sort));
    }

    /** Entry of a machine: description, recipe types, power, modes, modules and the structure. */
    private static void machine(MachineDefinition machine, int sort) {
        String id = machine.id();
        String item = NS + ":" + id;
        String nameZh = zh(Lang.block(id));
        String nameEn = en(Lang.block(id));
        String descZh = zh(Lang.description(id));
        String descEn = en(Lang.description(id));
        List<String> typesZh = new ArrayList<>();
        List<String> typesEn = new ArrayList<>();
        for (ProcessType type : machine.types()) {
            typesZh.add(typeZh(type));
            typesEn.add(typeEn(type));
        }
        String powerZh = switch (machine.power()) {
            case FUEL -> "烧燃料（煤、木炭、木柴）";
            case MANUAL -> "顶面的手摇柄，空手右键摇动";
            case ENERGY -> "FE 能量，从任意未关闭的面输入";
            default -> "不需要动力";
        };
        String powerEn = switch (machine.power()) {
            case FUEL -> "fuel (coal, charcoal or firewood)";
            case MANUAL -> "a hand crank on top, turned with an empty hand";
            case ENERGY -> "FE through any face that is not closed";
            default -> "none";
        };
        StringBuilder zh = new StringBuilder(nameZh + "是" + stage(machine.tier(), true) + "阶段的机器。" + descZh + "。$(br2)可做："
            + String.join("、", typesZh) + "。$(br)动力：" + powerZh + "。");
        StringBuilder en = new StringBuilder("The " + nameEn + " is a machine of the " + stage(machine.tier(), false) + " stage. " + descEn
            + ".$(br2)Recipe types: " + String.join(", ", typesEn) + ".$(br)Power: " + powerEn + ".");
        List<MachineMode> modes = machine.modes();
        if (modes.size() > 1) {
            List<String> modeZh = new ArrayList<>();
            List<String> modeEn = new ArrayList<>();
            for (MachineMode mode : modes) {
                modeZh.add(modeZh(mode.id()));
                modeEn.add(modeEn(mode.id()));
            }
            zh.append("$(br)模式：").append(String.join("、", modeZh)).append("，在界面里切换。");
            en.append("$(br)Modes: ").append(String.join(", ", modeEn)).append(", switched in the screen.");
        }
        List<MachineModule> modules = machine.modules();
        if (!modules.isEmpty()) {
            List<String> moduleZh = new ArrayList<>();
            List<String> moduleEn = new ArrayList<>();
            for (MachineModule module : modules) {
                moduleZh.add(zh(Lang.item(module.id())));
                moduleEn.add(en(Lang.item(module.id())));
            }
            zh.append("$(br)模块：").append(String.join("、", moduleZh)).append("。");
            en.append("$(br)Modules: ").append(String.join(", ", moduleEn)).append(".");
        }
        String body = add("entry." + id + ".text", zh.toString(), en.toString());
        add("entry." + id, nameZh, nameEn);
        List<Supplier<JsonObject>> pages = new ArrayList<>();
        JsonObject spotlight = spotlight(item, body);
        pages.add(() -> spotlight);
        StructurePattern structure = machine.structure();
        if (structure != null) {
            int[] repeat = structure.repeat();
            String structureZh = "多方块：控制器放在前面一层的中间，外壳搭成 " + structure.smallest().width() + " × "
                + structure.smallest().height() + " × " + structure.smallest().depth() + " 的结构。外壳的位置可换成物品仓、流体仓或能量仓，供自动化进出。";
            String structureEn = "Multiblock: the controller sits in the middle of the front face of a " + structure.smallest().width() + " × "
                + structure.smallest().height() + " × " + structure.smallest().depth() + " shell. Any shell block may be an item, fluid or energy port for automation.";
            if (repeat != null) {
                structureZh += "中间层可重复 " + repeat[0] + "–" + repeat[1] + " 次，越高并行越多。";
                structureEn += " The middle layer repeats " + repeat[0] + "–" + repeat[1] + " times; taller runs more in parallel.";
            }
            pages.add(() -> {
                JsonObject page = new JsonObject();
                page.addProperty("type", "patchouli:multiblock");
                page.addProperty("name", key("entry." + id));
                page.addProperty("enable_visualize", true);
                page.add("multiblock", multiblock(machine));
                return page;
            });
            JsonObject structurePage = text(add("entry." + id + ".structure", structureZh, structureEn));
            pages.add(() -> structurePage);
        }
        ENTRIES.add(new Entry(id, machine.tier().name().toLowerCase(java.util.Locale.ROOT), item, pages, sort));
    }

    /** The Patchouli multiblock of a machine's smallest shape: layers top first, the controller is '0'. */
    private static JsonObject multiblock(MachineDefinition machine) {
        StructurePattern pattern = machine.structure().smallest();
        JsonObject json = new JsonObject();
        JsonObject mapping = new JsonObject();
        Map<Character, Character> symbols = new LinkedHashMap<>();
        JsonArray layers = new JsonArray();
        for (int y = pattern.height() - 1; y >= 0; y--) {
            JsonArray rows = new JsonArray();
            for (int z = 0; z < pattern.depth(); z++) {
                StringBuilder row = new StringBuilder();
                for (int x = 0; x < pattern.width(); x++) {
                    char c = pattern.cell(x, y, z);
                    if (c == ' ') {
                        row.append(' ');
                    } else if (c == 'C') {
                        row.append('0');
                    } else {
                        char symbol = symbols.computeIfAbsent(c, key -> (char) ('A' + symbols.size()));
                        row.append(symbol);
                    }
                }
                rows.add(row.toString());
            }
            layers.add(rows);
        }
        symbols.forEach((c, symbol) -> {
            StructurePattern.Member member = pattern.member(c);
            Block block = member.blocks().get(0).get();
            mapping.addProperty(String.valueOf(symbol), PlatformTags.blockId(block));
        });
        mapping.addProperty("0", NS + ":" + machine.id() + "[facing=south]");
        json.add("mapping", mapping);
        json.add("pattern", layers);
        return json;
    }

    private static void history() {
        // id, icon, name zh / en, the history zh, the game zh, the history en, the game en; the game goes on its
        // own line (2026-11-11)
        String[][] items = {
            {"kelp_ash", "minecraft:kelp", "海藻灰制碱", "Kelp Ash Soda",
                "17–19 世纪，苏格兰和法国诺曼底等地的沿海居民烧海藻得到灰，从灰里淋出碱，也提取碘。",
                "游戏里用营火烧灰、淋灰桶淋出碱液。",
                "From the 17th to the 19th century, coastal people in Scotland, Normandy and elsewhere burned seaweed and leached soda and iodine from the ash.",
                "In the game, ash is burned on a campfire and leached in a leaching barrel."},
            {"potash", "minecraft:oak_log", "草木灰与珍珠灰", "Potash and Pearl Ash",
                "从古代到 19 世纪，各地都用水淋草木灰，再把淋出的灰水熬干得到钾碱。",
                "游戏里是营火烧灰、淋灰桶、锅熬煮三步。",
                "From antiquity to the 19th century, people leached wood ash with water and boiled the lye dry for potash.",
                "In the game it takes three steps: ash on a campfire, a leaching barrel and a pot."},
            {"sea_salt", NS + ":salt_pan", "晒盐与苦卤", "Sea Salt and Bittern",
                "中国沿海的盐田把海水引进浅池晒干得盐，剩下的苦卤用来点豆腐。",
                "游戏里的晒盐池照此设计。",
                "Salt fields on the Chinese coast let seawater dry in shallow pans; the bittern left over set tofu.",
                "The salt pan in the game follows them."},
            {"kanten", NS + ":drying_rack", "寒天", "Kanten",
                "日本江户时代，京都一带在冬季把石花菜冻放在室外反复冻融，脱水后得到寒天（琼脂）。",
                "游戏里在寒冷群系用晾晒架冻融。",
                "In Edo-period Japan, people around Kyoto froze and thawed gelidium jelly outdoors through the winter to dry it into kanten (agar).",
                "In the game the drying rack does it in cold biomes."},
            {"red_yeast_rice", NS + ":koji_tray", "红曲", "Red Yeast Rice",
                "中国传统的曲房培养红曲霉，宋代前后已普遍用来酿酒和着色。",
                "游戏里曲盘和曲房都能做，纯度随机。",
                "Traditional Chinese koji rooms grew red yeast rice, widely used for wine and colouring by the Song dynasty.",
                "In the game the koji tray and the koji room make it, with random purity."},
            {"saltpetre", NS + ":nitre_soil", "熬硝", "Boiling Saltpetre",
                "中国古代扫取墙根的硝土，淋水后加草木灰，再熬煮结晶得到硝石。",
                "游戏里的硝土、淋灰桶和锅还原了这个过程。",
                "In ancient China, nitre soil scraped from the foot of walls was leached, mixed with ash lye and boiled until saltpetre crystallised.",
                "The nitre soil, leaching barrel and pot of the game follow it."},
            {"sulfuring", NS + ":fumigation_box", "燃硫熏蒸", "Sulfur Fumigation",
                "传统的果干、蜜饯和银耳加工在密闭的屋里燃烧硫磺熏蒸，防止褐变和霉变。",
                "游戏里用熏蒸箱。",
                "Dried fruit, candied fruit and white fungus were traditionally fumigated with burning sulfur in a closed room against browning and mould.",
                "The game uses the fumigation box."},
            {"hartshorn", NS + ":earth_kiln", "鹿角盐", "Hartshorn",
                "17–18 世纪的欧洲用鹿角干馏得到碳酸铵类的膨松剂，叫鹿角盐。",
                "游戏里用土窑干馏。",
                "In the 17th and 18th centuries, Europeans dry-distilled deer antlers into an ammonium carbonate leavening called hartshorn.",
                "In the game, the clay kiln dry-distils it."},
            {"tartar", NS + ":wooden_tank", "酒石", "Tartar",
                "古罗马人已经知道葡萄酒桶壁上的酒石；1769 年舍勒用酒石制得酒石酸。",
                "游戏里把葡萄酒放在木桶罐里冷置，罐壁析出酒石。",
                "The Romans knew the tartar on the walls of wine casks; in 1769 Scheele made tartaric acid from it.",
                "In the game, wine kept cold in a wooden tank deposits tartar."},
            {"stevia", NS + ":stevia", "甜菊", "Stevia",
                "南美巴拉圭的瓜拉尼人用甜菊叶给马黛茶调味。",
                "游戏里在锅里煮甜菊叶取甜液。",
                "The Guarani of Paraguay sweetened mate with stevia leaves.",
                "In the game, stevia leaves are boiled in a pot for a sweet liquor."},
            {"indigo", NS + ":indigo_plant", "靛蓝", "Indigo",
                "中国古代用蓼蓝发酵制靛，“青出于蓝”说的就是它。",
                "游戏里在陶坛中发酵。",
                "Ancient China fermented indigo plants into indigo dye; the saying that blue comes from the indigo plant is about it.",
                "In the game it ferments in a crock."},
            {"cod_liver_oil", "minecraft:cod", "鱼肝油", "Cod Liver Oil",
                "18–19 世纪北欧沿海熬炼鳕鱼肝取油。",
                "游戏里在锅里熬煮。",
                "In the 18th and 19th centuries, Nordic coasts rendered oil from cod livers.",
                "In the game it is boiled in a pot."},
            {"tapping", NS + ":flint_knife", "割胶与采脂", "Tapping Latex and Resin",
                "中美洲的奥尔梅克人已经使用橡胶树的乳胶；割取松脂的历史也很悠久。",
                "游戏里用刀割取。",
                "The Olmecs of Central America already used rubber tree latex, and tapping pine resin is just as old.",
                "In the game, a knife cuts both."},
            {"papain", NS + ":flint_knife", "木瓜嫩肉", "Papaya Tenderising",
                "中美洲用木瓜叶和木瓜乳汁包裹肉类，让肉变软嫩。",
                "游戏里割取木瓜乳汁。",
                "In Central America, meat was wrapped in papaya leaves and latex to tenderise it.",
                "In the game, papaya latex is cut with a knife."}
        };
        int sort = 0;
        for (String[] row : items) {
            add("entry.history_" + row[0], row[2], row[3]);
            String body = add("entry.history_" + row[0] + ".text", row[4] + "$(br)" + row[5], row[6] + "$(br)" + row[7]);
            List<Supplier<JsonObject>> pages = new ArrayList<>();
            pages.add(constant(spotlight(row[1], body)));
            ENTRIES.add(new Entry("history_" + row[0], "history", row[1], pages, sort++));
        }
    }

    // ---- files ----

    private static void generate(PlatformDatagen.Sink sink) {
        JsonObject book = new JsonObject();
        book.addProperty("name", key("name"));
        book.addProperty("landing_text", key("landing"));
        book.addProperty("use_resource_pack", true);
        book.addProperty("i18n", true);
        book.addProperty("show_progress", false);
        book.addProperty("custom_book_item", NS + ":" + ID);
        // Version 0 makes Patchouli show the translated subtitle instead of "1st Edition" (I23, 2026-11-11).
        book.addProperty("subtitle", key("subtitle"));
        sink.json(PlatformDatagen.Kind.DATA, NS, "patchouli_books/" + ID + "/book", book);
        String base = "patchouli_books/" + ID + "/en_us/";
        for (Chapter chapter : CHAPTERS) {
            JsonObject json = new JsonObject();
            json.addProperty("name", key("category." + chapter.id));
            json.addProperty("description", key("category." + chapter.id + ".desc"));
            json.addProperty("icon", chapter.icon);
            json.addProperty("sortnum", chapter.sort);
            sink.json(PlatformDatagen.Kind.ASSET, NS, base + "categories/" + chapter.id, json);
        }
        for (Entry entry : ENTRIES) {
            JsonObject json = new JsonObject();
            json.addProperty("name", key("entry." + entry.id));
            json.addProperty("icon", entry.icon);
            json.addProperty("category", NS + ":" + entry.chapter);
            json.addProperty("sortnum", entry.sort);
            JsonArray pages = new JsonArray();
            entry.pages.forEach(page -> pages.add(page.get()));
            json.add("pages", pages);
            sink.json(PlatformDatagen.Kind.ASSET, NS, base + "entries/" + entry.chapter + "/" + entry.id, json);
        }
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:crafting_shapeless");
        recipe.addProperty("category", "misc");
        JsonArray ingredients = new JsonArray();
        ingredients.add(PlatformDatagen.ingredient("minecraft:book"));
        ingredients.add(PlatformDatagen.ingredient("minecraft:wheat"));
        recipe.add("ingredients", ingredients);
        recipe.add("result", PlatformDatagen.recipeResult(NS + ":" + ID, 1));
        sink.json(PlatformDatagen.Kind.RECIPE, NS, ID, recipe);
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/generated");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "minecraft:item/book");
        model.add("textures", textures);
        sink.json(PlatformDatagen.Kind.MODEL, NS, "item/" + ID, model);
        JsonObject definition = new JsonObject();
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("type", "minecraft:model");
        itemModel.addProperty("model", NS + ":item/" + ID);
        definition.add("model", itemModel);
        sink.json(PlatformDatagen.Kind.ITEM_DEFINITION, NS, ID, definition);
    }
}
