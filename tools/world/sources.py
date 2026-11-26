#!/usr/bin/env python3
"""I44 generation list: where the raw materials of additives/08 §C come from (来源规则, 2026-11-18, 问题 21).

One entry per species; the different parts of a species share its block (08 §C). The list is the input of

    src/main/resources/foodindustrial/world_sources.json   (read by world.source.SourceTable at start-up)

and of tools/world/source_assets.py (block states, models, placeholder textures).

Kinds (user, 2026-11-19):
  crop   a farmland crop planted with its first product (产物即种子); villagers sell it, village chests hold it
  wild   a wild herb in its biomes; right-click when grown (renewable) or break it for the products,
         shears pick the plant itself to move it
  tree   a tree: sapling + its own leaves on a vanilla trunk, or its own log when bark, wood, resin or latex
         comes from the trunk; flowers (age 2) and fruit (age 3) are picked from the leaves by hand,
         leaf products with shears (age 3); bark by stripping the log, resin and latex by tapping it
         with a knife (W02), wood by breaking it
  bush   a one-block shrub or woody vine (like the gardenia bush): the same stages on the bush itself

Numbers (counts, villager levels, rarities, biomes) are drafts to review (I44 待审).

Usage: python3 tools/world/sources.py   (from the repository root)
"""
import json
import pathlib
import sys

ROOT = pathlib.Path(__file__).resolve().parents[2]
OUT = ROOT / 'src' / 'main' / 'resources' / 'foodindustrial' / 'world_sources.json'
MATERIALS = ROOT / 'src' / 'main' / 'resources' / 'foodindustrial' / 'materials.json'

# ------------------------------------------------------------------ biome groups

BIOMES = {
    'plains': (['minecraft:plains', 'minecraft:sunflower_plains', 'minecraft:meadow'], '平原与草甸', 'plains and meadows'),
    'forest': (['minecraft:forest', 'minecraft:flower_forest', 'minecraft:birch_forest', 'minecraft:old_growth_birch_forest'],
               '森林', 'forests'),
    'dark_forest': (['minecraft:dark_forest', 'minecraft:old_growth_pine_taiga', 'minecraft:old_growth_spruce_taiga'],
                    '黑森林与原始针叶林', 'dark forests and old growth taigas'),
    'taiga': (['minecraft:taiga', 'minecraft:old_growth_pine_taiga', 'minecraft:old_growth_spruce_taiga'], '针叶林', 'taigas'),
    'alpine': (['minecraft:meadow', 'minecraft:windswept_hills', 'minecraft:windswept_gravelly_hills', 'minecraft:windswept_forest'],
               '草甸与风袭丘陵', 'meadows and windswept hills'),
    'jungle': (['minecraft:jungle', 'minecraft:sparse_jungle', 'minecraft:bamboo_jungle'], '丛林', 'jungles'),
    'savanna': (['minecraft:savanna', 'minecraft:savanna_plateau', 'minecraft:windswept_savanna'], '热带草原', 'savannas'),
    'desert': (['minecraft:desert', 'minecraft:badlands', 'minecraft:eroded_badlands'], '沙漠与恶地', 'deserts and badlands'),
    'arid': (['minecraft:desert', 'minecraft:badlands', 'minecraft:wooded_badlands', 'minecraft:savanna', 'minecraft:savanna_plateau'],
             '沙漠、恶地与热带草原', 'deserts, badlands and savannas'),
    'wetland': (['minecraft:swamp', 'minecraft:mangrove_swamp', 'minecraft:river'], '沼泽与河流', 'swamps and rivers'),
    'mediterranean': (['minecraft:windswept_hills', 'minecraft:windswept_gravelly_hills', 'minecraft:savanna_plateau',
                       'minecraft:windswept_savanna', 'minecraft:meadow'], '风袭丘陵与热带高原', 'windswept hills and savanna plateaus'),
    'east_asia': (['minecraft:cherry_grove', 'minecraft:forest', 'minecraft:flower_forest'], '樱花树林与森林', 'cherry groves and forests'),
    'subtropical': (['minecraft:bamboo_jungle', 'minecraft:cherry_grove', 'minecraft:sparse_jungle'], '竹林与樱花树林',
                    'bamboo jungles and cherry groves'),
    'beach': (['minecraft:beach'], '沙滩', 'beaches'),
}
# herbs of these groups grow on sand (and red sand) as well as on dirt
SANDY = {'desert', 'arid', 'beach'}

# village chests by climate (crops): loot tables the produce is added to
VILLAGES = {
    'temperate': ['chests/village/village_plains_house', 'chests/village/village_taiga_house'],
    'cold': ['chests/village/village_taiga_house', 'chests/village/village_snowy_house'],
    'arid': ['chests/village/village_desert_house'],
    'tropical': ['chests/village/village_savanna_house'],
}

# ------------------------------------------------------------------ new items (no material yet)

# id -> (zh, en, tag under materials/, texture kind, colour); registered by the world code like the I07 crops
NEW_ITEMS = {
    'red_rice': ('红米', 'Red Rice', 'red_rice', 'grain', '#8C2E2A'),
    'red_radish': ('红心萝卜', 'Red Radish', 'red_radish', 'root', '#C8365A'),
    'red_amaranth': ('红苋菜', 'Red Amaranth', 'red_amaranth', 'leaf', '#A02848'),
    'purple_sweet_potato': ('紫甘薯', 'Purple Sweet Potato', 'purple_sweet_potato', 'root', '#5E2C6E'),
    'barley': ('大麦', 'Barley', 'barley', 'grain', '#D8BE78'),
    'jerusalem_artichoke': ('菊芋块茎', 'Jerusalem Artichoke', 'jerusalem_artichoke', 'root', '#B89A70'),
    # finished-food line (foods/08 §6, foods/13, 16, 19, 22)
    'glutinous_paddy': ('糯稻', 'Glutinous Paddy', 'glutinous_paddy', 'grain', '#E2CF98'),
    'mung_bean': ('绿豆', 'Mung Bean', 'mung_bean', 'seed', '#5C9A40'),
    'adzuki_bean': ('赤豆', 'Adzuki Bean', 'adzuki_bean', 'seed', '#8E2A26'),
    'black_soybean': ('黑豆', 'Black Soybean', 'black_soybean', 'seed', '#2A2428'),
    'mustard_green': ('芥菜', 'Mustard Green', 'mustard_green', 'leaf', '#4E8C3A'),
    'broad_bean': ('蚕豆', 'Broad Bean', 'broad_bean', 'seed', '#8CB060'),
    'sorghum': ('高粱', 'Sorghum', 'sorghum', 'grain', '#9E4A2A'),
    'plum': ('梅子', 'Ume Plum', 'plum', 'fruit', '#B8C450'),
    # parts that the dismemberment enchantment drops (foods/17 §1.1)
    'pork_skin': ('猪皮', 'Pork Skin', 'pork_skin', 'tissue', '#E8B8A8'),
    'pork_hind_leg': ('猪后腿', 'Pork Hind Leg', 'pork_hind_leg', 'tissue', '#D88A80'),
    'small_intestine': ('小肠', 'Small Intestine', 'small_intestine', 'tissue', '#E0A8A0'),
    'chicken_feet': ('鸡爪', 'Chicken Feet', 'chicken_feet', 'tissue', '#E8C890'),
    # bamboo leaves for zongzi, crafted from bamboo (foods/13 §1.1)
    'zongzi_leaf': ('粽叶', 'Zongzi Leaf', 'zongzi_leaf', 'leaf', '#5A8A3A'),
    # own fallbacks of raw materials that only other mods gave (foods/23, 2026-11-25 user): crops
    'soybean': ('大豆', 'Soybean', 'soybean', 'seed', '#D8C27A'),
    'sesame': ('芝麻', 'Sesame', 'sesame', 'seed', '#EDE3CC'),
    'tomato': ('番茄', 'Tomato', 'tomato', 'fruit', '#D8382A'),
    'amomum': ('砂仁', 'Amomum', 'amomum', 'seed', '#7A5A3C'),
    'corn': ('玉米', 'Corn', 'corn', 'grain', '#E8C030'),
    'oats': ('燕麦', 'Oats', 'oats', 'grain', '#D9CB97'),
    'onion': ('洋葱', 'Onion', 'onion', 'root', '#C89058'),
    'scallion': ('大葱', 'Scallion', 'scallion', 'leaf', '#6AA040'),
    'cabbage': ('卷心菜', 'Cabbage', 'cabbage', 'leaf', '#9AC870'),
    'napa_cabbage': ('白菜', 'Napa Cabbage', 'napa_cabbage', 'leaf', '#D8E8B0'),
    'spinach': ('菠菜', 'Spinach', 'spinach', 'leaf', '#3E7A30'),
    'peanut': ('花生', 'Peanut', 'peanut', 'seed', '#C8A070'),
    'cucumber': ('黄瓜', 'Cucumber', 'cucumber', 'fruit', '#4E8A30'),
    'pineapple': ('菠萝', 'Pineapple', 'pineapple', 'fruit', '#E8B830'),
    # trees and bushes
    'cassia_bark': ('桂皮', 'Cassia Bark', 'cinnamon', 'bark', '#7A4A2A'),
    'walnut': ('核桃', 'Walnut', 'walnut', 'seed', '#A07850'),
    'orange': ('橙', 'Orange', 'orange', 'fruit', '#F08A20'),
    'lemon': ('柠檬', 'Lemon', 'lemon', 'fruit', '#F0E040'),
    'papaya': ('木瓜', 'Papaya', 'papaya', 'fruit', '#F0A040'),
    'grape': ('葡萄', 'Grape', 'grape', 'fruit', '#6A2A6A'),
    'tea_leaf': ('茶叶', 'Tea Leaves', 'tea', 'leaf', '#4E7A3A'),
    'coconut': ('椰子', 'Coconut', 'coconut', 'fruit', '#8A6A40'),
    # sea and animals: the oyster of the oyster reef (world.ModWorldBlocks), shrimp fished up, the duck's meat and eggs
    'oyster': ('蚝', 'Oyster', 'oyster', 'lump', '#B8B0A0'),
    'shrimp': ('虾', 'Shrimp', 'shrimp', 'fish', '#E89070'),
    'raw_duck': ('生鸭肉', 'Raw Duck', 'duck', 'poultry', '#E8B0A0'),
    'duck_egg': ('鸭蛋', 'Duck Egg', 'duck_egg', 'egg', '#D8E8E0'),
    # flavour raw materials that no mod gave (additives/08 §B, 26.1 recipe loading, 2026-11-25 user), and straw from rice
    # where Farmer's Delight is missing
    'lemongrass': ('柠檬草', 'Lemongrass', 'lemongrass', 'leaf', '#A0C058'),
    'caraway_seed': ('葛缕籽', 'Caraway Seed', 'caraway', 'seed', '#7A5A38'),
    'litsea_berry': ('山苍子', 'Litsea Berry', 'litsea', 'seed', '#4A4030'),
    'straw': ('稻草', 'Straw', 'straw', 'bark', '#D8C078'),
}
# extra tags the source items join (materials/<tag>): hawthorn (19 §1.1) is the hawthorn fruit of 08
EXTRA_TAGS = {'hawthorn': ['hawthorn_fruit'], 'ilmenite': ['ilmenite'], 'pyrolusite': ['pyrolusite'],
              # foods/23 (2026-11-25): the new fallbacks also join the group tags; red radish is a radish; laver is the I07-style sea plant
              'leafy_greens': ['cabbage', 'napa_cabbage', 'spinach'], 'nuts': ['walnut'], 'citrus': ['orange', 'lemon'],
              'radish': ['red_radish'], 'laver': ['laver']}

