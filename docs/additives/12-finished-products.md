# 12 成品清单

本章列出首批规划的 **331 种**成品名称，按 **GB 2760-2024 附录 E 食品分类系统**的 16 个大类组织，便于将来直接对照标准查“这类食品允许用哪些添加剂”。成品效果暂不设计；“添加剂”一栏列用到的添加剂编号（1–30 见 03，31–78 见 13，79–139 见 14），用于检查每种添加剂都有下游需求；目前主要标注第一批，第二、三批的标注随成品配方设计补齐。

- 制作方式在配方设计阶段再定：多数家常成品允许在农夫乐事的厨锅、砧板、煎锅中制作；工业化成品（罐头、方便面、饮料、膨化食品等）用本模组机器。
- 原料按 08 的标签约定引用，他模组作物（潘马斯等）只走标签。
- 建议 ID 统一 `foodindustrial:<id>`，小写下划线；每种成品同时加入标签 `foodindustrial:foods/<id>`，被其他成品或农夫乐事配方引用时一律用标签。

## 01 乳及乳制品

| 成品 | 建议 ID | 主要原料 | 添加剂 |
| --- | --- | --- | --- |
| 酸奶 | yogurt | 牛奶、乳酸菌 | 2 |
| 风味酸奶 | flavored_yogurt | 酸奶、果酱 | 16, 19 |
| 果粒酸奶 | fruit_yogurt | 酸奶、浆果 | 16 |
| 酸奶油 | sour_cream | 稀奶油、乳酸菌 | 2 |
| 马苏里拉奶酪 | mozzarella | 牛奶、凝乳酶 | 3 |
| 切达奶酪 | cheddar_cheese | 牛奶、盐 | 20 |
| 奶油奶酪 | cream_cheese | 牛奶、稀奶油 | 2 |
| 再制奶酪片 | processed_cheese_slice | 奶酪 | 10 |
| 炼乳 | condensed_milk | 牛奶、糖 | — |
| 奶粉 | milk_powder | 牛奶 | — |
| 调制乳粉 | formula_milk_powder | 奶粉 | 28 |
| 黄油 | butter | 稀奶油 | 20 |
| 淡奶油 | whipping_cream | 牛奶 | 27 |
| 奶片 | milk_tablet | 奶粉、糖 | — |
| 双皮奶 | double_skin_milk | 牛奶、蛋清、糖 | — |
| 姜撞奶 | ginger_milk_curd | 牛奶、姜（标签） | — |
| 奶豆腐 | milk_tofu | 牛奶 | 2 |

## 02 脂肪、油和乳化脂肪制品

| 成品 | 建议 ID | 主要原料 | 添加剂 |
| --- | --- | --- | --- |
| 猪油 | lard | 生猪排 | — |
| 牛油 | tallow | 生牛肉 | — |
| 食用植物油 | edible_oil | 种子、玉米（标签） | — |
| 花生油 | peanut_oil | 花生（标签） | — |
| 芝麻油 | sesame_oil | 芝麻（标签） | — |
| 人造黄油 | margarine | 植物油 | 20, 27 |
| 起酥油 | shortening | 植物油、动物脂 | 27 |
| 植脂末 | non_dairy_creamer | 植物油、葡萄糖浆 | 10, 27 |

## 03 冷冻饮品

| 成品 | 建议 ID | 主要原料 | 添加剂 |
| --- | --- | --- | --- |
| 香草冰淇淋 | vanilla_ice_cream | 牛奶、稀奶油、糖 | 15, 27, 30 |
| 巧克力冰淇淋 | chocolate_ice_cream | 牛奶、可可 | 27 |
| 薄荷冰淇淋 | mint_ice_cream | 牛奶、薄荷 | 22, 29 |
| 浆果冰淇淋 | berry_ice_cream | 牛奶、浆果 | 19, 27 |
| 抹茶冰淇淋 | matcha_ice_cream | 牛奶、茶叶（标签） | 27 |
| 雪糕 | ice_cream_bar | 冰淇淋、巧克力 | 15, 27 |
| 冰棍 | popsicle | 糖水、果汁 | 3 |
| 绿豆冰棍 | mung_bean_popsicle | 绿豆（标签）、糖 | — |
| 果味冰沙 | sorbet | 果汁、糖 | 3, 16 |
| 刨冰 | shaved_ice | 冰、糖浆 | 19 |
| 红豆冰 | red_bean_ice | 红豆（标签）、冰 | — |

