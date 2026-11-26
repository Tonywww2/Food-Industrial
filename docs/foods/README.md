# 成品子系统 · 设计文档

> 项目：Food Industrial（`foodindustrial`）· Stonecutter 三节点：1.20.1 Forge（默认）、1.21.1 NeoForge、26.1.2 NeoForge
> 范围：成品（[../additives/12-finished-products.md](../additives/12-finished-products.md) 的 333 种）的配方、食品中间体、调配香精、食品加工设备与工艺、成品品质与数值、代谢系统。添加剂本身见 [../additives/](../additives/README.md)，机器与配方框架见 [../machines/](../machines/README.md)。

| 文件 | 内容 |
| --- | --- |
| [01-overview.md](01-overview.md) | 成品配方总体设计：已定原则、添加剂效果的分层、品质、数值算法、代谢系统框架、纯度标签、调配香精、食品中间体、食品加工设备与工艺、分批计划、待定问题 |
| [02-metabolism.md](02-metabolism.md) | 代谢系统：时间与身体参数、急性成分（咖啡因、酒精、快吸收糖）、慢性成分（杂质、维生素 C、B1、D）、暂不设的成分、效果的实现通道、显示 |
| [03-nutrition-data.md](03-nutrition-data.md) | 营养数据与代谢配置：数据来源、数据包格式、配置项与命令、配置流程要点 |
| [04-quality.md](04-quality.md) | 品质与数值：品质项与功能类别、品质位、协同表、品质档的合成、饱食度与饱和度、一份的重量、现实配比 |
| [05-equipment.md](05-equipment.md) | 食品加工设备：配料段与加工段、添加剂槽、工艺与配方类型总表、成品的档次参数、第 1 批设备（和面与成型、高压均质与杀菌） |
| [06-product-methods.md](06-product-methods.md) | 成品制作方法（粗略版）与工艺统计：330 种成品的工序链、53 道工序的词表与统计 |
| [07-machine-basic-design.md](07-machine-basic-design.md) | 机器基本设计：53 道工序归并为配方类型（新增 R22–R29）、T0–T2 设备梯度、新机器 M41–M53、现有设备的新增能力、覆盖检查 |
| [08-recipe-format-and-intermediates.md](08-recipe-format-and-intermediates.md) | 终版配方的规则与写法、半成品、共用食品中间体清点、与现有物料重叠的成品、原料缺口 |
| [09-batch1-fats-sweeteners.md](09-batch1-fats-sweeteners.md) | 第 1 批（上）：容器、本批中间体、02 油脂 8 种与 11 甜味料 10 种的终版配方卡片、对现有配方的改动 |
| [10-batch1-condiments.md](10-batch1-condiments.md) | 第 1 批（下）：本批中间体、12 调味品 30 种的终版配方卡片 |
| [11-batch2-flavorings.md](11-batch2-flavorings.md) | 第 2 批：调配香精的风味类型、香料归组方法、香精物品与剂型、各剂型配方、成品一侧的对路判定 |
| [12-flavor-groups.md](12-flavor-groups.md) | 第 2 批附表：1,887 条香料逐条的风味组与依据 |
| [13-batch3-grain-products.md](13-batch3-grain-products.md) | 第 3 批（上）：原料缺口的来源、模具与容器、本批中间体、06 粮食制品 32 种的终版配方卡片、能食用成品的数值 |
| [14-batch3-bakery.md](14-batch3-bakery.md) | 第 3 批（下）：焙烤用的模具、本批中间体、07 焙烤食品 31 种的终版配方卡片、能食用成品的数值 |
| [15-batch4-dairy.md](15-batch4-dairy.md) | 第 4 批（上）：皱胃与菌种、奶酪模与容器、乳的中间体、01 乳及乳制品 17 种的终版配方卡片、第 4 批已定的问题、能食用成品的数值 |
| [16-batch4-frozen-eggs.md](16-batch4-frozen-eggs.md) | 第 4 批（下）：绿豆与鸭蛋、冰棍模与纸杯、冷饮与蛋的中间体、03 冷冻饮品 11 种与 10 蛋制品 8 种的终版配方卡片、能食用成品的数值 |
| [17-batch5-meat.md](17-batch5-meat.md) | 第 5 批（上）：肢解附魔与部位、灌肠嘴与火腿模、肠衣、马口铁罐、聚偏二氯乙烯链、肉的中间体、08 肉制品 24 种的终版配方卡片、第 5 批已定的问题、能食用成品的数值（含亚硝酸盐与误用量） |
| [18-batch5-aquatic.md](18-batch5-aquatic.md) | 第 5 批（下）：虾与鱼皮、鱼肉与鱼糜等中间体、09 水产制品 13 种的终版配方卡片、能食用成品的数值 |
| [19-batch6-produce.md](19-batch6-produce.md) | 第 6 批（上）：新原料与容器、豆腐模、豆浆与豆制品的中间体、臭卤与梅坯、04 果蔬豆类食用菌藻类坚果 50 种的终版配方卡片、大豆蛋白的做法、第 6 批已定的问题、能食用成品的数值 |
| [20-batch6-confectionery.md](20-batch6-confectionery.md) | 第 6 批（下）：糖果模具与胶基、巧克力浆、05 可可制品与糖果 24 种的终版配方卡片、熬糖与调温的温度档、能食用成品的数值 |
| [21-batch7-beverages.md](21-batch7-beverages.md) | 第 7 批（上）：玻璃瓶、铝罐与 PET 瓶、PET 链、茶汤与碳酸水等中间体、14 饮料 34 种与 13 特殊膳食用食品 4 种的终版配方卡片、咖啡因与强化量、能食用成品的数值 |
| [22-batch7-alcohol-snacks.md](22-batch7-alcohol-snacks.md) | 第 7 批（下）：橡木桶与滤纸袋、酿造与蒸馏的中间体、15 酒类 17 种与 16 其他类 17 种的终版配方卡片、酒类的能量与酒精折算、能食用成品的数值 |
| [23-own-sources.md](23-own-sources.md) | 本模组后备原料（草案，待审，2026-11-25）：只接他模组标签的食品原料改由本模组作物、树木、水产与动物后备；料酒所需的黄酒链提前 |
| [agents/food-nutrition-data.md](agents/food-nutrition-data.md) | 给原版与他模组食物配置营养数据的标准流程（AI 执行用，含不臆测的规则）；文件末尾附 Claude Code 子代理入口的内容 |

后续文档按 [01 第 10 节](01-overview.md#10-分批计划) 的分批计划逐份增加。

实装计划：[I34](../implementations/I34-nutrition-data.md) 营养数据、[I35](../implementations/I35-metabolism.md) 代谢系统、[I36](../implementations/I36-vanilla-nutrition.md) 原版与农夫乐事营养数据。

全项目文本遵循 [../text-standard.md](../text-standard.md)；实装顺序与进度见 [../implementations/](../implementations/README.md)。
