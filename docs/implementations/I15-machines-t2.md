# I15 T2 机器（M11、M12、M14、M15、M17–M21）

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | D 设备 |
| 依赖 | I14、I29 |
| 原文档 | [additives/06 §T2 机械化](../additives/06-machines.md)、[machines/03 §3](../machines/03-io-layout.md)、[machines/01 §1.2、§4](../machines/01-recipe-types.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `machine.mechanical`、`machine.definition`、`machine.entity`、`machine.processing`、`platform.machine` |

## 目标

9 台 FE 机器可用，产出提升到精制，开始支持升级与模块。

## 范围

- 做：定义、模式、模块、升级槽
- 不做：配方内容；第四批新增的模式与模块（M14 真空、M20 氯化、M21 高剪切乳化头）在本计划做定义，配方随第四批内容（[additives/15 §机器](../additives/15-batch-4.md)，尚无实装计划）

## 任务

- [x] M11 粉碎机（框架样例转正）（[additives/06 §T2 机械化](../additives/06-machines.md)）
- [x] 升级物品（速率、能效、并行、规范化）与数值：先拟定写回 [machines/01 §1](../machines/01-recipe-types.md) 并与用户确认（2026-11-11 定：速率 ×1.5、能耗 ×2；能效 ×0.7；并行 +1）；`Upgrade.register` 登记效果；`RecipeProcessor` 按并行数一次处理 N 份（自 I09 移入，2026-11-04）
- [x] 规范化模块（2026-11-05 定）：通用升级，占 1 格，多个不叠加；`Upgrade.register` 给设备档案置“规范化”标志，`OutcomeResolver` 按 I29 的第 6 步处理；升级面板汇总显示“规范化”；Shift 描述“产出纯度降到所在档位的下限”（[machines/01 §1.2](../machines/01-recipe-types.md)，[machines/03 §1](../machines/03-io-layout.md)，[machines/04 §2](../machines/04-gui.md)）
- [x] 模块物品：`MachineModule.register` 登记能力与能耗（自 I09 移入）
- [x] 进气面（`atmosphere.air`）（[machines/03 §1](../machines/03-io-layout.md)；自 I09 移入；排放开关随 I11 做，2026-11-05）：单方块固定背面进气，多方块用进气口（2026-11-11 定）
- [x] M12 固液分离机：滤板组、离心转鼓两种模块（[machines/01 §4](../machines/01-recipe-types.md)）
- [x] M14 搅拌反应罐：常压反应、聚合模式（活性炭脱色是配方，不另设模式，见 06 草案）；耐腐蚀内衬、真空（与 M15 的真空模块同一物品，2026-11-05）、低温夹套、紫外灯模块；输入储罐有氮气时为惰性（[machines/01 §4.3](../machines/01-recipe-types.md)）
- [x] M15 蒸发结晶器：五种模式；真空、多效模块（[machines/01 §4.3](../machines/01-recipe-types.md)）
- [x] M17 干燥机：冷却辊、冷冻干燥模块；M18 萃取罐：超临界模块，顶面要冷凝器（[machines/01 §4.3](../machines/01-recipe-types.md)）
- [x] M19 吸收塔：铅室衬里、燃烧除氧，高 4–8 层（2026-11-11 定）；M20 回转窑：熔融、碱熔、氯化等七种模式（多方块）（[additives/06 §T2 机械化](../additives/06-machines.md)）
- [x] M21 调配机：均质、归一；高剪切乳化头（T2，2026-11-05）、密炼转子模块；装规范化模块时均质产出取档位下限（`MixerBlockEntity`）（[machines/02 §4](../machines/02-architecture.md)）
- [x] 配方不写能耗时按配方类型取默认值（2026-11-11 定，[machines/01 §2](../machines/01-recipe-types.md)）
- [x] 多方块控制器的储罐一律 16,000 mB（含 T1 三台，[machines/03 §2](../machines/03-io-layout.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 成员方块的邻居变化（进气口） | `neighborChanged(…, BlockPos fromPos, …)`，public | 同左，protected | `neighborChanged(…, Orientation, …)` |
| 其余 | 无（全部经 platform） | 无 | 无 |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] M11–M21 中的 9 台 每台至少跑通一条第一批配方，界面、侧面配置正常；AE2 推送是 I09、I10 的通用实现，在 I24 验证（同 I14）
- [x] 规范化：同一物料、同一档的两种纯度产出装模块后输出同一 byte；劣质与 K0 产物不变（[machines/01 §1.2](../machines/01-recipe-types.md)）
- [x] 性能：睡眠与运行状态符合 [machines/02 §6](../machines/02-architecture.md)（缺条件、缺空气、缺冷凝器、等待原料、输出满都睡眠；进气口、背面方块变化时唤醒）

## 实现记录

- 已定（2026-11-11，用户确认）：升级数值（速率 ×1.5、能耗 ×2；能效 ×0.7；并行 +1，效果相乘）；配方不写能耗时按配方类型取默认值；M19 高 4–8 层，每比 4 层多一层速率 +25%；单方块机器固定从背面进气。规格写入 [additives/06 §T2 机械化：操作与实现规格（I15）](../additives/06-machines.md)，默认能耗写入 [machines/01 §2](../machines/01-recipe-types.md)，进气写入 [machines/03 §1](../machines/03-io-layout.md)
- 代码：`machine.mechanical`（`MechanicalMachines` 九台机器、两种外壳、进气口、机械框架、四种升级、十三种模块；`MixerBlockEntity` 均质与归一；`MechanicalData` 模型、方块状态、掉落、挖掘标签与合成）；M11 从 `ModMachines` 移入
- 机器框架扩展：
  - 并行：`RecipeProcessor` 在匹配时按输入够的份数（最多 1 + 并行升级数）定上限，每次完成前取产物放得下的最大份数；消耗、产出（每份各自掷产率）、能耗、催化剂磨损都乘以份数；纯度按同一组输入算一次
  - 默认能耗：`ProcessType.defaultEnergy`，`DeviceProfile.energyPerTick` 在配方不写 `energy` 时取用（再乘档次与升级的能耗系数）
  - 进气：`MachineDefinition.airIntake`；单方块看背面相邻是否为空气方块，邻居变化、放置、载入时重算；多方块看结构内的进气口（`AirIntakeBlock`，可放在外壳位置），任一面朝结构外且相邻空气即可；变化时重新匹配并唤醒。有空气时机器带 `atmosphere.air`
  - 模式要求（`MachineMode.needing`）：气流干燥、燃烧除氧、煅烧、焙烧要空气，缺时状态“条件不满足：空气”并暂停，进度保留
  - 气体（`GasSupply`）：M14 输入储罐有至少 10 mB 氮气时带 `atmosphere.inert`；运转需要惰性的配方时每 40 tick 耗 10 mB
  - 可变高度结构：`StructurePattern.Builder.repeat(min, max)` 让一层重复；校验从最高的变体往下找第一个成形的，结果带层数；登记范围按最高变体，仓口推送和进气口按成形的高度判断结构内外；`MachineDefinition.heightBonus(4, 250)` 给出速率
  - 速率：`MachineBlockEntity.speedPermille` = 升级 × 结构（塔高）；界面“速率”与剩余时间用它
  - 机器自带方块实体类：`MachineDefinition.Builder.entity`（M21）
  - 界面：`ProcessingMachineBlockEntity.Job` 统一给出进度、时长、能耗（含并行份数）、产物；升级面板装规范化模块时显示“规范化”，两类同名的能力词（真空）只列一次；结构页显示“层数：n”（可变高度时）
  - `Purity.itemOf`（物料的本模组物品）、`Purity.isOwn(Item)`（M21 归一用）
- 工艺条件词表新增“铅室”“乳化”与 22 个模式词（[text-standard §5](../text-standard.md)）
- platform：`machine.PlatformMemberBlock` 新增邻居变化钩子 `onNeighborChanged`（进气口）
- 机器（ID、槽位、相位、能力、模式、模块）：
  - M11 `crusher` 粉碎机：I1 O2 U2，相位 0，R01
  - M12 `solid_liquid_separator` 固液分离机：I1 O2 FI1 FO2 U2 X2，相位 0，R02、R03；滤板组（过滤）、离心转鼓（离心）
  - M14 `stirred_reactor` 搅拌反应罐：I3 O2 FI3 FO2 C1 U2 X3，相位 3，R11，加热、冷却，背面进气，氮气惰性；常压反应 / 聚合；耐腐蚀内衬、真空泵、低温夹套、紫外灯
  - M15 `evaporating_crystallizer` 蒸发结晶器：I1 O2 FI2 FO2 U2 X2，相位 2，R05、R06；浓缩（R05，加热）/ 蒸发结晶（R06，加热）/ 冷却结晶（R06，冷却）/ 等电点（R06）/ 冷冻（R06，冷冻）；真空泵、多效蒸发器
  - M17 `dryer` 干燥机：I2 O2 FI1 U2 X2，相位 2，R07，加热，背面进气；滚筒 / 气流（要空气）；冷却辊、冷冻干燥单元
  - M18 `extraction_tank` 萃取罐：I2 O2 FI2 FO2 U2 X1，相位 0，R04，溶剂回收、加热，顶面要冷凝器；溶剂浸提 / 液液萃取；超临界单元
  - M19 `absorption_tower` 吸收塔：I1 O1 FI2 FO2 U2 X1，相位 0，R12，多方块 3 × 3 × 4–8，塔壁，进气口；吸收 / 碳化 / 燃烧除氧（要空气）；铅室衬里
  - M20 `rotary_kiln` 回转窑：I3 O3 FO1 U2 X1，相位 1，R09，加热、高温，多方块 3 × 3 × 5 筒，窑筒壳，进气口；煅烧、焙烧（要空气）/ 缩合 / 熔融 / 碱熔 / 活化 / 氯化
  - M21 `mixer` 调配机：I6 O2 FI2 FO1 U2 X1，相位 0，R19；配料 / 均质 / 归一；高剪切乳化头、密炼转子
- 验证：三个节点 `compileJava`、`runData`、`test` 通过（每个节点 84 项单元测试，新增模式要求、可变高度结构、默认能耗 3 项）。三个节点用开发命令实测（开发测试配方 `i15_*` 放在测试世界的数据包里，不随模组发布），三个节点结果一致（只有产率的随机多出不同）：
  - 粉碎机：石膏矿 ×3 + 两个并行升级，一次做 3 份，每批 96 FE（8 FE/t × 4 × 3），出石膏 ×3；两个速率升级：速率 2.25、每批 128 FE，200 tick 的配方 12 批完成
  - 固液分离机：胶乳凝块压成湿胶片；石花菜煮液没有滤板组时“缺少：过滤”，装上后出琼脂冻 ×2
  - 搅拌反应罐：惰性测试配方没有氮气时“缺少：惰性”，加氮气后运转，100 tick 耗 20 mB；背面放石头后失去“空气”，拿走恢复
  - 蒸发结晶器：浓缩模式不做重结晶，切到蒸发结晶后盐 95% ×4 + 水 → 盐 98.4% ×2，盐 98% → 99.3%；装规范化模块后两者都出 97.1%；真空浓缩测试配方没有真空泵时“缺少：真空”，装上后出盐
  - 干燥机：滚筒模式苹果 → 苹果干；气流模式背面堵住时“条件不满足：空气”，拿走后继续
  - 萃取罐：没有冷凝器时“条件不满足：冷凝器”，放上后盐 + 水 → 卤水 1,100 mB（产率 ×1.1）
  - 调配机：配料测试配方；乳化测试配方要高剪切乳化头；均质盐 90% ×10、99% ×30、95% ×40（夹着一组蔗糖）→ 取 64 个合成盐 95.9% ×64，余 16 个；盐 98% ×10 均质后 98.0%，装规范化模块后 97.1%；归一糖 → 蔗糖 34%（外部默认值），装一个并行升级后每次换 2 个
  - 吸收塔：4 层成形（速率 1.0），加到 6 层后速率 1.5、结构页“层数：6”；吸收模式水 + 二氧化碳 → 测试产物；燃烧除氧模式煤 → 二氧化碳，进气口外侧堵住时“条件不满足：空气”，拿走后继续
  - 回转窑：没有进气口时煅烧模式“条件不满足：空气”，装上进气口后石膏 → 熟石膏 82%（40 FE/t 默认能耗）；进气口外侧堵住后停，切到熔融模式继续
  - 1.20.1 客户端：搅拌反应罐、调配机界面（3 输入 3 储罐 + 催化剂、6 输入）正常；升级面板“规范化”“增加能力：耐腐蚀、真空”；吸收塔控制器结构页“已成形、层数：6、仓口：5”
- 完成后用户确认（2026-11-11）：
  - 四个要空气的模式缺空气时停机；真空泵同时给 `pressure.vacuum` 与 `method.vacuum`；聚合模式 T2 即可切换；M21 均质 64 个不受并行影响、归一按并行多换、规范化对均质与归一都生效；升级每组 16、模块每组 1；M20 筒两端开口、留一个空模块槽；“黏土”取黏土球（[additives/06 §T2 机械化：操作与实现规格（I15）](../additives/06-machines.md) 实现补充）
  - 名称与描述（九台机器、外壳、进气口、机械框架、升级、模块的中英文名与 Shift 描述；模式词与“铅室”“乳化”的英文）
  - 合成摆法、占位贴图
  - 多方块储罐一律 16,000 mB，T1 三台也随之改为 16,000

## 待定

- T2 动力是否兼容应力或蒸汽（[additives/11 §待定问题 3](../additives/11-roadmap.md)）