## 04 水果、蔬菜、豆类、食用菌、藻类、坚果及籽类

### 水果制品

| 成品 | 建议 ID | 主要原料 | 添加剂 |
| --- | --- | --- | --- |
| 苹果酱 | apple_jam | 苹果、糖 | 3, 16 |
| 浆果果酱 | berry_jam | 甜浆果、糖 | 3, 16 |
| 发光浆果果酱 | glow_berry_jam | 发光浆果、糖 | 3, 16 |
| 柑橘果酱 | marmalade | 柑橘（标签）、糖 | 16 |
| 低糖果酱 | low_sugar_jam | 浆果、山梨糖醇 | 16, 26 |
| 苹果干 | dried_apple | 苹果 | 25 |
| 浆果干 | dried_berries | 甜浆果 | 25 |
| 蜜饯苹果片 | candied_apple_slices | 苹果、糖 | 25 |
| 糖渍橘皮 | candied_citrus_peel | 柑橘皮（标签）、糖 | 25 |
| 话梅 | preserved_plum | 梅子（标签）、盐、糖 | 18 |
| 糖水苹果罐头 | canned_apple | 苹果、糖 | 3, 28 |
| 浆果罐头 | canned_berries | 甜浆果、糖 | 3 |

### 蔬菜制品

| 成品 | 建议 ID | 主要原料 | 添加剂 |
| --- | --- | --- | --- |
| 四川泡菜 | sichuan_pickles | 卷心菜、萝卜（标签）、盐 | 2 |
| 酸菜 | suancai | 卷心菜、盐 | 2 |
| 榨菜 | zhacai | 芥菜（标签）、盐 | 23 |
| 雪菜 | xuecai | 芥菜（标签）、盐 | — |
| 腌黄瓜 | pickled_cucumber | 黄瓜（标签）、醋 | 1 |
| 醋渍甜菜根 | pickled_beetroot | 甜菜根、醋 | 1 |
| 糖醋胡萝卜 | sweet_sour_carrot | 胡萝卜、醋、糖 | 1 |
| 胡萝卜泥 | carrot_puree | 胡萝卜 | 20, 28 |
| 什锦蔬菜罐头 | canned_vegetables | 胡萝卜、马铃薯、豆类 | 1 |
| 脱水蔬菜 | dehydrated_vegetables | 胡萝卜、卷心菜 | 25 |

### 豆制品

| 成品 | 建议 ID | 主要原料 | 添加剂 |
| --- | --- | --- | --- |
| 卤水豆腐（北豆腐） | brine_tofu | 大豆（标签） | 11 |
| 石膏豆腐（南豆腐） | gypsum_tofu | 大豆 | 12 |
| 内酯豆腐 | lactone_tofu | 大豆 | 13 |
| 豆花 | douhua | 大豆 | 12, 13 |
| 冻豆腐 | frozen_tofu | 豆腐 | — |
| 豆干 | dried_tofu | 豆腐 | 11 |
| 五香豆干 | spiced_dried_tofu | 豆干、酱油 | 18 |
| 卤豆干 | braised_dried_tofu | 豆干、卤汁 | 18, 23 |
| 熏豆干 | smoked_tofu | 豆干 | — |
| 腐竹 | yuba | 豆浆 | 25 |
| 油豆皮 | tofu_skin | 豆浆 | — |
| 白腐乳 | white_fermented_tofu | 豆腐、曲 | — |
| 红腐乳 | red_fermented_tofu | 豆腐、红曲 | 21 |
| 臭豆腐 | stinky_tofu | 豆腐、臭卤 | — |
| 豆豉 | douchi | 黑豆（标签）、盐 | — |
| 纳豆 | natto | 大豆、纳豆菌 | — |
| 大豆素肉 | textured_soy_protein | 大豆蛋白 | 18, 21 |

