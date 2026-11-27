# Food Industrial 项目规范

本项目用 **Stonecutter** 维护一棵源码树，同时构建三个版本节点。本文件是强制规范；新代码、重构和 AI 代理生成的代码都必须遵守。

## 1. 版本节点

| 节点（project） | Minecraft | 加载器 | Java | 构建脚本 / 后端 |
|---|---|---|---|---|
| `1.20.1-forge`（**默认 / 激活 / VCS 版本**） | 1.20.1 | Forge 47.4.0 | 17 | `build.gradle.kts`（Architectury Loom） |
| `1.21.1-neoforge` | 1.21.1 | NeoForge 21.1.235 | 21 | `build-neoforge.gradle.kts`（ModStitch + ModDevGradle） |
| `26.1.2-neoforge` | 26.1.2 | NeoForge 26.1.2.109 | 25 | `build-neoforge.gradle.kts`（ModStitch + ModDevGradle） |

- 节点的版本号、元数据范围和依赖全部在 `versions/<节点>/gradle.properties`；全局 mod 元数据在根 `gradle.properties`。
- 提交到 git 的 `src/` 永远是 `1.20.1-forge` 的形态（`vcsVersion`）。切换到其他节点调试后，**提交前必须 Reset 回 `1.20.1-forge`**。
- 工具链快照：Gradle 9.5.0（守护进程 JDK 25，见 `gradle/gradle-daemon-jvm.properties`）、Stonecutter 0.9.7、Architectury Loom 1.11.458、ModStitch 0.8.5。本机需装有 JDK 17 / 21 / 25（或允许 foojay 自动下载）。

## 2. 核心规则：版本差异只能写在 `platform` 包

`com.tonywww.foodindustrial.platform`（含其子包）是唯一的兼容层。

1. **所有 Stonecutter 条件注释（`//? if …`、`//?} else {`、`/*? … */` 等）只能出现在 `platform` 包内。** `platform` 之外的 Java 文件不得出现任何 `//?` / `/*?`。
2. **加载器相关的 import 和类型只能出现在 `platform` 包内**：`net.minecraftforge.*`、`net.neoforged.*`、`@Mod`、`IEventBus`、`DeferredRegister`、`RegistryObject` / `DeferredHolder`、FML 事件、`ModList`、`FMLEnvironment` 等。
3. **在不同节点上签名或名称不同的 Minecraft API 只能在 `platform` 内直接调用**，例如：`ResourceLocation` / `Identifier`、`Item.Properties#setId`、`BlockBehaviour.Properties#setId`、`Item#use` 的返回类型、`appendHoverText` 签名、BlockEntity 的 NBT 存取（`saveAdditional`/`loadAdditional`，26.1 起 `ValueInput`/`ValueOutput`）、`AttributeModifier`、`registryOrThrow`/`lookupOrThrow`、`getMinBuildHeight`/`getMinY`、跨维度 `teleportTo`、渲染（26.1 起 extract/submit）等。
4. **业务代码（`platform` 以外的一切）只调用 `platform` 暴露的稳定 API 或辅助函数。** 需要的封装还不存在时，先在 `platform` 里新增一个**语义化**方法（按“做什么”命名，而不是按某个版本的 API 命名），三个节点都实现后，再在业务代码里调用它。
5. 业务代码不要写出版本相关的类型名。ID 用 `var` 接住 `PlatformIdentifier.id(...)` 的结果，或者用 `PlatformIdentifier.key(...)` 得到与版本无关的 `ResourceKey`；注册得到的对象一律用 `Supplier<T>` 持有。
6. 需要覆写签名随版本变化的方法（物品 tooltip、`use`、BlockEntity 存取、GeoEntity 等）时，在 `platform` 中提供基类（如 `PlatformItem`），由它覆写版本相关签名并转发给一个稳定的 protected 业务方法；业务类继承这个基类。

### 2.1 例外与归属

