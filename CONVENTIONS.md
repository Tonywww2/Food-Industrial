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
| `PlatformConfig` | `commonBoolean(section, name, comment, default)`：在 `FoodIndustrial.init()` 中声明 `config/foodindustrial-common.toml` 的选项 |
| `PlatformWorldgen` | 放置过滤器 `foodindustrial:enabled` 的开关（`setSwitches`）；开发用的放置与统计 |
| `platform.world` | `PlatformPlants`（作物、野生植株、果丛、海草类、树苗、树叶、可去皮原木）、`PlatformWood`（木制方块与可燃性） |

| `platform.machine` | `PlatformBlockEntity`（存取、同步、ticker、`onLoaded` / `onRemoved` / 邻居更新钩子）、`PlatformEntityBlock`、`PlatformMemberBlock`（无方块实体的成员方块的放置 / 移除钩子）、`PlatformMachineCapabilities`、`PlatformNeighborCache` |
| `platform.recipe` | `PlatformRecipes`（业务 `MapCodec` 注册配方类型，`loaded`、`jsonOps`）、`PlatformIngredients`（纯度原料、原料编解码） |
| `PlatformFuel` | 原版熔炉燃烧时间、燃烧后剩下的容器 |
| `PlatformCommands` | 命令的物品参数（`itemArgument`、`item`）与 2 级权限（`gameMasters`）；带 `CommandBuildContext` 的注册用 `PlatformEvents.onRegisterCommands(CommandRegistrar)`（I34） |
| `PlatformPlayerData`、`PlatformPlayerEvents`、`PlatformAttributes` | 随玩家存档的数据；玩家生命周期、吃喝、睡眠、交互拦截等事件；按语义属性加减带 ID 的临时修正（I35） |
| `platform.client.PlatformHud` | 相机角度、全屏遮罩、生存背包右侧的面板与按钮、Apothic Attributes 面板中的修正图标（I35） |

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
- 数据生成输出按节点分开：`versions/<节点>/src/generated/resources`（该目录会自动加入对应节点的资源）。原因：1.21 起数据包目录改为单数（`recipe`、`loot_table`、`advancement`、`tags/item` 等），1.20.1 仍为复数；26.1 物品还需要 `assets/<ns>/items/*.json`。
- `forge:` / `neoforge:` 前缀的数据（biome modifier、conditions 等）必须按加载器分开维护。

## 6. 依赖与 Mixin

- 依赖在 `versions/<节点>/gradle.properties` 声明，构建脚本按键自动处理：
  - `deps.jei=<版本>`：编译 JEI API，开发运行完整 JEI；
  - `deps.patchouli=<版本>`：编译 Patchouli API，开发运行完整 Patchouli；
  - `deps.mod.<名字>=<maven 坐标>`：编译 + 开发运行（Forge 节点自动走 Loom 重映射）。
  - `deps.opt.<名字>=<maven 坐标>`：可选联动模组（2026-11-11）。总在编译类路径上；开发运行时只在 `-PfiWith=<名字>,…`（或 `all`）点名时加入，便于分别测试装与不装的情况。根 `gradle.properties` 的 `fi.devWith=<名字>,…` 列出每次开发运行都加入的可选模组（现为 Jade，2026-11-12 用户定，用于测试）；`-PfiWithout=<名字>,…` 临时去掉，测试不装的情况。
  - `deps.dev.<名字>=<maven 坐标>`：只进开发运行时、不参与编译（2026-11-11），用于填充物料标签的模组（如潘马斯作物），让开发环境的 JEI 能显示用到这些标签的配方。
  - 某节点没有对应版本时删掉该键即可；源码中的联动代码必须用同样的条件排除。
  - 数据生成运行时，构建脚本把 `deps.mod.*`、`deps.opt.*` 及其内嵌模组的 jar 列表写到 `build/fi-external-jars.txt`，经系统属性 `foodindustrial.externalJars` 传给数据生成，用来从联动配方中去掉与他模组自带配方重叠的输入（I25）。
- 加依赖前先确认它对加载器版本的要求（例如 JEI 19.42+ 要求 NeoForge ≥ 21.1.238，所以 1.21.1 节点固定在 19.39.0.372）。
- Modrinth 上 Fabric / NeoForge 共用同一版本号时，用 **版本 ID** 固定（例如 26.1 的 Patchouli 用 `2CsnFLom`）。
- Forge 节点的依赖若用 Jar-in-Jar 内嵌其他模组（如 Thermal Foundation 内嵌 `thermal_core`），`build.gradle.kts` 会在配置阶段自动解出到 `versions/1.20.1-forge/build/jij/` 并加入开发运行时；无需手动处理。解出的模组以 `jij:<文件名>` 模块（flatDir 仓库）加入，与外层模组同批重映射——否则外层模组调用内嵌模组的方法（GTCEu 调 Registrate）会留着 SRG 名而报 `NoSuchMethodError`；内嵌的游戏库（清单 `FMLModType: GAMELIBRARY`，如 `mixinextras-forge`、Registrate）按模组加入，FML 才会加载其 `MixinConfigs`、让它看得到原版类（2026-11-11）。
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
