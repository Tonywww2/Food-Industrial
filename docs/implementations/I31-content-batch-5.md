# I31 第五批内容（香料、加工助剂、营养强化剂、胶基物质、表 A.1 补漏）

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | E 内容 |
| 依赖 | I30 |
| 原文档 | [additives/17](../additives/17-batch-5.md)、[additives/18](../additives/18-batch-5-flavors.md)、[additives/19](../additives/19-batch-5-synthetic-flavors.md)、[additives/05 §H](../additives/05-processes.md)、[additives/08 §C](../additives/08-raw-materials.md)、[additives/09 §E](../additives/09-chemical-formulas.md)、[additives/16](../additives/16-batch-5-backlog.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `content.batch5`（非香料，数据生成）、`content.flavors`（香料，按模板生成）、`content.Cultures` |

> 第五批设计已于 2026-11-11 完成（17–19）；设计中提出的问题 29–41 已于同日决定（[additives/11](../additives/11-roadmap.md)），17–19 与 08、09、10、14 已按决定修改。

## 目标

GB 2760-2024 表 A.1 漏列 4 种、加工助剂 32 种、营养强化剂 105 种（含公告增补 9 种）、胶基物质 21 种（424–584），以及食品用香料 1,845 条（表 B.2 385 条、表 B.3 1,460 条）在原料齐全时都能沿设计路线做出；只缺“暂无来源”原料的路线照常写入。

## 范围

- 做：424–584 的配方；香料 1,845 条的物料与配方（按路线模板批量生成）；新工艺 P97–P104 与并入现有工艺的 13 种做法；新增中间体的配方；新菌种、酶、催化剂；三处机器模式调整
- 做：物料补登（09 第 A–C 节新增 430 种、第 E 节香料 1,801 种；08 第 C 节原料 238 种、菌种与酶 16 种、催化剂与吸附助滤耗材 7 种），重新导出 `materials.json`
- 不做：暂无来源原料的获取途径（[additives/11 §待定问题 21](../additives/11-roadmap.md)）
- 不做：标“暂缓（11 问题 36）”的 5 条专利结构风味物（S1471、S1472、S1484、S1485、S1505）；它们专用的中间体也暂不注册

## 任务

- [x] 实现约定沿用 [additives/04 §实现约定（I18–I20）](../additives/04-production-routes.md) 与 I30 的补充约定（暂无来源照写、酶分级、固定化酶、分子蒸馏、基因重组、天然色素提取线）
- [x] 物料（[additives/09](../additives/09-chemical-formulas.md)）：`tools/materials/export_materials.py` 增加读取第 E 节（表头与第 A–C 节相同）；“分类”新值**香料**对应新的创造模式标签页；纯度类别为“—”的物料不带纯度（天然香料混合物、吸附助滤耗材）。已有物料被第五批复用的 46 种只在备注加了编号，不重复注册
- [x] 原料（[additives/08 §C](../additives/08-raw-materials.md)）：新增原料、菌种与酶、催化剂按表注册；221 种“暂无来源”的原料注册为空标签，纯度报表把用到它们的路线单列为“原料暂无来源”；四种热带树木胶乳为流体
- [x] 新工艺（[additives/05 §H](../additives/05-processes.md)）：P97 高压自由基聚合（M23 新增聚合模式，高压）、P98 费-托合成（M23 + 铁催化剂）、P99 萃取精馏（M22 第二流体输入为溶剂）、P100 乙炔乙烯基化、P101 炔化与部分加氢、P102 尿素包合分离、P103 蒸汽吹出提溴、P104 直接还原（M20 新增还原模式 `reduction`）；M23 乙氧基化模式扩到环氧丙烷（模式 ID 不改）
- [x] 香料路线模板（[additives/18 §1](../additives/18-batch-5-flavors.md)）：NT1–NT18、ST01–ST61 各写一个数据生成函数，输入（原料、前体、产物、阶段）后生成整条配方链；同一模板的配比、时长、来源值一致。路线表“原料”一栏的编码、编号、ID、标签按 18 的约定解析
- [x] “蒸馏”一步（18 §1.1）：同一配方类型 R10 在 M08（T1–T2，只取主馏分）与 M22（T3 起）都注册，纯度按设备档次给；ST50、ST51、ST55 只在 M22
- [x] 一物一号：与香料重叠的 7 条（S0003、S0004、S0312、S0364、S0877、S0883、S1206）不另建物料与配方，香料编码只作别名；另有 36 条香料经核对已是已有物料（模板 ST26），只在 JEI 中补香料编码
- [x] 可选路线照常注册（[additives/11 §待定问题 5](../additives/11-roadmap.md)）：氢氰酸（ST59、S0327、S0340 等）、硫化氢、硫酸二甲酯等试剂的路线
- [x] 新菌种与工程菌：同 I30（平板分离、纯化、扩培；基因重组用表达载体）
- [x] 汇总时改过的 3 条路线（S0307、S1431、S1455）以 19 为准，不按各组原稿
- [x] 改动已实装的内容（[additives/11 §问题 31、32、38](../additives/11-roadmap.md)）：I20 删除 133 B 配方（该路线改挂 S0224 B）；I19 的“原油切割”产出表加入丙烷、丁烷、轻石脑油（428、432、433、437 不另写原油配方）；`zinc_chloride` 改为化学品 K1（催化剂槽照样接受）；`paraffin_wax` 改 K7；`alpha_tocopherol` 显示名改为 d-α-生育酚（462 直接用它）
- [x] 新矿石（I07）：硒矿石、铬铁矿石的方块、标签与世界生成（[additives/08 §C](../additives/08-raw-materials.md) I07 两表，数值待审）
- [x] 动物血为流体（`animal_blood`）：原版生肉在 M12 压榨的副产；同时接受他模组血液标签
- [x] 联产写成一条多产出配方（[additives/18 §1.1](../additives/18-batch-5-flavors.md)）：如羰基合成的正丁醛与异丁醛、松节油精馏的蒎烯类、S1079 / S1080、S1081 / S1087 / S1088、S1230 / S1231；同一缩合物再分馏得到的单一异构体（S0719、S0931）是下游一步
- [x] 铬铁矿路线（527 B、528 B，经铬酸钠、重铬酸钠）按可选剧毒路线注册
- [x] 多步路线（[additives/18 §1.4](../additives/18-batch-5-flavors.md)、[additives/19 §6](../additives/19-batch-5-synthetic-flavors.md)，11 问题 40）：每个模板只写核心配方，提纯为可选精制；一行多个模板的 182 条路线按 19 §6 合为一条（M14 与 M23 同属 R11，合并后在 M23 上做）；配方类型不同的已改用登记的中间体（茉莉酸、异丙醇胺、高圣草酚、对乙基苯磺酸钠、大豆油脂肪酸、发酵起子培养液、柏木油、4,4-二甲基-1,3-二氧六环、1-庚烯与 9 种浸膏）

## 每一项的通用步骤

1. 物料：按 09 注册物品或流体（I05、I29），名称按文本规范（I04；香料名称的“又名”按 18 总说明处理），标签按 08（I06）。
2. 配方：非香料按 17 的路线逐条写；香料按模板函数生成，配方类型与设备按 [machines/01 §2](../machines/01-recipe-types.md)。
3. 门槛：需要纯度的输入写纯度原料（[additives/10 §4](../additives/10-purity-jei-ae2.md)）；不带纯度的物料不写纯度也不设门槛。
4. 检查：JEI 可见、最早阶段能做出、化学式与 09 一致。

## 清单

“最早阶段”取设计文档的阶段栏：本步设备档次，上游更高时括号注明（香料按前体关系图推算）。

### 非香料（424–584）

| 编号 | 名称 | 最早阶段 | 物料 ID（09 / 08） | 路线（原文档） | 状态 |
| --- | --- | --- | --- | --- | --- |
| 424 | 茶多酚棕榈酸酯 | T2（氯化亚砜为 T3） | `tea_polyphenol_palmitate` | [additives/17 §A](../additives/17-batch-5.md) | 已完成 |
| 425 | 硬脂酰乳酸钙 | T2 | `calcium_stearoyl_lactylate` | [additives/17 §A](../additives/17-batch-5.md) | 已完成 |
| 426 | 氢化松香甘油酯 | T3 | `hydrogenated_rosin_glycerol_ester` | [additives/17 §A](../additives/17-batch-5.md) | 已完成 |
| 427 | 木松香甘油酯 | T2 | `wood_rosin_glycerol_ester` | [additives/17 §A](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 428 | 丙烷 | T3 | `propane` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 429 | 硅酸镁（三硅酸镁） | T2 | `magnesium_trisilicate` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 430 | 氧化镁（包括重质、轻质） | T1 / T2 | `magnesium_oxide` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 431 | 1-丁醇 | T3 | `n_butanol` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 432 | 植物油抽提溶剂 | T3 | `vegetable_oil_extraction_solvent` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 433 | 石油醚 | T3 | `petroleum_ether` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 434 | 异丙醇 | T3 | `isopropanol` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 435 | 乙醚 | T2 / T3 | `diethyl_ether` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 436 | 乙酸乙酯 | T2 / T3 | `ethyl_acetate` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 437 | 正己烷 | T3 | `n_hexane` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 438 | 凹凸棒黏土 | T2 | `attapulgite_clay` | [additives/17 §B](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 439 | 不溶性聚乙烯聚吡咯烷酮（PVPP） | T3 | `pvpp` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 440 | 活性白土 | T2 | `activated_clay` | [additives/17 §B](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 441 | 聚苯乙烯 | T2 | `polystyrene` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 442 | 聚丙烯酰胺 | T2（丙烯腈为 T3，腈水合酶为 T4） | `polyacrylamide` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 443 | 膨润土 | T2 | `bentonite` | [additives/17 §B](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 444 | 珍珠岩 | T2 | `perlite` | [additives/17 §B](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 445 | 高碳醇脂肪酸酯复合物 | T2（十八醇为 T3） | `higher_alcohol_fatty_ester_complex` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 446 | 聚氧丙烯甘油醚（GP） | T3 | `gp_polyether` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 447 | 聚氧丙烯氧化乙烯甘油醚（GPE） | T3 | `gpe_polyether` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 448 | 聚氧乙烯聚氧丙烯胺醚（BAPE） | T3 | `bape_polyether` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 449 | 聚氧乙烯聚氧丙烯季戊四醇醚（PPE） | T3 | `ppe_polyether` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 450 | 蔗糖聚氧丙烯醚 | T3 | `sucrose_polyoxypropylene_ether` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 451 | 滑石粉 | T2 | `talc` | [additives/17 §B](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 452 | 石蜡 | T3 | `paraffin_wax` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 453 | 甲酸钠 | T3 | `sodium_formate` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 454 | 磷酸二氢铵 | T2（氨为 T3） | `monoammonium_phosphate` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 455 | 维生素 B 族（发酵用） | — | —（只引用单体） | [additives/17 §B](../additives/17-batch-5.md) | 已完成（不设物料，引用单体） |
| 456 | 五碳双缩醛（戊二醛） | T3 | `glutaraldehyde` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 457 | 氧化亚氮 | T2 / T3 | `nitrous_oxide` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 458 | 月桂酸 | T3 | `lauric_acid` | [additives/17 §B](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 459 | 过氧化氢 | T3 | `hydrogen_peroxide` | [additives/17 §B](../additives/17-batch-5.md) | 已完成 |
| 460 | 棕榈酸视黄酯 | T2（视黄醇为 T4，见 107 B；脂肪酶 102 为 T4） | `retinyl_palmitate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 461 | 麦角钙化醇（维生素 D2） | T4 | `ergocalciferol` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 462 | d-α-生育酚 | T3（65 为 T3） | `alpha_tocopherol` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 463 | dl-α-生育酚 | T3 | `dl_alpha_tocopherol` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 464 | d-α-醋酸生育酚 | T3 | `d_alpha_tocopheryl_acetate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 465 | dl-α-醋酸生育酚 | T3 | `dl_alpha_tocopheryl_acetate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 466 | 维生素 E 琥珀酸钙 | T2（463 为 T3） | `tocopheryl_calcium_succinate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 467 | d-α-琥珀酸生育酚 | T2（462 为 T3） | `d_alpha_tocopheryl_succinate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 468 | dl-α-琥珀酸生育酚 | T2（463 为 T3） | `dl_alpha_tocopheryl_succinate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 469 | 植物甲萘醌（维生素 K1） | T3 | `phylloquinone` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 470 | 硝酸硫胺素 | T2（109 为 T4） | `thiamine_mononitrate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 471 | 核黄素-5'-磷酸钠 | T2（63 为 T4；γ-丁内酯为 T3） | `riboflavin_phosphate_sodium` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 472 | 盐酸吡哆醇 | T3 | `pyridoxine_hydrochloride` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 473 | 5'-磷酸吡哆醛 | T2（472 为 T3） | `pyridoxal_phosphate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 474 | 氰钴胺 | T4 | `cyanocobalamin` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 475 | 盐酸氰钴胺 | T2（474 为 T4） | `cyanocobalamin_hydrochloride` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 476 | 羟钴胺 | T4 | `hydroxocobalamin` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 477 | L-抗坏血酸钾 | T2（28 为 T4） | `potassium_ascorbate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 478 | 维生素 C 磷酸酯镁 | T3（28 为 T4） | `magnesium_ascorbyl_phosphate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 479 | D-泛酸钙 | T2（D-泛解酸内酯为 T4，β-丙氨酸为 T3） | `calcium_d_pantothenate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 480 | D-泛酸钠 | T2（同 479） | `sodium_d_pantothenate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 481 | D-生物素 | T3 | `d_biotin` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 482 | 富马酸亚铁 | T2（57 为 T3） | `ferrous_fumarate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 483 | 柠檬酸铁 | T2（三氯化铁为 T3） | `ferric_citrate_powder` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 484 | 乳酸亚铁 | T2 | `ferrous_lactate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 485 | 氯化高铁血红素 | T2 | `hemin` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 486 | 铁卟啉 | T2（382 为 T4） | `heme_iron`（与 493 共用，2026-11-12） | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 487 | 甘氨酸亚铁 | T2 | `ferrous_bisglycinate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 488 | 还原铁 | T2 / T3 | `reduced_iron` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 489 | 羰基铁粉 | T3 | `carbonyl_iron` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 490 | 碳酸亚铁 | T2 | `ferrous_carbonate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 491 | 柠檬酸亚铁 | T2 | `ferrous_citrate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 492 | 琥珀酸亚铁 | T2（178 为 T3） | `ferrous_succinate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 493 | 血红素铁 | T2（382 为 T4） | `heme_iron` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 494 | 电解铁 | T3 | `electrolytic_iron` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 495 | L-乳酸钙 | T2 | `calcium_l_lactate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 496 | L-苏糖酸钙 | T2（28 为 T4） | `calcium_l_threonate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 497 | 甘氨酸钙 | T2 | `calcium_glycinate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 498 | 天门冬氨酸钙 | T2（L-天冬氨酸为 T4） | `calcium_aspartate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 499 | 柠檬酸苹果酸钙 | T2（56 为 T3） | `calcium_citrate_malate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 500 | 甘油磷酸钙 | T2 | `calcium_glycerophosphate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 501 | 骨粉（超细鲜骨粉） | T2 | `bone_powder` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 502 | 甘氨酸锌 | T2 | `zinc_glycinate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 503 | 氧化锌 | T2 / T3 | `zinc_oxide` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 504 | 乳酸锌 | T2 | `zinc_lactate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 505 | 柠檬酸锌 | T2 | `zinc_citrate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 506 | 氯化锌 | T2 | `zinc_chloride` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 507 | 乙酸锌 | T2 | `zinc_acetate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 508 | 碳酸锌 | T2 | `zinc_carbonate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 509 | 亚硒酸钠 | T3（B 焙烧 T2） | `sodium_selenite` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 510 | 硒酸钠 | T2（509 为 T3） | `sodium_selenate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 511 | 硒蛋白 | T2 | `selenoprotein` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 512 | 富硒食用菌粉 | T4 | `selenium_mushroom_powder` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 513 | L-硒-甲基硒代半胱氨酸 | T3（氨基酰化酶为 T4） | `methylselenocysteine` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 514 | 硒化卡拉胶 | T2（509 为 T3） | `selenium_carrageenan` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 515 | 富硒酵母 | T4 | `selenium_yeast` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 516 | 磷酸氢镁 | T2 | `magnesium_hydrogen_phosphate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 517 | 葡萄糖酸镁 | T2（13 为 T3） | `magnesium_gluconate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 518 | 葡萄糖酸铜 | T2（13 为 T3） | `copper_gluconate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 519 | 柠檬酸铜 | T2 | `copper_citrate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 520 | 碳酸铜 | T2 | `copper_carbonate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 521 | 氯化锰 | T2 | `manganese_chloride` | [additives/17 §C](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 522 | 碳酸锰 | T2 | `manganese_carbonate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 523 | 柠檬酸锰 | T2 | `manganese_citrate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 524 | 葡萄糖酸锰 | T2（13 为 T3） | `manganese_gluconate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 525 | 葡萄糖酸钾 | T2（13 为 T3）/ T4 | `potassium_gluconate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 526 | 碘化钠 | T2 | `sodium_iodide` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 527 | 硫酸铬 | T2（B 铬铁矿焙烧为 T2） | `chromium_sulfate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 528 | 氯化铬 | T2 | `chromium_chloride` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 529 | 钼酸钠 | T2 | `sodium_molybdate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 530 | 钼酸铵 | T2（氨为 T3） | `ammonium_molybdate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 531 | 氯化胆碱 | T3 | `choline_chloride` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 532 | 酒石酸氢胆碱 | T3 | `choline_bitartrate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 533 | 肌醇（环己六醇） | T3（320 为 T3） | `myo_inositol` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 534 | L-盐酸赖氨酸 | T4 | `l_lysine_hydrochloride` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 535 | L-赖氨酸天门冬氨酸盐 | T2（534、85 为 T4） | `l_lysine_l_aspartate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 536 | L-肉碱 | T4 | `l_carnitine` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 537 | L-肉碱酒石酸盐 | T2（536 为 T4） | `l_carnitine_l_tartrate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 538 | γ-亚麻酸 | T2（T3 起 M22 精馏提纯） | `gamma_linolenic_acid` | [additives/17 §C](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 539 | 低聚果糖（菊苣来源） | T2 / T3 | `fructooligosaccharides` | [additives/17 §C](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 540 | 1,3-二油酸 2-棕榈酸甘油三酯 | T3（脂肪酶 102 为 T4） | `dioleoyl_palmitoyl_glycerol` | [additives/17 §C](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 541 | 花生四烯酸油脂（高山被孢霉） | T4 | `arachidonic_acid_oil` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 542 | 乳铁蛋白 | T4 | `lactoferrin` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 543 | 酪蛋白钙肽 | T3 | `casein_calcium_peptide` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 544 | 酪蛋白磷酸肽 | T3 | `casein_phosphopeptide` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 545 | L-蛋氨酸 | T3（氨基酰化酶为 T4） | `l_methionine` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 546 | L-酪氨酸 | T4 | `l_tyrosine` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 547 | L-色氨酸 | T4 | `l_tryptophan` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 548 | 低聚半乳糖（乳糖来源） | T3（乳糖酶 103 为 T4） | `galactooligosaccharides` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 549 | 多聚果糖（菊苣来源） | T3 | `inulin` | [additives/17 §C](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 550 | 棉子糖（甜菜来源） | T4 | `raffinose` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 551 | 5'-单磷酸胞苷 | T4（73 前段为 T4） | `cytidylic_acid` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 552 | 5'-单磷酸尿苷 | T4 | `uridylic_acid` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 553 | 5'-尿苷酸二钠 | T2（552 为 T4） | `uridylic_disodium` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 554 | 5'-胞苷酸二钠 | T2（551 为 T4） | `cytidylic_disodium` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 555 | 维生素 K2 | T4 | `menaquinone_7` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 556 | （6S）-5-甲基四氢叶酸氨基葡萄糖盐 | T3（叶酸 111 为 T4） | `levomefolate_glucosamine` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 557 | （6S）-5-甲基四氢叶酸钙 | T3（叶酸 111 为 T4） | `levomefolate_calcium` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 558 | 柠檬酸亚铁钠 | T2（115 为 T1） | `sodium_ferrous_citrate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 559 | L-苏糖酸镁 | T2（496 前段 28 为 T4） | `magnesium_l_threonate` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 560 | 酵母 β-葡聚糖 | T2 | `yeast_beta_glucan` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 561 | 2'-岩藻糖基乳糖 | T4 | `fucosyllactose` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 562 | 乳糖-N-新四糖 | T4 | `lacto_n_neotetraose` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 563 | 3'-唾液酸乳糖钠盐 | T4 | `sialyllactose_sodium` | [additives/17 §C](../additives/17-batch-5.md) | 已完成 |
| 564 | 巴拉塔树胶 | T0 / T2 | `massaranduba_balata` | [additives/17 §D](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 565 | 节路顿胶 | T1 / T2 | `jelutong` | [additives/17 §D](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 566 | 来开欧胶 | T0 / T2 | `leche_caspi` | [additives/17 §D](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 567 | 芡茨棕树胶 | T0 / T2 | `chicle` | [additives/17 §D](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 568 | 糖胶树胶 | T0 / T2 | `chicle` | [additives/17 §D](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 569 | 丁二烯-苯乙烯 75/25、50/50 橡胶（丁苯橡胶） | T3 | `styrene_butadiene_rubber` | [additives/17 §D](../additives/17-batch-5.md) | 已完成 |
| 570 | 聚丁烯 | T3 | `polybutene` | [additives/17 §D](../additives/17-batch-5.md) | 已完成 |
| 571 | 聚乙烯 | T3 | `polyethylene` | [additives/17 §D](../additives/17-batch-5.md) | 已完成 |
| 572 | 异丁烯-异戊二烯共聚物（丁基橡胶） | T3 | `butyl_rubber` | [additives/17 §D](../additives/17-batch-5.md) | 已完成 |
| 573 | 部分二聚松香甘油酯 | T2 | `dimerized_rosin_glycerol_ester` | [additives/17 §D](../additives/17-batch-5.md) | 已完成 |
| 574 | 部分氢化松香甘油酯 | T3 | `hydrogenated_rosin_glycerol_ester` | [additives/17 §D](../additives/17-batch-5.md) | 已完成 |
| 575 | 部分氢化松香季戊四醇酯 | T3 | `hydrogenated_rosin_pentaerythritol_ester` | [additives/17 §D](../additives/17-batch-5.md) | 已完成 |
| 576 | 部分氢化松香甲酯 | T3 | `hydrogenated_rosin_methyl_ester` | [additives/17 §D](../additives/17-batch-5.md) | 已完成 |
| 577 | 醋酸乙烯酯-月桂酸乙烯酯共聚物 | T3 | `vinyl_acetate_laurate_copolymer` | [additives/17 §D](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 578 | 合成树脂（包括萜烯树脂） | T3（N298 为 T1） | `terpene_resin` | [additives/17 §D](../additives/17-batch-5.md) | 已完成 |
| 579 | 聚合松香（包括木松香、妥尔松香）甘油酯 | T3 | `polymerized_rosin_glycerol_ester` | [additives/17 §D](../additives/17-batch-5.md) | 已完成 |
| 580 | 聚乙烯蜡均聚物 | T3 | `polyethylene_wax` | [additives/17 §D](../additives/17-batch-5.md) | 已完成 |
| 581 | 石油石蜡（费-托合成法） | T3 | `fischer_tropsch_wax` | [additives/17 §D](../additives/17-batch-5.md) | 已完成 |
| 582 | 小烛树蜡 | T1 / T2 | `candelilla_wax` | [additives/17 §D](../additives/17-batch-5.md) | 已完成（原料暂无来源） |
| 583 | 三乙酸甘油酯 | T2 | `triacetin` | [additives/17 §D](../additives/17-batch-5.md) | 已完成 |
| 584 | 海藻酸铵 | T2 | `ammonium_alginate` | [additives/17 §D](../additives/17-batch-5.md) | 已完成 |

### 食品用香料（1845 条）

| 编号 | 名称 | 最早阶段 | 物料 ID（09） | 路线（原文档） | 状态 |
| --- | --- | --- | --- | --- | --- |
| N001 | 丁香叶油 | T1 | `clove_leaf_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N002 | 丁香花蕾酊(提取物) | T1 | `clove_bud_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N003 | 丁香花蕾油 | T1 | `clove_bud_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N004 | 罗勒油 | T1 | `basil_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N005 | 八角茴香油 | T1 | `star_anise_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N006 | 九里香浸膏 | T2（437 为 T3） | `jasmin_orange_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N007 | 广藿香油 | T1 | `patchouli_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N008 | 万寿菊油 | T1 | `tagetes_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N010 | 小豆蔻油 | T1 | `cardamom_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N011 | 小豆蔻酊 | T1 | `cardamom_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N012 | 小茴香酊 | T1 | `fennel_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N013 | 山苍子油 | T1 | `litsea_cubeba_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N014 | 山楂酊 | T1 | `hawthorn_fruit_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N015 | 大蒜油 | T1 | `garlic_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N016 | 大蒜油树脂 | T2 | `garlic_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N017 | 天然康酿克油 | T1 | `green_cognac_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N019 | 云木香油 | T1 | `costus_root_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N020 | 月桂叶油 | T1 | `sweet_bay_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N021 | 乌梅酊 | T1 | `wumei_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N022 | 布枯叶油 | T1 | `buchu_leaf_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N023 | 可可酊 | T1（M11 为 T2） | `cocoa_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N024 | 可可壳酊 | T1（M11 为 T2） | `cocoa_husk_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N025 | 甘松油 | T1 | `nardostachys_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N026 | 甘草酊 | T1 | `licorice_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N027 | 甘草流浸膏 | T1 | `licorice_fluid_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N028 | 冬青油 | T1 | `wintergreen_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N029 | 白兰花油 | T1 | `michelia_alba_flower_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N030 | 白兰叶油 | T1 | `michelia_alba_leaf_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N031 | 白兰花净油 | T2（N032 为 T3） | `michelia_alba_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N032 | 白兰花浸膏 | T2（437 为 T3） | `michelia_alba_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N033 | 白芷酊 | T1 | `angelica_dahurica_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N034 | 白柠檬油 | T1 | `lime_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N035 | 白柠檬萜烯 | T2 | `lime_oil_terpenes` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N036 | 生姜油树脂 | T2 | `ginger_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N037 | 肉豆蔻油 | T1 | `nutmeg_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N038 | 肉豆蔻酊 | T1 | `nutmeg_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N039 | 中国肉桂油 | T1 | `cassia_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N040 | 中国肉桂皮酊(提取物) | T1 | `cassia_bark_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N041 | 红茶酊 | T1 | `black_tea_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N042 | 印蒿油 | T1 | `davana_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N043 | 吐鲁酊(提取物) | T1 | `tolu_balsam_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N044 | 吐鲁香膏 | T1 | `tolu_balsam_gum` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N045 | 豆豉酊 | T1 | `douchi_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N046 | 杜松籽油（刺柏子油） | T1 | `juniper_berry_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N047 | 芫荽籽油 | T1 | `coriander_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N048 | 芹菜花油 | T1 | `celery_flower_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N049 | 芹菜籽油 | T1 | `celery_seed_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N050 | 牡荆叶油 | T1 | `vitex_leaf_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N051 | 圆柚油 | T1 | `grapefruit_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N052 | 苍术脂 | T1 | `atractylodes_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N053 | 枣子酊 | T1 | `jujube_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N054 | 玫瑰油 | T1 | `rose_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N055 | 玫瑰净油 | T2（N056 为 T3） | `rose_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N056 | 玫瑰浸膏 | T2（437 为 T3） | `rose_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N057 | 鸢尾浸膏 | T2 | `orris_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N058 | 鸢尾脂（鸢尾凝脂） | T1 | `orris_root_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N059 | 杭白菊花油 | T1 | `hangzhou_chrysanthemum_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N060 | 杭白菊花浸膏（杭菊花流浸膏） | T1 | `hangzhou_chrysanthemum_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N061 | 枫槭油 | T1 | `maple_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N062 | 枫槭浸膏 | T2 | `maple_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N063 | 岩蔷薇浸膏（赖百当浸膏） | T2（437 为 T3） | `labdanum_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N064 | 咖啡酊 | T1 | `coffee_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N065 | 罗汉果酊 | T1 | `luohan_fruit_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N066 | 金合欢浸膏 | T2（437 为 T3） | `cassie_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N067 | 依兰依兰油 | T1 | `ylang_ylang_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N068 | 大花茉莉净油 | T2（N069 为 T3） | `jasmine_grandiflorum_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N069 | 大花茉莉浸膏 | T2（437 为 T3） | `jasmine_grandiflorum_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N070 | 小花茉莉净油 | T2（N071 为 T3） | `jasmine_sambac_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N071 | 小花茉莉浸膏 | T2（437 为 T3） | `jasmine_sambac_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N072 | 佛手油 | T1 | `sarcodactylis_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N073 | 圆叶当归根酊（独活酊） | T1 | `angelica_root_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N074 | 洋葱油 | T1 | `onion_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N075 | 生姜油 | T1 | `ginger_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N076 | 姜黄油 | T1 | `turmeric_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N077 | 姜黄油树脂 | T2 | `turmeric_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N078 | 姜黄浸膏 | T2 | `turmeric_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N079 | 葫芦巴酊 | T1 | `fenugreek_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N080 | 玳玳花油 | T1 | `daidai_flower_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N081 | 玳玳花浸膏 | T2（437 为 T3） | `daidai_flower_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N082 | 玳玳果油 | T1 | `daidai_fruit_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N083 | 柚皮油 | T1 | `pummelo_peel_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N084 | 柏木叶油(北美香柏) | T1 | `cedar_leaf_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N085 | 枯茗籽油（孜然油） | T1 | `cumin_seed_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N086 | 柠檬油 | T1 | `lemon_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N087 | 无萜柠檬油 | T2 | `terpeneless_lemon_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N088 | 柠檬油萜烯 | T2 | `lemon_oil_terpenes` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N089 | 柠檬叶油 | T1 | `petitgrain_lemon_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N090 | 柠檬草油 | T1 | `lemongrass_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N091 | 栀子花浸膏 | T2（437 为 T3） | `gardenia_flower_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N092 | 树兰花油 | T1 | `aglaia_flower_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N093 | 树兰花酊 | T1 | `aglaia_flower_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N094 | 树兰花浸膏 | T2（437 为 T3） | `aglaia_flower_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N095 | 树苔净油 | T2（N096 为 T3） | `treemoss_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N096 | 树苔浸膏 | T2（437 为 T3） | `treemoss_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N097 | 香叶油（玫瑰香叶油） | T1 | `geranium_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N098 | 除萜香叶油 | T2 | `terpeneless_geranium_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N099 | 香风茶油（香茶菜油） | T1 | `xiangfengcha_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N101 | 香柠檬油 | T1 | `bergamot_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N102 | 香根油 | T1 | `vetiver_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N103 | 香根浸膏 | T2 | `vetiver_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N104 | 香荚兰豆酊 | T1 | `vanilla_bean_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N105 | 香荚兰豆浸膏(提取物) | T2 | `vanilla_bean_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N106 | 香附子油 | T1 | `cyperus_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N107 | 香葱油 | T1 | `chives_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N108 | 香紫苏油 | T1 | `clary_sage_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N109 | 香榧子壳浸膏 | T2 | `torreya_shell_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N110 | 橘子油 | T1 | `mandarin_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N111 | 除萜橘子油 | T2 | `terpeneless_mandarin_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N112 | 酒花酊 | T1 | `hops_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N113 | 酒花浸膏 | T2（437 为 T3） | `hops_extract_solid` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N114 | 桉叶油(蓝桉油) | T1 | `eucalyptus_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N115 | 海狸酊 | T1 | `castoreum_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N116 | 斯里兰卡肉桂皮油 | T1 | `cinnamon_bark_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N117 | 斯里兰卡肉桂叶油 | T1 | `cinnamon_leaf_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N118 | 桂花净油 | T2（N120 为 T3） | `osmanthus_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N119 | 桂花酊 | T1 | `osmanthus_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N120 | 桂花浸膏 | T2（437 为 T3） | `osmanthus_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N121 | 桂圆酊 | T1 | `longan_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N122 | 留兰香油 | T1 | `spearmint_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N123 | 核桃壳提取物 | T1 | `walnut_hull_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N124 | 素方花净油 | T2（`common_jasmine_concrete` 为 T4） | `common_jasmine_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N125 | 桦焦油 | T1 | `sweet_birch_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N126 | 蚕豆花酊 | T1 | `broad_bean_flower_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N127 | 绿茶酊 | T1 | `green_tea_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N128 | 野玫瑰浸膏 | T2（437 为 T3） | `wild_rose_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N129 | 甜小茴香油 | T1 | `sweet_fennel_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N130 | 甜叶菊油 | T1 | `stevia_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N131 | 甜橙油 | T1 | `orange_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N132 | 除萜甜橙油 | T2 | `terpeneless_orange_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N133 | 甜橙油萜烯 | T2 | `orange_oil_terpenes` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N134 | 菊苣浸膏 | T2 | `chicory_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N135 | 晚香玉浸膏 | T2（437 为 T3） | `tuberose_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N136 | 紫罗兰叶净油/浸膏 | T2（437 为 T3） | `violet_leaf_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N137 | 椒样薄荷油 | T1 | `peppermint_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N138 | 黑加仑酊 | T1 | `blackcurrant_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N139 | 黑加仑浸膏 | T2（437 为 T3） | `blackcurrant_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N140 | 槐树花净油 | T2（N141 为 T3） | `sophora_flower_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N141 | 槐树花浸膏 | T2（437 为 T3） | `sophora_flower_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N142 | 辣椒酊 | T1 | `capsicum_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N143 | 辣椒油树脂 | — | `paprika_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已有（核对引用） |
| N144 | 愈疮木油 | T1 | `guaiac_wood_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N145 | 缬草油 | T1 | `valerian_root_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N146 | 墨红花净油 | T2（N147 为 T3） | `crimson_glory_rose_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N147 | 墨红花浸膏 | T2（437 为 T3） | `crimson_glory_rose_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N149 | 橙叶油 | T1 | `petitgrain_bigarade_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N150 | 亚洲薄荷油 | — | `mint_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已有（核对引用） |
| N151 | 亚洲薄荷素油 | T2 | `dementholized_mint_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N152 | 檀香油 | T1 | `sandalwood_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N153 | 薰衣草油 | T1 | `lavender_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N154 | 头状百里香油（西班牙牛至油） | T1 | `spanish_origanum_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N155 | 可乐果提取物 | T1 | `kola_nut_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N156 | 加州胡椒油 | T1 | `schinus_molle_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N157 | 卡黎皮油 | T1 | `cascarilla_bark_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N158 | 百里香油 | T1 | `thyme_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N159 | 奶油发酵起子蒸馏物（黄油蒸馏物） | T1 | `starter_distillate` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N160 | 卡南伽油 | T1 | `cananga_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N161 | 月桂叶提取物/油树脂 | T2 | `laurel_leaf_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N162 | 生姜提取物(生姜浸膏) | T2 | `ginger_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N163 | 白栎木屑提取物 | T1 | `oak_chips_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N164 | 龙蒿油 | T1 | `estragon_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N165 | 白樟油 | T2 | `white_camphor_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N166 | 肉豆蔻衣油 | T1 | `mace_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N167 | 众香叶油 | T1 | `pimento_leaf_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N168 | 西班牙鼠尾草油 | T1 | `spanish_sage_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N169 | 红橘油 | T1 | `tangerine_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N170 | 杂薰衣草油 | T1 | `lavandin_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N171 | 杏仁油 | T1 | `apricot_kernel_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N172 | 苏合香油 | T1 | `styrax_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N173 | 苏合香提取物 | T1 | `styrax_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N174 | 长角豆油 | T2（437 为 T3） | `locust_bean_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N175 | 角豆提取物 | T1 | `carob_bean_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N176 | 皂树皮提取物 | — | `quillaja_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已有（核对引用） |
| N177 | 乳香油 | T1 | `olibanum_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N178 | 没药油 | T1 | `myrrh_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N179 | 良姜根提取物 | T1 | `galangal_root_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N180 | 苏格兰松油 | T1 | `scotch_pine_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N181 | 小茴香油（普通小茴香油） | T1 | `fennel_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N182 | 苦杏仁油 | T1 | `bitter_almond_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N183 | 阿魏油 | T1 | `asafoetida_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N184 | 金合欢净油 | T2（N066 为 T3） | `cassie_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N185 | 欧芹叶油 | T1 | `parsley_leaf_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N186 | 松针油 | T1 | `pine_needle_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N187 | 波罗尼花净油 | T2（437 为 T3） | `boronia_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N188 | 玫瑰木油 | T1 | `bois_de_rose_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N189 | 玫瑰草油 | T1 | `palmarosa_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N190 | 香茅油 | T1 | `citronella_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N191 | 迷迭香油 | T1 | `rosemary_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N192 | 香脂冷杉油 | T1 | `balsam_fir_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N193 | 香脂冷杉油树脂 | T1 | `balsam_fir_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N194 | 胡萝卜籽油 | T1 | `carrot_seed_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N195 | 春黄菊花油(罗马) | T1 | `roman_chamomile_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N196 | 春黄菊花净油(提取物)(罗马) | T2（437 为 T3） | `roman_chamomile_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N197 | 药鼠李提取物 | T1 | `cascara_bitterless_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N198 | 荜澄茄油 | T1 | `cubeb_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N199 | 胡薄荷油（唇萼薄荷油） | T1 | `pennyroyal_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N200 | 欧当归油 | T1 | `lovage_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N201 | 夏至草提取物 | T1 | `horehound_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N202 | 莫哈弗丝兰提取物 | T1 | `yucca_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N203 | 海草(藻)提取物 | T1 | `kelp_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N204 | 海索草油 | T1 | `hyssop_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N205 | 莳萝草油（莳萝油） | T1 | `dill_herb_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N206 | 秘鲁香脂 | T1 | `peru_balsam` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N207 | 格蓬油 | T1 | `galbanum_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N208 | 脂檀油 | T1 | `amyris_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N209 | 银白金合欢净油（含羞草净油） | T2（437 为 T3） | `mimosa_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N210 | 接骨木花净油 | T2（437 为 T3） | `elderflower_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N211 | 甘牛至油 | T1 | `sweet_marjoram_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N212 | 黄龙胆根提取物 | T1 | `gentian_root_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N213 | 黄葵籽油 | T1 | `ambrette_seed_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N214 | 野黑樱桃树皮提取物 | T1 | `wild_cherry_bark_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N215 | 黑胡椒油 | T1 | `black_pepper_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N216 | 葛缕籽油 | T1 | `caraway_seed_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N217 | 榄香香树脂 | T1 | `elemi_resinoid` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N218 | 蜡菊提取物 | T1 | `immortelle_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N219 | 香蜂花油 | T1 | `balm_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N220 | d-樟脑 | T2 | `d_camphor` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N221 | 橙花净油 | T2（437 为 T3） | `orange_flower_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N222 | 柚苷（柚皮甙提取物） | T2 | `naringin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N223 | 穗薰衣草油 | T1 | `spike_lavender_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N224 | 鹰爪豆净油 | T2（437 为 T3） | `genet_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N225 | 玳玳果皮油 | T1 | `daidai_peel_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N226 | 甜橙油(橙皮压榨法) | T1 | `cold_pressed_orange_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N227 | 小米辣椒油树脂 | T2 | `bush_red_pepper_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N228 | 丁香茎油 | T1 | `clove_stem_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N229 | 大茴香油（茴芹油） | T1 | `anise_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N230 | L-天冬酰胺 | T2 | `l_asparagine` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N231 | 巴拉圭茶净油/提取物 | T2 | `mate_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N232 | 白山核桃树皮提取物 | T1 | `hickory_bark_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N233 | 瓜拉纳提取物 | T1 | `guarana_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N235 | 白百里香油 | T2 | `white_thyme_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N236 | 白胡椒油 | T1 | `white_pepper_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N237 | 白胡椒油树脂 | T2 | `white_pepper_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N238 | 白康酿克油 | T2 | `white_cognac_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N239 | 白脱酯 | T2 | `butter_esters` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N240 | 白脱酸 | T1 | `butter_acids` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N241 | 众香果油 | T1 | `pimenta_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N242 | 安息香树脂 | T1 | `benzoin_resinoid` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N243 | 当归籽油 | T1 | `angelica_seed_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N244 | 当归根油 | T1 | `angelica_root_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N245 | 肉豆蔻衣油树脂/提取物 | T2 | `mace_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N246 | 西印度月桂叶提取物 | T1 | `west_indian_bay_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N247 | 西印度月桂叶油 | T1 | `west_indian_bay_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N248 | L-阿拉伯糖 | T2 | `l_arabinose` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N249 | 阿拉伯胶 | — | `gum_arabic` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已有（核对引用） |
| N250 | 欧当归提取物 | T1 | `lovage_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N251 | 欧芹油树脂 | T2 | `parsley_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N252 | 油酸 | — | `oleic_acid` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已有（核对引用） |
| N253 | 苦木提取物 | T1 | `quassia_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N254 | 苦橙叶净油 | T2（437 为 T3） | `orange_leaf_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N255 | 苦橙油 | T1 | `bitter_orange_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N256 | 金鸡纳树皮 | T1 | `cinchona_bark_powder` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N257 | 金钮扣油树脂 | T2 | `jambu_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N258 | 奎宁盐酸盐 | T2 | `quinine_hydrochloride` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N260 | 洋葱油树脂 | T2 | `onion_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N261 | 茶树油（互叶白千层油） | T1 | `tea_tree_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N262 | 除萜白柠檬油 | T2 | `terpeneless_lime_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N263 | 除萜甜橙皮油 | T2 | `terpeneless_orange_peel_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N265 | 黄芥末提取物/黄芥末油树脂 | T2 | `yellow_mustard_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N266 | 棕芥末提取物 | T1 | `brown_mustard_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N267 | 焦木酸 | — | `wood_vinegar` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已有（核对引用） |
| N268 | 紫苏油 | T1 | `perilla_leaf_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N269 | 葡萄柚油萜烯 | T2 | `grapefruit_oil_terpenes` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N270 | 黑胡椒油树脂/黑胡椒提取物 | T2 | `black_pepper_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N271 | 榄香油/提取物/香树脂 | T1 | `elemi_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N272 | 蜂蜡净油 | T2 | `beeswax_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N273 | 赖百当净油（岩蔷薇净油） | T2（N063 为 T3） | `labdanum_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N274 | 鼠尾草油（药鼠尾草油） | T1 | `sage_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N275 | 蜡菊净油 | T2（`helichrysum_concrete` 为 T4） | `helichrysum_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N276 | 糖蜜提取物 | T1 | `molasses_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N277 | 檀香醇(包括α-檀香醇、β-檀香醇) | T2 | `santalol` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N278 | 山达草流浸膏 | T1 | `yerba_santa_fluid_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N279 | 苜蓿提取物 | T1 | `alfalfa_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N281 | 众香子油树脂/提取物 | T2 | `allspice_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N282 | 黄葵籽净油 | T2（`ambrette_seed_concrete` 为 T4） | `ambrette_seed_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N283 | 秘鲁香膏油 | T2 | `peru_balsam_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N284 | 罗勒提取物 | T1 | `basil_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N285 | 芹菜籽固体提取物 | T2 | `celery_seed_extract_solid` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N286 | 芹菜籽(含 CO2)提取物 | T4 | `celery_seed_co2_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N287 | 母菊(匈牙利春黄菊)花油 | T1 | `hungarian_chamomile_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N288 | 黄色金鸡纳树皮提取物 | T1 | `cinchona_bark_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N289 | 丁香花蕾油树脂 | T2 | `clove_bud_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N290 | 红三叶草提取物(固体) | T2 | `red_clover_extract_solid` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N291 | 蒲公英流浸膏 | T1 | `dandelion_fluid_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N292 | 蒲公英根固体提取物 | T2 | `dandelion_root_extract_solid` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N293 | 加拿大飞蓬草油 | T1 | `fleabane_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N294 | 穗花槭提取物(固体) | T2 | `mountain_maple_extract_solid` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N295 | 芸香油 | T1 | `rue_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N296 | 鼠尾草油树脂/提取物 | T2 | `sage_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N297 | 菝葜提取物 | T1 | `sarsaparilla_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N298 | 水蒸气蒸馏松节油 | T1 | `turpentine` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N299 | 缬草根提取物 | T1 | `valerian_root_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N300 | 香荚兰油树脂 | T2 | `vanilla_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N301 | 紫罗兰叶净油 | T2（N136 为 T3） | `violet_leaf_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N302 | 洋艾油 | T1 | `wormwood_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N304 | 橘柚油 | T1 | `tangelo_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N305 | 晚香玉净油 | T2（N135 为 T3） | `tuberose_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N306 | 美国栗树叶提取物 | T1 | `chestnut_leaf_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N307 | 古巴香脂油 | T1 | `copaiba_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N308 | 达迷草叶 | T1 | `damiana_leaf_powder` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N309 | 母菊(匈牙利春黄菊)花净油 | T2（`hungarian_chamomile_concrete` 为 T4） | `hungarian_chamomile_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N310 | 接骨木花提取物 | T1 | `elderflower_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N311 | 防风根油 | T1 | `opoponax_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N312 | 藏红花提取物 | T1 | `saffron_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N313 | 香叶提取物 | T1 | `geranium_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N314 | 葫芦巴油树脂 | T2 | `fenugreek_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N315 | 柠檬提取物 | T1 | `lemon_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N316 | 德国鸢尾树脂 | T2 | `orris_resinoid` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N317 | 罗望子提取物(浸膏) | T1 | `tamarind_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N318 | 辣根油 | T1 | `horseradish_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N319 | 葫芦巴籽浸膏 | T2 | `fenugreek_seed_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N320 | 芹菜叶油 | T1 | `celery_leaf_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N321 | 柏木油萜烯 | T2 | `cedarwood_oil_terpenes` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N322 | 肉豆蔻油树脂 | T2 | `nutmeg_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N324 | 芫荽油/油树脂 | T2 | `coriander_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N326 | 韭葱油 | T1 | `leek_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N327 | 甜橙皮提取物 | T1 | `sweet_orange_peel_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N329 | 香橙皮油 | T1 | `yuzu_peel_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N330 | 海藻净油 | T2（`algues_concrete` 为 T4） | `algues_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N331 | 墨西哥鼠尾草油树脂（棘枝油树脂） | T2 | `mexican_oregano_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N332 | 甘草酸铵 | — | `ammonium_glycyrrhizinate` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已有（核对引用） |
| N333 | 冬香草油 | T1 | `winter_savory_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N334 | 苏合香 | T1 | `styrax` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N335 | 阿魏液态提取物(流浸膏) | T1 | `asafoetida_fluid_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N336 | 桃树叶净油 | T2（`peach_leaf_concrete` 为 T4） | `peach_leaf_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N337 | 白藓牛至 | T1 | `dittany_of_crete_powder` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N338 | 酒花油 | T1 | `hop_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N339 | 赖百当油 | T1 | `labdanum_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N340 | 薰衣草净油 | T2（`lavender_concrete` 为 T4） | `lavender_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N341 | 没药树脂提取物 | T2 | `opoponax_resinoid` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N342 | 花椒提取物 | T2 | `prickly_ash_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N344 | 儿茶粉 | T2 | `catechu_powder` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N345 | 苦艾 | T1 | `wormwood_powder` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N346 | 苦橙花油 | — | 见 N080 | [additives/18 §2](../additives/18-batch-5-flavors.md) | 见 N080 |
| N347 | 达瓦树胶 | — | `ghatti_gum` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已有（核对引用） |
| N348 | 苦艾提取物 | T1 | `wormwood_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N349 | 刺柏提取物 | T1 | `juniper_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N350 | 甘草提取物(粉) | T2 | `licorice_extract_powder` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N351 | 甜菜碱(天然提取) | T3 | `betaine` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N352 | 松焦油 | T2 | `pine_tar_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N353 | 橡苔净油 | T2（`oakmoss_concrete` 为 T4） | `oakmoss_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N354 | 苏格兰留兰香油 | T1 | `scotch_spearmint_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N355 | 海索草提取物（神香草提取物） | T1 | `hyssop_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N356 | 安古树皮提取物 | T1 | `angostura_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N357 | 德国春黄菊花(母菊花)提取物 | T1 | `german_chamomile_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N359 | L-苏氨酸 | T4 | `l_threonine` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N360 | L-丝氨酸 | T4 | `l_serine` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N361 | 灵猫净油 | T2（`civet_concrete` 为 T4） | `civet_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N362 | 胭脂树提取物 | — | `bixin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已有（核对引用） |
| N363 | 卡黎皮提取物 | T1 | `cascarilla_bark_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N364 | 肉桂皮油/油树脂 | T2 | `cinnamon_bark_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N365 | 刺梧桐树胶 | — | `karaya_gum` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已有（核对引用） |
| N366 | 橘叶油 | T1 | `petitgrain_mandarin_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N367 | 欧洲山松针叶油 | T1 | `dwarf_pine_needle_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N368 | 玫瑰果籽提取物 | T1 | `rose_hip_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N369 | 夏香草油 | T1 | `summer_savory_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N370 | 加拿大细辛油 | T1 | `canadian_snakeroot_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N372 | 黄蓍胶 | T1 | `tragacanth_gum` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N373 | 甘牛至油树脂/提取物 | T2 | `marjoram_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N374 | 摩洛哥豆蔻提取物 | T2 | `grains_of_paradise_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N375 | 橙皮素 | T4 | `hesperetin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N377 | 芝麻(CO2)提取物 | T4 | `sesame_co2_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N378 | 芝麻蒸馏物 | T1 | `sesame_distillate` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N379 | 干制鲣鱼(CO2)提取物 | T4 | `katsuobushi_co2_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N380 | 朗姆酒净油 | T2（`rum_concrete` 为 T4） | `rum_absolute` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N381 | 豆豉油树脂 | T2 | `douchi_oleoresin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N382 | 药蜀葵 | T1 | `althea_root_powder` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N383 | 香蜂草 | T1 | `lemon_balm_powder` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N384 | 白千层油 | T1 | `cajeput_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N388 | 毕澄茄 | T1 | `cubeb_powder` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N389 | 芦荟提取物 | T1 | `aloe_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N390 | 龙涎香酊 | T1 | `ambergris_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N391 | 黄葵酊 | T1 | `ambrette_tincture` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N392 | 燕根(萝藦科植物)提取物 | T1 | `swallowroot_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N393 | 红枣浸膏 | T2 | `jujube_concrete` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N394 | 高倍天然苹果香料 | T2 | `folded_apple_essence` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N395 | β-愈疮木烯 | T2 / T3（B 为 T3） | `beta_guaiene` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N396 | 褐藻胶 | — | `sodium_alginate` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已有（核对引用） |
| N397 | 香厚壳桂皮油 | T1 | `massoia_bark_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N398 | (—)-高圣草酚钠盐 | T2（`homoeriodictyol` 为 T4） | `homoeriodictyol_sodium` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N399 | 酶处理异槲皮苷 | T4 | `enzymatically_modified_isoquercitrin` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N400 | 葡萄籽提取物 | T2 | `grape_seed_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N401 | 留兰香提取物 | T1 | `spearmint_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| N402 | 杂醇油(精制过) | T2 | `fusel_oil` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N403 | 葡萄糖基甜菊糖苷 | T4 | `glucosyl_steviol_glycosides` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成 |
| N404 | 非洲竹芋提取物 | T1 | `katemfe_fruit_extract` | [additives/18 §2](../additives/18-batch-5-flavors.md) | 已完成（原料暂无来源） |
| S0003 | 异丙醇 | — | 见 434 | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 见 434 |
| S0004 | 正丁醇 | — | 见 431 | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 见 431 |
| S0005 | 异丁醇 | T2 / T3 | `isobutanol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0006 | 正戊醇 | T3 | `n_pentanol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0007 | 2-戊醇 | T3 | `pentan_2_ol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0009 | 1-戊烯-3-醇 | T3 | `pent_1_en_3_ol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0010 | 正己醇 | T3 | `n_hexanol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0011 | 2-己烯-1-醇 | T3 | `trans_2_hexenol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0012 | 4-己烯-1-醇 | T3 | `hex_4_en_1_ol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0013 | 正庚醇 | T3 | `n_heptanol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0014 | 正辛醇 | T3 | `n_octanol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0015 | 2-辛醇 | T2 | `octan_2_ol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0016 | 1-辛烯-3-醇 | T3 | `oct_1_en_3_ol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0017 | 顺式-5-辛烯-1-醇 | T3 | `cis_5_octenol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0018 | 正壬醇 | T3 | `n_nonanol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0019 | 顺式-6-壬烯-1-醇 | T3 | `cis_6_nonenol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0020 | 反式-2-壬烯-1-醇 | T3 | `trans_2_nonenol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0021 | 2,6-壬二烯-1-醇 | T3 | `trans_2_cis_6_nonadienol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0022 | 正癸醇 | T3 | `n_decanol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0023 | 十一醇 | T3 | `n_undecanol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0024 | 月桂醇（十二醇） | — | `lauryl_alcohol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0025 | 1-十六醇 | T3 | `cetyl_alcohol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0026 | 小茴香醇 | T3 | `fenchol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0027 | 叶醇 | T3 | `leaf_alcohol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0028 | 龙脑 | T2 / T3 | `borneol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0029 | 芳樟醇 | T2 / T3 | `linalool` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0030 | 氧化芳樟醇 | T3 | `linalool_oxide` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0031 | 异胡薄荷醇 | T3 | `isopulegol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0032 | 苏合香醇 | T3 | `styralyl_alcohol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0033 | 苯甲醇 | T3 | `benzyl_alcohol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0034 | 苯乙醇 | T3 / T4 | `phenethyl_alcohol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0035 | 苯丙醇 | T3 | `phenylpropyl_alcohol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0036 | 玫瑰醇 | T2 | `rhodinol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0037 | α-松油醇 | T3 | `alpha_terpineol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0038 | 金合欢醇 | T3 | `farnesol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0039 | 香叶醇 | T2 / T3 | `geraniol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0040 | dl-香茅醇 | T3 | `dl_citronellol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0041 | 茴香醇 | T3 | `anisyl_alcohol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0042 | 肉桂醇 | T3 | `cinnamyl_alcohol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0043 | α-紫罗兰醇（甲位紫罗兰醇） | T3 | `alpha_ionol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0044 | β-紫罗兰醇（乙位紫罗兰醇） | T3 | `beta_ionol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0045 | 二氢-β-紫罗兰醇 | T3 | `dihydro_beta_ionol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0046 | 橙花醇 | T3 | `nerol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0047 | 橙花叔醇 | T3 | `nerolidol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0048 | 二甲基苄基原醇 | T3 | `dimethylbenzylcarbinol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0050 | 3-己醇 | T3 | `hexan_3_ol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0051 | 1-己烯-3-醇 | T3 | `hex_1_en_3_ol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0052 | 2-乙基己醇 | T3 | `ethylhexanol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0053 | 2-庚醇 | T3 | `heptan_2_ol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0054 | 3-辛醇 | T3 | `octan_3_ol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0055 | 顺式-3-辛烯-1-醇 | T3 | `cis_3_octenol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0056 | 2-十一醇 | T3 | `undecan_2_ol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0057 | 对,α-二甲基苄醇 | T3 | `p_alpha_dimethylbenzyl_alcohol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0058 | 对-异丙基苄醇 | T3 | `p_isopropylbenzyl_alcohol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0059 | 对,α,α-三甲基苄醇 | T3 | `p_cymen_8_ol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0060 | β-石竹烯醇 | T3 | `caryophyllene_alcohol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0061 | 龙蒿脑 | T2 | `estragole` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0062 | 四氢香叶醇 | T3 | `tetrahydrogeraniol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0063 | 二氢香芹醇 | T3 | `dihydrocarveol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0064 | 1-对-䓝烯-4-醇 | T2 | `terpinen_4_ol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0065 | 紫苏醇 | T3 | `perillyl_alcohol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0067 | 3-(l-薄荷烷氧基)-2-甲基-1,2-丙二醇 | T3 | `menthoxy_methylpropanediol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0068 | 3,5,5-三甲基环己醇 | T3 | `trimethylcyclohexanol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0069 | 顺式-2-壬烯-1-醇 | T3 | `cis_2_nonenol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0070 | 反式,反式-2,4-癸二烯醇 | T3 | `trans_trans_2_4_decadienol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0071 | 反式-2-辛烯-4-醇 | T3 | `trans_2_octen_4_ol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0072 | 对-䓝-3-烯-1-醇 | T3 | `terpinen_1_ol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0073 | 对-䓝-1,8(10)二烯-9-醇 | T3 | `menthadienol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0074 | 柏木烯醇 | T2 | `cedrenol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0075 | 脱氢芳樟醇 | T4 | `hotrienol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0077 | d-核糖 | T4 | `d_ribose` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0078 | l-鼠李糖 | T2 | `l_rhamnose` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0079 | 二苯醚 | — | `diphenyl_ether` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0080 | 对-甲酚甲醚 | T3 | `p_cresyl_methyl_ether` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0081 | 异丁香酚甲醚 | T3 | `isoeugenyl_methyl_ether` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0082 | 甲基苯乙醚 | T3 | `methyl_phenethyl_ether` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0083 | 朗姆醚 | T3 | `rum_ether` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0084 | 仲丁基乙醚 | T3 | `sec_butyl_ethyl_ether` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0085 | 乙基苄基醚 | T3 | `ethyl_benzyl_ether` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0086 | 大茴香醚 | T3 | `anisole` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0087 | 邻-甲基大茴香醚 | T3 | `o_methylanisole` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0088 | 橙花醚 | T4 | `nerol_oxide` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0089 | 2,4-二甲基大茴香醚 | T3 | `dimethylanisole_2_4` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0090 | 香兰基乙醚 | T3 | `vanillyl_ethyl_ether` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0091 | 丁香酚 | T2 | `eugenol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0092 | 异丁香酚 | T3 | `isoeugenol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0093 | 甲基丁香酚 | T3 | `methyl_eugenol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0095 | 邻-甲酚 | T2 / T3 | `o_cresol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0100 | 2-甲氧基-4-甲基苯酚 | T2 / T3 | `creosol` | [additives/19 §1.1](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0101 | 对-乙基苯酚 | T3（`sodium_ethylbenzenesulfonate` 为 T4） | `p_ethylphenol` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0102 | 2-甲氧基-4-乙烯基苯酚 | T2 | `vinylguaiacol` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0103 | 对-二甲氧基苯 | T3 | `p_dimethoxybenzene` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0105 | 4-乙基愈疮木酚 | T3 | `ethylguaiacol` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0106 | 苯甲醛丙二醇缩醛 | T2 | `benzaldehyde_propylene_glycol_acetal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0107 | 2-异丙基苯酚 | T3 | `o_isopropylphenol` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0108 | 2,6-二甲基苯酚 | T3 | `xylenol_2_6` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0109 | 2,6-二甲氧基苯酚 | T3（`pyrogallol` 为 T4） | `syringol` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0110 | 间苯二酚 | — | `resorcinol` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0111 | 香芹酚 | T2 / T3 | `carvacrol` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0112 | 2-甲氧基-4-丙基苯酚 | T3 | `propylguaiacol` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0113 | 2,5-二甲基苯酚 | T3 | `xylenol_2_5` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0114 | 对-乙烯基苯酚 | T3（S0101 为 T4） | `p_vinylphenol` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0116 | 乙醛二乙缩醛 | T2 | `acetaldehyde_diethyl_acetal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0117 | 丙醛 | T3 | `propionaldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0118 | 3-(2-呋喃基)丙烯醛 | T3 | `furylacrolein` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0119 | 丁醛 | T3 | `butyraldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0120 | 2-甲基丁醛 | T3 | `methylbutyraldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0121 | 2-甲基-2-丁烯醛 | T3 | `tiglaldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0122 | 2-苯基-2-丁烯醛 | T3 | `phenylbutenal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0123 | 戊醛 | T3 | `valeraldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0124 | 异戊醛 | T3 | `isovaleraldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0125 | 2-甲基戊醛 | T3 | `methylvaleraldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0126 | 2-戊烯醛 | T3 | `trans_2_pentenal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0127 | 2-甲基-2-戊烯醛 | T3 | `methylpentenal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0128 | 4-甲基-2-苯基-2-戊烯醛 | T3 | `methylphenylpentenal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0129 | 2,4-戊二烯醛 | T3 | `pentadienal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0130 | 己醛 | T3 | `hexanal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0131 | 2-己烯醛（叶醛） | T3 | `trans_2_hexenal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0132 | 顺式-3-己烯醛 | T3 | `cis_3_hexenal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0133 | 5-甲基-2-苯基-2-己烯醛 | T3 | `methylphenylhexenal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0134 | 2-异丙基-5-甲基-2-己烯醛 | T3 | `isopropylmethylhexenal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0135 | 反式,反式-2,4-己二烯醛 | T3 | `trans_trans_2_4_hexadienal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0136 | 庚醛 | T3 | `heptanal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0137 | 4-庚烯醛 | T3 | `cis_4_heptenal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0138 | 反式-2-庚烯醛 | T3 | `trans_2_heptenal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0139 | 2,6-二甲基-5-庚烯醛 | T3 | `dimethylheptenal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0140 | 2,4-庚二烯醛 | T3 | `trans_trans_2_4_heptadienal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0141 | 辛醛 | T3 | `octanal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0142 | 2-辛烯醛 | T3 | `trans_2_octenal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0143 | 反式,反式-2,4-辛二烯醛 | T3 | `trans_trans_2_4_octadienal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0144 | 反式,反式-2,6-辛二烯醛 | T3 | `trans_trans_2_6_octadienal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0145 | 壬醛 | T3 | `nonanal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0146 | 甲基壬乙醛 | T3 | `methylundecanal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0147 | 2-壬烯醛 | T3 | `trans_2_nonenal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0148 | 顺式-6-壬烯醛 | T3 | `cis_6_nonenal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0149 | 反式-2-反式-4-壬二烯醛 | T3 | `trans_trans_2_4_nonadienal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0150 | 反式-2-顺式-6-壬二烯醛 | T3 | `trans_2_cis_6_nonadienal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0151 | 甲酸桃金娘烯酯 | T2（S0840 为 T3） | `myrtenyl_formate` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0152 | 正癸醛（癸醛） | T3 | `decanal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0153 | 2-癸烯醛 | T3 | `trans_2_decenal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0154 | 2,4-癸二烯醛 | T3 | `trans_trans_2_4_decadienal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0155 | 十一醛 | T3 | `undecanal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0156 | 2-十一烯醛 | T3 | `trans_2_undecenal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0157 | 2,4-十一碳二烯醛 | T3 | `trans_trans_2_4_undecadienal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0158 | 月桂醛 | T3 | `dodecanal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0159 | 2-十二碳烯醛 | T3 | `trans_2_dodecenal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0160 | 反式-2-顺式-6-十二碳二烯醛 | T3 | `trans_2_cis_6_dodecadienal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0161 | 十四醛 | T3 | `tetradecanal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0162 | 桃醛 | T3 | `gamma_undecalactone` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0163 | 大茴香醛 | T3 | `p_anisaldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0164 | 水杨醛 | T3 | `salicylaldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0166 | 甲基苯甲醛(邻、对、间位混合物) | T3 | `tolualdehydes` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0167 | 3,4-二甲氧基苯甲醛 | T3 | `veratraldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0168 | 苯乙醛 | T3 | `phenylacetaldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0169 | 苯乙醛二甲缩醛 | T2（S0168 为 T3） | `phenylacetaldehyde_dimethyl_acetal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0170 | 苯丙醛 | T3 | `hydrocinnamaldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0171 | 枯茗醛 | T2 / T3 | `cuminaldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0173 | 香茅醛 | T2 / T3 | `citronellal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0175 | 洋茉莉醛（胡椒醛） | T3 | `piperonal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0177 | 乙二醇缩肉桂醛 | T2 | `cinnamaldehyde_ethylene_glycol_acetal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0178 | 紫苏醛 | T2 / T3 | `perillaldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0179 | 对-䓝-1-烯-9-醛 | T3 | `p_menth_1_en_9_al` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0181 | 5-甲基糠醛 | T2 | `methylfurfural` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0182 | 1,1-二甲氧基乙烷 | T2 | `acetaldehyde_dimethyl_acetal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0183 | 2,6,6-三甲基环己-1,3-二烯基甲醛 | T3 | `safranal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0184 | 异丁醛 | T3 | `isobutyraldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0185 | 顺式-4-己烯醛 | T3 | `cis_4_hexenal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0186 | 顺式-5-辛烯醛 | T3 | `cis_5_octenal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0187 | 4-癸烯醛 | T3 | `cis_4_decenal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0188 | 反式,反式-2,4-十二碳二烯醛 | T3 | `trans_trans_2_4_dodecadienal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0189 | 2-十三烯醛 | T3 | `trans_2_tridecenal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0190 | 4-乙基苯甲醛 | T3 | `p_ethylbenzaldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0191 | 2-羟基-4-甲基苯甲醛 | T3 | `methylsalicylaldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0192 | 邻-甲氧基肉桂醛 | T3 | `o_methoxycinnamaldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0193 | 龙脑烯醛 | T3 | `campholenic_aldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0194 | α-己基肉桂醛 | T3 | `hexylcinnamaldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0195 | 香兰素1,2-丙二醇缩醛 | T2 | `vanillin_propylene_glycol_acetal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0196 | 乙醛乙醇顺式-3-己烯醇缩醛 | T2（S0027 为 T3） | `acetaldehyde_ethyl_hexenyl_acetal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0197 | 反式,反式-2,6-壬二烯醛 | T3 | `trans_trans_2_6_nonadienal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0198 | 2,4,7-癸三烯醛 | T3 | `decatrienal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0199 | β-甜橙醛 | T2 | `beta_sinensal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0200 | 4-羟基苯甲醛 | T3 | `p_hydroxybenzaldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0201 | 邻-甲氧基苯甲醛 | T3 | `o_anisaldehyde` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0202 | 12-甲基十三醛 | T3 | `methyltridecanal` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0203 | 甲乙酮 | T3 | `methyl_ethyl_ketone` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0204 | 3-羟基-2-丁酮（乙偶姻） | T4 / T3 | `acetoin` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0205 | 4-(对-甲氧基苯基)-2-丁酮 | T3 | `anisylacetone` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0206 | 4-苯基-3-丁烯-2-酮 | T3 | `benzalacetone` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0208 | 2-戊酮 | T3 | `methyl_propyl_ketone` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0209 | 1-戊烯-3-酮 | T3 | `ethyl_vinyl_ketone` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0210 | 2,3-戊二酮 | T3 | `acetylpropionyl` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0211 | 3-乙基-2-羟基-2-环戊烯-1-酮 | T3 | `ethyl_cyclopentenolone` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0213 | 4-己烯-3-酮 | T3 | `ethyl_propenyl_ketone` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0214 | 5-甲基-3-己烯-2-酮 | T3 | `methylhexenone` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0215 | 3,4-己二酮 | T3 | `dipropionyl` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0216 | 2-庚酮 | T3 | `methyl_amyl_ketone` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0217 | 3-庚烯-2-酮 | T3 | `methyl_pentenyl_ketone` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0218 | 6-甲基-5-庚烯-2-酮 | T3 | `methylheptenone` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0219 | 1-辛烯-3-酮 | T3 | `amyl_vinyl_ketone` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0220 | 2-壬酮 | T3 | `methyl_heptyl_ketone` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0221 | 2-十一酮 | T2 / T3 | `methyl_nonyl_ketone` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0222 | 2-十三酮 | T3 | `methyl_undecyl_ketone` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0223 | 圆柚酮 | T2 / T3 | `nootkatone` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0224 | l-香芹酮 | T2 / T3 | `l_carvone` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0225 | 苯乙酮 | T3 | `acetophenone` | [additives/19 §1.2](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0226 | 4-甲基苯乙酮 对-甲基苯乙酮 | T3 | `p_methylacetophenone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0227 | 对-甲氧基苯乙酮 | T3 | `p_methoxyacetophenone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0228 | 顺式茉莉酮 | T3 | `cis_jasmone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0229 | 覆盆子酮（悬钩子酮） | T3 | `raspberry_ketone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0230 | α-突厥酮 | T3 | `alpha_damascone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0231 | 突厥烯酮 | T3 | `damascenone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0232 | 苯甲醛甘油缩醛 | T2 | `benzaldehyde_glyceryl_acetal` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0233 | α-鸢尾酮 | T2 / T3 | `alpha_irone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0234 | α-紫罗兰酮 | T3 | `alpha_ionone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0235 | β-紫罗兰酮 | T3 | `beta_ionone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0236 | dl-樟脑 | T3 | `dl_camphor` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0238 | d,l-异薄荷酮 | T3 | `isomenthone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0239 | 4-(2-呋喃基)-3-丁烯-2-酮 | T3 | `furfurylidene_acetone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0240 | 2-乙基-4-羟基-5-甲基-3(2H)-呋喃酮 | T2 | `homofuraneol` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0241 | 4,5-二甲基-3-羟基-2,5-二氢呋喃-2-酮 | T3 | `sotolon` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0242 | 2-乙基-3-甲基-4-羟基二氢-2,5-呋喃-5-酮 | T3 | `abhexon` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0243 | 4,5-二氢-3(2H)-噻吩酮(四氢噻吩-3-酮) | T3 | `tetrahydrothiophenone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0244 | 2-乙基呋喃 | T3 | `ethylfuran` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0245 | 2-乙酰基呋喃 | T3 | `acetylfuran` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0246 | 2-乙酰基-5-甲基呋喃 | T3 | `acetylmethylfuran` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0248 | 1-苯基-1,2-丙二酮 | T3 | `phenylpropanedione` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0249 | 3,4-二甲基-1,2-环戊二酮 | T3 | `dimethylcyclopentanedione_34` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0250 | 3,5-二甲基-1,2-环戊二酮 | T3 | `dimethylcyclopentanedione_35` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0251 | 2,3-己二酮 | T3 | `acetyl_butyryl` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0252 | 1-甲基-2,3-环己二酮 | T3 | `methylcyclohexanedione` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0253 | 2,2,6-三甲基环己酮 | T3 | `trimethylcyclohexanone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0254 | 2,6,6-三甲基-2-环己烯-1,4-二酮 | T3 | `ketoisophorone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0255 | 3-庚酮 | T3 | `ethyl_butyl_ketone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0256 | 5-甲基-2-庚烯-4-酮 | T3 | `filbertone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0257 | 6-甲基-3,5-庚二烯-2-酮 | T3 | `methylheptadienone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0258 | 2-辛酮 | T3 | `methyl_hexyl_ketone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0259 | 3-辛酮 | T3 | `ethyl_amyl_ketone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0260 | 3-辛烯-2-酮 | T3 | `methyl_hexenyl_ketone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0261 | 6,10-二甲基-5,9-十一碳二烯-2-酮 | T3 | `geranylacetone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0262 | 2-十五酮 | T3 | `pentadecanone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0263 | 3-甲基环十五酮 | T3 | `muscone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0264 | 环十七-9-烯-1-酮 | T3 | `civetone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0265 | 二苯甲酮 | T3 | `benzophenone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0266 | 2-羟基苯乙酮 | T3 | `o_hydroxyacetophenone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0267 | 异弗尔酮 | T3 | `isophorone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0268 | 二氢茉莉酮 | T3 | `dihydrojasmone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0269 | 新甲基橙皮苷二氢查耳酮 | T3 | `neohesperidin_dhc` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0270 | 姜油酮 | T3 | `zingerone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0271 | β-突厥酮 | T3 | `beta_damascone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0272 | 3-甲硫基丁醛 | T3 | `methylthiobutanal` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0273 | α-戊基肉桂醛 | T3 | `amylcinnamaldehyde` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0274 | d-葑酮 | T2 | `d_fenchone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0275 | 2-甲基四氢呋喃-3-酮 | T3 | `methyltetrahydrofuranone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0277 | 2,5-二甲基-4-甲氧基-3(2H)-呋喃酮 | T3 | `mesifurane` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0278 | 2-戊基呋喃 | T3 | `pentylfuran` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0279 | 4,5,6,7-四氢-3,6-二甲基苯并呋喃（薄荷呋喃） | T2 | `menthofuran` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0280 | 1,5,5,9-四甲基-13-氧杂三环[8.3.0.0(4,9)]十三烷 | T3（`ambradiol` 为 T4） | `ambroxide` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0281 | 顺式-二氢香芹酮 | T3 | `cis_dihydrocarvone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0282 | 3-巯基-2-丁酮 | T3 | `mercaptobutanone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0283 | 胡椒基丙酮 | T3 | `piperonylacetone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0284 | 二氢-β-紫罗兰酮 | T3 | `dihydro_beta_ionone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0285 | 4-甲基-2,3-戊二酮 | T3 | `acetyl_isobutyryl` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0286 | 反式-7-甲基-3-辛烯-2-酮 | T3 | `methyloctenone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0287 | 3-乙酰硫基-2-甲基呋喃 | T3（S0471 为 T4） | `acetylthio_methylfuran` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0288 | 4-乙酰氧基-2,5-二甲基-3(2H)-呋喃酮 | T3 | `furaneol_acetate` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0289 | 3-乙基-2-羟基-4-甲基-2-环戊烯-1-酮 | T3 | `ethyl_methyl_cyclopentenolone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0290 | 环己酮 | T3 | `cyclohexanone` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0291 | 2,3-庚二酮 | T3 | `acetyl_valeryl` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0292 | 2,3-辛二酮 | T3 | `acetyl_hexanoyl` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0295 | 丙酮酸 | T3 | `pyruvic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0297 | 异丁酸 | — | `isobutyric_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0298 | 2-甲基丁酸 | T3 | `methylbutyric_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0299 | 2-乙基丁酸 | T3 | `ethylbutyric_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0300 | 戊酸 | T3 | `valeric_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0301 | 2-甲基戊酸 | T3 | `alpha_methylvaleric_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0302 | 2-甲基-2-戊烯酸（草莓酸） | T3 | `methylpentenoic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0303 | 异戊酸 | T3 | `isovaleric_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0304 | 己酸 | — | `hexanoic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0305 | 己二酸 | — | `adipic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0306 | 反式-2-己烯酸 | T3 | `trans_2_hexenoic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0307 | 3-己烯酸 | T3 | `hex_3_enoic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0308 | 庚酸 | T3 | `heptanoic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0309 | 辛酸 | — | `caprylic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0310 | 壬酸 | T3 | `nonanoic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0311 | 癸酸 | — | `capric_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0312 | 十二酸（月桂酸） | — | 见 458 | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 见 458 |
| S0313 | 十四酸（肉豆蔻酸） | T2 | `myristic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0314 | 十六酸（棕榈酸） | — | `palmitic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0316 | 苯乙酸 | T3 | `phenylacetic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0318 | 肉桂酸 | T3 | `cinnamic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0320 | 3-甲基戊酸（酐酪酸） | T3 | `beta_methylvaleric_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0321 | β-丙氨酸 | T3 | `beta_alanine` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0322 | L-苯基丙氨酸 | — | `l_phenylalanine` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0323 | L-半胱氨酸 | — | 见 181 | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 见 181 |
| S0324 | 甘氨酸 | — | `glycine` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0325 | L-谷氨酸 | — | `glutamic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0326 | L-亮氨酸 | T4 | `l_leucine` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0327 | DL-蛋氨酸 | T3 | `dl_methionine` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0328 | 乙酰丙酸 | T2 | `levulinic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0329 | 2-氧代丁酸 | T3 | `oxobutyric_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0330 | 2-甲基己酸 | T3 | `methylhexanoic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0331 | 2-甲基庚酸 | T3 | `methylheptanoic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0332 | 4-甲基辛酸 | T3 | `methyloctanoic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0333 | 3,7-二甲基-6-辛烯酸 | T3 | `citronellic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0334 | 9-癸烯酸 | T4 | `decenoic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0335 | 十一酸 | T3 | `undecanoic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0336 | 10-十一碳烯酸 | T3 | `undecylenic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0337 | 3-苯丙酸 | T3 | `hydrocinnamic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0339 | L-脯氨酸 | T4 | `l_proline` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0340 | DL-缬氨酸 | T3 | `dl_valine` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0341 | 2-(4-甲氧基苯氧基)-丙酸钠 | T3 | `lactisole` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0342 | L-和DL-丙氨酸 | — | `l_alanine` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0343 | L-精氨酸 | T4 | `l_arginine` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0344 | L-赖氨酸 | T4 | `l_lysine` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0345 | 3-甲基巴豆酸 | T3 | `methylcrotonic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0346 | 甲酸 | — | `formic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0347 | 4-甲基壬酸 | T3 | `methylnonanoic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0348 | 异己酸 | T3 | `isocaproic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0349 | 2-羟基苯甲酸（水杨酸） | T3 | `salicylic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0350 | 惕各酸 | T3 | `tiglic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0351 | 琥珀酸 | — | `succinic_acid` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0353 | 甲酸乙酯 | T2 | `ethyl_formate` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0354 | 甲酸丁酯 | T2（S0004 为 T3） | `butyl_formate` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0355 | 甲酸戊酯 | T2（S0006 为 T3） | `amyl_formate` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0356 | 甲酸异戊酯 | T2 | `isoamyl_formate` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0357 | 甲酸己酯 | T2（S0010 为 T3） | `hexyl_formate` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0358 | 甲酸苄酯 | T2（S0033 为 T3） | `benzyl_formate` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0359 | 甲酸香叶酯 | T2 | `geranyl_formate` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0360 | 甲酸香茅酯 | T2（S0040 为 T3） | `citronellyl_formate` | [additives/19 §1.3](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0361 | 甲酸苯乙酯 | T2（S0034 为 T3） | `phenethyl_formate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0362 | 甲酸芳樟酯 | T3 | `linalyl_formate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0363 | 乙酸甲酯 | T2 | `methyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0364 | 乙酸乙酯 | — | 见 436 | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 见 436 |
| S0365 | 乙酰乙酸乙酯 | T3 | `ethyl_acetoacetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0366 | 乙酸丙酯 | T2 | `propyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0367 | 乙酸异丙酯 | T2 / T3 | `isopropyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0368 | 乙酸烯丙酯 | T3 | `allyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0369 | 乙酰丙酸乙酯 | T2 | `ethyl_levulinate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0370 | 乙酸丁酯 | T2（S0004 为 T3） | `butyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0371 | 乙酸异丁酯 | T2 | `isobutyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0373 | 乙酸己酯 | T2（S0010 为 T3） | `hexyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0374 | 乙酸2-己烯酯 | T3 | `hex_2_enyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0375 | 乙酸庚酯 | T2（S0013 为 T3） | `heptyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0376 | 乙酸辛酯 | T2（S0014 为 T3） | `octyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0377 | 乙酸3-辛酯 | T2（S0054 为 T3） | `octan_3_yl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0378 | 1-辛烯-3-醇乙酸酯 | T3 | `octen_3_yl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0379 | 乙酸壬酯 | T2（S0018 为 T3） | `nonyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0380 | 2-丁烯酸己酯 | T2（S0010、S1019 为 T3） | `hexyl_2_butenoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0381 | 乙酸癸酯 | T2（S0022 为 T3） | `decyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0382 | 乙酸苄酯 | T2（S0033 为 T3） | `benzyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0383 | 乙酸苯乙酯 | T2（S0034 为 T3） | `phenethyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0384 | 乙酸茴香酯 | T3 | `anisyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0385 | 乙酸龙脑酯 | T3 / T2 | `bornyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0386 | 乙酸薄荷酯 | T3 | `menthyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0387 | 乙酸肉桂酯 | T3 | `cinnamyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0388 | 乙酸香茅酯 | T3 | `citronellyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0389 | 乙酸香叶酯 | T3 | `geranyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0390 | 乙酸对-甲酚酯 | T3 | `p_cresyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0391 | 乙酸苏合香酯 | T3 | `styrallyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0392 | 乙酸橙花酯 | T3 | `neryl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0393 | 乙酸松油酯 | T3 | `terpinyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0394 | 异丁酸肉桂酯 | T2（S0042 为 T3） | `cinnamyl_isobutyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0395 | 顺式-3-己烯-1-醇乙酸酯（乙酸叶醇酯） | T3 | `cis_3_hexenyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0396 | 乙酸糠酯 | T3 | `furfuryl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0397 | 庚酸烯丙酯 | T2（`allyl_alcohol`、S0308 为 T3） | `allyl_heptanoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0398 | 乙酸芳樟酯 | T3 | `linalyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0399 | 乙酸葛缕酯 | T3 | `carvyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0400 | 乙酸二氢葛缕酯 | T3 | `dihydrocarvyl_acetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0401 | 苯乙酸丁酯 | T2（S0004、S0316 为 T3） | `butyl_phenylacetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0402 | 丙酸乙酯 | T2 | `ethyl_propionate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0403 | 丙二酸二乙酯 | T2（`cyanoacetic_acid` 为 T3） | `diethyl_malonate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0404 | 丙酸异丁酯 | T2 | `isobutyl_propionate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0405 | 丙酸异戊酯 | T2 | `isoamyl_propionate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0406 | 丙酸顺式-3-己烯酯和丙酸反式-2-己烯酯 | T2（S0011、S0027 为 T3） | `hexenyl_propionate_mixture` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0407 | 丙酸香叶酯 | T2 | `geranyl_propionate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0408 | 丙酸香茅酯 | T2（S0040 为 T3） | `citronellyl_propionate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0409 | 丙酸苄酯 | T2（S0033 为 T3） | `benzyl_propionate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0410 | 丙酸苯乙酯 | T2（S0034 为 T3） | `phenethyl_propionate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0411 | 丙酸芳樟酯 | T3 | `linalyl_propionate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0412 | 丁酸甲酯 | T2 | `methyl_butyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0413 | 2-甲基丁酸甲酯 | T2（S0298 为 T3） | `methyl_2_methylbutyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0415 | 异丁酸乙酯 | T2 | `ethyl_isobutyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0416 | 2-甲基丁酸乙酯 | T2（S0298 为 T3） | `ethyl_2_methylbutyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0417 | 3-羟基丁酸乙酯 | T3 | `ethyl_3_hydroxybutyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0418 | 丁二酸二乙酯 | T2 | `diethyl_succinate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0419 | 异丁酸甲酯 | T2 | `methyl_isobutyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0420 | 丁酸丁酯 | T2（S0004 为 T3） | `butyl_butyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0421 | 丁酸异丁酯 | T2 | `isobutyl_butyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0422 | 2-甲基丁酸丁酯 | T2（S0004、S0298 为 T3） | `butyl_2_methylbutyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0423 | 2-甲基丁酸2-甲基丁酯 | T2（S0298 为 T3） | `methylbutyl_methylbutyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0424 | 异丁酸丁酯 | T2（S0004 为 T3） | `butyl_isobutyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0425 | 丁酸戊酯 | T2（S0006 为 T3） | `amyl_butyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0426 | 丁酸异戊酯 | T2 | `isoamyl_butyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0427 | 2-甲基丁酸异戊酯 | T2（S0298 为 T3） | `isoamyl_2_methylbutanoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0428 | 异丁酸异戊酯 | T2 | `isopentyl_isobutyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0429 | 丁酸己酯 | T2（S0010 为 T3） | `hexyl_butyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0430 | 2-甲基丁酸己酯 | T2（S0010、S0298 为 T3） | `hexyl_2_methylbutyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0431 | 丁酸顺式-3-己烯酯（丁酸叶醇酯） | T2（S0027 为 T3） | `cis_3_hexenyl_butyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0432 | 2-甲基丁酸-3-己烯酯 | T2（S0027、S0298 为 T3） | `hex_3_enyl_2_methylbutanoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0433 | 异丁酸庚酯 | T2（S0013 为 T3） | `heptyl_isobutyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0434 | 2-甲基丁酸辛酯 | T2（S0014、S0298 为 T3） | `octyl_2_methylbutyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0435 | 1-辛烯-3-醇丁酸酯 | T3 | `octen_3_yl_butyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0436 | 丁酸苄酯 | T2（S0033 为 T3） | `benzyl_butyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0437 | 异丁酸苄酯 | T2（S0033 为 T3） | `benzyl_isobutyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0438 | 丁酸苯乙酯 | T2（S0034 为 T3） | `phenethyl_butyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0439 | 2-甲基丁酸苯乙酯 | T2（S0034、S0298 为 T3） | `phenethyl_2_methylbutyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0440 | 异丁酸苯乙酯 | T2（S0034 为 T3） | `phenethyl_isobutyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0441 | 丁酸香叶酯 | T2 | `geranyl_butyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0442 | 异丁酸香叶酯 | T2 | `geranyl_isobutyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0443 | 丁酸芳樟酯 | T3 | `linalyl_butyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0444 | 异丁酸芳樟酯 | T3 | `linalyl_isobutyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0445 | 当归酸异丁酯 | T2 / T3 | `isobutyl_angelate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0446 | 异丁酸橙花酯 | T2（S0046 为 T3） | `neryl_isobutyrate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0447 | 正戊酸乙酯 | T2（S0300 为 T3） | `ethyl_valerate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0448 | 丁酰乳酸丁酯 | T3 | `butyl_butyryllactate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0449 | 异戊酸乙酯 | T2（S0303 为 T3） | `ethyl_isovalerate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0450 | 水杨酸丁酯（柳酸丁酯） | T2（S0004、S0349 为 T3） | `butyl_salicylate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0451 | 异戊酸丁酯 | T2（S0004、S0303 为 T3） | `butyl_isovalerate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0452 | 异戊酸异戊酯 | T2（S0303 为 T3） | `isoamyl_isovalerate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0453 | 异戊酸3-己烯酯 | T2（S0027、S0303 为 T3） | `hex_3_enyl_isovalerate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0454 | 异戊酸壬酯 | T2（S0018、S0303 为 T3） | `nonyl_isovalerate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0455 | 异戊酸苯乙酯 | T2（S0034、S0303 为 T3） | `phenethyl_isovalerate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0456 | 异戊酸香叶酯 | T2（S0303 为 T3） | `geranyl_isovalerate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0457 | 己酸甲酯 | T2 | `methyl_hexanoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0458 | 2-己烯酸甲酯 | T2（S0306 为 T3） | `methyl_2_hexenoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0459 | 己酸乙酯 | T2 | `ethyl_hexanoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0460 | 3-己烯酸乙酯 | T2（S0307 为 T3） | `ethyl_3_hexenoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0461 | 3-羟基己酸乙酯 | T3 | `ethyl_3_hydroxyhexanoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0462 | 反式-2-己烯酸乙酯 | T2（S0306 为 T3） | `ethyl_trans_2_hexenoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0463 | 己酸丙酯 | T2 | `propyl_hexanoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0464 | 己酸戊酯 | T2（S0006 为 T3） | `amyl_hexanoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0465 | 己酸异戊酯 | T2 | `isoamyl_hexanoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0466 | 己酸己酯 | T2（S0010 为 T3） | `hexyl_hexanoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0467 | 己酸顺式-3-己烯酯（己酸叶醇酯） | T2（S0027 为 T3） | `cis_3_hexenyl_hexanoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0468 | 庚酸乙酯 | T2（S0308 为 T3） | `ethyl_heptanoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0469 | 庚酸丙酯 | T2（S0308 为 T3） | `propyl_heptanoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0470 | 庚酸丁酯 | T2（S0004、S0308 为 T3） | `butyl_heptanoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0471 | 2-甲基-3-巯基呋喃 | T2（S0077 为 T4） | `methyl_3_furanthiol` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0472 | 辛酸甲酯 | T2 / T3 | `methyl_caprylate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0473 | 辛酸乙酯 | T2 | `ethyl_caprylate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0474 | 顺式-4-辛烯酸乙酯 | T3 | `ethyl_cis_4_octenoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0475 | 顺式-4,7-辛二烯酸乙酯 | T3 | `ethyl_cis_4_7_octadienoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0476 | 辛酸异戊酯 | T2 | `isoamyl_octanoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0477 | 辛酸壬酯 | T2（S0018 为 T3） | `nonyl_octanoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0478 | 辛酸苯乙酯 | T2（S0034 为 T3） | `phenethyl_octanoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0479 | 2-壬烯酸甲酯 | T2（`trans_2_nonenoic_acid` 为 T3） | `methyl_2_nonenoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0480 | 壬酸乙酯 | T2（S0310 为 T3） | `ethyl_nonanoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0481 | 癸酸乙酯 | T2 | `ethyl_decanoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0482 | 反式-2-顺式-4-癸二烯酸乙酯 | T3 / T4 | `ethyl_decadienoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0483 | 十二酸乙酯（月桂酸乙酯） | T2（S0312 为 T3） | `ethyl_laurate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0484 | 十四酸甲酯（肉豆蔻酸甲酯） | T2 / T3 | `methyl_myristate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0485 | 苯甲酸甲酯 | T2 | `methyl_benzoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0486 | 苯甲酸乙酯 | T2 | `ethyl_benzoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0487 | 苯甲酸丙酯 | T2 | `propyl_benzoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0488 | 苯甲酸己酯 | T2（S0010 为 T3） | `hexyl_benzoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0489 | 苯甲酸苄酯 | T2（S0033 为 T3） | `benzyl_benzoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0490 | 苯甲酸顺式-3-己烯酯（苯甲酸叶醇酯） | T3 | `cis_3_hexenyl_benzoate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0491 | 邻氨基苯甲酸甲酯 | T2（`anthranilic_acid` 为 T3） | `methyl_anthranilate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0492 | 苯乙酸甲酯 | T2（S0316 为 T3） | `methyl_phenylacetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0493 | 苯乙酸乙酯 | T2（S0316 为 T3） | `ethyl_phenylacetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0494 | 苯乙酸异戊酯 | T2（S0316 为 T3） | `isoamyl_phenylacetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0495 | 苯乙酸苯乙酯 | T2（S0034、S0316 为 T3） | `phenethyl_phenylacetate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0496 | 惕各酸乙酯 | T2（S0350 为 T3） | `ethyl_tiglate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0497 | 惕各酸苄酯 | T2（S0033、S0350 为 T3） | `benzyl_tiglate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0498 | 乳酸乙酯 | T2 | `ethyl_lactate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0499 | 乳酸丁酯 | T2（S0004 为 T3） | `butyl_lactate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0500 | 肉桂酸甲酯 | T2 / T3（S0318 为 T3） | `methyl_cinnamate` | [additives/19 §1.4](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0501 | 肉桂酸乙酯 | T2（S0318 为 T3） | `ethyl_cinnamate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0502 | 肉桂酸苄酯 | T2（S0033、S0318 为 T3） | `benzyl_cinnamate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0503 | 肉桂酸苯乙酯 | T2（S0034、S0318 为 T3） | `phenethyl_cinnamate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0504 | 肉桂酸肉桂酯 | T2（S0042、S0318 为 T3） | `cinnamyl_cinnamate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0505 | 水杨酸甲酯（柳酸甲酯） | T2 | `methyl_salicylate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0506 | 水杨酸乙酯（柳酸乙酯） | T2（S0349 为 T3） | `ethyl_salicylate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0507 | 水杨酸异戊酯（柳酸异戊酯） | T2（S0349 为 T3） | `isoamyl_salicylate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0508 | 十四酸乙酯（肉豆蔻酸乙酯） | T2 | `ethyl_myristate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0509 | 油酸乙酯 | T2 | `ethyl_oleate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0510 | 棕榈酸乙酯 | T2 | `ethyl_palmitate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0511 | 二氢茉莉酮酸甲酯 | T3 | `methyl_dihydrojasmonate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0512 | 椰子油混合酸乙酯 | T3 | `coconut_acid_ethyl_esters` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0513 | 柠檬酸三乙酯 | T2 | `triethyl_citrate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0514 | 甲酸大茴香酯 | T2（S0041 为 T3） | `anisyl_formate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0515 | 甲酸顺式-3-己烯酯（甲酸叶醇酯） | T2（S0027 为 T3） | `leaf_formate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0516 | 乙酸2-甲基丁酯 | T2 | `methylbutyl_acetate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0517 | 乙酸3-苯丙酯 | T2（S0035 为 T3） | `phenylpropyl_acetate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0518 | 乙酸丁香酯 | T3 | `eugenyl_acetate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0519 | 4,5-二甲基-2-异丁基-3-噻唑啉 | T3 | `dimethyl_isobutyl_thiazoline` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0520 | 乙酸异胡薄荷酯 | T3 | `isopulegyl_acetate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0521 | 乙酸1,3,3-三甲基-2-降龙脑酯 | T3 | `fenchyl_acetate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0522 | 丙酸甲酯 | T2 | `methyl_propionate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0523 | 丙烯酸乙酯 | T2（`acrylic_acid` 为 T3） | `ethyl_acrylate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0524 | 乳酸顺式-3-己烯酯（乳酸叶醇酯） | T2（S0027 为 T3） | `leaf_lactate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0525 | 丙酸癸酯 | T2（S0022 为 T3） | `decyl_propionate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0526 | 反式-2-丁烯酸乙酯 | T2（S1019 为 T3） | `ethyl_crotonate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0527 | 丁酸丙酯 | T2 | `propyl_butyrate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0528 | 异丁酸异丙酯 | T2（S0003 为 T3） | `isopropyl_isobutyrate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0529 | 2-甲基丁酸异丙酯 | T2（S0003、S0298 为 T3） | `isopropyl_methylbutyrate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0530 | 异丁酸己酯 | T2（S0010 为 T3） | `hexyl_isobutyrate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0531 | 丁酸庚酯 | T2（S0013 为 T3） | `heptyl_butyrate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0532 | 异丁酸辛酯 | T2（S0014 为 T3） | `octyl_isobutyrate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0533 | 异丁酸-3-苯丙酯 | T2（S0035 为 T3） | `phenylpropyl_isobutyrate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0534 | 丁酸香茅酯 | T2（S0040 为 T3） | `citronellyl_butyrate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0535 | 丁酸肉桂酯 | T2（S0042 为 T3） | `cinnamyl_butyrate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0536 | 异戊酸甲酯 | T2（S0303 为 T3） | `methyl_isovalerate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0537 | 异戊酸异丁酯 | T2（S0303 为 T3） | `isobutyl_isovalerate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0538 | 异戊酸2-甲基丁酯 | T2（S0303 为 T3） | `methylbutyl_isovalerate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0539 | 异戊酸苄酯 | T2（S0033、S0303 为 T3） | `benzyl_isovalerate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0540 | 2-戊基吡啶 | T3 | `pentylpyridine` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0541 | 异戊酸肉桂酯 | T2（S0042、S0303 为 T3） | `cinnamyl_isovalerate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0542 | 异戊酸薄荷酯 | T2（S0303 为 T3） | `menthyl_isovalerate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0543 | 3-己烯酸甲酯 | T2（S0307 为 T3） | `methyl_3_hexenoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0544 | 正己酸异丁酯 | T2 | `isobutyl_hexanoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0545 | 己酸烯丙酯 | T2（`allyl_alcohol` 为 T3） | `allyl_hexanoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0546 | 己酸芳樟酯 | T3 | `linalyl_hexanoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0547 | 3,7-二甲基-6-辛烯酸甲酯 | T2（S0333 为 T3） | `methyl_citronellate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0548 | 3-壬烯酸甲酯 | T3 | `methyl_3_nonenoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0549 | 9-十一烯酸甲酯 | T3 | `methyl_9_undecenoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0550 | 十一酸乙酯 | T2（S0335 为 T3） | `ethyl_undecanoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0551 | 十四酸异丙酯 | T2（S0003 为 T3） | `isopropyl_myristate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0552 | N-甲基邻氨基苯甲酸甲酯 | T2 / T3 | `methyl_n_methylanthranilate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0553 | 邻氨基苯甲酸乙酯 | T3 | `ethyl_anthranilate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0554 | 苯甲酸异戊酯 | T2 | `isoamyl_benzoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0555 | 苯甲酸苯乙酯 | T2（S0034 为 T3） | `phenethyl_benzoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0556 | 苯乙酸异丁酯 | T2（S0316 为 T3） | `isobutyl_phenylacetate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0557 | 苯乙酸己酯 | T2（S0010、S0316 为 T3） | `hexyl_phenylacetate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0558 | 苯丙酸乙酯 | T3 | `ethyl_hydrocinnamate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0559 | 环己基羧酸甲酯 | T2（S1301 为 T3） | `methyl_cyclohexanecarboxylate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0560 | 大茴香酸甲酯 | T2（S0879 为 T3） | `methyl_anisate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0561 | 大茴香酸乙酯 | T2（S0879 为 T3） | `ethyl_anisate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0562 | 水杨酸苯乙酯 | T3 | `phenethyl_salicylate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0563 | 十二酸异戊酯（月桂酸异戊酯） | T2（S0312 为 T3） | `isoamyl_laurate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0564 | 亚油酸甲酯(48%),亚麻酸甲酯(52%)混合物 | T3 | `methyl_linoleate_linolenate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0565 | 茉莉酮酸甲酯 | T4 | `methyl_jasmonate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0566 | 水杨酸苄酯（柳酸苄酯） | T3 | `benzyl_salicylate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0567 | 肉桂酸异丁酯 | T2（S0318 为 T3） | `isobutyl_cinnamate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0568 | 肉桂酸3-苯丙酯 | T2（S0035、S0318 为 T3） | `phenylpropyl_cinnamate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0569 | 酒石酸二乙酯 | T2 | `diethyl_tartrate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0570 | 菸酸甲酯 | T2（110 为 T3） | `methyl_nicotinate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0571 | 惕各酸苯乙酯 | T2（S0034、S0350 为 T3） | `phenethyl_tiglate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0573 | 3,5,5-三甲基-1-己醇 | T3 | `trimethylhexanol` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0574 | 丁酸茴香酯 | T2（S0041 为 T3） | `anisyl_butyrate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0575 | 异戊酸龙脑酯 | T2（S0303 为 T3） | `bornyl_isovalerate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0576 | 2,6-二甲基-4-庚醇 | T3 | `dimethylheptanol` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0577 | 苯甲酸异丁酯 | T2 | `isobutyl_benzoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0578 | 甲酸橙花酯 | T2（S0046 为 T3） | `neryl_formate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0579 | 乙酸甲基苄醇酯(邻、间、对位混合物) | T3 | `methylbenzyl_acetate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0580 | 乙酸顺式和反式-对1(7),8-䓝二烯-2-醇酯 | T3 | `menthadienyl_acetate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0581 | 乙酸龙脑烯醇酯 | T3 | `campholenyl_acetate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0582 | 丙酸丙酯 | T2 | `propyl_propionate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0583 | 丙酸丁酯 | T2（S0004 为 T3） | `butyl_propionate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0584 | 丙酸己酯 | T2（S0010 为 T3） | `hexyl_propionate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0585 | 丙酮酸乙酯 | T2（S0295 为 T3） | `ethyl_pyruvate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0586 | 丁酸辛酯 | T2（S0014 为 T3） | `octyl_butyrate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0587 | 异丁酸丙酯 | T2 | `propyl_isobutyrate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0588 | 异丁酸异丁酯 | T2 | `isobutyl_isobutyrate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0589 | 异丁酸香茅酯 | T2（S0040 为 T3） | `citronellyl_isobutyrate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0590 | 反式-2-丁烯酸顺式-3-己烯酯 | T2（S0027、S1019 为 T3） | `leaf_crotonate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0591 | 丁二酸单薄荷酯 | T3 | `monomenthyl_succinate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0592 | 正戊酸正戊酯 | T2（S0006、S0300 为 T3） | `pentyl_valerate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0593 | 异戊酸辛酯 | T2（S0014、S0303 为 T3） | `octyl_isovalerate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0594 | 己酸丁酯 | T2（S0004 为 T3） | `butyl_hexanoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0595 | 己酸苯乙酯 | T2（S0034 为 T3） | `phenethyl_hexanoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0596 | 异丁酸叶醇酯 | T2（S0027 为 T3） | `leaf_isobutyrate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0597 | 辛酸己酯 | T2（S0010 为 T3） | `hexyl_octanoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0598 | 2-辛烯酸乙酯 | T3 | `ethyl_2_octenoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0599 | 2,4,7-癸三烯酸乙酯 | T3 | `ethyl_decatrienoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0600 | 苯甲酸芳樟酯 | T3 | `linalyl_benzoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0601 | 反式-2-甲基2-丁烯酸顺式-3-己烯酯（惕各酸叶醇酯） | T2（S0027、S0350 为 T3） | `leaf_tiglate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0602 | 2-丁烯酸异丁酯 | T2（S1019 为 T3） | `isobutyl_crotonate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0603 | 3-甲基丁酸己酯 | T2（S0010、S0303 为 T3） | `hexyl_isovalerate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0604 | 顺式-3-己烯酸顺式-3-己烯酯 | T2（S0027、S0307 为 T3） | `leaf_3_hexenoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0605 | 3-羟基己酸甲酯 | T3 | `methyl_3_hydroxyhexanoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0606 | 苯甲酸香叶酯 | T3 | `geranyl_benzoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0607 | 琥珀酸二甲酯 | T2 | `dimethyl_succinate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0608 | 硬脂酸乙酯 | T2 | `ethyl_stearate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0609 | 3-甲基-2-丁烯-1-醇乙酸酯（乙酸异戊烯酯） | T3 | `prenyl_acetate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0610 | 己酸反式-2-己烯酯 | T2（S0011 为 T3） | `hexenyl_hexanoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0611 | 甲酸龙脑酯 | T2 | `bornyl_formate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0612 | 顺式-4-庚烯酸乙酯 | T3 | `ethyl_4_heptenoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0613 | 辛酸戊酯 | T2（S0006 为 T3） | `amyl_octanoate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0614 | 4-甲基戊酸甲酯 | T2（S0348 为 T3） | `methyl_4_methylvalerate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0615 | 乙酸胡椒醛酯 | T3 | `piperonyl_acetate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0616 | 丙酸肉桂酯 | T2（S0042 为 T3） | `cinnamyl_propionate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0617 | 异丁酸甲基苯基原酯 | T2（S0032 为 T3） | `styrallyl_isobutyrate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0618 | 异丁酸十二酯 | T2 | `dodecyl_isobutyrate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0619 | 异丁酸松油酯 | T3 | `terpinyl_isobutyrate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0620 | 水杨酸异丁酯 | T2（S0349 为 T3） | `isobutyl_salicylate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0621 | 肉桂酸异戊酯 | T2（S0318 为 T3） | `isoamyl_cinnamate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0622 | 乙酸异龙脑酯 | T3 | `isobornyl_acetate` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0623 | γ-戊内酯 | T3 | `gamma_valerolactone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0624 | γ-己内酯 | T3 | `gamma_hexalactone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0625 | γ-庚内酯 | T3 | `gamma_heptalactone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0626 | γ-辛内酯 | T3 | `gamma_octalactone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0627 | γ-壬内酯 | T3 | `gamma_nonalactone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0629 | γ-十二内酯 | T3 | `gamma_dodecalactone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0630 | γ-丁内酯 | T3 | `gamma_butyrolactone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0631 | δ-己内酯 | T3 | `delta_hexalactone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0632 | δ-辛内酯 | T3 | `delta_octalactone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0633 | δ-壬内酯 | T3 | `delta_nonalactone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0634 | δ-癸内酯 | T3 | `delta_decalactone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0635 | δ-十一内酯 | T3 | `delta_undecalactone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0636 | δ-十二内酯 | T3 | `delta_dodecalactone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0637 | 十五内酯 | T2 / T3 | `pentadecanolide` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0638 | 5-羟基-2-癸烯酸δ-内酯 | T2 | `massoia_lactone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0639 | 3-丙叉苯酞 | T3 | `propylidenephthalide` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0640 | 3-丁叉苯酞 | T2 / T3 | `butylidenephthalide` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0641 | 薄荷内酯 | T3 | `mintlactone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0642 | δ-十三内酯 | T3 | `delta_tridecalactone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0643 | δ-十四内酯 | T3 | `delta_tetradecalactone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0644 | 5-羟基-2,4-癸二烯酸内酯 | T4 | `pentyl_pyrone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0645 | 5-羟基-7-癸烯酸内酯（茉莉内酯） | T3 | `jasmine_lactone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0646 | 威士忌内酯 | T3 | `whiskey_lactone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0647 | 二氢猕猴桃内酯 | T3 / T4 | `dihydroactinidiolide` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0648 | 黄葵内酯 | T2 | `ambrettolide` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0649 | α-当归内酯 | T3 | `alpha_angelica_lactone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0650 | γ-甲基癸内酯 | T3 | `gamma_methyldecalactone` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0651 | β-石竹烯 | T2 | `beta_caryophyllene` | [additives/19 §1.5](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0652 | 巴伦西亚橘烯 | T2 | `valencene` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0653 | 月桂烯 | T3 / T2 | `myrcene` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0654 | d-苧烯 | — | `limonene` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0655 | 异松油烯 | T3 | `terpinolene` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0656 | 罗勒烯 | T3 | `ocimene` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0657 | 莰烯 | T3 | `camphene` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0658 | α-蒎烯 | T2 | `alpha_pinene` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0659 | β-蒎烯 | T2 | `beta_pinene` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0660 | 1,8-桉叶素 | T2 | `cineole_1_8` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0661 | 1,4-桉叶素 | T3 | `cineole_1_4` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0662 | 二氢香豆素 | T3 | `dihydrocoumarin` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0663 | 1,4-二甲基-4-乙酰基-1-环己烯 | T3 | `dimethyl_acetyl_cyclohexene` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0664 | 2-甲酰基-6,6-二甲基双环[3.1.1]庚-2-烯（桃金娘烯醛） | T3 | `myrtenal` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0665 | 茶螺烷 | T3 | `theaspirane` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0666 | 1,3,5-十一碳三烯 | T3 | `undecatriene` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0667 | 对,α-二甲基苯乙烯 | T3 | `p_alpha_dimethylstyrene` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0668 | α-水芹烯 | T2 | `alpha_phellandrene` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0669 | 红没药烯 | T2 / T3 | `bisabolene` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0670 | γ-松油烯 | T2 / T3 | `gamma_terpinene` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0671 | 6-羟基二氢茶螺烷 | T3 | `hydroxydihydrotheaspirane` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0672 | 1-甲基-3-甲氧基-4-异丙基苯 | T3 | `thymol_methyl_ether` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0673 | 间-二甲氧基苯 | T3 | `m_dimethoxybenzene` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0674 | 对-异丙基甲苯 | T3 | `p_cymene` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0675 | 3,4-二甲酚 | T3 | `dimethylphenol_3_4` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0676 | 1-甲基萘 | T3 | `methylnaphthalene_1` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0677 | 1,2-二甲氧基苯 | T3 | `o_dimethoxybenzene` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0678 | α-金合欢烯 | T3 | `alpha_farnesene` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0679 | 苏合香烯 | T3 | `styrene` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0680 | α-松油烯 | T3 / T2 | `alpha_terpinene` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0681 | 3-蒈烯 | T2 | `carene_3` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0682 | 聚苧烯 | T3 | `polylimonene` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0683 | 香菇素 | T2 / T3 | `lenthionine` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0684 | 氧化石竹烯 | T3 | `caryophyllene_oxide` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0685 | 2,4,6-三甲基-1,3,5-三氧杂环己烷（三聚乙醛） | T2 | `paraldehyde` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0686 | 甲硫醇 | T3 | `methyl_mercaptan` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0687 | 3-甲硫基丙醇 | T3 | `methylthio_propanol` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0688 | 正丁硫醇 | T3 | `butanethiol` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0689 | 2-甲基-1-丁硫醇 | T3 | `methyl_butanethiol_2_1` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0690 | 3-(甲硫基)-1-己醇 | T3 | `methylthio_hexanol_3` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0691 | 1,6-己二硫醇 | T3 | `hexanedithiol` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0692 | 糠基硫醇（咖啡醛） | T3 | `furfuryl_mercaptan` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0693 | 二甲基硫醚 | T3 | `dimethyl_sulfide` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0694 | 二甲基二硫醚 | T3 | `dimethyl_disulfide` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0695 | 二甲基三硫醚 | T3 | `dimethyl_trisulfide` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0696 | 二丁基硫醚 | T3 | `dibutyl_sulfide` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0697 | 2,2'-(硫代二亚甲基)-二呋喃二糠基硫醚 | T3 | `difurfuryl_sulfide` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0698 | 二糠基二硫醚 | T3 | `difurfuryl_disulfide` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0699 | 邻-甲硫基苯酚 | T3 | `o_methylthiophenol` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0700 | 3-甲硫基丙醛 | T3 | `methylthio_propionaldehyde` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0701 | 8-巯基薄荷酮 | T3 | `mercaptomenthone_8` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0702 | 硫代乙酸糠酯 | T3 | `furfuryl_thioacetate` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0703 | 3-甲硫基丙酸甲酯 | T3 | `methyl_methylthiopropionate` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0704 | 3-甲硫基丙酸乙酯 | T3 | `ethyl_methylthiopropionate` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0705 | 吲哚 | T3 | `indole` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0706 | 三甲基胺 | T3 | `trimethylamine` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0707 | 玫瑰醚 | T3 | `rose_oxide` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0708 | 羟基香茅醇 | T3 | `hydroxycitronellol` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0709 | 3,5-二甲基-1,2,4-三硫杂环戊烷 | T3 | `dimethyl_trithiolane` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0710 | 2-甲基吡嗪 | T3 | `methylpyrazine` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0711 | 2,3-二甲基吡嗪 | T3 | `dimethylpyrazine_2_3` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0712 | 2,5-二甲基吡嗪 | T3 | `dimethylpyrazine_2_5` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0714 | 对-甲苯基乙醛 | T3 | `p_tolylacetaldehyde` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0715 | 2,6,6-三甲基-1 或2-环己烯-1-甲醛 | T3 | `cyclocitral` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0716 | 2-异丁基-3-甲基吡嗪 | T3 | `isobutyl_methylpyrazine` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0717 | 2-甲氧基-3-仲丁基吡嗪 | T3 | `methoxy_sec_butylpyrazine` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0718 | 2,3-二乙基吡嗪 | T3 | `diethylpyrazine_2_3` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0719 | 3-乙基-2,6-二甲基吡嗪 | T3 | `ethyl_dimethylpyrazine_3_2_6` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0720 | 2-乙酰基吡嗪 | T3 | `acetylpyrazine` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0721 | 2-乙酰基-3-乙基吡嗪 | T3 | `acetyl_ethylpyrazine` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0722 | 2,3-二乙基-5-甲基吡嗪 | T3 | `diethyl_methylpyrazine` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0723 | 5-异丙基-2-甲基吡嗪 | T3 | `isopropyl_methylpyrazine` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0724 | 2,6-二甲基吡啶 | T3 | `dimethylpyridine_2_6` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0725 | 4-甲基噻唑 | T3 | `methylthiazole_4` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0726 | α-甲基肉桂醛 | T3 | `alpha_methylcinnamaldehyde` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0727 | 5-羟乙基-4-甲基噻唑 | T3 | `hydroxyethyl_methylthiazole` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0728 | 2,4,5-三甲基噻唑 | T3 | `trimethylthiazole` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0729 | 2-乙基-4-甲基噻唑 | T3 | `ethyl_methylthiazole` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0730 | 5-乙烯基-4-甲基噻唑 | T3 | `methyl_vinylthiazole` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0731 | 2-乙酰基噻唑 | T3 | `acetylthiazole` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0732 | 2-异丙基-4-甲基噻唑 | T3 | `isopropyl_methylthiazole` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0733 | 2-异丁基噻唑 | T3 | `isobutylthiazole` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0734 | 苯并噻唑 | T3 | `benzothiazole` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0735 | N-糠基吡咯 | T3 | `furfurylpyrrole` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0736 | 2-乙酰基吡咯 | T3 | `acetylpyrrole` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0737 | 5,6,7,8-四氢喹啉 | T3 | `tetrahydroquinoxaline` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0738 | 2,4,5-三甲基-3-噁唑啉 | T3 | `trimethyl_oxazoline` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0739 | 2-甲基-4-丙基-1,3-噁唑烷 | T2（S0786 为 T3） | `methyl_propyl_oxathiane` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0740 | 吡啶 | T3 | `pyridine` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0741 | 二丙基二硫醚 | T3 | `dipropyl_disulfide` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0742 | 2-戊基硫醇 | T3 | `pentanethiol_2` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0743 | 邻-甲基苯硫酚 | T3 | `o_toluenethiol` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0744 | 苄基硫醇 | T3 | `benzyl_mercaptan` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0745 | 1-对-䓝烯-8-硫醇 | T3 | `p_menthene_8_thiol` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0746 | 甲基丙基二硫醚 | T3 | `methyl_propyl_disulfide` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0747 | 甲基苄基二硫醚 | T3 | `methyl_benzyl_disulfide` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0748 | 甲基糠基二硫醚 | T3 | `methyl_furfuryl_disulfide` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0749 | 烯丙基二硫醚 | T2 / T3 | `diallyl_disulfide` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0750 | 双(2-甲基-3-呋喃基)二硫醚 | T3（S0471 为 T4） | `bis_methylfuryl_disulfide` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0751 | 糠基甲基硫醚 | T3 | `furfuryl_methyl_sulfide` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0752 | 2,6-二甲基苯硫酚 | T3 | `dimethylthiophenol_2_6` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0753 | 2-甲基-3(2-呋喃基)丙烯醛 | T3 | `methyl_furylacrolein` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0754 | 2-甲基四氢噻吩-3-酮 | T3 | `methyltetrahydrothiophenone` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0755 | 2-甲基-5-(甲硫基)呋喃 | T3 | `methyl_methylthiofuran_2_5` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0756 | 2-羟基-3,5,5-三甲基-2-环己烯酮 | T3 | `hydroxy_trimethylcyclohexenone` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0757 | 糠酸甲酯 | T3 | `methyl_furoate` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0758 | 硫代乙酸乙酯 | T3 | `ethyl_thioacetate` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0759 | 硫代乙酸丙酯 | T3 | `propyl_thioacetate` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0760 | 3-巯基丙酸乙酯 | T3 | `ethyl_mercaptopropionate_3` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0761 | 硫代丁酸甲酯 | T3 | `methyl_thiobutyrate` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0762 | 异硫氰酸烯丙酯 | T2 / T3 | `allyl_isothiocyanate` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0763 | 2-硫代糠酸甲酯 | T3 | `methyl_thiofuroate` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0764 | 3-甲基-1,2,4-三噻烷 | T3 | `methyl_trithiane` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0765 | 2,3,5,6-四甲基吡嗪 | T3 / T4 | `tetramethylpyrazine` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0766 | 2-乙基吡嗪 | T3 | `ethylpyrazine` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0767 | 2-乙基-3,(5或6)-二甲基吡嗪 | T3 | `ethyl_dimethylpyrazine_isomers` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0768 | 2-甲氧基-3-异丁基吡嗪 | T3（`leucinamide` 为 T4） | `methoxy_isobutylpyrazine` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0769 | 1-甲基-2-乙酰基吡咯 | T3 | `methyl_acetylpyrrole` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0770 | N-乙基-2-乙酰基吡咯 | T3 | `ethyl_acetylpyrrole` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0771 | 喹啉 | T3 | `quinoline` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0772 | 6-甲基喹啉 | T3 | `methylquinoline_6` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0773 | 5-甲基喹噁啉 | T3 | `methylquinoxaline_5` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0774 | 哌啶 | T3 | `piperidine` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0775 | β-甲基吲哚 | T3 | `beta_methylindole` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0776 | 5-乙基-2-甲基吡啶 | T3 | `ethyl_methylpyridine` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0777 | 3-乙基吡啶 | T3 | `ethylpyridine_3` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0778 | 2-乙酰基吡啶 | T3 | `acetylpyridine_2` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0779 | 3-乙酰基吡啶 | T3 | `acetylpyridine_3` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0780 | 甲酸肉桂酯 | T2（S0042 为 T3） | `cinnamyl_formate` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0781 | 异戊胺 | T3 | `isopentylamine` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0782 | 苯乙胺 | T3 | `phenethylamine` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0783 | 2-甲基-1,3-二硫环戊烷 | T2（S0887 为 T3） | `methyl_dithiolane` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0784 | 6-乙酰氧基二氢茶螺烷 | T3 | `acetoxydihydrotheaspirane` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0785 | 4,5-二甲基噻唑 | T3 | `dimethylthiazole_4_5` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0786 | 3-巯基己醇 | T3 | `mercaptohexanol_3` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0787 | 三硫丙酮 | T3 | `trithioacetone` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0788 | 2,6-二甲基吡嗪 | T3 | `dimethylpyrazine_2_6` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0789 | 2-(甲硫基)乙酸乙酯 | T3 | `ethyl_methylthioacetate` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0790 | 乙酸3-巯基己酯 | T3 | `mercaptohexyl_acetate` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0791 | 2-(甲基二硫基)丙酸乙酯 | T3 | `ethyl_methyldithiopropionate` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0792 | 3-(甲硫基)丁酸乙酯 | T3 | `ethyl_methylthiobutyrate` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0793 | 丁酸3-巯基己酯 | T2（S0786 为 T3） | `mercaptohexyl_butyrate` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0794 | 己酸3-巯基己酯 | T2（S0786 为 T3） | `mercaptohexyl_hexanoate` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0795 | 糠醇 | T3 | `furfuryl_alcohol` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0796 | 四氢糠醇 | T3 | `tetrahydrofurfuryl_alcohol` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0798 | 2-乙基-3-甲基吡嗪 | T3 | `ethyl_methylpyrazine_2_3` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0799 | 3-甲基-2-丁硫醇 | T3 | `methyl_butanethiol_3_2` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0800 | 2-甲基-3-四氢呋喃硫醇 | T3 | `methyltetrahydrofuranthiol` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0801 | 丙硫醇 | T3 | `propanethiol` | [additives/19 §1.6](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0802 | 1,3-丙二硫醇 | T3 | `propane_1_3_dithiol` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0803 | 烯丙基硫醇 | T3 | `allyl_mercaptan` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0804 | 4-甲氧基-2-甲基-2-丁硫醇 | T3 | `methoxy_methylbutanethiol` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0805 | 2-苯乙硫醇 | T3 | `phenylethyl_mercaptan` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0806 | 3-巯基-3-甲基-1-丁醇 | T3 | `mercapto_3_methylbutanol` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0807 | 甲基2-甲基-3-呋喃基二硫醚 | T3（S0471 为 T4） | `methyl_methylfuryl_disulfide` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0808 | 甲基乙基硫醚 | T3 | `methyl_ethyl_sulfide` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0809 | 甲基苯基二硫醚 | T3 | `methyl_phenyl_disulfide` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0810 | 二乙基硫醚 | T3 | `diethyl_sulfide` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0811 | 二丙基三硫醚 | T3 | `dipropyl_trisulfide` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0812 | 丙烯基丙基二硫醚 | T3 | `propenyl_propyl_disulfide` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0813 | 二烯丙基硫醚 | T3 | `diallyl_sulfide` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0814 | 二烯丙基三硫醚 | T2 / T3 | `diallyl_trisulfide` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0815 | 二烯丙基四硫醚 | T3 | `diallyl_tetrasulfide` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0816 | 2-甲硫甲基-2-丁烯醛 | T3 | `methylthiomethyl_butenal` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0817 | 3-甲硫基己醛 | T3 | `methylthio_hexanal` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0818 | 乙酸环己酯 | T2 | `cyclohexyl_acetate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0819 | 邻-氨基苯乙酮 | T3 | `o_aminoacetophenone` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0820 | 2-甲基-3-甲硫基呋喃 | T3（S0471 为 T4） | `methyl_methylthio_furan` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0821 | 甲酸3-巯基3-甲基丁酯 | T2（S0806 为 T3） | `mercapto_methylbutyl_formate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0822 | 乙酸3-甲硫基丙酯 | T2（S0687 为 T3） | `methylthiopropyl_acetate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0823 | 3-甲基硫代丁酸S-甲酯 | T3 | `s_methyl_thioisovalerate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0824 | 甲硫磺酸S-甲酯 | T3 | `s_methyl_methanethiosulfonate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0825 | 2-甲硫基丁酸甲酯 | T3 | `methyl_methylthiobutyrate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0826 | 3-甲硫基-1-己醇乙酸酯 | T2（S0690 为 T3） | `methylthiohexyl_acetate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0827 | 甲硫醇乙酸酯 | T3 | `s_methyl_thioacetate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0828 | (5H)-5-甲基-6,7-二氢环戊基并(b)吡嗪 | T3 | `methyl_dihydrocyclopentapyrazine` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0829 | 2-甲氧基吡嗪 | T3 | `methoxypyrazine` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0830 | 2-,5或6-甲氧基-3-甲基吡嗪 | T3 | `methoxy_methylpyrazine` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0831 | 2-乙酰基-3,5(或6)-二甲基吡嗪 | T3 | `acetyl_dimethylpyrazine` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0832 | 2-乙酰基3-甲基吡嗪 | T3 | `acetyl_methylpyrazine` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0833 | 四氢吡咯(吡咯烷) | T3 | `pyrrolidine` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0834 | 2-异丁基吡啶 | T3 | `isobutylpyridine` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0835 | 2-乙基-4,5-二甲基噁唑 | T3 | `ethyl_dimethyloxazole` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0836 | 硫化铵 | T2（S1010 为 T3） | `ammonium_sulfide` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0837 | 2-巯基丙酸乙酯 | T3 | `ethyl_mercaptopropionate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0838 | N-(4-羟基-3-甲氧基苄基)壬酰胺 | T3 | `nonivamide` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0839 | 1,4-二噻烷 | T3 | `dithiane_1_4` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0840 | 桃金娘烯醇 | T3 | `myrtenol` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0841 | 胡椒碱 | T2 | `piperine` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0842 | 2,3-二甲基苯并呋喃 | T3 | `dimethylbenzofuran` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0843 | 4-羟基-5-甲基-3(2H)-呋喃酮 | T2 | `norfuraneol` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0844 | γ-紫罗兰酮 | T3 | `gamma_ionone` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0845 | α-二氢紫罗兰酮 | T3 | `alpha_dihydroionone` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0846 | d-胡椒酮 | T2 | `d_piperitone` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0847 | 胡椒烯酮 | T2 | `piperitenone` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0848 | L-天冬氨酸 | — | `l_aspartic_acid` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0849 | DL-异亮氨酸 | T3 | `dl_isoleucine` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0850 | 焦木酸提取物 | T2 | `pyroligneous_acid_extract` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0853 | 琥珀酸二钠 | — | `disodium_succinate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0854 | 5’-鸟苷酸二钠 | — | `guanylic_disodium` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0855 | 5’-肌苷酸二钠 | — | `inosinic_disodium` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0857 | δ-十六内酯 | T3 | `delta_hexadecalactone` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0858 | (+/-)二氢薄荷内酯 | T3 | `dihydromintlactone` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0859 | 顺式-4-十二烯醛 | T3 | `z4_dodecenal` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0860 | 4,5-环氧反式-2-癸烯醛 | T3 | `epoxy_decenal` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0861 | 2-乙基-5-甲基吡嗪 | T3 | `ethyl_methylpyrazine` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0862 | 顺式-3-顺式-6-壬二烯-1-醇 | T4 | `cis_cis_nonadienol` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0863 | 2-甲基-1-丁醇 | T2 | `active_amyl_alcohol` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0864 | 异龙脑 | T3 | `isoborneol` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0865 | 2-壬醇 | T3 | `nonan_2_ol` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0866 | 反式-2-辛烯-1-醇 | T3 | `trans_2_octenol` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0867 | 香芹醇 | T3 | `carveol` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0868 | 对-䓝烷-2-酮 | T3 | `carvomenthone` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0869 | 4-甲基-3-戊烯-2-酮 | T3 | `mesityl_oxide` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0870 | 反式,反式-3,5-辛二烯-2-酮 | T3 | `octadienone` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0871 | 2-甲基呋喃 | T3 | `methylfuran` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0872 | 3-癸烯-2-酮 | T3 | `decenone` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0873 | 2-辛烯-4-酮 | T3 | `octen_4_one` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0874 | 2-呋喃基-2-丙酮 | T3 | `furyl_acetone` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0875 | 5-甲基-2,3-己二酮 | T3 | `methyl_hexanedione` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0876 | 2-甲基-3-戊烯酸 | T3 | `methyl_pentenoic_acid` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0877 | L-酪氨酸 | — | 见 546 | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 见 546 |
| S0878 | 2-氧代戊二酸 | T4 | `oxoglutaric_acid` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0879 | 4-茴香酸 | T3 | `anisic_acid` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0880 | 亚油酸 | T3 | `linoleic_acid` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0881 | 甘草酸 | — | `glycyrrhizic_acid` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0882 | L-胱氨酸 | — | `l_cystine` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0883 | L-蛋氨酸 | — | 见 545 | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 见 545 |
| S0884 | L-谷氨酰胺 | T4 | `l_glutamine` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0885 | 2-丙硫醇 | T3 | `isopropyl_mercaptan` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0886 | 4-巯基-4-甲基-2-戊酮 | T3 | `mercapto_methylpentanone` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0887 | 1,2-乙二硫醇 | T3 | `ethanedithiol` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0888 | 异戊烯基硫醇 | T3 | `prenyl_mercaptan` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0889 | d,l-(3-氨基-3-羧基丙基)二甲基氯化锍 | T3 | `methylmethionine_sulfonium_chloride` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0890 | 2-甲基-3-硫代乙酰氧基-4,5-二氢呋喃 | T3 | `methyl_thioacetoxy_dihydrofuran` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0891 | 异丁基硫醇 | T3 | `isobutyl_mercaptan` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0892 | 苯硫酚(原名称为苄基硫醇) | T3 | `benzenethiol` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0893 | 异硫氰酸苄酯 | T1 | `benzyl_isothiocyanate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0894 | 甲基烯丙基三硫醚 | T3 | `allyl_methyl_trisulfide` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0895 | 2-戊基噻吩 | T3 | `pentylthiophene` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0896 | 3,5-二乙基-1,2,4-三硫杂环戊烷 | T3 | `diethyl_trithiolane` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0897 | 噻吩 | T3 | `thiophene` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0898 | 2,4,6-三甲基二氢-4H-1,3,5-二噻嗪 | T3 | `trimethyl_dithiazine` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0899 | 异硫氰酸3-甲硫基丙酯 | T3 | `methylthiopropyl_isothiocyanate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0900 | 3-甲基丁基硫醇 | T3 | `isoamyl_mercaptan` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0901 | 2-乙酰基-2-噻唑啉 | T2（S0964 为 T3） | `acetyl_thiazoline` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0902 | 甲基丙基三硫醚 | T3 | `methyl_propyl_trisulfide` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0903 | 噻唑 | T3 | `thiazole` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0904 | 吡嗪 | T3 | `pyrazine` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0905 | 甲基1-丙烯基二硫醚 | T3 | `methyl_propenyl_disulfide` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0906 | 甲酸丙酯 | T2 | `propyl_formate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0907 | 香兰素3-(l-䓝氧基)丙-1,2-二醇缩醛 | T2（S1296 为 T3） | `vanillin_menthoxypropanediol_acetal` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0908 | 3-戊烯-2-酮 | T3 | `pentenone` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0909 | 十二酸甲酯（月桂酸甲酯） | T2（S0312 为 T3） | `methyl_laurate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0910 | 乙酸紫苏酯 | T3 | `perillyl_acetate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0911 | 苹果酸二乙酯 | T2 | `diethyl_malate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0912 | 甲硫基乙酸甲酯 | T3 | `methyl_methylthioacetate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0913 | 2-乙酰基-1-吡咯啉 | T3 | `acetyl_pyrroline` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0914 | 甲酸异丙酯 | T2（S0003 为 T3） | `isopropyl_formate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0915 | 4-甲基-2-戊烯醛 | T3 | `methyl_pentenal` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0916 | 亚油酸乙酯 | T2（S0880 为 T3） | `ethyl_linoleate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0917 | 2,4,6-三异丁基-5,6-二氢-4H-1,3,5-二噻嗪 | T3 | `triisobutyl_dithiazine` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0918 | 乙酸十二醇酯 | T2 | `dodecyl_acetate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0919 | 2-乙基丁醛 | T3 | `ethylbutyraldehyde` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0920 | 辛酸辛酯 | T2（S0014 为 T3） | `octyl_octanoate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0921 | 己醛二乙缩醛 | T2（S0130 为 T3） | `hexanal_diethyl_acetal` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0922 | 丙酸异丙酯 | T2（S0003 为 T3） | `isopropyl_propionate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0923 | 丁酸反式-2-己烯酯 | T2（S0011 为 T3） | `trans_2_hexenyl_butyrate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0924 | 异硫氰酸丁酯 | T3 | `butyl_isothiocyanate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0925 | N-葡糖酰基乙醇胺 | T3 | `gluconyl_ethanolamine` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0926 | N-乳酰基乙醇胺 | T3 | `lactoyl_ethanolamine` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0927 | 1-庚烯-3-醇 | T3 | `hepten_3_ol` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0928 | 乙硫醇 | T3 | `ethanethiol` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0930 | 乙酸l-龙脑酯 | T2 / T3 | `l_bornyl_acetate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0931 | 反式-α-突厥酮 | T3 | `trans_alpha_damascone` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0932 | 二乙基二硫醚 | T3 | `diethyl_disulfide` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0933 | 2,5-二甲基-3(2H)-呋喃酮 | T3 | `dimethylfuranone` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0934 | 香叶酸 | T3 | `geranic_acid` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0935 | 1-(3-羟基-5-甲基-2-噻吩)乙酮 | T3 | `hydroxy_methylthienyl_ethanone` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0936 | 异黄葵内酯 | T3 | `isoambrettolide` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0937 | 异丁酸异龙脑酯 | T3 | `isobornyl_isobutyrate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0938 | N-甲基邻氨基苯甲酸异丁酯 | T3 | `isobutyl_n_methylanthranilate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0939 | 丁酸3-(甲硫基)丙酯 | T2（S0687 为 T3） | `methionyl_butyrate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0940 | (S1)-甲氧基-3-庚硫醇 | T3 | `methoxy_heptanethiol` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0941 | 5-Z-辛烯酸甲酯 | T3 | `methyl_z5_octenoate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0942 | N-乙酰基邻氨基苯甲酸甲酯 | T3 | `methyl_n_acetylanthranilate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0943 | 3-甲基-2-(3-甲基-2-丁烯)呋喃 | T3 | `rosefuran` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0944 | 乙酸植醇酯 | T3 | `phytyl_acetate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0945 | 3,7,11-三甲基十二碳-2,6,10-三烯醇乙酸酯 | T3 | `farnesyl_acetate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0946 | 三乙胺 | T3 | `triethylamine` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0947 | 丙酸茴香酯 | T2（S0041 为 T3） | `anisyl_propionate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0948 | 丁酸3-丁酮-2-醇酯 | T2（S0204 为 T3） | `acetoin_butyrate` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0949 | 异喹啉 | T2 | `isoquinoline` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0950 | 2-丙酰噻唑 | T3 | `propionylthiazole` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0951 | 2(4)-异丙基-4(2),6-二甲基二氢(4H)-1,3,5-二噻嗪 | T3 | `isopropyl_dimethyl_dithiazine` | [additives/19 §1.7](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0952 | 丁酸松油酯 | T3 | `terpinyl_butyrate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0953 | 3-正丁基苯酞 | T3 | `butylphthalide` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0954 | 2,2-二甲基-5-(1-甲基-1-丙烯基)四氢呋喃 | T3 | `dimethyl_methylpropenyl_thf` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0955 | (6R)-3-甲基-6-(1-甲基乙基)-2-环己烯-1-酮 | T2 | `r_piperitone` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0956 | 3-甲基-2-丁烯-1-醇 | T3 | `prenol` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0957 | 对-䓝-1-烯-9-醇乙酸酯 | T3 | `p_menthenyl_acetate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0958 | 乙酸2-辛烯醇酯 | T3 | `octenyl_acetate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0959 | 1-(对-甲氧基苯基)-2-丙酮 | T3 | `p_methoxyphenylacetone` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0960 | 十八酸丁酯（硬脂酸丁酯） | T2（S0004 为 T3） | `butyl_stearate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0961 | (+/-)-1-苯乙基硫醇 | T3 | `alpha_methylbenzyl_mercaptan` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0962 | 4-异丙基-2-环己烯酮 | T3 | `cryptone` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0963 | 邻-甲氧基苯甲酸甲酯 | T3 | `methyl_o_methoxybenzoate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0964 | 丙酮醛 | T3 | `pyruvaldehyde` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0965 | 甲基乙基三硫醚 | T3 | `methyl_ethyl_trisulfide` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0966 | 2-甲基-2-(甲二硫基)-丙醛 | T3 | `methyldithio_isobutyraldehyde` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0967 | 二(甲硫基)甲烷 | T3 | `bis_methylthio_methane` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0968 | 2,3,5-三硫杂己烷 | T3 | `trithiahexane` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0969 | 4-乙基辛酸 | T3 | `ethyloctanoic_acid` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0970 | 二氢诺卡酮 | T3 | `dihydronootkatone` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0971 | 1-乙氧基-3-甲基-2-丁烯 | T3 | `prenyl_ethyl_ether` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0972 | 2-乙烯基-2-甲基-5-(1-甲基乙烯基)四氢呋喃 | T3 | `dehydroxy_linalool_oxide` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0973 | 异戊酸糠酯 | T3 | `furfuryl_isovalerate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0974 | 异戊酸芳樟酯 | T3 | `linalyl_isovalerate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0975 | 3-甲基-2-丁醇 | T3 | `methyl_isopropyl_carbinol` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0976 | 3-甲基-1-戊醇 | T3 | `methylpentanol` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0977 | 4-甲基-2-戊酮 | T3 | `methyl_isobutyl_ketone` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0978 | 反式-3-顺式-6-壬二烯醇 | T3（`nonenynol` 为 T4） | `nonadienol_3e_6z` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0979 | 庚酸甲酯 | T2（S0308 为 T3） | `methyl_heptanoate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0980 | 顺式-3-己烯醇丙酸酯 | T2（S0027 为 T3） | `cis_hexenyl_propionate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0981 | 反式-2-癸烯酸乙酯 | T2（S1104 为 T3） | `ethyl_trans_2_decenoate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0982 | 2-乙基苯酚 | T3 | `o_ethylphenol` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0983 | 盐酸硫胺素 | — | `thiamine_hydrochloride` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S0984 | N-甲基吡咯-2-甲醛 | T3 | `methylpyrrole_carboxaldehyde` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0985 | 乙酸香兰素酯 | T3 | `vanillin_acetate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0986 | L-组氨酸 | T4 | `l_histidine` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0987 | δ-突厥酮 | T3 | `delta_damascone` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0988 | 2-甲基戊酸乙酯 | T2（S0301 为 T3） | `ethyl_methylpentanoate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0989 | 4-甲硫基-2-丁酮 | T3 | `methylthioethyl_methyl_ketone` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0990 | 乳酸l-薄荷酯 | T2 | `menthyl_lactate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0991 | 甲基3-甲基-1-丁烯基二硫醚 | T3 | `methyl_methylbutenyl_disulfide` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0992 | 1-巯基-2-丙酮 | T3 | `mercaptoacetone` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0993 | 乙酸正戊酯 | T2（S0006 为 T3） | `amyl_acetate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0994 | 胡薄荷酮 | T2 | `pulegone` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0995 | 1-苯基丙醇-1 | T3 | `alpha_ethylbenzyl_alcohol` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0996 | 4-苯基2-丁醇 | T3 | `methyl_phenethyl_carbinol` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0997 | 庚醇-3 | T3 | `ethyl_butyl_carbinol` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S0998 | 3-乙酰氧基己酸甲酯 | T3 | `methyl_acetoxyhexanoate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S0999 | 对-䓝-1-烯-3-醇 | T3 | `piperitol` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1000 | 4-苧醇 | T2 | `sabinene_hydrate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1001 | 丙酮酸顺式-3-己烯酯（丙酮酸叶醇酯） | T2（S0027、S0295 为 T3） | `cis_hexenyl_pyruvate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1002 | 联苯 | T3 | `biphenyl` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1003 | 顺式-4-羟基-6-十二烯酸内酯 | T4 | `z_dodecenolactone` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1004 | 甲基亚磺酰甲烷 | T3 | `dimethyl_sulfoxide` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1005 | 3,7-二甲基-2,6-辛二烯酸甲酯（香叶酸甲酯） | T2（S0934 为 T3） | `methyl_geranate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1006 | 反式和顺式-4,8-二甲基-3,7-壬二烯-2-酮 | T3 | `dimethylnonadienone` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1007 | 异亚戊基异戊胺 | T3 | `isopentylidene_isopentylamine` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1008 | 戊酸异戊酯 | T2（S0300 为 T3） | `isoamyl_valerate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1009 | 丙酸反式-2-己烯酯 | T2（S0011 为 T3） | `trans_hexenyl_propionate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1010 | 硫化氢(仅用于热反应香料) | T3 | `hydrogen_sulfide` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1011 | 戊酸甲酯 | T2（S0300 为 T3） | `methyl_valerate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1012 | 丁酸异丙酯 | T2（S0003 为 T3） | `isopropyl_butyrate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1013 | 烯丙基甲基二硫醚 | T3 | `allyl_methyl_disulfide` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1014 | 3-壬酮 | T3 | `ethyl_hexyl_ketone` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1015 | 二苄基二硫醚 | T3 | `dibenzyl_disulfide` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1016 | 苯乙酸顺式-3-己烯酯（苯乙酸叶醇酯） | T2（S0027、S0316 为 T3） | `cis_hexenyl_phenylacetate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1017 | 乙酸3-(乙酰巯基)己酯 | T3 | `acetylmercaptohexyl_acetate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1018 | 己酸甲硫醇酯 | T3 | `s_methyl_hexanethioate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1019 | 反式-2-丁烯酸（巴豆酸） | T3 | `crotonic_acid` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1020 | 戊酸顺式-3-己烯酯（戊酸叶醇酯） | T2（S0027、S0300 为 T3） | `cis_hexenyl_valerate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1021 | 己酸苄酯 | T2（S0033 为 T3） | `benzyl_hexanoate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1022 | 烯丙基丙基二硫醚 | T3 | `allyl_propyl_disulfide` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1023 | 2,8-表硫-顺式-对-䓝烷 4,7,7-三甲基-6-硫杂双环[3.2.1]辛烷 硫代桉叶素 | T3 | `thiocineole` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1024 | 癸酸甲酯 | T2 | `methyl_decanoate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1025 | 甲酸异丁酯 | T2 | `isobutyl_formate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1026 | 4-庚酮 | T3 | `dipropyl_ketone` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1027 | 戊酸丁酯 | T2（S0004、S0300 为 T3） | `butyl_valerate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1028 | 丁酸环己酯 | T2 | `cyclohexyl_butyrate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1029 | 山梨酸乙酯 | T2 | `ethyl_sorbate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1030 | 单油酸甘油酯 | T3 | `glyceryl_monooleate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1031 | 5-羟基-4-辛酮 | T3 | `butyroin` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1032 | 壬酸甲酯 | T2（S0310 为 T3） | `methyl_nonanoate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1033 | 丙酸橙花酯 | T2（S0046 为 T3） | `neryl_propionate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1034 | 肉桂酸丙酯 | T2（S0318 为 T3） | `propyl_cinnamate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1035 | 丁酸玫瑰酯 | T2 | `rhodinyl_butyrate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1036 | 异丁酸玫瑰酯 | T2 | `rhodinyl_isobutyrate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1037 | 丙酸松油酯 | T3 | `terpinyl_propionate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1038 | 丙酸糠酯 | T3 | `furfuryl_propionate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1039 | 戊酸糠酯 | T3 | `furfuryl_valerate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1040 | 异茉莉酮 | T3 | `isojasmone` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1041 | 苄基甲基硫醚 | T3 | `benzyl_methyl_sulfide` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1042 | 3-甲基-2-丁烯醛 | T3 | `prenal` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1043 | 2,4-癸二烯酸丙酯 | T3 | `propyl_decadienoate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1044 | 反式-2-己烯酸己酯 | T2（S0010、S0306 为 T3） | `hexyl_trans_hexenoate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1045 | 4-烯丙基-2,6-二甲氧基苯酚 | T3（S0109 为 T4） | `methoxyeugenol` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1046 | 2-羟基-4-甲基戊酸甲酯 | T3（S0326 为 T4） | `methyl_leucate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1047 | 反式-2-辛烯酸甲酯 | T3 | `methyl_trans_octenoate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1048 | 2,2,6-三甲基-6-乙烯基四氢吡喃 | T3 | `trimethyl_vinyl_tetrahydropyran` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1049 | 香紫苏内酯 | T3（`sclareol` 为 T4） | `sclareolide` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1050 | 苯甲酸甲硫醇酯 | T3 | `s_methyl_thiobenzoate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1051 | 反式-2-己烯酸顺式-3-己烯酯 | T2（S0027、S0306 为 T3） | `cis_hexenyl_trans_hexenoate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1052 | 2-巯基苯甲醚 | T3 | `o_methoxythiophenol` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1053 | 香兰素苏和赤-2,3-丁二醇缩醛 | T2（`butane_2_3_diol` 为 T3） | `vanillin_butanediol_acetal` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1054 | 反式6-甲基-3-庚烯-2-酮 | T3 | `isoamylidene_acetone` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1055 | (±)3-巯基丁酸乙酯 | T3 | `ethyl_mercaptobutyrate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1056 | 3-巯基-2-甲基戊醇 | T3 | `mercapto_2_methylpentanol` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1057 | 乙醛二异戊醇缩醛 | T2 | `acetaldehyde_diisoamyl_acetal` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1058 | (+/-)-2-苯基-4-甲基-2-己烯醛 | T3 | `phenyl_methylhexenal` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1059 | 2-庚硫醇 | T3 | `sec_heptyl_mercaptan` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1060 | 2-(2-羟基-4-甲基-3-环己烯基)-丙酸γ-内酯 | T3 | `wine_lactone` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1061 | l-䓝基甲基醚 | T3 | `menthyl_methyl_ether` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1062 | 己酸异丙酯 | T2（S0003 为 T3） | `isopropyl_hexanoate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1063 | 2,4-己二烯-1-醇 | T3 | `sorbyl_alcohol` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1064 | 十六烷酸甲酯 | T2 | `methyl_palmitate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1065 | 5-甲基-2-噻吩甲醛 | T3 | `methylthiophene_carboxaldehyde` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1066 | 4-甲基-2,6-二甲氧基苯酚 | T3 | `methylsyringol` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1067 | 对-甲氧基肉桂醛 | T3 | `p_methoxycinnamaldehyde` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1068 | 2,4,5-三甲基噁唑 | T3 | `trimethyloxazole` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1069 | 苯甲醛二乙缩醛 | T2 | `benzaldehyde_diethyl_acetal` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1070 | d-新薄荷醇 | T3 | `d_neomenthol` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1071 | 2-壬烯酸γ-内酯 | T3 | `pentyl_furanone` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1072 | 反式-4-癸烯酸乙酯 | T2（S1491 为 T3） | `ethyl_trans_4_decenoate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1073 | 晚香玉内酯{二氢-5-[(Z,Z)-2,5-辛二烯-2(3H)-呋喃酮} | T4 | `tuberose_lactone` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1074 | 4-甲基-2-戊基-1,3-二氧戊环 | T2（S0130 为 T3） | `hexanal_pg_acetal` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1075 | 乙酸3-巯基庚酯 | T3 | `mercaptoheptyl_acetate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1076 | 甲基纤维素 | — | `methylcellulose` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S1077 | 植醇(3,7,11,15-四甲基-2-十六烯-1-醇) | T2 | `phytol` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1078 | 异戊醛二乙缩醛 | T2（S0124 为 T3） | `isovaleraldehyde_diethyl_acetal` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1079 | 异硫氰酸3-丁烯酯 | T2 / T3 | `butenyl_isothiocyanate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1080 | 异硫氰酸4-戊烯酯 | T2 / T3 | `pentenyl_isothiocyanate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1081 | 异硫氰酸5-己烯酯 | T2 / T3 | `hexenyl_isothiocyanate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1082 | 顺式-9-十八烯醇乙酸酯（乙酸油醇酯） | T3 | `oleyl_acetate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1083 | 糠基甲基醚 | T3 | `furfuryl_methyl_ether` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1084 | 3-己酮 | T3 | `ethyl_propyl_ketone` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1085 | 异硫氰酸2-丁酯 | T3 | `sec_butyl_isothiocyanate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1086 | 异硫氰酸异丁酯 | T3 | `isobutyl_isothiocyanate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1087 | 异硫氰酸6-(甲硫基)己酯 | T2 / T3（S1081 为 T3） | `methylthiohexyl_isothiocyanate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1088 | 异硫氰酸5-(甲硫基)戊酯 | T2 / T3（S1081 为 T3） | `methylthiopentyl_isothiocyanate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1089 | 异硫氰酸戊酯 | T3 | `amyl_isothiocyanate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1090 | 异硫氰酸异丙酯 | T3 | `isopropyl_isothiocyanate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1091 | 异硫氰酸异戊酯 | T3 | `isoamyl_isothiocyanate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1092 | 2,5-二甲基呋喃 | T3（`hydroxymethylfurfural` 为 T4） | `dimethylfuran` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1093 | 环紫罗兰酮 | T3 | `cycloionone` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1094 | 2-异丁基-4-甲基-1,3-二氧戊环 | T2（S0124 为 T3） | `isovaleraldehyde_pg_acetal` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1095 | 顺式和反式-2-异丙基-4-甲基-1,3-二氧戊环 | T2（S0184 为 T3） | `isobutyraldehyde_pg_acetal` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1096 | 4-氨基丁酸 | T4 | `gamma_aminobutyric_acid` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1097 | N-[2-(3,4-二甲氧基苯基)乙基]-3,4-二甲氧基肉桂酸酰胺 | T3 | `dimethoxycinnamoyl_homoveratrylamine` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1098 | 二-(1-丙烯基)硫醚(异构体混合物) | T3 | `dipropenyl_sulfide` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1099 | 乙酸2-戊酯 | T2（S0007 为 T3） | `sec_amyl_acetate` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1100 | 乙胺 | T3 | `ethylamine` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1101 | 2,8-二硫杂-4-壬烯-4-甲醛[5-(甲硫基)-2-(甲硫基甲基)-2-戊烯醛] | T3 | `methialdol` | [additives/19 §1.8](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1102 | 1-丁烯-1-基甲基硫醚 | T3 | `butenyl_methyl_sulfide` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1103 | 二异丙基二硫醚 | T3 | `diisopropyl_disulfide` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1104 | (E)-2-癸烯酸 | T3 | `e2_decenoic_acid` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1105 | l-苧烯 | T2 | `l_limonene` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1106 | 正己硫醇 | T3 | `hexanethiol` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1107 | 2-癸酮 | T3 | `decan_2_one` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1108 | 二糠基醚 | T3 | `difurfuryl_ether` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1109 | 异丁酸乙基香兰素酯 | T3 | `ethyl_vanillin_isobutyrate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1110 | 8-罗勒烯醇乙酸酯 | T3 | `ocimenyl_acetate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1111 | 丁胺 | T3 | `butylamine` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1112 | 1-氨基-2-丙醇 | T3 | `isopropanolamine` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1113 | 反式-1,5-辛二烯-3-酮 | T3 | `octa_1_5_dien_3_one` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1114 | 2,5-二甲基-4-乙氧基-3(2H)-呋喃酮 | T3 | `ethoxy_dimethylfuranone` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1115 | 反式-2-顺式-4-顺式-7-十三碳三烯醛 | T2 | `tridecatrienal` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1116 | 反式-2-顺式-4-癸二烯酸甲酯 | T3 | `methyl_decadienoate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1117 | 2-(4-甲基-2-羟基苯基)-丙酸-γ-内酯 | T3 | `dimethyl_benzofuranone` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1118 | 丙酸顺式-5-辛烯酯 | T2（S0017 为 T3） | `z5_octenyl_propionate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1119 | 3-甲基-2-丁烯硫醇乙酸酯 | T3 | `prenyl_thioacetate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1120 | 1-吡咯啉 | T3 | `pyrroline` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1121 | 2,3,4-三甲基-3-戊醇 | T3 | `trimethyl_3_pentanol` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1122 | 二异丙基三硫醚 | T3 | `diisopropyl_trisulfide` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1123 | 2-丙酰基-1-吡咯啉 | T3 | `propionyl_pyrroline` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1124 | 3,6-二乙基-1,2,4,5-四硫杂环己烷与3,5-二乙基-1,2,4-三硫杂环戊烷的混合物 | T3 | `diethyl_tetrathiane_trithiolane` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1125 | 2,5-二羟基-1,4-二噻烷 | T3 | `dihydroxy_dithiane` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1126 | 3-己烯醛(反式/顺式混合物) | T3 | `hex_3_enal_mix` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1127 | 4-羟基-3,5-二甲氧基苯甲醛 | T3 | `syringaldehyde` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1128 | 2-十一烯-1-醇 | T3 | `undec_2_en_1_ol` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1129 | 2-(4-羟基苯基)-乙胺（酪胺） | T4 | `tyramine` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1130 | 4[(2-呋喃甲基)硫基]-2-戊酮 | T3 | `furfurylthio_pentanone` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1131 | 己酸甲硫基甲酯 | T3 | `methylthiomethyl_hexanoate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1132 | 2,6-二甲基-4-庚酮 | T3 | `diisobutyl_ketone` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1134 | 反式-3-己烯醇 | T3 | `trans_3_hexenol` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1135 | 甲酸松油酯 | T2（S0037 为 T3） | `terpinyl_formate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1136 | 脱氢圆柚酮 | T3 | `dehydronootkatone` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1137 | 己酸香叶酯 | T2 | `geranyl_hexanoate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1138 | 3-甲基己醛 | T3 | `methylhexanal` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1139 | (反式,反式)-2,4-壬二烯 | T3 | `nonadiene` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1140 | 1-辛烯 | — | `octene` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已有（核对引用） |
| S1141 | 2-甲基苯乙酮 | T3 | `o_methylacetophenone` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1142 | 1-乙基-2-甲酰基吡咯（茶吡咯） | T3 | `ethyl_formylpyrrole` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1143 | 2-(4-甲基-5-噻唑基)乙醇辛酸酯 | T2（S0727 为 T3） | `methylthiazolylethyl_octanoate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1144 | 2-乙基-6-甲基吡嗪 | T3 | `ethyl_6_methylpyrazine` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1145 | 对-丙基苯酚 | T3 | `p_propylphenol` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1146 | 3,5-二乙基-2-甲基吡嗪 | T3 | `diethyl_2_methylpyrazine` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1147 | 马鞭草烯酮 | T3 | `verbenone` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1148 | 4-戊烯醛 | T3 | `pent_4_enal` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1149 | 乙酰乙酸乙酯丙二醇缩酮 | T2（S0365 为 T3） | `ethyl_acetoacetate_pg_ketal` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1150 | 山梨酸甲酯 | T2 | `methyl_sorbate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1151 | 2,5-二乙基四氢呋喃 | T3 | `diethyltetrahydrofuran` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1152 | 脱氢薄荷呋喃内酯 | T3 | `dehydromenthofurolactone` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1153 | 乙酸桃金娘烯酯 | T3 | `myrtenyl_acetate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1154 | 2-(4-甲基-5-噻唑基)乙醇己酸酯 | T2（S0727 为 T3） | `methylthiazolylethyl_hexanoate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1155 | 2-(4-甲基-5-噻唑基)乙醇丁酸酯 | T2（S0727 为 T3） | `methylthiazolylethyl_butyrate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1156 | 吡咯 | T3 | `pyrrole` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1157 | S-烯丙基-L-半胱氨酸 | T3 | `s_allyl_cysteine` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1158 | 2-巯基-3-丁醇 | T3 | `mercaptobutanol` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1159 | 硫代香叶醇 | T3 | `thiogeraniol` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1160 | 蒎烷硫醇 | T3 | `pinanyl_mercaptan` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1161 | α-甲基-β-羟基丙基α-甲基-β-巯丙基硫醚 | T3 | `hydroxybutyl_mercaptobutyl_sulfide` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1163 | 柠檬醛二乙缩醛 | T2 | `citral_diethyl_acetal` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1164 | 3-丙烯基-6-乙氧基苯酚 | T3 | `propenyl_guaethol` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1165 | β-甲基紫罗兰酮 | T3 | `methyl_beta_ionone` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1166 | δ-甲基紫罗兰酮 | T3 | `methyl_delta_ionone` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1167 | 2,6-壬二烯醛二乙缩醛 | T2（S0150 为 T3） | `nonadienal_diethyl_acetal` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1168 | 9-十一烯醛 | T3 | `undec_9_enal` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1169 | 10-十一烯醛 | T3 | `undec_10_enal` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1170 | 十六醛（杨梅醛） | T3 | `ethyl_methylphenylglycidate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1172 | 兔耳草醛（仙客来醛） | T3 | `cyclamen_aldehyde` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1173 | 羟基香茅醛 | T3 | `hydroxycitronellal` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1174 | β-环高柠檬醛 | T3 | `beta_homocyclocitral` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1175 | l-薄荷酮甘油缩酮 | T2 | `l_menthone_glyceryl_ketal` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1176 | 4-甲硫基-4-甲基-2-戊酮 | T3 | `methylthio_methylpentanone` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1177 | 3-巯基-2-戊酮 | T3 | `mercapto_2_pentanone` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1178 | d,l-薄荷酮甘油缩酮 | T3 | `dl_menthone_glyceryl_ketal` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1179 | α-甲基紫罗兰酮 | T3 | `methyl_alpha_ionone` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1180 | α-异甲基紫罗兰酮 | T3 | `alpha_isomethylionone` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1181 | 烯丙基α-紫罗兰酮 | T3 | `allyl_alpha_ionone` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1182 | 6-甲基香豆素 | T3 | `methylcoumarin` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1183 | 2-巯基丙酸 | T3 | `thiolactic_acid` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1184 | 2-甲基-4-戊烯酸 | T3 | `methyl_4_pentenoic_acid` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1185 | 乙酸二甲基苄基原酯 | T3 | `dimethylbenzylcarbinyl_acetate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1186 | 环己基乙酸烯丙酯 | T3 | `allyl_cyclohexaneacetate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1187 | 乙酸玫瑰酯 | T3 | `rhodinyl_acetate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1188 | 3-(2-呋喃基)丙酸乙酯 | T3 | `ethyl_furylpropionate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1189 | 丙酸烯丙酯 | T2（`allyl_alcohol` 为 T3） | `allyl_propionate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1190 | 3-环己基丙酸烯丙酯 | T3 | `allyl_cyclohexylpropionate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1191 | 3-(2-呋喃基)丙酸异丁酯 | T3 | `isobutyl_furylpropionate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1192 | 硫代丙酸糠酯 | T3 | `furfuryl_thiopropionate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1193 | 丁酸二甲基苄基原酯 | T3 | `dimethylbenzylcarbinyl_butyrate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1194 | 环己基丁酸烯丙酯 | T3 | `allyl_cyclohexanebutyrate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1195 | 1,3-壬二醇乙酸酯(混合酯) | T3 | `nonanediol_acetate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1196 | 丁酸苏合香酯 | T2（S0032 为 T3） | `styralyl_butyrate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1197 | 乙酸柏木酯 | T3 | `cedryl_acetate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1198 | 异丁酸麦芽酚酯 | T3 | `maltol_isobutyrate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1199 | 2-甲基-4-戊烯酸乙酯 | T2（S1184 为 T3） | `ethyl_methyl_4_pentenoate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1200 | 乙酸四氢糠酯 | T3 | `tetrahydrofurfuryl_acetate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1201 | 庚炔羧酸甲酯 | T3 | `methyl_octynoate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1202 | 辛炔羧酸甲酯 | T3 | `methyl_nonynoate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1203 | 癸二酸二乙酯 | T2（`sebacic_acid` 为 T3） | `diethyl_sebacate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1204 | 10-十一烯酸乙酯 | T2（S0336 为 T3） | `ethyl_undecenoate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1205 | 苯乙酸烯丙酯 | T2（`allyl_alcohol`、S0316 为 T3） | `allyl_phenylacetate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1206 | 三乙酸甘油酯 | — | 见 583 | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 见 583 |
| S1207 | 苯乙酸香叶酯 | T2（S0316 为 T3） | `geranyl_phenylacetate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1208 | 苯乙酸对-甲酚酯 | T3 | `p_cresyl_phenylacetate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1209 | 4-苯基丁酸甲酯（苯丁酸甲酯） | T2（`phenylbutyric_acid` 为 T3） | `methyl_phenylbutyrate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1210 | 4-苯基丁酸乙酯（苯丁酸乙酯） | T2（`phenylbutyric_acid` 为 T3） | `ethyl_phenylbutyrate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1211 | 肉桂酸烯丙酯 | T2（`allyl_alcohol`、S0318 为 T3） | `allyl_cinnamate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1212 | 2-甲基-3-戊烯酸乙酯 | T2（S0876 为 T3） | `ethyl_methyl_3_pentenoate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1213 | 亚硝酸乙酯 | T2 | `ethyl_nitrite` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1214 | 庚酸戊酯 | T2（S0006、S0308 为 T3） | `amyl_heptanoate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1215 | 3-乙酰基-2,5-二甲基呋喃 | T3（S1092 为 T4） | `acetyl_dimethylfuran` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1216 | 2,5-二甲基-3-氧代(2H)-4-呋喃丁酸酯 | T3 | `furaneol_butyrate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1217 | 2-甲氧基-3(5或6)-异丙基吡嗪 | T3 | `methoxy_isopropylpyrazine` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1218 | 2-甲基-3(5或6)-糠硫基吡嗪 | T3 | `methyl_furfurylthio_pyrazine` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1219 | 2-甲基(或乙基)-3(5或6)-甲氧基吡嗪 | T3 | `alkyl_methoxypyrazine_mix` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1220 | 2,5-二甲基-2,5-二羟基-1,4-二硫代环己烷 | T3 | `dimethyl_dihydroxy_dithiane` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1221 | 5,7-二氢-2-甲基噻吩并(3,4-d)嘧啶 | T3 | `methyl_dihydrothienopyrimidine` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1222 | 2-乙氧基噻唑 | T3 | `ethoxythiazole` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1223 | 2,4-二甲基-5-乙酰基噻唑 | T3 | `acetyl_dimethylthiazole` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1224 | 乙酸异丁香酯 | T3 | `isoeugenyl_acetate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1225 | 3-甲基丁酸对-甲酚酯 | T3 | `p_cresyl_isovalerate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1226 | l-薄荷醇乙二醇碳酸酯 | T3 | `menthyl_ethylene_glycol_carbonate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1227 | 3-(2-甲基丙基)吡啶 | T3 | `isobutylpyridine_3` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1228 | 乙基香兰素1,2-丙二醇缩醛 | T2 | `ethyl_vanillin_pg_acetal` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1229 | 人造康乃克油 | T2（S0468 为 T3） | `artificial_cognac_oil` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1230 | 山楂核烟熏香味料Ⅰ号 | T1 | `hawthorn_kernel_smoke_flavoring_1` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1231 | 山楂核烟熏香味料Ⅱ号 | T1 | `hawthorn_kernel_smoke_flavoring_2` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1232 | 苄基异丁基原醇 | T3 | `isobutylbenzylcarbinol` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1233 | 4-苯基-3-丁烯-2-醇 | T3 | `phenylbutenol` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1234 | 2-甲基-4-苯基-2-丁醇 | T3 | `methyl_phenyl_2_butanol` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1235 | l-薄荷醇丙二醇碳酸酯 | T3 | `menthyl_propylene_glycol_carbonate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1236 | 辛酸烯丙酯 | T2（`allyl_alcohol` 为 T3） | `allyl_octanoate` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1237 | α-丙基苯乙醇 | T3 | `propylphenethyl_alcohol` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1238 | 龙葵醇 | T3 | `hydratropyl_alcohol` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1239 | 四氢芳樟醇 | T3 | `tetrahydrolinalool` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1240 | 2,3-二巯基丁烷 | T3 | `butane_2_3_dithiol` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1241 | β-萘乙醚 | T3 | `naphthyl_ethyl_ether` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1242 | 异丁基β-萘醚 | T3 | `naphthyl_isobutyl_ether` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1243 | 邻-丙基苯酚 | T3 | `o_propylphenol` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1244 | 苄基异丁香酚 | T3 | `isoeugenyl_benzyl_ether` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1245 | 2-甲基-3(5或6)-甲硫基吡嗪 | T3 | `methyl_methylthio_pyrazine` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1246 | 香茅氧基乙醛 | T3 | `citronellyloxyacetaldehyde` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1247 | 乙醛苯乙醇丙醇缩醛 | T2（S0034 为 T3） | `acetaldehyde_phenethyl_propyl_acetal` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1248 | 2-甲基-3-(对甲基苯基)丙醛 | T3 | `satinaldehyde` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1249 | 2-苯基-3-(2-呋喃基)丙-2-烯醛 | T3 | `phenyl_furyl_propenal` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1250 | 3,5,5-三甲基己醛 | T3 | `trimethylhexanal` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1251 | 2-甲基-3(5或6)-乙氧基吡嗪 | T3 | `ethoxy_methylpyrazine` | [additives/19 §1.9](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1252 | 庚醛甘油缩醛 | T2（S0136 为 T3） | `heptanal_glyceryl_acetal` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1253 | 苯乙醛甘油缩醛 | T2（S0168 为 T3） | `phenylacetaldehyde_glyceryl_acetal` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1254 | 对-异丙基苯乙醛 | T3 | `p_isopropylphenylacetaldehyde` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1255 | 2-甲基-4-苯丁醛 | T3 | `methyl_4_phenylbutanal` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1256 | 龙葵醛 | T3 | `hydratropic_aldehyde` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1257 | 龙葵醛二甲缩醛 | T2（S1256 为 T3） | `hydratropic_aldehyde_dimethyl_acetal` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1258 | 羟基香茅醛二乙缩醛 | T2（S1173 为 T3） | `hydroxycitronellal_diethyl_acetal` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1259 | 柠檬醛二甲缩醛 | T2 | `citral_dimethyl_acetal` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1260 | 4-甲基-5-(2-乙酰氧乙基)-噻唑 | T3 | `methyl_acetoxyethyl_thiazole` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1261 | α-丁基肉桂醛 | T3 | `alpha_butylcinnamaldehyde` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1262 | 4-庚烯-3-酮 | T3 | `hept_4_en_3_one` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1263 | 4-甲基-1-苯基-2-戊酮 | T3 | `benzyl_isobutyl_ketone` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1264 | 1-(对-甲氧基苯基)-1-戊烯-3-酮 | T3 | `p_methoxyphenyl_pentenone` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1265 | α-己叉基环戊酮 | T3 | `alpha_hexylidenecyclopentanone` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1266 | 四甲基乙基环己烯酮 | T3 | `tetramethyl_ethylcyclohexenone` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1267 | 糠硫醇甲酸酯 | T3 | `furfurylthiol_formate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1268 | 甲基β-萘酮 | T3 | `methyl_beta_naphthyl_ketone` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1269 | 2-(3-苯丙基)四氢呋喃 | T3 | `phenylpropyl_tetrahydrofuran` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1270 | 烯丙基乙酸 | T3 | `allylacetic_acid` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1271 | 甲酸二甲基苄基原酯 | T2（S0048 为 T3） | `dimethylbenzylcarbinyl_formate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1272 | 4-乙酰基-6-叔丁基-1,1-二甲基茚满 | T3 | `acetyl_butyl_dimethylindane` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1273 | 癸醛二甲缩醛 | T2（S0152 为 T3） | `decanal_dimethyl_acetal` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1274 | 乙酸环己基乙酯 | T3 | `cyclohexaneethyl_acetate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1275 | 对-甲苯氧基乙酸乙酯 | T3 | `ethyl_p_tolyloxyacetate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1276 | 乙酸二甲基苯乙基原酯 | T3 | `dimethylphenethylcarbinyl_acetate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1277 | 丙酸甲基苯基原酯 | T2（S0032 为 T3） | `methylphenylcarbinyl_propionate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1278 | 2-呋喃基丙烯酸丙酯 | T2（`furylacrylic_acid` 为 T3） | `propyl_furanacrylate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1279 | 异丁酸二甲基苯乙基原酯 | T3 | `dimethylphenethylcarbinyl_isobutyrate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1280 | 异丁酸2-苯氧基乙酯 | T2（`phenoxyethanol` 为 T3） | `phenoxyethyl_isobutyrate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1281 | 十三碳二酸环乙二醇二酯 | T3 | `ethylene_brassylate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1282 | 邻氨基苯甲酸异丁酯 | T3 | `isobutyl_anthranilate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1283 | 对-叔丁基苯乙酸甲酯 | T3 | `methyl_tert_butylphenylacetate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1284 | 苯氧乙酸烯丙酯 | T2（`allyl_alcohol`、`phenoxyacetic_acid` 为 T3） | `allyl_phenoxyacetate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1285 | 苯乙酸辛酯 | T2（S0014、S0316 为 T3） | `octyl_phenylacetate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1286 | 苯乙酸苄酯 | T2（S0033、S0316 为 T3） | `benzyl_phenylacetate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1287 | 苯乙酸芳樟酯 | T3 | `linalyl_phenylacetate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1288 | 苯乙酸香茅酯 | T2（S0040、S0316 为 T3） | `citronellyl_phenylacetate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1289 | 苯乙酸愈创木酚酯 | T3 | `guaiacyl_phenylacetate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1290 | 3-甲基2-丁烯酸2-苯乙酯（千里酸苯乙酯） | T2（S0034、S0345 为 T3） | `phenethyl_senecioate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1291 | 3-苯基缩水甘油酸乙酯 | T3 | `ethyl_phenylglycidate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1292 | 肉桂酸芳樟酯 | T3 | `linalyl_cinnamate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1293 | 1,2-二[(1'-乙氧基)-乙氧基]丙烷 | T3 | `diethoxyethoxy_propane` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1294 | N,2,3-三甲基-2-异丙基丁酰胺 | T3 | `isopropyl_trimethylbutyramide` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1295 | N-乙基-2-异丙基-5-甲基-环己烷甲酰胺 | T3 | `ethyl_menthanecarboxamide` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1296 | 3-l-䓝氧基-1,2-丙二醇 | T3 | `menthoxypropanediol` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1297 | 香兰基丁醚 | T3 | `vanillyl_butyl_ether` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1298 | 9-癸烯醛 | T3（S0334 为 T4） | `dec_9_enal` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1299 | 2-仲丁基环己酮 | T3 | `sec_butylcyclohexanone` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1300 | 2,3-十一碳二酮 | T3 | `undecane_2_3_dione` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1301 | 环己烷基甲酸 | T3 | `cyclohexanecarboxylic_acid` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1302 | 5和6-癸烯酸（牛奶内酯） | T3 | `milk_lactone` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1303 | 八乙酸蔗糖酯 | T3 | `sucrose_octaacetate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1304 | 丁酸烯丙酯 | T2（`allyl_alcohol` 为 T3） | `allyl_butyrate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1305 | 异丁酸香兰素酯 | T3 | `vanillin_isobutyrate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1306 | 戊二酸单l-薄荷醇酯 | T3 | `monomenthyl_glutarate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1307 | 苯甲酰基乙酸乙酯 | T3 | `ethyl_benzoylacetate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1308 | ε-十二内酯 | T3 | `epsilon_dodecalactone` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1309 | 八氢香豆素 | T3 | `octahydrocoumarin` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1310 | 2,5-二甲基-3-呋喃硫醇 | T3 | `dimethyl_3_furanthiol` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1311 | 1,2-丁二硫醇 | T3 | `butane_1_2_dithiol` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1312 | 双-(2,5-二甲基-3-呋喃基)二硫醚 | T3 | `bis_dimethylfuryl_disulfide` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1313 | 丙基2-甲基-3-呋喃基二硫醚 | T3（S0471 为 T4） | `propyl_methylfuryl_disulfide` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1314 | 二环己基二硫醚 | T3 | `dicyclohexyl_disulfide` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1315 | 糠基异丙基硫醚 | T3 | `furfuryl_isopropyl_sulfide` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1316 | 2-乙基苯硫酚 | T3 | `o_ethylthiophenol` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1317 | 2-(乙酰氧基)丙酸甲硫醇酯 | T3 | `methylthio_acetoxypropionate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1318 | 2-(丙酰氧基)丙酸甲硫醇酯 | T3 | `methylthio_propionyloxypropionate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1319 | 3-糠硫基丙酸乙酯 | T3 | `ethyl_furfurylthiopropionate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1320 | 2-甲硫基吡嗪 | T3 | `methylthiopyrazine` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1321 | 异硫氰酸苯乙酯 | T3 | `phenethyl_isothiocyanate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1322 | 2-(3-苯丙基)吡啶 | T3 | `phenylpropyl_pyridine` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1323 | 4,5-二甲基-2-乙基-3-噻唑啉 | T3 | `ethyl_dimethyl_thiazoline` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1324 | 2-仲丁基-4,5-二甲基-3-噻唑啉 | T3 | `sec_butyl_dimethyl_thiazoline` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1325 | 吡嗪乙硫醇 | T3 | `pyrazine_ethanethiol` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1326 | 水杨酸苯酯 | T3 | `phenyl_salicylate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1327 | 庚醛二甲缩醛 | T2（S0136 为 T3） | `heptanal_dimethyl_acetal` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1328 | 羟基香茅醛二甲缩醛 | T2（S1173 为 T3） | `hydroxycitronellal_dimethyl_acetal` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1329 | 对-丙基茴香醚 | T3 | `p_propylanisole` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1330 | 异丁酸对-甲酚酯 | T3 | `p_tolyl_isobutyrate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1331 | 异丁酸邻-甲酚酯 | T3 | `o_tolyl_isobutyrate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1332 | 柠檬醛丙二醇缩醛 | T2 | `citral_propylene_glycol_acetal` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1333 | 反式-2-己烯醛二乙缩醛 | T2（S0131 为 T3） | `trans_2_hexenal_diethyl_acetal` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1334 | 2-巯基噻吩 | T3 | `mercaptothiophene` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1335 | 对-䓝-3,8-二醇 | T3 | `p_menthane_3_8_diol` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1336 | 1,8-辛二硫醇 | T3 | `octane_1_8_dithiol` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1337 | 螺[2,4-二硫杂-1-甲基-8-氧杂双环[3.3.0]-辛烷-3,3’-(1’-氧杂-2’-甲基)环戊烷] | T3 | `spiro_dithia_oxabicyclooctane` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1338 | 3-壬烯-2-酮 | T3 | `non_3_en_2_one` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1339 | 3-甲基-2,4-壬二酮 | T3 | `methyl_2_4_nonanedione` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1340 | 2,5-二甲基-3-硫代乙酰氧基呋喃 | T3 | `dimethyl_thioacetoxyfuran` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1341 | 反式-4-己烯醛 | T3 | `trans_4_hexenal` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1342 | 3-[(2-甲基-3-呋喃)硫基]-2-丁酮 | T3（S0471 为 T4） | `methylfurylthio_butanone` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1343 | 3-巯基-2-甲基戊醛 | T3 | `mercapto_methylpentanal` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1344 | 2-(l-䓝氧基)乙醇 | T3 | `menthoxyethanol` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1345 | 丙酸四氢糠酯 | T2（S0796 为 T3） | `tetrahydrofurfuryl_propionate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1346 | 异戊酸烯丙酯 | T2（`allyl_alcohol`、S0303 为 T3） | `allyl_isovalerate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1347 | 3-羟甲基-2-庚酮 | T3 | `hydroxymethyl_heptanone` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1348 | 三丙酸甘油酯 | T2 | `glyceryl_tripropanoate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1349 | 辛酸糠酯 | T3 | `furfuryl_octanoate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1350 | 丁酸反式-2-辛烯醇酯 | T2（S0866 为 T3） | `trans_2_octenyl_butanoate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1351 | 苯乙醛二异丁缩醛 | T2（S0168 为 T3） | `phenylacetaldehyde_diisobutyl_acetal` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1352 | 1,3-二苯基-2-丙酮 | T3 | `dibenzyl_ketone` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1353 | 10-十一烯酸丁酯 | T2（S0004、S0336 为 T3） | `butyl_undecylenate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1354 | 乙酸檀香酯 | T3 | `santalyl_acetate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1355 | 2-乙基丁酸香叶酯 | T2（S0299 为 T3） | `geranyl_ethylbutyrate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1356 | 3-羟甲基-2-辛酮 | T3 | `hydroxymethyl_octanone` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1357 | 1,2-环己二酮 | T3 | `cyclohexane_1_2_dione` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1359 | 赤、苏-3-巯基-2-甲基丁-1-醇 | T3 | `mercapto_2_methylbutanol` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1360 | 4-甲基联苯 | T3 | `p_methylbiphenyl` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1361 | α-戊基肉桂醇 | T3 | `alpha_amylcinnamyl_alcohol` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1362 | 1-苯基-3-甲基-戊醇-3 | T3 | `phenyl_3_methyl_3_pentanol` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1363 | 5-苯基戊醇 | T3 | `phenylpentanol` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1364 | 对-䓝烷醇-2 | T3 | `p_menthan_2_ol` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1365 | 脱氢二氢紫罗兰醇 | T3 | `dehydrodihydroionol` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1366 | 乙基葑醇 | T3 | `ethylfenchol` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1367 | 辛烯基琥珀酸单阿拉伯胶酯 | T3 | `gum_arabic_octenylsuccinate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1368 | N1-(2-甲氧基-4-甲基苄基)-N2-[2-(5-甲基-2-吡啶基)乙基]草酰胺 | T3 | `methoxymethylbenzyl_oxalamide` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1369 | N1-(2,4-二甲氧基苄基)-N2-[2-(2-吡啶基)乙基]草酰胺 | T3 | `dimethoxybenzyl_oxalamide` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1370 | N-(4-庚基)-(3,4-亚甲二氧基)苯甲酰胺 | T3 | `heptyl_piperonylamide` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1371 | 二苄醚 | T3 | `dibenzyl_ether` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1372 | 5-羟基-十二酸甘油酯 | T3 | `glyceryl_hydroxydodecanoate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1373 | 三丁酸甘油酯 | T2 | `tributyrin` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1374 | 壬酸烯丙酯 | T2（`allyl_alcohol`、S0310 为 T3） | `allyl_nonanoate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1375 | 5-羟基癸酸甘油酯 | T3 | `glyceryl_hydroxydecanoate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1376 | 丙酸3-苯基丙酯 | T2（S0035 为 T3） | `phenylpropyl_propionate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1377 | 肉桂酸异丙酯 | T2（S0003、S0318 为 T3） | `isopropyl_cinnamate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1378 | 2-酮基-4-丁硫醇 | T3 | `keto_butanethiol` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1379 | 甲基-对-甲苯缩水甘油酸乙酯 | T3 | `ethyl_methyl_tolylglycidate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1380 | 5-羟基-8-十一碳烯酸δ-内酯 | T3 | `delta_undec_8_enolactone` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1381 | N-环丙基-反式-2-顺式-6-壬二烯酰胺 | T3 | `cyclopropyl_nonadienamide` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1382 | N-乙基-反式-2-顺式-6-壬二烯酰胺 | T3 | `ethyl_nonadienamide` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1383 | 2,4-二甲基-1,3-二氧戊环 | T2 | `dimethyl_1_3_dioxolane` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1384 | β-萘甲醚 | T3 | `beta_naphthyl_methyl_ether` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1385 | 二羟基丙酮 | T4 | `dihydroxyacetone` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1386 | 二苯基二硫醚 | T3 | `diphenyl_disulfide` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1387 | 乙基香芹酚 | T3 | `ethyl_carvacrol` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1388 | 甲基苯甲醛甘油缩醛(邻、间、对异构体混合物) | T2（S0166 为 T3） | `tolualdehyde_glyceryl_acetal` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1389 | (+/-)-反式和顺式-4,8-二甲基-3,7-壬二烯-2-醇 | T3 | `dimethyl_nonadienol` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1390 | (+/-)-反式和顺式-4,8-二甲基-3,7-壬二烯-2-醇乙酸酯 | T3 | `dimethyl_nonadienyl_acetate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1391 | (反式和顺式)-1-甲氧基-1-癸烯 | T3 | `methoxy_1_decene` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1392 | 2-(4-甲基-5-噻唑基)乙醇癸酸酯 | T2（S0727 为 T3） | `methylthiazolylethyl_decanoate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1393 | 2-(4-甲基-5-噻唑基)乙醇异丁酸酯 | T2（S0727 为 T3） | `methylthiazolylethyl_isobutyrate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1394 | 2-(4-甲基-5-噻唑基)乙醇甲酸酯 | T2（S0727 为 T3） | `methylthiazolylethyl_formate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1395 | 异戊酸3-苯丙酯 | T2（S0035、S0303 为 T3） | `phenylpropyl_isovalerate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1396 | dl-薄荷脑(+/-)-1,2-丙二醇碳酸酯 | T3 | `dl_menthyl_propylene_glycol_carbonate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1397 | 乙酸1-乙氧基乙醇酯 | T3 | `ethoxyethyl_acetate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1398 | N-异丁基-反-2-反-4-癸二烯酸酰胺 | T3 | `isobutyl_decadienamide` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1399 | 二苯乙醇酮 | T3 | `benzoin` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1400 | 甲基异戊基二硫醚 | T3 | `methyl_isopentyl_disulfide` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1401 | 邻氨基苯甲酸烯丙酯 | T3 | `allyl_anthranilate` | [additives/19 §1.10](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1402 | 6-环己基己酸烯丙酯 | T2（`allyl_alcohol`、`cyclohexylhexanoic_acid` 为 T3） | `allyl_cyclohexanehexanoate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1403 | 5-环己基戊酸烯丙酯 | T2（`allyl_alcohol`、`cyclohexylvaleric_acid` 为 T3） | `allyl_cyclohexanevalerate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1404 | 2-乙基丁酸烯丙酯 | T2（`allyl_alcohol`、S0299 为 T3） | `allyl_2_ethylbutyrate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1405 | 惕各酸烯丙酯 | T2（`allyl_alcohol`、S0350 为 T3） | `allyl_tiglate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1406 | 10-十一烯酸烯丙酯 | T2（`allyl_alcohol`、S0336 为 T3） | `allyl_10_undecenoate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1407 | α-戊基肉桂醛二甲缩醛 | T2（S0273 为 T3） | `amylcinnamaldehyde_dimethyl_acetal` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1408 | 乙酸α-戊基肉桂酯 | T3 | `amylcinnamyl_acetate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1409 | 甲酸α-戊基肉桂酯 | T2（S1361 为 T3） | `amylcinnamyl_formate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1410 | 异戊酸α-戊基肉桂酯 | T2（S0303、S1361 为 T3） | `amylcinnamyl_isovalerate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1411 | 4(2-呋喃基)丁酸异戊酯 | T2（`furylbutyric_acid` 为 T3） | `isoamyl_furylbutyrate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1412 | 3(2-呋喃基)丙酸异戊酯 | T2（`furylpropionic_acid` 为 T3） | `isoamyl_furylpropionate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1413 | 2-戊基-5或6-酮-1,4-二噁烷 | T3 | `amyl_keto_dioxane` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1414 | 丙酮酸异戊酯 | T2（S0295 为 T3） | `isoamyl_pyruvate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1415 | 苄基丁基醚 | T3 | `benzyl_butyl_ether` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1416 | N-3,7-二甲基-2,6-辛二烯-环丙基甲酰胺 | T3 | `geranyl_cyclopropanecarboxamide` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1417 | N-(乙氧羰基甲基)-对-䓝烷-3-甲酰胺 | T3 | `ethoxycarbonylmethyl_menthanecarboxamide` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1418 | 硬木烟熏香味料 SmokEz C-10 | T1 | `smokez_c_10` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1419 | 硬木烟熏香味料 SEF 7525 | T2 | `scansmoke_sef_7525` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1420 | (反式,顺式)-2,6-壬二烯-1-醇乙酸酯 | T3 | `nonadienyl_acetate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1421 | 邻氨基苯甲酸苯乙酯 | T3 | `phenylethyl_anthranilate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1422 | 2-丙酰基-2-噻唑啉 | T3 | `propionyl_thiazoline` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1423 | 顺式-8-十四烯醛 | T3 | `cis_8_tetradecenal` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1424 | 烯丙硫醇己酸酯 | T3 | `allyl_thiohexanoate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1425 | 双香兰素 | T3 / T4 | `divanillin` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1426 | 顺式和反式-2-庚基环丙烷羧酸 | T3 | `heptylcyclopropanecarboxylic_acid` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1427 | 5-羟基-4-甲基己酸δ-内酯 | T3 | `hydroxy_methylhexanoic_delta_lactone` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1428 | 4-巯基-2-戊酮 | T3 | `mercapto_2_pentanone_4` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1429 | 2,4,6-三硫杂庚烷 | T3 | `trithiaheptane` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1430 | 1-(4-甲氧苯基)-4-甲基-1-戊烯-3-酮 | T3 | `methoxyphenyl_methylpentenone` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1431 | 3(2)-羟基-5-甲基-2(3)-己酮 | T3 | `hydroxy_methylhexanone` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1432 | 二巯基甲烷 | T3 | `dimercaptomethane` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1433 | 4-羟基-2-丁烯酸γ-内酯 | T3 | `butenolide` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1434 | (+/-)-3-甲硫基丁酸异丁酯 | T3 | `isobutyl_methylthiobutyrate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1435 | 3-甲硫基-2-丁酮 | T3 | `methylthio_2_butanone_3` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1436 | 顺式和反式-5-乙基-4-甲基-2-(2-甲基丙基)-噻唑啉 | T3 | `ethyl_methyl_isobutylthiazoline` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1437 | 1-戊硫醇 | T3 | `pentanethiol` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1438 | (+/-)-4-巯基-4-甲基-2-戊醇 | T3 | `mercapto_4_methyl_2_pentanol` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1439 | 异戊酸环己酯 | T2（S0303 为 T3） | `cyclohexyl_isovalerate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1440 | 2-噻吩基二硫醚 | T3 | `thienyl_disulfide` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1441 | 双(2-甲基-3-呋喃基)四硫醚 | T3（S0471 为 T4） | `bis_methylfuryl_tetrasulfide` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1442 | 辛酸对-甲酚酯 | T3 | `p_tolyl_octanoate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1443 | 丙酸麦芽酚酯 | T3 | `maltol_propionate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1444 | 顺式-2-己烯-1-醇 | T3 | `cis_2_hexenol` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1445 | (+/-)反式和顺式-2-己烯醛丙二醇缩醛 | T2（S0131 为 T3） | `hexenal_propylene_glycol_acetal` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1446 | 乙酸2-乙基丁酯 | T3 | `ethylbutyl_acetate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1447 | 2,5-二乙基-3-甲基吡嗪 | T3 | `diethyl_3_methylpyrazine_2_5` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1448 | 4-(甲硫基)-2-戊酮 | T3 | `methylthio_2_pentanone_4` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1449 | 甲硫基甲硫醇 | T3 | `methylthiomethyl_mercaptan` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1450 | 顺式和反式-5-乙基-4-甲基-2-(1-甲基丙基)-噻唑啉 | T3 | `ethyl_methyl_sec_butylthiazoline` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1451 | 辛醛二甲缩醛 | T2（S0141 为 T3） | `octanal_dimethyl_acetal` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1452 | 3-巯基-3-甲基-1-丁醇乙酸酯 | T3 | `mercapto_methylbutyl_acetate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1453 | (R,S)-3-羟基丁酸l-薄荷酯 | T3 | `menthyl_hydroxybutyrate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1454 | 异戊酸异丙酯 | T2（S0303 为 T3） | `isopropyl_isovalerate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1455 | 顺式-4-癸烯醇乙酸酯 | T3 | `cis_4_decenyl_acetate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1456 | 惕各酸香叶酯 | T3 | `geranyl_tiglate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1457 | N-苯甲酰邻氨基苯甲酸 | T3 | `benzoylanthranilic_acid` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1458 | 2,6,10-三甲基-2,6,10-十五碳三烯-14-酮 | T3 | `farnesylacetone` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1459 | 2,5-二甲基噻唑 | T3 | `dimethylthiazole_2_5` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1460 | 甲硫基甲醇丁酸酯 | T3 | `methylthiomethyl_butyrate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1461 | 2-甲硫基乙醇 | T3 | `methylthioethanol` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1462 | 二乙基三硫醚 | T3 | `diethyl_trisulfide` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1463 | 顺式和反式-1-巯基-对-䓝烷-3-酮 | T3 | `mercapto_p_menthan_3_one_1` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1464 | 4-羟基-4-甲基-7-顺式-癸烯酸γ-内酯 | T3 | `hydroxy_methyldecenoic_gamma_lactone` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1465 | 2-甲基辛醛 | T3 | `methyloctanal_2` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1466 | 3-甲基-5-丙基-2-环己烯-1-酮 | T3 | `methyl_propylcyclohexenone` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1467 | 2,4-壬二烯-1-醇 | T3 | `nonadienol_2_4` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1468 | 环戊硫醇 | T3 | `cyclopentanethiol` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1469 | N-对苯乙腈基薄荷烷基甲酰胺 | T3 | `benzeneacetonitrile_menthanecarboxamide` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1470 | N-[2-(吡啶-2-基)乙基]薄荷烷基甲酰胺 | T3 | `pyridinylethyl_menthanecarboxamide` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1471 | 4-氨基-5,6-二甲基噻吩并[2,3-d]嘧啶-2(1H)-酮盐酸盐 | T4 | `aminodimethylthienopyrimidinone_hcl` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 暂缓（11 问题 36） |
| S1472 | 3-[(4-氨基-2,2-二氧-1H-2,1,3-苯并噻二嗪-5-基)氧]-2,2-二甲基-N-丙基丙酰胺 | T4 | `aminobenzothiadiazinyloxy_propylamide` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 暂缓（11 问题 36） |
| S1473 | L-蛋氨酰基甘氨酸 | T4 | `methionylglycine` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1474 | 5-戊基-3H-呋喃-2-酮 | T3 | `pentyl_3h_furanone` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1475 | 2,5-二硫杂己烷 | T3 | `dithiahexane` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1476 | (2S,5R)-N-[4-(2-氨基-2-氧代乙基)苯基]-5-甲基-2-(丙基-2-)环己烷甲酰胺 | T4 | `aminooxoethylphenyl_menthanecarboxamide` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1477 | 5-甲基-2-呋喃甲硫醇 | T3 | `methylfurfuryl_mercaptan` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1478 | 6-甲基辛醛 | T3 | `methyloctanal_6` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1479 | 2(4)-乙基-4(2),6-二甲基二氢-1,3,5-二噻嗪 | T3 | `ethyl_dimethyldihydrodithiazine` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1480 | 3-庚基二氢-5-甲基-2(3H)-呋喃酮 | T3 | `heptyl_methyl_dihydrofuranone` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1481 | 香兰醇 | T3 | `vanillyl_alcohol` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1482 | 6-[5(6)-癸烯酰氧基]癸酸 | T3 | `decenoyloxydecanoic_acid` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1484 | 3-{1-[(3,5-二甲基-1,2-噁唑-4-基)甲基]-1H-吡唑-4-基}-1-(3-羟基苄基)咪唑啉-2,4-二酮 | T4 | `isoxazolylmethyl_pyrazolyl_hydantoin` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 暂缓（11 问题 36） |
| S1485 | 4-氨基-5-[3-(异丙基氨基)-2,2-二甲基-3-氧代丙氧基]-2-甲基喹啉-3-羧酸硫酸盐 | T4 | `aminoquinolinecarboxylic_acid_sulfate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 暂缓（11 问题 36） |
| S1486 | 9-癸烯-2-酮 | T3 | `decen_2_one_9` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1487 | 6-甲基庚醛 | T3 | `methylheptanal_6` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1488 | N-(2-异丙基-5-甲基环己基)环丙基甲酰胺 | T3 | `menthyl_cyclopropanecarboxamide` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1489 | 4-羟基-4-甲基-5-己烯酸γ-内酯 | T3 | `hydroxy_methylhexenoic_gamma_lactone` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1490 | 糠基2-甲基-3-呋喃基二硫醚 | T3（S0471 为 T4） | `furfuryl_methylfuryl_disulfide` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1491 | 4-癸烯酸 | T3 | `decenoic_acid_4` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1492 | 2-(4-甲基-5-噻唑基)乙醇丙酸酯 | T2（S0727 为 T3） | `methylthiazolylethyl_propionate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1493 | 4,5-辛二酮 | T3 | `octanedione_4_5` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1494 | 5-羟基癸酸乙酯 | T3 | `ethyl_hydroxydecanoate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1495 | 己二酸二辛酯 | T2（S0014 为 T3） | `dioctyl_adipate` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1496 | 乙基芳樟基醚 | T3 | `ethyl_linalyl_ether` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1497 | 大茴香脑 | T2 / T3 | `anethole` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成（原料暂无来源） |
| S1498 | 根皮素 | T2 | `phloretin` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1499 | 2-丙酰吡咯 | T3 | `propionylpyrrole` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1500 | 烯丙基1-丙烯基二硫醚 | T3 | `allyl_propenyl_disulfide` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1501 | 2-乙酰氧基-3-丁酮 | T3 | `acetoxybutanone` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1502 | (+/-)-1-环己基乙醇 | T3 | `cyclohexylethanol` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1503 | L-γ-谷氨酰-L-缬氨酰-甘氨酸 | T4 | `glutamyl_valyl_glycine` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1504 | (1R,2S,5R)-N-(4-甲氧苯基)-5-甲基-2-(1-甲基乙基)环己基甲酰胺 | T3 | `methoxyphenyl_menthanecarboxamide` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |
| S1505 | 2-(4-甲基苯氧基)-N-(1H-吡唑-3-基)-N-(噻吩-2-基甲基)乙酰胺 | T4 | `tolyloxy_pyrazolyl_thenyl_acetamide` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 暂缓（11 问题 36） |
| S1506 | 2-己基吡啶 | T3 | `hexylpyridine` | [additives/19 §1.11](../additives/19-batch-5-synthetic-flavors.md) | 已完成 |

## 2026-11-12 返工：配方唯一性（11 问题 41）

设计侧已按实装时提出的问题改定：**同一配方类型下模式、催化剂、条件都相同而输入物料集合（按物料种类、不看数量）相同即冲突，配比不是区分手段**；匹配改为全覆盖（非空输入槽必须全部被消耗），输入集合是真子集时并存。判据与区分手段见 [machines/01 §3.3](../machines/01-recipe-types.md)，逐条处理见 [additives/18 §1.5](../additives/18-batch-5-flavors.md)。

- [x] 框架：匹配改为全覆盖；I08 的数据生成检查增加一项，按（配方类型、`mode`、`catalyst`、`requirements`、输入物料集合）建索引，重复的报错。重复配方检查里“设备不同”的两组（熟石灰、罗汉果甜苷）照旧放过
- [x] 569 丁苯橡胶：两种配比不再写两条配方，只写一条（75/25），另一配比写在物料描述里
- [x] 580 聚乙烯蜡：链转移剂由氢气改为 `acetaldehyde`（[additives/17](../additives/17-batch-5.md) 的依据栏；氢气法为该专利列出的已有技术，作备选不实装）
- [x] 486 铁卟啉与 493 血红素铁合并为一个物料（`heme_iron`），两个编号都指向它，删去铁卟啉单独的配方与“不调酸、整液浓缩”的区分
- [x] 概率平板（[additives/18 §1.5](../additives/18-batch-5-flavors.md) 的“概率平板”表）：取消“同一块平板同时长出三种菌”的写法，改为同一宿主一块平板、产出按 `plate.weights` 抽取，玩家挑菌落：① 大肠杆菌工程菌（产半胱氨酸 181、产苏氨酸 N359、产酪氨酸 546、产色氨酸 547）；② 谷氨酸棒杆菌诱变株（534、535、S0344、S0326、S0339、S0343、S0884）；③ 枯草芽孢杆菌诱变株（S0077 产核糖、S0204 A 产乙偶姻，08 新增两个菌株标签）；④ 带供体样本的工程菌各自一块，产出为目标菌与宿主菌（失败）
- [x] 495 L-乳酸钙补钙盐法发酵路线：菌种用新增的产 L-乳酸乳杆菌菌株（与 114 的输入因此不同）
- [x] 按 18 §1.5 的“逐条处理”表改 20 条香料路线与 6 个中间体：S0018、S0023、S0032、S0062、S0074、S0077、S0204 A、S0224 B、S0693、S0810、S0814 B、S0815、S0930（删 B 路线）、S0996、S1017、S1233、S1429、S1502，中间体 `hydroxymenthofurolactone`、`p_toluidine`、`toluene_2_3_diamine`、`piperylene`、`isoprenol`、`dimethyldioxane`
- [x] 新增中间体三种（09 已登记）：`nitrotoluene`、`dinitrotoluene`、`nitrosyl_chloride`
- [x] 489 羰基铁粉、500 甘油磷酸钙的配方从已登记的中间体开始（17 已改写），其余 18 条“路线文字含中间体前段”的按同一规则核对
- [x] 联产：按 18 §1.5 的 30 组核对，确认每组只有一条配方（煤焦油、木焦油、松节油、杂醇油、各精油精馏、羰基合成的正异构醛、石蜡裂解的 α-烯烃、椰子油甲醇解、甲基紫罗兰酮与吡嗪异构体、甲乙酮与甲醛的区域异构等）
- [x] 第二轮核对的 14 组（[additives/18 §1.5](../additives/18-batch-5-flavors.md) 的“前几批与第二轮核对补上的”表）：
  - 前几批（文档已改，I18–I20、I30 的配方随之改）：51 按 [13](../additives/13-batch-2.md) 的混盐配料（磷酸氢二钠 + 磷酸二氢钠 2 : 1）实装；149 柠檬酸一钠改用碳酸氢钠；96 β-淀粉酶改为 402 麦芽浸提液加硫酸铵盐析分级；硫酸氢铵改为硫酸铵 + 硫酸；171 磷酸氢二铵改为 454 再氨化；1,3-二异丙苯改为异丙苯 + 丙烯；187 磷酸二氢钾改为磷酸 + 氯化钾（副产盐酸），186 改为 187 + 碳酸钾
  - 香料：S1364 改为 S0868 + 氢气；S0871 改为 S0795 + 氢气（氢解）；S1240 改为 S1158 + S1010；`trimethylphenol` 改为 S0113 + 甲醇；S0896 与 S1124 合为一条联产配方；S0869 与 S0267 合为一条联产配方（丙酮碱缩合同时出二聚物与三聚物）；N231 只做提取物（NT7，M04）
- [x] 条件不是可靠的区分手段（[machines/01 §3.3](../machines/01-recipe-types.md)）：复查实装里靠温度、压力区分的配方，输入相同的要改用催化剂或输入的差别；设备能力是集合（M14 加热与冷却、M23 加热与高温）
- [x] 全覆盖的两处说明：惰性气氛的氮气罐、燃料、电不算配方输入；产物槽里的存货不影响匹配（T0 器具的产物留在原槽）
- [x] 重跑三个节点的数据生成与单元测试，重复配方检查清零

## 2026-11-12 返工（二）：待审十条的审核（11 问题 42）

设计侧以还原现实为主审完下节原有的待审十条，用户逐条定下，文档已改（[additives/11](../additives/11-roadmap.md) 问题 42）。实装方的其余拟定照准：08 与 09 的改名和英文名、S1088 A 并入山葵蒸馏、液体烟熏香味料由木醋液沉降、手写路线的多步合并、S0052 加氢氧化钠、509 B 按含硒硫化矿焙烧、501 骨粉联产骨油、513 硫酸催化、橙皮苷与芦丁的设备、可选精制的方式。设计文档也已跟上实装检查补上的各组（13 第 33、40、49 行，14 第 127、132、137 行，15 第 190、196 行，17 第 435 行，18 N298，19 S0630、`alpha_cyclocitral`、`disulfur_dichloride`、ST11 四行），汇总在 [additives/18 §1.5](../additives/18-batch-5-flavors.md) 的“实装检查补上的”表。

- [x] 天然原料用量按 [additives/18 §1.4](../additives/18-batch-5-flavors.md) 的“原料用量”分档改写：精油取所在档（16 / 8 / 4 / 2 / 1，174 种原料逐个列出），浸膏、油树脂取半（最少 1），酊、提取物 2，香脂 1，干品粉末 4 或 2；取代“草本、花 8，果实、根 4”
- [x] 合成配方的输入数量按关键反应系数取整数，不再一律 1 单位；与方程式对不上的逐条改（数量仍不参与冲突判断）
- [x] 碱熔拆步：回转窑只出钠盐，水溶酸化另写 M14 配方。S0101：`sodium_p_ethylphenolate` → 盐酸酸化；I20 的 2-萘酚：`sodium_2_naphtholate` → 硫酸酸化（[additives/14](../additives/14-batch-3.md) 第 80 行）；I30 的间苯二酚：`disodium_resorcinolate` → 硫酸酸化（[additives/15](../additives/15-batch-4.md) 第 195 行）；ST38：M23 高温碱裂解只出 2-辛醇与 `disodium_sebacate`，`sebacic_acid` 另在 M14 硫酸酸化。四个中间体已登记在 09
- [x] 431 C 丙酮丁醇发酵：M05 密闭发酵出 `abe_broth`（第二流体输出为二氧化碳），M22 精馏按质量比 3 : 6 : 1 出丙酮、正丁醇、乙醇（每 1,000 mB 发酵液 60 / 120 / 20 mB）
- [x] 494 电解铁：配方只写铁 → 电解铁（M24 电解还原），去掉水；电解液不作输入（游戏简化）；氢气退火保持可选精制
- [x] 511 硒蛋白保持乙醇沉淀（GB 1903.28-2018 写的就是醇沉），实装不改
- [x] 513：酶法拆分配方加副产出 `acetyl_d_methylselenocysteine`，新增消旋配方（N-乙酰-D 体 + 少量乙酸酐，M14 加热 → `acetyl_methylselenocysteine`）；545 同样写（`acetyl_d_methionine` → `acetyl_dl_methionine`）
- [x] 528 B：重铬酸钠 + 盐酸 8 份 + 甲醇，M14（耐腐蚀内衬）还原，副产氯化钠，不再放出氯气，也不再超出储罐（[additives/17](../additives/17-batch-5.md) 第 528 行）
- [x] 轻石脑油裂解：`light_naphtha` + 水 → M27 裂解模式出 `naphtha_cracked_gas`；M22 深冷分离一条配方，每 1,000 mB 裂解气出乙烯 300、丙烯 150、碳四馏分 100、1,3-戊二烯 10 mB。删去丁烷加水蒸气裂解制碳四馏分的配方与 `piperylene` 单独的裂解配方；I19 的丁烷裂解制乙烯不动
- [x] 581 费托蜡：减压蒸馏除去的轻油产出 `light_naphtha`；加氢精制与活性炭渗滤保持可选精制，未精制的粗蜡达不到食品级
- [x] 糖蜜：`molasses` 改名 `cane_molasses`（甘蔗糖蜜）；新增流体标签 `foodindustrial:molasses`（甘蔗、甜菜糖蜜，[additives/08 §B](../additives/08-raw-materials.md)）供两者都可用的配方引用；N276 用 `cane_molasses`，N351 甜菜碱改用 `beet_molasses`，550 不变
- [x] 辣椒油树脂 `paprika_oleoresin` 改为不带纯度（K5 → —），I30 的 367、209 与 60 B 的引用随之核对
- [x] N267 焦木酸新增物料 `refined_wood_vinegar`：`wood_vinegar` + `catalysts/activated_carbon`，M14 一条配方，静置分出的 `wood_tar` 作副产出；N267 不再是“已有物料”
- [x] 颜色：09、08 写明的颜色优先，其次按 [additives/09](../additives/09-chemical-formulas.md) 表示约定后的“颜色的取值顺序”（天然香料按产品形态的默认色；外观鲜明的 16 种已写进各行），最后才用按类别、纯度的默认色
- [x] 重跑三个节点的数据生成、重复配方检查与单元测试

## 验收

- [x] 三个节点 `compileJava`、`runData`、`test` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 清单中每一项（阻塞除外）在“最早阶段”的设备上能做出，并能沿路线升级到最终阶段
- [x] 数据生成检查全部通过（时长为 4 的倍数、每条配方至少一台设备可执行、带纯度的产出写入纯度、不带纯度的物料不写纯度也不设门槛）
- [x] 不存在两条配方在同一设备、同一模式下输入完全相同而产出不同（香料模板批量生成后检查）
- [x] 09 第 E 节的物料全部导出，创造模式“香料”标签页可见

## 实现记录

- 物料：`tools/materials/export_materials.py` 读 09 第 E 节，分类“香料”进新的创造模式标签页“香料”（图标香兰素）；只被暂缓条目用到的物料不导出；08 第 C 节指向成品（`foods/`）的行不重复注册；状态为流体而颜色为空的物料按名称取默认色。占位贴图按 I05 生成
- 香料：`tools/flavors/export_flavors.py` 把 18 §2、19 §1 的路线与 19 §2 只有一个模板的中间体导出为 `flavors.json`（路线 1,788 条、中间体 92 种、别名 45 条、暂缓 5 条、手写 156 条）；`content.flavors.FlavorRecipes` 按模板写核心配方：同一设备、同一组输入的联产合为一条多产出配方（原来配比不同的按系数各写一条，2026-11-12 返工起改为报错，按 11 问题 41 处理）；每种带纯度的香料一条可选精制（液体蒸馏，固体乙醇重结晶，K1、K2 水重结晶，气体在精馏塔精馏）。`FlavorManual` 写 19 §2 逐步描述的中间体、18 §5 的六种中间体、超出模板设备的路线、煤焦油各馏分、木醋液沉降的液体烟熏香味料，手写的香料同样有可选精制
- 非香料：`content.batch5` 六个类：`Batch5Aids`（424–459）、`Batch5Vitamins`（460–481）、`Batch5Minerals`（482–530）、`Batch5Nutrients`（531–563，新菌种与酶）、`Batch5GumBase`（564–584）、`Batch5Refining`（可选精制）。多步路线按 11 问题 40 合为最难一步设备上的一条（中间的洗涤、过滤、干燥、粉碎、回收溶剂并入）；17 的“精制”与“纯化”各写成一条可选精制，按产物取方式：可溶盐与小分子水重结晶，脂溶性固体乙醇重结晶，难溶沉淀水洗，聚合物与胶体乙醇洗，铁粉通氢退火，蛋白、肽与低聚糖上大孔树脂，松香酯水蒸气汽提，液体蒸馏，维生素油与功能油脂分子蒸馏
- 写法：`Chem` 按物料表决定物品或流体（1 单位 = 1 个或 1,000 mB），带纯度的产出取来源值；`Batch` 加入 M14（加热、冷却、耐腐蚀、通氮、通气）、M23、M23 加氢、M27、M22 多馏分的写法
- 机器：M23 新增聚合模式（P97，高压）；M20 新增还原模式（P104）；M14 输入储罐 3 → 6、M23 3 → 5、M21 2 → 3（用户 2026-11-11 定）；M03 输入储罐 1 → 2、M14 输出储罐 2 → 3（用户 2026-11-12 定：储罐不够的设备补储罐，是否换设备看是否贴近现实），界面储罐宽度加 7、6 两档；条件词表加“还原”
- 槽位不够的处理（用户 2026-11-12 定：缺物品槽的先合成或混合）：N182 苦杏仁油先在蒸馏甑蒸出苦杏仁粗油，再在浸提桶用熟石灰、硫酸亚铁除氢氰酸（新增中间体 `crude_bitter_almond_oil`）；579 部分二聚松香先熔化（新增 `molten_partially_dimerized_rosin`），在精馏塔减压蒸馏得聚合松香；S1132 用预先做好的钯-离子交换树脂（新增催化剂 `catalysts/palladium_resin`，由贵金属催化剂与离子交换树脂浸渍）。储罐补足后回到设计写法的：565 节路顿胶、582 小烛树蜡、18 §5 芦丁在熬煮灶加热；S0385 / S0930 联产乙酸、577 回收乙酸乙烯酯、511 回收乙醇。ST38 蓖麻油碱裂解维持在高压反应釜（现实在反应釜中做，回转窑不加流体输入）
- 世界生成：硒矿石、铬铁矿石（方块、标签、世界生成、JEI 信息页，数值按 08 待审）
- 原料与标签：动物血由五种原版生肉在固液分离机压榨（4 个出 250 mB）；08 第 B 节补粗硒、铬、动物血三行他模组标签（未核对）；食用菌取原版褐色蘑菇作菌种工作台样本；富硒大豆由大豆在浸提桶用亚硒酸钠浸泡
- 文本：化学名检查扩到化学品名称（用户 2026-11-11 定，[text-standard](../text-standard.md) §1.4、§1.5、§1.9）；名称以 ASCII 字母或数字结尾时“桶”前加空格；香料别名在 JEI 信息页写 GB 2760 编码
- 改动前几批（联产合并、11 问题 31、32、38）：
  - I18：煤焦油精馏联产 1-甲基萘、喹啉；木焦油精馏联产 2-甲氧基-4-甲基苯酚、松焦油；百里香酚制薄荷醇联产 dl-薄荷醇
  - I19：原油切割加丙烷、轻石脑油
  - I20：删除 133 B，133 改由葛缕子油、莳萝油精馏（莳萝一路联产 α-水芹烯）；对苯二酚改为苯酚与双氧水联产邻苯二酚；肉桂水蒸气蒸馏联产中国肉桂油；乙醇精馏侧线加粗杂醇油（N402）
  - I30：辛烯联产丁烯、癸烯；丙烯腈联产乙腈；己二酸联产戊二酸；产半胱氨酸大肠杆菌的重组平板同时长出产酪氨酸、产色氨酸菌（三者输入相同）
  - 物料属性：`zinc_chloride` 改化学品 K1，`paraffin_wax` 改 K7，`alpha_tocopherol` 显示名改为 d-α-生育酚
- 纯度：被香料组复用的 7 种前几批物料（谷氨酸、L-苯丙氨酸、己酸、辛酸、癸酸、棕榈酸、油酸）补可选精制
- 用户确认（2026-11-12）：动物血每 4 个生肉压出 250 mB，富硒大豆 8 个配 1 份亚硒酸钠与 1 桶水浸泡，硒矿石 4 块焙烧出 1 份二氧化硒；08 第 B 节新加的他模组标签（`#forge:dusts/selenium`、`#forge:ingots/chrome`、`#forge:dusts/chrome`、`#forge:blood`）按现在的写法
- 用户确认（2026-11-12，返工）：数据生成检查放过的三类（按摆放环境区分的 T0 配方，竹林丛林里红曲霉优先；产物与比例相同；手工配方与机器配方）；190 改为酸式焦磷酸钠 + 焦磷酸四钠在 M14 溶解后低温干燥；435 B 改高压反应釜里酸性离子交换树脂液相脱水；ST11 注明气相的四行改 M27，没写催化剂的取氧化铝；甘露醇每 1,000 mB 海藻提取物加 250 mB 乙醇、回收 200 mB，松香联产松节油 100 mB；蜂蜡与两种重结晶的机器配方加“加热”。均按拟定
- 验证：三个节点数据生成与单元测试通过；重复配方检查只剩前几批手写与机器配方各一组（熟石灰、罗汉果甜苷，设备不同）；1.20.1 服务器用开发命令逐条试算（`/fidev process`）：动物血压榨、还原铁（回转窑还原、流化床氢还原）、硒矿石焙烧、亚硒酸钠、富硒酵母、电解铁、富马酸亚铁（通氮）、富硒大豆（浸提桶）、碳四馏分与丁二烯、聚乙烯（高压釜可做，搅拌反应罐缺高压、高温）、丁基橡胶、节路顿胶、聚合松香（壶式蒸馏釜）、糖蜜、粗杂醇油侧线、大肠杆菌重组平板（三种产氨基酸菌与宿主）；可选精制按输入纯度升级（亚硒酸钠 90.6% → 97.4%，血红素铁 12% → 58%，甲硫醇 95.2% → 99.4%，正己烷 84% → 97.4%），445 复配产物取食品级界限；补储罐后（2026-11-12）三个节点重新通过，熬煮灶上的节路顿胶、小烛树蜡、芦丁，苦杏仁粗油与除氰、熔融部分二聚松香与精馏塔减压蒸馏、钯-离子交换树脂与二异丁基酮、搅拌反应罐三个流体输出（月桂酸乙烯酯、硒蛋白、乙酸龙脑酯联产）逐条试算正常
- 报表与 17–19 对照（`purity_report_stages.py` 加上 17 的阶段；香料按本清单对照）：做得出的 1,469 项中 1,277 项与设计阶段一致或落在括号内的上游档次之间；106 项晚于设计，多为上游档次（酯类香料的酸、醇在 T3–T4，如丁酸靠 T4 发酵；441 苯乙烯、511 正己烷、573 甲苯为 T3；472 L-丙氨酸为 T4）；61 项早于设计（542 乳铁蛋白、550 棉子糖、543、544 的树脂纯化按约定在 T2 搅拌反应罐做；548 的乳糖酶等不带纯度的催化剂不计入档次，同 I30 的已知局限；461 麦角钙化醇为 T3）；除辣椒油树脂外，带纯度的产物都能精制到食品级
- 2026-11-12 返工（11 问题 41）：
  - 框架：`ProcessRecipe.match` 改为全覆盖（非空的输入槽与储罐都要被这条配方用上；M14 惰性气氛的氮气罐不算输入，T0 器具产物槽里的存货不影响匹配）。数据生成检查按（配方类型、模式、催化剂、输入物料集合）分组，同组两条若有一台设备在同一模式下能同时做就报错：设备按模式分开（`DeviceSpec.Configuration`，每个模式一组能力，模块、通气、气体供给都算上），所以 M15 蒸发结晶与冷却结晶、M08 三种模式、M09 两种模式各自不冲突；M14、M23 的温度不分模式，加热与冷却、加热与高温同时具备，靠温度、压力区分的配方都按冲突处理
  - 检查放过三类，数据生成日志逐组列出（共 15 组）：① 按摆放环境区分的 T0 配方（07 的 T0 规则，条件最多的优先，环境由玩家摆放决定）：瓮里米曲霉、红曲霉、根霉、野生霉菌五组，晾架上姜黄晒干与淋雨发霉；② 产物与比例相同（选中哪条结果一样，纯度与时长除外）：熟石灰、罗汉果甜苷、蜂蜡、天然橡胶两种干燥、熟石膏两种煅烧；③ 手工配方与机器配方：只有手工配方能上 T0 器具，机器上两条都能做时机器配方被选中（条件一样多时 ID 小的优先），即酒石酸氢钾、纯碱的重结晶。为此蜂蜡与这两种重结晶的机器配方补上与手工配方相同的“加热”条件，原来机器上被手工配方抢先
  - 联动配方：有输入相同的机器配方的手工配方不再导出到他模组机器（Create 搅拌的蜂蜡、Thermal 结晶器的两种重结晶原各有两条）；下面三条蒸馏甑配方加了模式后不再导出到 Thermal 精炼机、PneumaticCraft 精炼厂（原与精馏塔的多馏分配方在那里撞车）
  - 18 §1.5 的表：逐条处理、概率平板、联产各组、第二轮核对 14 组都按表改了；其中 51 三聚磷酸钠原本就是磷酸氢二钠 + 磷酸二氢钠 2 : 1，实际撞车的是 190（见待审）
  - 检查又查出的表外各组（苦卤蒸发与冷却结晶按模式分开后不再冲突，未改），处理一律是“后一步由前一步做”、换试剂或催化剂、联产合并：
    - 蒸馏甑与精馏塔：粗苯 / 煤焦油精馏、木醋液蒸馏 / 精馏、乙醇复蒸 / 杂醇油侧线——蒸馏甑一路加“蒸馏”模式（精馏塔没有）
    - 33 双乙酸钠改为乙酸钠 + 冰乙酸（原与乙酸钠同为纯碱 + 乙酸）
    - 49 A 磷酸三钙改为磷酸氢钙（170）+ 石灰乳（二步中和，原与 170 B 同为磷酸 + 石灰乳）
    - 40 A 甘露醇改由 N203 海藻提取物（即海带浸泡水）加乙醇除盐结晶（13 原写“浸泡水 → 浓缩 → 乙醇沉淀除盐 → 冷却结晶”，原与 N203 同为海带 + 水）
    - 127 苯甲醛一路的苯亚甲基二氯改为氯化苄 + 氯气光氯化（侧链逐级氯化，原与氯化苄同为甲苯 + 氯气）
    - 196 一路的萘三磺酸改为 2-萘磺酸 + 硫酸（逐级磺化，原与萘磺酸同为萘 + 硫酸）
    - 132 B 一路的 1,2-丙二胺改为异丙醇胺 + 氨，镍催化胺化（原与异丙醇胺同为环氧丙烷 + 氨）
    - 137 一路的松香与 N298 松节油合为一条：树脂水蒸气蒸馏，松节油随蒸汽出、分油器分出，松香留在釜里（N298 改手写）
    - S0630 γ-丁内酯改为琥珀酸酐 + 氢气，铜催化（顺酐 → 琥珀酸酐 → 丁内酯，原与琥珀酸酐同为顺酐 + 氢气、镍）
    - 二氯化二硫改为二氯化硫 + 硫（原与第三批二氯化硫同为硫 + 氯气）
    - ST11 注明“M27 气相”的四行（2-甲基吡啶、噻吩、吡嗪、吡咯）按 19 改在 M27 气相催化，没写催化剂的取氧化铝：2-甲基吡啶因此与 110 的 3-甲基吡啶（乙醛 + 氨，高压釜）类型不同
    - S0931 α-环柠檬醛按 19 改为从 S0715 环柠檬醛精馏切出（原与 S0715 同为柠檬醛 + 苯胺 + 硫酸，只差温度）
    - 三组异构体切割（α-环柠檬醛、反式 α-突厥酮、3-乙基-2,6-二甲基吡嗪）同时是母体的精制：剩下的母体按精馏塔默认纯度出，母体不再另写精制（原与母体的精制配方在精馏塔上撞车）
    - 435 B 乙醚、190 焦磷酸一氢三钠见待审
  - 检查本身：“每条配方至少一台设备可执行”原来把开发用测试设备也算上，改为不算（190 一度只有测试反应罐能做，查出后改在 M14）；开发命令 `/fidev findrecipe` 改按机器的选择规则（条件多的优先，一样多时 ID 小的优先），原来取加载顺序第一条
  - 验证：三个节点数据生成与单元测试通过，重复配方检查清零（放过的 15 组见上）；联动配方中同一台他模组机器上输入相同的组也清零；1.20.1 服务器用 `/fidev findrecipe`、`/fidev process` 试：熬煮灶上酒石酸氢钾、纯碱重结晶与蜂蜡选中机器配方，蒸馏甑上煤焦油、乙醇分别选中粗苯、复蒸，精馏塔上选中煤焦油精馏、杂醇油侧线、α-环柠檬醛切割（母体剩余 93.8%），M27 上乙醛 + 氨为 2-甲基吡啶、M23 上为 3-甲基吡啶，M23 上乙醇为树脂法乙醚、M27 上为乙烯，蒸发结晶器上海藻提取物出甘露醇，M14 上磷酸氢钙 + 石灰为磷酸三钙、两种焦磷酸钠为焦磷酸一氢三钠，蒸馏甑上树脂同时出松香与松节油
- 2026-11-12 返工（二）（11 问题 42）：
  - 天然原料：`export_flavors.py` 读 18 §1.4 的档表（174 种，导出为 `raw_tiers`）；精油（NT1、NT2、NT12）取档值（玫瑰花 16、甜橙 8、柏木 4、丁香花蕾 1），浸膏、油树脂（NT4、NT6，NT5 一步做出净油的）取一半、最少 1，酊与提取物（NT3、NT7）2，香脂（NT8）1，干品粉末（NT14）鲜叶与全草 4（达迷草叶、白藓牛至、苦艾、香蜂花）、其余 2。产出量不变（精油 100 mB，浸膏、油树脂 250 mB 或 1 件，提取物 500 mB）。NT11、NT17、ST21 照原写 4，ST13、ST53 以原料直接蒸馏的照原写 8；档表没有的 7 种见待审
  - 合成数量：`export_flavors.py` 按关键反应逐步配平（后一步用到的中间物定前一步的倍数，分数通分为整数），导出输入系数（按物料）与产物系数（按化学式）；模板配方的物品输入取系数件数，流体取系数 × 1,000 mB，主产物与回收的副产（酸酐给出的酸、氯化氢、丙酮、癸二酸二钠等）也按方程式，原写 500 mB 的改为系数 × 1,000 mB。例：乙醛二乙缩醛用乙醇 2 桶；蔗糖八乙酸酯用乙酸酐 8 桶、出乙酸 8 桶；龙脑 B 为莰烯 2 + 草酸 1 + 氢氧化钠 2 → 龙脑 2。总反应式的系数超出储罐的三种发酵按每份产物取整（见待审）。合并键不再含数量，配比不同的同一组反应物仍按 11 问题 41 报错
  - 手写配方逐条对照方程式（脚本比对生成的配方与 18、19 的关键反应），改了 S0316 苯乙酸（氢氧化钠 2、氯化钠 2）与 S1455（乙酸 1 桶）；余下对不上的是有意的：联产合并（异佛尔酮与异亚丙基丙酮、α-环柠檬醛切割）、异构平衡（S0445 B）、发酵培养基的水
  - 碱熔（早先已改）：S0101 对乙基苯酚、2-萘酚（I20）、间苯二酚（I30）都是回转窑或高温出钠盐、M14 酸化两条；ST38 蓖麻油在 M23 高温裂解出 2-辛醇与癸二酸二钠，癸二酸在 M14 硫酸酸化
  - 431 C：液化淀粉液 2 桶经丙酮丁醇梭菌在 M05 密闭发酵出 `abe_broth` 2 桶与二氧化碳 1 桶；精馏塔每桶发酵液出丙酮 60、正丁醇 120、乙醇 20 mB（原发酵直接出正丁醇 600、丙酮 300 mB 的配方删去）
  - 494 只写铁（电极催化剂照旧）；511 不改
  - 513、545：拆分配方加 N-乙酰-D 体副产（545 补乙酸副产，同 513）；消旋为 D 体 1 件 + 乙酸酐 100 mB 在 M14 加热 → N-乙酰-DL 体 1 件
  - 528 B：重铬酸钠 1 + 盐酸 8 桶 + 甲醇 1 桶，M14 耐腐蚀内衬加热 → 氯化铬 2、氯化钠 2、二氧化碳 1 桶
  - 裂解：轻石脑油 + 水在 M27 裂解模式 → 裂解气 1 桶；精馏塔深冷分离模式每桶裂解气出乙烯 300、丙烯 150、碳四馏分 100、1,3-戊二烯 10 mB；丁烷加水蒸气裂解制碳四馏分与单独的 1,3-戊二烯配方删去，I19 的丁烷裂解制乙烯不动
  - 581：费托合成同一配方另出轻石脑油 250 mB；镍催化加氢精制照旧为可选精制（活性炭渗滤并入，不另写）
  - 糖蜜：`cane_molasses`（甘蔗）与 `beet_molasses`（甜菜），流体标签 `foodindustrial:molasses` 收两者；N276、N351、550 照 18、17
  - 辣椒油树脂不带纯度：三条浸提去掉来源值，209 与 60 B 的引用本来不设门槛
  - N267：木醋液 1 桶 + 活性炭（4 次）在 M14 → 焦木酸 900 mB + 木焦油 100 mB（T2，`FlavorManual`）
  - 颜色：`export_materials.py` 对 18 §2、19 §5 的天然香料与浸膏按模板取 09 的形态默认色（颜色栏空着的才取，固体也写入，共 326 种）；占位贴图对有颜色的固体按颜色重画，只换仍是原占位图的 57 张（手画的不动）
  - 验证：三个节点数据生成与单元测试通过，重复配方检查放过的仍是原来 15 组；第五批物料都有配方产出；1.20.1 服务器用 `/fidev process`、`/fidev findrecipe` 试：发酵池出丙酮丁醇发酵液 2 桶与二氧化碳、精馏塔分出丙酮 60 / 正丁醇 120 / 乙醇 20 mB，气相反应器裂解轻石脑油、精馏塔深冷分离出 300 / 150 / 100 / 10 mB，搅拌反应罐上焦木酸、528 B 甲醇还原、两种消旋，电解槽电解铁，蒸馏甑玫瑰油（16 朵出 100 mB），无菌发酵罐精氨酸，高压反应釜费托蜡（粗蜡 90.6%，未到食品级）与轻石脑油，龙脑 B 出 2 件
- 2026-11-12 审定（三）（11 问题 44）：
  - 档表补上的 7 种直接读 18 §1.4（蜡菊花 16，蜡菊浸膏用 8 朵）；朗姆酒不套档表，`FlavorRecipes.FIXED_RAW` 写 4（朗姆酒在本模组是成品物品，08 写“物品”，所以是 4 瓶而不是 4 桶）
  - 发酵与生物转化（ST18）一律按每份产物取整，不按配平式缩放：葡萄糖 1，氮源按产物的氮数（脯氨酸氨 1，精氨酸氨 4 + 二氧化碳 1，赖氨酸硫酸铵 1）；培养基的水照旧 2 桶。双香兰素 B 随之为香兰素 2 + 氧气 1 → 1
  - `export_flavors.py`：注释可以嵌套（S0344 的原料栏注释里套了日期括号，原来因此被当成手写路线），化学式可以以括号开头（`(NH4)2SO4`）
  - 联产合并按输入分摊：两条各自的反应合成一条时（不是同一个方程式的两个产物，也不是切馏分），每个产物分得一半输入、按自己反应的得率出（`FlavorRecipes.share`）。同一方程式的两个产物（蓖麻油酸裂解出庚醛与十一碳烯酸、甲烷与硫出二硫化碳与硫化氢、S1124 的四硫杂环己烷与三硫杂环戊烷）按方程式的量，切馏分、萃取、结晶、干馏照原量。18 §1.5 的 32 组逐组核对后，按输入分摊的是：丙酮碱缩合（丙酮 3 桶 → 异弗尔酮 500 + 异亚丙基丙酮 750 mB）、α-蒎烯异构化（异松油烯、α-松油烯各 500）、法尼醇环化（红没药烯、α-金合欢烯各 500）、丙烯与 1-丁烯羰基合成（两种醛各 500）、椰子油甲醇解（两种甲酯各 1,500）、两组甲基紫罗兰酮、两种二乙基甲基吡嗪、1-戊烯-3-酮与 3-甲基-3-丁烯-2-酮（各 500）；表外第二轮合并的橙花醇与香叶醇、两种戊二酮、两种甲基苯乙酮、巴豆醛缩合的两种戊烯醛、两种十一烯醛同样各 500。其余各组是切馏分或同一方程式，照原量
  - 间苯二酚的碱熔：配方条件是“高温”，只有 M23 高压反应釜做得了（搅拌反应罐只有加热、冷却），本来就在 M23；I30 实现记录原写“M14 高温”是笔误，已改
  - 验证：三个节点数据生成与单元测试通过，重复配方检查放过的仍是 15 组，第五批物料都有配方产出；生成的配方与 18、19 方程式逐条比对，对不上的只剩按输入分摊的联产与有意的几条（异构平衡、培养基的水）

## 2026-11-12 审定（三）：返工（二）的数值（11 问题 44）

返工（二）时拟定的十条数值已审完：桶数、比例、并入写法一律照准（431 C 的 2 桶 → 2 桶 + 二氧化碳 1 桶、裂解 1 桶 + 水 1 桶 → 1 桶、N267 的木焦油 100 mB 与活性炭 4 次、581 的轻石脑油 250 mB、消旋的乙酸酐 100 mB、528 B 另出二氧化碳而不写水，后者本来就是 [additives/04 §实现约定](../additives/04-production-routes.md) 的“生成的水不写”）。“每产出 1 单位”按模板原产出量理解也照准，已写进 [additives/18 §1.4](../additives/18-batch-5-flavors.md)：8 个鲜叶出 100 mB 精油折合约 1.2%，与现实得油率相符。四处要改：

- [x] 蜡菊花改为 16 档（浸膏 8 个）：现实得油率约 0.1%，1 吨花出 1 千克，是得油率最低的花之一；其余六种照实装拟的写（柏木 4、桃叶 8、橡苔 8、海带 16、灵猫香 1），朗姆酒是流体、不套档表，按 4 桶写。档表已补上这 7 种
- [x] 赖氨酸（S0344）的氮源改用硫酸铵 1 份：[additives/17](../additives/17-batch-5.md) 第 534 号写的就是硫酸铵，现实赖氨酸发酵也用它；一份硫酸铵带两个氮，正好对上产物。19 S0344 的物料与方程式已改（`7 C6H12O6 + 6 (NH4)2SO4 → 6 C6H14N2O2 + 6 CO2 + 6 H2SO4 + 18 H2O`，硫酸留在发酵液里不写，同“溶液副产盐略去”）。脯氨酸氨 1 份、精氨酸氨 4 份 + 二氧化碳 1 份照准
- [x] 联产合并的产出量按输入分摊，不叠加各自单做的量：丙酮 3 份 → 异弗尔酮 500 mB + 异亚丙基丙酮 750 mB（原写法两个产物都按全量出，产物的碳多于输入）。切馏分、分馏类的联产（煤焦油、杂醇油、松香与松节油）照原量写。32 组逐组核对一遍
- [x] 间苯二酚（195）的碱熔放在 M23 高温，不是 M14：搅拌反应罐只有加热档，没有高温档（[additives/06](../additives/06-machines.md) M14 的条件栏），与 ST38 蓖麻油同一处理。05 P70、15 第 195 号、18 §1.4 已改

## 待审（实现时拟定，请确认）

- 无。返工（二）的十条数值已审定（11 问题 44），处理见上节。

## 待定

- 无。设计中提出的问题 29–44 已决定（29–40 为 2026-11-11，41–44 为 2026-11-12），结果见 [additives/11](../additives/11-roadmap.md) 与 [additives/17 §9](../additives/17-batch-5.md)
