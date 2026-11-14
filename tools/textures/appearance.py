"""Appearance of the chemicals (I33): the texture kind and the colour a texture is tinted with.

Kinds of solids (item textures): powder, crystal, lump, resin, flakes, pellets, metal, gel.
Kinds of fluids (block textures, tinted at run time): liquid, viscous, gas.

The colour follows 09 颜色的取值顺序: ① the colour 09 or 08 writes; ② the real appearance (named colours of
colourants and extracts, the colours of transition-metal compounds and of a few elements); ③ the default of the
purity category (most chemicals are white or off-white). overrides.json beside this file sets the kind or the
colour of single materials ({"id": {"kind": "lump", "color": "#RRGGBB"}}) and wins over everything but ①.
"""
import json
import pathlib
import re

HERE = pathlib.Path(__file__).resolve().parent
OVERRIDES = HERE / 'overrides.json'

SOLID_KINDS = ('powder', 'crystal', 'lump', 'resin', 'flakes', 'pellets', 'metal', 'gel')
FLUID_KINDS = ('liquid', 'viscous', 'gas')

# ---- kinds of solids ----

# metals in pieces; iron comes as powder (reduced, carbonyl, electrolytic iron)
METALS = {'Na', 'K', 'Mg', 'Ca', 'Al', 'Si', 'Zn', 'Sn', 'Cu', 'Ti'}
# elements and minerals that come as rock-like chunks
LUMP_WORDS = ('光卤石', '石灰石', '磷灰石', '高岭石', '明矾石', '炉渣', '高钛渣', '炉料', '天然橡胶', '巴拉塔树胶', '节路顿胶',
              '来开欧胶', '糖胶树胶', '胶基')
LUMP_FORMULAS = {'S', 'P4'}
RESIN_WORDS = ('松香', '紫胶', '虫胶', '树脂')
NOT_RESIN = ('紫胶红', '虫胶红', '紫胶桐酸')
FLAKE_WORDS = ('蜡', '羊毛脂', '硬脂', '棕榈酸', '月桂酸', '癸酸', '十四醇', '十八醇', '十三酸', '卤片', '氢化油', '动物脂', '甘油三酯',
               '甘油酯', '司盘', '山梨醇酐', '木糖醇酐')
PELLET_WORDS = ('颗粒', '聚乙烯', '聚苯乙烯', '共聚物')
# soaps of polyvalent metals and the lecithins are powders though fats or emulsifiers
FLAKE_EXCEPTIONS = ('硬脂酸镁', '硬脂酸钙', '磷脂', '抗坏血酸棕榈酸酯')
PELLET_FORMULAS = {'NaOH', 'KOH'}
GEL_WORDS = ('冻',)
POWDER_WORDS = ('粉', '灰', '色淀', '炭黑', '多糖', '低聚', '胶', '淀粉', '纤维素', '蛋白', '肽')
CRYSTAL_WORDS = ('矾', '（盐）', '硝石', '冰糖', '晶')
HYDRATE = re.compile(r'·(\d+)H2O')
SIMPLE_SALT = re.compile(r'^(?:Na|K|NH4|Mg|Ca|Ba|Zn|Mn|Cu|Fe|Co|Ni)\d*(?:Cl|Br|I|NO3|\(NO3\)\d|SO4|\(SO4\)\d|IO3|ClO3|MnO4)\d*$')