### 食用菌、藻类、坚果

| 成品 | 建议 ID | 主要原料 | 添加剂 |
| --- | --- | --- | --- |
| 凉拌海带丝 | kelp_salad | 海带、醋 | 1 |
| 海带结 | kelp_knots | 海带 | — |
| 琼脂凉粉 | agar_cold_jelly | 琼脂、醋 | 1, 14 |
| 调味海苔 | seasoned_seaweed_snack | 紫菜（标签）、油、盐 | 4 |
| 蘑菇罐头 | canned_mushrooms | 棕色蘑菇 | 3, 28 |
| 腌蘑菇 | pickled_mushrooms | 蘑菇、醋 | 1 |
| 炒南瓜子 | roasted_pumpkin_seeds | 南瓜种子、盐 | — |
| 椒盐坚果 | salted_nuts | 坚果（标签）、盐 | 4 |
| 琥珀核桃 | amber_walnuts | 核桃（标签）、糖 | 18 |
| 花生酱 | peanut_butter | 花生（标签） | 27 |
| 芝麻酱 | sesame_paste | 芝麻（标签） | — |

## 05 可可制品、巧克力和巧克力制品以及糖果

| 成品 | 建议 ID | 主要原料 | 添加剂 |
| --- | --- | --- | --- |
| 黑巧克力 | dark_chocolate | 可可豆、糖 | 30 |
| 牛奶巧克力 | milk_chocolate | 可可豆、奶粉 | 30 |
| 白巧克力 | white_chocolate | 可可脂、奶粉 | 30 |
| 巧克力豆 | chocolate_chips | 巧克力 | — |
| 糖衣巧克力豆 | coated_chocolate_beans | 巧克力、糖 | 19, 20, 22 |
| 酒心巧克力 | liqueur_chocolate | 巧克力、利口酒 | — |
| 硬糖 | hard_candy | 糖、葡萄糖浆 | 3, 19 |
| 薄荷糖 | mint_candy | 糖 | 29 |
| 润喉糖 | throat_lozenge | 糖、薄荷 | 29 |
| 明胶软糖 | gummy_candy | 糖、明胶 | 3, 17, 19 |
| 果汁软糖 | pectin_jelly_candy | 果汁、糖 | 3, 16 |
| 棉花糖 | marshmallow | 糖、明胶 | 17, 30 |
| 牛轧糖 | nougat | 糖、蛋清、坚果 | 17 |
| 太妃糖 | toffee | 糖、黄油 | 18 |
| 焦糖块 | caramel_candy | 糖、牛奶 | 18 |
| 奶糖 | milk_candy | 奶粉、糖 | 27 |
| 口香糖 | chewing_gum | 胶基、糖 | 29 |
| 无糖口香糖 | sugar_free_gum | 胶基、山梨糖醇 | 26, 29 |
| 棒棒糖 | lollipop | 糖 | 3, 19, 20 |
| 冰糖葫芦 | tanghulu | 山楂（标签）/ 苹果、糖 | — |
| 龙须酥 | dragon_beard_candy | 麦芽糖浆、面粉 | — |
| 芝麻糖 | sesame_brittle | 芝麻、麦芽糖浆 | — |
| 花生糖 | peanut_brittle | 花生、糖 | 6 |
| 跳跳糖 | popping_candy | 糖、加压 CO2 | 3 |

## 06 粮食和粮食制品

