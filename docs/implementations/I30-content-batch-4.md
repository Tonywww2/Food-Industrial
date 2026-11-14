# I30 第四批添加剂内容（140–423）

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | E 内容 |
| 依赖 | I20、I29 |
| 原文档 | [additives/15](../additives/15-batch-4.md)、[additives/05 §G](../additives/05-processes.md)、[additives/04 §实现约定（I18–I20）](../additives/04-production-routes.md)、[additives/08 §C](../additives/08-raw-materials.md)、[additives/09](../additives/09-chemical-formulas.md)、[additives/10 §1.1、§1.5](../additives/10-purity-jei-ae2.md)、[additives/11 §待定问题](../additives/11-roadmap.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `content.batch4`（数据生成）、`content.Cultures`、`manual.ManualRecipes`（T0 路线） |

> 物料已在 [I29](I29-purity-caps-compound.md) 按 2026-11-05 的 09、08 注册（第四批的中间体与产物、新增原料、菌种、酶、表达载体）；机器的第四批模式与模块已在 [I15](I15-machines-t2.md)–[I17](I17-machines-t4.md) 做好（M14 真空、M20 氯化、M21 高剪切乳化头、M23 加压蒸煮与水热合成、M24 熔盐、M25 分级、M27 气相催化与流化床、M30 基因重组）。本计划只写配方，缺的物料补登并写回 09 / 08。

## 目标

第四批 284 项（GB 2760-2024 表 A.1 其余 234 种、表 C.3 其余 50 种酶制剂）在原料齐全时都能沿 15 的路线做出；只缺“暂无来源”原料的路线照常写入，等以后补来源。

## 范围

- 做：140–423 的配方；新工艺 P80–P96 与并入现有工艺的做法；15 新增中间体的配方；新菌种的分离、纯化、扩培；工程菌种的基因重组与表达载体；第四批的铝色淀
- 不做：暂无来源原料的获取途径（08 §C 标“暂无来源（待定）”的原料，[additives/11 §待定问题 21](../additives/11-roadmap.md) 暂定维持现状）
- 不做：第五批（[I31](I31-content-batch-5.md)）

> 2026-11-12（11 问题 41）：工程菌与诱变株改为概率平板——同一宿主、同样操作得到的菌株共用一块 R18 平板配方，产出列出全部菌株、按 `plate.weights` 抽取，玩家挑菌落。本批受影响的是 181 号的产半胱氨酸大肠杆菌（与第五批的产苏氨酸、产酪氨酸、产色氨酸共用一块无供体样本的平板）与 12 种酶的工程菌、冰结构蛋白、凝乳酶（各自带供体样本，输入本来就不同，失败时产出宿主菌）。联产也要改：煤焦油、木焦油精馏与 262 号 1-辛烯等配方改为一条多产出（第五批的单体是同一条配方的产出，见 [additives/18 §1.5](../additives/18-batch-5-flavors.md)）。

## 任务

- [x] 实现约定沿用 [additives/04 §实现约定（I18–I20）](../additives/04-production-routes.md)：计量、默认 T1、基准时长、来源值、回收溶剂、门槛、合并步骤、吸附柱只收流体（固体提取物的树脂吸附在搅拌反应罐里以树脂为催化剂进行）、T4 化学步骤由 M23 / M14 承担；本批新增的约定写回该节
- [x] 已有配方的物料不重复写：34 种产物沿用 09 已有物料（如 373 氮气、二氧化碳、β-胡萝卜素），前三批也已做了部分本批中间体（乙二胺四乙酸、动物脂脂肪酸、L-苹果酸等）；逐项核对 15 的路线，缺的路线补上
- [x] 新工艺（[additives/05 §G](../additives/05-processes.md)）：P80 化学制浆、P81 水热合成（M23 现有加压档）、P82 气相催化合成（M27 气相催化、流化床）、P83 乳化（M21 高剪切乳化头）、P84 种子胶提取线、P85 淀粉乳变性、P86 淀粉干热磷酸化、P87 多糖碱化–醚化、P88 选择性包合沉淀、P89 / P90 天然色素提取线、P91 布赫勒反应、P92 热水解、P93 氯化焙烧（M20 氯化）、P94 基因重组（M30）、P95 层析分级（M25 分级）、P96 酶固定化；并入现有工艺的 7 种做法见 05 §G 开头。反复出现的步骤加进 `Batch` 共用步骤
- [x] 新菌种（[additives/08 §C 菌种与酶](../additives/08-raw-materials.md)）：平板分离样本、纯化、扩培，做法同 I18 的 `Cultures`；08 写的样本（土壤、腐烂水果、发霉谷物、腐木、堆肥、稻草、树叶或果皮、甜菜根、生肉）对应到 `samples/<种类>`，缺的样本标签拟定后写回 08
- [x] 基因重组（P94，[additives/15 §新增工艺](../additives/15-batch-4.md)）：15 个工程菌种（12 种只有重组来源的酶、凝乳酶、产半胱氨酸大肠杆菌、冰结构蛋白酵母）= 宿主菌种 + 供体样本 + 表达载体（I17 的平板操作）；表达载体由菌种工作台用大肠杆菌制得（游戏抽象），每次重组消耗 1 个
- [x] 酶（374–423，[additives/15 §F](../additives/15-batch-4.md)）：沿用 I20 的做法（同一菌种不同培养基得不同的酶）；分级（P95）：385、388、389 由 99 分级，390 B 由 98 分级，421 由 102 分级；固定化（P96）：179、330、350、374、406；酶不带纯度
- [x] 铝色淀：194、195、196、202、233 各有一个铝色淀物品，配比与纯度同 I20（色素 ×1 + 新制氢氧化铝 ×2 → 色淀 ×1）；233 的色淀为 `carmine`，界限见 [additives/10 §1.5](../additives/10-purity-jei-ae2.md)
- [x] 可选路线照常注册：158、281 A、344、366 B（氢氰酸、光气；[additives/11 §待定问题 5](../additives/11-roadmap.md)，2026-11-05 定）
- [x] 不带纯度的物料（[additives/10 §1.1](../additives/10-purity-jei-ae2.md)）：酶、酶源、蒸馏水与多组分混合物（脂肪酸混合物、制浆废液、包合物湿饼、天然油脂、紫胶洗色水等，以 09 的“—”为准）的产出不写纯度，配方不对它们设门槛
- [x] 暂无来源的原料：配方照写；纯度报表把这些路线单列为“原料暂无来源”，不参与“最早可做出的档次”的核对

## 清单

| 编号 | 名称 | 最早阶段 | 物料 ID（09 / 08） | 路线（原文档） | 状态 |
| --- | --- | --- | --- | --- | --- |
| 140 | L-苹果酸 | T3（富马酸酶为 T4，见 56 B） | `l_malic_acid` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 141 | 偏酒石酸 | T2 | `metatartaric_acid` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 142 | 磷酸 | T2 / T3 | `phosphoric_acid` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 143 | 盐酸 | T2 / T3 | `hydrochloric_acid` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 144 | 己二酸 | T3 | `adipic_acid` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 145 | 氢氧化钙（熟石灰） | T0 | `slaked_lime` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 146 | 氢氧化钾 | T1 / T3 | `potassium_hydroxide` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 147 | 柠檬酸钾 | T2 | `potassium_citrate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 148 | 碳酸氢三钠（天然碱） | T1 | `trona` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 149 | 柠檬酸一钠 | T2 | `monosodium_citrate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 150 | 碳酸氢钾 | T1 / T2 | `potassium_bicarbonate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 151 | 磷酸三钾 | T2 | `tripotassium_phosphate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 152 | 富马酸一钠 | T2（57 为 T3） | `monosodium_fumarate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 153 | 葡萄糖酸钠 | T3 / T4 | `sodium_gluconate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 154 | DL-苹果酸钠 | T2（56 A 为 T3） | `sodium_dl_malate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 155 | L-苹果酸钠 | T2（140 为 T4） | `sodium_l_malate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 156 | 微晶纤维素 | T3 | `microcrystalline_cellulose` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 157 | 硬脂酸镁 | T2 | `magnesium_stearate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 158 | 亚铁氰化钠 | T2（氢氰酸为 T3） | `sodium_ferrocyanide` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 159 | 硅酸钙 | T3 | `calcium_silicate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 160 | 柠檬酸铁铵 | T2（三氯化铁为 T3，见 161） | `ferric_ammonium_citrate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 161 | 酒石酸铁 | T2（氯气为 T3） | `ferric_tartrate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 162 | 纤维素 | T1 / T3 | `cellulose` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 163 | 聚二甲基硅氧烷及其乳液 | T3 | `polydimethylsiloxane` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 164 | 焦亚硫酸钾 | T2 | `potassium_metabisulfite` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 165 | 亚硫酸钠 | T2 | `sodium_sulfite` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 166 | 亚硫酸氢钠 | T2 | `sodium_bisulfite` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 167 | 低亚硫酸钠（保险粉） | T2 / T3 | `sodium_dithionite` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 168 | 硫磺 | T0 / T1 | `sulfur` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 169 | 硫酸铝铵 | T2 | `ammonium_alum` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 170 | 磷酸氢钙 | T2 | `dicalcium_phosphate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 171 | 磷酸氢二铵 | T2（氨为 T3） | `diammonium_phosphate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 172 | 碳酸铵 | T1 / T3 | `ammonium_carbonate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 173 | 硝酸钠 | T1 / T2 | `sodium_nitrate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 174 | 亚硝酸钾（硝石） | T2 / T3 | `potassium_nitrite` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 175 | 葡萄糖酸亚铁 | T2（113 为 T3） | `ferrous_gluconate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 176 | 5'-鸟苷酸二钠 | T3（73 为 T4） | `guanylic_disodium` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 177 | 5'-肌苷酸二钠 | T3（73 为 T4） | `inosinic_disodium` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 178 | 琥珀酸二钠 | T3 | `disodium_succinate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 179 | L-丙氨酸 | T4 | `l_alanine` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 180 | 氨基乙酸（甘氨酸） | T2 / T3 | `glycine` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 181 | L-半胱氨酸盐酸盐 | T3 / T4 | `l_cysteine_hydrochloride` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 182 | 碳酸镁 | T1 / T3 | `magnesium_carbonate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 183 | 磷酸三钠 | T2 | `trisodium_phosphate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 184 | 磷酸二氢钠 | T2 | `monosodium_phosphate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 185 | 磷酸二氢钙 | T2 | `monocalcium_phosphate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 186 | 磷酸氢二钾 | T2 | `dipotassium_phosphate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 187 | 磷酸二氢钾 | T2 | `monopotassium_phosphate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 188 | 乳酸钾 | T2 | `potassium_lactate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 189 | 乳酸钠 | T2 | `sodium_lactate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 190 | 焦磷酸一氢三钠 | T2 | `trisodium_pyrophosphate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 191 | 聚偏磷酸钾 | T2 | `potassium_polymetaphosphate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 192 | 酸式焦磷酸钙 | T2 | `calcium_acid_pyrophosphate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 193 | 焦磷酸四钾 | T2 | `tetrapotassium_pyrophosphate` | [additives/15 §A](../additives/15-batch-4.md) | 已完成 |
| 194 | 苋菜红及其铝色淀 | T3 | `amaranth`、`amaranth_lake` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 195 | 赤藓红及其铝色淀 | T3 | `erythrosine`、`erythrosine_lake` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 196 | 新红及其铝色淀 | T3 | `new_red`、`new_red_lake` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 197 | β-胡萝卜素 | T2 / T4 | `beta_carotene` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 198 | 二氧化钛 | T3 | `titanium_dioxide` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 199 | 酸性红（偶氮玉红） | T3 | `azorubine` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 200 | 氧化铁黑 | T2 | `iron_oxide_black` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 201 | 氧化铁红 | T1 / T2 | `iron_oxide_red` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 202 | 喹啉黄及其铝色淀 | T3 | `quinoline_yellow`、`quinoline_yellow_lake` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 203 | 番茄红素 | T2 / T4 | `lycopene` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 204 | β-阿朴-8'-胡萝卜素醛 | T3（107 B 为 T4） | `apocarotenal` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 205 | 姜黄 | T0 | `turmeric` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 206 | 红花黄 | T1 / T3 | `safflower_yellow` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 207 | 紫胶红（虫胶红） | T2 / T3 | `lac_dye` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 208 | 越橘红 | T1 / T3 | `lingonberry_red` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 209 | 辣椒橙 | T2 | `capsicum_orange` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 210 | 红米红 | T1 / T3 | `red_rice_red` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 211 | 菊花黄浸膏 | T1 / T2 | `coreopsis_yellow` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 212 | 黑豆红 | T1 / T3 | `black_bean_red` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 213 | 高粱红 | T1 / T3 | `sorghum_red` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 214 | 玉米黄 | T2 | `corn_yellow` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 215 | 萝卜红 | T1 / T3 | `radish_red` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 216 | 可可壳色 | T1 / T3 | `cocoa_shell_color` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 217 | 黑加仑红 | T1 / T3 | `blackcurrant_red` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 218 | 栀子蓝 | T2 / T3 | `gardenia_blue` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 219 | 沙棘黄 | T2 / T3 | `sea_buckthorn_yellow` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 220 | 玫瑰茄红 | T1 / T3 | `roselle_red` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 221 | 橡子壳棕 | T1 / T3 | `acorn_shell_brown` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 222 | 桑椹红 | T1 / T3 | `mulberry_red` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 223 | 天然苋菜红 | T1 / T3 | `amaranthus_red` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 224 | 金樱子棕 | T1 / T3 | `rosa_laevigata_brown` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 225 | 花生衣红 | T1 / T3 | `peanut_skin_red` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 226 | 葡萄皮红 | T1 / T3 | `grape_skin_red` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 227 | 蓝锭果红 | T1 / T3 | `haskap_red` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 228 | 藻蓝 | T2 / T4 | `phycocyanin` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 229 | 植物炭黑 | T1 / T2 | `vegetable_carbon` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 230 | 紫草红 | T0 / T2 | `lithospermum_red` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 231 | 柑橘黄 | T2 | `citrus_yellow` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 232 | 胭脂树橙 | T0 / T2 | `bixin`、`norbixin` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 233 | 胭脂虫红及其铝色淀 | T1 / T3 | `carminic_acid`、`carmine` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 234 | 叶黄素 | T2 | `lutein` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 235 | 天然胡萝卜素 | T2 | `natural_carotene` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 236 | 核黄素（维生素 B2） | T4 | `riboflavin` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 237 | 杨梅红 | T1 / T3 | `bayberry_red` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 238 | 番茄红 | T2 | `tomato_oleoresin` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 239 | 焦糖色（苛性硫酸盐法） | T2 | `caramel` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 240 | 红曲黄色素 | T3 / T4 | `monascus_yellow` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 241 | 叶绿素铜 | T2 | `copper_chlorophyll` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 242 | 紫甘薯色素 | T1 / T3 | `purple_sweet_potato_color` | [additives/15 §B](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 243 | 叶绿素铜钾盐 | T2 | `potassium_copper_chlorophyllin` | [additives/15 §B](../additives/15-batch-4.md) | 已完成 |
| 244 | 酪蛋白酸钠 | T1 / T2 | `sodium_caseinate` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 245 | 山梨醇酐三硬脂酸酯（司盘 65） | T2 | `sorbitan_tristearate` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 246 | 山梨醇酐单油酸酯（司盘 80） | T3 | `sorbitan_monooleate` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 247 | 木糖醇酐单硬脂酸酯 | T2（37 为 T3） | `xylitan_monostearate` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 248 | 山梨醇酐单棕榈酸酯（司盘 40） | T3 | `sorbitan_monopalmitate` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 249 | 双乙酰酒石酸单双甘油酯 | T3 | `diacetyltartaric_monoglycerides` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 250 | 硬脂酰乳酸钠 | T2 | `sodium_stearoyl_lactylate` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 251 | 聚氧乙烯(20)山梨醇酐单硬脂酸酯（吐温 60） | T3 | `polysorbate_60` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 252 | 辛，癸酸甘油酯 | T3 | `caprylic_capric_triglyceride` | [additives/15 §C](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 253 | 改性大豆磷脂 | T3 | `modified_soy_lecithin` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 254 | 丙二醇脂肪酸酯 | T2 / T4 | `propylene_glycol_monostearate` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 255 | 聚甘油脂肪酸酯 | T2 | `polyglycerol_esters` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 256 | 山梨醇酐单月桂酸酯（司盘 20） | T3 | `sorbitan_monolaurate` | [additives/15 §C](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 257 | 聚氧乙烯(20)山梨醇酐单月桂酸酯（吐温 20） | T3 | `polysorbate_20` | [additives/15 §C](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 258 | 聚氧乙烯(20)山梨醇酐单棕榈酸酯（吐温 40） | T3 | `polysorbate_40` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 259 | 乙酰化单，双甘油脂肪酸酯 | T2 / T3 | `acetylated_monoglycerides` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 260 | 硬脂酸钾 | T1 / T2 | `potassium_stearate` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 261 | 聚甘油蓖麻醇酸酯 | T2 | `polyglycerol_polyricinoleate` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 262 | 辛烯基琥珀酸淀粉钠 | T3 | `starch_sodium_octenylsuccinate` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 263 | 乳酸脂肪酸甘油酯 | T2 | `lactylated_monoglycerides` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 264 | 柠檬酸脂肪酸甘油酯 | T2 | `citrated_monoglycerides` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 265 | 铵磷脂 | T3 | `ammonium_phosphatide` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 266 | 琥珀酸单甘油酯 | T3 | `succinylated_monoglycerides` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 267 | 硬脂酸钙 | T1 / T2 | `calcium_stearate` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 268 | 酶解大豆磷脂 | T2 | `hydrolyzed_lecithin` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 269 | 皂树皮提取物 | T1 / T3 | `quillaja_extract` | [additives/15 §C](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 270 | 达瓦树胶 | T0 / T2 | `ghatti_gum` | [additives/15 §C](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 271 | 紫胶（虫胶） | T1 / T3 | `shellac` | [additives/15 §C](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 272 | 液体石蜡（白油） | T3 | `liquid_paraffin` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 273 | 吗啉脂肪酸盐果蜡 | T3 | `morpholine_fruit_wax` | [additives/15 §C](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 274 | 松香季戊四醇酯 | T3 | `rosin_pentaerythritol_ester` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 275 | 巴西棕榈蜡 | T0 / T2 | `carnauba_wax` | [additives/15 §C](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 276 | 硬脂酸 | T1 / T3 | `stearic_acid` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 277 | 聚乙烯醇 | T3 | `polyvinyl_alcohol` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 278 | 普鲁兰多糖 | T4 | `pullulan` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 279 | 聚乙二醇 | T3 | `polyethylene_glycol` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 280 | 蜂蜡 | T0 / T2 | `beeswax` | [additives/15 §C](../additives/15-batch-4.md) | 已完成 |
| 281 | 乙二胺四乙酸二钠 | T3 | `disodium_edta` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 282 | 柠檬酸亚锡二钠 | T2 | `disodium_stannous_citrate` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 283 | 刺梧桐胶 | T0 | `karaya_gum` | [additives/15 §D](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 284 | α-环状糊精 | T2（393 为 T4） | `alpha_cyclodextrin` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 285 | γ-环状糊精 | T3（393 为 T4） | `gamma_cyclodextrin` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 286 | 聚天冬氨酸钾 | T3（85 为 T4） | `potassium_polyaspartate` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 287 | 海藻酸钾 | T2 | `potassium_alginate` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 288 | 阿拉伯胶 | T0 / T3 | `gum_arabic` | [additives/15 §D](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 289 | 海藻酸丙二醇酯 | T3 | `propylene_glycol_alginate` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 290 | 罗望子多糖胶 | T2 | `tamarind_gum` | [additives/15 §D](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 291 | 羧甲基淀粉钠 | T2（氯乙酸钠为 T3，见 45） | `sodium_carboxymethyl_starch` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 292 | 淀粉磷酸酯钠 | T2 | `sodium_starch_phosphate` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 293 | 乙酰化二淀粉磷酸酯 | T2（乙酸酐为 T3，见 48） | `acetylated_distarch_phosphate` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 294 | 羟丙基二淀粉磷酸酯 | T3 | `hydroxypropyl_distarch_phosphate` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 295 | 磷酸化二淀粉磷酸酯 | T2 | `phosphated_distarch_phosphate` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 296 | 甲壳素（几丁质） | T2 | `chitin` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 297 | 亚麻籽胶 | T0 / T2 | `flaxseed_gum` | [additives/15 §D](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 298 | 田菁胶 | T1 / T2 | `sesbania_gum` | [additives/15 §D](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 299 | 聚葡萄糖 | T3 | `polydextrose` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 300 | 槐豆胶 | T1 / T2 | `locust_bean_gum` | [additives/15 §D](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 301 | β-环状糊精 | T2（393 为 T4） | `beta_cyclodextrin` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 302 | 瓜尔胶 | T1 / T2 | `guar_gum` | [additives/15 §D](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 303 | 脱乙酰甲壳素（壳聚糖） | T2 | `chitosan` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 304 | 羟丙基甲基纤维素 | T3 | `hydroxypropyl_methylcellulose` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 305 | 皂荚糖胶 | T1 / T2 | `gleditsia_gum` | [additives/15 §D](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 306 | 氧化淀粉 | T2（次氯酸钠为 T3） | `oxidized_starch` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 307 | 乙酰化双淀粉己二酸酯 | T2（144、乙酸酐为 T3） | `acetylated_distarch_adipate` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 308 | 氧化羟丙基淀粉 | T3 | `oxidized_hydroxypropyl_starch` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 309 | 磷酸酯双淀粉 | T2 | `distarch_phosphate` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 310 | 聚丙烯酸钠 | T3 | `sodium_polyacrylate` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 311 | 沙蒿胶 | T2 | `artemisia_gum` | [additives/15 §D](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 312 | 刺云实胶 | T1 / T2 | `tara_gum` | [additives/15 §D](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 313 | 可得然胶 | T4 | `curdlan` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 314 | 甲基纤维素 | T3 | `methylcellulose` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 315 | 可溶性大豆多糖 | T3 | `soluble_soybean_polysaccharide` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 316 | 决明胶 | T1 / T2 | `cassia_gum` | [additives/15 §D](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 317 | 海藻酸钙 | T2 | `calcium_alginate` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 318 | 三赞胶 | T4 | `sanzan_gum` | [additives/15 §D](../additives/15-batch-4.md) | 已完成 |
| 319 | D-异抗坏血酸 | T4 | `erythorbic_acid` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 320 | 肌醇六磷酸（植酸） | T3 | `phytic_acid` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 321 | 甘草抗氧化物 | T2 | `licorice_antioxidant` | [additives/15 §E](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 322 | 抗坏血酸钙 | T2（28 为 T4） | `calcium_ascorbate` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 323 | 抗坏血酸棕榈酸酯 | T3（28 为 T4） | `ascorbyl_palmitate` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 324 | 硫代二丙酸二月桂酯 | T3 | `dilauryl_thiodipropionate` | [additives/15 §E](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 325 | 4-己基间苯二酚 | T3 | `hexylresorcinol` | [additives/15 §E](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 326 | D-异抗坏血酸钠 | T4 | `sodium_erythorbate` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 327 | 竹叶抗氧化物 | T2 / T3 | `bamboo_leaf_antioxidant` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 328 | 乙二胺四乙酸二钠钙 | T2（乙二胺四乙酸为 T3，见 281） | `calcium_disodium_edta` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 329 | 茶黄素 | T1 / T3 | `theaflavins` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 330 | 抗坏血酸棕榈酸酯（酶法） | T2（28、102 为 T4） | `ascorbyl_palmitate` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 331 | 植酸钠 | T3 | `sodium_phytate` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 332 | 苯甲酸 | T3 | `benzoic_acid` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 333 | 山梨酸 | T3 | `sorbic_acid` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 334 | 丙酸钠 | T2 | `sodium_propionate` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 335 | 对羟基苯甲酸乙酯 | T3 | `ethyl_paraben` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 336 | 脱氢乙酸、脱氢乙酸钠（醋酸钠） | T2（双乙烯酮为 T3，见 86） | `dehydroacetic_acid`、`sodium_dehydroacetate` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 337 | 乙氧基喹 | T3 | `ethoxyquin` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 338 | 二氧化碳 | T1 / T2 | `carbon_dioxide` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 339 | 联苯醚 | T3 | `diphenyl_ether` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 340 | 稳定态二氧化氯 | T3 | `stabilized_chlorine_dioxide` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 341 | 丙酸 | T2 / T3 | `propionic_acid` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 342 | 单辛酸甘油酯 | T3 / T4 | `glyceryl_monocaprylate` | [additives/15 §E](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 343 | 对羟基苯甲酸甲酯钠 | T3 | `sodium_methyl_paraben` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 344 | 二甲基二碳酸盐 | T3 | `dimethyl_dicarbonate` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 345 | 液体二氧化碳（煤气化法） | T3 | `carbon_dioxide` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 346 | 溶菌酶 | T2 / T3 | `lysozyme` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 347 | 对羟基苯甲酸乙酯钠 | T3 | `sodium_ethyl_paraben` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 348 | ε-聚赖氨酸 | T4 | `epsilon_polylysine` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 349 | ε-聚赖氨酸盐酸盐 | T4 | `epsilon_polylysine_hydrochloride` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 350 | 异麦芽酮糖 | T4 | `isomaltulose` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 351 | 山梨糖醇 | T3 | `sorbitol` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 352 | 甘草酸一钾 | T2 | `monopotassium_glycyrrhizinate` | [additives/15 §E](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 353 | 甘草酸铵 | T2 | `ammonium_glycyrrhizinate` | [additives/15 §E](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 354 | 阿力甜 | T4 | `alitame` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 355 | 乳糖醇 | T3 | `lactitol` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 356 | 罗汉果甜苷 | T0 / T3 | `mogroside` | [additives/15 §E](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 357 | 索马甜 | T4 | `thaumatin` | [additives/15 §E](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 358 | 天门冬酰苯丙氨酸甲酯乙酰磺胺酸 | T2（85 为 T4） | `aspartame_acesulfame` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 359 | 麦芽糖醇液 | T3 | `maltitol_syrup` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 360 | 山梨糖醇液 | T3 | `sorbitol_syrup` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 361 | 环己基氨基磺酸钙 | T2（89 为 T3） | `calcium_cyclamate` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 362 | 甘草酸三钾 | T2 | `tripotassium_glycyrrhizinate` | [additives/15 §E](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 363 | 爱德万甜 | T3（85 为 T4） | `advantame` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 364 | 高锰酸钾 | T2 / T3 | `potassium_permanganate` | [additives/15 §E](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 365 | 异构化乳糖 | T3 | `lactulose_syrup` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 366 | 咖啡因 | T1 / T3 / T4 | `caffeine` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 367 | 辣椒油树脂 | T2 | `paprika_oleoresin` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 368 | 乙酸钠（醋酸钠） | T1 | `sodium_acetate` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 369 | 半乳甘露聚糖 | T3 | `galactomannan` | [additives/15 §E](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 370 | 羟基硬脂精 | T3 | `oxystearin` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 371 | 冰结构蛋白 | T4 | `ice_structuring_protein` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 372 | 硫酸镁（泻盐） | T1 / T2 | `epsom_salt` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 373 | 氮气 | T2 / T3 | `nitrogen` | [additives/15 §E](../additives/15-batch-4.md) | 已完成 |
| 374 | D-阿洛酮糖-3-差向异构酶 | T4 | `enzymes/psicose_epimerase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 375 | α-半乳糖苷酶 | T4 | `enzymes/alpha_galactosidase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 376 | α-乙酰乳酸脱羧酶 | T4 | `enzymes/aldc` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 377 | β-果糖基转移酶 | T4 | `enzymes/beta_fructosyltransferase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 378 | β-葡聚糖酶 | T4 | `enzymes/beta_glucanase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 379 | 阿拉伯呋喃糖苷酶 | T4 | `enzymes/arabinofuranosidase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 380 | 氨基肽酶 | T4 | `enzymes/aminopeptidase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 381 | 半纤维素酶 | T4 | `enzymes/hemicellulase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 382 | 蛋白酶（微生物） | T4 | `enzymes/microbial_protease` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 383 | 蛋白质谷氨酰胺酶 | T4 | `enzymes/protein_glutaminase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 384 | 单宁酶 | T1 / T4 | `enzymes/tannase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 385 | 多聚半乳糖醛酸酶 | T4 | `enzymes/polygalacturonase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 386 | 甘油磷脂胆固醇酰基转移酶 | T4 | `enzymes/gcat` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 387 | 谷氨酰胺酶 | T4 | `enzymes/glutaminase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 388 | 果胶裂解酶 | T4 | `enzymes/pectin_lyase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 389 | 果胶酯酶（果胶甲基酯酶） | T4 | `enzymes/pectinesterase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 390 | 过氧化氢酶 | T2 / T4 | `enzymes/catalase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 391 | 核酸酶 | T4 | `enzymes/nuclease` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 392 | 海藻糖酶 | T4 | `enzymes/trehalase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 393 | 环糊精葡萄糖苷转移酶 | T4 | `enzymes/cgtase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 394 | 己糖氧化酶 | T4 | `enzymes/hexose_oxidase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 395 | 菊糖酶 | T4 | `enzymes/inulinase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 396 | 壳聚糖酶 | T4 | `enzymes/chitosanase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 397 | 磷脂酶 | T2 | `enzymes/phospholipase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 398 | 磷脂酶 A1 | T4 | `enzymes/phospholipase_a1` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 399 | 磷脂酶 A2 | T2 | `enzymes/phospholipase_a2` | [additives/15 §F](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 400 | 磷脂酶 C | T4 | `enzymes/phospholipase_c` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 401 | 磷酸肌醇磷脂酶 C | T4 | `enzymes/pi_plc` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 402 | 麦芽碳水化合物水解酶（α-、β-淀粉酶） | T1 | `enzymes/malt_carbohydrase`、`enzymes/barley_malt` | [additives/15 §F](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 403 | 麦芽糖淀粉酶 | T4 | `enzymes/maltogenic_amylase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 404 | 麦芽四糖水解酶 | T4 | `enzymes/maltotetraohydrolase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 405 | 木聚糖酶 | T4 | `enzymes/xylanase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 406 | 葡糖异构酶（木糖异构酶） | T4 | `enzymes/glucose_isomerase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 407 | β-葡萄糖苷酶 | T4 | `enzymes/beta_glucosidase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 408 | α-葡萄糖苷酶 | T4 | `enzymes/alpha_glucosidase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 409 | 漆酶 | T4 | `enzymes/laccase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 410 | 溶血磷脂酶（磷脂酶 B） | T4 | `enzymes/lysophospholipase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 411 | 羧肽酶 | T4 | `enzymes/carboxypeptidase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 412 | 4-α-糖基转移酶 | T4 | `enzymes/alpha_glucanotransferase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 413 | 天门冬酰胺酶 | T4 | `enzymes/asparaginase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 414 | 脱氨酶 | T4 | `enzymes/deaminase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 415 | 胃蛋白酶 | T2 | `enzymes/pepsin` | [additives/15 §F](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 416 | 无花果蛋白酶 | T1 | `enzymes/ficin` | [additives/15 §F](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 417 | 右旋糖酐酶 | T4 | `enzymes/dextranase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 418 | 胰蛋白酶 | T2 | `enzymes/trypsin` | [additives/15 §F](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 419 | 胰凝乳蛋白酶（糜蛋白酶） | T2 | `enzymes/chymotrypsin` | [additives/15 §F](../additives/15-batch-4.md) | 已完成（原料暂无来源） |
| 420 | 蔗糖 1-果糖转移酶 | T4 | `enzymes/sucrose_fructosyltransferase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 421 | 酯酶 | T4 | `enzymes/esterase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 422 | 植酸酶 | T4 | `enzymes/phytase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |
| 423 | 转葡糖苷酶 | T4 | `enzymes/transglucosidase` | [additives/15 §F](../additives/15-batch-4.md) | 已完成 |

“最早阶段”照抄 15（以工艺为准，括号里是更高档次的上游）。物料 ID 取 09 备注“第四批 N 号”的物料与 08 的酶；两个 ID 的项是色素与它的铝色淀、洋红酸与胭脂虫红铝色淀、酶与酶源。

## 每一项的通用步骤

1. 物料：已在 I29 注册；名称按文本规范（I04），标签按 08（I06）。缺物料时补登 09 / 08 并写回。
2. 配方：按 15 的路线逐条写入数据生成；配方类型与设备按 [machines/01 §2](../machines/01-recipe-types.md)，阶段以工艺为准（[additives/README §阶段（Tier）约定](../additives/README.md)）。
3. 门槛：按 04 §实现约定；需要纯度的输入写纯度原料（[additives/10 §4](../additives/10-purity-jei-ae2.md)）。
4. 检查：JEI 可见、最早阶段能做出、化学式与 09 一致。

## 验收

- [x] 三个节点 `compileJava`、`runData`、`test` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 清单中原料齐全的每一项在“最早阶段”的设备上能做出，并能沿路线升级到最终阶段；纯度报表的“最早可做出的档次”与 15 对照，不一致的写进实现记录
- [x] 数据生成检查全部通过（时长为 4 的倍数、每条配方至少一台设备可执行、带纯度的产出写入纯度、不带纯度的物料不写纯度也不设门槛）
- [x] 15 个工程菌种都能在菌种工作台做出，每次重组消耗 1 个表达载体
- [x] 游戏内抽查：A–F 每组至少两条路线（见实现记录“验证”）

## 实现记录

- 代码：`content.batch4` 六个类，按 15 分组：`Batch4Salts`（A）、`Batch4Colours`（B）、`Batch4Emulsifiers`（C）、`Batch4Thickeners`（D）、`Batch4Others`（E）、`Batch4Enzymes`（F），共 685 条配方；T0 路线（145、230 A、232 A、270 A、275 A、280 A、283、288 A、297 A、356 A）在 `ManualRecipes`；新菌种的分离平板、橘青霉诱变在 `Cultures`；`Batch` 新增共用步骤：喷雾干燥、铝色淀、酶固定化
- 前三批已有的路线不重复写（140、143、145、146、148、166、173、176、177、197、203、235、236 A、238、332、333、341、351、359、360、373 等），只补缺的步骤（精制、另一条路线）
- 用户决定（2026-11-11）：
  - 198 B 氯化：按现实在流化床中氯化，高钛渣与焦炭先在调配机混成“氯化炉料”，在气相反应炉流化床模式中通氯气
  - 254、342、27 C 分子蒸馏：单酯先熔成熔融液，精馏塔分子蒸馏后冷却结晶（新增三种熔融液流体）
  - 218 栀子蓝：按现实做法用大豆蛋白水解物显色（豆渣 + 木瓜蛋白酶，FDA 21 CFR 73.168）
  - 81 / 194：G 酸与 R 酸联产（2-萘酚二磺化，硫酸钾析出 G 酸、硫酸钠析出 R 酸），去掉 I20 的 G 酸配方
  - 酶固定化（P96）：179、330、374、406 各设一个固定化物品（350 已有），固定化在搅拌反应罐里做，固定化物品作催化剂可用 64 次
  - 163：硅油乳液单设物品；沙子可代石英炼硅（来源纯度低）
  - 228 藻蓝只许冷冻干燥；204 三苯基氧膦写回收配方（光气氯化后金属还原，BASF 专利 US 5,527,966；专利首选铝粉，本模组没有铝的标签，用专利同列的铁）
  - 391：野生橘青霉经诱变平板得不产毒株，只有不产毒株产核酸酶；Tooltip 不加提示
- 物料补登（09、08、10）：氯化炉料、大豆蛋白水解物、硅油乳液（K12 复配）、氢化油、熔融单硬脂酸甘油酯 / 丙二醇脂肪酸酯 / 单辛酸甘油酯、四种固定化酶、橘青霉（不产毒株）；聚二甲基硅氧烷改名“聚二甲基硅氧烷（硅油）”
- 改动前三批：I18 的柠檬酸浅盘发酵加副产黑曲霉菌丝体（296 B 的原料），玉米湿磨加副产玉米蛋白粉（214）；I20 的微晶蜡减压蒸馏，馏出液由脱蜡油改为减压馏分油（272 先溶剂脱蜡，得石蜡与脱蜡油）
- 标签：越橘标签补入原版甜浆果（08 原写“标签默认含原版甜浆果”，导出时没有进标签）；08 §C 中暂无来源的作物与矿石（红米、红心萝卜、红苋菜、紫甘薯、钛铁矿石、软锰矿石、大麦、菊芋）写成空标签，配方已可引用，整合包可补内容；导出脚本把全部“暂无来源”原料写进 `materials.json` 的 `pending_sources`
- 纯度报表：用到暂无来源原料的配方单列在“原料暂无来源”一节（77 条），不计入纯度表和“最早可做出的档次”；`purity_report_stages.py` 加上 15 的阶段
- 菌种工作台：新增样本“生肉”（大肠杆菌）、“甜菜根”（红色精朊杆菌）；供体槽加入土壤、堆肥、稻草、树叶、水生植物样本，卡拉胶红藻，黑曲霉；表达载体由大肠杆菌 + 琼脂在基因重组模式下挑菌落得到（每块平板 1 个）
- 验证：三个节点数据生成与单元测试通过；1.20.1 服务器游戏内抽查（开发命令装料运行）：
  - A：175 葡萄糖酸亚铁（通氮）、169 硫酸铝铵、151 磷酸三钾
  - B：200 氧化铁黑（通空气）、218 大豆蛋白水解物（木瓜蛋白酶）与栀子蓝、208 越橘红（浸提桶，甜浆果 + 柠檬酸；抽查时发现越橘标签缺甜浆果，已补）
  - C：267 硬脂酸钙、250 硬脂酰乳酸钠（通氮）、252 月桂酸冷却结晶
  - D：309 磷酸酯双淀粉、287 海藻酸钾
  - E：322 抗坏血酸钙、334 丙酸钠
  - F：385 分级（多聚半乳糖醛酸酶 + 果胶裂解酶）、330 固定化脂肪酶、表达载体平板、枯草芽孢杆菌 + 稻草 + 表达载体的重组平板长出工程菌（D-阿洛酮糖-3-差向异构酶）与宿主，载体消耗 1 个
- 报表与 15 对照：
  - 一致或只差上游档次（括号中的上游）的占多数
  - 比 15 早的：156 微晶纤维素 T2（竹浆纤维素可在 T1 得到）；240 红曲黄、285 γ-环状糊精、320 植酸、331 植酸钠 T2（固体的树脂纯化按约定在 T2 搅拌反应罐里做）；350 异麦芽酮糖、365 异构化乳糖 T2
  - 达不到食品级的（K5 天然提取物食品级界限 90%，油树脂形态没有精制步骤）：209 辣椒橙、214 玉米黄、231 柑橘黄；另有 261 聚甘油蓖麻醇酸酯、370 羟基硬脂精（只有来源值）
  - 报表的已知局限：不带纯度的催化剂（酶、固定化酶）不计入“最早档次”，所以 284、301 环状糊精显示 T3、350 显示 T2，实际要等 T4 的工程菌；黑曲霉只能在 T4 菌种工作台分离，296、303 显示 T4
- 2026-11-12 审定（11 问题 43）：
  - 副产物与主产物同一配方：葡萄 4 → 葡萄汁 3 + 葡萄皮渣 1（压榨）；柑橘 4 → 柑橘汁 2 + 柑橘皮 1（压榨）；可可豆 4 → 可可碎粒 3 + 可可豆壳 1（研磨）；花生 4 → 花生油 2 + 花生红衣 1（压榨）；红米 4 → 大米 3 + 红米糠 1、稻谷 4 → 大米 3 + 米糠 1（研磨）
  - 稻谷作物 `paddy`（作物、野生稻谷、种子、世界生成；原 `rice` 作物改名），`materials/rice` 只作大米
  - 吐温 60、40 产出改为 500 mB；甘草渣由 352 甘草浸提联产 3 件，321 用甘草渣 4 件
  - 210、225、226 的树脂纯化在 T2 搅拌反应罐（`resin` 写法本就是 T2）
  - 间苯二酚拆成碱熔与酸化两条（11 问题 42，同 2-萘酚）：苯二磺酸是流体，回转窑没有流体输入，碱熔改在 M23 高温（苯二磺酸 1 桶 + 氢氧化钠 6 件 → 间苯二酚二钠 1 件 + 亚硫酸钠 2 件），间苯二酚二钠 + 水 + 硫酸在 M14 酸化（T2）
  - 辣椒油树脂改为不带纯度（11 问题 42）：367、60 B 三条浸提去掉来源纯度，209 的引用不设门槛，无需改
- 2026-11-12 审定（二）（11 问题 44）：间苯二酚的碱熔配方条件为“高温”，只有 M23 高压反应釜做得了，本来就在 M23（上面原写“M14 高温”是笔误，已改）；副产物比例照准，不改
- 2026-11-12（用户定：压榨的果汁、花生油出流体）：螺旋压榨机（M02）只有一个物品输出，原写的果汁、花生油成品与皮渣两种物品只有 T2 固液分离机做得了，葡萄皮红、花生衣红因此推迟到 T2。改为压榨出流体：葡萄 4 → 葡萄原汁 750 mB + 葡萄皮渣 1，柑橘 4 → 柑橘原汁 500 mB + 柑橘皮 1，花生 4 → 花生原油 500 mB + 花生红衣 1（比例照 4 → 3 + 1、4 → 2 + 1，以 1/4 桶计，同甜菜根 4 个压 1 桶原汁），都回到 T1；三种流体登记在 08 §C（`raw_grape_juice`、`raw_citrus_juice`、`crude_peanut_oil`，原料，不带纯度）。成品葡萄汁、柑橘汁、花生油由调配机（M21，T2）装瓶：250 mB + 玻璃瓶 → 1 瓶；玻璃瓶标签 `materials/glass_bottle` 补在 08 §A。1.20.1 服务器试过：螺旋压榨机做葡萄、柑橘、花生，调配机装瓶

## 2026-11-12 审定（11 问题 43）

设计侧审完本计划的待审十一条（[additives/11 问题 43](../additives/11-roadmap.md)）。照准的：实装中补的五种物料与做法（氢化油、芒硝加煤还原制硫化钠、矿石研磨得二氧化锰与菱镁矿粉、液化淀粉喷雾干燥得麦芽糊精、蒸馏水）；分级（P95）改在搅拌反应罐用离子交换树脂；基因重组的供体按生境；天然色素水溶线 T1 用水、T2 用稀乙醇；硅油乳液略去吐温 80；动物脂与椰子油脂肪酸的分离改法（现实中硬脂酸与棕榈酸沸点太近，溶剂冷却结晶才是实际做法）；略去溶液副产盐与咖啡因 A 的生石灰；236 B 直接用枯草芽孢杆菌、240 B 不写。要改的：

- [x] 副产物配方改为同时产出主副产物（[additives/15](../additives/15-batch-4.md) 各号与“新增原料”表已改）：葡萄 → 葡萄汁 + 葡萄皮渣；柑橘 → 柑橘汁 + 柑橘皮；可可豆 → 可可碎粒 + 可可豆壳；花生 → 花生油 + 花生红衣（脱红衣并入压榨）；稻谷 → 大米 + 米糠；红米 → 大米 + 红米糠。葡萄汁、柑橘汁是 [additives/12](../additives/12-finished-products.md) 新增的成品（[I21](I21-finished-products.md)）
- [x] 稻谷与大米分开：新增作物 `materials/paddy`（稻谷，08 §C、I07；不论是否装农夫乐事都生成），`materials/rice` 只作大米；碾米的产物是大米，不是稻谷
- [x] 吐温 60、40 按方程式减半：司盘 1 件 + 10 份环氧乙烷 → 0.5 份吐温（环氧乙烷与产物之比与方程式一致；固体司盘不能取半，整件消耗，多耗一半，作游戏简化写在 15 第 251 号）
- [x] 321 甘草抗氧化物改回甘草渣：352 的热水浸提同一配方联产 `materials/licorice_residue`（08 §C 新增），321 用它，不用甘草根
- [x] 树脂纯化（210、225、226）由 T3 改在 T2 搅拌反应罐，15 已改
- [x] 钴盐改名乙酸钴（见 [I05](I05-materials-registry.md)），本计划引用处随之改

## 2026-11-12 审定（二）（11 问题 44）

- [x] 间苯二酚（195）的碱熔放在 M23 高温，不是 M14：搅拌反应罐只有加热档，没有高温档（[additives/06](../additives/06-machines.md)）；间苯二磺酸是溶液、回转窑不收流体的判断没错，设备换成高压反应釜即可，与第五批 ST38 蓖麻油同一处理。[additives/05](../additives/05-processes.md) P70 与 [additives/15](../additives/15-batch-4.md) 第 195 号已改
- [x] 副产物配方的比例照准（葡萄、可可豆、稻谷、红米 4 → 3 + 1，柑橘、花生 4 → 2 + 1）：现实中豆壳约 12%、米糠约 8%、花生红衣约 3%，比这低得多，但那样副产少到难用，按游戏设定取整。[additives/04 §实现约定](../additives/04-production-routes.md) 已把原来“只出副产”的约定改成联产并记下比例

## 待审（实现时拟定，请确认）

2026-11-12 的两条已审定（11 问题 44），处理见上节。压榨改出流体（用户定）后实装拟定的：

- 三种流体的 ID、名称与颜色：葡萄原汁 `raw_grape_juice`（#6A1E3A）、柑橘原汁 `raw_citrus_juice`（#F2B33D）、花生原油 `crude_peanut_oil`（#E8C25A），与甜菜原汁同写在 08 §C
- 果汁、花生油的量按 1/4 桶折算（葡萄 4 → 750 mB、柑橘与花生 4 → 500 mB）
- 装瓶在调配机（M21，T2）的配料模式，每瓶 250 mB，玻璃瓶走新标签 `materials/glass_bottle`；成品的制作方式本来留到成品配方设计（12 开头），这里先给这三种

## 待定

- 15 §存疑中未问的事项维持 15 的写法：159 硅藻土作反应物（催化剂类别，不带纯度，可作输入）、163 消泡硅膏、167 锌无后备来源、174 不加还原剂、175 葡萄糖酸钙代钡、180 B 不加乌洛托品、181 B 硫源写硫酸铵、204 C10 二醛上游未展开、椰子与油棕等作物来源、动物原料掉落
- 第五批（I31）没有设计文档（16 只有清单），阻塞