# ------------------------------------------------------------------ tags of other mods (外部优先)

# materials/<tag> joins these: 'common' paths under forge: (1.20.1) and c: (1.21 on), optional; 'items' ids; 'own'
# tags of this mod; 'forge'/'c' entries for one convention. Pam's HarvestCraft 2 tag names checked 2026-11-19 in
# pamhc2crops/pamhc2trees (1.20 and 1.21.1 jars); the duck, duck egg, shrimp and laver names are not checked.
LINKED = {
    # only other mods (foods/08 §6, 13, 16, 17, 19, 21, 22)
    'coconut': {'common': ['crops/coconut']},
    'walnut': {'common': ['crops/walnut', 'nuts/walnut']},
    'nuts': {'common': ['nuts']},
    'radish': {'common': ['crops/radish', 'vegetables/radish']},
    'cucumber': {'common': ['crops/cucumber', 'vegetables/cucumber']},
    'oats': {'common': ['crops/oats', 'crops/oat', 'grain/oats']},
    'beetroot': {'items': ['minecraft:beetroot'], 'common': ['crops/beetroot']},
    'leafy_greens': {'common': ['crops/cabbage', 'crops/lettuce', 'crops/spinach', 'crops/kale', 'crops/bokchoy']},
    'duck': {'common': ['raw_duck', 'foods/raw_duck']},
    'duck_egg': {'common': ['eggs/duck']},
    'shrimp': {'common': ['raw_shrimp', 'foods/raw_shrimp']},
    'laver': {'common': ['crops/laver', 'foods/laver']},
    'cowhide': {'items': ['minecraft:leather'], 'forge': ['#forge:leather'], 'c': ['#c:leathers']},
    # other mods' wheat flour (Create: #forge:flour/wheat on 1.20.1, #c:flours/wheat on 1.21.1; I47, 2026-11-25 user)
    'wheat_flour': {'common': ['flour/wheat', 'flours/wheat']},
    # 草本 (foods/21 §1.1): the herb materials of 08
    'herbs': {'own': ['materials/licorice_root', 'materials/german_chamomile', 'materials/roman_chamomile', 'materials/chrysanthemum_flower',
                      'materials/mint', 'materials/rosemary', 'materials/peppermint', 'materials/monk_fruit', 'materials/perilla_leaf',
                      'materials/angelica_root', 'materials/angelica_dahurica_root', 'materials/atractylodes_rhizome', 'materials/sage']},
    # raw materials of 08 §C that are vanilla blocks or other mods' items (their 获取 names them; not exported before I44)
    'melon': {'items': ['minecraft:melon_slice']},
    'oak_log': {'own': [], 'items': ['#minecraft:oak_logs']},
    'dandelion': {'items': ['minecraft:dandelion']},
    'mushroom': {'items': ['minecraft:brown_mushroom']},
    'rose_flower': {'items': ['minecraft:rose_bush']},
    'fir_needles': {'items': ['minecraft:spruce_leaves']},
    'lime': {'common': ['fruits/lime']},
    'grapefruit': {'common': ['fruits/grapefruit']},
    # crops of this list that Pam's also grows: its items count too
    'ginger': {'common': ['crops/ginger']},
    'barley': {'common': ['crops/barley']},
    'garlic': {'common': ['crops/garlic']},
    'leek': {'common': ['crops/leek']},
    'mustard_seed': {'common': ['crops/mustardseeds']},
    'sorghum': {'common': ['crops/sorghum']},
    'alfalfa': {'common': ['crops/alfalfa']},
    'mulberry': {'common': ['crops/mulberry']},
    'juniper_berry': {'common': ['crops/juniperberry']},
    'coffee_bean': {'common': ['crops/coffeebean']},
}

# ------------------------------------------------------------------ crops (product is seed, 2026-11-19)

CROPS = []


def crop(cid, zh, en, drops, leaf, produce, level=1, climate='temperate', wild=None):
    """drops: [(item, min, max)] at full age; the first item is planted. wild: biome group of a wild patch."""
    CROPS.append({'id': cid, 'zh': zh, 'en': en, 'drops': [list(d) for d in drops], 'leaf': leaf, 'colour': produce,
                  'level': level, 'climate': climate, 'wild': wild})