| 成品 | 建议 ID | 主要原料 | 添加剂 |
| --- | --- | --- | --- |
| 自发粉 | self_rising_flour | 面粉 | 6, 10 |
| 挂面 | dried_noodles | 面粉、盐 | — |
| 兰州拉面 | lanzhou_pulled_noodles | 面粉、蓬灰 | 7 |
| 碱水面 | alkaline_noodles | 面粉 | 5, 7 |
| 鸡蛋面 | egg_noodles | 面粉、鸡蛋 | — |
| 方便面 | instant_noodles | 面粉、棕榈油（标签） | 5, 10, 27 |
| 刀削面 | knife_cut_noodles | 面粉 | — |
| 饺子 | dumplings | 面粉、肉、蔬菜 | — |
| 馄饨 | wontons | 面粉、肉 | — |
| 烧麦 | shumai | 面粉、糯米（标签） | — |
| 速冻饺子 | frozen_dumplings | 饺子 | 27 |
| 汤圆 | tangyuan | 糯米粉、芝麻 | — |
| 速冻汤圆 | frozen_tangyuan | 汤圆 | 27 |
| 年糕 | niangao | 大米 | — |
| 米粉 | rice_vermicelli | 大米 | — |
| 河粉 | hor_fun | 大米 | — |
| 粉丝 | glass_noodles | 绿豆 / 马铃薯淀粉 | 9 |
| 粉条 | starch_noodles | 马铃薯淀粉 | 9 |
| 凉皮 | liangpi | 面粉 | 1 |
| 凉粉 | starch_jelly | 淀粉 | 1 |
| 碱水粽 | alkaline_zongzi | 糯米 | 5, 7 |
| 油条 | youtiao | 面粉 | 6, 9, 10 |
| 麻花 | mahua | 面粉、糖 | 8 |
| 馓子 | sanzi | 面粉 | — |
| 馒头 | steamed_bun | 面粉、酵母 | — |
| 包子 | baozi | 面粉、肉 / 蔬菜 | — |
| 花卷 | flower_roll | 面粉 | — |
| 发糕 | fagao | 米粉 / 面粉、糖 | 6 |
| 燕麦片 | oat_flakes | 燕麦（标签） | — |
| 早餐谷物 | breakfast_cereal | 玉米、小麦 | 20, 28 |
| 米布丁 | rice_pudding | 大米、牛奶 | 14 |
| 珍珠粉圆 | tapioca_pearls | 淀粉 | 18 |

## 07 焙烤食品

| 成品 | 建议 ID | 主要原料 | 添加剂 |
| --- | --- | --- | --- |
| 白面包 | white_bread | 面粉、酵母 | 27, 28 |
| 全麦面包 | whole_wheat_bread | 全麦粉 | 27 |
| 吐司 | toast_loaf | 面粉、牛奶 | 27, 28 |
| 法棍 | baguette | 面粉 | 28 |
| 牛角包 | croissant | 面粉、黄油 | 27 |
| 贝果 | bagel | 面粉、碱水 | 5 |
| 碱水结 | pretzel | 面粉、碱水 | 5 |
| 甜甜圈 | donut | 面粉、糖 | 6, 10 |
| 海绵蛋糕 | sponge_cake | 面粉、鸡蛋 | 6, 10, 27 |
| 戚风蛋糕 | chiffon_cake | 面粉、鸡蛋 | 6, 10 |
| 芝士蛋糕 | cheesecake | 奶油奶酪 | 17 |
| 慕斯蛋糕 | mousse_cake | 稀奶油 | 17 |
| 奶油蛋糕 | cream_cake | 蛋糕、淡奶油 | 19, 27 |
| 广式月饼 | cantonese_mooncake | 面粉、转化糖浆、枧水 | 5, 7 |
| 五仁月饼 | wuren_mooncake | 面粉、坚果 | 7 |
| 蛋黄酥 | egg_yolk_pastry | 面粉、咸蛋黄 | — |
| 桃酥 | taosu | 面粉、猪油 | 6, 8 |
| 苏打饼干 | soda_cracker | 面粉、酵母 | 6 |
| 黄油曲奇 | butter_cookie | 面粉、黄油 | 30 |
| 威化饼干 | wafer | 面粉 | 6 |
| 夹心饼干 | sandwich_biscuit | 面粉、奶油 | 19, 22 |
| 蛋卷 | egg_roll | 面粉、鸡蛋 | 20 |
| 泡芙 | cream_puff | 面粉、鸡蛋 | 8 |
| 蛋挞 | egg_tart | 酥皮、蛋液 | — |
| 苹果派 | apple_pie | 苹果、面粉 | 16 |
| 浆果派 | berry_pie | 浆果、面粉 | 16 |
| 玛芬 | muffin | 面粉、鸡蛋 | 6, 10 |
| 司康 | scone | 面粉、黄油 | 6, 10 |
| 华夫饼 | waffle | 面粉、鸡蛋 | 6 |
| 铜锣烧 | dorayaki | 面粉、红豆 | 6 |
| 姜饼 | gingerbread | 面粉、蜂蜜 | 6, 8 |

