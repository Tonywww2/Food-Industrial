# 18 第五批：食品用香料（模板与天然香料）

GB 2760-2024 附录 B 的食品用香料共 1,892 条：表 B.2 天然香料 388 条（N001–N404）、表 B.3 合成香料 1,504 条（S0001–S1506）。前四批与物料表已有 47 条（★15、○31、近似 1，见 [16](16-batch-5-backlog.md) 第 1 节），本批设计其余 **1,845 条**：天然 385 条（本文第 2 节）、合成 1,460 条（[19](19-batch-5-synthetic-flavors.md)）。非香料部分（424–584）见 [17](17-batch-5.md)。

- **编号**：香料不另编号，用国标编码作编号（N###、S####）。与加工助剂、营养强化剂、胶基名单重叠的 7 条只设计一次，香料行写“见 4xx”（ST26）；本批另有 35 条经核对其实已有物料，同样写 ST26。
- **物料**：每条一个物料，ID、化学式、英文名、状态、纯度类别并入 [09](09-chemical-formulas.md) 第 E 节，“分类”记为**香料**（单独的创造模式标签页）。单一化合物固体 K3、液体 K4；精油、浸膏、净油、酊、油树脂、香脂、馏分、反应香精等多组分天然混合物不带纯度（10 第 1.1 节）；按主成分含量计价的纯化提取物为 K5。
- **路线**按模板写：一行 = 模板 + 原料，模板见第 1 节。原料一栏：其他香料写编码，已有添加剂写编号，09 物料与新中间体写 ID，原料写标签。
- **阶段**写本步工序所需设备的档次；上游原料要更高档次时在括号内注明，如“T2（S0318 为 T3）”。括号内的档次按前体关系图推算（全部 2,287 个节点无循环、无断链）；I31 的“最早阶段”取两者较高者。
- **核对**：单一化合物 1,456 条均由 SMILES 经 RDKit 反算元素组成，与化学式一致；关键反应全部配平（eqcheck bad 0）。
- **名称**取表 B.2、B.3 中文名：“又名”括号里是常用短名的写作日常名称（如 十二酸（月桂酸）），是系统名的删去（文本规范第 2 节）；原表文字层丢字、错位的已对照 PDF 改正，记在存疑。
- **缺原料时补原料本身**：植物原料登记植物部位（花、叶、籽、根、树脂），没有获取途径的写“暂无来源”，与第四批相同（08 第 C 节）。
- 路线中“未确认”表示没有找到可核对的原文，“游戏简化”表示为玩法做的简化。各组的存疑逐条列在第 6 节（合成香料见 19 第 5 节）；设计者的决定（2026-11-11）汇总在 [17](17-batch-5.md) 第 9 节与 [11](11-roadmap.md) 问题 29–41。

## 1 路线模板

一行路线只写“模板 + 原料”。模板 NT1–NT11、ST01–ST26 为设计前拟定，其余由各组提出、汇总时合并编号（多组提出的同类模板合为一个，如酮式脱羧、维蒂希烯化、MPV 还原）。

### 1.1 通用约定

- “蒸馏”一步：T1–T2 在 M08 蒸馏甑只取主馏分，T3 起在 M22 精馏塔分馏；两者同属 R10，纯度按设备档次给（../machines/01 第 1.1 节），所以同一条路线 T3 做出的纯度更高。模板的阶段不因蒸馏而提高；ST50、ST51、ST55 必须 M22 分馏，阶段为 T3。
- 模板阶段是下限：路线中其他工序、催化剂或前体要更高档次时取高者，括号注明，如“T2（S0318 为 T3）”。
- 有毒试剂（氢氰酸、硫化氢、硫酸二甲酯、光气等）只写试剂名，不写操作条件；可选路线全部开启（11 问题 5）。
- 同一品种有天然单离与化学合成两种做法时写 A、B 两条。
- 同一工序本来就同时得到的多个产物（联产：如羰基合成的正丁醛与异丁醛、松节油精馏得到的几种蒎烯、同一精油精馏分出的几种单体、同一干馏液的几个馏分）写成一条多产出配方；靠催化剂、模式区分的（如部分加氢与完全加氢）各写一条（2026-11-11，11 问题 34）。配比不是区分手段（2026-11-12 更正，11 问题 41，见第 1.5 节）。

### 1.2 天然香料（NT）

| 模板 | 名称 | 工序（机器、工艺） | 阶段 |
| --- | --- | --- | --- |
| NT1 | 精油·水蒸气蒸馏 | 原料 → M11 粉碎（籽、根、木、树皮）→ M08 蒸馏甑水蒸气蒸馏（P18，精油分层器模块）→ 油水分层 = 精油；馏出水为花水（副产） | T1 |
| NT2 | 精油·冷榨 | 柑橘果皮 → M02 螺旋压榨（P03）→ M12 离心分油 = 冷榨精油 | T1 |
| NT3 | 酊 | 原料 → M04 浸提桶用乙醇（酒精）室温浸渍（P21）→ M12 过滤 = 酊（乙醇溶液） | T1 |
| NT4 | 浸膏 | 原料 → M18 萃取罐溶剂浸提（正己烷、石油醚或乙醇，P21）→ 回收溶剂 = 浸膏 | T2 |
| NT5 | 净油 | 浸膏（NT4）→ M14 乙醇溶解 → M15 冷冻析蜡 → M12 过滤 → M18 回收乙醇 = 净油 | T2 |
| NT6 | 油树脂 | 香辛料干品 → M11 粉碎 → M18 溶剂浸提（丙酮、乙醇或正己烷）→ 回收溶剂 = 油树脂；T4 可用超临界二氧化碳（P62） | T2 |
| NT7 | 提取物（水或稀醇） | 原料 → M04 / M18 水或稀乙醇浸提（P21）→ M12 过滤 → M15 真空浓缩（P11）= 浓缩液；要粉末时 M28 喷雾干燥（P14，T3） | T1（浓缩液）/ T3（粉末） |
| NT8 | 树脂、香脂、树胶 | 树木分泌物（原料）→ M03 加热熔化或乙醇溶解 → M12 过滤除杂；树胶用水溶解过滤 | T1 |
| NT9 | 馏分与单离 | 精油（NT1 / NT2）→ 蒸馏（P17）→ 萜烯馏分、去萜油或单体；结晶性单体再 M15 冷却结晶（P12） | T2 |
| NT10 | 发酵、酶解来源 | 原料经 M29 发酵或酶解（P56）→ 分离 | T4 |
| NT11 | 干馏、烟熏 | 木材干馏（P32，M07 / M20）→ 冷凝液 → 沉降去焦油 → 精制 = 烟熏液 | T1 |
| NT12 | 精油·浸渍酶解后蒸馏 | 原料 → M11 粉碎 → M04 加温水浸渍，原料自带的酶水解香气前体（冬青苷 → 水杨酸甲酯；蒜氨酸类 → 硫代亚磺酸酯，蒸馏时转为二硫醚；芥子油苷 → 异硫氰酸酯）→ M08 水蒸气蒸馏（P18）→ 油水分层 = 精油。含氰苷的种仁（苦杏仁等）先 M02 压榨脱脂，蒸馏后在 M04 用熟石灰、硫酸亚铁除去氢氰酸 | T1 |
| NT13 | 发酵蒸馏物 | 原料 + 菌种 → M05 密闭发酵（P54）→ M08 蒸馏（P16）收馏出液 = 蒸馏物（稀水溶液） | T1 |
| NT14 | 干品粉末 | 原料 → M10 烘干（P14）→ M01 / M11 粉碎、筛分（P01、P02）= 香料粉 | T1 |
| NT15 | 油脂水解取酸 | 同 276 A：油脂 + 氢氧化钠（烧碱）→ H03 / M14 皂化（P41）→ 稀硫酸酸化（P39）→ 脂肪酸上浮、水洗 = 脂肪酸混合物；母液回收丙三醇（甘油） | T1 |
| NT16 | 多糖酸水解单离 | 多糖原料 → M14 稀硫酸水解（P39）→ 熟石灰中和（P37）→ M12 过滤除硫酸钙 → M14 活性炭脱色（P24）+ M12 过滤 → M15 浓缩、冷却结晶（P11、P12）；T3 可加 M25 离子交换除盐（P25） | T2 |
| NT17 | 生物碱提取成盐 | 树皮粉 + 熟石灰（石灰乳）→ M18 热甲苯浸提（P21）→ M18 稀酸反萃（P22）→ M14 活性炭脱色（P24）+ M12 过滤 → M15 冷却结晶（P12）= 生物碱盐；甲苯回收 | T2 |
| NT18 | 果汁香气回收 | 鲜果 → M02 压榨取汁（P03）→ M15 浓缩（P11），收集最初 10%–20% 蒸发量的冷凝液 → 蒸馏提浓（P17，约 100–150 倍）= 高倍香精；浓缩果汁为副产 | T2 |

### 1.3 合成香料（ST）

| 模板 | 名称 | 工序（机器、工艺） | 阶段 | 通式 |
| --- | --- | --- | --- | --- |
| ST01 | 酯化 | 酸 + 醇 → M14（硫酸或离子交换树脂催化，P42，带分水）→ 中和水洗 → 蒸馏（P17） | T2 | RCOOH + R'OH → RCOOR' + H2O |
| ST02 | 酸酐酰化 | 醇、酚 + 酸酐（乙酸酐 `acetic_anhydride` 等）→ M14 → 水洗 → 蒸馏；副产酸回收 | T3 | ROH + (R'CO)2O → R'COOR + R'COOH |
| ST03 | 酯交换 | 低级醇酯 + 醇 → M14（甲醇钠催化）→ 蒸出低级醇 → 蒸馏 | T3 | — |
| ST04 | 脱氢、氧化 | 醇 → M27 气相催化脱氢（`catalysts/copper`）或 M14 化学氧化（P45）→ 醛、酮；醛 M14 空气氧化（P44）→ 酸 → 蒸馏 | T3 | — |
| ST05 | 催化加氢 | 醛、酮、不饱和物 + 氢气 → M23 加压（`catalysts/nickel` 或 `catalysts/palladium`，P46）→ 蒸馏 | T3 | — |
| ST06 | 缩合 | 羟醛、克莱森–施密特、克脑文格尔、达森、珀金缩合：醛 / 酮 + 醛 / 酮 / 活泼亚甲基 → M14（碱或酸催化，P43）→ 脱水 → 蒸馏；需要时酸催化环化（如紫罗兰酮） | T3 | — |
| ST07 | 缩醛、缩酮 | 醛、酮 + 醇或二醇 → M14（酸催化，带分水，P50）→ 中和 → 蒸馏 | T2 | RCHO + 2 R'OH → RCH(OR')2 + H2O |
| ST08 | 内酯化 | 羟基酸 → M14 酸催化环化（P43）→ 蒸馏；或醇 + 丙烯酸酯自由基加成后环化（γ-内酯） | T3 | — |
| ST09 | 醚化 | 酚钠、醇钠 + 卤代烃或硫酸二甲酯 → M14（P58）→ 水洗 → 蒸馏；或醇酸催化脱水成醚 | T3 | — |
| ST10 | 含硫化合物 | 卤代烃 / 烯烃 + 硫氢化钠或硫化氢（S1010）→ 硫醇（M14 / M23）；醇 + 硫化氢 M27 气相催化；酮先成硫酮再还原；硫醇温和氧化 → 二硫醚；硫醇 + 卤代烃 → 硫醚；硫醇 + 酸酐或酰氯 → 硫酯 | T3 | — |
| ST11 | 含氮、含硫杂环 | 二羰基化合物 + 二胺 / 氨 → M14 缩合 → 脱氢（吡嗪、吡啶）；α-卤代酮 + 硫代酰胺 → 噻唑，或酮 + 硫 + 氨（阿辛格反应）→ 噻唑啉 → 脱氢；吡咯、噻吩同类 | T3 | — |
| ST12 | 美拉德反应分离 | 糖 + 氨基酸（或含硫氨基酸）→ M14 加热（P43）→ 反应香精 → M18 萃取 → 蒸馏取单体 | T2 | — |
| ST13 | 精油单离 | 含该成分的天然精油（N 编码或 NT1 原料）→ 蒸馏（P17）→ 单体；必要时化学法分离（亚硫酸氢盐加成、硼酸酯）后再蒸馏 | T2 | — |
| ST14 | 萜烯转化 | 松节油 α-蒎烯、柠檬烯（`limonene`）等 → M14 酸催化异构化、水合、酯化或 M23 加氢 → 蒸馏 | T3 | — |
| ST15 | 傅–克反应 | 芳烃 + 酸酐 / 酰氯 / 烯烃 → M14（`catalysts/aluminium_chloride` 或酸催化，P58）→ 水解 → 蒸馏 | T3 | — |
| ST16 | 格氏加成 | 卤代烃 + 镁 → 格氏试剂（M14 惰性气氛，乙醚）→ 与醛、酮、酯加成 → 水解 → 蒸馏 | T3 | — |
| ST17 | 成盐 | 酸 + 碱（P37，M14）→ M15 结晶 → M17 干燥 | T2 | — |
| ST18 | 发酵、生物转化 | 糖或前体 → M29 无菌发酵（写菌种）或固定化酶转化 → 萃取 → 蒸馏 / 结晶 | T4 | — |
| ST19 | 羰基合成 | 烯烃 + 一氧化碳 + 氢气 → M23 加压（`catalysts/cobalt` 或 `catalysts/carbonylation`）→ 醛 → 蒸馏 | T3 | RCH=CH2 + CO + H2 → RCH2CH2CHO |
| ST20 | 酰胺化 | 酸（或酰氯）+ 胺 → M14 → M15 结晶；也用于里特反应（腈 + 叔醇） | T3 | — |
| ST21 | 天然非挥发物提取 | 植物原料 → 浸提（P21）→ M25 吸附 / M15 结晶纯化 | T2（M25 为 T3） | — |
| ST22 | 配制混合物 | 已有组分按比例 → M21 调配（P05） | T2 | — |
| ST23 | 卤代 | 醇 + 氢卤酸（或卤化磷、氯化亚砜）→ M14 → 卤代烃（中间体） | T2 | ROH + HX → RX + H2O |
| ST24 | 过氧化 | 拜耳–维利格氧化（酮 → 酯、内酯）、烯烃环氧化：过氧化氢 / 过氧酸 → M14 | T3 | — |
| ST25 | 加成 | 烯烃水合、迈克尔加成、硫醇对不饱和羰基的加成 → M14 | T3 | — |
| ST26 | 已有 | 只引用已有编号或 09 物料，不另设计 | — | — |
| ST27 | 酮式脱羧 | 两种羧酸（其中之一过量，得不对称酮）或单一羧酸（得对称酮）→ M27 气相催化（P82，`manganese_dioxide` 等金属氧化物）→ 冷凝分水 → 蒸馏；二元酸分子内脱羧成环酮（加少量氢氧化钡，M20 缩合模式）；T2 可用钙盐 M20 干馏（P32） | T3 | — |
| ST28 | C-烷基化（含丙二酸酯合成） | 活泼亚甲基或甲基化合物（丙二酸二乙酯 S0403、腈、甲基吡啶、甲基吡嗪、经吗啉烯胺活化的酮）+ 强碱（醇钠，或 `sodium` + `ammonia` 制氨基钠）+ 卤代烃 → M14（惰性）→ 需要时碱水解、酸化加热脱羧（P43）→ 蒸馏 | T3 | — |
| ST29 | 维蒂希烯化 | 卤代烃 + `triphenylphosphine` → M14 成鏻盐 → 加碱（`sodium_methoxide` 等，惰性气氛）成叶立德 → 加醛（非稳定叶立德以顺式烯烃为主，P58）→ M12 滤出三苯基氧膦（`triphenylphosphine_oxide`，回收）→ 蒸馏 | T3 | — |
| ST30 | 消除、脱水、异构化 | 卤代烃 + 强碱 → 烯、炔；醇 + 酸或 M27 气相（`catalysts/alumina`）脱水 → 烯；环氧化物开环成烯丙醇；双键移位（M14 酸或碱催化，或氢氧化钾高温使烯丙基芳烃 → 丙烯基芳烃）；缩醛、乙酸酯热消除（M27 裂解模式）→ 蒸馏，未转化的异构体回流 | T3（碱催化移位 T2） | — |
| ST31 | 选择性羰基还原（MPV） | 不饱和醛、酮 + 异丙醇（S0003）→ M14 加热（`aluminium_isopropoxide` 催化，P67），边反应边蒸出丙酮 → 稀硫酸分解铝盐 → 水洗 → 蒸馏；只还原羰基，碳碳双键保留 | T3 | — |
| ST32 | 酰氯酰化 | 羧酸 + `thionyl_chloride` → M14（耐腐蚀内衬，P59）得酰氯，尾气二氧化硫、氯化氢进 M19 吸收 → 同罐加醇、酚、叔醇、硫醇或芳烃（芳烃按 ST15）→ 碱吸收氯化氢 → 水洗 → 蒸馏或 M15 结晶；胺的酰化归 ST20 | T3 | — |
| ST33 | 胺化 | 醇 + `ammonia` + `hydrogen` → M27 气相催化（`catalysts/nickel`）；醛、酮 + 氨或伯胺 + 氢气 → M23 还原胺化（P46）；卤代烃、活化芳卤 + 过量氨 → M23 氨解；中和 → 蒸馏分出伯胺（仲胺、叔胺回用） | T3 | — |
| ST34 | 气相催化胺化、环化 | 醇、二醇、氨基醇 + 氨、二胺或芳胺 → M27 气相催化（P82，`catalysts/alumina`；醇胺化时加氢气与 `catalysts/nickel`）→ 冷凝 → 蒸馏；二胺 + 二醇环化脱氢得吡嗪，苯胺 + 乙二醇得吲哚 | T3 | — |
| ST35 | 杂芳环自由基酰化、烷基化（米尼西反应） | 缺电子杂芳环（吡嗪、吡啶、噻唑）+ 醛（酰化：`hydrogen_peroxide` + `ferrous_sulfate`）或羧酸（烷基化：`ammonium_persulfate` + `catalysts/silver`）→ M14 稀硫酸中反应（P45）→ 碱中和 → M18 萃取（P22）→ 蒸馏分出位置异构体 | T3 | — |
| ST36 | 水解 | 酯、卤代烃、腈、芳基甲醚 + 水 → M14 酸催化（P39）或碱水解 / 皂化后酸化（P41；芳基甲醚用氢氧化钾高温选择性脱甲基）→ M15 结晶或蒸馏 | T2 | — |
| ST37 | 克莱森重排 | 烯丙醇 + 醛或乙烯基醚 → M14 酸催化成烯丙基乙烯基醚（必要时 M27 裂解模式脱醇）→ M14 加热 [3,3] 重排 → γ,δ-不饱和醛、酮；烯丙基芳基醚加热 → 邻烯丙基酚 → 蒸馏 | T3 | — |
| ST38 | 蓖麻油碱裂解 | 蓖麻油（`castor_oil`）或蓖麻油酸 + 氢氧化钠 → M23 高温（P70，250–280 °C；蓖麻油是流体，不进回转窑，2026-11-12），放出氢气，馏出 2-辛醇（含少量 2-辛酮）→ 蒸馏；釜残水溶、硫酸酸化（P39）→ M15 冷却结晶 = 癸二酸（`sebacic_acid`），母液回收丙三醇（甘油） | T3 | — |
| ST39 | 热裂解 | 原料 → M27 裂解模式（P57，400–600 °C 短停留）→ 急冷冷凝 → 蒸馏；蓖麻油酸（甲酯）裂解得庚醛与 10-十一碳烯酸，β-蒎烯开环得月桂烯，α-蒎烯得罗勒烯，环状缩醛裂解成二烯 | T3 | — |
| ST40 | 脂肪酸加氢制醇 | 脂肪酸（或其甲酯）+ 氢气 → M23 高压（`catalysts/copper`，P46）→ 冷却分水 → 蒸馏；碳碳双键同时饱和 | T3 | — |
| ST41 | 炔化物加成与部分加氢 | 同 P101：乙炔（`acetylene`）+ 钠（液氨介质，M14 惰性、冷却）→ 炔化钠 → 卤代烃烷基化得 1-炔烃 → 再成炔化钠，与环氧乙烷、醛或酮加成 → 炔醇 → M23 部分加氢（`catalysts/palladium`，限量通氢，P46）→ 顺式烯醇 → 蒸馏 | T3 | — |
| ST42 | 光敏氧化 | 烯醇 + 氧气 → M14 紫外灯模块（P74，光敏剂，冷却）→ 烯丙基氢过氧化物 → 亚硫酸钠还原（P67）→ 二醇 → 稀硫酸脱水或环化（P43）→ 蒸馏 | T4 | — |
| ST43 | β-卤代环醚还原开环（博德反应） | β-卤代环醚 + 金属钠（`sodium`）→ M14（惰性，乙醚）开环成烯醇钠 → 加水 → 分层 → 蒸馏 | T3 | — |
| ST44 | 酚气相邻位甲基化 | 酚 + 甲醇 → M27 气相催化（P82，固体酸或氧化镁）→ 冷凝分水 → 蒸馏分出邻位甲基化酚（副产甲酚、三甲酚回用） | T3 | — |
| ST45 | 芳香酸脱羧 | 芳香羧酸或肉桂酸类 → M14 加热（必要时加铜盐或碱），放出二氧化碳 → 减压蒸馏或 M15 结晶；生物法改用 ST18（脱羧酶菌体） | T2 | — |
| ST46 | 糖类酸催化脱水 | 糖或戊聚糖 + 稀盐酸 / 硫酸 → M14 加热（P39、P43），需要还原时加氯化亚锡 → M08 水蒸气带出产物（P18）→ M18 萃取 → 蒸馏 | T2 | — |
| ST47 | 磺化碱熔 | 芳烃 + 硫酸 → M14 耐腐蚀内衬磺化（P59）→ 中和成钠盐 → M20 碱熔模式（P70）→ 水溶、酸化 → 蒸馏 | T3 | — |
| ST48 | 乙酰氧基化 | 烯烃 + 乙酸 + 氧气 → M27 气相催化（`catalysts/palladium`，P82）→ 冷凝 → 水洗分出乙酸 → 蒸馏；同乙酸乙烯酯（134）前段，乙烯换为丙烯 | T3 | — |
| ST49 | 臭氧化断链 | 不饱和油脂、烯烃 + 氧气 → M14（紫外灯模块现场生成臭氧，低温夹套）→ 臭氧化物还原（ST05）→ 醛、醇、ω-羟基酸 → M15 / 蒸馏 | T4 | — |
| ST50 | 萜烯阳离子聚合 | 萜烯 → M14 聚合模式（P77，`catalysts/aluminium_chloride`，甲苯溶剂，低温）→ 水洗除催化剂 → M22 汽提溶剂与低聚物 → 树脂 | T3 | — |
| ST51 | 煤焦油馏分单离 | `coal_tar` → M22 切馏分（轻油、酚油、萘油、洗油）→ M18 稀硫酸萃取吡啶碱、氢氧化钠溶液萃取酚类（P22）→ 中和析出 → 蒸馏；吲哚、甲基萘按沸点切取，需要时 M15 冷却结晶 | T3 | — |
| ST52 | 狄尔斯–阿尔德加成 | 共轭二烯 + 亲二烯体（不饱和醛、酮）→ M23 加热加压（可加 `catalysts/aluminium_chloride`）→ 蒸馏 | T3 | — |
| ST53 | 异硫氰酸酯 | A 化学法（T3）：伯胺 + 二硫化碳（`carbon_disulfide`）+ 氢氧化钠 → M14 二硫代氨基甲酸盐 → 过氧化氢脱硫（P45）→ M12 滤除硫 → 蒸馏；B 酶解法（T1）：同 NT12，含芥子油苷的种子粉碎加水，自身黑芥子酶水解后 M08 水蒸气蒸馏 | A T3；B T1 | — |
| ST54 | 直接氯化 | 有机物 + 氯气（`chlorine`）：烯烃高温取代氯化、芳杂环氯化走 M27 气相催化模式；醛、酮、羧酸 α 位氯化走 M14 耐腐蚀内衬（羧酸加少量 `phosphorus_trichloride`，P59）→ 尾气氯化氢经 M19 吸收为盐酸 → 蒸馏 | T3 | — |
| ST55 | 油脂水解取单一脂肪酸 | 油料 → M02 压榨（P03）→ 植物油 → M23 高温高压水解（P39）→ 甘油水（回收 `glycerol`）+ 混合脂肪酸 → 尿素包合、冷冻结晶（P102）除去饱和酸与油酸 → M22 减压精馏 | T3 | — |
| ST56 | 黄鸣龙还原 | 酮 + 水合肼（`hydrazine_hydrate`）+ 氢氧化钾 → M14 高沸点二醇（`ethylene_glycol`）中回流成腙 → 蒸出水后升温分解放氮 → 水洗 → 蒸馏；羰基还原为亚甲基 | T3 | — |
| ST57 | 环丙烷化 | A 烯烃 + 重氮乙酸乙酯（由 `glycine_ethyl_ester` + `sodium_nitrite` + `hydrochloric_acid` 现制现用）→ M14（铜盐催化）→ 环丙烷甲酸酯；B γ-卤代酯 + 醇钠 → M14 分子内闭环；两者再皂化 → 酸化 → 蒸馏 | T3 | — |
| ST58 | 卡罗尔反应 | 叔烯丙醇（芳樟醇等）+ 乙酰乙酸乙酯（S0365）或双乙烯酮 → M14 加热（少量醇铝催化）→ 酯交换后 [3,3] 重排、脱羧 → γ,δ-不饱和甲基酮 → 蒸馏 | T3 | — |
| ST59 | 乙内酰脲法氨基酸合成（布赫雷尔–贝格斯） | 醛 + 氢氰酸 + 碳酸铵 → M23 加压 → 乙内酰脲 → 碱水解 → 中和结晶 = DL-氨基酸（只写试剂，不展开条件） | T3 | — |
| ST60 | 烯烃复分解 | 两种烯烃（或环烯烃开环）→ M14 惰性气氛（`catalysts/metathesis`，T4 催化剂）→ 放出乙烯 → 蒸馏 | T4 | — |
| ST61 | 科尔贝–施密特羧化 | 酚钠 + 二氧化碳 → M23 加压加热 → 水杨酸类钠盐 → 酸化结晶（M15） | T3 | — |

