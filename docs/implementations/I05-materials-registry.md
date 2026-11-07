# I05 物料与流体注册

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | B 物料 |
| 依赖 | I02、I04 |
| 原文档 | [additives/09](../additives/09-chemical-formulas.md)、[additives/08 §C](../additives/08-raw-materials.md)、[machines/03 §4](../machines/03-io-layout.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `material`、`registry`、`platform`（流体、桶、标签页）、`tools/materials` |

> 纯度模型已改为 v2（2026-11-04）：本计划中与 [I28](I28-purity-model-v2.md) 冲突的条目以 I28 为准。

## 目标

09 中的全部物料、08 中本模组新增的原料、菌种、酶、催化剂都注册为物品或流体，带化学式与默认纯度显示。

## 范围

- 做：物料主表、物品与流体注册、创造模式标签页
- 不做：标签（I06）
- 不做：世界生成与作物方块（I07）
- 不做：配方（I18–I20）

## 任务

- [x] 物料主表：把 09 的表（ID、名称、化学式、状态、GT 对应、备注）存为资源文件 `foodindustrial/materials.json`，注册时读取；新增物料只改这张表（[additives/09 §表示约定](../additives/09-chemical-formulas.md)）
- [x] 固体化学品注册为物品；液、气、溶液注册为流体，气体带“气态”标记、无桶（[machines/03 §4](../machines/03-io-layout.md)）
- [x] 桶的纯度：储罐与桶互倒时保留纯度；从世界取回的桶写入劣质 35%（[machines/03 §4](../machines/03-io-layout.md)）
- [x] 矿石与同名化学品分开注册，矿石 ID 加 `_ore`（[additives/08 §标签体系 规则 3](../additives/08-raw-materials.md)）。本计划注册化学品一侧（`gypsum`、`trona` 等）；矿石物品与方块随 I07 注册
- [x] 菌种、酶、催化剂物品（[additives/08 §C 菌种与酶](../additives/08-raw-materials.md)，[additives/08 §C 催化剂与助剂](../additives/08-raw-materials.md)）
- [x] 化学式、GT 对应写入物料定义，供 Tooltip 与 I26 使用
- [x] 创造模式标签页：化学品、矿物与原料、菌种与酶、机器、成品
- [x] 物料分类字段做成开放集合，预留危险性字段（不实现行为），不写只适用于食品的假设（[machines/02 §8](../machines/02-architecture.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 流体注册 | Forge `FluidType` | NeoForge `FluidType` | 同 1.21.1 |
| 桶的流体能力 | `initCapabilities` 返回 `FluidBucketWrapper` 子类 | `RegisterCapabilitiesEvent` 注册 `FluidHandler.ITEM` | `RegisterCapabilitiesEvent` 注册 `Fluid.ITEM`（`ItemAccessResourceHandler`） |
| 物品模型 | `models/item` | `models/item` | `models/item` + `items/` 物品定义 |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 09 表中每一行都能在游戏中找到对应物品或流体，名称通过 I04 检查

## 实现记录

- 主表：`tools/materials/export_materials.py` 读 09 的三张表和 08 §C 的原料、菌种与酶、催化剂表，写 `src/main/resources/foodindustrial/materials.json`；跳过标“见 09”“（I07）”“（I20）”“原版”的行。共 401 种：化学品 332（固 229、液 71、气 21、溶液 11）、催化剂 17、菌种 30、酶 14、原料 8（液 5、固 3）。水用原版，不注册
- 代码：`material.MaterialDef`（含开放的分类字段与预留的危险性字段）、`MaterialTable`、`MaterialItem`、`ModMaterials`；`registry.ModCreativeTabs`；`platform.PlatformFluids`、`FluidContainers`、`PlatformFluidContainers`、`PlatformRegistry.creativeTab`
- 注册结果：293 个物品、108 种流体（气体 21 种无桶无方块，其余 87 种有桶和流体方块）；启动日志 `Registered 401 materials (293 items, 108 fluids)`
- 纯度：化学品、菌种、酶及其流体与桶带纯度；催化剂、原料不带。无存储值时一律按劣质 35% 计（2026-11-04 定为全局默认，见 [additives/10 §1](../additives/10-purity-jei-ae2.md)）
- 桶：桶与储罐互倒保留纯度（三个节点实测：95% 的桶倒入再取出仍为 95%；储罐 80% 灌入空原版桶得 80%，靠覆写 `FluidType.getBucket`）。原版发射器取回的桶不带数据，按默认 35% 计（三个节点实测）。玩家用桶取回写入 35% 的代码在 `FluidContainers.Liquid.pickupBlock`，云端测试无玩家输入，未实测
- 化学式按元素族着色（2026-11-05 改，[text-standard §3.1](../text-standard.md)）：`core.text.ElementColors` 存 118 种元素的族与颜色，`ChemicalFormula.runs` 把化学式切成同色同大小的段，Tooltip（`Tooltips.coloredFormula`）与图标标签共用；单元测试检查物料表里每个化学式的元素都认识
- 图标上的化学式（2026-11-04 加）：化学品物品与其桶在界面格子顶部画一行化学式（`material.MaterialLabels` → `PlatformClient.registerSlotLabel`，三个节点都用加载器的物品装饰 `IItemDecorator`），规则见 [text-standard §3.1](../text-standard.md)
- 创造模式标签页：五个，空的不显示（机器、成品暂空）；带纯度的物料按五档标准值各列一个
- 资源：`tools/materials/placeholder_textures.py` 生成物品的占位贴图和流体的静止、流动贴图；桶不另画贴图，用加载器的动态桶模型（原版桶 + 流体贴图按流体颜色着色，2026-11-04 改；1.20.1 `forge:fluid_container`、1.21.1 `neoforge:fluid_container` 并登记物品着色，26.1 在物品定义里写 `neoforge:fluid_container`）；数据生成写物品模型、流体方块模型与方块状态，26.1.2 另写 `items/` 物品定义。三个节点客户端启动无本模组物料的缺失模型警告
- 单元测试：`MaterialTableTest`（ID 唯一、流体有颜色、纯度与分类对应、名称通过文本检查）

- I28 改写（2026-11-04）：是否带纯度改由“纯度类别”决定（K0 只有写了来源纯度的带含量），`materials.json` 增加 `bearer` 与 `purity`（类别、来源纯度、覆盖）；从世界取回的桶写入物料的缺省值（外部默认值，K0 为来源纯度）；创造栏按物料的外部默认值与各档下限各列一个。主表补 7 种、松脂移入 09，共 410 种。详见 [I28 实现记录](I28-purity-model-v2.md)

## 待审

- 英文名、流体颜色、Shift 描述占位（专业名称）均由实现方拟定，见 [additives/09](../additives/09-chemical-formulas.md) 与 [additives/08 §C](../additives/08-raw-materials.md) 的对应列
- 08 §C 新增行的 ID：`iron_catalyst`、`cobalt_salt`、`rhodium_iodide_catalyst`、`platinum_gauze`、`noble_metal_catalyst`、`diatomite`、`ion_exchange_resin`、`macroporous_resin`、`silver_catalyst`、`vpo_catalyst`、`palladium_on_carbon`
- β-淀粉酶的来源（[additives/08 §C 菌种与酶](../additives/08-raw-materials.md)）
- 桶的名称写作“完整名称 + 桶”：`乙醇（酒精）桶`、`Ethanol (Alcohol) Bucket`
- 标签页英文名：Chemicals、Minerals and Raw Materials、Cultures and Enzymes、Machines、Products
- 英文名大小写：虚词与化学前缀小写（[text-standard §2.1](../text-standard.md)）

## 待定

- 无。已定（2026-11-04）：09（及 08 §C 本模组新增原料）为主表，脚本导出 JSON；英文名、分类等新字段加为表格的列，英文名由实现方拟定待审；Shift 描述暂以专业名称占位；贴图先用程序生成的占位图；液体与溶液有桶、可倒进世界，气体无桶；桶装流体带纯度，倒进世界后不保留，用桶取回按劣质 35% 计（[machines/03 §4](../machines/03-io-layout.md)）
