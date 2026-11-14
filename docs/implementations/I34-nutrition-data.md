# I34 营养数据：数据包、命令与数据生成

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | E 内容 |
| 依赖 | I01、I04 |
| 原文档 | [foods/03](../foods/03-nutrition-data.md)、[foods/02 §3](../foods/02-metabolism.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `food.nutrition`（新包）、`platform`（新增方法见任务）、数据生成的输入 `tools/nutrition/<模组 ID>.json` |

## 目标

数据包 `data/<命名空间>/foodindustrial/nutrition/*.json` 按 foods/03 第 2 节加载并同步到客户端，任意物品都能查到每份的代谢成分；命令能查看单个物品的数据、导出缺数据的食物清单；本模组附带的营养数据全部由数据生成写出，生成时检查。

## 范围

- 做：成分键、文件解析、加载与优先级、客户端同步、查询接口、`/foodindustrial nutrition show | missing`、数据生成的输入与输出、生成时检查与报表
- 不做：代谢的计算、效果与显示（[I35](I35-metabolism.md)）；原版与农夫乐事的数值（[I36](I36-vanilla-nutrition.md)）；本模组成品的算法数值（[foods/01 §4](../foods/01-overview.md)，随成品各批的计划登记进本计划的生成器）

## 任务

### 数据格式与加载

- [x] 成分键集中在枚举 `food.nutrition.Component`：`caffeine_mg`、`alcohol_g`、`fast_sugar_g`、`vitamin_c_mg`、`vitamin_b1_mg`、`vitamin_d_ug`，各带单位与语言键，I35 与显示共用；以后加入的成分（[foods/02 §6](../foods/02-metabolism.md)）只在这里加一项（[foods/03 §2](../foods/03-nutrition-data.md)）
- [x] 解析：文件 `mod`、`priority`、`entries[]`；条目 `items`、`serving_g`、`per_serving`、`energy_kcal`、`basis`。可选字段用 `StrictCodecs.optional`（[CONVENTIONS §4.1](../../CONVENTIONS.md)）；`basis` 照原样读入、不校验、游戏不使用；`per_serving` 缺的键按 0 计并记为“缺键”；未知的成分键、`serving_g` ≤ 0、成分为负都按格式错误处理（[foods/03 §2](../foods/03-nutrition-data.md)）
- [x] 加载：`PlatformEvents.onDataReload("nutrition", "foodindustrial/nutrition", …)`，做法同外部纯度规则 `core.purity.PurityRules`；`mod` 未加载（`Platform.isModLoaded`）时整份跳过、不记警告；不存在的物品 ID 记警告后忽略；格式错误的文件记错误后整份跳过（[foods/03 §2](../foods/03-nutrition-data.md)）
- [x] 优先级：同一物品出现在多份文件时取 `priority` 大的；相同时记警告，取文件 ID 按字典序靠后的一份（[foods/03 §2](../foods/03-nutrition-data.md)）
- [x] 查询接口 `Nutrition.of(ItemStack)`：返回每份数据或空。结果按物品存表，重载后整表替换（同 `PurityRules` 的 `State`），查询不分配对象（[machines/02 §6](../machines/02-architecture.md)）
- [x] 同步：登录与 `/reload` 后经 `PlatformNetwork.toClient` 与 `PlatformEvents.onDatapackSync` 发给客户端（同 `PurityRules` 的 `SyncPayload`），只发物品、一份克数与六个成分，不发 `basis`；客户端收到后整表替换

### platform 新增

API 名称在实装时按目标版本的反编译源码核对（[CONVENTIONS §4](../../CONVENTIONS.md)）。

- [x] `PlatformItems.consumeKind(ItemStack)`：按使用动画返回吃、喝或都不是
- [x] `PlatformItems.foodValues(ItemStack)`：饱食度与饱和度；没有食物属性时为空
- [x] `PlatformItems.descriptionKey(Item)`：物品的翻译键
- [x] `Platform.gameDirectory()`：游戏目录（加载器的路径接口），导出文件用
- [x] 命令的物品参数：现有 `PlatformEvents.onRegisterCommands` 只给出命令分发器，物品参数要 `CommandBuildContext`；在 platform 增加带上下文的注册方法，或封装物品参数

### 命令（权限等级 2，[foods/03 §3.2](../foods/03-nutrition-data.md)）

- [x] 根命令 `/foodindustrial` 下的 `nutrition`。开发版已有 `/foodindustrial perf`（[I27](I27-performance.md)），同名根节点由 Brigadier 合并
- [x] `show <物品>`：一份克数、六个成分（含 0）、`energy_kcal`、所在文件与 `priority`；没有数据时写一行“无数据”
- [x] `missing`：遍历已注册的物品，挑出可吃、可喝的（有食物属性，或使用动画为吃、喝）中没有数据或缺键的，聊天里按模组给出个数，同时写 `<游戏目录>/foodindustrial/nutrition_missing.json`：每项物品 ID、翻译键、饱食度、饱和度、使用动画、所属模组、缺数据还是缺哪些键
- [x] 文本：命令反馈的键 `commands.foodindustrial.nutrition.*`，`zh_cn`、`en_us` 经 `Lang` 生成，照 [text-standard](../text-standard.md) 第 1 节与检查清单

### 数据生成（[foods/03 §1](../foods/03-nutrition-data.md)，2026-11-13 定：本模组的 `foodindustrial/nutrition/*.json` 全部由数据生成写出）

- [x] 输入：原版、农夫乐事等随模组附带的他方食物的手工数据，每个模组一份 `<模组 ID>.json`，格式同 [foods/03 §2](../foods/03-nutrition-data.md)、`basis` 必填，放在实装定下的位置（2026-11-13 用户定），写进本计划的实现记录；[I36](I36-vanilla-nutrition.md) 与流程文件（[foods/agents/food-nutrition-data.md](../foods/agents/food-nutrition-data.md)）写到这里
- [x] 只在部分游戏版本存在的物品（如 1.21 起的不祥之瓶）：输入条目加 `only_on`（Minecraft 版本列表，省略为全部节点），只在所列版本的节点生成；`only_on` 只用于输入，生成时去掉
- [x] 输出：`PlatformDatagen.register("nutrition", …)` 以 `Kind.DATA` 写 `data/foodindustrial/foodindustrial/nutrition/<模组 ID>.json`；生成的文件不手改
- [x] 本模组成品：生成器提供登记接口，成品各批的计划把 [foods/01 §4](../foods/01-overview.md) 算出的每份数据登记进来，`basis` 写算法与配方 ID；没有条目时不写 `foodindustrial.json`
- [x] 节点上没有某模组时（26.1.2 暂无农夫乐事，[additives/11](../additives/11-roadmap.md) 问题 14）不生成该模组的文件，日志写一行；以后该节点加入这个模组的依赖，即照常生成（foods/03 第 1 节交本计划定）
- [x] 生成时检查（`PlatformDatagen.addCheck`）：六个成分键齐全；手工条目的 `basis` 带 `food`、非空的 `sources` 与 `confirmed`；物品 ID 在该节点存在；`serving_g` > 0、成分 ≥ 0；同一物品不在两条条目中出现。任一项不过，数据生成失败
- [x] 报表：`sink.report("nutrition_report.md", …)`（写到 `build/reports/foodindustrial/`），按模组列每个物品的饱食度、饱和度、一份克数、`energy_kcal`、六个成分与数据来源，供抽查和与原版饱食度对照（[foods/03 §3.2](../foods/03-nutrition-data.md)、[foods/01 §4.3](../foods/01-overview.md)）

## 节点差异

名称为候选，实装时按反编译源码核对。

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 数据目录 | `data/<ns>/foodindustrial/nutrition/`（自定目录） | 同左 | 同左 |
| 食物属性 | `Item#getFoodProperties` | 数据组件 `DataComponents.FOOD` | 同 1.21.1 |
| 使用动画 | `UseAnim` | `UseAnim` | `ItemUseAnimation`（1.21.2 起改名） |
| 游戏目录 | Forge `FMLPaths.GAMEDIR` | NeoForge `FMLPaths.GAMEDIR` | 同 1.21.1 |
| 农夫乐事数据 | 生成 `farmersdelight.json` | 生成 | 暂无农夫乐事，不生成 |
| 标识符 | `ResourceLocation` | `ResourceLocation` | `Identifier` |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 单元测试：缺键按 0 计；未知键、负数、`serving_g` ≤ 0 整份跳过；`mod` 未加载整份跳过；优先级与字典序；`only_on` 的筛选（`NutritionFileTest`，8 项）
- [x] 三个节点的数据生成通过；故意在输入里缺键、缺 `basis`、写不存在的物品时数据生成失败（1.20.1 逐项试过，报错分别为 “lacks …”“has no basis”“unknown item”；检查代码各节点相同）
- [x] 游戏内：放一个测试数据包，`show` 显示正确；改文件后 `/reload` 生效；客户端收到同步（1.21.1 单人与专用服务器各测一次；1.20.1、26.1.2 单人游戏加载了测试数据包）
- [x] `missing` 在三个节点导出清单；装农夫乐事的节点列出农夫乐事的食物（1.21.1 实测：644 种，农夫乐事 80 种；1.20.1、26.1.2 同一代码路径）

## 待定

- 无。数据生成的输入放在 `tools/nutrition/`（2026-11-13，见实现记录）。

## 实现记录

- 2026-11-13 实装：
  - 包 `food.nutrition`：`Component`（六个成分键，带单位与语言键 `metabolism.foodindustrial.<成分>`，I35 共用）、`NutritionFile`（解析与写出；`StrictCodecs.optional`；`basis` 照原样保留；运行时的文件不读 `only_on`）、`NutritionData`（每份数据与合并规则，纯 Java，可单测）、`Nutrition`（加载、同步与查询）、`NutritionCommands`、`NutritionGen`
  - 服务端与客户端各存一张表：单人游戏里客户端收到同步时不覆盖集成服务端解析出的表（同步内容不含来源与缺键，`show`、`missing` 要用）。查询 `Nutrition.of(ItemStack)`（服务端）与 `Nutrition.client(ItemStack)`（显示）都是按物品查表，不分配对象
  - 数据生成的输入：`tools/nutrition/<模组 ID>.json`（2026-11-13 定，实装选用计划里的参考做法），文件名须与 `mod` 一致；两个构建脚本经系统属性 `foodindustrial.nutritionInputs` 把目录传给数据生成，不进发布 jar；没有这个属性时数据生成报错，防止漏掉随模组附带的数据。目录里的 `README.md` 写了输入的格式要求
  - 生成时检查分两处：读输入时（六个键齐全、`basis` 带 `food`、非空的 `sources` 与 `confirmed`）和写出的每份文件（`PlatformDatagen.addCheck`：再查一遍上述各项，加物品 ID 在该节点存在、一个物品只在一条条目中）；本模组成品的 `foodindustrial.json` 不查 `basis` 的三项（写算法与配方 ID）。同一物品出现在两份生成的文件中也报错
  - 节点上没有某模组时不生成它的文件并记一行日志（26.1.2 的农夫乐事）；`only_on` 按 `Platform.minecraftVersion()` 筛选
  - 报表 `build/reports/foodindustrial/nutrition_report.md`：按模组列物品、饱食度、饱和度（点数）、一份克数、能量、六个成分与来源（`basis` 的 `food`、`sources`、`processing`）
  - 现在 `tools/nutrition/` 里还没有输入（原版与农夫乐事的数值属 [I36](I36-vanilla-nutrition.md)），三个节点都不生成营养数据文件
- platform 新增：`PlatformItems.consumeKind`（`EAT`、`DRINK`、`NONE`）、`foodValues`（饱和度换算成点数：1.20.1 为饱食度 × 饱和度修正 × 2，1.21 起直接取 `saturation`）、`descriptionKey`；`Platform.gameDirectory`；`PlatformEvents.onRegisterCommands(CommandRegistrar)`（带 `CommandBuildContext`，原有只收分发器的写法保留）；`PlatformCommands`（`itemArgument`、`item`：26.1 的 `ItemInput` 改成了记录；`gameMasters`：26.1 起权限改为 `Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)`）。`DataWriter`、`DataReader` 加了 `putDouble`、`getDouble`（经 `Codec.DOUBLE`，各实现不用改）
- 命令：根节点 `/foodindustrial` 加了权限等级 2（Brigadier 合并同名根节点时沿用先注册的要求，开发版的 `perf` 随之也要 2 级）；`missing` 的饱和度保留两位小数
- 测试数据包只放在测试世界，没有进仓库
- 2026-11-13 修正 `missing`（I36 导出时发现）：投掷的药水（1.20.1、1.21.1 按使用动画被算作可喝）、26.1.2 带食物组件但没有可食用组件的四种鱼桶不再列出（`PlatformItems.consumable`）；放置后分口吃的方块一并列出，使用动画写 `bites`（`PlatformItems.eatenByBites`：蛋糕与农夫乐事的 `PieBlock`），它们的数据按一口计，与 I35 的 `ateBite` 一致

## 待审

- 命令反馈的措辞（`commands.foodindustrial.nutrition.*`）：`%s：一份 %s g（%s，优先级 %s）`、`缺键：%s`、`能量 %s kcal`、`缺营养数据的食物 %s 种，清单已写入 %s`、`可吃、可喝的物品都有完整的营养数据`；`show` 的成分行直接写键名（`caffeine_mg 95 · …`），便于对照文件
