# 08 原料清单与标签

第一批共用到约 **90 种一级原料**：原版物品 32 类、他模组原料 26 类、本模组新增 35 种（含菌种、酶与催化剂）。第二、三批另增约 80 种（“用于”一栏标 31–139 编号的行，来源见 [13](13-batch-2.md)、[14](14-batch-3.md)）。**所有配方输入一律按标签匹配**：配方只引用本模组命名空间下的标签，标签再聚合原版物品、本模组物品和各模组的 `forge:` 标签；只有配方输出写具体物品。

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

以后扩展到药物、日化品等领域时，在本表并列新增产品类前缀，不改动已有前缀（machines/02 第 8 节）。

规则：

1. **导入**：每个本模组标签包含对应的原版物品（如有）、本模组物品（如有）和各模组常用 `forge:` 标签，全部写成 `required: false`，某个模组没装也不会报错。
2. **导出**：本模组物品同时加入通用 `forge:` 标签（如碳酸钠加入 `#forge:dusts/soda_ash`，硫酸流体加入 `#forge:sulfuric_acid`），让其他模组和 GTCEu 的配方也能用。范围（2026-11-04 定）：分类为化学品的固体加入 `dusts/<ID>`，液体、气体、溶液加入 `<ID>`；1.21 起气体另加入 NeoForge 的 `c:gaseous`，桶加入 `c:buckets/<ID>`（1.20.1 没有这两类标签）。菌种、酶、催化剂、原料不导出。
3. **化学品与命名**：`chemicals/` 物品标签和化学品流体标签的名称 = 09 表中的建议 ID（农产流体如 `wine`、`whey`、`vegetable_oil` 用简短名）；固体导入 `#forge:dusts/<id>`、`#forge:gems/<id>`，流体导入 `#forge:<id>`。`materials/`（农产、矿石）、`catalysts/`、`additives/` 用简短名，可与 09 ID 不同（如 `catalysts/nickel` 对应 09 `nickel_catalyst`）。矿石与同名化学品分开注册：矿石物品 ID 加 `_ore` 后缀（`gypsum_ore` 进 `materials/gypsum`，化学品 `gypsum` 进 `chemicals/gypsum`）；`#forge:ores/<名>` 只导入 `materials/`，`#forge:dusts/<名>` 只导入 `chemicals/`，没有对应化学品的矿物粉（如磷灰石粉）才归 `materials/`。09 中分类为催化剂、又在 §C 催化剂表有行的化学品（镍催化剂、活性炭、五氧化二钒、钨酸钠、二丁基氧化锡、三氯化铝）同时进 `chemicals/<ID>` 和 §C 写的 `catalysts/<名>`（2026-11-04 定）。
4. **整合包可改**：整合包作者用数据包或 KubeJS 往本模组标签里追加物品即可，不需要改配方。
5. **版本迁移**：1.20.1 用 `forge:` 前缀；迁到 1.21 时整体换成 `c:` 前缀，配方不用动。
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

> 下表“配方引用标签”一栏省略了 `foodindustrial:` 前缀；流体标签注明“（流体）”。每行一个标签。“默认内容”中反引号内的条目由 I06 数据生成写入（`tools/materials/export_materials.py` 导出），全部 `required: false`；“本模组……”表示本模组同名物料按主表自动加入（规则 3）。“1.21 起”一栏同时用于 1.21.1 与 26.1.2；直接写的物品、流体 ID 只在该节点存在时写入（如农夫乐事的物品不进 26.1.2，热力的流体只进 1.20.1）。
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
| 枯萎的灌木 | `materials/saltbush` | `minecraft:dead_bush` | `minecraft:dead_bush` | 7（蓬灰） | 无机盐 |
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
| 辣椒 | `materials/chili` | `#forge:crops/chilipepper` | `#c:crops/chilipepper` | 60 |
| 姜黄 | `materials/turmeric` | — | — | 61 |
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

> 通用机械的气体属于 Mekanism Chemical，不进 Forge 流体标签；只有它额外注册的流体形态才能被匹配。

## C 本模组新增原料