| 内容 | 放在哪里 | 说明 |
|---|---|---|
| `@Mod` 入口 | `platform/ModEntrypoint` | 只做接线：绑定事件、调用 `FoodIndustrial.init()`、挂注册表 |
| Mixin 类 | `platform/mixin`（`foodindustrial.mixins.json` 的 `package`） | Mixin 直接面对原版内部实现，允许使用 `//?`；保持很薄，逻辑委托给业务代码 |
| 可选模组联动（JEI、Patchouli、农夫乐事、Thermal、PneumaticCraft 等） | `platform/compat/<模组>` | 各版本 API 往往完全不同；某节点没有该依赖时，用整文件 `//? if` 守卫排除，调用前先 `Platform.isModLoaded(...)` |
| 加载器 / 版本专属的数据生成适配 | `platform/datagen` | 数据生成的业务内容（配方、战利品的“是什么”）放业务包，通过 platform 适配器输出 |

## 3. 现有 platform API

| 类 | 用途 |
|---|---|
| `ModEntrypoint` | `@Mod` 入口；Forge 注入 `FMLJavaModLoadingContext`，NeoForge 注入 `IEventBus` 与 `ModContainer` |
| `Platform` | `loaderName()`、`minecraftVersion()`、`isModLoaded(id)`、`isPhysicalClient()`、`isDevelopment()` |
| `PlatformIdentifier` | `id(path)`、`id(ns, path)`、`vanilla(path)`、`parse(str)`、`key(registry, path)` |
| `PlatformRegistry` | `register(registryKey, name, supplier)`、`item(...)`、`block(...)`、`blockItem(...)`、`placingItem(...)`（自有名称、放置方块的物品，如作物）、`doubleHighBlockItem(...)`（门）；1.21.2+ 自动设置物品/方块 ID 和方块物品描述前缀 |
| `PlatformEvents` | `onCommonSetup(Runnable)`、`onClientSetup(Runnable)`（主线程执行）、`onAxeStrip(...)` 等游戏事件 |
| `PlatformConfig` | `commonBoolean(section, name, comment, default)`：在 `FoodIndustrial.init()` 中声明 `config/foodindustrial-common.toml` 的选项；`commonChoice(...)` 取一个允许值，`commonStringList(section, name, comment, default, allowed)` 取允许值组成的列表、可为空（I46）；`clientBoolean(...)`、`clientInt(section, name, comment, default, min, max)` 声明只在客户端读取的 `config/foodindustrial-client.toml` 的选项（I37） |
| `PlatformWorldgen` | 放置过滤器 `foodindustrial:enabled` 的开关（`setSwitches`）；开发用的放置与统计 |
| `platform.world` | `PlatformPlants`（作物、野生植株、果丛、海草类、树苗、树叶、可去皮原木）、`PlatformWood`（木制方块与可燃性） |

