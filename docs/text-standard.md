# 文本规范

> 适用范围：本项目全部文本——物品与方块名称、Tooltip、JEI、Patchouli 手册、配置注释、聊天提示、设计文档。
> 版本：v0.1（2026-11-04）

## 1 全局规则

1. **只写有用信息。** 删掉不改变含义的词。禁用词：可以、能够、一种、各种、非常、十分、重要的、神奇的、你、我们、请、感叹号、表情符号。
2. **同一事物只用一个名字。** 名称以第 2 节为准，全项目不换说法。有日常名称或俗称的，一律写作 **专业名称（日常名称）**，如 氯化钠（盐）、谷氨酸钠（味精）。
3. **短句。** Tooltip 与 JEI 单行不写句号；Patchouli 与文档段落用完整句。
4. **标点。** 中文用全角标点；范围用短横线“–”（U+2013，不用连字符“-”；例 40%–73%）；流程用“→”；结晶水用“·”。化学名称内部的半角括号照标准名书写，不改全角：聚合度（“聚氧乙烯(20)山梨醇酐单硬脂酸酯”）与位次后的取代基（“3-(3-羟基-4-甲氧基苯基)丙醛”）（2026-11-05 定）。物料名称照 GB 2760 原文书写（2026-11-11 定，I31）：名称内部的半角括号（取代基“双(2-甲基-3-呋喃基)二硫醚”、构型“(+/-)-”“(E)-”、可选位次“3,5(或6)-”）与描述符之间的半角逗号（“对,α-二甲基苄醇”“反式,反式-2,4-己二烯醛”）照写；只有名称末尾、内容全是汉字的限定括号改全角，算名称本身，不是日常名称（“丁香花蕾酊（提取物）”）。
5. **空格。** 中文与英文、数字之间加一个半角空格（“T2 机械化”“1000 mB”）；物料名称中紧挨汉字的位次与构型描述符不加空格（“丁酸3-丁酮-2-醇酯”“乳酸l-薄荷酯”“5和6-癸烯酸”，2026-11-11 定，I31）；数字与 % 之间不加空格；数字与其他单位之间加空格（“150 °C”“32 FE/t”）。
6. **数字。** 一律阿拉伯数字。纯度按下列精度向下截断显示，不四舍五入，显示值不高于实际值：低于 1% 保留两位小数，不足 0.01% 写“<0.01%”；1%–10% 一位；10%–90% 整数；90%–99.9% 一位；99.9% 及以上两位。只有 byte −128、127 显示为 0%、100%（换算见设计文档 10 第 1.2 节）。
7. **单位。** 流体 mB；能量 FE、FE/t；时间 tick（玩家可见处换算为秒，保留一位小数）；温度 °C；压力 MPa。
8. **化学式。** 数据与文档中用 ASCII 数字（`Na2CO3`）；游戏内显示时把元素和括号后的数字渲染为下标（Na₂CO₃、Ca(OH)₂、CaSO₄·2H₂O、(C₆H₇NaO₆)ₙ），结晶水前的系数不下标。化学式不翻译、不本地化。
9. **英文本地化。** 规则相同：句首大写，其余小写，不加句号；英文 Tooltip 单行不超过 60 个字符；物料的英文专业名称（Shift 描述行）照名称书写，不受此限（2026-11-11 定，I31）。
10. **默认简洁，详情才展开。** 物品名、默认 Tooltip、界面主窗口与状态行、聊天提示、JEI 配方页只放必要信息；精细描述只放在 Shift Tooltip、界面信息面板、JEI 信息页、Patchouli 手册。同一信息只在一处展开（`CONVENTIONS.md` 第 9 节）。

## 2 术语来源

