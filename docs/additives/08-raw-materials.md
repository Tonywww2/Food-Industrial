# 08 原料清单与标签

第一批共用到约 **90 种一级原料**：原版物品 32 类、他模组原料 26 类、本模组新增 35 种（含菌种、酶与催化剂）。第二、三批另增约 80 种（“用于”一栏标 31–139 编号的行，来源见 [13](13-batch-2.md)、[14](14-batch-3.md)）；第四批另增原料 69 种、菌种 32 个、酶 49 种、耗材 1 种、催化剂 5 种（“用于”标 140–423，来源见 [15](15-batch-4.md)），其中“暂无来源（待定）”的原料要等以后补作物、生物或联动标签。**所有配方输入一律按标签匹配**：配方只引用本模组命名空间下的标签，标签再聚合原版物品、本模组物品和各模组的 `forge:` 标签；只有配方输出写具体物品。 第五批（17–19）另增原料 244 种（多数为香料植物部位，暂无来源）、菌种与酶 23 种、催化剂与吸附助滤耗材 7 种，已并入第 C 节各表。

## 标签体系

| 标签前缀 | 类型 | 内容 | 例 |
| --- | --- | --- | --- |
| `foodindustrial:materials/<名称>` | 物品 | 农产、畜产、矿物等一级原料 | `materials/kelp` |
| `foodindustrial:chemicals/<09 中的建议 ID>` | 物品 | 固体化学品与中间体 | `chemicals/soda_ash` |
| `foodindustrial:<09 中的建议 ID>` | 流体 | 液体、气体、溶液 | 流体标签 `foodindustrial:sulfuric_acid` |
| `foodindustrial:cultures/<名称>` | 物品 | 菌种 | `cultures/acetobacter` |
| `foodindustrial:enzymes/<名称>` | 物品 | 酶制剂 | `enzymes/alpha_amylase` |
| `foodindustrial:catalysts/<名称>` | 物品 | 催化剂、吸附剂 | `catalysts/nickel` |
| `foodindustrial:additives/<名称>` | 物品 | 添加剂成品（供成品配方引用） | `additives/monosodium_glutamate` |
| `foodindustrial:foods/<名称>` | 物品 | 成品（供更高级成品、农夫乐事配方引用） | `foods/tofu` |
| `foodindustrial:chemicals` | 物品、流体 | 全部化学品（分类为化学品的物料；固体进物品标签，液体、气体、溶液进同名流体标签），方便检索（2026-11-11，用户补充） | `#foodindustrial:chemicals` |
| `foodindustrial:food_categories/<大类>` | 物品 | 成品按 GB 2760 附录 E 的 16 个大类（12，I21） | `food_categories/dairy` |
| `foodindustrial:samples/<种类>` | 物品 | M30 平板分离用的自然样本（土壤、腐烂植物、水果、霉变谷物等，游戏设定）；第四批补生肉 `samples/raw_meat`（原版五种生肉）、甜菜根 `samples/beetroot`（I30，2026-11-11 实装拟定）；基因重组的供体槽另收土壤、堆肥、稻草、树叶、水生植物样本 | `samples/soil` |
| `foodindustrial:culture_bench/<槽位>` | 物品 | M30 菌种工作台四个输入槽各收的物品（2026-11-11，用户补充）：`samples`（样本或菌种：全部菌种标签与 `samples/` 标签）、`media`（培养基：琼脂、葡萄糖）、`donors`（供体样本）、`vectors`（表达载体）；能在 M30 上跑的配方，原料必须在对应槽位的标签里，各占一个槽，数据生成检查 | `culture_bench/media` |

以后扩展到药物、日化品等领域时，在本表并列新增产品类前缀，不改动已有前缀（machines/02 第 8 节）。

规则：

1. **导入**：每个本模组标签包含对应的原版物品（如有）、本模组物品（如有）和各模组常用的 `forge:`、`c:` 标签（规则 5），全部写成 `required: false`，某个模组没装也不会报错。
2. **导出**：本模组物品同时加入通用 `forge:` 与 `c:` 标签（如碳酸钠加入 `#forge:dusts/soda_ash` 与 `#c:dusts/soda_ash`，硫酸流体加入 `#forge:sulfuric_acid` 与 `#c:sulfuric_acid`），让其他模组和 GTCEu 的配方也能用。范围（2026-11-04 定）：分类为化学品的固体加入 `dusts/<ID>`，液体、气体、溶液加入 `<ID>`；气体另加入 `forge:gaseous`（Forge）与 `c:gaseous`（NeoForge），桶加入 `forge:buckets/<ID>` 与 `c:buckets/<ID>`（2026-11-12 起每个版本都写，见规则 5）。菌种、酶、催化剂、原料不导出。
3. **化学品与命名**：`chemicals/` 物品标签和化学品流体标签的名称 = 09 表中的建议 ID（农产流体如 `wine`、`whey`、`vegetable_oil` 用简短名）；固体导入 `#forge:dusts/<id>`、`#forge:gems/<id>`，流体导入 `#forge:<id>`（`c:` 同样导入，规则 5）。`materials/`（农产、矿石）、`catalysts/`、`additives/` 用简短名，可与 09 ID 不同（如 `catalysts/nickel` 对应 09 `nickel_catalyst`）。矿石与同名化学品分开注册：矿石物品 ID 加 `_ore` 后缀（`gypsum_ore` 进 `materials/gypsum`，化学品 `gypsum` 进 `chemicals/gypsum`）；`#forge:ores/<名>` 只导入 `materials/`，`#forge:dusts/<名>` 只导入 `chemicals/`，没有对应化学品的矿物粉（如磷灰石粉）才归 `materials/`。09 中分类为催化剂、又在 §C 催化剂表有行的化学品（镍催化剂、活性炭、五氧化二钒、钨酸钠、二丁基氧化锡、三氯化铝）同时进 `chemicals/<ID>` 和 §C 写的 `catalysts/<名>`（2026-11-04 定）。
4. **整合包可改**：整合包作者用数据包或 KubeJS 往本模组标签里追加物品即可，不需要改配方。
5. **两种前缀都写**（2026-11-12，用户定；原为 1.20.1 用 `forge:`、1.21 起整体换成 `c:`）：每个版本同时导入、导出 `forge:` 与 `c:` 两种通用标签，两种写法的模组在任何版本都能互通。下面 §A/§B 两栏中的标签在每个版本都写，并各自补上另一前缀的同名标签（`#forge:x` 与 `#c:x`）；直接写的物品、流体 ID 仍只取该节点那一栏（如 1.20.1 的 `minecraft:grass` 在 1.21 起是 `minecraft:short_grass`）。配方只引用本模组标签，不用动。
6. **外部物品按低纯度计**：标签匹配到的原版或他模组物品没有纯度，配方检测时按劣质处理（外部默认值：该物料粗制下限的杂质加倍，规则见 10 第 1.6 节）。
7. **校验**：数据生成阶段检查所有配方输入均为标签，且每个本模组标签至少含一个物品（见 10 检查清单）。实现（2026-11-04）：标签一侧由 I06 检查——直接写的物品、流体 ID 必须在该节点存在（原版与本模组的不存在即失败，他模组的在该节点没有时不写），每个生成的标签至少一个条目，本模组物料标签必须含其本身；只含他模组标签的 `materials/` 标签（作物模组联动）在未装对应模组时为空，数据生成时列出，不算错误。配方输入一侧的检查随 I08 的配方数据生成实现。

```json
// data/foodindustrial/tags/items/materials/lemon.json
{
  "replace": false,
  "values": [
    { "id": "#forge:fruits/lemon", "required": false },
    { "id": "#forge:crops/lemon", "required": false }
  ]
}
```

> 下表“配方引用标签”一栏省略了 `foodindustrial:` 前缀；流体标签注明“（流体）”。每行一个标签。“默认内容”中反引号内的条目由 I06 数据生成写入（`tools/materials/export_materials.py` 导出），全部 `required: false`；“本模组……”表示本模组同名物料按主表自动加入（规则 3）。“1.21 起”一栏同时用于 1.21.1 与 26.1.2；两栏的标签条目在三个节点都写（规则 5）；直接写的物品、流体 ID 只在该节点存在时写入（如农夫乐事的物品不进 26.1.2，热力的流体只进 1.20.1）。
>
> 他模组标签已于 2026-11-04 在各节点的模组 jar 中核对：Forge / NeoForge 本体、农夫乐事、热力（含 thermal_core）、气动工艺、潘马斯（Pam's HarvestCraft 2 作物、果树、食物核心）、机械动力、通用机械、沉浸工程；GTCEu 的材料标签在运行时按 `dusts/<材料>`、`gems/<材料>`、流体 `<材料>` 生成，按其命名规则写入。`dusts/<名>`、`gems/<名>`、`ingots/<名>`、流体 `<名>` 等通用写法在 1.21 起直接换成 `c:` 前缀；改了名字的按 NeoForge 的实际标签写（`leather` → `c:leathers`、`sand` → `c:sands`、`raw_fishes` → `c:foods/raw_fish`、`raw_pork` → `c:foods/raw_pork`、`berries` → `c:foods/berry`）。原版物品直接写入，不只依赖加载器标签。

## A 原版物品

| 原料 | 配方引用标签 | 默认内容（1.20.1） | 默认内容（1.21 起） | 用于（03 编号） | 主要组成 |
| --- | --- | --- | --- | --- | --- |
| 海带 | `materials/kelp` | `minecraft:kelp` | `minecraft:kelp` | 5, 15，联产碘、D-甘露糖醇 | 海藻酸、D-甘露糖醇、钠钾盐、碘化物 |
| 干海带 | `materials/dried_kelp` | `minecraft:dried_kelp` | `minecraft:dried_kelp` | 5, 15, 昆布高汤 | 同上 |
| 海草 | `materials/seagrass` | `minecraft:seagrass` | `minecraft:seagrass` | 5（低产海藻灰） | 植物纤维、无机盐 |
| 甘蔗 | `materials/sugar_cane` | `minecraft:sugar_cane` | `minecraft:sugar_cane`、`#c:crops/sugar_cane` | 蔗糖（白糖）、朗姆酒 | 蔗糖 |
| 糖 | `chemicals/sucrose` | `minecraft:sugar`、本模组蔗糖（白糖）、`#forge:sugar` | `minecraft:sugar`、本模组蔗糖（白糖）、`#c:sugar` | 18，转化糖 | 蔗糖 C12H22O11 |
| 甜菜根 | `materials/beetroot` | `minecraft:beetroot`、`#forge:crops/beetroot`、`#forge:vegetables/beetroot` | `minecraft:beetroot`、`#c:crops/beetroot`、`#c:vegetables/beetroot` | 19 | 甜菜苷、蔗糖 |
| 胡萝卜 | `materials/carrot` | `minecraft:carrot`、`#forge:crops/carrot`、`#forge:vegetables/carrot` | `minecraft:carrot`、`#c:crops/carrot`、`#c:vegetables/carrot` | 20 | β-胡萝卜素 |
| 马铃薯 | `materials/potato` | `minecraft:potato`、`#forge:crops/potato`、`#forge:vegetables/potato` | `minecraft:potato`、`#c:crops/potato`、`#c:vegetables/potato` | 淀粉 → 2, 3, 4, 13, 26, 28 | 淀粉 |
| 小麦 | `materials/wheat` | `minecraft:wheat`、`#forge:crops/wheat`、`#forge:grain/wheat` | `minecraft:wheat`、`#c:crops/wheat`、`#c:grain/wheat`、`#c:grains/wheat` | 4（面筋）、淀粉、麦芽 | 淀粉、面筋蛋白 |
| 苹果 | `materials/apple` | `minecraft:apple`、`#forge:fruits/apple` | `minecraft:apple`、`#c:fruits/apple` | 16，果酒 → 1 | 果胶、果糖、苹果酸 |
| 浆果 | `materials/berries` | `minecraft:sweet_berries`、`minecraft:glow_berries`、`#forge:berries` | `minecraft:sweet_berries`、`minecraft:glow_berries`、`#c:foods/berry` | 果酒 → 1 | 糖、有机酸 |
| 可可豆 | `materials/cocoa_beans` | `minecraft:cocoa_beans`、`#forge:crops/cocoabeans` | `minecraft:cocoa_beans`、`#c:crops/cocoa_bean`、`#c:crops/cocoabeans` | 可可壳色（待定），巧克力 | 多酚、可可脂 |
| 蜂蜜 | `materials/honey` | `minecraft:honey_bottle` | `minecraft:honey_bottle` | 蜂蜜酒、成品 | 果糖、葡萄糖 |
| 蜂蜜 | `honey`（流体） | `#forge:honey` | `#c:honey` | 蜂蜜酒、成品 | 果糖、葡萄糖 |
| 蜜脾 | `materials/honeycomb` | `minecraft:honeycomb` | `minecraft:honeycomb` | 138（蜂蜡） | 蜡酯 |
| 鸡蛋 | `materials/egg` | `minecraft:egg`、`#forge:eggs` | `minecraft:egg`、`#c:eggs` | 69（蛋黄卵磷脂），成品 | 蛋白质、磷脂 |
| 蔬菜（腌菜用） | `materials/vegetables` | `minecraft:carrot`、`minecraft:beetroot`、`minecraft:potato`、`#forge:vegetables` | `minecraft:carrot`、`minecraft:beetroot`、`minecraft:potato`、`#c:foods/vegetable` | 2（酸浆，I12 补，2026-11-10） | 糖、有机酸 |
| 食用油（浸提用） | `edible_oils`（流体） | `#foodindustrial:vegetable_oil`、`foodindustrial:crude_soybean_oil`、`foodindustrial:fish_liver_oil` | `#foodindustrial:vegetable_oil`、`foodindustrial:crude_soybean_oil`、`foodindustrial:fish_liver_oil` | 60（辣椒油，I12 补，2026-11-10） | 甘油三酯 |
| 糖蜜（通用） | `molasses`（流体） | `foodindustrial:cane_molasses`、`foodindustrial:beet_molasses` | `foodindustrial:cane_molasses`、`foodindustrial:beet_molasses` | 甘蔗、甜菜糖蜜都可用的配方（发酵碳源，如 04 柠檬酸 C 路线）；限定甘蔗或甜菜来源的配方直接引用物料（2026-11-12，11 问题 42） | 蔗糖、转化糖 |
| 牛奶 | `milk`（流体） | `#forge:milk` | `#c:milk` | 2（酸奶式乳酸发酵），成品 | 乳糖、酪蛋白 |
| 牛奶 | `materials/milk` | `minecraft:milk_bucket` | `minecraft:milk_bucket` | 2（酸奶式乳酸发酵），成品 | 乳糖、酪蛋白 |
| 骨头 | `materials/bone` | `minecraft:bone`、`#forge:bones` | `minecraft:bone`、`#c:bones` | 8, 10, 17 | 羟基磷灰石、胶原 |
| 骨粉 | `materials/bone_meal` | `minecraft:bone_meal` | `minecraft:bone_meal` | 10 | 羟基磷灰石 |
| 山羊角 | `materials/horn` | `minecraft:goat_horn` | `minecraft:goat_horn` | 8（鹿角盐） | 角蛋白 |
| 皮革 / 兔子皮 | `materials/hide` | `minecraft:leather`、`minecraft:rabbit_hide`、`#forge:leather` | `minecraft:leather`、`minecraft:rabbit_hide`、`#c:leathers` | 17 | 胶原 |
| 生猪肉 | `materials/raw_pork` | `minecraft:porkchop`、`#forge:raw_pork` | `minecraft:porkchop`、`#c:foods/raw_pork` | 27（熬动物脂），24（腌肉） | 甘油三酯、蛋白质 |
| 生牛肉 | `materials/raw_beef` | `minecraft:beef`、`#forge:raw_beef` | `minecraft:beef`、`#c:foods/raw_beef` | 27（熬动物脂），24（腌肉） | 甘油三酯、蛋白质 |
| 生羊肉 | `materials/raw_mutton` | `minecraft:mutton`、`#forge:raw_mutton` | `minecraft:mutton`、`#c:foods/raw_mutton` | 27（熬动物脂），24（腌肉） | 甘油三酯、蛋白质 |
| 生鸡肉 | `materials/raw_chicken` | `minecraft:chicken`、`#forge:raw_chicken` | `minecraft:chicken`、`#c:foods/raw_chicken` | 27（熬动物脂），24（腌肉） | 甘油三酯、蛋白质 |
| 生兔肉 | `materials/raw_rabbit` | `minecraft:rabbit` | `minecraft:rabbit` | 27（熬动物脂），24（腌肉） | 甘油三酯、蛋白质 |
| 鱼 | `materials/raw_fish` | `#minecraft:fishes`、`#forge:raw_fishes` | `#minecraft:fishes`、`#c:foods/raw_fish` | 107（鱼肝油），成品 | 甘油三酯、胶原 |
| 原木 | `materials/logs` | `#minecraft:logs` | `#minecraft:logs` | 1（干馏），7（草木灰），30（木质素），123（麦芽酚） | 纤维素、木质素 |
| 树叶 | `materials/leaves` | `#minecraft:leaves` | `#minecraft:leaves` | 22 | 叶绿素 |
| 草 / 蕨 | `materials/grass` | `minecraft:grass`、`minecraft:fern`、`minecraft:tall_grass` | `minecraft:short_grass`、`minecraft:fern`、`minecraft:tall_grass` | 22 | 叶绿素 |
| 枯萎的灌木 | `materials/saltbush` | `minecraft:dead_bush` | `minecraft:dead_bush` | 7（营火、熔炉烧成草木灰，代表蓬灰，2026-11-10） | 无机盐 |
| 方解石 | `materials/limestone` | `minecraft:calcite`、`minecraft:dripstone_block` | `minecraft:calcite`、`minecraft:dripstone_block` | 12，氧化钙（生石灰），CO2 | CaCO3 |
| 沙子 | `materials/sand` | `minecraft:sand`、`minecraft:red_sand`、`#forge:sand` | `minecraft:sand`、`minecraft:red_sand`、`#c:sands` | 10（热法磷硅石） | SiO2 |
| 黏土 | `materials/clay` | `minecraft:clay_ball` | `minecraft:clay_ball` | 9（黏土酸溶），陶器 | 高岭石 |
| 煤炭 / 木炭 | `materials/coal` | `#minecraft:coals` | `#minecraft:coals` | 燃料、5、10、活性炭 | C |
| 铜 | `materials/copper` | `minecraft:copper_ingot`、`#forge:ingots/copper` | `minecraft:copper_ingot`、`#c:ingots/copper` | 22（硫酸铜） | Cu |
| 铁 | `materials/iron` | `minecraft:iron_ingot`、`#forge:ingots/iron` | `minecraft:iron_ingot`、`#c:ingots/iron` | 合成氨催化剂 | Fe |
| 金 | `materials/gold` | `minecraft:gold_ingot`、`#forge:ingots/gold` | `minecraft:gold_ingot`、`#c:ingots/gold` | 13（金催化） | Au |
| 冰 | `materials/ice` | `minecraft:ice`、`minecraft:packed_ice`、`minecraft:blue_ice` | `minecraft:ice`、`minecraft:packed_ice`、`minecraft:blue_ice` | T1 冷冻结晶（29）、冷却 | H2O |
| 雪 | `materials/snow` | `minecraft:snowball`、`minecraft:snow_block` | `minecraft:snowball`、`minecraft:snow_block` | T1 冷冻结晶（29）、冷却 | H2O |
| 水 | `water`（流体） | `#minecraft:water` | `#minecraft:water` | 全部 | H2O |
| 熔岩 | `heat_sources`（流体） | `#minecraft:lava` | `#minecraft:lava` | T1 热源 | — |
| 羊毛 | `materials/wool` | `#minecraft:wool` | `#minecraft:wool` | 108（洗毛得羊毛脂 → 胆固醇） | 角蛋白、羊毛脂 |
| 桦木原木 | `materials/hemicellulose` | `minecraft:birch_log`、`minecraft:stripped_birch_log` | `minecraft:birch_log`、`minecraft:stripped_birch_log` | 37（木糖），124（糠醛） | 半纤维素（木聚糖） |
| 羽毛 | `materials/feather` | `minecraft:feather`、`#forge:feathers` | `minecraft:feather`、`#c:feathers` | 181 | 角蛋白 |
| 石英 | `materials/quartz` | `minecraft:quartz`、`#forge:gems/quartz` | `minecraft:quartz`、`#c:gems/quartz` | 163（沙子只作低纯度替代） | SiO2 |
| 竹子 | `materials/bamboo` | `minecraft:bamboo` | `minecraft:bamboo` | 162、229、327 | 纤维素、半纤维素、木质素 |
| 越橘（甜浆果代用） | `materials/lingonberry` | `minecraft:sweet_berries` | `minecraft:sweet_berries` | 208（游戏简化，见 15；I30 实装时补入标签） | 花青素、有机酸 |
| 玻璃瓶 | `materials/glass_bottle` | `minecraft:glass_bottle` | `minecraft:glass_bottle` | 成品葡萄汁、柑橘汁、花生油装瓶（2026-11-12，I30 实装补） | 玻璃 |