| `platform.machine` | `PlatformBlockEntity`（存取、同步、ticker、`onLoaded` / `onRemoved` / 邻居更新钩子；`renderReach()` 让渲染器能画到方块外多少格，I37）、`PlatformEntityBlock`、`PlatformMemberBlock`（无方块实体的成员方块的放置 / 移除钩子）、`PlatformMachineCapabilities`、`PlatformNeighborCache` |
| `platform.recipe` | `PlatformRecipes`（业务 `MapCodec` 注册配方类型，`loaded`、`jsonOps`）、`PlatformIngredients`（纯度原料、原料编解码） |
| `PlatformFuel` | 原版熔炉燃烧时间、燃烧后剩下的容器 |
| `PlatformCommands` | 命令的物品参数（`itemArgument`、`item`）与 2 级权限（`gameMasters`）；带 `CommandBuildContext` 的注册用 `PlatformEvents.onRegisterCommands(CommandRegistrar)`（I34） |
| `PlatformPlayerData`、`PlatformPlayerEvents`、`PlatformAttributes` | 随玩家存档的数据；玩家生命周期、吃喝、睡眠、交互拦截等事件，受到的伤害按比例调整（`setDamageTakenScale`，I46）；按语义属性加减带 ID 的临时修正（I35） |
| `platform.client.PlatformHud` | 相机角度、全屏遮罩、生存背包右侧的面板与按钮、Apothic Attributes 面板中的修正图标（I35） |
| `platform.client.PlatformPanels` | 浮在方块旁、朝向相机的平面面板：业务代码每帧往 `Panel` 里填矩形、物品图标、流体方块和文字，三个节点的绘制在这里；`register(type, texture, textureSize, viewDistance, provider)`，可见距离为 0 时不产生任何逐方块开销（I37） |
| `platform.food.PlatformFoodData` | 成品的品质码与半成品键（1.20.1 NBT，1.21 起数据组件）；`scaleSaturation` 按品质系数改写 1.21 起物品上的食物组件（I38、I39） |
| `platform.recipe.PlatformCrafting` | 业务写的无序合成（`Grid`：匹配、产物、留在格里的工具与副产）包成三个节点的原版合成配方（I38 的 `food_crafting`） |
| `PlatformItem#saturationFactor` | 1.20.1 按物品数据给出饱和度系数（成品的品质档，I39）；1.21 起不用 |
| `PlatformItem#craftingRemainderItem`、`PlatformRecipeConditions.whenTagNotEmpty` | 物品自定的合成剩余物（装罐装瓶成品还回容器，I40）；配方只在某物品标签非空时加载（I40 的马口铁罐、铝罐；I49 起加工配方与工作台配方也可用，业务写 `ContentRecipe.whenTag`） |
| `PlatformFluids.bind` 的牛奶流体 | 打开加载器的牛奶流体（Forge、NeoForge 的 `enableMilkFluid`），牛奶桶成为装 1,000 mB 牛奶的流体容器（I49） |
| `PlatformItems#setModelVariant` | 物品模型的变体：原版自定义模型数据（1.20.1、1.21.1 由物品模型的 `custom_model_data` 覆写读，26.1 由 `minecraft:custom_model_data` 区间分派读第一个浮点数）；香精按剂型换贴图（I48） |
| `PlatformPackagedFluids` | 瓶装、罐装的液体成品作只出不进的物品流体容器：`register(item, fluid, amount, container)`，被抽走后变回容器，流体带物品的品质（I47）；本模组的桶装流体也带品质 |
| `platform.world` 的 `HarvestPlantBlock`、`FruitLeavesBlock`、`WallNestBlock`、`PlantHarvest` | 按年龄（0–3）右键或剪刀采摘的果丛与草本、会开花结果的树叶、贴墙的巢；采摘规则由业务的 `PlantHarvest` 给出（I44） |
| `platform.world.PlatformAnimals`、`platform.client.PlatformAnimalRenderers` | 借原版体型（豹猫、狐狸）的新生物：实体类型、属性、刷新规则、渲染器换贴图（I44） |
| `PlatformLoot`、`PlatformTrades`、`PlatformEnchantments` | 往原版战利品表加奖池（业务给原版 JSON；1.20.1 按 JSON 子集构建）；村民出售（1.21 起为 `villager_trade` 数据，`generate` 写出）；“肢解”附魔的注册（1.20.1 代码、1.21 起数据）与等级查询（I44） |
| `PlatformEvents.onLivingDrops`、`PlatformPlants.sapling(feature, sand)`、`PlatformDatagen.Kind.ENTITY_LOOT` | 生物死亡时的额外掉落；可种在沙上的树苗；生物战利品表的数据路径（I44） |

其余各类（`PlatformTags`、`PlatformFluids`、`PlatformDatagen` 等）见各实装计划的实现记录（I01–I09）。

业务注册的推荐写法：

```java
// com.tonywww.foodindustrial.item.ModItems —— 业务包，不含 //? 与加载器 import
public final class ModItems {
    public static final Supplier<Item> CAN = PlatformRegistry.item("can", new Item.Properties());

    public static void bootstrap() {
        // 空方法：触发类初始化，使上面的注册在 mod 构造期间入队
    }
}

// FoodIndustrial.init()
ModItems.bootstrap();
PlatformEvents.onCommonSetup(() -> LOGGER.info("common setup"));
```

