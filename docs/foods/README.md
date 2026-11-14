# 成品子系统 · 设计文档

> 项目：Food Industrial（`foodindustrial`）· Stonecutter 三节点：1.20.1 Forge（默认）、1.21.1 NeoForge、26.1.2 NeoForge
> 范围：成品（[../additives/12-finished-products.md](../additives/12-finished-products.md) 的 333 种）的配方、食品中间体、调配香精、食品加工设备与工艺、成品品质与数值、代谢系统。添加剂本身见 [../additives/](../additives/README.md)，机器与配方框架见 [../machines/](../machines/README.md)。

| 文件 | 内容 |
| --- | --- |
| [01-overview.md](01-overview.md) | 成品配方总体设计：已定原则、添加剂效果的分层、品质、数值算法、代谢系统框架、纯度标签、调配香精、食品中间体、食品加工设备与工艺、分批计划、待定问题 |
| [02-metabolism.md](02-metabolism.md) | 代谢系统：时间与身体参数、急性成分（咖啡因、酒精、快吸收糖）、慢性成分（杂质、维生素 C、B1、D）、暂不设的成分、效果的实现通道、显示 |
| [03-nutrition-data.md](03-nutrition-data.md) | 营养数据与代谢配置：数据来源、数据包格式、配置项与命令、配置流程要点 |
| [agents/food-nutrition-data.md](agents/food-nutrition-data.md) | 给原版与他模组食物配置营养数据的标准流程（AI 执行用，含不臆测的规则）；文件末尾附 Claude Code 子代理入口的内容 |

后续文档按 [01 第 10 节](01-overview.md#10-分批计划) 的分批计划逐份增加。

实装计划：[I34](../implementations/I34-nutrition-data.md) 营养数据、[I35](../implementations/I35-metabolism.md) 代谢系统、[I36](../implementations/I36-vanilla-nutrition.md) 原版与农夫乐事营养数据。

全项目文本遵循 [../text-standard.md](../text-standard.md)；实装顺序与进度见 [../implementations/](../implementations/README.md)。