## 08 肉及肉制品

| 成品 | 建议 ID | 主要原料 | 添加剂 |
| --- | --- | --- | --- |
| 腊肉 | larou | 猪肉、盐 | 24 |
| 腊肠 | lap_cheong | 猪肉、糖、酒 | 21, 24 |
| 咸肉 | salted_pork | 猪肉、盐 | 24 |
| 板鸭 | pressed_salted_duck | 鸭（标签）、盐 | 24 |
| 中式火腿 | chinese_ham | 猪腿 | 24 |
| 西式火腿 | western_ham | 猪肉 | 10, 24 |
| 培根 | bacon | 猪肉 | 10, 24 |
| 热狗肠 | hot_dog | 猪肉、牛肉 | 10, 24 |
| 午餐肉罐头 | luncheon_meat | 猪肉、淀粉 | 10, 24 |
| 火腿肠 | ham_sausage | 猪肉、淀粉 | 10, 21, 24 |
| 叉烧 | char_siu | 猪肉、糖 | 18, 21 |
| 烤鸭 | roast_duck | 鸭、麦芽糖浆 | 18 |
| 卤牛肉 | braised_beef | 牛肉、卤汁 | 18 |
| 酱鸭 | soy_braised_duck | 鸭、酱油 | 18 |
| 白切鸡 | white_cut_chicken | 鸡肉 | — |
| 红烧肉 | braised_pork_belly | 猪肉、糖、酱油 | 18 |
| 东坡肉 | dongpo_pork | 猪肉、黄酒 | 18 |
| 肉松 | pork_floss | 猪肉 | 18 |
| 牛肉干 | beef_jerky | 牛肉 | 23, 24 |
| 猪肉脯 | pork_jerky_sheet | 猪肉、糖 | 21 |
| 肉丸 | meatballs | 肉、淀粉 | 10 |
| 狮子头 | lions_head_meatball | 猪肉 | — |
| 肉冻 | meat_aspic | 猪皮、肉汤 | 17 |
| 卤鸡爪 | braised_chicken_feet | 鸡（标签） | 18 |

## 09 水产及其制品

