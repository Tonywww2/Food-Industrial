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
        add("panel", "io", "输入输出", "Input and output");
        add("panel", "redstone", "红石", "Redstone");

        add("status", "running", "运行中", "Running");
        add("status", "waiting", "等待原料", "Waiting for inputs");
        add("status", "output_full", "输出已满", "Output full");
        add("status", "no_power", "能量不足", "Not enough energy");
        add("status", "no_fuel", "燃料不足", "Not enough fuel");
        add("status", "missing", "条件不满足：%s", "Missing: %s");
        add("status", "catalyst", "催化剂", "Catalyst");
        add("status", "redstone", "红石禁止", "Stopped by redstone");
        add("status", "unformed", "结构未成形", "Structure incomplete");

        add("io", "off", "关闭", "Off");
        add("io", "input", "输入", "Input");
        add("io", "output", "输出", "Output");
        add("io", "both", "输入 + 输出", "Input + output");
        add("io", "items", "物品：%s", "Items: %s");
        add("io", "fluids", "流体：%s", "Fluids: %s");
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
        add("io", "scope_items", "物品输出范围", "Item output");
        add("io", "scope_fluid_out", "流体输出范围", "Fluid output");
        add("io", "scope_fluid_in", "流体输入范围", "Fluid input");
        add("io", "scope_all", "全部", "All");
        add("io", "scope_main", "仅主产物", "Main product only");
        add("io", "scope_byproduct", "仅副产物", "Byproducts only");
        add("io", "scope_tank", "储罐 %s", "Tank %s");
        add("io", "back_to_faces", "返回", "Back");

        add("redstone", "ignore", "忽略", "Ignore");
        add("redstone", "low", "低电平运行", "Run on low signal");
        add("redstone", "high", "高电平运行", "Run on high signal");
        add("redstone", "disabled", "禁用", "Disabled");
        add("redstone", "powered", "当前信号：有", "Signal: on");
        add("redstone", "unpowered", "当前信号：无", "Signal: off");

        add("info", "recipe", "配方：%s", "Recipe: %s");
        add("info", "none", "无", "None");
        add("info", "progress", "进度：%s / %s tick", "Progress: %s / %s tick");
        add("info", "remaining", "剩余：%s s", "Remaining: %s s");
        add("info", "speed", "速率：×%s", "Speed: ×%s");
        add("info", "energy", "能耗：%s FE/t", "Energy use: %s FE/t");
        add("info", "purity", "产出纯度：%s", "Output purity: %s");
        add("info", "yield", "产率：%s%%", "Yield: %s%%");
        add("info", "capabilities", "能力：", "Capabilities:");

        add("upgrades", "speed", "速率 ×%s", "Speed ×%s");
        add("upgrades", "energy", "能耗 ×%s", "Energy use ×%s");
        add("upgrades", "parallel", "并行 %s", "Parallel %s");
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
        add("tank", "draining_on", "自动排出：开", "Draining: on");
        add("tank", "draining_off", "自动排出：关", "Draining: off");
        add("tank", "mode_place", "放置模式", "Place mode");
        add("tank", "mode_use", "使用模式", "Use mode");
        add("tank", "locked", "已锁定：%s", "Locked: %s");
        add("tank", "dump", "再点一次清空", "Click again to empty");
        add("slot", "locked", "已锁定：%s", "Locked: %s");
        add("slot", "uses", "剩余 %s 次", "%s uses left");
        add("energy", "amount", "%s / %s FE", "%s / %s FE");
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
