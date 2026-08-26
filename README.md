# Gost — 鬼抓人 · 救赎者 · 猎魔人 多阶段对抗插件

> 一款基于 Bukkit/Spigot 1.20 - 26.1.2 的多人对抗小游戏插件，当前最新版本 **v2.3.1**。  
> 本插件灵感来源于游戏 **《Dead Realm》**——以此致敬那款曾带来无数欢乐的已逝佳作，并尝试在 Minecraft 中重现那份追逐与博弈的紧张与乐趣。  
> 玩家分为 **人类** 与 **鬼** 两个阵营，鬼负责感染人类，人类利用 **神圣守护**、**救赎者**、**猎魔人** 等机制周旋求生。  
> 支持 ** 1.20 - 26.1.2 全版本**（粒子/音效/物品自动版本兼容）。

---

## 📑 目录

- [下载与链接](#-下载与链接)
- [核心玩法](#-核心玩法)
- [神圣守护系统](#-神圣守护系统)
- [救赎者系统（v2.3.1 常驻化）](#-救赎者系统v231-常驻化)
- [猎魔人阶段（v2.3.1 重做）](#-猎魔人阶段v231-重做)
- [道具系统](#-道具系统)
- [经济与奖励系统](#-经济与奖励系统)
- [版本兼容性](#-版本兼容性)
- [特色系统](#-特色系统)
- [命令](#-命令)
- [权限](#-权限)
- [版本历史与机制演变](#-版本历史与机制演变)
- [开始游戏](#-开始游戏)
- [English Version](#-english-version)

---

## 📦 下载与链接

- [Modrinth 下载](https://modrinth.com/plugin/gost)
- [作者主页](https://726113394-cloud.github.io/PersonalPage/)
- [预览视频](https://www.bilibili.com/video/BV1aPQSBsE74/)
- [2.3.0 更新介绍视频](https://www.bilibili.com/video/BV1eHKj69E4H/)（v2.3.1 为后续迭代，核心变化见下文）

---

## 🎯 核心玩法

### 游戏流程
1. **准备阶段** — 玩家进入匹配队列，满员后开始游戏。
2. **感染阶段** — 鬼玩家追逐并感染人类，人类可拾取道具自保。
3. **神圣守护阶段** — 剩余人类数量 ≤ 触发人数时，自动激活神圣守护。
4. **猎魔人阶段** — 游戏剩余 90 秒时进入（≥v2.2.2 引入），剩余人类变为猎魔人反杀鬼。

### 两大阵营

| 阵营 | 说明 |
|------|------|
| 👤 **人类** | 初始身份，被鬼感染后变为鬼。 |
| 👻 **鬼**   | 感染人类使其加入鬼阵营，母体为鬼阵营领袖。 |

### 特殊角色

| 角色 | 说明 | 版本变化 |
|------|------|----------|
| 🛡️ **神圣守护** | 抵挡 **3 次** 鬼的攻击（v2.2.2 起改为 3 次，之前为 2 次），每次攻击将进攻者随机传送（v2.2.2 前为传送持有者）。 | 机制随版本调整 |
| 🎯 **猎魔人** | 猎魔人阶段由人类转化，持有 **收割者** 武器反杀鬼（v2.3.1 重做收割者技能及复活逻辑）。 | v2.3.1 大改 |
| 👑 **母体**   | 鬼阵营领袖，可攻击猎魔人，可感染普通人类。 | 稳定 |
| ✨ **救赎者** | **v2.3.1 起常驻**，每局最多 2 名，持有神之救赎转化鬼回人类。旧版本（≤2.3.0）中为模式2专属，仅在最后一名人类时出现（或随机）。 | v2.3.1 常驻化 |

---

## 🛡️ 神圣守护系统

- **激活条件**：剩余人类数量 ≤ 配置的触发人数（默认 2 人）。
- **防御次数**：可抵挡 **3 次** 攻击（v2.2.2 前为 2 次，v2.2.2 起统一为 3 次）。
- **传送效果**：每次被攻击，进攻者被随机传送到附近区域（v2.2.2 前为持有者被传送，v2.2.2 起改为攻击者被传送并减速）。
- **猎魔人阶段**：所有剩余人类自动获得/刷新神圣守护（3次抵挡重置），并转化为猎魔人（v2.2.2 起实现）。

> **历史差异**：在 v2.2.0 至 v2.2.1 期间，存在模式1（传统）和模式2（救赎者）的切换，模式2下最后一名人类成为救赎者且不能获得神圣守护。v2.3.1 已废弃模式切换，救赎者改为常驻独立系统。

---

## ✨ 救赎者系统（v2.3.1 常驻化）

> **自 v2.3.1 起，救赎者不再作为模式2的特殊角色，而是每局常驻。**

- 救赎者**常驻每一局游戏**，每局最多 **2名**（由配置 `redeemer.max-count` 控制）。
- 随"神之救赎"道具发放**随机绑定**人类玩家（并非最后一名人类）。
- 救赎者**直接获得独立神圣守护**（不影响原有系统，即拥有自己的抵挡次数）。
- 获得 **1次** 神之救赎使用次数，右键鬼玩家将其转化回人类（**单次使用，用完即消失**）。

**旧版本（≤2.3.0）机制对比：**
- 在 v2.2.2 及之前，救赎者仅存在于「模式2」中，触发条件是仅剩 1 名人类时，该人类变为救赎者（v2.2.2 改为随机发放道具绑定，但仍属模式2）。
- 旧版救赎者拥有 2 次（可配置）使用次数，使用后随机传送，且不拥有神圣守护。
- v2.3.1 将救赎者独立出来，不再依赖模式切换，且自带守护，使用次数固定为 1 次。

---

## ⚔️ 猎魔人阶段（v2.3.1 重做）

游戏剩余 90 秒时自动进入猎魔人阶段。该阶段所有拥有**神圣守护的人类**和**救赎者**变为 **猎魔人**。

### 收割者武器（v2.3.1 全新设计）
- **左键单体攻击**：普通鬼 **2次** 击中击杀，母体鬼 **4次** 击中击杀（纯击中次数判定，不依赖血量）。
- **右键范围技能「收割」**：以自身为中心 **4格半径** AOE，10秒冷却，冲击粒子 + 凋零死亡音效。
- 左键普通攻击冷却 **2秒**。

> **旧版本（≤2.3.0）**：收割者仅左键攻击，基于伤害值（可配置）而非击中次数，无右键技能。

### 鬼复活机制（v2.3.1 新增）
- 被击杀的鬼进入**死亡状态**：**隐身可自由移动**。
- **10秒复活倒计时**（ActionBar 显示）。
- 复活时**随机传送至游戏区域某坐标**（不原地复活）。
- 复活后恢复原身份（普通鬼/母体）继续参与对局。

> **旧版本**：鬼被击杀后直接进入旁观模式，无复活机制（v2.2.2 曾引入复活，但为固定时间原地复活，v2.3.1 改为随机传送）。

### 猎魔人击杀反馈（v2.3.1 新增）
- 猎魔人击杀鬼时，**击杀位置绽放烟花**（红黄BURST效果）。
- 伴随爆炸粒子 + 凋零死亡音效。
- 击杀字幕（中英双语）：` 击杀成功!` / ` Elimination!`

### 母体升级宝石（v2.3.1 新增）
- **触发条件**：参与游戏玩家 **> 5人** 时，猎魔人阶段自动放置。
- **生成位置**：游戏区域内随机坐标，生成**发光绿宝石**（持续附魔粒子标记）。
- **拾取规则**：**仅普通鬼**可拾取。
- **效果**：拾取后**变身为母体鬼**，获得母体血量，参与猎杀猎魔人。
- **提示**：全服公告 + 变身字幕 + 末影龙音效（中英双语）。

### 猎魔人阶段防御规则（v2.3.1 完善）
- **阵营伤害保护**：猎魔人无法伤害人类/猎魔人，**鬼无法伤害鬼（母体无法击杀普通鬼）**，人类无法伤害人类。
- **死亡拦截**：鬼/猎魔人真实死亡事件被取消，不会回重生点。
- **环境伤害免疫**：鬼/猎魔人免疫坠落/岩浆/火焰等环境伤害。
- **正常回血**：不再禁止回血，玩家可正常恢复（v2.2.2 曾禁止回血，v2.3.1 取消该限制）。

### 母体攻击猎魔人
- 无神圣守护猎魔人：母体 **4次** 击中 → 感染（进入旁观）。
- 有神圣守护猎魔人：先 **3次** 破除守护，再 **4次** 感染。

### 母体禁足
- 对局开始前母体随机锁定游戏区域坐标，**20秒** 无法移动（从 v2.1 起一直存在）。

---

## 🧰 道具系统

道具定时发放（默认每60秒），并有道具刷新系统在区域随机生成。以下道具为当前版本全部可用：

| 道具 | 效果 | 限制 | 版本备注 |
|------|------|------|----------|
| 💉 **肾上腺素** | 右键获得10s速度效果II | 仅人类 | 一直存在 |
| 🔥 **狂暴药水** | 右键获得10s速度效果II | 仅鬼 | 一直存在 |
| 🧊 **凝冰球** | 右键投掷命中给对方施加3s缓慢 III | 通用 | 一直存在 |
| 🔮 **控魂术** | 右键使用后全场鬼玩家无法移动6s（18秒冷却） | 仅人类 | 一直存在 |
| 🥩 **臭牛排** | 右键**开始食用**，1秒吃完后获得14s速度III+10s发光（30秒冷却） | 通用 | 自 v2.0.1 起 |
| 🪄 **传送珍珠** | 右键投掷传送（20秒冷却） | 通用 | 自 v2.0.1 起 |
| 👁️ **灵魂探测器** | 右键使用后所有玩家发光25秒（35秒冷却） | 仅鬼 | 一直存在 |
| ❤️ **第二次机会** | 被感染时被动触发，免疫感染+随机传送（180秒冷却） | 仅人类 | 一直存在 |
| 🧪 **漂浮药水** | 右键获得漂浮效果 4.5秒（飞起约3.5个方块高度） | 通用 | 自 v2.3.0 起 |
| 🗡️ **冲刺矛** | 左键向前冲刺(约5个方块距离)（仅 ≥1.21.4 版本生成） | 通用 | 自 v2.3.0 起 |
| 💊 **神之救赎** | 右键鬼玩家使其转化回人类 | 救赎者 | v2.3.1 常驻，使用次数1 |
| 🗡️ **收割者** | 猎魔人专属武器（左键攻击/右键技能"收割"） | 猎魔人 | v2.3.1 重做 |

### 背包管理系统（v2.3.1新增）
- 对局期间最多 **9个** 道具（由 `inventory.max-item-types` 控制）。
- 道具不可放入背包（强制移回物品栏），物品栏满则提示。
- 道具**不堆叠**：**狂暴药水**、**肾上腺素**、**控魂术**、**灵魂探测器**（除通用道具外，同类道具只能持有一个）。
- **收割者**/**神之救赎**强制放第一格，满格时替换。

> **旧版本**：无背包限制，道具可随意堆放和移动。

---

## 🏆 经济与奖励系统

### 入场与奖池
- 玩家加入游戏需支付 **入场费**（默认100金币），汇入人类奖池。
- 服务器可设置**额外奖金**（默认5000金币）。

### 胜利奖金分配
| 胜利方 | 分配规则 |
|--------|----------|
| 👤 人类胜利 | 人类获奖池 **70%**，鬼获 **30%** |
| 👻 鬼胜利   | 鬼获奖池 **100%** |

### 个人奖金计算
| 阵营 | 分配方式 |
|------|----------|
| 人类 | 100% 按存活时间比例分配 |
| 鬼   | 70% 按鬼存活时间 + 30% 按感染人数比例 |

### 猎魔人奖励
- 猎魔人每击杀鬼玩家，获人类奖池 **30%** 奖金。
- 母体击杀猎魔人获 **50%** 额外奖励。

### 额外奖励（v2.3.1新增，游戏结束结算，服务器额外支付，不占用奖池）
| 行为 | 奖励 |
|------|------|
| 救赎一名鬼玩家 | **100** 金币 |
| 猎魔人击杀普通鬼 | **50** 金币 |
| 猎魔人击杀母体鬼 | **100** 金币 |

### 奖金排行榜
游戏结束后显示奖金排行榜（金银铜奖牌 🥇🥈🥉），自 v2.3.0 起加入。

---

## 🌐 版本兼容性

- **Minecraft 1.20+ 全版本（截止2026.8.23）**
- 粒子名称自动兼容（`ParticleCompat` 运行时解析新旧枚举名）。
- 音效名称自动兼容（`SoundCompat` 运行时解析）。
- 冲刺矛（`GOLDEN_SPEAR`）仅 ≥1.21.4 生成，低版本自动禁用。
- Vault 经济接口多版本回退。

---

## ✨ 特色系统

### 🌑 黑暗效果与疾跑并存
- 使用 Minecraft 原生 **DARKNESS** 效果（1.19+）或 **BLINDNESS**（旧版本）。
- 通过属性修改器增加 **30% 移动速度**，抵消黑暗效果对疾跑的影响。
- 每 10 ticks 检查玩家疾跑状态，自动恢复被阻止的疾跑。
- 管理员和创造模式玩家也会受到黑暗效果影响。
- 管理员可通过 `/gostadmin dark` 控制开关（自 v2.0.2 起）。

### 👻 鬼玩家粒子效果
- 鬼玩家身上持续显示环绕粒子效果。
- **母体鬼**：红色粒子，**普通鬼**：绿色粒子。
- 支持 21 种粒子类型，RGB 颜色自定义。
- 可配置准备阶段是否显示。
- 管理员可通过 `/gostadmin particle` 管理（自 v2.1.2 起）。

### 💬 语言系统全面优化（Cover version 核心改进）
- 90+ 条默认中文消息，智能消息回退机制。
- 错误隔离保护，不影响核心游戏流程。
- 插件启动保护，LanguageManager 初始化失败仍能运行。
- 支持英文语言包（`language.default: en_US`，自 v2.2.3 起）。

### ❤️ 心跳声系统
- 游戏过程中人类方循环播放监守者出现时的心跳声。
- 管理员可通过 `/gostadmin heartbeat` 控制开关（自 v2.1.0 起）。

### 选区粒子框
- 设置选区后，选区边界持续显示**火焰粒子框 30秒**，直观看到选区大小（自 v2.3.0 起）。

---

## 📋 命令

### 玩家命令
| 命令 | 说明 | 权限 |
|------|------|------|
| `/gost join` | 加入游戏队列 | gost.player |
| `/gost leave` | 离开游戏/队列 | gost.player |
| `/gost info` | 查看游戏信息 | gost.player |
| `/gost help` | 帮助 | gost.player |

### 管理员命令（/gostadmin）
| 命令 | 说明 | 权限 | 版本 |
|------|------|------|------|
| `/gostadmin start <区域>` | 指定区域开始游戏 | gost.admin | 一直存在 |
| `/gostadmin stop` | 强制结束游戏 | gost.admin | 一直存在 |
| `/gostadmin testmode` | 单人测试模式（不会因人数不足结束） | gost.admin | v2.3.0+ |
| `/gostadmin giveitem <道具>` | 直接获得指定道具 | gost.admin | v2.3.0+ |
| `/gostadmin divine <status\|clear>` | 神圣守护管理（原/divineguardian） | gost.admin | 整合自 v2.1.1 |
| `/gostadmin particle <status\|enable\|disable>` | 鬼粒子管理（原/ghostparticle） | gost.admin | 整合自 v2.1.2 |
| `/gostadmin tool` | 获取选区工具 | gost.admin | v2.3.0+ |
| `/gostadmin pos1/pos2` | 设置选区点 | gost.admin | v2.3.0+ |
| `/gostadmin save/list/load/delete/info` | 区域管理 | gost.admin | 一直存在 |
| `/gostadmin reload` | 重载配置 | gost.admin | 一直存在 |
| `/gostadmin dark <on\|off\|status>` | 黑暗效果管理 | gost.admin | v2.0.2+ |
| `/gostadmin heartbeat <on\|off\|status>` | 心跳声管理 | gost.admin | v2.1.0+ |
| `/gostadmin economy <set\|status>` | 经济管理 | gost.admin | 一直存在 |

---

## 🔐 权限

| 权限节点 | 说明 | 默认 |
|----------|------|------|
| `gost.use` | 基础命令权限 | **所有玩家** |
| `gost.player` | 玩家游戏权限 | **所有玩家** |
| `gost.admin` | 管理员权限 | OP |

---

## 📈 版本历史与机制演变

以下是各主要版本的关键变化，帮助理解机制演进：

### v2.3.1（当前） — 救赎者常驻 & 猎魔人重做 & 奖励扩充
- **救赎者**：从模式2中独立，变为每局常驻（最多2名），自带神圣守护，使用次数固定1次。
- **猎魔人阶段**：
  - 收割者改为左键击中次数击杀（普通鬼2次，母体4次），新增右键范围技能「收割」（AOE）。
  - 鬼被击杀后进入死亡状态（隐身移动），10秒后随机传送复活（原为固定时间原地复活）。
  - 新增击杀烟花特效、母体升级宝石（普通鬼拾取变母体）。
  - 取消猎魔人阶段禁止回血，允许正常恢复。
  - 完善阵营伤害保护与环境免疫。
- **背包管理**：限制最多9种道具，强制不堆叠，收割者/神之救赎占第一格。
- **额外奖励**：救赎鬼、猎魔人击杀普通鬼/母体均可获得额外金币（服务器额外支付）。
- 配置版本升级至 28。

### v2.3.0 — 新道具 & 管理员工具 & 排行榜
- 新增道具：冲刺矛（≥1.21.4）、漂浮药水。
- 新增管理员命令：`/gost start`（强制开局）、`/gostadmin testmode`、`/gostadmin giveitem`。
- 新增选区工具及火焰粒子框显示。
- 新增游戏结束奖金排行榜（🥇🥈🥉）。
- 配置版本 27。

### v2.2.3 — 音效系统 & 英文支持
- 全面引入音效（阶段切换、道具发放/使用、阵容切换等）。
- 道具居中字幕提示。
- 英文语言支持（`language.default: en_US`）。
- 配置自动迁移，修复 1.20.x API 兼容性。
- 配置版本 26。

### v2.2.2 — 猎魔人登场（大改）
- 引入猎魔人阶段（游戏剩余90秒触发）。
- 鬼复活机制（被猎魔人击杀后进入旁观，固定时间后原地复活，可配置）。
- 智能血量调整：普通鬼2❤，母体3❤，猎魔人2❤（均基于伤害值）。
- 猎魔人阶段禁止自然回血。
- 收割者基于伤害值击杀（可配置每次伤害）。
- 神圣守护改为抵挡3次（此前为2次），且攻击者被传送（此前为持有者传送）。
- 猎魔人阶段所有人类自动获得/刷新神圣守护。

### v2.2.1_Cover — 语言系统优化 & 修复
- 90+条默认中文消息，智能回退，错误隔离。
- 凝冰球修复（对所有玩家生效），默认血量恢复为10颗心。
- 黑暗效果与疾跑并存系统完善。
- 配置版本 20。

### v2.2.0 — 神圣守护模式2（救赎者）
- 引入模式2：最后一名人类（或随机）成为救赎者，持有神之救赎（2次使用），可转化鬼回人类。
- 模式切换命令 `/divineguardian setmode`。
- 队列系统优化。

### v2.1.3 — 首个稳定版本
- 修复管理员免疫问题，创造模式自动切换生存。

### v2.1.2 — 鬼粒子效果
- 鬼玩家环绕粒子（母体红，普通绿），可自定义类型与颜色。
- 管理命令 `/ghostparticle`。

### v2.1.1 — 神圣守护系统
- 首次引入神圣守护（抵挡2次，传送持有者）。
- 管理命令 `/divineguardian`。

### v2.1.0 — 经济优化 & 心跳声
- 奖金分配优化，鬼转人类功能（默认关闭）。
- 心跳声系统。

### v2.0.2 — 黑暗效果
- 黑暗效果系统（`/gostadmin dark`）。

### v2.0.1 — 传送珍珠 & 臭牛排
- 新增传送珍珠、臭牛排。
- 智能倒计时。

### v2.0.0 — 架构重构
- 独立区域系统，道具系统优化。

### v1.0.0 — 已作废
- 初始版本，因机制与作者意图不符，不再使用。

---

## 🚀 开始游戏

使用命令 `/gost join` 加入队列，体验生死追逐的乐趣！  
- 感染方式：鬼玩家左键/右键点击人类。  
- 道具发放时全体高亮5秒。  
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
- [Redeemer System (permanent since v2.3.1)](#redeemer-system-permanent-since-v231)
- [Demon Hunter Phase (reworked in v2.3.1)](#demon-hunter-phase-reworked-in-v231)
- [Item System](#item-system)
- [Economy & Reward System](#economy--reward-system)
- [Version Compatibility](#version-compatibility)
- [Feature Systems](#feature-systems)
- [Commands](#commands)
- [Permissions](#permissions)
- [Version History & Mechanism Evolution](#version-history--mechanism-evolution)
- [Getting Started](#getting-started)

---

## Downloads & Links

- [Modrinth Download](https://modrinth.com/plugin/gost)
- [Author’s Homepage](https://726113394-cloud.github.io/PersonalPage/)
- [Preview Video](https://www.bilibili.com/video/BV1aPQSBsE74/)
- [2.3.0 Update Introduction](https://www.bilibili.com/video/BV1eHKj69E4H/) (v2.3.1 is a subsequent iteration; key changes are described below)

---

## Core Gameplay

### Game Flow
1. **Preparation Phase** – Players join the queue; game starts when full.
2. **Infection Phase** – Ghosts chase and infect Humans; Humans pick up items to defend themselves.
3. **Divine Guardian Phase** – Automatically activated when remaining Humans ≤ trigger count.
4. **Demon Hunter Phase** – Enters when 90 seconds remain (introduced in v2.2.2); remaining Humans become Demon Hunters to fight back.

### Two Factions

| Faction | Description |
|---------|-------------|
| 👤 **Human** | Initial identity; becomes Ghost after infection. |
| 👻 **Ghost** | Infects Humans to expand the Ghost faction; the Mother Ghost is the faction leader. |

### Special Roles

| Role | Description | Version Changes |
|------|-------------|-----------------|
| 🛡️ **Divine Guardian** | Blocks **3** Ghost attacks (changed to 3 in v2.2.2; previously 2). Each attack teleports the attacker (before v2.2.2, the holder was teleported). | Mechanics adjusted across versions |
| 🎯 **Demon Hunter** | Transformed from Humans during Demon Hunter phase; wields **Reaper** to kill Ghosts (reworked in v2.3.1 with new skills and respawn logic). | Major rework in v2.3.1 |
| 👑 **Mother Ghost** | Ghost faction leader; can attack Demon Hunters and infect normal Humans. | Stable |
| ✨ **Redeemer** | **Permanent since v2.3.1**, max 2 per game, holds Holy Redemption to convert Ghosts back to Humans. In older versions (≤2.3.0), it was exclusive to Mode 2, appearing only when one Human remained (or randomly). | Made permanent in v2.3.1 |

---

## Divine Guardian System

- **Trigger condition**: Remaining Humans ≤ configured threshold (default 2).
- **Defense charges**: Blocks **3** attacks (2 before v2.2.2; unified to 3 from v2.2.2 onward).
- **Teleport effect**: Each attack teleports the attacker to a random nearby location (before v2.2.2, the holder was teleported; from v2.2.2, the attacker is teleported and slowed).
- **Demon Hunter phase**: All remaining Humans automatically gain/refresh Divine Guardian (3 charges reset) and transform into Demon Hunters (implemented since v2.2.2).

> **Historical difference**: Between v2.2.0 and v2.2.1, there were Mode 1 (classic) and Mode 2 (Redeemer) toggles. In Mode 2, the last Human became a Redeemer and could not obtain Divine Guardian. v2.3.1 deprecated mode switching and made Redeemers a permanent independent system.

---

## Redeemer System (permanent since v2.3.1)

> **Starting from v2.3.1, the Redeemer is no longer a Mode‑2 exclusive role but is present in every game.**

- Redeemers **appear in every game**, up to **2 per game** (controlled by `redeemer.max-count`).
- They are **randomly bound** to Human players via the "Holy Redemption" item (not necessarily the last Human).
- Redeemers **directly receive an independent Divine Guardian** (separate from the main system, i.e., they have their own charges).
- They get **1 use** of Holy Redemption; right‑click a Ghost player to convert them back to Human (**single use, consumed after use**).

**Comparison with older versions (≤2.3.0):**
- In v2.2.2 and earlier, Redeemers existed only in "Mode 2", triggered when only 1 Human remained (or randomly assigned via item in v2.2.2, but still part of Mode 2).
- Old Redeemers had 2 (configurable) uses, teleported randomly after use, and did not have Divine Guardian.
- v2.3.1 decouples Redeemers from mode switching, gives them built‑in Guardian, and fixes usage to 1.

---

## Demon Hunter Phase (reworked in v2.3.1)

The phase automatically starts when 90 seconds remain. All Humans with **Divine Guardian** and **Redeemers** become **Demon Hunters**.

### Reaper Weapon (brand new design in v2.3.1)
- **Left‑click single‑target attack**: kills a normal Ghost in **2 hits**, Mother Ghost in **4 hits** (pure hit‑count, not damage‑based).
- **Right‑click area skill "Harvest"**: 4‑block radius AOE centered on self, 10‑second cooldown, with blast particles and Wither death sound.
- Left‑click normal attack cooldown: **2 seconds**.

> **Older versions (≤2.3.0)**: Reaper only had left‑click attack, based on configurable damage values (not hit count), and no right‑click skill.

### Ghost Respawn Mechanic (new in v2.3.1)
- Killed Ghosts enter a **death state**: **invisible and able to move freely**.
- **10‑second respawn countdown** (displayed in ActionBar).
- Upon respawn, they are **teleported to a random coordinate in the game area** (not at the death location).
- After respawn, they retain their original identity (normal Ghost or Mother) and continue playing.

> **Older versions**: Ghosts were sent directly to spectator mode without respawn (v2.2.2 introduced respawn but with fixed time and at the same location; v2.3.1 changed to random teleport).

### Demon Hunter Kill Feedback (new in v2.3.1)
- When a Demon Hunter kills a Ghost, **fireworks burst at the kill location** (red‑yellow BURST effect).
- Accompanied by explosion particles and Wither death sound.
- Kill subtitle (bilingual): ` 击杀成功!` / ` Elimination!`

### Mother Upgrade Gem (new in v2.3.1)
- **Trigger condition**: When **> 5 players** are in the game, the gem automatically spawns during the Demon Hunter phase.
- **Spawn location**: Random coordinate in the game area, appearing as a **glowing emerald** (with constant enchantment particle effect).
- **Pickup rule**: **Only normal Ghosts** can pick it up.
- **Effect**: The pickup player **transforms into a Mother Ghost**, gaining Mother health and the ability to hunt Demon Hunters.
- **Notifications**: Global broadcast + transformation subtitle + Ender Dragon sound (bilingual).

### Demon Hunter Phase Defense Rules (improved in v2.3.1)
- **Faction damage protection**: Demon Hunters cannot harm Humans/Demon Hunters; **Ghosts cannot harm Ghosts (Mother cannot kill normal Ghosts)**; Humans cannot harm Humans.
- **Death interception**: Real death events for Ghosts/Demon Hunters are cancelled; they do not respawn at bed/world spawn.
- **Environmental damage immunity**: Ghosts/Demon Hunters are immune to fall, lava, fire, etc.
- **Normal healing**: Healing is no longer disabled; players can regenerate normally (v2.2.2 had disabled healing; v2.3.1 removes that restriction).

### Mother Attacking Demon Hunters
- Demon Hunter without Divine Guardian: Mother needs **4 hits** → infection (sent to spectator).
- Demon Hunter with Divine Guardian: first **3 hits** break the Guardian, then **4 hits** to infect.

### Mother Immobilization
- At the start of the game, the Mother is locked to a random coordinate in the game area and **cannot move for 20 seconds** (this has existed since v2.1).

---

## Item System

Items are distributed periodically (default every 60 seconds) and also spawn randomly in the area. The following items are available in the current version:

| Item | Effect | Restriction | Version Notes |
|------|--------|-------------|---------------|
| 💉 **Adrenaline** | Right‑click for Speed II (10s) | Human only | Always present |
| 🔥 **Frenzy Potion** | Right‑click for Speed II (10s) | Ghost only | Always present |
| 🧊 **Ice Ball** | Right‑click throw; applies Slowness III (3s) on hit | Universal | Always present |
| 🔮 **Soul Control** | Right‑click freezes all Ghosts for 6s (18s cooldown) | Human only | Always present |
| 🥩 **Stinky Steak** | Right‑click to eat (1s); grants Speed III (14s) + Glowing (10s) (30s cooldown) | Universal | Since v2.0.1 |
| 🪄 **Teleport Pearl** | Right‑click throw to teleport (20s cooldown) | Universal | Since v2.0.1 |
| 👁️ **Soul Detector** | Right‑click reveals all players for 25s (35s cooldown) | Ghost only | Always present |
| ❤️ **Second Chance** | Passive trigger on infection; blocks infection + random teleport (180s cooldown) | Human only | Always present |
| 🧪 **Levitation Potion** | Right‑click for Levitation 4.5s (rises ~3.5 blocks) | Universal | Since v2.3.0 |
| 🗡️ **Spear Rush** | Left‑click to dash forward (~5 blocks) (only on ≥1.21.4) | Universal | Since v2.3.0 |
| 💊 **Holy Redemption** | Right‑click a Ghost to convert them back to Human | Redeemer | Permanent since v2.3.1, 1 use |
| 🗡️ **Reaper** | Demon Hunter weapon (left‑click attack / right‑click "Harvest" skill) | Demon Hunter | Reworked in v2.3.1 |

### Inventory Management System (new in v2.3.1)
- Max **9 item types** during a game (controlled by `inventory.max-item-types`).
- Items cannot be moved to the backpack (forced back to hotbar); warning if hotbar is full.
- Items **do not stack**: **Frenzy Potion**, **Adrenaline**, **Soul Control**, **Soul Detector** (except universal items; you can only hold one of each type).
- **Reaper** / **Holy Redemption** are forced to the first hotbar slot; if full, they replace existing items.

> **Older versions**: No inventory restrictions; items could be moved and stacked freely.

---

## Economy & Reward System

### Entry Fee & Prize Pool
- Players pay an **entry fee** (default 100 coins), which goes into the Human Prize Pool.
- Server can add a **bonus** (default 5000 coins).

### Victory Bonus Distribution
| Winner | Distribution |
|--------|--------------|
| 👤 Human Victory | Humans get **70%** of the pool; Ghosts get **30%** |
| 👻 Ghost Victory | Ghosts get **100%** of the pool |

### Individual Bonus Calculation
| Faction | Calculation |
|---------|-------------|
| Human | 100% proportional to **survival time** |
| Ghost | 70% proportional to **ghost survival time** + 30% proportional to **number of infections** |

### Demon Hunter Bonus
- Each Ghost kill by a Demon Hunter grants **30% of the Human Prize Pool**.
- Mother killing a Demon Hunter gets an extra **50%** bonus.

### Extra Rewards (new in v2.3.1, paid by server at game end, not taken from the pool)
| Action | Reward |
|--------|--------|
| Redeem a Ghost | **100** coins |
| Demon Hunter kills a normal Ghost | **50** coins |
| Demon Hunter kills a Mother Ghost | **100** coins |

### Bonus Leaderboard
At game end, a bonus leaderboard is shown (gold/silver/bronze medals 🥇🥈🥉), added since v2.3.0.

---

## Version Compatibility

- **Minecraft 1.20+ all versions (as of 2026‑08‑23)**
- Particle names auto‑compatible (`ParticleCompat` resolves old/new enum names at runtime).
- Sound names auto‑compatible (`SoundCompat` resolves at runtime).
- Spear Rush (`GOLDEN_SPEAR`) only spawns on ≥1.21.4; automatically disabled on older versions.
- Vault economy interface with fallback for multiple versions.

---

## Feature Systems

### 🌑 Dark Effect + Sprint Coexistence
- Uses native **DARKNESS** (1.19+) or **BLINDNESS** (older versions).
- Adds **+30% movement speed** via attribute modifier to counteract the dark effect's sprint inhibition.
- Checks sprint status every 10 ticks and restores sprint if blocked.
- Admins and creative mode players also receive the dark effect.
- Toggle with `/gostadmin dark` (since v2.0.2).

### 👻 Ghost Particle Effects
- Continuous orbiting particles around Ghosts.
- **Mother Ghost**: red particles; **normal Ghost**: green particles.
- Supports 21 particle types, RGB color customization.
- Configurable whether to show during preparation phase.
- Managed with `/gostadmin particle` (since v2.1.2).

### 💬 Comprehensive Language System (Cover version core improvement)
- 90+ default Chinese messages with smart fallback.
- Error isolation protection; does not affect core gameplay.
- Plugin startup protection; LanguageManager failure does not crash the plugin.
- English language support (`language.default: en_US`, since v2.2.3).

### ❤️ Heartbeat Sound System
- Humans hear the Warden's heartbeat sound during the game.
- Toggle with `/gostadmin heartbeat` (since v2.1.0).

### Selection Particle Box
- After setting a selection, the boundaries display a **flame particle box for 30 seconds** to visualize the area (since v2.3.0).

---

## Commands

### Player Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/gost join` | Join game queue | gost.player |
| `/gost leave` | Leave game/queue | gost.player |
| `/gost info` | View game info | gost.player |
| `/gost help` | Help | gost.player |

### Admin Commands (/gostadmin)
| Command | Description | Permission | Version |
|---------|-------------|------------|---------|
| `/gostadmin start <area>` | Start game in specified area | gost.admin | Always |
| `/gostadmin stop` | Force stop game | gost.admin | Always |
| `/gostadmin testmode` | Solo test mode (won't end due to insufficient players) | gost.admin | v2.3.0+ |
| `/gostadmin giveitem <item>` | Get a specific item directly | gost.admin | v2.3.0+ |
| `/gostadmin divine <status\|clear>` | Divine Guardian management (formerly /divineguardian) | gost.admin | Integrated since v2.1.1 |
| `/gostadmin particle <status\|enable\|disable>` | Ghost particle management (formerly /ghostparticle) | gost.admin | Integrated since v2.1.2 |
| `/gostadmin tool` | Get selection tool | gost.admin | v2.3.0+ |
| `/gostadmin pos1/pos2` | Set selection points | gost.admin | v2.3.0+ |
| `/gostadmin save/list/load/delete/info` | Area management | gost.admin | Always |
| `/gostadmin reload` | Reload config | gost.admin | Always |
| `/gostadmin dark <on\|off\|status>` | Dark effect control | gost.admin | v2.0.2+ |
| `/gostadmin heartbeat <on\|off\|status>` | Heartbeat sound control | gost.admin | v2.1.0+ |
| `/gostadmin economy <set\|status>` | Economy management | gost.admin | Always |

---

## Permissions

| Permission Node | Description | Default |
|-----------------|-------------|---------|
| `gost.use` | Basic command permission | **All players** |
| `gost.player` | Player game permission | **All players** |
| `gost.admin` | Admin permission | OP |

---

## Version History & Mechanism Evolution

Key changes in major versions to help understand the evolution:

### v2.3.1 (Current) – Redeemer Permanent & Demon Hunter Rework & Reward Expansion
- **Redeemer**: Independent from Mode 2, permanent (max 2 per game), with built‑in Divine Guardian, fixed 1 use.
- **Demon Hunter phase**:
  - Reaper changed to hit‑count kills (normal Ghost 2 hits, Mother 4 hits), added right‑click AOE skill "Harvest".
  - Killed Ghosts enter death state (invisible moving), respawn after 10s with random teleport (was fixed‑time same‑location respawn).
  - Added kill firework effects, Mother Upgrade Gem (normal Ghost pickup becomes Mother).
  - Removed healing prohibition during Demon Hunter phase; healing allowed.
  - Improved faction protection and environmental immunity.
- **Inventory management**: Max 9 item types, forced no‑stack, Reaper/Holy Redemption occupy first slot.
- **Extra rewards**: Redeeming Ghosts, Demon Hunter kills normal/Mother Ghosts all grant extra coins (server‑paid).
- Config version upgraded to 28.

### v2.3.0 – New Items & Admin Tools & Leaderboard
- New items: Spear Rush (≥1.21.4), Levitation Potion.
- New admin commands: `/gost start` (force start), `/gostadmin testmode`, `/gostadmin giveitem`.
- Selection tool and flame particle box display.
- Added end‑game bonus leaderboard (🥇🥈🥉).
- Config version 27.

### v2.2.3 – Sound System & English Support
- Full sound system (phase, item distribution/usage, faction switch sounds).
- Centered subtitle hints for items.
- English language support (`language.default: en_US`).
- Auto‑config migration, fixed 1.20.x API compatibility.
- Config version 26.

### v2.2.2 – Demon Hunter Debut (Major Overhaul)
- Introduced Demon Hunter phase (triggers at 90s remaining).
- Ghost respawn mechanic (spectator after killed by Demon Hunter, fixed‑time respawn at same location, configurable).
- Smart health adjustment: normal Ghost 2❤, Mother 3❤, Demon Hunter 2❤ (all damage‑based).
- Healing disabled during Demon Hunter phase.
- Reaper based on damage values (configurable per hit).
- Divine Guardian changed to 3 blocks (was 2), and attacker is teleported (was holder).
- All Humans auto‑gain/refresh Divine Guardian upon entering Demon Hunter phase.

### v2.2.1_Cover – Language System Optimization & Fixes
- 90+ Chinese messages, smart fallback, error isolation.
- Ice Ball fix (affects all players), default health restored to 10 hearts.
- Dark effect + sprint coexistence improved.
- Config version 20.

### v2.2.0 – Divine Guardian Mode 2 (Redeemer)
- Introduced Mode 2: last Human (or random) becomes Redeemer with Holy Redemption (2 uses), can convert Ghosts back to Humans.
- Mode switch command `/divineguardian setmode`.
- Queue system optimization.

### v2.1.3 – First Stable Version
- Fixed admin immunity; creative mode auto‑switch to survival.

### v2.1.2 – Ghost Particle Effects
- Orbiting particles around Ghosts (Mother red, normal green), customizable type/color.
- Management command `/ghostparticle`.

### v2.1.1 – Divine Guardian System
- First introduction of Divine Guardian (blocks 2 attacks, teleports holder).
- Management command `/divineguardian`.

### v2.1.0 – Economy Optimization & Heartbeat Sound
- Bonus distribution optimization, Ghost‑to‑Human feature (disabled by default).
- Heartbeat sound system.

### v2.0.2 – Dark Effect
- Dark effect system (`/gostadmin dark`).

### v2.0.1 – Teleport Pearl & Stinky Steak
- Added Teleport Pearl and Stinky Steak.
- Smart countdown.

### v2.0.0 – Architecture Refactor
- Independent area system, item system optimization.

### v1.0.0 – Deprecated
- Initial version, deprecated due to mechanics not matching author's intent.

---

## Getting Started

Use `/gost join` to join the queue and experience the thrill of life‑and‑death chase!  
- Infection method: Ghosts left‑/right‑click Humans.  
- All players are highlighted for 5 seconds when items are distributed.  
- The Demon Hunter phase brings a brand‑new counter‑attack experience.  

In the world of Gost, survival requires not only speed but also wisdom and strategy.