| 对象 | 标准名称取自 | 例 |
| --- | --- | --- |
| 食品添加剂 | GB 2760-2024 表 A.1 中文名，去掉“又名”“包括”括号 | 碳酸氢钠、谷氨酸钠、焦糖色（普通法） |
| 食品用香料 | GB 2760-2024 附录 B 中文名 | 香兰素、l-薄荷醇 |
| 其他化学品 | 中国化学会命名的常用名 | 氢氧化钠、硫酸、氯化氢 |
| 机器、工具、工艺 | 设计文档 06、07、05 中的名称 | 搅拌反应罐、淋灰桶、离心分离 |
| 纯度档位 | 固定五个：劣质、粗制、精制、食品级、实验室级 | — |
| 阶段 | 固定五个：手工、作坊、机械化、化工、生物 | — |
| 工艺条件 | 第 5 节的固定词表 | 高温、真空、无菌 |

### 2.1 专业名称（日常名称）

- 括号用全角；只取一个最通用的日常名称，不用地方叫法。
- 物品名、流体名、JEI、手册、文档全部使用同一完整写法；日常名称不单独使用。文档中同一表格行或段落内再次提及时，可只写专业名称。
- 矿物原料与提纯后的化学品分开命名：石膏矿石 / 硫酸钙（石膏）、天然碱矿 / 碳酸氢三钠（天然碱）。
- 缩写（GDL、BHT、CMC-Na 等）不作名称使用。
- 标准名已带限定括号的（焦糖色（普通法））不再追加；日常名称与专业名称相同的不追加。
- 菌种、酶等 08 物料名称里的括号是限定语（工程菌（漆酶）、橘青霉（不产毒株）），属于名称本身，不是日常名称；日常名称只出现在化学品名称中（2026-11-05 定）。
- 英文写作 `Sodium Chloride (Salt)`、`Monosodium Glutamate (MSG)`，单词首字母大写；虚词（of、and、on、in、for、with、the、or、from）与化学前缀（n-、o-、m-、p-、cis-、trans-、sec-、tert-，物料名称另有希腊字母 alpha-、γ- 等及其组合 p,α-、trans,trans-，2026-11-11 定）小写，如 `Cultures and Enzymes`、`sec-Butyl Acetate`。
- 构型前缀分两种写（2026-11-12 定，11 问题 43）：**费歇尔构型（氨基酸、糖、羟基酸等）的 D、L、DL 大写**，如 `L-Lysine`、`DL-Methionine`、`D-Ribose`、`Calcium D-Pantothenate`；**旋光方向的 d、l、dl 小写**，如 `d-Limonene`、`l-Menthol`、`dl-Camphor`、`d,l-Isomenthone`、`d-alpha-Tocopherol`。中文名取自国标，不随英文改（`d-核糖` 的英文为 `D-Ribose`）。

第一批与常用中间体的标准写法：

| 专业名称（日常名称） | 专业名称（日常名称） | 专业名称（日常名称） |
| --- | --- | --- |
| 氯化钠（盐） | 谷氨酸钠（味精） | 蔗糖（白糖） |
| 乙醇（酒精） | 乙酸（醋酸） | 冰乙酸（冰醋酸） |
| 碳酸钠（纯碱） | 碳酸氢钠（小苏打） | 碳酸氢铵（臭粉） |
| 硫酸铝钾（明矾） | 氯化镁（卤片） | 硫酸钙（石膏） |
| 葡萄糖酸-δ-内酯（内酯） | 硝酸钾（硝石） | 抗坏血酸（维生素 C） |
| 单，双甘油脂肪酸酯（单甘酯） | l-薄荷醇（薄荷脑） | 丙三醇（甘油） |
| 氢氧化钠（烧碱） | 氧化钙（生石灰） | 氢氧化钙（熟石灰） |
| 碳酸钙（石灰石） | 硫酸镁（泻盐） | 硫酸铜（胆矾） |
| 硫酸钠（元明粉） | 碳酸氢三钠（天然碱） | 半水硫酸钙（熟石膏） |
| 十水硫酸钠（芒硝） | 硫（硫磺） | 乙酸钠（醋酸钠） |
| 醋酸菌（醋母） | 硫酸亚铁（绿矾） | 硫酸锌（皓矾） |

## 3 Tooltip 规范

### 3.1 材料类、添加剂类

以后新增的其他化工产品（药品、日化品等）按本节同一格式。

适用于：原料、中间体、化学品、添加剂、菌种、酶、催化剂，以及它们的流体形态（储罐、JEI 中显示的流体）。

