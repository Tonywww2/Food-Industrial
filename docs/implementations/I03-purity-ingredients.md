# I03 纯度原料类型与对外 API

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | A 基础 |
| 依赖 | I02 |
| 原文档 | [additives/10 §4](../additives/10-purity-jei-ae2.md)、[additives/10 §8](../additives/10-purity-jei-ae2.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `api`、`platform.recipe` |

> 纯度模型已改为 v2（2026-11-04）：本计划中与 [I28](I28-purity-model-v2.md) 冲突的条目以 I28 为准。

## 目标

配方能写“输入纯度 ≥ 某档”，本模组、原版与他模组的配方都能用；对外 API 单独打包。

## 范围

- 做：`foodindustrial:purity`、`foodindustrial:fluid` 两种原料类型与 API 包
- 不做：配方框架本身（I08）

## 任务

- [x] `PlatformIngredients`：三个节点注册 `foodindustrial:purity`、`foodindustrial:fluid` 两种原料类型（自 I01 移入，[additives/10 §4.4](../additives/10-purity-jei-ae2.md)）
- [x] `PurityIngredient`：`test`、`getItems`（达标标准值，结果缓存）、`isEmpty` 委托给被包装原料、缓存在标签重载时失效（[additives/10 §4.1](../additives/10-purity-jei-ae2.md)，[additives/10 §4.4](../additives/10-purity-jei-ae2.md)）
- [x] `FluidPurityIngredient`：标签或流体 ID、数量、可选门槛（[additives/10 §4.2](../additives/10-purity-jei-ae2.md)）
- [x] JSON / Codec 与网络序列化（[additives/10 §4.3](../additives/10-purity-jei-ae2.md)）
- [x] API 包 `com.tonywww.foodindustrial.api`：`PurityAPI`、`PurityGrade`、两种原料的构造方法；只用原版与业务类型，随模组 jar 发布（[additives/10 §4.5](../additives/10-purity-jei-ae2.md)）
- [x] 兼容测试：原版工作台、农夫乐事厨锅配方使用纯度原料（[additives/10 §4.6](../additives/10-purity-jei-ae2.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 自定义原料 | `AbstractIngredient` + `IIngredientSerializer` | `ICustomIngredient` + 原料类型注册 | `ICustomIngredient`，`items()` 返回基础原料的全部物品，`display()` 给出达标的纯度变体 |
| JSON 类型键 | `type` | `type` | `neoforge:ingredient_type` |
| `base` 写法 | 对象（`tag` / `item`） | 对象（`tag` / `item`） | 物品 ID、`#标签` 或列表 |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] [additives/10 §8](../additives/10-purity-jei-ae2.md) 中与原料有关的条目全部勾选（机械动力配方部分移至 I25）
- [x] 农夫乐事厨锅中带门槛的输入不被过滤（1.20.1、1.21.1 节点）

## 实现记录

- 代码：`api.purity.PurityAPI`、`PurityIngredient`、`FluidPurityIngredient`；`core.purity.PurityMatching`；`platform.recipe.PlatformIngredients`、`ForgePurityIngredient`（1.20.1）、`NeoPurityIngredient`（1.21.1、26.1）；`PurityScale.THRESHOLD_CODEC`（`min_purity` 的档位名或百分比）
- 显示列表按 [additives/10 §4.4](../additives/10-purity-jei-ae2.md)：载体物品列出达标的各档标准值，外部物品在门槛不高于劣质标准值 35% 时列出；外部规则只影响判定，不影响显示列表
- 缓存：1.20.1 跟随 Forge 的原料失效计数（标签重载时清空）；1.21.1 由 NeoForge 的 `Ingredient` 按实例缓存，配方重载时重建；26.1 显示列表每次由 `display()` 计算
- 运行验证（2026-11-04）：三个节点 `/fidev ingredient` 判定与显示正确（80% 达“精制”、55.5% 不达，55.5% 门槛含边界，外部物品按规则判定，非法门槛 33.3 报错）；流体原料判定、显示与编码正确，`tag` 与 `fluid` 同时出现或数量为 0 时报错
- 农夫乐事厨锅（1.20.1、1.21.1）：带“精制”门槛的配方未被过滤，80% 的输入煮出产物，60% 的输入不反应
- 原版合成：1.21.1、26.1 用合成器（Crafter）验证，80% 合成成功、60% 不合成；1.20.1 没有合成器，配方加载与客户端同步无错误，匹配走同一 `test()`
- 客户端同步：1.20.1、1.21.1 带纯度原料的配方随登录同步到客户端，无解码错误或断线
- 机械动力未列入开发依赖，[additives/10 §8](../additives/10-purity-jei-ae2.md) 中“机械动力配方”一项留到 I25 联动时验证

- I28 改写（2026-11-04）：门槛改为 `PurityThreshold`（档位名按物料界限换算，可写 `material`，百分数不再限 0.5 的倍数）；显示列表改为门槛值与更高各档下限；`PurityAPI.getGrade` 改为 `gradeOf`（K0 与不带纯度返回空），`PurityIngredient.minK` 改为 `minPurity`。详见 [I28 实现记录](I28-purity-model-v2.md)
- I08 修正（2026-11-04）：`FluidPurityIngredient` 的可选字段改用 `StrictCodecs.optional`；此前 1.20.1 节点上写错的 `min_purity` 会被静默忽略（DataFixerUpper 6 的 `optionalFieldOf` 不报错），现在三个节点都报错（[CONVENTIONS §4.1](../../CONVENTIONS.md)）

## 待定

- 无。API 不单独打包，只按包名区分；签名只用原版与业务类型（2026-11-04 定，见 [additives/10 §4.5](../additives/10-purity-jei-ae2.md)）