## 4. 在 platform 里写版本分支

- **分清两条轴**：加载器轴用常量 `forge` / `neoforge`；Minecraft 版本轴用版本谓词（`>=1.21`、`>=1.21.2`、`>=26.1` 等）。不要用 `neoforge` 去代表“1.21+ 的原版改动”——1.21.1 和 26.1.2 同为 NeoForge，但原版 API 差异很大。
- 优先按**整个方法**或**整段 import** 分支，避免在表达式中间插入行内条件。
- 激活节点（1.20.1-forge）的分支是真实代码，其余分支在 `/* … */` 注释里，由 Stonecutter 切换时自动开关注释。

```java
//? if forge {
import net.minecraftforge.fml.ModList;
//?} else {
/*import net.neoforged.fml.ModList;
*///?}

//? if >=26.1 {
/*public static Identifier id(String path) { … }
*///?} else {
public static ResourceLocation id(String path) { … }
//?}
```

新增 platform API 的检查清单：

1. 用语义化名字定义方法，签名中不出现版本相关类型（确实无法避免时才返回原生类型，并在 Javadoc 说明业务代码用 `var` 接）。
2. 为三个节点都写实现（在目标版本的反编译源码或 `javap` 里确认签名，不凭记忆）。
3. 三个节点都编译通过（见第 7 节）。

### 4.1 跨节点的数据格式（2026-11-04）

- 数据格式里的可选字段用 `core.data.StrictCodecs.optional`，不用 `optionalFieldOf`：1.20.1 的 DataFixerUpper 6 把写错的可选字段静默换成默认值，1.21 起才报错。
- 配方 JSON 不用顶层字段 `conditions`：1.20.1 Forge 把它当作加载条件数组。本模组配方的条件写在 `requirements`。
- 编码配方用带注册表的 JSON ops（运行时 `PlatformRecipes.jsonOps`，数据生成 `PlatformDatagen.Sink.ops`）；数据生成里的原料用 `Sink.ingredient` 构造。26.1 用普通 `JsonOps` 会把标签原料展开成物品列表，或因标签不属于当前注册表而无法编码。

## 5. 资源与元数据

- `src/main/resources/META-INF/mods.toml` 只给 Forge 节点，`META-INF/neoforge.mods.toml` 只给 NeoForge 节点；构建脚本会排除另一个文件。两者和 `pack.mcmeta` 中的 `${...}` 由 `processResources` 按节点属性展开（`vers.loaderRange`、`vers.loaderDepRange`、`vers.minecraftRange`、`vers.packFormat`）。toml 中依赖表名必须写死 `[[dependencies.foodindustrial]]`。
- 手写的、各版本通用的 assets 放 `src/main/resources`。
- 数据生成输出按节点分开：`versions/<节点>/src/generated/resources`（该目录会自动加入对应节点的资源）。原因：1.21 起数据包目录改为单数（`recipe`、`loot_table`、`advancement`、`tags/item` 等），1.20.1 仍为复数；26.1 物品还需要 `assets/<ns>/items/*.json`。1.20.1-forge 的开发运行直接读这个目录，不复制（2026-11-20）：它是一个没有源码的源码集 `generated` 的资源输出，与主源码集一起登记为本模组；打 jar 时由 `jar` 任务从这里装入（不含 `.cache`）。NeoForge 节点同样原地读取（2026-11-26）。
- 资源文件里不写 Stonecutter 条件：Stonecutter 只预处理 Java 源码，所有节点直接读 `src/main/resources`，不再把它复制进 `build\generated\stonecutter`（2026-11-26）。确有版本差异的资源放数据生成，或在 `platform` 里按节点选择。
- `forge:` / `neoforge:` 前缀的数据（biome modifier、conditions 等）必须按加载器分开维护。

## 6. 依赖与 Mixin

