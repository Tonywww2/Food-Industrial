# 标签名

> 适用范围：本模组生成的物品标签与流体标签在 EMI、JEI 等界面里显示的名称（2026-11-25 用户定：为 EMI 标签补充译名）。键见[文本规范第 4 节](text-standard.md)。

## 1 规则

1. **键**：`tag.<类型>.<命名空间>.<路径>`，类型为 `item` 或 `fluid`，路径里的 `/` 换成 `.`（EMI 与 1.21 起的加载器约定相同），如 `tag.item.foodindustrial.materials.wheat_flour`。
2. **取名顺序**：先查第 2 节的表；表里没有的，标签里本模组的条目（物品、流体，或引用的本模组标签）只有一个名称时，取这个名称（流体的流动态不算）。`foodindustrial:materials/wheat_flour` 只有面粉，名为“面粉”；`foodindustrial:catalysts/immobilized_lipase` 只引用化学品标签“固定化脂肪酶”，同名。
3. **范围**：本模组命名空间的物品标签与流体标签都要有名称，数据生成时缺一个就报错，新加的组标签要先写进第 2 节；本模组生成的 `c`、`forge` 标签只在第 2 条取得到名称时写；`minecraft` 等他人的标签不写。方块与实体标签不在界面上显示，不写。
4. **写法**：照文本规范第 1、2 节，名称与所含物品的名称一致；组标签写所含物品的统称，原版物品照游戏里的名称。

## 2 表

第 2 条取不到名称的标签（组标签、只接原版或他模组物品的原料标签）。“说明”只供查阅，不进游戏。

