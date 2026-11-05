# I01 platform 适配层扩展

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | A 基础 |
| 依赖 | — |
| 原文档 | [machines/02 §1、§7](../machines/02-architecture.md)、[CONVENTIONS §2–4](../../CONVENTIONS.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `platform`、`platform.machine`、`platform.recipe`、`platform.purity`、`platform.network`、`platform.datagen` |

## 目标

把机器、配方、纯度、界面需要的版本相关能力全部封装成语义化的 platform API，三个节点都实现，业务层从此不再碰版本差异。

## 范围

- 做：[machines/02 §7](../machines/02-architecture.md) 表中列出的全部 API，以及物品 Tooltip、数据生成输出这两项已知缺口
- 不做：具体机器、配方的业务逻辑（I08、I09）
- 不做：可选模组联动（I22–I26 各自的 `platform/compat`）
- 不做：`PlatformIngredients`（并入 I03，与原料类型同时实现）、`PlatformVanillaCooking`（并入 I13，依赖 I08 的配方模型）

## 任务

- [x] `PlatformBlockEntity`：稳定的 `saveData(DataWriter)` / `loadData(DataReader)` / `serverTick()` / 客户端同步；包装 26.1 的 `ValueInput` / `ValueOutput`（[machines/02 §7](../machines/02-architecture.md)，[CONVENTIONS §2 第 6 条](../../CONVENTIONS.md)）
- [x] `DataWriter` / `DataReader`：业务层用的读写接口，支持 byte、int、long、字符串、嵌套与列表
- [x] `PlatformMachineCapabilities.register(...)`：把业务层的物品、流体、能量容器按侧面配置暴露；1.20.1 Forge 能力 + `LazyOptional`，1.21.1 `RegisterCapabilitiesEvent`，26.1 新传输接口（[machines/02 §7](../machines/02-architecture.md)）
- [x] `PlatformNeighborCache`：相邻方块能力缓存与失效通知（[machines/02 §6.3 第 9 条](../machines/02-architecture.md)）
- [x] `PlatformFluids`：`FluidAmount` 与各节点 `FluidStack` 互转；流体注册（含“气态”标记、无桶）；流体标签；客户端渲染属性（[machines/03 §4](../machines/03-io-layout.md)）
- [x] `PlatformPurity`：物品与流体纯度读写；1.20.1 NBT byte，1.21.1 起 byte 型数据组件 `foodindustrial:purity`（[machines/02 §1](../machines/02-architecture.md)，[additives/10 §1](../additives/10-purity-jei-ae2.md)）
- [x] `PlatformRecipes`：用业务 `Codec` 注册配方类型与序列化器；配方输入桥接（1.20.1 容器、1.21 起 `RecipeInput`）；配方与标签重载监听（[machines/02 §3](../machines/02-architecture.md)）
- [x] `PlatformMenus`、`PlatformNetwork`：带额外数据的菜单打开；客户端→服务器配置包、服务器→客户端同步包（[machines/04 §6](../machines/04-gui.md)）
- [x] `PlatformItem` 的 Tooltip 钩子：稳定的 `appendTooltip(lines, shiftDown)`，包装各节点 `appendHoverText` 签名（[text-standard §3](../text-standard.md)）
- [x] `PlatformDatagen`：配方、标签、语言、方块状态、物品模型按节点目录输出（1.21 起单数目录、26.1 的 `assets/<ns>/items`）（[CONVENTIONS §5](../../CONVENTIONS.md)）
- [x] 每个 API 写 Javadoc，签名在目标版本反编译源码中核对（[CONVENTIONS §4 检查清单](../../CONVENTIONS.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| BlockEntity 存取 | `saveAdditional` / `load` + `CompoundTag` | `saveAdditional` / `loadAdditional` + `HolderLookup.Provider` | `ValueOutput` / `ValueInput` |
| 能力 | Forge `Capability` + `LazyOptional` | `BlockCapability` | 新传输接口（实现前确认） |
| 纯度存储 | NBT byte | 数据组件 | 数据组件 |
| 配方序列化 | JSON + `FriendlyByteBuf` | `MapCodec` + `StreamCodec` | 同 1.21.1（实现前确认） |
| 网络 | `SimpleChannel` | `CustomPacketPayload` | `CustomPacketPayload` |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 一个测试用 BlockEntity 在三个节点上都能存取、同步、对漏斗与管道暴露物品和流体
- [x] `runServer` 在三个节点上启动无报错

## 实现记录

- 测试内容在 `devtest` 包，只在开发环境注册：`platform_test` 方块（两格物品、4000 mB 储罐、10000 FE，每 20 tick 搬运与推送）、`/fidev` 调试命令
- 运行验证（2026-11-04）：三个节点 `runServer` 通过 RCON 验证漏斗插入、方块实体搬运到箱子、向上推送 1000 mB 水与 500 FE、重启后数据保留；日志无本模组错误
- 客户端同步（2026-11-04）：三个节点的客户端（Xvfb）连接专用服务器，进入区块时收到测试方块实体的储罐数据（1000 mB 水）

## 待定

- 26.1.2 节点的传输接口与能量接口以反编译源码为准
