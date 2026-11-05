# 实装计划

> 设计文档写“做成什么样”，实装计划写“按什么顺序做、做到什么程度算完成”。计划不重复设计细节：每条任务都链接到原文档的具体一节，实现时以原文档为准。设计改动后，按下方“设计文档 → 计划”对照表找到受影响的计划并更新。

## 使用规则

1. **一份计划对应一个子系统或一批内容。** 编号从 I01 起递增，不复用；文件名为 `I<NN>-<英文短名>.md`。
2. **表头是唯一的状态来源。** 状态（未开始 / 进行中 / 已完成 / 搁置 / 阻塞）、阶段、依赖、原文档都写在每份计划开头的表里；本页的索引、对照表、依赖图由 `plan.py index` 从表头生成，不手改。
3. **引用原文档写成 `[文件 §节](相对路径)`**，节号或节标题与原文档一致，例如 [additives/10 §2.1](../additives/10-purity-jei-ae2.md)。
4. **验收至少包含三节点编译与 platform 自检**（[CONVENTIONS §7](../../CONVENTIONS.md)）。
5. **任务完成就勾选**；整份计划验收通过后把状态改为“已完成”，再运行 `plan.py index`。
6. **不确定的内容不臆测**：执行计划时遇到不确定的问题，按 [CONVENTIONS §8](../../CONVENTIONS.md) 先求证或向用户提问，结论写回原文档，再更新计划；等待期间把受影响的任务标为“待确认”。

## 阶段与里程碑

| 阶段 | 内容 |
| --- | --- |
| A 基础 | 版本适配层、纯度、纯度原料、文本 |
| B 物料 | 物料与流体注册、标签、世界生成 |
| C 框架 | 配方框架、机器框架、界面、物流方块 |
| D 设备 | T0 工具与世界交互、原版设备、T1–T4 机器 |
| E 内容 | 三批添加剂、成品 |
| F 联动与验证 | JEI、Patchouli、AE2、他模组、GTCEu、性能 |

| 里程碑 | 包含的计划 | 完成标志 |
| --- | --- | --- |
| M-A 框架可运行 | I01–I04、I08、I09、I28 | M11 粉碎机样例在三个节点上运行配方、被漏斗自动化，性能规范逐条落实 |
| M-B 第一批可玩 | I05–I07、I10、I12–I14、I18、I22 | 第一批 30 项在 T0–T1 可做，JEI 可查 |
| M-C 工业线 | I11、I15–I17、I19、I24 | 第二批可做，AE2 自动化测试通过 |
| M-D 全内容与联动 | I20、I21、I23、I25–I27 | 第三批、成品名称、手册、联动、性能基准完成 |

## 计划索引

<!-- plan-index:start -->
| 编号 | 计划 | 阶段 | 状态 | 依赖 |
| --- | --- | --- | --- | --- |
| [I01](I01-platform-api.md) | platform 适配层扩展 | A 基础 | 已完成 | — |
| [I02](I02-purity-core.md) | 纯度核心 | A 基础 | 已完成 | I01 |
| [I03](I03-purity-ingredients.md) | 纯度原料类型与对外 API | A 基础 | 已完成 | I02 |
| [I04](I04-text-and-lang.md) | 文本与本地化基础 | A 基础 | 已完成 | I01 |
| [I05](I05-materials-registry.md) | 物料与流体注册 | B 物料 | 已完成 | I02、I04 |
| [I06](I06-tags-and-external-rules.md) | 标签与外部物品规则 | B 物料 | 已完成 | I05 |
| [I07](I07-worldgen-and-crops.md) | 世界生成、作物与采集方块 | B 物料 | 已完成 | I05 |
| [I08](I08-recipe-framework.md) | 配方框架 | C 框架 | 已完成 | I02、I03、I28 |
| [I09](I09-machine-framework.md) | 机器框架 | C 框架 | 已完成 | I01、I08 |
| [I10](I10-machine-gui.md) | 机器界面与配置器 | C 框架 | 已完成 | I09、I04 |
| [I11](I11-logistics-blocks.md) | 辅助物流方块 | C 框架 | 已完成 | I09 |
| [I12](I12-t0-manual-devices.md) | T0 手工工具、被动设备与世界交互 | D 设备 | 未开始 | I08、I09、I07 |
| [I13](I13-vanilla-device-bridge.md) | 原版设备桥接 | D 设备 | 未开始 | I08、I01 |
| [I14](I14-machines-t1.md) | T1 作坊机器（M01–M10） | D 设备 | 未开始 | I09、I10、I08 |
| [I15](I15-machines-t2.md) | T2 机器（M11、M12、M14、M15、M17–M21） | D 设备 | 未开始 | I14 |
| [I16](I16-machines-t3.md) | T3 化工机器（M22–M28） | D 设备 | 未开始 | I15 |
| [I17](I17-machines-t4.md) | T4 生物与精细机器（M29–M31） | D 设备 | 未开始 | I16 |
| [I18](I18-content-batch-1.md) | 第一批添加剂内容（1–30） | E 内容 | 未开始 | I05、I06、I12、I13、I14 |
| [I19](I19-content-batch-2.md) | 第二批添加剂内容（31–78） | E 内容 | 未开始 | I18、I15、I16 |
| [I20](I20-content-batch-3.md) | 第三批添加剂内容（79–139） | E 内容 | 未开始 | I19、I17 |
| [I21](I21-finished-products.md) | 成品物品（名称阶段） | E 内容 | 未开始 | I05、I04 |
| [I22](I22-jei.md) | JEI 集成 | F 联动与验证 | 未开始 | I08、I13、I14 |
| [I23](I23-patchouli.md) | Patchouli 手册与多方块预览 | F 联动与验证 | 未开始 | I14、I04 |
| [I24](I24-ae2.md) | AE2 兼容验证 | F 联动与验证 | 未开始 | I15 |
| [I25](I25-mod-integrations.md) | 他模组联动 | F 联动与验证 | 未开始 | I06、I08 |
| [I26](I26-gtceu.md) | GTCEu 材料联动 | F 联动与验证 | 未开始 | I05、I06 |
| [I27](I27-performance.md) | 性能基准与回归 | F 联动与验证 | 未开始 | I09、I16 |
| [I28](I28-purity-model-v2.md) | 纯度模型 v2 | A 基础 | 已完成 | I02、I03、I05 |
<!-- plan-index:end -->