- 依赖在 `versions/<节点>/gradle.properties` 声明，构建脚本按键自动处理：
  - `deps.jei=<版本>`：编译 JEI API，开发运行完整 JEI；
  - `deps.patchouli=<版本>`：编译 Patchouli API，开发运行完整 Patchouli；
  - `deps.mod.<名字>=<maven 坐标>`：编译 + 开发运行（Forge 节点自动走 Loom 重映射）。
  - `deps.opt.<名字>=<maven 坐标>`：可选联动模组（2026-11-11）。总在编译类路径上；开发运行时只在 `-PfiWith=<名字>,…`（或 `all`）点名时加入，便于分别测试装与不装的情况。根 `gradle.properties` 的 `fi.devWith=<名字>,…` 列出每次开发运行都加入的可选模组（现为 Jade，2026-11-12 用户定，用于测试）；`-PfiWithout=<名字>,…` 临时去掉，测试不装的情况。
  - `deps.dev.<名字>=<maven 坐标>`：只进开发运行时、不参与编译（2026-11-11），用于填充物料标签的模组（如潘马斯作物），让开发环境的 JEI 能显示用到这些标签的配方；EMI 也走这一项（1.20.1、1.21.1，2026-11-20，用户要求），它经自带的 JEI 桥接（JEMI）显示本模组的 JEI 类别，本模组不写 EMI 插件；26.1 没有 EMI。`-PfiWithout=<名字>,…` 也能去掉 `deps.dev.*` 的模组（只用 JEI 测试：`-PfiWithout=emi`，2026-11-25）。
  - `deps.create05=<maven 坐标>`（只在 1.20.1-forge，2026-11-14）：机械动力 0.5.1（内嵌 Flywheel 0.6）。传动接口的应力与 Flywheel 渲染在 0.5.1 与 6 上写法不同：`platform.compat.create` 对着机械动力 6 编译；对着 0.5.1 的部分放在单独的源码集 `src/create05/java`（包 `platform.compat.create05`，只这一个节点，不写 Stonecutter 条件），编译类路径是主代码加去掉机械动力 6、Flywheel 1、Ponder 后的主类路径再加 0.5.1，类打进同一个 jar；运行时按 Flywheel 的版本（`0.` 开头）用反射调用它。`-PfiCreate05` 让开发运行改用机械动力 0.5.1（同时不加载机械动力 6）。
  - 某节点没有对应版本时删掉该键即可；源码中的联动代码必须用同样的条件排除。
  - 数据生成运行时，构建脚本把 `deps.mod.*`、`deps.opt.*` 及其内嵌模组的 jar 列表写到 `.gradle/foodindustrial/<节点>/external-jars.txt`，经系统属性 `foodindustrial.externalJars` 传给数据生成，用来从联动配方中去掉与他模组自带配方重叠的输入（I25）。
- 加依赖前先确认它对加载器版本的要求（例如 JEI 19.42+ 要求 NeoForge ≥ 21.1.238，所以 1.21.1 节点固定在 19.39.0.372）。
- Modrinth 上 Fabric / NeoForge 共用同一版本号时，用 **版本 ID** 固定（例如 26.1 的 Patchouli 用 `2CsnFLom`）。
- Forge 节点的依赖若用 Jar-in-Jar 内嵌其他模组（如 Thermal Foundation 内嵌 `thermal_core`），`build.gradle.kts` 会在配置阶段自动解出到 `.gradle/foodindustrial/1.20.1-forge/jij/`（NeoForge 节点同理，只加入编译类路径）并加入开发运行时；无需手动处理。这些配置阶段写出的文件不放在 `build/` 下，否则 `clean build` 会在检查过它们之后、编译之前把它们删掉（2026-11-13）。解出的模组以 `jij:<文件名>` 模块（flatDir 仓库）加入，与外层模组同批重映射——否则外层模组调用内嵌模组的方法（GTCEu 调 Registrate）会留着 SRG 名而报 `NoSuchMethodError`；内嵌的游戏库（清单 `FMLModType: GAMELIBRARY`，如 `mixinextras-forge`、Registrate）按模组加入，FML 才会加载其 `MixinConfigs`、让它看得到原版类（2026-11-11）。
- Loom 按坐标缓存重映射后的模组（项目根的 `.gradle/loom-cache/remapped_mods/`），类路径变了也不重做。开发运行时若出现带 SRG 名（`m_数字_`、`f_数字_`）的 `NoSuchMethodError` / `NoSuchFieldError`，删掉出错模组在该目录下的缓存后重新运行（2026-11-11，机械动力在内嵌模组改为同批重映射之前的缓存即如此）。
- Mixin：配置文件 `foodindustrial.mixins.json`，包为 `com.tonywww.foodindustrial.platform.mixin`。Forge 开发环境由 Loom 的 `forge.mixinConfig` 加载，生产环境由 jar 清单的 `MixinConfigs` 加载；NeoForge 由 `neoforge.mods.toml` 的 `[[mixins]]` 加载。**不要在 json 里手写 `refmap`**——Forge 节点由 Loom 自动生成并写入，NeoForge 节点不需要。

