# I28 纯度模型 v2

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | A 基础 |
| 依赖 | I02、I03、I05 |
| 原文档 | [additives/10 §1–2、§4、§4.7](../additives/10-purity-jei-ae2.md)、[machines/01 §1、§1.1](../machines/01-recipe-types.md)、[additives/07](../additives/07-manual-crafting.md)、[additives/09](../additives/09-chemical-formulas.md)、[text-standard §1、§3.1](../text-standard.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `core.purity`、`api.purity`、`tools/materials` |

## 目标

把已实装的 0.5% 刻度与“按设备档次定纯度”换成 v2：纯度是目标物质的湿基质量分数，存为两端对数刻度的 byte；纯度由原料和工序决定，设备档次只做加成；档位界限按物料类别给出。做完后 I08 起的配方结算直接按 v2 实现。

## 与已完成计划的关系

- I02、I03、I05 已按 0.5% 刻度完成。本计划改写其中的刻度、档位、外部默认值、创造栏列表与桶的默认纯度。
- 保持不变：外部纯度规则的文件位置、字段、多条命中取最低、同步方式；两种原料类型的注册、序列化与 `isEmpty` 委托；物料主表与导出脚本的结构；纯度载体由物料注册时登记。
- 被替代的条目：I02 任务 1–3 与验收第 2 条；I03 `getItems` 的显示列表；I05 的“从世界取回的桶写入 35%”与“创造栏按五档标准值各列一个”。
- I08 未开始，已按 v2 改写任务。

## 范围

- 做：刻度与运算、物料类别与档位数据、外部默认值、显示、物料主表导出、迁移旧代码、单元测试
- 不做：配方中的纯度结算（I08，按 [additives/10 §2.1](../additives/10-purity-jei-ae2.md)）
- 不做：JEI 中的纯度规则显示（I22）
- 不做：各批内容配方的纯度规则（I18–I21，按 [additives/07](../additives/07-manual-crafting.md) 与 [machines/01 §1.1](../machines/01-recipe-types.md)）

## 任务

刻度（[additives/10 §1.2](../additives/10-purity-jei-ae2.md)）：

- [x] `PurityScale` 对照表：256 项 b → p；b = −128 为 0，b = 127 为 1，其余 p = 1 / (1 + 10^(−b / 25.4))，类加载时用 `StrictMath.pow` 生成，之后只查表
- [x] 两种取整，都在表上二分查找：`ceilOf(x)` 取 p(b) ≥ x 的最小 b（配方与数据里写定的数值）；`floorOf(x)` 取 p(b) ≤ x 的最大 b（计算结果）
- [x] 运算（双精度计算，最后 `floorOf`）：除杂 p ÷ (p + (1 − p)(1 − e)·0.75^Δ)；来源同式（p 取写定值，e = 0）；劣化 p × (1 − l)；按数量加权平均；外部默认值 c ÷ (2 − c)（[additives/10 §1.6、§2.1](../additives/10-purity-jei-ae2.md)）
- [x] 显示 `format(b)`：向下截断，<1% 两位小数（不足 0.01% 写“<0.01%”），1%–10% 一位，10%–90% 整数，90%–99.9% 一位，≥99.9% 两位；只有 −128、127 显示 0%、100%（[text-standard §1 第 6 条](../text-standard.md)）

档位与物料数据（[additives/10 §1.3–1.5、§4.7](../additives/10-purity-jei-ae2.md)）：

- [x] `PurityGrade` 只留五个档位的名称、颜色、本地化键；去掉固定下限与标准值
- [x] 类别数据：`data/foodindustrial/purity_categories/*.json`，12 个类别 K0–K11 的四档下限；加载时检查下限严格递增、小数位数不超过显示规则在该区间的位数
- [x] 物料纯度数据：`category`（必填）、`source_purity`、`grades` 覆盖（`null` = 不适用）；`gradeOf(material, b)`、`lowerBound(material, grade)`；没有粗制档的物料按最低一档计算外部默认值
- [x] 数据随登录与 `/reload` 同步到客户端（与外部纯度规则同一时机）

物料主表（[additives/09](../additives/09-chemical-formulas.md)）：

- [x] `tools/materials/export_materials.py` 读 09 的“纯度类别”一栏写入 `materials.json`；08 §C 中分类为菌种、酶的记为 K11；“—”记为不带纯度（复配产品 `gum_base` 由带纯度改为不带）
- [x] 10 §1.5 的覆盖、07 与 machines/01 中出现的 `source_purity`（盐 83%、石膏 80% 等）写入物料数据；写在主表新列还是单独 JSON 由实现方决定，决定后写回 09 的说明
- [x] 07 用到但主表里还没有的物料补行：海藻灰、草木灰（K0，有含量）；碱液、钾碱液、硝水、石花菜冻（K0，不带纯度）；鱼肝油（K7）。英文名与颜色按现有做法由实现方拟定，标“待审”

外部物品与缺标签（[additives/10 §1.6](../additives/10-purity-jei-ae2.md)）：

- [x] 外部物品、缺 `Purity` 的本模组物品、从世界取回的桶：取外部默认值；K0 取 `source_purity`，没有则不带纯度
- [x] 外部纯度规则的 `purity`：百分数，或档位名按命中物品所属物料换算（`poor` = 外部默认值）；查不到物料时记警告并忽略这一项
- [x] 自带规则的数据生成检查（I06 的 `BundledPurityRules`）：上限由 60% 改为该物料的粗制下限

原料类型（[additives/10 §4.1、§4.2、§4.4](../additives/10-purity-jei-ae2.md)）：

- [x] `min_purity` 档位名按物料界限换算成 b（向上取整）；新增可选字段 `material`，省略时由 `base` 的标签查物料；查不到物料或该物料没有这一档时加载报错
- [x] 百分数门槛不再要求是 0.5 的倍数
- [x] `getItems()`：本模组物品返回门槛值与高于门槛的各档下限（低档在前）；外部物品只在门槛不高于外部默认值时返回

显示（[text-standard §3.1、§4](../text-standard.md)，[additives/10 §5](../additives/10-purity-jei-ae2.md)）：

- [x] Tooltip 纯度行用新显示规则，颜色按该物料的档位；K0 写“含量 7.3%”（灰色 §7），新增键 `tooltip.foodindustrial.content`
- [x] 创造栏：带纯度的物料按档各列一个（外部默认值与各档下限）
- [x] `/fidev purity` 同时输出 byte、显示值、档位与所属类别

迁移旧代码：

- [x] 删除 k = b + 128 的换算、JSON 百分数 0.5 倍数校验与五档标准值；数据包与数据生成里已写成百分数的值照旧可读（含义仍是百分数）
- [x] 1.20.1 工作台配方结果里写死的 `{Purity:…b}` 按新刻度重新生成（[additives/10 §4.3](../additives/10-purity-jei-ae2.md)）：本模组目前没有带纯度结果的工作台配方，无需重新生成；I08 起的数据生成直接按新刻度写

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 存储 | NBT `Purity`（byte） | 数据组件（byte） | 数据组件（byte） |
| 换算与显示 | 同一份 `PurityScale`，不分节点 | 同左 | 同左 |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 单元测试：对照表端点与 [additives/10 §1.2](../additives/10-purity-jei-ae2.md) 表中各值的显示一致；每个 b 有 `ceilOf(p(b)) = b`、`floorOf(p(b)) = b`
- [x] 单元测试：海盐 83% 去除 50%，T0 / T1 / T2 得 90.6% / 92.6% / 94.3%，T0 反复三次得 97.1%（[additives/10 §2.1](../additives/10-purity-jei-ae2.md)）
- [x] 单元测试：外部默认值 糖 74%、盐 82%、乙酸 1.8%（[additives/10 §1.6](../additives/10-purity-jei-ae2.md)）；外部规则 `"purity": "crude"` 对糖得 85%
- [x] 单元测试：显示截断 b 25 → 90.6%、b 77 → 99.90%、b −127 → <0.01%、b 127 → 100%；加权平均 1 个物品折合 1,000 mB
- [x] 三个节点的对照表逐项相同（测试中比较 256 个双精度值的哈希）
- [x] 运行验证：Tooltip、`/fidev purity` 与服务端判定一致；`/reload` 后类别数据与外部规则同时更新

## 实现记录

- 刻度：`core.purity.PurityScale`（对照表、`ceilOf` / `floorOf`、`ofPercent`、`removal` / `source` / `degrade` / `weightedAverage` / `externalDefault`、`format`、`checkWritten`）。对照表 256 个双精度值的 SHA-256 写进 `PurityScaleTest`，三个节点（Java 17 / 21 / 25）一致
- 档位与物料数据：`core.purity.PurityData`。类别文件 `data/foodindustrial/purity_categories/<类别>.json`，物料文件 `data/foodindustrial/purity_materials/<物料>.json`（ID 由路径给出，文件里的 `id` 可省略，写了须与路径一致）；类别文件四档都写，`null` 为无此档；物料文件只写覆盖的档。内置数据来自 `materials.json` 与 `purity_categories.json`，数据生成输出上述文件（保留 `null`：`PlatformDatagen.Sink.jsonKeepingNulls`）
- 加载顺序：配方加载时要把档位名换算成 b，所以纯度数据先于配方加载（`PlatformEvents.onDataReloadEarly`）：1.20.1、1.21.1 在准备阶段末尾发布（配方在应用阶段解析），26.1 在 `prepareSharedState` 发布（配方在准备阶段解析）。三个节点实测：数据包把 K1 精制改为 80% 后 `/reload`，已加载配方的“精制”门槛随之变为 81%，删去后恢复 95.2%；1.20.1 农夫乐事厨锅用 82% 的输入在改动后煮出产物、恢复后不反应
- 同步：`PlatformNetwork` 通道 `purity_data`，登录与 `/reload` 时在外部规则之前发送；客户端用同一解析器重建。1.20.1、1.21.1、26.1 客户端的 Tooltip 与服务端一致；26.1 客户端在 `/reload` 后显示新的外部默认值
- 物料归属：本模组载体按注册时登记的物料；外部物品与流体取物料表顺序中第一个标签包含它的物料（随标签与数据重载失效）。原料的门槛缺 `material` 时，从 `base` 的写法（`{"tag": …}`、`{"item": …}`、`"#标签"`、物品 ID、列表）找物料，列表须指向同一物料；外部物品自身查不到物料时，按原料所查物料的外部默认值计
- 原料：`core.purity.PurityThreshold`（写法、`material`、换算后的 b 与物料）；网络只传 b 与物料。26.1 的原料编解码改为手写 `MapCodec`，用调用方的 ops 编码，`#标签` 不会被展开成物品列表
- `getItems()`：外部物品按有效纯度（外部规则或外部默认值）不低于门槛时列出，与 `test()` 一致；没有外部规则时即“门槛不高于外部默认值”
- 从世界取回的桶：液体方块在取桶时写入该物料当时的缺省值（外部默认值，K0 为来源纯度）。发射器经加载器的流体接口取桶，不带 `Purity`，按同一缺省值计（三个节点实测：苦卤 7.3%、硫酸 43%）
- 物料主表：09 新增 §D“纯度数据”（来源纯度与覆盖），导出时与 10 §1.5 逐项核对；09 §C 补 7 行（`kelp_ash`、`wood_ash`、`soda_lye`、`potash_lye`、`saltpeter_liquor`、`agar_jelly`、`fish_liver_oil`），松脂由 08 §C 移入 09 §C（K0，含量按松香计）。主表共 410 种，383 种有纯度类别，其中 365 种带纯度（K0 中只有写了来源纯度的苦卤、松脂、海藻灰、草木灰带含量）；`gum_base` 改为不带纯度
- 调试：`/fidev purity` 输出 b、显示值、档位（K0 为 content）与类别；新增 `/fidev recipe <配方 ID>` 列出已加载配方的门槛
- 单元测试：`PurityScaleTest`（12 项）、`PurityGradeTest`（2 项）、`PurityDataTest`（7 项），三个节点共 35 项全部通过
- 2026-11-12 审定（11 问题 43）：松脂按 08 §C 导出（原料流体、不带纯度、颜色 `#C8902A`，创造栏“矿物与原料”）；`malt` 不带纯度；`getItems()` 本就用含外部规则的有效纯度，10 §4.4 已照实装；条状琼脂、甜菊叶浸液、栀子染液按覆盖指向 `agar`、`steviol_glycosides`、`gardenia_yellow`，`papaya_latex` 导出为不带纯度的原料流体

## 2026-11-12 审定（11 问题 43）

设计侧审完本计划的待审六条（[additives/11 问题 43](../additives/11-roadmap.md)）。照准的：七种新物料的 ID、英文名与颜色；来源纯度取 07 手工链的写定值（纯碱 20%、碳酸钾 50%、乙酸 4%、乳酸 1.2%、辣椒红素 0.2%）；野生菌种 20%。要改的：

- [x] 松脂改回不带纯度的原料：主表从 [additives/09](../additives/09-chemical-formulas.md) 删去 `resin`，恢复 [additives/08 §C](../additives/08-raw-materials.md) 的行（分类“原料”、颜色 `#C8902A`），创造栏回到“矿物与原料”。07 第 137 号的“含松香约 70%”是水蒸气蒸馏的得率，不是纯度（松香与松节油由同一配方联产，14 第 137 号）
- [x] 麦芽不记纯度：`malt` 去掉 45%，按 K11 酶源不带纯度（[additives/10 §1.1](../additives/10-purity-jei-ae2.md)）；07 第 96 号的产物写“麦芽”，不写“β-淀粉酶（K11）”
- [x] `getItems()` 用含外部规则的有效纯度判断是否列出外部物品：[additives/10 §4.4](../additives/10-purity-jei-ae2.md) 已按实装改，照实装写
- [x] 07 用到而主表没有的四种：条状琼脂、甜菊叶浸液、栀子染液按“覆盖”处理，物料就是 `agar`、`steviol_glycosides`、`gardenia_yellow`（09 §D 已有来源纯度），不另设物料；木瓜乳汁确实缺，08 §C 已补 `papaya_latex`（流体、原料、不带纯度，同无花果乳汁），不按蛋白酶的 40% 记

## 待审

- 无。2026-11-12 的六条已审定（11 问题 43），处理见上节。

## 待定

- 旧存档：不写迁移（2026-11-04 定）。开发存档里已有的纯度按新刻度解读
- [additives/11 待定问题](../additives/11-roadmap.md) 第 16–19 条：手工反复精制的上限、默认去除比例、色价类天然色素的界限、加权平均的计量