| 原料 | 配方引用标签 | 类型 | 获取 | 用于 | 主要组成 | ID | 英文名（待审） | 分类 | 颜色 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 海水 | `seawater`（流体） | 流体 | 在海洋、海滩群系用桶取水 | 11，氯化钠 | NaCl、MgCl2、MgSO4、KCl | seawater（见 09） | — | 见 09 | — |
| 苦卤 | `bittern`（流体） | 流体 | 晒盐池 / 蒸发结晶器副产 | 11, 7 | MgCl2、MgSO4、KCl | bittern（见 09） | — | 见 09 | — |
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
| 麒麟菜 | `materials/red_algae_carrageenan` | 海洋植物 | 温暖海洋生成，可养殖 | 42 | κ-卡拉胶 | eucheuma | Eucheuma | 原料 | — |
| 栀子果 | `materials/gardenia` | 灌木 | 丛林、竹林野生，成熟后右键采果（同甜浆果丛） | 62 | 栀子苷、藏花素 | gardenia_fruit | Gardenia Fruit | 原料 | — |
| 迷迭香 | `materials/rosemary` | 作物 | 草甸、风袭丘陵野生 | 67 | 鼠尾草酸、迷迭香酸 | rosemary | Rosemary | 原料 | — |
| 蓼蓝 | `materials/indigo_plant` | 作物 | 河边、沼泽野生 | 84 | 靛苷 | indigo_plant | Indigo Plant | 原料 | — |
| 橡胶木原木 | `materials/rubber_log` | 树木 | 丛林生成；导入热力的 `#forge:rubberwood_logs`、GTCEu 的 `#gtceu:rubber_logs`（原写 `#forge:logs/rubber`，核对不存在） | 136 | 顺式聚异戊二烯 | rubber_log | Rubber Log | 原料 | — |
| 硫矿石（后备） | 掉落硫，进 `chemicals/sulfur` | 矿石 | 未装热力（Thermal Foundation）时生成 | 25 | S | sulfur_ore | Sulfur Ore | 原料 | — |
| 稻谷（后备） | `materials/rice` | 作物 | 未装农夫乐事时在沼泽、河边野生 | 21，淀粉 | 淀粉 | rice | Paddy Rice | 原料 | — |
| 树皮 | `materials/tannin_source` | 物品 | 后备来源：未装农夫乐事时用斧给原木去皮掉落 | 93 | 单宁 | tree_bark | Tree Bark | 原料 | — |
| 松脂 | `resin`（流体） | 流体 | 松木原木 H15 割取（07）；导入 `thermal:resin` | 137 | 萜类树脂 | resin（见 09） | — | 见 09 | — |
| 乳胶 | `latex`（流体） | 流体 | 橡胶树 H15 割取（07）；导入 `thermal:latex` | 136 | 顺式聚异戊二烯 | latex | Latex | 原料 | #F0F0E8 |
| 葡萄酒 | `wine`（流体） | 流体 | 葡萄发酵；导入 `#forge:wine` | 54, 55 | 乙醇、酒石酸氢钾 | wine | Wine | 原料 | #5A1028 |
| 酒石 | `chemicals/potassium_bitartrate` | 物品 | H14 冷置析酒石、葡萄酒桶底 | 54, 55 | C4H5KO6 | potassium_bitartrate（见 09） | — | 见 09 | — |
| 大豆毛油 | `crude_soybean_oil`（流体） | 流体 | 大豆压榨或浸出 | 65, 69 | 甘油三酯、磷脂、生育酚 | crude_soybean_oil | Crude Soybean Oil | 原料 | #D8A030 |
| 番茄皮渣 | `materials/tomato_pomace` | 物品 | 番茄榨汁副产 | 59 | 番茄红素 | tomato_pomace | Tomato Pomace | 原料 | — |
| 啤酒酵母 | `materials/spent_yeast` | 物品 | 啤酒酿造副产 | 73 | 核糖核酸 | spent_yeast | Spent Brewer's Yeast | 原料 | — |
| 乳清 | `whey`（流体） | 流体 | 奶酪制作副产 | 32, 34 | 乳糖、乳清蛋白 | whey | Whey | 原料 | #E8E8B0 |
| 鱼肝 | `materials/fish_liver` | 物品 | 处理鳕鱼的副产 | 107 | 鱼肝油（维生素 A、D） | fish_liver | Fish Liver | 原料 | — |

