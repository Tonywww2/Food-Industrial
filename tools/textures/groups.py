"""Appearance of the second batch (I33, approved 2026-11-12): cultures, enzymes, flavours, raw materials,
catalysts and finished products. Each function gives {'kind', 'kind_reason', 'color', 'color_source'} and, for
cultures, 'badge'. Colours: the material's own colour (09 / 08) first, then names, then the kind's default.
"""
import json
import pathlib

import appearance

ROOT = pathlib.Path(__file__).resolve().parents[2]
FLAVORS = ROOT / 'src' / 'main' / 'resources' / 'foodindustrial' / 'flavors.json'


def first(name, table):
    """(value, word) of the entry of `table` with the longest word found in `name` (the earliest of equally long
    ones), so that 葡萄糖浆 wins over 葡萄 and 椒盐坚果 takes 坚果 rather than 盐."""
    best = None
    for word, value in table:
        if word in name and (best is None or len(word) > len(best[1])):
            best = (value, word)
    return best or (None, None)


def result(kind, why, colour, source, **extra):
    return {'kind': kind, 'kind_reason': why, 'color': colour, 'color_source': source, **extra}


def named(m, table, default, kind_default_name):
    if m.get('color'):
        return m['color'], '09 / 08'
    value, word = first(m['name']['zh'], table)
    if value:
        return value, f'名称：{word}'
    return default, f'{kind_default_name}默认色'


# ---- cultures ----

MOLD = ('曲霉', '根霉', '红曲霉', '木霉', '青霉', '毛霉', '被孢霉', '镰刀菌', '毛壳菌', '囊霉', '布拉霉', '二孢', '食用菌', '菌丝体', '杂菌')
YEAST = ('酵母', '短梗霉', '壶藻')
ENGINEERED = ('工程菌', '重组')
CULTURE_COLOURS = (
    ('红曲霉', '#B0281A'), ('黑曲霉', '#2A2A28'), ('斐济曲霉', '#2A2A28'), ('米曲霉', '#B8C060'), ('橘青霉', '#4A8A7A'),
    ('木霉', '#3A8A3A'), ('米黑根毛霉', '#6A6A60'), ('根霉', '#9A9A90'), ('布拉霉', '#E8801A'), ('阿舒囊霉', '#F0C020'),
    ('毛壳菌', '#8A8A50'), ('二孢', '#3A3A38'), ('被孢霉', '#F2F0E8'), ('镰刀菌', '#D890B0'), ('食用菌', '#F4F2EC'),
    ('黄单胞菌', '#F0D040'), ('鞘氨醇单胞菌', '#F0C030'), ('红色精朊杆菌', '#E06040'), ('荧光假单胞菌', '#D8E8A0'),
    ('金黄杆菌', '#F0B020'), ('褐黄孢', '#A07040'), ('锈棕色', '#8A4A20'), ('白色链霉菌', '#F0EEE6'), ('链轮丝菌', '#D8D0C0'),
    ('出芽短梗霉', '#3A3430'), ('壶藻', '#E8B060'), ('杂菌', '#8A9A6A'))
CULTURE_DEFAULTS = {'culture_bacteria': '#F4EEDC', 'culture_yeast': '#F2E8D0', 'culture_mold': '#F0F0EA'}


def culture(m):
    zh = m['name']['zh']
    if any(w in zh for w in MOLD) and '酵母' not in zh and '链霉菌' not in zh and '短梗霉' not in zh:
        kind, why = 'culture_mold', '霉菌'
    elif any(w in zh for w in YEAST):
        kind, why = 'culture_yeast', '酵母'
    else:
        kind, why = 'culture_bacteria', '细菌（默认）'
    engineered = any(w in zh for w in ENGINEERED) or zh.startswith('产')
    colour, source = named(m, CULTURE_COLOURS, CULTURE_DEFAULTS[kind], '菌种')
    return result(kind, why + ('，工程菌标记' if engineered else ''), colour, source, badge='engineered' if engineered else None)


# ---- enzymes ----

POWDER_ENZYMES = ('木瓜蛋白酶', '菠萝蛋白酶', '无花果蛋白酶', '凝乳酶', '胃蛋白酶', '胰蛋白酶', '胰凝乳蛋白酶')
ENZYME_COLOURS = (('漆酶', '#4A7AC8'), ('葡萄糖氧化酶', '#E8C040'), ('木瓜蛋白酶', '#F0E8D0'), ('胃蛋白酶', '#E8D8B0'),
)


def enzyme(m):
    zh = m['name']['zh']
    if zh in ('麦芽', '大麦麦芽'):
        kind, why, default = 'grain', '麦芽', '#C8A050'
    elif any(w in zh for w in POWDER_ENZYMES):
        kind, why, default = 'enzyme_powder', '动植物来源的酶粉', '#EDE4CC'
    else:
        kind, why, default = 'enzyme_liquid', '液体酶制剂（默认）', '#B8862E'
    colour, source = named(m, ENZYME_COLOURS if kind != 'grain' else (), default, '酶')
    return result(kind, why, colour, source)


