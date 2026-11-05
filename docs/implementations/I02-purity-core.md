# I02 纯度核心

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | A 基础 |
| 依赖 | I01 |
| 原文档 | [additives/10 §1–3](../additives/10-purity-jei-ae2.md)、[text-standard §3.1](../text-standard.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `core.purity`、`api.purity` |

> 纯度模型已改为 v2（2026-11-04）：本计划中与 [I28](I28-purity-model-v2.md) 冲突的条目以 I28 为准。

## 目标

纯度能被读写、显示、按档位判断；外部物品与缺标签物品有确定的默认纯度，服务器与客户端一致。

## 范围

- 做：纯度刻度、档位、外部纯度规则、Tooltip 纯度行
- 不做：纯度原料类型（I03）
- 不做：配方中的纯度结算（I08）

## 任务

- [x] `PurityGrade` 枚举：五档、下限、标准值、byte、颜色、本地化键（[additives/10 §1 五个档位与纯度刻度](../additives/10-purity-jei-ae2.md)）
- [x] 刻度工具：k ↔ byte ↔ 百分比（k 0–200，b = k − 128），JSON 百分比校验为 0.5 的倍数，显示格式（整数或一位小数）（[additives/10 §1](../additives/10-purity-jei-ae2.md)，[text-standard §1](../text-standard.md)）
- [x] `Purity.get / set`：调用 `PlatformPurity`；缺标签的本模组物品与外部物品按劣质 35% 计（2026-11-04 由粗制 60% 改）（[additives/10 §1 外部物品](../additives/10-purity-jei-ae2.md)）
- [x] 外部纯度规则：数据包 `data/<ns>/foodindustrial/purity_rules/*.json`，按标签或 ID 改写外部物品与流体的纯度，多条命中取最低，加载器不设上限；登录和重载时同步到客户端（[additives/10 §1 外部纯度规则](../additives/10-purity-jei-ae2.md)，[additives/10 §4.4](../additives/10-purity-jei-ae2.md)）
- [x] Tooltip：材料与添加剂默认显示化学式和纯度行，颜色按档位；按 Shift 显示一句描述；不显示“按住 Shift”提示（[text-standard §3.1](../text-standard.md)）
- [x] 化学式显示：数据中存 ASCII，显示时转下标（[text-standard §1](../text-standard.md)）
- [x] 数据生成检查：所有本模组产出路径都写入纯度（[additives/10 §1 本模组物品缺少 Purity](../additives/10-purity-jei-ae2.md)）。检查函数 `PurityChecks.requireOnOutput`；输出编码在 I08（配方结果）、I06 / I25（战利品与联动产出）出现时接入

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 存储 | NBT `Purity` | 数据组件 | 数据组件 |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 换算单元测试覆盖全部 201 个 k 值与五档边界
- [x] 同一物品在服务器判定与客户端 Tooltip 显示的纯度一致（含外部规则）

## 实现记录

- 代码：`api.purity.PurityGrade`；`core.purity.PurityScale`、`Purity`、`PurityRules`、`PurityChecks`；`core.text.ChemicalFormula`、`Tooltips`、`Lang`、`TextChecks`（后两项属 I04，先行用于纯度文本）
- 纯度载体由物料注册时登记（`Purity.registerItem` / `registerFluid`）；不登记的本模组物品（作物、矿石、海水等）与缺 `Purity` 的载体一样按劣质 35% 计
- 外部规则同步：`PlatformNetwork` 通道 `purity_rules`，登录与 `/reload` 时发送；`PurityRules.onChanged` 供 I03 原料缓存失效
- 单元测试：`src/test/java`，三个节点 `test` 任务共 16 项通过（覆盖 0–200 全部 k 与五档边界）
- 运行验证（2026-11-04）：三个节点专用服务器 + 客户端（Xvfb）连接，客户端收到 4 条规则，糖 35%、蜂蜜瓶 99%、石头 60%、水 35%（当时默认值为 60%） 与服务端 `/fidev purity` 一致；`/reload` 后客户端随之更新；Tooltip 显示“纯度 55.5%（劣质）”“H₂SO₄”；数据组件与 NBT 重启后保留
- 英文文本由实现方拟定，待审：`Purity %s%% (%s)`；档位 Poor / Crude / Refined / Food Grade / Lab Grade

- I28 改写（2026-11-04）：刻度改为两端对数 byte，`PurityScale` 改为查表与两种取整；`PurityGrade` 只留名称与颜色；档位界限、外部默认值改由物料数据给出（`PurityData`）；`Purity` 改为按物料登记载体，缺 `Purity` 时取物料的缺省值；外部规则的档位名按命中物品的物料换算。详见 [I28 实现记录](I28-purity-model-v2.md)

## 待定

- 无。外部纯度规则格式已定（2026-11-04）：`items` / `fluids` / `purity`，多条命中取最低，只作用于外部物品与流体，加载器不限数值，本模组自带规则不高于 60%（见 [additives/10 §1](../additives/10-purity-jei-ae2.md)）。刻度已定（2026-11-04）：p = k/200、b = k − 128；档位下限 56%/76%/95%/99%，标准值 35/60/80/95/99%；任意 0.5% 值可出现；显示整数或一位小数；JSON 写百分比。10、04、07、01、13 与文本规范已按此换算
