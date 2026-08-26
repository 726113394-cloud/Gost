# Gost — 鬼抓人 · 救赎者 · 猎魔人 多阶段对抗插件

> 一款基于 Bukkit/Spigot 1.20.x - 1.21.x 的多人对抗小游戏插件，当前最新版本 **v2.3.1**。  
> 本插件灵感来源于游戏 **《Dead Realm》**——以此致敬那款曾带来无数欢乐的已逝佳作，并尝试在 Minecraft 中重现那份追逐与博弈的紧张与乐趣。  
> 玩家分为 **人类** 与 **鬼** 两个阵营，鬼负责感染人类，人类利用 **神圣守护**、**救赎者**、**猎魔人** 等机制周旋求生。  
> 支持 **1.20.x 至 1.21.x 全版本**（粒子/音效/物品自动版本兼容）。

---

## 📑 目录

- [下载与链接](#-下载与链接)
- [核心玩法](#-核心玩法)
- [神圣守护系统](#-神圣守护系统)
- [救赎者系统](#-救赎者系统)
- [猎魔人阶段（v2.3.1 重做）](#-猎魔人阶段v231-重做)
- [道具系统](#-道具系统)
- [经济与奖励系统](#-经济与奖励系统)
- [版本兼容性](#-版本兼容性)
- [特色系统](#-特色系统)
- [命令](#-命令)
- [权限](#-权限)
- [版本历史](#-版本历史)
- [开始游戏](#-开始游戏)
- [English Version](#-english-version)

---

## 📦 下载与链接

- [Modrinth 下载](https://modrinth.com/plugin/gost)
- [作者主页](https://726113394-cloud.github.io/PersonalPage/)
- [预览视频](https://www.bilibili.com/video/BV1aPQSBsE74/)
- [官方文档](https://726113394-cloud.github.io/Gost/)

---

## 🎯 核心玩法

### 游戏流程
1. **准备阶段** — 玩家进入匹配队列，满员后开始游戏。
2. **感染阶段** — 鬼玩家追逐并感染人类，人类可拾取道具自保。
3. **神圣守护阶段** — 剩余人类数量 ≤ 触发人数时，自动激活神圣守护。
4. **猎魔人阶段** — 游戏剩余 90 秒时进入，拥有神圣守护的人类变为猎魔人反杀鬼。

### 两大阵营

| 阵营 | 说明 |
|------|------|
| 👤 **人类** | 初始身份，被鬼感染后变为鬼。 |
| 👻 **鬼** | 感染人类使其加入鬼阵营，母体为鬼阵营领袖。 |

### 特殊角色

| 角色 | 说明 |
|------|------|
| 🛡️ **神圣守护** | 抵挡 **3 次** 鬼的攻击，每次攻击将进攻者随机传送。 |
| 🎯 **猎魔人** | 猎魔人阶段由人类转化，持有 **收割者** 武器反杀鬼。 |
| 👑 **母体** | 鬼阵营领袖，可攻击猎魔人，可感染普通人类。 |
| ✨ **救赎者** | 常驻角色，每局最多 2 名，持有神之救赎道具转化鬼回人类。 |

---

## 🛡️ 神圣守护系统

神圣守护系统的机制随版本演变，以下按版本阶段分别说明：

### 📜 v2.2.2 以前

| 项目 | 说明 |
|------|------|
| **激活条件** | 剩余人类 ≤ 触发人数（默认 2 人） |
| **防御次数** | 可抵挡 **2 次** 攻击 |
| **传送效果** | 持有者被攻击时随机传送 |
| **猎魔人阶段** | 所有神圣守护自动刷新防御次数，并转化为猎魔人 |

### 📜 v2.2.2 ~ v2.3.0

| 项目 | 说明 |
|------|------|
| **激活条件** | 剩余人类 ≤ 触发人数（默认 2 人） |
| **防御次数** | 可抵挡 **3 次** 攻击 |
| **传送效果** | 进攻者被随机传送到附近区域并减速 |
| **猎魔人阶段** | 所有神圣守护自动刷新防御次数，并转化为猎魔人 |

> 此版本神圣守护机制保持稳定，与 v2.2.2 起一致。

### 📜 v2.3.1+（当前）

| 项目 | 说明 |
|------|------|
| **激活条件** | 剩余人类 ≤ 触发人数（默认 2 人） |
| **防御次数** | 可抵挡 **3 次** 攻击 |
| **传送效果** | 进攻者被随机传送到附近区域并减速 |
| **猎魔人阶段** | 所有神圣守护自动刷新防御次数，并转化为猎魔人 |

> **v2.3.1 变化**：传统神圣守护机制保持不变，新增的救赎者系统拥有独立的神圣守护（详见下方救赎者板块）。

---

## ✨ 救赎者系统

救赎者系统的机制随版本演变，以下按版本阶段分别说明：

### 📜 v2.2.2 以前

| 项目 | 说明 |
|------|------|
| **激活条件** | 人类数量减少到 **1 人** 时，最后一名人类成为 **救赎者** |
| **神之救赎道具** | 救赎者获得专属道具，可转化鬼玩家回人类 |
| **随机传送** | 使用道具后救赎者被随机传送到安全位置 |
| **次数限制** | 道具可使用 **2 次**（可配置），使用完后回归普通人类 |
| **视觉效果** | 救赎者全程高亮显示，拥有速度效果 |

### 📜 v2.2.2 ~ v2.3.0

| 项目 | 说明 |
|------|------|
| **激活条件** | 被发放道具「神圣救赎」的人类成为 **救赎者**，无法再获得神圣守护，不可取消 |
| **神之救赎道具** | 救赎者获得专属道具，可转化鬼玩家回人类 |
| **随机传送** | 使用道具后救赎者被随机传送到安全位置 |
| **次数限制** | 道具可使用 **1 次**，使用完后依旧保留救赎者身份 |
| **视觉效果** | 救赎者全程有云雾围绕效果 |

> **版本演进**：v2.2.2 前为最后一人成为救赎者（2 次使用，用完后回归普通人类）；v2.2.2 起为随机发放道具成为救赎者（1 次使用，保留救赎者身份）。

### 📜 v2.3.1+（当前）— 救赎者常驻

| 项目 | 说明 |
|------|------|
| **常驻机制** | 救赎者 **常驻每一局游戏**，每局最多 **2 名** |
| **激活条件** | 随「神之救赎」道具发放 **随机绑定** 人类玩家 |
| **独立神圣守护** | 救赎者直接获得 **独立的神圣守护（2 次防御）**，不影响原有神圣守护系统 |
| **神之救赎道具** | 救赎者获得专属道具，可转化鬼玩家回人类 |
| **次数限制** | 道具可使用 **2 次**，使用后依旧保留救赎者身份 |
| **随机传送** | 使用道具后救赎者被随机传送到安全位置 |
| **视觉效果** | 救赎者全程有云雾围绕效果 |

> **v2.3.1 重大更新**：删除模式切换，救赎者常驻；独立守护 2 次 + 神之救赎 2 次；每局最多 2 名。

---

## ⚔️ 猎魔人阶段（v2.3.1 重做）

游戏剩余 90 秒时自动进入猎魔人阶段。该阶段所有拥有**神圣守护的人类**和**救赎者**变为 **猎魔人**。

### 📜 v2.2.2 ~ v2.3.0（旧版）

- 所有拥有神圣守护的人类变为 **猎魔人**
- 猎魔人持有 **收割者** 道具，基于伤害值击杀鬼玩家（可配置）
- **普通鬼** 不能感染人类，只能躲避猎杀
- **母体** 可以攻击猎魔人，每次造成配置的伤害值
- 母体需要攻击 **3 次** 才能破除猎魔人的神圣守护
- 所有 **神之救赎** 道具被清除
- 鬼玩家血量调整（普通鬼 2❤，母体鬼 3❤）
- 猎魔人阶段 **禁止回血**（自然恢复、饱食度、药水均无效）
- 鬼被击杀后进入 **旁观模式**（可配置复活时间）

### 📜 v2.3.1+（当前）— 重做

#### 🗡️ 收割者武器重做
- **左键单体攻击**：普通鬼 **2 次** 击杀，母体鬼 **4 次** 击杀（纯击中次数判定，不依赖血量）
- **右键范围技能「收割」**：4 格半径 AOE，10 秒冷却，冲击粒子 + 凋零死亡音效
- 攻击冷却 **2 秒**

#### 👻 鬼复活机制（优化）
- 被击杀后进入 **死亡状态**：隐身但可自由移动（不锁定坐标、不减速、不飞行）
- **10 秒复活倒计时**（ActionBar 显示）
- 复活时 **随机传送** 至游戏区域内某坐标（非原地）
- 复活后恢复原身份（普通鬼/母体）继续参与对局

#### 🎆 猎魔人击杀反馈
- 击杀位置绽放 **红黄 BURST 烟花** + 爆炸粒子 + 凋零死亡音效
- 击杀字幕（中英双语）：` 击杀成功!` / ` Elimination!`

#### 💎 母体进化系统（v2.3.1 新增）
- **触发条件**：猎魔人阶段，参与游戏玩家 **> 5 人**
- **生成**：游戏区域内随机坐标放置 **发光绿宝石**（附魔粒子 + 发光效果标记）
- **拾取条件**：**仅普通鬼** 可拾取
- **效果**：拾取后变身为母体鬼，获得母体血量，可猎杀猎魔人
- **公告/字幕/音效**（中英双语）

#### 🛡️ 猎魔人阶段防御规则
- **阵营伤害保护**：猎魔人无法伤害人类/猎魔人，鬼无法伤害鬼（母体无法击杀普通鬼），人类无法伤害人类
- **死亡拦截**：鬼/猎魔人真实死亡事件被取消，不会回重生点
- **环境伤害免疫**：鬼/猎魔人免疫坠落/岩浆/火焰等环境伤害
- **正常回血**：不再禁止回血，玩家可正常恢复

#### 👑 母体攻击猎魔人
- 无神圣守护猎魔人：母体 **4 次** 击中 → 感染（进入旁观）
- 有神圣守护猎魔人：先 **3 次** 破除守护，再 **4 次** 感染

#### ⛓️ 母体禁足
- 对局开始前母体随机锁定游戏区域坐标，**20 秒** 无法移动

---

## 🧰 道具系统

道具会在游戏过程中 **定时发放** 给玩家，每次发放间隔可在配置中调整（默认每 60 秒发放一次），同时区域会随机刷新道具。发放道具时会揭示所有玩家位置 **5 秒**。

### 背包管理系统（v2.3.1）
- 对局期间最多 **9 个** 道具
- 道具不可放入背包（强制移回物品栏）
- 道具**不堆叠**，不允许两个相同道具（人鬼通用道具除外）
- **收割者** / **神之救赎** 强制放第一格

### 道具列表

| 道具 | 效果 | 限制 | 版本备注 |
|------|------|------|----------|
| 🗡️ **收割者** | 猎魔人专属武器（左键攻击/右键技能「收割」） | 猎魔人 | v2.3.1 重做 |
| 💊 **神之救赎** | 右键鬼玩家使其转化回人类 | 救赎者 | v2.3.1 常驻，2 次使用 |
| ❤️ **第二次机会** | 被感染时被动触发，免疫感染+随机传送（180秒冷却） | 仅人类 | 一直存在 |
| 🧊 **凝冰球** | 右键投掷命中给对方施加缓慢效果 | 通用 | 一直存在 |
| 🥩 **臭牛排** | 右键**开始食用**，1秒后生效，获得速度效果 | 通用 | 一直存在 |
| 🔮 **控魂术** | 右键使用后全场鬼玩家无法移动 | 仅人类 | 一直存在 |
| 👁️ **灵魂探测器** | 右键使用后所有玩家发光 | 仅鬼 | 一直存在 |
| 🪄 **传送珍珠** | 右键投掷传送 | 通用 | 一直存在 |
| 💉 **肾上腺素** | 右键获得速度效果 | 仅人类 | 一直存在 |
| 🔥 **狂暴药水** | 右键获得速度效果 | 仅鬼 | 一直存在 |
| 🗡️ **冲刺矛** | 左键冲刺，消耗品（仅 ≥1.21.4 版本生成） | 通用 | v2.3.0 新增 |
| 🧪 **漂浮药水** | 右键获得漂浮效果 | 通用 | v2.3.0 新增 |

---

## 🏆 经济与奖励系统

### 入场与奖池
- 玩家加入游戏需支付 **入场费**（默认 100 金币），入场费汇入 **人类奖池**。
- 服务器可设置 **额外奖金**（默认 5000 金币），增加奖池总额。

### 胜利奖金分配

| 胜利方 | 分配规则 |
|--------|----------|
| 👤 人类胜利 | 人类阵容获得奖池 **70%**，鬼阵容获得 **30%** |
| 👻 鬼胜利 | 鬼阵容获得奖池 **100%** |

### 个人奖金计算

| 阵营 | 分配方式 |
|------|----------|
| 👤 人类 | 100% 按 **存活时间比例** 分配 |
| 👻 鬼 | 70% 按 **鬼存活时间** + 30% 按 **感染人数比例** 分配 |

### 猎魔人奖励
- 猎魔人每击杀一名鬼玩家，可获得 **人类奖池 30%** 的奖金。
- **母体** 击杀猎魔人可获得 **50%** 的额外奖励。

### 额外奖励（v2.3.1 新增）
游戏结束时由服务器额外支付，不占用奖池：

| 行为 | 奖励 |
|------|------|
| 救赎一名鬼玩家 | **100** 金币 |
| 猎魔人击杀普通鬼 | **50** 金币 |
| 猎魔人击杀母体鬼 | **100** 金币 |

### 奖金排行榜
游戏结束后显示奖金排行榜，前三名显示 🥇🥈🥉 奖牌。

---

## 🌐 版本兼容性

- **Minecraft 1.20+ 全版本（截止 2026.8.23）**
- 粒子名称自动兼容（`ParticleCompat` 运行时解析新旧枚举名）
- 音效名称自动兼容（`SoundCompat` 运行时解析）
- 冲刺矛（`GOLDEN_SPEAR`）仅 ≥1.21.4 生成，低版本自动禁用
- Vault 经济接口多版本回退

---

## ✨ 特色系统

### 🌑 黑暗效果与疾跑并存
- 使用 Minecraft 原生 **DARKNESS** 效果（1.19+）或 **BLINDNESS**（旧版本）。
- 通过属性修改器增加 **30% 移动速度**，抵消黑暗效果对疾跑的影响。
- 每 10 ticks 检查玩家疾跑状态，自动恢复被阻止的疾跑。
- 管理员和创造模式玩家也会受到黑暗效果影响。
- 管理员可通过 `/gostadmin dark` 控制开关。

### 👻 鬼玩家粒子效果
- 鬼玩家身上持续显示环绕粒子效果。
- **母体鬼**：红色粒子，**普通鬼**：绿色粒子。
- 支持 21 种粒子类型，RGB 颜色自定义。
- 可配置准备阶段是否显示。
- 管理员可通过 `/gostadmin particle` 管理。

### 💬 语言系统全面优化
- 大幅扩充默认消息库，**90+ 条**默认中文消息。
- 智能消息回退机制，不再返回错误代码。
- 错误隔离保护，不影响核心游戏流程。
- 插件启动保护，LanguageManager 初始化失败仍能运行。
- 支持英文语言包（`language.default: en_US`）。

### ❤️ 心跳声系统
- 游戏过程中人类方循环播放监守者出现时的心跳声。
- 管理员可通过 `/gostadmin heartbeat` 控制开关。

### 选区粒子框（v2.3.1）
- 设置选区后自动显示 **火焰粒子框**，持续 30 秒。
- 帮助管理员直观确认游戏区域边界。

### 双语字幕（v2.3.1）
- 复活等待、重生、收割、救赎、母体禁足等核心事件新增 **中英双语字幕**。
- 根据服务器语言设置自动切换。

---

## 📋 命令

### 命令整合说明（v2.3.1）
v2.3.1 起，`/divineguardian` 和 `/ghostparticle` 命令已整合到 `/gostadmin` 下。旧版命令仍然可用，但建议使用新版统一命令格式。

### 玩家命令

| 命令 | 说明 | 权限 |
|------|------|------|
| `/gost join` | 加入游戏队列 | gost.player |
| `/gost leave` | 离开游戏队列 | gost.player |
| `/gost info` | 查看游戏信息 | gost.player |
| `/gost help` | 显示帮助 | gost.player |

### 管理员命令

| 命令 | 说明 | 权限 | 版本 |
|------|------|------|------|
| `/gost start` | 管理员强制开始游戏（跳过人数限制） | gost.admin | v2.3.0+ |
| `/gost stop` | 强制结束游戏 | gost.admin | 一直存在 |
| `/gost reload` | 重载配置文件 | gost.admin | 一直存在 |
| `/gostadmin tool` | 获取选区工具（岩浆膏） | gost.admin | v2.3.0+ |
| `/gostadmin pos1/pos2` | 设置选区点 | gost.admin | v2.3.0+ |
| `/gostadmin save/list/load/delete/info` | 区域管理 | gost.admin | 一直存在 |
| `/gostadmin dark <on\|off\|status>` | 黑暗效果管理 | gost.admin | v2.0.2+ |
| `/gostadmin heartbeat <on\|off\|status>` | 心跳声管理 | gost.admin | v2.1.0+ |
| `/gostadmin testmode` | 单人测试模式 | gost.admin | v2.3.0+ |
| `/gostadmin giveitem <道具名>` | 直接获得指定道具（支持模糊匹配） | gost.admin | v2.3.0+ |
| `/gostadmin economy <set\|status>` | 经济管理 | gost.admin | 一直存在 |
| `/gostadmin divine <status\|clear>` | 神圣守护管理（替代 `/divineguardian`） | gost.admin | 整合自 v2.1.1 |
| `/gostadmin particle <status\|enable\|disable>` | 鬼粒子管理（替代 `/ghostparticle`） | gost.admin | 整合自 v2.1.2 |

---

## 🔐 权限

| 权限节点 | 说明 | 默认 |
|----------|------|------|
| `gost.use` | 基础命令权限 | **所有玩家** |
| `gost.player` | 玩家游戏权限 | **所有玩家** |
| `gost.admin` | 管理员权限 | OP |

> **权限精简说明（v2.3.1）**：旧版 `gost.admin.divineguardian` 和 `gost.admin.ghostparticle` 权限已合并到 `gost.admin` 中。旧版权限节点仍然有效，但新版统一使用 `gost.admin` 管理所有管理员功能。

---

## 📈 版本历史

### v2.3.1（当前）— 救赎者重构 & 收割者重做

**✨ 救赎者系统重构**
- 删除神圣守护模式切换，救赎者**常驻每局游戏**
- 每局最多 **2 名** 救赎者，随「神之救赎」道具发放随机绑定
- 救赎者获得**独立神圣守护（2 次防御）** + **2 次**「神之救赎」使用次数

**⚔️ 收割者武器重做**
- 左键单体：普通鬼 **2 次**击杀，母体鬼 **4 次**击杀（纯击中次数）
- 右键范围技能「收割」：4 格 AOE，10 秒冷却

**👻 鬼复活机制优化**
- 死亡状态：隐身但可自由移动（不锁定坐标、不减速、不飞行）
- 复活方式：**随机地点复活**（游戏区域内随机坐标传送），不再原地复活
- 保留 10 秒倒计时 + 重生字幕/音效

**🎆 击杀烟花反馈**
- 猎魔人击杀鬼时，击杀位置绽放红黄 BURST 烟花
- 爆炸粒子 + 凋零死亡音效
- 击杀字幕（中英双语）

**💎 母体进化系统**
- 触发条件：猎魔人阶段，参与游戏玩家 > 5 人
- 生成：游戏区域内随机位置放置发光绿宝石
- 拾取条件：**仅普通鬼**可拾取
- 效果：拾取后变身为母体鬼

**🛡️ 猎魔人阶段防御重构**
- 新增阵营伤害保护、死亡拦截、环境伤害保护
- **删除「禁止回血」设计**

**🎒 背包管理系统**
- 最多 9 个道具，不可放入背包，不堆叠

**🔧 命令整合与权限精简**
- `/divineguardian` / `/ghostparticle` 并入 `/gostadmin`
- 仅保留 `gost.use` / `gost.player` / `gost.admin` 三个权限

**其他新增**
- 选区粒子框（设置选区后显示火焰粒子框 30 秒）
- 双语字幕（核心事件新增中英双语字幕）
- 配置版本升级到 28

---

### v2.3.0 — 新道具 & 管理员工具

- **新增道具**：冲刺矛（≥1.21.4）、漂浮药水
- **新增管理员命令**：`/gost start`、`/gostadmin testmode`、`/gostadmin giveitem`
- **新增游戏结束奖金排行榜**
- 配置版本升级到 27

---

### v2.2.3 — 视觉、听觉沉浸 & 重新支持英文

- **新增音效系统**：阶段音效、发放道具音效、道具使用音效
- **新增道具居中字幕提示**
- **新增英文语言支持**：`language.default: en_US`
- **配置自动迁移**：检测到配置版本不一致时自动迁移
- 修复 1.20.x API 兼容性
- 配置版本升级到 26

---

### v2.2.2 — 猎魔人登场（大改）

1. **鬼玩家复活机制**：猎魔人阶段鬼玩家被击杀后进入旁观模式，可配置复活倒计时（默认 15 秒）
2. **智能血量调整系统**：普通鬼 2 点，母体鬼 3 点，猎魔人 2 点（均可配置）
3. **禁止回血机制**：猎魔人阶段禁止所有玩家回血
4. **收割者伤害系统**：基于伤害值而非攻击次数的击杀逻辑
5. **神圣守护 3 次抵挡机制**：母体需攻击 3 次才能破除猎魔人守护
6. **猎魔人阶段自动激活**所有人类神圣守护

---

### v2.2.1_Cover — 第二稳定版本 | 修复神圣守护 & 凝冰球问题

- **语言系统全面优化**：90+ 条默认中文消息，智能回退，错误隔离
- **凝冰球修复**（对所有玩家生效），默认血量调整为 10 颗心
- **黑暗效果与疾跑并存系统**
- **神圣守护系统修复**
- 配置版本升级到 20

---

### v2.2.0 — 新增神圣守护模式 2（救赎者）

- **救赎者系统**：最后一名人类成为救赎者，神之救赎道具转化鬼玩家，2 次使用限制
- **游戏模式切换**：`/divineguardian setmode <1|2>`
- **队列系统全面优化**
- 代码精简与性能提升

---

### v2.1.3 — 首个稳定版本 | 管理员可参与

- 修复管理员游戏效果免疫问题
- 创造模式玩家自动切换为生存模式

---

### v2.1.2 — 新增粒子效果

- **鬼玩家粒子效果系统**：持续环绕粒子，颜色区分（母体红/普通绿）
- 支持 21 种粒子类型，RGB 颜色系统
- **管理命令**：`/ghostparticle` 全套命令
- 新增 `gost.admin.ghostparticle` 权限节点
- 配置版本升级到 14

---

### v2.1.1 — 神圣守护（大改）

- **新增神圣守护系统**：最后一位人类自动激活，3 次免疫感染机会（可配置）
- 被攻击时随机传送，获得速度 I + 发光效果
- **管理命令**：`/divineguardian` 全套命令
- 配置版本升级到 13

---

### v2.1.0 — 奖金分配优化

- **奖金分配系统全面优化**
- **转换玩家奖金优化**：被转换回人类的玩家获得 20% 额外奖金补偿
- **新增心跳声系统**：`/gostadmin heartbeat`
- **道具系统升级**：臭牛排新增发光效果
- **新增鬼转人类功能**（默认关闭）

---

### v2.0.2 — 修复问题、黑暗沉浸

- 修复编译错误，修复灵魂探测器失效
- **新增黑暗效果系统**：`/gostadmin dark`

---

### v2.0.1 — 新增传送珍珠 & 臭牛排

- 新增传送珍珠、臭牛排
- 智能倒计时系统

---

### v2.0.0 — 最初版本

- 架构重构，独立区域系统，道具系统优化

---

### v1.0.0 — 已作废

- 初始版本，因机制与作者意图不一致，不再使用

---

## 🚀 开始游戏

使用命令 `/gost join` 加入队列，体验生死追逐的乐趣！  
- 感染方式：鬼玩家左键/右键点击人类。  
- 道具发放时全体高亮 5 秒。  
- 猎魔人阶段带来全新反杀体验。  

在 Gost 的世界里，生存不仅需要速度，更需要智慧与策略。

---

# English Version

## Gost — Ghost Chase · Redeemer · Demon Hunter Multi‑Stage Confrontation Plugin

> A Minecraft multiplayer mini‑game plugin based on Bukkit/Spigot 1.20.x – 1.21.x, current version **v2.3.1**.  
> Inspired by **Dead Realm** – a tribute to that lost gem that brought countless joys, and an attempt to recreate the thrill of chase and tactical confrontation in Minecraft.  
> Players are divided into **Human** and **Ghost** factions. Ghosts infect Humans, while Humans use **Divine Guardian**, **Redeemer**, and **Demon Hunter** mechanics to survive and fight back.  
> Fully compatible with **1.20.x to 1.21.x** (particle/sound/item auto‑versioning).

---

## Table of Contents

- [Downloads & Links](#downloads--links)
- [Core Gameplay](#core-gameplay)
- [Divine Guardian System](#divine-guardian-system)
- [Redeemer System](#redeemer-system)
- [Demon Hunter Phase (reworked in v2.3.1)](#demon-hunter-phase-reworked-in-v231)
- [Item System](#item-system)
- [Economy & Reward System](#economy--reward-system)
- [Version Compatibility](#version-compatibility)
- [Feature Systems](#feature-systems)
- [Commands](#commands)
- [Permissions](#permissions)
- [Version History](#version-history)
- [Getting Started](#getting-started)

---

## Downloads & Links

- [Modrinth Download](https://modrinth.com/plugin/gost)
- [Author’s Homepage](https://726113394-cloud.github.io/PersonalPage/)
- [Preview Video](https://www.bilibili.com/video/BV1aPQSBsE74/)
- [Official Documentation](https://726113394-cloud.github.io/Gost/)

---

## Core Gameplay

### Game Flow
1. **Preparation Phase** – Players join the queue; game starts when full.
2. **Infection Phase** – Ghosts chase and infect Humans; Humans pick up items to defend themselves.
3. **Divine Guardian Phase** – Automatically activated when remaining Humans ≤ trigger count.
4. **Demon Hunter Phase** – Enters when 90 seconds remain; Humans with Divine Guardian become Demon Hunters.

### Two Factions

| Faction | Description |
|---------|-------------|
| 👤 **Human** | Initial identity; becomes Ghost after infection. |
| 👻 **Ghost** | Infects Humans to expand the Ghost faction; the Mother Ghost is the faction leader. |

### Special Roles

| Role | Description |
|------|-------------|
| 🛡️ **Divine Guardian** | Blocks **3** Ghost attacks; each attack teleports the attacker. |
| 🎯 **Demon Hunter** | Transformed from Humans during Demon Hunter phase; wields **Reaper** to kill Ghosts. |
| 👑 **Mother Ghost** | Ghost faction leader; can attack Demon Hunters and infect normal Humans. |
| ✨ **Redeemer** | Permanent role, max 2 per game, holds Holy Redemption to convert Ghosts back to Humans. |

---

## Divine Guardian System

The Divine Guardian system has evolved across versions:

### 📜 Before v2.2.2

| Item | Description |
|------|-------------|
| **Trigger** | Remaining Humans ≤ trigger count (default 2) |
| **Defense charges** | Blocks **2** attacks |
| **Teleport effect** | Holder is teleported when attacked |
| **Demon Hunter phase** | Divine Guardian refreshes; holders become Demon Hunters |

### 📜 v2.2.2 ~ v2.3.0

| Item | Description |
|------|-------------|
| **Trigger** | Remaining Humans ≤ trigger count (default 2) |
| **Defense charges** | Blocks **3** attacks |
| **Teleport effect** | Attacker is teleported to nearby area and slowed |
| **Demon Hunter phase** | Divine Guardian refreshes; holders become Demon Hunters |

> This version's Divine Guardian mechanism remained stable, consistent with v2.2.2 onward.

### 📜 v2.3.1+ (Current)

| Item | Description |
|------|-------------|
| **Trigger** | Remaining Humans ≤ trigger count (default 2) |
| **Defense charges** | Blocks **3** attacks |
| **Teleport effect** | Attacker is teleported to nearby area and slowed |
| **Demon Hunter phase** | Divine Guardian refreshes; holders become Demon Hunters |

> **v2.3.1 Change**: Traditional Divine Guardian mechanism remains unchanged; the new Redeemer system has its own independent Divine Guardian (see Redeemer section below).

---

## Redeemer System

The Redeemer system has evolved across versions:

### 📜 Before v2.2.2

| Item | Description |
|------|-------------|
| **Trigger** | When Humans reduce to **1**, the last Human becomes the **Redeemer** |
| **Holy Redemption** | Redeemer gets exclusive item to convert Ghosts back to Humans |
| **Random teleport** | Redeemer is teleported to a safe location after use |
| **Usage limit** | **2 uses** (configurable); reverts to normal Human after exhaustion |
| **Visual effects** | Full highlight + speed effect |

### 📜 v2.2.2 ~ v2.3.0

| Item | Description |
|------|-------------|
| **Trigger** | Player who receives the "Holy Redemption" item becomes **Redeemer**; cannot obtain Divine Guardian |
| **Holy Redemption** | Redeemer gets exclusive item to convert Ghosts back to Humans |
| **Random teleport** | Redeemer is teleported to a safe location after use |
| **Usage limit** | **1 use**; retains Redeemer status after use |
| **Visual effects** | Surrounded by mist |

> **Evolution**: Before v2.2.2, the last Human became Redeemer (2 uses, reverts to normal). From v2.2.2 onward, random item assignment (1 use, retains Redeemer status).

### 📜 v2.3.1+ (Current) — Redeemer Permanent

| Item | Description |
|------|-------------|
| **Permanent** | Redeemers **appear in every game**, max **2 per game** |
| **Trigger** | **Randomly bound** to Human players via "Holy Redemption" item |
| **Independent Divine Guardian** | Redeemers get **independent Divine Guardian (2 defenses)**, separate from the main system |
| **Holy Redemption** | Redeemer gets exclusive item to convert Ghosts back to Humans |
| **Usage limit** | **2 uses**; retains Redeemer status after use |
| **Random teleport** | Redeemer is teleported to a safe location after use |
| **Visual effects** | Surrounded by mist |

> **v2.3.1 Major Update**: Removed mode switching; Redeemers are permanent; independent Guardian (2 defenses) + Holy Redemption (2 uses); max 2 per game.

---

## Demon Hunter Phase (reworked in v2.3.1)

The phase automatically starts when 90 seconds remain. All Humans with **Divine Guardian** and **Redeemers** become **Demon Hunters**.

### 📜 v2.2.2 ~ v2.3.0 (Old)

- All Humans with Divine Guardian become **Demon Hunters**
- Demon Hunters hold **Reaper** item, killing Ghosts based on damage values (configurable)
- **Normal Ghosts** cannot infect Humans; can only hide
- **Mother Ghost** can attack Demon Hunters, dealing configured damage
- Mother needs **3 attacks** to break a Demon Hunter's Divine Guardian
- All **Holy Redemption** items are cleared
- Ghost health adjusted (normal 2❤, mother 3❤)
- **Healing disabled** during Demon Hunter phase
- Killed Ghosts enter **spectator mode** (respawn time configurable)

### 📜 v2.3.1+ (Current) — Reworked

#### 🗡️ Reaper Weapon Rework
- **Left-click single-target**: kills normal Ghost in **2 hits**, Mother Ghost in **4 hits** (pure hit-count, not damage-based)
- **Right-click area skill "Harvest"**: 4-block radius AOE, 10s cooldown, impact particles + Wither death sound
- Attack cooldown: **2 seconds**

#### 👻 Ghost Respawn Mechanic (optimized)
- Killed Ghosts enter **death state**: invisible but can move freely (no coordinate lock, no slow, no flying)
- **10-second respawn countdown** (displayed in ActionBar)
- Respawn with **random teleport** to a coordinate in the game area (not at death location)
- After respawn, retain original identity (normal Ghost/Mother)

#### 🎆 Demon Hunter Kill Feedback
- Kill location displays **red-yellow BURST fireworks** + explosion particles + Wither death sound
- Kill subtitle (bilingual): ` 击杀成功!` / ` Elimination!`

#### 💎 Mother Evolution System (new in v2.3.1)
- **Trigger**: Demon Hunter phase, with **> 5 players** in the game
- **Spawn**: Random coordinate in game area, **glowing emerald** (enchantment particles + glow effect)
- **Pickup**: **Only normal Ghosts** can pick it up
- **Effect**: Transforms into Mother Ghost, gains Mother health, can hunt Demon Hunters
- **Notifications**: Global broadcast + subtitle + sound (bilingual)

#### 🛡️ Demon Hunter Phase Defense Rules
- **Faction damage protection**: Demon Hunters cannot harm Humans/Demon Hunters; Ghosts cannot harm Ghosts; Humans cannot harm Humans
- **Death interception**: Real death events for Ghosts/Demon Hunters are cancelled
- **Environmental damage immunity**: Ghosts/Demon Hunters immune to fall/lava/fire damage
- **Normal healing**: Healing no longer disabled; players can regenerate normally

#### 👑 Mother Attacking Demon Hunters
- Demon Hunter without Divine Guardian: Mother needs **4 hits** → infection (sent to spectator)
- Demon Hunter with Divine Guardian: first **3 hits** break Guardian, then **4 hits** to infect

#### ⛓️ Mother Immobilization
- At game start, Mother is locked to a random coordinate and **cannot move for 20 seconds**

---

## Item System

Items are distributed periodically (default every 60 seconds) and also spawn randomly in the area. All players are highlighted for **5 seconds** when items are distributed.

### Inventory Management System (v2.3.1)
- Max **9 item types** during a game
- Items cannot be moved to backpack (forced back to hotbar)
- Items **do not stack**; no duplicate items allowed (except universal items)
- **Reaper** / **Holy Redemption** forced to first hotbar slot

### Item List

| Item | Effect | Restriction | Version Notes |
|------|--------|-------------|---------------|
| 🗡️ **Reaper** | Demon Hunter weapon (left-click attack / right-click "Harvest" skill) | Demon Hunter | Reworked in v2.3.1 |
| 💊 **Holy Redemption** | Right-click a Ghost to convert them back to Human | Redeemer | Permanent since v2.3.1, 2 uses |
| ❤️ **Second Chance** | Passive trigger on infection; blocks infection + random teleport (180s cooldown) | Human only | Always present |
| 🧊 **Ice Ball** | Throwable; applies slowness on hit | Universal | Always present |
| 🥩 **Stinky Steak** | Right-click to eat (1s); grants speed + glowing | Universal | Always present |
| 🔮 **Soul Control** | Right-click freezes all Ghosts for 6s | Human only | Always present |
| 👁️ **Soul Detector** | Right-click reveals all players | Ghost only | Always present |
| 🪄 **Teleport Pearl** | Right-click throw to teleport | Universal | Always present |
| 💉 **Adrenaline** | Right-click for speed effect | Human only | Always present |
| 🔥 **Frenzy Potion** | Right-click for speed effect | Ghost only | Always present |
| 🗡️ **Spear Rush** | Left-click to dash (consumed) (only on ≥1.21.4) | Universal | v2.3.0+ |
| 🧪 **Levitation Potion** | Right-click for levitation effect | Universal | v2.3.0+ |

---

## Economy & Reward System

### Entry Fee & Prize Pool
- Players pay an **entry fee** (default 100 coins), which goes into the **Human Prize Pool**.
- Server can add a **bonus** (default 5000 coins).

### Victory Bonus Distribution

| Winner | Distribution |
|--------|--------------|
| 👤 Human Victory | Humans get **70%** of the pool; Ghosts get **30%** |
| 👻 Ghost Victory | Ghosts get **100%** of the pool |

### Individual Bonus Calculation

| Faction | Calculation |
|---------|-------------|
| 👤 Human | 100% proportional to **survival time** |
| 👻 Ghost | 70% proportional to **ghost survival time** + 30% proportional to **number of infections** |

### Demon Hunter Bonus
- Each Ghost kill by a Demon Hunter grants **30% of the Human Prize Pool**.
- **Mother Ghost** killing a Demon Hunter gets an extra **50%** bonus.

### Extra Rewards (v2.3.1)
Paid by server at game end, not taken from the pool:

| Action | Reward |
|--------|--------|
| Redeem a Ghost | **100** coins |
| Demon Hunter kills a normal Ghost | **50** coins |
| Demon Hunter kills a Mother Ghost | **100** coins |

### Bonus Leaderboard
At game end, a bonus leaderboard is shown with 🥇🥈🥉 medals.

---

## Version Compatibility

- **Minecraft 1.20+ all versions (as of 2026-08-23)**
- Particle names auto-compatible (`ParticleCompat` resolves at runtime)
- Sound names auto-compatible (`SoundCompat` resolves at runtime)
- Spear Rush (`GOLDEN_SPEAR`) only spawns on ≥1.21.4; disabled on older versions
- Vault economy interface with fallback for multiple versions

---

## Feature Systems

### 🌑 Dark Effect + Sprint Coexistence
- Uses native **DARKNESS** (1.19+) or **BLINDNESS** (older versions)
- Adds **+30% movement speed** via attribute modifier to counteract sprint inhibition
- Checks sprint status every 10 ticks and restores if blocked
- Admins and creative mode players also receive the effect
- Toggle with `/gostadmin dark`

### 👻 Ghost Particle Effects
- Continuous orbiting particles around Ghosts
- **Mother Ghost**: red particles; **normal Ghost**: green particles
- Supports 21 particle types, RGB color customization
- Configurable whether to show during preparation phase
- Managed with `/gostadmin particle`

### 💬 Comprehensive Language System
- 90+ default Chinese messages with smart fallback
- Error isolation protection; does not affect core gameplay
- Plugin startup protection
- English language support (`language.default: en_US`)

### ❤️ Heartbeat Sound System
- Humans hear the Warden's heartbeat sound during the game
- Toggle with `/gostadmin heartbeat`

### Selection Particle Box (v2.3.1)
- After setting a selection, displays a **flame particle box for 30 seconds**
- Helps admins visualize the game area boundary

### Bilingual Subtitles (v2.3.1)
- Core events (respawn, harvest, redemption, immobilization, etc.) now have **bilingual subtitles**
- Auto-switches based on server language setting

---

## Commands

### Command Integration Note (v2.3.1)
From v2.3.1, `/divineguardian` and `/ghostparticle` commands have been integrated into `/gostadmin`. Old commands still work, but it's recommended to use the new unified format.

### Player Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/gost join` | Join game queue | gost.player |
| `/gost leave` | Leave game queue | gost.player |
| `/gost info` | View game info | gost.player |
| `/gost help` | Show help | gost.player |

### Admin Commands

| Command | Description | Permission | Version |
|---------|-------------|------------|---------|
| `/gost start` | Force start game (bypass player limit) | gost.admin | v2.3.0+ |
| `/gost stop` | Force stop game | gost.admin | Always |
| `/gost reload` | Reload config | gost.admin | Always |
| `/gostadmin tool` | Get selection tool | gost.admin | v2.3.0+ |
| `/gostadmin pos1/pos2` | Set selection points | gost.admin | v2.3.0+ |
| `/gostadmin save/list/load/delete/info` | Area management | gost.admin | Always |
| `/gostadmin dark <on\|off\|status>` | Dark effect control | gost.admin | v2.0.2+ |
| `/gostadmin heartbeat <on\|off\|status>` | Heartbeat control | gost.admin | v2.1.0+ |
| `/gostadmin testmode` | Solo test mode | gost.admin | v2.3.0+ |
| `/gostadmin giveitem <name>` | Get any item (fuzzy match) | gost.admin | v2.3.0+ |
| `/gostadmin economy <set\|status>` | Economy management | gost.admin | Always |
| `/gostadmin divine <status\|clear>` | Divine Guardian management (replaces `/divineguardian`) | gost.admin | Integrated since v2.1.1 |
| `/gostadmin particle <status\|enable\|disable>` | Ghost particle management (replaces `/ghostparticle`) | gost.admin | Integrated since v2.1.2 |

---

## Permissions

| Permission Node | Description | Default |
|-----------------|-------------|---------|
| `gost.use` | Basic command permission | **All players** |
| `gost.player` | Player game permission | **All players** |
| `gost.admin` | Admin permission | OP |

> **Permission Simplification (v2.3.1)**: Old `gost.admin.divineguardian` and `gost.admin.ghostparticle` permissions have been merged into `gost.admin`. Old permission nodes still work, but the new unified `gost.admin` is recommended for all admin functions.

---

## Version History

### v2.3.1 (Current) — Redeemer Refactor & Reaper Rework

**✨ Redeemer System Refactor**
- Removed Divine Guardian mode switching; Redeemers are **permanent in every game**
- Max **2 Redeemers** per game, randomly bound via "Holy Redemption" item
- Redeemers get **independent Divine Guardian (2 defenses)** + **2 uses** of Holy Redemption

**⚔️ Reaper Weapon Rework**
- Left-click single-target: normal Ghost **2 hits** kill, Mother Ghost **4 hits** kill (pure hit-count)
- Right-click area skill "Harvest": 4-block AOE, 10s cooldown

**👻 Ghost Respawn Mechanic Optimization**
- Death state: invisible but can move freely
- Respawn: **random location teleport** (in game area), no longer原地复活
- 10-second countdown + respawn subtitle/sound

**🎆 Kill Firework Feedback**
- Red-yellow BURST fireworks at kill location
- Explosion particles + Wither death sound
- Bilingual kill subtitle

**💎 Mother Evolution System**
- Trigger: Demon Hunter phase with > 5 players
- Spawn: glowing emerald at random location in game area
- Pickup: **only normal Ghosts** can pick up
- Effect: transforms into Mother Ghost

**🛡️ Demon Hunter Phase Defense Rework**
- Added faction damage protection, death interception, environmental damage protection
- **Removed "no healing" design**

**🎒 Inventory Management System**
- Max 9 items, cannot be moved to backpack, no stacking

**🔧 Command Integration & Permission Simplification**
- `/divineguardian` / `/ghostparticle` merged into `/gostadmin`
- Only 3 permissions remain: `gost.use` / `gost.player` / `gost.admin`

**Other New Features**
- Selection particle box (flame particle box for 30 seconds)
- Bilingual subtitles for core events
- Config version upgraded to 28

---

### v2.3.0 — New Items & Admin Tools

- **New items**: Spear Rush (≥1.21.4), Levitation Potion
- **New admin commands**: `/gost start`, `/gostadmin testmode`, `/gostadmin giveitem`
- **New end-game bonus leaderboard**
- Config version upgraded to 27

---

### v2.2.3 — Visual/Audio Immersion & English Support

- **New sound system**: phase sounds, item distribution sounds, item usage sounds
- **Centered subtitle hints** for items
- **English language support**: `language.default: en_US`
- **Auto-config migration**
- Fixed 1.20.x API compatibility
- Config version upgraded to 26

---

### v2.2.2 — Demon Hunter Debut (Major Overhaul)

1. **Ghost respawn mechanic**: spectator mode after being killed by Demon Hunter, configurable respawn timer (default 15s)
2. **Smart health adjustment**: normal Ghost 2❤, Mother 3❤, Demon Hunter 2❤ (all configurable)
3. **Healing disabled** during Demon Hunter phase
4. **Reaper damage system**: damage-based kills (not hit-count)
5. **Divine Guardian 3-hit protection**: Mother needs 3 attacks to break
6. **Auto-activation**: all Humans get Divine Guardian upon entering Demon Hunter phase

---

### v2.2.1_Cover — Language Overhaul & Fixes

- **Language system optimization**: 90+ Chinese messages, smart fallback, error isolation
- **Ice Ball fix** (affects all players), default health restored to 10 hearts
- **Dark effect + sprint coexistence system**
- **Divine Guardian system fixes**
- Config version upgraded to 20

---

### v2.2.0 — New Divine Guardian Mode 2 (Redeemer)

- **Redeemer system**: last Human becomes Redeemer with Holy Redemption (2 uses)
- **Mode switching**: `/divineguardian setmode <1|2>`
- **Queue system optimization**
- Code cleanup and performance improvements

---

### v2.1.3 — First Stable Version

- Fixed admin immunity issues
- Creative mode players auto-switch to survival

---

### v2.1.2 — Particle Effects

- **Ghost particle effects**: orbiting particles, color-coded (Mother red / normal green)
- Supports 21 particle types, RGB color system
- **Management commands**: `/ghostparticle` full command set
- New `gost.admin.ghostparticle` permission
- Config version upgraded to 14

---

### v2.1.1 — Divine Guardian (Major)

- **New Divine Guardian system**: last Human auto-activates, 3 infection immunity chances (configurable)
- Random teleport on attack, Speed I + Glowing effect
- **Management commands**: `/divineguardian` full command set
- Config version upgraded to 13

---

### v2.1.0 — Prize Pool Optimization

- **Prize pool system optimization**
- **Converted player bonus**: converted players get 20% extra bonus compensation
- **New heartbeat sound system**: `/gostadmin heartbeat`
- **Item system upgrade**: Stinky Steak gets glowing effect
- **New Ghost-to-Human feature** (disabled by default)

---

### v2.0.2 — Bug Fixes & Dark Effect

- Fixed compilation errors, fixed Soul Detector
- **New dark effect system**: `/gostadmin dark`

---

### v2.0.1 — Teleport Pearl & Stinky Steak

- Added Teleport Pearl and Stinky Steak
- Smart countdown system

---

### v2.0.0 — Initial Release

- Architecture refactor, independent area system, item system optimization

---

### v1.0.0 — Deprecated

- Initial version, deprecated due to mechanics not matching author's intent

---

## Getting Started

Use `/gost join` to join the queue and experience the thrill of life-and-death chase!
- Infection method: Ghosts left-/right-click Humans.
- All players are highlighted for 5 seconds when items are distributed.
- The Demon Hunter phase brings a brand-new counter-attack experience.

In the world of Gost, survival requires not only speed but also wisdom and strategy.