## 设计文档 → 计划

设计文档改动时，先在这里找到引用它的计划，逐个检查任务和验收是否需要更新。

<!-- doc-map:start -->
| 设计文档 | 引用它的计划 |
| --- | --- |
| [CONVENTIONS.md](../../CONVENTIONS.md) | [I01](I01-platform-api.md)、[I02](I02-purity-core.md)、[I03](I03-purity-ingredients.md)、[I04](I04-text-and-lang.md)、[I05](I05-materials-registry.md)、[I06](I06-tags-and-external-rules.md)、[I07](I07-worldgen-and-crops.md)、[I08](I08-recipe-framework.md)、[I09](I09-machine-framework.md)、[I10](I10-machine-gui.md)、[I11](I11-logistics-blocks.md)、[I12](I12-t0-manual-devices.md)、[I13](I13-vanilla-device-bridge.md)、[I14](I14-machines-t1.md)、[I15](I15-machines-t2.md)、[I16](I16-machines-t3.md)、[I17](I17-machines-t4.md)、[I22](I22-jei.md)、[I23](I23-patchouli.md)、[I24](I24-ae2.md)、[I25](I25-mod-integrations.md)、[I26](I26-gtceu.md)、[I27](I27-performance.md)、[I28](I28-purity-model-v2.md) |
| [docs/additives/02-gb2760-additive-list.md](../additives/02-gb2760-additive-list.md) | [I18](I18-content-batch-1.md)、[I19](I19-content-batch-2.md)、[I20](I20-content-batch-3.md)、[I21](I21-finished-products.md) |
| [docs/additives/03-first-batch.md](../additives/03-first-batch.md) | [I18](I18-content-batch-1.md) |
| [docs/additives/04-production-routes.md](../additives/04-production-routes.md) | [I18](I18-content-batch-1.md) |
| [docs/additives/05-processes.md](../additives/05-processes.md) | [I18](I18-content-batch-1.md)、[I19](I19-content-batch-2.md)、[I20](I20-content-batch-3.md) |
| [docs/additives/06-machines.md](../additives/06-machines.md) | [I14](I14-machines-t1.md)、[I15](I15-machines-t2.md)、[I16](I16-machines-t3.md)、[I17](I17-machines-t4.md)、[I23](I23-patchouli.md)、[I25](I25-mod-integrations.md) |
| [docs/additives/07-manual-crafting.md](../additives/07-manual-crafting.md) | [I07](I07-worldgen-and-crops.md)、[I12](I12-t0-manual-devices.md)、[I18](I18-content-batch-1.md)、[I23](I23-patchouli.md)、[I28](I28-purity-model-v2.md) |
| [docs/additives/08-raw-materials.md](../additives/08-raw-materials.md) | [I05](I05-materials-registry.md)、[I06](I06-tags-and-external-rules.md)、[I07](I07-worldgen-and-crops.md)、[I08](I08-recipe-framework.md)、[I25](I25-mod-integrations.md) |
| [docs/additives/09-chemical-formulas.md](../additives/09-chemical-formulas.md) | [I05](I05-materials-registry.md)、[I18](I18-content-batch-1.md)、[I19](I19-content-batch-2.md)、[I20](I20-content-batch-3.md)、[I26](I26-gtceu.md)、[I28](I28-purity-model-v2.md) |
| [docs/additives/10-purity-jei-ae2.md](../additives/10-purity-jei-ae2.md) | [I01](I01-platform-api.md)、[I02](I02-purity-core.md)、[I03](I03-purity-ingredients.md)、[I05](I05-materials-registry.md)、[I06](I06-tags-and-external-rules.md)、[I08](I08-recipe-framework.md)、[I09](I09-machine-framework.md)、[I12](I12-t0-manual-devices.md)、[I17](I17-machines-t4.md)、[I18](I18-content-batch-1.md)、[I19](I19-content-batch-2.md)、[I20](I20-content-batch-3.md)、[I21](I21-finished-products.md)、[I22](I22-jei.md)、[I23](I23-patchouli.md)、[I24](I24-ae2.md)、[I25](I25-mod-integrations.md)、[I28](I28-purity-model-v2.md) |
| [docs/additives/11-roadmap.md](../additives/11-roadmap.md) | [I06](I06-tags-and-external-rules.md)、[I07](I07-worldgen-and-crops.md)、[I09](I09-machine-framework.md)、[I11](I11-logistics-blocks.md)、[I15](I15-machines-t2.md)、[I16](I16-machines-t3.md)、[I20](I20-content-batch-3.md)、[I21](I21-finished-products.md)、[I28](I28-purity-model-v2.md) |
| [docs/additives/12-finished-products.md](../additives/12-finished-products.md) | [I21](I21-finished-products.md) |
| [docs/additives/13-batch-2.md](../additives/13-batch-2.md) | [I19](I19-content-batch-2.md) |
| [docs/additives/14-batch-3.md](../additives/14-batch-3.md) | [I20](I20-content-batch-3.md) |
| [docs/machines/01-recipe-types.md](../machines/01-recipe-types.md) | [I08](I08-recipe-framework.md)、[I12](I12-t0-manual-devices.md)、[I13](I13-vanilla-device-bridge.md)、[I14](I14-machines-t1.md)、[I15](I15-machines-t2.md)、[I16](I16-machines-t3.md)、[I18](I18-content-batch-1.md)、[I19](I19-content-batch-2.md)、[I20](I20-content-batch-3.md)、[I21](I21-finished-products.md)、[I22](I22-jei.md)、[I24](I24-ae2.md)、[I28](I28-purity-model-v2.md) |
| [docs/machines/02-architecture.md](../machines/02-architecture.md) | [I01](I01-platform-api.md)、[I05](I05-materials-registry.md)、[I08](I08-recipe-framework.md)、[I09](I09-machine-framework.md)、[I10](I10-machine-gui.md)、[I12](I12-t0-manual-devices.md)、[I13](I13-vanilla-device-bridge.md)、[I14](I14-machines-t1.md)、[I15](I15-machines-t2.md)、[I16](I16-machines-t3.md)、[I17](I17-machines-t4.md)、[I23](I23-patchouli.md)、[I27](I27-performance.md) |
| [docs/machines/03-io-layout.md](../machines/03-io-layout.md) | [I01](I01-platform-api.md)、[I05](I05-materials-registry.md)、[I08](I08-recipe-framework.md)、[I09](I09-machine-framework.md)、[I10](I10-machine-gui.md)、[I11](I11-logistics-blocks.md)、[I14](I14-machines-t1.md)、[I15](I15-machines-t2.md)、[I16](I16-machines-t3.md)、[I17](I17-machines-t4.md) |
| [docs/machines/04-gui.md](../machines/04-gui.md) | [I01](I01-platform-api.md)、[I04](I04-text-and-lang.md)、[I09](I09-machine-framework.md)、[I10](I10-machine-gui.md)、[I11](I11-logistics-blocks.md)、[I22](I22-jei.md) |
| [docs/text-standard.md](../text-standard.md) | [I01](I01-platform-api.md)、[I02](I02-purity-core.md)、[I04](I04-text-and-lang.md)、[I05](I05-materials-registry.md)、[I08](I08-recipe-framework.md)、[I22](I22-jei.md)、[I23](I23-patchouli.md)、[I28](I28-purity-model-v2.md) |
<!-- doc-map:end -->