G, DG, YG = '#4E8C3A', '#3A6E34', '#7AA048'  # leaf greens
crop('safflower', '红花', 'Safflower', [('safflower', 1, 3)], YG, '#E8501A', 2, 'arid')
crop('red_rice', '红米', 'Red Rice', [('red_rice', 1, 3)], YG, '#8C2E2A', 1, 'tropical')
crop('sorghum', '高粱', 'Sorghum', [('sorghum', 1, 3), ('sorghum_husk', 1, 2)], G, '#9E4A2A', 1, 'arid')
crop('red_radish', '红心萝卜', 'Red Radish', [('red_radish', 1, 3)], G, '#C8365A', 1)
crop('roselle', '玫瑰茄', 'Roselle', [('roselle', 1, 3)], DG, '#A01830', 2, 'tropical')
crop('red_amaranth', '红苋菜', 'Red Amaranth', [('red_amaranth', 1, 3)], '#7A3040', '#A02848', 1)
crop('marigold', '万寿菊', 'Marigold', [('marigold', 1, 3)], G, '#F0A020', 1)
crop('purple_sweet_potato', '紫甘薯', 'Purple Sweet Potato', [('purple_sweet_potato', 1, 3)], G, '#5E2C6E', 1, 'tropical')
crop('flax', '亚麻', 'Flax', [('flaxseed', 1, 3)], YG, '#8A5A30', 1)
crop('sesbania', '田菁', 'Sesbania', [('sesbania_seed', 1, 3)], G, '#6E5A3A', 2, 'tropical')
crop('guar', '瓜尔豆', 'Guar', [('guar_seed', 1, 3)], G, '#C8C0A0', 2, 'arid')
crop('sicklepod', '决明', 'Sicklepod', [('cassia_seed', 1, 3)], G, '#6A5030', 2, 'tropical')
crop('monk_fruit', '罗汉果', 'Monk Fruit', [('monk_fruit', 1, 2)], DG, '#6A4A2A', 3, 'tropical')
crop('barley', '大麦', 'Barley', [('barley', 1, 3)], YG, '#D8BE78', 1)
crop('jerusalem_artichoke', '菊芋', 'Jerusalem Artichoke', [('jerusalem_artichoke', 1, 3)], G, '#B89A70', 1)
crop('black_mustard', '黑芥', 'Black Mustard', [('mustard_seed', 1, 3)], G, '#3A2A20', 2)
crop('wasabi', '山葵', 'Wasabi', [('wasabi', 1, 2)], DG, '#8CB070', 3, 'cold')
crop('clary_sage', '香紫苏', 'Clary Sage', [('clary_sage_herb', 1, 3)], '#7A9070', '#B0A0D0', 2)
crop('rape', '油菜', 'Rape', [('rapeseed', 1, 3)], G, '#3A2A24', 1)
crop('basil', '罗勒', 'Basil', [('basil', 1, 3)], '#3E9A40', '#3E9A40', 1)
crop('patchouli', '广藿香', 'Patchouli', [('patchouli_leaf', 1, 3)], DG, '#4A6A30', 3, 'tropical')
crop('cardamom', '小豆蔻', 'Cardamom', [('cardamom', 1, 2)], DG, '#8AA060', 4, 'tropical')
crop('sweet_fennel', '甜茴香', 'Sweet Fennel', [('sweet_fennel_seed', 1, 3)], YG, '#9A9A50', 2)
crop('garlic', '大蒜', 'Garlic', [('garlic', 1, 3)], G, '#EAE2D0', 1)
crop('costus', '云木香', 'Costus', [('costus_root', 1, 2)], G, '#8A6440', 3, 'cold')
crop('angelica_dahurica', '白芷', 'Angelica Dahurica', [('angelica_dahurica_root', 1, 2)], G, '#D8CCAA', 2)
crop('coriander', '芫荽', 'Coriander', [('coriander_seed', 1, 3)], G, '#B09A60', 1)
crop('celery', '芹菜', 'Celery', [('celery_seed', 1, 2), ('celery_leaf', 1, 2), ('celery_flower', 0, 1)], '#6AA848', '#E8E8C8', 1)
crop('chrysanthemum', '杭白菊', 'Chrysanthemum', [('chrysanthemum_flower', 1, 3)], G, '#F0EEDC', 2)
crop('fenugreek', '葫芦巴', 'Fenugreek', [('fenugreek_seed', 1, 3)], G, '#C89A40', 2, 'arid')
crop('cumin', '孜然', 'Cumin', [('cumin_seed', 1, 3)], YG, '#8A6A40', 2, 'arid')
crop('rose_geranium', '香叶天竺葵', 'Rose Geranium', [('rose_geranium_leaf', 1, 3)], G, '#E080A0', 3)
crop('chives', '细香葱', 'Chives', [('chives', 1, 3)], '#4A9A3A', '#B080C0', 1)
crop('hops', '啤酒花', 'Hops', [('hops', 1, 3)], G, '#A8C858', 1)
crop('broad_bean', '蚕豆', 'Broad Bean', [('broad_bean', 1, 3), ('broad_bean_flower', 0, 1)], G, '#8CB060', 1)
crop('chicory', '菊苣', 'Chicory', [('chicory_root', 1, 2)], G, '#7090D0', 2)
crop('tuberose', '晚香玉', 'Tuberose', [('tuberose_flower', 1, 2)], G, '#F4F0E8', 3, 'tropical')
crop('sweet_violet', '香堇菜', 'Sweet Violet', [('violet_leaf', 1, 3)], DG, '#7040A0', 3)
crop('peppermint', '椒样薄荷', 'Peppermint', [('peppermint', 1, 3)], '#3E9A50', '#3E9A50', 1)
crop('crimson_glory_rose', '墨红', 'Crimson Glory Rose', [('crimson_glory_rose', 1, 2)], DG, '#8A1020', 3)
crop('lavender', '薰衣草', 'Lavender', [('lavender', 1, 3)], '#7A9A80', '#8A70C0', 2)
crop('thyme', '百里香', 'Thyme', [('thyme', 1, 3)], '#5A7A50', '#5A7A50', 1)
crop('ginger', '姜', 'Ginger', [('ginger', 1, 3)], G, '#D8B070', 1, 'tropical')
crop('tarragon', '龙蒿', 'Tarragon', [('tarragon', 1, 3)], G, '#5A8A40', 2)
crop('lavandin', '杂薰衣草', 'Lavandin', [('lavandin', 1, 3)], '#7A9A80', '#7A60B8', 2)
crop('galangal', '高良姜', 'Galangal', [('galangal_root', 1, 3)], G, '#C88A60', 2, 'tropical')
crop('fennel', '小茴香', 'Fennel', [('fennel_seed', 1, 3)], YG, '#8A8A50', 1)
crop('parsley', '欧芹', 'Parsley', [('parsley', 1, 3)], '#3E8E30', '#3E8E30', 1)
crop('citronella', '香茅', 'Citronella', [('citronella_grass', 1, 3)], '#8AB050', '#8AB050', 2, 'tropical')
crop('roman_chamomile', '罗马春黄菊', 'Roman Chamomile', [('roman_chamomile', 1, 3)], G, '#F0F0D8', 2)
crop('lovage', '欧当归', 'Lovage', [('lovage_root', 1, 2)], G, '#C8B080', 2)
crop('dill', '莳萝', 'Dill', [('dill', 1, 3)], YG, '#C8C848', 1)
crop('marjoram', '甘牛至', 'Marjoram', [('marjoram', 1, 3)], G, '#C090B0', 1)
crop('ambrette', '黄葵', 'Ambrette', [('ambrette_seed', 1, 3)], G, '#F0D040', 3, 'tropical')
crop('pepper', '胡椒', 'Pepper', [('black_pepper', 1, 2), ('white_pepper', 0, 1)], DG, '#2A2420', 3, 'tropical')
crop('lemon_balm', '香蜂花', 'Lemon Balm', [('lemon_balm', 1, 3)], YG, '#7AB048', 1)
crop('bird_eye_chili', '小米辣', 'Bird Eye Chili', [('bird_eye_chili', 1, 3)], G, '#D82818', 1, 'tropical')
crop('anise', '茴芹', 'Anise', [('anise_seed', 1, 3)], YG, '#A89A60', 2)
crop('angelica', '当归', 'Angelica', [('angelica_seed', 1, 2), ('angelica_root', 1, 2)], G, '#D8C8A0', 2, 'cold')
crop('white_mustard', '白芥', 'White Mustard', [('yellow_mustard_seed', 1, 3)], YG, '#E0C050', 1)
crop('mustard', '芥菜', 'Mustard', [('brown_mustard_seed', 1, 2), ('mustard_green', 1, 2)], G, '#4E8C3A', 1)
crop('perilla', '紫苏', 'Perilla', [('perilla_leaf', 1, 3)], '#6A3A6A', '#6A3A6A', 1)
crop('alfalfa', '苜蓿', 'Alfalfa', [('alfalfa', 1, 3)], G, '#8A70C0', 1)
crop('saffron', '番红花', 'Saffron Crocus', [('saffron', 1, 1)], G, '#C82010', 4, 'arid')
crop('horseradish', '辣根', 'Horseradish', [('horseradish_root', 1, 2)], G, '#E0D8C0', 1)
crop('leek', '韭葱', 'Leek', [('leek', 1, 3)], '#6A9A60', '#E8F0D8', 1)
crop('winter_savory', '冬香薄荷', 'Winter Savory', [('winter_savory', 1, 3)], DG, '#4A6A40', 2)
crop('scotch_spearmint', '苏格兰留兰香', 'Scotch Spearmint', [('scotch_spearmint', 1, 3)], '#3E9A50', '#3E9A50', 2)
crop('spearmint', '留兰香', 'Spearmint', [('spearmint', 1, 3)], '#48A848', '#48A848', 1)
crop('summer_savory', '夏香薄荷', 'Summer Savory', [('summer_savory', 1, 3)], G, '#6A8A50', 2)
crop('grains_of_paradise', '摩洛哥豆蔻', 'Grains of Paradise', [('grains_of_paradise', 1, 2)], DG, '#8A3020', 4, 'tropical')
crop('aloe', '芦荟', 'Aloe', [('aloe', 1, 2)], '#6A9A70', '#6A9A70', 2, 'arid')
# finished-food line (foods/08 §6): wild like paddy (glutinous paddy, foods/13) and the two beans (draft)
crop('glutinous_paddy', '糯稻', 'Glutinous Paddy', [('glutinous_paddy', 1, 3), ('straw', 1, 2)], YG, '#E2CF98', 1, 'tropical',
     wild='wetland')
crop('mung_bean', '绿豆', 'Mung Bean', [('mung_bean', 1, 3)], G, '#5C9A40', 1, 'temperate', wild='plains')
crop('adzuki_bean', '赤豆', 'Adzuki Bean', [('adzuki_bean', 1, 3)], G, '#8E2A26', 1, 'temperate', wild='plains')
crop('black_soybean', '黑豆', 'Black Soybean', [('black_soybean', 1, 3)], G, '#2A2428', 1)
# own fallbacks of raw materials that only other mods gave (foods/23 §3, 2026-11-25 user)
crop('soybean', '大豆', 'Soybean', [('soybean', 1, 3)], G, '#D8C27A', 1)
crop('sesame', '芝麻', 'Sesame', [('sesame', 1, 3)], G, '#EDE3CC', 2, 'arid')
crop('tomato', '番茄', 'Tomato', [('tomato', 1, 3)], G, '#D8382A', 1)
crop('amomum', '砂仁', 'Amomum', [('amomum', 1, 2)], DG, '#7A5A3C', 3, 'tropical', wild='jungle')
crop('corn', '玉米', 'Corn', [('corn', 1, 3)], G, '#E8C030', 1)
crop('oats', '燕麦', 'Oats', [('oats', 1, 3)], YG, '#D9CB97', 1, 'cold')
crop('onion', '洋葱', 'Onion', [('onion', 1, 3)], G, '#C89058', 1)
crop('scallion', '大葱', 'Scallion', [('scallion', 1, 3)], G, '#6AA040', 1)
crop('cabbage', '卷心菜', 'Cabbage', [('cabbage', 1, 2)], G, '#9AC870', 1, 'cold')
crop('napa_cabbage', '白菜', 'Napa Cabbage', [('napa_cabbage', 1, 2)], YG, '#D8E8B0', 1, 'cold')
crop('spinach', '菠菜', 'Spinach', [('spinach', 1, 3)], DG, '#3E7A30', 1, 'cold')
crop('peanut', '花生', 'Peanut', [('peanut', 1, 3)], G, '#C8A070', 1)
crop('cucumber', '黄瓜', 'Cucumber', [('cucumber', 1, 3)], G, '#4E8A30', 1)
crop('pineapple', '菠萝', 'Pineapple', [('pineapple', 1, 1)], DG, '#E8B830', 2, 'tropical')
# flavour raw materials that no mod gave (additives/08 §B, 2026-11-25 user): sources of 125 and 133 (litsea: a bush below)
crop('lemongrass', '柠檬草', 'Lemongrass', [('lemongrass', 1, 3)], '#9AB858', '#A0C058', 2, 'tropical')
crop('caraway', '葛缕子', 'Caraway', [('caraway_seed', 1, 3)], YG, '#7A5A38', 2, 'cold')

