# I11 辅助物流方块

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | C 框架 |
| 依赖 | I09 |
| 原文档 | [machines/03 §4–5](../machines/03-io-layout.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `logistics` |

## 目标

没有管道模组的节点上，液体和气体也能存放、在机器之间搬运。

## 范围

- 做：木桶罐、金属储罐、气瓶、输出储罐的排放开关
- 不做：流体管道（2026-11-05 定，移除）；物品导管（用原版漏斗或他模组）

## 任务

- [x] 储罐方块：木桶罐（T1，4,000 mB，只装液体）、金属储罐（T2，32,000 mB，液体或气体，竖直一列共用、最多 8 层）；默认不主动输入输出，空手 Shift + 右键切换自动排出（每 4 tick 向下 400 mB）；他模组管道任意面可进出（2026-11-05 定；[machines/03 §5](../machines/03-io-layout.md)）
- [x] 储罐交互：空手右键显示内容；桶、气瓶等容器右键装入取出；方块内画出液面（2026-11-05 定）
- [x] 储罐物品：挖掉保存流体与纯度、放下恢复；对着空气 Shift + 右键切换放置模式 / 使用模式（对着方块时不切换），使用模式下按 1,000 mB 捞起和放出流体（2026-11-05 定）
- [x] 气瓶物品（1,000 mB，只装气体，不堆叠），机器界面与储罐可用（[machines/03 §4–5](../machines/03-io-layout.md)）
- [x] 输出储罐的排放开关：无行为 / 排出溢出 / 全部排出，每 tick 最多 100 mB，只对气体（2026-11-05 定，自 I15 并入；[machines/03 §4](../machines/03-io-layout.md)，[machines/04 §1](../machines/04-gui.md)）
- [x] 配方（[machines/03 §5](../machines/03-io-layout.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 他模组管道 | 热力、气动可用 | 气动可用 | 无；流体靠相邻自动输入输出、储罐与气瓶 |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 26.1.2 节点上能只用本模组方块与物品搬运气体：相邻自动输出、金属储罐、气瓶（M27、M19 未做时用测试机器代替）

## 实现记录

- 代码：`logistics`（`TankKind`、`TankBlock`、`TankBlockEntity`、`TankBlockItem`、`GasCylinderItem`、`ModLogistics`、`LogisticsData`、`client.LogisticsClient`）；排放开关在 `machine.component.VentMode`、`TankSet`、`MachineBlockEntity`，界面在 `MachineScreen`；贴图脚本 `tools/logistics/logistics_textures.py`
- platform 新增：`PlatformWorldFluids`（看向的方块与流体源、按 1,000 mB 捞起与放置）、`PlatformTankItem`（物品流体容器：1.20.1 `FluidHandlerItemStack`，1.21.1 同名模板 + `foodindustrial:fluid_content` 组件，26.1 `ItemAccessFluidHandler`）、`client.PlatformBlockRenderers`（方块内流体长方体：1.20.1 / 1.21.1 `BlockEntityRenderer.render`，26.1 render state + `submitCustomGeometry`）、`PlatformEntityBlock.adjustDrops` 与 `UseResult.PASS_TO_ITEM`（不走空手交互，直接让手里的物品使用）、`PlatformBlockItem.onUseInAir`、`PlatformItemData.getInt`、`PlatformPlayers.blockReach / entityReach / aimsAtNothing`
- 金属储罐一列：每块存自己的一份，装入从最下层、取出从最上层；放下、挖掉后整列重新从下往上排；放置时检查层数与流体种类，不合规则放置失败并在动作栏提示
- 排放：每批先处理“全部排出”（红石禁止时跳过），机器因流体产物放不下而“输出已满”时处理“排出溢出”；排放后这一批不进入睡眠；排放冒烟
- 排放开关、锁定一起算作机器设置：拆下、红图都带上
- 完成后用户确认（2026-11-05）：金属储罐装入从最下层开始，挖掉一块只带走它自己那份，上面的重新下沉；金属储罐配方用原版玻璃 `minecraft:glass`；排放跟随机器的红石设置，默认“忽略”时红石不影响排放
- 储罐物品的模式切换只在对着空气时进行（2026-11-05 用户要求）：`PlatformPlayers.aimsAtNothing` 在方块交互距离内找方块与流体、在实体交互距离内找实体，都没有才算对着空气；对着方块时 Shift + 右键按当前模式使用（放置模式放下方块，使用模式捞起或放出流体）。1.20.1 实测（2026-11-05）：看地面、看实体时不切换，看天空、看远处空地时切换；对着方块 Shift + 右键放下储罐；使用模式对着水源 Shift + 右键捞起 1,000 mB；三个节点编译通过
- 运行验证（2026-11-05，三个节点 `runServer` + `runClient`，RCON 调用测试命令模拟右键）：木桶罐装满 4,000 mB 后再倒桶被拒绝，自动排出把水送进下方金属储罐；金属储罐一列从下往上装、用桶从上层取出；挖掉一块，掉落物带流体（1.20.1 NBT、1.21.1 / 26.1 `custom_data`），上面那块下沉；在水上面放装岩浆的储罐被拒绝，第 9 层被拒绝；储罐物品 Shift + 右键切换模式，使用模式捞起 1,000 mB 水再放回；测试结晶器产二氧化碳把储罐装满后“输出已满”，切到“排出溢出”后继续出石膏，切到“全部排出”后储罐排空，红石禁止时不排（1.21.1 上测）；气瓶在机器界面装 1,000 mB 二氧化碳（纯度 95.2% 保留），生存模式倒进金属储罐，木桶罐拒收；三个节点的方块内液面（水、岩浆、二氧化碳）与排放按钮显示一致

## 待定

- 有毒气体排放的后果（[additives/11 §待定问题 5](../additives/11-roadmap.md)）
