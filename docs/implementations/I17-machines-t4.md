# I17 T4 生物与精细机器（M29–M31）

| 项 | 内容 |
| --- | --- |
| 状态 | 未开始 |
| 阶段 | D 设备 |
| 依赖 | I16 |
| 原文档 | [additives/06 §T4 生物 / 精细](../additives/06-machines.md)、[machines/03 §3](../machines/03-io-layout.md)、[additives/15 §机器](../additives/15-batch-4.md)、[additives/08 §C 菌种与酶](../additives/08-raw-materials.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `machine.bio` |

## 目标

无菌发酵、菌种操作、膜分离可用，菌种体系闭环。

## 范围

- 做：M29 无菌发酵罐、M30 菌种工作台、M31 膜分离机
- 不做：配方内容

## 任务

- [ ] M29：在位灭菌、双菌种、染菌只减产或报废（[additives/06 §T4 生物 / 精细](../additives/06-machines.md)，[additives/10 §6](../additives/10-purity-jei-ae2.md)）
- [ ] M30：分离、纯化、扩培、诱变、基因重组的交互界面（`CultureBenchBlockEntity`）；基因重组模式（T4，2026-11-05）：宿主菌种 + 供体样本 + 表达载体 → 工程菌种，表达载体 `cultures/expression_vector` 是耗材，每次消耗 1 个，不带纯度；工程菌种的配方随第四批内容（[machines/02 §4](../machines/02-architecture.md)）
- [ ] M31：四种膜模式，膜组件耗材（[additives/06 §T4 生物 / 精细](../additives/06-machines.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 差异 | 无 | 无 | 无 |

## 验收

- [ ] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [ ] M29–M31 每台至少跑通一条第一批配方，界面、侧面配置、AE2 推送正常
- [ ] 性能：睡眠与运行状态符合 [machines/02 §6](../machines/02-architecture.md)

## 待定

- 无
