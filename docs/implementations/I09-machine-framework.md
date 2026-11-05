# I09 机器框架

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | C 框架 |
| 依赖 | I01、I08 |
| 原文档 | [machines/02 §2、§4–6](../machines/02-architecture.md)、[machines/03 §1–3](../machines/03-io-layout.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `machine.*`、`platform.machine` |

## 目标

用一个 `MachineDefinition` 就能做出一台能运行配方、能被自动化、满足性能规范的机器（单方块与多方块）。

## 范围

- 做：组件、机器定义、BlockEntity 层次、处理状态机、调度、睡眠唤醒、多方块框架
- 不做：界面（I10）
- 不做：具体机器（I14–I17）

## 任务

- [x] 组件：`MachineInventory`（I、O、C、F、U、X 角色）、`TankSet`（液体 / 气体 / 锁定）、`EnergyBuffer`、`HeatSource`、`SideConfig`、`RedstoneControl`、`UpgradeSet`、`ModuleSet`、`CatalystSlot`（产率改为概率产出，不设累加器，2026-11-04 定；[machines/02 §4.1](../machines/02-architecture.md)，[machines/03 §1–2](../machines/03-io-layout.md)）
- [x] `MachineDefinition` 构建器：一处声明生成方块、物品、BlockEntity 类型、语言键、设备登记与数据生成条目；菜单移到 I10（2026-11-04）（[machines/02 §4.2](../machines/02-architecture.md)）
- [x] BlockEntity 层次：`FIBlockEntity` → `MachineBlockEntity` → `ProcessingMachineBlockEntity` → `MultiblockControllerBlockEntity`；`PortBlockEntity`（[machines/02 §4](../machines/02-architecture.md)）
- [x] `RecipeProcessor` 状态机与每批流程（[machines/02 §5](../machines/02-architecture.md)）
- [x] 调度：4 tick 一批、相位表、T0 被动设备 20 tick 一批（[machines/02 §6.1](../machines/02-architecture.md)）
- [x] 睡眠与事件唤醒；自动输入输出与退避；能力缓存；热路径禁止分配（[machines/02 §6.2–6.3](../machines/02-architecture.md)）
- [x] 多方块：结构图样、仓口登记、成员方块事件触发校验；成员只用本模组方块（2026-11-04 定）（[machines/02 §6.3 第 12 条](../machines/02-architecture.md)，[machines/04 §5](../machines/04-gui.md)）
- [x] 方块状态 `active` 去抖；配置的读写与“是否默认”（写进物品、放下恢复随 I10 配置器做）（[machines/02 §6.3 第 11 条](../machines/02-architecture.md)，[machines/04 §4](../machines/04-gui.md)）
- [x] 用 M11 粉碎机做端到端样例（定义 → 运行配方 → 漏斗进出）
- [x] `MachineDefinition`、模式、模块不引用食品概念（[machines/02 §8](../machines/02-architecture.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 能力暴露 | 经 `PlatformMachineCapabilities` | 同左 | 同左 |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] [machines/02 §6](../machines/02-architecture.md) 第 1–12 条逐条有对应实现或测试
- [x] M11 样例在三个节点上运行，睡眠时单台开销接近空 ticker

## 实现记录

- 代码：`machine.definition`（`MachineDefinition`、`IoLayout`、`MachineMode`、`MachineModule`、`Upgrade`、`PowerKind`、`TickPhase`、`StructurePattern`）、`machine.component`（`MachineInventory`、`TankSet`、`EnergyBuffer`、`HeatSource`、`SideConfig`、`RedstoneControl`、`UpgradeSet`、`CatalystCounters`、`Backoff`、`SlotRole`）、`machine.processing`（`RecipeProcessor`、`MachineStatus`）、`machine.block`（`MachineBlock`、`CasingBlock`、`PortBlock`）、`machine.entity`（`FIBlockEntity`、`MachineBlockEntity`、`ProcessingMachineBlockEntity`、`MultiblockControllerBlockEntity`、`PortBlockEntity`）、`machine.ModMachines`、`MultiblockRegistry`、`MachinePerf`、`MachineData`；配方新增 `catalyst` 字段（`recipe.Catalyst`，2026-11-04 定）
- platform 新增：`PlatformMemberBlock`（放置、移除钩子，26.1 用 `affectNeighborsAfterRemoval`）、`PlatformFuel`（原版熔炉燃烧时间与燃烧后剩下的容器）、`PlatformEntityBlock` 的邻居方块更新钩子与 `ticksOnServer`、`PlatformBlockEntity.onLoaded / onRemoved / onNeighborBlockChanged / onNeighborStorageChanged`；`FluidTanks.joins / joined`：本模组储罐接收同种流体时按 mB 加权合并纯度（[additives/10 §2.2 第 4 条](../additives/10-purity-jei-ae2.md)）
- 第 6 节逐条：1 4 tick 一批（`TickPhase.BATCH`，时长按 4 的倍数结算）；2 相位表（`TickPhase`，ticker 第一行比较相位）；3 被动设备 20 tick（`TickPhase.PASSIVE_BATCH`，随 I12）；4 仓口不 tick（`PortBlock.ticksOnServer`）；5、6 睡眠与事件唤醒（输入、输出、燃料、升级、能量、红石、邻居存储变化）；7 自动输入输出每批最多一次、退避 4 → 80（`Backoff`）；8 输入变化才匹配，按第一输入查索引；9 邻居能力走 `PlatformNeighborCache`；10 未完成的批次不分配对象，进度与能量只标记保存（`markUnsaved`），内容变化才通知邻居；11 `active` 立即开、空闲 40 tick 才关；12 多方块按成员事件校验
- 性能（2026-11-04，1.20.1 服务端）：200 台运行中的粉碎机预热后平均 0.96 µs/批（每 tick 0.048 ms，按比例 500 台约 0.12 ms/tick）；睡眠的机器每批只做相位与睡眠两次判断就返回。`/foodindustrial perf start|stop|report`（开发环境）
- T1 燃料按原版熔炉：有配方可做才点新燃料，点着后烧完；燃烧时间取原版值，熔岩桶留下桶（2026-11-04 定）
- M11 粉碎机作为样例注册（T2、FE、I 1 / O 2 / U 2、相位 0），占位贴图由 `tools/machines/placeholder_textures.py` 生成；英文名 Crusher 与仓口的 Item / Fluid / Energy Port 待审
- 通用仓口：物品仓、流体仓、能量仓；方向为输入 / 输出 / 关闭，可开自动输出（由控制器在批次中推送，不推回结构内）
- 未做、已移交：菜单、拆下写物品（I10）；升级与模块物品及数值、并行、进气面、满时排放（I15）；可变尺寸结构图样与各多方块的外壳（I14 起随设计）；`Device` 接口与 `ManualProcess`（I12，机器侧用 `DeviceSpec`）
- 运行验证（2026-11-04，三个节点 `runServer`）：粉碎机由漏斗从顶面输入、底面漏斗取出，石膏 88%（T2，基准 T0）、产率按概率多出；红石“高电平运行”无信号时停、有信号时运行；测试多方块（3×3×3 测试外壳 + 仓口）成形、拆一块外壳即不成形、放回再成形，仓口随之接入与断开；通过物品仓输入、另一个物品仓自动输出到箱子（T3 得 90.6%）；燃料测试机无燃料时“能量不足”，放入煤炭后运行，海盐 83% → 92.6%（T1），停工后照烧；重启后内容、能量、燃烧、仓口配置与多方块成形状态都保留；26.1 客户端渲染粉碎机与仓口模型正常
- 单元测试：面映射、默认侧面、退避、催化剂计数、红石、槽位模板（三个节点 54 项通过）

## 待定

- T2 是否兼容机械动力应力或蒸汽（[additives/11 §待定问题 3](../additives/11-roadmap.md)）
