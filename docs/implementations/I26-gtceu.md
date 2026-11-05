# I26 GTCEu 材料联动

| 项 | 内容 |
| --- | --- |
| 状态 | 未开始 |
| 阶段 | F 联动与验证 |
| 依赖 | I05、I06 |
| 原文档 | [additives/09 §与 GTCEu 的联动](../additives/09-chemical-formulas.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `platform.compat.gtceu` |

## 目标

装有 GTCEu 时两边的同种材料互认，纯度门槛表现与不装时一致。

## 范围

- 做：材料对应、标签互通、不一致项处理

## 任务

- [ ] 按 09 的 GT 对应列建立映射（[additives/09 §与 GTCEu 的联动](../additives/09-chemical-formulas.md)）
- [ ] GT 材料按外部物品计（外部默认值，additives/10 §1.6）（[additives/09 §与 GTCEu 的联动 联动方式 3](../additives/09-chemical-formulas.md)）；需要提高的经 `BundledPurityRules` 加规则，不高于该物料的粗制下限（2026-11-04 定；上限随 I28 由 60% 改）
- [ ] 处理不一致项（糖、钾碱、明矾石、磷灰石、草木灰、岩盐等）（[additives/09 §与 GTCEu 的联动](../additives/09-chemical-formulas.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| GTCEu | 待加入依赖 | 视版本可用性 | 视版本可用性 |

## 验收

- [ ] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [ ] 装有 GTCEu 时第一批路线仍可完成

## 待定

- 无
