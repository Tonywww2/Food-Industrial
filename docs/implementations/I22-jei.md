# I22 JEI 集成

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | F 联动与验证 |
| 依赖 | I08、I13、I14 |
| 原文档 | [additives/10 §5](../additives/10-purity-jei-ae2.md)、[machines/01 §6](../machines/01-recipe-types.md)、[text-standard §4–5](../text-standard.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `platform.compat.jei` |

## 目标

每种配方类型一个 JEI 类别，所有设备为催化剂，纯度规则、条件、设备差异一目了然。

## 范围

- 做：类别、催化剂、设备行与设备切换、条件图标、转移处理器、隐藏桥接配方、多方块结构页

## 任务

- [x] 21 个配方类别，类别键 `jei.foodindustrial.category.<配方类型 ID>`（[machines/01 §6](../machines/01-recipe-types.md)，[text-standard §4](../text-standard.md)）
- [x] 催化剂：机器、手工工具、被动设备、原版设备（[machines/01 §5](../machines/01-recipe-types.md)）
- [x] 配方页的设备行（耗时、纯度、产率）与设备切换，转移到 AE2 样板终端时按所选设备编码（2026-11-11，随 I24 验证）（[machines/01 §6](../machines/01-recipe-types.md)）
- [x] 纯度规则的显示：来源、除杂、保持、劣化，有上限时加“最高”；输出槽按门槛输入与所选设备算出结果；不带纯度的产物不显示纯度规则（2026-11-05，[additives/10 §1.1](../additives/10-purity-jei-ae2.md)）（[additives/10 §5 显示纯度产出](../additives/10-purity-jei-ae2.md)）
- [x] 条件图标与词表（[text-standard §5](../text-standard.md)）
- [x] 自定义转移处理器，逐槽用 `Ingredient.test` 选料（[additives/10 §5 一键转移](../additives/10-purity-jei-ae2.md)）
- [x] 从原版熔炉、烟熏炉、高炉、营火类别中隐藏桥接配方（[additives/10 §5 原版设备](../additives/10-purity-jei-ae2.md)）
- [x] 锁定槽的 JEI 拖入（自 I10 移入，2026-11-05；[machines/04 §1](../machines/04-gui.md)）
- [x] 机器界面点击进度箭头打开该机器可做的配方类型的 JEI 页（自 I10 移入，2026-11-05；[machines/04 §1](../machines/04-gui.md)）
- [x] 机器界面的侧面板（含展开后的面板）登记为 JEI 额外区域（`PlatformScreen.extraAreas()`），JEI 侧栏让开这些面板（I10 发现，2026-11-05 用户确认；[machines/04 §2](../machines/04-gui.md)）
- [x] 多方块结构页：每台多方块机器一页，画出结构、标出可换成仓口的位置、列出所需方块与数量，控制器为催化剂（2026-11-05 定；[additives/10 §5 多方块](../additives/10-purity-jei-ae2.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| JEI 版本 | 15.35.0.175（原 15.2.0.25，见实现记录） | 19.39.0.372 | 29.53.0.115 |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] [additives/10 §8](../additives/10-purity-jei-ae2.md) 中 JEI 条目全部勾选

## 实现记录

- 代码：`platform.compat.jei`（`FoodIndustrialJeiPlugin`、`ProcessCategory`、`WorldCategory`、`StructureCategory`、`MachineTransfer`、`MachineScreenHandler`、`JeiSupport`）；版本无关的页面模型在 `gui.client.recipe`（`RecipePage`、`WorldPage`、`StructurePage`、`RecipeDevices`、`RecipeTextures`），文本在 `gui.RecipeText`、`gui.RecipeInfo`；条件图标贴图 `textures/gui/recipe.png` 由 `tools/gui/recipe_textures.py` 生成
- 类别：19 种加工配方类型 + W01 方块转化 + W02 割取 = 21 个，另加“多方块结构”1 个。催化剂：29 台机器、H01–H09 的 11 件工具与被动设备、原版营火（含灵魂营火）、熔炉、烟熏炉、高炉；W02 的催化剂为燧石刀；世界交互 H10（营火）、H12（晾晒架）、H13（陶坛、曲盘）、H15（刀）随所在配方类型出现；H11 硝土生成没有设备（类别图标为泥土），H14 冷置析酒石由木桶罐的代码实现、不是配方，改为给酒石加 JEI 信息页（`/fidev catalysts` 列出全部设备与各类别的催化剂）
- 配方页（160×88）：输入 3 列、催化剂在箭头下、输出 3 列；条件图标行（悬停显示条件词）；摘要“能耗 · 耗时”（手工工具加操作次数，不写档次）；设备行列出能做此配方的设备，点击切换，设备的 Tooltip 依次为设备名、档次、能耗 · 耗时、产率，输出槽按门槛输入与所选设备的档次和设备档案算出纯度（JEI 每次轮换槽位会清掉覆盖，在 `onDisplayedIngredientsUpdate` 里重新写入）；输出槽 Tooltip 写纯度规则（来源 / 除杂 / 保持 / 劣化，含“最高”）、概率、随机范围、最低档次（“T2 起产出”）、不产出的设备；档次一律直接写 `T2` 并用档次颜色（text-standard §3.4，2026-11-11 用户要求）；输入槽写标签、纯度门槛、“劣质也可”；催化剂写可用次数
- 结构页（160×116）：逐层平面网格（按钮切层，前排在下），蓝框为可换成仓口的位置、金框为控制器；右侧列出所需外壳方块与数量、可替换的仓口；中间层可重复时写重复次数。控制器为催化剂
- 世界交互页：W01（方块、结果、条件、随机刻与附近生物的概率）、W02（方块、刀、容器、产物、冷却）
- 转移：`MachineTransfer` 按槽用 `Ingredient.test` 从背包选料，缺料时标出缺的槽；发送 `CLEAR_SLOT` 与 `TRANSFER` 两个菜单动作。机器界面点进度箭头打开该机器的配方类别；侧面板登记为 JEI 额外区域；从 JEI 拖物品或流体到锁定槽 / 储罐即锁定
- 桥接配方：从原版熔炼、烟熏、高炉、营火类别隐藏（1.20.1 共 27 条）
- 标签时序（1.20.1）：JEI 15 在 Forge 上于 `RecipesUpdatedEvent` 启动，此时客户端还没收到标签；插件检查到标签未绑定时推迟，等 `TagsUpdatedEvent`（客户端收包）后用运行时的 `IRecipeManager.addRecipes` 补入。Forge 1.20.1 的 `Ingredient` 会永久缓存标签展开结果，改为标签更新时 `Ingredient.invalidateAll()`，读取前 `fresh()` 重新展开
- 空标签：本整合包里没有物品的标签（如可选模组的玉米），加载器会显示一个名为 “Empty Tag” 的屏障；`PlatformIngredients.displayStacks` 去掉这个占位，输入或催化剂为空的配方不进 JEI，与 JEI 对原版配方的处理一致（2026-11-11）
- 26.1：客户端只收到 `OnDatapackSyncEvent.sendRecipes` 发送的配方类型，`PlatformRecipes` 发送本模组全部类型并在 `RecipesReceivedEvent` 读取
- JEI 升级：1.20.1 原用的 15.2.0.25 缺 `IRecipeSlotDrawable.createDisplayOverrides`、`IRecipeExtrasBuilder` 等接口（15.20 起才有），先升到 15.74.0.234（JEI 1.20.1 分支的最新版，用户批准）。之后为让 GTCEu 7.5.3 在开发环境运行（I26）改为 15.35.0.175：GTCEu 的 JEI mixin 针对 15.20–15.35 的 `FluidHelper.getTooltip(ITooltipBuilder, …)`，15.40 起改回 `List` 参数，GTCEu 注入失败、游戏无法启动；装 GTCEu 的整合包只能用 15.35 及以前的 JEI。本模组按 15.35 的 API 编译，15.35 与 15.74 上都能用（2026-11-11）
- 验证：三个节点编译、数据生成通过；1.20.1 游戏内逐项检查（类别与催化剂、条件图标 Tooltip、设备切换后纯度变化、催化剂“可用 16 次”、输出规则行、转移的三种结果、箭头点击、侧面板避让、拖入锁定、R / U 任一纯度的物品、结构页切层、W01 / W02 页、信息页）；1.21.1 与 26.1.2 游戏内检查类别、配方页、结构页切层与文本
- 设备切换后重新显示该配方（`IRecipesGui.showRecipes`，只列这一条）：AE2 的样板转移读的是配方页建成时放进槽位的产物，不读显示覆盖，不重建时编出的是打开页面时所选设备的纯度（I24 发现，2026-11-11）。复查（1.20.1，JEI 15.35，装机械动力与 GTCEu，2026-11-11）：摘要为“能耗 10 FE/t · 耗时 5 秒”，设备 Tooltip 为“粉碎机 / T2（蓝）/ 能耗 10 FE/t · 耗时 5 秒 / 产率 ×1.1”；切换设备重新显示后，按 JEI 的返回键回到原来的配方列表，所选设备保留
- 空标签的补充（2026-11-11，用户要求）：开发环境以 `deps.dev.*` 加入潘马斯 HarvestCraft 2 的作物与果树（1.20.1、1.21.1；26.1 无版本），补上玉米、大豆、茶、葡萄、菠萝、木瓜、柑橘、桂皮的标签；钒、铂、铑由 GTCEu 补上（`-PfiWith=gtceu`，见 I26）
- 26.1.2 的 JEI 29 把 `RecipeType`、`addRecipeCatalyst(ItemStack, …)` 等标为将移除，目前仍可用，编译有弃用警告

## 待审（实现时拟定，请确认）

- 无。已确认（2026-11-11，用户）：页面布局与条件图标、新增文本、1.20.1 JEI 升级并改用 15.35.0.175（理由见实现记录）、档次颜色（text-standard §3.4）、空标签配方不显示（开发环境另加填充这些标签的模组，见实现记录）、原版设备耗时按倍率显示、点设备后只显示这一条配方

## 待定

- 无。已定（2026-11-11，用户确认）：多方块结构页逐层切换（平面网格逐层显示方块图标，按钮切层，标出仓口位置，列出所需方块与数量），立体预览交给 Patchouli 手册
