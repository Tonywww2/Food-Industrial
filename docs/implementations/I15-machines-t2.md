# I15 T2 机器（M11、M12、M14、M15、M17–M21）

| 项 | 内容 |
| --- | --- |
| 状态 | 未开始 |
| 阶段 | D 设备 |
| 依赖 | I14、I29 |
| 原文档 | [additives/06 §T2 机械化](../additives/06-machines.md)、[machines/03 §3](../machines/03-io-layout.md)、[machines/01 §1.2、§4](../machines/01-recipe-types.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `machine.mechanical` |

## 目标

9 台 FE 机器可用，产出提升到精制，开始支持升级与模块。

## 范围

- 做：定义、模式、模块、升级槽
- 不做：配方内容；第四批新增的模式与模块（M14 真空、M20 氯化、M21 高剪切乳化头）在本计划做定义，配方随第四批内容（[additives/15 §机器](../additives/15-batch-4.md)，尚无实装计划）

## 任务

- [ ] M11 粉碎机（框架样例转正）（[additives/06 §T2 机械化](../additives/06-machines.md)）
- [ ] 升级物品（速率、能效、并行、规范化）与数值：先拟定写回 [machines/01 §1](../machines/01-recipe-types.md) 并与用户确认；`Upgrade.register` 登记效果；`RecipeProcessor` 按并行数一次处理 N 份（自 I09 移入，2026-11-04）
- [ ] 规范化模块（2026-11-05 定）：通用升级，占 1 格，多个不叠加；`Upgrade.register` 给设备档案置“规范化”标志，`OutcomeResolver` 按 I29 的第 6 步处理；升级面板汇总显示“规范化”；Shift 描述“产出纯度降到所在档位的下限”（[machines/01 §1.2](../machines/01-recipe-types.md)，[machines/03 §1](../machines/03-io-layout.md)，[machines/04 §2](../machines/04-gui.md)）
- [ ] 模块物品：`MachineModule.register` 登记能力与能耗（自 I09 移入）
- [ ] 进气面（`atmosphere.air`）（[machines/03 §1](../machines/03-io-layout.md)；自 I09 移入；排放开关随 I11 做，2026-11-05）
- [ ] M12 固液分离机：滤板组、离心转鼓两种模块（[machines/01 §4](../machines/01-recipe-types.md)）
- [ ] M14 搅拌反应罐：常压反应、活性炭脱色、聚合模式；耐腐蚀内衬、真空（与 M15 的真空模块同一物品，2026-11-05）、低温夹套、紫外灯模块（[machines/01 §4.3](../machines/01-recipe-types.md)）
- [ ] M15 蒸发结晶器：五种模式；真空、多效模块（[machines/01 §4.3](../machines/01-recipe-types.md)）
- [ ] M17 干燥机：冷却辊、冷冻干燥模块；M18 萃取罐：超临界模块（[machines/01 §4.3](../machines/01-recipe-types.md)）
- [ ] M19 吸收塔：铅室衬里、燃烧除氧；M20 回转窑：熔融、碱熔、氯化模式（T3，通氯气焙烧，2026-11-05）（多方块）（[additives/06 §T2 机械化](../additives/06-machines.md)）
- [ ] M21 调配机：均质、归一；高剪切乳化头（T2，2026-11-05）、密炼转子模块；装规范化模块时均质产出取档位下限（`MixerBlockEntity`）（[machines/02 §4](../machines/02-architecture.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 差异 | 无 | 无 | 无 |

## 验收

- [ ] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [ ] M11–M21 中的 9 台 每台至少跑通一条第一批配方，界面、侧面配置、AE2 推送正常
- [ ] 规范化：同一物料、同一档的两种纯度产出装模块后输出同一 byte；劣质与 K0 产物不变（[machines/01 §1.2](../machines/01-recipe-types.md)）
- [ ] 性能：睡眠与运行状态符合 [machines/02 §6](../machines/02-architecture.md)

## 待定

- T2 动力是否兼容应力或蒸汽（[additives/11 §待定问题 3](../additives/11-roadmap.md)）