> 生肉按 2026-11-04 的回答补全为猪、牛、羊、鸡、兔；原版兔肉没有通用标签。草在 1.20.3 起改名为 `minecraft:short_grass`。原表“方解石”的候选 `#forge:stone/limestone` 未核实到，未写入。

## B 他模组原料

### 已依赖模组

| 原料 | 配方引用标签 | 默认内容（1.20.1） | 默认内容（1.21 起） | 用于 |
| --- | --- | --- | --- | --- |
| 大米 | `materials/rice` | `farmersdelight:rice`、`foodindustrial:rice`、`#forge:crops/rice`、`#forge:grain/rice` | `farmersdelight:rice`、`foodindustrial:rice`、`#c:crops/rice`、`#c:grain/rice` | 21（红曲米），淀粉 |
| 番茄 | `materials/tomato` | `#forge:crops/tomato`、`#forge:vegetables/tomato` | `#c:crops/tomato`、`#c:vegetables/tomato` | 59（番茄红素），成品 |
| 洋葱 | `materials/onion` | `#forge:crops/onion` | `#c:crops/onion` | 成品（12） |
| 卷心菜 | `materials/cabbage` | `#forge:crops/cabbage` | `#c:crops/cabbage` | 成品（12） |
| 稻草 | `materials/straw` | `farmersdelight:straw` | `farmersdelight:straw` | 淋灰桶滤层、燃料 |
| 硫（硫磺） | `chemicals/sulfur` | 本模组硫（硫磺）、`#forge:dusts/sulfur`、`#forge:gems/sulfur` | 本模组硫（硫磺）、`#c:dusts/sulfur`、`#c:gems/sulfur` | 25，硫酸 |
| 硝酸钾（硝石） | `chemicals/potassium_nitrate` | 本模组硝酸钾（硝石）、`#forge:dusts/niter`、`#forge:gems/niter`、`#forge:dusts/saltpeter` | 本模组硝酸钾（硝石）、`#c:dusts/niter`、`#c:gems/niter`、`#c:dusts/saltpeter` | 24 |
| 磷灰石 | `materials/apatite` | `#forge:gems/apatite`、`#forge:dusts/apatite` | `#c:gems/apatite`、`#c:dusts/apatite` | 10 |
| 镍 | `materials/nickel` | `#forge:ingots/nickel`、`#forge:dusts/nickel` | `#c:ingots/nickel`、`#c:dusts/nickel` | 26（镍催化剂） |
| 铅 | `materials/lead` | `#forge:ingots/lead` | `#c:ingots/lead` | 铅室法硫酸、电解阴极 |
| 焦炭 | `materials/coke` | `#forge:coal_coke`、`#forge:gems/coke` | `#c:coal_coke`、`#c:gems/coke` | 5、10 还原剂 |
| 原油 | `crude_oil`（流体） | 本模组原油、`#forge:crude_oil` | 本模组原油、`#c:crude_oil` | 23（甲苯） |
| 木焦油 | `wood_tar`（流体） | 本模组干馏产物、`#forge:creosote` | 本模组干馏产物、`#c:creosote` | 30（愈创木酚） |
| 植物油 | `vegetable_oil`（流体） | `#forge:plantoil`、`pneumaticcraft:vegetable_oil` | `#c:plantoil`、`pneumaticcraft:vegetable_oil` | 27 |
| 树皮 | `materials/tannin_source` | `farmersdelight:tree_bark`、本模组树皮（后备）、`#minecraft:oak_logs` | `farmersdelight:tree_bark`、`#minecraft:oak_logs` | 93（单宁 → 没食子酸） |
| 乳胶 | `latex`（流体） | 本模组乳胶、`thermal:latex`、`#forge:latex` | 本模组乳胶、`#c:latex` | 136 |
| 松脂 | `resin`（流体） | 本模组松脂、`thermal:resin` | 本模组松脂 | 137 |
| 动物血 | `animal_blood`（流体） | 本模组动物血、`#forge:blood` | 本模组动物血、`#c:blood` | 485、486、493（I31 补，2026-11-11；他模组血液标签未核对） |
| 锡 | `materials/tin` | `#forge:ingots/tin` | `#c:ingots/tin` | 87（有机锡催化剂） |
| 银 | `materials/silver` | `#forge:ingots/silver` | `#c:ingots/silver` | 银催化剂：72、120（环氧乙烷） |

> 金属镍用 `materials/nickel`，`catalysts/nickel` 只放镍催化剂（2026-11-04 定）。焦炭：原表候选 `#forge:gems/coal_coke` 不存在，GTCEu 的焦炭是 `gems/coke`。`#forge:straw`、`#forge:vegetable_oil` 未核实到，未写入。乳胶另导入热力的 `#forge:latex`。

### 计划联动的作物模组（潘马斯等，只到标签层级）

| 原料 | 配方引用标签 | 默认内容（1.20.1） | 默认内容（1.21 起） | 用于 |
| --- | --- | --- | --- | --- |
| 柠檬 | `materials/lemon` | `#forge:fruits/lemon`、`#forge:crops/lemon` | `#c:fruits/lemon`、`#c:crops/lemon` | 3 |
| 柑橘 | `materials/citrus` | `#forge:fruits/lemon`、`#forge:fruits/orange`、`#forge:fruits/lime`、`#forge:fruits/grapefruit` | `#c:fruits/lemon`、`#c:fruits/orange`、`#c:fruits/lime`、`#c:fruits/grapefruit` | 3、16（柑橘皮果胶） |
| 葡萄 | `materials/grape` | `#forge:fruits/grape`、`#forge:crops/grape` | `#c:fruits/grape`、`#c:crops/grape` | 葡萄酒 → 54、55，果酒 |
| 大豆 | `materials/soybean` | `#forge:crops/soybean`、`#forge:seeds/soybean` | `#c:crops/soybean`、`#c:seeds/soybean` | 豆腐（11、12、13 的下游），大豆毛油 → 65、69 |
| 玉米 | `materials/corn` | `#forge:crops/corn` | `#c:crops/corn` | 淀粉、玉米油 |
| 辣椒 | `materials/chili` | `foodindustrial:chili_pepper`、`#forge:crops/chilipepper` | `foodindustrial:chili_pepper`、`#c:crops/chilipepper` | 60 |
| 姜黄 | `materials/turmeric` | `foodindustrial:turmeric_rhizome` | `foodindustrial:turmeric_rhizome` | 61 |
| 茶叶 | `materials/tea` | `#forge:crops/tealeaf` | `#c:crops/tealeaf` | 66 |
| 薄荷 | `materials/mint` | 本模组薄荷（I07） | 本模组薄荷（I07） | 29 |
| 香荚兰 | `materials/vanilla` | 本模组香荚兰（I07）、`#forge:crops/vanillabean` | 本模组香荚兰（I07）、`#c:crops/vanillabean` | 30 |
| 芝麻 | `materials/sesame` | `#forge:crops/sesameseeds` | `#c:crops/sesameseeds` | 油脂 |
| 花生 | `materials/peanut` | `#forge:crops/peanut` | `#c:crops/peanut` | 油脂 |
| 木瓜 | `materials/papaya` | `#forge:fruits/papaya` | `#c:fruits/papaya` | 101（植物蛋白酶） |
| 菠萝 | `materials/pineapple` | `#forge:fruits/pineapple` | `#c:fruits/pineapple` | 101（植物蛋白酶） |
| 栀子 | `materials/gardenia` | 本模组栀子（I07） | 本模组栀子（I07） | 62 |
| 迷迭香 | `materials/rosemary` | 本模组迷迭香（I07） | 本模组迷迭香（I07） | 67 |
| 山苍子 | `materials/litsea` | — | — | 125, 126, 133 |
| 柠檬草 | `materials/lemongrass` | — | — | 125, 126, 133 |
| 桂皮 | `materials/cinnamon` | `#forge:crops/cinnamon` | `#c:crops/cinnamon` | 125, 126, 133 |
| 葛缕子 | `materials/caraway` | — | — | 125, 126, 133 |
| 蓖麻 | `materials/castor_bean` | — | — | 130（蓖麻油） |
| 橙 | `materials/orange` | `#forge:fruits/orange` | `#c:fruits/orange` | 133（橙皮油中的 d-柠檬烯） |

> 柑橘包含柠檬、橙、青柠、葡萄柚（2026-11-04 定）。潘马斯的实际标签名与原表候选不同的已改正：香荚兰 `crops/vanillabean`、芝麻 `crops/sesameseeds`、桂皮 `crops/cinnamon`、茶叶 `crops/tealeaf`、可可 `crops/cocoabeans`。以下候选在核对的模组中都不存在，未写入：`#forge:crops/turmeric`、`#forge:crops/tea`、`#forge:crops/mint`、`#forge:crops/vanilla`、`#forge:fruits/vanillabean`、`#forge:crops/sesame`、`#forge:crops/chili_pepper`、`#forge:crops/gardenia`、`#forge:crops/rosemary`、`#forge:crops/lemongrass`、`#forge:spices/cinnamon`、`#forge:crops/caraway`、`#forge:crops/castor`、`#forge:crops/sugarcane`（1.20.1）、`#forge:fruits/berries`、`#forge:crops/cocoa`。内容为空的标签（姜黄、山苍子、柠檬草、葛缕子、蓖麻）暂不生成，等有来源时再加；整合包可自行创建。

### 计划联动的科技模组（中间体互通）

| 中间体 | 配方引用标签 | 默认内容（1.20.1） | 默认内容（1.21 起） |
| --- | --- | --- | --- |
| 氯化钠（盐） | `chemicals/salt` | 本模组氯化钠（盐）、`#forge:dusts/salt` | 本模组氯化钠（盐）、`#c:dusts/salt` |
| 饱和食盐水 | `brine`（流体） | 本模组饱和食盐水、`#forge:brine` | 本模组饱和食盐水、`#c:brine` |
| 海水 | `seawater`（流体） | 本模组海水 | 本模组海水 |
| 硫酸 | `sulfuric_acid`（流体） | 本模组硫酸、`#forge:sulfuric_acid` | 本模组硫酸、`#c:sulfuric_acid` |
| 盐酸 | `hydrochloric_acid`（流体） | 本模组盐酸、`#forge:hydrochloric_acid` | 本模组盐酸、`#c:hydrochloric_acid` |
| 氯化氢 | `hydrogen_chloride`（流体） | 本模组氯化氢、`#forge:hydrogen_chloride` | 本模组氯化氢、`#c:hydrogen_chloride` |
| 氢气 | `hydrogen`（流体） | 本模组氢气、`#forge:hydrogen` | 本模组氢气、`#c:hydrogen` |
| 氧气 | `oxygen`（流体） | 本模组氧气、`#forge:oxygen` | 本模组氧气、`#c:oxygen` |
| 氯气 | `chlorine`（流体） | 本模组氯气、`#forge:chlorine` | 本模组氯气、`#c:chlorine` |
| 乙醇 | `ethanol`（流体） | 本模组乙醇（酒精）、`#forge:ethanol` | 本模组乙醇（酒精）、`#c:ethanol` |
| 锌 | `materials/zinc` | `#forge:ingots/zinc` | `#c:ingots/zinc` |
| 钨 | `materials/tungsten` | `#forge:dusts/tungsten`、`#forge:ingots/tungsten` | `#c:dusts/tungsten`、`#c:ingots/tungsten` |
| 钴 | `materials/cobalt` | `#forge:ingots/cobalt`、`#forge:dusts/cobalt` | `#c:ingots/cobalt`、`#c:dusts/cobalt` |
| 铂 | `materials/platinum` | `#forge:ingots/platinum`、`#forge:dusts/platinum` | `#c:ingots/platinum`、`#c:dusts/platinum` |
| 铑 | `materials/rhodium` | `#forge:ingots/rhodium`、`#forge:dusts/rhodium` | `#c:ingots/rhodium`、`#c:dusts/rhodium` |
| 钯 | `materials/palladium` | `#forge:ingots/palladium`、`#forge:dusts/palladium` | `#c:ingots/palladium`、`#c:dusts/palladium` |
| 钒 | `materials/vanadium` | `#forge:ingots/vanadium`、`#forge:dusts/vanadium` | `#c:ingots/vanadium`、`#c:dusts/vanadium` |
| 钼 | `materials/molybdenum` | —（候选 GTCEu `#forge:dusts/molybdenum`、`#forge:ingots/molybdenum`，未核对） | —（未核对） |
| 铋 | `materials/bismuth` | —（候选 GTCEu `#forge:dusts/bismuth`、`#forge:ingots/bismuth`，未核对） | —（未核对） |
| 粗硒 | `materials/selenium` | 本模组粗硒、`#forge:dusts/selenium`（候选，未核对） | 本模组粗硒、`#c:dusts/selenium`（未核对） |
| 铬 | `materials/chromium` | 本模组铬、`#forge:ingots/chrome`、`#forge:dusts/chrome`（GTCEu 命名，未核对） | 本模组铬、`#c:ingots/chrome`、`#c:dusts/chrome`（未核对） |

> 钴、铂、铑、钯、钒（I18 补，2026-11-11）：催化剂原料，由其他模组提供（11 待定问题 4），只导入他模组标签，未装时标签为空；候选标签未在各模组 jar 中核对。
>
> 粗硒、铬、动物血（I31 补，2026-11-11）：第 C 节写的他模组标签写入本表，由 I06 数据生成导入；本模组物品照常在标签内（粗硒、铬暂无来源）。
>
> 通用机械的气体属于 Mekanism Chemical，不进 Forge 流体标签；只有它额外注册的流体形态才能被匹配。

## C 本模组新增原料

