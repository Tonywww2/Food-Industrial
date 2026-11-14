# I16 T3 化工机器（M22–M28）

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | D 设备 |
| 依赖 | I15 |
| 原文档 | [additives/06 §T3 化工](../additives/06-machines.md)、[machines/03 §3](../machines/03-io-layout.md)、[machines/01 §4.3](../machines/01-recipe-types.md)、[additives/15 §机器](../additives/15-batch-4.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `machine.chemical`、`machine.entity`、`machine.processing`、`recipe` |

## 目标

7 台化工机器可用，产出提升到食品级，石化与电化学线打通。

## 范围

- 做：定义、多方块、催化剂槽；第四批新增的模式（2026-11-05）
- 不做：配方内容（第四批模式的配方随第四批内容，尚无实装计划）

## 任务

- [x] M22 精馏塔：层数 = 馏分数（塔板 2–6 层，配方的流体产物数不能多于塔板数）；常压、减压、深冷、汽提四种模式，分子蒸馏模块（T4，合成随 I17）（[additives/06 §T3 化工](../additives/06-machines.md)）
- [x] M23 高压反应釜：催化剂槽，加氢、羰基化、合成氨、加压氧化、环氧化、乙氧基化六种模式；加压蒸煮、水热合成用现有加压档，不另设模式（2026-11-05）（[additives/06 §T3 化工](../additives/06-machines.md)）
- [x] M24 电解槽：电极（石墨电极）放催化剂槽，三路流体输出；氯碱、电解还原、电解氧化、电解水、熔盐五种模式（熔盐为高温，2026-11-05）（[machines/03 §3](../machines/03-io-layout.md)）
- [x] M25 吸附柱：吸附剂饱和后自动再生（2026-11-11 用户定）；吸附、分级两种模式（[additives/06 §T3 化工](../additives/06-machines.md)）
- [x] M26 电弧炉、M27 气相反应炉（顶上 1–4 段炉管）、M28 喷雾干燥塔：多方块；M27 四种模式：燃烧、气相催化（原“催化氧化”）、裂解、流化床（2026-11-05）；裂解产率按炉管段数（[machines/01 §4.3](../machines/01-recipe-types.md)）
- [x] 化工框架、化工塔壁、耐火砖、炉管、石墨电极；T3 模块（低温夹套、多效蒸发器、密炼转子）的合成

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 差异 | 无（全部经 platform） | 无 | 无 |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] M22–M28 每台至少跑通一条配方（第一批没有 R10、R11、R13、R14、R15 的现成配方，用开发测试配方；R07、R09 用 07 配方的同类测试），界面、侧面配置正常；AE2 推送在 I24 验证（同 I14、I15）
- [x] 性能：睡眠与运行状态符合 [machines/02 §6](../machines/02-architecture.md)（缺塔板、缺再生液、等待原料都睡眠；结构高度变化时重新匹配）

## 实现记录

- 已定（2026-11-11，用户确认）：M25 的吸附剂饱和后自动再生。其余规格由实现方拟定，写入 [additives/06 §T3 化工：操作与实现规格（I16）](../additives/06-machines.md)
- 代码：`machine.chemical`（`ChemicalMachines` 七台机器、化工塔壁、耐火砖、炉管、化工框架、石墨电极、分子蒸馏模块；`DistillationColumnBlockEntity`、`AdsorptionColumnBlockEntity`、`GasPhaseReactorBlockEntity`、`CrackingYield`；`ChemicalData` 模型、方块状态、掉落、挖掘标签与合成）
- 机器框架扩展（I17 共用）：
  - `MachineBlockEntity` 的钩子：`offers`（不做的配方静默跳过，M25）、`recipeLacking`（缺的不是能力时的提示，M22 塔板）、`structureParallel`（结构给的并行份数，M29）、`setupTicks`（每批前的准备时长，M29 在位灭菌）、`outputPermille`（产物数量系数，M27 裂解、M29 染菌）、`completing` / `completed`（完成前后）、`wearCatalyst`、`catalystEmptied`（催化剂槽空了计数清零，原来没做）、`catalystSaturated`、`extraCapabilities`
  - `RecipeProcessor` 用这些钩子；产物数量系数不是整数时按概率多出 1；缺的条件里可以带 `@` 开头的提示键（“条件不满足：塔板层数”）
  - 多方块控制器可用自己的方块实体类（`MachineDefinition.entity`）；结构层数变化时重新匹配配方
  - `MachineStatus` 增加“在位灭菌”“等待接种”“平板待挑取”（I17 用）