# ------------------------------------------------------------------ wild herbs

WILD = []


def wild(wid, zh, en, drops, biome, leaf, produce, rarity=24):
    WILD.append({'id': wid, 'zh': zh, 'en': en, 'drops': [list(d) for d in drops], 'biome': biome, 'leaf': leaf,
                 'colour': produce, 'rarity': rarity, 'sand': biome in SANDY})


wild('coreopsis', '剑叶金鸡菊', 'Coreopsis', [('coreopsis', 1, 2)], 'plains', G, '#F0C020')
wild('gromwell', '紫草', 'Gromwell', [('gromwell_root', 1, 2)], 'alpine', G, '#6A1A40')
wild('sand_sagebrush', '沙蒿', 'Sand Sagebrush', [('artemisia_seed', 1, 2)], 'desert', '#9AA890', '#8A7A50')
wild('licorice', '甘草', 'Licorice', [('licorice_root', 1, 2)], 'arid', G, '#C8A050')
wild('katemfe', '西非竹芋', 'Katemfe', [('katemfe_fruit', 1, 2)], 'jungle', DG, '#C83020')
wild('shiitake', '香菇', 'Shiitake', [('shiitake', 1, 2)], 'dark_forest', '#8A6040', '#7A5030')
wild('nardostachys', '甘松', 'Spikenard', [('nardostachys_root', 1, 2)], 'alpine', G, '#8A5A40')
wild('davana', '印蒿', 'Davana', [('davana_herb', 1, 2)], 'savanna', '#90A080', '#E0C060')
wild('atractylodes', '苍术', 'Atractylodes', [('atractylodes_rhizome', 1, 2)], 'forest', DG, '#A88A60')
wild('orris', '鸢尾', 'Orris', [('orris_root', 1, 2)], 'plains', '#6A9A80', '#8A70C8')
wild('tree_moss', '树苔', 'Tree Moss', [('tree_moss', 1, 2)], 'taiga', '#A8B090', '#A8B090')
wild('rabdosia', '香茶菜', 'Rabdosia', [('rabdosia_herb', 1, 2)], 'forest', G, '#9A80C0')
wild('vetiver', '香根草', 'Vetiver', [('vetiver_root', 1, 2)], 'jungle', '#7AA050', '#8A6A40')
wild('nutgrass', '香附子', 'Nutgrass', [('cyperus_rhizome', 1, 2)], 'wetland', '#6A9A40', '#5A4030')
wild('valerian', '缬草', 'Valerian', [('valerian_root', 1, 2)], 'forest', G, '#E8D0E0')
wild('conehead_thyme', '头状百里香', 'Conehead Thyme', [('conehead_thyme', 1, 2)], 'mediterranean', '#6A7A50', '#C080B0')
wild('spanish_sage', '西班牙鼠尾草', 'Spanish Sage', [('spanish_sage', 1, 2)], 'mediterranean', '#8A9A80', '#8A80C0')
wild('asafoetida', '阿魏', 'Asafoetida', [('asafoetida', 1, 2)], 'desert', '#8AA050', '#C8A060')
wild('palmarosa', '玫瑰草', 'Palmarosa', [('palmarosa', 1, 2)], 'savanna', '#8AB050', '#8AB050')
wild('pennyroyal', '胡薄荷', 'Pennyroyal', [('pennyroyal', 1, 2)], 'wetland', '#4A8A50', '#A080C8')
wild('horehound', '欧夏至草', 'Horehound', [('horehound', 1, 2)], 'plains', '#9AA890', '#E8E8E0')
wild('yucca', '丝兰', 'Yucca', [('yucca_trunk', 1, 1)], 'desert', '#7A9A70', '#A08860')
wild('hyssop', '海索草', 'Hyssop', [('hyssop', 1, 2)], 'mediterranean', G, '#5060C0')
wild('galbanum', '格蓬', 'Galbanum', [('galbanum', 1, 2)], 'desert', '#8AA050', '#C8B070')
wild('gentian', '黄龙胆', 'Yellow Gentian', [('gentian_root', 1, 2)], 'alpine', G, '#F0C030')
wild('immortelle', '蜡菊', 'Immortelle', [('immortelle_flower', 1, 2)], 'mediterranean', '#A0A890', '#F0C030')
wild('spike_lavender', '穗薰衣草', 'Spike Lavender', [('spike_lavender', 1, 2)], 'mediterranean', '#8AA090', '#9080C8')
wild('jambu', '金钮扣', 'Jambu', [('jambu', 1, 2)], 'jungle', G, '#F0C020')
wild('sage', '药鼠尾草', 'Sage', [('sage', 1, 2)], 'mediterranean', '#8A9A88', '#8A9A88')
wild('german_chamomile', '母菊', 'German Chamomile', [('german_chamomile', 1, 2)], 'plains', YG, '#F0F0E0')
wild('red_clover', '红三叶草', 'Red Clover', [('red_clover', 1, 2)], 'plains', G, '#D05080')
wild('canadian_fleabane', '加拿大飞蓬', 'Canadian Fleabane', [('canadian_fleabane', 1, 2)], 'plains', YG, '#F0F0E0')
wild('rue', '芸香', 'Rue', [('rue', 1, 2)], 'mediterranean', '#7A9A90', '#E0D040')
wild('sarsaparilla', '菝葜', 'Sarsaparilla', [('sarsaparilla_root', 1, 2)], 'jungle', DG, '#9A6040')
wild('wormwood', '苦艾', 'Wormwood', [('wormwood', 1, 2)], 'plains', '#A0A898', '#C8C080')
wild('damiana', '达迷草', 'Damiana', [('damiana_leaf', 1, 2)], 'arid', G, '#F0D030')
wild('opoponax', '甜没药', 'Opoponax', [('opoponax', 1, 2)], 'desert', '#8AA050', '#C89040')
wild('mexican_oregano', '墨西哥牛至', 'Mexican Oregano', [('mexican_oregano', 1, 2)], 'arid', '#6A8A50', '#E8E8D0')
wild('dittany_of_crete', '白藓牛至', 'Dittany of Crete', [('dittany_of_crete', 1, 2)], 'alpine', '#A8B0A0', '#D080A0')
wild('oakmoss', '橡苔', 'Oakmoss', [('oakmoss', 1, 2)], 'forest', '#A0B090', '#A0B090')
wild('canadian_snakeroot', '加拿大细辛', 'Canadian Snakeroot', [('canadian_snakeroot', 1, 2)], 'dark_forest', DG, '#7A3030')
wild('tragacanth', '黄蓍', 'Tragacanth', [('tragacanth_lump', 1, 2)], 'desert', '#9AA890', '#F0E8D0')
wild('marshmallow', '药蜀葵', 'Marshmallow', [('marshmallow_root', 1, 2)], 'wetland', G, '#F0D0E0')
wild('candelilla', '小烛树', 'Candelilla', [('candelilla_stem', 1, 2)], 'desert', '#8AA080', '#8AA080')
wild('evening_primrose', '月见草', 'Evening Primrose', [('evening_primrose_seed', 1, 2)], 'plains', G, '#F0E040')

# ------------------------------------------------------------------ trees and bushes

TREES = []
WOODS = {'oak', 'birch', 'spruce', 'jungle', 'acacia', 'dark_oak', 'cherry'}


def tree(tid, zh, en, biome, trunk='oak', form='tree', flower=(), fruit=(), leaf=(), strip=(), wood=(), tap=None,
         leaves='#4E8C3A', bloom='#F0F0F0', colour='#C04030', bark=None, rarity=8):
    """trunk: a vanilla wood type, or 'own' (own log, bark colour); form: tree, conifer, palm or bush.
    tap: (result, kind, amount) with kind item or fluid (fluids come in a bottle, 250 mB)."""
    assert form in ('tree', 'conifer', 'palm', 'bush'), tid
    assert (trunk in WOODS) or (trunk == 'own' and bark), tid
    assert form == 'bush' or trunk != 'own' or strip or wood or tap, tid
    TREES.append({'id': tid, 'zh': zh, 'en': en, 'biome': biome, 'trunk': trunk, 'form': form,
                  'flower': [list(d) for d in flower], 'fruit': [list(d) for d in fruit], 'leaf': [list(d) for d in leaf],
                  'strip': [list(d) for d in strip], 'wood': [list(d) for d in wood], 'tap': list(tap) if tap else None,
                  'leaves': leaves, 'bloom': bloom, 'colour': colour, 'bark': bark, 'rarity': rarity})


def bush(bid, zh, en, biome, **kw):
    kw.setdefault('rarity', 32)
    tree(bid, zh, en, biome, form='bush', **kw)


B, DK, PALE = '#4E8C3A', '#2E5A2A', '#7A9A6A'
# --- shrubs and woody vines
bush('blackcurrant', '黑加仑', 'Blackcurrant', 'taiga', fruit=[('blackcurrant', 1, 3)], leaf=[('blackcurrant_bud', 1, 2)],
     colour='#2A1830')
