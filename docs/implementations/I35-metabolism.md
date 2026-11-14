# I35 代谢系统

| 项 | 内容 |
| --- | --- |
| 状态 | 进行中 |
| 阶段 | E 内容 |
| 依赖 | I34、I01、I04 |
| 原文档 | [foods/02](../foods/02-metabolism.md)、[foods/03 §3.1](../foods/03-nutrition-data.md)、[foods/01 §9](../foods/01-overview.md)、[text-standard §3.3](../text-standard.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `food.metabolism`（新包）、`platform`（玩家数据、属性、事件、客户端钩子）、`platform/mixin`、`platform/compat/apothic_attributes`、`devtest` |

## 目标

玩家吃喝后，每份的代谢成分（[I34](I34-nutrition-data.md) 的数据，加上成品“土法”“劣质”带的杂质）进入体内，按现实的消除规律随世界时间下降；到档时修改玩家属性或给特殊效果，退档即恢复；死亡、慢性部分与失去意识的各项效果按配置；物品 Tooltip、背包右侧的“体内”面板、动作栏与 Apothic Attributes 的属性面板显示。

## 范围

- 做：玩家代谢数据与保存、结算、七种成分（[foods/02 §3](../foods/02-metabolism.md)）、各档效果、配置项、显示、Apothic Attributes 属性面板的来源图标、开发命令、单元测试
- 不做：营养数据的加载与命令（[I34](I34-nutrition-data.md)）；原版与农夫乐事的数值（[I36](I36-vanilla-nutrition.md)）；成品“土法”“劣质”标签的写入（成品各批的计划，本计划只读）；暂不设的成分（[foods/02 §6](../foods/02-metabolism.md)）

## 实现约定

数值全部取自原文档，集中在一个常量类，不在别处写死。

| 项 | 取值 | 原文档 |
| --- | --- | --- |
| 时间 | 1 游戏小时 = 1,000 tick，1 游戏天 = 24,000 tick | [foods/02 §2](../foods/02-metabolism.md) |
| 时间差 | 取主世界“一天中的时间”与“游戏刻”两者差值中的较大者；离线期间不计 | [foods/02 §2](../foods/02-metabolism.md) |
| 身体 | 体重 70 kg，含水系数 r = 0.68 | [foods/02 §2](../foods/02-metabolism.md) |
| 咖啡因 | 一级消除，半衰期 4,000 tick | [foods/02 §4.1](../foods/02-metabolism.md) |
| 酒精 | 吃下时血液酒精浓度（%）加 酒精克数 ÷（70,000 × 0.68）× 100；零级消除，每 1,000 tick 降 0.015 个百分点，不低于 0 | [foods/02 §4.2](../foods/02-metabolism.md) |
| 快吸收糖 | 每次进食一条记录，2,000 tick 后移出 | [foods/02 §4.3](../foods/02-metabolism.md) |
| 杂质 | 每份“土法”0.2、“劣质”2；一级消除，半衰期 720,000 tick | [foods/02 §5.1](../foods/02-metabolism.md) |
| 营养素 | 储备天数每 24,000 tick 减 1；吃下时加 含量 ÷ 每日需要量，不超过上限。每日需要量与上限：维生素 C 100 mg、30 天；B1 1.4 mg、14 天；D 15 µg、30 天 | [foods/02 §5.2–5.5](../foods/02-metabolism.md) |
| 缺乏 | 储备降到 0 为第一档；储备为 0 持续满一个上限的时长为第二档；储备回升即解除 | [foods/02 §5.2](../foods/02-metabolism.md) |
| 日照 | 每累计 1,000 tick 日照，维生素 D 加 1 天 | [foods/02 §5.5](../foods/02-metabolism.md) |
| 档位 | 按下限判定，相邻两档之间不留空档（如酒精 0.05%–0.06% 之间仍按 0.01% 那一档） | [foods/02 §4](../foods/02-metabolism.md) |
| 修正 | 每个成分对每个属性单独一个修正；本系统内同一属性的百分数修正相加（`ADD_MULTIPLIED_BASE`，1.20.1 为 `MULTIPLY_BASE`）；最大生命为加减固定值 | [foods/02 §7](../foods/02-metabolism.md) |
| 初始 | 新玩家营养素储备为上限，其余为 0 | [foods/02 §5.2](../foods/02-metabolism.md) |

## 任务

### 一 platform 新增（[foods/02 §7](../foods/02-metabolism.md)）

API 名称见“节点差异”，实装时按目标版本的反编译源码核对（[CONVENTIONS §4](../../CONVENTIONS.md)）。

- [x] 玩家数据：`PlatformPlayers` 增加按名称登记的玩家数据，服务端保存、随玩家存档；死亡后复制与否由业务决定（第四节）
- [x] 玩家事件：登录、退出、重生（新旧实体与是否因死亡）、换维度、每 tick 结束（服务端，每名玩家）
- [x] 吃喝完成：`onItemConsumed(ServerPlayer, ItemStack)`，物品为吃之前的样子；原版蛋糕经 mixin `CakeBlock#eat` 在吃下一口时回调，物品为蛋糕（[foods/03 §2](../foods/03-nutrition-data.md)：分口吃的方块以一口为一份）
- [x] 农夫乐事的派（放置后分口吃）：按农夫乐事源码找到吃一口的方法，经 `platform/compat` 回调；找不到稳定的钩子时列入待审
- [x] 属性修正：`PlatformAttributes`，以语义枚举（攻击伤害、攻击速度、移动速度、最大生命、挖掘速度、跳跃高度、摔落伤害、受到的治疗）加、改、删带 ID 的临时修正（不存档；登录、重生、换维度后重加）。1.20.1 没有的挖掘速度、跳跃高度、摔落伤害，以及各节点都没有原版属性的受到的治疗，由 platform 记下每名玩家的倍率，在对应事件里套用；装有 Apothic Attributes 时，1.20.1 的挖掘速度改挂它的 `attributeslib:mining_speed`，三个节点的受到的治疗改挂它的 `healing_received`，本模组的事件不再套用这两项（第七节）
- [x] 治疗量钩子：减少全部来源的治疗（[foods/02 §5.3](../foods/02-metabolism.md)，2026-11-13 定）
- [x] 饥饿消耗：mixin `Player#causeFoodExhaustion`，把消耗量交业务缩放
- [x] 睡眠：上床检查钩子，业务返回不能入睡时取消，并在动作栏提示一行
- [x] 饱食度：减饱食度并清空饱和度（呕吐用）
- [x] 伤害：按伤害类型 ID 造成伤害（酒精中毒用）
- [x] 世界时间：主世界的一天中的时间与游戏刻（结算用）
- [x] 日照判定：白天、玩家头部所在格头顶无遮挡、天空光照 15（[foods/02 §5.5](../foods/02-metabolism.md)）
- [x] 限制行动：失去意识时按配置取消移动与跳跃、攻击、破坏与放置方块、使用物品、打开容器（[foods/03 §3.1](../foods/03-nutrition-data.md)），各节点用对应的交互事件
- [x] 客户端：视角摇晃（相机角度事件）、全屏遮罩（界面层）、生存背包界面加按钮与面板（界面初始化与绘制事件）

### 二 数据与结算（[foods/02 §2](../foods/02-metabolism.md)、[§3](../foods/02-metabolism.md)）

- [x] `MetabolismState`：咖啡因（mg）、血液酒精浓度（%）、快吸收糖记录（克数、吃下的时刻）、杂质、三种营养素的储备天数与“储备为 0 的起始时刻”、日照累计、上次结算时刻（一天中的时间与游戏刻各一）、当前各档（判断跨档）、上次呕吐与上次中毒伤害的时刻；经 `core.data` 的 `DataWriter`、`DataReader` 存取
- [x] 结算：每名玩家每 20 tick 一次，另在进食、登录、退出、重生、配置变化时立即结算；按时间差用解析式一次算完（一级：乘以 `0.5^(Δt / 半衰期)`；零级：减去 `速率 × Δt`，不低于 0），不逐 tick 模拟；结算路径不分配对象（[machines/02 §6](../machines/02-architecture.md)）
- [x] 时间差：取主世界“一天中的时间”与“游戏刻”两者差值中的较大者：睡觉跳过的夜晚照算，关闭昼夜循环时按游戏刻继续，时间倒退时按游戏刻正常流逝（[foods/02 §2](../foods/02-metabolism.md)，2026-11-13 定）
- [x] 离线不计：退出时结算一次；登录时把两个“上次结算时刻”设为当前时刻，不结算离线期间（2026-11-13 定）
- [x] 进食：查 `Nutrition.of(stack)`（I34），加入咖啡因、酒精、快吸收糖与三种营养素；没有数据的物品不计入（[foods/03 §1](../foods/03-nutrition-data.md)）
- [x] 杂质来源：读物品的“土法”“劣质”标签（接口 `food.ProductGrade`；标签的写入与数据键随成品各批的计划定，在那之前只经开发命令测试，[foods/01 §5](../foods/01-overview.md)）

### 三 效果

- [x] 咖啡因（[foods/02 §4.1](../foods/02-metabolism.md)）：≥ 75 mg 挖掘速度 +10%、攻击速度 +5%；≥ 100 mg 不能入睡；210–420 mg 另加饥饿消耗 −15%；> 400 mg 取消前述增益，攻击伤害 −10%
- [x] 酒精（[foods/02 §4.2](../foods/02-metabolism.md)）：0.01% 起攻击速度 −5%；0.06% 起改为移动速度 −15%、攻击速度 −15%，客户端视野摇晃；> 0.20% 每游戏小时呕吐一次（失去 3 点饱食度与全部饱和度）；> 0.30% 失去意识，各项效果按配置（默认画面变黑，不能移动、跳跃、攻击、破坏与放置方块、使用物品、打开容器，受伤不醒），降回 0.30% 以下醒来；≥ 0.40% 每游戏小时受 2 点酒精中毒伤害
- [x] 伤害类型 `foodindustrial:alcohol_poisoning`：数据生成写伤害类型 JSON（`Kind.DATA`）、标签 `minecraft:bypasses_armor`（无视护甲，2026-11-13 定）与死亡消息的语言键
- [x] 快吸收糖（[foods/02 §4.3](../foods/02-metabolism.md)）：窗口内合计 ≥ 30 g 时饥饿消耗 −20%；与咖啡因的耐力合计不超过 −30%
- [x] 杂质（[foods/02 §5.1](../foods/02-metabolism.md)）：≥ 10 攻击伤害 −10%、挖掘速度 −10%；≥ 30 另加移动速度 −10%；≥ 60 另加最大生命 −4
- [x] 维生素 C（[foods/02 §5.3](../foods/02-metabolism.md)）：第一档攻击伤害 −10%、受到的全部治疗 −50%；第二档另加最大生命 −4
- [x] 维生素 B1（[foods/02 §5.4](../foods/02-metabolism.md)）：第一档移动速度 −15%、跳跃高度 −20%；第二档移动速度改为 −30%
- [x] 维生素 D（[foods/02 §5.5](../foods/02-metabolism.md)）：日照每满 1,000 tick 加 1 天；第一档摔落伤害 +50%；第二档另加攻击伤害 −10%
- [x] 修正的划分与合成（[foods/02 §7](../foods/02-metabolism.md)，2026-11-13 定）：每个成分对每个属性单独一个修正，ID 由成分与属性得出（1.20.1 由名称派生固定 UUID，1.21 起 `foodindustrial:metabolism/<成分>/<属性>`）；本系统内同一属性的百分数修正相加；最大生命降低后当前生命不超过新上限
- [x] 跨档提示：升档、降档都在动作栏提示一行（`PlatformPlayers.actionBar`）；措辞由实装拟定后列入待审

### 四 配置（[foods/03 §3.1](../foods/03-nutrition-data.md)）

- [x] 在 `FoodIndustrial.init()` 中以 `PlatformConfig.commonBoolean` 声明 `[metabolism]` 节的十个键：`clear_on_death`、`impurity`、`nutrients`、`unconscious_screen`、`unconscious_movement`、`unconscious_attack`、`unconscious_blocks`、`unconscious_use_items`、`unconscious_containers` 默认 `true`，`unconscious_wake_on_damage` 默认 `false`；注释用英文（同现有配置项）
- [x] 死亡：`clear_on_death` 为 `true` 时新实体从初始状态开始（体内量清零、营养素储备回到上限、缺乏计时清零）；为 `false` 时整份复制
- [x] 开关：`impurity` 关闭时不计入、不显示，效果移除，体内量归零；`nutrients` 关闭时不计入、不消耗、不显示，效果移除，储备回到上限；重新开启从初始状态开始，不补算关闭期间。结算时检查开关的变化
- [x] 失去意识：七个 `unconscious_*` 键逐项决定画面变黑、移动与跳跃、攻击、破坏与放置方块、使用物品、打开容器的限制，以及受伤是否醒来；醒来后重新判定，体内量仍在 0.30% 以上则再次失去意识
- [x] 多人：以服务端的配置为准；同步给客户端的状态带开关的取值，客户端据此隐藏或绘制

### 五 显示（[foods/02 §8](../foods/02-metabolism.md)）

- [x] 同步：服务端把本人的状态发给本人的客户端（`PlatformNetwork.toClient`）；登录、重生、换维度、配置变化时全量发送，结算后显示值（按显示精度取整）或档位有变化时发送
- [x] 营养成分 Tooltip：写法照 [text-standard §3.3](../text-standard.md)（2026-11-13 定，同日改为图标组件），有营养数据的物品（含原版、他模组）默认显示图标行，按住 Shift 换成表格（读 I34 同步到客户端的表）
- [x] “体内”面板（2026-11-13 定）：生存背包界面加一个按钮，点开后面板显示在背包右侧，不遮挡左侧的配方书；创造模式背包不显示。列各成分的当前值与档位、营养素的储备天数，关闭的部分不显示，成分前画该成分的图标（第七节）
- [x] 与其他界面共处：按钮在副手栏正上方（2026-11-13 用户要求），避开 Apothic Attributes 的按钮（背包内玩家模型框的右上角，其 26.1 版位置可在它的配置里调整）；面板区域经 JEI 插件报给 JEI（`IGuiContainerHandler#getGuiExtraAreas`，接入方式同 [I22](I22-jei.md)；Apothic Attributes 对它的面板也是这样做的），JEI 的物品列表随之让开
- [x] 视野摇晃、画面变黑：客户端按同步来的档位与配置绘制
- [x] 文本：成分名、档位名、跨档提示、面板标题、不能入睡的提示、死亡消息，`zh_cn`、`en_us` 同时存在，经 `Lang` 生成；默认可见处不写解释（[CONVENTIONS §9](../../CONVENTIONS.md)、[text-standard](../text-standard.md)）

### 六 开发命令与测试

- [x] 开发命令（`devtest`，只在开发环境注册）：`/fidev metabolism show [玩家]`、`set <成分> <值>`、`eat <物品> [份数]`（按 I34 的数据计入，不扣物品）、`grade <土法|劣质> [份数]`（模拟带标签的成品）、`advance <tick>`（按经过这么长时间结算）、`reset`
- [x] 单元测试（`src/test/java`，只测不依赖游戏的计算）：咖啡因 4,000 tick 减半；500 mL、4% 的啤酒含酒精约 15.8 g、折合约 0.033%（[foods/02 §4.2](../foods/02-metabolism.md)）；酒精每 1,000 tick 降 0.015 个百分点；快吸收糖的窗口与 −30% 封顶；每天三份“土法”的杂质稳态约 26、“劣质”约 260（[foods/02 §5.1](../foods/02-metabolism.md)）；营养素的消耗、上限与两档缺乏的起点；时间差取较大者（睡觉、关闭昼夜循环、时间倒退三种情形）；各档的边界

### 七 Apothic Attributes 联动（2026-11-13 用户要求，[foods/02 §7](../foods/02-metabolism.md)）

装有 Apothic Attributes 时，它的属性面板列出每个属性的修正，并在每条修正前画来源图标；认不出来源的修正画问号。本系统的修正要显示自己的图标。

- [x] 依赖：三个节点以可选依赖加入（[CONVENTIONS §6](../../CONVENTIONS.md)）：1.20.1 用 Forge 版 1.3.7（模组 ID `attributeslib`，包 `dev.shadowsoffire.attributeslib`）；1.21.1 用 2.11.0、26.1.2 用 3.1.0（模组 ID `apothic_attributes`，包 `dev.shadowsoffire.apothic_attributes`）。三者都依赖 Placebo，开发运行时一并加入；坐标取 Modrinth 的版本 ID
- [x] 来源类型：`platform/compat/apothic_attributes` 在客户端注册一个 `ModifierSourceType`（`ModifierSourceType.register`）。`extract(LivingEntity, …)` 遍历玩家各属性上本模组的修正，按 ID 反查成分，交出带成分的 `ModifierSource`；`getPriority` 排在装备（0）与药水效果（100）之后，取 200；`ModifierSource` 按成分顺序比较，`render` 在面板为图标留的 9×9 格子里画该成分的图标（做法同它自带的物品来源：16×16 按半尺寸画）
- [x] 图标（2026-11-13 定）：每种成分一个，共 7 个（咖啡因、酒精、快吸收糖、杂质、维生素 C、维生素 B1、维生素 D），16×16，放 `assets/foodindustrial/textures/gui/metabolism/<成分>.png`，“体内”面板共用；画法（手绘或由 `tools/textures` 生成）由实装拟定后列入待审
- [x] 同步：Apothic Attributes 在初始化时把玩家的全部属性设为同步（`setSyncable(true)`），服务端加的修正在客户端可见，联动不另发数据
- [x] 承载属性（2026-11-13 定）：装有时，1.20.1 的挖掘速度改挂 `attributeslib:mining_speed`（它在挖掘速度事件中乘上该属性值；1.21 起原版有 `block_break_speed`，不需要），三个节点的受到的治疗改挂 `healing_received`（它在治疗事件中乘上该属性值），本模组的事件随之不套用这两项，避免算两次；未装时仍走本模组的事件。跳跃高度、摔落伤害在 1.20.1 走事件，面板里不显示；饥饿消耗、不能入睡等不是属性的效果在任何节点都不进面板
- [x] 未装 Apothic Attributes 时不加载联动类（`Platform.isModLoaded`，[CONVENTIONS §2.1](../../CONVENTIONS.md)）

### 八 代谢速率属性（2026-11-13 用户要求，[foods/02 §2](../foods/02-metabolism.md)）

- [x] platform：`PlatformAttributes.registerPlayer(name, 默认值, 下限, 上限)` 注册本模组的玩家属性 `foodindustrial:<name>`（`RangedAttribute`，设为同步；26.1 起倾向为中性），经实体属性修改事件加给玩家；`value(玩家, 属性)` 读取（玩家没有该属性时取默认值）
- [x] `foodindustrial:metabolic_rate`：默认 1.0，范围 0–100（用户定）；结算时把经过的世界时间乘以速率作为体内时间（不足 1 tick 的部分留到下次），全部随时间的变化（消退、消耗、缺乏转重、每小时的呕吐与中毒）都随之变快变慢；日照合成维生素 D 按实际时间；开发命令 `advance` 也按速率
- [x] 显示（用户定）：同步时附上速率；面板的倒计时、储备天数、缺乏天数、呕吐与中毒的间隔秒数按实际时间换算；速率为 0 时不显示倒计时；速率不是 1 时悬浮卡片末尾加一行“代谢速率 ×0.5”；食物 Tooltip 的“日需”不变
- [x] 文本：属性名“代谢速率 / Metabolic Rate”，Apothic Attributes 的说明行 `….desc`“体内成分消退、营养素消耗的快慢”，卡片行“代谢速率 ×%s”
- [x] 单元测试：速率 0.5 时咖啡因 8,000 tick 减半、速率 2 时酒精半小时降 0.015 个百分点、营养素一天耗 2 天、速率 0 不变；不足 1 tick 的部分累计

## 节点差异

名称为候选，实装时按反编译源码核对。

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 玩家数据 | Forge 能力（`AttachCapabilitiesEvent`）或实体的持久 NBT，死亡时在 `PlayerEvent.Clone` 中处理 | 数据附件（`AttachmentType`），死亡时在 `PlayerEvent.Clone` 中按配置处理 | 同 1.21.1；存取按 26.1 的 `ValueInput`、`ValueOutput` |
| 吃喝完成 | `LivingEntityUseItemEvent.Finish` | 同名事件 | 同名事件 |
| 蛋糕 | mixin `CakeBlock#eat` | 同左 | 同左 |
| 属性修正 | `AttributeModifier(UUID, 名称, 值, Operation)`，属性为 `Attribute` | `AttributeModifier(ResourceLocation, 值, Operation)`，属性为 `Holder<Attribute>`，运算名改为 `ADD_VALUE` 等 | ID 为 `Identifier` |
| 挖掘速度 | `PlayerEvent.BreakSpeed` | 属性 `player.block_break_speed` | 同左（1.21.2 起属性 ID 去掉前缀） |
| 跳跃高度 | `LivingEvent.LivingJumpEvent` 调整起跳速度 | 属性 `generic.jump_strength` | 同左 |
| 摔落伤害 | `LivingFallEvent` 的伤害倍率 | 属性 `generic.fall_damage_multiplier` | 同左 |
| 治疗 | `LivingHealEvent` | 同名 | 同名 |
| 饥饿消耗 | mixin `Player#causeFoodExhaustion` | 同左 | 同左 |
| 不能入睡 | `PlayerSleepInBedEvent` | `CanPlayerSleepEvent` | 核对 |
| 视野摇晃 | `ViewportEvent.ComputeCameraAngles` | 同名 | 同名 |
| 画面变黑 | `RegisterGuiOverlaysEvent` | `RegisterGuiLayersEvent` | 同 1.21.1，绘制按 26.1 的界面接口 |
| 背包面板 | `ScreenEvent.Init.Post`、`ScreenEvent.Render.Post` | 同名 | 同名 |
| JEI 让开面板 | `IGuiContainerHandler#getGuiExtraAreas`，区域为 `Rect2i` | 同左 | 核对 |
| 伤害类型 | `data/<ns>/damage_type/`，标签 `data/minecraft/tags/damage_type/` | 同左 | 同左 |
| Apothic Attributes | 1.3.7；修正以 UUID 区分；`render(GuiGraphics, …)` | 2.11.0；修正以 `ResourceLocation` 区分；`render(GuiGraphics, …)` | 3.1.0；修正以 `Identifier` 区分；`render(GuiGraphicsExtractor, …)` |
| 标识符 | `ResourceLocation` | `ResourceLocation` | `Identifier` |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 单元测试通过（`MetabolismStateTest` 16 项，三个节点）
- [ ] 三个节点游戏内（开发命令配合 I34 的测试数据包）：咖啡因三档与不能入睡；酒精五档（视野摇晃、呕吐、失去意识与醒来、中毒伤害无视护甲、死亡消息）；快吸收糖的窗口与封顶；杂质三档；三种营养素的两档缺乏与恢复，维生素 C 缺乏时药水与自然恢复都减半；晒太阳补维生素 D；睡一觉、关闭昼夜循环后照常结算；下线再上线不扣离线期间
  - 已测（2026-11-13）：1.21.1 吃曲奇（真吃与开发命令）计入咖啡因与快吸收糖、动作栏跨档提示；喝蜂蜜瓶计入快吸收糖（专用服务器）；吃一口蛋糕计入一份；咖啡因 ≥ 100 mg 不能入睡并提示；失去意识时画面变黑、不能破坏方块，醒来后恢复；1.20.1 呕吐扣饱食度；死亡后清空（`clear_on_death` 默认）；专用服务器下线约 100 秒再上线，时钟只走了在线的 168 tick
  - 未测：中毒伤害与死亡消息、视野摇晃的手感、维生素 B1、D 的属性效果、晒太阳、睡觉与关闭昼夜循环（时间差的算法有单元测试）
- [ ] 属性修正在登录、重生、换维度后正确，退档后完全移除（用 `/attribute` 查看）：1.21.1、1.20.1 查过攻击速度、攻击伤害、最大生命、方块破坏速度、受到的治疗与退档后的值；换维度未测
- [ ] 配置：`clear_on_death` 两种取值；杂质、营养素开关的关闭与重新开启；七个失去意识的键逐项生效（只测了默认值）
- [x] 显示：原版食物按住 Shift 显示营养成分行；“体内”面板在背包右侧，配方书打开时互不遮挡，JEI 物品列表让开，创造模式背包不显示（1.21.1、26.1.2 打开过面板，1.20.1 看到按钮；Tooltip 在 1.21.1 单人与专用服务器看过）
- [x] 装有 Apothic Attributes 时（三个节点），其属性面板中本系统的修正显示所属成分的图标，不显示问号；1.20.1 的挖掘速度与三个节点的受到的治疗出现在它的面板里，效果只算一次；未装时游戏正常（1.20.1、26.1.2 看过面板中“受到治疗 −50%”带维生素 C 图标；1.21.1 未装时正常）
- [ ] 多人：两名玩家互不影响；客户端显示与服务端一致（只有一名玩家的专用服务器测过）
- [ ] 结算路径没有每次分配（JFR 分配采样，做法同 [I27](I27-performance.md)）：按无分配写（数组与复用的视图对象），未做 JFR
- [ ] 长周期（30 天缺乏）在开发中用 `advance` 验证；正常游戏中的完整体验经用户同意后移入[最终验收测试](../final-acceptance.md)

## 待定

- 无。2026-11-13 用户已定（[additives/11](../additives/11-roadmap.md) 问题 47）：时间差取两者较大者；离线不算；本系统内修正按加算；维生素 C 缺乏减少全部来源的治疗；失去意识的各项效果可配置；酒精中毒为新的伤害类型、无视护甲；Shift Tooltip 含原版与他模组物品；“体内”面板在背包右侧；每种成分一个图标；装有 Apothic Attributes 时挖掘速度（1.20.1）与受到的治疗改挂它的属性

## 实现记录

- 2026-11-13 实装：
  - 包 `food.metabolism`：`Substance`（七种成分、档数、图标、语言键）、`MetabolismRules`（全部数值与分档函数，别处不写数字）、`MetabolismState`（体内量与算术，纯 Java，可单测；自己的时钟只前进）、`MetabolismEffects`（每个成分、每个属性一个修正）、`MetabolismView`（发给本人客户端的显示值、档位与配置）、`MetabolismText`、`MetabolismData`（伤害类型与标签的数据生成）、`Metabolism`（事件、结算、同步、配置）、`client/MetabolismClient`；`food.ProductGrade`（劣质、土法、不标、特级；读法由成品各批装入，现在全部为不标）
  - 结算：每名玩家每 20 tick（`tickCount % 20`），另在吃喝、登录、退出、重生、换维度时；时间差取主世界一天中的时间与游戏刻两者差值的较大者，不为负；登录与重生时从当前时刻重新计时（离线与死亡画面期间不计）。快吸收糖用 32 格的定长环形记录（满了并入最近一格），营养素记“储备归零的时刻”算第二档
  - 日照：结算时玩家在白天、头部所在格能看到天空且天空光照 15，按这次结算经过的游戏刻（最多 40）累计，满 1,000 加 1 天；睡觉跳过的时间不算日照
  - 档位按下限判定；“> 400 mg”“> 0.20%”“> 0.30%”按严格大于；浮点比较留 1e−9 的余量。酒精各档累加：0.06% 起的移动、攻击速度 −15% 与视野摇晃在更高档仍在
  - 呕吐与中毒按体内时钟每满 1,000 tick 一次，进入该档时立即一次；失去意识每次结算重新判定，受伤醒来后（配置开启时）下一次结算仍在 0.30% 以上即再次失去意识
  - 跨档提示：同一次结算的多项变化合成一行，用 “ · ” 分隔；登录、重生后的首次同步不提示
  - 修正 ID：1.21 起 `foodindustrial:metabolism/<成分>/<属性>`，1.20.1 由同一名称派生 UUID（`PlatformAttributes.declare` 登记全部 ID，客户端据此认出修正）；本系统内同一属性的百分数相加（`MULTIPLY_BASE` / `ADD_MULTIPLIED_BASE`），最大生命为加减固定值；失去意识时另加 `metabolism/unconscious/movement_speed`、`…/jump_strength` 两个乘总值 −100% 的修正锁住行走与跳跃
  - 跳跃高度 −20% 按起跳速度 −20% 实现（1.21 起 `jump_strength` 属性就是起跳速度；1.20.1 在跳跃事件里乘竖直速度），三个节点一致，实际高度约减 36%
  - 饥饿消耗：`Player#causeFoodExhaustion` 的 mixin 把每次消耗乘以 1 − 减免（咖啡因耐力 15%、快吸收糖 20%，合计不超过 30%），只在服务端
  - 失去意识的限制：攻击（攻击实体事件）、破坏与放置方块（左键方块、破坏、放置事件；右键时手持方块物品按放置处理）、使用物品（右键物品、开始使用物品）、打开容器（右键有菜单或容器的方块、与实体交互）。两端都判断：客户端按同步来的状态先拦下
  - 不能入睡：上床检查时返回 `OTHER_PROBLEM`（没有原版提示），本模组在动作栏写“咖啡因过多，无法入睡”
  - 农夫乐事的派：`PieBlock#consumeBite(Level, BlockPos, BlockState, Player)` 在 1.20.1 与 1.21.1 相同，用 `@Pseudo` mixin 在吃下一口后回调；mixin 只能放在 `platform/mixin`（mixin 配置的包），没有放进 `platform/compat`；26.1.2 没有农夫乐事，mixin 跳过
  - 伤害类型 `foodindustrial:alcohol_poisoning`：`scaling` 为 `never`，`exhaustion` 0，死亡消息 `death.attack.foodindustrial.alcohol_poisoning`（“%1$s 死于酒精中毒”），标签 `minecraft:bypasses_armor`
  - 配置：`[metabolism]` 十个键照计划声明；开关的变化在结算时检查，关闭与重新开启都回到初始状态
- platform 新增：`PlatformPlayerData`（玩家数据：1.20.1 为 Forge 能力，1.21 起为数据附件；死亡不复制，由业务在 `onClone` 决定；离开末地的克隆在 1.20.1 由 platform 复制）、`PlatformPlayerEvents`（登录、退出、重生、换维度、克隆、每 tick、受伤、吃喝完成与蛋糕、派的一口、饥饿消耗缩放、睡眠检查、交互拦截）、`PlatformAttributes`（语义属性、修正、事件套用的倍率、Apothic Attributes 承载属性）、`PlatformPlayers`（主世界时间、游戏刻、日照判定、减饱食度、按伤害类型 ID 造成伤害）、`client/PlatformHud`（相机角度、全屏遮罩、背包侧面板与按钮、Apothic 图标）、mixin `PlayerExhaustionMixin`、`CakeBlockMixin`、`FarmersDelightPieMixin`，JEI 的 `InventoryPanelHandler`
  - 26.1：`TriState` 移到 `net.minecraft.util`；破坏方块事件为 `level.block.BreakBlockEvent`；`LivingDamageEvent.Post` 用 `getHealthDamage`；白天为 `isBrightOutside`；时间为 `getOverworldClockTime`；伤害为 `hurtServer`
  - 遮罩在 1.20.1、1.21.1 要平移到深度 2000 画，否则 HUD 各层（深度递增）盖在上面
- Apothic Attributes：三个节点以 `deps.opt.apothic`、`deps.opt.placebo` 加入（1.20.1：1.3.7 / Placebo 8.6.3；1.21.1：2.11.0 / 9.9.4；26.1.2：3.1.0 / 10.0.2，Modrinth 版本 ID 见各节点 `gradle.properties`），开发运行时用 `-PfiWith=apothic,placebo` 加入。来源类型的优先级 200，`render` 把 16 × 16 图标缩到 8 × 8 画；1.21.1 的 Apothic Attributes 2.11.0 另有 `mining_speed`，按计划仍用原版 `block_break_speed`
- 图标：`tools/textures/metabolism_icons.py` 画 7 个 16 × 16 成分图标与 8 × 8 的按钮图标（小人），写到 `assets/foodindustrial/textures/gui/metabolism/`
- 面板：按钮 10 × 10，在副手栏正上方（背包坐标 80, 49，与副手栏的格框相隔 2 像素；2026-11-13 用户要求，原在玩家模型框内左上角 28, 10）；面板贴在背包右侧。2026-11-13 按样版改版，见下一条（原为每行图标、名称、数值 · 档位，宽度随文字）。Apothic Attributes 的面板与配方书都在背包左侧；两者都打开时，窗口较窄会把右侧面板挤出屏幕
- 发现的既有问题（与本计划无关，未改）：26.1.2 上 9 个配方因引用的标签不存在而加载失败（如 `materials/straw`、`materials/litsea`、`catalysts/immobilized_lipase`）：这些标签在该节点只含没有安装的他模组物品，按 I06“空标签不写”没有生成

- 界面改版实装（2026-11-13）：platform 新增 Tooltip 组件 `TooltipRows`（图标、文字、长条按行排列，可按组折行）与渲染器 `TooltipRowsRenderer`（经 `RegisterClientTooltipComponentFactoriesEvent` 注册），物品 Tooltip 经 `RenderTooltipEvent.GatherComponents` 加入（`PlatformClient.onTooltipRows`），插在他模组放在最后的模组名行之前；`GuiCanvas` 统一三个节点的矩形、图标、文字、长条与悬浮框绘制，`GuiBar` 描述长条。面板（第二轮 F）：不画底板与标题，每种成分一枚 44 × 10 的胶囊（`#CC101116`，四角各削两像素），胶囊间隔 2；内有 8 × 8 图标与 26 × 2 细条，细条底 `#1CFFFFFF`，填充按档位着色，档位分界画成 `#121318` 的 1 像素缺口；悬浮卡片里的长条仍是 120 × 4、带各档淡色区间。长条右端：咖啡因 500 mg、酒精 0.45%、快吸收糖 60 g、杂质 100（超出画满）。26.1 的 Tooltip 在屏幕自身绘制的末尾统一画出，面板改在 `ScreenEvent.Render.Foreground` 里画，手持物品与物品 Tooltip 仍在面板之上；1.20.1、1.21.1 仍在 `Render.Post`
- 倒计时：同步时附上每种成分离降档的 tick 数（咖啡因、酒精、杂质按公式 `MetabolismRules.ticksToDrop`，快吸收糖按窗口内各次进食的到期时间，营养素按储备与缺乏时长）和营养素已缺乏的 tick 数，客户端按收到后经过的游戏时间往下数；单元测试 14 项通过
- 游戏内（三个节点）：面板、简洁与 Shift 完整卡片、食品 Tooltip 的默认与 Shift 状态均显示正常

- 代谢速率属性实装（2026-11-13 用户要求）：`PlatformAttributes.registerPlayer` 经 `PlatformRegistry` 注册 `RangedAttribute`（`setSyncable(true)`，26.1 另设 `Sentiment.NEUTRAL`），在模组总线的 `EntityAttributeModificationEvent` 里加给玩家（1.20.1 传 `Attribute`，1.21 起传 `DeferredHolder` 即 `Holder<Attribute>`）；`MetabolismState.advance(ticks, rate, …)` 把世界时间乘以速率，余数累计（不存档）；状态记下最近一次结算的速率并随视图同步（速率变化即重发）；客户端把同步来的体内 tick 数按速率换回实际 tick
- 游戏内（三个节点）：`/attribute @s foodindustrial:metabolic_rate get` 为 1.0；设为 2 后 `advance 4000` 咖啡因由 147.5 降到 36.3（两个半衰期），维生素 C 少 1/3 天；卡片的咖啡因倒计时减半、维生素 C 显示“储备 14 天”“14 天后储备耗尽”、B1“7 天后转为脚气病”，末尾“代谢速率 ×2”；设为 0 后体内量不变、不显示倒计时，末尾“代谢速率 ×0”。1.20.1 装 Apothic Attributes 时其面板列出“代谢速率 0.5”，悬浮显示说明与 0–100 的范围

## 待审

- [x] 档位名称与跨档提示（`metabolism.foodindustrial.*`）：正常；咖啡因 警觉 / 难以入睡 / 耐力 / 过量；酒精 微醺 / 醉酒 / 呕吐 / 失去意识 / 酒精中毒；快吸收糖 耐力；杂质 疲劳 / 乏力 / 贫血；维生素 C 缺乏 / 坏血病；B1 缺乏 / 脚气病；D 缺乏 / 骨软化。提示写成“咖啡因：警觉”，同时多项用“ · ”连接；不能入睡写“咖啡因过多，无法入睡”；面板标题与按钮提示“体内”（2026-11-13 批准）
- [x] Shift 营养成分行的精度（text-standard §3.3）：100 起取整，1–100 一位小数，1 以下两位小数，四舍五入、去掉末尾的 0，低于 0.01 写“<0.01”（2026-11-13 批准）
- [x] 酒精各档累加；跳跃高度按起跳速度 −20%；呕吐与中毒在进入该档时先发生一次；打开容器的限制把与实体的交互一并拦下（2026-11-13 批准）
- [x] 图标画法；视野摇晃滚转 ±3°（0.20% 以上 ±5°）、偏航为其一半、周期 80 tick；画面变黑为 96% 不透明的黑色（2026-11-13 批准）
- [ ] 正常游戏中 30 天缺乏等长周期体验，是否移入[最终验收测试](../final-acceptance.md)（等待后续）
- [x] 面板的数值写法（咖啡因整数 mg、酒精三位小数 %、快吸收糖整数 g、杂质一位小数、营养素“储备 N 天”）：照样版（2026-11-13 批准）

## 界面改版（2026-11-13 用户要求）

- “体内”面板改为更现代的风格：缩略长条，悬浮显示详细信息；先出几版 HTML 样版供用户选择
- 食品 Tooltip 改为新的 Tooltip 组件类型：缩略图 + 彩色文字 + 数据；同样先出 HTML 样版
- 选定后再实装；现有的面板与 Shift 文字行在改版前保留
- 样版（2026-11-13 出）：面板 A 竖排长条 / B 暗色仪表 / C 顶边细条 / D 分段卡片，悬浮卡片分简洁与完整；Tooltip 1 图标并排 / 2 对齐表格 / 3 大图标网格 / 4 默认简要 · Shift 展开

用户选定（2026-11-13）：

- 面板：B 的半透明深色底 + A 的排法（每行成分图标 + 横条，不写文字），标题“体内”；长条上淡色标出各档区间并画分界刻度，填充色按当前档位（正常灰、增益绿、轻度橙、重度红）；营养素的长条为储备天数 ÷ 上限，耗尽后改为斜纹底，填充缺乏的进度
- 悬浮卡片：默认简洁（图标、成分名、档位、当前值；当前效果；下一次变档的倒计时），按住 Shift 显示完整（另加长条与全部档位的阈值、效果，营养素另写每日需要量与储备上限）
- 食品 Tooltip：4 号；不显示饱食度与饱和度；规则改写见 [text-standard §3.3](../text-standard.md)
- 倒计时：咖啡因、酒精、快吸收糖用现实时间“分:秒”（同原版药水效果），杂质与营养素用游戏天
- 成分颜色、效果行写法（“+10% 挖掘速度”，同原版属性行）照样版
- 新的 Tooltip 组件类型由 platform 提供（图标、文字、长条按行排列，可折行），物品 Tooltip 与面板悬浮卡片共用

第二轮（2026-11-13 用户要求面板“更现代、更加简洁小巧”）：样版 E 细条（50 宽，8 像素图标 + 3 像素细条）/ F 浮动胶囊（每种成分一枚独立胶囊）/ G 图标列（22 宽，16 像素图标下一道细条）/ H 双列（两列四行）；四种都去掉标题和边框、四角削圆，档位分界改为长条上的缺口；另问是否隐藏为 0 的成分。用户选 F（2026-11-13）；隐藏为 0 的成分未答复，按现状全部显示；悬浮卡片不变
