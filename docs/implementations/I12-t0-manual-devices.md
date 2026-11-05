# I12 T0 手工工具、被动设备与世界交互

| 项 | 内容 |
| --- | --- |
| 状态 | 未开始 |
| 阶段 | D 设备 |
| 依赖 | I08、I09、I07 |
| 原文档 | [additives/07](../additives/07-manual-crafting.md)、[machines/01 §2](../machines/01-recipe-types.md)、[additives/10 §2.1](../additives/10-purity-jei-ae2.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `manual`、`world` |

## 目标

开局不靠机器就能做出 07 列出的全部手工产物，纯度按原料和工序确定。

## 范围

- 做：H01–H09 工具与被动设备、H11–H15 世界交互、自然接种
- 不做：营火、熔炉等原版设备（I13）

## 任务

- [ ] 手持工具 `ManualProcess`：右键一次推进一步，`manual.actions` 步完成（[machines/02 §5](../machines/02-architecture.md)）
- [ ] H01 研钵、H02 筛子 / 布袋（两件物品）、H03 锅（陶锅 / 铁锅两件，焦化只认铁锅）（[additives/07 §手工工具](../additives/07-manual-crafting.md)）
- [ ] 被动设备：H04 陶坛、H05 淋灰桶、H06 晒盐池（世界结构）、H07 晾晒架、H08 曲盘、H09 熏蒸箱，20 tick 一批（[additives/07 §手工工具](../additives/07-manual-crafting.md)，[machines/02 §6.1 第 3 条](../machines/02-architecture.md)）
- [ ] W01 方块转化、W02 割取的配方格式与 `BlockTransformRecipe`、`HarvestRecipe`（自 I08 移入，2026-11-04 定；配方类型 ID 与默认来源规则已在 I08 登记）：格式先拟定写回 [machines/01 §3.1](../machines/01-recipe-types.md)，与用户确认后实装（[machines/02 §3](../machines/02-architecture.md)）
- [ ] H11 硝土生成（W01，随机刻）；H12 寒冷冻融；H14 冷置析酒石；H15 割取（W02，带冷却）（[additives/07 §世界交互](../additives/07-manual-crafting.md)）
- [ ] H13 自然接种：按底物与群系长出确定菌种（[additives/07 §自然接种（H13）](../additives/07-manual-crafting.md)）
- [ ] 环境条件只影响耗时与产量；失败产物（焦糖渣、死烧石膏、霉变姜黄、徒长麦芽）（[additives/10 §2.2 第 6 条](../additives/10-purity-jei-ae2.md)，[additives/07 §手工能做出的第一批产品](../additives/07-manual-crafting.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 交互事件 | 由 platform 事件包装 | 同左 | 同左 |

## 验收

- [ ] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [ ] [additives/07](../additives/07-manual-crafting.md) 两张产品表中每一行都能按所写链条做出，纯度与表中一致

## 待定

- 无
