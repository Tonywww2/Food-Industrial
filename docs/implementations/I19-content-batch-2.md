# I19 第二批添加剂内容（31–78）

| 项 | 内容 |
| --- | --- |
| 状态 | 未开始 |
| 阶段 | E 内容 |
| 依赖 | I18、I15、I16 |
| 原文档 | [additives/13](../additives/13-batch-2.md)、[additives/05 §F](../additives/05-processes.md)、[additives/09](../additives/09-chemical-formulas.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `content.batch2`（数据生成） |

> 纯度模型已改为 v2（2026-11-04）：本计划中与 [I28](I28-purity-model-v2.md) 冲突的条目以 I28 为准。
>
> 2026-11-05 设计调整（以原文档为准）：
> - 阶段按工艺重标：52、53、71、61 改为 T2（[additives/11 §待定问题 25](../additives/11-roadmap.md)），清单已改。
> - 56 拆为两种物料：A 路线得 DL-苹果酸 `malic_acid`，B 路线得 L-苹果酸 `l_malic_acid`（与第四批 140 同一物品）。
> - 61 A 的姜黄粉是独立物品 `turmeric`（K5，界限见 [additives/10 §1.5](../additives/10-purity-jei-ae2.md)，手工 3% 为食品级）；姜黄素本身从 B 起。
> - 动物脂改为混合油脂 `animal_fat`（不带纯度）：71 用的硬脂酸按第四批 276 从动物脂脂肪酸压榨、精馏得到，粗硬脂酸含棕榈酸（[additives/15](../additives/15-batch-4.md)）；第四批暂无实装计划，这条中间体路线在本计划先做。
> - 77 B 用氢氰酸，为可选路线；用氢氰酸、光气的可选路线全部注册，不设配置项禁用（[additives/11 §待定问题 5](../additives/11-roadmap.md)，2026-11-05）。
> - 不带纯度的物料（2026-11-05，[additives/10 §1.1](../additives/10-purity-jei-ae2.md)）：酶与酶源（麦芽、大麦麦芽、木瓜乳汁等）、蒸馏水、多组分混合物（苦卤、灰、碱液、硝水、发酵液、天然油脂、松脂、焦油、原油等）。这些物料的产出不写 `Purity`，配方不对它们设门槛，它们也不参与起点纯度；输入全部不带纯度时产物按来源方式取值。

## 目标

第二批 48 项可做，石化线（乙烯酮、乙烯）起步。

## 范围

- 做：物料、路线、P57–P65 涉及的模式与模块配方

## 清单

| 编号 | 名称 | 最早阶段 | 物料 ID（09） | 路线（原文档） | 状态 |
| --- | --- | --- | --- | --- | --- |
| 31 | 山梨酸钾 | T3 | `potassium_sorbate` | [additives/13 §防腐剂（31–35）](../additives/13-batch-2.md) | 未开始 |
| 32 | 丙酸钙 | T2 | `calcium_propionate` | [additives/13 §防腐剂（31–35）](../additives/13-batch-2.md) | 未开始 |
| 33 | 双乙酸钠 | T2 | `sodium_diacetate` | [additives/13 §防腐剂（31–35）](../additives/13-batch-2.md) | 未开始 |
| 34 | 乳酸链球菌素 | T4 | `nisin` | [additives/13 §防腐剂（31–35）](../additives/13-batch-2.md) | 未开始 |
| 35 | 纳他霉素 | T4 | `natamycin` | [additives/13 §防腐剂（31–35）](../additives/13-batch-2.md) | 未开始 |
| 36 | 甜菊糖苷 | T0 | — | [additives/13 §甜味剂（36–41）](../additives/13-batch-2.md) | 未开始 |
| 37 | 木糖醇 | T3 | `xylitol` | [additives/13 §甜味剂（36–41）](../additives/13-batch-2.md) | 未开始 |
| 38 | 赤藓糖醇 | T4 | `erythritol` | [additives/13 §甜味剂（36–41）](../additives/13-batch-2.md) | 未开始 |
| 39 | 麦芽糖醇 | T3 | `maltitol` | [additives/13 §甜味剂（36–41）](../additives/13-batch-2.md) | 未开始 |
| 40 | D-甘露糖醇 | T2 | `mannitol` | [additives/13 §甜味剂（36–41）](../additives/13-batch-2.md) | 未开始 |
| 41 | 糖精钠 | T3 | `sodium_saccharin` | [additives/13 §甜味剂（36–41）](../additives/13-batch-2.md) | 未开始 |
| 42 | 卡拉胶 | T1 | `carrageenan` | [additives/13 §增稠剂（42–48）](../additives/13-batch-2.md) | 未开始 |
| 43 | 黄原胶 | T4 | `xanthan_gum` | [additives/13 §增稠剂（42–48）](../additives/13-batch-2.md) | 未开始 |
| 44 | 结冷胶 | T4 | `gellan_gum` | [additives/13 §增稠剂（42–48）](../additives/13-batch-2.md) | 未开始 |
| 45 | 羧甲基纤维素钠 | T3 | `cmc_unit` | [additives/13 §增稠剂（42–48）](../additives/13-batch-2.md) | 未开始 |
| 46 | 酸处理淀粉 | T2 | — | [additives/13 §增稠剂（42–48）](../additives/13-batch-2.md) | 未开始 |
| 47 | 羟丙基淀粉 | T3 | — | [additives/13 §增稠剂（42–48）](../additives/13-batch-2.md) | 未开始 |
| 48 | 醋酸酯淀粉 | T3 | — | [additives/13 §增稠剂（42–48）](../additives/13-batch-2.md) | 未开始 |
| 49 | 磷酸三钙 | T2 | `tricalcium_phosphate` | [additives/13 §磷酸盐（49–53）](../additives/13-batch-2.md) | 未开始 |
| 50 | 磷酸氢二钠 | T2 | `disodium_phosphate` | [additives/13 §磷酸盐（49–53）](../additives/13-batch-2.md) | 未开始 |
| 51 | 三聚磷酸钠 | T3 | `sodium_tripolyphosphate` | [additives/13 §磷酸盐（49–53）](../additives/13-batch-2.md) | 未开始 |
| 52 | 多聚磷酸钠（含六偏磷酸钠） | T2 | — | [additives/13 §磷酸盐（49–53）](../additives/13-batch-2.md) | 未开始 |
| 53 | 焦磷酸钠 | T2 | `tetrasodium_pyrophosphate` | [additives/13 §磷酸盐（49–53）](../additives/13-batch-2.md) | 未开始 |
| 54 | L(+)-酒石酸 | T1 | `tartaric_acid` | [additives/13 §酸度调节剂（54–58）](../additives/13-batch-2.md) | 未开始 |
| 55 | 酒石酸氢钾（塔塔粉） | T0 | `potassium_bitartrate` | [additives/13 §酸度调节剂（54–58）](../additives/13-batch-2.md) | 未开始 |
| 56 | 苹果酸 | T3 | `malic_acid`（DL）、`l_malic_acid`（L） | [additives/13 §酸度调节剂（54–58）](../additives/13-batch-2.md) | 未开始 |
| 57 | 富马酸 | T3 | `fumaric_acid` | [additives/13 §酸度调节剂（54–58）](../additives/13-batch-2.md) | 未开始 |
| 58 | 柠檬酸钠 | T2 | `trisodium_citrate` | [additives/13 §酸度调节剂（54–58）](../additives/13-batch-2.md) | 未开始 |
| 59 | 番茄红素 | T2 | `lycopene` | [additives/13 §着色剂（59–64）](../additives/13-batch-2.md) | 未开始 |
| 60 | 辣椒红 | T0 | — | [additives/13 §着色剂（59–64）](../additives/13-batch-2.md) | 未开始 |
| 61 | 姜黄素 | T2 | `curcumin`（A 的姜黄粉为 `turmeric`） | [additives/13 §着色剂（59–64）](../additives/13-batch-2.md) | 未开始 |
| 62 | 栀子黄 | T0 | — | [additives/13 §着色剂（59–64）](../additives/13-batch-2.md) | 未开始 |
| 63 | 核黄素（维生素 B2） | T4 | `riboflavin` | [additives/13 §着色剂（59–64）](../additives/13-batch-2.md) | 未开始 |
| 64 | 焦糖色（加氨生产、亚硫酸铵法） | T3 | `caramel` | [additives/13 §着色剂（59–64）](../additives/13-batch-2.md) | 未开始 |
| 65 | 生育酚（维生素 E） | T3 | — | [additives/13 §抗氧化剂（65–69）](../additives/13-batch-2.md) | 未开始 |
| 66 | 茶多酚 | T1 | — | [additives/13 §抗氧化剂（65–69）](../additives/13-batch-2.md) | 未开始 |
| 67 | 迷迭香提取物 | T2 | — | [additives/13 §抗氧化剂（65–69）](../additives/13-batch-2.md) | 未开始 |
| 68 | 抗坏血酸钠 | T2 | `sodium_ascorbate` | [additives/13 §抗氧化剂（65–69）](../additives/13-batch-2.md) | 未开始 |
| 69 | 磷脂（卵磷脂） | T1 | `lecithin` | [additives/13 §抗氧化剂（65–69）](../additives/13-batch-2.md) | 未开始 |
| 70 | 蔗糖脂肪酸酯 | T3 | — | [additives/13 §乳化剂（70–72）](../additives/13-batch-2.md) | 未开始 |
| 71 | 山梨醇酐单硬脂酸酯（司盘 60） | T2 | `sorbitan_monostearate` | [additives/13 §乳化剂（70–72）](../additives/13-batch-2.md) | 未开始 |
| 72 | 聚氧乙烯(20)山梨醇酐单油酸酯（吐温 80） | T3 | — | [additives/13 §乳化剂（70–72）](../additives/13-batch-2.md) | 未开始 |
| 73 | 5'-呈味核苷酸二钠 | T4 | — | [additives/13 §增味剂、其他（73–78）](../additives/13-batch-2.md) | 未开始 |
| 74 | 氯化钙 | T2 | `calcium_chloride` | [additives/13 §增味剂、其他（73–78）](../additives/13-batch-2.md) | 未开始 |
| 75 | 氯化钾 | T1 | `potassium_chloride` | [additives/13 §增味剂、其他（73–78）](../additives/13-batch-2.md) | 未开始 |
| 76 | 二氧化硅 | T3 | `silicon_dioxide` | [additives/13 §增味剂、其他（73–78）](../additives/13-batch-2.md) | 未开始 |
| 77 | 亚铁氰化钾（黄血盐） | T1 | `potassium_ferrocyanide` | [additives/13 §增味剂、其他（73–78）](../additives/13-batch-2.md) | 未开始 |
| 78 | 丙三醇（甘油）、丙二醇 | T2 | `glycerol` | [additives/13 §增味剂、其他（73–78）](../additives/13-batch-2.md) | 未开始 |

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
- [ ] [additives/13 §新增原料](../additives/13-batch-2.md) 中的原料都有获取途径

## 待定

- 无
