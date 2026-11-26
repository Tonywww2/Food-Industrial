# 营养数据的输入（I34）

数据生成的输入：随模组附带的原版、农夫乐事等他方食物的手工营养数据，每个模组一份 `<模组 ID>.json`，格式同 [foods/03 §2](../../docs/foods/03-nutrition-data.md)，另加：

- `basis` 必填：`food`、非空的 `sources`、`confirmed`（`processing`、`assumptions` 照实写）
- 六个成分键（咖啡因、酒精、快吸收糖、维生素 C、B1、D）全部写出，0 也写出
- `only_on`（可选）：Minecraft 版本列表（如 `["1.21.1", "26.1.2"]`），条目只在这些版本的节点生成；省略为全部节点

数据生成（`runData`）把这些文件写成 `data/foodindustrial/foodindustrial/nutrition/<模组 ID>.json`，去掉 `only_on`；节点上没有该模组时不生成。生成的文件不手改。配置流程见 [foods/agents/food-nutrition-data.md](../../docs/foods/agents/food-nutrition-data.md)。

## 维生素 A、钙、铁、锌（I46）

`vitamin_a_ug`（预成型）、`provitamin_a_ug`（类胡萝卜素折 µg RAE）、`calcium_mg`、`iron_mg`、`zinc_mg` 由 `micronutrients/micronutrients.py` 写进各条目，不手填：

1. `micronutrients/recipes.json` 逐条写出 I36 每个数值的算法：直接取一个 FDC 条目（`fdc`，可带 `g`、`retention`、`d_from`），按配方相加再分份（`mix` + `divide`，配料为 `fdc:<编号>` 或 `item:<物品 ID>`），或没有来源（`none`）。
2. `python3 tools/nutrition/micronutrients/micronutrients.py`：先按 `recipes.json` 重算维生素 C、B1、D，与已批准的数值对不上就停止、不写文件；对上后用同一批条目、份量、配方与保留率算出上述五项写回，来源没有的项不写（配料中有一项缺就整项不写）。要用 `tools/foods/nutrition/fetch_sources.py` 缓存的 FDC 批量文件。

改了某个条目的份量或配方时，同步改 `recipes.json` 再运行。数据生成只读本目录下的 `<模组 ID>.json`，不读子目录。