各模板的使用次数（路线表与中间体合计）：NT1 126、NT2 14、NT3 33、NT4 46、NT5 30、NT6 28、NT7 56、NT8 8、NT9 22、NT10 5、NT11 3、NT12 8、NT13 2、NT14 7、NT15 1、NT16 1、NT17 1、NT18 1、ST01 348、ST02 82、ST03 37、ST04 103、ST05 180、ST06 170、ST07 49、ST08 21、ST09 72、ST10 144、ST11 62、ST12 5、ST13 61、ST14 30、ST15 46、ST16 46、ST17 6、ST18 23、ST19 18、ST20 32、ST21 14、ST22 1、ST23 59、ST24 34、ST25 62、ST26 48、ST27 12、ST28 14、ST29 5、ST30 28、ST31 11、ST32 26、ST33 16、ST34 7、ST35 9、ST36 2、ST37 5、ST38 2、ST39 7、ST40 6、ST41 4、ST42 3、ST43 2、ST44 3、ST45 2、ST46 3、ST47 2、ST48 2、ST49 1、ST50 1、ST51 6、ST52 2、ST53 13、ST54 9、ST55 1、ST56 1、ST57 2、ST58 1、ST59 2、ST60 2、ST61 1。

### 1.4 实装方式（2026-11-11，11 问题 40）

- **核心配方**是改变物料的那一步，产物出来就是该物料（来源值见 04 实现约定）；**可选精制**是同一物料的提纯（产物 → 产物，纯度按配方类型提高），做不做由玩家选；**并入**的工序不单独写配方，试剂作输入、回收物与副产作副产出。
- **一行写了多个模板时，相邻核心配方的配方类型相同就合为一条**：M14 与 M23 同属 R11，合并后在 M23 上做，模式取需要加压的那一步（加氢或羰基化；两者都有时取加氢）；全在 M14 的合为一条 M14 配方；两步都是 R10 的合为一条蒸馏配方。合并后的输入是各步原料之和，中间产物不出现，产出是最终产物与各步副产，时长为各步之和。
- **配方类型不同的相邻步骤拆开**，中间产物登记为物料（茉莉酸、异丙醇胺、高圣草酚、对乙基苯磺酸钠、大豆油脂肪酸、发酵起子培养液、柏木油、4,4-二甲基-1,3-二氧六环、1-庚烯，以及 9 种浸膏），已改写进对应路线。
- 逐条结果见 [19](19-batch-5-synthetic-flavors.md) 第 6 节；天然香料改写后只剩“提取物 / 油树脂”这类两种产品形态的条目（N161、N231、N245、N265），两条配方产出同一物品，不是多步。

- **原料用量**（2026-11-12，11 问题 42）：核心配方每产出 1 单位所需的天然原料个数，按现实得油率分档（下表），取代“草本、花 8，果实、根 4”：
  - 精油（NT1、NT2、NT12）：取原料所在档的值；
  - 浸膏、油树脂（NT4、NT6，以及 NT5 前段的浸膏）：取档值的一半，最少 1（溶剂提取的得率约为水蒸气蒸馏的 5–10 倍）；
  - 酊、提取物（NT3、NT7）：2（可溶物得率 10%–30%，与档无关）；树脂、香脂熔化过滤（NT8）：1；干品粉末（NT14）：鲜叶、全草 4，其余 2；
  - 输入是中间物或流体的模板（NT9、NT10、NT13、NT15–NT18）照原写；溶剂、试剂的数量照 I31 的生成约定；
  - 流体原料不套档表，按桶写（朗姆酒浸膏 `rum_concrete` 用朗姆酒 4 桶）（2026-11-12，11 问题 44）；
  - **产出量不随档改**：“每产出 1 单位”指模板原来的产出量（精油 100 mB，浸膏 250 mB 或 1 件），折合现实得率（如 8 个鲜叶出 100 mB 精油约 1.2%）（2026-11-12，11 问题 44）。
- **合成配方的数量**按关键反应的系数取整数（如缩醛两份醇、二酯两份酸），系数都是 1 时各写 1；配方数量与方程式对不上的改其一（2026-11-12，11 问题 42）。数量仍不是区分配方的手段（第 1.5 节）。
- **发酵配方的数量**（ST18、NT17 等）按每份产物取整，不按配平式缩放（2026-11-12，11 问题 44）：葡萄糖 1 份；氮源按产物的氮数，氨 1 份算 1 个氮（S0339 脯氨酸 1 份、S0343 精氨酸 4 份 + 二氧化碳 1 份），硫酸铵 1 份算 2 个氮（S0344 赖氨酸 1 份，与 17 第 534 号一致）；培养基的水照模板写 2 桶，不进方程式。方程式仍写配平式。
- **联产合并的产出量按输入分摊**（2026-11-12，11 问题 44）：两个产物本来各自消耗全部原料时（丙酮碱缩合：S0267 异弗尔酮要 3 份丙酮、S0869 异亚丙基丙酮要 2 份），合并后按输入分成两份写（丙酮 3 份 → 异弗尔酮 500 mB + 异亚丙基丙酮 750 mB），不把两个产物各按单做的量叠加，否则产物的碳多于输入。切馏分、分馏类的联产（煤焦油、杂醇油、松香与松节油）照原量写，本来就是同一批料的不同部分。
- **可选精制**按产物性质：液体蒸馏，固体乙醇重结晶，K1、K2 水重结晶，气体在精馏塔精馏（I31 实装，2026-11-12 确认）。
- **碱熔**（ST47、P70）：回转窑没有流体输入，碱熔只出酚钠等钠盐，水溶酸化另写 M14 配方；ST38 的蓖麻油是流体，碱裂解改在 M23 高温做，釜残癸二酸二钠另在 M14 酸化（2026-11-12，11 问题 42）。第四批 195 号的间苯二磺酸同样是溶液，碱熔也在 M23 高温（搅拌反应罐没有高温档）（2026-11-12，11 问题 44）。

| 档 | 现实得油率 | 原料（`materials/` 省略） |
| --- | --- | --- |
| 16 | 低于 0.1%：多数鲜花、葱类、陈化鸢尾根、酒脚、海带 | `aglaia_flower`、`bitter_orange_flower`、`boronia_flower`、`cassie_flower`、`celery_leaf`、`chives`、`crimson_glory_rose`、`elderflower`、`gardenia_flower`、`genet_flower`、`immortelle_flower`、`jasmin_orange_flower`、`jasmine_grandiflorum_flower`、`jasmine_sambac_flower`、`kelp`、`leek`、`lemon_balm`、`mimosa_flower`、`onion`、`orris_root`、`osmanthus_flower`、`rose_flower`、`tuberose_flower`、`violet_leaf`、`wild_rose_flower`、`wine_lees` |
| 8 | 0.1%–1%：鲜叶与全草、柑橘全果、多数根与树皮、芫荽籽 | `abies_needles`、`ambrette_seed`、`angelica_root`、`basil`、`bergamot`、`bitter_almond`、`bitter_orange`、`bitter_orange_leaf`、`blackcurrant_bud`、`canadian_fleabane`、`carrot_seed`、`celery_flower`、`ceylon_cinnamon_bark`、`chrysanthemum_flower`、`citronella_grass`、`clary_sage_herb`、`conehead_thyme`、`coriander_seed`、`costus_root`、`cyperus_rhizome`、`daidai_fruit`、`davana_herb`、`dill`、`dwarf_pine_needles`、`fingered_citron`、`fir_needles`、`garlic`、`german_chamomile`、`grapefruit`、`horseradish_root`、`hyssop`、`lavender`、`lemon`、`lemon_leaf`、`lemongrass`、`lime`、`lovage_root`、`mandarin`、`mandarin_leaf`、`maple_syrup`、`marigold`、`marjoram`、`massoia_bark`、`michelia_alba_flower`、`michelia_alba_leaf`、`oakmoss`、`orange`、`palmarosa`、`parsley`、`peach_leaf`、`pennyroyal`、`peppermint`、`perilla_leaf`、`pine_needles`、`pummelo`、`rabdosia_herb`、`rockrose_twig`、`roman_chamomile`、`rose_geranium_leaf`、`rosemary`、`rosewood`、`rue`、`sage`、`scotch_spearmint`、`sophora_flower`、`spanish_sage`、`spearmint`、`spike_lavender`、`stevia`、`summer_savory`、`tangelo`、`tangerine`、`tarragon`、`thuja_leaf`、`thyme`、`torreya_shell`、`tree_moss`、`valerian_root`、`vitex_leaf`、`winter_savory`、`wintergreen_leaf`、`wormwood`、`yuzu` |
| 4 | 1%–5%：籽与干香辛料、木材、干叶、根茎 | `foods/douchi`、`allspice`、`amyris_wood`、`angelica_seed`、`anise_seed`、`atractylodes_rhizome`、`bird_eye_chili`、`black_pepper`、`buchu_leaf`、`cajeput_leaf`、`canadian_snakeroot`、`cananga_flower`、`caraway`、`carob_pod`、`cascarilla_bark`、`cedarwood`、`celery_seed`、`ceylon_cinnamon_leaf`、`chicory_root`、`cinnamon`、`clove_leaf`、`cumin_seed`、`dried_turmeric`、`eucalyptus_leaf`、`fennel_seed`、`fenugreek_seed`、`ginger`、`grains_of_paradise`、`guaiac_wood`、`hemicellulose`、`hops`、`jambu`、`jujube`、`juniper_berry`、`katsuobushi`、`laurel_leaf`、`lavandin`、`litsea`、`mexican_oregano`、`nardostachys_root`、`patchouli_leaf`、`peruvian_pepper_berry`、`pimento_leaf`、`sandalwood`、`sichuan_pepper`、`star_anise`、`styrax`、`sweet_fennel_seed`、`tea_tree_leaf`、`vetiver_root`、`west_indian_bay_leaf`、`white_pepper`、`yellow_mustard_seed`、`ylang_ylang_flower` |
| 2 | 5%–15%：小豆蔻、肉豆蔻、树脂类、香荚兰、种仁 | `apricot_kernel`、`asafoetida`、`cardamom`、`clove_stem`、`cubeb`、`cured_vanilla_bean`、`galbanum`、`mace`、`myrrh`、`nutmeg`、`olibanum`、`opoponax`、`sesame` |
| 1 | 15% 以上：丁香花蕾、香脂、灵猫香 | `civet`、`clove_bud`、`copaiba_balsam`、`elemi` |

| 模板 | 核心配方（类型、设备） | 可选精制 | 并入的工序 |
| --- | --- | --- | --- |
| NT1 | R10 水蒸气蒸馏（M08 `steam_distillation`） | — | 粉碎、油水分层（精油分层器模块）；花水为副产出 |
| NT2 | R02 压榨（M02；T2 起 M12） | — | 离心分油 |
| NT3 | R04 浸提（M04） | — | 过滤 |
| NT4 | R04 溶剂浸提（M18，回收溶剂作副产出） | — | 过滤、蒸除溶剂 |
| NT5 | R04 溶剂浸提（M18：浸膏 + 乙醇 → 净油 + 植物蜡 + 回收乙醇） | — | 冷冻析蜡、过滤 |
| NT6 | R04 溶剂浸提（M18） | — | 粉碎、回收溶剂 |
| NT7 | R04 浸提（M04 / M18） | R05 浓缩（M15）、R07 喷雾干燥（M28） | 过滤 |
| NT8 | R04 热浸提（M03） | — | 熔化、过滤 |
| NT9 | R10 蒸馏（M08 只取主馏分；M22 多馏分） | R06 冷却结晶（M15） | — |
| NT10 | R16 发酵或酶解（M29） | R04、R10 | — |
| NT11 | R09 干馏（M07 `dry_distillation`、M20） | R03 沉降（M04） | 冷凝；N267 焦木酸为单独物料 `refined_wood_vinegar`：`wood_vinegar` + 活性炭，M14 一条（静置分出的木焦油作副产出，2026-11-12） |
| NT12 | R10 水蒸气蒸馏（M08），浸渍酶解并入（配方加水，时长取浸提与蒸馏之和） | — | 粉碎、浸渍；含氰苷原料的除氰（熟石灰、硫酸亚铁作输入） |
| NT13 | 两条：R16 密闭发酵（M05）→ `starter_culture_broth`；R10 蒸馏（M08） | — | — |
| NT14 | R01 研磨（M01 / M11） | — | 烘干（原料为干品时省略） |
| NT15 | R11 皂化与酸化（H03 / M14） | — | 水洗；甘油为副产出 |
| NT16 | R11 酸水解与中和（M14） | R06 结晶（M15）、R15 离子交换（M25） | 过滤、脱色 |
| NT17 | R04 浸提与反萃（M18） | R06 结晶（M15） | 脱色、过滤 |
| NT18 | R05 浓缩并回收香气（M15：果汁 → 香气冷凝液 + 浓缩果汁） | R10 提浓 | 压榨取汁（用 12 成品果汁） |
| ST01 | R11（M14） | R10 蒸馏（M08 / M22） | 中和水洗 |
| ST02 | R11（M14） | R10 | 水洗；副产酸为副产出 |
| ST03 | R11（M14） | R10 | 蒸出的低级醇为副产出 |
| ST04 | R13 气相脱氢（M27）或 R11 化学 / 空气氧化（M14），按路线所写 | R10 | — |
| ST05 | R11（M23 加氢模式） | R10 | — |
| ST06 | R11（M14） | R10 | 脱水、环化 |
| ST07 | R11（M14） | R10 | 中和 |
| ST08 | R11（M14） | R10 | — |
| ST09 | R11（M14） | R10 | 水洗 |
| ST10 | R11（M14；加压时 M23）或 R13（M27 气相），按路线所写 | R10 | — |
| ST11 | R11（M14）；路线注明“M27 气相”的四行（`alpha_picoline`、S0897、S0904、S1156）为 R13（M27 `gas_catalysis`，没写催化剂的用 `catalysts/alumina`，2026-11-12） | R10 | 脱氢（液相时） |
| ST12 | R11（M14），直接产出目标物（来源值低） | R04 萃取、R10 | — |
| ST13 | R10 蒸馏（M08 / M22）：精油 → 单体 + 余油 | — | 亚硫酸氢盐、硼酸酯等化学法分离 |
| ST14 | R11（M14；加氢时 M23） | R10 | — |
| ST15 | R11（M14） | R10 | 水解 |
| ST16 | R11（M14 惰性） | R10 | 格氏试剂制备与水解（镁、卤代烃作输入） |
| ST17 | R11（M14） | R06 结晶、R07 干燥 | — |
| ST18 | R16（M29） | R04 萃取、R10 / R06 | — |
| ST19 | R11（M23 羰基化模式） | R10 | — |
| ST20 | R11（M14） | R06 | — |
| ST21 | R04（M18） | R15（M25）、R06 | — |
| ST22 | R19（M21） | — | — |
| ST23 | R11（M14） | R10 | — |
| ST24 | R11（M14） | R10 | — |
| ST25 | R11（M14） | R10 | — |
| ST26 | 不写配方（引用已有物料） | — | — |
| ST27 | R13（M27 气相催化）；二元酸成环时 R09（M20 缩合） | R10 | 冷凝分水 |
| ST28 | R11（M14 惰性） | R10 | 水解、脱羧 |
| ST29 | R11（M14 惰性） | R10 | 三苯基氧膦为副产出 |
| ST30 | R11（M14）或 R13（M27 气相），按路线所写 | R10 | — |
| ST31 | R11（M14） | R10 | 丙酮为副产出 |
| ST32 | R11（M14 耐腐蚀内衬），制酰氯与酰化合为一条 | R10 / R06 | 尾气吸收；盐酸为副产出 |
| ST33 | R11（M23 加氢或氨解）或 R13（M27 气相胺化），按路线所写 | R10 | — |
| ST34 | R13（M27） | R10 | — |
| ST35 | R11（M14） | R04 萃取、R10 | 中和 |
| ST36 | R11（M14） | R06 / R10 | — |
| ST37 | R11（M14），成醚与 [3,3] 重排合为一条（脱醇并入，游戏简化） | R10 | — |
| ST38 | 两条：R11 碱裂解（M23 高温；蓖麻油是流体，回转窑不能进料）→ 2-辛醇 + `disodium_sebacate`；R11 酸化（M14）→ 癸二酸（2026-11-12） | R10 / R06 | — |
| ST39 | R13 裂解（M27） | R10 | 急冷 |
| ST40 | R11（M23 加氢模式） | R10 | — |
| ST41 | R11（M23），炔化与部分加氢合为一条 | R10 | — |
| ST42 | R11（M14 紫外灯模块） | R10 | 还原、脱水 |
| ST43 | R11（M14 惰性） | R10 | — |
| ST44 | R13（M27） | R10 | — |
| ST45 | R11（M14） | R10 / R06 | — |
| ST46 | R11（M14） | R10（M08 水蒸气带出） | 萃取 |
| ST47 | 三条：R11 磺化成盐（M14）→ 芳基磺酸钠；R09 碱熔（M20）→ 酚钠；R11 酸化（M14）（2026-11-12） | R10 | — |
| ST48 | R13（M27） | R10 | 水洗 |
| ST49 | R11（M14 紫外灯模块，低温夹套） | R10 / R06 | 臭氧化物还原 |
| ST50 | R11（M14 聚合模式） | R10（M22 汽提） | — |
| ST51 | R10（M22 多馏分） | R04 萃取 | — |
| ST52 | R11（M23） | R10 | — |
| ST53 | A R11（M14）；B 同 NT12 | R10 | — |
| ST54 | R13（M27 气相）或 R11（M14 耐腐蚀内衬），按路线所写 | R10 | 尾气吸收 |
| ST55 | 两条：R11 高温高压水解（M23）→ 脂肪酸混合物；R06 尿素包合（M15，P102） | R10（M22 减压） | — |
| ST56 | R11（M14） | R10 | — |
| ST57 | R11（M14） | R10 | 皂化、酸化 |
| ST58 | R11（M14） | R10 | — |
| ST59 | R11（M23） | R06 | 水解 |
| ST60 | R11（M14 惰性） | R10 | — |
| ST61 | R11（M23） | R06 | 酸化 |

### 1.5 配方唯一性（2026-11-12，11 问题 41）

- **匹配要求全覆盖**：设备输入槽里非空的槽位必须全部被这条配方消耗，有剩余就不匹配。所以一条配方的输入集合是另一条的真子集时不算冲突（{A, B} 与 {A, B, C} 并存）。两处说明（2026-11-12）：设备为满足条件自己消耗的物料不是配方输入，不参与全覆盖判断（M14 惰性气氛的氮气罐、燃料、电）；全覆盖只看输入槽，产物槽里的存货不影响匹配，T0 器具的产物留在原槽，下一批照常开始。
- **冲突的判据**：同一配方类型下，`mode`、`catalyst`、`requirements` 都相同，而两条配方的输入物料集合（按物料种类，不看数量）相同。**数量与配比不是区分手段**——1 钻石 + 1 铁与 1 钻石 + 2 铁冲突（2026-11-12 定，取代问题 34 中“靠配比区分”的说法）。
- **区分手段**（按优先次序）：① 本来就是同一道工序的多个产物，合并成一条多产出配方（联产，见下表）；② 模式或催化剂不同；③ 增减某个输入物料（试剂、溶剂、还原剂、菌株标签）；④ 拆成两步，中间产物登记为物料（“后一步由前一步做”，第五批多数冲突都这样解）；⑤ `requirements` 的条件不同——**只靠条件不可靠**，设备的能力是集合：M14 搅拌反应罐同时具备加热与冷却，M23 高压反应釜同时具备加热与高温，两条输入相同、只差温度或压力的配方在同一台设备上总是先注册的那条被选中。只有条件改变了“哪些设备能做”时才算区分，否则要和催化剂或输入的差别一起用（2026-11-12 补）。
- **路线文字里出现已登记的中间体时，配方从该中间体开始**（问题 40）：中间体的制取是它自己的配方，不要把前段一并写进下游配方，否则两条配方的输入相同。
- **概率产出**：同一宿主、同样操作得到的菌株共用一块 R18 平板配方，产出列出全部菌株，按 `plate.weights` 抽取，玩家挑菌落（[machines/01 §3.1](../machines/01-recipe-types.md) 的 `plate` 字段、[additives/06 §T4](06-machines.md)）。所有需要挑出来的菌株都带概率，带供体样本的工程菌也一样（失败产出宿主菌）。
- **数据生成检查**：按（配方类型、`mode`、`catalyst`、`requirements`、输入物料集合）建索引，重复的报错（I31）。

