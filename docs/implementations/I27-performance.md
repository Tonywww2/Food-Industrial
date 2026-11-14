# I27 性能基准与回归

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | F 联动与验证 |
| 依赖 | I09、I16 |
| 原文档 | [machines/02 §6](../machines/02-architecture.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `debug`、测试世界 |

## 目标

性能规范有可重复的测量，回归能被发现。

## 范围

- 做：调试命令、基准世界、回归阈值

## 任务

- [x] 开发版 `/foodindustrial perf`：按机器类型统计每批耗时（[machines/02 §6.4 第 15 条](../machines/02-architecture.md)）
- [x] 基准世界：500 台运行中的机器、500 台睡眠机器，用 spark 采样（[machines/02 §6.4](../machines/02-architecture.md)）
- [x] 热路径分配检查（JFR 分配采样）（[machines/02 §6.3 第 10 条](../machines/02-architecture.md)）
- [x] 回归阈值：平均总耗时 ≤ 1 ms/tick；超过即阻止发布（[machines/02 §6.4 第 15 条](../machines/02-architecture.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 测量 | 三节点分别测量 | 同左 | 同左 |
| 服务器总耗时 | `/forge tps` | `/tick query` | `/tick query`；需 `pause-when-empty-seconds=-1`，否则无人 60 秒后暂停 |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 三个节点的基准结果记录在本计划末尾

## 实现记录

- 命令（开发版）：`/foodindustrial perf start | stop | report` 按机器类型记批次数、睡眠结束的批次、每批平均耗时与每 tick 总耗时（`machine.MachinePerf`，关闭时机器每 tick 只多读一个静态字段）；`/fidev bench <运行> <睡眠>` 在执行位置旁摆粉碎机（每排 32 台、间隔 2 格，下垫石头），运行的放 64 个石膏矿并充满电；`/fidev benchfill` 重新装料
- 测量步骤（服务器，RCON）：`forceload add 192 192 272 272` → `execute positioned 200 150 200 run fidev bench 500 500` → 等 20 秒 → `foodindustrial perf start` → 30 秒后 `foodindustrial perf report`，同时读服务器总耗时；再用 `fill` 清掉机器、`kill @e[type=item]` 清掉掉出的物品后读基线。分配检查用 `jcmd <pid> JFR.start duration=60s settings=profile`，`jfr print --events jdk.ObjectAllocationSample` 后筛出栈里有本模组的样本
- spark 已作为可选依赖接入（`-PfiWith=spark`），但它的命令结果异步发送，RCON 收不到；采样改用 JFR（同为采样分析器，执行与分配样本都能按栈筛选）
- JFR 发现与修正：
  - `MachineDefinition.lacking` 每批用 for-each 遍历要求列表，1.20.1（Java 17）上迭代器没有被消除，每批分配一个对象；改为下标循环
  - 带模式的机器每批调用 `modeLacking`，每次新建一个 `TreeSet`；改为先用位掩码判断，缺条件时才算缺哪项
  - W01 方块转化在随机刻里每次匹配都截字符串、拼方块 ID；改为重载时解析一次方块或标签（`WorldInteractions.Ticked`）
  - 其余分配都在输入变化后的重新匹配与产出时（匹配数组、产物纯度、输出空间检查的物品堆副本），每次完成约 1 KB，不在每批的路径上；三个节点的样本里没有其他每批分配

## 基准结果（2026-11-11）

500 台运行中、500 台睡眠的粉碎机（T2，石膏矿 → 石膏），测量期间 JFR 关闭。睡眠的机器每 tick 只比较两次就返回，测量窗口内没有批次。

| 节点 | Java | 每批耗时 | 本模组机器 ms/tick | 服务器 ms/tick（有基准 / 清除后） |
| --- | --- | --- | --- | --- |
| 1.20.1-forge | 17 | 0.75 µs | 0.094 | 1.42–1.71 / 0.89 |
| 1.21.1-neoforge | 21 | 0.32 µs | 0.040 | 0.8 / 0.4 |
| 26.1.2-neoforge | 25 | 0.47 µs | 0.058 | 0.7 / 0.3 |

三个节点都低于 1 ms/tick 的阈值：按本模组统计的批次耗时不到 0.1 ms/tick；连同 1,000 个方块实体的调度开销，服务器总耗时增加 0.4–0.8 ms/tick。测量在开发环境的云端容器里进行，绝对值随机器而变，回归比较时用同一台机器、同一步骤。

## 待审（实现时拟定，请确认）

- 无。已确认（2026-11-11，用户）：用 JFR 代替 spark 采样；基准世界只用粉碎机一种机器

## 待定

- 无
