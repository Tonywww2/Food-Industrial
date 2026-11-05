# 09 物料与中间产物化学式（GT 联动）

前三批（[03](03-first-batch.md)、[13](13-batch-2.md)、[14](14-batch-3.md)）涉及的原料、中间体、副产物与合成产物共 **341 种**：306 种有确定化学式，其中 **300 种已由结构式（SMILES）经 RDKit 反算元素组成逐一核对一致**；另 6 种（高岭石、五氧化二钒、红斑素、红曲红素、叶绿素 a、叶绿素铜钠）为公认化学式，未做结构核对；35 种高分子或天然混合物只给组成描述。表内按无机物、有机小分子、高分子分组，组内按批次先后排列。添加剂本身的化学式见 [02](02-gb2760-additive-list.md)。

## 表示约定

| 项 | 约定 | 例 |
| --- | --- | --- |
| 有机物 | Hill 式，C、H 在前，其余按字母序 | `C6H8O7`、`C5H8NNaO4` |
| 无机物 | 习惯写法，保留原子团括号 | `Ca(OH)2`、`KAl(SO4)2·12H2O` |
| 水合物 | `·nH2O`，n 可为小数 | `CaSO4·0.5H2O` |
| 高分子 | 重复单元 + n | `(C6H7NaO6)n` |
| 混合物 | 写主成分 | 苦卤：MgCl2、MgSO4、KCl、NaCl |
| 数字 | 不用下标，便于检索、写进 JSON、与 GTCEu 对照 | — |

