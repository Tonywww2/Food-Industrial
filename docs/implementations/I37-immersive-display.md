# I37 沉浸式展示

| 项 | 内容 |
| --- | --- |
| 状态 | 已完成 |
| 阶段 | C 框架 |
| 依赖 | I09、I10 |
| 原文档 | [machines/04 §9](../machines/04-gui.md)、[machines/02 §6](../machines/02-architecture.md) |
| 节点 | 1.20.1-forge、1.21.1-neoforge、26.1.2-neoforge |
| 代码位置 | `machine.display`、`machine.display.client`、`platform.client.PlatformPanels`、`gui.client.MachineScreen` |

## 目标

每个机器窗口里有一个“沉浸式展示”按钮；开启后，机器在世界里用一块小面板显示原料、进度和成品，并带表现加工过程的小动画（2026-11-18，用户要求）。

## 范围

- 做：窗口按钮与客户端开关、客户端配置的默认值、面板（原料、进度箭头、成品、状态灯）、五类动画、按需询问的数据通道、三个节点的面板绘制
- 不做：锅与被动设备（没有窗口，内容物本来就画在世界里，I12）；仓口窗口；催化剂、燃料、升级、模块的显示

## 用户的决定（2026-11-18）

| 问题 | 决定 |
| --- | --- |
| 开关范围 | 全局一个开关：在任何机器窗口里点都是切换它，对所有机器生效 |
| 展示形式 | 迷你面板：半透明深色底板加格子，像缩小的机器界面，浮在机器旁并朝向玩家 |
| 动画 | 按工艺分类，五类：研磨抖动、压榨压扁、加热冒汽、搅拌转圈、发酵冒泡；成品到达时弹一下 |

其余按原话：开关是客户端功能，会话结束后重置；配置项决定默认值，默认关；设计时尽量少占资源。

## 任务

- [x] `platform`：客户端配置选项（`PlatformConfig.clientBoolean`、`clientInt`），三个节点的面板渲染（`PlatformPanels`），方块实体的渲染范围（`PlatformBlockEntity.renderReach`）（[machines/04 §9.4](../machines/04-gui.md)）
- [x] 数据通道：客户端询问、服务器回复（`MachineDisplay`）（[machines/04 §9.4](../machines/04-gui.md)）
- [x] 客户端状态：开关、会话重置、选最近的机器、面板内容（`ImmersiveDisplay`）（[machines/04 §9.1](../machines/04-gui.md)）
- [x] 布局与动画（`PanelLayout`、`Motion`、`Motions`）及单元测试（[machines/04 §9.2、§9.3](../machines/04-gui.md)）
- [x] 机器窗口的按钮、图标、提示文字（[machines/04 §1](../machines/04-gui.md)）
- [x] 面板贴图 `textures/gui/immersive_panel.png` 与按钮图标，写入 [贴图需求](../texture-requirements.md)

## 节点差异

| 项 | 1.20.1-forge | 1.21.1-neoforge | 26.1.2-neoforge |
| --- | --- | --- | --- |
| 客户端配置 | `ForgeConfigSpec`，`ModConfig.Type.CLIENT` | `ModConfigSpec` | `ModConfigSpec` |
| 渲染范围 | 方块实体的 `getRenderBoundingBox()` | 渲染器的 `getRenderBoundingBox(T)` | 同 1.21.1 |
| 绘制 | `BlockEntityRenderer.render`，直接写入 `MultiBufferSource` | 同 1.20.1 | 先 `extractRenderState` 再 `submit`，经 `SubmitNodeCollector` 提交 |
| 渲染类型 | `RenderType.entityTranslucentCull` | 同 1.20.1 | `RenderTypes.entityTranslucent` |
| 物品图标 | `ItemRenderer.renderStatic`（`GUI` 显示方式） | 同 1.20.1 | `ItemModelResolver` 解析成 `ItemStackRenderState`，物品不变时沿用 |
| 文字 | `Font.drawInBatch` | 同 1.20.1 | `SubmitNodeCollector.submitText` |

## 验收

- [x] 三个节点 `compileJava` 通过，platform 以外的自检无结果（[CONVENTIONS §7](../../CONVENTIONS.md)）
- [x] 三个节点单元测试通过（各 123 项，其中本计划 10 项）
- [x] 三个节点游戏内：面板显示、按钮提示与切换
- [x] 1.20.1 游戏内：默认关、配置默认开、退出世界后重置、范围、数量上限、文字距离、两种位置、五类动画

