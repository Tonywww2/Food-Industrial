# I51 第 5 批成品（肉制品与水产制品）

| 项 | 内容 |
| --- | --- |
| 状态 | 未开始 |
| 阶段 | E 内容 |
| 依赖 | I50、I44 |
| 原文档 | [foods/17](../foods/17-batch5-meat.md)、[foods/18](../foods/18-batch5-aquatic.md)、[additives/11 问题 69](../additives/11-roadmap.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `content.food5`（数据生成）、`food` |

## 目标

37 种成品可做：24 种肉及肉制品、13 种水产及其制品，连同灌肠嘴、火腿模、马口铁罐与三类肠衣；器官与部位由“肢解”附魔供给。

## 范围

- 做：两类成品的配方与数值核对、部位与器官的用法、肠衣分类、家常菜的家常路线、腌腊与干制品的生熟分段
- 不做：肢解附魔与器官掉落（[I44](I44-ingredient-sources.md)）；肠衣与罐的注册（[I40](I40-molds-and-containers.md)）；聚偏二氯乙烯的化工链（[I31](I31-content-batch-5.md)）

## 清单

| 节 | 内容 | 项数 | 原文档 |
| --- | --- | --- | --- |
| 腌腊制品 | 腊肉、腊肠、咸肉、板鸭、金华火腿等 | 见原文档 | [foods/17 §3](../foods/17-batch5-meat.md) |
| 西式肉制品 | 西式火腿、培根、热狗肠、火腿肠等 | 见原文档 | [foods/17 §4](../foods/17-batch5-meat.md) |
| 酱卤与烧烤 | 卤牛肉、酱鸭、卤鸡爪、烤鸭等 | 见原文档 | [foods/17 §5](../foods/17-batch5-meat.md) |
| 家常菜 | 红烧肉、东坡肉、狮子头等 | 见原文档 | [foods/17 §6](../foods/17-batch5-meat.md) |
| 干制肉品 | 肉松、牛肉干等 | 见原文档 | [foods/17 §7](../foods/17-batch5-meat.md) |
| 鱼糜制品 | 鱼丸、鱼豆腐、蟹棒等 | 见原文档 | [foods/18 §3](../foods/18-batch5-aquatic.md) |
| 腌干、熏制、罐头与调味品 | 咸鱼、鱼干、熏鱼、鱼罐头、虾酱、蚝油等 | 见原文档 | [foods/18 §4](../foods/18-batch5-aquatic.md) |
| 合计 | 肉 24、水产 13 | 37 | — |

## 每一项的通用步骤

1. 物料：成品物品已在 [I21](I21-finished-products.md) 注册，补齐食物属性、堆叠上限与分类标签；中间体与半成品按本批设计文档的“中间体”一节新注册（[I05](I05-materials-registry.md)、[foods/08 §3、§4](../foods/08-recipe-format-and-intermediates.md)）
2. 配方：按卡片的工序逐条写进数据生成，配方类型、模式与设备按 [machines/05 §1、§2](../machines/05-final-design.md)，写法按 [foods/08 §2](../foods/08-recipe-format-and-intermediates.md)
3. 品质与容器：配料段的品质位按 [foods/04 §2](../foods/04-quality.md)，容器与模具按 [machines/05 §3、§4](../machines/05-final-design.md) 与本批的“原料、模具与容器”一节
4. 数值：每份的营养与代谢数据由 [I45](I45-food-nutrition-generation.md) 的输入文件提供，本计划只核对卡片与生成结果一致
5. 检查：JEI 可见、最早档次的设备上能做出、唯一性检查无冲突、容器返还正确

## 任务

- [ ] 按清单逐节写配方；切割、斩拌与灌肠走 R26，腌制与陈酿走 R25，烟熏走 R08，罐头走 R29 与 R21
- [ ] 部位与器官按 [foods/17 §1](../foods/17-batch5-meat.md) 引用，鸭肉与虾只接他模组标签，未装时相关成品不注册配方
- [ ] 肠衣：天然肠衣与胶原蛋白肠衣进 `casings/edible`，塑料肠衣不能吃（[machines/05 §4](../machines/05-final-design.md)）
- [ ] 腊肉、腊肠、咸肉、板鸭、金华火腿、咸鱼、鱼干的成品为蒸煮后的熟品，生品作半成品（[foods/17 §8](../foods/17-batch5-meat.md)）
- [ ] 护色剂与亚硝酸盐：配料段放护色剂时按 GB 2760 残留限量计，家常菜不加；误投的 10 种下游成品接 [I45](I45-food-nutrition-generation.md)
- [ ] 切割鱼（R26）出鱼肉与鱼皮，剥虾出虾仁与虾蟹壳，副产按联产配方写（[foods/18 §1](../foods/18-batch5-aquatic.md)）

## 验收

- [ ] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [ ] 清单里每一项在设计写明的最早档次上能做出，并能沿路线升到更高档次的设备
- [ ] 数据生成检查全部通过（耗时为 4 的倍数、每条配方至少一台设备可执行、唯一性无冲突）
- [ ] 每种成品的品质档随添加剂变化，Tooltip 的品质行与营养成分组件显示正确
- [ ] 本批新增的中间体、容器与模具都有名称、贴图与合成

## 待定

- 无