bush('sea_buckthorn', '沙棘', 'Sea Buckthorn', 'alpine', fruit=[('sea_buckthorn', 1, 3)], leaves='#8A9A80', colour='#F09020')
bush('cherokee_rose', '金樱子', 'Cherokee Rose', 'subtropical', fruit=[('rosa_laevigata', 1, 2)], colour='#D07020')
bush('haskap', '蓝锭果', 'Haskap', 'taiga', fruit=[('haskap', 1, 3)], colour='#3A3A80')
bush('annatto', '胭脂树', 'Annatto', 'jungle', fruit=[('annatto_seed', 1, 3)], colour='#C82818')
bush('tara', '刺云实', 'Tara', 'savanna', fruit=[('tara_seed', 1, 2)], colour='#C87030')
bush('coffee', '咖啡', 'Coffee', 'jungle', fruit=[('coffee_bean', 1, 3)], leaves=DK, colour='#B02020')
bush('murraya', '九里香', 'Orange Jasmine', 'subtropical', flower=[('jasmin_orange_flower', 1, 3)], leaves=DK)
bush('buchu', '布枯', 'Buchu', 'mediterranean', leaf=[('buchu_leaf', 1, 3)], leaves='#5A7A40', bloom='#E0B0E0')
bush('wintergreen', '平铺白珠', 'Wintergreen', 'taiga', leaf=[('wintergreen_leaf', 1, 3)], leaves=DK, colour='#C82030')
bush('juniper', '杜松', 'Juniper', 'taiga', fruit=[('juniper_berry', 1, 3)], leaves='#4A6A5A', colour='#3A4A70')
bush('vitex', '牡荆', 'Vitex', 'east_asia', leaf=[('vitex_leaf', 1, 3)], bloom='#9A80D0')
bush('rockrose', '岩蔷薇', 'Rockrose', 'mediterranean', leaf=[('rockrose_twig', 1, 3)], leaves='#5A6A40')
bush('cassie', '金合欢', 'Cassie', 'savanna', flower=[('cassie_flower', 1, 3)], bloom='#F0D020')
bush('jasmine_grandiflorum', '大花茉莉', 'Spanish Jasmine', 'savanna', flower=[('jasmine_grandiflorum_flower', 1, 3)])
bush('jasmine_sambac', '茉莉', 'Arabian Jasmine', 'subtropical', flower=[('jasmine_sambac_flower', 1, 3)], leaves=DK)
bush('jasmine_officinale', '素方花', 'Common Jasmine', 'alpine', flower=[('jasmine_officinale_flower', 1, 3)])
bush('aglaia', '米仔兰', 'Aglaia', 'subtropical', flower=[('aglaia_flower', 1, 3)], leaves=DK, bloom='#F0E040')
bush('wild_rose', '野蔷薇', 'Multiflora Rose', 'east_asia', flower=[('wild_rose_flower', 1, 3)], bloom='#F0D0E0')
bush('dog_rose', '犬蔷薇', 'Dog Rose', 'plains', fruit=[('rose_hip', 1, 3)], bloom='#F0B0C8', colour='#D03020')
bush('boronia', '波罗尼亚', 'Brown Boronia', 'mediterranean', flower=[('boronia_flower', 1, 3)], bloom='#8A4020')
bush('cubeb', '荜澄茄', 'Cubeb', 'jungle', fruit=[('cubeb', 1, 3)], leaves=DK, colour='#3A2A20')
bush('elder', '接骨木', 'Elder', 'forest', flower=[('elderflower', 1, 3)], bloom='#F0EEDC')
bush('broom', '鹰爪豆', 'Spanish Broom', 'mediterranean', flower=[('genet_flower', 1, 3)], leaves='#6A8A40', bloom='#F0D020')
bush('guarana', '瓜拉纳', 'Guarana', 'jungle', fruit=[('guarana_seed', 1, 3)], leaves=DK, colour='#C82018')
bush('yerba_santa', '圣草', 'Yerba Santa', 'mediterranean', leaf=[('yerba_santa', 1, 3)], leaves='#6A7A50')
bush('sichuan_pepper', '花椒', 'Sichuan Pepper', 'east_asia', fruit=[('sichuan_pepper', 1, 3)], colour='#A02818')
bush('mountain_pine', '欧洲山松', 'Mountain Pine', 'alpine', leaf=[('dwarf_pine_needles', 1, 3)], leaves='#2E5A3A')
# --- trees on vanilla trunks
tree('mulberry', '桑树', 'Mulberry', 'east_asia', fruit=[('mulberry', 1, 3)], colour='#4A1A3A')
tree('bayberry', '杨梅', 'Chinese Bayberry', 'subtropical', fruit=[('bayberry', 1, 3)], leaves=DK, colour='#9A1830')
tree('coconut', '椰子树', 'Coconut Palm', 'beach', trunk='jungle', form='palm', fruit=[('copra', 1, 2), ('coconut', 0, 1)], colour='#8A6A40',
     rarity=4)
tree('oil_palm', '油棕', 'Oil Palm', 'jungle', trunk='jungle', form='palm', fruit=[('oil_palm_fruit', 1, 3)], colour='#C83018')
tree('carnauba', '巴西棕榈', 'Carnauba Palm', 'savanna', trunk='acacia', form='palm', leaf=[('carnauba_powder', 1, 2)],
     leaves='#7A9A70')
tree('tamarind', '酸角', 'Tamarind', 'savanna', trunk='acacia', fruit=[('tamarind_pulp', 1, 2), ('tamarind_seed', 1, 2)],
     colour='#7A5030')
tree('carob', '角豆树', 'Carob', 'mediterranean', fruit=[('carob_pod', 1, 2), ('carob_seed', 1, 1)], leaves=DK, colour='#5A3020')
tree('honeylocust', '皂荚', 'Chinese Honeylocust', 'east_asia', fruit=[('gleditsia_seed', 1, 3)], colour='#4A2A1A')
tree('tallow_tree', '乌桕', 'Tallow Tree', 'subtropical', fruit=[('tallow_tree_seed', 1, 3)], colour='#F0F0E8')
tree('malania', '蒜头果', 'Malania', 'subtropical', fruit=[('malania_seed', 1, 2)], colour='#7A8A40')
tree('broadleaf_peppermint', '宽叶薄荷桉', 'Broad-leaved Peppermint', 'savanna', trunk='birch', leaf=[('eucalyptus_dives', 1, 3)],
     leaves='#7A9A80')
tree('clove', '丁香', 'Clove', 'jungle', trunk='jungle', flower=[('clove_bud', 1, 2), ('clove_stem', 0, 1)],
     leaf=[('clove_leaf', 1, 2)], leaves=DK, bloom='#C04030')
tree('star_anise', '八角', 'Star Anise', 'subtropical', fruit=[('star_anise', 1, 2)], leaves=DK, colour='#7A3A20')
tree('hawthorn', '山楂', 'Hawthorn', 'east_asia', fruit=[('hawthorn_fruit', 1, 3)], bloom='#F0F0F0', colour='#C82020')
tree('ume', '梅', 'Ume', 'east_asia', trunk='cherry', fruit=[('plum', 1, 3)], bloom='#F0C0D0', colour='#B8C450')
tree('michelia', '白兰', 'White Champaca', 'subtropical', flower=[('michelia_alba_flower', 1, 2)], leaf=[('michelia_alba_leaf', 1, 2)],
     bloom='#F8F4E8')
tree('nutmeg', '肉豆蔻', 'Nutmeg', 'jungle', trunk='jungle', fruit=[('nutmeg', 1, 2), ('mace', 0, 1)], leaves=DK, colour='#D0A040')
tree('jujube', '枣', 'Jujube', 'east_asia', fruit=[('jujube', 1, 3)], colour='#8A1A18')
tree('fingered_citron', '佛手', 'Fingered Citron', 'subtropical', fruit=[('fingered_citron', 1, 1)], leaves=DK, colour='#E8C830')
tree('pummelo', '柚', 'Pummelo', 'subtropical', fruit=[('pummelo', 1, 1)], leaves=DK, colour='#D8D850')
tree('thuja', '北美香柏', 'Northern White Cedar', 'taiga', trunk='spruce', form='conifer', leaf=[('thuja_leaf', 1, 3)],
     leaves='#3A6A3A')
tree('lemon', '柠檬', 'Lemon', 'mediterranean', fruit=[('lemon', 1, 3)], leaf=[('lemon_leaf', 1, 3)], leaves=DK, colour='#F0E040')  # fruit: foods/23
tree('bergamot', '香柠檬', 'Bergamot', 'mediterranean', fruit=[('bergamot', 1, 2)], leaves=DK, colour='#C8D040')
tree('torreya', '香榧', 'Chinese Nutmeg Yew', 'east_asia', trunk='spruce', form='conifer', fruit=[('torreya_shell', 1, 2)],
     leaves='#2E5A2A', colour='#6A4A2A')
tree('blue_gum', '蓝桉', 'Blue Gum', 'savanna', trunk='birch', leaf=[('eucalyptus_leaf', 1, 3)], leaves='#7A9AA0')
tree('osmanthus', '桂花', 'Osmanthus', 'subtropical', flower=[('osmanthus_flower', 1, 3)], leaves=DK, bloom='#F0B030')
tree('longan', '龙眼', 'Longan', 'subtropical', fruit=[('longan', 1, 3)], leaves=DK, colour='#B08A50')
tree('pagoda_tree', '槐', 'Pagoda Tree', 'east_asia', flower=[('sophora_flower', 1, 3)], bloom='#F0F0D0')
tree('bitter_orange', '酸橙', 'Bitter Orange', 'mediterranean', flower=[('bitter_orange_flower', 1, 2)], fruit=[('bitter_orange', 1, 2)],
     leaf=[('bitter_orange_leaf', 1, 2)], leaves=DK, colour='#E87020')
