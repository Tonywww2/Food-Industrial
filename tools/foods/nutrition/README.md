# 成品的营养输入（I45）

本模组 265 种能吃的成品的每份营养与代谢数据的输入。流程：

1. `batch3.json`–`batch7.json`：每种成品一条，数值照各批设计文档“能食用成品的数值”一节与卡片录入（字段见下）。
2. `python3 tools/foods/nutrition/fetch_sources.py`：把各条引用的成分表条目（`cn:` 中国食物成分表、`fdc:` USDA FoodData Central、`jp:` 日本食品標準成分表、`retn:` USDA 营养素保留率表）读成每 100 g 的数值，写进 `sources.json`。要联网，下载缓存在 `build/cache/nutrition/`；`--refresh` 重读全部。
3. `python3 tools/foods/nutrition.py`：算每份的数值、饱食度与饱和度系数并检查，写 `src/main/resources/foodindustrial/food_nutrition.json`（模组读它：注册时的食物属性、数据生成的营养数据文件）与报表 `build/reports/foodindustrial/food_nutrition.md`；有错误时不写输出、退出码 1。
4. `runData`：`foodindustrial.json` 营养数据随成品写出，并核对饱食度与饱和度系数。

## 条目字段

| 字段 | 说明 |
| --- | --- |
| `id`、`name`、`card` | 成品 ID、中文名、卡片所在文档 |
| `serving_g` 或 `serving_ml`（加 `density`） | 一份的重量；饮料按体积乘密度（饮料 1.0 g/mL，foods/21 §1；蒸馏酒按 foods/22 的三点拟合） |
| `energy_kcal`、`hunger` | 每份能量、设计文档的饱食度（生成时按 `round(20 × √(kcal / 2,000))` 核对） |
| `macros_100g` | 卡片的蛋白质、脂肪、碳水化合物（每 100 g）；蛋白质用于饱和度系数，三者用于能量换算的警告 |
| `composition` | 维生素与矿物质的来源：一个条目（`cn:1316`），或配料折算 `{"mix": [[条目, 克数, {"retention": "retn:0301", "fill": 条目}], …], "yield_g": 出品}`，或产品标签式的 `{"per100g": {…}, "source": "…"}`，或 `null`（没有来源，全部留空）。来源没有的项留空，不写 0 |
| `free_sugar` | 游离糖（快吸收糖）：`{"g": 每份}`、`{"per100g": x}`、`{"parts": [[名称, 克数, 糖源条目或比例], …], "total_g": 全部原料}`（糖的份额乘每份重量，损耗不计）或 `{"from_source": 条目}`（条目的总糖，没有总糖时取碳水化合物） |
| `caffeine_mg`、`alcohol_g` | 每份；不写时咖啡因取成分条目的值（FDC 有），没有就是 0；酒精为 0 |
| `curing_nitrite_mg` | 放护色剂时每份的亚硝酸盐（GB 2760 残留限量 × 每份） |
| `misuse_salt_g`、`misuse_check` | 盐位误投亚硝酸盐时每份的量（= 每份加盐量，g）；`misuse_check` 按盐分表重算并核对（foods/10 §11） |
| `override` | 个别项的每份值（强化的维生素 C、配方不含的强化剂等），连同 `override_note` |
| `*_note`、`notes` | 依据与假设，写进生成数据的 `basis` |

单位换算（茶匙、汤匙、杯、个）按 USDA FDC 的份量，见各批文件开头的 `notes`。

记的营养素（I46，2026-11-19 减为 7 种）：维生素 C、B1、D、A，钙、铁、锌。维生素 A 分两个键：`vitamin_a_ug` 为预成型（视黄醇；中国食物成分表平台的“维生素A”不含胡萝卜素，按预成型取），`provitamin_a_ug` 为类胡萝卜素折 µg RAE（β-胡萝卜素 ÷ 12，α-胡萝卜素与 β-隐黄质 ÷ 24；成分表的胡萝卜素与日本表的 β-胡萝卜素当量按 ÷ 12），保留率分别取 USDA 表的维生素 A（392）与 β-胡萝卜素（321）。
