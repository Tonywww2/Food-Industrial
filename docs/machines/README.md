# 机器系统 · 设计文档

> 项目：Food Industrial（`foodindustrial`）· Stonecutter 三节点：1.20.1 Forge（默认）、1.21.1 NeoForge、26.1.2 NeoForge
> 范围：机器与配方的组织方式、程序架构与性能规范、槽位、界面。各添加剂用到哪台机器见 [../additives/06-machines.md](../additives/06-machines.md)。

| 文件 | 内容 |
| --- | --- |
| [01-recipe-types.md](01-recipe-types.md) | 19 种通用配方类型 + 2 种世界交互类型；设备档次参数（速率、纯度加成、产率）与默认纯度规则；37 → 29 台的合并方案与旧编号对照；模块总表 |
| [02-architecture.md](02-architecture.md) | 分层与包结构、配方与设备的类关系、处理状态机、**性能规范（强制）**、platform 需新增的 API |
| [03-io-layout.md](03-io-layout.md) | 槽位角色、容量、每台机器的物品 / 流体 / 专用槽、气体规则、辅助方块 |
| [04-gui.md](04-gui.md) | 主窗口、侧面板、六面输入输出配置、配置器、多方块界面、语言键 |

实装顺序与进度见 [../implementations/](../implementations/README.md)。

核心约定：

1. 配方不绑定机器，只声明条件；纯度由配方的工序规则和输入纯度决定，设备档次参数决定速率、纯度加成、产率。同一设备、同一配方、同一输入纯度结果唯一，AE2 能稳定自动化。
2. 机器逻辑 4 tick 一批，按类型错开相位；无事可做的机器睡眠，事件唤醒。
3. 版本差异只写在 `platform` 包，业务层用自己的数据类型（`FluidAmount`、`MachineInventory`、`EnergyBuffer`），在能力边界转换。
4. 气体按流体处理，不设第二套存储。
5. 机器与配方系统不针对食品：以后扩展到药物、日用化学品等其他化工产品时，只新增物料、配方和必要的能力或模块，不改框架（02 第 8 节）。