#### 联产：合并成一条多产出配方

同一道工序本来就同时得到的产物，写成一条配方的多个产出（需要时产出带 `min_tier`、`chance`）；单体再由下游一步分出的，另写一条配方。

| 一条配方 | 全部产出 | 模板 |
| --- | --- | --- |
| `coal_tar`（煤焦油）M22 多馏分精馏 | S0095 A 邻-甲酚、S0675 3,4-二甲酚、S0676 1-甲基萘、S0705 A 吲哚、S0724 A 2,6-二甲基吡啶、S0740 B 吡啶、S0771 B 喹啉、S0949 异喹啉、`methylnaphthalene` 2-甲基萘 | ST51 |
| `wood_tar`（木焦油）精馏 | S0100 A 2-甲氧基-4-甲基苯酚、N352 松焦油 | ST13、NT9 |
| N298 松节油精馏 | S0658 α-蒎烯、S0659 β-蒎烯、S0681 3-蒈烯 | ST13 |
| S0658 α-蒎烯酸催化异构化 | S0655 异松油烯、S0657 莰烯、S0680 A α-松油烯 | ST14 |
| N402 杂醇油精馏 | S0005 A 异丁醇、S0863 2-甲基-1-丁醇 | ST13 |
| N261 精油精馏 | S0064 1-对-䓝烯-4-醇、S0670 A γ-松油烯、S0680 B α-松油烯 | ST13 |
| N131 甜橙油精馏 | S0199 β-甜橙醛、S0652 巴伦西亚橘烯、N132 除萜甜橙油 | ST13、NT9 |
| N186 松针油精馏 | S0385 B 乙酸龙脑酯、S0930 乙酸 l-龙脑酯、S1105 l-苧烯 | ST13 |
| N015 蒜油精馏 | S0749 A 烯丙基二硫醚、S0814 A 二烯丙基三硫醚 | ST13 |
| N199 精油精馏 | S0847 胡椒烯酮、S0994 胡薄荷酮 | ST13 |
| N086 柠檬油精馏 | N087 无萜柠檬油、N088 柠檬油萜烯 | NT9 |
| N034 白柠檬油精馏 | N035 白柠檬萜烯、N262 除萜白柠檬油 | NT9 |
| N226 甜橙油（冷榨）精馏 | N133 甜橙油萜烯、N263 除萜甜橙皮油 | NT9 |
| N051 葡萄柚油精馏 | S0223 A 圆柚酮、N269 葡萄柚油萜烯 | ST13、NT9 |
| N097 香叶油精馏 | S0036 玫瑰醇、N098 除萜香叶油 | ST13、NT9 |
| `crude_camphor_oil` 樟脑原油精馏 | N165 白樟油、N220 d-樟脑 | NT9 |
| S1081 山葵油精馏 | S1087 A 异硫氰酸 6-(甲硫基)己酯、S1088 A 异硫氰酸 5-(甲硫基)戊酯 | ST13 |
| N318 辣根油精馏 | S1079 A 异硫氰酸 3-丁烯酯、S1080 A 异硫氰酸 4-戊烯酯 | ST13 |
| S0047 法尼醇酸催化环化 | S0669 B 红没药烯、S0678 α-金合欢烯 | ST14 |
| `ricinoleic_acid` 蓖麻油酸裂解 | S0136 庚醛、S0336 10-十一碳烯酸 | ST39 |
| `paraffin_wax` 石蜡裂解 | 262 号 1-辛烯、`butene` 1-丁烯、`decene` 1-癸烯 | ST39 |
| `propene` 丙烯羰基合成 | S0119 丁醛、S0184 异丁醛 | ST19 |
| `butene` 1-丁烯羰基合成 | S0120 2-甲基丁醛、S0123 戊醛 | ST19 |
| `coconut_oil` 椰子油甲醇解 | S0472 B 辛酸甲酯、S0484 B 十四酸甲酯（同一配方按碳数切馏分） | ST03 |
| 125 + S0203 碱缩合后 `sulfuric_acid` 环化 | S1165 β-甲基紫罗兰酮、S1166 δ-甲基紫罗兰酮 | ST06 |
| 125 + S0203 碱缩合后 `phosphoric_acid` 环化 | S1179 α-甲基紫罗兰酮、S1180 α-异甲基紫罗兰酮 | ST06 |
| S0210 + `butanediamine` 缩合脱氢 | S1146 3,5-二乙基-2-甲基吡嗪、S1447 2,5-二乙基-3-甲基吡嗪 | ST11 |
| S0203 甲乙酮 + `formaldehyde` 缩合 | S0209 1-戊烯-3-酮、`methyl_isopropenyl_ketone` 3-甲基-3-丁烯-2-酮 | ST06 |
| `cedarwood_oil` 柏木油精馏 | N321 柏木油萜烯、S0074 柏木烯醇、`cedrol` 柏木脑（结晶） | ST13、NT9 |
| S1230、S1231 山楂核干馏液 | 两种烟熏香味料（2026-11-11 已定） | NT11 |
| `acetone` + `potassium_hydroxide` 丙酮碱缩合 | S0869 4-甲基-3-戊烯-2-酮（二聚物）、S0267 异弗尔酮（三聚物，M22 分出） | ST06 |
| S0117 + S1010 + `sulfur` | S0896 3,5-二乙基-1,2,4-三硫杂环戊烷、S1124 3,6-二乙基-1,2,4,5-四硫杂环己烷（M22 分出；原写的氨催化只改产出比，不作区分手段） | ST10、ST11 |

#### 逐条处理

| 条目 | 原因 | 处理 |
| --- | --- | --- |
| S0018 正壬醇 / S0145 壬醛 | 多模板合并后与 S0145 输入相同 | 不合并：S0018 改为 S0145 + 氢气加氢（`catalysts/copper`） |
| S0032 / S1502、S0996 / S1233、S0040 / S0062、S0023 / S0335 | 同一底物、同一加氢，只差氢气用量 | 按催化剂与温度区分：选择性加氢羰基用 `catalysts/copper`（常压、加热），深度加氢用 `catalysts/nickel` + 高温 |
| S0686 甲硫醇 / S0693 二甲基硫醚 | 甲醇 + 硫化氢，只差比例 | S0693 改为 S0686 + 甲醇（气相，`catalysts/alumina`） |
| S0928 乙硫醇 / S0810 二乙基硫醚 | 同上 | S0810 改为 S0928 + 乙醇 |
| S0749 B / S0814 B / S0815 多硫醚 | 硫化钠 + 硫，只差硫量 | 递进：S0814 B 改为 S0749 + 硫，S0815 改为 S0814 + 硫 |
| S1449 / S1429 | 甲硫醇 + 甲醛 + 硫化氢，只差比例 | S1429 改为 S1449 + 甲醛 + S0686 缩合 |
| S0790 / S1017 | S0786 + 乙酸酐，只差酸酐用量 | S1017 加 `sodium_acetate`（碱催化、加热）使 O、S 同时乙酰化 |
| S1152 / `hydroxymenthofurolactone` | S0279 + 氧气，光敏氧化 | `hydroxymenthofurolactone` 加 `sodium_sulfite` 还原氢过氧化物 |
| S0385 A / S0930 B 乙酸龙脑酯的两种构型 | 龙脑物料只有消旋一个，酯化无法区分构型 | 删去 S0930 B，l-龙脑酯只从 N186 单离（S0930 A） |
| S0077 d-核糖 / S0204 A 乙偶姻 | 葡萄糖 + 枯草芽孢杆菌 | 各设诱变菌株：`cultures/bacillus_subtilis_ribose`、`cultures/bacillus_subtilis_acetoin` |
| `p_toluidine` / `toluene_2_3_diamine` | 甲苯 + 混酸 + 氢气，只差硝化深度 | 拆中间体：`nitrotoluene`（一硝化）与 `dinitrotoluene`（由 `nitrotoluene` 再硝化） |
| `piperylene` / `light_naphtha` | 都写成原油 | `piperylene` 改为 `light_naphtha` M27 裂解后切 C5 馏分；2026-11-12 起与碳四馏分同一裂解（17 `naphtha_cracked_gas`） |
| `isoprenol` / `dimethyldioxane` | 异丁烯 + 甲醛，普林斯反应 | 按条件区分：`isoprenol` 走 M23 高压水相、不加酸；`dimethyldioxane` 加 `sulfuric_acid` 走 M14 |
| S0224 B l-香芹酮 / S0670 B γ-松油烯 | 都从 `limonene` | S0224 B 补试剂物料 `nitrosyl_chloride`（亚硝酸钠 + 盐酸现制，剧毒只写试剂） |
| 489 羰基铁粉、500 甘油磷酸钙、496、513、529、534、545、550 等 | 路线文字把已登记中间体的前段一并写了进去，直接实装会与中间体自己的配方撞车 | 按问题 40：配方从已登记的中间体开始，前段是中间体自己的配方 |
| 486 铁卟啉 / 493 血红素铁 | 同一物质 | 合并为一个物料（2026-11-12 已定） |
| 495 L-乳酸钙 / 114 乳酸钙 | 钙盐法发酵的输入与 114 相同 | 495 用新增的产 L-乳酸乳杆菌菌株（`cultures/lactobacillus_l_lactate`），两条配方的输入因此不同 |
| 569 丁苯橡胶 75/25 与 50/50 | 同一物料的两条配方只差配比 | 只写一条配方（75/25），另一配比写在物料描述里 |
| 571 聚乙烯 / 580 聚乙烯蜡 | 乙烯高压聚合 | 580 改用 `acetaldehyde` 作链转移剂（US4820876）；氢气法作备选写在依据栏 |
| 工程菌与诱变株 | 同一宿主 + 表达载体（或同一诱变操作）输入相同 | 按宿主一块概率平板：产出列出该宿主的全部菌株，按 `plate.weights` 抽取（见本节末） |
| N053 / N393、N079 / N319、N104 / N105、N391 / 黄葵籽浸膏、N082 / N225、N172 / N334、N198 / N388、N219 / N383、N302 / N345 | 同一原料的不同产品形态 | 不冲突：酊用 M04、浸膏用 M18、精油用 M08、冷榨用 M02、粉用 M11、香脂用 M03，设备不同 |

#### 前几批与第二轮核对补上的（2026-11-12）

实装时又查出 14 组，7 组在前几批、7 组在香料；处理一律是“后一步由前一步做”或换试剂，不靠配比。

| 条目 | 原因 | 处理 |
| --- | --- | --- |
| 三聚磷酸钠 51 / 焦磷酸钠 53 | 都写成磷酸氢二钠煅烧缩合 | 文档本来已分开（[13](13-batch-2.md) 第 51、53 行）：51 用磷酸氢二钠 + 磷酸二氢钠按 2 : 1 的混盐（Na/P = 5/3），53 只用磷酸氢二钠；实装按文档的混盐配料改。实装核对后实际撞车的是 51 与 190，见下表 |
| 柠檬酸三钠 58 / 柠檬酸一钠 149 | 柠檬酸 + 碳酸钠，只差配比 | 149 改用碳酸氢钠（小苏打）部分中和（依据本来就写“碳酸钠或碳酸氢钠”） |
| 96 β-淀粉酶 / 402 麦芽碳水化合物水解酶 | 都是大麦麦芽温水浸提 | 402 为麦芽浸提液（粗酶，含 α-、β-淀粉酶）；96 改为 402 + `ammonium_sulfate` 盐析分级（M14 + M12） |
| 硫酸铵 / 硫酸氢铵 | 氨 + 硫酸，只差配比 | 硫酸氢铵改为 `ammonium_sulfate` + `sulfuric_acid`（M14 加热） |
| 磷酸氢二铵 171 / 磷酸二氢铵 454 | 磷酸 + 氨，只差中和终点 | 454 为磷酸 + 氨一步氨化；171 改为 454 + 氨二步氨化（现实工艺） |
| 异丙苯 / 1,3-二异丙苯 | 苯 + 丙烯，只差丙烯份数 | 二异丙苯改为 `cumene` + `propene` 再烷基化（325 的前段改写） |
| 磷酸氢二钾 186 / 磷酸二氢钾 187 | 磷酸 + 碳酸钾，只差中和终点 | 187 改为磷酸 + 氯化钾（现实工艺，副产盐酸进 M19 吸收）；186 改为 187 + 碳酸钾 |
| S0868 对-䓝烷-2-酮 / S1364 对-䓝烷醇-2 | 合并后都是 133 + 氢气 | S1364 改为 S0868 + 氢气（`catalysts/copper`，只还原羰基） |
| S0795 糠醇 / S0871 2-甲基呋喃 | 糠醛 + 氢气 + 铜催化剂 | S0871 改为 S0795 + 氢气（继续氢解脱水） |
| S1158 2-巯基-3-丁醇 / S1240 2,3-二巯基丁烷 | 环氧丁烷 + 硫化氢 | S1240 改为 S1158 + S1010（酸催化置换羟基） |
| S0113 2,5-二甲基苯酚 / `trimethylphenol` 2,3,6-三甲基苯酚 | 间甲酚 + 甲醇气相甲基化，只差甲醇份数 | `trimethylphenol` 改为 S0113 + `methanol`（在剩下的邻位再甲基化） |
| N231 巴拉圭茶净油 / 提取物 | 同一行两个模板（NT5、NT7），原料相同 | 只做提取物（NT7，M04 稀乙醇浸提）；GB 名称含两种形态时取浸提物 |

#### 实装检查补上的（2026-11-12）

I31 的数据生成检查（按配方类型、模式、催化剂、输入物料集合分组）又查出下列各组，处理一律是“后一步由前一步做”、换试剂或催化剂、联产合并；文档已按实装改（11 问题 41、42）。

| 条目 | 原因 | 处理 |
| --- | --- | --- |
| 三聚磷酸钠 51 / 焦磷酸一氢三钠 190 | 磷酸氢二钠 + 磷酸二氢钠，只差配比（2 : 1 与 1 : 1） | 190 改为 `sodium_acid_pyrophosphate` + `tetrasodium_pyrophosphate` 在 M14 溶解后低温干燥（15 第 190 行） |
| 乙酸钠 / 双乙酸钠 33 | 纯碱 + 乙酸，只差配比 | 33 改为 `sodium_acetate` + 冰乙酸（13 第 33 行） |
| 磷酸氢钙 170 B / 磷酸三钙 49 A | 磷酸 + 石灰乳，只差中和终点 | 49 A 改为 `dicalcium_phosphate` + 石灰乳二步中和 |
| 海草(藻)提取物 N203 / 甘露醇 40 A | 海带 + 水 | 40 A 改由 N203（`kelp_extract`，即海带浸泡水）加乙醇除盐结晶，每 1,000 mB 加乙醇 250 mB、回收 200 mB（M15） |
| 氯化苄 / 苄叉二氯（127 B） | 甲苯 + 氯气，只差氯化深度 | 苄叉二氯改为 `benzyl_chloride` + 氯气光氯化（侧链逐级氯化） |
| 2-萘磺酸 / 萘三磺酸（196） | 萘 + 硫酸，只差磺化深度 | 萘三磺酸改为 `naphthalene_sulfonic_acid` + 硫酸（逐级磺化） |
| 异丙醇胺 / 1,2-丙二胺（132 B） | 环氧丙烷 + 氨 | 1,2-丙二胺改为 `isopropanolamine` + 氨，`catalysts/nickel` 胺化 |
| 松香（137）/ 松节油 N298 | 都是树脂水蒸气蒸馏 | 联产合并：一条配方，松节油随蒸汽馏出、分油器分出，松香留在釜里 |
| 琥珀酸酐 / γ-丁内酯 S0630 | 马来酸酐 + 氢气，镍 | S0630 改为 `succinic_anhydride` + 氢气，`catalysts/copper` |
| 二氯化硫 / `disulfur_dichloride` | 硫 + 氯气，只差比例 | `disulfur_dichloride` 改为 `sulfur_dichloride` + 硫 |
| 3-甲基吡啶（110）/ `alpha_picoline` | 乙醛 + 氨 | ST11 中注明“M27 气相”的四行在 M27 气相催化（没写催化剂的用 `catalysts/alumina`），110 在 M23 高压釜，配方类型不同 |
| 乙烯 / 乙醚 435 B | 乙醇 + 氧化铝，气相 | 435 B 改为 M23 加压、强酸性离子交换树脂液相脱水 |
| 环柠檬醛 S0715 / `alpha_cyclocitral` | 柠檬醛 + 苯胺 + 硫酸，只差温度 | `alpha_cyclocitral` 改为从 S0715 精馏切出 α 体 |
| 粗苯 / 煤焦油精馏、木醋液蒸馏 / 精馏、乙醇复蒸 / 杂醇油侧线 | 蒸馏甑与精馏塔同属 R10，输入相同 | 蒸馏甑一路用“蒸馏”模式（精馏塔没有此模式） |
| 异构体切割与母体精制（`alpha_cyclocitral`、S0931、3-乙基-2,6-二甲基吡嗪） | 精馏塔上切异构体与母体的可选精制输入相同 | 切割配方同时是母体的精制：剩下的母体按精馏塔默认纯度出，母体不再另写精制 |

#### 概率平板

| 平板 | 输入 | 产出（按权重抽取） | 说明 |
| --- | --- | --- | --- |
| 大肠杆菌工程菌平板 | `cultures/e_coli` + 供体样本（无供体时写环境样本）+ `cultures/expression_vector` | 产半胱氨酸（181）、产苏氨酸（N359）、产酪氨酸（546）、产色氨酸（547） | R18 基因重组模式；同一供体样本的只长出对应菌株，无供体样本的四株共用一块平板 |
| 谷氨酸棒杆菌诱变平板 | `cultures/corynebacterium` + 培养基 | 产赖氨酸（534、535、S0344）、产亮氨酸（S0326）、产脯氨酸（S0339）、产精氨酸（S0343）、产谷氨酰胺（S0884） | R18 诱变模式；五株共用一块平板 |
| 枯草芽孢杆菌诱变平板 | `cultures/bacillus_subtilis` + 培养基 | 产核糖（S0077）、产乙偶姻（S0204 A） | R18 诱变模式 |
| 带供体样本的工程菌（第四批 12 种酶、冰结构蛋白、凝乳酶、母乳低聚糖三种） | 宿主菌种 + 各自的供体样本 + `cultures/expression_vector` | 目标工程菌（权重 1）、宿主菌种（失败，权重 1） | 输入本来就不同，不冲突；按问题 41 同样带概率 |

## 2 表 B.2 天然香料（385 条）

按国标序号分三段。化学式、英文名见 09 第 E.1 节。

### 2.1 序号 1–130（N001–N132，129 条）