def solid_kind(m):
    zh, formula, category = m['name']['zh'], m.get('formula') or '', purity(m)
    if formula in METALS:
        return 'metal', '金属单质'
    if any(w in zh for w in GEL_WORDS):
        return 'gel', '凝胶'
    if formula in PELLET_FORMULAS or any(w in zh for w in PELLET_WORDS):
        return 'pellets', '片碱、颗粒、聚合物粒料'
    if any(w in zh for w in RESIN_WORDS) and not any(w in zh for w in NOT_RESIN):
        return 'resin', '树脂、松香类'
    if formula in LUMP_FORMULAS or any(w in zh for w in LUMP_WORDS):
        return 'lump', '矿物、块状单质、胶基'
    if (any(w in zh for w in FLAKE_WORDS) or category in ('k7_fat', 'k8_emulsifier')) and not any(w in zh for w in FLAKE_EXCEPTIONS):
        return 'flakes', '蜡、脂肪酸与乳化剂（片状）'
    if any(w in zh for w in POWDER_WORDS):
        return 'powder', '名称写明粉、胶、淀粉等'
    if formula == 'I2' or 'MnO4' in formula:
        return 'crystal', '有光泽的晶体'
    if any(w in zh for w in CRYSTAL_WORDS):
        return 'crystal', '矾类、盐、硝石'
    if category == 'k1_inorganic':
        hydrate = HYDRATE.search(formula)
        if hydrate and int(hydrate.group(1)) >= 5:
            return 'crystal', '多结晶水的盐'
        if SIMPLE_SALT.match(formula.split('·')[0]) and not formula.startswith('CaSO4'):
            return 'crystal', '可溶的简单盐'
        return 'powder', '无机物（默认粉末）'
    if category == 'k2_sugar':
        return 'crystal', '糖与糖醇'
    return 'powder', '默认粉末'


# ---- kinds of fluids ----

VISCOUS_WORDS = ('甘油', '糖蜜', '糖醇液', '山梨糖醇液', '焦糖', '焦油', '熔融', '吐温', '司盘', '聚甘油', '硅油', '石蜡', '聚丁烯',
                 '聚氧', '蓖麻', '椰子油', '棕榈油', '鱼肝油', '月见草油', '梓油', '油树脂', '生育酚', '甲萘醌', '视黄', '脂肪酸酯',
                 '油脂', '原油', '馏分油', '脱蜡油', '异构化乳糖', '液化淀粉', '松香', '植酸', '高碳醇', '番茄红', '天然胡萝卜素',
                 '亚麻酸', '花生四烯酸', '大豆油', '酯复合物')
THIN_WORDS = ('石油醚', '杂醇油', '薄荷原油', '樟脑原油', '粗油', '石脑油')


def fluid_kind(m):
    if m['state'] == 'gas':
        return 'gas', '气体'
    zh = m['name']['zh']
    if m['name']['zh'] == '硫酸' or (any(w in zh for w in VISCOUS_WORDS) and not any(w in zh for w in THIN_WORDS)):
        return 'viscous', '油、糖浆、熔体等黏稠液体'
    return 'liquid', '液体'


# ---- colours ----

