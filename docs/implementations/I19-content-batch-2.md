# I19 第二批添加剂内容（31–78）

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | E 内容 |
| 依赖 | I18、I15、I16 |
| 原文档 | [additives/13](../additives/13-batch-2.md)、[additives/05 §F](../additives/05-processes.md)、[additives/09](../additives/09-chemical-formulas.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `content.batch2`（数据生成） |

> 2026-11-10：[additives/07](../additives/07-manual-crafting.md) 的 T0 手工链（本批 36、55、60、61、62、69、84 号）已在 [I12](I12-t0-manual-devices.md) 实装，本计划只做 T1 起的路线。
>
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
| 31 | 山梨酸钾 | T3 | `potassium_sorbate` | [additives/13 §防腐剂（31–35）](../additives/13-batch-2.md) | 已完成 |
| 32 | 丙酸钙 | T2 | `calcium_propionate` | [additives/13 §防腐剂（31–35）](../additives/13-batch-2.md) | 已完成 |
| 33 | 双乙酸钠 | T2 | `sodium_diacetate` | [additives/13 §防腐剂（31–35）](../additives/13-batch-2.md) | 已完成 |
| 34 | 乳酸链球菌素 | T4 | `nisin` | [additives/13 §防腐剂（31–35）](../additives/13-batch-2.md) | 已完成 |
| 35 | 纳他霉素 | T4 | `natamycin` | [additives/13 §防腐剂（31–35）](../additives/13-batch-2.md) | 已完成 |
| 36 | 甜菊糖苷 | T0 | — | [additives/13 §甜味剂（36–41）](../additives/13-batch-2.md) | 已完成 |
| 37 | 木糖醇 | T3 | `xylitol` | [additives/13 §甜味剂（36–41）](../additives/13-batch-2.md) | 已完成 |
| 38 | 赤藓糖醇 | T4 | `erythritol` | [additives/13 §甜味剂（36–41）](../additives/13-batch-2.md) | 已完成 |
| 39 | 麦芽糖醇 | T3 | `maltitol` | [additives/13 §甜味剂（36–41）](../additives/13-batch-2.md) | 已完成 |
| 40 | D-甘露糖醇 | T2 | `mannitol` | [additives/13 §甜味剂（36–41）](../additives/13-batch-2.md) | 已完成 |
| 41 | 糖精钠 | T3 | `sodium_saccharin` | [additives/13 §甜味剂（36–41）](../additives/13-batch-2.md) | 已完成 |
| 42 | 卡拉胶 | T1 | `carrageenan` | [additives/13 §增稠剂（42–48）](../additives/13-batch-2.md) | 已完成 |
| 43 | 黄原胶 | T4 | `xanthan_gum` | [additives/13 §增稠剂（42–48）](../additives/13-batch-2.md) | 已完成 |
| 44 | 结冷胶 | T4 | `gellan_gum` | [additives/13 §增稠剂（42–48）](../additives/13-batch-2.md) | 已完成 |
| 45 | 羧甲基纤维素钠 | T3 | `cmc_unit` | [additives/13 §增稠剂（42–48）](../additives/13-batch-2.md) | 已完成 |
| 46 | 酸处理淀粉 | T2 | — | [additives/13 §增稠剂（42–48）](../additives/13-batch-2.md) | 已完成 |
| 47 | 羟丙基淀粉 | T3 | — | [additives/13 §增稠剂（42–48）](../additives/13-batch-2.md) | 已完成 |
| 48 | 醋酸酯淀粉 | T3 | — | [additives/13 §增稠剂（42–48）](../additives/13-batch-2.md) | 已完成 |
| 49 | 磷酸三钙 | T2 | `tricalcium_phosphate` | [additives/13 §磷酸盐（49–53）](../additives/13-batch-2.md) | 已完成 |
| 50 | 磷酸氢二钠 | T2 | `disodium_phosphate` | [additives/13 §磷酸盐（49–53）](../additives/13-batch-2.md) | 已完成 |
| 51 | 三聚磷酸钠 | T3 | `sodium_tripolyphosphate` | [additives/13 §磷酸盐（49–53）](../additives/13-batch-2.md) | 已完成 |
| 52 | 多聚磷酸钠（含六偏磷酸钠） | T2 | — | [additives/13 §磷酸盐（49–53）](../additives/13-batch-2.md) | 已完成 |
| 53 | 焦磷酸钠 | T2 | `tetrasodium_pyrophosphate` | [additives/13 §磷酸盐（49–53）](../additives/13-batch-2.md) | 已完成 |
| 54 | L(+)-酒石酸 | T1 | `tartaric_acid` | [additives/13 §酸度调节剂（54–58）](../additives/13-batch-2.md) | 已完成 |
| 55 | 酒石酸氢钾（塔塔粉） | T0 | `potassium_bitartrate` | [additives/13 §酸度调节剂（54–58）](../additives/13-batch-2.md) | 已完成 |
| 56 | 苹果酸 | T3 | `malic_acid`（DL）、`l_malic_acid`（L） | [additives/13 §酸度调节剂（54–58）](../additives/13-batch-2.md) | 已完成 |
| 57 | 富马酸 | T3 | `fumaric_acid` | [additives/13 §酸度调节剂（54–58）](../additives/13-batch-2.md) | 已完成 |
| 58 | 柠檬酸钠 | T2 | `trisodium_citrate` | [additives/13 §酸度调节剂（54–58）](../additives/13-batch-2.md) | 已完成 |
| 59 | 番茄红素 | T2 | `lycopene` | [additives/13 §着色剂（59–64）](../additives/13-batch-2.md) | 已完成 |
| 60 | 辣椒红 | T0 | — | [additives/13 §着色剂（59–64）](../additives/13-batch-2.md) | 已完成 |
| 61 | 姜黄素 | T2 | `curcumin`（A 的姜黄粉为 `turmeric`） | [additives/13 §着色剂（59–64）](../additives/13-batch-2.md) | 已完成 |
| 62 | 栀子黄 | T0 | — | [additives/13 §着色剂（59–64）](../additives/13-batch-2.md) | 已完成 |
| 63 | 核黄素（维生素 B2） | T4 | `riboflavin` | [additives/13 §着色剂（59–64）](../additives/13-batch-2.md) | 已完成 |
| 64 | 焦糖色（加氨生产、亚硫酸铵法） | T3 | `caramel` | [additives/13 §着色剂（59–64）](../additives/13-batch-2.md) | 已完成 |
| 65 | 生育酚（维生素 E） | T3 | — | [additives/13 §抗氧化剂（65–69）](../additives/13-batch-2.md) | 已完成 |
| 66 | 茶多酚 | T1 | — | [additives/13 §抗氧化剂（65–69）](../additives/13-batch-2.md) | 已完成 |
| 67 | 迷迭香提取物 | T2 | — | [additives/13 §抗氧化剂（65–69）](../additives/13-batch-2.md) | 已完成 |
| 68 | 抗坏血酸钠 | T2 | `sodium_ascorbate` | [additives/13 §抗氧化剂（65–69）](../additives/13-batch-2.md) | 已完成 |
| 69 | 磷脂（卵磷脂） | T1 | `lecithin` | [additives/13 §抗氧化剂（65–69）](../additives/13-batch-2.md) | 已完成 |
| 70 | 蔗糖脂肪酸酯 | T3 | — | [additives/13 §乳化剂（70–72）](../additives/13-batch-2.md) | 已完成 |
| 71 | 山梨醇酐单硬脂酸酯（司盘 60） | T2 | `sorbitan_monostearate` | [additives/13 §乳化剂（70–72）](../additives/13-batch-2.md) | 已完成 |
| 72 | 聚氧乙烯(20)山梨醇酐单油酸酯（吐温 80） | T3 | — | [additives/13 §乳化剂（70–72）](../additives/13-batch-2.md) | 已完成 |
| 73 | 5'-呈味核苷酸二钠 | T4 | — | [additives/13 §增味剂、其他（73–78）](../additives/13-batch-2.md) | 已完成 |
| 74 | 氯化钙 | T2 | `calcium_chloride` | [additives/13 §增味剂、其他（73–78）](../additives/13-batch-2.md) | 已完成 |
| 75 | 氯化钾 | T1 | `potassium_chloride` | [additives/13 §增味剂、其他（73–78）](../additives/13-batch-2.md) | 已完成 |
| 76 | 二氧化硅 | T3 | `silicon_dioxide` | [additives/13 §增味剂、其他（73–78）](../additives/13-batch-2.md) | 已完成 |
| 77 | 亚铁氰化钾（黄血盐） | T1 | `potassium_ferrocyanide` | [additives/13 §增味剂、其他（73–78）](../additives/13-batch-2.md) | 已完成 |
| 78 | 丙三醇（甘油）、丙二醇 | T2 | `glycerol` | [additives/13 §增味剂、其他（73–78）](../additives/13-batch-2.md) | 已完成 |

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
- [x] [additives/13 §新增原料](../additives/13-batch-2.md) 中的原料都有获取途径

## 实现记录

- 代码：`content.batch2.Batch2Recipes`（约 120 条：31–78 号 T1 起的路线，石化起步：原油切割与裂解、乙醇脱水制乙烯、乙酸裂解制乙烯酮；树脂、钒磷氧、银、氯化锌催化剂）；菌种在 `content.Cultures`
- 物料补登（09、08）：酸处理淀粉、羟丙基淀粉、醋酸酯淀粉、多聚磷酸钠（六偏磷酸钠）、吐温 80、精炼大豆油；36 号用 `steviol_glycosides`，60 号 `capsanthin`，62 号 `gardenia_yellow`，65 号 `alpha_tocopherol`，66 号 `epigallocatechin_gallate`（09 注“茶多酚主成分”），67 号 `carnosic_acid`（09 注“迷迭香提取物”），70 号 `sucrose_monostearate`，73 号为 `inosinic_disodium` 与 `guanylic_disodium` 两种
- 验证：三个节点数据生成与单元测试通过；报表最早档次与 13 一致的：31 T3、33 T2、34 T4、35 T4、36 T0、37 T3、38 T4、39 T3、40 T2、41 T3、42 T1、43 T4、44 T4、45 T3、46 T2、47 T3、48 T3、49 T2、50 T2、52 T2、53 T2、54 T1、55 T0、56 T3、57 T3、58 T2、59 T2、60 T0、61 T2、62 T0、63 T4、64 T3、65 T3、66 T1、67 T2、69 T1、72 T3、73 T4、74 T2、75 T1、78 T2
- 与 13 不同的：32 丙酸钙 T3（丙酸杆菌要在菌种工作台分离，T4；T3 由乙烯羰基化）；51 三聚磷酸钠 T2（回转窑缩合，13 为 T3）；76 二氧化硅 T2（水玻璃在回转窑熔融，13 为 T3）；68 抗坏血酸钠、71 司盘 60 随原料（抗坏血酸 T4、山梨糖醇 T3）；70 蔗糖酯 T2（甲醇来自木醋液）；77 黄血盐 T2（三种原料，土窑只有两个输入槽，改在回转窑）
- 游戏内抽查与 I18 一起做，见 [I18 实现记录](I18-content-batch-1.md)
- 2026-11-12 审定（11 问题 43）：糖精两条氧化都在（高锰酸钾一条；氧气 + `catalysts/cobalt` 加压一条，标签现只有乙酸钴）；离子交换树脂改为聚苯乙烯 4 件 + 硫酸 1 桶在 M14 耐腐蚀内衬加热磺化出 4 件，大孔吸附树脂改为苯乙烯 1 桶 + 甲苯 500 mB + 水 1 桶在 M14 聚合模式加热悬浮聚合出 4 件、回收甲苯 450 mB（汽提并入）

## 2026-11-12 审定（11 问题 43）

设计侧审完本计划的待审三条（[additives/11 问题 43](../additives/11-roadmap.md)）。照准的：实现约定、配方数值、六个英文名。要改的：

- [x] 糖精的氧化两条都写：高锰酸钾一条（第四批已有来源）与氧气 + 乙酸钴一条，输入不同不冲突；原文“高锰酸钾暂无来源”作废
- [x] 钴盐改名乙酸钴（见 [I05](I05-materials-registry.md)），本计划引用处随之改

- [x] 两种树脂的单体改为苯乙烯（2026-11-12 批准，11 问题 43；原写苯 + 乙烯聚合）：离子交换树脂由聚苯乙烯（第五批 441 `catalysts/polystyrene`）+ 硫酸在 M14（耐腐蚀内衬）磺化，季铵型的氯甲基化与胺化并入同一步；大孔吸附树脂由苯乙烯（`styrene`）+ 甲苯（致孔剂）+ 水在 M14 聚合模式悬浮聚合，M22 汽提回收甲苯。二乙烯苯交联剂省略作游戏简化；两条配方的输入与 441 聚苯乙烯（只有苯乙烯、本体聚合）不同，不冲突。路线见 [additives/08 §C 催化剂与助剂](../additives/08-raw-materials.md)

## 待审（实现时拟定，请确认）

- 无。2026-11-12 的三条已审定（11 问题 43），处理见上节。

## 待定

- 59 B 三孢布拉霉发酵番茄红素没做：原料与 20 B 的 β-胡萝卜素相同，缺“环化抑制剂”物料，同样原料只会选中一条配方
- 原油、丁烷、丙烯依赖他模组（原油暂无本模组来源，11 待定问题 13）；乙烯另可由乙醇脱水得到