## 依赖图

<!-- dep-graph:start -->
```mermaid
flowchart LR
  I01["I01 platform 适配层扩展"]
  I02["I02 纯度核心"]
  I03["I03 纯度原料类型与对外 API"]
  I04["I04 文本与本地化基础"]
  I05["I05 物料与流体注册"]
  I06["I06 标签与外部物品规则"]
  I07["I07 世界生成、作物与采集方块"]
  I08["I08 配方框架"]
  I09["I09 机器框架"]
  I10["I10 机器界面与配置器"]
  I11["I11 辅助物流方块"]
  I12["I12 T0 手工工具、被动设备与世界交互"]
  I13["I13 原版设备桥接"]
  I14["I14 T1 作坊机器（M01–M10）"]
  I15["I15 T2 机器（M11、M12、M14、M15、M17–M21）"]
  I16["I16 T3 化工机器（M22–M28）"]
  I17["I17 T4 生物与精细机器（M29–M31）"]
  I18["I18 第一批添加剂内容（1–30）"]
  I19["I19 第二批添加剂内容（31–78）"]
  I20["I20 第三批添加剂内容（79–139）"]
  I21["I21 成品物品（名称阶段）"]
  I22["I22 JEI 集成"]
  I23["I23 Patchouli 手册与多方块预览"]
  I24["I24 AE2 兼容验证"]
  I25["I25 他模组联动"]
  I26["I26 GTCEu 材料联动"]
  I27["I27 性能基准与回归"]
  I28["I28 纯度模型 v2"]
  I01 --> I02
  I02 --> I03
  I01 --> I04
  I02 --> I05
  I04 --> I05
  I05 --> I06
  I05 --> I07
  I02 --> I08
  I03 --> I08
  I28 --> I08
  I01 --> I09
  I08 --> I09
  I09 --> I10
  I04 --> I10
  I09 --> I11
  I08 --> I12
  I09 --> I12
  I07 --> I12
  I08 --> I13
  I01 --> I13
  I09 --> I14
  I10 --> I14
  I08 --> I14
  I14 --> I15
  I15 --> I16
  I16 --> I17
  I05 --> I18
  I06 --> I18
  I12 --> I18
  I13 --> I18
  I14 --> I18
  I18 --> I19
  I15 --> I19
  I16 --> I19
  I19 --> I20
  I17 --> I20
  I05 --> I21
  I04 --> I21
  I08 --> I22
  I13 --> I22
  I14 --> I22
  I14 --> I23
  I04 --> I23
  I15 --> I24
  I06 --> I25
  I08 --> I25
  I05 --> I26
  I06 --> I26
  I09 --> I27
  I16 --> I27
  I02 --> I28
  I03 --> I28
  I05 --> I28
```
<!-- dep-graph:end -->

