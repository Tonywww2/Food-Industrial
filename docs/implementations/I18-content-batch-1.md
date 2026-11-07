# I18 第一批添加剂内容（1–30）

| 项 | 内容 |
| --- | --- |
| 状态 | 未开始 |
| 阶段 | E 内容 |
| 依赖 | I05、I06、I12、I13、I14 |
| 原文档 | [additives/03](../additives/03-first-batch.md)、[additives/04](../additives/04-production-routes.md)、[additives/05 §A–E](../additives/05-processes.md)、[additives/07](../additives/07-manual-crafting.md)、[additives/09](../additives/09-chemical-formulas.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `content.batch1`（数据生成） |

> 纯度模型已改为 v2（2026-11-04）：本计划中与 [I28](I28-purity-model-v2.md) 冲突的条目以 I28 为准。
>
> 2026-11-05 设计调整（以原文档为准）：
> - 不带纯度的物料（2026-11-05，[additives/10 §1.1](../additives/10-purity-jei-ae2.md)）：酶与酶源（麦芽、大麦麦芽、木瓜乳汁等）、蒸馏水、多组分混合物（苦卤、灰、碱液、硝水、发酵液、天然油脂、松脂、焦油、原油等）。这些物料的产出不写 `Purity`，配方不对它们设门槛，它们也不参与起点纯度；输入全部不带纯度时产物按来源方式取值。
> - 27 用的动物脂改为混合油脂 `animal_fat`（不带纯度）；`tristearin` 只作硬化油代表物（[additives/09](../additives/09-chemical-formulas.md)）。
> - 10 C 热法磷酸的炉渣为 `phosphorus_slag`（不带纯度）；`calcium_silicate` 只指食品硅酸钙。
> - 11 用的 `magnesite` 改名“菱镁矿粉”；食品碳酸镁是另一物料 `magnesium_carbonate`。
> - 共用中间体增加蒸馏水 `distilled_water`：M08 蒸馏，M25 离子交换、M31 反渗透同产，不带纯度。
> - 用氢氰酸、光气的可选路线全部注册，不设配置项禁用（[additives/11 §待定问题 5](../additives/11-roadmap.md)，2026-11-05）；本批涉及 2 D。
> - 精制配方的 `t0_max` 与复配产品的 K12 规则见 [I29](I29-purity-caps-compound.md)；装规范化模块的设备按档位下限输出（I15）。

## 目标

第一批 30 项从 T0 到最终阶段全部可做，共用中间体先行。

## 范围

- 做：共用中间体与 30 项添加剂的物料、配方、门槛
- 不做：T2 以上机器尚未完成时，相应路线先登记、后补

## 清单

| 编号 | 名称 | 最早阶段 | 物料 ID（09） | 路线（原文档） | 状态 |
| --- | --- | --- | --- | --- | --- |
| — | 共用中间体（硫酸、硝酸、盐酸、氮气、乙醛、蒸馏水等） | T1–T3 | 见 09 | [additives/04 §共用中间体与基础化学品](../additives/04-production-routes.md) | 未开始 |
| 1 | 冰乙酸（冰醋酸） | T0（最终 T3） | — | [additives/04 §1](../additives/04-production-routes.md) | 未开始 |
| 2 | 乳酸 | T0（最终 T4） | `lactic_acid` | [additives/04 §2](../additives/04-production-routes.md) | 未开始 |
| 3 | 柠檬酸 | T1（最终 T4） | `citric_acid` | [additives/04 §3](../additives/04-production-routes.md) | 未开始 |
| 4 | 谷氨酸钠（味精） | T2（最终 T4） | `monosodium_glutamate` | [additives/04 §4](../additives/04-production-routes.md) | 未开始 |
| 5 | 碳酸钠（纯碱） | T0（最终 T3） | `soda_ash` | [additives/04 §5](../additives/04-production-routes.md) | 未开始 |
| 6 | 碳酸氢钠（小苏打） | T1（最终 T3） | `sodium_bicarbonate` | [additives/04 §6](../additives/04-production-routes.md) | 未开始 |
| 7 | 碳酸钾 | T0（最终 T3） | `potassium_carbonate` | [additives/04 §7](../additives/04-production-routes.md) | 未开始 |
| 8 | 碳酸氢铵（臭粉） | T1（最终 T3） | `ammonium_bicarbonate` | [additives/04 §8](../additives/04-production-routes.md) | 未开始 |
| 9 | 硫酸铝钾（明矾） | T1（最终 T3） | `potassium_alum` | [additives/04 §9](../additives/04-production-routes.md) | 未开始 |
| 10 | 焦磷酸二氢二钠 | T2（最终 T3） | `sodium_acid_pyrophosphate` | [additives/04 §10](../additives/04-production-routes.md) | 未开始 |
| 11 | 氯化镁（卤片） | T0（最终 T3） | `magnesium_chloride` | [additives/04 §11](../additives/04-production-routes.md) | 未开始 |
| 12 | 硫酸钙（石膏） | T0（最终 T2） | `gypsum` | [additives/04 §12](../additives/04-production-routes.md) | 未开始 |
| 13 | 葡萄糖酸-δ-内酯（内酯） | T3（最终 T4） | `glucono_delta_lactone` | [additives/04 §13](../additives/04-production-routes.md) | 未开始 |
| 14 | 琼脂 | T0（最终 T2） | — | [additives/04 §14](../additives/04-production-routes.md) | 未开始 |
| 15 | 海藻酸钠 | T2（最终 T2） | `sodium_alginate` | [additives/04 §15](../additives/04-production-routes.md) | 未开始 |
| 16 | 果胶 | T1（最终 T2） | `pectin` | [additives/04 §16](../additives/04-production-routes.md) | 未开始 |
| 17 | 明胶 | T1（最终 T3） | `gelatin` | [additives/04 §17](../additives/04-production-routes.md) | 未开始 |
| 18 | 焦糖色（普通法） | T0（最终 T2） | `caramel` | [additives/04 §18](../additives/04-production-routes.md) | 未开始 |
| 19 | 甜菜红 | T1（最终 T2） | — | [additives/04 §19](../additives/04-production-routes.md) | 未开始 |
| 20 | β-胡萝卜素 | T2（最终 T4） | `beta_carotene` | [additives/04 §20](../additives/04-production-routes.md) | 未开始 |
| 21 | 红曲米 / 红曲红 | T1（最终 T4） | — | [additives/04 §21](../additives/04-production-routes.md) | 未开始 |
| 22 | 叶绿素铜钠盐 | T2（最终 T2） | `sodium_copper_chlorophyllin` | [additives/04 §22](../additives/04-production-routes.md) | 未开始 |
| 23 | 苯甲酸钠 | T3（最终 T3） | `sodium_benzoate` | [additives/04 §23](../additives/04-production-routes.md) | 未开始 |
| 24 | 亚硝酸钠 / 硝酸钾（硝石） | T0（最终 T3） | `sodium_nitrite` | [additives/04 §24](../additives/04-production-routes.md) | 未开始 |
| 25 | 焦亚硫酸钠 / 二氧化硫 | T0（最终 T2） | `sodium_metabisulfite` | [additives/04 §25](../additives/04-production-routes.md) | 未开始 |
| 26 | 山梨糖醇 | T2（最终 T3） | `sorbitol` | [additives/04 §26](../additives/04-production-routes.md) | 未开始 |
| 27 | 单，双甘油脂肪酸酯（单甘酯） | T2（最终 T4） | — | [additives/04 §27](../additives/04-production-routes.md) | 未开始 |
| 28 | 抗坏血酸（维生素 C） | T4（最终 T4） | `ascorbic_acid` | [additives/04 §28](../additives/04-production-routes.md) | 未开始 |
| 29 | l-薄荷醇（薄荷脑） | T1（最终 T3） | `menthol` | [additives/04 §29](../additives/04-production-routes.md) | 未开始 |
| 30 | 香兰素 | T1（最终 T3） | `vanillin` | [additives/04 §30](../additives/04-production-routes.md) | 未开始 |

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
- [ ] [additives/03 §清单](../additives/03-first-batch.md) 中每项的“最早”“最终”阶段都能做到

## 待定

- 红曲米是唯一保留随机纯度的物料（[additives/07 §作坊阶段（T1）的特殊随机](../additives/07-manual-crafting.md)）
