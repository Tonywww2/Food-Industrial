package com.tonywww.foodindustrial.gui;

import com.tonywww.foodindustrial.core.text.Lang;

/** Language entries of the machine screens (machines/04 §7), keys {@code gui.foodindustrial.<group>.<key>}. */
public final class GuiText {
    private GuiText() {
    }

    /** Key of a screen text. */
    public static String key(String group, String key) {
        return Lang.gui(group, key);
    }

    private static void add(String group, String key, String zh, String en) {
        Lang.add(Lang.gui(group, key), zh, en);
    }

    static void bootstrap() {
        add("panel", "info", "信息", "Info");
        add("panel", "upgrades", "升级与模块", "Upgrades and modules");
        add("panel", "io", "I/O", "I/O");
        add("panel", "redstone", "红石", "Redstone");

        add("status", "running", "运行中", "Running");
        add("status", "waiting", "等待原料", "Waiting for inputs");
        add("status", "output_full", "输出已满", "Output full");
        add("status", "no_power", "能量不足", "Not enough energy");
        add("status", "no_fuel", "燃料不足", "Not enough fuel");
        add("status", "no_crank", "需要摇手摇柄", "Turn the hand crank");
        add("status", "missing", "条件不满足：%s", "Missing: %s");
        add("status", "catalyst", "催化剂", "Catalyst");
        add("status", "redstone", "红石禁止", "Stopped by redstone");
        add("status", "unformed", "结构未成形", "Structure incomplete");
        add("status", "sterilizing", "在位灭菌", "Sterilizing in place");
        add("status", "inoculate", "等待接种", "Ready to inoculate");
        add("status", "plate", "平板待挑取", "Plate ready to pick");

        add("io", "off", "关闭", "Off");
        add("io", "input", "输入", "Input");
        add("io", "output", "输出", "Output");
        add("io", "both", "输入 + 输出", "Input + output");
        add("io", "items", "物品：%s", "Items: %s");
        add("io", "fluids", "流体：%s", "Fluids: %s");
        add("io", "neighbour", "相邻：%s", "Next to: %s");
        add("io", "front", "前", "Front");
        add("io", "back", "后", "Back");
        add("io", "left", "左", "Left");
        add("io", "right", "右", "Right");
        add("io", "top", "上", "Top");
        add("io", "bottom", "下", "Bottom");
        add("io", "auto_input_items", "自动输入物品：%s", "Auto input items: %s");
        add("io", "auto_input_fluids", "自动输入流体：%s", "Auto input fluids: %s");
        add("io", "auto_output_items", "自动输出物品：%s", "Auto output items: %s");
        add("io", "auto_output_fluids", "自动输出流体：%s", "Auto output fluids: %s");
        add("io", "on", "开", "On");
        add("io", "switched_off", "关", "Off");
        add("io", "advanced", "Alt + 单击：高级选项", "Alt + click: advanced options");
        add("io", "advanced_title", "高级：%s", "Advanced: %s");
        add("io", "scope_out", "输出范围", "Output range");
        add("io", "scope_in", "输入范围", "Input range");
        add("io", "label_items", "物品：", "Items:");
        add("io", "label_fluids", "流体：", "Fluids:");
        add("io", "scope_all", "全部", "All");
        add("io", "scope_main", "仅主产物", "Main product only");
        add("io", "scope_byproduct", "仅副产物", "Byproducts only");
        add("io", "scope_tank", "储罐 %s", "Tank %s");
        add("io", "back_to_faces", "返回", "Back");

        add("redstone", "ignore", "忽略", "Ignore");
        add("redstone", "low", "低电平运行", "Run on low signal");
        add("redstone", "high", "高电平运行", "Run on high signal");
        add("redstone", "disabled", "禁用", "Disabled");

        add("info", "recipe", "配方：%s", "Recipe: %s");
        add("info", "none", "无", "None");
        add("info", "progress", "进度：%s / %s tick", "Progress: %s / %s tick");
        add("info", "remaining", "剩余：%s s", "Remaining: %s s");
        add("info", "speed", "速率：×%s", "Speed: ×%s");
        add("info", "energy", "能耗：%s FE/t", "Energy use: %s FE/t");
        add("info", "purity", "产出纯度：%s", "Output purity: %s");
        add("info", "yield", "产率：%s%%", "Yield: %s%%");
        add("info", "capabilities", "能力：", "Capabilities:");
        // M06 climate (I14, additives/06 §T1 作坊)
        add("info", "climate", "温湿度：%s", "Climate: %s");
        add("info", "contaminated", "上一批：染菌，主产物减半", "Last batch contaminated: main product halved");
        // Jade overlay (2026-11-11): condition states, fuel, crank, catalysts, progress text
        add("jade", "temperature", "温度：%s", "Temperature: %s");
        add("jade", "pressure", "压力：%s", "Pressure: %s");
        add("jade", "atmosphere", "气氛：%s", "Atmosphere: %s");
        add("jade", "environment", "环境：%s", "Surroundings: %s");
        add("jade", "air_blocked", "进气口被挡住", "Air intake blocked");
        add("jade", "interrupted", "本批中途停过，完成时可能染菌", "This batch stopped on the way and may come out contaminated");
        add("jade", "fuel", "燃料：剩余 %s", "Fuel: %s left");
        add("jade", "crank", "手摇：可运行 %s", "Crank: runs for %s");
        add("jade", "catalyst", "%s：剩余 %s 次", "%s: %s uses left");
        add("jade", "saturated", "%s：已饱和", "%s: saturated");
        add("jade", "remaining", "剩余 %s", "%s left");
        add("jade", "seconds", "%s 秒", "%s s");
        add("jade", "minutes", "%s 分 %s 秒", "%s min %s s");
        Lang.add("config.jade.plugin_foodindustrial.machine", "食品工业：机器状态", "Food Industrial: machine state");
        Lang.add("config.jade.plugin_foodindustrial.device", "食品工业：锅与被动设备状态", "Food Industrial: pot and passive device state");
        // M30 culture plate (I17)
        add("plate", "inoculate", "接种", "Inoculate");
        add("plate", "identify", "鉴定", "Identify");
        add("plate", "pick", "挑取", "Pick");
        add("plate", "discard", "弃去", "Discard");
        add("plate", "unidentified", "未鉴定菌落", "Unidentified colony");
        add("plate", "incubating", "培养中", "Incubating");
        add("plate", "identifying", "鉴定中", "Identifying");
        add("climate", "met", "达标", "Met");
        add("climate", "warm", "温暖", "Warm");
        add("climate", "humid", "湿润", "Humid");
        add("climate", "none", "未达标", "Not met");

        add("upgrades", "speed", "速率 ×%s", "Speed ×%s");
        add("upgrades", "energy", "能耗 ×%s", "Energy use ×%s");
        add("upgrades", "parallel", "并行 %s", "Parallel %s");
        add("upgrades", "normalizing", "规范化", "Standardization");
        add("upgrades", "adds", "增加能力：", "Adds:");

        add("tank", "amount", "%s / %s mB", "%s / %s mB");
        add("vent", "title", "排放：%s", "Venting: %s");
        add("vent", "off", "无行为", "Off");
        add("vent", "overflow", "排出溢出", "Vent overflow");
        add("vent", "all", "全部排出", "Vent all");
        add("vent", "gas_only", "只排气体，每 tick 最多 100 mB", "Gases only, at most 100 mB per tick");
        add("tank", "empty", "空", "Empty");
        add("tank", "too_tall", "储罐最多叠 8 层", "At most 8 tanks in a column");
        add("tank", "mixed", "相连的储罐只能装同种流体", "Stacked tanks hold one fluid");
        add("tank", "tartar", "酒石 %s", "Tartar %s");
        add("tank", "draining_on", "自动排出：开", "Draining: on");
        add("tank", "draining_off", "自动排出：关", "Draining: off");
        add("tank", "mode_place", "放置模式", "Place mode");
        add("tank", "mode_use", "使用模式", "Use mode");
        add("tank", "locked", "已锁定：%s", "Locked: %s");
        add("tank", "dump", "再点一次清空", "Click again to empty");
        // T0 tools and devices (I12, additives/07 §操作与实现规格)
        add("manual", "grind", "研磨 %s / %s", "Grinding %s / %s");
        add("manual", "filter", "过滤 %s / %s", "Filtering %s / %s");
        add("manual", "press", "拧压 %s / %s", "Pressing %s / %s");
        add("manual", "stir", "搅拌 %s / %s", "Stirring %s / %s");
        add("manual", "in_progress", "进行中：%s 步", "In progress: %s steps");
        add("manual", "done", "完成", "Done");
        add("manual", "no_recipe", "不能这样加工", "Cannot be processed this way");
        add("manual", "cannot_hold", "放不下", "Does not fit");
        add("manual", "not_heated", "下方没有点燃的营火", "No lit campfire below");
        add("manual", "sealed", "封口", "Sealed");
        add("manual", "open", "开口", "Open");
        add("manual", "progress", "进度 %s%%", "Progress %s%%");
        add("manual", "missing", "缺少：%s", "Needs: %s");
        add("manual", "separator", "、", ", ");
        add("manual", "sulfured", "硫熏", "Sulfured");
        add("slot", "locked", "已锁定：%s", "Locked: %s");
        add("slot", "uses", "剩余 %s 次", "%s uses left");
        add("slot", "saturated", "已饱和，用再生液再生", "Saturated; regenerate it");
        // slot names shown on hover (2026-11-11): the roles, then the machines' own words
        add("slot", "input", "输入", "Input");
        add("slot", "output", "输出", "Output");
        add("slot", "catalyst", "催化剂", "Catalyst");
        add("slot", "fuel", "燃料", "Fuel");
        add("slot", "upgrade", "升级", "Upgrade");
        add("slot", "module", "模块", "Module");
        add("slot", "culture", "菌种", "Culture");
        add("slot", "sample", "样本或菌种", "Sample or culture");
        add("slot", "medium", "培养基", "Medium");
        add("slot", "donor", "供体样本", "Donor sample");
        add("slot", "vector", "表达载体", "Expression vector");
        add("slot", "electrode", "电极", "Electrode");
        add("slot", "adsorbent", "吸附剂", "Adsorbent");
        add("slot", "membrane", "膜组件", "Membrane module");
        add("tank", "input", "输入储罐", "Input tank");
        add("tank", "output", "输出储罐", "Output tank");
        // what a T3/T4 machine lacks for a recipe (I16, I17)
        add("need", "trays", "塔板层数", "Trays");
        add("need", "regenerant", "再生液", "Regenerant");
        add("energy", "amount", "%s / %s FE", "%s / %s FE");
        add("energy", "manual", "人力：%s / %s s", "Crank power: %s / %s s");
        add("fuel", "burning", "燃烧：%s s", "Burning: %s s");
        add("fuel", "out", "未点燃", "Not lit");
        add("mode", "current", "模式：%s", "Mode: %s");

        add("structure", "main", "主页", "Main");
        add("structure", "tab", "结构", "Structure");
        add("structure", "formed", "已成形", "Formed");
        add("structure", "unformed", "未成形", "Incomplete");
        add("structure", "wrong", "%s：需要 %s", "%s: needs %s");
        add("structure", "more", "另有 %s 处", "%s more");
        add("structure", "highlight", "高亮", "Highlight");
        add("structure", "ports", "仓口：%s", "Ports: %s");
        add("structure", "needs_ports", "需要仓口：%s", "Needs ports: %s");
        add("structure", "layers", "层数：%s", "Layers: %s");
        add("port_need", "item_input", "输入物品仓", "Item input");
        add("port_need", "item_output", "输出物品仓", "Item output");
        add("port_need", "fluid_input", "输入流体仓", "Fluid input");
        add("port_need", "fluid_output", "输出流体仓", "Fluid output");
        add("port_need", "energy_input", "能量仓", "Energy");

        add("port", "mode", "方向：%s", "Direction: %s");
        add("port", "auto", "自动输出：%s", "Auto output: %s");
        add("port", "unattached", "未连接控制器", "No controller");

        add("machine", "configured", "带有设置", "Has saved settings");
        add("redprint", "recorded", "已记录：%s", "Recorded: %s");
        add("redprint", "copied", "已复制设置", "Settings copied");
        add("redprint", "pasted", "已粘贴设置", "Settings pasted");
        add("redprint", "mismatch", "机器类型不同", "Different machine");
        add("redprint", "empty", "红图为空", "Redprint is empty");
        add("redprint", "cleared", "红图已清空", "Redprint cleared");
    }
}