| 状态 | 行 | 内容 | 颜色 |
| --- | --- | --- | --- |
| 默认 | 第 1 行 | 化学式；无确定化学式（混合物、提取物、菌种）则不显示 | 元素符号按元素着色，其余浅灰（见下） |
| 默认 | 第 2 行 | `精制（97.1%）`（档位名在前，纯度在括号内，2026-11-04 定）；K0 原料与中间物写 `含量 7.3%`；物品不带纯度则不显示 | 纯度按档位着色（见下）；含量灰色 §7 |
| 按住 Shift | 追加 1 行 | 一句描述：用途或来源，不超过 24 个汉字，不加句号 | 灰色 §7 |

- 两行都没有内容时，默认状态不显示任何附加行。
- 化学式的颜色（2026-11-05 定）：元素符号按所在的 IUPAC 族着色，1–18 族各一色，镧系、锕系算第 3 族；氢不随第 1 族，单独为白色；下标数字、括号、结晶水的“·”、系数和不认识的符号一律浅灰 §7。Tooltip 化学式行与图标上的化学式都用这套颜色，色值见 `core.text.ElementColors`。
- 物品图标上的化学式（2026-11-04 定）：分类为化学品、有确定化学式的物品及其桶，在界面格子（背包、快捷栏、容器、JEI）里沿图标顶部画一行化学式，颜色同上，下标数字缩小下移；最大为半号字，放不下时缩小到占满格宽。手持、掉落物与展示框上不画。
- 不显示“按住 Shift 查看更多”之类的提示行。
- 描述句式：`<用途>，<来源>` 或二者之一。
- 尚未写描述的物料暂以专业名称占位（2026-11-04 定），之后逐条替换。

纯度颜色：

| 档位 | 颜色 |
| --- | --- |
| 劣质 | 红色 §c |
| 粗制 | 金色 §6 |
| 精制 | 青色 §b |
| 食品级 | 绿色 §a |
| 实验室级 | 品红 §d |

示例：

| 物品 | 默认 | Shift 追加 |
| --- | --- | --- |
| 碳酸钠（纯碱） | Na₂CO₃ / 精制（97.1%） | 面食用碱，由氨碱法或海藻灰制得 |
| 碳酸氢钠（小苏打） | NaHCO₃ / 食品级（99.0%） | 面点膨松剂，受热放出二氧化碳 |
| 谷氨酸钠（味精） | C₅H₈NNaO₄·H₂O / 食品级（99.0%） | 鲜味剂，由淀粉发酵制得 |
| 硫酸钙（石膏） | CaSO₄·2H₂O / 劣质（81%） | 豆腐凝固剂，由石膏矿石研磨提纯 |
| 饱和食盐水（流体） | 含量 26% | 氯碱电解、氨碱法的原料 |
| 苦卤（流体） | 不显示（不带纯度） | 晒盐母液，富含氯化镁，用于点豆腐 |
| 海藻酸钠 | (C₆H₇NaO₆)ₙ / 精制（58%） | 增稠剂，从海带中提取 |
| 醋酸菌（醋母） | 劣质（20%） | 把酒氧化成醋的菌种 |
| 镍催化剂 | Ni | 加氢催化剂，在机器内按次数消耗 |
| 海带（原版） | 不追加 | 不追加 |

> 原版与他模组物品不加本模组 Tooltip（营养成分行除外，见第 3.3 节）；它们在配方中按劣质计（外部默认值，设计文档 10 第 1.6 节），只在 JEI 输入槽提示“按劣质计”。

### 3.2 机器类

适用于：M01–M31 机器（编号含合并留下的空缺，见 machines/01）、多方块的控制器与仓口、H01–H09 手工工具。

| 状态 | 内容 | 颜色 |
| --- | --- | --- |
| 默认 | 不显示任何附加行 | — |
| 按住 Shift | 一句描述：功能，必要时加一个关键条件，不超过 24 个汉字，不加句号 | 灰色 §7 |

示例：