| 编码 | 名称 | 物料 ID | 阶段 | 模板 | 原料 |
| --- | --- | --- | --- | --- | --- |
| N001 | 丁香叶油 | `clove_leaf_oil` | T1 | NT1 | `materials/clove_leaf` |
| N002 | 丁香花蕾酊(提取物) | `clove_bud_tincture` | T1 | NT3 | `materials/clove_bud` + `ethanol` |
| N003 | 丁香花蕾油 | `clove_bud_oil` | T1 | NT1 | `materials/clove_bud` |
| N004 | 罗勒油 | `basil_oil` | T1 | NT1 | `materials/basil` |
| N005 | 八角茴香油 | `star_anise_oil` | T1 | NT1 | `materials/star_anise` |
| N006 | 九里香浸膏 | `jasmin_orange_concrete` | T2（437 为 T3） | NT4 | `materials/jasmin_orange_flower` + `n_hexane` |
| N007 | 广藿香油 | `patchouli_oil` | T1 | NT1 | `materials/patchouli_leaf` |
| N008 | 万寿菊油 | `tagetes_oil` | T1 | NT1 | `materials/marigold` |
| N010 | 小豆蔻油 | `cardamom_oil` | T1 | NT1 | `materials/cardamom` |
| N011 | 小豆蔻酊 | `cardamom_tincture` | T1 | NT3 | `materials/cardamom` + `ethanol` |
| N012 | 小茴香酊 | `fennel_tincture` | T1 | NT3 | `materials/fennel_seed` + `ethanol` |
| N013 | 山苍子油 | `litsea_cubeba_oil` | T1 | NT1 | `materials/litsea` |
| N014 | 山楂酊 | `hawthorn_fruit_tincture` | T1 | NT3 | `materials/hawthorn_fruit` + `ethanol` |
| N015 | 大蒜油 | `garlic_oil` | T1 | NT12 | `materials/garlic` |
| N016 | 大蒜油树脂 | `garlic_oleoresin` | T2 | NT6 | `materials/garlic` + `ethanol` |
| N017 | 天然康酿克油 | `green_cognac_oil` | T1 | NT1 | `materials/wine_lees` |
| N019 | 云木香油 | `costus_root_oil` | T1 | NT1 | `materials/costus_root` |
| N020 | 月桂叶油 | `sweet_bay_oil` | T1 | NT1 | `materials/laurel_leaf` |
| N021 | 乌梅酊 | `wumei_tincture` | T1 | NT3 | `materials/smoked_plum` + `ethanol` |
| N022 | 布枯叶油 | `buchu_leaf_oil` | T1 | NT1 | `materials/buchu_leaf` |
| N023 | 可可酊 | `cocoa_tincture` | T1（M11 为 T2） | NT3 | `materials/cocoa_nibs` + `ethanol` |
| N024 | 可可壳酊 | `cocoa_husk_tincture` | T1（M11 为 T2） | NT3 | `materials/cocoa_shell` + `ethanol` |
| N025 | 甘松油 | `nardostachys_oil` | T1 | NT1 | `materials/nardostachys_root` |
| N026 | 甘草酊 | `licorice_tincture` | T1 | NT3 | `materials/licorice_root` + `ethanol` |
| N027 | 甘草流浸膏 | `licorice_fluid_extract` | T1 | NT7 | `materials/licorice_root` + `water` |
| N028 | 冬青油 | `wintergreen_oil` | T1 | NT12 | `materials/wintergreen_leaf` |
| N029 | 白兰花油 | `michelia_alba_flower_oil` | T1 | NT1 | `materials/michelia_alba_flower` |
| N030 | 白兰叶油 | `michelia_alba_leaf_oil` | T1 | NT1 | `materials/michelia_alba_leaf` |
| N031 | 白兰花净油 | `michelia_alba_absolute` | T2（N032 为 T3） | NT5 | N032 + `ethanol` |
| N032 | 白兰花浸膏 | `michelia_alba_concrete` | T2（437 为 T3） | NT4 | `materials/michelia_alba_flower` + `n_hexane` |
| N033 | 白芷酊 | `angelica_dahurica_tincture` | T1 | NT3 | `materials/angelica_dahurica_root` + `ethanol` |
| N034 | 白柠檬油 | `lime_oil` | T1 | NT2 | `materials/lime` |
| N035 | 白柠檬萜烯 | `lime_oil_terpenes` | T2 | NT9 | N034 |
| N036 | 生姜油树脂 | `ginger_oleoresin` | T2 | NT6 | `materials/ginger` + `acetone` |
| N037 | 肉豆蔻油 | `nutmeg_oil` | T1 | NT1 | `materials/nutmeg` |
| N038 | 肉豆蔻酊 | `nutmeg_tincture` | T1 | NT3 | `materials/nutmeg` + `ethanol` |
| N039 | 中国肉桂油 | `cassia_oil` | T1 | NT1 | `materials/cinnamon` |
| N040 | 中国肉桂皮酊(提取物) | `cassia_bark_tincture` | T1 | NT3 | `materials/cinnamon` + `ethanol` |
| N041 | 红茶酊 | `black_tea_tincture` | T1 | NT3 | `materials/black_tea` + `ethanol` |
| N042 | 印蒿油 | `davana_oil` | T1 | NT1 | `materials/davana_herb` |
| N043 | 吐鲁酊(提取物) | `tolu_balsam_tincture` | T1 | NT3 | `materials/tolu_balsam` + `ethanol` |
| N044 | 吐鲁香膏 | `tolu_balsam_gum` | T1 | NT8 | `materials/tolu_balsam` |
| N045 | 豆豉酊 | `douchi_tincture` | T1 | NT3 | `foods/douchi` + `ethanol` |
| N046 | 杜松籽油（刺柏子油） | `juniper_berry_oil` | T1 | NT1 | `materials/juniper_berry` |
| N047 | 芫荽籽油 | `coriander_oil` | T1 | NT1 | `materials/coriander_seed` |
| N048 | 芹菜花油 | `celery_flower_oil` | T1 | NT1 | `materials/celery_flower` |
| N049 | 芹菜籽油 | `celery_seed_oil` | T1 | NT1 | `materials/celery_seed` |
| N050 | 牡荆叶油 | `vitex_leaf_oil` | T1 | NT1 | `materials/vitex_leaf` |
| N051 | 圆柚油 | `grapefruit_oil` | T1 | NT2 | `materials/grapefruit` |
| N052 | 苍术脂 | `atractylodes_oil` | T1 | NT1 | `materials/atractylodes_rhizome` |
| N053 | 枣子酊 | `jujube_tincture` | T1 | NT3 | `materials/jujube` + `ethanol` |
| N054 | 玫瑰油 | `rose_oil` | T1 | NT1 | `materials/rose_flower` |
| N055 | 玫瑰净油 | `rose_absolute` | T2（N056 为 T3） | NT5 | N056 + `ethanol` |
| N056 | 玫瑰浸膏 | `rose_concrete` | T2（437 为 T3） | NT4 | `materials/rose_flower` + `n_hexane` |
| N057 | 鸢尾浸膏 | `orris_concrete` | T2 | NT4 | `materials/orris_root` + `ethanol` |
| N058 | 鸢尾脂（鸢尾凝脂） | `orris_root_extract` | T1 | NT1 | `materials/orris_root` |
| N059 | 杭白菊花油 | `hangzhou_chrysanthemum_oil` | T1 | NT1 | `materials/chrysanthemum_flower` |
| N060 | 杭白菊花浸膏（杭菊花流浸膏） | `hangzhou_chrysanthemum_extract` | T1 | NT7 | `materials/chrysanthemum_flower` + `ethanol` |
| N061 | 枫槭油 | `maple_oil` | T1 | NT1 | `materials/maple_syrup` |
| N062 | 枫槭浸膏 | `maple_concrete` | T2 | NT4 | `materials/maple_syrup` + `ethanol` |
| N063 | 岩蔷薇浸膏（赖百当浸膏） | `labdanum_extract` | T2（437 为 T3） | NT4 | `materials/rockrose_twig` + `n_hexane` |
| N064 | 咖啡酊 | `coffee_tincture` | T1 | NT3 | `materials/roasted_coffee_bean` + `ethanol` |
| N065 | 罗汉果酊 | `luohan_fruit_tincture` | T1 | NT3 | `materials/monk_fruit` + `ethanol` |
| N066 | 金合欢浸膏 | `cassie_concrete` | T2（437 为 T3） | NT4 | `materials/cassie_flower` + `n_hexane` |
| N067 | 依兰依兰油 | `ylang_ylang_oil` | T1 | NT1 | `materials/ylang_ylang_flower` |
| N068 | 大花茉莉净油 | `jasmine_grandiflorum_absolute` | T2（N069 为 T3） | NT5 | N069 + `ethanol` |
| N069 | 大花茉莉浸膏 | `jasmine_grandiflorum_concrete` | T2（437 为 T3） | NT4 | `materials/jasmine_grandiflorum_flower` + `n_hexane` |
| N070 | 小花茉莉净油 | `jasmine_sambac_absolute` | T2（N071 为 T3） | NT5 | N071 + `ethanol` |
| N071 | 小花茉莉浸膏 | `jasmine_sambac_concrete` | T2（437 为 T3） | NT4 | `materials/jasmine_sambac_flower` + `n_hexane` |
| N072 | 佛手油 | `sarcodactylis_oil` | T1 | NT2 | `materials/fingered_citron` |
| N073 | 圆叶当归根酊（独活酊） | `angelica_root_tincture` | T1 | NT3 | `materials/angelica_root` + `ethanol` |
| N074 | 洋葱油 | `onion_oil` | T1 | NT12 | `materials/onion` |
| N075 | 生姜油 | `ginger_oil` | T1 | NT1 | `materials/ginger` |
| N076 | 姜黄油 | `turmeric_oil` | T1 | NT1 | `materials/dried_turmeric` |
| N077 | 姜黄油树脂 | `turmeric_oleoresin` | T2 | NT6 | `materials/dried_turmeric` + `acetone` |
| N078 | 姜黄浸膏 | `turmeric_extract` | T2 | NT4 | `materials/dried_turmeric` + `ethanol` |
| N079 | 葫芦巴酊 | `fenugreek_tincture` | T1 | NT3 | `materials/fenugreek_seed` + `ethanol` |
| N080 | 玳玳花油 | `daidai_flower_oil` | T1 | NT1 | `materials/bitter_orange_flower` |
| N081 | 玳玳花浸膏 | `daidai_flower_concrete` | T2（437 为 T3） | NT4 | `materials/bitter_orange_flower` + `n_hexane` |
| N082 | 玳玳果油 | `daidai_fruit_oil` | T1 | NT1 | `materials/daidai_fruit`（整果水蒸气蒸馏） |
| N083 | 柚皮油 | `pummelo_peel_oil` | T1 | NT2 | `materials/pummelo` |
| N084 | 柏木叶油(北美香柏) | `cedar_leaf_oil` | T1 | NT1 | `materials/thuja_leaf` |
| N085 | 枯茗籽油（孜然油） | `cumin_seed_oil` | T1 | NT1 | `materials/cumin_seed` |
| N086 | 柠檬油 | `lemon_oil` | T1 | NT2 | `materials/lemon` |
| N087 | 无萜柠檬油 | `terpeneless_lemon_oil` | T2 | NT9 | N086 |
| N088 | 柠檬油萜烯 | `lemon_oil_terpenes` | T2 | NT9 | N086 |
| N089 | 柠檬叶油 | `petitgrain_lemon_oil` | T1 | NT1 | `materials/lemon_leaf` |
| N090 | 柠檬草油 | `lemongrass_oil` | T1 | NT1 | `materials/lemongrass` |
| N091 | 栀子花浸膏 | `gardenia_flower_concrete` | T2（437 为 T3） | NT4 | `materials/gardenia_flower` + `n_hexane` |
| N092 | 树兰花油 | `aglaia_flower_oil` | T1 | NT1 | `materials/aglaia_flower` |
| N093 | 树兰花酊 | `aglaia_flower_tincture` | T1 | NT3 | `materials/aglaia_flower` + `ethanol` |
| N094 | 树兰花浸膏 | `aglaia_flower_concrete` | T2（437 为 T3） | NT4 | `materials/aglaia_flower` + `n_hexane` |
| N095 | 树苔净油 | `treemoss_absolute` | T2（N096 为 T3） | NT5 | N096 + `ethanol` |
| N096 | 树苔浸膏 | `treemoss_concrete` | T2（437 为 T3） | NT4 | `materials/tree_moss` + `n_hexane` |
| N097 | 香叶油（玫瑰香叶油） | `geranium_oil` | T1 | NT1 | `materials/rose_geranium_leaf` |
| N098 | 除萜香叶油 | `terpeneless_geranium_oil` | T2 | NT9 | N097 |
| N099 | 香风茶油（香茶菜油） | `xiangfengcha_oil` | T1 | NT1 | `materials/rabdosia_herb` |
| N101 | 香柠檬油 | `bergamot_oil` | T1 | NT2 | `materials/bergamot` |
| N102 | 香根油 | `vetiver_oil` | T1 | NT1 | `materials/vetiver_root` |
| N103 | 香根浸膏 | `vetiver_concrete` | T2 | NT4 | `materials/vetiver_root` + `ethanol` |
| N104 | 香荚兰豆酊 | `vanilla_bean_tincture` | T1 | NT3 | `materials/cured_vanilla_bean` + `ethanol` |
| N105 | 香荚兰豆浸膏(提取物) | `vanilla_bean_concrete` | T2 | NT4 | `materials/cured_vanilla_bean` + `ethanol` |
| N106 | 香附子油 | `cyperus_oil` | T1 | NT1 | `materials/cyperus_rhizome` |
| N107 | 香葱油 | `chives_oil` | T1 | NT12 | `materials/chives` |
| N108 | 香紫苏油 | `clary_sage_oil` | T1 | NT1 | `materials/clary_sage_herb` |
| N109 | 香榧子壳浸膏 | `torreya_shell_concrete` | T2 | NT4 | `materials/torreya_shell` + `ethanol` |
| N110 | 橘子油 | `mandarin_oil` | T1 | NT2 | `materials/mandarin` |
| N111 | 除萜橘子油 | `terpeneless_mandarin_oil` | T2 | NT9 | N110 |
| N112 | 酒花酊 | `hops_tincture` | T1 | NT3 | `materials/hops` + `ethanol` |
| N113 | 酒花浸膏 | `hops_extract_solid` | T2（437 为 T3） | NT4 | `materials/hops` + `n_hexane` |
| N114 | 桉叶油(蓝桉油) | `eucalyptus_oil` | T1 | NT1 | `materials/eucalyptus_leaf` |
| N115 | 海狸酊 | `castoreum_tincture` | T1 | NT3 | `materials/castoreum` + `ethanol` |
| N116 | 斯里兰卡肉桂皮油 | `cinnamon_bark_oil` | T1 | NT1 | `materials/ceylon_cinnamon_bark` |
| N117 | 斯里兰卡肉桂叶油 | `cinnamon_leaf_oil` | T1 | NT1 | `materials/ceylon_cinnamon_leaf` |
| N118 | 桂花净油 | `osmanthus_absolute` | T2（N120 为 T3） | NT5 | N120 + `ethanol` |
| N119 | 桂花酊 | `osmanthus_tincture` | T1 | NT3 | `materials/osmanthus_flower` + `ethanol` |
| N120 | 桂花浸膏 | `osmanthus_concrete` | T2（437 为 T3） | NT4 | `materials/osmanthus_flower` + `n_hexane` |
| N121 | 桂圆酊 | `longan_tincture` | T1 | NT3 | `materials/longan` + `ethanol` |
| N122 | 留兰香油 | `spearmint_oil` | T1 | NT1 | `materials/spearmint` |
| N123 | 核桃壳提取物 | `walnut_hull_extract` | T1 | NT7 | `materials/walnut_hull` + `water` |
| N124 | 素方花净油 | `common_jasmine_absolute` | T2（`common_jasmine_concrete` 为 T4） | NT5 | `common_jasmine_concrete` + `ethanol` |
| N125 | 桦焦油 | `sweet_birch_oil` | T1 | NT12 | `materials/hemicellulose` |
| N126 | 蚕豆花酊 | `broad_bean_flower_tincture` | T1 | NT3 | `materials/broad_bean_flower` + `ethanol` |
| N127 | 绿茶酊 | `green_tea_tincture` | T1 | NT3 | `materials/tea` + `ethanol` |
| N128 | 野玫瑰浸膏 | `wild_rose_concrete` | T2（437 为 T3） | NT4 | `materials/wild_rose_flower` + `n_hexane` |
| N129 | 甜小茴香油 | `sweet_fennel_oil` | T1 | NT1 | `materials/sweet_fennel_seed` |
| N130 | 甜叶菊油 | `stevia_oil` | T1 | NT1 | `materials/stevia` |
| N131 | 甜橙油 | `orange_oil` | T1 | NT1 | `materials/orange`（果皮水蒸气蒸馏；冷榨油见 N226） |
| N132 | 除萜甜橙油 | `terpeneless_orange_oil` | T2 | NT9 | N131 |

### 2.2 序号 131–260（N133–N266，130 条）

| 编码 | 名称 | 物料 ID | 阶段 | 模板 | 原料 |
| --- | --- | --- | --- | --- | --- |
| N133 | 甜橙油萜烯 | `orange_oil_terpenes` | T2 | NT9 | N226 |
| N134 | 菊苣浸膏 | `chicory_concrete` | T2 | NT4 | `materials/chicory_root` + `ethanol` |
| N135 | 晚香玉浸膏 | `tuberose_concrete` | T2（437 为 T3） | NT4 | `materials/tuberose_flower` + `n_hexane` |
| N136 | 紫罗兰叶净油/浸膏 | `violet_leaf_concrete` | T2（437 为 T3） | NT4 | `materials/violet_leaf` + `n_hexane`（本条只作浸膏；净油见 N301） |
| N137 | 椒样薄荷油 | `peppermint_oil` | T1 | NT1 | `materials/peppermint` |
| N138 | 黑加仑酊 | `blackcurrant_tincture` | T1 | NT3 | `materials/blackcurrant_bud` + `ethanol` |
| N139 | 黑加仑浸膏 | `blackcurrant_concrete` | T2（437 为 T3） | NT4 | `materials/blackcurrant_bud` + `n_hexane` |
| N140 | 槐树花净油 | `sophora_flower_absolute` | T2（N141 为 T3） | NT5 | N141 + `ethanol` |
| N141 | 槐树花浸膏 | `sophora_flower_concrete` | T2（437 为 T3） | NT4 | `materials/sophora_flower` + `n_hexane` |
| N142 | 辣椒酊 | `capsicum_tincture` | T1 | NT3 | `materials/dried_chili` + `ethanol` |
| N143 | 辣椒油树脂 | `paprika_oleoresin` | — | ST26 | 已有：367 |
| N144 | 愈疮木油 | `guaiac_wood_oil` | T1 | NT1 | `materials/guaiac_wood` |
| N145 | 缬草油 | `valerian_root_oil` | T1 | NT1 | `materials/valerian_root` |
| N146 | 墨红花净油 | `crimson_glory_rose_absolute` | T2（N147 为 T3） | NT5 | N147 + `ethanol` |
| N147 | 墨红花浸膏 | `crimson_glory_rose_concrete` | T2（437 为 T3） | NT4 | `materials/crimson_glory_rose` + `n_hexane` |
| N149 | 橙叶油 | `petitgrain_bigarade_oil` | T1 | NT1 | `materials/bitter_orange_leaf` |
| N150 | 亚洲薄荷油 | `mint_oil` | — | ST26 | 已有：`mint_oil`（29 A 中间物） |
| N151 | 亚洲薄荷素油 | `dementholized_mint_oil` | T2 | NT9 | `mint_oil` |
| N152 | 檀香油 | `sandalwood_oil` | T1 | NT1 | `materials/sandalwood` |
| N153 | 薰衣草油 | `lavender_oil` | T1 | NT1 | `materials/lavender` |
| N154 | 头状百里香油（西班牙牛至油） | `spanish_origanum_oil` | T1 | NT1 | `materials/conehead_thyme` |
| N155 | 可乐果提取物 | `kola_nut_extract` | T1 | NT7 | `materials/kola_nut` + `ethanol` |
| N156 | 加州胡椒油 | `schinus_molle_oil` | T1 | NT1 | `materials/peruvian_pepper_berry` |
| N157 | 卡黎皮油 | `cascarilla_bark_oil` | T1 | NT1 | `materials/cascarilla_bark` |
| N158 | 百里香油 | `thyme_oil` | T1 | NT1 | `materials/thyme` |
| N159 | 奶油发酵起子蒸馏物（黄油蒸馏物） | `starter_distillate` | T1 | NT13 | `starter_culture_broth`（M08 蒸馏收馏出液） |
| N160 | 卡南伽油 | `cananga_oil` | T1 | NT1 | `materials/cananga_flower` |
| N161 | 月桂叶提取物/油树脂 | `laurel_leaf_oleoresin` | T2 | NT6 / NT7 | `materials/laurel_leaf` + `ethanol` |
| N162 | 生姜提取物(生姜浸膏) | `ginger_extract` | T2 | NT4 | `materials/ginger` + `ethanol` |
| N163 | 白栎木屑提取物 | `oak_chips_extract` | T1 | NT7 | `materials/oak_log` + `ethanol` |
| N164 | 龙蒿油 | `estragon_oil` | T1 | NT1 | `materials/tarragon` |
| N165 | 白樟油 | `white_camphor_oil` | T2 | NT9 | `crude_camphor_oil` |
| N166 | 肉豆蔻衣油 | `mace_oil` | T1 | NT1 | `materials/mace` |
| N167 | 众香叶油 | `pimento_leaf_oil` | T1 | NT1 | `materials/pimento_leaf` |
| N168 | 西班牙鼠尾草油 | `spanish_sage_oil` | T1 | NT1 | `materials/spanish_sage` |
| N169 | 红橘油 | `tangerine_oil` | T1 | NT2 | `materials/tangerine` |
| N170 | 杂薰衣草油 | `lavandin_oil` | T1 | NT1 | `materials/lavandin` |
| N171 | 杏仁油 | `apricot_kernel_oil` | T1 | NT2 | `materials/apricot_kernel` |
| N172 | 苏合香油 | `styrax_oil` | T1 | NT1 | `materials/styrax` |
| N173 | 苏合香提取物 | `styrax_extract` | T1 | NT8 | `materials/styrax` + `ethanol` |
| N174 | 长角豆油 | `locust_bean_oil` | T2（437 为 T3） | NT4 | `materials/carob_pod` + `n_hexane` |
| N175 | 角豆提取物 | `carob_bean_extract` | T1 | NT7 | `materials/carob_pod` + `water` |
| N176 | 皂树皮提取物 | `quillaja_extract` | — | ST26 | 已有：269 |
| N177 | 乳香油 | `olibanum_oil` | T1 | NT1 | `materials/olibanum` |
| N178 | 没药油 | `myrrh_oil` | T1 | NT1 | `materials/myrrh` |
| N179 | 良姜根提取物 | `galangal_root_extract` | T1 | NT7 | `materials/galangal_root` + `ethanol` |
| N180 | 苏格兰松油 | `scotch_pine_oil` | T1 | NT1 | `materials/pine_needles` |
| N181 | 小茴香油（普通小茴香油） | `fennel_oil` | T1 | NT1 | `materials/fennel_seed` |
| N182 | 苦杏仁油 | `bitter_almond_oil` | T1 | NT12 | `materials/bitter_almond` + `slaked_lime` + `ferrous_sulfate` |
| N183 | 阿魏油 | `asafoetida_oil` | T1 | NT1 | `materials/asafoetida` |
| N184 | 金合欢净油 | `cassie_absolute` | T2（N066 为 T3） | NT5 | N066 + `ethanol` |
| N185 | 欧芹叶油 | `parsley_leaf_oil` | T1 | NT1 | `materials/parsley` |
| N186 | 松针油 | `pine_needle_oil` | T1 | NT1 | `materials/abies_needles` |
| N187 | 波罗尼花净油 | `boronia_absolute` | T2（437 为 T3） | NT5 | `materials/boronia_flower` + `n_hexane` + `ethanol` |
| N188 | 玫瑰木油 | `bois_de_rose_oil` | T1 | NT1 | `materials/rosewood` |
| N189 | 玫瑰草油 | `palmarosa_oil` | T1 | NT1 | `materials/palmarosa` |
| N190 | 香茅油 | `citronella_oil` | T1 | NT1 | `materials/citronella_grass` |
| N191 | 迷迭香油 | `rosemary_oil` | T1 | NT1 | `materials/rosemary` |
| N192 | 香脂冷杉油 | `balsam_fir_oil` | T1 | NT1 | `materials/fir_needles` |
| N193 | 香脂冷杉油树脂 | `balsam_fir_oleoresin` | T1 | NT8 | `materials/canada_balsam` |
| N194 | 胡萝卜籽油 | `carrot_seed_oil` | T1 | NT1 | `materials/carrot_seed` |
| N195 | 春黄菊花油(罗马) | `roman_chamomile_oil` | T1 | NT1 | `materials/roman_chamomile` |
| N196 | 春黄菊花净油(提取物)(罗马) | `roman_chamomile_absolute` | T2（437 为 T3） | NT5 | `materials/roman_chamomile` + `n_hexane` + `ethanol` |
| N197 | 药鼠李提取物 | `cascara_bitterless_extract` | T1 | NT7 | `materials/cascara_bark` + `water` |
| N198 | 荜澄茄油 | `cubeb_oil` | T1 | NT1 | `materials/cubeb` |
| N199 | 胡薄荷油（唇萼薄荷油） | `pennyroyal_oil` | T1 | NT1 | `materials/pennyroyal` |
| N200 | 欧当归油 | `lovage_oil` | T1 | NT1 | `materials/lovage_root` |
| N201 | 夏至草提取物 | `horehound_extract` | T1 | NT7 | `materials/horehound` + `water` |
| N202 | 莫哈弗丝兰提取物 | `yucca_extract` | T1 | NT7 | `materials/yucca_trunk` + `water` |
| N203 | 海草(藻)提取物 | `kelp_extract` | T1 | NT7 | `materials/kelp` + `water` |
| N204 | 海索草油 | `hyssop_oil` | T1 | NT1 | `materials/hyssop` |
| N205 | 莳萝草油（莳萝油） | `dill_herb_oil` | T1 | NT1 | `materials/dill` |
| N206 | 秘鲁香脂 | `peru_balsam` | T1 | NT8 | `materials/crude_peru_balsam` + `water` |
| N207 | 格蓬油 | `galbanum_oil` | T1 | NT1 | `materials/galbanum` |
| N208 | 脂檀油 | `amyris_oil` | T1 | NT1 | `materials/amyris_wood` |
| N209 | 银白金合欢净油（含羞草净油） | `mimosa_absolute` | T2（437 为 T3） | NT5 | `materials/mimosa_flower` + `n_hexane` + `ethanol` |
| N210 | 接骨木花净油 | `elderflower_absolute` | T2（437 为 T3） | NT5 | `materials/elderflower` + `n_hexane` + `ethanol` |
| N211 | 甘牛至油 | `sweet_marjoram_oil` | T1 | NT1 | `materials/marjoram` |
| N212 | 黄龙胆根提取物 | `gentian_root_extract` | T1 | NT7 | `materials/gentian_root` + `ethanol` |
| N213 | 黄葵籽油 | `ambrette_seed_oil` | T1 | NT1 | `materials/ambrette_seed` |
| N214 | 野黑樱桃树皮提取物 | `wild_cherry_bark_extract` | T1 | NT7 | `materials/wild_cherry_bark` + `water` |
| N215 | 黑胡椒油 | `black_pepper_oil` | T1 | NT1 | `materials/black_pepper` |
| N216 | 葛缕籽油 | `caraway_seed_oil` | T1 | NT1 | `materials/caraway` |
| N217 | 榄香香树脂 | `elemi_resinoid` | T1 | NT8 | `materials/elemi` + `ethanol` |
| N218 | 蜡菊提取物 | `immortelle_extract` | T1 | NT7 | `materials/immortelle_flower` + `ethanol` |
| N219 | 香蜂花油 | `balm_oil` | T1 | NT1 | `materials/lemon_balm` |
| N220 | d-樟脑 | `d_camphor` | T2 | NT9 | `crude_camphor_oil` |
| N221 | 橙花净油 | `orange_flower_absolute` | T2（437 为 T3） | NT5 | `materials/bitter_orange_flower` + `n_hexane` + `ethanol` |
| N222 | 柚苷（柚皮甙提取物） | `naringin` | T2 | ST21 | `materials/grapefruit` + `water` |
| N223 | 穗薰衣草油 | `spike_lavender_oil` | T1 | NT1 | `materials/spike_lavender` |
| N224 | 鹰爪豆净油 | `genet_absolute` | T2（437 为 T3） | NT5 | `materials/genet_flower` + `n_hexane` + `ethanol` |
| N225 | 玳玳果皮油 | `daidai_peel_oil` | T1 | NT2 | `materials/daidai_fruit`（果皮冷榨） |
| N226 | 甜橙油(橙皮压榨法) | `cold_pressed_orange_oil` | T1 | NT2 | `materials/orange` |
| N227 | 小米辣椒油树脂 | `bush_red_pepper_oleoresin` | T2 | NT6 | `materials/bird_eye_chili` + `acetone` |
| N228 | 丁香茎油 | `clove_stem_oil` | T1 | NT1 | `materials/clove_stem` |
| N229 | 大茴香油（茴芹油） | `anise_oil` | T1 | NT1 | `materials/anise_seed` |
| N230 | L-天冬酰胺 | `l_asparagine` | T2 | ST21 | `materials/potato` |
| N231 | 巴拉圭茶提取物 | `mate_extract` | T2 | NT7 | `materials/mate_leaf` + `ethanol`（稀乙醇浸提；GB 名称含净油与提取物两种形态，取浸提物） |
| N232 | 白山核桃树皮提取物 | `hickory_bark_extract` | T1 | NT7 | `materials/hickory_bark` + `ethanol` |
| N233 | 瓜拉纳提取物 | `guarana_extract` | T1 | NT7 | `materials/guarana_seed` + `ethanol` |
| N235 | 白百里香油 | `white_thyme_oil` | T2 | NT9 | N158 |
| N236 | 白胡椒油 | `white_pepper_oil` | T1 | NT1 | `materials/white_pepper` |
| N237 | 白胡椒油树脂 | `white_pepper_oleoresin` | T2 | NT6 | `materials/white_pepper` + `acetone` |
| N238 | 白康酿克油 | `white_cognac_oil` | T2 | NT9 | N017 |
| N239 | 白脱酯 | `butter_esters` | T2 | ST01 | N240 + `ethanol` |
| N240 | 白脱酸 | `butter_acids` | T1 | NT15 | `foods/butter` + `sodium_hydroxide` + `sulfuric_acid` |
| N241 | 众香果油 | `pimenta_oil` | T1 | NT1 | `materials/allspice` |
| N242 | 安息香树脂 | `benzoin_resinoid` | T1 | NT8 | `materials/benzoin` + `ethanol` |
| N243 | 当归籽油 | `angelica_seed_oil` | T1 | NT1 | `materials/angelica_seed` |
| N244 | 当归根油 | `angelica_root_oil` | T1 | NT1 | `materials/angelica_root` |
| N245 | 肉豆蔻衣油树脂/提取物 | `mace_oleoresin` | T2 | NT6 / NT7 | `materials/mace` + `acetone` |
| N246 | 西印度月桂叶提取物 | `west_indian_bay_extract` | T1 | NT7 | `materials/west_indian_bay_leaf` + `ethanol` |
| N247 | 西印度月桂叶油 | `west_indian_bay_oil` | T1 | NT1 | `materials/west_indian_bay_leaf` |
| N248 | L-阿拉伯糖 | `l_arabinose` | T2 | NT16 | 288 + `sulfuric_acid` + `slaked_lime` |
| N249 | 阿拉伯胶 | `gum_arabic` | — | ST26 | 已有：288 |
| N250 | 欧当归提取物 | `lovage_extract` | T1 | NT7 | `materials/lovage_root` + `ethanol` |
| N251 | 欧芹油树脂 | `parsley_oleoresin` | T2 | NT6 | `materials/parsley` + `acetone` |
| N252 | 油酸 | `oleic_acid` | — | ST26 | 已有：`oleic_acid` |
| N253 | 苦木提取物 | `quassia_extract` | T1 | NT7 | `materials/quassia_wood` + `water` |
| N254 | 苦橙叶净油 | `orange_leaf_absolute` | T2（437 为 T3） | NT5 | `materials/bitter_orange_leaf` + `n_hexane` + `ethanol` |
| N255 | 苦橙油 | `bitter_orange_oil` | T1 | NT2 | `materials/bitter_orange` |
| N256 | 金鸡纳树皮 | `cinchona_bark_powder` | T1 | NT14 | `materials/cinchona_bark` |
| N257 | 金钮扣油树脂 | `jambu_oleoresin` | T2 | NT6 | `materials/jambu` + `ethanol` |
| N258 | 奎宁盐酸盐 | `quinine_hydrochloride` | T2 | NT17 | `materials/cinchona_bark` + `slaked_lime` + `toluene` + `hydrochloric_acid` |
| N260 | 洋葱油树脂 | `onion_oleoresin` | T2 | NT6 | `materials/onion` + `ethanol` |
| N261 | 茶树油（互叶白千层油） | `tea_tree_oil` | T1 | NT1 | `materials/tea_tree_leaf` |
| N262 | 除萜白柠檬油 | `terpeneless_lime_oil` | T2 | NT9 | N034（白柠檬油去萜） |
| N263 | 除萜甜橙皮油 | `terpeneless_orange_peel_oil` | T2 | NT9 | N226 |
| N265 | 黄芥末提取物/黄芥末油树脂 | `yellow_mustard_oleoresin` | T2 | NT6 / NT7 | `materials/yellow_mustard_seed` + `ethanol` |
| N266 | 棕芥末提取物 | `brown_mustard_extract` | T1 | NT7 | `materials/brown_mustard_seed` + `water` |

