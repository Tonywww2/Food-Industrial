# I20 第三批添加剂内容（79–139）

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | E 内容 |
| 依赖 | I19、I17 |
| 原文档 | [additives/14](../additives/14-batch-3.md)、[additives/05 §F](../additives/05-processes.md)、[additives/09](../additives/09-chemical-formulas.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `content.batch3`（数据生成） |

> 2026-11-10：[additives/07](../additives/07-manual-crafting.md) 的 T0 手工链（本批 96、107、136、137 号）已在 [I12](I12-t0-manual-devices.md) 实装，本计划只做 T1 起的路线；101 木瓜乳汁暂不做。
>
> 纯度模型已改为 v2（2026-11-04）：本计划中与 [I28](I28-purity-model-v2.md) 冲突的条目以 I28 为准。
>
> 2026-11-05 设计调整（以原文档为准）：
> - 79–84 每种色素的铝色淀是单独物品（`tartrazine_lake` 等，P71），界限见 [additives/10 §1.5](../additives/10-purity-jei-ae2.md)；清单已加 ID。
> - 94–106 的酶不带纯度（现实按酶活计价），粗酶与精制酶是同一物品（[additives/10 §1.1](../additives/10-purity-jei-ae2.md)）。
> - 96 的酶源为大麦麦芽 `enzymes/barley_malt`，大麦暂无来源；小麦麦芽 `enzymes/malt` 只作 T1 糖化（[additives/08 §C 菌种与酶](../additives/08-raw-materials.md)）。101 的木瓜乳汁不带纯度。
> - 105 A 用的皱胃 `materials/abomasum` 暂无来源；表 C.3 第 39 项（大肠杆菌来源）由工程菌（凝乳酶）覆盖，属第四批（[additives/15 §F 酶制剂（374–423）](../additives/15-batch-4.md)）。
> - 107 A 的鱼肝油、108 的羊毛脂、137 的松脂与松香是多组分混合物，不带纯度。
> - 117 的乙二胺四乙酸按第四批 281：A 氰甲基化为可选路线、照常注册（不设配置项禁用，2026-11-05），B 氯乙酸法；第四批暂无实装计划，这条中间体路线在本计划先做。
> - 138 蜡最早 T0（蜂蜡手工熔炼），清单已改。
> - 不带纯度的物料（2026-11-05，[additives/10 §1.1](../additives/10-purity-jei-ae2.md)）：酶与酶源（麦芽、大麦麦芽、木瓜乳汁等）、蒸馏水、多组分混合物（苦卤、灰、碱液、硝水、发酵液、天然油脂、松脂、焦油、原油等）。这些物料的产出不写 `Purity`，配方不对它们设门槛，它们也不参与起点纯度；输入全部不带纯度时产物按来源方式取值。

## 目标

第三批 61 项可做，芳烃精细合成与四份专项名单落地。

## 范围

- 做：合成色素、高倍甜味剂、合成抗氧化剂、酶制剂、营养强化剂、香料、胶基

## 清单

| 编号 | 名称 | 最早阶段 | 物料 ID（09） | 路线（原文档） | 状态 |
| --- | --- | --- | --- | --- | --- |
| 79 | 柠檬黄 | T3 | `tartrazine`、`tartrazine_lake` | [additives/14 §A 合成色素（79–84）](../additives/14-batch-3.md) | 已完成 |
| 80 | 日落黄 | T3 | `sunset_yellow`、`sunset_yellow_lake` | [additives/14 §A 合成色素（79–84）](../additives/14-batch-3.md) | 已完成 |
| 81 | 胭脂红 | T3 | `ponceau_4r`、`ponceau_4r_lake` | [additives/14 §A 合成色素（79–84）](../additives/14-batch-3.md) | 已完成 |
| 82 | 诱惑红 | T3 | `allura_red`、`allura_red_lake` | [additives/14 §A 合成色素（79–84）](../additives/14-batch-3.md) | 已完成 |
| 83 | 亮蓝 | T3 | `brilliant_blue`、`brilliant_blue_lake` | [additives/14 §A 合成色素（79–84）](../additives/14-batch-3.md) | 已完成 |
| 84 | 靛蓝 | T1 / T3 | `indigo_carmine`、`indigo_carmine_lake` | [additives/14 §A 合成色素（79–84）](../additives/14-batch-3.md) | 已完成 |
| 85 | 天门冬酰苯丙氨酸甲酯（阿斯巴甜） | T4 | `aspartame` | [additives/14 §B 高倍甜味剂（85–89）](../additives/14-batch-3.md) | 已完成 |
| 86 | 乙酰磺胺酸钾（安赛蜜） | T3 | `acesulfame_potassium` | [additives/14 §B 高倍甜味剂（85–89）](../additives/14-batch-3.md) | 已完成 |
| 87 | 三氯蔗糖 | T4 | `sucralose` | [additives/14 §B 高倍甜味剂（85–89）](../additives/14-batch-3.md) | 已完成 |
| 88 | 纽甜 | T4 | `neotame` | [additives/14 §B 高倍甜味剂（85–89）](../additives/14-batch-3.md) | 已完成 |
| 89 | 环己基氨基磺酸钠（甜蜜素） | T3 | `sodium_cyclamate` | [additives/14 §B 高倍甜味剂（85–89）](../additives/14-batch-3.md) | 已完成 |
| 90 | 二丁基羟基甲苯 | T3 | `bht` | [additives/14 §C 合成抗氧化剂（90–93）](../additives/14-batch-3.md) | 已完成 |
| 91 | 丁基羟基茴香醚 | T3 | `bha` | [additives/14 §C 合成抗氧化剂（90–93）](../additives/14-batch-3.md) | 已完成 |
| 92 | 特丁基对苯二酚 | T3 | `tbhq` | [additives/14 §C 合成抗氧化剂（90–93）](../additives/14-batch-3.md) | 已完成 |
| 93 | 没食子酸丙酯 | T2 | `propyl_gallate` | [additives/14 §C 合成抗氧化剂（90–93）](../additives/14-batch-3.md) | 已完成 |
| 94 | α-淀粉酶 | T4 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 已完成 |
| 95 | 葡萄糖淀粉酶 | T4 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 已完成 |
| 96 | β-淀粉酶 | T1 | —（酶，不带纯度；酶源 `enzymes/barley_malt`） | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 已完成 |
| 97 | 普鲁兰酶 | T4 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 已完成 |
| 98 | 葡萄糖氧化酶 | T4 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 已完成 |
| 99 | 果胶酶 | T4 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 已完成 |
| 100 | 纤维素酶 | T4 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 已完成 |
| 101 | 蛋白酶（木瓜蛋白酶、菠萝蛋白酶） | T1 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 已完成 |
| 102 | 脂肪酶 | T4 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 已完成 |
| 103 | 乳糖酶 | T4 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 已完成 |
| 104 | 谷氨酰胺转氨酶 | T4 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 已完成 |
| 105 | 凝乳酶 | T1 / T4 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 已完成 |
| 106 | 转化酶（蔗糖酶） | T2 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 已完成 |
| 107 | 维生素 A 乙酸酯 | T1 / T4 | `retinyl_acetate` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 已完成 |
| 108 | 维生素 D3 | T4 | `cholecalciferol` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 已完成 |
| 109 | 盐酸硫胺（维生素 B1） | T4 | `thiamine_hydrochloride` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 已完成 |
| 110 | 烟酰胺 / 烟酸 | T4 | `nicotinamide` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 已完成 |
| 111 | 叶酸 | T4 | `folic_acid` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 已完成 |
| 112 | 碳酸钙（轻质） | T2 | `calcium_carbonate` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 已完成 |
| 113 | 葡萄糖酸钙 | T3 | `calcium_gluconate` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 已完成 |
| 114 | 乳酸钙 | T2 | `calcium_lactate` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 已完成 |
| 115 | 硫酸亚铁 | T1 | `ferrous_sulfate` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 已完成 |
| 116 | 焦磷酸铁 | T3 | `ferric_pyrophosphate` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 已完成 |
| 117 | 乙二胺四乙酸铁钠 | T4 | `sodium_iron_edta` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 已完成 |
| 118 | 硫酸锌 / 葡萄糖酸锌 | T2 / T3 | `zinc_sulfate` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 已完成 |
| 119 | 碘酸钾 | T3 | `potassium_iodate` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 已完成 |
| 120 | 牛磺酸 | T3 | `taurine` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 已完成 |
| 121 | 二十二碳六烯酸 | T4 | `dha` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 已完成 |
| 122 | 乙基香兰素 | T3 | `ethyl_vanillin` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 已完成 |
| 123 | 麦芽酚 | T2 | `maltol` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 已完成 |
| 124 | 乙基麦芽酚 | T4 | `ethyl_maltol` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 已完成 |
| 125 | 柠檬醛 | T1 | `citral` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 已完成 |
| 126 | 肉桂醛 | T1 / T3 | `cinnamaldehyde` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 已完成 |
| 127 | 苯甲醛 | T2 / T3 | `benzaldehyde` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 已完成 |
| 128 | 乙酸异戊酯 | T2 | `isoamyl_acetate` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 已完成 |
| 129 | 丁酸乙酯 | T2 | `ethyl_butyrate` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 已完成 |
| 130 | γ-癸内酯 | T4 | `gamma_decalactone` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 已完成 |
| 131 | 呋喃酮（4-羟基-2,5-二甲基-3(2H)-呋喃酮） | T2 | — | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 已完成 |
| 132 | 2,3,5-三甲基吡嗪 | T3 | `trimethylpyrazine` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 已完成 |
| 133 | d-香芹酮 | T1 / T3 | `d_carvone` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 已完成 |
| 134 | 聚乙酸乙烯酯 | T3 | `polyvinyl_acetate` | [additives/14 §G 胶基（134–139）](../additives/14-batch-3.md) | 已完成 |
| 135 | 聚异丁烯 | T3 | `polyisobutylene` | [additives/14 §G 胶基（134–139）](../additives/14-batch-3.md) | 已完成 |
| 136 | 天然橡胶 | T1 | `natural_rubber` | [additives/14 §G 胶基（134–139）](../additives/14-batch-3.md) | 已完成 |
| 137 | 松香甘油酯 | T2 | `rosin_glycerol_ester` | [additives/14 §G 胶基（134–139）](../additives/14-batch-3.md) | 已完成 |
| 138 | 蜡 | T0 / T3 | — | [additives/14 §G 胶基（134–139）](../additives/14-batch-3.md) | 已完成 |
| 139 | 胶基与口香糖 | T3 | — | [additives/14 §G 胶基（134–139）](../additives/14-batch-3.md) | 已完成 |

物料 ID 为“—”的项还没有在 09 中登记（添加剂本身的化学式见 [additives/02](../additives/02-gb2760-additive-list.md)），在 I05 中补登后填入。

## 每一项的通用步骤

1. 物料：按 09 注册物品或流体（I05），名称按文本规范（I04），标签按 08（I06）。
2. 配方：按路线逐条写入数据生成；配方类型与设备按 [machines/01 §2](../machines/01-recipe-types.md)，T0 工序写 `manual.purity`（[additives/10 §2.1](../additives/10-purity-jei-ae2.md)）。
3. 门槛：需要纯度的输入写纯度原料（[additives/10 §4](../additives/10-purity-jei-ae2.md)）。
4. 检查：JEI 可见、最早阶段能做出、化学式与 09 一致。

## 验收

- [x] 清单中每一项在“最早阶段”的设备上能做出，并能沿路线升级到最终阶段
- [x] 数据生成检查全部通过（时长为 4 的倍数、每条配方至少一台设备可执行、带纯度的产出写入纯度、不带纯度的物料不写纯度也不设门槛）
- [x] 三个节点的数据生成输出无报错
- [x] 营养强化剂逐项对照 GB 14880 现行版本后再实装（[additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md)）

## 实现记录

- 代码：`content.batch3.Batch3Recipes`（约 150 条：芳烃链（硝化、还原、磺化、重氮化、碱熔）、79–84 合成色素与六种铝色淀、85–89 高倍甜味剂、90–93 合成抗氧化剂、94–106 酶、107–121 营养强化剂、122–133 香料、134–139 胶基）；酶的发酵按菌种与培养基区分（同一菌种不同培养基得不同的酶）
- 物料补登（09、08）：2-萘磺酸、d-柠檬烯；酶 10 种：葡萄糖氧化酶、果胶酶、纤维素酶、木瓜蛋白酶、菠萝蛋白酶、脂肪酶、乳糖酶、谷氨酰胺转氨酶、凝乳酶、转化酶
- 铝色淀：色素 ×1 + 新制氢氧化铝 ×2 → 色淀 ×1，纯度为色素的三分之一（劣化 67%，起点只取色素）
- 没有中间体物料的多步合成合并为一步（吡唑啉酮、甲氧基甲苯胺磺酸、维生素 B1 的两环、叶酸的三组分、乙基麦芽酚、d-香芹酮的亚硝酰氯与肟），写在源码注释
- 过氧化氢由电解硫酸（过硫酸盐法）得到，供对苯二酚与 54 B；二氧化锰、高锰酸钾暂无来源，相应路线另给替代
- 验证：三个节点数据生成与单元测试通过；报表最早档次与 14 一致的：80–83 T3、85 T4、86 T3、88 T4、89–92 T3、94、95、97–100、102–104 T4、96、101、105 T1、106 T2、110 T4、112 T2、113 T3、115 T1、116 T3、119、120 T3、121 T4、122 T3、125、126 T1、127 T2、130 T4、131 T2、133 T3、134、135 T3、136 T0、138 T1、139 T3
- 与 14 不同的：79 柠檬黄、84 靛蓝 T2（铁粉还原苯胺在 T2 搅拌反应罐即可）；87 三氯蔗糖、109 硫胺素、111 叶酸、117 乙二胺四乙酸铁钠、124 乙基麦芽酚 T3（14 为 T4：T4 没有做液相反应的机器，见实现约定）；93 没食子酸丙酯、128 乙酸异戊酯 T3（正丙醇、异戊醇要精馏塔从乙醇分出杂醇油）；107 维生素 A 乙酸酯 T3（鱼肝油分子蒸馏；14 的 T1 是鱼肝油本身）；114 乳酸钙、118 硫酸锌、123 麦芽酚 T1（熬煮灶、土窑即可，14 为 T2）；129 丁酸乙酯 T4（丁酸梭菌要在菌种工作台分离）；132 三甲基吡嗪 T2（美拉德路线）；137 松香甘油酯 T3（250 °C 级要高温，搅拌反应罐只到加热）
- 游戏内抽查与 I18 一起做，见 [I18 实现记录](I18-content-batch-1.md)
- 2026-11-12 审定（11 问题 43）：2-萘酚拆成两条：萘磺酸 1 件 + 氢氧化钠 3 件在回转窑碱熔出 `sodium_2_naphtholate` 与亚硫酸钠各 1 件；萘酚钠 2 件 + 水 1 桶 + 硫酸 1 桶在 M14 酸化出 2-萘酚 2 件与硫酸钠 1 件（T2）。酶的英文名按 IUBMB 推荐名改了五种（Triacylglycerol Lipase、β-Galactosidase、Protein-Glutamine γ-Glutamyltransferase、Fruit Bromelain、β-Fructofuranosidase，常用名在括号里），其余五种不变，08 酶表下注明

## 2026-11-12 审定（11 问题 43）

设计侧审完本计划的待审三条（[additives/11 问题 43](../additives/11-roadmap.md)）。照准的：实现约定、配方数值、Naphthalene-2-sulfonic Acid 与 d-Limonene 两个英文名。要改的：

- [x] 2-萘酚的碱熔按 11 问题 42 拆成两条：M20 碱熔出 `sodium_2_naphtholate`，M14 水溶、硫酸酸化出 2-萘酚（[additives/14](../additives/14-batch-3.md) 第 80 号已改，与 [I31](I31-content-batch-5.md) 同一处理）
- [x] 十种酶的英文名按国际生化学会（IUBMB）推荐名核对一遍（[additives/08 §C 菌种与酶](../additives/08-raw-materials.md) 的酶表）
- [ ] 合并为一步的合成、酶的培养基、铝色淀的配比：设计侧下一轮对照源码核对；铝色淀的染料含量按对应国标取值

## 待审（实现时拟定，请确认）

- 无。2026-11-12 的三条已审定（11 问题 43），处理见上节。

## 待定

- 合成色素混用限量（[additives/11 §待定问题 11](../additives/11-roadmap.md)）
- 回转窑的“氯化”模式没有流体输入（I15 的槽位 FI0），三氯化铝改在 T3 高压反应釜里通氯气制得；是否给回转窑加流体仓口另议
- 维生素 A 乙酸酯的合成路线（β-紫罗兰酮链增长）没做，只做鱼肝油分子蒸馏 → 视黄醇 → 乙酰化
- 98 葡萄糖氧化酶已能制得，13 号内酯的酶法氧化没加（现有发酵与电解路线）