# ---- flavours ----

_TEMPLATES = None


def flavor_template(mid):
    global _TEMPLATES
    if _TEMPLATES is None:
        data = json.loads(FLAVORS.read_text(encoding='utf-8'))
        _TEMPLATES = {}
        for route in data['routes']:
            found = [t for v in route['variants'] for t in v['templates']]
            if found:
                _TEMPLATES.setdefault(route['id'], found[0])
    return _TEMPLATES.get(mid, '')


FLAVOR_POWDER = ('苷', '氨酸', '糖', '盐', '粉', '胶', '蛋白', '肽', '素钠')


def flavor(m):
    zh, template = m['name']['zh'], flavor_template(m['id'])
    if m['state'] != 'solid':
        if m['state'] == 'gas':
            kind, why = 'gas', '气体'
        elif template in ('NT5', 'NT6', 'NT8', 'NT4') or '流浸膏' in zh or appearance.fluid_kind(m)[0] == 'viscous':
            kind, why = 'viscous', '净油、油树脂、浸膏、香脂'
        else:
            kind, why = 'liquid', '精油、液体香料、酊与提取物'
    elif template in ('NT4', 'NT5') or any(w in zh for w in ('鸢尾脂', '苍术脂', '凝脂')):
        kind, why = 'paste', '浸膏、凝脂'
    elif template == 'NT8':
        kind, why = ('flakes', '胶片') if '胶' in zh else ('resin', '固体香脂与树脂')
    elif template in ('NT7', 'NT14', 'NT10') or any(w in zh for w in FLAVOR_POWDER):
        kind, why = 'powder', '提取物、干品粉末、苷与氨基酸'
    elif appearance.solid_kind(m)[0] == 'flakes':
        kind, why = 'flakes', '蜡状的脂肪酸、酯与醇'
    else:
        kind, why = 'crystal', '合成香料与结晶单体（默认）'
    colour, source = appearance.colour(m)
    return result(kind, why, colour, source)


# ---- raw materials ----

RAW_EXACT = {'花椒': 'seed', '决明子': 'seed', '白芥子': 'seed', '花生红衣': 'pieces', '丁香花梗': 'pieces', '柑橘皮': 'pieces',
             '青核桃皮': 'pieces', '香蜂花': 'leaf', '印蒿': 'leaf', '黑加仑芽': 'leaf', '金樱子': 'fruit', '枫糖浆': 'bottle',
             '小米辣': 'fruit', '剑叶金鸡菊花序': 'flower', '红三叶草花序': 'flower'}
# checked in order: the first kind with a word in the name wins
RAW_KINDS = (
    ('metal', ('铬',)),
    ('culture_mold', ('杂菌', '菌丝体')),
    ('culture_yeast', ('啤酒酵母',)),
    ('grain', ('小麦', '麦芽')),
    ('powder', ('粉', '死烧石膏')),
    ('lump', ('矿', '粗硒', '胶乳凝块', '龙涎香')),
    ('flakes', ('胶片',)),
    ('resin', ('原脂', '香脂', '胶块', '原胶', '乳香', '没药', '安息香', '格蓬', '阿魏', '榄香脂')),
    ('tissue', ('鱼肝', '蛋黄', '蛋清', '胰脏', '胃黏膜', '肝脏', '皱胃', '香囊', '灵猫香')),
    ('bark', ('树皮', '桂皮', '伐根', '茎秆', '心材')),
    ('pieces', ('渣', '壳', '种皮', '糠', '碎', '核', '干片', '霉变', '胭脂虫', '鲣节', '椰子干', '果肉', '酒脚', '干辣椒')),
    ('mushroom', ('香菇', '食用菌')),
    ('leaf', ('叶', '针', '枝')),
    ('flower', ('花', '杭白菊', '依兰', '薰衣草', '金钮扣', '柱头')),
    ('root', ('根', '姜', '苍术', '香附', '大蒜', '细辛')),
    ('bark', ('皮', '木', '茎')),
    ('seed', ('籽', '种子', '豆', '仁', '米', '麦', '胡椒', '八角', '豆蔻', '杜松子', '荜澄茄', '孜然', '众香果', '可乐果')),
    ('fruit', ('果', '柚', '柠檬', '橘', '橙', '佛手', '西瓜', '山楂', '枣', '桑椹', '杨梅', '黑加仑', '越橘', '龙眼', '乌梅')),
    ('leaf', ('草', '薄荷', '罗勒', '欧芹', '莳萝', '龙蒿', '百里香', '牛至', '海索草', '芸香', '苦艾', '蒲公英', '苜蓿', '留兰香',
              '香茶菜', '飞蓬', '红茶', '树苔', '橡苔', '韭葱', '细香葱', '香茅', '鼠尾草', '香薄荷', '芹菜', '紫苏', '天竺葵', '香紫苏')),
)
RAW_DEFAULTS = {'bottle': '#A0601A', 'leaf': '#4A7A3A', 'flower': '#E8C0D0', 'seed': '#B89060', 'root': '#A07850', 'bark': '#7A5030',
                'fruit': '#D08030', 'mushroom': '#9A7050', 'tissue': '#D89080', 'pieces': '#A07850', 'resin': '#C8902A',
                'lump': '#A8A098', 'powder': '#E0D0B0', 'flakes': '#E8DCC0', 'metal': '#A8B0B8', 'grain': '#D8B070',
                'culture_mold': '#2A2A28', 'culture_yeast': '#E8D8B0'}
