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
- 界面调整（2026-11-05，用户要求）：红石与 I/O 面板的按钮缩到 10 × 10（8 × 8 小图标，`elements.png` 第 132 行，`Widgets.smallButton`），面格 12 × 12；面格通道改为居中的小方块、不写字母，相邻方块写进悬停提示；单个面的高级设置页返回按钮移到面板左上角，标题“高级：<面>”，按“输出范围 / 输入范围”分节、每行文字后跟按钮；面板宽度按文字实际宽度计算；红石面板去掉当前信号行（`MachineView` 不再同步 `powered`）；I/O 面板标题改为“I/O”；右侧面板收起时用物品图标（I/O 为设备扳手、红石为红石粉），展开后不画右上角图标，标题可用到面板右边；顺带修正高级设置页三行时超出面板底部
- 主窗口分区（2026-11-05，用户要求）：`GuiLayout` 改为能量区、原料区、加工区、成品区四个带线框的区（`zones()`，`Widgets.zoneFrame`），区上下对齐、铺满窗口宽度，加工区占剩余宽度；元素放在 18 像素网格上，槽位相邻不留缝、每列上下居中；加工区从上到下是进度箭头、单独线框的纯度（`purity()`，悬停显示完整纯度行）、催化剂；所有机器都用 190 高的窗口；物品槽每列垂直靠上、水平居中，只有能量区上下居中；模式按钮上移 1 像素给线框让位；测试机器 `dev_reactor`（同 M14）、`dev_fermenter`（无能源、两个催化剂）用于核对布局；`GuiLayoutTest` 检查元素在所属区内、区的顺序与对齐、网格、催化剂位置
- 发现、移交：JEI 物品列表会盖住右侧面板（开发环境可按 Ctrl + O 隐藏），已在 I22 增加“侧面板登记为 JEI 额外区域”
- 槽位与储罐悬停显示类型（2026-11-11，用户补充，[machines/04](../machines/04-gui.md)）：`MachineDefinition` 加 `slotNames`（各机器另起的槽位名，未写的按类别“输入”“输出”“催化剂”等）与 `slotFilters`（按标签限定输入槽，手放、漏斗、仓口都判，M30 用）；主窗口与仓口窗口空槽显示类型名，有物品时在提示下加一行灰色类型名（与物品名相同时不重复，如表达载体），储罐提示加“输入储罐 / 输出储罐”
- 运行验证（1.20.1 `runServer` + `runClient`）：M30 四个输入槽分别显示“样本或菌种”“培养基”“供体样本”，空输出槽“输出”；M14 储罐“输入储罐”“输出储罐”、催化剂槽“催化剂”、输入、输出；漏斗往 M30 送泥土、琼脂、生鳕鱼、表达载体各进对应槽，盐被拒

## 待定

- 无。已定（2026-11-05）：贴图先用脚本生成；面板展开状态只在本次游戏内记住；配置器改为设备扳手与红图，不在世界里逐面配置；锁定用 Alt + 单击；JEI 相关两项移到 I22
- 英文名 Device Wrench、Redprint 待审
