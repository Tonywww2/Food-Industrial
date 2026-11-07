# I13 原版设备桥接

| 项 | 内容 |
| --- | --- |
| 状态 | 未开始 |
| 阶段 | D 设备 |
| 依赖 | I08、I01 |
| 原文档 | [machines/01 §5](../machines/01-recipe-types.md)、[machines/02 §3 原版设备桥接](../machines/02-architecture.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `platform.recipe`、`recipe` |

## 目标

营火、熔炉、烟熏炉（T0）与高炉（T1）能执行本模组的干燥、烟熏、窑炉配方，纯度按规则写入。

## 范围

- 做：桥接配方生成与注册、原版设备档案、冲突检查
- 不做：JEI 中隐藏原版类别里的桥接配方（I22）

## 任务

- [ ] `VanillaDeviceProfiles`：四种原版设备的档次、能力、耗时倍率（[machines/01 §5](../machines/01-recipe-types.md)）
- [ ] `PlatformVanillaCooking`（`platform.recipe`）：把桥接配方注册到原版熔炼、烟熏、高炉、营火烹饪类型，适配各节点 `assemble` 参数（自 I01 移入，[machines/02 §3 原版设备桥接](../machines/02-architecture.md)）
- [ ] `VanillaCookingBridge`：从 R07–R09 配方按 `vanilla_devices` 生成；`matches` 用本模组原料判定；`assemble` 调用 `OutcomeResolver` 写入纯度（[machines/02 §3 原版设备桥接](../machines/02-architecture.md)）
- [ ] 冲突检查：同一输入在同一原版设备已有原版配方时不注册（[machines/01 §5 规则 3](../machines/01-recipe-types.md)）
- [ ] 在三个节点的反编译源码中确认熔炉、营火取产物时调用 `assemble`

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 烹饪配方输入 | 容器 | `SingleRecipeInput` | 实现前确认 |

## 验收

- [ ] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [ ] [machines/01 §5](../machines/01-recipe-types.md) 示例表的 2 条配方在对应原版设备上结果一致：海藻灰不带纯度（多组分混合物，2026-11-05），熟石膏保持输入纯度
- [ ] 档次加成另取一条来源方式的 R07–R09 配方验证：营火、熔炉（T0）与 M07 土窑（T1）结果不同
- [ ] 原木在熔炉中仍得木炭，海带在营火上仍得干海带

## 待定

- 无