RAW_COLOURS = (
    # flowers
    ('玫瑰花', '#C02040'), ('墨红', '#8A1020'), ('野蔷薇', '#E8A0B0'), ('茉莉', '#F4F4EC'), ('素方花', '#F4F4EC'),
    ('桂花', '#F0A030'), ('薰衣草', '#8A6AC0'), ('杭白菊', '#F2EEDC'), ('母菊', '#F2EEDC'), ('春黄菊', '#F2EEDC'),
    ('万寿菊', '#F08A10'), ('金合欢', '#F0D020'), ('银荆', '#F0D020'), ('晚香玉', '#F4F0E4'), ('栀子花', '#F4F0E0'),
    ('依兰', '#E8E060'), ('卡南伽', '#E8E060'), ('苦橙花', '#F4F0E8'), ('接骨木花', '#F2EED8'), ('槐花', '#F0ECC8'),
    ('红花', '#E06020'), ('波罗尼', '#9A4A6A'), ('白兰花', '#F4F0E4'), ('树兰花', '#F0E070'), ('九里香花', '#F4F0E4'),
    ('蚕豆花', '#C8A8D0'), ('芹菜花', '#F0F0E0'), ('红三叶草', '#C04A80'), ('剑叶金鸡菊', '#F0C020'), ('鹰爪豆花', '#F0D020'),
    ('玫瑰茄', '#9A1030'), ('啤酒花', '#A8C060'), ('藏红花', '#C02010'), ('金钮扣', '#E8C020'), ('蒲公英', '#E8D020'),
    # leaves
    ('红茶', '#3A2A1A'), ('巴拉圭茶', '#6A7A3A'), ('甜菊', '#7A9A4A'), ('树苔', '#9AA88A'), ('橡苔', '#9AA88A'),
    ('芦荟', '#5A8A4A'), ('紫苏叶', '#6A3A5A'),
    # seeds, spices
    ('黑胡椒', '#2A2420'), ('白胡椒', '#E8DCC0'), ('白芥子', '#E8D070'), ('芥菜籽', '#8A5A2A'), ('亚麻籽', '#8A5A2A'),
    ('咖啡生豆', '#A8B088'), ('焙炒咖啡豆', '#3A2414'), ('可可', '#4A2A1A'), ('小豆蔻', '#8AA060'), ('八角', '#6A3A1A'),
    ('丁香', '#5A2A1A'), ('杜松子', '#2A3A5A'), ('花椒', '#8A2A1A'), ('胭脂树籽', '#B02A1A'), ('黄葵籽', '#6A4A2A'),
    ('罗望子', '#6A3A1A'), ('瓜拉纳', '#3A2414'), ('可乐果', '#C8A070'), ('荜澄茄', '#3A2A20'), ('众香果', '#5A3A2A'),
    ('胡椒果', '#C04060'), ('香荚兰', '#2A1A10'), ('角豆', '#6A3A1A'), ('杏仁', '#C8A070'), ('苦扁桃仁', '#C8A070'),
    ('大米', '#F4F0E4'), ('蒸米', '#F6F2EA'), ('小麦', '#D8B070'), ('麦', '#D8B070'), ('核桃', '#3A5A2A'),
    # roots
    ('生姜', '#D8B070'), ('姜黄', '#E8A020'), ('甘草', '#C8A060'), ('紫草', '#4A1A3A'), ('鸢尾', '#E8DCC0'),
    ('菊苣', '#8A6A4A'), ('黄龙胆', '#A07030'), ('大蒜', '#F0EADC'), ('辣根', '#E8DCC0'), ('山葵', '#8AB070'),
    # barks, woods
    ('肉桂', '#A0602A'), ('檀香', '#D0A060'), ('白栎', '#C8A070'), ('柏木', '#C8906A'), ('愈疮木', '#6A5A3A'),
    ('樟', '#B08A5A'), ('玫瑰木', '#B0703A'), ('儿茶', '#6A2A1A'), ('金鸡纳', '#8A4A2A'),
    # fruits
    ('白柠檬', '#9AC040'), ('柠檬', '#F0E040'), ('葡萄柚', '#F0A060'), ('柑橘', '#F08A20'), ('橘', '#F08A20'),
    ('橙', '#F08A20'), ('佛手', '#F0D040'), ('香柠檬', '#C8D040'), ('柚', '#F0D060'), ('玳玳', '#F09020'),
    ('西瓜', '#3A8A3A'), ('山楂', '#C02020'), ('枣', '#8A1A1A'), ('桑椹', '#3A1A2A'), ('黑加仑', '#2A1A2A'),
    ('杨梅', '#8A1A2A'), ('越橘', '#A01830'), ('沙棘', '#F08A10'), ('金樱子', '#D0601A'), ('玫瑰果', '#C0301A'),
    ('罗汉果', '#6A4A2A'), ('龙眼', '#C8A060'), ('油棕果', '#C04010'), ('乌梅', '#2A1A1A'), ('椰子', '#F0EAD8'),
    ('酸角', '#6A3A1A'), ('竹芋果', '#C0301A'), ('蓝锭果', '#3A2A5A'), ('番茄', '#C02818'),
    # animal and others
    ('鱼肝', '#8A3A2A'), ('熟蛋黄', '#F0C040'), ('蛋黄', '#F0B020'), ('蛋清', '#F0F0E8'), ('胰脏', '#E0B0A0'),
    ('胃黏膜', '#E0A0A0'), ('肝脏', '#6A1A1A'), ('皱胃', '#E8C8B8'), ('香囊', '#6A4A2A'), ('灵猫香', '#5A4A2A'),
    ('龙涎香', '#B0A890'), ('鲣节', '#8A5A3A'), ('胭脂虫', '#6A2A3A'), ('虾蟹壳', '#E07040'), ('胶乳', '#F0ECD8'),
    ('湿胶片', '#E8E0C0'), ('香菇', '#8A5A3A'), ('食用菌', '#D8C8B0'),
    # resins and gums
    ('乳香', '#E8D8A0'), ('没药', '#8A4A1A'), ('安息香', '#C8A070'), ('格蓬', '#B0A060'), ('阿魏', '#A08050'),
    ('榄香', '#E8E0B0'), ('吐鲁', '#8A4A1A'), ('秘鲁', '#3A1A10'), ('苏合香', '#8A6A3A'), ('加拿大香脂', '#E8D080'),
    ('古巴香脂', '#C89040'), ('阿拉伯胶', '#E8C870'), ('刺梧桐胶', '#E0D0B0'), ('达瓦树胶', '#D8C080'), ('黄蓍胶', '#F0E8D0'),
    ('紫胶', '#A0501A'),
    # pomace, husks, powders, minerals
    ('番茄皮渣', '#C0301A'), ('葡萄皮渣', '#5A1A3A'), ('可可豆壳', '#6A4020'), ('橡子壳', '#7A5030'), ('高粱壳', '#8A3A2A'),
    ('黑豆种皮', '#2A1A2A'), ('红米糠', '#8A2A2A'), ('米糠', '#D8C8A0'), ('豆渣', '#F0E6D0'), ('焦糖渣', '#5A2A10'),
    ('花生红衣', '#8A2A2A'), ('柑橘皮', '#F08A20'), ('山楂核', '#A07850'), ('香榧子壳', '#6A4A2A'), ('霉变姜黄', '#6A6A40'),
    ('栀子', '#E0801A'), ('青核桃皮', '#3A5A2A'), ('辣椒', '#B0281A'), ('螺旋藻', '#2A5A3A'), ('盐藻', '#C08A2A'),
    ('玉米蛋白', '#E8C040'), ('巴西棕榈蜡', '#D8C080'), ('死烧石膏', '#F2F0EA'), ('凹凸棒', '#C8B898'), ('膨润土', '#D8D0B8'),
    ('珍珠岩', '#A0A0A8'), ('滑石', '#E8E8E0'), ('粗硒', '#6A3A3A'), ('铬', '#A8B0B8'), ('杂菌', '#8A9A6A'),
    ('黑曲霉', '#2A2A28'), ('啤酒酵母', '#D8C090'), ('富硒大豆', '#E8D090'), ('树皮', '#6A4A30'), ('枫糖浆', '#A0601A'),
    ('酒脚', '#5A1A2A'), ('决明子', '#6A4A20'), ('印蒿', '#7A8A5A'), ('香蜂花', '#5A8A3A'), ('小米辣', '#C0201A'))


