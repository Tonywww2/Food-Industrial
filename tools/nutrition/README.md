# 营养数据的输入（I34）

数据生成的输入：随模组附带的原版、农夫乐事等他方食物的手工营养数据，每个模组一份 `<模组 ID>.json`，格式同 [foods/03 §2](../../docs/foods/03-nutrition-data.md)，另加：

- `basis` 必填：`food`、非空的 `sources`、`confirmed`（`processing`、`assumptions` 照实写）
- 六个成分键全部写出，0 也写出
- `only_on`（可选）：Minecraft 版本列表（如 `["1.21.1", "26.1.2"]`），条目只在这些版本的节点生成；省略为全部节点

数据生成（`runData`）把这些文件写成 `data/foodindustrial/foodindustrial/nutrition/<模组 ID>.json`，去掉 `only_on`；节点上没有该模组时不生成。生成的文件不手改。配置流程见 [foods/agents/food-nutrition-data.md](../../docs/foods/agents/food-nutrition-data.md)。
