# 09 物料与中间产物化学式（GT 联动）

前三批（[03](03-first-batch.md)、[13](13-batch-2.md)、[14](14-batch-3.md)）涉及的原料、中间体、副产物与合成产物共 **341 种**：306 种有确定化学式，其中 **300 种已由结构式（SMILES）经 RDKit 反算元素组成逐一核对一致**；另 6 种（高岭石、五氧化二钒、红斑素、红曲红素、叶绿素 a、叶绿素铜钠）为公认化学式，未做结构核对；35 种高分子或天然混合物只给组成描述。第四批（[15](15-batch-4.md)）另增 362 种（含 2026-11-05 拆分新增的 18 种）：中间体 146 种（备注“第四批：”加用途编号）、产物 198 种（备注“第四批 N 号”），化学式由反应式配平与人工核对，“核对”栏留空，待补 RDKit 核对；另有 31 种第四批产物沿用已有物料，备注补了编号。表内按无机物、有机小分子、高分子分组，组内按批次先后排列。添加剂本身的化学式见 [02](02-gb2760-additive-list.md)。

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
| `bittern` | 苦卤 | MgCl2、MgSO4、KCl、NaCl 浓溶液 | 液 | — |  | 本模组流体；多组分混合物，不带纯度（2026-11-05） | Bittern | 化学品 | #B8B06A | — |
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
| `nitrous_oxide` | 氧化亚氮 | `N2O` | 气 | — |  | 第四批：144 副产；GB 2760 表 C.2 加工助剂（推进、起泡） | Nitrous Oxide | 化学品 | #D8E2EA | K9 |
| `silicon` | 硅 | `Si` | 固 | — |  | 第四批：163 | Silicon | 化学品 | — | K1 |
| `zinc_dithionite` | 连二亚硫酸锌 | `ZnS2O4` | 溶液 | — |  | 第四批：167 | Zinc Dithionite | 化学品 | #E6EAD8 | K0 |
| `phosphorus_trichloride` | 三氯化磷 | `PCl3` | 液 | — |  | 第四批：204 | Phosphorus Trichloride | 化学品 | #F0F0E0 | K4 |
| `sodium` | 钠 | `Na` | 固 | — |  | 第四批：204 | Sodium | 化学品 | — | K1 |
| `titanyl_sulfate` | 硫酸氧钛 | `TiOSO4` | 溶液 | — |  | 第四批：198 | Titanyl Sulfate | 化学品 | #E8E8F4 | K0 |
| `metatitanic_acid` | 偏钛酸 | `TiO(OH)2` | 固 | — |  | 第四批：198 | Metatitanic Acid | 化学品 | — | K1 |
| `titanium_tetrachloride` | 四氯化钛 | `TiCl4` | 液 | — |  | 第四批：198 | Titanium Tetrachloride | 化学品 | #F2F0E6 | K4 |
| `hydrogen_peroxide` | 过氧化氢（双氧水） | `H2O2` | 溶液 | — |  | 第四批：253、340；54 B 已用 | Hydrogen Peroxide | 化学品 | #DDEFF5 | K4 |
| `ammonium_persulfate` | 过二硫酸铵 | `(NH4)2S2O8` | 固 | — |  | 第四批：253（过氧化氢中间体）、310（聚合引发剂） | Ammonium Persulfate | 化学品 | — | K1 |
| `stannous_chloride` | 氯化亚锡 | `SnCl2·2H2O` | 固 | — |  | 第四批：282 | Stannous Chloride | 化学品 | — | K1 |
| `sodium_trimetaphosphate` | 三偏磷酸钠 | `Na3P3O9` | 固 | — |  | 第四批：293、294、295、309 | Sodium Trimetaphosphate | 化学品 | — | K1 |
| `bismuth_molybdate_catalyst` | 钼酸铋催化剂 | `Bi2(MoO4)3` | 固 | — |  | 第四批：310 | Bismuth Molybdate Catalyst | 催化剂 | — | — |
| `zinc_chloride` | 氯化锌 | `ZnCl2` | 固 | — |  | 第四批：325 | Zinc Chloride | 催化剂 | — | — |
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
| `glutamic_acid` | L-谷氨酸 | `C5H9NO4` | 固 | — | ✓ |  | L-Glutamic Acid | 化学品 | — | K3 |
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
| `alpha_tocopherol` | α-生育酚 | `C29H50O2` | 液 | — | ✓ |  | α-Tocopherol | 化学品 | #C89030 | K4 |
| `epigallocatechin_gallate` | 表没食子儿茶素没食子酸酯 | `C22H18O11` | 固 | — | ✓ | 茶多酚主成分 | Epigallocatechin Gallate | 化学品 | — | K5 |
| `carnosic_acid` | 鼠尾草酸 | `C20H28O4` | 固 | — | ✓ | 迷迭香提取物 | Carnosic Acid | 化学品 | — | K5 |
| `methyl_stearate` | 硬脂酸甲酯 | `C19H38O2` | 液 | — | ✓ | 脂肪酸甲酯代表 | Methyl Stearate | 化学品 | #F0E8C8 | K7 |
| `sucrose_monostearate` | 单硬脂酸蔗糖酯 | `C30H56O12` | 固 | — | ✓ |  | Sucrose Monostearate | 化学品 | — | K8 |
| `sorbitan_monostearate` | 山梨醇酐单硬脂酸酯（司盘 60） | `C24H46O6` | 固 | — | ✓ |  | Sorbitan Monostearate (Span 60) | 化学品 | — | K8 |
| `inosinic_disodium` | 5'-肌苷酸二钠 | `C10H11N4Na2O8P` | 固 | — | ✓ | 第四批 177 号 | Disodium 5'-Inosinate | 化学品 | — | K3 |
| `guanylic_disodium` | 5'-鸟苷酸二钠 | `C10H12N5Na2O8P` | 固 | — | ✓ | 第四批 176 号 | Disodium 5'-Guanylate | 化学品 | — | K3 |
| `adenylic_acid` | 5'-腺苷酸 | `C10H14N5O7P` | 固 | — | ✓ | 脱氨转化为肌苷酸 | 5'-Adenylic Acid | 化学品 | — | K3 |
| `benzene` | 苯 | `C6H6` | 液 | benzene | ✓ |  | Benzene | 化学品 | #F0F0E0 | K4 |
| `nitrobenzene` | 硝基苯 | `C6H5NO2` | 液 | nitrobenzene | ✓ |  | Nitrobenzene | 化学品 | #E8D060 | K4 |
| `aniline` | 苯胺 | `C6H7N` | 液 | — | ✓ |  | Aniline | 化学品 | #B88850 | K4 |
| `sulfanilic_acid` | 对氨基苯磺酸 | `C6H7NO3S` | 固 | — | ✓ | 重氮组分 | Sulfanilic Acid | 化学品 | — | K3 |
| `naphthalene` | 萘 | `C10H8` | 固 | naphthalene | ✓ |  | Naphthalene | 化学品 | — | K3 |
| `beta_naphthol` | 2-萘酚 | `C10H8O` | 固 | — | ✓ |  | 2-Naphthol | 化学品 | — | K3 |
| `schaeffer_acid` | 2-萘酚-6-磺酸 | `C10H8O4S` | 固 | — | ✓ | 偶合组分 | 2-Naphthol-6-sulfonic Acid | 化学品 | — | K3 |
| `n_phenylglycine` | N-苯基甘氨酸 | `C8H9NO2` | 固 | — | ✓ | 合成靛蓝中间体 | N-Phenylglycine | 化学品 | — | K3 |
| `indigo` | 靛青 | `C16H10N2O2` | 固 | — | ✓ | 靛蓝染料，磺化前 | Indigo | 化学品 | — | K3 |
| `indigo_carmine` | 靛蓝 | `C16H8N2Na2O8S2` | 固 | — | ✓ | GB 2760 名称；化学名靛蓝二磺酸钠 | Indigo Carmine | 化学品 | — | K10 |
| `l_aspartic_acid` | L-天冬氨酸 | `C4H7NO4` | 固 | — | ✓ |  | L-Aspartic Acid | 化学品 | — | K3 |
| `l_phenylalanine` | L-苯丙氨酸 | `C9H11NO2` | 固 | — | ✓ |  | L-Phenylalanine | 化学品 | — | K3 |
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
| `thiamine_hydrochloride` | 盐酸硫胺（维生素 B1） | `C12H18Cl2N4OS` | 固 | — | ✓ | 营养强化剂 | Thiamine Hydrochloride (Vitamin B1) | 化学品 | — | K3 |
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
| `ferric_citrate` | 柠檬酸铁 | `C6H5FeO7` | 溶液 | — |  | 第四批：160 | Ferric Citrate | 化学品 | #A0602A | K0 |
| `methyl_chloride` | 氯甲烷 | `CH3Cl` | 气 | — |  | 第四批：163、304、314、363、366 | Chloromethane | 化学品 | #DCE6E0 | K9 |
| `dimethyldichlorosilane` | 二甲基二氯硅烷 | `C2H6Cl2Si` | 液 | — |  | 第四批：163 | Dimethyldichlorosilane | 化学品 | #E2E6EE | K4 |
| `methyltrichlorosilane` | 甲基三氯硅烷 | `CH3Cl3Si` | 液 | — |  | 第四批：163 副产 | Methyltrichlorosilane | 化学品 | #E0E4EC | K4 |
| `trimethylchlorosilane` | 三甲基氯硅烷 | `C3H9ClSi` | 液 | — |  | 第四批：163（制封端剂） | Trimethylchlorosilane | 化学品 | #E4E8F0 | K4 |
| `octamethylcyclotetrasiloxane` | 八甲基环四硅氧烷 | `C8H24O4Si4` | 液 | — |  | 第四批：163 | Octamethylcyclotetrasiloxane | 化学品 | #E8ECF2 | K4 |
| `hexamethyldisiloxane` | 六甲基二硅氧烷 | `C6H18OSi2` | 液 | — |  | 第四批：163（封端剂） | Hexamethyldisiloxane | 化学品 | #E6EAF2 | K4 |
| `sodium_formate` | 甲酸钠 | `CHNaO2` | 固 | — |  | 第四批：167、274（副产）；GB 2760 表 C.2 加工助剂 | Sodium Formate | 化学品 | — | K3 |
| `guanylic_acid` | 5'-鸟苷酸 | `C10H14N5O8P` | 固 | — |  | 第四批：176 | 5'-Guanylic Acid | 化学品 | — | K3 |
| `inosinic_acid` | 5'-肌苷酸 | `C10H13N4O8P` | 固 | — |  | 第四批：177；73 已用此式 | 5'-Inosinic Acid | 化学品 | — | K3 |
| `succinic_acid` | 琥珀酸 | `C4H6O4` | 固 | — |  | 第四批：178；以后琥珀酸单甘油酯等 | Succinic Acid | 化学品 | — | K3 |
| `l_cystine` | L-胱氨酸 | `C6H12N2O4S2` | 固 | — |  | 第四批：181 | L-Cystine | 化学品 | — | K3 |
| `l_cysteine` | L-半胱氨酸 | `C3H7NO2S` | 固 | — |  | 第四批：181 | L-Cysteine | 化学品 | — | K3 |
| `magnesium_bicarbonate` | 碳酸氢镁 | `Mg(HCO3)2` | 溶液 | — |  | 第四批：182 | Magnesium Bicarbonate | 化学品 | #E6EEF0 | K0 |
| `nitronaphthalene` | 1-硝基萘 | `C10H7NO2` | 固 | — |  | 第四批：194、199（81 同用） | 1-Nitronaphthalene | 化学品 | — | K3 |
| `naphthylamine` | 1-萘胺 | `C10H9N` | 固 | — |  | 第四批：194、199（81 同用） | 1-Naphthylamine | 化学品 | — | K3 |
| `naphthionic_diazonium` | 4-磺基-1-萘重氮盐 | `C10H6N2O3S` | 溶液 | — |  | 第四批：194、199（81 同用） | 4-Sulfo-1-naphthalenediazonium Salt | 化学品 | #E0C878 | K0 |
| `r_acid` | 2-萘酚-3,6-二磺酸（R 酸） | `C10H8O7S2` | 固 | — |  | 第四批：194 | 2-Naphthol-3,6-disulfonic Acid (R Acid) | 化学品 | — | K3 |
| `nevile_winther_acid` | 1-萘酚-4-磺酸 | `C10H8O4S` | 固 | — |  | 第四批：199 | 1-Naphthol-4-sulfonic Acid (Nevile–Winther Acid) | 化学品 | — | K3 |
| `phthalic_anhydride` | 邻苯二甲酸酐 | `C8H4O3` | 固 | — |  | 第四批：195、202 | Phthalic Anhydride | 化学品 | — | K3 |
| `benzenedisulfonic_acid` | 间苯二磺酸 | `C6H6O6S2` | 溶液 | — |  | 第四批：195 | Benzene-1,3-disulfonic Acid | 化学品 | #E8E2C8 | K0 |
| `resorcinol` | 间苯二酚 | `C6H6O2` | 固 | — |  | 第四批：195、325 | Resorcinol | 化学品 | — | K3 |
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
| `n_hexane` | 正己烷 | `C6H14` | 液 | — |  | 第四批：207、230、231、234；20、60 已用 | n-Hexane | 化学品 | #E6EEF0 | K4 |
| `ethyl_acetate` | 乙酸乙酯 | `C4H8O2` | 液 | — |  | 第四批：238、277（副产）、329；59、66 已作溶剂 | Ethyl Acetate | 化学品 | #E2EEEA | K4 |
| `oleic_acid` | 油酸 | `C18H34O2` | 液 | — |  | 第四批：246、273；植物油或动物脂脂肪酸（276）分出 | Oleic Acid | 化学品 | #E8D890 | K7 |
| `lauric_acid` | 月桂酸 | `C12H24O2` | 固 | — |  | 第四批：256、257、324 | Lauric Acid | 化学品 | — | K7 |
| `palmitic_acid` | 棕榈酸 | `C16H32O2` | 固 | — |  | 第四批：248、258、323、330；棕榈油或动物脂脂肪酸精馏 | Palmitic Acid | 化学品 | — | K7 |
| `caprylic_acid` | 辛酸 | `C8H16O2` | 液 | — |  | 第四批：252、342 | Octanoic Acid | 化学品 | #ECE6CC | K7 |
| `capric_acid` | 癸酸 | `C10H20O2` | 固 | — |  | 第四批：252 | Decanoic Acid | 化学品 | — | K7 |
| `sorbitan_monolaurate` | 山梨醇酐单月桂酸酯（司盘 20） | `C18H34O6` | 液 | — |  | 第四批 256 号；用于 256、257 | Sorbitan Monolaurate (Span 20) | 化学品 | #D8B860 | K8 |
| `sorbitan_monopalmitate` | 山梨醇酐单棕榈酸酯（司盘 40） | `C22H42O6` | 固 | — |  | 第四批 248 号；用于 248、258 | Sorbitan Monopalmitate (Span 40) | 化学品 | — | K8 |
| `sorbitan_monooleate` | 山梨醇酐单油酸酯（司盘 80） | `C24H44O6` | 液 | — |  | 第四批 246 号；用于 246；72 已用 | Sorbitan Monooleate (Span 80) | 化学品 | #C8A048 | K8 |
| `diacetyltartaric_anhydride` | 双乙酰酒石酸酐 | `C8H8O7` | 固 | — |  | 第四批：249 | Diacetyltartaric Anhydride | 化学品 | — | K3 |
| `triacetin` | 三乙酸甘油酯 | `C9H14O6` | 液 | — |  | 第四批：259 | Glyceryl Triacetate | 化学品 | #EAEAD8 | K4 |
| `octene` | 1-辛烯 | `C8H16` | 液 | — |  | 第四批：262 | 1-Octene | 化学品 | #E4ECEA | K4 |
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
| `lauryl_alcohol` | 1-十二醇（月桂醇） | `C12H26O` | 液 | — |  | 第四批：324 | 1-Dodecanol (Lauryl Alcohol) | 化学品 | #EEEEE4 | K7 |
| `hexanoic_acid` | 己酸 | `C6H12O2` | 液 | — |  | 第四批：325 | Hexanoic Acid | 化学品 | #EEE8D0 | K7 |
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
| `glycyrrhizic_acid` | 甘草酸 | `C42H62O16` | 固 | — |  | 第四批：352、353、362 | Glycyrrhizic Acid | 化学品 | — | K5 |
| `isobutyric_acid` | 异丁酸 | `C4H8O2` | 液 | — |  | 第四批：354 | Isobutyric Acid | 化学品 | #ECE8D8 | K4 |
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
| `formic_acid` | 甲酸（蚁酸） | `CH2O2` | 液 | — |  | 第四批：366 | Methanoic Acid (Formic Acid) | 化学品 | #EAEEEE | K4 |
| `l_malic_acid` | L-苹果酸 | `C4H6O5` | 固 | — |  | 第四批 140 号；56 B 同产；DL 型为 `malic_acid` | L-Malic Acid | 化学品 | — | K3 |
| `adipic_acid` | 己二酸 | `C6H10O4` | 固 | — |  | 第四批 144 号 | Adipic Acid | 化学品 | — | K3 |
| `potassium_citrate` | 柠檬酸钾 | `C6H5K3O7·H2O` | 固 | — |  | 第四批 147 号 | Potassium Citrate | 化学品 | — | K3 |
| `monosodium_citrate` | 柠檬酸一钠 | `C6H7NaO7` | 固 | — |  | 第四批 149 号 | Monosodium Citrate | 化学品 | — | K3 |
| `monosodium_fumarate` | 富马酸一钠 | `C4H3NaO4` | 固 | — |  | 第四批 152 号 | Monosodium Fumarate | 化学品 | — | K3 |
| `sodium_gluconate` | 葡萄糖酸钠 | `C6H11NaO7` | 固 | — |  | 第四批 153 号 | Sodium Gluconate | 化学品 | — | K3 |
| `sodium_dl_malate` | DL-苹果酸钠 | `C4H4Na2O5` | 固 | — |  | 第四批 154 号；二钠盐 | Sodium DL-Malate | 化学品 | — | K3 |
| `sodium_l_malate` | L-苹果酸钠 | `C4H4Na2O5` | 固 | — |  | 第四批 155 号；二钠盐 | Sodium L-Malate | 化学品 | — | K3 |
| `magnesium_stearate` | 硬脂酸镁 | `C36H70MgO4` | 固 | — |  | 第四批 157 号 | Magnesium Stearate | 化学品 | — | K7 |
| `ferrous_gluconate` | 葡萄糖酸亚铁 | `C12H22FeO14·2H2O` | 固 | — |  | 第四批 175 号 | Ferrous Gluconate | 化学品 | — | K3 |
| `disodium_succinate` | 琥珀酸二钠 | `C4H4Na2O4·6H2O` | 固 | — |  | 第四批 178 号 | Disodium Succinate | 化学品 | — | K3 |
| `l_alanine` | L-丙氨酸 | `C3H7NO2` | 固 | — |  | 第四批 179 号 | L-Alanine | 化学品 | — | K3 |
| `glycine` | 氨基乙酸（甘氨酸） | `C2H5NO2` | 固 | — |  | 第四批 180 号 | Aminoacetic Acid (Glycine) | 化学品 | — | K3 |
| `l_cysteine_hydrochloride` | L-半胱氨酸盐酸盐 | `C3H8ClNO2S·H2O` | 固 | — |  | 第四批 181 号 | L-Cysteine Hydrochloride | 化学品 | — | K3 |
| `potassium_lactate` | 乳酸钾 | `C3H5KO3` | 溶液 | — |  | 第四批 188 号；约 60% 水溶液；界限见 10 §1.5 | Potassium Lactate | 化学品 | #EEECDC | K4 |
| `sodium_lactate` | 乳酸钠 | `C3H5NaO3` | 溶液 | — |  | 第四批 189 号；约 60% 水溶液；界限见 10 §1.5 | Sodium Lactate | 化学品 | #EEEADA | K4 |
| `amaranth` | 苋菜红 | `C20H11N2Na3O10S3` | 固 | — |  | 第四批 194 号 | Amaranth | 化学品 | — | K10 |
| `erythrosine` | 赤藓红 | `C20H6I4Na2O5` | 固 | — |  | 第四批 195 号 | Erythrosine | 化学品 | — | K10 |
| `new_red` | 新红 | `C18H12N3Na3O11S3` | 固 | — |  | 第四批 196 号 | New Red | 化学品 | — | K10 |
| `azorubine` | 酸性红（偶氮玉红） | `C20H12N2Na2O7S2` | 固 | — |  | 第四批 199 号 | Azorubine (Carmoisine) | 化学品 | — | K10 |
| `quinoline_yellow` | 喹啉黄 | `C18H9NNa2O8S2` | 固 | — |  | 第四批 202 号 | Quinoline Yellow | 化学品 | — | K10 |
| `apocarotenal` | β-阿朴-8'-胡萝卜素醛 | `C30H40O` | 固 | — |  | 第四批 204 号 | β-Apo-8'-carotenal | 化学品 | — | K5 |
| `bixin` | 胭脂树橙 | `C25H30O4` | 固 | — |  | 第四批 232 号；油溶型，水溶型见 `norbixin` | Bixin | 化学品 | — | K5 |
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
| `diphenyl_ether` | 联苯醚 | `C12H10O` | 固 | — |  | 第四批 339 号；熔点约 27 °C | Diphenyl Ether | 化学品 | — | K3 |
| `glyceryl_monocaprylate` | 单辛酸甘油酯 | `C11H22O4` | 固 | — |  | 第四批 342 号 | Glyceryl Monocaprylate | 化学品 | — | K8 |
| `sodium_methyl_paraben` | 对羟基苯甲酸甲酯钠 | `C8H7NaO3` | 固 | — |  | 第四批 343 号 | Sodium Methyl p-Hydroxybenzoate | 化学品 | — | K3 |
| `dimethyl_dicarbonate` | 二甲基二碳酸盐 | `C4H6O5` | 液 | — |  | 第四批 344 号 | Dimethyl Dicarbonate | 化学品 | #ECEEE6 | K4 |
| `sodium_ethyl_paraben` | 对羟基苯甲酸乙酯钠 | `C9H9NaO3` | 固 | — |  | 第四批 347 号 | Sodium Ethyl p-Hydroxybenzoate | 化学品 | — | K3 |
| `isomaltulose` | 异麦芽酮糖 | `C12H22O11·H2O` | 固 | — |  | 第四批 350 号 | Isomaltulose | 化学品 | — | K2 |
| `monopotassium_glycyrrhizinate` | 甘草酸一钾 | `C42H61KO16` | 固 | — |  | 第四批 352 号 | Monopotassium Glycyrrhizinate | 化学品 | — | K5 |
| `ammonium_glycyrrhizinate` | 甘草酸铵 | `C42H65NO16` | 固 | — |  | 第四批 353 号 | Ammonium Glycyrrhizinate | 化学品 | — | K5 |
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