> I07 的方块与作物（2026-11-04 定）：作物的收获物本身可种（同胡萝卜），野外有野生植株；栀子按甜浆果丛、香荚兰按可可果的形态；橡胶木做完整木材套组（原木、木头、去皮原木、去皮木头、木板、楼梯、台阶、栅栏、栅栏门、门、活板门、压力板、按钮、树叶、树苗；告示牌、悬挂式告示牌、船列入后续）；松脂先按原料注册，纯度口径按纯度模型 v2（07 #137：K0，按松香计含量）由 I28 落实。后备来源只做硫矿石、磷灰石矿石（未装热力时）和稻谷、树皮（未装农夫乐事时）；镍、锡、银、铅不做后备，依赖其他模组。后备物品在各节点都注册，只在缺对应模组时生成或掉落，配置文件 `foodindustrial-common.toml` 的 `[fallbacks]` 可强制启用。

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
| 石膏矿石 | `materials/gypsum` | `foodindustrial:gypsum_ore`、`#forge:ores/gypsum` | `foodindustrial:gypsum_ore`、`#c:ores/gypsum` |
| 明矾石矿石 | `materials/alunite` | `foodindustrial:alunite_ore`、`#forge:ores/alunite` | `foodindustrial:alunite_ore`、`#c:ores/alunite` |
| 天然碱矿 | `materials/trona` | `foodindustrial:trona_ore` | `foodindustrial:trona_ore` |
| 岩盐 | `materials/rock_salt` | `foodindustrial:rock_salt`、`#forge:ores/salt` | `foodindustrial:rock_salt`、`#c:ores/salt` |
| 菱镁矿石 | `materials/magnesite` | `foodindustrial:magnesite_ore`、`#forge:ores/magnesite` | `foodindustrial:magnesite_ore`、`#c:ores/magnesite` |
| 钠硝石矿 | `materials/nitratine` | `foodindustrial:nitratine` | `foodindustrial:nitratine` |
| 硝土 | `materials/nitre_soil` | `foodindustrial:nitre_soil` | `foodindustrial:nitre_soil` |
| 磷灰石 | `materials/apatite` | `foodindustrial:apatite_ore`、`#forge:ores/apatite` | `foodindustrial:apatite_ore`、`#c:ores/apatite` |
| 橡胶木原木 | `materials/rubber_log` | `foodindustrial:rubber_log`、`foodindustrial:rubber_wood`、`#forge:rubberwood_logs`、`#gtceu:rubber_logs` | `foodindustrial:rubber_log`、`foodindustrial:rubber_wood`、`#gtceu:rubber_logs` |

> 矿石的 `ores/<名>` 按规则 3 的通用写法写入；其中只有 `ores/apatite`（热力）在核对的模组中有提供者。

#### 世界生成（I07，2026-11-04 拟定，数值待审）

矿脉只替换石头（`#minecraft:stone_ore_replaceables`），不做深板岩变种，高度都在 0 以上。“每区块”是每个区块尝试的次数，“1/N”表示每 N 个区块一次。

