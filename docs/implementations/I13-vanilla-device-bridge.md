# I13 原版设备桥接

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | D 设备 |
| 依赖 | I08、I01 |
| 原文档 | [machines/01 §5](../machines/01-recipe-types.md)、[machines/02 §3 原版设备桥接](../machines/02-architecture.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `platform.recipe`、`platform.mixin`、`recipe`、`manual` |

## 目标

营火、熔炉、烟熏炉（T0）与高炉（T1）能执行本模组的干燥、烟熏、窑炉配方，纯度按规则写入。

## 范围

- 做：桥接配方生成与注册、原版设备档案、冲突检查
- 不做：JEI 中隐藏原版类别里的桥接配方（I22）

## 任务

- [x] `VanillaDeviceProfiles`：四种原版设备的档次、能力、耗时倍率（[machines/01 §5](../machines/01-recipe-types.md)）
- [x] `PlatformVanillaCooking`（`platform.recipe`）：把桥接配方注册到原版熔炼、烟熏、高炉、营火烹饪类型，适配各节点 `assemble` 参数（自 I01 移入，[machines/02 §3 原版设备桥接](../machines/02-architecture.md)）
- [x] `VanillaCookingBridge`：从 R07–R09 配方按 `vanilla_devices` 生成；`matches` 用本模组原料判定；`assemble` 调用 `OutcomeResolver` 写入纯度（[machines/02 §3 原版设备桥接](../machines/02-architecture.md)）
- [x] 冲突检查：同一输入在同一原版设备已有原版配方时不注册（[machines/01 §5 规则 3](../machines/01-recipe-types.md)）
- [x] 在三个节点的反编译源码中确认熔炉、营火取产物时调用 `assemble`

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 烹饪配方输入 | 容器 | `SingleRecipeInput` | `SingleRecipeInput` |
| 原版配方类构造 | `(id, 组, 分类, 原料, 结果, 经验, 时长)` | `(组, 分类, 原料, 结果, 经验, 时长)` | `(CommonInfo, CookingBookInfo, 原料, ItemStackTemplate, 经验, 时长)`；加载配方时不能创建物品堆 |
| 序列化器 | `fromJson` / `fromNetwork` / `toNetwork` | `MapCodec` + `StreamCodec` | 同左，`RecipeSerializer` 为类 |
| 熔炉产物叠放 | 只比物品：本模组加 Mixin 改为比较 NBT | 比较组件（原版） | 同左 |
| 取配方列表 | `getAllRecipesFor` | `getAllRecipesFor`（`RecipeHolder`） | 服务端 `recipeAccess().recipeMap().byType` |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] [machines/01 §5](../machines/01-recipe-types.md) 示例表的 2 条配方在对应原版设备上结果一致：海藻灰不带纯度（多组分混合物，2026-11-05），熟石膏保持输入纯度
- [x] 档次加成另取一条来源方式的 R07–R09 配方验证：营火、熔炉（T0）与 M07 土窑（T1）结果不同（M07 属 I14，用同为 T1 的高炉代替）
- [x] 原木在熔炉中仍得木炭，海带在营火上仍得干海带

## 实现记录

- 已定（2026-11-10，用户确认）：桥接配方由数据生成写成文件，格式见 [machines/01 §5 规则 4](../machines/01-recipe-types.md)；晾晒架晒干的物品各加一条要求“加热”的干燥配方给原版设备；枯萎的灌木烧成草木灰，不另设蓬灰物料（[additives/07 §手工链配方（I12）](../additives/07-manual-crafting.md)）
- 代码：`recipe.VanillaDevice`（四种设备的档次、配方类型、能力、耗时倍率，并登记为 `DeviceSpec`，计划中的 `VanillaDeviceProfiles` 合并在这里）；`recipe.VanillaCookingBridge`（桥接文件的读取、匹配与带纯度的结果，生成桥接文件）；`ProcessRecipe.TYPED_CODEC`（带 `type` 的整条配方，供嵌入）；`DeviceSpec` 对原版设备的限制（一个物品、数量 1，一个物品产物，无流体、概率与纯度区间）；`RecipeChecks` 检查 `vanilla_devices` 中的设备都能执行该配方；`RecipeGen` 为每条配方的每台原版设备写桥接文件
- platform 新增：`recipe.PlatformVanillaCooking`（四个序列化器 `foodindustrial:vanilla_campfire_cooking`、`vanilla_smelting`、`vanilla_smoking`、`vanilla_blasting`；桥接配方继承原版的熔炼、烟熏、高炉、营火配方类，熔炉和营火照原版使用；匹配时先看这台设备的其他配方，能做就让路，按物品缓存，配方重载后重建）；Mixin `AbstractFurnaceBlockEntityMixin`（只在 1.20.1：熔炉叠放产物改为比较 NBT，纯度不同的产物不叠在一起）
- 已在三个节点的反编译源码中确认：熔炉 `canBurn` 与 `burn`、营火完成烹饪时都调用 `assemble`；1.20.1 熔炉叠放只比物品，1.21.1、26.1.2 比较组件
- 内容：干海带烧海藻灰（营火、熔炉）、原木烧草木灰（营火）、枯萎的灌木烧草木灰（营火、熔炉）、石膏烧熟石膏（熔炉、高炉，保持纯度）、六种加热干燥（营火、熔炉、烟熏炉），共 10 条配方、25 份桥接文件
- 验证：三个节点 `compileJava`、`runData`、`test` 通过（每个节点 76 项单元测试，新增 `VanillaDeviceTest`）；三个节点实测（`runServer` + `runClient`，熔炉类用命令放入物品，营火用真实点击）：熔炉中干海带 → 海藻灰、枯萎的灌木 → 草木灰、原木仍得木炭、海带仍得干海带；营火上干海带 → 海藻灰、原木 → 草木灰（600 tick）；熔炉、高炉中石膏 80% → 熟石膏 80%；烟熏炉中苹果 → 苹果干、湿胶片 → 天然橡胶 90.6%；测试配方（方解石 → 盐，来源 83%）在熔炉得 83%、在高炉得更高的值（档次加成）；熔炉输出格有熟石膏 80% 时放入纯度不同的石膏不再烧，同纯度的叠成 2 个；下雨时晾晒架上的苹果显示“缺少：晴天”，不走加热干燥
- 26.1.2 首次启动失败：加载配方时创建物品堆（组件尚未绑定），改为只取产物物品与数量，构造 `ItemStackTemplate`

- 完成后用户确认（2026-11-10）：下列各项全部通过
  - 加热干燥与烧灰配方的时长 200 tick（营火 600、烟熏炉与高炉 100）、经验 0.1、每份 1 → 1
  - 熔炉、高炉同时具备“加热”（[machines/01 §5](../machines/01-recipe-types.md) 表中原写“高温”）
  - 原木只在营火上烧灰；干海带、枯萎的灌木在营火和熔炉上都能烧

## 待定

- 无
