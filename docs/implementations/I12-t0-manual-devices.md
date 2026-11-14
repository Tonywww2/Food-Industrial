# I12 T0 手工工具、被动设备与世界交互

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | D 设备 |
| 依赖 | I08、I09、I07、I29 |
| 原文档 | [additives/07](../additives/07-manual-crafting.md)、[machines/01 §2](../machines/01-recipe-types.md)、[additives/10 §2.1](../additives/10-purity-jei-ae2.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `manual`、`world.interaction`、`recipe`、`platform.world` |

## 目标

开局不靠机器就能做出 07 列出的全部手工产物，纯度按原料和工序确定。

## 范围

- 做：H01–H09 工具与被动设备、H11–H15 世界交互、自然接种
- 不做：营火、熔炉等原版设备（I13）

## 任务

- [x] 手持工具 `ManualProcess`：右键一次推进一步，`manual.actions` 步完成（[machines/02 §5](../machines/02-architecture.md)）
- [x] H01 研钵、H02 筛子 / 布袋（两件物品）、H03 锅（陶锅 / 铁锅两件，焦化只认铁锅）（[additives/07 §手工工具](../additives/07-manual-crafting.md)）
- [x] 被动设备：H04 陶坛、H05 淋灰桶、H06 晒盐池（世界结构）、H07 晾晒架、H08 曲盘、H09 熏蒸箱，20 tick 一批（[additives/07 §手工工具](../additives/07-manual-crafting.md)，[machines/02 §6.1 第 3 条](../machines/02-architecture.md)）
- [x] W01 方块转化、W02 割取的配方格式与 `BlockTransformRecipe`、`HarvestRecipe`（自 I08 移入，2026-11-04 定；配方类型 ID 与默认来源规则已在 I08 登记）：格式见 [machines/01 §3.2](../machines/01-recipe-types.md)（2026-11-10 定）（[machines/02 §3](../machines/02-architecture.md)）
- [x] H11 硝土生成（W01，随机刻）；H12 寒冷冻融；H14 冷置析酒石；H15 割取（W02，带冷却）（[additives/07 §世界交互](../additives/07-manual-crafting.md)）
- [x] H13 自然接种：按底物与群系长出确定菌种（[additives/07 §自然接种（H13）](../additives/07-manual-crafting.md)）
- [x] 不带纯度的手工产物不写 `Purity`：苦卤、海藻灰、草木灰、碱液、钾碱液、硝水、松脂、鱼肝油（多组分混合物），麦芽、木瓜乳汁（酶源）（2026-11-05，[additives/10 §1.1](../additives/10-purity-jei-ae2.md)，[additives/07 §手工能做出的第一批产品](../additives/07-manual-crafting.md)）
- [x] 精制配方带 `t0_max`（I29）：手工反复精制到上限后不再升高，例如海盐在 T0 上停在 95.2%（[additives/07 §手工能做出的第一批产品](../additives/07-manual-crafting.md)）
- [x] 环境条件只影响耗时与产量；失败产物（焦糖渣、死烧石膏、霉变姜黄、徒长麦芽；麦芽不带纯度后，徒长麦芽是不能糖化的单独物品）（[additives/10 §2.2 第 6 条](../additives/10-purity-jei-ae2.md)，[additives/07 §手工能做出的第一批产品](../additives/07-manual-crafting.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 交互事件 | 由 platform 事件包装 | 同左 | 同左 |
| 世界数据（割取冷却） | `computeIfAbsent(load, create, name)` | `SavedData.Factory` | `SavedDataType` + 编解码器 |
| 物品数据（工具进度、硫熏标记） | NBT | 数据组件 | 数据组件，写入需要注册表 |
| 方块内物品显示 | `ItemRenderer.renderStatic` | 同左 | `ItemModelResolver` + `ItemStackRenderState` |
| 食物属性 | `saturationMod` | `saturationModifier` | 同左 |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] [additives/07](../additives/07-manual-crafting.md) 两张产品表中每一行都能按所写链条做出，纯度与表中一致（5、7 的海藻灰、草木灰由 I13 的 H10 营火烧灰提供，实测时用命令给出；101 木瓜乳汁暂不做）

## 实现记录

- 代码：`manual`（`T0Kind` 设备种类与内容、`T0Devices` 登记、`T0DeviceBlock`、`T0DeviceBlockEntity`、`DeviceContents` 原地内容、`InPlaceProcess` 原地转化、`HandTool` / `HandToolItem` 手持工具、`Fumigation`、`Tartar`、`ManualData` 资源与合成、`ManualRecipes` 手工链配方、`client.ManualClient`）；`world.interaction`（`BlockTransformRecipe`、`HarvestRecipe`、`FluidBottleItem`、`WorldInteractions`、`WorldInteractionData`）；`recipe`（`Environment`、`EnvironmentFactor`、`DeviceSpec` 的手持限制、`ProcessRecipe.environment_factors` 与 `specificity()`）；`logistics`（木桶罐析酒石）；`world`（姜黄、辣椒两种作物与野生植株）；贴图脚本 `tools/manual/manual_textures.py`；开发命令 `devtest.DevManualCommands`（`/fidev t0 show|use|advance`、`/fidev tool`、`/fidev env`、`/fidev tartar`）
- platform 新增：`world.PlatformEnvironment`（露天、降雨、白天、群系温度与标签）；`world.PlatformRandomTicks` 与 Mixin `BlockStateBaseMixin`（让泥土、砂土参与随机刻，按注册名判断）；`world.PlatformFluidPickup` 与 Mixin `LiquidBlockMixin`（海洋、海滩群系的水装桶得海水桶，发射器同样生效）；`PlatformSavedData`（随维度保存的数据，带注册表）；`PlatformEvents.onServerLevelTick`、`onRightClickBlock`；`PlatformItems.damage`、`food`；`PlatformTags.block`、`entityTag`、`entityMatches`；`PlatformEntityBlock.shape`、`onNeighborShape`；`client.PlatformClient.onItemTooltip`；`client.PlatformBlockRenderers.registerContents`（方块内的液面与物品）；`PlatformDatagen` 实体标签
- 原地转化：被动设备每 20 tick 一批（相位按坐标错开），每批按位置重新计算环境；锅每搅拌一次推进一步；同一内容取条件最多的配方（催化剂算条件），一样多按配方 ID；配方完成后立即清空“当前配方”，状态栏用预览显示接下来会做什么及缺少的条件。设备只收能作为本设备某条配方原料或催化剂的物品；手持容器认桶（1,000 mB）和液体瓶（250 mB）
- 配方生成：`RecipeGen.Output.parse` 读 JSON 写的配方，原料写成字符串；26.1 的数据生成中标签尚未绑定，先用占位物品解码，再换回按数据生成注册表构造的原料
- 内容：手工链 65 条配方（见 [additives/07 §手工链配方（I12）](../additives/07-manual-crafting.md)）；工具与设备的合成（淋灰桶另有羊毛做法）、燧石小刀（耐久 64）、液体瓶、鳕鱼取鱼肝；W01 硝土、W02 乳胶与松脂；苹果干、浆果干先登记为食物（成品属 I21）
- 文档：07 新增手工链配方表、陶坛 2 个储罐、液体瓶、木炭层暂不做；08、09、10 新增中间物与产物（石花菜煮液、栀子煮液、蛋黄浸液、蓼蓝发酵液、果酒、蛋黄、熟蛋黄、浸水 / 发芽 / 徒长小麦、蒸米、蒸麦、杂菌、焦糖渣、死烧石膏、胶乳凝块、湿胶片、甜菊干叶、干辣椒、辣椒粉、煮姜黄、姜黄干片、霉变姜黄、栀子碎、琼脂、甜菊糖苷、栀子黄，姜黄、辣椒两种作物）；machines/01 §3.2 W02 产物可为液体
- 验证：三个节点 `compileJava`、`runData`、`test` 通过（每个节点 74 项单元测试，新增 `EnvironmentTest`），三个节点生成的 65 条手工配方与纯度报表一致：乙酸 4.0%、琼脂 76%、姜黄 3.0% 为食品级，海盐、硝酸钾在 T0 停在 95.2%，碳酸钠、碳酸钾停在 90.6%
- 1.20.1 实测（`runServer` + `runClient`，测试命令模拟右键，另用真实点击验证割取、刮酒石、陶坛封口、研钵叠放）：果酒封口 2 天成酒，开口 3 天得醋母 20% 与乙酸 4.0%，加醋母 2 天成醋，寒冷群系 4 天；四格相连的晒盐池倒 4 桶海水平均分配，各出盐 83% 与苦卤 250 mB，液体瓶取 250 mB，Shift + 右键一次取走整片的盐；沙漠中半天出盐、苦卤 375 mB；陶锅重结晶 83% → 90.6%；淋灰桶出碱液流进下方木桶罐，铁锅熬出纯碱 20%；晾晒架苹果、浆果（4 个一份，3 个不收）、煮姜黄晒干，下雨时煮姜黄霉变；熏蒸箱烧 1 份硫给苹果干加硫熏标记；雪原中石花菜冻 4 天成琼脂 76%，平原中显示“缺少：寒冷群系”；曲盘蒸米在丛林、竹林、其他群系（遮光）分别得红曲霉、米根霉、米曲霉，露天得杂菌，浸水小麦发芽后再放 2 天得徒长麦芽；陶坛盐水腌胡萝卜得乳酸 1.2% 与老卤，乳胶加一瓶醋得胶乳凝块，蓼蓝发酵加熟石灰得靛青 2.1%，熟蛋黄泡果酒得蛋黄浸液；布袋压湿胶片、研钵磨石膏 81%、筛子滤石花菜煮液（还回空桶）与分蛋；铁锅糖色 30% 再搅拌成焦糖渣，陶锅 8 个石膏先全部成熟石膏再死烧，鱼肝油继续熬炼减为 750 mB；屋顶下的泥土在随机刻加速时变成硝土、露天的不变，20 只牲畜的棚里 75 秒内有地面变成硝土；海洋群系发射器取水得海水桶，平原得水桶；割取云杉原木得一瓶松脂（液体瓶 250 mB）、刀耗 1 点耐久，冷却中再割无效；寒冷群系木桶罐葡萄酒积满 4 份酒石后用刀刮下（80%）
- 1.21.1、26.1.2 实测（同一套命令与真实点击）：果酒、醋、晒盐池、铁锅糖色、晾晒与熏蒸、淋灰桶、研钵、海水桶、硝土、割取都与 1.20.1 一致，方块内物品显示正常，26.1.2 停服时割取冷却数据保存无错误
- 运行中发现并已改：空的液体瓶不被当作容器而被放进设备格（改为同时试 1,000 mB 与 250 mB）；设备收任意物品（改为只收配方原料与催化剂）；配方完成后状态栏仍显示上一条配方的进度；26.1.2 写硫熏标记、工具进度、世界数据需要注册表；26.1.2 实体类型没有 `is(TagKey)`、蜂蜜音效是 `Holder`（改用 `builtInRegistryHolder()` 与装瓶音效）；手持工具英文说明超过 60 字符

- 完成后用户确认（2026-11-10）：下列各项全部通过
  - 数值：各配方的耗时、搅拌与操作次数、数量（07 手工链配方表）；硝土随机刻 1%、牲畜 10%；割取 250 mB、冷却 6,000 tick；熏蒸 1,200 tick；燧石小刀耐久 64；苹果干 4 饥饿值 / 0.6 饱和度、浆果干 4 / 0.4；工具与设备的合成配方
  - 草拟的中间步骤：果酒（苹果、浆果）与葡萄酒、饱和食盐水、浸水小麦、蒸米与蒸麦、筛子分蛋、熟蛋黄、栀子碎、辣椒粉、湿胶片
  - 实装时的取舍：
    - 液体瓶代替乳胶瓶、松脂瓶（任何液体都能装 250 mB）
    - 陶坛 2 个储罐（乳胶加醋需要两种液体）
    - 晒盐池配方把苦卤写在前面，使“炎热群系苦卤 ×1.5”落在苦卤上
    - 并入前后步骤的工序：1、69 的过滤，84 的静置取沉淀，107 的静置取上层油
    - 甜菊叶浸液、辣椒油、栀子染液做成固体物品（甜菊糖苷 1.5%、辣椒红素 0.2%、栀子黄 0.2%），没有单独的液体
    - 硝水熬硝时加 1 份草木灰（锅只有一个储罐，不用草木灰水）
    - 湿胶片晾干不要求环境
    - 开口陶坛的果酒自然接种时同时得到乙酸（醋母 + 醋）；陶坛开口时果酒会变醋，浸提蛋黄需封口
    - 鳕鱼在工作台整条换成鱼肝
    - 鱼肝油熬炼过久：做完后继续搅拌，每 8 次 1,000 mB 减为 750 mB
    - 状态栏“缺少”列出条件最多、被挡住的那条配方缺的条件（如开口陶坛里的盐水腌菜显示“缺少：隔绝空气”）
  - 未做：H10 营火烧灰属 I13（2026-11-10 已在 I13 完成）；JEI 类别属 I22；H05 木炭层（07 §暂不做）

## 待定

- 无。操作与实现规格见 [additives/07 §操作与实现规格（I12）](../additives/07-manual-crafting.md)（2026-11-10 定）；H15 木瓜乳汁暂不做
