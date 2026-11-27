# 未完成事项（2026-11-26）

换方向前的汇总，回来时从这里找入口。各项的状态以原文档为准：实装计划看各计划的表头（索引在 [implementations/README](implementations/README.md)），贴图看 [texture-requirements](texture-requirements.md)。做完一项就从这里删掉。

## 一、等用户决定或操作

### 构建速度

实测见 [CONVENTIONS](../CONVENTIONS.md) 第 7 节 2026-11-26 的记录：慢在 Defender 实时防护，NeoForge 原地读生成资源、Stonecutter 不再复制资源两处已改。改了代码后三节点 `build` 仍要十几分钟，大头是 `jar` 和 `remapJar` 重写约 3.5 万个文件。

- 用户操作：管理员 PowerShell 加排除项，或把项目放到 Dev Drive：
  `Add-MpPreference -ExclusionPath 'C:\Users\Tony\Documents\EX\ForgeDev\FoodIndustrial','C:\Users\Tony\.gradle'`
- 待定：加不了排除项时，是否改 Forge 节点的打包流程（`remapJar` 只处理代码和手写资源，最后再装入生成资源；估计省约 7 分钟，jar 要进游戏重新验证）。

### 贴图

- 手摇柄、传动接口、模组手册现借用原版或机械动力的贴图，是否另画（texture-requirements“借用的贴图”）。
- 流体是否做成动画。
- 脚本画的范围：已定方块用脚本画，物品是否也改为脚本画。
- I55 参考图 `I55_crops_bushes_trees_16x16.png` 第二行第 4 株对应哪种作物（`tools/textures/reference/extras.py` 里留空）。
- [I33](implementations/I33-chemical-textures.md)：用户看核对页与游戏内效果（物品栏、JEI、储罐、放置的流体）。

### 待审

- [I44](implementations/I44-ingredient-sources.md)“待审”一节共 10 项：生成清单数值、农民交易、种属的处理、灵猫与河狸、肢解掉落、燕根、龙涎香概率、矿层名称与分布、螺旋藻与盐藻的样本、鸭等的他模组标签名。I51 依赖 I44，开始第 5 批前要确认。
- [I35](implementations/I35-metabolism.md)：30 天缺乏这类长周期体验是否移入[最终验收测试](final-acceptance.md)。

## 二、贴图未完成

- 未重绘：香荚兰、石花菜、麒麟菜；橡胶木门两张（门面取出来不方正）；I55 参考图没画的 9 种作物。
- 用户定以后修：I55 灌木丛（参考图画成树叶方块）、蚝礁（参考图多了底座）。
- 流体模板：等新图去掉四周深色边框、气体改回半透明后再应用。
- 参考图没画到的，以后按参考图风格另画：I44 的其余物种、灰度模板、界面、机器模块与模具等物品、实体。

## 三、实装计划

| 计划 | 状态 | 剩下的 |
| --- | --- | --- |
| [I42](implementations/I42-food-machines-t2.md) T2 多方块机组 | 进行中 | 其余六台机组的成形与样例配方；1.21.1、26.1.2 实测；AE2 随 I54 |
| [I41](implementations/I41-food-machines-t1.md) 成品线 T1 设备 | 进行中 | 农夫乐事厨锅、砧板、煎锅随有配方的批次；传动接口驱动、漏斗与侧面配置的自动化；1.21.1、26.1.2 实测 |
| [I35](implementations/I35-metabolism.md) 代谢 | 进行中 | 三节点游戏内逐项实测、换维度、配置项、多人、JFR 分配采样 |
| [I44](implementations/I44-ingredient-sources.md) 原料来源 | 进行中 | 待审 10 项（见上） |
| [I33](implementations/I33-chemical-textures.md) 物料与成品贴图 | 进行中 | 用户查看（见上） |
| I12–I15、I17 | 进行中 | 成品线新增能力随 I41 验收 |
| [I50](implementations/I50-food-batch-4.md)–[I53](implementations/I53-food-batch-7.md) 第 4–7 批成品 | 未开始 | I50 的依赖已完成，可直接开始 |
| [I54](implementations/I54-food-integrations.md) 成品线联动与验收 | 未开始 | 等 I47–I53 |

## 四、工作区

- 云端仓库的改动都已同步到用户电脑（最后一批 s78，含本文件）。提交由用户在自己的仓库做。
- 可删：`_to_delete\` 里的同步包 `fi-sync73.tar`–`fi-sync78.tar`；`build\fi-profile-build.log`、`build\fi-profile-build2.log`、`build\reports\profile\`（构建测速）；`build\blockbench\`（机器建模的导出，已装进 `tools/models/bbmodel/` 与 `*_shape.json`）。