### 2.3 序号 261–388（N267–N404，126 条）

| 编码 | 名称 | 物料 ID | 阶段 | 模板 | 原料 |
| --- | --- | --- | --- | --- | --- |
| N267 | 焦木酸 | `refined_wood_vinegar` | T2 | NT11 | `wood_vinegar` + `catalysts/activated_carbon`（M14：静置分出木焦油 `wood_tar` 作副产出，活性炭吸附多环芳烃，P24；木醋液不带纯度，香料级另设物料，2026-11-12） |
| N268 | 紫苏油 | `perilla_leaf_oil` | T1 | NT1 | `materials/perilla_leaf` |
| N269 | 葡萄柚油萜烯 | `grapefruit_oil_terpenes` | T2 | NT9 | N051 |
| N270 | 黑胡椒油树脂/黑胡椒提取物 | `black_pepper_oleoresin` | T2 | NT6 | `materials/black_pepper` + `acetone` |
| N271 | 榄香油/提取物/香树脂 | `elemi_oil` | T1 | NT1（香树脂即 N217） | `materials/elemi` |
| N272 | 蜂蜡净油 | `beeswax_absolute` | T2 | NT5 | `beeswax` + `ethanol` |
| N273 | 赖百当净油（岩蔷薇净油） | `labdanum_absolute` | T2（N063 为 T3） | NT5 | N063 + `ethanol` |
| N274 | 鼠尾草油（药鼠尾草油） | `sage_oil` | T1 | NT1 | `materials/sage` |
| N275 | 蜡菊净油 | `helichrysum_absolute` | T2（`helichrysum_concrete` 为 T4） | NT5 | `helichrysum_concrete` + `ethanol` |
| N276 | 糖蜜提取物 | `molasses_extract` | T1 | NT7 | `cane_molasses` + `ethanol` |
| N277 | 檀香醇(包括α-檀香醇、β-檀香醇) | `santalol` | T2 | NT9 | N152 |
| N278 | 山达草流浸膏 | `yerba_santa_fluid_extract` | T1 | NT7 | `materials/yerba_santa` + `ethanol` |
| N279 | 苜蓿提取物 | `alfalfa_extract` | T1 | NT7 | `materials/alfalfa` + `ethanol` |
| N281 | 众香子油树脂/提取物 | `allspice_oleoresin` | T2 | NT6 | `materials/allspice` + `acetone` |
| N282 | 黄葵籽净油 | `ambrette_seed_absolute` | T2（`ambrette_seed_concrete` 为 T4） | NT5 | `ambrette_seed_concrete` + `ethanol` |
| N283 | 秘鲁香膏油 | `peru_balsam_oil` | T2 | NT9 | N206 |
| N284 | 罗勒提取物 | `basil_extract` | T1 | NT7 | `materials/basil` + `ethanol` |
| N285 | 芹菜籽固体提取物 | `celery_seed_extract_solid` | T2 | NT7 | `materials/celery_seed` + `ethanol` |
| N286 | 芹菜籽(含 CO2)提取物 | `celery_seed_co2_extract` | T4 | NT6 | `materials/celery_seed` + `carbon_dioxide` |
| N287 | 母菊(匈牙利春黄菊)花油 | `hungarian_chamomile_oil` | T1 | NT1 | `materials/german_chamomile` |
| N288 | 黄色金鸡纳树皮提取物 | `cinchona_bark_extract` | T1 | NT7 | `materials/cinchona_bark` + `ethanol` |
| N289 | 丁香花蕾油树脂 | `clove_bud_oleoresin` | T2 | NT6 | `materials/clove_bud` + `acetone` |
| N290 | 红三叶草提取物(固体) | `red_clover_extract_solid` | T2 | NT7 | `materials/red_clover` + `ethanol` |
| N291 | 蒲公英流浸膏 | `dandelion_fluid_extract` | T1 | NT7 | `materials/dandelion` + `ethanol` |
| N292 | 蒲公英根固体提取物 | `dandelion_root_extract_solid` | T2 | NT7 | `materials/dandelion` + `water` |
| N293 | 加拿大飞蓬草油 | `fleabane_oil` | T1 | NT1 | `materials/canadian_fleabane` |
| N294 | 穗花槭提取物(固体) | `mountain_maple_extract_solid` | T2 | NT7 | `materials/mountain_maple_bark` + `water` |
| N295 | 芸香油 | `rue_oil` | T1 | NT1 | `materials/rue` |
| N296 | 鼠尾草油树脂/提取物 | `sage_oleoresin` | T2 | NT6 | `materials/sage` + `acetone` |
| N297 | 菝葜提取物 | `sarsaparilla_extract` | T1 | NT7 | `materials/sarsaparilla_root` + `ethanol` |
| N298 | 水蒸气蒸馏松节油 | `turpentine` | T1 | NT1 | `resin`（与 137 松香同一配方：松节油随蒸汽馏出、分油器分出，松香留在釜里，2026-11-12） |
| N299 | 缬草根提取物 | `valerian_root_extract` | T1 | NT7 | `materials/valerian_root` + `ethanol` |
| N300 | 香荚兰油树脂 | `vanilla_oleoresin` | T2 | NT6 | `materials/cured_vanilla_bean` + `acetone` |
| N301 | 紫罗兰叶净油 | `violet_leaf_absolute` | T2（N136 为 T3） | NT5 | N136 + `ethanol` |
| N302 | 洋艾油 | `wormwood_oil` | T1 | NT1 | `materials/wormwood` |
| N304 | 橘柚油 | `tangelo_oil` | T1 | NT2 | `materials/tangelo` |
| N305 | 晚香玉净油 | `tuberose_absolute` | T2（N135 为 T3） | NT5 | N135 + `ethanol` |
| N306 | 美国栗树叶提取物 | `chestnut_leaf_extract` | T1 | NT7 | `materials/chestnut_leaf` + `water` |
| N307 | 古巴香脂油 | `copaiba_oil` | T1 | NT1 | `materials/copaiba_balsam` |
| N308 | 达迷草叶 | `damiana_leaf_powder` | T1 | NT14 | `materials/damiana_leaf` |
| N309 | 母菊(匈牙利春黄菊)花净油 | `hungarian_chamomile_absolute` | T2（`hungarian_chamomile_concrete` 为 T4） | NT5 | `hungarian_chamomile_concrete` + `ethanol` |
| N310 | 接骨木花提取物 | `elderflower_extract` | T1 | NT7 | `materials/elderflower` + `ethanol` |
| N311 | 防风根油 | `opoponax_oil` | T1 | NT1 | `materials/opoponax` |
| N312 | 藏红花提取物 | `saffron_extract` | T1 | NT7 | `materials/saffron` + `ethanol` |
| N313 | 香叶提取物 | `geranium_extract` | T1 | NT7 | `materials/rose_geranium_leaf` + `ethanol` |
| N314 | 葫芦巴油树脂 | `fenugreek_oleoresin` | T2 | NT6 | `materials/fenugreek_seed` + `acetone` |
| N315 | 柠檬提取物 | `lemon_extract` | T1 | NT3 | `materials/lemon` + `ethanol` |
| N316 | 德国鸢尾树脂 | `orris_resinoid` | T2 | NT4 | `materials/orris_root` + `acetone` |
| N317 | 罗望子提取物(浸膏) | `tamarind_extract` | T1 | NT7 | `materials/tamarind_pulp` + `water` |
| N318 | 辣根油 | `horseradish_oil` | T1 | NT12 | `materials/horseradish_root` |
| N319 | 葫芦巴籽浸膏 | `fenugreek_seed_extract` | T2 | NT4 | `materials/fenugreek_seed` + `ethanol` |
| N320 | 芹菜叶油 | `celery_leaf_oil` | T1 | NT1 | `materials/celery_leaf` |
| N321 | 柏木油萜烯 | `cedarwood_oil_terpenes` | T2 | NT9 | `cedarwood_oil`（M22 切取萜烯馏分） |
| N322 | 肉豆蔻油树脂 | `nutmeg_oleoresin` | T2 | NT6 | `materials/nutmeg` + `acetone` |
| N324 | 芫荽油/油树脂 | `coriander_oleoresin` | T2 | NT6（油即 N047） | `materials/coriander_seed` + `acetone` |
| N326 | 韭葱油 | `leek_oil` | T1 | NT12 | `materials/leek` |
| N327 | 甜橙皮提取物 | `sweet_orange_peel_extract` | T1 | NT3 | `materials/orange` + `ethanol` |
| N329 | 香橙皮油 | `yuzu_peel_oil` | T1 | NT2 | `materials/yuzu` |
| N330 | 海藻净油 | `algues_absolute` | T2（`algues_concrete` 为 T4） | NT5 | `algues_concrete` + `ethanol` |
| N331 | 墨西哥鼠尾草油树脂（棘枝油树脂） | `mexican_oregano_oleoresin` | T2 | NT6 | `materials/mexican_oregano` + `acetone` |
| N332 | 甘草酸铵 | `ammonium_glycyrrhizinate` | — | ST26 | 已有：353 |
| N333 | 冬香草油 | `winter_savory_oil` | T1 | NT1 | `materials/winter_savory` |
| N334 | 苏合香 | `styrax` | T1 | NT8 | `materials/styrax` |
| N335 | 阿魏液态提取物(流浸膏) | `asafoetida_fluid_extract` | T1 | NT7 | `materials/asafoetida` + `ethanol` |
| N336 | 桃树叶净油 | `peach_leaf_absolute` | T2（`peach_leaf_concrete` 为 T4） | NT5 | `peach_leaf_concrete` + `ethanol` |
| N337 | 白藓牛至 | `dittany_of_crete_powder` | T1 | NT14 | `materials/dittany_of_crete` |
| N338 | 酒花油 | `hop_oil` | T1 | NT1 | `materials/hops` |
| N339 | 赖百当油 | `labdanum_oil` | T1 | NT1 | `materials/rockrose_twig` |
| N340 | 薰衣草净油 | `lavender_absolute` | T2（`lavender_concrete` 为 T4） | NT5 | `lavender_concrete` + `ethanol` |
| N341 | 没药树脂提取物 | `opoponax_resinoid` | T2 | NT4 | `materials/opoponax` + `ethanol` |
| N342 | 花椒提取物 | `prickly_ash_extract` | T2 | NT6 | `materials/sichuan_pepper` + `acetone` |
| N344 | 儿茶粉 | `catechu_powder` | T2 | NT7 | `materials/catechu_heartwood` + `water` |
| N345 | 苦艾 | `wormwood_powder` | T1 | NT14 | `materials/wormwood` |
| N346 | 苦橙花油 | `daidai_flower_oil` | — | ST26 | 见 N080（FEMA 同为 2771，与 N080 共用物料 `daidai_flower_oil`） |
| N347 | 达瓦树胶 | `ghatti_gum` | — | ST26 | 已有：270 |
| N348 | 苦艾提取物 | `wormwood_extract` | T1 | NT7 | `materials/wormwood` + `ethanol` |
| N349 | 刺柏提取物 | `juniper_extract` | T1 | NT7 | `materials/juniper_berry` + `ethanol` |
| N350 | 甘草提取物(粉) | `licorice_extract_powder` | T2 | NT7 | N027 |
| N351 | 甜菜碱(天然提取) | `betaine` | T3 | ST21 | `beet_molasses` + `catalysts/ion_exchange_resin`（甜菜碱来自甜菜糖蜜，2026-11-12） |
| N352 | 松焦油 | `pine_tar_oil` | T2 | NT9 | `wood_tar` |
| N353 | 橡苔净油 | `oakmoss_absolute` | T2（`oakmoss_concrete` 为 T4） | NT5 | `oakmoss_concrete` + `ethanol` |
| N354 | 苏格兰留兰香油 | `scotch_spearmint_oil` | T1 | NT1 | `materials/scotch_spearmint` |
| N355 | 海索草提取物（神香草提取物） | `hyssop_extract` | T1 | NT7 | `materials/hyssop` + `ethanol` |
| N356 | 安古树皮提取物 | `angostura_extract` | T1 | NT7 | `materials/angostura_bark` + `ethanol` |
| N357 | 德国春黄菊花(母菊花)提取物 | `german_chamomile_extract` | T1 | NT7 | `materials/german_chamomile` + `ethanol` |
| N359 | L-苏氨酸 | `l_threonine` | T4 | NT10 | `glucose` + `ammonia` + `cultures/e_coli_threonine` |
| N360 | L-丝氨酸 | `l_serine` | T4 | NT10 | `glycine` + `formaldehyde` + `cultures/e_coli` |
| N361 | 灵猫净油 | `civet_absolute` | T2（`civet_concrete` 为 T4） | NT5 | `civet_concrete` + `ethanol` |
| N362 | 胭脂树提取物 | `bixin` | — | ST26 | 已有：232 |
| N363 | 卡黎皮提取物 | `cascarilla_bark_extract` | T1 | NT7 | `materials/cascarilla_bark` + `ethanol` |
| N364 | 肉桂皮油/油树脂 | `cinnamon_bark_oleoresin` | T2 | NT6（油即 126 A 桂皮油、N116） | `materials/cinnamon` + `acetone` |
| N365 | 刺梧桐树胶 | `karaya_gum` | — | ST26 | 已有：283 |
| N366 | 橘叶油 | `petitgrain_mandarin_oil` | T1 | NT1 | `materials/mandarin_leaf` |
| N367 | 欧洲山松针叶油 | `dwarf_pine_needle_oil` | T1 | NT1 | `materials/dwarf_pine_needles` |
| N368 | 玫瑰果籽提取物 | `rose_hip_extract` | T1 | NT7 | `materials/rose_hip` + `water` |
| N369 | 夏香草油 | `summer_savory_oil` | T1 | NT1 | `materials/summer_savory` |
| N370 | 加拿大细辛油 | `canadian_snakeroot_oil` | T1 | NT1 | `materials/canadian_snakeroot` |
| N372 | 黄蓍胶 | `tragacanth_gum` | T1 | NT8 | `materials/tragacanth_lump` |
| N373 | 甘牛至油树脂/提取物 | `marjoram_oleoresin` | T2 | NT6 | `materials/marjoram` + `acetone` |
| N374 | 摩洛哥豆蔻提取物 | `grains_of_paradise_extract` | T2 | NT6 | `materials/grains_of_paradise` + `acetone` |
| N375 | 橙皮素 | `hesperetin` | T4 | NT10 | `hesperidin` + `enzymes/naringinase` |
| N377 | 芝麻(CO2)提取物 | `sesame_co2_extract` | T4 | NT6 | `materials/sesame` + `carbon_dioxide` |
| N378 | 芝麻蒸馏物 | `sesame_distillate` | T1 | NT1 | `materials/sesame` |
| N379 | 干制鲣鱼(CO2)提取物 | `katsuobushi_co2_extract` | T4 | NT6 | `materials/katsuobushi` + `carbon_dioxide` |
| N380 | 朗姆酒净油 | `rum_absolute` | T2（`rum_concrete` 为 T4） | NT5 | `rum_concrete` + `ethanol` |
| N381 | 豆豉油树脂 | `douchi_oleoresin` | T2 | NT6 | `foods/douchi` + `acetone` |
| N382 | 药蜀葵 | `althea_root_powder` | T1 | NT14 | `materials/marshmallow_root` |
| N383 | 香蜂草 | `lemon_balm_powder` | T1 | NT14 | `materials/lemon_balm` |
| N384 | 白千层油 | `cajeput_oil` | T1 | NT1 | `materials/cajeput_leaf` |
| N388 | 毕澄茄 | `cubeb_powder` | T1 | NT14 | `materials/cubeb` |
| N389 | 芦荟提取物 | `aloe_extract` | T1 | NT7 | `materials/aloe` + `water` |
| N390 | 龙涎香酊 | `ambergris_tincture` | T1 | NT3 | `materials/ambergris` + `ethanol` |
| N391 | 黄葵酊 | `ambrette_tincture` | T1 | NT3 | `materials/ambrette_seed` + `ethanol` |
| N392 | 燕根(萝藦科植物)提取物 | `swallowroot_extract` | T1 | NT7 | `materials/swallowroot` + `ethanol` |
| N393 | 红枣浸膏 | `jujube_concrete` | T2 | NT4 | `materials/jujube` + `ethanol` |
| N394 | 高倍天然苹果香料 | `folded_apple_essence` | T2 | NT18 | `foods/apple_juice`（12 成品苹果汁） |
| N395 | β-愈疮木烯 | `beta_guaiene` | T2 / T3（B 为 T3） | A NT9；B ST14 | A N007；B N144 |
| N396 | 褐藻胶 | `sodium_alginate` | — | ST26 | 已有：15 |
| N397 | 香厚壳桂皮油 | `massoia_bark_oil` | T1 | NT1 | `materials/massoia_bark` |
| N398 | (—)-高圣草酚钠盐 | `homoeriodictyol_sodium` | T2（`homoeriodictyol` 为 T4） | ST17 | `homoeriodictyol` + `sodium_hydroxide` |
| N399 | 酶处理异槲皮苷 | `enzymatically_modified_isoquercitrin` | T4 | NT10 | `rutin` + `enzymes/naringinase` + `starch` + `enzymes/cgtase` |
| N400 | 葡萄籽提取物 | `grape_seed_extract` | T2 | NT7 | `materials/grape_pomace` + `ethanol` + `catalysts/macroporous_resin` |
| N401 | 留兰香提取物 | `spearmint_extract` | T1 | NT7 | `materials/spearmint` + `ethanol` |
| N402 | 杂醇油(精制过) | `fusel_oil` | T2 | NT9 | `crude_fusel_oil` |
| N403 | 葡萄糖基甜菊糖苷 | `glucosyl_steviol_glycosides` | T4 | NT10 | `steviol_glycosides` + `starch` + `enzymes/cgtase` |
| N404 | 非洲竹芋提取物 | `katemfe_fruit_extract` | T1 | NT7 | `materials/katemfe_fruit` + `water` |

