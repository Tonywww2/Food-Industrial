# I52 第 6 批成品（果蔬豆类菌藻坚果、可可与糖果）

| 项 | 内容 |
| --- | --- |
| 状态 | 未开始 |
| 阶段 | E 内容 |
| 依赖 | I51 |
| 原文档 | [foods/19](../foods/19-batch6-produce.md)、[foods/20](../foods/20-batch6-confectionery.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `content.food6`（数据生成）、`food` |

## 目标

74 种成品可做：50 种果蔬豆类食用菌藻类坚果制品、24 种可可制品巧克力与糖果，连同豆腐模、巧克力模、硬糖模、棒棒糖模、淀粉模与糖衣锅。

## 范围

- 做：两类成品的配方与数值核对、本批中间体、新增作物与他模组标签的接入、糖果与巧克力的三种模式
- 不做：模具的注册（[I40](I40-molds-and-containers.md)）；M48、M49 的设备本体（[I41](I41-food-machines-t1.md)、[I42](I42-food-machines-t2.md)）

## 清单

| 节 | 内容 | 项数 | 原文档 |
| --- | --- | --- | --- |
| 水果制品 | 果脯、果酱、果干、糖渍橘皮、冰糖葫芦等 | 见原文档 | [foods/19 §3](../foods/19-batch6-produce.md) |
| 蔬菜制品 | 榨菜、雪菜、泡菜、酸菜、腌黄瓜、醋渍甜菜根等 | 见原文档 | [foods/19 §4](../foods/19-batch6-produce.md) |
| 豆制品 | 北豆腐、南豆腐、豆干、腐乳、豆豉、豆浆、素肉等 | 见原文档 | [foods/19 §5](../foods/19-batch6-produce.md) |
| 食用菌、藻类、坚果与籽类 | 干菇、调味海苔、凉拌海带丝、琼脂凉粉、椒盐坚果、琥珀核桃等 | 见原文档 | [foods/19 §6](../foods/19-batch6-produce.md) |
| 可可制品与巧克力 | 可可粉、可可脂、三种巧克力、巧克力豆、酒心巧克力等 | 见原文档 | [foods/20 §3](../foods/20-batch6-confectionery.md) |
| 糖果 | 硬糖、润喉糖、棒棒糖、奶糖、软糖、棉花糖、糖衣巧克力豆、龙须酥等 | 见原文档 | [foods/20 §4](../foods/20-batch6-confectionery.md) |
| 合计 | 果蔬等 50、可可与糖果 24 | 74 | — |

## 每一项的通用步骤

1. 物料：成品物品已在 [I21](I21-finished-products.md) 注册，补齐食物属性、堆叠上限与分类标签；中间体与半成品按本批设计文档的“中间体”一节新注册（[I05](I05-materials-registry.md)、[foods/08 §3、§4](../foods/08-recipe-format-and-intermediates.md)）
2. 配方：按卡片的工序逐条写进数据生成，配方类型、模式与设备按 [machines/05 §1、§2](../machines/05-final-design.md)，写法按 [foods/08 §2](../foods/08-recipe-format-and-intermediates.md)
3. 品质与容器：配料段的品质位按 [foods/04 §2](../foods/04-quality.md)，容器与模具按 [machines/05 §3、§4](../machines/05-final-design.md) 与本批的“原料、模具与容器”一节
4. 数值：每份的营养与代谢数据由 [I45](I45-food-nutrition-generation.md) 的输入文件提供，本计划只核对卡片与生成结果一致
5. 检查：JEI 可见、最早档次的设备上能做出、唯一性检查无冲突、容器返还正确

## 任务

- [ ] 按清单逐节写配方；糖果与巧克力走 R27 的调温、浇注、包衣，凝固走 R11，腌制走 R25，干燥走 R07
- [ ] 新增作物（黑豆、芥菜、梅子、山楂）与他模组标签（黄瓜、萝卜、甜菜根、核桃、坚果）接 [I44](I44-ingredient-sources.md)
- [ ] 豆腐模走 R02 压榨的催化剂，苦卤与石膏两条点卤路线都保留（[foods/19 §5](../foods/19-batch6-produce.md)）
- [ ] 酒心巧克力的壳厚与酒心比例按 [foods/20 §3](../foods/20-batch6-confectionery.md)；腐乳的酒精量接 [I45](I45-food-nutrition-generation.md)
- [ ] 大豆素肉的挤压膨化走 M39 的挤压膨化段模块（[foods/19 §5](../foods/19-batch6-produce.md)）

## 验收

- [ ] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [ ] 清单里每一项在设计写明的最早档次上能做出，并能沿路线升到更高档次的设备
- [ ] 数据生成检查全部通过（耗时为 4 的倍数、每条配方至少一台设备可执行、唯一性无冲突）
- [ ] 每种成品的品质档随添加剂变化，Tooltip 的品质行与营养成分组件显示正确
- [ ] 本批新增的中间体、容器与模具都有名称、贴图与合成

## 待定

- 无
