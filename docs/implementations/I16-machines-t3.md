# I16 T3 化工机器（M22–M28）

| 项 | 内容 |
| --- | --- |
| 状态 | 未开始 |
| 阶段 | D 设备 |
| 依赖 | I15 |
| 原文档 | [additives/06 §T3 化工](../additives/06-machines.md)、[machines/03 §3](../machines/03-io-layout.md)、[machines/01 §4.3](../machines/01-recipe-types.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `machine.chemical` |

## 目标

7 台化工机器可用，产出提升到食品级，石化与电化学线打通。

## 范围

- 做：定义、多方块、催化剂槽
- 不做：配方内容

## 任务

- [ ] M22 精馏塔：层数 = 馏分数；深冷、汽提、分子蒸馏模块（[additives/06 §T3 化工](../additives/06-machines.md)）
- [ ] M23 高压反应釜：催化剂槽，加氢、合成氨、环氧化（[additives/06 §T3 化工](../additives/06-machines.md)）
- [ ] M24 电解槽：电极催化剂槽，三路流体输出（[machines/03 §3](../machines/03-io-layout.md)）
- [ ] M25 吸附柱：吸附剂再生（[additives/06 §T3 化工](../additives/06-machines.md)）
- [ ] M26 电弧炉、M27 气相反应炉（炉管段扩展）、M28 喷雾干燥塔：多方块（[machines/01 §4.3](../machines/01-recipe-types.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 差异 | 无 | 无 | 无 |

## 验收

- [ ] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [ ] M22–M28 每台至少跑通一条第一批配方，界面、侧面配置、AE2 推送正常
- [ ] 性能：睡眠与运行状态符合 [machines/02 §6](../machines/02-architecture.md)

## 待定

- 贵金属与过渡金属催化剂来源（[additives/11 §待定问题 4](../additives/11-roadmap.md)）