| 机器 | Shift 追加 |
| --- | --- |
| 搅拌反应罐 | 常压液相反应：中和、酸解、皂化、酯化 |
| 曲房 | 固态发酵与制曲，温湿度由结构决定 |
| 吸收塔 | 用液体吸收气体，塔越高效率越高 |
| 高压反应釜 | 加氢、羰基化、合成氨等高压反应 |
| 无菌发酵罐 | 无菌通气发酵，可双菌种混合 |
| 淋灰桶 | 淋洗灰与硝土，底部出浸出液 |

### 3.3 其他类别

食品成品、工具（刀、铲等）、方块装饰的其余 Tooltip 规则待成品效果设计时补充，在此之前除下面的品质行与营养成分组件外不加附加行。

品质行（2026-11-14 定，设计文档 foods/04 第 4 节）：本模组成品的品质档与纯度标签。

| 状态 | 内容 | 颜色 |
| --- | --- | --- |
| 默认 | 名称下一行：“品质档 · 标签”，如“优 · 特级”；只有一项时只写这一项；品质一般且没有标签时不显示 | 品质档：一般灰 §7、良好绿 §a、优金 §6；标签：特级淡紫 §d、土法黄 §e、劣质红 §c（2026-11-18 用户确认） |
| 按住 Shift | 品质行之后追加一行：各评价项的档，写成“项名 档”，多项用“ · ”分隔，如“状态 优 · 保存性 良好” | 项名灰 §7，档同上 |

营养成分组件（2026-11-13 定，设计文档 foods/02 第 8 节；同日改为图标组件，用户在样版中选定“默认简要 · Shift 展开”）：Tooltip 末尾（他模组放在最后的模组名行之前）追加一个图标组件，列每份所含的非零代谢成分，按咖啡因、酒精、快吸收糖、亚硝酸盐、维生素 C、维生素 B1、维生素 D、维生素 A、钙、铁、锌的顺序（亚硝酸盐 2026-11-18 加，后四项 2026-11-19 加，I46）；不列饱食度与饱和度。维生素 A 写预成型与类胡萝卜素之和，“日需”按两者之和算；服务端配置停用的营养素与杂质不列（[foods/03](foods/03-nutrition-data.md) 第 3.1 节）。

| 状态 | 内容 | 颜色 |
| --- | --- | --- |
| 默认 | 图标行：每项为成分图标（8 × 8）+“含量 单位”，一行放不下时按项折行 | 含量按成分着色（见下） |
| 按住 Shift | 换成表格：每项一行，依次为图标、成分名、“含量 单位”、注；注只用于营养素（“日需 75%”，每份含量 ÷ 每日需要量，取整，不足 1% 写“<1%”）与酒精（“≈ 0.033%”，每份折合的血液酒精浓度，三位小数） | 成分名按成分着色，含量白色，注灰色 §7 |

带杂质的食物（营养数据的 `impurity`，2026-11-13 用户要求）在快吸收糖与亚硝酸盐之后加一项杂质：图标为杂质图标，含量只写数字、不写单位，表格中不加注。

成分颜色（2026-11-13 用户确认）：咖啡因 `#C9A27E`、酒精 `#7FD3E6`、快吸收糖 `#F5B3D0`、维生素 C `#FF7A33`、维生素 B1 `#A8D26E`、维生素 D `#FFF07A`；杂质 `#B4A7C6`（拟定，与杂质图标同为紫灰色）；亚硝酸盐 `#8C9CFF`（I45）；维生素 A `#F0A860`、钙 `#E6E0D2`、铁 `#D9625B`、锌 `#8FB8D6`（2026-11-19 用户确认，I46）。

- 适用于有营养数据的全部物品，含原版与他模组的食物（数据见设计文档 foods/03）；没有非零成分、也没有杂质时不追加。
- 单位为 mg、g、µg，数字与单位之间加空格（第 1 节第 5 条）。含量的精度：100 起取整，1–100 一位小数，1 以下两位小数，四舍五入、去掉末尾的 0，低于 0.01 写“<0.01”（2026-11-13 批准）。

强制声明行（2026-11-18 定，GB 2760-2024 附录 A 表 A.1 脚注 3、脚注 9）：投入过天门冬酰苯丙氨酸甲酯（阿斯巴甜）或天门冬酰苯丙氨酸甲酯乙酰磺胺酸的本模组成品，Tooltip 在营养成分组件之前追加固定声明行。