| 成品 | 建议 ID | 主要原料 | 添加剂 |
| --- | --- | --- | --- |
| 鱼丸 | fish_balls | 鳕鱼 | 10 |
| 蟹肉棒 | crab_stick | 鱼糜 | 10, 21 |
| 鱼豆腐 | fish_tofu | 鱼糜、淀粉 | 10 |
| 咸鱼 | salted_fish | 鳕鱼、盐 | — |
| 鱼干 | dried_fish | 鳕鱼 / 鲑鱼 | — |
| 熏鱼 | smoked_fish | 鲑鱼、糖 | 18 |
| 鱼罐头 | canned_fish | 鲑鱼 | 3 |
| 豆豉鲮鱼罐头 | fried_dace_with_black_beans | 鱼、豆豉 | — |
| 糖醋鱼 | sweet_sour_fish | 鳕鱼、醋、糖 | 1, 18 |
| 腌三文鱼 | gravlax | 鲑鱼、盐、糖 | — |
| 鱼皮冻 | fish_skin_jelly | 鱼皮 | 17 |
| 鱼露 | fish_sauce | 鱼、盐 | — |
| 虾酱 | shrimp_paste | 虾（标签）、盐 | — |

## 10 蛋及蛋制品

| 成品 | 建议 ID | 主要原料 | 添加剂 |
| --- | --- | --- | --- |
| 皮蛋 | century_egg | 鸭蛋 / 鸡蛋、碱、盐 | 5, 7 |
| 咸鸭蛋 | salted_egg | 鸭蛋 / 鸡蛋、盐 | — |
| 卤蛋 | braised_egg | 鸡蛋、卤汁 | 18 |
| 茶叶蛋 | tea_egg | 鸡蛋、茶叶 | 18 |
| 糟蛋 | wine_preserved_egg | 鸭蛋、酒糟 | — |
| 溏心蛋 | ramen_egg | 鸡蛋、酱油 | — |
| 鸡蛋布丁 | egg_pudding | 鸡蛋、牛奶 | 14 |
| 蛋清粉 | egg_white_powder | 蛋清 | — |

> 皮蛋的现代无铅工艺用硫酸铜、硫酸锌作加工助剂（GB 2760 表 C.2），可作为硫酸铜的另一个用途。

## 11 甜味料

| 成品 | 建议 ID | 主要原料 | 添加剂 |
| --- | --- | --- | --- |
| 蔗糖（白糖） | 即化学品 `sucrose` 的食品级，不另设物品 | 甘蔗 / 甜菜 | 25 |
| 绵白糖 | soft_white_sugar | 蔗糖（白糖） | — |
| 红糖 | brown_sugar | 甘蔗汁 | — |
| 冰糖 | rock_sugar | 蔗糖（白糖） | — |
| 方糖 | sugar_cube | 蔗糖（白糖） | — |
| 糖粉 | icing_sugar | 蔗糖（白糖）、淀粉 | — |
| 麦芽糖浆 | maltose_syrup | 大米、麦芽 | — |
| 葡萄糖浆 | glucose_syrup | 淀粉 | — |
| 果葡糖浆 | high_fructose_syrup | 葡萄糖浆 | — |
| 焦糖糖浆 | caramel_syrup | 糖 | 18 |
| 代糖 | tabletop_sweetener | 山梨糖醇 | 26 |

## 12 调味品

