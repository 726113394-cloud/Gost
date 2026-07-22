🎮 Gost — 人鬼追逐 · 救赎者 · 猎魔人 多阶段多阵容对抗插件

> 一款基于 Bukkit/Spigot 的 Minecraft 多人对抗小游戏插件。  
> 本插件灵感来源于游戏 《Dead Realm》——以此致敬那款曾带来无数欢乐的已逝佳作，并尝试在 Minecraft 中重现那份追逐与博弈的紧张与乐趣。

⚡ 救赎者系统 | 🎮 模式切换 | 🌑 黑暗疾跑并存 | 🔧 队列优化 | ✨ 猎魔人阶段 | 🔊 音效系统全面更新

---

## 📑 目录

- [📦 下载与链接](#-下载与链接)
- [🎯 核心玩法](#-核心玩法)
- [🛡️ 神圣守护系统 — 双模式](#️-神圣守护系统--双模式)
- [⚔️ 猎魔人阶段](#️-猎魔人阶段)
- [🧰 道具系统](#-道具系统)
- [🔊 音效系统](#-音效系统)
- [👻 鬼转人类功能](#-鬼转人类功能)
- [🏆 经济与奖励系统](#-经济与奖励系统)
- [✨ 特色系统](#-特色系统)
- [📋 命令](#-命令)
- [🔐 权限](#-权限)
- [⚙️ 配置](#️-配置)
- [📈 版本历史](#-版本历史)
- [🚀 开始游戏](#-开始游戏)
- [🌐 English Version](#-english-version)

---

## 📦 下载与链接

- [Modrinth 下载](https://modrinth.com/plugin/gost)
- [作者主页](https://726113394-cloud.github.io/PersonalPage/)
- [预览视频](https://www.bilibili.com/video/BV1aPQSBsE74/)
- [2.3.0 版本更新介绍](https://www.bilibili.com/video/BV1eHKj69E4H/)

---

🎯 核心玩法

游戏流程：
1. 准备阶段 — 玩家进入匹配队列，满员后开始游戏
2. 感染阶段 — 鬼玩家追逐并感染人类，人类可拾取道具自保
3. 神圣守护阶段 — 剩余人类数量 ≤ 触发人数时，自动激活神圣守护
4. 猎魔人阶段 — 该阶段需要至少3名玩家，当游戏剩余 90 秒时进入，拥有神圣守护的人类变为猎魔人，普通鬼失去感染能力

两大阵营：
| 阵营 | 说明 |
|------|------|
| 👤 人类 | 初始身份，被鬼感染后变为鬼 |
| 👻 鬼   | 感染人类使其加入鬼阵营，普通鬼在猎魔人阶段会失去感染能力，其母体为鬼阵营领袖 |

特殊角色：
| 角色 | 说明 |
|------|------|
| 🛡️ 神圣守护 | 剩余人类较少时自动激活，可抵挡 3 次 鬼的攻击，每次攻击会将进攻者随机传送 |
| 🎯 猎魔人   | 猎魔人阶段由拥有神圣守护的玩家转化，持有 收割者 道具，可击杀鬼玩家。玩家成为猎魔人后神圣守护会刷新，猎魔人被感染后成为普通鬼 |
| 👑 母体     | 鬼阵营领袖，每局游戏会产出一位，在猎魔人阶段可攻击猎魔人，猎魔人阶段可感染普通人类 |
| ✨ 救赎者   | 模式2专属，最后一名人类成为救赎者（2.2.2及之后版本改为随机），持有 神之救赎 道具，可转化鬼玩家回人类，救赎者无法获得神圣守护 |


🛡️ 神圣守护系统 — 双模式

模式1：传统神圣守护
- 激活条件：剩余人类 ≤ 触发人数（默认2人）
- 防御次数：可抵挡 3 次（2.2.2版本前为2次）攻击（不论攻击者）
- 传送效果：2.2.2前：持有者被攻击时随机传送；2.2.2起：进攻者被随机传送并减速
- 猎魔人阶段：所有神圣守护自动刷新防御次数，并转化为猎魔人

模式2：救赎者模式（2.2.2版本前）
- 激活条件：仅剩1名人类时，最后一名人类成为救赎者
- 神之救赎道具：可转化鬼玩家回人类，使用后随机传送
- 次数限制：2次（可配置），用完后回归普通人类
- 视觉效果：全程高亮 + 速度效果

模式2：救赎者模式（2.2.2版本起）
- 激活条件：被发放道具[神圣救赎]的人类成为救赎者，无法获得神圣守护，不可取消
- 神之救赎道具：可转化鬼玩家回人类，使用后随机传送
- 次数限制：1次，使用后仍保留救赎者身份
- 视觉效果：全程云雾环绕

模式切换命令：/divineguardian setmode <1|2>，游戏进行中切换立即生效。


⚔️ 猎魔人阶段

游戏剩余 90 秒时自动进入猎魔人阶段：
- 所有拥有神圣守护的人类变为 猎魔人
- 猎魔人持有 收割者 道具，可攻击鬼玩家
- 普通鬼 不能感染人类，只能躲避猎杀
- 母体 可以攻击猎魔人，每次造成配置的伤害值
- 母体需要攻击 3 次 才能破除猎魔人的神圣守护
- 所有 神之救赎 道具被清除
- 鬼玩家血量调整，猎魔人阶段禁止回血


🧰 道具系统

道具会在游戏过程中 定时发放 给人类玩家，每次发放间隔可在配置中调整（默认每 60 秒发放一次），发放时会在聊天栏显示剩余时间提示。默认发放道具会同时揭示所有玩家位置 5 秒。

| 道具 | 说明 | 专属阵营 | 冷却/音效 |
|------|------|----------|----------|
| 🗡️ 收割者 | 猎魔人专属武器，攻击鬼玩家造成伤害，血量归零击杀并触发复活倒计时 | 猎魔人 | 冷却2秒，无音效 |
| 💊 神之救赎 | 救赎者专属，右键点击鬼玩家将其转化回人类（次数限制） | 救赎者 | 冷却10秒，音效 ITEM_TOTEM_USE |
| ❤️ 第二次机会 | 人类被感染时自动触发，免疫本次感染并保留人类身份 | 人类 | 冷却180秒，多种音效 |
| 🧊 凝冰球 | 投掷雪球，命中后施加 缓慢 IV（4秒） | 通用 | 投掷音效 ENTITY_SNOWBALL_THROW |
| 🥩 臭牛排 | 无视饱食度直接食用，获得 速度 III（14秒）和发光（10秒） | 通用 | 进食音效 + ENTITY_PLAYER_BURP |
| 🔮 控魂术 | 人类专用，使用后全场鬼玩家无法移动 6秒 | 人类 | 冷却18秒，使用者/被控鬼分别有音效 |
| 👁️ 灵魂探测器 | 鬼专用，使用后所有玩家位置暴露 25秒 | 鬼 | 冷却35秒，使用者/全服音效 |
| 🪄 传送珍珠 | 投掷后玩家传送到珍珠落点（仅右键） | 通用 | 冷却20秒，音效 ENTITY_ENDER_PEARL_THROW |
| 🧪 肾上腺素 | 右键使用获得 速度 II（10秒） | 人类 | 音效 ENTITY_PLAYER_SPLASH |
| 🔥 狂暴药水 | 右键使用获得 速度 II（10秒） | 鬼 | 音效 ENTITY_WITHER_SHOOT |
| 🗡️ 冲刺矛 | 左键冲刺，使用后消失（仅服务器版本 ≥ 1.21.4 可用） | 通用 | 内置矛冲刺音效 |
| 🧪 漂浮药水 | 右键使用获得 漂浮 效果 4.5 秒（约飞3.4格高） | 通用 | 音效 ENTITY_PLAYER_SPLASH |


🔊 音效系统

发放道具音效：
- 每个玩家获得道具时播放：ENTITY_ITEM_PICKUP（不同道具不同音调）
- 发放完毕全局提示：BLOCK_NOTE_BLOCK_PLING

阵容切换音效：
- 人类 → 鬼：ENTITY_VEX_AMBIENT（阴森低鸣）
- 鬼 → 人类：ENTITY_PLAYER_LEVELUP（升级音效）

阶段音效：
- 准备阶段倒计时最后3秒：BLOCK_NOTE_BLOCK_HAT（每秒一次）
- 游戏正式开始：ENTITY_ENDER_DRAGON_GROWL（全服）
- 人类胜利：人类 UI_TOAST_CHALLENGE_COMPLETE，鬼 ENTITY_WITHER_DEATH
- 鬼胜利：鬼 UI_TOAST_CHALLENGE_COMPLETE，人类 ENTITY_WITHER_DEATH


👻 鬼转人类功能

游戏剩余 3 分钟 时，系统会随机将部分 非母体鬼玩家 转换回人类阵容（默认关闭，可在配置中启用）。
- 转换后的玩家保留其作为鬼时的累计时间记录
- 可获得 20% 额外奖金补偿
- 仍能获得作为鬼时的奖金继承


🏆 经济与奖励系统

入场与奖池：
- 玩家加入游戏需支付 入场费（默认 100 金币），入场费汇入 人类奖池
- 服务器可设置 额外奖金（默认 5000 金币），增加奖池总额

胜利奖金分配：
- 人类胜利：人类阵容获得奖池 70%，鬼阵容获得 30%
- 鬼胜利：鬼阵容获得奖池 100%

个人奖金计算：
- 人类：100% 按 存活时间比例 分配
- 鬼：70% 按 鬼存活时间 + 30% 按 感染人数比例 分配

猎魔人奖励：
- 猎魔人每击杀一名鬼玩家，可获得 人类奖池 30% 的奖金
- 母体 击杀猎魔人可获得 50% 的额外奖励（高于普通感染奖励）


✨ 特色系统

🌑 黑暗效果与疾跑并存：
- 使用 Minecraft 原生 DARKNESS 效果（1.19+）或 BLINDNESS（旧版本）
- 通过属性修改器增加 30% 移动速度，抵消黑暗效果对疾跑的影响
- 每 10 ticks 检查玩家疾跑状态，自动恢复被阻止的疾跑
- 管理员和创造模式玩家也会受到黑暗效果影响

👻 鬼玩家粒子效果：
- 鬼玩家身上持续显示环绕粒子效果
- 母体鬼：红色粒子，普通鬼：绿色粒子
- 支持 21 种粒子类型，RGB 颜色自定义
- 可配置准备阶段是否显示

💬 语言系统全面优化（Cover version 核心改进）：
- 大幅扩充默认消息库，90+ 条默认中文消息
- 智能消息回退机制，不再返回错误代码
- 错误隔离保护，不影响核心游戏流程
- 插件启动保护，LanguageManager 初始化失败仍能运行

❤️ 心跳声系统：
- 游戏过程中人类方循环播放监守者出现时的心跳声
- 管理员可通过 /gostadmin heartbeat 控制开关


📋 命令

| 命令 | 说明 | 权限 |
|------|------|------|
| /gost join | 加入游戏队列 | gost.use |
| /gost leave | 离开游戏队列 | gost.use |
| /gost stop | 管理员强制结束游戏 | gost.admin |
| /gost reload | 重载配置文件 | gost.admin |
| /gostadmin dark <on/off/status> | 控制黑暗效果开关 | gost.admin |
| /gostadmin heartbeat <on/off/status> | 控制心跳声开关 | gost.admin |
| /gost start | 管理员强制开始游戏（跳过人数限制） | gost.admin |
| /gostadmin testmode | 管理员单人测试模式（对局不因人数不足结束） | gost.admin |
| /gostadmin giveitem <道具名> | 直接获得指定道具（支持模糊匹配） | gost.admin |
| /divineguardian status | 查看神圣守护状态 | gost.admin |
| /divineguardian setmode <1/2> | 切换神圣守护模式 | gost.admin |
| /divineguardian setcharges <次数> | 设置最大使用次数 | gost.admin |
| /divineguardian force <玩家> | 强制激活神圣守护 | gost.admin |
| /ghostparticle status | 查看粒子效果状态 | gost.admin.ghostparticle |
| /ghostparticle settype <类型> | 设置粒子类型 | gost.admin.ghostparticle |
| /ghostparticle setmothercolor <R,G,B> | 设置母体鬼颜色 | gost.admin.ghostparticle |


🔐 权限

| 权限节点 | 说明 |
|----------|------|
| gost.use | 玩家基础权限（加入/离开游戏） |
| gost.player | 玩家权限（默认直接给予所有玩家） |
| gost.admin | 管理员权限（开始/结束/重载/黑暗/心跳） |
| gost.admin.divineguardian | 神圣守护管理权限 |
| gost.admin.ghostparticle | 鬼玩家粒子效果管理权限 |


⚙️ 配置

配置文件位于 plugins/Gost/config.yml，主要配置项：

# 游戏设置
game:
  duration: 420          # 游戏时长（秒），默认7分钟
  min-players: 2
  max-players: 16

# 经济设置
economy:
  entry-fee: 100.0       # 入场费
  server-bonus: 5000.0   # 服务器奖金

# 黑暗效果设置
dark-effect:
  enabled: true          # 黑暗效果默认启用
  description: "不影响疾跑"

# 神圣守护设置
divine-guardian:
  enabled: true
  trigger-human-count: 2
  holy-guardian:
    defense-charges: 3
    teleport-attacker: true
    teleport-radius: 10.0
  demon-hunter:
    phase-start-time: 90
    health:
      demon-hunter: 2.0
      mother-attack-damage: 1.0

# 鬼玩家粒子效果
ghost-particle:
  enabled: true
  particle-type: REDSTONE
  interval: 15
  count: 5
  mother-color: "255,0,0"
  normal-color: "0,255,0"

完整配置请参考 config.yml 文件，支持热重载 /gost reload。


📈 版本历史

v2.3.0 当前版本 | 新道具 & 管理员工具
- 新增道具：
  - 冲刺矛（GOLDEN_SPEAR）：左键冲刺，使用后消失（仅 1.21.4+ 可用）
  - 漂浮药水（LINGERING_POTION）：右键获得漂浮 4.5 秒
- 新增管理员命令：
  - /gost start — 强制开局（跳过人数限制）
  - /gostadmin testmode — 单人测试模式
  - /gostadmin giveitem <道具名> — 直接获得道具（支持模糊匹配）
- 新增游戏结束奖金排行榜：游戏结算时按奖金从高到低排序显示所有参与玩家的奖励排行，前三名分别显示金银铜奖牌（🥇🥈🥉）
- 配置版本升级到 27，插件版本升级到 2.3.0

v2.2.3 视觉、听觉沉浸 & 重新支持英文
- 新增音效系统（阶段音效、发放道具音效、道具使用音效、阵容切换音效）
- 新增道具居中字幕提示
- 新增英文语言支持：在 config.yml 中设置 language.default: en_US 即可切换为英文
- 配置自动迁移：检测到配置版本不一致时自动迁移，旧配置备份为 config_old_v{版本号}.yml
- 修复 1.20.x API 兼容性
- 配置版本升级到 26

v2.2.2 猎魔人登场（大改）
1. 鬼玩家复活机制：猎魔人阶段鬼玩家被击杀后进入旁观模式，可配置的复活倒计时（默认15秒），倒计时显示在ActionBar上，复活时播放视觉效果和音效。
2. 智能血量调整系统：普通鬼血量降至2点，母体鬼血量降至3点，猎魔人血量固定为2点（均可配置）。自动适应游戏阶段变化。
3. 禁止回血机制：猎魔人阶段禁止所有玩家回血（包括自然恢复、饱食度、药水效果），并提供明确的游戏提示。
4. 收割者伤害系统：基于伤害值而非攻击次数的击杀逻辑，可配置每次攻击伤害值，显示伤害值和剩余血量信息。
5. 神圣守护3次抵挡机制：母体需攻击3次才能破除猎魔人守护，普通鬼猎魔人阶段无法感染只能躲避猎杀。
6. 猎魔人阶段自动激活所有人类神圣守护，无猎魔人时广播提示。
配置项：divine-guardian.demon-hunter 下可调整复活时间、血量、伤害等参数。

v2.2.1_Cover 第二稳定版本 | 修复神圣守护2 & 凝冰球问题
- 语言系统全面优化（90+条默认中文消息，智能回退，错误隔离）
- 凝冰球修复（对所有玩家生效），默认血量调整为10颗心
- 黑暗效果与疾跑并存系统
- 神圣守护系统修复，道具系统修复
- 配置系统升级到 20

v2.2.0 新增神圣守护模式2
- 神圣守护模式2 - 救赎者系统
- 游戏模式切换（/divineguardian setmode <1|2>）
- 队列系统全面优化
- 代码精简与性能提升

v2.1.3 首个稳定版本 | 管理员可参与
- 修复管理员游戏效果免疫问题
- 创造模式玩家自动切换为生存模式

v2.1.2 新增粒子效果
- 新增鬼玩家粒子效果系统（21种粒子类型，RGB颜色）
- 鬼玩家粒子效果管理命令（/ghostparticle）
- 配置系统升级到 14
- 新增 gost.admin.ghostparticle 权限

v2.1.1 神圣守护（大改）
- 新增神圣守护系统
- 神圣守护视觉效果及管理命令（/divineguardian）
- 配置系统升级到 13

v2.1.0 奖金分配优化
- 奖金分配系统全面优化
- 转换玩家奖金优化
- 新增心跳声系统（/gostadmin heartbeat）
- 道具系统升级
- 新增鬼转人类功能（默认关闭）

v2.0.2 修复问题、黑暗沉浸
- 修复编译错误，修复灵魂探测器失效
- 新增黑暗效果系统（/gostadmin dark）

v2.0.1 新增传送珍珠 & 臭牛排
- 新增传送珍珠、臭牛排
- 智能倒计时系统

v2.0.0 最初版本
- 架构重构，独立区域系统，道具系统优化

v1.0.0 已作废
- ⚠️ 此版本已作废（因机制与作者想要的不一致）


🚀 开始游戏

使用命令 /gost join 加入队列，体验生死追逐的乐趣！
感染方式：鬼玩家左键/右键点击人类
道具发放全体高亮5秒
猎魔人阶段带来全新体验


在 Gost 的世界里，生存不仅需要速度，更需要智慧与策略。

# 🌐 English Version

## 🎮 Gost — Ghost Chase · Redeemer · Demon Hunter Multi‑Stage Confrontation Plugin

> A Minecraft multiplayer mini-game plugin based on Bukkit/Spigot.  
> Inspired by **Dead Realm** — paying tribute to the deceased masterpiece that brought countless joys, and attempting to recreate the tension and fun of chase and confrontation in Minecraft.

⚡ **Redeemer System** | 🎮 **Mode Switching** | 🌑 **Dark Effect + Sprint** | 🔧 **Queue Optimization** | ✨ **Demon Hunter Phase** | 🔊 **Sound System Overhaul**

---

## 📑 Table of Contents

- [📦 Downloads & Links](#-downloads--links)
- [🎯 Core Gameplay](#-core-gameplay)
- [🛡️ Divine Guardian System — Two Modes](#️-divine-guardian-system--two-modes)
- [⚔️ Demon Hunter Phase](#️-demon-hunter-phase)
- [🧰 Item System](#-item-system)
- [🔊 Sound System](#-sound-system)
- [👻 Ghost-to-Human Feature](#-ghost-to-human-feature)
- [🏆 Economy & Reward System](#-economy--reward-system)
- [✨ Feature Systems](#-feature-systems)
- [📋 Commands](#-commands)
- [🔐 Permissions](#-permissions)
- [⚙️ Configuration](#️-configuration)
- [📈 Version History](#-version-history)
- [🚀 Start Playing](#-start-playing)

---

## 📦 Downloads & Links

- [Download on Modrinth](https://modrinth.com/plugin/gost)
- [Author's Homepage](https://726113394-cloud.github.io/PersonalPage/)
- [Preview Video](https://www.bilibili.com/video/BV1aPQSBsE74/)
- [2.3.0 Update Introduction](https://www.bilibili.com/video/BV1eHKj69E4H/)

---

## 🎯 Core Gameplay

### Game Flow
1. **Preparation Phase** — Players join the match queue; game starts when full.
2. **Infection Phase** — Ghosts chase and infect humans; humans pick up items to survive.
3. **Divine Guardian Phase** — Activated automatically when remaining humans ≤ trigger count.
4. **Demon Hunter Phase** — Requires at least 3 players. When 90 seconds remain, players with Divine Guardian become Demon Hunters, and normal ghosts lose infection ability.

### Two Factions

| Faction | Description |
|---------|-------------|
| 👤 Human | Initial identity; becomes ghost after infection. |
| 👻 Ghost | Infects humans to expand ghost faction; normal ghosts lose infection ability in Demon Hunter phase; the Mother Ghost is the faction leader. |

### Special Roles

| Role | Description |
|------|-------------|
| 🛡️ Divine Guardian | Activates when few humans remain; blocks **3** attacks; teleports the attacker on each hit. |
| 🎯 Demon Hunter | Transformed from Divine Guardian during Demon Hunter phase; holds **Reaper** to kill ghosts. Divine Guardian resets upon becoming a Demon Hunter. Demon Hunters become normal ghosts after being infected. |
| 👑 Mother Ghost | Ghost faction leader; one per game. Can attack Demon Hunters and infect normal humans during Demon Hunter phase. |
| ✨ Redeemer | Mode 2 exclusive. The last human becomes the Redeemer (random after 2.2.2). Holds **Holy Redemption** item to convert ghosts back to humans. Redeemers cannot obtain Divine Guardian. |

---

## 🛡️ Divine Guardian System — Two Modes

| Mode | Description |
|------|-------------|
| **Mode 1: Classic Divine Guardian** | **Trigger**: Remaining humans ≤ configured count (default 2)<br>**Defense charges**: Blocks **3 (2 before 2.2.2)** attacks<br>**Teleport effect**: Before 2.2.2: holder teleports; After 2.2.2: attacker teleported and slowed<br>**Demon Hunter phase**: Divine Guardian refreshes; holders become Demon Hunters |
| **Mode 2: Redeemer Mode (before 2.2.2)** | **Trigger**: Only 1 human remains → last human becomes Redeemer<br>**Holy Redemption**: Converts a ghost back to human; random teleport after use<br>**Usage limit**: 2 times (configurable), returns to normal human after exhaustion<br>**Visual effects**: Full highlight + speed effect |
| **Mode 2: Redeemer Mode (since 2.2.2)** | **Trigger**: Player who receives the [Holy Redemption] item becomes Redeemer; cannot obtain Divine Guardian; irreversible<br>**Holy Redemption**: Converts ghosts back to humans; random teleport<br>**Usage limit**: 1 time; retains Redeemer status after use<br>**Visual effects**: Surrounded by mist |

> **Mode switch command**: `/divineguardian setmode <1|2>`, takes effect immediately even during game.

---

## ⚔️ Demon Hunter Phase

Automatically enters Demon Hunter phase when 90 seconds remain:

- All players with Divine Guardian become **Demon Hunters**
- Demon Hunters receive **Reaper** item to attack ghosts
- **Normal ghosts** cannot infect humans; can only hide
- **Mother Ghost** can attack Demon Hunters, dealing configured damage per hit
- Mother needs **3 attacks** to break a Demon Hunter's Divine Guardian
- All **Holy Redemption** items are cleared
- Ghost health adjusted; healing disabled during Demon Hunter phase

---

## 🧰 Item System

Items are **periodically distributed** to human players (default interval: 60 seconds). A global 5-second highlight reveals all players when items are distributed.

| Item | Description | Faction | Cooldown / Sound |
|------|-------------|---------|------------------|
| 🗡️ **Reaper** | Demon Hunter weapon; deals damage; kills when health reaches zero; triggers respawn timer | Demon Hunter | 2s cooldown, no sound |
| 💊 **Holy Redemption** | Redeemer exclusive; right-click a ghost to convert them back to human | Redeemer | 10s cooldown, `ITEM_TOTEM_USE` |
| ❤️ **Second Chance** | Triggers when a human is infected; blocks infection and keeps human identity | Human | 180s cooldown, multiple sounds |
| 🧊 **Ice Ball** | Throwable snowball; applies **Slowness IV (4s)** on hit | Universal | `ENTITY_SNOWBALL_THROW` |
| 🥩 **Stinky Steak** | Eat regardless of hunger; grants **Speed III (14s)** + Glowing (10s) | Universal | Eating sound + `ENTITY_PLAYER_BURP` |
| 🔮 **Soul Control** | Human only; freezes all ghosts for **6 seconds** | Human | 18s cooldown, user/ghost sounds |
| 👁️ **Soul Detector** | Ghost only; reveals all player positions for **25 seconds** | Ghost | 35s cooldown, user/global sounds |
| 🪄 **Teleport Pearl** | Throw to teleport to landing point (right-click only) | Universal | 20s cooldown, `ENTITY_ENDER_PEARL_THROW` |
| 🧪 **Adrenaline** | Right-click for **Speed II (10s)** | Human | `ENTITY_PLAYER_SPLASH` |
| 🔥 **Frenzy Potion** | Right-click for **Speed II (10s)** | Ghost | `ENTITY_WITHER_SHOOT` |
| 🗡️ **Spear Rush** | Left-click to dash; consumed on use (requires server ≥ 1.21.4) | Universal | Built-in spear rush sound |
| 🧪 **Levitation Potion** | Right-click for **Levitation (4.5s)**, ~3.4 blocks height | Universal | `ENTITY_PLAYER_SPLASH` |

---

## 🔊 Sound System

### Item Distribution Sounds
- Each player receives an item: `ENTITY_ITEM_PICKUP` (different pitch per item)
- Global distribution complete: `BLOCK_NOTE_BLOCK_PLING`

### Faction Switch Sounds
- Human → Ghost: `ENTITY_VEX_AMBIENT` (eerie hum)
- Ghost → Human: `ENTITY_PLAYER_LEVELUP` (level up)

### Phase Sounds
- Last 3 seconds of preparation countdown: `BLOCK_NOTE_BLOCK_HAT` (once per second)
- Game start: `ENTITY_ENDER_DRAGON_GROWL` (global)
- Human victory: Humans hear `UI_TOAST_CHALLENGE_COMPLETE`; Ghosts hear `ENTITY_WITHER_DEATH`
- Ghost victory: Ghosts hear `UI_TOAST_CHALLENGE_COMPLETE`; Humans hear `ENTITY_WITHER_DEATH`

---

## 👻 Ghost-to-Human Feature

With **3 minutes** remaining, the system randomly converts some **non‑Mother ghosts** back to the human faction (disabled by default, can be enabled in config).

- Converted players retain accumulated ghost time records
- Receive a **20% extra bonus compensation**
- Still receive their ghost bonus inheritance

---

## 🏆 Economy & Reward System

### Entry & Prize Pool
- Players pay an **entry fee** (default 100 coins) which goes into the **Human Prize Pool**
- Server can add a **bonus** (default 5000 coins) to increase total pool

### Victory Bonus Distribution

| Winner | Distribution |
|--------|--------------|
| 👤 Human Victory | Human faction gets **70%** of pool; Ghost faction gets **30%** |
| 👻 Ghost Victory | Ghost faction gets **100%** of pool |

### Individual Bonus Calculation

| Faction | Calculation |
|---------|-------------|
| 👤 Human | 100% proportional to **survival time** |
| 👻 Ghost | 70% proportional to **ghost survival time** + 30% proportional to **number of infections** |

### Demon Hunter Reward
- Each ghost kill by a Demon Hunter grants **30% of the Human Prize Pool**
- **Mother Ghost** killing a Demon Hunter receives an additional **50% bonus** (higher than normal infection reward)

---

## ✨ Feature Systems

### 🌑 Dark Effect + Sprint Coexistence
- Uses native Minecraft **DARKNESS** (1.19+) or **BLINDNESS** (older versions)
- Adds **+30% movement speed** via attribute modifier to counteract dark effect's sprint inhibition
- Smart check every 10 ticks restores sprint if blocked
- Admins and creative mode players also receive dark effect

### 👻 Ghost Particle Effects
- Continuous orbiting particles around ghosts
- **Mother ghost**: red particles; **normal ghost**: green particles
- Supports 21 particle types, RGB color customization
- Configurable whether to show during preparation phase

### 💬 Comprehensive Language System (Cover version core improvement)
- 90+ default Chinese messages
- Smart fallback mechanism, no more error codes
- Error isolation protection, does not affect core gameplay
- Plugin startup protection

### ❤️ Heartbeat Sound System
- Humans hear the Warden's heartbeat sound during the game
- Admins can toggle with `/gostadmin heartbeat`

---

## 📋 Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/gost join` | Join game queue | gost.use |
| `/gost leave` | Leave game queue | gost.use |
| `/gost stop` | Force stop game (admin) | gost.admin |
| `/gost reload` | Reload config | gost.admin |
| `/gostadmin dark <on/off/status>` | Toggle dark effect | gost.admin |
| `/gostadmin heartbeat <on/off/status>` | Toggle heartbeat sound | gost.admin |
| `/gost start` | Force start game (bypass player limit) | gost.admin |
| `/gostadmin testmode` | Admin solo test mode | gost.admin |
| `/gostadmin giveitem <item name>` | Get any item (fuzzy match) | gost.admin |
| `/divineguardian status` | View Divine Guardian status | gost.admin |
| `/divineguardian setmode <1/2>` | Switch Divine Guardian mode | gost.admin |
| `/divineguardian setcharges <count>` | Set max defense charges | gost.admin |
| `/divineguardian force <player>` | Force activate Divine Guardian | gost.admin |
| `/ghostparticle status` | View particle status | gost.admin.ghostparticle |
| `/ghostparticle settype <type>` | Set particle type | gost.admin.ghostparticle |
| `/ghostparticle setmothercolor <R,G,B>` | Set mother ghost color | gost.admin.ghostparticle |

---

## 🔐 Permissions

| Permission Node | Description |
|-----------------|-------------|
| `gost.use` | Basic player permission (join/leave) |
| `gost.player` | Player permission (given to all players by default) |
| `gost.admin` | Admin permission (start/stop/reload/dark/heartbeat) |
| `gost.admin.divineguardian` | Divine Guardian management |
| `gost.admin.ghostparticle` | Ghost particle management |

---

## ⚙️ Configuration

Configuration file located at `plugins/Gost/config.yml`:

```yaml
# Game settings
game:
  duration: 420          # Game duration (seconds), default 7 minutes
  min-players: 2
  max-players: 16

# Economy settings
economy:
  entry-fee: 100.0       # Entry fee
  server-bonus: 5000.0   # Server bonus

# Dark effect settings
dark-effect:
  enabled: true          # Dark effect enabled by default
  description: "Does not affect sprint"

# Divine Guardian settings
divine-guardian:
  enabled: true
  trigger-human-count: 2
  holy-guardian:
    defense-charges: 3
    teleport-attacker: true
    teleport-radius: 10.0
  demon-hunter:
    phase-start-time: 90
    health:
      demon-hunter: 2.0
      mother-attack-damage: 1.0

# Ghost particle effect
ghost-particle:
  enabled: true
  particle-type: REDSTONE
  interval: 15
  count: 5
  mother-color: "255,0,0"
  normal-color: "0,255,0"
  
  # 📈 Version History (English)

## v2.3.0 Current | New Items & Admin Tools
- **New Items:**
  - **Spear Rush** (`GOLDEN_SPEAR`): Left-click to dash, consumed on use (≥1.21.4 only)
  - **Levitation Potion** (`LINGERING_POTION`): Right-click for Levitation 4.5s
- **New Admin Commands:**
  - `/gost start` — Force start (bypass player limit)
  - `/gostadmin testmode` — Solo test mode
  - `/gostadmin giveitem <name>` — Get any item (fuzzy match)
- **Added end-of-game reward leaderboard** with 🥇🥈🥉 medals for top 3
- Config version upgraded to **27**, plugin version upgraded to **2.3.0**

---

## v2.2.3 More immersive | Support English again
- New sound system (phase, item distribution, item usage, faction switch sounds)
- Centered subtitle hints for items
- English language support (`language.default: en_US`)
- Automatic config migration
- Fixed 1.20.x API compatibility
- Config version upgraded to **26**

---

## v2.2.2 Demon Hunter Overhaul
1. Ghost respawn mechanic (15s configurable)
2. Smart health adjustment (normal ghost 2❤, mother 3❤, demon hunter 2❤)
3. No healing during Demon Hunter phase
4. Reaper damage system (damage-based kills)
5. Divine Guardian 3-hit protection
6. Demon Hunter phase auto-activates Divine Guardian for all remaining humans

---

## v2.2.1_Cover Language Overhaul & Bug Fixes
- 90+ Chinese messages, smart fallback, error isolation
- Ice Ball fix (affects all players), default health set to 10 hearts
- Dark effect + sprint coexistence
- Divine Guardian fixes, item system fixes
- Config version upgraded to **20**

---

## v2.2.0 New Divine Guardian Mode 2
- Redeemer system
- Game mode switching (`/divineguardian setmode <1|2>`)
- Queue system optimization
- Code cleanup

---

## v2.1.3 First Stable Version
- Fixed admin immunity
- Creative mode auto-switch to survival

---

## v2.1.2 New Particle Effects
- Ghost particle effects (21 types, RGB colors)
- `/ghostparticle` management commands
- Config version upgraded to **14**
- New `gost.admin.ghostparticle` permission

---

## v2.1.1 Divine Guardian
- Divine Guardian system
- Visual effects and management commands
- Config version upgraded to **13**

---

## v2.1.0 Prize Pool Optimization
- Prize pool optimization
- Converted player bonus
- Heartbeat sound system
- Ghost-to-human feature

---

## v2.0.2 Dark Effect
- Dark effect system (`/gostadmin dark`)

---

## v2.0.1 Teleport Pearl & Stinky Steak
- Teleport pearl, stinky steak
- Smart countdown system

---

## v2.0.0 Initial Release
- Architecture refactor, area system, item system optimization

---

## v1.0.0 Deprecated
- ⚠️ Deprecated (mechanic inconsistent with author's intention)