| 投入的添加剂 | 声明行 |
| --- | --- |
| 天门冬酰苯丙氨酸甲酯（阿斯巴甜） | 阿斯巴甜（含苯丙氨酸） |
| 天门冬酰苯丙氨酸甲酯乙酰磺胺酸 | 天门冬酰苯丙氨酸甲酯乙酰磺胺酸（含苯丙氨酸） |

- 颜色灰色 §7；默认状态显示，按住 Shift 不变。
- 两种同时投入时各占一行，按上表顺序。
- 判定按成品的物品数据：配方固定投入的恒显示；配料段添加剂槽临时投入的，由该段写进产物的物品数据后显示（foods/05 第 2、3 节），经后续加工段带到最终成品。
- 投入量不设门槛，投入即声明。

### 3.4 档次（2026-11-11 定）

- 档次直接写 `T2`，不加“档次：”之类的标签。
- 每个档次一种颜色，凡提到档次的地方（Tooltip、JEI、机器描述）都用这个颜色；色值见 `core.Tier#color`。
- JEI 配方页的摘要行只写“能耗 · 耗时”（手工工具末尾加操作次数），不写档次；设备的 Tooltip 依次为设备名、档次、能耗 · 耗时、产率。
- 工艺手册不写 T 级，也不用“档次”一词，只写各档次对应的阶段名，阶段名用该档次的颜色。

| 档次 | 阶段 | 颜色 |
| --- | --- | --- |
| T0 | 手工与世界 | 土褐 `#A67C52` |
| T1 | 作坊 | 绿 `#5E9E3A` |
| T2 | 工业 | 蓝 `#3F7FC4` |
| T3 | 化工 | 橙 `#D0703A` |
| T4 | 生物与精细 | 紫 `#A35FC4` |

颜色取中等明度，在深色 Tooltip 与手册的浅色纸面上都能看清；纯度档位用的是原版亮色（§3.1），两套颜色明度不同。色值由实现方拟定，2026-11-11 用户确认。

## 4 本地化键

| 用途 | 键 | 值示例 |
| --- | --- | --- |
| 物品名 | `item.foodindustrial.<id>` | 碳酸钠（纯碱） |
| 方块名 | `block.foodindustrial.<id>` | 搅拌反应罐 |
| 流体名 | `fluid_type.foodindustrial.<id>` | 苦卤 |
| 标签名 | `tag.<类型>.<命名空间>.<路径>`（类型为 item 或 fluid，路径里的 / 换成 .；取名规则与表见 [tag-names](tag-names.md)，2026-11-25） | 面粉 |
| Shift 描述 | `tooltip.foodindustrial.<id>.desc` | 面食用碱，由氨碱法或海藻灰制得 |
| 纯度行 | `tooltip.foodindustrial.purity` | %s（%s%%）（参数依次为档位名、纯度） |
| 含量行（K0） | `tooltip.foodindustrial.content` | 含量 %s%% |
| 档位名 | `purity.foodindustrial.<poor|crude|refined|food|lab>` | 精制 |
| JEI 类别 | `jei.foodindustrial.category.<配方类型 ID>` | 研磨 |
| 工艺条件 | `condition.foodindustrial.<类别>.<取值>`（见第 5 节） | 高温 |

- 化学式不进语言文件，从物料数据（设计文档 09 的约定）读取。
- 每个键在 `zh_cn` 与 `en_us` 中同时存在；数据生成时检查缺失。

## 5 工艺条件词表（JEI、Patchouli、配置共用）

以 [machines/01 §3](machines/01-recipe-types.md) 为准（2026-11-04 定）。语言键为 `condition.foodindustrial.<类别>.<取值>`，配方 JSON 中的条件取值用同一英文词。键名与英文由实现方拟定，**待审**。