| 成品 | 建议 ID | 主要原料 | 添加剂 |
| --- | --- | --- | --- |
| 氯化钠（盐） | 即化学品 `salt` 的食品级，不另设物品 | 海水 / 岩盐 | — |
| 加碘盐 | iodized_salt | 氯化钠（盐） | 119（碘酸钾） |
| 低钠盐 | low_sodium_salt | 氯化钠（盐） | 75（氯化钾） |
| 海盐 | sea_salt | 海水 | — |
| 生抽 | light_soy_sauce | 大豆、小麦、盐 | 23 |
| 老抽 | dark_soy_sauce | 生抽 | 18, 23 |
| 鲜味酱油 | premium_soy_sauce | 生抽 | 4 |
| 米醋 | rice_vinegar | 大米 | 1 |
| 陈醋 | aged_vinegar | 高粱（标签）、麸皮 | 1, 18 |
| 苹果醋 | apple_cider_vinegar | 苹果 | 1 |
| 白醋 | white_vinegar | 冰乙酸、水 | 1 |
| 豆瓣酱 | doubanjiang | 蚕豆（标签）、辣椒 | — |
| 甜面酱 | sweet_bean_sauce | 面粉、盐 | 18 |
| 黄豆酱 | soybean_paste | 大豆、盐 | 23 |
| 番茄酱 | ketchup | 番茄 | 1, 23 |
| 沙拉酱 | salad_dressing | 植物油、蛋黄 | 3, 27 |
| 蛋黄酱 | mayonnaise | 植物油、蛋黄 | 1 |
| 芥末酱 | mustard | 芥末籽（标签） | 1 |
| 辣椒酱 | chili_sauce | 辣椒（标签） | 23 |
| 蚝油 | oyster_sauce | 蚝（标签） | 4, 18 |
| 鸡精 | chicken_bouillon_powder | 鸡肉、盐 | 4 |
| 高汤块 | stock_cube | 肉汤、盐 | 4, 18 |
| 昆布高汤 | kombu_dashi | 干海带 | — |
| 卤汁 | master_stock | 酱油、香料 | 18 |
| 料酒 | cooking_wine | 黄酒 | — |
| 火锅底料 | hotpot_base | 牛油、辣椒 | — |
| 咖喱块 | curry_roux | 油脂、香料 | 27 |
| 五香粉 | five_spice_powder | 香料（标签） | — |
| 十三香 | thirteen_spice | 香料（标签） | — |
| 辣椒油 | chili_oil | 植物油、辣椒 | — |
| 花椒油 | sichuan_pepper_oil | 植物油、花椒（标签） | — |

## 13 特殊膳食用食品

| 成品 | 建议 ID | 主要原料 | 添加剂 |
| --- | --- | --- | --- |
| 婴儿米粉 | infant_rice_cereal | 大米 | 28 |
| 蛋白粉 | protein_powder | 乳清 / 大豆蛋白 | — |
| 代餐粉 | meal_replacement_powder | 谷物粉 | 28 |
| 能量胶 | energy_gel | 葡萄糖浆 | 3, 26 |

## 14 饮料类

| 成品 | 建议 ID | 主要原料 | 添加剂 |
| --- | --- | --- | --- |
| 苹果汁 | apple_juice | 苹果 | 28 |
| 浆果汁 | berry_juice | 甜浆果 | 28 |
| 胡萝卜汁 | carrot_juice | 胡萝卜 | 20 |
| 甜菜汁 | beet_juice | 甜菜根 | 19 |
| 西瓜汁 | watermelon_juice | 西瓜 | — |
| 柠檬水 | lemonade | 柠檬（标签）、糖 | 3 |
| 柠檬汽水 | lemon_soda | 柠檬、CO2 | 3, 23 |
| 可乐 | cola | 糖、CO2、磷酸 | 64（焦糖色 IV 类） |
| 姜汁汽水 | ginger_ale | 姜（标签）、CO2 | 3 |
| 橙味汽水 | orange_soda | 糖、CO2 | 3, 20, 23 |
| 苏打水 | soda_water | 水、碳酸氢钠 | 6 |
| 气泡水 | sparkling_water | 水、CO2 | — |
| 运动饮料 | sports_drink | 葡萄糖、盐 | 3 |
| 能量饮料 | energy_drink | 糖 | 3, 28 |
| 维生素水 | vitamin_water | 水、糖 | 3, 28 |
| 酸梅汤 | sour_plum_drink | 乌梅（标签）、冰糖 | — |
| 凉茶 | herbal_tea_drink | 草本（标签） | — |
| 冰红茶 | iced_black_tea | 茶叶、糖 | 3, 28 |
| 绿茶饮料 | green_tea_drink | 茶叶 | 28 |
| 奶茶 | milk_tea | 茶叶、牛奶 | 18, 27 |
| 珍珠奶茶 | bubble_tea | 奶茶、粉圆 | 18 |
| 罐装咖啡 | canned_coffee | 咖啡豆（标签）、牛奶 | 27 |
| 速溶咖啡 | instant_coffee | 咖啡豆 | — |
| 豆奶 | soy_milk | 大豆 | 6 |
| 核桃露 | walnut_milk | 核桃 | 27 |
| 乳酸菌饮料 | probiotic_milk_drink | 牛奶、乳酸菌 | 2, 16 |
| 酸奶饮品 | yogurt_drink | 酸奶 | 15, 16 |
| 果味饮料 | fruit_flavored_drink | 糖、水 | 3, 19, 20, 23 |
| 薄荷饮料 | mint_drink | 薄荷 | 22, 29 |
| 蜂蜜柠檬茶 | honey_lemon_tea | 蜂蜜、柠檬 | 3 |
| 姜茶 | ginger_tea | 姜、红糖 | — |
| 果味冲剂 | fruit_drink_powder | 糖、果粉 | 3, 28 |

