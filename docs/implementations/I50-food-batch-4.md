# I50 第 4 批成品（乳制品、冷冻饮品、蛋制品）

| 项 | 内容 |
| --- | --- |
| 状态 | 未开始 |
| 阶段 | E 内容 |
| 依赖 | I49 |
| 原文档 | [foods/15](../foods/15-batch4-dairy.md)、[foods/16](../foods/16-batch4-frozen-eggs.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `content.food4`（数据生成）、`food` |

## 目标

36 种成品可做：17 种乳及乳制品、11 种冷冻饮品、8 种蛋制品，连同奶酪模、冰棍模、纸杯与本模组玻璃瓶。

## 范围

- 做：三类成品的配方与数值核对、本批中间体、冷冻与凝冻的设备分工、鸭蛋与皮蛋类的标签接入
- 不做：模具与容器的注册（[I40](I40-molds-and-containers.md)）；M50、M51 的设备本体（[I41](I41-food-machines-t1.md)、[I42](I42-food-machines-t2.md)）

## 清单

| 节 | 内容 | 项数 | 原文档 |
| --- | --- | --- | --- |
| 发酵乳与稀奶油制品 | 酸奶、风味酸奶、稀奶油、淡奶油、黄油等 | 见原文档 | [foods/15 §3](../foods/15-batch4-dairy.md) |
| 奶酪 | 切达、马苏里拉、奶豆腐等 | 见原文档 | [foods/15 §4](../foods/15-batch4-dairy.md) |
| 炼乳与乳粉 | 甜炼乳、全脂乳粉、调制乳粉等 | 见原文档 | [foods/15 §5](../foods/15-batch4-dairy.md) |
| 中式乳品甜点 | 双皮奶、奶片等 | 见原文档 | [foods/15 §6](../foods/15-batch4-dairy.md) |
| 冰淇淋 | 各档冰淇淋 | 见原文档 | [foods/16 §3](../foods/16-batch4-frozen-eggs.md) |
| 雪糕、冰棍、冰沙与刨冰 | 雪糕、绿豆冰棍、冰沙、刨冰等 | 见原文档 | [foods/16 §4](../foods/16-batch4-frozen-eggs.md) |
| 蛋制品 | 卤蛋、茶叶蛋、溏心蛋、皮蛋、咸鸭蛋、糟蛋、蛋清粉等 | 8 | [foods/16 §5](../foods/16-batch4-frozen-eggs.md) |
| 合计 | 乳 17、冷冻饮品 11、蛋 8 | 36 | — |

## 每一项的通用步骤

1. 物料：成品物品已在 [I21](I21-finished-products.md) 注册，补齐食物属性、堆叠上限与分类标签；中间体与半成品按本批设计文档的“中间体”一节新注册（[I05](I05-materials-registry.md)、[foods/08 §3、§4](../foods/08-recipe-format-and-intermediates.md)）
2. 配方：按卡片的工序逐条写进数据生成，配方类型、模式与设备按 [machines/05 §1、§2](../machines/05-final-design.md)，写法按 [foods/08 §2](../foods/08-recipe-format-and-intermediates.md)
3. 品质与容器：配料段的品质位按 [foods/04 §2](../foods/04-quality.md)，容器与模具按 [machines/05 §3、§4](../machines/05-final-design.md) 与本批的“原料、模具与容器”一节
4. 数值：每份的营养与代谢数据由 [I45](I45-food-nutrition-generation.md) 的输入文件提供，本计划只核对卡片与生成结果一致
5. 检查：JEI 可见、最早档次的设备上能做出、唯一性检查无冲突、容器返还正确

## 任务

- [ ] 按清单逐节写配方；凝乳与凝固走 R11，均质与杀菌走 R19、R21，冷冻与凝冻走 R28
- [ ] 淡奶油等容器统一用本模组玻璃瓶（[foods/15 §1](../foods/15-batch4-dairy.md)、[machines/05 §4](../machines/05-final-design.md)）
- [ ] 鸭蛋接他模组标签，皮蛋、咸鸭蛋、糟蛋同时接受鸡蛋（[foods/16 §1](../foods/16-batch4-frozen-eggs.md)）
- [ ] 卤蛋、茶叶蛋、溏心蛋的亚硝酸盐误用量接 [I45](I45-food-nutrition-generation.md)
- [ ] M50 冰盐冷冻桶在 T1 能做出冰淇淋与冰棍，耗冰与冰加盐的两种模式分别验证

## 验收

- [ ] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [ ] 清单里每一项在设计写明的最早档次上能做出，并能沿路线升到更高档次的设备
- [ ] 数据生成检查全部通过（耗时为 4 的倍数、每条配方至少一台设备可执行、唯一性无冲突）
- [ ] 每种成品的品质档随添加剂变化，Tooltip 的品质行与营养成分组件显示正确
- [ ] 本批新增的中间体、容器与模具都有名称、贴图与合成

## 待定

- 无