## 7. 构建与验证（Windows PowerShell）

```powershell
.\gradlew.bat projects
.\gradlew.bat :1.20.1-forge:compileJava
.\gradlew.bat :1.21.1-neoforge:compileJava
.\gradlew.bat :26.1.2-neoforge:compileJava
.\gradlew.bat :1.20.1-forge:build :1.21.1-neoforge:build :26.1.2-neoforge:build
.\gradlew.bat :1.20.1-forge:runClient      # 也可 runServer / runData；运行目录为 run\<节点>\
```

- 改动 platform 的版本分支后，**三个节点都要编译**；`BUILD SUCCESSFUL` 不等于运行正常，涉及注册、事件、Mixin、渲染的改动还要 `runServer` / `runClient`。
- 新增 `.java` 文件后若出现类缺失，执行一次 `.\gradlew.bat :<节点>:compileJava --rerun-tasks`（Stonecutter 有时不会立即重新生成）。
- 切换 IDE 中的激活节点：使用 Gradle 任务组 `stonecutter` 里的 “Set active project to …”；提交前执行 “Reset active project”。产物位于 `versions\<节点>\build\libs\`，文件名带 Minecraft 版本（如 `foodindustrial-forge-0.0.0+1.20.1.jar`）。
- 同时开多个 Gradle 命令（2026-11-04）：`gradle.properties` 开启了按需配置（`org.gradle.configureondemand`），带节点路径的命令只配置该节点。1.20.1-forge 由 Architectury Loom 构建，Loom 在配置阶段持有每个项目的配置锁，另一个进程同时配置 1.20.1-forge 时会显示 “Waiting for lock to be released … held by pid …”，等对方配置完（通常几十秒；对方在重建 Loom 缓存时要几分钟）。NeoForge 节点的命令不配置 1.20.1-forge，不受这把锁影响；IDE 导入与不带节点路径的命令（如 `build`）仍配置全部节点。被强行结束的构建会留下锁文件，若锁上的 pid 已被其他非 Java 程序占用，`build.gradle.kts` 会把锁标为放弃，Loom 随后重建缓存（需要联网）；若持锁的是仍在运行的 Java 进程，在任务管理器里按 pid 找到并结束它。
- 构建耗时与内存（2026-11-13 实测，云端 2 核）：不带节点路径的 `build` 依次构建三个节点，clean build 约 2 分钟，峰值内存约 3.4 GB（Gradle 进程最多 3 GB，另有每个 Java 版本一个编译进程）。只要某个节点的 jar 时用 `:<节点>:build`。不产出源码 jar（Forge 节点重映射它要约 50 秒、峰值多约 0.6 GB，用户定）。Windows 上生成资源有三万余个小文件，实时防护会逐个扫描复制、打包的文件，可把项目目录与 `%USERPROFILE%\.gradle` 加入 Microsoft Defender 的排除项。
- 资源处理（2026-11-18）：资源没有变化时，`processResources` 会被跳过（显示 `UP-TO-DATE`）。任何一个资源文件变了，Gradle 都会把该节点的全部资源（约 3.3 万个文件、140 MB）重新复制一遍，这是 Gradle 复制任务的做法。NeoForge 节点原先每次构建都重新复制：ModStitch 的 `appendModMetadata` 任务每次都把处理后的 `neoforge.mods.toml` 重写一遍，使 `processResources` 的输出发生变化；现在该任务只在通过 ModStitch 登记了 mixin 配置或 class tweaker 时才运行。所以 NeoForge 的 mixin 配置要直接写在 `neoforge.mods.toml` 的 `[[mixins]]` 里（第 6 节），不要用 `modstitch { mixin { … } }` 登记。
- 1.20.1-forge 开发运行的启动时间（2026-11-20，用户反映很慢）：用户机器上原先每次 `runClient` 编译约 1 分 50 秒、复制资源约 2 分 40 秒。现在 ① 命令里只有 `run…` 任务时（`runClient`、`runServer`、`runData`），`compileJava` 不跑 Mixin 注解处理器：它不支持增量编译，改一个类也要全部重编；开发环境用 Mojang 名运行，不需要 refmap。改一个类后重新启动，编译约 4 秒（云端实测）。之后跑 `build` 会带处理器全量编译一次，再跑 `runClient` 又全量编译一次。② 生成资源不再复制（第 5 节），`processResources` 只处理手写资源（约 4,900 个文件）。③ 控制台日志改为 info（完整日志仍在 `run\<节点>\logs\debug.log`）。仍慢时：把项目目录与 `%USERPROFILE%\.gradle` 加入 Defender 排除项，或在用户级 `%USERPROFILE%\.gradle\gradle.properties` 写 `org.gradle.daemon=true`（项目里的 `daemon=false` 是用户原有设置，不改）。
- 本地 `build` 很慢（2026-11-26，用户：一次约半小时）：两个 NeoForge 节点也改为像 1.20.1-forge 那样原地读取生成资源（一个没有源码、资源输出就是 `src\generated\resources` 的源码集 `generated`，登记进本模组；jar 直接从该目录取文件，不含 `.cache`），`processResources` 只复制手写资源（约 5,300 个文件），不再在任何资源变化时把约 3 万个生成文件复制进 `build\resources`。jar 内容不变（只少了 `.cache`）。
  同日在用户机器上实测三节点 `build`（改动后第一次，含一次性清理旧文件）共 16.5 分钟：`:1.20.1-forge:remapJar` 9 分 39 秒，三个 `jar` 各 2–3 分钟，三个 `stonecutterGenerate` 各约 1 分钟，三个 `processResources` 各 1–1.5 分钟，编译合计不到 1 分钟；构建期间 Defender（MsMpEng）占满一个核，Gradle 本身几乎空闲。同样的构建云端 2.5 分钟。慢在实时防护逐个扫描写出的小文件和大 jar，最有效的是加 Defender 排除项（或把项目放到 Dev Drive）。
  随后 `stonecutterGenerate` 不再复制资源（第 5 节），每个节点每次少写约 5,300 个文件；三个 jar 的条目与内容不变（云端逐项比对）。
- 同一节点的客户端与服务端共用 `run\<节点>\`：先等服务端启动完成再开客户端，同时启动会争写 `config\` 下的模组配置文件，可能把文件写坏。

提交前的自检（`platform` 以外必须没有任何结果）：

```powershell
Get-ChildItem src\main\java -Recurse -Filter *.java |
  Where-Object { $_.FullName -notmatch '\\platform\\' } |
  Select-String -Pattern '//\?', '/\*\?', 'net\.minecraftforge', 'net\.neoforged', '\bResourceLocation\b', '\bIdentifier\b', 'DeferredRegister', 'RegistryObject', 'DeferredHolder', 'InteractionResultHolder', 'registryOrThrow', 'lookupOrThrow', 'getMinBuildHeight', 'getMaxBuildHeight'
