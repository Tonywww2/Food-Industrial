# 09 物料与中间产物化学式（GT 联动）

前三批（[03](03-first-batch.md)、[13](13-batch-2.md)、[14](14-batch-3.md)）涉及的原料、中间体、副产物与合成产物共 **341 种**：306 种有确定化学式，其中 **300 种已由结构式（SMILES）经 RDKit 反算元素组成逐一核对一致**；另 6 种（高岭石、五氧化二钒、红斑素、红曲红素、叶绿素 a、叶绿素铜钠）为公认化学式，未做结构核对；35 种高分子或天然混合物只给组成描述。第四批（[15](15-batch-4.md)）另增 362 种（含 2026-11-05 拆分新增的 18 种）：中间体 146 种（备注“第四批：”加用途编号）、产物 198 种（备注“第四批 N 号”），化学式由反应式配平与人工核对，“核对”栏留空，待补 RDKit 核对；另有 31 种第四批产物沿用已有物料，备注补了编号。第五批（[17](17-batch-5.md)、[18](18-batch-5-flavors.md)、[19](19-batch-5-synthetic-flavors.md)）另增 470 种中间体与非香料产物（备注“第五批：”加用途、“第五批 N 号”），化学式全部由 SMILES 经 RDKit 核对（有结构的打 ✓）；香料 1801 种单列第 E 节。2026-11-12 另为第三、四批补碱熔中间体 2 种（`sodium_2_naphtholate`、`disodium_resorcinolate`，11 问题 42）；同日按 11 问题 43 把松脂改回 08 §C 的原料（不带纯度）、把乙酸钴由 08 §C 移入本表。表内按无机物、有机小分子、高分子分组，组内按批次先后排列。添加剂本身的化学式见 [02](02-gb2760-additive-list.md)。

## 表示约定

| 项 | 约定 | 例 |
| --- | --- | --- |
| 有机物 | Hill 式，C、H 在前，其余按字母序 | `C6H8O7`、`C5H8NNaO4` |
| 无机物 | 习惯写法，保留原子团括号 | `Ca(OH)2`、`KAl(SO4)2·12H2O` |
| 水合物 | `·nH2O`，n 可为小数 | `CaSO4·0.5H2O` |
| 高分子 | 重复单元 + n | `(C6H7NaO6)n` |
| 混合物 | 写主成分 | 苦卤：MgCl2、MgSO4、KCl、NaCl |
| 数字 | 不用下标，便于检索、写进 JSON、与 GTCEu 对照 | — |

本表与 08 §C 是物料主表（2026-11-04 定）：`tools/materials/export_materials.py` 把两处的表导出为 `src/main/resources/foodindustrial/materials.json`，注册物品与流体时读取；新增物料只改表。“英文名”“颜色”由实现方拟定，待审；“分类”取 化学品 / 催化剂 / 原料 / 菌种 / 酶 / 原版（开放集合，可扩展），决定创造模式标签页和是否带纯度（化学品、菌种带纯度；酶、耗材不带，2026-11-05，见 10 §1.1）；“纯度类别”是带纯度物料的分档类别（K0–K12，界限见 [10](10-purity-jei-ae2.md) 第 1.4 节），“—”表示不带纯度（催化剂、原料、原版）；08 §C 中分类为菌种的物料一律为 K11，不另列；“颜色”是流体的着色。化学式一栏中反引号里的内容是 Tooltip 显示的化学式，没有反引号的只是组成描述，不显示。

**颜色的取值顺序**（2026-11-12，11 问题 42）：① 本表与 08 写明的颜色；② 现实外观：天然香料按产品形态取下表的默认色，外观鲜明的单列条目已写进各行的“颜色”栏；③ 实现方按类别、纯度给的默认色（流体按名称取色，香料按类别给色，纯度越低越深）。现实中多数单体香料是无色至淡黄的液体或白色结晶，按类别上色只为便于辨认。

| 天然香料形态 | 默认色 | 现实外观 |
| --- | --- | --- |
| 精油（NT1、NT2、NT12） | #F2E3A0 | 无色至淡黄的液体 |
| 浸膏（NT4） | #5C4A2E | 深棕、深绿的蜡状膏体 |
| 净油（NT5） | #8A5A24 | 琥珀至深棕的黏稠液体 |
| 油树脂（NT6） | #5A3418 | 深棕黏稠液体 |
| 酊、提取物（NT3、NT7） | #7A4E26 | 棕色液体 |
| 树脂、香脂（NT8） | #9A6A30 | 琥珀色块状或黏稠物 |

表中的“建议 ID”同时是该物料的注册名和标签名：固体为物品标签 `foodindustrial:chemicals/<ID>`，液体与气体为流体标签 `foodindustrial:<ID>`，配方一律引用标签（见 08、10）。

建议在模组内为每种物料存一份数据（例如 `data/foodindustrial/chemistry/<id>.json`）：元素组成表、结晶水数、显示字符串、可选的 GTCEu 材料 ID。物品 Tooltip 从这里读化学式，联动 GTCEu 时也从这里映射。

```json
{
  "id": "foodindustrial:gypsum",
  "display": "CaSO4·2H2O",
  "composition": { "Ca": 1, "S": 1, "O": 4 },
  "hydrate_water": 2,
  "gtceu": "gtceu:gypsum",
  "category": "foodindustrial:k1_inorganic",
  "source_purity": 80
}
```

纯度相关字段（`category`、`source_purity`、`grades`）的含义见 10 第 4.7 节；各物料的来源纯度与覆盖写在本表第 D 节（2026-11-04 定，I28），数据生成输出为 `data/foodindustrial/purity_materials/<ID>.json`。

## 与 GTCEu 的联动

GTCEu（GregTech CEu Modern）用 `Material.Builder(...).components(...)` 定义材料组成，水合物用 `Water, n` 作组分（例：二水硫酸钙（石膏）= `Calcium, 1, Sulfur, 1, Water, 2, Oxygen, 4`）。以下对照取自其源码 `common/data/materials/*.java`。

**联动方式（建议，由浅到深）：**

1. **化学式对照**：Tooltip 显示与 GTCEu 一致的化学式；不依赖 GTCEu。
2. **标签互通**：本模组的粉、流体同时打上 GTCEu 会用到的 `forge:dusts/<材料名>` 等标签，让双方配方都能吃对方的物料。
3. **材料互认**（可选依赖）：GTCEu 加载时本模组照常注册全部化学品，不删除重复项；GT 的盐酸、硫酸、氨等通过标签导入，按外部物品计（外部默认值，见 10 第 1.6 节），需经本模组的精制工序才能用于更高门槛的配方。这样纯度门槛在装与不装 GTCEu 时表现一致。

**需要注意的不一致：**

| GTCEu 材料 | GTCEu 组成 | 实际物质 | 处理 |
| --- | --- | --- | --- |
| `sugar` | C6H12O6 | 这是葡萄糖的式子，不是蔗糖 C12H22O11 | 本模组的“葡萄糖”可映射到它；“蔗糖”单独注册 |
| `potash` | K2O | 钾碱在化学史上指 K2CO3 | 本模组的碳酸钾映射到 `potassium_carbonate`，不映射 `potash` |
| `alunite` | K、Al、Si、H、O 的硅酸盐 | 明矾石是 KAl3(SO4)2(OH)6 | 本模组单独注册明矾石，不映射 |
| `apatite` | Ca5(PO4)3Cl | 食品磷酸常用的是氟磷灰石 Ca5(PO4)3F | 两者都可作磷源，配方副产物按各自组成写 |
| `ash` / `dark_ash` | C | 草木灰主要有用成分是 K2CO3 | 本模组的草木灰、海藻灰单独注册 |
| `rock_salt` | KCl | 名称易与岩盐（NaCl）混淆 | 本模组 KCl 映射到 `rock_salt`，岩盐映射到 `salt` |
| `magnesium_chloride` | MgCl2（无水） | 氯化镁（卤片）是 MgCl2·6H2O | 映射时注明结晶水 |
| `salt_water` | Salt + Water | 海水还含镁、钾盐 | 只作近似映射，不能产出苦卤 |

**计量建议**：沿用“1 份 = 1 个粉 = 1000 mB”，配方按化学计量比书写（04 的反应式均已配平，可直接换算）。GTCEu 分解类配方的计量习惯与此不同，接入 GT 机器时需对照其现有配方确定换算。

## 核对方法

- 每种有确定结构的物料写出 SMILES，用 RDKit 解析后统计全部原子（含隐式氢），与文中化学式的元素计数比较；水合物比较无水部分。
- 04、13、14 中全部可解析的反应式（93 + 43 + 57 = 193 条）用同样的元素计数逐条检查配平；聚合、水解成单体等不定比式（7 条）人工核对。
- 本表与 02 的核对脚本思路相同，可在实现时转为单元测试，防止后续修改引入错误。

## A 无机物

| 建议 ID | 名称 | 化学式 / 组成 | 状态 | GTCEu 材料 | 核对 | 备注 | 英文名（待审） | 分类 | 颜色 | 纯度类别 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `water` | 水 | `H2O` | 液 | water | ✓ |  | Water | 原版 | — | — |
| `distilled_water` | 蒸馏水 | `H2O` | 液 | distilled_water |  | M08 蒸馏水；M25 离子交换、M31 反渗透所得同用此物品；品质看电导率，不带纯度（2026-11-05）；143 B 用 | Distilled Water | 化学品 | #9CC8F0 | — |
| `seawater` | 海水 | NaCl 约 3.5% 及 Mg、K、Ca 盐的水溶液 | 液 | salt_water（近似） |  | 本模组流体 | Seawater | 原料 | #2E6E8E | — |
| `bittern` | 苦卤 | MgCl2、MgSO4、KCl、NaCl 浓溶液，含少量 NaBr（P103 提溴，2026-11-11） | 液 | — |  | 本模组流体；多组分混合物，不带纯度（2026-11-05） | Bittern | 化学品 | #B8B06A | — |
| `salt` | 氯化钠（盐） | `NaCl` | 固 | salt | ✓ |  | Sodium Chloride (Salt) | 化学品 | — | K1 |
| `brine` | 饱和食盐水 | `NaCl` | 溶液 | — | ✓ | 约 26% 水溶液；氨碱法、氯碱电解原料 | Saturated Brine | 化学品 | #C8D8E8 | K0 |
| `potassium_chloride` | 氯化钾 | `KCl` | 固 | rock_salt | ✓ | GT 名为 rock_salt | Potassium Chloride | 化学品 | — | K1 |
| `carnallite` | 光卤石 | `KMgCl3·6H2O` | 固 | — | ✓ | 即 KCl·MgCl2·6H2O | Carnallite | 化学品 | — | K1 |
| `magnesium_chloride` | 氯化镁（卤片） | `MgCl2·6H2O` | 固 | magnesium_chloride | ✓ | GT 为无水物 | Magnesium Chloride (Nigari) | 化学品 | — | K1 |
| `epsom_salt` | 硫酸镁（泻盐） | `MgSO4·7H2O` | 固 | — | ✓ | 第四批 372 号 | Magnesium Sulfate (Epsom Salt) | 化学品 | — | K1 |
| `magnesium_hydroxide` | 氢氧化镁 | `Mg(OH)2` | 固 | — | ✓ |  | Magnesium Hydroxide | 化学品 | — | K1 |
| `magnesite` | 菱镁矿粉 | `MgCO3` | 固 | magnesite | ✓ | 菱镁矿石研磨；食品碳酸镁见 `magnesium_carbonate`（2026-11-05 拆分） | Ground Magnesite | 化学品 | — | K1 |
| `magnesium_carbonate` | 碳酸镁 | `Mg5(CO3)4(OH)2·4H2O` | 固 | — |  | 第四批 182 号（碱式碳酸镁，2026-11-05 由 `magnesite` 拆出） | Magnesium Carbonate | 化学品 | — | K1 |
| `calcium_carbonate` | 碳酸钙（石灰石） | `CaCO3` | 固 | calcite / calcium_carbonate | ✓ |  | Calcium Carbonate (Limestone) | 化学品 | — | K1 |
| `quicklime` | 氧化钙（生石灰） | `CaO` | 固 | quicklime | ✓ |  | Calcium Oxide (Quicklime) | 化学品 | — | K1 |
| `slaked_lime` | 氢氧化钙（熟石灰） | `Ca(OH)2` | 固 | calcium_hydroxide | ✓ | 第四批 145 号 | Calcium Hydroxide (Slaked Lime) | 化学品 | — | K1 |
| `calcium_chloride` | 氯化钙 | `CaCl2` | 固 | calcium_chloride | ✓ |  | Calcium Chloride | 化学品 | — | K1 |
| `gypsum` | 硫酸钙（石膏） | `CaSO4·2H2O` | 固 | gypsum | ✓ | 二水物；第 12 号添加剂 | Calcium Sulfate (Gypsum) | 化学品 | — | K1 |
| `plaster` | 半水硫酸钙（熟石膏） | `CaSO4·0.5H2O` | 固 | — | ✓ |  | Calcium Sulfate Hemihydrate (Plaster of Paris) | 化学品 | — | K1 |
| `calcium_sulfide` | 硫化钙 | `CaS` | 固 | — | ✓ | 路布兰废渣 | Calcium Sulfide | 化学品 | — | K1 |
| `soda_ash` | 碳酸钠（纯碱） | `Na2CO3` | 固 | soda_ash | ✓ |  | Sodium Carbonate (Soda Ash) | 化学品 | — | K1 |
| `trona` | 碳酸氢三钠（天然碱） | `Na3H(CO3)2·2H2O` | 固 | trona | ✓ | 第四批 148 号 | Sodium Sesquicarbonate (Trona) | 化学品 | — | K1 |
| `sodium_bicarbonate` | 碳酸氢钠（小苏打） | `NaHCO3` | 固 | sodium_bicarbonate | ✓ |  | Sodium Bicarbonate (Baking Soda) | 化学品 | — | K1 |
| `potassium_carbonate` | 碳酸钾 | `K2CO3` | 固 | potassium_carbonate | ✓ | GT 的 potash 是 K2O，不要对错 | Potassium Carbonate | 化学品 | — | K1 |
| `sodium_hydroxide` | 氢氧化钠（烧碱） | `NaOH` | 固 | sodium_hydroxide | ✓ |  | Sodium Hydroxide (Caustic Soda) | 化学品 | — | K1 |
| `potassium_hydroxide` | 氢氧化钾 | `KOH` | 固 | potassium_hydroxide | ✓ | 第四批 146 号 | Potassium Hydroxide | 化学品 | — | K1 |
| `hydrogen_chloride` | 氯化氢 | `HCl` | 气 | hydrochloric_acid | ✓ | 气体；水溶液为盐酸 `hydrochloric_acid` | Hydrogen Chloride | 化学品 | #D8E8D0 | K9 |
| `hydrochloric_acid` | 盐酸 | `HCl` | 溶液 | hydrochloric_acid | ✓ | 约 31% 水溶液；配方中的“盐酸”；第四批 143 号 | Hydrochloric Acid | 化学品 | #D0E8C0 | K4 |
| `sulfuric_acid` | 硫酸 | `H2SO4` | 液 | sulfuric_acid | ✓ |  | Sulfuric Acid | 化学品 | #E8D8A0 | K4 |
| `nitric_acid` | 硝酸 | `HNO3` | 液 | nitric_acid |  | 共用中间体（04）：硝酸钾 + 硫酸蒸馏，或氨氧化后吸收；I18 补（2026-11-11） | Nitric Acid | 化学品 | #F0EAC8 | K4 |
| `potassium_bisulfate` | 硫酸氢钾 | `KHSO4` | 固 | — | ✓ | 硝石制硝酸副产 | Potassium Bisulfate | 化学品 | — | K1 |
| `sulfur` | 硫（硫磺） | `S` | 固 | sulfur | ✓ | 第四批 168 号 | Sulfur (Brimstone) | 化学品 | — | K1 |
| `sulfur_dioxide` | 二氧化硫 | `SO2` | 气 | sulfur_dioxide | ✓ |  | Sulfur Dioxide | 化学品 | #E0E0C8 | K9 |
| `sulfur_trioxide` | 三氧化硫 | `SO3` | 气 | sulfur_trioxide | ✓ |  | Sulfur Trioxide | 化学品 | #F0F0F0 | K9 |
| `sodium_bisulfite` | 亚硫酸氢钠 | `NaHSO3` | 溶液 | — | ✓ | 第四批 166 号；作添加剂（166）按溶液计，界限见 10 §1.5（2026-11-05） | Sodium Bisulfite | 化学品 | #E8F0F0 | K1 |
| `sodium_metabisulfite` | 焦亚硫酸钠 | `Na2S2O5` | 固 | — | ✓ |  | Sodium Metabisulfite | 化学品 | — | K1 |
| `sodium_sulfate` | 硫酸钠（元明粉） | `Na2SO4` | 固 | — | ✓ | 无水物；十水物为芒硝 Na2SO4·10H2O | Sodium Sulfate (Thenardite) | 化学品 | — | K1 |
| `sodium_sulfide` | 硫化钠 | `Na2S` | 固 | sodium_sulfide | ✓ |  | Sodium Sulfide | 化学品 | — | K1 |
| `ammonia` | 氨 | `NH3` | 气 | ammonia | ✓ |  | Ammonia | 化学品 | #E0E8F8 | K9 |
| `ammonium_chloride` | 氯化铵 | `NH4Cl` | 固 | ammonium_chloride | ✓ |  | Ammonium Chloride | 化学品 | — | K1 |
| `ammonium_bicarbonate` | 碳酸氢铵（臭粉） | `NH4HCO3` | 固 | — | ✓ |  | Ammonium Bicarbonate (Baker's Ammonia) | 化学品 | — | K1 |
| `ammonium_bisulfate` | 硫酸氢铵 | `NH4HSO4` | 固 | — | ✓ | 乳腈法副产 | Ammonium Bisulfate | 化学品 | — | K1 |
| `carbon_dioxide` | 二氧化碳 | `CO2` | 气 | carbon_dioxide | ✓ | 第四批 338、345 号 | Carbon Dioxide | 化学品 | #D8D8D8 | K9 |
| `carbon_monoxide` | 一氧化碳 | `CO` | 气 | carbon_monoxide | ✓ |  | Carbon Monoxide | 化学品 | #C8C8C8 | K9 |
| `oxygen` | 氧气 | `O2` | 气 | oxygen | ✓ |  | Oxygen | 化学品 | #C8E0FF | K9 |
| `nitrogen` | 氮气 | `N2` | 气 | nitrogen | ✓ | 第四批 373 号 | Nitrogen | 化学品 | #D0D8F0 | K9 |
| `hydrogen` | 氢气 | `H2` | 气 | hydrogen | ✓ |  | Hydrogen | 化学品 | #F0F8FF | K9 |
| `chlorine` | 氯气 | `Cl2` | 气 | chlorine | ✓ |  | Chlorine | 化学品 | #C8E060 | K9 |
| `nitric_oxide` | 一氧化氮 | `NO` | 气 | nitric_oxide | ✓ |  | Nitric Oxide | 化学品 | #D8D8E8 | K9 |
| `nitrogen_dioxide` | 二氧化氮 | `NO2` | 气 | nitrogen_dioxide | ✓ |  | Nitrogen Dioxide | 化学品 | #A04020 | K9 |
| `sodium_nitrite` | 亚硝酸钠 | `NaNO2` | 固 | sodium_nitrite | ✓ |  | Sodium Nitrite | 化学品 | — | K1 |
| `sodium_nitrate` | 硝酸钠 | `NaNO3` | 固 | — | ✓ | 第四批 173 号 | Sodium Nitrate | 化学品 | — | K1 |
| `potassium_nitrate` | 硝酸钾（硝石） | `KNO3` | 固 | saltpeter | ✓ |  | Potassium Nitrate (Saltpeter) | 化学品 | — | K1 |
| `calcium_nitrate` | 硝酸钙 | `Ca(NO3)2` | 固 | — | ✓ | 硝土主成分之一 | Calcium Nitrate | 化学品 | — | K1 |
| `hydroxyapatite` | 羟基磷灰石（骨灰） | `Ca5(PO4)3OH` | 固 | — | ✓ |  | Hydroxyapatite (Bone Ash) | 化学品 | — | K1 |
| `fluorapatite` | 氟磷灰石 | `Ca5(PO4)3F` | 固 | apatite（GT 为氯磷灰石 Ca5(PO4)3Cl） | ✓ |  | Fluorapatite | 化学品 | — | K1 |
| `tricalcium_phosphate` | 磷酸三钙 | `Ca3(PO4)2` | 固 | tricalcium_phosphate | ✓ |  | Tricalcium Phosphate | 化学品 | — | K1 |
| `phosphoric_acid` | 磷酸 | `H3PO4` | 液 | phosphoric_acid | ✓ | 第四批 142 号 | Phosphoric Acid | 化学品 | #F0F0E0 | K4 |
| `white_phosphorus` | 白磷 | `P4` | 固 | — | ✓ | GT 只有元素 phosphorus | White Phosphorus | 化学品 | — | K1 |
| `phosphorus_pentoxide` | 五氧化二磷 | `P4O10` | 固 | phosphorus_pentoxide | ✓ |  | Phosphorus Pentoxide | 化学品 | — | K1 |
| `monosodium_phosphate` | 磷酸二氢钠 | `NaH2PO4` | 固 | — | ✓ | 第四批 184 号 | Monosodium Phosphate | 化学品 | — | K1 |
| `sodium_acid_pyrophosphate` | 焦磷酸二氢二钠 | `Na2H2P2O7` | 固 | — | ✓ |  | Sodium Acid Pyrophosphate | 化学品 | — | K1 |
| `dicalcium_phosphate` | 磷酸氢钙 | `CaHPO4·2H2O` | 固 | — | ✓ | 明胶脱钙副产；第四批 170 号 | Dicalcium Phosphate | 化学品 | — | K1 |
| `hydrogen_fluoride` | 氟化氢 | `HF` | 气 | hydrofluoric_acid | ✓ |  | Hydrogen Fluoride | 化学品 | #E8F0E8 | K9 |
| `silicon_dioxide` | 二氧化硅 | `SiO2` | 固 | silicon_dioxide | ✓ |  | Silicon Dioxide | 化学品 | — | K1 |
| `calcium_silicate` | 硅酸钙 | `CaSiO3` | 固 | — | ✓ | 第四批 159 号（水热合成）；电炉炉渣另设 `phosphorus_slag`（2026-11-05 拆分） | Calcium Silicate | 化学品 | — | K1 |
| `alunite` | 明矾石 | `KAl3(SO4)2(OH)6` | 固 | alunite（GT 组成为硅酸盐，不一致） | ✓ |  | Alunite | 化学品 | — | K1 |
| `kaolinite` | 高岭石 | `Al2Si2O5(OH)4` | 固 | — |  | 黏土主成分 | Kaolinite | 化学品 | — | K1 |
| `alumina` | 氧化铝 | `Al2O3` | 固 | — | ✓ |  | Aluminium Oxide | 化学品 | — | K1 |
| `aluminium_hydroxide` | 氢氧化铝 | `Al(OH)3` | 固 | — | ✓ | 也作铝色淀载体 | Aluminium Hydroxide | 化学品 | — | K1 |
| `aluminium_sulfate` | 硫酸铝 | `Al2(SO4)3` | 固 | — | ✓ |  | Aluminium Sulfate | 化学品 | — | K1 |
| `potassium_sulfate` | 硫酸钾 | `K2SO4` | 固 | potassium_sulfate | ✓ |  | Potassium Sulfate | 化学品 | — | K1 |
| `potassium_alum` | 硫酸铝钾（明矾） | `KAl(SO4)2·12H2O` | 固 | — | ✓ |  | Potassium Aluminium Sulfate (Alum) | 化学品 | — | K1 |
| `copper_sulfate` | 硫酸铜（胆矾） | `CuSO4·5H2O` | 固 | — | ✓ |  | Copper Sulfate (Blue Vitriol) | 化学品 | — | K1 |
| `sodium_hypochlorite` | 次氯酸钠 | `NaClO` | 溶液 | — | ✓ |  | Sodium Hypochlorite | 化学品 | #E0F0C0 | K0 |
| `iodine` | 碘 | `I2` | 固 | iodine | ✓ | 海带联产 | Iodine | 化学品 | — | K1 |
| `potassium_iodide` | 碘化钾 | `KI` | 固 | potassium_iodide | ✓ |  | Potassium Iodide | 化学品 | — | K1 |
| `activated_carbon` | 活性炭 | `C` | 固 | activated_carbon | ✓ |  | Activated Carbon | 催化剂 | — | — |
| `nickel_catalyst` | 镍催化剂 | `Ni` | 固 | nickel | ✓ |  | Nickel Catalyst | 催化剂 | — | — |
| `vanadium_pentoxide` | 五氧化二钒 | `V2O5` | 固 | — |  | 接触法催化剂 | Vanadium Pentoxide | 催化剂 | — | — |
| `sodium_silicate` | 硅酸钠（水玻璃） | `Na2SiO3` | 溶液 | — | ✓ | 二氧化硅原料 | Sodium Silicate (Water Glass) | 化学品 | #E8E8D8 | K0 |
| `chlorosulfonic_acid` | 氯磺酸 | `ClHO3S` | 液 | — | ✓ |  | Chlorosulfonic Acid | 化学品 | #E8E0B0 | K4 |
| `sulfamic_acid` | 氨基磺酸 | `H3NO3S` | 固 | — | ✓ |  | Sulfamic Acid | 化学品 | — | K1 |
| `thionyl_chloride` | 氯化亚砜 | `Cl2OS` | 液 | — | ✓ |  | Thionyl Chloride | 化学品 | #F0E890 | K4 |
| `sulfur_dichloride` | 二氯化硫 | `Cl2S` | 液 | — | ✓ |  | Sulfur Dichloride | 化学品 | #C03020 | K4 |
| `calcium_ferrocyanide` | 亚铁氰化钙 | `Ca2[Fe(CN)6]` | 溶液 | calcium_ferrocyanide | ✓ |  | Calcium Ferrocyanide | 化学品 | #E8E070 | K0 |
| `potassium_ferrocyanide` | 亚铁氰化钾（黄血盐） | `K4[Fe(CN)6]·3H2O` | 固 | potassium_ferrocyanide | ✓ |  | Potassium Ferrocyanide (Yellow Prussiate) | 化学品 | — | K1 |
| `ferrous_sulfate` | 硫酸亚铁（绿矾） | `FeSO4·7H2O` | 固 | — | ✓ |  | Ferrous Sulfate (Green Vitriol) | 化学品 | — | K1 |
| `disodium_phosphate` | 磷酸氢二钠 | `Na2HPO4` | 固 | — | ✓ |  | Disodium Phosphate | 化学品 | — | K1 |
| `sodium_tripolyphosphate` | 三聚磷酸钠 | `Na5P3O10` | 固 | — | ✓ |  | Sodium Tripolyphosphate | 化学品 | — | K1 |
| `sodium_hexametaphosphate` | 多聚磷酸钠（六偏磷酸钠） | `(NaPO3)n` | 固 | — |  | 第二批 52 号：磷酸二氢钠熔融急冷的玻璃态；I19 补（2026-11-11） | Sodium Hexametaphosphate | 化学品 | — | K1 |
| `tetrasodium_pyrophosphate` | 焦磷酸钠 | `Na4P2O7` | 固 | — | ✓ |  | Tetrasodium Pyrophosphate | 化学品 | — | K1 |
| `potassium_iodate` | 碘酸钾 | `KIO3` | 固 | — | ✓ | 营养强化剂 | Potassium Iodate | 化学品 | — | K1 |
| `ferric_pyrophosphate` | 焦磷酸铁 | `Fe4(P2O7)3` | 固 | — | ✓ | 营养强化剂 | Ferric Pyrophosphate | 化学品 | — | K1 |
| `zinc_sulfate` | 硫酸锌（皓矾） | `ZnSO4·7H2O` | 固 | — | ✓ |  | Zinc Sulfate (White Vitriol) | 化学品 | — | K1 |
| `manganese_dioxide` | 二氧化锰 | `MnO2` | 固 | pyrolusite | ✓ | 苯胺氧化制对苯醌 | Manganese Dioxide | 化学品 | — | K1 |
| `manganese_sulfate` | 硫酸锰 | `MnSO4` | 固 | — | ✓ | 对苯醌副产 | Manganese Sulfate | 化学品 | — | K1 |
| `ammonium_sulfate` | 硫酸铵 | `(NH4)2SO4` | 固 | — | ✓ | 副产，可作肥料 | Ammonium Sulfate | 化学品 | — | K1 |
| `ferric_chloride` | 三氯化铁 | `FeCl3` | 固 | iron_iii_chloride | ✓ | 焦磷酸铁原料 | Ferric Chloride | 化学品 | — | K1 |
| `ferric_hydroxide` | 氢氧化铁 | `Fe(OH)3` | 固 | — | ✓ | 乙二胺四乙酸铁钠原料 | Ferric Hydroxide | 化学品 | — | K1 |
| `aluminium_chloride` | 三氯化铝 | `AlCl3` | 固 | — | ✓ | 低温阳离子聚合引发剂 | Aluminium Chloride | 催化剂 | — | — |
| `sodium_tungstate` | 钨酸钠 | `Na2WO4` | 固 | — | ✓ | 环氧化催化剂 | Sodium Tungstate | 催化剂 | — | — |
| `nitrous_oxide` | 氧化亚氮 | `N2O` | 气 | — |  | 第四批：144 副产；GB 2760 表 C.2 加工助剂（推进、起泡）；第五批 457 号 | Nitrous Oxide | 化学品 | #D8E2EA | K9 |
| `silicon` | 硅 | `Si` | 固 | — |  | 第四批：163 | Silicon | 化学品 | — | K1 |
| `zinc_dithionite` | 连二亚硫酸锌 | `ZnS2O4` | 溶液 | — |  | 第四批：167 | Zinc Dithionite | 化学品 | #E6EAD8 | K0 |
| `phosphorus_trichloride` | 三氯化磷 | `PCl3` | 液 | — |  | 第四批：204 | Phosphorus Trichloride | 化学品 | #F0F0E0 | K4 |
| `sodium` | 钠 | `Na` | 固 | — |  | 第四批：204 | Sodium | 化学品 | — | K1 |
| `titanyl_sulfate` | 硫酸氧钛 | `TiOSO4` | 溶液 | — |  | 第四批：198 | Titanyl Sulfate | 化学品 | #E8E8F4 | K0 |
| `metatitanic_acid` | 偏钛酸 | `TiO(OH)2` | 固 | — |  | 第四批：198 | Metatitanic Acid | 化学品 | — | K1 |
| `titanium_tetrachloride` | 四氯化钛 | `TiCl4` | 液 | — |  | 第四批：198 | Titanium Tetrachloride | 化学品 | #F2F0E6 | K4 |
| `hydrogen_peroxide` | 过氧化氢（双氧水） | `H2O2` | 溶液 | — |  | 第四批：253、340；54 B 已用；第五批 459 号 | Hydrogen Peroxide | 化学品 | #DDEFF5 | K4 |
| `ammonium_persulfate` | 过二硫酸铵 | `(NH4)2S2O8` | 固 | — |  | 第四批：253（过氧化氢中间体）、310（聚合引发剂） | Ammonium Persulfate | 化学品 | — | K1 |
| `stannous_chloride` | 氯化亚锡 | `SnCl2·2H2O` | 固 | — |  | 第四批：282 | Stannous Chloride | 化学品 | — | K1 |
| `sodium_trimetaphosphate` | 三偏磷酸钠 | `Na3P3O9` | 固 | — |  | 第四批：293、294、295、309 | Sodium Trimetaphosphate | 化学品 | — | K1 |
| `bismuth_molybdate_catalyst` | 钼酸铋催化剂 | `Bi2(MoO4)3` | 固 | — |  | 第四批：310 | Bismuth Molybdate Catalyst | 催化剂 | — | — |
| `zinc_chloride` | 氯化锌 | `ZnCl2` | 固 | — |  | 第四批：325；2026-11-11 定：改为化学品 K1（506 作营养强化剂），催化剂槽照样接受（11 问题 31）；第五批 506 号 | Zinc Chloride | 化学品 | — | K1 |
| `sodium_chlorate` | 氯酸钠 | `NaClO3` | 固 | — |  | 第四批：340 | Sodium Chlorate | 化学品 | — | K1 |
| `chlorine_dioxide` | 二氧化氯 | `ClO2` | 气 | — |  | 第四批：340 | Chlorine Dioxide | 化学品 | #D8B030 | K9 |
| `potassium_manganate` | 锰酸钾 | `K2MnO4` | 固 | — |  | 第四批：364 | Potassium Manganate | 化学品 | — | K1 |
| `potassium_bicarbonate` | 碳酸氢钾 | `KHCO3` | 固 | — |  | 第四批 150 号 | Potassium Bicarbonate | 化学品 | — | K1 |
| `tripotassium_phosphate` | 磷酸三钾 | `K3PO4` | 固 | — |  | 第四批 151 号 | Tripotassium Phosphate | 化学品 | — | K1 |
| `sodium_ferrocyanide` | 亚铁氰化钠 | `Na4[Fe(CN)6]·10H2O` | 固 | — |  | 第四批 158 号 | Sodium Ferrocyanide | 化学品 | — | K1 |
| `potassium_metabisulfite` | 焦亚硫酸钾 | `K2S2O5` | 固 | — |  | 第四批 164 号 | Potassium Metabisulfite | 化学品 | — | K1 |
| `sodium_sulfite` | 亚硫酸钠 | `Na2SO3` | 固 | — |  | 第四批 165 号 | Sodium Sulfite | 化学品 | — | K1 |
| `sodium_dithionite` | 低亚硫酸钠（保险粉） | `Na2S2O4` | 固 | — |  | 第四批 167 号 | Sodium Dithionite (Sodium Hydrosulfite) | 化学品 | — | K1 |
| `ammonium_alum` | 硫酸铝铵 | `NH4Al(SO4)2·12H2O` | 固 | — |  | 第四批 169 号 | Ammonium Aluminium Sulfate (Ammonium Alum) | 化学品 | — | K1 |
| `diammonium_phosphate` | 磷酸氢二铵 | `(NH4)2HPO4` | 固 | — |  | 第四批 171 号 | Diammonium Phosphate | 化学品 | — | K1 |
| `ammonium_carbonate` | 碳酸铵 | `(NH4)2CO3` | 固 | — |  | 第四批 172 号；商品为碳酸氢铵与氨基甲酸铵的混合物 | Ammonium Carbonate | 化学品 | — | K1 |
| `potassium_nitrite` | 亚硝酸钾 | `KNO2` | 固 | — |  | 第四批 174 号 | Potassium Nitrite | 化学品 | — | K1 |
| `trisodium_phosphate` | 磷酸三钠 | `Na3PO4` | 固 | — |  | 第四批 183 号 | Trisodium Phosphate | 化学品 | — | K1 |
| `monocalcium_phosphate` | 磷酸二氢钙 | `Ca(H2PO4)2·H2O` | 固 | — |  | 第四批 185 号 | Monocalcium Phosphate | 化学品 | — | K1 |
| `dipotassium_phosphate` | 磷酸氢二钾 | `K2HPO4` | 固 | — |  | 第四批 186 号 | Dipotassium Phosphate | 化学品 | — | K1 |
| `monopotassium_phosphate` | 磷酸二氢钾 | `KH2PO4` | 固 | — |  | 第四批 187 号 | Monopotassium Phosphate | 化学品 | — | K1 |
| `trisodium_pyrophosphate` | 焦磷酸一氢三钠 | `Na3HP2O7` | 固 | — |  | 第四批 190 号 | Trisodium Hydrogen Pyrophosphate | 化学品 | — | K1 |
| `potassium_polymetaphosphate` | 聚偏磷酸钾 | `(KPO3)n` | 固 | — |  | 第四批 191 号 | Potassium Polymetaphosphate | 化学品 | — | K1 |
| `calcium_acid_pyrophosphate` | 酸式焦磷酸钙 | `CaH2P2O7` | 固 | — |  | 第四批 192 号 | Calcium Acid Pyrophosphate | 化学品 | — | K1 |
| `tetrapotassium_pyrophosphate` | 焦磷酸四钾 | `K4P2O7` | 固 | — |  | 第四批 193 号 | Tetrapotassium Pyrophosphate | 化学品 | — | K1 |
| `titanium_dioxide` | 二氧化钛 | `TiO2` | 固 | — |  | 第四批 198 号 | Titanium Dioxide | 化学品 | — | K1 |
| `iron_oxide_black` | 氧化铁黑 | `Fe3O4` | 固 | — |  | 第四批 200 号 | Iron Oxide Black | 化学品 | — | K1 |
| `iron_oxide_red` | 氧化铁红 | `Fe2O3` | 固 | — |  | 第四批 201 号 | Iron Oxide Red | 化学品 | — | K1 |
| `vegetable_carbon` | 植物炭黑 | `C` | 固 | — |  | 第四批 229 号 | Vegetable Carbon | 化学品 | — | K1 |
| `stabilized_chlorine_dioxide` | 稳定态二氧化氯 | `ClO2` | 溶液 | — |  | 第四批 340 号；以亚氯酸钠形式稳定于碳酸钠–过氧化氢水溶液，含量以 ClO2 计；不同于气体 `chlorine_dioxide`；界限见 10 §1.5 | Stabilized Chlorine Dioxide | 化学品 | #E8E0B0 | K1 |
| `potassium_permanganate` | 高锰酸钾 | `KMnO4` | 固 | — |  | 第四批 364 号 | Potassium Permanganate | 化学品 | — | K1 |
| `potassium_thiocyanate` | 硫氰酸钾 | `CKNS` | 固 | — | ✓ | 第五批：S0762 B | Potassium Thiocyanate | 化学品 | — | K1 |
| `carbon_disulfide` | 二硫化碳 | `CS2` | 液 | — | ✓ | 第五批：S0727、S0734、`potassium_thiocyanate`；S0899、S0924；S1085、S1086、S1089、S1090、S1091；S1321 | Carbon Disulfide | 化学品 | — | K4 |
| `selenium_dioxide` | 二氧化硒 | `SeO2` | 固 | — | ✓ | 第五批：S0664；509 | Selenium Dioxide | 化学品 | — | K1 |
| `hydrazine_hydrate` | 水合肼 | `N2H4·H2O` | 液 | — | ✓ | 第五批：S0895 | Hydrazine Hydrate | 化学品 | — | K4 |
| `magnesium` | 镁 | `Mg` | 固 | — | ✓ | 第五批：S0873、S0927、S0931、S0943；S0954；S1121、S1139、S1151、S1232、S1234、S1237 | Magnesium | 化学品 | — | K1 |
| `thiourea` | 硫脲 | `CH4N2S` | 固 | — | ✓ | 第五批：S1222 | Thiourea | 化学品 | — | K3 |
| `disulfur_dichloride` | 二氯化二硫 | `Cl2S2` | 液 | — | ✓ | 第五批：S1441 | Disulfur Dichloride | 化学品 | — | K4 |
| `sulfamoyl_chloride` | 氨基磺酰氯 | `ClH2NO2S` | 固 | — | ✓ | 第五批：S1472 | Sulfamoyl Chloride | 化学品 | — | K3 |
| `ammonium_nitrate` | 硝酸铵 | `NH4NO3` | 固 | — | ✓ | 第五批：457 | Ammonium Nitrate | 化学品 | — | K1 |
| `phosphoryl_chloride` | 三氯氧磷 | `Cl3OP` | 液 | — | ✓ | 第五批：471、472、478 | Phosphoryl Chloride | 化学品 | — | K4 |
| `cobalt_chloride` | 氯化钴 | `CoCl2·6H2O` | 固 | — | ✓ | 第五批：474、476 | Cobalt(II) Chloride Hexahydrate | 化学品 | — | K1 |
| `bromine` | 溴 | `Br2` | 液 | — | ✓ | 第五批：481 | Bromine | 化学品 | — | K4 |
| `hydrobromic_acid` | 氢溴酸 | `HBr` | 溶液 | — | ✓ | 第五批：481 | Hydrobromic Acid | 化学品 | — | K4 |
| `iron_pentacarbonyl` | 五羰基铁 | `C5FeO5` | 液 | — | ✓ | 第五批：489 | Iron Pentacarbonyl | 化学品 | — | K4 |
| `molybdenum_trioxide` | 三氧化钼 | `MoO3` | 固 | — | ✓ | 第五批：529、530；钼酸铋催化剂（310）也可经此 | Molybdenum Trioxide | 化学品 | — | K1 |
| `sodium_chromate` | 铬酸钠 | `Na2CrO4` | 固 | — | ✓ | 第五批：527 B、528 B | Sodium Chromate | 化学品 | — | K0 |
| `sodium_dichromate` | 重铬酸钠 | `Na2Cr2O7·2H2O` | 固 | — |  | 第五批：527 B、528 B | Sodium Dichromate | 化学品 | — | K0 |
| `nitrosyl_chloride` | 亚硝酰氯 | `ClNO` | 气 | — | ✓ | 第五批：S0224 B | Nitrosyl Chloride | 化学品 | — | K9 |
| `magnesium_trisilicate` | 硅酸镁（三硅酸镁） | `Mg2Si3O8` | 固 | — | ✓ | 第五批 429 号 | Magnesium Trisilicate | 化学品 | — | K1 |
| `magnesium_oxide` | 氧化镁 | `MgO` | 固 | — | ✓ | 第五批 430 号 | Magnesium Oxide | 化学品 | — | K1 |
| `talc` | 滑石粉 | `Mg3Si4O10(OH)2` | 固 | — |  | 第五批 451 号 | Talc | 化学品 | — | K1 |
| `monoammonium_phosphate` | 磷酸二氢铵 | `NH4H2PO4` | 固 | — | ✓ | 第五批 454 号 | Ammonium Dihydrogen Phosphate | 化学品 | — | K1 |
| `reduced_iron` | 还原铁 | `Fe` | 固 | — | ✓ | 第五批 488 号 | Reduced Iron | 化学品 | — | K1 |
| `carbonyl_iron` | 羰基铁粉 | `Fe` | 固 | — | ✓ | 第五批 489 号 | Carbonyl Iron Powder | 化学品 | — | K1 |
| `ferrous_carbonate` | 碳酸亚铁 | `FeCO3` | 固 | — | ✓ | 第五批 490 号 | Ferrous Carbonate | 化学品 | — | K1 |
| `electrolytic_iron` | 电解铁 | `Fe` | 固 | — | ✓ | 第五批 494 号 | Electrolytic Iron | 化学品 | — | K1 |
| `zinc_oxide` | 氧化锌 | `ZnO` | 固 | — | ✓ | 第五批 503 号 | Zinc Oxide | 化学品 | — | K1 |
| `zinc_carbonate` | 碳酸锌 | `ZnCO3` | 固 | — | ✓ | 第五批 508 号 | Zinc Carbonate | 化学品 | — | K1 |
| `sodium_selenite` | 亚硒酸钠 | `Na2SeO3` | 固 | — | ✓ | 第五批 509 号 | Sodium Selenite | 化学品 | — | K1 |
| `sodium_selenate` | 硒酸钠 | `Na2SeO4` | 固 | — | ✓ | 第五批 510 号 | Sodium Selenate | 化学品 | — | K1 |
| `magnesium_hydrogen_phosphate` | 磷酸氢镁 | `MgHPO4·3H2O` | 固 | — | ✓ | 第五批 516 号 | Magnesium Hydrogen Phosphate | 化学品 | — | K1 |
| `copper_carbonate` | 碳酸铜 | `CuCO3·Cu(OH)2` | 固 | — | ✓ | 第五批 520 号 | Basic Copper Carbonate | 化学品 | — | K1 |
| `manganese_chloride` | 氯化锰 | `MnCl2·4H2O` | 固 | — | ✓ | 第五批 521 号 | Manganese Chloride | 化学品 | — | K1 |
| `manganese_carbonate` | 碳酸锰 | `MnCO3` | 固 | — | ✓ | 第五批 522 号 | Manganese Carbonate | 化学品 | — | K1 |
| `sodium_iodide` | 碘化钠 | `NaI` | 固 | — | ✓ | 第五批 526 号 | Sodium Iodide | 化学品 | — | K1 |
| `chromium_sulfate` | 硫酸铬 | `Cr2(SO4)3·6H2O` | 固 | — | ✓ | 第五批 527 号 | Chromium Sulfate | 化学品 | — | K1 |
| `chromium_chloride` | 氯化铬 | `CrCl3·6H2O` | 固 | — | ✓ | 第五批 528 号 | Chromium Chloride | 化学品 | — | K1 |
| `sodium_molybdate` | 钼酸钠 | `Na2MoO4·2H2O` | 固 | — | ✓ | 第五批 529 号 | Sodium Molybdate | 化学品 | — | K1 |
| `ammonium_molybdate` | 钼酸铵 | `(NH4)6Mo7O24·4H2O` | 固 | — | ✓ | 第五批 530 号 | Ammonium Molybdate | 化学品 | — | K1 |

## B 有机小分子

| 建议 ID | 名称 | 化学式 / 组成 | 状态 | GTCEu 材料 | 核对 | 备注 | 英文名（待审） | 分类 | 颜色 | 纯度类别 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `methane` | 甲烷 | `CH4` | 气 | methane | ✓ | 沼气、干馏煤气 | Methane | 化学品 | #E8E8E8 | K9 |
| `glucose` | 葡萄糖 | `C6H12O6` | 固 | sugar（GT 的 sugar 就是此式） | ✓ |  | Glucose | 化学品 | — | K2 |
| `fructose` | 果糖 | `C6H12O6` | 固 | — | ✓ |  | Fructose | 化学品 | — | K2 |
| `sucrose` | 蔗糖（白糖） | `C12H22O11` | 固 | —（不要映射到 GT sugar） | ✓ |  | Sucrose (White Sugar) | 化学品 | — | K2 |
| `maltose` | 麦芽糖 | `C12H22O11` | 固 | — | ✓ |  | Maltose | 化学品 | — | K2 |
| `ethanol` | 乙醇（酒精） | `C2H6O` | 液 | ethanol | ✓ |  | Ethanol (Alcohol) | 化学品 | #E8F0F8 | K4 |
| `methanol` | 甲醇 | `CH4O` | 液 | methanol | ✓ |  | Methanol | 化学品 | #E0F0F8 | K4 |
| `acetone` | 丙酮 | `C3H6O` | 液 | acetone | ✓ |  | Acetone | 化学品 | #F0F0F8 | K4 |
| `acetaldehyde` | 乙醛 | `C2H4O` | 液 | — | ✓ |  | Acetaldehyde | 化学品 | #F0F0E8 | K4 |
| `hydrogen_cyanide` | 氢氰酸 | `CHN` | 液 | hydrogen_cyanide | ✓ | 剧毒，可选路线 | Hydrocyanic Acid | 化学品 | #E8E8F0 | K4 |
| `acetic_acid` | 乙酸（醋酸） | `C2H4O2` | 液 | acetic_acid | ✓ |  | Ethanoic Acid (Acetic Acid) | 化学品 | #F0E8D8 | K4 |
| `oxalic_acid` | 乙二酸（草酸） | `C2H2O4` | 固 | — | ✓ | 乙醛酸原料 | Ethanedioic Acid (Oxalic Acid) | 化学品 | — | K3 |
| `sodium_acetate` | 乙酸钠 | `C2H3NaO2` | 固 | — | ✓ | 第四批 368 号 | Sodium Acetate | 化学品 | — | K3 |
| `calcium_acetate` | 乙酸钙（灰醋石） | `C4H6CaO4` | 固 | dissolved_calcium_acetate（溶液） | ✓ |  | Calcium Acetate (Grey Acetate) | 化学品 | — | K3 |
| `lactic_acid` | 乳酸 | `C3H6O3` | 液 | — | ✓ |  | Lactic Acid | 化学品 | #F0E8C0 | K4 |
| `calcium_lactate` | 乳酸钙 | `C6H10CaO6·5H2O` | 固 | — | ✓ |  | Calcium Lactate | 化学品 | — | K3 |
| `lactonitrile` | 乳腈 | `C3H5NO` | 液 | — | ✓ |  | Lactonitrile | 化学品 | #F0E8C8 | K4 |
| `citric_acid` | 柠檬酸 | `C6H8O7` | 固 | — | ✓ |  | Citric Acid | 化学品 | — | K3 |
| `calcium_citrate` | 柠檬酸钙 | `C12H10Ca3O14` | 固 | — | ✓ | 即 Ca3(C6H5O7)2，商品为四水物 | Calcium Citrate | 化学品 | — | K3 |
| `glutamic_acid` | L-谷氨酸 | `C5H9NO4` | 固 | — | ✓ | 表 B.3 S0325 | L-Glutamic Acid | 化学品 | — | K3 |
| `glutamic_acid_hydrochloride` | 谷氨酸盐酸盐 | `C5H10ClNO4` | 固 | — | ✓ |  | Glutamic Acid Hydrochloride | 化学品 | — | K3 |
| `monosodium_glutamate` | 谷氨酸钠（味精） | `C5H8NNaO4·H2O` | 固 | — | ✓ |  | Monosodium Glutamate (MSG) | 化学品 | — | K3 |
| `gluconic_acid` | 葡萄糖酸 | `C6H12O7` | 液 | — | ✓ |  | Gluconic Acid | 化学品 | #F0E0B0 | K4 |
| `glucono_delta_lactone` | 葡萄糖酸-δ-内酯（内酯） | `C6H10O6` | 固 | — | ✓ |  | Glucono-δ-lactone (GDL) | 化学品 | — | K3 |
| `mannitol` | D-甘露糖醇 | `C6H14O6` | 固 | — | ✓ | 海带联产 | D-Mannitol | 化学品 | — | K2 |
| `galacturonic_acid` | 半乳糖醛酸 | `C6H10O7` | 固 | — | ✓ | 果胶单元 | Galacturonic Acid | 化学品 | — | K3 |
| `toluene` | 甲苯 | `C7H8` | 液 | toluene | ✓ |  | Toluene | 化学品 | #E8E8E0 | K4 |
| `benzoic_acid` | 苯甲酸 | `C7H6O2` | 固 | — | ✓ | 第四批 332 号 | Benzoic Acid | 化学品 | — | K3 |
| `sodium_benzoate` | 苯甲酸钠 | `C7H5NaO2` | 固 | — | ✓ |  | Sodium Benzoate | 化学品 | — | K3 |
| `sorbitol` | 山梨糖醇 | `C6H14O6` | 固 | — | ✓ | 第四批 351 号 | Sorbitol | 化学品 | — | K2 |
| `l_sorbose` | L-山梨糖 | `C6H12O6` | 固 | — | ✓ |  | L-Sorbose | 化学品 | — | K2 |
| `diacetone_sorbose` | 二丙酮-L-山梨糖 | `C12H20O6` | 固 | — | ✓ |  | Diacetone-L-sorbose | 化学品 | — | K3 |
| `ketogulonic_acid` | 2-酮-L-古龙酸 | `C6H10O7` | 固 | — | ✓ |  | 2-Keto-L-gulonic Acid | 化学品 | — | K3 |
| `methyl_ketogulonate` | 2-酮-L-古龙酸甲酯 | `C7H12O7` | 固 | — | ✓ |  | Methyl 2-Keto-L-gulonate | 化学品 | — | K3 |
| `ascorbic_acid` | 抗坏血酸（维生素 C） | `C6H8O6` | 固 | — | ✓ |  | Ascorbic Acid (Vitamin C) | 化学品 | — | K3 |
| `sodium_ascorbate` | 抗坏血酸钠 | `C6H7NaO6` | 固 | — | ✓ |  | Sodium Ascorbate | 化学品 | — | K3 |
| `glycerol` | 丙三醇（甘油） | `C3H8O3` | 液 | glycerol | ✓ |  | Glycerol (Glycerin) | 化学品 | #F0F0F0 | K4 |
| `stearic_acid` | 硬脂酸 | `C18H36O2` | 固 | — | ✓ | 第四批 276 号 | Stearic Acid | 化学品 | — | K7 |
| `sodium_stearate` | 硬脂酸钠（肥皂） | `C18H35NaO2` | 固 | — | ✓ | 皂化副产 | Sodium Stearate (Soap) | 化学品 | — | K7 |
| `tristearin` | 硬脂酸甘油三酯 | `C57H110O6` | 固 | — | ✓ | 硬化油的代表物；动物脂见 `animal_fat`（2026-11-05）；多组分混合物，不带纯度（2026-11-05） | Glyceryl Tristearate | 化学品 | — | — |
| `triolein` | 油酸甘油三酯 | `C57H104O6` | 液 | seed_oil（未定义组成） | ✓ | 植物油代表物；多组分混合物，不带纯度（2026-11-05） | Glyceryl Trioleate | 化学品 | #F0D870 | — |
| `glyceryl_monostearate` | 单硬脂酸甘油酯 | `C21H42O4` | 固 | — | ✓ |  | Glyceryl Monostearate | 化学品 | — | K8 |
| `menthol` | l-薄荷醇（薄荷脑） | `C10H20O` | 固 | — | ✓ |  | l-Menthol (Menthol Crystals) | 化学品 | — | K3 |
| `menthone` | 薄荷酮 | `C10H18O` | 液 | — | ✓ | 薄荷素油主成分之一 | Menthone | 化学品 | #F0F0E0 | K4 |
| `thymol` | 百里酚 | `C10H14O` | 固 | — | ✓ |  | Thymol | 化学品 | — | K3 |
| `m_cresol` | 间甲酚 | `C7H8O` | 液 | — | ✓ |  | m-Cresol | 化学品 | #E8D0A0 | K4 |
| `propene` | 丙烯 | `C3H6` | 气 | propene | ✓ |  | Propene | 化学品 | #E8E8E8 | K9 |
| `guaiacol` | 愈创木酚 | `C7H8O2` | 液 | — | ✓ |  | Guaiacol | 化学品 | #E8D898 | K4 |
| `glyoxylic_acid` | 乙醛酸 | `C2H2O3` | 液 | — | ✓ |  | Glyoxylic Acid | 化学品 | #F0F0E8 | K4 |
| `vanillylmandelic_acid` | 3-甲氧基-4-羟基扁桃酸 | `C9H10O5` | 固 | — | ✓ |  | 4-Hydroxy-3-methoxymandelic Acid | 化学品 | — | K3 |
| `vanillin` | 香兰素 | `C8H8O3` | 固 | — | ✓ |  | Vanillin | 化学品 | — | K3 |
| `betanidin` | 甜菜素 | `C18H16N2O8` | 固 | — | ✓ | 甜菜红苷的苷元 | Betanidin | 化学品 | — | K5 |
| `betanin` | 甜菜苷 | `C24H26N2O13` | 固 | — | ✓ | 甜菜红主成分 | Betanin | 化学品 | — | K5 |
| `beta_carotene` | β-胡萝卜素 | `C40H56` | 固 | — | ✓ | 第四批 197 号；油树脂另设 `natural_carotene`（235） | β-Carotene | 化学品 | — | K5 |
| `citrinin` | 桔青霉素 | `C13H14O5` | 固 | — | ✓ | 红曲杂质 | Citrinin | 化学品 | — | K5 |
| `rubropunctatin` | 红斑素 | `C21H22O5` | 固 | — |  | 红曲红组分 | Rubropunctatin | 化学品 | — | K5 |
| `monascorubrin` | 红曲红素 | `C23H26O5` | 固 | — |  | 红曲红组分 | Monascorubrin | 化学品 | — | K5 |
| `chlorophyll_a` | 叶绿素 a | `C55H72MgN4O5` | 固 | — |  |  | Chlorophyll A | 化学品 | — | K5 |
| `sodium_copper_chlorophyllin` | 叶绿素铜钠盐 | `C34H31CuN4Na3O6` | 固 | — |  | 以铜叶绿酸 e6 三钠计 | Sodium Copper Chlorophyllin | 化学品 | — | K5 |
| `methylimidazole` | 4-甲基咪唑 | `C4H6N2` | 固 | — | ✓ | III/IV 类焦糖色杂质 | 4-Methylimidazole | 化学品 | — | K3 |
| `ketene` | 乙烯酮 | `C2H2O` | 气 | — | ✓ |  | Ketene | 化学品 | #E8E8D8 | K9 |
| `diketene` | 双乙烯酮 | `C4H4O2` | 液 | — | ✓ |  | Diketene | 化学品 | #E8E0C0 | K4 |
| `crotonaldehyde` | 巴豆醛 | `C4H6O` | 液 | — | ✓ |  | Crotonaldehyde | 化学品 | #F0E8B0 | K4 |
| `acetic_anhydride` | 乙酸酐（醋酐） | `C4H6O3` | 液 | acetic_anhydride | ✓ |  | Ethanoic Anhydride (Acetic Anhydride) | 化学品 | #F0F0E8 | K4 |
| `sorbic_acid` | 山梨酸 | `C6H8O2` | 固 | — | ✓ | 第四批 333 号 | Sorbic Acid | 化学品 | — | K3 |
| `potassium_sorbate` | 山梨酸钾 | `C6H7KO2` | 固 | — | ✓ |  | Potassium Sorbate | 化学品 | — | K3 |
| `ethylene` | 乙烯 | `C2H4` | 气 | ethylene | ✓ |  | Ethylene | 化学品 | #E8E8E8 | K9 |
| `propionic_acid` | 丙酸 | `C3H6O2` | 液 | — | ✓ | 第四批 341 号 | Propionic Acid | 化学品 | #F0F0E8 | K4 |
| `calcium_propionate` | 丙酸钙 | `C6H10CaO4` | 固 | — | ✓ |  | Calcium Propionate | 化学品 | — | K3 |
| `sodium_diacetate` | 双乙酸钠 | `C4H7NaO4` | 固 | — | ✓ |  | Sodium Diacetate | 化学品 | — | K3 |
| `xylose` | 木糖 | `C5H10O5` | 固 | — | ✓ |  | Xylose | 化学品 | — | K2 |
| `xylitol` | 木糖醇 | `C5H12O5` | 固 | — | ✓ |  | Xylitol | 化学品 | — | K2 |
| `erythritol` | 赤藓糖醇 | `C4H10O4` | 固 | — | ✓ |  | Erythritol | 化学品 | — | K2 |
| `maltitol` | 麦芽糖醇 | `C12H24O11` | 固 | — | ✓ |  | Maltitol | 化学品 | — | K2 |
| `o_toluenesulfonyl_chloride` | 邻甲苯磺酰氯 | `C7H7ClO2S` | 液 | — | ✓ |  | o-Toluenesulfonyl Chloride | 化学品 | #E8E0C8 | K4 |
| `o_toluenesulfonamide` | 邻甲苯磺酰胺 | `C7H9NO2S` | 固 | — | ✓ |  | o-Toluenesulfonamide | 化学品 | — | K3 |
| `saccharin` | 糖精 | `C7H5NO3S` | 固 | — | ✓ |  | Saccharin | 化学品 | — | K3 |
| `sodium_saccharin` | 糖精钠 | `C7H4NNaO3S·2H2O` | 固 | — | ✓ |  | Sodium Saccharin | 化学品 | — | K3 |
| `chloroacetic_acid` | 氯乙酸 | `C2H3ClO2` | 固 | — | ✓ |  | Chloroacetic Acid | 化学品 | — | K3 |
| `sodium_chloroacetate` | 氯乙酸钠 | `C2H2ClNaO2` | 固 | — | ✓ |  | Sodium Chloroacetate | 化学品 | — | K3 |
| `cmc_unit` | 羧甲基纤维素钠 | `C8H11NaO7` | 固 | — | ✓ | 以取代度 1 的葡萄糖单元计 | Sodium Carboxymethyl Cellulose | 化学品 | — | K6 |
| `propylene_chlorohydrin` | 1-氯-2-丙醇 | `C3H7ClO` | 液 | — | ✓ |  | 1-Chloro-2-propanol | 化学品 | #F0F0E8 | K4 |
| `propylene_oxide` | 环氧丙烷 | `C3H6O` | 液 | — | ✓ |  | Propylene Oxide | 化学品 | #F0F0F0 | K4 |
| `propylene_glycol` | 1,2-丙二醇 | `C3H8O2` | 液 | — | ✓ |  | 1,2-Propanediol | 化学品 | #F0F0F0 | K4 |
| `ethylene_oxide` | 环氧乙烷 | `C2H4O` | 气 | — | ✓ |  | Ethylene Oxide | 化学品 | #E8E8E8 | K9 |
| `butane` | 丁烷 | `C4H10` | 气 | butane | ✓ |  | Butane | 化学品 | #E8E8E8 | K9 |
| `maleic_anhydride` | 顺丁烯二酸酐（马来酸酐） | `C4H2O3` | 固 | — | ✓ |  | cis-Butenedioic Anhydride (Maleic Anhydride) | 化学品 | — | K3 |
| `maleic_acid` | 顺丁烯二酸（马来酸） | `C4H4O4` | 固 | — | ✓ |  | cis-Butenedioic Acid (Maleic Acid) | 化学品 | — | K3 |
| `fumaric_acid` | 富马酸 | `C4H4O4` | 固 | — | ✓ |  | Fumaric Acid | 化学品 | — | K3 |
| `malic_acid` | DL-苹果酸 | `C4H6O5` | 固 | — | ✓ | 56 A；L 型为 `l_malic_acid`（2026-11-05 拆分） | DL-Malic Acid | 化学品 | — | K3 |
| `tartaric_acid` | L(+)-酒石酸 | `C4H6O6` | 固 | — | ✓ |  | L(+)-Tartaric Acid | 化学品 | — | K3 |
| `potassium_bitartrate` | 酒石酸氢钾（塔塔粉） | `C4H5KO6` | 固 | — | ✓ | 酒石主成分 | Potassium Bitartrate (Cream of Tartar) | 化学品 | — | K3 |
| `calcium_tartrate` | 酒石酸钙 | `C4H4CaO6` | 固 | — | ✓ |  | Calcium Tartrate | 化学品 | — | K3 |
| `epoxysuccinic_acid` | 顺式环氧琥珀酸 | `C4H4O5` | 固 | — | ✓ | 酶法酒石酸前体 | cis-Epoxysuccinic Acid | 化学品 | — | K3 |
| `trisodium_citrate` | 柠檬酸钠 | `C6H5Na3O7·2H2O` | 固 | — | ✓ |  | Sodium Citrate | 化学品 | — | K3 |
| `lycopene` | 番茄红素 | `C40H56` | 固 | — | ✓ | 第四批 203 号；油树脂另设 `tomato_oleoresin`（238） | Lycopene | 化学品 | — | K5 |
| `capsanthin` | 辣椒红素 | `C40H56O3` | 固 | — | ✓ | 辣椒红主成分 | Capsanthin | 化学品 | — | K5 |
| `capsaicin` | 辣椒素 | `C18H27NO3` | 固 | — | ✓ | 脱辣时分出 | Capsaicin | 化学品 | — | K5 |
| `curcumin` | 姜黄素 | `C21H20O6` | 固 | — | ✓ | 姜黄粉另设 `turmeric`（205） | Curcumin | 化学品 | — | K5 |
| `riboflavin` | 核黄素（维生素 B2） | `C17H20N4O6` | 固 | — | ✓ | 第四批 236 号 | Riboflavin (Vitamin B2) | 化学品 | — | K3 |
| `alpha_tocopherol` | d-α-生育酚 | `C29H50O2` | 液 | — | ✓ | 2026-11-11 定：天然 α-生育酚即 d 型，第五批 462 号直接用本物料，显示名改为 d-α-生育酚 | d-alpha-Tocopherol | 化学品 | #C89030 | K4 |
| `epigallocatechin_gallate` | 表没食子儿茶素没食子酸酯 | `C22H18O11` | 固 | — | ✓ | 茶多酚主成分 | Epigallocatechin Gallate | 化学品 | — | K5 |
| `carnosic_acid` | 鼠尾草酸 | `C20H28O4` | 固 | — | ✓ | 迷迭香提取物 | Carnosic Acid | 化学品 | — | K5 |
| `methyl_stearate` | 硬脂酸甲酯 | `C19H38O2` | 液 | — | ✓ | 脂肪酸甲酯代表 | Methyl Stearate | 化学品 | #F0E8C8 | K7 |
| `sucrose_monostearate` | 单硬脂酸蔗糖酯 | `C30H56O12` | 固 | — | ✓ |  | Sucrose Monostearate | 化学品 | — | K8 |
| `sorbitan_monostearate` | 山梨醇酐单硬脂酸酯（司盘 60） | `C24H46O6` | 固 | — | ✓ |  | Sorbitan Monostearate (Span 60) | 化学品 | — | K8 |
| `inosinic_disodium` | 5'-肌苷酸二钠 | `C10H11N4Na2O8P` | 固 | — | ✓ | 第四批 177 号；表 B.3 S0855 | Disodium 5'-Inosinate | 化学品 | — | K3 |
| `guanylic_disodium` | 5'-鸟苷酸二钠 | `C10H12N5Na2O8P` | 固 | — | ✓ | 第四批 176 号；表 B.3 S0854 | Disodium 5'-Guanylate | 化学品 | — | K3 |
| `adenylic_acid` | 5'-腺苷酸 | `C10H14N5O7P` | 固 | — | ✓ | 脱氨转化为肌苷酸 | 5'-Adenylic Acid | 化学品 | — | K3 |
| `benzene` | 苯 | `C6H6` | 液 | benzene | ✓ |  | Benzene | 化学品 | #F0F0E0 | K4 |
| `nitrobenzene` | 硝基苯 | `C6H5NO2` | 液 | nitrobenzene | ✓ |  | Nitrobenzene | 化学品 | #E8D060 | K4 |
| `aniline` | 苯胺 | `C6H7N` | 液 | — | ✓ |  | Aniline | 化学品 | #B88850 | K4 |
| `sulfanilic_acid` | 对氨基苯磺酸 | `C6H7NO3S` | 固 | — | ✓ | 重氮组分 | Sulfanilic Acid | 化学品 | — | K3 |
| `naphthalene` | 萘 | `C10H8` | 固 | naphthalene | ✓ |  | Naphthalene | 化学品 | — | K3 |
| `naphthalene_sulfonic_acid` | 2-萘磺酸 | `C10H8O3S` | 固 | — |  | 第三批 80 号：萘高温磺化，碱熔得 2-萘酚钠（`sodium_2_naphtholate`），酸化得 2-萘酚；I20 补（2026-11-11） | Naphthalene-2-sulfonic Acid | 化学品 | — | K3 |
| `beta_naphthol` | 2-萘酚 | `C10H8O` | 固 | — | ✓ |  | 2-Naphthol | 化学品 | — | K3 |
| `schaeffer_acid` | 2-萘酚-6-磺酸 | `C10H8O4S` | 固 | — | ✓ | 偶合组分 | 2-Naphthol-6-sulfonic Acid | 化学品 | — | K3 |
| `n_phenylglycine` | N-苯基甘氨酸 | `C8H9NO2` | 固 | — | ✓ | 合成靛蓝中间体 | N-Phenylglycine | 化学品 | — | K3 |
| `indigo` | 靛青 | `C16H10N2O2` | 固 | — | ✓ | 靛蓝染料，磺化前 | Indigo | 化学品 | — | K3 |
| `indigo_carmine` | 靛蓝 | `C16H8N2Na2O8S2` | 固 | — | ✓ | GB 2760 名称；化学名靛蓝二磺酸钠 | Indigo Carmine | 化学品 | — | K10 |
| `l_aspartic_acid` | L-天冬氨酸 | `C4H7NO4` | 固 | — | ✓ | 表 B.3 S0848 | L-Aspartic Acid | 化学品 | — | K3 |
| `l_phenylalanine` | L-苯丙氨酸 | `C9H11NO2` | 固 | — | ✓ | 表 B.3 S0322 | L-Phenylalanine | 化学品 | — | K3 |
| `phenylalanine_methyl_ester` | L-苯丙氨酸甲酯 | `C10H13NO2` | 液 | — | ✓ |  | L-Phenylalanine Methyl Ester | 化学品 | #F0F0E0 | K4 |
| `acetoacetamide_n_sulfonic_acid` | 乙酰乙酰胺-N-磺酸 | `C4H7NO5S` | 溶液 | — | ✓ | 安赛蜜中间体 | Acetoacetamide-N-sulfonic Acid | 化学品 | #F0F0E0 | K0 |
| `acesulfame_acid` | 乙酰磺胺酸 | `C4H5NO4S` | 固 | — | ✓ |  | Acesulfame Acid | 化学品 | — | K3 |
| `dimethylbutanal` | 3,3-二甲基丁醛 | `C6H12O` | 液 | — | ✓ | 纽甜中间体 | 3,3-Dimethylbutanal | 化学品 | #F0F0E8 | K4 |
| `cyclohexylamine` | 环己胺 | `C6H13N` | 液 | — | ✓ |  | Cyclohexylamine | 化学品 | #F0F0E8 | K4 |
| `cyclamic_acid` | 环己基氨基磺酸 | `C6H13NO3S` | 固 | — | ✓ |  | Cyclamic Acid | 化学品 | — | K3 |
| `p_cresol` | 对甲酚 | `C7H8O` | 固 | — | ✓ |  | p-Cresol | 化学品 | — | K3 |
| `isobutylene` | 异丁烯 | `C4H8` | 气 | — | ✓ |  | Isobutylene | 化学品 | #E8E8E8 | K9 |
| `p_methoxyphenol` | 对甲氧基苯酚 | `C7H8O2` | 固 | — | ✓ |  | p-Methoxyphenol | 化学品 | — | K3 |
| `hydroquinone` | 对苯二酚 | `C6H6O2` | 固 | — | ✓ |  | Hydroquinone | 化学品 | — | K3 |
| `phenol` | 苯酚 | `C6H6O` | 固 | phenol | ✓ |  | Phenol | 化学品 | — | K3 |
| `cumene` | 异丙苯 | `C9H12` | 液 | cumene | ✓ |  | Cumene | 化学品 | #F0F0E8 | K4 |
| `gallic_acid` | 没食子酸 | `C7H6O5` | 固 | — | ✓ |  | Gallic Acid | 化学品 | — | K3 |
| `n_propanol` | 正丙醇 | `C3H8O` | 液 | — | ✓ |  | n-Propanol | 化学品 | #F0F0F8 | K4 |
| `isoamyl_alcohol` | 异戊醇 | `C5H12O` | 液 | — | ✓ | 杂醇油主成分 | Isoamyl Alcohol | 化学品 | #F0F0E8 | K4 |
| `butyric_acid` | 丁酸 | `C4H8O2` | 液 | — | ✓ |  | Butyric Acid | 化学品 | #F0E8D8 | K4 |
| `ricinoleic_acid` | 蓖麻油酸 | `C18H34O3` | 液 | — | ✓ | γ-癸内酯原料 | Ricinoleic Acid | 化学品 | #E8D080 | K7 |
| `vinyl_acetate` | 乙酸乙烯酯 | `C4H6O2` | 液 | vinyl_acetate | ✓ | 胶基单体 | Vinyl Acetate | 化学品 | #F0F0F0 | K4 |
| `furfural` | 糠醛 | `C5H4O2` | 液 | — | ✓ |  | Furfural | 化学品 | #C08030 | K4 |
| `retinyl_acetate` | 维生素 A 乙酸酯 | `C22H32O2` | 固 | — | ✓ | 营养强化剂 | Vitamin A Acetate | 化学品 | — | K3 |
| `cholecalciferol` | 维生素 D3 | `C27H44O` | 固 | — | ✓ | 营养强化剂 | Vitamin D3 | 化学品 | — | K3 |
| `thiamine_hydrochloride` | 盐酸硫胺（维生素 B1） | `C12H18Cl2N4OS` | 固 | — | ✓ | 营养强化剂；表 B.3 S0983 | Thiamine Hydrochloride (Vitamin B1) | 化学品 | — | K3 |
| `nicotinamide` | 烟酰胺 | `C6H6N2O` | 固 | — | ✓ | 营养强化剂 | Nicotinamide | 化学品 | — | K3 |
| `nicotinic_acid` | 烟酸 | `C6H5NO2` | 固 | — | ✓ | 营养强化剂 | Nicotinic Acid | 化学品 | — | K3 |
| `folic_acid` | 叶酸 | `C19H19N7O6` | 固 | — | ✓ | 营养强化剂 | Folic Acid | 化学品 | — | K3 |
| `calcium_gluconate` | 葡萄糖酸钙 | `C12H22CaO14` | 固 | — | ✓ | 营养强化剂 | Calcium Gluconate | 化学品 | — | K3 |
| `zinc_gluconate` | 葡萄糖酸锌 | `C12H22O14Zn` | 固 | — | ✓ | 营养强化剂 | Zinc Gluconate | 化学品 | — | K3 |
| `sodium_iron_edta` | 乙二胺四乙酸铁钠 | `C10H12FeN2NaO8` | 固 | — | ✓ | 营养强化剂 | Sodium Iron EDTA | 化学品 | — | K3 |
| `taurine` | 牛磺酸 | `C2H7NO3S` | 固 | — | ✓ | 营养强化剂 | Taurine | 化学品 | — | K3 |
| `dha` | 二十二碳六烯酸 | `C22H32O2` | 液 | — | ✓ | 藻油营养强化剂 | Docosahexaenoic Acid | 化学品 | #E8C870 | K7 |
| `ethyl_vanillin` | 乙基香兰素 | `C9H10O3` | 固 | — | ✓ | 香料 | Ethyl Vanillin | 化学品 | — | K3 |
| `guaethol` | 邻乙氧基苯酚 | `C8H10O2` | 液 | — | ✓ | 乙基香兰素原料 | Guaethol | 化学品 | #E8E0C0 | K4 |
| `maltol` | 麦芽酚 | `C6H6O3` | 固 | — | ✓ | 香料 | Maltol | 化学品 | — | K3 |
| `ethyl_maltol` | 乙基麦芽酚 | `C7H8O3` | 固 | — | ✓ | 香料 | Ethyl Maltol | 化学品 | — | K3 |
| `citral` | 柠檬醛 | `C10H16O` | 液 | — | ✓ | 香料 | Citral | 化学品 | #F0E070 | K4 |
| `limonene` | d-柠檬烯 | `C10H16` | 液 | — |  | 橙皮油主成分；经亚硝酰氯加成、成肟、水解得 l-香芹酮，为第五批 S0224 B（2026-11-11 由 133 B 移来，11 问题 32）；I20 补（2026-11-11）；表 B.3 S0654 | d-Limonene | 化学品 | #F8E8A0 | K4 |
| `cinnamaldehyde` | 肉桂醛 | `C9H8O` | 液 | — | ✓ | 香料 | Cinnamaldehyde | 化学品 | #E8C060 | K4 |
| `benzaldehyde` | 苯甲醛 | `C7H6O` | 液 | — | ✓ | 香料 | Benzaldehyde | 化学品 | #F0F0D0 | K4 |
| `isoamyl_acetate` | 乙酸异戊酯 | `C7H14O2` | 液 | — | ✓ | 香料，香蕉香 | Isoamyl Acetate | 化学品 | #F0F0E8 | K4 |
| `ethyl_butyrate` | 丁酸乙酯 | `C6H12O2` | 液 | — | ✓ | 香料，菠萝香 | Ethyl Butyrate | 化学品 | #F0F0E8 | K4 |
| `gamma_decalactone` | γ-癸内酯 | `C10H18O2` | 液 | — | ✓ | 香料，桃香 | γ-Decalactone | 化学品 | #F0E8C0 | K4 |
| `furaneol` | 4-羟基-2,5-二甲基-3(2H)-呋喃酮 | `C6H8O3` | 固 | — | ✓ | 香料，焦糖香 | 4-Hydroxy-2,5-dimethyl-3(2H)-furanone | 化学品 | — | K3 |
| `trimethylpyrazine` | 2,3,5-三甲基吡嗪 | `C7H10N2` | 液 | — | ✓ | 香料，烘烤香 | 2,3,5-Trimethylpyrazine | 化学品 | #E8D8A0 | K4 |
| `d_carvone` | d-香芹酮 | `C10H14O` | 液 | — | ✓ | 香料 | d-Carvone | 化学品 | #F0E8C0 | K4 |
| `methyl_cyclopentenolone` | 甲基环戊烯醇酮 | `C6H8O2` | 固 | — | ✓ | 香料 | Methyl Cyclopentenolone | 化学品 | — | K3 |
| `sulfanilic_diazonium` | 对磺基苯重氮盐 | `C6H4N2O3S` | 溶液 | — | ✓ | 偶氮色素重氮组分 | 4-Sulfobenzenediazonium Salt | 化学品 | #E8E0C0 | K0 |
| `hydrazinobenzenesulfonic_acid` | 对肼基苯磺酸 | `C6H8N2O3S` | 固 | — | ✓ | 柠檬黄中间体 | 4-Hydrazinobenzenesulfonic Acid | 化学品 | — | K3 |
| `diethyl_oxaloacetate` | 草酰乙酸二乙酯 | `C8H12O5` | 液 | — | ✓ | 柠檬黄中间体 | Diethyl Oxaloacetate | 化学品 | #E8E0B0 | K4 |
| `naphthionic_acid` | 4-氨基-1-萘磺酸 | `C10H9NO3S` | 固 | — | ✓ | 胭脂红重氮组分 | 4-Amino-1-naphthalenesulfonic Acid | 化学品 | — | K3 |
| `g_acid` | 2-萘酚-6,8-二磺酸 | `C10H8O7S2` | 固 | — | ✓ | 胭脂红偶合组分 | 2-Naphthol-6,8-disulfonic Acid | 化学品 | — | K3 |
| `cresidine_sulfonic_acid` | 4-氨基-5-甲氧基-2-甲基苯磺酸 | `C8H11NO4S` | 固 | — | ✓ | 诱惑红重氮组分 | 4-Amino-5-methoxy-2-methylbenzenesulfonic Acid | 化学品 | — | K3 |
| `benzaldehyde_2_sulfonic_acid` | 苯甲醛-2-磺酸 | `C7H6O4S` | 固 | — | ✓ | 亮蓝中间体 | Benzaldehyde-2-sulfonic Acid | 化学品 | — | K3 |
| `ethyl_sulfobenzyl_aniline` | N-乙基-N-（3-磺苄基）苯胺 | `C15H17NO3S` | 固 | — | ✓ | 亮蓝中间体 | N-Ethyl-N-(3-sulfobenzyl)aniline | 化学品 | — | K3 |
| `tartrazine` | 柠檬黄 | `C16H9N4Na3O9S2` | 固 | — | ✓ | 黄色合成色素 | Tartrazine | 化学品 | — | K10 |
| `sunset_yellow` | 日落黄 | `C16H10N2Na2O7S2` | 固 | — | ✓ | 橙黄合成色素 | Sunset Yellow | 化学品 | — | K10 |
| `ponceau_4r` | 胭脂红 | `C20H11N2Na3O10S3` | 固 | — | ✓ | 红色合成色素 | Ponceau 4R | 化学品 | — | K10 |
| `allura_red` | 诱惑红 | `C18H14N2Na2O8S2` | 固 | — | ✓ | 红色合成色素 | Allura Red | 化学品 | — | K10 |
| `brilliant_blue` | 亮蓝 | `C37H34N2Na2O9S3` | 固 | — | ✓ | 蓝色合成色素 | Brilliant Blue | 化学品 | — | K10 |
| `indoxyl` | 吲哚酚 | `C8H7NO` | 固 | — | ✓ | 靛蓝前体 | Indoxyl | 化学品 | — | K3 |
| `aspartame` | 天门冬酰苯丙氨酸甲酯（阿斯巴甜） | `C14H18N2O5` | 固 | — | ✓ | 高倍甜味剂 | Aspartyl Phenylalanine Methyl Ester (Aspartame) | 化学品 | — | K3 |
| `acesulfame_potassium` | 乙酰磺胺酸钾（安赛蜜） | `C4H4KNO4S` | 固 | — | ✓ | 高倍甜味剂 | Acesulfame Potassium (Acesulfame K) | 化学品 | — | K3 |
| `sucrose_6_acetate` | 蔗糖-6-乙酸酯 | `C14H24O12` | 固 | — | ✓ | 三氯蔗糖中间体 | Sucrose-6-acetate | 化学品 | — | K3 |
| `sucralose` | 三氯蔗糖 | `C12H19Cl3O8` | 固 | — | ✓ | 高倍甜味剂 | Sucralose | 化学品 | — | K3 |
| `dimethylformamide` | N,N-二甲基甲酰胺 | `C3H7NO` | 液 | — | ✓ | 溶剂，氯化助剂 | N,N-Dimethylformamide | 化学品 | #F0F0F0 | K4 |
| `sodium_methoxide` | 甲醇钠 | `CH3NaO` | 固 | — | ✓ | 脱乙酰催化剂 | Sodium Methoxide | 化学品 | — | K3 |
| `dibutyltin_oxide` | 二丁基氧化锡 | `C8H18OSn` | 固 | — | ✓ | 选择性酰化催化剂 | Dibutyltin Oxide | 催化剂 | — | — |
| `tert_butyl_chloride` | 叔丁基氯 | `C4H9Cl` | 液 | — | ✓ | 纽甜中间体 | tert-Butyl Chloride | 化学品 | #F0F0F0 | K4 |
| `chlorodimethylbutane` | 1-氯-3,3-二甲基丁烷 | `C6H13Cl` | 液 | — | ✓ | 纽甜中间体 | 1-Chloro-3,3-dimethylbutane | 化学品 | #F0F0F0 | K4 |
| `neotame` | 纽甜 | `C20H30N2O5` | 固 | — | ✓ | 高倍甜味剂 | Neotame | 化学品 | — | K3 |
| `sodium_cyclamate` | 环己基氨基磺酸钠（甜蜜素） | `C6H12NNaO3S` | 固 | — | ✓ | 高倍甜味剂 | Sodium Cyclohexylsulfamate (Cyclamate) | 化学品 | — | K3 |
| `isobutane` | 异丁烷 | `C4H10` | 气 | — | ✓ | 脱氢制异丁烯 | Isobutane | 化学品 | #E8E8E8 | K9 |
| `bht` | 二丁基羟基甲苯 | `C15H24O` | 固 | — | ✓ | 合成抗氧化剂 | Butylated Hydroxytoluene | 化学品 | — | K3 |
| `bha` | 丁基羟基茴香醚 | `C11H16O2` | 固 | — | ✓ | 合成抗氧化剂，以 3-异构体计 | Butylated Hydroxyanisole | 化学品 | — | K3 |
| `tbhq` | 特丁基对苯二酚 | `C10H14O2` | 固 | — | ✓ | 合成抗氧化剂 | tert-Butylhydroquinone | 化学品 | — | K3 |
| `p_benzoquinone` | 对苯醌 | `C6H4O2` | 固 | — | ✓ | 对苯二酚前体 | p-Benzoquinone | 化学品 | — | K3 |
| `propyl_gallate` | 没食子酸丙酯 | `C10H12O5` | 固 | — | ✓ | 合成抗氧化剂 | Propyl Gallate | 化学品 | — | K3 |
| `benzal_chloride` | 苄叉二氯 | `C7H6Cl2` | 液 | — | ✓ | 苯甲醛中间体 | Benzal Chloride | 化学品 | #E8E8D8 | K4 |
| `ethyl_vanillylmandelic_acid` | 3-乙氧基-4-羟基扁桃酸 | `C10H12O5` | 溶液 | — | ✓ | 乙基香兰素中间体 | 3-Ethoxy-4-hydroxymandelic Acid | 化学品 | #E8E0C8 | K0 |
| `diacetyl` | 2,3-丁二酮 | `C4H6O2` | 液 | — | ✓ | 吡嗪原料，奶油香 | 2,3-Butanedione | 化学品 | #E8D040 | K4 |
| `propylenediamine` | 1,2-丙二胺 | `C3H10N2` | 液 | — | ✓ | 吡嗪原料 | 1,2-Diaminopropane | 化学品 | #F0F0E8 | K4 |
| `dihydrotrimethylpyrazine` | 二氢三甲基吡嗪 | `C7H12N2` | 液 | — | ✓ | 吡嗪中间体 | Dihydrotrimethylpyrazine | 化学品 | #E8D8A0 | K4 |
| `sodium_isethionate` | 羟乙基磺酸钠 | `C2H5NaO4S` | 溶液 | — | ✓ | 牛磺酸中间体 | Sodium Isethionate | 化学品 | #F0F0F0 | K0 |
| `sodium_taurinate` | 牛磺酸钠 | `C2H6NNaO3S` | 溶液 | — | ✓ | 牛磺酸中间体 | Sodium Taurinate | 化学品 | #F0F0F0 | K0 |
| `edta` | 乙二胺四乙酸 | `C10H16N2O8` | 固 | — | ✓ | 螯合剂 | Ethylenediaminetetraacetic Acid | 化学品 | — | K3 |
| `cholesterol` | 胆固醇 | `C27H46O` | 固 | — | ✓ | 羊毛脂提取 | Cholesterol | 化学品 | — | K3 |
| `dehydrocholesterol` | 7-脱氢胆固醇 | `C27H44O` | 固 | — | ✓ | 维生素 D3 前体 | 7-Dehydrocholesterol | 化学品 | — | K3 |
| `retinol` | 视黄醇 | `C20H30O` | 液 | — | ✓ | 维生素 A | Retinol | 化学品 | #E8C040 | K4 |
| `methylpyridine` | 3-甲基吡啶 | `C6H7N` | 液 | — | ✓ | 烟酰胺原料 | 3-Methylpyridine | 化学品 | #F0F0E0 | K4 |
| `cyanopyridine` | 3-氰基吡啶 | `C6H4N2` | 固 | — | ✓ | 烟酰胺中间体 | 3-Cyanopyridine | 化学品 | — | K3 |
| `abietic_acid` | 松香酸 | `C20H30O2` | 固 | — | ✓ | 松香主成分 | Abietic Acid | 化学品 | — | K5 |
| `cyclohexane` | 环己烷 | `C6H12` | 液 | — |  | 第四批：144、284 | Cyclohexane | 化学品 | #E4ECEC | K4 |
| `cyclohexanol` | 环己醇 | `C6H12O` | 液 | — |  | 第四批：144（KA 油按此计，游戏简化） | Cyclohexanol | 化学品 | #E8EAE0 | K4 |
| `sodium_tartrate` | 酒石酸钠 | `C4H4Na2O6` | 溶液 | — |  | 第四批：161 | Sodium Tartrate | 化学品 | #E4EEE8 | K0 |
| `ferric_citrate` | 柠檬酸铁 | `C6H5FeO7` | 溶液 | — |  | 第四批：160；固体见 `ferric_citrate_powder`（483，2026-11-11） | Ferric Citrate | 化学品 | #A0602A | K0 |
| `methyl_chloride` | 氯甲烷 | `CH3Cl` | 气 | — |  | 第四批：163、304、314、363、366 | Chloromethane | 化学品 | #DCE6E0 | K9 |
| `dimethyldichlorosilane` | 二甲基二氯硅烷 | `C2H6Cl2Si` | 液 | — |  | 第四批：163 | Dimethyldichlorosilane | 化学品 | #E2E6EE | K4 |
| `methyltrichlorosilane` | 甲基三氯硅烷 | `CH3Cl3Si` | 液 | — |  | 第四批：163 副产 | Methyltrichlorosilane | 化学品 | #E0E4EC | K4 |
| `trimethylchlorosilane` | 三甲基氯硅烷 | `C3H9ClSi` | 液 | — |  | 第四批：163（制封端剂） | Trimethylchlorosilane | 化学品 | #E4E8F0 | K4 |
| `octamethylcyclotetrasiloxane` | 八甲基环四硅氧烷 | `C8H24O4Si4` | 液 | — |  | 第四批：163 | Octamethylcyclotetrasiloxane | 化学品 | #E8ECF2 | K4 |
| `hexamethyldisiloxane` | 六甲基二硅氧烷 | `C6H18OSi2` | 液 | — |  | 第四批：163（封端剂） | Hexamethyldisiloxane | 化学品 | #E6EAF2 | K4 |
| `sodium_formate` | 甲酸钠 | `CHNaO2` | 固 | — |  | 第四批：167、274（副产）；GB 2760 表 C.2 加工助剂；第五批 453 号 | Sodium Formate | 化学品 | — | K3 |
| `guanylic_acid` | 5'-鸟苷酸 | `C10H14N5O8P` | 固 | — |  | 第四批：176 | 5'-Guanylic Acid | 化学品 | — | K3 |
| `inosinic_acid` | 5'-肌苷酸 | `C10H13N4O8P` | 固 | — |  | 第四批：177；73 已用此式 | 5'-Inosinic Acid | 化学品 | — | K3 |
| `succinic_acid` | 琥珀酸 | `C4H6O4` | 固 | — |  | 第四批：178；以后琥珀酸单甘油酯等；表 B.3 S0351 | Succinic Acid | 化学品 | — | K3 |
| `l_cystine` | L-胱氨酸 | `C6H12N2O4S2` | 固 | — |  | 第四批：181；表 B.3 S0882 | L-Cystine | 化学品 | — | K3 |
| `l_cysteine` | L-半胱氨酸 | `C3H7NO2S` | 固 | — |  | 第四批：181 | L-Cysteine | 化学品 | — | K3 |
| `magnesium_bicarbonate` | 碳酸氢镁 | `Mg(HCO3)2` | 溶液 | — |  | 第四批：182 | Magnesium Bicarbonate | 化学品 | #E6EEF0 | K0 |
| `nitronaphthalene` | 1-硝基萘 | `C10H7NO2` | 固 | — |  | 第四批：194、199（81 同用） | 1-Nitronaphthalene | 化学品 | — | K3 |
| `naphthylamine` | 1-萘胺 | `C10H9N` | 固 | — |  | 第四批：194、199（81 同用） | 1-Naphthylamine | 化学品 | — | K3 |
| `naphthionic_diazonium` | 4-磺基-1-萘重氮盐 | `C10H6N2O3S` | 溶液 | — |  | 第四批：194、199（81 同用） | 4-Sulfo-1-naphthalenediazonium Salt | 化学品 | #E0C878 | K0 |
| `r_acid` | 2-萘酚-3,6-二磺酸（R 酸） | `C10H8O7S2` | 固 | — |  | 第四批：194 | 2-Naphthol-3,6-disulfonic Acid (R Acid) | 化学品 | — | K3 |
| `nevile_winther_acid` | 1-萘酚-4-磺酸 | `C10H8O4S` | 固 | — |  | 第四批：199 | 1-Naphthol-4-sulfonic Acid (Nevile–Winther Acid) | 化学品 | — | K3 |
| `phthalic_anhydride` | 邻苯二甲酸酐 | `C8H4O3` | 固 | — |  | 第四批：195、202 | Phthalic Anhydride | 化学品 | — | K3 |
| `benzenedisulfonic_acid` | 间苯二磺酸 | `C6H6O6S2` | 溶液 | — |  | 第四批：195 | Benzene-1,3-disulfonic Acid | 化学品 | #E8E2C8 | K0 |
| `resorcinol` | 间苯二酚 | `C6H6O2` | 固 | — |  | 第四批：195、325；表 B.3 S0110 | Resorcinol | 化学品 | — | K3 |
| `fluorescein` | 荧光素 | `C20H12O5` | 固 | — |  | 第四批：195 | Fluorescein | 化学品 | — | K3 |
| `tetraiodofluorescein` | 四碘荧光素 | `C20H8I4O5` | 固 | — |  | 第四批：195 | Tetraiodofluorescein | 化学品 | — | K3 |
| `naphthalene_trisulfonic_acid` | 萘-1,3,6-三磺酸 | `C10H8O9S3` | 溶液 | — |  | 第四批：196 | Naphthalene-1,3,6-trisulfonic Acid | 化学品 | #E4DCC0 | K0 |
| `nitronaphthalene_trisulfonic_acid` | 1-硝基萘-3,6,8-三磺酸 | `C10H7NO11S3` | 溶液 | — |  | 第四批：196 | 1-Nitronaphthalene-3,6,8-trisulfonic Acid | 化学品 | #E0C060 | K0 |
| `koch_acid` | 1-萘胺-3,6,8-三磺酸（科赫酸） | `C10H9NO9S3` | 固 | — |  | 第四批：196 | 1-Naphthylamine-3,6,8-trisulfonic Acid (Koch Acid) | 化学品 | — | K3 |
| `h_acid` | 1-氨基-8-萘酚-3,6-二磺酸（H 酸） | `C10H9NO7S2` | 固 | — |  | 第四批：196 | 1-Amino-8-naphthol-3,6-disulfonic Acid (H Acid) | 化学品 | — | K3 |
| `acetyl_h_acid` | N-乙酰 H 酸 | `C12H11NO8S2` | 固 | — |  | 第四批：196 | N-Acetyl H Acid | 化学品 | — | K3 |
| `quinaldine` | 2-甲基喹啉 | `C10H9N` | 液 | — |  | 第四批：202 | 2-Methylquinoline (Quinaldine) | 化学品 | #D8C890 | K4 |
| `quinophthalone` | 喹酞酮（喹啉黄 SS） | `C18H11NO2` | 固 | — |  | 第四批：202 | Quinophthalone (Quinoline Yellow SS) | 化学品 | — | K3 |
| `chlorobenzene` | 氯苯 | `C6H5Cl` | 液 | — |  | 第四批：204、337、339 | Chlorobenzene | 化学品 | #E2EAE6 | K4 |
| `triphenylphosphine` | 三苯基膦 | `C18H15P` | 固 | — |  | 第四批：204 | Triphenylphosphine | 化学品 | — | K3 |
| `triphenylphosphine_oxide` | 三苯基氧膦 | `C18H15OP` | 固 | — |  | 第四批：204（副产） | Triphenylphosphine Oxide | 化学品 | — | K3 |
| `retinyl_phosphonium_chloride` | 视黄基三苯基氯化鏻 | `C38H44ClP` | 固 | — |  | 第四批：204 | Retinyltriphenylphosphonium Chloride | 化学品 | — | K3 |
| `octatrienedial` | 2,7-二甲基-2,4,6-辛三烯二醛（C10 二醛） | `C10H12O2` | 固 | — |  | 第四批：204（197 合成同用） | 2,7-Dimethyl-2,4,6-octatrienedial | 化学品 | — | K3 |
| `pheophytin_a` | 脱镁叶绿素 a | `C55H74N4O5` | 固 | — |  | 第四批：241 | Pheophytin A | 化学品 | — | K5 |
| `geniposide` | 京尼平苷 | `C17H24O10` | 溶液 | — |  | 第四批：218 | Geniposide | 化学品 | #E8E0C4 | K0 |
| `genipin` | 京尼平 | `C11H14O5` | 固 | — |  | 第四批：218 | Genipin | 化学品 | — | K5 |
| `potassium_palmitate` | 棕榈酸钾 | `C16H31KO2` | 固 | — |  | 第四批：234（皂化副产） | Potassium Palmitate | 化学品 | — | K7 |
| `potassium_norbixinate` | 降红木素钾 | `C24H26K2O4` | 溶液 | — |  | 第四批：232 | Potassium Norbixinate | 化学品 | #D8601C | K0 |
| `n_hexane` | 正己烷 | `C6H14` | 液 | — |  | 第四批：207、230、231、234；20、60 已用；第五批 437 号 | n-Hexane | 化学品 | #E6EEF0 | K4 |
| `ethyl_acetate` | 乙酸乙酯 | `C4H8O2` | 液 | — |  | 第四批：238、277（副产）、329；59、66 已作溶剂；第五批 436 号 | Ethyl Acetate | 化学品 | #E2EEEA | K4 |
| `oleic_acid` | 油酸 | `C18H34O2` | 液 | — |  | 第四批：246、273；植物油或动物脂脂肪酸（276）分出；表 B.2 N252 | Oleic Acid | 化学品 | #E8D890 | K7 |
| `lauric_acid` | 月桂酸 | `C12H24O2` | 固 | — |  | 第四批：256、257、324；第五批 458 号 | Lauric Acid | 化学品 | — | K7 |
| `palmitic_acid` | 棕榈酸 | `C16H32O2` | 固 | — |  | 第四批：248、258、323、330；棕榈油或动物脂脂肪酸精馏；表 B.3 S0314 | Palmitic Acid | 化学品 | — | K7 |
| `caprylic_acid` | 辛酸 | `C8H16O2` | 液 | — |  | 第四批：252、342；表 B.3 S0309 | Octanoic Acid | 化学品 | #ECE6CC | K7 |
| `capric_acid` | 癸酸 | `C10H20O2` | 固 | — |  | 第四批：252；表 B.3 S0311 | Decanoic Acid | 化学品 | — | K7 |
| `sorbitan_monolaurate` | 山梨醇酐单月桂酸酯（司盘 20） | `C18H34O6` | 液 | — |  | 第四批 256 号；用于 256、257 | Sorbitan Monolaurate (Span 20) | 化学品 | #D8B860 | K8 |
| `sorbitan_monopalmitate` | 山梨醇酐单棕榈酸酯（司盘 40） | `C22H42O6` | 固 | — |  | 第四批 248 号；用于 248、258 | Sorbitan Monopalmitate (Span 40) | 化学品 | — | K8 |
| `sorbitan_monooleate` | 山梨醇酐单油酸酯（司盘 80） | `C24H44O6` | 液 | — |  | 第四批 246 号；用于 246；72 已用 | Sorbitan Monooleate (Span 80) | 化学品 | #C8A048 | K8 |
| `diacetyltartaric_anhydride` | 双乙酰酒石酸酐 | `C8H8O7` | 固 | — |  | 第四批：249 | Diacetyltartaric Anhydride | 化学品 | — | K3 |
| `triacetin` | 三乙酸甘油酯 | `C9H14O6` | 液 | — |  | 第四批：259；第五批 583 号 | Glyceryl Triacetate | 化学品 | #EAEAD8 | K4 |
| `octene` | 1-辛烯 | `C8H16` | 液 | — |  | 第四批：262；表 B.3 S1140 | 1-Octene | 化学品 | #E4ECEA | K4 |
| `octenyl_succinic_anhydride` | 辛烯基琥珀酸酐 | `C12H18O3` | 液 | — |  | 第四批：262 | Octenylsuccinic Anhydride | 化学品 | #E8E4C8 | K4 |
| `succinic_anhydride` | 丁二酸酐（琥珀酸酐） | `C4H4O3` | 固 | — |  | 第四批：266 | Butanedioic Anhydride (Succinic Anhydride) | 化学品 | — | K3 |
| `diethanolamine` | 二乙醇胺 | `C4H11NO2` | 液 | — |  | 第四批：273 | Diethanolamine | 化学品 | #EAEAE0 | K4 |
| `morpholine` | 吗啉 | `C4H9NO` | 液 | — |  | 第四批：273 | Morpholine | 化学品 | #E6EAE6 | K4 |
| `formaldehyde` | 甲醛（福尔马林） | `CH2O` | 溶液 | — |  | 第四批：274、281（约 37% 水溶液） | Formaldehyde (Formalin) | 化学品 | #E4EEF0 | K4 |
| `pentaerythritol` | 季戊四醇 | `C5H12O4` | 固 | — |  | 第四批：274 | Pentaerythritol | 化学品 | — | K3 |
| `ethylene_glycol` | 乙二醇 | `C2H6O2` | 液 | — |  | 第四批：279 | Ethylene Glycol | 化学品 | #E8F0EC | K4 |
| `ethylene_dichloride` | 1,2-二氯乙烷 | `C2H4Cl2` | 液 | — |  | 第四批：281 | 1,2-Dichloroethane | 化学品 | #E8ECE2 | K4 |
| `ethylenediamine` | 乙二胺 | `C2H8N2` | 液 | — |  | 第四批：281 | Ethylenediamine | 化学品 | #ECE8DC | K4 |
| `tetrasodium_edta` | 乙二胺四乙酸四钠 | `C10H12N2Na4O8` | 溶液 | — |  | 第四批：281；117、328 同用 | Tetrasodium EDTA | 化学品 | #E6EEEE | K0 |
| `distannous_citrate` | 柠檬酸二亚锡 | `C6H4O7Sn2` | 固 | — |  | 第四批：282 | Distannous Citrate | 化学品 | — | K3 |
| `acrylic_acid` | 丙烯酸 | `C3H4O2` | 液 | — |  | 第四批：310 | Acrylic Acid | 化学品 | #E8EEEA | K4 |
| `ketogluconic_acid` | 2-酮-D-葡萄糖酸 | `C6H10O7` | 溶液 | — |  | 第四批：319、326 | 2-Keto-D-gluconic Acid | 化学品 | #EEE8D4 | K0 |
| `methyl_ketogluconate` | 2-酮-D-葡萄糖酸甲酯 | `C7H12O7` | 固 | — |  | 第四批：319、326 | Methyl 2-Keto-D-gluconate | 化学品 | — | K3 |
| `lauryl_alcohol` | 1-十二醇（月桂醇） | `C12H26O` | 液 | — |  | 第四批：324；表 B.3 S0024 | 1-Dodecanol (Lauryl Alcohol) | 化学品 | #EEEEE4 | K7 |
| `hexanoic_acid` | 己酸 | `C6H12O2` | 液 | — |  | 第四批：325；表 B.3 S0304 | Hexanoic Acid | 化学品 | #EEE8D0 | K7 |
| `acrylonitrile` | 丙烯腈 | `C3H3N` | 液 | — |  | 第四批：324 | Acrylonitrile | 化学品 | #E6EEE2 | K4 |
| `thiodipropionitrile` | 3,3'-硫代二丙腈 | `C6H8N2S` | 液 | — |  | 第四批：324 | 3,3'-Thiodipropionitrile | 化学品 | #E8E6D4 | K4 |
| `thiodipropionic_acid` | 3,3'-硫代二丙酸 | `C6H10O4S` | 固 | — |  | 第四批：324 | 3,3'-Thiodipropionic Acid | 化学品 | — | K3 |
| `diisopropylbenzene` | 1,3-二异丙苯 | `C12H18` | 液 | — |  | 第四批：325 | 1,3-Diisopropylbenzene | 化学品 | #E2E8EC | K4 |
| `hexanoylresorcinol` | 4-己酰间苯二酚 | `C12H16O3` | 固 | — |  | 第四批：325 | 4-Hexanoylresorcinol | 化学品 | — | K3 |
| `p_hydroxybenzoic_acid` | 对羟基苯甲酸 | `C7H6O3` | 固 | — |  | 第四批：335、343、347 | p-Hydroxybenzoic Acid | 化学品 | — | K3 |
| `methyl_paraben` | 对羟基苯甲酸甲酯 | `C8H8O3` | 固 | — |  | 第四批：343 | Methyl p-Hydroxybenzoate | 化学品 | — | K3 |
| `p_nitrochlorobenzene` | 对硝基氯苯 | `C6H4ClNO2` | 固 | — |  | 第四批：337 | p-Nitrochlorobenzene | 化学品 | — | K3 |
| `p_nitrophenetole` | 对硝基苯乙醚 | `C8H9NO3` | 固 | — |  | 第四批：337 | p-Nitrophenetole | 化学品 | — | K3 |
| `p_phenetidine` | 对氨基苯乙醚 | `C8H11NO` | 液 | — |  | 第四批：337 | p-Phenetidine | 化学品 | #C08850 | K4 |
| `phosgene` | 光气 | `CCl2O` | 气 | — |  | 第四批：344 | Phosgene | 化学品 | #E4EADC | K9 |
| `methyl_chloroformate` | 氯甲酸甲酯 | `C2H3ClO2` | 液 | — |  | 第四批：344 | Methyl Chloroformate | 化学品 | #E8ECE6 | K4 |
| `glycyrrhizic_acid` | 甘草酸 | `C42H62O16` | 固 | — |  | 第四批：352、353、362；表 B.3 S0881 | Glycyrrhizic Acid | 化学品 | — | K5 |
| `isobutyric_acid` | 异丁酸 | `C4H8O2` | 液 | — |  | 第四批：354；表 B.3 S0297 | Isobutyric Acid | 化学品 | #ECE8D8 | K4 |
| `calcium_isobutyrate` | 异丁酸钙 | `C8H14CaO4` | 固 | — |  | 第四批：354 | Calcium Isobutyrate | 化学品 | — | K3 |
| `diisopropyl_ketone` | 二异丙基酮 | `C7H14O` | 液 | — |  | 第四批：354 | Diisopropyl Ketone | 化学品 | #E8ECE4 | K4 |
| `tetramethylthietanone` | 2,2,4,4-四甲基-3-硫杂环丁酮 | `C7H12OS` | 液 | — |  | 第四批：354 | 2,2,4,4-Tetramethyl-3-thietanone | 化学品 | #E8E0B8 | K4 |
| `tetramethylthietanamine` | 2,2,4,4-四甲基-3-硫杂环丁胺 | `C7H15NS` | 液 | — |  | 第四批：354 | 2,2,4,4-Tetramethyl-3-thietanamine | 化学品 | #E4DEC0 | K4 |
| `alaninamide` | DL-丙氨酰胺 | `C3H8N2O` | 固 | — |  | 第四批：354 | DL-Alaninamide | 化学品 | — | K3 |
| `d_alanine` | D-丙氨酸 | `C3H7NO2` | 固 | — |  | 第四批：354 | D-Alanine | 化学品 | — | K3 |
| `lactose` | 乳糖 | `C12H22O11·H2O` | 固 | — |  | 第四批：355、365 | Lactose | 化学品 | — | K2 |
| `ethoxy_methoxybenzaldehyde` | 3-乙氧基-4-甲氧基苯甲醛 | `C10H12O3` | 固 | — |  | 第四批：363 | 3-Ethoxy-4-methoxybenzaldehyde | 化学品 | — | K3 |
| `isovanillin` | 异香兰素 | `C8H8O3` | 固 | — |  | 第四批：363 | Isovanillin | 化学品 | — | K3 |
| `hydroxymethoxycinnamaldehyde` | 3-羟基-4-甲氧基肉桂醛 | `C10H10O3` | 固 | — |  | 第四批：363 | 3-Hydroxy-4-methoxycinnamaldehyde | 化学品 | — | K3 |
| `hydroxymethoxyphenylpropanal` | 3-(3-羟基-4-甲氧基苯基)丙醛 | `C10H12O3` | 液 | — |  | 第四批：363 | 3-(3-Hydroxy-4-methoxyphenyl)propanal | 化学品 | #E0D4A8 | K4 |
| `urea` | 尿素 | `CH4N2O` | 固 | — |  | 第四批：366；86 已用 | Urea | 化学品 | — | K3 |
| `methylamine` | 甲胺 | `CH5N` | 气 | — |  | 第四批：366 | Methylamine | 化学品 | #E0E8EA | K9 |
| `dimethylurea` | 1,3-二甲基脲 | `C3H8N2O` | 固 | — |  | 第四批：366 | 1,3-Dimethylurea | 化学品 | — | K3 |
| `cyanoacetic_acid` | 氰乙酸 | `C3H3NO2` | 固 | — |  | 第四批：366 | Cyanoacetic Acid | 化学品 | — | K3 |
| `aminodimethyluracil` | 6-氨基-1,3-二甲基尿嘧啶 | `C6H9N3O2` | 固 | — |  | 第四批：366 | 6-Amino-1,3-dimethyluracil | 化学品 | — | K3 |
| `theophylline` | 茶碱 | `C7H8N4O2` | 固 | — |  | 第四批：366 | Theophylline | 化学品 | — | K3 |
| `formic_acid` | 甲酸（蚁酸） | `CH2O2` | 液 | — |  | 第四批：366；表 B.3 S0346 | Methanoic Acid (Formic Acid) | 化学品 | #EAEEEE | K4 |
| `l_malic_acid` | L-苹果酸 | `C4H6O5` | 固 | — |  | 第四批 140 号；56 B 同产；DL 型为 `malic_acid` | L-Malic Acid | 化学品 | — | K3 |
| `adipic_acid` | 己二酸 | `C6H10O4` | 固 | — |  | 第四批 144 号；表 B.3 S0305 | Adipic Acid | 化学品 | — | K3 |
| `potassium_citrate` | 柠檬酸钾 | `C6H5K3O7·H2O` | 固 | — |  | 第四批 147 号 | Potassium Citrate | 化学品 | — | K3 |
| `monosodium_citrate` | 柠檬酸一钠 | `C6H7NaO7` | 固 | — |  | 第四批 149 号 | Monosodium Citrate | 化学品 | — | K3 |
| `monosodium_fumarate` | 富马酸一钠 | `C4H3NaO4` | 固 | — |  | 第四批 152 号 | Monosodium Fumarate | 化学品 | — | K3 |
| `sodium_gluconate` | 葡萄糖酸钠 | `C6H11NaO7` | 固 | — |  | 第四批 153 号 | Sodium Gluconate | 化学品 | — | K3 |
| `sodium_dl_malate` | DL-苹果酸钠 | `C4H4Na2O5` | 固 | — |  | 第四批 154 号；二钠盐 | Sodium DL-Malate | 化学品 | — | K3 |
| `sodium_l_malate` | L-苹果酸钠 | `C4H4Na2O5` | 固 | — |  | 第四批 155 号；二钠盐 | Sodium L-Malate | 化学品 | — | K3 |
| `magnesium_stearate` | 硬脂酸镁 | `C36H70MgO4` | 固 | — |  | 第四批 157 号 | Magnesium Stearate | 化学品 | — | K7 |
| `ferrous_gluconate` | 葡萄糖酸亚铁 | `C12H22FeO14·2H2O` | 固 | — |  | 第四批 175 号 | Ferrous Gluconate | 化学品 | — | K3 |
| `disodium_succinate` | 琥珀酸二钠 | `C4H4Na2O4·6H2O` | 固 | — |  | 第四批 178 号；表 B.3 S0853 | Disodium Succinate | 化学品 | — | K3 |
| `l_alanine` | L-丙氨酸 | `C3H7NO2` | 固 | — |  | 第四批 179 号；表 B.3 S0342 | L-Alanine | 化学品 | — | K3 |
| `glycine` | 氨基乙酸（甘氨酸） | `C2H5NO2` | 固 | — |  | 第四批 180 号；表 B.3 S0324 | Aminoacetic Acid (Glycine) | 化学品 | — | K3 |
| `l_cysteine_hydrochloride` | L-半胱氨酸盐酸盐 | `C3H8ClNO2S·H2O` | 固 | — |  | 第四批 181 号 | L-Cysteine Hydrochloride | 化学品 | — | K3 |
| `potassium_lactate` | 乳酸钾 | `C3H5KO3` | 溶液 | — |  | 第四批 188 号；约 60% 水溶液；界限见 10 §1.5 | Potassium Lactate | 化学品 | #EEECDC | K4 |
| `sodium_lactate` | 乳酸钠 | `C3H5NaO3` | 溶液 | — |  | 第四批 189 号；约 60% 水溶液；界限见 10 §1.5 | Sodium Lactate | 化学品 | #EEEADA | K4 |
| `amaranth` | 苋菜红 | `C20H11N2Na3O10S3` | 固 | — |  | 第四批 194 号 | Amaranth | 化学品 | — | K10 |
| `erythrosine` | 赤藓红 | `C20H6I4Na2O5` | 固 | — |  | 第四批 195 号 | Erythrosine | 化学品 | — | K10 |
| `new_red` | 新红 | `C18H12N3Na3O11S3` | 固 | — |  | 第四批 196 号 | New Red | 化学品 | — | K10 |
| `azorubine` | 酸性红（偶氮玉红） | `C20H12N2Na2O7S2` | 固 | — |  | 第四批 199 号 | Azorubine (Carmoisine) | 化学品 | — | K10 |
| `quinoline_yellow` | 喹啉黄 | `C18H9NNa2O8S2` | 固 | — |  | 第四批 202 号 | Quinoline Yellow | 化学品 | — | K10 |
| `apocarotenal` | β-阿朴-8'-胡萝卜素醛 | `C30H40O` | 固 | — |  | 第四批 204 号 | β-Apo-8'-carotenal | 化学品 | — | K5 |
| `bixin` | 胭脂树橙 | `C25H30O4` | 固 | — |  | 第四批 232 号；油溶型，水溶型见 `norbixin`；表 B.2 N362 | Bixin | 化学品 | — | K5 |
| `carminic_acid` | 胭脂虫红 | `C22H20O13` | 固 | — |  | 第四批 233 号 B；铝色淀为 `carmine` | Carminic Acid | 化学品 | — | K5 |
| `lutein` | 叶黄素 | `C40H56O2` | 固 | — |  | 第四批 234 号 | Lutein | 化学品 | — | K5 |
| `copper_chlorophyll` | 叶绿素铜 | `C55H72CuN4O5` | 固 | — |  | 第四批 241 号 | Copper Chlorophyll | 化学品 | — | K5 |
| `potassium_copper_chlorophyllin` | 叶绿素铜钾盐 | `C34H31CuK3N4O6` | 固 | — |  | 第四批 243 号 | Potassium Copper Chlorophyllin | 化学品 | — | K5 |
| `sorbitan_tristearate` | 山梨醇酐三硬脂酸酯（司盘 65） | `C60H114O8` | 固 | — |  | 第四批 245 号 | Sorbitan Tristearate (Span 65) | 化学品 | — | K8 |
| `xylitan_monostearate` | 木糖醇酐单硬脂酸酯 | `C23H44O5` | 固 | — |  | 第四批 247 号 | Xylitan Monostearate | 化学品 | — | K8 |
| `sodium_stearoyl_lactylate` | 硬脂酰乳酸钠 | `C24H43NaO6` | 固 | — |  | 第四批 250 号；以硬脂酰-2-乳酸钠计 | Sodium Stearoyl Lactylate | 化学品 | — | K8 |
| `propylene_glycol_monostearate` | 丙二醇脂肪酸酯 | `C21H42O3` | 固 | — |  | 第四批 254 号；混合酯，以丙二醇单硬脂酸酯计 | Propylene Glycol Esters of Fatty Acids | 化学品 | — | K8 |
| `potassium_stearate` | 硬脂酸钾 | `C18H35KO2` | 固 | — |  | 第四批 260 号 | Potassium Stearate | 化学品 | — | K7 |
| `calcium_stearate` | 硬脂酸钙 | `C36H70CaO4` | 固 | — |  | 第四批 267 号 | Calcium Stearate | 化学品 | — | K7 |
| `disodium_edta` | 乙二胺四乙酸二钠 | `C10H14N2Na2O8·2H2O` | 固 | — |  | 第四批 281 号 | Disodium EDTA | 化学品 | — | K3 |
| `disodium_stannous_citrate` | 柠檬酸亚锡二钠 | `C6H6Na2O8Sn` | 固 | — |  | 第四批 282 号；式量 370.79（专利 CN1995005A），结构未确认 | Disodium Stannous Citrate | 化学品 | — | K3 |
| `alpha_cyclodextrin` | α-环状糊精 | `C36H60O30` | 固 | — |  | 第四批 284 号 | α-Cyclodextrin | 化学品 | — | K3 |
| `gamma_cyclodextrin` | γ-环状糊精 | `C48H80O40` | 固 | — |  | 第四批 285 号 | γ-Cyclodextrin | 化学品 | — | K3 |
| `beta_cyclodextrin` | β-环状糊精 | `C42H70O35` | 固 | — |  | 第四批 301 号 | β-Cyclodextrin | 化学品 | — | K3 |
| `erythorbic_acid` | D-异抗坏血酸 | `C6H8O6` | 固 | — |  | 第四批 319 号 | D-Isoascorbic Acid (Erythorbic Acid) | 化学品 | — | K3 |
| `phytic_acid` | 肌醇六磷酸（植酸） | `C6H18O24P6` | 溶液 | — |  | 第四批 320 号；约 50% 水溶液；界限见 10 §1.5 | Inositol Hexaphosphate (Phytic Acid) | 化学品 | #E8E0B8 | K4 |
| `calcium_ascorbate` | 抗坏血酸钙 | `C12H14CaO12·2H2O` | 固 | — |  | 第四批 322 号 | Calcium Ascorbate | 化学品 | — | K3 |
| `ascorbyl_palmitate` | 抗坏血酸棕榈酸酯 | `C22H38O7` | 固 | — |  | 第四批 323、330 号 | Ascorbyl Palmitate | 化学品 | — | K3 |
| `dilauryl_thiodipropionate` | 硫代二丙酸二月桂酯 | `C30H58O4S` | 固 | — |  | 第四批 324 号 | Dilauryl Thiodipropionate | 化学品 | — | K3 |
| `hexylresorcinol` | 4-己基间苯二酚 | `C12H18O2` | 固 | — |  | 第四批 325 号 | 4-Hexylresorcinol | 化学品 | — | K3 |
| `sodium_erythorbate` | D-异抗坏血酸钠 | `C6H7NaO6·H2O` | 固 | — |  | 第四批 326 号 | Sodium D-Isoascorbate (Sodium Erythorbate) | 化学品 | — | K3 |
| `calcium_disodium_edta` | 乙二胺四乙酸二钠钙 | `C10H12CaN2Na2O8·2H2O` | 固 | — |  | 第四批 328 号 | Calcium Disodium EDTA | 化学品 | — | K3 |
| `sodium_phytate` | 植酸钠 | `C6H6Na12O24P6` | 固 | — |  | 第四批 331 号；以十二钠盐计 | Sodium Phytate | 化学品 | — | K3 |
| `sodium_propionate` | 丙酸钠 | `C3H5NaO2` | 固 | — |  | 第四批 334 号 | Sodium Propionate | 化学品 | — | K3 |
| `ethyl_paraben` | 对羟基苯甲酸乙酯 | `C9H10O3` | 固 | — |  | 第四批 335 号 | Ethyl p-Hydroxybenzoate (Ethylparaben) | 化学品 | — | K3 |
| `dehydroacetic_acid` | 脱氢乙酸 | `C8H8O4` | 固 | — |  | 第四批 336 号；钠盐见 `sodium_dehydroacetate` | Dehydroacetic Acid | 化学品 | — | K3 |
| `ethoxyquin` | 乙氧基喹 | `C14H19NO` | 液 | — |  | 第四批 337 号 | Ethoxyquin | 化学品 | #B07830 | K4 |
| `diphenyl_ether` | 联苯醚 | `C12H10O` | 固 | — |  | 第四批 339 号；熔点约 27 °C；表 B.3 S0079 | Diphenyl Ether | 化学品 | — | K3 |
| `glyceryl_monocaprylate` | 单辛酸甘油酯 | `C11H22O4` | 固 | — |  | 第四批 342 号 | Glyceryl Monocaprylate | 化学品 | — | K8 |
| `molten_glyceryl_monostearate` | 熔融单硬脂酸甘油酯 | `C21H42O4` | 液 | — |  | 第四批：27 C、249 的分子蒸馏馏出液，冷却结晶得单硬脂酸甘油酯（2026-11-11，用户定：馏出熔融液再冷却结晶） | Molten Glyceryl Monostearate | 化学品 | #EFE4B8 | K8 |
| `molten_propylene_glycol_monostearate` | 熔融丙二醇脂肪酸酯 | `C21H42O3` | 液 | — |  | 第四批：254 A 的分子蒸馏馏出液，冷却结晶得丙二醇脂肪酸酯（2026-11-11，同上） | Molten Propylene Glycol Esters of Fatty Acids | 化学品 | #EDE2BA | K8 |
| `molten_glyceryl_monocaprylate` | 熔融单辛酸甘油酯 | `C11H22O4` | 液 | — |  | 第四批：342 的分子蒸馏馏出液，冷却结晶得单辛酸甘油酯（2026-11-11，同上） | Molten Glyceryl Monocaprylate | 化学品 | #F2EAC8 | K8 |
| `sodium_methyl_paraben` | 对羟基苯甲酸甲酯钠 | `C8H7NaO3` | 固 | — |  | 第四批 343 号 | Sodium Methyl p-Hydroxybenzoate | 化学品 | — | K3 |
| `dimethyl_dicarbonate` | 二甲基二碳酸盐 | `C4H6O5` | 液 | — |  | 第四批 344 号 | Dimethyl Dicarbonate | 化学品 | #ECEEE6 | K4 |
| `sodium_ethyl_paraben` | 对羟基苯甲酸乙酯钠 | `C9H9NaO3` | 固 | — |  | 第四批 347 号 | Sodium Ethyl p-Hydroxybenzoate | 化学品 | — | K3 |
| `isomaltulose` | 异麦芽酮糖 | `C12H22O11·H2O` | 固 | — |  | 第四批 350 号 | Isomaltulose | 化学品 | — | K2 |
| `monopotassium_glycyrrhizinate` | 甘草酸一钾 | `C42H61KO16` | 固 | — |  | 第四批 352 号 | Monopotassium Glycyrrhizinate | 化学品 | — | K5 |
| `ammonium_glycyrrhizinate` | 甘草酸铵 | `C42H65NO16` | 固 | — |  | 第四批 353 号；表 B.2 N332 | Ammonium Glycyrrhizinate | 化学品 | — | K5 |
| `alitame` | 阿力甜 | `C14H25N3O4S` | 固 | — |  | 第四批 354 号；商品含结晶水 | Alitame | 化学品 | — | K3 |
| `lactitol` | 乳糖醇 | `C12H24O11·H2O` | 固 | — |  | 第四批 355 号 | Lactitol | 化学品 | — | K2 |
| `aspartame_acesulfame` | 天门冬酰苯丙氨酸甲酯乙酰磺胺酸 | `C18H23N3O9S` | 固 | — |  | 第四批 358 号 | Aspartame-Acesulfame Salt | 化学品 | — | K3 |
| `sorbitol_syrup` | 山梨糖醇液 | `C6H14O6` | 溶液 | — |  | 第四批 360 号；约 70% 水溶液，同物质另一形态（流体）；界限见 10 §1.5 | Sorbitol Syrup | 化学品 | #F0EEDC | K2 |
| `calcium_cyclamate` | 环己基氨基磺酸钙 | `C12H24CaN2O6S2·2H2O` | 固 | — |  | 第四批 361 号 | Calcium Cyclohexylsulfamate (Calcium Cyclamate) | 化学品 | — | K3 |
| `tripotassium_glycyrrhizinate` | 甘草酸三钾 | `C42H59K3O16` | 固 | — |  | 第四批 362 号 | Tripotassium Glycyrrhizinate | 化学品 | — | K5 |
| `advantame` | 爱德万甜 | `C24H30N2O7·H2O` | 固 | — |  | 第四批 363 号 | Advantame | 化学品 | — | K3 |
| `caffeine` | 咖啡因 | `C8H10N4O2` | 固 | — |  | 第四批 366 号 | Caffeine | 化学品 | — | K3 |
| `norbixin` | 降红木素 | `C24H28O4` | 固 | — |  | 第四批 232 号；水溶型胭脂树橙 | Norbixin | 化学品 | — | K5 |
| `sodium_dehydroacetate` | 脱氢乙酸钠 | `C8H7NaO4·H2O` | 固 | — |  | 第四批 336 号；一水物 | Sodium Dehydroacetate | 化学品 | — | K3 |
| `butyl_chloride` | 1-氯丁烷 | `C4H9Cl` | 液 | — | ✓ | 第五批：S0055、S0071 | 1-Chlorobutane | 化学品 | — | K4 |
| `hexyl_chloride` | 1-氯己烷 | `C6H13Cl` | 液 | — | ✓ | 第五批：S0069 | 1-Chlorohexane | 化学品 | — | K4 |
| `cis_5_octenyl_chloride` | 顺式-5-辛烯基氯 | `C8H15Cl` | 液 | — | ✓ | 第五批：S0019 | (Z)-5-Octenyl Chloride | 化学品 | — | K4 |
| `aluminium_isopropoxide` | 异丙醇铝 | `C9H21AlO3` | 固 | — | ✓ | 第五批：ST31（S0011、S0020、S0042、S0043、S0044、S0070）；S0999、S1063 | Aluminium Isopropoxide | 化学品 | — | K3 |
| `xylenol_2_4` | 2,4-二甲基苯酚 | `C8H10O` | 液 | — | ✓ | 第五批：S0089 | 2,4-Xylenol | 化学品 | — | K4 |
| `chloro_methyl_tetrahydropyran` | 3-氯-2-甲基四氢吡喃 | `C6H11ClO` | 液 | — | ✓ | 第五批：S0012 | 3-Chloro-2-methyltetrahydropyran | 化学品 | — | K4 |
| `sebacic_acid` | 癸二酸 | `C10H18O4` | 固 | — | ✓ | 第五批：S1203；由 `disodium_sebacate`（S0015 副产）酸化 | Sebacic Acid | 化学品 | — | K3 |
| `ferulic_acid` | 阿魏酸 | `C10H10O4` | 固 | — | ✓ | 第五批：S0102 | Ferulic Acid | 化学品 | — | K3 |
| `pyrogallol` | 连苯三酚（焦性没食子酸） | `C6H6O3` | 固 | — | ✓ | 第五批：S0109 | Pyrogallol | 化学品 | — | K3 |
| `butene` | 1-丁烯 | `C4H8` | 气 | — | ✓ | 第五批：S0120、S0123、`sec_butanol`；`epoxybutane` | 1-Butene | 化学品 | — | K9 |
| `decene` | 1-癸烯 | `C10H20` | 液 | — | ✓ | 第五批：S0155 | 1-Decene | 化学品 | — | K4 |
| `sec_butanol` | 2-丁醇（仲丁醇） | `C4H10O` | 液 | — | ✓ | 第五批：S0203 | 2-Butanol | 化学品 | — | K4 |
| `myristyl_alcohol` | 1-十四醇（肉豆蔻醇） | `C14H30O` | 固 | — | ✓ | 第五批：S0161 | 1-Tetradecanol | 化学品 | — | K7 |
| `ethyl_chloroacetate` | 氯乙酸乙酯 | `C4H7ClO2` | 液 | — | ✓ | 第五批：S0139、S0146；S0461；S0714、S0789；S1170 | Ethyl Chloroacetate | 化学品 | — | K4 |
| `ethylbenzene` | 乙苯 | `C8H10` | 液 | — | ✓ | 第五批：S0101、S0190、S0225；S0679；569 | Ethylbenzene | 化学品 | — | K4 |
| `catechol` | 邻苯二酚 | `C6H6O2` | 固 | — | ✓ | 第五批：`benzodioxole` | Catechol | 化学品 | — | K3 |
| `dichloromethane` | 二氯甲烷 | `CH2Cl2` | 液 | — | ✓ | 第五批：`benzodioxole`；S0683 B | Dichloromethane | 化学品 | — | K4 |
| `benzodioxole` | 1,3-苯并二氧杂环戊烯（亚甲二氧基苯） | `C7H6O2` | 液 | — | ✓ | 第五批：`methylenedioxymandelic_acid` | 1,3-Benzodioxole | 化学品 | — | K4 |
| `methylenedioxymandelic_acid` | 3,4-亚甲二氧基扁桃酸 | `C9H8O5` | 固 | — | ✓ | 第五批：S0175 | 3,4-Methylenedioxymandelic Acid | 化学品 | — | K3 |
| `trans_4_heptenal` | 反式-4-庚烯醛 | `C7H12O` | 液 | — | ✓ | 第五批：S0197 | (E)-4-Heptenal | 化学品 | — | K4 |
| `cis_3_hexenyl_chloride` | 顺式-3-己烯基氯 | `C6H11Cl` | 液 | — | ✓ | 第五批：S0137 | (Z)-3-Hexenyl Chloride | 化学品 | — | K4 |
| `anisylidene_acetone` | 4-(4-甲氧基苯基)-3-丁烯-2-酮（茴香叉丙酮） | `C11H12O2` | 固 | — | ✓ | 第五批：S0205 | Anisylideneacetone | 化学品 | — | K3 |
| `glutaric_acid` | 戊二酸 | `C5H8O4` | 固 | — | ✓ | 第五批：`diethyl_glutarate` | Glutaric Acid | 化学品 | — | K3 |
| `diethyl_glutarate` | 戊二酸二乙酯 | `C9H16O4` | 液 | — | ✓ | 第五批：S0211 | Diethyl Glutarate | 化学品 | — | K4 |
| `diethyl_oxalate` | 草酸二乙酯 | `C6H10O4` | 液 | — | ✓ | 第五批：S0211 | Diethyl Oxalate | 化学品 | — | K4 |
| `ethyl_chloride` | 氯乙烷 | `C2H5Cl` | 气 | — | ✓ | 第五批：S0211；S0770；S1114、S1142、S1151、S1164 | Chloroethane | 化学品 | — | K9 |
| `methyltridecanoic_acid` | 12-甲基十三酸 | `C14H28O2` | 固 | — | ✓ | 第五批：S0202 | 12-Methyltridecanoic Acid | 化学品 | — | K7 |
| `hydroxybutanone` | 4-羟基-2-丁酮 | `C4H8O2` | 液 | — | ✓ | 第五批：S0229 | 4-Hydroxy-2-butanone | 化学品 | — | K4 |
| `pseudoionone` | 假性紫罗兰酮 | `C13H20O` | 液 | — | ✓ | 第五批：S0234、S0235；463、469 | Pseudoionone | 化学品 | — | K4 |
| `valerylfuran` | 2-戊酰基呋喃 | `C9H12O2` | 液 | — | ✓ | 第五批：S0278 | 2-Valerylfuran | 化学品 | — | K4 |
| `diethyl_methylglutarate` | 3-甲基戊二酸二乙酯 | `C10H18O4` | 液 | — | ✓ | 第五批：S0249、S0289 | Diethyl 3-Methylglutarate | 化学品 | — | K4 |
| `sec_butyl_chloride` | 2-氯丁烷 | `C4H9Cl` | 液 | — | ✓ | 第五批：S0256、S0320 | 2-Chlorobutane | 化学品 | — | K4 |
| `methylcyclohexanone` | 2-甲基环己酮 | `C7H12O` | 液 | — | ✓ | 第五批：S0252 | 2-Methylcyclohexanone | 化学品 | — | K4 |
| `phenyl_acetate` | 乙酸苯酯 | `C8H8O2` | 液 | — | ✓ | 第五批：S0266 | Phenyl Acetate | 化学品 | — | K4 |
| `benzoyl_chloride` | 苯甲酰氯 | `C7H5ClO` | 液 | — | ✓ | 第五批：S0265 | Benzoyl Chloride | 化学品 | — | K4 |
| `propiophenone` | 苯丙酮 | `C9H10O` | 液 | — | ✓ | 第五批：S0248 | Propiophenone | 化学品 | — | K4 |
| `undecanedione` | 2,5-十一烷二酮 | `C11H20O2` | 固 | — | ✓ | 第五批：S0268 | 2,5-Undecanedione | 化学品 | — | K3 |
| `undecenedione` | (Z)-8-十一碳烯-2,5-二酮 | `C11H18O2` | 液 | — | ✓ | 第五批：S0228 | (Z)-Undec-8-ene-2,5-dione | 化学品 | — | K4 |
| `dichlorodecane` | 1,10-二氯癸烷 | `C10H20Cl2` | 液 | — | ✓ | 第五批：`hexadecanedione` | 1,10-Dichlorodecane | 化学品 | — | K4 |
| `hexadecanedione` | 2,15-十六烷二酮 | `C16H30O2` | 固 | — | ✓ | 第五批：S0263 | 2,15-Hexadecanedione | 化学品 | — | K3 |
| `diethyl_octadecenedioate` | 9-十八碳烯二酸二乙酯 | `C22H40O4` | 液 | — | ✓ | 第五批：S0264 | Diethyl 9-Octadecenedioate | 化学品 | — | K4 |
| `methylhexanol` | 2-甲基-1-己醇 | `C7H16O` | 液 | — | ✓ | 第五批：S0330；`methylhexyl_chloride` | 2-Methyl-1-hexanol | 化学品 | — | K4 |
| `methylhexyl_chloride` | 1-氯-2-甲基己烷 | `C7H15Cl` | 液 | — | ✓ | 第五批：S0332 | 1-Chloro-2-methylhexane | 化学品 | — | K4 |
| `methylheptanol` | 2-甲基-1-庚醇 | `C8H18O` | 液 | — | ✓ | 第五批：S0331；`methylheptyl_chloride` | 2-Methyl-1-heptanol | 化学品 | — | K4 |
| `methylheptyl_chloride` | 1-氯-2-甲基庚烷 | `C8H17Cl` | 液 | — | ✓ | 第五批：S0347 | 1-Chloro-2-methylheptane | 化学品 | — | K4 |
| `methylpentanal` | 4-甲基戊醛 | `C6H12O` | 液 | — | ✓ | 第五批：S0286、S0348 | 4-Methylpentanal | 化学品 | — | K4 |
| `chloropropionic_acid` | 2-氯丙酸 | `C3H5ClO2` | 液 | — | ✓ | 第五批：S0341 | 2-Chloropropionic Acid | 化学品 | — | K4 |
| `ethyl_thioglycolate` | 巯基乙酸乙酯 | `C4H8O2S` | 液 | — | ✓ | 第五批：S0243 | Ethyl Thioglycolate | 化学品 | — | K4 |
| `neohesperidin` | 新橙皮苷 | `C28H34O15` | 固 | — | ✓ | 第五批：S0269 | Neohesperidin | 化学品 | — | K3 |
| `ambradiol` | 龙涎二醇 | `C16H30O2` | 固 | — | ✓ | 第五批：S0280 | Ambradiol | 化学品 | — | K3 |
| `dehydrozingerone` | 脱氢姜酮 | `C11H12O3` | 固 | — | ✓ | 第五批：S0270 | Dehydrozingerone | 化学品 | — | K3 |
| `piperonylideneacetone` | 胡椒叉丙酮 | `C11H10O3` | 固 | — | ✓ | 第五批：S0283 | Piperonylideneacetone | 化学品 | — | K3 |
| `allyl_alcohol` | 烯丙醇 | `C3H6O` | 液 | — | ✓ | 第五批：S0397；S0545（F10 已引用同一 ID）；S1148、S1184、S1186、S1189、S1190、S1194、S1205、S1211、S1236；S1284、S1304、S1346、S1374、S1401；`allyl_chloride` | Allyl Alcohol | 化学品 | — | K4 |
| `propionic_anhydride` | 丙酸酐 | `C6H10O3` | 液 | — | ✓ | 第五批：S0411；S1037、S0995 | Propionic Anhydride | 化学品 | — | K4 |
| `butyric_anhydride` | 丁酸酐 | `C8H14O3` | 液 | — | ✓ | 第五批：S0435、S0443、S0448；S0952 | Butyric Anhydride | 化学品 | — | K4 |
| `isobutyric_anhydride` | 异丁酸酐 | `C8H14O3` | 液 | — | ✓ | 第五批：S0619 B | Isobutyric Anhydride | 化学品 | — | K4 |
| `anthranilic_acid` | 邻氨基苯甲酸 | `C7H7NO2` | 固 | — | ✓ | 第五批：S0491 | Anthranilic Acid | 化学品 | — | K3 |
| `z_hexadienyl_chloride` | (Z)-1-氯-2,5-己二烯 | `C6H9Cl` | 液 | — | ✓ | 第五批：S0475 | (Z)-1-Chloro-2,5-hexadiene | 化学品 | — | K4 |
| `trans_2_nonenoic_acid` | 反式-2-壬烯酸 | `C9H16O2` | 液 | — | ✓ | 第五批：S0479 | (E)-2-Nonenoic Acid | 化学品 | — | K4 |
| `cyclopentanone` | 环戊酮 | `C5H8O` | 液 | — | ✓ | 第五批：S0511、S0632、S0633、S0634、S0636、S0642、S0643；S0857（经 `undecylcyclopentanone`）；S1265；`hexenylcyclopentanone` | Cyclopentanone | 化学品 | — | K4 |
| `propylcyclopentanone` | 2-丙基环戊酮 | `C8H14O` | 液 | — | ✓ | 第五批：S0632 | 2-Propylcyclopentanone | 化学品 | — | K4 |
| `butylcyclopentanone` | 2-丁基环戊酮 | `C9H16O` | 液 | — | ✓ | 第五批：S0633 | 2-Butylcyclopentanone | 化学品 | — | K4 |
| `pentylcyclopentanone` | 2-戊基环戊酮 | `C10H18O` | 液 | — | ✓ | 第五批：S0634 | 2-Pentylcyclopentanone | 化学品 | — | K4 |
| `hexylcyclopentanone` | 2-己基环戊酮 | `C11H20O` | 液 | — | ✓ | 第五批：S0635 | 2-Hexylcyclopentanone | 化学品 | — | K4 |
| `heptylcyclopentanone` | 2-庚基环戊酮 | `C12H22O` | 液 | — | ✓ | 第五批：S0636 | 2-Heptylcyclopentanone | 化学品 | — | K4 |
| `octylcyclopentanone` | 2-辛基环戊酮 | `C13H24O` | 液 | — | ✓ | 第五批：S0642 | 2-Octylcyclopentanone | 化学品 | — | K4 |
| `nonylcyclopentanone` | 2-壬基环戊酮 | `C14H26O` | 液 | — | ✓ | 第五批：S0643 | 2-Nonylcyclopentanone | 化学品 | — | K4 |
| `pentylcyclopentenone` | 2-戊基-2-环戊烯-1-酮 | `C10H16O` | 液 | — | ✓ | 第五批：S0511 | 2-Pentyl-2-cyclopenten-1-one | 化学品 | — | K4 |
| `dimethyl_malonate` | 丙二酸二甲酯 | `C5H8O4` | 液 | — | ✓ | 第五批：S0511 | Dimethyl Malonate | 化学品 | — | K4 |
| `malonic_acid` | 丙二酸 | `C3H4O4` | 固 | — | ✓ | 第五批：S0548；S1104、`furylacrylic_acid` | Malonic Acid | 化学品 | — | K3 |
| `ethyl_oxohexanoate` | 5-氧代己酸乙酯 | `C8H14O3` | 液 | — | ✓ | 第五批：S0631 | Ethyl 5-Oxohexanoate | 化学品 | — | K4 |
| `alpha_picoline` | 2-甲基吡啶 | `C6H7N` | 液 | — | ✓ | 第五批：S0540；S0778；S1322；`pyridylethylamine` | 2-Methylpyridine | 化学品 | — | K4 |
| `limonene_oxide` | 1,2-环氧柠檬烯 | `C10H16O` | 液 | — | ✓ | 第五批：S0580 | Limonene 1,2-Oxide | 化学品 | — | K4 |
| `hydroxymenthofurolactone` | 羟基薄荷呋喃内酯 | `C10H14O3` | 固 | — | ✓ | 第五批：S0641 | Hydroxymenthofurolactone | 化学品 | — | K3 |
| `pentenylcyclopentanone` | 2-[(Z)-2-戊烯基]环戊酮 | `C10H16O` | 液 | — | ✓ | 第五批：S0645 | 2-[(Z)-2-Pentenyl]cyclopentanone | 化学品 | — | K4 |
| `hydroxypentadecanoic_acid` | 15-羟基十五酸 | `C15H30O3` | 固 | — | ✓ | 第五批：S0637 | 15-Hydroxypentadecanoic Acid | 化学品 | — | K3 |
| `glyoxal` | 乙二醛 | `C2H2O2` | 溶液 | — | ✓ | 第五批：S0717、S0768、S0773；S1217 | Glyoxal | 化学品 | — | K4 |
| `isoprene` | 异戊二烯 | `C5H8` | 液 | — | ✓ | 第五批：S0663；`tert_butyl_dimethylindane`（S1272）；572 | Isoprene | 化学品 | — | K4 |
| `methyl_isopropenyl_ketone` | 3-甲基-3-丁烯-2-酮（甲基异丙烯基酮） | `C5H8O` | 液 | — | ✓ | 第五批：S0663、S0799 | 3-Methyl-3-buten-2-one | 化学品 | — | K4 |
| `p_toluidine` | 对甲苯胺 | `C7H9N` | 固 | — | ✓ | 第五批：S0772 | p-Toluidine | 化学品 | — | K3 |
| `phenylhydrazine` | 苯肼 | `C6H8N2` | 液 | — | ✓ | 第五批：S0775 | Phenylhydrazine | 化学品 | — | K4 |
| `toluene_2_3_diamine` | 2,3-二氨基甲苯（邻位甲苯二胺） | `C7H10N2` | 固 | — | ✓ | 第五批：S0773 | Toluene-2,3-diamine | 化学品 | — | K3 |
| `hexanediol` | 1,6-己二醇 | `C6H14O2` | 固 | — | ✓ | 第五批：S0691 | 1,6-Hexanediol | 化学品 | — | K3 |
| `leucinamide` | L-亮氨酰胺 | `C6H14N2O` | 固 | — | ✓ | 第五批：S0768 | L-Leucinamide | 化学品 | — | K3 |
| `isoleucinamide` | DL-异亮氨酰胺 | `C6H14N2O` | 固 | — | ✓ | 第五批：S0717 | DL-Isoleucinamide | 化学品 | — | K3 |
| `acrolein` | 丙烯醛 | `C3H4O` | 液 | — | ✓ | 第五批：S0700；S0927；456；545 | Acrolein | 化学品 | — | K4 |
| `allyl_chloride` | 3-氯丙烯（烯丙基氯） | `C3H5Cl` | 液 | — | ✓ | 第五批：S0749 B、S0762 B；S0803、S0813、S0814、S0815、S0931；S1045；S1157、S1181、S1243、`allyl_alcohol`；S1270 | Allyl Chloride | 化学品 | — | K4 |
| `prenyl_methyl_ether` | 1-甲氧基-3-甲基-2-丁烯 | `C6H12O` | 液 | — | ✓ | 第五批：S0804 | 1-Methoxy-3-methyl-2-butene | 化学品 | — | K4 |
| `undecylcyclopentanone` | 2-十一烷基环戊酮 | `C16H30O` | 液 | — | ✓ | 第五批：S0857 | 2-Undecylcyclopentanone | 化学品 | — | K4 |
| `o_nitroacetophenone` | 邻硝基苯乙酮 | `C8H7NO3` | 液 | — | ✓ | 第五批：S0819 | o-Nitroacetophenone | 化学品 | — | K4 |
| `chloropyrazine` | 2-氯吡嗪 | `C4H3ClN2` | 液 | — | ✓ | 第五批：S0829；S1320 | 2-Chloropyrazine | 化学品 | — | K4 |
| `chloromethylpyrazine` | 2-氯-3-甲基吡嗪（含 5-、6-氯异构体） | `C5H5ClN2` | 液 | — | ✓ | 第五批：S0830；S1218、S1219、S1245、S1251 | 2-Chloro-3-methylpyrazine | 化学品 | — | K4 |
| `vanillylamine` | 香草胺（4-羟基-3-甲氧基苄胺） | `C8H11NO2` | 固 | — | ✓ | 第五批：S0838 | Vanillylamine | 化学品 | — | K3 |
| `chlorobutanone` | 3-氯-2-丁酮 | `C4H7ClO` | 液 | — | ✓ | 第五批：S0842；S1342 | 3-Chloro-2-butanone | 化学品 | — | K4 |
| `nitroethane` | 硝基乙烷 | `C2H5NO2` | 液 | — | ✓ | 第五批：S0874 | Nitroethane | 化学品 | — | K4 |
| `methylthiopropylamine` | 3-甲硫基丙胺 | `C4H11NS` | 液 | — | ✓ | 第五批：S0899 | 3-(Methylthio)propylamine | 化学品 | — | K4 |
| `ethanolamine` | 乙醇胺（2-氨基乙醇） | `C2H7NO` | 液 | — | ✓ | 第五批：S0925、S0926 | Ethanolamine | 化学品 | — | K4 |
| `alpha_cyclocitral` | α-环柠檬醛 | `C10H16O` | 液 | — | ✓ | 第五批：S0931 | alpha-Cyclocitral | 化学品 | — | K4 |
| `aleuritic_acid` | 紫胶桐酸（9,10,16-三羟基十六酸） | `C16H32O5` | 固 | — | ✓ | 第五批：S0936 | Aleuritic Acid | 化学品 | — | K3 |
| `trimethyl_orthoformate` | 原甲酸三甲酯 | `C4H10O3` | 液 | — | ✓ | 第五批：S0936 | Trimethyl Orthoformate | 化学品 | — | K4 |
| `ethyl_chlorocrotonate` | 3-氯巴豆酸乙酯 | `C6H9ClO2` | 液 | — | ✓ | 第五批：S0935 | Ethyl 3-Chlorocrotonate | 化学品 | — | K4 |
| `methallyl_chloride` | 甲基烯丙基氯（3-氯-2-甲基丙烯） | `C4H7Cl` | 液 | — | ✓ | 第五批：S0943 | Methallyl Chloride | 化学品 | — | K4 |
| `acetonitrile` | 乙腈 | `C2H3N` | 液 | — | ✓ | 第五批：S0943（经 `trimethyl_orthoacetate`）；`acetamidine_hydrochloride`、`thioacetamide` | Acetonitrile | 化学品 | — | K4 |
| `trimethyl_orthoacetate` | 原乙酸三甲酯 | `C5H12O3` | 液 | — | ✓ | 第五批：S0943 | Trimethyl Orthoacetate | 化学品 | — | K4 |
| `chloroacetaldehyde` | 氯乙醛 | `C2H3ClO` | 溶液 | — | ✓ | 第五批：S0903；S1125、S1222、S1246 | Chloroacetaldehyde | 化学品 | — | K4 |
| `thioformamide` | 硫代甲酰胺 | `CH3NS` | 固 | — | ✓ | 第五批：S0903 | Thioformamide | 化学品 | — | K3 |
| `piperylene` | 1,3-戊二烯（间戊二烯） | `C5H8` | 液 | — | ✓ | 第五批：S0987 | Piperylene (1,3-Pentadiene) | 化学品 | — | K4 |
| `sclareol` | 香紫苏醇 | `C20H36O2` | 固 | — | ✓ | 第五批：S1049 | Sclareol | 化学品 | — | K3 |
| `oleyl_alcohol` | 顺式-9-十八烯醇（油醇） | `C18H36O` | 液 | — | ✓ | 第五批：S1082 | Oleyl Alcohol | 化学品 | — | K7 |
| `butane_2_3_diol` | 2,3-丁二醇 | `C4H10O2` | 液 | — | ✓ | 第五批：S1053 | 2,3-Butanediol | 化学品 | — | K4 |
| `hydroxymethylfurfural` | 5-羟甲基糠醛 | `C6H6O3` | 固 | — | ✓ | 第五批：S1092 | 5-Hydroxymethylfurfural | 化学品 | — | K3 |
| `dimethoxycinnamic_acid` | 3,4-二甲氧基肉桂酸 | `C11H12O4` | 固 | — | ✓ | 第五批：S1097、`homoveratrylamine` | 3,4-Dimethoxycinnamic Acid | 化学品 | — | K3 |
| `homoveratrylamine` | 3,4-二甲氧基苯乙胺（高藜芦胺） | `C10H15NO2` | 液 | — | ✓ | 第五批：S1097 | Homoveratrylamine | 化学品 | — | K4 |
| `nonenynol` | (Z)-6-壬烯-3-炔-1-醇 | `C9H14O` | 液 | — | ✓ | 第五批：S0978 | (Z)-6-Nonen-3-yn-1-ol | 化学品 | — | K4 |
| `isoprenol` | 3-甲基-3-丁烯-1-醇 | `C5H10O` | 液 | — | ✓ | 第五批：S0956、S0954；572 | Isoprenol (3-Methyl-3-buten-1-ol) | 化学品 | — | K4 |
| `methyl_vinyl_ketone` | 甲基乙烯基酮 | `C4H6O` | 液 | — | ✓ | 第五批：S0962、S0989；S1378 | Methyl Vinyl Ketone | 化学品 | — | K4 |
| `methyl_isopropyl_ketone` | 3-甲基-2-丁酮 | `C5H10O` | 液 | — | ✓ | 第五批：S0975；S1430 | Methyl Isopropyl Ketone | 化学品 | — | K4 |
| `isobutylamine` | 异丁胺 | `C4H11N` | 液 | — | ✓ | 第五批：S1086；S1398 | Isobutylamine | 化学品 | — | K4 |
| `isopropylamine` | 异丙胺 | `C3H9N` | 液 | — | ✓ | 第五批：S1090；S1485 | Isopropylamine | 化学品 | — | K4 |
| `chloromethyl_methyl_sulfide` | 氯甲基甲基硫醚 | `C2H5ClS` | 液 | — | ✓ | 第五批：S1131；S1460 | Chloromethyl Methyl Sulfide | 化学品 | — | K4 |
| `methylpentene` | 2-甲基-1-戊烯 | `C6H12` | 液 | — | ✓ | 第五批：S1138 | 2-Methyl-1-pentene | 化学品 | — | K4 |
| `furan` | 呋喃 | `C4H4O` | 液 | — | ✓ | 第五批：S1156；`furylbutyric_acid` | Furan | 化学品 | — | K4 |
| `epoxybutane` | 2,3-环氧丁烷 | `C4H8O` | 液 | — | ✓ | 第五批：S1158、S1161、S1240 | 2,3-Epoxybutane | 化学品 | — | K4 |
| `undecenol` | 10-十一烯-1-醇 | `C11H22O` | 液 | — | ✓ | 第五批：S1168、S1169 | 10-Undecen-1-ol | 化学品 | — | K4 |
| `phenylbutyric_acid` | 4-苯基丁酸 | `C10H12O2` | 固 | — | ✓ | 第五批：S1194、S1209、S1210 | 4-Phenylbutyric Acid | 化学品 | — | K3 |
| `cedrol` | 柏木脑 | `C15H26O` | 固 | — | ✓ | 第五批：S1197 | Cedrol | 化学品 | — | K3 |
| `heptyne` | 1-庚炔 | `C7H12` | 液 | — | ✓ | 第五批：S1201 | 1-Heptyne | 化学品 | — | K4 |
| `octyne` | 1-辛炔 | `C8H14` | 液 | — | ✓ | 第五批：S1202 | 1-Octyne | 化学品 | — | K4 |
| `acetamidine_hydrochloride` | 盐酸乙脒 | `C2H7ClN2` | 固 | — | ✓ | 第五批：S1221 | Acetamidine Hydrochloride | 化学品 | — | K3 |
| `thioacetamide` | 硫代乙酰胺 | `C2H5NS` | 固 | — | ✓ | 第五批：S1223；S1459 | Thioacetamide | 化学品 | — | K3 |
| `acetylacetone` | 乙酰丙酮 | `C5H8O2` | 液 | — | ✓ | 第五批：S1223 | Acetylacetone | 化学品 | — | K4 |
| `ethylene_carbonate` | 碳酸乙烯酯 | `C3H4O3` | 固 | — | ✓ | 第五批：S1226 | Ethylene Carbonate | 化学品 | — | K3 |
| `propylene_carbonate` | 碳酸丙烯酯 | `C4H6O3` | 液 | — | ✓ | 第五批：S1235；S1396 | Propylene Carbonate | 化学品 | — | K4 |
| `benzyl_chloride` | 苄基氯 | `C7H7Cl` | 液 | — | ✓ | 第五批：S1232、S1237、S1244；S1415；`benzyl_cyanide`；481 | Benzyl Chloride | 化学品 | — | K4 |
| `diisobutylene` | 二异丁烯 | `C8H16` | 液 | — | ✓ | 第五批：S1250 | Diisobutylene (2,4,4-Trimethyl-1-pentene) | 化学品 | — | K4 |
| `butanediamine` | 1,2-丁二胺 | `C4H12N2` | 液 | — | ✓ | 第五批：S1146；S1447 | 1,2-Diaminobutane | 化学品 | — | K4 |
| `furylacrylic_acid` | 3-(2-呋喃基)丙烯酸 | `C7H6O3` | 固 | — | ✓ | 第五批：S1188；S1278 | 3-(2-Furyl)acrylic Acid | 化学品 | — | K3 |
| `glutaric_anhydride` | 戊二酸酐 | `C5H6O3` | 固 | — | ✓ | 第五批：S1306 | Glutaric Anhydride | 化学品 | — | K3 |
| `tert_butylbenzene` | 叔丁基苯 | `C10H14` | 液 | — | ✓ | 第五批：`tert_butyl_dimethylindane`、`tert_butylbenzyl_chloride`（S1272、S1283） | tert-Butylbenzene | 化学品 | — | K4 |
| `tert_butyl_dimethylindane` | 6-叔丁基-1,1-二甲基茚满 | `C15H22` | 液 | — | ✓ | 第五批：S1272 | 6-tert-Butyl-1,1-dimethylindane | 化学品 | — | K4 |
| `tert_butylbenzyl_chloride` | 对叔丁基苄氯 | `C11H15Cl` | 液 | — | ✓ | 第五批：S1283 | 4-tert-Butylbenzyl Chloride | 化学品 | — | K4 |
| `phenoxyethanol` | 2-苯氧基乙醇 | `C8H10O2` | 液 | — | ✓ | 第五批：S1280 | 2-Phenoxyethanol | 化学品 | — | K4 |
| `phenoxyacetic_acid` | 苯氧乙酸 | `C8H8O3` | 固 | — | ✓ | 第五批：S1284 | Phenoxyacetic Acid | 化学品 | — | K3 |
| `brassylic_acid` | 十三碳二酸 | `C13H24O4` | 固 | — | ✓ | 第五批：S1281 | Brassylic Acid | 化学品 | — | K3 |
| `ethyl_vinyl_ether` | 乙基乙烯基醚 | `C4H8O` | 液 | — | ✓ | 第五批：S1293、S1397；456；F10 的 S1293 同用 | Ethyl Vinyl Ether | 化学品 | — | K4 |
| `isopropyl_chloride` | 2-氯丙烷 | `C3H7Cl` | 液 | — | ✓ | 第五批：S1315；`diisopropylpropionitrile` | Isopropyl Chloride | 化学品 | — | K4 |
| `propionitrile` | 丙腈 | `C3H5N` | 液 | — | ✓ | 第五批：`diisopropylpropionitrile` | Propionitrile | 化学品 | — | K4 |
| `diisopropylpropionitrile` | 2,2-二异丙基丙腈 | `C9H17N` | 液 | — | ✓ | 第五批：S1294 | 2,2-Diisopropylpropionitrile | 化学品 | — | K4 |
| `menthanecarboxylic_acid` | 对䓝烷-3-甲酸 | `C11H20O2` | 固 | — | ✓ | 第五批：S1295 | p-Menthane-3-carboxylic Acid | 化学品 | — | K3 |
| `epichlorohydrin` | 环氧氯丙烷 | `C3H5ClO` | 液 | — | ✓ | 第五批：S1296 | Epichlorohydrin | 化学品 | — | K4 |
| `hexylcyclohexanone` | 2-己基环己酮 | `C12H22O` | 液 | — | ✓ | 第五批：S1308 | 2-Hexylcyclohexanone | 化学品 | — | K4 |
| `dichlorobutane` | 1,2-二氯丁烷 | `C4H8Cl2` | 液 | — | ✓ | 第五批：S1311 | 1,2-Dichlorobutane | 化学品 | — | K4 |
| `sec_butylphenol` | 2-仲丁基苯酚 | `C10H14O` | 液 | — | ✓ | 第五批：S1299 | 2-sec-Butylphenol | 化学品 | — | K4 |
| `cyclohexanethiol` | 环己硫醇 | `C6H12S` | 液 | — | ✓ | 第五批：S1314 | Cyclohexanethiol | 化学品 | — | K4 |
| `phenethyl_chloride` | 2-苯乙基氯 | `C8H9Cl` | 液 | — | ✓ | 第五批：S1322、S1362 | 2-Phenylethyl Chloride | 化学品 | — | K4 |
| `vinylpyrazine` | 2-乙烯基吡嗪 | `C6H6N2` | 液 | — | ✓ | 第五批：S1325 | 2-Vinylpyrazine | 化学品 | — | K4 |
| `suberic_acid` | 辛二酸 | `C8H14O4` | 固 | — | ✓ | 第五批：`dichlorooctane` | Suberic Acid | 化学品 | — | K3 |
| `dichlorooctane` | 1,8-二氯辛烷 | `C8H16Cl2` | 液 | — | ✓ | 第五批：S1336 | 1,8-Dichlorooctane | 化学品 | — | K4 |
| `hexenylcyclopentanone` | 2-(3-己烯基)环戊酮 | `C11H18O` | 液 | — | ✓ | 第五批：S1380 | 2-(3-Hexenyl)cyclopentanone | 化学品 | — | K4 |
| `dehydrodihydroionone` | 脱氢二氢紫罗兰酮 | `C13H20O` | 液 | — | ✓ | 第五批：S1365 | Dehydrodihydroionone | 化学品 | — | K4 |
| `methoxymethylbenzylamine` | 2-甲氧基-4-甲基苄胺 | `C9H13NO` | 液 | — | ✓ | 第五批：S1368 | 2-Methoxy-4-methylbenzylamine | 化学品 | — | K4 |
| `methylpyridylethylamine` | 2-(5-甲基-2-吡啶基)乙胺 | `C8H12N2` | 液 | — | ✓ | 第五批：S1368 | 2-(5-Methylpyridin-2-yl)ethylamine | 化学品 | — | K4 |
| `dimethoxybenzylamine` | 2,4-二甲氧基苄胺 | `C9H13NO2` | 液 | — | ✓ | 第五批：S1369 | 2,4-Dimethoxybenzylamine | 化学品 | — | K4 |
| `pyridylethylamine` | 2-(2-吡啶基)乙胺 | `C7H10N2` | 液 | — | ✓ | 第五批：S1369 | 2-(Pyridin-2-yl)ethylamine | 化学品 | — | K4 |
| `piperonylic_acid` | 胡椒酸 | `C8H6O4` | 固 | — | ✓ | 第五批：S1370 | Piperonylic Acid | 化学品 | — | K3 |
| `heptan_4_amine` | 4-庚胺 | `C7H17N` | 液 | — | ✓ | 第五批：S1370 | 4-Heptylamine | 化学品 | — | K4 |
| `nonadienoic_acid` | 反-2-顺-6-壬二烯酸 | `C9H14O2` | 液 | — | ✓ | 第五批：S1381、S1382 | (2E,6Z)-Nona-2,6-dienoic Acid | 化学品 | — | K4 |
| `cyclopropylamine` | 环丙胺 | `C3H7N` | 液 | — | ✓ | 第五批：S1381 | Cyclopropylamine | 化学品 | — | K4 |
| `decadienoic_acid` | 反-2-反-4-癸二烯酸 | `C10H16O2` | 固 | — | ✓ | 第五批：S1398 | (2E,4E)-Deca-2,4-dienoic Acid | 化学品 | — | K3 |
| `cyclohexylhexanoic_acid` | 6-环己基己酸 | `C12H22O2` | 液 | — | ✓ | 第五批：S1402 | 6-Cyclohexylhexanoic Acid | 化学品 | — | K4 |
| `cyclohexylvaleric_acid` | 5-环己基戊酸 | `C11H20O2` | 液 | — | ✓ | 第五批：S1403 | 5-Cyclohexylvaleric Acid | 化学品 | — | K4 |
| `furylbutyric_acid` | 4-(2-呋喃基)丁酸 | `C8H10O3` | 固 | — | ✓ | 第五批：S1411 | 4-(2-Furyl)butyric Acid | 化学品 | — | K3 |
| `furylpropionic_acid` | 3-(2-呋喃基)丙酸 | `C7H8O3` | 固 | — | ✓ | 第五批：S1412 | 3-(2-Furyl)propionic Acid | 化学品 | — | K3 |
| `heptanediol` | 1,2-庚二醇 | `C7H16O2` | 液 | — | ✓ | 第五批：S1413 | 1,2-Heptanediol | 化学品 | — | K4 |
| `benzyl_cyanide` | 苯乙腈 | `C8H7N` | 液 | — | ✓ | 第五批：`aminophenylacetonitrile` | Benzyl Cyanide | 化学品 | — | K4 |
| `aminophenylacetonitrile` | 对氨基苯乙腈 | `C8H8N2` | 固 | — | ✓ | 第五批：S1469 | 4-Aminophenylacetonitrile | 化学品 | — | K3 |
| `cyclopropanecarboxylic_acid` | 环丙烷甲酸 | `C4H6O2` | 液 | — | ✓ | 第五批：S1416、S1488 | Cyclopropanecarboxylic Acid | 化学品 | — | K4 |
| `geranylamine` | 香叶胺 | `C10H19N` | 液 | — | ✓ | 第五批：S1416 | Geranylamine | 化学品 | — | K4 |
| `menthylamine` | 薄荷胺 | `C10H21N` | 液 | — | ✓ | 第五批：S1488 | Menthylamine | 化学品 | — | K4 |
| `glycine_ethyl_ester` | 甘氨酸乙酯 | `C4H9NO2` | 液 | — | ✓ | 第五批：S1417、S1426、S1484 | Glycine Ethyl Ester | 化学品 | — | K4 |
| `cysteamine` | 半胱胺 | `C2H7NS` | 固 | — | ✓ | 第五批：S1422 | Cysteamine | 化学品 | — | K3 |
| `chlorooctanol` | 8-氯-1-辛醇 | `C8H17ClO` | 液 | — | ✓ | 第五批：S1423 | 8-Chloro-1-octanol | 化学品 | — | K4 |
| `hexynol` | 2-己炔-1-醇 | `C6H10O` | 液 | — | ✓ | 第五批：S1444 | 2-Hexyn-1-ol | 化学品 | — | K4 |
| `chloropropanal` | 2-氯丙醛 | `C3H5ClO` | 液 | — | ✓ | 第五批：S1459 | 2-Chloropropanal | 化学品 | — | K4 |
| `vinyl_chloride` | 氯乙烯 | `C2H3Cl` | 气 | — | ✓ | 第五批：S1489 | Vinyl Chloride | 化学品 | — | K9 |
| `vinylpyridine` | 2-乙烯基吡啶 | `C7H7N` | 液 | — | ✓ | 第五批：`pyridylethylamine` | 2-Vinylpyridine | 化学品 | — | K4 |
| `malononitrile` | 丙二腈 | `C3H2N2` | 固 | — | ✓ | 第五批：S1471 | Malononitrile | 化学品 | — | K3 |
| `hydroxypivalic_acid` | 3-羟基-2,2-二甲基丙酸 | `C5H10O3` | 固 | — | ✓ | 第五批：S1472、S1485 | Hydroxypivalic Acid | 化学品 | — | K3 |
| `propylamine` | 正丙胺 | `C3H9N` | 液 | — | ✓ | 第五批：S1472 | Propylamine | 化学品 | — | K4 |
| `aminofluorobenzonitrile` | 2-氨基-6-氟苯甲腈 | `C7H5FN2` | 固 | — | ✓ | 第五批：S1472、S1485 | 2-Amino-6-fluorobenzonitrile | 化学品 | — | K3 |
| `isoxazolylmethyl_pyrazolamine` | 1-[(3,5-二甲基异噁唑-4-基)甲基]-1H-吡唑-4-胺 | `C9H12N4O` | 固 | — | ✓ | 第五批：S1484 | 1-[(3,5-Dimethylisoxazol-4-yl)methyl]-1H-pyrazol-4-amine | 化学品 | — | K3 |
| `m_hydroxybenzaldehyde` | 3-羟基苯甲醛 | `C7H6O2` | 固 | — | ✓ | 第五批：S1484 | 3-Hydroxybenzaldehyde | 化学品 | — | K3 |
| `decadiene` | 1,9-癸二烯 | `C10H18` | 液 | — | ✓ | 第五批：S1486 | 1,9-Decadiene | 化学品 | — | K4 |
| `methylhexene` | 5-甲基-1-己烯 | `C7H14` | 液 | — | ✓ | 第五批：S1487 | 5-Methyl-1-hexene | 化学品 | — | K4 |
| `methylheptene` | 5-甲基-1-庚烯 | `C8H16` | 液 | — | ✓ | 第五批：S1478 | 5-Methyl-1-heptene | 化学品 | — | K4 |
| `valylglycine` | L-缬氨酰甘氨酸 | `C7H14N2O3` | 固 | — | ✓ | 第五批：S1503 | L-Valylglycine | 化学品 | — | K3 |
| `p_anisidine` | 对氨基苯甲醚 | `C7H9NO` | 固 | — | ✓ | 第五批：S1504 | p-Anisidine | 化学品 | — | K3 |
| `aminopyrazole` | 3-氨基吡唑 | `C3H5N3` | 固 | — | ✓ | 第五批：S1505 | 3-Aminopyrazole | 化学品 | — | K3 |
| `thiophenecarboxaldehyde` | 2-噻吩甲醛 | `C5H4OS` | 液 | — | ✓ | 第五批：S1505 | 2-Thiophenecarboxaldehyde | 化学品 | — | K4 |
| `tolyloxyacetic_acid` | 对甲苯氧基乙酸 | `C9H10O3` | 固 | — | ✓ | 第五批：S1505 | (4-Methylphenoxy)acetic Acid | 化学品 | — | K3 |
| `thioacetic_acid` | 硫代乙酸 | `C2H4OS` | 液 | — | ✓ | 第五批：S1477 | Thioacetic Acid | 化学品 | — | K4 |
| `hesperidin` | 橙皮苷 | `C28H34O15` | 固 | — | ✓ | 第五批：N375 | Hesperidin | 化学品 | — | K3 |
| `rutin` | 芦丁 | `C27H30O16` | 固 | — | ✓ | 第五批：N399 | Rutin | 化学品 | — | K3 |
| `palmitoyl_chloride` | 棕榈酰氯 | `C16H31ClO` | 液 | — | ✓ | 第五批：424 | Palmitoyl Chloride | 化学品 | — | K4 |
| `butadiene` | 1,3-丁二烯 | `C4H6` | 气 | — | ✓ | 第五批：569 | 1,3-Butadiene | 化学品 | — | K9 |
| `vinyl_laurate` | 月桂酸乙烯酯 | `C14H26O2` | 液 | — | ✓ | 第五批：577 | Vinyl Laurate | 化学品 | — | K4 |
| `calcium_carbide` | 碳化钙（电石） | `C2Ca` | 固 | — | ✓ | 第五批：439、456（经乙炔）；463、469、472（经乙炔） | Calcium Carbide | 化学品 | — | K1 |
| `acetylene` | 乙炔 | `C2H2` | 气 | — | ✓ | 第五批：439、456；463、469、472 | Acetylene | 化学品 | — | K9 |
| `pyrrolidone` | 2-吡咯烷酮 | `C4H7NO` | 液 | — | ✓ | 第五批：439 | 2-Pyrrolidone | 化学品 | — | K4 |
| `n_vinylpyrrolidone` | N-乙烯基吡咯烷酮 | `C6H9NO` | 液 | — | ✓ | 第五批：439 | N-Vinyl-2-pyrrolidone | 化学品 | — | K4 |
| `acrylamide` | 丙烯酰胺 | `C3H5NO` | 溶液 | — | ✓ | 第五批：442 | Acrylamide | 化学品 | — | K0 |
| `stearyl_alcohol` | 1-十八醇（硬脂醇） | `C18H38O` | 固 | — | ✓ | 第五批：445 | 1-Octadecanol (Stearyl Alcohol) | 化学品 | — | K7 |
| `butynediol` | 2-丁炔-1,4-二醇 | `C4H6O2` | 固 | — | ✓ | 第五批：472 | 2-Butyne-1,4-diol | 化学品 | — | K3 |
| `butenediol` | 顺-2-丁烯-1,4-二醇 | `C4H8O2` | 液 | — | ✓ | 第五批：472 | cis-2-Butene-1,4-diol | 化学品 | — | K4 |
| `isopropyl_dioxepine` | 2-异丙基-4,7-二氢-1,3-二氧杂环庚烯 | `C8H14O2` | 液 | — | ✓ | 第五批：472 | 2-Isopropyl-4,7-dihydro-1,3-dioxepine | 化学品 | — | K4 |
| `ethyl_formylalaninate` | N-甲酰-L-丙氨酸乙酯 | `C6H11NO3` | 液 | — | ✓ | 第五批：472 | Ethyl N-Formyl-L-alaninate | 化学品 | — | K4 |
| `methyl_ethoxy_oxazole` | 4-甲基-5-乙氧基噁唑 | `C6H9NO2` | 液 | — | ✓ | 第五批：472 | 4-Methyl-5-ethoxyoxazole | 化学品 | — | K4 |
| `trimethylphenol` | 2,3,6-三甲基苯酚 | `C9H12O` | 固 | — | ✓ | 第五批：463 | 2,3,6-Trimethylphenol | 化学品 | — | K3 |
| `trimethylbenzoquinone` | 2,3,5-三甲基-1,4-苯醌 | `C9H10O2` | 固 | — | ✓ | 第五批：463 | 2,3,5-Trimethyl-1,4-benzoquinone | 化学品 | — | K3 |
| `trimethylhydroquinone` | 三甲基氢醌 | `C9H12O2` | 固 | — | ✓ | 第五批：463 | Trimethylhydroquinone | 化学品 | — | K3 |
| `phytone` | 植酮（6,10,14-三甲基-2-十五酮） | `C18H36O` | 液 | — | ✓ | 第五批：463、469 | Phytone (Hexahydrofarnesylacetone) | 化学品 | — | K4 |
| `isophytol` | 异植醇 | `C20H40O` | 液 | — | ✓ | 第五批：463、465、466、468、469 | Isophytol | 化学品 | — | K4 |
| `ergosterol` | 麦角固醇 | `C28H44O` | 固 | — | ✓ | 第五批：461 | Ergosterol | 化学品 | — | K3 |
| `methylnaphthalene` | 2-甲基萘 | `C11H10` | 固 | — | ✓ | 第五批：469 | 2-Methylnaphthalene | 化学品 | — | K3 |
| `menadione` | 甲萘醌（维生素 K3） | `C11H8O2` | 固 | — | ✓ | 第五批：469 | Menadione | 化学品 | — | K3 |
| `menadiol` | 甲萘氢醌 | `C11H10O2` | 固 | — | ✓ | 第五批：469 | Menadiol | 化学品 | — | K3 |
| `pyridoxal` | 吡哆醛 | `C8H9NO3` | 固 | — | ✓ | 第五批：473 | Pyridoxal | 化学品 | — | K3 |
| `dl_pantolactone` | DL-泛解酸内酯 | `C6H10O3` | 固 | — | ✓ | 第五批：479、480 | DL-Pantolactone | 化学品 | — | K3 |
| `d_pantolactone` | D-泛解酸内酯 | `C6H10O3` | 固 | — | ✓ | 第五批：479、480 | D-Pantolactone | 化学品 | — | K3 |
| `benzylamine` | 苄胺 | `C7H9N` | 液 | — | ✓ | 第五批：481 | Benzylamine | 化学品 | — | K4 |
| `dibenzyl_imidazolidinone_diacid` | 顺-1,3-二苄基-2-氧代咪唑烷-4,5-二甲酸 | `C19H18N2O5` | 固 | — | ✓ | 第五批：481 | cis-1,3-Dibenzyl-2-oxoimidazolidine-4,5-dicarboxylic Acid | 化学品 | — | K3 |
| `biotin_lactone` | D-生物素内酯 | `C19H18N2O3` | 固 | — | ✓ | 第五批：481 | 1,3-Dibenzyl-tetrahydrofuro[3,4-d]imidazole-2,4-dione | 化学品 | — | K3 |
| `biotin_thiolactone` | D-生物素硫代内酯 | `C19H18N2O2S` | 固 | — | ✓ | 第五批：481 | 1,3-Dibenzyl-tetrahydrothieno[3,4-d]imidazole-2,4-dione | 化学品 | — | K3 |
| `dibenzylbiotin` | 二苄基生物素 | `C24H28N2O3S` | 固 | — | ✓ | 第五批：481 | 1,3-Dibenzylbiotin | 化学品 | — | K3 |
| `gamma_butyrobetaine` | γ-丁基甜菜碱 | `C7H15NO2` | 固 | — | ✓ | 第五批：536 | γ-Butyrobetaine | 化学品 | — | K3 |
| `acetyl_dl_methionine` | N-乙酰-DL-蛋氨酸 | `C7H13NO3S` | 固 | — | ✓ | 第五批：545；`acetyl_d_methionine` 消旋回用 | N-Acetyl-DL-methionine | 化学品 | — | K3 |
| `tetrahydrofolic_acid` | 四氢叶酸 | `C19H23N7O6` | 固 | — | ✓ | 第五批：556、557 | Tetrahydrofolic Acid | 化学品 | — | K3 |
| `levomefolic_acid` | （6S）-5-甲基四氢叶酸 | `C20H25N7O6` | 固 | — | ✓ | 第五批：556、557 | Levomefolic Acid | 化学品 | — | K3 |
| `glucosamine_hydrochloride` | D-氨基葡萄糖盐酸盐 | `C6H14ClNO5` | 固 | — | ✓ | 第五批：556 | D-Glucosamine Hydrochloride | 化学品 | — | K3 |
| `sodium_methylselenolate` | 甲硒醇钠 | `CH3NaSe` | 溶液 | — | ✓ | 第五批：513 | Sodium Methylselenolate | 化学品 | — | K0 |
| `acetamide` | 乙酰胺 | `C2H5NO` | 固 | — | ✓ | 第五批：513 | Acetamide | 化学品 | — | K3 |
| `acetamidoacrylic_acid` | α-乙酰氨基丙烯酸 | `C5H7NO3` | 固 | — | ✓ | 第五批：513 | 2-Acetamidoacrylic Acid | 化学品 | — | K3 |
| `acetyl_methylselenocysteine` | N-乙酰-DL-硒甲基硒代半胱氨酸 | `C6H11NO3Se` | 固 | — | ✓ | 第五批：513；`acetyl_d_methylselenocysteine` 消旋回用 | N-Acetyl-DL-Se-methylselenocysteine | 化学品 | — | K3 |
| `glycerophosphoric_acid` | 甘油磷酸 | `C3H9O6P` | 溶液 | — | ✓ | 第五批：500；以后甘油磷酸钠、镁等 | Glycerophosphoric Acid | 化学品 | — | K0 |
| `calcium_oxalate` | 草酸钙 | `C2CaO4` | 固 | — | ✓ | 第五批：496 副产 | Calcium Oxalate | 化学品 | — | K3 |
| `hydroxybutanal` | 4-羟基丁醛 | `C4H8O2` | 液 | — | ✓ | 第五批：S1455 | 4-Hydroxybutanal | 化学品 | — | K4 |
| `carbethoxypropyl_tpp_bromide` | (3-乙氧羰基丙基)三苯基溴化鏻 | `C24H26BrO2P` | 固 | — | ✓ | 第五批：S0474 | (3-Ethoxycarbonylpropyl)triphenylphosphonium Bromide | 化学品 | — | K3 |
| `ethyl_bromoacetate` | 溴乙酸乙酯 | `C4H7BrO2` | 液 | — | ✓ | 第五批：S0461 | Ethyl Bromoacetate | 化学品 | — | K4 |
| `dl_menthol` | dl-薄荷醇 | `C10H20O` | 固 | — | ✓ | 第五批：S1396 | dl-Menthol | 化学品 | — | K3 |
| `methylprenol` | 2-甲基异戊烯醇（2,3-二甲基-2-丁烯-1-醇） | `C6H12O` | 液 | — | ✓ | 第五批：S0233 B | 2-Methylprenol | 化学品 | — | K4 |
| `methyl_citral` | 6-甲基柠檬醛 | `C11H18O` | 液 | — | ✓ | 第五批：S0233 B | 6-Methylcitral | 化学品 | — | K4 |
| `butenol_3` | 3-丁烯-1-醇 | `C4H8O` | 液 | — | ✓ | 第五批：`butenylamine` | 3-Buten-1-ol | 化学品 | — | K4 |
| `butenylamine` | 3-丁烯-1-胺 | `C4H9N` | 液 | — | ✓ | 第五批：S1079 B | 3-Buten-1-amine | 化学品 | — | K4 |
| `pentenol_4` | 4-戊烯-1-醇 | `C5H10O` | 液 | — | ✓ | 第五批：`pentenylamine` | 4-Penten-1-ol | 化学品 | — | K4 |
| `pentenylamine` | 4-戊烯-1-胺 | `C5H11N` | 液 | — | ✓ | 第五批：S1080 B、`methylthiopentylamine` | 4-Penten-1-amine | 化学品 | — | K4 |
| `dibromohexane` | 1,6-二溴己烷 | `C6H12Br2` | 液 | — | ✓ | 第五批：`hexenylamine` | 1,6-Dibromohexane | 化学品 | — | K4 |
| `hexenylamine` | 5-己烯-1-胺 | `C6H13N` | 液 | — | ✓ | 第五批：S1081 B、`methylthiohexylamine` | 5-Hexen-1-amine | 化学品 | — | K4 |
| `methylthiopentylamine` | 5-(甲硫基)戊胺 | `C6H15NS` | 液 | — | ✓ | 第五批：S1088 B | 5-(Methylthio)pentylamine | 化学品 | — | K4 |
| `methylthiohexylamine` | 6-(甲硫基)己胺 | `C7H17NS` | 液 | — | ✓ | 第五批：S1087 B | 6-(Methylthio)hexylamine | 化学品 | — | K4 |
| `isobutyl_tiglate` | 惕各酸异丁酯 | `C9H16O2` | 液 | — | ✓ | 第五批：S0445 B | Isobutyl Tiglate | 化学品 | — | K4 |
| `hexanoic_anhydride` | 己酸酐 | `C12H22O3` | 液 | — | ✓ | 第五批：S0546 B | Hexanoic Anhydride | 化学品 | — | K4 |
| `benzoic_anhydride` | 苯甲酸酐 | `C14H10O3` | 固 | — | ✓ | 第五批：S0600 B、S0606 B | Benzoic Anhydride | 化学品 | — | K3 |
| `jasmonic_acid` | 茉莉酸 | `C12H18O3` | 液 | — | ✓ | 第五批：S0565 | Jasmonic Acid | 化学品 | — | K4 |
| `homoeriodictyol` | 高圣草酚 | `C16H14O6` | 固 | — | ✓ | 第五批：N398 | Homoeriodictyol | 化学品 | — | K5 |
| `sodium_ethylbenzenesulfonate` | 对乙基苯磺酸钠 | `C8H9NaO3S` | 固 | — | ✓ | 第五批：S0101 | Sodium p-Ethylbenzenesulfonate | 化学品 | — | K3 |
| `dimethyldioxane` | 4,4-二甲基-1,3-二氧六环 | `C6H12O2` | 液 | — | ✓ | 第五批：`isoprene` | 4,4-Dimethyl-1,3-dioxane | 化学品 | — | K4 |
| `heptene_1` | 1-庚烯 | `C7H14` | 液 | — | ✓ | 第五批：`heptanediol` | 1-Heptene | 化学品 | — | K4 |
| `nitrotoluene` | 对硝基甲苯 | `C7H7NO2` | 固 | — | ✓ | 第五批：`p_toluidine`、`dinitrotoluene` | p-Nitrotoluene | 化学品 | — | K3 |
| `dinitrotoluene` | 2,3-二硝基甲苯 | `C7H6N2O4` | 固 | — | ✓ | 第五批：`toluene_2_3_diamine` | 2,3-Dinitrotoluene | 化学品 | — | K3 |
| `calcium_stearoyl_lactylate` | 硬脂酰乳酸钙 | `C48H86CaO12` | 固 | — | ✓ | 第五批 425 号 | Calcium Stearoyl Lactylate | 化学品 | — | K8 |
| `propane` | 丙烷 | `C3H8` | 气 | — | ✓ | 第五批 428 号 | Propane | 化学品 | — | K9 |
| `n_butanol` | 1-丁醇 | `C4H10O` | 液 | — | ✓ | 第五批 431 号 | 1-Butanol | 化学品 | — | K4 |
| `isopropanol` | 异丙醇 | `C3H8O` | 液 | — | ✓ | 第五批 434 号 | Isopropyl Alcohol | 化学品 | — | K4 |
| `diethyl_ether` | 乙醚 | `C4H10O` | 液 | — | ✓ | 第五批 435 号 | Diethyl Ether | 化学品 | — | K4 |
| `glutaraldehyde` | 五碳双缩醛（戊二醛） | `C5H8O2` | 溶液 | — | ✓ | 第五批 456 号 | Glutaraldehyde | 化学品 | — | K4 |
| `retinyl_palmitate` | 棕榈酸视黄酯 | `C36H60O2` | 液 | — | ✓ | 第五批 460 号 | Retinyl Palmitate | 化学品 | — | K4 |
| `ergocalciferol` | 麦角钙化醇（维生素 D2） | `C28H44O` | 固 | — | ✓ | 第五批 461 号 | Ergocalciferol (Vitamin D2) | 化学品 | — | K3 |
| `dl_alpha_tocopherol` | dl-α-生育酚 | `C29H50O2` | 液 | — | ✓ | 第五批 463 号 | dl-α-Tocopherol | 化学品 | — | K4 |
| `d_alpha_tocopheryl_acetate` | d-α-醋酸生育酚 | `C31H52O3` | 液 | — | ✓ | 第五批 464 号 | d-α-Tocopheryl Acetate | 化学品 | — | K4 |
| `dl_alpha_tocopheryl_acetate` | dl-α-醋酸生育酚 | `C31H52O3` | 液 | — | ✓ | 第五批 465 号 | dl-α-Tocopheryl Acetate | 化学品 | — | K4 |
| `tocopheryl_calcium_succinate` | 维生素 E 琥珀酸钙 | `C66H106CaO10` | 固 | — | ✓ | 第五批 466 号 | Tocopherol Calcium Succinate | 化学品 | — | K3 |
| `d_alpha_tocopheryl_succinate` | d-α-琥珀酸生育酚 | `C33H54O5` | 固 | — | ✓ | 第五批 467 号 | d-α-Tocopheryl Succinate | 化学品 | — | K3 |
| `dl_alpha_tocopheryl_succinate` | dl-α-琥珀酸生育酚 | `C33H54O5` | 固 | — | ✓ | 第五批 468 号 | dl-α-Tocopheryl Succinate | 化学品 | — | K3 |
| `phylloquinone` | 植物甲萘醌（维生素 K1） | `C31H46O2` | 液 | — | ✓ | 第五批 469 号 | Phylloquinone (Vitamin K1) | 化学品 | — | K4 |
| `thiamine_mononitrate` | 硝酸硫胺素 | `C12H17N5O4S` | 固 | — | ✓ | 第五批 470 号 | Thiamine Mononitrate | 化学品 | — | K3 |
| `riboflavin_phosphate_sodium` | 核黄素-5'-磷酸钠 | `C17H20N4NaO9P·2H2O` | 固 | — | ✓ | 第五批 471 号 | Riboflavin 5'-Phosphate Sodium | 化学品 | — | K3 |
| `pyridoxine_hydrochloride` | 盐酸吡哆醇 | `C8H12ClNO3` | 固 | — | ✓ | 第五批 472 号 | Pyridoxine Hydrochloride | 化学品 | — | K3 |
| `pyridoxal_phosphate` | 5'-磷酸吡哆醛 | `C8H10NO6P` | 固 | — | ✓ | 第五批 473 号 | Pyridoxal 5'-Phosphate | 化学品 | — | K3 |
| `cyanocobalamin` | 氰钴胺 | `C63H88CoN14O14P` | 固 | — | ✓ | 第五批 474 号 | Cyanocobalamin | 化学品 | — | K3 |
| `cyanocobalamin_hydrochloride` | 盐酸氰钴胺 | `C63H88CoN14O14P·HCl` | 固 | — | ✓ | 第五批 475 号 | Cyanocobalamin Hydrochloride | 化学品 | — | K3 |
| `hydroxocobalamin` | 羟钴胺 | `C62H89CoN13O15P` | 固 | — | ✓ | 第五批 476 号 | Hydroxocobalamin | 化学品 | — | K3 |
| `potassium_ascorbate` | L-抗坏血酸钾 | `C6H7KO6` | 固 | — | ✓ | 第五批 477 号 | Potassium L-Ascorbate | 化学品 | — | K3 |
| `magnesium_ascorbyl_phosphate` | 维生素 C 磷酸酯镁 | `C12H12Mg3O18P2` | 固 | — | ✓ | 第五批 478 号 | Magnesium L-Ascorbyl-2-Phosphate | 化学品 | — | K3 |
| `calcium_d_pantothenate` | D-泛酸钙 | `C18H32CaN2O10` | 固 | — | ✓ | 第五批 479 号 | Calcium D-Pantothenate | 化学品 | — | K3 |
| `sodium_d_pantothenate` | D-泛酸钠 | `C9H16NNaO5` | 固 | — | ✓ | 第五批 480 号 | Sodium D-Pantothenate | 化学品 | — | K3 |
| `d_biotin` | D-生物素 | `C10H16N2O3S` | 固 | — | ✓ | 第五批 481 号 | D-Biotin | 化学品 | — | K3 |
| `ferrous_fumarate` | 富马酸亚铁 | `C4H2FeO4` | 固 | — | ✓ | 第五批 482 号 | Ferrous Fumarate | 化学品 | — | K3 |
| `ferric_citrate_powder` | 柠檬酸铁粉 | `C6H5FeO7` | 固 | — | ✓ | 第五批 483 号 | Ferric Citrate Powder | 化学品 | — | K3 |
| `ferrous_lactate` | 乳酸亚铁 | `C6H10FeO6·3H2O` | 固 | — | ✓ | 第五批 484 号 | Ferrous Lactate | 化学品 | — | K3 |
| `hemin` | 氯化高铁血红素 | `C34H32ClFeN4O4` | 固 | — | ✓ | 第五批 485 号 | Hemin | 化学品 | — | K3 |
| `ferrous_bisglycinate` | 甘氨酸亚铁 | `C4H8FeN2O4` | 固 | — | ✓ | 第五批 487 号 | Ferrous Bisglycinate | 化学品 | — | K3 |
| `ferrous_citrate` | 柠檬酸亚铁 | `C6H6FeO7` | 固 | — | ✓ | 第五批 491 号 | Ferrous Citrate | 化学品 | — | K3 |
| `ferrous_succinate` | 琥珀酸亚铁 | `C4H4FeO4` | 固 | — | ✓ | 第五批 492 号 | Ferrous Succinate | 化学品 | — | K3 |
| `heme_iron` | 血红素铁 | `C34H32FeN4O4` | 固 | — | ✓ | 第五批 493 号；486 铁卟啉为同一物质，共用本物料（2026-11-12，11 问题 41） | Heme Iron | 化学品 | — | K5 |
| `calcium_l_lactate` | L-乳酸钙 | `C6H10CaO6·5H2O` | 固 | — | ✓ | 第五批 495 号 | Calcium L-Lactate | 化学品 | — | K3 |
| `calcium_l_threonate` | L-苏糖酸钙 | `C8H14CaO10` | 固 | — | ✓ | 第五批 496 号 | Calcium L-Threonate | 化学品 | — | K3 |
| `calcium_glycinate` | 甘氨酸钙 | `C4H8CaN2O4` | 固 | — | ✓ | 第五批 497 号 | Calcium Glycinate | 化学品 | — | K3 |
| `calcium_aspartate` | 天门冬氨酸钙 | `C8H12CaN2O8` | 固 | — | ✓ | 第五批 498 号 | Calcium L-Aspartate | 化学品 | — | K3 |
| `calcium_citrate_malate` | 柠檬酸苹果酸钙 | `Ca6(C6H5O7)2(C4H4O5)3·5H2O` | 固 | — | ✓ | 第五批 499 号 | Calcium Citrate Malate | 化学品 | — | K3 |
| `calcium_glycerophosphate` | 甘油磷酸钙 | `C3H7CaO6P` | 固 | — | ✓ | 第五批 500 号 | Calcium Glycerophosphate | 化学品 | — | K3 |
| `zinc_citrate` | 柠檬酸锌 | `C12H10O14Zn3·3H2O` | 固 | — | ✓ | 第五批 505 号 | Zinc Citrate | 化学品 | — | K3 |
| `methylselenocysteine` | L-硒-甲基硒代半胱氨酸 | `C4H9NO2Se` | 固 | — | ✓ | 第五批 513 号 | L-Se-Methylselenocysteine | 化学品 | — | K3 |
| `magnesium_gluconate` | 葡萄糖酸镁 | `C12H22MgO14·2H2O` | 固 | — | ✓ | 第五批 517 号 | Magnesium Gluconate | 化学品 | — | K3 |
| `copper_gluconate` | 葡萄糖酸铜 | `C12H22CuO14` | 固 | — | ✓ | 第五批 518 号 | Copper Gluconate | 化学品 | — | K3 |
| `copper_citrate` | 柠檬酸铜 | `(C6H4Cu2O7)2·5H2O` | 固 | — | ✓ | 第五批 519 号 | Copper Citrate | 化学品 | — | K3 |
| `manganese_citrate` | 柠檬酸锰 | `C12H10Mn3O14` | 固 | — | ✓ | 第五批 523 号 | Manganese Citrate | 化学品 | — | K3 |
| `manganese_gluconate` | 葡萄糖酸锰 | `C12H22MnO14·2H2O` | 固 | — | ✓ | 第五批 524 号 | Manganese Gluconate | 化学品 | — | K3 |
| `potassium_gluconate` | 葡萄糖酸钾 | `C6H11KO7` | 固 | — | ✓ | 第五批 525 号 | Potassium Gluconate | 化学品 | — | K3 |
| `choline_chloride` | 氯化胆碱 | `C5H14ClNO` | 固 | — | ✓ | 第五批 531 号 | Choline Chloride | 化学品 | — | K3 |
| `choline_bitartrate` | 酒石酸氢胆碱 | `C9H19NO7` | 固 | — | ✓ | 第五批 532 号 | Choline Bitartrate | 化学品 | — | K3 |
| `myo_inositol` | 肌醇 | `C6H12O6` | 固 | — | ✓ | 第五批 533 号 | Myo-Inositol | 化学品 | — | K2 |
| `l_lysine_hydrochloride` | L-盐酸赖氨酸 | `C6H15ClN2O2` | 固 | — | ✓ | 第五批 534 号 | L-Lysine Monohydrochloride | 化学品 | — | K3 |
| `l_lysine_l_aspartate` | L-赖氨酸天门冬氨酸盐 | `C10H21N3O6` | 固 | — | ✓ | 第五批 535 号 | L-Lysine L-Aspartate | 化学品 | — | K3 |
| `l_carnitine` | L-肉碱 | `C7H15NO3` | 固 | — | ✓ | 第五批 536 号 | L-Carnitine | 化学品 | — | K3 |
| `l_carnitine_l_tartrate` | L-肉碱酒石酸盐 | `C18H36N2O12` | 固 | — | ✓ | 第五批 537 号 | L-Carnitine L-Tartrate | 化学品 | — | K3 |
| `gamma_linolenic_acid` | γ-亚麻酸 | `C18H30O2` | 液 | — | ✓ | 第五批 538 号 | γ-Linolenic Acid | 化学品 | — | K7 |
| `dioleoyl_palmitoyl_glycerol` | 1,3-二油酸 2-棕榈酸甘油三酯 | `C55H102O6` | 液 | — | ✓ | 第五批 540 号 | 1,3-Dioleoyl-2-palmitoylglycerol (OPO) | 化学品 | — | K7 |
| `l_methionine` | L-蛋氨酸 | `C5H11NO2S` | 固 | — | ✓ | 第五批 545 号 | L-Methionine | 化学品 | — | K3 |
| `l_tyrosine` | L-酪氨酸 | `C9H11NO3` | 固 | — | ✓ | 第五批 546 号 | L-Tyrosine | 化学品 | — | K3 |
| `l_tryptophan` | L-色氨酸 | `C11H12N2O2` | 固 | — | ✓ | 第五批 547 号 | L-Tryptophan | 化学品 | — | K3 |
| `raffinose` | 棉子糖 | `C18H32O16·5H2O` | 固 | — | ✓ | 第五批 550 号 | Raffinose Pentahydrate | 化学品 | — | K2 |
| `cytidylic_acid` | 5'-单磷酸胞苷 | `C9H14N3O8P` | 固 | — | ✓ | 第五批 551 号 | Cytidine 5'-Monophosphate | 化学品 | — | K3 |
| `uridylic_acid` | 5'-单磷酸尿苷 | `C9H13N2O9P` | 固 | — | ✓ | 第五批 552 号 | Uridine 5'-Monophosphate | 化学品 | — | K3 |
| `uridylic_disodium` | 5'-尿苷酸二钠 | `C9H11N2Na2O9P` | 固 | — | ✓ | 第五批 553 号 | Disodium 5'-Uridylate | 化学品 | — | K3 |
| `cytidylic_disodium` | 5'-胞苷酸二钠 | `C9H12N3Na2O8P` | 固 | — | ✓ | 第五批 554 号 | Disodium 5'-Cytidylate | 化学品 | — | K3 |
| `menaquinone_7` | 维生素 K2 | `C46H64O2` | 固 | — | ✓ | 第五批 555 号 | Vitamin K2 (Menaquinone-7) | 化学品 | — | K3 |
| `levomefolate_glucosamine` | （6S）-5-甲基四氢叶酸氨基葡萄糖盐 | `C32H51N9O16` | 固 | — | ✓ | 第五批 556 号 | (6S)-5-Methyltetrahydrofolic Acid, Glucosamine Salt | 化学品 | — | K3 |
| `levomefolate_calcium` | （6S）-5-甲基四氢叶酸钙 | `C20H23CaN7O6` | 固 | — | ✓ | 第五批 557 号 | Calcium L-Methylfolate | 化学品 | — | K3 |
| `sodium_ferrous_citrate` | 柠檬酸亚铁钠 | `C12H10FeNa4O14` | 固 | — | ✓ | 第五批 558 号 | Sodium Ferrous Citrate | 化学品 | — | K3 |
| `magnesium_l_threonate` | L-苏糖酸镁 | `C8H14MgO10·H2O` | 固 | — | ✓ | 第五批 559 号 | Magnesium L-Threonate | 化学品 | — | K3 |
| `fucosyllactose` | 2'-岩藻糖基乳糖 | `C18H32O15` | 固 | — | ✓ | 第五批 561 号 | 2'-Fucosyllactose | 化学品 | — | K2 |
| `lacto_n_neotetraose` | 乳糖-N-新四糖 | `C26H45NO21` | 固 | — | ✓ | 第五批 562 号 | Lacto-N-neotetraose | 化学品 | — | K2 |
| `sialyllactose_sodium` | 3'-唾液酸乳糖钠盐 | `C23H38NNaO19` | 固 | — | ✓ | 第五批 563 号 | 3'-Sialyllactose Sodium Salt | 化学品 | — | K2 |
| `sodium_2_naphtholate` | 2-萘酚钠 | `C10H7NaO` | 固 | — | ✓ | 第三批 80 号：2-萘磺酸碱熔所得，水溶后酸化得 2-萘酚（2026-11-12，11 问题 42） | Sodium 2-Naphtholate | 化学品 | — | K3 |
| `disodium_resorcinolate` | 间苯二酚二钠 | `C6H4Na2O2` | 固 | — | ✓ | 第四批 195 号：间苯二磺酸碱熔所得，水溶后酸化得间苯二酚（2026-11-12，11 问题 42） | Disodium Resorcinolate | 化学品 | — | K3 |
| `sodium_p_ethylphenolate` | 对乙基苯酚钠 | `C8H9NaO` | 固 | — | ✓ | 第五批：S0101 | Sodium 4-Ethylphenolate | 化学品 | — | K3 |
| `disodium_sebacate` | 癸二酸二钠 | `C10H16Na2O4` | 固 | — | ✓ | 第五批：`sebacic_acid`（S0015 碱裂解副产） | Disodium Sebacate | 化学品 | — | K3 |
| `acetyl_d_methylselenocysteine` | N-乙酰-D-硒甲基硒代半胱氨酸 | `C6H11NO3Se` | 固 | — | ✓ | 第五批：513（酶法拆分剩下的 D 体，消旋后回到拆分） | N-Acetyl-D-Se-methylselenocysteine | 化学品 | — | K3 |
| `acetyl_d_methionine` | N-乙酰-D-蛋氨酸 | `C7H13NO3S` | 固 | — | ✓ | 第五批：545（酶法拆分剩下的 D 体，消旋后回到拆分） | N-Acetyl-D-methionine | 化学品 | — | K3 |
| `cobalt_acetate` | 乙酸钴 | `C4H6CoO4·4H2O` | 固 | — | ✓ | 液相空气氧化催化剂（23、332 与香料的烯丙位氧化）；原为 08 §C 的“钴盐”（2026-11-12，11 问题 43） | Cobalt(II) Acetate Tetrahydrate | 催化剂 | #B04870 | — |

## C 高分子、天然混合物

| 建议 ID | 名称 | 化学式 / 组成 | 状态 | GTCEu 材料 | 核对 | 备注 | 英文名（待审） | 分类 | 颜色 | 纯度类别 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `starch` | 淀粉 | `(C6H10O5)n` | 固 | — |  |  | Starch | 化学品 | — | K6 |
| `cellulose` | 纤维素 | `(C6H10O5)n` | 固 | — |  | 第四批 162 号 | Cellulose | 化学品 | — | K6 |
| `alginic_acid` | 海藻酸 | `(C6H8O6)n` | 固 | — |  |  | Alginic Acid | 化学品 | — | K6 |
| `sodium_alginate` | 海藻酸钠 | `(C6H7NaO6)n` | 固 | — |  | 表 B.2 N396 | Sodium Alginate | 化学品 | — | K6 |
| `calcium_alginate` | 海藻酸钙 | `[(C6H7O6)2Ca]n` | 固 | — |  | 第四批 317 号 | Calcium Alginate | 化学品 | — | K6 |
| `agarose` | 琼脂糖 | `(C12H18O9)n` | 固 | — |  | 琼脂主成分 | Agarose | 化学品 | — | K6 |
| `pectin` | 果胶 | 部分甲酯化的聚半乳糖醛酸，单元 C6H8O6 / C7H10O6 | 固 | — |  |  | Pectin | 化学品 | — | K6 |
| `gelatin` | 明胶 | 蛋白质（甘氨酸–脯氨酸–羟脯氨酸重复） | 固 | gelatin |  |  | Gelatin | 化学品 | — | K6 |
| `gluten` | 面筋 | 蛋白质（麦醇溶蛋白 + 麦谷蛋白） | 固 | — |  |  | Gluten | 化学品 | — | K6 |
| `lignin` | 木质素 | 酚类聚合物 | 固 | — |  | 多组分混合物，不带纯度（2026-11-05） | Lignin | 化学品 | — | — |
| `caramel` | 焦糖色 | 糖类热解聚合物 | 液 | — |  | 第四批 239 号 | Caramel Color | 化学品 | #4A2408 | K5 |
| `wood_tar` | 木焦油 | 酚类混合物（愈创木酚、甲酚等） | 液 | creosote（近似） |  | 导入 `#forge:creosote`；杂酚油为其馏分；多组分混合物，不带纯度（2026-11-05） | Wood Tar | 化学品 | #3A2414 | — |
| `wood_vinegar` | 木醋液 | 乙酸、甲醇、丙酮与水的混合物 | 液 | — |  | 1 C 木材干馏的冷凝液，蒸馏分出甲醇与乙酸；多组分混合物，不带纯度；I18 补（2026-11-11）；表 B.2 N267 的香料级另设 `refined_wood_vinegar`（2026-11-12） | Wood Vinegar | 化学品 | #8A5A2C | — |
| `mint_oil` | 薄荷原油 | l-薄荷醇（约 70%–80%）、薄荷酮等的混合物 | 液 | — |  | 29 A 薄荷水蒸气蒸馏、油水分层所得，冷冻析出薄荷醇；多组分混合物，不带纯度；I18 补（2026-11-11）；表 B.2 N150 | Crude Mint Oil | 化学品 | #C8E8B0 | — |
| `coal_tar` | 煤焦油 | 芳烃混合物（苯、甲苯、萘等） | 液 | coal_tar |  | 多组分混合物，不带纯度（2026-11-05） | Coal Tar | 化学品 | #1A1A1A | — |
| `crude_oil` | 原油 | 烃类混合物 | 液 | oil |  | 多组分混合物，不带纯度（2026-11-05） | Crude Oil | 化学品 | #151510 | — |
| `carrageenan` | 卡拉胶 | 硫酸化半乳聚糖 | 固 | — |  |  | Carrageenan | 化学品 | — | K6 |
| `xanthan_gum` | 黄原胶 | 五糖重复单元胞外多糖 | 固 | — |  |  | Xanthan Gum | 化学品 | — | K6 |
| `gellan_gum` | 结冷胶 | 四糖重复单元胞外多糖 | 固 | — |  |  | Gellan Gum | 化学品 | — | K6 |
| `nisin` | 乳酸链球菌素 | 34 肽，`C143H230N42O37S7` | 固 | — |  |  | Nisin | 化学品 | — | K5 |
| `natamycin` | 纳他霉素 | 多烯大环内酯，`C33H47NO13` | 固 | — |  |  | Natamycin | 化学品 | — | K5 |
| `lecithin` | 磷脂（卵磷脂） | 磷脂酰胆碱为主的混合物 | 固 | — |  |  | Phospholipids (Lecithin) | 化学品 | — | K8 |
| `polyvinyl_acetate` | 聚乙酸乙烯酯 | `(C4H6O2)n` | 固 | polyvinyl_acetate |  | 胶基 | Polyvinyl Acetate | 化学品 | — | K3 |
| `polyisobutylene` | 聚异丁烯 | `(C4H8)n` | 固 | — |  | 胶基 | Polyisobutylene | 化学品 | — | K3 |
| `natural_rubber` | 天然橡胶 | `(C5H8)n` | 固 | rubber |  | 胶基 | Natural Rubber | 化学品 | — | K6 |
| `rosin` | 松香 | 树脂酸混合物，主成分松香酸 C20H30O2 | 固 | — |  | 多组分混合物，不带纯度（2026-11-05） | Rosin | 化学品 | — | — |
| `enzymes` | 酶制剂 | 蛋白质 | 固 | — |  | 不带纯度（2026-11-05） | Enzyme Preparation | 酶 | — | — |
| `tannin` | 单宁 | 没食子酰葡萄糖混合物，以单宁酸 C76H52O46 计 | 固 | — |  | 没食子酸原料 | Tannin | 化学品 | — | K5 |
| `rosin_glycerol_ester` | 松香甘油酯 | 松香酸与丙三醇的酯混合物 | 固 | — |  | 胶基树脂 | Glycerol Ester of Rosin | 化学品 | — | K5 |
| `beeswax` | 蜂蜡 | 棕榈酸三十醇酯等长链酯混合物 | 固 | — |  | 胶基蜡、上光；第四批 280 号 | Beeswax | 化学品 | — | K7 |
| `microcrystalline_wax` | 微晶蜡 | C30–C70 支链烷烃混合物 | 固 | — |  | 胶基蜡 | Microcrystalline Wax | 化学品 | — | K7 |
| `lanolin` | 羊毛脂 | 甾醇与脂肪醇的脂肪酸酯混合物 | 固 | — |  | 胆固醇原料；多组分混合物，不带纯度（2026-11-05） | Lanolin | 化学品 | — | — |
| `castor_oil` | 蓖麻油 | 蓖麻油酸甘油三酯为主 | 液 | — |  | γ-癸内酯原料；多组分混合物，不带纯度（2026-11-05） | Castor Oil | 化学品 | #E8D890 | — |
| `gum_base` | 胶基 | 弹性体、树脂、蜡、软化剂、填料的混炼物 | 固 | — |  | 口香糖基料 | Gum Base | 化学品 | — | K12 |
| `baking_powder` | 复配膨松剂（泡打粉） | 碳酸氢钠、焦磷酸二氢二钠与淀粉的混合物 | 固 | — |  | 第一批 6 号的复配产品（2026-11-11 由 12 成品清单移入） | Baking Powder | 化学品 | — | K12 |
| `glutamate_broth` | 谷氨酸发酵液 | 谷氨酸铵为主的发酵液，含菌体、残糖 | 液 | — |  | 等电结晶原料；多组分混合物，不带纯度（2026-11-05） | Glutamate Fermentation Broth | 化学品 | #C8B070 | — |
| `kelp_ash` | 海藻灰 | 碳酸钠、氯化钠、硫酸盐等的混合物 | 固 | — |  | H10 烧灰（07 第 5 号）；I28 补；多组分混合物，不带纯度（2026-11-05） | Kelp Ash | 化学品 | — | — |
| `wood_ash` | 草木灰 | 碳酸钾、碳酸钙等的混合物 | 固 | — |  | H10 烧灰（07 第 7 号）；I28 补；多组分混合物，不带纯度（2026-11-05） | Wood Ash | 化学品 | — | — |
| `soda_lye` | 碱液 | 碳酸钠为主的水溶液 | 溶液 | — |  | H05 淋海藻灰（07 第 5 号），熬干得粗碱；I28 补；多组分混合物，不带纯度（2026-11-05） | Soda Lye | 化学品 | #C8BC8C | — |
| `potash_lye` | 钾碱液 | 碳酸钾为主的水溶液 | 溶液 | — |  | H05 淋草木灰（07 第 7 号），熬干得粗钾碱；I28 补；多组分混合物，不带纯度（2026-11-05） | Potash Lye | 化学品 | #B8A47C | — |
| `saltpeter_liquor` | 硝水 | 硝酸钙、硝酸钾等的水溶液 | 溶液 | — |  | H05 淋硝土（07 第 24 号）；I28 补；多组分混合物，不带纯度（2026-11-05） | Saltpeter Liquor | 化学品 | #D8D0B0 | — |
| `agar_jelly` | 石花菜冻 | 琼脂凝胶，含水约 99% | 固 | — |  | 红藻熬煮、过滤、冷却所得（07 第 14 号），冻融得条状琼脂；I28 补 | Agar Jelly | 化学品 | — | K0 |
| `pectin_liquor` | 果胶液 | 果胶的稀水溶液 | 液 | — |  | 16 A 苹果熬煮所得，只能直接用于果酱；酸提液醇沉得果胶；I18 补（2026-11-11） | Pectin Liquor | 化学品 | #E8DCA8 | K0 |
| `red_yeast_rice` | 红曲米 | 红曲霉固态发酵的大米（含红曲色素） | 固 | — |  | 21 A 曲房产物，唯一保留随机纯度的物料（10 §2.2）；I18 补（2026-11-11） | Red Yeast Rice | 化学品 | — | K5 |
| `agar` | 琼脂 | 琼脂糖与琼脂胶，以琼脂糖计 | 固 | — |  | 07 第 14 号（条状琼脂）；I12 补（2026-11-10） | Agar | 化学品 | — | K6 |
| `steviol_glycosides` | 甜菊糖苷 | 甜菊苷、莱鲍迪苷 A 等，以甜菊苷计 | 固 | — |  | 07 第 36 号（甜菊叶浸液）；I12 补（2026-11-10） | Steviol Glycosides | 化学品 | — | K5 |
| `gardenia_yellow` | 栀子黄 | 西红花苷、西红花酸 | 固 | — |  | 07 第 62 号（栀子染液）；I12 补（2026-11-10） | Gardenia Yellow | 化学品 | — | K5 |
| `fish_liver_oil` | 鱼肝油 | 甘油三酯为主，含维生素 A、D | 液 | — |  | 熬炼鱼肝（07 第 107 号）；I28 补；多组分混合物，不带纯度（2026-11-05） | Fish Liver Oil | 化学品 | #E0B040 | — |
| `sulfite_spent_liquor` | 亚硫酸盐制浆废液 | 木质素磺酸盐与半纤维素糖的水溶液 | 溶液 | — |  | 第四批：162 副产；30 号 B 的原料；多组分混合物，不带纯度（2026-11-05） | Sulfite Spent Liquor | 化学品 | #6A4A28 | — |
| `copper_catalyst` | 铜催化剂 | `Cu`（含锌、锡助剂） | 固 | — |  | 第四批：163 | Copper Catalyst | 催化剂 | — | — |
| `zinc_hydroxide` | 氢氧化锌 | `Zn(OH)2` | 固 | — |  | 第四批：167 副产 | Zinc Hydroxide | 化学品 | — | K1 |
| `titanium_slag` | 高钛渣 | TiO2 为主，含铁、钙、镁氧化物 | 固 | — |  | 第四批：198；多组分混合物，不带纯度（2026-11-05） | Titanium Slag | 化学品 | — | — |
| `lac_wash_liquor` | 紫胶洗色水 | 紫胶色酸水溶液，含蜡、虫体碎屑 | 溶液 | — |  | 第四批：207；多组分混合物，不带纯度（2026-11-05） | Lac Wash Liquor | 化学品 | #A02838 | — |
| `marigold_oleoresin` | 万寿菊油树脂 | 叶黄素脂肪酸酯为主，以叶黄素二棕榈酸酯 C72H116O4 计 | 固 | — |  | 第四批：234；多组分混合物，不带纯度（2026-11-05） | Marigold Oleoresin | 化学品 | — | — |
| `maltodextrin` | 麦芽糊精 | `(C6H10O5)n`，DE 10–20 | 固 | — |  | 第四批：水溶线喷雾干燥载体；19 B、P79 已用 | Maltodextrin | 化学品 | — | K6 |
| `coconut_oil` | 椰子油 | 月桂酸甘油三酯为主，以三月桂酸甘油酯 C39H74O6 计 | 液 | — |  | 第四批：252、256、257、324、325、342；多组分混合物，不带纯度（2026-11-05） | Coconut Oil | 化学品 | #F2EED8 | — |
| `palm_oil` | 棕榈油 | 棕榈酸、油酸甘油三酯为主，以三棕榈酸甘油酯 C51H98O6 计 | 液 | — |  | 第四批：248、258；多组分混合物，不带纯度（2026-11-05） | Palm Oil | 化学品 | #E8A040 | — |
| `coconut_fatty_acids` | 椰子油脂肪酸 | 月桂酸约 45%，另含肉豆蔻酸、辛酸、癸酸、棕榈酸、油酸 | 液 | — |  | 第四批：252、256；多组分混合物，不带纯度（2026-11-05） | Coconut Fatty Acids | 化学品 | #EAE2C0 | — |
| `palm_fatty_acids` | 棕榈油脂肪酸 | 棕榈酸约 44%，另含油酸、亚油酸、硬脂酸 | 液 | — |  | 第四批：248；多组分混合物，不带纯度（2026-11-05） | Palm Fatty Acids | 化学品 | #E0C888 | — |
| `polyglycerol` | 聚甘油 | 丙三醇醚化缩聚物，以三聚甘油 C9H20O7 计 | 液 | — |  | 第四批：255、261；多组分混合物，不带纯度（2026-11-05） | Polyglycerol | 化学品 | #E0C890 | — |
| `polyricinoleic_acid` | 聚蓖麻醇酸 | 蓖麻油酸自缩聚物，以四聚体 C72H130O9 计 | 液 | — |  | 第四批：261；多组分混合物，不带纯度（2026-11-05） | Polyricinoleic Acid | 化学品 | #C8A050 | — |
| `vacuum_gas_oil` | 减压馏分油 | C15–C40 烃类混合物，含蜡、芳烃、硫化物 | 液 | — |  | 第四批：272；多组分混合物，不带纯度（2026-11-05） | Vacuum Gas Oil | 化学品 | #3A2A18 | — |
| `dewaxed_oil` | 脱蜡油 | 脱蜡后的减压馏分油，含芳烃、硫化物 | 液 | — |  | 第四批：272；多组分混合物，不带纯度（2026-11-05） | Dewaxed Oil | 化学品 | #5A4020 | — |
| `paraffin_wax` | 石蜡 | C20–C40 正构烷烃混合物 | 固 | — |  | 第四批：262、272（副产）；2026-11-11 定：蜡统一 K7（与微晶蜡一致，11 问题 31）；第五批 452 号 | Paraffin Wax | 化学品 | — | K7 |
| `casein` | 酪蛋白 | 乳蛋白（αs-、β-、κ-酪蛋白） | 固 | — |  | 第四批：244 | Casein | 化学品 | — | K6 |
| `seedlac` | 颗粒紫胶 | 紫胶树脂为主，含蜡与残余色素 | 固 | — |  | 第四批：271；多组分混合物，不带纯度（2026-11-05） | Seedlac | 化学品 | — | — |
| `liquefied_starch` | 液化淀粉液 | DE 5–8 的糊精水溶液 | 溶液 | — |  | 第四批：284、285、301；多组分混合物，不带纯度（2026-11-05） | Liquefied Starch | 化学品 | #EDE8D8 | — |
| `alpha_cd_cyclohexane_complex` | α-环状糊精–环己烷包合物 | α-环状糊精与环己烷的包合物（湿饼） | 固 | — |  | 第四批：284；湿饼，不带纯度（2026-11-05） | α-Cyclodextrin–Cyclohexane Complex | 化学品 | — | — |
| `beta_cd_toluene_complex` | β-环状糊精–甲苯包合物 | β-环状糊精与甲苯的包合物（湿饼） | 固 | — |  | 第四批：301；湿饼，不带纯度（2026-11-05） | β-Cyclodextrin–Toluene Complex | 化学品 | — | — |
| `polysuccinimide` | 聚琥珀酰亚胺 | `(C4H3NO2)n` | 固 | — |  | 第四批：286 | Polysuccinimide | 化学品 | — | K3 |
| `immobilized_sucrose_isomerase` | 固定化蔗糖异构酶菌体 | 海藻酸钙包埋的红色精朊杆菌灭活菌体 | 固 | — |  | 第四批：350 | Immobilized Sucrose Isomerase Cells | 催化剂 | — | — |
| `immobilized_aspartate_decarboxylase` | 固定化 L-天冬氨酸 β-脱羧酶菌体 | 卡拉胶包埋的达库哈假单胞菌菌体 | 固 | — |  | 第四批：179（P96；2026-11-11 用户定：固定化酶各设物品） | Immobilized Aspartate β-Decarboxylase Cells | 催化剂 | — | — |
| `immobilized_lipase` | 固定化脂肪酶 | 海藻酸钙包埋的脂肪酶（102） | 固 | — |  | 第四批：330（P96，同上） | Immobilized Lipase | 催化剂 | — | — |
| `immobilized_psicose_epimerase` | 固定化 D-阿洛酮糖-3-差向异构酶 | 海藻酸钙包埋的酶（374） | 固 | — |  | 第四批：374（P96，同上） | Immobilized D-Psicose 3-Epimerase | 催化剂 | — | — |
| `immobilized_glucose_isomerase` | 固定化葡糖异构酶 | 海藻酸钙包埋的锈棕色链霉菌菌体（406） | 固 | — |  | 第四批：406（P96，同上） | Immobilized Glucose Isomerase | 催化剂 | — | — |
| `pancreatic_extract` | 胰脏浸提液 | 组成：胰蛋白酶原、胰凝乳蛋白酶原、磷脂酶 A2 原、胰淀粉酶、胰脂肪酶的稀酸溶液 | 溶液 | — |  | 第四批：397、399、418、419；酶源，不带纯度（2026-11-05） | Pancreatic Extract | 化学品 | #D8B0A0 | — |
| `metatartaric_acid` | 偏酒石酸 | `(C4H4O5)n` | 固 | — |  | 第四批 141 号；酒石酸分子间酯化聚合物，极易吸湿 | Metatartaric Acid | 化学品 | — | K3 |
| `microcrystalline_cellulose` | 微晶纤维素 | `(C6H10O5)n` | 固 | — |  | 第四批 156 号 | Microcrystalline Cellulose | 化学品 | — | K6 |
| `ferric_ammonium_citrate` | 柠檬酸铁铵 | 柠檬酸铁(III)铵络盐，组成不定 | 固 | — |  | 第四批 160 号 | Ferric Ammonium Citrate | 化学品 | — | K3 |
| `ferric_tartrate` | 酒石酸铁 | 铁(III) 与 meso-、D-、L-酒石酸的络合物水溶液，组成不定 | 溶液 | — |  | 第四批 161 号；产品为溶液（喷于盐上防结块）；界限见 10 §1.5 | Iron Tartrate | 化学品 | #8A4A20 | K4 |
| `polydimethylsiloxane` | 聚二甲基硅氧烷（硅油） | `(C2H6OSi)n` | 液 | — |  | 第四批 163 号；乳液另设 `polydimethylsiloxane_emulsion`（2026-11-11，用户定） | Polydimethylsiloxane | 化学品 | #E8EEF2 | K3 |
| `safflower_yellow` | 红花黄 | 查尔酮类，主成分羟基红花黄色素 A C27H32O16 | 固 | — |  | 第四批 206 号 | Safflower Yellow | 化学品 | — | K5 |
| `lac_dye` | 紫胶红（虫胶红） | 紫胶色酸混合物，主成分紫胶色酸 A C26H19NO12 | 固 | — |  | 第四批 207 号 | Lac Dye | 化学品 | — | K5 |
| `lingonberry_red` | 越橘红 | 花青素类 | 固 | — |  | 第四批 208 号 | Lingonberry Red | 化学品 | — | K5 |
| `capsicum_orange` | 辣椒橙 | β-胡萝卜素、玉米黄质、β-隐黄质 | 固 | — |  | 第四批 209 号 | Paprika Orange | 化学品 | — | K5 |
| `red_rice_red` | 红米红 | 花青素类 | 固 | — |  | 第四批 210 号 | Red Rice Red | 化学品 | — | K5 |
| `coreopsis_yellow` | 菊花黄浸膏 | 黄酮类浸膏 | 固 | — |  | 第四批 211 号 | Coreopsis Yellow Extract | 化学品 | — | K5 |
| `black_bean_red` | 黑豆红 | 花青素类 | 固 | — |  | 第四批 212 号 | Black Bean Red | 化学品 | — | K5 |
| `sorghum_red` | 高粱红 | 黄酮类 | 固 | — |  | 第四批 213 号 | Sorghum Red | 化学品 | — | K5 |
| `corn_yellow` | 玉米黄 | 玉米黄质 C40H56O2、叶黄素为主 | 固 | — |  | 第四批 214 号 | Corn Yellow | 化学品 | — | K5 |
| `radish_red` | 萝卜红 | 花青素类 | 固 | — |  | 第四批 215 号 | Radish Red | 化学品 | — | K5 |
| `cocoa_shell_color` | 可可壳色 | 多酚聚合物 | 固 | — |  | 第四批 216 号 | Cocoa Shell Color | 化学品 | — | K5 |
| `blackcurrant_red` | 黑加仑红 | 花青素类 | 固 | — |  | 第四批 217 号 | Blackcurrant Red | 化学品 | — | K5 |
| `gardenia_blue` | 栀子蓝 | 京尼平–氨基酸聚合物 | 固 | — |  | 第四批 218 号 | Gardenia Blue | 化学品 | — | K5 |
| `sea_buckthorn_yellow` | 沙棘黄 | 类胡萝卜素、黄酮 | 固 | — |  | 第四批 219 号 | Sea Buckthorn Yellow | 化学品 | — | K5 |
| `roselle_red` | 玫瑰茄红 | 花青素类 | 固 | — |  | 第四批 220 号 | Roselle Red | 化学品 | — | K5 |
| `acorn_shell_brown` | 橡子壳棕 | 多酚类 | 固 | — |  | 第四批 221 号 | Acorn Shell Brown | 化学品 | — | K5 |
| `mulberry_red` | 桑椹红 | 花青素类 | 固 | — |  | 第四批 222 号 | Mulberry Red | 化学品 | — | K5 |
| `amaranthus_red` | 天然苋菜红 | 甜菜色素类 | 固 | — |  | 第四批 223 号 | Natural Amaranthus Red | 化学品 | — | K5 |
| `rosa_laevigata_brown` | 金樱子棕 | 多酚、黄酮类 | 固 | — |  | 第四批 224 号 | Cherokee Rose Brown | 化学品 | — | K5 |
| `peanut_skin_red` | 花生衣红 | 原花青素类 | 固 | — |  | 第四批 225 号 | Peanut Skin Red | 化学品 | — | K5 |
| `grape_skin_red` | 葡萄皮红 | 花青素类 | 固 | — |  | 第四批 226 号 | Grape Skin Red | 化学品 | — | K5 |
| `haskap_red` | 蓝锭果红 | 花青素类 | 固 | — |  | 第四批 227 号 | Haskap Red | 化学品 | — | K5 |
| `phycocyanin` | 藻蓝 | 藻蓝蛋白（蛋白质） | 固 | — |  | 第四批 228 号 | Phycocyanin | 化学品 | — | K5 |
| `lithospermum_red` | 紫草红 | 主成分紫草素 C16H16O5 | 固 | — |  | 第四批 230 号 | Gromwell Red | 化学品 | — | K5 |
| `citrus_yellow` | 柑橘黄 | 类胡萝卜素 | 固 | — |  | 第四批 231 号 | Citrus Yellow | 化学品 | — | K5 |
| `bayberry_red` | 杨梅红 | 花青素类 | 固 | — |  | 第四批 237 号 | Bayberry Red | 化学品 | — | K5 |
| `monascus_yellow` | 红曲黄色素 | 红曲素 C21H26O5、红曲黄素 C23H30O5 | 固 | — |  | 第四批 240 号 | Monascus Yellow | 化学品 | — | K5 |
| `purple_sweet_potato_color` | 紫甘薯色素 | 花青素类 | 固 | — |  | 第四批 242 号 | Purple Sweet Potato Color | 化学品 | — | K5 |
| `sodium_caseinate` | 酪蛋白酸钠 | 乳酪蛋白的钠盐（蛋白质） | 固 | — |  | 第四批 244 号 | Sodium Caseinate | 化学品 | — | K6 |
| `diacetyltartaric_monoglycerides` | 双乙酰酒石酸单双甘油酯 | 混合酯，以单硬脂酸甘油酯双乙酰酒石酸酯 C29H50O11 计 | 固 | — |  | 第四批 249 号 | Diacetyl Tartaric Acid Esters of Mono- and Diglycerides | 化学品 | — | K8 |
| `polysorbate_60` | 聚氧乙烯(20)山梨醇酐单硬脂酸酯（吐温 60） | 聚氧乙烯醚混合物，以 C64H126O26 计 | 液 | — |  | 第四批 251 号 | Polyoxyethylene (20) Sorbitan Monostearate (Polysorbate 60) | 化学品 | #D0A848 | K8 |
| `polysorbate_80` | 聚氧乙烯(20)山梨醇酐单油酸酯（吐温 80） | 聚氧乙烯醚混合物，以 C64H124O26 计 | 液 | — |  | 第二批 72 号；I19 补（2026-11-11） | Polyoxyethylene (20) Sorbitan Monooleate (Polysorbate 80) | 化学品 | #E0C870 | K8 |
| `caprylic_capric_triglyceride` | 辛，癸酸甘油酯 | 辛酸、癸酸甘油三酯混合物，以三辛酸甘油酯 C27H50O6 计 | 液 | — |  | 第四批 252 号 | Caprylic/Capric Triglyceride | 化学品 | #F0ECD0 | K7 |
| `modified_soy_lecithin` | 改性大豆磷脂 | 乙酰化或羟基化的大豆磷脂混合物 | 固 | — |  | 第四批 253 号 | Modified Soybean Lecithin | 化学品 | — | K8 |
| `polyglycerol_esters` | 聚甘油脂肪酸酯 | 混合酯，以三聚甘油单硬脂酸酯 C27H54O8 计 | 固 | — |  | 第四批 255 号 | Polyglycerol Esters of Fatty Acids | 化学品 | — | K8 |
| `polysorbate_20` | 聚氧乙烯(20)山梨醇酐单月桂酸酯（吐温 20） | 聚氧乙烯醚混合物，以 C58H114O26 计 | 液 | — |  | 第四批 257 号 | Polyoxyethylene (20) Sorbitan Monolaurate (Polysorbate 20) | 化学品 | #D8B858 | K8 |
| `polysorbate_40` | 聚氧乙烯(20)山梨醇酐单棕榈酸酯（吐温 40） | 聚氧乙烯醚混合物，以 C62H122O26 计 | 液 | — |  | 第四批 258 号 | Polyoxyethylene (20) Sorbitan Monopalmitate (Polysorbate 40) | 化学品 | #D4B050 | K8 |
| `acetylated_monoglycerides` | 乙酰化单，双甘油脂肪酸酯 | 混合酯，以单硬脂酸甘油酯二乙酸酯 C25H46O6 计 | 液 | — |  | 第四批 259 号 | Acetylated Mono- and Diglycerides | 化学品 | #EEE6C0 | K8 |
| `polyglycerol_polyricinoleate` | 聚甘油蓖麻醇酸酯 | 混合酯，以三聚甘油聚蓖麻醇酸酯 C81H148O15 计 | 液 | — |  | 第四批 261 号 | Polyglycerol Polyricinoleate (PGPR) | 化学品 | #B08838 | K8 |
| `starch_sodium_octenylsuccinate` | 辛烯基琥珀酸淀粉钠 | 变性淀粉 (C6H10O5)n，少量单元为辛烯基琥珀酸酯钠盐 C18H27NaO8 | 固 | — |  | 第四批 262 号 | Starch Sodium Octenyl Succinate | 化学品 | — | K6 |
| `lactylated_monoglycerides` | 乳酸脂肪酸甘油酯 | 混合酯，以单硬脂酸甘油酯乳酸酯 C24H46O6 计 | 固 | — |  | 第四批 263 号 | Lactic and Fatty Acid Esters of Glycerol (LACTEM) | 化学品 | — | K8 |
| `citrated_monoglycerides` | 柠檬酸脂肪酸甘油酯 | 混合酯，以单硬脂酸甘油酯柠檬酸酯 C27H48O10 计 | 固 | — |  | 第四批 264 号 | Citric and Fatty Acid Esters of Glycerol (CITREM) | 化学品 | — | K8 |
| `ammonium_phosphatide` | 铵磷脂 | 磷脂酸铵盐混合物，以单硬脂酸甘油酯磷酸铵 C21H46NO7P 计 | 固 | — |  | 第四批 265 号 | Ammonium Phosphatides | 化学品 | — | K8 |
| `succinylated_monoglycerides` | 琥珀酸单甘油酯 | 混合酯，以单硬脂酸甘油酯琥珀酸酯 C25H46O7 计 | 固 | — |  | 第四批 266 号 | Succinylated Monoglycerides | 化学品 | — | K8 |
| `hydrolyzed_lecithin` | 酶解大豆磷脂 | 溶血磷脂为主的大豆磷脂混合物 | 固 | — |  | 第四批 268 号 | Enzymatically Hydrolysed Soybean Lecithin | 化学品 | — | K8 |
| `quillaja_extract` | 皂树皮提取物 | 三萜皂苷为主，苷元以皂树酸 C30H46O5 计 | 固 | — |  | 第四批 269 号；表 B.2 N176 | Quillaia Extract | 化学品 | — | K5 |
| `ghatti_gum` | 达瓦树胶 | 酸性多糖钙镁盐（阿拉伯糖、半乳糖、甘露糖、葡萄糖醛酸） | 固 | — |  | 第四批 270 号；原料胶块为 `materials/ghatti_lump`；表 B.2 N347 | Gum Ghatti | 化学品 | — | K6 |
| `shellac` | 紫胶（虫胶） | 天然树脂，紫胶桐酸 C16H32O5、紫胶酸 C15H20O6 等羟基酸的聚酯 | 固 | — |  | 第四批 271 号 | Shellac | 化学品 | — | K5 |
| `liquid_paraffin` | 液体石蜡（白油） | 饱和烷烃混合物 CnH2n+2 | 液 | — |  | 第四批 272 号 | Liquid Paraffin (White Mineral Oil) | 化学品 | #F2F4F2 | K7 |
| `morpholine_fruit_wax` | 吗啉脂肪酸盐果蜡 | 巴西棕榈蜡或紫胶与油酸吗啉盐的水乳液 | 液 | — |  | 第四批 273 号；复配乳液，配比未确认 | Morpholine Fatty Acid Salt Fruit Wax | 化学品 | #E8E0C8 | K12 |
| `rosin_pentaerythritol_ester` | 松香季戊四醇酯 | 松香酸与季戊四醇的酯混合物 | 固 | — |  | 第四批 274 号 | Pentaerythritol Ester of Rosin | 化学品 | — | K5 |
| `carnauba_wax` | 巴西棕榈蜡 | C26–C30 脂肪酸与长链醇的酯为主的蜡混合物 | 固 | — |  | 第四批 275 号 | Carnauba Wax | 化学品 | — | K7 |
| `polyvinyl_alcohol` | 聚乙烯醇 | `(C2H4O)n` | 固 | — |  | 第四批 277 号 | Polyvinyl Alcohol | 化学品 | — | K3 |
| `pullulan` | 普鲁兰多糖 | `(C6H10O5)n` | 固 | — |  | 第四批 278 号；α-1,4 / α-1,6 葡聚糖 | Pullulan | 化学品 | — | K6 |
| `polyethylene_glycol` | 聚乙二醇 | `(C2H4O)n` | 固 | — |  | 第四批 279 号；即 H(OCH2CH2)nOH；低分子量为液态 | Polyethylene Glycol | 化学品 | — | K3 |
| `karaya_gum` | 刺梧桐胶 | 乙酰化酸性多糖（半乳糖、鼠李糖、半乳糖醛酸） | 固 | — |  | 第四批 283 号；原料胶块为 `materials/karaya_lump`；表 B.2 N365 | Karaya Gum (Sterculia Gum) | 化学品 | — | K6 |
| `potassium_polyaspartate` | 聚天冬氨酸钾 | `(C4H4KNO3)n` | 固 | — |  | 第四批 286 号 | Potassium Polyaspartate | 化学品 | — | K3 |
| `potassium_alginate` | 海藻酸钾 | `(C6H7KO6)n` | 固 | — |  | 第四批 287 号 | Potassium Alginate | 化学品 | — | K6 |
| `gum_arabic` | 阿拉伯胶 | 阿拉伯半乳聚糖–蛋白复合物 | 固 | — |  | 第四批 288 号；原料胶块为 `materials/acacia_lump`；表 B.2 N249 | Gum Arabic | 化学品 | — | K6 |
| `propylene_glycol_alginate` | 海藻酸丙二醇酯 | 海藻酸部分丙二醇酯，酯化单元 C9H14O7 | 固 | — |  | 第四批 289 号 | Propylene Glycol Alginate | 化学品 | — | K6 |
| `tamarind_gum` | 罗望子多糖胶 | 木葡聚糖 | 固 | — |  | 第四批 290 号 | Tamarind Seed Polysaccharide | 化学品 | — | K6 |
| `sodium_carboxymethyl_starch` | 羧甲基淀粉钠 | `C8H11NaO7` | 固 | — |  | 第四批 291 号；以取代度 1 的葡萄糖单元计（同 `cmc_unit`） | Sodium Carboxymethyl Starch | 化学品 | — | K6 |
| `sodium_starch_phosphate` | 淀粉磷酸酯钠 | 变性淀粉，取代单元 C6H10NaO8P | 固 | — |  | 第四批 292 号 | Sodium Starch Phosphate | 化学品 | — | K6 |
| `acetylated_distarch_phosphate` | 乙酰化二淀粉磷酸酯 | 磷酸交联并乙酰化的淀粉 | 固 | — |  | 第四批 293 号 | Acetylated Distarch Phosphate | 化学品 | — | K6 |
| `hydroxypropyl_distarch_phosphate` | 羟丙基二淀粉磷酸酯 | 羟丙基醚化并磷酸交联的淀粉 | 固 | — |  | 第四批 294 号 | Hydroxypropyl Distarch Phosphate | 化学品 | — | K6 |
| `phosphated_distarch_phosphate` | 磷酸化二淀粉磷酸酯 | 磷酸交联后再磷酸酯化的淀粉 | 固 | — |  | 第四批 295 号 | Phosphated Distarch Phosphate | 化学品 | — | K6 |
| `chitin` | 甲壳素（几丁质） | `(C8H13NO5)n` | 固 | — |  | 第四批 296 号 | Chitin | 化学品 | — | K6 |
| `flaxseed_gum` | 亚麻籽胶 | 种皮多糖（阿拉伯木聚糖、鼠李半乳糖醛酸聚糖） | 固 | — |  | 第四批 297 号 | Flaxseed Gum | 化学品 | — | K6 |
| `sesbania_gum` | 田菁胶 | 半乳甘露聚糖 | 固 | — |  | 第四批 298 号 | Sesbania Gum | 化学品 | — | K6 |
| `polydextrose` | 聚葡萄糖 | 葡萄糖随机缩聚物（含少量山梨糖醇、柠檬酸），单元 C6H10O5 | 固 | — |  | 第四批 299 号；合成高分子 | Polydextrose | 化学品 | — | K3 |
| `locust_bean_gum` | 槐豆胶 | 半乳甘露聚糖，甘露糖∶半乳糖约 4∶1 | 固 | — |  | 第四批 300 号 | Locust Bean Gum | 化学品 | — | K6 |
| `guar_gum` | 瓜尔胶 | 半乳甘露聚糖，甘露糖∶半乳糖约 2∶1 | 固 | — |  | 第四批 302 号 | Guar Gum | 化学品 | — | K6 |
| `chitosan` | 脱乙酰甲壳素（壳聚糖） | `(C6H11NO4)n` | 固 | — |  | 第四批 303 号 | Deacetylated Chitin (Chitosan) | 化学品 | — | K6 |
| `hydroxypropyl_methylcellulose` | 羟丙基甲基纤维素 | 纤维素甲基、羟丙基混合醚 | 固 | — |  | 第四批 304 号 | Hydroxypropyl Methylcellulose | 化学品 | — | K6 |
| `gleditsia_gum` | 皂荚糖胶 | 半乳甘露聚糖 | 固 | — |  | 第四批 305 号 | Gleditsia Gum | 化学品 | — | K6 |
| `oxidized_starch` | 氧化淀粉 | 部分羟基氧化为羧基、羰基的淀粉 | 固 | — |  | 第四批 306 号 | Oxidized Starch | 化学品 | — | K6 |
| `acid_treated_starch` | 酸处理淀粉 | 糖苷键部分水解的淀粉 | 固 | — |  | 第二批 46 号；I19 补（2026-11-11） | Acid-Treated Starch | 化学品 | — | K6 |
| `hydroxypropyl_starch` | 羟丙基淀粉 | 部分羟基醚化为羟丙基的淀粉 | 固 | — |  | 第二批 47 号；I19 补（2026-11-11） | Hydroxypropyl Starch | 化学品 | — | K6 |
| `starch_acetate` | 醋酸酯淀粉 | 部分羟基乙酰化的淀粉 | 固 | — |  | 第二批 48 号；I19 补（2026-11-11） | Starch Acetate | 化学品 | — | K6 |
| `acetylated_distarch_adipate` | 乙酰化双淀粉己二酸酯 | 己二酸交联并乙酰化的淀粉 | 固 | — |  | 第四批 307 号 | Acetylated Distarch Adipate | 化学品 | — | K6 |
| `oxidized_hydroxypropyl_starch` | 氧化羟丙基淀粉 | 次氯酸钠氧化的羟丙基淀粉 | 固 | — |  | 第四批 308 号 | Oxidized Hydroxypropyl Starch | 化学品 | — | K6 |
| `distarch_phosphate` | 磷酸酯双淀粉 | 三偏磷酸钠交联的淀粉 | 固 | — |  | 第四批 309 号 | Distarch Phosphate | 化学品 | — | K6 |
| `sodium_polyacrylate` | 聚丙烯酸钠 | `(C3H3NaO2)n` | 固 | — |  | 第四批 310 号 | Sodium Polyacrylate | 化学品 | — | K3 |
| `artemisia_gum` | 沙蒿胶 | 沙蒿籽种皮多糖 | 固 | — |  | 第四批 311 号 | Artemisia Seed Gum | 化学品 | — | K6 |
| `tara_gum` | 刺云实胶 | 半乳甘露聚糖，甘露糖∶半乳糖约 3∶1 | 固 | — |  | 第四批 312 号 | Tara Gum | 化学品 | — | K6 |
| `curdlan` | 可得然胶 | `(C6H10O5)n` | 固 | — |  | 第四批 313 号；β-1,3-葡聚糖 | Curdlan | 化学品 | — | K6 |
| `methylcellulose` | 甲基纤维素 | `C7H12O5` | 固 | — |  | 第四批 314 号；以取代度 1 的葡萄糖单元计；表 B.3 S1076 | Methylcellulose | 化学品 | — | K6 |
| `soluble_soybean_polysaccharide` | 可溶性大豆多糖 | 果胶类酸性多糖（半乳糖醛酸、半乳糖、阿拉伯糖） | 固 | — |  | 第四批 315 号 | Soluble Soybean Polysaccharide | 化学品 | — | K6 |
| `cassia_gum` | 决明胶 | 半乳甘露聚糖，甘露糖∶半乳糖约 5∶1 | 固 | — |  | 第四批 316 号 | Cassia Gum | 化学品 | — | K6 |
| `sanzan_gum` | 三赞胶 | 鞘氨醇单胞菌胞外多糖 | 固 | — |  | 第四批 318 号 | Sanzan Gum | 化学品 | — | K6 |
| `licorice_antioxidant` | 甘草抗氧化物 | 甘草黄酮为主（光甘草定 C20H20O4 等） | 固 | — |  | 第四批 321 号 | Licorice Antioxidant Extract | 化学品 | — | K5 |
| `bamboo_leaf_antioxidant` | 竹叶抗氧化物 | 竹叶黄酮（碳苷黄酮）、内酯、酚酸 | 固 | — |  | 第四批 327 号 | Bamboo Leaf Antioxidant Extract | 化学品 | — | K5 |
| `theaflavins` | 茶黄素 | 茶黄素类混合物，以茶黄素 C29H24O12 计 | 固 | — |  | 第四批 329 号 | Theaflavins | 化学品 | — | K5 |
| `lysozyme` | 溶菌酶 | 蛋白质（酶），129 个氨基酸残基的单链多肽 | 固 | — |  | 第四批 346 号；酶，按酶活计价，不带纯度（2026-11-05）；同时打 `enzymes/` 标签 | Lysozyme | 化学品 | — | — |
| `epsilon_polylysine` | ε-聚赖氨酸 | `(C6H12N2O)n` | 固 | — |  | 第四批 348 号 | ε-Polylysine | 化学品 | — | K5 |
| `epsilon_polylysine_hydrochloride` | ε-聚赖氨酸盐酸盐 | `(C6H13ClN2O)n` | 固 | — |  | 第四批 349 号 | ε-Polylysine Hydrochloride | 化学品 | — | K5 |
| `mogroside` | 罗汉果甜苷 | 罗汉果苷类，以罗汉果苷 V C60H102O29 计 | 固 | — |  | 第四批 356 号 | Mogrosides | 化学品 | — | K5 |
| `thaumatin` | 索马甜 | 甜味蛋白质（207 个氨基酸残基） | 固 | — |  | 第四批 357 号 | Thaumatin | 化学品 | — | K5 |
| `maltitol_syrup` | 麦芽糖醇液 | 氢化糖浆，麦芽糖醇 C12H24O11 为主，含山梨糖醇与氢化低聚糖 | 溶液 | — |  | 第四批 359 号；同物质另一形态（流体）；界限见 10 §1.5 | Maltitol Syrup | 化学品 | #F0ECD4 | K2 |
| `lactulose_syrup` | 异构化乳糖 | 乳果糖 C12H22O11 为主，含乳糖、半乳糖的糖浆 | 溶液 | — |  | 第四批 365 号；界限见 10 §1.5 | Lactulose Syrup | 化学品 | #E8D8A0 | K2 |
| `paprika_oleoresin` | 辣椒油树脂 | 辣椒红素 C40H56O3、辣椒素 C18H27NO3 并存的溶剂提取物 | 液 | — |  | 第四批 367 号；60 B 路线中间产物，09 未注册；辣椒橙（08.107）原料；表 B.2 N143；2026-11-12 改为不带纯度的混合物（油树脂整体使用，11 问题 42） | Paprika Oleoresin | 化学品 | #B02010 | — |
| `galactomannan` | 半乳甘露聚糖 | 低聚合度半乳甘露聚糖（甘露糖主链、半乳糖支链） | 固 | — |  | 第四批 369 号 | Galactomannan | 化学品 | — | K6 |
| `oxystearin` | 羟基硬脂精 | 部分氧化的硬脂酸等甘油酯混合物 | 固 | — |  | 第四批 370 号 | Oxystearin | 化学品 | — | K7 |
| `ice_structuring_protein` | 冰结构蛋白 | 蛋白质（III 型冰结构蛋白，66 个氨基酸残基） | 溶液 | — |  | 第四批 371 号；液体浓缩物，冷冻储存；界限见 10 §1.5 | Ice Structuring Protein | 化学品 | #E8EEF0 | K5 |
| `amaranth_lake` | 苋菜红铝色淀 | 苋菜红吸附于氢氧化铝（以苋菜红计） | 固 | — |  | P71；194 号的铝色淀（2026-11-05 拆分） | Amaranth Aluminium Lake | 化学品 | — | K10 |
| `ponceau_4r_lake` | 胭脂红铝色淀 | 胭脂红吸附于氢氧化铝（以胭脂红计） | 固 | — |  | P71；81 号的铝色淀（2026-11-05 拆分） | Ponceau 4R Aluminium Lake | 化学品 | — | K10 |
| `erythrosine_lake` | 赤藓红铝色淀 | 赤藓红吸附于氢氧化铝（以赤藓红计） | 固 | — |  | P71；195 号的铝色淀（2026-11-05 拆分） | Erythrosine Aluminium Lake | 化学品 | — | K10 |
| `new_red_lake` | 新红铝色淀 | 新红吸附于氢氧化铝（以新红计） | 固 | — |  | P71；196 号的铝色淀（2026-11-05 拆分） | New Red Aluminium Lake | 化学品 | — | K10 |
| `tartrazine_lake` | 柠檬黄铝色淀 | 柠檬黄吸附于氢氧化铝（以柠檬黄计） | 固 | — |  | P71；79 号的铝色淀（2026-11-05 拆分） | Tartrazine Aluminium Lake | 化学品 | — | K10 |
| `sunset_yellow_lake` | 日落黄铝色淀 | 日落黄吸附于氢氧化铝（以日落黄计） | 固 | — |  | P71；80 号的铝色淀（2026-11-05 拆分） | Sunset Yellow Aluminium Lake | 化学品 | — | K10 |
| `brilliant_blue_lake` | 亮蓝铝色淀 | 亮蓝吸附于氢氧化铝（以亮蓝计） | 固 | — |  | P71；83 号的铝色淀（2026-11-05 拆分） | Brilliant Blue Aluminium Lake | 化学品 | — | K10 |
| `indigo_carmine_lake` | 靛蓝铝色淀 | 靛蓝吸附于氢氧化铝（以靛蓝计） | 固 | — |  | P71；84 号的铝色淀（2026-11-05 拆分） | Indigo Carmine Aluminium Lake | 化学品 | — | K10 |
| `allura_red_lake` | 诱惑红铝色淀 | 诱惑红吸附于氢氧化铝（以诱惑红计） | 固 | — |  | P71；82 号的铝色淀（2026-11-05 拆分） | Allura Red Aluminium Lake | 化学品 | — | K10 |
| `quinoline_yellow_lake` | 喹啉黄铝色淀 | 喹啉黄吸附于氢氧化铝（以喹啉黄计） | 固 | — |  | P71；202 号的铝色淀（2026-11-05 拆分） | Quinoline Yellow Aluminium Lake | 化学品 | — | K10 |
| `carmine` | 胭脂虫红铝色淀 | 洋红酸吸附于氢氧化铝（以洋红酸计） | 固 | — |  | P71；233 号的铝色淀（2026-11-05 拆分） | Carmine | 化学品 | — | K5 |
| `turmeric` | 姜黄 | 姜黄根茎粉，含姜黄素类（以姜黄素计） | 固 | — |  | 第四批 205 号；07 第 61 号（2026-11-05 由姜黄素拆出） | Turmeric | 化学品 | — | K5 |
| `natural_carotene` | 天然胡萝卜素 | 胡萝卜等的油树脂，以 β-胡萝卜素计 | 液 | — |  | 第四批 235 号（2026-11-05 拆分） | Natural Carotene | 化学品 | #E06010 | K5 |
| `tomato_oleoresin` | 番茄红 | 番茄皮渣油树脂，以番茄红素计 | 液 | — |  | 第四批 238 号（2026-11-05 拆分） | Tomato Oleoresin | 化学品 | #B81818 | K5 |
| `animal_fat` | 动物脂 | 棕榈酸、硬脂酸、油酸甘油三酯为主，反应式以硬脂酸甘油三酯 C57H110O6 计 | 固 | — |  | 27 熬制；2026-11-05 由 `tristearin` 改为混合油脂；多组分混合物，不带纯度（2026-11-05） | Animal Fat | 化学品 | — | — |
| `tallow_fatty_acids` | 动物脂脂肪酸 | 棕榈酸、硬脂酸、油酸混合物 | 固 | — |  | 第四批：276（压榨分出油酸，精馏分出棕榈酸、硬脂酸）、246、248；多组分混合物，不带纯度（2026-11-05） | Tallow Fatty Acids | 化学品 | — | — |
| `phosphorus_slag` | 磷炉渣 | CaSiO3 为主，含 CaF2、P2O5 | 固 | — |  | 10 号 C 热法磷酸的 M26 副产（2026-11-05 由 `calcium_silicate` 拆出）；副产炉渣，不带纯度（2026-11-05） | Phosphorus Furnace Slag | 化学品 | — | — |
| `chlorination_charge` | 氯化炉料 | 高钛渣与焦炭粉的混合料 | 固 | — |  | 第四批：198 B，在气相反应炉流化床模式中通氯气氯化（2026-11-11，用户定：按现实的流化床氯化）；多组分混合物，不带纯度 | Chlorination Charge | 化学品 | — | — |
| `soy_protein_hydrolysate` | 大豆蛋白水解物 | 豆渣蛋白经蛋白酶水解所得的肽与氨基酸水溶液 | 溶液 | — |  | 第四批：218 栀子蓝显色用的氨基来源（2026-11-11，用户定：按现实制法，见 15 依据）；多组分混合物，不带纯度 | Soy Protein Hydrolysate | 化学品 | #D8C080 | — |
| `polydimethylsiloxane_emulsion` | 聚二甲基硅氧烷乳液（硅油乳液） | 硅油经司盘 60 乳化分散于水，以聚二甲基硅氧烷计 | 液 | — |  | 第四批 163 号（2026-11-11，用户定：乳液单设物品）；P83 复配 | Polydimethylsiloxane Emulsion | 化学品 | #F4F6F8 | K12 |
| `hardened_oil` | 氢化油（硬化油） | 植物油镍催化加氢所得，以硬脂酸甘油三酯 C57H110O6 计 | 固 | — |  | 第四批：265、276 B、370 的“氢化油”（2026-11-11 实装补登，待审）；多组分混合物，不带纯度 | Hardened Oil | 化学品 | — | — |
| `stillingia_oil` | 梓油（乌桕籽仁油） | 甘油三酯混合物，含 2,4-癸二烯酸酯基 | 液 | — |  | 第五批：S0482 | Stillingia Oil | 化学品 | — | — |
| `common_jasmine_concrete` | 素方花浸膏 | 蜡与香气成分（乙酸苄酯 C9H10O2、芳樟醇 C10H18O） | 固 | — |  | 第五批：N124 | Common Jasmine Concrete | 化学品 | — | — |
| `crude_bitter_almond_oil` | 苦杏仁粗油 | 主成分 苯甲醛 C7H6O，含氢氰酸 | 液 | — |  | 第五批：N182 蒸馏所得、未除氢氰酸的油，在浸提桶用熟石灰、硫酸亚铁处理后为苦杏仁油（蒸馏甑只有一个物品输入，2026-11-12，用户定：缺物品槽的先合成或混合） | Crude Bitter Almond Oil | 化学品 | — | — |
| `crude_camphor_oil` | 樟脑原油 | 主成分 d-樟脑 C10H16O、1,8-桉叶素 C10H18O | 液 | — |  | 第五批：N165、N220 | Crude Camphor Oil | 化学品 | — | — |
| `cane_molasses` | 甘蔗糖蜜 | 蔗糖、转化糖、灰分与水的浓糖液 | 液 | — |  | 第五批：N276；原 `molasses`，2026-11-12 改名，两种糖蜜都可用的配方引用流体标签 `foodindustrial:molasses`（08 §B，11 问题 42） | Cane Molasses | 化学品 | — | — |
| `crude_fusel_oil` | 粗杂醇油 | 异戊醇、2-甲基-1-丁醇、异丁醇、正丙醇与水的混合物 | 液 | — |  | 第五批：N402（128 可共用） | Crude Fusel Oil | 化学品 | — | — |
| `hydrogenated_rosin` | 氢化松香 | 树脂酸混合物，主成分二氢松香酸 C20H32O2 | 固 | — |  | 第五批：426、574、575、576 | Hydrogenated Rosin | 化学品 | — | — |
| `wood_rosin` | 木松香 | 树脂酸混合物，主成分松香酸 C20H30O2 | 固 | — |  | 第五批：427、579 | Wood Rosin | 化学品 | — | — |
| `partially_dimerized_rosin` | 部分二聚松香 | 松香酸二聚体与未聚合树脂酸的混合物，二聚体以 C40H60O4 计 | 固 | — |  | 第五批：573、579 | Partially Dimerized Rosin | 化学品 | — | — |
| `polymerized_rosin` | 聚合松香 | 松香酸二聚体为主，以 C40H60O4 计 | 固 | — |  | 第五批：579 | Polymerized Rosin | 化学品 | — | — |
| `molten_partially_dimerized_rosin` | 熔融部分二聚松香 | 部分二聚松香的熔融液，二聚体以 C40H60O4 计 | 液 | — |  | 第五批：579 精馏塔减压蒸馏的进料（精馏塔不收物品，先熔化，2026-11-12，用户定：缺物品槽的先合成或混合） | Molten Partially Dimerized Rosin | 化学品 | — | — |
| `c4_fraction` | 碳四馏分 | 1,3-丁二烯、正丁烯、异丁烯、丁烷的混合物 | 气 | — |  | 第五批：569、570；由 `naphtha_cracked_gas` 分离（2026-11-12） | C4 Fraction | 化学品 | — | — |
| `c4_raffinate` | 抽余碳四 | 正丁烯、异丁烯、丁烷的混合物 | 气 | — |  | 第五批：570 | C4 Raffinate | 化学品 | — | — |
| `light_naphtha` | 轻石脑油 | C5–C6 烷烃混合物，含少量苯与硫化物 | 液 | — |  | 第五批：428、432、433、`naphtha_cracked_gas`；581 费-托合成的轻油也产出本物料 | Light Naphtha | 化学品 | — | — |
| `hydrotreated_naphtha` | 加氢轻石脑油 | C5–C6 烷烃混合物，苯已加氢为环己烷 | 液 | — |  | 第五批：432、433、437 | Hydrotreated Light Naphtha | 化学品 | — | — |
| `cobalamin_broth` | 钴胺素发酵液 | 5'-脱氧腺苷钴胺、甲钴胺、羟钴胺的混合发酵液，含菌体 | 溶液 | — |  | 第五批：474、475、476 | Cobalamin Fermentation Broth | 化学品 | — | — |
| `evening_primrose_oil` | 月见草油 | 亚油酸（约 70%）、γ-亚麻酸（约 9%）甘油三酯为主 | 液 | — |  | 第五批：538 | Evening Primrose Oil | 化学品 | — | — |
| `beet_molasses` | 甜菜糖蜜 | 蔗糖约 50%、棉子糖 1%–2%、甜菜碱、无机盐的浓糖液 | 液 | — |  | 第五批：550、N351 | Beet Molasses | 化学品 | — | — |
| `nucleotide_hydrolysate` | 5'-核苷酸酶解液 | 5'-腺苷酸、鸟苷酸、胞苷酸、尿苷酸的混合水溶液 | 溶液 | — |  | 第五批：551、552（73 共用） | 5'-Nucleotide Hydrolysate | 化学品 | — | — |
| `helichrysum_concrete` | 蜡菊浸膏 | 主成分乙酸橙花酯 C12H20O2；含植物蜡 | 固 | — |  | 第五批：N275 | Helichrysum Concrete | 香料 | — | — |
| `ambrette_seed_concrete` | 黄葵籽浸膏 | 主成分黄葵内酯 C16H28O2；含植物蜡 | 固 | — |  | 第五批：N282 | Ambrette Seed Concrete | 香料 | — | — |
| `hungarian_chamomile_concrete` | 母菊花浸膏 | 主成分 α-红没药醇氧化物 A C15H26O2，含烯炔双环醚；含植物蜡 | 固 | — |  | 第五批：N309 | Hungarian Chamomile Concrete | 香料 | — | — |
| `algues_concrete` | 海藻浸膏 | 褐藻浸膏的乙醇可溶部分（脂肪酸、甾醇、溴酚类）；含植物蜡 | 固 | — |  | 第五批：N330 | Algues Concrete | 香料 | — | — |
| `peach_leaf_concrete` | 桃树叶浸膏 | 主成分苯甲醛 C7H6O；含植物蜡 | 固 | — |  | 第五批：N336 | Peach Leaf Concrete | 香料 | — | — |
| `lavender_concrete` | 薰衣草浸膏 | 主成分乙酸芳樟酯 C12H20O2、芳樟醇 C10H18O；含植物蜡 | 固 | — |  | 第五批：N340 | Lavender Concrete | 香料 | — | — |
| `oakmoss_concrete` | 橡苔浸膏 | 主成分 β-苔色酸甲酯 C10H12O4，含地衣酸类；含植物蜡 | 固 | — |  | 第五批：N353 | Oakmoss Concrete | 香料 | — | — |
| `civet_concrete` | 灵猫浸膏 | 主成分灵猫酮 C17H30O，含粪臭素 | 固 | — |  | 第五批：N361 | Civet Concrete | 香料 | — | — |
| `rum_concrete` | 朗姆酒浸膏 | 朗姆酒香气浓缩物（酯类、高级醇、β-大马酮） | 固 | — |  | 第五批：N380 | Rum Concrete | 香料 | — | — |
| `soybean_fatty_acids` | 大豆油脂肪酸 | 亚油酸 C18H32O2、油酸、棕榈酸等的混合物 | 液 | — |  | 第五批：S0880 | Soybean Oil Fatty Acids | 化学品 | — | — |
| `starter_culture_broth` | 发酵起子培养液 | 乳酸菌发酵乳，含 2,3-丁二酮 C4H6O2 | 液 | — |  | 第五批：N159 | Starter Culture Broth | 化学品 | — | — |
| `cedarwood_oil` | 柏木油 | 主成分 α-柏木烯 C15H24、柏木脑 C15H26O | 液 | — |  | 第五批：N321、`cedrol` | Cedarwood Oil | 香料 | — | — |
| `tea_polyphenol_palmitate` | 茶多酚棕榈酸酯 | 茶多酚棕榈酸酯混合物，以表没食子儿茶素没食子酸酯单棕榈酸酯 C38H48O12 计 | 固 | — |  | 第五批 424 号 | Tea Polyphenol Palmitate | 化学品 | — | K5 |
| `hydrogenated_rosin_glycerol_ester` | 氢化松香甘油酯 | 氢化松香树脂酸与丙三醇的酯混合物 | 固 | — |  | 第五批 426 号 | Glycerol Ester of Hydrogenated Rosin | 化学品 | — | K5 |
| `wood_rosin_glycerol_ester` | 木松香甘油酯 | 木松香树脂酸与丙三醇的酯混合物 | 固 | — |  | 第五批 427 号 | Glycerol Ester of Wood Rosin | 化学品 | — | K5 |
| `vegetable_oil_extraction_solvent` | 植物油抽提溶剂 | C6 烷烃混合物，主成分正己烷 C6H14、甲基戊烷、甲基环戊烷 | 液 | — |  | 第五批 432 号 | Vegetable Oil Extraction Solvent | 化学品 | — | — |
| `petroleum_ether` | 石油醚 | C5–C6 烷烃混合物，主成分正戊烷 C5H12、异戊烷、正己烷 | 液 | — |  | 第五批 433 号 | Petroleum Ether | 化学品 | — | — |
| `attapulgite_clay` | 凹凸棒黏土 | 坡缕石 (Mg,Al)2Si4O10(OH)·4H2O 为主 | 固 | — |  | 第五批 438 号；也作吸附、助滤耗材（08 催化剂与助剂） | Attapulgite Clay | 催化剂 | — | — |
| `pvpp` | 不溶性聚乙烯聚吡咯烷酮 | `(C6H9NO)n` | 固 | — |  | 第五批 439 号；也作吸附、助滤耗材（08 催化剂与助剂） | Insoluble Polyvinylpolypyrrolidone (PVPP) | 催化剂 | — | — |
| `activated_clay` | 活性白土 | 酸活化蒙脱石，SiO2 为主，含 Al2O3 | 固 | — |  | 第五批 440 号；也作吸附、助滤耗材（08 催化剂与助剂） | Activated Clay | 催化剂 | — | — |
| `polystyrene` | 聚苯乙烯 | `(C8H8)n` | 固 | — |  | 第五批 441 号；也作吸附、助滤耗材（08 催化剂与助剂） | Polystyrene | 催化剂 | — | — |
| `polyacrylamide` | 聚丙烯酰胺 | `(C3H5NO)n` | 固 | — |  | 第五批 442 号 | Polyacrylamide | 化学品 | — | K3 |
| `bentonite` | 膨润土 | 蒙脱石 (Na,Ca)0.33(Al,Mg)2Si4O10(OH)2·nH2O 为主 | 固 | — |  | 第五批 443 号；也作吸附、助滤耗材（08 催化剂与助剂） | Bentonite | 催化剂 | — | — |
| `perlite` | 珍珠岩 | 火山玻璃，SiO2 约 70%–75%、Al2O3 约 12%–15% | 固 | — |  | 第五批 444 号；也作吸附、助滤耗材（08 催化剂与助剂） | Perlite | 催化剂 | — | — |
| `higher_alcohol_fatty_ester_complex` | 高碳醇脂肪酸酯复合物 | 高碳醇（以 1-十八醇 C18H38O 计）、脂肪酸酯与乳化剂的乳液 | 液 | — |  | 第五批 445 号 | Higher Alcohol Fatty Acid Ester Complex | 化学品 | — | K12 |
| `gp_polyether` | 聚氧丙烯甘油醚 | C3H5[O(C3H6O)nH]3，总聚合度 > 16；以 51 计 C156H314O54 | 液 | — |  | 第五批 446 号 | Polyoxypropylene Glycerol Ether (GP) | 化学品 | — | K3 |
| `gpe_polyether` | 聚氧丙烯氧化乙烯甘油醚 | GP 链端接聚氧乙烯；以 GP（51）+ 6 EO 计 C168H338O60 | 液 | — |  | 第五批 447 号 | Polyoxypropylene Oxyethylene Glycerol Ether (GPE) | 化学品 | — | K3 |
| `bape_polyether` | 聚氧乙烯聚氧丙烯胺醚 | 胺起始的环氧丙烷–环氧乙烷嵌段聚醚；以乙二胺 + 40 PO + 8 EO 计 C138H280N2O48 | 液 | — |  | 第五批 448 号 | Polyoxyethylene Polyoxypropylene Amine Ether (BAPE) | 化学品 | — | K3 |
| `ppe_polyether` | 聚氧乙烯聚氧丙烯季戊四醇醚 | 季戊四醇起始的环氧丙烷–环氧乙烷嵌段聚醚；以 48 PO + 12 EO 计 C173H348O64 | 液 | — |  | 第五批 449 号 | Polyoxyethylene Polyoxypropylene Pentaerythritol Ether (PPE) | 化学品 | — | K3 |
| `sucrose_polyoxypropylene_ether` | 蔗糖聚氧丙烯醚 | 蔗糖起始的聚氧丙烯醚；以 16 PO 计 C60H118O27 | 液 | — |  | 第五批 450 号 | Sucrose Polyoxypropylene Ether | 化学品 | — | K3 |
| `bone_powder` | 骨粉 | 主成分羟基磷灰石 Ca5(PO4)3OH，含胶原蛋白 | 固 | — |  | 第五批 501 号 | Bone Powder | 化学品 | — | — |
| `zinc_glycinate` | 甘氨酸锌 | `C4H8N2O4Zn` | 固 | — | ✓ | 第五批 502 号 | Zinc Glycinate | 化学品 | — | K3 |
| `zinc_lactate` | 乳酸锌 | `C6H10O6Zn·3H2O` | 固 | — | ✓ | 第五批 504 号 | Zinc Lactate | 化学品 | — | K3 |
| `zinc_acetate` | 乙酸锌 | `C4H6O4Zn·2H2O` | 固 | — | ✓ | 第五批 507 号 | Zinc Acetate | 化学品 | — | K3 |
| `selenoprotein` | 硒蛋白 | 富含硒代蛋氨酸的大豆蛋白 | 固 | — |  | 第五批 511 号 | Selenoprotein | 化学品 | — | K5 |
| `selenium_mushroom_powder` | 富硒食用菌粉 | 食用菌菌体，硒以有机硒为主 | 固 | — |  | 第五批 512 号 | Selenium-Enriched Edible Fungi Powder | 化学品 | — | — |
| `selenium_carrageenan` | 硒化卡拉胶 | κ-卡拉胶中部分硫酸酯基被亚硒酸酯基取代 | 固 | — |  | 第五批 514 号 | Selenium Carrageenan | 化学品 | — | K6 |
| `selenium_yeast` | 富硒酵母 | 酿酒酵母菌体，硒主要以硒代蛋氨酸存在 | 固 | — |  | 第五批 515 号 | Selenium-Enriched Yeast | 化学品 | — | — |
| `fructooligosaccharides` | 低聚果糖 | 以蔗果三糖 C18H32O16 计（GFn、Fn 混合，聚合度 2–8） | 固 | — |  | 第五批 539 号 | Fructooligosaccharides (Chicory) | 化学品 | — | K2 |
| `arachidonic_acid_oil` | 花生四烯酸油脂 | 主成分花生四烯酸甘油三酯，以三花生四烯酸甘油酯 C63H98O6 计 | 液 | — |  | 第五批 541 号 | Arachidonic Acid Oil (Mortierella Alpina) | 化学品 | — | K7 |
| `lactoferrin` | 乳铁蛋白 | 糖蛋白（约 690 个氨基酸残基，每分子结合 2 个 Fe3+） | 固 | — |  | 第五批 542 号 | Lactoferrin | 化学品 | — | K5 |
| `casein_calcium_peptide` | 酪蛋白钙肽 | 酪蛋白磷酸肽与 Ca2+ 的结合物（含 SerP-SerP-SerP-Glu-Glu 序列） | 固 | — |  | 第五批 543 号 | Casein Calcium Peptide | 化学品 | — | K5 |
| `casein_phosphopeptide` | 酪蛋白磷酸肽 | 酪蛋白胰蛋白酶水解所得磷酸化肽混合物 | 固 | — |  | 第五批 544 号 | Casein Phosphopeptide | 化学品 | — | K5 |
| `galactooligosaccharides` | 低聚半乳糖 | 以 4'-半乳糖基乳糖 C18H32O16 计（聚合度 2–8） | 固 | — |  | 第五批 548 号 | Galactooligosaccharides (From Lactose) | 化学品 | — | K2 |
| `inulin` | 多聚果糖 | (C6H10O5)n，以 GFn 计（聚合度 2–60） | 固 | — |  | 第五批 549 号 | Inulin (Chicory) | 化学品 | — | K6 |
| `yeast_beta_glucan` | 酵母 β-葡聚糖 | (C6H10O5)n（β-1,3 主链、β-1,6 支链） | 固 | — |  | 第五批 560 号 | Yeast β-Glucan | 化学品 | — | K6 |
| `massaranduba_balata` | 巴拉塔树胶 | 反式-1,4-聚异戊二烯 (C5H8)n 与树脂的混合物 | 固 | — |  | 第五批 564 号 | Massaranduba Balata | 化学品 | — | K6 |
| `jelutong` | 节路顿胶 | 树脂与反式聚异戊二烯 (C5H8)n（干基约 20%）的混合物 | 固 | — |  | 第五批 565 号 | Jelutong | 化学品 | — | K6 |
| `leche_caspi` | 来开欧胶 | 香树素乙酸酯 C32H52O2 与聚异戊二烯 (C5H8)n 为主的混合物 | 固 | — |  | 第五批 566 号 | Leche Caspi (Sorva) | 化学品 | — | K6 |
| `chicle` | 糖胶树胶 | 反式聚异戊二烯 (C5H8)n 与树脂的混合物 | 固 | — |  | 第五批 568、567 号 | Chicle | 化学品 | — | K6 |
| `styrene_butadiene_rubber` | 丁二烯-苯乙烯 75/25、50/50 橡胶（丁苯橡胶） | `(C4H6)n(C8H8)m` | 固 | — |  | 第五批 569 号 | Butadiene-Styrene Rubber 75/25, 50/50 (SBR) | 化学品 | — | K3 |
| `polybutene` | 聚丁烯 | `(C4H8)n` | 液 | — |  | 第五批 570 号 | Polybutene | 化学品 | — | K3 |
| `polyethylene` | 聚乙烯 | `(C2H4)n` | 固 | — |  | 第五批 571 号 | Polyethylene | 化学品 | — | K3 |
| `butyl_rubber` | 异丁烯-异戊二烯共聚物（丁基橡胶） | `(C4H8)n(C5H8)m` | 固 | — |  | 第五批 572 号 | Isobutylene-Isoprene Copolymer (Butyl Rubber) | 化学品 | — | K3 |
| `dimerized_rosin_glycerol_ester` | 部分二聚松香甘油酯 | 部分二聚松香与丙三醇的酯混合物 | 固 | — |  | 第五批 573 号 | Glycerol Ester of Partially Dimerized Rosin | 化学品 | — | K5 |
| `hydrogenated_rosin_pentaerythritol_ester` | 部分氢化松香季戊四醇酯 | 氢化松香树脂酸与季戊四醇的酯混合物 | 固 | — |  | 第五批 575 号 | Pentaerythritol Ester of Partially Hydrogenated Rosin | 化学品 | — | K5 |
| `hydrogenated_rosin_methyl_ester` | 部分氢化松香甲酯 | 以二氢松香酸甲酯 C21H34O2 为主的混合物 | 液 | — |  | 第五批 576 号 | Methyl Ester of Partially Hydrogenated Rosin | 化学品 | — | K5 |
| `vinyl_acetate_laurate_copolymer` | 醋酸乙烯酯-月桂酸乙烯酯共聚物 | `(C4H6O2)n(C14H26O2)m` | 固 | — |  | 第五批 577 号 | Vinyl Acetate-Vinyl Laurate Copolymer | 化学品 | — | K3 |
| `terpene_resin` | 萜烯树脂 | `(C10H16)n` | 固 | — |  | 第五批 578 号 | Synthetic Resin (Terpene Resin) | 化学品 | — | K3 |
| `polymerized_rosin_glycerol_ester` | 聚合松香甘油酯 | 聚合松香与丙三醇的酯混合物 | 固 | — |  | 第五批 579 号 | Glycerol Ester of Polymerized Rosin | 化学品 | — | K5 |
| `polyethylene_wax` | 聚乙烯蜡均聚物 | (C2H4)n（相对分子质量 500–1200） | 固 | — |  | 第五批 580 号 | Polyethylene Wax Homopolymer | 化学品 | — | K7 |
| `fischer_tropsch_wax` | 石油石蜡（费-托合成法） | 正构烷烃混合物，以 C50H102 计（凝固点 93–99 °C） | 固 | — |  | 第五批 581 号 | Paraffin Wax, Synthetic (Fischer–Tropsch) | 化学品 | — | K7 |
| `candelilla_wax` | 小烛树蜡 | 烃（C29–C33，约 50%）、蜡酯、游离酸与三萜树脂酯的混合物 | 固 | — |  | 第五批 582 号 | Candelilla Wax | 化学品 | — | K7 |
| `ammonium_alginate` | 海藻酸铵 | `(C6H11NO6)n` | 固 | — |  | 第五批 584 号 | Ammonium Alginate | 化学品 | — | K6 |
| `abe_broth` | 丙酮丁醇发酵液 | 丙酮、正丁醇、乙醇（质量比约 3 : 6 : 1）的稀水溶液 | 液 | — |  | 第五批：431 C | ABE Fermentation Broth | 化学品 | — | — |
| `naphtha_cracked_gas` | 石脑油裂解气 | 乙烯、丙烯、碳四与碳五馏分、甲烷、氢气的混合物 | 气 | — |  | 第五批：569、570；S0987（`piperylene`） | Naphtha Cracked Gas | 化学品 | — | — |

## D 纯度数据（来源纯度与单个物料的覆盖）

本节与上表的“纯度类别”一栏一起由 `export_materials.py` 导出（2026-11-04 定，I28）：每种带纯度类别的物料写成 `materials.json` 中的纯度数据，数据生成再输出为数据包文件 `data/foodindustrial/purity_materials/<ID>.json`；类别界限取自 [10](10-purity-jei-ae2.md) 第 1.4 节，导出为 `purity_categories.json`，数据生成输出为 `data/foodindustrial/purity_categories/<类别 ID>.json`。两组文件都随服务器启动与 `/reload` 加载并同步到客户端，整合包可改（文件格式见 10 第 4.7 节）。

- **来源纯度**：配方的来源方式不写数值时取这个值；K0 物料写了才带纯度（显示“含量”），外部物品与缺标签时也取这个值（10 第 1.3、1.6、2.1 节）。数值取自 [07](07-manual-crafting.md) 与 machines/01 中出现的写定值；机器配方需要别的数值时在配方里写 `value`。
- **四档覆盖**：“—”沿用所属类别的界限，“不适用”表示该物料没有这一档。数值与 10 第 1.5 节一致，导出时逐项核对；10 第 1.5 节中的栀子黄由 I12 补入（2026-11-10）。
- 只列有来源纯度或覆盖的物料；其余沿用类别界限、没有来源纯度。

| 建议 ID | 名称 | 来源纯度 | 粗制 | 精制 | 食品级 | 实验室级 | 出处 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| `salt` | 氯化钠（盐） | 83% | 90% | 95% | 97% | 99.8% | 07 海盐；10 §1.5 |
| `gypsum` | 硫酸钙（石膏） | 80% | — | — | — | — | 07 第 12 号；machines/01 |
| `soda_ash` | 碳酸钠（纯碱） | 20% | — | — | — | — | 07 第 5 号（熬干） |
| `potassium_carbonate` | 碳酸钾 | 50% | — | — | — | — | 07 第 7 号（熬干） |
| `potassium_nitrate` | 硝酸钾（硝石） | 90% | — | — | — | — | 07 第 24 号 |
| `potassium_bitartrate` | 酒石酸氢钾（塔塔粉） | 80% | — | — | — | — | 07 第 55 号（酒石）；13 H14 |
| `acetic_acid` | 乙酸（醋酸） | 4% | 不适用 | 不适用 | 3.5% | 99.8% | 07 第 1 号（食醋）；10 §1.5 |
| `lactic_acid` | 乳酸 | 1.2% | 20% | 50% | 76% | 90% | 07 第 2 号（酸浆）；10 §1.5 |
| `citric_acid` | 柠檬酸 | — | 90% | 95% | 99.5% | 99.8% | 10 §1.5 |
| `ethanol` | 乙醇（酒精） | — | 80% | 90% | 92.4% | 99.5% | 10 §1.5 |
| `sulfuric_acid` | 硫酸 | — | 60% | 80% | 92.5% | 98% | 10 §1.5 |
| `hydrochloric_acid` | 盐酸 | — | 20% | 25% | 31% | 36% | 10 §1.5 |
| `phosphoric_acid` | 磷酸 | — | 40% | 60% | 75% | 85% | 10 §1.5 |
| `tartrazine` | 柠檬黄 | — | 50% | 70% | 87% | 95% | 10 §1.5 |
| `sunset_yellow` | 日落黄 | — | 50% | 70% | 87% | 95% | 10 §1.5 |
| `glyceryl_monostearate` | 单硬脂酸甘油酯 | — | 20% | 30% | 40% | 90% | 10 §1.5 |
| `molten_glyceryl_monostearate` | 熔融单硬脂酸甘油酯 | — | 20% | 30% | 40% | 90% | 10 §1.5（同单甘酯） |
| `natural_rubber` | 天然橡胶 | 90% | 80% | 90% | 95% | 不适用 | 07 第 136 号；10 §1.5 |
| `caramel` | 焦糖色 | 30% | 20% | 30% | 40% | 不适用 | 07 第 18 号；10 §1.5 |
| `capsanthin` | 辣椒红素 | 0.2% | 0.5% | 1% | 2.4% | 90% | 07 第 60 号（辣椒油）；10 §1.5 |
| `monascorubrin` | 红曲红素 | — | 1% | 5% | 10% | 90% | 10 §1.5 |
| `betanin` | 甜菜苷 | — | 0.05% | 0.1% | 0.27% | 90% | 10 §1.5 |
| `turmeric` | 姜黄 | 3% | 0.5% | 1% | 1.8% | 不适用 | 07 第 61 号（姜黄粉）；10 §1.5 |
| `indigo` | 靛青 | 2% | — | — | — | — | 07 第 84 号（靛泥） |
| `lecithin` | 磷脂（卵磷脂） | 30% | — | — | — | — | 07 第 69 号（蛋黄卵磷脂） |
| `agar` | 琼脂 | 75% | — | — | — | — | 07 第 14 号（条状琼脂）；I12 |
| `pectin_liquor` | 果胶液 | 1% | — | — | — | — | 16 A（游戏设定，I18，2026-11-11） |
| `red_yeast_rice` | 红曲米 | — | 30% | 45% | 55% | 不适用 | 游戏设定（I18，2026-11-11）：曲房产出 40%–73%（07 作坊阶段的特殊随机），上半区为食品级 |
| `steviol_glycosides` | 甜菊糖苷 | 1.5% | — | — | — | — | 07 第 36 号（甜菊叶浸液）；I12 |
| `gardenia_yellow` | 栀子黄 | 0.2% | 0.2% | 0.5% | 0.9% | 90% | 07 第 62 号（栀子染液）；10 §1.5；I12 |
| `acetobacter` | 醋酸菌（醋母） | 20% | — | — | — | — | 07 自然接种 |
| `lactobacillus` | 乳酸菌（老卤） | 20% | — | — | — | — | 07 自然接种 |
| `monascus` | 红曲霉 | 20% | — | — | — | — | 07 自然接种 |
| `rhizopus` | 米根霉 | 20% | — | — | — | — | 07 自然接种 |
| `aspergillus_oryzae` | 米曲霉 | 20% | — | — | — | — | 07 自然接种 |
| `new_red_lake` | 新红铝色淀 | — | 3% | 6% | 10% | 不适用 | 10 §1.5 |
| `tartrazine_lake` | 柠檬黄铝色淀 | — | 3% | 6% | 10% | 不适用 | 10 §1.5 |
| `erythrosine_lake` | 赤藓红铝色淀 | — | 3% | 6% | 10% | 不适用 | 10 §1.5 |
| `amaranth_lake` | 苋菜红铝色淀 | — | 3% | 6% | 10% | 不适用 | 10 §1.5 |
| `sunset_yellow_lake` | 日落黄铝色淀 | — | 3% | 6% | 10% | 不适用 | 10 §1.5 |
| `brilliant_blue_lake` | 亮蓝铝色淀 | — | 3% | 6% | 10% | 不适用 | 10 §1.5 |
| `allura_red_lake` | 诱惑红铝色淀 | — | 3% | 6% | 10% | 不适用 | 10 §1.5 |
| `indigo_carmine_lake` | 靛蓝铝色淀 | — | 3% | 6% | 10% | 不适用 | 10 §1.5 |
| `quinoline_yellow_lake` | 喹啉黄铝色淀 | — | 3% | 6% | 10% | 不适用 | 10 §1.5 |
| `ponceau_4r_lake` | 胭脂红铝色淀 | — | 7% | 13% | 22% | 不适用 | 10 §1.5 |
| `carmine` | 胭脂虫红铝色淀 | — | 12% | 24% | 40% | 不适用 | 10 §1.5 |
| `natural_carotene` | 天然胡萝卜素 | — | 1.5% | 3% | 5% | 不适用 | 10 §1.5 |
| `tomato_oleoresin` | 番茄红 | — | 1.5% | 3% | 5% | 不适用 | 10 §1.5 |
| `magnesium_carbonate` | 碳酸镁 | — | 80% | 88% | 92.8% | 不适用 | 10 §1.5 |
| `calcium_silicate` | 硅酸钙 | — | 20% | 30% | 40% | 不适用 | 10 §1.5 |
| `potassium_lactate` | 乳酸钾 | — | 15% | 30% | 50% | 不适用 | 10 §1.5 |
| `sodium_lactate` | 乳酸钠 | — | 15% | 30% | 50% | 不适用 | 10 §1.5 |
| `phytic_acid` | 肌醇六磷酸（植酸） | — | 15% | 30% | 50% | 不适用 | 10 §1.5 |
| `sorbitol_syrup` | 山梨糖醇液 | — | 15% | 30% | 50% | 不适用 | 10 §1.5 |
| `maltitol_syrup` | 麦芽糖醇液 | — | 10% | 20% | 34% | 不适用 | 10 §1.5 |
| `lactulose_syrup` | 异构化乳糖 | — | 10% | 20% | 33% | 不适用 | 10 §1.5 |
| `stabilized_chlorine_dioxide` | 稳定态二氧化氯 | — | 0.3% | 0.7% | 1.1% | 不适用 | 10 §1.5 |
| `ice_structuring_protein` | 冰结构蛋白 | — | 0.15% | 0.3% | 0.5% | 不适用 | 10 §1.5 |
| `ferric_tartrate` | 酒石酸铁 | — | 0.8% | 1.7% | 2.8% | 不适用 | 10 §1.5 |
| `sodium_bisulfite` | 亚硫酸氢钠 | — | 10% | 20% | 32% | 不适用 | 10 §1.5 |


## E 食品用香料（第五批）

GB 2760-2024 表 B.2、B.3 中第五批设计的香料，每条一个物料（路线见 [18](18-batch-5-flavors.md)、[19](19-batch-5-synthetic-flavors.md)），共 1801 种：天然 374 种、合成 1427 种；另有 44 条沿用已有物料，只在备注补编码。“分类”为**香料**（单独的创造模式标签页）；单一化合物“核对”打 ✓（SMILES 经 RDKit 反算一致），天然混合物写主成分、不带纯度。与第 A–C 节同一表头，`tools/materials/export_materials.py` 需把本节一并导出（I31）。

### E.1 天然香料（表 B.2）

| 建议 ID | 名称 | 化学式 / 组成 | 状态 | GTCEu 材料 | 核对 | 备注 | 英文名（待审） | 分类 | 颜色 | 纯度类别 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `clove_leaf_oil` | 丁香叶油 | 主成分丁香酚 C10H12O2 | 液 | — |  | 第五批 N001 | Clove Leaf Oil | 香料 | — | — |
| `clove_bud_tincture` | 丁香花蕾酊(提取物) | 主成分丁香酚 C10H12O2（乙醇溶液） | 溶液 | — |  | 第五批 N002 | Clove Bud Tincture (Extract) | 香料 | — | — |
| `clove_bud_oil` | 丁香花蕾油 | 主成分丁香酚 C10H12O2 | 液 | — |  | 第五批 N003 | Clove Bud Oil | 香料 | — | — |
| `basil_oil` | 罗勒油 | 主成分芳樟醇 C10H18O、甲基胡椒酚 C10H12O | 液 | — |  | 第五批 N004 | Basil Oil | 香料 | — | — |
| `star_anise_oil` | 八角茴香油 | 主成分反式茴香脑 C10H12O | 液 | — |  | 第五批 N005 | Star Anise Oil | 香料 | — | — |
| `jasmin_orange_concrete` | 九里香浸膏 | 蜡与香气成分的混合物（主成分未确认） | 固 | — |  | 第五批 N006 | Common Jasmin Orange Concrete | 香料 | — | — |
| `patchouli_oil` | 广藿香油 | 主成分广藿香醇 C15H26O | 液 | — |  | 第五批 N007 | Patchouli Oil | 香料 | #8A5A20 | — |
| `tagetes_oil` | 万寿菊油 | 主成分二氢万寿菊酮 C10H18O、罗勒烯 C10H16 | 液 | — |  | 第五批 N008 | Tagetes Oil | 香料 | — | — |
| `cardamom_oil` | 小豆蔻油 | 主成分 1,8-桉叶素 C10H18O、乙酸松油酯 C12H20O2 | 液 | — |  | 第五批 N010 | Cardamom Oil | 香料 | — | — |
| `cardamom_tincture` | 小豆蔻酊 | 主成分 1,8-桉叶素 C10H18O（乙醇溶液） | 溶液 | — |  | 第五批 N011 | Cardamom Tincture | 香料 | — | — |
| `fennel_tincture` | 小茴香酊 | 主成分反式茴香脑 C10H12O（乙醇溶液） | 溶液 | — |  | 第五批 N012 | Fennel Tincture | 香料 | — | — |
| `litsea_cubeba_oil` | 山苍子油 | 主成分柠檬醛 C10H16O | 液 | — |  | 第五批 N013 | Litsea Cubeba Berry Oil | 香料 | — | — |
| `hawthorn_fruit_tincture` | 山楂酊 | 主成分有机酸（柠檬酸 C6H8O7 等）与黄酮（乙醇溶液） | 溶液 | — |  | 第五批 N014 | Hawthorn Fruit Tincture | 香料 | — | — |
| `garlic_oil` | 大蒜油 | 主成分二烯丙基二硫醚 C6H10S2 | 液 | — |  | 第五批 N015 | Garlic Oil | 香料 | — | — |
| `garlic_oleoresin` | 大蒜油树脂 | 主成分含硫化合物（二烯丙基二硫醚 C6H10S2 等） | 液 | — |  | 第五批 N016 | Garlic Oleoresin | 香料 | — | — |
| `green_cognac_oil` | 天然康酿克油 | 主成分辛酸乙酯 C10H20O2、癸酸乙酯 C12H24O2 | 液 | — |  | 第五批 N017 | Cognac Oil, Green | 香料 | — | — |
| `costus_root_oil` | 云木香油 | 主成分去氢木香内酯 C15H18O2、木香烃内酯 C15H20O2 | 液 | — |  | 第五批 N019 | Costus Root Oil | 香料 | — | — |
| `sweet_bay_oil` | 月桂叶油 | 主成分 1,8-桉叶素 C10H18O | 液 | — |  | 第五批 N020 | Sweet Bay Oil | 香料 | — | — |
| `wumei_tincture` | 乌梅酊 | 主成分有机酸（柠檬酸 C6H8O7 等）（乙醇溶液） | 溶液 | — |  | 第五批 N021 | Wumei Tincture | 香料 | — | — |
| `buchu_leaf_oil` | 布枯叶油 | 主成分地奥酚 C10H16O2、异薄荷酮 C10H18O | 液 | — |  | 第五批 N022 | Buchu Leaf Oil | 香料 | — | — |
| `cocoa_tincture` | 可可酊 | 主成分可可碱 C7H8N4O2、多酚（乙醇溶液） | 溶液 | — |  | 第五批 N023 | Cocoa Tincture | 香料 | #4A2A1A | — |
| `cocoa_husk_tincture` | 可可壳酊 | 主成分可可碱 C7H8N4O2、多酚（乙醇溶液） | 溶液 | — |  | 第五批 N024 | Cocoa Husk Tincture | 香料 | — | — |
| `nardostachys_oil` | 甘松油 | 主成分倍半萜（缬草酮 C15H26O 等） | 液 | — |  | 第五批 N025 | China Nardostachys Oil | 香料 | — | — |
| `licorice_tincture` | 甘草酊 | 主成分甘草酸 C42H62O16（乙醇溶液） | 溶液 | — |  | 第五批 N026 | Licorice Tincture | 香料 | — | — |
| `licorice_fluid_extract` | 甘草流浸膏 | 主成分甘草酸 C42H62O16（水溶液） | 溶液 | — |  | 第五批 N027 | Licorice Extract | 香料 | — | — |
| `wintergreen_oil` | 冬青油 | 主成分水杨酸甲酯 C8H8O3 | 液 | — |  | 第五批 N028 | Wintergreen Oil | 香料 | — | — |
| `michelia_alba_flower_oil` | 白兰花油 | 主成分芳樟醇 C10H18O | 液 | — |  | 第五批 N029 | Michelia Alba Flower Oil | 香料 | — | — |
| `michelia_alba_leaf_oil` | 白兰叶油 | 主成分芳樟醇 C10H18O | 液 | — |  | 第五批 N030 | Michelia Alba Leaf Oil | 香料 | — | — |
| `michelia_alba_absolute` | 白兰花净油 | 主成分芳樟醇 C10H18O、2-苯乙醇 C8H10O | 液 | — |  | 第五批 N031 | Michelia Alba Flower Absolute | 香料 | — | — |
| `michelia_alba_concrete` | 白兰花浸膏 | 蜡与香气成分（芳樟醇 C10H18O 为主） | 固 | — |  | 第五批 N032 | Michelia Alba Flower Concrete | 香料 | — | — |
| `angelica_dahurica_tincture` | 白芷酊 | 主成分欧前胡素 C16H14O4 等呋喃香豆素（乙醇溶液） | 溶液 | — |  | 第五批 N033 | Angelica Dahurica Tincture | 香料 | — | — |
| `lime_oil` | 白柠檬油 | 主成分 d-柠檬烯 C10H16，含柠檬醛 C10H16O | 液 | — |  | 第五批 N034 | Lime Oil | 香料 | — | — |
| `lime_oil_terpenes` | 白柠檬萜烯 | 主成分 d-柠檬烯 C10H16 | 液 | — |  | 第五批 N035 | Lime Oil Terpenes | 香料 | — | — |
| `ginger_oleoresin` | 生姜油树脂 | 主成分 6-姜酚 C17H26O4、α-姜烯 C15H24 | 液 | — |  | 第五批 N036 | Ginger Oleoresin | 香料 | — | — |
| `nutmeg_oil` | 肉豆蔻油 | 主成分桧烯、α-蒎烯 C10H16，含肉豆蔻醚 C11H12O3 | 液 | — |  | 第五批 N037 | Nutmeg Oil | 香料 | — | — |
| `nutmeg_tincture` | 肉豆蔻酊 | 主成分肉豆蔻醚 C11H12O3 等（乙醇溶液） | 溶液 | — |  | 第五批 N038 | Nutmeg Tincture | 香料 | — | — |
| `cassia_oil` | 中国肉桂油 | 主成分肉桂醛 C9H8O | 液 | — |  | 第五批 N039 | Cassia Oil | 香料 | #8A3A1A | — |
| `cassia_bark_tincture` | 中国肉桂皮酊(提取物) | 主成分肉桂醛 C9H8O（乙醇溶液） | 溶液 | — |  | 第五批 N040 | Cassia Bark Tincture (Extract) | 香料 | — | — |
| `black_tea_tincture` | 红茶酊 | 主成分茶黄素、茶多酚与咖啡因 C8H10N4O2（乙醇溶液） | 溶液 | — |  | 第五批 N041 | Black Tea Tincture | 香料 | #7A2E14 | — |
| `davana_oil` | 印蒿油 | 主成分印蒿酮 C15H24O2 | 液 | — |  | 第五批 N042 | Davana Oil | 香料 | — | — |
| `tolu_balsam_tincture` | 吐鲁酊(提取物) | 主成分苯甲酸苄酯 C14H12O2、肉桂酸苄酯 C16H14O2 与树脂（乙醇溶液） | 溶液 | — |  | 第五批 N043 | Tolu Balsam Tincture (Extract) | 香料 | — | — |
| `tolu_balsam_gum` | 吐鲁香膏 | 树脂为主，含苯甲酸苄酯 C14H12O2、肉桂酸苄酯 C16H14O2 | 固 | — |  | 第五批 N044 | Tolu Balsam Gum | 香料 | — | — |
| `douchi_tincture` | 豆豉酊 | 发酵与美拉德产物（吡嗪类、氨基酸）的乙醇溶液，主成分未确认 | 溶液 | — |  | 第五批 N045 | Fermented Soybean (Douchi) Tincture | 香料 | — | — |
| `juniper_berry_oil` | 杜松籽油（刺柏子油） | 主成分 α-蒎烯 C10H16 | 液 | — |  | 第五批 N046 | Juniper Berry Oil | 香料 | — | — |
| `coriander_oil` | 芫荽籽油 | 主成分芳樟醇 C10H18O | 液 | — |  | 第五批 N047 | Coriander Oil | 香料 | — | — |
| `celery_flower_oil` | 芹菜花油 | 主成分 d-柠檬烯 C10H16，含 3-正丁基苯酞 C12H14O2 | 液 | — |  | 第五批 N048 | Celery Flower Oil | 香料 | — | — |
| `celery_seed_oil` | 芹菜籽油 | 主成分 d-柠檬烯 C10H16，含 β-芹子烯 C15H24、3-正丁基苯酞 C12H14O2 | 液 | — |  | 第五批 N049 | Celery Seed Oil | 香料 | — | — |
| `vitex_leaf_oil` | 牡荆叶油 | 主成分 β-石竹烯 C15H24、桧烯 C10H16 | 液 | — |  | 第五批 N050 | Vitex Cannabifolia Leaf Oil | 香料 | — | — |
| `grapefruit_oil` | 圆柚油 | 主成分 d-柠檬烯 C10H16，含圆柚酮 C15H22O | 液 | — |  | 第五批 N051 | Grapefruit Oil, Expressed | 香料 | — | — |
| `atractylodes_oil` | 苍术脂 | 主成分 β-桉叶醇、茅术醇 C15H26O，含苍术素 C13H10O | 固 | — |  | 第五批 N052 | Atractylodes Oil | 香料 | — | — |
| `jujube_tincture` | 枣子酊 | 主成分糖与有机酸（乙醇溶液），香气成分未确认 | 溶液 | — |  | 第五批 N053 | Chinese Date (Common Jujube) Tincture | 香料 | — | — |
| `rose_oil` | 玫瑰油 | 主成分香茅醇 C10H20O、香叶醇 C10H18O | 液 | — |  | 第五批 N054 | Rose Oil | 香料 | — | — |
| `rose_absolute` | 玫瑰净油 | 主成分 2-苯乙醇 C8H10O | 液 | — |  | 第五批 N055 | Rose Absolute | 香料 | — | — |
| `rose_concrete` | 玫瑰浸膏 | 蜡与香气成分（2-苯乙醇 C8H10O、香茅醇 C10H20O） | 固 | — |  | 第五批 N056 | Rose Concrete | 香料 | — | — |
| `orris_concrete` | 鸢尾浸膏 | 肉豆蔻酸 C14H28O2 等脂肪酸与树脂，香气成分 α-鸢尾酮 C14H22O | 固 | — |  | 第五批 N057 | Orris Concrete | 香料 | — | — |
| `orris_root_extract` | 鸢尾脂（鸢尾凝脂） | 主成分肉豆蔻酸 C14H28O2，香气成分 α-鸢尾酮 C14H22O | 固 | — |  | 第五批 N058 | Orris Root Extract | 香料 | — | — |
| `hangzhou_chrysanthemum_oil` | 杭白菊花油 | 主成分樟脑 C10H16O、龙脑 C10H18O | 液 | — |  | 第五批 N059 | Chrysanthemum Hangzhou Flower Oil | 香料 | — | — |
| `hangzhou_chrysanthemum_extract` | 杭白菊花浸膏（杭菊花流浸膏） | 主成分黄酮（木犀草素苷）与绿原酸 C16H18O9（稀乙醇溶液） | 溶液 | — |  | 第五批 N060 | Chrysanthemum Hangzhou Flower Extract | 香料 | — | — |
| `maple_oil` | 枫槭油 | 主成分未确认（枫糖香气成分如香兰素 C8H8O3、呋喃酮类） | 液 | — |  | 第五批 N061 | Maple Oil | 香料 | — | — |
| `maple_concrete` | 枫槭浸膏 | 主成分未确认（同 N061） | 固 | — |  | 第五批 N062 | Maple Concrete | 香料 | — | — |
| `labdanum_extract` | 岩蔷薇浸膏（赖百当浸膏） | 半日花烷型二萜树脂（以半日花烷酸 C20H36O3 计） | 固 | — |  | 第五批 N063 | Labdanum Extract | 香料 | — | — |
| `coffee_tincture` | 咖啡酊 | 主成分咖啡因 C8H10N4O2、绿原酸 C16H18O9（乙醇溶液） | 溶液 | — |  | 第五批 N064 | Coffee Tincture | 香料 | #3A2414 | — |
| `luohan_fruit_tincture` | 罗汉果酊 | 主成分罗汉果苷 V C60H102O29（乙醇溶液） | 溶液 | — |  | 第五批 N065 | Luohan Fruit Tincture | 香料 | — | — |
| `cassie_concrete` | 金合欢浸膏 | 蜡与香气成分（水杨酸甲酯 C8H8O3、苯甲醇 C7H8O 等） | 固 | — |  | 第五批 N066 | Cassie Concrete | 香料 | — | — |
| `ylang_ylang_oil` | 依兰依兰油 | 主成分大根香叶烯 D C15H24、芳樟醇 C10H18O、乙酸苄酯 C9H10O2 | 液 | — |  | 第五批 N067 | Ylang Ylang Oil | 香料 | — | — |
| `jasmine_grandiflorum_absolute` | 大花茉莉净油 | 主成分乙酸苄酯 C9H10O2、苯甲酸苄酯 C14H12O2 | 液 | — |  | 第五批 N068 | Jasminum Grandiflorum Absolute | 香料 | — | — |
| `jasmine_grandiflorum_concrete` | 大花茉莉浸膏 | 蜡与香气成分（乙酸苄酯 C9H10O2 为主） | 固 | — |  | 第五批 N069 | Jasminum Grandiflorum Concrete | 香料 | — | — |
| `jasmine_sambac_absolute` | 小花茉莉净油 | 主成分芳樟醇 C10H18O、α-法尼烯 C15H24 | 液 | — |  | 第五批 N070 | Jasminum Sambac Absolute | 香料 | — | — |
| `jasmine_sambac_concrete` | 小花茉莉浸膏 | 蜡与香气成分（芳樟醇 C10H18O、α-法尼烯 C15H24） | 固 | — |  | 第五批 N071 | Jasminum Sambac Concrete | 香料 | — | — |
| `sarcodactylis_oil` | 佛手油 | 主成分 d-柠檬烯 C10H16、γ-松油烯 C10H16 | 液 | — |  | 第五批 N072 | Sarcodactylis Oil | 香料 | — | — |
| `angelica_root_tincture` | 圆叶当归根酊（独活酊） | 主成分香豆素（当归素 C11H6O3 等）与挥发油（乙醇溶液） | 溶液 | — |  | 第五批 N073 | Angelica Root Tincture (Extract) | 香料 | — | — |
| `onion_oil` | 洋葱油 | 主成分二丙基二硫醚 C6H14S2 等含硫化合物 | 液 | — |  | 第五批 N074 | Onion Oil | 香料 | — | — |
| `ginger_oil` | 生姜油 | 主成分 α-姜烯 C15H24 | 液 | — |  | 第五批 N075 | Ginger Oil | 香料 | — | — |
| `turmeric_oil` | 姜黄油 | 主成分芳姜黄酮 C15H20O、姜黄酮 C15H22O | 液 | — |  | 第五批 N076 | Turmeric Oil | 香料 | — | — |
| `turmeric_oleoresin` | 姜黄油树脂 | 主成分姜黄素 C21H20O6，含姜黄油 | 液 | — |  | 第五批 N077 | Turmeric Oleoresin | 香料 | #C0601A | — |
| `turmeric_extract` | 姜黄浸膏 | 主成分姜黄素 C21H20O6 | 固 | — |  | 第五批 N078 | Turmeric Extract | 香料 | — | — |
| `fenugreek_tincture` | 葫芦巴酊 | 主成分葫芦巴碱 C7H7NO2、葫芦巴内酯 C6H8O3（乙醇溶液） | 溶液 | — |  | 第五批 N079 | Fenugreek Tincture (Extract) | 香料 | — | — |
| `daidai_flower_oil` | 玳玳花油 | 主成分芳樟醇 C10H18O、乙酸芳樟酯 C12H20O2 | 液 | — |  | 第五批 N080 | Daidai Flower Oil | 香料 | — | — |
| `daidai_flower_concrete` | 玳玳花浸膏 | 蜡与香气成分（芳樟醇 C10H18O、邻氨基苯甲酸甲酯 C8H9NO2） | 固 | — |  | 第五批 N081 | Daidai Flower Concrete | 香料 | — | — |
| `daidai_fruit_oil` | 玳玳果油 | 主成分 d-柠檬烯 C10H16 | 液 | — |  | 第五批 N082 | Daidai Fruit Oil | 香料 | — | — |
| `pummelo_peel_oil` | 柚皮油 | 主成分 d-柠檬烯 C10H16 | 液 | — |  | 第五批 N083 | Pummelo Peel Oil | 香料 | — | — |
| `cedar_leaf_oil` | 柏木叶油(北美香柏) | 主成分 α-侧柏酮 C10H16O | 液 | — |  | 第五批 N084 | Cedar Leaf Oil | 香料 | — | — |
| `cumin_seed_oil` | 枯茗籽油（孜然油） | 主成分枯茗醛 C10H12O、γ-松油烯 C10H16 | 液 | — |  | 第五批 N085 | Cumin Seed Oil | 香料 | — | — |
| `lemon_oil` | 柠檬油 | 主成分 d-柠檬烯 C10H16，含柠檬醛 C10H16O | 液 | — |  | 第五批 N086 | Lemon Oil | 香料 | — | — |
| `terpeneless_lemon_oil` | 无萜柠檬油 | 主成分柠檬醛 C10H16O | 液 | — |  | 第五批 N087 | Lemon Oil, Terpeneless | 香料 | — | — |
| `lemon_oil_terpenes` | 柠檬油萜烯 | 主成分 d-柠檬烯 C10H16 | 液 | — |  | 第五批 N088 | Terpenes of Lemon Oil | 香料 | — | — |
| `petitgrain_lemon_oil` | 柠檬叶油 | 主成分柠檬醛 C10H16O、d-柠檬烯 C10H16 | 液 | — |  | 第五批 N089 | Petitgrain Lemon Oil | 香料 | — | — |
| `lemongrass_oil` | 柠檬草油 | 主成分柠檬醛 C10H16O | 液 | — |  | 第五批 N090 | Lemongrass Oil | 香料 | — | — |
| `gardenia_flower_concrete` | 栀子花浸膏 | 蜡与香气成分（芳樟醇 C10H18O、乙酸苄酯 C9H10O2） | 固 | — |  | 第五批 N091 | Gardenia Flower Concrete | 香料 | — | — |
| `aglaia_flower_oil` | 树兰花油 | 主成分未确认（倍半萜为主） | 液 | — |  | 第五批 N092 | Aglaia Odorata Flower Oil | 香料 | — | — |
| `aglaia_flower_tincture` | 树兰花酊 | 主成分未确认（同 N092，乙醇溶液） | 溶液 | — |  | 第五批 N093 | Aglaia Odorata Flower Tincture | 香料 | — | — |
| `aglaia_flower_concrete` | 树兰花浸膏 | 蜡与香气成分（主成分未确认） | 固 | — |  | 第五批 N094 | Aglaia Odorata Flower Concrete | 香料 | — | — |
| `treemoss_absolute` | 树苔净油 | 主成分 β-地衣酚羧酸甲酯 C10H12O4、黑茶渍素 C19H18O8 | 液 | — |  | 第五批 N095 | Treemoss Absolute | 香料 | — | — |
| `treemoss_concrete` | 树苔浸膏 | 蜡与地衣酚类（β-地衣酚羧酸甲酯 C10H12O4、黑茶渍素 C19H18O8） | 固 | — |  | 第五批 N096 | Treemoss Concrete | 香料 | — | — |
| `geranium_oil` | 香叶油（玫瑰香叶油） | 主成分香茅醇 C10H20O、香叶醇 C10H18O | 液 | — |  | 第五批 N097 | Geranium Oil (Geranium Rose Oil) | 香料 | — | — |
| `terpeneless_geranium_oil` | 除萜香叶油 | 主成分香茅醇 C10H20O、香叶醇 C10H18O | 液 | — |  | 第五批 N098 | Geranium Oil, Terpeneless | 香料 | — | — |
| `xiangfengcha_oil` | 香风茶油（香茶菜油） | 主成分未确认 | 液 | — |  | 第五批 N099 | Xiang Feng Cha Oil | 香料 | — | — |
| `bergamot_oil` | 香柠檬油 | 主成分乙酸芳樟酯 C12H20O2、d-柠檬烯 C10H16、芳樟醇 C10H18O | 液 | — |  | 第五批 N101 | Bergamot Oil | 香料 | #9AB84A | — |
| `vetiver_oil` | 香根油 | 主成分客烯醇 C15H24O、α-岩兰酮 C15H22O | 液 | — |  | 第五批 N102 | Vetiver Oil | 香料 | #6A4418 | — |
| `vetiver_concrete` | 香根浸膏 | 树脂与香气成分（客烯醇 C15H24O、α-岩兰酮 C15H22O） | 固 | — |  | 第五批 N103 | Vetiver Concrete | 香料 | — | — |
| `vanilla_bean_tincture` | 香荚兰豆酊 | 主成分香兰素 C8H8O3（乙醇溶液） | 溶液 | — |  | 第五批 N104 | Vanilla Bean Tincture | 香料 | — | — |
| `vanilla_bean_concrete` | 香荚兰豆浸膏(提取物) | 主成分香兰素 C8H8O3 | 固 | — |  | 第五批 N105 | Vanilla Bean Concrete (Extract) | 香料 | — | — |
| `cyperus_oil` | 香附子油 | 主成分香附烯 C15H24、α-香附酮 C15H22O | 液 | — |  | 第五批 N106 | Cyperus Oil | 香料 | — | — |
| `chives_oil` | 香葱油 | 主成分二丙基二硫醚 C6H14S2 等含硫化合物 | 液 | — |  | 第五批 N107 | Chives Oil | 香料 | — | — |
| `clary_sage_oil` | 香紫苏油 | 主成分乙酸芳樟酯 C12H20O2、芳樟醇 C10H18O | 液 | — |  | 第五批 N108 | Clary Sage Oil | 香料 | — | — |
| `torreya_shell_concrete` | 香榧子壳浸膏 | 蜡与香气成分（d-柠檬烯、α-蒎烯 C10H16，未确认） | 固 | — |  | 第五批 N109 | Torreya Grandis Shell Concrete | 香料 | — | — |
| `mandarin_oil` | 橘子油 | 主成分 d-柠檬烯 C10H16，含 N-甲基邻氨基苯甲酸甲酯 C9H11NO2 | 液 | — |  | 第五批 N110 | Mandarin Oil | 香料 | #F0A030 | — |
| `terpeneless_mandarin_oil` | 除萜橘子油 | 主成分 N-甲基邻氨基苯甲酸甲酯 C9H11NO2、癸醛 C10H20O | 液 | — |  | 第五批 N111 | Mandarin Oil, Terpeneless | 香料 | — | — |
| `hops_tincture` | 酒花酊 | 主成分 α-酸（葎草酮 C21H30O5）（乙醇溶液） | 溶液 | — |  | 第五批 N112 | Hops Tincture (Extract) | 香料 | — | — |
| `hops_extract_solid` | 酒花浸膏 | 主成分 α-酸（葎草酮 C21H30O5）、β-酸（蛇麻酮 C26H38O4） | 固 | — |  | 第五批 N113 | Hops Extract, Solid | 香料 | — | — |
| `eucalyptus_oil` | 桉叶油(蓝桉油) | 主成分 1,8-桉叶素 C10H18O | 液 | — |  | 第五批 N114 | Eucalyptus Oil | 香料 | — | — |
| `castoreum_tincture` | 海狸酊 | 主成分酚类（4-乙基苯酚 C8H10O 等）、苯甲酸 C7H6O2（乙醇溶液） | 溶液 | — |  | 第五批 N115 | Castoreum Tincture (Extract) | 香料 | — | — |
| `cinnamon_bark_oil` | 斯里兰卡肉桂皮油 | 主成分肉桂醛 C9H8O，含丁香酚 C10H12O2 | 液 | — |  | 第五批 N116 | Cinnamon Bark Oil | 香料 | — | — |
| `cinnamon_leaf_oil` | 斯里兰卡肉桂叶油 | 主成分丁香酚 C10H12O2 | 液 | — |  | 第五批 N117 | Cinnamon Leaf Oil | 香料 | — | — |
| `osmanthus_absolute` | 桂花净油 | 主成分 β-紫罗兰酮 C13H20O、二氢-β-紫罗兰酮 C13H22O、芳樟醇 C10H18O | 液 | — |  | 第五批 N118 | Osmanthus Fragrans Flower Absolute | 香料 | — | — |
| `osmanthus_tincture` | 桂花酊 | 主成分 β-紫罗兰酮 C13H20O、芳樟醇 C10H18O（乙醇溶液） | 溶液 | — |  | 第五批 N119 | Osmanthus Fragrans Flower Tincture | 香料 | — | — |
| `osmanthus_concrete` | 桂花浸膏 | 蜡与香气成分（β-紫罗兰酮 C13H20O 等） | 固 | — |  | 第五批 N120 | Osmanthus Fragrans Flower Concrete | 香料 | — | — |
| `longan_tincture` | 桂圆酊 | 主成分糖与有机酸（乙醇溶液），香气成分未确认 | 溶液 | — |  | 第五批 N121 | Longan Tincture | 香料 | — | — |
| `spearmint_oil` | 留兰香油 | 主成分 l-香芹酮 C10H14O | 液 | — |  | 第五批 N122 | Spearmint Oil | 香料 | — | — |
| `walnut_hull_extract` | 核桃壳提取物 | 主成分单宁与胡桃醌 C10H6O3（水溶液） | 溶液 | — |  | 第五批 N123 | Walnut Hull Extract | 香料 | — | — |
| `common_jasmine_absolute` | 素方花净油 | 主成分乙酸苄酯 C9H10O2、芳樟醇 C10H18O、苯甲酸苄酯 C14H12O2 | 液 | — |  | 第五批 N124 | Common White Jasmine Flower Absolute | 香料 | — | — |
| `sweet_birch_oil` | 桦焦油 | 主成分水杨酸甲酯 C8H8O3 | 液 | — |  | 第五批 N125 | Sweet Birch Oil | 香料 | — | — |
| `broad_bean_flower_tincture` | 蚕豆花酊 | 主成分未确认（黄酮、L-多巴 C9H11NO4 等的乙醇溶液） | 溶液 | — |  | 第五批 N126 | Broad Bean Flower Tincture | 香料 | — | — |
| `green_tea_tincture` | 绿茶酊 | 主成分茶多酚（表没食子儿茶素没食子酸酯 C22H18O11）、咖啡因 C8H10N4O2（乙醇溶液） | 溶液 | — |  | 第五批 N127 | Green Tea Tincture | 香料 | #8A9A3A | — |
| `wild_rose_concrete` | 野玫瑰浸膏 | 蜡与香气成分（2-苯乙醇 C8H10O、香茅醇 C10H20O） | 固 | — |  | 第五批 N128 | Wild Rose Concrete | 香料 | — | — |
| `sweet_fennel_oil` | 甜小茴香油 | 主成分反式茴香脑 C10H12O | 液 | — |  | 第五批 N129 | Fennel Oil, Sweet | 香料 | — | — |
| `stevia_oil` | 甜叶菊油 | 主成分倍半萜（斯巴醇 C15H24O、石竹烯氧化物 C15H24O） | 液 | — |  | 第五批 N130 | Stevia Rebaudiana Oil | 香料 | — | — |
| `orange_oil` | 甜橙油 | 主成分 d-柠檬烯 C10H16 | 液 | — |  | 第五批 N131 | Orange Oil | 香料 | #F0A030 | — |
| `terpeneless_orange_oil` | 除萜甜橙油 | 主成分癸醛 C10H20O、芳樟醇 C10H18O | 液 | — |  | 第五批 N132 | Orange Oil, Terpeneless | 香料 | — | — |
| `orange_oil_terpenes` | 甜橙油萜烯 | 主成分 d-柠檬烯 C10H16 | 液 | — |  | 第五批 N133 | Terpenes of Orange Oil | 香料 | — | — |
| `chicory_concrete` | 菊苣浸膏 | 主成分 山莴苣素 C15H16O5 等苦味内酯 | 固 | — |  | 第五批 N134 | Chicory Concrete (Extract) | 香料 | — | — |
| `tuberose_concrete` | 晚香玉浸膏 | 主成分 苯甲酸苄酯 C14H12O2、邻氨基苯甲酸甲酯 C8H9NO2，含植物蜡 | 固 | — |  | 第五批 N135 | Tuberose Concrete | 香料 | — | — |
| `violet_leaf_concrete` | 紫罗兰叶浸膏 | 主成分 2,6-壬二烯醛 C9H14O | 液 | — |  | 第五批 N136 | Violet Leaf Concrete | 香料 | — | — |
| `peppermint_oil` | 椒样薄荷油 | 主成分 l-薄荷醇 C10H20O、薄荷酮 C10H18O | 液 | — |  | 第五批 N137 | Peppermint Oil | 香料 | — | — |
| `blackcurrant_tincture` | 黑加仑酊 | 黑加仑芽香气成分的乙醇溶液，主成分 δ-3-蒈烯 C10H16 | 溶液 | — |  | 第五批 N138 | Blackcurrant Tincture | 香料 | — | — |
| `blackcurrant_concrete` | 黑加仑浸膏 | 主成分 δ-3-蒈烯 C10H16、β-石竹烯 C15H24，含植物蜡 | 固 | — |  | 第五批 N139 | Blackcurrant Concrete | 香料 | — | — |
| `sophora_flower_absolute` | 槐树花净油 | 槐花香气成分，主成分未确认 | 液 | — |  | 第五批 N140 | Sophora Japonica Flower Absolute | 香料 | — | — |
| `sophora_flower_concrete` | 槐树花浸膏 | 槐花香气成分与植物蜡，主成分未确认 | 固 | — |  | 第五批 N141 | Sophora Japonica Flower Concrete | 香料 | — | — |
| `capsicum_tincture` | 辣椒酊 | 辣椒素 C18H27NO3 的乙醇溶液 | 溶液 | — |  | 第五批 N142 | Capsicum Tincture (Extract) | 香料 | — | — |
| `guaiac_wood_oil` | 愈疮木油 | 主成分 愈创木醇、布藜醇 C15H26O | 固 | — |  | 第五批 N144 | Guaiac Wood Oil | 香料 | — | — |
| `valerian_root_oil` | 缬草油 | 主成分 乙酸龙脑酯 C12H20O2 | 液 | — |  | 第五批 N145 | Valerian Root Oil | 香料 | — | — |
| `crimson_glory_rose_absolute` | 墨红花净油 | 主成分 苯乙醇 C8H10O、香茅醇 C10H20O | 液 | — |  | 第五批 N146 | Rose Crimson Glory Flower Absolute | 香料 | — | — |
| `crimson_glory_rose_concrete` | 墨红花浸膏 | 主成分 苯乙醇 C8H10O、香茅醇 C10H20O，含植物蜡 | 固 | — |  | 第五批 N147 | Rose Crimson Glory Flower Concrete | 香料 | — | — |
| `petitgrain_bigarade_oil` | 橙叶油 | 主成分 乙酸芳樟酯 C12H20O2、芳樟醇 C10H18O | 液 | — |  | 第五批 N149 | Petitgrain Bigarade Oil | 香料 | — | — |
| `dementholized_mint_oil` | 亚洲薄荷素油 | 主成分 l-薄荷醇 C10H20O、薄荷酮 C10H18O | 液 | — |  | 第五批 N151 | Mentha Arvensis Oil, Partially Dementholized | 香料 | — | — |
| `sandalwood_oil` | 檀香油 | 主成分 α-、β-檀香醇 C15H24O | 液 | — |  | 第五批 N152 | Sandalwood Oil | 香料 | — | — |
| `lavender_oil` | 薰衣草油 | 主成分 乙酸芳樟酯 C12H20O2、芳樟醇 C10H18O | 液 | — |  | 第五批 N153 | Lavender Oil | 香料 | — | — |
| `spanish_origanum_oil` | 头状百里香油（西班牙牛至油） | 主成分 香芹酚 C10H14O | 液 | — |  | 第五批 N154 | Origanum Oil (Thymus Capitatus) | 香料 | — | — |
| `kola_nut_extract` | 可乐果提取物 | 主成分 咖啡因 C8H10N4O2、可可碱 C7H8N4O2 | 液 | — |  | 第五批 N155 | Kola Nut Extract | 香料 | — | — |
| `schinus_molle_oil` | 加州胡椒油 | 主成分 α-水芹烯 C10H16、月桂烯 C10H16 | 液 | — |  | 第五批 N156 | Schinus Molle Oil | 香料 | — | — |
| `cascarilla_bark_oil` | 卡黎皮油 | 主成分 对伞花烃 C10H14、β-石竹烯 C15H24 | 液 | — |  | 第五批 N157 | Cascarilla Bark Oil | 香料 | — | — |
| `thyme_oil` | 百里香油 | 主成分 百里酚 C10H14O、对伞花烃 C10H14 | 液 | — |  | 第五批 N158 | Thyme Oil | 香料 | — | — |
| `starter_distillate` | 奶油发酵起子蒸馏物（黄油蒸馏物） | 主成分 2,3-丁二酮 C4H6O2 的稀水溶液 | 溶液 | — |  | 第五批 N159 | Butter Starter Distillate | 香料 | — | — |
| `cananga_oil` | 卡南伽油 | 主成分 β-石竹烯 C15H24、大根香叶烯 D C15H24、乙酸苄酯 C9H10O2 | 液 | — |  | 第五批 N160 | Cananga Oil | 香料 | — | — |
| `laurel_leaf_oleoresin` | 月桂叶提取物/油树脂 | 主成分 1,8-桉叶素 C10H18O | 液 | — |  | 第五批 N161 | Laurel Leaves Extract/oleoresin | 香料 | — | — |
| `ginger_extract` | 生姜提取物(生姜浸膏) | 主成分 6-姜酚 C17H26O4、姜烯 C15H24 | 液 | — |  | 第五批 N162 | Ginger Extract (Ginger Concrete) | 香料 | — | — |
| `oak_chips_extract` | 白栎木屑提取物 | 主成分 橡木内酯 C9H16O2、香兰素 C8H8O3，含鞣花单宁 | 液 | — |  | 第五批 N163 | Oak Chips Extract | 香料 | — | — |
| `estragon_oil` | 龙蒿油 | 主成分 甲基胡椒酚（草蒿脑）C10H12O | 液 | — |  | 第五批 N164 | Estragon Oil | 香料 | — | — |
| `white_camphor_oil` | 白樟油 | 主成分 1,8-桉叶素 C10H18O、d-柠檬烯 C10H16、α-蒎烯 C10H16 | 液 | — |  | 第五批 N165 | Camphor Oil, White | 香料 | — | — |
| `mace_oil` | 肉豆蔻衣油 | 主成分 香桧烯 C10H16、α-蒎烯 C10H16、肉豆蔻醚 C11H12O3 | 液 | — |  | 第五批 N166 | Mace Oil | 香料 | — | — |
| `pimento_leaf_oil` | 众香叶油 | 主成分 丁香酚 C10H12O2 | 液 | — |  | 第五批 N167 | Pimento Leaf Oil | 香料 | — | — |
| `spanish_sage_oil` | 西班牙鼠尾草油 | 主成分 1,8-桉叶素 C10H18O、樟脑 C10H16O | 液 | — |  | 第五批 N168 | Sage Oil, Spanish | 香料 | — | — |
| `tangerine_oil` | 红橘油 | 主成分 d-柠檬烯 C10H16、γ-松油烯 C10H16 | 液 | — |  | 第五批 N169 | Tangerine Oil | 香料 | #E8802A | — |
| `lavandin_oil` | 杂薰衣草油 | 主成分 芳樟醇 C10H18O、乙酸芳樟酯 C12H20O2、樟脑 C10H16O | 液 | — |  | 第五批 N170 | Lavandin Oil | 香料 | — | — |
| `apricot_kernel_oil` | 杏仁油 | 油酸、亚油酸甘油三酯为主，以三油酸甘油酯 C57H104O6 计 | 液 | — |  | 第五批 N171 | Apricot Kernel Oil | 香料 | — | — |
| `styrax_oil` | 苏合香油 | 主成分 苯乙烯 C8H8、肉桂醇 C9H10O | 液 | — |  | 第五批 N172 | Styrax Oil | 香料 | — | — |
| `styrax_extract` | 苏合香提取物 | 主成分 肉桂酸 C9H8O2 及其酯，含树脂 | 液 | — |  | 第五批 N173 | Styrax Extract | 香料 | — | — |
| `locust_bean_oil` | 长角豆油 | 主成分未确认 | 液 | — |  | 第五批 N174 | Locust Bean Oil | 香料 | — | — |
| `carob_bean_extract` | 角豆提取物 | 主成分 蔗糖 C12H22O11、D-松醇 C7H14O6 | 液 | — |  | 第五批 N175 | Carob Bean Extract | 香料 | — | — |
| `olibanum_oil` | 乳香油 | 主成分 α-蒎烯 C10H16 | 液 | — |  | 第五批 N177 | Olibanum Oil | 香料 | — | — |
| `myrrh_oil` | 没药油 | 主成分 呋喃桉叶-1,3-二烯 C15H20O | 液 | — |  | 第五批 N178 | Myrrh Oil | 香料 | — | — |
| `galangal_root_extract` | 良姜根提取物 | 主成分 1'-乙酰氧基胡椒酚乙酸酯 C13H14O4、1,8-桉叶素 C10H18O | 液 | — |  | 第五批 N179 | Galangal Root Extract | 香料 | — | — |
| `scotch_pine_oil` | 苏格兰松油 | 主成分 α-蒎烯 C10H16、δ-3-蒈烯 C10H16 | 液 | — |  | 第五批 N180 | Pine Oil, Scotch | 香料 | — | — |
| `fennel_oil` | 小茴香油（普通小茴香油） | 主成分 反式茴香脑 C10H12O、葑酮 C10H16O | 液 | — |  | 第五批 N181 | Fennel Oil (Common) | 香料 | — | — |
| `bitter_almond_oil` | 苦杏仁油 | 主成分 苯甲醛 C7H6O（约 95%） | 液 | — |  | 第五批 N182 | Almond Oil, Bitter | 香料 | — | — |
| `asafoetida_oil` | 阿魏油 | 主成分 仲丁基丙烯基二硫醚 C7H14S2 | 液 | — |  | 第五批 N183 | Asafoetida Oil | 香料 | — | — |
| `cassie_absolute` | 金合欢净油 | 主成分 水杨酸甲酯 C8H8O3、苯甲醇 C7H8O | 液 | — |  | 第五批 N184 | Cassie Absolute | 香料 | — | — |
| `parsley_leaf_oil` | 欧芹叶油 | 主成分 1,3,8-对薄荷三烯 C10H14、β-水芹烯 C10H16 | 液 | — |  | 第五批 N185 | Parsley Leaf Oil | 香料 | — | — |
| `pine_needle_oil` | 松针油 | 主成分 乙酸龙脑酯 C12H20O2、莰烯 C10H16 | 液 | — |  | 第五批 N186 | Pine Needle Oil | 香料 | — | — |
| `boronia_absolute` | 波罗尼花净油 | 主成分 β-紫罗兰酮 C13H20O | 液 | — |  | 第五批 N187 | Boronia Absolute | 香料 | — | — |
| `bois_de_rose_oil` | 玫瑰木油 | 主成分 芳樟醇 C10H18O | 液 | — |  | 第五批 N188 | Bois De Rose Oil | 香料 | — | — |
| `palmarosa_oil` | 玫瑰草油 | 主成分 香叶醇 C10H18O | 液 | — |  | 第五批 N189 | Palmarosa Oil | 香料 | — | — |
| `citronella_oil` | 香茅油 | 主成分 香茅醛 C10H18O、香叶醇 C10H18O | 液 | — |  | 第五批 N190 | Citronella Oil | 香料 | — | — |
| `rosemary_oil` | 迷迭香油 | 主成分 1,8-桉叶素 C10H18O、樟脑 C10H16O、α-蒎烯 C10H16 | 液 | — |  | 第五批 N191 | Rosemary Oil | 香料 | — | — |
| `balsam_fir_oil` | 香脂冷杉油 | 主成分 β-蒎烯 C10H16、乙酸龙脑酯 C12H20O2 | 液 | — |  | 第五批 N192 | Balsam Fir Oil | 香料 | — | — |
| `balsam_fir_oleoresin` | 香脂冷杉油树脂 | 树脂酸（以松香酸 C20H30O2 计）与 α-、β-蒎烯 C10H16 | 液 | — |  | 第五批 N193 | Balsam Fir Oleoresin | 香料 | — | — |
| `carrot_seed_oil` | 胡萝卜籽油 | 主成分 胡萝卜醇 C15H26O | 液 | — |  | 第五批 N194 | Carrot Seed Oil | 香料 | — | — |
| `roman_chamomile_oil` | 春黄菊花油(罗马) | 主成分 当归酸异丁酯 C9H16O2、当归酸异戊酯 C10H18O2 | 液 | — |  | 第五批 N195 | Chamomile Flower Oil (Roman) | 香料 | — | — |
| `roman_chamomile_absolute` | 春黄菊花净油(提取物)(罗马) | 主成分 当归酸异丁酯 C9H16O2、当归酸异戊酯 C10H18O2 | 液 | — |  | 第五批 N196 | Chamomile Flower Absolute (Extract) (Roman) | 香料 | — | — |
| `cascara_bitterless_extract` | 药鼠李提取物 | 主成分 蒽醌苷（鼠李苷、芦荟苷 C21H22O9） | 液 | — |  | 第五批 N197 | Cascara Bitterless Extract | 香料 | — | — |
| `cubeb_oil` | 荜澄茄油 | 主成分 香桧烯 C10H16、荜澄茄烯 C15H24 | 液 | — |  | 第五批 N198 | Cubeb Oil | 香料 | — | — |
| `pennyroyal_oil` | 胡薄荷油（唇萼薄荷油） | 主成分 胡薄荷酮 C10H16O | 液 | — |  | 第五批 N199 | Pennyroyal Oil | 香料 | — | — |
| `lovage_oil` | 欧当归油 | 主成分 藁本内酯 C12H14O2、β-水芹烯 C10H16 | 液 | — |  | 第五批 N200 | Lovage Oil | 香料 | — | — |
| `horehound_extract` | 夏至草提取物 | 主成分 欧夏至草素 C20H28O4 | 液 | — |  | 第五批 N201 | Horehound Extract | 香料 | — | — |
| `yucca_extract` | 莫哈弗丝兰提取物 | 甾体皂苷为主，苷元以菝葜皂苷元 C27H44O3 计 | 液 | — |  | 第五批 N202 | Yucca Mohave Extract | 香料 | — | — |
| `kelp_extract` | 海草(藻)提取物 | 主成分 D-甘露糖醇 C6H14O6、L-谷氨酸 C5H9NO4 | 液 | — |  | 第五批 N203 | Kelp | 香料 | — | — |
| `hyssop_oil` | 海索草油 | 主成分 松樟酮、异松樟酮 C10H16O | 液 | — |  | 第五批 N204 | Hyssop Oil | 香料 | — | — |
| `dill_herb_oil` | 莳萝草油（莳萝油） | 主成分 α-水芹烯 C10H16、莳萝醚 C10H16O、d-香芹酮 C10H14O | 液 | — |  | 第五批 N205 | Dill Herb Oil | 香料 | — | — |
| `peru_balsam` | 秘鲁香脂 | 主成分 苯甲酸苄酯 C14H12O2、肉桂酸苄酯 C16H14O2，含树脂 | 液 | — |  | 第五批 N206 | Balsam Peru | 香料 | #3A1E10 | — |
| `galbanum_oil` | 格蓬油 | 主成分 β-蒎烯 C10H16、δ-3-蒈烯 C10H16 | 液 | — |  | 第五批 N207 | Galbanum Oil | 香料 | — | — |
| `amyris_oil` | 脂檀油 | 主成分 缬草萜烯醇、榄香醇 C15H26O | 液 | — |  | 第五批 N208 | Amyris Oil | 香料 | — | — |
| `mimosa_absolute` | 银白金合欢净油（含羞草净油） | 主成分未确认，含茴香醛 C8H8O2 | 液 | — |  | 第五批 N209 | Mimosa Absolute | 香料 | — | — |
| `elderflower_absolute` | 接骨木花净油 | 主成分 芳樟醇 C10H18O 及其氧化物 | 液 | — |  | 第五批 N210 | Elder Flower Absolute | 香料 | — | — |
| `sweet_marjoram_oil` | 甘牛至油 | 主成分 4-松油醇 C10H18O、γ-松油烯 C10H16 | 液 | — |  | 第五批 N211 | Marjoram Oil, Sweet | 香料 | — | — |
| `gentian_root_extract` | 黄龙胆根提取物 | 主成分 龙胆苦苷 C16H20O9 | 液 | — |  | 第五批 N212 | Gentian Root Extract | 香料 | — | — |
| `ambrette_seed_oil` | 黄葵籽油 | 主成分 乙酸金合欢酯 C17H28O2、黄葵内酯 C16H28O2 | 液 | — |  | 第五批 N213 | Ambrette Seed Oil | 香料 | — | — |
| `wild_cherry_bark_extract` | 野黑樱桃树皮提取物 | 主成分 苯甲醛 C7H6O、野樱苷 C14H17NO6 | 液 | — |  | 第五批 N214 | Cherry Bark Extract (Wild) | 香料 | — | — |
| `black_pepper_oil` | 黑胡椒油 | 主成分 β-石竹烯 C15H24、d-柠檬烯 C10H16、香桧烯 C10H16 | 液 | — |  | 第五批 N215 | Pepper Oil, Black | 香料 | — | — |
| `caraway_seed_oil` | 葛缕籽油 | 主成分 d-香芹酮 C10H14O、d-柠檬烯 C10H16 | 液 | — |  | 第五批 N216 | Caraway Seed Oil | 香料 | — | — |
| `elemi_resinoid` | 榄香香树脂 | 树脂（α-、β-香树脂醇 C30H50O）与精油（d-柠檬烯 C10H16、榄香脂素 C12H16O3） | 固 | — |  | 第五批 N217 | Elemi Resinoid | 香料 | — | — |
| `immortelle_extract` | 蜡菊提取物 | 主成分 乙酸橙花酯 C12H20O2 | 液 | — |  | 第五批 N218 | Immortelle Extract | 香料 | — | — |
| `balm_oil` | 香蜂花油 | 主成分 柠檬醛 C10H16O、β-石竹烯 C15H24 | 液 | — |  | 第五批 N219 | Balm Oil | 香料 | — | — |
| `d_camphor` | d-樟脑 | `C10H16O` | 固 | — | ✓ | 第五批 N220 | d-Camphor | 香料 | — | K3 |
| `orange_flower_absolute` | 橙花净油 | 主成分 芳樟醇 C10H18O、苯乙醇 C8H10O、邻氨基苯甲酸甲酯 C8H9NO2 | 液 | — |  | 第五批 N221 | Orange Flower Absolute | 香料 | — | — |
| `naringin` | 柚苷（柚皮甙提取物） | `C27H32O14` | 固 | — | ✓ | 第五批 N222 | Naringin Extract | 香料 | — | K3 |
| `spike_lavender_oil` | 穗薰衣草油 | 主成分 芳樟醇 C10H18O、1,8-桉叶素 C10H18O、樟脑 C10H16O | 液 | — |  | 第五批 N223 | Spike Lavender Oil | 香料 | — | — |
| `genet_absolute` | 鹰爪豆净油 | 主成分未确认 | 液 | — |  | 第五批 N224 | Genet Absolute | 香料 | — | — |
| `daidai_peel_oil` | 玳玳果皮油 | 主成分 d-柠檬烯 C10H16 | 液 | — |  | 第五批 N225 | Daidai Peel Oil | 香料 | — | — |
| `cold_pressed_orange_oil` | 甜橙油(橙皮压榨法) | 主成分 d-柠檬烯 C10H16（约 95%） | 液 | — |  | 第五批 N226 | Orange Oil, Sweet, Cold Pressed | 香料 | — | — |
| `bush_red_pepper_oleoresin` | 小米辣椒油树脂 | 主成分 辣椒素 C18H27NO3 | 液 | — |  | 第五批 N227 | Bush Red Pepper Oleoresin | 香料 | #9A1A10 | — |
| `clove_stem_oil` | 丁香茎油 | 主成分 丁香酚 C10H12O2 | 液 | — |  | 第五批 N228 | Clove Stem Oil | 香料 | — | — |
| `anise_oil` | 大茴香油（茴芹油） | 主成分 反式茴香脑 C10H12O | 液 | — |  | 第五批 N229 | Anise Oil | 香料 | — | — |
| `l_asparagine` | L-天冬酰胺 | `C4H8N2O3·H2O` | 固 | — | ✓ | 第五批 N230 | L-Asparagine | 香料 | — | K3 |
| `mate_extract` | 巴拉圭茶提取物 | 主成分 咖啡因 C8H10N4O2、绿原酸 C16H18O9 | 液 | — |  | 第五批 N231 | Mate Extract | 香料 | — | — |
| `hickory_bark_extract` | 白山核桃树皮提取物 | 单宁与酚类，主成分未确认 | 液 | — |  | 第五批 N232 | Hickory Bark Extract | 香料 | — | — |
| `guarana_extract` | 瓜拉纳提取物 | 主成分 咖啡因 C8H10N4O2 | 液 | — |  | 第五批 N233 | Guarana Extract | 香料 | — | — |
| `white_thyme_oil` | 白百里香油 | 主成分 百里酚 C10H14O、对伞花烃 C10H14 | 液 | — |  | 第五批 N235 | Thyme Oil, White | 香料 | — | — |
| `white_pepper_oil` | 白胡椒油 | 主成分 β-石竹烯 C15H24、d-柠檬烯 C10H16 | 液 | — |  | 第五批 N236 | Pepper Oil, White | 香料 | — | — |
| `white_pepper_oleoresin` | 白胡椒油树脂 | 主成分 胡椒碱 C17H19NO3 | 液 | — |  | 第五批 N237 | Pepper Oleoresin, White | 香料 | — | — |
| `white_cognac_oil` | 白康酿克油 | 主成分 癸酸乙酯 C12H24O2、辛酸乙酯 C10H20O2 | 液 | — |  | 第五批 N238 | Cognac Oil, White | 香料 | — | — |
| `butter_esters` | 白脱酯 | 主成分 丁酸乙酯 C6H12O2 等脂肪酸乙酯 | 液 | — |  | 第五批 N239 | Butter Esters | 香料 | — | — |
| `butter_acids` | 白脱酸 | 主成分 棕榈酸 C16H32O2、油酸 C18H34O2、丁酸 C4H8O2 | 固 | — |  | 第五批 N240 | Butter Acids | 香料 | — | — |
| `pimenta_oil` | 众香果油 | 主成分 丁香酚 C10H12O2 | 液 | — |  | 第五批 N241 | Pimenta Oil | 香料 | — | — |
| `benzoin_resinoid` | 安息香树脂 | 主成分 苯甲酸松柏醇酯 C17H16O4、苯甲酸 C7H6O2 | 固 | — |  | 第五批 N242 | Benzoin Resinoid | 香料 | — | — |
| `angelica_seed_oil` | 当归籽油 | 主成分 β-水芹烯 C10H16 | 液 | — |  | 第五批 N243 | Angelica Seed Oil | 香料 | — | — |
| `angelica_root_oil` | 当归根油 | 主成分 α-蒎烯 C10H16、δ-3-蒈烯 C10H16 | 液 | — |  | 第五批 N244 | Angelica Root Oil | 香料 | — | — |
| `mace_oleoresin` | 肉豆蔻衣油树脂/提取物 | 主成分 肉豆蔻醚 C11H12O3、香桧烯 C10H16 | 液 | — |  | 第五批 N245 | Mace Oleoresin/extract | 香料 | — | — |
| `west_indian_bay_extract` | 西印度月桂叶提取物 | 主成分 丁香酚 C10H12O2、月桂烯 C10H16 | 液 | — |  | 第五批 N246 | Bay Leaves, West Indian, Extract | 香料 | — | — |
| `west_indian_bay_oil` | 西印度月桂叶油 | 主成分 丁香酚 C10H12O2、胡椒酚 C9H10O、月桂烯 C10H16 | 液 | — |  | 第五批 N247 | Bay Leaves, West Indian, Oil | 香料 | — | — |
| `l_arabinose` | L-阿拉伯糖 | `C5H10O5` | 固 | — | ✓ | 第五批 N248 | L-Arabinose | 香料 | — | K2 |
| `lovage_extract` | 欧当归提取物 | 主成分 藁本内酯 C12H14O2 | 液 | — |  | 第五批 N250 | Lovage Extract | 香料 | — | — |
| `parsley_oleoresin` | 欧芹油树脂 | 主成分 芹菜脑 C12H14O4、肉豆蔻醚 C11H12O3 | 液 | — |  | 第五批 N251 | Parsley Oleoresin | 香料 | — | — |
| `quassia_extract` | 苦木提取物 | 主成分 苦木素 C22H28O6 | 液 | — |  | 第五批 N253 | Quassia Extract | 香料 | — | — |
| `orange_leaf_absolute` | 苦橙叶净油 | 主成分 乙酸芳樟酯 C12H20O2、芳樟醇 C10H18O | 液 | — |  | 第五批 N254 | Orange Leaf Absolute | 香料 | — | — |
| `bitter_orange_oil` | 苦橙油 | 主成分 d-柠檬烯 C10H16 | 液 | — |  | 第五批 N255 | Orange Oil, Bitter | 香料 | — | — |
| `cinchona_bark_powder` | 金鸡纳树皮 | 主成分 奎宁 C20H24N2O2 等生物碱 | 固 | — |  | 第五批 N256 | Cinchona Bark (Yellow) | 香料 | — | — |
| `jambu_oleoresin` | 金钮扣油树脂 | 主成分 金钮扣酰胺 C14H23NO | 液 | — |  | 第五批 N257 | Jambu Oleoresin | 香料 | — | — |
| `quinine_hydrochloride` | 奎宁盐酸盐 | `C20H25ClN2O2·2H2O` | 固 | — | ✓ | 第五批 N258 | Quinine Hydrochloride | 香料 | — | K3 |
| `onion_oleoresin` | 洋葱油树脂 | 主成分 二丙基二硫醚 C6H14S2 | 液 | — |  | 第五批 N260 | Onion Oleoresin | 香料 | — | — |
| `tea_tree_oil` | 茶树油（互叶白千层油） | 主成分 4-松油醇 C10H18O、γ-松油烯 C10H16 | 液 | — |  | 第五批 N261 | Tea Tree Oil | 香料 | — | — |
| `terpeneless_lime_oil` | 除萜白柠檬油 | 主成分 柠檬醛 C10H16O | 液 | — |  | 第五批 N262 | Lime Oil, Expressed, Terpeneless | 香料 | — | — |
| `terpeneless_orange_peel_oil` | 除萜甜橙皮油 | 主成分 芳樟醇 C10H18O、癸醛 C10H20O | 液 | — |  | 第五批 N263 | Orange Peel Oil, Sweet, Terpeneless | 香料 | — | — |
| `yellow_mustard_oleoresin` | 黄芥末提取物/黄芥末油树脂 | 主成分 4-羟基苄基异硫氰酸酯 C8H7NOS | 液 | — |  | 第五批 N265 | Mustard Extract/oleoresin, Yellow | 香料 | — | — |
| `brown_mustard_extract` | 棕芥末提取物 | 主成分 烯丙基异硫氰酸酯 C4H5NS | 液 | — |  | 第五批 N266 | Mustard Extract, Brown | 香料 | — | — |
| `perilla_leaf_oil` | 紫苏油 | 主成分紫苏醛 C10H14O | 液 | — |  | 第五批 N268 | Perilla Leaf Oil (Shiso Oil) | 香料 | — | — |
| `grapefruit_oil_terpenes` | 葡萄柚油萜烯 | 主成分 d-柠檬烯 C10H16 | 液 | — |  | 第五批 N269 | Grapefruit Oil Terpenes | 香料 | — | — |
| `black_pepper_oleoresin` | 黑胡椒油树脂/黑胡椒提取物 | 主成分胡椒碱 C17H19NO3，含挥发油 | 液 | — |  | 第五批 N270 | Black Pepper Oleoresin/extract | 香料 | — | — |
| `elemi_oil` | 榄香油/提取物/香树脂 | 主成分 d-柠檬烯 C10H16、榄香醇 C15H26O | 液 | — |  | 第五批 N271 | Elemi Oil/extract/resinoid | 香料 | — | — |
| `beeswax_absolute` | 蜂蜡净油 | 蜂蜡的乙醇可溶部分（游离脂肪酸、烃、香气成分） | 固 | — |  | 第五批 N272 | Beeswax Absolute | 香料 | — | — |
| `labdanum_absolute` | 赖百当净油（岩蔷薇净油） | 半日花烷型二萜（以半日花烷酸 C20H36O3 计） | 液 | — |  | 第五批 N273 | Labdanum Absolute | 香料 | — | — |
| `sage_oil` | 鼠尾草油（药鼠尾草油） | 主成分 α-侧柏酮 C10H16O，含 1,8-桉叶素、樟脑 | 液 | — |  | 第五批 N274 | Sage Oil (Dalmatian) | 香料 | — | — |
| `helichrysum_absolute` | 蜡菊净油 | 主成分乙酸橙花酯 C12H20O2 | 液 | — |  | 第五批 N275 | Helichrysum Absolute | 香料 | — | — |
| `molasses_extract` | 糖蜜提取物 | 糖蜜稀醇提取浓缩物（糖、焦糖与美拉德香气成分） | 液 | — |  | 第五批 N276 | Molasses Extract | 香料 | — | — |
| `santalol` | 檀香醇(包括α-檀香醇、β-檀香醇) | `C15H24O` | 液 | — | ✓ | 第五批 N277 | Santalol, α- and β- | 香料 | — | K4 |
| `yerba_santa_fluid_extract` | 山达草流浸膏 | 主成分高圣草酚 C16H14O6、圣草酚 | 溶液 | — |  | 第五批 N278 | Yerba Santa Fluid Extract | 香料 | — | — |
| `alfalfa_extract` | 苜蓿提取物 | 苜蓿全草稀醇提取物（皂苷、黄酮、香豆雌酚） | 液 | — |  | 第五批 N279 | Alfalfa Extract | 香料 | — | — |
| `allspice_oleoresin` | 众香子油树脂/提取物 | 主成分丁香酚 C10H12O2 | 液 | — |  | 第五批 N281 | Allspice Oleoresin/extract | 香料 | — | — |
| `ambrette_seed_absolute` | 黄葵籽净油 | 主成分黄葵内酯 C16H28O2 | 液 | — |  | 第五批 N282 | Ambrette Seed Absolute | 香料 | — | — |
| `peru_balsam_oil` | 秘鲁香膏油 | 主成分苯甲酸苄酯 C14H12O2、肉桂酸苄酯 C16H14O2 | 液 | — |  | 第五批 N283 | Peru Balsam Oil | 香料 | — | — |
| `basil_extract` | 罗勒提取物 | 主成分芳樟醇 C10H18O、甲基胡椒酚 C10H12O | 液 | — |  | 第五批 N284 | Basil Extract | 香料 | — | — |
| `celery_seed_extract_solid` | 芹菜籽固体提取物 | 主成分 3-正丁基苯酞 C12H14O2、瑟丹内酯 | 固 | — |  | 第五批 N285 | Celery Seed Extract, Solid | 香料 | — | — |
| `celery_seed_co2_extract` | 芹菜籽(含 CO2)提取物 | 主成分 d-柠檬烯 C10H16，含 3-正丁基苯酞 C12H14O2 | 液 | — |  | 第五批 N286 | Celery Seed CO2 Extract | 香料 | — | — |
| `hungarian_chamomile_oil` | 母菊(匈牙利春黄菊)花油 | 主成分 α-红没药醇氧化物 A C15H26O2、母菊天蓝烃 C14H16 | 液 | — |  | 第五批 N287 | Chamomile Flower Oil (Hungarian) | 香料 | #2A4A9A | — |
| `cinchona_bark_extract` | 黄色金鸡纳树皮提取物 | 主成分奎宁 C20H24N2O2 等生物碱 | 液 | — |  | 第五批 N288 | Cinchona Bark Extract (Yellow) | 香料 | — | — |
| `clove_bud_oleoresin` | 丁香花蕾油树脂 | 主成分丁香酚 C10H12O2 | 液 | — |  | 第五批 N289 | Clove Bud Oleoresin | 香料 | — | — |
| `red_clover_extract_solid` | 红三叶草提取物(固体) | 主成分异黄酮（芒柄花素 C16H12O4、鹰嘴豆芽素 A） | 固 | — |  | 第五批 N290 | Red Clover Tops Extract, Solid | 香料 | — | — |
| `dandelion_fluid_extract` | 蒲公英流浸膏 | 蒲公英稀醇提取物（倍半萜内酯苦味质、菊糖） | 溶液 | — |  | 第五批 N291 | Dandelion Fluid Extract | 香料 | — | — |
| `dandelion_root_extract_solid` | 蒲公英根固体提取物 | 蒲公英根水提取物（菊糖、倍半萜内酯苦味质） | 固 | — |  | 第五批 N292 | Dandelion Root Solid Extract | 香料 | — | — |
| `fleabane_oil` | 加拿大飞蓬草油 | 主成分 d-柠檬烯 C10H16 | 液 | — |  | 第五批 N293 | Fleabane Oil | 香料 | — | — |
| `mountain_maple_extract_solid` | 穗花槭提取物(固体) | 穗花槭树皮水提取物（鞣质、酚类） | 固 | — |  | 第五批 N294 | Mountain Maple Extract, Solid | 香料 | — | — |
| `rue_oil` | 芸香油 | 主成分 2-十一酮 C11H22O | 液 | — |  | 第五批 N295 | Rue Oil | 香料 | — | — |
| `sage_oleoresin` | 鼠尾草油树脂/提取物 | 主成分鼠尾草酸 C20H28O4，含 α-侧柏酮 C10H16O | 液 | — |  | 第五批 N296 | Sage Oleoresin/extract | 香料 | — | — |
| `sarsaparilla_extract` | 菝葜提取物 | 主成分菝葜皂苷（苷元菝葜皂苷元 C27H44O3） | 液 | — |  | 第五批 N297 | Sarsaparilla Extract | 香料 | — | — |
| `turpentine` | 水蒸气蒸馏松节油 | 主成分 α-蒎烯 C10H16、β-蒎烯 | 液 | — |  | 第五批 N298；与松香（14 第 137 号）同一配方联产 | Turpentine, Steam-distilled | 香料 | — | — |
| `valerian_root_extract` | 缬草根提取物 | 主成分缬草烯酸 C15H22O2 | 液 | — |  | 第五批 N299 | Valerian Root Extract | 香料 | — | — |
| `vanilla_oleoresin` | 香荚兰油树脂 | 主成分香兰素 C8H8O3 | 液 | — |  | 第五批 N300 | Vanilla Oleoresin | 香料 | — | — |
| `violet_leaf_absolute` | 紫罗兰叶净油 | 主成分 (2E,6Z)-壬二烯醛 C9H14O | 液 | — |  | 第五批 N301 | Violet Leaves Absolute | 香料 | — | — |
| `wormwood_oil` | 洋艾油 | 主成分 β-侧柏酮 C10H16O，含乙酸桧酯 | 液 | — |  | 第五批 N302 | Wormwood Oil | 香料 | — | — |
| `tangelo_oil` | 橘柚油 | 主成分 d-柠檬烯 C10H16 | 液 | — |  | 第五批 N304 | Tangelo Oil | 香料 | — | — |
| `tuberose_absolute` | 晚香玉净油 | 主成分苯甲酸苄酯 C14H12O2、邻氨基苯甲酸甲酯 C8H9NO2 | 液 | — |  | 第五批 N305 | Tuberose Absolute | 香料 | — | — |
| `chestnut_leaf_extract` | 美国栗树叶提取物 | 栗叶水提取物（鞣花鞣质为主） | 液 | — |  | 第五批 N306 | Chestnut Leaves Extract | 香料 | — | — |
| `copaiba_oil` | 古巴香脂油 | 主成分 β-石竹烯 C15H24 | 液 | — |  | 第五批 N307 | Copaiba Oil | 香料 | — | — |
| `damiana_leaf_powder` | 达迷草叶 | 干叶（挥发油、黄酮、熊果苷 C12H16O7） | 固 | — |  | 第五批 N308 | Damiana Leaves | 香料 | — | — |
| `hungarian_chamomile_absolute` | 母菊(匈牙利春黄菊)花净油 | 主成分 α-红没药醇氧化物 A C15H26O2，含烯炔双环醚 | 液 | — |  | 第五批 N309 | Chamomile Flower Absolute (Hungarian) | 香料 | — | — |
| `elderflower_extract` | 接骨木花提取物 | 接骨木花稀醇提取物（芳樟醇氧化物、玫瑰醚、芦丁） | 液 | — |  | 第五批 N310 | Elder Flowers Extract | 香料 | — | — |
| `opoponax_oil` | 防风根油 | 主成分倍半萜烯（α-檀香烯、红没药烯）C15H24 | 液 | — |  | 第五批 N311 | Opoponax Oil | 香料 | — | — |
| `saffron_extract` | 藏红花提取物 | 主成分藏花素 C44H64O24、藏花醛 C10H14O | 液 | — |  | 第五批 N312 | Saffron Extract | 香料 | #C04A10 | — |
| `geranium_extract` | 香叶提取物 | 主成分香茅醇 C10H20O、香叶醇 C10H18O | 液 | — |  | 第五批 N313 | Geranium Extract | 香料 | — | — |
| `fenugreek_oleoresin` | 葫芦巴油树脂 | 主成分葫芦巴内酯 C6H8O3，含脂肪油 | 液 | — |  | 第五批 N314 | Fenugreek Oleoresin | 香料 | — | — |
| `lemon_extract` | 柠檬提取物 | 主成分 d-柠檬烯 C10H16、柠檬醛 C10H16O（乙醇溶液） | 溶液 | — |  | 第五批 N315 | Lemon Extract | 香料 | — | — |
| `orris_resinoid` | 德国鸢尾树脂 | 主成分鸢尾酮 C14H22O，含肉豆蔻酸 C14H28O2 | 固 | — |  | 第五批 N316 | Orris Resinoid | 香料 | — | — |
| `tamarind_extract` | 罗望子提取物(浸膏) | 主成分酒石酸 C4H6O6 与糖 | 固 | — |  | 第五批 N317 | Tamarind Extract | 香料 | — | — |
| `horseradish_oil` | 辣根油 | 主成分异硫氰酸烯丙酯 C4H5NS | 液 | — |  | 第五批 N318 | Horseradish Oil | 香料 | — | — |
| `fenugreek_seed_extract` | 葫芦巴籽浸膏 | 主成分葫芦巴内酯 C6H8O3、葫芦巴碱 C7H7NO2 | 固 | — |  | 第五批 N319 | Fenugreek Seed Extract | 香料 | — | — |
| `celery_leaf_oil` | 芹菜叶油 | 主成分 d-柠檬烯 C10H16、月桂烯，含 3-正丁基苯酞 | 液 | — |  | 第五批 N320 | Celery Leaf Oil | 香料 | — | — |
| `cedarwood_oil_terpenes` | 柏木油萜烯 | 主成分 α-柏木烯 C15H24、罗汉柏烯 | 液 | — |  | 第五批 N321 | Cedarwood Oil Terpenes | 香料 | — | — |
| `nutmeg_oleoresin` | 肉豆蔻油树脂 | 主成分桧烯 C10H16、肉豆蔻醚 C11H12O3，含脂肪油 | 液 | — |  | 第五批 N322 | Nutmeg Oleoresin | 香料 | — | — |
| `coriander_oleoresin` | 芫荽油/油树脂 | 主成分芳樟醇 C10H18O | 液 | — |  | 第五批 N324 | Coriander Oil/oleoresin | 香料 | — | — |
| `leek_oil` | 韭葱油 | 主成分二丙基二硫醚 C6H14S2 等含硫化合物 | 液 | — |  | 第五批 N326 | Leek Oil | 香料 | — | — |
| `sweet_orange_peel_extract` | 甜橙皮提取物 | 主成分 d-柠檬烯 C10H16（乙醇溶液） | 溶液 | — |  | 第五批 N327 | Orange Peel Extract, Sweet | 香料 | — | — |
| `yuzu_peel_oil` | 香橙皮油 | 主成分 d-柠檬烯 C10H16，含 γ-萜品烯 | 液 | — |  | 第五批 N329 | Citrus Junos Peel Oil (Yuzu Oil) | 香料 | — | — |
| `algues_absolute` | 海藻净油 | 褐藻浸膏的乙醇可溶部分（脂肪酸、甾醇、溴酚类） | 液 | — |  | 第五批 N330 | Algues Absolute (Seaweed Absolute) | 香料 | — | — |
| `mexican_oregano_oleoresin` | 墨西哥鼠尾草油树脂（棘枝油树脂） | 主成分香芹酚 C10H14O，含百里香酚 | 液 | — |  | 第五批 N331 | Oregano Oleoresin (Mexican, Lippia) | 香料 | — | — |
| `winter_savory_oil` | 冬香草油 | 主成分香芹酚 C10H14O | 液 | — |  | 第五批 N333 | Savory Oil, Winter | 香料 | — | — |
| `styrax` | 苏合香 | 主成分肉桂酸 C9H8O2 及其酯（肉桂酸肉桂酯 C18H16O2），含树脂 | 液 | — |  | 第五批 N334 | Styrax | 香料 | — | — |
| `asafoetida_fluid_extract` | 阿魏液态提取物(流浸膏) | 主成分仲丁基丙烯基二硫醚 C7H14S2，含阿魏酸酯 | 溶液 | — |  | 第五批 N335 | Asafoetida Fluid Extract | 香料 | — | — |
| `peach_leaf_absolute` | 桃树叶净油 | 主成分苯甲醛 C7H6O | 液 | — |  | 第五批 N336 | Peach Tree Leaf Absolute | 香料 | — | — |
| `dittany_of_crete_powder` | 白藓牛至 | 干全草（挥发油以香芹酚 C10H14O 为主） | 固 | — |  | 第五批 N337 | Dittany of Crete | 香料 | — | — |
| `hop_oil` | 酒花油 | 主成分月桂烯 C10H16、葎草烯 C15H24 | 液 | — |  | 第五批 N338 | Hops Oil | 香料 | — | — |
| `labdanum_oil` | 赖百当油 | 主成分 α-蒎烯 C10H16 | 液 | — |  | 第五批 N339 | Labdanum Oil | 香料 | — | — |
| `lavender_absolute` | 薰衣草净油 | 主成分乙酸芳樟酯 C12H20O2、芳樟醇 C10H18O | 液 | — |  | 第五批 N340 | Lavender Absolute | 香料 | — | — |
| `opoponax_resinoid` | 没药树脂提取物 | 甜没药树脂的乙醇可溶部分（倍半萜烯、树脂） | 固 | — |  | 第五批 N341 | Opoponax Resinoid | 香料 | — | — |
| `prickly_ash_extract` | 花椒提取物 | 主成分羟基-α-山椒素 C16H25NO2，含挥发油 | 液 | — |  | 第五批 N342 | Prickly Ash Extract | 香料 | — | — |
| `catechu_powder` | 儿茶粉 | 主成分儿茶素 C15H14O6、儿茶鞣质 | 固 | — |  | 第五批 N344 | Catechu Powder | 香料 | — | — |
| `wormwood_powder` | 苦艾 | 干全草（苦艾素 C30H40O6、侧柏酮） | 固 | — |  | 第五批 N345 | Wormwood | 香料 | — | — |
| `wormwood_extract` | 苦艾提取物 | 主成分苦艾素 C30H40O6 | 液 | — |  | 第五批 N348 | Wormwood Extract | 香料 | — | — |
| `juniper_extract` | 刺柏提取物 | 主成分 α-蒎烯 C10H16（挥发部分），含糖 | 液 | — |  | 第五批 N349 | Juniper Extract | 香料 | — | — |
| `licorice_extract_powder` | 甘草提取物(粉) | 主成分甘草酸 C42H62O16 | 固 | — |  | 第五批 N350 | Licorice Extract Powder | 香料 | — | — |
| `betaine` | 甜菜碱(天然提取) | `C5H11NO2` | 固 | — | ✓ | 第五批 N351 | Betaine (Natural Extract) | 香料 | — | K3 |
| `pine_tar_oil` | 松焦油 | 松焦油精馏馏分（萜烯、愈创木酚 C7H8O2、甲酚） | 液 | — |  | 第五批 N352 | Pine Tar Oil | 香料 | — | — |
| `oakmoss_absolute` | 橡苔净油 | 主成分 β-苔色酸甲酯 C10H12O4，含地衣酸类 | 液 | — |  | 第五批 N353 | Oakmoss Absolute | 香料 | — | — |
| `scotch_spearmint_oil` | 苏格兰留兰香油 | 主成分 l-香芹酮 C10H14O | 液 | — |  | 第五批 N354 | Scotch Spearmint Oil | 香料 | — | — |
| `hyssop_extract` | 海索草提取物（神香草提取物） | 主成分松樟酮 C10H16O，含迷迭香酸 | 液 | — |  | 第五批 N355 | Hyssop Extract | 香料 | — | — |
| `angostura_extract` | 安古树皮提取物 | 主成分喹啉生物碱（加利平 C20H21NO3） | 液 | — |  | 第五批 N356 | Angostura Extract | 香料 | — | — |
| `german_chamomile_extract` | 德国春黄菊花(母菊花)提取物 | 主成分芹菜素 C15H10O5，含 α-红没药醇 | 液 | — |  | 第五批 N357 | Chamomile (German) Extract | 香料 | — | — |
| `l_threonine` | L-苏氨酸 | `C4H9NO3` | 固 | — | ✓ | 第五批 N359 | L-Threonine | 香料 | — | K3 |
| `l_serine` | L-丝氨酸 | `C3H7NO3` | 固 | — | ✓ | 第五批 N360 | L-Serine | 香料 | — | K3 |
| `civet_absolute` | 灵猫净油 | 主成分灵猫酮 C17H30O，含粪臭素 | 液 | — |  | 第五批 N361 | Civet Absolute | 香料 | — | — |
| `cascarilla_bark_extract` | 卡黎皮提取物 | 卡黎皮稀醇提取物（克罗烷二萜苦味质、挥发油） | 液 | — |  | 第五批 N363 | Cascarilla Bark Extract | 香料 | — | — |
| `cinnamon_bark_oleoresin` | 肉桂皮油/油树脂 | 主成分肉桂醛 C9H8O | 液 | — |  | 第五批 N364 | Cinnamon Bark Oil/oleoresin | 香料 | — | — |
| `petitgrain_mandarin_oil` | 橘叶油 | 主成分 N-甲基邻氨基苯甲酸甲酯 C9H11NO2，含 γ-萜品烯 | 液 | — |  | 第五批 N366 | Petitgrain Mandarin Oil | 香料 | — | — |
| `dwarf_pine_needle_oil` | 欧洲山松针叶油 | 主成分 α-蒎烯 C10H16、δ-3-蒈烯 | 液 | — |  | 第五批 N367 | Dwarf Pine Needle Oil | 香料 | — | — |
| `rose_hip_extract` | 玫瑰果籽提取物 | 玫瑰果水提取物（有机酸、糖、抗坏血酸 C6H8O6） | 液 | — |  | 第五批 N368 | Rose Hips Extract | 香料 | — | — |
| `summer_savory_oil` | 夏香草油 | 主成分香芹酚 C10H14O，含 γ-萜品烯 | 液 | — |  | 第五批 N369 | Savory Oil, Summer | 香料 | — | — |
| `canadian_snakeroot_oil` | 加拿大细辛油 | 主成分甲基丁香酚 C11H14O2 | 液 | — |  | 第五批 N370 | Snakeroot Oil, Canadian | 香料 | — | — |
| `tragacanth_gum` | 黄蓍胶 | 酸性多糖（黄蓍胶酸、阿拉伯半乳聚糖） | 固 | — |  | 第五批 N372 | Tragacanth Gum | 香料 | — | K6 |
| `marjoram_oleoresin` | 甘牛至油树脂/提取物 | 主成分萜品烯-4-醇 C10H18O | 液 | — |  | 第五批 N373 | Marjoram Oleoresin/extract | 香料 | — | — |
| `grains_of_paradise_extract` | 摩洛哥豆蔻提取物 | 主成分 6-姜辣烯酮（6-paradol）C17H26O3，含 6-姜酚 | 液 | — |  | 第五批 N374 | Grains of Paradise Extract | 香料 | — | — |
| `hesperetin` | 橙皮素 | `C16H14O6` | 固 | — | ✓ | 第五批 N375 | Hesperetin | 香料 | — | K3 |
| `sesame_co2_extract` | 芝麻(CO2)提取物 | 焙炒芝麻 CO2 提取物（甘油三酯为主，含吡嗪、2-糠硫醇等香气成分） | 液 | — |  | 第五批 N377 | Sesame CO2 Extract | 香料 | — | — |
| `sesame_distillate` | 芝麻蒸馏物 | 焙炒芝麻水蒸气蒸馏香气馏分（吡嗪类、2-糠硫醇、愈创木酚） | 液 | — |  | 第五批 N378 | Sesame Distillate | 香料 | — | — |
| `katsuobushi_co2_extract` | 干制鲣鱼(CO2)提取物 | 鲣节 CO2 提取物（鱼脂为主，含烟熏酚类、吡嗪、含硫香气成分） | 液 | — |  | 第五批 N379 | Katsuobushi CO2 Extract | 香料 | — | — |
| `rum_absolute` | 朗姆酒净油 | 朗姆酒香气浓缩物（酯类、高级醇、β-大马酮） | 液 | — |  | 第五批 N380 | Rum Absolute | 香料 | — | — |
| `douchi_oleoresin` | 豆豉油树脂 | 豆豉溶剂提取物（吡嗪、酯类、脂肪酸） | 液 | — |  | 第五批 N381 | Toushi Oleoresin (Douchi Oleoresin) | 香料 | — | — |
| `althea_root_powder` | 药蜀葵 | 干根（黏液多糖、果胶、天冬酰胺） | 固 | — |  | 第五批 N382 | Althea Root | 香料 | — | — |
| `lemon_balm_powder` | 香蜂草 | 干茎叶（柠檬醛、香茅醛、迷迭香酸） | 固 | — |  | 第五批 N383 | Balm (Lemon Balm) | 香料 | — | — |
| `cajeput_oil` | 白千层油 | 主成分 1,8-桉叶素 C10H18O | 液 | — |  | 第五批 N384 | Cajeput Oil | 香料 | — | — |
| `cubeb_powder` | 毕澄茄 | 干未熟果（挥发油桧烯、荜澄茄烯；木脂素荜澄茄脂素 C20H20O6） | 固 | — |  | 第五批 N388 | Cubebs | 香料 | — | — |
| `aloe_extract` | 芦荟提取物 | 主成分芦荟苷 C21H22O9 | 液 | — |  | 第五批 N389 | Aloe Extract | 香料 | — | — |
| `ambergris_tincture` | 龙涎香酊 | 主成分龙涎香醇 C30H52O（乙醇溶液） | 溶液 | — |  | 第五批 N390 | Ambergris Tincture | 香料 | — | — |
| `ambrette_tincture` | 黄葵酊 | 主成分黄葵内酯 C16H28O2（乙醇溶液） | 溶液 | — |  | 第五批 N391 | Ambrette Tincture | 香料 | — | — |
| `swallowroot_extract` | 燕根(萝藦科植物)提取物 | 主成分 2-羟基-4-甲氧基苯甲醛 C8H8O3 | 液 | — |  | 第五批 N392 | Swallowroot Extract | 香料 | — | — |
| `jujube_concrete` | 红枣浸膏 | 红枣稀醇提取浓缩物（糖、有机酸、美拉德香气成分） | 固 | — |  | 第五批 N393 | Date Concrete (Jujube) | 香料 | — | — |
| `folded_apple_essence` | 高倍天然苹果香料 | 苹果汁回收香气的水溶液（乙酸丁酯、己醛、反-2-己烯醛、乙醇） | 溶液 | — |  | 第五批 N394 | Folded Apple Essence | 香料 | — | — |
| `beta_guaiene` | β-愈疮木烯 | `C15H24` | 液 | — | ✓ | 第五批 N395 | β-Guaiene | 香料 | — | K4 |
| `massoia_bark_oil` | 香厚壳桂皮油 | 主成分 C10-马索亚内酯 C10H16O2 | 液 | — |  | 第五批 N397 | Massoia Bark Oil | 香料 | — | — |
| `homoeriodictyol_sodium` | (—)-高圣草酚钠盐 | `C16H13NaO6` | 固 | — | ✓ | 第五批 N398 | (−)-Homoeriodictyol Sodium Salt | 香料 | — | K3 |
| `enzymatically_modified_isoquercitrin` | 酶处理异槲皮苷 | α-葡萄糖基异槲皮苷混合物，以异槲皮苷 C21H20O12 计 | 固 | — |  | 第五批 N399 | Isoquercitrin, Enzymatically Modified | 香料 | — | K5 |
| `grape_seed_extract` | 葡萄籽提取物 | 原花青素（儿茶素、表儿茶素低聚物），以原花青素 B2 C30H26O12 计 | 固 | — |  | 第五批 N400 | Grape Seed Extract | 香料 | — | K5 |
| `spearmint_extract` | 留兰香提取物 | 主成分 l-香芹酮 C10H14O | 液 | — |  | 第五批 N401 | Spearmint Extract | 香料 | — | — |
| `fusel_oil` | 杂醇油(精制过) | 主成分异戊醇 C5H12O，含 2-甲基-1-丁醇、异丁醇 | 液 | — |  | 第五批 N402 | Fusel Oil, Refined | 香料 | — | — |
| `glucosyl_steviol_glycosides` | 葡萄糖基甜菊糖苷 | α-葡萄糖基化甜菊糖苷混合物，以甜菊苷 C38H60O18 计 | 固 | — |  | 第五批 N403 | Glucosyl Steviol Glycosides | 香料 | — | K5 |
| `katemfe_fruit_extract` | 非洲竹芋提取物 | 主成分索马甜（甜味蛋白质） | 液 | — |  | 第五批 N404 | Katemfe Fruit Extract | 香料 | — | — |
| `refined_wood_vinegar` | 焦木酸 | 乙酸、酚类、羰基化合物的水溶液（木醋液除去焦油与多环芳烃） | 液 | — |  | 第五批 N267 | Pyroligneous Acid | 香料 | #A0602A | — |

### E.2 合成香料（表 B.3）

| 建议 ID | 名称 | 化学式 / 组成 | 状态 | GTCEu 材料 | 核对 | 备注 | 英文名（待审） | 分类 | 颜色 | 纯度类别 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `isobutanol` | 异丁醇 | `C4H10O` | 液 | — | ✓ | 第五批 S0005 | Isobutyl Alcohol | 香料 | — | K4 |
| `n_pentanol` | 正戊醇 | `C5H12O` | 液 | — | ✓ | 第五批 S0006 | 1-Pentanol (Amyl Alcohol) | 香料 | — | K4 |
| `pentan_2_ol` | 2-戊醇 | `C5H12O` | 液 | — | ✓ | 第五批 S0007 | 2-Pentanol | 香料 | — | K4 |
| `pent_1_en_3_ol` | 1-戊烯-3-醇 | `C5H10O` | 液 | — | ✓ | 第五批 S0009 | 1-Penten-3-ol | 香料 | — | K4 |
| `n_hexanol` | 正己醇 | `C6H14O` | 液 | — | ✓ | 第五批 S0010 | 1-Hexanol (Hexyl Alcohol) | 香料 | — | K4 |
| `trans_2_hexenol` | 2-己烯-1-醇 | `C6H12O` | 液 | — | ✓ | 第五批 S0011 | 2-Hexen-1-ol | 香料 | — | K4 |
| `hex_4_en_1_ol` | 4-己烯-1-醇 | `C6H12O` | 液 | — | ✓ | 第五批 S0012 | 4-Hexen-1-ol | 香料 | — | K4 |
| `n_heptanol` | 正庚醇 | `C7H16O` | 液 | — | ✓ | 第五批 S0013 | 1-Heptanol (Heptyl Alcohol) | 香料 | — | K4 |
| `n_octanol` | 正辛醇 | `C8H18O` | 液 | — | ✓ | 第五批 S0014 | 1-Octanol (Octyl Alcohol) | 香料 | — | K4 |
| `octan_2_ol` | 2-辛醇 | `C8H18O` | 液 | — | ✓ | 第五批 S0015 | 2-Octanol | 香料 | — | K4 |
| `oct_1_en_3_ol` | 1-辛烯-3-醇 | `C8H16O` | 液 | — | ✓ | 第五批 S0016 | 1-Octen-3-ol | 香料 | — | K4 |
| `cis_5_octenol` | 顺式-5-辛烯-1-醇 | `C8H16O` | 液 | — | ✓ | 第五批 S0017 | cis-5-Octen-1-ol | 香料 | — | K4 |
| `n_nonanol` | 正壬醇 | `C9H20O` | 液 | — | ✓ | 第五批 S0018 | 1-Nonanol (Nonyl Alcohol) | 香料 | — | K4 |
| `cis_6_nonenol` | 顺式-6-壬烯-1-醇 | `C9H18O` | 液 | — | ✓ | 第五批 S0019 | cis-6-Nonen-1-ol | 香料 | — | K4 |
| `trans_2_nonenol` | 反式-2-壬烯-1-醇 | `C9H18O` | 液 | — | ✓ | 第五批 S0020 | trans-2-Nonen-1-ol | 香料 | — | K4 |
| `trans_2_cis_6_nonadienol` | 2,6-壬二烯-1-醇 | `C9H16O` | 液 | — | ✓ | 第五批 S0021 | 2,6-Nonadien-1-ol | 香料 | — | K4 |
| `n_decanol` | 正癸醇 | `C10H22O` | 液 | — | ✓ | 第五批 S0022 | 1-Decanol (Decyl Alcohol) | 香料 | — | K4 |
| `n_undecanol` | 十一醇 | `C11H24O` | 液 | — | ✓ | 第五批 S0023 | Undecyl Alcohol | 香料 | — | K4 |
| `cetyl_alcohol` | 1-十六醇 | `C16H34O` | 固 | — | ✓ | 第五批 S0025 | 1-Hexadecanol | 香料 | — | K7 |
| `fenchol` | 小茴香醇 | `C10H18O` | 固 | — | ✓ | 第五批 S0026 | Fenchyl Alcohol | 香料 | — | K3 |
| `leaf_alcohol` | 叶醇 | `C6H12O` | 液 | — | ✓ | 第五批 S0027 | Leaf Alcohol (cis-3-Hexen-1-ol) | 香料 | — | K4 |
| `borneol` | 龙脑 | `C10H18O` | 固 | — | ✓ | 第五批 S0028 | Borneol | 香料 | — | K3 |
| `linalool` | 芳樟醇 | `C10H18O` | 液 | — | ✓ | 第五批 S0029 | Linalool | 香料 | — | K4 |
| `linalool_oxide` | 氧化芳樟醇 | `C10H18O2` | 液 | — | ✓ | 第五批 S0030 | Linalool Oxide | 香料 | — | K4 |
| `isopulegol` | 异胡薄荷醇 | `C10H18O` | 液 | — | ✓ | 第五批 S0031 | Isopulegol | 香料 | — | K4 |
| `styralyl_alcohol` | 苏合香醇 | `C8H10O` | 液 | — | ✓ | 第五批 S0032 | Styralyl Alcohol (α-Methylbenzyl Alcohol) | 香料 | — | K4 |
| `benzyl_alcohol` | 苯甲醇 | `C7H8O` | 液 | — | ✓ | 第五批 S0033 | Benzyl Alcohol | 香料 | — | K4 |
| `phenethyl_alcohol` | 苯乙醇 | `C8H10O` | 液 | — | ✓ | 第五批 S0034 | Phenethyl Alcohol | 香料 | — | K4 |
| `phenylpropyl_alcohol` | 苯丙醇 | `C9H12O` | 液 | — | ✓ | 第五批 S0035 | Phenylpropyl Alcohol | 香料 | — | K4 |
| `rhodinol` | 玫瑰醇 | 主成分 l-香茅醇 C10H20O、香叶醇 C10H18O | 液 | — |  | 第五批 S0036 | Rhodinol | 香料 | — | — |
| `alpha_terpineol` | α-松油醇 | `C10H18O` | 固 | — | ✓ | 第五批 S0037 | α-Terpineol | 香料 | — | K3 |
| `farnesol` | 金合欢醇 | `C15H26O` | 液 | — | ✓ | 第五批 S0038 | Farnesol | 香料 | — | K4 |
| `geraniol` | 香叶醇 | `C10H18O` | 液 | — | ✓ | 第五批 S0039 | Geraniol | 香料 | — | K4 |
| `dl_citronellol` | dl-香茅醇 | `C10H20O` | 液 | — | ✓ | 第五批 S0040 | dl-Citronellol | 香料 | — | K4 |
| `anisyl_alcohol` | 茴香醇 | `C8H10O2` | 液 | — | ✓ | 第五批 S0041 | Anisyl Alcohol | 香料 | — | K4 |
| `cinnamyl_alcohol` | 肉桂醇 | `C9H10O` | 固 | — | ✓ | 第五批 S0042 | Cinnamic Alcohol | 香料 | — | K3 |
| `alpha_ionol` | α-紫罗兰醇（甲位紫罗兰醇） | `C13H22O` | 液 | — | ✓ | 第五批 S0043 | α-Ionol | 香料 | — | K4 |
| `beta_ionol` | β-紫罗兰醇（乙位紫罗兰醇） | `C13H22O` | 液 | — | ✓ | 第五批 S0044 | β-Ionol | 香料 | — | K4 |
| `dihydro_beta_ionol` | 二氢-β-紫罗兰醇 | `C13H24O` | 液 | — | ✓ | 第五批 S0045 | Dihydro-β-ionol | 香料 | — | K4 |
| `nerol` | 橙花醇 | `C10H18O` | 液 | — | ✓ | 第五批 S0046 | Nerol | 香料 | — | K4 |
| `nerolidol` | 橙花叔醇 | `C15H26O` | 液 | — | ✓ | 第五批 S0047 | Nerolidol | 香料 | — | K4 |
| `dimethylbenzylcarbinol` | 二甲基苄基原醇 | `C10H14O` | 液 | — | ✓ | 第五批 S0048 | Dimethyl Benzyl Carbinol | 香料 | — | K4 |
| `hexan_3_ol` | 3-己醇 | `C6H14O` | 液 | — | ✓ | 第五批 S0050 | 3-Hexanol | 香料 | — | K4 |
| `hex_1_en_3_ol` | 1-己烯-3-醇 | `C6H12O` | 液 | — | ✓ | 第五批 S0051 | 1-Hexen-3-ol | 香料 | — | K4 |
| `ethylhexanol` | 2-乙基己醇 | `C8H18O` | 液 | — | ✓ | 第五批 S0052 | 2-Ethyl-1-hexanol | 香料 | — | K4 |
| `heptan_2_ol` | 2-庚醇 | `C7H16O` | 液 | — | ✓ | 第五批 S0053 | 2-Heptanol | 香料 | — | K4 |
| `octan_3_ol` | 3-辛醇 | `C8H18O` | 液 | — | ✓ | 第五批 S0054 | 3-Octanol | 香料 | — | K4 |
| `cis_3_octenol` | 顺式-3-辛烯-1-醇 | `C8H16O` | 液 | — | ✓ | 第五批 S0055 | cis-3-Octen-1-ol | 香料 | — | K4 |
| `undecan_2_ol` | 2-十一醇 | `C11H24O` | 液 | — | ✓ | 第五批 S0056 | 2-Undecanol | 香料 | — | K4 |
| `p_alpha_dimethylbenzyl_alcohol` | 对,α-二甲基苄醇 | `C9H12O` | 液 | — | ✓ | 第五批 S0057 | p,α-Dimethylbenzyl Alcohol | 香料 | — | K4 |
| `p_isopropylbenzyl_alcohol` | 对-异丙基苄醇 | `C10H14O` | 液 | — | ✓ | 第五批 S0058 | p-Isopropylbenzyl Alcohol | 香料 | — | K4 |
| `p_cymen_8_ol` | 对,α,α-三甲基苄醇 | `C10H14O` | 液 | — | ✓ | 第五批 S0059 | p,α,α-Trimethylbenzyl Alcohol | 香料 | — | K4 |
| `caryophyllene_alcohol` | β-石竹烯醇 | `C15H26O` | 固 | — | ✓ | 第五批 S0060 | β-Caryophyllene Alcohol | 香料 | — | K3 |
| `estragole` | 龙蒿脑 | `C10H12O` | 液 | — | ✓ | 第五批 S0061 | Estragole | 香料 | — | K4 |
| `tetrahydrogeraniol` | 四氢香叶醇 | `C10H22O` | 液 | — | ✓ | 第五批 S0062 | Tetrahydrogeraniol | 香料 | — | K4 |
| `dihydrocarveol` | 二氢香芹醇 | `C10H18O` | 液 | — | ✓ | 第五批 S0063 | Dihydrocarveol | 香料 | — | K4 |
| `terpinen_4_ol` | 1-对-䓝烯-4-醇 | `C10H18O` | 液 | — | ✓ | 第五批 S0064 | 1-p-Menthen-4-ol | 香料 | — | K4 |
| `perillyl_alcohol` | 紫苏醇 | `C10H16O` | 液 | — | ✓ | 第五批 S0065 | Perilla Alcohol | 香料 | — | K4 |
| `menthoxy_methylpropanediol` | 3-(l-薄荷烷氧基)-2-甲基-1,2-丙二醇 | `C14H28O3` | 液 | — | ✓ | 第五批 S0067 | 3-(l-Menthoxy)-2-methylpropane-1,2-diol | 香料 | — | K4 |
| `trimethylcyclohexanol` | 3,5,5-三甲基环己醇 | `C9H18O` | 固 | — | ✓ | 第五批 S0068 | 3,5,5-Trimethylcyclohexanol | 香料 | — | K3 |
| `cis_2_nonenol` | 顺式-2-壬烯-1-醇 | `C9H18O` | 液 | — | ✓ | 第五批 S0069 | cis-2-Nonen-1-ol | 香料 | — | K4 |
| `trans_trans_2_4_decadienol` | 反式,反式-2,4-癸二烯醇 | `C10H18O` | 液 | — | ✓ | 第五批 S0070 | (E,E)-2,4-Decadien-1-ol (trans,trans-2,4-Decadien-1-ol) | 香料 | — | K4 |
| `trans_2_octen_4_ol` | 反式-2-辛烯-4-醇 | `C8H16O` | 液 | — | ✓ | 第五批 S0071 | (E)-2-Octen-4-ol | 香料 | — | K4 |
| `terpinen_1_ol` | 对-䓝-3-烯-1-醇 | `C10H18O` | 液 | — | ✓ | 第五批 S0072 | p-Menth-3-en-1-ol | 香料 | — | K4 |
| `menthadienol` | 对-䓝-1,8(10)二烯-9-醇 | `C10H16O` | 液 | — | ✓ | 第五批 S0073 | Menthadienol [p-Mentha-1,8(10)-dien-9-ol] | 香料 | — | K4 |
| `cedrenol` | 柏木烯醇 | `C15H24O` | 液 | — | ✓ | 第五批 S0074 | Cedrenol | 香料 | — | K4 |
| `hotrienol` | 脱氢芳樟醇 | `C10H16O` | 液 | — | ✓ | 第五批 S0075 | Dehydrolinalool [(E)-3,7-Dimethyl-1,5,7-octatrien-3-ol] | 香料 | — | K4 |
| `d_ribose` | d-核糖 | `C5H10O5` | 固 | — | ✓ | 第五批 S0077 | D-Ribose | 香料 | — | K2 |
| `l_rhamnose` | l-鼠李糖 | `C6H12O5·H2O` | 固 | — | ✓ | 第五批 S0078 | L-Rhamnose | 香料 | — | K2 |
| `p_cresyl_methyl_ether` | 对-甲酚甲醚 | `C8H10O` | 液 | — | ✓ | 第五批 S0080 | p-Cresyl Methyl Ether | 香料 | — | K4 |
| `isoeugenyl_methyl_ether` | 异丁香酚甲醚 | `C11H14O2` | 液 | — | ✓ | 第五批 S0081 | Isoeugenyl Methyl Ether | 香料 | — | K4 |
| `methyl_phenethyl_ether` | 甲基苯乙醚 | `C9H12O` | 液 | — | ✓ | 第五批 S0082 | Methyl Phenethyl Ether | 香料 | — | K4 |
| `rum_ether` | 朗姆醚 | 混合物：乙酸乙酯、甲酸乙酯、乙醛、乙醛二乙缩醛等（气相色谱检出 22 种） | 液 | — |  | 第五批 S0083 | Rum Ether (Ethyl Oxyhydrate) | 香料 | — | — |
| `sec_butyl_ethyl_ether` | 仲丁基乙醚 | `C6H14O` | 液 | — | ✓ | 第五批 S0084 | sec-Butyl Ethyl Ether | 香料 | — | K4 |
| `ethyl_benzyl_ether` | 乙基苄基醚 | `C9H12O` | 液 | — | ✓ | 第五批 S0085 | Ethyl Benzyl Ether | 香料 | — | K4 |
| `anisole` | 大茴香醚 | `C7H8O` | 液 | — | ✓ | 第五批 S0086 | Anisole | 香料 | — | K4 |
| `o_methylanisole` | 邻-甲基大茴香醚 | `C8H10O` | 液 | — | ✓ | 第五批 S0087 | o-Methylanisole | 香料 | — | K4 |
| `nerol_oxide` | 橙花醚 | `C10H16O` | 液 | — | ✓ | 第五批 S0088 | Nerol Oxide | 香料 | — | K4 |
| `dimethylanisole_2_4` | 2,4-二甲基大茴香醚 | `C9H12O` | 液 | — | ✓ | 第五批 S0089 | 2,4-Dimethylanisole | 香料 | — | K4 |
| `vanillyl_ethyl_ether` | 香兰基乙醚 | `C10H14O3` | 液 | — | ✓ | 第五批 S0090 | Vanillyl Ethyl Ether | 香料 | — | K4 |
| `eugenol` | 丁香酚 | `C10H12O2` | 液 | — | ✓ | 第五批 S0091 | Eugenol | 香料 | — | K4 |
| `isoeugenol` | 异丁香酚 | `C10H12O2` | 液 | — | ✓ | 第五批 S0092 | Isoeugenol | 香料 | — | K4 |
| `methyl_eugenol` | 甲基丁香酚 | `C11H14O2` | 液 | — | ✓ | 第五批 S0093 | Methyleugenol | 香料 | — | K4 |
| `o_cresol` | 邻-甲酚 | `C7H8O` | 固 | — | ✓ | 第五批 S0095 | o-Cresol | 香料 | — | K3 |
| `creosol` | 2-甲氧基-4-甲基苯酚 | `C8H10O2` | 液 | — | ✓ | 第五批 S0100 | 2-Methoxy-4-methylphenol | 香料 | — | K4 |
| `p_ethylphenol` | 对-乙基苯酚 | `C8H10O` | 固 | — | ✓ | 第五批 S0101 | p-Ethylphenol | 香料 | — | K3 |
| `vinylguaiacol` | 2-甲氧基-4-乙烯基苯酚 | `C9H10O2` | 液 | — | ✓ | 第五批 S0102 | 2-Methoxy-4-vinylphenol | 香料 | — | K4 |
| `p_dimethoxybenzene` | 对-二甲氧基苯 | `C8H10O2` | 固 | — | ✓ | 第五批 S0103 | p-Dimethoxybenzene | 香料 | — | K3 |
| `ethylguaiacol` | 4-乙基愈疮木酚 | `C9H12O2` | 液 | — | ✓ | 第五批 S0105 | 4-Ethylguaiacol | 香料 | — | K4 |
| `benzaldehyde_propylene_glycol_acetal` | 苯甲醛丙二醇缩醛 | `C10H12O2` | 液 | — | ✓ | 第五批 S0106 | Benzaldehyde Propylene Glycol Acetal | 香料 | — | K4 |
| `o_isopropylphenol` | 2-异丙基苯酚 | `C9H12O` | 液 | — | ✓ | 第五批 S0107 | 2-Isopropylphenol | 香料 | — | K4 |
| `xylenol_2_6` | 2,6-二甲基苯酚 | `C8H10O` | 固 | — | ✓ | 第五批 S0108 | 2,6-Xylenol | 香料 | — | K3 |
| `syringol` | 2,6-二甲氧基苯酚 | `C8H10O3` | 固 | — | ✓ | 第五批 S0109 | 2,6-Dimethoxyphenol | 香料 | — | K3 |
| `carvacrol` | 香芹酚 | `C10H14O` | 液 | — | ✓ | 第五批 S0111 | Carvacrol | 香料 | — | K4 |
| `propylguaiacol` | 2-甲氧基-4-丙基苯酚 | `C10H14O2` | 液 | — | ✓ | 第五批 S0112 | 2-Methoxy-4-propylphenol | 香料 | — | K4 |
| `xylenol_2_5` | 2,5-二甲基苯酚 | `C8H10O` | 固 | — | ✓ | 第五批 S0113 | 2,5-Xylenol | 香料 | — | K3 |
| `p_vinylphenol` | 对-乙烯基苯酚 | `C8H8O` | 固 | — | ✓ | 第五批 S0114 | p-Vinylphenol | 香料 | — | K3 |
| `acetaldehyde_diethyl_acetal` | 乙醛二乙缩醛 | `C6H14O2` | 液 | — | ✓ | 第五批 S0116 | Acetaldehyde Diethyl Acetal | 香料 | — | K4 |
| `propionaldehyde` | 丙醛 | `C3H6O` | 液 | — | ✓ | 第五批 S0117 | Propionaldehyde | 香料 | — | K4 |
| `furylacrolein` | 3-(2-呋喃基)丙烯醛 | `C7H6O2` | 固 | — | ✓ | 第五批 S0118 | 3-(2-Furyl)acrolein | 香料 | — | K3 |
| `butyraldehyde` | 丁醛 | `C4H8O` | 液 | — | ✓ | 第五批 S0119 | Butyraldehyde | 香料 | — | K4 |
| `methylbutyraldehyde` | 2-甲基丁醛 | `C5H10O` | 液 | — | ✓ | 第五批 S0120 | 2-Methylbutyraldehyde | 香料 | — | K4 |
| `tiglaldehyde` | 2-甲基-2-丁烯醛 | `C5H8O` | 液 | — | ✓ | 第五批 S0121 | 2-Methyl-2-butenal | 香料 | — | K4 |
| `phenylbutenal` | 2-苯基-2-丁烯醛 | `C10H10O` | 液 | — | ✓ | 第五批 S0122 | 2-Phenyl-2-butenal | 香料 | — | K4 |
| `valeraldehyde` | 戊醛 | `C5H10O` | 液 | — | ✓ | 第五批 S0123 | Valeraldehyde | 香料 | — | K4 |
| `isovaleraldehyde` | 异戊醛 | `C5H10O` | 液 | — | ✓ | 第五批 S0124 | Isovaleraldehyde | 香料 | — | K4 |
| `methylvaleraldehyde` | 2-甲基戊醛 | `C6H12O` | 液 | — | ✓ | 第五批 S0125 | 2-Methylvaleraldehyde | 香料 | — | K4 |
| `trans_2_pentenal` | 2-戊烯醛 | `C5H8O` | 液 | — | ✓ | 第五批 S0126 | 2-Pentenal | 香料 | — | K4 |
| `methylpentenal` | 2-甲基-2-戊烯醛 | `C6H10O` | 液 | — | ✓ | 第五批 S0127 | 2-Methyl-2-pentenal | 香料 | — | K4 |
| `methylphenylpentenal` | 4-甲基-2-苯基-2-戊烯醛 | `C12H14O` | 液 | — | ✓ | 第五批 S0128 | 4-Methyl-2-phenyl-2-pentenal | 香料 | — | K4 |
| `pentadienal` | 2,4-戊二烯醛 | `C5H6O` | 液 | — | ✓ | 第五批 S0129 | 2,4-Pentadienal | 香料 | — | K4 |
| `hexanal` | 己醛 | `C6H12O` | 液 | — | ✓ | 第五批 S0130 | Hexanal | 香料 | — | K4 |
| `trans_2_hexenal` | 2-己烯醛（叶醛） | `C6H10O` | 液 | — | ✓ | 第五批 S0131 | 2-Hexenal (Leaf Aldehyde) | 香料 | — | K4 |
| `cis_3_hexenal` | 顺式-3-己烯醛 | `C6H10O` | 液 | — | ✓ | 第五批 S0132 | cis-3-Hexenal | 香料 | — | K4 |
| `methylphenylhexenal` | 5-甲基-2-苯基-2-己烯醛 | `C13H16O` | 液 | — | ✓ | 第五批 S0133 | 5-Methyl-2-phenyl-2-hexenal | 香料 | — | K4 |
| `isopropylmethylhexenal` | 2-异丙基-5-甲基-2-己烯醛 | `C10H18O` | 液 | — | ✓ | 第五批 S0134 | 2-Isopropyl-5-methyl-2-hexenal | 香料 | — | K4 |
| `trans_trans_2_4_hexadienal` | 反式,反式-2,4-己二烯醛 | `C6H8O` | 液 | — | ✓ | 第五批 S0135 | trans,trans-2,4-Hexadienal | 香料 | — | K4 |
| `heptanal` | 庚醛 | `C7H14O` | 液 | — | ✓ | 第五批 S0136 | Heptyl Aldehyde | 香料 | — | K4 |
| `cis_4_heptenal` | 4-庚烯醛 | `C7H12O` | 液 | — | ✓ | 第五批 S0137 | 4-Heptenal | 香料 | — | K4 |
| `trans_2_heptenal` | 反式-2-庚烯醛 | `C7H12O` | 液 | — | ✓ | 第五批 S0138 | trans-2-Heptenal | 香料 | — | K4 |
| `dimethylheptenal` | 2,6-二甲基-5-庚烯醛 | `C9H16O` | 液 | — | ✓ | 第五批 S0139 | 2,6-Dimethyl-5-heptenal | 香料 | — | K4 |
| `trans_trans_2_4_heptadienal` | 2,4-庚二烯醛 | `C7H10O` | 液 | — | ✓ | 第五批 S0140 | 2,4-Heptadienal | 香料 | — | K4 |
| `octanal` | 辛醛 | `C8H16O` | 液 | — | ✓ | 第五批 S0141 | Octyl Aldehyde | 香料 | — | K4 |
| `trans_2_octenal` | 2-辛烯醛 | `C8H14O` | 液 | — | ✓ | 第五批 S0142 | 2-Octenal | 香料 | — | K4 |
| `trans_trans_2_4_octadienal` | 反式,反式-2,4-辛二烯醛 | `C8H12O` | 液 | — | ✓ | 第五批 S0143 | trans,trans-2,4-Octadienal | 香料 | — | K4 |
| `trans_trans_2_6_octadienal` | 反式,反式-2,6-辛二烯醛 | `C8H12O` | 液 | — | ✓ | 第五批 S0144 | trans,trans-2,6-Octadienal | 香料 | — | K4 |
| `nonanal` | 壬醛 | `C9H18O` | 液 | — | ✓ | 第五批 S0145 | Nonanal | 香料 | — | K4 |
| `methylundecanal` | 甲基壬乙醛 | `C12H24O` | 液 | — | ✓ | 第五批 S0146 | Methyl Nonyl Acetaldehyde (2-Methylundecanal) | 香料 | — | K4 |
| `trans_2_nonenal` | 2-壬烯醛 | `C9H16O` | 液 | — | ✓ | 第五批 S0147 | 2-Nonenal | 香料 | — | K4 |
| `cis_6_nonenal` | 顺式-6-壬烯醛 | `C9H16O` | 液 | — | ✓ | 第五批 S0148 | cis-6-Nonenal | 香料 | — | K4 |
| `trans_trans_2_4_nonadienal` | 反式-2-反式-4-壬二烯醛 | `C9H14O` | 液 | — | ✓ | 第五批 S0149 | 2,4-Nonadienal (trans-2-trans-4-Nonadienal) | 香料 | — | K4 |
| `trans_2_cis_6_nonadienal` | 反式-2-顺式-6-壬二烯醛 | `C9H14O` | 液 | — | ✓ | 第五批 S0150 | Nona-2-trans-6-cis-dienal | 香料 | — | K4 |
| `myrtenyl_formate` | 甲酸桃金娘烯酯 | `C11H16O2` | 液 | — | ✓ | 第五批 S0151 | Myrtenyl Formate | 香料 | — | K4 |
| `decanal` | 正癸醛（癸醛） | `C10H20O` | 液 | — | ✓ | 第五批 S0152 | n-Decyl Aldehyde (Decanal) | 香料 | — | K4 |
| `trans_2_decenal` | 2-癸烯醛 | `C10H18O` | 液 | — | ✓ | 第五批 S0153 | 2-Decenal | 香料 | — | K4 |
| `trans_trans_2_4_decadienal` | 2,4-癸二烯醛 | `C10H16O` | 液 | — | ✓ | 第五批 S0154 | 2,4-Decadienal | 香料 | — | K4 |
| `undecanal` | 十一醛 | `C11H22O` | 液 | — | ✓ | 第五批 S0155 | Undecanal | 香料 | — | K4 |
| `trans_2_undecenal` | 2-十一烯醛 | `C11H20O` | 液 | — | ✓ | 第五批 S0156 | 2-Undecenal | 香料 | — | K4 |
| `trans_trans_2_4_undecadienal` | 2,4-十一碳二烯醛 | `C11H18O` | 液 | — | ✓ | 第五批 S0157 | 2,4-Undecadienal | 香料 | — | K4 |
| `dodecanal` | 月桂醛 | `C12H24O` | 液 | — | ✓ | 第五批 S0158 | Lauric Aldehyde | 香料 | — | K4 |
| `trans_2_dodecenal` | 2-十二碳烯醛 | `C12H22O` | 液 | — | ✓ | 第五批 S0159 | 2-Dodecenal | 香料 | — | K4 |
| `trans_2_cis_6_dodecadienal` | 反式-2-顺式-6-十二碳二烯醛 | `C12H20O` | 液 | — | ✓ | 第五批 S0160 | 2-trans-6-cis-Dodecadienal | 香料 | — | K4 |
| `tetradecanal` | 十四醛 | `C14H28O` | 固 | — | ✓ | 第五批 S0161 | Tetradecyl Aldehyde | 香料 | — | K3 |
| `gamma_undecalactone` | 桃醛 | `C11H20O2` | 液 | — | ✓ | 第五批 S0162 | Peach Aldehyde (γ-Undecalactone) | 香料 | — | K4 |
| `p_anisaldehyde` | 大茴香醛 | `C8H8O2` | 液 | — | ✓ | 第五批 S0163 | p-Anisaldehyde | 香料 | — | K4 |
| `salicylaldehyde` | 水杨醛 | `C7H6O2` | 液 | — | ✓ | 第五批 S0164 | Salicylaldehyde | 香料 | — | K4 |
| `tolualdehydes` | 甲基苯甲醛(邻、对、间位混合物) | C8H8O（邻、间、对位异构体混合物） | 液 | — |  | 第五批 S0166 | Tolualdehydes (Mixed o, m, p) | 香料 | — | K4 |
| `veratraldehyde` | 3,4-二甲氧基苯甲醛 | `C9H10O3` | 固 | — | ✓ | 第五批 S0167 | 3,4-Dimethoxybenzenecarbonal | 香料 | — | K3 |
| `phenylacetaldehyde` | 苯乙醛 | `C8H8O` | 液 | — | ✓ | 第五批 S0168 | Phenylacetaldehyde | 香料 | — | K4 |
| `phenylacetaldehyde_dimethyl_acetal` | 苯乙醛二甲缩醛 | `C10H14O2` | 液 | — | ✓ | 第五批 S0169 | Phenylacetaldehyde Dimethyl Acetal | 香料 | — | K4 |
| `hydrocinnamaldehyde` | 苯丙醛 | `C9H10O` | 液 | — | ✓ | 第五批 S0170 | Phenylpropyl Aldehyde (3-Phenylpropionaldehyde) | 香料 | — | K4 |
| `cuminaldehyde` | 枯茗醛 | `C10H12O` | 液 | — | ✓ | 第五批 S0171 | Cuminaldehyde | 香料 | — | K4 |
| `citronellal` | 香茅醛 | `C10H18O` | 液 | — | ✓ | 第五批 S0173 | Citronellal | 香料 | — | K4 |
| `piperonal` | 洋茉莉醛（胡椒醛） | `C8H6O3` | 固 | — | ✓ | 第五批 S0175 | Heliotropin (Piperonal) | 香料 | — | K3 |
| `cinnamaldehyde_ethylene_glycol_acetal` | 乙二醇缩肉桂醛 | `C11H12O2` | 液 | — | ✓ | 第五批 S0177 | Cinnamaldehyde Ethylene Glycol Acetal | 香料 | — | K4 |
| `perillaldehyde` | 紫苏醛 | `C10H14O` | 液 | — | ✓ | 第五批 S0178 | Perillaldehyde | 香料 | — | K4 |
| `p_menth_1_en_9_al` | 对-䓝-1-烯-9-醛 | `C10H16O` | 液 | — | ✓ | 第五批 S0179 | p-Menth-1-en-9-al | 香料 | — | K4 |
| `methylfurfural` | 5-甲基糠醛 | `C6H6O2` | 液 | — | ✓ | 第五批 S0181 | 5-Methylfurfural | 香料 | — | K4 |
| `acetaldehyde_dimethyl_acetal` | 1,1-二甲氧基乙烷 | `C4H10O2` | 液 | — | ✓ | 第五批 S0182 | 1,1-Dimethoxyethane | 香料 | — | K4 |
| `safranal` | 2,6,6-三甲基环己-1,3-二烯基甲醛 | `C10H14O` | 液 | — | ✓ | 第五批 S0183 | (2,6,6-Trimethylcyclohexa-1,3-dienyl)methanal | 香料 | — | K4 |
| `isobutyraldehyde` | 异丁醛 | `C4H8O` | 液 | — | ✓ | 第五批 S0184 | Isobutyraldehyde | 香料 | — | K4 |
| `cis_4_hexenal` | 顺式-4-己烯醛 | `C6H10O` | 液 | — | ✓ | 第五批 S0185 | cis-4-Hexenal | 香料 | — | K4 |
| `cis_5_octenal` | 顺式-5-辛烯醛 | `C8H14O` | 液 | — | ✓ | 第五批 S0186 | cis-5-Octenal | 香料 | — | K4 |
| `cis_4_decenal` | 4-癸烯醛 | `C10H18O` | 液 | — | ✓ | 第五批 S0187 | 4-Decenal | 香料 | — | K4 |
| `trans_trans_2_4_dodecadienal` | 反式,反式-2,4-十二碳二烯醛 | `C12H20O` | 液 | — | ✓ | 第五批 S0188 | trans,trans-2,4-Dodecadienal | 香料 | — | K4 |
| `trans_2_tridecenal` | 2-十三烯醛 | `C13H24O` | 液 | — | ✓ | 第五批 S0189 | 2-Tridecenal | 香料 | — | K4 |
| `p_ethylbenzaldehyde` | 4-乙基苯甲醛 | `C9H10O` | 液 | — | ✓ | 第五批 S0190 | 4-Ethylbenzaldehyde | 香料 | — | K4 |
| `methylsalicylaldehyde` | 2-羟基-4-甲基苯甲醛 | `C8H8O2` | 固 | — | ✓ | 第五批 S0191 | 2-Hydroxy-4-methylbenzaldehyde | 香料 | — | K3 |
| `o_methoxycinnamaldehyde` | 邻-甲氧基肉桂醛 | `C10H10O2` | 固 | — | ✓ | 第五批 S0192 | o-Methoxycinnamaldehyde | 香料 | — | K3 |
| `campholenic_aldehyde` | 龙脑烯醛 | `C10H16O` | 液 | — | ✓ | 第五批 S0193 | Campholenic Aldehyde | 香料 | — | K4 |
| `hexylcinnamaldehyde` | α-己基肉桂醛 | `C15H20O` | 液 | — | ✓ | 第五批 S0194 | α-Hexylcinnamaldehyde | 香料 | — | K4 |
| `vanillin_propylene_glycol_acetal` | 香兰素1,2-丙二醇缩醛 | `C11H14O4` | 液 | — | ✓ | 第五批 S0195 | Vanillin Propylene Glycol Acetal | 香料 | — | K4 |
| `acetaldehyde_ethyl_hexenyl_acetal` | 乙醛乙醇顺式-3-己烯醇缩醛 | `C10H20O2` | 液 | — | ✓ | 第五批 S0196 | Acetaldehyde Ethyl cis-3-Hexenyl Acetal | 香料 | — | K4 |
| `trans_trans_2_6_nonadienal` | 反式,反式-2,6-壬二烯醛 | `C9H14O` | 液 | — | ✓ | 第五批 S0197 | 2-trans-6-trans-Nonadienal | 香料 | — | K4 |
| `decatrienal` | 2,4,7-癸三烯醛 | `C10H14O` | 液 | — | ✓ | 第五批 S0198 | 2,4,7-Decatrienal | 香料 | — | K4 |
| `beta_sinensal` | β-甜橙醛 | `C15H22O` | 液 | — | ✓ | 第五批 S0199 | β-Sinensal | 香料 | — | K4 |
| `p_hydroxybenzaldehyde` | 4-羟基苯甲醛 | `C7H6O2` | 固 | — | ✓ | 第五批 S0200 | 4-Hydroxybenzaldehyde | 香料 | — | K3 |
| `o_anisaldehyde` | 邻-甲氧基苯甲醛 | `C8H8O2` | 固 | — | ✓ | 第五批 S0201 | o-Methoxybenzaldehyde | 香料 | — | K3 |
| `methyltridecanal` | 12-甲基十三醛 | `C14H28O` | 液 | — | ✓ | 第五批 S0202 | 12-Methyltridecanal | 香料 | — | K4 |
| `methyl_ethyl_ketone` | 甲乙酮 | `C4H8O` | 液 | — | ✓ | 第五批 S0203 | Methyl Ethyl Ketone | 香料 | — | K4 |
| `acetoin` | 3-羟基-2-丁酮（乙偶姻） | `C4H8O2` | 液 | — | ✓ | 第五批 S0204 | 3-Hydroxy-2-butanone (Acetoin) | 香料 | — | K4 |
| `anisylacetone` | 4-(对-甲氧基苯基)-2-丁酮 | `C11H14O2` | 液 | — | ✓ | 第五批 S0205 | 4-(p-Methoxyphenyl)-2-butanone | 香料 | — | K4 |
| `benzalacetone` | 4-苯基-3-丁烯-2-酮 | `C10H10O` | 固 | — | ✓ | 第五批 S0206 | 4-Phenyl-3-buten-2-one | 香料 | — | K3 |
| `methyl_propyl_ketone` | 2-戊酮 | `C5H10O` | 液 | — | ✓ | 第五批 S0208 | 2-Pentanone | 香料 | — | K4 |
| `ethyl_vinyl_ketone` | 1-戊烯-3-酮 | `C5H8O` | 液 | — | ✓ | 第五批 S0209 | 1-Penten-3-one | 香料 | — | K4 |
| `acetylpropionyl` | 2,3-戊二酮 | `C5H8O2` | 液 | — | ✓ | 第五批 S0210 | 2,3-Pentanedione | 香料 | — | K4 |
| `ethyl_cyclopentenolone` | 3-乙基-2-羟基-2-环戊烯-1-酮 | `C7H10O2` | 固 | — | ✓ | 第五批 S0211 | 3-Ethyl-2-hydroxy-2-cyclopenten-1-one | 香料 | — | K3 |
| `ethyl_propenyl_ketone` | 4-己烯-3-酮 | `C6H10O` | 液 | — | ✓ | 第五批 S0213 | 4-Hexen-3-one | 香料 | — | K4 |
| `methylhexenone` | 5-甲基-3-己烯-2-酮 | `C7H12O` | 液 | — | ✓ | 第五批 S0214 | 5-Methyl-3-hexen-2-one | 香料 | — | K4 |
| `dipropionyl` | 3,4-己二酮 | `C6H10O2` | 液 | — | ✓ | 第五批 S0215 | 3,4-Hexanedione | 香料 | — | K4 |
| `methyl_amyl_ketone` | 2-庚酮 | `C7H14O` | 液 | — | ✓ | 第五批 S0216 | 2-Heptanone | 香料 | — | K4 |
| `methyl_pentenyl_ketone` | 3-庚烯-2-酮 | `C7H12O` | 液 | — | ✓ | 第五批 S0217 | 3-Hepten-2-one (Methyl Pentenyl Ketone) | 香料 | — | K4 |
| `methylheptenone` | 6-甲基-5-庚烯-2-酮 | `C8H14O` | 液 | — | ✓ | 第五批 S0218 | 6-Methyl-5-hepten-2-one | 香料 | — | K4 |
| `amyl_vinyl_ketone` | 1-辛烯-3-酮 | `C8H14O` | 液 | — | ✓ | 第五批 S0219 | 1-Octen-3-one | 香料 | — | K4 |
| `methyl_heptyl_ketone` | 2-壬酮 | `C9H18O` | 液 | — | ✓ | 第五批 S0220 | 2-Nonanone | 香料 | — | K4 |
| `methyl_nonyl_ketone` | 2-十一酮 | `C11H22O` | 液 | — | ✓ | 第五批 S0221 | 2-Undecanone | 香料 | — | K4 |
| `methyl_undecyl_ketone` | 2-十三酮 | `C13H26O` | 固 | — | ✓ | 第五批 S0222 | 2-Tridecanone | 香料 | — | K3 |
| `nootkatone` | 圆柚酮 | `C15H22O` | 固 | — | ✓ | 第五批 S0223 | Nootkatone | 香料 | — | K3 |
| `l_carvone` | l-香芹酮 | `C10H14O` | 液 | — | ✓ | 第五批 S0224 | l-Carvone | 香料 | — | K4 |
| `acetophenone` | 苯乙酮 | `C8H8O` | 液 | — | ✓ | 第五批 S0225 | Acetophenone | 香料 | — | K4 |
| `p_methylacetophenone` | 4-甲基苯乙酮 对-甲基苯乙酮 | `C9H10O` | 液 | — | ✓ | 第五批 S0226 | 4-Methylacetophenone (p-Methylacetophenone) | 香料 | — | K4 |
| `p_methoxyacetophenone` | 对-甲氧基苯乙酮 | `C9H10O2` | 固 | — | ✓ | 第五批 S0227 | p-Methoxyacetophenone | 香料 | — | K3 |
| `cis_jasmone` | 顺式茉莉酮 | `C11H16O` | 液 | — | ✓ | 第五批 S0228 | cis-Jasmone | 香料 | — | K4 |
| `raspberry_ketone` | 覆盆子酮（悬钩子酮） | `C10H12O2` | 固 | — | ✓ | 第五批 S0229 | Raspberry Ketone [4-(p-Hydroxyphenyl)-2-butanone] | 香料 | — | K3 |
| `alpha_damascone` | α-突厥酮 | `C13H20O` | 液 | — | ✓ | 第五批 S0230 | α-Damascone | 香料 | — | K4 |
| `damascenone` | 突厥烯酮 | `C13H18O` | 液 | — | ✓ | 第五批 S0231 | Damascenone | 香料 | — | K4 |
| `benzaldehyde_glyceryl_acetal` | 苯甲醛甘油缩醛 | `C10H12O3` | 液 | — | ✓ | 第五批 S0232 | Benzaldehyde Glyceryl Acetal | 香料 | — | K4 |
| `alpha_irone` | α-鸢尾酮 | `C14H22O` | 液 | — | ✓ | 第五批 S0233 | α-Irone | 香料 | — | K4 |
| `alpha_ionone` | α-紫罗兰酮 | `C13H20O` | 液 | — | ✓ | 第五批 S0234 | α-Ionone | 香料 | — | K4 |
| `beta_ionone` | β-紫罗兰酮 | `C13H20O` | 液 | — | ✓ | 第五批 S0235 | β-Ionone | 香料 | — | K4 |
| `dl_camphor` | dl-樟脑 | `C10H16O` | 固 | — | ✓ | 第五批 S0236 | dl-Camphor | 香料 | — | K3 |
| `isomenthone` | d,l-异薄荷酮 | `C10H18O` | 液 | — | ✓ | 第五批 S0238 | d,l-Isomenthone | 香料 | — | K4 |
| `furfurylidene_acetone` | 4-(2-呋喃基)-3-丁烯-2-酮 | `C8H8O2` | 固 | — | ✓ | 第五批 S0239 | 4-(2-Furyl)-3-buten-2-one | 香料 | — | K3 |
| `homofuraneol` | 2-乙基-4-羟基-5-甲基-3(2H)-呋喃酮 | `C7H10O3` | 液 | — | ✓ | 第五批 S0240 | 2-Ethyl-4-hydroxy-5-methyl-3(2H)-furanone (Homofuraneol) | 香料 | — | K4 |
| `sotolon` | 4,5-二甲基-3-羟基-2,5-二氢呋喃-2-酮 | `C6H8O3` | 固 | — | ✓ | 第五批 S0241 | 4,5-Dimethyl-3-hydroxy-2,5-dihydrofuran-2-one (Sotolon) | 香料 | — | K3 |
| `abhexon` | 2-乙基-3-甲基-4-羟基二氢-2,5-呋喃-5-酮 | `C7H10O3` | 液 | — | ✓ | 第五批 S0242 | 2-Ethyl-3-methyl-4-hydroxydihydro-2,5-furan-5-one (Abhexon) | 香料 | — | K4 |
| `tetrahydrothiophenone` | 4,5-二氢-3(2H)-噻吩酮(四氢噻吩-3-酮) | `C4H6OS` | 液 | — | ✓ | 第五批 S0243 | 4,5-Dihydro-3(2H)-thiophenone (Tetrahydrothiophen-3-one) | 香料 | — | K4 |
| `ethylfuran` | 2-乙基呋喃 | `C6H8O` | 液 | — | ✓ | 第五批 S0244 | 2-Ethylfuran | 香料 | — | K4 |
| `acetylfuran` | 2-乙酰基呋喃 | `C6H6O2` | 固 | — | ✓ | 第五批 S0245 | 2-Acetylfuran | 香料 | — | K3 |
| `acetylmethylfuran` | 2-乙酰基-5-甲基呋喃 | `C7H8O2` | 液 | — | ✓ | 第五批 S0246 | 2-Acetyl-5-methylfuran | 香料 | — | K4 |
| `phenylpropanedione` | 1-苯基-1,2-丙二酮 | `C9H8O2` | 液 | — | ✓ | 第五批 S0248 | 1-Phenyl-1,2-propanedione | 香料 | — | K4 |
| `dimethylcyclopentanedione_34` | 3,4-二甲基-1,2-环戊二酮 | `C7H10O2` | 固 | — | ✓ | 第五批 S0249 | 3,4-Dimethyl-1,2-cyclopentanedione | 香料 | — | K3 |
| `dimethylcyclopentanedione_35` | 3,5-二甲基-1,2-环戊二酮 | `C7H10O2` | 固 | — | ✓ | 第五批 S0250 | 3,5-Dimethyl-1,2-cyclopentanedione | 香料 | — | K3 |
| `acetyl_butyryl` | 2,3-己二酮 | `C6H10O2` | 液 | — | ✓ | 第五批 S0251 | 2,3-Hexanedione | 香料 | — | K4 |
| `methylcyclohexanedione` | 1-甲基-2,3-环己二酮 | `C7H10O2` | 固 | — | ✓ | 第五批 S0252 | 1-Methyl-2,3-cyclohexanedione (3-Methylcyclohexane-1,2-dione) | 香料 | — | K3 |
| `trimethylcyclohexanone` | 2,2,6-三甲基环己酮 | `C9H16O` | 液 | — | ✓ | 第五批 S0253 | 2,2,6-Trimethylcyclohexanone | 香料 | — | K4 |
| `ketoisophorone` | 2,6,6-三甲基-2-环己烯-1,4-二酮 | `C9H12O2` | 固 | — | ✓ | 第五批 S0254 | 2,6,6-Trimethylcyclohex-2-ene-1,4-dione (Ketoisophorone) | 香料 | — | K3 |
| `ethyl_butyl_ketone` | 3-庚酮 | `C7H14O` | 液 | — | ✓ | 第五批 S0255 | 3-Heptanone | 香料 | — | K4 |
| `filbertone` | 5-甲基-2-庚烯-4-酮 | `C8H14O` | 液 | — | ✓ | 第五批 S0256 | 5-Methyl-2-hepten-4-one (Filbertone) | 香料 | — | K4 |
| `methylheptadienone` | 6-甲基-3,5-庚二烯-2-酮 | `C8H12O` | 液 | — | ✓ | 第五批 S0257 | 6-Methyl-3,5-heptadien-2-one | 香料 | — | K4 |
| `methyl_hexyl_ketone` | 2-辛酮 | `C8H16O` | 液 | — | ✓ | 第五批 S0258 | 2-Octanone | 香料 | — | K4 |
| `ethyl_amyl_ketone` | 3-辛酮 | `C8H16O` | 液 | — | ✓ | 第五批 S0259 | 3-Octanone | 香料 | — | K4 |
| `methyl_hexenyl_ketone` | 3-辛烯-2-酮 | `C8H14O` | 液 | — | ✓ | 第五批 S0260 | 3-Octen-2-one | 香料 | — | K4 |
| `geranylacetone` | 6,10-二甲基-5,9-十一碳二烯-2-酮 | `C13H22O` | 液 | — | ✓ | 第五批 S0261 | 6,10-Dimethyl-5,9-undecadien-2-one (Geranylacetone) | 香料 | — | K4 |
| `pentadecanone` | 2-十五酮 | `C15H30O` | 固 | — | ✓ | 第五批 S0262 | 2-Pentadecanone | 香料 | — | K3 |
| `muscone` | 3-甲基环十五酮 | `C16H30O` | 液 | — | ✓ | 第五批 S0263 | 3-Methyl-1-cyclopentadecanone (Muscone) | 香料 | — | K4 |
| `civetone` | 环十七-9-烯-1-酮 | `C17H30O` | 固 | — | ✓ | 第五批 S0264 | Cycloheptadec-9-en-1-one (Civetone) | 香料 | — | K3 |
| `benzophenone` | 二苯甲酮 | `C13H10O` | 固 | — | ✓ | 第五批 S0265 | Benzophenone | 香料 | — | K3 |
| `o_hydroxyacetophenone` | 2-羟基苯乙酮 | `C8H8O2` | 液 | — | ✓ | 第五批 S0266 | 2-Hydroxyacetophenone | 香料 | — | K4 |
| `isophorone` | 异弗尔酮 | `C9H14O` | 液 | — | ✓ | 第五批 S0267 | Isophorone | 香料 | — | K4 |
| `dihydrojasmone` | 二氢茉莉酮 | `C11H18O` | 液 | — | ✓ | 第五批 S0268 | Dihydrojasmone (2-Pentyl-3-methyl-2-cyclopenten-1-one) | 香料 | — | K4 |
| `neohesperidin_dhc` | 新甲基橙皮苷二氢查耳酮 | `C28H36O15` | 固 | — | ✓ | 第五批 S0269 | Neohesperidin Dihydrochalcone (Neohesperidin DHC) | 香料 | — | K3 |
| `zingerone` | 姜油酮 | `C11H14O3` | 固 | — | ✓ | 第五批 S0270 | Zingerone | 香料 | — | K3 |
| `beta_damascone` | β-突厥酮 | `C13H20O` | 液 | — | ✓ | 第五批 S0271 | β-Damascone | 香料 | — | K4 |
| `methylthiobutanal` | 3-甲硫基丁醛 | `C5H10OS` | 液 | — | ✓ | 第五批 S0272 | 3-(Methylthio)butanal | 香料 | — | K4 |
| `amylcinnamaldehyde` | α-戊基肉桂醛 | `C14H18O` | 液 | — | ✓ | 第五批 S0273 | α-Amylcinnamaldehyde | 香料 | — | K4 |
| `d_fenchone` | d-葑酮 | `C10H16O` | 液 | — | ✓ | 第五批 S0274 | d-Fenchone | 香料 | — | K4 |
| `methyltetrahydrofuranone` | 2-甲基四氢呋喃-3-酮 | `C5H8O2` | 液 | — | ✓ | 第五批 S0275 | 2-Methyltetrahydrofuran-3-one | 香料 | — | K4 |
| `mesifurane` | 2,5-二甲基-4-甲氧基-3(2H)-呋喃酮 | `C7H10O3` | 液 | — | ✓ | 第五批 S0277 | 2,5-Dimethyl-4-methoxy-3(2H)-furanone (Mesifurane) | 香料 | — | K4 |
| `pentylfuran` | 2-戊基呋喃 | `C9H14O` | 液 | — | ✓ | 第五批 S0278 | 2-Pentylfuran | 香料 | — | K4 |
| `menthofuran` | 4,5,6,7-四氢-3,6-二甲基苯并呋喃（薄荷呋喃） | `C10H14O` | 液 | — | ✓ | 第五批 S0279 | 4,5,6,7-Tetrahydro-3,6-dimethylbenzofuran (Menthofuran) | 香料 | — | K4 |
| `ambroxide` | 1,5,5,9-四甲基-13-氧杂三环[8.3.0.0(4,9)]十三烷 | `C16H28O` | 固 | — | ✓ | 第五批 S0280 | 1,5,5,9-Tetramethyl-13-oxatricyclo[8.3.0.0(4,9)]tridecane (Ambrox) | 香料 | — | K3 |
| `cis_dihydrocarvone` | 顺式-二氢香芹酮 | `C10H16O` | 液 | — | ✓ | 第五批 S0281 | cis-Dihydrocarvone | 香料 | — | K4 |
| `mercaptobutanone` | 3-巯基-2-丁酮 | `C4H8OS` | 液 | — | ✓ | 第五批 S0282 | 3-Mercapto-2-butanone | 香料 | — | K4 |
| `piperonylacetone` | 胡椒基丙酮 | `C11H12O3` | 固 | — | ✓ | 第五批 S0283 | Piperonyl Acetone | 香料 | — | K3 |
| `dihydro_beta_ionone` | 二氢-β-紫罗兰酮 | `C13H22O` | 液 | — | ✓ | 第五批 S0284 | Dihydro-β-ionone | 香料 | — | K4 |
| `acetyl_isobutyryl` | 4-甲基-2,3-戊二酮 | `C6H10O2` | 液 | — | ✓ | 第五批 S0285 | 4-Methyl-2,3-pentanedione | 香料 | — | K4 |
| `methyloctenone` | 反式-7-甲基-3-辛烯-2-酮 | `C9H16O` | 液 | — | ✓ | 第五批 S0286 | (E)-7-Methyl-3-octen-2-one | 香料 | — | K4 |
| `acetylthio_methylfuran` | 3-乙酰硫基-2-甲基呋喃 | `C7H8O2S` | 液 | — | ✓ | 第五批 S0287 | 3-(Acetylthio)-2-methylfuran | 香料 | — | K4 |
| `furaneol_acetate` | 4-乙酰氧基-2,5-二甲基-3(2H)-呋喃酮 | `C8H10O4` | 液 | — | ✓ | 第五批 S0288 | 4-Acetoxy-2,5-dimethyl-3(2H)-furanone | 香料 | — | K4 |
| `ethyl_methyl_cyclopentenolone` | 3-乙基-2-羟基-4-甲基-2-环戊烯-1-酮 | `C8H12O2` | 固 | — | ✓ | 第五批 S0289 | 3-Ethyl-2-hydroxy-4-methylcyclopent-2-en-1-one | 香料 | — | K3 |
| `cyclohexanone` | 环己酮 | `C6H10O` | 液 | — | ✓ | 第五批 S0290 | Cyclohexanone | 香料 | — | K4 |
| `acetyl_valeryl` | 2,3-庚二酮 | `C7H12O2` | 液 | — | ✓ | 第五批 S0291 | 2,3-Heptanedione | 香料 | — | K4 |
| `acetyl_hexanoyl` | 2,3-辛二酮 | `C8H14O2` | 液 | — | ✓ | 第五批 S0292 | 2,3-Octanedione | 香料 | — | K4 |
| `pyruvic_acid` | 丙酮酸 | `C3H4O3` | 液 | — | ✓ | 第五批 S0295 | Pyruvic Acid | 香料 | — | K4 |
| `methylbutyric_acid` | 2-甲基丁酸 | `C5H10O2` | 液 | — | ✓ | 第五批 S0298 | 2-Methylbutyric Acid | 香料 | — | K4 |
| `ethylbutyric_acid` | 2-乙基丁酸 | `C6H12O2` | 液 | — | ✓ | 第五批 S0299 | 2-Ethylbutyric Acid | 香料 | — | K4 |
| `valeric_acid` | 戊酸 | `C5H10O2` | 液 | — | ✓ | 第五批 S0300 | Valeric Acid | 香料 | — | K4 |
| `alpha_methylvaleric_acid` | 2-甲基戊酸 | `C6H12O2` | 液 | — | ✓ | 第五批 S0301 | 2-Methylvaleric Acid | 香料 | — | K4 |
| `methylpentenoic_acid` | 2-甲基-2-戊烯酸（草莓酸） | `C6H10O2` | 液 | — | ✓ | 第五批 S0302 | 2-Methyl-2-pentenoic Acid (Strawberriff) | 香料 | — | K4 |
| `isovaleric_acid` | 异戊酸 | `C5H10O2` | 液 | — | ✓ | 第五批 S0303 | Isovaleric Acid | 香料 | — | K4 |
| `trans_2_hexenoic_acid` | 反式-2-己烯酸 | `C6H10O2` | 固 | — | ✓ | 第五批 S0306 | trans-2-Hexenoic Acid | 香料 | — | K3 |
| `hex_3_enoic_acid` | 3-己烯酸 | `C6H10O2` | 液 | — | ✓ | 第五批 S0307 | 3-Hexenoic Acid | 香料 | — | K4 |
| `heptanoic_acid` | 庚酸 | `C7H14O2` | 液 | — | ✓ | 第五批 S0308 | Heptanoic Acid | 香料 | — | K7 |
| `nonanoic_acid` | 壬酸 | `C9H18O2` | 液 | — | ✓ | 第五批 S0310 | Nonanoic Acid | 香料 | — | K7 |
| `myristic_acid` | 十四酸（肉豆蔻酸） | `C14H28O2` | 固 | — | ✓ | 第五批 S0313 | Tetradecanoic Acid (Myristic Acid) | 香料 | — | K7 |
| `phenylacetic_acid` | 苯乙酸 | `C8H8O2` | 固 | — | ✓ | 第五批 S0316 | Phenylacetic Acid | 香料 | — | K3 |
| `cinnamic_acid` | 肉桂酸 | `C9H8O2` | 固 | — | ✓ | 第五批 S0318 | Cinnamic Acid | 香料 | — | K3 |
| `beta_methylvaleric_acid` | 3-甲基戊酸（酐酪酸） | `C6H12O2` | 液 | — | ✓ | 第五批 S0320 | 3-Methylpentanoic Acid | 香料 | — | K4 |
| `beta_alanine` | β-丙氨酸 | `C3H7NO2` | 固 | — | ✓ | 第五批 S0321 | β-Alanine | 香料 | — | K3 |
| `l_leucine` | L-亮氨酸 | `C6H13NO2` | 固 | — | ✓ | 第五批 S0326 | L-Leucine | 香料 | — | K3 |
| `dl_methionine` | DL-蛋氨酸 | `C5H11NO2S` | 固 | — | ✓ | 第五批 S0327 | DL-Methionine | 香料 | — | K3 |
| `levulinic_acid` | 乙酰丙酸 | `C5H8O3` | 固 | — | ✓ | 第五批 S0328 | Levulinic Acid | 香料 | — | K3 |
| `oxobutyric_acid` | 2-氧代丁酸 | `C4H6O3` | 固 | — | ✓ | 第五批 S0329 | 2-Oxobutyric Acid | 香料 | — | K3 |
| `methylhexanoic_acid` | 2-甲基己酸 | `C7H14O2` | 液 | — | ✓ | 第五批 S0330 | 2-Methylhexanoic Acid | 香料 | — | K4 |
| `methylheptanoic_acid` | 2-甲基庚酸 | `C8H16O2` | 液 | — | ✓ | 第五批 S0331 | 2-Methylheptanoic Acid | 香料 | — | K4 |
| `methyloctanoic_acid` | 4-甲基辛酸 | `C9H18O2` | 液 | — | ✓ | 第五批 S0332 | 4-Methyloctanoic Acid | 香料 | — | K4 |
| `citronellic_acid` | 3,7-二甲基-6-辛烯酸 | `C10H18O2` | 液 | — | ✓ | 第五批 S0333 | 3,7-Dimethyl-6-octenoic Acid (Citronellic Acid) | 香料 | — | K4 |
| `decenoic_acid` | 9-癸烯酸 | `C10H18O2` | 液 | — | ✓ | 第五批 S0334 | 9-Decenoic Acid | 香料 | — | K7 |
| `undecanoic_acid` | 十一酸 | `C11H22O2` | 固 | — | ✓ | 第五批 S0335 | Undecanoic Acid | 香料 | — | K7 |
| `undecylenic_acid` | 10-十一碳烯酸 | `C11H20O2` | 固 | — | ✓ | 第五批 S0336 | 10-Undecenoic Acid | 香料 | — | K7 |
| `hydrocinnamic_acid` | 3-苯丙酸 | `C9H10O2` | 固 | — | ✓ | 第五批 S0337 | 3-Phenylpropionic Acid | 香料 | — | K3 |
| `l_proline` | L-脯氨酸 | `C5H9NO2` | 固 | — | ✓ | 第五批 S0339 | L-Proline | 香料 | — | K3 |
| `dl_valine` | DL-缬氨酸 | `C5H11NO2` | 固 | — | ✓ | 第五批 S0340 | DL-Valine | 香料 | — | K3 |
| `lactisole` | 2-(4-甲氧基苯氧基)-丙酸钠 | `C10H11NaO4` | 固 | — | ✓ | 第五批 S0341 | Sodium 2-(4-Methoxyphenoxy)propanoate (Lactisole) | 香料 | — | K3 |
| `l_arginine` | L-精氨酸 | `C6H14N4O2` | 固 | — | ✓ | 第五批 S0343 | L-Arginine | 香料 | — | K3 |
| `l_lysine` | L-赖氨酸 | `C6H14N2O2` | 固 | — | ✓ | 第五批 S0344 | L-Lysine | 香料 | — | K3 |
| `methylcrotonic_acid` | 3-甲基巴豆酸 | `C5H8O2` | 固 | — | ✓ | 第五批 S0345 | 3-Methylcrotonic Acid | 香料 | — | K3 |
| `methylnonanoic_acid` | 4-甲基壬酸 | `C10H20O2` | 液 | — | ✓ | 第五批 S0347 | 4-Methylnonanoic Acid | 香料 | — | K4 |
| `isocaproic_acid` | 异己酸 | `C6H12O2` | 液 | — | ✓ | 第五批 S0348 | Isohexanoic Acid (4-Methylpentanoic Acid) | 香料 | — | K4 |
| `salicylic_acid` | 2-羟基苯甲酸（水杨酸） | `C7H6O3` | 固 | — | ✓ | 第五批 S0349 | 2-Hydroxybenzoic Acid (Salicylic Acid) | 香料 | — | K3 |
| `tiglic_acid` | 惕各酸 | `C5H8O2` | 固 | — | ✓ | 第五批 S0350 | Tiglic Acid | 香料 | — | K3 |
| `ethyl_formate` | 甲酸乙酯 | `C3H6O2` | 液 | — | ✓ | 第五批 S0353 | Ethyl Formate | 香料 | — | K4 |
| `butyl_formate` | 甲酸丁酯 | `C5H10O2` | 液 | — | ✓ | 第五批 S0354 | Butyl Formate | 香料 | — | K4 |
| `amyl_formate` | 甲酸戊酯 | `C6H12O2` | 液 | — | ✓ | 第五批 S0355 | Amyl Formate | 香料 | — | K4 |
| `isoamyl_formate` | 甲酸异戊酯 | `C6H12O2` | 液 | — | ✓ | 第五批 S0356 | Isoamyl Formate | 香料 | — | K4 |
| `hexyl_formate` | 甲酸己酯 | `C7H14O2` | 液 | — | ✓ | 第五批 S0357 | Hexyl Formate | 香料 | — | K4 |
| `benzyl_formate` | 甲酸苄酯 | `C8H8O2` | 液 | — | ✓ | 第五批 S0358 | Benzyl Formate | 香料 | — | K4 |
| `geranyl_formate` | 甲酸香叶酯 | `C11H18O2` | 液 | — | ✓ | 第五批 S0359 | Geranyl Formate | 香料 | — | K4 |
| `citronellyl_formate` | 甲酸香茅酯 | `C11H20O2` | 液 | — | ✓ | 第五批 S0360 | Citronellyl Formate | 香料 | — | K4 |
| `phenethyl_formate` | 甲酸苯乙酯 | `C9H10O2` | 液 | — | ✓ | 第五批 S0361 | Phenethyl Formate | 香料 | — | K4 |
| `linalyl_formate` | 甲酸芳樟酯 | `C11H18O2` | 液 | — | ✓ | 第五批 S0362 | Linalyl Formate | 香料 | — | K4 |
| `methyl_acetate` | 乙酸甲酯 | `C3H6O2` | 液 | — | ✓ | 第五批 S0363 | Methyl Acetate | 香料 | — | K4 |
| `ethyl_acetoacetate` | 乙酰乙酸乙酯 | `C6H10O3` | 液 | — | ✓ | 第五批 S0365 | Ethyl Acetoacetate | 香料 | — | K4 |
| `propyl_acetate` | 乙酸丙酯 | `C5H10O2` | 液 | — | ✓ | 第五批 S0366 | Propyl Acetate | 香料 | — | K4 |
| `isopropyl_acetate` | 乙酸异丙酯 | `C5H10O2` | 液 | — | ✓ | 第五批 S0367 | Isopropyl Acetate | 香料 | — | K4 |
| `allyl_acetate` | 乙酸烯丙酯 | `C5H8O2` | 液 | — | ✓ | 第五批 S0368 | Allyl Acetate | 香料 | — | K4 |
| `ethyl_levulinate` | 乙酰丙酸乙酯 | `C7H12O3` | 液 | — | ✓ | 第五批 S0369 | Ethyl Levulinate | 香料 | — | K4 |
| `butyl_acetate` | 乙酸丁酯 | `C6H12O2` | 液 | — | ✓ | 第五批 S0370 | Butyl Acetate | 香料 | — | K4 |
| `isobutyl_acetate` | 乙酸异丁酯 | `C6H12O2` | 液 | — | ✓ | 第五批 S0371 | Isobutyl Acetate | 香料 | — | K4 |
| `hexyl_acetate` | 乙酸己酯 | `C8H16O2` | 液 | — | ✓ | 第五批 S0373 | Hexyl Acetate | 香料 | — | K4 |
| `hex_2_enyl_acetate` | 乙酸2-己烯酯 | `C8H14O2` | 液 | — | ✓ | 第五批 S0374 | 2-Hexen-1-yl Acetate | 香料 | — | K4 |
| `heptyl_acetate` | 乙酸庚酯 | `C9H18O2` | 液 | — | ✓ | 第五批 S0375 | Heptyl Acetate | 香料 | — | K4 |
| `octyl_acetate` | 乙酸辛酯 | `C10H20O2` | 液 | — | ✓ | 第五批 S0376 | Octyl Acetate | 香料 | — | K4 |
| `octan_3_yl_acetate` | 乙酸3-辛酯 | `C10H20O2` | 液 | — | ✓ | 第五批 S0377 | 3-Octyl Acetate | 香料 | — | K4 |
| `octen_3_yl_acetate` | 1-辛烯-3-醇乙酸酯 | `C10H18O2` | 液 | — | ✓ | 第五批 S0378 | 1-Octen-3-yl Acetate | 香料 | — | K4 |
| `nonyl_acetate` | 乙酸壬酯 | `C11H22O2` | 液 | — | ✓ | 第五批 S0379 | Nonyl Acetate | 香料 | — | K4 |
| `hexyl_2_butenoate` | 2-丁烯酸己酯 | `C10H18O2` | 液 | — | ✓ | 第五批 S0380 | n-Hexyl 2-Butenoate | 香料 | — | K4 |
| `decyl_acetate` | 乙酸癸酯 | `C12H24O2` | 液 | — | ✓ | 第五批 S0381 | Decyl Acetate | 香料 | — | K4 |
| `benzyl_acetate` | 乙酸苄酯 | `C9H10O2` | 液 | — | ✓ | 第五批 S0382 | Benzyl Acetate | 香料 | — | K4 |
| `phenethyl_acetate` | 乙酸苯乙酯 | `C10H12O2` | 液 | — | ✓ | 第五批 S0383 | Phenethyl Acetate | 香料 | — | K4 |
| `anisyl_acetate` | 乙酸茴香酯 | `C10H12O3` | 液 | — | ✓ | 第五批 S0384 | Anisyl Acetate | 香料 | — | K4 |
| `bornyl_acetate` | 乙酸龙脑酯 | `C12H20O2` | 液 | — | ✓ | 第五批 S0385 | Bornyl Acetate | 香料 | — | K4 |
| `menthyl_acetate` | 乙酸薄荷酯 | `C12H22O2` | 液 | — | ✓ | 第五批 S0386 | Menthyl Acetate | 香料 | — | K4 |
| `cinnamyl_acetate` | 乙酸肉桂酯 | `C11H12O2` | 液 | — | ✓ | 第五批 S0387 | Cinnamyl Acetate | 香料 | — | K4 |
| `citronellyl_acetate` | 乙酸香茅酯 | `C12H22O2` | 液 | — | ✓ | 第五批 S0388 | Citronellyl Acetate | 香料 | — | K4 |
| `geranyl_acetate` | 乙酸香叶酯 | `C12H20O2` | 液 | — | ✓ | 第五批 S0389 | Geranyl Acetate | 香料 | — | K4 |
| `p_cresyl_acetate` | 乙酸对-甲酚酯 | `C9H10O2` | 液 | — | ✓ | 第五批 S0390 | p-Cresyl Acetate | 香料 | — | K4 |
| `styrallyl_acetate` | 乙酸苏合香酯 | `C10H12O2` | 液 | — | ✓ | 第五批 S0391 | Styrallyl Acetate | 香料 | — | K4 |
| `neryl_acetate` | 乙酸橙花酯 | `C12H20O2` | 液 | — | ✓ | 第五批 S0392 | Neryl Acetate | 香料 | — | K4 |
| `terpinyl_acetate` | 乙酸松油酯 | `C12H20O2` | 液 | — | ✓ | 第五批 S0393 | Terpinyl Acetate | 香料 | — | K4 |
| `cinnamyl_isobutyrate` | 异丁酸肉桂酯 | `C13H16O2` | 液 | — | ✓ | 第五批 S0394 | Cinnamyl Isobutyrate | 香料 | — | K4 |
| `cis_3_hexenyl_acetate` | 顺式-3-己烯-1-醇乙酸酯（乙酸叶醇酯） | `C8H14O2` | 液 | — | ✓ | 第五批 S0395 | cis-3-Hexen-1-yl Acetate (Leaf Acetate) | 香料 | — | K4 |
| `furfuryl_acetate` | 乙酸糠酯 | `C7H8O3` | 液 | — | ✓ | 第五批 S0396 | Furfuryl Acetate | 香料 | — | K4 |
| `allyl_heptanoate` | 庚酸烯丙酯 | `C10H18O2` | 液 | — | ✓ | 第五批 S0397 | Allyl Heptanoate | 香料 | — | K4 |
| `linalyl_acetate` | 乙酸芳樟酯 | `C12H20O2` | 液 | — | ✓ | 第五批 S0398 | Linalyl Acetate | 香料 | — | K4 |
| `carvyl_acetate` | 乙酸葛缕酯 | `C12H18O2` | 液 | — | ✓ | 第五批 S0399 | Carvyl Acetate | 香料 | — | K4 |
| `dihydrocarvyl_acetate` | 乙酸二氢葛缕酯 | `C12H20O2` | 液 | — | ✓ | 第五批 S0400 | Dihydrocarvyl Acetate | 香料 | — | K4 |
| `butyl_phenylacetate` | 苯乙酸丁酯 | `C12H16O2` | 液 | — | ✓ | 第五批 S0401 | Butyl Phenylacetate | 香料 | — | K4 |
| `ethyl_propionate` | 丙酸乙酯 | `C5H10O2` | 液 | — | ✓ | 第五批 S0402 | Ethyl Propionate | 香料 | — | K4 |
| `diethyl_malonate` | 丙二酸二乙酯 | `C7H12O4` | 液 | — | ✓ | 第五批 S0403 | Diethyl Malonate | 香料 | — | K4 |
| `isobutyl_propionate` | 丙酸异丁酯 | `C7H14O2` | 液 | — | ✓ | 第五批 S0404 | Isobutyl Propionate | 香料 | — | K4 |
| `isoamyl_propionate` | 丙酸异戊酯 | `C8H16O2` | 液 | — | ✓ | 第五批 S0405 | Isoamyl Propionate | 香料 | — | K4 |
| `hexenyl_propionate_mixture` | 丙酸顺式-3-己烯酯和丙酸反式-2-己烯酯 | C9H16O2（两种异构体） | 液 | — |  | 第五批 S0406 | cis-3-Hexenyl Propionate and trans-2-Hexenyl Propionate | 香料 | — | — |
| `geranyl_propionate` | 丙酸香叶酯 | `C13H22O2` | 液 | — | ✓ | 第五批 S0407 | Geranyl Propionate | 香料 | — | K4 |
| `citronellyl_propionate` | 丙酸香茅酯 | `C13H24O2` | 液 | — | ✓ | 第五批 S0408 | Citronellyl Propionate | 香料 | — | K4 |
| `benzyl_propionate` | 丙酸苄酯 | `C10H12O2` | 液 | — | ✓ | 第五批 S0409 | Benzyl Propionate | 香料 | — | K4 |
| `phenethyl_propionate` | 丙酸苯乙酯 | `C11H14O2` | 液 | — | ✓ | 第五批 S0410 | Phenethyl Propionate | 香料 | — | K4 |
| `linalyl_propionate` | 丙酸芳樟酯 | `C13H22O2` | 液 | — | ✓ | 第五批 S0411 | Linalyl Propionate | 香料 | — | K4 |
| `methyl_butyrate` | 丁酸甲酯 | `C5H10O2` | 液 | — | ✓ | 第五批 S0412 | Methyl Butyrate | 香料 | — | K4 |
| `methyl_2_methylbutyrate` | 2-甲基丁酸甲酯 | `C6H12O2` | 液 | — | ✓ | 第五批 S0413 | Methyl 2-Methylbutyrate | 香料 | — | K4 |
| `ethyl_isobutyrate` | 异丁酸乙酯 | `C6H12O2` | 液 | — | ✓ | 第五批 S0415 | Ethyl Isobutyrate | 香料 | — | K4 |
| `ethyl_2_methylbutyrate` | 2-甲基丁酸乙酯 | `C7H14O2` | 液 | — | ✓ | 第五批 S0416 | Ethyl 2-Methylbutyrate | 香料 | — | K4 |
| `ethyl_3_hydroxybutyrate` | 3-羟基丁酸乙酯 | `C6H12O3` | 液 | — | ✓ | 第五批 S0417 | Ethyl 3-Hydroxybutyrate | 香料 | — | K4 |
| `diethyl_succinate` | 丁二酸二乙酯 | `C8H14O4` | 液 | — | ✓ | 第五批 S0418 | Diethyl Succinate | 香料 | — | K4 |
| `methyl_isobutyrate` | 异丁酸甲酯 | `C5H10O2` | 液 | — | ✓ | 第五批 S0419 | Methyl Isobutyrate | 香料 | — | K4 |
| `butyl_butyrate` | 丁酸丁酯 | `C8H16O2` | 液 | — | ✓ | 第五批 S0420 | Butyl Butyrate | 香料 | — | K4 |
| `isobutyl_butyrate` | 丁酸异丁酯 | `C8H16O2` | 液 | — | ✓ | 第五批 S0421 | Isobutyl Butyrate | 香料 | — | K4 |
| `butyl_2_methylbutyrate` | 2-甲基丁酸丁酯 | `C9H18O2` | 液 | — | ✓ | 第五批 S0422 | n-Butyl 2-Methylbutyrate | 香料 | — | K4 |
| `methylbutyl_methylbutyrate` | 2-甲基丁酸2-甲基丁酯 | `C10H20O2` | 液 | — | ✓ | 第五批 S0423 | 2-Methylbutyl 2-Methylbutyrate | 香料 | — | K4 |
| `butyl_isobutyrate` | 异丁酸丁酯 | `C8H16O2` | 液 | — | ✓ | 第五批 S0424 | Butyl Isobutyrate | 香料 | — | K4 |
| `amyl_butyrate` | 丁酸戊酯 | `C9H18O2` | 液 | — | ✓ | 第五批 S0425 | Amyl Butyrate | 香料 | — | K4 |
| `isoamyl_butyrate` | 丁酸异戊酯 | `C9H18O2` | 液 | — | ✓ | 第五批 S0426 | Isoamyl Butyrate | 香料 | — | K4 |
| `isoamyl_2_methylbutanoate` | 2-甲基丁酸异戊酯 | `C10H20O2` | 液 | — | ✓ | 第五批 S0427 | Isoamyl 2-Methylbutanoate | 香料 | — | K4 |
| `isopentyl_isobutyrate` | 异丁酸异戊酯 | `C9H18O2` | 液 | — | ✓ | 第五批 S0428 | Isopentyl Isobutyrate | 香料 | — | K4 |
| `hexyl_butyrate` | 丁酸己酯 | `C10H20O2` | 液 | — | ✓ | 第五批 S0429 | Hexyl Butyrate | 香料 | — | K4 |
| `hexyl_2_methylbutyrate` | 2-甲基丁酸己酯 | `C11H22O2` | 液 | — | ✓ | 第五批 S0430 | Hexyl 2-Methylbutyrate | 香料 | — | K4 |
| `cis_3_hexenyl_butyrate` | 丁酸顺式-3-己烯酯（丁酸叶醇酯） | `C10H18O2` | 液 | — | ✓ | 第五批 S0431 | cis-3-Hexenyl Butyrate (Leaf Butyrate) | 香料 | — | K4 |
| `hex_3_enyl_2_methylbutanoate` | 2-甲基丁酸-3-己烯酯 | `C11H20O2` | 液 | — | ✓ | 第五批 S0432 | 3-Hexenyl 2-Methylbutanoate | 香料 | — | K4 |
| `heptyl_isobutyrate` | 异丁酸庚酯 | `C11H22O2` | 液 | — | ✓ | 第五批 S0433 | Heptyl Isobutyrate | 香料 | — | K4 |
| `octyl_2_methylbutyrate` | 2-甲基丁酸辛酯 | `C13H26O2` | 液 | — | ✓ | 第五批 S0434 | Octyl 2-Methylbutyrate | 香料 | — | K4 |
| `octen_3_yl_butyrate` | 1-辛烯-3-醇丁酸酯 | `C12H22O2` | 液 | — | ✓ | 第五批 S0435 | 1-Octen-3-yl Butyrate | 香料 | — | K4 |
| `benzyl_butyrate` | 丁酸苄酯 | `C11H14O2` | 液 | — | ✓ | 第五批 S0436 | Benzyl Butyrate | 香料 | — | K4 |
| `benzyl_isobutyrate` | 异丁酸苄酯 | `C11H14O2` | 液 | — | ✓ | 第五批 S0437 | Benzyl Isobutyrate | 香料 | — | K4 |
| `phenethyl_butyrate` | 丁酸苯乙酯 | `C12H16O2` | 液 | — | ✓ | 第五批 S0438 | Phenethyl Butyrate | 香料 | — | K4 |
| `phenethyl_2_methylbutyrate` | 2-甲基丁酸苯乙酯 | `C13H18O2` | 液 | — | ✓ | 第五批 S0439 | Phenethyl 2-Methylbutyrate | 香料 | — | K4 |
| `phenethyl_isobutyrate` | 异丁酸苯乙酯 | `C12H16O2` | 液 | — | ✓ | 第五批 S0440 | Phenethyl Isobutyrate | 香料 | — | K4 |
| `geranyl_butyrate` | 丁酸香叶酯 | `C14H24O2` | 液 | — | ✓ | 第五批 S0441 | Geranyl Butyrate | 香料 | — | K4 |
| `geranyl_isobutyrate` | 异丁酸香叶酯 | `C14H24O2` | 液 | — | ✓ | 第五批 S0442 | Geranyl Isobutyrate | 香料 | — | K4 |
| `linalyl_butyrate` | 丁酸芳樟酯 | `C14H24O2` | 液 | — | ✓ | 第五批 S0443 | Linalyl Butyrate | 香料 | — | K4 |
| `linalyl_isobutyrate` | 异丁酸芳樟酯 | `C14H24O2` | 液 | — | ✓ | 第五批 S0444 | Linalyl Isobutyrate | 香料 | — | K4 |
| `isobutyl_angelate` | 当归酸异丁酯 | `C9H16O2` | 液 | — | ✓ | 第五批 S0445 | Isobutyl Angelate | 香料 | — | K4 |
| `neryl_isobutyrate` | 异丁酸橙花酯 | `C14H24O2` | 液 | — | ✓ | 第五批 S0446 | Neryl Isobutyrate | 香料 | — | K4 |
| `ethyl_valerate` | 正戊酸乙酯 | `C7H14O2` | 液 | — | ✓ | 第五批 S0447 | Ethyl Valerate | 香料 | — | K4 |
| `butyl_butyryllactate` | 丁酰乳酸丁酯 | `C11H20O4` | 液 | — | ✓ | 第五批 S0448 | Butyl Butyryllactate | 香料 | — | K4 |
| `ethyl_isovalerate` | 异戊酸乙酯 | `C7H14O2` | 液 | — | ✓ | 第五批 S0449 | Ethyl Isovalerate | 香料 | — | K4 |
| `butyl_salicylate` | 水杨酸丁酯（柳酸丁酯） | `C11H14O3` | 液 | — | ✓ | 第五批 S0450 | Butyl Salicylate | 香料 | — | K4 |
| `butyl_isovalerate` | 异戊酸丁酯 | `C9H18O2` | 液 | — | ✓ | 第五批 S0451 | Butyl Isovalerate | 香料 | — | K4 |
| `isoamyl_isovalerate` | 异戊酸异戊酯 | `C10H20O2` | 液 | — | ✓ | 第五批 S0452 | Isoamyl Isovalerate | 香料 | — | K4 |
| `hex_3_enyl_isovalerate` | 异戊酸3-己烯酯 | `C11H20O2` | 液 | — | ✓ | 第五批 S0453 | 3-Hexenyl Isovalerate | 香料 | — | K4 |
| `nonyl_isovalerate` | 异戊酸壬酯 | `C14H28O2` | 液 | — | ✓ | 第五批 S0454 | Nonyl Isovalerate | 香料 | — | K4 |
| `phenethyl_isovalerate` | 异戊酸苯乙酯 | `C13H18O2` | 液 | — | ✓ | 第五批 S0455 | Phenethyl Isovalerate | 香料 | — | K4 |
| `geranyl_isovalerate` | 异戊酸香叶酯 | `C15H26O2` | 液 | — | ✓ | 第五批 S0456 | Geranyl Isovalerate | 香料 | — | K4 |
| `methyl_hexanoate` | 己酸甲酯 | `C7H14O2` | 液 | — | ✓ | 第五批 S0457 | Methyl Hexanoate | 香料 | — | K4 |
| `methyl_2_hexenoate` | 2-己烯酸甲酯 | `C7H12O2` | 液 | — | ✓ | 第五批 S0458 | Methyl 2-Hexenoate | 香料 | — | K4 |
| `ethyl_hexanoate` | 己酸乙酯 | `C8H16O2` | 液 | — | ✓ | 第五批 S0459 | Ethyl Hexanoate (Ethyl Caproate) | 香料 | — | K4 |
| `ethyl_3_hexenoate` | 3-己烯酸乙酯 | `C8H14O2` | 液 | — | ✓ | 第五批 S0460 | Ethyl 3-Hexenoate | 香料 | — | K4 |
| `ethyl_3_hydroxyhexanoate` | 3-羟基己酸乙酯 | `C8H16O3` | 液 | — | ✓ | 第五批 S0461 | Ethyl 3-Hydroxyhexanoate | 香料 | — | K4 |
| `ethyl_trans_2_hexenoate` | 反式-2-己烯酸乙酯 | `C8H14O2` | 液 | — | ✓ | 第五批 S0462 | Ethyl trans-2-Hexenoate | 香料 | — | K4 |
| `propyl_hexanoate` | 己酸丙酯 | `C9H18O2` | 液 | — | ✓ | 第五批 S0463 | Propyl Hexanoate | 香料 | — | K4 |
| `amyl_hexanoate` | 己酸戊酯 | `C11H22O2` | 液 | — | ✓ | 第五批 S0464 | Amyl Hexanoate | 香料 | — | K4 |
| `isoamyl_hexanoate` | 己酸异戊酯 | `C11H22O2` | 液 | — | ✓ | 第五批 S0465 | Isoamyl Hexanoate | 香料 | — | K4 |
| `hexyl_hexanoate` | 己酸己酯 | `C12H24O2` | 液 | — | ✓ | 第五批 S0466 | Hexyl Hexanoate | 香料 | — | K4 |
| `cis_3_hexenyl_hexanoate` | 己酸顺式-3-己烯酯（己酸叶醇酯） | `C12H22O2` | 液 | — | ✓ | 第五批 S0467 | cis-3-Hexenyl Hexanoate (Leaf Hexanoate) | 香料 | — | K4 |
| `ethyl_heptanoate` | 庚酸乙酯 | `C9H18O2` | 液 | — | ✓ | 第五批 S0468 | Ethyl Heptanoate | 香料 | — | K4 |
| `propyl_heptanoate` | 庚酸丙酯 | `C10H20O2` | 液 | — | ✓ | 第五批 S0469 | Propyl Heptanoate | 香料 | — | K4 |
| `butyl_heptanoate` | 庚酸丁酯 | `C11H22O2` | 液 | — | ✓ | 第五批 S0470 | Butyl Heptanoate | 香料 | — | K4 |
| `methyl_3_furanthiol` | 2-甲基-3-巯基呋喃 | `C5H6OS` | 液 | — | ✓ | 第五批 S0471 | 2-Methyl-3-furanthiol | 香料 | — | K4 |
| `methyl_caprylate` | 辛酸甲酯 | `C9H18O2` | 液 | — | ✓ | 第五批 S0472 | Methyl Caprylate | 香料 | — | K4 |
| `ethyl_caprylate` | 辛酸乙酯 | `C10H20O2` | 液 | — | ✓ | 第五批 S0473 | Ethyl Caprylate | 香料 | — | K4 |
| `ethyl_cis_4_octenoate` | 顺式-4-辛烯酸乙酯 | `C10H18O2` | 液 | — | ✓ | 第五批 S0474 | Ethyl cis-4-Octenoate | 香料 | — | K4 |
| `ethyl_cis_4_7_octadienoate` | 顺式-4,7-辛二烯酸乙酯 | `C10H16O2` | 液 | — | ✓ | 第五批 S0475 | Ethyl cis-4,7-Octadienoate | 香料 | — | K4 |
| `isoamyl_octanoate` | 辛酸异戊酯 | `C13H26O2` | 液 | — | ✓ | 第五批 S0476 | Isoamyl Octanoate | 香料 | — | K4 |
| `nonyl_octanoate` | 辛酸壬酯 | `C17H34O2` | 液 | — | ✓ | 第五批 S0477 | Nonyl Octanoate | 香料 | — | K4 |
| `phenethyl_octanoate` | 辛酸苯乙酯 | `C16H24O2` | 液 | — | ✓ | 第五批 S0478 | Phenethyl Octanoate | 香料 | — | K4 |
| `methyl_2_nonenoate` | 2-壬烯酸甲酯 | `C10H18O2` | 液 | — | ✓ | 第五批 S0479 | Methyl 2-Nonenoate | 香料 | — | K4 |
| `ethyl_nonanoate` | 壬酸乙酯 | `C11H22O2` | 液 | — | ✓ | 第五批 S0480 | Ethyl Nonanoate | 香料 | — | K4 |
| `ethyl_decanoate` | 癸酸乙酯 | `C12H24O2` | 液 | — | ✓ | 第五批 S0481 | Ethyl Decanoate | 香料 | — | K4 |
| `ethyl_decadienoate` | 反式-2-顺式-4-癸二烯酸乙酯 | `C12H20O2` | 液 | — | ✓ | 第五批 S0482 | Ethyl trans-2,cis-4-Decadienoate | 香料 | — | K4 |
| `ethyl_laurate` | 十二酸乙酯（月桂酸乙酯） | `C14H28O2` | 液 | — | ✓ | 第五批 S0483 | Ethyl Dodecanoate (Ethyl Laurate) | 香料 | — | K4 |
| `methyl_myristate` | 十四酸甲酯（肉豆蔻酸甲酯） | `C15H30O2` | 液 | — | ✓ | 第五批 S0484 | Methyl Tetradecanoate (Methyl Myristate) | 香料 | — | K4 |
| `methyl_benzoate` | 苯甲酸甲酯 | `C8H8O2` | 液 | — | ✓ | 第五批 S0485 | Methyl Benzoate | 香料 | — | K4 |
| `ethyl_benzoate` | 苯甲酸乙酯 | `C9H10O2` | 液 | — | ✓ | 第五批 S0486 | Ethyl Benzoate | 香料 | — | K4 |
| `propyl_benzoate` | 苯甲酸丙酯 | `C10H12O2` | 液 | — | ✓ | 第五批 S0487 | Propyl Benzoate | 香料 | — | K4 |
| `hexyl_benzoate` | 苯甲酸己酯 | `C13H18O2` | 液 | — | ✓ | 第五批 S0488 | Hexyl Benzoate | 香料 | — | K4 |
| `benzyl_benzoate` | 苯甲酸苄酯 | `C14H12O2` | 液 | — | ✓ | 第五批 S0489 | Benzyl Benzoate | 香料 | — | K4 |
| `cis_3_hexenyl_benzoate` | 苯甲酸顺式-3-己烯酯（苯甲酸叶醇酯） | `C13H16O2` | 液 | — | ✓ | 第五批 S0490 | cis-3-Hexenyl Benzoate (Leaf Benzoate) | 香料 | — | K4 |
| `methyl_anthranilate` | 邻氨基苯甲酸甲酯 | `C8H9NO2` | 液 | — | ✓ | 第五批 S0491 | Methyl Anthranilate | 香料 | — | K4 |
| `methyl_phenylacetate` | 苯乙酸甲酯 | `C9H10O2` | 液 | — | ✓ | 第五批 S0492 | Methyl Phenylacetate | 香料 | — | K4 |
| `ethyl_phenylacetate` | 苯乙酸乙酯 | `C10H12O2` | 液 | — | ✓ | 第五批 S0493 | Ethyl Phenylacetate | 香料 | — | K4 |
| `isoamyl_phenylacetate` | 苯乙酸异戊酯 | `C13H18O2` | 液 | — | ✓ | 第五批 S0494 | Isoamyl Phenylacetate | 香料 | — | K4 |
| `phenethyl_phenylacetate` | 苯乙酸苯乙酯 | `C16H16O2` | 固 | — | ✓ | 第五批 S0495 | Phenethyl Phenylacetate | 香料 | — | K3 |
| `ethyl_tiglate` | 惕各酸乙酯 | `C7H12O2` | 液 | — | ✓ | 第五批 S0496 | Ethyl Tiglate | 香料 | — | K4 |
| `benzyl_tiglate` | 惕各酸苄酯 | `C12H14O2` | 液 | — | ✓ | 第五批 S0497 | Benzyl Tiglate | 香料 | — | K4 |
| `ethyl_lactate` | 乳酸乙酯 | `C5H10O3` | 液 | — | ✓ | 第五批 S0498 | Ethyl Lactate | 香料 | — | K4 |
| `butyl_lactate` | 乳酸丁酯 | `C7H14O3` | 液 | — | ✓ | 第五批 S0499 | Butyl Lactate | 香料 | — | K4 |
| `methyl_cinnamate` | 肉桂酸甲酯 | `C10H10O2` | 固 | — | ✓ | 第五批 S0500 | Methyl Cinnamate | 香料 | — | K3 |
| `ethyl_cinnamate` | 肉桂酸乙酯 | `C11H12O2` | 液 | — | ✓ | 第五批 S0501 | Ethyl Cinnamate | 香料 | — | K4 |
| `benzyl_cinnamate` | 肉桂酸苄酯 | `C16H14O2` | 固 | — | ✓ | 第五批 S0502 | Benzyl Cinnamate | 香料 | — | K3 |
| `phenethyl_cinnamate` | 肉桂酸苯乙酯 | `C17H16O2` | 固 | — | ✓ | 第五批 S0503 | Phenethyl Cinnamate | 香料 | — | K3 |
| `cinnamyl_cinnamate` | 肉桂酸肉桂酯 | `C18H16O2` | 固 | — | ✓ | 第五批 S0504 | Cinnamyl Cinnamate | 香料 | — | K3 |
| `methyl_salicylate` | 水杨酸甲酯（柳酸甲酯） | `C8H8O3` | 液 | — | ✓ | 第五批 S0505 | Methyl Salicylate | 香料 | — | K4 |
| `ethyl_salicylate` | 水杨酸乙酯（柳酸乙酯） | `C9H10O3` | 液 | — | ✓ | 第五批 S0506 | Ethyl Salicylate | 香料 | — | K4 |
| `isoamyl_salicylate` | 水杨酸异戊酯（柳酸异戊酯） | `C12H16O3` | 液 | — | ✓ | 第五批 S0507 | Isoamyl Salicylate | 香料 | — | K4 |
| `ethyl_myristate` | 十四酸乙酯（肉豆蔻酸乙酯） | `C16H32O2` | 液 | — | ✓ | 第五批 S0508 | Ethyl Tetradecanoate (Ethyl Myristate) | 香料 | — | K7 |
| `ethyl_oleate` | 油酸乙酯 | `C20H38O2` | 液 | — | ✓ | 第五批 S0509 | Ethyl Oleate | 香料 | — | K7 |
| `ethyl_palmitate` | 棕榈酸乙酯 | `C18H36O2` | 固 | — | ✓ | 第五批 S0510 | Ethyl Palmitate | 香料 | — | K7 |
| `methyl_dihydrojasmonate` | 二氢茉莉酮酸甲酯 | `C13H22O3` | 液 | — | ✓ | 第五批 S0511 | Methyl Dihydrojasmonate | 香料 | — | K4 |
| `coconut_acid_ethyl_esters` | 椰子油混合酸乙酯 | 主成分月桂酸乙酯 C14H28O2，另含辛、癸、肉豆蔻、棕榈酸乙酯 | 液 | — |  | 第五批 S0512 | Ethyl Ester of Coconut Oil Mixed Acid | 香料 | — | — |
| `triethyl_citrate` | 柠檬酸三乙酯 | `C12H20O7` | 液 | — | ✓ | 第五批 S0513 | Triethyl Citrate | 香料 | — | K4 |
| `anisyl_formate` | 甲酸大茴香酯 | `C9H10O3` | 液 | — | ✓ | 第五批 S0514 | Anisyl Formate | 香料 | — | K4 |
| `leaf_formate` | 甲酸顺式-3-己烯酯（甲酸叶醇酯） | `C7H12O2` | 液 | — | ✓ | 第五批 S0515 | cis-3-Hexenyl Formate (Leaf Formate) | 香料 | — | K4 |
| `methylbutyl_acetate` | 乙酸2-甲基丁酯 | `C7H14O2` | 液 | — | ✓ | 第五批 S0516 | 2-Methylbutyl Acetate | 香料 | — | K4 |
| `phenylpropyl_acetate` | 乙酸3-苯丙酯 | `C11H14O2` | 液 | — | ✓ | 第五批 S0517 | 3-Phenylpropyl Acetate | 香料 | — | K4 |
| `eugenyl_acetate` | 乙酸丁香酯 | `C12H14O3` | 固 | — | ✓ | 第五批 S0518 | Eugenyl Acetate | 香料 | — | K3 |
| `dimethyl_isobutyl_thiazoline` | 4,5-二甲基-2-异丁基-3-噻唑啉 | `C9H17NS` | 液 | — | ✓ | 第五批 S0519 | 4,5-Dimethyl-2-isobutyl-3-thiazoline | 香料 | — | K4 |
| `isopulegyl_acetate` | 乙酸异胡薄荷酯 | `C12H20O2` | 液 | — | ✓ | 第五批 S0520 | Isopulegyl Acetate | 香料 | — | K4 |
| `fenchyl_acetate` | 乙酸1,3,3-三甲基-2-降龙脑酯 | `C12H20O2` | 液 | — | ✓ | 第五批 S0521 | 1,3,3-Trimethyl-2-norbornanyl Acetate | 香料 | — | K4 |
| `methyl_propionate` | 丙酸甲酯 | `C4H8O2` | 液 | — | ✓ | 第五批 S0522 | Methyl Propionate | 香料 | — | K4 |
| `ethyl_acrylate` | 丙烯酸乙酯 | `C5H8O2` | 液 | — | ✓ | 第五批 S0523 | Ethyl Acrylate | 香料 | — | K4 |
| `leaf_lactate` | 乳酸顺式-3-己烯酯（乳酸叶醇酯） | `C9H16O3` | 液 | — | ✓ | 第五批 S0524 | cis-3-Hexenyl Lactate (Leaf Lactate) | 香料 | — | K4 |
| `decyl_propionate` | 丙酸癸酯 | `C13H26O2` | 液 | — | ✓ | 第五批 S0525 | Decyl Propionate | 香料 | — | K4 |
| `ethyl_crotonate` | 反式-2-丁烯酸乙酯 | `C6H10O2` | 液 | — | ✓ | 第五批 S0526 | Ethyl trans-2-Butenoate | 香料 | — | K4 |
| `propyl_butyrate` | 丁酸丙酯 | `C7H14O2` | 液 | — | ✓ | 第五批 S0527 | Propyl Butyrate | 香料 | — | K4 |
| `isopropyl_isobutyrate` | 异丁酸异丙酯 | `C7H14O2` | 液 | — | ✓ | 第五批 S0528 | Isopropyl Isobutyrate | 香料 | — | K4 |
| `isopropyl_methylbutyrate` | 2-甲基丁酸异丙酯 | `C8H16O2` | 液 | — | ✓ | 第五批 S0529 | Isopropyl 2-Methylbutyrate | 香料 | — | K4 |
| `hexyl_isobutyrate` | 异丁酸己酯 | `C10H20O2` | 液 | — | ✓ | 第五批 S0530 | Hexyl Isobutyrate | 香料 | — | K4 |
| `heptyl_butyrate` | 丁酸庚酯 | `C11H22O2` | 液 | — | ✓ | 第五批 S0531 | Heptyl Butyrate | 香料 | — | K4 |
| `octyl_isobutyrate` | 异丁酸辛酯 | `C12H24O2` | 液 | — | ✓ | 第五批 S0532 | Octyl Isobutyrate | 香料 | — | K4 |
| `phenylpropyl_isobutyrate` | 异丁酸-3-苯丙酯 | `C13H18O2` | 液 | — | ✓ | 第五批 S0533 | 3-Phenylpropyl Isobutyrate | 香料 | — | K4 |
| `citronellyl_butyrate` | 丁酸香茅酯 | `C14H26O2` | 液 | — | ✓ | 第五批 S0534 | Citronellyl Butyrate | 香料 | — | K4 |
| `cinnamyl_butyrate` | 丁酸肉桂酯 | `C13H16O2` | 液 | — | ✓ | 第五批 S0535 | Cinnamyl Butyrate | 香料 | — | K4 |
| `methyl_isovalerate` | 异戊酸甲酯 | `C6H12O2` | 液 | — | ✓ | 第五批 S0536 | Methyl Isovalerate | 香料 | — | K4 |
| `isobutyl_isovalerate` | 异戊酸异丁酯 | `C9H18O2` | 液 | — | ✓ | 第五批 S0537 | Isobutyl Isovalerate | 香料 | — | K4 |
| `methylbutyl_isovalerate` | 异戊酸2-甲基丁酯 | `C10H20O2` | 液 | — | ✓ | 第五批 S0538 | 2-Methylbutyl Isovalerate | 香料 | — | K4 |
| `benzyl_isovalerate` | 异戊酸苄酯 | `C12H16O2` | 液 | — | ✓ | 第五批 S0539 | Benzyl Isovalerate | 香料 | — | K4 |
| `pentylpyridine` | 2-戊基吡啶 | `C10H15N` | 液 | — | ✓ | 第五批 S0540 | 2-Pentylpyridine | 香料 | — | K4 |
| `cinnamyl_isovalerate` | 异戊酸肉桂酯 | `C14H18O2` | 液 | — | ✓ | 第五批 S0541 | Cinnamyl Isovalerate | 香料 | — | K4 |
| `menthyl_isovalerate` | 异戊酸薄荷酯 | `C15H28O2` | 液 | — | ✓ | 第五批 S0542 | Menthyl Isovalerate | 香料 | — | K4 |
| `methyl_3_hexenoate` | 3-己烯酸甲酯 | `C7H12O2` | 液 | — | ✓ | 第五批 S0543 | Methyl 3-Hexenoate | 香料 | — | K4 |
| `isobutyl_hexanoate` | 正己酸异丁酯 | `C10H20O2` | 液 | — | ✓ | 第五批 S0544 | Isobutyl Caproate | 香料 | — | K4 |
| `allyl_hexanoate` | 己酸烯丙酯 | `C9H16O2` | 液 | — | ✓ | 第五批 S0545 | Allyl Hexanoate | 香料 | — | K4 |
| `linalyl_hexanoate` | 己酸芳樟酯 | `C16H28O2` | 液 | — | ✓ | 第五批 S0546 | Linalyl Hexanoate | 香料 | — | K4 |
| `methyl_citronellate` | 3,7-二甲基-6-辛烯酸甲酯 | `C11H20O2` | 液 | — | ✓ | 第五批 S0547 | Methyl 3,7-Dimethyl-6-octenoate | 香料 | — | K4 |
| `methyl_3_nonenoate` | 3-壬烯酸甲酯 | `C10H18O2` | 液 | — | ✓ | 第五批 S0548 | Methyl 3-Nonenoate | 香料 | — | K4 |
| `methyl_9_undecenoate` | 9-十一烯酸甲酯 | `C12H22O2` | 液 | — | ✓ | 第五批 S0549 | Methyl 9-Undecenoate | 香料 | — | K4 |
| `ethyl_undecanoate` | 十一酸乙酯 | `C13H26O2` | 液 | — | ✓ | 第五批 S0550 | Ethyl Undecanoate | 香料 | — | K4 |
| `isopropyl_myristate` | 十四酸异丙酯 | `C17H34O2` | 液 | — | ✓ | 第五批 S0551 | Isopropyl Tetradecanoate (Isopropyl Myristate) | 香料 | — | K7 |
| `methyl_n_methylanthranilate` | N-甲基邻氨基苯甲酸甲酯 | `C9H11NO2` | 液 | — | ✓ | 第五批 S0552 | Methyl N-Methylanthranilate (Dimethyl Anthranilate) | 香料 | — | K4 |
| `ethyl_anthranilate` | 邻氨基苯甲酸乙酯 | `C9H11NO2` | 液 | — | ✓ | 第五批 S0553 | Ethyl Anthranilate | 香料 | — | K4 |
| `isoamyl_benzoate` | 苯甲酸异戊酯 | `C12H16O2` | 液 | — | ✓ | 第五批 S0554 | Isoamyl Benzoate | 香料 | — | K4 |
| `phenethyl_benzoate` | 苯甲酸苯乙酯 | `C15H14O2` | 液 | — | ✓ | 第五批 S0555 | Phenethyl Benzoate | 香料 | — | K4 |
| `isobutyl_phenylacetate` | 苯乙酸异丁酯 | `C12H16O2` | 液 | — | ✓ | 第五批 S0556 | Isobutyl Phenylacetate | 香料 | — | K4 |
| `hexyl_phenylacetate` | 苯乙酸己酯 | `C14H20O2` | 液 | — | ✓ | 第五批 S0557 | Hexyl Phenylacetate | 香料 | — | K4 |
| `ethyl_hydrocinnamate` | 苯丙酸乙酯 | `C11H14O2` | 液 | — | ✓ | 第五批 S0558 | Ethyl 3-Phenylpropionate (Ethyl Hydrocinnamate) | 香料 | — | K4 |
| `methyl_cyclohexanecarboxylate` | 环己基羧酸甲酯 | `C8H14O2` | 液 | — | ✓ | 第五批 S0559 | Methyl Cyclohexanecarboxylate | 香料 | — | K4 |
| `methyl_anisate` | 大茴香酸甲酯 | `C9H10O3` | 固 | — | ✓ | 第五批 S0560 | Methyl p-Anisate | 香料 | — | K3 |
| `ethyl_anisate` | 大茴香酸乙酯 | `C10H12O3` | 液 | — | ✓ | 第五批 S0561 | Ethyl p-Anisate | 香料 | — | K4 |
| `phenethyl_salicylate` | 水杨酸苯乙酯 | `C15H14O3` | 固 | — | ✓ | 第五批 S0562 | Phenethyl Salicylate | 香料 | — | K3 |
| `isoamyl_laurate` | 十二酸异戊酯（月桂酸异戊酯） | `C17H34O2` | 液 | — | ✓ | 第五批 S0563 | Isoamyl Dodecanoate (Isoamyl Laurate) | 香料 | — | K7 |
| `methyl_linoleate_linolenate` | 亚油酸甲酯(48%),亚麻酸甲酯(52%)混合物 | 亚油酸甲酯 C19H34O2（48%）、亚麻酸甲酯 C19H32O2（52%） | 液 | — |  | 第五批 S0564 | Methyl Linoleate (48%) and Methyl Linolenate (52%) Mixture | 香料 | — | — |
| `methyl_jasmonate` | 茉莉酮酸甲酯 | `C13H20O3` | 液 | — | ✓ | 第五批 S0565 | Methyl Jasmonate | 香料 | — | K4 |
| `benzyl_salicylate` | 水杨酸苄酯（柳酸苄酯） | `C14H12O3` | 液 | — | ✓ | 第五批 S0566 | Benzyl Salicylate | 香料 | — | K4 |
| `isobutyl_cinnamate` | 肉桂酸异丁酯 | `C13H16O2` | 液 | — | ✓ | 第五批 S0567 | Isobutyl Cinnamate | 香料 | — | K4 |
| `phenylpropyl_cinnamate` | 肉桂酸3-苯丙酯 | `C18H18O2` | 液 | — | ✓ | 第五批 S0568 | 3-Phenylpropyl Cinnamate | 香料 | — | K4 |
| `diethyl_tartrate` | 酒石酸二乙酯 | `C8H14O6` | 液 | — | ✓ | 第五批 S0569 | Diethyl Tartrate | 香料 | — | K4 |
| `methyl_nicotinate` | 菸酸甲酯 | `C7H7NO2` | 固 | — | ✓ | 第五批 S0570 | Methyl Nicotinate | 香料 | — | K3 |
| `phenethyl_tiglate` | 惕各酸苯乙酯 | `C13H16O2` | 液 | — | ✓ | 第五批 S0571 | Phenethyl Tiglate | 香料 | — | K4 |
| `trimethylhexanol` | 3,5,5-三甲基-1-己醇 | `C9H20O` | 液 | — | ✓ | 第五批 S0573 | 3,5,5-Trimethyl-1-hexanol | 香料 | — | K4 |
| `anisyl_butyrate` | 丁酸茴香酯 | `C12H16O3` | 液 | — | ✓ | 第五批 S0574 | Anisyl Butyrate | 香料 | — | K4 |
| `bornyl_isovalerate` | 异戊酸龙脑酯 | `C15H26O2` | 液 | — | ✓ | 第五批 S0575 | Bornyl Isovalerate | 香料 | — | K4 |
| `dimethylheptanol` | 2,6-二甲基-4-庚醇 | `C9H20O` | 液 | — | ✓ | 第五批 S0576 | 2,6-Dimethyl-4-heptanol | 香料 | — | K4 |
| `isobutyl_benzoate` | 苯甲酸异丁酯 | `C11H14O2` | 液 | — | ✓ | 第五批 S0577 | Isobutyl Benzoate | 香料 | — | K4 |
| `neryl_formate` | 甲酸橙花酯 | `C11H18O2` | 液 | — | ✓ | 第五批 S0578 | Neryl Formate | 香料 | — | K4 |
| `methylbenzyl_acetate` | 乙酸甲基苄醇酯(邻、间、对位混合物) | C10H12O2（邻、间、对位混合物） | 液 | — |  | 第五批 S0579 | Methylbenzyl Acetate (Mixed o-, m-, p-) | 香料 | — | K4 |
| `menthadienyl_acetate` | 乙酸顺式和反式-对1(7),8-䓝二烯-2-醇酯 | `C12H18O2` | 液 | — | ✓ | 第五批 S0580 | cis- and trans-p-1(7),8-Menthadien-2-yl Acetate | 香料 | — | K4 |
| `campholenyl_acetate` | 乙酸龙脑烯醇酯 | `C12H20O2` | 液 | — | ✓ | 第五批 S0581 | Campholene Acetate | 香料 | — | K4 |
| `propyl_propionate` | 丙酸丙酯 | `C6H12O2` | 液 | — | ✓ | 第五批 S0582 | Propyl Propionate | 香料 | — | K4 |
| `butyl_propionate` | 丙酸丁酯 | `C7H14O2` | 液 | — | ✓ | 第五批 S0583 | Butyl Propionate | 香料 | — | K4 |
| `hexyl_propionate` | 丙酸己酯 | `C9H18O2` | 液 | — | ✓ | 第五批 S0584 | Hexyl Propionate | 香料 | — | K4 |
| `ethyl_pyruvate` | 丙酮酸乙酯 | `C5H8O3` | 液 | — | ✓ | 第五批 S0585 | Ethyl Pyruvate | 香料 | — | K4 |
| `octyl_butyrate` | 丁酸辛酯 | `C12H24O2` | 液 | — | ✓ | 第五批 S0586 | Octyl Butyrate | 香料 | — | K4 |
| `propyl_isobutyrate` | 异丁酸丙酯 | `C7H14O2` | 液 | — | ✓ | 第五批 S0587 | n-Propyl Isobutyrate | 香料 | — | K4 |
| `isobutyl_isobutyrate` | 异丁酸异丁酯 | `C8H16O2` | 液 | — | ✓ | 第五批 S0588 | Isobutyl Isobutyrate | 香料 | — | K4 |
| `citronellyl_isobutyrate` | 异丁酸香茅酯 | `C14H26O2` | 液 | — | ✓ | 第五批 S0589 | Citronellyl Isobutyrate | 香料 | — | K4 |
| `leaf_crotonate` | 反式-2-丁烯酸顺式-3-己烯酯 | `C10H16O2` | 液 | — | ✓ | 第五批 S0590 | (Z)-3-Hexenyl (E)-2-Butenoate [Leaf (E)-2-Butenoate] | 香料 | — | K4 |
| `monomenthyl_succinate` | 丁二酸单薄荷酯 | `C14H24O4` | 固 | — | ✓ | 第五批 S0591 | Monomenthyl Succinate | 香料 | — | K3 |
| `pentyl_valerate` | 正戊酸正戊酯 | `C10H20O2` | 液 | — | ✓ | 第五批 S0592 | Pentyl Valerate | 香料 | — | K4 |
| `octyl_isovalerate` | 异戊酸辛酯 | `C13H26O2` | 液 | — | ✓ | 第五批 S0593 | Octyl Isovalerate | 香料 | — | K4 |
| `butyl_hexanoate` | 己酸丁酯 | `C10H20O2` | 液 | — | ✓ | 第五批 S0594 | Butyl Hexanoate | 香料 | — | K4 |
| `phenethyl_hexanoate` | 己酸苯乙酯 | `C14H20O2` | 液 | — | ✓ | 第五批 S0595 | Phenethyl Hexanoate | 香料 | — | K4 |
| `leaf_isobutyrate` | 异丁酸叶醇酯 | `C10H18O2` | 液 | — | ✓ | 第五批 S0596 | Leaf Isobutyrate [(Z)-3-Hexenyl Isobutyrate] | 香料 | — | K4 |
| `hexyl_octanoate` | 辛酸己酯 | `C14H28O2` | 液 | — | ✓ | 第五批 S0597 | Hexyl Octanoate | 香料 | — | K4 |
| `ethyl_2_octenoate` | 2-辛烯酸乙酯 | `C10H18O2` | 液 | — | ✓ | 第五批 S0598 | Ethyl 2-Octenoate | 香料 | — | K4 |
| `ethyl_decatrienoate` | 2,4,7-癸三烯酸乙酯 | `C12H18O2` | 液 | — | ✓ | 第五批 S0599 | Ethyl 2,4,7-Decatrienoate | 香料 | — | K4 |
| `linalyl_benzoate` | 苯甲酸芳樟酯 | `C17H22O2` | 液 | — | ✓ | 第五批 S0600 | Linalyl Benzoate | 香料 | — | K4 |
| `leaf_tiglate` | 反式-2-甲基2-丁烯酸顺式-3-己烯酯（惕各酸叶醇酯） | `C11H18O2` | 液 | — | ✓ | 第五批 S0601 | (Z)-3-Hexenyl (E)-2-Methyl-2-butenoate (Leaf Tiglate) | 香料 | — | K4 |
| `isobutyl_crotonate` | 2-丁烯酸异丁酯 | `C8H14O2` | 液 | — | ✓ | 第五批 S0602 | Isobutyl 2-Butenoate | 香料 | — | K4 |
| `hexyl_isovalerate` | 3-甲基丁酸己酯 | `C11H22O2` | 液 | — | ✓ | 第五批 S0603 | Hexyl 3-Methylbutanoate | 香料 | — | K4 |
| `leaf_3_hexenoate` | 顺式-3-己烯酸顺式-3-己烯酯 | `C12H20O2` | 液 | — | ✓ | 第五批 S0604 | cis-3-Hexenyl cis-3-Hexenoate (Leaf cis-3-Hexenoate) | 香料 | — | K4 |
| `methyl_3_hydroxyhexanoate` | 3-羟基己酸甲酯 | `C7H14O3` | 液 | — | ✓ | 第五批 S0605 | Methyl 3-Hydroxyhexanoate | 香料 | — | K4 |
| `geranyl_benzoate` | 苯甲酸香叶酯 | `C17H22O2` | 液 | — | ✓ | 第五批 S0606 | Geranyl Benzoate | 香料 | — | K4 |
| `dimethyl_succinate` | 琥珀酸二甲酯 | `C6H10O4` | 液 | — | ✓ | 第五批 S0607 | Dimethyl Succinate | 香料 | — | K4 |
| `ethyl_stearate` | 硬脂酸乙酯 | `C20H40O2` | 固 | — | ✓ | 第五批 S0608 | Ethyl Stearate | 香料 | — | K7 |
| `prenyl_acetate` | 3-甲基-2-丁烯-1-醇乙酸酯（乙酸异戊烯酯） | `C7H12O2` | 液 | — | ✓ | 第五批 S0609 | 3-Methyl-2-buten-1-ol Acetate (Prenyl Acetate) | 香料 | — | K4 |
| `hexenyl_hexanoate` | 己酸反式-2-己烯酯 | `C12H22O2` | 液 | — | ✓ | 第五批 S0610 | trans-2-Hexenyl Hexanoate | 香料 | — | K4 |
| `bornyl_formate` | 甲酸龙脑酯 | `C11H18O2` | 液 | — | ✓ | 第五批 S0611 | Bornyl Formate | 香料 | — | K4 |
| `ethyl_4_heptenoate` | 顺式-4-庚烯酸乙酯 | `C9H16O2` | 液 | — | ✓ | 第五批 S0612 | Ethyl (Z)-Hept-4-enoate | 香料 | — | K4 |
| `amyl_octanoate` | 辛酸戊酯 | `C13H26O2` | 液 | — | ✓ | 第五批 S0613 | Amyl Octanoate | 香料 | — | K4 |
| `methyl_4_methylvalerate` | 4-甲基戊酸甲酯 | `C7H14O2` | 液 | — | ✓ | 第五批 S0614 | Methyl 4-Methylvalerate | 香料 | — | K4 |
| `piperonyl_acetate` | 乙酸胡椒醛酯 | `C10H10O4` | 液 | — | ✓ | 第五批 S0615 | Heliotropin Acetate (Piperonyl Acetate) | 香料 | — | K4 |
| `cinnamyl_propionate` | 丙酸肉桂酯 | `C12H14O2` | 液 | — | ✓ | 第五批 S0616 | Cinnamyl Propionate | 香料 | — | K4 |
| `styrallyl_isobutyrate` | 异丁酸甲基苯基原酯 | `C12H16O2` | 液 | — | ✓ | 第五批 S0617 | Methyl Phenyl Carbinyl Isobutyrate (Styrallyl Isobutyrate) | 香料 | — | K4 |
| `dodecyl_isobutyrate` | 异丁酸十二酯 | `C16H32O2` | 液 | — | ✓ | 第五批 S0618 | Dodecyl Isobutyrate | 香料 | — | K4 |
| `terpinyl_isobutyrate` | 异丁酸松油酯 | `C14H24O2` | 液 | — | ✓ | 第五批 S0619 | Terpinyl Isobutyrate | 香料 | — | K4 |
| `isobutyl_salicylate` | 水杨酸异丁酯 | `C11H14O3` | 液 | — | ✓ | 第五批 S0620 | Isobutyl Salicylate | 香料 | — | K4 |
| `isoamyl_cinnamate` | 肉桂酸异戊酯 | `C14H18O2` | 液 | — | ✓ | 第五批 S0621 | Isoamyl Cinnamate | 香料 | — | K4 |
| `isobornyl_acetate` | 乙酸异龙脑酯 | `C12H20O2` | 液 | — | ✓ | 第五批 S0622 | Isobornyl Acetate | 香料 | — | K4 |
| `gamma_valerolactone` | γ-戊内酯 | `C5H8O2` | 液 | — | ✓ | 第五批 S0623 | γ-Valerolactone | 香料 | — | K4 |
| `gamma_hexalactone` | γ-己内酯 | `C6H10O2` | 液 | — | ✓ | 第五批 S0624 | γ-Hexalactone | 香料 | — | K4 |
| `gamma_heptalactone` | γ-庚内酯 | `C7H12O2` | 液 | — | ✓ | 第五批 S0625 | γ-Heptalactone | 香料 | — | K4 |
| `gamma_octalactone` | γ-辛内酯 | `C8H14O2` | 液 | — | ✓ | 第五批 S0626 | γ-Octalactone | 香料 | — | K4 |
| `gamma_nonalactone` | γ-壬内酯 | `C9H16O2` | 液 | — | ✓ | 第五批 S0627 | γ-Nonalactone | 香料 | — | K4 |
| `gamma_dodecalactone` | γ-十二内酯 | `C12H22O2` | 液 | — | ✓ | 第五批 S0629 | γ-Dodecalactone | 香料 | — | K4 |
| `gamma_butyrolactone` | γ-丁内酯 | `C4H6O2` | 液 | — | ✓ | 第五批 S0630 | γ-Butyrolactone | 香料 | — | K4 |
| `delta_hexalactone` | δ-己内酯 | `C6H10O2` | 液 | — | ✓ | 第五批 S0631 | δ-Hexalactone | 香料 | — | K4 |
| `delta_octalactone` | δ-辛内酯 | `C8H14O2` | 液 | — | ✓ | 第五批 S0632 | δ-Octalactone | 香料 | — | K4 |
| `delta_nonalactone` | δ-壬内酯 | `C9H16O2` | 液 | — | ✓ | 第五批 S0633 | δ-Nonalactone | 香料 | — | K4 |
| `delta_decalactone` | δ-癸内酯 | `C10H18O2` | 液 | — | ✓ | 第五批 S0634 | δ-Decalactone | 香料 | — | K4 |
| `delta_undecalactone` | δ-十一内酯 | `C11H20O2` | 液 | — | ✓ | 第五批 S0635 | δ-Undecalactone | 香料 | — | K4 |
| `delta_dodecalactone` | δ-十二内酯 | `C12H22O2` | 液 | — | ✓ | 第五批 S0636 | δ-Dodecalactone | 香料 | — | K4 |
| `pentadecanolide` | 十五内酯 | `C15H28O2` | 固 | — | ✓ | 第五批 S0637 | Pentadecanolide | 香料 | — | K3 |
| `massoia_lactone` | 5-羟基-2-癸烯酸δ-内酯 | `C10H16O2` | 液 | — | ✓ | 第五批 S0638 | 5-Hydroxy-2-decenoic Acid δ-Lactone (Coco Lactone) | 香料 | — | K4 |
| `propylidenephthalide` | 3-丙叉苯酞 | `C11H10O2` | 液 | — | ✓ | 第五批 S0639 | 3-Propylidenephthalide | 香料 | — | K4 |
| `butylidenephthalide` | 3-丁叉苯酞 | `C12H12O2` | 液 | — | ✓ | 第五批 S0640 | 3-Butylidenephthalide | 香料 | — | K4 |
| `mintlactone` | 薄荷内酯 | `C10H14O2` | 液 | — | ✓ | 第五批 S0641 | Mintlactone | 香料 | — | K4 |
| `delta_tridecalactone` | δ-十三内酯 | `C13H24O2` | 液 | — | ✓ | 第五批 S0642 | δ-Tridecalactone | 香料 | — | K4 |
| `delta_tetradecalactone` | δ-十四内酯 | `C14H26O2` | 液 | — | ✓ | 第五批 S0643 | δ-Tetradecalactone | 香料 | — | K4 |
| `pentyl_pyrone` | 5-羟基-2,4-癸二烯酸内酯 | `C10H14O2` | 液 | — | ✓ | 第五批 S0644 | 5-Hydroxy-2,4-decadienoic Acid Lactone (6-Pentyl-α-pyrone) | 香料 | — | K4 |
| `jasmine_lactone` | 5-羟基-7-癸烯酸内酯（茉莉内酯） | `C10H16O2` | 液 | — | ✓ | 第五批 S0645 | 5-Hydroxy-7-decenoic Acid Lactone (Jasmine Lactone) | 香料 | — | K4 |
| `whiskey_lactone` | 威士忌内酯 | `C9H16O2` | 液 | — | ✓ | 第五批 S0646 | Whiskey Lactone | 香料 | — | K4 |
| `dihydroactinidiolide` | 二氢猕猴桃内酯 | `C11H16O2` | 固 | — | ✓ | 第五批 S0647 | Dihydroactinidiolide [(+/-)-(2,6,6-Trimethyl-2-hydroxycyclohexylidene)acetic Acid γ-Lactone] | 香料 | — | K3 |
| `ambrettolide` | 黄葵内酯 | `C16H28O2` | 液 | — | ✓ | 第五批 S0648 | Ambrettolide | 香料 | — | K4 |
| `alpha_angelica_lactone` | α-当归内酯 | `C5H6O2` | 液 | — | ✓ | 第五批 S0649 | α-Angelica Lactone | 香料 | — | K4 |
| `gamma_methyldecalactone` | γ-甲基癸内酯 | `C11H20O2` | 液 | — | ✓ | 第五批 S0650 | γ-Methyldecalactone | 香料 | — | K4 |
| `beta_caryophyllene` | β-石竹烯 | `C15H24` | 液 | — | ✓ | 第五批 S0651 | β-Caryophyllene | 香料 | — | K4 |
| `valencene` | 巴伦西亚橘烯 | `C15H24` | 液 | — | ✓ | 第五批 S0652 | Valencene | 香料 | — | K4 |
| `myrcene` | 月桂烯 | `C10H16` | 液 | — | ✓ | 第五批 S0653 | Myrcene | 香料 | — | K4 |
| `terpinolene` | 异松油烯 | `C10H16` | 液 | — | ✓ | 第五批 S0655 | Terpinolene | 香料 | — | K4 |
| `ocimene` | 罗勒烯 | `C10H16` | 液 | — | ✓ | 第五批 S0656 | Ocimene | 香料 | — | K4 |
| `camphene` | 莰烯 | `C10H16` | 液 | — | ✓ | 第五批 S0657 | Camphene | 香料 | — | K4 |
| `alpha_pinene` | α-蒎烯 | `C10H16` | 液 | — | ✓ | 第五批 S0658 | α-Pinene | 香料 | — | K4 |
| `beta_pinene` | β-蒎烯 | `C10H16` | 液 | — | ✓ | 第五批 S0659 | β-Pinene | 香料 | — | K4 |
| `cineole_1_8` | 1,8-桉叶素 | `C10H18O` | 液 | — | ✓ | 第五批 S0660 | 1,8-Cineole | 香料 | — | K4 |
| `cineole_1_4` | 1,4-桉叶素 | `C10H18O` | 液 | — | ✓ | 第五批 S0661 | 1,4-Cineole | 香料 | — | K4 |
| `dihydrocoumarin` | 二氢香豆素 | `C9H8O2` | 液 | — | ✓ | 第五批 S0662 | Dihydrocoumarin | 香料 | — | K4 |
| `dimethyl_acetyl_cyclohexene` | 1,4-二甲基-4-乙酰基-1-环己烯 | `C10H16O` | 液 | — | ✓ | 第五批 S0663 | 1,4-Dimethyl-4-acetyl-1-cyclohexene | 香料 | — | K4 |
| `myrtenal` | 2-甲酰基-6,6-二甲基双环[3.1.1]庚-2-烯（桃金娘烯醛） | `C10H14O` | 液 | — | ✓ | 第五批 S0664 | 2-Formyl-6,6-dimethylbicyclo[3.1.1]hept-2-ene (Myrtenal) | 香料 | — | K4 |
| `theaspirane` | 茶螺烷 | `C13H22O` | 液 | — | ✓ | 第五批 S0665 | Theaspirane [2,6,10,10-Tetramethyl-1-oxaspiro(4,5)dec-6-ene] | 香料 | — | K4 |
| `undecatriene` | 1,3,5-十一碳三烯 | `C11H18` | 液 | — | ✓ | 第五批 S0666 | 1,3,5-Undecatriene | 香料 | — | K4 |
| `p_alpha_dimethylstyrene` | 对,α-二甲基苯乙烯 | `C10H12` | 液 | — | ✓ | 第五批 S0667 | p,α-Dimethylstyrene | 香料 | — | K4 |
| `alpha_phellandrene` | α-水芹烯 | `C10H16` | 液 | — | ✓ | 第五批 S0668 | α-Phellandrene | 香料 | — | K4 |
| `bisabolene` | 红没药烯 | `C15H24` | 液 | — | ✓ | 第五批 S0669 | Bisabolene | 香料 | — | K4 |
| `gamma_terpinene` | γ-松油烯 | `C10H16` | 液 | — | ✓ | 第五批 S0670 | γ-Terpinene | 香料 | — | K4 |
| `hydroxydihydrotheaspirane` | 6-羟基二氢茶螺烷 | `C13H24O2` | 液 | — | ✓ | 第五批 S0671 | 6-Hydroxydihydrotheaspirane | 香料 | — | K4 |
| `thymol_methyl_ether` | 1-甲基-3-甲氧基-4-异丙基苯 | `C11H16O` | 液 | — | ✓ | 第五批 S0672 | 1-Methyl-3-methoxy-4-isopropylbenzene | 香料 | — | K4 |
| `m_dimethoxybenzene` | 间-二甲氧基苯 | `C8H10O2` | 液 | — | ✓ | 第五批 S0673 | m-Dimethoxybenzene | 香料 | — | K4 |
| `p_cymene` | 对-异丙基甲苯 | `C10H14` | 液 | — | ✓ | 第五批 S0674 | p-Cymene | 香料 | — | K4 |
| `dimethylphenol_3_4` | 3,4-二甲酚 | `C8H10O` | 固 | — | ✓ | 第五批 S0675 | 3,4-Dimethylphenol | 香料 | — | K3 |
| `methylnaphthalene_1` | 1-甲基萘 | `C11H10` | 液 | — | ✓ | 第五批 S0676 | 1-Methylnaphthalene | 香料 | — | K4 |
| `o_dimethoxybenzene` | 1,2-二甲氧基苯 | `C8H10O2` | 液 | — | ✓ | 第五批 S0677 | 1,2-Dimethoxybenzene | 香料 | — | K4 |
| `alpha_farnesene` | α-金合欢烯 | `C15H24` | 液 | — | ✓ | 第五批 S0678 | α-Farnesene | 香料 | — | K4 |
| `styrene` | 苏合香烯 | `C8H8` | 液 | — | ✓ | 第五批 S0679 | Styrene | 香料 | — | K4 |
| `alpha_terpinene` | α-松油烯 | `C10H16` | 液 | — | ✓ | 第五批 S0680 | α-Terpinene | 香料 | — | K4 |
| `carene_3` | 3-蒈烯 | `C10H16` | 液 | — | ✓ | 第五批 S0681 | 3-Carene | 香料 | — | K4 |
| `polylimonene` | 聚苧烯 | `(C10H16)n` | 固 | — |  | 第五批 S0682 | Polylimonene | 香料 | — | K6 |
| `lenthionine` | 香菇素 | `C2H4S5` | 固 | — | ✓ | 第五批 S0683 | Lenthionine | 香料 | — | K3 |
| `caryophyllene_oxide` | 氧化石竹烯 | `C15H24O` | 固 | — | ✓ | 第五批 S0684 | Caryophyllene Oxide | 香料 | — | K3 |
| `paraldehyde` | 2,4,6-三甲基-1,3,5-三氧杂环己烷（三聚乙醛） | `C6H12O3` | 液 | — | ✓ | 第五批 S0685 | 2,4,6-Trimethyl-1,3,5-trioxacyclohexane (Paraldehyde) | 香料 | — | K4 |
| `methyl_mercaptan` | 甲硫醇 | `CH4S` | 气 | — | ✓ | 第五批 S0686；545 同用（2026-11-11 实装时合并 17 的 `methanethiol`，一物一号） | Methyl Mercaptan | 香料 | — | K9 |
| `methylthio_propanol` | 3-甲硫基丙醇 | `C4H10OS` | 液 | — | ✓ | 第五批 S0687 | 3-(Methylthio)propanol | 香料 | — | K4 |
| `butanethiol` | 正丁硫醇 | `C4H10S` | 液 | — | ✓ | 第五批 S0688 | 1-Butanethiol | 香料 | — | K4 |
| `methyl_butanethiol_2_1` | 2-甲基-1-丁硫醇 | `C5H12S` | 液 | — | ✓ | 第五批 S0689 | 2-Methyl-1-butanethiol | 香料 | — | K4 |
| `methylthio_hexanol_3` | 3-(甲硫基)-1-己醇 | `C7H16OS` | 液 | — | ✓ | 第五批 S0690 | 3-(Methylthio)-1-hexanol | 香料 | — | K4 |
| `hexanedithiol` | 1,6-己二硫醇 | `C6H14S2` | 液 | — | ✓ | 第五批 S0691 | 1,6-Hexanedithiol | 香料 | — | K4 |
| `furfuryl_mercaptan` | 糠基硫醇（咖啡醛） | `C5H6OS` | 液 | — | ✓ | 第五批 S0692 | Furfuryl Mercaptan | 香料 | — | K4 |
| `dimethyl_sulfide` | 二甲基硫醚 | `C2H6S` | 液 | — | ✓ | 第五批 S0693 | Dimethyl Sulfide | 香料 | — | K4 |
| `dimethyl_disulfide` | 二甲基二硫醚 | `C2H6S2` | 液 | — | ✓ | 第五批 S0694 | Dimethyl Disulfide | 香料 | — | K4 |
| `dimethyl_trisulfide` | 二甲基三硫醚 | `C2H6S3` | 液 | — | ✓ | 第五批 S0695 | Dimethyl Trisulfide | 香料 | — | K4 |
| `dibutyl_sulfide` | 二丁基硫醚 | `C8H18S` | 液 | — | ✓ | 第五批 S0696 | Dibutyl Sulfide | 香料 | — | K4 |
| `difurfuryl_sulfide` | 2,2'-(硫代二亚甲基)-二呋喃二糠基硫醚 | `C10H10O2S` | 液 | — | ✓ | 第五批 S0697 | 2,2'-(Thiodimethylene)difuran (Difurfuryl Sulfide) | 香料 | — | K4 |
| `difurfuryl_disulfide` | 二糠基二硫醚 | `C10H10O2S2` | 液 | — | ✓ | 第五批 S0698 | Difurfuryl Disulfide | 香料 | — | K4 |
| `o_methylthiophenol` | 邻-甲硫基苯酚 | `C7H8OS` | 液 | — | ✓ | 第五批 S0699 | o-(Methylthio)phenol | 香料 | — | K4 |
| `methylthio_propionaldehyde` | 3-甲硫基丙醛 | `C4H8OS` | 液 | — | ✓ | 第五批 S0700；545 同用（2026-11-11 实装时合并 17 的 `methional`，一物一号） | 3-(Methylthio)propionaldehyde | 香料 | — | K4 |
| `mercaptomenthone_8` | 8-巯基薄荷酮 | `C10H18OS` | 液 | — | ✓ | 第五批 S0701 | p-Mentha-8-thiol-3-one | 香料 | — | K4 |
| `furfuryl_thioacetate` | 硫代乙酸糠酯 | `C7H8O2S` | 液 | — | ✓ | 第五批 S0702 | Furfuryl Thioacetate | 香料 | — | K4 |
| `methyl_methylthiopropionate` | 3-甲硫基丙酸甲酯 | `C5H10O2S` | 液 | — | ✓ | 第五批 S0703 | Methyl 3-Methylthiopropionate | 香料 | — | K4 |
| `ethyl_methylthiopropionate` | 3-甲硫基丙酸乙酯 | `C6H12O2S` | 液 | — | ✓ | 第五批 S0704 | Ethyl 3-Methylthiopropionate | 香料 | — | K4 |
| `indole` | 吲哚 | `C8H7N` | 固 | — | ✓ | 第五批 S0705 | Indole | 香料 | — | K3 |
| `trimethylamine` | 三甲基胺 | `C3H9N` | 气 | — | ✓ | 第五批 S0706 | Trimethylamine | 香料 | — | K9 |
| `rose_oxide` | 玫瑰醚 | `C10H18O` | 液 | — | ✓ | 第五批 S0707 | Rose Oxide | 香料 | — | K4 |
| `hydroxycitronellol` | 羟基香茅醇 | `C10H22O2` | 液 | — | ✓ | 第五批 S0708 | Hydroxycitronellol | 香料 | — | K4 |
| `dimethyl_trithiolane` | 3,5-二甲基-1,2,4-三硫杂环戊烷 | `C4H8S3` | 液 | — | ✓ | 第五批 S0709 | 3,5-Dimethyl-1,2,4-trithiolane | 香料 | — | K4 |
| `methylpyrazine` | 2-甲基吡嗪 | `C5H6N2` | 液 | — | ✓ | 第五批 S0710 | 2-Methylpyrazine | 香料 | — | K4 |
| `dimethylpyrazine_2_3` | 2,3-二甲基吡嗪 | `C6H8N2` | 液 | — | ✓ | 第五批 S0711 | 2,3-Dimethylpyrazine | 香料 | — | K4 |
| `dimethylpyrazine_2_5` | 2,5-二甲基吡嗪 | `C6H8N2` | 液 | — | ✓ | 第五批 S0712 | 2,5-Dimethylpyrazine | 香料 | — | K4 |
| `p_tolylacetaldehyde` | 对-甲苯基乙醛 | `C9H10O` | 液 | — | ✓ | 第五批 S0714 | p-Tolylacetaldehyde | 香料 | — | K4 |
| `cyclocitral` | 2,6,6-三甲基-1 或2-环己烯-1-甲醛 | `C10H16O` | 液 | — | ✓ | 第五批 S0715 | 2,6,6-Trimethyl-1 or 2-Cyclohexene-1-carboxaldehyde | 香料 | — | K4 |
| `isobutyl_methylpyrazine` | 2-异丁基-3-甲基吡嗪 | `C9H14N2` | 液 | — | ✓ | 第五批 S0716 | 2-Isobutyl-3-methylpyrazine | 香料 | — | K4 |
| `methoxy_sec_butylpyrazine` | 2-甲氧基-3-仲丁基吡嗪 | `C9H14N2O` | 液 | — | ✓ | 第五批 S0717 | 2-Methoxy-3-sec-butylpyrazine | 香料 | — | K4 |
| `diethylpyrazine_2_3` | 2,3-二乙基吡嗪 | `C8H12N2` | 液 | — | ✓ | 第五批 S0718 | 2,3-Diethylpyrazine | 香料 | — | K4 |
| `ethyl_dimethylpyrazine_3_2_6` | 3-乙基-2,6-二甲基吡嗪 | `C8H12N2` | 液 | — | ✓ | 第五批 S0719 | 3-Ethyl-2,6-dimethylpyrazine | 香料 | — | K4 |
| `acetylpyrazine` | 2-乙酰基吡嗪 | `C6H6N2O` | 固 | — | ✓ | 第五批 S0720 | Acetylpyrazine | 香料 | — | K3 |
| `acetyl_ethylpyrazine` | 2-乙酰基-3-乙基吡嗪 | `C8H10N2O` | 液 | — | ✓ | 第五批 S0721 | 2-Acetyl-3-ethylpyrazine | 香料 | — | K4 |
| `diethyl_methylpyrazine` | 2,3-二乙基-5-甲基吡嗪 | `C9H14N2` | 液 | — | ✓ | 第五批 S0722 | 2,3-Diethyl-5-methylpyrazine | 香料 | — | K4 |
| `isopropyl_methylpyrazine` | 5-异丙基-2-甲基吡嗪 | `C8H12N2` | 液 | — | ✓ | 第五批 S0723 | 5-Isopropyl-2-methylpyrazine | 香料 | — | K4 |
| `dimethylpyridine_2_6` | 2,6-二甲基吡啶 | `C7H9N` | 液 | — | ✓ | 第五批 S0724 | 2,6-Dimethylpyridine | 香料 | — | K4 |
| `methylthiazole_4` | 4-甲基噻唑 | `C4H5NS` | 液 | — | ✓ | 第五批 S0725 | 4-Methylthiazole | 香料 | — | K4 |
| `alpha_methylcinnamaldehyde` | α-甲基肉桂醛 | `C10H10O` | 液 | — | ✓ | 第五批 S0726 | α-Methylcinnamaldehyde | 香料 | — | K4 |
| `hydroxyethyl_methylthiazole` | 5-羟乙基-4-甲基噻唑 | `C6H9NOS` | 液 | — | ✓ | 第五批 S0727 | 5-Hydroxyethyl-4-methylthiazole | 香料 | — | K4 |
| `trimethylthiazole` | 2,4,5-三甲基噻唑 | `C6H9NS` | 液 | — | ✓ | 第五批 S0728 | 2,4,5-Trimethylthiazole | 香料 | — | K4 |
| `ethyl_methylthiazole` | 2-乙基-4-甲基噻唑 | `C6H9NS` | 液 | — | ✓ | 第五批 S0729 | 2-Ethyl-4-methylthiazole | 香料 | — | K4 |
| `methyl_vinylthiazole` | 5-乙烯基-4-甲基噻唑 | `C6H7NS` | 液 | — | ✓ | 第五批 S0730 | 4-Methyl-5-vinylthiazole | 香料 | — | K4 |
| `acetylthiazole` | 2-乙酰基噻唑 | `C5H5NOS` | 液 | — | ✓ | 第五批 S0731 | 2-Acetylthiazole | 香料 | — | K4 |
| `isopropyl_methylthiazole` | 2-异丙基-4-甲基噻唑 | `C7H11NS` | 液 | — | ✓ | 第五批 S0732 | 2-Isopropyl-4-methylthiazole | 香料 | — | K4 |
| `isobutylthiazole` | 2-异丁基噻唑 | `C7H11NS` | 液 | — | ✓ | 第五批 S0733 | 2-Isobutylthiazole | 香料 | — | K4 |
| `benzothiazole` | 苯并噻唑 | `C7H5NS` | 液 | — | ✓ | 第五批 S0734 | Benzothiazole | 香料 | — | K4 |
| `furfurylpyrrole` | N-糠基吡咯 | `C9H9NO` | 液 | — | ✓ | 第五批 S0735 | N-Furfurylpyrrole | 香料 | — | K4 |
| `acetylpyrrole` | 2-乙酰基吡咯 | `C6H7NO` | 固 | — | ✓ | 第五批 S0736 | 2-Acetylpyrrole | 香料 | — | K3 |
| `tetrahydroquinoxaline` | 5,6,7,8-四氢喹啉 | `C8H10N2` | 液 | — | ✓ | 第五批 S0737 | 5,6,7,8-Tetrahydroquinoxaline | 香料 | — | K4 |
| `trimethyl_oxazoline` | 2,4,5-三甲基-3-噁唑啉 | `C6H11NO` | 液 | — | ✓ | 第五批 S0738 | 2,4,5-Trimethyl-3-oxazoline | 香料 | — | K4 |
| `methyl_propyl_oxathiane` | 2-甲基-4-丙基-1,3-噁唑烷 | `C8H16OS` | 液 | — | ✓ | 第五批 S0739 | 2-Methyl-4-propyl-1,3-oxathiane | 香料 | — | K4 |
| `pyridine` | 吡啶 | `C5H5N` | 液 | — | ✓ | 第五批 S0740 | Pyridine | 香料 | — | K4 |
| `dipropyl_disulfide` | 二丙基二硫醚 | `C6H14S2` | 液 | — | ✓ | 第五批 S0741 | Propyl Disulfide | 香料 | — | K4 |
| `pentanethiol_2` | 2-戊基硫醇 | `C5H12S` | 液 | — | ✓ | 第五批 S0742 | 2-Pentanethiol | 香料 | — | K4 |
| `o_toluenethiol` | 邻-甲基苯硫酚 | `C7H8S` | 液 | — | ✓ | 第五批 S0743 | o-Toluenethiol | 香料 | — | K4 |
| `benzyl_mercaptan` | 苄基硫醇 | `C7H8S` | 液 | — | ✓ | 第五批 S0744 | Benzyl Mercaptan | 香料 | — | K4 |
| `p_menthene_8_thiol` | 1-对-䓝烯-8-硫醇 | `C10H18S` | 液 | — | ✓ | 第五批 S0745 | 1-p-Menthene-8-thiol | 香料 | — | K4 |
| `methyl_propyl_disulfide` | 甲基丙基二硫醚 | `C4H10S2` | 液 | — | ✓ | 第五批 S0746 | Methyl Propyl Disulfide | 香料 | — | K4 |
| `methyl_benzyl_disulfide` | 甲基苄基二硫醚 | `C8H10S2` | 液 | — | ✓ | 第五批 S0747 | Methyl Benzyl Disulfide | 香料 | — | K4 |
| `methyl_furfuryl_disulfide` | 甲基糠基二硫醚 | `C6H8OS2` | 液 | — | ✓ | 第五批 S0748 | Methyl Furfuryl Disulfide | 香料 | — | K4 |
| `diallyl_disulfide` | 烯丙基二硫醚 | `C6H10S2` | 液 | — | ✓ | 第五批 S0749 | Allyl Disulfide | 香料 | — | K4 |
| `bis_methylfuryl_disulfide` | 双(2-甲基-3-呋喃基)二硫醚 | `C10H10O2S2` | 液 | — | ✓ | 第五批 S0750 | Bis(2-methyl-3-furyl) Disulfide | 香料 | — | K4 |
| `furfuryl_methyl_sulfide` | 糠基甲基硫醚 | `C6H8OS` | 液 | — | ✓ | 第五批 S0751 | Furfuryl Methyl Sulfide | 香料 | — | K4 |
| `dimethylthiophenol_2_6` | 2,6-二甲基苯硫酚 | `C8H10S` | 液 | — | ✓ | 第五批 S0752 | 2,6-Dimethylthiophenol | 香料 | — | K4 |
| `methyl_furylacrolein` | 2-甲基-3(2-呋喃基)丙烯醛 | `C8H8O2` | 液 | — | ✓ | 第五批 S0753 | 2-Methyl-3-(2-furyl)acrolein | 香料 | — | K4 |
| `methyltetrahydrothiophenone` | 2-甲基四氢噻吩-3-酮 | `C5H8OS` | 液 | — | ✓ | 第五批 S0754 | 2-Methyltetrahydrothiophen-3-one | 香料 | — | K4 |
| `methyl_methylthiofuran_2_5` | 2-甲基-5-(甲硫基)呋喃 | `C6H8OS` | 液 | — | ✓ | 第五批 S0755 | 2-Methyl-5-(methylthio)furan | 香料 | — | K4 |
| `hydroxy_trimethylcyclohexenone` | 2-羟基-3,5,5-三甲基-2-环己烯酮 | `C9H14O2` | 固 | — | ✓ | 第五批 S0756 | 2-Hydroxy-3,5,5-trimethyl-2-cyclohexenone | 香料 | — | K3 |
| `methyl_furoate` | 糠酸甲酯 | `C6H6O3` | 液 | — | ✓ | 第五批 S0757 | Methyl 2-Furoate | 香料 | — | K4 |
| `ethyl_thioacetate` | 硫代乙酸乙酯 | `C4H8OS` | 液 | — | ✓ | 第五批 S0758 | Ethyl Thioacetate | 香料 | — | K4 |
| `propyl_thioacetate` | 硫代乙酸丙酯 | `C5H10OS` | 液 | — | ✓ | 第五批 S0759 | Propyl Thioacetate | 香料 | — | K4 |
| `ethyl_mercaptopropionate_3` | 3-巯基丙酸乙酯 | `C5H10O2S` | 液 | — | ✓ | 第五批 S0760 | Ethyl 3-Mercaptopropionate | 香料 | — | K4 |
| `methyl_thiobutyrate` | 硫代丁酸甲酯 | `C5H10OS` | 液 | — | ✓ | 第五批 S0761 | Methyl Thiobutyrate | 香料 | — | K4 |
| `allyl_isothiocyanate` | 异硫氰酸烯丙酯 | `C4H5NS` | 液 | — | ✓ | 第五批 S0762 | Allyl Isothiocyanate | 香料 | — | K4 |
| `methyl_thiofuroate` | 2-硫代糠酸甲酯 | `C6H6O2S` | 液 | — | ✓ | 第五批 S0763 | Methyl 2-Thiofuroate | 香料 | — | K4 |
| `methyl_trithiane` | 3-甲基-1,2,4-三噻烷 | `C4H8S3` | 液 | — | ✓ | 第五批 S0764 | 3-Methyl-1,2,4-trithiane | 香料 | — | K4 |
| `tetramethylpyrazine` | 2,3,5,6-四甲基吡嗪 | `C8H12N2` | 固 | — | ✓ | 第五批 S0765 | 2,3,5,6-Tetramethylpyrazine | 香料 | — | K3 |
| `ethylpyrazine` | 2-乙基吡嗪 | `C6H8N2` | 液 | — | ✓ | 第五批 S0766 | 2-Ethylpyrazine | 香料 | — | K4 |
| `ethyl_dimethylpyrazine_isomers` | 2-乙基-3,(5或6)-二甲基吡嗪 | `C8H12N2` | 液 | — | ✓ | 第五批 S0767 | 2-Ethyl-3(5 or 6)-Dimethylpyrazine | 香料 | — | K4 |
| `methoxy_isobutylpyrazine` | 2-甲氧基-3-异丁基吡嗪 | `C9H14N2O` | 液 | — | ✓ | 第五批 S0768 | 2-Methoxy-3-isobutylpyrazine | 香料 | — | K4 |
| `methyl_acetylpyrrole` | 1-甲基-2-乙酰基吡咯 | `C7H9NO` | 液 | — | ✓ | 第五批 S0769 | 1-Methyl-2-acetylpyrrole | 香料 | — | K4 |
| `ethyl_acetylpyrrole` | N-乙基-2-乙酰基吡咯 | `C8H11NO` | 液 | — | ✓ | 第五批 S0770 | 1-Ethyl-2-acetylpyrrole | 香料 | — | K4 |
| `quinoline` | 喹啉 | `C9H7N` | 液 | — | ✓ | 第五批 S0771 | Quinoline | 香料 | — | K4 |
| `methylquinoline_6` | 6-甲基喹啉 | `C10H9N` | 液 | — | ✓ | 第五批 S0772 | 6-Methylquinoline | 香料 | — | K4 |
| `methylquinoxaline_5` | 5-甲基喹噁啉 | `C9H8N2` | 液 | — | ✓ | 第五批 S0773 | 5-Methylquinoxaline | 香料 | — | K4 |
| `piperidine` | 哌啶 | `C5H11N` | 液 | — | ✓ | 第五批 S0774 | Piperidine | 香料 | — | K4 |
| `beta_methylindole` | β-甲基吲哚 | `C9H9N` | 固 | — | ✓ | 第五批 S0775 | β-Methylindole | 香料 | — | K3 |
| `ethyl_methylpyridine` | 5-乙基-2-甲基吡啶 | `C8H11N` | 液 | — | ✓ | 第五批 S0776 | 5-Ethyl-2-methylpyridine | 香料 | — | K4 |
| `ethylpyridine_3` | 3-乙基吡啶 | `C7H9N` | 液 | — | ✓ | 第五批 S0777 | 3-Ethylpyridine | 香料 | — | K4 |
| `acetylpyridine_2` | 2-乙酰基吡啶 | `C7H7NO` | 液 | — | ✓ | 第五批 S0778 | 2-Acetylpyridine | 香料 | — | K4 |
| `acetylpyridine_3` | 3-乙酰基吡啶 | `C7H7NO` | 液 | — | ✓ | 第五批 S0779 | 3-Acetylpyridine | 香料 | — | K4 |
| `cinnamyl_formate` | 甲酸肉桂酯 | `C10H10O2` | 液 | — | ✓ | 第五批 S0780 | Cinnamyl Formate | 香料 | — | K4 |
| `isopentylamine` | 异戊胺 | `C5H13N` | 液 | — | ✓ | 第五批 S0781 | Isopentylamine | 香料 | — | K4 |
| `phenethylamine` | 苯乙胺 | `C8H11N` | 液 | — | ✓ | 第五批 S0782 | Phenethylamine | 香料 | — | K4 |
| `methyl_dithiolane` | 2-甲基-1,3-二硫环戊烷 | `C4H8S2` | 液 | — | ✓ | 第五批 S0783 | 2-Methyl-1,3-dithiolane | 香料 | — | K4 |
| `acetoxydihydrotheaspirane` | 6-乙酰氧基二氢茶螺烷 | `C15H26O3` | 液 | — | ✓ | 第五批 S0784 | 6-Acetoxydihydrotheaspirane | 香料 | — | K4 |
| `dimethylthiazole_4_5` | 4,5-二甲基噻唑 | `C5H7NS` | 液 | — | ✓ | 第五批 S0785 | 4,5-Dimethylthiazole | 香料 | — | K4 |
| `mercaptohexanol_3` | 3-巯基己醇 | `C6H14OS` | 液 | — | ✓ | 第五批 S0786 | 3-Mercaptohexanol | 香料 | — | K4 |
| `trithioacetone` | 三硫丙酮 | `C9H18S3` | 液 | — | ✓ | 第五批 S0787 | Trithioacetone | 香料 | — | K4 |
| `dimethylpyrazine_2_6` | 2,6-二甲基吡嗪 | `C6H8N2` | 液 | — | ✓ | 第五批 S0788 | 2,6-Dimethylpyrazine | 香料 | — | K4 |
| `ethyl_methylthioacetate` | 2-(甲硫基)乙酸乙酯 | `C5H10O2S` | 液 | — | ✓ | 第五批 S0789 | Ethyl 2-(Methylthio)acetate | 香料 | — | K4 |
| `mercaptohexyl_acetate` | 乙酸3-巯基己酯 | `C8H16O2S` | 液 | — | ✓ | 第五批 S0790 | 3-Mercaptohexyl Acetate | 香料 | — | K4 |
| `ethyl_methyldithiopropionate` | 2-(甲基二硫基)丙酸乙酯 | `C6H12O2S2` | 液 | — | ✓ | 第五批 S0791 | Ethyl 2-(Methyldithio)propionate | 香料 | — | K4 |
| `ethyl_methylthiobutyrate` | 3-(甲硫基)丁酸乙酯 | `C7H14O2S` | 液 | — | ✓ | 第五批 S0792 | Ethyl 3-(Methylthio)butyrate | 香料 | — | K4 |
| `mercaptohexyl_butyrate` | 丁酸3-巯基己酯 | `C10H20O2S` | 液 | — | ✓ | 第五批 S0793 | 3-Mercaptohexyl Butyrate | 香料 | — | K4 |
| `mercaptohexyl_hexanoate` | 己酸3-巯基己酯 | `C12H24O2S` | 液 | — | ✓ | 第五批 S0794 | 3-Mercaptohexyl Hexanoate | 香料 | — | K4 |
| `furfuryl_alcohol` | 糠醇 | `C5H6O2` | 液 | — | ✓ | 第五批 S0795 | Furfuryl Alcohol | 香料 | — | K4 |
| `tetrahydrofurfuryl_alcohol` | 四氢糠醇 | `C5H10O2` | 液 | — | ✓ | 第五批 S0796 | Tetrahydrofurfuryl Alcohol | 香料 | — | K4 |
| `ethyl_methylpyrazine_2_3` | 2-乙基-3-甲基吡嗪 | `C7H10N2` | 液 | — | ✓ | 第五批 S0798 | 2-Ethyl-3-methylpyrazine | 香料 | — | K4 |
| `methyl_butanethiol_3_2` | 3-甲基-2-丁硫醇 | `C5H12S` | 液 | — | ✓ | 第五批 S0799 | 3-Methyl-2-butanethiol | 香料 | — | K4 |
| `methyltetrahydrofuranthiol` | 2-甲基-3-四氢呋喃硫醇 | `C5H10OS` | 液 | — | ✓ | 第五批 S0800 | 2-Methyl-3-tetrahydrofuranthiol | 香料 | — | K4 |
| `propanethiol` | 丙硫醇 | `C3H8S` | 液 | — | ✓ | 第五批 S0801 | Propanethiol | 香料 | — | K4 |
| `propane_1_3_dithiol` | 1,3-丙二硫醇 | `C3H8S2` | 液 | — | ✓ | 第五批 S0802 | 1,3-Propanedithiol | 香料 | — | K4 |
| `allyl_mercaptan` | 烯丙基硫醇 | `C3H6S` | 液 | — | ✓ | 第五批 S0803 | Allyl Mercaptan (2-Propene-1-thiol) | 香料 | — | K4 |
| `methoxy_methylbutanethiol` | 4-甲氧基-2-甲基-2-丁硫醇 | `C6H14OS` | 液 | — | ✓ | 第五批 S0804 | 4-Methoxy-2-methyl-2-butanethiol | 香料 | — | K4 |
| `phenylethyl_mercaptan` | 2-苯乙硫醇 | `C8H10S` | 液 | — | ✓ | 第五批 S0805 | 2-Phenylethyl Mercaptan | 香料 | — | K4 |
| `mercapto_3_methylbutanol` | 3-巯基-3-甲基-1-丁醇 | `C5H12OS` | 液 | — | ✓ | 第五批 S0806 | 3-Mercapto-3-methyl-1-butanol | 香料 | — | K4 |
| `methyl_methylfuryl_disulfide` | 甲基2-甲基-3-呋喃基二硫醚 | `C6H8OS2` | 液 | — | ✓ | 第五批 S0807 | Methyl 2-Methyl-3-furyl Disulfide | 香料 | — | K4 |
| `methyl_ethyl_sulfide` | 甲基乙基硫醚 | `C3H8S` | 液 | — | ✓ | 第五批 S0808 | Methyl Ethyl Sulfide | 香料 | — | K4 |
| `methyl_phenyl_disulfide` | 甲基苯基二硫醚 | `C7H8S2` | 液 | — | ✓ | 第五批 S0809 | Methyl Phenyl Disulfide | 香料 | — | K4 |
| `diethyl_sulfide` | 二乙基硫醚 | `C4H10S` | 液 | — | ✓ | 第五批 S0810 | Diethyl Sulfide | 香料 | — | K4 |
| `dipropyl_trisulfide` | 二丙基三硫醚 | `C6H14S3` | 液 | — | ✓ | 第五批 S0811 | Dipropyl Trisulfide | 香料 | — | K4 |
| `propenyl_propyl_disulfide` | 丙烯基丙基二硫醚 | `C6H12S2` | 液 | — | ✓ | 第五批 S0812 | Propenyl Propyl Disulfide | 香料 | — | K4 |
| `diallyl_sulfide` | 二烯丙基硫醚 | `C6H10S` | 液 | — | ✓ | 第五批 S0813 | Allyl Sulfide | 香料 | — | K4 |
| `diallyl_trisulfide` | 二烯丙基三硫醚 | `C6H10S3` | 液 | — | ✓ | 第五批 S0814 | Diallyl Trisulfide | 香料 | — | K4 |
| `diallyl_tetrasulfide` | 二烯丙基四硫醚 | `C6H10S4` | 液 | — | ✓ | 第五批 S0815 | Diallyl Tetrasulfide (Diallyl Polysulfide) | 香料 | — | K4 |
| `methylthiomethyl_butenal` | 2-甲硫甲基-2-丁烯醛 | `C6H10OS` | 液 | — | ✓ | 第五批 S0816 | 2-(Methylthio)methyl-2-butenal | 香料 | — | K4 |
| `methylthio_hexanal` | 3-甲硫基己醛 | `C7H14OS` | 液 | — | ✓ | 第五批 S0817 | 3-Methylthiohexanal | 香料 | — | K4 |
| `cyclohexyl_acetate` | 乙酸环己酯 | `C8H14O2` | 液 | — | ✓ | 第五批 S0818 | Cyclohexyl Acetate | 香料 | — | K4 |
| `o_aminoacetophenone` | 邻-氨基苯乙酮 | `C8H9NO` | 液 | — | ✓ | 第五批 S0819 | o-Aminoacetophenone | 香料 | — | K4 |
| `methyl_methylthio_furan` | 2-甲基-3-甲硫基呋喃 | `C6H8OS` | 液 | — | ✓ | 第五批 S0820 | 2-Methyl-3-(methylthio)furan | 香料 | — | K4 |
| `mercapto_methylbutyl_formate` | 甲酸3-巯基3-甲基丁酯 | `C6H12O2S` | 液 | — | ✓ | 第五批 S0821 | 3-Mercapto-3-methylbutyl Formate | 香料 | — | K4 |
| `methylthiopropyl_acetate` | 乙酸3-甲硫基丙酯 | `C6H12O2S` | 液 | — | ✓ | 第五批 S0822 | 3-(Methylthio)propyl Acetate | 香料 | — | K4 |
| `s_methyl_thioisovalerate` | 3-甲基硫代丁酸S-甲酯 | `C6H12OS` | 液 | — | ✓ | 第五批 S0823 | S-Methyl 3-Methylbutanethioate (Methylthiol Isovalerate) | 香料 | — | K4 |
| `s_methyl_methanethiosulfonate` | 甲硫磺酸S-甲酯 | `C2H6O2S2` | 液 | — | ✓ | 第五批 S0824 | S-Methyl Methanethiosulfonate | 香料 | — | K4 |
| `methyl_methylthiobutyrate` | 2-甲硫基丁酸甲酯 | `C6H12O2S` | 液 | — | ✓ | 第五批 S0825 | Methyl 2-(Methylthio)butyrate | 香料 | — | K4 |
| `methylthiohexyl_acetate` | 3-甲硫基-1-己醇乙酸酯 | `C9H18O2S` | 液 | — | ✓ | 第五批 S0826 | 3-(Methylthio)hexyl Acetate | 香料 | — | K4 |
| `s_methyl_thioacetate` | 甲硫醇乙酸酯 | `C3H6OS` | 液 | — | ✓ | 第五批 S0827 | S-Methyl Thioacetate | 香料 | — | K4 |
| `methyl_dihydrocyclopentapyrazine` | (5H)-5-甲基-6,7-二氢环戊基并(b)吡嗪 | `C8H10N2` | 液 | — | ✓ | 第五批 S0828 | 5-Methyl-6,7-dihydro-5H-cyclopenta[b]pyrazine | 香料 | — | K4 |
| `methoxypyrazine` | 2-甲氧基吡嗪 | `C5H6N2O` | 液 | — | ✓ | 第五批 S0829 | 2-Methoxypyrazine | 香料 | — | K4 |
| `methoxy_methylpyrazine` | 2-,5或6-甲氧基-3-甲基吡嗪 | `C6H8N2O` | 液 | — | ✓ | 第五批 S0830 | 2-, 5- or 6-Methoxy-3-methylpyrazine | 香料 | — | K4 |
| `acetyl_dimethylpyrazine` | 2-乙酰基-3,5(或6)-二甲基吡嗪 | `C8H10N2O` | 液 | — | ✓ | 第五批 S0831 | 2-Acetyl-3,5(or 6)-Dimethylpyrazine | 香料 | — | K4 |
| `acetyl_methylpyrazine` | 2-乙酰基3-甲基吡嗪 | `C7H8N2O` | 液 | — | ✓ | 第五批 S0832 | 2-Acetyl-3-methylpyrazine | 香料 | — | K4 |
| `pyrrolidine` | 四氢吡咯(吡咯烷) | `C4H9N` | 液 | — | ✓ | 第五批 S0833 | Tetrahydropyrrole (Pyrrolidine) | 香料 | — | K4 |
| `isobutylpyridine` | 2-异丁基吡啶 | `C9H13N` | 液 | — | ✓ | 第五批 S0834 | 2-Isobutylpyridine | 香料 | — | K4 |
| `ethyl_dimethyloxazole` | 2-乙基-4,5-二甲基噁唑 | `C7H11NO` | 液 | — | ✓ | 第五批 S0835 | 2-Ethyl-4,5-dimethyloxazole | 香料 | — | K4 |
| `ammonium_sulfide` | 硫化铵 | `(NH4)2S` | 溶液 | — | ✓ | 第五批 S0836 | Ammonium Sulfide | 香料 | — | K1 |
| `ethyl_mercaptopropionate` | 2-巯基丙酸乙酯 | `C5H10O2S` | 液 | — | ✓ | 第五批 S0837 | Ethyl 2-Mercaptopropionate | 香料 | — | K4 |
| `nonivamide` | N-(4-羟基-3-甲氧基苄基)壬酰胺 | `C17H27NO3` | 固 | — | ✓ | 第五批 S0838 | N-(4-Hydroxy-3-methoxybenzyl)nonanamide | 香料 | — | K3 |
| `dithiane_1_4` | 1,4-二噻烷 | `C4H8S2` | 固 | — | ✓ | 第五批 S0839 | 1,4-Dithiane | 香料 | — | K3 |
| `myrtenol` | 桃金娘烯醇 | `C10H16O` | 液 | — | ✓ | 第五批 S0840 | Myrtenol | 香料 | — | K4 |
| `piperine` | 胡椒碱 | `C17H19NO3` | 固 | — | ✓ | 第五批 S0841 | Piperine | 香料 | — | K3 |
| `dimethylbenzofuran` | 2,3-二甲基苯并呋喃 | `C10H10O` | 液 | — | ✓ | 第五批 S0842 | 2,3-Dimethylbenzofuran | 香料 | — | K4 |
| `norfuraneol` | 4-羟基-5-甲基-3(2H)-呋喃酮 | `C5H6O3` | 固 | — | ✓ | 第五批 S0843 | 4-Hydroxy-5-methyl-3(2H)-furanone | 香料 | — | K3 |
| `gamma_ionone` | γ-紫罗兰酮 | `C13H20O` | 液 | — | ✓ | 第五批 S0844 | gamma-Ionone | 香料 | — | K4 |
| `alpha_dihydroionone` | α-二氢紫罗兰酮 | `C13H22O` | 液 | — | ✓ | 第五批 S0845 | Dihydro-alpha-ionone | 香料 | — | K4 |
| `d_piperitone` | d-胡椒酮 | `C10H16O` | 液 | — | ✓ | 第五批 S0846 | d-Piperitone (p-Menth-1-en-3-one) | 香料 | — | K4 |
| `piperitenone` | 胡椒烯酮 | `C10H14O` | 液 | — | ✓ | 第五批 S0847 | Piperitenone (p-Mentha-1,4(8)-dien-3-one) | 香料 | — | K4 |
| `dl_isoleucine` | DL-异亮氨酸 | `C6H13NO2` | 固 | — | ✓ | 第五批 S0849 | DL-Isoleucine | 香料 | — | K3 |
| `pyroligneous_acid_extract` | 焦木酸提取物 | 主成分愈创木酚 C7H8O2、2,6-二甲氧基苯酚等酚类 | 液 | — |  | 第五批 S0850 | Pyroligneous Acid Extract | 香料 | — | — |
| `delta_hexadecalactone` | δ-十六内酯 | `C16H30O2` | 固 | — | ✓ | 第五批 S0857 | delta-Hexadecalactone | 香料 | — | K3 |
| `dihydromintlactone` | (+/-)二氢薄荷内酯 | `C10H16O2` | 液 | — | ✓ | 第五批 S0858 | (+/-)-Dihydromintlactone | 香料 | — | K4 |
| `z4_dodecenal` | 顺式-4-十二烯醛 | `C12H22O` | 液 | — | ✓ | 第五批 S0859 | (Z)-4-Dodecenal | 香料 | — | K4 |
| `epoxy_decenal` | 4,5-环氧反式-2-癸烯醛 | `C10H16O2` | 液 | — | ✓ | 第五批 S0860 | trans-4,5-Epoxy-(E)-2-decenal | 香料 | — | K4 |
| `ethyl_methylpyrazine` | 2-乙基-5-甲基吡嗪 | `C7H10N2` | 液 | — | ✓ | 第五批 S0861 | 2-Ethyl-5-methylpyrazine | 香料 | — | K4 |
| `cis_cis_nonadienol` | 顺式-3-顺式-6-壬二烯-1-醇 | `C9H16O` | 液 | — | ✓ | 第五批 S0862 | (3Z,6Z)-Nona-3,6-dien-1-ol | 香料 | — | K4 |
| `active_amyl_alcohol` | 2-甲基-1-丁醇 | `C5H12O` | 液 | — | ✓ | 第五批 S0863 | 2-Methyl-1-butanol | 香料 | — | K4 |
| `isoborneol` | 异龙脑 | `C10H18O` | 固 | — | ✓ | 第五批 S0864 | Isoborneol | 香料 | — | K3 |
| `nonan_2_ol` | 2-壬醇 | `C9H20O` | 液 | — | ✓ | 第五批 S0865 | 2-Nonanol | 香料 | — | K4 |
| `trans_2_octenol` | 反式-2-辛烯-1-醇 | `C8H16O` | 液 | — | ✓ | 第五批 S0866 | (E)-2-Octen-1-ol | 香料 | — | K4 |
| `carveol` | 香芹醇 | `C10H16O` | 液 | — | ✓ | 第五批 S0867 | Carveol | 香料 | — | K4 |
| `carvomenthone` | 对-䓝烷-2-酮 | `C10H18O` | 液 | — | ✓ | 第五批 S0868 | p-Menthan-2-one | 香料 | — | K4 |
| `mesityl_oxide` | 4-甲基-3-戊烯-2-酮 | `C6H10O` | 液 | — | ✓ | 第五批 S0869 | 4-Methyl-3-penten-2-one (Mesityl Oxide) | 香料 | — | K4 |
| `octadienone` | 反式,反式-3,5-辛二烯-2-酮 | `C8H12O` | 液 | — | ✓ | 第五批 S0870 | (E,E)-3,5-Octadien-2-one | 香料 | — | K4 |
| `methylfuran` | 2-甲基呋喃 | `C5H6O` | 液 | — | ✓ | 第五批 S0871 | 2-Methylfuran | 香料 | — | K4 |
| `decenone` | 3-癸烯-2-酮 | `C10H18O` | 液 | — | ✓ | 第五批 S0872 | 3-Decen-2-one | 香料 | — | K4 |
| `octen_4_one` | 2-辛烯-4-酮 | `C8H14O` | 液 | — | ✓ | 第五批 S0873 | 2-Octen-4-one | 香料 | — | K4 |
| `furyl_acetone` | 2-呋喃基-2-丙酮 | `C7H8O2` | 液 | — | ✓ | 第五批 S0874 | 1-(2-Furyl)-2-propanone | 香料 | — | K4 |
| `methyl_hexanedione` | 5-甲基-2,3-己二酮 | `C7H12O2` | 液 | — | ✓ | 第五批 S0875 | 5-Methyl-2,3-hexanedione | 香料 | — | K4 |
| `methyl_pentenoic_acid` | 2-甲基-3-戊烯酸 | `C6H10O2` | 液 | — | ✓ | 第五批 S0876 | 2-Methyl-3-pentenoic Acid | 香料 | — | K4 |
| `oxoglutaric_acid` | 2-氧代戊二酸 | `C5H6O5` | 固 | — | ✓ | 第五批 S0878 | 2-Oxopentanedioic Acid (alpha-Ketoglutaric Acid) | 香料 | — | K3 |
| `anisic_acid` | 4-茴香酸 | `C8H8O3` | 固 | — | ✓ | 第五批 S0879 | 4-Anisic Acid | 香料 | — | K3 |
| `linoleic_acid` | 亚油酸 | `C18H32O2` | 液 | — | ✓ | 第五批 S0880 | Linoleic Acid | 香料 | — | K7 |
| `l_glutamine` | L-谷氨酰胺 | `C5H10N2O3` | 固 | — | ✓ | 第五批 S0884 | L-Glutamine | 香料 | — | K3 |
| `isopropyl_mercaptan` | 2-丙硫醇 | `C3H8S` | 液 | — | ✓ | 第五批 S0885 | 2-Propanethiol | 香料 | — | K4 |
| `mercapto_methylpentanone` | 4-巯基-4-甲基-2-戊酮 | `C6H12OS` | 液 | — | ✓ | 第五批 S0886 | 4-Mercapto-4-methyl-2-pentanone | 香料 | — | K4 |
| `ethanedithiol` | 1,2-乙二硫醇 | `C2H6S2` | 液 | — | ✓ | 第五批 S0887 | 1,2-Ethanedithiol | 香料 | — | K4 |
| `prenyl_mercaptan` | 异戊烯基硫醇 | `C5H10S` | 液 | — | ✓ | 第五批 S0888 | Prenyl Mercaptan | 香料 | — | K4 |
| `methylmethionine_sulfonium_chloride` | d,l-(3-氨基-3-羧基丙基)二甲基氯化锍 | `C6H14ClNO2S` | 固 | — | ✓ | 第五批 S0889 | DL-(3-Amino-3-carboxypropyl)dimethylsulfonium Chloride (DL-Methylmethionine Sulfonium Chloride) | 香料 | — | K3 |
| `methyl_thioacetoxy_dihydrofuran` | 2-甲基-3-硫代乙酰氧基-4,5-二氢呋喃 | `C7H10O2S` | 液 | — | ✓ | 第五批 S0890 | 2-Methyl-3-thioacetoxy-4,5-dihydrofuran | 香料 | — | K4 |
| `isobutyl_mercaptan` | 异丁基硫醇 | `C4H10S` | 液 | — | ✓ | 第五批 S0891 | Isobutyl Mercaptan | 香料 | — | K4 |
| `benzenethiol` | 苯硫酚(原名称为苄基硫醇) | `C6H6S` | 液 | — | ✓ | 第五批 S0892 | Benzenethiol | 香料 | — | K4 |
| `benzyl_isothiocyanate` | 异硫氰酸苄酯 | `C8H7NS` | 液 | — | ✓ | 第五批 S0893 | Benzyl Isothiocyanate | 香料 | — | K4 |
| `allyl_methyl_trisulfide` | 甲基烯丙基三硫醚 | `C4H8S3` | 液 | — | ✓ | 第五批 S0894 | Allyl Methyl Trisulfide | 香料 | — | K4 |
| `pentylthiophene` | 2-戊基噻吩 | `C9H14S` | 液 | — | ✓ | 第五批 S0895 | 2-Pentylthiophene | 香料 | — | K4 |
| `diethyl_trithiolane` | 3,5-二乙基-1,2,4-三硫杂环戊烷 | `C6H12S3` | 液 | — | ✓ | 第五批 S0896 | 3,5-Diethyl-1,2,4-trithiolane | 香料 | — | K4 |
| `thiophene` | 噻吩 | `C4H4S` | 液 | — | ✓ | 第五批 S0897 | Thiophene | 香料 | — | K4 |
| `trimethyl_dithiazine` | 2,4,6-三甲基二氢-4H-1,3,5-二噻嗪 | `C6H13NS2` | 固 | — | ✓ | 第五批 S0898 | 2,4,6-Trimethyldihydro-4H-1,3,5-dithiazine (Thialdine) | 香料 | — | K3 |
| `methylthiopropyl_isothiocyanate` | 异硫氰酸3-甲硫基丙酯 | `C5H9NS2` | 液 | — | ✓ | 第五批 S0899 | 3-(Methylthio)propyl Isothiocyanate | 香料 | — | K4 |
| `isoamyl_mercaptan` | 3-甲基丁基硫醇 | `C5H12S` | 液 | — | ✓ | 第五批 S0900 | 3-Methylbutanethiol | 香料 | — | K4 |
| `acetyl_thiazoline` | 2-乙酰基-2-噻唑啉 | `C5H7NOS` | 液 | — | ✓ | 第五批 S0901 | 2-Acetyl-2-thiazoline | 香料 | — | K4 |
| `methyl_propyl_trisulfide` | 甲基丙基三硫醚 | `C4H10S3` | 液 | — | ✓ | 第五批 S0902 | Methyl Propyl Trisulfide | 香料 | — | K4 |
| `thiazole` | 噻唑 | `C3H3NS` | 液 | — | ✓ | 第五批 S0903 | Thiazole | 香料 | — | K4 |
| `pyrazine` | 吡嗪 | `C4H4N2` | 固 | — | ✓ | 第五批 S0904 | Pyrazine | 香料 | — | K3 |
| `methyl_propenyl_disulfide` | 甲基1-丙烯基二硫醚 | `C4H8S2` | 液 | — | ✓ | 第五批 S0905 | Methyl 1-Propenyl Disulfide | 香料 | — | K4 |
| `propyl_formate` | 甲酸丙酯 | `C4H8O2` | 液 | — | ✓ | 第五批 S0906 | Propyl Formate | 香料 | — | K4 |
| `vanillin_menthoxypropanediol_acetal` | 香兰素3-(l-䓝氧基)丙-1,2-二醇缩醛 | `C21H32O5` | 液 | — | ✓ | 第五批 S0907 | Vanillin 3-(l-Menthoxy)propane-1,2-diol Acetal | 香料 | — | K4 |
| `pentenone` | 3-戊烯-2-酮 | `C5H8O` | 液 | — | ✓ | 第五批 S0908 | 3-Penten-2-one | 香料 | — | K4 |
| `methyl_laurate` | 十二酸甲酯（月桂酸甲酯） | `C13H26O2` | 液 | — | ✓ | 第五批 S0909 | Methyl Dodecanoate (Methyl Laurate) | 香料 | — | K7 |
| `perillyl_acetate` | 乙酸紫苏酯 | `C12H18O2` | 液 | — | ✓ | 第五批 S0910 | Perillyl Acetate (p-Mentha-1,8-dien-7-yl Acetate) | 香料 | — | K4 |
| `diethyl_malate` | 苹果酸二乙酯 | `C8H14O5` | 液 | — | ✓ | 第五批 S0911 | Diethyl Malate | 香料 | — | K4 |
| `methyl_methylthioacetate` | 甲硫基乙酸甲酯 | `C4H8O2S` | 液 | — | ✓ | 第五批 S0912 | Methyl (Methylthio)acetate | 香料 | — | K4 |
| `acetyl_pyrroline` | 2-乙酰基-1-吡咯啉 | `C6H9NO` | 液 | — | ✓ | 第五批 S0913 | 2-Acetyl-1-pyrroline | 香料 | — | K4 |
| `isopropyl_formate` | 甲酸异丙酯 | `C4H8O2` | 液 | — | ✓ | 第五批 S0914 | Isopropyl Formate | 香料 | — | K4 |
| `methyl_pentenal` | 4-甲基-2-戊烯醛 | `C6H10O` | 液 | — | ✓ | 第五批 S0915 | 4-Methyl-2-pentenal | 香料 | — | K4 |
| `ethyl_linoleate` | 亚油酸乙酯 | `C20H36O2` | 液 | — | ✓ | 第五批 S0916 | Ethyl Linoleate | 香料 | — | K7 |
| `triisobutyl_dithiazine` | 2,4,6-三异丁基-5,6-二氢-4H-1,3,5-二噻嗪 | `C15H31NS2` | 液 | — | ✓ | 第五批 S0917 | 2,4,6-Triisobutyl-5,6-dihydro-4H-1,3,5-dithiazine | 香料 | — | K4 |
| `dodecyl_acetate` | 乙酸十二醇酯 | `C14H28O2` | 液 | — | ✓ | 第五批 S0918 | Dodecyl Acetate | 香料 | — | K4 |
| `ethylbutyraldehyde` | 2-乙基丁醛 | `C6H12O` | 液 | — | ✓ | 第五批 S0919 | 2-Ethylbutyraldehyde | 香料 | — | K4 |
| `octyl_octanoate` | 辛酸辛酯 | `C16H32O2` | 液 | — | ✓ | 第五批 S0920 | Octyl Caprylate (Octyl Octanoate) | 香料 | — | K4 |
| `hexanal_diethyl_acetal` | 己醛二乙缩醛 | `C10H22O2` | 液 | — | ✓ | 第五批 S0921 | Hexanal Diethyl Acetal | 香料 | — | K4 |
| `isopropyl_propionate` | 丙酸异丙酯 | `C6H12O2` | 液 | — | ✓ | 第五批 S0922 | Isopropyl Propionate | 香料 | — | K4 |
| `trans_2_hexenyl_butyrate` | 丁酸反式-2-己烯酯 | `C10H18O2` | 液 | — | ✓ | 第五批 S0923 | trans-2-Hexenyl Butyrate | 香料 | — | K4 |
| `butyl_isothiocyanate` | 异硫氰酸丁酯 | `C5H9NS` | 液 | — | ✓ | 第五批 S0924 | Butyl Isothiocyanate | 香料 | — | K4 |
| `gluconyl_ethanolamine` | N-葡糖酰基乙醇胺 | `C8H17NO7` | 固 | — | ✓ | 第五批 S0925 | N-Gluconyl Ethanolamine | 香料 | — | K3 |
| `lactoyl_ethanolamine` | N-乳酰基乙醇胺 | `C5H11NO3` | 液 | — | ✓ | 第五批 S0926 | N-Lactoyl Ethanolamine | 香料 | — | K4 |
| `hepten_3_ol` | 1-庚烯-3-醇 | `C7H14O` | 液 | — | ✓ | 第五批 S0927 | 1-Hepten-3-ol | 香料 | — | K4 |
| `ethanethiol` | 乙硫醇 | `C2H6S` | 液 | — | ✓ | 第五批 S0928 | Ethanethiol | 香料 | — | K4 |
| `l_bornyl_acetate` | 乙酸l-龙脑酯 | `C12H20O2` | 液 | — | ✓ | 第五批 S0930 | l-Bornyl Acetate | 香料 | — | K4 |
| `trans_alpha_damascone` | 反式-α-突厥酮 | `C13H20O` | 液 | — | ✓ | 第五批 S0931 | trans-alpha-Damascone | 香料 | — | K4 |
| `diethyl_disulfide` | 二乙基二硫醚 | `C4H10S2` | 液 | — | ✓ | 第五批 S0932 | Diethyl Disulfide | 香料 | — | K4 |
| `dimethylfuranone` | 2,5-二甲基-3(2H)-呋喃酮 | `C6H8O2` | 液 | — | ✓ | 第五批 S0933 | 2,5-Dimethyl-3(2H)-furanone | 香料 | — | K4 |
| `geranic_acid` | 香叶酸 | `C10H16O2` | 液 | — | ✓ | 第五批 S0934 | Geranic Acid | 香料 | — | K4 |
| `hydroxy_methylthienyl_ethanone` | 1-(3-羟基-5-甲基-2-噻吩)乙酮 | `C7H8O2S` | 固 | — | ✓ | 第五批 S0935 | 1-(3-Hydroxy-5-methyl-2-thienyl)ethanone | 香料 | — | K3 |
| `isoambrettolide` | 异黄葵内酯 | `C16H28O2` | 液 | — | ✓ | 第五批 S0936 | Isoambrettolide | 香料 | — | K4 |
| `isobornyl_isobutyrate` | 异丁酸异龙脑酯 | `C14H24O2` | 液 | — | ✓ | 第五批 S0937 | Isobornyl Isobutyrate | 香料 | — | K4 |
| `isobutyl_n_methylanthranilate` | N-甲基邻氨基苯甲酸异丁酯 | `C12H17NO2` | 液 | — | ✓ | 第五批 S0938 | Isobutyl N-Methylanthranilate | 香料 | — | K4 |
| `methionyl_butyrate` | 丁酸3-(甲硫基)丙酯 | `C8H16O2S` | 液 | — | ✓ | 第五批 S0939 | 3-(Methylthio)propyl Butyrate (Methionyl Butyrate) | 香料 | — | K4 |
| `methoxy_heptanethiol` | (S1)-甲氧基-3-庚硫醇 | `C8H18OS` | 液 | — | ✓ | 第五批 S0940 | (S)-1-Methoxy-3-heptanethiol | 香料 | — | K4 |
| `methyl_z5_octenoate` | 5-Z-辛烯酸甲酯 | `C9H16O2` | 液 | — | ✓ | 第五批 S0941 | Methyl (Z)-5-Octenoate | 香料 | — | K4 |
| `methyl_n_acetylanthranilate` | N-乙酰基邻氨基苯甲酸甲酯 | `C10H11NO3` | 固 | — | ✓ | 第五批 S0942 | Methyl N-Acetylanthranilate | 香料 | — | K3 |
| `rosefuran` | 3-甲基-2-(3-甲基-2-丁烯)呋喃 | `C10H14O` | 液 | — | ✓ | 第五批 S0943 | 3-Methyl-2-(3-methylbut-2-enyl)furan (Rosefuran) | 香料 | — | K4 |
| `phytyl_acetate` | 乙酸植醇酯 | `C22H42O2` | 液 | — | ✓ | 第五批 S0944 | Phytyl Acetate | 香料 | — | K4 |
| `farnesyl_acetate` | 3,7,11-三甲基十二碳-2,6,10-三烯醇乙酸酯 | `C17H28O2` | 液 | — | ✓ | 第五批 S0945 | 3,7,11-Trimethyldodeca-2,6,10-trienyl Acetate (Farnesyl Acetate) | 香料 | — | K4 |
| `triethylamine` | 三乙胺 | `C6H15N` | 液 | — | ✓ | 第五批 S0946 | Triethylamine | 香料 | — | K4 |
| `anisyl_propionate` | 丙酸茴香酯 | `C11H14O3` | 液 | — | ✓ | 第五批 S0947 | Anisyl Propionate | 香料 | — | K4 |
| `acetoin_butyrate` | 丁酸3-丁酮-2-醇酯 | `C8H14O3` | 液 | — | ✓ | 第五批 S0948 | Butan-3-one-2-yl Butanoate | 香料 | — | K4 |
| `isoquinoline` | 异喹啉 | `C9H7N` | 固 | — | ✓ | 第五批 S0949 | Isoquinoline | 香料 | — | K3 |
| `propionylthiazole` | 2-丙酰噻唑 | `C6H7NOS` | 液 | — | ✓ | 第五批 S0950 | 2-Propionylthiazole | 香料 | — | K4 |
| `isopropyl_dimethyl_dithiazine` | 2(4)-异丙基-4(2),6-二甲基二氢(4H)-1,3,5-二噻嗪 | `C8H17NS2` | 液 | — | ✓ | 第五批 S0951 | 2(4)-Isopropyl-4(2),6-dimethyldihydro-4H-1,3,5-dithiazine | 香料 | — | K4 |
| `terpinyl_butyrate` | 丁酸松油酯 | `C14H24O2` | 液 | — | ✓ | 第五批 S0952 | Terpinyl Butyrate | 香料 | — | K4 |
| `butylphthalide` | 3-正丁基苯酞 | `C12H14O2` | 液 | — | ✓ | 第五批 S0953 | 3-n-Butylphthalide | 香料 | — | K4 |
| `dimethyl_methylpropenyl_thf` | 2,2-二甲基-5-(1-甲基-1-丙烯基)四氢呋喃 | `C10H18O` | 液 | — | ✓ | 第五批 S0954 | 2,2-Dimethyl-5-(1-methylpropen-1-yl)tetrahydrofuran | 香料 | — | K4 |
| `r_piperitone` | (6R)-3-甲基-6-(1-甲基乙基)-2-环己烯-1-酮 | `C10H16O` | 液 | — | ✓ | 第五批 S0955 | 2-Cyclohexen-1-one, 3-Methyl-6-(1-methylethyl)-, (6R)- ((R)-Piperitone) | 香料 | — | K4 |
| `prenol` | 3-甲基-2-丁烯-1-醇 | `C5H10O` | 液 | — | ✓ | 第五批 S0956 | 3-Methyl-2-buten-1-ol | 香料 | — | K4 |
| `p_menthenyl_acetate` | 对-䓝-1-烯-9-醇乙酸酯 | `C12H20O2` | 液 | — | ✓ | 第五批 S0957 | 1-p-Menthen-9-yl Acetate | 香料 | — | K4 |
| `octenyl_acetate` | 乙酸2-辛烯醇酯 | `C10H18O2` | 液 | — | ✓ | 第五批 S0958 | 2-Octen-1-yl Acetate | 香料 | — | K4 |
| `p_methoxyphenylacetone` | 1-(对-甲氧基苯基)-2-丙酮 | `C10H12O2` | 液 | — | ✓ | 第五批 S0959 | 1-(p-Methoxyphenyl)-2-propanone | 香料 | — | K4 |
| `butyl_stearate` | 十八酸丁酯（硬脂酸丁酯） | `C22H44O2` | 液 | — | ✓ | 第五批 S0960 | Butyl Octadecanoate (Butyl Stearate) | 香料 | — | K7 |
| `alpha_methylbenzyl_mercaptan` | (+/-)-1-苯乙基硫醇 | `C8H10S` | 液 | — | ✓ | 第五批 S0961 | (±)-1-Phenylethyl Mercaptan | 香料 | — | K4 |
| `cryptone` | 4-异丙基-2-环己烯酮 | `C9H14O` | 液 | — | ✓ | 第五批 S0962 | 4-Isopropyl-2-cyclohexenone | 香料 | — | K4 |
| `methyl_o_methoxybenzoate` | 邻-甲氧基苯甲酸甲酯 | `C9H10O3` | 液 | — | ✓ | 第五批 S0963 | Methyl o-Methoxybenzoate | 香料 | — | K4 |
| `pyruvaldehyde` | 丙酮醛 | `C3H4O2` | 溶液 | — | ✓ | 第五批 S0964 | Pyruvaldehyde | 香料 | — | K4 |
| `methyl_ethyl_trisulfide` | 甲基乙基三硫醚 | `C3H8S3` | 液 | — | ✓ | 第五批 S0965 | Methyl Ethyl Trisulfide | 香料 | — | K4 |
| `methyldithio_isobutyraldehyde` | 2-甲基-2-(甲二硫基)-丙醛 | `C5H10OS2` | 液 | — | ✓ | 第五批 S0966 | 2-Methyl-2-(methyldithio)propanal | 香料 | — | K4 |
| `bis_methylthio_methane` | 二(甲硫基)甲烷 | `C3H8S2` | 液 | — | ✓ | 第五批 S0967 | Bis(methylthio)methane | 香料 | — | K4 |
| `trithiahexane` | 2,3,5-三硫杂己烷 | `C3H8S3` | 液 | — | ✓ | 第五批 S0968 | 2,3,5-Trithiahexane | 香料 | — | K4 |
| `ethyloctanoic_acid` | 4-乙基辛酸 | `C10H20O2` | 液 | — | ✓ | 第五批 S0969 | 4-Ethyloctanoic Acid | 香料 | — | K7 |
| `dihydronootkatone` | 二氢诺卡酮 | `C15H24O` | 液 | — | ✓ | 第五批 S0970 | Dihydronootkatone | 香料 | — | K4 |
| `prenyl_ethyl_ether` | 1-乙氧基-3-甲基-2-丁烯 | `C7H14O` | 液 | — | ✓ | 第五批 S0971 | 1-Ethoxy-3-methyl-2-butene | 香料 | — | K4 |
| `dehydroxy_linalool_oxide` | 2-乙烯基-2-甲基-5-(1-甲基乙烯基)四氢呋喃 | `C10H16O` | 液 | — | ✓ | 第五批 S0972 | 2-Ethenyl-2-methyl-5-(1-methylethenyl)tetrahydrofuran | 香料 | — | K4 |
| `furfuryl_isovalerate` | 异戊酸糠酯 | `C10H14O3` | 液 | — | ✓ | 第五批 S0973 | Furfuryl Isovalerate | 香料 | — | K4 |
| `linalyl_isovalerate` | 异戊酸芳樟酯 | `C15H26O2` | 液 | — | ✓ | 第五批 S0974 | Linalyl Isovalerate | 香料 | — | K4 |
| `methyl_isopropyl_carbinol` | 3-甲基-2-丁醇 | `C5H12O` | 液 | — | ✓ | 第五批 S0975 | 3-Methyl-2-butanol | 香料 | — | K4 |
| `methylpentanol` | 3-甲基-1-戊醇 | `C6H14O` | 液 | — | ✓ | 第五批 S0976 | 3-Methyl-1-pentanol | 香料 | — | K4 |
| `methyl_isobutyl_ketone` | 4-甲基-2-戊酮 | `C6H12O` | 液 | — | ✓ | 第五批 S0977 | 4-Methyl-2-pentanone | 香料 | — | K4 |
| `nonadienol_3e_6z` | 反式-3-顺式-6-壬二烯醇 | `C9H16O` | 液 | — | ✓ | 第五批 S0978 | trans-3-cis-6-Nonadienol | 香料 | — | K4 |
| `methyl_heptanoate` | 庚酸甲酯 | `C8H16O2` | 液 | — | ✓ | 第五批 S0979 | Methyl Heptanoate | 香料 | — | K4 |
| `cis_hexenyl_propionate` | 顺式-3-己烯醇丙酸酯 | `C9H16O2` | 液 | — | ✓ | 第五批 S0980 | (Z)-3-Hexenyl Propionate | 香料 | — | K4 |
| `ethyl_trans_2_decenoate` | 反式-2-癸烯酸乙酯 | `C12H22O2` | 液 | — | ✓ | 第五批 S0981 | Ethyl trans-2-Decenoate | 香料 | — | K4 |
| `o_ethylphenol` | 2-乙基苯酚 | `C8H10O` | 液 | — | ✓ | 第五批 S0982 | 2-Ethylphenol | 香料 | — | K4 |
| `methylpyrrole_carboxaldehyde` | N-甲基吡咯-2-甲醛 | `C6H7NO` | 液 | — | ✓ | 第五批 S0984 | N-Methylpyrrole-2-carboxaldehyde | 香料 | — | K4 |
| `vanillin_acetate` | 乙酸香兰素酯 | `C10H10O4` | 固 | — | ✓ | 第五批 S0985 | Vanillin Acetate | 香料 | — | K3 |
| `l_histidine` | L-组氨酸 | `C6H9N3O2` | 固 | — | ✓ | 第五批 S0986 | L-Histidine | 香料 | — | K3 |
| `delta_damascone` | δ-突厥酮 | `C13H20O` | 液 | — | ✓ | 第五批 S0987 | δ-Damascone | 香料 | — | K4 |
| `ethyl_methylpentanoate` | 2-甲基戊酸乙酯 | `C8H16O2` | 液 | — | ✓ | 第五批 S0988 | Ethyl 2-Methylpentanoate | 香料 | — | K4 |
| `methylthioethyl_methyl_ketone` | 4-甲硫基-2-丁酮 | `C5H10OS` | 液 | — | ✓ | 第五批 S0989 | 4-Methylthio-2-butanone | 香料 | — | K4 |
| `menthyl_lactate` | 乳酸l-薄荷酯 | `C13H24O3` | 固 | — | ✓ | 第五批 S0990 | l-Menthyl Lactate | 香料 | — | K3 |
| `methyl_methylbutenyl_disulfide` | 甲基3-甲基-1-丁烯基二硫醚 | `C6H12S2` | 液 | — | ✓ | 第五批 S0991 | Methyl 3-Methyl-1-butenyl Disulfide | 香料 | — | K4 |
| `mercaptoacetone` | 1-巯基-2-丙酮 | `C3H6OS` | 液 | — | ✓ | 第五批 S0992 | 1-Mercapto-2-propanone | 香料 | — | K4 |
| `amyl_acetate` | 乙酸正戊酯 | `C7H14O2` | 液 | — | ✓ | 第五批 S0993 | Pentyl Acetate | 香料 | — | K4 |
| `pulegone` | 胡薄荷酮 | `C10H16O` | 液 | — | ✓ | 第五批 S0994 | Pulegone | 香料 | — | K4 |
| `alpha_ethylbenzyl_alcohol` | 1-苯基丙醇-1 | `C9H12O` | 液 | — | ✓ | 第五批 S0995 | 1-Phenylpropan-1-ol | 香料 | — | K4 |
| `methyl_phenethyl_carbinol` | 4-苯基2-丁醇 | `C10H14O` | 液 | — | ✓ | 第五批 S0996 | 4-Phenyl-2-butanol | 香料 | — | K4 |
| `ethyl_butyl_carbinol` | 庚醇-3 | `C7H16O` | 液 | — | ✓ | 第五批 S0997 | Heptan-3-ol | 香料 | — | K4 |
| `methyl_acetoxyhexanoate` | 3-乙酰氧基己酸甲酯 | `C9H16O4` | 液 | — | ✓ | 第五批 S0998 | Methyl 3-Acetoxyhexanoate | 香料 | — | K4 |
| `piperitol` | 对-䓝-1-烯-3-醇 | `C10H18O` | 液 | — | ✓ | 第五批 S0999 | p-Menth-1-en-3-ol | 香料 | — | K4 |
| `sabinene_hydrate` | 4-苧醇 | `C10H18O` | 液 | — | ✓ | 第五批 S1000 | 4-Thujanol | 香料 | — | K4 |
| `cis_hexenyl_pyruvate` | 丙酮酸顺式-3-己烯酯（丙酮酸叶醇酯） | `C9H14O3` | 液 | — | ✓ | 第五批 S1001 | cis-3-Hexenyl Pyruvate (Leaf Pyruvate) | 香料 | — | K4 |
| `biphenyl` | 联苯 | `C12H10` | 固 | — | ✓ | 第五批 S1002 | Biphenyl | 香料 | — | K3 |
| `z_dodecenolactone` | 顺式-4-羟基-6-十二烯酸内酯 | `C12H20O2` | 液 | — | ✓ | 第五批 S1003 | (Z)-4-Hydroxy-6-dodecenoic Acid Lactone | 香料 | — | K4 |
| `dimethyl_sulfoxide` | 甲基亚磺酰甲烷 | `C2H6OS` | 液 | — | ✓ | 第五批 S1004 | Methylsulfinylmethane | 香料 | — | K4 |
| `methyl_geranate` | 3,7-二甲基-2,6-辛二烯酸甲酯（香叶酸甲酯） | `C11H18O2` | 液 | — | ✓ | 第五批 S1005 | Methyl 3,7-Dimethyl-2,6-octadienoate (Methyl Geranate) | 香料 | — | K4 |
| `dimethylnonadienone` | 反式和顺式-4,8-二甲基-3,7-壬二烯-2-酮 | `C11H18O` | 液 | — | ✓ | 第五批 S1006 | (E)- and (Z)-4,8-Dimethyl-3,7-nonadien-2-one | 香料 | — | K4 |
| `isopentylidene_isopentylamine` | 异亚戊基异戊胺 | `C10H21N` | 液 | — | ✓ | 第五批 S1007 | Isopentylidene Isopentylamine | 香料 | — | K4 |
| `isoamyl_valerate` | 戊酸异戊酯 | `C10H20O2` | 液 | — | ✓ | 第五批 S1008 | Isoamyl Valerate | 香料 | — | K4 |
| `trans_hexenyl_propionate` | 丙酸反式-2-己烯酯 | `C9H16O2` | 液 | — | ✓ | 第五批 S1009 | trans-2-Hexenyl Propionate (2-Hexen-1-ol, Propanoate, (E)-) | 香料 | — | K4 |
| `hydrogen_sulfide` | 硫化氢(仅用于热反应香料) | `H2S` | 气 | — | ✓ | 第五批 S1010 | Hydrogen Sulfide | 香料 | — | K9 |
| `methyl_valerate` | 戊酸甲酯 | `C6H12O2` | 液 | — | ✓ | 第五批 S1011 | Methyl Valerate | 香料 | — | K4 |
| `isopropyl_butyrate` | 丁酸异丙酯 | `C7H14O2` | 液 | — | ✓ | 第五批 S1012 | Isopropyl Butyrate | 香料 | — | K4 |
| `allyl_methyl_disulfide` | 烯丙基甲基二硫醚 | `C4H8S2` | 液 | — | ✓ | 第五批 S1013 | Allyl Methyl Disulfide | 香料 | — | K4 |
| `ethyl_hexyl_ketone` | 3-壬酮 | `C9H18O` | 液 | — | ✓ | 第五批 S1014 | 3-Nonanone | 香料 | — | K4 |
| `dibenzyl_disulfide` | 二苄基二硫醚 | `C14H14S2` | 固 | — | ✓ | 第五批 S1015 | Benzyl Disulfide | 香料 | — | K3 |
| `cis_hexenyl_phenylacetate` | 苯乙酸顺式-3-己烯酯（苯乙酸叶醇酯） | `C14H18O2` | 液 | — | ✓ | 第五批 S1016 | cis-3-Hexenyl Phenylacetate (Leaf Phenylacetate) | 香料 | — | K4 |
| `acetylmercaptohexyl_acetate` | 乙酸3-(乙酰巯基)己酯 | `C10H18O3S` | 液 | — | ✓ | 第五批 S1017 | 3-Acetylmercaptohexyl Acetate | 香料 | — | K4 |
| `s_methyl_hexanethioate` | 己酸甲硫醇酯 | `C7H14OS` | 液 | — | ✓ | 第五批 S1018 | S-Methyl Hexanethioate (Methyl Thiohexanoate) | 香料 | — | K4 |
| `crotonic_acid` | 反式-2-丁烯酸（巴豆酸） | `C4H6O2` | 固 | — | ✓ | 第五批 S1019 | (E)-2-Butenoic Acid (Crotonic Acid) | 香料 | — | K3 |
| `cis_hexenyl_valerate` | 戊酸顺式-3-己烯酯（戊酸叶醇酯） | `C11H20O2` | 液 | — | ✓ | 第五批 S1020 | (Z)-3-Hexenyl Valerate (Leaf Valerate) | 香料 | — | K4 |
| `benzyl_hexanoate` | 己酸苄酯 | `C13H18O2` | 液 | — | ✓ | 第五批 S1021 | Benzyl Hexanoate | 香料 | — | K4 |
| `allyl_propyl_disulfide` | 烯丙基丙基二硫醚 | `C6H12S2` | 液 | — | ✓ | 第五批 S1022 | Allyl Propyl Disulfide | 香料 | — | K4 |
| `thiocineole` | 2,8-表硫-顺式-对-䓝烷 4,7,7-三甲基-6-硫杂双环[3.2.1]辛烷 硫代桉叶素 | `C10H18S` | 液 | — | ✓ | 第五批 S1023 | 2,8-Epithio-cis-p-menthane (4,7,7-Trimethyl-6-thiabicyclo[3.2.1]octane; Thiocineole) | 香料 | — | K4 |
| `methyl_decanoate` | 癸酸甲酯 | `C11H22O2` | 液 | — | ✓ | 第五批 S1024 | Methyl Decanoate | 香料 | — | K7 |
| `isobutyl_formate` | 甲酸异丁酯 | `C5H10O2` | 液 | — | ✓ | 第五批 S1025 | Isobutyl Formate | 香料 | — | K4 |
| `dipropyl_ketone` | 4-庚酮 | `C7H14O` | 液 | — | ✓ | 第五批 S1026 | 4-Heptanone | 香料 | — | K4 |
| `butyl_valerate` | 戊酸丁酯 | `C9H18O2` | 液 | — | ✓ | 第五批 S1027 | Butyl Valerate | 香料 | — | K4 |
| `cyclohexyl_butyrate` | 丁酸环己酯 | `C10H18O2` | 液 | — | ✓ | 第五批 S1028 | Cyclohexyl Butyrate | 香料 | — | K4 |
| `ethyl_sorbate` | 山梨酸乙酯 | `C8H12O2` | 液 | — | ✓ | 第五批 S1029 | Ethyl Sorbate (Ethyl 2,4-Hexadienoate) | 香料 | — | K4 |
| `glyceryl_monooleate` | 单油酸甘油酯 | `C21H40O4` | 固 | — | ✓ | 第五批 S1030 | Glyceryl Monooleate | 香料 | — | K8 |
| `butyroin` | 5-羟基-4-辛酮 | `C8H16O2` | 液 | — | ✓ | 第五批 S1031 | 5-Hydroxy-4-octanone | 香料 | — | K4 |
| `methyl_nonanoate` | 壬酸甲酯 | `C10H20O2` | 液 | — | ✓ | 第五批 S1032 | Methyl Nonanoate | 香料 | — | K4 |
| `neryl_propionate` | 丙酸橙花酯 | `C13H22O2` | 液 | — | ✓ | 第五批 S1033 | Neryl Propionate | 香料 | — | K4 |
| `propyl_cinnamate` | 肉桂酸丙酯 | `C12H14O2` | 液 | — | ✓ | 第五批 S1034 | Propyl Cinnamate | 香料 | — | K4 |
| `rhodinyl_butyrate` | 丁酸玫瑰酯 | `C14H26O2` | 液 | — | ✓ | 第五批 S1035 | Rhodinyl Butyrate | 香料 | — | K4 |
| `rhodinyl_isobutyrate` | 异丁酸玫瑰酯 | `C14H26O2` | 液 | — | ✓ | 第五批 S1036 | Rhodinyl Isobutyrate | 香料 | — | K4 |
| `terpinyl_propionate` | 丙酸松油酯 | `C13H22O2` | 液 | — | ✓ | 第五批 S1037 | Terpinyl Propionate | 香料 | — | K4 |
| `furfuryl_propionate` | 丙酸糠酯 | `C8H10O3` | 液 | — | ✓ | 第五批 S1038 | Furfuryl Propionate | 香料 | — | K4 |
| `furfuryl_valerate` | 戊酸糠酯 | `C10H14O3` | 液 | — | ✓ | 第五批 S1039 | Furfuryl Pentanoate | 香料 | — | K4 |
| `isojasmone` | 异茉莉酮 | `C11H18O` | 液 | — | ✓ | 第五批 S1040 | Isojasmone | 香料 | — | K4 |
| `benzyl_methyl_sulfide` | 苄基甲基硫醚 | `C8H10S` | 液 | — | ✓ | 第五批 S1041 | Benzyl Methyl Sulfide | 香料 | — | K4 |
| `prenal` | 3-甲基-2-丁烯醛 | `C5H8O` | 液 | — | ✓ | 第五批 S1042 | 3-Methyl-2-butenal | 香料 | — | K4 |
| `propyl_decadienoate` | 2,4-癸二烯酸丙酯 | `C13H22O2` | 液 | — | ✓ | 第五批 S1043 | Propyl 2,4-Decadienoate | 香料 | — | K4 |
| `hexyl_trans_hexenoate` | 反式-2-己烯酸己酯 | `C12H22O2` | 液 | — | ✓ | 第五批 S1044 | Hexyl trans-2-Hexenoate | 香料 | — | K4 |
| `methoxyeugenol` | 4-烯丙基-2,6-二甲氧基苯酚 | `C11H14O3` | 液 | — | ✓ | 第五批 S1045 | 4-Allyl-2,6-dimethoxyphenol | 香料 | — | K4 |
| `methyl_leucate` | 2-羟基-4-甲基戊酸甲酯 | `C7H14O3` | 液 | — | ✓ | 第五批 S1046 | Methyl 2-Hydroxy-4-methylpentanoate | 香料 | — | K4 |
| `methyl_trans_octenoate` | 反式-2-辛烯酸甲酯 | `C9H16O2` | 液 | — | ✓ | 第五批 S1047 | Methyl trans-2-Octenoate | 香料 | — | K4 |
| `trimethyl_vinyl_tetrahydropyran` | 2,2,6-三甲基-6-乙烯基四氢吡喃 | `C10H18O` | 液 | — | ✓ | 第五批 S1048 | 2,2,6-Trimethyl-6-vinyltetrahydropyran | 香料 | — | K4 |
| `sclareolide` | 香紫苏内酯 | `C16H26O2` | 固 | — | ✓ | 第五批 S1049 | Sclareolide [Decahydro-3a,6,6,9a-tetramethylnaphtho(2,1-b)furan-2(1H)-one] | 香料 | — | K3 |
| `s_methyl_thiobenzoate` | 苯甲酸甲硫醇酯 | `C8H8OS` | 液 | — | ✓ | 第五批 S1050 | S-Methyl Benzothioate | 香料 | — | K4 |
| `cis_hexenyl_trans_hexenoate` | 反式-2-己烯酸顺式-3-己烯酯 | `C12H20O2` | 液 | — | ✓ | 第五批 S1051 | (Z)-3-Hexenyl (E)-2-Hexenoate | 香料 | — | K4 |
| `o_methoxythiophenol` | 2-巯基苯甲醚 | `C7H8OS` | 液 | — | ✓ | 第五批 S1052 | 2-Mercaptoanisole | 香料 | — | K4 |
| `vanillin_butanediol_acetal` | 香兰素苏和赤-2,3-丁二醇缩醛 | `C12H16O4` | 液 | — | ✓ | 第五批 S1053 | Vanillin Erythro- and Threo-butan-2,3-diol Acetal | 香料 | — | K4 |
| `isoamylidene_acetone` | 反式6-甲基-3-庚烯-2-酮 | `C8H14O` | 液 | — | ✓ | 第五批 S1054 | (E)-6-Methyl-3-hepten-2-one | 香料 | — | K4 |
| `ethyl_mercaptobutyrate` | (±)3-巯基丁酸乙酯 | `C6H12O2S` | 液 | — | ✓ | 第五批 S1055 | (±)-Ethyl 3-Mercaptobutyrate | 香料 | — | K4 |
| `mercapto_2_methylpentanol` | 3-巯基-2-甲基戊醇 | `C6H14OS` | 液 | — | ✓ | 第五批 S1056 | 3-Mercapto-2-methylpentan-1-ol | 香料 | — | K4 |
| `acetaldehyde_diisoamyl_acetal` | 乙醛二异戊醇缩醛 | `C12H26O2` | 液 | — | ✓ | 第五批 S1057 | Acetaldehyde Diisoamyl Acetal | 香料 | — | K4 |
| `phenyl_methylhexenal` | (+/-)-2-苯基-4-甲基-2-己烯醛 | `C13H16O` | 液 | — | ✓ | 第五批 S1058 | (±)-2-Phenyl-4-methyl-2-hexenal | 香料 | — | K4 |
| `sec_heptyl_mercaptan` | 2-庚硫醇 | `C7H16S` | 液 | — | ✓ | 第五批 S1059 | 2-Heptanethiol | 香料 | — | K4 |
| `wine_lactone` | 2-(2-羟基-4-甲基-3-环己烯基)-丙酸γ-内酯 | `C10H14O2` | 液 | — | ✓ | 第五批 S1060 | 2-(2-Hydroxy-4-methyl-3-cyclohexenyl)propionic Acid γ-Lactone (Wine Lactone) | 香料 | — | K4 |
| `menthyl_methyl_ether` | l-䓝基甲基醚 | `C11H22O` | 液 | — | ✓ | 第五批 S1061 | l-Menthyl Methyl Ether (2-Isopropyl-5-methylcyclohexyl Methyl Ether) | 香料 | — | K4 |
| `isopropyl_hexanoate` | 己酸异丙酯 | `C9H18O2` | 液 | — | ✓ | 第五批 S1062 | Isopropyl Hexanoate | 香料 | — | K4 |
| `sorbyl_alcohol` | 2,4-己二烯-1-醇 | `C6H10O` | 液 | — | ✓ | 第五批 S1063 | 2,4-Hexadien-1-ol | 香料 | — | K4 |
| `methyl_palmitate` | 十六烷酸甲酯 | `C17H34O2` | 固 | — | ✓ | 第五批 S1064 | Methyl Hexadecanoate | 香料 | — | K7 |
| `methylthiophene_carboxaldehyde` | 5-甲基-2-噻吩甲醛 | `C6H6OS` | 液 | — | ✓ | 第五批 S1065 | 5-Methyl-2-thiophenecarboxaldehyde | 香料 | — | K4 |
| `methylsyringol` | 4-甲基-2,6-二甲氧基苯酚 | `C9H12O3` | 固 | — | ✓ | 第五批 S1066 | 4-Methyl-2,6-dimethoxyphenol | 香料 | — | K3 |
| `p_methoxycinnamaldehyde` | 对-甲氧基肉桂醛 | `C10H10O2` | 固 | — | ✓ | 第五批 S1067 | p-Methoxycinnamaldehyde | 香料 | — | K3 |
| `trimethyloxazole` | 2,4,5-三甲基噁唑 | `C6H9NO` | 液 | — | ✓ | 第五批 S1068 | 2,4,5-Trimethyloxazole | 香料 | — | K4 |
| `benzaldehyde_diethyl_acetal` | 苯甲醛二乙缩醛 | `C11H16O2` | 液 | — | ✓ | 第五批 S1069 | Benzaldehyde Diethyl Acetal | 香料 | — | K4 |
| `d_neomenthol` | d-新薄荷醇 | `C10H20O` | 液 | — | ✓ | 第五批 S1070 | d-Neomenthol | 香料 | — | K4 |
| `pentyl_furanone` | 2-壬烯酸γ-内酯 | `C9H14O2` | 液 | — | ✓ | 第五批 S1071 | 2-Nonenoic Acid gamma-Lactone | 香料 | — | K4 |
| `ethyl_trans_4_decenoate` | 反式-4-癸烯酸乙酯 | `C12H22O2` | 液 | — | ✓ | 第五批 S1072 | Ethyl trans-4-Decenoate | 香料 | — | K4 |
| `tuberose_lactone` | 晚香玉内酯{二氢-5-[(Z,Z)-2,5-辛二烯-2(3H)-呋喃酮} | `C12H18O2` | 液 | — | ✓ | 第五批 S1073 | Tuberose Lactone {Dihydro-5-[(Z,Z)-octa-2,5-dienyl]-2(3H)-furanone} | 香料 | — | K4 |
| `hexanal_pg_acetal` | 4-甲基-2-戊基-1,3-二氧戊环 | `C9H18O2` | 液 | — | ✓ | 第五批 S1074 | 4-Methyl-2-pentyl-1,3-dioxolane (Hexanal Propylene Glycol Acetal) | 香料 | — | K4 |
| `mercaptoheptyl_acetate` | 乙酸3-巯基庚酯 | `C9H18O2S` | 液 | — | ✓ | 第五批 S1075 | 3-Mercaptoheptyl Acetate | 香料 | — | K4 |
| `phytol` | 植醇(3,7,11,15-四甲基-2-十六烯-1-醇) | `C20H40O` | 液 | — | ✓ | 第五批 S1077 | Phytol (3,7,11,15-Tetramethyl-2-hexadecen-1-ol) | 香料 | — | K4 |
| `isovaleraldehyde_diethyl_acetal` | 异戊醛二乙缩醛 | `C9H20O2` | 液 | — | ✓ | 第五批 S1078 | Isovaleraldehyde Diethyl Acetal | 香料 | — | K4 |
| `butenyl_isothiocyanate` | 异硫氰酸3-丁烯酯 | `C5H7NS` | 液 | — | ✓ | 第五批 S1079 | 3-Butenyl Isothiocyanate | 香料 | — | K4 |
| `pentenyl_isothiocyanate` | 异硫氰酸4-戊烯酯 | `C6H9NS` | 液 | — | ✓ | 第五批 S1080 | 4-Pentenyl Isothiocyanate | 香料 | — | K4 |
| `hexenyl_isothiocyanate` | 异硫氰酸5-己烯酯 | `C7H11NS` | 液 | — | ✓ | 第五批 S1081 | 5-Hexenyl Isothiocyanate | 香料 | — | K4 |
| `oleyl_acetate` | 顺式-9-十八烯醇乙酸酯（乙酸油醇酯） | `C20H38O2` | 液 | — | ✓ | 第五批 S1082 | cis-9-Octadecenyl Acetate (Oleyl Acetate) | 香料 | — | K7 |
| `furfuryl_methyl_ether` | 糠基甲基醚 | `C6H8O2` | 液 | — | ✓ | 第五批 S1083 | Furfuryl Methyl Ether | 香料 | — | K4 |
| `ethyl_propyl_ketone` | 3-己酮 | `C6H12O` | 液 | — | ✓ | 第五批 S1084 | 3-Hexanone | 香料 | — | K4 |
| `sec_butyl_isothiocyanate` | 异硫氰酸2-丁酯 | `C5H9NS` | 液 | — | ✓ | 第五批 S1085 | 2-Butyl Isothiocyanate | 香料 | — | K4 |
| `isobutyl_isothiocyanate` | 异硫氰酸异丁酯 | `C5H9NS` | 液 | — | ✓ | 第五批 S1086 | Isobutyl Isothiocyanate | 香料 | — | K4 |
| `methylthiohexyl_isothiocyanate` | 异硫氰酸6-(甲硫基)己酯 | `C8H15NS2` | 液 | — | ✓ | 第五批 S1087 | 6-(Methylthio)hexyl Isothiocyanate | 香料 | — | K4 |
| `methylthiopentyl_isothiocyanate` | 异硫氰酸5-(甲硫基)戊酯 | `C7H13NS2` | 液 | — | ✓ | 第五批 S1088 | 5-(Methylthio)pentyl Isothiocyanate | 香料 | — | K4 |
| `amyl_isothiocyanate` | 异硫氰酸戊酯 | `C6H11NS` | 液 | — | ✓ | 第五批 S1089 | Amyl Isothiocyanate | 香料 | — | K4 |
| `isopropyl_isothiocyanate` | 异硫氰酸异丙酯 | `C4H7NS` | 液 | — | ✓ | 第五批 S1090 | Isopropyl Isothiocyanate | 香料 | — | K4 |
| `isoamyl_isothiocyanate` | 异硫氰酸异戊酯 | `C6H11NS` | 液 | — | ✓ | 第五批 S1091 | Isoamyl Isothiocyanate | 香料 | — | K4 |
| `dimethylfuran` | 2,5-二甲基呋喃 | `C6H8O` | 液 | — | ✓ | 第五批 S1092 | 2,5-Dimethylfuran | 香料 | — | K4 |
| `cycloionone` | 环紫罗兰酮 | `C13H20O` | 液 | — | ✓ | 第五批 S1093 | Cycloionone | 香料 | — | K4 |
| `isovaleraldehyde_pg_acetal` | 2-异丁基-4-甲基-1,3-二氧戊环 | `C8H16O2` | 液 | — | ✓ | 第五批 S1094 | 2-Isobutyl-4-methyl-1,3-dioxolane (Isovaleraldehyde Propylene Glycol Acetal) | 香料 | — | K4 |
| `isobutyraldehyde_pg_acetal` | 顺式和反式-2-异丙基-4-甲基-1,3-二氧戊环 | `C7H14O2` | 液 | — | ✓ | 第五批 S1095 | cis- and trans-2-Isopropyl-4-methyl-1,3-dioxolane (Isobutyraldehyde Propylene Glycol Acetal) | 香料 | — | K4 |
| `gamma_aminobutyric_acid` | 4-氨基丁酸 | `C4H9NO2` | 固 | — | ✓ | 第五批 S1096 | 4-Aminobutyric Acid (gamma-Aminobutyric Acid) | 香料 | — | K3 |
| `dimethoxycinnamoyl_homoveratrylamine` | N-[2-(3,4-二甲氧基苯基)乙基]-3,4-二甲氧基肉桂酸酰胺 | `C21H25NO5` | 固 | — | ✓ | 第五批 S1097 | N-[2-(3,4-Dimethoxyphenyl)ethyl]-3,4-dimethoxycinnamic Acid Amide | 香料 | — | K3 |
| `dipropenyl_sulfide` | 二-(1-丙烯基)硫醚(异构体混合物) | `C6H10S` | 液 | — | ✓ | 第五批 S1098 | Di-(1-propenyl) Sulfide (Mixture of Isomers) | 香料 | — | K4 |
| `sec_amyl_acetate` | 乙酸2-戊酯 | `C7H14O2` | 液 | — | ✓ | 第五批 S1099 | 2-Pentyl Acetate | 香料 | — | K4 |
| `ethylamine` | 乙胺 | `C2H7N` | 气 | — | ✓ | 第五批 S1100 | Ethylamine | 香料 | — | K9 |
| `methialdol` | 2,8-二硫杂-4-壬烯-4-甲醛[5-(甲硫基)-2-(甲硫基甲基)-2-戊烯醛] | `C8H14OS2` | 液 | — | ✓ | 第五批 S1101 | 2,8-Dithianon-4-ene-4-carboxaldehyde [5-(Methylthio)-2-(methylthiomethyl)-2-pentenal; Methialdol] | 香料 | — | K4 |
| `butenyl_methyl_sulfide` | 1-丁烯-1-基甲基硫醚 | `C5H10S` | 液 | — | ✓ | 第五批 S1102 | 1-Buten-1-yl Methyl Sulfide | 香料 | — | K4 |
| `diisopropyl_disulfide` | 二异丙基二硫醚 | `C6H14S2` | 液 | — | ✓ | 第五批 S1103 | Diisopropyl Disulfide | 香料 | — | K4 |
| `e2_decenoic_acid` | (E)-2-癸烯酸 | `C10H18O2` | 液 | — | ✓ | 第五批 S1104 | (E)-2-Decenoic Acid | 香料 | — | K4 |
| `l_limonene` | l-苧烯 | `C10H16` | 液 | — | ✓ | 第五批 S1105 | l-Limonene | 香料 | — | K4 |
| `hexanethiol` | 正己硫醇 | `C6H14S` | 液 | — | ✓ | 第五批 S1106 | 1-Hexanethiol | 香料 | — | K4 |
| `decan_2_one` | 2-癸酮 | `C10H20O` | 液 | — | ✓ | 第五批 S1107 | 2-Decanone | 香料 | — | K4 |
| `difurfuryl_ether` | 二糠基醚 | `C10H10O3` | 液 | — | ✓ | 第五批 S1108 | Difurfuryl Ether | 香料 | — | K4 |
| `ethyl_vanillin_isobutyrate` | 异丁酸乙基香兰素酯 | `C13H16O4` | 液 | — | ✓ | 第五批 S1109 | Ethyl Vanillin Isobutyrate | 香料 | — | K4 |
| `ocimenyl_acetate` | 8-罗勒烯醇乙酸酯 | `C12H18O2` | 液 | — | ✓ | 第五批 S1110 | 8-Ocimenyl Acetate (2,6-Dimethyl-2,5,7-octatrien-1-yl Acetate) | 香料 | — | K4 |
| `butylamine` | 丁胺 | `C4H11N` | 液 | — | ✓ | 第五批 S1111 | Butylamine | 香料 | — | K4 |
| `isopropanolamine` | 1-氨基-2-丙醇 | `C3H9NO` | 液 | — | ✓ | 第五批 S1112 | 1-Amino-2-propanol | 香料 | — | K4 |
| `octa_1_5_dien_3_one` | 反式-1,5-辛二烯-3-酮 | `C8H12O` | 液 | — | ✓ | 第五批 S1113 | (E)-1,5-Octadien-3-one | 香料 | — | K4 |
| `ethoxy_dimethylfuranone` | 2,5-二甲基-4-乙氧基-3(2H)-呋喃酮 | `C8H12O3` | 液 | — | ✓ | 第五批 S1114 | 2,5-Dimethyl-4-ethoxy-3(2H)-furanone | 香料 | — | K4 |
| `tridecatrienal` | 反式-2-顺式-4-顺式-7-十三碳三烯醛 | `C13H20O` | 液 | — | ✓ | 第五批 S1115 | (2E,4Z,7Z)-Trideca-2,4,7-trienal | 香料 | — | K4 |
| `methyl_decadienoate` | 反式-2-顺式-4-癸二烯酸甲酯 | `C11H18O2` | 液 | — | ✓ | 第五批 S1116 | Methyl (2E,4Z)-2,4-Decadienoate | 香料 | — | K4 |
| `dimethyl_benzofuranone` | 2-(4-甲基-2-羟基苯基)-丙酸-γ-内酯 | `C10H10O2` | 固 | — | ✓ | 第五批 S1117 | 2-(4-Methyl-2-hydroxyphenyl)propionic Acid γ-Lactone (3,6-Dimethyl-2(3H)-benzofuranone) | 香料 | — | K3 |
| `z5_octenyl_propionate` | 丙酸顺式-5-辛烯酯 | `C11H20O2` | 液 | — | ✓ | 第五批 S1118 | (Z)-5-Octenyl Propionate | 香料 | — | K4 |
| `prenyl_thioacetate` | 3-甲基-2-丁烯硫醇乙酸酯 | `C7H12OS` | 液 | — | ✓ | 第五批 S1119 | Prenyl Thioacetate (3-Methyl-2-butenyl Thioacetate) | 香料 | — | K4 |
| `pyrroline` | 1-吡咯啉 | `C4H7N` | 液 | — | ✓ | 第五批 S1120 | 1-Pyrroline | 香料 | — | K4 |
| `trimethyl_3_pentanol` | 2,3,4-三甲基-3-戊醇 | `C8H18O` | 液 | — | ✓ | 第五批 S1121 | 2,3,4-Trimethyl-3-pentanol | 香料 | — | K4 |
| `diisopropyl_trisulfide` | 二异丙基三硫醚 | `C6H14S3` | 液 | — | ✓ | 第五批 S1122 | Diisopropyl Trisulfide | 香料 | — | K4 |
| `propionyl_pyrroline` | 2-丙酰基-1-吡咯啉 | `C7H11NO` | 液 | — | ✓ | 第五批 S1123 | 2-Propionyl-1-pyrroline | 香料 | — | K4 |
| `diethyl_tetrathiane_trithiolane` | 3,6-二乙基-1,2,4,5-四硫杂环己烷与3,5-二乙基-1,2,4-三硫杂环戊烷的混合物 | C6H12S4 与 C6H12S3 的混合物 | 液 | — |  | 第五批 S1124 | Mixture of 3,6-Diethyl-1,2,4,5-tetrathiane and 3,5-Diethyl-1,2,4-trithiolane | 香料 | — | — |
| `dihydroxy_dithiane` | 2,5-二羟基-1,4-二噻烷 | `C4H8O2S2` | 固 | — | ✓ | 第五批 S1125 | 2,5-Dihydroxy-1,4-dithiane (Mercaptoacetaldehyde Dimer) | 香料 | — | K3 |
| `hex_3_enal_mix` | 3-己烯醛(反式/顺式混合物) | `C6H10O` | 液 | — | ✓ | 第五批 S1126 | 3-Hexenal (trans/cis Mix) | 香料 | — | K4 |
| `syringaldehyde` | 4-羟基-3,5-二甲氧基苯甲醛 | `C9H10O4` | 固 | — | ✓ | 第五批 S1127 | 4-Hydroxy-3,5-dimethoxybenzaldehyde (Syringaldehyde) | 香料 | — | K3 |
| `undec_2_en_1_ol` | 2-十一烯-1-醇 | `C11H22O` | 液 | — | ✓ | 第五批 S1128 | 2-Undecen-1-ol | 香料 | — | K4 |
| `tyramine` | 2-(4-羟基苯基)-乙胺（酪胺） | `C8H11NO` | 固 | — | ✓ | 第五批 S1129 | 2-(4-Hydroxyphenyl)ethylamine (Tyramine) | 香料 | — | K3 |
| `furfurylthio_pentanone` | 4[(2-呋喃甲基)硫基]-2-戊酮 | `C10H14O2S` | 液 | — | ✓ | 第五批 S1130 | 4-[(2-Furanmethyl)thio]-2-pentanone (4-Furfurylthio-2-pentanone) | 香料 | — | K4 |
| `methylthiomethyl_hexanoate` | 己酸甲硫基甲酯 | `C8H16O2S` | 液 | — | ✓ | 第五批 S1131 | Methylthiomethyl Hexanoate | 香料 | — | K4 |
| `diisobutyl_ketone` | 2,6-二甲基-4-庚酮 | `C9H18O` | 液 | — | ✓ | 第五批 S1132 | 2,6-Dimethyl-4-heptanone (Diisobutyl Ketone) | 香料 | — | K4 |
| `trans_3_hexenol` | 反式-3-己烯醇 | `C6H12O` | 液 | — | ✓ | 第五批 S1134 | trans-3-Hexenol | 香料 | — | K4 |
| `terpinyl_formate` | 甲酸松油酯 | `C11H18O2` | 液 | — | ✓ | 第五批 S1135 | Terpinyl Formate | 香料 | — | K4 |
| `dehydronootkatone` | 脱氢圆柚酮 | `C15H20O` | 液 | — | ✓ | 第五批 S1136 | Dehydronootkatone | 香料 | — | K4 |
| `geranyl_hexanoate` | 己酸香叶酯 | `C16H28O2` | 液 | — | ✓ | 第五批 S1137 | Geranyl Hexanoate | 香料 | — | K4 |
| `methylhexanal` | 3-甲基己醛 | `C7H14O` | 液 | — | ✓ | 第五批 S1138 | 3-Methylhexanal | 香料 | — | K4 |
| `nonadiene` | (反式,反式)-2,4-壬二烯 | `C9H16` | 液 | — | ✓ | 第五批 S1139 | (E,E)-2,4-Nonadiene | 香料 | — | K4 |
| `o_methylacetophenone` | 2-甲基苯乙酮 | `C9H10O` | 液 | — | ✓ | 第五批 S1141 | 2-Methylacetophenone | 香料 | — | K4 |
| `ethyl_formylpyrrole` | 1-乙基-2-甲酰基吡咯（茶吡咯） | `C7H9NO` | 液 | — | ✓ | 第五批 S1142 | 1-Ethyl-2-formylpyrrole (Tea Pyrrole) | 香料 | — | K4 |
| `methylthiazolylethyl_octanoate` | 2-(4-甲基-5-噻唑基)乙醇辛酸酯 | `C14H23NO2S` | 液 | — | ✓ | 第五批 S1143 | 2-(4-Methyl-5-thiazolyl)ethyl Octanoate | 香料 | — | K4 |
| `ethyl_6_methylpyrazine` | 2-乙基-6-甲基吡嗪 | `C7H10N2` | 液 | — | ✓ | 第五批 S1144 | 2-Ethyl-6-methylpyrazine | 香料 | — | K4 |
| `p_propylphenol` | 对-丙基苯酚 | `C9H12O` | 液 | — | ✓ | 第五批 S1145 | p-Propylphenol | 香料 | — | K4 |
| `diethyl_2_methylpyrazine` | 3,5-二乙基-2-甲基吡嗪 | `C9H14N2` | 液 | — | ✓ | 第五批 S1146 | 3,5-Diethyl-2-methylpyrazine | 香料 | — | K4 |
| `verbenone` | 马鞭草烯酮 | `C10H14O` | 液 | — | ✓ | 第五批 S1147 | Verbenone | 香料 | — | K4 |
| `pent_4_enal` | 4-戊烯醛 | `C5H8O` | 液 | — | ✓ | 第五批 S1148 | 4-Pentenal | 香料 | — | K4 |
| `ethyl_acetoacetate_pg_ketal` | 乙酰乙酸乙酯丙二醇缩酮 | `C9H16O4` | 液 | — | ✓ | 第五批 S1149 | Ethyl Acetoacetate Propylene Glycol Ketal | 香料 | — | K4 |
| `methyl_sorbate` | 山梨酸甲酯 | `C7H10O2` | 液 | — | ✓ | 第五批 S1150 | Methyl Sorbate | 香料 | — | K4 |
| `diethyltetrahydrofuran` | 2,5-二乙基四氢呋喃 | `C8H16O` | 液 | — | ✓ | 第五批 S1151 | 2,5-Diethyltetrahydrofuran | 香料 | — | K4 |
| `dehydromenthofurolactone` | 脱氢薄荷呋喃内酯 | `C10H12O2` | 液 | — | ✓ | 第五批 S1152 | Dehydromenthofurolactone | 香料 | — | K4 |
| `myrtenyl_acetate` | 乙酸桃金娘烯酯 | `C12H18O2` | 液 | — | ✓ | 第五批 S1153 | Myrtenyl Acetate | 香料 | — | K4 |
| `methylthiazolylethyl_hexanoate` | 2-(4-甲基-5-噻唑基)乙醇己酸酯 | `C12H19NO2S` | 液 | — | ✓ | 第五批 S1154 | 2-(4-Methyl-5-thiazolyl)ethyl Hexanoate | 香料 | — | K4 |
| `methylthiazolylethyl_butyrate` | 2-(4-甲基-5-噻唑基)乙醇丁酸酯 | `C10H15NO2S` | 液 | — | ✓ | 第五批 S1155 | 2-(4-Methyl-5-thiazolyl)ethyl Butyrate | 香料 | — | K4 |
| `pyrrole` | 吡咯 | `C4H5N` | 液 | — | ✓ | 第五批 S1156 | Pyrrole | 香料 | — | K4 |
| `s_allyl_cysteine` | S-烯丙基-L-半胱氨酸 | `C6H11NO2S` | 固 | — | ✓ | 第五批 S1157 | S-Allyl-L-cysteine | 香料 | — | K3 |
| `mercaptobutanol` | 2-巯基-3-丁醇 | `C4H10OS` | 液 | — | ✓ | 第五批 S1158 | 2-Mercapto-3-butanol | 香料 | — | K4 |
| `thiogeraniol` | 硫代香叶醇 | `C10H18S` | 液 | — | ✓ | 第五批 S1159 | Thiogeraniol | 香料 | — | K4 |
| `pinanyl_mercaptan` | 蒎烷硫醇 | `C10H18S` | 液 | — | ✓ | 第五批 S1160 | Pinanyl Mercaptan | 香料 | — | K4 |
| `hydroxybutyl_mercaptobutyl_sulfide` | α-甲基-β-羟基丙基α-甲基-β-巯丙基硫醚 | `C8H18OS2` | 液 | — | ✓ | 第五批 S1161 | α-Methyl-β-hydroxypropyl α-Methyl-β-mercaptopropyl Sulfide | 香料 | — | K4 |
| `citral_diethyl_acetal` | 柠檬醛二乙缩醛 | `C14H26O2` | 液 | — | ✓ | 第五批 S1163 | Citral Diethyl Acetal | 香料 | — | K4 |
| `propenyl_guaethol` | 3-丙烯基-6-乙氧基苯酚 | `C11H14O2` | 固 | — | ✓ | 第五批 S1164 | 3-Propenyl-6-ethoxyphenol (Propenyl Guaethol) | 香料 | — | K3 |
| `methyl_beta_ionone` | β-甲基紫罗兰酮 | `C14H22O` | 液 | — | ✓ | 第五批 S1165 | Methyl-β-ionone | 香料 | — | K4 |
| `methyl_delta_ionone` | δ-甲基紫罗兰酮 | `C14H22O` | 液 | — | ✓ | 第五批 S1166 | Methyl-δ-ionone | 香料 | — | K4 |
| `nonadienal_diethyl_acetal` | 2,6-壬二烯醛二乙缩醛 | `C13H24O2` | 液 | — | ✓ | 第五批 S1167 | 2,6-Nonadienal Diethyl Acetal | 香料 | — | K4 |
| `undec_9_enal` | 9-十一烯醛 | `C11H20O` | 液 | — | ✓ | 第五批 S1168 | 9-Undecenal | 香料 | — | K4 |
| `undec_10_enal` | 10-十一烯醛 | `C11H20O` | 液 | — | ✓ | 第五批 S1169 | 10-Undecenal | 香料 | — | K4 |
| `ethyl_methylphenylglycidate` | 十六醛（杨梅醛） | `C12H14O3` | 液 | — | ✓ | 第五批 S1170 | Aldehyde C-16 (Ethyl Methylphenylglycidate, Strawberry Aldehyde) | 香料 | — | K4 |
| `cyclamen_aldehyde` | 兔耳草醛（仙客来醛） | `C13H18O` | 液 | — | ✓ | 第五批 S1172 | Cyclamen Aldehyde | 香料 | — | K4 |
| `hydroxycitronellal` | 羟基香茅醛 | `C10H20O2` | 液 | — | ✓ | 第五批 S1173 | Hydroxycitronellal | 香料 | — | K4 |
| `beta_homocyclocitral` | β-环高柠檬醛 | `C11H18O` | 液 | — | ✓ | 第五批 S1174 | β-Homocyclocitral | 香料 | — | K4 |
| `l_menthone_glyceryl_ketal` | l-薄荷酮甘油缩酮 | `C13H24O3` | 液 | — | ✓ | 第五批 S1175 | l-Menthone 1,2-Glyceryl Ketal | 香料 | — | K4 |
| `methylthio_methylpentanone` | 4-甲硫基-4-甲基-2-戊酮 | `C7H14OS` | 液 | — | ✓ | 第五批 S1176 | 4-(Methylthio)-4-methyl-2-pentanone | 香料 | — | K4 |
| `mercapto_2_pentanone` | 3-巯基-2-戊酮 | `C5H10OS` | 液 | — | ✓ | 第五批 S1177 | 3-Mercapto-2-pentanone | 香料 | — | K4 |
| `dl_menthone_glyceryl_ketal` | d,l-薄荷酮甘油缩酮 | `C13H24O3` | 液 | — | ✓ | 第五批 S1178 | d,l-Menthone 1,2-Glyceryl Ketal | 香料 | — | K4 |
| `methyl_alpha_ionone` | α-甲基紫罗兰酮 | `C14H22O` | 液 | — | ✓ | 第五批 S1179 | Methyl-α-ionone | 香料 | — | K4 |
| `alpha_isomethylionone` | α-异甲基紫罗兰酮 | `C14H22O` | 液 | — | ✓ | 第五批 S1180 | α-Isomethylionone | 香料 | — | K4 |
| `allyl_alpha_ionone` | 烯丙基α-紫罗兰酮 | `C16H24O` | 液 | — | ✓ | 第五批 S1181 | Allyl α-Ionone | 香料 | — | K4 |
| `methylcoumarin` | 6-甲基香豆素 | `C10H8O2` | 固 | — | ✓ | 第五批 S1182 | 6-Methylcoumarin | 香料 | — | K3 |
| `thiolactic_acid` | 2-巯基丙酸 | `C3H6O2S` | 液 | — | ✓ | 第五批 S1183 | 2-Mercaptopropionic Acid | 香料 | — | K4 |
| `methyl_4_pentenoic_acid` | 2-甲基-4-戊烯酸 | `C6H10O2` | 液 | — | ✓ | 第五批 S1184 | 2-Methyl-4-pentenoic Acid | 香料 | — | K4 |
| `dimethylbenzylcarbinyl_acetate` | 乙酸二甲基苄基原酯 | `C12H16O2` | 固 | — | ✓ | 第五批 S1185 | Benzyldimethylcarbinyl Acetate | 香料 | — | K3 |
| `allyl_cyclohexaneacetate` | 环己基乙酸烯丙酯 | `C11H18O2` | 液 | — | ✓ | 第五批 S1186 | Allyl Cyclohexaneacetate | 香料 | — | K4 |
| `rhodinyl_acetate` | 乙酸玫瑰酯 | `C12H22O2` | 液 | — | ✓ | 第五批 S1187 | Rhodinyl Acetate | 香料 | — | K4 |
| `ethyl_furylpropionate` | 3-(2-呋喃基)丙酸乙酯 | `C9H12O3` | 液 | — | ✓ | 第五批 S1188 | Ethyl 3-(2-Furyl)propanoate | 香料 | — | K4 |
| `allyl_propionate` | 丙酸烯丙酯 | `C6H10O2` | 液 | — | ✓ | 第五批 S1189 | Allyl Propionate | 香料 | — | K4 |
| `allyl_cyclohexylpropionate` | 3-环己基丙酸烯丙酯 | `C12H20O2` | 液 | — | ✓ | 第五批 S1190 | Allyl 3-Cyclohexylpropionate | 香料 | — | K4 |
| `isobutyl_furylpropionate` | 3-(2-呋喃基)丙酸异丁酯 | `C11H16O3` | 液 | — | ✓ | 第五批 S1191 | Isobutyl 3-(2-Furyl)propionate | 香料 | — | K4 |
| `furfuryl_thiopropionate` | 硫代丙酸糠酯 | `C8H10O2S` | 液 | — | ✓ | 第五批 S1192 | Furfuryl Thiopropionate | 香料 | — | K4 |
| `dimethylbenzylcarbinyl_butyrate` | 丁酸二甲基苄基原酯 | `C14H20O2` | 液 | — | ✓ | 第五批 S1193 | Dimethylbenzylcarbinyl Butyrate | 香料 | — | K4 |
| `allyl_cyclohexanebutyrate` | 环己基丁酸烯丙酯 | `C13H22O2` | 液 | — | ✓ | 第五批 S1194 | Allyl Cyclohexanebutyrate | 香料 | — | K4 |
| `nonanediol_acetate` | 1,3-壬二醇乙酸酯(混合酯) | 主成分 1,3-壬二醇二乙酸酯 C13H24O4（含单乙酸酯） | 液 | — |  | 第五批 S1195 | 1,3-Nonanediol Acetate (Mixed Esters) | 香料 | — | — |
| `styralyl_butyrate` | 丁酸苏合香酯 | `C12H16O2` | 液 | — | ✓ | 第五批 S1196 | Styralyl Butyrate | 香料 | — | K4 |
| `cedryl_acetate` | 乙酸柏木酯 | `C17H28O2` | 固 | — | ✓ | 第五批 S1197 | Cedryl Acetate | 香料 | — | K3 |
| `maltol_isobutyrate` | 异丁酸麦芽酚酯 | `C10H12O4` | 液 | — | ✓ | 第五批 S1198 | Maltol Isobutyrate | 香料 | — | K4 |
| `ethyl_methyl_4_pentenoate` | 2-甲基-4-戊烯酸乙酯 | `C8H14O2` | 液 | — | ✓ | 第五批 S1199 | Ethyl 2-Methyl-4-pentenoate | 香料 | — | K4 |
| `tetrahydrofurfuryl_acetate` | 乙酸四氢糠酯 | `C7H12O3` | 液 | — | ✓ | 第五批 S1200 | Tetrahydrofurfuryl Acetate | 香料 | — | K4 |
| `methyl_octynoate` | 庚炔羧酸甲酯 | `C9H14O2` | 液 | — | ✓ | 第五批 S1201 | Methyl Heptine Carbonate (Methyl 2-Octynoate) | 香料 | — | K4 |
| `methyl_nonynoate` | 辛炔羧酸甲酯 | `C10H16O2` | 液 | — | ✓ | 第五批 S1202 | Methyl Octine Carbonate (Methyl 2-Nonynoate) | 香料 | — | K4 |
| `diethyl_sebacate` | 癸二酸二乙酯 | `C14H26O4` | 液 | — | ✓ | 第五批 S1203 | Diethyl Sebacate | 香料 | — | K4 |
| `ethyl_undecenoate` | 10-十一烯酸乙酯 | `C13H24O2` | 液 | — | ✓ | 第五批 S1204 | Ethyl 10-Undecenoate | 香料 | — | K4 |
| `allyl_phenylacetate` | 苯乙酸烯丙酯 | `C11H12O2` | 液 | — | ✓ | 第五批 S1205 | Allyl Phenylacetate | 香料 | — | K4 |
| `geranyl_phenylacetate` | 苯乙酸香叶酯 | `C18H24O2` | 液 | — | ✓ | 第五批 S1207 | Geranyl Phenylacetate | 香料 | — | K4 |
| `p_cresyl_phenylacetate` | 苯乙酸对-甲酚酯 | `C15H14O2` | 固 | — | ✓ | 第五批 S1208 | p-Cresyl Phenylacetate | 香料 | — | K3 |
| `methyl_phenylbutyrate` | 4-苯基丁酸甲酯（苯丁酸甲酯） | `C11H14O2` | 液 | — | ✓ | 第五批 S1209 | Methyl 4-Phenylbutyrate | 香料 | — | K4 |
| `ethyl_phenylbutyrate` | 4-苯基丁酸乙酯（苯丁酸乙酯） | `C12H16O2` | 液 | — | ✓ | 第五批 S1210 | Ethyl 4-Phenylbutyrate | 香料 | — | K4 |
| `allyl_cinnamate` | 肉桂酸烯丙酯 | `C12H12O2` | 液 | — | ✓ | 第五批 S1211 | Allyl Cinnamate | 香料 | — | K4 |
| `ethyl_methyl_3_pentenoate` | 2-甲基-3-戊烯酸乙酯 | `C8H14O2` | 液 | — | ✓ | 第五批 S1212 | Ethyl 2-Methyl-3-pentenoate | 香料 | — | K4 |
| `ethyl_nitrite` | 亚硝酸乙酯 | `C2H5NO2` | 气 | — | ✓ | 第五批 S1213 | Ethyl Nitrite | 香料 | — | K9 |
| `amyl_heptanoate` | 庚酸戊酯 | `C12H24O2` | 液 | — | ✓ | 第五批 S1214 | Amyl Heptanoate | 香料 | — | K4 |
| `acetyl_dimethylfuran` | 3-乙酰基-2,5-二甲基呋喃 | `C8H10O2` | 液 | — | ✓ | 第五批 S1215 | 3-Acetyl-2,5-dimethylfuran | 香料 | — | K4 |
| `furaneol_butyrate` | 2,5-二甲基-3-氧代(2H)-4-呋喃丁酸酯 | `C10H14O4` | 液 | — | ✓ | 第五批 S1216 | 2,5-Dimethyl-3-oxo-(2H)-fur-4-yl Butyrate | 香料 | — | K4 |
| `methoxy_isopropylpyrazine` | 2-甲氧基-3(5或6)-异丙基吡嗪 | `C8H12N2O` | 液 | — | ✓ | 第五批 S1217 | 2-Methoxy-3(5 or 6)-Isopropylpyrazine | 香料 | — | K4 |
| `methyl_furfurylthio_pyrazine` | 2-甲基-3(5或6)-糠硫基吡嗪 | `C10H10N2OS` | 液 | — | ✓ | 第五批 S1218 | 2-Methyl-3(5 or 6)-(Furfurylthio)pyrazine (Mixture of Isomers) | 香料 | — | K4 |
| `alkyl_methoxypyrazine_mix` | 2-甲基(或乙基)-3(5或6)-甲氧基吡嗪 | 主成分 2-甲氧基-3-甲基吡嗪 C6H8N2O，含乙基同系物 C7H10N2O | 液 | — |  | 第五批 S1219 | 2-Methyl(or Ethyl)-3(5 or 6)-Methoxypyrazine | 香料 | — | — |
| `dimethyl_dihydroxy_dithiane` | 2,5-二甲基-2,5-二羟基-1,4-二硫代环己烷 | `C6H12O2S2` | 固 | — | ✓ | 第五批 S1220 | 2,5-Dimethyl-2,5-dihydroxy-1,4-dithiane | 香料 | — | K3 |
| `methyl_dihydrothienopyrimidine` | 5,7-二氢-2-甲基噻吩并(3,4-d)嘧啶 | `C7H8N2S` | 固 | — | ✓ | 第五批 S1221 | 5,7-Dihydro-2-methylthieno[3,4-d]pyrimidine | 香料 | — | K3 |
| `ethoxythiazole` | 2-乙氧基噻唑 | `C5H7NOS` | 液 | — | ✓ | 第五批 S1222 | 2-Ethoxythiazole | 香料 | — | K4 |
| `acetyl_dimethylthiazole` | 2,4-二甲基-5-乙酰基噻唑 | `C7H9NOS` | 液 | — | ✓ | 第五批 S1223 | 2,4-Dimethyl-5-acetylthiazole | 香料 | — | K4 |
| `isoeugenyl_acetate` | 乙酸异丁香酯 | `C12H14O3` | 固 | — | ✓ | 第五批 S1224 | Isoeugenyl Acetate | 香料 | — | K3 |
| `p_cresyl_isovalerate` | 3-甲基丁酸对-甲酚酯 | `C12H16O2` | 液 | — | ✓ | 第五批 S1225 | p-Cresyl Isovalerate (p-Methylphenyl 3-Methylbutyrate) | 香料 | — | K4 |
| `menthyl_ethylene_glycol_carbonate` | l-薄荷醇乙二醇碳酸酯 | `C13H24O4` | 液 | — | ✓ | 第五批 S1226 | l-Menthol Ethylene Glycol Carbonate | 香料 | — | K4 |
| `isobutylpyridine_3` | 3-(2-甲基丙基)吡啶 | `C9H13N` | 液 | — | ✓ | 第五批 S1227 | 3-(2-Methylpropyl)pyridine | 香料 | — | K4 |
| `ethyl_vanillin_pg_acetal` | 乙基香兰素1,2-丙二醇缩醛 | `C12H16O4` | 液 | — | ✓ | 第五批 S1228 | Ethyl Vanillin Propylene Glycol Acetal | 香料 | — | K4 |
| `artificial_cognac_oil` | 人造康乃克油 | 主成分 辛酸乙酯 C10H20O2、癸酸乙酯 C12H24O2、庚酸乙酯 C9H18O2 | 液 | — |  | 第五批 S1229 | Artificial Cognac Oil | 香料 | — | — |
| `hawthorn_kernel_smoke_flavoring_1` | 山楂核烟熏香味料Ⅰ号 | 酚类、羰基化合物、有机酸的混合物（常压 85–110 °C 馏分） | 液 | — |  | 第五批 S1230 | Smoke Flavoring No. I from Hawthorn Kernels | 香料 | — | — |
| `hawthorn_kernel_smoke_flavoring_2` | 山楂核烟熏香味料Ⅱ号 | 酚类、羰基化合物、有机酸的水溶性混合物 | 溶液 | — |  | 第五批 S1231 | Smoke Flavoring No. II from Hawthorn Kernels | 香料 | — | — |
| `isobutylbenzylcarbinol` | 苄基异丁基原醇 | `C12H18O` | 液 | — | ✓ | 第五批 S1232 | Isobutylbenzylcarbinol (α-Isobutylphenethyl Alcohol) | 香料 | — | K4 |
| `phenylbutenol` | 4-苯基-3-丁烯-2-醇 | `C10H12O` | 液 | — | ✓ | 第五批 S1233 | 4-Phenyl-3-buten-2-ol | 香料 | — | K4 |
| `methyl_phenyl_2_butanol` | 2-甲基-4-苯基-2-丁醇 | `C11H16O` | 液 | — | ✓ | 第五批 S1234 | 2-Methyl-4-phenyl-2-butanol | 香料 | — | K4 |
| `menthyl_propylene_glycol_carbonate` | l-薄荷醇丙二醇碳酸酯 | `C14H26O4` | 液 | — | ✓ | 第五批 S1235 | l-Menthol 1- (Or 2-) Propylene Glycol Carbonate | 香料 | — | K4 |
| `allyl_octanoate` | 辛酸烯丙酯 | `C11H20O2` | 液 | — | ✓ | 第五批 S1236 | Allyl Octanoate | 香料 | — | K4 |
| `propylphenethyl_alcohol` | α-丙基苯乙醇 | `C11H16O` | 液 | — | ✓ | 第五批 S1237 | α-Propylphenethyl Alcohol | 香料 | — | K4 |
| `hydratropyl_alcohol` | 龙葵醇 | `C9H12O` | 液 | — | ✓ | 第五批 S1238 | Hydratropyl Alcohol (β-Methylphenethyl Alcohol) | 香料 | — | K4 |
| `tetrahydrolinalool` | 四氢芳樟醇 | `C10H22O` | 液 | — | ✓ | 第五批 S1239 | Tetrahydrolinalool | 香料 | — | K4 |
| `butane_2_3_dithiol` | 2,3-二巯基丁烷 | `C4H10S2` | 液 | — | ✓ | 第五批 S1240 | 2,3-Dimercaptobutane (2,3-Butanedithiol) | 香料 | — | K4 |
| `naphthyl_ethyl_ether` | β-萘乙醚 | `C12H12O` | 固 | — | ✓ | 第五批 S1241 | β-Naphthyl Ethyl Ether | 香料 | — | K3 |
| `naphthyl_isobutyl_ether` | 异丁基β-萘醚 | `C14H16O` | 固 | — | ✓ | 第五批 S1242 | β-Naphthyl Isobutyl Ether | 香料 | — | K3 |
| `o_propylphenol` | 邻-丙基苯酚 | `C9H12O` | 液 | — | ✓ | 第五批 S1243 | o-Propylphenol | 香料 | — | K4 |
| `isoeugenyl_benzyl_ether` | 苄基异丁香酚 | `C17H18O2` | 固 | — | ✓ | 第五批 S1244 | Isoeugenyl Benzyl Ether (Benzyl Isoeugenol) | 香料 | — | K3 |
| `methyl_methylthio_pyrazine` | 2-甲基-3(5或6)-甲硫基吡嗪 | `C6H8N2S` | 液 | — | ✓ | 第五批 S1245 | 2-Methyl-3(5 or 6)-(Methylthio)pyrazine | 香料 | — | K4 |
| `citronellyloxyacetaldehyde` | 香茅氧基乙醛 | `C12H22O2` | 液 | — | ✓ | 第五批 S1246 | Citronellyloxyacetaldehyde | 香料 | — | K4 |
| `acetaldehyde_phenethyl_propyl_acetal` | 乙醛苯乙醇丙醇缩醛 | `C13H20O2` | 液 | — | ✓ | 第五批 S1247 | Acetaldehyde Phenethyl Propyl Acetal | 香料 | — | K4 |
| `satinaldehyde` | 2-甲基-3-(对甲基苯基)丙醛 | `C11H14O` | 液 | — | ✓ | 第五批 S1248 | 2-Methyl-3-(p-methylphenyl)propanal (Satinaldehyde) | 香料 | — | K4 |
| `phenyl_furyl_propenal` | 2-苯基-3-(2-呋喃基)丙-2-烯醛 | `C13H10O2` | 固 | — | ✓ | 第五批 S1249 | 2-Phenyl-3-(2-furyl)prop-2-enal | 香料 | — | K3 |
| `trimethylhexanal` | 3,5,5-三甲基己醛 | `C9H18O` | 液 | — | ✓ | 第五批 S1250 | 3,5,5-Trimethylhexanal | 香料 | — | K4 |
| `ethoxy_methylpyrazine` | 2-甲基-3(5或6)-乙氧基吡嗪 | `C7H10N2O` | 液 | — | ✓ | 第五批 S1251 | 2-Methyl-3(5 or 6)-Ethoxypyrazine | 香料 | — | K4 |
| `heptanal_glyceryl_acetal` | 庚醛甘油缩醛 | `C10H20O3` | 液 | — | ✓ | 第五批 S1252 | Heptanal Glyceryl Acetal | 香料 | — | K4 |
| `phenylacetaldehyde_glyceryl_acetal` | 苯乙醛甘油缩醛 | `C11H14O3` | 液 | — | ✓ | 第五批 S1253 | Phenylacetaldehyde Glyceryl Acetal | 香料 | — | K4 |
| `p_isopropylphenylacetaldehyde` | 对-异丙基苯乙醛 | `C11H14O` | 液 | — | ✓ | 第五批 S1254 | p-Isopropyl Phenylacetaldehyde | 香料 | — | K4 |
| `methyl_4_phenylbutanal` | 2-甲基-4-苯丁醛 | `C11H14O` | 液 | — | ✓ | 第五批 S1255 | 2-Methyl-4-phenylbutyraldehyde | 香料 | — | K4 |
| `hydratropic_aldehyde` | 龙葵醛 | `C9H10O` | 液 | — | ✓ | 第五批 S1256 | Hydratropic Aldehyde | 香料 | — | K4 |
| `hydratropic_aldehyde_dimethyl_acetal` | 龙葵醛二甲缩醛 | `C11H16O2` | 液 | — | ✓ | 第五批 S1257 | Hydratropic Aldehyde Dimethyl Acetal | 香料 | — | K4 |
| `hydroxycitronellal_diethyl_acetal` | 羟基香茅醛二乙缩醛 | `C14H30O3` | 液 | — | ✓ | 第五批 S1258 | Hydroxycitronellal Diethyl Acetal | 香料 | — | K4 |
| `citral_dimethyl_acetal` | 柠檬醛二甲缩醛 | `C12H22O2` | 液 | — | ✓ | 第五批 S1259 | Citral Dimethyl Acetal | 香料 | — | K4 |
| `methyl_acetoxyethyl_thiazole` | 4-甲基-5-(2-乙酰氧乙基)-噻唑 | `C8H11NO2S` | 液 | — | ✓ | 第五批 S1260 | 4-Methyl-5-(2-acetoxyethyl)thiazole | 香料 | — | K4 |
| `alpha_butylcinnamaldehyde` | α-丁基肉桂醛 | `C13H16O` | 液 | — | ✓ | 第五批 S1261 | α-Butylcinnamaldehyde | 香料 | — | K4 |
| `hept_4_en_3_one` | 4-庚烯-3-酮 | `C7H12O` | 液 | — | ✓ | 第五批 S1262 | 4-Hepten-3-one | 香料 | — | K4 |
| `benzyl_isobutyl_ketone` | 4-甲基-1-苯基-2-戊酮 | `C12H16O` | 液 | — | ✓ | 第五批 S1263 | 4-Methyl-1-phenyl-2-pentanone | 香料 | — | K4 |
| `p_methoxyphenyl_pentenone` | 1-(对-甲氧基苯基)-1-戊烯-3-酮 | `C12H14O2` | 固 | — | ✓ | 第五批 S1264 | 1-(p-Methoxyphenyl)-1-penten-3-one | 香料 | — | K3 |
| `alpha_hexylidenecyclopentanone` | α-己叉基环戊酮 | `C11H18O` | 液 | — | ✓ | 第五批 S1265 | α-Hexylidenecyclopentanone | 香料 | — | K4 |
| `tetramethyl_ethylcyclohexenone` | 四甲基乙基环己烯酮 | `C12H20O` | 液 | — | ✓ | 第五批 S1266 | Tetramethyl Ethylcyclohexenone | 香料 | — | K4 |
| `furfurylthiol_formate` | 糠硫醇甲酸酯 | `C6H6O2S` | 液 | — | ✓ | 第五批 S1267 | Furfurylthiol Formate | 香料 | — | K4 |
| `methyl_beta_naphthyl_ketone` | 甲基β-萘酮 | `C12H10O` | 固 | — | ✓ | 第五批 S1268 | Methyl β-Naphthyl Ketone | 香料 | — | K3 |
| `phenylpropyl_tetrahydrofuran` | 2-(3-苯丙基)四氢呋喃 | `C13H18O` | 液 | — | ✓ | 第五批 S1269 | 2-(3-Phenylpropyl)tetrahydrofuran | 香料 | — | K4 |
| `allylacetic_acid` | 烯丙基乙酸 | `C5H8O2` | 液 | — | ✓ | 第五批 S1270 | Allylacetic Acid | 香料 | — | K4 |
| `dimethylbenzylcarbinyl_formate` | 甲酸二甲基苄基原酯 | `C11H14O2` | 液 | — | ✓ | 第五批 S1271 | Dimethyl Benzyl Carbinyl Formate | 香料 | — | K4 |
| `acetyl_butyl_dimethylindane` | 4-乙酰基-6-叔丁基-1,1-二甲基茚满 | `C17H24O` | 固 | — | ✓ | 第五批 S1272 | 4-Acetyl-6-tert-butyl-1,1-dimethylindane | 香料 | — | K3 |
| `decanal_dimethyl_acetal` | 癸醛二甲缩醛 | `C12H26O2` | 液 | — | ✓ | 第五批 S1273 | Decanal Dimethyl Acetal (1,1-Dimethoxydecane) | 香料 | — | K4 |
| `cyclohexaneethyl_acetate` | 乙酸环己基乙酯 | `C10H18O2` | 液 | — | ✓ | 第五批 S1274 | Cyclohexaneethyl Acetate | 香料 | — | K4 |
| `ethyl_p_tolyloxyacetate` | 对-甲苯氧基乙酸乙酯 | `C11H14O3` | 液 | — | ✓ | 第五批 S1275 | Ethyl (p-Tolyloxy)acetate | 香料 | — | K4 |
| `dimethylphenethylcarbinyl_acetate` | 乙酸二甲基苯乙基原酯 | `C13H18O2` | 液 | — | ✓ | 第五批 S1276 | Dimethyl Phenethyl Carbinyl Acetate | 香料 | — | K4 |
| `methylphenylcarbinyl_propionate` | 丙酸甲基苯基原酯 | `C11H14O2` | 液 | — | ✓ | 第五批 S1277 | Methyl Phenylcarbinyl Propionate | 香料 | — | K4 |
| `propyl_furanacrylate` | 2-呋喃基丙烯酸丙酯 | `C10H12O3` | 液 | — | ✓ | 第五批 S1278 | Propyl 2-Furanacrylate | 香料 | — | K4 |
| `dimethylphenethylcarbinyl_isobutyrate` | 异丁酸二甲基苯乙基原酯 | `C15H22O2` | 液 | — | ✓ | 第五批 S1279 | Dimethyl Phenethyl Carbinyl Isobutyrate | 香料 | — | K4 |
| `phenoxyethyl_isobutyrate` | 异丁酸2-苯氧基乙酯 | `C12H16O3` | 液 | — | ✓ | 第五批 S1280 | 2-Phenoxyethyl Isobutyrate | 香料 | — | K4 |
| `ethylene_brassylate` | 十三碳二酸环乙二醇二酯 | `C15H26O4` | 液 | — | ✓ | 第五批 S1281 | Ethylene Brassylate | 香料 | — | K4 |
| `isobutyl_anthranilate` | 邻氨基苯甲酸异丁酯 | `C11H15NO2` | 液 | — | ✓ | 第五批 S1282 | Isobutyl Anthranilate | 香料 | — | K4 |
| `methyl_tert_butylphenylacetate` | 对-叔丁基苯乙酸甲酯 | `C13H18O2` | 液 | — | ✓ | 第五批 S1283 | Methyl p-tert-Butylphenylacetate | 香料 | — | K4 |
| `allyl_phenoxyacetate` | 苯氧乙酸烯丙酯 | `C11H12O3` | 液 | — | ✓ | 第五批 S1284 | Allyl Phenoxyacetate | 香料 | — | K4 |
| `octyl_phenylacetate` | 苯乙酸辛酯 | `C16H24O2` | 液 | — | ✓ | 第五批 S1285 | Octyl Phenylacetate | 香料 | — | K4 |
| `benzyl_phenylacetate` | 苯乙酸苄酯 | `C15H14O2` | 液 | — | ✓ | 第五批 S1286 | Benzyl Phenylacetate | 香料 | — | K4 |
| `linalyl_phenylacetate` | 苯乙酸芳樟酯 | `C18H24O2` | 液 | — | ✓ | 第五批 S1287 | Linalyl Phenylacetate | 香料 | — | K4 |
| `citronellyl_phenylacetate` | 苯乙酸香茅酯 | `C18H26O2` | 液 | — | ✓ | 第五批 S1288 | Citronellyl Phenylacetate | 香料 | — | K4 |
| `guaiacyl_phenylacetate` | 苯乙酸愈创木酚酯 | `C15H14O3` | 固 | — | ✓ | 第五批 S1289 | Guaiacyl Phenylacetate | 香料 | — | K3 |
| `phenethyl_senecioate` | 3-甲基2-丁烯酸2-苯乙酯（千里酸苯乙酯） | `C13H16O2` | 液 | — | ✓ | 第五批 S1290 | 2-Phenethyl 3-Methyl-2-butenoate (Phenethyl Senecioate) | 香料 | — | K4 |
| `ethyl_phenylglycidate` | 3-苯基缩水甘油酸乙酯 | `C11H12O3` | 液 | — | ✓ | 第五批 S1291 | Ethyl 3-Phenylglycidate | 香料 | — | K4 |
| `linalyl_cinnamate` | 肉桂酸芳樟酯 | `C19H24O2` | 液 | — | ✓ | 第五批 S1292 | Linalyl Cinnamate | 香料 | — | K4 |
| `diethoxyethoxy_propane` | 1,2-二[(1'-乙氧基)-乙氧基]丙烷 | `C11H24O4` | 液 | — | ✓ | 第五批 S1293 | 1,2-Di[(1'-ethoxy)ethoxy]propane | 香料 | — | K4 |
| `isopropyl_trimethylbutyramide` | N,2,3-三甲基-2-异丙基丁酰胺 | `C10H21NO` | 固 | — | ✓ | 第五批 S1294 | 2-Isopropyl-N,2,3-trimethylbutyramide | 香料 | — | K3 |
| `ethyl_menthanecarboxamide` | N-乙基-2-异丙基-5-甲基-环己烷甲酰胺 | `C13H25NO` | 固 | — | ✓ | 第五批 S1295 | N-Ethyl-2-isopropyl-5-methylcyclohexanecarboxamide | 香料 | — | K3 |
| `menthoxypropanediol` | 3-l-䓝氧基-1,2-丙二醇 | `C13H26O3` | 液 | — | ✓ | 第五批 S1296 | 3-l-Menthoxypropane-1,2-diol | 香料 | — | K4 |
| `vanillyl_butyl_ether` | 香兰基丁醚 | `C12H18O3` | 液 | — | ✓ | 第五批 S1297 | Vanillyl Butyl Ether | 香料 | — | K4 |
| `dec_9_enal` | 9-癸烯醛 | `C10H18O` | 液 | — | ✓ | 第五批 S1298 | 9-Decenal | 香料 | — | K4 |
| `sec_butylcyclohexanone` | 2-仲丁基环己酮 | `C10H18O` | 液 | — | ✓ | 第五批 S1299 | 2-sec-Butylcyclohexanone | 香料 | — | K4 |
| `undecane_2_3_dione` | 2,3-十一碳二酮 | `C11H20O2` | 液 | — | ✓ | 第五批 S1300 | 2,3-Undecanedione | 香料 | — | K4 |
| `cyclohexanecarboxylic_acid` | 环己烷基甲酸 | `C7H12O2` | 固 | — | ✓ | 第五批 S1301 | Cyclohexanecarboxylic Acid | 香料 | — | K3 |
| `milk_lactone` | 5和6-癸烯酸（牛奶内酯） | `C10H18O2` | 液 | — | ✓ | 第五批 S1302 | 5- and 6-Decenoic Acid (Milk Lactone) | 香料 | — | K4 |
| `sucrose_octaacetate` | 八乙酸蔗糖酯 | `C28H38O19` | 固 | — | ✓ | 第五批 S1303 | Sucrose Octaacetate | 香料 | — | K3 |
| `allyl_butyrate` | 丁酸烯丙酯 | `C7H12O2` | 液 | — | ✓ | 第五批 S1304 | Allyl Butyrate | 香料 | — | K4 |
| `vanillin_isobutyrate` | 异丁酸香兰素酯 | `C12H14O4` | 液 | — | ✓ | 第五批 S1305 | Vanillin Isobutyrate | 香料 | — | K4 |
| `monomenthyl_glutarate` | 戊二酸单l-薄荷醇酯 | `C15H26O4` | 液 | — | ✓ | 第五批 S1306 | l-Monomenthyl Glutarate | 香料 | — | K4 |
| `ethyl_benzoylacetate` | 苯甲酰基乙酸乙酯 | `C11H12O3` | 液 | — | ✓ | 第五批 S1307 | Ethyl Benzoylacetate | 香料 | — | K4 |
| `epsilon_dodecalactone` | ε-十二内酯 | `C12H22O2` | 液 | — | ✓ | 第五批 S1308 | ε-Dodecalactone | 香料 | — | K4 |
| `octahydrocoumarin` | 八氢香豆素 | `C9H14O2` | 液 | — | ✓ | 第五批 S1309 | Octahydrocoumarin | 香料 | — | K4 |
| `dimethyl_3_furanthiol` | 2,5-二甲基-3-呋喃硫醇 | `C6H8OS` | 液 | — | ✓ | 第五批 S1310 | 2,5-Dimethyl-3-furanthiol | 香料 | — | K4 |
| `butane_1_2_dithiol` | 1,2-丁二硫醇 | `C4H10S2` | 液 | — | ✓ | 第五批 S1311 | 1,2-Butanedithiol | 香料 | — | K4 |
| `bis_dimethylfuryl_disulfide` | 双-(2,5-二甲基-3-呋喃基)二硫醚 | `C12H14O2S2` | 液 | — | ✓ | 第五批 S1312 | Bis(2,5-dimethyl-3-furyl) Disulfide | 香料 | — | K4 |
| `propyl_methylfuryl_disulfide` | 丙基2-甲基-3-呋喃基二硫醚 | `C8H12OS2` | 液 | — | ✓ | 第五批 S1313 | Propyl 2-Methyl-3-furyl Disulfide | 香料 | — | K4 |
| `dicyclohexyl_disulfide` | 二环己基二硫醚 | `C12H22S2` | 液 | — | ✓ | 第五批 S1314 | Dicyclohexyl Disulfide | 香料 | — | K4 |
| `furfuryl_isopropyl_sulfide` | 糠基异丙基硫醚 | `C8H12OS` | 液 | — | ✓ | 第五批 S1315 | Furfuryl Isopropyl Sulfide | 香料 | — | K4 |
| `o_ethylthiophenol` | 2-乙基苯硫酚 | `C8H10S` | 液 | — | ✓ | 第五批 S1316 | 2-Ethylthiophenol | 香料 | — | K4 |
| `methylthio_acetoxypropionate` | 2-(乙酰氧基)丙酸甲硫醇酯 | `C6H10O3S` | 液 | — | ✓ | 第五批 S1317 | Methylthio 2-(Acetyloxy)propionate | 香料 | — | K4 |
| `methylthio_propionyloxypropionate` | 2-(丙酰氧基)丙酸甲硫醇酯 | `C7H12O3S` | 液 | — | ✓ | 第五批 S1318 | Methylthio 2-(Propionyloxy)propionate | 香料 | — | K4 |
| `ethyl_furfurylthiopropionate` | 3-糠硫基丙酸乙酯 | `C10H14O3S` | 液 | — | ✓ | 第五批 S1319 | Ethyl 3-(Furfurylthio)propionate | 香料 | — | K4 |
| `methylthiopyrazine` | 2-甲硫基吡嗪 | `C5H6N2S` | 液 | — | ✓ | 第五批 S1320 | 2-Methylthiopyrazine | 香料 | — | K4 |
| `phenethyl_isothiocyanate` | 异硫氰酸苯乙酯 | `C9H9NS` | 液 | — | ✓ | 第五批 S1321 | Phenethyl Isothiocyanate | 香料 | — | K4 |
| `phenylpropyl_pyridine` | 2-(3-苯丙基)吡啶 | `C14H15N` | 液 | — | ✓ | 第五批 S1322 | 2-(3-Phenylpropyl)pyridine | 香料 | — | K4 |
| `ethyl_dimethyl_thiazoline` | 4,5-二甲基-2-乙基-3-噻唑啉 | `C7H13NS` | 液 | — | ✓ | 第五批 S1323 | 4,5-Dimethyl-2-ethyl-3-thiazoline | 香料 | — | K4 |
| `sec_butyl_dimethyl_thiazoline` | 2-仲丁基-4,5-二甲基-3-噻唑啉 | `C9H17NS` | 液 | — | ✓ | 第五批 S1324 | 2-(2-Butyl)-4,5-dimethyl-3-thiazoline | 香料 | — | K4 |
| `pyrazine_ethanethiol` | 吡嗪乙硫醇 | `C6H8N2S` | 液 | — | ✓ | 第五批 S1325 | Pyrazine Ethanethiol | 香料 | — | K4 |
| `phenyl_salicylate` | 水杨酸苯酯 | `C13H10O3` | 固 | — | ✓ | 第五批 S1326 | Phenyl Salicylate | 香料 | — | K3 |
| `heptanal_dimethyl_acetal` | 庚醛二甲缩醛 | `C9H20O2` | 液 | — | ✓ | 第五批 S1327 | Heptanal Dimethyl Acetal | 香料 | — | K4 |
| `hydroxycitronellal_dimethyl_acetal` | 羟基香茅醛二甲缩醛 | `C12H26O3` | 液 | — | ✓ | 第五批 S1328 | Hydroxycitronellal Dimethyl Acetal | 香料 | — | K4 |
| `p_propylanisole` | 对-丙基茴香醚 | `C10H14O` | 液 | — | ✓ | 第五批 S1329 | p-Propyl Anisole | 香料 | — | K4 |
| `p_tolyl_isobutyrate` | 异丁酸对-甲酚酯 | `C11H14O2` | 液 | — | ✓ | 第五批 S1330 | p-Tolyl Isobutyrate | 香料 | — | K4 |
| `o_tolyl_isobutyrate` | 异丁酸邻-甲酚酯 | `C11H14O2` | 液 | — | ✓ | 第五批 S1331 | o-Tolyl Isobutyrate | 香料 | — | K4 |
| `citral_propylene_glycol_acetal` | 柠檬醛丙二醇缩醛 | `C13H22O2` | 液 | — | ✓ | 第五批 S1332 | Citral Propylene Glycol Acetal | 香料 | — | K4 |
| `trans_2_hexenal_diethyl_acetal` | 反式-2-己烯醛二乙缩醛 | `C10H20O2` | 液 | — | ✓ | 第五批 S1333 | trans-2-Hexenal Diethyl Acetal | 香料 | — | K4 |
| `mercaptothiophene` | 2-巯基噻吩 | `C4H4S2` | 液 | — | ✓ | 第五批 S1334 | 2-Mercaptothiophene | 香料 | — | K4 |
| `p_menthane_3_8_diol` | 对-䓝-3,8-二醇 | `C10H20O2` | 固 | — | ✓ | 第五批 S1335 | p-Menthane-3,8-diol | 香料 | — | K3 |
| `octane_1_8_dithiol` | 1,8-辛二硫醇 | `C8H18S2` | 液 | — | ✓ | 第五批 S1336 | 1,8-Octanedithiol | 香料 | — | K4 |
| `spiro_dithia_oxabicyclooctane` | 螺[2,4-二硫杂-1-甲基-8-氧杂双环[3.3.0]-辛烷-3,3’-(1’-氧杂-2’-甲基)环戊烷] | `C10H16O2S2` | 液 | — | ✓ | 第五批 S1337 | Spiro[2,4-dithia-1-methyl-8-oxabicyclo[3.3.0]octane-3,3'-(1'-oxa-2'-methyl)cyclopentane] | 香料 | — | K4 |
| `non_3_en_2_one` | 3-壬烯-2-酮 | `C9H16O` | 液 | — | ✓ | 第五批 S1338 | 3-Nonen-2-one | 香料 | — | K4 |
| `methyl_2_4_nonanedione` | 3-甲基-2,4-壬二酮 | `C10H18O2` | 液 | — | ✓ | 第五批 S1339 | 3-Methyl-2,4-nonanedione | 香料 | — | K4 |
| `dimethyl_thioacetoxyfuran` | 2,5-二甲基-3-硫代乙酰氧基呋喃 | `C8H10O2S` | 液 | — | ✓ | 第五批 S1340 | 2,5-Dimethyl-3-thioacetoxyfuran | 香料 | — | K4 |
| `trans_4_hexenal` | 反式-4-己烯醛 | `C6H10O` | 液 | — | ✓ | 第五批 S1341 | trans-4-Hexenal | 香料 | — | K4 |
| `methylfurylthio_butanone` | 3-[(2-甲基-3-呋喃)硫基]-2-丁酮 | `C9H12O2S` | 液 | — | ✓ | 第五批 S1342 | (±)-3-[(2-Methyl-3-furyl)thio]-2-butanone | 香料 | — | K4 |
| `mercapto_methylpentanal` | 3-巯基-2-甲基戊醛 | `C6H12OS` | 液 | — | ✓ | 第五批 S1343 | 3-Mercapto-2-methylpentanal | 香料 | — | K4 |
| `menthoxyethanol` | 2-(l-䓝氧基)乙醇 | `C12H24O2` | 液 | — | ✓ | 第五批 S1344 | 2-(l-Menthoxy)ethanol | 香料 | — | K4 |
| `tetrahydrofurfuryl_propionate` | 丙酸四氢糠酯 | `C8H14O3` | 液 | — | ✓ | 第五批 S1345 | Tetrahydrofurfuryl Propionate | 香料 | — | K4 |
| `allyl_isovalerate` | 异戊酸烯丙酯 | `C8H14O2` | 液 | — | ✓ | 第五批 S1346 | Allyl Isovalerate | 香料 | — | K4 |
| `hydroxymethyl_heptanone` | 3-羟甲基-2-庚酮 | `C8H16O2` | 液 | — | ✓ | 第五批 S1347 | 3-(Hydroxymethyl)-2-heptanone | 香料 | — | K4 |
| `glyceryl_tripropanoate` | 三丙酸甘油酯 | `C12H20O6` | 液 | — | ✓ | 第五批 S1348 | Glyceryl Tripropanoate | 香料 | — | K7 |
| `furfuryl_octanoate` | 辛酸糠酯 | `C13H20O3` | 液 | — | ✓ | 第五批 S1349 | Furfuryl Octanoate | 香料 | — | K4 |
| `trans_2_octenyl_butanoate` | 丁酸反式-2-辛烯醇酯 | `C12H22O2` | 液 | — | ✓ | 第五批 S1350 | trans-2-Octen-1-yl Butanoate | 香料 | — | K4 |
| `phenylacetaldehyde_diisobutyl_acetal` | 苯乙醛二异丁缩醛 | `C16H26O2` | 液 | — | ✓ | 第五批 S1351 | Phenylacetaldehyde Diisobutyl Acetal | 香料 | — | K4 |
| `dibenzyl_ketone` | 1,3-二苯基-2-丙酮 | `C15H14O` | 固 | — | ✓ | 第五批 S1352 | 1,3-Diphenyl-2-propanone | 香料 | — | K3 |
| `butyl_undecylenate` | 10-十一烯酸丁酯 | `C15H28O2` | 液 | — | ✓ | 第五批 S1353 | Butyl 10-Undecylenate | 香料 | — | K4 |
| `santalyl_acetate` | 乙酸檀香酯 | `C17H26O2` | 液 | — | ✓ | 第五批 S1354 | Santalyl Acetate | 香料 | — | K4 |
| `geranyl_ethylbutyrate` | 2-乙基丁酸香叶酯 | `C16H28O2` | 液 | — | ✓ | 第五批 S1355 | Geranyl 2-Ethylbutyrate | 香料 | — | K4 |
| `hydroxymethyl_octanone` | 3-羟甲基-2-辛酮 | `C9H18O2` | 液 | — | ✓ | 第五批 S1356 | 3-Hydroxymethyl-2-octanone | 香料 | — | K4 |
| `cyclohexane_1_2_dione` | 1,2-环己二酮 | `C6H8O2` | 固 | — | ✓ | 第五批 S1357 | 1,2-Cyclohexanedione | 香料 | — | K3 |
| `mercapto_2_methylbutanol` | 赤、苏-3-巯基-2-甲基丁-1-醇 | `C5H12OS` | 液 | — | ✓ | 第五批 S1359 | Erythro- and Threo-3-Mercapto-2-methylbutan-1-ol (3-Mercapto-2-methylbutyl Alcohol) | 香料 | — | K4 |
| `p_methylbiphenyl` | 4-甲基联苯 | `C13H12` | 固 | — | ✓ | 第五批 S1360 | 4-Methyl Biphenyl | 香料 | — | K3 |
| `alpha_amylcinnamyl_alcohol` | α-戊基肉桂醇 | `C14H20O` | 液 | — | ✓ | 第五批 S1361 | α-Amylcinnamyl Alcohol | 香料 | — | K4 |
| `phenyl_3_methyl_3_pentanol` | 1-苯基-3-甲基-戊醇-3 | `C12H18O` | 液 | — | ✓ | 第五批 S1362 | 1-Phenyl-3-methyl-3-pentanol | 香料 | — | K4 |
| `phenylpentanol` | 5-苯基戊醇 | `C11H16O` | 液 | — | ✓ | 第五批 S1363 | 5-Phenylpentanol | 香料 | — | K4 |
| `p_menthan_2_ol` | 对-䓝烷醇-2 | `C10H20O` | 液 | — | ✓ | 第五批 S1364 | p-Menthan-2-ol | 香料 | — | K4 |
| `dehydrodihydroionol` | 脱氢二氢紫罗兰醇 | `C13H22O` | 液 | — | ✓ | 第五批 S1365 | Dehydrodihydroionol | 香料 | — | K4 |
| `ethylfenchol` | 乙基葑醇 | `C12H22O` | 液 | — | ✓ | 第五批 S1366 | Ethyl Fenchol | 香料 | — | K4 |
| `gum_arabic_octenylsuccinate` | 辛烯基琥珀酸单阿拉伯胶酯 | 阿拉伯胶部分羟基的辛烯基琥珀酸半酯，酯化单元 C12H18O3 | 固 | — |  | 第五批 S1367 | Gum Arabic, Hydrogen Octenylbutanedioate | 香料 | — | K6 |
| `methoxymethylbenzyl_oxalamide` | N1-(2-甲氧基-4-甲基苄基)-N2-[2-(5-甲基-2-吡啶基)乙基]草酰胺 | `C19H23N3O3` | 固 | — | ✓ | 第五批 S1368 | N1-(2-Methoxy-4-methylbenzyl)-N2-[2-(5-methylpyridin-2-yl)ethyl]oxalamide | 香料 | — | K3 |
| `dimethoxybenzyl_oxalamide` | N1-(2,4-二甲氧基苄基)-N2-[2-(2-吡啶基)乙基]草酰胺 | `C18H21N3O4` | 固 | — | ✓ | 第五批 S1369 | N1-(2,4-Dimethoxybenzyl)-N2-[2-(pyridin-2-yl)ethyl]oxalamide | 香料 | — | K3 |
| `heptyl_piperonylamide` | N-(4-庚基)-(3,4-亚甲二氧基)苯甲酰胺 | `C15H21NO3` | 固 | — | ✓ | 第五批 S1370 | N-(Heptan-4-yl)benzo[d][1,3]dioxole-5-carboxamide | 香料 | — | K3 |
| `dibenzyl_ether` | 二苄醚 | `C14H14O` | 液 | — | ✓ | 第五批 S1371 | Dibenzyl Ether | 香料 | — | K4 |
| `glyceryl_hydroxydodecanoate` | 5-羟基-十二酸甘油酯 | `C15H30O5` | 液 | — | ✓ | 第五批 S1372 | Glyceryl 5-Hydroxydodecanoate | 香料 | — | K7 |
| `tributyrin` | 三丁酸甘油酯 | `C15H26O6` | 液 | — | ✓ | 第五批 S1373 | Tributyrin | 香料 | — | K7 |
| `allyl_nonanoate` | 壬酸烯丙酯 | `C12H22O2` | 液 | — | ✓ | 第五批 S1374 | Allyl Nonanoate | 香料 | — | K4 |
| `glyceryl_hydroxydecanoate` | 5-羟基癸酸甘油酯 | `C13H26O5` | 液 | — | ✓ | 第五批 S1375 | Glyceryl 5-Hydroxydecanoate | 香料 | — | K7 |
| `phenylpropyl_propionate` | 丙酸3-苯基丙酯 | `C12H16O2` | 液 | — | ✓ | 第五批 S1376 | 3-Phenylpropyl Propionate | 香料 | — | K4 |
| `isopropyl_cinnamate` | 肉桂酸异丙酯 | `C12H14O2` | 液 | — | ✓ | 第五批 S1377 | Isopropyl Cinnamate | 香料 | — | K4 |
| `keto_butanethiol` | 2-酮基-4-丁硫醇 | `C4H8OS` | 液 | — | ✓ | 第五批 S1378 | 2-Keto-4-butanethiol | 香料 | — | K4 |
| `ethyl_methyl_tolylglycidate` | 甲基-对-甲苯缩水甘油酸乙酯 | `C13H16O3` | 液 | — | ✓ | 第五批 S1379 | Ethyl Methyl-p-tolylglycidate | 香料 | — | K4 |
| `delta_undec_8_enolactone` | 5-羟基-8-十一碳烯酸δ-内酯 | `C11H18O2` | 液 | — | ✓ | 第五批 S1380 | 5-Hydroxy-8-undecenoic Acid δ-Lactone | 香料 | — | K4 |
| `cyclopropyl_nonadienamide` | N-环丙基-反式-2-顺式-6-壬二烯酰胺 | `C12H19NO` | 固 | — | ✓ | 第五批 S1381 | N-Cyclopropyl-(2E,6Z)-nonadienamide | 香料 | — | K3 |
| `ethyl_nonadienamide` | N-乙基-反式-2-顺式-6-壬二烯酰胺 | `C11H19NO` | 液 | — | ✓ | 第五批 S1382 | N-Ethyl-(2E,6Z)-nonadienamide | 香料 | — | K4 |
| `dimethyl_1_3_dioxolane` | 2,4-二甲基-1,3-二氧戊环 | `C5H10O2` | 液 | — | ✓ | 第五批 S1383 | 2,4-Dimethyl-1,3-dioxolane (Acetaldehyde Propylene Glycol Acetal) | 香料 | — | K4 |
| `beta_naphthyl_methyl_ether` | β-萘甲醚 | `C11H10O` | 固 | — | ✓ | 第五批 S1384 | β-Naphthyl Methyl Ether | 香料 | — | K3 |
| `dihydroxyacetone` | 二羟基丙酮 | `C3H6O3` | 固 | — | ✓ | 第五批 S1385 | Dihydroxyacetone | 香料 | — | K2 |
| `diphenyl_disulfide` | 二苯基二硫醚 | `C12H10S2` | 固 | — | ✓ | 第五批 S1386 | Phenyl Disulfide | 香料 | — | K3 |
| `ethyl_carvacrol` | 乙基香芹酚 | `C12H18O` | 液 | — | ✓ | 第五批 S1387 | Ethyl Carvacrol | 香料 | — | K4 |
| `tolualdehyde_glyceryl_acetal` | 甲基苯甲醛甘油缩醛(邻、间、对异构体混合物) | `C11H14O3` | 液 | — | ✓ | 第五批 S1388 | Tolualdehyde Glyceryl Acetal (o-, m-, p-Mixed Isomers) | 香料 | — | K4 |
| `dimethyl_nonadienol` | (+/-)-反式和顺式-4,8-二甲基-3,7-壬二烯-2-醇 | `C11H20O` | 液 | — | ✓ | 第五批 S1389 | (±)-trans- and cis-4,8-Dimethyl-3,7-nonadien-2-ol | 香料 | — | K4 |
| `dimethyl_nonadienyl_acetate` | (+/-)-反式和顺式-4,8-二甲基-3,7-壬二烯-2-醇乙酸酯 | `C13H22O2` | 液 | — | ✓ | 第五批 S1390 | (±)-trans- and cis-4,8-Dimethyl-3,7-nonadien-2-yl Acetate | 香料 | — | K4 |
| `methoxy_1_decene` | (反式和顺式)-1-甲氧基-1-癸烯 | `C11H22O` | 液 | — | ✓ | 第五批 S1391 | trans- and cis-1-Methoxy-1-decene | 香料 | — | K4 |
| `methylthiazolylethyl_decanoate` | 2-(4-甲基-5-噻唑基)乙醇癸酸酯 | `C16H27NO2S` | 液 | — | ✓ | 第五批 S1392 | 2-(4-Methyl-5-thiazolyl)ethyl Decanoate | 香料 | — | K4 |
| `methylthiazolylethyl_isobutyrate` | 2-(4-甲基-5-噻唑基)乙醇异丁酸酯 | `C10H15NO2S` | 液 | — | ✓ | 第五批 S1393 | 2-(4-Methyl-5-thiazolyl)ethyl Isobutyrate | 香料 | — | K4 |
| `methylthiazolylethyl_formate` | 2-(4-甲基-5-噻唑基)乙醇甲酸酯 | `C7H9NO2S` | 液 | — | ✓ | 第五批 S1394 | 2-(4-Methyl-5-thiazolyl)ethyl Formate | 香料 | — | K4 |
| `phenylpropyl_isovalerate` | 异戊酸3-苯丙酯 | `C14H20O2` | 液 | — | ✓ | 第五批 S1395 | 3-Phenylpropyl Isovalerate | 香料 | — | K4 |
| `dl_menthyl_propylene_glycol_carbonate` | dl-薄荷脑(+/-)-1,2-丙二醇碳酸酯 | `C14H26O4` | 液 | — | ✓ | 第五批 S1396 | dl-Menthol (±)-Propylene Glycol Carbonate | 香料 | — | K4 |
| `ethoxyethyl_acetate` | 乙酸1-乙氧基乙醇酯 | `C6H12O3` | 液 | — | ✓ | 第五批 S1397 | 1-Ethoxyethyl Acetate | 香料 | — | K4 |
| `isobutyl_decadienamide` | N-异丁基-反-2-反-4-癸二烯酸酰胺 | `C14H25NO` | 固 | — | ✓ | 第五批 S1398 | N-Isobutyl-(2E,4E)-decadienamide | 香料 | — | K3 |
| `benzoin` | 二苯乙醇酮 | `C14H12O2` | 固 | — | ✓ | 第五批 S1399 | Benzoin (2-Hydroxy-2-phenylacetophenone) | 香料 | — | K3 |
| `methyl_isopentyl_disulfide` | 甲基异戊基二硫醚 | `C6H14S2` | 液 | — | ✓ | 第五批 S1400 | Methyl Isopentyl Disulfide | 香料 | — | K4 |
| `allyl_anthranilate` | 邻氨基苯甲酸烯丙酯 | `C10H11NO2` | 液 | — | ✓ | 第五批 S1401 | Allyl Anthranilate | 香料 | — | K4 |
| `allyl_cyclohexanehexanoate` | 6-环己基己酸烯丙酯 | `C15H26O2` | 液 | — | ✓ | 第五批 S1402 | Allyl Cyclohexanehexanoate | 香料 | — | K4 |
| `allyl_cyclohexanevalerate` | 5-环己基戊酸烯丙酯 | `C14H24O2` | 液 | — | ✓ | 第五批 S1403 | Allyl Cyclohexanevalerate | 香料 | — | K4 |
| `allyl_2_ethylbutyrate` | 2-乙基丁酸烯丙酯 | `C9H16O2` | 液 | — | ✓ | 第五批 S1404 | Allyl 2-Ethylbutyrate | 香料 | — | K4 |
| `allyl_tiglate` | 惕各酸烯丙酯 | `C8H12O2` | 液 | — | ✓ | 第五批 S1405 | Allyl Tiglate (Allyl trans-2-Methyl-2-butenoate) | 香料 | — | K4 |
| `allyl_10_undecenoate` | 10-十一烯酸烯丙酯 | `C14H24O2` | 液 | — | ✓ | 第五批 S1406 | Allyl 10-Undecenoate | 香料 | — | K4 |
| `amylcinnamaldehyde_dimethyl_acetal` | α-戊基肉桂醛二甲缩醛 | `C16H24O2` | 液 | — | ✓ | 第五批 S1407 | α-Amylcinnamaldehyde Dimethyl Acetal | 香料 | — | K4 |
| `amylcinnamyl_acetate` | 乙酸α-戊基肉桂酯 | `C16H22O2` | 液 | — | ✓ | 第五批 S1408 | α-Amylcinnamyl Acetate | 香料 | — | K4 |
| `amylcinnamyl_formate` | 甲酸α-戊基肉桂酯 | `C15H20O2` | 液 | — | ✓ | 第五批 S1409 | α-Amylcinnamyl Formate | 香料 | — | K4 |
| `amylcinnamyl_isovalerate` | 异戊酸α-戊基肉桂酯 | `C19H28O2` | 液 | — | ✓ | 第五批 S1410 | α-Amylcinnamyl Isovalerate | 香料 | — | K4 |
| `isoamyl_furylbutyrate` | 4(2-呋喃基)丁酸异戊酯 | `C13H20O3` | 液 | — | ✓ | 第五批 S1411 | Isoamyl 4-(2-Furan)butyrate | 香料 | — | K4 |
| `isoamyl_furylpropionate` | 3(2-呋喃基)丙酸异戊酯 | `C12H18O3` | 液 | — | ✓ | 第五批 S1412 | Isoamyl 3-(2-Furan)propionate | 香料 | — | K4 |
| `amyl_keto_dioxane` | 2-戊基-5或6-酮-1,4-二噁烷 | `C9H16O3` | 液 | — | ✓ | 第五批 S1413 | 2-Amyl-5 or 6-Keto-1,4-dioxane | 香料 | — | K4 |
| `isoamyl_pyruvate` | 丙酮酸异戊酯 | `C8H14O3` | 液 | — | ✓ | 第五批 S1414 | Isoamyl Pyruvate | 香料 | — | K4 |
| `benzyl_butyl_ether` | 苄基丁基醚 | `C11H16O` | 液 | — | ✓ | 第五批 S1415 | Benzyl Butyl Ether | 香料 | — | K4 |
| `geranyl_cyclopropanecarboxamide` | N-3,7-二甲基-2,6-辛二烯-环丙基甲酰胺 | `C14H23NO` | 液 | — | ✓ | 第五批 S1416 | N-3,7-Dimethyl-2,6-octadienylcyclopropylcarboxamide | 香料 | — | K4 |
| `ethoxycarbonylmethyl_menthanecarboxamide` | N-(乙氧羰基甲基)-对-䓝烷-3-甲酰胺 | `C15H27NO3` | 固 | — | ✓ | 第五批 S1417 | [N-(Ethoxycarbonyl)methyl]-p-menthane-3-carboxamide | 香料 | — | K3 |
| `smokez_c_10` | 硬木烟熏香味料 SmokEz C-10 | 主成分乙酸、羰基化合物、酚类（水溶液） | 液 | — |  | 第五批 S1418 | SmokEz C-10 | 香料 | — | — |
| `scansmoke_sef_7525` | 硬木烟熏香味料 SEF 7525 | 主成分酚类、羰基化合物（焦油提取物） | 液 | — |  | 第五批 S1419 | Scansmoke SEF 7525 | 香料 | — | — |
| `nonadienyl_acetate` | (反式,顺式)-2,6-壬二烯-1-醇乙酸酯 | `C11H18O2` | 液 | — | ✓ | 第五批 S1420 | (E,Z)-2,6-Nonadien-1-ol Acetate | 香料 | — | K4 |
| `phenylethyl_anthranilate` | 邻氨基苯甲酸苯乙酯 | `C15H15NO2` | 液 | — | ✓ | 第五批 S1421 | Phenylethyl Anthranilate | 香料 | — | K4 |
| `propionyl_thiazoline` | 2-丙酰基-2-噻唑啉 | `C6H9NOS` | 液 | — | ✓ | 第五批 S1422 | 2-Propionyl-2-thiazoline | 香料 | — | K4 |
| `cis_8_tetradecenal` | 顺式-8-十四烯醛 | `C14H26O` | 液 | — | ✓ | 第五批 S1423 | (Z)-8-Tetradecenal | 香料 | — | K4 |
| `allyl_thiohexanoate` | 烯丙硫醇己酸酯 | `C9H16OS` | 液 | — | ✓ | 第五批 S1424 | Allyl Thiohexanoate | 香料 | — | K4 |
| `divanillin` | 双香兰素 | `C16H14O6` | 固 | — | ✓ | 第五批 S1425 | Divanillin | 香料 | — | K3 |
| `heptylcyclopropanecarboxylic_acid` | 顺式和反式-2-庚基环丙烷羧酸 | `C11H20O2` | 液 | — | ✓ | 第五批 S1426 | cis- and trans-2-Heptylcyclopropanecarboxylic Acid | 香料 | — | K4 |
| `hydroxy_methylhexanoic_delta_lactone` | 5-羟基-4-甲基己酸δ-内酯 | `C7H12O2` | 液 | — | ✓ | 第五批 S1427 | 5-Hydroxy-4-methylhexanoic Acid δ-Lactone | 香料 | — | K4 |
| `mercapto_2_pentanone_4` | 4-巯基-2-戊酮 | `C5H10OS` | 液 | — | ✓ | 第五批 S1428 | 4-Mercapto-2-pentanone | 香料 | — | K4 |
| `trithiaheptane` | 2,4,6-三硫杂庚烷 | `C4H10S3` | 液 | — | ✓ | 第五批 S1429 | 2,4,6-Trithiaheptane | 香料 | — | K4 |
| `methoxyphenyl_methylpentenone` | 1-(4-甲氧苯基)-4-甲基-1-戊烯-3-酮 | `C13H16O2` | 固 | — | ✓ | 第五批 S1430 | 1-(4-Methoxyphenyl)-4-methyl-1-penten-3-one | 香料 | — | K3 |
| `hydroxy_methylhexanone` | 3(2)-羟基-5-甲基-2(3)-己酮 | `C7H14O2` | 液 | — | ✓ | 第五批 S1431 | 3(2)-Hydroxy-5-methyl-2(3)-hexanone | 香料 | — | K4 |
| `dimercaptomethane` | 二巯基甲烷 | `CH4S2` | 液 | — | ✓ | 第五批 S1432 | Dimercaptomethane | 香料 | — | K4 |
| `butenolide` | 4-羟基-2-丁烯酸γ-内酯 | `C4H4O2` | 液 | — | ✓ | 第五批 S1433 | 4-Hydroxy-2-butenoic Acid γ-Lactone [2(5H)-Furanone] | 香料 | — | K4 |
| `isobutyl_methylthiobutyrate` | (+/-)-3-甲硫基丁酸异丁酯 | `C9H18O2S` | 液 | — | ✓ | 第五批 S1434 | (+/-)-Isobutyl 3-Methylthiobutyrate | 香料 | — | K4 |
| `methylthio_2_butanone_3` | 3-甲硫基-2-丁酮 | `C5H10OS` | 液 | — | ✓ | 第五批 S1435 | 3-(Methylthio)-2-butanone | 香料 | — | K4 |
| `ethyl_methyl_isobutylthiazoline` | 顺式和反式-5-乙基-4-甲基-2-(2-甲基丙基)-噻唑啉 | `C10H19NS` | 液 | — | ✓ | 第五批 S1436 | cis- and trans-5-Ethyl-4-methyl-2-(2-methylpropyl)thiazoline | 香料 | — | K4 |
| `pentanethiol` | 1-戊硫醇 | `C5H12S` | 液 | — | ✓ | 第五批 S1437 | 1-Pentanethiol | 香料 | — | K4 |
| `mercapto_4_methyl_2_pentanol` | (+/-)-4-巯基-4-甲基-2-戊醇 | `C6H14OS` | 液 | — | ✓ | 第五批 S1438 | (+/-)-4-Mercapto-4-methyl-2-pentanol | 香料 | — | K4 |
| `cyclohexyl_isovalerate` | 异戊酸环己酯 | `C11H20O2` | 液 | — | ✓ | 第五批 S1439 | Cyclohexyl Isovalerate | 香料 | — | K4 |
| `thienyl_disulfide` | 2-噻吩基二硫醚 | `C8H6S4` | 固 | — | ✓ | 第五批 S1440 | 2-Thienyl Disulfide | 香料 | — | K3 |
| `bis_methylfuryl_tetrasulfide` | 双(2-甲基-3-呋喃基)四硫醚 | `C10H10O2S4` | 液 | — | ✓ | 第五批 S1441 | Bis(2-methyl-3-furyl) Tetrasulfide | 香料 | — | K4 |
| `p_tolyl_octanoate` | 辛酸对-甲酚酯 | `C15H22O2` | 液 | — | ✓ | 第五批 S1442 | p-Tolyl Octanoate | 香料 | — | K4 |
| `maltol_propionate` | 丙酸麦芽酚酯 | `C9H10O4` | 液 | — | ✓ | 第五批 S1443 | Maltol Propionate | 香料 | — | K4 |
| `cis_2_hexenol` | 顺式-2-己烯-1-醇 | `C6H12O` | 液 | — | ✓ | 第五批 S1444 | (Z)-2-Hexen-1-ol | 香料 | — | K4 |
| `hexenal_propylene_glycol_acetal` | (+/-)反式和顺式-2-己烯醛丙二醇缩醛 | `C9H16O2` | 液 | — | ✓ | 第五批 S1445 | (+/-)-trans- and cis-2-Hexenal Propylene Glycol Acetal | 香料 | — | K4 |
| `ethylbutyl_acetate` | 乙酸2-乙基丁酯 | `C8H16O2` | 液 | — | ✓ | 第五批 S1446 | 2-Ethylbutyl Acetate | 香料 | — | K4 |
| `diethyl_3_methylpyrazine_2_5` | 2,5-二乙基-3-甲基吡嗪 | `C9H14N2` | 液 | — | ✓ | 第五批 S1447 | 2,5-Diethyl-3-methylpyrazine | 香料 | — | K4 |
| `methylthio_2_pentanone_4` | 4-(甲硫基)-2-戊酮 | `C6H12OS` | 液 | — | ✓ | 第五批 S1448 | 4-(Methylthio)-2-pentanone | 香料 | — | K4 |
| `methylthiomethyl_mercaptan` | 甲硫基甲硫醇 | `C2H6S2` | 液 | — | ✓ | 第五批 S1449 | Methylthiomethyl Mercaptan | 香料 | — | K4 |
| `ethyl_methyl_sec_butylthiazoline` | 顺式和反式-5-乙基-4-甲基-2-(1-甲基丙基)-噻唑啉 | `C10H19NS` | 液 | — | ✓ | 第五批 S1450 | cis- and trans-5-Ethyl-4-methyl-2-(1-methylpropyl)thiazoline | 香料 | — | K4 |
| `octanal_dimethyl_acetal` | 辛醛二甲缩醛 | `C10H22O2` | 液 | — | ✓ | 第五批 S1451 | Octanal Dimethyl Acetal | 香料 | — | K4 |
| `mercapto_methylbutyl_acetate` | 3-巯基-3-甲基-1-丁醇乙酸酯 | `C7H14O2S` | 液 | — | ✓ | 第五批 S1452 | 3-Mercapto-3-methyl-1-butyl Acetate | 香料 | — | K4 |
| `menthyl_hydroxybutyrate` | (R,S)-3-羟基丁酸l-薄荷酯 | `C14H26O3` | 液 | — | ✓ | 第五批 S1453 | l-Menthyl (R,S)-3-Hydroxybutyrate | 香料 | — | K4 |
| `isopropyl_isovalerate` | 异戊酸异丙酯 | `C8H16O2` | 液 | — | ✓ | 第五批 S1454 | Isopropyl Isovalerate | 香料 | — | K4 |
| `cis_4_decenyl_acetate` | 顺式-4-癸烯醇乙酸酯 | `C12H22O2` | 液 | — | ✓ | 第五批 S1455 | cis-4-Decenyl Acetate | 香料 | — | K4 |
| `geranyl_tiglate` | 惕各酸香叶酯 | `C15H24O2` | 液 | — | ✓ | 第五批 S1456 | Geranyl Tiglate | 香料 | — | K4 |
| `benzoylanthranilic_acid` | N-苯甲酰邻氨基苯甲酸 | `C14H11NO3` | 固 | — | ✓ | 第五批 S1457 | N-Benzoylanthranilic Acid | 香料 | — | K3 |
| `farnesylacetone` | 2,6,10-三甲基-2,6,10-十五碳三烯-14-酮 | `C18H30O` | 液 | — | ✓ | 第五批 S1458 | 2,6,10-Trimethyl-2,6,10-pentadecatrien-14-one | 香料 | — | K4 |
| `dimethylthiazole_2_5` | 2,5-二甲基噻唑 | `C5H7NS` | 液 | — | ✓ | 第五批 S1459 | 2,5-Dimethylthiazole | 香料 | — | K4 |
| `methylthiomethyl_butyrate` | 甲硫基甲醇丁酸酯 | `C6H12O2S` | 液 | — | ✓ | 第五批 S1460 | Methylthiomethyl Butyrate | 香料 | — | K4 |
| `methylthioethanol` | 2-甲硫基乙醇 | `C3H8OS` | 液 | — | ✓ | 第五批 S1461 | 2-(Methylthio)ethanol | 香料 | — | K4 |
| `diethyl_trisulfide` | 二乙基三硫醚 | `C4H10S3` | 液 | — | ✓ | 第五批 S1462 | Diethyl Trisulfide | 香料 | — | K4 |
| `mercapto_p_menthan_3_one_1` | 顺式和反式-1-巯基-对-䓝烷-3-酮 | `C10H18OS` | 液 | — | ✓ | 第五批 S1463 | cis- and trans-1-Mercapto-p-menthan-3-one | 香料 | — | K4 |
| `hydroxy_methyldecenoic_gamma_lactone` | 4-羟基-4-甲基-7-顺式-癸烯酸γ-内酯 | `C11H18O2` | 液 | — | ✓ | 第五批 S1464 | 4-Hydroxy-4-methyl-7-cis-decenoic Acid γ-Lactone | 香料 | — | K4 |
| `methyloctanal_2` | 2-甲基辛醛 | `C9H18O` | 液 | — | ✓ | 第五批 S1465 | 2-Methyloctanal | 香料 | — | K4 |
| `methyl_propylcyclohexenone` | 3-甲基-5-丙基-2-环己烯-1-酮 | `C10H16O` | 液 | — | ✓ | 第五批 S1466 | 3-Methyl-5-propyl-2-cyclohexen-1-one | 香料 | — | K4 |
| `nonadienol_2_4` | 2,4-壬二烯-1-醇 | `C9H16O` | 液 | — | ✓ | 第五批 S1467 | 2,4-Nonadien-1-ol | 香料 | — | K4 |
| `cyclopentanethiol` | 环戊硫醇 | `C5H10S` | 液 | — | ✓ | 第五批 S1468 | Cyclopentanethiol | 香料 | — | K4 |
| `benzeneacetonitrile_menthanecarboxamide` | N-对苯乙腈基薄荷烷基甲酰胺 | `C19H26N2O` | 固 | — | ✓ | 第五批 S1469 | N-p-Benzeneacetonitrile Menthanecarboxamide | 香料 | — | K3 |
| `pyridinylethyl_menthanecarboxamide` | N-[2-(吡啶-2-基)乙基]薄荷烷基甲酰胺 | `C18H28N2O` | 固 | — | ✓ | 第五批 S1470 | N-[2-(Pyridin-2-yl)ethyl]-3-p-menthanecarboxamide | 香料 | — | K3 |
| `aminodimethylthienopyrimidinone_hcl` | 4-氨基-5,6-二甲基噻吩并[2,3-d]嘧啶-2(1H)-酮盐酸盐 | `C8H10ClN3OS` | 固 | — | ✓ | 第五批 S1471 | 4-Amino-5,6-dimethylthieno[2,3-d]pyrimidin-2(1H)-one Hydrochloride | 香料 | — | K3 |
| `aminobenzothiadiazinyloxy_propylamide` | 3-[(4-氨基-2,2-二氧-1H-2,1,3-苯并噻二嗪-5-基)氧]-2,2-二甲基-N-丙基丙酰胺 | `C15H22N4O4S` | 固 | — | ✓ | 第五批 S1472 | 3-[(4-Amino-2,2-dioxido-1H-2,1,3-benzothiadiazin-5-yl)oxy]-2,2-dimethyl-N-propylpropanamide | 香料 | — | K3 |
| `methionylglycine` | L-蛋氨酰基甘氨酸 | `C7H14N2O3S` | 固 | — | ✓ | 第五批 S1473 | L-Methionylglycine | 香料 | — | K3 |
| `pentyl_3h_furanone` | 5-戊基-3H-呋喃-2-酮 | `C9H14O2` | 液 | — | ✓ | 第五批 S1474 | 5-Pentyl-3H-furan-2-one | 香料 | — | K4 |
| `dithiahexane` | 2,5-二硫杂己烷 | `C4H10S2` | 液 | — | ✓ | 第五批 S1475 | 2,5-Dithiahexane | 香料 | — | K4 |
| `aminooxoethylphenyl_menthanecarboxamide` | (2S,5R)-N-[4-(2-氨基-2-氧代乙基)苯基]-5-甲基-2-(丙基-2-)环己烷甲酰胺 | `C19H28N2O2` | 固 | — | ✓ | 第五批 S1476 | (2S,5R)-N-[4-(2-Amino-2-oxoethyl)phenyl]-5-methyl-2-(propan-2-yl)cyclohexanecarboxamide | 香料 | — | K3 |
| `methylfurfuryl_mercaptan` | 5-甲基-2-呋喃甲硫醇 | `C6H8OS` | 液 | — | ✓ | 第五批 S1477 | 5-Methyl-2-furanmethanethiol (5-Methylfurfuryl Mercaptan) | 香料 | — | K4 |
| `methyloctanal_6` | 6-甲基辛醛 | `C9H18O` | 液 | — | ✓ | 第五批 S1478 | 6-Methyloctanal | 香料 | — | K4 |
| `ethyl_dimethyldihydrodithiazine` | 2(4)-乙基-4(2),6-二甲基二氢-1,3,5-二噻嗪 | `C7H15NS2` | 液 | — | ✓ | 第五批 S1479 | 2(4)-Ethyl-4(2),6-dimethyldihydro-1,3,5-dithiazinane | 香料 | — | K4 |
| `heptyl_methyl_dihydrofuranone` | 3-庚基二氢-5-甲基-2(3H)-呋喃酮 | `C12H22O2` | 液 | — | ✓ | 第五批 S1480 | 3-Heptyldihydro-5-methyl-2(3H)-furanone | 香料 | — | K4 |
| `vanillyl_alcohol` | 香兰醇 | `C8H10O3` | 固 | — | ✓ | 第五批 S1481 | Vanillyl Alcohol | 香料 | — | K3 |
| `decenoyloxydecanoic_acid` | 6-[5(6)-癸烯酰氧基]癸酸 | `C20H36O4` | 液 | — | ✓ | 第五批 S1482 | 6-[5(6)-Decenoyloxy]decanoic Acid | 香料 | — | K7 |
| `isoxazolylmethyl_pyrazolyl_hydantoin` | 3-{1-[(3,5-二甲基-1,2-噁唑-4-基)甲基]-1H-吡唑-4-基}-1-(3-羟基苄基)咪唑啉-2,4-二酮 | `C19H19N5O4` | 固 | — | ✓ | 第五批 S1484 | 3-{1-[(3,5-Dimethyl-1,2-oxazol-4-yl)methyl]-1H-pyrazol-4-yl}-1-(3-hydroxybenzyl)imidazolidine-2,4-dione | 香料 | — | K3 |
| `aminoquinolinecarboxylic_acid_sulfate` | 4-氨基-5-[3-(异丙基氨基)-2,2-二甲基-3-氧代丙氧基]-2-甲基喹啉-3-羧酸硫酸盐 | `C19H27N3O8S` | 固 | — | ✓ | 第五批 S1485 | 4-Amino-5-[3-(isopropylamino)-2,2-dimethyl-3-oxopropoxy]-2-methylquinoline-3-carboxylic Acid Sulfate | 香料 | — | K3 |
| `decen_2_one_9` | 9-癸烯-2-酮 | `C10H18O` | 液 | — | ✓ | 第五批 S1486 | 9-Decen-2-one | 香料 | — | K4 |
| `methylheptanal_6` | 6-甲基庚醛 | `C8H16O` | 液 | — | ✓ | 第五批 S1487 | 6-Methylheptanal | 香料 | — | K4 |
| `menthyl_cyclopropanecarboxamide` | N-(2-异丙基-5-甲基环己基)环丙基甲酰胺 | `C14H25NO` | 固 | — | ✓ | 第五批 S1488 | Cyclopropanecarboxylic Acid (2-Isopropyl-5-methylcyclohexyl)amide | 香料 | — | K3 |
| `hydroxy_methylhexenoic_gamma_lactone` | 4-羟基-4-甲基-5-己烯酸γ-内酯 | `C7H10O2` | 液 | — | ✓ | 第五批 S1489 | 4-Hydroxy-4-methyl-5-hexenoic Acid γ-Lactone | 香料 | — | K4 |
| `furfuryl_methylfuryl_disulfide` | 糠基2-甲基-3-呋喃基二硫醚 | `C10H10O2S2` | 液 | — | ✓ | 第五批 S1490 | Furfuryl 2-Methyl-3-furyl Disulfide | 香料 | — | K4 |
| `decenoic_acid_4` | 4-癸烯酸 | `C10H18O2` | 液 | — | ✓ | 第五批 S1491 | 4-Decenoic Acid | 香料 | — | K4 |
| `methylthiazolylethyl_propionate` | 2-(4-甲基-5-噻唑基)乙醇丙酸酯 | `C9H13NO2S` | 液 | — | ✓ | 第五批 S1492 | 2-(4-Methyl-5-thiazolyl)ethyl Propionate | 香料 | — | K4 |
| `octanedione_4_5` | 4,5-辛二酮 | `C8H14O2` | 液 | — | ✓ | 第五批 S1493 | 4,5-Octanedione | 香料 | — | K4 |
| `ethyl_hydroxydecanoate` | 5-羟基癸酸乙酯 | `C12H24O3` | 液 | — | ✓ | 第五批 S1494 | Ethyl 5-Hydroxydecanoate | 香料 | — | K4 |
| `dioctyl_adipate` | 己二酸二辛酯 | `C22H42O4` | 液 | — | ✓ | 第五批 S1495 | Dioctyl Adipate | 香料 | — | K4 |
| `ethyl_linalyl_ether` | 乙基芳樟基醚 | `C12H22O` | 液 | — | ✓ | 第五批 S1496 | Ethyl Linalyl Ether | 香料 | — | K4 |
| `anethole` | 大茴香脑 | `C10H12O` | 液 | — | ✓ | 第五批 S1497 | trans-Anethole (Anise Camphor) | 香料 | — | K4 |
| `phloretin` | 根皮素 | `C15H14O5` | 固 | — | ✓ | 第五批 S1498 | Phloretin | 香料 | — | K3 |
| `propionylpyrrole` | 2-丙酰吡咯 | `C7H9NO` | 固 | — | ✓ | 第五批 S1499 | 2-Propionylpyrrole | 香料 | — | K3 |
| `allyl_propenyl_disulfide` | 烯丙基1-丙烯基二硫醚 | `C6H10S2` | 液 | — | ✓ | 第五批 S1500 | Allyl 1-Propenyl Disulfide | 香料 | — | K4 |
| `acetoxybutanone` | 2-乙酰氧基-3-丁酮 | `C6H10O3` | 液 | — | ✓ | 第五批 S1501 | 2-Acetoxy-3-butanone | 香料 | — | K4 |
| `cyclohexylethanol` | (+/-)-1-环己基乙醇 | `C8H16O` | 液 | — | ✓ | 第五批 S1502 | (+/-)-1-Cyclohexylethanol | 香料 | — | K4 |
| `glutamyl_valyl_glycine` | L-γ-谷氨酰-L-缬氨酰-甘氨酸 | `C12H21N3O6` | 固 | — | ✓ | 第五批 S1503 | L-γ-Glutamyl-L-valyl-glycine | 香料 | — | K3 |
| `methoxyphenyl_menthanecarboxamide` | (1R,2S,5R)-N-(4-甲氧苯基)-5-甲基-2-(1-甲基乙基)环己基甲酰胺 | `C18H27NO2` | 固 | — | ✓ | 第五批 S1504 | (1R,2S,5R)-N-(4-Methoxyphenyl)-5-methyl-2-(1-methylethyl)cyclohexanecarboxamide | 香料 | — | K3 |
| `tolyloxy_pyrazolyl_thenyl_acetamide` | 2-(4-甲基苯氧基)-N-(1H-吡唑-3-基)-N-(噻吩-2-基甲基)乙酰胺 | `C17H17N3O2S` | 固 | — | ✓ | 第五批 S1505 | 2-(4-Methylphenoxy)-N-(1H-pyrazol-3-yl)-N-(thiophen-2-ylmethyl)acetamide | 香料 | — | K3 |
| `hexylpyridine` | 2-己基吡啶 | `C11H17N` | 液 | — | ✓ | 第五批 S1506 | 2-Hexylpyridine | 香料 | — | K4 |