def raw(m):
    zh = m['name']['zh']
    if m['state'] != 'solid':
        kind, why = appearance.fluid_kind(m)
        if any(w in zh for w in ('胶乳', '乳胶', '乳汁', '松脂', '毛油', '原油', '大豆油', '血')):
            kind, why = 'viscous', '胶乳、油脂、松脂、血'
        colour, source = (m['color'], '09 / 08') if m.get('color') else ('#C8A070', '原料默认色')
        return result(kind, why, colour, source)
    kind, why = 'pieces', '原料（默认）'
    if zh in RAW_EXACT:
        kind, why = RAW_EXACT[zh], '逐个指定'
    else:
        for name, words in RAW_KINDS:
            if any(w in zh for w in words):
                kind, why = name, f'名称含 {next(w for w in words if w in zh)}'
                break
    colour, source = named(m, RAW_COLOURS, RAW_DEFAULTS[kind], '种类')
    return result(kind, why, colour, source)


# ---- catalysts ----

CATALYST_KINDS = (('beads', ('树脂', '固定化', '聚苯乙烯')), ('mesh', ('铂网',)),
                  ('powder', ('黏土', '白土', '膨润土', '珍珠岩', '硅藻土', '钯碳', '金 / 钯', '三氯化铝', '钨酸钠', '二丁基氧化锡',
                              '五氧化二钒', '钼酸铋', '复分解', '聚吡咯烷酮', '乙酸钴')))