## 新建计划

用 `plan.py`（只需 Python 3 标准库）：

```powershell
# 新一批内容：按内容模板生成 I28-content-batch-4.md，并重建本页索引
python docs\implementations\plan.py new content-batch-4 "第四批添加剂内容" --kind content --stage "E 内容" --deps I20 --docs ../additives/15-batch-4.md

# 新子系统：按系统模板生成
python docs\implementations\plan.py new seasoning-framework "调味品工艺框架" --kind system --stage "C 框架" --deps I08,I09 --docs ../seasonings/README.md

# 手改表头后重建索引；提交前检查编号、依赖、状态与所有链接
python docs\implementations\plan.py index
python docs\implementations\plan.py check
```

不用脚本时：复制 [_template-system.md](_template-system.md) 或 [_template-content.md](_template-content.md)，改名为下一个编号，替换 `{{…}}` 占位，再运行 `plan.py index`。

新增设计内容时放到哪里：

| 新增的设计内容 | 做法 |
| --- | --- |
| 新一批添加剂（如 `additives/15-batch-4.md`） | `new --kind content`，依赖上一批内容计划；清单按批次文档的分组填写 |
| 新的设计文档目录（如调味品） | 先为它的框架部分建 system 计划，再为内容建 content 计划 |
| 已有子系统的新规则（如新的纯度规则、新的配方条件） | 在对应计划的任务表末尾追加，链接到原文档新增的一节 |
| 新机器或新模块 | 追加到对应阶段的机器计划（I14–I17）；新阶段或新体系另建 system 计划 |
| 新配方类型 | I08 追加类型，I22 追加 JEI 类别 |
| 新的联动模组 | I25 追加一节；工作量大时另建计划 |

## 文件

| 文件 | 用途 |
| --- | --- |
| [_template-system.md](_template-system.md) | 子系统计划模板：目标、范围、任务、节点差异、验收、待定 |
| [_template-content.md](_template-content.md) | 内容计划模板：目标、范围、清单、通用步骤、验收、待定 |
| [plan.py](plan.py) | 新建计划、重建索引、检查链接 |
