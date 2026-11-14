# I26 GTCEu 材料联动

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | F 联动与验证 |
| 依赖 | I05、I06 |
| 原文档 | [additives/09 §与 GTCEu 的联动](../additives/09-chemical-formulas.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `platform.compat.gtceu` |

## 目标

装有 GTCEu 时两边的同种材料互认，纯度门槛表现与不装时一致。

## 范围

- 做：材料对应、标签互通、不一致项处理

## 任务

- [x] 按 09 的 GT 对应列建立映射（[additives/09 §与 GTCEu 的联动](../additives/09-chemical-formulas.md)）
- [x] GT 材料按外部物品计（外部默认值，additives/10 §1.6）（[additives/09 §与 GTCEu 的联动 联动方式 3](../additives/09-chemical-formulas.md)）；需要提高的经 `BundledPurityRules` 加规则，不高于该物料的粗制下限（2026-11-04 定；上限随 I28 由 60% 改）；对应到不带纯度的物料（原油、木焦油、植物油、蒸馏水等）时不写纯度（2026-11-05，[additives/10 §1.1](../additives/10-purity-jei-ae2.md)）
- [x] 处理不一致项（糖、钾碱、明矾石、磷灰石、草木灰、岩盐等）（[additives/09 §与 GTCEu 的联动](../additives/09-chemical-formulas.md)）

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| GTCEu | 7.5.3（`deps.opt.gtceu`，只经标签联动，代码不引用） | 7.0.2 测试版（同左） | 无版本 |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 装有 GTCEu 时第一批路线仍可完成（1.20.1 开发环境实测，见实现记录）

## 实现记录

- 做法：GTCEu 把自己的粉、晶体、流体放进与其材料同名的通用标签（`forge:dusts/<名>`、`forge:<名>`，1.21.1 起 `c:`）。本模组物料与 GT 同名的（如硫酸、氨、氯化钠以外的大部分化学品），I06 生成的物料标签已含这些通用标签；名字不同的，在 `TagPlan` 里按 09 的 GT 对应列再导入 GT 名字的通用标签（可选条目），例如氯化钾标签导入 `#forge:dusts/rock_salt`，碳酸钙导入 `calcite`，熟石灰导入 `calcium_hydroxide`，硝酸钾导入 `saltpeter`，二氧化锰导入 `pyrolusite`，氯化铁导入 `iron_iii_chloride`，天然橡胶导入 `rubber`，海水导入 `salt_water`，三油酸甘油酯导入 `seed_oil`，木焦油导入 `creosote`，原油导入 `oil`，氟化氢导入 `hydrofluoric_acid`
- 不导入（`TagPlan.GTCEU_NOT_IMPORTED`）：葡萄糖（GT 的 sugar 粉就是原版糖，本模组记作蔗糖，导入会让原版糖同时算葡萄糖）、乙酸钙（GT 的是溶液流体）、镍催化剂（GT 的 nickel 是金属镍，`forge:dusts/nickel` 已属本模组的镍）、氯化氢（GT 的 hydrochloric_acid 已按同名进入本模组的盐酸）、氟磷灰石（GT apatite 的标签已属本模组的原料磷灰石）；钾碱、明矾石、草木灰与海藻灰在 GT 列本来就不对应
- 纯度：GT 物品按外部物品计（外部默认值），不加外部纯度规则，需经本模组的精制工序才能用于更高门槛（09 联动方式 3）；对应到不带纯度的物料（原油、木焦油、植物油、海水等）时本来就不写纯度
- 氯化镁的结晶水：GT 为无水 MgCl2，本模组为六水合物，两者同名、标签互通，化学式以本模组为准（09 已注明）
- 验证：三个节点数据生成通过，标签文件含上述导入。由于本模组不删除、不替换任何自有物品，第一批路线的配方与物品在装 GT 时不变，GT 物品只是多了一种可用原料
- 开发环境运行 GTCEu（2026-11-11，用户要求）：原先 GTCEu 7.5.3 的 mixin（`RepairItemRecipeMixin` 用 MixinExtras 捕获局部变量）注入失败、游戏无法启动。构建改动后可运行（[CONVENTIONS §6](../../CONVENTIONS.md)）：GTCEu 以可选依赖 `deps.opt.gtceu` 加入（`-PfiWith=gtceu`）；内嵌的 `mixinextras-forge` 按模组加载，MixinExtras 才会启动；内嵌模组与 GTCEu 同批重映射，否则 GTCEu 对 Registrate 的调用留着 SRG 名；1.20.1 的 JEI 改为 15.35.0.175（GTCEu 的 JEI mixin 只认 15.20–15.35，见 [I22](I22-jei.md)）；GTCEu 加机械动力时客户端需约 3.5 GB 堆
- 1.20.1 实测（GTCEu 7.5.3 + 机械动力，服务器与客户端，2026-11-11）：启动无配方错误；JEI 加载本模组全部配方（因标签为空而不显示的 0 条，钒、铂、铑的标签由 GT 填上）；石膏标签含 GT 的石膏粉，研磨页可轮换到 GT 的石膏矿石；石磨等本模组机器照常运行
- 1.21.1：GTCEu 7.0.2 测试版在专用服务器上启动即崩溃（GTCEu 自身在服务器端引用客户端类 `ClientLevel`），只能在客户端单人世界测试

## 待审（实现时拟定，请确认）

- 无。已确认（2026-11-11，用户）：不导入的五项及理由；GT 物品不加外部纯度规则

## 待定

- 无
