# I39 品质、标签与成品 Tooltip

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 状态说明 | 每种成品的数值与检查随 I45 与各批 |
| 阶段 | C 框架 |
| 依赖 | I38、I04、I34 |
| 原文档 | [foods/04](../foods/04-quality.md)、[foods/01 §5](../foods/01-overview.md)、[text-standard §3.3、§3.4](../text-standard.md)、[foods/05 §3](../foods/05-equipment.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `food.quality`（新包）、`gui`、`gui.client`、`platform.client`、`tools/foods` |

## 目标

成品带品质档与纯度标签：配料段按四个评价项算出档位，写进物品数据；Tooltip 按文本规范显示品质行、各评价项、营养成分组件与强制声明行；品质档影响饱和度。

## 范围

- 做：四个评价项与功能类别的对应、品质位取值、协同表、品质档合成、纯度标签（特级与“土法”“劣质”）、饱食度与饱和度的换算、Tooltip 的品质行与声明行、档次颜色
- 不做：添加剂槽的槽位与匹配（[I38](I38-food-recipe-types.md)）；营养成分组件的数据与结算（[I34](I34-nutrition-data.md)、[I35](I35-metabolism.md)）；维生素与矿物质（[I46](I46-vitamins-and-minerals.md)）；每种成品的数值（[I45](I45-food-nutrition-generation.md)）

## 任务

### 品质

- [x] 四个评价项与 GB 2760 功能类别的对应表按 [foods/04 §1](../foods/04-quality.md) 落成数据（数据生成输出，便于改）
- [x] 配方的品质位按 [foods/04 §2](../foods/04-quality.md)：每项最多两个品质位，A 槽的添加剂按功能类别或配方列出的品种归项
- [x] 每项的档位按 [foods/04 §3](../foods/04-quality.md) 与协同表 [§3.1](../foods/04-quality.md)
- [x] 成品的品质档按 [foods/04 §4](../foods/04-quality.md) 合成三档（一般、良好、优）
- [x] 纯度标签：达到成品自定最低纯度给“特级”，不达要求打“土法”“劣质”并带杂质减益，按 [foods/01 §5](../foods/01-overview.md)、[foods/02 §5.1](../foods/02-metabolism.md)（杂质值由 [I35](I35-metabolism.md) 结算）
- [x] 饱食度按 `20 × √(每份能量 / 2,000 kcal)`，饱和度按品质档系数 1.0、1.2、1.4，按 [foods/04 §5.2、§5.3](../foods/04-quality.md)；一份重量按 [§5.4](../foods/04-quality.md)（换算式与品质系数已做；每种成品的能量随 [I45](I45-food-nutrition-generation.md)）
- [x] 堆叠上限：散装鲜品与汤羹 16、包装 64（[foods/01 §11](../foods/01-overview.md) 第 3 项）（成品表的 `stack` 字段，各批填）

### Tooltip

- [x] 品质行：名称下一行“品质档 · 标签”，颜色按 [text-standard §3.3](../text-standard.md)；品质一般且无标签时不显示
- [x] Shift 追加各评价项的档，写成“项名 档”，多项用“ · ”分隔（[text-standard §3.3](../text-standard.md)）
- [x] 强制声明行：投入过阿斯巴甜或天门冬酰苯丙氨酸甲酯乙酰磺胺酸的成品，在营养成分组件之前加固定声明行，灰色 §7，判定按物品数据里的添加剂记录（[text-standard §3.3](../text-standard.md)，GB 2760-2024 附录 A 表 A.1 脚注 3、脚注 9）
- [ ] 档次颜色与阶段名按 [text-standard §3.4](../text-standard.md)，与添加剂线共用 `core.Tier#color`（手册与 JEI 里的阶段名随 [I54](I54-food-integrations.md)）
- [x] 标签的颜色在实装时拟定，连同品质档的三色一并提交用户审（[text-standard §3.3](../text-standard.md) 留的空）

### 检查与工具

- [ ] 数据生成检查：每种成品的最低纯度、品质位数目、饱食度与饱和度都有值，缺项报错（数据随 [I45](I45-food-nutrition-generation.md) 与各批来，检查一并在 I45 做）
- [x] 命令 `/foodindustrial quality show`：看手上成品的品质档、各项档位与标签来源，便于调试

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 物品数据 | NBT | 数据组件 | 同 1.21.1 |
| Tooltip 图标组件 | `ClientTooltipComponent` | 同左 | 同左 |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [ ] 同一条配方放不同添加剂，品质档按协同表变化，Tooltip 随之变
- [ ] 不达最低纯度做出的成品带“土法”或“劣质”，吃下后杂质进入代谢（与 [I35](I35-metabolism.md) 联测）
- [x] 投入阿斯巴甜的成品有“阿斯巴甜（含苯丙氨酸）”一行，两种甜味剂都投入时两行按顺序出现
- [ ] 饱食度与饱和度与 [foods/04 §5.5](../foods/04-quality.md) 的原版对照表一致

## 实现记录

- 品质码：一个整数。每项两位（0 未评、1 一般、2 良好、3 优），四项共 8 位；标签两位（无、特级、土法、劣质）；再加阿斯巴甜、阿斯巴甜-安赛蜜盐、腌制剂、亚硝酸盐误用四个标记。成品档 = 已评各项的平均（≥ 1.5 为优，≥ 0.5 为良好），特级再升一档
- 合并（加工段多个带品质的输入）：每项取较低档、标签取最差、特级要全部特级、标记取并集
- 功能类别到评价项的表：`tools/foods/additive_functions.py` 生成 `foodindustrial/additive_functions.json`（[foods/04 §1](../foods/04-quality.md)）；协同表在 `food.quality.Synergies`
- 饱和度：1.20.1 由成品物品按物品数据给出（`PlatformItem#saturationFactor`）；1.21 起把乘过系数的食物组件写在物品上（`FoodLine.withQuality`，配方产物都经过它）。所以 1.21 起用 `/give` 写入品质的物品饱和度不变，调试用 `/fidev setquality <码>`
- 饱食度与饱和度的换算式在 `food.FoodValues`，单元测试对照 [foods/04 §5.5](../foods/04-quality.md) 的表
- Tooltip：品质行在名称下一行，Shift 加各项；声明行放在模组名一行之前；储罐、仓口、储罐物品与流体瓶的流体提示也显示品质
- 堆叠上限：成品表每行可写 `stack`，不写为 64；散装鲜品的 16 随各批填
- 实测（2026-11-18）：
  - 1.20.1：四种品质码的酸奶，品质行、标签颜色、Shift 各项、两种声明行都按 text-standard §3.3 显示；饥饿归零后吃一般的酸奶得 4 饱食度、2.4 饱和度，吃优的再得 4 与 3.36（×1.4）
  - 1.21.1、26.1.2：同样的吃法，饱和度 2.4 与 3.36；1.21.1 的 Tooltip 同 1.20.1
- 没有测到的：标签带来的杂质减益（代谢一侧，随 [I35](I35-metabolism.md) 联测）；同一配方放不同添加剂时的档位变化在工作台上测过一组，机器上随 [I41](I41-food-machines-t1.md)

## 用户确认（2026-11-18）

- 颜色：品质档“一般”灰 §7、“良好”绿 §a、“优”金 §6；标签“特级”淡紫 §d、“土法”黄 §e、“劣质”红 §c

## 待定

- [foods/02 §8](../foods/02-metabolism.md) 第 4 项：各档效果数值按设计拟定值实装，试玩后再调