# real colours named in the name (② of 09): most specific first
NAMED = (
    ('叶绿素铜钠', '#1E6A2E'), ('叶绿素铜钾', '#1E6A2E'), ('叶绿素铜', '#2A5A2A'), ('脱镁叶绿素', '#5A6A2A'), ('叶绿素', '#2E6A2A'),
    ('β-胡萝卜素', '#E8701A'), ('胡萝卜素醛', '#E0601A'), ('胡萝卜素', '#E8781A'), ('番茄红', '#C0281A'), ('辣椒红', '#B0281A'),
    ('辣椒橙', '#E0701A'), ('姜黄', '#F0B000'), ('叶黄素', '#F0B820'), ('玉米黄', '#E8B020'), ('胭脂树橙', '#E06A1A'),
    ('降红木素', '#C04A1A'), ('胭脂虫红', '#A0102A'), ('胭脂红', '#C8102E'), ('苋菜红', '#9A1030'), ('诱惑红', '#D0202A'),
    ('赤藓红', '#E0285A'), ('新红', '#D82838'), ('酸性红', '#B01830'), ('柠檬黄', '#F0D020'), ('日落黄', '#F07818'),
    ('喹啉黄', '#E8D030'), ('亮蓝', '#1A5ED8'), ('靛蓝', '#22308A'), ('栀子蓝', '#2A4AA0'), ('藻蓝', '#2050B0'),
    ('栀子黄', '#E8A818'), ('红花黄', '#E8B020'), ('甜菜苷', '#8A1838'), ('甜菜素', '#8A1838'), ('甜菜红', '#8A1838'),
    ('红曲黄', '#E0A020'), ('红曲', '#B0281A'), ('紫胶桐酸', '#F2F0EA'), ('蓝锭果红', '#7A1A3A'), ('乳铁蛋白', '#E8B0A0'),
    ('钴胺', '#B0202A'),
    ('红斑素', '#C0301A'), ('桔青霉素', '#E8D040'), ('血红素', '#5A1A10'), ('茶黄素', '#B06A20'), ('单宁', '#A07840'),
    ('紫胶', '#9A4A1A'), ('虫胶', '#9A4A1A'), ('松香', '#C8902A'), ('蜂蜡', '#E8C860'), ('巴西棕榈蜡', '#D8C080'),
    ('小烛树蜡', '#D8C890'), ('羊毛脂', '#E8D080'), ('木质素', '#6A4A2A'), ('炭黑', '#1A1A1A'), ('草木灰', '#8A8A86'),
    ('海藻灰', '#5A5A56'), ('骨灰', '#EDE6D6'), ('炉渣', '#5A5650'), ('高钛渣', '#3A3430'), ('氯化炉料', '#4A4440'),
    ('可可壳', '#6A4020'), ('橡子壳棕', '#7A5030'), ('金樱子棕', '#8A5A2A'), ('黑豆红', '#5A1A2A'), ('黑加仑红', '#5A1A3A'),
    ('紫草红', '#7A1A4A'), ('紫甘薯', '#6A2A6A'), ('萝卜红', '#B02040'), ('天然苋菜红', '#9A1030'), ('桑椹红', '#6A1A3A'),
    ('杨梅红', '#9A1A3A'), ('玫瑰茄红', '#A01838'), ('葡萄皮红', '#6A1E3A'), ('花生衣红', '#8A2A2A'), ('越橘红', '#A01830'),
    ('高粱红', '#7A2A1A'), ('红米红', '#8A1A2A'), ('沙棘黄', '#E89A20'), ('菊花黄', '#E8B830'), ('柑橘黄', '#F0A020'),
    ('可可', '#6A4020'), ('甘草', '#C8A060'), ('京尼平', '#E8E0C8'), ('硒蛋白', '#C8B088'), ('富马酸亚铁', '#9A4A2A'),
    ('葡萄糖酸亚铁', '#B8B078'), ('焦磷酸铁', '#E6D8B0'))
# colour words when nothing more specific is known: the last one names the colour (X红 is red); words that only look
# like colours are skipped (棕榈 palm, 黄原 xanthan, 视黄 retinyl, 橙皮 orange peel, 紫苏 perilla, 紫罗兰 violet, ...)
NOT_COLOURS = ('棕榈', '黄原', '视黄', '橙皮', '紫苏', '紫罗兰', '黄酮', '黄樟', '红没药', '红木', '蓝桉', '绿原')
WORDS = (('黑', '#222222'), ('棕', '#6A4020'), ('褐', '#6A4020'), ('紫', '#6A2A7A'), ('蓝', '#2A5ED8'), ('绿', '#3A7A2A'),
         ('橙', '#E87820'), ('红', '#B0202A'), ('黄', '#E8C020'))