## 实现记录

- 结构：
  - `machine.display.MachineDisplay`（两端共用）：配置选项、询问与回复两个数据包、服务器端的处理。服务器只在收到询问时读机器；内容的校验值由状态、输入输出槽和全部储罐算出，只在服务器上计算和比较
  - `machine.display.client.ImmersiveDisplay`：开关、客户端 tick 里的会话重置与每 10 tick 的询问、每台机器的显示状态（`Shown`）与面板填充
  - `machine.display.PanelLayout`、`Motion`、`Motions`：布局与动画的纯计算，不依赖游戏类，有单元测试
  - `platform.client.PlatformPanels`：`Panel` 是一组定长数组（矩形 64、物品 24、流体 16、文字 24），业务代码每帧清空后重填；渲染器把它朝向相机画出来。面板的法线统一改成正面朝上，光照不随朝向变化
- 开关关闭时的开销：面板渲染器的可见距离返回 0，游戏在距离判断处就跳过所有机器，不调用业务代码；客户端 tick 里只剩一次布尔判断；不发包
- 开启时：只有游戏本来就要画的机器（在视锥内、在 `immersive_range` 内）会被问到面板；被画到的机器记入列表，30 tick 没被画到就不再询问，100 tick 后忘掉
- 进度：服务器回进度、总时长和每 tick 的进度（千分之一为单位，含速度升级），客户端按收到后经过的时间推算，所以箭头在两次回复之间也在走
- 面板在机器方块之外，原版只按方块本身判断是否在视野里，所以机器的渲染范围向四周各加 1 格、向上加 2 格（1.20.1 由方块实体给出，NeoForge 由渲染器给出）
- 一边最多 12 项（机器最多 6 个输入槽加 6 个输入储罐）
- 贴图由 `tools/gui/gui_textures.py` 生成；该脚本不再画设备扳手与红图的物品图标（已由 `tools/textures/item_art.py` 负责）。贴图需求文档的两张示意图由新脚本 `tools/gui/texture_figures.py` 生成
- 实测（2026-11-18，云端开发环境，软件渲染）：
  - 1.20.1：
    - 配置默认（关）进入世界：64 台运行中的粉碎机都没有面板；打开粉碎机窗口，按钮提示“沉浸式展示：关”，点击后变“开”，面板出现
    - `immersive_limit = 6`：只有最近的 6 台显示面板
    - 开启后退出到标题画面再进入同一世界：面板不再显示（回到配置默认）
    - `immersive_display = true`：进入世界即显示
    - 一排六台机器（粉碎机、固液分离机、干燥机、搅拌反应罐、发酵缸、调配机）都有面板：粉碎机原料抖动，固液分离机原料被压扁，干燥机上方有蒸汽，搅拌反应罐两种原料转圈，发酵缸有气泡；进度箭头连续前进；输出已满的机器亮黄灯；流体显示为带量的色块
    - 机器上方放方块后，面板改到正面上沿前方
    - 距离：10 格处有面板无文字，14 格处有面板，18 格处无面板（范围 16）
    - 帧率（2 核、llvmpipe 软件渲染，画面里约 20 块面板）：开启 18 fps，关闭 20–21 fps，各读两次；开启时客户端每秒多发 2 个包
  - 1.21.1、26.1.2：五台机器的面板（上方与正面两种位置）、物品、数量、流体、箭头正常；按钮提示“沉浸式展示：开”，点击后面板消失
  - 没有测到的：运行中修改配置文件，云端环境里没有触发重新加载，改动都是重启后验证的；1.21.1 与 26.1.2 上没有重复测范围、上限、退出重进和帧率（这些是三个节点共用的代码）；没有在真实显卡上测帧率；没有测多人服务器

## 待审（实现时拟定，请确认）

- 另加的两个配置项：`immersive_range`（默认 16 格）、`immersive_limit`（默认 24 块）
- 面板位置：机器正上方；上方有方块时改到正面上沿前方
- 数量与流体量只在 8 格以内显示
- 状态灯的颜色：运行绿、等待灰、能量不足 / 条件不满足 / 结构未成形红、其余黄
- 19 种配方类型归入五类动画的分法（[machines/04 §9.3](../machines/04-gui.md)）；机器有多个配方类型时按第一个
- 没在运行、原料与成品都空的机器不显示面板
- 面板不显示催化剂、燃料、升级、模块
- 仓口窗口没有按钮

## 待定

- 无
