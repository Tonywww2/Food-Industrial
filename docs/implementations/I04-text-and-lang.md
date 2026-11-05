# I04 文本与本地化基础

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | A 基础 |
| 依赖 | I01 |
| 原文档 | [text-standard](../text-standard.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `core.text`、`platform.datagen` |

## 目标

所有名称、Tooltip、界面与 JEI 文本按文本规范自动生成和检查，违反规范时数据生成失败。

## 范围

- 做：本地化键生成、Tooltip 组件、命名与禁用词检查
- 不做：各功能自己的文本内容（随各计划提交）

## 任务

- [x] 本地化键构造工具：物品、描述、纯度、档位、JEI 类别、条件、界面（[text-standard §4](../text-standard.md)，[machines/04 §7](../machines/04-gui.md)）
- [x] Tooltip 组件：材料类、机器类、其他类三种格式（[text-standard §3.1–3.3](../text-standard.md)）
- [x] 数据生成检查：禁用词、标点、单位写法、Shift 描述长度不超过 24 字且无句号（[text-standard §1](../text-standard.md)，[text-standard §3](../text-standard.md)）
- [x] 命名检查：有日常名称的物料必须写成“专业名称（日常名称）”，日常名称不单独出现；英文名格式（[text-standard §2.1](../text-standard.md)）。日常名称由 I05 物料主表登记（`Lang.addCommonName`）
- [x] 条件词表的语言键（[text-standard §5](../text-standard.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| Tooltip 签名 | 由 `PlatformItem` 包装 | 同左 | 同左 |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] [text-standard §6](../text-standard.md) 检查清单全部自动化或列入数据生成检查

## 实现记录

- 代码：`core.text.Lang`（键构造、两种语言的条目、数据生成输出）、`TextChecks`、`Tooltips`（材料类、机器类；其他类不加附加行）、`ConditionVocabulary`、`ChemicalFormula`
- 数据生成：`lang` 生成器写 `zh_cn` / `en_us` 前先跑全部检查；任何违规都让 `runData` 失败（2026-11-04 用一条违规条目实测：禁用词、感叹号、英文小写开头均报出，构建失败）
- 条件词表按 [text-standard §5](../text-standard.md)（以 machines/01 §3 为准，2026-11-04 定）生成 43 个键；键名与英文由实现方拟定，待审
- [text-standard §6](../text-standard.md) 检查清单：Shift 描述长度与句号、禁用词、键一一对应、命名格式由数据生成检查；“默认只有化学式与纯度两行”“机器默认无附加行”由 `Tooltips` 的固定格式保证
- 单元测试：`TextChecksTest`、`ConditionVocabularyTest`、`ChemicalFormulaTest`

## 待定

- 无
