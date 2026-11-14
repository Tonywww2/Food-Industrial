# I07 世界生成、作物与采集方块

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | B 物料 |
| 依赖 | I05 |
| 原文档 | [additives/08 §C](../additives/08-raw-materials.md)、[additives/07 §世界交互](../additives/07-manual-crafting.md)、[additives/11 §待定问题 14](../additives/11-roadmap.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `world`、`platform.world`、`platform`（配置、世界生成开关、事件）、`platform.datagen`、`tools/world` |

## 目标

本模组新增的矿石、作物、海洋植物、树木能在世界中获得；依赖模组缺席的节点有后备来源。

## 范围

- 做：矿石、作物、海洋植物、橡胶树（含木材套组）、硝土方块、后备来源
- 不做：世界交互的玩法逻辑（I12，含 H11 硝土生成、H15 割取）
- 不做：橡胶木告示牌、悬挂式告示牌、船、运输船（2026-11-04 定列入后续，尚未排入计划）

## 任务

- [x] 矿石与地表方块：石膏矿石、天然碱矿、岩盐、钠硝石矿、菱镁矿、后备磷灰石（[additives/08 §C](../additives/08-raw-materials.md)）；矿石物品 ID 加 `_ore`，与 I05 已注册的同名化学品分开（[additives/08 §标签体系 规则 3](../additives/08-raw-materials.md)）
- [x] 作物与灌木：甜菊、薄荷、香荚兰、蓼蓝、迷迭香、栀子等（[additives/08 §C](../additives/08-raw-materials.md)）
- [x] 海洋植物：红藻（石花菜）、麒麟菜；橡胶树（[additives/08 §C](../additives/08-raw-materials.md)）
- [x] 硝土方块（供 W01 方块转化）（[additives/07 §世界交互 H11](../additives/07-manual-crafting.md)）
- [x] 后备来源：未装热力或农夫乐事的节点提供硫、镍、锡、银、铅、乳胶、松脂、树皮、稻米的替代获取方式（[additives/11 §待定问题 14](../additives/11-roadmap.md)）。按 2026-11-04 的回答只做硫、磷灰石、稻谷、树皮；镍、锡、银、铅不做；乳胶、松脂来自本模组橡胶树与 H15
- [x] 新物品与流体加入 08 中对应的标签（`materials/<名>`，松脂流体进 `resin`）；08 §C“获取”栏写的导入（`#forge:ores/gypsum` 等）按 I06 的做法核对后写进 08 的标签表
- [x] 群系修改器按加载器分别生成（[CONVENTIONS §5](../../CONVENTIONS.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 群系修改 | Forge biome modifier | NeoForge biome modifier | 同 1.21.1 |
| 后备来源 | 运行时按是否装有热力（Thermal Foundation）、农夫乐事启用；开发环境两者都有，默认全部关闭 | 开发环境无热力：硫矿石、磷灰石矿石启用 | 开发环境两者都无：全部启用 |
| 方块类 | `BushBlock`、`AbstractTreeGrower`、`LeavesBlock` | `BushBlock`（需 `codec()`）、`TreeGrower`、`LeavesBlock` | `VegetationBlock`、`TreeGrower`、`TintedParticleLeavesBlock` |
| 木制方块构造参数 | 属性在前、`BlockSetType` 在后 | `BlockSetType` 在前 | 同 1.21.1 |
| 去皮 | `ToolAction` | `ItemAbility` | 同 1.21.1 |
| 树的泥土 | `dirt_provider` + `force_dirt` | 同 1.20.1 | `below_trunk_provider` |
| 镂空渲染 | 模型 `render_type` | 同 1.20.1 | 按贴图透明度自动 |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 新建世界中每种矿石、作物在设计的群系中出现

## 实现记录

- 方块（`world.ModWorldBlocks`）：矿石 6 种（石膏、明矾石、菱镁矿、岩盐、后备磷灰石、后备硫；硫矿石掉落硫并给经验）、地表矿 2 种（天然碱矿、钠硝石矿）、硝土；作物 5 种（甜菊、薄荷、迷迭香、蓼蓝、后备稻谷），各有农田作物方块与野生植株，收获物本身可种；栀子丛（同甜浆果丛，右键采栀子果，不扎人）；香荚兰（同可可果，长在丛林原木侧面）；石花菜、麒麟菜（同海草，骨粉使其向附近水中蔓延）；橡胶木套组 15 个方块。物品放进“矿物与原料”标签页
- 树皮（后备）与松脂流体走物料主表（08 §C，原料；松脂的 K0 含量由 I28 加上）；树皮在用斧给原木去皮时掉落（后备启用时）
- 后备开关（`world.Fallbacks`）：硫、磷灰石看 `thermal_foundation`，稻谷、树皮看 `farmersdelight`，运行时判断；配置文件 `config/foodindustrial-common.toml` 的 `[fallbacks] force_<名>` 可强制启用。世界生成用自注册的放置过滤器 `foodindustrial:enabled` 判断开关
- 资源：方块状态、方块与物品模型、占位贴图由 `tools/world/world_assets.py` 生成到 `src/main/resources`（三个节点通用；橡胶木文件取自原版橡木，三个节点的原版文件已核对一致）；26.1.2 的物品定义、战利品表、原版方块与物品标签、木材配方、世界生成与群系修改器由数据生成按节点输出
- 原版差异进 platform：`platform.world`（作物、野生植株、果丛、海草类、可去皮原木、树苗、树叶、木制方块构造）、`PlatformConfig`、`PlatformWorldgen`（开关过滤器、开发用放置与统计）、`PlatformEvents.onAxeStrip`、`PlatformRegistry.placingItem` / `doubleHighBlockItem`、`PlatformBlocks.copy` / `experienceDropping`、`PlatformDatagen` 的 JSON 写法（整数区间、树下方块、配方原料与产物、精准采集与剪刀判定、镂空渲染）；Mixin `FireBlockAccessor` 设置木头的可燃性
- 运行验证（三个节点服务端，固定种子 20261104 新建世界，`/fidev scan` 统计周围区块）：沙漠有石膏、天然碱、钠硝石；尖峭山峰有明矾石；全世界有岩盐、菱镁矿；平原有野生甜菊、薄荷；草甸有野生迷迭香；沼泽有野生蓼蓝；丛林有橡胶树、香荚兰、栀子丛；暖水海洋有石花菜、麒麟菜。后备：1.20.1 默认全部关闭、改配置后启用；1.21.1 只有硫与磷灰石；26.1.2 全部启用（沼泽出现野生稻谷）
- 未实测：用斧去皮掉落树皮（需要玩家操作）
- 2026-11-10（I12，用户确认）：新增姜黄根茎、辣椒两种作物与野生植株（丛林、热带草原；热带草原、平原），做法同上，贴图由 `tools/world/world_assets.py` 生成
- 2026-11-12（用户确认待审时补）：岩盐调低为每区块 2 次、规模 16（原 3 次、24，实测每区块约 100 块；调后 1.20.1 新世界种子 20261104 两处各 81 个区块实测每区块约 26–30 块，同处菱镁矿约 18–19 块）；原版堆肥桶可收本模组作物与植物（`PlatformCompost`：1.20.1 在通用初始化时写进堆肥桶的表，1.21 起由数据生成写 NeoForge 的数据映射 `neoforge:compostables`），概率照原版同类：七种作物的收获物 0.65（同小麦、胡萝卜）、香荚兰豆 0.65（同可可豆）、栀子果 0.3（同甜浆果）、石花菜与麒麟菜 0.3（同海草、海带）、橡胶树树苗与树叶 0.3（同其他树苗、树叶）。1.20.1 服务端实测：漏斗往堆肥桶送薄荷，堆肥桶层数上升

## 待审

- 无。已确认（2026-11-12，用户）：08 §C“世界生成”表的数值，其中岩盐按要求调低（见实现记录）；英文名；野生植株掉落 1–2 个收获物、去皮一次掉 1 个树皮；木材套组配方的原料用标签；橡胶木告示牌与船先不做；堆肥概率由实现方定（见实现记录）

## 待定

- 无。已定（2026-11-04）：同名作物照常注册本模组版本，`materials/<名>` 标签合并双方；生成数值与后备来源形式由实现方参照原版拟定，写回 [additives/08](../additives/08-raw-materials.md) 待审。另定（2026-11-04）：作物收获物本身可种；栀子按甜浆果丛、香荚兰按可可果；橡胶木做完整木材套组，告示牌与船列入后续；后备只做硫、磷灰石、稻谷、树皮，不做金属；后备在各节点注册、缺对应模组时启用，配置可强制启用；方块 ID 与名称按 08 §C 的表。松脂：先按回答登记为原料、不带纯度；之后的纯度模型 v2（07 #137）改为 K0、按松香计含量，由 I28 落实
