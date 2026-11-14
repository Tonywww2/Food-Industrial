# I18 第一批添加剂内容（1–30）

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | E 内容 |
| 依赖 | I05、I06、I12、I13、I14 |
| 原文档 | [additives/03](../additives/03-first-batch.md)、[additives/04](../additives/04-production-routes.md)、[additives/05 §A–E](../additives/05-processes.md)、[additives/07](../additives/07-manual-crafting.md)、[additives/09](../additives/09-chemical-formulas.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `content.batch1`（数据生成） |

> 2026-11-10：[additives/07](../additives/07-manual-crafting.md) 两张产品表的 T0 手工链（本批 1、2、5、7、11、12、14、18、24、25 号与海盐）已在 [I12](I12-t0-manual-devices.md) 实装（用户确认 I12 做完全部手工链），本计划只做 T1 起的路线。
>
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
| — | 共用中间体（硫酸、硝酸、盐酸、氮气、乙醛、蒸馏水等） | T1–T3 | 见 09 | [additives/04 §共用中间体与基础化学品](../additives/04-production-routes.md) | 已完成 |
| 1 | 冰乙酸（冰醋酸） | T0（最终 T3） | — | [additives/04 §1](../additives/04-production-routes.md) | 已完成 |
| 2 | 乳酸 | T0（最终 T4） | `lactic_acid` | [additives/04 §2](../additives/04-production-routes.md) | 已完成 |
| 3 | 柠檬酸 | T1（最终 T4） | `citric_acid` | [additives/04 §3](../additives/04-production-routes.md) | 已完成 |
| 4 | 谷氨酸钠（味精） | T2（最终 T4） | `monosodium_glutamate` | [additives/04 §4](../additives/04-production-routes.md) | 已完成 |
| 5 | 碳酸钠（纯碱） | T0（最终 T3） | `soda_ash` | [additives/04 §5](../additives/04-production-routes.md) | 已完成 |
| 6 | 碳酸氢钠（小苏打） | T1（最终 T3） | `sodium_bicarbonate` | [additives/04 §6](../additives/04-production-routes.md) | 已完成 |
| 7 | 碳酸钾 | T0（最终 T3） | `potassium_carbonate` | [additives/04 §7](../additives/04-production-routes.md) | 已完成 |
| 8 | 碳酸氢铵（臭粉） | T1（最终 T3） | `ammonium_bicarbonate` | [additives/04 §8](../additives/04-production-routes.md) | 已完成 |
| 9 | 硫酸铝钾（明矾） | T1（最终 T3） | `potassium_alum` | [additives/04 §9](../additives/04-production-routes.md) | 已完成 |
| 10 | 焦磷酸二氢二钠 | T2（最终 T3） | `sodium_acid_pyrophosphate` | [additives/04 §10](../additives/04-production-routes.md) | 已完成 |
| 11 | 氯化镁（卤片） | T0（最终 T3） | `magnesium_chloride` | [additives/04 §11](../additives/04-production-routes.md) | 已完成 |
| 12 | 硫酸钙（石膏） | T0（最终 T2） | `gypsum` | [additives/04 §12](../additives/04-production-routes.md) | 已完成 |
| 13 | 葡萄糖酸-δ-内酯（内酯） | T3（最终 T4） | `glucono_delta_lactone` | [additives/04 §13](../additives/04-production-routes.md) | 已完成 |
| 14 | 琼脂 | T0（最终 T2） | — | [additives/04 §14](../additives/04-production-routes.md) | 已完成 |
| 15 | 海藻酸钠 | T2（最终 T2） | `sodium_alginate` | [additives/04 §15](../additives/04-production-routes.md) | 已完成 |
| 16 | 果胶 | T1（最终 T2） | `pectin` | [additives/04 §16](../additives/04-production-routes.md) | 已完成 |
| 17 | 明胶 | T1（最终 T3） | `gelatin` | [additives/04 §17](../additives/04-production-routes.md) | 已完成 |
| 18 | 焦糖色（普通法） | T0（最终 T2） | `caramel` | [additives/04 §18](../additives/04-production-routes.md) | 已完成 |
| 19 | 甜菜红 | T1（最终 T2） | — | [additives/04 §19](../additives/04-production-routes.md) | 已完成 |
| 20 | β-胡萝卜素 | T2（最终 T4） | `beta_carotene` | [additives/04 §20](../additives/04-production-routes.md) | 已完成 |
| 21 | 红曲米 / 红曲红 | T1（最终 T4） | — | [additives/04 §21](../additives/04-production-routes.md) | 已完成 |
| 22 | 叶绿素铜钠盐 | T2（最终 T2） | `sodium_copper_chlorophyllin` | [additives/04 §22](../additives/04-production-routes.md) | 已完成 |
| 23 | 苯甲酸钠 | T3（最终 T3） | `sodium_benzoate` | [additives/04 §23](../additives/04-production-routes.md) | 已完成 |
| 24 | 亚硝酸钠 / 硝酸钾（硝石） | T0（最终 T3） | `sodium_nitrite` | [additives/04 §24](../additives/04-production-routes.md) | 已完成 |
| 25 | 焦亚硫酸钠 / 二氧化硫 | T0（最终 T2） | `sodium_metabisulfite` | [additives/04 §25](../additives/04-production-routes.md) | 已完成 |
| 26 | 山梨糖醇 | T2（最终 T3） | `sorbitol` | [additives/04 §26](../additives/04-production-routes.md) | 已完成 |
| 27 | 单，双甘油脂肪酸酯（单甘酯） | T2（最终 T4） | — | [additives/04 §27](../additives/04-production-routes.md) | 已完成 |
| 28 | 抗坏血酸（维生素 C） | T4（最终 T4） | `ascorbic_acid` | [additives/04 §28](../additives/04-production-routes.md) | 已完成 |
| 29 | l-薄荷醇（薄荷脑） | T1（最终 T3） | `menthol` | [additives/04 §29](../additives/04-production-routes.md) | 已完成 |
| 30 | 香兰素 | T1（最终 T3） | `vanillin` | [additives/04 §30](../additives/04-production-routes.md) | 已完成 |

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
- [x] [additives/03 §清单](../additives/03-first-batch.md) 中每项的“最早”“最终”阶段都能做到

## 实现记录

- 代码：`content`（`ContentRecipe` 配方写法、`Batch` 共用步骤：重结晶、活性炭脱色、树脂吸附、吸附柱吸附与离子交换、菌种纯化与扩培；`Cultures` 菌种工作台的平板分离、诱变、纯化、扩培；`ModContent`）、`content.batch1.Batch1Recipes`（约 190 条：共用中间体、催化剂、1–30 号 T1 起的全部路线）
- 框架补充：配方原料可写纯度门槛（`标签|档位`，`RecipeGen`）；同内容多条配方条件一样多时原料多的优先（`ProcessRecipe.specificity`，machines/02、additives/07 已改）；平板配方只挑一个菌落，产物数不受产物槽数限制（`DeviceSpec`）；纯度报表把没有配方产出的原料（矿物、他模组物品、刮下的酒石）按外部默认值计，并增加“最早可做出的档次”
- 物料补登（09、08）：硝酸、木醋液、薄荷原油、果胶液（K0，含量 1%）、红曲米（K5，覆盖界限）、甜菜原汁、泡打粉（K12）；催化剂原料标签钴、铂、铑、钯、钒（他模组提供）
- 调整：I12 的“杂菌”配方加条件“空气”，曲盘有此能力（行为不变），无菌的菌种工作台因此不长杂菌（07 已改）
- 验证：三个节点数据生成与单元测试通过；纯度报表的最早档次与 03 对照见下；游戏内抽查见下
- 最早档次（报表）与 03 一致的：1 T0、2 T0、3 T1、4 T2、5 T0、6 T1、7 T0、8 T1、9 T1、10 T2、11 T1、12 T0、13 T3、14 T0、16 T1（果胶液）、17 T1、18 T0、19 T1、20 T2、21 T1、22 T2、23 T3、24 T0、25 T2、26 T3、27 T2、28 T4、29 T1、30 T1；15 为 T2（氯化钙由石灰石与盐酸得到，补了这条配方）
- 游戏内抽查（2026-11-11，1.20.1 `runServer`，`/fidev machine … set run`）：T1 煮锅苛化（纯碱 90%、熟石灰 95%）→ 氢氧化钠 2 个 96.3%、碳酸钙 96.3%；T1 结晶罐冷却卤水 → 光卤石 90.6%；T2 搅拌反应罐骨头 + 熟石灰 + 盐酸 → 明胶 61%、磷酸氢钙 90.6%；T2 蒸发结晶器（真空模块）浓缩甜菜原汁 → 甜菜苷（含量 0.15%）；T2 混合机小苏打、焦磷酸二氢二钠（99.5%）、淀粉 → 复配膨松剂 3 个 95.2%；T3 高压反应釜葡萄糖加氢（镍催化剂）→ 山梨糖醇液 96.3%；T3 电解槽食盐水 → 氢氧化钠 90.6%、氢气与氯气 95.2%；T4 菌种工作台泥土平板接种、培养、鉴定后挑菌 → 褐黄孢链霉菌 20%，谷氨酸棒杆菌（99.5%）加葡萄糖（95%）扩培 → 2 个 99.5%（只按菌种计，葡萄糖不拉低）；漏斗向菌种工作台送物按标签进对应槽，盐被拒
- 2026-11-12 审定（11 问题 43）：乙酸钴由他模组的钴 + 冰乙酸 2 份（门槛 90%）在 M14 加热、通空气溶解，出乙酸钴 1 件与水（T2）；蒸米的配方本就引用 `materials/rice`，大米与稻谷分开后自然改用大米，稻谷先在研磨机碾米（I30）

## 2026-11-12 审定（11 问题 43）

设计侧审完本计划的待审四条（[additives/11 问题 43](../additives/11-roadmap.md)）。照准的：配方数值与合并方式写在源码注释，用纯度报表对照 03、13、14（设计侧建议数据生成另导一张“原料用量、来源纯度、时长”对照表，便于审到数值）；七个英文名；红曲米 30% / 45% / 55%（09 §D 已记）；样本标签收的种类。要改的：

- [x] 钴盐的配方改为乙酸钴：他模组的钴（`catalysts/cobalt` 按标签匹配）+ 冰乙酸，M14 通空气溶解 → M15 结晶（[additives/08 §C 催化剂与助剂](../additives/08-raw-materials.md)）
- [x] 蒸米改用大米：原写稻谷，稻谷先碾米（[additives/08 §C](../additives/08-raw-materials.md)）；碾米配方见 [I30](I30-content-batch-4.md)
- [ ] 其余九种催化剂的配方（镍、铁、铑碘、铂网、五氧化二钒、金 / 钯、铜、活性炭、银）设计侧下一轮对照源码逐个核对，标准是每种都从它的金属或盐加载体做出来

## 2026-11-12 审定（二）（11 问题 44）

- [x] 乙酸钴把结晶并进 M14 那一条、直接产出物品：照准。结晶、过滤、干燥这类不改变物料的工序本来就并入相邻步骤（[additives/04 §实现约定](../additives/04-production-routes.md)、11 问题 40）

## 待审（实现时拟定，请确认）

- 无。2026-11-12 的一条已审定（11 问题 44），处理见上节。

## 待定

- 27 C 单甘酯分子蒸馏没做：精馏塔只收流体，单甘酯是固体物品
- 氮气、氧气的深冷分离没做：精馏塔没有进气口，也没有“空气”流体；T2 用吸收塔燃烧除氧得氮气，T3 电解水得氧气
- 酿酒酵母只能在菌种工作台从水果样本分离（T4）；07 的自然接种没有酵母，葡萄糖、蔗糖发酵制乙醇实际要到 T4 才有菌种（乙醇本身 T1 可由果酒蒸馏得到）
- 依赖菌种工作台分离的菌种（黑曲霉、丙酸杆菌等）使对应的 T1–T2 发酵路线实际从 T4 起
- 红曲米是唯一保留随机纯度的物料（[additives/07 §作坊阶段（T1）的特殊随机](../additives/07-manual-crafting.md)）