## 3 已有的香料（不在本批设计）

| 编码 | 名称 | 已有 |
| --- | --- | --- |
| N018 | 天然薄荷脑 | 29 号 |
| S0066 | 薄荷脑 | 29 号 |
| S0098 | 麦芽酚 | 123 号 |
| S0165 | 苯甲醛 | 127 号 |
| S0172 | 香兰素 | 30 号 |
| S0174 | 柠檬醛 | 125 号 |
| S0176 | 肉桂醛 | 126 号 |
| S0276 | 4-羟基-2,5-二甲基-3(2H)-呋喃酮 | 131 号 |
| S0372 | 乙酸异戊酯 | 128 号 |
| S0414 | 丁酸乙酯 | 129 号 |
| S0628 | γ-癸内酯 | 130 号 |
| S0713 | 2,3,5-三甲基吡嗪 | 132 号 |
| S1133 | d-香芹酮 | 133 号 |
| S1162 | 乙基麦芽酚 | 124 号 |
| S1171 | 乙基香兰素 | 122 号 |
| N343 | 蓖麻油 | `castor_oil` |
| N371 | 单宁酸 | `tannin` |
| S0001 | 丙二醇 | 78 号 |
| S0002 | 甘油（丙三醇） | 78 号 |
| S0008 | 异戊醇 | `isoamyl_alcohol` |
| S0049 | 正丙醇 | `n_propanol` |
| S0076 | d-木糖 | `xylose` |
| S0094 | 对-甲酚 | `p_cresol` |
| S0096 | 间-甲酚 | `m_cresol` |
| S0097 | 百里香酚（麝香草酚） | `thymol` |
| S0099 | 苯酚 | `phenol` |
| S0104 | 愈疮木酚 | `guaiacol` |
| S0115 | 乙醛 | `acetaldehyde` |
| S0180 | 糠醛 | `furfural` |
| S0207 | 丁二酮2,3-丁二酮 | `diacetyl` |
| S0212 | 甲基环戊烯醇酮 | `methyl_cyclopentenolone` |
| S0237 | 薄荷酮 | `menthone` |
| S0247 | 丙酮 | `acetone` |
| S0293 | 乙酸 | 1 号 |
| S0294 | 丙酸 | `propionic_acid` |
| S0296 | 丁酸 | `butyric_acid` |
| S0315 | 苯甲酸 | `benzoic_acid` |
| S0317 | 柠檬酸 | 3 号 |
| S0319 | 富马酸 | 57 号 |
| S0338 | 乳酸 | 2 号 |
| S0352 | 硬脂酸 | `stearic_acid` |
| S0797 | 牛磺酸 | 120 号 |
| S0851 | 乙酸钠（醋酸钠） | `sodium_acetate` |
| S0852 | 双乙酸钠（二醋酸钠） | 33 号 |
| S0856 | 磷酸三钙 | 49 号 |
| S0929 | 多聚磷酸钠(包括六偏磷酸钠) | 52 号 |
| S1358 | 松香酸甘油酯 | ≈137（近似，设计者待定） |

## 4 新增原料

天然香料新增原料 217 种，已并入 08 第 C 节“第五批新增原料”。“暂无来源”的原料暂时没有获取途径，对应路线照写，纯度报表把它们单列为“原料暂无来源”（I30 约定）。

| 原料 | 配方引用标签 | 现实来源 | 本模组来源 | 用于 |
| --- | --- | --- | --- | --- |
| 丁香叶 | `materials/clove_leaf` | 丁香 Syzygium aromaticum（Eugenia spp.）叶；印度尼西亚、马达加斯加 | 暂无来源 | N001 |
| 丁香花蕾 | `materials/clove_bud` | 丁香未开放花蕾，晒干；坦桑尼亚桑给巴尔、印度尼西亚、马达加斯加 | 暂无来源 | N002、N003 |
| 罗勒 | `materials/basil` | 罗勒 Ocimum basilicum 花期地上部分；埃及、印度、法国 | 暂无来源 | N004 |
| 八角 | `materials/star_anise` | 八角 Illicium verum 成熟果实；广西、云南 | 暂无来源 | N005 |
| 九里香花 | `materials/jasmin_orange_flower` | 九里香 Murraya paniculata 花；广东、广西、福建 | 暂无来源 | N006 |
| 广藿香叶 | `materials/patchouli_leaf` | 广藿香 Pogostemon cablin 叶（晒干、堆放发酵）；印度尼西亚、广东 | 暂无来源 | N007 |
| 小豆蔻 | `materials/cardamom` | 小豆蔻 Elettaria cardamomum（原表作 Elletaria）果实；印度、危地马拉 | 暂无来源 | N010、N011 |
| 甜茴香籽 | `materials/sweet_fennel_seed` | 甜茴香 Foeniculum vulgare var. dulce 果实；法国、意大利 | 暂无来源 | N129 |
| 山楂 | `materials/hawthorn_fruit` | 山楂 Crataegus pinnatifida 等果实；山东、河北、辽宁 | 暂无来源 | N014 |
| 大蒜 | `materials/garlic` | 蒜 Allium sativum 鳞茎；山东金乡、河南 | 暂无来源 | N015、N016 |
| 葡萄酒酒脚 | `materials/wine_lees` | 葡萄酒发酵、澄清后沉在罐底的酵母与沉渣 | 葡萄酒（`wine`）澄清的沉渣，与酒石同出（H14 冷置、M04 澄清配方补此副产） | N017 |
| 云木香根 | `materials/costus_root` | 云木香 Saussurea costus（S. lappa C.B.Clarke，原表作 Saussures lappa Clanke）根；云南丽江、印度克什米尔 | 暂无来源 | N019 |
| 乌梅 | `materials/smoked_plum` | 梅 Prunus mume 近成熟果实低温烘焙至乌黑；四川、福建、浙江 | 暂无来源（12 酸梅汤同用） | N021 |
| 布枯叶 | `materials/buchu_leaf` | 布枯 Agathosma betulina（Barosma betulina）叶；南非西开普 | 暂无来源 | N022 |
| 可可碎粒 | `materials/cocoa_nibs` | 焙炒可可豆去壳后的子叶 | 可可豆（`materials/cocoa_beans`）焙炒（熔炉 / M17）、M11 破碎风选的主产物，可可豆壳为副产（同 216） | N023 |
| 甘松根 | `materials/nardostachys_root` | 甘松 Nardostachys chinensis 根及根茎；四川、甘肃、青海 | 暂无来源 | N025 |
| 冬青叶 | `materials/wintergreen_leaf` | 平铺白珠树 Gaultheria procumbens 叶；北美东部 | 暂无来源 | N028 |
| 白兰花 | `materials/michelia_alba_flower` | 白兰 Michelia × alba 花；广东、福建、广西 | 暂无来源 | N029、N032（经 N032 供 N031） |
| 白兰叶 | `materials/michelia_alba_leaf` | 白兰叶；广东 | 暂无来源 | N030 |
| 白芷根 | `materials/angelica_dahurica_root` | 白芷 Angelica dahurica 根；四川遂宁、浙江、河南 | 暂无来源 | N033 |
| 肉豆蔻 | `materials/nutmeg` | 肉豆蔻 Myristica fragrans 种仁；印度尼西亚、格林纳达 | 暂无来源 | N037、N038 |
| 红茶 | `materials/black_tea` | 茶 Camellia sinensis 鲜叶经萎凋、揉捻、发酵、干燥 | 茶叶（`materials/tea`）萎凋、揉捻后陶坛通气发酵（同 329 A 前段）、M10 烘干（待加配方） | N041 |
| 印蒿 | `materials/davana_herb` | 印蒿 Artemisia pallens 花期地上部分；印度卡纳塔克 | 暂无来源 | N042 |
| 吐鲁香脂 | `materials/tolu_balsam` | 吐鲁香树 Myroxylon balsamum 树干割伤流出的香脂；哥伦比亚、委内瑞拉 | 暂无来源 | N043、N044 |
| 豆豉 | `foods/douchi` | 黑豆蒸煮、制曲后加盐发酵 | 12 成品豆豉（`douchi`）；`foods/douchi` 标签待生成 | N045 |
| 杜松子 | `materials/juniper_berry` | 欧洲刺柏 Juniperus communis 球果；意大利、克罗地亚 | 暂无来源 | N046 |
| 芫荽籽 | `materials/coriander_seed` | 芫荽 Coriandrum sativum 果实；俄罗斯、乌克兰、摩洛哥 | 暂无来源 | N047 |
| 芹菜花 | `materials/celery_flower` | 旱芹 Apium graveolens 花期地上部分（花序） | 暂无来源 | N048 |
| 芹菜籽 | `materials/celery_seed` | 旱芹果实；印度、法国 | 暂无来源 | N049 |
| 牡荆叶 | `materials/vitex_leaf` | 牡荆 Vitex negundo var. cannabifolia 叶；华东、华南 | 暂无来源 | N050 |
| 苍术 | `materials/atractylodes_rhizome` | 茅苍术 Atractylodes lancea 根茎；江苏茅山、湖北 | 暂无来源 | N052 |
| 枣 | `materials/jujube` | 枣 Ziziphus jujuba 成熟果实（干）；新疆、河北、山东 | 暂无来源 | N053 |
| 玫瑰花 | `materials/rose_flower` | 大马士革玫瑰 Rosa damascena、玫瑰 Rosa rugosa 鲜花；保加利亚、土耳其、山东平阴、甘肃苦水 | 原版 `minecraft:rose_bush`（游戏简化；与 N2 `materials/crimson_glory_rose` 重叠，见存疑） | N054、N056（经 N056 供 N055） |
| 鸢尾根 | `materials/orris_root` | 德国鸢尾 Iris germanica、香根鸢尾 I. pallida、I. florentina 根茎，干燥陈化 2–3 年；意大利托斯卡纳、摩洛哥 | 暂无来源 | N057、N058 |
| 杭白菊 | `materials/chrysanthemum_flower` | 菊 Chrysanthemum morifolium（杭白菊）头状花序；浙江桐乡 | 暂无来源 | N059、N060 |
| 枫糖浆 | `materials/maple_syrup` | 糖槭 Acer saccharum 等树液熬浓的糖浆；加拿大魁北克、美国东北 | 暂无来源（作物模组槭树标签未核对） | N061、N062 |
| 岩蔷薇枝叶 | `materials/rockrose_twig` | 岩蔷薇 Cistus ladanifer 枝叶；西班牙安达卢西亚、葡萄牙 | 暂无来源 | N063 |
| 焙炒咖啡豆 | `materials/roasted_coffee_bean` | 咖啡 Coffea arabica、C. canephora 种子焙炒 | 咖啡生豆（`materials/coffee_bean`）焙炒（熔炉 / M17，同可可豆焙炒） | N064 |
| 金合欢花 | `materials/cassie_flower` | 金合欢 Vachellia farnesiana（Acacia farnesiana）花；法国南部、埃及、印度 | 暂无来源 | N066 |
| 大花茉莉花 | `materials/jasmine_grandiflorum_flower` | 素馨花 Jasminum grandiflorum 花；埃及、印度 | 暂无来源 | N069（经 N069 供 N068） |
| 小花茉莉花 | `materials/jasmine_sambac_flower` | 茉莉花 Jasminum sambac 花；广西横州、福建福州 | 暂无来源 | N071（经 N071 供 N070） |
| 素方花 | `materials/jasmine_officinale_flower` | 素方花 Jasminum officinale 花；四川、云南、法国格拉斯 | 暂无来源 | N124（经 `common_jasmine_concrete`） |
| 佛手 | `materials/fingered_citron` | 佛手 Citrus medica var. sarcodactylis 果实；广东肇庆、浙江金华、四川 | 暂无来源 | N072 |
| 葫芦巴籽 | `materials/fenugreek_seed` | 胡卢巴 Trigonella foenum-graecum 种子；印度、甘肃、河南 | 暂无来源 | N079 |
| 柚 | `materials/pummelo` | 柚 Citrus maxima（C. grandis）果实；福建、广西、广东 | 暂无来源 | N083 |
| 北美香柏枝叶 | `materials/thuja_leaf` | 北美香柏 Thuja occidentalis 枝叶；加拿大、美国东北 | 暂无来源 | N084 |
| 孜然 | `materials/cumin_seed` | 孜然芹 Cuminum cyminum 果实；新疆、印度、伊朗 | 暂无来源 | N085 |
| 柠檬叶 | `materials/lemon_leaf` | 柠檬 Citrus limon 叶与嫩枝；意大利西西里、巴拉圭 | 暂无来源 | N089 |
| 栀子花 | `materials/gardenia_flower` | 栀子 Gardenia jasminoides 花；湖南、江西、福建 | 暂无来源（本模组栀子灌木 I07 可加花期采花，待定） | N091 |
| 树兰花 | `materials/aglaia_flower` | 米仔兰 Aglaia odorata 花；广东、广西、福建 | 暂无来源 | N092、N093、N094 |
| 树苔 | `materials/tree_moss` | 扁枝衣 Pseudevernia furfuracea（原表作 Evernia furfuraceae，地衣）；法国、摩洛哥、东欧 | 暂无来源 | N096（经 N096 供 N095） |
| 香叶天竺葵 | `materials/rose_geranium_leaf` | 香叶天竺葵 Pelargonium graveolens 茎叶；云南、埃及、留尼汪 | 暂无来源 | N097 |
| 香茶菜 | `materials/rabdosia_herb` | 香茶菜属 Isodon（Rabdosia）spp. 全草；华南、西南 | 暂无来源 | N099 |
| 香柠檬 | `materials/bergamot` | 香柠檬 Citrus bergamia（C. aurantium subsp. bergamia）果实；意大利卡拉布里亚 | 暂无来源 | N101 |
| 香根草根 | `materials/vetiver_root` | 香根草 Chrysopogon zizanioides（Vetiveria zizanioides，原表误作 Vertiver）根；海地、印度尼西亚爪哇、福建 | 暂无来源 | N102、N103 |
| 发酵香荚兰豆 | `materials/cured_vanilla_bean` | 香荚兰豆荚杀青、发汗、干燥后的成品 | 香荚兰豆（`materials/vanilla`）热水杀青、发汗、干燥（30 A 前段，H07 晾晒架） | N104、N105 |
| 香附 | `materials/cyperus_rhizome` | 香附子 Cyperus rotundus 块茎；山东、浙江、湖南 | 暂无来源 | N106 |
| 细香葱 | `materials/chives` | 北葱 Allium schoenoprasum 叶；欧洲、华北 | 暂无来源 | N107 |
| 香紫苏 | `materials/clary_sage_herb` | 南欧丹参 Salvia sclarea 花期全草；俄罗斯、法国、陕西 | 暂无来源 | N108 |
| 香榧子壳 | `materials/torreya_shell` | 香榧 Torreya grandis 'Merrillii' 种子的假种皮与外壳；浙江诸暨、绍兴 | 暂无来源 | N109 |
| 啤酒花 | `materials/hops` | 啤酒花 Humulus lupulus 雌花序；新疆、甘肃、德国巴伐利亚 | 暂无来源（12 啤酒同用） | N112、N113 |
| 蓝桉叶 | `materials/eucalyptus_leaf` | 蓝桉 Eucalyptus globulus 叶；云南、澳大利亚、葡萄牙 | 暂无来源 | N114 |
| 海狸香囊 | `materials/castoreum` | 河狸 Castor fiber、C. canadensis 香囊（干燥）；加拿大、俄罗斯、新疆阿勒泰 | 暂无来源 | N115 |
| 锡兰肉桂皮 | `materials/ceylon_cinnamon_bark` | 锡兰肉桂 Cinnamomum verum（C. zeylanicum）树皮；斯里兰卡 | 暂无来源 | N116 |
| 锡兰肉桂叶 | `materials/ceylon_cinnamon_leaf` | 锡兰肉桂叶；斯里兰卡 | 暂无来源 | N117 |
| 桂花 | `materials/osmanthus_flower` | 木犀 Osmanthus fragrans 花；广西桂林、浙江杭州、湖北咸宁 | 暂无来源 | N119、N120（经 N120 供 N118） |
| 龙眼 | `materials/longan` | 龙眼 Dimocarpus longan（原表作 Euphoria longana）果实（桂圆干）；福建、广东、广西 | 暂无来源 | N121 |
| 留兰香 | `materials/spearmint` | 留兰香 Mentha spicata 全草；江苏、美国 | 暂无来源 | N122 |
| 青核桃皮 | `materials/walnut_hull` | 胡桃 Juglans regia、黑胡桃 J. nigra 的青果皮；云南、新疆、美国 | 暂无来源 | N123 |
| 蚕豆花 | `materials/broad_bean_flower` | 蚕豆 Vicia faba 花；云南、四川 | 暂无来源 | N126 |
| 野蔷薇花 | `materials/wild_rose_flower` | 野蔷薇 Rosa multiflora 花；华东、华中 | 暂无来源 | N128 |
| 菊苣根 | `materials/chicory_root` | Cichorium intybus 的根（烘焙后用），比利时、法国、中国新疆 | 暂无来源 | N134 |
| 晚香玉花 | `materials/tuberose_flower` | Polianthes tuberosa，印度、中国云南 | 暂无来源 | N135 |
| 紫罗兰叶 | `materials/violet_leaf` | Viola odorata，法国、埃及 | 暂无来源 | N136 |
| 椒样薄荷 | `materials/peppermint` | Mentha × piperita 全草，美国、印度 | 暂无来源 | N137 |
| 黑加仑芽 | `materials/blackcurrant_bud` | Ribes nigrum 休眠芽，法国勃艮第 | 暂无来源 | N138、N139 |
| 槐花 | `materials/sophora_flower` | Styphnolobium japonicum（Sophora japonica）花，中国 | 暂无来源 | N141（N140） |
| 愈疮木 | `materials/guaiac_wood` | Bulnesia sarmientoi 心材，巴拉圭、阿根廷 | 暂无来源 | N144 |
| 缬草根 | `materials/valerian_root` | Valeriana officinalis，欧洲、中国 | 暂无来源 | N145 |
| 墨红花 | `materials/crimson_glory_rose` | Rosa 'Crimson Glory'，中国浙江、云南 | 暂无来源（原版玫瑰丛归玫瑰花 `materials/rose_flower`，2026-11-11 定） | N147（N146） |
| 苦橙叶 | `materials/bitter_orange_leaf` | Citrus aurantium 叶与嫩枝，巴拉圭、法国 | 暂无来源 | N149、N254 |
| 檀香木 | `materials/sandalwood` | Santalum album 心材，印度、澳大利亚 | 暂无来源 | N152 |
| 薰衣草 | `materials/lavender` | Lavandula angustifolia 花穗，法国、中国新疆 | 暂无来源 | N153 |
| 头状百里香 | `materials/conehead_thyme` | Thymbra capitata（Thymus capitatus）全草，西班牙、地中海沿岸 | 暂无来源 | N154 |
| 可乐果 | `materials/kola_nut` | Cola acuminata、C. nitida 种子，西非 | 暂无来源 | N155 |
| 加州胡椒果 | `materials/peruvian_pepper_berry` | Schinus molle 果实，秘鲁、美国加州 | 暂无来源 | N156 |
| 卡黎皮 | `materials/cascarilla_bark` | Croton eluteria 树皮，巴哈马 | 暂无来源 | N157 |
| 百里香 | `materials/thyme` | Thymus vulgaris、T. zygis 全草，西班牙、法国 | 暂无来源 | N158（N235） |
| 依兰花 | `materials/ylang_ylang_flower` | Cananga odorata 花（卡南伽油用 f. macrophylla），印度尼西亚、科摩罗 | 暂无来源 | N067 |
| 月桂叶 | `materials/laurel_leaf` | Laurus nobilis，土耳其、地中海沿岸 | 暂无来源 | N161 |
| 生姜 | `materials/ginger` | Zingiber officinale 根茎（干），中国、印度 | 暂无来源 | N162 |
| 白栎木 | `materials/oak_log` | Quercus alba 木材（烘烤木屑），北美 | `#minecraft:oak_logs`（原版橡木原木） | N163 |
| 龙蒿 | `materials/tarragon` | Artemisia dracunculus 全草，法国、中国新疆 | 暂无来源 | N164 |
| 樟木 | `materials/camphor_wood` | Cinnamomum camphora 木材与根，中国、日本 | 暂无来源 | `crude_camphor_oil`（N165、N220） |
| 肉豆蔻衣 | `materials/mace` | Myristica fragrans 假种皮，印度尼西亚、格林纳达 | 暂无来源 | N166、N245 |
| 众香叶 | `materials/pimento_leaf` | Pimenta dioica（P. officinalis）叶，牙买加 | 暂无来源 | N167 |
| 西班牙鼠尾草 | `materials/spanish_sage` | Salvia lavandulifolia 全草，西班牙 | 暂无来源 | N168 |
| 红橘 | `materials/mandarin` | Citrus reticulata 果实（压榨果皮），中国、地中海沿岸 | 暂无来源 | N110 |
| 杂薰衣草 | `materials/lavandin` | Lavandula × intermedia 花穗，法国 | 暂无来源 | N170 |
| 杏仁 | `materials/apricot_kernel` | Prunus armeniaca 果核仁，中国、土耳其 | 暂无来源 | N171 |
| 苏合香原脂 | `materials/styrax` | Liquidambar orientalis、L. styraciflua 树干渗出的香脂，土耳其、中美洲 | 暂无来源 | N172、N173 |
| 角豆荚 | `materials/carob_pod` | Ceratonia siliqua 荚果（籽即已有的 `materials/carob_seed`），地中海沿岸 | 暂无来源 | N174、N175 |
| 乳香 | `materials/olibanum` | Boswellia sacra、B. carteri 树脂，索马里、阿曼 | 暂无来源 | N177 |
| 没药 | `materials/myrrh` | Commiphora myrrha 树脂，索马里、埃塞俄比亚 | 暂无来源 | N178 |
| 高良姜根茎 | `materials/galangal_root` | Alpinia galanga、A. officinarum，中国广东、海南，东南亚 | 暂无来源 | N179 |
| 松针 | `materials/pine_needles` | Pinus sylvestris 针叶与嫩枝，欧洲 | 暂无来源（原版云杉树叶已归冷杉针叶 `materials/fir_needles`，2026-11-11 定） | N180 |
| 小茴香籽 | `materials/fennel_seed` | Foeniculum vulgare 果实，中国、印度 | 暂无来源 | N181 |
| 苦扁桃仁 | `materials/bitter_almond` | Prunus dulcis var. amara 种仁，摩洛哥、地中海沿岸 | 暂无来源 | N182 |
| 阿魏 | `materials/asafoetida` | Ferula assa-foetida 油胶树脂，伊朗、阿富汗；中国新疆（F. sinkiangensis） | 暂无来源 | N183 |
| 欧芹 | `materials/parsley` | Petroselinum crispum 全草与籽，欧洲 | 暂无来源 | N185、N251 |
| 冷杉针叶 | `materials/fir_needles` | Abies sibirica、A. balsamea 针叶与嫩枝，俄罗斯、加拿大 | 原版云杉树叶 `minecraft:spruce_leaves`（游戏简化：香脂冷杉与云杉同为松科） | N192 |
| 波罗尼花 | `materials/boronia_flower` | Boronia megastigma，澳大利亚塔斯马尼亚 | 暂无来源 | N187 |
| 玫瑰木 | `materials/rosewood` | Aniba rosaeodora 木材，巴西亚马孙 | 暂无来源 | N188 |
| 玫瑰草 | `materials/palmarosa` | Cymbopogon martini 全草，印度 | 暂无来源 | N189 |
| 香茅 | `materials/citronella_grass` | Cymbopogon nardus、C. winterianus 全草，斯里兰卡、印度尼西亚、中国 | 暂无来源 | N190 |
| 加拿大香脂 | `materials/canada_balsam` | Abies balsamea 树皮油囊中的原脂，加拿大 | 暂无来源 | N193 |
| 胡萝卜籽 | `materials/carrot_seed` | Daucus carota 果实，法国、匈牙利 | 暂无来源 | N194 |
| 罗马春黄菊花 | `materials/roman_chamomile` | Chamaemelum nobile（Anthemis nobilis）花，英国、法国 | 暂无来源 | N195、N196 |
| 药鼠李皮 | `materials/cascara_bark` | Frangula purshiana（Rhamnus purshiana）树皮，北美太平洋沿岸 | 暂无来源 | N197 |
| 荜澄茄 | `materials/cubeb` | Piper cubeba 未熟果实，印度尼西亚 | 暂无来源 | N198 |
| 胡薄荷 | `materials/pennyroyal` | Mentha pulegium 全草，地中海沿岸 | 暂无来源 | N199 |
| 欧当归根 | `materials/lovage_root` | Levisticum officinale，欧洲 | 暂无来源 | N200、N250 |
| 欧夏至草 | `materials/horehound` | Marrubium vulgare 全草，欧洲 | 暂无来源 | N201 |
| 丝兰茎 | `materials/yucca_trunk` | Yucca schidigera（莫哈弗丝兰）茎干，美国西南部、墨西哥 | 暂无来源 | N202 |
| 海索草 | `materials/hyssop` | Hyssopus officinalis 全草，欧洲 | 暂无来源 | N204 |
| 莳萝 | `materials/dill` | Anethum graveolens 全草，欧洲、美国 | 暂无来源 | N205 |
| 秘鲁香脂原脂 | `materials/crude_peru_balsam` | Myroxylon balsamum var. pereirae 树干伤口渗出物，萨尔瓦多 | 暂无来源 | N206 |
| 格蓬 | `materials/galbanum` | Ferula gummosa（F. galbaniflua）油胶树脂，伊朗 | 暂无来源 | N207 |
| 脂檀木 | `materials/amyris_wood` | Amyris balsamifera 木材，海地、牙买加 | 暂无来源 | N208 |
| 银荆花 | `materials/mimosa_flower` | Acacia dealbata 花，法国、印度 | 暂无来源 | N209 |
| 接骨木花 | `materials/elderflower` | Sambucus nigra、S. canadensis，欧洲、北美 | 暂无来源 | N210 |
| 甘牛至 | `materials/marjoram` | Origanum majorana 全草，埃及、地中海沿岸 | 暂无来源 | N211 |
| 黄龙胆根 | `materials/gentian_root` | Gentiana lutea，欧洲山区 | 暂无来源 | N212 |
| 黄葵籽 | `materials/ambrette_seed` | Abelmoschus moschatus（Hibiscus abelmoschus）种子，印度、厄瓜多尔、中国 | 暂无来源 | N213 |
| 野黑樱桃树皮 | `materials/wild_cherry_bark` | Prunus serotina 树皮，北美 | 暂无来源 | N214 |
| 黑胡椒 | `materials/black_pepper` | Piper nigrum 未熟果实（晒干），越南、印度、中国海南 | 暂无来源 | N215 |
| 榄香脂 | `materials/elemi` | Canarium luzonicum 油树脂，菲律宾 | 暂无来源 | N217 |
| 蜡菊花 | `materials/immortelle_flower` | Helichrysum italicum（H. angustifolium）花，法国科西嘉、巴尔干 | 暂无来源 | N218 |
| 香蜂花 | `materials/lemon_balm` | Melissa officinalis 全草，欧洲 | 暂无来源 | N219 |
| 苦橙花 | `materials/bitter_orange_flower` | Citrus aurantium subsp. amara 花，突尼斯、摩洛哥、埃及 | 暂无来源 | N221 |
| 葡萄柚 | `materials/grapefruit` | Citrus × paradisi（及柚 C. maxima）果皮、幼果，美国、中国 | `#forge:fruits/grapefruit`（08 柑橘已列） | N222 |
| 穗薰衣草 | `materials/spike_lavender` | Lavandula latifolia 花穗，西班牙 | 暂无来源 | N223 |
| 鹰爪豆花 | `materials/genet_flower` | Spartium junceum 花，地中海沿岸 | 暂无来源 | N224 |
| 苦橙 | `materials/bitter_orange` | Citrus aurantium 果实（含玳玳 C. aurantium 'Daidai'），西班牙、中国浙江 | 暂无来源 | N255 |
| 小米辣 | `materials/bird_eye_chili` | Capsicum frutescens 干果，中国云南、海南 | 暂无来源（或并入 `materials/dried_chili`） | N227 |
| 丁香花梗 | `materials/clove_stem` | Syzygium aromaticum（Eugenia spp.）花梗，印度尼西亚、马达加斯加 | 暂无来源 | N228 |
| 茴芹籽 | `materials/anise_seed` | Pimpinella anisum 果实，土耳其、埃及 | 暂无来源 | N229 |
| 巴拉圭茶叶 | `materials/mate_leaf` | Ilex paraguariensis 叶，巴拉圭、阿根廷、巴西 | 暂无来源 | N231 |
| 山核桃树皮 | `materials/hickory_bark` | Carya ovata 等树皮，北美 | 暂无来源 | N232 |
| 瓜拉纳籽 | `materials/guarana_seed` | Paullinia cupana 种子，巴西 | 暂无来源 | N233 |
| 白胡椒 | `materials/white_pepper` | Piper nigrum 成熟果实去果皮，中国海南、印度尼西亚 | 暂无来源 | N236、N237 |
| 众香果 | `materials/allspice` | Pimenta dioica 未熟果实（晒干），牙买加 | 暂无来源 | N241 |
| 安息香 | `materials/benzoin` | Styrax tonkinensis 树脂，老挝、越南 | 暂无来源 | N242 |
| 当归籽 | `materials/angelica_seed` | Angelica archangelica、A. sinensis 果实，欧洲、中国甘肃 | 暂无来源 | N243 |
| 当归根 | `materials/angelica_root` | Angelica archangelica、A. sinensis 根，欧洲、中国甘肃 | 暂无来源 | N244 |
| 西印度月桂叶 | `materials/west_indian_bay_leaf` | Pimenta racemosa（P. acris）叶，多米尼克、西印度群岛 | 暂无来源 | N246、N247 |
| 苦木 | `materials/quassia_wood` | Picrasma excelsa、Quassia amara 木材，牙买加、苏里南 | 暂无来源 | N253 |
| 金鸡纳树皮 | `materials/cinchona_bark` | Cinchona calisaya、C. ledgeriana 等树皮，秘鲁、刚果（金）、印度尼西亚、中国云南 | 暂无来源 | N256、N258 |
| 金钮扣 | `materials/jambu` | Acmella oleracea（Spilanthes acmella var. oleracea）全草与花序，巴西、中国云南 | 暂无来源 | N257 |
| 互叶白千层叶 | `materials/tea_tree_leaf` | Melaleuca alternifolia 叶与嫩枝，澳大利亚 | 暂无来源 | N261 |
| 白柠檬 | `materials/lime` | Citrus aurantiifolia 果实，墨西哥、秘鲁 | `#forge:fruits/lime`（08 柑橘已列） | N262 |
| 白芥子 | `materials/yellow_mustard_seed` | Sinapis alba 种子，加拿大 | 暂无来源 | N265 |
| 芥菜籽 | `materials/brown_mustard_seed` | Brassica juncea 种子，加拿大、印度、中国 | 暂无来源 | N266 |
| 紫苏叶 | `materials/perilla_leaf` | 紫苏 Perilla frutescens 叶，中国、日本、韩国 | 暂无来源 | N268 |
| 药鼠尾草 | `materials/sage` | Salvia officinalis 叶，克罗地亚达尔马提亚、阿尔巴尼亚 | 暂无来源 | N274、N296 |
| 山达草叶 | `materials/yerba_santa` | Eriodictyon californicum 叶，美国加利福尼亚 | 暂无来源 | N278、N398 |
| 苜蓿 | `materials/alfalfa` | Medicago sativa 全草，美国、中国甘肃、内蒙古 | 暂无来源 | N279 |
| 母菊花 | `materials/german_chamomile` | Matricaria chamomilla（M. recutita）头状花序，匈牙利、埃及、中国新疆 | 暂无来源 | N287、N309、N357 |
| 红三叶草花序 | `materials/red_clover` | Trifolium pratense 开花顶部，欧洲、北美 | 暂无来源 | N290 |
| 蒲公英 | `materials/dandelion` | Taraxacum officinale 根与全草，欧洲、中国 | 原版 `minecraft:dandelion`（游戏简化：花代全株） | N291、N292 |
| 加拿大飞蓬 | `materials/canadian_fleabane` | Erigeron canadensis（Conyza canadensis）开花全草，北美 | 暂无来源 | N293 |
| 穗花槭树皮 | `materials/mountain_maple_bark` | Acer spicatum 树皮（部位未确认），北美东北部 | 暂无来源 | N294 |
| 芸香 | `materials/rue` | Ruta graveolens 开花全草，地中海沿岸、北非 | 暂无来源 | N295 |
| 菝葜根 | `materials/sarsaparilla_root` | Smilax ornata、S. regelii 等的根，墨西哥、洪都拉斯、牙买加 | 暂无来源 | N297 |
| 苦艾 | `materials/wormwood` | Artemisia absinthium 开花全草，欧洲、北非、中国新疆 | 暂无来源 | N302、N345、N348 |
| 橘柚 | `materials/tangelo` | Citrus × tangelo（橘 × 葡萄柚）果实，美国佛罗里达 | 暂无来源 | N304 |
| 美国栗叶 | `materials/chestnut_leaf` | Castanea dentata 叶，北美东部（野生种群因栗疫病已很少） | 暂无来源 | N306 |
| 古巴香脂 | `materials/copaiba_balsam` | Copaifera officinalis、C. langsdorffii 等树干钻孔流出的油树脂，巴西亚马孙 | 暂无来源 | N307 |
| 达迷草叶 | `materials/damiana_leaf` | Turnera diffusa 叶，墨西哥 | 暂无来源 | N308 |
| 甜没药 | `materials/opoponax` | Commiphora guidottii（C. erythraea）树胶树脂，索马里、埃塞俄比亚 | 暂无来源 | N311、N341 |
| 藏红花柱头 | `materials/saffron` | Crocus sativus 柱头，伊朗、西班牙；中国上海崇明栽培 | 暂无来源 | N312 |
| 酸角果肉 | `materials/tamarind_pulp` | Tamarindus indica 果肉，泰国、印度、中国云南（种子即已有 `materials/tamarind_seed`） | 暂无来源 | N317 |
| 辣根 | `materials/horseradish_root` | Armoracia rusticana 根，欧洲、中国山东 | 暂无来源 | N318 |
| 芹菜叶 | `materials/celery_leaf` | Apium graveolens 茎叶，印度、欧洲、中国 | 暂无来源（作物模组芹菜标签待核对） | N320 |
| 柏木 | `materials/cedarwood` | 柏木 Cupressus funebris 木材（中国柏木油），或北美红柏 Juniperus virginiana；中国四川、贵州，美国 | 暂无来源 | N321 |
| 韭葱 | `materials/leek` | Allium porrum 全株，欧洲、中国山东 | 暂无来源（作物模组韭葱标签待核对） | N326 |
| 香橙 | `materials/yuzu` | Citrus junos 果实（取皮），日本高知、中国四川、湖北 | 暂无来源 | N329 |
| 墨西哥牛至 | `materials/mexican_oregano` | Lippia graveolens 叶，墨西哥 | 暂无来源 | N331 |
| 冬香薄荷 | `materials/winter_savory` | Satureja montana 开花全草，巴尔干、地中海沿岸 | 暂无来源 | N333 |
| 桃叶 | `materials/peach_leaf` | Prunus persica 叶，中国、地中海沿岸 | 暂无来源 | N336 |
| 白藓牛至 | `materials/dittany_of_crete` | Origanum dictamnus 开花全草，希腊克里特岛 | 暂无来源 | N337 |
| 花椒 | `materials/sichuan_pepper` | Zanthoxylum bungeanum 果皮，中国四川、陕西、甘肃 | 暂无来源 | N342 |
| 儿茶心材 | `materials/catechu_heartwood` | Senegalia catechu（Acacia catechu）心材，印度、缅甸、中国云南 | 暂无来源 | N344 |
| 橡苔 | `materials/oakmoss` | Evernia prunastri（生于栎树的地衣），巴尔干、摩洛哥 | 暂无来源 | N353 |
| 苏格兰留兰香 | `materials/scotch_spearmint` | Mentha × gracilis（M. cardiaca）茎叶，美国中西部、西北部 | 暂无来源 | N354 |
| 安古树皮 | `materials/angostura_bark` | Angostura trifoliata（Galipea officinalis）树皮，委内瑞拉、哥伦比亚 | 暂无来源 | N356 |
| 灵猫香 | `materials/civet` | 非洲灵猫 Civettictis civetta、大灵猫 Viverra zibetha 会阴腺分泌物，埃塞俄比亚；中国浙江养殖小灵猫 | 暂无来源 | N361 |
| 橘叶 | `materials/mandarin_leaf` | Citrus reticulata 叶与嫩枝，意大利西西里、巴拉圭 | 暂无来源 | N366 |
| 欧洲山松针叶 | `materials/dwarf_pine_needles` | Pinus mugo 针叶与枝梢，奥地利、斯洛伐克 | 暂无来源（不与 N2 `materials/pine_needles` 共用，避免 NT1 配方冲突） | N367 |
| 玫瑰果 | `materials/rose_hip` | Rosa canina 等的果实，智利、欧洲、中国新疆 | 暂无来源 | N368 |
| 夏香薄荷 | `materials/summer_savory` | Satureja hortensis 开花全草，欧洲东南部 | 暂无来源 | N369 |
| 加拿大细辛 | `materials/canadian_snakeroot` | Asarum canadense 根茎与根，北美东部 | 暂无来源 | N370 |
| 黄蓍胶片 | `materials/tragacanth_lump` | Astragalus gummifer 等茎干割伤渗出物（带状、片状），伊朗、土耳其 | 暂无来源 | N372 |
| 摩洛哥豆蔻 | `materials/grains_of_paradise` | Aframomum melegueta 种子，加纳、尼日利亚 | 暂无来源 | N374 |
| 鲣节 | `materials/katsuobushi` | 鲣鱼 Katsuwonus pelamis 鱼柳经蒸煮、烟熏焙干、接种霉菌陈化，日本鹿儿岛枕崎、静冈烧津 | 鱼（`materials/raw_fish`）M03 蒸煮 → M10 烟熏、烘干（需补此配方；游戏简化，省去霉化） | N379 |
| 药蜀葵根 | `materials/marshmallow_root` | Althaea officinalis 根，欧洲、西亚 | 暂无来源 | N382 |
| 白千层枝叶 | `materials/cajeput_leaf` | Melaleuca cajuputi 叶与嫩枝，印度尼西亚、越南 | 暂无来源 | N384 |
| 芦荟叶 | `materials/aloe` | Aloe ferox、A. vera 叶（取汁），南非、加勒比 | 暂无来源 | N389 |
| 龙涎香 | `materials/ambergris` | 抹香鲸 Physeter macrocephalus 肠道分泌物，海上漂浮或冲上海岸的陈化块 | 暂无来源 | N390 |
| 燕根 | `materials/swallowroot` | Decalepis hamiltonii 根，印度南部 | 暂无来源 | N392 |
| 香厚壳桂皮 | `materials/massoia_bark` | Cryptocarya massoy 树皮，新几内亚 | 暂无来源 | N397 |
| 柚苷酶（酶） | `enzymes/naringinase` | α-L-鼠李糖苷酶与 β-葡萄糖苷酶复合酶，黑曲霉、青霉发酵生产 | 黑曲霉（`cultures/aspergillus_niger`）M29 发酵，柑橘皮诱导（游戏简化，同 15 F 组通用酶流程） | N375、N399 |
| 产苏氨酸大肠杆菌（菌种） | `cultures/e_coli_threonine` | 解除反馈抑制的苏氨酸生产菌（大肠杆菌） | M30 基因重组（P94）：大肠杆菌（游戏简化，同 `cultures/e_coli_cysteine`） | N359 |
| 玳玳果 | `materials/daidai_fruit` | 玳玳 Citrus aurantium ‘Daidai’ 果实；浙江金华 | 暂无来源 | N082、N225 |
| 红橘 | `materials/tangerine` | 红橘 Citrus reticulata ‘Tangerine’ 果皮；四川、重庆 | 暂无来源 | N169 |
| 卡南伽花 | `materials/cananga_flower` | 卡南伽 Cananga odorata var. macrophylla 花；印度尼西亚爪哇 | 暂无来源 | N160 |
| 冷杉属针叶 | `materials/abies_needles` | 冷杉属 Abies spp. 针叶（GB 原表学名；与 N192 的香脂冷杉分开） | 暂无来源 | N186 |