本表与 08 §C 是物料主表（2026-11-04 定）：`tools/materials/export_materials.py` 把两处的表导出为 `src/main/resources/foodindustrial/materials.json`，注册物品与流体时读取；新增物料只改表。“英文名”“颜色”由实现方拟定，待审；“分类”取 化学品 / 催化剂 / 原料 / 菌种 / 酶 / 原版（开放集合，可扩展），决定创造模式标签页和是否带纯度（化学品、菌种、酶带纯度，见 10 §1）；“纯度类别”是带纯度物料的分档类别（K0–K11，界限见 [10](10-purity-jei-ae2.md) 第 1.4 节），“—”表示不带纯度（催化剂、原料、原版、复配产品）；08 §C 中分类为菌种、酶的物料一律为 K11，不另列；“颜色”是流体的着色。化学式一栏中反引号里的内容是 Tooltip 显示的化学式，没有反引号的只是组成描述，不显示。

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
| `seawater` | 海水 | NaCl 约 3.5% 及 Mg、K、Ca 盐的水溶液 | 液 | salt_water（近似） |  | 本模组流体 | Seawater | 原料 | #2E6E8E | — |
| `bittern` | 苦卤 | MgCl2、MgSO4、KCl、NaCl 浓溶液 | 液 | — |  | 本模组流体 | Bittern | 化学品 | #B8B06A | K0 |
| `salt` | 氯化钠（盐） | `NaCl` | 固 | salt | ✓ |  | Sodium Chloride (Salt) | 化学品 | — | K1 |
| `brine` | 饱和食盐水 | `NaCl` | 溶液 | — | ✓ | 约 26% 水溶液；氨碱法、氯碱电解原料 | Saturated Brine | 化学品 | #C8D8E8 | K0 |
| `potassium_chloride` | 氯化钾 | `KCl` | 固 | rock_salt | ✓ | GT 名为 rock_salt | Potassium Chloride | 化学品 | — | K1 |
| `carnallite` | 光卤石 | `KMgCl3·6H2O` | 固 | — | ✓ | 即 KCl·MgCl2·6H2O | Carnallite | 化学品 | — | K1 |
| `magnesium_chloride` | 氯化镁（卤片） | `MgCl2·6H2O` | 固 | magnesium_chloride | ✓ | GT 为无水物 | Magnesium Chloride (Nigari) | 化学品 | — | K1 |
| `epsom_salt` | 硫酸镁（泻盐） | `MgSO4·7H2O` | 固 | — | ✓ |  | Magnesium Sulfate (Epsom Salt) | 化学品 | — | K1 |
| `magnesium_hydroxide` | 氢氧化镁 | `Mg(OH)2` | 固 | — | ✓ |  | Magnesium Hydroxide | 化学品 | — | K1 |
| `magnesite` | 碳酸镁（菱镁矿） | `MgCO3` | 固 | magnesite | ✓ |  | Magnesium Carbonate (Magnesite) | 化学品 | — | K1 |
| `calcium_carbonate` | 碳酸钙（石灰石） | `CaCO3` | 固 | calcite / calcium_carbonate | ✓ |  | Calcium Carbonate (Limestone) | 化学品 | — | K1 |
| `quicklime` | 氧化钙（生石灰） | `CaO` | 固 | quicklime | ✓ |  | Calcium Oxide (Quicklime) | 化学品 | — | K1 |
| `slaked_lime` | 氢氧化钙（熟石灰） | `Ca(OH)2` | 固 | calcium_hydroxide | ✓ |  | Calcium Hydroxide (Slaked Lime) | 化学品 | — | K1 |
| `calcium_chloride` | 氯化钙 | `CaCl2` | 固 | calcium_chloride | ✓ |  | Calcium Chloride | 化学品 | — | K1 |
| `gypsum` | 硫酸钙（石膏） | `CaSO4·2H2O` | 固 | gypsum | ✓ | 二水物；第 12 号添加剂 | Calcium Sulfate (Gypsum) | 化学品 | — | K1 |
| `plaster` | 半水硫酸钙（熟石膏） | `CaSO4·0.5H2O` | 固 | — | ✓ |  | Calcium Sulfate Hemihydrate (Plaster of Paris) | 化学品 | — | K1 |
| `calcium_sulfide` | 硫化钙 | `CaS` | 固 | — | ✓ | 路布兰废渣 | Calcium Sulfide | 化学品 | — | K1 |
| `soda_ash` | 碳酸钠（纯碱） | `Na2CO3` | 固 | soda_ash | ✓ |  | Sodium Carbonate (Soda Ash) | 化学品 | — | K1 |
| `trona` | 碳酸氢三钠（天然碱） | `Na3H(CO3)2·2H2O` | 固 | trona | ✓ |  | Sodium Sesquicarbonate (Trona) | 化学品 | — | K1 |
| `sodium_bicarbonate` | 碳酸氢钠（小苏打） | `NaHCO3` | 固 | sodium_bicarbonate | ✓ |  | Sodium Bicarbonate (Baking Soda) | 化学品 | — | K1 |
| `potassium_carbonate` | 碳酸钾 | `K2CO3` | 固 | potassium_carbonate | ✓ | GT 的 potash 是 K2O，不要对错 | Potassium Carbonate | 化学品 | — | K1 |
| `sodium_hydroxide` | 氢氧化钠（烧碱） | `NaOH` | 固 | sodium_hydroxide | ✓ |  | Sodium Hydroxide (Caustic Soda) | 化学品 | — | K1 |
| `potassium_hydroxide` | 氢氧化钾 | `KOH` | 固 | potassium_hydroxide | ✓ |  | Potassium Hydroxide | 化学品 | — | K1 |
| `hydrogen_chloride` | 氯化氢 | `HCl` | 气 | hydrochloric_acid | ✓ | 气体；水溶液为盐酸 `hydrochloric_acid` | Hydrogen Chloride | 化学品 | #D8E8D0 | K9 |
| `hydrochloric_acid` | 盐酸 | `HCl` | 溶液 | hydrochloric_acid | ✓ | 约 31% 水溶液；配方中的“盐酸” | Hydrochloric Acid | 化学品 | #D0E8C0 | K4 |
| `sulfuric_acid` | 硫酸 | `H2SO4` | 液 | sulfuric_acid | ✓ |  | Sulfuric Acid | 化学品 | #E8D8A0 | K4 |
| `potassium_bisulfate` | 硫酸氢钾 | `KHSO4` | 固 | — | ✓ | 硝石制硝酸副产 | Potassium Bisulfate | 化学品 | — | K1 |
| `sulfur` | 硫（硫磺） | `S` | 固 | sulfur | ✓ |  | Sulfur (Brimstone) | 化学品 | — | K1 |
| `sulfur_dioxide` | 二氧化硫 | `SO2` | 气 | sulfur_dioxide | ✓ |  | Sulfur Dioxide | 化学品 | #E0E0C8 | K9 |
| `sulfur_trioxide` | 三氧化硫 | `SO3` | 气 | sulfur_trioxide | ✓ |  | Sulfur Trioxide | 化学品 | #F0F0F0 | K9 |
| `sodium_bisulfite` | 亚硫酸氢钠 | `NaHSO3` | 溶液 | — | ✓ |  | Sodium Bisulfite | 化学品 | #E8F0F0 | K0 |
| `sodium_metabisulfite` | 焦亚硫酸钠 | `Na2S2O5` | 固 | — | ✓ |  | Sodium Metabisulfite | 化学品 | — | K1 |
| `sodium_sulfate` | 硫酸钠（元明粉） | `Na2SO4` | 固 | — | ✓ | 无水物；十水物为芒硝 Na2SO4·10H2O | Sodium Sulfate (Thenardite) | 化学品 | — | K1 |
| `sodium_sulfide` | 硫化钠 | `Na2S` | 固 | sodium_sulfide | ✓ |  | Sodium Sulfide | 化学品 | — | K1 |
| `ammonia` | 氨 | `NH3` | 气 | ammonia | ✓ |  | Ammonia | 化学品 | #E0E8F8 | K9 |
| `ammonium_chloride` | 氯化铵 | `NH4Cl` | 固 | ammonium_chloride | ✓ |  | Ammonium Chloride | 化学品 | — | K1 |
| `ammonium_bicarbonate` | 碳酸氢铵（臭粉） | `NH4HCO3` | 固 | — | ✓ |  | Ammonium Bicarbonate (Baker's Ammonia) | 化学品 | — | K1 |
| `ammonium_bisulfate` | 硫酸氢铵 | `NH4HSO4` | 固 | — | ✓ | 乳腈法副产 | Ammonium Bisulfate | 化学品 | — | K1 |
| `carbon_dioxide` | 二氧化碳 | `CO2` | 气 | carbon_dioxide | ✓ |  | Carbon Dioxide | 化学品 | #D8D8D8 | K9 |
| `carbon_monoxide` | 一氧化碳 | `CO` | 气 | carbon_monoxide | ✓ |  | Carbon Monoxide | 化学品 | #C8C8C8 | K9 |
| `oxygen` | 氧气 | `O2` | 气 | oxygen | ✓ |  | Oxygen | 化学品 | #C8E0FF | K9 |
| `nitrogen` | 氮气 | `N2` | 气 | nitrogen | ✓ |  | Nitrogen | 化学品 | #D0D8F0 | K9 |
| `hydrogen` | 氢气 | `H2` | 气 | hydrogen | ✓ |  | Hydrogen | 化学品 | #F0F8FF | K9 |
| `chlorine` | 氯气 | `Cl2` | 气 | chlorine | ✓ |  | Chlorine | 化学品 | #C8E060 | K9 |
| `nitric_oxide` | 一氧化氮 | `NO` | 气 | nitric_oxide | ✓ |  | Nitric Oxide | 化学品 | #D8D8E8 | K9 |
| `nitrogen_dioxide` | 二氧化氮 | `NO2` | 气 | nitrogen_dioxide | ✓ |  | Nitrogen Dioxide | 化学品 | #A04020 | K9 |
| `sodium_nitrite` | 亚硝酸钠 | `NaNO2` | 固 | sodium_nitrite | ✓ |  | Sodium Nitrite | 化学品 | — | K1 |
| `sodium_nitrate` | 硝酸钠 | `NaNO3` | 固 | — | ✓ |  | Sodium Nitrate | 化学品 | — | K1 |
| `potassium_nitrate` | 硝酸钾（硝石） | `KNO3` | 固 | saltpeter | ✓ |  | Potassium Nitrate (Saltpeter) | 化学品 | — | K1 |
| `calcium_nitrate` | 硝酸钙 | `Ca(NO3)2` | 固 | — | ✓ | 硝土主成分之一 | Calcium Nitrate | 化学品 | — | K1 |
| `hydroxyapatite` | 羟基磷灰石（骨灰） | `Ca5(PO4)3OH` | 固 | — | ✓ |  | Hydroxyapatite (Bone Ash) | 化学品 | — | K1 |
| `fluorapatite` | 氟磷灰石 | `Ca5(PO4)3F` | 固 | apatite（GT 为氯磷灰石 Ca5(PO4)3Cl） | ✓ |  | Fluorapatite | 化学品 | — | K1 |
| `tricalcium_phosphate` | 磷酸三钙 | `Ca3(PO4)2` | 固 | tricalcium_phosphate | ✓ |  | Tricalcium Phosphate | 化学品 | — | K1 |
| `phosphoric_acid` | 磷酸 | `H3PO4` | 液 | phosphoric_acid | ✓ |  | Phosphoric Acid | 化学品 | #F0F0E0 | K4 |
| `white_phosphorus` | 白磷 | `P4` | 固 | — | ✓ | GT 只有元素 phosphorus | White Phosphorus | 化学品 | — | K1 |
| `phosphorus_pentoxide` | 五氧化二磷 | `P4O10` | 固 | phosphorus_pentoxide | ✓ |  | Phosphorus Pentoxide | 化学品 | — | K1 |
| `monosodium_phosphate` | 磷酸二氢钠 | `NaH2PO4` | 固 | — | ✓ |  | Monosodium Phosphate | 化学品 | — | K1 |
| `sodium_acid_pyrophosphate` | 焦磷酸二氢二钠 | `Na2H2P2O7` | 固 | — | ✓ |  | Sodium Acid Pyrophosphate | 化学品 | — | K1 |
| `dicalcium_phosphate` | 磷酸氢钙 | `CaHPO4·2H2O` | 固 | — | ✓ | 明胶脱钙副产 | Dicalcium Phosphate | 化学品 | — | K1 |
| `hydrogen_fluoride` | 氟化氢 | `HF` | 气 | hydrofluoric_acid | ✓ |  | Hydrogen Fluoride | 化学品 | #E8F0E8 | K9 |
| `silicon_dioxide` | 二氧化硅 | `SiO2` | 固 | silicon_dioxide | ✓ |  | Silicon Dioxide | 化学品 | — | K1 |
| `calcium_silicate` | 硅酸钙 | `CaSiO3` | 固 | — | ✓ | 电弧炉渣 | Calcium Silicate | 化学品 | — | K1 |
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
| `sodium_acetate` | 乙酸钠 | `C2H3NaO2` | 固 | — | ✓ |  | Sodium Acetate | 化学品 | — | K3 |
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
| `benzoic_acid` | 苯甲酸 | `C7H6O2` | 固 | — | ✓ |  | Benzoic Acid | 化学品 | — | K3 |
| `sodium_benzoate` | 苯甲酸钠 | `C7H5NaO2` | 固 | — | ✓ |  | Sodium Benzoate | 化学品 | — | K3 |
| `sorbitol` | 山梨糖醇 | `C6H14O6` | 固 | — | ✓ |  | Sorbitol | 化学品 | — | K2 |
| `l_sorbose` | L-山梨糖 | `C6H12O6` | 固 | — | ✓ |  | L-Sorbose | 化学品 | — | K2 |
| `diacetone_sorbose` | 二丙酮-L-山梨糖 | `C12H20O6` | 固 | — | ✓ |  | Diacetone-L-sorbose | 化学品 | — | K3 |
| `ketogulonic_acid` | 2-酮-L-古龙酸 | `C6H10O7` | 固 | — | ✓ |  | 2-Keto-L-gulonic Acid | 化学品 | — | K3 |
| `methyl_ketogulonate` | 2-酮-L-古龙酸甲酯 | `C7H12O7` | 固 | — | ✓ |  | Methyl 2-Keto-L-gulonate | 化学品 | — | K3 |
| `ascorbic_acid` | 抗坏血酸（维生素 C） | `C6H8O6` | 固 | — | ✓ |  | Ascorbic Acid (Vitamin C) | 化学品 | — | K3 |
| `sodium_ascorbate` | 抗坏血酸钠 | `C6H7NaO6` | 固 | — | ✓ |  | Sodium Ascorbate | 化学品 | — | K3 |
| `glycerol` | 丙三醇（甘油） | `C3H8O3` | 液 | glycerol | ✓ |  | Glycerol (Glycerin) | 化学品 | #F0F0F0 | K4 |
| `stearic_acid` | 硬脂酸 | `C18H36O2` | 固 | — | ✓ |  | Stearic Acid | 化学品 | — | K7 |
| `sodium_stearate` | 硬脂酸钠（肥皂） | `C18H35NaO2` | 固 | — | ✓ | 皂化副产 | Sodium Stearate (Soap) | 化学品 | — | K7 |
| `tristearin` | 硬脂酸甘油三酯 | `C57H110O6` | 固 | — | ✓ | 动物脂代表物 | Glyceryl Tristearate | 化学品 | — | K7 |
| `triolein` | 油酸甘油三酯 | `C57H104O6` | 液 | seed_oil（未定义组成） | ✓ | 植物油代表物 | Glyceryl Trioleate | 化学品 | #F0D870 | K7 |
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
| `beta_carotene` | β-胡萝卜素 | `C40H56` | 固 | — | ✓ |  | β-Carotene | 化学品 | — | K5 |
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
| `sorbic_acid` | 山梨酸 | `C6H8O2` | 固 | — | ✓ |  | Sorbic Acid | 化学品 | — | K3 |
| `potassium_sorbate` | 山梨酸钾 | `C6H7KO2` | 固 | — | ✓ |  | Potassium Sorbate | 化学品 | — | K3 |
| `ethylene` | 乙烯 | `C2H4` | 气 | ethylene | ✓ |  | Ethylene | 化学品 | #E8E8E8 | K9 |
| `propionic_acid` | 丙酸 | `C3H6O2` | 液 | — | ✓ |  | Propionic Acid | 化学品 | #F0F0E8 | K4 |
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
| `malic_acid` | 苹果酸 | `C4H6O5` | 固 | — | ✓ |  | Malic Acid | 化学品 | — | K3 |
| `tartaric_acid` | L(+)-酒石酸 | `C4H6O6` | 固 | — | ✓ |  | L(+)-Tartaric Acid | 化学品 | — | K3 |
| `potassium_bitartrate` | 酒石酸氢钾（塔塔粉） | `C4H5KO6` | 固 | — | ✓ | 酒石主成分 | Potassium Bitartrate (Cream of Tartar) | 化学品 | — | K3 |
| `calcium_tartrate` | 酒石酸钙 | `C4H4CaO6` | 固 | — | ✓ |  | Calcium Tartrate | 化学品 | — | K3 |
| `epoxysuccinic_acid` | 顺式环氧琥珀酸 | `C4H4O5` | 固 | — | ✓ | 酶法酒石酸前体 | cis-Epoxysuccinic Acid | 化学品 | — | K3 |
| `trisodium_citrate` | 柠檬酸钠 | `C6H5Na3O7·2H2O` | 固 | — | ✓ |  | Sodium Citrate | 化学品 | — | K3 |
| `lycopene` | 番茄红素 | `C40H56` | 固 | — | ✓ |  | Lycopene | 化学品 | — | K5 |
| `capsanthin` | 辣椒红素 | `C40H56O3` | 固 | — | ✓ | 辣椒红主成分 | Capsanthin | 化学品 | — | K5 |
| `capsaicin` | 辣椒素 | `C18H27NO3` | 固 | — | ✓ | 脱辣时分出 | Capsaicin | 化学品 | — | K5 |
| `curcumin` | 姜黄素 | `C21H20O6` | 固 | — | ✓ |  | Curcumin | 化学品 | — | K5 |
| `riboflavin` | 核黄素（维生素 B2） | `C17H20N4O6` | 固 | — | ✓ |  | Riboflavin (Vitamin B2) | 化学品 | — | K3 |
| `alpha_tocopherol` | α-生育酚 | `C29H50O2` | 液 | — | ✓ |  | α-Tocopherol | 化学品 | #C89030 | K4 |
| `epigallocatechin_gallate` | 表没食子儿茶素没食子酸酯 | `C22H18O11` | 固 | — | ✓ | 茶多酚主成分 | Epigallocatechin Gallate | 化学品 | — | K5 |
| `carnosic_acid` | 鼠尾草酸 | `C20H28O4` | 固 | — | ✓ | 迷迭香提取物 | Carnosic Acid | 化学品 | — | K5 |
| `methyl_stearate` | 硬脂酸甲酯 | `C19H38O2` | 液 | — | ✓ | 脂肪酸甲酯代表 | Methyl Stearate | 化学品 | #F0E8C8 | K7 |
| `sucrose_monostearate` | 单硬脂酸蔗糖酯 | `C30H56O12` | 固 | — | ✓ |  | Sucrose Monostearate | 化学品 | — | K8 |
| `sorbitan_monostearate` | 山梨醇酐单硬脂酸酯（司盘 60） | `C24H46O6` | 固 | — | ✓ |  | Sorbitan Monostearate (Span 60) | 化学品 | — | K8 |
| `inosinic_disodium` | 5'-肌苷酸二钠 | `C10H11N4Na2O8P` | 固 | — | ✓ |  | Disodium 5'-Inosinate | 化学品 | — | K3 |
| `guanylic_disodium` | 5'-鸟苷酸二钠 | `C10H12N5Na2O8P` | 固 | — | ✓ |  | Disodium 5'-Guanylate | 化学品 | — | K3 |
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

## C 高分子、天然混合物

| 建议 ID | 名称 | 化学式 / 组成 | 状态 | GTCEu 材料 | 核对 | 备注 | 英文名（待审） | 分类 | 颜色 | 纯度类别 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `starch` | 淀粉 | `(C6H10O5)n` | 固 | — |  |  | Starch | 化学品 | — | K6 |
| `cellulose` | 纤维素 | `(C6H10O5)n` | 固 | — |  |  | Cellulose | 化学品 | — | K6 |
| `alginic_acid` | 海藻酸 | `(C6H8O6)n` | 固 | — |  |  | Alginic Acid | 化学品 | — | K6 |
| `sodium_alginate` | 海藻酸钠 | `(C6H7NaO6)n` | 固 | — |  |  | Sodium Alginate | 化学品 | — | K6 |
| `calcium_alginate` | 海藻酸钙 | `[(C6H7O6)2Ca]n` | 固 | — |  |  | Calcium Alginate | 化学品 | — | K6 |
| `agarose` | 琼脂糖 | `(C12H18O9)n` | 固 | — |  | 琼脂主成分 | Agarose | 化学品 | — | K6 |
| `pectin` | 果胶 | 部分甲酯化的聚半乳糖醛酸，单元 C6H8O6 / C7H10O6 | 固 | — |  |  | Pectin | 化学品 | — | K6 |
| `gelatin` | 明胶 | 蛋白质（甘氨酸–脯氨酸–羟脯氨酸重复） | 固 | gelatin |  |  | Gelatin | 化学品 | — | K6 |
| `gluten` | 面筋 | 蛋白质（麦醇溶蛋白 + 麦谷蛋白） | 固 | — |  |  | Gluten | 化学品 | — | K6 |
| `lignin` | 木质素 | 酚类聚合物 | 固 | — |  |  | Lignin | 化学品 | — | K6 |
| `caramel` | 焦糖色 | 糖类热解聚合物 | 液 | — |  |  | Caramel Color | 化学品 | #4A2408 | K5 |
| `wood_tar` | 木焦油 | 酚类混合物（愈创木酚、甲酚等） | 液 | creosote（近似） |  | 导入 `#forge:creosote`；杂酚油为其馏分，按木焦油计 | Wood Tar | 化学品 | #3A2414 | K0 |
| `coal_tar` | 煤焦油 | 芳烃混合物（苯、甲苯、萘等） | 液 | coal_tar |  |  | Coal Tar | 化学品 | #1A1A1A | K0 |
| `crude_oil` | 原油 | 烃类混合物 | 液 | oil |  |  | Crude Oil | 化学品 | #151510 | K0 |
| `carrageenan` | 卡拉胶 | 硫酸化半乳聚糖 | 固 | — |  |  | Carrageenan | 化学品 | — | K6 |
| `xanthan_gum` | 黄原胶 | 五糖重复单元胞外多糖 | 固 | — |  |  | Xanthan Gum | 化学品 | — | K6 |
| `gellan_gum` | 结冷胶 | 四糖重复单元胞外多糖 | 固 | — |  |  | Gellan Gum | 化学品 | — | K6 |
| `nisin` | 乳酸链球菌素 | 34 肽，`C143H230N42O37S7` | 固 | — |  |  | Nisin | 化学品 | — | K5 |
| `natamycin` | 纳他霉素 | 多烯大环内酯，`C33H47NO13` | 固 | — |  |  | Natamycin | 化学品 | — | K5 |
| `lecithin` | 磷脂（卵磷脂） | 磷脂酰胆碱为主的混合物 | 固 | — |  |  | Phospholipids (Lecithin) | 化学品 | — | K8 |
| `polyvinyl_acetate` | 聚乙酸乙烯酯 | `(C4H6O2)n` | 固 | polyvinyl_acetate |  | 胶基 | Polyvinyl Acetate | 化学品 | — | K3 |
| `polyisobutylene` | 聚异丁烯 | `(C4H8)n` | 固 | — |  | 胶基 | Polyisobutylene | 化学品 | — | K3 |
| `natural_rubber` | 天然橡胶 | `(C5H8)n` | 固 | rubber |  | 胶基 | Natural Rubber | 化学品 | — | K6 |
| `rosin` | 松香 | 树脂酸混合物，主成分松香酸 C20H30O2 | 固 | — |  |  | Rosin | 化学品 | — | K5 |
| `enzymes` | 酶制剂 | 蛋白质 | 固 | — |  |  | Enzyme Preparation | 酶 | — | K11 |
| `tannin` | 单宁 | 没食子酰葡萄糖混合物，以单宁酸 C76H52O46 计 | 固 | — |  | 没食子酸原料 | Tannin | 化学品 | — | K5 |
| `rosin_glycerol_ester` | 松香甘油酯 | 松香酸与丙三醇的酯混合物 | 固 | — |  | 胶基树脂 | Glycerol Ester of Rosin | 化学品 | — | K5 |
| `beeswax` | 蜂蜡 | 棕榈酸三十醇酯等长链酯混合物 | 固 | — |  | 胶基蜡、上光 | Beeswax | 化学品 | — | K7 |
| `microcrystalline_wax` | 微晶蜡 | C30–C70 支链烷烃混合物 | 固 | — |  | 胶基蜡 | Microcrystalline Wax | 化学品 | — | K7 |
| `lanolin` | 羊毛脂 | 甾醇与脂肪醇的脂肪酸酯混合物 | 固 | — |  | 胆固醇原料 | Lanolin | 化学品 | — | K7 |
| `castor_oil` | 蓖麻油 | 蓖麻油酸甘油三酯为主 | 液 | — |  | γ-癸内酯原料 | Castor Oil | 化学品 | #E8D890 | K7 |
| `gum_base` | 胶基 | 弹性体、树脂、蜡、软化剂、填料的混炼物 | 固 | — |  | 口香糖基料 | Gum Base | 化学品 | — | — |
| `glutamate_broth` | 谷氨酸发酵液 | 谷氨酸铵为主的发酵液，含菌体、残糖 | 液 | — |  | 等电结晶原料 | Glutamate Fermentation Broth | 化学品 | #C8B070 | K0 |
| `resin` | 松脂 | 萜类树脂，主成分松香酸 C20H30O2 | 液 | — |  | 松木 H15 割取（07 第 137 号），含量按松香计；原在 08 §C，I28 移入（K0 带含量） | Resin | 化学品 | #C8902A | K0 |
| `kelp_ash` | 海藻灰 | 碳酸钠、氯化钠、硫酸盐等的混合物 | 固 | — |  | H10 烧灰（07 第 5 号），含量按碳酸钠计；I28 补 | Kelp Ash | 化学品 | — | K0 |
| `wood_ash` | 草木灰 | 碳酸钾、碳酸钙等的混合物 | 固 | — |  | H10 烧灰（07 第 7 号），含量按碳酸钾计；I28 补 | Wood Ash | 化学品 | — | K0 |
| `soda_lye` | 碱液 | 碳酸钠为主的水溶液 | 溶液 | — |  | H05 淋海藻灰（07 第 5 号），熬干得粗碱；I28 补 | Soda Lye | 化学品 | #C8BC8C | K0 |
| `potash_lye` | 钾碱液 | 碳酸钾为主的水溶液 | 溶液 | — |  | H05 淋草木灰（07 第 7 号），熬干得粗钾碱；I28 补 | Potash Lye | 化学品 | #B8A47C | K0 |
| `saltpeter_liquor` | 硝水 | 硝酸钙、硝酸钾等的水溶液 | 溶液 | — |  | H05 淋硝土（07 第 24 号）；I28 补 | Saltpeter Liquor | 化学品 | #D8D0B0 | K0 |
| `agar_jelly` | 石花菜冻 | 琼脂凝胶，含水约 99% | 固 | — |  | 红藻熬煮、过滤、冷却所得（07 第 14 号），冻融得条状琼脂；I28 补 | Agar Jelly | 化学品 | — | K0 |
| `fish_liver_oil` | 鱼肝油 | 甘油三酯为主，含维生素 A、D | 液 | — |  | 熬炼鱼肝（07 第 107 号）；I28 补 | Fish Liver Oil | 化学品 | #E0B040 | K7 |

## D 纯度数据（来源纯度与单个物料的覆盖）

本节与上表的“纯度类别”一栏一起由 `export_materials.py` 导出（2026-11-04 定，I28）：每种带纯度类别的物料写成 `materials.json` 中的纯度数据，数据生成再输出为数据包文件 `data/foodindustrial/purity_materials/<ID>.json`；类别界限取自 [10](10-purity-jei-ae2.md) 第 1.4 节，导出为 `purity_categories.json`，数据生成输出为 `data/foodindustrial/purity_categories/<类别 ID>.json`。两组文件都随服务器启动与 `/reload` 加载并同步到客户端，整合包可改（文件格式见 10 第 4.7 节）。

- **来源纯度**：配方的来源方式不写数值时取这个值；K0 物料写了才带纯度（显示“含量”），外部物品与缺标签时也取这个值（10 第 1.3、1.6、2.1 节）。数值取自 [07](07-manual-crafting.md) 与 machines/01 中出现的写定值；机器配方需要别的数值时在配方里写 `value`。
- **四档覆盖**：“—”沿用所属类别的界限，“不适用”表示该物料没有这一档。数值与 10 第 1.5 节一致，导出时逐项核对；10 第 1.5 节中尚未进入主表的物料（甜菊糖苷、栀子黄）在注册时补到这里。
- 只列有来源纯度或覆盖的物料；其余沿用类别界限、没有来源纯度。

| 建议 ID | 名称 | 来源纯度 | 粗制 | 精制 | 食品级 | 实验室级 | 出处 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| `salt` | 氯化钠（盐） | 83% | 90% | 95% | 97% | 99.8% | 07 海盐；10 §1.5 |
| `bittern` | 苦卤 | 7% | — | — | — | — | 07 第 11 号 |
| `gypsum` | 硫酸钙（石膏） | 80% | — | — | — | — | 07 第 12 号；machines/01 |
| `soda_ash` | 碳酸钠（纯碱） | 20% | — | — | — | — | 07 第 5 号（熬干） |
| `potassium_carbonate` | 碳酸钾 | 50% | — | — | — | — | 07 第 7 号（熬干） |
| `potassium_nitrate` | 硝酸钾（硝石） | 90% | — | — | — | — | 07 第 24 号 |
| `potassium_bitartrate` | 酒石酸氢钾（塔塔粉） | 80% | — | — | — | — | 07 第 55 号（酒石）；13 H14 |
| `acetic_acid` | 乙酸（醋酸） | 4% | 不适用 | 不适用 | 3.5% | 99.8% | 07 第 1 号（食醋）；10 §1.5 |
| `lactic_acid` | 乳酸 | 1.2% | 20% | 50% | 80% | 90% | 07 第 2 号（酸浆）；10 §1.5 |
| `citric_acid` | 柠檬酸 | — | 90% | 95% | 99.5% | 99.8% | 10 §1.5 |
| `ethanol` | 乙醇（酒精） | — | 80% | 90% | 92.4% | 99.5% | 10 §1.5 |
| `sulfuric_acid` | 硫酸 | — | 60% | 80% | 92.5% | 98% | 10 §1.5 |
| `hydrochloric_acid` | 盐酸 | — | 20% | 25% | 31% | 36% | 10 §1.5 |
| `phosphoric_acid` | 磷酸 | — | 50% | 75% | 85% | 不适用 | 10 §1.5 |
| `tartrazine` | 柠檬黄 | — | 50% | 70% | 87% | 95% | 10 §1.5 |
| `sunset_yellow` | 日落黄 | — | 50% | 70% | 87% | 95% | 10 §1.5 |
| `glyceryl_monostearate` | 单硬脂酸甘油酯 | — | 20% | 30% | 40% | 90% | 10 §1.5 |
| `natural_rubber` | 天然橡胶 | 90% | 80% | 90% | 95% | 不适用 | 07 第 136 号；10 §1.5 |
| `caramel` | 焦糖色 | 30% | 20% | 30% | 40% | 不适用 | 07 第 18 号；10 §1.5 |
| `capsanthin` | 辣椒红素 | 0.2% | 1% | 5% | 10% | 50% | 07 第 60 号（辣椒油）；10 §1.5 |
| `monascorubrin` | 红曲红素 | — | 1% | 5% | 10% | 50% | 10 §1.5 |
| `betanin` | 甜菜苷 | — | 1% | 5% | 10% | 50% | 10 §1.5 |
| `curcumin` | 姜黄素 | 3% | — | — | — | — | 07 第 61 号（姜黄粉） |
| `indigo` | 靛青 | 2% | — | — | — | — | 07 第 84 号（靛泥） |
| `lecithin` | 磷脂（卵磷脂） | 30% | — | — | — | — | 07 第 69 号（蛋黄卵磷脂） |
| `fish_liver_oil` | 鱼肝油 | 90% | — | — | — | — | 07 第 107 号 |
| `resin` | 松脂 | 70% | — | — | — | — | 07 第 137 号 |
| `kelp_ash` | 海藻灰 | 8% | — | — | — | — | 07 第 5 号；machines/01 |
| `wood_ash` | 草木灰 | 6% | — | — | — | — | 07 第 7 号 |
| `malt` | 麦芽 | 45% | — | — | — | — | 07 第 96 号 |
| `acetobacter` | 醋酸菌（醋母） | 20% | — | — | — | — | 07 自然接种 |
| `lactobacillus` | 乳酸菌（老卤） | 20% | — | — | — | — | 07 自然接种 |
| `monascus` | 红曲霉 | 20% | — | — | — | — | 07 自然接种 |
| `rhizopus` | 米根霉 | 20% | — | — | — | — | 07 自然接种 |
| `aspergillus_oryzae` | 米曲霉 | 20% | — | — | — | — | 07 自然接种 |