## C 高分子、天然混合物

| 建议 ID | 名称 | 化学式 / 组成 | 状态 | GTCEu 材料 | 核对 | 备注 | 英文名（待审） | 分类 | 颜色 | 纯度类别 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `starch` | 淀粉 | `(C6H10O5)n` | 固 | — |  |  | Starch | 化学品 | — | K6 |
| `cellulose` | 纤维素 | `(C6H10O5)n` | 固 | — |  | 第四批 162 号 | Cellulose | 化学品 | — | K6 |
| `alginic_acid` | 海藻酸 | `(C6H8O6)n` | 固 | — |  |  | Alginic Acid | 化学品 | — | K6 |
| `sodium_alginate` | 海藻酸钠 | `(C6H7NaO6)n` | 固 | — |  |  | Sodium Alginate | 化学品 | — | K6 |
| `calcium_alginate` | 海藻酸钙 | `[(C6H7O6)2Ca]n` | 固 | — |  | 第四批 317 号 | Calcium Alginate | 化学品 | — | K6 |
| `agarose` | 琼脂糖 | `(C12H18O9)n` | 固 | — |  | 琼脂主成分 | Agarose | 化学品 | — | K6 |
| `pectin` | 果胶 | 部分甲酯化的聚半乳糖醛酸，单元 C6H8O6 / C7H10O6 | 固 | — |  |  | Pectin | 化学品 | — | K6 |
| `gelatin` | 明胶 | 蛋白质（甘氨酸–脯氨酸–羟脯氨酸重复） | 固 | gelatin |  |  | Gelatin | 化学品 | — | K6 |
| `gluten` | 面筋 | 蛋白质（麦醇溶蛋白 + 麦谷蛋白） | 固 | — |  |  | Gluten | 化学品 | — | K6 |
| `lignin` | 木质素 | 酚类聚合物 | 固 | — |  | 多组分混合物，不带纯度（2026-11-05） | Lignin | 化学品 | — | — |
| `caramel` | 焦糖色 | 糖类热解聚合物 | 液 | — |  | 第四批 239 号 | Caramel Color | 化学品 | #4A2408 | K5 |
| `wood_tar` | 木焦油 | 酚类混合物（愈创木酚、甲酚等） | 液 | creosote（近似） |  | 导入 `#forge:creosote`；杂酚油为其馏分；多组分混合物，不带纯度（2026-11-05） | Wood Tar | 化学品 | #3A2414 | — |
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
| `glutamate_broth` | 谷氨酸发酵液 | 谷氨酸铵为主的发酵液，含菌体、残糖 | 液 | — |  | 等电结晶原料；多组分混合物，不带纯度（2026-11-05） | Glutamate Fermentation Broth | 化学品 | #C8B070 | — |
| `resin` | 松脂 | 萜类树脂，主成分松香酸 C20H30O2 | 液 | — |  | 松木 H15 割取（07 第 137 号）；原在 08 §C，I28 移入（K0 带含量）；多组分混合物，不带纯度（2026-11-05） | Resin | 化学品 | #C8902A | — |
| `kelp_ash` | 海藻灰 | 碳酸钠、氯化钠、硫酸盐等的混合物 | 固 | — |  | H10 烧灰（07 第 5 号）；I28 补；多组分混合物，不带纯度（2026-11-05） | Kelp Ash | 化学品 | — | — |
| `wood_ash` | 草木灰 | 碳酸钾、碳酸钙等的混合物 | 固 | — |  | H10 烧灰（07 第 7 号）；I28 补；多组分混合物，不带纯度（2026-11-05） | Wood Ash | 化学品 | — | — |
| `soda_lye` | 碱液 | 碳酸钠为主的水溶液 | 溶液 | — |  | H05 淋海藻灰（07 第 5 号），熬干得粗碱；I28 补；多组分混合物，不带纯度（2026-11-05） | Soda Lye | 化学品 | #C8BC8C | — |
| `potash_lye` | 钾碱液 | 碳酸钾为主的水溶液 | 溶液 | — |  | H05 淋草木灰（07 第 7 号），熬干得粗钾碱；I28 补；多组分混合物，不带纯度（2026-11-05） | Potash Lye | 化学品 | #B8A47C | — |
| `saltpeter_liquor` | 硝水 | 硝酸钙、硝酸钾等的水溶液 | 溶液 | — |  | H05 淋硝土（07 第 24 号）；I28 补；多组分混合物，不带纯度（2026-11-05） | Saltpeter Liquor | 化学品 | #D8D0B0 | — |
| `agar_jelly` | 石花菜冻 | 琼脂凝胶，含水约 99% | 固 | — |  | 红藻熬煮、过滤、冷却所得（07 第 14 号），冻融得条状琼脂；I28 补 | Agar Jelly | 化学品 | — | K0 |
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
| `paraffin_wax` | 石蜡 | C20–C40 正构烷烃混合物 | 固 | — |  | 第四批：262、272（副产）；多组分混合物，不带纯度（2026-11-05） | Paraffin Wax | 化学品 | — | — |
| `casein` | 酪蛋白 | 乳蛋白（αs-、β-、κ-酪蛋白） | 固 | — |  | 第四批：244 | Casein | 化学品 | — | K6 |
| `seedlac` | 颗粒紫胶 | 紫胶树脂为主，含蜡与残余色素 | 固 | — |  | 第四批：271；多组分混合物，不带纯度（2026-11-05） | Seedlac | 化学品 | — | — |
| `liquefied_starch` | 液化淀粉液 | DE 5–8 的糊精水溶液 | 溶液 | — |  | 第四批：284、285、301；多组分混合物，不带纯度（2026-11-05） | Liquefied Starch | 化学品 | #EDE8D8 | — |
| `alpha_cd_cyclohexane_complex` | α-环状糊精–环己烷包合物 | α-环状糊精与环己烷的包合物（湿饼） | 固 | — |  | 第四批：284；湿饼，不带纯度（2026-11-05） | α-Cyclodextrin–Cyclohexane Complex | 化学品 | — | — |
| `beta_cd_toluene_complex` | β-环状糊精–甲苯包合物 | β-环状糊精与甲苯的包合物（湿饼） | 固 | — |  | 第四批：301；湿饼，不带纯度（2026-11-05） | β-Cyclodextrin–Toluene Complex | 化学品 | — | — |
| `polysuccinimide` | 聚琥珀酰亚胺 | `(C4H3NO2)n` | 固 | — |  | 第四批：286 | Polysuccinimide | 化学品 | — | K3 |
| `immobilized_sucrose_isomerase` | 固定化蔗糖异构酶菌体 | 海藻酸钙包埋的红色精朊杆菌灭活菌体 | 固 | — |  | 第四批：350 | Immobilized Sucrose Isomerase Cells | 催化剂 | — | — |
| `pancreatic_extract` | 胰脏浸提液 | 组成：胰蛋白酶原、胰凝乳蛋白酶原、磷脂酶 A2 原、胰淀粉酶、胰脂肪酶的稀酸溶液 | 溶液 | — |  | 第四批：397、399、418、419；酶源，不带纯度（2026-11-05） | Pancreatic Extract | 化学品 | #D8B0A0 | — |
| `metatartaric_acid` | 偏酒石酸 | `(C4H4O5)n` | 固 | — |  | 第四批 141 号；酒石酸分子间酯化聚合物，极易吸湿 | Metatartaric Acid | 化学品 | — | K3 |
| `microcrystalline_cellulose` | 微晶纤维素 | `(C6H10O5)n` | 固 | — |  | 第四批 156 号 | Microcrystalline Cellulose | 化学品 | — | K6 |
| `ferric_ammonium_citrate` | 柠檬酸铁铵 | 柠檬酸铁(III)铵络盐，组成不定 | 固 | — |  | 第四批 160 号 | Ferric Ammonium Citrate | 化学品 | — | K3 |
| `ferric_tartrate` | 酒石酸铁 | 铁(III) 与 meso-、D-、L-酒石酸的络合物水溶液，组成不定 | 溶液 | — |  | 第四批 161 号；产品为溶液（喷于盐上防结块）；界限见 10 §1.5 | Iron Tartrate | 化学品 | #8A4A20 | K4 |
| `polydimethylsiloxane` | 聚二甲基硅氧烷及其乳液 | `(C2H6OSi)n` | 液 | — |  | 第四批 163 号；硅油；乳液为硅油加司盘 60、吐温 80 乳化所得，不另设物品 | Polydimethylsiloxane | 化学品 | #E8EEF2 | K3 |
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
| `quillaja_extract` | 皂树皮提取物 | 三萜皂苷为主，苷元以皂树酸 C30H46O5 计 | 固 | — |  | 第四批 269 号 | Quillaia Extract | 化学品 | — | K5 |
| `ghatti_gum` | 达瓦树胶 | 酸性多糖钙镁盐（阿拉伯糖、半乳糖、甘露糖、葡萄糖醛酸） | 固 | — |  | 第四批 270 号；原料胶块为 `materials/ghatti_lump` | Gum Ghatti | 化学品 | — | K6 |
| `shellac` | 紫胶（虫胶） | 天然树脂，紫胶桐酸 C16H32O5、紫胶酸 C15H20O6 等羟基酸的聚酯 | 固 | — |  | 第四批 271 号 | Shellac | 化学品 | — | K5 |
| `liquid_paraffin` | 液体石蜡（白油） | 饱和烷烃混合物 CnH2n+2 | 液 | — |  | 第四批 272 号 | Liquid Paraffin (White Mineral Oil) | 化学品 | #F2F4F2 | K7 |
| `morpholine_fruit_wax` | 吗啉脂肪酸盐果蜡 | 巴西棕榈蜡或紫胶与油酸吗啉盐的水乳液 | 液 | — |  | 第四批 273 号；复配乳液，配比未确认 | Morpholine Fatty Acid Salt Fruit Wax | 化学品 | #E8E0C8 | K12 |
| `rosin_pentaerythritol_ester` | 松香季戊四醇酯 | 松香酸与季戊四醇的酯混合物 | 固 | — |  | 第四批 274 号 | Pentaerythritol Ester of Rosin | 化学品 | — | K5 |
| `carnauba_wax` | 巴西棕榈蜡 | C26–C30 脂肪酸与长链醇的酯为主的蜡混合物 | 固 | — |  | 第四批 275 号 | Carnauba Wax | 化学品 | — | K7 |
| `polyvinyl_alcohol` | 聚乙烯醇 | `(C2H4O)n` | 固 | — |  | 第四批 277 号 | Polyvinyl Alcohol | 化学品 | — | K3 |
| `pullulan` | 普鲁兰多糖 | `(C6H10O5)n` | 固 | — |  | 第四批 278 号；α-1,4 / α-1,6 葡聚糖 | Pullulan | 化学品 | — | K6 |
| `polyethylene_glycol` | 聚乙二醇 | `(C2H4O)n` | 固 | — |  | 第四批 279 号；即 H(OCH2CH2)nOH；低分子量为液态 | Polyethylene Glycol | 化学品 | — | K3 |
| `karaya_gum` | 刺梧桐胶 | 乙酰化酸性多糖（半乳糖、鼠李糖、半乳糖醛酸） | 固 | — |  | 第四批 283 号；原料胶块为 `materials/karaya_lump` | Karaya Gum (Sterculia Gum) | 化学品 | — | K6 |
| `potassium_polyaspartate` | 聚天冬氨酸钾 | `(C4H4KNO3)n` | 固 | — |  | 第四批 286 号 | Potassium Polyaspartate | 化学品 | — | K3 |
| `potassium_alginate` | 海藻酸钾 | `(C6H7KO6)n` | 固 | — |  | 第四批 287 号 | Potassium Alginate | 化学品 | — | K6 |
| `gum_arabic` | 阿拉伯胶 | 阿拉伯半乳聚糖–蛋白复合物 | 固 | — |  | 第四批 288 号；原料胶块为 `materials/acacia_lump` | Gum Arabic | 化学品 | — | K6 |
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
| `acetylated_distarch_adipate` | 乙酰化双淀粉己二酸酯 | 己二酸交联并乙酰化的淀粉 | 固 | — |  | 第四批 307 号 | Acetylated Distarch Adipate | 化学品 | — | K6 |
| `oxidized_hydroxypropyl_starch` | 氧化羟丙基淀粉 | 次氯酸钠氧化的羟丙基淀粉 | 固 | — |  | 第四批 308 号 | Oxidized Hydroxypropyl Starch | 化学品 | — | K6 |
| `distarch_phosphate` | 磷酸酯双淀粉 | 三偏磷酸钠交联的淀粉 | 固 | — |  | 第四批 309 号 | Distarch Phosphate | 化学品 | — | K6 |
| `sodium_polyacrylate` | 聚丙烯酸钠 | `(C3H3NaO2)n` | 固 | — |  | 第四批 310 号 | Sodium Polyacrylate | 化学品 | — | K3 |
| `artemisia_gum` | 沙蒿胶 | 沙蒿籽种皮多糖 | 固 | — |  | 第四批 311 号 | Artemisia Seed Gum | 化学品 | — | K6 |
| `tara_gum` | 刺云实胶 | 半乳甘露聚糖，甘露糖∶半乳糖约 3∶1 | 固 | — |  | 第四批 312 号 | Tara Gum | 化学品 | — | K6 |
| `curdlan` | 可得然胶 | `(C6H10O5)n` | 固 | — |  | 第四批 313 号；β-1,3-葡聚糖 | Curdlan | 化学品 | — | K6 |
| `methylcellulose` | 甲基纤维素 | `C7H12O5` | 固 | — |  | 第四批 314 号；以取代度 1 的葡萄糖单元计 | Methylcellulose | 化学品 | — | K6 |
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
| `paprika_oleoresin` | 辣椒油树脂 | 辣椒红素 C40H56O3、辣椒素 C18H27NO3 并存的溶剂提取物 | 液 | — |  | 第四批 367 号；60 B 路线中间产物，09 未注册；辣椒橙（08.107）原料 | Paprika Oleoresin | 化学品 | #B02010 | K5 |
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