tree('daidai', '玳玳', 'Daidai', 'subtropical', fruit=[('daidai_fruit', 1, 2)], leaves=DK, colour='#E88020')
tree('kola', '可乐树', 'Kola', 'jungle', trunk='jungle', fruit=[('kola_nut', 1, 2)], leaves=DK, colour='#C87050')
tree('peruvian_pepper', '加州胡椒树', 'Peruvian Pepper', 'savanna', trunk='acacia', fruit=[('peruvian_pepper_berry', 1, 3)],
     leaves='#6A8A50', colour='#D03050')
tree('ylang_ylang', '依兰', 'Ylang-ylang', 'jungle', trunk='jungle', flower=[('ylang_ylang_flower', 1, 3)], bloom='#E8E040')
tree('cananga', '卡南伽', 'Cananga', 'jungle', trunk='jungle', flower=[('cananga_flower', 1, 3)], bloom='#D8D850')
tree('bay_laurel', '月桂', 'Bay Laurel', 'mediterranean', leaf=[('laurel_leaf', 1, 3)], leaves=DK)
tree('pimento', '众香', 'Pimento', 'jungle', trunk='jungle', fruit=[('allspice', 1, 3)], leaf=[('pimento_leaf', 1, 2)], leaves=DK,
     colour='#5A3A2A')
tree('mandarin', '橘', 'Mandarin', 'subtropical', fruit=[('mandarin', 1, 2)], leaf=[('mandarin_leaf', 1, 2)], leaves=DK, colour='#F08020')
tree('tangerine', '红橘', 'Tangerine', 'subtropical', fruit=[('tangerine', 1, 2)], leaves=DK, colour='#E85A18')
tree('apricot', '杏', 'Apricot', 'east_asia', trunk='cherry', fruit=[('apricot_kernel', 1, 2)], bloom='#F8E0E8', colour='#F0A040')
tree('bitter_almond', '苦扁桃', 'Bitter Almond', 'mediterranean', fruit=[('bitter_almond', 1, 2)], bloom='#F8E8F0', colour='#B08A60')
tree('mimosa', '银荆', 'Silver Wattle', 'mediterranean', trunk='acacia', flower=[('mimosa_flower', 1, 3)], leaves='#7A9A90',
     bloom='#F0E020')
tree('mate', '巴拉圭冬青', 'Yerba Mate', 'jungle', leaf=[('mate_leaf', 1, 3)], leaves=DK)
tree('west_indian_bay', '西印度月桂', 'West Indian Bay', 'jungle', trunk='jungle', leaf=[('west_indian_bay_leaf', 1, 3)], leaves=DK)
tree('tea_tree', '互叶白千层', 'Tea Tree', 'wetland', trunk='birch', leaf=[('tea_tree_leaf', 1, 3)], leaves='#5A8A50')
tree('tangelo', '橘柚', 'Tangelo', 'mediterranean', fruit=[('tangelo', 1, 2)], leaves=DK, colour='#F07020')
tree('american_chestnut', '美国栗', 'American Chestnut', 'forest', leaf=[('chestnut_leaf', 1, 3)])
tree('yuzu', '香橙', 'Yuzu', 'east_asia', fruit=[('yuzu', 1, 2)], leaves=DK, colour='#F0C820')
tree('peach', '桃', 'Peach', 'east_asia', trunk='cherry', leaf=[('peach_leaf', 1, 3)], bloom='#F0A0C0')
tree('cajeput', '白千层', 'Cajeput', 'wetland', trunk='birch', leaf=[('cajeput_leaf', 1, 3)], leaves='#5A8A50')
# --- trees with their own log: bark (strip), wood (break), resin, gum and latex (tap)
tree('quillaja', '皂树', 'Soapbark', 'mediterranean', trunk='own', strip=[('quillaja_bark', 1, 2)], leaves=DK, bark='#6A5A4A')
tree('gum_ghatti', '达瓦树', 'Axlewood', 'savanna', trunk='own', tap=('ghatti_lump', 'item', 1), bark='#7A6A58')
tree('karaya', '刺梧桐', 'Karaya', 'savanna', trunk='own', tap=('karaya_lump', 'item', 1), bark='#9A8A78')
tree('gum_arabic', '阿拉伯胶树', 'Gum Arabic Tree', 'savanna', trunk='own', tap=('acacia_lump', 'item', 1), leaves='#6A8A50',
     bark='#5A4A3A')
tree('fig', '无花果', 'Fig', 'mediterranean', trunk='own', tap=('ficus_latex', 'fluid', 250), bark='#8A8A80')
tree('camphor', '樟', 'Camphor Tree', 'subtropical', trunk='own', wood=[('camphor_wood', 1, 2)], leaf=[('borneol_camphor_leaf', 1, 2)],
     leaves=DK, bark='#5A4A3A')
tree('chinese_cypress', '柏木', 'Chinese Weeping Cypress', 'east_asia', trunk='own', form='conifer', wood=[('cedarwood', 1, 2)],
     leaves='#3A5A3A', bark='#7A5A40')
tree('tolu', '吐鲁香脂树', 'Tolu Balsam Tree', 'jungle', trunk='own', tap=('tolu_balsam', 'item', 1), leaves=DK, bark='#6A5040')
tree('sugar_maple', '糖槭', 'Sugar Maple', 'forest', trunk='own', tap=('maple_syrup', 'bottled', 1), leaves='#5A8A3A', bark='#6A5A4A')
tree('guaiac', '愈疮木', 'Palo Santo', 'savanna', trunk='own', wood=[('guaiac_wood', 1, 2)], bark='#7A6A50')
tree('sandalwood', '檀香', 'Sandalwood', 'savanna', trunk='own', wood=[('sandalwood', 1, 2)], leaves=DK, bark='#5A4030')
tree('cascarilla', '卡黎', 'Cascarilla', 'jungle', trunk='own', strip=[('cascarilla_bark', 1, 2)], bark='#8A7A6A')
tree('oriental_sweetgum', '苏合香树', 'Oriental Sweetgum', 'mediterranean', trunk='own', tap=('styrax_resin', 'item', 1),
     bark='#5A4A3A')
tree('frankincense', '乳香树', 'Frankincense', 'desert', trunk='own', tap=('olibanum', 'item', 1), leaves='#6A8A50', bark='#A89A88',
     rarity=16)
tree('myrrh', '没药树', 'Myrrh', 'desert', trunk='own', tap=('myrrh', 'item', 1), leaves='#6A8A50', bark='#8A8070', rarity=16)
tree('pine', '松树', 'Pine', 'taiga', trunk='own', form='conifer', wood=[('pine_stump', 1, 1)], leaf=[('pine_needles', 1, 3)],
     leaves='#2E5A3A', bark='#6A4A30')
tree('rosewood', '玫瑰木', 'Rosewood', 'jungle', trunk='own', wood=[('rosewood', 1, 2)], leaves=DK, bark='#7A4A30')
tree('balsam_fir', '香脂冷杉', 'Balsam Fir', 'taiga', trunk='own', form='conifer', tap=('canada_balsam', 'item', 1),
     leaf=[('abies_needles', 1, 3)], leaves='#2E4A3A', bark='#5A5A50')
tree('cascara', '药鼠李', 'Cascara Buckthorn', 'forest', trunk='own', strip=[('cascara_bark', 1, 2)], bark='#6A5A50')
tree('peru_balsam', '秘鲁香脂树', 'Peru Balsam Tree', 'jungle', trunk='own', tap=('crude_peru_balsam', 'item', 1), leaves=DK,
     bark='#5A4030')
tree('amyris', '脂檀', 'Amyris', 'savanna', trunk='own', wood=[('amyris_wood', 1, 2)], bark='#8A7A60')
tree('black_cherry', '野黑樱桃', 'Black Cherry', 'forest', trunk='own', strip=[('wild_cherry_bark', 1, 2)], bloom='#F8F8F0', bark='#3A2A28')
tree('elemi', '吕宋橄榄', 'Elemi Tree', 'jungle', trunk='own', tap=('elemi', 'item', 1), leaves=DK, bark='#8A8070')
tree('hickory', '山核桃', 'Shagbark Hickory', 'forest', trunk='own', strip=[('hickory_bark', 1, 2)], bark='#5A5048')
tree('benzoin', '越南安息香', 'Siam Benzoin', 'jungle', trunk='own', tap=('benzoin_resin', 'item', 1), bark='#7A6A5A')
tree('quassia', '苦木', 'Quassia', 'jungle', trunk='own', wood=[('quassia_wood', 1, 2)], bark='#9A8A70')
tree('cinchona', '金鸡纳树', 'Cinchona', 'jungle', trunk='own', strip=[('cinchona_bark', 1, 2)], leaves=DK, bark='#8A5A40')
tree('mountain_maple', '穗花槭', 'Mountain Maple', 'forest', trunk='own', strip=[('mountain_maple_bark', 1, 2)], bark='#6A5A50')
tree('copaiba', '古巴香脂树', 'Copaiba', 'jungle', trunk='own', tap=('copaiba_balsam', 'item', 1), leaves=DK, bark='#7A6048')
tree('catechu', '儿茶', 'Catechu', 'savanna', trunk='own', wood=[('catechu_heartwood', 1, 2)], leaves='#6A8A50', bark='#4A3A30')
tree('angostura', '安古树', 'Angostura', 'jungle', trunk='own', strip=[('angostura_bark', 1, 2)], bark='#7A6A58')
tree('ceylon_cinnamon', '锡兰肉桂', 'Ceylon Cinnamon', 'jungle', trunk='own', strip=[('ceylon_cinnamon_bark', 1, 2)],
     leaf=[('ceylon_cinnamon_leaf', 1, 2)], leaves=DK, bark='#8A5A38')