## 15 酒类

| 成品 | 建议 ID | 主要原料 | 添加剂 |
| --- | --- | --- | --- |
| 甜米酒 | jiuniang | 糯米、酒曲 | — |
| 黄酒 | huangjiu | 大米、麦曲 | 18 |
| 白酒 | baijiu | 高粱、大曲 | — |
| 啤酒 | beer | 大麦（标签）/ 小麦、啤酒花（标签） | — |
| 苹果酒 | cider | 苹果 | 25 |
| 浆果酒 | berry_wine | 甜浆果 | 25 |
| 葡萄酒 | grape_wine | 葡萄（标签） | 25 |
| 起泡酒 | sparkling_wine | 葡萄酒、CO2 | 25 |
| 蜂蜜酒 | mead | 蜂蜜 | — |
| 白兰地 | brandy | 果酒 | 18 |
| 威士忌 | whisky | 大麦 / 小麦 | 18 |
| 朗姆酒 | rum | 甘蔗 | 18 |
| 伏特加 | vodka | 马铃薯 | — |
| 青梅酒 | plum_liqueur | 梅子、白酒 | — |
| 薄荷利口酒 | mint_liqueur | 薄荷、乙醇 | 22, 29 |
| 香草利口酒 | vanilla_liqueur | 香荚兰、乙醇 | 30 |
| 红曲酒 | red_yeast_rice_wine | 大米、红曲 | 21 |

## 16 其他类

| 成品 | 建议 ID | 主要原料 | 添加剂 |
| --- | --- | --- | --- |
| 水果果冻 | fruit_jelly | 果汁、糖 | 3, 14 |
| 琼脂果冻 | agar_jelly | 琼脂、糖 | 14, 19 |
| 明胶果冻 | gelatin_jelly | 明胶、果汁 | 17 |
| 杏仁豆腐 | almond_tofu | 杏仁（标签）、牛奶 | 14 |
| 焦糖布丁 | caramel_custard | 鸡蛋、牛奶 | 17, 18 |
| 龟苓膏 | guilinggao | 草本（标签）、淀粉 | — |
| 爆爆珠 | popping_boba | 果汁、氯化钙 | 3, 15 |
| 椰果 | nata_de_coco | 椰汁（标签）、醋酸菌 | 1 |
| 马铃薯片 | potato_chips | 马铃薯、油 | 25, 27 |
| 虾片 | prawn_crackers | 淀粉、虾 | 9 |
| 玉米片 | corn_chips | 玉米 | 7 |
| 爆米花 | popcorn | 玉米、糖 | 18 |
| 锅巴 | guoba | 大米 | — |
| 膨化米饼 | puffed_rice_cracker | 大米 | 6 |
| 调味茶包 | flavored_tea | 茶叶 | 29, 30 |
| 胶原蛋白肠衣 | collagen_casing | 皮革 | 17 |
| 干酵母 | dry_yeast | 酵母 | — |
| 复配膨松剂（泡打粉） | baking_powder | 碳酸氢钠、淀粉 | 6, 10 |
