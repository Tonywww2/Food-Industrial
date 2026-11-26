# I40 模具、工具与容器包装

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 状态说明 | 塑料肠衣、PET 瓶的合成随第 5、7 批 |
| 阶段 | B 物料 |
| 依赖 | I05、I38 |
| 原文档 | [machines/05 §3、§4](../machines/05-final-design.md)、[foods/05 §5](../foods/05-equipment.md)、[texture-requirements](../texture-requirements.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `food.tool`（新包）、`material`、`registry`、`tools/textures`、`platform.datagen` |

## 目标

23 种模具与工具、12 种容器包装注册为物品，有合成、贴图与本地化名称；容器作 R29 的输入按配方消耗，返还型在倒出时还回。

## 范围

- 做：模具与工具物品、容器与包装物品、组标签（可食肠衣）、合成配方、返还规则、物品贴图与名称
- 不做：计次催化剂的消耗机制（[I38](I38-food-recipe-types.md)）；用到它们的成品配方（[I47](I47-food-batch-1.md)–[I53](I53-food-batch-7.md)）；聚乙烯、聚偏二氯乙烯、PET、铝、锡等原料本身（[I30](I30-content-batch-4.md)、[I31](I31-content-batch-5.md) 与他模组）

## 清单

| 组 | 项数 | 内容 | 原文档 |
| --- | --- | --- | --- |
| 已有 | 4 | 刀、筛网、压片冲模（第 1 批）、糖衣锅归机器模块 | [machines/05 §3](../machines/05-final-design.md) |
| 新增模具 | 19 | 包馅模、拉伸模、挤丝模、蛋糕模、吐司模、挞派模、夹板、月饼模、奶酪模、冰棍模、灌肠嘴、火腿模、豆腐模、巧克力模、硬糖模、棒棒糖模、淀粉模、吹瓶模、橡木桶 | [machines/05 §3](../machines/05-final-design.md) |
| 容器包装 | 11 | 纸袋、玻璃罐、塑料袋、纸杯、马口铁罐、塑料肠衣、可食肠衣（组标签）、玻璃瓶、铝罐、PET 瓶、滤纸袋 | [machines/05 §4](../machines/05-final-design.md) |

## 任务

- [x] 模具与工具物品注册，ID 按 [machines/05 §3](../machines/05-final-design.md)；名称与 Shift 描述按 [text-standard §3.2](../text-standard.md)（工具按机器类的一句描述）
- [x] 容器与包装物品注册，ID 与装什么按 [machines/05 §4](../machines/05-final-design.md)；可食肠衣为组标签 `casings/edible`，收天然肠衣与胶原蛋白肠衣
- [x] 返还规则：玻璃罐、玻璃瓶在倒出时还回，其余不返还，照 [foods/05 §5](../foods/05-equipment.md) 的桶与瓶同例
- [x] 容器统一用本模组玻璃瓶 `glass_bottle`，第 4 批淡奶油改接本模组瓶（[foods/15 §1](../foods/15-batch4-dairy.md)、[machines/05 §4](../machines/05-final-design.md)）（淡奶油的配方随 [I50](I50-food-batch-4.md) 写）
- [x] 合成：容器的合成按 [machines/05 §4](../machines/05-final-design.md) 的“合成”一栏；没装产锡、产铝模组时马口铁罐与铝罐做不出，照问题 14 的做法不给后备
- [x] 模具的合成：[machines/05 §7](../machines/05-final-design.md) 第 3 项留给实装，本计划先按“材料与用途相称”拟 19 条（金属模用铁锭、烘焙模用铁板或黏土、木制用木板与木棍），拟好后按 [CONVENTIONS §8](../../CONVENTIONS.md) 交用户确认再写进数据生成，结论写回 machines/05 §3
- [ ] 吹瓶模与 PET 瓶的最低档次（T3）写进配方门槛（[machines/05 §4](../machines/05-final-design.md)）（PET 随第 7 批登记，配方随 [I53](I53-food-batch-7.md)）
- [x] 贴图：模具与工具按物品贴图，容器按包装外观；张数与文件名并入 [texture-requirements](../texture-requirements.md) 的“工具与容器”一节，生成走 [I33](I33-chemical-textures.md) 的双层模板
- [x] 创造模式物品栏分组：模具与容器各一组，排在成品之后

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 物品注册 | `DeferredRegister` | 同左 | 同左 |
| 返还容器 | 配方里写 `remainder` | 同左 | 同左 |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 23 种模具与工具、11 种容器都能合成，JEI 能查到合成与用途（塑料肠衣、PET 瓶随第 5、7 批；JEI 的用途页随 I54）
- [x] 玻璃罐与玻璃瓶倒出内容后还回容器，其余按配方消耗
- [x] 没装产锡、产铝模组时马口铁罐与铝罐的配方不出现在 JEI
- [x] 全部新物品有 `zh_cn` 与 `en_us` 名称，且贴图不是占位图

## 实现记录

- 物品：`food.tool.FoodTools` 注册 21 种模具（第 1 批的筛网、压片冲模没有 ID，定为 `granulating_screen`、`tablet_die`；糖衣锅是机器模块，随 [I41](I41-food-machines-t1.md)）与 10 种容器；标签 `foodindustrial:molds`、`foodindustrial:containers`；可食肠衣标签 `foodindustrial:casings/edible` 先收胶原蛋白肠衣，天然肠衣随 [I51](I51-food-batch-5.md) 登记时加入
- 模具的 Shift 描述按机器类一句；容器不加附加行（[text-standard §3.3](../text-standard.md)）
- 返还：成品表每行可写 `container`（如 `glass_jar`）。装在玻璃罐、灌装玻璃瓶里的成品，在工作台上用掉或吃完后还回容器；1.20.1 与 1.21.1 由物品的合成剩余物给出，26.1 由 `getCraftingRemainder` 给出（`PlatformItem#craftingRemainderItem`）。成品倒进储罐（物品变流体）随 [I47](I47-food-batch-1.md) 的液体成品一起做
- 合成：纸袋、纸杯、玻璃罐、塑料袋、滤纸袋照 machines/05 §4 的数量；纸袋与纸杯同为纸 ×3，给了不同形状（见“待审”）；马口铁罐、铝罐只在锡锭、铝锭标签非空时加载（Forge `forge:tag_empty`、NeoForge `neoforge:tag_empty` 取反，`PlatformRecipeConditions.whenTagNotEmpty`）；灌装玻璃瓶在工作台用 `food_crafting`：玻璃 ×3 + 吹瓶模（留在格里）→ ×4。塑料肠衣与 PET 瓶等第 5、7 批的聚偏二氯乙烯与 PET 登记后再写
- 创造模式物品栏：新增“模具”“容器与包装”两页
- 贴图：`tools/textures/food_tool_art.py`，31 张，清单并入 [texture-requirements](../texture-requirements.md)
- 实测（2026-11-19）：
  - 1.20.1：两页物品齐全；吹瓶模 Shift 描述正确；工作台玻璃 ×3 + 吹瓶模得灌装玻璃瓶 ×4，吹瓶模留在格里；临时给酸奶设玻璃罐后，吃掉 2 个中的 1 个，玻璃罐进背包，吃掉最后 1 个，手上变成玻璃罐；装了热力（有锡）时马口铁罐配方存在，铝罐配方不存在（`/recipe give` 报未知配方）
  - 26.1.2：灌装玻璃瓶的合成与吹瓶模留格、吃酸奶还回玻璃罐同 1.20.1
- 没有测到的：成品在工作台配方里用掉时的容器返还（还没有这样的配方，随 I47）；1.21.1 上的实测；模具在机器里计次（随 I41）

## 用户确认（2026-11-18）

- 模具的合成（工作台有形状，各出 1 个；铁 = 铁锭，粒 = 铁粒，栏 = 铁栏杆，棍 = 木棍，板 = 任意木板，线 = 线，· = 空）：

| 模具 | 材料 | 形状 |
| --- | --- | --- |
| 筛网 | 木棍 ×4、铁栏杆 ×1 | `·棍·` / `棍栏棍` / `·棍·` |
| 压片冲模 | 铁锭 ×2、铁粒 ×1 | `铁` / `粒` / `铁`（竖排） |
| 包馅模 | 铁锭 ×1、铁粒 ×2 | `粒·粒` / `·铁·` |
| 拉伸模 | 铁锭 ×1、木棍 ×2 | `棍铁棍` |
| 挤丝模 | 铁锭 ×2、铁栏杆 ×1 | `铁栏铁` |
| 蛋糕模 | 铁锭 ×3、铁粒 ×2 | `铁·铁` / `粒铁粒` |
| 吐司模 | 铁锭 ×3、铁粒 ×2 | `粒·粒` / `铁铁铁` |
| 挞派模 | 铁锭 ×1、铁粒 ×2 | `粒铁粒` |
| 夹板 | 铁锭 ×2、木棍 ×1 | `铁铁棍` |
| 月饼模 | 木板 ×1、木棍 ×1 | `棍` / `板`（竖排） |
| 奶酪模 | 木板 ×4、铁粒 ×1 | `板·板` / `板粒板` |
| 冰棍模 | 铁锭 ×2、铁粒 ×3 | `粒粒粒` / `铁·铁` |
| 灌肠嘴 | 铁锭 ×2、铁粒 ×1 | `铁·铁` / `·粒·` |
| 火腿模 | 铁锭 ×4、铁粒 ×1 | `铁粒铁` / `铁·铁` |
| 豆腐模 | 木板 ×4、线 ×1 | `板·板` / `板线板` |
| 巧克力模 | 铁粒 ×5 | `粒·粒` / `粒粒粒` |
| 硬糖模 | 铁粒 ×4、铁锭 ×1 | `粒铁粒` / `粒·粒` |
| 棒棒糖模 | 铁粒 ×4、木棍 ×1 | `粒棍粒` / `粒·粒` |
| 淀粉模 | 木板 ×3、木棍 ×2 | `棍·棍` / `板板板` |
| 吹瓶模 | 铁锭 ×4、铁栏杆 ×1 | `铁栏铁` / `铁·铁` |
| 橡木桶 | 木板 ×6、铁锭 ×2（数量已定） | `板铁板` / `板·板` / `板铁板` |

- 纸袋与纸杯的形状：纸袋为三张纸竖排，纸杯为碗形（`纸·纸` / `·纸·`）
- 模具一组堆叠 16（同桶）
- 本模组玻璃瓶的名称：原版玻璃瓶也叫“玻璃瓶”，暂定“灌装玻璃瓶”（Filling Glass Bottle）以免同名

## 待定

- 无。模具的合成已由用户确认（2026-11-18），写回了 [machines/05 §7](../machines/05-final-design.md) 第 3 项