| 原料 | 配方引用标签 | 类型 | 获取 | 用于 | 主要组成 | ID | 英文名（待审） | 分类 | 颜色 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 海水 | `seawater`（流体） | 流体 | 在海洋、海滩群系用桶取水 | 11，氯化钠 | NaCl、MgCl2、MgSO4、KCl | seawater（见 09） | — | 见 09 | — |
| 苦卤 | `bittern`（流体） | 流体 | 晒盐池 / 蒸发结晶器副产 | 11, 7；481（P103 提溴） | MgCl2、MgSO4、KCl、少量 NaBr | bittern（见 09） | — | 见 09 | — |
| 光卤石 | `chemicals/carnallite` | 物品 | 苦卤分步结晶 | 7, 11 | KCl·MgCl2·6H2O | carnallite（见 09） | — | 见 09 | — |
| 石花菜 | `materials/red_algae` | 海洋植物 | 温暖海洋生成，可养殖 | 14 | 琼脂糖 | gelidium | Gelidium | 原料 | — |
| 薄荷 | `materials/mint` | 作物 | 草原、平原野生 | 29 | 薄荷醇、薄荷酮 | mint | Mint | 原料 | — |
| 香荚兰豆 | `materials/vanilla` | 藤本作物 | 丛林野生，长在丛林原木侧面（同可可果） | 30 | 香兰素葡萄糖苷 | vanilla_pod | Vanilla Pod | 原料 | — |
| 甜菊 | `materials/stevia` | 作物 | 平原、热带草原野生 | 36 | 甜菊苷、莱鲍迪苷 A | stevia | Stevia | 原料 | — |
| 石膏矿石 | `materials/gypsum` | 矿石 / 方块 | 沙漠、恶地地下；导入 `#forge:ores/gypsum`（石膏粉 `#forge:dusts/gypsum` 进 `chemicals/gypsum`） | 12 | CaSO4·2H2O | gypsum_ore | Gypsum Ore | 原料 | — |
| 明矾石矿石 | `materials/alunite` | 矿石 | 山地、火山附近 | 9 | KAl3(SO4)2(OH)6 | alunite_ore | Alunite Ore | 原料 | — |
| 天然碱矿 | `materials/trona` | 地表方块（碱壳） | 沙漠、恶地的干湖床 | 5 | Na3H(CO3)2·2H2O | trona_ore | Trona Ore | 原料 | — |
| 岩盐 | `materials/rock_salt` | 矿石 | 地下；导入 `#forge:ores/salt` | 5，氯化钠 | NaCl | rock_salt | Rock Salt | 原料 | — |
| 菱镁矿石（可选） | `materials/magnesite` | 矿石 | 地下；导入 `#forge:ores/magnesite` | 11 | MgCO3 | magnesite_ore | Magnesite Ore | 原料 | — |
| 钠硝石矿 | `materials/nitratine` | 地表方块（硝壳） | 沙漠、恶地地表 | 24（硝酸钠） | NaNO3 | nitratine | Nitratine Ore | 原料 | — |
| 硝土 | `materials/nitre_soil` | 方块 | 07 中 H11 世界交互 | 24 | Ca(NO3)2、KNO3 混合 | nitre_soil | Nitre Soil | 原料 | — |
| 磷灰石（后备） | `materials/apatite` | 矿石 | 未安装热力时生成 | 10 | Ca5(PO4)3F | apatite_ore | Apatite Ore | 原料 | — |
| 麒麟菜 | `materials/red_algae_carrageenan` | 海洋植物 | 温暖海洋生成，可养殖 | 42、394（基因供体） | κ-卡拉胶 | eucheuma | Eucheuma | 原料 | — |
| 栀子果 | `materials/gardenia` | 灌木 | 丛林、竹林野生，成熟后右键采果（同甜浆果丛） | 62 | 栀子苷、藏花素 | gardenia_fruit | Gardenia Fruit | 原料 | — |
| 迷迭香 | `materials/rosemary` | 作物 | 草甸、风袭丘陵野生 | 67 | 鼠尾草酸、迷迭香酸 | rosemary | Rosemary | 原料 | — |
| 蓼蓝 | `materials/indigo_plant` | 作物 | 河边、沼泽野生 | 84 | 靛苷 | indigo_plant | Indigo Plant | 原料 | — |
| 橡胶木原木 | `materials/rubber_log` | 树木 | 丛林生成；导入热力的 `#forge:rubberwood_logs`、GTCEu 的 `#gtceu:rubber_logs`（原写 `#forge:logs/rubber`，核对不存在） | 136 | 顺式聚异戊二烯 | rubber_log | Rubber Log | 原料 | — |
| 硫矿石（后备） | 掉落硫，进 `chemicals/sulfur` | 矿石 | 未装热力（Thermal Foundation）时生成 | 25 | S | sulfur_ore | Sulfur Ore | 原料 | — |
| 稻谷 | `materials/paddy` | 作物 | 沼泽、河边野生（I07；不论是否装农夫乐事都生成，2026-11-12 与大米分开，原为“稻谷（后备）”、与大米同一 ID，11 问题 43） | 大米与米糠（碾米，同一配方）、21，淀粉 | 淀粉、米糠 | paddy | Paddy | 原料 | — |
| 树皮 | `materials/tannin_source` | 物品 | 后备来源：未装农夫乐事时用斧给原木去皮掉落 | 93 | 单宁 | tree_bark | Tree Bark | 原料 | — |
| 松脂 | `resin`（流体） | 流体 | 松木原木 H15 割取（07）；导入 `thermal:resin` | 137、N298 | 萜类树脂（松香约 70%、松节油 8.7%–17.4%，得率不是纯度） | resin | Resin | 原料 | #C8902A |
| 乳胶 | `latex`（流体） | 流体 | 橡胶树 H15 割取（07）；导入 `thermal:latex` | 136 | 顺式聚异戊二烯 | latex | Latex | 原料 | #F0F0E8 |
| 葡萄酒 | `wine`（流体） | 流体 | 葡萄发酵；导入 `#forge:wine` | 54, 55 | 乙醇、酒石酸氢钾 | wine | Wine | 原料 | #5A1028 |
| 酒石 | `chemicals/potassium_bitartrate` | 物品 | H14 冷置析酒石、葡萄酒桶底 | 54, 55 | C4H5KO6 | potassium_bitartrate（见 09） | — | 见 09 | — |
| 大豆毛油 | `crude_soybean_oil`（流体） | 流体 | 大豆压榨或浸出 | 65, 69 | 甘油三酯、磷脂、生育酚 | crude_soybean_oil | Crude Soybean Oil | 原料 | #D8A030 |
| 番茄皮渣 | `materials/tomato_pomace` | 物品 | 番茄榨汁副产 | 59 | 番茄红素 | tomato_pomace | Tomato Pomace | 原料 | — |
| 啤酒酵母 | `materials/spent_yeast` | 物品 | 啤酒酿造副产 | 73 | 核糖核酸 | spent_yeast | Spent Brewer's Yeast | 原料 | — |
| 乳清 | `whey`（流体） | 流体 | 奶酪制作副产 | 32, 34 | 乳糖、乳清蛋白 | whey | Whey | 原料 | #E8E8B0 |
| 鱼肝 | `materials/fish_liver` | 物品 | 处理鳕鱼的副产（工作台：鳕鱼 → 鱼肝，I12） | 107 | 鱼肝油（维生素 A、D） | fish_liver | Fish Liver | 原料 | — |
| 姜黄根茎 | `materials/turmeric` | 作物 | 丛林、热带草原野生（I12，2026-11-10） | 61 | 姜黄素类、姜黄油 | turmeric_rhizome | Turmeric Rhizome | 原料 | — |
| 辣椒 | `materials/chili` | 作物 | 热带草原、平原野生（I12，2026-11-10）；导入 `#forge:crops/chilipepper` | 60 | 辣椒红素、辣椒素 | chili_pepper | Chili Pepper | 原料 | — |
| 果酒 | `fruit_wine`（流体） | 流体 | 苹果或浆果加水，陶坛封口发酵（07） | 1, 69 | 乙醇、有机酸 | fruit_wine | Fruit Wine | 原料 | #C89838 |
| 石花菜煮液 | `agar_broth`（流体） | 流体 | 石花菜加水在锅中熬煮（07 第 14 号） | 14 | 琼脂、藻渣 | agar_broth | Agar Broth | 原料 | #D8CC98 |
| 甜菜原汁 | `beetroot_juice`（流体） | 流体 | 甜菜根压榨（M02，19 A） | 19 | 甜菜苷、糖、水 | beetroot_juice | Raw Beet Juice | 原料 | #8A1838 |
| 葡萄原汁 | `raw_grape_juice`（流体） | 流体 | 葡萄 M02 压榨，与葡萄皮渣同一配方；装瓶为成品葡萄汁（2026-11-12，用户定：压榨的果汁、花生油出流体） | 226、成品葡萄汁 | 糖、有机酸、花青素 | raw_grape_juice | Raw Grape Juice | 原料 | #6A1E3A |
| 柑橘原汁 | `raw_citrus_juice`（流体） | 流体 | 柑橘 M02 压榨，与柑橘皮同一配方；装瓶为成品柑橘汁（同上） | 231、成品柑橘汁 | 糖、柠檬酸 | raw_citrus_juice | Raw Citrus Juice | 原料 | #F2B33D |
| 花生原油 | `crude_peanut_oil`（流体） | 流体 | 花生 M02 压榨，与花生红衣同一配方；装瓶为成品花生油（同上） | 225、成品花生油 | 甘油三酯 | crude_peanut_oil | Crude Peanut Oil | 原料 | #E8C25A |
| 精炼大豆油 | `refined_soybean_oil`（流体） | 流体 | 大豆毛油水化脱胶、碱炼、脱色、汽提脱臭（P63，65、69） | 65, 69 | 甘油三酯 | refined_soybean_oil | Refined Soybean Oil | 原料 | #F0D878 |
| 栀子煮液 | `gardenia_broth`（流体） | 流体 | 栀子碎加水在陶锅水煮（07 第 62 号） | 62 | 西红花苷、栀子苷 | gardenia_broth | Gardenia Broth | 原料 | #E0A020 |
| 蛋黄浸液 | `yolk_extract`（流体） | 流体 | 熟蛋黄用果酒在陶坛封口浸提（07 第 69 号） | 69 | 磷脂、乙醇 | yolk_extract | Egg Yolk Extract | 原料 | #E8C850 |
| 蓼蓝发酵液 | `indigo_ferment`（流体） | 流体 | 蓼蓝加水在陶坛封口发酵（07 第 84 号） | 84 | 吲哚酚、靛苷 | indigo_ferment | Indigo Ferment | 原料 | #3A6078 |
| 蛋黄 | `materials/egg_yolk` | 物品 | 鸡蛋用筛子分离，同时得蛋清（I12） | 69 | 磷脂、蛋白质 | egg_yolk | Egg Yolk | 原料 | — |
| 熟蛋黄 | `materials/cooked_egg_yolk` | 物品 | 蛋黄加水在锅中煮熟 | 69 | 磷脂、蛋白质 | cooked_egg_yolk | Cooked Egg Yolk | 原料 | — |
| 甜菊干叶 | `materials/dried_stevia` | 物品 | 甜菊在晾晒架晒干 | 36 | 甜菊苷 | dried_stevia | Dried Stevia Leaves | 原料 | — |
| 干辣椒 | `materials/dried_chili` | 物品 | 辣椒在晾晒架晒干 | 60 | 辣椒红素 | dried_chili | Dried Chili | 原料 | — |
| 辣椒粉 | `materials/chili_powder` | 物品 | 干辣椒在研钵研磨 | 60 | 辣椒红素 | chili_powder | Chili Powder | 原料 | — |
| 煮姜黄 | `materials/boiled_turmeric` | 物品 | 姜黄根茎加水在锅中水煮 | 61 | 姜黄素类 | boiled_turmeric | Boiled Turmeric | 原料 | — |
| 姜黄干片 | `materials/dried_turmeric` | 物品 | 煮姜黄在晾晒架晒干 | 61 | 姜黄素类 | dried_turmeric | Dried Turmeric | 原料 | — |
| 霉变姜黄 | `materials/moldy_turmeric` | 物品 | 晾晒中的煮姜黄淋雨（失败产物，只能丢弃或堆肥） | — | — | moldy_turmeric | Moldy Turmeric | 原料 | — |
| 栀子碎 | `materials/crushed_gardenia` | 物品 | 栀子果在研钵捣碎 | 62 | 西红花苷 | crushed_gardenia | Crushed Gardenia | 原料 | — |
| 浸水小麦 | `materials/soaked_wheat` | 物品 | 小麦加水在陶坛浸泡 | 96 | 淀粉 | soaked_wheat | Soaked Wheat | 原料 | — |
| 发芽小麦 | `materials/sprouted_wheat` | 物品 | 浸水小麦在曲盘发芽 | 96 | 淀粉、淀粉酶 | sprouted_wheat | Sprouted Wheat | 原料 | — |
| 徒长麦芽 | `materials/overgrown_malt` | 物品 | 发芽小麦在曲盘上超过 4 天（失败产物，不能糖化） | — | — | overgrown_malt | Overgrown Malt | 原料 | — |
| 大米 | `materials/rice` | 物品 | 稻谷（`materials/paddy`）或红米 M01 / M11 碾米的主产物，与米糠（红米糠）同一配方（联产）；农夫乐事的大米同标签可用（第 B 节）。I07 原把稻谷登记为 `rice`，2026-11-12 与稻谷分开后本模组另设此物品（11 问题 43，实装补） | 21、H13（蒸米）、淀粉 | 淀粉 | rice | Rice | 原料 | — |
| 蒸米 | `materials/steamed_rice` | 物品 | 大米（`materials/rice`）加水在锅中蒸煮（2026-11-12 由稻谷改为大米，稻谷先碾米） | H13 | 淀粉 | steamed_rice | Steamed Rice | 原料 | — |
| 蒸麦 | `materials/steamed_wheat` | 物品 | 小麦加水在锅中蒸煮 | H13 | 淀粉 | steamed_wheat | Steamed Wheat | 原料 | — |
| 杂菌 | `materials/wild_mold` | 物品 | 蒸米、蒸麦在曲盘上不遮光（失败产物，含产毒的黄曲霉，只能丢弃或堆肥） | — | — | wild_mold | Wild Mold | 原料 | — |
| 焦糖渣 | `materials/burnt_caramel` | 物品 | 糖色在铁锅里继续加热（失败产物） | — | — | burnt_caramel | Burnt Caramel | 原料 | — |
| 死烧石膏 | `materials/dead_burnt_gypsum` | 物品 | 熟石膏在锅里继续干炒（失败产物，不能点豆腐） | — | 无水硫酸钙 | dead_burnt_gypsum | Dead-Burnt Gypsum | 原料 | — |
| 胶乳凝块 | `materials/rubber_coagulum` | 物品 | 乳胶加醋在陶坛凝固 | 136 | 顺式聚异戊二烯 | rubber_coagulum | Rubber Coagulum | 原料 | — |
| 湿胶片 | `materials/wet_rubber_sheet` | 物品 | 胶乳凝块用布袋压片 | 136 | 顺式聚异戊二烯、水 | wet_rubber_sheet | Wet Rubber Sheet | 原料 | — |
| 红花干花 | `materials/safflower` | 物品 | 暂无来源（待定，见 15） | 206 | 红花黄色素（羟基红花黄色素 A）、红花红色素 | safflower | Dried Safflower | 原料 | — |
| 紫胶原胶 | `materials/sticklac` | 物品 | 暂无来源（待定，见 15） | 207、271、273 | 紫胶树脂、紫胶色酸、蜡 | sticklac | Sticklac | 原料 | — |
| 越橘 | `materials/lingonberry` | 物品 | 标签默认含原版甜浆果（游戏简化） | 208 | 花青素、有机酸 | lingonberry | Lingonberry | 原料 | — |
| 红米 | `materials/red_rice` | 作物 | 暂无来源（待定，见 15） | 210 | 淀粉、种皮花青素 | red_rice | Red Rice | 原料 | — |
| 红米糠 | `materials/red_rice_bran` | 物品 | 红米经 M01 / M11 碾米，与大米同一配方（联产，2026-11-12） | 210 | 花青素、米糠油、蛋白质 | red_rice_bran | Red Rice Bran | 原料 | — |
| 剑叶金鸡菊花序 | `materials/coreopsis` | 物品 | 暂无来源（待定，见 15） | 211 | 查耳酮、橙酮类黄酮 | coreopsis | Coreopsis Flower | 原料 | — |
| 黑豆种皮 | `materials/black_soybean_hull` | 物品 | 暂无来源（待定，见 15） | 212 | 花青素（矢车菊素-3-葡萄糖苷） | black_soybean_hull | Black Soybean Hull | 原料 | — |
| 高粱壳 | `materials/sorghum_husk` | 物品 | 暂无来源（待定，见 15） | 213 | 3-脱氧花青素、黄酮 | sorghum_husk | Sorghum Husk | 原料 | — |
| 玉米蛋白粉 | `materials/corn_gluten_meal` | 物品 | 玉米（`materials/corn`）M01 湿磨副产 | 214 | 玉米醇溶蛋白、玉米黄质、叶黄素 | corn_gluten_meal | Corn Gluten Meal | 原料 | — |
| 红心萝卜 | `materials/red_radish` | 作物 | 暂无来源（待定，见 15）；作物模组标签未核对 | 215 | 花青素（天竺葵素酰化苷） | red_radish | Red Radish | 原料 | — |
| 可可豆壳 | `materials/cocoa_shell` | 物品 | 可可豆焙炒、M11 破碎风选的副产 | 216 | 多酚聚合色素、纤维 | cocoa_shell | Cocoa Shell | 原料 | — |
| 黑加仑 | `materials/blackcurrant` | 物品 | 暂无来源（待定，见 15） | 217 | 花青素（飞燕草素、矢车菊素芸香苷） | blackcurrant | Blackcurrant | 原料 | — |
| 沙棘果 | `materials/sea_buckthorn` | 物品 | 暂无来源（待定，见 15） | 219 | 类胡萝卜素、黄酮、油脂 | sea_buckthorn | Sea Buckthorn Berry | 原料 | — |
| 玫瑰茄花萼 | `materials/roselle` | 物品 | 暂无来源（待定，见 15） | 220 | 花青素（飞燕草素桑布双糖苷）、有机酸 | roselle | Roselle Calyx | 原料 | — |
| 橡子壳 | `materials/acorn_shell` | 物品 | 暂无来源（待定，见 15） | 221 | 单宁、多酚 | acorn_shell | Acorn Shell | 原料 | — |
| 桑椹 | `materials/mulberry` | 物品 | 暂无来源（待定，见 15） | 222 | 花青素（矢车菊素-3-葡萄糖苷） | mulberry | Mulberry | 原料 | — |
| 红苋菜 | `materials/red_amaranth` | 作物 | 暂无来源（待定，见 15） | 223 | 苋菜红素（甜菜色素） | red_amaranth | Red Amaranth | 原料 | — |
| 金樱子 | `materials/rosa_laevigata` | 物品 | 暂无来源（待定，见 15） | 224 | 多酚、单宁、黄酮 | rosa_laevigata | Cherokee Rose Hip | 原料 | — |
| 花生红衣 | `materials/peanut_skin` | 物品 | 花生（`materials/peanut`）榨油前脱红衣的副产（12 的榨油线补此工序） | 225 | 原花青素、单宁 | peanut_skin | Peanut Skin | 原料 | — |
| 葡萄皮渣 | `materials/grape_pomace` | 物品 | 葡萄（`materials/grape`）M02 压榨或葡萄酒压渣的副产 | 226 | 花青素、单宁、酒石 | grape_pomace | Grape Pomace | 原料 | — |
| 蓝锭果 | `materials/haskap` | 物品 | 暂无来源（待定，见 15） | 227 | 花青素（矢车菊素-3-葡萄糖苷） | haskap | Haskap Berry | 原料 | — |
| 螺旋藻粉 | `materials/spirulina` | 物品 | 暂无来源（待定，见 15） | 228 | 藻蓝蛋白、蛋白质、叶绿素 | spirulina | Spirulina Powder | 原料 | — |
| 紫草根 | `materials/gromwell_root` | 物品 | 暂无来源（待定，见 15） | 230 | 紫草素及其酯 | gromwell_root | Gromwell Root | 原料 | — |
| 柑橘皮 | `materials/citrus_peel` | 物品 | 柑橘（`materials/citrus`）M02 压榨的副产 | 231，16 | 类胡萝卜素、果胶、柠檬烯 | citrus_peel | Citrus Peel | 原料 | — |
| 胭脂树籽 | `materials/annatto_seed` | 物品 | 暂无来源（待定，见 15） | 232 | 红木素、降红木素 | annatto_seed | Annatto Seed | 原料 | — |
| 胭脂虫干 | `materials/dried_cochineal` | 物品 | 暂无来源（待定，见 15） | 233 | 胭脂红酸 | dried_cochineal | Dried Cochineal | 原料 | — |
| 万寿菊干花 | `materials/marigold` | 物品 | 暂无来源（待定，见 15） | 234 | 叶黄素酯 | marigold | Dried Marigold | 原料 | — |
| 杨梅 | `materials/bayberry` | 物品 | 暂无来源（待定，见 15） | 237 | 花青素（矢车菊素-3-葡萄糖苷）、有机酸 | bayberry | Chinese Bayberry | 原料 | — |
| 紫甘薯 | `materials/purple_sweet_potato` | 作物 | 暂无来源（待定，见 15） | 242 | 酰化花青素、淀粉 | purple_sweet_potato | Purple Sweet Potato | 原料 | — |
| 盐藻粉 | `materials/dunaliella` | 物品 | 暂无来源（待定，见 15） | 197 | β-胡萝卜素、甘油 | dunaliella | Dunaliella Powder | 原料 | — |
| 钛铁矿石 | `materials/ilmenite` | 矿石 | 暂无来源（待定，见 15）；GTCEu `#forge:ores/ilmenite`、`#forge:dusts/ilmenite` 未核对 | 198 | FeTiO3 | ilmenite | Ilmenite Ore | 原料 | — |
| 椰子干 | `materials/copra` | 物品 | 暂无来源（待定，见 15）；潘马斯椰子标签未核对 | 252、256、257、324、325、342 | 椰子油（月桂酸甘油三酯为主） | copra | Copra | 原料 | — |
| 油棕果 | `materials/oil_palm_fruit` | 物品 | 暂无来源（待定，见 15） | 248、258 | 棕榈油、类胡萝卜素 | oil_palm_fruit | Oil Palm Fruit | 原料 | — |
| 皂树皮 | `materials/quillaja_bark` | 物品 | 暂无来源（待定，见 15） | 269 | 皂树皂苷、多酚 | quillaja_bark | Quillaja Bark | 原料 | — |
| 达瓦树胶块 | `materials/ghatti_lump` | 物品 | 暂无来源（待定，见 15） | 270 | 酸性杂多糖（阿拉伯糖、半乳糖、葡萄糖醛酸） | ghatti_lump | Gum Ghatti Lump | 原料 | — |
| 巴西棕榈蜡粉 | `materials/carnauba_powder` | 物品 | 暂无来源（待定，见 15） | 273、275 | 蜡酯（长链脂肪酸与长链醇） | carnauba_powder | Carnauba Wax Powder | 原料 | — |
| 刺梧桐胶块 | `materials/karaya_lump` | 物品 | 暂无来源（待定，见 15） | 283 | 乙酰化酸性多糖（鼠李糖、半乳糖醛酸） | karaya_lump | Karaya Gum Lump | 原料 | — |
| 阿拉伯胶块 | `materials/acacia_lump` | 物品 | 暂无来源（待定，见 15） | 288 | 阿拉伯半乳聚糖–蛋白复合物 | acacia_lump | Gum Arabic Lump | 原料 | — |
| 罗望子种子 | `materials/tamarind_seed` | 物品 | 暂无来源（待定，见 15）；潘马斯罗望子标签未核对 | 290 | 罗望子多糖（木葡聚糖） | tamarind_seed | Tamarind Seed | 原料 | — |
| 亚麻籽 | `materials/flaxseed` | 物品 | 暂无来源（待定，见 15）；潘马斯亚麻标签未核对 | 297 | 种皮黏液（亚麻籽胶）、亚麻籽油 | flaxseed | Flaxseed | 原料 | — |
| 田菁籽 | `materials/sesbania_seed` | 物品 | 暂无来源（待定，见 15） | 298 | 半乳甘露聚糖 | sesbania_seed | Sesbania Seed | 原料 | — |
| 槐豆 | `materials/carob_seed` | 物品 | 暂无来源（待定，见 15） | 300、369 | 半乳甘露聚糖（甘露糖 : 半乳糖约 4 : 1） | carob_seed | Carob Seed | 原料 | — |
| 瓜尔豆 | `materials/guar_seed` | 物品 | 暂无来源（待定，见 15） | 302、369 | 半乳甘露聚糖（约 2 : 1） | guar_seed | Guar Seed | 原料 | — |
| 皂荚籽 | `materials/gleditsia_seed` | 物品 | 暂无来源（待定，见 15） | 305 | 半乳甘露聚糖 | gleditsia_seed | Honeylocust Seed | 原料 | — |
| 沙蒿籽 | `materials/artemisia_seed` | 物品 | 暂无来源（待定，见 15） | 311 | 沙蒿胶多糖、油脂 | artemisia_seed | Sand Sagebrush Seed | 原料 | — |
| 刺云实籽 | `materials/tara_seed` | 物品 | 暂无来源（待定，见 15） | 312 | 半乳甘露聚糖（约 3 : 1） | tara_seed | Tara Seed | 原料 | — |
| 决明子 | `materials/cassia_seed` | 物品 | 暂无来源（待定，见 15） | 316 | 半乳甘露聚糖、蒽醌 | cassia_seed | Cassia Seed | 原料 | — |
| 虾蟹壳 | `materials/crustacean_shell` | 物品 | 暂无来源（待定，见 15） | 296、303 | 甲壳素、碳酸钙、蛋白质 | crustacean_shell | Crustacean Shell | 原料 | — |
| 黑曲霉菌丝体 | `materials/fungal_mycelium` | 物品 | 柠檬酸（3）B、C 路线发酵液过滤副产 | 296、303 | 甲壳素–葡聚糖、蛋白质 | fungal_mycelium | Fungal Mycelium | 原料 | — |
| 豆渣 | `materials/okara` | 物品 | 大豆（`materials/soybean`）M01 湿磨、M12 过滤副产，与豆奶、豆腐共用 | 315 | 膳食纤维、大豆多糖、蛋白质 | okara | Okara | 原料 | — |
| 甘草根 | `materials/licorice_root` | 物品 | 暂无来源（待定，见 15） | 321、352、353、362 | 甘草酸、甘草黄酮 | licorice_root | Licorice Root | 原料 | — |
| 甘草渣 | `materials/licorice_residue` | 物品 | 甘草根热水浸提（352）后的渣，与甘草酸浸提液同一配方（联产，2026-11-12，11 问题 43） | 321 | 甘草黄酮、纤维 | licorice_residue | Licorice Residue | 原料 | — |
| 罗汉果干果 | `materials/monk_fruit` | 物品 | 暂无来源（待定，见 15） | 356 | 罗汉果苷 V | monk_fruit | Dried Monk Fruit | 原料 | — |
| 西非竹芋果 | `materials/katemfe_fruit` | 物品 | 暂无来源（待定，见 15） | 357 | 索马甜（甜味蛋白） | katemfe_fruit | Katemfe Fruit | 原料 | — |
| 米糠 | `materials/rice_bran` | 物品 | 稻谷（`materials/paddy`）M01 / M11 碾米，与大米同一配方（联产，2026-11-12） | 320、331 | 米糠油、阿魏酸酯、植酸 | rice_bran | Rice Bran | 原料 | — |
| 蛋清 | `materials/egg_white` | 物品 | 鸡蛋（`materials/egg`）在工作台或 M12 分离，蛋黄供 69 | 346 | 卵清蛋白、溶菌酶 | egg_white | Egg White | 原料 | — |
| 咖啡生豆 | `materials/coffee_bean` | 物品 | 暂无来源（待定，见 15）；潘马斯 `#forge:crops/coffeebean` 未核对 | 366 | 咖啡因、绿原酸 | coffee_bean | Green Coffee Bean | 原料 | — |
| 软锰矿石 | `materials/pyrolusite` | 矿石 | 暂无来源（待定，见 15）；GTCEu `#forge:ores/pyrolusite`、`#forge:dusts/pyrolusite` 未核对 | 92、364 | MnO2 | pyrolusite | Pyrolusite Ore | 原料 | — |
| 胰脏 | `materials/pancreas` | 物品 | 暂无来源（待定，见 15） | 397、399、418、419 | 胰蛋白酶原、胰脂肪酶、胰淀粉酶 | pancreas | Pancreas | 原料 | — |
| 胃黏膜 | `materials/gastric_mucosa` | 物品 | 暂无来源（待定，见 15） | 415 | 胃蛋白酶原 | gastric_mucosa | Gastric Mucosa | 原料 | — |
| 肝脏 | `materials/liver` | 物品 | 暂无来源（待定，见 15）（不含鱼肝） | 390 | 过氧化氢酶 | liver | Liver | 原料 | — |
| 无花果乳汁 | `ficus_latex`（流体） | 流体 | 暂无来源（待定，见 15）；作物模组提供无花果树时扩展 H15 割取 | 416 | 无花果蛋白酶 | ficus_latex | Fig Latex | 原料 | #F0EEE6 |
| 木瓜乳汁 | `papaya_latex`（流体） | 流体 | 未熟木瓜（`materials/papaya`）H15 割取（07 第 101 号；木瓜的割取暂不做） | 101 | 木瓜蛋白酶 | papaya_latex | Papaya Latex | 原料 | #F2F0E4 |
| 大麦 | `materials/barley` | 作物 | 暂无来源（待定，见 15）；潘马斯 `#forge:crops/barley` 未核对 | 402（大麦麦芽）、378（诱导底物） | 淀粉、β-葡聚糖 | barley | Barley | 原料 | — |
| 皱胃 | `materials/abomasum` | 物品 | 暂无来源（待定，见 15） | 105 A、工程菌（凝乳酶）供体 | 凝乳酶原、胃蛋白酶原 | abomasum | Abomasum | 原料 | — |
| 菊芋块茎 | `materials/jerusalem_artichoke` | 作物 | 暂无来源（待定，见 15） | 395 | 菊粉 | jerusalem_artichoke | Jerusalem Artichoke | 原料 | — |
| 龙脑樟枝叶 | `materials/borneol_camphor_leaf` | 物品 | 暂无来源（待定，见 17–19） | S0028 | — | borneol_camphor_leaf | Borneol Camphor Leaf | 原料 | — |
| 乌桕籽 | `materials/tallow_tree_seed` | 物品 | 暂无来源（待定，见 17–19） | S0482 | — | tallow_tree_seed | Tallow Tree Seed | 原料 | — |
| 蒜头果种子 | `materials/malania_seed` | 物品 | 暂无来源（待定，见 17–19） | S0637 | — | malania_seed | Malania Seed | 原料 | — |
| 香菇（干） | `materials/shiitake` | 物品 | 暂无来源（待定，见 17–19） | S0683 A | — | shiitake | Shiitake | 原料 | — |
| 芥菜籽 | `materials/mustard_seed` | 物品 | 暂无来源（待定，见 17–19） | S0762 A | — | mustard_seed | Mustard Seed | 原料 | — |
| 西瓜 | `materials/melon` | 物品 | 原版 `minecraft:melon_slice` | S0862 | — | melon | Melon | 原料 | — |
| 宽叶薄荷桉叶 | `materials/eucalyptus_dives` | 物品 | 暂无来源（待定，见 17–19） | S0955 | — | eucalyptus_dives | Eucalyptus Dives | 原料 | — |
| 山葵根茎 | `materials/wasabi` | 物品 | 暂无来源（待定，见 17–19） | S1081、S1087、S1088 | — | wasabi | Wasabi | 原料 | — |
| 香紫苏（复用 N 组标签） | `materials/clary_sage_herb` | 物品 | 暂无来源（同 N108） | S1049（经 `sclareol`）、N108 | — | clary_sage_herb | Clary Sage Herb | 原料 | — |
| 山楂核 | `materials/hawthorn_kernel` | 物品 | `materials/hawthorn_fruit`（N1 组新增）去肉取核（游戏简化：M11 粉碎时副产） | S1230、S1231 | — | hawthorn_kernel | Hawthorn Kernel | 原料 | — |
| 柏木 | `materials/cedarwood` | 物品 | 暂无来源（待定，见 17–19） | S1197（经 `cedrol`）、N321 | — | cedarwood | Cedarwood | 原料 | — |
| 高芥酸菜籽 | `materials/rapeseed` | 物品 | 暂无来源（待定，见 17–19） | S1281（经 `brassylic_acid`） | — | rapeseed | Rapeseed | 原料 | — |
| 丁香叶 | `materials/clove_leaf` | 物品 | 暂无来源（待定，见 17–19） | N001 | — | clove_leaf | Clove Leaf | 原料 | — |
| 丁香花蕾 | `materials/clove_bud` | 物品 | 暂无来源（待定，见 17–19） | N002、N003 | — | clove_bud | Clove Bud | 原料 | — |
| 罗勒 | `materials/basil` | 物品 | 暂无来源（待定，见 17–19） | N004 | — | basil | Basil | 原料 | — |
| 八角 | `materials/star_anise` | 物品 | 暂无来源（待定，见 17–19） | N005 | — | star_anise | Star Anise | 原料 | — |
| 九里香花 | `materials/jasmin_orange_flower` | 物品 | 暂无来源（待定，见 17–19） | N006 | — | jasmin_orange_flower | Jasmin Orange Flower | 原料 | — |
| 广藿香叶 | `materials/patchouli_leaf` | 物品 | 暂无来源（待定，见 17–19） | N007 | — | patchouli_leaf | Patchouli Leaf | 原料 | — |
| 小豆蔻 | `materials/cardamom` | 物品 | 暂无来源（待定，见 17–19） | N010、N011 | — | cardamom | Cardamom | 原料 | — |
| 甜茴香籽 | `materials/sweet_fennel_seed` | 物品 | 暂无来源（待定，见 17–19） | N129 | — | sweet_fennel_seed | Sweet Fennel Seed | 原料 | — |
| 山楂 | `materials/hawthorn_fruit` | 物品 | 暂无来源（待定，见 17–19） | N014 | — | hawthorn_fruit | Hawthorn Fruit | 原料 | — |
| 大蒜 | `materials/garlic` | 物品 | 暂无来源（待定，见 17–19） | N015、N016 | — | garlic | Garlic | 原料 | — |
| 葡萄酒酒脚 | `materials/wine_lees` | 物品 | 葡萄酒（`wine`）澄清的沉渣，与酒石同出（H14 冷置、M04 澄清配方补此副产） | N017 | — | wine_lees | Wine Lees | 原料 | — |
| 云木香根 | `materials/costus_root` | 物品 | 暂无来源（待定，见 17–19） | N019 | — | costus_root | Costus Root | 原料 | — |
| 乌梅 | `materials/smoked_plum` | 物品 | 暂无来源（12 酸梅汤同用） | N021 | — | smoked_plum | Smoked Plum | 原料 | — |
| 布枯叶 | `materials/buchu_leaf` | 物品 | 暂无来源（待定，见 17–19） | N022 | — | buchu_leaf | Buchu Leaf | 原料 | — |
| 可可碎粒 | `materials/cocoa_nibs` | 物品 | 可可豆（`materials/cocoa_beans`）焙炒（熔炉 / M17）、M11 破碎风选的主产物，可可豆壳为副产（同 216） | N023 | — | cocoa_nibs | Cocoa Nibs | 原料 | — |
| 甘松根 | `materials/nardostachys_root` | 物品 | 暂无来源（待定，见 17–19） | N025 | — | nardostachys_root | Nardostachys Root | 原料 | — |
| 冬青叶 | `materials/wintergreen_leaf` | 物品 | 暂无来源（待定，见 17–19） | N028 | — | wintergreen_leaf | Wintergreen Leaf | 原料 | — |
| 白兰花 | `materials/michelia_alba_flower` | 物品 | 暂无来源（待定，见 17–19） | N029、N032（经 N032 供 N031） | — | michelia_alba_flower | Michelia Alba Flower | 原料 | — |
| 白兰叶 | `materials/michelia_alba_leaf` | 物品 | 暂无来源（待定，见 17–19） | N030 | — | michelia_alba_leaf | Michelia Alba Leaf | 原料 | — |
| 白芷根 | `materials/angelica_dahurica_root` | 物品 | 暂无来源（待定，见 17–19） | N033 | — | angelica_dahurica_root | Angelica Dahurica Root | 原料 | — |
| 肉豆蔻 | `materials/nutmeg` | 物品 | 暂无来源（待定，见 17–19） | N037、N038 | — | nutmeg | Nutmeg | 原料 | — |
| 红茶 | `materials/black_tea` | 物品 | 茶叶（`materials/tea`）萎凋、揉捻后陶坛通气发酵（同 329 A 前段）、M10 烘干（待加配方） | N041 | — | black_tea | Black Tea | 原料 | — |
| 印蒿 | `materials/davana_herb` | 物品 | 暂无来源（待定，见 17–19） | N042 | — | davana_herb | Davana Herb | 原料 | — |
| 吐鲁香脂 | `materials/tolu_balsam` | 物品 | 暂无来源（待定，见 17–19） | N043、N044 | — | tolu_balsam | Tolu Balsam | 原料 | — |
| 豆豉 | `foods/douchi` | 物品 | 12 成品豆豉（`douchi`）；`foods/douchi` 标签待生成 | N045 | — | douchi | Douchi | 原料 | — |
| 杜松子 | `materials/juniper_berry` | 物品 | 暂无来源（待定，见 17–19） | N046 | — | juniper_berry | Juniper Berry | 原料 | — |
| 芫荽籽 | `materials/coriander_seed` | 物品 | 暂无来源（待定，见 17–19） | N047 | — | coriander_seed | Coriander Seed | 原料 | — |
| 芹菜花 | `materials/celery_flower` | 物品 | 暂无来源（待定，见 17–19） | N048 | — | celery_flower | Celery Flower | 原料 | — |
| 芹菜籽 | `materials/celery_seed` | 物品 | 暂无来源（待定，见 17–19） | N049 | — | celery_seed | Celery Seed | 原料 | — |
| 牡荆叶 | `materials/vitex_leaf` | 物品 | 暂无来源（待定，见 17–19） | N050 | — | vitex_leaf | Vitex Leaf | 原料 | — |
| 苍术 | `materials/atractylodes_rhizome` | 物品 | 暂无来源（待定，见 17–19） | N052 | — | atractylodes_rhizome | Atractylodes Rhizome | 原料 | — |
| 枣 | `materials/jujube` | 物品 | 暂无来源（待定，见 17–19） | N053 | — | jujube | Jujube | 原料 | — |
| 玫瑰花 | `materials/rose_flower` | 物品 | 原版 `minecraft:rose_bush`（游戏简化；与 N2 `materials/crimson_glory_rose` 重叠，见存疑） | N054、N056（经 N056 供 N055） | — | rose_flower | Rose Flower | 原料 | — |
| 鸢尾根 | `materials/orris_root` | 物品 | 暂无来源（待定，见 17–19） | N057、N058 | — | orris_root | Orris Root | 原料 | — |
| 杭白菊 | `materials/chrysanthemum_flower` | 物品 | 暂无来源（待定，见 17–19） | N059、N060 | — | chrysanthemum_flower | Chrysanthemum Flower | 原料 | — |
| 枫糖浆 | `materials/maple_syrup` | 物品 | 暂无来源（作物模组槭树标签未核对） | N061、N062 | — | maple_syrup | Maple Syrup | 原料 | — |
| 岩蔷薇枝叶 | `materials/rockrose_twig` | 物品 | 暂无来源（待定，见 17–19） | N063 | — | rockrose_twig | Rockrose Twig | 原料 | — |
| 焙炒咖啡豆 | `materials/roasted_coffee_bean` | 物品 | 咖啡生豆（`materials/coffee_bean`）焙炒（熔炉 / M17，同可可豆焙炒） | N064 | — | roasted_coffee_bean | Roasted Coffee Bean | 原料 | — |
| 金合欢花 | `materials/cassie_flower` | 物品 | 暂无来源（待定，见 17–19） | N066 | — | cassie_flower | Cassie Flower | 原料 | — |
| 大花茉莉花 | `materials/jasmine_grandiflorum_flower` | 物品 | 暂无来源（待定，见 17–19） | N069（经 N069 供 N068） | — | jasmine_grandiflorum_flower | Jasmine Grandiflorum Flower | 原料 | — |
| 小花茉莉花 | `materials/jasmine_sambac_flower` | 物品 | 暂无来源（待定，见 17–19） | N071（经 N071 供 N070） | — | jasmine_sambac_flower | Jasmine Sambac Flower | 原料 | — |
| 素方花 | `materials/jasmine_officinale_flower` | 物品 | 暂无来源（待定，见 17–19） | N124（经 `common_jasmine_concrete`） | — | jasmine_officinale_flower | Jasmine Officinale Flower | 原料 | — |
| 佛手 | `materials/fingered_citron` | 物品 | 暂无来源（待定，见 17–19） | N072 | — | fingered_citron | Fingered Citron | 原料 | — |
| 葫芦巴籽 | `materials/fenugreek_seed` | 物品 | 暂无来源（待定，见 17–19） | N079 | — | fenugreek_seed | Fenugreek Seed | 原料 | — |
| 柚 | `materials/pummelo` | 物品 | 暂无来源（待定，见 17–19） | N083 | — | pummelo | Pummelo | 原料 | — |
| 北美香柏枝叶 | `materials/thuja_leaf` | 物品 | 暂无来源（待定，见 17–19） | N084 | — | thuja_leaf | Thuja Leaf | 原料 | — |
| 孜然 | `materials/cumin_seed` | 物品 | 暂无来源（待定，见 17–19） | N085 | — | cumin_seed | Cumin Seed | 原料 | — |
| 柠檬叶 | `materials/lemon_leaf` | 物品 | 暂无来源（待定，见 17–19） | N089 | — | lemon_leaf | Lemon Leaf | 原料 | — |
| 栀子花 | `materials/gardenia_flower` | 物品 | 暂无来源（本模组栀子灌木 I07 可加花期采花，待定） | N091 | — | gardenia_flower | Gardenia Flower | 原料 | — |
| 树兰花 | `materials/aglaia_flower` | 物品 | 暂无来源（待定，见 17–19） | N092、N093、N094 | — | aglaia_flower | Aglaia Flower | 原料 | — |
| 树苔 | `materials/tree_moss` | 物品 | 暂无来源（待定，见 17–19） | N096（经 N096 供 N095） | — | tree_moss | Tree Moss | 原料 | — |
| 香叶天竺葵 | `materials/rose_geranium_leaf` | 物品 | 暂无来源（待定，见 17–19） | N097 | — | rose_geranium_leaf | Rose Geranium Leaf | 原料 | — |
| 香茶菜 | `materials/rabdosia_herb` | 物品 | 暂无来源（待定，见 17–19） | N099 | — | rabdosia_herb | Rabdosia Herb | 原料 | — |
| 香柠檬 | `materials/bergamot` | 物品 | 暂无来源（待定，见 17–19） | N101 | — | bergamot | Bergamot | 原料 | — |
| 香根草根 | `materials/vetiver_root` | 物品 | 暂无来源（待定，见 17–19） | N102、N103 | — | vetiver_root | Vetiver Root | 原料 | — |
| 发酵香荚兰豆 | `materials/cured_vanilla_bean` | 物品 | 香荚兰豆（`materials/vanilla`）热水杀青、发汗、干燥（30 A 前段，H07 晾晒架） | N104、N105 | — | cured_vanilla_bean | Cured Vanilla Bean | 原料 | — |
| 香附 | `materials/cyperus_rhizome` | 物品 | 暂无来源（待定，见 17–19） | N106 | — | cyperus_rhizome | Cyperus Rhizome | 原料 | — |
| 细香葱 | `materials/chives` | 物品 | 暂无来源（待定，见 17–19） | N107 | — | chives | Chives | 原料 | — |
| 香榧子壳 | `materials/torreya_shell` | 物品 | 暂无来源（待定，见 17–19） | N109 | — | torreya_shell | Torreya Shell | 原料 | — |
| 啤酒花 | `materials/hops` | 物品 | 暂无来源（12 啤酒同用） | N112、N113 | — | hops | Hops | 原料 | — |
| 蓝桉叶 | `materials/eucalyptus_leaf` | 物品 | 暂无来源（待定，见 17–19） | N114 | — | eucalyptus_leaf | Eucalyptus Leaf | 原料 | — |
| 海狸香囊 | `materials/castoreum` | 物品 | 暂无来源（待定，见 17–19） | N115 | — | castoreum | Castoreum | 原料 | — |
| 锡兰肉桂皮 | `materials/ceylon_cinnamon_bark` | 物品 | 暂无来源（待定，见 17–19） | N116 | — | ceylon_cinnamon_bark | Ceylon Cinnamon Bark | 原料 | — |
| 锡兰肉桂叶 | `materials/ceylon_cinnamon_leaf` | 物品 | 暂无来源（待定，见 17–19） | N117 | — | ceylon_cinnamon_leaf | Ceylon Cinnamon Leaf | 原料 | — |
| 桂花 | `materials/osmanthus_flower` | 物品 | 暂无来源（待定，见 17–19） | N119、N120（经 N120 供 N118） | — | osmanthus_flower | Osmanthus Flower | 原料 | — |
| 龙眼 | `materials/longan` | 物品 | 暂无来源（待定，见 17–19） | N121 | — | longan | Longan | 原料 | — |
| 留兰香 | `materials/spearmint` | 物品 | 暂无来源（待定，见 17–19） | N122 | — | spearmint | Spearmint | 原料 | — |
| 青核桃皮 | `materials/walnut_hull` | 物品 | 暂无来源（待定，见 17–19） | N123 | — | walnut_hull | Walnut Hull | 原料 | — |
| 蚕豆花 | `materials/broad_bean_flower` | 物品 | 暂无来源（待定，见 17–19） | N126 | — | broad_bean_flower | Broad Bean Flower | 原料 | — |
| 野蔷薇花 | `materials/wild_rose_flower` | 物品 | 暂无来源（待定，见 17–19） | N128 | — | wild_rose_flower | Wild Rose Flower | 原料 | — |
| 菊苣根 | `materials/chicory_root` | 物品 | 暂无来源（待定，见 17–19） | N134、549、539 | — | chicory_root | Chicory Root | 原料 | — |
| 晚香玉花 | `materials/tuberose_flower` | 物品 | 暂无来源（待定，见 17–19） | N135 | — | tuberose_flower | Tuberose Flower | 原料 | — |
| 紫罗兰叶 | `materials/violet_leaf` | 物品 | 暂无来源（待定，见 17–19） | N136 | — | violet_leaf | Violet Leaf | 原料 | — |
| 椒样薄荷 | `materials/peppermint` | 物品 | 暂无来源（待定，见 17–19） | N137 | — | peppermint | Peppermint | 原料 | — |
| 黑加仑芽 | `materials/blackcurrant_bud` | 物品 | 暂无来源（待定，见 17–19） | N138、N139 | — | blackcurrant_bud | Blackcurrant Bud | 原料 | — |
| 槐花 | `materials/sophora_flower` | 物品 | 暂无来源（待定，见 17–19） | N141（N140） | — | sophora_flower | Sophora Flower | 原料 | — |
| 愈疮木 | `materials/guaiac_wood` | 物品 | 暂无来源（待定，见 17–19） | N144 | — | guaiac_wood | Guaiac Wood | 原料 | — |
| 缬草根 | `materials/valerian_root` | 物品 | 暂无来源（待定，见 17–19） | N145 | — | valerian_root | Valerian Root | 原料 | — |
| 墨红花 | `materials/crimson_glory_rose` | 物品 | 暂无来源（原版玫瑰丛归玫瑰花 `materials/rose_flower`，2026-11-11 定） | N147（N146） | — | crimson_glory_rose | Crimson Glory Rose | 原料 | — |
| 苦橙叶 | `materials/bitter_orange_leaf` | 物品 | 暂无来源（待定，见 17–19） | N149、N254 | — | bitter_orange_leaf | Bitter Orange Leaf | 原料 | — |
| 檀香木 | `materials/sandalwood` | 物品 | 暂无来源（待定，见 17–19） | N152 | — | sandalwood | Sandalwood | 原料 | — |
| 薰衣草 | `materials/lavender` | 物品 | 暂无来源（待定，见 17–19） | N153 | — | lavender | Lavender | 原料 | — |
| 头状百里香 | `materials/conehead_thyme` | 物品 | 暂无来源（待定，见 17–19） | N154 | — | conehead_thyme | Conehead Thyme | 原料 | — |
| 可乐果 | `materials/kola_nut` | 物品 | 暂无来源（待定，见 17–19） | N155 | — | kola_nut | Kola Nut | 原料 | — |
| 加州胡椒果 | `materials/peruvian_pepper_berry` | 物品 | 暂无来源（待定，见 17–19） | N156 | — | peruvian_pepper_berry | Peruvian Pepper Berry | 原料 | — |
| 卡黎皮 | `materials/cascarilla_bark` | 物品 | 暂无来源（待定，见 17–19） | N157 | — | cascarilla_bark | Cascarilla Bark | 原料 | — |
| 百里香 | `materials/thyme` | 物品 | 暂无来源（待定，见 17–19） | N158（N235） | — | thyme | Thyme | 原料 | — |
| 依兰花 | `materials/ylang_ylang_flower` | 物品 | 暂无来源（待定，见 17–19） | N067 | — | ylang_ylang_flower | Ylang Ylang Flower | 原料 | — |
| 月桂叶 | `materials/laurel_leaf` | 物品 | 暂无来源（待定，见 17–19） | N161 | — | laurel_leaf | Laurel Leaf | 原料 | — |
| 生姜 | `materials/ginger` | 物品 | 暂无来源（待定，见 17–19） | N162 | — | ginger | Ginger | 原料 | — |
| 白栎木 | `materials/oak_log` | 物品 | `#minecraft:oak_logs`（原版橡木原木） | N163 | — | oak_log | Oak Log | 原料 | — |
| 龙蒿 | `materials/tarragon` | 物品 | 暂无来源（待定，见 17–19） | N164 | — | tarragon | Tarragon | 原料 | — |
| 樟木 | `materials/camphor_wood` | 物品 | 暂无来源（待定，见 17–19） | `crude_camphor_oil`（N165、N220） | — | camphor_wood | Camphor Wood | 原料 | — |
| 肉豆蔻衣 | `materials/mace` | 物品 | 暂无来源（待定，见 17–19） | N166、N245 | — | mace | Mace | 原料 | — |
| 众香叶 | `materials/pimento_leaf` | 物品 | 暂无来源（待定，见 17–19） | N167 | — | pimento_leaf | Pimento Leaf | 原料 | — |
| 西班牙鼠尾草 | `materials/spanish_sage` | 物品 | 暂无来源（待定，见 17–19） | N168 | — | spanish_sage | Spanish Sage | 原料 | — |
| 红橘 | `materials/mandarin` | 物品 | 暂无来源（待定，见 17–19） | N110 | — | mandarin | Mandarin | 原料 | — |
| 杂薰衣草 | `materials/lavandin` | 物品 | 暂无来源（待定，见 17–19） | N170 | — | lavandin | Lavandin | 原料 | — |
| 杏仁 | `materials/apricot_kernel` | 物品 | 暂无来源（待定，见 17–19） | N171 | — | apricot_kernel | Apricot Kernel | 原料 | — |
| 苏合香原脂 | `materials/styrax` | 物品 | 暂无来源（待定，见 17–19） | N172、N173 | — | styrax_resin | Styrax | 原料 | — |
| 角豆荚 | `materials/carob_pod` | 物品 | 暂无来源（待定，见 17–19） | N174、N175 | — | carob_pod | Carob Pod | 原料 | — |
| 乳香 | `materials/olibanum` | 物品 | 暂无来源（待定，见 17–19） | N177 | — | olibanum | Olibanum | 原料 | — |
| 没药 | `materials/myrrh` | 物品 | 暂无来源（待定，见 17–19） | N178 | — | myrrh | Myrrh | 原料 | — |
| 高良姜根茎 | `materials/galangal_root` | 物品 | 暂无来源（待定，见 17–19） | N179 | — | galangal_root | Galangal Root | 原料 | — |
| 松针 | `materials/pine_needles` | 物品 | 暂无来源（原版云杉树叶已归冷杉针叶 `materials/fir_needles`，2026-11-11 定） | N180 | — | pine_needles | Pine Needles | 原料 | — |
| 小茴香籽 | `materials/fennel_seed` | 物品 | 暂无来源（待定，见 17–19） | N181 | — | fennel_seed | Fennel Seed | 原料 | — |
| 苦扁桃仁 | `materials/bitter_almond` | 物品 | 暂无来源（待定，见 17–19） | N182 | — | bitter_almond | Bitter Almond | 原料 | — |
| 阿魏 | `materials/asafoetida` | 物品 | 暂无来源（待定，见 17–19） | N183 | — | asafoetida | Asafoetida | 原料 | — |
| 欧芹 | `materials/parsley` | 物品 | 暂无来源（待定，见 17–19） | N185、N251 | — | parsley | Parsley | 原料 | — |
| 冷杉针叶 | `materials/fir_needles` | 物品 | 原版云杉树叶 `minecraft:spruce_leaves`（游戏简化：香脂冷杉与云杉同为松科） | N192 | — | fir_needles | Fir Needles | 原料 | — |
| 波罗尼花 | `materials/boronia_flower` | 物品 | 暂无来源（待定，见 17–19） | N187 | — | boronia_flower | Boronia Flower | 原料 | — |
| 玫瑰木 | `materials/rosewood` | 物品 | 暂无来源（待定，见 17–19） | N188 | — | rosewood | Rosewood | 原料 | — |
| 玫瑰草 | `materials/palmarosa` | 物品 | 暂无来源（待定，见 17–19） | N189 | — | palmarosa | Palmarosa | 原料 | — |
| 香茅 | `materials/citronella_grass` | 物品 | 暂无来源（待定，见 17–19） | N190 | — | citronella_grass | Citronella Grass | 原料 | — |
| 加拿大香脂 | `materials/canada_balsam` | 物品 | 暂无来源（待定，见 17–19） | N193 | — | canada_balsam | Canada Balsam | 原料 | — |
| 胡萝卜籽 | `materials/carrot_seed` | 物品 | 暂无来源（待定，见 17–19） | N194 | — | carrot_seed | Carrot Seed | 原料 | — |
| 罗马春黄菊花 | `materials/roman_chamomile` | 物品 | 暂无来源（待定，见 17–19） | N195、N196 | — | roman_chamomile | Roman Chamomile | 原料 | — |
| 药鼠李皮 | `materials/cascara_bark` | 物品 | 暂无来源（待定，见 17–19） | N197 | — | cascara_bark | Cascara Bark | 原料 | — |
| 荜澄茄 | `materials/cubeb` | 物品 | 暂无来源（待定，见 17–19） | N198 | — | cubeb | Cubeb | 原料 | — |
| 胡薄荷 | `materials/pennyroyal` | 物品 | 暂无来源（待定，见 17–19） | N199 | — | pennyroyal | Pennyroyal | 原料 | — |
| 欧当归根 | `materials/lovage_root` | 物品 | 暂无来源（待定，见 17–19） | N200、N250 | — | lovage_root | Lovage Root | 原料 | — |
| 欧夏至草 | `materials/horehound` | 物品 | 暂无来源（待定，见 17–19） | N201 | — | horehound | Horehound | 原料 | — |
| 丝兰茎 | `materials/yucca_trunk` | 物品 | 暂无来源（待定，见 17–19） | N202 | — | yucca_trunk | Yucca Trunk | 原料 | — |
| 海索草 | `materials/hyssop` | 物品 | 暂无来源（待定，见 17–19） | N204 | — | hyssop | Hyssop | 原料 | — |
| 莳萝 | `materials/dill` | 物品 | 暂无来源（待定，见 17–19） | N205 | — | dill | Dill | 原料 | — |
| 秘鲁香脂原脂 | `materials/crude_peru_balsam` | 物品 | 暂无来源（待定，见 17–19） | N206 | — | crude_peru_balsam | Crude Peru Balsam | 原料 | — |
| 格蓬 | `materials/galbanum` | 物品 | 暂无来源（待定，见 17–19） | N207 | — | galbanum | Galbanum | 原料 | — |
| 脂檀木 | `materials/amyris_wood` | 物品 | 暂无来源（待定，见 17–19） | N208 | — | amyris_wood | Amyris Wood | 原料 | — |
| 银荆花 | `materials/mimosa_flower` | 物品 | 暂无来源（待定，见 17–19） | N209 | — | mimosa_flower | Mimosa Flower | 原料 | — |
| 接骨木花 | `materials/elderflower` | 物品 | 暂无来源（待定，见 17–19） | N210 | — | elderflower | Elderflower | 原料 | — |
| 甘牛至 | `materials/marjoram` | 物品 | 暂无来源（待定，见 17–19） | N211 | — | marjoram | Marjoram | 原料 | — |
| 黄龙胆根 | `materials/gentian_root` | 物品 | 暂无来源（待定，见 17–19） | N212 | — | gentian_root | Gentian Root | 原料 | — |
| 黄葵籽 | `materials/ambrette_seed` | 物品 | 暂无来源（待定，见 17–19） | N213 | — | ambrette_seed | Ambrette Seed | 原料 | — |
| 野黑樱桃树皮 | `materials/wild_cherry_bark` | 物品 | 暂无来源（待定，见 17–19） | N214 | — | wild_cherry_bark | Wild Cherry Bark | 原料 | — |
| 黑胡椒 | `materials/black_pepper` | 物品 | 暂无来源（待定，见 17–19） | N215 | — | black_pepper | Black Pepper | 原料 | — |
| 榄香脂 | `materials/elemi` | 物品 | 暂无来源（待定，见 17–19） | N217 | — | elemi | Elemi | 原料 | — |
| 蜡菊花 | `materials/immortelle_flower` | 物品 | 暂无来源（待定，见 17–19） | N218 | — | immortelle_flower | Immortelle Flower | 原料 | — |
| 香蜂花 | `materials/lemon_balm` | 物品 | 暂无来源（待定，见 17–19） | N219 | — | lemon_balm | Lemon Balm | 原料 | — |
| 苦橙花 | `materials/bitter_orange_flower` | 物品 | 暂无来源（待定，见 17–19） | N221 | — | bitter_orange_flower | Bitter Orange Flower | 原料 | — |
| 葡萄柚 | `materials/grapefruit` | 物品 | `#forge:fruits/grapefruit`（08 柑橘已列） | N222 | — | grapefruit | Grapefruit | 原料 | — |
| 穗薰衣草 | `materials/spike_lavender` | 物品 | 暂无来源（待定，见 17–19） | N223 | — | spike_lavender | Spike Lavender | 原料 | — |
| 鹰爪豆花 | `materials/genet_flower` | 物品 | 暂无来源（待定，见 17–19） | N224 | — | genet_flower | Genet Flower | 原料 | — |
| 苦橙 | `materials/bitter_orange` | 物品 | 暂无来源（待定，见 17–19） | N255 | — | bitter_orange | Bitter Orange | 原料 | — |
| 小米辣 | `materials/bird_eye_chili` | 物品 | 暂无来源（或并入 `materials/dried_chili`） | N227 | — | bird_eye_chili | Bird Eye Chili | 原料 | — |
| 丁香花梗 | `materials/clove_stem` | 物品 | 暂无来源（待定，见 17–19） | N228 | — | clove_stem | Clove Stem | 原料 | — |
| 茴芹籽 | `materials/anise_seed` | 物品 | 暂无来源（待定，见 17–19） | N229 | — | anise_seed | Anise Seed | 原料 | — |
| 巴拉圭茶叶 | `materials/mate_leaf` | 物品 | 暂无来源（待定，见 17–19） | N231 | — | mate_leaf | Mate Leaf | 原料 | — |
| 山核桃树皮 | `materials/hickory_bark` | 物品 | 暂无来源（待定，见 17–19） | N232 | — | hickory_bark | Hickory Bark | 原料 | — |
| 瓜拉纳籽 | `materials/guarana_seed` | 物品 | 暂无来源（待定，见 17–19） | N233 | — | guarana_seed | Guarana Seed | 原料 | — |
| 白胡椒 | `materials/white_pepper` | 物品 | 暂无来源（待定，见 17–19） | N236、N237 | — | white_pepper | White Pepper | 原料 | — |
| 众香果 | `materials/allspice` | 物品 | 暂无来源（待定，见 17–19） | N241 | — | allspice | Allspice | 原料 | — |
| 安息香 | `materials/benzoin` | 物品 | 暂无来源（待定，见 17–19） | N242 | — | benzoin_resin | Benzoin | 原料 | — |
| 当归籽 | `materials/angelica_seed` | 物品 | 暂无来源（待定，见 17–19） | N243 | — | angelica_seed | Angelica Seed | 原料 | — |
| 当归根 | `materials/angelica_root` | 物品 | 暂无来源（待定，见 17–19） | N244 | — | angelica_root | Angelica Root | 原料 | — |
| 西印度月桂叶 | `materials/west_indian_bay_leaf` | 物品 | 暂无来源（待定，见 17–19） | N246、N247 | — | west_indian_bay_leaf | West Indian Bay Leaf | 原料 | — |
| 苦木 | `materials/quassia_wood` | 物品 | 暂无来源（待定，见 17–19） | N253 | — | quassia_wood | Quassia Wood | 原料 | — |
| 金鸡纳树皮 | `materials/cinchona_bark` | 物品 | 暂无来源（待定，见 17–19） | N256、N258 | — | cinchona_bark | Cinchona Bark | 原料 | — |
| 金钮扣 | `materials/jambu` | 物品 | 暂无来源（待定，见 17–19） | N257 | — | jambu | Jambu | 原料 | — |
| 互叶白千层叶 | `materials/tea_tree_leaf` | 物品 | 暂无来源（待定，见 17–19） | N261 | — | tea_tree_leaf | Tea Tree Leaf | 原料 | — |
| 白柠檬 | `materials/lime` | 物品 | `#forge:fruits/lime`（08 柑橘已列） | N262 | — | lime | Lime | 原料 | — |
| 白芥子 | `materials/yellow_mustard_seed` | 物品 | 暂无来源（待定，见 17–19） | N265 | — | yellow_mustard_seed | Yellow Mustard Seed | 原料 | — |
| 芥菜籽 | `materials/brown_mustard_seed` | 物品 | 暂无来源（待定，见 17–19） | N266 | — | brown_mustard_seed | Brown Mustard Seed | 原料 | — |
| 紫苏叶 | `materials/perilla_leaf` | 物品 | 暂无来源（待定，见 17–19） | N268 | — | perilla_leaf | Perilla Leaf | 原料 | — |
| 药鼠尾草 | `materials/sage` | 物品 | 暂无来源（待定，见 17–19） | N274、N296 | — | sage | Sage | 原料 | — |
| 山达草叶 | `materials/yerba_santa` | 物品 | 暂无来源（待定，见 17–19） | N278、N398 | — | yerba_santa | Yerba Santa | 原料 | — |
| 苜蓿 | `materials/alfalfa` | 物品 | 暂无来源（待定，见 17–19） | N279 | — | alfalfa | Alfalfa | 原料 | — |
| 母菊花 | `materials/german_chamomile` | 物品 | 暂无来源（待定，见 17–19） | N287、N309、N357 | — | german_chamomile | German Chamomile | 原料 | — |
| 红三叶草花序 | `materials/red_clover` | 物品 | 暂无来源（待定，见 17–19） | N290 | — | red_clover | Red Clover | 原料 | — |
| 蒲公英 | `materials/dandelion` | 物品 | 原版 `minecraft:dandelion`（游戏简化：花代全株） | N291、N292 | — | dandelion | Dandelion | 原料 | — |
| 加拿大飞蓬 | `materials/canadian_fleabane` | 物品 | 暂无来源（待定，见 17–19） | N293 | — | canadian_fleabane | Canadian Fleabane | 原料 | — |
| 穗花槭树皮 | `materials/mountain_maple_bark` | 物品 | 暂无来源（待定，见 17–19） | N294 | — | mountain_maple_bark | Mountain Maple Bark | 原料 | — |
| 芸香 | `materials/rue` | 物品 | 暂无来源（待定，见 17–19） | N295 | — | rue | Rue | 原料 | — |
| 菝葜根 | `materials/sarsaparilla_root` | 物品 | 暂无来源（待定，见 17–19） | N297 | — | sarsaparilla_root | Sarsaparilla Root | 原料 | — |
| 苦艾 | `materials/wormwood` | 物品 | 暂无来源（待定，见 17–19） | N302、N345、N348 | — | wormwood | Wormwood | 原料 | — |
| 橘柚 | `materials/tangelo` | 物品 | 暂无来源（待定，见 17–19） | N304 | — | tangelo | Tangelo | 原料 | — |
| 美国栗叶 | `materials/chestnut_leaf` | 物品 | 暂无来源（待定，见 17–19） | N306 | — | chestnut_leaf | Chestnut Leaf | 原料 | — |
| 古巴香脂 | `materials/copaiba_balsam` | 物品 | 暂无来源（待定，见 17–19） | N307 | — | copaiba_balsam | Copaiba Balsam | 原料 | — |
| 达迷草叶 | `materials/damiana_leaf` | 物品 | 暂无来源（待定，见 17–19） | N308 | — | damiana_leaf | Damiana Leaf | 原料 | — |
| 甜没药 | `materials/opoponax` | 物品 | 暂无来源（待定，见 17–19） | N311、N341 | — | opoponax | Opoponax | 原料 | — |
| 藏红花柱头 | `materials/saffron` | 物品 | 暂无来源（待定，见 17–19） | N312 | — | saffron | Saffron | 原料 | — |
| 酸角果肉 | `materials/tamarind_pulp` | 物品 | 暂无来源（待定，见 17–19） | N317 | — | tamarind_pulp | Tamarind Pulp | 原料 | — |
| 辣根 | `materials/horseradish_root` | 物品 | 暂无来源（待定，见 17–19） | N318 | — | horseradish_root | Horseradish Root | 原料 | — |
| 芹菜叶 | `materials/celery_leaf` | 物品 | 暂无来源（作物模组芹菜标签待核对） | N320 | — | celery_leaf | Celery Leaf | 原料 | — |
| 韭葱 | `materials/leek` | 物品 | 暂无来源（作物模组韭葱标签待核对） | N326 | — | leek | Leek | 原料 | — |
| 香橙 | `materials/yuzu` | 物品 | 暂无来源（待定，见 17–19） | N329 | — | yuzu | Yuzu | 原料 | — |
| 墨西哥牛至 | `materials/mexican_oregano` | 物品 | 暂无来源（待定，见 17–19） | N331 | — | mexican_oregano | Mexican Oregano | 原料 | — |
| 冬香薄荷 | `materials/winter_savory` | 物品 | 暂无来源（待定，见 17–19） | N333 | — | winter_savory | Winter Savory | 原料 | — |
| 桃叶 | `materials/peach_leaf` | 物品 | 暂无来源（待定，见 17–19） | N336 | — | peach_leaf | Peach Leaf | 原料 | — |
| 白藓牛至 | `materials/dittany_of_crete` | 物品 | 暂无来源（待定，见 17–19） | N337 | — | dittany_of_crete | Dittany of Crete | 原料 | — |
| 花椒 | `materials/sichuan_pepper` | 物品 | 暂无来源（待定，见 17–19） | N342 | — | sichuan_pepper | Sichuan Pepper | 原料 | — |
| 儿茶心材 | `materials/catechu_heartwood` | 物品 | 暂无来源（待定，见 17–19） | N344 | — | catechu_heartwood | Catechu Heartwood | 原料 | — |
| 橡苔 | `materials/oakmoss` | 物品 | 暂无来源（待定，见 17–19） | N353 | — | oakmoss | Oakmoss | 原料 | — |
| 苏格兰留兰香 | `materials/scotch_spearmint` | 物品 | 暂无来源（待定，见 17–19） | N354 | — | scotch_spearmint | Scotch Spearmint | 原料 | — |
| 安古树皮 | `materials/angostura_bark` | 物品 | 暂无来源（待定，见 17–19） | N356 | — | angostura_bark | Angostura Bark | 原料 | — |
| 灵猫香 | `materials/civet` | 物品 | 暂无来源（待定，见 17–19） | N361 | — | civet | Civet | 原料 | — |
| 橘叶 | `materials/mandarin_leaf` | 物品 | 暂无来源（待定，见 17–19） | N366 | — | mandarin_leaf | Mandarin Leaf | 原料 | — |
| 欧洲山松针叶 | `materials/dwarf_pine_needles` | 物品 | 暂无来源（不与 N2 `materials/pine_needles` 共用，避免 NT1 配方冲突） | N367 | — | dwarf_pine_needles | Dwarf Pine Needles | 原料 | — |
| 玫瑰果 | `materials/rose_hip` | 物品 | 暂无来源（待定，见 17–19） | N368 | — | rose_hip | Rose Hip | 原料 | — |
| 夏香薄荷 | `materials/summer_savory` | 物品 | 暂无来源（待定，见 17–19） | N369 | — | summer_savory | Summer Savory | 原料 | — |
| 加拿大细辛 | `materials/canadian_snakeroot` | 物品 | 暂无来源（待定，见 17–19） | N370 | — | canadian_snakeroot | Canadian Snakeroot | 原料 | — |
| 黄蓍胶片 | `materials/tragacanth_lump` | 物品 | 暂无来源（待定，见 17–19） | N372 | — | tragacanth_lump | Tragacanth Lump | 原料 | — |
| 摩洛哥豆蔻 | `materials/grains_of_paradise` | 物品 | 暂无来源（待定，见 17–19） | N374 | — | grains_of_paradise | Grains of Paradise | 原料 | — |
| 鲣节 | `materials/katsuobushi` | 物品 | 鱼（`materials/raw_fish`）M03 蒸煮 → M10 烟熏、烘干（需补此配方；游戏简化，省去霉化） | N379 | — | katsuobushi | Katsuobushi | 原料 | — |
| 药蜀葵根 | `materials/marshmallow_root` | 物品 | 暂无来源（待定，见 17–19） | N382 | — | marshmallow_root | Marshmallow Root | 原料 | — |
| 白千层枝叶 | `materials/cajeput_leaf` | 物品 | 暂无来源（待定，见 17–19） | N384 | — | cajeput_leaf | Cajeput Leaf | 原料 | — |
| 芦荟叶 | `materials/aloe` | 物品 | 暂无来源（待定，见 17–19） | N389 | — | aloe | Aloe | 原料 | — |
| 龙涎香 | `materials/ambergris` | 物品 | 暂无来源（待定，见 17–19） | N390 | — | ambergris | Ambergris | 原料 | — |
| 燕根 | `materials/swallowroot` | 物品 | 暂无来源（待定，见 17–19） | N392 | — | swallowroot | Swallowroot | 原料 | — |
| 香厚壳桂皮 | `materials/massoia_bark` | 物品 | 暂无来源（待定，见 17–19） | N397 | — | massoia_bark | Massoia Bark | 原料 | — |
| 巴拉塔胶乳 | `balata_latex`（流体） | 流体 | 暂无来源（待定，见 17–19） | 564 | — | balata_latex | Balata Latex | 原料 | — |
| 节路顿胶乳 | `jelutong_latex`（流体） | 流体 | 暂无来源（待定，见 17–19） | 565 | — | jelutong_latex | Jelutong Latex | 原料 | — |
| 来开欧胶乳 | `sorva_latex`（流体） | 流体 | 暂无来源（待定，见 17–19） | 566 | — | sorva_latex | Sorva Latex | 原料 | — |
| 人心果胶乳 | `sapodilla_latex`（流体） | 流体 | 暂无来源（待定，见 17–19） | 567、568 | — | sapodilla_latex | Sapodilla Latex | 原料 | — |
| 松树伐根 | `materials/pine_stump` | 物品 | 暂无来源（待定，见 17–19） | 427、579 | — | pine_stump | Pine Stump | 原料 | — |
| 小烛树茎秆 | `materials/candelilla_stem` | 物品 | 暂无来源（待定，见 17–19） | 582 | — | candelilla_stem | Candelilla Stem | 原料 | — |
| 凹凸棒石黏土矿 | `materials/attapulgite` | 物品 | 暂无来源（待定，见 17–19） | 438 | — | attapulgite | Attapulgite | 原料 | — |
| 膨润土矿石 | `materials/bentonite` | 物品 | 暂无来源（GTCEu `#forge:ores/bentonite`、`#forge:dusts/bentonite`，未核对） | 443、440 | — | bentonite_ore | Bentonite | 原料 | — |
| 珍珠岩矿石 | `materials/perlite` | 物品 | 暂无来源（GTCEu `#forge:dusts/perlite`，未核对） | 444 | — | perlite_ore | Perlite | 原料 | — |
| 滑石矿石 | `materials/talc` | 物品 | 暂无来源（GTCEu `#forge:ores/talc`、`#forge:dusts/talc`、`soapstone`，未核对） | 451 | — | talc_ore | Talc | 原料 | — |
| 月见草籽 | `materials/evening_primrose_seed` | 物品 | 暂无来源（待定，见 17–19） | 538 | — | evening_primrose_seed | Evening Primrose Seed | 原料 | — |
| 粗硒 | `materials/selenium` | 物品 | 他模组标签（候选 `#forge:dusts/selenium`，未核对）；本模组另有硒矿石焙烧路线（509 B，不经粗硒） | 509（经 509 供 510–515） | — | selenium | Selenium | 原料 | — |
| 铬 | `materials/chromium` | 物品 | 他模组标签 `#forge:ingots/chrome`、`#forge:dusts/chrome`（GTCEu 命名，未核对）；未装时为空 | 527、528 | — | chromium | Chromium | 原料 | — |
| 动物血 | `animal_blood`（流体） | 流体 | 原版生肉（牛肉、猪肉、羊肉、鸡肉、兔肉）在 M12 固液分离机压榨，副产动物血（游戏简化）；同时接受他模组血液标签（如 `#forge:blood`，未核对） | 485、486、493 | — | animal_blood | Animal Blood | 原料 | — |
| 富硒大豆 | `materials/selenium_rich_soybean` | 物品 | 大豆（标签）在 M04 浸提桶用亚硒酸钠（509）稀溶液浸泡发芽富集（游戏简化：现实为富硒土壤或叶面喷施栽培） | 511 | — | selenium_rich_soybean | Selenium Rich Soybean | 原料 | — |
| 食用菌 | `materials/mushroom` | 物品 | 原版 `minecraft:brown_mushroom`（褐色蘑菇） | 512（分离菌种） | — | mushroom | Mushroom | 原料 | — |
| 硒矿石 | `materials/selenium_ore` | 矿石 / 方块 | 世界生成（硒矿石，见 08 世界生成表） | 509 B | — | selenium_ore | Selenium Ore | 原料 | — |
| 铬铁矿石 | `materials/chromite` | 矿石 / 方块 | 世界生成（铬铁矿石，见 08 世界生成表）；同时导入 `#forge:ores/chromite` | `sodium_chromate`（527 B、528 B） | — | chromite | Chromite | 原料 | — |
| 玳玳果 | `materials/daidai_fruit` | 物品 | 暂无来源（待定，见 17–19） | N082、N225 | — | daidai_fruit | Daidai Fruit | 原料 | — |
| 红橘 | `materials/tangerine` | 物品 | 暂无来源（待定，见 17–19） | N169 | — | tangerine | Tangerine | 原料 | — |
| 卡南伽花 | `materials/cananga_flower` | 物品 | 暂无来源（待定，见 17–19） | N160 | — | cananga_flower | Cananga Flower | 原料 | — |
| 冷杉属针叶 | `materials/abies_needles` | 物品 | 暂无来源（待定，见 17–19） | N186 | — | abies_needles | Abies Needles | 原料 | — |
| 朗姆酒 | `foods/rum` | 物品 | 12 成品朗姆酒（`rum`，2026-11-11 实装时核对成品表已有） | N380 | — | rum | Rum | 原料 | — |