| 方块 | 群系 | 方式 | 高度 | 每区块 | 规模 |
| --- | --- | --- | --- | --- | --- |
| 石膏矿石 | 沙漠、恶地 | 矿脉 | 0–80 | 8 | 12 |
| 明矾石矿石 | 山地（`#minecraft:is_mountain`）、风袭丘陵 | 矿脉 | 32–160 | 6 | 9 |
| 岩盐 | 主世界 | 矿脉 | 0–48 | 3 | 24 |
| 菱镁矿石 | 主世界 | 矿脉 | 0–64 | 4 | 9 |
| 天然碱矿 | 沙漠、恶地 | 地表矿脉，替换沙、红沙、陶瓦 | 地表 | 1/6 | 20 |
| 钠硝石矿 | 沙漠、恶地 | 地表矿脉，替换沙、红沙、陶瓦 | 地表 | 1/10 | 16 |
| 磷灰石矿石（后备） | 主世界 | 矿脉 | 0–64 | 4 | 9 |
| 硫矿石（后备） | 主世界 | 矿脉 | 0–32 | 5 | 7 |
| 野生甜菊 | 平原、向日葵平原、热带草原 | 草地上成簇 | 地表 | 1/24 | 每簇 32 次，水平 ±7，垂直 ±3 |
| 野生薄荷 | 平原、向日葵平原、草甸 | 同上 | 地表 | 1/24 | 同上 |
| 野生迷迭香 | 草甸、风袭丘陵 | 同上 | 地表 | 1/24 | 同上 |
| 野生蓼蓝 | 河流、沼泽 | 同上 | 地表 | 1/16 | 同上 |
| 野生稻谷（后备） | 沼泽、红树林沼泽、河流 | 同上 | 地表 | 1/16 | 同上 |
| 栀子丛 | 丛林、稀疏丛林、竹林 | 同上，已成熟 | 地表 | 1/32 | 同上 |
| 香荚兰 | 丛林、稀疏丛林、竹林 | 长在丛林原木侧面，可替换树干上的藤蔓 | 树冠下地面以上 1–8 格 | 每个朝向 16 次 | — |
| 石花菜 | 暖水海洋、温水海洋、温水深海 | 海底 | 海底 | 8 | — |
| 麒麟菜 | 暖水海洋 | 海底 | 海底 | 6 | — |
| 橡胶树 | 丛林、稀疏丛林 | 树（直干 5–7 格，球形树冠半径 2） | 地表 | 1/3 | — |

### 菌种与酶（物品，可在 M30 菌种工作台繁殖）

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
| β-淀粉酶 | `enzymes/beta_amylase` | 麦芽浸提纯化（2026-11-04 定单设，来源待审） | 39（T3 糖化） | beta_amylase | β-Amylase | 酶 |
| α-淀粉酶 | `enzymes/alpha_amylase` | 枯草芽孢杆菌发酵 | T2+ 液化 | alpha_amylase | α-Amylase | 酶 |
| 葡萄糖淀粉酶 | `enzymes/glucoamylase` | 黑曲霉发酵 | T2+ 糖化 | glucoamylase | Glucoamylase | 酶 |
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
| 核酸酶 | `enzymes/nuclease` | 对应菌种发酵（见 13） | 73 | nuclease | Nuclease | 酶 |
| 脱氨酶 | `enzymes/deaminase` | 对应菌种发酵（见 13） | 73 | deaminase | Deaminase | 酶 |
| 富马酸酶 | `enzymes/fumarase` | 对应菌种发酵（见 13） | 56 | fumarase | Fumarase | 酶 |
| 环氧琥珀酸水解酶 | `enzymes/epoxysuccinate_hydrolase` | 对应菌种发酵（见 13） | 54 | epoxysuccinate_hydrolase | Epoxysuccinate Hydrolase | 酶 |
| 天冬氨酸酶 | `enzymes/aspartase` | 对应菌种发酵（见 14） | 85 | aspartase | Aspartase | 酶 |
| 腈水合酶 | `enzymes/nitrile_hydratase` | 对应菌种发酵（见 14） | 110 | nitrile_hydratase | Nitrile Hydratase | 酶 |
| 单宁酶 | `enzymes/tannase` | 对应菌种发酵（见 14） | 93 | tannase | Tannase | 酶 |
| 嗜热菌蛋白酶 | `enzymes/thermolysin` | 对应菌种发酵（见 14） | 85 | thermolysin | Thermolysin | 酶 |
| 酶制剂成品（94–106） | `enzymes/<名称>` | 菌种发酵或动植物提取（见 14） | 成品配方、上述工艺 | （I20） | （I20） | 酶 |

### 催化剂与助剂（机器内部计数消耗）

催化剂放在机器的催化剂槽，消耗记在机器内部计数器里，到量后整件消耗（“到量”由配方的 `runs` 写明，省略为 64 次，2026-11-04 定，见 ../machines/01 第 3.1 节）；物品本身不带耐久、不改 NBT，可堆叠，AE2 中只占一个类型（见 10 第 6 节）。贵金属与过渡金属（铑、铂、钒、钴、钯、钨）由其他模组提供，配方按标签匹配（2026-11-04 定，11 问题 4）；全部催化剂物品由本模组注册。

