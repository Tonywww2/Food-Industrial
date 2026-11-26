package com.tonywww.foodindustrial.gui;

import com.tonywww.foodindustrial.core.text.Lang;

/**
 * Language entries of the recipe pages shown by JEI (I22, additives/10 §5): category names
 * ({@code jei.foodindustrial.category.<type>}) and the page texts ({@code gui.foodindustrial.recipe.*},
 * {@code gui.foodindustrial.structure.*}). English names are drafts awaiting review (2026-11-11).
 */
public final class RecipeText {
    private RecipeText() {
    }

    /** Key of a recipe page text. */
    public static String key(String key) {
        return Lang.gui("recipe", key);
    }

    /** Key of a structure page text. */
    public static String structure(String key) {
        return Lang.gui("structure", key);
    }

    /** Category of the multiblock structure pages. */
    public static final String STRUCTURE_CATEGORY = "structure";

    private static void category(String type, String zh, String en) {
        Lang.add(Lang.jeiCategory(type), zh, en);
    }

    private static void add(String key, String zh, String en) {
        Lang.add(key(key), zh, en);
    }

    static void bootstrap() {
        // machines/01 §2 type names
        category("grinding", "研磨", "Grinding");
        category("pressing", "压榨", "Pressing");
        category("separating", "固液分离", "Solid-Liquid Separation");
        category("extracting", "浸提与萃取", "Extraction");
        category("boiling", "熬煮与蒸发", "Boiling and Evaporation");
        category("crystallizing", "结晶", "Crystallization");
        category("drying", "干燥", "Drying");
        category("fumigating", "熏蒸与烟熏", "Fumigation and Smoking");
        category("kiln", "窑炉高温处理", "Kiln Firing");
        category("distilling", "蒸馏", "Distillation");
        category("reacting", "液相反应", "Liquid-Phase Reaction");
        category("absorbing", "气体吸收", "Gas Absorption");
        category("gas_reacting", "气相高温反应", "Gas-Phase Reaction");
        category("electrolyzing", "电解", "Electrolysis");
        category("purifying", "吸附与膜分离", "Adsorption and Membranes");
        category("fermenting", "液态发酵", "Liquid Fermentation");
        category("solid_fermenting", "固态发酵", "Solid Fermentation");
        category("culturing", "菌种操作", "Culture Work");
        category("mixing", "配料与混炼", "Blending");
        // machines/05 §1.1 (I38)
        category("forming", "和面与成型", "Dough and Forming");
        category("sterilizing", "杀菌", "Sterilization");
        category("cooking", "蒸煮", "Cooking");
        category("baking", "烘烤", "Baking");
        category("frying", "油炸", "Frying");
        category("curing", "腌制与陈酿", "Curing and Aging");
        category("cutting", "切割与斩拌", "Cutting and Chopping");
        category("confectionery", "糖果与巧克力", "Confectionery");
        category("chilling", "冷冻", "Chilling and Freezing");
        category("packaging", "灌装封口", "Filling and Sealing");
        category("block_transform", "方块转化", "Block Transformation");
        category("harvesting", "割取", "Harvesting");
        category("pumping", "取水", "Pumping");
        category(STRUCTURE_CATEGORY, "多方块结构", "Multiblock Structure");

        add("tag", "标签：%s", "Tag: %s");
        add("need_purity", "需要纯度 ≥ %s", "Needs purity ≥ %s");
        add("as_poor", "外部物品按劣质计", "Items from other sources count as poor");
        add("rule_max", "最高 %s", "Max %s");
        add("rule_t0_max", "手工最高 %s", "Max %s by hand");
        add("random", "纯度 %s–%s（随机）", "Purity %s–%s (random)");
        add("chance", "概率 %s", "Chance %s");
        add("min_tier", "%s 起产出", "Made from %s on");
        add("not_made", "所选设备不产出", "Not made on the selected device");
        add("plate", "挑取一个菌落", "Pick one colony");
        add("plate_weight", "出现概率 %s", "Appears %s of the time");
        add("plate_by_purity", "菌落比例随样本纯度", "Colonies follow the sample purity");
        add("catalyst_runs", "可用 %s 次", "Lasts %s runs");
        add("no_purity", "不带纯度", "No purity");
        add("time", "耗时 %s 秒", "Time %s s");
        add("actions", "操作 %s 次", "%s actions");
        add("energy", "能耗 %s FE/t", "Energy %s FE/t");
        add("yield", "产率 ×%s", "Yield ×%s");
        add("select", "点击查看此设备的产出", "Click to show this device's output");
        add("selected", "当前显示此设备的产出", "Showing this device's output");
        add("random_tick", "随机刻概率 %s", "Random tick chance %s");
        add("entities", "附近有生物时概率 %s", "Chance %s with mobs nearby");
        add("tool", "用此工具右键", "Right-click with this tool");
        add("container", "收集到此容器", "Collected into this container");
        add("cooldown", "冷却 %s 秒", "Cooldown %s s");
        add("transfer_device", "这台机器不做此配方", "This machine cannot run this recipe");
        add("transfer_missing", "背包里缺少原料", "Missing ingredients in the inventory");
        add("tool_kept", "不消耗，留在合成格", "Not used up; stays in the grid");
        add("container_back", "至少 %s mB，用后还回空容器", "At least %s mB; the empty container comes back");
        add("second_kept", "副产物：%s ×%s，留在合成格", "Second product: %s ×%s, stays in the grid");
        add("workbench_process", "工序：%s", "Process: %s");
        add("additives", "空格可放添加剂", "Free slots take additives");

        Lang.add(structure("layer"), "第 %s / %s 层", "Layer %s / %s");
        Lang.add(structure("all_layers"), "全部层", "All layers");
        Lang.add(structure("port"), "可换成仓口", "Can be a port");
        Lang.add(structure("controller"), "控制器", "Controller");
        Lang.add(structure("needs"), "需要：", "Needs:");
        Lang.add(structure("repeat"), "中间层可重复 %s–%s 次", "Middle layer repeats %s–%s times");
        Lang.add(structure("previous"), "上一层", "Previous layer");
        Lang.add(structure("next"), "下一层", "Next layer");
        Lang.add(structure("air"), "空气", "Air");
    }
}
