# I32 Jade 显示

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | F 联动与验证 |
| 依赖 | I09、I10、I12 |
| 原文档 | [machines/04 §8](../machines/04-gui.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `gui.MachineReadout`、`gui.MachineTexts`、`platform.compat.jade` |

## 目标

装有 Jade 时，看向机器或 T0 设备就能看到它的状态、条件和加工进度（2026-11-11，用户要求）。

## 范围

- 做：状态行、条件状态（温度、压力、气氛、环境、无菌）、曲房温湿度、染菌提示、燃料与手摇、催化剂次数、进度条；T0 设备的内容物、条件、缺少的条件与进度
- 不做：物品、流体、能量（Jade 通过能力自行显示）

## 任务

- [x] 阅读 Jade 官方文档（插件入门、配置、各版本升级说明），并对照三个版本的 API jar（[machines/04 §8](../machines/04-gui.md)）
- [x] 三个节点以可选依赖 `deps.opt.jade` 加入，开发运行时用 `-PfiWith=jade`
- [x] 服务器数据：机器的状态、模式、能力、催化剂、进气口；T0 设备的内容物、能力、缺少的条件
- [x] 客户端行：按条件类别每类一行，只列设备能提供且其配方要求的值
- [x] 进度条：用 Jade 的进度扩展，机器一条、T0 设备每个运行中的单元一条
- [x] 状态行文字与界面共用（`MachineTexts`）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| Jade | 11.12.3（见实现记录） | 15.10.6 | 26.1.12 |
| 进度数据 | `CompoundTag`，服务端扩展接口带玩家与世界参数 | `CompoundTag`，参数为 `Accessor` | `ProgressView.Data` |
| 标识符 | `ResourceLocation` | `ResourceLocation` | `Identifier` |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 三个节点游戏内显示状态、条件与进度，Jade 自带的物品、流体、能量显示正常

## 实现记录

- 结构：`gui.MachineReadout` 与版本无关，服务端 `write` 写入数据、`bars` 给出进度条，客户端 `lines` 生成文字与语气（好、普通、提示、警告、问题）；`platform.compat.jade.FoodIndustrialJadePlugin` 由 Jade 按注解加载（未装 Jade 时不加载），数据提供者与组件提供者用同一 ID、分成两个对象（Jade 1.21.6 起不允许一个对象两者兼任），语气映射到 Jade 主题色
- 条件值的筛选：按设备的配方类型扫描配方要求，只列设备能提供、配方也要求的值，结果按配方索引缓存（重载后重算）；例如晾晒架只列露天、晴天、白天，淋灰桶不显示环境
- 文本：`gui.foodindustrial.jade.*`，Jade 开关 `config.jade.plugin_foodindustrial.machine`、`.device`
- 1.20.1 用 Jade 11.12.3：11.13.0 起 Jade 的一个 mixin 用 SRG 别名定位 `Font$StringRenderOutput` 的外部类字段，Loom 开发环境里这个字段叫 `field_24240`，客户端启动即崩溃；11.12.3 的插件接口相同，玩家可用 11.12 及以后的版本
- 顺带：界面的状态行文字与颜色改由 `MachineTexts` 生成（Jade 与界面一致）；`MachineBlockEntity.gasPresent()`、`SterileFermenterBlockEntity.interrupted()`、`T0DeviceBlockEntity.unitStates()` 为显示新增
- 实测（2026-11-11）：
  - 1.20.1：粉碎机运行中，Jade 显示物品、能量条与“剩余 1 秒”；蒸发结晶器冷却结晶模式写“温度：冷却”“压力：常压”；搅拌反应罐写“温度：加热、冷却”“气氛：空气”，堵住背面后变为黄色“气氛：进气口被挡住”；结晶槽晾晒结晶模式写“环境：露天、晴天、白天”；石磨无手摇时红色“需要摇手摇柄”并显示进度；晾晒架两份苹果各一条“剩余 19 分 51 秒”；陶锅在营火上写“温度：加热”；陶坛写“气氛：空气”；晒盐池写环境
  - 1.21.1、26.1.2：粉碎机、搅拌反应罐、结晶槽、晾晒架同上
- 开发环境默认带 Jade（2026-11-12，用户要求，用于测试）：根 `gradle.properties` 的 `fi.devWith=jade` 让三个节点的开发运行（客户端、服务器、数据生成）都加入 Jade，不必再写 `-PfiWith=jade`；测试不装 Jade 时用 `-PfiWithout=jade`。三个节点数据生成与单元测试通过，生成的数据不变

## 待审（实现时拟定，请确认）

- 无。已确认（2026-11-12，用户）：显示的行与措辞（“环境”一词、无菌只写“无菌”）；1.20.1 的 Jade 依赖用 11.12.3；Jade 开关名“食品工业：锅与被动设备状态”

## 待定

- 无
