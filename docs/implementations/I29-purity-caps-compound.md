# I29 纯度补充：手工上限、复配产品、规范化与不带纯度的物料

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | A 基础 |
| 依赖 | I28、I08 |
| 原文档 | [additives/10 §1.1、§1.4、§1.5、§2.1、§2.2、§4.3](../additives/10-purity-jei-ae2.md)、[machines/01 §1、§1.1、§1.2](../machines/01-recipe-types.md)、[additives/07](../additives/07-manual-crafting.md)、[additives/09](../additives/09-chemical-formulas.md)、[additives/08 §C](../additives/08-raw-materials.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `core.purity`、`recipe`、`material`、`tools/materials` |

## 目标

补上 I28、I08 完成后定下的规则（2026-11-05）：手工精制配方有只在 T0 设备上生效的上限；复配产品带纯度，由配方和设备决定；装规范化模块的设备把产出降到所在档位的下限；酶、酶源、蒸馏水与多组分混合物不带纯度。物料主表按当天的 09、08 重新导出。

## 范围

- 做：`t0_max` 字段与结算、K12 类别、R19 复配的默认规则与检查、规范化的结算、不带纯度物料的导出规则与检查、物料主表重新导出、单元测试
- 不做：07 手工配方本身（I18–I20 按 07 写入，每条精制配方带 `t0_max`）
- 不做：泡打粉等复配产品的物品登记（I21）；JEI 中“手工最高”的显示（I22）
- 不做：规范化模块物品与升级槽登记（I15）；第四批内容的配方（尚无实装计划）

## 任务

- [x] 产物纯度规则新增可选字段 `t0_max`：档位名（取该物料这一档的下限）或百分数，向上取整（[additives/10 §2.1 第 4 步](../additives/10-purity-jei-ae2.md)）
- [x] `OutcomeResolver`：执行设备档次为 T0（手工工具、被动设备、营火、熔炉、烟熏炉）时套用 `t0_max`；`max` 对所有设备生效；两者都是 min(结果, max(上限, 起点纯度))，不降低起点纯度
- [x] 数据生成检查：`t0_max` 写档位名而该物料没有这一档时报错
- [x] 类别 `k12_compound`：粗制 80、精制 90、食品级 95、实验室级 `null`（[additives/10 §1.4](../additives/10-purity-jei-ae2.md)）；09 中 `gum_base` 的纯度类别已改为 K12
- [x] R19 复配的默认规则：来源方式，取产物物料的食品级下限（K12 为 95%）（[machines/01 §1.1](../machines/01-recipe-types.md)）
- [x] 数据生成检查：产物属 K12 的配方，每个带纯度的输入都必须有食品级门槛（`min_purity` 不低于该物料的 `food`）（[additives/10 §1.1](../additives/10-purity-jei-ae2.md)）
- [x] `OutcomeResolver` 第 6 步规范化（2026-11-05）：设备档案带规范化标志时，每个带纯度的产物改为所在档位下限的 b（写定值向上取整）；只降不升；劣质产物、K0 物料不变（[additives/10 §2.1 第 6 步](../additives/10-purity-jei-ae2.md)，[machines/01 §1.2](../machines/01-recipe-types.md)）
- [x] 不带纯度的物料（2026-11-05，[additives/10 §1.1](../additives/10-purity-jei-ae2.md)）：导出规则改为 09“纯度类别”为“—”与 08 分类为“酶”“耗材”的物料不带纯度，08 分类为“菌种”的记为 K11（I28 中“酶记为 K11”作废）；类别 K11 只含菌种（[additives/10 §1.4](../additives/10-purity-jei-ae2.md)）
- [x] 数据生成检查：纯度原料（`min_purity`）或 `purity_sources` 指向不带纯度的物料时报错；不带纯度的产物写了 `purity` 规则时报错；外部纯度规则命中不带纯度的物料时记警告并忽略
- [x] 物料主表按 2026-11-05 的 09、08 重新导出（[additives/09](../additives/09-chemical-formulas.md)，[additives/08 §C](../additives/08-raw-materials.md)）：
  - 新增物料注册并生成占位贴图：第四批的中间体与产物、拆分新增的 11 种铝色淀、姜黄、天然胡萝卜素、番茄红、碳酸镁（`magnesium_carbonate`）、动物脂（`animal_fat`）、动物脂脂肪酸、磷炉渣、蒸馏水；08 新增的原料、菌种、酶与表达载体（`cultures/expression_vector`，耗材）
  - 改名：`malic_acid` → DL-苹果酸，`magnesite` → 菱镁矿粉；`sodium_bisulfite` 由 K0 改为 K1
  - 改为不带纯度：酶与酶源（`enzymes`、`lysozyme`、`pancreatic_extract` 与 08 的酶）、多组分混合物（苦卤、碱液、钾碱液、硝水、海藻灰、草木灰、松脂、松香、天然油脂、脂肪酸混合物、原油、焦油等，以 09 的“—”为准）
  - 纯度数据（09 第 D 节、10 §1.5）：乳酸、磷酸、辣椒红素、红曲红素、甜菜苷的界限已改；新增溶液形态产物、铝色淀、姜黄、天然胡萝卜素、番茄红、碳酸镁、硅酸钙、亚硫酸氢钠的覆盖；删除苦卤、海藻灰、草木灰、松脂、鱼肝油、麦芽的来源纯度；姜黄来源 3%（原记在姜黄素下）；栀子黄登记物料时按 10 §1.5

## 节点差异

无。

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 单元测试：海盐重结晶带 `t0_max` = 精制，T0 上 83% → 90.6% → 94.7% → 95.2% 后不再升高；95.2% 在 T2 上执行同一配方得 98.4%（[additives/10 §2.1](../additives/10-purity-jei-ae2.md)）
- [x] 单元测试：起点已高于上限（98% 海盐在 T0 上执行）时结果不低于 98%
- [x] 单元测试：M21 配出的 K12 产物为 95.2%（食品级）；工作台结果 `{Purity:20b}` 显示 85%（粗制）（[additives/10 §4.3](../additives/10-purity-jei-ae2.md)）
- [x] 数据生成：K12 配方缺少食品级门槛时 `runData` 失败
- [x] 单元测试：规范化。上一条 T2 所得的海盐 98.4%（食品级，b 46）在带规范化标志的设备上输出 97.1%（b 39，食品级下限 97% 向上取整）；95.2%（精制，b 33）不变；劣质 85%（b 20）不变；K0 物料不变
- [x] 单元测试：酶、蒸馏水、苦卤的产出不写 `Purity`；对它们写 `min_purity` 的配方 `runData` 失败
- [x] 启动日志的物料数与重新导出的 `materials.json` 一致；`MaterialTableTest` 通过（纯度与分类对应：酶、耗材不带纯度）

## 实现记录

- `t0_max`：`recipe.PurityRule` 新增可选字段（写法同 `min_purity`：档位名或百分数），`t0MaxByte(material)` 按产物物料换算，写定值向上取整。`OutcomeResolver.apply(rule, material, start, tierBonus, t0, normalizing)`：先算模式与加成，再依次套用 `max`（所有设备）和 `t0_max`（只在 T0），每个上限都是 min(结果, max(上限, ⌊起点⌋))；起点不存在（来源方式、无参与输入）时不设下限。执行设备是否 T0 由 `DeviceProfile.tier()` 判断，`RecipeProcessor`、`/fidev recipe` 与纯度报告都走这条路径
- K12：类别由 10 §1.4 导出为 `k12_compound`（粗制 80、精制 90、食品级 95、实验室级 `null`），`PurityData.COMPOUND_CATEGORY` 与 `Material.compound()`；R19 调配的默认规则为内部常量 `PurityRule.SOURCE_FOOD`（来源方式，取产物物料食品级下限，再加档次加成），不对应 JSON 写法
- 规范化：`DeviceProfile` 新增 `normalizing`，由机器升级槽汇总（`Upgrade.normalizing`、`UpgradeSet.normalizing()`、`Upgrade.standardisation()`；模块物品由 I15 登记）。`OutcomeResolver.normalize(b, material)` 在取整之后执行：带档位的物料降到所在档位下限，劣质、K0、不带纯度不变；`purity_range` 的结果同样规范化
- 不带纯度的物料：`Purity.registerPlainMaterial` 登记它们的配方标签（主表中没有纯度类别的行）；`PurityData.Material.bears()` 判断物料是否带纯度（有档位，或 K0 有来源纯度）。外部物品只属于这类物料的标签时没有纯度，命中它的外部规则记警告并忽略，每次重载每个物品只记一次；本模组附带的外部规则（`checkBundled`）命中这类物料时数据生成失败
- 检查（`recipe.RecipeChecks`，数据生成时报错即失败）：`t0_max` 档位名在产物物料中不存在；`min_purity` 或 `purity_sources` 指向不带纯度的输入（含 K0 无来源纯度）；有 K12 产物的配方，每个带纯度的输入的 `min_purity` 不低于该物料的食品级下限，物料没有食品级时也报错。工作台等原版格式配方由 `RecipeChecks.checkCraftingJson` 检查同一条：platform 新增 `PlatformDatagen.addCheck`（对某类生成文件运行检查，三个节点共用）、`recipeResultItem`、`recipeIngredients`（读产物 ID 与原料，兼容 1.20.1 的 `item` 与 1.21 起的 `id`）
- 导出：`export_materials.py` 按 09“纯度类别”与 08 分类决定纯度；09 英文名没有括号时日常名称的英文与专业名相同；08 的名称括号不拆日常名称（用户确认，2026-11-05）。主表 911 种（化学品 699、原料 65、菌种 62、酶 63、催化剂 21、耗材 1），720 种有纯度类别，其中 697 种带纯度；新增 501 种物料，贴图脚本生成 405 张物品占位贴图（流体共用灰度贴图）
- 文档：09、08 补全新增流体的颜色与英文名；08 中“斐济曲霉（暂译）”改为“斐济曲霉”（“暂译”移到来源栏），四个“又名”括号去掉（果胶酯酶、葡糖异构酶、溶血磷脂酶、胰凝乳蛋白酶），名称括号的规则写入 08 与文本规范第 2.1 节（均为用户确认，2026-11-05）
- 文本检查：中文名称里紧跟数字、字母、连字符的半角括号（GB 2760 的“聚氧乙烯(20)…”、取代基“3-(3-羟基-4-甲氧基苯基)丙醛”）不算半角标点；“盐(NaCl)”这类仍报错
- 验证：三个节点 `compileJava`、`runData`、`test` 通过（每个节点 70 项单元测试），启动日志“Registered 911 materials (702 items, 209 fluids)”与 `materials.json` 一致。临时加入错误配方后 1.20.1 `runData` 失败并逐条报出：K12 配方的盐、蔗糖缺少食品级门槛（机器配方与工作台配方各一条）、酶的产物写了纯度规则、乙酸 `t0_max: "crude"`、酶与蒸馏水写 `min_purity`、苦卤写入 `purity_sources`。1.20.1 服务端实测：`/fidev purity` 中酶（带 `{Purity:50b}`）、蒸馏水桶、苦卤桶、表达载体都没有纯度，胶基、醋酸菌、盐带纯度；外部规则命中橡木原木（属不带纯度的 `tree_bark` 标签）时记警告，原木仍无纯度
- 完成后用户确认（2026-11-05）：
  - 新增流体的颜色（95 种）与英文名按实现方拟定；为满足文本规范改的 4 个英文名照用：Alcaligenes Faecalis Myxogenes、Protease (Microbial)、Diacetyl Tartaric Acid Esters of Mono- and Diglycerides、Engineered Strain (Phospholipid Cholesterol Acyltransferase)
  - 上限的下限取“起点纯度向下取整”，各模式相同：来源方式有参与输入时起点照常按加权平均计算，上限不低于它；没有参与输入时没有起点，上限可以低于来源值（例如来源 95% 而 `max` 90% 时得 90%）
  - 中文名称里紧跟数字、字母、连字符的半角括号按上条例外放行，09 的写法不改

## 待定

无。