| 催化剂 | 配方引用标签 | 组成 | 用于 | 来源 | ID | 英文名（待审） | 分类 | 显示化学式 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 镍催化剂 | `catalysts/nickel` | 雷尼镍或 Ni / 硅藻土 | 26，28，29 | 镍（热力）+ 硅藻土 | nickel_catalyst（见 09） | — | 催化剂 | 见 09 |
| 铁催化剂 | `catalysts/iron` | Fe3O4 + K2O、Al2O3 助剂 | 合成氨 | 铁锭 | iron_catalyst | Iron Catalyst | 催化剂 | — |
| 钴盐 | `catalysts/cobalt` | 醋酸钴 | 23 | 他模组的钴，按标签匹配（11 问题 4） | cobalt_salt | Cobalt Salt | 催化剂 | — |
| 铑碘催化剂 | `catalysts/carbonylation` | Rh + 碘化物 | 1（羰基化） | 他模组的铑，按标签匹配（11 问题 4） | rhodium_iodide_catalyst | Rhodium Iodide Catalyst | 催化剂 | — |
| 铂网 | `catalysts/platinum_gauze` | Pt–Rh 网 | 24（氨氧化） | 他模组的铂、铑，按标签匹配（11 问题 4） | platinum_gauze | Platinum Gauze | 催化剂 | — |
| 五氧化二钒 | `catalysts/vanadium` | V2O5 | 接触法硫酸 | 他模组的钒，按标签匹配（11 问题 4） | vanadium_pentoxide（见 09） | — | 催化剂 | 见 09 |
| 金 / 钯 | `catalysts/noble_metal` | Au、Pd | 13（催化氧化，可选） | 金锭；钯由他模组提供，按标签匹配（11 问题 4） | noble_metal_catalyst | Noble Metal Catalyst | 催化剂 | — |
| 硅藻土 | `catalysts/diatomite` | SiO2 为主 | 助滤剂、催化剂载体 | 本模组矿物、`#forge:dusts/diatomite` | diatomite | Diatomite | 催化剂 | — |
| 活性炭 / 骨炭 | `catalysts/activated_carbon` | C | 脱色 | 木炭 / 骨头活化 | activated_carbon（见 09） | — | 催化剂 | 见 09 |
| 离子交换树脂 | `catalysts/ion_exchange_resin` | 聚苯乙烯磺酸 / 季铵树脂 | 离子交换 | 第二批石化线；获得树脂之前，转游离酸改用钙盐酸解（P39：加硫酸，滤除硫酸钙） | ion_exchange_resin | Ion Exchange Resin | 催化剂 | — |
| 大孔吸附树脂 | `catalysts/macroporous_resin` | 苯乙烯–二乙烯苯共聚物 | 36, 62 | 同离子交换树脂 | macroporous_resin | Macroporous Adsorption Resin | 催化剂 | — |
| 银催化剂 | `catalysts/silver` | Ag / 氧化铝 | 环氧乙烷（72, 120） | 银锭 | silver_catalyst | Silver Catalyst | 催化剂 | — |
| 钒磷氧催化剂 | `catalysts/vpo` | (VO)2P2O7 | 丁烷氧化制马来酸酐（56, 57） | 五氧化二钒 + 磷酸 | vpo_catalyst | Vanadium Phosphorus Oxide Catalyst | 催化剂 | `(VO)2P2O7` |
| 钨酸钠 | `catalysts/tungstate` | Na2WO4 | 马来酸酐环氧化（54） | 他模组的钨，按标签匹配（11 问题 4） | sodium_tungstate（见 09） | — | 催化剂 | 见 09 |
| 有机锡 | `catalysts/organotin` | 二丁基氧化锡 | 蔗糖-6-乙酸酯（87） | 锡锭 | dibutyltin_oxide（见 09） | — | 催化剂 | 见 09 |
| 钯碳 | `catalysts/palladium` | Pd / C | 还原胺化（88）、乙酸乙烯酯（134） | `catalysts/noble_metal` + 活性炭 | palladium_on_carbon | Palladium on Carbon | 催化剂 | — |
| 三氯化铝 | `catalysts/aluminium_chloride` | AlCl3 | 异丁烯低温聚合（135） | 氧化铝 + 氯气 + 焦炭 | aluminium_chloride（见 09） | — | 催化剂 | 见 09 |