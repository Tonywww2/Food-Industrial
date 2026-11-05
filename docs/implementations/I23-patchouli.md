# I23 Patchouli 手册与多方块预览

| 项 | 内容 |
| --- | --- |
| 状态 | 未开始 |
| 阶段 | F 联动与验证 |
| 依赖 | I14、I04 |
| 原文档 | [additives/10 §5 多方块](../additives/10-purity-jei-ae2.md)、[additives/07 §历史原型速览](../additives/07-manual-crafting.md)、[text-standard §1](../text-standard.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `platform.compat.patchouli`、手册资源 |

## 目标

一本按阶段组织的手册，介绍工艺与历史原型，并能预览 10 台多方块的结构。

## 范围

- 做：手册结构、多方块预览、条目文本

## 任务

- [ ] 手册分 T0–T4 五章，每台机器与工具一个条目（[additives/06](../additives/06-machines.md)，[additives/07](../additives/07-manual-crafting.md)）
- [ ] 10 台多方块的结构预览（M06、M07、M10、M19、M20、M22、M26、M27、M28、M29）（[machines/02 §4](../machines/02-architecture.md)）
- [ ] 历史原型条目（[additives/07 §历史原型速览](../additives/07-manual-crafting.md)）
- [ ] 文本用完整句，遵循文本规范（[text-standard §1](../text-standard.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| Patchouli | 1.20.1-84-FORGE | 1.21.1-93-NEOFORGE | 版本 ID `2CsnFLom` |

## 验收

- [ ] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [ ] 三个节点手册可打开，多方块预览与实际结构一致

## 待定

- 无