| 类别（键） | 中文 | 取值键 | English |
| --- | --- | --- | --- |
| 温度（`temperature`） | 常温 | `ambient` | Ambient |
| | 加热 | `heated` | Heated |
| | 高温 | `high` | High temperature |
| | 极高温 | `extreme` | Extreme temperature |
| | 冷却 | `cooling` | Cooling |
| | 冷冻 | `freezing` | Freezing |
| 压力（`pressure`） | 常压 | `atmospheric` | Atmospheric pressure |
| | 加压 | `pressurized` | Pressurized |
| | 高压 | `high` | High pressure |
| | 真空 | `vacuum` | Vacuum |
| 气氛（`atmosphere`） | 空气 | `air` | Air |
| | 隔绝空气 | `airtight` | Airtight |
| | 惰性 | `inert` | Inert |
| | 指定气体 | `gas` | Specified gas |
| 无菌（`sterile`） | 无菌 | `sterile` | Sterile |
| 方法（`method`） | 过滤 | `filtration` | Filtration |
| | 离心 | `centrifuge` | Centrifugation |
| | 沉降 | `settling` | Settling |
| | 明火 | `open_flame` | Open flame |
| | 溶剂回收 | `solvent_recovery` | Solvent recovery |
| | 超临界 | `supercritical` | Supercritical |
| | 真空 | `vacuum` | Vacuum |
| | 耐腐蚀 | `corrosion_resistant` | Corrosion resistant |
| | 紫外 | `ultraviolet` | Ultraviolet |
| | 聚合 | `polymerization` | Polymerization |
| | 密炼 | `internal_mixing` | Internal mixing |
| | 离子交换 | `ion_exchange` | Ion exchange |
| | 膜 | `membrane` | Membrane |
| | 电渗析 | `electrodialysis` | Electrodialysis |
| | 多馏分 | `multi_fraction` | Multiple fractions |
| | 油水分层 | `oil_separation` | Oil separation |
| | 铅室 | `lead_chamber` | Lead chamber |
| | 乳化 | `emulsifying` | Emulsifying |
| | 分子蒸馏 | `molecular_distillation` | Molecular distillation |
| 热敏（`heat_sensitivity`） | 普通 | `normal` | Normal |
| | 热敏 | `sensitive` | Heat sensitive |
| | 极热敏 | `very_sensitive` | Highly heat sensitive |
| 环境（`environment`） | 露天 | `open_sky` | Open sky |
| | 晴天 | `clear_weather` | Clear weather |
| | 寒冷群系 | `cold_biome` | Cold biome |
| | 炎热群系 | `hot_biome` | Hot biome |
| | 海洋群系 | `ocean_biome` | Ocean biome |
| | 不露天 | `sheltered` | Sheltered |
| | 淋雨 | `rain` | In the rain |
| | 白天 | `daytime` | Daytime |
| | 非寒冷群系 | `non_cold_biome` | Non-cold biome |
| | 丛林群系 | `jungle_biome` | Jungle biome |
| | 竹林 | `bamboo_biome` | Bamboo jungle |
| | 临海 | `coastal` | Coastal |
| 模式（`mode`） | 干磨 | `dry_grinding` | Dry grinding |
| | 湿磨 | `wet_grinding` | Wet grinding |
| | 开口 | `open` | Open |
| | 密闭 | `sealed` | Sealed |
| | 厌氧消化 | `anaerobic` | Anaerobic digestion |
| | 曲盘 | `koji_tray` | Koji trays |
| | 浅盘 | `shallow_tray` | Shallow trays |
| | 煅烧 | `calcining` | Calcining |
| | 烧灰 | `ashing` | Ashing |
| | 干馏 | `dry_distillation` | Dry distillation |
| | 蒸馏 | `distillation` | Distillation |
| | 水蒸气蒸馏 | `steam_distillation` | Steam distillation |
| | 升华 | `sublimation` | Sublimation |
| | 冷却结晶 | `cooling_crystallization` | Cooling crystallization |
| | 晾晒结晶 | `sun_crystallization` | Sun crystallization |
| | 烘干 | `drying` | Drying |
| | 烟熏 | `smoking` | Smoking |
| | 燃硫 | `sulfur_burning` | Sulfur burning |
| | 常压反应 | `reaction` | Reaction |
| | 聚合 | `polymerization` | Polymerization |
| | 浓缩 | `concentration` | Concentration |
| | 蒸发结晶 | `evaporative_crystallization` | Evaporative crystallization |
| | 等电点 | `isoelectric` | Isoelectric precipitation |
| | 冷冻 | `freezing` | Freezing |
| | 滚筒 | `drum_drying` | Drum drying |
| | 气流 | `flash_drying` | Flash drying |
| | 溶剂浸提 | `solvent_extraction` | Solvent extraction |
| | 液液萃取 | `liquid_extraction` | Liquid-liquid extraction |
| | 吸收 | `absorption` | Absorption |
| | 碳化 | `carbonation` | Carbonation |
| | 燃烧除氧 | `combustion` | Combustion |
| | 焙烧 | `roasting` | Roasting |
| | 缩合 | `condensation` | Condensation |
| | 熔融 | `melting` | Melting |
| | 碱熔 | `alkali_fusion` | Alkali fusion |
| | 活化 | `activation` | Activation |
| | 氯化 | `chlorination` | Chlorination |
| | 配料 | `blending` | Blending |
| | 均质 | `homogenizing` | Homogenizing |
| | 归一 | `normalizing` | Normalizing |
| | 常压精馏 | `atmospheric_distillation` | Atmospheric distillation |
| | 减压精馏 | `vacuum_distillation` | Vacuum distillation |
| | 深冷分离 | `cryogenic_separation` | Cryogenic separation |
| | 汽提 | `stripping` | Stripping |
| | 加氢 | `hydrogenation` | Hydrogenation |
| | 羰基化 | `carbonylation` | Carbonylation |
| | 合成氨 | `ammonia_synthesis` | Ammonia synthesis |
| | 加压氧化 | `pressure_oxidation` | Pressure oxidation |
| | 环氧化 | `epoxidation` | Epoxidation |
| | 乙氧基化 | `ethoxylation` | Ethoxylation |
| | 氯碱 | `chlor_alkali` | Chlor-alkali |
| | 电解还原 | `electroreduction` | Electroreduction |
| | 电解氧化 | `electrooxidation` | Electrooxidation |
| | 电解水 | `water_electrolysis` | Water electrolysis |
| | 熔盐电解 | `molten_salt` | Molten salt electrolysis |
| | 吸附 | `adsorption` | Adsorption |
| | 分级 | `fractionation` | Fractionation |
| | 再生 | `regeneration` | Regeneration |
| | 燃烧 | `burning` | Burning |
| | 气相催化 | `gas_catalysis` | Gas-phase catalysis |
| | 裂解 | `cracking` | Cracking |
| | 流化床 | `fluidized_bed` | Fluidized bed |
| | 通气发酵 | `aerated` | Aerated fermentation |
| | 平板分离 | `isolation` | Plate isolation |
| | 纯化 | `purification` | Purification |
| | 扩培 | `expansion` | Expansion |
| | 诱变 | `mutagenesis` | Mutagenesis |
| | 基因重组 | `recombination` | Genetic recombination |
| | 超滤 | `ultrafiltration` | Ultrafiltration |
| | 纳滤 | `nanofiltration` | Nanofiltration |
| | 反渗透 | `reverse_osmosis` | Reverse osmosis |
| | 双极膜电渗析 | `bipolar_electrodialysis` | Bipolar membrane electrodialysis |
| | 和面 | `dough_mixing` | Dough mixing |
| | 压延 | `sheeting` | Sheeting |
| | 切条 | `slitting` | Slitting |
| | 成型 | `molding` | Molding |
| | 巴氏杀菌 | `pasteurizing` | Pasteurizing |
| | 超高温灭菌 | `uht` | UHT sterilizing |
| | 罐内灭菌 | `retorting` | In-container sterilizing |
| | 煮 | `water_boiling` | Boiling |
| | 蒸 | `steaming` | Steaming |
| | 卤 | `braising` | Braising |
| | 烘烤 | `baking` | Baking |
| | 焙炒 | `pan_roasting` | Pan roasting |
| | 醒发 | `proofing` | Proofing |
| | 腌制 | `marinating` | Marinating |
| | 陈酿 | `aging` | Aging |
| | 切割 | `slicing` | Cutting |
| | 斩拌 | `chopping` | Chopping |
| | 灌肠 | `stuffing` | Stuffing |
| | 调温 | `tempering` | Tempering |
| | 浇注 | `depositing` | Depositing |
| | 包衣 | `panning` | Panning |
| | 冷却 | `chilling` | Chilling |
| | 速冻 | `quick_freezing` | Quick freezing |
| | 凝冻 | `churn_freezing` | Churn freezing |
| | 凝固 | `coagulating` | Coagulating |
| | 凝乳 | `curdling` | Curdling |
| | 酶解 | `enzymolysis` | Enzymatic hydrolysis |
| | 搅打 | `whipping` | Whipping |
| | 搅乳 | `churning` | Churning |
| | 高压均质 | `high_pressure_homogenizing` | High-pressure homogenizing |
| | 熬煮 | `simmering` | Simmering |
| | 蒸煮 | `cooking` | Cooking |
| | 油炸 | `frying` | Frying |
| | 杀菌 | `sterilizing` | Sterilizing |
| 纯度方式（`purity_mode`） | 来源 | `source` | Source |
| | 除杂 | `removal` | Impurity removal |
| | 保持 | `keep` | Keep |
| | 劣化 | `degrade` | Degradation |
| 随机（`random`） | 纯度随机（只用于红曲米） | `purity` | Random purity |
| | 概率产出 | `chance` | Chance output |

