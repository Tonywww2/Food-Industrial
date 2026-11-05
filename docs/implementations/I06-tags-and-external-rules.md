# I06 标签与外部物品规则

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | B 物料 |
| 依赖 | I05 |
| 原文档 | [additives/08 §标签体系](../additives/08-raw-materials.md)、[additives/08 §A](../additives/08-raw-materials.md)、[additives/08 §B](../additives/08-raw-materials.md)、[additives/10 §3](../additives/10-purity-jei-ae2.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `material.tags`、`core.purity`（自带规则）、`platform.datagen`、`tools/materials` |

> 纯度模型已改为 v2（2026-11-04）：本计划中与 [I28](I28-purity-model-v2.md) 冲突的条目以 I28 为准。

## 目标

所有配方输入都能按标签匹配，原版与他模组的同种物料直接可用并按规则计纯度。

## 范围

- 做：本模组标签、导入与导出的通用标签、外部纯度规则默认值
- 不做：他模组机器配方（I25）

## 任务

- [x] 按标签前缀体系生成 `materials/`、`chemicals/`、流体、`cultures/`、`enzymes/`、`catalysts/`、`additives/`、`foods/` 标签（[additives/08 §标签体系](../additives/08-raw-materials.md)）。`additives/`、`foods/` 还没有物品，物料加进主表后由同一机制生成（I18–I21）
- [x] 导入原版与通用标签，全部 `required: false`；1.20.1 用 `forge:`，1.21 起用 `c:`（[additives/08 §标签体系 规则 5](../additives/08-raw-materials.md)）
- [x] 本模组物品导出到通用标签（[additives/08 §标签体系](../additives/08-raw-materials.md)）
- [x] 外部纯度规则的默认数据包；数据生成检查自带规则不高于 60%（[additives/10 §1 外部纯度规则](../additives/10-purity-jei-ae2.md)）。现在不带任何规则，I25、I26 联动时添加（2026-11-04 定）
- [x] 节点差异：热力只在 1.20.1，农夫乐事在 26.1.2 缺席，相关导入条目按节点生成（[additives/11 §待定问题 14](../additives/11-roadmap.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 通用标签前缀 | `forge:` | `c:` | `c:` |
| 数据目录 | `tags/items` | `tags/item` | `tags/item` |
| 气体标签 | 无 | `c:gaseous` | `c:gaseous` |
| 桶标签 | 无 | `c:buckets/<ID>` | `c:buckets/<ID>` |
| 直接写的他模组 ID | 农夫乐事、热力、气动工艺 | 农夫乐事、气动工艺 | 无（不存在的不写） |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 三个节点的数据生成输出中没有指向不存在物品的标签条目

## 实现记录

- 数据来源：[additives/08 §A/§B](../additives/08-raw-materials.md) 改为每行一个标签，分“默认内容（1.20.1）”“默认内容（1.21 起）”两列；`tools/materials/export_materials.py` 导出为 `src/main/resources/foodindustrial/tag_imports.json`（95 个标签，含 09 化学品的 `catalysts/` 标签）。本模组物料自己的标签、规则 3 的导入、规则 2 的导出由主表在数据生成时算出
- 他模组标签名在各节点的 jar 中核对（Forge / NeoForge、农夫乐事、热力、气动工艺、潘马斯、机械动力、通用机械、沉浸工程；GTCEu 按其运行时命名规则），未核实的候选不写入，列在 08 各表下方
- 代码：`material.tags.TagImports`（读表）、`TagPlan`（不依赖游戏的计算与检查）、`ModTags`（数据生成）；`core.purity.BundledPurityRules`（自带规则的数据生成与 60% 检查）；`PlatformDatagen.gaseousFluidTag`、`bucketTag`，`PlatformTags.itemsIn`
- 检查：原版与本模组的直接 ID 不存在即让 `runData` 失败；他模组 ID 在该节点不存在时不写并记日志（26.1.2：农夫乐事的大米、稻草、树皮，气动工艺的植物油）；引用本模组标签必须已生成；空标签不写。数据生成日志列出只含他模组标签的本模组标签（如 `materials/lemon`、`materials/nickel`），未装对应模组时为空，后备来源见 I07
- 输出：1.20.1 共 596 个物品标签、217 个流体标签；1.21.1 为 678 / 217；26.1.2 为 677 / 217
- 运行验证（三个节点服务端，`/fidev tag`）：`chemicals/salt` 与导出的 `dusts/salt`、`chemicals/potassium_nitrate`（1.20.1 含热力的硝石）、`catalysts/nickel` 只有镍催化剂、`materials/grass`（1.21 起为 `short_grass`）、`c:gaseous` 21 种气体、`c:buckets/ethanol`；日志无标签加载错误
- 单元测试：`TagPlanTest`

## 待审

- 08 §A/§B 中对原表候选的改正与补充：潘马斯的实际标签名（香荚兰、芝麻、桂皮、茶叶、可可），GTCEu 焦炭 `gems/coke`，乳胶另导入 `#forge:latex`，原版物品直接写入（鸡蛋、骨头、皮革、沙子、铜铁金锭）
- 1.21 起按通用写法换成 `c:` 但在核对的模组中没有提供者的标签（如 `c:dusts/niter`、`c:gems/apatite`、`c:latex`）
- `materials/raw_fish` 沿用原表的 `#minecraft:fishes`，其中含熟鳕鱼、熟鲑鱼

## 待定

- 无。已定（2026-11-04）：导出范围为规则 2 加 1.21 起的 `c:gaseous` 与 `c:buckets/<ID>`；09 中兼作催化剂的化学品同时进 `chemicals/` 与 `catalysts/`；自带外部纯度规则现在不写，I25 / I26 联动时添加；“配方输入只引用本模组标签”的检查移到 I08；金属镍用 `materials/nickel`；柑橘含柠檬、橙、青柠、葡萄柚；生肉补全为猪、牛、羊、鸡、兔