## D 纯度数据（来源纯度与单个物料的覆盖）

本节与上表的“纯度类别”一栏一起由 `export_materials.py` 导出（2026-11-04 定，I28）：每种带纯度类别的物料写成 `materials.json` 中的纯度数据，数据生成再输出为数据包文件 `data/foodindustrial/purity_materials/<ID>.json`；类别界限取自 [10](10-purity-jei-ae2.md) 第 1.4 节，导出为 `purity_categories.json`，数据生成输出为 `data/foodindustrial/purity_categories/<类别 ID>.json`。两组文件都随服务器启动与 `/reload` 加载并同步到客户端，整合包可改（文件格式见 10 第 4.7 节）。

- **来源纯度**：配方的来源方式不写数值时取这个值；K0 物料写了才带纯度（显示“含量”），外部物品与缺标签时也取这个值（10 第 1.3、1.6、2.1 节）。数值取自 [07](07-manual-crafting.md) 与 machines/01 中出现的写定值；机器配方需要别的数值时在配方里写 `value`。
- **四档覆盖**：“—”沿用所属类别的界限，“不适用”表示该物料没有这一档。数值与 10 第 1.5 节一致，导出时逐项核对；10 第 1.5 节中尚未进入主表的物料（栀子黄）在注册时补到这里。
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
| `natural_rubber` | 天然橡胶 | 90% | 80% | 90% | 95% | 不适用 | 07 第 136 号；10 §1.5 |
| `caramel` | 焦糖色 | 30% | 20% | 30% | 40% | 不适用 | 07 第 18 号；10 §1.5 |
| `capsanthin` | 辣椒红素 | 0.2% | 0.5% | 1% | 2.4% | 90% | 07 第 60 号（辣椒油）；10 §1.5 |
| `monascorubrin` | 红曲红素 | — | 1% | 5% | 10% | 90% | 10 §1.5 |
| `betanin` | 甜菜苷 | — | 0.05% | 0.1% | 0.27% | 90% | 10 §1.5 |
| `turmeric` | 姜黄 | 3% | 0.5% | 1% | 1.8% | 不适用 | 07 第 61 号（姜黄粉）；10 §1.5 |
| `indigo` | 靛青 | 2% | — | — | — | — | 07 第 84 号（靛泥） |
| `lecithin` | 磷脂（卵磷脂） | 30% | — | — | — | — | 07 第 69 号（蛋黄卵磷脂） |
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
