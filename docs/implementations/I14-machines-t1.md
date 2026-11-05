# I14 T1 作坊机器（M01–M10）

| 项 | 内容 |
| --- | --- |
| 状态 | 未开始 |
| 阶段 | D 设备 |
| 依赖 | I09、I10、I08 |
| 原文档 | [additives/06 §T1 作坊](../additives/06-machines.md)、[machines/03 §3](../machines/03-io-layout.md)、[machines/01 §4.3](../machines/01-recipe-types.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `machine.workshop` |

## 目标

10 台作坊机器可用，玩家能用燃料与人力把第一批产品做到粗制。

## 范围

- 做：M01–M10 的定义、模式、模块、多方块结构、模型与贴图
- 不做：配方内容（I18 起）

## 任务

- [ ] M01 石磨、M02 螺旋压榨机：人力（右键摇柄）与水车驱动（[additives/06 §T1 作坊](../additives/06-machines.md)）
- [ ] M03 熬煮灶：燃料热源，R05 与简单中和（[additives/06 §T1 作坊](../additives/06-machines.md)）
- [ ] M04 浸提桶：沉降、浸提、通气模式（接 M05 的 CO2、M10 的烟气）（[additives/06 §T1 作坊](../additives/06-machines.md)）
- [ ] M05 发酵缸：开口 / 密闭 / 厌氧三种模式，导出逸出气体（[additives/06 §T1 作坊](../additives/06-machines.md)）
- [ ] M06 曲房、M07 土窑、M10 烘房：多方块结构与温湿度来源（[additives/06 §T1 作坊](../additives/06-machines.md)）；成员只用本模组方块（外壳等），图样先拟定写回文档与用户确认；需要可变尺寸时给 `StructurePattern` 加可重复层
- [ ] M08 蒸馏甑：只得主馏分；精油分层器模块（[machines/01 §4.3](../machines/01-recipe-types.md)）
- [ ] M09 结晶槽：露天晾晒条件（[additives/06 §T1 作坊](../additives/06-machines.md)）
- [ ] 槽位按 [machines/03 §3](../machines/03-io-layout.md)；相位按 [machines/02 §6.1](../machines/02-architecture.md)

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 差异 | 无（全部经 platform） | 无 | 无 |

## 验收

- [ ] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [ ] M01–M10 每台至少跑通一条第一批配方，界面、侧面配置、AE2 推送正常
- [ ] 性能：睡眠与运行状态符合 [machines/02 §6](../machines/02-architecture.md)

## 待定

- 无
