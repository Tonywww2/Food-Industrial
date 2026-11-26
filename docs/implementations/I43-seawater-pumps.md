# I43 海水泵与取水（H16、M54、W03）

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 状态说明 | 1.21.1 只做了编译与数据生成，未进游戏实测 |
| 阶段 | D 设备 |
| 依赖 | I12、I14 |
| 原文档 | [additives/07 §H16 手摇海水泵、M54 电动海水泵](../additives/07-manual-crafting.md)、[additives/06 §T1 作坊](../additives/06-machines.md)、[machines/01 §2](../machines/01-recipe-types.md)、[additives/08 §C](../additives/08-raw-materials.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `manual`、`machine.workshop`、`recipe`、`world.interaction`、`platform.world` |

## 目标

海水改由海水泵获取：新增配方类型 W03 取水、环境能力“临海”、H16 手摇海水泵（T0）与 M54 电动海水泵（T1）；空桶不再直接取海水，晒盐与苦卤这条线从 T0 起照旧走得通。

## 范围

- 做：W03 配方类型、`coastal` 环境能力、H16 与 M54 两台设备、合成、贴图、取消空桶取海水、JEI 类别与条件显示
- 不做：海水的下游配方（已有，[additives/07 §手工链配方](../additives/07-manual-crafting.md)）；成品线设备（[I41](I41-food-machines-t1.md)、[I42](I42-food-machines-t2.md)）

## 任务

- [x] W03 `pumping` 取水注册，格式同 W01、W02（[machines/01 §2、§3.3](../machines/01-recipe-types.md)）：无输入物品，只有产出与条件
- [x] 环境能力“临海”`coastal`：群系在 `#minecraft:is_ocean` 或 `#minecraft:is_beach` 内，且设备六面相邻有水方块；词表按 [text-standard §5](../text-standard.md) 已补
- [x] H16 手摇海水泵：1 个储罐 1,000 mB，空手右键一次抽 125 mB，8 次满罐；桶与液体瓶取出，也每批向正下方容器送出（同 H05 出液）；合成 木桶 ×1 + 铁锭 ×2 + 木棍 ×1（[additives/07](../additives/07-manual-crafting.md)）
- [x] M54 电动海水泵：单方块，储罐 4,000 mB，每批 1,000 mB，耗时 40 tick，10 FE/t；合成 铁锭 ×4 + 活塞 ×1 + 木桶罐 ×1；接管道与漏斗按机器通用规则（[additives/06 §通用约定](../additives/06-machines.md)）
- [x] 取消空桶直接取海水：海水流体不再给桶交互，获取方式只剩两台泵（[additives/08 §C](../additives/08-raw-materials.md) 的“获取”一栏已改）
- [x] 不在临海位置时动作栏与界面显示缺少“临海”，不开工也不耗能
- [x] 贴图：H16 一张（手工设备），M54 四张（机器），文件名按 [texture-requirements](../texture-requirements.md) 已补的两行
- [x] JEI：W03 一个类别，催化剂为 H16 与 M54，条件显示“临海”（[I22](I22-jei.md) 的现成做法，计数在 [I54](I54-food-integrations.md) 复核）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 群系标签 | `minecraft:is_ocean`、`is_beach` | 同左 | 同左 |
| 桶交互 | 流体属性里关掉 | 同左 | 同左 |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 海洋与海滩群系水边：H16 八次右键满罐，M54 通电后连续出水
- [x] 内陆放置两台泵都不开工，提示缺少“临海”
- [x] 空桶对海水方块不再取到本模组海水
- [x] H06 晒盐池经泵供水仍出盐与苦卤，北豆腐这条线在 T0 走得通

## 实现记录

- W03：`ProcessTypes.PUMPING`（`pumping`，世界交互类，不进 R 类别），配方 `world.interaction.PumpingRecipe`（`result` 必须是流体，`requirements` 可选），默认耗能 10 FE/t；唯一一条配方 `foodindustrial:world/seawater`：海水 1,000 mB，要求“临海”
- “临海”`environment.coastal`：`Environment.coastal(level, pos)`，群系在 `#minecraft:is_ocean` 或 `#minecraft:is_beach` 内，且六面任一面是水（含含水方块）；只在 W03 的设备上算（M54 用 `mayHave` 让配方索引知道它可能有这项）
- H16 `hand_seawater_pump`（`manual.HandPumpBlock`、`HandPumpBlockEntity`）：不是 `T0Kind`（没有配方输入），单独注册；空手右键抽一批的 1/8，动作栏显示罐内量，不临海时显示“缺少：临海”；手持桶或液体瓶右键取出；每 4 tick 向正下方能接流体的方块送出至多 400 mB（同木桶罐）；管道只能抽出、不能注入；斧挖
- M54 `seawater_pump`（`machine.workshop.SeawaterPumpBlockEntity`）：FO 1（4,000 mB），不临海时状态为缺少“临海”、不耗能；每批 40 tick、10 FE/t，速度与升级照机器通用规则
- 取消空桶取海水：去掉了海洋群系里空桶取水换成海水桶的钩子；`RecipeInfo` 与手册“海水”一页改为说泵
- 晒盐池（H06）不是流体容器，泵与浸提桶一样只往能接流体的方块（储罐、机器）送；晒盐池仍用桶倒入
- JEI：W03 一个类别“取水”，催化剂为 H16 与 M54；页面只有产出海水与条件图标，悬停显示“临海”（`WorldPage.of(PumpingRecipe)`、`WorldCategory`）
- 手册：T0 章节加“手摇海水泵”一页，M54 按机器页自动生成
- 贴图：H16 一张（`tools/manual/manual_textures.py`），M54 四张占位图（`tools/machines/placeholder_textures.py`）
- 实测：
  - 1.20.1（2026-11-19）：内陆放 H16 右键提示“缺少：临海”；海洋群系水边 8 次右键满 1,000 mB；生存模式空桶取出得到海水桶；空桶对海洋里的水只得到原版水桶；正下方放木桶罐，1,000 mB 全部送进；海水桶倒进晒盐池，晒出盐与苦卤 250 mB；M54 临海通电每 40 tick 出 1,000 mB、10 FE/t，内陆显示缺少“临海”；JEI“取水”类别、两个催化剂、条件悬停均正常
  - 26.1.2（2026-11-19）：同样的摆法，“临海”判定、8 次满罐、送进下方木桶罐、生存模式桶取出海水桶、内陆提示缺少“临海”；M54 临海出满 4,000 mB 后停在“输出已满”，耗 1,600 FE；内陆不开工、不耗能

## 用户确认（待审）

- M54 的 FE 缓存 4,000 FE（设计未定；T1 机器默认没有缓存，够抽 400 tick，即 10 批）
- H16 做成实心方块（`forceSolidOn`），挨着流动的水放也不会被冲掉；碰撞箱 10 × 10 × 14
- 名称与说明：
  - 手摇海水泵 / Hand Seawater Pump：“放在海边水旁，空手右键抽海水，桶或瓶取出” / “Set by the sea; right-click to pump, take with a bucket”
  - 电动海水泵 / Seawater Pump：“放在海边水旁，通电抽海水” / “Set by the sea; pumps seawater on power”
- 合成（按 additives/07 的材料定的摆法）：
  - H16：` 棍 ` / `铁 木桶 铁`（木桶为原版木桶）
  - M54：` 活塞 ` / `铁 木桶罐 铁` / `铁 _ 铁`
- H16 送往正下方：每 4 tick 至多 400 mB（同木桶罐的出液速度）

## 待定

- 无