# compounds whose colour comes from a metal or element (② of 09), by formula
FORMULA = (
    (r'^S$', '#F0E040'), (r'^I2$', '#3A2A48'), (r'^P4$', '#F0EAC8'), (r'^C$', '#1A1A1A'), (r'^Si$', '#6A7078'),
    (r'^Se$', '#8A1A1A'), (r'^Na$', '#C8CACC'), (r'^K$', '#C8CACC'), (r'^Mg$', '#C0C2C4'), (r'^Fe$', '#6A6A70'),
    (r'^Fe3O4$', '#2A2826'), (r'^Fe2O3$', '#9A2E1A'), (r'^Fe\(OH\)3$', '#8A4020'), (r'^FeCl3', '#5A3018'),
    (r'Fe\(CN\)6', '#E8D040'), (r'MnO4$', '#4A1458'), (r'^K2MnO4$', '#1E4A2A'), (r'^MnO2$', '#2A2622'),
    (r'Cr2O7', '#E06A10'), (r'CrO4', '#F0D020'), (r'Cr', '#3A6A40'), (r'Co', '#D06A8A'), (r'Ni', '#5AA060'),
    (r'^MoO3$', '#D8E0B8'), (r'Cu.*CO3', '#3A9A6A'), (r'Cu', '#2F7FD8'), (r'Mn', '#F0D8DC'))
# 亚铁 (iron(II)) salts are pale green, organic iron salts brownish
IRON = (('亚铁', '#A8C890'), ('铁', '#9A5A2A'))
# defaults of the purity categories (③ of 09)
DEFAULTS = {'k1_inorganic': '#F4F4F2', 'k2_sugar': '#FBFBF6', 'k3_organic_solid': '#F2F0EA', 'k5_natural_extract': '#C8A070',
            'k6_colloid': '#EEE6D2', 'k7_fat': '#F4EEDC', 'k8_emulsifier': '#EFE4C6', 'k10_synthetic_color': '#C02020',
            'k12_compound': '#F2F0EA', 'k0_raw': '#E8E0D0', None: '#F0EEE8'}
LAKE = '铝色淀'


def purity(m):
    return m['purity']['category'].split(':')[1] if m.get('purity') else None


def overrides():
    if OVERRIDES.exists():
        return json.loads(OVERRIDES.read_text(encoding='utf-8'))
    return {}


def mix(colour, other, share):
    a = [int(colour[i:i + 2], 16) for i in (1, 3, 5)]
    b = [int(other[i:i + 2], 16) for i in (1, 3, 5)]
    return '#' + ''.join(f'{round(x + (y - x) * share):02X}' for x, y in zip(a, b))


def colour(m, override=None):
    """(colour, source) of a chemical."""
    if m.get('color'):
        return m['color'], '09 / 08'
    if override and override.get('color'):
        return override['color'], 'overrides.json'
    zh, formula = m['name']['zh'], m.get('formula') or ''
    for word, value in NAMED:
        if word in zh:
            return (mix(value, '#FFFFFF', 0.35) if LAKE in zh else value), f'名称：{word}'
    for pattern, value in FORMULA:
        if re.search(pattern, formula):
            return value, f'化学式：{pattern}'
    if 'Fe' in formula or '铁' in zh:
        for word, value in IRON:
            if word in zh:
                return value, f'名称：{word}'
    plain = zh
    for word in NOT_COLOURS:
        plain = plain.replace(word, '')
    found = [(plain.rfind(word), word, value) for word, value in WORDS if word in plain]
    if found:
        _, word, value = max(found)
        return (mix(value, '#FFFFFF', 0.35) if LAKE in zh else value), f'颜色字：{word}'
    return DEFAULTS.get(purity(m), DEFAULTS[None]), '纯度类别默认色'


def appearance(m):
    """{'kind', 'kind_reason', 'color', 'color_source'} of a chemical (solid or fluid)."""
    override = overrides().get(m['id'], {})
    if m['state'] == 'solid':
        kind, why = solid_kind(m)
    else:
        kind, why = fluid_kind(m)
    if override.get('kind'):
        kind, why = override['kind'], 'overrides.json'
    value, source = colour(m, override)
    return {'kind': kind, 'kind_reason': why, 'color': value, 'color_source': source}