## 5 新增中间体

6 种，化学式见 09。

| 建议 ID | 名称 | 阶段 | 路线 | 用于 |
| --- | --- | --- | --- | --- |
| `common_jasmine_concrete` | 素方花浸膏 | T4 | NT4：`materials/jasmine_officinale_flower` + `n_hexane` | N124 |
| `crude_camphor_oil` | 樟脑原油 | T1 | NT1 `materials/camphor_wood` | N165、N220 |
| `cane_molasses` | 甘蔗糖蜜 | T0 | 甘蔗（`materials/sugar_cane`）M02 压榨 → 澄清 → M15 蒸发结晶取出蔗糖后的母液，与蔗糖同一配方（联产）；甜菜一路得 `beet_molasses`（17）；两者都可用的配方引用流体标签 `foodindustrial:molasses`（原 `molasses`，2026-11-12 分为两种，11 问题 42） | N276 |
| `hesperidin` | 橙皮苷 | T2 | ST21：`materials/citrus_peel` → M04 石灰水浸提（P40）→ M12 过滤 → 盐酸酸化（P37）→ M15 冷却结晶（P12） | N375 |
| `rutin` | 芦丁 | T2 | ST21：`materials/sophora_flower`（槐米）→ M03 石灰水煮提（P40）→ M12 趁热过滤 → 盐酸酸化 → M09 / M15 冷却结晶（P12） | N399 |
| `crude_fusel_oil` | 粗杂醇油 | T0 | 乙醇发酵醪 M22 精馏侧线采出（同 128 路线的副产） | N402（128 可共用） |

## 6 存疑与待核

按设计分组列出（N1–N3 为本文第 2 节的三段；合成香料 F01–F11 见 19，非香料 G1–G4 见 17）。各组提到的“ST-组号-n”“NT-组号-n”已换成第 1 节的统一编号。

### N1 组（序号 1–130）

- 中文名已与 PDF 原表（第 153–157 页）逐项核对，无错字；清单共 129 条（N009、N018、N100 不在本组）。
- 英文名原表笔误已按正名写回或在学名中改正：Vertiver → Vetiver（N102、N103）、Saussures lappa Clanke → Saussurea lappa C.B.Clarke（N019）、Elletaria → Elettaria（N010、N011）、Evernia furfuraceae → Pseudevernia furfuracea（N095、N096）。
- N106 香附子油：原表学名 Cupressus sempervirens（地中海柏木）与中文名香附子（Cyperus rotundus）不符，按中文名设计。
- N125 桦焦油：中文名指桦木焦油（干馏），英文名、学名与 FEMA 2154 为甜桦油（Betula lenta 树皮浸渍后蒸馏，水杨酸甲酯约 98% 以上）；按英文名用 NT12，原料借原版桦木原木 `materials/hemicellulose`（游戏简化）；若按中文名应改 NT11。
- N057 / N058：调香业的 orris concrete（FEMA 2829）其实是陈化鸢尾根水蒸气蒸馏所得的鸢尾脂（orris butter），FEMA 2830 为溶剂提取物；本组按中文名分配（浸膏 NT4、凝脂 NT1），两者是否对调待定。
- N061 / N062 枫槭油、枫槭浸膏：现实原料部位与工艺未确认，暂以枫糖浆为原料（NT1 蒸馏、NT4 乙醇浸提），主成分未确认。
- 主要香气成分未查到可靠来源：N006 九里香浸膏、N092–N094 树兰花、N099 香风茶油、N109 香榧子壳浸膏、N126 蚕豆花酊、N053 枣子酊、N121 桂圆酊、N045 豆豉酊。
- N008 万寿菊油：商品万寿菊油多由小万寿菊 Tagetes minuta 花期全草蒸馏，本组借已有万寿菊干花 `materials/marigold`（T. erecta，游戏简化）。
- N127 绿茶酊直接用 `materials/tea`（鲜叶视同绿茶，游戏简化）；N041 红茶酊另设 `materials/black_tea`，否则两者配方相同。
- 与 N2 组配方重复（同模板、同原料）：N110 橘子油与 N169 红橘油同为 NT2 + `materials/mandarin`；N082 玳玳果油与 N225 玳玳果皮油、N255 苦橙油同为 NT2 + `materials/bitter_orange`；N131 甜橙油与 N226 甜橙油（橙皮压榨法）同为 NT2 + `materials/orange`。建议合并为同一物料，或区分原料（如 `materials/tangerine`、`materials/daidai_fruit`）或改 N131 为蒸馏法（NT1）。
- N080 玳玳花油（NT1 + `materials/bitter_orange_flower`）与 N3 组 N346 苦橙花油大概率同配方；N081 玳玳花浸膏即 N2 组 N221 橙花净油内含的浸膏步骤，建议 N221 改为 NT5 + N081。
- NT9 一次精馏同时得萜烯馏分与除萜油：本组 N087 / N088 同出 N086；N035 白柠檬萜烯的另一馏分即 N2 组 N262 除萜白柠檬油（N2 写作 NT2、NT9 + `materials/lime`，建议改为 NT9 + N034）；N132 除萜甜橙油出自 N131，N2 组 N133、N263 出自 N226，若 N131 与 N226 合并则三者同出一塔。
- `materials/rose_flower`（本组）与 N2 组 `materials/crimson_glory_rose` 都收原版 `minecraft:rose_bush`，N056 玫瑰浸膏与 N147 墨红花浸膏会被同一物品匹配为两条配方；建议只让一个标签收玫瑰丛。
- N116 斯里兰卡肉桂皮油与 N3 组 N364 肉桂皮油 / 油树脂同为 Cinnamomum 树皮油，原料标签与物料 ID 需与 N3 统一；`materials/ceylon_cinnamon_bark` 是否并入 `materials/cinnamon` 待定。
- NT6 写“香辛料干品”：大蒜、生姜为鲜品标签（N2 的 `materials/ginger` 注明干品），大蒜是否另设干品物品（同 `materials/dried_turmeric`）待定。
- NT12 与 N2 的 NT12（含氰苷种仁酶解蒸馏）同属先酶解后蒸馏，可考虑合并为一个模板加变体。
- N124 素方花净油：表 B.2 无素方花浸膏，新增中间体 `common_jasmine_concrete`（不单列香料编码）。
- N104 香荚兰豆酊即 30 A 路线中的香荚兰酊；`materials/cured_vanilla_bean` 为 30 A 杀青、发汗、干燥的产物，建议 30 A 改为引用 N104。
- N027 甘草流浸膏即 352 前段的热水浸提浓缩液，建议 352 改为以 N027 为起点。
- N077 姜黄油树脂即 61 B 浸提浓缩的中间物（16 §1.4）；61 B 以姜黄粉（205）为原料，本组用 `materials/dried_turmeric`，两者是否统一待定。
- N045 豆豉酊以 12 的成品豆豉为原料，标签 `foods/douchi` 尚未生成；N3 组 N381 豆豉油树脂宜同用。
- N017 天然康酿克油的原料葡萄酒酒脚需在葡萄酒澄清配方（H14、M04）补副产输出；N023 可可碎粒、N064 焙炒咖啡豆、N041 红茶同样需补加工配方。
- N052 苍术脂常温为固体（挥发油中茅术醇、β-桉叶醇析晶），状态记固；未找到描述析晶的链接。
- N034 白柠檬油：商品青柠油以整果破碎蒸馏的蒸馏油为主，本组按冷榨（NT2）设计；蒸馏油可作 B 路线（NT1）。
- 新原料多数暂无来源；能直接取得的只有玫瑰（原版玫瑰丛）、桦木（原版桦木原木）、青柠与葡萄柚（08 柑橘已列的 `#forge:fruits/lime`、`#forge:fruits/grapefruit`），以及由已有原料加工得到的可可碎粒、焙炒咖啡豆、红茶、发酵香荚兰豆、葡萄酒酒脚。