tree('massoia', '香厚壳桂', 'Massoia', 'jungle', trunk='own', strip=[('massoia_bark', 1, 2)], leaves=DK, bark='#9A7A58')
# own fallbacks of raw materials that only other mods gave (foods/23 §4, 2026-11-25 user)
tree('cassia', '肉桂', 'Cassia', 'subtropical', trunk='own', strip=[('cassia_bark', 1, 2)], leaves=DK, bark='#6A4A30')
tree('walnut', '核桃', 'Walnut', 'forest', fruit=[('walnut', 1, 3)], colour='#8AA050')
tree('orange', '甜橙', 'Sweet Orange', 'subtropical', fruit=[('orange', 1, 3)], leaves=DK, colour='#F08A20')
tree('papaya', '番木瓜', 'Papaya', 'jungle', trunk='jungle', form='palm', fruit=[('papaya', 1, 2)], colour='#F0A040')
bush('grape', '葡萄', 'Grape', 'mediterranean', fruit=[('grape', 1, 3)], colour='#6A2A6A')
bush('tea', '茶', 'Tea', 'subtropical', leaf=[('tea_leaf', 1, 3)], leaves=DK, bloom='#F0F0E0')
bush('litsea', '山鸡椒', 'Litsea', 'subtropical', fruit=[('litsea_berry', 1, 3)], bloom='#F0E8B0', colour='#3A3A28')
tree('balata', '巴拉塔树', 'Bulletwood', 'jungle', trunk='own', tap=('balata_latex', 'fluid', 250), leaves=DK, bark='#6A4A38')
tree('jelutong', '节路顿', 'Jelutong', 'jungle', trunk='own', tap=('jelutong_latex', 'fluid', 250), bark='#9A9080')
tree('sorva', '来开欧', 'Sorva', 'jungle', trunk='own', tap=('sorva_latex', 'fluid', 250), bark='#7A7060')
tree('sapodilla', '人心果', 'Sapodilla', 'jungle', trunk='own', tap=('sapodilla_latex', 'fluid', 250), leaves=DK, bark='#6A5040')

# ------------------------------------------------------------------ other sources (handled apart, listed for the check)

# gardenia: the I07 gardenia bush gains a flowering stage (08 §C)
GARDENIA = {'flower': [['gardenia_flower', 1, 2]], 'fruit': [['gardenia_fruit', 1, 3]]}
# vanilla blocks of the same species (同一物种共用方块): acorns from oak leaves, seeds from carrots (loot added)
VANILLA_DROPS = [
    {'table': 'blocks/oak_leaves', 'item': 'acorn_shell', 'chance': 0.02, 'zh': '橡树树叶', 'en': 'oak leaves'},
    {'table': 'blocks/dark_oak_leaves', 'item': 'acorn_shell', 'chance': 0.02, 'zh': '深色橡树树叶', 'en': 'dark oak leaves'},
    {'table': 'blocks/carrots', 'item': 'carrot_seed', 'chance': 0.25, 'age': 7, 'block': 'minecraft:carrots',
     'zh': '成熟的胡萝卜', 'en': 'ripe carrots'},
]
# smoked plum (乌梅): ume plums smoked in a smoker (R08 fumigating, manual.ManualRecipes; 12 酸梅汤)
SMOKED = [{'input': 'plum', 'result': 'smoked_plum'}]
# insects (user 2026-11-19: 虫类改采集): scraped off their host with a knife (W02), the host resting 6,000 ticks after
SCRAPE = [
    {'item': 'sticklac', 'blocks': ['minecraft:jungle_leaves'], 'zh': '丛林树叶', 'en': 'jungle leaves'},
    {'item': 'dried_cochineal', 'blocks': ['minecraft:cactus'], 'zh': '仙人掌', 'en': 'cacti'},
]
# animals (user 2026-11-19: 兽类新增生物, on vanilla bodies): spawn weight and pack size as vanilla's ocelots and foxes
ANIMALS = [
    {'id': 'civet', 'body': 'OCELOT', 'zh': '灵猫', 'en': 'Civet', 'drop': ['civet', 1, 1], 'biomes': ['jungle', 'savanna'],
     'weight': 4, 'min': 1, 'max': 2, 'colour': '#8A8270', 'marks': '#2A2622'},
    {'id': 'beaver', 'body': 'FOX', 'zh': '河狸', 'en': 'Beaver', 'drop': ['castoreum', 1, 1], 'biomes': ['wetland'],
     'weight': 6, 'min': 1, 'max': 3, 'colour': '#6A4A32', 'marks': '#3A281C'},
    # foods/23 §5 (2026-11-25 user): a brown 麻鸭 on the chicken's body, laying duck eggs; spawn as vanilla chickens
    {'id': 'duck', 'body': 'CHICKEN', 'zh': '鸭', 'en': 'Duck', 'drop': ['raw_duck', 1, 1], 'biomes': ['wetland'],
     'weight': 10, 'min': 2, 'max': 4, 'colour': '#8A6A4A', 'marks': '#3A2E22', 'lays': 'duck_egg'},
]
# parts a weapon with “肢解” adds to the drops of an adult (foods/17 §1.1; every kill, counts as the table)
ORGANS = {
    'minecraft:cow': [['abomasum', 1], ['liver', 1]],
    'minecraft:mooshroom': [['abomasum', 1], ['liver', 1]],
    'minecraft:pig': [['pork_skin', 1], ['pork_hind_leg', 2], ['small_intestine', 1], ['gastric_mucosa', 1], ['pancreas', 1], ['liver', 1]],
    'minecraft:chicken': [['chicken_feet', 2]],
    'minecraft:sheep': [['small_intestine', 1]],
}
# ambergris (龙涎香): fishing treasure and shipwreck chests (08 §C: 战利品)
LOOT = [
    {'table': 'gameplay/fishing', 'item': 'ambergris', 'chance': 0.01},
    # foods/23 §5 (2026-11-25): shrimp now and then with a fish; oysters rarely (their source is the oyster reef)
    {'table': 'gameplay/fishing/fish', 'item': 'shrimp', 'chance': 0.2, 'zh': '钓鱼钓到鱼时有时一并钓到', 'en': 'Sometimes fished up with a fish'},
    {'table': 'gameplay/fishing/fish', 'item': 'oyster', 'chance': 0.05, 'zh': '', 'en': ''},
    {'table': 'chests/shipwreck_treasure', 'item': 'ambergris', 'chance': 0.15},
    {'table': 'chests/shipwreck_supply', 'item': 'ambergris', 'chance': 0.05},
]
# information pages written here instead of from the entries: straw also comes from the paddy of world.ModWorldBlocks
INFO = {'straw': ('收获成熟的稻谷、糯稻时一并得到', 'Comes with a ripe paddy or glutinous paddy harvest')}
# ores and deposits of I44 in world.ModWorldBlocks: block -> what it drops (for the no-source report)
DEPOSITS = {'ilmenite': 'ilmenite', 'pyrolusite': 'pyrolusite', 'attapulgite_deposit': 'attapulgite',
            'bentonite_deposit': 'bentonite_ore', 'perlite_deposit': 'perlite_ore', 'talc_deposit': 'talc_ore'}
# 08 §C categories handled by other code (I44): insects scraped with a knife, two new animals, organs, ores, cultures
OTHER = {
    'ilmenite': 'ore', 'pyrolusite': 'ore', 'attapulgite': 'ore', 'bentonite_ore': 'ore', 'perlite_ore': 'ore', 'talc_ore': 'ore',
    'spirulina': 'culture', 'dunaliella': 'culture', 'ambergris': 'loot', 'swallowroot': 'nest',
}


# ------------------------------------------------------------------ export and checks

def raw_materials():
    """The 08 §C rows whose 获取 names one of the six categories: id -> category letter."""
    text = (ROOT / 'docs' / 'additives' / '08-raw-materials.md').read_text(encoding='utf-8').split('\n')
    start = next(i for i, l in enumerate(text) if l.startswith('### 来源规则'))
    end = next(i for i, l in enumerate(text) if l.startswith('### 菌种与酶'))
    found = {}
    for line in text[start:end]:
        if not line.startswith('| ') or line.startswith('| ---'):
            continue
        cells = [c.strip() for c in line.strip('|').split('|')]
        if len(cells) < 8:
            continue
        source = cells[3]
        for prefix in ('本模组树木', '本模组作物', '野生采集', '实体掉落', '用带', '本模组矿石', 'M30'):
            if source.startswith(prefix):
                found[cells[6]] = prefix
    return found


