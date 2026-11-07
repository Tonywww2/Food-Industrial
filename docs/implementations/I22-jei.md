# I22 JEI 集成

| 项 | 内容 |
| --- | --- |
| 状态 | 未开始 |
| 阶段 | F 联动与验证 |
| 依赖 | I08、I13、I14 |
| 原文档 | [additives/10 §5](../additives/10-purity-jei-ae2.md)、[machines/01 §6](../machines/01-recipe-types.md)、[text-standard §4–5](../text-standard.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `platform.compat.jei` |

## 目标

每种配方类型一个 JEI 类别，所有设备为催化剂，纯度规则、条件、设备差异一目了然。

## 范围

- 做：类别、催化剂、设备行与设备切换、条件图标、转移处理器、隐藏桥接配方、多方块结构页

## 任务

- [ ] 21 个配方类别，类别键 `jei.foodindustrial.category.<配方类型 ID>`（[machines/01 §6](../machines/01-recipe-types.md)，[text-standard §4](../text-standard.md)）
- [ ] 催化剂：机器、手工工具、被动设备、原版设备（[machines/01 §5](../machines/01-recipe-types.md)）
- [ ] 配方页的设备行（耗时、纯度、产率）与设备切换，转移到 AE2 样板终端时按所选设备编码（[machines/01 §6](../machines/01-recipe-types.md)）
- [ ] 纯度规则的显示：来源、除杂、保持、劣化，有上限时加“最高”；输出槽按门槛输入与所选设备算出结果；不带纯度的产物不显示纯度规则（2026-11-05，[additives/10 §1.1](../additives/10-purity-jei-ae2.md)）（[additives/10 §5 显示纯度产出](../additives/10-purity-jei-ae2.md)）
- [ ] 条件图标与词表（[text-standard §5](../text-standard.md)）
- [ ] 自定义转移处理器，逐槽用 `Ingredient.test` 选料（[additives/10 §5 一键转移](../additives/10-purity-jei-ae2.md)）
- [ ] 从原版熔炉、烟熏炉、高炉、营火类别中隐藏桥接配方（[additives/10 §5 原版设备](../additives/10-purity-jei-ae2.md)）
- [ ] 锁定槽的 JEI 拖入（自 I10 移入，2026-11-05；[machines/04 §1](../machines/04-gui.md)）
- [ ] 机器界面点击进度箭头打开该机器可做的配方类型的 JEI 页（自 I10 移入，2026-11-05；[machines/04 §1](../machines/04-gui.md)）
- [ ] 机器界面的侧面板（含展开后的面板）登记为 JEI 额外区域（`PlatformScreen.extraAreas()`），JEI 侧栏让开这些面板（I10 发现，2026-11-05 用户确认；[machines/04 §2](../machines/04-gui.md)）
- [ ] 多方块结构页：每台多方块机器一页，画出结构、标出可换成仓口的位置、列出所需方块与数量，控制器为催化剂（2026-11-05 定；[additives/10 §5 多方块](../additives/10-purity-jei-ae2.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| JEI 版本 | 15.2.0.25 | 19.39.0.372 | 29.53.0.115 |

## 验收

- [ ] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [ ] [additives/10 §8](../additives/10-purity-jei-ae2.md) 中 JEI 条目全部勾选

## 待定

- 多方块结构页的展示方式：可旋转的立体图，还是逐层切换（开始 I22 时问用户）