> I07 的方块与作物（2026-11-04 定）：作物的收获物本身可种（同胡萝卜），野外有野生植株；栀子按甜浆果丛、香荚兰按可可果的形态；橡胶木做完整木材套组（原木、木头、去皮原木、去皮木头、木板、楼梯、台阶、栅栏、栅栏门、门、活板门、压力板、按钮、树叶、树苗；告示牌、悬挂式告示牌、船列入后续）；松脂先按原料注册；松脂是多组分混合物，不带纯度（2026-11-05，10 §1.1）。后备来源只做硫矿石、磷灰石矿石（未装热力时）和稻谷、树皮（未装农夫乐事时）；镍、锡、银、铅不做后备，依赖其他模组。后备物品在各节点都注册，只在缺对应模组时生成或掉落，配置文件 `foodindustrial-common.toml` 的 `[fallbacks]` 可强制启用。

#### I07 方块与作物的标签

| 原料 | 配方引用标签 | 默认内容（1.20.1） | 默认内容（1.21 起） |
| --- | --- | --- | --- |
| 石花菜 | `materials/red_algae` | `foodindustrial:gelidium` | `foodindustrial:gelidium` |
| 麒麟菜 | `materials/red_algae_carrageenan` | `foodindustrial:eucheuma` | `foodindustrial:eucheuma` |
| 薄荷 | `materials/mint` | `foodindustrial:mint` | `foodindustrial:mint` |
| 香荚兰 | `materials/vanilla` | `foodindustrial:vanilla_pod` | `foodindustrial:vanilla_pod` |
| 甜菊 | `materials/stevia` | `foodindustrial:stevia` | `foodindustrial:stevia` |
| 栀子 | `materials/gardenia` | `foodindustrial:gardenia_fruit` | `foodindustrial:gardenia_fruit` |
| 迷迭香 | `materials/rosemary` | `foodindustrial:rosemary` | `foodindustrial:rosemary` |
| 蓼蓝 | `materials/indigo_plant` | `foodindustrial:indigo_plant` | `foodindustrial:indigo_plant` |
| 稻谷 | `materials/paddy` | `foodindustrial:paddy` | `foodindustrial:paddy` |
| 石膏矿石 | `materials/gypsum` | `foodindustrial:gypsum_ore`、`#forge:ores/gypsum` | `foodindustrial:gypsum_ore`、`#c:ores/gypsum` |
| 明矾石矿石 | `materials/alunite` | `foodindustrial:alunite_ore`、`#forge:ores/alunite` | `foodindustrial:alunite_ore`、`#c:ores/alunite` |
| 天然碱矿 | `materials/trona` | `foodindustrial:trona_ore` | `foodindustrial:trona_ore` |
| 岩盐 | `materials/rock_salt` | `foodindustrial:rock_salt`、`#forge:ores/salt` | `foodindustrial:rock_salt`、`#c:ores/salt` |
| 菱镁矿石 | `materials/magnesite` | `foodindustrial:magnesite_ore`、`#forge:ores/magnesite` | `foodindustrial:magnesite_ore`、`#c:ores/magnesite` |
| 钠硝石矿 | `materials/nitratine` | `foodindustrial:nitratine` | `foodindustrial:nitratine` |
| 硝土 | `materials/nitre_soil` | `foodindustrial:nitre_soil` | `foodindustrial:nitre_soil` |
| 磷灰石 | `materials/apatite` | `foodindustrial:apatite_ore`、`#forge:ores/apatite` | `foodindustrial:apatite_ore`、`#c:ores/apatite` |
| 橡胶木原木 | `materials/rubber_log` | `foodindustrial:rubber_log`、`foodindustrial:rubber_wood`、`#forge:rubberwood_logs`、`#gtceu:rubber_logs` | `foodindustrial:rubber_log`、`foodindustrial:rubber_wood`、`#gtceu:rubber_logs` |
| 硒矿石 | `materials/selenium_ore` | `foodindustrial:selenium_ore` | `foodindustrial:selenium_ore` |
| 铬铁矿石 | `materials/chromite` | `foodindustrial:chromite_ore`、`#forge:ores/chromite` | `foodindustrial:chromite_ore`、`#c:ores/chromite` |

