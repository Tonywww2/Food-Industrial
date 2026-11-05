# I27 性能基准与回归

| 项 | 内容 |
| --- | --- |
| 状态 | 未开始 |
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

- [ ] 开发版 `/foodindustrial perf`：按机器类型统计每批耗时（[machines/02 §6.4 第 15 条](../machines/02-architecture.md)）
- [ ] 基准世界：500 台运行中的机器、500 台睡眠机器，用 spark 采样（[machines/02 §6.4](../machines/02-architecture.md)）
- [ ] 热路径分配检查（JFR 分配采样）（[machines/02 §6.3 第 10 条](../machines/02-architecture.md)）
- [ ] 回归阈值：平均总耗时 ≤ 1 ms/tick；超过即阻止发布（[machines/02 §6.4 第 15 条](../machines/02-architecture.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 测量 | 三节点分别测量 | 同左 | 同左 |

## 验收

- [ ] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [ ] 三个节点的基准结果记录在本计划末尾

## 待定

- 无