| 类型 | 标签（`foodindustrial:` 省略） | 中文 | English | 说明 |
| --- | --- | --- | --- | --- |
| 物品 | `chemicals` | 化学品 | Chemicals | 分类为化学品的固体物料 |
| 流体 | `chemicals` | 化学品 | Chemicals | 分类为化学品的液体、气体、溶液 |
| 物品 | `containers` | 容器与包装 | Containers and Packaging | 同创造模式标签页 |
| 物品 | `molds` | 模具 | Molds | 同创造模式标签页 |
| 物品 | `flavorings` | 香精 | Flavoring Blends | 11 种香精物品 |
| 物品 | `flavor_notes/all` | 香料 | Flavorings | 辅香格；同创造模式标签页 |
| 流体 | `flavor_notes/all` | 香料 | Flavorings | 辅香格 |
| 物品 | `flavor_notes/alcoholic` | 酒香香料 | Alcoholic Flavorings | 主香格 |
| 物品 | `flavor_notes/cooling` | 清凉香料 | Cooling Flavorings | 主香格 |
| 物品 | `flavor_notes/dairy` | 乳香香料 | Dairy Flavorings | 主香格 |
| 物品 | `flavor_notes/floral` | 花香香料 | Floral Flavorings | 主香格 |
| 物品 | `flavor_notes/fruity` | 果香香料 | Fruity Flavorings | 主香格 |
| 物品 | `flavor_notes/green` | 蔬菜与青香香料 | Green Flavorings | 主香格 |
| 物品 | `flavor_notes/nutty_roasted` | 坚果与焙烤香料 | Nutty and Roasted Flavorings | 主香格 |
| 物品 | `flavor_notes/savory` | 肉味与咸味香料 | Savory Flavorings | 主香格 |
| 物品 | `flavor_notes/smoky` | 烟熏香料 | Smoky Flavorings | 主香格 |
| 物品 | `flavor_notes/spicy` | 辛香香料 | Spicy Flavorings | 主香格 |
| 物品 | `flavor_notes/sweet` | 甜香香料 | Sweet Flavorings | 主香格 |
| 流体 | `flavor_notes/alcoholic` | 酒香香料 | Alcoholic Flavorings | 主香格 |
| 流体 | `flavor_notes/cooling` | 清凉香料 | Cooling Flavorings | 主香格 |
| 流体 | `flavor_notes/dairy` | 乳香香料 | Dairy Flavorings | 主香格 |
| 流体 | `flavor_notes/floral` | 花香香料 | Floral Flavorings | 主香格 |
| 流体 | `flavor_notes/fruity` | 果香香料 | Fruity Flavorings | 主香格 |
| 流体 | `flavor_notes/green` | 蔬菜与青香香料 | Green Flavorings | 主香格 |
| 流体 | `flavor_notes/nutty_roasted` | 坚果与焙烤香料 | Nutty and Roasted Flavorings | 主香格 |
| 流体 | `flavor_notes/savory` | 肉味与咸味香料 | Savory Flavorings | 主香格 |
| 流体 | `flavor_notes/smoky` | 烟熏香料 | Smoky Flavorings | 主香格 |
| 流体 | `flavor_notes/spicy` | 辛香香料 | Spicy Flavorings | 主香格 |
| 流体 | `flavor_notes/sweet` | 甜香香料 | Sweet Flavorings | 主香格 |
| 物品 | `flavor_carriers/emulsifiers` | 乳化香精载体 | Emulsion Flavoring Carriers | 阿拉伯胶、辛烯基琥珀酸淀粉钠 |
| 物品 | `flavor_carriers/powders` | 粉末香精载体 | Powder Flavoring Carriers | 麦芽糊精、淀粉、盐、葡萄糖、蔗糖 |
| 流体 | `flavor_carriers/oil_soluble` | 油溶性香精载体 | Oil-Soluble Flavoring Carriers | 植物油、辛，癸酸甘油酯、三乙酸甘油酯 |
| 流体 | `flavor_carriers/water_soluble` | 水溶性香精载体 | Water-Soluble Flavoring Carriers | 丙二醇、乙醇 |
| 物品 | `food_categories/dairy` | 乳及乳制品 | Dairy Products | GB 2760 附录 E 大类 |
| 物品 | `food_categories/fats_oils` | 脂肪、油和乳化脂肪制品 | Fats, Oils and Fat Emulsions | GB 2760 附录 E 大类 |
| 物品 | `food_categories/frozen_desserts` | 冷冻饮品 | Frozen Desserts | GB 2760 附录 E 大类 |
| 物品 | `food_categories/fruits_vegetables` | 水果、蔬菜、豆类、食用菌、藻类、坚果及籽类 | Fruits, Vegetables, Legumes, Fungi, Algae, Nuts and Seeds | GB 2760 附录 E 大类 |
| 物品 | `food_categories/cocoa_confectionery` | 可可制品、巧克力和巧克力制品以及糖果 | Cocoa Products, Chocolate and Confectionery | GB 2760 附录 E 大类 |
| 物品 | `food_categories/cereals` | 粮食和粮食制品 | Cereals and Cereal Products | GB 2760 附录 E 大类 |
| 物品 | `food_categories/bakery` | 焙烤食品 | Bakery Products | GB 2760 附录 E 大类 |
| 物品 | `food_categories/meat` | 肉及肉制品 | Meat and Meat Products | GB 2760 附录 E 大类 |
| 物品 | `food_categories/aquatic` | 水产及其制品 | Aquatic Products | GB 2760 附录 E 大类 |
| 物品 | `food_categories/eggs` | 蛋及蛋制品 | Eggs and Egg Products | GB 2760 附录 E 大类 |
| 物品 | `food_categories/sweeteners` | 甜味料 | Sweeteners | GB 2760 附录 E 大类 |
| 物品 | `food_categories/condiments` | 调味品 | Condiments | GB 2760 附录 E 大类 |
| 物品 | `food_categories/special_dietary` | 特殊膳食用食品 | Foods for Special Dietary Uses | GB 2760 附录 E 大类 |
| 物品 | `food_categories/beverages` | 饮料类 | Beverages | GB 2760 附录 E 大类 |
| 物品 | `food_categories/alcoholic` | 酒类 | Alcoholic Beverages | GB 2760 附录 E 大类 |
| 物品 | `food_categories/other` | 其他类 | Other Foods | GB 2760 附录 E 大类 |
| 物品 | `foods/biscuits` | 饼干 | Biscuits | 桃酥、黄油曲奇、苏打饼干 |
| 物品 | `foods/cake_base` | 蛋糕坯 | Cake Bases | 海绵蛋糕、戚风蛋糕 |
| 物品 | `foods/cooking_oil` | 烹调油 | Cooking Oils | 食用植物油、花生油、芝麻油 |
| 流体 | `foods/cooking_oil` | 烹调油 | Cooking Oils | 同上 |
| 物品 | `foods/soy_sauce` | 酱油 | Soy Sauces | 生抽、老抽、鲜味酱油 |
| 流体 | `foods/soy_sauce` | 酱油 | Soy Sauces | 同上 |
| 物品 | `foods/vinegar` | 食醋 | Vinegars | 米醋、陈醋、苹果醋、白醋 |
| 流体 | `foods/vinegar` | 食醋 | Vinegars | 同上 |
| 物品 | `animal_fats` | 动物脂肪 | Animal Fats | 猪油、牛油、动物脂 |
| 物品 | `cooking_fats` | 炼制油脂 | Rendered Fats | 猪油、牛油 |
| 物品 | `food_emulsifiers` | 食品乳化剂 | Food Emulsifiers | 单硬脂酸甘油酯、卵磷脂 |
| 物品 | `stock_meats` | 熬汤用肉 | Stock Meats | 熬肉汤的生肉 |
| 物品 | `salt_position` | 盐位 | Salt Slot | 盐、亚硝酸钠、亚硝酸钾 |
| 物品 | `lamian_alkali` | 拉面蓬灰 | Lamian Alkali | 草木灰、碳酸钾 |
| 物品 | `mustard_seeds` | 芥菜籽 | Mustard Seeds | 黄芥籽、黑芥籽 |
| 物品 | `fumigatable` | 可熏蒸的食品 | Fumigatable Foods | H09 熏蒸箱 |
| 物品 | `mortar_stones` | 研钵用石 | Mortar Stones | 石头、方解石 |
| 物品 | `rubber_logs` | 橡胶木 | Rubber Wood | 橡胶木原木、木头及去皮的 |
| 物品 | `coolants/ice` | 冷却用冰 | Cooling Ice | 冰、浮冰、蓝冰 |
| 物品 | `culture_bench/samples` | 样本或菌种 | Samples or Cultures | M30 第 1 槽 |
| 物品 | `culture_bench/media` | 培养基 | Culture Media | M30 第 2 槽 |
| 物品 | `culture_bench/donors` | 供体样本 | Donor Samples | M30 第 3 槽 |
| 物品 | `culture_bench/vectors` | 表达载体 | Expression Vectors | M30 第 4 槽 |
| 流体 | `edible_oils` | 浸提用食用油 | Edible Oils for Extraction | 08 §A 食用油（浸提用） |
| 流体 | `molasses` | 糖蜜 | Molasses | 甘蔗糖蜜、甜菜糖蜜 |
| 流体 | `kansui` | 枧水 | Kansui | 碱液、钾碱液 |
| 流体 | `honey` | 蜂蜜 | Honey | 他模组的蜂蜜流体 |
| 流体 | `milk` | 牛奶 | Milk | 加载器与他模组的牛奶流体 |
| 流体 | `water` | 水 | Water | 原版水 |
| 流体 | `heat_sources` | 热源流体 | Heat Source Fluids | 熔岩 |
| 物品 | `samples/soil` | 土壤样本 | Soil Samples | M30 平板分离 |
| 物品 | `samples/decaying_plant` | 腐烂植物样本 | Decaying Plant Samples | M30 平板分离 |
| 物品 | `samples/fruit` | 水果样本 | Fruit Samples | M30 平板分离 |
| 物品 | `samples/raw_meat` | 生肉样本 | Raw Meat Samples | M30 平板分离 |
| 物品 | `samples/beetroot` | 甜菜根样本 | Beetroot Samples | M30 平板分离 |
| 物品 | `samples/brine` | 卤水样本 | Brine Samples | M30 平板分离 |
| 物品 | `samples/cocoa` | 可可样本 | Cocoa Samples | M30 平板分离 |
| 物品 | `samples/compost` | 堆肥样本 | Compost Samples | M30 平板分离 |
| 物品 | `samples/dairy` | 乳品样本 | Dairy Samples | M30 平板分离 |
| 物品 | `samples/honey` | 蜂蜜样本 | Honey Samples | M30 平板分离 |
| 物品 | `samples/leaf` | 树叶样本 | Leaf Samples | M30 平板分离 |
| 物品 | `samples/marine` | 海洋样本 | Marine Samples | M30 平板分离 |
| 物品 | `samples/mushroom` | 蘑菇样本 | Mushroom Samples | M30 平板分离 |
| 物品 | `samples/rotten_fat` | 腐败脂肪样本 | Rotten Fat Samples | M30 平板分离 |
| 物品 | `samples/rotten_wood` | 朽木样本 | Rotten Wood Samples | M30 平板分离 |
| 物品 | `samples/straw` | 稻草样本 | Straw Samples | M30 平板分离 |
| 物品 | `samples/aquatic_plant` | 水生植物样本 | Aquatic Plant Samples | M30 平板分离 |
| 物品 | `materials/amomum` | 砂仁 | Amomum |  |
| 物品 | `materials/apple` | 苹果 | Apple |  |
| 物品 | `materials/bamboo` | 竹子 | Bamboo |  |
| 物品 | `materials/beetroot` | 甜菜根 | Beetroot |  |
| 物品 | `materials/berries` | 浆果 | Berries |  |
| 物品 | `materials/bismuth` | 铋 | Bismuth |  |
| 物品 | `materials/bone` | 骨头 | Bone |  |
| 物品 | `materials/bone_meal` | 骨粉 | Bone Meal |  |
| 物品 | `materials/cabbage` | 卷心菜 | Cabbage |  |
| 物品 | `materials/carrot` | 胡萝卜 | Carrot |  |
| 物品 | `materials/cinnamon` | 桂皮 | Cassia Bark |  |
| 物品 | `materials/citrus` | 柑橘 | Citrus |  |
| 物品 | `materials/clay` | 黏土球 | Clay Ball |  |
| 物品 | `materials/coal` | 煤炭与木炭 | Coal and Charcoal |  |
| 物品 | `materials/cobalt` | 钴 | Cobalt |  |
| 物品 | `materials/cocoa_beans` | 可可豆 | Cocoa Beans |  |
| 物品 | `materials/coconut` | 椰子 | Coconut |  |
| 物品 | `materials/coke` | 焦炭 | Coke |  |
| 物品 | `materials/copper` | 铜 | Copper |  |
| 物品 | `materials/corn` | 玉米 | Corn |  |
| 物品 | `materials/cowhide` | 牛皮 | Cowhide |  |
| 物品 | `materials/cucumber` | 黄瓜 | Cucumber |  |
| 物品 | `materials/dried_kelp` | 干海带 | Dried Kelp |  |
| 物品 | `materials/duck` | 生鸭肉 | Raw Duck |  |
| 物品 | `materials/duck_egg` | 鸭蛋 | Duck Egg |  |
| 物品 | `materials/egg` | 鸡蛋 | Egg |  |
| 物品 | `materials/feather` | 羽毛 | Feather |  |
| 物品 | `materials/glass_bottle` | 玻璃瓶 | Glass Bottle |  |
| 物品 | `materials/gold` | 金 | Gold |  |
| 物品 | `materials/grape` | 葡萄 | Grape |  |
| 物品 | `materials/grass` | 草与蕨 | Grass and Ferns |  |
| 物品 | `materials/hemicellulose` | 桦木原木 | Birch Log |  |
| 物品 | `materials/herbs` | 草本 | Herbs |  |
| 物品 | `materials/hide` | 皮革与兔子皮 | Leather and Rabbit Hide |  |
| 物品 | `materials/honey` | 蜂蜜 | Honey |  |
| 物品 | `materials/honeycomb` | 蜜脾 | Honeycomb |  |
| 物品 | `materials/horn` | 山羊角 | Goat Horn |  |
| 物品 | `materials/ice` | 冰 | Ice |  |
| 物品 | `materials/iron` | 铁 | Iron |  |
| 物品 | `materials/kelp` | 海带 | Kelp |  |
| 物品 | `materials/laver` | 紫菜 | Laver |  |
| 物品 | `materials/lead` | 铅 | Lead |  |
| 物品 | `materials/leafy_greens` | 叶菜 | Leafy Greens |  |
| 物品 | `materials/leaves` | 树叶 | Leaves |  |
| 物品 | `materials/lemon` | 柠檬 | Lemon |  |
| 物品 | `materials/limestone` | 方解石与滴水石块 | Calcite and Dripstone |  |
| 物品 | `materials/logs` | 原木 | Logs |  |
| 物品 | `materials/milk` | 牛奶 | Milk |  |
| 物品 | `materials/molybdenum` | 钼 | Molybdenum |  |
| 物品 | `materials/nickel` | 镍 | Nickel |  |
| 物品 | `materials/nuts` | 坚果 | Nuts |  |
| 物品 | `materials/oats` | 燕麦 | Oats |  |
| 物品 | `materials/onion` | 洋葱 | Onion |  |
| 物品 | `materials/orange` | 橙 | Orange |  |
| 物品 | `materials/oyster` | 蚝 | Oyster |  |
| 物品 | `materials/palladium` | 钯 | Palladium |  |
| 物品 | `materials/papaya` | 木瓜 | Papaya |  |
| 物品 | `materials/peanut` | 花生 | Peanut |  |
| 物品 | `materials/pineapple` | 菠萝 | Pineapple |  |
| 物品 | `materials/platinum` | 铂 | Platinum |  |
| 物品 | `materials/potato` | 马铃薯 | Potato |  |
| 物品 | `materials/quartz` | 下界石英 | Nether Quartz |  |
| 物品 | `materials/radish` | 萝卜 | Radish |  |
| 物品 | `materials/raw_beef` | 生牛肉 | Raw Beef |  |
| 物品 | `materials/raw_chicken` | 生鸡肉 | Raw Chicken |  |
| 物品 | `materials/raw_fish` | 生鱼 | Raw Fish |  |
| 物品 | `materials/raw_mutton` | 生羊肉 | Raw Mutton |  |
| 物品 | `materials/raw_pork` | 生猪肉 | Raw Pork |  |
| 物品 | `materials/raw_rabbit` | 生兔肉 | Raw Rabbit |  |
| 物品 | `materials/rhodium` | 铑 | Rhodium |  |
| 物品 | `materials/rubber_log` | 橡胶木原木 | Rubber Log |  |
| 物品 | `materials/saltbush` | 枯萎的灌木 | Dead Bush |  |
| 物品 | `materials/sand` | 沙子 | Sand |  |
| 物品 | `materials/seagrass` | 海草 | Seagrass |  |
| 物品 | `materials/sesame` | 芝麻 | Sesame |  |
| 物品 | `materials/shrimp` | 虾 | Shrimp |  |
| 物品 | `materials/silver` | 银 | Silver |  |
| 物品 | `materials/snow` | 雪 | Snow |  |
| 物品 | `materials/soybean` | 大豆 | Soybean |  |
| 物品 | `materials/straw` | 稻草 | Straw |  |
| 物品 | `materials/sugar_cane` | 甘蔗 | Sugar Cane |  |
| 物品 | `materials/tea` | 茶叶 | Tea Leaves |  |
| 物品 | `materials/tin` | 锡 | Tin |  |
| 物品 | `materials/tomato` | 番茄 | Tomato |  |
| 物品 | `materials/tungsten` | 钨 | Tungsten |  |
| 物品 | `materials/vanadium` | 钒 | Vanadium |  |
| 物品 | `materials/vegetables` | 蔬菜 | Vegetables |  |
| 物品 | `materials/walnut` | 核桃 | Walnut |  |
| 物品 | `materials/wheat` | 小麦 | Wheat |  |
| 物品 | `materials/wool` | 羊毛 | Wool |  |
| 物品 | `materials/zinc` | 锌 | Zinc |  |
