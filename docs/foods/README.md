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
| [agents/food-nutrition-data.md](agents/food-nutrition-data.md) | 给原版与他模组食物配置营养数据的标准流程（AI 执行用，含不臆测的规则）；文件末尾附 Claude Code 子代理入口的内容 |

后续文档按 [01 第 10 节](01-overview.md#10-分批计划) 的分批计划逐份增加。

实装计划：[I34](../implementations/I34-nutrition-data.md) 营养数据、[I35](../implementations/I35-metabolism.md) 代谢系统、[I36](../implementations/I36-vanilla-nutrition.md) 原版与农夫乐事营养数据。

全项目文本遵循 [../text-standard.md](../text-standard.md)；实装顺序与进度见 [../implementations/](../implementations/README.md)。