### N2 组（序号 131–260）

- 已有物料（ST26）5 条：N143 = 367 `paprika_oleoresin`，N150 = `mint_oil`（29 A 的薄荷原油，即亚洲薄荷油），N176 = 269 `quillaja_extract`，N249 = 288 `gum_arabic`，N252 = `oleic_acid`；其中 `paprika_oleoresin` 已于 2026-11-12 改为不带纯度（11 问题 42）；`quillaja_extract` 按有效成分皂苷计价，仍为 K5（10 第 1.1 节）。
- N151 亚洲薄荷素油就是 29 A 冷冻析出薄荷醇后的母液（04 已写作“薄荷素油”副产，但 09 无物料），本批补 `dementholized_mint_oil`，不新增工序。
- NT9 的“结晶单体”在现实中只需冷却析晶：N151 与 N220 按 29 A 用 M09 结晶槽在 T1 完成，阶段写 T1；建议 NT9 注明结晶可用 M09（T1），精馏才需 M22（T2）。
- 双形态条目（N136 净油/浸膏、N161 提取物/油树脂、N231 净油/提取物、N245 油树脂/提取物、N265 提取物/油树脂）各只设一个物料，模板写两种；N136 与 N3 组 N301 紫罗兰叶净油重叠，合并时决定是否共用物料。
- 原料标签与 N1、N3 组可能重名或同物异名，合并时统一：黑胡椒、生姜、众香果、月桂叶、红橘（N1 橘子油同种 Citrus reticulata）、苦橙（含玳玳，N1 玳玳花 / 果油可能另设标签）、依兰花（N160 卡南伽油与 N1 依兰油同种）、薰衣草、接骨木花、甘牛至、海索草、香蜂花、缬草根、金鸡纳树皮、卡黎皮、苏合香、榄香脂、黄葵籽、荜澄茄、阿魏、葡萄柚、白柠檬、小茴香籽、当归根。
- 新增模板编号 NT12–NT17 由本组自排，与 N1、N3 组提出的新模板可能撞号，合并时重排。
- N137 椒样薄荷（Mentha × piperita）与已有 `materials/mint`（中国薄荷，出薄荷原油）不同种，新增 `materials/peppermint`；如要少加原料，可并入 `materials/mint`（游戏简化）。
- N138、N139 共用 FEMA 2346（黑加仑芽），按芽设 `materials/blackcurrant_bud`；黑加仑酊也可改用已有果实标签 `materials/blackcurrant`，未确认国内做法。
- N171 杏仁油的 CAS 72869-69-3 是杏核仁压榨脂肪油（非精油），借用 NT2（压榨 + 离心），不带纯度，同 09 `castor_oil`。
- N174 长角豆油无 FEMA 号，查不到工艺，暂按角豆荚溶剂浸提（NT4），未确认。
- N227 小米辣若并入 `materials/dried_chili`，与 367 只靠溶剂区分配方，不建议；故新增 `materials/bird_eye_chili`。
- N159 现实做法是脱脂乳加约 0.1% 柠檬酸接种乳酸乳球菌等发酵后水蒸气蒸馏，无须无菌罐；NT10 定为 M29（T4）偏高，另提 NT13（M05 + M08，T1）。
- N182 苦杏仁油要除去酶解产生的氢氰酸，按规则 8 只写试剂与模板（NT12）；FFPA 级才可食用。
- N230 L-天冬酰胺按一水合物（市售形态）写化学式；现实工业来源（合成或提取）未确认，取马铃薯汁浓缩结晶为游戏简化（史上首次由芦笋汁结晶得到）。
- N258 现实先结晶硫酸奎宁再转盐酸盐，NT17 以稀盐酸反萃直接结晶盐酸盐，游戏简化；化学式按 09 写法把 HCl 并入 Hill 式，结晶水 2 个。
- N256 金鸡纳树皮条目即树皮本身，给了成品 `cinchona_bark_powder`（NT14 烘干粉碎）；也可直接把原料 `materials/cinchona_bark` 作为该香料，不另设物料。
- N203 FEMA 2606 指海带本身，按中文名“提取物”走 NT7；N133 甜橙油萜烯以 d-柠檬烯为主，与已有 `limonene`（K4）是否合并待定，本批按馏分单列、不带纯度。
- N239、N240 原料用成品标签 `foods/butter`（12 黄油）；白脱酸实际含全部乳脂脂肪酸，反应式以三丁酸甘油酯计只代表特征酸。
- 松针、冷杉针叶（N180、N186、N192）原版没有松、冷杉，建议用云杉树叶代替（游戏简化）；墨红花用原版玫瑰丛（游戏简化）。
- 清单中文名与 PDF 原表一致，未发现错字；英文名按原表补空格，并改正原表拼写：N155 “Kolas nut … Cola acuminate” 写为 Kola nut extract（Cola acuminata），学名 Salvia lavandulifolia、Sambucus nigra 等的断字已在“现实来源”写全。

### N3 组（序号 261–388）

- N301 与 N2 的 N136：FEMA 都是 3110、学名都是 Viola odorata，判断 N301 紫罗兰叶净油就是 N136 中的净油形态，同一物料；建议 N136 拆为浸膏 `violet_leaf_concrete` 与净油，净油与 N301 共用 `violet_leaf_absolute`（本组按 NT5 以 N136 浸膏为原料）；若 N136 保持一个合并物料，N301 改为引用 N136，不另设物料。
- N346 苦橙花油与 N1 的 N080 玳玳花油同为 FEMA 2771，原料（`materials/bitter_orange_flower`）与模板（NT1）都相同，配方会冲突；建议二者共用一个物料。
- 已有物料（ST26）5 条：N332 = 353、N347 = 270、N362 = 232 `bixin`（香料用胭脂树提取物与着色剂胭脂树橙同物）、N365 = 283、N396 = 15（褐藻胶即以海藻酸钠为主的海藻酸盐）。N267 焦木酸原写已有 `wood_vinegar`，2026-11-12 定另设精制物料 `refined_wood_vinegar`（NT11）。
- 纯度：N399、N400、N403 是按含量（约 90%–95%）定规格的纯化提取物，按 BRIEF5“按主成分含量计价的天然提取物”给 K5（同 09 `steviol_glycosides`），与“混合物不带纯度”的组说明不一致，待定；N372 黄蓍胶随 09 `ghatti_gum`、`karaya_gum` 给 K6。
- 阶段：NT9 用 M22、NT7 粉末用 M28，06 中这两台都是 T3，模板写 T2，本组照模板写；N351 必须用 M25 离子排斥层析（P95），写 T3。
- 复用他组模板：干品六项（N308、N337、N345、N382、N383、N388）用 N2 的 NT14；N318 辣根油（黑芥子苷经芥子酶生成异硫氰酸烯丙酯）、N326 韭葱油用 N1 的 NT12。
- 复用 N1、N2 的原料标签（未在本组“新增原料”重列，合并时把本组编码补进其“用于”栏）：N1 的 clove_bud、nutmeg、juniper_berry、coriander_seed、jujube、orris_root、fenugreek_seed、hops、spearmint、rose_geranium_leaf、celery_seed、rockrose_twig、cured_vanilla_bean、basil；N2 的 black_pepper、allspice、elemi、immortelle_flower、ambrette_seed、violet_leaf、valerian_root、lavender、cascarilla_bark、styrax、asafoetida、elderflower、marjoram、hyssop、lemon_balm、cubeb、cinchona_bark、bitter_orange_flower、sophora_flower（rutin 用槐米，游戏简化与槐花共用）。
- 同原料同机器的配方区分：NT6 油树脂统一用丙酮，与 NT4 乙醇浸膏区分（N314 / N319，N300 / N1 的 N105）；N316 鸢尾树脂与 N1 的 N057 鸢尾浸膏都是根茎溶剂提取物，本组改用丙酮避免冲突，也可合并；N334 苏合香只加热熔化过滤，与 N2 的 N173（乙醇溶解）同用 NT8，靠有无溶剂区分；N350 由 N027 甘草流浸膏喷雾干燥，避免与 N027 重复浸提。
- N354 苏格兰留兰香（Mentha × gracilis）与 N122 留兰香同走 NT1，共用 `materials/spearmint` 会配方冲突，故另设 `materials/scotch_spearmint`；N367 同理另设 `materials/dwarf_pine_needles`。
- 多形态条目各只设一个物料：N270、N281、N296、N373（油树脂/提取物，按油树脂 NT6）；N324 的油即 N047、N364 的油即 N116 与 126 A 桂皮油，本组只设油树脂；N271 的香树脂即 N217，本组只设精油。
- N277 是 α、β 两种异构体的混合物，SMILES 按主成分 α-檀香醇（PubChem 11085337）写，K4 指 α + β 合计。
- N395 在广藿香油中含量很低（A 路线产率低）；B 路线由愈疮木油中的愈创木醇经硫酸氢钾酸催化脱水，属化学转化，是否仍算天然香料待定；立体构型取 PubChem 15560252。
- N359 按通行的大肠杆菌发酵写，另提菌种 `cultures/e_coli_threonine`；本次检索受限，未取到可核对的工业来源链接（未确认）。
- N360 现实工业法为甘氨酸与甲醛（或甲醇）经丝氨酸羟甲基转移酶转化（US5382517）；本组用已有 `cultures/e_coli` 全细胞（游戏简化，现实为高表达 glyA 的工程菌或甲基营养菌）。
- N375 用新增酶 `enzymes/naringinase` 水解橙皮苷，现实也常用稀硫酸–甲醇回流酸水解；酶法依据未取到可核对链接（未确认）。
- N399 现实先以 α-L-鼠李糖苷酶只切除鼠李糖得异槲皮苷，再用 CGTase 以淀粉为供体葡萄糖基化；游戏中柚苷酶须设定为只切鼠李糖（游戏简化）。
- N378“Sesame dist.”工艺未确认，按焙炒芝麻水蒸气蒸馏（NT1）写；N380 朗姆酒净油工艺未确认，按朗姆酒液液萃取后乙醇净化（NT4、NT5）写。
- N352 用 09 `wood_tar`（游戏简化：松木焦油以木焦油代），建议作为现有木焦油精馏配方的一个馏分输出，不另立配方。
- 游戏简化：N330 以 `materials/kelp` 代墨角藻（Fucus vesiculosus）等褐藻；N291、N292 以原版蒲公英整株代根。
- N342 中文“花椒提取物”与英文“prickly ash bark”（美洲花椒属树皮）部位不同，本组按国内习惯用花椒果皮（Zanthoxylum bungeanum）。
- N294 提取部位未确认，按树皮写。
- N361 灵猫净油、N390 龙涎香酊是动物源，现实受动物保护与贸易限制，游戏中暂无来源，可能长期做不出。
- N321 在表 B.2 中没有对应的柏木油条目，模板写 NT1、NT9 连做；N269、N273、N277、N283、N301、N305、N350、N395 的原料是 N1、N2 组品种（写编码）。
- 新中间体 4 个：`cane_molasses`（甘蔗制糖的母液联产；原 `molasses`，2026-11-12 改名）、`crude_fusel_oil`（128 乙醇精馏侧线，128 可直接引用）、`hesperidin`、`rutin`。
- 原表用字：N388 写作“毕澄茄”，N198 写作“荜澄茄”，照抄原表，疑为原表用字不一；其余中文名与 PDF 原表一致。英文名改正原表拼写：Canarium commune（原作 cimmune）、Helichrysum angustifolium（augustifolium）、Lavandula angustifolia（angustidolia）、Pelargonium（Pelargonlium）、Iris germanica（germanical）、Galipea officinalis（offincinalis）、Castanea dentata（dentate）、Cinnamomum（Cinnamomaum）、Homoeriodictyol（Homoeriodyctiol）。
- 母菊（Matricaria chamomilla，N287、N309、N357）与 N2 的罗马春黄菊（`materials/roman_chamomile`）不同种，另设 `materials/german_chamomile`。

## 7 依据

模板依据见 [19](19-batch-5-synthetic-flavors.md) 第 4 节；天然香料各组查阅的来源如下。

| 链接 | 支持的编码或模板 |
| --- | --- |
| [Absolute (perfumery)](https://en.wikipedia.org/wiki/Absolute_(perfumery)) | NT4、NT5：浸膏与净油（N031、N032、N055、N056、N068–N071、N081、N091、N094–N096、N118、N120、N124、N128 等） |
| [Oil of Betula (BPC 1911)](https://henriettes-herb.com/eclectic/bpc1911/betula-lent_oleu.html) | NT12、N125（树皮浸渍后蒸馏，冬青苷经桦木酶水解生成水杨酸甲酯，含量 ≥98%） |
| [Gaultheria procumbens](https://en.wikipedia.org/wiki/Gaultheria_procumbens) | NT12、N028（叶须发酵至少三天才出油） |
| [Garlic oil](https://en.wikipedia.org/wiki/Garlic_oil) | NT12、N015、N016（压碎大蒜水蒸气蒸馏，二烯丙基二硫醚约 60%） |
| [Cognac oil（ChemicalBook）](https://m.chemicalbook.com/ProductChemicalPropertiesCB0402383_EN.htm) | N017（葡萄酒酒脚水蒸气蒸馏，主成分脂肪酸乙酯、异戊酯） |
| [Wine or Cognac Oil（A Treatise on Beverages）](https://chestofbooks.com/food/beverages/A-Treatise-On-Beverages/Wine-Or-Cognac-Oil.html) | N017（酒脚加水蒸汽蒸馏，辛酸乙酯） |
| [Orris root](https://en.wikipedia.org/wiki/Orris_root) | N057、N058（根茎干燥陈化，蒸馏得鸢尾脂，含肉豆蔻酸） |
| [Castoreum](https://en.wikipedia.org/wiki/Castoreum) | N115（干燥香囊破碎后乙醇提取） |
| [Labdanum](https://en.wikipedia.org/wiki/Labdanum) | N063（枝叶煮提或溶剂提取） |
| [Balsam of Tolu](https://en.wikipedia.org/wiki/Balsam_of_Tolu) | N043、N044（割树干收香脂，含苯甲酸苄酯、肉桂酸苄酯） |
| [Separation of terpenes from lemon essential oil by selective fractionation under a vacuum](https://journals.uran.ua/eejet/article/view/160220) | NT9：N087、N088（同法 N035、N098、N111、N132） |
| [21 CFR 184.1408 甘草提取物（ChemicalBook 转载）](https://m.chemicalbook.com/ProductChemicalPropertiesCB1492372_EN.htm) | N027（甘草水提浓缩，同 15 第 352 号依据） |
| 04 第 30 号 A 路线（内部文档） | N104、N105、`materials/cured_vanilla_bean` |
| 15 第 216、329、352 号（内部文档） | `materials/cocoa_nibs`、`materials/black_tea`、N027 |
| https://www.law.cornell.edu/cfr/text/21/184.1848 | N159、NT13（脱脂乳加柠檬酸培养乳酸菌后水蒸气蒸馏） |
| https://www.thegoodscentscompany.com/data/fl1013031.html | N239（白脱酸与乙醇酯化） |
| https://www.femaflavor.org/flavor-library/butter-acids | N240（品种依据；工艺同 15 第 276 号 A 皂化–酸化） |
| https://femaflavor.org/flavor-library/apricot-kernel-oil-prunus-armeniaca-l | N171（CAS 72869-69-3，杏核仁油） |
| https://www.thegoodscentscompany.com/data/es1038841.html | N182、NT12（种仁先压榨出脂肪油，渣再水蒸气蒸馏） |
| https://m.chemicalbook.com/ProductChemicalPropertiesCB8420437_EN.htm | N165、N220、`crude_camphor_oil`（樟脑原油析出樟脑后分馏得白樟油） |
| https://pubchem.ncbi.nlm.nih.gov/compound/159055 | N220（(1R,4R)-(+)-樟脑 SMILES） |
| https://patents.google.com/patent/US2421063 | N222（柚、葡萄柚果皮热水浸提、结晶回收柚苷） |
| https://pubchem.ncbi.nlm.nih.gov/compound/442428 | N222（柚苷 SMILES） |
| https://patents.google.com/patent/US10308674 | N248、NT16（以阿拉伯胶为原料酸水解制 L-阿拉伯糖） |
| https://pubchem.ncbi.nlm.nih.gov/compound/439195 | N248（L-阿拉伯糖 SMILES） |
| https://en.wikipedia.org/wiki/Asparagine | N230（由植物汁液结晶得到） |
| https://pubchem.ncbi.nlm.nih.gov/compound/170358 | N230（L-天冬酰胺一水合物 SMILES） |
| https://henriettes-herb.com/eclectic/bpc1911/cinchona_quin20.html | N258、NT17（树皮加石灰乳、溶剂浸提、稀酸反萃、结晶） |
| https://pubchem.ncbi.nlm.nih.gov/compound/16211283 | N258（盐酸奎宁二水合物 SMILES） |
| https://web.ist.utl.pt/ist11061/fidel/flaves/sec3/thyme.html | N235（白百里香油为红百里香油重蒸馏） |
| https://m.chemicalbook.com/ProductChemicalPropertiesCB0402383_EN.htm | N238（白康酿克油由粗康酿克油精馏） |
| https://sigmaaldrich.com/CA/en/product/aldrich/w296708 | N267（焦木酸 FEMA 2967，别名 wood vinegar，即 09 `wood_vinegar`） |
| https://bvbvf1.bib-bvb.de/vufind/PrimoRecord/cdi_epo_espacenet_DE10252532A1 | N398（山达草属植物用与水不混溶的溶剂浸提、除蜡、活性炭处理，加含钠碱液析出高圣草酚钠盐，重结晶） |
| https://api.chemicalbook.com/ProductChemicalPropertiesCB7103968_EN.htm | N395（愈疮木烯通常由愈创木醇经 KHSO4 脱水制得；天然少量存在于广藿香油） |
| https://data.epo.org/gpi/EP1669462B1 | N399（α-糖基异槲皮苷制法专利；检索结果标题，未展开核对） |
| https://patentimages.storage.googleapis.com/9f/87/82/3f0e6761aa21c6/US11242550.pdf | N403（酶法制备葡萄糖基甜菊糖苷；检索结果标题，未展开核对） |
| https://patents.google.com/patent/US4359430 | N351（从糖蜜回收甜菜碱；检索结果标题，未展开核对） |
| https://www.agrana.com/en/products/all-productportfolios/betaine/production | N351（甜菜糖蜜来源的甜菜碱生产；检索结果，未展开核对） |
| https://patents.google.com/patent/US5382517 | N360（酶法由甘氨酸制 L-丝氨酸；检索结果标题，未展开核对） |
| https://patents.google.com/patent/US4561941 | NT18、N394（果汁香精回收；检索结果标题，未展开核对） |
| https://image-ppubs.uspto.gov/dirsearch-public/print/downloadPdf/5510134 | NT18、N394（由果汁冷凝蒸汽制高浓度果香；检索结果标题，未展开核对） |
| https://en.wikipedia.org/wiki/Turpentine | N298（松脂水蒸气蒸馏得松节油，残余为松香） |
| https://en.wikipedia.org/wiki/Tragacanth | N372（黄芪属植物茎干渗出的干胶，伊朗、土耳其） |
| https://en.wikipedia.org/wiki/Katsuobushi | `materials/katsuobushi`（鲣鱼蒸煮、烟熏干燥、霉化） |
| https://pubchem.ncbi.nlm.nih.gov/compound/11085337 | N277（α-檀香醇 SMILES） |
| https://pubchem.ncbi.nlm.nih.gov/compound/62074 | N332（甘草酸铵；本组写成铵盐离子形式） |
| https://pubchem.ncbi.nlm.nih.gov/compound/247 | N351（甜菜碱 SMILES） |
| https://pubchem.ncbi.nlm.nih.gov/compound/6288 | N359（L-苏氨酸 SMILES） |
| https://pubchem.ncbi.nlm.nih.gov/compound/5951 | N360（L-丝氨酸 SMILES） |
| https://pubchem.ncbi.nlm.nih.gov/compound/5281226 | N362（胭脂树橙 SMILES） |
| https://pubchem.ncbi.nlm.nih.gov/compound/72281 | N375（橙皮素 SMILES） |
| https://pubchem.ncbi.nlm.nih.gov/compound/10621 | `hesperidin`（橙皮苷 SMILES） |
| https://pubchem.ncbi.nlm.nih.gov/compound/5280805 | `rutin`（芦丁 SMILES） |
| https://pubchem.ncbi.nlm.nih.gov/compound/15560252 | N395（β-愈疮木烯 SMILES，CAS 88-84-6） |
| https://pubchem.ncbi.nlm.nih.gov/compound/71587782 | N398（高圣草酚钠盐 SMILES，7-位酚钠） |

