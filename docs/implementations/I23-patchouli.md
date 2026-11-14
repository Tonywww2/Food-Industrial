# I23 Patchouli 手册与多方块预览

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | F 联动与验证 |
| 依赖 | I14、I04 |
| 原文档 | [additives/10 §5 多方块](../additives/10-purity-jei-ae2.md)、[additives/07 §历史原型速览](../additives/07-manual-crafting.md)、[text-standard §1](../text-standard.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `platform.compat.patchouli`、手册资源 |

## 目标

一本按阶段组织的手册，介绍工艺与历史原型，并能预览 10 台多方块的结构。

## 范围

- 做：手册结构、多方块预览、条目文本

## 任务

- [x] 手册分 T0–T4 五章，每台机器与工具一个条目（[additives/06](../additives/06-machines.md)，[additives/07](../additives/07-manual-crafting.md)）
- [x] 10 台多方块的结构预览（M06、M07、M10、M19、M20、M22、M26、M27、M28、M29）（[machines/02 §4](../machines/02-architecture.md)）
- [x] 历史原型条目（[additives/07 §历史原型速览](../additives/07-manual-crafting.md)）
- [x] 文本用完整句，遵循文本规范（[text-standard §1](../text-standard.md)）
- [x] 手册可合成：书 + 小麦（无序），不自动给（2026-11-11，用户确认）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| Patchouli | 1.20.1-84-FORGE | 1.21.1-93-NEOFORGE | 版本 ID `2CsnFLom` |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 三个节点手册可打开，多方块预览与实际结构一致

## 实现记录

- 代码：`book.ManualBook`（手册物品 `foodindustrial:manual`、合成配方、全部条目与文本，数据生成时写出 Patchouli 的 `book.json`、分类与条目文件），`platform.compat.patchouli.PatchouliBooks`（打开手册；未装 Patchouli 时右键只在动作栏提示“需要安装 Patchouli 才能阅读”）
- 手册用 Patchouli 的 i18n 模式：条目文件只写翻译键，文本进 `zh_cn` / `en_us` 语言文件，经文本规范检查（检查时去掉 `$(…)` 格式码）
- 结构：首页 + 6 个分类——手工与世界（15 条：H01–H09 工具、原版设备、海水、硝土、割取等）、作坊（10）、工业（9）、化工（7）、生物与精细（3）、历史原型（14）；机器条目写所属阶段、做什么、动力、模式，配一页物品展示
- 手册只写阶段，不写 T 级，也不用“档次”一词：分类名、首页简介与条目里都写阶段名，阶段名用对应档次的颜色（text-standard §3.4，2026-11-11 用户要求）
- 历史原型条目分两行：先写历史来源，换行再写游戏里的做法（2026-11-11 用户要求）
- 复查（1.20.1、26.1.2 客户端，2026-11-11）：首页按阶段介绍、阶段名带颜色；石磨条目写“作坊阶段的机器”；历史条目“海带”的游戏做法另起一行；生成的手册文件与语言文件中没有 T 级和“档次”
- 10 台多方块（M06、M07、M10、M19、M20、M22、M26、M27、M28、M29）各有一页立体预览，由机器的 `StructurePattern` 在数据生成时转成 Patchouli 多方块，与成形检查、JEI 结构页同源
- 手册由书 + 小麦无序合成，放在“机器”创造标签页
- 副标题：Patchouli 默认显示“1st 版”，改为不写版本号、副标题用模组名“食品工业”（2026-11-11）
- 历史条目“酒石”原写“木桶”，与实现（木桶罐）不符，改为木桶罐
- 验证：三个节点编译与数据生成通过；1.20.1、1.21.1、26.1.2 游戏内打开手册，首页、分类、条目与多方块预览正常

## 待审（实现时拟定，请确认）

- 无。已确认（2026-11-11，用户）：物品名“工艺手册 / Process Handbook”与副标题、全部条目文本、手册图标沿用原版书的贴图

## 待定

- 无。已定（2026-11-11，用户确认）：手册由书 + 小麦无序合成，不在进入世界时自动给