- 工艺条件词表新增 22 个模式词（“再生”只用于再生配方）与“分子蒸馏”（[text-standard §5](../text-standard.md)）
- 机器（ID、槽位、相位、能力、模式）：
  - M22 `distillation_column` 精馏塔：O1 FI2 FO6 U3 X1，相位 1，加热、多馏分；常压精馏 / 减压精馏（真空）/ 深冷分离（冷冻）/ 汽提；3 × 3 塔 4–8 层，化工塔壁
  - M23 `high_pressure_reactor` 高压反应釜：I2 O2 FI3 FO2 C1 U3，相位 3，加热、高温、加压、高压；六种模式
  - M24 `electrolyzer` 电解槽：I1 O1 FI2 FO3 C1 U3，相位 2；氯碱 / 电解还原 / 电解氧化 / 电解水 / 熔盐电解（高温）
  - M25 `adsorption_column` 吸附柱：FI2 FO2 C1 U3，相位 2，离子交换；吸附 / 分级；饱和后自动再生
  - M26 `electric_arc_furnace` 电弧炉：I3 O2 FO2 C1 U3，相位 1，加热、高温、极高温；3 × 3 × 3，耐火砖
  - M27 `gas_phase_reactor` 气相反应炉：I1 O1 FI3 FO3 C1 U3，相位 1，加热、高温，进气口；燃烧 / 气相催化 / 裂解 / 流化床；3 × 3 × 3 炉膛（耐火砖）+ 顶上 1–4 段炉管
  - M28 `spray_dryer` 喷雾干燥塔：I1 O1 FI1 U3，相位 2，加热、热敏、极热敏；3 × 3 × 4，化工塔壁
- 验证：三个节点 `compileJava`、`runData`、`test` 通过（每个节点 87 项单元测试，I16、I17 新增裂解产率、菌落外观、平板权重 3 项）。三个节点用开发命令实测（开发测试配方 `i16_*`、`i17_*` 放在测试世界的数据包里），结果一致（只有随机的产率与菌落不同）：
  - 精馏塔：两层塔板做三馏分的配方时“条件不满足：塔板层数”，加一层后运转，果酒 → 乙醇 200、蒸馏水 600、乙酸 100 mB 分进三个输出储罐
  - 高压反应釜：毛豆油 + 氢气 + 镍催化剂（加氢模式、高压）运转，催化剂计数 1
  - 电解槽：没有电极时等待；放石墨电极后盐水 → 氢气、氯气、蒸馏水
  - 吸附柱：活性炭用两次后饱和，状态“条件不满足：再生液”；加水后自动跑再生配方（出再生废液），计数清零，接着吸附
  - 电弧炉：极高温测试配方带电极运转
  - 气相反应炉：1 段炉管时乙醇裂解出乙烯 700 mB（×0.7），加到 4 段后每次 1,000 mB
  - 喷雾干燥塔：热敏测试配方运转

- 完成后用户确认（2026-11-11，“全部批准”）：
  - 规格中由实现方拟定的部分（[additives/06 §T3 化工：操作与实现规格（I16）](../additives/06-machines.md)）：各机器的模式、能力、相位；M22 塔板数限制馏分数；M23 加压蒸煮与水热合成不写模式；M25 再生配方写 `"mode": "regeneration"`；M27 裂解产率 ×0.7 起每段 +0.1、可装进气口；M26、M27、M28 的结构；M22 与 M28 共用化工塔壁，M26 与 M27 共用耐火砖
  - 名称与描述（Distillation Column、High-Pressure Reactor、Electrolytic Cell、Adsorption Column、Electric Arc Furnace、Gas-Phase Reactor、Spray Drying Tower、Chemical Frame、Chemical Tower Wall、Refractory Bricks、Furnace Tube、Graphite Electrode、Molecular Distillation Unit；模式词与“分子蒸馏”的英文）
  - 合成配方、占位贴图；石墨电极的物品标签 `foodindustrial:catalysts/electrode`

## 待定

- 贵金属与过渡金属催化剂来源（[additives/11 §待定问题 4](../additives/11-roadmap.md)）