- 模式类与“油水分层”由 I14 新增（2026-11-10），T2 机器的模式与“铅室”“乳化”由 I15 新增，T3、T4 机器的模式与“分子蒸馏”由 I16、I17 新增（2026-11-11；“再生”只用于 M25 的再生配方），取值是机器的模式名（additives/06 §T1 作坊：操作与实现规格），配方用 `"mode"` 字段指定，不写在 `requirements` 里。
- 环境类中 machines/01 §3 列出的“时间”条件尚无取值，出现时在本表补充。不露天、淋雨、白天、非寒冷群系、丛林群系、竹林由 I12 新增（2026-11-10 定），判定见 additives/07 §操作与实现规格。
- JEI 中每个条件显示为图标，悬停显示词表中的词，不加解释句。
- 成品线的 30 个模式由 I38 新增（2026-11-18，[machines/05 §1.1、§1.2](machines/05-final-design.md)）。与已有词重名的改了英文 ID：R23 的烘烤与焙炒为 `baking`、`pan_roasting`（`roasting` 是 M20 的焙烧），R28 的冷却为 `chilling`（`cooling` 是温度条件、`freezing` 是 M15 的冷冻），R19 的高压均质为 `high_pressure_homogenizing`（`homogenizing` 是 M21 的均质），R26 的切割为 `slicing`，R22 的煮为 `water_boiling`。成型类的包馅、拉伸、挤压、压片、制粒、模压膨化不是模式，由模具（计次催化剂）区分；R10 的一次蒸馏与精馏沿用 `distillation` 与 `atmospheric_distillation`；R12 的常压通气、加压、高压用压力条件（`atmospheric`、`pressurized`、`high`），不另设模式。
- 熬煮灶的四个模式（熬煮、蒸煮、油炸、杀菌）由 I41 新增（2026-11-19，[machines/05 §2.3](machines/05-final-design.md)）：熬煮留原有配方，蒸煮、油炸、焙炒、杀菌各跑对应的成品线配方。

## 6 检查清单

- [ ] 名称与第 2 节来源一致，全项目无同物异名；有日常名称的都写成“专业名称（日常名称）”
- [ ] 材料、添加剂默认只有化学式与纯度两行
- [ ] 机器默认无附加行
- [ ] 每条 Shift 描述一句、≤ 24 个汉字、无句号、无禁用词
- [ ] 投入阿斯巴甜或天门冬酰苯丙氨酸甲酯乙酰磺胺酸的成品带“含苯丙氨酸”声明行（第 3.3 节）
- [ ] `zh_cn`、`en_us` 键一一对应