def main():
    materials = {m['id']: m for m in json.loads(MATERIALS.read_text(encoding='utf-8'))['materials']}
    known = set(materials) | set(NEW_ITEMS) | {'gardenia_fruit'}  # gardenia fruit: an I07 item
    covered = {}

    def use(item, where):
        if item not in known:
            sys.exit(f'{where}: unknown item {item}')
        covered.setdefault(item, []).append(where)

    ids = set()
    for group in (CROPS, WILD, TREES):
        for entry in group:
            if entry['id'] in ids:
                sys.exit(f'duplicate species {entry["id"]}')
            ids.add(entry['id'])
    for c in CROPS:
        for item, low, high in c['drops']:
            use(item, c['id'])
    for w in WILD:
        if w['biome'] not in BIOMES:
            sys.exit(f'{w["id"]}: unknown biome group')
        for item, low, high in w['drops']:
            use(item, w['id'])
    for t in TREES:
        if t['biome'] not in BIOMES:
            sys.exit(f'{t["id"]}: unknown biome group')
        for part in ('flower', 'fruit', 'leaf', 'strip', 'wood'):
            for item, low, high in t[part]:
                use(item, t['id'])
        if t['tap']:
            use(t['tap'][0], t['id'])
        if t['form'] == 'bush' and (t['strip'] or t['wood'] or t['tap']):
            sys.exit(f'{t["id"]}: a bush has no trunk')
    for part in GARDENIA.values():
        for item, low, high in part:
            use(item, 'gardenia_bush')
    for item in sorted({part for parts in ORGANS.values() for part, count in parts}):
        use(item, 'organ')
    for scrape in SCRAPE:
        use(scrape['item'], 'scrape')
    for animal in ANIMALS:
        use(animal['drop'][0], 'animal ' + animal['id'])
        if animal.get('lays'):
            use(animal['lays'], 'animal ' + animal['id'])
    for loot in LOOT:
        if loot['item'] in NEW_ITEMS and loot['item'] != 'oyster':  # the oyster's source is the reef (world.ModWorldBlocks)
            use(loot['item'], 'loot')
    for item in sorted({drop['item'] for drop in VANILLA_DROPS}):
        use(item, 'vanilla')
    for smoked in SMOKED:
        use(smoked['result'], 'smoking')
    for item in OTHER:
        if item in materials or item in ('ilmenite', 'pyrolusite'):
            covered.setdefault(item, []).append(OTHER[item])
    for item in INFO:
        if item not in known:
            sys.exit(f'info page: unknown item {item}')
    raw = raw_materials()
    missing = sorted(set(raw) - set(covered))
    if missing:
        sys.exit(f'raw materials without a source: {missing}')
    twice = sorted(item for item, where in covered.items() if len(where) > 1)
    if twice:
        sys.exit(f'items with two sources: {twice}')
    data = {
        'biomes': {k: {'biomes': v[0], 'zh': v[1], 'en': v[2]} for k, v in BIOMES.items()},
        'villages': VILLAGES,
        'items': {k: {'zh': v[0], 'en': v[1], 'tag': v[2], 'texture': v[3], 'colour': v[4]} for k, v in NEW_ITEMS.items()},
        'extra_tags': EXTRA_TAGS,
        'crops': CROPS, 'wild': WILD, 'trees': TREES,
        'gardenia': GARDENIA, 'vanilla_drops': VANILLA_DROPS, 'smoked': SMOKED, 'loot': LOOT, 'linked': LINKED, 'scrape': SCRAPE, 'organs': ORGANS, 'animals': ANIMALS, 'deposits': DEPOSITS,
        'info': {k: {'zh': v[0], 'en': v[1]} for k, v in INFO.items()},
    }
    OUT.write_text(json.dumps(data, ensure_ascii=False, indent=1) + '\n', encoding='utf-8')
    bushes = sum(1 for t in TREES if t['form'] == 'bush')
    own = sum(1 for t in TREES if t['trunk'] == 'own')
    print(f'{len(CROPS)} crops, {len(WILD)} wild herbs, {len(TREES) - bushes} trees ({own} with their own log), {bushes} bushes; '
          f'{len(raw)} raw materials of 08 §C covered -> {OUT.relative_to(ROOT)}')


BODIES = {'OCELOT': '豹猫', 'FOX': '狐狸', 'CHICKEN': '鸡'}  # the vanilla bodies of world.PlatformAnimals, for the list


def markdown():
    """docs/implementations/lists/I44-generation-list.md: the list as tables for review (--markdown)."""
    materials = {m['id']: m['name']['zh'] for m in json.loads(MATERIALS.read_text(encoding='utf-8'))['materials']}
    names = {**materials, **{k: v[0] for k, v in NEW_ITEMS.items()}, 'gardenia_fruit': '栀子果'}

    def parts(drops):
        return '、'.join(f'{names.get(i, i)} {lo}–{hi}' if lo != hi else f'{names.get(i, i)} {lo}' for i, lo, hi in drops)

    level = {1: 2, 2: 3, 3: 4, 4: 5, 5: 5}
    climate = {'temperate': '温带（平原、针叶林村庄）', 'cold': '寒带（针叶林、雪原村庄）', 'arid': '干旱（沙漠村庄）',
               'tropical': '热带（热带草原村庄）'}
    out = ['# I44 生成清单', '', '由 `tools/world/sources.py` 生成（`python3 tools/world/sources.py --markdown`），数值为实装草案，待审。'
           '群系组见文末。', '']
    out += ['## 作物（产物即种子，2026-11-19 用户定）', '', '成熟时掉落下列产物（第一个用于栽种）；农民出售该产物（第一列等级），村庄箱子按气候放入。', '',
            '| 作物 | ID | 成熟掉落 | 农民等级 | 村庄 | 野生 |', '| --- | --- | --- | --- | --- | --- |']
    for c in CROPS:
        out.append(f"| {c['zh']} | `{c['id']}_crop` | {parts(c['drops'])} | {level[c['level']]} | {climate[c['climate']]} | "
                   f"{BIOMES[c['wild']][1] if c['wild'] else '—'} |")
    out += ['', '## 野生草本', '', '长成（4 阶段的最后一阶）后右键采集，回到第 2 阶；破坏也给产物；剪刀连株取下。', '',
            '| 植物 | 方块 ID | 产物 | 群系 | 稀有度（每 N 区块一簇） | 沙地 |', '| --- | --- | --- | --- | --- | --- |']
    for w in WILD:
        out.append(f"| {w['zh']} | `wild_{w['id']}` | {parts(w['drops'])} | {BIOMES[w['biome']][1]} | {w['rarity']} | "
                   f"{'是' if w['sand'] else '—'} |")
    out += ['', '## 树木与灌木', '', '开花（第 3 阶）空手采花，结果（第 4 阶）空手采果，剪刀采叶（第 4 阶）；只有花的树种在第 4 阶采花。'
            '自有原木：斧去皮得皮，砍下得木，持刀割取得树脂、胶乳（胶乳与枫糖浆副手拿玻璃瓶）。', '',
            '| 树种 | 形态 | 树干 | 花 | 果 | 叶（剪刀） | 皮 / 木 / 割取 | 群系 | 稀有度 |',
            '| --- | --- | --- | --- | --- | --- | --- | --- | --- |']
    form = {'tree': '乔木', 'conifer': '针叶乔木', 'palm': '棕榈', 'bush': '灌木丛'}
    trunk = {'oak': '橡木', 'birch': '白桦', 'spruce': '云杉', 'jungle': '丛林', 'acacia': '金合欢', 'dark_oak': '深色橡木',
             'cherry': '樱花', 'own': '自有原木'}
    for t in TREES:
        trunk_parts = []
        if t['strip']:
            trunk_parts.append('皮 ' + parts(t['strip']))
        if t['wood']:
            trunk_parts.append('木 ' + parts(t['wood']))
        if t['tap']:
            unit = ' mB' if t['tap'][1] == 'fluid' else ''
            trunk_parts.append(f"割 {names.get(t['tap'][0], t['tap'][0])} {t['tap'][2]}{unit}")
        out.append(f"| {t['zh']} | {form[t['form']]} | {'—' if t['form'] == 'bush' else trunk[t['trunk']]} | {parts(t['flower']) or '—'} | "
                   f"{parts(t['fruit']) or '—'} | {parts(t['leaf']) or '—'} | {'；'.join(trunk_parts) or '—'} | {BIOMES[t['biome']][1]} | "
                   f"1/{t['rarity']} |")
    out += ['', '## 其他来源', '',
            f"- 栀子丛（I07）：第 3 阶采{parts(GARDENIA['flower'])}，第 4 阶采{parts(GARDENIA['fruit'])}",
            '- 原版同种：' + '；'.join(f"{d['zh']} {int(d['chance'] * 100)}% 掉落{names[d['item']]}" for d in VANILLA_DROPS),
            '- 乌梅：梅子在烟熏炉熏制（R08）',
            '- 虫类刮取（持刀右键，休息 6,000 tick）：' + '；'.join(f"{s['zh']}→{names[s['item']]}" for s in SCRAPE),
            '- 燕巢（山地崖壁，Y 64–192）：持刀刮取燕根（休息 12,000 tick），挖下得燕根 1–2',
            '- 新生物：' + '；'.join(f"{a['zh']}（{BODIES[a['body']]}体型，"
                                    f"{'、'.join(BIOMES[b][1] for b in a['biomes'])}，权重 {a['weight']}，每群 {a['min']}–{a['max']}）"
                                    f"掉落{names[a['drop'][0]]}" + (f"，下{names[a['lays']]}" if a.get('lays') else '') for a in ANIMALS),
            '- 肢解（击杀成年个体，每次）：' + '；'.join(f"{k.split(':')[1]}：" + '、'.join(f'{names.get(i, i)} ×{n}' for i, n in v)
                                          for k, v in ORGANS.items()),
            '- 战利品：' + '；'.join(f"{l['table']} {l['chance'] * 100:g}% {names[l['item']]}" for l in LOOT),
            '']
    out += ['## 群系组', '', '| 组 | 群系 |', '| --- | --- |']
    for key, (biomes, zh, en) in BIOMES.items():
        out.append(f"| {zh} | {'、'.join(b.split(':')[1] for b in biomes)} |")
    path = ROOT / 'docs' / 'implementations' / 'lists' / 'I44-generation-list.md'
    path.write_text('\n'.join(out) + '\n', encoding='utf-8')
    print('generation list ->', path.relative_to(ROOT))


if __name__ == '__main__':
    main()
    if '--markdown' in sys.argv:
        markdown()
