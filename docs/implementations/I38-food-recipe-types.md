# I38 成品线配方类型与两段模型（R20–R29）

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 状态说明 | 机器上的验收随 I41、I42，JEI 随 I54 |
| 阶段 | C 框架 |
| 依赖 | I08、I09、I29 |
| 原文档 | [machines/05 §1](../machines/05-final-design.md)、[machines/01 §2、§3](../machines/01-recipe-types.md)、[foods/05 §2–§4](../foods/05-equipment.md)、[foods/07 §2、§3](../foods/07-machine-basic-design.md)、[foods/08 §1、§2](../foods/08-recipe-format-and-intermediates.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `recipe`、`recipe.food`（新包）、`platform.recipe`、`tools/foods` |

## 目标

R20–R29 十种配方类型在三个节点注册、序列化并能在机器上执行；配料段写进物品数据、加工段原样带走的两段模型就位；添加剂槽 A 与计次催化剂（模具、器具、橡木桶）的机制可用。做完后成品配方的数据生成有地方可写，机器由 [I41](I41-food-machines-t1.md)、[I42](I42-food-machines-t2.md) 接上。

## 范围

- 做：十种新配方类型与序列化、模式与条件字段、成品配方的档次参数、两段模型的物品数据传递、添加剂槽 A 的槽位角色、计次催化剂、沿用 R01–R19 在成品线的新模式、配方唯一性检查
- 不做：品质档与标签的判定算法（[I39](I39-food-quality.md)）；机器本体（[I41](I41-food-machines-t1.md)、[I42](I42-food-machines-t2.md)）；模具与容器物品（[I40](I40-molds-and-containers.md)）；配方数据本身（[I47](I47-food-batch-1.md)–[I53](I53-food-batch-7.md)）；W03 取水（[I43](I43-seawater-pumps.md)）

## 任务

### 配方类型

- [x] 十种类型按 [machines/05 §1.1](../machines/05-final-design.md) 注册：R20 `forming`、R21 `sterilizing`、R22 `cooking`、R23 `baking`、R24 `frying`、R25 `curing`、R26 `cutting`、R27 `confectionery`、R28 `chilling`、R29 `packaging`；ID、默认能耗、覆盖工艺与 [machines/01 §2](../machines/01-recipe-types.md) 的总表一致
- [x] 每种类型的模式取值按 [machines/05 §1.1](../machines/05-final-design.md) 落成枚举，词表按 [text-standard §5](../text-standard.md) 补齐（成型类的包馅、拉伸、挤压、压片、制粒、模压膨化、吹瓶；R27 的调温、浇注、包衣；R28 的速冻、凝冻、冷却；R10 的一次蒸馏与精馏；R12 的常压通气、加压、高压）
- [x] 条件：各类型“催化剂与条件”一栏的条件按 [machines/05 §1.1](../machines/05-final-design.md) 接到现有条件系统（[machines/01 §3](../machines/01-recipe-types.md)），加压蒸煮按档次下限判定（≥ T2）
- [x] 成品配方的档次参数（速率、产率，不加纯度）按 [foods/05 §4.2](../foods/05-equipment.md)
- [x] 配方写法按 [foods/08 §1、§2](../foods/08-recipe-format-and-intermediates.md)：主料、辅料、添加剂位、容器、产物与副产的字段顺序，半成品按 [foods/08 §3](../foods/08-recipe-format-and-intermediates.md) 单独注册
- [x] 唯一性检查进数据生成：同类型 + 同模式 + 同催化剂 + 同条件 + 同输入集合即冲突，数量不作区分，按 [machines/01 §3.2](../machines/01-recipe-types.md) 报错并指出两条配方的 ID

### 两段模型与添加剂槽

- [x] 配料段（R19 调配、R20 和面模式）把品质、纯度标签与添加剂记录写进产物的物品数据，按 [foods/05 §2](../foods/05-equipment.md)、[machines/05 §1.3](../machines/05-final-design.md)
- [x] 加工段把输入的品质、标签与添加剂记录原样带到产物；多输入时按 [foods/04 §2](../foods/04-quality.md) 的品质位取值
- [x] 添加剂槽 A：8 格，只在配料段设备与工作台的无序合成；不参与配方的全覆盖匹配，放不放添加剂是同一条配方；槽里有这条配方不接受的添加剂时不开工，按 [foods/05 §3](../foods/05-equipment.md)
- [x] 自动化：单方块侧面配置多“添加剂”角色，只送进 A 槽（[foods/05 §3](../foods/05-equipment.md)）；多方块物品仓的“添加剂”方向随 [I42](I42-food-machines-t2.md) 的仓口一起做
- [x] 工作台的无序合成接受添加剂，最多填满 9 格，判定同上

### 计次催化剂

- [x] 机器里的模具与器具按次数消耗，字段 `catalyst: {ingredient, runs}`，`runs` 默认 64；刀与工作台上的模具不消耗，按 [machines/05 §3](../machines/05-final-design.md)、[machines/01 §3.2](../machines/01-recipe-types.md)
- [x] 橡木桶（R25 陈酿）按同一机制计次（[foods/22 §1](../foods/22-batch7-alcohol-snacks.md)）
- [x] 耗木棍的成品（冰棍、棒棒糖）每份另耗 1 根，写成普通输入，不走催化剂（[machines/05 §3、§7](../machines/05-final-design.md)）

### 沿用类型的扩展

- [x] R01–R19 在成品线的新模式与方法按 [machines/05 §1.2](../machines/05-final-design.md)：R11 的凝固、凝乳、酶解；R19 的搅打、搅乳、高压均质；R12 用压力条件；R05 的带油翻炒与 R08 的冷熏、热熏在 machines/05 §1.2 的终稿里没有新模式
- [x] 现有设备的新增能力在 [I41](I41-food-machines-t1.md) 落地前，先把类型与模式注册齐，数据生成能写出配方

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 物品数据 | NBT（`CompoundTag`） | 数据组件 | 同 1.21.1 |
| 配方序列化 | `RecipeSerializer` + `Codec` | 同左，注册表分流 | 同左 |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 十种类型各有一条样例配方通过序列化与反序列化（JEI 类别在设备齐备后由 [I54](I54-food-integrations.md) 验）
- [x] 两条故意写重的配方让数据生成报错并指名（单元测试 `UniquenessKeyTest`，报错沿用 I29 的检查）
- [ ] 配料段写进的品质、标签与添加剂记录在退出存档、重进后仍在产物上（下一次实测时验）
- [ ] 计次催化剂用满 64 次后消耗一个，JEI 的催化剂页显示次数（机制沿用 I29；在 I40 的模具与 I41 的机器上测）

## 实现记录

- 配方类型：`ProcessTypes` 加 R20–R29（ID 照 machines/05 §1.1 终稿，与本计划起草时的 `steaming`、`freezing`、`filling_sealing` 不同），默认能耗照该表；JEI 与配方页的类别名、沉浸式展示的动作随类型加齐
- 模式：条件词表加 30 个（[text-standard §5](../text-standard.md)）。重名的改了英文 ID：`baking`、`pan_roasting`、`chilling`、`high_pressure_homogenizing`、`slicing`、`water_boiling`；成型类的包馅、拉伸等由模具区分；R10 沿用 `distillation` 与 `atmospheric_distillation`；R12 用压力条件
- 两段模型：
  - 配料段 = 配方里写了 `quality` 的那一步（R19 调配、搅打、搅乳，R20 和面，工作台）：`"quality": {"items": ["color", {"item": "taste", "accepts": [...], "count": 2}], "min_grade": "food"}`；品质、标签与添加剂标记写进产物
  - 加工段：其余配方把输入的品质合并后写到主产物（多输入每项取较低档，标签取最差，特级要全部特级，标记取并集）；副产物与流体产物是成品或半成品时也带上
  - 品质的物品数据：1.20.1 为 NBT `FoodQuality`，1.21 起为数据组件 `foodindustrial:food_quality`；流体的品质存在 `FluidAmount` 里，品质不同的流体不合并进同一储罐
  - 半成品统一用一个物品 `foodindustrial:semi_finished`，用“成品 ID|阶段”区分（如 `steamed_bun|dough`），名称显示为“成品（阶段）”；配方里写 `{"semi_finished": "steamed_bun|dough", "count": 2}`；阶段名由各批注册
- 添加剂槽 A：槽位角色 `ADDITIVE`，机器的 IO 布局多一个 `additives` 数；放了配方不收的添加剂、同一项多于两种、或数量不够时不开工，提示“配方不收的添加剂”“同一品质项的添加剂多于两种”“添加剂数量不足”；侧面配置多一个“添加剂”模式（绿色），自动化只从这个面送进 A 槽
- 工作台：新配方类型 `foodindustrial:food_crafting`（无序）：`process`、`ingredients`、`tools`（刀、模具，不消耗）、`result`、`byproduct`（留在第一格）、`quality`；三个节点各自包一层原版合成配方
- 原版桥接：营火、熔炉、烟熏炉也跑 R23 的 1 进 1 出配方，产物带上输入的品质
- 产率：主产物是成品（物品或同 ID 的流体）时不随档次加产率（[foods/05 §4.2](../foods/05-equipment.md)）
- 盐位：标签 `foodindustrial:salt_position`（盐、亚硝酸钠、亚硝酸钾），配料段把盐位上的亚硝酸盐记为误用
- 实测（2026-11-18，1.20.1）：`/fidev foodsamples` 十种类型与工作台各一条样例，解析、写回、网络编码后不变；工作台和面做出带品质的半成品面团；放色素被拒；刀与副产物留在合成格
- 没有测到的：退出重进后品质是否还在；A 槽在机器界面里的摆放与自动化（随 [I41](I41-food-machines-t1.md) 的机器测）；计次催化剂（随 I40、I41）；JEI（随 I54）

## 用户确认（2026-11-18）

- R20–R29 未写耗时的配方用的基础耗时（tick）：和面与成型 100、杀菌 200、蒸煮 200、烘烤 200、油炸 100、腌制与陈酿 12,000、切割与斩拌 100、糖果与巧克力 200、冷冻 400、灌装封口 40

## 待定

- 无。[machines/05 §7](../machines/05-final-design.md) 第 7 项“模式 ID 查重”已定，结论写回了 machines/05 与 [text-standard §5](../text-standard.md)
- 遇到设计未定的字段按 [CONVENTIONS §8](../../CONVENTIONS.md) 先求证或提问，结论写回原文档再继续