> 矿石的 `ores/<名>` 按规则 3 的通用写法写入；其中只有 `ores/apatite`（热力）在核对的模组中有提供者。

#### 世界生成（I07，2026-11-04 拟定；2026-11-12 用户确认，岩盐按要求调低）

矿脉只替换石头（`#minecraft:stone_ore_replaceables`），不做深板岩变种，高度都在 0 以上。“每区块”是每个区块尝试的次数，“1/N”表示每 N 个区块一次。

| 方块 | 群系 | 方式 | 高度 | 每区块 | 规模 |
| --- | --- | --- | --- | --- | --- |
| 石膏矿石 | 沙漠、恶地 | 矿脉 | 0–80 | 8 | 12 |
| 明矾石矿石 | 山地（`#minecraft:is_mountain`）、风袭丘陵 | 矿脉 | 32–160 | 6 | 9 |
| 岩盐 | 主世界 | 矿脉 | 0–48 | 2 | 16（原每区块 3 次、规模 24，实测每区块约 100 块，2026-11-12 调低） |
| 菱镁矿石 | 主世界 | 矿脉 | 0–64 | 4 | 9 |
| 天然碱矿 | 沙漠、恶地 | 地表矿脉，替换沙、红沙、陶瓦 | 地表 | 1/6 | 20 |
| 钠硝石矿 | 沙漠、恶地 | 地表矿脉，替换沙、红沙、陶瓦 | 地表 | 1/10 | 16 |
| 磷灰石矿石（后备） | 主世界 | 矿脉 | 0–64 | 4 | 9 |
| 硫矿石（后备） | 主世界 | 矿脉 | 0–32 | 5 | 7 |
| 硒矿石 | 主世界 | 矿脉 | 0–48 | 2 | 6 |
| 铬铁矿石 | 山地（`#minecraft:is_mountain`）、风袭丘陵 | 矿脉 | 0–64 | 3 | 8 |
| 野生甜菊 | 平原、向日葵平原、热带草原 | 草地上成簇 | 地表 | 1/24 | 每簇 32 次，水平 ±7，垂直 ±3 |
| 野生薄荷 | 平原、向日葵平原、草甸 | 同上 | 地表 | 1/24 | 同上 |
| 野生迷迭香 | 草甸、风袭丘陵 | 同上 | 地表 | 1/24 | 同上 |
| 野生蓼蓝 | 河流、沼泽 | 同上 | 地表 | 1/16 | 同上 |
| 野生稻谷 | 沼泽、红树林沼泽、河流 | 同上 | 地表 | 1/16 | 同上 |
| 栀子丛 | 丛林、稀疏丛林、竹林 | 同上，已成熟 | 地表 | 1/32 | 同上 |
| 香荚兰 | 丛林、稀疏丛林、竹林 | 长在丛林原木侧面，可替换树干上的藤蔓 | 树冠下地面以上 1–8 格 | 每个朝向 16 次 | — |
| 石花菜 | 暖水海洋、温水海洋、温水深海 | 海底 | 海底 | 8 | — |
| 麒麟菜 | 暖水海洋 | 海底 | 海底 | 6 | — |
| 橡胶树 | 丛林、稀疏丛林 | 树（直干 5–7 格，球形树冠半径 2） | 地表 | 1/3 | — |