CATALYST_COLOURS = (('活性炭', '#1A1A1A'), ('镍', '#6A7A6A'), ('五氧化二钒', '#E08A1A'), ('钼酸铋', '#E8D040'), ('铜', '#5A3A2A'),
                    ('固定化', '#E0C890'), ('凹凸棒', '#C8B898'), ('膨润土', '#D8D0B8'), ('珍珠岩', '#F2F2F0'), ('硅藻土', '#F0EEE6'),
                    ('铁', '#3A3A3A'), ('铑碘', '#8A2A2A'), ('铂网', '#D0D4D8'), ('金 / 钯', '#8A7A5A'), ('钯-离子交换', '#5A4A3A'),
                    ('离子交换树脂', '#C8781A'), ('大孔吸附树脂', '#F2EEE0'), ('银', '#D8DAE0'), ('钒磷氧', '#3A4A3A'),
                    ('钯碳', '#1A1A1A'), ('复分解', '#6A3A6A'), ('聚苯乙烯', '#F4F4F0'), ('活性白土', '#E8E0D0'))


def catalyst(m):
    zh = m['name']['zh']
    kind, why = 'pellets', '金属载体催化剂与颗粒（默认）'
    for name, words in CATALYST_KINDS:
        if any(w in zh for w in words):
            kind, why = name, f'名称含 {next(w for w in words if w in zh)}'
            break
    colour, source = named(m, CATALYST_COLOURS, '#C8C8C4', '催化剂')
    return result(kind, why, colour, source)


def material(m):
    """Appearance of any material of materials.json."""
    category = m['category']
    if category == 'chemical':
        return appearance.appearance(m)
    if category == 'culture':
        return culture(m)
    if category == 'enzyme':
        return enzyme(m)
    if category == 'flavor':
        return flavor(m)
    if category == 'catalyst':
        return catalyst(m)
    if category == 'consumable':
        return result('enzyme_liquid', '表达载体（质粒溶液）', '#7AD0E0', '耗材默认色')
    return raw(m)


# ---- finished products ----

