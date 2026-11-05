# I10 机器界面与配置器

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | C 框架 |
| 依赖 | I09、I04 |
| 原文档 | [machines/04](../machines/04-gui.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `gui`、`gui.client`、`machine.tool`、`platform.client.gui` |

## 目标

所有机器共用一套界面：主窗口、侧面板、六面配置、锁定、模式、多方块结构页；设备扳手旋转与拆下机器，红图复制粘贴设置。

## 范围

- 做：通用界面组件与面板、设备扳手与红图、配置数据包
- 不做：点击进度箭头打开 JEI、从 JEI 拖入设定锁定（移到 I22，2026-11-05 定）
- 不做：各机器的槽位摆放（随 I14–I17）

## 任务

- [x] 主窗口组件：能源条、储罐条、进度箭头与产出纯度、状态行（[machines/04 §1](../machines/04-gui.md)）
- [x] 侧面板：信息、升级与模块、输入输出、红石；展开状态按机器类型记住，只在本次游戏内有效（2026-11-05 定）（[machines/04 §2](../machines/04-gui.md)）
- [x] 六面配置：十字展开图、物品与流体两条通道、蓝 / 橙 / 蓝橙对角拼色 + 字母（[machines/04 §3.1](../machines/04-gui.md)）
- [x] 高级选项与自动输入输出开关（[machines/04 §3.2–3.3](../machines/04-gui.md)）
- [x] 槽位与储罐锁定：Alt + 单击（2026-11-05 定；[machines/04 §1](../machines/04-gui.md)）
- [x] 设备扳手（旋转、拆下）与红图（复制、粘贴、清空），含配方（2026-11-05 定；[machines/04 §4](../machines/04-gui.md)）
- [x] 界面贴图：脚本生成热力风格的大色块占位贴图（2026-11-05 定）
- [x] 多方块结构页与仓口小窗口（[machines/04 §5](../machines/04-gui.md)）
- [x] 配置修改的服务器校验与增量同步（[machines/04 §6](../machines/04-gui.md)）
- [x] 通用机器菜单类型（自 I09 移入）：`MachineDefinition` 一处声明即有菜单；状态行用 `MachineStatus` 与 `RecipeProcessor.missing()`（[machines/02 §4.2](../machines/02-architecture.md)）
- [x] 拆下时把非默认配置写进物品、放下时恢复（自 I09 移入；`MachineBlockEntity.saveConfig / loadConfig / hasDefaultConfig` 已有，需 platform 的物品自定义数据读写）（[machines/04 §4](../machines/04-gui.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 界面绘制 | 1.20.1 `GuiGraphics` | 1.21.1 `GuiGraphics` | 26.1 渲染改为 extract / submit，需 platform 包装 |
| 包装 | `Canvas`、`PlatformScreen`（`renderBg / renderLabels / render`，点击外部判定 5 个参数） | 同左（不再手动画背景暗化） | `extractBackground / extractLabels / extractRenderState`，`MouseButtonEvent`，点击外部判定 4 个参数，窗口尺寸走构造器 |
| 储罐流体贴图 | `IClientFluidTypeExtensions` | 同左 | `FluidStateModelSet` 与 `FluidModel` 的着色 |
| 物品自定义数据 | 物品 NBT | `minecraft:custom_data` | 同左（`CompoundTag` 读取改为 `getCompoundOrEmpty / getStringOr`） |
| 方块移除时掉落内容 | `Block.onRemove` | 同左 | `BlockEntity.preRemoveSideEffects` |
| 动作栏消息 | `displayClientMessage(…, true)` | 同左 | `sendOverlayMessage` |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 同一机器在三个节点上界面一致，配置在重进世界后保留

## 实现记录

- 代码：`gui`（`ModMenus`、`FIMenu`、`MachineMenu`、`PortMenu`、`MachineContainer`、`MachineView`、`MenuNetwork`、`MenuActions`、`GuiLayout`、`GuiText`）、`gui.client`（`MachineScreen`、`PortScreen`、`Widgets`、`StructureHighlight`、`ClientGui`）、`machine.tool`（`MachineTool`、`DeviceWrenchItem`、`RedprintItem`）、`machine.block.MachineBlockItem`；贴图脚本 `tools/gui/gui_textures.py`（`textures/gui/window.png`、`panel_*.png`、`elements.png` 与两件工具的物品贴图，坐标与 `Widgets` 对应）
- platform 新增：`client.gui.Canvas`（填充、贴图、文字、物品、流体、提示框、变换）、`client.gui.PlatformScreen`（`drawBackground / drawForeground / drawOverlay / onClick / isInsideExtras / appendSlotTooltip / extraAreas`）、`PlatformItemData`（物品上的模组数据）、`PlatformBlockItem`、`PlatformItem.onUseInAir`、`PlatformPlayers.actionBar`、`PlatformFluids.displayName`、`PlatformBlockEntity.onBroken`、`PlatformClient.onClientTick`、`ClientHooks.openMenu / markerParticle / isAltDown / isControlDown`、`Platform.isAltDown / isControlDown`、`PlatformRegistry.blockItem`（自定义类）
- 同步：菜单打开期间服务器每 tick 采集一次 `MachineView`，按 10 组（状态、能量与燃料、进度、储罐、锁定、催化剂、设置、升级、结构、仓口）比较，只发送变化的组；客户端只合并，不计算。客户端动作带菜单编号发往服务器，服务器核对是本玩家打开的菜单、距离有效、取值在范围内后执行
- 菜单：一种通用机器菜单与一种仓口菜单，`MachineDefinition` 无需额外声明；升级与模块槽只在升级面板展开时可点，每格 1 个；结构页隐藏机器槽位
- 输出槽：主产物只进第一个输出槽，副产物进其余输出槽，按此实现“仅主产物 / 仅副产物”（[machines/03 §1](../machines/03-io-layout.md)，2026-11-05）
- 锁定移入设置：与侧面、红石、模式一起保存，拆下与红图都带上锁定；储罐锁定为流体种类，忽略数量
- 修复 I09 遗留：机器被挖掉时内容物不掉落，现在挖掉与扳手拆下都掉落（`PlatformBlockEntity.onBroken`）
- 设备扳手、红图：配方由数据生成（扳手 `I I / C / I`，红图无序），物品模型为手持；英文名 Device Wrench、Redprint 待审
- 开发命令：`/fidev machine <pos> set gui|action|carried|tool|use|useair <玩家> …`（打开界面、按客户端动作修改、设置鼠标上的物品、用工具、模拟对方块或空气右键）
- 运行验证（2026-11-05，三个节点 `runServer` + `runClient`，用合成鼠标点击）：粉碎机界面、信息面板（配方、进度、剩余秒数、速率、能耗、劣质（88%）、产率 110%）、升级面板、输入输出面板（十字图、相邻方块图标、IO 对角拼色、自动输出开关、Alt 高级选项“仅副产物 / 仅主产物”）、红石面板；底面设为仅副产物时主产物不推出，改为仅主产物后推入下方箱子；Alt + 单击锁定输入槽（灰色虚影 + 小锁）；测试燃料结晶器的火焰、两个储罐、空桶取水与倒回、Shift + 右键两次清空；测试多方块结构页（错误坐标、另有 23 处、高亮粒子）、主页无输入输出面板；流体仓与物品仓窗口、方向与自动输出切换；扳手旋转（南 → 西）与拆下（物品带设置、内容物掉落），放下后设置与锁定恢复；红图复制、粘贴到朝东的同类机器（面按朝向换算）、类型不同提示、潜行右键空气清空；重启后设置、锁定、仓口配置都保留；1.21.1 与 26.1 的界面、流体贴图、物品数据、工具配方与 1.20.1 一致
- 单元测试：自动排布在 7 种槽位组合下不越界、不重叠，高度规则，槽位角色顺序，高级选项计入设置（三个节点 58 项通过）
- 发现、移交：JEI 物品列表会盖住右侧面板（开发环境可按 Ctrl + O 隐藏），已在 I22 增加“侧面板登记为 JEI 额外区域”

## 待定

- 无。已定（2026-11-05）：贴图先用脚本生成；面板展开状态只在本次游戏内记住；配置器改为设备扳手与红图，不在世界里逐面配置；锁定用 Alt + 单击；JEI 相关两项移到 I22
- 英文名 Device Wrench、Redprint 待审
