# I25 他模组联动

| 项 | 内容 |
| --- | --- |
| 状态 | 未开始 |
| 阶段 | F 联动与验证 |
| 依赖 | I06、I08 |
| 原文档 | [additives/06 §他模组机器参与](../additives/06-machines.md)、[additives/08 §B](../additives/08-raw-materials.md)、[additives/10 §7](../additives/10-purity-jei-ae2.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `platform.compat.<模组>` |

## 目标

农夫乐事、机械动力、热力、通用机械、气动工艺、核电工艺的机器能执行对应工序，作物模组经标签接入。

## 范围

- 做：各模组的等价配方注册、开关、节点守卫
- 不做：GTCEu（I26）

## 任务

- [ ] 农夫乐事：厨锅、砧板配方；煎锅与炉灶只跑原版营火配方（[additives/06 §他模组机器参与](../additives/06-machines.md)）
- [ ] 机械动力：磨石、粉碎轮、搅拌、压制、洗涤、鼓风机（[additives/06 §他模组机器参与](../additives/06-machines.md)）
- [ ] 机械动力配方使用 `foodindustrial:purity` 时判定正确，客户端与服务端一致（自 I03 移入，[additives/10 §8](../additives/10-purity-jei-ae2.md)）
- [ ] 热力、通用机械、气动工艺、核电工艺的对应机器（[additives/06 §他模组机器参与](../additives/06-machines.md)）
- [ ] 输出纯度：生成联动配方时按本模组配方的纯度规则、输入取门槛、农夫乐事与机械动力按 T1、其余按 T2 算出固定值（[additives/10 §7](../additives/10-purity-jei-ae2.md)）
- [ ] 潘马斯等作物模组只到标签层（[additives/08 §B 计划联动的作物模组](../additives/08-raw-materials.md)）
- [ ] 各联动模组的外部纯度规则（不高于该物料的粗制下限，I28 起；原为 60%）经 `BundledPurityRules` 加入（自 I06 移入，2026-11-04 定；[additives/10 §1](../additives/10-purity-jei-ae2.md)）
- [ ] 每个联动可在配置中关闭；未安装时整文件排除（[CONVENTIONS §2.1](../../CONVENTIONS.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 可用模组 | 农夫乐事、热力、气动 | 农夫乐事、气动 | 暂无 |

## 验收

- [ ] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [ ] 安装与不安装各联动模组时都能启动

## 待定

- 机械动力等尚未加入依赖，按节点可用性逐个加入