PRODUCT_KINDS = (
    ('stick_treat', ('棒棒糖', '冰糖葫芦', '雪糕', '冰棍')),
    ('candy', ('软糖', '酒心')),
    ('fish', ('糖醋鱼',)),
    ('block', ('高汤块', '再制奶酪片', '凉粉', '肉冻', '鱼皮冻', '鱼豆腐', '豆干', '年糕', '方糖', '奶片')),
    ('pieces', ('蜜饯苹果片', '椒盐坚果', '大豆素肉')),
    ('bag', ('婴儿米粉',)),
    ('noodles', ('米粉', '河粉', '粉丝', '粉条')),
    ('egg', ('咸鸭蛋',)),
    ('bread', ('面包', '甜甜圈')),
    ('soda_can', ('汽水', '可乐', '苏打水', '气泡水', '能量饮料', '罐装咖啡')),
    ('tin_can', ('罐头',)),
    ('jar_chunks', ('泡菜', '酸菜', '榨菜', '雪菜', '腌黄瓜', '醋渍', '糖醋胡萝卜', '腌蘑菇', '腐乳', '糖渍')),
    ('jar', ('果酱', '苹果酱', '豆瓣酱', '甜面酱', '黄豆酱', '番茄酱', '沙拉酱', '蛋黄酱', '芥末酱', '辣椒酱', '花生酱', '芝麻酱',
             '虾酱', '炼乳', '胡萝卜泥', '火锅底料')),
    ('bottle', ('酱油', '生抽', '老抽', '醋', '料酒', '蚝油', '卤汁', '辣椒油', '花椒油', '鱼露', '高汤', '植物油', '花生油', '芝麻油',
                '糖浆', '酒', '白兰地', '威士忌', '伏特加', '汁', '柠檬水', '酸梅汤', '凉茶', '红茶', '绿茶饮料', '豆奶', '核桃露', '饮料',
                '饮品', '维生素水', '蜂蜜柠檬茶', '姜茶', '淡奶油')),
    ('cup', ('酸奶', '冰淇淋', '冰沙', '刨冰', '红豆冰', '布丁', '果冻', '双皮奶', '姜撞奶', '豆花', '龟苓膏', '杏仁豆腐', '奶茶',
             '能量胶', '爆爆珠', '椰果')),
    ('bag', ('粉', '盐', '白糖', '红糖', '代糖', '片', '爆米花', '锅巴', '米饼', '跳跳糖', '燕麦', '谷物', '方便面', '脱水', '肉松',
             '茶包', '鸡精', '十三香', '咖啡', '冲剂', '干酵母', '植脂末', '珍珠粉圆')),
    ('noodles', ('面', '凉皮', '腐竹', '龙须酥', '海带丝')),
    ('bun', ('饺子', '馄饨', '烧麦', '汤圆', '包子', '馒头', '花卷', '月饼', '蛋黄酥', '泡芙', '丸', '狮子头', '粽', '发糕')),
    ('bread', ('吐司', '法棍', '牛角包', '贝果', '碱水结')),
    ('cake', ('蛋糕', '蛋挞', '派', '玛芬', '司康')),
    ('biscuit', ('饼干', '曲奇', '威化', '桃酥', '姜饼', '华夫饼', '铜锣烧', '海苔', '油豆皮')),
    ('fried_stick', ('油条', '麻花', '馓子', '蟹肉棒', '蛋卷')),
    ('candy', ('糖', '巧克力豆', '酒心')),
    ('bar', ('巧克力', '牛轧', '太妃', '焦糖块', '猪肉脯', '牛肉干')),
    ('sausage', ('肠', '热狗')),
    ('poultry', ('鸭', '鸡')),
    ('fish', ('鱼', '三文鱼')),
    ('egg', ('蛋',)),
    ('meat', ('肉', '火腿', '培根', '叉烧')),
    ('pieces', ('干', '话梅', '瓜子', '坚果', '核桃', '豆豉', '纳豆', '海带结')),
    ('block', ('奶酪', '黄油', '起酥油', '猪油', '牛油', '豆腐', '方糖', '冰糖', '块', '奶豆腐', '冻')),
)
PRODUCT_DEFAULTS = {'bottle': '#A06A2A', 'soda_can': '#C83030', 'tin_can': '#D8B040', 'jar': '#A0602A', 'jar_chunks': '#B8A050',
                    'cup': '#F2EEE4', 'bag': '#E8D8B0', 'block': '#F0E6C8', 'bar': '#5A3420', 'noodles': '#F0E0B0',
                    'bun': '#F4EEE0', 'bread': '#C8883A', 'cake': '#F0D8A0', 'biscuit': '#D8A060', 'fried_stick': '#C8883A',
                    'candy': '#E06080', 'stick_treat': '#F0A0B0', 'sausage': '#A0302A', 'meat': '#9A3A2A', 'poultry': '#B0602A',
                    'fish': '#A8B0B8', 'egg': '#E8D8C0', 'pieces': '#A07040'}
