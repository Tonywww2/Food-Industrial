# I08 配方框架

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | C 框架 |
| 依赖 | I02、I03、I28 |
| 原文档 | [machines/01 §1–3](../machines/01-recipe-types.md)、[machines/02 §3](../machines/02-architecture.md)、[additives/10 §2](../additives/10-purity-jei-ae2.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `recipe`、`platform.recipe` |

## 目标

19 种通用配方类型可用数据包书写，任何设备执行时纯度与产率按规则结算；2 种世界交互类型登记 ID 与默认规则，配方格式随 I12 实装。

## 范围

- 做：配方模型、序列化、匹配索引、纯度与产率结算、数据生成检查
- 不做：设备与机器（I09–I17）
- 不做：原版设备桥接（I13）
- 不做：W01、W02 的配方格式与 `BlockTransformRecipe`、`HarvestRecipe`（移到 I12，2026-11-04 定）

## 任务

- [x] `ProcessType` 注册：R01–R19、W01–W02，是否可用产率、默认纯度规则；槽位上限与允许的条件由登记的设备推导，不单独声明（2026-11-04 定；[machines/01 §2、§3.1](../machines/01-recipe-types.md)）
- [x] `ProcessRecipe` 与 `Codec`：输入、输出（`min_tier`、`chance`、每个产物的 `purity`）、`purity_sources`、`purity_range`、时长、能耗、条件、`mode`、`manual`、`vanilla_devices`；字段写法见 [machines/01 §3.1](../machines/01-recipe-types.md)（2026-11-04 定）（[machines/02 §3](../machines/02-architecture.md)）
- [x] `Conditions` 与设备能力的集合比较，结果缓存；条件按词表分类书写（[machines/01 §3](../machines/01-recipe-types.md)，[文本规范 §5](../text-standard.md)）
- [x] `DeviceProfile`：档次默认值（速率、纯度加成、产率、能耗系数）；设备登记表（档次、配方类型、能力、模式、槽位数、是否手工），供基准档次与可执行检查使用（[machines/01 §1](../machines/01-recipe-types.md)）
- [x] `OutcomeResolver`：起点纯度按数量加权平均（默认只计 K1–K11 的输入）；来源、除杂、保持、劣化四种方式；比基准档次每高一档杂质再乘 0.75；`max`；结果向下取整；换算只用 `PurityScale`；产率只用于物理类配方，多出部分按概率产出、期望值不变（2026-11-04 定，[machines/01 §1 规则 3](../machines/01-recipe-types.md)）；`chance` 与 `purity_range` 的随机数由设备传入（[additives/10 §2.1](../additives/10-purity-jei-ae2.md)，[machines/01 §1 规则](../machines/01-recipe-types.md)）
- [x] 基准档次：配方重载时取能执行它的设备中最低的档次，存进配方索引（[additives/10 §2.1 第 3 步](../additives/10-purity-jei-ae2.md)）
- [x] 配方不写 `purity` 时按配方类型的默认规则（[machines/01 §1.1](../machines/01-recipe-types.md)）
- [x] `RecipeIndex`：重载时按第一输入建表，标签重载时清空（[machines/02 §6.4 第 13 条](../machines/02-architecture.md)）
- [x] `HomogenizeRule`：M21 均质按数量加权平均；归一取外部物品的有效纯度（[additives/10 §2.2 第 3 条](../additives/10-purity-jei-ae2.md)，[additives/10 §6](../additives/10-purity-jei-ae2.md)）
- [x] 数据生成检查：时长为 4 的倍数、每条配方至少一台设备可执行、每个带纯度的产物有纯度规则（写明或默认）（[machines/02 §6.1 第 1 条](../machines/02-architecture.md)，[machines/03 §3](../machines/03-io-layout.md)，[additives/10 §2.1](../additives/10-purity-jei-ae2.md)）
- [x] 纯度报表（数据生成）：每种物料在 T0–T4 各档能达到的最高纯度、最早达到食品级的档次，附 03、13、14 的最早 / 最终阶段，只出表、不自动判定（2026-11-04 定；[machines/01 §1.1](../machines/01-recipe-types.md)）
- [x] 数据生成检查：配方输入只引用本模组标签，输出写具体物品（自 I06 移入，2026-11-04 定；[additives/10 §3](../additives/10-purity-jei-ae2.md)，[additives/08 §标签体系 规则 7](../additives/08-raw-materials.md)）
- [x] 条件键为开放集合；只属于食品的规则写在配方或数据里，不写进框架代码（[machines/02 §8](../machines/02-architecture.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 配方输入 | 容器包装 | `RecipeInput` | `RecipeInput` |
| 数据目录 | `recipes` | `recipe` | `recipe` |
| 顶层 `conditions` | Forge 加载条件，不能用；配方条件写在 `requirements` | 不冲突 | 不冲突 |
| 可选字段 | DataFixerUpper 6 不报错，改用 `StrictCodecs.optional` | 报错 | 报错 |
| 编码标签原料 | `JsonOps` 即可 | 同左 | 必须用带注册表的 ops，数据生成的原料用 `Sink.ingredient` 构造 |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 单元测试：4 份 83% 海盐重结晶（去除 50%，基准 T0）在 T0 / T1 / T2 得 90.6% / 92.6% / 94.3%，T0 反复三次得 97.1%（[additives/10 §2.1](../additives/10-purity-jei-ae2.md)）；T2 设备产率 1.1：主产物 1 个时多出 1 个的概率为 10%，4 个时 40%，流体 250 mB 固定多 25 mB
- [x] 配方 JSON 在三个节点上往返一致

## 实现记录

- 代码：`core.Tier`；`recipe` 包的 `ProcessType`、`ProcessTypes`、`ProcessRecipe`、`ProcessInput`、`ProcessOutput`、`PurityRule`、`Conditions`、`Capabilities`、`VanillaDevice`、`DeviceProfile`、`DeviceSpec`、`OutcomeResolver`、`RecipeIndex`、`RecipeChecks`、`PurityReport`、`RecipeGen`、`HomogenizeRule`；`core.data.StrictCodecs`；platform 新增 `PlatformRecipes.loaded / jsonOps / networkRoundTrip`、`PlatformIngredients.candidateItems / unwrap`、`PlatformDatagen.Sink.report / ops / ingredient`
- 配方字段按 [machines/01 §3.1](../machines/01-recipe-types.md)：条件写在 `requirements`（1.20.1 Forge 占用顶层 `conditions`，2026-11-04 实测后改名）；条件值必须是条件词表里的词，写错的值、写错的可选字段在三个节点都报错；`mode` 也按词表校验，`mode` 类别的词随机器加入
- 设备登记表 `DeviceSpec`：档次与档案、配方类型、可提供的全部能力（含模式与模块）、I/O/FI/FO 槽位数、手工设备只做带 `manual` 的配方、按时间运行的设备只做带 `time` 的配方、原版方块只做在 `vanilla_devices` 里列出它的配方。配方重载后第一次查询时建 `RecipeIndex`：按第一个输入的候选物品或流体建表，记下每条配方的基准档次与可执行设备；没有设备能执行的配方在日志里汇总一行
- 起点纯度只计“参与”的输入：写了 `purity_sources` 时按它，否则为分档物料（K1–K11）的输入；读取当时生效的纯度数据。读输入的规则在没有起点纯度时退回来源方式
- 产率：主产物多出 数量 × (倍率 − 1)，整数部分必出、小数部分按概率；`chance` 与 `purity_range` 的随机数由设备传入。纯度结算是纯函数
- `HomogenizeRule` 只有结算（加权平均向下取整、归一取有效纯度）；按标签生成供 JEI 显示的均质、归一配方随 M21（I15）与 JEI（I22）做
- 数据生成：内容通过 `RecipeGen.register` 加配方；写出时跑 `RecipeChecks`（错误使数据生成失败），并写 `versions/<节点>/build/reports/foodindustrial/purity_report.md`；`tools/materials/purity_report_stages.py` 在报表后附上 03、13、14 的阶段原表。目前还没有配方，报表为空
- 开发命令（只在开发环境）：`/fidev recipes`（基准档次、可执行设备、检查、JSON / 网络 / 文件往返）、`/fidev process`、`/fidev findrecipe`、`/fidev purityreport`；`devtest` 登记了 6 台测试设备
- 单元测试 48 项三个节点通过：海盐重结晶 T0 / T1 / T2 得 90.6% / 92.6% / 94.3%，T0 反复三次 90.6% → 94.7% → 97.1%；石膏来源 80 得 81% / 84% / 88%；海藻灰 8.6% / 11%；产率 T2 主产物 1 个 10%、4 个 40% 多 1 个，250 mB 固定多 25 mB；条件解析、默认规则、纯度规则编码
- 运行验证（2026-11-04）：三个节点 `runServer` 加载测试数据包 `fitest` 的 10 条配方：8 条载入，JSON、网络、数据包文件三方往返一致；写错的 `chance`、条件词各报一条清楚的错误；海盐重结晶、海水晒盐（83%、苦卤 7.3%）、石膏研磨（T2 平均 1.1047 个）、`min_tier` 馏分（T1 不产、T3 平均 24.8 mB）、R10 默认 75% 与多馏分 90%、带“精制”门槛的配方查找（96% 找到、90% 找不到）、外部物品输入与无设备配方的检查结果都与文档一致
- 数据生成：三个节点用临时配方验证标签原料按节点格式写出（26.1 为 `"#foodindustrial:chemicals/salt"`），验证后删除

## 待定

- 无