### 菌种与酶（物品，可在 M30 菌种工作台繁殖）

菌种带纯度（K11，按目标菌所占比例）；酶与耗材不带纯度，现实按酶活计价（2026-11-05 定，10 §1.1）。

名称里的括号是名称的一部分（限定语，如“工程菌（漆酶）”），不是日常名称，游戏内原样显示；GB 2760 的“又名”括号不写（2026-11-05 定，文本规范第 2.1 节）。

| 菌种 / 酶 | 配方引用标签 | 自然来源（H13） | 用于 | ID | 英文名（待审） | 分类 |
| --- | --- | --- | --- | --- | --- | --- |
| 醋酸菌（醋母） | `cultures/acetobacter` | 陶坛开口静置果酒（H13） | 1 | acetobacter | Acetobacter (Vinegar Mother) | 菌种 |
| 乳酸菌（老卤） | `cultures/lactobacillus` | 陶坛封口腌菜（H13） | 2 | lactobacillus | Lactobacillus (Aged Brine) | 菌种 |
| 酿酒酵母 | `cultures/yeast` | 果皮表面（浆果、苹果） | 乙醇、19 | yeast | Saccharomyces Cerevisiae | 菌种 |
| 黑曲霉 | `cultures/aspergillus_niger` | 发霉的面包 / 谷物 | 3, 13，葡萄糖氧化酶 | aspergillus_niger | Aspergillus Niger | 菌种 |
| 红曲霉 | `cultures/monascus` | 曲盘蒸米，丛林类群系（H13） | 21 | monascus | Monascus | 菌种 |
| 谷氨酸棒杆菌 | `cultures/corynebacterium` | 土壤样本（M30 筛选） | 4 | corynebacterium | Corynebacterium Glutamicum | 菌种 |
| 三孢布拉霉（+） | `cultures/blakeslea_plus` | 腐烂植物样本（M30 筛选） | 20 | blakeslea_plus | Blakeslea Trispora (+) | 菌种 |
| 三孢布拉霉（−） | `cultures/blakeslea_minus` | 腐烂植物样本（M30 筛选） | 20 | blakeslea_minus | Blakeslea Trispora (−) | 菌种 |
| 氧化葡萄糖酸杆菌 | `cultures/gluconobacter` | 腐烂水果（M30 筛选） | 28 | gluconobacter | Gluconobacter Oxydans | 菌种 |
| 酮古龙酸菌 | `cultures/ketogulonicigenium` | 土壤样本（M30 诱变选育） | 28 | ketogulonicigenium | Ketogulonicigenium Vulgare | 菌种 |
| 巨大芽孢杆菌 | `cultures/bacillus_megaterium` | 土壤样本（M30 诱变选育） | 28 | bacillus_megaterium | Bacillus Megaterium | 菌种 |
| 麦芽 | `enzymes/malt` | 小麦发芽 | T1 糖化（β-淀粉酶来源） | malt | Malt | 酶 |
| β-淀粉酶 | `enzymes/beta_amylase` | 麦芽浸提液（402 `enzymes/malt_carbohydrase`）加硫酸铵盐析分级（M14 + M12；2026-11-12 由“大麦麦芽浸提纯化”改，见 18 §1.5） | 39（T3 糖化） | beta_amylase | β-Amylase | 酶 |
| α-淀粉酶 | `enzymes/alpha_amylase` | 枯草芽孢杆菌发酵 | T2+ 液化 | alpha_amylase | α-Amylase | 酶 |
| 葡萄糖淀粉酶 | `enzymes/glucoamylase` | 黑曲霉发酵 | T2+ 糖化 | glucoamylase | Glucoamylase | 酶 |
| 葡萄糖氧化酶 | `enzymes/glucose_oxidase` | 黑曲霉发酵 | 98（13 号酶法氧化） | glucose_oxidase | Glucose Oxidase | 酶 |
| 果胶酶 | `enzymes/pectinase` | 黑曲霉发酵 | 99 | pectinase | Pectinase | 酶 |
| 纤维素酶 | `enzymes/cellulase` | 李氏木霉发酵 | 100 | cellulase | Cellulase | 酶 |
| 木瓜蛋白酶 | `enzymes/papain` | 木瓜压榨、醇沉 | 101 | papain | Papain | 酶 |
| 菠萝蛋白酶 | `enzymes/bromelain` | 菠萝压榨、醇沉 | 101 | bromelain | Fruit Bromelain | 酶 |
| 脂肪酶 | `enzymes/lipase` | 米黑根毛霉发酵 | 102 | lipase | Triacylglycerol Lipase | 酶 |
| 乳糖酶 | `enzymes/lactase` | 乳克鲁维酵母发酵 | 103 | lactase | β-Galactosidase (Lactase) | 酶 |
| 谷氨酰胺转氨酶 | `enzymes/transglutaminase` | 茂原链轮丝菌发酵 | 104 | transglutaminase | Protein-Glutamine γ-Glutamyltransferase (Transglutaminase) | 酶 |
| 凝乳酶 | `enzymes/chymosin` | 小牛皱胃浸提；乳克鲁维酵母发酵 | 105 | chymosin | Chymosin | 酶 |
| 转化酶（蔗糖酶） | `enzymes/invertase` | 啤酒酵母自溶浸提 | 106 | invertase | β-Fructofuranosidase (Invertase) | 酶 |
| 丙酸杆菌 | `cultures/propionibacterium` | 奶酪（M30 筛选） | 32 | propionibacterium | Propionibacterium | 菌种 |
| 乳酸乳球菌 | `cultures/lactococcus` | 自然酸化的牛奶 | 34 | lactococcus | Lactococcus Lactis | 菌种 |
| 褐黄孢链霉菌 | `cultures/streptomyces_gilvosporeus` | 土壤样本（M30 筛选） | 35 | streptomyces_gilvosporeus | Streptomyces Gilvosporeus | 菌种 |
| 丛梗孢酵母 | `cultures/moniliella` | 蜂蜜、蜜脾 | 38 | moniliella | Moniliella | 菌种 |
| 野油菜黄单胞菌 | `cultures/xanthomonas` | 病叶（卷心菜） | 43 | xanthomonas | Xanthomonas Campestris | 菌种 |
| 少动鞘氨醇单胞菌 | `cultures/sphingomonas` | 水生植物（睡莲叶） | 44 | sphingomonas | Sphingomonas Paucimobilis | 菌种 |
| 阿舒囊霉 | `cultures/ashbya` | 腐烂植物样本 | 63 | ashbya | Ashbya Gossypii | 菌种 |
| 米根霉 | `cultures/rhizopus` | 曲盘蒸米，竹林（H13） | 57 | rhizopus | Rhizopus Oryzae | 菌种 |
| 米曲霉 | `cultures/aspergillus_oryzae` | 曲盘蒸米或蒸麦，其他群系（H13） | 93（单宁酶），酱油 | aspergillus_oryzae | Aspergillus Oryzae | 菌种 |
| 裂殖壶藻 | `cultures/schizochytrium` | 红树林沼泽海水 | 121 | schizochytrium | Schizochytrium | 菌种 |
| 丁酸梭菌 | `cultures/clostridium_butyricum` | 泥土（厌氧，M30 筛选） | 129 | clostridium_butyricum | Clostridium Butyricum | 菌种 |
| 解脂耶氏酵母 | `cultures/yarrowia` | 腐败油脂 | 130 | yarrowia | Yarrowia Lipolytica | 菌种 |
| 茂原链轮丝菌 | `cultures/streptoverticillium` | 土壤样本（M30 筛选） | 104 | streptoverticillium | Streptoverticillium Mobaraense | 菌种 |
| 乳克鲁维酵母 | `cultures/kluyveromyces` | 发酵乳 | 103, 105 | kluyveromyces | Kluyveromyces Lactis | 菌种 |
| 李氏木霉 | `cultures/trichoderma` | 腐木 | 100 | trichoderma | Trichoderma Reesei | 菌种 |
| 枯草芽孢杆菌 | `cultures/bacillus_subtilis` | 稻草、土壤 | 94 | bacillus_subtilis | Bacillus Subtilis | 菌种 |
| 地衣芽孢杆菌 | `cultures/bacillus_licheniformis` | 稻草、土壤 | 94 | bacillus_licheniformis | Bacillus Licheniformis | 菌种 |
| 嗜酸普鲁兰芽孢杆菌 | `cultures/bacillus_acidopullulyticus` | 土壤样本（M30 筛选） | 97 | bacillus_acidopullulyticus | Bacillus Acidopullulyticus | 菌种 |
| 米黑根毛霉 | `cultures/rhizomucor` | 堆肥 | 102 | rhizomucor | Rhizomucor Miehei | 菌种 |
| 普鲁兰酶 | `enzymes/pullulanase` | 对应菌种发酵（见 13） | 39 | pullulanase | Pullulanase | 酶 |
| 核酸酶 | `enzymes/nuclease` | 对应菌种发酵（见 13） | 73；391（表 C.3 品种） | nuclease | Nuclease | 酶 |
| 脱氨酶 | `enzymes/deaminase` | 对应菌种发酵（见 13） | 73；414（表 C.3 品种） | deaminase | Deaminase | 酶 |
| 富马酸酶 | `enzymes/fumarase` | 对应菌种发酵（见 13） | 56 | fumarase | Fumarase | 酶 |
| 环氧琥珀酸水解酶 | `enzymes/epoxysuccinate_hydrolase` | 对应菌种发酵（见 13） | 54 | epoxysuccinate_hydrolase | Epoxysuccinate Hydrolase | 酶 |
| 天冬氨酸酶 | `enzymes/aspartase` | 对应菌种发酵（见 14） | 85 | aspartase | Aspartase | 酶 |
| 腈水合酶 | `enzymes/nitrile_hydratase` | 对应菌种发酵（见 14） | 110 | nitrile_hydratase | Nitrile Hydratase | 酶 |
| 单宁酶 | `enzymes/tannase` | 对应菌种发酵（见 14） | 93；384（表 C.3 品种） | tannase | Tannase | 酶 |
| 嗜热菌蛋白酶 | `enzymes/thermolysin` | 对应菌种发酵（见 14） | 85 | thermolysin | Thermolysin | 酶 |
| 酶制剂成品（94–106；374–423 见下） | `enzymes/<名称>` | 菌种发酵或动植物提取（见 14） | 成品配方、上述工艺 | （I20） | （I20） | 酶 |
| 达库哈假单胞菌 | `cultures/pseudomonas_dacunhae` | 土壤样本（M30 筛选） | 179 | pseudomonas_dacunhae | Pseudomonas Dacunhae | 菌种 |
| 大肠杆菌 | `cultures/e_coli` | 生肉样本（M30 筛选，游戏设定） | 181、105 的宿主；表达载体 | e_coli | Escherichia Coli | 菌种 |
| 表达载体 | `cultures/expression_vector` | M30 用大肠杆菌制得（游戏抽象） | P94 每次消耗 1 个 | expression_vector | Expression Vector | 耗材 |
| 产半胱氨酸大肠杆菌 | `cultures/e_coli_cysteine` | M30 基因重组（P94）：大肠杆菌（游戏简化） | 181 | e_coli_cysteine | Cysteine-Producing E. Coli | 菌种 |
| 工程菌（凝乳酶） | `cultures/engineered_chymosin` | M30 基因重组（P94）：大肠杆菌 + 皱胃样本 | 105（表 C.3 第 39 项） | engineered_chymosin | Engineered Strain (Chymosin) | 菌种 |
| 出芽短梗霉 | `cultures/aureobasidium` | 树叶或果皮样本（M30 筛选） | 278 | aureobasidium | Aureobasidium Pullulans | 菌种 |
| 粪产碱杆菌黏胶变种 | `cultures/alcaligenes_myxogenes` | 土壤样本（M30 筛选） | 313 | alcaligenes_myxogenes | Alcaligenes Faecalis Myxogenes | 菌种 |
| 三赞鞘氨醇单胞菌 | `cultures/sphingomonas_sanxanigenens` | 土壤样本（M30 筛选） | 318 | sphingomonas_sanxanigenens | Sphingomonas Sanxanigenens | 菌种 |
| 荧光假单胞菌 | `cultures/pseudomonas_fluorescens` | 腐烂水果（M30 筛选） | 319、326 | pseudomonas_fluorescens | Pseudomonas Fluorescens | 菌种 |
| 白色链霉菌 | `cultures/streptomyces_albulus` | 土壤样本（M30 筛选） | 348、349 | streptomyces_albulus | Streptomyces Albulus | 菌种 |
| 红色精朊杆菌 | `cultures/protaminobacter` | 甜菜根样本（M30 筛选，游戏设定） | 350 | protaminobacter | Protaminobacter Rubrum | 菌种 |
| 节杆菌 | `cultures/arthrobacter` | 土壤样本（M30 筛选） | 354 | arthrobacter | Arthrobacter | 菌种 |
| 重组酿酒酵母（冰结构蛋白） | `cultures/yeast_isp` | M30 基因重组（P94）：酿酒酵母 + 鳕鱼 | 371 | yeast_isp | Recombinant Yeast (Ice Structuring Protein) | 菌种 |
| 斐济曲霉 | `cultures/aspergillus_fijiensis` | 土壤样本（M30 筛选）；中文名暂译 | 377 | aspergillus_fijiensis | Aspergillus Fijiensis | 菌种 |
| 解朊金黄杆菌 | `cultures/chryseobacterium` | 土壤样本（M30 筛选） | 383 | chryseobacterium | Chryseobacterium Proteolyticum | 菌种 |
| 解淀粉芽孢杆菌 | `cultures/bacillus_amyloliquefaciens` | 稻草、土壤 | 387 | bacillus_amyloliquefaciens | Bacillus Amyloliquefaciens | 菌种 |
| 橘青霉 | `cultures/penicillium_citrinum` | 发霉谷物（M30 筛选）；野生株产桔青霉素，不直接用于生产 | 391（诱变出发株） | penicillium_citrinum | Penicillium Citrinum | 菌种 |
| 橘青霉（不产毒株） | `cultures/penicillium_citrinum_nontoxic` | 橘青霉经 M30 诱变平板选育（2026-11-11，用户定：体现不产毒株） | 391 | penicillium_citrinum_nontoxic | Penicillium Citrinum (Non-Toxigenic) | 菌种 |
| 多形汉逊酵母 | `cultures/hansenula` | 腐烂水果（M30 筛选） | 394（宿主） | hansenula | Hansenula Polymorpha | 菌种 |
| 锈棕色链霉菌 | `cultures/streptomyces_rubiginosus` | 土壤样本（M30 筛选） | 406 | streptomyces_rubiginosus | Streptomyces Rubiginosus | 菌种 |
| 苍白空气芽孢杆菌 | `cultures/aeribacillus` | 堆肥 | 412 | aeribacillus | Aeribacillus Pallidus | 菌种 |
| 无定毛壳菌 | `cultures/chaetomium` | 腐木（M30 筛选） | 417 | chaetomium | Chaetomium Erraticum | 菌种 |
| 工程菌（D-阿洛酮糖-3-差向异构酶） | `cultures/engineered_psicose_epimerase` | M30 基因重组（P94）：枯草芽孢杆菌工程菌（供体瘤胃球菌） | 374 | engineered_psicose_epimerase | Engineered Strain (D-Psicose 3-Epimerase) | 菌种 |
| 工程菌（α-乙酰乳酸脱羧酶） | `cultures/engineered_aldc` | M30 基因重组（P94）：枯草芽孢杆菌工程菌（供体短小芽孢杆菌） | 376 | engineered_aldc | Engineered Strain (α-Acetolactate Decarboxylase) | 菌种 |
| 工程菌（甘油磷脂胆固醇酰基转移酶） | `cultures/engineered_gcat` | M30 基因重组（P94）：地衣芽孢杆菌工程菌（供体杀鲑气单胞菌） | 386 | engineered_gcat | Engineered Strain (Phospholipid Cholesterol Acyltransferase) | 菌种 |
| 工程菌（环糊精葡萄糖苷转移酶） | `cultures/engineered_cgtase` | M30 基因重组（P94）：地衣芽孢杆菌工程菌（供体高温厌氧杆菌） | 393 | engineered_cgtase | Engineered Strain (Cyclomaltodextrin Glucanotransferase) | 菌种 |
| 工程菌（己糖氧化酶） | `cultures/engineered_hexose_oxidase` | M30 基因重组（P94）：多形汉逊酵母工程菌（供体皱波角叉菜） | 394 | engineered_hexose_oxidase | Engineered Strain (Hexose Oxidase) | 菌种 |
| 工程菌（磷脂酶 A1） | `cultures/engineered_phospholipase_a1` | M30 基因重组（P94）：米曲霉工程菌（供体红聚颈腔菌） | 398 | engineered_phospholipase_a1 | Engineered Strain (Phospholipase A1) | 菌种 |
| 工程菌（磷脂酶 C） | `cultures/engineered_phospholipase_c` | M30 基因重组（P94）：地衣芽孢杆菌工程菌（供体苏云金芽孢杆菌） | 400 | engineered_phospholipase_c | Engineered Strain (Phospholipase C) | 菌种 |
| 工程菌（磷酸肌醇磷脂酶 C） | `cultures/engineered_pi_plc` | M30 基因重组（P94）：地衣芽孢杆菌工程菌（供体假单胞菌） | 401 | engineered_pi_plc | Engineered Strain (Phosphoinositide Phospholipase C) | 菌种 |
| 工程菌（麦芽糖淀粉酶） | `cultures/engineered_maltogenic_amylase` | M30 基因重组（P94）：枯草芽孢杆菌工程菌（供体嗜热脂肪地芽孢杆菌） | 403 | engineered_maltogenic_amylase | Engineered Strain (Maltogenic Amylase) | 菌种 |
| 工程菌（麦芽四糖水解酶） | `cultures/engineered_maltotetraohydrolase` | M30 基因重组（P94）：地衣芽孢杆菌工程菌（供体施氏假单胞菌） | 404 | engineered_maltotetraohydrolase | Engineered Strain (Maltotetraohydrolase) | 菌种 |
| 工程菌（α-葡萄糖苷酶） | `cultures/engineered_alpha_glucosidase` | M30 基因重组（P94）：李氏木霉工程菌（供体黑曲霉） | 408 | engineered_alpha_glucosidase | Engineered Strain (α-Glucosidase) | 菌种 |
| 工程菌（漆酶） | `cultures/engineered_laccase` | M30 基因重组（P94）：米曲霉工程菌（供体嗜热毁丝霉） | 409 | engineered_laccase | Engineered Strain (Laccase) | 菌种 |
| D-阿洛酮糖-3-差向异构酶 | `enzymes/psicose_epimerase` | 枯草芽孢杆菌工程菌（供体瘤胃球菌） | 374 | psicose_epimerase | D-Psicose 3-Epimerase | 酶 |
| α-半乳糖苷酶 | `enzymes/alpha_galactosidase` | 黑曲霉 | 375 | alpha_galactosidase | α-Galactosidase | 酶 |
| α-乙酰乳酸脱羧酶 | `enzymes/aldc` | 枯草芽孢杆菌工程菌（供体短小芽孢杆菌） | 376 | aldc | α-Acetolactate Decarboxylase (ALDC) | 酶 |
| β-果糖基转移酶 | `enzymes/beta_fructosyltransferase` | Aspergillus fijiensis | 377 | beta_fructosyltransferase | β-Fructofuranosidase (Fructosyltransferase) | 酶 |
| β-葡聚糖酶 | `enzymes/beta_glucanase` | 枯草芽孢杆菌 | 378 | beta_glucanase | β-Glucanase | 酶 |
| 阿拉伯呋喃糖苷酶 | `enzymes/arabinofuranosidase` | 黑曲霉 | 379 | arabinofuranosidase | Arabinofuranosidase | 酶 |
| 氨基肽酶 | `enzymes/aminopeptidase` | 米曲霉 | 380 | aminopeptidase | Aminopeptidase | 酶 |
| 半纤维素酶 | `enzymes/hemicellulase` | 黑曲霉 | 381 | hemicellulase | Hemicellulase | 酶 |
| 蛋白酶（微生物） | `enzymes/microbial_protease` | 枯草芽孢杆菌（中性蛋白酶） | 382 | microbial_protease | Protease (Microbial) | 酶 |
| 蛋白质谷氨酰胺酶 | `enzymes/protein_glutaminase` | 解朊金黄杆菌 | 383 | protein_glutaminase | Protein-Glutaminase | 酶 |
| 多聚半乳糖醛酸酶 | `enzymes/polygalacturonase` | 黑曲霉 | 385 | polygalacturonase | Polygalacturonase | 酶 |
| 甘油磷脂胆固醇酰基转移酶 | `enzymes/gcat` | 地衣芽孢杆菌工程菌（供体杀鲑气单胞菌） | 386 | gcat | Glycerophospholipid Cholesterol Acyltransferase (GCAT) | 酶 |
| 谷氨酰胺酶 | `enzymes/glutaminase` | 解淀粉芽孢杆菌 | 387 | glutaminase | Glutaminase | 酶 |
| 果胶裂解酶 | `enzymes/pectin_lyase` | 黑曲霉 | 388 | pectin_lyase | Pectin Lyase | 酶 |
| 果胶酯酶 | `enzymes/pectinesterase` | 黑曲霉 | 389 | pectinesterase | Pectinesterase | 酶 |
| 过氧化氢酶 | `enzymes/catalase` | A 牛肝；B 黑曲霉 | 390 | catalase | Catalase | 酶 |
| 海藻糖酶 | `enzymes/trehalase` | 李氏木霉 | 392 | trehalase | Trehalase | 酶 |
| 环糊精葡萄糖苷转移酶 | `enzymes/cgtase` | 地衣芽孢杆菌工程菌（供体高温厌氧杆菌） | 393 | cgtase | Cyclomaltodextrin Glucanotransferase (CGTase) | 酶 |
| 己糖氧化酶 | `enzymes/hexose_oxidase` | 多形汉逊酵母工程菌（供体皱波角叉菜） | 394 | hexose_oxidase | Hexose Oxidase | 酶 |
| 菊糖酶 | `enzymes/inulinase` | 黑曲霉 | 395 | inulinase | Inulinase | 酶 |
| 壳聚糖酶 | `enzymes/chitosanase` | 枯草芽孢杆菌 | 396 | chitosanase | Chitosanase | 酶 |
| 磷脂酶 | `enzymes/phospholipase` | 猪胰脏 | 397 | phospholipase | Phospholipase | 酶 |
| 磷脂酶 A1 | `enzymes/phospholipase_a1` | 米曲霉工程菌（供体红聚颈腔菌） | 398 | phospholipase_a1 | Phospholipase A1 | 酶 |
| 磷脂酶 A2 | `enzymes/phospholipase_a2` | 猪胰脏 | 399 | phospholipase_a2 | Phospholipase A2 | 酶 |
| 磷脂酶 C | `enzymes/phospholipase_c` | 地衣芽孢杆菌工程菌（供体苏云金芽孢杆菌） | 400 | phospholipase_c | Phospholipase C | 酶 |
| 磷酸肌醇磷脂酶 C | `enzymes/pi_plc` | 地衣芽孢杆菌工程菌（供体假单胞菌） | 401 | pi_plc | Phosphoinositide Phospholipase C (PI-PLC) | 酶 |
| 麦芽碳水化合物水解酶（α-、β-淀粉酶） | `enzymes/malt_carbohydrase` | 大麦麦芽 | 402 | malt_carbohydrase | Malt Carbohydrases (α- and β-Amylase) | 酶 |
| 麦芽糖淀粉酶 | `enzymes/maltogenic_amylase` | 枯草芽孢杆菌工程菌（供体嗜热脂肪地芽孢杆菌） | 403 | maltogenic_amylase | Maltogenic Amylase | 酶 |
| 麦芽四糖水解酶 | `enzymes/maltotetraohydrolase` | 地衣芽孢杆菌工程菌（供体施氏假单胞菌） | 404 | maltotetraohydrolase | Maltotetraohydrolase | 酶 |
| 木聚糖酶 | `enzymes/xylanase` | 李氏木霉 | 405 | xylanase | Xylanase | 酶 |
| 葡糖异构酶 | `enzymes/glucose_isomerase` | 锈棕色链霉菌 | 406 | glucose_isomerase | Glucose Isomerase | 酶 |
| β-葡萄糖苷酶 | `enzymes/beta_glucosidase` | 黑曲霉 | 407 | beta_glucosidase | β-Glucosidase | 酶 |
| α-葡萄糖苷酶 | `enzymes/alpha_glucosidase` | 李氏木霉工程菌（供体黑曲霉） | 408 | alpha_glucosidase | α-Glucosidase | 酶 |
| 漆酶 | `enzymes/laccase` | 米曲霉工程菌（供体嗜热毁丝霉） | 409 | laccase | Laccase | 酶 |
| 溶血磷脂酶 | `enzymes/lysophospholipase` | 黑曲霉 | 410 | lysophospholipase | Lysophospholipase | 酶 |
| 羧肽酶 | `enzymes/carboxypeptidase` | 黑曲霉 | 411 | carboxypeptidase | Carboxypeptidase | 酶 |
| 4-α-糖基转移酶 | `enzymes/alpha_glucanotransferase` | 苍白空气芽孢杆菌 | 412 | alpha_glucanotransferase | 4-α-Glucanotransferase | 酶 |
| 天门冬酰胺酶 | `enzymes/asparaginase` | 米曲霉 | 413 | asparaginase | Asparaginase | 酶 |
| 胃蛋白酶 | `enzymes/pepsin` | 猪胃黏膜 | 415 | pepsin | Pepsin | 酶 |
| 无花果蛋白酶 | `enzymes/ficin` | 无花果乳汁 | 416 | ficin | Ficin | 酶 |
| 右旋糖酐酶 | `enzymes/dextranase` | 无定毛壳菌 | 417 | dextranase | Dextranase | 酶 |
| 胰蛋白酶 | `enzymes/trypsin` | 猪胰脏 | 418 | trypsin | Trypsin | 酶 |
| 胰凝乳蛋白酶 | `enzymes/chymotrypsin` | 猪胰脏 | 419 | chymotrypsin | Chymotrypsin | 酶 |
| 蔗糖 1-果糖转移酶 | `enzymes/sucrose_fructosyltransferase` | 米曲霉 | 420 | sucrose_fructosyltransferase | Sucrose 1-Fructosyltransferase | 酶 |
| 酯酶 | `enzymes/esterase` | 米黑根毛霉 | 421 | esterase | Esterase | 酶 |
| 植酸酶 | `enzymes/phytase` | 黑曲霉 | 422 | phytase | Phytase | 酶 |
| 转葡糖苷酶 | `enzymes/transglucosidase` | 黑曲霉 | 423 | transglucosidase | Transglucosidase | 酶 |
| L-天冬氨酸 β-脱羧酶 | `enzymes/aspartate_decarboxylase` | 达库哈假单胞菌 M29 发酵，卡拉胶包埋固定化 | 179 | aspartate_decarboxylase | Aspartate β-Decarboxylase | 酶 |
| 大麦麦芽 | `enzymes/barley_malt` | 大麦发芽（大麦标签未核对） | 402 | barley_malt | Barley Malt | 酶 |
| 可可毛色二孢（菌种） | `cultures/lasiodiplodia` | 暂无来源（待定，见 17–19） | S0565 | lasiodiplodia | Lasiodiplodia | 菌种 |
| 柚苷酶（酶） | `enzymes/naringinase` | 黑曲霉（`cultures/aspergillus_niger`）M29 发酵，柑橘皮诱导（游戏简化，同 15 F 组通用酶流程） | N375、N399 | naringinase | Naringinase | 酶 |
| 产苏氨酸大肠杆菌（菌种） | `cultures/e_coli_threonine` | M30 基因重组（P94）：大肠杆菌（游戏简化，同 `cultures/e_coli_cysteine`） | N359 | e_coli_threonine | Threonine-Producing E. Coli | 菌种 |
| 丙酮丁醇梭菌 | `cultures/clostridium_acetobutylicum` | 土壤样本（M30 筛选） | 431 C | clostridium_acetobutylicum | Clostridium Acetobutylicum | 菌种 |
| 产赖氨酸谷氨酸棒杆菌 | `cultures/corynebacterium_lysine` | M30 诱变：谷氨酸棒杆菌（`cultures/corynebacterium`） | 534、535、S0344 | corynebacterium_lysine | Corynebacterium Lysine | 菌种 |
| 产酪氨酸大肠杆菌 | `cultures/e_coli_tyrosine` | M30 基因重组（P94）：大肠杆菌（游戏简化，同 `cultures/e_coli_cysteine`） | 546 | e_coli_tyrosine | Tyrosine-Producing E. Coli | 菌种 |
| 产色氨酸大肠杆菌 | `cultures/e_coli_tryptophan` | 同上 | 547 | e_coli_tryptophan | Tryptophan-Producing E. Coli | 菌种 |
| 高山被孢霉 | `cultures/mortierella` | 土壤样本（M30 筛选） | 541 | mortierella | Mortierella | 菌种 |
| L-肉碱转化菌（HK4 株） | `cultures/carnitine_hk4` | 土壤样本（M30 筛选后诱变） | 536 | carnitine_hk4 | Carnitine Hk4 | 菌种 |
| 尖孢镰刀菌 | `cultures/fusarium` | 腐烂植物样本（M30 筛选） | 479、480 | fusarium | Fusarium | 菌种 |
| D-泛解酸内酯水解酶 | `enzymes/d_lactonase` | 尖孢镰刀菌 M29 发酵 → 菌丝体海藻酸钙包埋（P96） | 479、480 | d_lactonase | D-Pantolactone Hydrolase | 酶 |
| 氨基酰化酶 | `enzymes/aminoacylase` | 米曲霉（`cultures/aspergillus_oryzae`）M29 发酵 → 固定化（P96） | 545、513 | aminoacylase | Aminoacylase | 酶 |
| 工程菌（2'-岩藻糖基乳糖） | `cultures/engineered_2fl` | M30 基因重组（P94）：大肠杆菌 + 环境样本（供体，游戏设定） | 561 | engineered_2fl | Engineered 2Fl | 菌种 |
| 工程菌（乳糖-N-新四糖） | `cultures/engineered_lnnt` | 同上 | 562 | engineered_lnnt | Engineered Lnnt | 菌种 |
| 工程菌（3'-唾液酸乳糖） | `cultures/engineered_3sl` | 同上 | 563 | engineered_3sl | Engineered 3Sl | 菌种 |
| 食用菌菌种 | `cultures/edible_fungus` | M30 平板分离：食用菌（`materials/mushroom`）组织作样本（游戏设定；食用菌需加入 `culture_bench/samples`） | 512 | edible_fungus | Edible Fungus | 菌种 |
| 产亮氨酸谷氨酸棒杆菌 | `cultures/corynebacterium_leucine` | M30 诱变：谷氨酸棒杆菌（`cultures/corynebacterium`） | S0326 | corynebacterium_leucine | Corynebacterium Leucine | 菌种 |
| 产脯氨酸谷氨酸棒杆菌 | `cultures/corynebacterium_proline` | M30 诱变：谷氨酸棒杆菌（`cultures/corynebacterium`） | S0339 | corynebacterium_proline | Corynebacterium Proline | 菌种 |
| 产精氨酸谷氨酸棒杆菌 | `cultures/corynebacterium_arginine` | M30 诱变：谷氨酸棒杆菌（`cultures/corynebacterium`） | S0343 | corynebacterium_arginine | Corynebacterium Arginine | 菌种 |
| 产谷氨酰胺谷氨酸棒杆菌 | `cultures/corynebacterium_glutamine` | M30 诱变：谷氨酸棒杆菌（`cultures/corynebacterium`） | S0884 | corynebacterium_glutamine | Corynebacterium Glutamine | 菌种 |
| 产 L-乳酸乳杆菌 | `cultures/lactobacillus_l_lactate` | M30 诱变：乳酸菌（`cultures/lactobacillus`） | 495 B | lactobacillus_l_lactate | Lactobacillus l Lactate | 菌种 |
| 产核糖枯草芽孢杆菌 | `cultures/bacillus_subtilis_ribose` | M30 诱变：枯草芽孢杆菌（`cultures/bacillus_subtilis`）；与产乙偶姻株共用一块概率平板 | S0077 | bacillus_subtilis_ribose | Bacillus Subtilis Ribose | 菌种 |
| 产乙偶姻枯草芽孢杆菌 | `cultures/bacillus_subtilis_acetoin` | M30 诱变：枯草芽孢杆菌（`cultures/bacillus_subtilis`）；与产核糖株共用一块概率平板 | S0204 A | bacillus_subtilis_acetoin | Bacillus Subtilis Acetoin | 菌种 |