PRODUCT_COLOURS = (
    ('黑巧克力', '#3A2014'), ('牛奶巧克力', '#6A4020'), ('白巧克力', '#F2EAD8'), ('巧克力冰淇淋', '#6A4020'), ('巧克力', '#5A3420'),
    ('抹茶', '#7A9A3A'), ('薄荷', '#9AD8B0'), ('香草', '#F2E8C8'), ('发光浆果', '#F0A040'), ('浆果', '#8A1A4A'),
    ('苹果醋', '#D8B060'), ('苹果', '#E8D070'), ('柑橘', '#F09020'), ('橙', '#F09020'), ('柠檬', '#F0E060'), ('西瓜', '#E04050'),
    ('葡萄酒', '#6A0E1E'), ('葡萄', '#5A1A4A'), ('胡萝卜', '#E8701A'), ('甜菜', '#8A1838'), ('番茄', '#C8281A'),
    ('辣椒油', '#C0301A'), ('辣椒', '#B0201A'), ('芥末', '#D8C030'), ('芝麻油', '#B07820'), ('芝麻', '#8A6A40'),
    ('花生油', '#E8C050'), ('花生糖', '#C89050'), ('花生', '#B07840'), ('豆瓣', '#8A2A1A'), ('甜面酱', '#3A1E10'),
    ('黄豆酱', '#8A5A2A'), ('老抽', '#2A1208'), ('生抽', '#5A2A10'), ('酱油', '#5A2A10'), ('蚝油', '#3A2010'),
    ('陈醋', '#2A1408'), ('米醋', '#C89A50'), ('白醋', '#F0F0E8'), ('料酒', '#B07020'), ('鱼露', '#A06020'), ('卤汁', '#5A2A10'),
    ('昆布', '#C8B070'), ('高汤块', '#C89040'), ('花椒油', '#D8B040'), ('植物油', '#F0D060'), ('焦糖布丁', '#E0B050'),
    ('焦糖', '#6A3010'), ('果葡', '#F0E8C0'), ('葡萄糖浆', '#F0E8C0'), ('麦芽糖', '#D8A040'), ('可乐', '#3A1A10'),
    ('咖啡', '#4A2A1A'), ('珍珠奶茶', '#C8A078'), ('奶茶', '#C8A078'), ('冰红茶', '#8A3A1A'), ('绿茶', '#A8C060'),
    ('凉茶', '#6A3A1A'), ('姜', '#E8C870'), ('酸梅', '#6A1A1A'), ('豆奶', '#F0E8D0'), ('核桃', '#C8A880'), ('蜂蜜', '#E0A020'),
    ('白酒', '#F0F4F8'), ('伏特加', '#F0F4F8'), ('啤酒', '#E8B030'), ('起泡酒', '#F0E0A0'), ('黄酒', '#B07020'),
    ('甜米酒', '#F0ECD8'), ('白兰地', '#B0601A'), ('威士忌', '#C0701A'), ('朗姆', '#8A4A1A'), ('青梅', '#C8D060'),
    ('红曲', '#B0281A'), ('苏打水', '#E0F0F8'), ('气泡水', '#E0F0F8'), ('能量饮料', '#F0D020'), ('运动饮料', '#60B0E0'),
    ('维生素水', '#F0A040'), ('乳酸菌', '#F4F0E8'), ('酸奶', '#F4F0E6'), ('淡奶油', '#F4F2EC'), ('奶油蛋糕', '#F4F0E4'),
    ('黄油曲奇', '#D8A050'), ('人造黄油', '#F0E070'), ('黄油', '#F0D878'), ('马苏里拉', '#F4F0E0'), ('切达', '#F0B030'),
    ('奶油奶酪', '#F4F0E4'), ('奶酪', '#F0C850'), ('芝士', '#F0D890'), ('红腐乳', '#B0301A'), ('臭豆腐', '#5A5A50'),
    ('五香豆干', '#8A5A30'), ('卤豆干', '#6A3A1A'), ('熏豆干', '#7A3A1A'), ('豆干', '#A07840'), ('冻豆腐', '#E8E0C8'),
    ('豆腐', '#EDE8D8'), ('腐乳', '#F0E6C8'), ('豆花', '#F0EAD8'), ('腊肠', '#8A1A1A'), ('腊肉', '#8A2A1A'),
    ('咸肉', '#C87A6A'), ('中式火腿', '#A04A3A'), ('火腿肠', '#D06A6A'), ('火腿', '#D88070'), ('培根', '#C8705A'),
    ('叉烧', '#A0201A'), ('卤牛肉', '#6A2A1A'), ('红烧', '#8A3010'), ('东坡', '#8A3010'), ('热狗', '#B04030'),
    ('肠衣', '#F0E0C8'), ('牛肉干', '#5A2A1A'), ('猪肉脯', '#8A2A1A'), ('肉松', '#C88A50'), ('午餐肉', '#D08070'),
    ('烤鸭', '#9A4A1A'), ('板鸭', '#B07040'), ('酱鸭', '#6A2A10'), ('白切鸡', '#F0E0B0'), ('鸡爪', '#A0602A'),
    ('皮蛋', '#2A2A20'), ('糖葫芦', '#C02020'), ('乳粉', '#F4F0E4'), ('咸鸭蛋', '#D8E8E8'), ('茶叶蛋', '#6A4A2A'), ('卤蛋', '#8A5A2A'), ('糟蛋', '#E8D8B0'),
    ('溏心蛋', '#F0E8D8'), ('鸡蛋布丁', '#F0D070'), ('三文鱼', '#F08060'), ('糖醋', '#C0501A'), ('熏鱼', '#7A3A1A'),
    ('咸鱼', '#C8B890'), ('鱼干', '#C8B890'), ('鱼丸', '#F0EEE6'), ('鱼罐头', '#A8B0B8'), ('豆豉鲮鱼', '#5A3A2A'),
    ('蟹肉棒', '#E04030'), ('虾酱', '#A06060'), ('虾片', '#F0C0A0'), ('红豆', '#6A1A1A'), ('绿豆', '#7AA050'),
    ('盐', '#F4F4F2'), ('红糖', '#8A4A20'), ('冰糖', '#F4F4F0'), ('方糖', '#F8F8F4'), ('糖粉', '#F8F8F6'), ('绵白糖', '#F8F8F6'),
    ('代糖', '#F8F8F6'), ('薯片', '#F0C040'), ('玉米', '#F0C030'), ('爆米花', '#F4EAC0'), ('锅巴', '#D8A050'),
    ('米饼', '#F0E0B0'), ('燕麦', '#D8C090'), ('谷物', '#E0A040'), ('五香粉', '#8A5A30'), ('十三香', '#8A5A30'),
    ('鸡精', '#F0D870'), ('咖喱', '#D89020'), ('火锅', '#A0201A'), ('什锦蔬菜', '#A0B060'), ('脱水蔬菜', '#6A9A3A'),
    ('泡菜', '#C85030'), ('榨菜', '#A8B060'), ('雪菜', '#6A8A3A'), ('酸菜', '#C8C080'), ('黄瓜', '#6A9A3A'),
    ('蘑菇', '#B09070'), ('海带', '#3A4A2A'), ('海苔', '#2A3A2A'), ('纳豆', '#A07040'), ('豆豉', '#3A2A20'),
    ('坚果', '#A0703A'), ('瓜子', '#8AA060'), ('话梅', '#6A2A1A'), ('蜜饯', '#F0B040'), ('糖渍', '#F0B040'),
    ('龟苓膏', '#2A1A10'), ('杏仁豆腐', '#F4F0E8'), ('焦糖块', '#A06020'), ('琼脂果冻', '#F0F0E8'), ('明胶果冻', '#F0B040'),
    ('水果果冻', '#F06080'), ('椰果', '#F4F4F0'), ('爆爆珠', '#F06080'), ('冰沙', '#F08080'), ('刨冰', '#E8F0F8'),
    ('雪糕', '#F2E8D0'), ('冰棍', '#E8F0F8'), ('全麦', '#9A6A3A'), ('吐司', '#E0B070'), ('牛角包', '#D89A40'),
    ('贝果', '#B07030'), ('碱水', '#8A4A1A'), ('甜甜圈', '#D8A060'), ('慕斯', '#E8C0C8'), ('蛋挞', '#F0C040'),
    ('姜饼', '#A0602A'), ('威化', '#F0D8A0'), ('桃酥', '#E0B070'), ('月饼', '#C08040'), ('蛋黄酥', '#E8C060'),
    ('泡芙', '#E0B060'), ('粽', '#6A8A3A'), ('发糕', '#F0E8C8'), ('年糕', '#F4F0E8'), ('油条', '#D08A3A'),
    ('麻花', '#C07A30'), ('馓子', '#D8A050'), ('蛋卷', '#E8B050'), ('肉丸', '#8A5030'), ('狮子头', '#8A5030'),
    ('润喉糖', '#E8B040'), ('棉花糖', '#F4E8F0'), ('牛轧糖', '#F2EEE4'), ('太妃', '#C08040'), ('奶糖', '#F2EAD8'),
    ('口香糖', '#F0A0C0'), ('芝麻糖', '#D8B080'), ('巧克力豆', '#5A3420'), ('糖衣', '#E04040'), ('跳跳糖', '#F080C0'),
    ('奶片', '#F4F0E4'), ('奶粉', '#F4F0E4'), ('炼乳', '#F2EAD0'), ('沙拉酱', '#F4F0E0'), ('蛋黄酱', '#F0E0A0'),
    ('自发粉', '#F4F0E8'), ('蛋清粉', '#F4F2EA'), ('婴儿米粉', '#F0E8D0'), ('蛋白粉', '#F0E6D0'), ('代餐粉', '#E0C8A0'),
    ('冲剂', '#F0A040'), ('干酵母', '#D8C090'), ('植脂末', '#F4F0E8'), ('茶包', '#8A5A2A'), ('珍珠粉圆', '#3A2A20'),
    ('猪油', '#F4F0E4'), ('牛油', '#F0E4C0'), ('起酥油', '#F4F0E4'), ('凉粉', '#E8E4D8'), ('肉冻', '#C8A080'),
    ('鱼皮冻', '#D8C8B0'), ('鱼豆腐', '#E8D0A0'), ('大豆素肉', '#C8A070'), ('腐竹', '#E8D090'), ('龙须酥', '#F2E8D0'),
    ('凉皮', '#F0E8D0'), ('方便面', '#E8C070'), ('能量胶', '#F0A040'), ('双皮奶', '#F4F0E8'), ('姜撞奶', '#F2E8D0'),
    ('米布丁', '#F0E8D0'), ('奶豆腐', '#F4F0E4'))


def product(food):
    zh = food['name']['zh']
    kind = None
    for name, words in PRODUCT_KINDS:
        if any(w in zh for w in words):
            kind, why = name, f'名称含 {next(w for w in words if w in zh)}'
            break
    if kind is None:
        kind, why = 'bag', '默认'
    value, word = first(zh, PRODUCT_COLOURS)
    if value:
        return result(kind, why, value, f'名称：{word}')
    return result(kind, why, PRODUCT_DEFAULTS[kind], '种类默认色')
