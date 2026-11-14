# 19 第五批：合成香料（表 B.3）

表 B.3 合成香料 1,504 条中本批设计 1,460 条；总说明、路线模板见 [18](18-batch-5-flavors.md) 第 1 节，物料（ID、化学式、英文名）见 [09](09-chemical-formulas.md) 第 E.2 节。

- 按国标序号分 11 段（与设计分组一致）；序号 650 以前大体按官能团排列，之后为萜烯、含硫与含氮杂环和历年增补品种混排。
- 前体优先用表内其他香料（写编码），表外前体为新中间体（第 2 节，化学式并入 09 第 A、B 节）。
- 一行写了多个模板的路线如何写成配方，见第 6 节与 18 第 1.4 节。
- 汇总时为消除循环依赖改了 3 条路线：S0307（改克脑文格尔–林斯特德缩合）、S1431（改交叉酮醇缩合）、S1455（改维蒂希烯化，新增中间体 `hydroxybutanal`）；原路线互为前体。

## 1 路线表

### 1.1 序号 1–100（S0003–S0100，89 条，醇、酚为主）

| 编码 | 名称 | 物料 ID | 阶段 | 模板 | 原料 | 关键反应 |
| --- | --- | --- | --- | --- | --- | --- |
| S0003 | 异丙醇 | `isopropanol` | — | ST26 | 见 434 | — |
| S0004 | 正丁醇 | `n_butanol` | — | ST26 | 见 431 | — |
| S0005 | 异丁醇 | `isobutanol` | T2 / T3 | A ST13；B ST05 | A N402（杂醇油精馏）；B S0184 + `hydrogen` | B：C4H8O + H2 → C4H10O |
| S0006 | 正戊醇 | `n_pentanol` | T3 | ST05 | S0123 + `hydrogen` | C5H10O + H2 → C5H12O |
| S0007 | 2-戊醇 | `pentan_2_ol` | T3 | ST16 | `methyl_chloride` + `magnesium` + S0119 | CH3Cl + Mg + C4H8O + H2O → C5H12O + Mg(OH)Cl |
| S0009 | 1-戊烯-3-醇 | `pent_1_en_3_ol` | T3 | ST16 | `vinyl_chloride` + `magnesium` + S0117 | C2H3Cl + Mg + C3H6O + H2O → C5H10O + Mg(OH)Cl |
| S0010 | 正己醇 | `n_hexanol` | T3 | ST40 | `hexanoic_acid` + `hydrogen` | C6H12O2 + 2 H2 → C6H14O + H2O |
| S0011 | 2-己烯-1-醇 | `trans_2_hexenol` | T3 | ST31 | S0131 + S0003 | C6H10O + C3H8O → C6H12O + C3H6O |
| S0012 | 4-己烯-1-醇 | `hex_4_en_1_ol` | T3 | ST43 | `chloro_methyl_tetrahydropyran` + `sodium` | C6H11ClO + 2 Na → C6H11NaO + NaCl；C6H11NaO + H2O → C6H12O + NaOH |
| S0013 | 正庚醇 | `n_heptanol` | T3 | ST05 | S0136 + `hydrogen` | C7H14O + H2 → C7H16O |
| S0014 | 正辛醇 | `n_octanol` | T3 | ST40 | `caprylic_acid` + `hydrogen` | C8H16O2 + 2 H2 → C8H18O + H2O |
| S0015 | 2-辛醇 | `octan_2_ol` | T3 | ST38 | `castor_oil` + `sodium_hydroxide`（M23 高温碱裂解，2-辛醇馏出；釜残 `disodium_sebacate` 作副产出，2026-11-12） | 以蓖麻油酸计：C18H34O3 + 2 NaOH → C8H18O + C10H16Na2O4 + H2；C10H16Na2O4 + H2SO4 → C10H18O4 + Na2SO4 |
| S0016 | 1-辛烯-3-醇 | `oct_1_en_3_ol` | T3 | ST16 | `vinyl_chloride` + `magnesium` + S0130 | C2H3Cl + Mg + C6H12O + H2O → C8H16O + Mg(OH)Cl |
| S0017 | 顺式-5-辛烯-1-醇 | `cis_5_octenol` | T3 | ST16 | `cis_3_hexenyl_chloride` + `magnesium` + `ethylene_oxide` | C6H11Cl + Mg + C2H4O + H2O → C8H16O + Mg(OH)Cl |
| S0018 | 正壬醇 | `n_nonanol` | T3 | ST05 | S0145 + `hydrogen`（`catalysts/copper`） | C9H18O + H2 → C9H20O |
| S0019 | 顺式-6-壬烯-1-醇 | `cis_6_nonenol` | T3 | ST16 | `cis_5_octenyl_chloride` + `magnesium` + `formaldehyde` | C8H15Cl + Mg + CH2O + H2O → C9H18O + Mg(OH)Cl |
| S0020 | 反式-2-壬烯-1-醇 | `trans_2_nonenol` | T3 | ST31 | S0147 + S0003 | C9H16O + C3H8O → C9H18O + C3H6O |
| S0021 | 2,6-壬二烯-1-醇 | `trans_2_cis_6_nonadienol` | T3 | ST16 + ST02 | `cis_3_hexenyl_chloride` + `magnesium` + `acrolein` + `acetic_anhydride` + `sodium_hydroxide` | C6H11Cl + Mg + C3H4O + H2O → C9H16O + Mg(OH)Cl；乙酸酐烯丙位重排：C9H16O + C4H6O3 → C11H18O2 + C2H4O2；C11H18O2 + NaOH → C9H16O + C2H3NaO2 |
| S0022 | 正癸醇 | `n_decanol` | T3 | ST40 | `capric_acid` + `hydrogen` | C10H20O2 + 2 H2 → C10H22O + H2O |
| S0023 | 十一醇 | `n_undecanol` | T3 | ST40 | S0336 + `hydrogen`（`catalysts/copper`，高压，羧基还原为羟甲基） | C11H20O2 + 3 H2 → C11H24O + H2O |
| S0024 | 月桂醇（十二醇） | `lauryl_alcohol` | — | ST26 | 已有：`lauryl_alcohol` | — |
| S0025 | 1-十六醇 | `cetyl_alcohol` | T3 | ST40 | `palmitic_acid` + `hydrogen` | C16H32O2 + 2 H2 → C16H34O + H2O |
| S0026 | 小茴香醇 | `fenchol` | T3 | A ST14；B ST05 | A S0658 + `acetic_acid` + `sodium_hydroxide`；B S0274 + `hydrogen` | A：C10H16 + C2H4O2 → C12H20O2；C12H20O2 + NaOH → C10H18O + C2H3NaO2；B：C10H16O + H2 → C10H18O |
| S0027 | 叶醇 | `leaf_alcohol` | T3 | ST41 | `acetylene` + `ethyl_chloride` + `sodium` + `ethylene_oxide` + `hydrogen` | C2H2 + C2H5Cl + Na → C4H6 + NaCl + 1/2 H2；C4H6 + C2H4O → C6H10O；C6H10O + H2 → C6H12O |
| S0028 | 龙脑 | `borneol` | T2 / T3 | A ST13；B ST14 | A `materials/borneol_camphor_leaf`（NT1，升华精制）；B S0658 + `oxalic_acid` + `sodium_hydroxide` | B：2 C10H16 + C2H2O4 → C22H34O4；C22H34O4 + 2 NaOH → 2 C10H18O + C2Na2O4 |
| S0029 | 芳樟醇 | `linalool` | T2 / T3 | A ST13；B ST14 | A N047；B S0658 + `hydrogen` + `oxygen` | B：C10H16 + H2 → C10H18；C10H18 + O2 → C10H18O2；C10H18O2 + H2 → C10H18O + H2O；蒎醇热裂解：C10H18O → C10H18O |
| S0030 | 氧化芳樟醇 | `linalool_oxide` | T3 | ST24 | S0029 + `hydrogen_peroxide` + `acetic_acid` | C10H18O + H2O2 → C10H18O2 + H2O |
| S0031 | 异胡薄荷醇 | `isopulegol` | T3 | ST06 | S0173（`catalysts/zinc_chloride`） | C10H18O → C10H18O |
| S0032 | 苏合香醇 | `styralyl_alcohol` | T3 | ST05 | S0225 + `hydrogen`（`catalysts/copper`，只还原羰基） | C8H8O + H2 → C8H10O |
| S0033 | 苯甲醇 | `benzyl_alcohol` | T3 | ST05 | 127 + `hydrogen` | C7H6O + H2 → C7H8O |
| S0034 | 苯乙醇 | `phenethyl_alcohol` | T3 / T4 | A ST15；B ST18 | A `benzene` + `ethylene_oxide`（`catalysts/aluminium_chloride`）；B `l_phenylalanine` + `cultures/yeast` | A：C6H6 + C2H4O → C8H10O |
| S0035 | 苯丙醇 | `phenylpropyl_alcohol` | T3 | ST05 | 126 + `hydrogen` | C9H8O + 2 H2 → C9H12O |
| S0036 | 玫瑰醇 | `rhodinol` | T2 | ST13 | N097 | — |
| S0037 | α-松油醇 | `alpha_terpineol` | T3 | ST14 | S0658 + `water`（`sulfuric_acid` 催化） | C10H16 + 3 H2O → C10H20O2·H2O；C10H20O2·H2O → C10H18O + 2 H2O |
| S0038 | 金合欢醇 | `farnesol` | T3 | ST14 | S0047 + `acetic_anhydride` + `sodium_hydroxide` | C15H26O + C4H6O3 → C17H28O2 + C2H4O2；C17H28O2 + NaOH → C15H26O + C2H3NaO2 |
| S0039 | 香叶醇 | `geraniol` | T2 / T3 | A ST13；B ST14 | A N189；B S0653 + `hydrogen_chloride` + `sodium_acetate` + `sodium_hydroxide` | B：C10H16 + HCl → C10H17Cl；C10H17Cl + C2H3NaO2 → C12H20O2 + NaCl；C12H20O2 + NaOH → C10H18O + C2H3NaO2 |
| S0040 | dl-香茅醇 | `dl_citronellol` | T3 | ST05 | S0039 + `hydrogen`（`catalysts/copper`，选择加氢） | C10H18O + H2 → C10H20O |
| S0041 | 茴香醇 | `anisyl_alcohol` | T3 | ST05 | S0163 + `hydrogen` | C8H8O2 + H2 → C8H10O2 |
| S0042 | 肉桂醇 | `cinnamyl_alcohol` | T3 | ST31 | 126 + S0003 | C9H8O + C3H8O → C9H10O + C3H6O |
| S0043 | α-紫罗兰醇（甲位紫罗兰醇） | `alpha_ionol` | T3 | ST31 | S0234 + S0003 | C13H20O + C3H8O → C13H22O + C3H6O |
| S0044 | β-紫罗兰醇（乙位紫罗兰醇） | `beta_ionol` | T3 | ST31 | S0235 + S0003 | C13H20O + C3H8O → C13H22O + C3H6O |
| S0045 | 二氢-β-紫罗兰醇 | `dihydro_beta_ionol` | T3 | ST05 | S0284 + `hydrogen` | C13H22O + H2 → C13H24O |
| S0046 | 橙花醇 | `nerol` | T3 | ST14 | S0653 + `hydrogen_chloride` + `sodium_acetate` + `sodium_hydroxide`（与 S0039 B 同线，精馏分出） | C10H16 + HCl → C10H17Cl；C10H17Cl + C2H3NaO2 → C12H20O2 + NaCl；C12H20O2 + NaOH → C10H18O + C2H3NaO2 |
| S0047 | 橙花叔醇 | `nerolidol` | T3 | ST16 | `vinyl_chloride` + `magnesium` + S0261 | C2H3Cl + Mg + C13H22O + H2O → C15H26O + Mg(OH)Cl |
| S0048 | 二甲基苄基原醇 | `dimethylbenzylcarbinol` | T3 | ST16 | `benzyl_chloride` + `magnesium` + `acetone` | C7H7Cl + Mg + C3H6O + H2O → C10H14O + Mg(OH)Cl |
| S0050 | 3-己醇 | `hexan_3_ol` | T3 | ST16 | `ethyl_chloride` + `magnesium` + S0119 | C2H5Cl + Mg + C4H8O + H2O → C6H14O + Mg(OH)Cl |
| S0051 | 1-己烯-3-醇 | `hex_1_en_3_ol` | T3 | ST16 | `vinyl_chloride` + `magnesium` + S0119 | C2H3Cl + Mg + C4H8O + H2O → C6H12O + Mg(OH)Cl |
| S0052 | 2-乙基己醇 | `ethylhexanol` | T3 | ST06 + ST05 | S0119 + `hydrogen` | 2 C4H8O → C8H14O + H2O；C8H14O + 2 H2 → C8H18O |
| S0053 | 2-庚醇 | `heptan_2_ol` | T3 | ST16 | `methyl_chloride` + `magnesium` + S0130 | CH3Cl + Mg + C6H12O + H2O → C7H16O + Mg(OH)Cl |
| S0054 | 3-辛醇 | `octan_3_ol` | T3 | ST16 | `ethyl_chloride` + `magnesium` + S0130 | C2H5Cl + Mg + C6H12O + H2O → C8H18O + Mg(OH)Cl |
| S0055 | 顺式-3-辛烯-1-醇 | `cis_3_octenol` | T3 | ST41 | `acetylene` + `butyl_chloride` + `sodium` + `ethylene_oxide` + `hydrogen` | C2H2 + C4H9Cl + Na → C6H10 + NaCl + 1/2 H2；C6H10 + C2H4O → C8H14O；C8H14O + H2 → C8H16O |
| S0056 | 2-十一醇 | `undecan_2_ol` | T3 | ST05 | S0221 + `hydrogen` | C11H22O + H2 → C11H24O |
| S0057 | 对,α-二甲基苄醇 | `p_alpha_dimethylbenzyl_alcohol` | T3 | ST05 | S0226 + `hydrogen` | C9H10O + H2 → C9H12O |
| S0058 | 对-异丙基苄醇 | `p_isopropylbenzyl_alcohol` | T3 | ST05 | S0171 + `hydrogen` | C10H12O + H2 → C10H14O |
| S0059 | 对,α,α-三甲基苄醇 | `p_cymen_8_ol` | T3 | ST16 | `methyl_chloride` + `magnesium` + S0226 | CH3Cl + Mg + C9H10O + H2O → C10H14O + Mg(OH)Cl |
| S0060 | β-石竹烯醇 | `caryophyllene_alcohol` | T3 | ST14 | S0651 + `water`（`sulfuric_acid`，冷却） | C15H24 + H2O → C15H26O |
| S0061 | 龙蒿脑 | `estragole` | T2 | ST13 | N164 | — |
| S0062 | 四氢香叶醇 | `tetrahydrogeraniol` | T3 | ST05 | S0039 + `hydrogen`（`catalysts/nickel`，高温，碳碳双键一并饱和） | C10H18O + 2 H2 → C10H22O |
| S0063 | 二氢香芹醇 | `dihydrocarveol` | T3 | ST05 | S0281 + `hydrogen` | C10H16O + H2 → C10H18O |
| S0064 | 1-对-䓝烯-4-醇 | `terpinen_4_ol` | T2 | ST13 | N261 | — |
| S0065 | 紫苏醇 | `perillyl_alcohol` | T3 | ST24 + ST14 | S0659 + `hydrogen_peroxide` | C10H16 + H2O2 → C10H16O + H2O；环氧化物开环重排：C10H16O → C10H16O |
| S0067 | 3-(l-薄荷烷氧基)-2-甲基-1,2-丙二醇 | `menthoxy_methylpropanediol` | T3 | ST09 + ST24 | 29 + `methallyl_chloride` + `sodium_hydroxide` + `hydrogen_peroxide` | C10H20O + C4H7Cl + NaOH → C14H26O + NaCl + H2O；C14H26O + H2O2 → C14H26O2 + H2O；C14H26O2 + H2O → C14H28O3 |
| S0068 | 3,5,5-三甲基环己醇 | `trimethylcyclohexanol` | T3 | ST05 | S0267 + `hydrogen` | C9H14O + 2 H2 → C9H18O |
| S0069 | 顺式-2-壬烯-1-醇 | `cis_2_nonenol` | T3 | ST41 | `acetylene` + `hexyl_chloride` + `sodium` + `formaldehyde` + `hydrogen` | C2H2 + C6H13Cl + Na → C8H14 + NaCl + 1/2 H2；C8H14 + CH2O → C9H16O；C9H16O + H2 → C9H18O |
| S0070 | 反式,反式-2,4-癸二烯醇 | `trans_trans_2_4_decadienol` | T3 | ST31 | S0154 + S0003 | C10H16O + C3H8O → C10H18O + C3H6O |
| S0071 | 反式-2-辛烯-4-醇 | `trans_2_octen_4_ol` | T3 | ST16 | `butyl_chloride` + `magnesium` + `crotonaldehyde` | C4H9Cl + Mg + C4H6O + H2O → C8H16O + Mg(OH)Cl |
| S0072 | 对-䓝-3-烯-1-醇 | `terpinen_1_ol` | T3 | ST14 | S0680 + `water`（`sulfuric_acid` 催化） | C10H16 + H2O → C10H18O |
| S0073 | 对-䓝-1,8(10)二烯-9-醇 | `menthadienol` | T3 | ST14 | `limonene` + `sodium_hypochlorite` + `sodium_acetate` + `sodium_hydroxide` | C10H16 + HClO → C10H15Cl + H2O；C10H15Cl + C2H3NaO2 → C12H18O2 + NaCl；C12H18O2 + NaOH → C10H16O + C2H3NaO2 |
| S0074 | 柏木烯醇 | `cedrenol` | T2 | ST13 | `cedarwood_oil`（M22 精馏分出，与 N321、`cedrol` 同一精馏的联产） | — |
| S0075 | 脱氢芳樟醇 | `hotrienol` | T4 | ST42 | S0029 + `oxygen` + `sodium_sulfite` | C10H18O + O2 → C10H18O3；C10H18O3 + Na2SO3 → C10H18O2 + Na2SO4；C10H18O2 → C10H16O + H2O |
| S0077 | d-核糖 | `d_ribose` | T4 | ST18 | `glucose` + `cultures/bacillus_subtilis_ribose`（转酮醇酶缺陷株） | C6H12O6 + O2 → C5H10O5 + CO2 + H2O |
| S0078 | l-鼠李糖 | `l_rhamnose` | T2 | ST21 | `materials/sophora_flower` + `sulfuric_acid` | 芦丁酸水解：C27H30O16 + 2 H2O → C15H10O7 + C6H12O6 + C6H12O5 |
| S0079 | 二苯醚 | `diphenyl_ether` | — | ST26 | 已有：339 | — |
| S0080 | 对-甲酚甲醚 | `p_cresyl_methyl_ether` | T3 | ST09 | `p_cresol` + `methyl_chloride` + `sodium_hydroxide` | C7H8O + CH3Cl + NaOH → C8H10O + NaCl + H2O |
| S0081 | 异丁香酚甲醚 | `isoeugenyl_methyl_ether` | T3 | ST09 | S0092 + `methyl_chloride` + `sodium_hydroxide` | C10H12O2 + CH3Cl + NaOH → C11H14O2 + NaCl + H2O |
| S0082 | 甲基苯乙醚 | `methyl_phenethyl_ether` | T3 | ST09 | S0034 + `methyl_chloride` + `sodium_hydroxide` | C8H10O + CH3Cl + NaOH → C9H12O + NaCl + H2O |
| S0083 | 朗姆醚 | `rum_ether` | T3 | ST04 | `ethanol` + `wood_vinegar` + `manganese_dioxide` + `sulfuric_acid` | 乙醇氧化：C2H6O + MnO2 + H2SO4 → C2H4O + MnSO4 + 2 H2O；其余为乙醛、乙酸与乙醇的缩合、酯化产物 |
| S0084 | 仲丁基乙醚 | `sec_butyl_ethyl_ether` | T3 | ST09 | `sec_butanol` + `ethyl_chloride` + `sodium_hydroxide` | C4H10O + C2H5Cl + NaOH → C6H14O + NaCl + H2O |
| S0085 | 乙基苄基醚 | `ethyl_benzyl_ether` | T3 | ST09 | `benzyl_chloride` + `ethanol` + `sodium_hydroxide` | C7H7Cl + C2H6O + NaOH → C9H12O + NaCl + H2O |
| S0086 | 大茴香醚 | `anisole` | T3 | ST09 | `phenol` + `methyl_chloride` + `sodium_hydroxide` | C6H6O + CH3Cl + NaOH → C7H8O + NaCl + H2O |
| S0087 | 邻-甲基大茴香醚 | `o_methylanisole` | T3 | ST09 | S0095 + `methyl_chloride` + `sodium_hydroxide` | C7H8O + CH3Cl + NaOH → C8H10O + NaCl + H2O |
| S0088 | 橙花醚 | `nerol_oxide` | T4 | ST42 | S0046 + `oxygen` + `sodium_sulfite` | C10H18O + O2 → C10H18O3；C10H18O3 + Na2SO3 → C10H18O2 + Na2SO4；C10H18O2 → C10H16O + H2O |
| S0089 | 2,4-二甲基大茴香醚 | `dimethylanisole_2_4` | T3 | ST09 | `xylenol_2_4` + `methyl_chloride` + `sodium_hydroxide` | C8H10O + CH3Cl + NaOH → C9H12O + NaCl + H2O |
| S0090 | 香兰基乙醚 | `vanillyl_ethyl_ether` | T3 | ST09 | S1481 + `ethanol`（`catalysts/ion_exchange_resin`，酸催化脱水） | C8H10O3 + C2H6O → C10H14O3 + H2O |
| S0091 | 丁香酚 | `eugenol` | T2 | ST13 | N001 + `sodium_hydroxide`（碱溶分出酚，再酸化） | — |
| S0092 | 异丁香酚 | `isoeugenol` | T3 | ST30 | S0091 + `potassium_hydroxide` | C10H12O2 → C10H12O2 |
| S0093 | 甲基丁香酚 | `methyl_eugenol` | T3 | ST09 | S0091 + `methyl_chloride` + `sodium_hydroxide` | C10H12O2 + CH3Cl + NaOH → C11H14O2 + NaCl + H2O |
| S0095 | 邻-甲酚 | `o_cresol` | T2 / T3 | A ST13；B ST15 | A `coal_tar`（甲酚馏分精馏）；B `phenol` + `methanol`（M27 气相，`catalysts/alumina`） | B：C6H6O + CH4O → C7H8O + H2O |
| S0100 | 2-甲氧基-4-甲基苯酚 | `creosol` | T2 / T3 | A ST13；B ST05 | A `wood_tar`（精馏）；B 30 + `hydrogen`（`catalysts/palladium`，氢解） | B：C8H8O3 + 2 H2 → C8H10O2 + H2O |

### 1.2 序号 101–225（S0101–S0225，116 条，醛、缩醛为主）

| 编码 | 名称 | 物料 ID | 阶段 | 模板 | 原料 | 关键反应 |
| --- | --- | --- | --- | --- | --- | --- |
| S0101 | 对-乙基苯酚 | `p_ethylphenol` | T3（`sodium_ethylbenzenesulfonate` 为 T4） | ST47 | `sodium_ethylbenzenesulfonate` + `sodium_hydroxide`（M20 碱熔，P70）→ `sodium_p_ethylphenolate`；M14 水溶、`hydrochloric_acid` 酸化（两条配方，2026-11-12，11 问题 42） | 前段：C8H10 + H2SO4 → C8H10O3S + H2O；C8H10O3S + NaOH → C8H9NaO3S + H2O；C8H9NaO3S + 2 NaOH → C8H9NaO + Na2SO3 + H2O；C8H9NaO + HCl → C8H10O + NaCl |
| S0102 | 2-甲氧基-4-乙烯基苯酚 | `vinylguaiacol` | T2 | ST45 | `ferulic_acid` | C10H10O4 → C9H10O2 + CO2 |
| S0103 | 对-二甲氧基苯 | `p_dimethoxybenzene` | T3 | ST09 | `hydroquinone` + `methyl_chloride` + `sodium_hydroxide` | C6H6O2 + 2 CH3Cl + 2 NaOH → C8H10O2 + 2 NaCl + 2 H2O |
| S0105 | 4-乙基愈疮木酚 | `ethylguaiacol` | T3 | ST05 | S0102 + `hydrogen` | C9H10O2 + H2 → C9H12O2 |
| S0106 | 苯甲醛丙二醇缩醛 | `benzaldehyde_propylene_glycol_acetal` | T2 | ST07 | 127 + `propylene_glycol` | C7H6O + C3H8O2 → C10H12O2 + H2O |
| S0107 | 2-异丙基苯酚 | `o_isopropylphenol` | T3 | ST15 | `phenol` + `propene` | C6H6O + C3H6 → C9H12O |
| S0108 | 2,6-二甲基苯酚 | `xylenol_2_6` | T3 | ST44 | `phenol` + `methanol` | C6H6O + 2 CH4O → C8H10O + 2 H2O |
| S0109 | 2,6-二甲氧基苯酚 | `syringol` | T3（`pyrogallol` 为 T4） | ST09 | `pyrogallol` + `methyl_chloride` + `sodium_hydroxide` | C6H6O3 + 2 CH3Cl + 2 NaOH → C8H10O3 + 2 NaCl + 2 H2O |
| S0110 | 间苯二酚 | `resorcinol` | — | ST26 | 已有：`resorcinol` | — |
| S0111 | 香芹酚 | `carvacrol` | T2 / T3 | A ST13；B ST15 | A N154；B S0095 + `propene` | B：C7H8O + C3H6 → C10H14O |
| S0112 | 2-甲氧基-4-丙基苯酚 | `propylguaiacol` | T3 | ST05 | S0091 + `hydrogen` | C10H12O2 + H2 → C10H14O2 |
| S0113 | 2,5-二甲基苯酚 | `xylenol_2_5` | T3 | ST44 | `m_cresol` + `methanol` | C7H8O + CH4O → C8H10O + H2O |
| S0114 | 对-乙烯基苯酚 | `p_vinylphenol` | T3（S0101 为 T4） | ST04 | S0101 | C8H10O → C8H8O + H2 |
| S0116 | 乙醛二乙缩醛 | `acetaldehyde_diethyl_acetal` | T2 | ST07 | `acetaldehyde` + `ethanol` | C2H4O + 2 C2H6O → C6H14O2 + H2O |
| S0117 | 丙醛 | `propionaldehyde` | T3 | ST19 | `ethylene` + `carbon_monoxide` + `hydrogen` | C2H4 + CO + H2 → C3H6O |
| S0118 | 3-(2-呋喃基)丙烯醛 | `furylacrolein` | T3 | ST06 | `furfural` + `acetaldehyde` | C5H4O2 + C2H4O → C7H6O2 + H2O |
| S0119 | 丁醛 | `butyraldehyde` | T3 | ST19 | `propene` + `carbon_monoxide` + `hydrogen` | C3H6 + CO + H2 → C4H8O |
| S0120 | 2-甲基丁醛 | `methylbutyraldehyde` | T3 | ST19 | `butene` + `carbon_monoxide` + `hydrogen`（S0123 的支链联产物，M22 分出） | C4H8 + CO + H2 → C5H10O |
| S0121 | 2-甲基-2-丁烯醛 | `tiglaldehyde` | T3 | ST06 | S0117 + `acetaldehyde` | C3H6O + C2H4O → C5H8O + H2O |
| S0122 | 2-苯基-2-丁烯醛 | `phenylbutenal` | T3 | ST06 | S0168 + `acetaldehyde` | C8H8O + C2H4O → C10H10O + H2O |
| S0123 | 戊醛 | `valeraldehyde` | T3 | ST19 | `butene` + `carbon_monoxide` + `hydrogen` | C4H8 + CO + H2 → C5H10O |
| S0124 | 异戊醛 | `isovaleraldehyde` | T3 | ST19 | `isobutylene` + `carbon_monoxide` + `hydrogen` | C4H8 + CO + H2 → C5H10O |
| S0125 | 2-甲基戊醛 | `methylvaleraldehyde` | T3 | ST05 | S0127 + `hydrogen` | C6H10O + H2 → C6H12O |
| S0126 | 2-戊烯醛 | `trans_2_pentenal` | T3 | ST06 | S0117 + `acetaldehyde`（与 S0121 同釜，M22 分出） | C3H6O + C2H4O → C5H8O + H2O |
| S0127 | 2-甲基-2-戊烯醛 | `methylpentenal` | T3 | ST06 | S0117 | 2 C3H6O → C6H10O + H2O |
| S0128 | 4-甲基-2-苯基-2-戊烯醛 | `methylphenylpentenal` | T3 | ST06 | S0168 + S0184 | C8H8O + C4H8O → C12H14O + H2O |
| S0129 | 2,4-戊二烯醛 | `pentadienal` | T3 | ST06 | `crotonaldehyde` + `formaldehyde` | C4H6O + CH2O → C5H6O + H2O |
| S0130 | 己醛 | `hexanal` | T3 | ST05 | S0131 + `hydrogen` | C6H10O + H2 → C6H12O |
| S0131 | 2-己烯醛（叶醛） | `trans_2_hexenal` | T3 | ST06 | S0119 + `acetaldehyde` | C4H8O + C2H4O → C6H10O + H2O |
| S0132 | 顺式-3-己烯醛 | `cis_3_hexenal` | T3 | ST04 | S0027 + `sodium_hypochlorite` | C6H12O + NaClO → C6H10O + NaCl + H2O |
| S0133 | 5-甲基-2-苯基-2-己烯醛 | `methylphenylhexenal` | T3 | ST06 | S0168 + S0124 | C8H8O + C5H10O → C13H16O + H2O |
| S0134 | 2-异丙基-5-甲基-2-己烯醛 | `isopropylmethylhexenal` | T3 | ST06 | S0124 | 2 C5H10O → C10H18O + H2O |
| S0135 | 反式,反式-2,4-己二烯醛 | `trans_trans_2_4_hexadienal` | T3 | ST06 | `crotonaldehyde` + `acetaldehyde` | C4H6O + C2H4O → C6H8O + H2O |
| S0136 | 庚醛 | `heptanal` | T3 | ST39 | `ricinoleic_acid`（联产 S0336） | C18H34O3 → C7H14O + C11H20O2 |
| S0137 | 4-庚烯醛 | `cis_4_heptenal` | T3 | ST16 | `cis_3_hexenyl_chloride` + `dimethylformamide` | C6H11Cl + Mg → C6H11ClMg；C6H11ClMg + C3H7NO + H2O → C7H12O + C2H7N + ClHMgO |
| S0138 | 反式-2-庚烯醛 | `trans_2_heptenal` | T3 | ST06 | S0123 + `acetaldehyde` | C5H10O + C2H4O → C7H12O + H2O |
| S0139 | 2,6-二甲基-5-庚烯醛 | `dimethylheptenal` | T3 | ST06 | S0218 + `ethyl_chloroacetate` + `sodium_methoxide` | C8H14O + C4H7ClO2 + CH3NaO → C12H20O3 + NaCl + CH4O；C12H20O3 + H2O → C9H16O + CO2 + C2H6O |
| S0140 | 2,4-庚二烯醛 | `trans_trans_2_4_heptadienal` | T3 | ST06 | S0126 + `acetaldehyde` | C5H8O + C2H4O → C7H10O + H2O |
| S0141 | 辛醛 | `octanal` | T3 | ST04 | S0014 | C8H18O → C8H16O + H2 |
| S0142 | 2-辛烯醛 | `trans_2_octenal` | T3 | ST06 | S0130 + `acetaldehyde` | C6H12O + C2H4O → C8H14O + H2O |
| S0143 | 反式,反式-2,4-辛二烯醛 | `trans_trans_2_4_octadienal` | T3 | ST06 | S0131 + `acetaldehyde` | C6H10O + C2H4O → C8H12O + H2O |
| S0144 | 反式,反式-2,6-辛二烯醛 | `trans_trans_2_6_octadienal` | T3 | ST06 | S1341 + `acetaldehyde` | C6H10O + C2H4O → C8H12O + H2O |
| S0145 | 壬醛 | `nonanal` | T3 | ST19 | `octene` + `carbon_monoxide` + `hydrogen` | C8H16 + CO + H2 → C9H18O |
| S0146 | 甲基壬乙醛 | `methylundecanal` | T3 | ST06 | S0221 + `ethyl_chloroacetate` + `sodium_methoxide` | C11H22O + C4H7ClO2 + CH3NaO → C15H28O3 + NaCl + CH4O；C15H28O3 + H2O → C12H24O + CO2 + C2H6O |
| S0147 | 2-壬烯醛 | `trans_2_nonenal` | T3 | ST06 | S0136 + `acetaldehyde` | C7H14O + C2H4O → C9H16O + H2O |
| S0148 | 顺式-6-壬烯醛 | `cis_6_nonenal` | T3 | ST04 | S0019 + `sodium_hypochlorite` | C9H18O + NaClO → C9H16O + NaCl + H2O |
| S0149 | 反式-2-反式-4-壬二烯醛 | `trans_trans_2_4_nonadienal` | T3 | ST06 | S0138 + `acetaldehyde` | C7H12O + C2H4O → C9H14O + H2O |
| S0150 | 反式-2-顺式-6-壬二烯醛 | `trans_2_cis_6_nonadienal` | T3 | ST04 | S0021 + `sodium_hypochlorite` | C9H16O + NaClO → C9H14O + NaCl + H2O |
| S0151 | 甲酸桃金娘烯酯 | `myrtenyl_formate` | T2（S0840 为 T3） | ST01 | S0840 + `formic_acid` | C10H16O + CH2O2 → C11H16O2 + H2O |
| S0152 | 正癸醛（癸醛） | `decanal` | T3 | ST04 | S0022 | C10H22O → C10H20O + H2 |
| S0153 | 2-癸烯醛 | `trans_2_decenal` | T3 | ST06 | S0141 + `acetaldehyde` | C8H16O + C2H4O → C10H18O + H2O |
| S0154 | 2,4-癸二烯醛 | `trans_trans_2_4_decadienal` | T3 | ST06 | S0142 + `acetaldehyde` | C8H14O + C2H4O → C10H16O + H2O |
| S0155 | 十一醛 | `undecanal` | T3 | ST19 | `decene` + `carbon_monoxide` + `hydrogen` | C10H20 + CO + H2 → C11H22O |
| S0156 | 2-十一烯醛 | `trans_2_undecenal` | T3 | ST06 | S0145 + `acetaldehyde` | C9H18O + C2H4O → C11H20O + H2O |
| S0157 | 2,4-十一碳二烯醛 | `trans_trans_2_4_undecadienal` | T3 | ST06 | S0147 + `acetaldehyde` | C9H16O + C2H4O → C11H18O + H2O |
| S0158 | 月桂醛 | `dodecanal` | T3 | ST04 | `lauryl_alcohol` | C12H26O → C12H24O + H2 |
| S0159 | 2-十二碳烯醛 | `trans_2_dodecenal` | T3 | ST06 | S0152 + `acetaldehyde` | C10H20O + C2H4O → C12H22O + H2O |
| S0160 | 反式-2-顺式-6-十二碳二烯醛 | `trans_2_cis_6_dodecadienal` | T3 | ST06 | S0187 + `acetaldehyde` | C10H18O + C2H4O → C12H20O + H2O |
| S0161 | 十四醛 | `tetradecanal` | T3 | ST04 | `myristyl_alcohol` | C14H30O → C14H28O + H2 |
| S0162 | 桃醛 | `gamma_undecalactone` | T3 | ST08 | S0014 + `acrylic_acid` | C8H18O + C3H4O2 → C11H20O2 + H2O |
| S0163 | 大茴香醛 | `p_anisaldehyde` | T3 | ST04 | S0080 + `manganese_dioxide` + `sulfuric_acid` | C8H10O + 2 MnO2 + 2 H2SO4 → C8H8O2 + 2 MnSO4 + 3 H2O |
| S0164 | 水杨醛 | `salicylaldehyde` | T3 | ST06 | `phenol` + `formaldehyde`（`magnesium_chloride`，邻位甲酰化） | C6H6O + 2 CH2O → C7H6O2 + CH4O |
| S0166 | 甲基苯甲醛(邻、对、间位混合物) | `tolualdehydes` | T3 | ST15 | `toluene` + `carbon_monoxide`（`catalysts/aluminium_chloride` + `hydrogen_chloride`） | C7H8 + CO → C8H8O |
| S0167 | 3,4-二甲氧基苯甲醛 | `veratraldehyde` | T3 | ST09 | 30 + `methyl_chloride` + `sodium_hydroxide` | C8H8O3 + CH3Cl + NaOH → C9H10O3 + NaCl + H2O |
| S0168 | 苯乙醛 | `phenylacetaldehyde` | T3 | ST04 | S0034（`catalysts/silver`） | C8H10O → C8H8O + H2 |
| S0169 | 苯乙醛二甲缩醛 | `phenylacetaldehyde_dimethyl_acetal` | T2（S0168 为 T3） | ST07 | S0168 + `methanol` | C8H8O + 2 CH4O → C10H14O2 + H2O |
| S0170 | 苯丙醛 | `hydrocinnamaldehyde` | T3 | ST05 | 126 + `hydrogen`（`catalysts/palladium`） | C9H8O + H2 → C9H10O |
| S0171 | 枯茗醛 | `cuminaldehyde` | T2 / T3 | A ST13；B ST15 | A N085；B `cumene` + `carbon_monoxide` | B：C9H12 + CO → C10H12O |
| S0173 | 香茅醛 | `citronellal` | T2 / T3 | A ST13；B ST05 | A N190；B 125 + `hydrogen` | B：C10H16O + H2 → C10H18O |
| S0175 | 洋茉莉醛（胡椒醛） | `piperonal` | T3 | ST04 | `methylenedioxymandelic_acid` + `oxygen`（同 30 号 C 路线后段） | 2 C9H8O5 + O2 → 2 C8H6O3 + 2 CO2 + 2 H2O |
| S0177 | 乙二醇缩肉桂醛 | `cinnamaldehyde_ethylene_glycol_acetal` | T2 | ST07 | 126 + `ethylene_glycol` | C9H8O + C2H6O2 → C11H12O2 + H2O |
| S0178 | 紫苏醛 | `perillaldehyde` | T2 / T3 | A ST13；B ST04 | A N268；B S0065 | B：C10H16O → C10H14O + H2 |
| S0179 | 对-䓝-1-烯-9-醛 | `p_menth_1_en_9_al` | T3 | ST24 | `limonene` + `hydrogen_peroxide`（8,9-环氧物经 `catalysts/zinc_chloride` 重排） | C10H16 + H2O2 → C10H16O + H2O |
| S0181 | 5-甲基糠醛 | `methylfurfural` | T2 | ST46 | `sucrose` + `stannous_chloride` + `hydrochloric_acid` | C12H22O11 + H2O → 2 C6H12O6；C6H12O6 + SnCl2 + 2 HCl → C6H6O2 + SnCl4 + 4 H2O |
| S0182 | 1,1-二甲氧基乙烷 | `acetaldehyde_dimethyl_acetal` | T2 | ST07 | `acetaldehyde` + `methanol` | C2H4O + 2 CH4O → C4H10O2 + H2O |
| S0183 | 2,6,6-三甲基环己-1,3-二烯基甲醛 | `safranal` | T3 | ST04 | S0715 | C10H16O → C10H14O + H2 |
| S0184 | 异丁醛 | `isobutyraldehyde` | T3 | ST19 | `propene` + `carbon_monoxide` + `hydrogen`（S0119 的支链联产物，M22 分出） | C3H6 + CO + H2 → C4H8O |
| S0185 | 顺式-4-己烯醛 | `cis_4_hexenal` | T3 | ST04 | S0012 + `sodium_hypochlorite` | C6H12O + NaClO → C6H10O + NaCl + H2O |
| S0186 | 顺式-5-辛烯醛 | `cis_5_octenal` | T3 | ST04 | S0017 + `sodium_hypochlorite` | C8H16O + NaClO → C8H14O + NaCl + H2O |
| S0187 | 4-癸烯醛 | `cis_4_decenal` | T3 | ST04 | S1455 + `sodium_hydroxide` + `sodium_hypochlorite`（先皂化得顺式-4-癸烯醇） | C12H22O2 + NaOH → C10H20O + C2H3NaO2；C10H20O + NaClO → C10H18O + NaCl + H2O |
| S0188 | 反式,反式-2,4-十二碳二烯醛 | `trans_trans_2_4_dodecadienal` | T3 | ST06 | S0153 + `acetaldehyde` | C10H18O + C2H4O → C12H20O + H2O |
| S0189 | 2-十三烯醛 | `trans_2_tridecenal` | T3 | ST06 | S0155 + `acetaldehyde` | C11H22O + C2H4O → C13H24O + H2O |
| S0190 | 4-乙基苯甲醛 | `p_ethylbenzaldehyde` | T3 | ST15 | `ethylbenzene` + `carbon_monoxide`（`catalysts/aluminium_chloride` + `hydrogen_chloride`） | C8H10 + CO → C9H10O |
| S0191 | 2-羟基-4-甲基苯甲醛 | `methylsalicylaldehyde` | T3 | ST06 | `m_cresol` + `formaldehyde`（`magnesium_chloride`，邻位甲酰化） | C7H8O + 2 CH2O → C8H8O2 + CH4O |
| S0192 | 邻-甲氧基肉桂醛 | `o_methoxycinnamaldehyde` | T3 | ST06 | S0201 + `acetaldehyde` | C8H8O2 + C2H4O → C10H10O2 + H2O |
| S0193 | 龙脑烯醛 | `campholenic_aldehyde` | T3 | ST24 | S0658 + `hydrogen_peroxide`（环氧蒎烷经 `catalysts/zinc_chloride` 重排） | C10H16 + H2O2 → C10H16O + H2O |
| S0194 | α-己基肉桂醛 | `hexylcinnamaldehyde` | T3 | ST06 | 127 + S0141 | C7H6O + C8H16O → C15H20O + H2O |
| S0195 | 香兰素1,2-丙二醇缩醛 | `vanillin_propylene_glycol_acetal` | T2 | ST07 | 30 + `propylene_glycol` | C8H8O3 + C3H8O2 → C11H14O4 + H2O |
| S0196 | 乙醛乙醇顺式-3-己烯醇缩醛 | `acetaldehyde_ethyl_hexenyl_acetal` | T2（S0027 为 T3） | ST07 | S0116 + S0027（缩醛交换） | C6H14O2 + C6H12O → C10H20O2 + C2H6O |
| S0197 | 反式,反式-2,6-壬二烯醛 | `trans_trans_2_6_nonadienal` | T3 | ST06 | `trans_4_heptenal` + `acetaldehyde` | C7H12O + C2H4O → C9H14O + H2O |
| S0198 | 2,4,7-癸三烯醛 | `decatrienal` | T3 | ST06 | S0132 + `acetaldehyde`（2 份） | C6H10O + 2 C2H4O → C10H14O + 2 H2O |
| S0199 | β-甜橙醛 | `beta_sinensal` | T2 | ST13 | N131 | — |
| S0200 | 4-羟基苯甲醛 | `p_hydroxybenzaldehyde` | T3 | ST04 | `p_cresol` + `oxygen`（`catalysts/cobalt`，碱性甲醇液） | C7H8O + O2 → C7H6O2 + H2O |
| S0201 | 邻-甲氧基苯甲醛 | `o_anisaldehyde` | T3 | ST09 | S0164 + `methyl_chloride` + `sodium_hydroxide` | C7H6O2 + CH3Cl + NaOH → C8H8O2 + NaCl + H2O |
| S0202 | 12-甲基十三醛 | `methyltridecanal` | T3 | ST05 | `methyltridecanoic_acid` + `hydrogen`（羧酸气相选择加氢成醛） | C14H28O2 + H2 → C14H28O + H2O |
| S0203 | 甲乙酮 | `methyl_ethyl_ketone` | T3 | ST04 | `sec_butanol` | C4H10O → C4H8O + H2 |
| S0204 | 3-羟基-2-丁酮（乙偶姻） | `acetoin` | T4 / T3 | A ST18；B ST05 | A `glucose`（`cultures/bacillus_subtilis_acetoin`）；B `diacetyl` + `hydrogen` | A：C6H12O6 + O2 → C4H8O2 + 2 CO2 + 2 H2O；B：C4H6O2 + H2 → C4H8O2 |
| S0205 | 4-(对-甲氧基苯基)-2-丁酮 | `anisylacetone` | T3 | ST05 | `anisylidene_acetone` + `hydrogen` | C11H12O2 + H2 → C11H14O2 |
| S0206 | 4-苯基-3-丁烯-2-酮 | `benzalacetone` | T3 | ST06 | 127 + `acetone` | C7H6O + C3H6O → C10H10O + H2O |
| S0208 | 2-戊酮 | `methyl_propyl_ketone` | T3 | ST05 | S0908 + `hydrogen` | C5H8O + H2 → C5H10O |
| S0209 | 1-戊烯-3-酮 | `ethyl_vinyl_ketone` | T3 | ST06 | S0203 + `formaldehyde`（仲胺盐催化，曼尼希后消除） | C4H8O + CH2O → C5H8O + H2O |
| S0210 | 2,3-戊二酮 | `acetylpropionyl` | T3 | ST04 | S0208 + `sodium_nitrite` + `hydrochloric_acid`（亚硝化后水解） | C5H10O + NaNO2 + HCl → C5H9NO2 + NaCl + H2O；C5H9NO2 + H2O → C5H8O2 + H3NO |
| S0211 | 3-乙基-2-羟基-2-环戊烯-1-酮 | `ethyl_cyclopentenolone` | T3 | ST06 | `diethyl_glutarate` + `diethyl_oxalate` + `ethyl_chloride` + `sodium_methoxide` | C9H16O4 + C6H10O4 → C11H14O6 + 2 C2H6O；C11H14O6 + C2H5Cl → C13H18O6 + HCl；C13H18O6 + 2 H2O → C7H10O2 + 2 CO2 + 2 C2H6O |
| S0213 | 4-己烯-3-酮 | `ethyl_propenyl_ketone` | T3 | ST06 | S0203 + `acetaldehyde` | C4H8O + C2H4O → C6H10O + H2O |
| S0214 | 5-甲基-3-己烯-2-酮 | `methylhexenone` | T3 | ST06 | S0184 + `acetone` | C4H8O + C3H6O → C7H12O + H2O |
| S0215 | 3,4-己二酮 | `dipropionyl` | T3 | ST04 | S1084 + `sodium_nitrite` + `hydrochloric_acid`（亚硝化后水解） | C6H12O + NaNO2 + HCl → C6H11NO2 + NaCl + H2O；C6H11NO2 + H2O → C6H10O2 + H3NO |
| S0216 | 2-庚酮 | `methyl_amyl_ketone` | T3 | ST05 | S0217 + `hydrogen` | C7H12O + H2 → C7H14O |
| S0217 | 3-庚烯-2-酮 | `methyl_pentenyl_ketone` | T3 | ST06 | S0119 + `acetone` | C4H8O + C3H6O → C7H12O + H2O |
| S0218 | 6-甲基-5-庚烯-2-酮 | `methylheptenone` | T3 | ST06 | 125 + `water`（碱催化逆羟醛裂解） | C10H16O + H2O → C8H14O + C2H4O |
| S0219 | 1-辛烯-3-酮 | `amyl_vinyl_ketone` | T3 | ST04 | S0016 + `sodium_hypochlorite` | C8H16O + NaClO → C8H14O + NaCl + H2O |
| S0220 | 2-壬酮 | `methyl_heptyl_ketone` | T3 | ST27 | `caprylic_acid` + `acetic_acid` | C8H16O2 + C2H4O2 → C9H18O + CO2 + H2O |
| S0221 | 2-十一酮 | `methyl_nonyl_ketone` | T2 / T3 | A ST13；B ST27 | A N295；B `capric_acid` + `acetic_acid` | B：C10H20O2 + C2H4O2 → C11H22O + CO2 + H2O |
| S0222 | 2-十三酮 | `methyl_undecyl_ketone` | T3 | ST27 | `lauric_acid` + `acetic_acid` | C12H24O2 + C2H4O2 → C13H26O + CO2 + H2O |
| S0223 | 圆柚酮 | `nootkatone` | T2 / T3 | A ST13；B ST04 | A N051；B S0652 + `oxygen`（`catalysts/cobalt`） | B：C15H24 + O2 → C15H22O + H2O |
| S0224 | l-香芹酮 | `l_carvone` | T2 / T3 | A ST13；B 同 133 B | A N122；B `limonene` + `nitrosyl_chloride`（加成成肟后碱性水解，2026-11-11 由 133 B 移来） | B：C10H16 + ClNO → C10H16ClNO；C10H16ClNO → C10H15NO + HCl；C10H15NO + H2O → C10H14O + H3NO |
| S0225 | 苯乙酮 | `acetophenone` | T3 | ST04 | `ethylbenzene` + `oxygen`（`catalysts/cobalt`，P44） | C8H10 + O2 → C8H8O + H2O |

### 1.3 序号 226–360（S0226–S0360，124 条，酮、酸为主）

| 编码 | 名称 | 物料 ID | 阶段 | 模板 | 原料 | 关键反应 |
| --- | --- | --- | --- | --- | --- | --- |
| S0226 | 4-甲基苯乙酮 对-甲基苯乙酮 | `p_methylacetophenone` | T3 | ST15 | `toluene` + `acetic_anhydride`（`catalysts/aluminium_chloride`） | C7H8 + C4H6O3 → C9H10O + C2H4O2 |
| S0227 | 对-甲氧基苯乙酮 | `p_methoxyacetophenone` | T3 | ST15 | S0086 + `acetic_anhydride`（`catalysts/aluminium_chloride`） | C7H8O + C4H6O3 → C9H10O2 + C2H4O2 |
| S0228 | 顺式茉莉酮 | `cis_jasmone` | T3 | ST06 | `undecenedione` + `sodium_hydroxide`（分子内羟醛缩合） | C11H18O2 → C11H16O + H2O |
| S0229 | 覆盆子酮（悬钩子酮） | `raspberry_ketone` | T3 | ST15 | `phenol` + `hydroxybutanone`（酸性白土或 `catalysts/ion_exchange_resin`） | C6H6O + C4H8O2 → C10H12O2 + H2O |
| S0230 | α-突厥酮 | `alpha_damascone` | T3 | ST16、ST04、ST30（同 S0931） | `alpha_cyclocitral` + `allyl_chloride` + `magnesium` + `oxygen`（格氏加成 → 氧化 → 双键移位成共轭） | C3H5Cl + Mg → C3H5ClMg；C10H16O + C3H5ClMg + HCl → C13H22O + MgCl2；2 C13H22O + O2 → 2 C13H20O + 2 H2O |
| S0231 | 突厥烯酮 | `damascenone` | T3 | ST24 | S0230 + `hydrogen_peroxide`（环内双键环氧化 → 碱开环成烯丙醇 → 酸催化脱水） | C13H20O + H2O2 → C13H20O2 + H2O；C13H20O2 → C13H18O + H2O |
| S0232 | 苯甲醛甘油缩醛 | `benzaldehyde_glyceryl_acetal` | T2 | ST07 | 127 + `glycerol` | C7H6O + C3H8O3 → C10H12O3 + H2O |
| S0233 | α-鸢尾酮 | `alpha_irone` | T2 / T3 | A ST13；B ST06 | A N058（亚硫酸氢盐加成分出羰基组分后精馏）；B `methyl_citral` + `acetone` → 假鸢尾酮 → 酸催化环化（硫酸得 α-反式为主；三氟化硼或氯磺酸得 α-顺式为主，只写试剂）→ 精馏 | B：C11H18O + C3H6O → C14H22O + H2O；C14H22O → C14H22O |
| S0234 | α-紫罗兰酮 | `alpha_ionone` | T3 | ST06 | `pseudoionone` + `phosphoric_acid`（酸催化环化） | 环化：C13H20O → C13H20O |
| S0235 | β-紫罗兰酮 | `beta_ionone` | T3 | ST06 | `pseudoionone` + `sulfuric_acid`（浓硫酸环化） | 环化：C13H20O → C13H20O |
| S0236 | dl-樟脑 | `dl_camphor` | T3 | ST04 | S0864（`catalysts/copper` 气相脱氢） | C10H18O → C10H16O + H2 |
| S0238 | d,l-异薄荷酮 | `isomenthone` | T3 | ST14 | `menthone`（酸或碱催化差向异构达平衡，M22 分出，余料回流） | 差向异构：C10H18O → C10H18O |
| S0239 | 4-(2-呋喃基)-3-丁烯-2-酮 | `furfurylidene_acetone` | T3 | ST06 | `furfural` + `acetone` | C5H4O2 + C3H6O → C8H8O2 + H2O |
| S0240 | 2-乙基-4-羟基-5-甲基-3(2H)-呋喃酮 | `homofuraneol` | T2 | ST12 | `xylose` + `l_alanine`（同 131，戊糖 + 丙氨酸） | C5H10O5 + C3H7NO2 → C7H10O3 + CO2 + NH3 + 2 H2O |
| S0241 | 4,5-二甲基-3-羟基-2,5-二氢呋喃-2-酮 | `sotolon` | T3 | ST06 | S0329 + `acetaldehyde`（羟醛缩合后内酯化） | C4H6O3 + C2H4O → C6H8O3 + H2O |
| S0242 | 2-乙基-3-甲基-4-羟基二氢-2,5-呋喃-5-酮 | `abhexon` | T3 | ST06 | S0329 + S0117（同 S0241，乙醛换丙醛） | C4H6O3 + C3H6O → C7H10O3 + H2O |
| S0243 | 4,5-二氢-3(2H)-噻吩酮(四氢噻吩-3-酮) | `tetrahydrothiophenone` | T3 | ST25、ST06 | `ethyl_thioglycolate` + S0523 + `sodium_methoxide`（硫醇迈克尔加成 → 狄克曼缩合 → 水解脱羧） | C4H8O2S + C5H8O2 → C9H16O4S；C9H16O4S → C7H10O3S + C2H6O；C7H10O3S + H2O → C4H6OS + CO2 + C2H6O |
| S0244 | 2-乙基呋喃 | `ethylfuran` | T3 | ST05 | S0245 + `hydrogen`（`catalysts/copper` 气相氢解，同糠醛制 2-甲基呋喃） | C6H6O2 + 2 H2 → C6H8O + H2O |
| S0245 | 2-乙酰基呋喃 | `acetylfuran` | T3 | ST15 | `furan` + `acetic_anhydride`（磷酸催化） | C4H4O + C4H6O3 → C6H6O2 + C2H4O2 |
| S0246 | 2-乙酰基-5-甲基呋喃 | `acetylmethylfuran` | T3 | ST15 | S0871 + `acetic_anhydride`（磷酸催化） | C5H6O + C4H6O3 → C7H8O2 + C2H4O2 |
| S0248 | 1-苯基-1,2-丙二酮 | `phenylpropanedione` | T3 | ST04 | `propiophenone` + `sodium_nitrite` + `hydrochloric_acid`（亚硝化后水解） | C9H10O + NaNO2 + HCl → C9H9NO2 + NaCl + H2O；C9H9NO2 + H2O → C9H8O2 + H3NO |
| S0249 | 3,4-二甲基-1,2-环戊二酮 | `dimethylcyclopentanedione_34` | T3 | ST06 | `diethyl_methylglutarate` + `diethyl_oxalate` + `methyl_chloride` + `sodium_methoxide`（克莱森–狄克曼缩合 → 甲基化 → 水解脱羧，同 F02 组 S0211） | C10H18O4 + C6H10O4 → C12H16O6 + 2 C2H6O；C12H16O6 + CH3Cl → C13H18O6 + HCl；C13H18O6 + 2 H2O → C7H10O2 + 2 CO2 + 2 C2H6O |
| S0250 | 3,5-二甲基-1,2-环戊二酮 | `dimethylcyclopentanedione_35` | T3 | ST06 | `diethyl_glutarate` + `diethyl_oxalate` + `methyl_chloride` + `sodium_methoxide`（同 S0249，两个活泼位各甲基化一次） | C9H16O4 + C6H10O4 → C11H14O6 + 2 C2H6O；C11H14O6 + 2 CH3Cl → C13H18O6 + 2 HCl；C13H18O6 + 2 H2O → C7H10O2 + 2 CO2 + 2 C2H6O |
| S0251 | 2,3-己二酮 | `acetyl_butyryl` | T3 | ST04 | S1084 + `sodium_nitrite` + `hydrochloric_acid`（亚硝化后水解，与 S0215 同釜，M22 分出） | C6H12O + NaNO2 + HCl → C6H11NO2 + NaCl + H2O；C6H11NO2 + H2O → C6H10O2 + H3NO |
| S0252 | 1-甲基-2,3-环己二酮 | `methylcyclohexanedione` | T3 | ST04 | `methylcyclohexanone` + `sodium_nitrite` + `hydrochloric_acid`（亚甲基一侧亚硝化后水解） | C7H12O + NaNO2 + HCl → C7H11NO2 + NaCl + H2O；C7H11NO2 + H2O → C7H10O2 + H3NO |
| S0253 | 2,2,6-三甲基环己酮 | `trimethylcyclohexanone` | T3 | ST05 | S0254 + `hydrogen`（气相部分加氢，氧化物催化剂） | C9H12O2 + 3 H2 → C9H16O + H2O |
| S0254 | 2,6,6-三甲基-2-环己烯-1,4-二酮 | `ketoisophorone` | T3 | ST04 | S0267 + `oxygen`（碱异构为 β-异弗尔酮后锰盐催化空气氧化，P44） | C9H14O + O2 → C9H12O2 + H2O |
| S0255 | 3-庚酮 | `ethyl_butyl_ketone` | T3 | ST27 | `propionic_acid` + S0300（`manganese_dioxide`） | C3H6O2 + C5H10O2 → C7H14O + CO2 + H2O |
| S0256 | 5-甲基-2-庚烯-4-酮 | `filbertone` | T3 | ST16、ST04 | `sec_butyl_chloride` + `magnesium` + `crotonaldehyde` + `oxygen`（格氏 1,2-加成 → 烯丙醇氧化） | C4H9Cl + Mg → C4H9ClMg；C4H6O + C4H9ClMg + HCl → C8H16O + MgCl2；2 C8H16O + O2 → 2 C8H14O + 2 H2O |
| S0257 | 6-甲基-3,5-庚二烯-2-酮 | `methylheptadienone` | T3 | ST06 | S1042 + `acetone` | C5H8O + C3H6O → C8H12O + H2O |
| S0258 | 2-辛酮 | `methyl_hexyl_ketone` | T3 | ST04 | S0015（`catalysts/copper` 气相脱氢） | C8H18O → C8H16O + H2 |
| S0259 | 3-辛酮 | `ethyl_amyl_ketone` | T3 | ST27 | `propionic_acid` + `hexanoic_acid`（`manganese_dioxide`） | C3H6O2 + C6H12O2 → C8H16O + CO2 + H2O |
| S0260 | 3-辛烯-2-酮 | `methyl_hexenyl_ketone` | T3 | ST06 | S0123 + `acetone` | C5H10O + C3H6O → C8H14O + H2O |
| S0261 | 6,10-二甲基-5,9-十一碳二烯-2-酮 | `geranylacetone` | T3 | ST58 | S0029 + S0365 | C10H18O + C6H10O3 → C13H22O + C2H6O + CO2 |
| S0262 | 2-十五酮 | `pentadecanone` | T3 | ST27 | S0313 + `acetic_acid`（`manganese_dioxide`） | C14H28O2 + C2H4O2 → C15H30O + CO2 + H2O |
| S0263 | 3-甲基环十五酮 | `muscone` | T3 | ST06、ST05 | `hexadecanedione`（高度稀释分子内羟醛缩合）+ `hydrogen`（`catalysts/palladium`） | C16H30O2 → C16H28O + H2O；C16H28O + H2 → C16H30O |
| S0264 | 环十七-9-烯-1-酮 | `civetone` | T3 | ST06 | `diethyl_octadecenedioate` + `sodium_methoxide`（高度稀释狄克曼缩合 → 水解脱羧，M22 分出顺式体） | C22H40O4 → C20H34O3 + C2H6O；C20H34O3 + H2O → C17H30O + CO2 + C2H6O |
| S0265 | 二苯甲酮 | `benzophenone` | T3 | ST15 | `benzene` + `benzoyl_chloride`（`catalysts/aluminium_chloride`） | C6H6 + C7H5ClO → C13H10O + HCl |
| S0266 | 2-羟基苯乙酮 | `o_hydroxyacetophenone` | T3 | ST15 | `phenyl_acetate` + `aluminium_chloride`（弗里斯重排，邻位体 M08 水蒸气蒸馏分出，对位体留釜） | 重排：C8H8O2 → C8H8O2 |
| S0267 | 异弗尔酮 | `isophorone` | T3 | ST06 | `acetone`（`potassium_hydroxide` 碱缩合，与 S0869 同一配方的联产：三聚物为本品） | 3 C3H6O → C9H14O + 2 H2O |
| S0268 | 二氢茉莉酮 | `dihydrojasmone` | T3 | ST06 | `undecanedione` + `sodium_hydroxide`（分子内羟醛缩合） | C11H20O2 → C11H18O + H2O |
| S0269 | 新甲基橙皮苷二氢查耳酮 | `neohesperidin_dhc` | T3 | ST05 | `neohesperidin` + `potassium_hydroxide` + `hydrogen`（碱开环成查耳酮后加氢，`catalysts/nickel`） | C28H34O15 + H2 → C28H36O15 |
| S0270 | 姜油酮 | `zingerone` | T3 | ST05 | `dehydrozingerone` + `hydrogen`（`catalysts/palladium`） | C11H12O3 + H2 → C11H14O3 |
| S0271 | β-突厥酮 | `beta_damascone` | T3 | ST16、ST04、ST30 | S0715（β 体）+ `allyl_chloride` + `magnesium` + `oxygen`（同 S0230） | C3H5Cl + Mg → C3H5ClMg；C10H16O + C3H5ClMg + HCl → C13H22O + MgCl2；2 C13H22O + O2 → 2 C13H20O + 2 H2O |
| S0272 | 3-甲硫基丁醛 | `methylthiobutanal` | T3 | ST25 | `crotonaldehyde` + S0686（同甲硫基丙醛的丙烯醛法） | C4H6O + CH4S → C5H10OS |
| S0273 | α-戊基肉桂醛 | `amylcinnamaldehyde` | T3 | ST06 | 127 + S0136 | C7H6O + C7H14O → C14H18O + H2O |
| S0274 | d-葑酮 | `d_fenchone` | T2 | ST13 | N181 | — |
| S0275 | 2-甲基四氢呋喃-3-酮 | `methyltetrahydrofuranone` | T3 | ST25、ST06 | S0498 + S0523 + `sodium_methoxide`（氧杂迈克尔加成 → 狄克曼缩合 → 水解脱羧） | C5H10O3 + C5H8O2 → C10H18O5；C10H18O5 → C8H12O4 + C2H6O；C8H12O4 + H2O → C5H8O2 + CO2 + C2H6O |
| S0277 | 2,5-二甲基-4-甲氧基-3(2H)-呋喃酮 | `mesifurane` | T3 | ST09 | 131 + `methyl_chloride` + `sodium_hydroxide` | C6H8O3 + CH3Cl + NaOH → C7H10O3 + NaCl + H2O |
| S0278 | 2-戊基呋喃 | `pentylfuran` | T3 | ST05 | `valerylfuran` + `hydrogen`（`catalysts/copper` 气相氢解） | C9H12O2 + 2 H2 → C9H14O + H2O |
| S0279 | 4,5,6,7-四氢-3,6-二甲基苯并呋喃（薄荷呋喃） | `menthofuran` | T2 | ST13 | N137 | — |
| S0280 | 1,5,5,9-四甲基-13-氧杂三环[8.3.0.0(4,9)]十三烷 | `ambroxide` | T3（`ambradiol` 为 T4） | ST09 | `ambradiol` + `sulfuric_acid`（酸催化分子内脱水成醚） | C16H30O2 → C16H28O + H2O |
| S0281 | 顺式-二氢香芹酮 | `cis_dihydrocarvone` | T3 | ST05 | 133 + `hydrogen`（选择加氢环内共轭双键，`catalysts/palladium`） | C10H14O + H2 → C10H16O |
| S0282 | 3-巯基-2-丁酮 | `mercaptobutanone` | T3 | ST10 | `chlorobutanone` + S1010 + `sodium_hydroxide`（硫氢化钠就地生成） | C4H7ClO + H2S + NaOH → C4H8OS + NaCl + H2O |
| S0283 | 胡椒基丙酮 | `piperonylacetone` | T3 | ST05 | `piperonylideneacetone` + `hydrogen`（`catalysts/palladium`） | C11H10O3 + H2 → C11H12O3 |
| S0284 | 二氢-β-紫罗兰酮 | `dihydro_beta_ionone` | T3 | ST05 | S0235 + `hydrogen`（选择加氢侧链双键） | C13H20O + H2 → C13H22O |
| S0285 | 4-甲基-2,3-戊二酮 | `acetyl_isobutyryl` | T3 | ST04 | S0977 + `sodium_nitrite` + `hydrochloric_acid`（亚硝化后水解） | C6H12O + NaNO2 + HCl → C6H11NO2 + NaCl + H2O；C6H11NO2 + H2O → C6H10O2 + H3NO |
| S0286 | 反式-7-甲基-3-辛烯-2-酮 | `methyloctenone` | T3 | ST06 | `methylpentanal` + `acetone` | C6H12O + C3H6O → C9H16O + H2O |
| S0287 | 3-乙酰硫基-2-甲基呋喃 | `acetylthio_methylfuran` | T3（S0471 为 T4） | ST10 | S0471 + `acetic_anhydride`（硫酯化） | C5H6OS + C4H6O3 → C7H8O2S + C2H4O2 |
| S0288 | 4-乙酰氧基-2,5-二甲基-3(2H)-呋喃酮 | `furaneol_acetate` | T3 | ST02 | 131 + `acetic_anhydride` | C6H8O3 + C4H6O3 → C8H10O4 + C2H4O2 |
| S0289 | 3-乙基-2-羟基-4-甲基-2-环戊烯-1-酮 | `ethyl_methyl_cyclopentenolone` | T3 | ST06 | `diethyl_methylglutarate` + `diethyl_oxalate` + `ethyl_chloride` + `sodium_methoxide`（同 S0249，甲基化改乙基化） | C10H18O4 + C6H10O4 → C12H16O6 + 2 C2H6O；C12H16O6 + C2H5Cl → C14H20O6 + HCl；C14H20O6 + 2 H2O → C8H12O2 + 2 CO2 + 2 C2H6O |
| S0290 | 环己酮 | `cyclohexanone` | T3 | ST04 | `cyclohexanol`（`catalysts/copper` 气相脱氢） | C6H12O → C6H10O + H2 |
| S0291 | 2,3-庚二酮 | `acetyl_valeryl` | T3 | ST04 | S0216 + `sodium_nitrite` + `hydrochloric_acid`（亚硝化后水解） | C7H14O + NaNO2 + HCl → C7H13NO2 + NaCl + H2O；C7H13NO2 + H2O → C7H12O2 + H3NO |
| S0292 | 2,3-辛二酮 | `acetyl_hexanoyl` | T3 | ST04 | S0258 + `sodium_nitrite` + `hydrochloric_acid`（亚硝化后水解） | C8H16O + NaNO2 + HCl → C8H15NO2 + NaCl + H2O；C8H15NO2 + H2O → C8H14O2 + H3NO |
| S0295 | 丙酮酸 | `pyruvic_acid` | T3 | ST30 | 54 + `potassium_bisulfate`（加热脱水脱羧，同 G4 组中间体 `pyruvic_acid`） | C4H6O6 → C3H4O3 + CO2 + H2O |
| S0297 | 异丁酸 | `isobutyric_acid` | — | ST26 | 已有：`isobutyric_acid` | — |
| S0298 | 2-甲基丁酸 | `methylbutyric_acid` | T3 | ST04 | S0120 + `oxygen` | 2 C5H10O + O2 → 2 C5H10O2 |
| S0299 | 2-乙基丁酸 | `ethylbutyric_acid` | T3 | ST04 | S0919 + `oxygen` | 2 C6H12O + O2 → 2 C6H12O2 |
| S0300 | 戊酸 | `valeric_acid` | T3 | ST04 | S0123 + `oxygen` | 2 C5H10O + O2 → 2 C5H10O2 |
| S0301 | 2-甲基戊酸 | `alpha_methylvaleric_acid` | T3 | ST04 | S0125 + `oxygen` | 2 C6H12O + O2 → 2 C6H12O2 |
| S0302 | 2-甲基-2-戊烯酸（草莓酸） | `methylpentenoic_acid` | T3 | ST04 | S0127 + `oxygen`（`catalysts/silver`，保留双键） | 2 C6H10O + O2 → 2 C6H10O2 |
| S0303 | 异戊酸 | `isovaleric_acid` | T3 | ST04 | S0124 + `oxygen` | 2 C5H10O + O2 → 2 C5H10O2 |
| S0304 | 己酸 | `hexanoic_acid` | — | ST26 | 已有：`hexanoic_acid` | — |
| S0305 | 己二酸 | `adipic_acid` | — | ST26 | 已有：144（`adipic_acid`） | — |
| S0306 | 反式-2-己烯酸 | `trans_2_hexenoic_acid` | T3 | ST04 | S0131 + `oxygen`（`catalysts/silver`，保留双键） | 2 C6H10O + O2 → 2 C6H10O2 |
| S0307 | 3-己烯酸 | `hex_3_enoic_acid` | T3 | ST06 | S0119 + `malonic_acid`（克脑文格尔–林斯特德缩合，弱碱催化，缩合同时脱羧，得反式-3-己烯酸） | C4H8O + C3H4O4 → C6H10O2 + CO2 + H2O |
| S0308 | 庚酸 | `heptanoic_acid` | T3 | ST04 | S0136 + `oxygen` | 2 C7H14O + O2 → 2 C7H14O2 |
| S0309 | 辛酸 | `caprylic_acid` | — | ST26 | 已有：`caprylic_acid` | — |
| S0310 | 壬酸 | `nonanoic_acid` | T3 | ST04 | S0145 + `oxygen` | 2 C9H18O + O2 → 2 C9H18O2 |
| S0311 | 癸酸 | `capric_acid` | — | ST26 | 已有：`capric_acid` | — |
| S0312 | 十二酸（月桂酸） | `lauric_acid` | — | ST26 | 见 458（已有：`lauric_acid`） | — |
| S0313 | 十四酸（肉豆蔻酸） | `myristic_acid` | T2 | ST13 | `coconut_fatty_acids`（M22 减压精馏切 C14 馏分，M15 冷却结晶） | — |
| S0314 | 十六酸（棕榈酸） | `palmitic_acid` | — | ST26 | 已有：`palmitic_acid` | — |
| S0316 | 苯乙酸 | `phenylacetic_acid` | T3 | ST19 | `benzyl_chloride` + `carbon_monoxide` + `sodium_hydroxide`（`catalysts/cobalt` 羰基化）→ `hydrochloric_acid` 酸化 | C7H7Cl + CO + 2 NaOH → C8H7NaO2 + NaCl + H2O；C8H7NaO2 + HCl → C8H8O2 + NaCl |
| S0318 | 肉桂酸 | `cinnamic_acid` | T3 | ST06 | 127 + `acetic_anhydride` + `sodium_acetate`（珀金反应） | C7H6O + C4H6O3 → C9H8O2 + C2H4O2 |
| S0320 | 3-甲基戊酸（酐酪酸） | `beta_methylvaleric_acid` | T3 | ST28 | S0403 + `sec_butyl_chloride` + `sodium_methoxide` | C7H12O4 + C4H9Cl + CH3NaO → C11H20O4 + NaCl + CH4O；C11H20O4 + 2 H2O → C6H12O2 + CO2 + 2 C2H6O |
| S0321 | β-丙氨酸 | `beta_alanine` | T3 | ST25 | `acrylic_acid` + `ammonia`（M23 加压氨加成） | C3H4O2 + NH3 → C3H7NO2 |
| S0322 | L-苯基丙氨酸 | `l_phenylalanine` | — | ST26 | 已有：`l_phenylalanine` | — |
| S0323 | L-半胱氨酸 | `l_cysteine` | — | ST26 | 已有：`l_cysteine`（盐酸盐见 181） | — |
| S0324 | 甘氨酸 | `glycine` | — | ST26 | 已有：180（`glycine`） | — |
| S0325 | L-谷氨酸 | `glutamic_acid` | — | ST26 | 已有：`glutamic_acid` | — |
| S0326 | L-亮氨酸 | `l_leucine` | T4 | ST18 | `cultures/corynebacterium_leucine` + `glucose` + `ammonia`（M29 通气发酵 → 离子交换 → 等电点结晶） | 5 C6H12O6 + 4 NH3 → 4 C6H13NO2 + 6 CO2 + 10 H2O |
| S0327 | DL-蛋氨酸 | `dl_methionine` | T3 | ST59 | S0700 + `hydrogen_cyanide` + `ammonium_carbonate` | C4H8OS + CHN + CH8N2O3 → C6H10N2O2S + NH3 + 2 H2O；C6H10N2O2S + 2 H2O → C5H11NO2S + CO2 + NH3 |
| S0328 | 乙酰丙酸 | `levulinic_acid` | T2 | ST46 | `glucose` + `sulfuric_acid`（副产 `formic_acid`） | C6H12O6 → C5H8O3 + CH2O2 + H2O |
| S0329 | 2-氧代丁酸 | `oxobutyric_acid` | T3 | ST06 | S0402 + `diethyl_oxalate` + `sodium_methoxide`（克莱森缩合 → 水解脱羧） | C5H10O2 + C6H10O4 → C9H14O5 + C2H6O；C9H14O5 + 2 H2O → C4H6O3 + CO2 + 2 C2H6O |
| S0330 | 2-甲基己酸 | `methylhexanoic_acid` | T3 | ST04 | `methylhexanol` + `oxygen`（醇 → 醛 → 酸） | C7H16O + O2 → C7H14O2 + H2O |
| S0331 | 2-甲基庚酸 | `methylheptanoic_acid` | T3 | ST04 | `methylheptanol` + `oxygen`（醇 → 醛 → 酸） | C8H18O + O2 → C8H16O2 + H2O |
| S0332 | 4-甲基辛酸 | `methyloctanoic_acid` | T3 | ST28 | S0403 + `methylhexyl_chloride` + `sodium_methoxide` | C7H12O4 + C7H15Cl + CH3NaO → C14H26O4 + NaCl + CH4O；C14H26O4 + 2 H2O → C9H18O2 + CO2 + 2 C2H6O |
| S0333 | 3,7-二甲基-6-辛烯酸 | `citronellic_acid` | T3 | ST04 | S0173 + `oxygen`（`catalysts/silver`，保留双键） | 2 C10H18O + O2 → 2 C10H18O2 |
| S0334 | 9-癸烯酸 | `decenoic_acid` | T4 | ST60 | S0509 + `ethylene`（乙烯解后皂化、酸化，副产 `decene`） | C20H38O2 + C2H4 → C12H22O2 + C10H20；C12H22O2 + H2O → C10H18O2 + C2H6O |
| S0335 | 十一酸 | `undecanoic_acid` | T3 | ST05 | S0336 + `hydrogen`（`catalysts/nickel`） | C11H20O2 + H2 → C11H22O2 |
| S0336 | 10-十一碳烯酸 | `undecylenic_acid` | T3 | ST39 | `ricinoleic_acid`（联产 S0136） | C18H34O3 → C7H14O + C11H20O2 |
| S0337 | 3-苯丙酸 | `hydrocinnamic_acid` | T3 | ST05 | S0318 + `hydrogen`（`catalysts/nickel`） | C9H8O2 + H2 → C9H10O2 |
| S0339 | L-脯氨酸 | `l_proline` | T4 | ST18 | `cultures/corynebacterium_proline` + `glucose` + `ammonia`（同 S0326） | 11 C6H12O6 + 12 NH3 → 12 C5H9NO2 + 6 CO2 + 30 H2O |
| S0340 | DL-缬氨酸 | `dl_valine` | T3 | ST59 | S0184 + `hydrogen_cyanide` + `ammonium_carbonate` | C4H8O + CHN + CH8N2O3 → C6H10N2O2 + NH3 + 2 H2O；C6H10N2O2 + 2 H2O → C5H11NO2 + CO2 + NH3 |
| S0341 | 2-(4-甲氧基苯氧基)-丙酸钠 | `lactisole` | T3 | ST09、ST17 | `p_methoxyphenol` + `chloropropionic_acid` + `sodium_hydroxide`（威廉森醚化，钠盐结晶） | C7H8O2 + C3H5ClO2 + 2 NaOH → C10H11NaO4 + NaCl + 2 H2O |
| S0342 | L-和DL-丙氨酸 | `l_alanine` | — | ST26 | 已有：179（`l_alanine`）；DL 体按表 B.3 注 3 视同已批准 | — |
| S0343 | L-精氨酸 | `l_arginine` | T4 | ST18 | `cultures/corynebacterium_arginine` + `glucose` + `ammonia` + `carbon_dioxide`（同 S0326） | 11 C6H12O6 + 48 NH3 + 6 CO2 → 12 C6H14N4O2 + 54 H2O |
| S0344 | L-赖氨酸 | `l_lysine` | T4 | ST18 | `cultures/corynebacterium_lysine` + `glucose` + `ammonium_sulfate`（同 S0326，氮源按 17 第 534 号用硫酸铵，现实赖氨酸发酵即此（2026-11-12，11 问题 44）；534 L-盐酸赖氨酸引用本项） | 7 C6H12O6 + 6 (NH4)2SO4 → 6 C6H14N2O2 + 6 CO2 + 6 H2SO4 + 18 H2O |
| S0345 | 3-甲基巴豆酸 | `methylcrotonic_acid` | T3 | ST04 | S1042 + `oxygen`（`catalysts/silver`，保留双键） | 2 C5H8O + O2 → 2 C5H8O2 |
| S0346 | 甲酸 | `formic_acid` | — | ST26 | 已有：`formic_acid` | — |
| S0347 | 4-甲基壬酸 | `methylnonanoic_acid` | T3 | ST28 | S0403 + `methylheptyl_chloride` + `sodium_methoxide` | C7H12O4 + C8H17Cl + CH3NaO → C15H28O4 + NaCl + CH4O；C15H28O4 + 2 H2O → C10H20O2 + CO2 + 2 C2H6O |
| S0348 | 异己酸 | `isocaproic_acid` | T3 | ST04 | `methylpentanal` + `oxygen` | 2 C6H12O + O2 → 2 C6H12O2 |
| S0349 | 2-羟基苯甲酸（水杨酸） | `salicylic_acid` | T3 | ST61 | `phenol` + `sodium_hydroxide` + `carbon_dioxide` + `sulfuric_acid`（同 335 羧化段，钾碱改钠碱得邻位） | C6H6O + NaOH + CO2 → C7H5NaO3 + H2O；2 C7H5NaO3 + H2SO4 → 2 C7H6O3 + Na2SO4 |
| S0350 | 惕各酸 | `tiglic_acid` | T3 | ST04 | S0121 + `oxygen`（`catalysts/silver`，保留双键） | 2 C5H8O + O2 → 2 C5H8O2 |
| S0351 | 琥珀酸 | `succinic_acid` | — | ST26 | 已有：`succinic_acid` | — |
| S0353 | 甲酸乙酯 | `ethyl_formate` | T2 | ST01 | `formic_acid` + `ethanol`（甲酸自催化，不加硫酸） | CH2O2 + C2H6O → C3H6O2 + H2O |
| S0354 | 甲酸丁酯 | `butyl_formate` | T2（S0004 为 T3） | ST01 | `formic_acid` + S0004 | CH2O2 + C4H10O → C5H10O2 + H2O |
| S0355 | 甲酸戊酯 | `amyl_formate` | T2（S0006 为 T3） | ST01 | `formic_acid` + S0006 | CH2O2 + C5H12O → C6H12O2 + H2O |
| S0356 | 甲酸异戊酯 | `isoamyl_formate` | T2 | ST01 | `formic_acid` + `isoamyl_alcohol` | CH2O2 + C5H12O → C6H12O2 + H2O |
| S0357 | 甲酸己酯 | `hexyl_formate` | T2（S0010 为 T3） | ST01 | `formic_acid` + S0010 | CH2O2 + C6H14O → C7H14O2 + H2O |
| S0358 | 甲酸苄酯 | `benzyl_formate` | T2（S0033 为 T3） | ST01 | `formic_acid` + S0033 | CH2O2 + C7H8O → C8H8O2 + H2O |
| S0359 | 甲酸香叶酯 | `geranyl_formate` | T2 | ST01 | `formic_acid` + S0039（不加强酸，低温，防异构） | CH2O2 + C10H18O → C11H18O2 + H2O |
| S0360 | 甲酸香茅酯 | `citronellyl_formate` | T2（S0040 为 T3） | ST01 | `formic_acid` + S0040 | CH2O2 + C10H20O → C11H20O2 + H2O |

### 1.4 序号 361–500（S0361–S0500，138 条，酯（一））

| 编码 | 名称 | 物料 ID | 阶段 | 模板 | 原料 | 关键反应 |
| --- | --- | --- | --- | --- | --- | --- |
| S0361 | 甲酸苯乙酯 | `phenethyl_formate` | T2（S0034 为 T3） | ST01 | S0346 + S0034 | CH2O2 + C8H10O → C9H10O2 + H2O |
| S0362 | 甲酸芳樟酯 | `linalyl_formate` | T3 | ST02 | S0029 + S0346 + `acetic_anhydride`（现制甲酸乙酸混合酐） | C10H18O + CH2O2 + C4H6O3 → C11H18O2 + 2 C2H4O2 |
| S0363 | 乙酸甲酯 | `methyl_acetate` | T2 | ST01 | S0293 + `methanol` | C2H4O2 + CH4O → C3H6O2 + H2O |
| S0364 | 乙酸乙酯 | `ethyl_acetate` | — | ST26 | 见 436（已有：`ethyl_acetate`） | — |
| S0365 | 乙酰乙酸乙酯 | `ethyl_acetoacetate` | T3 | A ST02；B ST06 | A `diketene` + `ethanol`；B S0364（乙醇钠催化克莱森缩合） | A：C4H4O2 + C2H6O → C6H10O3；B：2 C4H8O2 → C6H10O3 + C2H6O |
| S0366 | 乙酸丙酯 | `propyl_acetate` | T2 | ST01 | S0293 + S0049 | C2H4O2 + C3H8O → C5H10O2 + H2O |
| S0367 | 乙酸异丙酯 | `isopropyl_acetate` | T2 / T3 | A ST01；B ST25 | A S0293 + S0003；B `propene` + S0293（离子交换树脂催化加成） | A：C2H4O2 + C3H8O → C5H10O2 + H2O；B：C3H6 + C2H4O2 → C5H10O2 |
| S0368 | 乙酸烯丙酯 | `allyl_acetate` | T3 | ST48 | `propene` + S0293 + `oxygen` | 2 C3H6 + 2 C2H4O2 + O2 → 2 C5H8O2 + 2 H2O |
| S0369 | 乙酰丙酸乙酯 | `ethyl_levulinate` | T2 | ST01 | S0328 + `ethanol` | C5H8O3 + C2H6O → C7H12O3 + H2O |
| S0370 | 乙酸丁酯 | `butyl_acetate` | T2（S0004 为 T3） | ST01 | S0293 + S0004 | C2H4O2 + C4H10O → C6H12O2 + H2O |
| S0371 | 乙酸异丁酯 | `isobutyl_acetate` | T2 | ST01 | S0293 + S0005 | C2H4O2 + C4H10O → C6H12O2 + H2O |
| S0373 | 乙酸己酯 | `hexyl_acetate` | T2（S0010 为 T3） | ST01 | S0293 + S0010 | C2H4O2 + C6H14O → C8H16O2 + H2O |
| S0374 | 乙酸2-己烯酯 | `hex_2_enyl_acetate` | T3 | ST02 | S0011 + `acetic_anhydride` | C6H12O + C4H6O3 → C8H14O2 + C2H4O2 |
| S0375 | 乙酸庚酯 | `heptyl_acetate` | T2（S0013 为 T3） | ST01 | S0293 + S0013 | C2H4O2 + C7H16O → C9H18O2 + H2O |
| S0376 | 乙酸辛酯 | `octyl_acetate` | T2（S0014 为 T3） | ST01 | S0293 + S0014 | C2H4O2 + C8H18O → C10H20O2 + H2O |
| S0377 | 乙酸3-辛酯 | `octan_3_yl_acetate` | T2（S0054 为 T3） | ST01 | S0293 + S0054 | C2H4O2 + C8H18O → C10H20O2 + H2O |
| S0378 | 1-辛烯-3-醇乙酸酯 | `octen_3_yl_acetate` | T3 | ST02 | S0016 + `acetic_anhydride` | C8H16O + C4H6O3 → C10H18O2 + C2H4O2 |
| S0379 | 乙酸壬酯 | `nonyl_acetate` | T2（S0018 为 T3） | ST01 | S0293 + S0018 | C2H4O2 + C9H20O → C11H22O2 + H2O |
| S0380 | 2-丁烯酸己酯 | `hexyl_2_butenoate` | T2（S0010、S1019 为 T3） | ST01 | S1019 + S0010 | C4H6O2 + C6H14O → C10H18O2 + H2O |
| S0381 | 乙酸癸酯 | `decyl_acetate` | T2（S0022 为 T3） | ST01 | S0293 + S0022 | C2H4O2 + C10H22O → C12H24O2 + H2O |
| S0382 | 乙酸苄酯 | `benzyl_acetate` | T2（S0033 为 T3） | ST01 | S0293 + S0033 | C2H4O2 + C7H8O → C9H10O2 + H2O |
| S0383 | 乙酸苯乙酯 | `phenethyl_acetate` | T2（S0034 为 T3） | ST01 | S0293 + S0034 | C2H4O2 + C8H10O → C10H12O2 + H2O |
| S0384 | 乙酸茴香酯 | `anisyl_acetate` | T3 | ST02 | S0041 + `acetic_anhydride` | C8H10O2 + C4H6O3 → C10H12O3 + C2H4O2 |
| S0385 | 乙酸龙脑酯 | `bornyl_acetate` | T3 / T2 | A ST02；B ST13 | A S0028 + `acetic_anhydride`；B N186（松针油） | A：C10H18O + C4H6O3 → C12H20O2 + C2H4O2；B：— |
| S0386 | 乙酸薄荷酯 | `menthyl_acetate` | T3 | ST02 | S0066 + `acetic_anhydride` | C10H20O + C4H6O3 → C12H22O2 + C2H4O2 |
| S0387 | 乙酸肉桂酯 | `cinnamyl_acetate` | T3 | ST02 | S0042 + `acetic_anhydride` | C9H10O + C4H6O3 → C11H12O2 + C2H4O2 |
| S0388 | 乙酸香茅酯 | `citronellyl_acetate` | T3 | ST02 | S0040 + `acetic_anhydride` | C10H20O + C4H6O3 → C12H22O2 + C2H4O2 |
| S0389 | 乙酸香叶酯 | `geranyl_acetate` | T3 | ST02 | S0039 + `acetic_anhydride` | C10H18O + C4H6O3 → C12H20O2 + C2H4O2 |
| S0390 | 乙酸对-甲酚酯 | `p_cresyl_acetate` | T3 | ST02 | S0094 + `acetic_anhydride` | C7H8O + C4H6O3 → C9H10O2 + C2H4O2 |
| S0391 | 乙酸苏合香酯 | `styrallyl_acetate` | T3 | ST02 | S0032 + `acetic_anhydride` | C8H10O + C4H6O3 → C10H12O2 + C2H4O2 |
| S0392 | 乙酸橙花酯 | `neryl_acetate` | T3 | ST02 | S0046 + `acetic_anhydride` | C10H18O + C4H6O3 → C12H20O2 + C2H4O2 |
| S0393 | 乙酸松油酯 | `terpinyl_acetate` | T3 | A ST14；B ST02 | A S0658 + S0293（酸催化加成酯化，一步）；B S0037 + `acetic_anhydride` | A：C10H16 + C2H4O2 → C12H20O2；B：C10H18O + C4H6O3 → C12H20O2 + C2H4O2 |
| S0394 | 异丁酸肉桂酯 | `cinnamyl_isobutyrate` | T2（S0042 为 T3） | ST01 | S0297 + S0042 | C4H8O2 + C9H10O → C13H16O2 + H2O |
| S0395 | 顺式-3-己烯-1-醇乙酸酯（乙酸叶醇酯） | `cis_3_hexenyl_acetate` | T3 | ST02 | S0027 + `acetic_anhydride` | C6H12O + C4H6O3 → C8H14O2 + C2H4O2 |
| S0396 | 乙酸糠酯 | `furfuryl_acetate` | T3 | ST02 | S0795 + `acetic_anhydride` | C5H6O2 + C4H6O3 → C7H8O3 + C2H4O2 |
| S0397 | 庚酸烯丙酯 | `allyl_heptanoate` | T2（`allyl_alcohol`、S0308 为 T3） | ST01 | S0308 + `allyl_alcohol` | C7H14O2 + C3H6O → C10H18O2 + H2O |
| S0398 | 乙酸芳樟酯 | `linalyl_acetate` | T3 | ST02 | S0029 + `acetic_anhydride` | C10H18O + C4H6O3 → C12H20O2 + C2H4O2 |
| S0399 | 乙酸葛缕酯 | `carvyl_acetate` | T3 | ST02 | S0867 + `acetic_anhydride` | C10H16O + C4H6O3 → C12H18O2 + C2H4O2 |
| S0400 | 乙酸二氢葛缕酯 | `dihydrocarvyl_acetate` | T3 | ST02 | S0063 + `acetic_anhydride` | C10H18O + C4H6O3 → C12H20O2 + C2H4O2 |
| S0401 | 苯乙酸丁酯 | `butyl_phenylacetate` | T2（S0004、S0316 为 T3） | ST01 | S0316 + S0004 | C8H8O2 + C4H10O → C12H16O2 + H2O |
| S0402 | 丙酸乙酯 | `ethyl_propionate` | T2 | ST01 | S0294 + `ethanol` | C3H6O2 + C2H6O → C5H10O2 + H2O |
| S0403 | 丙二酸二乙酯 | `diethyl_malonate` | T2（`cyanoacetic_acid` 为 T3） | ST01 | `cyanoacetic_acid` + `ethanol` + `hydrogen_chloride`（腈醇解与酯化同釜） | C3H3NO2 + 2 C2H6O + HCl → C7H12O4 + NH4Cl |
| S0404 | 丙酸异丁酯 | `isobutyl_propionate` | T2 | ST01 | S0294 + S0005 | C3H6O2 + C4H10O → C7H14O2 + H2O |
| S0405 | 丙酸异戊酯 | `isoamyl_propionate` | T2 | ST01 | S0294 + S0008 | C3H6O2 + C5H12O → C8H16O2 + H2O |
| S0406 | 丙酸顺式-3-己烯酯和丙酸反式-2-己烯酯 | `hexenyl_propionate_mixture` | T2（S0011、S0027 为 T3） | ST01 | S0294 + S0027 + S0011（混合醇酯化，或两酯分别制得后 M21 调配） | C3H6O2 + C6H12O → C9H16O2 + H2O |
| S0407 | 丙酸香叶酯 | `geranyl_propionate` | T2 | ST01 | S0294 + S0039 | C3H6O2 + C10H18O → C13H22O2 + H2O |
| S0408 | 丙酸香茅酯 | `citronellyl_propionate` | T2（S0040 为 T3） | ST01 | S0294 + S0040 | C3H6O2 + C10H20O → C13H24O2 + H2O |
| S0409 | 丙酸苄酯 | `benzyl_propionate` | T2（S0033 为 T3） | ST01 | S0294 + S0033 | C3H6O2 + C7H8O → C10H12O2 + H2O |
| S0410 | 丙酸苯乙酯 | `phenethyl_propionate` | T2（S0034 为 T3） | ST01 | S0294 + S0034 | C3H6O2 + C8H10O → C11H14O2 + H2O |
| S0411 | 丙酸芳樟酯 | `linalyl_propionate` | T3 | ST02 | S0029 + `propionic_anhydride` | C10H18O + C6H10O3 → C13H22O2 + C3H6O2 |
| S0412 | 丁酸甲酯 | `methyl_butyrate` | T2 | ST01 | S0296 + `methanol` | C4H8O2 + CH4O → C5H10O2 + H2O |
| S0413 | 2-甲基丁酸甲酯 | `methyl_2_methylbutyrate` | T2（S0298 为 T3） | ST01 | S0298 + `methanol` | C5H10O2 + CH4O → C6H12O2 + H2O |
| S0415 | 异丁酸乙酯 | `ethyl_isobutyrate` | T2 | ST01 | S0297 + `ethanol` | C4H8O2 + C2H6O → C6H12O2 + H2O |
| S0416 | 2-甲基丁酸乙酯 | `ethyl_2_methylbutyrate` | T2（S0298 为 T3） | ST01 | S0298 + `ethanol` | C5H10O2 + C2H6O → C7H14O2 + H2O |
| S0417 | 3-羟基丁酸乙酯 | `ethyl_3_hydroxybutyrate` | T3 | ST05 | S0365 + `hydrogen`（`catalysts/nickel`） | C6H10O3 + H2 → C6H12O3 |
| S0418 | 丁二酸二乙酯 | `diethyl_succinate` | T2 | ST01 | S0351 + `ethanol` | C4H6O4 + 2 C2H6O → C8H14O4 + 2 H2O |
| S0419 | 异丁酸甲酯 | `methyl_isobutyrate` | T2 | ST01 | S0297 + `methanol` | C4H8O2 + CH4O → C5H10O2 + H2O |
| S0420 | 丁酸丁酯 | `butyl_butyrate` | T2（S0004 为 T3） | ST01 | S0296 + S0004 | C4H8O2 + C4H10O → C8H16O2 + H2O |
| S0421 | 丁酸异丁酯 | `isobutyl_butyrate` | T2 | ST01 | S0296 + S0005 | C4H8O2 + C4H10O → C8H16O2 + H2O |
| S0422 | 2-甲基丁酸丁酯 | `butyl_2_methylbutyrate` | T2（S0004、S0298 为 T3） | ST01 | S0298 + S0004 | C5H10O2 + C4H10O → C9H18O2 + H2O |
| S0423 | 2-甲基丁酸2-甲基丁酯 | `methylbutyl_methylbutyrate` | T2（S0298 为 T3） | ST01 | S0298 + S0863 | C5H10O2 + C5H12O → C10H20O2 + H2O |
| S0424 | 异丁酸丁酯 | `butyl_isobutyrate` | T2（S0004 为 T3） | ST01 | S0297 + S0004 | C4H8O2 + C4H10O → C8H16O2 + H2O |
| S0425 | 丁酸戊酯 | `amyl_butyrate` | T2（S0006 为 T3） | ST01 | S0296 + S0006 | C4H8O2 + C5H12O → C9H18O2 + H2O |
| S0426 | 丁酸异戊酯 | `isoamyl_butyrate` | T2 | ST01 | S0296 + S0008 | C4H8O2 + C5H12O → C9H18O2 + H2O |
| S0427 | 2-甲基丁酸异戊酯 | `isoamyl_2_methylbutanoate` | T2（S0298 为 T3） | ST01 | S0298 + S0008 | C5H10O2 + C5H12O → C10H20O2 + H2O |
| S0428 | 异丁酸异戊酯 | `isopentyl_isobutyrate` | T2 | ST01 | S0297 + S0008 | C4H8O2 + C5H12O → C9H18O2 + H2O |
| S0429 | 丁酸己酯 | `hexyl_butyrate` | T2（S0010 为 T3） | ST01 | S0296 + S0010 | C4H8O2 + C6H14O → C10H20O2 + H2O |
| S0430 | 2-甲基丁酸己酯 | `hexyl_2_methylbutyrate` | T2（S0010、S0298 为 T3） | ST01 | S0298 + S0010 | C5H10O2 + C6H14O → C11H22O2 + H2O |
| S0431 | 丁酸顺式-3-己烯酯（丁酸叶醇酯） | `cis_3_hexenyl_butyrate` | T2（S0027 为 T3） | ST01 | S0296 + S0027 | C4H8O2 + C6H12O → C10H18O2 + H2O |
| S0432 | 2-甲基丁酸-3-己烯酯 | `hex_3_enyl_2_methylbutanoate` | T2（S0027、S0298 为 T3） | ST01 | S0298 + S0027 | C5H10O2 + C6H12O → C11H20O2 + H2O |
| S0433 | 异丁酸庚酯 | `heptyl_isobutyrate` | T2（S0013 为 T3） | ST01 | S0297 + S0013 | C4H8O2 + C7H16O → C11H22O2 + H2O |
| S0434 | 2-甲基丁酸辛酯 | `octyl_2_methylbutyrate` | T2（S0014、S0298 为 T3） | ST01 | S0298 + S0014 | C5H10O2 + C8H18O → C13H26O2 + H2O |
| S0435 | 1-辛烯-3-醇丁酸酯 | `octen_3_yl_butyrate` | T3 | ST02 | S0016 + `butyric_anhydride` | C8H16O + C8H14O3 → C12H22O2 + C4H8O2 |
| S0436 | 丁酸苄酯 | `benzyl_butyrate` | T2（S0033 为 T3） | ST01 | S0296 + S0033 | C4H8O2 + C7H8O → C11H14O2 + H2O |
| S0437 | 异丁酸苄酯 | `benzyl_isobutyrate` | T2（S0033 为 T3） | ST01 | S0297 + S0033 | C4H8O2 + C7H8O → C11H14O2 + H2O |
| S0438 | 丁酸苯乙酯 | `phenethyl_butyrate` | T2（S0034 为 T3） | ST01 | S0296 + S0034 | C4H8O2 + C8H10O → C12H16O2 + H2O |
| S0439 | 2-甲基丁酸苯乙酯 | `phenethyl_2_methylbutyrate` | T2（S0034、S0298 为 T3） | ST01 | S0298 + S0034 | C5H10O2 + C8H10O → C13H18O2 + H2O |
| S0440 | 异丁酸苯乙酯 | `phenethyl_isobutyrate` | T2（S0034 为 T3） | ST01 | S0297 + S0034 | C4H8O2 + C8H10O → C12H16O2 + H2O |
| S0441 | 丁酸香叶酯 | `geranyl_butyrate` | T2 | ST01 | S0296 + S0039 | C4H8O2 + C10H18O → C14H24O2 + H2O |
| S0442 | 异丁酸香叶酯 | `geranyl_isobutyrate` | T2 | ST01 | S0297 + S0039 | C4H8O2 + C10H18O → C14H24O2 + H2O |
| S0443 | 丁酸芳樟酯 | `linalyl_butyrate` | T3 | ST02 | S0029 + `butyric_anhydride` | C10H18O + C8H14O3 → C14H24O2 + C4H8O2 |
| S0444 | 异丁酸芳樟酯 | `linalyl_isobutyrate` | T3 | ST02 | S0029 + `isobutyric_anhydride` | C10H18O + C8H14O3 → C14H24O2 + C4H8O2 |
| S0445 | 当归酸异丁酯 | `isobutyl_angelate` | T2 / T3 | A ST13；B ST30 | A N195（罗马春黄菊花油，当归酸异丁酯约占 14%）；B `isobutyl_tiglate` → M22 反应精馏：异构化与精馏同时进行，平衡时当归酸酯不超过约 10%，低沸点的当归酸异丁酯连续蒸出，惕各酸酯回流（现实用芳基亚磺酸催化，游戏中用 `catalysts/ion_exchange_resin` 代替，游戏简化） | B：C9H16O2 → C9H16O2 |
| S0446 | 异丁酸橙花酯 | `neryl_isobutyrate` | T2（S0046 为 T3） | ST01 | S0297 + S0046 | C4H8O2 + C10H18O → C14H24O2 + H2O |
| S0447 | 正戊酸乙酯 | `ethyl_valerate` | T2（S0300 为 T3） | ST01 | S0300 + `ethanol` | C5H10O2 + C2H6O → C7H14O2 + H2O |
| S0448 | 丁酰乳酸丁酯 | `butyl_butyryllactate` | T3 | ST02 | S0499 + `butyric_anhydride` | C7H14O3 + C8H14O3 → C11H20O4 + C4H8O2 |
| S0449 | 异戊酸乙酯 | `ethyl_isovalerate` | T2（S0303 为 T3） | ST01 | S0303 + `ethanol` | C5H10O2 + C2H6O → C7H14O2 + H2O |
| S0450 | 水杨酸丁酯（柳酸丁酯） | `butyl_salicylate` | T2（S0004、S0349 为 T3） | ST01 | S0349 + S0004 | C7H6O3 + C4H10O → C11H14O3 + H2O |
| S0451 | 异戊酸丁酯 | `butyl_isovalerate` | T2（S0004、S0303 为 T3） | ST01 | S0303 + S0004 | C5H10O2 + C4H10O → C9H18O2 + H2O |
| S0452 | 异戊酸异戊酯 | `isoamyl_isovalerate` | T2（S0303 为 T3） | ST01 | S0303 + S0008 | C5H10O2 + C5H12O → C10H20O2 + H2O |
| S0453 | 异戊酸3-己烯酯 | `hex_3_enyl_isovalerate` | T2（S0027、S0303 为 T3） | ST01 | S0303 + S0027 | C5H10O2 + C6H12O → C11H20O2 + H2O |
| S0454 | 异戊酸壬酯 | `nonyl_isovalerate` | T2（S0018、S0303 为 T3） | ST01 | S0303 + S0018 | C5H10O2 + C9H20O → C14H28O2 + H2O |
| S0455 | 异戊酸苯乙酯 | `phenethyl_isovalerate` | T2（S0034、S0303 为 T3） | ST01 | S0303 + S0034 | C5H10O2 + C8H10O → C13H18O2 + H2O |
| S0456 | 异戊酸香叶酯 | `geranyl_isovalerate` | T2（S0303 为 T3） | ST01 | S0303 + S0039 | C5H10O2 + C10H18O → C15H26O2 + H2O |
| S0457 | 己酸甲酯 | `methyl_hexanoate` | T2 | ST01 | S0304 + `methanol` | C6H12O2 + CH4O → C7H14O2 + H2O |
| S0458 | 2-己烯酸甲酯 | `methyl_2_hexenoate` | T2（S0306 为 T3） | ST01 | S0306 + `methanol` | C6H10O2 + CH4O → C7H12O2 + H2O |
| S0459 | 己酸乙酯 | `ethyl_hexanoate` | T2 | ST01 | S0304 + `ethanol` | C6H12O2 + C2H6O → C8H16O2 + H2O |
| S0460 | 3-己烯酸乙酯 | `ethyl_3_hexenoate` | T2（S0307 为 T3） | ST01 | S0307 + `ethanol` | C6H10O2 + C2H6O → C8H14O2 + H2O |
| S0461 | 3-羟基己酸乙酯 | `ethyl_3_hydroxyhexanoate` | T3 | ST16 | S0119 + `ethyl_bromoacetate` + `materials/zinc`（雷福尔马茨基反应） | C4H8O + C4H7BrO2 + Zn + HBr → C8H16O3 + ZnBr2 |
| S0462 | 反式-2-己烯酸乙酯 | `ethyl_trans_2_hexenoate` | T2（S0306 为 T3） | ST01 | S0306 + `ethanol` | C6H10O2 + C2H6O → C8H14O2 + H2O |
| S0463 | 己酸丙酯 | `propyl_hexanoate` | T2 | ST01 | S0304 + S0049 | C6H12O2 + C3H8O → C9H18O2 + H2O |
| S0464 | 己酸戊酯 | `amyl_hexanoate` | T2（S0006 为 T3） | ST01 | S0304 + S0006 | C6H12O2 + C5H12O → C11H22O2 + H2O |
| S0465 | 己酸异戊酯 | `isoamyl_hexanoate` | T2 | ST01 | S0304 + S0008 | C6H12O2 + C5H12O → C11H22O2 + H2O |
| S0466 | 己酸己酯 | `hexyl_hexanoate` | T2（S0010 为 T3） | ST01 | S0304 + S0010 | C6H12O2 + C6H14O → C12H24O2 + H2O |
| S0467 | 己酸顺式-3-己烯酯（己酸叶醇酯） | `cis_3_hexenyl_hexanoate` | T2（S0027 为 T3） | ST01 | S0304 + S0027 | C6H12O2 + C6H12O → C12H22O2 + H2O |
| S0468 | 庚酸乙酯 | `ethyl_heptanoate` | T2（S0308 为 T3） | ST01 | S0308 + `ethanol` | C7H14O2 + C2H6O → C9H18O2 + H2O |
| S0469 | 庚酸丙酯 | `propyl_heptanoate` | T2（S0308 为 T3） | ST01 | S0308 + S0049 | C7H14O2 + C3H8O → C10H20O2 + H2O |
| S0470 | 庚酸丁酯 | `butyl_heptanoate` | T2（S0004、S0308 为 T3） | ST01 | S0308 + S0004 | C7H14O2 + C4H10O → C11H22O2 + H2O |
| S0471 | 2-甲基-3-巯基呋喃 | `methyl_3_furanthiol` | T2（S0077 为 T4） | ST12 | S0077 + S0323（核糖–半胱氨酸美拉德反应，P75） | 核糖脱水成 4-羟基-5-甲基-3(2H)-呋喃酮（S0843）：C5H10O5 → C5H6O3 + 2 H2O；再与半胱氨酸降解释出的硫化氢经还原步骤生成 C5H6OS，无确定计量式 |
| S0472 | 辛酸甲酯 | `methyl_caprylate` | T2 / T3 | A ST01；B ST03 | A S0309 + `methanol`；B `coconut_oil` + `methanol`（甲醇解后 M22 切取 C8 馏分） | A：C8H16O2 + CH4O → C9H18O2 + H2O；B：以三辛酸甘油酯计 C27H50O6 + 3 CH4O → 3 C9H18O2 + C3H8O3 |
| S0473 | 辛酸乙酯 | `ethyl_caprylate` | T2 | ST01 | S0309 + `ethanol` | C8H16O2 + C2H6O → C10H20O2 + H2O |
| S0474 | 顺式-4-辛烯酸乙酯 | `ethyl_cis_4_octenoate` | T3 | ST29 | S0119 + `carbethoxypropyl_tpp_bromide` + `sodium_methoxide` | C24H26BrO2P + C4H8O + CH3NaO → C10H18O2 + C18H15OP + NaBr + CH4O |
| S0475 | 顺式-4,7-辛二烯酸乙酯 | `ethyl_cis_4_7_octadienoate` | T3 | ST28 | `z_hexadienyl_chloride` + S0403 | C6H9Cl + C7H12O4 + C2H5NaO → C13H20O4 + NaCl + C2H6O；C13H20O4 + H2O → C10H16O2 + CO2 + C2H6O |
| S0476 | 辛酸异戊酯 | `isoamyl_octanoate` | T2 | ST01 | S0309 + S0008 | C8H16O2 + C5H12O → C13H26O2 + H2O |
| S0477 | 辛酸壬酯 | `nonyl_octanoate` | T2（S0018 为 T3） | ST01 | S0309 + S0018 | C8H16O2 + C9H20O → C17H34O2 + H2O |
| S0478 | 辛酸苯乙酯 | `phenethyl_octanoate` | T2（S0034 为 T3） | ST01 | S0309 + S0034 | C8H16O2 + C8H10O → C16H24O2 + H2O |
| S0479 | 2-壬烯酸甲酯 | `methyl_2_nonenoate` | T2（`trans_2_nonenoic_acid` 为 T3） | ST01 | `trans_2_nonenoic_acid` + `methanol` | C9H16O2 + CH4O → C10H18O2 + H2O |
| S0480 | 壬酸乙酯 | `ethyl_nonanoate` | T2（S0310 为 T3） | ST01 | S0310 + `ethanol` | C9H18O2 + C2H6O → C11H22O2 + H2O |
| S0481 | 癸酸乙酯 | `ethyl_decanoate` | T2 | ST01 | S0311 + `ethanol` | C10H20O2 + C2H6O → C12H24O2 + H2O |
| S0482 | 反式-2-顺式-4-癸二烯酸乙酯 | `ethyl_decadienoate` | T3 / T4 | A ST03；B ST18 | A `stillingia_oil` + `ethanol`（乙醇钠催化醇解，M22 精馏切取目标酯）；B `stillingia_oil` + `ethanol` + `enzymes/lipase`（固定化脂肪酶，P65） | 甘油酯醇解得乙酯混合物，目标酯约占 5%，不写计量式 |
| S0483 | 十二酸乙酯（月桂酸乙酯） | `ethyl_laurate` | T2（S0312 为 T3） | ST01 | S0312 + `ethanol` | C12H24O2 + C2H6O → C14H28O2 + H2O |
| S0484 | 十四酸甲酯（肉豆蔻酸甲酯） | `methyl_myristate` | T2 / T3 | A ST01；B ST03 | A S0313 + `methanol`；B `coconut_oil` + `methanol`（甲醇解后 M22 切取 C14 馏分） | A：C14H28O2 + CH4O → C15H30O2 + H2O；B：以三肉豆蔻酸甘油酯计 C45H86O6 + 3 CH4O → 3 C15H30O2 + C3H8O3 |
| S0485 | 苯甲酸甲酯 | `methyl_benzoate` | T2 | ST01 | S0315 + `methanol` | C7H6O2 + CH4O → C8H8O2 + H2O |
| S0486 | 苯甲酸乙酯 | `ethyl_benzoate` | T2 | ST01 | S0315 + `ethanol` | C7H6O2 + C2H6O → C9H10O2 + H2O |
| S0487 | 苯甲酸丙酯 | `propyl_benzoate` | T2 | ST01 | S0315 + S0049 | C7H6O2 + C3H8O → C10H12O2 + H2O |
| S0488 | 苯甲酸己酯 | `hexyl_benzoate` | T2（S0010 为 T3） | ST01 | S0315 + S0010 | C7H6O2 + C6H14O → C13H18O2 + H2O |
| S0489 | 苯甲酸苄酯 | `benzyl_benzoate` | T2（S0033 为 T3） | ST01 | S0315 + S0033 | C7H6O2 + C7H8O → C14H12O2 + H2O |
| S0490 | 苯甲酸顺式-3-己烯酯（苯甲酸叶醇酯） | `cis_3_hexenyl_benzoate` | T3 | ST03 | S0485 + S0027（甲醇钠催化，蒸出甲醇） | C8H8O2 + C6H12O → C13H16O2 + CH4O |
| S0491 | 邻氨基苯甲酸甲酯 | `methyl_anthranilate` | T2（`anthranilic_acid` 为 T3） | ST01 | `anthranilic_acid` + `methanol` | C7H7NO2 + CH4O → C8H9NO2 + H2O |
| S0492 | 苯乙酸甲酯 | `methyl_phenylacetate` | T2（S0316 为 T3） | ST01 | S0316 + `methanol` | C8H8O2 + CH4O → C9H10O2 + H2O |
| S0493 | 苯乙酸乙酯 | `ethyl_phenylacetate` | T2（S0316 为 T3） | ST01 | S0316 + `ethanol` | C8H8O2 + C2H6O → C10H12O2 + H2O |
| S0494 | 苯乙酸异戊酯 | `isoamyl_phenylacetate` | T2（S0316 为 T3） | ST01 | S0316 + S0008 | C8H8O2 + C5H12O → C13H18O2 + H2O |
| S0495 | 苯乙酸苯乙酯 | `phenethyl_phenylacetate` | T2（S0034、S0316 为 T3） | ST01 | S0316 + S0034 | C8H8O2 + C8H10O → C16H16O2 + H2O |
| S0496 | 惕各酸乙酯 | `ethyl_tiglate` | T2（S0350 为 T3） | ST01 | S0350 + `ethanol` | C5H8O2 + C2H6O → C7H12O2 + H2O |
| S0497 | 惕各酸苄酯 | `benzyl_tiglate` | T2（S0033、S0350 为 T3） | ST01 | S0350 + S0033 | C5H8O2 + C7H8O → C12H14O2 + H2O |
| S0498 | 乳酸乙酯 | `ethyl_lactate` | T2 | ST01 | S0338 + `ethanol` | C3H6O3 + C2H6O → C5H10O3 + H2O |
| S0499 | 乳酸丁酯 | `butyl_lactate` | T2（S0004 为 T3） | ST01 | S0338 + S0004 | C3H6O3 + C4H10O → C7H14O3 + H2O |
| S0500 | 肉桂酸甲酯 | `methyl_cinnamate` | T2 / T3（S0318 为 T3） | A ST01；B ST06 | A S0318 + `methanol`；B `benzaldehyde` + S0363（钠或甲醇钠催化克莱森缩合） | A：C9H8O2 + CH4O → C10H10O2 + H2O；B：C7H6O + C3H6O2 → C10H10O2 + H2O |

### 1.5 序号 501–650（S0501–S0651，149 条，酯（二）、内酯）

| 编码 | 名称 | 物料 ID | 阶段 | 模板 | 原料 | 关键反应 |
| --- | --- | --- | --- | --- | --- | --- |
| S0501 | 肉桂酸乙酯 | `ethyl_cinnamate` | T2（S0318 为 T3） | ST01 | S0318 + `ethanol` | C9H8O2 + C2H6O → C11H12O2 + H2O |
| S0502 | 肉桂酸苄酯 | `benzyl_cinnamate` | T2（S0033、S0318 为 T3） | ST01 | S0318 + S0033 | C9H8O2 + C7H8O → C16H14O2 + H2O |
| S0503 | 肉桂酸苯乙酯 | `phenethyl_cinnamate` | T2（S0034、S0318 为 T3） | ST01 | S0318 + S0034 | C9H8O2 + C8H10O → C17H16O2 + H2O |
| S0504 | 肉桂酸肉桂酯 | `cinnamyl_cinnamate` | T2（S0042、S0318 为 T3） | ST01 | S0318 + S0042 | C9H8O2 + C9H10O → C18H16O2 + H2O |
| S0505 | 水杨酸甲酯（柳酸甲酯） | `methyl_salicylate` | T2 | A ST13；B ST01 | A N028；B S0349 + `methanol` | A：—；B：C7H6O3 + CH4O → C8H8O3 + H2O |
| S0506 | 水杨酸乙酯（柳酸乙酯） | `ethyl_salicylate` | T2（S0349 为 T3） | ST01 | S0349 + `ethanol` | C7H6O3 + C2H6O → C9H10O3 + H2O |
| S0507 | 水杨酸异戊酯（柳酸异戊酯） | `isoamyl_salicylate` | T2（S0349 为 T3） | ST01 | S0349 + S0008 | C7H6O3 + C5H12O → C12H16O3 + H2O |
| S0508 | 十四酸乙酯（肉豆蔻酸乙酯） | `ethyl_myristate` | T2 | ST01 | S0313 + `ethanol` | C14H28O2 + C2H6O → C16H32O2 + H2O |
| S0509 | 油酸乙酯 | `ethyl_oleate` | T2 | ST01 | `oleic_acid` + `ethanol` | C18H34O2 + C2H6O → C20H38O2 + H2O |
| S0510 | 棕榈酸乙酯 | `ethyl_palmitate` | T2 | ST01 | S0314 + `ethanol` | C16H32O2 + C2H6O → C18H36O2 + H2O |
| S0511 | 二氢茉莉酮酸甲酯 | `methyl_dihydrojasmonate` | T3 | ST25 | `pentylcyclopentenone` + `dimethyl_malonate`（甲醇钠催化迈克尔加成，再加水脱甲氧羰基） | C10H16O + C5H8O4 → C15H24O5；C15H24O5 + H2O → C13H22O3 + CO2 + CH4O |
| S0512 | 椰子油混合酸乙酯 | `coconut_acid_ethyl_esters` | T3 | ST03 | `coconut_oil` + `ethanol`（乙醇钠催化） | 以三月桂酸甘油酯计：C39H74O6 + 3 C2H6O → 3 C14H28O2 + C3H8O3 |
| S0513 | 柠檬酸三乙酯 | `triethyl_citrate` | T2 | ST01 | S0317 + `ethanol` | C6H8O7 + 3 C2H6O → C12H20O7 + 3 H2O |
| S0514 | 甲酸大茴香酯 | `anisyl_formate` | T2（S0041 为 T3） | ST01 | S0041 + S0346（甲酸自催化，不加硫酸） | C8H10O2 + CH2O2 → C9H10O3 + H2O |
| S0515 | 甲酸顺式-3-己烯酯（甲酸叶醇酯） | `leaf_formate` | T2（S0027 为 T3） | ST01 | S0027 + S0346（甲酸自催化，不加硫酸） | C6H12O + CH2O2 → C7H12O2 + H2O |
| S0516 | 乙酸2-甲基丁酯 | `methylbutyl_acetate` | T2 | ST01 | S0863 + S0293 | C5H12O + C2H4O2 → C7H14O2 + H2O |
| S0517 | 乙酸3-苯丙酯 | `phenylpropyl_acetate` | T2（S0035 为 T3） | ST01 | S0035 + S0293 | C9H12O + C2H4O2 → C11H14O2 + H2O |
| S0518 | 乙酸丁香酯 | `eugenyl_acetate` | T3 | ST02 | S0091 + `acetic_anhydride` | C10H12O2 + C4H6O3 → C12H14O3 + C2H4O2 |
| S0519 | 4,5-二甲基-2-异丁基-3-噻唑啉 | `dimethyl_isobutyl_thiazoline` | T3 | ST11 | S0282 + S0124 + `ammonia`（阿辛格反应，低温） | C4H8OS + C5H10O + NH3 → C9H17NS + 2 H2O |
| S0520 | 乙酸异胡薄荷酯 | `isopulegyl_acetate` | T3 | ST02 | S0031 + `acetic_anhydride` | C10H18O + C4H6O3 → C12H20O2 + C2H4O2 |
| S0521 | 乙酸1,3,3-三甲基-2-降龙脑酯 | `fenchyl_acetate` | T3 | ST02 | S0026 + `acetic_anhydride` | C10H18O + C4H6O3 → C12H20O2 + C2H4O2 |
| S0522 | 丙酸甲酯 | `methyl_propionate` | T2 | ST01 | S0294 + `methanol` | C3H6O2 + CH4O → C4H8O2 + H2O |
| S0523 | 丙烯酸乙酯 | `ethyl_acrylate` | T2（`acrylic_acid` 为 T3） | ST01 | `acrylic_acid` + `ethanol`（加阻聚剂） | C3H4O2 + C2H6O → C5H8O2 + H2O |
| S0524 | 乳酸顺式-3-己烯酯（乳酸叶醇酯） | `leaf_lactate` | T2（S0027 为 T3） | ST01 | S0338 + S0027 | C3H6O3 + C6H12O → C9H16O3 + H2O |
| S0525 | 丙酸癸酯 | `decyl_propionate` | T2（S0022 为 T3） | ST01 | S0294 + S0022 | C3H6O2 + C10H22O → C13H26O2 + H2O |
| S0526 | 反式-2-丁烯酸乙酯 | `ethyl_crotonate` | T2（S1019 为 T3） | ST01 | S1019 + `ethanol` | C4H6O2 + C2H6O → C6H10O2 + H2O |
| S0527 | 丁酸丙酯 | `propyl_butyrate` | T2 | ST01 | S0296 + S0049 | C4H8O2 + C3H8O → C7H14O2 + H2O |
| S0528 | 异丁酸异丙酯 | `isopropyl_isobutyrate` | T2（S0003 为 T3） | ST01 | S0297 + S0003 | C4H8O2 + C3H8O → C7H14O2 + H2O |
| S0529 | 2-甲基丁酸异丙酯 | `isopropyl_methylbutyrate` | T2（S0003、S0298 为 T3） | ST01 | S0298 + S0003 | C5H10O2 + C3H8O → C8H16O2 + H2O |
| S0530 | 异丁酸己酯 | `hexyl_isobutyrate` | T2（S0010 为 T3） | ST01 | S0297 + S0010 | C4H8O2 + C6H14O → C10H20O2 + H2O |
| S0531 | 丁酸庚酯 | `heptyl_butyrate` | T2（S0013 为 T3） | ST01 | S0296 + S0013 | C4H8O2 + C7H16O → C11H22O2 + H2O |
| S0532 | 异丁酸辛酯 | `octyl_isobutyrate` | T2（S0014 为 T3） | ST01 | S0297 + S0014 | C4H8O2 + C8H18O → C12H24O2 + H2O |
| S0533 | 异丁酸-3-苯丙酯 | `phenylpropyl_isobutyrate` | T2（S0035 为 T3） | ST01 | S0297 + S0035 | C4H8O2 + C9H12O → C13H18O2 + H2O |
| S0534 | 丁酸香茅酯 | `citronellyl_butyrate` | T2（S0040 为 T3） | ST01 | S0296 + S0040 | C4H8O2 + C10H20O → C14H26O2 + H2O |
| S0535 | 丁酸肉桂酯 | `cinnamyl_butyrate` | T2（S0042 为 T3） | ST01 | S0296 + S0042 | C4H8O2 + C9H10O → C13H16O2 + H2O |
| S0536 | 异戊酸甲酯 | `methyl_isovalerate` | T2（S0303 为 T3） | ST01 | S0303 + `methanol` | C5H10O2 + CH4O → C6H12O2 + H2O |
| S0537 | 异戊酸异丁酯 | `isobutyl_isovalerate` | T2（S0303 为 T3） | ST01 | S0303 + S0005 | C5H10O2 + C4H10O → C9H18O2 + H2O |
| S0538 | 异戊酸2-甲基丁酯 | `methylbutyl_isovalerate` | T2（S0303 为 T3） | ST01 | S0303 + S0863 | C5H10O2 + C5H12O → C10H20O2 + H2O |
| S0539 | 异戊酸苄酯 | `benzyl_isovalerate` | T2（S0033、S0303 为 T3） | ST01 | S0303 + S0033 | C5H10O2 + C7H8O → C12H16O2 + H2O |
| S0540 | 2-戊基吡啶 | `pentylpyridine` | T3 | ST06 + ST05 | `alpha_picoline` + S0119 + `hydrogen`（活泼甲基缩合得 2-戊烯基吡啶，再加氢） | C6H7N + C4H8O → C10H13N + H2O；C10H13N + H2 → C10H15N |
| S0541 | 异戊酸肉桂酯 | `cinnamyl_isovalerate` | T2（S0042、S0303 为 T3） | ST01 | S0303 + S0042 | C5H10O2 + C9H10O → C14H18O2 + H2O |
| S0542 | 异戊酸薄荷酯 | `menthyl_isovalerate` | T2（S0303 为 T3） | ST01 | S0303 + S0066 | C5H10O2 + C10H20O → C15H28O2 + H2O |
| S0543 | 3-己烯酸甲酯 | `methyl_3_hexenoate` | T2（S0307 为 T3） | ST01 | S0307 + `methanol` | C6H10O2 + CH4O → C7H12O2 + H2O |
| S0544 | 正己酸异丁酯 | `isobutyl_hexanoate` | T2 | ST01 | S0304 + S0005 | C6H12O2 + C4H10O → C10H20O2 + H2O |
| S0545 | 己酸烯丙酯 | `allyl_hexanoate` | T2（`allyl_alcohol` 为 T3） | ST01 | S0304 + `allyl_alcohol` | C6H12O2 + C3H6O → C9H16O2 + H2O |
| S0546 | 己酸芳樟酯 | `linalyl_hexanoate` | T3 | A ST03；B ST02 | A S0459 + S0029（叔醇，甲醇钠催化酯交换，蒸出乙醇）；B `hexanoic_anhydride` + S0029 | A：C8H16O2 + C10H18O → C16H28O2 + C2H6O；B：C12H22O3 + C10H18O → C16H28O2 + C6H12O2 |
| S0547 | 3,7-二甲基-6-辛烯酸甲酯 | `methyl_citronellate` | T2（S0333 为 T3） | ST01 | S0333 + `methanol` | C10H18O2 + CH4O → C11H20O2 + H2O |
| S0548 | 3-壬烯酸甲酯 | `methyl_3_nonenoate` | T3 | ST06 + ST01 | S0136 + `malonic_acid` + `methanol`（林斯特德缩合脱羧得 3-壬烯酸，再酯化） | C7H14O + C3H4O4 → C9H16O2 + CO2 + H2O；C9H16O2 + CH4O → C10H18O2 + H2O |
| S0549 | 9-十一烯酸甲酯 | `methyl_9_undecenoate` | T3 | ST01 + ST30 | S0336 + `methanol`（酯化后双键内移） | C11H20O2 + CH4O → C12H22O2 + H2O |
| S0550 | 十一酸乙酯 | `ethyl_undecanoate` | T2（S0335 为 T3） | ST01 | S0335 + `ethanol` | C11H22O2 + C2H6O → C13H26O2 + H2O |
| S0551 | 十四酸异丙酯 | `isopropyl_myristate` | T2（S0003 为 T3） | ST01 | S0313 + S0003 | C14H28O2 + C3H8O → C17H34O2 + H2O |
| S0552 | N-甲基邻氨基苯甲酸甲酯 | `methyl_n_methylanthranilate` | T2 / T3 | A ST13；B ST05 | A N366（橘叶油，碱洗后精馏）；B S0491 + `formaldehyde` + `hydrogen`（还原甲基化，`catalysts/nickel`） | A：—；B：C8H9NO2 + CH2O + H2 → C9H11NO2 + H2O |
| S0553 | 邻氨基苯甲酸乙酯 | `ethyl_anthranilate` | T3 | ST03 | S0491 + `ethanol` | C8H9NO2 + C2H6O → C9H11NO2 + CH4O |
| S0554 | 苯甲酸异戊酯 | `isoamyl_benzoate` | T2 | ST01 | S0315 + S0008 | C7H6O2 + C5H12O → C12H16O2 + H2O |
| S0555 | 苯甲酸苯乙酯 | `phenethyl_benzoate` | T2（S0034 为 T3） | ST01 | S0315 + S0034 | C7H6O2 + C8H10O → C15H14O2 + H2O |
| S0556 | 苯乙酸异丁酯 | `isobutyl_phenylacetate` | T2（S0316 为 T3） | ST01 | S0316 + S0005 | C8H8O2 + C4H10O → C12H16O2 + H2O |
| S0557 | 苯乙酸己酯 | `hexyl_phenylacetate` | T2（S0010、S0316 为 T3） | ST01 | S0316 + S0010 | C8H8O2 + C6H14O → C14H20O2 + H2O |
| S0558 | 苯丙酸乙酯 | `ethyl_hydrocinnamate` | T3 | ST05 | S0501 + `hydrogen`（`catalysts/palladium`，只加氢碳碳双键） | C11H12O2 + H2 → C11H14O2 |
| S0559 | 环己基羧酸甲酯 | `methyl_cyclohexanecarboxylate` | T2（S1301 为 T3） | ST01 | S1301 + `methanol` | C7H12O2 + CH4O → C8H14O2 + H2O |
| S0560 | 大茴香酸甲酯 | `methyl_anisate` | T2（S0879 为 T3） | ST01 | S0879 + `methanol` | C8H8O3 + CH4O → C9H10O3 + H2O |
| S0561 | 大茴香酸乙酯 | `ethyl_anisate` | T2（S0879 为 T3） | ST01 | S0879 + `ethanol` | C8H8O3 + C2H6O → C10H12O3 + H2O |
| S0562 | 水杨酸苯乙酯 | `phenethyl_salicylate` | T3 | ST03 | S0505 + S0034 | C8H8O3 + C8H10O → C15H14O3 + CH4O |
| S0563 | 十二酸异戊酯（月桂酸异戊酯） | `isoamyl_laurate` | T2（S0312 为 T3） | ST01 | S0312 + S0008 | C12H24O2 + C5H12O → C17H34O2 + H2O |
| S0564 | 亚油酸甲酯(48%),亚麻酸甲酯(52%)混合物 | `methyl_linoleate_linolenate` | T3 | ST03 | `materials/flaxseed`（M02 压榨取油）+ `methanol`（酯交换后 M22 分馏去饱和与油酸甲酯） | 以三亚麻酸甘油酯计：C57H92O6 + 3 CH4O → 3 C19H32O2 + C3H8O3 |
| S0565 | 茉莉酮酸甲酯 | `methyl_jasmonate` | T4 | ST01 | `jasmonic_acid` + `methanol` | C12H18O3 + CH4O → C13H20O3 + H2O |
| S0566 | 水杨酸苄酯（柳酸苄酯） | `benzyl_salicylate` | T3 | ST03 | S0505 + S0033 | C8H8O3 + C7H8O → C14H12O3 + CH4O |
| S0567 | 肉桂酸异丁酯 | `isobutyl_cinnamate` | T2（S0318 为 T3） | ST01 | S0318 + S0005 | C9H8O2 + C4H10O → C13H16O2 + H2O |
| S0568 | 肉桂酸3-苯丙酯 | `phenylpropyl_cinnamate` | T2（S0035、S0318 为 T3） | ST01 | S0318 + S0035 | C9H8O2 + C9H12O → C18H18O2 + H2O |
| S0569 | 酒石酸二乙酯 | `diethyl_tartrate` | T2 | ST01 | 54 + `ethanol` | C4H6O6 + 2 C2H6O → C8H14O6 + 2 H2O |
| S0570 | 菸酸甲酯 | `methyl_nicotinate` | T2（110 为 T3） | ST01 | 110（`nicotinic_acid`）+ `methanol` | C6H5NO2 + CH4O → C7H7NO2 + H2O |
| S0571 | 惕各酸苯乙酯 | `phenethyl_tiglate` | T2（S0034、S0350 为 T3） | ST01 | S0350 + S0034 | C5H8O2 + C8H10O → C13H16O2 + H2O |
| S0573 | 3,5,5-三甲基-1-己醇 | `trimethylhexanol` | T3 | ST05 | S1250 + `hydrogen` | C9H18O + H2 → C9H20O |
| S0574 | 丁酸茴香酯 | `anisyl_butyrate` | T2（S0041 为 T3） | ST01 | S0296 + S0041 | C4H8O2 + C8H10O2 → C12H16O3 + H2O |
| S0575 | 异戊酸龙脑酯 | `bornyl_isovalerate` | T2（S0303 为 T3） | ST01 | S0303 + S0028 | C5H10O2 + C10H18O → C15H26O2 + H2O |
| S0576 | 2,6-二甲基-4-庚醇 | `dimethylheptanol` | T3 | ST05 | S1132 + `hydrogen` | C9H18O + H2 → C9H20O |
| S0577 | 苯甲酸异丁酯 | `isobutyl_benzoate` | T2 | ST01 | S0315 + S0005 | C7H6O2 + C4H10O → C11H14O2 + H2O |
| S0578 | 甲酸橙花酯 | `neryl_formate` | T2（S0046 为 T3） | ST01 | S0046 + S0346（甲酸自催化，低温） | C10H18O + CH2O2 → C11H18O2 + H2O |
| S0579 | 乙酸甲基苄醇酯(邻、间、对位混合物) | `methylbenzyl_acetate` | T3 | ST05 + ST02 | S0166 + `hydrogen` + `acetic_anhydride` | C8H8O + H2 → C8H10O；C8H10O + C4H6O3 → C10H12O2 + C2H4O2 |
| S0580 | 乙酸顺式和反式-对1(7),8-䓝二烯-2-醇酯 | `menthadienyl_acetate` | T3 | ST30 + ST02 | `limonene_oxide` + `acetic_anhydride`（碱催化开环异构得对-1(7),8-䓝二烯-2-醇，再乙酰化） | 异构化组成不变；C10H16O + C4H6O3 → C12H18O2 + C2H4O2 |
| S0581 | 乙酸龙脑烯醇酯 | `campholenyl_acetate` | T3 | ST05 + ST02 | S0193 + `hydrogen`（`catalysts/copper`，只还原醛基）+ `acetic_anhydride` | C10H16O + H2 → C10H18O；C10H18O + C4H6O3 → C12H20O2 + C2H4O2 |
| S0582 | 丙酸丙酯 | `propyl_propionate` | T2 | ST01 | S0294 + S0049 | C3H6O2 + C3H8O → C6H12O2 + H2O |
| S0583 | 丙酸丁酯 | `butyl_propionate` | T2（S0004 为 T3） | ST01 | S0294 + S0004 | C3H6O2 + C4H10O → C7H14O2 + H2O |
| S0584 | 丙酸己酯 | `hexyl_propionate` | T2（S0010 为 T3） | ST01 | S0294 + S0010 | C3H6O2 + C6H14O → C9H18O2 + H2O |
| S0585 | 丙酮酸乙酯 | `ethyl_pyruvate` | T2（S0295 为 T3） | ST01 | S0295 + `ethanol` | C3H4O3 + C2H6O → C5H8O3 + H2O |
| S0586 | 丁酸辛酯 | `octyl_butyrate` | T2（S0014 为 T3） | ST01 | S0296 + S0014 | C4H8O2 + C8H18O → C12H24O2 + H2O |
| S0587 | 异丁酸丙酯 | `propyl_isobutyrate` | T2 | ST01 | S0297 + S0049 | C4H8O2 + C3H8O → C7H14O2 + H2O |
| S0588 | 异丁酸异丁酯 | `isobutyl_isobutyrate` | T2 | ST01 | S0297 + S0005 | C4H8O2 + C4H10O → C8H16O2 + H2O |
| S0589 | 异丁酸香茅酯 | `citronellyl_isobutyrate` | T2（S0040 为 T3） | ST01 | S0297 + S0040 | C4H8O2 + C10H20O → C14H26O2 + H2O |
| S0590 | 反式-2-丁烯酸顺式-3-己烯酯 | `leaf_crotonate` | T2（S0027、S1019 为 T3） | ST01 | S1019 + S0027 | C4H6O2 + C6H12O → C10H16O2 + H2O |
| S0591 | 丁二酸单薄荷酯 | `monomenthyl_succinate` | T3 | ST02 | S0066 + `succinic_anhydride`（开环单酯化，无副产酸） | C10H20O + C4H4O3 → C14H24O4 |
| S0592 | 正戊酸正戊酯 | `pentyl_valerate` | T2（S0006、S0300 为 T3） | ST01 | S0300 + S0006 | C5H10O2 + C5H12O → C10H20O2 + H2O |
| S0593 | 异戊酸辛酯 | `octyl_isovalerate` | T2（S0014、S0303 为 T3） | ST01 | S0303 + S0014 | C5H10O2 + C8H18O → C13H26O2 + H2O |
| S0594 | 己酸丁酯 | `butyl_hexanoate` | T2（S0004 为 T3） | ST01 | S0304 + S0004 | C6H12O2 + C4H10O → C10H20O2 + H2O |
| S0595 | 己酸苯乙酯 | `phenethyl_hexanoate` | T2（S0034 为 T3） | ST01 | S0304 + S0034 | C6H12O2 + C8H10O → C14H20O2 + H2O |
| S0596 | 异丁酸叶醇酯 | `leaf_isobutyrate` | T2（S0027 为 T3） | ST01 | S0297 + S0027 | C4H8O2 + C6H12O → C10H18O2 + H2O |
| S0597 | 辛酸己酯 | `hexyl_octanoate` | T2（S0010 为 T3） | ST01 | S0309 + S0010 | C8H16O2 + C6H14O → C14H28O2 + H2O |
| S0598 | 2-辛烯酸乙酯 | `ethyl_2_octenoate` | T3 | ST06 | S0130 + S0403（先半水解成丙二酸单乙酯，再德布纳缩合脱羧） | C7H12O4 + H2O → C5H8O4 + C2H6O；C6H12O + C5H8O4 → C10H18O2 + CO2 + H2O |
| S0599 | 2,4,7-癸三烯酸乙酯 | `ethyl_decatrienoate` | T3 | ST04 + ST01 | S0198 + `oxygen` + `ethanol`（醛氧化为酸，再酯化） | 2 C10H14O + O2 → 2 C10H14O2；C10H14O2 + C2H6O → C12H18O2 + H2O |
| S0600 | 苯甲酸芳樟酯 | `linalyl_benzoate` | T3 | A ST03；B ST02 | A S0485 + S0029（叔醇，甲醇钠催化酯交换，蒸出甲醇）；B `benzoic_anhydride` + S0029 | A：C8H8O2 + C10H18O → C17H22O2 + CH4O；B：C14H10O3 + C10H18O → C17H22O2 + C7H6O2 |
| S0601 | 反式-2-甲基2-丁烯酸顺式-3-己烯酯（惕各酸叶醇酯） | `leaf_tiglate` | T2（S0027、S0350 为 T3） | ST01 | S0350 + S0027 | C5H8O2 + C6H12O → C11H18O2 + H2O |
| S0602 | 2-丁烯酸异丁酯 | `isobutyl_crotonate` | T2（S1019 为 T3） | ST01 | S1019 + S0005 | C4H6O2 + C4H10O → C8H14O2 + H2O |
| S0603 | 3-甲基丁酸己酯 | `hexyl_isovalerate` | T2（S0010、S0303 为 T3） | ST01 | S0303 + S0010 | C5H10O2 + C6H14O → C11H22O2 + H2O |
| S0604 | 顺式-3-己烯酸顺式-3-己烯酯 | `leaf_3_hexenoate` | T2（S0027、S0307 为 T3） | ST01 | S0307（顺式）+ S0027 | C6H10O2 + C6H12O → C12H20O2 + H2O |
| S0605 | 3-羟基己酸甲酯 | `methyl_3_hydroxyhexanoate` | T3 | ST03 | S0461 + `methanol`（甲醇大过量，蒸出乙醇） | C8H16O3 + CH4O → C7H14O3 + C2H6O |
| S0606 | 苯甲酸香叶酯 | `geranyl_benzoate` | T3 | A ST03；B ST02 | A S0485 + S0039；B `benzoic_anhydride` + S0039 | A：C8H8O2 + C10H18O → C17H22O2 + CH4O；B：C14H10O3 + C10H18O → C17H22O2 + C7H6O2 |
| S0607 | 琥珀酸二甲酯 | `dimethyl_succinate` | T2 | ST01 | S0351 + `methanol` | C4H6O4 + 2 CH4O → C6H10O4 + 2 H2O |
| S0608 | 硬脂酸乙酯 | `ethyl_stearate` | T2 | ST01 | S0352 + `ethanol` | C18H36O2 + C2H6O → C20H40O2 + H2O |
| S0609 | 3-甲基-2-丁烯-1-醇乙酸酯（乙酸异戊烯酯） | `prenyl_acetate` | T3 | ST02 | S0956 + `acetic_anhydride` | C5H10O + C4H6O3 → C7H12O2 + C2H4O2 |
| S0610 | 己酸反式-2-己烯酯 | `hexenyl_hexanoate` | T2（S0011 为 T3） | ST01 | S0304 + S0011 | C6H12O2 + C6H12O → C12H22O2 + H2O |
| S0611 | 甲酸龙脑酯 | `bornyl_formate` | T2 | ST01 | S0028 + S0346（甲酸自催化） | C10H18O + CH2O2 → C11H18O2 + H2O |
| S0612 | 顺式-4-庚烯酸乙酯 | `ethyl_4_heptenoate` | T3 | ST04 + ST01 | S0137 + `oxygen` + `ethanol`（醛氧化为酸，再酯化） | 2 C7H12O + O2 → 2 C7H12O2；C7H12O2 + C2H6O → C9H16O2 + H2O |
| S0613 | 辛酸戊酯 | `amyl_octanoate` | T2（S0006 为 T3） | ST01 | S0309 + S0006 | C8H16O2 + C5H12O → C13H26O2 + H2O |
| S0614 | 4-甲基戊酸甲酯 | `methyl_4_methylvalerate` | T2（S0348 为 T3） | ST01 | S0348 + `methanol` | C6H12O2 + CH4O → C7H14O2 + H2O |
| S0615 | 乙酸胡椒醛酯 | `piperonyl_acetate` | T3 | ST05 + ST02 | S0175 + `hydrogen` + `acetic_anhydride`（醛还原为胡椒醇，再乙酰化） | C8H6O3 + H2 → C8H8O3；C8H8O3 + C4H6O3 → C10H10O4 + C2H4O2 |
| S0616 | 丙酸肉桂酯 | `cinnamyl_propionate` | T2（S0042 为 T3） | ST01 | S0294 + S0042 | C3H6O2 + C9H10O → C12H14O2 + H2O |
| S0617 | 异丁酸甲基苯基原酯 | `styrallyl_isobutyrate` | T2（S0032 为 T3） | ST01 | S0297 + S0032 | C4H8O2 + C8H10O → C12H16O2 + H2O |
| S0618 | 异丁酸十二酯 | `dodecyl_isobutyrate` | T2 | ST01 | S0297 + S0024 | C4H8O2 + C12H26O → C16H32O2 + H2O |
| S0619 | 异丁酸松油酯 | `terpinyl_isobutyrate` | T3 | A ST03；B ST02 | A S0419 + S0037（叔醇，甲醇钠催化酯交换，蒸出甲醇）；B `isobutyric_anhydride` + S0037 | A：C5H10O2 + C10H18O → C14H24O2 + CH4O；B：C8H14O3 + C10H18O → C14H24O2 + C4H8O2 |
| S0620 | 水杨酸异丁酯 | `isobutyl_salicylate` | T2（S0349 为 T3） | ST01 | S0349 + S0005 | C7H6O3 + C4H10O → C11H14O3 + H2O |
| S0621 | 肉桂酸异戊酯 | `isoamyl_cinnamate` | T2（S0318 为 T3） | ST01 | S0318 + S0008 | C9H8O2 + C5H12O → C14H18O2 + H2O |
| S0622 | 乙酸异龙脑酯 | `isobornyl_acetate` | T3 | ST14 | S0657 + S0293（酸催化加成重排） | C10H16 + C2H4O2 → C12H20O2 |
| S0623 | γ-戊内酯 | `gamma_valerolactone` | T3 | ST05 | S0328 + `hydrogen`（加氢后自发内酯化） | C5H8O3 + H2 → C5H8O2 + H2O |
| S0624 | γ-己内酯 | `gamma_hexalactone` | T3 | ST08 | S0049 + S0523（过氧化物引发自由基加成后环化） | C3H8O + C5H8O2 → C6H10O2 + C2H6O |
| S0625 | γ-庚内酯 | `gamma_heptalactone` | T3 | ST08 | S0004 + S0523 | C4H10O + C5H8O2 → C7H12O2 + C2H6O |
| S0626 | γ-辛内酯 | `gamma_octalactone` | T3 | ST08 | S0006 + S0523 | C5H12O + C5H8O2 → C8H14O2 + C2H6O |
| S0627 | γ-壬内酯 | `gamma_nonalactone` | T3 | ST08 | S0010 + S0523 | C6H14O + C5H8O2 → C9H16O2 + C2H6O |
| S0629 | γ-十二内酯 | `gamma_dodecalactone` | T3 | ST08 | S0018 + S0523 | C9H20O + C5H8O2 → C12H22O2 + C2H6O |
| S0630 | γ-丁内酯 | `gamma_butyrolactone` | T3 | ST05 | `succinic_anhydride` + `hydrogen`（`catalysts/copper`；顺酐 → 琥珀酸酐 → 丁内酯，2026-11-12 改，与琥珀酸酐的加氢区分） | C4H4O3 + 2 H2 → C4H6O2 + H2O |
| S0631 | δ-己内酯 | `delta_hexalactone` | T3 | ST05 + ST08 | `ethyl_oxohexanoate` + `hydrogen`（酮基加氢后酸催化内酯化） | C8H14O3 + H2 → C6H10O2 + C2H6O |
| S0632 | δ-辛内酯 | `delta_octalactone` | T3 | ST24 | `propylcyclopentanone` + `hydrogen_peroxide` | C8H14O + H2O2 → C8H14O2 + H2O |
| S0633 | δ-壬内酯 | `delta_nonalactone` | T3 | ST24 | `butylcyclopentanone` + `hydrogen_peroxide` | C9H16O + H2O2 → C9H16O2 + H2O |
| S0634 | δ-癸内酯 | `delta_decalactone` | T3 | A ST05；B ST24 | A S0638 + `hydrogen`；B `pentylcyclopentanone` + `hydrogen_peroxide` | A：C10H16O2 + H2 → C10H18O2；B：C10H18O + H2O2 → C10H18O2 + H2O |
| S0635 | δ-十一内酯 | `delta_undecalactone` | T3 | ST24 | `hexylcyclopentanone` + `hydrogen_peroxide` | C11H20O + H2O2 → C11H20O2 + H2O |
| S0636 | δ-十二内酯 | `delta_dodecalactone` | T3 | ST24 | `heptylcyclopentanone` + `hydrogen_peroxide` | C12H22O + H2O2 → C12H22O2 + H2O |
| S0637 | 十五内酯 | `pentadecanolide` | T2 / T3 | A ST13；B ST08 | A N244；B `hydroxypentadecanoic_acid`（聚酯解聚–内酯化，高真空） | A：—；B：C15H30O3 → C15H28O2 + H2O |
| S0638 | 5-羟基-2-癸烯酸δ-内酯 | `massoia_lactone` | T2 | ST13 | N397 | — |
| S0639 | 3-丙叉苯酞 | `propylidenephthalide` | T3 | ST06 | `phthalic_anhydride` + S0296（丁酸钠催化，珀金型缩合脱羧） | C8H4O3 + C4H8O2 → C11H10O2 + CO2 + H2O |
| S0640 | 3-丁叉苯酞 | `butylidenephthalide` | T2 / T3 | A ST13；B ST06 | A N200；B `phthalic_anhydride` + S0300（戊酸钠催化） | A：—；B：C8H4O3 + C5H10O2 → C12H12O2 + CO2 + H2O |
| S0641 | 薄荷内酯 | `mintlactone` | T3（`hydroxymenthofurolactone` 为 T4） | ST05 + ST08 | `hydroxymenthofurolactone` + `hydrogen`（碱液中 `catalysts/palladium` 加氢，酸化后带分水脱水） | C10H14O3 + H2 → C10H14O2 + H2O |
| S0642 | δ-十三内酯 | `delta_tridecalactone` | T3 | ST24 | `octylcyclopentanone` + `hydrogen_peroxide` | C13H24O + H2O2 → C13H24O2 + H2O |
| S0643 | δ-十四内酯 | `delta_tetradecalactone` | T3 | ST24 | `nonylcyclopentanone` + `hydrogen_peroxide` | C14H26O + H2O2 → C14H26O2 + H2O |
| S0644 | 5-羟基-2,4-癸二烯酸内酯 | `pentyl_pyrone` | T4 | ST18 | `glucose` + `cultures/trichoderma`（发酵液溶剂萃取 → 精馏） | 木霉次级代谢产物，不写计量式 |
| S0645 | 5-羟基-7-癸烯酸内酯（茉莉内酯） | `jasmine_lactone` | T3 | ST24 | `pentenylcyclopentanone` + `hydrogen_peroxide`（选择性拜耳–维利格氧化） | C10H16O + H2O2 → C10H16O2 + H2O |
| S0646 | 威士忌内酯 | `whiskey_lactone` | T3 | ST08 | S0006 + S0526（自由基加成后环化） | C5H12O + C6H10O2 → C9H16O2 + C2H6O |
| S0647 | 二氢猕猴桃内酯 | `dihydroactinidiolide` | T3 / T4 | A ST04；B ST04 | A 20 + `oxygen`（空气氧化降解，联产 β-紫罗兰酮）；B S0235 + `oxygen`（光照异构后自氧化） | A：降解产物混合，不写计量式；B：C13H20O + O2 → C11H16O2 + C2H4O |
| S0648 | 黄葵内酯 | `ambrettolide` | T2 | ST13 | N213 | — |
| S0649 | α-当归内酯 | `alpha_angelica_lactone` | T3 | ST08 | S0328（磷酸催化，减压蒸出） | C5H8O3 → C5H6O2 + H2O |
| S0650 | γ-甲基癸内酯 | `gamma_methyldecalactone` | T3 | ST08 | S0015 + S0523（自由基加成后环化） | C8H18O + C5H8O2 → C11H20O2 + C2H6O |
| S0651 | β-石竹烯 | `beta_caryophyllene` | T2 | ST13 | N001（碱洗除丁香酚后的萜烯馏分再精馏） | — |

### 1.6 序号 651–800（S0652–S0801，148 条，萜烯、烃、含氮杂环为主）

| 编码 | 名称 | 物料 ID | 阶段 | 模板 | 原料 | 关键反应 |
| --- | --- | --- | --- | --- | --- | --- |
| S0652 | 巴伦西亚橘烯 | `valencene` | T2 | ST13 | N131 | — |
| S0653 | 月桂烯 | `myrcene` | T3 / T2 | A ST39；B ST13 | A S0659；B N338 | C10H16 → C10H16 |
| S0654 | d-苧烯 | `limonene` | — | ST26 | 已有：`limonene` | — |
| S0655 | 异松油烯 | `terpinolene` | T3 | ST14 | S0658（酸催化异构化） | C10H16 → C10H16 |
| S0656 | 罗勒烯 | `ocimene` | T3 | ST39 | S0658（热解开环，联产别罗勒烯） | C10H16 → C10H16 |
| S0657 | 莰烯 | `camphene` | T3 | ST14 | S0658（二氧化钛固体酸异构化） | C10H16 → C10H16 |
| S0658 | α-蒎烯 | `alpha_pinene` | T2 | ST13 | N298 | — |
| S0659 | β-蒎烯 | `beta_pinene` | T2 | ST13 | N298 | — |
| S0660 | 1,8-桉叶素 | `cineole_1_8` | T2 | ST13 | N114 | — |
| S0661 | 1,4-桉叶素 | `cineole_1_4` | T3 | ST14 | S0064（酸催化分子内醚化） | C10H18O → C10H18O |
| S0662 | 二氢香豆素 | `dihydrocoumarin` | T3 | ST06、ST05 | S0164 + `acetic_anhydride` + `sodium_acetate` + `hydrogen`（珀金反应得香豆素后加氢） | C7H6O2 + C4H6O3 → C9H6O2 + C2H4O2 + H2O；C9H6O2 + H2 → C9H8O2 |
| S0663 | 1,4-二甲基-4-乙酰基-1-环己烯 | `dimethyl_acetyl_cyclohexene` | T3 | ST52 | `isoprene` + `methyl_isopropenyl_ketone` | C5H8 + C5H8O → C10H16O |
| S0664 | 2-甲酰基-6,6-二甲基双环[3.1.1]庚-2-烯（桃金娘烯醛） | `myrtenal` | T3 | ST04 | S0658 + `selenium_dioxide`（烯丙位氧化，P45） | C10H16 + SeO2 → C10H14O + Se + H2O |
| S0665 | 茶螺烷 | `theaspirane` | T3 | ST02、ST05、ST14 | S0235 + `acetic_anhydride` + `hydrogen`（烯醇乙酸酯 → 还原 → 酸催化环化） | C13H20O + C4H6O3 → C15H22O2 + C2H4O2；C15H22O2 + H2 + H2O → C13H22O + C2H4O2；C13H22O → C13H22O |
| S0666 | 1,3,5-十一碳三烯 | `undecatriene` | T3 | ST29 | S0154 + `methyl_chloride` + `triphenylphosphine` + `sodium_methoxide` | C18H15P + CH3Cl → C19H18ClP；C19H18ClP + CH3NaO + C10H16O → C11H18 + C18H15OP + NaCl + CH4O |
| S0667 | 对,α-二甲基苯乙烯 | `p_alpha_dimethylstyrene` | T3 | ST14 | S0059（酸催化脱水） | C10H14O → C10H12 + H2O |
| S0668 | α-水芹烯 | `alpha_phellandrene` | T2 | ST13 | N205 | — |
| S0669 | 红没药烯 | `bisabolene` | T2 / T3 | A ST13；B ST14 | A N311；B S0047（酸催化脱水） | C15H26O → C15H24 + H2O |
| S0670 | γ-松油烯 | `gamma_terpinene` | T2 / T3 | A ST13；B ST14 | A N261；B `limonene`（酸催化异构化） | C10H16 → C10H16 |
| S0671 | 6-羟基二氢茶螺烷 | `hydroxydihydrotheaspirane` | T3 | ST24、ST05 | S0665 + `hydrogen_peroxide` + `acetic_acid`（过乙酸环氧化）+ `hydrogen` | C13H22O + H2O2 → C13H22O2 + H2O；C13H22O2 + H2 → C13H24O2 |
| S0672 | 1-甲基-3-甲氧基-4-异丙基苯 | `thymol_methyl_ether` | T3 | ST09 | `thymol` + `methyl_chloride` + `sodium_hydroxide` | C10H14O + CH3Cl + NaOH → C11H16O + NaCl + H2O |
| S0673 | 间-二甲氧基苯 | `m_dimethoxybenzene` | T3 | ST09 | `resorcinol` + `methyl_chloride` + `sodium_hydroxide` | C6H6O2 + 2 CH3Cl + 2 NaOH → C8H10O2 + 2 NaCl + 2 H2O |
| S0674 | 对-异丙基甲苯 | `p_cymene` | T3 | ST15 | `toluene` + `propene`（`catalysts/aluminium_chloride`，精馏分出对位） | C7H8 + C3H6 → C10H14 |
| S0675 | 3,4-二甲酚 | `dimethylphenol_3_4` | T3 | ST51 | `coal_tar`（酚油馏分碱洗提酚） | — |
| S0676 | 1-甲基萘 | `methylnaphthalene_1` | T3 | ST51 | `coal_tar`（洗油馏分） | — |
| S0677 | 1,2-二甲氧基苯 | `o_dimethoxybenzene` | T3 | ST09 | `guaiacol` + `methyl_chloride` + `sodium_hydroxide` | C7H8O2 + CH3Cl + NaOH → C8H10O2 + NaCl + H2O |
| S0678 | α-金合欢烯 | `alpha_farnesene` | T3 | ST14 | S0047（酸催化脱水，精馏分出 α 体） | C15H26O → C15H24 + H2O |
| S0679 | 苏合香烯 | `styrene` | T3 | ST04 | `ethylbenzene`（M27 气相催化脱氢，`catalysts/iron`） | C8H10 → C8H8 + H2 |
| S0680 | α-松油烯 | `alpha_terpinene` | T3 / T2 | A ST14；B ST13 | A S0658（酸催化异构化）；B N261 | C10H16 → C10H16 |
| S0681 | 3-蒈烯 | `carene_3` | T2 | ST13 | N298 | — |
| S0682 | 聚苧烯 | `polylimonene` | T3 | ST50 | `limonene` + `catalysts/aluminium_chloride` | —（阳离子聚合，组成 (C10H16)n） |
| S0683 | 香菇素 | `lenthionine` | T2 / T3 | A ST21；B ST10 | A `materials/shiitake`；B `dichloromethane` + `sodium_sulfide` + `sulfur`（多硫化钠） | 2 CH2Cl2 + 2 Na2S + 3 S → C2H4S5 + 4 NaCl |
| S0684 | 氧化石竹烯 | `caryophyllene_oxide` | T3 | ST24 | S0651 + `hydrogen_peroxide` + `acetic_acid`（过乙酸） | C15H24 + H2O2 → C15H24O + H2O |
| S0685 | 2,4,6-三甲基-1,3,5-三氧杂环己烷（三聚乙醛） | `paraldehyde` | T2 | ST07 | `acetaldehyde` + `sulfuric_acid`（催化三聚） | 3 C2H4O → C6H12O3 |
| S0686 | 甲硫醇 | `methyl_mercaptan` | T3 | ST10 | `methanol` + S1010（M27 气相，`catalysts/alumina`） | CH4O + H2S → CH4S + H2O |
| S0687 | 3-甲硫基丙醇 | `methylthio_propanol` | T3 | ST05 | S0700 + `hydrogen` | C4H8OS + H2 → C4H10OS |
| S0688 | 正丁硫醇 | `butanethiol` | T3 | ST10 | S0004 + S1010（M27 气相，`catalysts/alumina`） | C4H10O + H2S → C4H10S + H2O |
| S0689 | 2-甲基-1-丁硫醇 | `methyl_butanethiol_2_1` | T3 | ST10 | S0863 + S1010（M27 气相，`catalysts/alumina`） | C5H12O + H2S → C5H12S + H2O |
| S0690 | 3-(甲硫基)-1-己醇 | `methylthio_hexanol_3` | T3 | ST05 | S0817 + `hydrogen` | C7H14OS + H2 → C7H16OS |
| S0691 | 1,6-己二硫醇 | `hexanedithiol` | T3 | ST23、ST10 | `hexanediol` + `hydrochloric_acid` + S1010 + `sodium_hydroxide` | C6H14O2 + 2 HCl → C6H12Cl2 + 2 H2O；C6H12Cl2 + 2 H2S + 2 NaOH → C6H14S2 + 2 NaCl + 2 H2O |
| S0692 | 糠基硫醇（咖啡醛） | `furfuryl_mercaptan` | T3 | ST23、ST10 | S0795 + `hydrochloric_acid` + S1010 + `sodium_hydroxide` | C5H6O2 + HCl → C5H5ClO + H2O；C5H5ClO + H2S + NaOH → C5H6OS + NaCl + H2O |
| S0693 | 二甲基硫醚 | `dimethyl_sulfide` | T3 | ST10 | S0686 + `methanol`（M27 气相，`catalysts/alumina`） | CH4S + CH4O → C2H6S + H2O |
| S0694 | 二甲基二硫醚 | `dimethyl_disulfide` | T3 | ST10 | S0686 + `sulfur` | 2 CH4S + S → C2H6S2 + H2S |
| S0695 | 二甲基三硫醚 | `dimethyl_trisulfide` | T3 | ST10 | S0686 + `sulfur_dichloride` | 2 CH4S + Cl2S → C2H6S3 + 2 HCl |
| S0696 | 二丁基硫醚 | `dibutyl_sulfide` | T3 | ST23、ST10 | S0004 + `hydrochloric_acid` + `sodium_sulfide` | C4H10O + HCl → C4H9Cl + H2O；2 C4H9Cl + Na2S → C8H18S + 2 NaCl |
| S0697 | 2,2'-(硫代二亚甲基)-二呋喃二糠基硫醚 | `difurfuryl_sulfide` | T3 | ST23、ST10 | S0795 + `hydrochloric_acid` + `sodium_sulfide` | C5H6O2 + HCl → C5H5ClO + H2O；2 C5H5ClO + Na2S → C10H10O2S + 2 NaCl |
| S0698 | 二糠基二硫醚 | `difurfuryl_disulfide` | T3 | ST10 | S0692 + `hydrogen_peroxide` | 2 C5H6OS + H2O2 → C10H10O2S2 + 2 H2O |
| S0699 | 邻-甲硫基苯酚 | `o_methylthiophenol` | T3 | ST15 | `phenol` + S0694（`catalysts/aluminium_chloride`，邻位硫甲基化） | C6H6O + C2H6S2 → C7H8OS + CH4S |
| S0700 | 3-甲硫基丙醛 | `methylthio_propionaldehyde` | T3 | ST25 | `acrolein` + S0686 | C3H4O + CH4S → C4H8OS |
| S0701 | 8-巯基薄荷酮 | `mercaptomenthone_8` | T3 | ST25 | S0994 + S1010（碱催化） | C10H16O + H2S → C10H18OS |
| S0702 | 硫代乙酸糠酯 | `furfuryl_thioacetate` | T3 | ST10 | S0692 + `acetic_anhydride` | C5H6OS + C4H6O3 → C7H8O2S + C2H4O2 |
| S0703 | 3-甲硫基丙酸甲酯 | `methyl_methylthiopropionate` | T3 | ST01、ST25 | `acrylic_acid` + `methanol` + S0686 | C3H4O2 + CH4O → C4H6O2 + H2O；C4H6O2 + CH4S → C5H10O2S |
| S0704 | 3-甲硫基丙酸乙酯 | `ethyl_methylthiopropionate` | T3 | ST25 | S0523 + S0686 | C5H8O2 + CH4S → C6H12O2S |
| S0705 | 吲哚 | `indole` | T3 | A ST51；B ST34 | A `coal_tar`（洗油馏分）；B `aniline` + `ethylene_glycol` | C6H7N + C2H6O2 → C8H7N + 2 H2O + H2 |
| S0706 | 三甲基胺 | `trimethylamine` | T3 | ST34 | `methanol` + `ammonia`（`catalysts/alumina`，联产一甲胺、二甲胺） | 3 CH4O + NH3 → C3H9N + 3 H2O |
| S0707 | 玫瑰醚 | `rose_oxide` | T3 | ST24、ST09 | S0040 + `hydrogen_peroxide`（`catalysts/tungstate` 生成单线态氧）+ `sodium_sulfite` + `sulfuric_acid` | 2 H2O2 → O2 + 2 H2O；C10H20O + O2 → C10H20O3；C10H20O3 + Na2SO3 → C10H20O2 + Na2SO4；C10H20O2 → C10H18O + H2O |
| S0708 | 羟基香茅醇 | `hydroxycitronellol` | T3 | ST05 | S1173 + `hydrogen` | C10H20O2 + H2 → C10H22O2 |
| S0709 | 3,5-二甲基-1,2,4-三硫杂环戊烷 | `dimethyl_trithiolane` | T3 | ST10 | `acetaldehyde` + S1010 + `sulfur` | 2 C2H4O + 2 H2S + S → C4H8S3 + 2 H2O |
| S0710 | 2-甲基吡嗪 | `methylpyrazine` | T3 | ST34 | `ethylenediamine` + `propylene_glycol` | C2H8N2 + C3H8O2 → C5H6N2 + 2 H2O + 3 H2 |
| S0711 | 2,3-二甲基吡嗪 | `dimethylpyrazine_2_3` | T3 | ST11 | `diacetyl` + `ethylenediamine` | C4H6O2 + C2H8N2 → C6H8N2 + 2 H2O + H2 |
| S0712 | 2,5-二甲基吡嗪 | `dimethylpyrazine_2_5` | T3 | ST34 | `isopropanolamine`（M27 气相环化脱氢，`catalysts/alumina`） | 2 C3H9NO → C6H8N2 + 2 H2O + 3 H2 |
| S0714 | 对-甲苯基乙醛 | `p_tolylacetaldehyde` | T3 | ST06 | S0166（对位）+ `ethyl_chloroacetate` + `sodium_hydroxide`（达森缩合 → 皂化脱羧） | C8H8O + C4H7ClO2 + NaOH → C12H14O3 + NaCl + H2O；C12H14O3 + H2O → C9H10O + CO2 + C2H6O |
| S0715 | 2,6,6-三甲基-1 或2-环己烯-1-甲醛 | `cyclocitral` | T3 | ST06 | `citral` + `aniline` + `sulfuric_acid`（成希夫碱 → 酸催化环化 → 水解，苯胺回收） | C10H16O + C6H7N → C16H21N + H2O；C16H21N + H2O → C10H16O + C6H7N |
| S0716 | 2-异丁基-3-甲基吡嗪 | `isobutyl_methylpyrazine` | T3 | ST11 | S0875 + `ethylenediamine` | C7H12O2 + C2H8N2 → C9H14N2 + 2 H2O + H2 |
| S0717 | 2-甲氧基-3-仲丁基吡嗪 | `methoxy_sec_butylpyrazine` | T3 | ST11、ST09 | `isoleucinamide` + `glyoxal` + `methyl_chloride` + `sodium_hydroxide` | C6H14N2O + C2H2O2 → C8H12N2O + 2 H2O；C8H12N2O + CH3Cl + NaOH → C9H14N2O + NaCl + H2O |
| S0718 | 2,3-二乙基吡嗪 | `diethylpyrazine_2_3` | T3 | ST11 | S0215 + `ethylenediamine` | C6H10O2 + C2H8N2 → C8H12N2 + 2 H2O + H2 |
| S0719 | 3-乙基-2,6-二甲基吡嗪 | `ethyl_dimethylpyrazine_3_2_6` | T3 | — | S0767（M22 精馏分出 3-乙基-2,6-二甲基异构体，余下回到 S0767） | — |
| S0720 | 2-乙酰基吡嗪 | `acetylpyrazine` | T3 | ST35 | S0904 + `acetaldehyde` + `hydrogen_peroxide` + `ferrous_sulfate` | C4H4N2 + C2H4O + H2O2 → C6H6N2O + 2 H2O |
| S0721 | 2-乙酰基-3-乙基吡嗪 | `acetyl_ethylpyrazine` | T3 | ST35 | S0766 + `acetaldehyde` + `hydrogen_peroxide` + `ferrous_sulfate` | C6H8N2 + C2H4O + H2O2 → C8H10N2O + 2 H2O |
| S0722 | 2,3-二乙基-5-甲基吡嗪 | `diethyl_methylpyrazine` | T3 | ST11 | S0215 + `propylenediamine` | C6H10O2 + C3H10N2 → C9H14N2 + 2 H2O + H2 |
| S0723 | 5-异丙基-2-甲基吡嗪 | `isopropyl_methylpyrazine` | T3 | ST35 | S0710 + `isobutyric_acid` + `ammonium_persulfate`（脱羧烷基化，M22 分出 5 位异构体） | C5H6N2 + C4H8O2 + (NH4)2S2O8 → C8H12N2 + CO2 + 2 NH4HSO4 |
| S0724 | 2,6-二甲基吡啶 | `dimethylpyridine_2_6` | T3 | A ST51；B ST11 | A `coal_tar`（吡啶碱馏分）；B `acetone` + `formaldehyde` + `ammonia` | 2 C3H6O + CH2O + NH3 → C7H9N + 3 H2O + H2 |
| S0725 | 4-甲基噻唑 | `methylthiazole_4` | T3 | ST11 | S0992 + `formaldehyde` + `ammonia`（阿辛格反应得噻唑啉后脱氢） | C3H6OS + CH2O + NH3 → C4H7NS + 2 H2O；C4H7NS → C4H5NS + H2 |
| S0726 | α-甲基肉桂醛 | `alpha_methylcinnamaldehyde` | T3 | ST06 | `benzaldehyde` + S0117 | C7H6O + C3H6O → C10H10O + H2O |
| S0727 | 5-羟乙基-4-甲基噻唑 | `hydroxyethyl_methylthiazole` | T3 | ST11、ST10 | S0365 + `ethylene_oxide` + `chlorine` + `carbon_disulfide` + `ammonia` + `hydrogen_peroxide`（α-乙酰基-γ-丁内酯 → 氯化开环脱羧 → 二硫代氨基甲酸铵闭环 → 氧化脱巯基） | C6H10O3 + C2H4O → C6H8O3 + C2H6O；C6H8O3 + Cl2 → C6H7ClO3 + HCl；C6H7ClO3 + H2O → C5H9ClO2 + CO2；CS2 + 2 NH3 → CH6N2S2；C5H9ClO2 + CH6N2S2 → C6H9NOS2 + NH4Cl + H2O；C6H9NOS2 + 3 H2O2 → C6H9NOS + H2SO4 + 2 H2O |
| S0728 | 2,4,5-三甲基噻唑 | `trimethylthiazole` | T3 | ST11 | S0282 + `acetaldehyde` + `ammonia`（阿辛格反应后脱氢） | C4H8OS + C2H4O + NH3 → C6H11NS + 2 H2O；C6H11NS → C6H9NS + H2 |
| S0729 | 2-乙基-4-甲基噻唑 | `ethyl_methylthiazole` | T3 | ST11 | S0992 + S0117 + `ammonia`（阿辛格反应后脱氢） | C3H6OS + C3H6O + NH3 → C6H11NS + 2 H2O；C6H11NS → C6H9NS + H2 |
| S0730 | 5-乙烯基-4-甲基噻唑 | `methyl_vinylthiazole` | T3 | ST39 | S1260（乙酸酯热消除） | C8H11NO2S → C6H7NS + C2H4O2 |
| S0731 | 2-乙酰基噻唑 | `acetylthiazole` | T3 | ST35 | S0903 + `acetaldehyde` + `hydrogen_peroxide` + `ferrous_sulfate` | C3H3NS + C2H4O + H2O2 → C5H5NOS + 2 H2O |
| S0732 | 2-异丙基-4-甲基噻唑 | `isopropyl_methylthiazole` | T3 | ST11 | S0992 + S0184 + `ammonia`（阿辛格反应后脱氢） | C3H6OS + C4H8O + NH3 → C7H13NS + 2 H2O；C7H13NS → C7H11NS + H2 |
| S0733 | 2-异丁基噻唑 | `isobutylthiazole` | T3 | ST11 | S1125 + S0124 + `ammonia`（阿辛格反应后脱氢） | C4H8O2S2 + 2 C5H10O + 2 NH3 → 2 C7H13NS + 4 H2O；C7H13NS → C7H11NS + H2 |
| S0734 | 苯并噻唑 | `benzothiazole` | T3 | ST10、ST11 | `aniline` + `carbon_disulfide` + `sulfur` + `sodium_hydroxide` + `formic_acid`（2-巯基苯并噻唑 → 碱解得邻氨基苯硫酚 → 甲酸闭环） | C6H7N + CS2 + S → C7H5NS2 + H2S；C7H5NS2 + 5 NaOH → C6H6NNaS + Na2CO3 + Na2S + 2 H2O；C6H7NS + CH2O2 → C7H5NS + 2 H2O |
| S0735 | N-糠基吡咯 | `furfurylpyrrole` | T3 | ST23、ST09 | S0795 + `hydrochloric_acid` + S1156 + `potassium_hydroxide` | C5H6O2 + HCl → C5H5ClO + H2O；C5H5ClO + C4H5N + KOH → C9H9NO + KCl + H2O |
| S0736 | 2-乙酰基吡咯 | `acetylpyrrole` | T3 | ST15 | S1156 + `acetic_anhydride` | C4H5N + C4H6O3 → C6H7NO + C2H4O2 |
| S0737 | 5,6,7,8-四氢喹啉 | `tetrahydroquinoxaline` | T3 | ST11 | S1357 + `ethylenediamine` | C6H8O2 + C2H8N2 → C8H10N2 + 2 H2O + H2 |
| S0738 | 2,4,5-三甲基-3-噁唑啉 | `trimethyl_oxazoline` | T3 | ST11 | S0204 + `acetaldehyde` + `ammonia` | C4H8O2 + C2H4O + NH3 → C6H11NO + 2 H2O |
| S0739 | 2-甲基-4-丙基-1,3-噁唑烷 | `methyl_propyl_oxathiane` | T2（S0786 为 T3） | ST07 | S0786 + `acetaldehyde` | C6H14OS + C2H4O → C8H16OS + H2O |
| S0740 | 吡啶 | `pyridine` | T3 | A ST11；B ST51 | A `acetaldehyde` + `formaldehyde` + `ammonia`（同 110 号吡啶碱路线）；B `coal_tar`（吡啶碱馏分） | 2 C2H4O + CH2O + NH3 → C5H5N + 3 H2O + H2 |
| S0741 | 二丙基二硫醚 | `dipropyl_disulfide` | T3 | ST10 | S0801 + `hydrogen_peroxide` | 2 C3H8S + H2O2 → C6H14S2 + 2 H2O |
| S0742 | 2-戊基硫醇 | `pentanethiol_2` | T3 | ST10 | S0007 + S1010（M27 气相，`catalysts/alumina`） | C5H12O + H2S → C5H12S + H2O |
| S0743 | 邻-甲基苯硫酚 | `o_toluenethiol` | T3 | ST05 | `o_toluenesulfonyl_chloride` + `hydrogen`（`catalysts/palladium`） | C7H7ClO2S + 3 H2 → C7H8S + HCl + 2 H2O |
| S0744 | 苄基硫醇 | `benzyl_mercaptan` | T3 | ST23、ST10 | S0033 + `hydrochloric_acid` + S1010 + `sodium_hydroxide` | C7H8O + HCl → C7H7Cl + H2O；C7H7Cl + H2S + NaOH → C7H8S + NaCl + H2O |
| S0745 | 1-对-䓝烯-8-硫醇 | `p_menthene_8_thiol` | T3 | ST25 | `limonene` + S1010（`catalysts/aluminium_chloride`） | C10H16 + H2S → C10H18S |
| S0746 | 甲基丙基二硫醚 | `methyl_propyl_disulfide` | T3 | ST10 | S0801 + S0824 | C3H8S + C2H6O2S2 → C4H10S2 + CH4O2S |
| S0747 | 甲基苄基二硫醚 | `methyl_benzyl_disulfide` | T3 | ST10 | S0744 + S0824 | C7H8S + C2H6O2S2 → C8H10S2 + CH4O2S |
| S0748 | 甲基糠基二硫醚 | `methyl_furfuryl_disulfide` | T3 | ST10 | S0692 + S0824 | C5H6OS + C2H6O2S2 → C6H8OS2 + CH4O2S |
| S0749 | 烯丙基二硫醚 | `diallyl_disulfide` | T2 / T3 | A ST13；B ST10 | A N015；B `allyl_chloride` + `sodium_sulfide` + `sulfur` | 2 C3H5Cl + Na2S + S → C6H10S2 + 2 NaCl |
| S0750 | 双(2-甲基-3-呋喃基)二硫醚 | `bis_methylfuryl_disulfide` | T3（S0471 为 T4） | ST10 | S0471 + `hydrogen_peroxide` | 2 C5H6OS + H2O2 → C10H10O2S2 + 2 H2O |
| S0751 | 糠基甲基硫醚 | `furfuryl_methyl_sulfide` | T3 | ST10 | S0692 + `methyl_chloride` + `sodium_hydroxide` | C5H6OS + CH3Cl + NaOH → C6H8OS + NaCl + H2O |
| S0752 | 2,6-二甲基苯硫酚 | `dimethylthiophenol_2_6` | T3 | ST10 | S0108 + S1010（M27 气相，`catalysts/alumina`） | C8H10O + H2S → C8H10S + H2O |
| S0753 | 2-甲基-3(2-呋喃基)丙烯醛 | `methyl_furylacrolein` | T3 | ST06 | `furfural` + S0117 | C5H4O2 + C3H6O → C8H8O2 + H2O |
| S0754 | 2-甲基四氢噻吩-3-酮 | `methyltetrahydrothiophenone` | T3 | ST25、ST06 | S0837 + S0523 + `sodium_methoxide`（迈克尔加成 → 狄克曼环化 → 水解脱羧） | C5H10O2S + C5H8O2 → C10H18O4S；C10H18O4S → C8H12O3S + C2H6O；C8H12O3S + H2O → C5H8OS + CO2 + C2H6O |
| S0755 | 2-甲基-5-(甲硫基)呋喃 | `methyl_methylthiofuran_2_5` | T3 | ST15 | S0871 + S0694 + `chlorine`（甲次磺酰氯亲电取代） | C2H6S2 + Cl2 → 2 CH3ClS；C5H6O + CH3ClS → C6H8OS + HCl |
| S0756 | 2-羟基-3,5,5-三甲基-2-环己烯酮 | `hydroxy_trimethylcyclohexenone` | T3 | ST24 | `isophorone` + `hydrogen_peroxide` + `sodium_hydroxide` + `sulfuric_acid`（环氧化 → 酸催化重排） | C9H14O + H2O2 → C9H14O2 + H2O；C9H14O2 → C9H14O2 |
| S0757 | 糠酸甲酯 | `methyl_furoate` | T3 | ST04、ST01 | `furfural` + `oxygen` + `methanol` | 2 C5H4O2 + O2 → 2 C5H4O3；C5H4O3 + CH4O → C6H6O3 + H2O |
| S0758 | 硫代乙酸乙酯 | `ethyl_thioacetate` | T3 | ST10 | S0928 + `acetic_anhydride` | C2H6S + C4H6O3 → C4H8OS + C2H4O2 |
| S0759 | 硫代乙酸丙酯 | `propyl_thioacetate` | T3 | ST10 | S0801 + `acetic_anhydride` | C3H8S + C4H6O3 → C5H10OS + C2H4O2 |
| S0760 | 3-巯基丙酸乙酯 | `ethyl_mercaptopropionate_3` | T3 | ST25 | S0523 + S1010（碱催化） | C5H8O2 + H2S → C5H10O2S |
| S0761 | 硫代丁酸甲酯 | `methyl_thiobutyrate` | T3 | ST10 | `butyric_acid` + `thionyl_chloride` + S0686 | C4H8O2 + Cl2OS → C4H7ClO + SO2 + HCl；C4H7ClO + CH4S → C5H10OS + HCl |
| S0762 | 异硫氰酸烯丙酯 | `allyl_isothiocyanate` | T2 / T3 | A ST13；B ST10 | A `materials/mustard_seed`（加水酶解后 NT1 水蒸气蒸馏）；B `allyl_chloride` + `potassium_thiocyanate` | C3H5Cl + CKNS → C4H5NS + KCl |
| S0763 | 2-硫代糠酸甲酯 | `methyl_thiofuroate` | T3 | ST04、ST10 | `furfural` + `oxygen` + `thionyl_chloride` + S0686 | 2 C5H4O2 + O2 → 2 C5H4O3；C5H4O3 + Cl2OS → C5H3ClO2 + SO2 + HCl；C5H3ClO2 + CH4S → C6H6O2S + HCl |
| S0764 | 3-甲基-1,2,4-三噻烷 | `methyl_trithiane` | T3 | ST10 | S0887 + `acetaldehyde` + S1010 + `hydrogen_peroxide` | C2H6S2 + C2H4O + H2S + H2O2 → C4H8S3 + 3 H2O |
| S0765 | 2,3,5,6-四甲基吡嗪 | `tetramethylpyrazine` | T3 / T4 | A ST11；B ST18 | A S0204 + `ammonia`；B `glucose` + `ammonia` + `cultures/bacillus_subtilis` | 2 C4H8O2 + 2 NH3 → C8H12N2 + 4 H2O + H2 |
| S0766 | 2-乙基吡嗪 | `ethylpyrazine` | T3 | ST35 | S0904 + `propionic_acid` + `ammonium_persulfate`（脱羧烷基化） | C4H4N2 + C3H6O2 + (NH4)2S2O8 → C6H8N2 + CO2 + 2 NH4HSO4 |
| S0767 | 2-乙基-3,(5或6)-二甲基吡嗪 | `ethyl_dimethylpyrazine_isomers` | T3 | ST11 | S0210 + `propylenediamine`（不分离异构体） | C5H8O2 + C3H10N2 → C8H12N2 + 2 H2O + H2 |
| S0768 | 2-甲氧基-3-异丁基吡嗪 | `methoxy_isobutylpyrazine` | T3（`leucinamide` 为 T4） | ST11、ST09 | `leucinamide` + `glyoxal` + `methyl_chloride` + `sodium_hydroxide` | C6H14N2O + C2H2O2 → C8H12N2O + 2 H2O；C8H12N2O + CH3Cl + NaOH → C9H14N2O + NaCl + H2O |
| S0769 | 1-甲基-2-乙酰基吡咯 | `methyl_acetylpyrrole` | T3 | ST09 | S0736 + `methyl_chloride` + `potassium_hydroxide` | C6H7NO + CH3Cl + KOH → C7H9NO + KCl + H2O |
| S0770 | N-乙基-2-乙酰基吡咯 | `ethyl_acetylpyrrole` | T3 | ST09 | S0736 + `ethyl_chloride` + `potassium_hydroxide` | C6H7NO + C2H5Cl + KOH → C8H11NO + KCl + H2O |
| S0771 | 喹啉 | `quinoline` | T3 | A ST11；B ST51 | A `aniline` + `glycerol` + `nitrobenzene` + `sulfuric_acid`（斯克劳普合成）；B `coal_tar`（喹啉碱馏分） | 2 C6H7N + 3 C3H8O3 + C6H5NO2 → 3 C9H7N + 11 H2O |
| S0772 | 6-甲基喹啉 | `methylquinoline_6` | T3 | ST11 | `p_toluidine` + `glycerol` + `nitrobenzene` + `sulfuric_acid`（斯克劳普合成） | 3 C7H9N + 3 C3H8O3 + C6H5NO2 → 3 C10H9N + C6H7N + 11 H2O |
| S0773 | 5-甲基喹噁啉 | `methylquinoxaline_5` | T3 | ST11 | `toluene_2_3_diamine` + `glyoxal` | C7H10N2 + C2H2O2 → C9H8N2 + 2 H2O |
| S0774 | 哌啶 | `piperidine` | T3 | ST05 | S0740 + `hydrogen` | C5H5N + 3 H2 → C5H11N |
| S0775 | β-甲基吲哚 | `beta_methylindole` | T3 | ST11 | `phenylhydrazine` + S0117（`catalysts/zinc_chloride`，费歇尔吲哚合成） | C6H8N2 + C3H6O → C9H9N + NH3 + H2O |
| S0776 | 5-乙基-2-甲基吡啶 | `ethyl_methylpyridine` | T3 | ST11 | S0685 + `ammonia`（M23 液相加压，乙酸铵催化） | 4 C6H12O3 + 3 NH3 → 3 C8H11N + 12 H2O |
| S0777 | 3-乙基吡啶 | `ethylpyridine_3` | T3 | ST05 | S0779 + `hydrogen`（`catalysts/palladium`，羰基氢解） | C7H7NO + 2 H2 → C7H9N + H2O |
| S0778 | 2-乙酰基吡啶 | `acetylpyridine_2` | T3 | ST04、ST01、ST06 | `alpha_picoline` + `oxygen` + `ethanol` + `ethyl_acetate` + `sodium_methoxide`（氧化为吡啶-2-甲酸 → 酯化 → 克莱森缩合 → 水解脱羧） | 2 C6H7N + 3 O2 → 2 C6H5NO2 + 2 H2O；C6H5NO2 + C2H6O → C8H9NO2 + H2O；C8H9NO2 + C4H8O2 → C10H11NO3 + C2H6O；C10H11NO3 + H2O → C7H7NO + CO2 + C2H6O |
| S0779 | 3-乙酰基吡啶 | `acetylpyridine_3` | T3 | ST01、ST06 | `nicotinic_acid` + `ethanol` + `ethyl_acetate` + `sodium_methoxide`（酯化 → 克莱森缩合 → 水解脱羧） | C6H5NO2 + C2H6O → C8H9NO2 + H2O；C8H9NO2 + C4H8O2 → C10H11NO3 + C2H6O；C10H11NO3 + H2O → C7H7NO + CO2 + C2H6O |
| S0780 | 甲酸肉桂酯 | `cinnamyl_formate` | T2（S0042 为 T3） | ST01 | S0042 + `formic_acid` | C9H10O + CH2O2 → C10H10O2 + H2O |
| S0781 | 异戊胺 | `isopentylamine` | T3 | ST34 | `isoamyl_alcohol` + `ammonia` + `hydrogen`（`catalysts/nickel`） | C5H12O + NH3 → C5H13N + H2O |
| S0782 | 苯乙胺 | `phenethylamine` | T3 | ST34 | S0034 + `ammonia` + `hydrogen`（`catalysts/nickel`） | C8H10O + NH3 → C8H11N + H2O |
| S0783 | 2-甲基-1,3-二硫环戊烷 | `methyl_dithiolane` | T2（S0887 为 T3） | ST07 | `acetaldehyde` + S0887 | C2H4O + C2H6S2 → C4H8S2 + H2O |
| S0784 | 6-乙酰氧基二氢茶螺烷 | `acetoxydihydrotheaspirane` | T3 | ST02 | S0671 + `acetic_anhydride` | C13H24O2 + C4H6O3 → C15H26O3 + C2H4O2 |
| S0785 | 4,5-二甲基噻唑 | `dimethylthiazole_4_5` | T3 | ST11 | S0282 + `formaldehyde` + `ammonia`（阿辛格反应后脱氢） | C4H8OS + CH2O + NH3 → C5H9NS + 2 H2O；C5H9NS → C5H7NS + H2 |
| S0786 | 3-巯基己醇 | `mercaptohexanol_3` | T3 | ST25、ST05 | S0131 + S1010 + `hydrogen` | C6H10O + H2S → C6H12OS；C6H12OS + H2 → C6H14OS |
| S0787 | 三硫丙酮 | `trithioacetone` | T3 | ST10 | `acetone` + S1010（盐酸、`catalysts/zinc_chloride` 催化） | 3 C3H6O + 3 H2S → C9H18S3 + 3 H2O |
| S0788 | 2,6-二甲基吡嗪 | `dimethylpyrazine_2_6` | T3 | ST34 | `propylenediamine` + `propylene_glycol`（M22 与 2,5-异构体分离） | C3H10N2 + C3H8O2 → C6H8N2 + 2 H2O + 3 H2 |
| S0789 | 2-(甲硫基)乙酸乙酯 | `ethyl_methylthioacetate` | T3 | ST10 | `ethyl_chloroacetate` + S0686 + `sodium_hydroxide` | C4H7ClO2 + CH4S + NaOH → C5H10O2S + NaCl + H2O |
| S0790 | 乙酸3-巯基己酯 | `mercaptohexyl_acetate` | T3 | ST02 | S0786 + `acetic_anhydride`（酸催化，O-乙酰化） | C6H14OS + C4H6O3 → C8H16O2S + C2H4O2 |
| S0791 | 2-(甲基二硫基)丙酸乙酯 | `ethyl_methyldithiopropionate` | T3 | ST10 | S0837 + S0824 | C5H10O2S + C2H6O2S2 → C6H12O2S2 + CH4O2S |
| S0792 | 3-(甲硫基)丁酸乙酯 | `ethyl_methylthiobutyrate` | T3 | ST25 | S0526 + S0686 | C6H10O2 + CH4S → C7H14O2S |
| S0793 | 丁酸3-巯基己酯 | `mercaptohexyl_butyrate` | T2（S0786 为 T3） | ST01 | S0786 + `butyric_acid` | C6H14OS + C4H8O2 → C10H20O2S + H2O |
| S0794 | 己酸3-巯基己酯 | `mercaptohexyl_hexanoate` | T2（S0786 为 T3） | ST01 | S0786 + `hexanoic_acid` | C6H14OS + C6H12O2 → C12H24O2S + H2O |
| S0795 | 糠醇 | `furfuryl_alcohol` | T3 | ST05 | `furfural` + `hydrogen`（`catalysts/copper`） | C5H4O2 + H2 → C5H6O2 |
| S0796 | 四氢糠醇 | `tetrahydrofurfuryl_alcohol` | T3 | ST05 | S0795 + `hydrogen`（`catalysts/nickel`） | C5H6O2 + 2 H2 → C5H10O2 |
| S0798 | 2-乙基-3-甲基吡嗪 | `ethyl_methylpyrazine_2_3` | T3 | ST11 | S0210 + `ethylenediamine` | C5H8O2 + C2H8N2 → C7H10N2 + 2 H2O + H2 |
| S0799 | 3-甲基-2-丁硫醇 | `methyl_butanethiol_3_2` | T3 | ST05、ST10 | `methyl_isopropenyl_ketone` + `hydrogen` + S1010（加氢得 3-甲基-2-丁酮 → 硫化氢还原硫醇化，硫化态催化剂） | C5H8O + H2 → C5H10O；C5H10O + H2S + H2 → C5H12S + H2O |
| S0800 | 2-甲基-3-四氢呋喃硫醇 | `methyltetrahydrofuranthiol` | T3 | ST10 | S0275 + S1010 + `hydrogen`（还原硫醇化，硫化态催化剂） | C5H8O2 + H2S + H2 → C5H10OS + H2O |
| S0801 | 丙硫醇 | `propanethiol` | T3 | ST10 | `n_propanol` + S1010（M27 气相，`catalysts/alumina`） | C3H8O + H2S → C3H8S + H2O |

### 1.7 序号 801–950（S0802–S0951，146 条，含硫化合物、杂环与增补品种）

| 编码 | 名称 | 物料 ID | 阶段 | 模板 | 原料 | 关键反应 |
| --- | --- | --- | --- | --- | --- | --- |
| S0802 | 1,3-丙二硫醇 | `propane_1_3_dithiol` | T3 | ST10 | S0803 + S1010（自由基加成） | C3H6S + H2S → C3H8S2 |
| S0803 | 烯丙基硫醇 | `allyl_mercaptan` | T3 | ST10 | `allyl_chloride` + S1010 + `sodium_hydroxide` | C3H5Cl + H2S + NaOH → C3H6S + NaCl + H2O |
| S0804 | 4-甲氧基-2-甲基-2-丁硫醇 | `methoxy_methylbutanethiol` | T3 | ST10 | `prenyl_methyl_ether` + S1010（酸催化） | C6H12O + H2S → C6H14OS |
| S0805 | 2-苯乙硫醇 | `phenylethyl_mercaptan` | T3 | ST10 | S0679 + S1010（自由基加成） | C8H8 + H2S → C8H10S |
| S0806 | 3-巯基-3-甲基-1-丁醇 | `mercapto_3_methylbutanol` | T3 | ST10 | S0956 + S1010（酸催化） | C5H10O + H2S → C5H12OS |
| S0807 | 甲基2-甲基-3-呋喃基二硫醚 | `methyl_methylfuryl_disulfide` | T3（S0471 为 T4） | ST10 | S0471 + S0824 | C5H6OS + C2H6O2S2 → C6H8OS2 + CH4O2S |
| S0808 | 甲基乙基硫醚 | `methyl_ethyl_sulfide` | T3 | ST10 | S0928 + `methyl_chloride` + `sodium_hydroxide` | C2H6S + CH3Cl + NaOH → C3H8S + NaCl + H2O |
| S0809 | 甲基苯基二硫醚 | `methyl_phenyl_disulfide` | T3 | ST10 | S0892 + S0824 | C6H6S + C2H6O2S2 → C7H8S2 + CH4O2S |
| S0810 | 二乙基硫醚 | `diethyl_sulfide` | T3 | ST10 | S0928 + `ethanol`（M27 气相，`catalysts/alumina`） | C2H6S + C2H6O → C4H10S + H2O |
| S0811 | 二丙基三硫醚 | `dipropyl_trisulfide` | T3 | ST10 | S0801 + `sulfur_dichloride` | 2 C3H8S + Cl2S → C6H14S3 + 2 HCl |
| S0812 | 丙烯基丙基二硫醚 | `propenyl_propyl_disulfide` | T3 | ST30 | S1022 | C6H12S2 → C6H12S2 |
| S0813 | 二烯丙基硫醚 | `diallyl_sulfide` | T3 | ST10 | `allyl_chloride` + `sodium_sulfide` | 2 C3H5Cl + Na2S → C6H10S + 2 NaCl |
| S0814 | 二烯丙基三硫醚 | `diallyl_trisulfide` | T2 / T3 | A ST13；B ST10 | A N015；B S0749 + `sulfur`（M14 加硫） | B：C6H10S2 + S → C6H10S3 |
| S0815 | 二烯丙基四硫醚 | `diallyl_tetrasulfide` | T3 | ST10 | S0814 + `sulfur`（M14 加硫） | C6H10S3 + S → C6H10S4 |
| S0816 | 2-甲硫甲基-2-丁烯醛 | `methylthiomethyl_butenal` | T3 | ST06 | S0700 + S0115 | C4H8OS + C2H4O → C6H10OS + H2O |
| S0817 | 3-甲硫基己醛 | `methylthio_hexanal` | T3 | ST25 | S0131 + S0686 | C6H10O + CH4S → C7H14OS |
| S0818 | 乙酸环己酯 | `cyclohexyl_acetate` | T2 | ST01 | `cyclohexanol` + S0293 | C6H12O + C2H4O2 → C8H14O2 + H2O |
| S0819 | 邻-氨基苯乙酮 | `o_aminoacetophenone` | T3 | ST05 | `o_nitroacetophenone` + `hydrogen`（`catalysts/palladium`） | C8H7NO3 + 3 H2 → C8H9NO + 2 H2O |
| S0820 | 2-甲基-3-甲硫基呋喃 | `methyl_methylthio_furan` | T3（S0471 为 T4） | ST10 | S0471 + `methyl_chloride` + `sodium_hydroxide` | C5H6OS + CH3Cl + NaOH → C6H8OS + NaCl + H2O |
| S0821 | 甲酸3-巯基3-甲基丁酯 | `mercapto_methylbutyl_formate` | T2（S0806 为 T3） | ST01 | S0806 + S0346 | C5H12OS + CH2O2 → C6H12O2S + H2O |
| S0822 | 乙酸3-甲硫基丙酯 | `methylthiopropyl_acetate` | T2（S0687 为 T3） | ST01 | S0687 + S0293 | C4H10OS + C2H4O2 → C6H12O2S + H2O |
| S0823 | 3-甲基硫代丁酸S-甲酯 | `s_methyl_thioisovalerate` | T3 | ST10 | S0303 + `thionyl_chloride` + S0686 | C5H10O2 + Cl2OS → C5H9ClO + SO2 + HCl；C5H9ClO + CH4S → C6H12OS + HCl |
| S0824 | 甲硫磺酸S-甲酯 | `s_methyl_methanethiosulfonate` | T3 | ST24 | S0694 + `hydrogen_peroxide` | C2H6S2 + 2 H2O2 → C2H6O2S2 + 2 H2O |
| S0825 | 2-甲硫基丁酸甲酯 | `methyl_methylthiobutyrate` | T3 | ST54、ST01、ST10 | S0296 + `chlorine` + `methanol` + S0686 + `sodium_hydroxide` | C4H8O2 + Cl2 → C4H7ClO2 + HCl；C4H7ClO2 + CH4O → C5H9ClO2 + H2O；C5H9ClO2 + CH4S + NaOH → C6H12O2S + NaCl + H2O |
| S0826 | 3-甲硫基-1-己醇乙酸酯 | `methylthiohexyl_acetate` | T2（S0690 为 T3） | ST01 | S0690 + S0293 | C7H16OS + C2H4O2 → C9H18O2S + H2O |
| S0827 | 甲硫醇乙酸酯 | `s_methyl_thioacetate` | T3 | ST10 | S0686 + `acetic_anhydride` | CH4S + C4H6O3 → C3H6OS + C2H4O2 |
| S0828 | (5H)-5-甲基-6,7-二氢环戊基并(b)吡嗪 | `methyl_dihydrocyclopentapyrazine` | T3 | ST11 | S0212 + `ethylenediamine` | C6H8O2 + C2H8N2 → C8H10N2 + 2 H2O + H2 |
| S0829 | 2-甲氧基吡嗪 | `methoxypyrazine` | T3 | ST09 | `chloropyrazine` + `sodium_methoxide` | C4H3ClN2 + CH3NaO → C5H6N2O + NaCl |
| S0830 | 2-,5或6-甲氧基-3-甲基吡嗪 | `methoxy_methylpyrazine` | T3 | ST09 | `chloromethylpyrazine` + `sodium_methoxide` | C5H5ClN2 + CH3NaO → C6H8N2O + NaCl |
| S0831 | 2-乙酰基-3,5(或6)-二甲基吡嗪 | `acetyl_dimethylpyrazine` | T3 | ST35 | S0712 + S0788 + S0115 + `hydrogen_peroxide` + `ferrous_sulfate` | C6H8N2 + C2H4O + H2O2 → C8H10N2O + 2 H2O |
| S0832 | 2-乙酰基3-甲基吡嗪 | `acetyl_methylpyrazine` | T3 | ST35 | S0710 + S0115 + `hydrogen_peroxide` + `ferrous_sulfate` | C5H6N2 + C2H4O + H2O2 → C7H8N2O + 2 H2O |
| S0833 | 四氢吡咯(吡咯烷) | `pyrrolidine` | T3 | ST05 | S0630 + `hydrogen` + `ammonia`（M23，`catalysts/nickel`） | C4H6O2 + 2 H2 → C4H10O2；C4H10O2 + NH3 → C4H9N + 2 H2O |
| S0834 | 2-异丁基吡啶 | `isobutylpyridine` | T3 | ST35 | S0740 + S0303 + `ammonium_persulfate` | C5H5N + C5H10O2 + (NH4)2S2O8 → C9H13N + CO2 + (NH4)2SO4 + H2SO4 |
| S0835 | 2-乙基-4,5-二甲基噁唑 | `ethyl_dimethyloxazole` | T3 | ST01、ST11 | S0204 + S0294 + `ammonia` | C4H8O2 + C3H6O2 → C7H12O3 + H2O；C7H12O3 + NH3 → C7H11NO + 2 H2O |
| S0836 | 硫化铵 | `ammonium_sulfide` | T2（S1010 为 T3） | ST17 | S1010 + `ammonia`（水中吸收） | H2S + 2 NH3 → (NH4)2S |
| S0837 | 2-巯基丙酸乙酯 | `ethyl_mercaptopropionate` | T3 | ST54、ST01、ST10 | S0294 + `chlorine` + `ethanol` + S1010 + `sodium_hydroxide` | C3H6O2 + Cl2 → C3H5ClO2 + HCl；C3H5ClO2 + C2H6O → C5H9ClO2 + H2O；C5H9ClO2 + H2S + NaOH → C5H10O2S + NaCl + H2O |
| S0838 | N-(4-羟基-3-甲氧基苄基)壬酰胺 | `nonivamide` | T3 | ST20 | `vanillylamine` + S0310 | C8H11NO2 + C9H18O2 → C17H27NO3 + H2O |
| S0839 | 1,4-二噻烷 | `dithiane_1_4` | T3 | ST10 | S0887 + `ethylene_dichloride` + `sodium_hydroxide` | C2H6S2 + C2H4Cl2 + 2 NaOH → C4H8S2 + 2 NaCl + 2 H2O |
| S0840 | 桃金娘烯醇 | `myrtenol` | T3 | ST31 | S0664 + S0003 | C10H14O + C3H8O → C10H16O + C3H6O |
| S0841 | 胡椒碱 | `piperine` | T2 | ST21 | N270（乙醇重结晶） | — |
| S0842 | 2,3-二甲基苯并呋喃 | `dimethylbenzofuran` | T3 | ST09、ST11 | S0099 + `chlorobutanone` + `sodium_hydroxide` + `phosphoric_acid`（环化） | C6H6O + C4H7ClO + NaOH → C10H12O2 + NaCl + H2O；C10H12O2 → C10H10O + H2O |
| S0843 | 4-羟基-5-甲基-3(2H)-呋喃酮 | `norfuraneol` | T2 | ST12 | S0076 + S0324 | C5H10O5 → C5H6O3 + 2 H2O |
| S0844 | γ-紫罗兰酮 | `gamma_ionone` | T3 | ST06 | S0174 + S0247 + `sulfuric_acid`（低温环化，精馏取 γ 馏分） | C10H16O + C3H6O → C13H20O + H2O |
| S0845 | α-二氢紫罗兰酮 | `alpha_dihydroionone` | T3 | ST05 | S0234 + `hydrogen`（`catalysts/palladium`，侧链选择加氢） | C13H20O + H2 → C13H22O |
| S0846 | d-胡椒酮 | `d_piperitone` | T2 | ST13 | N151 | — |
| S0847 | 胡椒烯酮 | `piperitenone` | T2 | ST13 | N199 | — |
| S0848 | L-天冬氨酸 | `l_aspartic_acid` | — | ST26 | 已有：`l_aspartic_acid` | — |
| S0849 | DL-异亮氨酸 | `dl_isoleucine` | T3 | ST54 | S0320 + `chlorine` + `ammonia`（α-氯代后氨解，同 180 B） | C6H12O2 + Cl2 → C6H11ClO2 + HCl；C6H11ClO2 + 2 NH3 → C6H13NO2 + NH4Cl |
| S0850 | 焦木酸提取物 | `pyroligneous_acid_extract` | T2 | NT4 | N267 + `ethyl_acetate` | — |
| S0853 | 琥珀酸二钠 | `disodium_succinate` | — | ST26 | 已有：178（`disodium_succinate`） | — |
| S0854 | 5’-鸟苷酸二钠 | `guanylic_disodium` | — | ST26 | 已有：176（`guanylic_disodium`） | — |
| S0855 | 5’-肌苷酸二钠 | `inosinic_disodium` | — | ST26 | 已有：177（`inosinic_disodium`） | — |
| S0857 | δ-十六内酯 | `delta_hexadecalactone` | T3 | ST24 | `undecylcyclopentanone` + `hydrogen_peroxide` | C16H30O + H2O2 → C16H30O2 + H2O |
| S0858 | (+/-)二氢薄荷内酯 | `dihydromintlactone` | T3（S0641 为 T4） | ST05 | S0641 + `hydrogen` | C10H14O2 + H2 → C10H16O2 |
| S0859 | 顺式-4-十二烯醛 | `z4_dodecenal` | T3 | ST29 | S0141 + S0630 + `hydrochloric_acid` + `triphenylphosphine` + `thionyl_chloride` + `hydrogen`（末步罗森蒙德加氢） | C4H6O2 + HCl → C4H7ClO2；C4H7ClO2 + C18H15P → C22H22ClO2P；C22H22ClO2P + C8H16O → C12H22O2 + C18H15OP + HCl；C12H22O2 + Cl2OS → C12H21ClO + SO2 + HCl；C12H21ClO + H2 → C12H22O + HCl |
| S0860 | 4,5-环氧反式-2-癸烯醛 | `epoxy_decenal` | T3 | ST24 | S0154 + `hydrogen_peroxide` + S0293（过乙酸） | C10H16O + H2O2 → C10H16O2 + H2O |
| S0861 | 2-乙基-5-甲基吡嗪 | `ethyl_methylpyrazine` | T3 | ST16 | S0712 + `sodium` + `ammonia` + `methyl_chloride`（氨基钠侧链金属化） | 2 Na + 2 NH3 → 2 NaNH2 + H2；C6H8N2 + NaNH2 + CH3Cl → C7H10N2 + NaCl + NH3 |
| S0862 | 顺式-3-顺式-6-壬二烯-1-醇 | `cis_cis_nonadienol` | T4 | ST18 | `materials/flaxseed` + `materials/melon` + `cultures/yeast` | C18H30O2 + O2 → C18H30O4；C18H30O4 → C9H14O + C9H16O3；C9H14O + H2 → C9H16O |
| S0863 | 2-甲基-1-丁醇 | `active_amyl_alcohol` | T2 | ST13 | N402 | — |
| S0864 | 异龙脑 | `isoborneol` | T3 | ST03 | S0622 + `methanol` | C12H20O2 + CH4O → C10H18O + C3H6O2 |
| S0865 | 2-壬醇 | `nonan_2_ol` | T3 | ST05 | S0220 + `hydrogen` | C9H18O + H2 → C9H20O |
| S0866 | 反式-2-辛烯-1-醇 | `trans_2_octenol` | T3 | ST31 | S0142 + S0003 | C8H14O + C3H8O → C8H16O + C3H6O |
| S0867 | 香芹醇 | `carveol` | T3 | ST31 | S1133 + S0003 | C10H14O + C3H8O → C10H16O + C3H6O |
| S0868 | 对-䓝烷-2-酮 | `carvomenthone` | T3 | ST05 | S1133 + `hydrogen` | C10H14O + 2 H2 → C10H18O |
| S0869 | 4-甲基-3-戊烯-2-酮 | `mesityl_oxide` | T3 | ST06 | S0247（`potassium_hydroxide` 碱缩合，与 S0267 同一配方的联产：二聚物为本品，三聚物为 S0267） | 2 C3H6O → C6H10O + H2O |
| S0870 | 反式,反式-3,5-辛二烯-2-酮 | `octadienone` | T3 | ST06 | S0126 + S0247 | C5H8O + C3H6O → C8H12O + H2O |
| S0871 | 2-甲基呋喃 | `methylfuran` | T3 | ST05 | S0795 + `hydrogen`（M23 气相，`catalysts/copper`，氢解脱水） | C5H6O2 + H2 → C5H6O + H2O |
| S0872 | 3-癸烯-2-酮 | `decenone` | T3 | ST06 | S0136 + S0247 | C7H14O + C3H6O → C10H18O + H2O |
| S0873 | 2-辛烯-4-酮 | `octen_4_one` | T3 | ST16、ST04 | `crotonaldehyde` + S0004 + `hydrochloric_acid` + `magnesium` + `oxygen` | C4H10O + HCl → C4H9Cl + H2O；C4H9Cl + Mg → C4H9ClMg；C4H6O + C4H9ClMg + HCl → C8H16O + MgCl2；2 C8H16O + O2 → 2 C8H14O + 2 H2O |
| S0874 | 2-呋喃基-2-丙酮 | `furyl_acetone` | T3 | ST06、ST05 | S0180 + `nitroethane` + `hydrogen` | C5H4O2 + C2H5NO2 → C7H7NO3 + H2O；C7H7NO3 + 2 H2 → C7H9NO2 + H2O；C7H9NO2 + H2O → C7H8O2 + H3NO |
| S0875 | 5-甲基-2,3-己二酮 | `methyl_hexanedione` | T3 | ST04 | S1431 + `oxygen`（`catalysts/copper`） | 2 C7H14O2 + O2 → 2 C7H12O2 + 2 H2O |
| S0876 | 2-甲基-3-戊烯酸 | `methyl_pentenoic_acid` | T3 | ST30 | S0302 | C6H10O2 → C6H10O2 |
| S0877 | L-酪氨酸 | `l_tyrosine` | — | ST26 | 见 546 | — |
| S0878 | 2-氧代戊二酸 | `oxoglutaric_acid` | T4 | ST18 | `glucose` + `cultures/yarrowia`（限硫胺素） | C6H12O6 + 2 O2 → C5H6O5 + CO2 + 3 H2O |
| S0879 | 4-茴香酸 | `anisic_acid` | T3 | ST04 | S0163 + `oxygen` | 2 C8H8O2 + O2 → 2 C8H8O3 |
| S0880 | 亚油酸 | `linoleic_acid` | T3 | ST55 | `soybean_fatty_acids` + `urea` + `methanol`（M15 尿素包合、冷冻结晶除去饱和酸与油酸，P102）→ M22 减压精馏 | 前段（以三亚油酸甘油酯计）：C57H98O6 + 3 H2O → 3 C18H32O2 + C3H8O3 |
| S0881 | 甘草酸 | `glycyrrhizic_acid` | — | ST26 | 已有：`glycyrrhizic_acid` | — |
| S0882 | L-胱氨酸 | `l_cystine` | — | ST26 | 已有：`l_cystine` | — |
| S0883 | L-蛋氨酸 | `l_methionine` | — | ST26 | 见 545 | — |
| S0884 | L-谷氨酰胺 | `l_glutamine` | T4 | ST18 | `glucose` + `ammonia` + `cultures/corynebacterium_glutamine`（高铵、限锌） | C6H12O6 + 2 NH3 + 3/2 O2 → C5H10N2O3 + CO2 + 4 H2O |
| S0885 | 2-丙硫醇 | `isopropyl_mercaptan` | T3 | ST10 | `propene` + S1010（酸催化） | C3H6 + H2S → C3H8S |
| S0886 | 4-巯基-4-甲基-2-戊酮 | `mercapto_methylpentanone` | T3 | ST25 | S0869 + S1010 | C6H10O + H2S → C6H12OS |
| S0887 | 1,2-乙二硫醇 | `ethanedithiol` | T3 | ST10 | `ethylene_dichloride` + S1010 + `sodium_hydroxide` | C2H4Cl2 + 2 H2S + 2 NaOH → C2H6S2 + 2 NaCl + 2 H2O |
| S0888 | 异戊烯基硫醇 | `prenyl_mercaptan` | T3 | ST23、ST10 | S0956 + `hydrochloric_acid` + S1010 + `sodium_hydroxide` | C5H10O + HCl → C5H9Cl + H2O；C5H9Cl + H2S + NaOH → C5H10S + NaCl + H2O |
| S0889 | d,l-(3-氨基-3-羧基丙基)二甲基氯化锍 | `methylmethionine_sulfonium_chloride` | T3 | ST10 | S0327 + `methyl_chloride` | C5H11NO2S + CH3Cl → C6H14ClNO2S |
| S0890 | 2-甲基-3-硫代乙酰氧基-4,5-二氢呋喃 | `methyl_thioacetoxy_dihydrofuran` | T3 | ST10 | S0275 + S1010 + `acetic_anhydride` | C5H8O2 + H2S → C5H8OS + H2O；C5H8OS + C4H6O3 → C7H10O2S + C2H4O2 |
| S0891 | 异丁基硫醇 | `isobutyl_mercaptan` | T3 | ST10 | S0005 + S1010（M27 气相，氧化铝） | C4H10O + H2S → C4H10S + H2O |
| S0892 | 苯硫酚(原名称为苄基硫醇) | `benzenethiol` | T3 | ST10 | `chlorobenzene` + S1010（M27 气相高温） | C6H5Cl + H2S → C6H6S + HCl |
| S0893 | 异硫氰酸苄酯 | `benzyl_isothiocyanate` | T1 | ST53 | `materials/papaya`（籽，B 酶解法） | C14H18KNO9S2 + H2O → C8H7NS + C6H12O6 + KHSO4 |
| S0894 | 甲基烯丙基三硫醚 | `allyl_methyl_trisulfide` | T3 | ST10 | S0803 + S0686 + `sulfur_dichloride` | C3H6S + CH4S + Cl2S → C4H8S3 + 2 HCl |
| S0895 | 2-戊基噻吩 | `pentylthiophene` | T3 | ST15、ST56 | S0897 + S0300 + `thionyl_chloride` + `zinc_chloride` + `hydrazine_hydrate` + `potassium_hydroxide` | C5H10O2 + Cl2OS → C5H9ClO + SO2 + HCl；C4H4S + C5H9ClO → C9H12OS + HCl；C9H12OS + N2H4 → C9H14S + N2 + H2O |
| S0896 | 3,5-二乙基-1,2,4-三硫杂环戊烷 | `diethyl_trithiolane` | T3 | ST11 | S0117 + S1010 + `sulfur` | 2 C3H6O + 2 H2S + S → C6H12S3 + 2 H2O |
| S0897 | 噻吩 | `thiophene` | T3 | ST11 | `butane` + `sulfur`（M27 气相，560 °C，`catalysts/alumina`） | C4H10 + 4 S → C4H4S + 3 H2S |
| S0898 | 2,4,6-三甲基二氢-4H-1,3,5-二噻嗪 | `trimethyl_dithiazine` | T3 | ST11 | S0115 + `ammonia` + S1010 | 3 C2H4O + NH3 + 2 H2S → C6H13NS2 + 3 H2O |
| S0899 | 异硫氰酸3-甲硫基丙酯 | `methylthiopropyl_isothiocyanate` | T3 | ST53 | `methylthiopropylamine` + `carbon_disulfide` + `sodium_hydroxide` + `hydrogen_peroxide` | C4H11NS + CS2 + NaOH → C5H10NNaS3 + H2O；C5H10NNaS3 + H2O2 → C5H9NS2 + S + NaOH + H2O |
| S0900 | 3-甲基丁基硫醇 | `isoamyl_mercaptan` | T3 | ST10 | S0008 + S1010（M27 气相，氧化铝） | C5H12O + H2S → C5H12S + H2O |
| S0901 | 2-乙酰基-2-噻唑啉 | `acetyl_thiazoline` | T2（S0964 为 T3） | ST12 | S0323 + S0964 | 2 C3H7NO2S + 2 C3H4O2 + O2 → 2 C5H7NOS + 2 CO2 + 4 H2O |
| S0902 | 甲基丙基三硫醚 | `methyl_propyl_trisulfide` | T3 | ST10 | S0686 + S0801 + `sulfur_dichloride` | CH4S + C3H8S + Cl2S → C4H10S3 + 2 HCl |
| S0903 | 噻唑 | `thiazole` | T3 | ST11 | `chloroacetaldehyde` + `thioformamide` | C2H3ClO + CH3NS → C3H3NS + HCl + H2O |
| S0904 | 吡嗪 | `pyrazine` | T3 | ST11 | `ethylenediamine` + `ethylene_glycol`（M27 气相，`catalysts/copper`） | C2H8N2 + C2H6O2 → C4H4N2 + 2 H2O + 3 H2 |
| S0905 | 甲基1-丙烯基二硫醚 | `methyl_propenyl_disulfide` | T3 | ST30 | S1013 | C4H8S2 → C4H8S2 |
| S0906 | 甲酸丙酯 | `propyl_formate` | T2 | ST01 | S0049 + S0346 | C3H8O + CH2O2 → C4H8O2 + H2O |
| S0907 | 香兰素3-(l-䓝氧基)丙-1,2-二醇缩醛 | `vanillin_menthoxypropanediol_acetal` | T2（S1296 为 T3） | ST07 | S0172 + S1296 | C8H8O3 + C13H26O3 → C21H32O5 + H2O |
| S0908 | 3-戊烯-2-酮 | `pentenone` | T3 | ST06 | S0115 + S0247 | C2H4O + C3H6O → C5H8O + H2O |
| S0909 | 十二酸甲酯（月桂酸甲酯） | `methyl_laurate` | T2（S0312 为 T3） | ST01 | S0312 + `methanol` | C12H24O2 + CH4O → C13H26O2 + H2O |
| S0910 | 乙酸紫苏酯 | `perillyl_acetate` | T3 | ST02 | S0065 + `acetic_anhydride` | C10H16O + C4H6O3 → C12H18O2 + C2H4O2 |
| S0911 | 苹果酸二乙酯 | `diethyl_malate` | T2 | ST01 | `malic_acid` + `ethanol` | C4H6O5 + 2 C2H6O → C8H14O5 + 2 H2O |
| S0912 | 甲硫基乙酸甲酯 | `methyl_methylthioacetate` | T3 | ST01、ST10 | `chloroacetic_acid` + `methanol` + S0686 + `sodium_hydroxide` | C2H3ClO2 + CH4O → C3H5ClO2 + H2O；C3H5ClO2 + CH4S + NaOH → C4H8O2S + NaCl + H2O |
| S0913 | 2-乙酰基-1-吡咯啉 | `acetyl_pyrroline` | T3 | ST05、ST04 | S0736 + `hydrogen` + `oxygen`（末步温和氧化） | C6H7NO + 3 H2 → C6H13NO；C6H13NO + O2 → C6H9NO + 2 H2O |
| S0914 | 甲酸异丙酯 | `isopropyl_formate` | T2（S0003 为 T3） | ST01 | S0003 + S0346 | C3H8O + CH2O2 → C4H8O2 + H2O |
| S0915 | 4-甲基-2-戊烯醛 | `methyl_pentenal` | T3 | ST06 | S0184 + S0115 | C4H8O + C2H4O → C6H10O + H2O |
| S0916 | 亚油酸乙酯 | `ethyl_linoleate` | T2（S0880 为 T3） | ST01 | S0880 + `ethanol` | C18H32O2 + C2H6O → C20H36O2 + H2O |
| S0917 | 2,4,6-三异丁基-5,6-二氢-4H-1,3,5-二噻嗪 | `triisobutyl_dithiazine` | T3 | ST11 | S0124 + `ammonia` + S1010 | 3 C5H10O + NH3 + 2 H2S → C15H31NS2 + 3 H2O |
| S0918 | 乙酸十二醇酯 | `dodecyl_acetate` | T2 | ST01 | S0024 + S0293 | C12H26O + C2H4O2 → C14H28O2 + H2O |
| S0919 | 2-乙基丁醛 | `ethylbutyraldehyde` | T3 | ST06、ST05 | S0119 + S0115 + `hydrogen`（`catalysts/palladium`） | C4H8O + C2H4O → C6H10O + H2O；C6H10O + H2 → C6H12O |
| S0920 | 辛酸辛酯 | `octyl_octanoate` | T2（S0014 为 T3） | ST01 | S0309 + S0014 | C8H16O2 + C8H18O → C16H32O2 + H2O |
| S0921 | 己醛二乙缩醛 | `hexanal_diethyl_acetal` | T2（S0130 为 T3） | ST07 | S0130 + `ethanol` | C6H12O + 2 C2H6O → C10H22O2 + H2O |
| S0922 | 丙酸异丙酯 | `isopropyl_propionate` | T2（S0003 为 T3） | ST01 | S0294 + S0003 | C3H6O2 + C3H8O → C6H12O2 + H2O |
| S0923 | 丁酸反式-2-己烯酯 | `trans_2_hexenyl_butyrate` | T2（S0011 为 T3） | ST01 | S0296 + S0011 | C4H8O2 + C6H12O → C10H18O2 + H2O |
| S0924 | 异硫氰酸丁酯 | `butyl_isothiocyanate` | T3 | ST53 | S1111 + `carbon_disulfide` + `sodium_hydroxide` + `hydrogen_peroxide` | C4H11N + CS2 + NaOH → C5H10NNaS2 + H2O；C5H10NNaS2 + H2O2 → C5H9NS + S + NaOH + H2O |
| S0925 | N-葡糖酰基乙醇胺 | `gluconyl_ethanolamine` | T3 | ST20 | `glucono_delta_lactone` + `ethanolamine` | C6H10O6 + C2H7NO → C8H17NO7 |
| S0926 | N-乳酰基乙醇胺 | `lactoyl_ethanolamine` | T3 | ST20 | S0498 + `ethanolamine` | C5H10O3 + C2H7NO → C5H11NO3 + C2H6O |
| S0927 | 1-庚烯-3-醇 | `hepten_3_ol` | T3 | ST16 | `acrolein` + S0004 + `hydrochloric_acid` + `magnesium` | C4H10O + HCl → C4H9Cl + H2O；C4H9Cl + Mg → C4H9ClMg；C3H4O + C4H9ClMg + HCl → C7H14O + MgCl2 |
| S0928 | 乙硫醇 | `ethanethiol` | T3 | ST10 | `ethanol` + S1010（M27 气相，氧化铝） | C2H6O + H2S → C2H6S + H2O |
| S0930 | 乙酸l-龙脑酯 | `l_bornyl_acetate` | T2 | ST13 | N186（松针油精馏，与 S0385 B、S1105 同一配方的联产） | — |
| S0931 | 反式-α-突厥酮 | `trans_alpha_damascone` | T3 | — | S0230（M22 精馏分出反式体，顺式回流） | — |
| S0932 | 二乙基二硫醚 | `diethyl_disulfide` | T3 | ST10 | S0928 + `hydrogen_peroxide` | 2 C2H6S + H2O2 → C4H10S2 + 2 H2O |
| S0933 | 2,5-二甲基-3(2H)-呋喃酮 | `dimethylfuranone` | T3 | ST06、ST11 | S0247 + S0498 + `sodium_methoxide` + `sulfuric_acid`（环化） | C3H6O + C5H10O3 → C6H10O3 + C2H6O；C6H10O3 → C6H8O2 + H2O |
| S0934 | 香叶酸 | `geranic_acid` | T3 | ST04 | S0174 + `oxygen` | 2 C10H16O + O2 → 2 C10H16O2 |
| S0935 | 1-(3-羟基-5-甲基-2-噻吩)乙酮 | `hydroxy_methylthienyl_ethanone` | T3 | ST11 | S0992 + `ethyl_chlorocrotonate` + `sodium_methoxide` | C3H6OS + C6H9ClO2 → C7H8O2S + HCl + C2H6O |
| S0936 | 异黄葵内酯 | `isoambrettolide` | T3 | ST08 | `aleuritic_acid` + `trimethyl_orthoformate` + `acetic_anhydride` + `potassium_hydroxide` + `glycerol`（解聚成环） | C16H32O5 + C4H10O3 → C18H34O6 + 2 CH4O；C18H34O6 → C16H30O3 + CO2 + CH4O；C16H30O3 → C16H28O2 + H2O |
| S0937 | 异丁酸异龙脑酯 | `isobornyl_isobutyrate` | T3 | ST14 | S0657 + S0297 | C10H16 + C4H8O2 → C14H24O2 |
| S0938 | N-甲基邻氨基苯甲酸异丁酯 | `isobutyl_n_methylanthranilate` | T3 | ST03 | S0552 + S0005 | C9H11NO2 + C4H10O → C12H17NO2 + CH4O |
| S0939 | 丁酸3-(甲硫基)丙酯 | `methionyl_butyrate` | T2（S0687 为 T3） | ST01 | S0687 + S0296 | C4H10OS + C4H8O2 → C8H16O2S + H2O |
| S0940 | (S1)-甲氧基-3-庚硫醇 | `methoxy_heptanethiol` | T3 | ST04、ST25、ST10 | S0927 + `oxygen` + `methanol` + S1010 + `hydrogen`（末步临氢巯基化） | 2 C7H14O + O2 → 2 C7H12O + 2 H2O；C7H12O + CH4O → C8H16O2；C8H16O2 + H2S + H2 → C8H18OS + H2O |
| S0941 | 5-Z-辛烯酸甲酯 | `methyl_z5_octenoate` | T3 | ST28、ST01 | `cis_3_hexenyl_chloride`（F02 中间体，由 S0027 制）+ S0403 + `sodium` + `ethanol` + `methanol` | C6H11Cl + C7H12O4 + C2H5NaO → C13H22O4 + NaCl + C2H6O；C13H22O4 + 2 H2O → C8H14O2 + CO2 + 2 C2H6O；C8H14O2 + CH4O → C9H16O2 + H2O |
| S0942 | N-乙酰基邻氨基苯甲酸甲酯 | `methyl_n_acetylanthranilate` | T3 | ST20 | S0491 + `acetic_anhydride` | C8H9NO2 + C4H6O3 → C10H11NO3 + C2H4O2 |
| S0943 | 3-甲基-2-(3-甲基-2-丁烯)呋喃 | `rosefuran` | T3 | ST16 | S0180 + `methallyl_chloride` + `magnesium` + `trimethyl_orthoacetate`（约翰逊–克莱森重排、酸异构化、水解脱羧） | C4H7Cl + Mg → C4H7ClMg；C5H4O2 + C4H7ClMg + HCl → C9H12O2 + MgCl2；C9H12O2 + C5H12O3 → C12H16O3 + 2 CH4O；C12H16O3 + H2O → C10H14O + CO2 + CH4O |
| S0944 | 乙酸植醇酯 | `phytyl_acetate` | T3 | ST02 | S1077 + `acetic_anhydride` | C20H40O + C4H6O3 → C22H42O2 + C2H4O2 |
| S0945 | 3,7,11-三甲基十二碳-2,6,10-三烯醇乙酸酯 | `farnesyl_acetate` | T3 | ST02 | S0038 + `acetic_anhydride` | C15H26O + C4H6O3 → C17H28O2 + C2H4O2 |
| S0946 | 三乙胺 | `triethylamine` | T3 | ST05 | `ethanol` + `ammonia` + `hydrogen`（M23 临氢胺化，`catalysts/nickel`） | 3 C2H6O + NH3 → C6H15N + 3 H2O |
| S0947 | 丙酸茴香酯 | `anisyl_propionate` | T2（S0041 为 T3） | ST01 | S0041 + S0294 | C8H10O2 + C3H6O2 → C11H14O3 + H2O |
| S0948 | 丁酸3-丁酮-2-醇酯 | `acetoin_butyrate` | T2（S0204 为 T3） | ST01 | S0204 + S0296 | C4H8O2 + C4H8O2 → C8H14O3 + H2O |
| S0949 | 异喹啉 | `isoquinoline` | T2 | ST13 | `coal_tar`（喹啉馏分，硫酸盐分步结晶） | — |
| S0950 | 2-丙酰噻唑 | `propionylthiazole` | T3 | ST35 | S0903 + S0117 + `hydrogen_peroxide` + `ferrous_sulfate` | C3H3NS + C3H6O + H2O2 → C6H7NOS + 2 H2O |
| S0951 | 2(4)-异丙基-4(2),6-二甲基二氢(4H)-1,3,5-二噻嗪 | `isopropyl_dimethyl_dithiazine` | T3 | ST11 | S0115 + S0184 + `ammonia` + S1010 | 2 C2H4O + C4H8O + NH3 + 2 H2S → C8H17NS2 + 3 H2O |

### 1.8 序号 951–1100（S0952–S1101，150 条，历年增补品种（一））

| 编码 | 名称 | 物料 ID | 阶段 | 模板 | 原料 | 关键反应 |
| --- | --- | --- | --- | --- | --- | --- |
| S0952 | 丁酸松油酯 | `terpinyl_butyrate` | T3 | ST02 | S0037 + `butyric_anhydride` | C10H18O + C8H14O3 → C14H24O2 + C4H8O2 |
| S0953 | 3-正丁基苯酞 | `butylphthalide` | T3 | ST05 | S0640 + `hydrogen` | C12H12O2 + H2 → C12H14O2 |
| S0954 | 2,2-二甲基-5-(1-甲基-1-丙烯基)四氢呋喃 | `dimethyl_methylpropenyl_thf` | T3 | ST23、ST16、ST09（未确认） | `isoprenol` + `thionyl_chloride` + `magnesium` + S0121（格氏加成后酸催化环化成醚） | C5H10O + Cl2OS → C5H9Cl + SO2 + HCl；C5H9Cl + Mg → C5H9ClMg；C5H8O + C5H9ClMg + H2O → C10H18O + ClHMgO；C10H18O → C10H18O |
| S0955 | (6R)-3-甲基-6-(1-甲基乙基)-2-环己烯-1-酮 | `r_piperitone` | T2 | ST13 | NT1 `materials/eucalyptus_dives` | — |
| S0956 | 3-甲基-2-丁烯-1-醇 | `prenol` | T3 | ST05 | `isoprenol` + `hydrogen`（`catalysts/palladium`，氢气氛下双键内移） | C5H10O → C5H10O |
| S0957 | 对-䓝-1-烯-9-醇乙酸酯 | `p_menthenyl_acetate` | T3 | ST05、ST02 | S0179 + `hydrogen`（`catalysts/copper`，只还原醛基）+ `acetic_anhydride` | C10H16O + H2 → C10H18O；C10H18O + C4H6O3 → C12H20O2 + C2H4O2 |
| S0958 | 乙酸2-辛烯醇酯 | `octenyl_acetate` | T3 | ST02 | S0866 + `acetic_anhydride` | C8H16O + C4H6O3 → C10H18O2 + C2H4O2 |
| S0959 | 1-(对-甲氧基苯基)-2-丙酮 | `p_methoxyphenylacetone` | T3 | ST24 | S1497 + `hydrogen_peroxide` + `formic_acid`（过甲酸环氧化后酸催化重排） | C10H12O + H2O2 → C10H12O2 + H2O |
| S0960 | 十八酸丁酯（硬脂酸丁酯） | `butyl_stearate` | T2（S0004 为 T3） | ST01 | `stearic_acid` + S0004 | C18H36O2 + C4H10O → C22H44O2 + H2O |
| S0961 | (+/-)-1-苯乙基硫醇 | `alpha_methylbenzyl_mercaptan` | T3 | ST10 | S0032 + S1010（M27 气相，`catalysts/alumina`） | C8H10O + H2S → C8H10S + H2O |
| S0962 | 4-异丙基-2-环己烯酮 | `cryptone` | T3 | ST06 | S0124 + `methyl_vinyl_ketone`（迈克尔加成后分子内羟醛缩合） | C5H10O + C4H6O → C9H14O + H2O |
| S0963 | 邻-甲氧基苯甲酸甲酯 | `methyl_o_methoxybenzoate` | T3 | ST09 | S0505 + `methyl_chloride` + `sodium_hydroxide` | C8H8O3 + CH3Cl + NaOH → C9H10O3 + NaCl + H2O |
| S0964 | 丙酮醛 | `pyruvaldehyde` | T3 | ST04 | `propylene_glycol` + `oxygen`（M27 气相氧化脱氢，`catalysts/silver`） | C3H8O2 + O2 → C3H4O2 + 2 H2O |
| S0965 | 甲基乙基三硫醚 | `methyl_ethyl_trisulfide` | T3 | ST10 | S0686 + S0928 + `sulfur_dichloride` | CH4S + C2H6S + Cl2S → C3H8S3 + 2 HCl |
| S0966 | 2-甲基-2-(甲二硫基)-丙醛 | `methyldithio_isobutyraldehyde` | T3 | ST10 | S0686 + `sulfur_dichloride` + S0184（甲基二硫氯对醛 α 位取代） | CH4S + Cl2S → CH3ClS2 + HCl；C4H8O + CH3ClS2 → C5H10OS2 + HCl |
| S0967 | 二(甲硫基)甲烷 | `bis_methylthio_methane` | T3 | ST07 | `formaldehyde` + S0686（二硫缩醛） | CH2O + 2 CH4S → C3H8S2 + H2O |
| S0968 | 2,3,5-三硫杂己烷 | `trithiahexane` | T3 | ST10 | S1449 + S0824 | C2H6S2 + C2H6O2S2 → C3H8S3 + CH4O2S |
| S0969 | 4-乙基辛酸 | `ethyloctanoic_acid` | T3 | ST23、ST28 | S0052 + `thionyl_chloride` + S0403 + `sodium` + `ethanol` | C8H18O + Cl2OS → C8H17Cl + SO2 + HCl；2 Na + 2 C2H6O → 2 C2H5NaO + H2；C8H17Cl + C7H12O4 + C2H5NaO → C15H28O4 + NaCl + C2H6O；C15H28O4 + 2 H2O → C10H20O2 + CO2 + 2 C2H6O |
| S0970 | 二氢诺卡酮 | `dihydronootkatone` | T3 | ST05 | S0223 + `hydrogen`（`catalysts/palladium`，只加氢异丙烯基） | C15H22O + H2 → C15H24O |
| S0971 | 1-乙氧基-3-甲基-2-丁烯 | `prenyl_ethyl_ether` | T3 | ST23、ST09 | S0956 + `hydrochloric_acid` + `ethanol` + `sodium_hydroxide` | C5H10O + HCl → C5H9Cl + H2O；C5H9Cl + C2H6O + NaOH → C7H14O + NaCl + H2O |
| S0972 | 2-乙烯基-2-甲基-5-(1-甲基乙烯基)四氢呋喃 | `dehydroxy_linalool_oxide` | T3 | ST14 | S0030（酸催化脱水） | C10H18O2 → C10H16O + H2O |
| S0973 | 异戊酸糠酯 | `furfuryl_isovalerate` | T3 | ST03 | S0536 + S0795 | C6H12O2 + C5H6O2 → C10H14O3 + CH4O |
| S0974 | 异戊酸芳樟酯 | `linalyl_isovalerate` | T3 | ST32 | S0303 + `thionyl_chloride` + S0029 | C5H10O2 + Cl2OS → C5H9ClO + SO2 + HCl；C5H9ClO + C10H18O → C15H26O2 + HCl |
| S0975 | 3-甲基-2-丁醇 | `methyl_isopropyl_carbinol` | T3 | ST05 | `methyl_isopropyl_ketone` + `hydrogen` | C5H10O + H2 → C5H12O |
| S0976 | 3-甲基-1-戊醇 | `methylpentanol` | T3 | ST40 | S0320 + `hydrogen`（`catalysts/copper`） | C6H12O2 + 2 H2 → C6H14O + H2O |
| S0977 | 4-甲基-2-戊酮 | `methyl_isobutyl_ketone` | T3 | ST05 | S0869 + `hydrogen`（`catalysts/palladium`） | C6H10O + H2 → C6H12O |
| S0978 | 反式-3-顺式-6-壬二烯醇 | `nonadienol_3e_6z` | T3（`nonenynol` 为 T4） | ST05 | `nonenynol` + `sodium` + `ammonia`（钠–液氨还原炔键得反式） | C9H14O + 2 Na + 2 NH3 → C9H16O + 2 H2NNa |
| S0979 | 庚酸甲酯 | `methyl_heptanoate` | T2（S0308 为 T3） | ST01 | S0308 + `methanol` | C7H14O2 + CH4O → C8H16O2 + H2O |
| S0980 | 顺式-3-己烯醇丙酸酯 | `cis_hexenyl_propionate` | T2（S0027 为 T3） | ST01 | S0027 + `propionic_acid` | C6H12O + C3H6O2 → C9H16O2 + H2O |
| S0981 | 反式-2-癸烯酸乙酯 | `ethyl_trans_2_decenoate` | T2（S1104 为 T3） | ST01 | S1104 + `ethanol` | C10H18O2 + C2H6O → C12H22O2 + H2O |
| S0982 | 2-乙基苯酚 | `o_ethylphenol` | T3 | ST15 | `phenol` + `ethylene`（`catalysts/aluminium_chloride`，邻位烷基化，P72） | C6H6O + C2H4 → C8H10O |
| S0983 | 盐酸硫胺素 | `thiamine_hydrochloride` | — | ST26 | 已有：109（`thiamine_hydrochloride`） | — |
| S0984 | N-甲基吡咯-2-甲醛 | `methylpyrrole_carboxaldehyde` | T3 | ST09、ST15 | S1156 + `methyl_chloride` + `sodium_hydroxide` + `dimethylformamide` + `thionyl_chloride`（维尔斯迈尔甲酰化） | C4H5N + CH3Cl + NaOH → C5H7N + NaCl + H2O；C5H7N + C3H7NO → C6H7NO + C2H7N |
| S0985 | 乙酸香兰素酯 | `vanillin_acetate` | T3 | ST02 | `vanillin` + `acetic_anhydride` | C8H8O3 + C4H6O3 → C10H10O4 + C2H4O2 |
| S0986 | L-组氨酸 | `l_histidine` | T4 | ST18 | `glucose` + `ammonium_sulfate` + `cultures/corynebacterium`（组氨酸高产株） | 葡萄糖经组氨酸合成途径发酵（总式不计） |
| S0987 | δ-突厥酮 | `delta_damascone` | T3 | ST52、ST06 | `piperylene` + S0869（`catalysts/aluminium_chloride`）+ `acetaldehyde` | C5H8 + C6H10O → C11H18O；C11H18O + C2H4O → C13H20O + H2O |
| S0988 | 2-甲基戊酸乙酯 | `ethyl_methylpentanoate` | T2（S0301 为 T3） | ST01 | S0301 + `ethanol` | C6H12O2 + C2H6O → C8H16O2 + H2O |
| S0989 | 4-甲硫基-2-丁酮 | `methylthioethyl_methyl_ketone` | T3 | ST25 | `methyl_vinyl_ketone` + S0686 | C4H6O + CH4S → C5H10OS |
| S0990 | 乳酸l-薄荷酯 | `menthyl_lactate` | T2 | ST01 | `menthol` + `lactic_acid` | C10H20O + C3H6O3 → C13H24O3 + H2O |
| S0991 | 甲基3-甲基-1-丁烯基二硫醚 | `methyl_methylbutenyl_disulfide` | T3 | ST10 | S0124 + S1010 + S0824（烯硫醇与甲硫基磺酸酯交换） | C5H10O + H2S → C5H10S + H2O；C5H10S + C2H6O2S2 → C6H12S2 + CH4O2S |
| S0992 | 1-巯基-2-丙酮 | `mercaptoacetone` | T3 | ST23、ST10 | `acetone` + `chlorine` + S1010 + `sodium_hydroxide` | C3H6O + Cl2 → C3H5ClO + HCl；C3H5ClO + H2S + NaOH → C3H6OS + NaCl + H2O |
| S0993 | 乙酸正戊酯 | `amyl_acetate` | T2（S0006 为 T3） | ST01 | S0006 + `acetic_acid` | C5H12O + C2H4O2 → C7H14O2 + H2O |
| S0994 | 胡薄荷酮 | `pulegone` | T2 | ST13 | N199 | — |
| S0995 | 1-苯基丙醇-1 | `alpha_ethylbenzyl_alcohol` | T3 | ST15、ST05 | `benzene` + `propionic_anhydride`（`catalysts/aluminium_chloride`）+ `hydrogen` | C6H6 + C6H10O3 → C9H10O + C3H6O2；C9H10O + H2 → C9H12O |
| S0996 | 4-苯基2-丁醇 | `methyl_phenethyl_carbinol` | T3 | ST05 | S0206 + `hydrogen`（`catalysts/nickel`，碳碳双键与羰基一并加氢） | C10H10O + 2 H2 → C10H14O |
| S0997 | 庚醇-3 | `ethyl_butyl_carbinol` | T3 | ST05 | S0255 + `hydrogen` | C7H14O + H2 → C7H16O |
| S0998 | 3-乙酰氧基己酸甲酯 | `methyl_acetoxyhexanoate` | T3 | ST02 | S0605 + `acetic_anhydride` | C7H14O3 + C4H6O3 → C9H16O4 + C2H4O2 |
| S0999 | 对-䓝-1-烯-3-醇 | `piperitol` | T3 | ST31 | S0846 + S0003 + `aluminium_isopropoxide` | C10H16O + C3H8O → C10H18O + C3H6O |
| S1000 | 4-苧醇 | `sabinene_hydrate` | T2 | ST13 | N211 | — |
| S1001 | 丙酮酸顺式-3-己烯酯（丙酮酸叶醇酯） | `cis_hexenyl_pyruvate` | T2（S0027、S0295 为 T3） | ST01 | S0295 + S0027 | C3H4O3 + C6H12O → C9H14O3 + H2O |
| S1002 | 联苯 | `biphenyl` | T3 | ST04 | `benzene`（M27 高温脱氢偶联，P82） | 2 C6H6 → C12H10 + H2 |
| S1003 | 顺式-4-羟基-6-十二烯酸内酯 | `z_dodecenolactone` | T4 | ST18 | S0880 + `cultures/lactobacillus`（亚油酸水合）+ `cultures/yarrowia`（β-氧化，P76） | C18H32O2 + H2O → C18H34O3；C18H34O3 + 3 O2 → C12H22O3 + 3 C2H4O2；C12H22O3 → C12H20O2 + H2O |
| S1004 | 甲基亚磺酰甲烷 | `dimethyl_sulfoxide` | T3 | ST04 | S0693 + `oxygen`（`nitrogen_dioxide` 催化液相氧化，P44） | 2 C2H6S + O2 → 2 C2H6OS |
| S1005 | 3,7-二甲基-2,6-辛二烯酸甲酯（香叶酸甲酯） | `methyl_geranate` | T2（S0934 为 T3） | ST01 | S0934 + `methanol` | C10H16O2 + CH4O → C11H18O2 + H2O |
| S1006 | 反式和顺式-4,8-二甲基-3,7-壬二烯-2-酮 | `dimethylnonadienone` | T3 | ST06 | `acetone` + S0218 | C3H6O + C8H14O → C11H18O + H2O |
| S1007 | 异亚戊基异戊胺 | `isopentylidene_isopentylamine` | T3 | ST06 | S0124 + S0781（脱水缩合成亚胺） | C5H10O + C5H13N → C10H21N + H2O |
| S1008 | 戊酸异戊酯 | `isoamyl_valerate` | T2（S0300 为 T3） | ST01 | S0300 + `isoamyl_alcohol` | C5H10O2 + C5H12O → C10H20O2 + H2O |
| S1009 | 丙酸反式-2-己烯酯 | `trans_hexenyl_propionate` | T2（S0011 为 T3） | ST01 | S0011 + `propionic_acid` | C6H12O + C3H6O2 → C9H16O2 + H2O |
| S1010 | 硫化氢(仅用于热反应香料) | `hydrogen_sulfide` | T3 | ST10 | `hydrogen` + `sulfur`（M27 气相催化，P82；与 F10 中间体 `hydrogen_sulfide` 为同一物料） | H2 + S → H2S |
| S1011 | 戊酸甲酯 | `methyl_valerate` | T2（S0300 为 T3） | ST01 | S0300 + `methanol` | C5H10O2 + CH4O → C6H12O2 + H2O |
| S1012 | 丁酸异丙酯 | `isopropyl_butyrate` | T2（S0003 为 T3） | ST01 | `butyric_acid` + S0003 | C4H8O2 + C3H8O → C7H14O2 + H2O |
| S1013 | 烯丙基甲基二硫醚 | `allyl_methyl_disulfide` | T3 | ST10 | S0803 + S0824 | C3H6S + C2H6O2S2 → C4H8S2 + CH4O2S |
| S1014 | 3-壬酮 | `ethyl_hexyl_ketone` | T3 | ST27 | `propionic_acid`（过量）+ S0308 | C3H6O2 + C7H14O2 → C9H18O + CO2 + H2O |
| S1015 | 二苄基二硫醚 | `dibenzyl_disulfide` | T3 | ST10 | S0744 + `hydrogen_peroxide` | 2 C7H8S + H2O2 → C14H14S2 + 2 H2O |
| S1016 | 苯乙酸顺式-3-己烯酯（苯乙酸叶醇酯） | `cis_hexenyl_phenylacetate` | T2（S0027、S0316 为 T3） | ST01 | S0316 + S0027 | C8H8O2 + C6H12O → C14H18O2 + H2O |
| S1017 | 乙酸3-(乙酰巯基)己酯 | `acetylmercaptohexyl_acetate` | T3 | ST02 | S0786 + `acetic_anhydride` + `sodium_acetate`（碱催化、加热，巯基与羟基同时乙酰化） | C6H14OS + 2 C4H6O3 → C10H18O3S + 2 C2H4O2 |
| S1018 | 己酸甲硫醇酯 | `s_methyl_hexanethioate` | T3 | ST32 | `hexanoic_acid` + `thionyl_chloride` + S0686 | C6H12O2 + Cl2OS → C6H11ClO + SO2 + HCl；C6H11ClO + CH4S → C7H14OS + HCl |
| S1019 | 反式-2-丁烯酸（巴豆酸） | `crotonic_acid` | T3 | ST04 | `crotonaldehyde` + `oxygen`（液相空气氧化，P44） | 2 C4H6O + O2 → 2 C4H6O2 |
| S1020 | 戊酸顺式-3-己烯酯（戊酸叶醇酯） | `cis_hexenyl_valerate` | T2（S0027、S0300 为 T3） | ST01 | S0300 + S0027 | C5H10O2 + C6H12O → C11H20O2 + H2O |
| S1021 | 己酸苄酯 | `benzyl_hexanoate` | T2（S0033 为 T3） | ST01 | `hexanoic_acid` + S0033 | C6H12O2 + C7H8O → C13H18O2 + H2O |
| S1022 | 烯丙基丙基二硫醚 | `allyl_propyl_disulfide` | T3 | ST10 | S0803 + S0801 + `hydrogen_peroxide`（交叉氧化后精馏分出） | C3H6S + C3H8S + H2O2 → C6H12S2 + 2 H2O |
| S1023 | 2,8-表硫-顺式-对-䓝烷 4,7,7-三甲基-6-硫杂双环[3.2.1]辛烷 硫代桉叶素 | `thiocineole` | T3 | ST10 | `limonene` + `sulfur` + S0670（γ-松油烯供氢，副产 S0674） | C10H16 + S + C10H16 → C10H18S + C10H14 |
| S1024 | 癸酸甲酯 | `methyl_decanoate` | T2 | ST01 | `capric_acid` + `methanol` | C10H20O2 + CH4O → C11H22O2 + H2O |
| S1025 | 甲酸异丁酯 | `isobutyl_formate` | T2 | ST01 | `formic_acid` + S0005 | CH2O2 + C4H10O → C5H10O2 + H2O |
| S1026 | 4-庚酮 | `dipropyl_ketone` | T3 | ST27 | `butyric_acid` | 2 C4H8O2 → C7H14O + CO2 + H2O |
| S1027 | 戊酸丁酯 | `butyl_valerate` | T2（S0004、S0300 为 T3） | ST01 | S0300 + S0004 | C5H10O2 + C4H10O → C9H18O2 + H2O |
| S1028 | 丁酸环己酯 | `cyclohexyl_butyrate` | T2 | ST01 | `butyric_acid` + `cyclohexanol` | C4H8O2 + C6H12O → C10H18O2 + H2O |
| S1029 | 山梨酸乙酯 | `ethyl_sorbate` | T2 | ST01 | `sorbic_acid` + `ethanol` | C6H8O2 + C2H6O → C8H12O2 + H2O |
| S1030 | 单油酸甘油酯 | `glyceryl_monooleate` | T3 | ST03 | `triolein` + `glycerol`（甘油解，P42） | C57H104O6 + 2 C3H8O3 → 3 C21H40O4 |
| S1031 | 5-羟基-4-辛酮 | `butyroin` | T3 | ST06 | S0119（盐酸硫胺 109 催化偶姻缩合） | 2 C4H8O → C8H16O2 |
| S1032 | 壬酸甲酯 | `methyl_nonanoate` | T2（S0310 为 T3） | ST01 | S0310 + `methanol` | C9H18O2 + CH4O → C10H20O2 + H2O |
| S1033 | 丙酸橙花酯 | `neryl_propionate` | T2（S0046 为 T3） | ST01 | S0046 + `propionic_acid` | C10H18O + C3H6O2 → C13H22O2 + H2O |
| S1034 | 肉桂酸丙酯 | `propyl_cinnamate` | T2（S0318 为 T3） | ST01 | S0318 + `n_propanol` | C9H8O2 + C3H8O → C12H14O2 + H2O |
| S1035 | 丁酸玫瑰酯 | `rhodinyl_butyrate` | T2 | ST01 | S0036 + `butyric_acid` | C10H20O + C4H8O2 → C14H26O2 + H2O |
| S1036 | 异丁酸玫瑰酯 | `rhodinyl_isobutyrate` | T2 | ST01 | S0036 + `isobutyric_acid` | C10H20O + C4H8O2 → C14H26O2 + H2O |
| S1037 | 丙酸松油酯 | `terpinyl_propionate` | T3 | ST02 | S0037 + `propionic_anhydride` | C10H18O + C6H10O3 → C13H22O2 + C3H6O2 |
| S1038 | 丙酸糠酯 | `furfuryl_propionate` | T3 | ST03 | S0522 + S0795 | C4H8O2 + C5H6O2 → C8H10O3 + CH4O |
| S1039 | 戊酸糠酯 | `furfuryl_valerate` | T3 | ST03 | S1011 + S0795 | C6H12O2 + C5H6O2 → C10H14O3 + CH4O |
| S1040 | 异茉莉酮 | `isojasmone` | T3 | ST30 | S1265（酸催化双键内移） | C11H18O → C11H18O |
| S1041 | 苄基甲基硫醚 | `benzyl_methyl_sulfide` | T3 | ST10 | S0744 + `methyl_chloride` + `sodium_hydroxide` | C7H8S + CH3Cl + NaOH → C8H10S + NaCl + H2O |
| S1042 | 3-甲基-2-丁烯醛 | `prenal` | T3 | ST04 | S0956 + `oxygen`（M27 气相氧化脱氢，`catalysts/silver`） | 2 C5H10O + O2 → 2 C5H8O + 2 H2O |
| S1043 | 2,4-癸二烯酸丙酯 | `propyl_decadienoate` | T3 | ST03 | S0482 + `n_propanol` | C12H20O2 + C3H8O → C13H22O2 + C2H6O |
| S1044 | 反式-2-己烯酸己酯 | `hexyl_trans_hexenoate` | T2（S0010、S0306 为 T3） | ST01 | S0306 + S0010 | C6H10O2 + C6H14O → C12H22O2 + H2O |
| S1045 | 4-烯丙基-2,6-二甲氧基苯酚 | `methoxyeugenol` | T3（S0109 为 T4） | ST09 | S0109 + `allyl_chloride` + `sodium_hydroxide`（成烯丙基醚后加热克莱森重排至对位） | C8H10O3 + C3H5Cl + NaOH → C11H14O3 + NaCl + H2O；C11H14O3 → C11H14O3 |
| S1046 | 2-羟基-4-甲基戊酸甲酯 | `methyl_leucate` | T3（S0326 为 T4） | ST01 | S0326 + `sodium_nitrite` + `sulfuric_acid`（重氮化水解，P68）+ `methanol` | C6H13NO2 + HNO2 → C6H12O3 + N2 + H2O；C6H12O3 + CH4O → C7H14O3 + H2O |
| S1047 | 反式-2-辛烯酸甲酯 | `methyl_trans_octenoate` | T3 | ST03 | S0598 + `methanol` | C10H18O2 + CH4O → C9H16O2 + C2H6O |
| S1048 | 2,2,6-三甲基-6-乙烯基四氢吡喃 | `trimethyl_vinyl_tetrahydropyran` | T3 | ST14 | S0029（酸催化分子内环化） | C10H18O → C10H18O |
| S1049 | 香紫苏内酯 | `sclareolide` | T3（`sclareol` 为 T4） | ST24 | `sclareol` + `hydrogen_peroxide`（侧链氧化降解后内酯化，游戏简化） | C20H36O2 + 3 H2O2 → C16H26O2 + C4H6O + 5 H2O |
| S1050 | 苯甲酸甲硫醇酯 | `s_methyl_thiobenzoate` | T3 | ST32 | `benzoic_acid` + `thionyl_chloride` + S0686 | C7H6O2 + Cl2OS → C7H5ClO + SO2 + HCl；C7H5ClO + CH4S → C8H8OS + HCl |
| S1051 | 反式-2-己烯酸顺式-3-己烯酯 | `cis_hexenyl_trans_hexenoate` | T2（S0027、S0306 为 T3） | ST01 | S0306 + S0027 | C6H10O2 + C6H12O → C12H20O2 + H2O |
| S1052 | 2-巯基苯甲醚 | `o_methoxythiophenol` | T3 | ST10 | `guaiacol` + S1010（M27 气相，`catalysts/alumina`，酚羟基换巯基） | C7H8O2 + H2S → C7H8OS + H2O |
| S1053 | 香兰素苏和赤-2,3-丁二醇缩醛 | `vanillin_butanediol_acetal` | T2（`butane_2_3_diol` 为 T3） | ST07 | `vanillin` + `butane_2_3_diol` | C8H8O3 + C4H10O2 → C12H16O4 + H2O |
| S1054 | 反式6-甲基-3-庚烯-2-酮 | `isoamylidene_acetone` | T3 | ST06 | S0124 + `acetone` | C5H10O + C3H6O → C8H14O + H2O |
| S1055 | (±)3-巯基丁酸乙酯 | `ethyl_mercaptobutyrate` | T3 | ST25 | S0526 + S1010 | C6H10O2 + H2S → C6H12O2S |
| S1056 | 3-巯基-2-甲基戊醇 | `mercapto_2_methylpentanol` | T3 | ST25、ST05 | S0127 + S1010 + `hydrogen` | C6H10O + H2S → C6H12OS；C6H12OS + H2 → C6H14OS |
| S1057 | 乙醛二异戊醇缩醛 | `acetaldehyde_diisoamyl_acetal` | T2 | ST07 | `acetaldehyde` + `isoamyl_alcohol` | C2H4O + 2 C5H12O → C12H26O2 + H2O |
| S1058 | (+/-)-2-苯基-4-甲基-2-己烯醛 | `phenyl_methylhexenal` | T3 | ST06 | S0168 + S0120 | C8H8O + C5H10O → C13H16O + H2O |
| S1059 | 2-庚硫醇 | `sec_heptyl_mercaptan` | T3 | ST10 | S0053 + S1010（M27 气相，`catalysts/alumina`） | C7H16O + H2S → C7H16S + H2O |
| S1060 | 2-(2-羟基-4-甲基-3-环己烯基)-丙酸γ-内酯 | `wine_lactone` | T3 | ST04、ST24、ST08 | S0179 + `oxygen` + `hydrogen_peroxide`（醛氧化成酸，烯丙位羟基化后内酯化） | 2 C10H16O + O2 → 2 C10H16O2；C10H16O2 + H2O2 → C10H14O2 + 2 H2O |
| S1061 | l-䓝基甲基醚 | `menthyl_methyl_ether` | T3 | ST09 | `menthol` + `methyl_chloride` + `sodium_hydroxide` | C10H20O + CH3Cl + NaOH → C11H22O + NaCl + H2O |
| S1062 | 己酸异丙酯 | `isopropyl_hexanoate` | T2（S0003 为 T3） | ST01 | `hexanoic_acid` + S0003 | C6H12O2 + C3H8O → C9H18O2 + H2O |
| S1063 | 2,4-己二烯-1-醇 | `sorbyl_alcohol` | T3 | ST31 | S0135 + S0003 + `aluminium_isopropoxide` | C6H8O + C3H8O → C6H10O + C3H6O |
| S1064 | 十六烷酸甲酯 | `methyl_palmitate` | T2 | ST01 | `palmitic_acid` + `methanol` | C16H32O2 + CH4O → C17H34O2 + H2O |
| S1065 | 5-甲基-2-噻吩甲醛 | `methylthiophene_carboxaldehyde` | T3 | ST11、ST15 | S0871 + S1010（M27 气相，`catalysts/alumina`）+ `dimethylformamide` + `thionyl_chloride`（维尔斯迈尔甲酰化） | C5H6O + H2S → C5H6S + H2O；C5H6S + C3H7NO → C6H6OS + C2H7N |
| S1066 | 4-甲基-2,6-二甲氧基苯酚 | `methylsyringol` | T3 | ST05 | S1127 + `hydrogen`（`catalysts/palladium`，醛基氢解为甲基） | C9H10O4 + 2 H2 → C9H12O3 + H2O |
| S1067 | 对-甲氧基肉桂醛 | `p_methoxycinnamaldehyde` | T3 | ST06 | S0163 + `acetaldehyde` | C8H8O2 + C2H4O → C10H10O2 + H2O |
| S1068 | 2,4,5-三甲基噁唑 | `trimethyloxazole` | T3 | ST02、ST11 | S0204 + `acetic_anhydride` + `ammonia` | C4H8O2 + C4H6O3 → C6H10O3 + C2H4O2；C6H10O3 + NH3 → C6H9NO + 2 H2O |
| S1069 | 苯甲醛二乙缩醛 | `benzaldehyde_diethyl_acetal` | T2 | ST07 | `benzaldehyde` + `ethanol` | C7H6O + 2 C2H6O → C11H16O2 + H2O |
| S1070 | d-新薄荷醇 | `d_neomenthol` | T3 | ST05 | `menthone` + `hydrogen`（与薄荷醇同出，M22 精馏分离） | C10H18O + H2 → C10H20O |
| S1071 | 2-壬烯酸γ-内酯 | `pentyl_furanone` | T3 | ST23 | S0627 + `chlorine` + `sodium_hydroxide`（α-氯代后碱消除） | C9H16O2 + Cl2 → C9H15ClO2 + HCl；C9H15ClO2 + NaOH → C9H14O2 + NaCl + H2O |
| S1072 | 反式-4-癸烯酸乙酯 | `ethyl_trans_4_decenoate` | T2（S1491 为 T3） | ST01 | S1491 + `ethanol` | C10H18O2 + C2H6O → C12H22O2 + H2O |
| S1073 | 晚香玉内酯{二氢-5-[(Z,Z)-2,5-辛二烯-2(3H)-呋喃酮} | `tuberose_lactone` | T4 | ST18 | S0564 + `cultures/lactobacillus` + `cultures/yarrowia`（同 S1003，底物换亚麻酸） | C19H32O2 + H2O → C18H30O2 + CH4O；C18H30O2 + 3 O2 → C12H18O2 + 3 C2H4O2 |
| S1074 | 4-甲基-2-戊基-1,3-二氧戊环 | `hexanal_pg_acetal` | T2（S0130 为 T3） | ST07 | S0130 + `propylene_glycol` | C6H12O + C3H8O2 → C9H18O2 + H2O |
| S1075 | 乙酸3-巯基庚酯 | `mercaptoheptyl_acetate` | T3 | ST25、ST05、ST01 | S0138 + S1010 + `hydrogen` + `acetic_acid`（酸催化下只酯化羟基） | C7H12O + H2S → C7H14OS；C7H14OS + H2 → C7H16OS；C7H16OS + C2H4O2 → C9H18O2S + H2O |
| S1076 | 甲基纤维素 | `methylcellulose` | — | ST26 | 已有：314（`methylcellulose`） | — |
| S1077 | 植醇(3,7,11,15-四甲基-2-十六烯-1-醇) | `phytol` | T2 | ST21 | `chlorophyll_a` + `sodium_hydroxide`（皂化去植醇，P41；叶绿素铜钠盐线副产） | C55H72MgN4O5 + 3 NaOH → C34H31MgN4Na3O6 + C20H40O + CH4O |
| S1078 | 异戊醛二乙缩醛 | `isovaleraldehyde_diethyl_acetal` | T2（S0124 为 T3） | ST07 | S0124 + `ethanol` | C5H10O + 2 C2H6O → C9H20O2 + H2O |
| S1079 | 异硫氰酸3-丁烯酯 | `butenyl_isothiocyanate` | T2 / T3 | A ST13；B ST53 | A N318（与 S1080 同一精馏，一条配方多个产出）；B `butenylamine` + `carbon_disulfide` + `sodium_hydroxide` + `hydrogen_peroxide`（ST53 A 化学法） | B：C4H9N + CS2 + H2O2 → C5H7NS + S + 2 H2O |
| S1080 | 异硫氰酸4-戊烯酯 | `pentenyl_isothiocyanate` | T2 / T3 | A ST13；B ST53 | A N318（与 S1079 同一精馏）；B `pentenylamine` + `carbon_disulfide` + `sodium_hydroxide` + `hydrogen_peroxide`（ST53 A 化学法） | B：C5H11N + CS2 + H2O2 → C6H9NS + S + 2 H2O |
| S1081 | 异硫氰酸5-己烯酯 | `hexenyl_isothiocyanate` | T2 / T3 | A ST13；B ST53 | A `materials/wasabi` 酶解蒸馏得山葵油（ST53 B）→ M22 精馏，与 S1087、S1088 同一精馏；B `hexenylamine` + `carbon_disulfide` + `sodium_hydroxide` + `hydrogen_peroxide`（ST53 A 化学法） | B：C6H13N + CS2 + H2O2 → C7H11NS + S + 2 H2O |
| S1082 | 顺式-9-十八烯醇乙酸酯（乙酸油醇酯） | `oleyl_acetate` | T3 | ST02 | `oleyl_alcohol` + `acetic_anhydride` | C18H36O + C4H6O3 → C20H38O2 + C2H4O2 |
| S1083 | 糠基甲基醚 | `furfuryl_methyl_ether` | T3 | ST09 | S0795 + `methyl_chloride` + `sodium_hydroxide` | C5H6O2 + CH3Cl + NaOH → C6H8O2 + NaCl + H2O |
| S1084 | 3-己酮 | `ethyl_propyl_ketone` | T3 | ST04 | S0050（M27 气相脱氢，`catalysts/copper`） | C6H14O → C6H12O + H2 |
| S1085 | 异硫氰酸2-丁酯 | `sec_butyl_isothiocyanate` | T3 | ST33、ST53（A） | S0203 + `ammonia` + `hydrogen` + `carbon_disulfide` + `sodium_hydroxide` + `hydrogen_peroxide` | C4H8O + NH3 + H2 → C4H11N + H2O；C4H11N + CS2 + H2O2 → C5H9NS + S + 2 H2O |
| S1086 | 异硫氰酸异丁酯 | `isobutyl_isothiocyanate` | T3 | ST53（A） | `isobutylamine` + `carbon_disulfide` + `sodium_hydroxide` + `hydrogen_peroxide` | C4H11N + CS2 + H2O2 → C5H9NS + S + 2 H2O |
| S1087 | 异硫氰酸6-(甲硫基)己酯 | `methylthiohexyl_isothiocyanate` | T2 / T3（S1081 为 T3） | A ST13；B ST53 | A 同 S1081 的山葵油精馏；B `methylthiohexylamine` + `carbon_disulfide` + `sodium_hydroxide` + `hydrogen_peroxide`（ST53 A 化学法） | B：C7H17NS + CS2 + H2O2 → C8H15NS2 + S + 2 H2O |
| S1088 | 异硫氰酸5-(甲硫基)戊酯 | `methylthiopentyl_isothiocyanate` | T2 / T3（S1081 为 T3） | A ST13；B ST53 | A 同 S1081 的山葵油精馏；B `methylthiopentylamine` + `carbon_disulfide` + `sodium_hydroxide` + `hydrogen_peroxide`（ST53 A 化学法） | B：C6H15NS + CS2 + H2O2 → C7H13NS2 + S + 2 H2O |
| S1089 | 异硫氰酸戊酯 | `amyl_isothiocyanate` | T3 | ST33、ST53（A） | S0006 + `ammonia` + `hydrogen` + `carbon_disulfide` + `sodium_hydroxide` + `hydrogen_peroxide` | C5H12O + NH3 → C5H13N + H2O；C5H13N + CS2 + H2O2 → C6H11NS + S + 2 H2O |
| S1090 | 异硫氰酸异丙酯 | `isopropyl_isothiocyanate` | T3 | ST53（A） | `isopropylamine` + `carbon_disulfide` + `sodium_hydroxide` + `hydrogen_peroxide` | C3H9N + CS2 + H2O2 → C4H7NS + S + 2 H2O |
| S1091 | 异硫氰酸异戊酯 | `isoamyl_isothiocyanate` | T3 | ST53（A） | S0781 + `carbon_disulfide` + `sodium_hydroxide` + `hydrogen_peroxide` | C5H13N + CS2 + H2O2 → C6H11NS + S + 2 H2O |
| S1092 | 2,5-二甲基呋喃 | `dimethylfuran` | T3（`hydroxymethylfurfural` 为 T4） | ST05 | `hydroxymethylfurfural` + `hydrogen`（`catalysts/copper`，氢解） | C6H6O3 + 3 H2 → C6H8O + 2 H2O |
| S1093 | 环紫罗兰酮 | `cycloionone` | T3 | ST14 | S0235（M14 紫外灯模块，P74 光异构后电环化成 2H-吡喃） | C13H20O → C13H20O |
| S1094 | 2-异丁基-4-甲基-1,3-二氧戊环 | `isovaleraldehyde_pg_acetal` | T2（S0124 为 T3） | ST07 | S0124 + `propylene_glycol` | C5H10O + C3H8O2 → C8H16O2 + H2O |
| S1095 | 顺式和反式-2-异丙基-4-甲基-1,3-二氧戊环 | `isobutyraldehyde_pg_acetal` | T2（S0184 为 T3） | ST07 | S0184 + `propylene_glycol` | C4H8O + C3H8O2 → C7H14O2 + H2O |
| S1096 | 4-氨基丁酸 | `gamma_aminobutyric_acid` | T4 | ST18 | S0325 + `cultures/lactobacillus`（谷氨酸脱羧酶全细胞转化，P76） | C5H9NO4 → C4H9NO2 + CO2 |
| S1097 | N-[2-(3,4-二甲氧基苯基)乙基]-3,4-二甲氧基肉桂酸酰胺 | `dimethoxycinnamoyl_homoveratrylamine` | T3 | ST20 | `dimethoxycinnamic_acid` + `thionyl_chloride` + `homoveratrylamine` | C11H12O4 + Cl2OS → C11H11ClO3 + SO2 + HCl；C11H11ClO3 + C10H15NO2 → C21H25NO5 + HCl |
| S1098 | 二-(1-丙烯基)硫醚(异构体混合物) | `dipropenyl_sulfide` | T3 | ST30 | S0813 + `potassium_hydroxide` | C6H10S → C6H10S |
| S1099 | 乙酸2-戊酯 | `sec_amyl_acetate` | T2（S0007 为 T3） | ST01 | S0007 + `acetic_acid` | C5H12O + C2H4O2 → C7H14O2 + H2O |
| S1100 | 乙胺 | `ethylamine` | T3 | ST33 | `ethanol` + `ammonia` + `hydrogen`（M27 气相，`catalysts/nickel`） | C2H6O + NH3 → C2H7N + H2O |
| S1101 | 2,8-二硫杂-4-壬烯-4-甲醛[5-(甲硫基)-2-(甲硫基甲基)-2-戊烯醛] | `methialdol` | T3 | ST06 | S0700（自身羟醛缩合） | 2 C4H8OS → C8H14OS2 + H2O |

### 1.9 序号 1101–1250（S1102–S1251，147 条，历年增补品种（二））

| 编码 | 名称 | 物料 ID | 阶段 | 模板 | 原料 | 关键反应 |
| --- | --- | --- | --- | --- | --- | --- |
| S1102 | 1-丁烯-1-基甲基硫醚 | `butenyl_methyl_sulfide` | T3 | ST10 | S0119 + S0686 | C4H8O + CH4S → C5H10S + H2O |
| S1103 | 二异丙基二硫醚 | `diisopropyl_disulfide` | T3 | ST10 | S0885 + `hydrogen_peroxide` | 2 C3H8S + H2O2 → C6H14S2 + 2 H2O |
| S1104 | (E)-2-癸烯酸 | `e2_decenoic_acid` | T3 | ST06 | S0141 + `malonic_acid`（吡啶催化，克脑文格尔–多布纳缩合） | C8H16O + C3H4O4 → C10H18O2 + CO2 + H2O |
| S1105 | l-苧烯 | `l_limonene` | T2 | ST13 | N186 | — |
| S1106 | 正己硫醇 | `hexanethiol` | T3 | ST23、ST10 | S0010 + `hydrochloric_acid` + S1010 + `sodium_hydroxide` | C6H14O + HCl → C6H13Cl + H2O；C6H13Cl + H2S + NaOH → C6H14S + NaCl + H2O |
| S1107 | 2-癸酮 | `decan_2_one` | T3 | ST05 | S0872 + `hydrogen` | C10H18O + H2 → C10H20O |
| S1108 | 二糠基醚 | `difurfuryl_ether` | T3 | ST09 | S0795（酸催化脱水成醚） | 2 C5H6O2 → C10H10O3 + H2O |
| S1109 | 异丁酸乙基香兰素酯 | `ethyl_vanillin_isobutyrate` | T3 | ST32 | 122 + `isobutyric_acid` + `thionyl_chloride` | C4H8O2 + Cl2OS → C4H7ClO + SO2 + HCl；C9H10O3 + C4H7ClO → C13H16O4 + HCl |
| S1110 | 8-罗勒烯醇乙酸酯 | `ocimenyl_acetate` | T3 | ST23、ST09 | S0656 + `sodium_hypochlorite` + `acetic_acid` + `sodium_acetate` | C10H16 + HClO → C10H15Cl + H2O；C10H15Cl + C2H3NaO2 → C12H18O2 + NaCl |
| S1111 | 丁胺 | `butylamine` | T3 | ST05 | S0119 + `ammonia` + `hydrogen`（还原胺化） | C4H8O + NH3 + H2 → C4H11N + H2O |
| S1112 | 1-氨基-2-丙醇 | `isopropanolamine` | T3 | ST25 | `propylene_oxide` + `ammonia`（环氧开环） | C3H6O + NH3 → C3H9NO |
| S1113 | 反式-1,5-辛二烯-3-酮 | `octa_1_5_dien_3_one` | T3 | ST32、ST15、ST30 | S0307 + `thionyl_chloride` + `ethylene`（`catalysts/aluminium_chloride`） | C6H10O2 + Cl2OS → C6H9ClO + SO2 + HCl；C6H9ClO + C2H4 → C8H13ClO；C8H13ClO → C8H12O + HCl |
| S1114 | 2,5-二甲基-4-乙氧基-3(2H)-呋喃酮 | `ethoxy_dimethylfuranone` | T3 | ST09 | 131 + `ethyl_chloride` + `sodium_hydroxide` | C6H8O3 + C2H5Cl + NaOH → C8H12O3 + NaCl + H2O |
| S1115 | 反式-2-顺式-4-顺式-7-十三碳三烯醛 | `tridecatrienal` | T2 | ST12 | `materials/egg_yolk`（蛋黄脂质加热氧化，游戏简化） | —（花生四烯酸残基热氧化裂解，组成不定） |
| S1116 | 反式-2-顺式-4-癸二烯酸甲酯 | `methyl_decadienoate` | T3 | ST03 | S0482 + `methanol` + `sodium_methoxide` | C12H20O2 + CH4O → C11H18O2 + C2H6O |
| S1117 | 2-(4-甲基-2-羟基苯基)-丙酸-γ-内酯 | `dimethyl_benzofuranone` | T3 | ST15、ST08 | `m_cresol` + `lactic_acid`（`sulfuric_acid` 催化） | C7H8O + C3H6O3 → C10H10O2 + 2 H2O |
| S1118 | 丙酸顺式-5-辛烯酯 | `z5_octenyl_propionate` | T2（S0017 为 T3） | ST01 | S0017 + `propionic_acid` | C8H16O + C3H6O2 → C11H20O2 + H2O |
| S1119 | 3-甲基-2-丁烯硫醇乙酸酯 | `prenyl_thioacetate` | T3 | ST10 | S0888 + `acetic_anhydride` | C5H10S + C4H6O3 → C7H12OS + C2H4O2 |
| S1120 | 1-吡咯啉 | `pyrroline` | T3 | ST04 | S0833 + `sodium_hypochlorite`（N-氯化后碱消除） | C4H9N + NaClO → C4H8ClN + NaOH；C4H8ClN + NaOH → C4H7N + NaCl + H2O |
| S1121 | 2,3,4-三甲基-3-戊醇 | `trimethyl_3_pentanol` | T3 | ST16 | `methyl_chloride` + `magnesium` + `diisopropyl_ketone` | CH3Cl + Mg → CH3ClMg；C7H14O + CH3ClMg + HCl → C8H18O + MgCl2 |
| S1122 | 二异丙基三硫醚 | `diisopropyl_trisulfide` | T3 | ST10 | S0885 + `sulfur_dichloride` | 2 C3H8S + Cl2S → C6H14S3 + 2 HCl |
| S1123 | 2-丙酰基-1-吡咯啉 | `propionyl_pyrroline` | T3 | ST32、ST15、ST05、ST04 | S1156 + `propionic_acid` + `thionyl_chloride` + `hydrogen` + `oxygen` | C3H6O2 + Cl2OS → C3H5ClO + SO2 + HCl；C4H5N + C3H5ClO → C7H9NO + HCl；C7H9NO + 3 H2 → C7H15NO；C7H15NO + O2 → C7H11NO + 2 H2O |
| S1124 | 3,6-二乙基-1,2,4,5-四硫杂环己烷与3,5-二乙基-1,2,4-三硫杂环戊烷的混合物 | `diethyl_tetrathiane_trithiolane` | T3 | ST10 | S0117 + S1010 + `sulfur`（与 S0896 同一配方的联产，M22 分出；氨只改产出比） | 2 C3H6O + 2 H2S + 2 S → C6H12S4 + 2 H2O；2 C3H6O + 2 H2S + S → C6H12S3 + 2 H2O |
| S1125 | 2,5-二羟基-1,4-二噻烷 | `dihydroxy_dithiane` | T3 | ST10 | `chloroacetaldehyde` + S1010 + `sodium_hydroxide` | 2 C2H3ClO + 2 H2S + 2 NaOH → C4H8O2S2 + 2 NaCl + 2 H2O |
| S1126 | 3-己烯醛(反式/顺式混合物) | `hex_3_enal_mix` | T3 | ST04 | S0027 + `oxygen` | 2 C6H12O + O2 → 2 C6H10O + 2 H2O |
| S1127 | 4-羟基-3,5-二甲氧基苯甲醛 | `syringaldehyde` | T3 | ST04 | `sulfite_spent_liquor`（阔叶木）+ `oxygen` + `sodium_hydroxide`（同香兰素 B 路线，铜催化） | —（木质素磺酸盐碱性空气氧化，与香兰素共产） |
| S1128 | 2-十一烯-1-醇 | `undec_2_en_1_ol` | T3 | ST05 | S0156 + `hydrogen`（选择性还原羰基） | C11H20O + H2 → C11H22O |
| S1129 | 2-(4-羟基苯基)-乙胺（酪胺） | `tyramine` | T4 | ST18 | S0877 + `cultures/lactobacillus`（酪氨酸脱羧酶） | C9H11NO3 → C8H11NO + CO2 |
| S1130 | 4[(2-呋喃甲基)硫基]-2-戊酮 | `furfurylthio_pentanone` | T3 | ST25 | S0692 + S0908 | C5H6OS + C5H8O → C10H14O2S |
| S1131 | 己酸甲硫基甲酯 | `methylthiomethyl_hexanoate` | T3 | ST10 | `hexanoic_acid` + `chloromethyl_methyl_sulfide` + `sodium_hydroxide` | C6H12O2 + C2H5ClS + NaOH → C8H16O2S + NaCl + H2O |
| S1132 | 2,6-二甲基-4-庚酮 | `diisobutyl_ketone` | T3 | ST06、ST05 | S0977 + `acetone` + `hydrogen`（`catalysts/palladium` + `catalysts/ion_exchange_resin`） | C6H12O + C3H6O → C9H16O + H2O；C9H16O + H2 → C9H18O |
| S1134 | 反式-3-己烯醇 | `trans_3_hexenol` | T3 | ST05 | S0460 + `hydrogen`（`catalysts/copper` 酯加氢，保留双键） | C8H14O2 + 2 H2 → C6H12O + C2H6O |
| S1135 | 甲酸松油酯 | `terpinyl_formate` | T2（S0037 为 T3） | ST01 | S0037 + `formic_acid` | C10H18O + CH2O2 → C11H18O2 + H2O |
| S1136 | 脱氢圆柚酮 | `dehydronootkatone` | T3 | ST04 | S0223 + `oxygen`（催化脱氢） | 2 C15H22O + O2 → 2 C15H20O + 2 H2O |
| S1137 | 己酸香叶酯 | `geranyl_hexanoate` | T2 | ST01 | S0039 + `hexanoic_acid` | C10H18O + C6H12O2 → C16H28O2 + H2O |
| S1138 | 3-甲基己醛 | `methylhexanal` | T3 | ST19 | `methylpentene` + `carbon_monoxide` + `hydrogen` | C6H12 + CO + H2 → C7H14O |
| S1139 | (反式,反式)-2,4-壬二烯 | `nonadiene` | T3 | ST23、ST16、ST30 | `crotonaldehyde` + S0006 + `hydrochloric_acid` + `magnesium` | C5H12O + HCl → C5H11Cl + H2O；C5H11Cl + Mg → C5H11ClMg；C4H6O + C5H11ClMg + HCl → C9H18O + MgCl2；C9H18O → C9H16 + H2O |
| S1140 | 1-辛烯 | `octene` | — | ST26 | 已有：`octene` | — |
| S1141 | 2-甲基苯乙酮 | `o_methylacetophenone` | T3 | ST15 | `toluene` + `acetic_anhydride`（取邻位馏分） | C7H8 + C4H6O3 → C9H10O + C2H4O2 |
| S1142 | 1-乙基-2-甲酰基吡咯（茶吡咯） | `ethyl_formylpyrrole` | T3 | ST09、ST15 | S1156 + `ethyl_chloride` + `sodium_hydroxide` + `dimethylformamide` + `phosphorus_trichloride` + `oxygen` | C4H5N + C2H5Cl + NaOH → C6H9N + NaCl + H2O；2 PCl3 + O2 → 2 Cl3OP；C6H9N + C3H7NO + Cl3OP + H2O → C7H9NO + C2H7N + HCl + HPO2Cl2 |
| S1143 | 2-(4-甲基-5-噻唑基)乙醇辛酸酯 | `methylthiazolylethyl_octanoate` | T2（S0727 为 T3） | ST01 | S0727 + `caprylic_acid` | C6H9NOS + C8H16O2 → C14H23NO2S + H2O |
| S1144 | 2-乙基-6-甲基吡嗪 | `ethyl_6_methylpyrazine` | T3 | ST28 | S0788 + `sodium` + `ammonia` + `methyl_chloride`（氨基钠侧链甲基化） | 2 Na + 2 NH3 → 2 NaNH2 + H2；C6H8N2 + NaNH2 + CH3Cl → C7H10N2 + NaCl + NH3 |
| S1145 | 对-丙基苯酚 | `p_propylphenol` | T3 | ST15、ST05 | `phenol` + `propionic_acid` + `hydrogen` | C6H6O + C3H6O2 → C9H10O2 + H2O；C9H10O2 + 2 H2 → C9H12O + H2O |
| S1146 | 3,5-二乙基-2-甲基吡嗪 | `diethyl_2_methylpyrazine` | T3 | ST11 | S0210 + `butanediamine`（缩合后脱氢，M22 分出异构体） | C5H8O2 + C4H12N2 → C9H14N2 + 2 H2O + H2 |
| S1147 | 马鞭草烯酮 | `verbenone` | T3 | ST14 | S0658 + `oxygen`（钴盐催化烯丙位氧化） | C10H16 + O2 → C10H14O + H2O |
| S1148 | 4-戊烯醛 | `pent_4_enal` | T3 | ST37 | `acetaldehyde` + `allyl_alcohol` | C2H4O + 2 C3H6O → C8H14O2 + H2O；C8H14O2 → C5H8O + C3H6O |
| S1149 | 乙酰乙酸乙酯丙二醇缩酮 | `ethyl_acetoacetate_pg_ketal` | T2（S0365 为 T3） | ST07 | S0365 + `propylene_glycol` | C6H10O3 + C3H8O2 → C9H16O4 + H2O |
| S1150 | 山梨酸甲酯 | `methyl_sorbate` | T2 | ST01 | 333 + `methanol` | C6H8O2 + CH4O → C7H10O2 + H2O |
| S1151 | 2,5-二乙基四氢呋喃 | `diethyltetrahydrofuran` | T3 | ST16、ST05 | S0624 + `ethyl_chloride` + `magnesium` + `hydrogen` | C2H5Cl + Mg → C2H5ClMg；C6H10O2 + C2H5ClMg + HCl → C8H16O2 + MgCl2；C8H16O2 + H2 → C8H16O + H2O |
| S1152 | 脱氢薄荷呋喃内酯 | `dehydromenthofurolactone` | T3 | ST24 | S0279 + `oxygen`（光敏氧化，M14 紫外灯模块） | C10H14O + O2 → C10H12O2 + H2O |
| S1153 | 乙酸桃金娘烯酯 | `myrtenyl_acetate` | T3 | ST02 | S0840 + `acetic_anhydride` | C10H16O + C4H6O3 → C12H18O2 + C2H4O2 |
| S1154 | 2-(4-甲基-5-噻唑基)乙醇己酸酯 | `methylthiazolylethyl_hexanoate` | T2（S0727 为 T3） | ST01 | S0727 + `hexanoic_acid` | C6H9NOS + C6H12O2 → C12H19NO2S + H2O |
| S1155 | 2-(4-甲基-5-噻唑基)乙醇丁酸酯 | `methylthiazolylethyl_butyrate` | T2（S0727 为 T3） | ST01 | S0727 + `butyric_acid` | C6H9NOS + C4H8O2 → C10H15NO2S + H2O |
| S1156 | 吡咯 | `pyrrole` | T3 | ST11 | `furan` + `ammonia`（M27 气相，`catalysts/alumina`） | C4H4O + NH3 → C4H5N + H2O |
| S1157 | S-烯丙基-L-半胱氨酸 | `s_allyl_cysteine` | T3 | ST10 | `l_cysteine` + `allyl_chloride` + `sodium_hydroxide` | C3H7NO2S + C3H5Cl + NaOH → C6H11NO2S + NaCl + H2O |
| S1158 | 2-巯基-3-丁醇 | `mercaptobutanol` | T3 | ST10 | `epoxybutane` + S1010 | C4H8O + H2S → C4H10OS |
| S1159 | 硫代香叶醇 | `thiogeraniol` | T3 | ST23、ST10 | S0039 + `thionyl_chloride` + S1010 + `sodium_hydroxide` | C10H18O + Cl2OS → C10H17Cl + SO2 + HCl；C10H17Cl + H2S + NaOH → C10H18S + NaCl + H2O |
| S1160 | 蒎烷硫醇 | `pinanyl_mercaptan` | T3 | ST10 | S0658 + S1010（`catalysts/aluminium_chloride`） | C10H16 + H2S → C10H18S |
| S1161 | α-甲基-β-羟基丙基α-甲基-β-巯丙基硫醚 | `hydroxybutyl_mercaptobutyl_sulfide` | T3 | ST25 | S1240 + `epoxybutane` | C4H10S2 + C4H8O → C8H18OS2 |
| S1163 | 柠檬醛二乙缩醛 | `citral_diethyl_acetal` | T2 | ST07 | 125 + `ethanol` | C10H16O + 2 C2H6O → C14H26O2 + H2O |
| S1164 | 3-丙烯基-6-乙氧基苯酚 | `propenyl_guaethol` | T3 | ST09、ST36 | S0092 + `ethyl_chloride` + `sodium_hydroxide` + `potassium_hydroxide` + `hydrochloric_acid` | C10H12O2 + C2H5Cl + NaOH → C12H16O2 + NaCl + H2O；C12H16O2 + KOH → C11H13KO2 + CH4O；C11H13KO2 + HCl → C11H14O2 + KCl |
| S1165 | β-甲基紫罗兰酮 | `methyl_beta_ionone` | T3 | ST06 | 125 + S0203（碱缩合后 `sulfuric_acid` 环化） | C10H16O + C4H8O → C14H22O + H2O |
| S1166 | δ-甲基紫罗兰酮 | `methyl_delta_ionone` | T3 | ST06 | 125 + S0203（异构缩合后 `sulfuric_acid` 环化） | C10H16O + C4H8O → C14H22O + H2O |
| S1167 | 2,6-壬二烯醛二乙缩醛 | `nonadienal_diethyl_acetal` | T2（S0150 为 T3） | ST07 | S0150 + `ethanol` | C9H14O + 2 C2H6O → C13H24O2 + H2O |
| S1168 | 9-十一烯醛 | `undec_9_enal` | T3 | ST04 | `undecenol`（脱氢时双键迁移，M22 分出 9-位异构体） | C11H22O → C11H20O + H2 |
| S1169 | 10-十一烯醛 | `undec_10_enal` | T3 | ST04 | `undecenol`（`catalysts/copper` 气相脱氢） | C11H22O → C11H20O + H2 |
| S1170 | 十六醛（杨梅醛） | `ethyl_methylphenylglycidate` | T3 | ST06 | S0225 + `ethyl_chloroacetate` + `sodium_hydroxide`（达森缩合） | C8H8O + C4H7ClO2 + NaOH → C12H14O3 + NaCl + H2O |
| S1172 | 兔耳草醛（仙客来醛） | `cyclamen_aldehyde` | T3 | ST06、ST05 | S0171 + S0117 + `hydrogen` | C10H12O + C3H6O → C13H16O + H2O；C13H16O + H2 → C13H18O |
| S1173 | 羟基香茅醛 | `hydroxycitronellal` | T3 | ST25 | S0173 + `sodium_bisulfite` + `sulfuric_acid`（亚硫酸氢盐加成保护后硫酸水合） | C10H18O + H2O → C10H20O2 |
| S1174 | β-环高柠檬醛 | `beta_homocyclocitral` | T3 | ST24 | S0235 + `hydrogen_peroxide` + `acetic_acid`（过氧乙酸氧化后水解） | C13H20O + H2O2 → C11H18O + C2H4O2 |
| S1175 | l-薄荷酮甘油缩酮 | `l_menthone_glyceryl_ketal` | T2 | ST07 | `menthone` + `glycerol` | C10H18O + C3H8O3 → C13H24O3 + H2O |
| S1176 | 4-甲硫基-4-甲基-2-戊酮 | `methylthio_methylpentanone` | T3 | ST25 | S0869 + S0686 | C6H10O + CH4S → C7H14OS |
| S1177 | 3-巯基-2-戊酮 | `mercapto_2_pentanone` | T3 | ST23、ST10 | S0208 + `chlorine` + S1010 + `sodium_hydroxide` | C5H10O + Cl2 → C5H9ClO + HCl；C5H9ClO + H2S + NaOH → C5H10OS + NaCl + H2O |
| S1178 | d,l-薄荷酮甘油缩酮 | `dl_menthone_glyceryl_ketal` | T3 | ST05、ST07 | `thymol` + `hydrogen` + `glycerol`（同 S1175，薄荷酮改为百里酚部分加氢所得 d,l-薄荷酮） | C10H14O + 2 H2 → C10H18O；C10H18O + C3H8O3 → C13H24O3 + H2O |
| S1179 | α-甲基紫罗兰酮 | `methyl_alpha_ionone` | T3 | ST06 | 125 + S0203（碱缩合后 `phosphoric_acid` 环化） | C10H16O + C4H8O → C14H22O + H2O |
| S1180 | α-异甲基紫罗兰酮 | `alpha_isomethylionone` | T3 | ST06 | 125 + S0203（异构缩合后 `phosphoric_acid` 环化） | C10H16O + C4H8O → C14H22O + H2O |
| S1181 | 烯丙基α-紫罗兰酮 | `allyl_alpha_ionone` | T3 | ST28 | S0234 + `allyl_chloride` + `sodium_hydroxide` | C13H20O + C3H5Cl + NaOH → C16H24O + NaCl + H2O |
| S1182 | 6-甲基香豆素 | `methylcoumarin` | T3 | ST06 | `p_cresol` + `malic_acid`（`sulfuric_acid`，佩希曼缩合） | C7H8O + C4H6O5 → C10H8O2 + CO + 3 H2O |
| S1183 | 2-巯基丙酸 | `thiolactic_acid` | T3 | ST23、ST10 | `propionic_acid` + `chlorine` + S1010 + `sodium_hydroxide` | C3H6O2 + Cl2 → C3H5ClO2 + HCl；C3H5ClO2 + H2S + NaOH → C3H6O2S + NaCl + H2O |
| S1184 | 2-甲基-4-戊烯酸 | `methyl_4_pentenoic_acid` | T3 | ST37、ST04 | S0117 + `allyl_alcohol` + `oxygen` | C3H6O + 2 C3H6O → C9H16O2 + H2O；C9H16O2 → C6H10O + C3H6O；2 C6H10O + O2 → 2 C6H10O2 |
| S1185 | 乙酸二甲基苄基原酯 | `dimethylbenzylcarbinyl_acetate` | T3 | ST02 | S0048 + `acetic_anhydride` | C10H14O + C4H6O3 → C12H16O2 + C2H4O2 |
| S1186 | 环己基乙酸烯丙酯 | `allyl_cyclohexaneacetate` | T3 | ST05、ST01 | S0316 + `hydrogen` + `allyl_alcohol` | C8H8O2 + 3 H2 → C8H14O2；C8H14O2 + C3H6O → C11H18O2 + H2O |
| S1187 | 乙酸玫瑰酯 | `rhodinyl_acetate` | T3 | ST02 | S0036 + `acetic_anhydride` | C10H20O + C4H6O3 → C12H22O2 + C2H4O2 |
| S1188 | 3-(2-呋喃基)丙酸乙酯 | `ethyl_furylpropionate` | T3 | ST05、ST01 | `furylacrylic_acid` + `hydrogen`（`catalysts/palladium`，只加侧链双键）+ `ethanol` | C7H6O3 + H2 → C7H8O3；C7H8O3 + C2H6O → C9H12O3 + H2O |
| S1189 | 丙酸烯丙酯 | `allyl_propionate` | T2（`allyl_alcohol` 为 T3） | ST01 | `propionic_acid` + `allyl_alcohol` | C3H6O2 + C3H6O → C6H10O2 + H2O |
| S1190 | 3-环己基丙酸烯丙酯 | `allyl_cyclohexylpropionate` | T3 | ST05、ST01 | S0337 + `hydrogen` + `allyl_alcohol` | C9H10O2 + 3 H2 → C9H16O2；C9H16O2 + C3H6O → C12H20O2 + H2O |
| S1191 | 3-(2-呋喃基)丙酸异丁酯 | `isobutyl_furylpropionate` | T3 | ST03 | S1188 + S0005 + `sodium_methoxide` | C9H12O3 + C4H10O → C11H16O3 + C2H6O |
| S1192 | 硫代丙酸糠酯 | `furfuryl_thiopropionate` | T3 | ST32 | S0692 + `propionic_acid` + `thionyl_chloride` | C3H6O2 + Cl2OS → C3H5ClO + SO2 + HCl；C5H6OS + C3H5ClO → C8H10O2S + HCl |
| S1193 | 丁酸二甲基苄基原酯 | `dimethylbenzylcarbinyl_butyrate` | T3 | ST32 | S0048 + `butyric_acid` + `thionyl_chloride` | C4H8O2 + Cl2OS → C4H7ClO + SO2 + HCl；C10H14O + C4H7ClO → C14H20O2 + HCl |
| S1194 | 环己基丁酸烯丙酯 | `allyl_cyclohexanebutyrate` | T3 | ST05、ST01 | `phenylbutyric_acid` + `hydrogen` + `allyl_alcohol` | C10H12O2 + 3 H2 → C10H18O2；C10H18O2 + C3H6O → C13H22O2 + H2O |
| S1195 | 1,3-壬二醇乙酸酯(混合酯) | `nonanediol_acetate` | T3 | ST25 | `octene` + `formaldehyde` + `acetic_acid`（`sulfuric_acid` 催化，普林斯反应） | C8H16 + CH2O + 2 C2H4O2 → C13H24O4 + H2O |
| S1196 | 丁酸苏合香酯 | `styralyl_butyrate` | T2（S0032 为 T3） | ST01 | S0032 + `butyric_acid` | C8H10O + C4H8O2 → C12H16O2 + H2O |
| S1197 | 乙酸柏木酯 | `cedryl_acetate` | T3 | ST02 | `cedrol` + `acetic_anhydride` | C15H26O + C4H6O3 → C17H28O2 + C2H4O2 |
| S1198 | 异丁酸麦芽酚酯 | `maltol_isobutyrate` | T3 | ST32 | 123 + `isobutyric_acid` + `thionyl_chloride` | C4H8O2 + Cl2OS → C4H7ClO + SO2 + HCl；C6H6O3 + C4H7ClO → C10H12O4 + HCl |
| S1199 | 2-甲基-4-戊烯酸乙酯 | `ethyl_methyl_4_pentenoate` | T2（S1184 为 T3） | ST01 | S1184 + `ethanol` | C6H10O2 + C2H6O → C8H14O2 + H2O |
| S1200 | 乙酸四氢糠酯 | `tetrahydrofurfuryl_acetate` | T3 | ST02 | S0796 + `acetic_anhydride` | C5H10O2 + C4H6O3 → C7H12O3 + C2H4O2 |
| S1201 | 庚炔羧酸甲酯 | `methyl_octynoate` | T3 | ST16 | `heptyne` + `sodium` + `ammonia` + `methyl_chloroformate`（炔钠酰化） | 2 Na + 2 NH3 → 2 NaNH2 + H2；C7H12 + NaNH2 → C7H11Na + NH3；C7H11Na + C2H3ClO2 → C9H14O2 + NaCl |
| S1202 | 辛炔羧酸甲酯 | `methyl_nonynoate` | T3 | ST16 | `octyne` + `sodium` + `ammonia` + `methyl_chloroformate`（同 S1201） | 2 Na + 2 NH3 → 2 NaNH2 + H2；C8H14 + NaNH2 → C8H13Na + NH3；C8H13Na + C2H3ClO2 → C10H16O2 + NaCl |
| S1203 | 癸二酸二乙酯 | `diethyl_sebacate` | T2（`sebacic_acid` 为 T3） | ST01 | `sebacic_acid` + `ethanol` | C10H18O4 + 2 C2H6O → C14H26O4 + 2 H2O |
| S1204 | 10-十一烯酸乙酯 | `ethyl_undecenoate` | T2（S0336 为 T3） | ST01 | S0336 + `ethanol` | C11H20O2 + C2H6O → C13H24O2 + H2O |
| S1205 | 苯乙酸烯丙酯 | `allyl_phenylacetate` | T2（`allyl_alcohol`、S0316 为 T3） | ST01 | S0316 + `allyl_alcohol` | C8H8O2 + C3H6O → C11H12O2 + H2O |
| S1206 | 三乙酸甘油酯 | `triacetin` | — | ST26 | 见 583（已有：`triacetin`） | — |
| S1207 | 苯乙酸香叶酯 | `geranyl_phenylacetate` | T2（S0316 为 T3） | ST01 | S0039 + S0316 | C10H18O + C8H8O2 → C18H24O2 + H2O |
| S1208 | 苯乙酸对-甲酚酯 | `p_cresyl_phenylacetate` | T3 | ST32 | `p_cresol` + S0316 + `thionyl_chloride` | C8H8O2 + Cl2OS → C8H7ClO + SO2 + HCl；C7H8O + C8H7ClO → C15H14O2 + HCl |
| S1209 | 4-苯基丁酸甲酯（苯丁酸甲酯） | `methyl_phenylbutyrate` | T2（`phenylbutyric_acid` 为 T3） | ST01 | `phenylbutyric_acid` + `methanol` | C10H12O2 + CH4O → C11H14O2 + H2O |
| S1210 | 4-苯基丁酸乙酯（苯丁酸乙酯） | `ethyl_phenylbutyrate` | T2（`phenylbutyric_acid` 为 T3） | ST01 | `phenylbutyric_acid` + `ethanol` | C10H12O2 + C2H6O → C12H16O2 + H2O |
| S1211 | 肉桂酸烯丙酯 | `allyl_cinnamate` | T2（`allyl_alcohol`、S0318 为 T3） | ST01 | S0318 + `allyl_alcohol` | C9H8O2 + C3H6O → C12H12O2 + H2O |
| S1212 | 2-甲基-3-戊烯酸乙酯 | `ethyl_methyl_3_pentenoate` | T2（S0876 为 T3） | ST01 | S0876 + `ethanol` | C6H10O2 + C2H6O → C8H14O2 + H2O |
| S1213 | 亚硝酸乙酯 | `ethyl_nitrite` | T2 | ST01 | `ethanol` + `sodium_nitrite` + `sulfuric_acid`（亚硝酸酯化） | C2H6O + NaNO2 + H2SO4 → C2H5NO2 + NaHSO4 + H2O |
| S1214 | 庚酸戊酯 | `amyl_heptanoate` | T2（S0006、S0308 为 T3） | ST01 | S0308 + S0006 | C7H14O2 + C5H12O → C12H24O2 + H2O |
| S1215 | 3-乙酰基-2,5-二甲基呋喃 | `acetyl_dimethylfuran` | T3（S1092 为 T4） | ST15 | S1092 + `acetic_anhydride`（`zinc_chloride` 催化） | C6H8O + C4H6O3 → C8H10O2 + C2H4O2 |
| S1216 | 2,5-二甲基-3-氧代(2H)-4-呋喃丁酸酯 | `furaneol_butyrate` | T3 | ST32 | 131 + `butyric_acid` + `thionyl_chloride` | C4H8O2 + Cl2OS → C4H7ClO + SO2 + HCl；C6H8O3 + C4H7ClO → C10H14O4 + HCl |
| S1217 | 2-甲氧基-3(5或6)-异丙基吡嗪 | `methoxy_isopropylpyrazine` | T3 | ST11、ST09 | S0340 + `ammonia` + `glyoxal` + `phosphorus_trichloride` + `oxygen` + `sodium_methoxide` | C5H11NO2 + NH3 → C5H12N2O + H2O；C5H12N2O + C2H2O2 → C7H10N2O + 2 H2O；2 PCl3 + O2 → 2 Cl3OP；C7H10N2O + Cl3OP → C7H9ClN2 + HPO2Cl2；C7H9ClN2 + CH3NaO → C8H12N2O + NaCl |
| S1218 | 2-甲基-3(5或6)-糠硫基吡嗪 | `methyl_furfurylthio_pyrazine` | T3 | ST10 | `chloromethylpyrazine` + S0692 + `sodium_hydroxide` | C5H5ClN2 + C5H6OS + NaOH → C10H10N2OS + NaCl + H2O |
| S1219 | 2-甲基(或乙基)-3(5或6)-甲氧基吡嗪 | `alkyl_methoxypyrazine_mix` | T3 | ST23、ST09 | `chloromethylpyrazine` + S0766 + `chlorine` + `sodium_methoxide` | C5H5ClN2 + CH3NaO → C6H8N2O + NaCl；C6H8N2 + Cl2 → C6H7ClN2 + HCl；C6H7ClN2 + CH3NaO → C7H10N2O + NaCl |
| S1220 | 2,5-二甲基-2,5-二羟基-1,4-二硫代环己烷 | `dimethyl_dihydroxy_dithiane` | T3 | ST25 | S0992（自发二聚） | 2 C3H6OS → C6H12O2S2 |
| S1221 | 5,7-二氢-2-甲基噻吩并(3,4-d)嘧啶 | `methyl_dihydrothienopyrimidine` | T3 | ST11 | S0243 + S0353 + `sodium_methoxide` + `acetamidine_hydrochloride` + `sodium_hydroxide` | C4H6OS + C3H6O2 → C5H6O2S + C2H6O；C5H6O2S + C2H7ClN2 + NaOH → C7H8N2S + NaCl + 3 H2O |
| S1222 | 2-乙氧基噻唑 | `ethoxythiazole` | T3 | ST11、ST09 | `chloroacetaldehyde` + `thiourea` + `sodium_nitrite` + `hydrochloric_acid` + `ethanol` + `sodium_hydroxide` | C2H3ClO + CH4N2S → C3H4N2S + HCl + H2O；C3H4N2S + NaNO2 + 2 HCl → C3H2ClNS + NaCl + N2 + 2 H2O；C3H2ClNS + C2H6O + NaOH → C5H7NOS + NaCl + H2O |
| S1223 | 2,4-二甲基-5-乙酰基噻唑 | `acetyl_dimethylthiazole` | T3 | ST23、ST11 | `acetylacetone` + `chlorine` + `thioacetamide` | C5H8O2 + Cl2 → C5H7ClO2 + HCl；C5H7ClO2 + C2H5NS → C7H9NOS + HCl + H2O |
| S1224 | 乙酸异丁香酯 | `isoeugenyl_acetate` | T3 | ST02 | S0092 + `acetic_anhydride` | C10H12O2 + C4H6O3 → C12H14O3 + C2H4O2 |
| S1225 | 3-甲基丁酸对-甲酚酯 | `p_cresyl_isovalerate` | T3 | ST32 | `p_cresol` + S0303 + `thionyl_chloride` | C5H10O2 + Cl2OS → C5H9ClO + SO2 + HCl；C7H8O + C5H9ClO → C12H16O2 + HCl |
| S1226 | l-薄荷醇乙二醇碳酸酯 | `menthyl_ethylene_glycol_carbonate` | T3 | ST03 | 29 + `ethylene_carbonate`（碱催化开环酯交换） | C10H20O + C3H4O3 → C13H24O4 |
| S1227 | 3-(2-甲基丙基)吡啶 | `isobutylpyridine_3` | T3 | ST23、ST28 | `methylpyridine` + S0003 + `hydrochloric_acid` + `sodium` + `ammonia` | C3H8O + HCl → C3H7Cl + H2O；2 Na + 2 NH3 → 2 NaNH2 + H2；C6H7N + NaNH2 + C3H7Cl → C9H13N + NaCl + NH3 |
| S1228 | 乙基香兰素1,2-丙二醇缩醛 | `ethyl_vanillin_pg_acetal` | T2 | ST07 | 122 + `propylene_glycol` | C9H10O3 + C3H8O2 → C12H16O4 + H2O |
| S1229 | 人造康乃克油 | `artificial_cognac_oil` | T2（S0468 为 T3） | ST22 | S0473 + S0481 + S0468 | — |
| S1230 | 山楂核烟熏香味料Ⅰ号 | `hawthorn_kernel_smoke_flavoring_1` | T1 | NT11 | `materials/hawthorn_kernel`（干馏冷凝液再 M08 蒸馏取 85–110 °C 馏分；与 S1231 同一干馏液，一条配方两个产出） | — |
| S1231 | 山楂核烟熏香味料Ⅱ号 | `hawthorn_kernel_smoke_flavoring_2` | T1 | NT11 | `materials/hawthorn_kernel`（干馏冷凝液油水分层取水相精制；与 S1230 同一干馏液，一条配方两个产出） | — |
| S1232 | 苄基异丁基原醇 | `isobutylbenzylcarbinol` | T3 | ST16 | `benzyl_chloride` + `magnesium` + S0124 | C7H7Cl + Mg → C7H7ClMg；C5H10O + C7H7ClMg + HCl → C12H18O + MgCl2 |
| S1233 | 4-苯基-3-丁烯-2-醇 | `phenylbutenol` | T3 | ST05 | S0206 + `hydrogen`（`catalysts/copper`，只还原羰基） | C10H10O + H2 → C10H12O |
| S1234 | 2-甲基-4-苯基-2-丁醇 | `methyl_phenyl_2_butanol` | T3 | ST05、ST16 | S0206 + `hydrogen` + `methyl_chloride` + `magnesium` | C10H10O + H2 → C10H12O；CH3Cl + Mg → CH3ClMg；C10H12O + CH3ClMg + HCl → C11H16O + MgCl2 |
| S1235 | l-薄荷醇丙二醇碳酸酯 | `menthyl_propylene_glycol_carbonate` | T3 | ST03 | 29 + `propylene_carbonate`（碱催化开环酯交换） | C10H20O + C4H6O3 → C14H26O4 |
| S1236 | 辛酸烯丙酯 | `allyl_octanoate` | T2（`allyl_alcohol` 为 T3） | ST01 | `caprylic_acid` + `allyl_alcohol` | C8H16O2 + C3H6O → C11H20O2 + H2O |
| S1237 | α-丙基苯乙醇 | `propylphenethyl_alcohol` | T3 | ST16 | `benzyl_chloride` + `magnesium` + S0119 | C7H7Cl + Mg → C7H7ClMg；C4H8O + C7H7ClMg + HCl → C11H16O + MgCl2 |
| S1238 | 龙葵醇 | `hydratropyl_alcohol` | T3 | ST05 | S1256 + `hydrogen` | C9H10O + H2 → C9H12O |
| S1239 | 四氢芳樟醇 | `tetrahydrolinalool` | T3 | ST05 | S0029 + `hydrogen` | C10H18O + 2 H2 → C10H22O |
| S1240 | 2,3-二巯基丁烷 | `butane_2_3_dithiol` | T3 | ST10 | S1158 + S1010（M14 酸催化置换羟基） | C4H10OS + H2S → C4H10S2 + H2O |
| S1241 | β-萘乙醚 | `naphthyl_ethyl_ether` | T3 | ST09 | `beta_naphthol` + `ethanol`（`sulfuric_acid` 催化） | C10H8O + C2H6O → C12H12O + H2O |
| S1242 | 异丁基β-萘醚 | `naphthyl_isobutyl_ether` | T3 | ST09 | `beta_naphthol` + S0005（`sulfuric_acid` 催化） | C10H8O + C4H10O → C14H16O + H2O |
| S1243 | 邻-丙基苯酚 | `o_propylphenol` | T3 | ST09、ST37、ST05 | `phenol` + `allyl_chloride` + `sodium_hydroxide` + `hydrogen` | C6H6O + C3H5Cl + NaOH → C9H10O + NaCl + H2O；C9H10O + H2 → C9H12O |
| S1244 | 苄基异丁香酚 | `isoeugenyl_benzyl_ether` | T3 | ST09 | S0092 + `benzyl_chloride` + `sodium_hydroxide` | C10H12O2 + C7H7Cl + NaOH → C17H18O2 + NaCl + H2O |
| S1245 | 2-甲基-3(5或6)-甲硫基吡嗪 | `methyl_methylthio_pyrazine` | T3 | ST10 | `chloromethylpyrazine` + S0686 + `sodium_hydroxide` | C5H5ClN2 + CH4S + NaOH → C6H8N2S + NaCl + H2O |
| S1246 | 香茅氧基乙醛 | `citronellyloxyacetaldehyde` | T3 | ST09 | S0040 + `chloroacetaldehyde`（以其乙缩醛形式醚化后水解）+ `sodium_hydroxide` | C10H20O + C2H3ClO + NaOH → C12H22O2 + NaCl + H2O |
| S1247 | 乙醛苯乙醇丙醇缩醛 | `acetaldehyde_phenethyl_propyl_acetal` | T2（S0034 为 T3） | ST07 | `acetaldehyde` + S0034 + `n_propanol` | C2H4O + C8H10O + C3H8O → C13H20O2 + H2O |
| S1248 | 2-甲基-3-(对甲基苯基)丙醛 | `satinaldehyde` | T3 | ST06、ST05 | S0166（取对位）+ S0117 + `hydrogen` | C8H8O + C3H6O → C11H12O + H2O；C11H12O + H2 → C11H14O |
| S1249 | 2-苯基-3-(2-呋喃基)丙-2-烯醛 | `phenyl_furyl_propenal` | T3 | ST06 | `furfural` + S0168 | C5H4O2 + C8H8O → C13H10O2 + H2O |
| S1250 | 3,5,5-三甲基己醛 | `trimethylhexanal` | T3 | ST19 | `diisobutylene` + `carbon_monoxide` + `hydrogen` | C8H16 + CO + H2 → C9H18O |
| S1251 | 2-甲基-3(5或6)-乙氧基吡嗪 | `ethoxy_methylpyrazine` | T3 | ST09 | `chloromethylpyrazine` + `ethanol` + `sodium_hydroxide` | C5H5ClN2 + C2H6O + NaOH → C7H10N2O + NaCl + H2O |

### 1.10 序号 1251–1400（S1252–S1401，149 条，历年增补品种（三））

| 编码 | 名称 | 物料 ID | 阶段 | 模板 | 原料 | 关键反应 |
| --- | --- | --- | --- | --- | --- | --- |
| S1252 | 庚醛甘油缩醛 | `heptanal_glyceryl_acetal` | T2（S0136 为 T3） | ST07 | S0136 + `glycerol` | C7H14O + C3H8O3 → C10H20O3 + H2O |
| S1253 | 苯乙醛甘油缩醛 | `phenylacetaldehyde_glyceryl_acetal` | T2（S0168 为 T3） | ST07 | S0168 + `glycerol` | C8H8O + C3H8O3 → C11H14O3 + H2O |
| S1254 | 对-异丙基苯乙醛 | `p_isopropylphenylacetaldehyde` | T3 | ST06 | S0171 + `ethyl_chloroacetate` | C10H12O + C4H7ClO2 + NaOH → C14H18O3 + NaCl + H2O；C14H18O3 + H2O → C11H14O + CO2 + C2H6O |
| S1255 | 2-甲基-4-苯丁醛 | `methyl_4_phenylbutanal` | T3 | ST06 + ST05 | S0168 + S0117 + `hydrogen` | C8H8O + C3H6O → C11H12O + H2O；C11H12O + H2 → C11H14O |
| S1256 | 龙葵醛 | `hydratropic_aldehyde` | T3 | A ST19；B ST06 | A S0679 + `carbon_monoxide` + `hydrogen`；B S0225 + `ethyl_chloroacetate` | A：C8H8 + CO + H2 → C9H10O；B：C8H8O + C4H7ClO2 + NaOH → C12H14O3 + NaCl + H2O；C12H14O3 + H2O → C9H10O + CO2 + C2H6O |
| S1257 | 龙葵醛二甲缩醛 | `hydratropic_aldehyde_dimethyl_acetal` | T2（S1256 为 T3） | ST07 | S1256 + `methanol` | C9H10O + 2 CH4O → C11H16O2 + H2O |
| S1258 | 羟基香茅醛二乙缩醛 | `hydroxycitronellal_diethyl_acetal` | T2（S1173 为 T3） | ST07 | S1173 + `ethanol` | C10H20O2 + 2 C2H6O → C14H30O3 + H2O |
| S1259 | 柠檬醛二甲缩醛 | `citral_dimethyl_acetal` | T2 | ST07 | 125 + `methanol` | C10H16O + 2 CH4O → C12H22O2 + H2O |
| S1260 | 4-甲基-5-(2-乙酰氧乙基)-噻唑 | `methyl_acetoxyethyl_thiazole` | T3 | ST02 | S0727 + `acetic_anhydride` | C6H9NOS + C4H6O3 → C8H11NO2S + C2H4O2 |
| S1261 | α-丁基肉桂醛 | `alpha_butylcinnamaldehyde` | T3 | ST06 | 127 + S0130 | C7H6O + C6H12O → C13H16O + H2O |
| S1262 | 4-庚烯-3-酮 | `hept_4_en_3_one` | T3 | ST06 | S0203 + S0117 | C4H8O + C3H6O → C7H12O + H2O |
| S1263 | 4-甲基-1-苯基-2-戊酮 | `benzyl_isobutyl_ketone` | T3 | ST27 | S0316 + S0303 | C8H8O2 + C5H10O2 → C12H16O + CO2 + H2O |
| S1264 | 1-(对-甲氧基苯基)-1-戊烯-3-酮 | `p_methoxyphenyl_pentenone` | T3 | ST06 | S0163 + S0203 | C8H8O2 + C4H8O → C12H14O2 + H2O |
| S1265 | α-己叉基环戊酮 | `alpha_hexylidenecyclopentanone` | T3 | ST06 | `cyclopentanone` + S0130 | C5H8O + C6H12O → C11H18O + H2O |
| S1266 | 四甲基乙基环己烯酮 | `tetramethyl_ethylcyclohexenone` | T3 | ST06 | S0203 | 3 C4H8O → C12H20O + 2 H2O |
| S1267 | 糠硫醇甲酸酯 | `furfurylthiol_formate` | T3 | ST10 | S0692 + `formic_acid` + `acetic_anhydride` | CH2O2 + C4H6O3 → C3H4O3 + C2H4O2；C5H6OS + C3H4O3 → C6H6O2S + C2H4O2 |
| S1268 | 甲基β-萘酮 | `methyl_beta_naphthyl_ketone` | T3 | ST15 | `naphthalene` + `acetic_anhydride` | C10H8 + C4H6O3 → C12H10O + C2H4O2 |
| S1269 | 2-(3-苯丙基)四氢呋喃 | `phenylpropyl_tetrahydrofuran` | T3 | ST06 + ST05 | `furfural` + S0225 + `hydrogen` | C5H4O2 + C8H8O → C13H10O2 + H2O；C13H10O2 + 5 H2 → C13H18O + H2O |
| S1270 | 烯丙基乙酸 | `allylacetic_acid` | T3 | ST28 | S0403 + `allyl_chloride` | C7H12O4 + C3H5Cl + NaOH → C10H16O4 + NaCl + H2O；C10H16O4 + 2 H2O → C5H8O2 + 2 C2H6O + CO2 |
| S1271 | 甲酸二甲基苄基原酯 | `dimethylbenzylcarbinyl_formate` | T2（S0048 为 T3） | ST01 | S0048 + `formic_acid` | C10H14O + CH2O2 → C11H14O2 + H2O |
| S1272 | 4-乙酰基-6-叔丁基-1,1-二甲基茚满 | `acetyl_butyl_dimethylindane` | T3 | ST15 | `tert_butyl_dimethylindane` + `acetic_anhydride` | C15H22 + C4H6O3 → C17H24O + C2H4O2 |
| S1273 | 癸醛二甲缩醛 | `decanal_dimethyl_acetal` | T2（S0152 为 T3） | ST07 | S0152 + `methanol` | C10H20O + 2 CH4O → C12H26O2 + H2O |
| S1274 | 乙酸环己基乙酯 | `cyclohexaneethyl_acetate` | T3 | ST05 | S0383 + `hydrogen` | C10H12O2 + 3 H2 → C10H18O2 |
| S1275 | 对-甲苯氧基乙酸乙酯 | `ethyl_p_tolyloxyacetate` | T3 | ST09 | `p_cresol` + `ethyl_chloroacetate` + `sodium_hydroxide` | C7H8O + C4H7ClO2 + NaOH → C11H14O3 + NaCl + H2O |
| S1276 | 乙酸二甲基苯乙基原酯 | `dimethylphenethylcarbinyl_acetate` | T3 | ST02 | S1234 + `acetic_anhydride` | C11H16O + C4H6O3 → C13H18O2 + C2H4O2 |
| S1277 | 丙酸甲基苯基原酯 | `methylphenylcarbinyl_propionate` | T2（S0032 为 T3） | ST01 | S0032 + `propionic_acid` | C8H10O + C3H6O2 → C11H14O2 + H2O |
| S1278 | 2-呋喃基丙烯酸丙酯 | `propyl_furanacrylate` | T2（`furylacrylic_acid` 为 T3） | ST01 | `furylacrylic_acid` + `n_propanol` | C7H6O3 + C3H8O → C10H12O3 + H2O |
| S1279 | 异丁酸二甲基苯乙基原酯 | `dimethylphenethylcarbinyl_isobutyrate` | T3 | ST32 | S1234 + `isobutyric_acid` + `thionyl_chloride` | C11H16O + C4H8O2 + Cl2OS → C15H22O2 + SO2 + 2 HCl |
| S1280 | 异丁酸2-苯氧基乙酯 | `phenoxyethyl_isobutyrate` | T2（`phenoxyethanol` 为 T3） | ST01 | `phenoxyethanol` + `isobutyric_acid` | C8H10O2 + C4H8O2 → C12H16O3 + H2O |
| S1281 | 十三碳二酸环乙二醇二酯 | `ethylene_brassylate` | T3 | ST01 | `brassylic_acid` + `ethylene_glycol` | C13H24O4 + C2H6O2 → C15H26O4 + 2 H2O |
| S1282 | 邻氨基苯甲酸异丁酯 | `isobutyl_anthranilate` | T3 | ST03 | S0491 + S0005 | C8H9NO2 + C4H10O → C11H15NO2 + CH4O |
| S1283 | 对-叔丁基苯乙酸甲酯 | `methyl_tert_butylphenylacetate` | T3 | ST19 | `tert_butylbenzyl_chloride` + `carbon_monoxide` + `methanol` | C11H15Cl + CO + CH4O + NaOH → C13H18O2 + NaCl + H2O |
| S1284 | 苯氧乙酸烯丙酯 | `allyl_phenoxyacetate` | T2（`allyl_alcohol`、`phenoxyacetic_acid` 为 T3） | ST01 | `phenoxyacetic_acid` + `allyl_alcohol` | C8H8O3 + C3H6O → C11H12O3 + H2O |
| S1285 | 苯乙酸辛酯 | `octyl_phenylacetate` | T2（S0014、S0316 为 T3） | ST01 | S0316 + S0014 | C8H8O2 + C8H18O → C16H24O2 + H2O |
| S1286 | 苯乙酸苄酯 | `benzyl_phenylacetate` | T2（S0033、S0316 为 T3） | ST01 | S0316 + S0033 | C8H8O2 + C7H8O → C15H14O2 + H2O |
| S1287 | 苯乙酸芳樟酯 | `linalyl_phenylacetate` | T3 | ST32 | S0029 + S0316 + `thionyl_chloride` | C10H18O + C8H8O2 + Cl2OS → C18H24O2 + SO2 + 2 HCl |
| S1288 | 苯乙酸香茅酯 | `citronellyl_phenylacetate` | T2（S0040、S0316 为 T3） | ST01 | S0040 + S0316 | C10H20O + C8H8O2 → C18H26O2 + H2O |
| S1289 | 苯乙酸愈创木酚酯 | `guaiacyl_phenylacetate` | T3 | ST32 | `guaiacol` + S0316 + `thionyl_chloride` | C7H8O2 + C8H8O2 + Cl2OS → C15H14O3 + SO2 + 2 HCl |
| S1290 | 3-甲基2-丁烯酸2-苯乙酯（千里酸苯乙酯） | `phenethyl_senecioate` | T2（S0034、S0345 为 T3） | ST01 | S0034 + S0345 | C8H10O + C5H8O2 → C13H16O2 + H2O |
| S1291 | 3-苯基缩水甘油酸乙酯 | `ethyl_phenylglycidate` | T3 | ST06 | 127 + `ethyl_chloroacetate` | C7H6O + C4H7ClO2 + NaOH → C11H12O3 + NaCl + H2O |
| S1292 | 肉桂酸芳樟酯 | `linalyl_cinnamate` | T3 | ST32 | S0029 + S0318 + `thionyl_chloride` | C10H18O + C9H8O2 + Cl2OS → C19H24O2 + SO2 + 2 HCl |
| S1293 | 1,2-二[(1'-乙氧基)-乙氧基]丙烷 | `diethoxyethoxy_propane` | T3 | ST25 | `propylene_glycol` + `ethyl_vinyl_ether` | C3H8O2 + 2 C4H8O → C11H24O4 |
| S1294 | N,2,3-三甲基-2-异丙基丁酰胺 | `isopropyl_trimethylbutyramide` | T3 | ST20 | `diisopropylpropionitrile` + `methanol` | C9H17N + CH4O → C10H21NO |
| S1295 | N-乙基-2-异丙基-5-甲基-环己烷甲酰胺 | `ethyl_menthanecarboxamide` | T3 | ST20 | `menthanecarboxylic_acid` + `thionyl_chloride` + S1100 | C11H20O2 + Cl2OS → C11H19ClO + SO2 + HCl；C11H19ClO + C2H7N + NaOH → C13H25NO + NaCl + H2O |
| S1296 | 3-l-䓝氧基-1,2-丙二醇 | `menthoxypropanediol` | T3 | ST09 | 29 + `epichlorohydrin` + `sodium_acetate` + `sodium_hydroxide` | C10H20O + C3H5ClO → C13H25ClO2；C13H25ClO2 + C2H3NaO2 → C15H28O4 + NaCl；C15H28O4 + NaOH → C13H26O3 + C2H3NaO2 |
| S1297 | 香兰基丁醚 | `vanillyl_butyl_ether` | T3 | ST09 | S1481 + S0004 | C8H10O3 + C4H10O → C12H18O3 + H2O |
| S1298 | 9-癸烯醛 | `dec_9_enal` | T3（S0334 为 T4） | ST05 | S0334 + `thionyl_chloride` + `hydrogen` | C10H18O2 + Cl2OS → C10H17ClO + SO2 + HCl；C10H17ClO + H2 → C10H18O + HCl |
| S1299 | 2-仲丁基环己酮 | `sec_butylcyclohexanone` | T3 | ST05 | `sec_butylphenol` + `hydrogen` | C10H14O + 2 H2 → C10H18O |
| S1300 | 2,3-十一碳二酮 | `undecane_2_3_dione` | T3 | ST06 + ST04 | S0145 + `acetaldehyde` + `thiamine_hydrochloride` | C9H18O + C2H4O → C11H22O2；C11H22O2 → C11H20O2 + H2 |
| S1301 | 环己烷基甲酸 | `cyclohexanecarboxylic_acid` | T3 | ST05 | `benzoic_acid` + `hydrogen` | C7H6O2 + 3 H2 → C7H12O2 |
| S1302 | 5和6-癸烯酸（牛奶内酯） | `milk_lactone` | T3 | ST08 | S0634 | C10H18O2 → C10H18O2 |
| S1303 | 八乙酸蔗糖酯 | `sucrose_octaacetate` | T3 | ST02 | `sucrose` + `acetic_anhydride` + `sodium_acetate` | C12H22O11 + 8 C4H6O3 → C28H38O19 + 8 C2H4O2 |
| S1304 | 丁酸烯丙酯 | `allyl_butyrate` | T2（`allyl_alcohol` 为 T3） | ST01 | `allyl_alcohol` + `butyric_acid` | C3H6O + C4H8O2 → C7H12O2 + H2O |
| S1305 | 异丁酸香兰素酯 | `vanillin_isobutyrate` | T3 | ST32 | 30 + `isobutyric_acid` + `thionyl_chloride` | C8H8O3 + C4H8O2 + Cl2OS → C12H14O4 + SO2 + 2 HCl |
| S1306 | 戊二酸单l-薄荷醇酯 | `monomenthyl_glutarate` | T3 | ST02 | 29 + `glutaric_anhydride` | C10H20O + C5H6O3 → C15H26O4 |
| S1307 | 苯甲酰基乙酸乙酯 | `ethyl_benzoylacetate` | T3 | ST06 | S0486 + `ethyl_acetate` + `sodium_methoxide` | C9H10O2 + C4H8O2 → C11H12O3 + C2H6O |
| S1308 | ε-十二内酯 | `epsilon_dodecalactone` | T3 | ST24 | `hexylcyclohexanone` + `hydrogen_peroxide` | C12H22O + H2O2 → C12H22O2 + H2O |
| S1309 | 八氢香豆素 | `octahydrocoumarin` | T3 | ST05 | S0662 + `hydrogen` | C9H8O2 + 3 H2 → C9H14O2 |
| S1310 | 2,5-二甲基-3-呋喃硫醇 | `dimethyl_3_furanthiol` | T3 | ST10 | S0933 + `hydrogen_sulfide` | C6H8O2 + H2S → C6H8OS + H2O |
| S1311 | 1,2-丁二硫醇 | `butane_1_2_dithiol` | T3 | ST10 | `dichlorobutane` + `hydrogen_sulfide` + `sodium_hydroxide` | C4H8Cl2 + 2 H2S + 2 NaOH → C4H10S2 + 2 NaCl + 2 H2O |
| S1312 | 双-(2,5-二甲基-3-呋喃基)二硫醚 | `bis_dimethylfuryl_disulfide` | T3 | ST10 | S1310 + `oxygen` | 4 C6H8OS + O2 → 2 C12H14O2S2 + 2 H2O |
| S1313 | 丙基2-甲基-3-呋喃基二硫醚 | `propyl_methylfuryl_disulfide` | T3（S0471 为 T4） | ST10 | S0471 + S0801 + `oxygen` | 2 C5H6OS + 2 C3H8S + O2 → 2 C8H12OS2 + 2 H2O |
| S1314 | 二环己基二硫醚 | `dicyclohexyl_disulfide` | T3 | ST10 | `cyclohexanethiol` + `oxygen` | 4 C6H12S + O2 → 2 C12H22S2 + 2 H2O |
| S1315 | 糠基异丙基硫醚 | `furfuryl_isopropyl_sulfide` | T3 | ST10 | S0692 + `isopropyl_chloride` + `sodium_hydroxide` | C5H6OS + C3H7Cl + NaOH → C8H12OS + NaCl + H2O |
| S1316 | 2-乙基苯硫酚 | `o_ethylthiophenol` | T3 | ST10 | `ethylbenzene` + `chlorosulfonic_acid` + `materials/zinc` + `hydrogen_chloride` | C8H10 + 2 ClHO3S → C8H9ClO2S + H2SO4 + HCl；C8H9ClO2S + 3 Zn + 5 HCl → C8H10S + 3 ZnCl2 + 2 H2O |
| S1317 | 2-(乙酰氧基)丙酸甲硫醇酯 | `methylthio_acetoxypropionate` | T3 | ST10 | 2 + `acetic_anhydride` + `thionyl_chloride` + S0686 | C3H6O3 + C4H6O3 → C5H8O4 + C2H4O2；C5H8O4 + Cl2OS → C5H7ClO3 + SO2 + HCl；C5H7ClO3 + CH4S → C6H10O3S + HCl |
| S1318 | 2-(丙酰氧基)丙酸甲硫醇酯 | `methylthio_propionyloxypropionate` | T3 | ST10 | 2 + `propionic_acid` + `thionyl_chloride` + S0686 | C3H6O2 + Cl2OS → C3H5ClO + SO2 + HCl；C3H6O3 + C3H5ClO → C6H10O4 + HCl；C6H10O4 + Cl2OS → C6H9ClO3 + SO2 + HCl；C6H9ClO3 + CH4S → C7H12O3S + HCl |
| S1319 | 3-糠硫基丙酸乙酯 | `ethyl_furfurylthiopropionate` | T3 | ST25 | S0692 + S0523 | C5H6OS + C5H8O2 → C10H14O3S |
| S1320 | 2-甲硫基吡嗪 | `methylthiopyrazine` | T3 | ST10 | `chloropyrazine` + S0686 + `sodium_hydroxide` | C4H3ClN2 + CH4S + NaOH → C5H6N2S + NaCl + H2O |
| S1321 | 异硫氰酸苯乙酯 | `phenethyl_isothiocyanate` | T3 | ST10 | S0782 + `carbon_disulfide` + `hydrogen_peroxide` | C8H11N + CS2 + H2O2 → C9H9NS + S + 2 H2O |
| S1322 | 2-(3-苯丙基)吡啶 | `phenylpropyl_pyridine` | T3 | ST28 | `alpha_picoline` + `phenethyl_chloride` + `sodium` + `ammonia` | C6H7N + C8H9Cl + NaNH2 → C14H15N + NaCl + NH3 |
| S1323 | 4,5-二甲基-2-乙基-3-噻唑啉 | `ethyl_dimethyl_thiazoline` | T3 | ST11 | S0282 + S0117 + `ammonia` | C4H8OS + C3H6O + NH3 → C7H13NS + 2 H2O |
| S1324 | 2-仲丁基-4,5-二甲基-3-噻唑啉 | `sec_butyl_dimethyl_thiazoline` | T3 | ST11 | S0282 + S0120 + `ammonia` | C4H8OS + C5H10O + NH3 → C9H17NS + 2 H2O |
| S1325 | 吡嗪乙硫醇 | `pyrazine_ethanethiol` | T3 | ST25 | `vinylpyrazine` + `hydrogen_sulfide` | C6H6N2 + H2S → C6H8N2S |
| S1326 | 水杨酸苯酯 | `phenyl_salicylate` | T3 | ST32 | S0349 + `phenol` + `thionyl_chloride` | C7H6O3 + C6H6O + Cl2OS → C13H10O3 + SO2 + 2 HCl |
| S1327 | 庚醛二甲缩醛 | `heptanal_dimethyl_acetal` | T2（S0136 为 T3） | ST07 | S0136 + `methanol` | C7H14O + 2 CH4O → C9H20O2 + H2O |
| S1328 | 羟基香茅醛二甲缩醛 | `hydroxycitronellal_dimethyl_acetal` | T2（S1173 为 T3） | ST07 | S1173 + `methanol` | C10H20O2 + 2 CH4O → C12H26O3 + H2O |
| S1329 | 对-丙基茴香醚 | `p_propylanisole` | T3 | ST05 | S1497 + `hydrogen` | C10H12O + H2 → C10H14O |
| S1330 | 异丁酸对-甲酚酯 | `p_tolyl_isobutyrate` | T3 | ST32 | `p_cresol` + `isobutyric_acid` + `thionyl_chloride` | C7H8O + C4H8O2 + Cl2OS → C11H14O2 + SO2 + 2 HCl |
| S1331 | 异丁酸邻-甲酚酯 | `o_tolyl_isobutyrate` | T3 | ST32 | S0095 + `isobutyric_acid` + `thionyl_chloride` | C7H8O + C4H8O2 + Cl2OS → C11H14O2 + SO2 + 2 HCl |
| S1332 | 柠檬醛丙二醇缩醛 | `citral_propylene_glycol_acetal` | T2 | ST07 | 125 + `propylene_glycol` | C10H16O + C3H8O2 → C13H22O2 + H2O |
| S1333 | 反式-2-己烯醛二乙缩醛 | `trans_2_hexenal_diethyl_acetal` | T2（S0131 为 T3） | ST07 | S0131 + `ethanol` | C6H10O + 2 C2H6O → C10H20O2 + H2O |
| S1334 | 2-巯基噻吩 | `mercaptothiophene` | T3 | ST10 | S0897 + `chlorosulfonic_acid` + `materials/zinc` + `hydrogen_chloride` | C4H4S + 2 ClHO3S → C4H3ClO2S2 + H2SO4 + HCl；C4H3ClO2S2 + 3 Zn + 5 HCl → C4H4S2 + 3 ZnCl2 + 2 H2O |
| S1335 | 对-䓝-3,8-二醇 | `p_menthane_3_8_diol` | T3 | ST14 | S0173 + `sulfuric_acid` | C10H18O + H2O → C10H20O2 |
| S1336 | 1,8-辛二硫醇 | `octane_1_8_dithiol` | T3 | ST10 | `dichlorooctane` + `hydrogen_sulfide` + `sodium_hydroxide` | C8H16Cl2 + 2 H2S + 2 NaOH → C8H18S2 + 2 NaCl + 2 H2O |
| S1337 | 螺[2,4-二硫杂-1-甲基-8-氧杂双环[3.3.0]-辛烷-3,3’-(1’-氧杂-2’-甲基)环戊烷] | `spiro_dithia_oxabicyclooctane` | T3 | ST10 | S0275 + `hydrogen_sulfide` | 2 C5H8O2 + 2 H2S → C10H16O2S2 + 2 H2O |
| S1338 | 3-壬烯-2-酮 | `non_3_en_2_one` | T3 | ST06 | S0130 + `acetone` | C6H12O + C3H6O → C9H16O + H2O |
| S1339 | 3-甲基-2,4-壬二酮 | `methyl_2_4_nonanedione` | T3 | ST06 | S0203 + S0457 + `sodium_methoxide` | C4H8O + C7H14O2 → C10H18O2 + CH4O |
| S1340 | 2,5-二甲基-3-硫代乙酰氧基呋喃 | `dimethyl_thioacetoxyfuran` | T3 | ST10 | S1310 + `acetic_anhydride` | C6H8OS + C4H6O3 → C8H10O2S + C2H4O2 |
| S1341 | 反式-4-己烯醛 | `trans_4_hexenal` | T3 | ST04 | S0012 | C6H12O → C6H10O + H2 |
| S1342 | 3-[(2-甲基-3-呋喃)硫基]-2-丁酮 | `methylfurylthio_butanone` | T3（S0471 为 T4） | ST10 | S0471 + `chlorobutanone` + `sodium_hydroxide` | C5H6OS + C4H7ClO + NaOH → C9H12O2S + NaCl + H2O |
| S1343 | 3-巯基-2-甲基戊醛 | `mercapto_methylpentanal` | T3 | ST25 | S0127 + `hydrogen_sulfide` | C6H10O + H2S → C6H12OS |
| S1344 | 2-(l-䓝氧基)乙醇 | `menthoxyethanol` | T3 | ST09 | 29 + `ethylene_oxide` | C10H20O + C2H4O → C12H24O2 |
| S1345 | 丙酸四氢糠酯 | `tetrahydrofurfuryl_propionate` | T2（S0796 为 T3） | ST01 | S0796 + `propionic_acid` | C5H10O2 + C3H6O2 → C8H14O3 + H2O |
| S1346 | 异戊酸烯丙酯 | `allyl_isovalerate` | T2（`allyl_alcohol`、S0303 为 T3） | ST01 | `allyl_alcohol` + S0303 | C3H6O + C5H10O2 → C8H14O2 + H2O |
| S1347 | 3-羟甲基-2-庚酮 | `hydroxymethyl_heptanone` | T3 | ST06 | S0216 + `formaldehyde` | C7H14O + CH2O → C8H16O2 |
| S1348 | 三丙酸甘油酯 | `glyceryl_tripropanoate` | T2 | ST01 | `glycerol` + `propionic_acid` | C3H8O3 + 3 C3H6O2 → C12H20O6 + 3 H2O |
| S1349 | 辛酸糠酯 | `furfuryl_octanoate` | T3 | ST03 | S0795 + S0472 | C5H6O2 + C9H18O2 → C13H20O3 + CH4O |
| S1350 | 丁酸反式-2-辛烯醇酯 | `trans_2_octenyl_butanoate` | T2（S0866 为 T3） | ST01 | S0866 + `butyric_acid` | C8H16O + C4H8O2 → C12H22O2 + H2O |
| S1351 | 苯乙醛二异丁缩醛 | `phenylacetaldehyde_diisobutyl_acetal` | T2（S0168 为 T3） | ST07 | S0168 + S0005 | C8H8O + 2 C4H10O → C16H26O2 + H2O |
| S1352 | 1,3-二苯基-2-丙酮 | `dibenzyl_ketone` | T3 | ST27 | S0316 | 2 C8H8O2 → C15H14O + CO2 + H2O |
| S1353 | 10-十一烯酸丁酯 | `butyl_undecylenate` | T2（S0004、S0336 为 T3） | ST01 | S0336 + S0004 | C11H20O2 + C4H10O → C15H28O2 + H2O |
| S1354 | 乙酸檀香酯 | `santalyl_acetate` | T3 | ST02 | N277 + `acetic_anhydride` | C15H24O + C4H6O3 → C17H26O2 + C2H4O2 |
| S1355 | 2-乙基丁酸香叶酯 | `geranyl_ethylbutyrate` | T2（S0299 为 T3） | ST01 | S0039 + S0299 | C10H18O + C6H12O2 → C16H28O2 + H2O |
| S1356 | 3-羟甲基-2-辛酮 | `hydroxymethyl_octanone` | T3 | ST06 | S0258 + `formaldehyde` | C8H16O + CH2O → C9H18O2 |
| S1357 | 1,2-环己二酮 | `cyclohexane_1_2_dione` | T3 | ST04 | S0290 + `oxygen` + `sodium_methoxide` | C6H10O + O2 → C6H8O2 + H2O |
| S1359 | 赤、苏-3-巯基-2-甲基丁-1-醇 | `mercapto_2_methylbutanol` | T3 | ST25 + ST05 | S0121 + `hydrogen_sulfide` + `hydrogen` | C5H8O + H2S → C5H10OS；C5H10OS + H2 → C5H12OS |
| S1360 | 4-甲基联苯 | `p_methylbiphenyl` | T3 | ST15 | S1002 + `methanol` | C12H10 + CH4O → C13H12 + H2O |
| S1361 | α-戊基肉桂醇 | `alpha_amylcinnamyl_alcohol` | T3 | ST05 | S0273 + `hydrogen` | C14H18O + H2 → C14H20O |
| S1362 | 1-苯基-3-甲基-戊醇-3 | `phenyl_3_methyl_3_pentanol` | T3 | ST16 | `phenethyl_chloride` + S0203 | C8H9Cl + Mg + C4H8O + H2O → C12H18O + Mg(OH)Cl |
| S1363 | 5-苯基戊醇 | `phenylpentanol` | T3 | ST06 + ST05 | 126 + `acetaldehyde` + `hydrogen` | C9H8O + C2H4O → C11H10O + H2O；C11H10O + 3 H2 → C11H16O |
| S1364 | 对-䓝烷醇-2 | `p_menthan_2_ol` | T3 | ST05 | S0868 + `hydrogen`（`catalysts/copper`，只还原羰基） | C10H18O + H2 → C10H20O |
| S1365 | 脱氢二氢紫罗兰醇 | `dehydrodihydroionol` | T3 | ST05 | `dehydrodihydroionone` + `hydrogen` | C13H20O + H2 → C13H22O |
| S1366 | 乙基葑醇 | `ethylfenchol` | T3 | ST16 | S0274 + `ethyl_chloride` | C10H16O + C2H5Cl + Mg + H2O → C12H22O + Mg(OH)Cl |
| S1367 | 辛烯基琥珀酸单阿拉伯胶酯 | `gum_arabic_octenylsuccinate` | T3 | ST02 | `gum_arabic` + `octenyl_succinic_anhydride` | 阿拉伯胶羟基与酸酐开环成半酯，组成不定 |
| S1368 | N1-(2-甲氧基-4-甲基苄基)-N2-[2-(5-甲基-2-吡啶基)乙基]草酰胺 | `methoxymethylbenzyl_oxalamide` | T3 | ST20 | `diethyl_oxalate` + `methoxymethylbenzylamine` + `methylpyridylethylamine` | C6H10O4 + C9H13NO + C8H12N2 → C19H23N3O3 + 2 C2H6O |
| S1369 | N1-(2,4-二甲氧基苄基)-N2-[2-(2-吡啶基)乙基]草酰胺 | `dimethoxybenzyl_oxalamide` | T3 | ST20 | `diethyl_oxalate` + `dimethoxybenzylamine` + `pyridylethylamine` | C6H10O4 + C9H13NO2 + C7H10N2 → C18H21N3O4 + 2 C2H6O |
| S1370 | N-(4-庚基)-(3,4-亚甲二氧基)苯甲酰胺 | `heptyl_piperonylamide` | T3 | ST20 | `piperonylic_acid` + `thionyl_chloride` + `heptan_4_amine` | C8H6O4 + Cl2OS → C8H5ClO3 + SO2 + HCl；C8H5ClO3 + C7H17N + NaOH → C15H21NO3 + NaCl + H2O |
| S1371 | 二苄醚 | `dibenzyl_ether` | T3 | ST09 | S0033 + `sulfuric_acid` | 2 C7H8O → C14H14O + H2O |
| S1372 | 5-羟基-十二酸甘油酯 | `glyceryl_hydroxydodecanoate` | T3 | ST03 | S0636 + `glycerol` | C12H22O2 + C3H8O3 → C15H30O5 |
| S1373 | 三丁酸甘油酯 | `tributyrin` | T2 | ST01 | `glycerol` + `butyric_acid` | C3H8O3 + 3 C4H8O2 → C15H26O6 + 3 H2O |
| S1374 | 壬酸烯丙酯 | `allyl_nonanoate` | T2（`allyl_alcohol`、S0310 为 T3） | ST01 | `allyl_alcohol` + S0310 | C3H6O + C9H18O2 → C12H22O2 + H2O |
| S1375 | 5-羟基癸酸甘油酯 | `glyceryl_hydroxydecanoate` | T3 | ST03 | S0634 + `glycerol` | C10H18O2 + C3H8O3 → C13H26O5 |
| S1376 | 丙酸3-苯基丙酯 | `phenylpropyl_propionate` | T2（S0035 为 T3） | ST01 | S0035 + `propionic_acid` | C9H12O + C3H6O2 → C12H16O2 + H2O |
| S1377 | 肉桂酸异丙酯 | `isopropyl_cinnamate` | T2（S0003、S0318 为 T3） | ST01 | S0318 + S0003 | C9H8O2 + C3H8O → C12H14O2 + H2O |
| S1378 | 2-酮基-4-丁硫醇 | `keto_butanethiol` | T3 | ST25 | `methyl_vinyl_ketone` + `hydrogen_sulfide` | C4H6O + H2S → C4H8OS |
| S1379 | 甲基-对-甲苯缩水甘油酸乙酯 | `ethyl_methyl_tolylglycidate` | T3 | ST06 | S0226 + `ethyl_chloroacetate` | C9H10O + C4H7ClO2 + NaOH → C13H16O3 + NaCl + H2O |
| S1380 | 5-羟基-8-十一碳烯酸δ-内酯 | `delta_undec_8_enolactone` | T3 | ST24 | `hexenylcyclopentanone` + `hydrogen_peroxide` | C11H18O + H2O2 → C11H18O2 + H2O |
| S1381 | N-环丙基-反式-2-顺式-6-壬二烯酰胺 | `cyclopropyl_nonadienamide` | T3 | ST20 | `nonadienoic_acid` + `thionyl_chloride` + `cyclopropylamine` | C9H14O2 + Cl2OS → C9H13ClO + SO2 + HCl；C9H13ClO + C3H7N + NaOH → C12H19NO + NaCl + H2O |
| S1382 | N-乙基-反式-2-顺式-6-壬二烯酰胺 | `ethyl_nonadienamide` | T3 | ST20 | `nonadienoic_acid` + `thionyl_chloride` + S1100 | C9H14O2 + Cl2OS → C9H13ClO + SO2 + HCl；C9H13ClO + C2H7N + NaOH → C11H19NO + NaCl + H2O |
| S1383 | 2,4-二甲基-1,3-二氧戊环 | `dimethyl_1_3_dioxolane` | T2 | ST07 | `acetaldehyde` + `propylene_glycol` | C2H4O + C3H8O2 → C5H10O2 + H2O |
| S1384 | β-萘甲醚 | `beta_naphthyl_methyl_ether` | T3 | ST09 | `beta_naphthol` + `methanol` + `sulfuric_acid` | C10H8O + CH4O → C11H10O + H2O |
| S1385 | 二羟基丙酮 | `dihydroxyacetone` | T4 | ST18 | `glycerol` + `cultures/gluconobacter` | 2 C3H8O3 + O2 → 2 C3H6O3 + 2 H2O |
| S1386 | 二苯基二硫醚 | `diphenyl_disulfide` | T3 | ST10 | S0892 + `oxygen` | 4 C6H6S + O2 → 2 C12H10S2 + 2 H2O |
| S1387 | 乙基香芹酚 | `ethyl_carvacrol` | T3 | ST09 | S0111 + `ethyl_chloride` + `sodium_hydroxide` | C10H14O + C2H5Cl + NaOH → C12H18O + NaCl + H2O |
| S1388 | 甲基苯甲醛甘油缩醛(邻、间、对异构体混合物) | `tolualdehyde_glyceryl_acetal` | T2（S0166 为 T3） | ST07 | S0166 + `glycerol` | C8H8O + C3H8O3 → C11H14O3 + H2O |
| S1389 | (+/-)-反式和顺式-4,8-二甲基-3,7-壬二烯-2-醇 | `dimethyl_nonadienol` | T3 | ST16 | 125 + `methyl_chloride` | C10H16O + CH3Cl + Mg + H2O → C11H20O + Mg(OH)Cl |
| S1390 | (+/-)-反式和顺式-4,8-二甲基-3,7-壬二烯-2-醇乙酸酯 | `dimethyl_nonadienyl_acetate` | T3 | ST02 | S1389 + `acetic_anhydride` | C11H20O + C4H6O3 → C13H22O2 + C2H4O2 |
| S1391 | (反式和顺式)-1-甲氧基-1-癸烯 | `methoxy_1_decene` | T3 | ST30 | S1273 | C12H26O2 → C11H22O + CH4O |
| S1392 | 2-(4-甲基-5-噻唑基)乙醇癸酸酯 | `methylthiazolylethyl_decanoate` | T2（S0727 为 T3） | ST01 | S0727 + `capric_acid` | C6H9NOS + C10H20O2 → C16H27NO2S + H2O |
| S1393 | 2-(4-甲基-5-噻唑基)乙醇异丁酸酯 | `methylthiazolylethyl_isobutyrate` | T2（S0727 为 T3） | ST01 | S0727 + `isobutyric_acid` | C6H9NOS + C4H8O2 → C10H15NO2S + H2O |
| S1394 | 2-(4-甲基-5-噻唑基)乙醇甲酸酯 | `methylthiazolylethyl_formate` | T2（S0727 为 T3） | ST01 | S0727 + `formic_acid` | C6H9NOS + CH2O2 → C7H9NO2S + H2O |
| S1395 | 异戊酸3-苯丙酯 | `phenylpropyl_isovalerate` | T2（S0035、S0303 为 T3） | ST01 | S0035 + S0303 | C9H12O + C5H10O2 → C14H20O2 + H2O |
| S1396 | dl-薄荷脑(+/-)-1,2-丙二醇碳酸酯 | `dl_menthyl_propylene_glycol_carbonate` | T3 | ST03 | `dl_menthol` + `propylene_carbonate` | C10H20O + C4H6O3 → C14H26O4 |
| S1397 | 乙酸1-乙氧基乙醇酯 | `ethoxyethyl_acetate` | T3 | ST25 | `acetic_acid` + `ethyl_vinyl_ether` | C2H4O2 + C4H8O → C6H12O3 |
| S1398 | N-异丁基-反-2-反-4-癸二烯酸酰胺 | `isobutyl_decadienamide` | T3 | ST20 | `decadienoic_acid` + `thionyl_chloride` + `isobutylamine` | C10H16O2 + Cl2OS → C10H15ClO + SO2 + HCl；C10H15ClO + C4H11N + NaOH → C14H25NO + NaCl + H2O |
| S1399 | 二苯乙醇酮 | `benzoin` | T3 | ST06 | 127 + `thiamine_hydrochloride` | 2 C7H6O → C14H12O2 |
| S1400 | 甲基异戊基二硫醚 | `methyl_isopentyl_disulfide` | T3 | ST10 | S0900 + S0686 + `oxygen` | 2 C5H12S + 2 CH4S + O2 → 2 C6H14S2 + 2 H2O |
| S1401 | 邻氨基苯甲酸烯丙酯 | `allyl_anthranilate` | T3 | ST03 | S0491 + `allyl_alcohol` | C8H9NO2 + C3H6O → C10H11NO2 + CH4O |

### 1.11 序号 1401–1504（S1402–S1506，104 条，历年增补品种（四））

| 编码 | 名称 | 物料 ID | 阶段 | 模板 | 原料 | 关键反应 |
| --- | --- | --- | --- | --- | --- | --- |
| S1402 | 6-环己基己酸烯丙酯 | `allyl_cyclohexanehexanoate` | T2（`allyl_alcohol`、`cyclohexylhexanoic_acid` 为 T3） | ST01 | `cyclohexylhexanoic_acid` + `allyl_alcohol` | C12H22O2 + C3H6O → C15H26O2 + H2O |
| S1403 | 5-环己基戊酸烯丙酯 | `allyl_cyclohexanevalerate` | T2（`allyl_alcohol`、`cyclohexylvaleric_acid` 为 T3） | ST01 | `cyclohexylvaleric_acid` + `allyl_alcohol` | C11H20O2 + C3H6O → C14H24O2 + H2O |
| S1404 | 2-乙基丁酸烯丙酯 | `allyl_2_ethylbutyrate` | T2（`allyl_alcohol`、S0299 为 T3） | ST01 | S0299 + `allyl_alcohol` | C6H12O2 + C3H6O → C9H16O2 + H2O |
| S1405 | 惕各酸烯丙酯 | `allyl_tiglate` | T2（`allyl_alcohol`、S0350 为 T3） | ST01 | S0350 + `allyl_alcohol` | C5H8O2 + C3H6O → C8H12O2 + H2O |
| S1406 | 10-十一烯酸烯丙酯 | `allyl_10_undecenoate` | T2（`allyl_alcohol`、S0336 为 T3） | ST01 | S0336 + `allyl_alcohol` | C11H20O2 + C3H6O → C14H24O2 + H2O |
| S1407 | α-戊基肉桂醛二甲缩醛 | `amylcinnamaldehyde_dimethyl_acetal` | T2（S0273 为 T3） | ST07 | S0273 + `methanol` | C14H18O + 2 CH4O → C16H24O2 + H2O |
| S1408 | 乙酸α-戊基肉桂酯 | `amylcinnamyl_acetate` | T3 | ST02 | S1361 + `acetic_anhydride` | C14H20O + C4H6O3 → C16H22O2 + C2H4O2 |
| S1409 | 甲酸α-戊基肉桂酯 | `amylcinnamyl_formate` | T2（S1361 为 T3） | ST01 | S1361 + `formic_acid` | C14H20O + CH2O2 → C15H20O2 + H2O |
| S1410 | 异戊酸α-戊基肉桂酯 | `amylcinnamyl_isovalerate` | T2（S0303、S1361 为 T3） | ST01 | S1361 + S0303 | C14H20O + C5H10O2 → C19H28O2 + H2O |
| S1411 | 4(2-呋喃基)丁酸异戊酯 | `isoamyl_furylbutyrate` | T2（`furylbutyric_acid` 为 T3） | ST01 | `furylbutyric_acid` + `isoamyl_alcohol` | C8H10O3 + C5H12O → C13H20O3 + H2O |
| S1412 | 3(2-呋喃基)丙酸异戊酯 | `isoamyl_furylpropionate` | T2（`furylpropionic_acid` 为 T3） | ST01 | `furylpropionic_acid` + `isoamyl_alcohol` | C7H8O3 + C5H12O → C12H18O3 + H2O |
| S1413 | 2-戊基-5或6-酮-1,4-二噁烷 | `amyl_keto_dioxane` | T3 | ST09 + ST08 | `heptanediol` + `sodium_chloroacetate` + `sodium_hydroxide` | C7H16O2 + C2H2ClNaO2 + NaOH → C9H17NaO4 + NaCl + H2O；C9H17NaO4 + HCl → C9H16O3 + NaCl + H2O |
| S1414 | 丙酮酸异戊酯 | `isoamyl_pyruvate` | T2（S0295 为 T3） | ST01 | S0295 + `isoamyl_alcohol` | C3H4O3 + C5H12O → C8H14O3 + H2O |
| S1415 | 苄基丁基醚 | `benzyl_butyl_ether` | T3 | ST09 | `benzyl_chloride` + 431 + `sodium_hydroxide` | C7H7Cl + C4H10O + NaOH → C11H16O + NaCl + H2O |
| S1416 | N-3,7-二甲基-2,6-辛二烯-环丙基甲酰胺 | `geranyl_cyclopropanecarboxamide` | T3 | ST20 | `cyclopropanecarboxylic_acid` + `geranylamine` + `thionyl_chloride` | C4H6O2 + SOCl2 → C4H5ClO + SO2 + HCl；C4H5ClO + C10H19N → C14H23NO + HCl |
| S1417 | N-(乙氧羰基甲基)-对-䓝烷-3-甲酰胺 | `ethoxycarbonylmethyl_menthanecarboxamide` | T3 | ST20 | `menthanecarboxylic_acid` + `thionyl_chloride` + `glycine_ethyl_ester` | C11H20O2 + SOCl2 → C11H19ClO + SO2 + HCl；C11H19ClO + C4H9NO2 → C15H27NO3 + HCl |
| S1418 | 硬木烟熏香味料 SmokEz C-10 | `smokez_c_10` | T1 | NT11 | `materials/logs` + `water` | — |
| S1419 | 硬木烟熏香味料 SEF 7525 | `scansmoke_sef_7525` | T2 | NT4 | `wood_tar` + `ethanol`（木焦油为已有物料，由干馏得到） | — |
| S1420 | (反式,顺式)-2,6-壬二烯-1-醇乙酸酯 | `nonadienyl_acetate` | T3 | ST02 | S0021 + `acetic_anhydride` | C9H16O + C4H6O3 → C11H18O2 + C2H4O2 |
| S1421 | 邻氨基苯甲酸苯乙酯 | `phenylethyl_anthranilate` | T3 | ST03 | S0491 + S0034 | C8H9NO2 + C8H10O → C15H15NO2 + CH4O |
| S1422 | 2-丙酰基-2-噻唑啉 | `propionyl_thiazoline` | T3 | ST11 + ST04 | S0117 + `hydrogen_cyanide` + `cysteamine` + `oxygen` | C3H6O + CHN → C4H7NO；C4H7NO + C2H7NS → C6H11NOS + NH3；2 C6H11NOS + O2 → 2 C6H9NOS + 2 H2O |
| S1423 | 顺式-8-十四烯醛 | `cis_8_tetradecenal` | T3 | ST29 + ST04 | `chlorooctanol` + `triphenylphosphine` + S0130 + `sodium_hydroxide` + `oxygen` | C8H17ClO + C18H15P → C26H32ClOP；C26H32ClOP + C6H12O + NaOH → C14H28O + C18H15OP + NaCl + H2O；2 C14H28O + O2 → 2 C14H26O + 2 H2O |
| S1424 | 烯丙硫醇己酸酯 | `allyl_thiohexanoate` | T3 | ST32 | `hexanoic_acid` + `thionyl_chloride` + S0803 | C6H12O2 + SOCl2 → C6H11ClO + SO2 + HCl；C6H11ClO + C3H6S → C9H16OS + HCl |
| S1425 | 双香兰素 | `divanillin` | T3 / T4 | A ST04；B ST18 | A 30 + `ammonium_persulfate`（`ferrous_sulfate` 催化）；B 30 + `oxygen` + `enzymes/laccase` | A：2 C8H8O3 + (NH4)2S2O8 → C16H14O6 + (NH4)2SO4 + H2SO4；B：4 C8H8O3 + O2 → 2 C16H14O6 + 2 H2O |
| S1426 | 顺式和反式-2-庚基环丙烷羧酸 | `heptylcyclopropanecarboxylic_acid` | T3 | ST30 + ST57 | S0018 + `glycine_ethyl_ester` + `sodium_nitrite` + `hydrochloric_acid` | C9H20O → C9H18 + H2O；C4H9NO2 + NaNO2 + HCl → C4H6N2O2 + NaCl + 2 H2O；C9H18 + C4H6N2O2 → C13H24O2 + N2；C13H24O2 + H2O → C11H20O2 + C2H6O |
| S1427 | 5-羟基-4-甲基己酸δ-内酯 | `hydroxy_methylhexanoic_delta_lactone` | T3 | ST25 + ST05 + ST08 | S0203 + `acrylic_acid` + `hydrogen` | C4H8O + C3H4O2 → C7H12O3；C7H12O3 + H2 → C7H14O3；C7H14O3 → C7H12O2 + H2O |
| S1428 | 4-巯基-2-戊酮 | `mercapto_2_pentanone_4` | T3 | ST25 | S0908 + S1010 | C5H8O + H2S → C5H10OS |
| S1429 | 2,4,6-三硫杂庚烷 | `trithiaheptane` | T3 | ST10 | S1449 + `formaldehyde` + S0686（M14 酸催化缩合） | C2H6S2 + CH2O + CH4S → C4H10S3 + H2O |
| S1430 | 1-(4-甲氧苯基)-4-甲基-1-戊烯-3-酮 | `methoxyphenyl_methylpentenone` | T3 | ST06 | S0163 + `methyl_isopropyl_ketone` | C8H8O2 + C5H10O → C13H16O2 + H2O |
| S1431 | 3(2)-羟基-5-甲基-2(3)-己酮 | `hydroxy_methylhexanone` | T3 | ST06 | S0124 + `acetaldehyde`（交叉酮醇缩合，噻唑盐催化；工业路线未确认） | C5H10O + C2H4O → C7H14O2 |
| S1432 | 二巯基甲烷 | `dimercaptomethane` | T3 | ST10 | `formaldehyde` + S1010 | CH2O + 2 H2S → CH4S2 + H2O |
| S1433 | 4-羟基-2-丁烯酸γ-内酯 | `butenolide` | T3 | ST24 | `furfural` + `hydrogen_peroxide`（`formic_acid` 介质） | C5H4O2 + H2O2 → C4H4O2 + CH2O2 |
| S1434 | (+/-)-3-甲硫基丁酸异丁酯 | `isobutyl_methylthiobutyrate` | T3 | ST03 | S0792 + S0005 | C7H14O2S + C4H10O → C9H18O2S + C2H6O |
| S1435 | 3-甲硫基-2-丁酮 | `methylthio_2_butanone_3` | T3 | ST10 | S0282 + `methyl_chloride` + `sodium_hydroxide` | C4H8OS + CH3Cl + NaOH → C5H10OS + NaCl + H2O |
| S1436 | 顺式和反式-5-乙基-4-甲基-2-(2-甲基丙基)-噻唑啉 | `ethyl_methyl_isobutylthiazoline` | T3 | ST11 | S1177 + S0124 + `ammonia` | C5H10OS + C5H10O + NH3 → C10H19NS + 2 H2O |
| S1437 | 1-戊硫醇 | `pentanethiol` | T3 | ST10 | S0006 + S1010 | C5H12O + H2S → C5H12S + H2O |
| S1438 | (+/-)-4-巯基-4-甲基-2-戊醇 | `mercapto_4_methyl_2_pentanol` | T3 | ST05 | S0886 + `hydrogen` | C6H12OS + H2 → C6H14OS |
| S1439 | 异戊酸环己酯 | `cyclohexyl_isovalerate` | T2（S0303 为 T3） | ST01 | S0303 + `cyclohexanol` | C5H10O2 + C6H12O → C11H20O2 + H2O |
| S1440 | 2-噻吩基二硫醚 | `thienyl_disulfide` | T3 | ST10 | S1334 + `hydrogen_peroxide` | 2 C4H4S2 + H2O2 → C8H6S4 + 2 H2O |
| S1441 | 双(2-甲基-3-呋喃基)四硫醚 | `bis_methylfuryl_tetrasulfide` | T3（S0471 为 T4） | ST10 | S0471 + `disulfur_dichloride` | 2 C5H6OS + Cl2S2 → C10H10O2S4 + 2 HCl |
| S1442 | 辛酸对-甲酚酯 | `p_tolyl_octanoate` | T3 | ST32 | `caprylic_acid` + `thionyl_chloride` + `p_cresol` | C8H16O2 + SOCl2 → C8H15ClO + SO2 + HCl；C8H15ClO + C7H8O → C15H22O2 + HCl |
| S1443 | 丙酸麦芽酚酯 | `maltol_propionate` | T3 | ST02 | 123 + `propionic_anhydride` | C6H6O3 + C6H10O3 → C9H10O4 + C3H6O2 |
| S1444 | 顺式-2-己烯-1-醇 | `cis_2_hexenol` | T3 | ST05 | `hexynol` + `hydrogen`（林德拉钯，`catalysts/palladium`） | C6H10O + H2 → C6H12O |
| S1445 | (+/-)反式和顺式-2-己烯醛丙二醇缩醛 | `hexenal_propylene_glycol_acetal` | T2（S0131 为 T3） | ST07 | S0131 + `propylene_glycol` | C6H10O + C3H8O2 → C9H16O2 + H2O |
| S1446 | 乙酸2-乙基丁酯 | `ethylbutyl_acetate` | T3 | ST05 + ST01 | S0919 + `hydrogen` + `acetic_acid` | C6H12O + H2 → C6H14O；C6H14O + C2H4O2 → C8H16O2 + H2O |
| S1447 | 2,5-二乙基-3-甲基吡嗪 | `diethyl_3_methylpyrazine_2_5` | T3 | ST11 | S0210 + `butanediamine` | C5H8O2 + C4H12N2 → C9H16N2 + 2 H2O；C9H16N2 → C9H14N2 + H2 |
| S1448 | 4-(甲硫基)-2-戊酮 | `methylthio_2_pentanone_4` | T3 | ST25 | S0908 + S0686 | C5H8O + CH4S → C6H12OS |
| S1449 | 甲硫基甲硫醇 | `methylthiomethyl_mercaptan` | T3 | ST10 | S0686 + `formaldehyde` + S1010 | CH4S + CH2O + H2S → C2H6S2 + H2O |
| S1450 | 顺式和反式-5-乙基-4-甲基-2-(1-甲基丙基)-噻唑啉 | `ethyl_methyl_sec_butylthiazoline` | T3 | ST11 | S1177 + S0120 + `ammonia` | C5H10OS + C5H10O + NH3 → C10H19NS + 2 H2O |
| S1451 | 辛醛二甲缩醛 | `octanal_dimethyl_acetal` | T2（S0141 为 T3） | ST07 | S0141 + `methanol` | C8H16O + 2 CH4O → C10H22O2 + H2O |
| S1452 | 3-巯基-3-甲基-1-丁醇乙酸酯 | `mercapto_methylbutyl_acetate` | T3 | ST02 | S0806 + `acetic_anhydride` | C5H12OS + C4H6O3 → C7H14O2S + C2H4O2 |
| S1453 | (R,S)-3-羟基丁酸l-薄荷酯 | `menthyl_hydroxybutyrate` | T3 | ST03 | S0417 + 29 | C6H12O3 + C10H20O → C14H26O3 + C2H6O |
| S1454 | 异戊酸异丙酯 | `isopropyl_isovalerate` | T2（S0303 为 T3） | ST01 | S0303 + 434 | C5H10O2 + C3H8O → C8H16O2 + H2O |
| S1455 | 顺式-4-癸烯醇乙酸酯 | `cis_4_decenyl_acetate` | T3 | ST02 + ST29 | `hydroxybutanal` + `acetic_anhydride` → 4-乙酰氧基丁醛；`hexyl_chloride` + `triphenylphosphine` → 己基三苯基氯化𬭸 + `sodium_methoxide` → 叶立德，与 4-乙酰氧基丁醛 Z 选择性烯化 | C4H8O2 + C4H6O3 → C6H10O3 + C2H4O2；C6H13Cl + C18H15P → C24H28ClP；C24H28ClP + C6H10O3 + CH3NaO → C12H22O2 + C18H15OP + NaCl + CH4O |
| S1456 | 惕各酸香叶酯 | `geranyl_tiglate` | T3 | ST03 | S0496 + S0039 | C7H12O2 + C10H18O → C15H24O2 + C2H6O |
| S1457 | N-苯甲酰邻氨基苯甲酸 | `benzoylanthranilic_acid` | T3 | ST32 + ST20 | `benzoic_acid` + `thionyl_chloride` + `anthranilic_acid` | C7H6O2 + SOCl2 → C7H5ClO + SO2 + HCl；C7H5ClO + C7H7NO2 → C14H11NO3 + HCl |
| S1458 | 2,6,10-三甲基-2,6,10-十五碳三烯-14-酮 | `farnesylacetone` | T3 | ST06 | S0047 + `diketene` | C15H26O + C4H4O2 → C18H30O + CO2 |
| S1459 | 2,5-二甲基噻唑 | `dimethylthiazole_2_5` | T3 | ST11 | `thioacetamide` + `chloropropanal` | C2H5NS + C3H5ClO → C5H7NS + HCl + H2O |
| S1460 | 甲硫基甲醇丁酸酯 | `methylthiomethyl_butyrate` | T3 | ST10 | `butyric_acid` + `chloromethyl_methyl_sulfide` + `sodium_hydroxide` | C4H8O2 + C2H5ClS + NaOH → C6H12O2S + NaCl + H2O |
| S1461 | 2-甲硫基乙醇 | `methylthioethanol` | T3 | ST25 | S0686 + `ethylene_oxide` | CH4S + C2H4O → C3H8OS |
| S1462 | 二乙基三硫醚 | `diethyl_trisulfide` | T3 | ST10 | S0928 + `sulfur_dichloride` | 2 C2H6S + Cl2S → C4H10S3 + 2 HCl |
| S1463 | 顺式和反式-1-巯基-对-䓝烷-3-酮 | `mercapto_p_menthan_3_one_1` | T3 | ST25 | S0846 + S1010 | C10H16O + H2S → C10H18OS |
| S1464 | 4-羟基-4-甲基-7-顺式-癸烯酸γ-内酯 | `hydroxy_methyldecenoic_gamma_lactone` | T3 | ST16 + ST08 | `cis_3_hexenyl_chloride` + `magnesium` + S0369 | C6H11Cl + Mg + C7H12O3 → C11H18O2 + C2H5ClMgO |
| S1465 | 2-甲基辛醛 | `methyloctanal_2` | T3 | ST06 + ST05 | S0141 + `formaldehyde` + `hydrogen` | C8H16O + CH2O → C9H16O + H2O；C9H16O + H2 → C9H18O |
| S1466 | 3-甲基-5-丙基-2-环己烯-1-酮 | `methyl_propylcyclohexenone` | T3 | ST06 | S0119 + S0365 | C4H8O + 2 C6H10O3 → C10H16O + 2 CO2 + 2 C2H6O |
| S1467 | 2,4-壬二烯-1-醇 | `nonadienol_2_4` | T3 | ST05 | S0149 + `hydrogen` | C9H14O + H2 → C9H16O |
| S1468 | 环戊硫醇 | `cyclopentanethiol` | T3 | ST10 | `cyclopentanone` + S1010 + `hydrogen` | C5H8O + H2S + H2 → C5H10S + H2O |
| S1469 | N-对苯乙腈基薄荷烷基甲酰胺 | `benzeneacetonitrile_menthanecarboxamide` | T3 | ST20 | `menthanecarboxylic_acid` + `thionyl_chloride` + `aminophenylacetonitrile` | C11H20O2 + SOCl2 → C11H19ClO + SO2 + HCl；C11H19ClO + C8H8N2 → C19H26N2O + HCl |
| S1470 | N-[2-(吡啶-2-基)乙基]薄荷烷基甲酰胺 | `pyridinylethyl_menthanecarboxamide` | T3 | ST20 | `menthanecarboxylic_acid` + `thionyl_chloride` + `pyridylethylamine` | C11H20O2 + SOCl2 → C11H19ClO + SO2 + HCl；C11H19ClO + C7H10N2 → C18H28N2O + HCl |
| S1471 | 4-氨基-5,6-二甲基噻吩并[2,3-d]嘧啶-2(1H)-酮盐酸盐 | `aminodimethylthienopyrimidinone_hcl` | T4 | ST11 + ST17 | 暂缓（11 问题 36）：S0203 + `malononitrile` + `sulfur` + `urea` + `hydrochloric_acid` | C4H8O + C3H2N2 + S → C7H8N2S + H2O；C7H8N2S + CH4N2O → C8H9N3OS + NH3；C8H9N3OS + HCl → C8H10ClN3OS |
| S1472 | 3-[(4-氨基-2,2-二氧-1H-2,1,3-苯并噻二嗪-5-基)氧]-2,2-二甲基-N-丙基丙酰胺 | `aminobenzothiadiazinyloxy_propylamide` | T4 | ST20 + ST09 + ST11 | 暂缓（11 问题 36）：`hydroxypivalic_acid` + `propylamine` + `aminofluorobenzonitrile` + `sulfamoyl_chloride` | C5H10O3 + C3H9N → C8H17NO2 + H2O；C8H17NO2 + C7H5FN2 + NaOH → C15H21N3O2 + NaF + H2O；C15H21N3O2 + ClH2NO2S → C15H22N4O4S + HCl |
| S1473 | L-蛋氨酰基甘氨酸 | `methionylglycine` | T4 | ST20 | 545 + `glycine` | C5H11NO2S + C2H5NO2 → C7H14N2O3S + H2O |
| S1474 | 5-戊基-3H-呋喃-2-酮 | `pentyl_3h_furanone` | T3 | ST25 + ST08 | S0130 + `acrylic_acid` | C6H12O + C3H4O2 → C9H16O3；C9H16O3 → C9H14O2 + H2O |
| S1475 | 2,5-二硫杂己烷 | `dithiahexane` | T3 | ST10 | `ethylene_dichloride` + S0686 + `sodium_hydroxide` | C2H4Cl2 + 2 CH4S + 2 NaOH → C4H10S2 + 2 NaCl + 2 H2O |
| S1476 | (2S,5R)-N-[4-(2-氨基-2-氧代乙基)苯基]-5-甲基-2-(丙基-2-)环己烷甲酰胺 | `aminooxoethylphenyl_menthanecarboxamide` | T4 | ST18 | S1469 + `enzymes/nitrile_hydratase` | C19H26N2O + H2O → C19H28N2O2 |
| S1477 | 5-甲基-2-呋喃甲硫醇 | `methylfurfuryl_mercaptan` | T3 | ST05 + ST10 | S0181 + `hydrogen` + `thioacetic_acid` + `sodium_hydroxide` | C6H6O2 + H2 → C6H8O2；C6H8O2 + C2H4OS → C8H10O2S + H2O；C8H10O2S + NaOH → C6H8OS + C2H3NaO2 |
| S1478 | 6-甲基辛醛 | `methyloctanal_6` | T3 | ST19 | `methylheptene` + `carbon_monoxide` + `hydrogen` | C8H16 + CO + H2 → C9H18O |
| S1479 | 2(4)-乙基-4(2),6-二甲基二氢-1,3,5-二噻嗪 | `ethyl_dimethyldihydrodithiazine` | T3 | ST11 | `acetaldehyde` + S0117 + `ammonia` + S1010 | 2 C2H4O + C3H6O + NH3 + 2 H2S → C7H15NS2 + 3 H2O |
| S1480 | 3-庚基二氢-5-甲基-2(3H)-呋喃酮 | `heptyl_methyl_dihydrofuranone` | T3 | ST06 + ST05 | S0623 + S0136 + `hydrogen` | C5H8O2 + C7H14O → C12H20O2 + H2O；C12H20O2 + H2 → C12H22O2 |
| S1481 | 香兰醇 | `vanillyl_alcohol` | T3 | ST05 | 30 + `hydrogen` | C8H8O3 + H2 → C8H10O3 |
| S1482 | 6-[5(6)-癸烯酰氧基]癸酸 | `decenoyloxydecanoic_acid` | T3 | ST25 | S1302 | 2 C10H18O2 → C20H36O4 |
| S1484 | 3-{1-[(3,5-二甲基-1,2-噁唑-4-基)甲基]-1H-吡唑-4-基}-1-(3-羟基苄基)咪唑啉-2,4-二酮 | `isoxazolylmethyl_pyrazolyl_hydantoin` | T4 | ST33 + ST11 | 暂缓（11 问题 36）：`m_hydroxybenzaldehyde` + `glycine_ethyl_ester` + `hydrogen` + `isoxazolylmethyl_pyrazolamine` + `phosgene` | C7H6O2 + C4H9NO2 + H2 → C11H15NO3 + H2O；C9H12N4O + CCl2O → C10H10N4O2 + 2 HCl；C10H10N4O2 + C11H15NO3 → C19H19N5O4 + C2H6O |
| S1485 | 4-氨基-5-[3-(异丙基氨基)-2,2-二甲基-3-氧代丙氧基]-2-甲基喹啉-3-羧酸硫酸盐 | `aminoquinolinecarboxylic_acid_sulfate` | T4 | ST20 + ST09 + ST11 + ST17 | 暂缓（11 问题 36）：`hydroxypivalic_acid` + `isopropylamine` + `aminofluorobenzonitrile` + S0365 + `sulfuric_acid` | C5H10O3 + C3H9N → C8H17NO2 + H2O；C8H17NO2 + C7H5FN2 + NaOH → C15H21N3O2 + NaF + H2O；C15H21N3O2 + C6H10O3 → C21H29N3O4 + H2O；C21H29N3O4 + H2O → C19H25N3O4 + C2H6O；C19H25N3O4 + H2SO4 → C19H27N3O8S |
| S1486 | 9-癸烯-2-酮 | `decen_2_one_9` | T3 | ST04 | `decadiene` + `oxygen`（瓦克氧化，`catalysts/palladium`） | 2 C10H18 + O2 → 2 C10H18O |
| S1487 | 6-甲基庚醛 | `methylheptanal_6` | T3 | ST19 | `methylhexene` + `carbon_monoxide` + `hydrogen` | C7H14 + CO + H2 → C8H16O |
| S1488 | N-(2-异丙基-5-甲基环己基)环丙基甲酰胺 | `menthyl_cyclopropanecarboxamide` | T3 | ST20 | `cyclopropanecarboxylic_acid` + `thionyl_chloride` + `menthylamine` | C4H6O2 + SOCl2 → C4H5ClO + SO2 + HCl；C4H5ClO + C10H21N → C14H25NO + HCl |
| S1489 | 4-羟基-4-甲基-5-己烯酸γ-内酯 | `hydroxy_methylhexenoic_gamma_lactone` | T3 | ST16 + ST08 | `vinyl_chloride` + `magnesium` + S0369 | C2H3Cl + Mg + C7H12O3 → C7H10O2 + C2H5ClMgO |
| S1490 | 糠基2-甲基-3-呋喃基二硫醚 | `furfuryl_methylfuryl_disulfide` | T3（S0471 为 T4） | ST10 | S0692 + S0471 + `hydrogen_peroxide` | C5H6OS + C5H6OS + H2O2 → C10H10O2S2 + 2 H2O |
| S1491 | 4-癸烯酸 | `decenoic_acid_4` | T3 | ST04 | S0187 + `oxygen` | 2 C10H18O + O2 → 2 C10H18O2 |
| S1492 | 2-(4-甲基-5-噻唑基)乙醇丙酸酯 | `methylthiazolylethyl_propionate` | T2（S0727 为 T3） | ST01 | S0727 + `propionic_acid` | C6H9NOS + C3H6O2 → C9H13NO2S + H2O |
| S1493 | 4,5-辛二酮 | `octanedione_4_5` | T3 | ST04 | S1031 + `oxygen`（`catalysts/copper`） | 2 C8H16O2 + O2 → 2 C8H14O2 + 2 H2O |
| S1494 | 5-羟基癸酸乙酯 | `ethyl_hydroxydecanoate` | T3 | ST03 | S0634 + `ethanol` | C10H18O2 + C2H6O → C12H24O3 |
| S1495 | 己二酸二辛酯 | `dioctyl_adipate` | T2（S0014 为 T3） | ST01 | `adipic_acid` + S0014 | C6H10O4 + 2 C8H18O → C22H42O4 + 2 H2O |
| S1496 | 乙基芳樟基醚 | `ethyl_linalyl_ether` | T3 | ST09 | S0029 + `sodium` + `ethyl_chloride` | 2 C10H18O + 2 Na + 2 C2H5Cl → 2 C12H22O + 2 NaCl + H2 |
| S1497 | 大茴香脑 | `anethole` | T2 / T3 | A ST13；B ST30 | A N005；B S0061 + `potassium_hydroxide` | A —；B：C10H12O → C10H12O |
| S1498 | 根皮素 | `phloretin` | T2 | ST21 + P39 | `materials/apple` + `ethanol` + `hydrochloric_acid` | C21H24O10 + H2O → C15H14O5 + C6H12O6 |
| S1499 | 2-丙酰吡咯 | `propionylpyrrole` | T3 | ST15 | S1156 + `propionic_anhydride` | C4H5N + C6H10O3 → C7H9NO + C3H6O2 |
| S1500 | 烯丙基1-丙烯基二硫醚 | `allyl_propenyl_disulfide` | T3 | ST30 | S0749 + `potassium_hydroxide` | C6H10S2 → C6H10S2 |
| S1501 | 2-乙酰氧基-3-丁酮 | `acetoxybutanone` | T3 | ST02 | S0204 + `acetic_anhydride` | C4H8O2 + C4H6O3 → C6H10O3 + C2H4O2 |
| S1502 | (+/-)-1-环己基乙醇 | `cyclohexylethanol` | T3 | ST05 | S0225 + `hydrogen`（`catalysts/nickel`，高温，苯环一并加氢） | C8H8O + 4 H2 → C8H16O |
| S1503 | L-γ-谷氨酰-L-缬氨酰-甘氨酸 | `glutamyl_valyl_glycine` | T4 | ST18 | S0884 + `valylglycine` + `enzymes/glutaminase` | C5H10N2O3 + C7H14N2O3 → C12H21N3O6 + NH3 |
| S1504 | (1R,2S,5R)-N-(4-甲氧苯基)-5-甲基-2-(1-甲基乙基)环己基甲酰胺 | `methoxyphenyl_menthanecarboxamide` | T3 | ST20 | `menthanecarboxylic_acid` + `thionyl_chloride` + `p_anisidine` | C11H20O2 + SOCl2 → C11H19ClO + SO2 + HCl；C11H19ClO + C7H9NO → C18H27NO2 + HCl |
| S1505 | 2-(4-甲基苯氧基)-N-(1H-吡唑-3-基)-N-(噻吩-2-基甲基)乙酰胺 | `tolyloxy_pyrazolyl_thenyl_acetamide` | T4 | ST33 + ST32 + ST20 | 暂缓（11 问题 36）：`aminopyrazole` + `thiophenecarboxaldehyde` + `hydrogen` + `tolyloxyacetic_acid` + `thionyl_chloride` | C3H5N3 + C5H4OS + H2 → C8H9N3S + H2O；C9H10O3 + SOCl2 → C9H9ClO2 + SO2 + HCl；C8H9N3S + C9H9ClO2 → C17H17N3O2S + HCl |
| S1506 | 2-己基吡啶 | `hexylpyridine` | T3 | ST06 + ST05 | `alpha_picoline` + S0123 + `hydrogen` | C6H7N + C5H10O → C11H15N + H2O；C11H15N + H2 → C11H17N |

## 2 新增中间体

合成香料新增中间体 249 种（不含已作为成品登记的 11 种：`isophorone` = S0267、`hydrogen_sulfide` = S1010、`styrene` = S0679、`isobutyraldehyde` = S0184、`beta_alanine` = S0321、`trimethylamine` = S0706、`gamma_butyrolactone` = S0630、`l_lysine` = S0344、`dl_methionine` = S0327、`pyruvic_acid` = S0295、`isopropanolamine` = S1112）。阶段为推算的最早档次。

| 建议 ID | 名称 | 阶段 | 路线 | 用于 |
| --- | --- | --- | --- | --- |
| `butyl_chloride` | 1-氯丁烷 | T3 | ST23 S0004 + `hydrogen_chloride`（`catalysts/zinc_chloride`） | S0055、S0071 |
| `hexyl_chloride` | 1-氯己烷 | T3 | ST23 S0010 + `hydrogen_chloride`（`catalysts/zinc_chloride`） | S0069 |
| `cis_5_octenyl_chloride` | 顺式-5-辛烯基氯 | T3 | ST23 S0017 + `thionyl_chloride` | S0019 |
| `aluminium_isopropoxide` | 异丙醇铝 | T3 | `sodium` + S0003 → M14（惰性）异丙醇钠 → 加 `aluminium_chloride` → M12 滤除氯化钠（盐）→ M22 减压蒸馏 | ST31（S0011、S0020、S0042、S0043、S0044、S0070）；S0999、S1063 |
| `xylenol_2_4` | 2,4-二甲基苯酚 | T3 | ST15 `p_cresol` + `methanol` → M27 气相催化邻位甲基化（`catalysts/alumina`）→ M22 精馏 | S0089 |
| `chloro_methyl_tetrahydropyran` | 3-氯-2-甲基四氢吡喃 | T3 | `furfural` ST05 加氢 → 四氢糠醇 → M27 气相脱水扩环（`catalysts/alumina`）→ 3,4-二氢-2H-吡喃 → M14 加 `chlorine` → 2,3-二氯四氢吡喃 → ST16 甲基氯化镁（`methyl_chloride` + `magnesium`）取代 2 位氯 → M22 精馏 | S0012 |
| `sebacic_acid` | 癸二酸 | T3 | `disodium_sebacate`（S0015 碱裂解副产）+ `sulfuric_acid` → M14 酸化（P39）→ M15 冷却结晶 → M17 干燥（酸化单独一条，2026-11-12，11 问题 42） | S1203 |
| `ferulic_acid` | 阿魏酸 | T2 | ST21 `materials/rice_bran`（米糠油皂脚中谷维素碱水解 → 酸析 → M15 结晶） | S0102 |
| `pyrogallol` | 连苯三酚（焦性没食子酸） | T4 | ST45 `gallic_acid` | S0109 |
| `butene` | 1-丁烯 | T3 | 同 262 号 1-辛烯：`paraffin_wax` M27 裂解（P57）→ M22 精馏切取 C4 烯烃 | S0120、S0123、`sec_butanol`；`epoxybutane` |
| `decene` | 1-癸烯 | T3 | 同 262 号 1-辛烯：`paraffin_wax` M27 裂解（P57）→ M22 精馏切取 C10 烯烃 | S0155 |
| `sec_butanol` | 2-丁醇（仲丁醇） | T3 | ST25 `butene` + `water`（硫酸催化水合） | S0203 |
| `myristyl_alcohol` | 1-十四醇（肉豆蔻醇） | T3 | ST05 S0313 + `hydrogen` | S0161 |
| `ethyl_chloroacetate` | 氯乙酸乙酯 | T2 | ST01 `chloroacetic_acid` + `ethanol` | S0139、S0146；S0461；S0714、S0789；S1170 |
| `ethylbenzene` | 乙苯 | T3 | ST15 `benzene` + `ethylene` | S0101、S0190、S0225；S0679；569 |
| `catechol` | 邻苯二酚 | T3 | ST24 `phenol` + `hydrogen_peroxide`（联产 `hydroquinone`，M22 分离） | `benzodioxole` |
| `dichloromethane` | 二氯甲烷 | T2 | ST23 `methyl_chloride` + `chlorine`（M27 气相热氯化，P59；副产三氯甲烷，M22 分离） | `benzodioxole`；S0683 B |
| `benzodioxole` | 1,3-苯并二氧杂环戊烯（亚甲二氧基苯） | T3 | ST09 `catechol` + `dichloromethane` + `sodium_hydroxide` | `methylenedioxymandelic_acid` |
| `methylenedioxymandelic_acid` | 3,4-亚甲二氧基扁桃酸 | T3 | ST06 `benzodioxole` + `glyoxylic_acid`（同 30 号 C 路线前段） | S0175 |
| `trans_4_heptenal` | 反式-4-庚烯醛 | T3 | ST37 S0009 + S0116 | S0197 |
| `cis_3_hexenyl_chloride` | 顺式-3-己烯基氯 | T3 | ST23 S0027 + `thionyl_chloride` | S0137 |
| `anisylidene_acetone` | 4-(4-甲氧基苯基)-3-丁烯-2-酮（茴香叉丙酮） | T3 | ST06 S0163 + `acetone` | S0205 |
| `glutaric_acid` | 戊二酸 | T3 | ST04 144 号己二酸路线（KA 油硝酸氧化）母液副产 → M15 分步结晶 | `diethyl_glutarate` |
| `diethyl_glutarate` | 戊二酸二乙酯 | T3 | ST01 `glutaric_acid` + `ethanol` | S0211 |
| `diethyl_oxalate` | 草酸二乙酯 | T2 | ST01 `oxalic_acid` + `ethanol` | S0211 |
| `ethyl_chloride` | 氯乙烷 | T2 | ST23 `ethanol` + `hydrogen_chloride` | S0211；S0770；S1114、S1142、S1151、S1164 |
| `methyltridecanoic_acid` | 12-甲基十三酸 | T2 | ST13 `lanolin` 皂化 → 酸化 → 甲酯化 → M22 精馏切取异构 C14 酸甲酯 → 水解（未确认） | S0202 |
| `hydroxybutanone` | 4-羟基-2-丁酮 | T3 | ST06 `acetone` + `formaldehyde`（稀碱，只做羟醛加成不脱水；即 F10 组 `methyl_vinyl_ketone` 前段） | S0229 |
| `pseudoionone` | 假性紫罗兰酮 | T3 | ST06 125 + `acetone`（`sodium_hydroxide`） | S0234、S0235；463、469 |
| `valerylfuran` | 2-戊酰基呋喃 | T3 | ST15 `furan` + S0300（与 `acetic_anhydride` 成混酐，磷酸催化） | S0278 |
| `diethyl_methylglutarate` | 3-甲基戊二酸二乙酯 | T3 | ST25 S0403 + S0526（`sodium_methoxide`，迈克尔加成）→ 三酯 → `salt` + `water` 加热脱去一个酯基（P43）→ M22 精馏 | S0249、S0289 |
| `sec_butyl_chloride` | 2-氯丁烷 | T3 | ST23 `sec_butanol` + `hydrogen_chloride`（`catalysts/zinc_chloride`） | S0256、S0320 |
| `methylcyclohexanone` | 2-甲基环己酮 | T3 | ST05 S0095 + `hydrogen`（`catalysts/palladium`，酚加氢停在酮，同苯酚制环己酮） | S0252 |
| `phenyl_acetate` | 乙酸苯酯 | T3 | ST02 `phenol` + `acetic_anhydride` | S0266 |
| `benzoyl_chloride` | 苯甲酰氯 | T3 | ST23 `benzoic_acid` + `thionyl_chloride`（同 ST32 前段，尾气进 M19） | S0265 |
| `propiophenone` | 苯丙酮 | T3 | ST27 `benzoic_acid` + `propionic_acid`（`manganese_dioxide`，丙酸过量，副产 3-戊酮） | S0248 |
| `undecanedione` | 2,5-十一烷二酮 | T3 | ST25（施泰特尔加成，`thiamine_hydrochloride` 作噻唑鎓催化剂，弱碱）S0136 + `methyl_vinyl_ketone` | S0268 |
| `undecenedione` | (Z)-8-十一碳烯-2,5-二酮 | T3 | ST25（同 `undecanedione`）S0137 + `methyl_vinyl_ketone` | S0228 |
| `dichlorodecane` | 1,10-二氯癸烷 | T3 | ST01 `sebacic_acid` + `methanol` → ST05 酯加氢（`catalysts/copper`）→ 1,10-癸二醇 → ST23 `hydrogen_chloride`（同 F10 组 `dichlorooctane`） | `hexadecanedione` |
| `hexadecanedione` | 2,15-十六烷二酮 | T3 | ST28 S0365（2 份）+ `dichlorodecane` + `sodium_methoxide` → 稀碱水解、酸化加热脱羧（酮式分解）→ M15 结晶 | S0263 |
| `diethyl_octadecenedioate` | 9-十八碳烯二酸二乙酯 | T3 | ST60 S0509（自身复分解，副产 9-十八碳烯，M22 分出） | S0264 |
| `methylhexanol` | 2-甲基-1-己醇 | T3 | ST06 + ST05：S0119 + S0117（交叉羟醛缩合得 2-甲基-2-己烯醛）→ 全加氢（`catalysts/nickel`） | S0330；`methylhexyl_chloride` |
| `methylhexyl_chloride` | 1-氯-2-甲基己烷 | T3 | ST23 `methylhexanol` + `hydrogen_chloride`（`catalysts/zinc_chloride`） | S0332 |
| `methylheptanol` | 2-甲基-1-庚醇 | T3 | ST06 + ST05：S0123 + S0117（交叉羟醛缩合得 2-甲基-2-庚烯醛）→ 全加氢（`catalysts/nickel`） | S0331；`methylheptyl_chloride` |
| `methylheptyl_chloride` | 1-氯-2-甲基庚烷 | T3 | ST23 `methylheptanol` + `hydrogen_chloride`（`catalysts/zinc_chloride`） | S0347 |
| `methylpentanal` | 4-甲基戊醛 | T3 | ST06 + ST05：S0184 + `acetaldehyde`（交叉羟醛缩合得 4-甲基-2-戊烯醛）→ 选择加氢（`catalysts/palladium`） | S0286、S0348 |
| `chloropropionic_acid` | 2-氯丙酸 | T3 | ST54 `propionic_acid` + `chlorine`（少量 `phosphorus_trichloride`） | S0341 |
| `ethyl_thioglycolate` | 巯基乙酸乙酯 | T3 | ST10 `ethyl_chloroacetate` + S1010 + `sodium_hydroxide`（硫氢化钠就地生成） | S0243 |
| `neohesperidin` | 新橙皮苷 | T2 | ST21 `materials/bitter_orange`（幼果）+ `ethanol` 浸提（P21）→ M25 大孔树脂吸附（`catalysts/macroporous_resin`）→ M15 结晶 | S0269 |
| `ambradiol` | 龙涎二醇 | T4 | ST05 S1049 + `hydrogen`（内酯加氢开环成二醇，`catalysts/copper`；现实多用氢化铝锂，游戏简化） | S0280 |
| `dehydrozingerone` | 脱氢姜酮 | T3 | ST06 30 + `acetone`（`sodium_hydroxide`） | S0270 |
| `piperonylideneacetone` | 胡椒叉丙酮 | T3 | ST06 S0175 + `acetone`（`sodium_hydroxide`） | S0283 |
| `allyl_alcohol` | 烯丙醇 | T3 | ST01 逆向：S0368 + `water` → M14 离子交换树脂（`catalysts/ion_exchange_resin`）催化水解，边反应边蒸出 → M22 精馏；副产乙酸回用于 ST48（F10 同用此 ID） | S0397；S0545（F10 已引用同一 ID）；S1148、S1184、S1186、S1189、S1190、S1194、S1205、S1211、S1236；S1284、S1304、S1346、S1374、S1401；`allyl_chloride` |
| `propionic_anhydride` | 丙酸酐 | T3 | ST02 S0294 + `acetic_anhydride`（酸酐交换，反应精馏蒸出乙酸） | S0411；S1037、S0995 |
| `butyric_anhydride` | 丁酸酐 | T3 | ST02 S0296 + `acetic_anhydride`（酸酐交换，反应精馏蒸出乙酸） | S0435、S0443、S0448；S0952 |
| `isobutyric_anhydride` | 异丁酸酐 | T3 | ST02（酸酐交换）：`isobutyric_acid` + `acetic_anhydride`，反应精馏蒸出乙酸 | S0619 B |
| `anthranilic_acid` | 邻氨基苯甲酸 | T3 | ST20 `phthalic_anhydride` + `ammonia` + `sodium_hydroxide` → 邻苯二甲酰胺酸钠 → M14 加 `sodium_hypochlorite` 霍夫曼降解（P45）→ `hydrochloric_acid` 酸化 → M15 结晶 | S0491 |
| `z_hexadienyl_chloride` | (Z)-1-氯-2,5-己二烯 | T3 | 炔丙醇 + `allyl_chloride`（F07、F10 同用）亚铜催化偶联 → 5-己烯-2-炔-1-醇 → ST05 部分加氢（`catalysts/palladium`，毒化）→ ST23 + `hydrochloric_acid`；炔丙醇本模组暂缺，未确认 | S0475 |
| `trans_2_nonenoic_acid` | 反式-2-壬烯酸 | T3 | ST04 S0147 → M14 空气氧化（P44）→ M22 精馏 | S0479 |
| `stillingia_oil` | 梓油（乌桕籽仁油） | T0 | `materials/tallow_tree_seed` → M11 粉碎、筛去桕脂 → M02 螺旋压榨种仁（P03）→ M12 过滤 | S0482 |
| `cyclopentanone` | 环戊酮 | T3 | ST27 144（`adipic_acid`，少量氢氧化钡） | S0511、S0632、S0633、S0634、S0636、S0642、S0643；S0857（经 `undecylcyclopentanone`）；S1265；`hexenylcyclopentanone` |
| `propylcyclopentanone` | 2-丙基环戊酮 | T3 | ST06 + ST05 `cyclopentanone` + S0117 + `hydrogen`（缩合得亚丙基环戊酮，再加氢） | S0632 |
| `butylcyclopentanone` | 2-丁基环戊酮 | T3 | ST06 + ST05 `cyclopentanone` + S0119 + `hydrogen` | S0633 |
| `pentylcyclopentanone` | 2-戊基环戊酮 | T3 | ST06 + ST05 `cyclopentanone` + S0123 + `hydrogen` | S0634 |
| `hexylcyclopentanone` | 2-己基环戊酮 | T3 | ST05 S1265 + `hydrogen` | S0635 |
| `heptylcyclopentanone` | 2-庚基环戊酮 | T3 | ST06 + ST05 `cyclopentanone` + S0136 + `hydrogen` | S0636 |
| `octylcyclopentanone` | 2-辛基环戊酮 | T3 | ST06 + ST05 `cyclopentanone` + S0141 + `hydrogen` | S0642 |
| `nonylcyclopentanone` | 2-壬基环戊酮 | T3 | ST06 + ST05 `cyclopentanone` + S0145 + `hydrogen` | S0643 |
| `pentylcyclopentenone` | 2-戊基-2-环戊烯-1-酮 | T3 | ST06 + ST30 `cyclopentanone` + S0123（缩合得 2-戊叉基环戊酮，酸催化双键内移） | S0511 |
| `dimethyl_malonate` | 丙二酸二甲酯 | T2 | ST01 `cyanoacetic_acid` + `methanol`（硫酸，腈基同时醇解，副产硫酸氢铵） | S0511 |
| `malonic_acid` | 丙二酸 | T3 | ST36 S0403（酸催化水解，回收乙醇） | S0548；S1104、`furylacrylic_acid` |
| `ethyl_oxohexanoate` | 5-氧代己酸乙酯 | T3 | ST25 `acetone` + S0523（胺催化迈克尔加成） | S0631 |
| `alpha_picoline` | 2-甲基吡啶 | T3 | ST11 `acetaldehyde` + `ammonia`（M27 气相，`catalysts/alumina`，联产 4-甲基吡啶） | S0540；S0778；S1322；`pyridylethylamine` |
| `limonene_oxide` | 1,2-环氧柠檬烯 | T3 | ST24 S0654 + `hydrogen_peroxide`（选择性环氧化环内双键） | S0580 |
| `hydroxymenthofurolactone` | 羟基薄荷呋喃内酯 | T4 | ST42 S0279 + `oxygen`（M14 紫外灯模块光敏氧化）→ `sodium_sulfite` 还原氢过氧化物（细节未确认） | S0641 |
| `pentenylcyclopentanone` | 2-[(Z)-2-戊烯基]环戊酮 | T0 | 未确认（文献为环戊酮 α-位引入顺式-2-戊烯基，现有模板无对应） | S0645 |
| `hydroxypentadecanoic_acid` | 15-羟基十五酸 | T4 | ST49 + ST05 `materials/malania_seed`（M02 压榨取油）+ `oxygen` + `hydrogen`（断链产物为甘油酯形态） | S0637 |
| `glyoxal` | 乙二醛 | T3 | ST04 `ethylene_glycol` + `oxygen`（M27 气相氧化，`catalysts/silver`；40% 水溶液） | S0717、S0768、S0773；S1217 |
| `isoprene` | 异戊二烯 | T3 | ST39：`dimethyldioxane` M27 裂解 → 异戊二烯 + `formaldehyde`（回用）；与 F10、G1 同 ID | S0663；`tert_butyl_dimethylindane`（S1272）；572 |
| `methyl_isopropenyl_ketone` | 3-甲基-3-丁烯-2-酮（甲基异丙烯基酮） | T3 | ST06 S0203 + `formaldehyde`（羟醛缩合后脱水） | S0663、S0799 |
| `p_toluidine` | 对甲苯胺 | T3 | ST05：`nitrotoluene` + `hydrogen`（M23，`catalysts/nickel`）→ M15 结晶 | S0772 |
| `phenylhydrazine` | 苯肼 | T0 | P68、P67 `aniline` + `sodium_nitrite` + `hydrochloric_acid` + `sodium_sulfite`（重氮化后亚硫酸盐还原、酸解） | S0775 |
| `toluene_2_3_diamine` | 2,3-二氨基甲苯（邻位甲苯二胺） | T3 | ST05：`dinitrotoluene` + `hydrogen`（M23，`catalysts/nickel`）→ M22 精馏 | S0773 |
| `hexanediol` | 1,6-己二醇 | T3 | ST01、ST05 `adipic_acid` + `methanol` + `hydrogen`（己二酸二甲酯加氢，`catalysts/copper`） | S0691 |
| `leucinamide` | L-亮氨酰胺 | T4 | ST01、ST20 S0326 + `methanol` + `hydrochloric_acid` + `ammonia`（甲酯盐酸盐氨解） | S0768 |
| `isoleucinamide` | DL-异亮氨酰胺 | T3 | ST01、ST20 S0849 + `methanol` + `hydrochloric_acid` + `ammonia`（甲酯盐酸盐氨解） | S0717 |
| `potassium_thiocyanate` | 硫氰酸钾 | T3 | ST17 `carbon_disulfide` + `ammonia`（M23 加压得硫氰酸铵，副产 S1010）+ `potassium_hydroxide` | S0762 B |
| `acrolein` | 丙烯醛 | T3 | ST04 `propene` + `oxygen`（M27 气相，`catalysts/bismuth_molybdate`）；与 F07 同 ID | S0700；S0927；456；545 |
| `carbon_disulfide` | 二硫化碳 | T3 | M27 气相催化（P82，`catalysts/alumina`）：`methane` + `sulfur`，副产 S1010；与 F07、F10 同 ID | S0727、S0734、`potassium_thiocyanate`；S0899、S0924；S1085、S1086、S1089、S1090、S1091；S1321 |
| `allyl_chloride` | 3-氯丙烯（烯丙基氯） | T0 | M27 高温气相氯化（P59）：`propene` + `chlorine`；与 F07、F10 同 ID | S0749 B、S0762 B；S0803、S0813、S0814、S0815、S0931；S1045；S1157、S1181、S1243、`allyl_alcohol`；S1270 |
| `selenium_dioxide` | 二氧化硒 | T0 | 同 G4（剧毒，只写试剂名） | S0664；509 |
| `prenyl_methyl_ether` | 1-甲氧基-3-甲基-2-丁烯 | T3 | ST23 S0956 + `hydrochloric_acid` → 异戊烯基氯 → ST09 + `sodium_methoxide` → M22 精馏 | S0804 |
| `undecylcyclopentanone` | 2-十一烷基环戊酮 | T3 | ST06 `cyclopentanone` + S0155 → 2-十一亚基环戊酮 → ST05 加氢（`catalysts/palladium`）→ M22 减压精馏 | S0857 |
| `o_nitroacetophenone` | 邻硝基苯乙酮 | T3 | `ethylbenzene`（F02 中间体）+ `nitric_acid` + `sulfuric_acid` 混酸硝化（P66）→ M22 分出邻硝基乙苯 → ST04 M23 加压空气氧化（`catalysts/cobalt`）→ M22 精馏 | S0819 |
| `chloropyrazine` | 2-氯吡嗪 | T3 | 与 F10 同 ID 同物质，合并时以 F10 为准；ST54 S0904 + `chlorine`（M27 气相）→ M22 精馏 | S0829；S1320 |
| `chloromethylpyrazine` | 2-氯-3-甲基吡嗪（含 5-、6-氯异构体） | T3 | ST54 S0710 + `chlorine`（M27 气相）→ M22 精馏（异构体混合物整体使用） | S0830；S1218、S1219、S1245、S1251 |
| `vanillylamine` | 香草胺（4-羟基-3-甲氧基苄胺） | T3 | ST05 S0172 + `ammonia` + `hydrogen`（M23 临氢还原胺化，`catalysts/nickel`）→ 盐酸成盐 → M15 结晶 | S0838 |
| `chlorobutanone` | 3-氯-2-丁酮 | T3 | 与 F10 同 ID 同物质，合并时以 F10 为准；ST23 S0204 + `thionyl_chloride` → M22 精馏 | S0842；S1342 |
| `nitroethane` | 硝基乙烷 | T2 | `ethyl_chloride`（F02 中间体）+ `sodium_nitrite`（M14，`dimethylformamide` 溶剂，P58）→ 水洗 → M22 精馏（副产亚硝酸乙酯） | S0874 |
| `hydrazine_hydrate` | 水合肼 | T0 | 拉希法：`ammonia` + `sodium_hypochlorite`（M14）→ M22 蒸出过量氨、精馏浓缩 | S0895 |
| `methylthiopropylamine` | 3-甲硫基丙胺 | T3 | ST25 `acrylonitrile` + S0686 → 3-甲硫基丙腈 → ST05 加氢（M23，`catalysts/nickel`，加 `ammonia` 抑制仲胺）→ M22 精馏 | S0899 |
| `ethanolamine` | 乙醇胺（2-氨基乙醇） | T3 | ST25 `ethylene_oxide` + 过量 `ammonia` 水溶液（M23，P60）→ M22 精馏（副产 `diethanolamine`） | S0925、S0926 |
| `magnesium` | 镁 | T0 | `magnesium_chloride` 脱水 → M24 熔盐电解 → 镁 + `chlorine` | S0873、S0927、S0931、S0943；S0954；S1121、S1139、S1151、S1232、S1234、S1237 |
| `alpha_cyclocitral` | α-环柠檬醛 | T3 | S0715（`cyclocitral`，α、β 体混合物）M22 精馏切出 α 体，同时是 S0715 的精制（2026-11-12 改：原与 S0715 同为柠檬醛 + 苯胺 + 硫酸、只差温度，11 问题 41） | S0931 |
| `aleuritic_acid` | 紫胶桐酸（9,10,16-三羟基十六酸） | T2 | ST21 `shellac` + `sodium_hydroxide` 皂化（M14，P41）→ 紫胶桐酸钠析出 → `sulfuric_acid` 酸化 → M15 结晶 | S0936 |
| `trimethyl_orthoformate` | 原甲酸三甲酯 | T2 | 品纳反应（ST07 写法）：`hydrogen_cyanide` + `methanol` + `hydrogen_chloride`（M14，低温）→ 甲亚胺酸甲酯盐酸盐 → 甲醇解 → 滤除 `ammonium_chloride` → M22 精馏 | S0936 |
| `ethyl_chlorocrotonate` | 3-氯巴豆酸乙酯 | T3 | ST23 S0365 + `phosphorus_trichloride` + `chlorine`（即五氯化磷，M14）→ M22 减压精馏 | S0935 |
| `methallyl_chloride` | 甲基烯丙基氯（3-氯-2-甲基丙烯） | T3 | ST54 `isobutylene` + `chlorine`（M27 气相）→ M22 精馏 | S0943 |
| `acetonitrile` | 乙腈 | T0 | `acrylonitrile` 线副产：丙烯氨氧化（M27）粗腈 → M22 精馏分出 | S0943（经 `trimethyl_orthoacetate`）；`acetamidine_hydrochloride`、`thioacetamide` |
| `trimethyl_orthoacetate` | 原乙酸三甲酯 | T2 | 品纳反应（ST07 写法）：`acetonitrile` + `methanol` + `hydrogen_chloride`（M14，低温）→ 甲醇解 → 滤除 `ammonium_chloride` → M22 精馏 | S0943 |
| `chloroacetaldehyde` | 氯乙醛 | T3 | ST54 S0115 + `chlorine`（M14 水溶液中氯化）→ 稀水溶液直接使用 | S0903；S1125、S1222、S1246 |
| `thioformamide` | 硫代甲酰胺 | T3 | ST20 S0346 + `ammonia` → 甲酰胺 → + 五硫化二磷（`white_phosphorus` + `sulfur` 熔合）硫化（M14）→ 现制现用 | S0903 |
| `piperylene` | 1,3-戊二烯（间戊二烯） | T3 | ST39：`light_naphtha` M27 裂解模式（P57）→ 裂解气 M22 分离切 C5 馏分（现实即石脑油裂解副产）；与碳四馏分同一裂解，见 17 `naphtha_cracked_gas`（2026-11-12） | S0987 |
| `sclareol` | 香紫苏醇 | T4 | NT4 `materials/clary_sage_herb`（N108 蒸油后的秸秆）+ `n_hexane`→ ST21 冷却结晶 | S1049 |
| `oleyl_alcohol` | 顺式-9-十八烯醇（油醇） | T3 | ST05 `oleic_acid` + `hydrogen`（锌铬催化，保留碳碳双键） | S1082 |
| `butane_2_3_diol` | 2,3-丁二醇 | T3 | ST05 S0204 + `hydrogen` | S1053 |
| `hydroxymethylfurfural` | 5-羟甲基糠醛 | T4 | ST46 `fructose`（M14 酸催化脱水，P43）→ M18 萃取 → M15 结晶 | S1092 |
| `dimethoxycinnamic_acid` | 3,4-二甲氧基肉桂酸 | T3 | ST06 S0167 + `acetic_anhydride` + `sodium_acetate`（珀金反应）→ 酸化 → M15 结晶 | S1097、`homoveratrylamine` |
| `homoveratrylamine` | 3,4-二甲氧基苯乙胺（高藜芦胺） | T3 | ST05 `dimethoxycinnamic_acid` + `hydrogen` → ST20 `ammonia`（成酰胺）→ `sodium_hypochlorite` 霍夫曼降解（P45）→ M22 减压精馏 | S1097 |
| `nonenynol` | (Z)-6-壬烯-3-炔-1-醇 | T4 | ST41：`acetylene` + `ethylene_oxide` → 3-丁炔-1-醇；`acetylene` + `ethyl_chloride` + `formaldehyde` → 2-戊炔-1-醇 → 部分加氢 → ST23 氯代 → (Z)-1-氯-2-戊烯；两者亚铜催化偶联（未确认） | S0978 |
| `isoprenol` | 3-甲基-3-丁烯-1-醇 | T3 | ST25：`isobutylene` + `formaldehyde`（M23 高压水相，不加酸催化剂）；与 `dimethyldioxane` 按条件区分 | S0956、S0954；572 |
| `methyl_vinyl_ketone` | 甲基乙烯基酮 | T3 | 同 F10（复用 ID）：ST06 `acetone` + `formaldehyde` → 脱水 | S0962、S0989；S1378 |
| `methyl_isopropyl_ketone` | 3-甲基-2-丁酮 | T0 | 同 F11（复用 ID）：`isobutyric_acid` + `acetic_acid` 气相交叉酮化脱羧 | S0975；S1430 |
| `isobutylamine` | 异丁胺 | T3 | 同 F10（复用 ID）：ST33 S0005 + `ammonia` + `hydrogen` | S1086；S1398 |
| `isopropylamine` | 异丙胺 | T0 | 同 F11（复用 ID）：`acetone` + `ammonia` + `hydrogen` 还原胺化 | S1090；S1485 |
| `chloromethyl_methyl_sulfide` | 氯甲基甲基硫醚 | T3 | ST23 S0693 + `chlorine`（侧链氯化） | S1131；S1460 |
| `methylpentene` | 2-甲基-1-戊烯 | T3 | ST25 `propene` 二聚（M23，`catalysts/nickel`，游戏简化） | S1138 |
| `furan` | 呋喃 | T3 | ST04 `furfural` M27 气相催化脱羰（`catalysts/palladium`） | S1156；`furylbutyric_acid` |
| `epoxybutane` | 2,3-环氧丁烷 | T3 | ST24 `butene` + `hydrogen_peroxide`（M23 环氧化模式） | S1158、S1161、S1240 |
| `undecenol` | 10-十一烯-1-醇 | T3 | ST05 S1204 + `hydrogen`（`catalysts/copper` 酯加氢，保留双键） | S1168、S1169 |
| `phenylbutyric_acid` | 4-苯基丁酸 | T3 | ST15 `benzene` + S0630（`catalysts/aluminium_chloride`） | S1194、S1209、S1210 |
| `cedrol` | 柏木脑 | T2 | NT9：`cedarwood_oil` → M15 冷却结晶 | S1197 |
| `heptyne` | 1-庚炔 | T3 | ST23 + ST30：S0136 + `phosphorus_trichloride` + `chlorine` 得 1,1-二氯庚烷，再 `sodium` + `ammonia`（氨基钠）双脱氯化氢 | S1201 |
| `octyne` | 1-辛炔 | T3 | ST23 + ST30：`octene` + `chlorine` 得 1,2-二氯辛烷，再氨基钠双脱氯化氢 | S1202 |
| `acetamidine_hydrochloride` | 盐酸乙脒 | T3 | ST20 `acetonitrile` + `ethanol` + `hydrogen_chloride`（平纳反应得亚胺酯盐酸盐）再 + `ammonia` 氨解 | S1221 |
| `thioacetamide` | 硫代乙酰胺 | T3 | ST10 `acetonitrile` + S1010（碱催化加成） | S1223；S1459 |
| `thiourea` | 硫脲 | T3 | ST10 `methane` + `sulfur` 于 M27 制二硫化碳，再 + `ammonia` 得硫氰酸铵，M14 加热异构 | S1222 |
| `acetylacetone` | 乙酰丙酮 | T3 | ST02 `acetone` + `ketene`（酸催化）得乙酸异丙烯酯，M27 热重排 | S1223 |
| `ethylene_carbonate` | 碳酸乙烯酯 | T3 | ST25 `ethylene_oxide` + `carbon_dioxide`（M23 加压） | S1226 |
| `propylene_carbonate` | 碳酸丙烯酯 | T3 | ST25 `propylene_oxide` + `carbon_dioxide`（M23 加压） | S1235；S1396 |
| `benzyl_chloride` | 苄基氯 | T2 | ST23 `toluene` + `chlorine`（M14 紫外灯模块侧链光氯化，同 `benzal_chloride` 前段） | S1232、S1237、S1244；S1415；`benzyl_cyanide`；481 |
| `diisobutylene` | 二异丁烯 | T3 | ST25 `isobutylene` 酸催化二聚（`catalysts/ion_exchange_resin`） | S1250 |
| `butanediamine` | 1,2-丁二胺 | T2 | ST23 1-丁烯（`ethylene` 二聚）+ `chlorine` 得 1,2-二氯丁烷，再 + `ammonia` 氨解（同 `propylenediamine` 做法） | S1146；S1447 |
| `furylacrylic_acid` | 3-(2-呋喃基)丙烯酸 | T3 | ST06 `furfural` + `malonic_acid`（克脑文格尔–多布纳缩合）；ID 同 F10，路线以 F10 为准 | S1188；S1278 |
| `glutaric_anhydride` | 戊二酸酐 | T3 | `glutaric_acid`（F02）→ M20 加热脱水（P43）→ M22 减压精馏 | S1306 |
| `tert_butylbenzene` | 叔丁基苯 | T3 | ST15 `benzene` + `isobutylene`（`catalysts/aluminium_chloride`） | `tert_butyl_dimethylindane`、`tert_butylbenzyl_chloride`（S1272、S1283） |
| `tert_butyl_dimethylindane` | 6-叔丁基-1,1-二甲基茚满 | T3 | ST15 `tert_butylbenzene` + `isoprene`（`sulfuric_acid` 催化环烷基化） | S1272 |
| `tert_butylbenzyl_chloride` | 对叔丁基苄氯 | T3 | ST15 `tert_butylbenzene` + `formaldehyde` + `hydrogen_chloride`（布朗克氯甲基化，`catalysts/zinc_chloride`） | S1283 |
| `phenoxyethanol` | 2-苯氧基乙醇 | T3 | ST09 `phenol` + `ethylene_oxide`（M23 乙氧基化，P60） | S1280 |
| `phenoxyacetic_acid` | 苯氧乙酸 | T3 | ST09 `phenol` + `chloroacetic_acid` + `sodium_hydroxide` → 酸化 → M15 结晶 | S1284 |
| `brassylic_acid` | 十三碳二酸 | T3 | `materials/rapeseed` → M02 压榨 → 皂化（P41）取芥酸 → ST24 `hydrogen_peroxide` 氧化断裂（`catalysts/tungstate`）→ M22 分出壬酸 → M15 结晶 | S1281 |
| `ethyl_vinyl_ether` | 乙基乙烯基醚 | T3 | ST30 S0116 裂解脱乙醇 | S1293、S1397；456；F10 的 S1293 同用 |
| `isopropyl_chloride` | 2-氯丙烷 | T3 | ST23 S0003 + `hydrogen_chloride`（`catalysts/zinc_chloride`） | S1315；`diisopropylpropionitrile` |
| `propionitrile` | 丙腈 | T3 | ST05 `acrylonitrile` + `hydrogen`（`catalysts/nickel`） | `diisopropylpropionitrile` |
| `diisopropylpropionitrile` | 2,2-二异丙基丙腈 | T3 | ST28 `propionitrile` + `isopropyl_chloride`（氨基钠，两次烷基化） | S1294 |
| `menthanecarboxylic_acid` | 对䓝烷-3-甲酸 | T3 | ST23 29 + `hydrogen_chloride` → 薄荷基氯 → ST16 镁、`carbon_dioxide` → 酸化 → M15 结晶 | S1295 |
| `epichlorohydrin` | 环氧氯丙烷 | T2 | ST23 `glycerol` + `hydrogen_chloride`（`acetic_acid` 催化）→ 1,3-二氯-2-丙醇 → M14 `sodium_hydroxide` 闭环 → M22 精馏 | S1296 |
| `hexylcyclohexanone` | 2-己基环己酮 | T3 | ST06 S0290 + S0130 → 2-己叉基环己酮 → ST05 碳碳双键加氢（`catalysts/palladium`） | S1308 |
| `dichlorobutane` | 1,2-二氯丁烷 | T3 | ST25 `butene`（F02）+ `chlorine` 加成（M14，P59） | S1311 |
| `sec_butylphenol` | 2-仲丁基苯酚 | T3 | ST15 `phenol` + `butene`（F02）（酚铝催化邻位烷基化） | S1299 |
| `cyclohexanethiol` | 环己硫醇 | T3 | ST10 `cyclohexanol` + `hydrogen_sulfide` → M27 气相催化（`catalysts/alumina`） | S1314 |
| `phenethyl_chloride` | 2-苯乙基氯 | T3 | ST23 S0034 + `hydrogen_chloride` | S1322、S1362 |
| `vinylpyrazine` | 2-乙烯基吡嗪 | T3 | ST06 S0710 + `formaldehyde`（侧链羟甲基化）→ 脱水 → M22 减压精馏 | S1325 |
| `suberic_acid` | 辛二酸 | T3 | ST04 `castor_oil` + `nitric_acid` 氧化断裂 → M15 分步结晶（副产壬二酸） | `dichlorooctane` |
| `dichlorooctane` | 1,8-二氯辛烷 | T3 | ST01 `suberic_acid` + `methanol` → ST05 酯加氢（`catalysts/copper`）→ 1,8-辛二醇 → ST23 `hydrogen_chloride` | S1336 |
| `hexenylcyclopentanone` | 2-(3-己烯基)环戊酮 | T3 | ST28 `cyclopentanone`（`morpholine` 成烯胺）+ `cis_3_hexenyl_chloride`（F02）→ 水解 | S1380 |
| `dehydrodihydroionone` | 脱氢二氢紫罗兰酮 | T3 | ST04 S0284 环上脱氢（烯丙位溴化后脱溴化氢） | S1365 |
| `methoxymethylbenzylamine` | 2-甲氧基-4-甲基苄胺 | T3 | ST09 S0191 + `methyl_chloride` → 2-甲氧基-4-甲基苯甲醛 → ST33 `ammonia` + `hydrogen` 还原胺化 | S1368 |
| `methylpyridylethylamine` | 2-(5-甲基-2-吡啶基)乙胺 | T3 | ST11 S0117 + `acetaldehyde` + `ammonia`（M27 气相缩合，M22 分出 2,5-二甲基吡啶）→ ST06 `formaldehyde`（2-位甲基羟甲基化、脱水）→ ST25 `ammonia` 加成 | S1368 |
| `dimethoxybenzylamine` | 2,4-二甲氧基苄胺 | T3 | ST15 S0673 + `formaldehyde` + `hydrogen_chloride`（氯甲基化）→ ST33 过量 `ammonia` 氨解 | S1369 |
| `pyridylethylamine` | 2-(2-吡啶基)乙胺 | T3 | ST06 `alpha_picoline` + `formaldehyde` → 2-乙烯基吡啶 → ST25 `ammonia` 加成 | S1369 |
| `piperonylic_acid` | 胡椒酸 | T3 | ST04 S0175 空气氧化（P44） | S1370 |
| `heptan_4_amine` | 4-庚胺 | T3 | ST33 S1026 + `ammonia` + `hydrogen` 还原胺化 | S1370 |
| `nonadienoic_acid` | 反-2-顺-6-壬二烯酸 | T3 | ST04 S0150 空气氧化（P44） | S1381、S1382 |
| `cyclopropylamine` | 环丙胺 | T3 | S0630 + `hydrogen_chloride` + `methanol`（ST23 开环成 4-氯丁酸甲酯）→ `sodium_methoxide` 闭环 → `ammonia` 氨解成环丙甲酰胺 → `sodium_hypochlorite` 霍夫曼降解（P45）→ M22 精馏 | S1381 |
| `decadienoic_acid` | 反-2-反-4-癸二烯酸 | T3 | ST04 S0154 空气氧化（P44）→ M15 结晶 | S1398 |
| `cyclohexylhexanoic_acid` | 6-环己基己酸 | T3 | ST15：`benzene` + `adipic_acid`（多聚磷酸催化单酰化）→ 5-苯甲酰戊酸 → ST05 羰基氢解与苯环加氢（`catalysts/palladium`、`catalysts/nickel`）→ M22 精馏 | S1402 |
| `cyclohexylvaleric_acid` | 5-环己基戊酸 | T3 | ST06：126 + `acetic_anhydride`（`sodium_acetate`，珀金缩合）→ 5-苯基-2,4-戊二烯酸 → ST05 全加氢（`catalysts/nickel`）→ M22 精馏 | S1403 |
| `furylbutyric_acid` | 4-(2-呋喃基)丁酸 | T3 | ST15：`furan` + `succinic_anhydride`（`catalysts/zinc_chloride`）→ 4-(2-呋喃基)-4-氧代丁酸 → P67 黄鸣龙还原（`hydrazine_hydrate`、`potassium_hydroxide`）→ 酸化 → M15 结晶 | S1411 |
| `furylpropionic_acid` | 3-(2-呋喃基)丙酸 | T3 | ST05：`furylacrylic_acid`（沿用 F10）选择性加氢（`catalysts/palladium`）→ M15 结晶 | S1412 |
| `heptanediol` | 1,2-庚二醇 | T3 | ST24：`heptene_1` + `hydrogen_peroxide` 环氧化 → 酸性水解（同一配方）→ M22 精馏 | S1413 |
| `benzyl_cyanide` | 苯乙腈 | T3 | ST09：`benzyl_chloride` + `hydrogen_cyanide` + `sodium_hydroxide`（M14，相转移）→ 水洗 → M22 精馏 | `aminophenylacetonitrile` |
| `aminophenylacetonitrile` | 对氨基苯乙腈 | T3 | P66：`benzyl_cyanide` 混酸硝化 → 分出对硝基苯乙腈 → ST05 硝基加氢（`catalysts/palladium`）→ M15 结晶 | S1469 |
| `cyclopropanecarboxylic_acid` | 环丙烷甲酸 | T3 | ST23：S0630 + `hydrogen_chloride` + `methanol` 开环 → 4-氯丁酸甲酯 → ST57 `sodium_methoxide` 分子内闭环 → 皂化、酸化 → M22 精馏 | S1416、S1488 |
| `geranylamine` | 香叶胺 | T3 | ST23：S0039 + `phosphorus_trichloride` → 香叶基氯 → ST33 + `ammonia`（大过量，M23）→ M22 减压精馏 | S1416 |
| `menthylamine` | 薄荷胺 | T3 | ST33：`menthone` + `ammonia` + `hydrogen` 还原胺化（M23，`catalysts/nickel`）→ M22 减压精馏 | S1488 |
| `glycine_ethyl_ester` | 甘氨酸乙酯 | T2 | ST01：`glycine` + `ethanol`（`hydrogen_chloride` 催化，得盐酸盐）→ M15 结晶 → 用前中和 | S1417、S1426、S1484 |
| `cysteamine` | 半胱胺 | T3 | ST10：`ethanolamine` + `sulfuric_acid` → 硫酸氢 2-氨基乙酯 → + `carbon_disulfide` 闭环成 2-巯基噻唑啉 → 酸水解 → M15 结晶 | S1422 |
| `chlorooctanol` | 8-氯-1-辛醇 | T3 | ST05：`suberic_acid`（沿用 F10）经二甲酯加氢 → 1,8-辛二醇 → ST23 + `hydrochloric_acid` 单氯代（带分水）→ M22 精馏 | S1423 |
| `disulfur_dichloride` | 二氯化二硫 | T0 | P59：`sulfur_dichloride` + `sulfur`（M14 耐腐蚀内衬）→ M22 精馏（2026-11-12 改，与第三批二氯化硫区分，11 问题 41） | S1441 |
| `hexynol` | 2-己炔-1-醇 | T3 | ST16：炔丙醇双负离子 + 1-氯丙烷烷基化（`sodium` 液氨，M14 低温）→ M22 精馏；炔丙醇由乙炔 + `formaldehyde` 雷佩法，上游未展开 | S1444 |
| `chloropropanal` | 2-氯丙醛 | T3 | P59：S0117 + `chlorine`（M14 耐腐蚀内衬，低温）→ M22 精馏 | S1459 |
| `vinyl_chloride` | 氯乙烯 | T3 | ST30：`ethylene_dichloride` 热裂解脱氯化氢（M27 裂解模式）→ M22 精馏；副产 `hydrogen_chloride` | S1489 |
| `vinylpyridine` | 2-乙烯基吡啶 | T3 | ST06：`alpha_picoline` + `formaldehyde` → 2-(2-羟乙基)吡啶 → ST30 脱水（`sodium_hydroxide`）→ M22 减压精馏 | `pyridylethylamine` |
| `malononitrile` | 丙二腈 | T3 | ST20：`cyanoacetic_acid` 酯化后 + `ammonia` 氨解 → 氰乙酰胺 → ST30 脱水（`phosphorus_trichloride`）→ M22 减压精馏 | S1471 |
| `hydroxypivalic_acid` | 3-羟基-2,2-二甲基丙酸 | T3 | ST06：S0184 + `formaldehyde`（碱催化羟醛）→ 羟基新戊醛 → ST04 `hydrogen_peroxide` 氧化 → M15 结晶 | S1472、S1485 |
| `propylamine` | 正丙胺 | T3 | ST33：`n_propanol` + `ammonia` + `hydrogen`（M23，`catalysts/nickel`）→ M22 精馏 | S1472 |
| `aminofluorobenzonitrile` | 2-氨基-6-氟苯甲腈 | T3 | ST33：2,6-二氟苯甲腈 + `ammonia` 芳环氨解（M23）→ M15 结晶；二氟苯甲腈由 2,6-二氯苯甲腈与氟化钾卤素交换，上游未展开 | S1472、S1485 |
| `sulfamoyl_chloride` | 氨基磺酰氯 | T3 | ST32：`sulfamic_acid` + `thionyl_chloride`（M14 耐腐蚀内衬，现制现用） | S1472 |
| `isoxazolylmethyl_pyrazolamine` | 1-[(3,5-二甲基异噁唑-4-基)甲基]-1H-吡唑-4-胺 | T3 | ST09：4-氯甲基-3,5-二甲基异噁唑 + 4-硝基吡唑 N-烷基化（M14）→ ST05 硝基加氢 → M15 结晶；两种杂环砌块上游未展开 | S1484 |
| `m_hydroxybenzaldehyde` | 3-羟基苯甲醛 | T3 | P66：127 硝化 → 间硝基苯甲醛 → ST05 加氢 → P68 重氮化后水解 → M15 结晶 | S1484 |
| `decadiene` | 1,9-癸二烯 | T3 | ST16：`allyl_chloride` + `magnesium` → 烯丙基氯化镁，与 1,4-二氯丁烷偶联（S0630 经 ST05 加氢得 1,4-丁二醇，再 ST23 氯代）→ M22 精馏 | S1486 |
| `methylhexene` | 5-甲基-1-己烯 | T3 | ST23：S0005 + `hydrogen_chloride` → 异丁基氯 → ST16 + `magnesium`，与 `allyl_chloride` 偶联 → M22 精馏 | S1487 |
| `methylheptene` | 5-甲基-1-庚烯 | T3 | ST23：S0863 + `hydrogen_chloride` → 2-甲基丁基氯 → ST16 + `magnesium`，与 `allyl_chloride` 偶联 → M22 精馏 | S1478 |
| `valylglycine` | L-缬氨酰甘氨酸 | T3 | ST20（P73 保护–缩合–脱保护）：L-缬氨酸（S0340 的 L 型）+ `glycine` → M15 结晶 | S1503 |
| `p_anisidine` | 对氨基苯甲醚 | T3 | 同 `p_phenetidine`，乙醇改为 `methanol`：ST09 `p_nitrochlorobenzene` + `sodium_methoxide` → 对硝基苯甲醚 → ST05 加氢 → M22 减压精馏 | S1504 |
| `aminopyrazole` | 3-氨基吡唑 | T3 | ST11：`acrylonitrile` + `hydrazine_hydrate` 环化 → 3-氨基-2-吡唑啉 → ST04 液相氧化脱氢 → M15 结晶 | S1505 |
| `thiophenecarboxaldehyde` | 2-噻吩甲醛 | T3 | ST15：S0897 + `dimethylformamide`（维尔斯迈尔甲酰化，三氯氧磷由 `phosphorus_trichloride` 氧化）→ 水解 → M22 减压精馏 | S1505 |
| `tolyloxyacetic_acid` | 对甲苯氧基乙酸 | T3 | ST09：`p_cresol` + `chloroacetic_acid` + `sodium_hydroxide` → 酸化 → M15 结晶 | S1505 |
| `thioacetic_acid` | 硫代乙酸 | T3 | ST10：`acetic_anhydride` + S1010（M14）→ M22 精馏 | S1477 |
| `hydroxybutanal` | 4-羟基丁醛 | T3 | ST19：`allyl_alcohol` + `carbon_monoxide` + `hydrogen`（`catalysts/carbonylation`，直链选择性） | S1455 |
| `carbethoxypropyl_tpp_bromide` | (3-乙氧羰基丙基)三苯基溴化鏻 | T3 | ST05 `maleic_anhydride` + `hydrogen` → γ-丁内酯 → ST23 + `ethanol` + `hydrobromic_acid` 开环 → 4-溴丁酸乙酯 → M14 + `triphenylphosphine` 季鏻化（P58） | S0474 |
| `ethyl_bromoacetate` | 溴乙酸乙酯 | T3 | ST54：`acetic_acid` + `bromine`（少量 `phosphorus_trichloride`，α-溴化，P59）→ 溴乙酸 → ST01 + `ethanol` | S0461 |
| `dl_menthol` | dl-薄荷醇 | T3 | ST05：`thymol` + `hydrogen`（M23，`catalysts/nickel`）→ 薄荷醇异构体混合物 → M22 精馏分出 dl-薄荷醇，其余异构体回流差向异构化 | S1396 |
| `methylprenol` | 2-甲基异戊烯醇（2,3-二甲基-2-丁烯-1-醇） | T3 | `isoamyl_alcohol` ST30 脱水 → 2-甲基-2-丁烯 → + `formaldehyde` M23 加压普林斯缩合（同 572、`isoprenol` 的做法）→ ST30 双键移位（工业来源未确认，游戏简化） | S0233 B |
| `methyl_citral` | 6-甲基柠檬醛 | T3 | ST37：`methylprenol` + S1042（异戊烯醛）缩醛化 → 裂解 → [3,3] 重排（同异戊烯醇 + 异戊烯醛制柠檬醛） | S0233 B |
| `butenol_3` | 3-丁烯-1-醇 | T3 | ST16：`allyl_chloride` + `magnesium` → 格氏试剂 + `formaldehyde` → 水解 | `butenylamine` |
| `butenylamine` | 3-丁烯-1-胺 | T3 | ST23 + ST33：`butenol_3` + `hydrogen_chloride` → 4-氯-1-丁烯 → + `ammonia`（大过量，M23 氨解） | S1079 B |
| `pentenol_4` | 4-戊烯-1-醇 | T4 | ST05 + ST23 + ST43：`furfural` 加氢 → 四氢糠醇 → + `hydrogen_chloride` → 四氢糠基氯 → + `sodium` 还原开环 | `pentenylamine` |
| `pentenylamine` | 4-戊烯-1-胺 | T4 | ST23 + ST33：`pentenol_4` + `hydrogen_chloride` → 5-氯-1-戊烯 → + `ammonia`（M23 氨解） | S1080 B、`methylthiopentylamine` |
| `dibromohexane` | 1,6-二溴己烷 | T3 | ST23：`hexanediol` + `hydrobromic_acid` | `hexenylamine` |
| `hexenylamine` | 5-己烯-1-胺 | T3 | ST30 + ST33：`dibromohexane` + `potassium_hydroxide` 部分消除 → 6-溴-1-己烯 → + `ammonia`（M23 氨解） | S1081 B、`methylthiohexylamine` |
| `methylthiopentylamine` | 5-(甲硫基)戊胺 | T4 | ST25：`pentenylamine` + S0686（甲硫醇）自由基反马氏加成（过氧化物引发，M14） | S1088 B |
| `methylthiohexylamine` | 6-(甲硫基)己胺 | T3 | ST25：`hexenylamine` + S0686（甲硫醇）自由基反马氏加成（过氧化物引发，M14） | S1087 B |
| `isobutyl_tiglate` | 惕各酸异丁酯 | T3 | ST01：S0350 + S0005 | S0445 B |
| `hexanoic_anhydride` | 己酸酐 | T3 | ST02（酸酐交换）：`hexanoic_acid` + `acetic_anhydride`，反应精馏蒸出乙酸 | S0546 B |
| `benzoic_anhydride` | 苯甲酸酐 | T3 | ST02（酸酐交换）：`benzoic_acid` + `acetic_anhydride`，反应精馏蒸出乙酸 | S0600 B、S0606 B |
| `helichrysum_concrete` | 蜡菊浸膏 | T4 | NT4：`materials/immortelle_flower` + `n_hexane`（M18 溶剂浸提，回收溶剂） | N275 |
| `ambrette_seed_concrete` | 黄葵籽浸膏 | T4 | NT4：`materials/ambrette_seed` + `ethanol`（M18 溶剂浸提，回收溶剂） | N282 |
| `hungarian_chamomile_concrete` | 母菊花浸膏 | T4 | NT4：`materials/german_chamomile` + `n_hexane`（M18 溶剂浸提，回收溶剂） | N309 |
| `algues_concrete` | 海藻浸膏 | T4 | NT4：`materials/kelp` + `n_hexane`（M18 溶剂浸提，回收溶剂） | N330 |
| `peach_leaf_concrete` | 桃树叶浸膏 | T4 | NT4：`materials/peach_leaf` + `n_hexane`（M18 溶剂浸提，回收溶剂） | N336 |
| `lavender_concrete` | 薰衣草浸膏 | T4 | NT4：`materials/lavender` + `n_hexane`（M18 溶剂浸提，回收溶剂） | N340 |
| `oakmoss_concrete` | 橡苔浸膏 | T4 | NT4：`materials/oakmoss` + `n_hexane`（M18 溶剂浸提，回收溶剂） | N353 |
| `civet_concrete` | 灵猫浸膏 | T4 | NT4：`materials/civet` + `n_hexane`（M18 溶剂浸提，回收溶剂） | N361 |
| `rum_concrete` | 朗姆酒浸膏 | T4 | NT4：`foods/rum` + `n_hexane`（M18 溶剂浸提，回收溶剂） | N380 |
| `jasmonic_acid` | 茉莉酸 | T4 | ST18：`glucose` + `cultures/lasiodiplodia`（M29 通气发酵）→ 发酵液萃取 | S0565 |
| `homoeriodictyol` | 高圣草酚 | T4 | NT4：`materials/yerba_santa` + `ethyl_acetate`（M18 浸提）→ M15 冷却结晶 | N398 |
| `sodium_ethylbenzenesulfonate` | 对乙基苯磺酸钠 | T4 | ST47 前段：`ethylbenzene` + `sulfuric_acid`（M14 耐腐蚀内衬磺化，P59）→ `sodium_hydroxide` 中和成盐 | S0101 |
| `sodium_p_ethylphenolate` | 对乙基苯酚钠 | T4 | ST47：`sodium_ethylbenzenesulfonate` + `sodium_hydroxide` → M20 碱熔（P70），`sodium_sulfite` 为副产出（2026-11-12，11 问题 42） | S0101 |
| `disodium_sebacate` | 癸二酸二钠 | T3 | ST38：S0015 碱裂解（`castor_oil` + `sodium_hydroxide`，M23 高温）的釜残（2026-11-12，11 问题 42） | `sebacic_acid` |
| `soybean_fatty_acids` | 大豆油脂肪酸 | T0 | `refined_soybean_oil` M23 高温高压水解（P39），甘油水回收 `glycerol` | S0880 |
| `starter_culture_broth` | 发酵起子培养液 | T1 | NT13 前段：`materials/milk` + `cultures/lactococcus` + 柠檬酸（3）→ M05 密闭发酵（P54） | N159 |
| `cedarwood_oil` | 柏木油 | T1 | NT1：`materials/cedarwood` | N321、`cedrol` |
| `dimethyldioxane` | 4,4-二甲基-1,3-二氧六环 | T2 | ST07：`isobutylene` + `formaldehyde` + `sulfuric_acid`（M14 酸催化普林斯反应） | `isoprene` |
| `heptene_1` | 1-庚烯 | T3 | ST30：S0013 气相脱水（M27，`catalysts/alumina`） | `heptanediol` |
| `nitrotoluene` | 对硝基甲苯 | T3 | P66：`toluene` + `nitric_acid` + `sulfuric_acid`（M14 耐腐蚀内衬，一硝化）→ M15 冷却结晶分出对位异构体 | `p_toluidine`、`dinitrotoluene` |
| `dinitrotoluene` | 2,3-二硝基甲苯 | T3 | P66：`nitrotoluene` + `nitric_acid` + `sulfuric_acid`（M14，二硝化，高温）→ M22 分出邻位异构体 | `toluene_2_3_diamine` |
| `nitrosyl_chloride` | 亚硝酰氯 | T2 | `sodium_nitrite` + `hydrochloric_acid`（M14 现制现用，剧毒，只写试剂） | S0224 B |

## 3 新增原料、菌种与催化剂

| 原料 | 配方引用标签 | 现实来源 | 本模组来源 | 用于 |
| --- | --- | --- | --- | --- |
| 龙脑樟枝叶 | `materials/borneol_camphor_leaf` | 龙脑樟 Cinnamomum camphora（龙脑型化学型）枝叶；江西吉安、湖南新晃、贵州等地人工林 | 暂无来源 | S0028 |
| 烯烃复分解催化剂 | `catalysts/metathesis` | 钌卡宾络合物（格拉布催化剂）或 Re2O7/Al2O3（加四甲基锡助剂）；工业乙烯解另用钨、钼卡宾络合物 | 暂无来源（催化剂，按次数消耗，待定） | S0334、`diethyl_octadecenedioate` |
| 乌桕籽 | `materials/tallow_tree_seed` | 乌桕 *Triadica sebifera* (L.) Small（异名 *Sapium sebiferum*）的种子，外被白色蜡层（桕脂），种仁榨油即梓油；主产中国长江流域及以南 | 暂无来源 | S0482 |
| 蒜头果种子 | `materials/malania_seed` | 蒜头果 Malania oleifera Chun et S. K. Lee 的种子，种仁油富含 15-二十四碳烯酸（神经酸）；产云南东南部、广西西部 | 暂无来源 | S0637 |
| 可可毛色二孢（菌种） | `cultures/lasiodiplodia` | Lasiodiplodia theobromae（异名 Botryodiplodia theobromae），热带植物病原真菌，深层发酵分泌茉莉酸 | 暂无来源 | S0565 |
| 香菇（干） | `materials/shiitake` | 香菇 *Lentinula edodes* 的干燥子实体，主产中国、日本、韩国；复水时酶解生成香菇素 | 暂无来源 | S0683 A |
| 芥菜籽 | `materials/mustard_seed` | 黑芥 *Brassica nigra*（*Rhamphospermum nigrum*）、褐芥 *Brassica juncea* 种子，含黑芥子苷，主产印度、加拿大、尼泊尔、中国 | 暂无来源 | S0762 A |
| 西瓜 | `materials/melon` | 西瓜 Citrullus lanatus 果肉（含 9-脂氧合酶、氢过氧化物裂解酶），中国、土耳其、印度主产 | 原版 `minecraft:melon_slice` | S0862 |
| 宽叶薄荷桉叶 | `materials/eucalyptus_dives` | *Eucalyptus dives* Schauer 叶，原产澳大利亚东南部，南非栽培为胡椒酮油主产地 | 暂无来源 | S0955 |
| 山葵根茎 | `materials/wasabi` | *Eutrema japonicum* (Miq.) Koidz.（*Wasabia japonica*）根茎，日本、中国云南与四川 | 暂无来源 | S1081、S1087、S1088 |
| 香紫苏（复用 N 组标签） | `materials/clary_sage_herb` | *Salvia sclarea* L. 花期全草，N108 蒸油后的秸秆用于提取香紫苏醇 | 暂无来源（同 N108） | S1049（经 `sclareol`） |
| 山楂核 | `materials/hawthorn_kernel` | 山楂 *Crataegus pinnatifida* Bunge 果核（果品加工副产），中国华北、东北 | `materials/hawthorn_fruit`（N1 组新增）去肉取核（游戏简化：M11 粉碎时副产） | S1230、S1231 |
| 柏木 | `materials/cedarwood` | 柏木 *Cupressus funebris* Endl. 木材（中国四川、贵州）；北美红桧 *Juniperus virginiana* L. 木材（美国） | 暂无来源 | S1197（经 `cedrol`） |
| 高芥酸菜籽 | `materials/rapeseed` | 甘蓝型油菜高芥酸品种（Brassica napus，HEAR）与海甘蓝（Crambe abyssinica）的种子，油中芥酸约 45%–55%；加拿大、欧洲 | 暂无来源 | S1281（经 `brassylic_acid`） |
| 产亮氨酸谷氨酸棒杆菌 | `cultures/corynebacterium_leucine` | 谷氨酸棒杆菌的营养缺陷、类似物抗性诱变株 | M30 诱变：谷氨酸棒杆菌（`cultures/corynebacterium`） | S0326 |
| 产脯氨酸谷氨酸棒杆菌 | `cultures/corynebacterium_proline` | 谷氨酸棒杆菌的营养缺陷、类似物抗性诱变株 | M30 诱变：谷氨酸棒杆菌（`cultures/corynebacterium`） | S0339 |
| 产精氨酸谷氨酸棒杆菌 | `cultures/corynebacterium_arginine` | 谷氨酸棒杆菌的营养缺陷、类似物抗性诱变株 | M30 诱变：谷氨酸棒杆菌（`cultures/corynebacterium`） | S0343 |
| 产谷氨酰胺谷氨酸棒杆菌 | `cultures/corynebacterium_glutamine` | 谷氨酸棒杆菌的营养缺陷、类似物抗性诱变株 | M30 诱变：谷氨酸棒杆菌（`cultures/corynebacterium`） | S0884 |
| 产核糖枯草芽孢杆菌 | `cultures/bacillus_subtilis_ribose` | 转酮醇酶缺陷的枯草芽孢杆菌诱变株 | M30 诱变：枯草芽孢杆菌（`cultures/bacillus_subtilis`）；与产乙偶姻株共用一块概率平板 | S0077 |
| 产乙偶姻枯草芽孢杆菌 | `cultures/bacillus_subtilis_acetoin` | 产 3-羟基-2-丁酮的枯草芽孢杆菌诱变株 | M30 诱变：枯草芽孢杆菌（`cultures/bacillus_subtilis`）；与产核糖株共用一块概率平板 | S0204 A |

## 4 依据

| 链接 | 支持的编码或模板 |
| --- | --- |
| https://www.chemicalbook.com/article/how-to-synthesis-leaf-alcohol.htm | S0027、ST41 |
| https://patents.google.com/patent/US4355193A | S0017、S0019 |
| https://m.chemicalbook.com/ProductChemicalPropertiesCB81074040_EN.htm | S0012、ST43 |
| https://chemicalbook.com/ChemicalProductProperty_EN_CB0738053.htm | S0083 |
| https://patents.google.com/patent/US7166728 | S0088、S0075、ST42 |
| https://chemicalbook.com/ChemicalProductProperty_EN_CB8175410.htm | S0060 |
| https://chemicalbook.com/ChemicalProductProperty_EN_CB5159709.htm | S0074 |
| https://patentimages.storage.googleapis.com/ef/36/6c/1392e59fae0f55/US6515188.pdf | S0067 |
| https://iris.cnr.it/retrieve/68737f04-d8c6-4717-b6a2-0edade2d38b4/prod_17617-doc_21327.pdf | S0073 |
| https://foreverest.net/zh-hans/products/turpentine-derivative-products/borneol.html | S0028 |
| https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4681011/ | S0077 |
| https://patents.google.com/patent/US5952517 | S0015、ST38、`sebacic_acid` |
| https://patents.google.com/patent/US5077206 | S0078 |
| https://patents.google.com/patent/US5994598 | S0065 |
| https://en.wikipedia.org/wiki/2,6-Xylenol | ST44、S0108、S0113 |
| https://en.wikipedia.org/wiki/Pyrogallol | ST45、`pyrogallol`、S0109 |
| https://en.wikipedia.org/wiki/Ferulic_acid | `ferulic_acid`、ST45（阿魏酸脱羧酶）、S0102 |
| https://en.wikipedia.org/wiki/2-Methoxy-4-vinylphenol | S0102、S0105 |
| https://en.wikipedia.org/wiki/Syringol | S0109 |
| https://en.wikipedia.org/wiki/Heptanal | ST39、S0136 |
| https://en.wikipedia.org/wiki/5-Methylfurfural | ST46、S0181 |
| http://orgsyn.org/demo.aspx?prep=CV2P0393 | ST46、S0181（蔗糖 + 盐酸 + 氯化亚锡） |
| https://en.wikipedia.org/wiki/Ketonic_decarboxylation | ST27、S0220、S0221、S0222；S1014、S1026（ST27，二氧化铈、二氧化锰 / 氧化铝催化；混合酸时其中之一过量） |
| https://en.wikipedia.org/wiki/2-Undecanone | S0221 |
| https://en.wikipedia.org/wiki/Claisen_rearrangement | ST37；ST37（S1148、S1184、S1243） |
| https://www.chemicalbook.com/ProductChemicalPropertiesCB7273685_EN.htm | S0137（由顺式-3-己烯醇制）、`trans_4_heptenal`（1-戊烯-3-醇乙烯基醚克莱森重排） |
| https://en.wikipedia.org/wiki/Bouveault_aldehyde_synthesis | ST16 用 N,N-二甲基甲酰胺甲酰化：S0137 |
| https://en.wikipedia.org/wiki/4-Ethylphenol | ST47、S0101 |
| https://en.wikipedia.org/wiki/Hydroformylation | ST19：S0117、S0119、S0120、S0123、S0124、S0145、S0155、S0184；S1138、S1250 |
| https://en.wikipedia.org/wiki/Pentanal | S0120、S0123 |
| https://en.wikipedia.org/wiki/Isovaleraldehyde | S0124 |
| https://en.wikipedia.org/wiki/Nonanal | S0145 |
| https://en.wikipedia.org/wiki/Undecanal | S0155、`decene` |
| https://en.wikipedia.org/wiki/2-Heptanone | S0216、S0217 |
| https://en.wikipedia.org/wiki/Butanone | S0203 |
| https://en.wikipedia.org/wiki/2-Butanol | `sec_butanol` |
| https://en.wikipedia.org/wiki/2-Methylundecanal | S0146（达森法：2-十一酮 + 氯乙酸乙酯） |
| https://en.wikipedia.org/wiki/Darzens_reaction | S0139、S0146；S1170 |
| https://en.wikipedia.org/wiki/Piperonal | S0175、`methylenedioxymandelic_acid` |
| https://en.wikipedia.org/wiki/1,3-Benzodioxole | `benzodioxole` |
| https://en.wikipedia.org/wiki/Catechol | `catechol` |
| https://en.wikipedia.org/wiki/Dichloromethane | `dichloromethane` |
| https://en.wikipedia.org/wiki/Salicylaldehyde | S0164、S0191（酚 + 甲醛，镁盐邻位甲酰化） |
| https://en.wikipedia.org/wiki/4-Anisaldehyde | S0163 |
| https://en.wikipedia.org/wiki/Veratraldehyde | S0167 |
| https://en.wikipedia.org/wiki/Phenylacetaldehyde | S0168 |
| https://en.wikipedia.org/wiki/Hydrocinnamaldehyde | S0170 |
| https://en.wikipedia.org/wiki/Cuminaldehyde | S0171 |
| https://en.wikipedia.org/wiki/Gattermann_reaction | S0166、S0171 B、S0190（加特曼–科赫甲酰化） |
| https://en.wikipedia.org/wiki/Citronellal | S0173 A |
| https://en.wikipedia.org/wiki/Perillaldehyde | S0178 |
| https://en.wikipedia.org/wiki/Benzylideneacetone | S0206 |
| https://en.wikipedia.org/wiki/Acetophenone | S0225（乙苯氧化） |
| https://en.wikipedia.org/wiki/Ethylbenzene | `ethylbenzene` |
| https://en.wikipedia.org/wiki/Nootkatone | S0223 B |
| https://en.wikipedia.org/wiki/Carvone | S0224（多数香芹酮由柠檬烯制） |
| https://en.wikipedia.org/wiki/Acetoin | S0204 A |
| https://en.wikipedia.org/wiki/1-Tetradecanol | `myristyl_alcohol` |
| https://en.wikipedia.org/wiki/Diethyl_oxalate | `diethyl_oxalate` |
| https://en.wikipedia.org/wiki/Chloroethane | `ethyl_chloride` |
| https://cosylab.iiitd.edu.in/flavordb2/molecules_details?id=5362620 | S0187（FEMA 3264 = 顺式-4-癸烯醛） |
| https://www.thegoodscentscompany.com/data/rw1029382.html | S0185（FEMA 3430 = (Z)-4-己烯-1-醇） |
| https://en.wikipedia.org/wiki/Friedel%E2%80%93Crafts_reaction | S0226、S0227、S0245、S0246、S0265、`valerylfuran` |
| https://en.wikipedia.org/wiki/Jasmone | S0228 |
| https://en.wikipedia.org/wiki/2,5-Undecanedione | S0268、`undecanedione`（亨斯迪克环化） |
| https://en.wikipedia.org/wiki/Stetter_reaction | `undecanedione`、`undecenedione` |
| https://en.wikipedia.org/wiki/Raspberry_ketone | S0229 |
| https://www.nbinno.com/insights/1525490-beta-damascenone-synthesis-citral-fragrance-supplier | S0230、S0231（环柠檬醛系格氏加成，α-突厥酮环氧化 → 开环 → 脱水） |
| https://en.wikipedia.org/wiki/Damascenone | S0231 |
| https://en.wikipedia.org/wiki/Damascone | S0230、S0271；S0987（结构） |
| https://en.wikipedia.org/wiki/Ionone | S0234、S0235、`pseudoionone` |
| https://patents.google.com/patent/US5185470 | S0233（合成法未采用的依据） |
| https://en.wikipedia.org/wiki/Irone | S0233 |
| https://en.wikipedia.org/wiki/Camphor | S0236（α-蒎烯 → 莰烯 → 异龙脑 → 脱氢） |
| https://en.wikipedia.org/wiki/Menthone | S0238 |
| https://en.wikipedia.org/wiki/Sotolon | S0241、S0242 |
| https://en.wikipedia.org/wiki/Furan | `furan`、S0244（糠醛气相加氢制 2-甲基呋喃同类）；`furan`（糠醛脱羰） |
| https://pages.uoregon.edu/chendon/coffee_literature/old_literature/1963%20Tetrahedron,%20Diketone%20synthesis%20--%20smokey%20flavors.pdf | S0249、S0250、S0252、S0289 |
| https://data.epo.org/gpi/EP1505051A1 | S0253 |
| https://en.wikipedia.org/wiki/Isophorone | S0267、S0254 |
| https://en.wikipedia.org/wiki/Carroll_rearrangement | ST58、S0261 |
| https://en.wikipedia.org/wiki/Geranylacetone | S0261 |
| https://en.wikipedia.org/wiki/Muscone | S0263 |
| https://www.chemicalbook.com/synthesis/dl-muscone.htm | S0263、`hexadecanedione` |
| https://en.wikipedia.org/wiki/Civetone | S0264 |
| https://en.wikipedia.org/wiki/Ethenolysis | ST60、S0334 |
| https://www.chemicalbook.com/synthesis/9-decenoic-acid-methyl-ester.htm | S0334（油酸酯乙烯解） |
| https://en.wikipedia.org/wiki/Benzophenone | S0265 |
| https://en.wikipedia.org/wiki/Fries_rearrangement | S0266 |
| https://en.wikipedia.org/wiki/Neohesperidin_dihydrochalcone | S0269 |
| https://en.wikipedia.org/wiki/Zingerone | S0270 |
| https://en.wikipedia.org/wiki/Methional | S0272（硫醇对烯醛的加成） |
| https://en.wikipedia.org/wiki/Fenchone | S0274 |
| https://en.wikipedia.org/wiki/Menthofuran | S0279 |
| https://en.wikipedia.org/wiki/Ambroxide | S0280、`ambradiol` |
| https://www.chimia.ch/chimia/article/download/2024_468/2024_468/23673 | S0280（香紫苏醇 → 香紫苏内酯 → 二醇 → 环化） |
| https://en.wikipedia.org/wiki/Pyruvic_acid | S0295（酒石酸脱水脱羧） |
| https://en.wikipedia.org/wiki/Valeric_acid | S0300、ST04 醛氧化成酸 |
| https://en.wikipedia.org/wiki/Isovaleric_acid | S0303 |
| https://en.wikipedia.org/wiki/Heptanoic_acid | S0308 |
| https://en.wikipedia.org/wiki/Nonanoic_acid | S0310 |
| https://en.wikipedia.org/wiki/Myristic_acid | S0313 |
| https://en.wikipedia.org/wiki/Phenylacetic_acid | S0316 |
| https://en.wikipedia.org/wiki/Perkin_reaction | S0318 |
| https://en.wikipedia.org/wiki/Cinnamic_acid | S0318、S0337 |
| https://uspto.report/patent/app/20170267625 | S0332、S0347（丙二酸酯烷基化） |
| https://en.wikipedia.org/wiki/%CE%92-Alanine | S0321 |
| https://en.wikipedia.org/wiki/Corynebacterium_glutamicum | S0326、S0339、S0343、S0344 |
| https://en.wikipedia.org/wiki/Bucherer%E2%80%93Bergs_reaction | ST59、S0327、S0340 |
| https://en.wikipedia.org/wiki/Methionine | S0327 |
| https://en.wikipedia.org/wiki/Levulinic_acid | S0328 |
| https://en.wikipedia.org/wiki/Undecylenic_acid | S0336、S0335 |
| https://en.wikipedia.org/wiki/Lactisole | S0341 |
| https://en.wikipedia.org/wiki/Kolbe%E2%80%93Schmitt_reaction | ST61、S0349 |
| https://en.wikipedia.org/wiki/Tiglic_acid | S0350 |
| https://en.wikipedia.org/wiki/Ethyl_formate | S0353–S0360（甲酸直接酯化） |
| https://en.wikipedia.org/wiki/Sebacic_acid | `dichlorodecane`；ST38、`sebacic_acid` |
| https://en.wikipedia.org/wiki/Benzoyl_chloride | `benzoyl_chloride` |
| https://en.wikipedia.org/wiki/Propiophenone | `propiophenone`（苯甲酸与丙酸气相交叉脱羧） |
| [Allyl acetate（维基百科）](https://en.wikipedia.org/wiki/Allyl_acetate) | S0368、ST48、`allyl_alcohol` |
| [Ethyl acetoacetate（维基百科）](https://en.wikipedia.org/wiki/Ethyl_acetoacetate) | S0365、S0417 |
| [Diethyl malonate（维基百科）](https://en.wikipedia.org/wiki/Diethyl_malonate) | S0403 |
| [Anthranilic acid（维基百科）](https://en.wikipedia.org/wiki/Anthranilic_acid) | `anthranilic_acid`、S0491 |
| [Hofmann rearrangement（维基百科）](https://en.wikipedia.org/wiki/Hofmann_rearrangement) | `anthranilic_acid` |
| [Acetic formic anhydride（维基百科）](https://en.wikipedia.org/wiki/Acetic_formic_anhydride) | S0362 |
| [Synthesis of terpinyl acetate from α-pinene（PubMed）](https://pubmed.ncbi.nlm.nih.gov/38662654/) | S0393 A |
| [Organic Syntheses CV1P0252 肉桂酸乙酯](http://www.orgsyn.org/demo.aspx?prep=CV1P0252) | S0500 B（同法，乙酸乙酯换乙酸甲酯） |
| [Gatfield 等，CHIMIA 2001, 55, 397](https://www.chimia.ch/chimia/article/view/2001_397) | S0482 B、`stillingia_oil` |
| [Ethyl 2-trans-4-cis-decadienoate（ChemicalBook）](https://m.chemicalbook.com/ChemicalProductProperty_EN_CB9758375.htm) | S0482 A |
| [Ethyl cis-4,7-octadienoate（ChemicalBook）](https://chemicalbook.com/ChemicalProductProperty_EN_CB31211413.htm) | S0475、ST28、`z_hexadienyl_chloride` |
| [Methyl (Z)-oct-4-enoate 合成（ChemicalBook）](https://chemicalbook.com/synthesis/methyl-z-oct-4-enoate.htm) | S0474（只证实顺式-4-辛烯酸乙酯的文献来源，路线未确认） |
| [Wittig reaction（维基百科）](https://en.wikipedia.org/wiki/Wittig_reaction) | ST29 |
| [Reformatsky reaction（维基百科）](https://en.wikipedia.org/wiki/Reformatsky_reaction) | S0461 |
| [Roman chamomile 精油成分（scent.vn）](https://scent.vn/pages/oil/roman-chamomile-2882) | S0445 |
| [US 4613680 当归酸及其酯的制备（Justia）](https://patents.justia.com/patent/4613680) | S0445（存疑） |
| [2-Methyl-3-furanthiol 合成（ChemicalBook）](https://www.chemicalbook.com/synthesis/2-methyl-3-furanthiol.htm) | S0471（存疑） |
| [Formation of Aroma Compounds from Ribose and Cysteine during the Maillard Reaction](https://data.zhenzhi.com/details/1/911025584) | S0471 |
| https://image-ppubs.uspto.gov/dirsearch-public/print/downloadPdf/4260830 | S0511（甲基二氢茉莉酮酸酯制法专利 US4260830） |
| https://en.wikipedia.org/wiki/%CE%94-Decalactone | S0632–S0636、S0642、S0643（2-烷基环戊酮拜耳–维利格氧化，Delfone 即 2-戊基环戊酮） |
| https://en.wikipedia.org/wiki/Cyclopentanone | `cyclopentanone`、ST27 |
| https://en.wikipedia.org/wiki/Malonic_acid | `malonic_acid`、ST36；`malonic_acid` |
| https://en.wikipedia.org/wiki/Allyl_alcohol | `allyl_alcohol`、ST30、ST36；`allyl_alcohol` |
| https://en.wikipedia.org/wiki/2-Methylpyridine | `alpha_picoline` |
| https://m.chemicalbook.com/CASEN_2294-76-0.htm | S0540（2-甲基吡啶烷基化或 2-戊烯基吡啶加氢） |
| https://image-ppubs.uspto.gov/dirsearch-public/print/downloadPdf/6333180 | S0565（茉莉酸、茉莉酮酸甲酯生物法专利 US6333180） |
| https://www.jstage.jst.go.jp/article/jos1956/33/9/33_9_628/_article/-char/ja | S0565 存疑（合成法前体） |
| https://scilit.com/publications/1910bc6bfe75b0763155e6fd8aa7af41 | S0645（由 2-[(Z)-2-戊烯基]环戊酮制茉莉内酯） |
| https://chemicalbook.com/CASEN_106-02-5.htm | S0637（十五内酯工业路线概述） |
| https://doaj.org/article/26f22b89837d49c1930749210f8a0e45 | S0637 B、`hydroxypentadecanoic_acid`、ST49、`materials/malania_seed` |
| https://patents.google.com/patent/US6512126 | S0641（羟基薄荷呋喃内酯加氢、脱水制薄荷内酯） |
| https://chemicalbook.com/CASEN_15356-74-8.htm | S0647 A（β-胡萝卜素降解） |
| https://pubs.rsc.org/en/content/articlelanding/1976/p1/p19760000532 | S0647 B（β-紫罗兰酮光解产物自氧化） |
| [Wikipedia: Myrcene](https://en.wikipedia.org/wiki/Myrcene) | S0653（β-蒎烯 400 °C 热解）、ST39 |
| [Wikipedia: Ocimene](https://en.wikipedia.org/wiki/Ocimene) | S0656 |
| [Wikipedia: Camphene](https://en.wikipedia.org/wiki/Camphene) | S0657（α-蒎烯二氧化钛异构化） |
| [Wikipedia: Valencene](https://en.wikipedia.org/wiki/Valencene) | S0652 |
| [Wikipedia: Pinene](https://en.wikipedia.org/wiki/Pinene)、[3-Carene](https://en.wikipedia.org/wiki/3-Carene) | S0658、S0659、S0681 |
| [Wikipedia: Eucalyptol](https://en.wikipedia.org/wiki/Eucalyptol) | S0660 |
| [Wikipedia: Terpinolene](https://en.wikipedia.org/wiki/Terpinolene)、[Terpinene](https://en.wikipedia.org/wiki/Terpinene)、[Phellandrene](https://en.wikipedia.org/wiki/Phellandrene) | S0655、S0670、S0680、S0668 |
| [Wikipedia: Bisabolene](https://en.wikipedia.org/wiki/Bisabolene)、[Farnesene](https://en.wikipedia.org/wiki/Farnesene) | S0669、S0678 |
| [Wikipedia: Perkin reaction](https://en.wikipedia.org/wiki/Perkin_reaction)、[Coumarin](https://en.wikipedia.org/wiki/Coumarin) | S0662 |
| [Wikipedia: Diels–Alder reaction](https://en.wikipedia.org/wiki/Diels%E2%80%93Alder_reaction)、[Prins reaction](https://en.wikipedia.org/wiki/Prins_reaction) | S0663、ST52、`isoprene` |
| [Wikipedia: Myrtenal](https://en.wikipedia.org/wiki/Myrtenal) | S0664 |
| [US 4011245（茶螺烷制法）](https://patents.justia.com/patent/4011245) | S0665 |
| [US 4014905（6,7-环氧茶螺烷、6-羟基物及其酯）](https://patents.google.com/patent/US4014905A/en) | S0671、S0784 |
| [Wikipedia: Wittig reaction](https://en.wikipedia.org/wiki/Wittig_reaction) | S0666、ST29 |
| [Wikipedia: Veratrole](https://en.wikipedia.org/wiki/Veratrole)、[p-Cymene](https://en.wikipedia.org/wiki/P-Cymene) | S0677、S0673、S0672、S0674 |
| [Wikipedia: Styrene](https://en.wikipedia.org/wiki/Styrene) | S0679（乙苯脱氢） |
| [Wikipedia: Lenthionine](https://en.wikipedia.org/wiki/Lenthionine)、[US 2021/0009553](https://uspto.report/patent/app/20210009553) | S0683（二卤甲烷 + 多硫化钠） |
| [Wikipedia: Paraldehyde](https://en.wikipedia.org/wiki/Paraldehyde) | S0685 |
| [Wikipedia: Hydrogen sulfide](https://en.wikipedia.org/wiki/Hydrogen_sulfide)、[Dimethyl sulfide](https://en.wikipedia.org/wiki/Dimethyl_sulfide)、[Methanethiol](https://en.wikipedia.org/wiki/Methanethiol) | S0686、S0688、S0689、S0693、S0742、S0801（醇 + 硫化氢，氧化铝） |
| [Wikipedia: Dimethyl disulfide](https://en.wikipedia.org/wiki/Dimethyl_disulfide) | S0694 |
| [Wikipedia: Furan-2-ylmethanethiol](https://en.wikipedia.org/wiki/Furan-2-ylmethanethiol) | S0692（现实硫脲法） |
| [Wikipedia: Acrolein](https://en.wikipedia.org/wiki/Acrolein)、[Methional](https://en.wikipedia.org/wiki/Methional) | S0700、S0687、`acrolein` |
| [Wikipedia: Indole](https://en.wikipedia.org/wiki/Indole) | S0705（苯胺 + 乙二醇气相法；煤焦油） |
| [Wikipedia: Trimethylamine](https://en.wikipedia.org/wiki/Trimethylamine) | S0706、ST34 |
| [Wikipedia: Rose oxide](https://en.wikipedia.org/wiki/Rose_oxide) | S0707 |
| [Wikipedia: Hydroxycitronellal](https://en.wikipedia.org/wiki/Hydroxycitronellal) | S0708 |
| [Wikipedia: Pyrazine](https://en.wikipedia.org/wiki/Pyrazine)、[Tetramethylpyrazine](https://en.wikipedia.org/wiki/Tetramethylpyrazine)、[Isopropanolamine](https://en.wikipedia.org/wiki/Isopropanolamine) | S0710–S0723、S0765–S0768、S0788、S0798（二羰基 / 二醇 + 二胺缩合脱氢） |
| [Wikipedia: Minisci reaction](https://en.wikipedia.org/wiki/Minisci_reaction) | ST35、S0720、S0721、S0723、S0731、S0766 |
| [Wikipedia: Darzens reaction](https://en.wikipedia.org/wiki/Darzens_reaction) | S0714 |
| [Wikipedia: Asinger reaction](https://en.wikipedia.org/wiki/Asinger_reaction)、[Thiazole](https://en.wikipedia.org/wiki/Thiazole) | S0725、S0728、S0729、S0732、S0733、S0738、S0785 |
| [Wikipedia: Hantzsch thiazole synthesis](https://en.wikipedia.org/wiki/Hantzsch_thiazole_synthesis) | S0727（同类闭环） |
| [Wikipedia: Mercaptobenzothiazole](https://en.wikipedia.org/wiki/Mercaptobenzothiazole)、[Benzothiazole](https://en.wikipedia.org/wiki/Benzothiazole) | S0734（凯利法；邻氨基苯硫酚闭环） |
| [Wikipedia: Pyridine](https://en.wikipedia.org/wiki/Pyridine)、[Chichibabin pyridine synthesis](https://en.wikipedia.org/wiki/Chichibabin_pyridine_synthesis)、[Lutidine](https://en.wikipedia.org/wiki/Lutidine)、[5-Ethyl-2-methylpyridine](https://en.wikipedia.org/wiki/5-Ethyl-2-methylpyridine) | S0740、S0724、S0776、`alpha_picoline`、ST51 |
| [Wikipedia: Skraup reaction](https://en.wikipedia.org/wiki/Skraup_reaction)、[Quinoline](https://en.wikipedia.org/wiki/Quinoline)、[p-Toluidine](https://en.wikipedia.org/wiki/P-Toluidine) | S0771、S0772 |
| [Wikipedia: Quinoxaline](https://en.wikipedia.org/wiki/Quinoxaline)、[Toluenediamine](https://en.wikipedia.org/wiki/Toluenediamine)、[Glyoxal](https://en.wikipedia.org/wiki/Glyoxal) | S0737、S0773、`toluene_2_3_diamine`、`glyoxal` |
| [Wikipedia: Piperidine](https://en.wikipedia.org/wiki/Piperidine) | S0774（吡啶加氢） |
| [Wikipedia: Skatole](https://en.wikipedia.org/wiki/Skatole)、[Fischer indole synthesis](https://en.wikipedia.org/wiki/Fischer_indole_synthesis)、[Phenylhydrazine](https://en.wikipedia.org/wiki/Phenylhydrazine) | S0775、`phenylhydrazine` |
| [Wikipedia: Thiophenol](https://en.wikipedia.org/wiki/Thiophenol) | S0743、S0752（氯苯 + 硫化氢氧化铝气相法为同类依据） |
| [Wikipedia: Benzyl mercaptan](https://en.wikipedia.org/wiki/Benzyl_mercaptan) | S0744、S0747 |
| [Wikipedia: Diallyl disulfide](https://en.wikipedia.org/wiki/Diallyl_disulfide) | S0749（烯丙基氯 + 二硫化钠；大蒜油） |
| [Wikipedia: Allyl isothiocyanate](https://en.wikipedia.org/wiki/Allyl_isothiocyanate) | S0762（芥菜籽；烯丙基氯 + 硫氰酸钾） |
| [Wikipedia: Isophorone](https://en.wikipedia.org/wiki/Isophorone) | S0756、`isophorone` |
| [Wikipedia: Trithioacetone](https://en.wikipedia.org/wiki/Trithioacetone) | S0787 |
| [Wikipedia: Furfuryl alcohol](https://en.wikipedia.org/wiki/Furfuryl_alcohol)、[Tetrahydrofurfuryl alcohol](https://en.wikipedia.org/wiki/Tetrahydrofurfuryl_alcohol) | S0795、S0796 |
| [Wikipedia: Carbon disulfide](https://en.wikipedia.org/wiki/Carbon_disulfide) | `carbon_disulfide`（甲烷 + 硫，硅胶 / 氧化铝） |
| [Wikipedia: 1,6-Hexanediol](https://en.wikipedia.org/wiki/1,6-Hexanediol) | `hexanediol`、S0691 |
| [Wikipedia: Newman–Kwart rearrangement](https://en.wikipedia.org/wiki/Newman%E2%80%93Kwart_rearrangement) | S0752 替代路线 |
| https://patents.google.com/patent/US4203907 | S0943（糠醛 + 甲基烯丙基格氏试剂、原乙酸酯克莱森重排、异构化、水解脱羧） |
| https://patents.google.com/patent/US4014902 | S0936（紫胶桐酸 → 原甲酸酯成环 → 乙酸酐热解 → 碱性甘油解聚成环） |
| https://patents.google.com/patent/US3931306 | S0876（2-甲基-3-戊烯酸异构体混合物制法） |
| https://patents.google.com/patent/US6129941 | S0940（同系物 1-甲氧基-3-己硫醇） |
| https://www.chemicalbook.com/synthesis/2-ethyl-5-methylpyrazine.htm | S0861（2,5-二甲基吡嗪烷基化） |
| https://patents.google.com/patent/GB1496411A/en | S0933（存在工业制法，细节未确认） |
| https://en.wikipedia.org/wiki/Minisci_reaction | ST35 |
| https://en.wikipedia.org/wiki/Meerwein%E2%80%93Ponndorf%E2%80%93Verley_reduction | ST31；S0999、S1063 |
| https://en.wikipedia.org/wiki/Wolff%E2%80%93Kishner_reduction | ST56 |
| https://en.wikipedia.org/wiki/Wittig_reaction | ST29 |
| https://en.wikipedia.org/wiki/Malonic_ester_synthesis | ST28；S0969 |
| https://en.wikipedia.org/wiki/Pinner_reaction | `trimethyl_orthoformate`、`trimethyl_orthoacetate` |
| https://en.wikipedia.org/wiki/Benzyl_isothiocyanate | S0893、ST53 |
| https://en.wikipedia.org/wiki/Thiophene | S0897 |
| https://en.wikipedia.org/wiki/Pyrrolidine | S0833 |
| https://en.wikipedia.org/wiki/Isoquinoline | S0949 |
| https://en.wikipedia.org/wiki/2-Methylfuran | S0871 |
| https://en.wikipedia.org/wiki/Thiophenol | S0892 |
| https://en.wikipedia.org/wiki/Ethanethiol | S0928、S0810 |
| https://en.wikipedia.org/wiki/Triethylamine | S0946 |
| https://en.wikipedia.org/wiki/Piperine | S0841 |
| https://en.wikipedia.org/wiki/2-Acetyl-1-pyrroline | S0913、S0901 |
| https://en.wikipedia.org/wiki/Mesityl_oxide | S0869、S0886 |
| https://en.wikipedia.org/wiki/Isoborneol | S0864 |
| https://en.wikipedia.org/wiki/Allyl_chloride | `allyl_chloride`；`allyl_chloride`、S1045；`allyl_chloride` |
| https://en.wikipedia.org/wiki/Carbon_disulfide | `carbon_disulfide`；`carbon_disulfide` |
| https://en.wikipedia.org/wiki/Hydrazine | `hydrazine_hydrate` |
| https://en.wikipedia.org/wiki/Ethanolamine | `ethanolamine` |
| https://en.wikipedia.org/wiki/Acrolein | `acrolein` |
| https://en.wikipedia.org/wiki/Magnesium | `magnesium` |
| https://en.wikipedia.org/wiki/Prenol | S0956、S1042、`isoprenol`（异丁烯 + 甲醛 → 异戊烯醇 → 临氢异构） |
| https://en.wikipedia.org/wiki/Piperitone | S0955（宽叶薄荷桉为胡椒酮主要来源） |
| https://en.wikipedia.org/wiki/Pulegone | S0994（胡薄荷油） |
| https://en.wikipedia.org/wiki/Methyl_isobutyl_ketone | S0977 |
| https://en.wikipedia.org/wiki/Methanethiol | S0961、S1059、S1052（醇 + 硫化氢经氧化铝催化制硫醇） |
| https://en.wikipedia.org/wiki/Hydrogen_sulfide | S1010（氢气与熔融硫约 450 °C 反应） |
| https://en.wikipedia.org/wiki/Dimethyl_sulfoxide | S1004（二甲硫醚经氧气或二氧化氮氧化） |
| https://en.wikipedia.org/wiki/Crotonic_acid | S1019（巴豆醛氧化） |
| https://en.wikipedia.org/wiki/Biphenyl | S1002（苯氧化脱氢偶联） |
| https://en.wikipedia.org/wiki/Ethylamine | S1100（乙醇 + 氨，氧化物催化） |
| https://en.wikipedia.org/wiki/4-Heptanone | S1026 |
| https://en.wikipedia.org/wiki/Isothiocyanate | S1085、S1086、S1089、S1090、S1091（二硫代氨基甲酸盐脱硫） |
| https://en.wikipedia.org/wiki/Wasabi | S1081、S1087、S1088（山葵含 ω-甲硫基烷基异硫氰酸酯） |
| https://patents.justia.com/patent/8093432 | S0987（环己烯基酮与醛羟醛缩合制突厥酮类） |
| https://en.wikipedia.org/wiki/Vilsmeier%E2%80%93Haack_reaction | S0984、S1065；S1142 |
| https://en.wikipedia.org/wiki/Methylglyoxal | S0964 |
| https://en.wikipedia.org/wiki/Methyl_vinyl_ketone | S0962、S0989（丙酮 + 甲醛缩合脱水） |
| https://en.wikipedia.org/wiki/Oleyl_alcohol | `oleyl_alcohol`（油酸酯选择还原，保留双键）、S1082 |
| https://en.wikipedia.org/wiki/2,5-Dimethylfuran | S1092、`hydroxymethylfurfural`（果糖经 5-羟甲基糠醛转化） |
| https://en.wikipedia.org/wiki/Phytol | S1077（叶绿素降解释放植醇） |
| https://en.wikipedia.org/wiki/Sclareolide | S1049 |
| https://en.wikipedia.org/wiki/Histidine | S0986（谷氨酸棒杆菌产组氨酸文献） |
| https://en.wikipedia.org/wiki/GABA | S1096（谷氨酸脱羧酶） |
| https://en.wikipedia.org/wiki/Wine_lactone | S1060（结构） |
| https://www.chemicalbook.com/CASEN_5718-75-2.htm | S1023（(R)-柠檬烯 + 硫 + γ-松油烯制硫代桉叶素） |
| https://pubchemlite.lcsb.uni.lu/e/compound/110664 | S1093（结构：2,5,5,8a-四甲基-7,8-二氢-6H-色烯） |
| https://www.chemicalbook.com/ProductChemicalPropertiesCB4239477_EN.htm | S1164（异丁香酚乙基化后碱选择性脱甲基） |
| https://patents.google.com/patent/US6034268A/en | S1110（罗勒烯次氯酸氯化、乙酸钠取代） |
| https://www.foodb.ca/compounds/FDB016170 | S1152（结构、分子式） |
| https://hmdb.ca/metabolites/HMDB31737 | S1166（δ-甲基紫罗兰酮结构） |
| https://www.chemicalbook.com/ChemicalProductProperty_EN_CB2773544.htm | S1160（蒎烷硫醇 CAS 23832-18-0） |
| https://www.chinesestandard.net/PDF.aspx/GB1886.127-2016 | S1230、S1231（原料、干馏、Ⅰ号与Ⅱ号区别） |
| https://www.chemicalbook.com/ProductChemicalPropertiesCB9325465_EN.htm | S1222（2-卤代噻唑与醇钠） |
| https://www.evitachem.com/product/evt-304155 | S1221（乙脒成环路线） |
| https://www.thegoodscentscompany.com/data/rw1037201.html | S1115（天然存在于加热蛋黄、鸡肉风味） |
| https://chemicalbook.com/ProductChemicalPropertiesCB2785172_EN.htm | S1151（天然存在，无合成路线） |
| https://en.wikipedia.org/wiki/Cyclamen_aldehyde | S1172 |
| https://en.wikipedia.org/wiki/Hydroxycitronellal | S1173 |
| https://en.wikipedia.org/wiki/Ethyl_methylphenylglycidate | S1170 |
| https://en.wikipedia.org/wiki/Diisobutyl_ketone | S1132 |
| https://en.wikipedia.org/wiki/Pyrrole | S1156（呋喃 + 氨气相合成） |
| https://en.wikipedia.org/wiki/Tyramine | S1129（酪氨酸脱羧） |
| https://en.wikipedia.org/wiki/Syringaldehyde | S1127（木质素来源） |
| https://en.wikipedia.org/wiki/Verbenone | S1147（α-蒎烯烯丙位氧化） |
| https://en.wikipedia.org/wiki/Pechmann_condensation | S1182 |
| https://en.wikipedia.org/wiki/Prins_reaction | S1195 |
| https://en.wikipedia.org/wiki/Sandmeyer_reaction | S1222 |
| https://en.wikipedia.org/wiki/Thiazole | S1222、S1223（汉奇噻唑合成） |
| https://en.wikipedia.org/wiki/Glyoxal | `glyoxal` |
| https://en.wikipedia.org/wiki/Thiourea | `thiourea` |
| https://en.wikipedia.org/wiki/Thioacetamide | `thioacetamide` |
| https://en.wikipedia.org/wiki/Acetylacetone | `acetylacetone` |
| https://en.wikipedia.org/wiki/Acetonitrile | `acetonitrile` |
| https://en.wikipedia.org/wiki/Ethylene_carbonate | `ethylene_carbonate`、`propylene_carbonate` |
| https://en.wikipedia.org/wiki/Benzyl_chloride | `benzyl_chloride` |
| [Wikipedia：Ketonic decarboxylation](https://en.wikipedia.org/wiki/Ketonic_decarboxylation) | ST27；S1263、S1352、`cyclopentanone` |
| [Wikipedia：Darzens reaction](https://en.wikipedia.org/wiki/Darzens_reaction) | S1254、S1256 B、S1291、S1379 |
| [Wikipedia：Hydroformylation](https://en.wikipedia.org/wiki/Hydroformylation) | S1256 A |
| [Wikipedia：Malonic ester synthesis](https://en.wikipedia.org/wiki/Malonic_ester_synthesis) | ST28；S1270 |
| [ChemicalBook：2-Isopropyl-N,2,3-trimethylbutyramide](https://www.chemicalbook.com/ChemicalProductProperty_EN_CB4227036.htm) | S1294（2,2-二异丙基丙腈与甲醇的里特反应） |
| [US 4150052 A（Wilkinson Sword）](https://patents.google.com/patent/US4150052A/en) | S1295、`menthanecarboxylic_acid`（格氏试剂羧化、酰氯与乙胺） |
| [US 6515188 B2](https://patents.google.com/patent/US6515188) | S1296（薄荷醇 + 环氧氯丙烷 → 乙酸钠 → 水解） |
| [ChemicalBook：Celestolide](https://www.chemicalbook.com/ProductChemicalPropertiesCB5406038_EN.htm) | S1272、`tert_butyl_dimethylindane` |
| [ChemicalBook：2-(3-phenylpropyl)tetrahydrofuran 合成](https://www.chemicalbook.com/synthesis/2-3-phenylpropyl-tetrahydrofuran.htm) | S1269（3-(2-呋喃基)丙烯酰苯加氢） |
| [The Good Scents Company：FEMA 3061](https://www.thegoodscentscompany.com/data/rw1507851.html) | S1266 组成 |
| [FooDB FDB016954](https://www.foodb.ca/compounds/FDB016954) | S1337 结构 |
| [ChemicalBook：Glycerol 5-hydroxydecanoate](https://www.chemicalbook.com/ProductChemicalPropertiesCB3196236_EN.htm) | S1375、S1372（单酯 C13H26O5） |
| [ContaminantDB CHEM006099](https://contaminantdb.ca/contaminants/CHEM006099) | S1380 结构 |
| [US 4018827 A](https://patents.google.com/patent/US4018827A/en) | S1357（环己酮碱性氧气氧化） |
| [Wikipedia：Brassylic acid](https://en.wikipedia.org/wiki/Brassylic_acid) | `brassylic_acid`、S1281 |
| [Wikipedia：Allyl alcohol](https://en.wikipedia.org/wiki/Allyl_alcohol) | `allyl_alcohol`（环氧丙烷经明矾重排） |
| [Wikipedia：Isoprene](https://en.wikipedia.org/wiki/Isoprene) | `isoprene`（异丁烯 + 甲醛普林斯法） |
| [Wikipedia：2-Methylpyridine](https://en.wikipedia.org/wiki/2-Methylpyridine) | `alpha_picoline`、`pyridylethylamine` |
| [Wikipedia：Cyclopropylamine](https://en.wikipedia.org/wiki/Cyclopropylamine) | `cyclopropylamine`（由环丙甲酰胺制得） |
| [Wikipedia：p-Menthane-3,8-diol](https://en.wikipedia.org/wiki/P-Menthane-3,8-diol) | S1335 |
| [Wikipedia：Dihydroxyacetone](https://en.wikipedia.org/wiki/Dihydroxyacetone) | S1385（氧化葡萄糖酸杆菌转化甘油） |
| [Wikipedia：Thiophenol](https://en.wikipedia.org/wiki/Thiophenol) | S1386；ST10 氯磺化–锌还原（S1316、S1334） |
| [Wikipedia：Carbon disulfide](https://en.wikipedia.org/wiki/Carbon_disulfide) | `carbon_disulfide`、`hydrogen_sulfide` 副产 |
| https://pmc.ncbi.nlm.nih.gov/articles/PMC10652705 | S1418（EFSA 2023：SmokEz C-10 原料木种、热解冷凝、取水相） |
| https://doaj.org/article/d6033a92513044849105d535ce0e35b5 | S1419（EFSA 2023：Scansmoke SEF 7525 由硬木焦油制得） |
| https://en.wikipedia.org/wiki/Methanedithiol | S1432（甲醛 + 硫化氢制二巯基甲烷） |
| https://pubs.rsc.org/en/content/getauthorversionpdf/C5GC02411K | S1433（糠醛过氧化氢氧化，检索所得，未读全文） |
| https://link.springer.com/doi/10.1007/s13399-021-01297-0 | S1433（糠醛氧化制 2(5H)-呋喃酮，检索所得，未读全文） |
| https://patents.google.com/patent/US4150052 | `menthanecarboxylic_acid`、S1417、S1469、S1470、S1504（N-取代薄荷烷甲酰胺） |
| https://patents.google.com/patent/US4178459 | `menthanecarboxylic_acid`、S1417、S1469、S1470、S1504 |
| https://image-ppubs.uspto.gov/dirsearch-public/print/downloadPdf/8541421 | S1471（取代噻吩并[2,3-d]嘧啶甜味增强剂） |
| https://image-ppubs.uspto.gov/dirsearch-public/print/downloadPdf/7928111 | S1471 |
| https://patents.google.com/patent/US10087154 | S1472、S1485（甜味增强剂的制备工艺与中间体） |
| https://patents.justia.com/patent/9732052 | S1472、S1485 |
| https://www.thegoodscentscompany.com/opl/1359963-68-0.html | S1485 |
| https://agris.fao.org/search/en/records/65df08fe0f3e94b9e5d437c4 | S1503（细菌 γ-谷氨酰转肽酶合成 γ-Glu-Val-Gly） |
| https://image-ppubs.uspto.gov/dirsearch-public/print/downloadPdf/6051741 | S1458（卡罗尔反应制 γ,δ-不饱和酮） |
| https://image-ppubs.uspto.gov/dirsearch-public/print/downloadPdf/8217203 | S1458（(E,E)-金合欢基丙酮的制备） |
| https://agregationstrasbourg.chimie.unistra.fr/lib/exe/fetch.php/synthese_de_la_divaniline_peroxydase_de_raifort.pdf | S1425（香兰素酶法氧化偶联制双香兰素） |
| https://scent.vn/en/pages/compound/dioctyl-adipate-31271 | S1495（己二酸二辛酯身份） |
| [US5185470A：Process for producing irone](https://patents.google.com/patent/US5185470)（6-甲基柠檬醛与丙酮缩合成假鸢尾酮，酸催化环化；硫酸得 α-反式为主） | S0233 B（2026-11-11） |
| [US4613680：Preparation of angelic acid or esters thereof](https://patents.justia.com/patent/4613680)（惕各酸酯在芳基亚磺酸催化下异构化，同时精馏蒸出低沸点的当归酸酯） | S0445 B（2026-11-11） |
| [Wikipedia: Angelic acid](https://en.wikipedia.org/wiki/Angelic_acid)（罗马春黄菊油含当归酸异丁酯；惕各酸光异构化效率很低） | S0445 |

## 5 存疑与待核

按设计分组列出（F01–F11 即第 1 节的 11 段）。各组提到的临时模板编号已换成 18 第 1 节的统一编号。

### F01 组（序号 1–100）

- S0064 名称：清单作“1-对-烯-4-醇”，PDF 原表（第 167 页）为“1-对-䓝烯-4-醇(又名1-对-薄荷烯-4-醇)”，已改正。
- S0075 英文名为 Dehydrolinalool，括号内化学名为 (E)-3,7-二甲基-1,5,7-辛三烯-3-醇（FEMA 3830，即脱氢芳樟醇的俗称之一 hotrienol）；按括号内结构设计，物料 ID 用 `hotrienol`，与炔醇“脱氢芳樟醇”（3,7-二甲基-6-辛烯-1-炔-3-醇）区分。工业做法未确认，按芳樟醇光敏氧化 → 二醇 → 脱水设计（葡萄酒、蜂蜜中由同一二醇酸催化生成）。
- S0003、S0004 物料 ID 暂写 `isopropanol`、`n_butanol`，须与 434、431 号取同一 ID。
- 模板编号：ST31（MPV 还原）与 F07 的 MPV 模板同义，汇总时合并；ST38（蓖麻油碱裂解制 2-辛醇、癸二酸）与 F02 的 ST39（蓖麻油酸热裂解制庚醛、10-十一烯酸）原料相同、工艺不同，宜分列。
- 防成环：F02 的 S0150 由 S0021 氧化、S0178 B 由 S0065 氧化，故本组 S0021 不用 S0150 还原，改为顺式-3-己烯基格氏试剂 + 丙烯醛 → 1,6-壬二烯-3-醇 → 乙酸酐烯丙位重排 → 皂化（重排步骤未确认）；S0065 不用 S0178 还原，改为 β-蒎烯环氧化后重排（US5994598）。
- 待 F03 核对：本组 S0026 B、S0043、S0044、S0045、S0047、S0057、S0059、S0063、S0068 分别以 S0274、S0234、S0235、S0284、S0261、S0226、S0226、S0281、S0267 为前体，F03 不应再由这些醇氧化得到对应的酮。
- 仲醇尽量不依赖对应酮：S0007、S0050、S0053、S0054 用格氏试剂加成醛（游戏简化，现实多为烯烃水合或酮加氢）；S0032、S0056、S0057 仍由酮加氢（酮的来源已确认与本组无关）。
- S0019 现实路线（US4355193）为顺式-3-己烯基氯化镁 + 1-溴-3-氯丙烷（铜催化）→ 顺式-6-壬烯基氯，需溴代试剂；本组改为 S0017 氯化后格氏试剂 + 甲醛增一碳（游戏简化）。S0017 由顺式-3-己烯基格氏试剂 + 环氧乙烷，工业做法未确认。
- S0012 现实为 Boord 反应（3-氯-2-甲基四氢吡喃 + 钠），中间体链较长，前段（糠醛 → 四氢糠醇 → 二氢吡喃 → 2,3-二氯四氢吡喃 → 甲基化）合并写进 `chloro_methyl_tetrahydropyran` 一行；是否拆出二氢吡喃等中间体待定。
- S0028 B 草酸酯化–皂化法为国内合成龙脑常见做法，未找到可核对原文（未确认）；foreverest 只说明以 α-蒎烯为原料、产品为龙脑与异龙脑混合物。可与 S0864 异龙脑共用一条配方。
- S0026 A 为 α-蒎烯与乙酸加成得小茴香酯、龙脑酯、松油酯混合物后皂化精馏（未确认比例）；B 由 S0274 加氢。
- S0072 未找到工业来源，按 α-松油烯酸催化水合设计（未确认），产物与 S0064 同为副产。
- S0073 文献合成为柠檬烯经丁基锂金属化、硼酸三甲酯、过氧化氢氧化（CNR 文献），本模组无有机锂；改为次氯酸烯丙位氯化 → 乙酸钠取代 → 皂化（未确认，游戏简化）。
- S0067 2-甲基同系物的工业路线未找到；参照 3-l-薄荷氧基丙二醇（US6515188：l-薄荷醇 + 环氧氯丙烷，路易斯酸催化）类推，按 l-薄荷醇 + 甲基烯丙基氯醚化 → 环氧化 → 水解设计（未确认）。
- S0074 依 Arctander：从北美圆柏木油单离，或由柏木烯经乙酸柏木烯酯氧化得到；本组只写单离（A），柏木烯氧化法试剂未查到，未设。
- S0077 现实用转酮醇酶缺陷的枯草芽孢杆菌突变株；香料组输出格式无“新增菌种”节，暂用 `cultures/bacillus_subtilis`（经 P51 诱变选育），是否另立菌种标签待定。
- S0078 用 N2 已登记的 `materials/sophora_flower`（槐花；槐米即花蕾，芦丁含量更高）；现实另有柑橘皮柚皮苷、新橙皮苷酶解法（US5077206）。产品按一水物。ST21 本应写“—”，因含酸水解一步，关键反应写了芦丁水解式。
- S0034 B（酵母艾利希途径生物转化 L-苯丙氨酸）为天然级来源，无单一配平式，只写 A 式。S0027 天然级（亚麻酸经脂氧合酶、裂解酶得顺式-3-己烯醛，再酵母还原）未设。
- S0036 玫瑰醇按香叶油馏分（l-香茅醇为主，含香叶醇）处理，不带纯度；若按 FEMA 2980 视为单一化合物，则化学式 C10H20O、纯度类别 K4。
- S0083 朗姆醚为乙醇、木醋液（含乙酸）在二氧化锰–硫酸中氧化后馏出的混合物，不带纯度；关键反应只写乙醇氧化一步。
- S0095 A、S0100 A 用 ST13 精馏焦油（煤焦油、木焦油），ST13 原文写“天然精油”，建议汇总时把 ST13 适用范围写成“天然精油或焦油”。
- ST42 用 M14 紫外灯模块（T4），故 S0075、S0088 为 T4；若改为过氧化氢–钼酸钠化学法单线态氧可降到 T3，但 09 无钼酸钠。
- 复用其他组中间体与原料，表内不重复列出：`magnesium`、`acrolein`、`methallyl_chloride`（F07）；`ethyl_chloride`、`cis_3_hexenyl_chloride`、`sec_butanol`（F02，丁烯水合）；`vinyl_chloride`、`benzyl_chloride`（F11）；`acetylene`、`calcium_carbide`（G2）；原料 `materials/cedarwood`（N3）、`materials/sophora_flower`（N2）。格氏反应溶剂乙醚为 435。
- S0047 天然来源（卡伯尔香木油等）不在表 B.2，只设格氏法；S0038 由 S0047 经乙酸酐烯丙位重排、皂化得到，工业多用钒催化异构化，未确认。

### F02 组（序号 101–225）

- S0187 4-癸烯醛（FEMA 3264 为顺式体）借用 S1455 顺式-4-癸烯醇乙酸酯皂化后氧化；S1455 不得反过来以 S0187 为原料，建议 S1455 用维蒂希法（己醛 + 4-乙酰氧基丁基叶立德）直接制备。
- 防循环：S0132（S0027）、S0141（S0014）、S0148（S0019）、S0150（S0021）、S0152（S0022）、S0162（S0014）、S0168（S0034）、S0185（S0012）、S0186（S0017）、S0219（S0016）以 F01 的醇为前体，F01 这些醇不能再以对应醛加氢制备。
- 133 号 d-香芹酮 B 路线（d-柠檬烯 → 亚硝酰氯 → 肟 → 水解）实际得 R-(−)-香芹酮，即 l-香芹酮（S0224）；建议把该路线移给 S0224 B，133 只保留葛缕子、莳萝籽 A 路线。
- 2-烯醛与 2,4-二烯醛（S0126、S0131、S0138、S0140、S0142、S0143、S0147、S0149、S0153、S0154、S0156、S0157、S0159、S0188、S0189）统一写“醛 + 乙醛”交叉缩合（ST06）；现实多用缩醛 + 乙烯基醚或维蒂希–霍纳法提高选择性，属游戏简化。
- S0129 2,4-戊二烯醛：巴豆醛 + 甲醛插烯羟醛缩合，工业路线未确认。
- S0198 2,4,7-癸三烯醛：顺式-3-己烯醛连续两次与乙醛缩合，未确认；顺式-3-己烯醛在碱中易异构为反式-2-己烯醛，现实多用维蒂希法。
- S0197 反式,反式-2,6-壬二烯醛：反式-4-庚烯醛由 1-戊烯-3-醇乙烯基醚克莱森重排得到（依据见 ChemicalBook），再与乙醛缩合；两步组合本身未查到工业资料。
- S0179 对-䓝-1-烯-9-醛：柠檬烯 8,9-位环氧后路易斯酸重排，未确认；过氧化氢优先环氧化环内双键，需分出 8,9-环氧物。
- S0183 藏红花醛：环柠檬醛（S0715）直接“脱氢”为游戏简化，现实为烯丙位溴化–脱溴化氢，或藏红花苦苷水解。
- S0202 12-甲基十三醛：工业路线未确认；羊毛脂异构酸切割（`methyltridecanoic_acid`）与羧酸气相选择加氢成醛均为游戏简化。
- S0211 乙基环戊烯醇酮：戊二酸二乙酯 + 草酸二乙酯克莱森–狄克曼缩合 → 乙基化 → 水解脱羧；同一路线把 `ethyl_chloride` 换成 `methyl_chloride` 即得 S0212 甲基环戊烯醇酮（09 已有物料，项目尚无路线），建议一并补。该路线为经典实验室合成，工业资料未查到。
- S0210、S0215 亚硝化–水解法依据是 2,3-丁二酮由甲乙酮亚硝化的经典制法，两种二酮的工业资料未查到；3-己酮亚硝化同时得 2,3-己二酮（S0251），M22 分出。
- S0218 6-甲基-5-庚烯-2-酮：柠檬醛（125）逆羟醛裂解为游戏简化；现实大宗为乙炔–丙酮或异丁烯–甲醛–丙酮法。
- S0200 4-羟基苯甲醛：对甲酚钴催化空气氧化未找到可核对的链接（维基写的是苯酚 + 氯仿法，有毒，未采用）。
- S0166 甲基苯甲醛为邻、间、对位异构体混合物：SMILES 写“—”，纯度暂按 K4（同一分子式的异构体混合物）；是否改为不带纯度待定。
- S0114 对-乙烯基苯酚易聚合，商品多为丙二醇或乙醇溶液；物料按纯品登记，是否改为溶液待定。
- 状态取室温：S0161 十四醛（熔点约 23–30 °C）取固，S0204 乙偶姻（约 15 °C）、S0225 苯乙酮（约 20 °C）取液，可再定。
- 联产与同釜：S0120 / S0123（丁烯羰基合成）、S0184 / S0119（丙烯羰基合成）、S0121 / S0126（丙醛–乙醛交叉缩合）、S0215 / S0251（3-己酮亚硝化）、S0136 / S0336（蓖麻油酸裂解），配方宜写成多产物。
- `butene`、`decene` 沿用 262 号石蜡裂解切取 α-烯烃（15 存疑已提现实主流为乙烯齐聚）；若改齐聚，与 1-辛烯一并改。
- 胡椒醛（S0175）新增 4 个中间体（`catechol`、`dichloromethane`、`benzodioxole`、`methylenedioxymandelic_acid`），后段与 30 号 C 路线同法；S0283、S0615、S1370 等胡椒基香料可共用。
- 顺式构型按 FEMA 编号确定：S0137（FEMA 3289，顺式-4-庚烯醛）、S0185（FEMA 3430 的前体 S0012 为 (Z)-4-己烯-1-醇）、S0187（FEMA 3264 为顺式-4-癸烯醛）。
- 有毒试剂只写名称与模板：S0224 B 的亚硝酰氯，S0210 / S0215 的亚硝酸钠–盐酸，`dichloromethane`、`ethyl_chloride`。
- 英文名：S0213 原表“4-Hexene-3-one”改为“4-Hexen-3-one”；其余按原表补回空格，S0167 保留原表“3,4-Dimethoxybenzenecarbonal”（即藜芦醛）。中文名抽查 PDF 第 169–175 页，未发现错字。
- S0224 B 写“同 133 B”（规则 7 的写法），不另设模板；S0175 后段写“同 30 号 C 路线”。
- S0144 用 S1341 反式-4-己烯醛；F10 草稿中 S1341 写成 S0012 氧化，而 S0012（FEMA 3430）为顺式醇，所得是顺式-4-己烯醛（即本组 S0185），S1341 需另找反式来源（如 ST37 克莱森重排：1-丁烯-3-醇 + 乙醛缩醛），合稿时核对。
- `ethyl_chloroacetate`、`ethyl_chloride` 与 F10 草稿用的 ID 相同（同一物质），合稿时保留一份定义；F07 的 S0859 用维蒂希模板制顺式-4-十二烯醛，S1455（顺式-4-癸烯醇乙酸酯）可同法，以保证 S0187 前体成立。
- 新模板按协调要求编号为 ST44 至 ST47，合稿时统一重编。

### F03 组（序号 226–360）

- 名称改正：S0226 原表分两行印“4-甲基苯乙酮”“对-甲基苯乙酮”（同一物质两个名称），文字层连成一串，本表加空格分开；S0240、S0243、S0277、S0288 文字层“3(2H )”的空格为斜体 H 造成，已去掉；其余名称与 PDF 第 173–178 页原表一致。
- 物料 ID 撞车：S0230 α-突厥酮用 `alpha_damascone`，F07 组 S0931 反式-α-突厥酮也用了 `alpha_damascone`；两者工业品同为以反式为主的 α-突厥酮，建议共用一个物料（S0931 写 ST26 引用 S0230），或 S0931 改 `trans_alpha_damascone`。
- 同物质两处登记：S0267 异弗尔酮即 F06 组中间体 `isophorone`，S0295 丙酮酸即 G4 组中间体 `pyruvic_acid`（路线也相同），汇总时以香料编码为准、中间体条目删去；S0312 见 458（G2 组）。
- S0233 α-鸢尾酮只写天然单离（N058 鸢尾脂，含量低）；合成法为 6-甲基柠檬醛 + 丙酮 → 假性鸢尾酮 → 氯磺酸低温环化（US5185470），6-甲基柠檬醛链条太长未采用，需要合成路线时再补。
- S0231 突厥烯酮按“α-突厥酮环氧化 → 碱开环 → 脱水”写，依据是供应商技术文章，工厂原文未确认；S0230、S0271 末步双键移位借用 F07 组 ST30，格氏加成后的烯丙醇氧化现实用二氧化锰或欧芬脑尔氧化，写成空气氧化为游戏简化。
- S0228、S0268：1,4-二酮（`undecenedione`、`undecanedione`）用施泰特尔加成（维生素 B1 盐酸盐 109 作噻唑鎓催化剂），二酮碱催化环化为亨斯迪克法（有文献）；施泰特尔一步的工业应用未确认。
- S0263 麝香酮：2,15-十六烷二酮分子内缩合 → 加氢有专利与目录依据；二酮由乙酰乙酸乙酯双烷基化制得为实验室路线，工业来源未确认，链条为蓖麻油 → 癸二酸 → 二氯癸烷 → 二酮 → 麝香酮，共 4 个中间步骤。
- S0264 灵猫酮：油酸乙酯自身复分解得 E / Z 混合二酯，狄克曼成环需高度稀释，天然为顺式，需 M22 分离异构体；复分解催化剂 `catalysts/metathesis` 是新增催化剂，与 S0334 共用（T4）。
- S0249、S0250、S0289 照 F02 组 S0211 的戊二酸酯–草酸酯缩合写法；S0250 用双甲基化（文献为甲基环戊烯醇酮甲醚再烷基化），`diethyl_methylglutarate` 的迈克尔加成–脱酯基路线为实验室法，均未找到工业原文。
- α-二酮（S0248、S0251、S0252、S0285、S0291、S0292）沿用 F02 组写法（模板 ST04，亚硝酸钠–盐酸亚硝化后水解），亚硝化选择性按亚甲基一侧写；S0251 与 S0215 由 S1084 同釜联产。汇总时可考虑单列“亚硝化–水解”模板。
- 酮式脱羧（S0255、S0259、S0262、`propiophenone`）借用 ST27 / ST27；交叉脱羧同时生成两种对称酮（3-戊酮、4-庚酮 S1026、5-壬酮等），配方宜写多产物。
- 引用他组模板：ST39（S0336 与 S0136 同一配方联产）、ST46（S0328）、ST28（S0320、S0332、S0347，与 ST28 同义）、ST28（`hexadecanedione`）、ST30（S0295）、ST54（`chloropropionic_acid`）。
- 氨基酸发酵（S0326、S0339、S0343、S0344）都写 `cultures/corynebacterium`；现实为各自的高产工程株，同一菌种标签 + 同样底物出四种产物需在配方中区分（例如另设菌株标签或用诱变选育的子标签）。S0344 发酵产物现实多以盐酸盐（534）或硫酸盐出罐，游戏按离子交换后游离碱写。
- S0327、S0340 用氢氰酸（有毒，规则 8 只写名称与模板）；DL-蛋氨酸也是 545 L-蛋氨酸酶法拆分的可能原料，需与 G 组核对。S0342 只有 L 体（179），DL 体未设计。
- S0269：新橙皮苷取自酸橙幼果（枳实），沿用 N2 组 `materials/bitter_orange`（成熟果，用于冷榨油）；若要区分，需另设“酸橙幼果”原料。另一工业路线为柚皮苷 + 异香兰素（`isovanillin`）缩合后加氢，未采用。
- S0280 Ambrox：香紫苏内酯（S1049）还原成二醇现实用氢化铝锂，写成催化加氢为游戏简化；Givaudan 生物法（高法尼醇 + 角鲨烯–藿烯环化酶）未采用。
- S0253 由 S0254 气相部分加氢（EP1505051）；S0254 由 S0267 异构–空气氧化，三者成一条链。
- 状态取室温（约 20 °C）：S0226（熔点约 22–28 °C）取液，S0241（约 26–29 °C）、S0245（约 29–30 °C）、S0254（约 26–28 °C）、S0336（约 24 °C）、S0328（约 33 °C）取固，S0302（约 24–26 °C）取液，可再定；`undecanedione` 熔点未确认，暂取固。
- 纯度类别：直链 C6 以上脂肪酸（S0308、S0310、S0313、S0334、S0335、S0336）按 09 `hexanoic_acid`、`caprylic_acid` 的先例取 K7，支链短链酸取 K4 / K3。
- S0307 由反式-3-己烯醇（S1134）氧化得反式体，FEMA 3170 商品以反式为主；若要顺式需改用叶醇（S0027）。
- S0229 覆盆子酮按苯酚 + 4-羟基-2-丁酮酸催化烷基化写（国内工业法，原文未核对）；另一常见法为 S0200 对羟基苯甲醛 + 丙酮缩合后加氢（同 S0270 写法）。
- S0313 肉豆蔻酸用 ST13 精馏椰子油脂肪酸（`coconut_fatty_acids`），ST13 原文写“天然精油”，同 F01 组意见，建议适用范围扩为“天然精油、焦油与脂肪酸混合物”。

### F04 组（序号 361–500）

- 英文名：S0386 原表为 “Menthol acetate”，按通用名写 “Menthyl acetate”；S0484 原表 “Methtyl myristate” 为拼写错误，改为 “Methyl myristate”；S0369 原表 “Ethyl acetylpropanoate”，按通用名写 “Ethyl levulinate”。中文名与 PDF 原表核对无误。
- ST28 与 F10 已用的丙二酸酯烷基化–脱羧模板（其路线表写 ST30）应为同一模板，汇总时合并。
- S0475 前体 `z_hexadienyl_chloride` 的上游需炔丙醇（乙炔 + 甲醛），本模组暂缺；ChemicalBook 只给出由 (Z)-2,5-己二烯基丙二酸单乙酯脱羧制得，烯基氯来源未确认。S0475 在补齐乙炔化学之前无法量产。
- S0474 维蒂希路线按 (Z)-4-烯酸酯的常用合成法拟定，未找到该酯工业路线原文（未确认）；鏻盐用氯化物，现实多用溴化物，本模组无溴（游戏简化）。
- S0461 雷福尔马茨基反应现实多用溴乙酸乙酯；本模组无溴，改用氯乙酸乙酯（游戏简化）。替代做法为 β-酮酯（3-氧代己酸乙酯）ST05 加氢，需另建中间体，未展开。
- S0471 美拉德反应得率低、产物为混合物，只写第一步计量式。工业合成法（CN106749128：2-甲基呋喃 S0871 经氯气与硫氰酸盐硫氰化后水解）细节未确认，需新增硫氰酸钠，未列 B 路线。
- S0445 只有 ST13 单离（N195），罗马春黄菊原料暂无来源时无法量产。惕各酸酯经有机亚磺酸催化异构化、边反应边精馏可得当归酸酯（US 4613680），需新增催化剂与模板，未列。
- S0406 为两种异构体的混合物：按“不是单一分子组成”纯度写“—”、SMILES 写“—”；若视为单一物质可改 K4。
- 阶段：表内 S 编码前体的档次以各组结果为准，本组只注明 09 物料与本组新中间体带来的更高档次；统稿时按规则 3 补注。
- 纯度：长链脂肪酸酯（S0481、S0483、S0484 等）按“液态香料”定 K4；按 10 第 1.1 节 K7“单一脂肪酸的酯”也说得通，由设计定。
- 立体：S0385 未写立体（普通 SMILES 无法表示外消旋的内型相对构型），与 S0622 乙酸异龙脑酯、S0930 乙酸 l-龙脑酯只能靠名称区分；S0399、S0400 为顺反混合物，不写立体；S0374、S0458、S0462 按商品反式写 E；S0432、S0453 按 FEMA 3497、3498（顺式-3-己烯酯）写 Z；S0386 按 l-薄荷醇（S0066）写 (1R,2S,5R)。
- S0368 现实催化剂为钯–金负载、乙酸钾助剂，借用 `catalysts/palladium`（游戏简化）。`allyl_alcohol` 的工业来源有乙酸烯丙酯水解与烯丙基氯水解两条，本组取前者。
- S0403 现实工艺为氰乙酸钠在乙醇中用氯化氢酯化；本组直接用 09 的 `cyanoacetic_acid`（游戏简化）。
- S0472、S0484 B 路线：椰子油中辛酸、肉豆蔻酸各占一部分，M22 切取相应馏分；反应式以三辛酸 / 三肉豆蔻酸甘油酯计，为形式式。

### F05 组（序号 501–650）

- S0580 名称：清单文字层缺“䓝”，按 PDF 第 189 页原表改为“对1(7),8-䓝二烯-2-醇酯”。
- S0591 英文名：原表即为“Diethyl butanedioate (Mono-menthyl succinate)”，前半与中文名不符（原表错误），按中文名与 FEMA 3810 写 Monomenthyl succinate。
- S0617 中文名“异丁酸甲基苯基原酯”照原表抄录，意为甲基苯基原醇（苏合香醇，S0032）的异丁酸酯。
- 阶段括注的“前体为 T3”按各前体的常规工业路线估计（其他组物料尚未定稿），汇总时按实际档次复核。
- 叔醇酯 S0546、S0600、S0619 与香叶醇酯 S0606 用 ST03 甲醇钠酯交换；叔醇转化率低，现实也常用对应酸酐（ST02，需新增丁酸酐、己酸酐、异丁酸酐、苯甲酸酐等中间体）或酰氯，待定。
- 香茅醇、橙花醇、龙脑等萜醇用 ST01（S0534、S0575、S0578、S0589、S0611）时强酸会引起环化或重排；甲酸酯用甲酸自催化，其余建议用 ST01 中的离子交换树脂催化。
- S0549：FEMA 2750 常与 10-十一烯酸甲酯（十一烯酸甲酯）混称；若按 10-位异构体处理，可去掉 ST30 双键内移，直接 ST01。
- S0564：亚麻籽油甲酯中亚麻酸甲酯约 55%、亚油酸甲酯约 15%，要得到 48∶52 须另配高亚油酸油（如红花籽油）或调配；“分馏得到”为游戏简化。
- S0604：S0307“3-己烯酸”未注明顺反；本品要求顺式酸，若 S0307 定为反式，需另由叶醇（S0027）经 ST04 氧化取得顺式-3-己烯酸。
- S0605：由乙酯换甲酯需甲醇大过量并蒸出乙醇，与 ST03“蒸出低级醇”的常规方向相反；也可由 3-氧代己酸甲酯加氢（需新中间体），待定。
- S0599、S0612：多烯醛直接空气氧化副反应多，现实宜用亚氯酸钠等化学氧化（ST04 的 M14 化学氧化分支，P45）。
- S0630：维基百科所列工业路线为 1,4-丁二醇铜催化脱氢（ST04）；本表用顺丁烯二酸酐（马来酸酐）加氢以复用 09 物料，现实亦有此工艺，未附依据。
- S0634：A 用香厚壳桂皮油单离的 S0638 加氢；维基百科另列 6-戊基-α-吡喃酮（S0644）加氢，可作 C 路线。
- S0637：A 当归根油中十五内酯含量低（游戏简化）；B 依据 BMC Chemistry 2022 蒜头果油路线，需新模板 ST49 与 M14 紫外灯模块（T4）；工业主流的环十二碳三烯衍生路线（环十二酮扩环）未展开。
- S0641：`hydroxymenthofurolactone` 由薄荷呋喃（S0279）氧化取得的条件未确认；专利只给出其加氢、脱水成薄荷内酯的步骤。
- S0644：现有 `cultures/trichoderma` 为李氏木霉；现实产 6-戊基-α-吡喃酮的是绿色木霉（Trichoderma viride）、哈茨木霉等，游戏简化共用；如需区分另建 `cultures/trichoderma_viride`。
- S0645：`pentenylcyclopentanone` 的工业来源未查到，路线未确认；文献仅证实由它经拜耳–维利格氧化得茉莉内酯。可考虑改为天然单离（茉莉、晚香玉净油中含量低）或暂缓。
- S0647：A 依据为专利摘要“β-胡萝卜素在氮气与空气中降解”，产物为混合物，需 M22 精馏、M15 结晶分离；B 为 1976 年文献的 β-紫罗兰酮光解后自氧化路线，需光照。
- S0565：发酵所得以 (−)-茉莉酸为主；合成法（2-(顺-2-戊烯基)-2-环戊烯-1-酮与丙二酸二甲酯加成，同 S0511）因前体无现成路线未列。
- S0579 为邻、间、对位异构体混合物，SMILES 写“—”，化学式按共同分子式；S0580 为顺反混合物，SMILES 不注立体。
- 链式模板（如“ST06 + ST05”）的中间产物（亚烷基环戊酮、2-戊烯基吡啶、甲基苄醇、胡椒醇、龙脑烯醇、茉莉酸、3-壬烯酸、2,4,7-癸三烯酸、顺式-4-庚烯酸）未单独登记物料；配方需要独立产物时再补。
- S0512、S0564 纯度类别按“多组分混合物不带纯度”写“—”；若按“脂肪酸酯 K7”处理可改。长链脂肪酸酯（S0508–S0510、S0551、S0563、S0608）记 K7。
- `allyl_alcohol` 与 F10 引用的 ID 相同，A 路线复用 F07 的 `allyl_chloride`，汇总时合并。

### F06 组（序号 651–800）

- S0738 清单名文字层丢“噁”，按 PDF 第 195 页改为“2,4,5-三甲基-3-噁唑啉”。
- S0739 清单名丢“噁”，按 PDF 第 195 页改为“2-甲基-4-丙基-1,3-噁唑烷”；原表中文“噁唑烷”与英文名 1,3-oxathiane、FEMA 3578 不符，按英文名与 FEMA 设计为 1,3-氧硫杂环己烷（C8H16OS）。
- S0745 清单名丢“䓝”，按 PDF 第 195 页改为“1-对-䓝烯-8-硫醇(又名1-对-薄荷烯-8-硫醇)”。
- S0773 清单名丢“噁”，按 PDF 第 196 页改为“5-甲基喹噁啉”。
- S0737 原表中文本身为“5,6,7,8-四氢喹啉”，与英文名 Tetrahydroquinoxaline、FEMA 3321 不符；名称照抄，结构按英文名与 FEMA 设计为四氢喹噁啉（C8H10N2）。
- S0731 原表英文名拼写为“2-Actylthiazole”，写回 2-Acetylthiazole。
- S0697 原表中文是两个同义名分两行（“2,2'-(硫代二亚甲基)-二呋喃”“二糠基硫醚”），名称照抄清单；英文原表列 4 个同义名，取第一个并括注通用名。
- S0767 为 3,5- 与 3,6-二甲基异构体混合物，SMILES 取 2-乙基-3,6-二甲基吡嗪；S0719 是其中的 3,5-（即 3-乙基-2,6-）单体，两条同一反应、是否共用同一物料待定。
- S0656、S0665、S0669、S0707 为异构体混合物，SMILES 取主异构体（反式-β-罗勒烯、β-红没药烯）或不标立体。
- S0682 聚苧烯状态写“固”（萜烯树脂），低分子量产品可能为高黏液体，未确认。
- ST10 工序未列“醇 + 硫化氢 M27 气相催化（氧化铝）”与“酮 + 硫化氢 + 氢气还原硫醇化”；本组 S0686、S0688、S0689、S0693、S0742、S0752、S0801 与 S0799、S0800 用到，建议并入 ST10（甲硫醇、二甲基硫醚、正丁硫醇的工业法即醇 + 硫化氢）。
- 硫化氢引用表内 S1010（仅用于热反应香料）作前体，与 F07 一致；是否另设非香料物料由设计者定。
- S0692 糠基硫醇现实为糠醇 + 硫脲（盐酸）成异硫脲盐后碱解；写作糠基氯 + 硫化氢 / 氢氧化钠为游戏简化（糠基氯不稳定，现实不分离）。S0697、S0735 同样经糠基氯。
- S0752 酚 + 硫化氢气相法仿照氯苯 + 硫化氢制苯硫酚的工业法，本品未确认；替代路线为 2,6-二甲基苯胺重氮化–黄原酸盐法或 2,6-二甲苯酚纽曼–夸特重排。
- S0743 芳磺酰氯还原现实多用锌粉 / 硫酸（P67），写作钯催化加氢为游戏简化。
- S0777 现实为 3-乙酰基吡啶的沃尔夫–基希纳还原（肼），写作催化氢解为游戏简化。
- S0799、S0800 的酮还原硫醇化为同类专利工艺，本品未确认。
- S0764 合成未查到原文（未确认），按同类三硫杂环 S0709 的醛 + 硫化氢 + 氧化写。
- S0699 苯酚与二甲基二硫醚的邻位硫甲基化选择性未确认。
- S0755 实验室法为 2-甲基呋喃锂化后加二甲基二硫醚；写作甲次磺酰氯亲电取代为游戏简化，未确认。
- S0665 按美国专利 4011245 由 β-紫罗兰酮经烯醇乙酸酯、还原、酸催化环化；专利用乙酸异丙烯酯与硼氢化钠，写作乙酸酐与催化加氢为游戏简化。
- S0671 按美国专利 4014905 经 6,7-环氧茶螺烷还原，还原剂未核，写作催化加氢。
- S0707 现实为光敏氧化或钼酸钠催化过氧化氢产生单线态氧；写作钨酸钠为游戏简化，也可改用 M14 紫外灯模块（P74，T4）。
- S0717、S0768 羟基吡嗪甲基化现实用重氮甲烷或经 2-氯吡嗪 + 甲醇钠；写作氯甲烷为游戏简化（O / N 选择性未核）。
- S0725、S0728、S0729、S0732、S0733、S0785 噻唑工业多用 α-卤代酮 + 硫代酰胺（汉奇法）；本组改用阿辛格反应（表内 α-巯基酮 S0992、S0282、S1125 + 醛 + 氨）得 3-噻唑啉后脱氢，避免新增硫代酰胺，脱氢条件未确认。
- S0720、S0721、S0723、S0731、S0766 用米尼斯奇反应（ST35）；乙酰基吡嗪、2-乙酰基噻唑工业上也用氰基物 + 格氏试剂或酯缩合，单品产率与异构体比例未确认。
- S0790 酸酐对巯基、羟基的选择性未核；现实常经 3-乙酰硫基己基乙酸酯再选择性脱 S-乙酰基。
- S0786 现实多为 2-己烯醛加硫代乙酸后氢化铝锂还原；写作硫化氢加成后催化加氢为游戏简化（含硫醛易使催化剂中毒）。
- S0664 桃金娘烯醛用二氧化硒（G4 定义，剧毒），只写试剂名；未用桃金娘烯醇（S0840）脱氢，因 F07 的 S0840 由 S0664 还原而来，避免循环。
- S0674 甲苯异丙基化得间、对位混合物，需精馏分出对位；S0678 橙花叔醇脱水得 α、β-金合欢烯与红没药烯混合物，α 体收率未确认。
- S0714 原料 S0166 为邻、间、对位甲基苯甲醛混合物，本路线需对位单体（精馏分离），或由甲苯加特曼–科赫羰基化直接得对位为主。
- S0762 合成法（B）需新增硫氰酸钾；天然法（A）用芥菜籽加水酶解后水蒸气蒸馏，属表 B.3 注许可的天然等同物。
- S0654 已有 `limonene`（09，d-柠檬烯），按规则 10 写 ST26。
- S0679 物料 ID `styrene` 与 G1 新增中间体（569 用）同 ID、同物质，汇总时合并为一条。
- S0765 B 用枯草芽孢杆菌发酵产四甲基吡嗪，发酵条件与氨源形态未核。

### F07 组（序号 801–950）

- 名称按 PDF 原表补字：S0835 补“噁”（二甲基噁唑）；S0868、S0907、S0910 补“䓝”；S0828、S0843、S0933、S0951 删去斜体留下的空格。
- 英文名原表误拼：S0907 原表 “Vanlillin”（PDF 原文即如此）写回 Vanillin；S0807 “disufide” 改 disulfide；S0825 “methythio” 改 methylthio。
- S0940 原表名“(S1)-甲氧基-3-庚硫醇”照抄，实为 (S)-1-甲氧基-3-庚硫醇；路线得外消旋体，(S) 型需脂肪酶拆分硫代乙酸酯，未设计；同系物制法见 US6129941。
- S0877、S0883 物料 ID 暂写 `l_tyrosine`、`l_methionine`，以 G3 组 546、545 的定名为准。S0848、S0881、S0882 直接用 09 物料。
- 复用他组中间体：F02 的 `cis_3_hexenyl_chloride`（S0941）、`ethylbenzene`（`o_nitroacetophenone`）、`ethyl_chloride`（`nitroethane`）；`allyl_chloride`、`cyclopentanone`、`chloropyrazine`、`carbon_disulfide`、`chlorobutanone` 与 F10 同 ID 同物质，合并时以 F10 为准。
- 新模板编号按协调要求写 ST30 至 ST56；ST55 与 N2 组 NT15（油脂皂化取酸）前段重合，可合并为 NT15 + 精馏分离。
- ST31 的异丙醇铝、ST35 的银盐没有现成标签：建议新增 `catalysts/aluminium_isopropoxide`，银盐暂用 `catalysts/silver`。
- 硫化氢统一用 S1010（表内注“仅用于热反应香料”，此处作试剂）；硫氢化钠不单列，写作硫化氢 + 氢氧化钠就地生成。
- S0802 由烯丙基硫醇与硫化氢自由基加成，工业上多用 1,3-二溴丙烷 + 硫脲，本路线未确认。
- S0861 用氨基钠在液氨中侧链金属化后甲基化，借 ST16 名义；商品多为 2-乙基-5(6)-甲基吡嗪混合物。
- S0831、S0832、S0834、S0950 的米尼西反应位置选择性差，需精馏分出异构体；S0832 收率低，工业路线未确认。
- S0862 走西瓜匀浆 9-脂氧合酶 / 氢过氧化物裂解酶的天然路线，产率未确认；化学法（炔烃偶联 + 林德拉加氢）需乙炔，未设计。
- S0859 维蒂希 + 罗森蒙德多步路线是文献通用法，未确认为工业路线。
- S0874 硝基烯还原现实多用铁粉 / 盐酸（P67），写作加氢为游戏简化。
- S0876 由草莓酸（S0302）碱异构化得平衡混合物，工业制法未确认（IFF 有专利族，见依据）。
- S0933 用乳酸乙酯 + 丙酮克莱森缩合再酸环化（α'-羟基-1,3-二酮成环的通用法），工业制法未确认（Montedison GB1496411 另有制法）。
- S0935 的菲塞尔曼型成环路线未确认。
- S0936 原专利用原甲酸三甲酯成环原酸酯、乙酸酐热解得反式烯酸、再碱性甘油中解聚成环；反应式简化为三步。
- S0943 照 US4203907 四步法，连带新增 `methallyl_chloride`、`acetonitrile`、`trimethyl_orthoacetate` 三个中间体，可考虑列为远期品种。
- S0846 天然来源用亚洲薄荷素油（N151），胡椒酮含量低；d / l 构型未定，SMILES 未写立体。S0847 胡薄荷油（N199）中胡椒烯酮含量低，未确认。
- S0893 B 法用木瓜籽：`materials/papaya` 是否可分出种子需定。
- S0850 是表 B.3 中的天然来源混合物，套用 NT4 模板（N267 溶剂萃取），不带纯度。
- S0901 美拉德法收率低；化学法（半胱胺 + 丙酮醛、再氧化）未设计。S0913 不稳定，商品需 β-环状糊精包合或锌盐络合，成品形态待定。
- S0863 2-甲基-1-丁醇与异戊醇沸点只差约 3 °C，杂醇油精馏需多层塔板，可做 M22 塔板层数门槛。
- 有毒试剂只写名称：硫化氢、二硫化碳、水合肼、氢氰酸（`trimethyl_orthoformate`）、氯乙醛、丙烯醛、二氯化硫。

### F08 组（序号 951–1100）

- 名称改正（对照 PDF 原表）：S0957“对-1-烯-9-醇乙酸酯”→“对-䓝-1-烯-9-醇乙酸酯”（第 200 页）；S1000“4-醇”→“4-苧醇”（第 202 页）；S1023 补“䓝”，原表三个名称并列、无“又名”，照原表以空格分隔（第 203 页）；S1061“l-基甲基醚”→“l-䓝基甲基醚”、S1068“2,4,5-三甲基唑”→“2,4,5-三甲基噁唑”（第 204 页）。
- 英文名原表拼写错误已改：S1001 pyrovate → pyruvate；S1029 hexadiencate → hexadienoate；S1061 ehter → ether；S1101 Dithianon-4-en → Dithianon-4-ene。S1073 中文名原表缺“基]”，照原表抄录未补。
- S0955 为 (6R)-胡椒酮（l-胡椒酮），与 S0846 d-胡椒酮构型相反，不能共用物料；按宽叶薄荷桉叶油单离写，需新增原料 `materials/eucalyptus_dives`。设计者如不想加原料，可改为“S0846 同线、标注构型不同”的游戏简化。
- 未确认路线（查不到工业做法，按同类反应设计）：S0954（格氏加成后酸催化环化）、S0966（甲基二硫氯对异丁醛 α 位取代）、S0991（烯硫醇与 S0824 交换；烯硫醇不稳定）、S1052（酚羟基与硫化氢气相交换为游戏简化，现实多用邻甲氧基苯胺重氮化–黄原酸法）、S1060（烯丙位羟基化–内酯化的立体选择性）、S1071（α-氯代–消除，文献多用 α-硒化消除）、S1093（β-紫罗兰酮光异构后电环化成 2H-吡喃，用 M14 紫外灯模块，P74）。
- S0978 需要乙炔链：`nonenynol` 由 ST41 用 `acetylene`（G2）、`ethylene_oxide`、`ethyl_chloride`（F02）合成，再钠–液氨还原成反式；链长 5 步以上，设计者可决定是否暂缓本项。
- S1003、S1073：亚油酸经乳酸菌水合酶生成 10-羟基-12-十八烯酸、再经酵母 β-氧化成 γ-(Z)-6-十二烯内酯有文献报道；晚香玉内酯由亚麻酸（S0564 中的亚麻酸甲酯水解所得）同法生成为推断，未确认。
- 异硫氰酸烯基酯与甲硫基烷基酯：S1079、S1080 从辣根油（N318）单离，S1081、S1087、S1088 从山葵油单离；这些成分在油中含量低（多数低于 5%），现实商品多为合成（ST53 A），但所需 ω-烯基胺、ω-甲硫基烷基胺表内与 09 均无。
- S1035、S1036 玫瑰醇（S0036）商品为 l-香茅醇（6-烯）与 7-烯异构体混合物；SMILES 按 3,7-二甲基-7-辛烯基（α 型）写，以区别香茅酯。
- S1040 异茉莉酮为 2-己基-2-环戊烯-1-酮与 2-己叉基环戊酮（S1265）的混合物，SMILES 写主成分 2-己基-2-环戊烯-1-酮；路线照 F05 `pentylcyclopentenone` 写法（ST30 双键内移）。
- 叔醇酯 S0952、S1037 用 F04 的 `butyric_anhydride`、`propionic_anhydride`（ST02）；S0974 无异戊酸酐，改用 F10 的 ST32 酰氯酯化；硫酯 S1018、S1050 用 F11 的 ST32 酰氯酰化（与 F07 S0823 写法一致）。
- S0964 丙酮醛：现实另有糖类经甲基乙二醛合酶降解的生物法；此处取 1,2-丙二醇气相氧化脱氢（与甲醛银法同类），商品为约 40% 水溶液，状态记“溶液”。
- 纯度类别界线：C10 及以上脂肪酸、脂肪酸酯与脂肪醇酯（S0960、S0969、S1024、S1064、S1082）按 K7 “单一脂肪酸及其盐与酯”记；短链香料酯记 K4；S1030 单油酸甘油酯按单甘酯记 K8。
- S1049 现实氧化剂为高锰酸钾、臭氧或生物转化；反应式以过氧化氢配平，副产甲基乙烯基酮为游戏简化。
- S1077 植醇：ST21 模板默认关键反应写“—”，此处给出皂化方程，因植醇正是 09 叶绿素铜钠盐线皂化（P41）的副产，建议设为该线副产物而非单独提取。
- S1010 硫化氢与 F10 中间体 `hydrogen_sulfide` 同一物料，物料 ID 一致；路线照 F10 写 M27 气相催化（P82）。
- S0986 L-组氨酸：现实生产菌为代谢改造的谷氨酸棒杆菌；09 标签 `cultures/corynebacterium` 为谷氨酸生产菌，是否另设高产株标签由设计者定。
- S1045 用 `allyl_chloride` 成烯丙基醚后加热对位克莱森重排，未另提重排模板（写在 ST09 原料备注里）；如 F02 的 ST37（克莱森重排）汇总后扩展到芳基烯丙基醚，可改引该模板。

### F09 组（序号 1101–1250）

- S1105 l-苧烯：表 B.2 没有以 l-苧烯为主的精油；暂用 N186 松针油（冷杉类，含 (−)-苧烯）精馏单离，也可用 N122 留兰香油；工业上 l-苧烯量小，得率待定。
- S1108 二糠基醚：糠醇酸催化脱水同时易树脂化，现实多用低酸度或热醚化，得率低。
- S1110 8-罗勒烯醇乙酸酯：按专利 US6034268，罗勒烯经次氯酸（次氯酸钠 + 乙酸）氯化后与乙酸钠取代；模板借 ST23 + ST09（羧酸盐取代），产物为 E/Z 混合物。
- S1113 反式-1,5-辛二烯-3-酮：路线为反式-3-己烯酰氯与乙烯的傅–克酰化再脱氯化氢（通用的乙烯基酮制法），本品专用路线未查到；产品不稳定，需低温保存。
- S1115 反式-2-顺式-4-顺式-7-十三碳三烯醛：全合成路线未查到；已知存在于加热蛋黄、鸡肉风味中，暂按 ST12 的加热–萃取–精馏流程做蛋黄脂质热氧化单离（游戏简化），关键反应写“—”。
- S1117 3,6-二甲基-2(3H)-苯并呋喃酮：按酚与 α-羟基酸酸催化缩合成 3-取代苯并呋喃酮的通用方法写间甲酚 + 乳酸，本品专用路线未确认；固 / 液状态未确认。
- S1120 1-吡咯啉、S1123 2-丙酰基-1-吡咯啉：两者都不稳定（前者易三聚），商品多为稳定化形式；S1123 路线比照 2-乙酰基-1-吡咯啉的文献合成（2-酰基吡咯加氢后氧化），未确认。
- S1124：两种组分不同分子式，按混合物处理，SMILES、纯度写“—”；组分之一 3,5-二乙基-1,2,4-三硫杂环戊烷另有编码 S0896，设计者可改为 ST22 配制。
- S1127 丁香醛：比照 04 香兰素 B 路线（阔叶木制浆废液碱性空气氧化），需阔叶木来源，09 的 `sulfite_spent_liquor` 不分针叶 / 阔叶；关键反应写“—”。
- S1129 酪胺：按乳杆菌酪氨酸脱羧酶写 T4；工业也有在高沸点溶剂中热脱羧（T3），可二选一。
- S1138 3-甲基己醛：`methylpentene` 由丙烯二聚得，现实镍系二聚以 2-甲基-2-戊烯为主，选择性为游戏简化。
- S1146 3,5-二乙基-2-甲基吡嗪：2,3-戊二酮 + 1,2-丁二胺缩合同时生成 2,5-二乙基-3-甲基吡嗪（S1447），需 M22 分离；`butanediamine` 路线较长。
- S1151 2,5-二乙基四氢呋喃：只查到天然存在（薄荷、香蜂草），合成路线未查到；暂写 γ-己内酯格氏加成后还原环化。
- S1152 脱氢薄荷呋喃内酯：结构按 FooDB（5,6-二氢-3,6-二甲基-2(4H)-苯并呋喃酮，C10H12O2）；薄荷呋喃光敏氧化路线未确认，紫外灯模块为 T4 时阶段应改 T4。
- S1160 蒎烷硫醇：结构按 PubChem“2-Mercaptopinane”（蒎烷-2-硫醇，CAS 23832-18-0）。
- S1166 δ-甲基紫罗兰酮：结构按 HMDB（即 β-异甲基紫罗兰酮，CAS 79-89-0）；部分资料用别的异构体，需设计者确认。
- S1168 9-十一烯醛：现实为 10-十一烯醛生产中双键迁移的伴生物，商品“十一烯醛”常为两者混合。
- S1170 十六醛（杨梅醛）：名称为商品俗名，实为 3-甲基-3-苯基缩水甘油酸乙酯（C12H14O3），不是十六碳醛。
- S1187 乙酸玫瑰酯：玫瑰醇（S0036）是香茅醇、香叶醇等的天然混合物，SMILES 按 α-型（3,7-二甲基-7-辛烯基）写，商品为混合物。
- S1213 亚硝酸乙酯：沸点约 17 °C，按气体 K9；商品多为乙醇溶液。
- S1217、S1218、S1245、S1251：清单名称注明 3、5、6 位异构体混合物，分子式相同，SMILES 写主异构体（3-位），纯度按单一化合物 K4；S1219 含甲基、乙基两种同系物，按混合物写“—”。
- S1217 2-甲氧基-3-异丙基吡嗪：按 α-氨基酸酰胺 + 乙二醛（Seifert 法）再氯代、甲氧基化；三氯氧磷由三氯化磷氧化现制，副产 HPO2Cl2 未单列物料。
- S1221：文献路线为 4-氧代四氢噻吩-3-甲酸甲酯与乙脒缩合、氯代、脱氯；这里简化为四氢噻吩-3-酮甲酰化后与乙脒直接成环，未确认。
- S1222 2-乙氧基噻唑：现实由 2-溴噻唑 + 乙醇钠；这里改用 2-氯噻唑（桑德迈尔反应需氯化亚铜，09 无此物料，游戏简化省略），`thiourea` 路线较长。
- S1230、S1231：GB 1886.127-2016 规定原料只用山楂核、干馏温度不超过 800 °C；Ⅰ号为冷凝液常压 85–110 °C 馏分，Ⅱ号为油水分层后的水相精制品；表 B.3 收录但工艺属 NT11，模板写 NT11。
- 各含硫路线中的硫化氢直接引用表 B.3 的 S1010（与 F07 一致）；格氏、炔钠、氨基钠路线用的 `magnesium`、`sodium` + `ammonia` 写法与 F07 一致。
- 新模板 ST32～6 按协调要求临时编号；F07 已有维蒂希模板 ST29，本组未用。

### F10 组（序号 1251–1400）

- 复用 F02 已提出的中间体（同一物质，不重复登记）：`ethyl_chloroacetate`（S1254、S1256 B、S1275、S1291、S1379）、`ethylbenzene`（S1316）、`diethyl_oxalate`（S1368、S1369）、`ethyl_chloride`（S1366、S1387）、`butene`（`dichlorobutane`、`sec_butylphenol`）、`cis_3_hexenyl_chloride`（`hexenylcyclopentanone`）、`glutaric_acid`（`glutaric_anhydride`）。
- 名称修正：S1296、S1344、S1364 清单名称丢了“䓝”字，已按 PDF 原表改为“3-l-䓝氧基-1,2-丙二醇”“2-(l-䓝氧基)乙醇”“对-䓝烷醇-2”。
- 英文名修正（PDF 原文有误或文字层错位）：S1262 Heptene → Hepten；S1300 Undecadione → Undecanedione；S1310 furathiol → furanthiol；S1312 disufide → disulfide；S1339 nonadione → nonanedione；S1359 rythro → erythro；S1379 toly → tolyl；S1396 Metho → Menthol。
- 异构体混合物按主成分写单一 SMILES：S1252、S1253、S1388（甘油缩醛实为五元、六元环混合物，S1388 另含邻、间位，以对位五元环计）；S1266（以 5-乙基-3,4,5,6-四甲基-2-环己烯-1-酮计）；S1302（以 (E)-5-癸烯酸计）；S1354（以 α-檀香酯计，前体 N277 含 α、β）；S1389、S1390、S1391、S1380 不写双键构型。是否改成“—”待设计者定。
- S1396 为 dl 型；S0066 / 29 号是 l-薄荷醇，dl-薄荷醇由 `thymol` 经 ST05 加氢得到，是否另立物料待定；与 S1235（l 型）的路线宜统一为“同 S1235，薄荷醇换 dl 型”。
- 模板扩用（不另立模板）：ST06 含达森缩水甘油酸酯缩合（S1254、S1256 B、S1291、S1379）、克莱森酯缩合（S1307、S1339）、珀金反应（`furylacrylic_acid`）、安息香与偶姻缩合（S1399、S1300，`thiamine_hydrochloride` 催化）、普林斯反应（`isoprene`）；ST05 含罗森蒙德还原（S1298）；ST10 含氯磺化后锌粉还原制芳硫酚（S1316、S1334）；ST15 含氯甲基化（`tert_butylbenzyl_chloride`、`dimethoxybenzylamine`）；ST20 含里特反应（S1294）；ST25 含烯烃加氯（`dichlorobutane`）。建议在模板说明中补一句。
- 未确认路线：S1263（交叉酮式脱羧选择性差，按异戊酸过量写）；S1266（按甲乙酮三分子缩合推定，未找到工业原文）；S1300（交叉偶姻缩合，产物两种羟基酮氧化后同为 2,3-十一碳二酮）；S1302（δ-癸内酯开环异构化推定）；S1339（甲乙酮在亚甲基一侧酰化的区域选择性）；S1344（薄荷醇单乙氧基化，现实或为薄荷氧基乙酸酯还原）；S1359（含巯基醛加氢易使镍中毒，现实或用硼氢化钠还原）；S1360（联苯择形甲基化，游戏简化）；S1363（肉桂醛与乙醛缩合后全加氢）；S1365（`dehydrodihydroionone` 来源）；S1380（按茉莉内酯同类做法：2-烷基环戊酮拜耳–维利格氧化）；`suberic_acid`（蓖麻油硝酸氧化取辛二酸）；`ethyl_vinyl_ether`（现实主要为乙炔与乙醇加成，本模组无乙炔，改缩醛裂解）；`chloropyrazine`；`methylpyridylethylamine`。
- S1316 乙苯氯磺化以对位为主，邻位为少量组分；S1334 实验室常用噻吩锂化后加硫，本表统一写氯磺化–还原。
- S1368、S1369、S1370、S1381、S1382、S1398 为鲜味、清凉、刺激感类酰胺，胺类前体链长（共 8 个新中间体）；是否简化为“游戏简化：胺 + 酰化剂一步”待设计者定。
- `hydrogen_sulfide`、`carbon_disulfide` 是 ST10 共用试剂，各含硫组都会用到，建议并入 04 共用中间体；有毒试剂只写模板与试剂名。
- 阶段括注：本组新中间体与已知 T3 物料已注；其他组设计的 B.3 前体（S0136、S0168、S0316 等）按推定标注，汇总时按各组实际阶段复核。
- S1385 二羟基丙酮按丙糖归 K2；三丙酸甘油酯、三丁酸甘油酯、5-羟基癸酸 / 十二酸甘油单酯归 K7；S1367 为改性阿拉伯胶，归 K6，关键反应写文字。
- S1264、S1289、S1381 的常温状态未核对（按固体写）。

### F11 组（序号 1401–1504）

- 名称按 PDF 第 218–222 页原表改正：S1413“二噁烷”、S1484“1,2-噁唑”（清单缺“噁”），S1417、S1463“对-䓝烷”（清单缺“䓝”）；S1418、S1419 补中英文间空格；S1433、S1471、S1480 去掉“(5H )”等括号内多余空格。
- S1500 原表英文 “Ally 1-propenyl disulfide” 为 Allyl 之误，已改；表 B.3 本身没有 S1483（序号 1482 直接接 S1484），清单 104 项与原表一致。
- 硫化氢用表内编码 S1010（原表限“仅用于热反应香料”），本组把它当合成试剂引用，未另建中间体；F10 另登记了 `hydrogen_sulfide`，与 S1010 为同一物质，合并时二选一。
- ID 复用（不重复登记，只引用）：F02 的 `cis_3_hexenyl_chloride`、`ethyl_chloride`；F04 的 `allyl_alcohol`、`anthranilic_acid`、`propionic_anhydride`；F07 的 `allyl_chloride`、`cyclopentanone`、`magnesium`、`acetonitrile`、`hydrazine_hydrate`、`ethanolamine`、`carbon_disulfide`；F10 的 `menthanecarboxylic_acid`、`alpha_picoline`、`pyridylethylamine`、`dichlorobutane`（1,2-二氯丁烷）、`furylacrylic_acid`、`suberic_acid`。以上为 2026-11-11 读到的各组输出，后续改 ID 时本组引用需同步。
- S1443、S1499 原拟用丙酰氯，改用 F04 已登记的 `propionic_anhydride`（ST02 / ST15 酸酐法），更接近现实做法。
- 同罐连续完成、或由表内物料一步得到且只供本组一项使用的中间物（酰氯、氰醇、2-亚甲基辛醛、α-庚叉基内酯、5-甲基糠醇、1-壬烯、2-乙基丁醇、顺式-4-癸烯醇、酰胺–醚中间体等）不登记，写在路线表的多模板组合里；实装若要拆成多步配方需补 ID。
- S1418、S1419 是烟熏液，按 NT11 写（B.3 里没有对应的 ST 模板）；SmokEz C-10 依 EFSA 意见为硬枫、白橡、山核桃等锯末回转窑热解冷凝后取水相，SEF 7525 由同类硬木焦油制得，焦油的萃取溶剂未确认（暂写 `ethanol`）。
- S1413、S1431、S1479 为位置异构体混合物，SMILES 只写名称中第一个异构体；S1426、S1436、S1445、S1450、S1463 的顺反为环上相对构型混合物，SMILES 不标相对构型；S1407–S1410 戊基肉桂系列商品以 (Z) 为主，未标双键构型。
- S1476 原表只给 (2S,5R)，SMILES 按 l-薄荷醇来源的薄荷烷甲酸补了 C1 为 R，待核；S1417、S1469、S1470 原表无构型，同样按 l-薄荷醇来源写 (1R,2S,5R)。
- S1476 用腈水合酶把 S1469 的氰甲基水合成酰胺（游戏简化，复用 `enzymes/nitrile_hydratase`）；现实更可能是薄荷烷甲酰氯直接与 2-(4-氨基苯基)乙酰胺缩合，未确认。
- S1471、S1472、S1484、S1485、S1505 是专利多步合成的甜味增强 / 风味调节化合物，路线按专利族与通用反应拼接：2,6-二氟苯甲腈、4-氯甲基-3,5-二甲基异噁唑、4-硝基吡唑等杂环砌块上游未展开；S1471 用尿素闭环、S1472 用氨基磺酰氯闭环均未核对专利原文。建议设计者决定是否简化为单一“精细化工合成”配方。
- S1485 原表只写“硫酸盐”，本组按 1∶1 硫酸氢盐写化学式 C19H27N3O8S，也可能是半硫酸盐，未确认。
- S1495 “己二酸二辛酯”按字面写正辛酯（CAS 123-79-5）；俗称“dioctyl adipate”也常指己二酸二(2-乙基己)酯（CAS 103-23-1），若为后者原料改为 S0052，未确认。
- S1503 文献为细菌 γ-谷氨酰转肽酶（GGT）转肽，本组用已有 `enzymes/glutaminase`（同具 γ-谷氨酰转移活性）；是否新增 GGT 由设计者定。L-缬氨酸只有表内 DL 型 S0340，L 型来源（发酵）未单列。
- S1425 B 路线用已有 `enzymes/laccase`，文献所见为辣根过氧化物酶 / 过氧化氢偶联，两者都属酚的酶法氧化偶联。
- S1486 9-癸烯-2-酮的工业路线未查到，本组按 1,9-癸二烯单侧瓦克氧化写；S1423 (Z)-8-十四烯醛按信息素类常用的维蒂希路线写（C8 砌块经 F10 的 `suberic_acid` 加氢、单氯代），现实工艺未核对；S1444 按 2-己炔-1-醇林德拉加氢写，乙炔、炔丙醇未登记。
- S1482 按 5 / 6-癸烯酸（S1302）酸催化自加成成酯（estolide 型）写，未确认；S1422 按丙醛氰醇 + 半胱胺成噻唑啉再氧化写，未确认；`cysteamine` 按乙醇胺–二硫化碳经 2-巯基噻唑啉水解的工业路线写。
- S1402 用己二酸在多聚磷酸中直接傅–克酰化（游戏简化，现实多用己二酸单酯酰氯）；`furylbutyric_acid` 的呋喃–丁二酸酐酰化未核对文献。
- S1498 根皮素按苹果（`materials/apple`，取果渣）浸提根皮苷后酸水解写；根皮苷在苹果树皮、叶与幼果中含量更高，若要提高产率需补原料“苹果树皮”（暂无来源）。
- S1458 卡罗尔反应（橙花叔醇 + 双乙烯酮）归入 ST06；S1486 瓦克氧化归入 ST04；S1471 吉瓦尔德反应、S1436 / S1450 阿辛格反应、S1485 喹啉成环均归入 ST11。ST30 同时含消除与双键移位，设计者也可拆成两个模板。

## 6 多步路线的实装方式

一行写了多个模板的路线（含新增中间体的路线）共 181 条，按 [18](18-batch-5-flavors.md) 第 1.4 节的规则全部合为一条配方，不需要再登记中间物；配方类型不同、必须拆开的已在路线中改用登记的中间体（11 问题 40）。

| 编码 / 中间体 | 名称 | 模板 | 实装 |
| --- | --- | --- | --- |
| S0021 | 2,6-壬二烯-1-醇 | ST16、ST02 | 合为一条 R11 配方：M14 |
| S0052 | 2-乙基己醇 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S0065 | 紫苏醇 | ST24、ST14 | 合为一条 R11 配方：M14 |
| S0067 | 3-(l-薄荷烷氧基)-2-甲基-1,2-丙二醇 | ST09、ST24 | 合为一条 R11 配方：M14 |
| S0230 | α-突厥酮 | ST16、ST04、ST30 | 合为一条 R11 配方：M14 |
| S0243 | 4,5-二氢-3(2H)-噻吩酮(四氢噻吩-3-酮) | ST25、ST06 | 合为一条 R11 配方：M14 |
| S0256 | 5-甲基-2-庚烯-4-酮 | ST16、ST04 | 合为一条 R11 配方：M14 |
| S0263 | 3-甲基环十五酮 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S0271 | β-突厥酮 | ST16、ST04、ST30 | 合为一条 R11 配方：M14 |
| S0275 | 2-甲基四氢呋喃-3-酮 | ST25、ST06 | 合为一条 R11 配方：M14 |
| S0341 | 2-(4-甲氧基苯氧基)-丙酸钠 | ST09、ST17 | 合为一条 R11 配方：M14 |
| S0540 | 2-戊基吡啶 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S0548 | 3-壬烯酸甲酯 | ST06、ST01 | 合为一条 R11 配方：M14 |
| S0549 | 9-十一烯酸甲酯 | ST01、ST30 | 合为一条 R11 配方：M14 |
| S0579 | 乙酸甲基苄醇酯(邻、间、对位混合物) | ST05、ST02 | 合为一条 R11 配方：M23（加氢模式） |
| S0580 | 乙酸顺式和反式-对1(7),8-䓝二烯-2-醇酯 | ST30、ST02 | 合为一条 R11 配方：M14 |
| S0581 | 乙酸龙脑烯醇酯 | ST05、ST02 | 合为一条 R11 配方：M23（加氢模式） |
| S0599 | 2,4,7-癸三烯酸乙酯 | ST04、ST01 | 合为一条 R11 配方：M14 |
| S0612 | 顺式-4-庚烯酸乙酯 | ST04、ST01 | 合为一条 R11 配方：M14 |
| S0615 | 乙酸胡椒醛酯 | ST05、ST02 | 合为一条 R11 配方：M23（加氢模式） |
| S0631 | δ-己内酯 | ST05、ST08 | 合为一条 R11 配方：M23（加氢模式） |
| S0641 | 薄荷内酯 | ST05、ST08 | 合为一条 R11 配方：M23（加氢模式） |
| S0662 | 二氢香豆素 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S0665 | 茶螺烷 | ST02、ST05、ST14 | 合为一条 R11 配方：M23（加氢模式） |
| S0671 | 6-羟基二氢茶螺烷 | ST24、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S0691 | 1,6-己二硫醇 | ST23、ST10 | 合为一条 R11 配方：M14 |
| S0692 | 糠基硫醇（咖啡醛） | ST23、ST10 | 合为一条 R11 配方：M14 |
| S0696 | 二丁基硫醚 | ST23、ST10 | 合为一条 R11 配方：M14 |
| S0697 | 2,2'-(硫代二亚甲基)-二呋喃二糠基硫醚 | ST23、ST10 | 合为一条 R11 配方：M14 |
| S0703 | 3-甲硫基丙酸甲酯 | ST01、ST25 | 合为一条 R11 配方：M14 |
| S0707 | 玫瑰醚 | ST24、ST09 | 合为一条 R11 配方：M14 |
| S0717 | 2-甲氧基-3-仲丁基吡嗪 | ST11、ST09 | 合为一条 R11 配方：M14 |
| S0727 | 5-羟乙基-4-甲基噻唑 | ST11、ST10 | 合为一条 R11 配方：M14 |
| S0734 | 苯并噻唑 | ST10、ST11 | 合为一条 R11 配方：M14 |
| S0735 | N-糠基吡咯 | ST23、ST09 | 合为一条 R11 配方：M14 |
| S0744 | 苄基硫醇 | ST23、ST10 | 合为一条 R11 配方：M14 |
| S0754 | 2-甲基四氢噻吩-3-酮 | ST25、ST06 | 合为一条 R11 配方：M14 |
| S0757 | 糠酸甲酯 | ST04、ST01 | 合为一条 R11 配方：M14 |
| S0763 | 2-硫代糠酸甲酯 | ST04、ST10 | 合为一条 R11 配方：M14 |
| S0768 | 2-甲氧基-3-异丁基吡嗪 | ST11、ST09 | 合为一条 R11 配方：M14 |
| S0778 | 2-乙酰基吡啶 | ST04、ST01、ST06 | 合为一条 R11 配方：M14 |
| S0779 | 3-乙酰基吡啶 | ST01、ST06 | 合为一条 R11 配方：M14 |
| S0786 | 3-巯基己醇 | ST25、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S0799 | 3-甲基-2-丁硫醇 | ST05、ST10 | 合为一条 R11 配方：M23（加氢模式） |
| S0825 | 2-甲硫基丁酸甲酯 | ST54、ST01、ST10 | 合为一条 R11 配方：M14 |
| S0835 | 2-乙基-4,5-二甲基噁唑 | ST01、ST11 | 合为一条 R11 配方：M14 |
| S0837 | 2-巯基丙酸乙酯 | ST54、ST01、ST10 | 合为一条 R11 配方：M14 |
| S0842 | 2,3-二甲基苯并呋喃 | ST09、ST11 | 合为一条 R11 配方：M14 |
| S0873 | 2-辛烯-4-酮 | ST16、ST04 | 合为一条 R11 配方：M14 |
| S0874 | 2-呋喃基-2-丙酮 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S0888 | 异戊烯基硫醇 | ST23、ST10 | 合为一条 R11 配方：M14 |
| S0895 | 2-戊基噻吩 | ST15、ST56 | 合为一条 R11 配方：M14 |
| S0912 | 甲硫基乙酸甲酯 | ST01、ST10 | 合为一条 R11 配方：M14 |
| S0913 | 2-乙酰基-1-吡咯啉 | ST05、ST04 | 合为一条 R11 配方：M23（加氢模式） |
| S0919 | 2-乙基丁醛 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S0933 | 2,5-二甲基-3(2H)-呋喃酮 | ST06、ST11 | 合为一条 R11 配方：M14 |
| S0940 | (S1)-甲氧基-3-庚硫醇 | ST04、ST25、ST10 | 合为一条 R11 配方：M14 |
| S0941 | 5-Z-辛烯酸甲酯 | ST28、ST01 | 合为一条 R11 配方：M14 |
| S0954 | 2,2-二甲基-5-(1-甲基-1-丙烯基)四氢呋喃 | ST23、ST16、ST09 | 合为一条 R11 配方：M14 |
| S0957 | 对-䓝-1-烯-9-醇乙酸酯 | ST05、ST02 | 合为一条 R11 配方：M23（加氢模式） |
| S0969 | 4-乙基辛酸 | ST23、ST28 | 合为一条 R11 配方：M14 |
| S0971 | 1-乙氧基-3-甲基-2-丁烯 | ST23、ST09 | 合为一条 R11 配方：M14 |
| S0984 | N-甲基吡咯-2-甲醛 | ST09、ST15 | 合为一条 R11 配方：M14 |
| S0987 | δ-突厥酮 | ST52、ST06 | 合为一条 R11 配方：M23（加压，不写模式） |
| S0992 | 1-巯基-2-丙酮 | ST23、ST10 | 合为一条 R11 配方：M14 |
| S0995 | 1-苯基丙醇-1 | ST15、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S1056 | 3-巯基-2-甲基戊醇 | ST25、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S1060 | 2-(2-羟基-4-甲基-3-环己烯基)-丙酸γ-内酯 | ST04、ST24、ST08 | 合为一条 R11 配方：M14 |
| S1065 | 5-甲基-2-噻吩甲醛 | ST11、ST15 | 合为一条 R11 配方：M14 |
| S1068 | 2,4,5-三甲基噁唑 | ST02、ST11 | 合为一条 R11 配方：M14 |
| S1075 | 乙酸3-巯基庚酯 | ST25、ST05、ST01 | 合为一条 R11 配方：M23（加氢模式） |
| S1085 | 异硫氰酸2-丁酯 | ST33、ST53 | 合为一条 R11 配方：M23（加氢模式） |
| S1089 | 异硫氰酸戊酯 | ST33、ST53 | 合为一条 R11 配方：M23（加氢模式） |
| S1106 | 正己硫醇 | ST23、ST10 | 合为一条 R11 配方：M14 |
| S1110 | 8-罗勒烯醇乙酸酯 | ST23、ST09 | 合为一条 R11 配方：M14 |
| S1113 | 反式-1,5-辛二烯-3-酮 | ST32、ST15、ST30 | 合为一条 R11 配方：M14 |
| S1117 | 2-(4-甲基-2-羟基苯基)-丙酸-γ-内酯 | ST15、ST08 | 合为一条 R11 配方：M14 |
| S1123 | 2-丙酰基-1-吡咯啉 | ST32、ST15、ST05、ST04 | 合为一条 R11 配方：M23（加氢模式） |
| S1132 | 2,6-二甲基-4-庚酮 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S1139 | (反式,反式)-2,4-壬二烯 | ST23、ST16、ST30 | 合为一条 R11 配方：M14 |
| S1142 | 1-乙基-2-甲酰基吡咯（茶吡咯） | ST09、ST15 | 合为一条 R11 配方：M14 |
| S1145 | 对-丙基苯酚 | ST15、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S1151 | 2,5-二乙基四氢呋喃 | ST16、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S1159 | 硫代香叶醇 | ST23、ST10 | 合为一条 R11 配方：M14 |
| S1164 | 3-丙烯基-6-乙氧基苯酚 | ST09、ST36 | 合为一条 R11 配方：M14 |
| S1172 | 兔耳草醛（仙客来醛） | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S1177 | 3-巯基-2-戊酮 | ST23、ST10 | 合为一条 R11 配方：M14 |
| S1178 | d,l-薄荷酮甘油缩酮 | ST05、ST07 | 合为一条 R11 配方：M23（加氢模式） |
| S1183 | 2-巯基丙酸 | ST23、ST10 | 合为一条 R11 配方：M14 |
| S1184 | 2-甲基-4-戊烯酸 | ST37、ST04 | 合为一条 R11 配方：M14 |
| S1186 | 环己基乙酸烯丙酯 | ST05、ST01 | 合为一条 R11 配方：M23（加氢模式） |
| S1188 | 3-(2-呋喃基)丙酸乙酯 | ST05、ST01 | 合为一条 R11 配方：M23（加氢模式） |
| S1190 | 3-环己基丙酸烯丙酯 | ST05、ST01 | 合为一条 R11 配方：M23（加氢模式） |
| S1194 | 环己基丁酸烯丙酯 | ST05、ST01 | 合为一条 R11 配方：M23（加氢模式） |
| S1217 | 2-甲氧基-3(5或6)-异丙基吡嗪 | ST11、ST09 | 合为一条 R11 配方：M14 |
| S1219 | 2-甲基(或乙基)-3(5或6)-甲氧基吡嗪 | ST23、ST09 | 合为一条 R11 配方：M14 |
| S1222 | 2-乙氧基噻唑 | ST11、ST09 | 合为一条 R11 配方：M14 |
| S1223 | 2,4-二甲基-5-乙酰基噻唑 | ST23、ST11 | 合为一条 R11 配方：M14 |
| S1227 | 3-(2-甲基丙基)吡啶 | ST23、ST28 | 合为一条 R11 配方：M14 |
| S1234 | 2-甲基-4-苯基-2-丁醇 | ST05、ST16 | 合为一条 R11 配方：M23（加氢模式） |
| S1243 | 邻-丙基苯酚 | ST09、ST37、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S1248 | 2-甲基-3-(对甲基苯基)丙醛 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S1255 | 2-甲基-4-苯丁醛 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S1269 | 2-(3-苯丙基)四氢呋喃 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S1300 | 2,3-十一碳二酮 | ST06、ST04 | 合为一条 R11 配方：M14 |
| S1359 | 赤、苏-3-巯基-2-甲基丁-1-醇 | ST25、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S1363 | 5-苯基戊醇 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S1413 | 2-戊基-5或6-酮-1,4-二噁烷 | ST09、ST08 | 合为一条 R11 配方：M14 |
| S1422 | 2-丙酰基-2-噻唑啉 | ST11、ST04 | 合为一条 R11 配方：M14 |
| S1423 | 顺式-8-十四烯醛 | ST29、ST04 | 合为一条 R11 配方：M14 |
| S1426 | 顺式和反式-2-庚基环丙烷羧酸 | ST30、ST57 | 合为一条 R11 配方：M14 |
| S1427 | 5-羟基-4-甲基己酸δ-内酯 | ST25、ST05、ST08 | 合为一条 R11 配方：M23（加氢模式） |
| S1446 | 乙酸2-乙基丁酯 | ST05、ST01 | 合为一条 R11 配方：M23（加氢模式） |
| S1455 | 顺式-4-癸烯醇乙酸酯 | ST02、ST29 | 合为一条 R11 配方：M14 |
| S1457 | N-苯甲酰邻氨基苯甲酸 | ST32、ST20 | 合为一条 R11 配方：M14 |
| S1464 | 4-羟基-4-甲基-7-顺式-癸烯酸γ-内酯 | ST16、ST08 | 合为一条 R11 配方：M14 |
| S1465 | 2-甲基辛醛 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S1471 | 4-氨基-5,6-二甲基噻吩并[2,3-d]嘧啶-2(1H)-酮盐酸盐 | ST11、ST17 | 合为一条 R11 配方：M14 |
| S1472 | 3-[(4-氨基-2,2-二氧-1H-2,1,3-苯并噻二嗪-5-基)氧]-2,2-二甲基-N-丙基丙酰胺 | ST20、ST09、ST11 | 合为一条 R11 配方：M14 |
| S1474 | 5-戊基-3H-呋喃-2-酮 | ST25、ST08 | 合为一条 R11 配方：M14 |
| S1477 | 5-甲基-2-呋喃甲硫醇 | ST05、ST10 | 合为一条 R11 配方：M23（加氢模式） |
| S1480 | 3-庚基二氢-5-甲基-2(3H)-呋喃酮 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| S1484 | 3-{1-[(3,5-二甲基-1,2-噁唑-4-基)甲基]-1H-吡唑-4-基}-1-(3-羟基苄基)咪唑啉-2,4-二酮 | ST33、ST11 | 合为一条 R11 配方：M23（加氢模式） |
| S1485 | 4-氨基-5-[3-(异丙基氨基)-2,2-二甲基-3-氧代丙氧基]-2-甲基喹啉-3-羧酸硫酸盐 | ST20、ST09、ST11、ST17 | 合为一条 R11 配方：M14 |
| S1489 | 4-羟基-4-甲基-5-己烯酸γ-内酯 | ST16、ST08 | 合为一条 R11 配方：M14 |
| S1505 | 2-(4-甲基苯氧基)-N-(1H-吡唑-3-基)-N-(噻吩-2-基甲基)乙酰胺 | ST33、ST32、ST20 | 合为一条 R11 配方：M23（加氢模式） |
| S1506 | 2-己基吡啶 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| `chloro_methyl_tetrahydropyran` | 3-氯-2-甲基四氢吡喃 | ST05、ST16 | 合为一条 R11 配方：M23（加氢模式） |
| `benzoyl_chloride` | 苯甲酰氯 | ST23、ST32 | 合为一条 R11 配方：M14 |
| `dichlorodecane` | 1,10-二氯癸烷 | ST01、ST05、ST23 | 合为一条 R11 配方：M23（加氢模式） |
| `methylhexanol` | 2-甲基-1-己醇 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| `methylheptanol` | 2-甲基-1-庚醇 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| `methylpentanal` | 4-甲基戊醛 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| `allyl_alcohol` | 烯丙醇 | ST01、ST48 | 一条 R11 配方：M14（ST48 只是副产乙酸的去向，不是本路线的一步） |
| `z_hexadienyl_chloride` | (Z)-1-氯-2,5-己二烯 | ST05、ST23 | 合为一条 R11 配方：M23（加氢模式） |
| `propylcyclopentanone` | 2-丙基环戊酮 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| `butylcyclopentanone` | 2-丁基环戊酮 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| `pentylcyclopentanone` | 2-戊基环戊酮 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| `heptylcyclopentanone` | 2-庚基环戊酮 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| `octylcyclopentanone` | 2-辛基环戊酮 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| `nonylcyclopentanone` | 2-壬基环戊酮 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| `pentylcyclopentenone` | 2-戊基-2-环戊烯-1-酮 | ST06、ST30 | 合为一条 R11 配方：M14 |
| `hydroxypentadecanoic_acid` | 15-羟基十五酸 | ST49、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| `hexanediol` | 1,6-己二醇 | ST01、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| `leucinamide` | L-亮氨酰胺 | ST01、ST20 | 合为一条 R11 配方：M14 |
| `isoleucinamide` | DL-异亮氨酰胺 | ST01、ST20 | 合为一条 R11 配方：M14 |
| `prenyl_methyl_ether` | 1-甲氧基-3-甲基-2-丁烯 | ST23、ST09 | 合为一条 R11 配方：M14 |
| `undecylcyclopentanone` | 2-十一烷基环戊酮 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| `methylthiopropylamine` | 3-甲硫基丙胺 | ST25、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| `sclareol` | 香紫苏醇 | NT4、ST21 | 合为一条 R04 配方（M18） |
| `homoveratrylamine` | 3,4-二甲氧基苯乙胺（高藜芦胺） | ST05、ST20 | 合为一条 R11 配方：M23（加氢模式） |
| `nonenynol` | (Z)-6-壬烯-3-炔-1-醇 | ST41、ST23 | 合为一条 R11 配方：M23（加氢模式） |
| `heptyne` | 1-庚炔 | ST23、ST30 | 合为一条 R11 配方：M14 |
| `octyne` | 1-辛炔 | ST23、ST30 | 合为一条 R11 配方：M14 |
| `menthanecarboxylic_acid` | 对䓝烷-3-甲酸 | ST23、ST16 | 合为一条 R11 配方：M14 |
| `hexylcyclohexanone` | 2-己基环己酮 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| `dichlorooctane` | 1,8-二氯辛烷 | ST01、ST05、ST23 | 合为一条 R11 配方：M23（加氢模式） |
| `methoxymethylbenzylamine` | 2-甲氧基-4-甲基苄胺 | ST09、ST33 | 合为一条 R11 配方：M23（加氢模式） |
| `methylpyridylethylamine` | 2-(5-甲基-2-吡啶基)乙胺 | ST11、ST06、ST25 | 合为一条 R11 配方：M14 |
| `dimethoxybenzylamine` | 2,4-二甲氧基苄胺 | ST15、ST33 | 合为一条 R11 配方：M23（加氢模式） |
| `pyridylethylamine` | 2-(2-吡啶基)乙胺 | ST06、ST25 | 合为一条 R11 配方：M14 |
| `cyclohexylhexanoic_acid` | 6-环己基己酸 | ST15、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| `cyclohexylvaleric_acid` | 5-环己基戊酸 | ST06、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| `cyclopropanecarboxylic_acid` | 环丙烷甲酸 | ST23、ST57 | 合为一条 R11 配方：M14 |
| `geranylamine` | 香叶胺 | ST23、ST33 | 合为一条 R11 配方：M23（加氢模式） |
| `chlorooctanol` | 8-氯-1-辛醇 | ST05、ST23 | 合为一条 R11 配方：M23（加氢模式） |
| `vinylpyridine` | 2-乙烯基吡啶 | ST06、ST30 | 合为一条 R11 配方：M14 |
| `malononitrile` | 丙二腈 | ST20、ST30 | 合为一条 R11 配方：M14 |
| `hydroxypivalic_acid` | 3-羟基-2,2-二甲基丙酸 | ST06、ST04 | 合为一条 R11 配方：M14 |
| `isoxazolylmethyl_pyrazolamine` | 1-[(3,5-二甲基异噁唑-4-基)甲基]-1H-吡唑-4-胺 | ST09、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| `decadiene` | 1,9-癸二烯 | ST16、ST05、ST23 | 合为一条 R11 配方：M23（加氢模式） |
| `methylhexene` | 5-甲基-1-己烯 | ST23、ST16 | 合为一条 R11 配方：M14 |
| `methylheptene` | 5-甲基-1-庚烯 | ST23、ST16 | 合为一条 R11 配方：M14 |
| `p_anisidine` | 对氨基苯甲醚 | ST09、ST05 | 合为一条 R11 配方：M23（加氢模式） |
| `aminopyrazole` | 3-氨基吡唑 | ST11、ST04 | 合为一条 R11 配方：M14（吡唑啉脱氢在液相做） |
| `carbethoxypropyl_tpp_bromide` | (3-乙氧羰基丙基)三苯基溴化鏻 | ST05、ST23 | 合为一条 R11 配方：M23（加氢模式） |
| `ethyl_bromoacetate` | 溴乙酸乙酯 | ST54、ST01 | 合为一条 R11 配方：M14 |
| `butenylamine` | 3-丁烯-1-胺 | ST23、ST33 | 合为一条 R11 配方：M23（加氢模式） |
| `pentenol_4` | 4-戊烯-1-醇 | ST05、ST23、ST43 | 合为一条 R11 配方：M23（加氢模式） |
| `pentenylamine` | 4-戊烯-1-胺 | ST23、ST33 | 合为一条 R11 配方：M23（加氢模式） |
| `hexenylamine` | 5-己烯-1-胺 | ST30、ST33 | 合为一条 R11 配方：M23（加氢模式） |