> 英文名按国际生化学会（IUBMB）推荐名核对（2026-11-12，I20，11 问题 43）：第三批的十种酶里，脂肪酶、乳糖酶、谷氨酰胺转氨酶、菠萝蛋白酶、转化酶改为推荐名（Triacylglycerol Lipase、β-Galactosidase、Protein-Glutamine γ-Glutamyltransferase、Fruit Bromelain、β-Fructofuranosidase），常用名放括号里；葡萄糖氧化酶、纤维素酶、木瓜蛋白酶、凝乳酶与推荐名相同；果胶酶是多种酶的混合制剂，没有单一推荐名，不改。

### 催化剂与助剂（机器内部计数消耗）

催化剂放在机器的催化剂槽，消耗记在机器内部计数器里，到量后整件消耗（“到量”由配方的 `runs` 写明，省略为 64 次，2026-11-04 定，见 ../machines/01 第 3.1 节）；物品本身不带耐久、不改 NBT，可堆叠，AE2 中只占一个类型（见 10 第 6 节）。贵金属与过渡金属（铑、铂、钒、钴、钯、钨）由其他模组提供，配方按标签匹配（2026-11-04 定，11 问题 4）；全部催化剂物品由本模组注册。

| 催化剂 | 配方引用标签 | 组成 | 用于 | 来源 | ID | 英文名（待审） | 分类 | 显示化学式 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 镍催化剂 | `catalysts/nickel` | 雷尼镍或 Ni / 硅藻土 | 26，28，29 | 镍（热力）+ 硅藻土 | nickel_catalyst（见 09） | — | 催化剂 | 见 09 |
| 铁催化剂 | `catalysts/iron` | Fe3O4 + K2O、Al2O3 助剂 | 合成氨 | 铁锭 | iron_catalyst | Iron Catalyst | 催化剂 | — |
| 乙酸钴 | `catalysts/cobalt` | Co(C2H3O2)2·4H2O | 23、332 与香料的液相氧化 | 他模组的钴（按标签匹配，11 问题 4）+ 冰乙酸，M14 通空气溶解 → M15 结晶（2026-11-12 由笼统的“钴盐”改为具体的盐，11 问题 43） | cobalt_acetate（见 09） | — | 催化剂 | 见 09 |
| 铑碘催化剂 | `catalysts/carbonylation` | Rh + 碘化物 | 1（羰基化） | 他模组的铑，按标签匹配（11 问题 4） | rhodium_iodide_catalyst | Rhodium Iodide Catalyst | 催化剂 | — |
| 铂网 | `catalysts/platinum_gauze` | Pt–Rh 网 | 24（氨氧化） | 他模组的铂、铑，按标签匹配（11 问题 4） | platinum_gauze | Platinum Gauze | 催化剂 | — |
| 五氧化二钒 | `catalysts/vanadium` | V2O5 | 接触法硫酸 | 他模组的钒，按标签匹配（11 问题 4） | vanadium_pentoxide（见 09） | — | 催化剂 | 见 09 |
| 金 / 钯 | `catalysts/noble_metal` | Au、Pd | 13（催化氧化，可选） | 金锭；钯由他模组提供，按标签匹配（11 问题 4） | noble_metal_catalyst | Noble Metal Catalyst | 催化剂 | — |
| 硅藻土 | `catalysts/diatomite` | SiO2 为主 | 助滤剂、催化剂载体 | 本模组矿物、`#forge:dusts/diatomite` | diatomite | Diatomite | 催化剂 | — |
| 活性炭 / 骨炭 | `catalysts/activated_carbon` | C | 脱色 | 木炭 / 骨头活化 | activated_carbon（见 09） | — | 催化剂 | 见 09 |
| 离子交换树脂 | `catalysts/ion_exchange_resin` | 聚苯乙烯磺酸 / 季铵树脂 | 离子交换 | 聚苯乙烯（441）+ 硫酸，M14（耐腐蚀内衬）磺化（P59）→ M12 水洗 → M17 干燥；季铵型的氯甲基化与胺化并入同一步（2026-11-12 由“苯与乙烯聚合”改为苯乙烯一路，二乙烯苯交联剂省略作游戏简化，11 问题 43）。获得树脂之前，转游离酸改用钙盐酸解（P39：加硫酸，滤除硫酸钙） | ion_exchange_resin | Ion Exchange Resin | 催化剂 | — |
| 大孔吸附树脂 | `catalysts/macroporous_resin` | 苯乙烯–二乙烯苯共聚物 | 36, 62 | 苏合香烯（苯乙烯，S0679）+ 甲苯（致孔剂）+ 水，M14 聚合模式悬浮聚合（P77）→ M22 汽提回收甲苯 → M12 过滤 → M17 干燥、筛分（2026-11-12 同离子交换树脂改为苯乙烯一路；致孔剂保留，二乙烯苯交联剂省略作游戏简化，11 问题 43） | macroporous_resin | Macroporous Adsorption Resin | 催化剂 | — |
| 银催化剂 | `catalysts/silver` | Ag / 氧化铝 | 环氧乙烷（72, 120） | 银锭 | silver_catalyst | Silver Catalyst | 催化剂 | — |
| 钒磷氧催化剂 | `catalysts/vpo` | (VO)2P2O7 | 丁烷氧化制马来酸酐（56, 57） | 五氧化二钒 + 磷酸 | vpo_catalyst | Vanadium Phosphorus Oxide Catalyst | 催化剂 | `(VO)2P2O7` |
| 钨酸钠 | `catalysts/tungstate` | Na2WO4 | 马来酸酐环氧化（54） | 他模组的钨，按标签匹配（11 问题 4） | sodium_tungstate（见 09） | — | 催化剂 | 见 09 |
| 有机锡 | `catalysts/organotin` | 二丁基氧化锡 | 蔗糖-6-乙酸酯（87） | 锡锭 | dibutyltin_oxide（见 09） | — | 催化剂 | 见 09 |
| 钯碳 | `catalysts/palladium` | Pd / C | 还原胺化（88）、乙酸乙烯酯（134） | `catalysts/noble_metal` + 活性炭 | palladium_on_carbon | Palladium on Carbon | 催化剂 | — |
| 三氯化铝 | `catalysts/aluminium_chloride` | AlCl3 | 异丁烯低温聚合（135） | 氧化铝 + 氯气 + 焦炭 | aluminium_chloride（见 09） | — | 催化剂 | 见 09 |
| 氧化铝 | `catalysts/alumina` | Al2O3 | 甲醇氢氯化（304、314、163 的氯甲烷） | 09 的 `alumina`（化学品）同时进催化剂标签 | alumina（见 09） | — | 见 09 | 见 09 |
| 铜催化剂 | `catalysts/copper` | Cu（含锌、锡助剂） | 甲基氯硅烷直接法（163） | 铜锭 | copper_catalyst（见 09） | — | 催化剂 | 见 09 |
| 钼酸铋催化剂 | `catalysts/bismuth_molybdate` | Bi2(MoO4)3 | 丙烯氧化制丙烯酸（310） | 钼、铋（他模组，按标签匹配） | bismuth_molybdate_catalyst（见 09） | — | 催化剂 | 见 09 |
| 氯化锌 | `catalysts/zinc_chloride` | ZnCl2 | 己酸与间苯二酚酰化（325）；第五批 506 号作营养强化剂，物料改为化学品 K1，催化剂槽照样接受（2026-11-11） | 锌 + 盐酸 | zinc_chloride（见 09） | — | 催化剂 | 见 09 |
| 固定化蔗糖异构酶菌体 | `catalysts/immobilized_sucrose_isomerase` | 海藻酸钙包埋的灭活菌体 | 异麦芽酮糖（350） | 红色精朊杆菌发酵 → 海藻酸钙包埋（P96） | immobilized_sucrose_isomerase（见 09） | — | 催化剂 | 见 09 |
| 烯烃复分解催化剂 | `catalysts/metathesis` | 钌卡宾络合物（格拉布催化剂）或 Re2O7/Al2O3（加四甲基锡助剂）；工业乙烯解另用钨、钼卡宾络合物 | S0334、`diethyl_octadecenedioate` | 暂无来源（催化剂，按次数消耗，待定） | metathesis | Metathesis | 催化剂 | — |
| 凹凸棒黏土 | `catalysts/attapulgite` | 坡缕石 (Mg,Al)2Si4O10(OH)·4H2O 为主 | 吸附、助滤（GB 2760 表 C.2） | 第五批 438 号 | attapulgite_clay（见 09） | — | 催化剂 | 见 09 |
| 不溶性聚乙烯聚吡咯烷酮 | `catalysts/pvpp` | (C6H9NO)n | 吸附、助滤（GB 2760 表 C.2） | 第五批 439 号 | pvpp（见 09） | — | 催化剂 | 见 09 |
| 活性白土 | `catalysts/activated_clay` | 酸活化蒙脱石，SiO2 为主，含 Al2O3 | 吸附、助滤（GB 2760 表 C.2） | 第五批 440 号 | activated_clay（见 09） | — | 催化剂 | 见 09 |
| 聚苯乙烯 | `catalysts/polystyrene` | (C8H8)n | 吸附、助滤（GB 2760 表 C.2） | 第五批 441 号 | polystyrene（见 09） | — | 催化剂 | 见 09 |
| 膨润土 | `catalysts/bentonite` | 蒙脱石 (Na,Ca)0.33(Al,Mg)2Si4O10(OH)2·nH2O 为主 | 吸附、助滤（GB 2760 表 C.2） | 第五批 443 号 | bentonite（见 09） | — | 催化剂 | 见 09 |
| 珍珠岩 | `catalysts/perlite` | 火山玻璃，SiO2 约 70%–75%、Al2O3 约 12%–15% | 吸附、助滤（GB 2760 表 C.2） | 第五批 444 号 | perlite（见 09） | — | 催化剂 | 见 09 |
| 钯-离子交换树脂 | `catalysts/palladium_resin` | 钯负载于磺酸型离子交换树脂（双功能：酸催化缩合与加氢） | S1132（一步缩合加氢） | 贵金属催化剂 + 离子交换树脂，搅拌反应罐加热浸渍（I31 实装拟定，2026-11-12：高压反应釜只有一个催化剂槽，按用户定的“先合成或混合”做成一种催化剂） | palladium_resin | Palladium on Ion Exchange Resin | 催化剂 | — |