# I20 第三批添加剂内容（79–139）

| 项 | 内容 |
| --- | --- |
| 状态 | 未开始 |
| 阶段 | E 内容 |
| 依赖 | I19、I17 |
| 原文档 | [additives/14](../additives/14-batch-3.md)、[additives/05 §F](../additives/05-processes.md)、[additives/09](../additives/09-chemical-formulas.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `content.batch3`（数据生成） |

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
| 79 | 柠檬黄 | T3 | `tartrazine`、`tartrazine_lake` | [additives/14 §A 合成色素（79–84）](../additives/14-batch-3.md) | 未开始 |
| 80 | 日落黄 | T3 | `sunset_yellow`、`sunset_yellow_lake` | [additives/14 §A 合成色素（79–84）](../additives/14-batch-3.md) | 未开始 |
| 81 | 胭脂红 | T3 | `ponceau_4r`、`ponceau_4r_lake` | [additives/14 §A 合成色素（79–84）](../additives/14-batch-3.md) | 未开始 |
| 82 | 诱惑红 | T3 | `allura_red`、`allura_red_lake` | [additives/14 §A 合成色素（79–84）](../additives/14-batch-3.md) | 未开始 |
| 83 | 亮蓝 | T3 | `brilliant_blue`、`brilliant_blue_lake` | [additives/14 §A 合成色素（79–84）](../additives/14-batch-3.md) | 未开始 |
| 84 | 靛蓝 | T1 / T3 | `indigo_carmine`、`indigo_carmine_lake` | [additives/14 §A 合成色素（79–84）](../additives/14-batch-3.md) | 未开始 |
| 85 | 天门冬酰苯丙氨酸甲酯（阿斯巴甜） | T4 | `aspartame` | [additives/14 §B 高倍甜味剂（85–89）](../additives/14-batch-3.md) | 未开始 |
| 86 | 乙酰磺胺酸钾（安赛蜜） | T3 | `acesulfame_potassium` | [additives/14 §B 高倍甜味剂（85–89）](../additives/14-batch-3.md) | 未开始 |
| 87 | 三氯蔗糖 | T4 | `sucralose` | [additives/14 §B 高倍甜味剂（85–89）](../additives/14-batch-3.md) | 未开始 |
| 88 | 纽甜 | T4 | `neotame` | [additives/14 §B 高倍甜味剂（85–89）](../additives/14-batch-3.md) | 未开始 |
| 89 | 环己基氨基磺酸钠（甜蜜素） | T3 | `sodium_cyclamate` | [additives/14 §B 高倍甜味剂（85–89）](../additives/14-batch-3.md) | 未开始 |
| 90 | 二丁基羟基甲苯 | T3 | `bht` | [additives/14 §C 合成抗氧化剂（90–93）](../additives/14-batch-3.md) | 未开始 |
| 91 | 丁基羟基茴香醚 | T3 | `bha` | [additives/14 §C 合成抗氧化剂（90–93）](../additives/14-batch-3.md) | 未开始 |
| 92 | 特丁基对苯二酚 | T3 | `tbhq` | [additives/14 §C 合成抗氧化剂（90–93）](../additives/14-batch-3.md) | 未开始 |
| 93 | 没食子酸丙酯 | T2 | `propyl_gallate` | [additives/14 §C 合成抗氧化剂（90–93）](../additives/14-batch-3.md) | 未开始 |
| 94 | α-淀粉酶 | T4 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 未开始 |
| 95 | 葡萄糖淀粉酶 | T4 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 未开始 |
| 96 | β-淀粉酶 | T1 | —（酶，不带纯度；酶源 `enzymes/barley_malt`） | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 未开始 |
| 97 | 普鲁兰酶 | T4 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 未开始 |
| 98 | 葡萄糖氧化酶 | T4 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 未开始 |
| 99 | 果胶酶 | T4 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 未开始 |
| 100 | 纤维素酶 | T4 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 未开始 |
| 101 | 蛋白酶（木瓜蛋白酶、菠萝蛋白酶） | T1 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 未开始 |
| 102 | 脂肪酶 | T4 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 未开始 |
| 103 | 乳糖酶 | T4 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 未开始 |
| 104 | 谷氨酰胺转氨酶 | T4 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 未开始 |
| 105 | 凝乳酶 | T1 / T4 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 未开始 |
| 106 | 转化酶（蔗糖酶） | T2 | — | [additives/14 §D 酶制剂（94–106）](../additives/14-batch-3.md) | 未开始 |
| 107 | 维生素 A 乙酸酯 | T1 / T4 | `retinyl_acetate` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 未开始 |
| 108 | 维生素 D3 | T4 | `cholecalciferol` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 未开始 |
| 109 | 盐酸硫胺（维生素 B1） | T4 | `thiamine_hydrochloride` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 未开始 |
| 110 | 烟酰胺 / 烟酸 | T4 | `nicotinamide` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 未开始 |
| 111 | 叶酸 | T4 | `folic_acid` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 未开始 |
| 112 | 碳酸钙（轻质） | T2 | `calcium_carbonate` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 未开始 |
| 113 | 葡萄糖酸钙 | T3 | `calcium_gluconate` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 未开始 |
| 114 | 乳酸钙 | T2 | `calcium_lactate` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 未开始 |
| 115 | 硫酸亚铁 | T1 | `ferrous_sulfate` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 未开始 |
| 116 | 焦磷酸铁 | T3 | `ferric_pyrophosphate` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 未开始 |
| 117 | 乙二胺四乙酸铁钠 | T4 | `sodium_iron_edta` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 未开始 |
| 118 | 硫酸锌 / 葡萄糖酸锌 | T2 / T3 | `zinc_sulfate` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 未开始 |
| 119 | 碘酸钾 | T3 | `potassium_iodate` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 未开始 |
| 120 | 牛磺酸 | T3 | `taurine` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 未开始 |
| 121 | 二十二碳六烯酸 | T4 | `dha` | [additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md) | 未开始 |
| 122 | 乙基香兰素 | T3 | `ethyl_vanillin` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 未开始 |
| 123 | 麦芽酚 | T2 | `maltol` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 未开始 |
| 124 | 乙基麦芽酚 | T4 | `ethyl_maltol` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 未开始 |
| 125 | 柠檬醛 | T1 | `citral` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 未开始 |
| 126 | 肉桂醛 | T1 / T3 | `cinnamaldehyde` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 未开始 |
| 127 | 苯甲醛 | T2 / T3 | `benzaldehyde` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 未开始 |
| 128 | 乙酸异戊酯 | T2 | `isoamyl_acetate` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 未开始 |
| 129 | 丁酸乙酯 | T2 | `ethyl_butyrate` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 未开始 |
| 130 | γ-癸内酯 | T4 | `gamma_decalactone` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 未开始 |
| 131 | 呋喃酮（4-羟基-2,5-二甲基-3(2H)-呋喃酮） | T2 | — | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 未开始 |
| 132 | 2,3,5-三甲基吡嗪 | T3 | `trimethylpyrazine` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 未开始 |
| 133 | d-香芹酮 | T1 / T3 | `d_carvone` | [additives/14 §F 食品用香料（122–133）](../additives/14-batch-3.md) | 未开始 |
| 134 | 聚乙酸乙烯酯 | T3 | `polyvinyl_acetate` | [additives/14 §G 胶基（134–139）](../additives/14-batch-3.md) | 未开始 |
| 135 | 聚异丁烯 | T3 | `polyisobutylene` | [additives/14 §G 胶基（134–139）](../additives/14-batch-3.md) | 未开始 |
| 136 | 天然橡胶 | T1 | `natural_rubber` | [additives/14 §G 胶基（134–139）](../additives/14-batch-3.md) | 未开始 |
| 137 | 松香甘油酯 | T2 | `rosin_glycerol_ester` | [additives/14 §G 胶基（134–139）](../additives/14-batch-3.md) | 未开始 |
| 138 | 蜡 | T0 / T3 | — | [additives/14 §G 胶基（134–139）](../additives/14-batch-3.md) | 未开始 |
| 139 | 胶基与口香糖 | T3 | — | [additives/14 §G 胶基（134–139）](../additives/14-batch-3.md) | 未开始 |

物料 ID 为“—”的项还没有在 09 中登记（添加剂本身的化学式见 [additives/02](../additives/02-gb2760-additive-list.md)），在 I05 中补登后填入。

## 每一项的通用步骤

1. 物料：按 09 注册物品或流体（I05），名称按文本规范（I04），标签按 08（I06）。
2. 配方：按路线逐条写入数据生成；配方类型与设备按 [machines/01 §2](../machines/01-recipe-types.md)，T0 工序写 `manual.purity`（[additives/10 §2.1](../additives/10-purity-jei-ae2.md)）。
3. 门槛：需要纯度的输入写纯度原料（[additives/10 §4](../additives/10-purity-jei-ae2.md)）。
4. 检查：JEI 可见、最早阶段能做出、化学式与 09 一致。

## 验收

- [ ] 清单中每一项在“最早阶段”的设备上能做出，并能沿路线升级到最终阶段
- [ ] 数据生成检查全部通过（时长为 4 的倍数、每条配方至少一台设备可执行、带纯度的产出写入纯度、不带纯度的物料不写纯度也不设门槛）
- [ ] 三个节点的数据生成输出无报错
- [ ] 营养强化剂逐项对照 GB 14880 现行版本后再实装（[additives/14 §E 营养强化剂（107–121）](../additives/14-batch-3.md)）

## 待定

- 合成色素混用限量（[additives/11 §待定问题 11](../additives/11-roadmap.md)）