```

## 8. 不确定的内容先求证，不臆测

适用于代码、数据、设计文档和实装计划的全部工作，人和 AI 代理同样遵守。

1. 遇到不确定的内容——API 签名或行为、原版机制、他模组接口、数值、设计意图、文档之间的矛盾——**不凭记忆或推测写入，也不自行决定**。
2. **先搜集证据**：目标版本的反编译源码或 `javap`（第 4 节）、官方文档、依赖模组源码、`docs/` 中的设计文档与实装计划。证据足以确定时再继续，并在提交说明或文档中写明依据。
3. **证据不足，或问题属于设计取舍**（玩法、数值、命名、范围、优先级）时，**向用户提问，收到明确回答后再继续**；回答写回对应的设计文档或实装计划。
4. 等待回答期间，只做不受该问题影响的部分；受影响的部分标为“待确认”，不得用假设的默认值顶替后合并。

## 9. 面向玩家的内容保持简洁

1. **默认可见的内容只放必要信息**：物品与方块名称、默认 Tooltip、界面主窗口与状态行、聊天与动作栏提示、JEI 配方页、进度标题。不写解释、背景、操作教程，不重复其他位置已有的信息。
2. **精细描述只放在详情区域**：Shift Tooltip（仍限一句，见 `docs/text-standard.md` 第 3.1 节）、界面信息面板、JEI 信息页、Patchouli 手册条目。
3. **同一信息只在一处展开**，其他位置只给名称或图标。
4. 具体写法以 `docs/text-standard.md` 为准；新增玩家可见文本前对照其检查清单。


## 10. 与用户交流用中文

1. **与用户的一切交流都用中文**：回复、提问（含选项文字）、进度消息、完成汇报、等待确认时的说明（2026-11-05 用户要求）。人和 AI 代理同样遵守；对话中途不得改用其他语言。
2. 代码标识符、命令、文件路径、日志与报错原文保持原样，不翻译，在中文句子里引用。
3. 本条只管与用户的交流；代码注释、提交说明等其他文字的语言不在本条范围内。

## 11. 机器的方块贴图与立体模型

2026-11-26 用户确认 4 台试做机器（螺旋压榨机、浸提桶、萃取罐、培养台），定为以后所有机器的规范。

1. **16x 规范**：方块贴图 16×16，每个模型单位 1 像素；模型方块的坐标都是整数，最小的部件 1 单位；部件图集（`<id>_parts.png`，32×32）也是每单位 1 像素。
2. **贴图用脚本画**（`tools/textures/draw/`），不从参考图缩小取样。按光照层级画：每种材质九级色阶，左上受光，边缘用两个像素过渡，边框与条带投下一行短阴影，凹处在上沿和左沿下最暗。
3. **贴近参考图**：外形、布局、配色尽量与参考图一致；边框和包边的粗细也照参考图，多数只有一个像素（钢板的边、木箱的包铁、铜板的包边），不要画成两个像素的斜面（2026-11-26 用户：边框过粗，与参考图不符）。有原版对应物的材质照原版的配色与画法，例如木制机器照原版木桶：木板平涂、每块板一条亮线一条深缝、铁条两像素宽（向光一半是铁色，另一半近黑）、铁条下面一行木板在阴影里。图案自己画，不照抄原版贴图。
4. **凸出的部件用方块搭**：参考图画成凸出的部件（阀门、龙头、仪表、手轮、螺杆、出料槽等）用方块搭成立体的，形状要一眼认得出，例如龙头由伸出的管、向下的出水嘴和把手组成。部件各面由 `tools/textures/draw/part_faces.py` 按面的实际大小单独画，与方块贴图用同一套色阶。搭出来的部件不再画在面上；参考图画成开口的部分向里凹。
5. **流程**：`tools/models/machines.py` 写形状 → `preview.py` 本地预览，与参考图对照 → 在用户电脑的 Blockbench（MCP 插件）里建模、截图、导出 → `install.py` 核对导出与形状一致，写成 `<id>_shape.json` → 核对 Blockbench 工程内嵌的贴图与仓库一致 → 进游戏看。

详细做法和各机器的记录见 `docs/texture-requirements.md` 的“脚本画的方块贴图”“立体模型（Blockbench）”两节。
