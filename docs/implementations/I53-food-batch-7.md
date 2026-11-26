# I53 第 7 批成品（饮料、特殊膳食、酒类与其他类）

| 项 | 内容 |
| --- | --- |
| 状态 | 未开始 |
| 阶段 | E 内容 |
| 依赖 | I52、I46 |
| 原文档 | [foods/21](../foods/21-batch7-beverages.md)、[foods/22](../foods/22-batch7-alcohol-snacks.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `content.food7`（数据生成）、`food` |

## 目标

72 种成品可做：34 种饮料、4 种特殊膳食用食品、17 种酒类、17 种其他类，连同吹瓶模、玻璃瓶、铝罐、PET 瓶、滤纸袋与橡木桶。

## 范围

- 做：四类成品的配方与数值核对、碳酸化的三档、蒸馏与陈酿、特殊膳食的强化配方、其他类的小食
- 不做：容器与模具的注册（[I40](I40-molds-and-containers.md)）；维生素与矿物质的结算（[I46](I46-vitamins-and-minerals.md)）；每份数值的录入（[I45](I45-food-nutrition-generation.md)）

## 清单

| 节 | 内容 | 项数 | 原文档 |
| --- | --- | --- | --- |
| 果汁与蔬菜汁 | 鲜榨汁、浓缩汁、复合汁等 | 见原文档 | [foods/21 §2](../foods/21-batch7-beverages.md) |
| 汽水与功能饮料 | 汽水、运动饮料、能量饮料、维生素水等 | 见原文档 | [foods/21 §3](../foods/21-batch7-beverages.md) |
| 茶、咖啡与植物奶 | 茶饮料、奶茶、罐装咖啡、速溶咖啡、豆奶、燕麦奶等 | 见原文档 | [foods/21 §4](../foods/21-batch7-beverages.md) |
| 特殊膳食用食品 | 婴儿米粉、代餐粉等 | 4 | [foods/21 §5](../foods/21-batch7-beverages.md) |
| 酒类 | 啤酒、黄酒、白酒、果酒、葡萄酒、威士忌、白兰地、朗姆酒、利口酒等 | 17 | [foods/22 §2](../foods/22-batch7-alcohol-snacks.md) |
| 其他类 | 马铃薯片、玉米片、爆米花、虾片、果冻、椰果、龟苓膏、爆爆珠、调味茶包等 | 17 | [foods/22 §3](../foods/22-batch7-alcohol-snacks.md) |
| 合计 | 饮料 34、特殊膳食 4、酒 17、其他 17 | 72 | — |

## 每一项的通用步骤

1. 物料：成品物品已在 [I21](I21-finished-products.md) 注册，补齐食物属性、堆叠上限与分类标签；中间体与半成品按本批设计文档的“中间体”一节新注册（[I05](I05-materials-registry.md)、[foods/08 §3、§4](../foods/08-recipe-format-and-intermediates.md)）
2. 配方：按卡片的工序逐条写进数据生成，配方类型、模式与设备按 [machines/05 §1、§2](../machines/05-final-design.md)，写法按 [foods/08 §2](../foods/08-recipe-format-and-intermediates.md)
3. 品质与容器：配料段的品质位按 [foods/04 §2](../foods/04-quality.md)，容器与模具按 [machines/05 §3、§4](../machines/05-final-design.md) 与本批的“原料、模具与容器”一节
4. 数值：每份的营养与代谢数据由 [I45](I45-food-nutrition-generation.md) 的输入文件提供，本计划只核对卡片与生成结果一致
5. 检查：JEI 可见、最早档次的设备上能做出、唯一性检查无冲突、容器返还正确

## 任务

- [ ] 按清单逐节写配方；碳酸化按三档（M04 常压通气 T1、M19 加压 T2、跳跳糖 600 psi T3），蒸馏按 R10 的一次蒸馏与精馏两模式，陈酿按 R25 加橡木桶催化剂
- [ ] 酒类的每份酒精与密度换算接 [I45](I45-food-nutrition-generation.md)；Tooltip 的酒精注（血液酒精浓度）由 [I35](I35-metabolism.md) 提供
- [ ] 特殊膳食用食品的强化成分接 [I46](I46-vitamins-and-minerals.md)，婴儿米粉按 GB 10769-2025、代餐粉按 GB 28050 的门槛（[foods/21 §5](../foods/21-batch7-beverages.md)）
- [ ] 吹瓶模与 PET 瓶的 T3 门槛、铝罐与马口铁罐的外部依赖按 [machines/05 §4](../machines/05-final-design.md)
- [ ] 大麦、啤酒花、高粱接 [I44](I44-ingredient-sources.md) 的新作物；椰子与草本组标签按 [foods/21 §1](../foods/21-batch7-beverages.md)
- [ ] 麦糟、酒糟等副产按联产配方写（[foods/08 §5](../foods/08-recipe-format-and-intermediates.md)）

## 验收

- [ ] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [ ] 清单里每一项在设计写明的最早档次上能做出，并能沿路线升到更高档次的设备
- [ ] 数据生成检查全部通过（耗时为 4 的倍数、每条配方至少一台设备可执行、唯一性无冲突）
- [ ] 每种成品的品质档随添加剂变化，Tooltip 的品质行与营养成分组件显示正确
- [ ] 本批新增的中间体、容器与模具都有名称、贴图与合成

## 待定

- 无
