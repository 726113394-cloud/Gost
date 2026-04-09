# 🎮 Gost - 极限生存对抗游戏

**Gost v2.2.1_Cover version** 是一款融合了 **猫抓老鼠、鬼抓人、躲猫猫、CF生化模式** 等多种玩法的 Minecraft 服务器插件。在紧张刺激的对抗中，体验人类与鬼之间的生死追逐，运用策略与道具，成为最后的幸存者！

## 🆕 v2.2.1_Cover version 更新亮点

### 🎯 语言系统全面优化（Cover version核心改进）
- **大幅扩充默认消息库**：在 `LanguageManager.java` 中添加了 **90+ 条默认中文消息**
- **智能消息回退机制**：不再返回 "§cMissing message: xxx"，而是根据消息键前缀返回友好的中文提示：
  - `game.` → "§e游戏提示"
  - `role.` → "§c角色状态"
  - `item.` → "§a道具效果"
  - `error.` → "§c操作失败"
  - 更多智能分类提示
- **错误隔离保护**：
  - 所有消息获取和处理都加入了异常捕获
  - 即使部分功能异常，也不会影响核心游戏流程
  - 玩家始终能看到可理解的中文提示
- **插件启动保护**：如果 LanguageManager 初始化失败，插件仍能正常运行
- **消息获取保护**：任何消息获取失败都会返回友好的替代消息
- **玩家体验优先**：所有玩家可见的提示都是完整的中文句子

### ⚡ 神圣守护模式2 - 救赎者系统
- **救赎者**：当人类数量减少到1时，最后一名人类成为救赎者
- **神之救赎道具**：救赎者获得专属道具，可转化鬼玩家回人类
- **随机传送**：使用道具后救赎者会被随机传送到安全位置
- **次数限制**：道具可使用2次（可配置），使用完后回归普通人类
- **视觉效果**：救赎者全程高亮显示，拥有速度效果

### 🎮 游戏模式切换
- **模式1**：传统神圣守护（感染免疫+随机传送）
- **模式2**：全新救赎者模式（转化鬼玩家）
- **命令切换**：`/divineguardian setmode <1|2>`
- **动态生效**：游戏进行中切换模式会立即生效

### 🔧 队列系统全面优化
- **智能队列清理**：当队列人数少于最小玩家数时，自动清空队列并退还金币
- **退出队列提示**：玩家退出队列时会收到明确的金币退还提示
- **加入队列引导**：玩家加入队列时收到使用`/gost leave`退出的提示
- **Boss栏同步修复**：修复队列玩家离开后Boss栏不消失的问题
- **队列状态透明**：实时显示队列状态，避免玩家困惑

### 🗑️ 代码精简与性能提升
- **移除冗余系统**：删除NPC、独立货币发放、观战系统，节省插件空间
- **配置清理**：清理config.yml中相关配置项，简化配置管理
- **内存优化**：移除未使用的代码和依赖，提升插件性能
- **错误修复**：修复队列状态同步问题，提升稳定性

### 🌑 黑暗效果与疾跑并存系统（重大改进）
- **原生DARKNESS效果**：使用Minecraft原生的DARKNESS效果（1.19+）或BLINDNESS效果（旧版本）
- **疾跑功能修复**：通过属性修改器增加移动速度，抵消DARKNESS效果对疾跑的影响
- **智能检查机制**：定时检查玩家疾跑状态，自动恢复被阻止的疾跑
- **属性修改器系统**：为每个有黑暗效果的玩家添加30%移动速度加成
- **版本兼容性**：自动检测并适配不同Minecraft版本的效果类型
- **管理员友好**：管理员和创造模式玩家也会受到黑暗效果影响，但有明确提示

### 🛠️ 游戏平衡性调整
- **✅ 凝冰球修复**：修复了凝冰球只对鬼玩家生效的问题（现在对所有玩家生效）
- **✅ 默认血量调整**：默认血量已调整为10颗心（20点生命值）
- **✅ 道具系统优化**：移除了道具叠加开关相关设计
- **✅ 配置版本更新**：更新了配置版本号（v19 → v20）

## 📋 目录

- [✨ 核心特色](#-核心特色)
  - [🏃‍♂️ 紧张刺激的追逐体验](#-紧张刺激的追逐体验)
  - [🏆 经济与奖励系统](#-经济与奖励系统)
  - [🏃‍♀️ 智能匹配与队列系统](#-智能匹配与队列系统)
  - [🧰 多层次道具系统](#-多层次道具系统)
  - [👥 沉浸式视觉系统](#-沉浸式视觉系统)
  - [🗺️ 灵活区域管理系统](#️-灵活区域管理系统)
- [🚀 快速开始](#-快速开始)
  - [📦 安装要求](#-安装要求)
- [🎮 游戏指南](#-游戏指南)
  - [📝 玩家基础命令](#-玩家基础命令)
  - [🧰 道具使用指南](#-道具使用指南-1)
  - [⚙️ 管理员命令大全](#️-管理员命令大全)
- [🎯 完整游戏流程](#-完整游戏流程)
  - [1. 🗺️ 区域设置阶段](#1-️-区域设置阶段)
  - [2. 👥 玩家准备阶段](#2-玩家准备阶段)
  - [3. 🚀 游戏进行阶段](#3-游戏进行阶段)
  - [4. 🏆 游戏结束阶段](#4-游戏结束阶段)
- [⚙️ 配置文件详解](#️-配置文件详解)
  - [📋 主要配置项示例](#-主要配置项示例)
- [🔐 权限节点](#-权限节点)
  - [玩家权限（默认所有玩家拥有）](#玩家权限默认所有玩家拥有)
  - [管理员权限](#管理员权限)
- [⚠️ 重要注意事项](#️-重要注意事项)
  - [必需条件](#必需条件)
  - [可选增强](#可选增强)
  - [游戏规则](#游戏规则)
- [🔧 故障排除指南](#-故障排除指南)
  - [常见问题与解决方案](#常见问题与解决方案)
  - [📊 日志诊断](#-日志诊断)
- [📈 版本更新记录](#-版本更新记录)
  - [v2.2.1（当前版本）](#v221当前版本)
  - [v2.1.3](#v213)
  - [v2.0.2](#v202)
  - [v2.0.0](#v200)
  - [v1.0.0](#v100)
- [🤝 技术支持与反馈](#-技术支持与反馈)
- [🎉 开始你的生存对抗之旅！](#-开始你的生存对抗之旅)
- [🇬🇧 English Version](#-english-version)

## ✨ 核心特色

### 🏃‍♂️ 紧张刺激的追逐体验
- **阵营对抗**：人类 vs 鬼，生死追逐，极限逃生
- **时间压力**：限定时间内决定胜负，紧张刺激
- **感染转化**：被鬼触碰后转化为鬼，阵营动态变化
- **策略生存**：利用地形、道具和团队协作求生存

### 🏆 经济与奖励系统
- **入场机制**：100金币入场费（可配置），确保玩家投入
- **奖池系统**：所有入场费 + 5000金币服务器奖金（可配置）
- **智能分配**：
  - **人类胜利时**：人类阵容获得奖池的70%，鬼阵容获得奖池的30%
  - **鬼胜利时**：鬼阵容获得奖池的100%
  - **人类奖金分配**：100%按存活时间比例分配（存活时间越长，奖金越多）
  - **鬼奖金分配**：70%按鬼存活时间，30%按感染人数比例分配
- **详细反馈**：每个玩家收到详细的奖金分配说明，包括存活时间、感染人数、分配比例等
- **风险与回报**：高风险高回报，鼓励积极参与和策略性游戏

### 🏃‍♀️ 智能匹配与队列系统
- **智能队列**：自动匹配玩家，支持自定义等待时间
- **满员加速**：队列满员后自动开始倒计时（可配置30秒）
- **醒目提示**：最后10秒显示居中大文本倒计时
- **人数平衡**：严格的最小/最大玩家数限制（2-16人）

### 🧰 多层次道具系统
#### 🎁 初始道具（游戏开始时自动发放）
- **🧪 肾上腺素**（人类专属）：速度提升效果，持续10秒
- **🔥 狂暴药水**（鬼专属）：速度提升效果，持续10秒  
- **👻 控魂术**（随机3名人类）：冻结所有鬼6秒，使用末影之眼
- **❄️ 凝冰球**（随机6名玩家）：减速鬼4秒，使用雪球投掷

#### 🔄 随机刷新道具（每分钟自动刷新）
- **🍖 臭牛排**（权重15）：无视饱食度直接食用，获得速度II效果14秒和发光效果10秒，冷却30秒
- **💎 传送珍珠**（权重20）：双方可用，右键投掷传送，冷却时间20秒
- **🧭 灵魂探测器**（鬼专属，权重12）：使用后所有玩家发光25秒，冷却35秒
- **🛡️ 一次机会**（人类专属，权重10）：被动触发抵挡感染，冷却时间180秒，触发时持有者会被随机传送到安全位置

#### 📊 道具限制系统
- **种类限制**：每个玩家最多只能拥有6种不同种类的道具（可配置）
- **数量限制**：每个玩家最多获得1个刷新道具（可配置）
- **阵营限制**：部分道具仅限特定阵营使用
- **唯一性**：某些道具（如一次机会）每个玩家只能拥有一个

#### ⏰ 智能时间管理系统
- **游戏倒计时**：每次发放道具后自动提示"距离游戏结束还有X分钟Y秒"
- **母体禁足**：母体鬼前20秒禁足，最后10秒显示醒目倒计时
- **队列提示**：队列满员后显示10秒醒目倒计时

### 👥 沉浸式视觉系统
- **队伍区分**：人类（白色）、鬼（红色）、母体鬼（深红色）
- **即时提示**：使用道具时ActionBar显示使用方法
- **重要通知**：关键时刻显示居中大文本提示
- **视听效果**：感染闪电/音效、幽灵感知高亮等特效

### 🛡️ 神圣守护系统（v2.2.1新增）
- **最后一位人类**：当只剩下一位人类玩家时，自动激活神圣守护
- **免疫感染**：拥有3次免疫感染的机会（可配置）
- **随机传送**：被攻击时随机传送到地图安全位置
- **特殊效果**：获得速度I效果、发光效果
- **失效机制**：使用次数耗尽后获得隐身10秒效果
- **完整管理**：管理员可控制所有神圣守护参数

### 🗺️ 灵活区域管理系统
- **简易选区**：使用岩浆膏进行左键/右键选区（物品可配置）
- **区域存档**：支持保存最多20个游戏区域
- **一键管理**：保存、加载、删除、查看区域信息
- **自动优化**：游戏开始时自动传送玩家到区域中心
- **边界保护**：游戏期间禁止离开指定安全区域

## 🚀 快速开始

### 📦 安装要求
#### 必需插件
- **Vault** + 经济插件（如EssentialsX） - 经济系统支持

#### 可选模组
- **Injured Effects** - 实现2点生命值效果（增强游戏体验）

#### 推荐环境
- **Minecraft版本**：1.20.x（最佳兼容性）
- **服务器核心**：Paper/Spigot（推荐Paper以获得最佳性能）

## 🎮 游戏指南

### 📝 玩家基础命令

#### 主命令
```bash
/gost join      # 加入游戏队列
/gost leave     # 离开游戏或队列
/gost info      # 查看当前游戏信息
/gost help      # 显示帮助信息
```

#### 命令别名
```bash
/join           # /gost join 的别名
/leave          # /gost leave 的别名
```

#### 详细说明
1. **/gost join**
   - 功能：加入游戏队列
   - 权限：`gost.player` 和 `gost.join`
   - 条件：需要100金币入场费（可配置）
   - 效果：加入队列，等待其他玩家加入

2. **/gost leave**
   - 功能：离开游戏或队列
   - 权限：`gost.player` 和 `gost.leave`
   - 效果：如果已在队列中，退出队列并退还金币；如果已在游戏中，退出游戏

3. **/gost info**
   - 功能：查看当前游戏信息
   - 权限：`gost.player`
   - 显示：队列状态、游戏状态、玩家数量等信息

4. **/gost help**
   - 功能：显示玩家帮助信息
   - 权限：`gost.player`
   - 显示：所有玩家可用命令列表

### 🧰 道具使用指南

#### ⚡ 感染机制说明
- **感染方式**：鬼玩家**右键点击**人类玩家进行感染（不是左键攻击）
- **感染条件**：双方必须在游戏区域内，且人类玩家处于存活状态
- **感染效果**：被感染的人类玩家立即转变为鬼阵营
- **感染提示**：被感染玩家会收到屏幕居中字幕和音效提示
- **感染限制**：一次机会道具可以抵挡一次感染（被动触发）

#### 🧪 肾上腺素（人类专属 - 速度药水）
- **物品图标**：喷溅型速度药水
- **详细效果**：立即获得**速度II**效果，大幅提升移动速度
- **持续时间**：10秒（可在config.yml中配置）
- **使用方法**：**右键点击**物品直接使用
- **使用反馈**：
  - ActionBar显示：`右键使用获得速度提升`
  - 获得速度粒子效果
  - 聊天框提示：`你使用了肾上腺素！速度提升！`
- **战术价值**：逃生、追击、快速转移位置
- **注意事项**：仅限人类玩家使用，鬼玩家无法使用

#### 🔥 狂暴药水（鬼专属 - 速度药水）
- **物品图标**：喷溅型速度药水（红色）
- **详细效果**：立即获得**速度II**效果，大幅提升移动速度
- **持续时间**：10秒（可在config.yml中配置）
- **使用方法**：**右键点击**物品直接使用
- **使用反馈**：
  - ActionBar显示：`右键使用获得速度提升`
  - 获得速度粒子效果
  - 聊天框提示：`你使用了狂暴药水！速度提升！`
- **战术价值**：快速追击人类、快速移动感染
- **注意事项**：仅限鬼玩家使用，人类玩家无法使用

#### ❄️ 凝冰球（投掷类道具 - 雪球）
- **物品图标**：雪球
- **详细效果**：命中玩家后使其获得**减速IV**效果，大幅降低移动速度（v2.2.1_Cover version修复：现在对所有玩家生效）
- **持续时间**：4秒（可在config.yml中配置）
- **使用方法**：**右键点击**投掷雪球
- **使用反馈**：
  - 命中玩家时显示减速粒子效果
  - 被命中玩家收到提示：`你被凝冰球击中了！移动速度降低！`
  - 使用者收到提示：`成功命中目标！`
- **战术价值**：
  - **人类使用**：阻止鬼玩家追击、为队友创造逃生机会
  - **鬼使用**：减缓人类逃跑速度、协助队友围捕
- **注意事项**：需要准确投掷命中，对所有玩家都有效

#### 👻 控魂术（人类战略道具 - 末影之眼）
- **物品图标**：末影之眼
- **详细效果**：使用后**冻结区域内所有鬼玩家**，使其完全无法移动
- **持续时间**：6秒（可在config.yml中配置）
- **冷却时间**：18秒（可在config.yml中配置）
- **使用方法**：**右键点击**物品使用
- **使用反馈**：
  - ActionBar显示：`右键使用冻结所有鬼`
  - 全服广播：`[控魂术] 所有鬼玩家被冻结！`
  - 鬼玩家收到提示：`你被控魂术影响了！无法移动！`
- **战术价值**：团队逃生、战略转移、关键时刻保命
- **注意事项**：一次性使用道具，使用后消失，有冷却时间限制

#### 🍖 臭牛排（刷新道具 - 熟牛排）
- **物品图标**：熟牛排
- **详细效果**：无视当前饱食度**立即食用**，获得**速度II**效果和**发光**效果
- **持续时间**：
  - 速度效果：14秒（可配置）
  - 发光效果：10秒（可配置）
- **冷却时间**：30秒（可配置）
- **使用方法**：**右键点击**直接食用
- **使用反馈**：
  - 立即恢复饱食度
  - 获得速度粒子效果和发光效果
  - 聊天框提示：`你食用了臭牛排！获得速度II效果和发光效果！`
- **特色功能**：任何饱食度状态下均可立即食用，无需等待
- **战术价值**：紧急情况下的快速速度提升和位置暴露（发光效果）

#### 💎 传送珍珠（战术道具 - 末影珍珠）
- **物品图标**：末影珍珠
- **详细效果**：**右键投掷**后传送到落点位置
- **冷却时间**：20秒（可在config.yml中配置）
- **阵营限制**：人类和鬼玩家均可使用
- **使用方法**：**右键点击**投掷末影珍珠
- **使用反馈**：
  - 传送时显示末影粒子效果
  - 冷却期间物品显示冷却状态
  - 聊天框显示冷却剩余时间
- **战术价值**：
  - 人类：快速逃生、跨越障碍
  - 鬼：快速接近目标、突袭
- **注意事项**：冷却期间无法再次使用，注意传送落点安全

#### 🧭 灵魂探测器（鬼专属道具 - 指南针）
- **物品图标**：指南针
- **详细效果**：使用后使**所有玩家获得高亮效果**，暴露位置
- **持续时间**：25秒（可在config.yml中配置）
- **冷却时间**：35秒（可在config.yml中配置）
- **阵营限制**：仅限鬼玩家使用
- **使用方法**：**右键点击**使用指南针
- **使用反馈**：
  - 所有玩家身体发光（高亮效果）
  - 全服广播：`灵魂探测器已激活！所有玩家位置暴露！`
  - 使用者收到提示：`灵魂探测器已启动，持续25秒`
- **战术价值**：发现隐藏的人类、团队围剿、战略侦查
- **注意事项**：一次性使用道具，使用后消失

#### 🛡️ 一次机会（人类专属道具 - 不死图腾）
- **物品图标**：不死图腾
- **详细效果**：**被动触发**，抵挡一次鬼玩家的感染攻击，并随机传送持有者
- **触发条件**：当鬼玩家**右键点击**试图感染时自动触发
- **人类效果**：
  - 速度II效果，持续10秒（可配置）
  - 高亮效果，持续10秒（可配置）
  - **随机传送**：被传送到游戏区域内的随机安全位置
- **鬼效果**：
  - 缓慢I效果，持续7秒（可配置）
- **冷却时间**：180秒（可在config.yml中配置）
- **传送机制**：
  - 在游戏区域内随机选择安全位置
  - 确保位置安全（不在液体中，有足够空间）
  - 显示传送坐标给玩家
- **使用方法**：自动触发，无需手动使用
- **使用反馈**：
  - **屏幕居中字幕**：`✨ 一次机会触发！` / `成功抵挡感染！`
  - **对话框消息**：详细显示效果信息
  - **全服广播**：`XXX 使用了一次机会抵挡了 YYY 的感染！`
  - **个人提示**：人类获得速度和高亮，鬼获得减速
- **战术价值**：关键时刻保命、反击机会、削弱攻击者
- **特殊限制**：
  - 每个玩家只能拥有一个
  - 触发后道具消失
  - 冷却期间无法再次获得效果
- **注意事项**：仅抵挡感染，不抵挡其他伤害

### ⚙️ 管理员命令大全

#### 主管理员命令
```bash
/gostadmin                 # 显示管理员帮助（需要 gost.admin 权限）
/ga                        # /gostadmin 的别名
```

#### 区域管理命令
```bash
/gostadmin tool            # 获取区域选择工具（岩浆膏）
/gostadmin pos1            # 设置第一个点（工具左键点击）
/gostadmin pos2            # 设置第二个点（工具右键点击）
/gostadmin save <名称>     # 保存当前选区为指定名称
/gostadmin list            # 列出所有已保存的区域
/gostadmin load <名称> [enable|disable]  # 加载/启用/禁用区域
/gostadmin delete <名称>   # 删除指定的保存区域
/gostadmin info <名称>     # 查看指定区域的详细信息
/gostadmin clear           # 清除当前选区
```

#### 游戏管理命令
```bash
/gostadmin start <区域名称>  # 使用指定区域强制开始游戏
/gostadmin stop             # 强制停止当前游戏
/gostadmin reload           # 重载配置文件
/gostadmin status           # 查看当前游戏状态
/gostadmin dark <on|off|status>  # 黑暗效果管理
/gostadmin heartbeat <on|off|status>  # 心跳声效果管理
/gostadmin help             # 显示管理员帮助
```

#### ⚠️ 历史功能：假人系统管理命令（v2.2.0后已移除）
```bash
/gostadmin bot add <数量>     # 添加指定数量的假人到队列（已移除）
/gostadmin bot remove <数量>  # 从队列移除指定数量的假人（已移除）
/gostadmin bot clear          # 清除所有假人（已移除）
/gostadmin bot info           # 查看假人信息（已移除）
/gostadmin bot count          # 查看假人数量（已移除）
```



#### 神圣守护管理命令（v2.2.1更新）
```bash
/divineguardian              # 显示神圣守护帮助（需要 gost.admin.divineguardian 权限）
/dg                          # /divineguardian 的别名

# 状态查看
/divineguardian status       # 查看神圣守护状态
/divineguardian info         # 查看当前神圣守护信息

# 功能控制
/divineguardian enable       # 启用神圣守护功能
/divineguardian disable      # 禁用神圣守护功能
/divineguardian reload       # 重新加载神圣守护配置
/divineguardian clear        # 清除神圣守护数据

# 配置管理
/divineguardian setcharges <次数>    # 设置最大使用次数（1-10）
/divineguardian setcooldown <秒数>   # 设置冷却时间（1-60秒）
/divineguardian broadcast <on|off>   # 设置广播开关
/divineguardian setmode <1|2>        # 设置模式（1=神圣守护，2=救赎者）

# 高级管理
/divineguardian force <玩家名>       # 强制为指定玩家激活神圣守护
```

#### 鬼玩家粒子效果管理命令（v2.1.2新增）
```bash
/ghostparticle               # 显示粒子效果帮助（需要 gost.admin.ghostparticle 权限）
/gp                          # /ghostparticle 的别名

# 状态查看
/ghostparticle status        # 查看粒子效果状态
/ghostparticle listtypes     # 列出可用粒子类型

# 功能控制
/ghostparticle enable        # 启用粒子效果
/ghostparticle disable       # 禁用粒子效果
/ghostparticle reload        # 重新加载配置
/ghostparticle test          # 测试粒子效果

# 配置管理
/ghostparticle settype <类型>        # 设置粒子类型
/ghostparticle setcount <数量>       # 设置粒子数量
/ghostparticle setinterval <间隔>    # 设置生成间隔（刻）
/ghostparticle setmothercolor <红,绿,蓝>  # 设置母体鬼颜色
/ghostparticle setnormalcolor <红,绿,蓝>  # 设置普通鬼颜色
/ghostparticle setsize <大小>        # 设置粒子大小
/ghostparticle setpreparation <on|off>  # 设置准备阶段显示
```

#### 详细命令说明

##### 区域管理命令
1. **/gostadmin tool**
   - 功能：获取区域选择工具（默认岩浆膏）
   - 权限：`gost.admin`
   - 效果：给予玩家选区工具

2. **/gostadmin pos1 / pos2**
   - 功能：设置选区点1和点2
   - 权限：`gost.admin`
   - 用法：先用工具左键/右键点击方块，然后执行命令

3. **/gostadmin save <名称>**
   - 功能：保存当前选区
   - 权限：`gost.admin.create`
   - 参数：区域名称（不能包含空格）

4. **/gostadmin load <名称> [enable|disable]**
   - 功能：加载/启用/禁用区域
   - 权限：`gost.admin`
   - 参数：区域名称 + 可选操作（enable/disable）

5. **/gostadmin delete <名称>**
   - 功能：删除区域
   - 权限：`gost.admin.delete`
   - 参数：区域名称

##### 游戏管理命令
1. **/gostadmin start <区域名称>**
   - 功能：强制开始游戏
   - 权限：`gost.admin.manage`
   - 参数：必须指定已保存的区域名称

2. **/gostadmin stop**
   - 功能：强制停止游戏
   - 权限：`gost.admin.manage`
   - 效果：立即结束当前游戏

3. **/gostadmin reload**
   - 功能：重载配置文件
   - 权限：`gost.admin.reload`
   - 效果：重新加载 config.yml 配置



##### 黑暗效果命令
1. **/gostadmin dark on**
   - 功能：启用黑暗效果
   - 权限：`gost.admin`
   - 效果：给予所有玩家失明效果

2. **/gostadmin dark off**
   - 功能：禁用黑暗效果
   - 权限：`gost.admin`
   - 效果：移除所有玩家的失明效果

##### 心跳声效果命令
1. **/gostadmin heartbeat on**
   - 功能：启用心跳声效果
   - 权限：`gost.admin`
   - 效果：游戏过程中人类玩家听到监守者心跳声

2. **/gostadmin heartbeat off**
   - 功能：禁用心跳声效果
   - 权限：`gost.admin`
   - 效果：停止心跳声播放

## 🎯 完整游戏流程

### 1. 🗺️ 区域设置阶段
```bash
# 第一步：获取选区工具
/gostadmin tool

# 第二步：使用工具选择区域
- 左键点击：设置第一个点（pos1）
- 右键点击：设置第二个点（pos2）

# 第三步：保存区域
/gostadmin save 我的竞技场

# 第四步：加载区域（开始游戏前必需）
/gostadmin load 我的竞技场
```

### 2. 👥 玩家准备阶段
```bash
# 玩家加入游戏队列
/gost join

# 查看当前队列状态
/gost info

# 队列满员后自动开始10秒倒计时
# 倒计时显示：居中大文本提示
```

### 👻 鬼转人类功能（2.1版本新增）

#### 功能概述
在游戏最后阶段（默认剩余3分钟），随机将非母体的鬼玩家转换回人类阵容，增加游戏变数和策略性。

#### 触发条件
1. **时间条件**：游戏剩余时间达到配置值（默认180秒/3分钟）
2. **玩家条件**：只转换非母体的鬼玩家
3. **数量限制**：每次转换指定数量的玩家（默认1名）

#### 转换效果
1. **阵容转换**：从鬼阵容移除，加入人类阵容
2. **道具重置**：清空所有原有道具
3. **基础道具**：给予肾上腺素道具（速度效果）
4. **通知系统**：
   - 个人消息：详细说明转换效果
   - 全服广播：告知所有玩家转换信息
   - 屏幕字幕：显示转换动画

#### 配置说明
```yaml
ghost-to-human:
  enabled: false          # 是否启用功能（默认关闭）
  remaining-time: 180     # 剩余多少秒时触发（默认3分钟）
  count: 1                # 转换数量（默认1名）
```

#### 战术意义
- **平衡调整**：在游戏后期为人类方增加力量
- **策略变化**：改变游戏局势，增加不确定性
- **趣味性**：为被感染的玩家提供"复活"机会

### 3. 🚀 游戏进行阶段
- **准备阶段**：20秒（可配置），玩家适应环境
- **游戏阶段**：7分钟（可配置），紧张刺激的对抗
- **道具刷新**：每分钟自动刷新随机道具
- **母体禁足**：母体鬼前20秒禁足，最后10秒倒计时提示
- **感染机制**：鬼触碰人类即可感染，转化为鬼阵营

### 4. 🏆 游戏结束阶段
- **鬼胜利条件**：所有人类被感染
- **人类胜利条件**：时间结束仍有幸存者
- **奖励分配**：
  - **人类胜利时**：人类阵容获得奖池的70%，鬼阵容获得奖池的30%
  - **鬼胜利时**：鬼阵容获得奖池的100%
  - **人类奖金分配**：100%按存活时间比例分配
  - **鬼奖金分配**：70%按鬼存活时间，30%按感染人数比例分配
- **详细反馈**：每个玩家收到详细的奖金分配说明
- **自动清理**：游戏结束后自动重置所有玩家状态

## ⚙️ 配置文件详解

配置文件位于 `plugins/Gost/config.yml`，所有配置项都支持热重载。

### 📋 主要配置项示例
```yaml
# 配置版本（勿手动修改）
ScriptIrc-config-version: 20

# 🎮 游戏核心设置
game:
  duration: 420                 # 游戏总时长（秒），默认7分钟
  preparation-time: 20          # 准备阶段时长（秒）
  queue-time: 60                # 队列等待时间（秒）
  min-players: 2                # 最小玩家数
  max-players: 16               # 最大玩家数
  max-games: 1                  # 最大同时游戏数（0=禁止新游戏）
  match-queue-time: 30          # 匹配队列时间（秒），队列满员后等待多久开始游戏

# 💰 经济系统设置
economy:
  entry-fee: 100.0              # 入场费金额
  server-bonus: 5000.0          # 服务器奖金金额

# 🧰 道具效果设置
items:
  adrenaline:
    duration: 10                # 肾上腺素持续时间（秒）
    speed-level: 2              # 肾上腺素速度等级
  frenzy:
    duration: 10                # 狂暴药水持续时间（秒）
    speed-level: 2              # 狂暴药水速度等级
  ice-ball:
    slow-duration: 4            # 凝冰球减速持续时间（秒）
    slow-level: 4               # 凝冰球减速等级
  soul-control:
    freeze-duration: 6          # 控魂术冻结持续时间（秒）
    cooldown: 18                # 控魂术冷却时间（秒）
  teleport-pearl:
    cooldown: 20                # 传送珍珠冷却时间（秒）
  stinky-steak:
    speed-duration: 14          # 臭牛排速度效果持续时间（秒）
    speed-level: 1              # 臭牛排速度效果等级（1=速度II）
    glowing-duration: 10        # 臭牛排发光效果持续时间（秒）
    cooldown: 30                # 臭牛排冷却时间（秒）
  soul-detector:
    duration: 25                # 灵魂探测器暴露持续时间（秒）
    cooldown: 35                # 灵魂探测器冷却时间（秒）
  second-chance:
    cooldown: 180               # 一次机会冷却时间（秒）
    human-speed-duration: 10    # 人类玩家速度效果持续时间（秒）
    human-speed-level: 2        # 人类玩家速度效果等级
    human-glowing-duration: 10  # 人类玩家高亮效果持续时间（秒）
    ghost-slow-duration: 7      # 鬼玩家缓慢效果持续时间（秒）
    ghost-slow-level: 1         # 鬼玩家缓慢效果等级

# ⚡ 游戏效果设置
effects:
  mother-ghost-blindness-duration: 20    # 母体鬼失明持续时间（秒）
  ghost-immobilize-duration: 20          # 母体鬼固定时间（秒）
  ghost-sense-duration: 5                # 幽灵感知高亮持续时间（秒）
  infection-lightning: true              # 感染时是否显示闪电
  infection-sound: true                  # 感染时是否播放音效
  minute-glowing:
    enabled: true                        # 是否启用每分钟高亮效果
    duration: 5                          # 高亮持续时间（秒）
    interval: 60                         # 触发间隔时间（秒）

# 👻 鬼转人类功能设置
ghost-to-human:
  enabled: false                         # 是否启用鬼转人类功能
  remaining-time: 180                    # 剩余多少秒时触发（默认3分钟）
  count: 1                               # 转换数量

# ❤️ 游戏血量设置
health:
  max-health: 10.0               # 游戏期间玩家的最大生命值（默认10颗心，即20点生命值）
  # 推荐添加Injured Effects模组并限制最大生命值为4.0以下，可以添加恐怖氛围

# 🌑 黑暗效果设置（支持疾跑）
dark-effect:
  enabled: true                  # 是否启用黑暗效果（给予所有玩家黑暗视觉效果，不影响疾跑）
  duration: 999999               # 黑暗效果持续时间（秒），设置为极大值以持续整个游戏
  amplifier: 0                   # 黑暗效果等级（0为默认）

# ❤️ 心跳声设置
heartbeat:
  enabled: true                  # 是否启用心跳声效果（游戏过程中人类玩家听到监守者心跳声）
  interval: 10                   # 心跳声播放间隔（秒）

# 🔄 道具刷新系统
item-spawn:
  enabled: true                 # 是否启用道具刷新
  interval: 60                  # 刷新间隔（秒）
  max-per-refresh: 3            # 每次刷新数量上限
  max-per-player: 1             # 每位玩家最多获得数量
  max-item-types-per-player: 6  # 玩家最多拥有的道具种类数量

# 🛡️ 神圣守护设置（v2.2.1更新）
divine-guardian:
  enabled: false                # 是否启用神圣守护（最后一位人类玩家获得特殊能力）
  mode: "1"                     # 模式：1=神圣守护（感染免疫+随机传送），2=救赎者（转化鬼玩家）
  max-charges: 3                # 最大使用次数（免疫感染的次数）
  cooldown: 5                   # 每次使用冷却时间（秒）
  broadcast: true               # 是否广播神圣守护触发消息
  invisibility-duration: 10     # 神圣守护失效后隐身持续时间（秒）

# ⚡ 救赎者设置（神圣守护模式2）
redeemer:
  max-uses: 2                   # 神之救赎最大使用次数
  speed-level: 1                # 救赎者速度效果等级（1=速度I）
  holy-redemption-cooldown: 10  # 神之救赎冷却时间（秒）
  conversion-invincibility-time: 5 # 转化后无敌时间（秒）
  broadcast: true               # 是否广播救赎者消息

# 👻 鬼玩家粒子效果设置（v2.1.2新增）
ghost-particle:
  enabled: true                 # 是否启用鬼玩家粒子效果
  type: REDSTONE                # 粒子类型：REDSTONE, FLAME, SOUL_FIRE_FLAME, DRAGON_BREATH, PORTAL, DUST_COLOR_TRANSITION, SPELL_MOB, SPELL_WITCH, ENCHANTMENT_TABLE, CRIT_MAGIC, FIREWORKS_SPARK, HEART, NOTE, VILLAGER_ANGRY, VILLAGER_HAPPY, TOTEM_OF_UNDYING, COMPOSTER, SQUID_INK, DRIPPING_OBSIDIAN_TEAR, FALLING_OBSIDIAN_TEAR, LANDING_OBSIDIAN_TEAR
  count: 5                      # 每次生成粒子数量
  interval: 15                  # 粒子生成间隔（刻，20刻=1秒）
  mother-color: "255,0,0"       # 母体鬼粒子颜色（RGB格式：红,绿,蓝）
  normal-color: "0,255,0"       # 普通鬼粒子颜色（RGB格式：红,绿,蓝）
  size: 1.0                     # 粒子大小
  show-in-preparation: true     # 准备阶段是否显示粒子

# 🗺️ 区域选择设置
area:
  selection-tool: MAGMA_CREAM   # 选区工具物品（默认为岩浆膏）
  max-areas: 20                 # 最大存档区域数量
  auto-teleport: true           # 是否自动传送到区域
```

## 🔐 权限节点

### 玩家权限（默认所有玩家拥有）
- `gost.player` - 基础玩家权限（使用所有玩家命令：join/leave/info/help）
- `gost.join` - 允许玩家加入游戏队列（默认 true）
- `gost.leave` - 允许玩家离开游戏或队列（默认 true）

### 管理员权限
#### 基础管理员权限
- `gost.admin` - 管理员基础权限（默认 op）
  - 包含所有管理员命令的基础访问权限
  - 包括：区域管理、游戏管理、假人系统、黑暗效果、心跳声效果

#### 区域管理权限
- `gost.admin.create` - 创建区域权限
  - 允许使用 `/gostadmin save <名称>` 保存选区
- `gost.admin.delete` - 删除区域权限
  - 允许使用 `/gostadmin delete <名称>` 删除区域

#### 游戏管理权限
- `gost.admin.manage` - 管理游戏权限
  - 允许使用 `/gostadmin start <区域>` 开始游戏
  - 允许使用 `/gostadmin stop` 停止游戏
- `gost.admin.reload` - 重载配置权限
  - 允许使用 `/gostadmin reload` 重载配置文件

#### 特殊功能管理权限
- `gost.admin.divineguardian` - 管理神圣守护权限（v2.2.1新增，默认 op）
  - 允许使用所有 `/divineguardian` 命令
  - 包括：启用/禁用、配置管理、强制激活等
- `gost.admin.ghostparticle` - 管理鬼玩家粒子效果权限（v2.1.2新增，默认 op）
  - 允许使用所有 `/ghostparticle` 命令
  - 包括：启用/禁用、粒子配置、测试效果等

### 权限继承关系
```
gost.admin
├── gost.admin.create
├── gost.admin.delete
├── gost.admin.manage
├── gost.admin.reload
├── gost.admin.divineguardian
└── gost.admin.ghostparticle
```

### 权限配置示例
```yaml
# 给予玩家基础权限（默认已包含）
permissions:
  gost.player:
    default: true
  gost.join:
    default: true
  gost.leave:
    default: true

# 给予管理员完整权限
permissions:
  gost.admin:
    default: op
  gost.admin.divineguardian:
    default: op
  gost.admin.ghostparticle:
    default: op

# 给予特定玩家部分权限
permissions:
  gost.admin.create:
    default: false
    player1: true
    player2: true
  gost.admin.manage:
    default: false
    admin1: true
```

## ⚠️ 重要注意事项

### 必需条件
1. **经济系统**：需要安装 Vault + 经济插件（如 EssentialsX）
2. **PVP设置**：服务器PVP需要开启（用于感染判定）
3. **版本兼容**：确保使用 Minecraft 1.20.x 版本

### 可选增强
1. **生命值模组**：Injured Effects 模组可实现2点生命值效果（增强体验）
2. **性能优化**：推荐使用 Paper 服务端以获得最佳性能

### 游戏规则
1. **背包保护**：游戏期间禁止丢弃和移动物品
2. **区域边界**：玩家无法离开设定的游戏区域
3. **阵营平衡**：游戏会自动平衡人类和鬼的数量

## 🔧 故障排除指南

### 常见问题与解决方案

#### ❌ 游戏无法开始
1. **检查区域设置**：确保已使用 `/gostadmin tool` 设置并保存区域
2. **检查玩家数量**：等待玩家数达到最小要求（默认2人）
3. **检查游戏状态**：确认没有其他游戏正在进行

#### ❌ 队列系统问题（v2.2.1修复）
1. **队列卡住不启动**：如果只有一个玩家在队列中，游戏不会启动，这是正常设计
2. **无法退出队列**：使用 `/gost leave` 命令可以退出队列，退出时会退还入场金币
3. **Boss栏不消失**：v2.2.1已修复队列玩家离开后Boss栏不消失的问题
4. **队列人数不足自动清空**：当队列人数少于最小玩家数时，队列会自动清空并退还所有玩家金币
5. **队列状态提示**：加入队列时会收到退出提示，退出队列时会收到金币退还提示

#### ❌ 经济系统问题
1. **检查插件安装**：确认 Vault 和经济插件已正确安装
2. **检查权限设置**：确保玩家有足够金币支付入场费
3. **检查配置文件**：确认经济相关配置项设置正确

#### ❌ 感染机制无效
1. **检查PVP设置**：服务器必须开启 PVP
2. **检查区域范围**：玩家必须在设定的游戏区域内
3. **检查阵营状态**：确认玩家处于正确的游戏状态

#### ❌ 道具系统问题
1. **检查刷新配置**：确认 `item-spawn.enabled` 为 true
2. **检查游戏阶段**：道具只在游戏进行阶段刷新
3. **检查玩家状态**：确认玩家在游戏中且存活
4. **灵魂探测器问题**：v2.0.1版本中灵魂探测器可能失效，请升级到v2.0.2版本
5. **"一次机会"道具问题**：
   - 确认玩家是人类阵营（仅人类可使用）
   - 检查道具是否已触发过（每个玩家只能使用一次）
   - 确认冷却时间是否已结束（默认180秒）
   - v2.2.1版本已修复触发问题，请确保使用最新版本

#### ❌ 神圣守护系统问题
1. **神圣守护无法触发**：
   - 确认神圣守护功能已启用（`divine-guardian.enabled: true`）
   - 检查是否只剩下最后一名人类玩家
   - 确认游戏正在进行中（非准备阶段）
   - v2.2.1_Cover version版本已修复触发问题，请确保使用最新版本
2. **救赎者模式问题**：
   - 确认模式设置为2（`divine-guardian.mode: "2"`）
   - 检查救赎者道具是否正确发放
   - 确认转化次数未超过限制（默认2次）
3. **模式切换问题**：
   - 使用 `/divineguardian setmode <1|2>` 命令切换模式
   - 切换后可能需要重新加载配置或重启游戏
   - v2.2.1_Cover version版本增强了模式切换的稳定性

#### ❌ 语言系统问题（v2.2.1_Cover version已修复）
1. **玩家看到"Missing message"错误**：
   - 这是v2.2.1_B版本的已知问题
   - v2.2.1_Cover version已完全修复此问题
   - 现在玩家始终看到友好的中文提示
2. **消息显示不完整**：
   - v2.2.1_Cover version添加了90+条默认中文消息
   - 即使语言文件加载失败，玩家也能看到完整提示
   - 所有消息都有智能回退机制
3. **插件启动失败**：
   - v2.2.1_Cover version增加了错误隔离保护
   - 即使LanguageManager初始化失败，插件仍能正常运行
   - 所有消息获取都有异常捕获保护

### 📊 日志诊断
遇到问题时，请查看服务器日志：
```bash
# 查看实时日志
tail -f logs/latest.log

# 搜索Gost相关错误
grep -i "gost" logs/latest.log
```

## 📈 版本更新记录

### v2.2.1_Cover version（当前版本） {#v221coverversion}
- **🎯 语言系统全面优化（Cover version核心改进）**：
  - **大幅扩充默认消息库**：在 `LanguageManager.java` 中添加了 **90+ 条默认中文消息**
  - **智能消息回退机制**：不再返回 "§cMissing message: xxx"，而是根据消息键前缀返回友好的中文提示
  - **错误隔离保护**：所有消息获取和处理都加入了异常捕获，即使部分功能异常，也不会影响核心游戏流程
  - **插件启动保护**：如果 LanguageManager 初始化失败，插件仍能正常运行
  - **玩家体验优先**：所有玩家可见的提示都是完整的中文句子

- **🛠️ 游戏平衡性调整**：
  - **✅ 凝冰球修复**：修复了凝冰球只对鬼玩家生效的问题（现在对所有玩家生效）
  - **✅ 默认血量调整**：默认血量已调整为10颗心（20点生命值）
  - **✅ 道具系统优化**：移除了道具叠加开关相关设计
  - **✅ 配置版本更新**：更新了配置版本号（v19 → v20）

- **⚡ 神圣守护模式2 - 救赎者系统**：
  - **救赎者**：当人类数量减少到1时，最后一名人类成为救赎者
  - **神之救赎道具**：救赎者获得专属道具，可转化鬼玩家回人类
  - **随机传送**：使用道具后救赎者会被随机传送到安全位置
  - **次数限制**：道具可使用2次（可配置），使用完后回归普通人类
  - **视觉效果**：救赎者全程高亮显示，拥有速度效果

- **🎮 游戏模式切换**：
  - **模式1**：传统神圣守护（感染免疫+随机传送）
  - **模式2**：全新救赎者模式（转化鬼玩家）
  - **命令切换**：`/divineguardian setmode <1|2>`
  - **动态生效**：游戏进行中切换模式会立即生效

- **🔧 队列系统优化与修复**：
  - **队列人数不足强制清空**：当队列人数少于最小玩家数时，自动清空队列并退还所有玩家金币
  - **退出队列金币退还提示**：玩家退出队列时会收到金币退还的明确提示
  - **加入队列退出提示**：玩家加入队列时会收到使用`/gost leave`退出队列的提示
  - **Boss栏修复**：修复了队列中玩家离开后Boss栏不消失的问题

- **🗑️ 代码清理与优化**：
  - **移除NPC系统**：删除所有NPC相关代码，节省空间
  - **移除货币系统**：删除独立的货币发放系统
  - **移除观战系统**：删除观战相关代码
  - **移除假人（Bot）系统**：删除假人相关功能，简化插件结构
  - **配置清理**：清理config.yml中相关的配置项

- **🔧 性能与稳定性**：
  - **内存优化**：移除未使用的代码和依赖
  - **错误修复**：修复队列状态同步问题
  - **提示完善**：完善玩家操作反馈提示

- **🌑 黑暗效果与疾跑并存系统**：
  - **原生DARKNESS效果**：使用Minecraft原生的DARKNESS效果（1.19+）或BLINDNESS效果（旧版本）
  - **疾跑功能修复**：通过属性修改器增加30%移动速度，抵消DARKNESS效果对疾跑的影响
  - **智能检查机制**：每10 ticks检查玩家疾跑状态，自动恢复被阻止的疾跑
  - **属性修改器系统**：为每个有黑暗效果的玩家添加移动速度加成
  - **版本兼容性**：自动检测并适配不同Minecraft版本的效果类型
  - **管理员友好**：管理员和创造模式玩家也会受到黑暗效果影响，但有明确提示

- **🛡️ 神圣守护系统修复**：
  - **神圣守护触发修复**：修复神圣守护在某些情况下无法正确触发的问题
  - **救赎者模式优化**：优化救赎者道具的发放和使用逻辑
  - **模式切换稳定性**：增强游戏进行中模式切换的稳定性
  - **冷却时间修复**：修复神圣守护冷却时间计算问题

- **🎯 道具系统修复**：
  - **"一次机会"道具修复**：修复"一次机会"道具在某些情况下无法正确触发的问题
  - **道具冷却时间同步**：修复道具冷却时间显示不同步的问题
  - **道具发放逻辑优化**：优化随机道具发放算法，确保公平性
  - **道具使用反馈**：增强道具使用时的视觉和听觉反馈

- **⚙️ 配置系统升级**：
  - 配置版本升级到20
  - 黑暗效果描述更新为"不影响疾跑"
  - 黑暗效果默认启用状态改为true
### v2.1.3
- **🔧 修复管理员游戏效果免疫问题**：
  - **管理员黑暗效果修复**：管理员现在会正常受到黑暗效果影响
  - **母体鬼禁足修复**：管理员母体鬼现在会正常受到失明和禁足效果
  - **强制效果应用**：游戏效果现在会强制应用，确保管理员无法绕过
  - **游戏开始清理**：游戏开始时清理所有玩家效果，确保公平性
  - **管理员提示**：管理员收到游戏效果应用时的提示消息
- **🎮 创造模式玩家自动切换**：
  - **自动模式切换**：创造模式玩家进入游戏时自动切换为生存模式
  - **强制效果应用**：创造模式玩家也会被强制应用游戏效果
  - **公平性保障**：确保所有玩家在相同条件下进行游戏

### v2.1.2
- **👻 新增鬼玩家粒子效果系统**：
  - **持续环绕粒子**：鬼玩家身上持续显示环绕粒子效果
  - **颜色区分**：母体鬼为红色粒子，普通鬼为绿色粒子
  - **智能生成**：15刻间隔（约0.75秒），每次生成5个粒子
  - **多种粒子类型**：支持21种粒子类型，包括REDSTONE、FLAME、SOUL_FIRE_FLAME等
  - **RGB颜色系统**：支持自定义RGB颜色格式（红,绿,蓝）
  - **准备阶段控制**：可配置准备阶段是否显示粒子
- **🎮 鬼玩家粒子效果管理命令**：
  - `/ghostparticle` 或 `/gp` - 粒子效果管理主命令
  - `/ghostparticle status` - 查看粒子效果状态
  - `/ghostparticle enable/disable` - 启用/禁用粒子效果
  - `/ghostparticle reload` - 重新加载配置
  - `/ghostparticle settype <类型>` - 设置粒子类型
  - `/ghostparticle setcount <数量>` - 设置粒子数量
  - `/ghostparticle setinterval <间隔>` - 设置生成间隔（刻）
  - `/ghostparticle setmothercolor <红,绿,蓝>` - 设置母体鬼颜色
  - `/ghostparticle setnormalcolor <红,绿,蓝>` - 设置普通鬼颜色
  - `/ghostparticle setsize <大小>` - 设置粒子大小
  - `/ghostparticle setpreparation <on|off>` - 设置准备阶段显示
  - `/ghostparticle test` - 测试粒子效果
  - `/ghostparticle listtypes` - 列出可用粒子类型
- **⚙️ 配置系统升级**：
  - 添加鬼玩家粒子效果相关配置项（ghost-particle.enabled等）
  - 配置版本升级到14
  - 支持21种粒子类型选择
  - 完整的RGB颜色配置
- **🔐 权限系统完善**：
  - 新增 `gost.admin.ghostparticle` 权限节点
- **✨ 视觉效果增强**：
  - **环绕效果**：粒子围绕玩家旋转
  - **高度调整**：粒子在玩家身体周围
  - **母体增强**：母体鬼有更大的环绕半径和额外头顶粒子
  - **性能优化**：定时任务控制，避免性能问题
- **🔄 智能集成系统**：
  - 与现有玩家角色系统无缝集成
  - 玩家角色变化时自动更新粒子效果
  - 支持离线玩家数据更新
  - 自动清理数据防止内存泄漏

### v2.1.1
- **🛡️ 新增神圣守护系统**：
  - **最后一位人类**：当只剩下一位人类玩家时，自动激活神圣守护
  - **免疫感染**：拥有3次免疫感染的机会（可配置）
  - **随机传送**：被攻击时随机传送到地图安全位置
  - **特殊效果**：获得速度I效果、发光效果
  - **失效机制**：使用次数耗尽后获得隐身10秒效果
  - **完整管理**：管理员可控制所有神圣守护参数
- **✨ 神圣守护视觉效果**：
  - **发光效果**：显示神圣状态
  - **粒子效果**：激活、触发、传送、失效时都有粒子效果
  - **音效系统**：激活、触发、传送时播放特殊音效
  - **屏幕标题**：激活、触发、失效时显示屏幕标题
- **🎮 神圣守护管理命令**：
  - `/divineguardian status` - 查看神圣守护状态
  - `/divineguardian enable/disable` - 启用/禁用功能
  - `/divineguardian setcharges <次数>` - 设置最大使用次数
  - `/divineguardian setcooldown <秒数>` - 设置冷却时间
  - `/divineguardian broadcast <on|off>` - 设置广播开关
  - `/divineguardian force <玩家名>` - 强制激活神圣守护
  - `/divineguardian clear` - 清除神圣守护数据
- **⚙️ 配置系统升级**：
  - 添加神圣守护相关配置项（divine-guardian.enabled等）
  - 配置版本升级到13
- **🔐 权限系统完善**：
  - 新增 `gost.admin.divineguardian` 权限节点
- **🔄 感染系统优化**：
  - 神圣守护触发时自动取消感染
  - 随机传送前检查安全位置
  - 冷却时间机制防止滥用

### v2.1.0
- **🎮 奖金分配系统全面优化**：
  - **人类胜利时**：人类阵容获得奖池的70%，鬼阵容获得奖池的30%
  - **鬼胜利时**：鬼阵容获得奖池的100%
  - **人类奖金分配**：100%按存活时间比例分配
  - **鬼奖金分配**：70%按鬼存活时间，30%按感染人数比例分配
  - **详细反馈**：每个玩家收到详细的奖金分配说明
- **👻 转换玩家奖金优化**：
  - **累计鬼时间记录**：记录玩家作为鬼的累计时间
  - **人类奖金补偿**：被转换回人类的玩家获得20%的额外奖金补偿
  - **鬼奖金继承**：被转换回人类的玩家仍能获得作为鬼时的奖金
  - **公平分配**：确保转换玩家获得合理的双重奖励
- **游戏预备阶段优化**：预备阶段人类不会有黑暗效果，只有鬼有黑暗效果
- **新增心跳声系统**：游戏过程中人类方会循环播放监守者出现时的心跳声
- **新增管理员命令**：`/gostadmin heartbeat on/off/status` 控制心跳声开关
- **道具系统升级**：臭牛排新增发光效果和冷却时间
- **道具冷却时间调整**：控魂术(18s)、灵魂探测器(35s)、一次机会(180s)、传送珍珠(20s)
- **新增鬼转人类功能**：游戏剩余3分钟时随机转换非母体鬼玩家回人类阵容（默认关闭）
- **配置系统升级**：添加心跳声、道具冷却时间和鬼转人类功能相关配置项
- **权限系统完善**：`gost.player`权限默认直接给予所有玩家
- **插件版本升级**：统一版本号到2.1.0

### v2.0.2
- **修复编译错误**：修复ItemMeta导入和枚举常量错误
- **修复功能问题**：修复v2.0.1中"灵魂探测器"道具失效的问题
- **代码优化**：使用isHuman()和isGhost()方法简化角色检查逻辑
- **新增黑暗效果系统**：可配置的全局失明效果，增强恐怖氛围
- **新增管理员命令**：`/gostadmin dark on/off/status` 控制黑暗效果开关
- **配置系统升级**：添加黑暗效果相关配置项（dark-effect.enabled等）
- **性能优化**：优化居中文本字幕显示逻辑

### v2.0.1
- **新增传送珍珠**：双方可用战术传送道具
- **新增臭牛排**：无视饱食度的速度道具
- **智能倒计时系统**：队列满员10秒倒计时、母体禁足倒计时
- **游戏时间提示**：每次道具刷新后显示剩余时间
- **配置系统升级**：添加匹配队列时间、道具刷新间隔等配置
- **性能优化**：优化事件处理，减少服务器负载

### v2.0.0
- **架构重构**：完全重新设计插件架构
- **独立区域系统**：内置区域选择和管理功能
- **道具系统优化**：简化道具发放逻辑，提高稳定性
- **经济系统增强**：改进奖励分配算法
- **视觉效果升级**：ActionBar提示、居中大文本等

### v1.0.0
- **基础版本**：实现核心感染对抗玩法
- **基础道具系统**：肾上腺素、狂暴药水等基础道具
- **简易区域管理**：基础选区功能
- **经济系统雏形**：入场费和奖励分配

## 🤝 技术支持与反馈

遇到问题或有建议？请按以下步骤操作：

1. **查看文档**：仔细阅读本文档的相关章节
2. **检查日志**：查看服务器日志获取详细错误信息
3. **验证配置**：确认配置文件设置正确
4. **测试环境**：在纯净环境中测试插件功能
5. **联系支持**：提供详细的问题描述和日志信息

---

## 🎉 开始你的生存对抗之旅！

**Gost** 不仅仅是一个游戏插件，它是一个完整的生存对抗体验。无论你是喜欢躲藏的人类，还是热衷追逐的鬼，这里都有属于你的战斗方式。

### 给服主的建议：
1. **多样化地图**：创建不同风格的游戏区域（城市、森林、迷宫等）
2. **平衡配置**：根据玩家反馈调整道具效果和游戏时长
3. **定期活动**：举办比赛或活动，提高玩家参与度
4. **社区建设**：鼓励玩家分享策略和游戏录像

### 给玩家的提示：
1. **团队协作**：人类需要相互照应，鬼需要协同围捕
2. **道具策略**：合理使用道具，关键时刻扭转战局
3. **地形利用**：熟悉地图，利用障碍物和制高点
4. **时机把握**：母体鬼禁足期间是人类的最佳逃跑时机

**准备好开始这场猫鼠游戏了吗？加入队列，体验心跳加速的追逐对抗！** 🏃‍♂️👻

---

*"在Gost的世界里，生存不仅需要速度，更需要智慧与策略。"*

---

## 🇬🇧 English Version

# 🎮 Gost - Extreme Survival Confrontation Game

**Gost v2.2.1_Cover version** is a Minecraft server addon that combines multiple gameplays such as **cat and mouse, ghost catch, peek-a-boo, CF biochemical mode**, and more. Experience the life-and-death chase between humans and ghosts in a tense and exciting confrontation, using strategies and props to become the last survivor!

## 📋 Table of Contents

- [✨ Core Features](#-core-features)
  - [🏃‍♂️ An Exciting Chase Experience](#-an-exciting-chase-experience)
  - [🏆 Economy and Reward System](#-economy-and-reward-system)
  - [🏃‍♀️ Smart Matchmaking & Queue System](#-smart-matchmaking--queue-system)
  - [🧰 Multi-level Item System](#-multi-level-item-system)
  - [👥 Immersive Visual System](#-immersive-visual-system)
  - [🗺️ Flexible Area Management System](#️-flexible-area-management-system)
- [🚀 Quick Start](#-quick-start)
  - [📦 Installation Requirements](#-installation-requirements)
- [🎮 Game Guide](#-game-guide)
  - [📝 Player Basic Commands](#-player-basic-commands)
  - [🧰 Item Usage Guide](#-item-usage-guide)
  - [⚙️ Admin Commands](#️-admin-commands)
- [🎯 Complete Game Flow](#-complete-game-flow)
  - [1. 🗺️ Area Setup Phase](#1-️-area-setup-phase)
  - [2. 👥 Player Preparation Phase](#2-player-preparation-phase)
  - [3. 🚀 Game Running Phase](#3-game-running-phase)
  - [4. 🏆 Game End Phase](#4-game-end-phase)
- [⚙️ Configuration File Details](#️-configuration-file-details)
  - [📋 Main Configuration Items Example](#-main-configuration-items-example)
- [🔐 Permission Nodes](#-permission-nodes)
  - [Player Permissions (Default for all players)](#player-permissions-default-for-all-players)
  - [Admin Permissions](#admin-permissions)
- [⚠️ Important Notes](#️-important-notes)
  - [Required Conditions](#required-conditions)
  - [Optional Enhancements](#optional-enhancements)
  - [Game Rules](#game-rules)
- [🔧 Troubleshooting Guide](#-troubleshooting-guide)
  - [Frequently Asked Questions & Solutions](#frequently-asked-questions--solutions)
  - [📊 Log Diagnosis](#-log-diagnosis)
- [📈 Version Update History](#-version-update-history)
  - [v2.2.1 (Current Version)](#v221-current-version)
  - [v2.1.3](#v213)
  - [v2.0.2](#v202)
  - [v2.0.0](#v200)
  - [v1.0.0](#v100)
- [🤝 Technical Support & Feedback](#-technical-support--feedback)
- [🎉 Start Your Survival Confrontation Journey!](#-start-your-survival-confrontation-journey)

## ✨ Core Features

### 🏃‍♂️ An Exciting Chase Experience
- **Faction Confrontation**: Humans vs Ghosts, life-and-death chase, extreme escape
- **Time Pressure**: Decide the winner within the time limit, tense and exciting
- **Infection Transformation**: Transformed into a ghost after being touched by a ghost, dynamic camp changes
- **Strategic Survival**: Utilize terrain, power-ups, and teamwork to survive

### 🏆 Economy and Reward System
- **Entry Mechanism**: 100 gold entry fee (configurable) to ensure player engagement
- **Prize Pool System**: All Entry Fees + 5000 Coins Server Bonus (Configurable)
- **Smart Distribution**:
  - **When humans win**: 70% of the prize pool for the Human lineup, 30% of the prize pool for the Ghost lineup
  - **When ghosts win**: The ghost lineup receives 100% of the prize pool
  - **Human Bonus Distribution**: 100% proportional to survival time (the longer you survive, the more prizes you have)
  - **Ghost bonus distribution**: 70% is distributed according to the survival time of the ghost, and 30% is distributed according to the number of infected people
  - **Detailed feedback**: Each player receives detailed instructions on the distribution of bonuses, including survival time, number of infections, distribution ratio, etc.
- **Risk and Reward**: High risk and high reward, encouraging active participation and strategic play

### 🏃‍♀️ Smart Matchmaking & Queue System
- **Automatic Queue**: Players automatically join the queue when they join the game
- **Intelligent Matching**: Automatically starts the game when the number of players reaches the minimum requirement
- **Queue Management**: View queue status, number of players, and estimated waiting time
- **Fair Distribution**: Randomly assign initial roles (humans/ghosts) to ensure fairness

### 🧰 Multi-level Item System
- **🔄 Randomly refresh items** (automatically refresh every minute)
  - **🍖 Stinky Steak** (Weight 15): Eat directly regardless of satiety, gain Speed II for 14 seconds and Glow for 10 seconds, cooldown for 30 seconds
  - **💎 Teleport Pearl** (Weight 20): Available to both sides, right-click to throw teleport, cooldown 20 seconds
  - **🧭 Soul Detector** (Ghost Exclusive, Weight 12): After use, all players glow for 25 seconds and cool down for 35 seconds
  - **🛡️ 1 Chance** (Human Exclusive, Weight 10): Passively triggers Resist Infection with a cooldown of 180 seconds, and randomly teleports the holder to a safe location

- **📊 Item restriction system**
  - **Variety Limit**: Each player can only have up to 6 different types of items (configurable)
  - **Quantity limit**: Each player can get up to 1 refresh item (configurable)
  - **Faction Restrictions**: Some items can only be used by certain factions
  - **Uniqueness**: Some items (such as a chance) can only be owned by one per player

### 👥 Immersive Visual System
- **🎵 Heartbeat Sound System**: Humans hear the Warden's heartbeat sound during the game (configurable)
- **🌑 Dark Effect System**: Global blindness effect to enhance the horror atmosphere (configurable)
- **👻 Ghost Visual Effects**: Ghost players have special visual effects
- **🎮 Game Stage Effects**: Different visual effects for different game stages

### 🗺️ Flexible Area Management System
- **Easy Selection**: Use Magma Paste for left/right click selection (item configurable)
- **Region Save**: Supports saving up to 20 game areas
- **One-click management**: Save, load, delete, and view area information
- **Auto-Optimize**: Automatically teleport players to the regional center at the start of the game
- **Boundary Protection**: It is forbidden to leave designated safe areas during gameplay

## 🚀 Quick Start

### 📦 Installation Requirements:
- **Required plugins**
  - **Vault + Economy Plugins** (e.g., EssentialsX) - Economy system support
- **Optional mods**
  - **Injured Effects** - Achieve 2 health effects (enhance gameplay)
- **Recommended environment**
  - **Minecraft version**: 1.20.x (best compatibility)
  - **Server core**: Paper/Spigot (recommended for best performance)

## 🎮 Game Guide

### 📝 Player Basic Commands:
- `/gost join` - Join the game queue
- `/gost leave` - Leave the game queue
- `/gost info` - View game information
- `/gost help` - View help information

### 🧰 Item Usage Guide:
1. **Stinky Steak**: Right-click to eat, gain speed and glow effects
2. **Teleport Pearl**: Right-click to throw, teleport to landing location
3. **Soul Detector** (Ghost only): Right-click to use, reveal all players
4. **1 Chance** (Human only): Automatically triggers when infected, resists one infection

### ⚙️ Admin Commands:
- `/gostadmin create <name>` - Create a game area
- `/gostadmin delete <name>` - Delete a game area
- `/gostadmin list` - List all saved areas
- `/gostadmin load <name>` - Load a game area
- `/gostadmin start` - Start the game
- `/gostadmin stop` - Stop the game
- `/gostadmin reload` - Reload configuration
- `/gostadmin heartbeat <on/off/status>` - Control heartbeat sound
- `/gostadmin dark <on/off/status>` - Control dark effect

#### ⚠️ Historical Feature: Bot System Management Commands (Removed after v2.2.0):
- `/gostadmin bot add <count>` - Add specified number of bots to queue (removed)
- `/gostadmin bot remove <count>` - Remove specified number of bots from queue (removed)
- `/gostadmin bot clear` - Clear all bots (removed)
- `/gostadmin bot info` - View bot information (removed)
- `/gostadmin bot count` - View bot count (removed)

#### Divine Guardian Management Commands (v2.2.1 added):
- `/divineguardian status` - View divine guardian status
- `/divineguardian enable/disable` - Enable/disable function
- `/divineguardian reload` - Reload divine guardian configuration
- `/divineguardian info` - View current divine guardian information
- `/divineguardian clear` - Clear divine guardian data

#### Configuration Management:
- `/divineguardian setcharges <count>` - Set maximum usage count (1-10)
- `/divineguardian setcooldown <seconds>` - Set cooldown time (1-60 seconds)
- `/divineguardian broadcast <on|off>` - Set broadcast switch

#### Advanced Management:
- `/divineguardian force <playername>` - Force activate divine guardian for specified player

#### Ghost Particle Effect Management Commands (v2.1.2 added):
- `/ghostparticle status` - View particle effect status
- `/ghostparticle enable/disable` - Enable/disable particle effects
- `/ghostparticle reload` - Reload configuration
- `/ghostparticle test` - Test particle effects
- `/ghostparticle listtypes` - List available particle types

#### Configuration Management:
- `/ghostparticle settype <type>` - Set particle type
- `/ghostparticle setcount <count>` - Set particle count
- `/ghostparticle setinterval <interval>` - Set generation interval (ticks)
- `/ghostparticle setmothercolor <red,green,blue>` - Set mother ghost color
- `/ghostparticle setnormalcolor <red,green,blue>` - Set normal ghost color
- `/ghostparticle setsize <size>` - Set particle size
- `/ghostparticle setpreparation <on|off>` - Set preparation stage display

## 🎯 Complete Game Flow

### 1. 🗺️ Area Setup Phase
- Admin uses Magma Paste to select game area
- Save area with `/gostadmin create <name>`
- Load area with `/gostadmin load <name>`

### 2. 👥 Player Preparation Phase
- Players join queue with `/gost join`
- When minimum players reached, game automatically starts
- Players randomly assigned roles (Human/Ghost)
- 30-second preparation time

### 3. 🚀 Game Running Phase
- **Game duration**: 10 minutes (configurable)
- **Infection mechanism**: Ghosts infect humans by touching
- **Item refresh**: Items randomly refresh every minute
- **Ghost to Human conversion**: In last 3 minutes, non-parent ghosts may convert back to humans

### 4. 🏆 Game End Phase
- **Human victory**: At least one human survives until time ends
- **Ghost victory**: All humans infected before time ends
- **Bonus distribution**: Based on survival time and infection count
- **Detailed feedback**: Each player receives bonus breakdown

## ⚙️ Configuration File Details

### 📋 Main Configuration Items Example:
```yaml
# config.yml
ScriptIrc-config-version: 14
game:
  duration: 600  # Game duration in seconds (10 minutes)
  min-players: 2  # Minimum players to start
  max-players: 20  # Maximum players
  entry-fee: 100  # Entry fee in coins
  server-bonus: 5000  # Server bonus in coins
  
heartbeat:
  enabled: true  # Enable heartbeat sound
  interval: 20  # Heartbeat interval in seconds
  
dark-effect:
  enabled: true  # Enable dark effect
  intensity: 10  # Effect intensity
  
ghost-to-human:
  enabled: false  # Enable ghost to human conversion
  trigger-time: 180  # Time remaining to trigger (seconds)
  max-conversions: 1  # Maximum conversions per trigger

divine-guardian:
  enabled: false  # Enable divine guardian (last human gets special abilities)
  max-charges: 3  # Maximum usage count (immunity to infection)
  cooldown: 5  # Cooldown time per use (seconds)
  broadcast: true  # Broadcast divine guardian trigger messages
  invisibility-duration: 10  # Invisibility duration after divine guardian expires (seconds)

ghost-particle:
  enabled: true  # Enable ghost player particle effects
  type: REDSTONE  # Particle type: REDSTONE, FLAME, SOUL_FIRE_FLAME, DRAGON_BREATH, PORTAL, DUST_COLOR_TRANSITION, SPELL_MOB, SPELL_WITCH, ENCHANTMENT_TABLE, CRIT_MAGIC, FIREWORKS_SPARK, HEART, NOTE, VILLAGER_ANGRY, VILLAGER_HAPPY, TOTEM_OF_UNDYING, COMPOSTER, SQUID_INK, DRIPPING_OBSIDIAN_TEAR, FALLING_OBSIDIAN_TEAR, LANDING_OBSIDIAN_TEAR
  count: 5  # Number of particles per generation
  interval: 15  # Particle generation interval (ticks, 20 ticks = 1 second)
  mother-color: "255,0,0"  # Mother ghost particle color (RGB format: red,green,blue)
  normal-color: "0,255,0"  # Normal ghost particle color (RGB format: red,green,blue)
  size: 1.0  # Particle size
  show-in-preparation: true  # Whether to show particles during preparation stage
```

## 🔐 Permission Nodes

### Player Permissions (Default for all players):
- `gost.player` - Base player permissions (use all player commands: join/leave/info)

### Admin Permissions:
- `gost.admin` - Basic admin permissions
- `gost.admin.create` - Create zone permissions
- `gost.admin.delete` - Deletes regional permissions
- `gost.admin.reload` - Overload configuration permissions
- `gost.admin.manage` - Manage game permissions
- `gost.admin.divineguardian` - Manage divine guardian permissions (v2.2.1 added)
- `gost.admin.ghostparticle` - Manage ghost particle effect permissions (v2.1.2 added)

## ⚠️ Important Notes

### Required Conditions:
1. **Vault + Economy plugin** must be installed
2. **PVP must be enabled** on the server
3. **Game area must be set** before starting game

### Optional Enhancements:
1. **Injured Effects mod** for better visual effects
2. **Custom maps** for varied gameplay
3. **Sound packs** for enhanced atmosphere

### Game Rules:
1. Players cannot leave game area during gameplay
2. Ghosts cannot infect other ghosts
3. Humans cannot infect anyone
4. Items have cooldowns and usage limits

## 🔧 Troubleshooting Guide

### Frequently Asked Questions & Solutions:

#### ❌ Game cannot be started
- **Check Locale Settings**: Ensure that the region is set up using the `/gostadmin` tool and saved
- **Check the number of players**: wait for the minimum number of players to reach the minimum (2 by default)
- **Check Game Status**: Confirm that no other games are playing

#### ❌ Economic system problems
- **Check Plugin Installation**: Verify that Vault and Economy plugins are installed correctly
- **Check permission settings**: Make sure players have enough coins to pay for the entrance fee
- **Check the profile**: Verify that the economy-related configuration items are set correctly

#### ❌ The infection mechanism is ineffective
- **Check PVP settings**: The server must have PVP turned on
- **Check the area range**: Players must be within the set game area
- **Check Faction Status**: Confirm that players are in the correct game state

#### ❌ Item system issues
- **Check the refresh configuration**: Confirm that item-spawn.enabled is true
- **Check the game stage**: Items only refresh during the game stage
- **Check player status**: Confirm that the player is in the game and alive
- **Soul detector issue**: The soul detector may not be invalid in v2.0.1, please upgrade to v2.0.2
- **"One Chance" item issues**:
  - Confirm player is human faction (humans only)
  - Check if item has already been triggered (each player can use only once)
  - Confirm cooldown has ended (default 180 seconds)
  - v2.2.1 version fixed trigger issues, ensure using latest version

#### ❌ Divine Guardian system issues
1. **Divine Guardian not triggering**:
   - Confirm Divine Guardian is enabled (`divine-guardian.enabled: true`)
   - Check if only one human player remains
   - Confirm game is in progress (not preparation stage)
   - v2.2.1 version fixed trigger issues, ensure using latest version
2. **Redeemer mode issues**:
   - Confirm mode is set to 2 (`divine-guardian.mode: "2"`)
   - Check if Redeemer item is correctly distributed
   - Confirm conversion count not exceeded limit (default 2 times)
3. **Mode switching issues**:
   - Use `/divineguardian setmode <1|2>` command to switch modes
   - May need to reload config or restart game after switching
   - v2.2.1 version enhanced mode switching stability

### 📊 Log Diagnosis:
```bash
# View real-time logs
tail -f logs/latest.log

# Search for Gost related errors
grep -i "gost" logs/latest.log
```

## 📈 Version Update History

### v2.2.1 (Current Version) {#v221-current-version}
- **⚡ Divine Guardian Mode 2 - Redeemer System**:
  - **Redeemer**: When only one human player remains, they become the Redeemer
  - **Holy Redemption Item**: Redeemer receives exclusive item to convert ghost players back to human
  - **Random Teleport**: After using the item, Redeemer is randomly teleported to a safe location
  - **Usage Limit**: Item can be used 2 times (configurable), after which Redeemer returns to normal human
  - **Visual Effects**: Redeemer is highlighted throughout and has speed effects
- **🎮 Game Mode Switching**:
  - **Mode 1**: Traditional Divine Guardian (infection immunity + random teleport)
  - **Mode 2**: New Redeemer mode (converts ghost players)
  - **Command Switching**: `/divineguardian setmode <1|2>`
  - **Dynamic Activation**: Mode changes take effect immediately, even during gameplay
- **🔧 Queue System Optimization & Fixes**:
  - **Automatic Queue Clearance**: When queue players are less than minimum required, queue auto-clears with gold refunds
  - **Exit Queue Gold Refund Notifications**: Players receive clear notifications when gold is refunded upon leaving queue
  - **Queue Join Guidance**: Players receive instructions on using `/gost leave` when joining queue
  - **Boss Bar Fix**: Fixed issue where Boss bar wouldn't disappear after players left queue
  - **Queue Status Transparency**: Real-time queue status display to avoid player confusion
- **🗑️ Code Cleanup & Optimization**:
  - **NPC System Removed**: All NPC-related code deleted to save space
  - **Currency System Removed**: Independent currency distribution system removed
  - **Spectator System Removed**: Spectator-related code deleted
  - **Bot System Removed**: Bot-related functionality deleted, simplifying plugin structure
  - **Config Cleanup**: Related configuration items cleaned from config.yml
- **🔧 Performance & Stability**:
  - **Memory Optimization**: Removed unused code and dependencies
  - **Bug Fixes**: Fixed queue status synchronization issues
  - **Notification Improvements**: Enhanced player feedback for operations

- **🛡️ Divine Guardian System Fixes**:
  - **Divine Guardian Trigger Fix**: Fixed issue where Divine Guardian sometimes failed to trigger correctly
  - **Redeemer Mode Optimization**: Optimized Redeemer item distribution and usage logic
  - **Mode Switching Stability**: Enhanced stability when switching modes during gameplay
  - **Cooldown Fix**: Fixed Divine Guardian cooldown calculation issues

- **🎯 Item System Fixes**:
  - **"One Chance" Item Fix**: Fixed issue where "One Chance" item sometimes failed to trigger correctly
  - **Item Cooldown Synchronization**: Fixed item cooldown display synchronization issues
  - **Item Distribution Logic Optimization**: Optimized random item distribution algorithm for fairness
  - **Item Usage Feedback**: Enhanced visual and auditory feedback when using items

- **🌑 Dark Effect with Sprint Support System**:
  - **Native DARKNESS Effect**: Uses Minecraft's native DARKNESS effect (1.19+) or BLINDNESS effect (older versions)
  - **Sprint Function Fix**: Adds 30% movement speed via attribute modifiers to counteract DARKNESS effect's impact on sprinting
  - **Smart Check Mechanism**: Checks player sprint status every 10 ticks, automatically restores blocked sprinting
  - **Attribute Modifier System**: Adds movement speed bonus to each player with dark effect
  - **Version Compatibility**: Automatically detects and adapts to different Minecraft version effect types
  - **Admin Friendly**: Admins and creative mode players also receive dark effects with clear notifications

- **⚙️ Configuration System Upgrade**:
  - Configuration version upgraded to 19
  - Dark effect description updated to "does not affect sprinting"
  - Dark effect default enabled state changed to true
### v2.1.3
- **🔧 Fixed Admin Game Effect Immunity Issue**:
  - **Admin Dark Effect Fix**: Administrators now properly receive dark effects during gameplay
  - **Mother Ghost Immobilization Fix**: Admin mother ghosts now properly receive blindness and immobilization effects
  - **Force Effect Application**: Game effects are now forcibly applied to ensure admins cannot bypass them
  - **Game Start Cleanup**: All player effects are cleared at game start to ensure fairness
  - **Admin Notifications**: Admins receive notifications when game effects are applied to them

### v2.1.2
- **👻 New Ghost Player Particle Effect System**:
  - **Continuous Orbiting Particles**: Ghost players continuously display orbiting particle effects
  - **Color Differentiation**: Mother ghost: red particles, Normal ghost: green particles
  - **Smart Generation**: 15 tick interval (approx. 0.75 seconds), 5 particles per generation
  - **Multiple Particle Types**: Support for 21 particle types including REDSTONE, FLAME, SOUL_FIRE_FLAME, etc.
  - **RGB Color System**: Support custom RGB color format (red,green,blue)
  - **Preparation Stage Control**: Configurable whether to show particles during preparation stage
- **🎮 Ghost Particle Effect Management Commands**:
  - `/ghostparticle` or `/gp` - Main management command
  - `/ghostparticle status` - View particle effect status
  - `/ghostparticle enable/disable` - Enable/disable particle effects
  - `/ghostparticle reload` - Reload configuration
  - `/ghostparticle settype <type>` - Set particle type
  - `/ghostparticle setcount <count>` - Set particle count
  - `/ghostparticle setinterval <interval>` - Set generation interval (ticks)
  - `/ghostparticle setmothercolor <red,green,blue>` - Set mother ghost color
  - `/ghostparticle setnormalcolor <red,green,blue>` - Set normal ghost color
  - `/ghostparticle setsize <size>` - Set particle size
  - `/ghostparticle setpreparation <on|off>` - Set preparation stage display
  - `/ghostparticle test` - Test particle effects
  - `/ghostparticle listtypes` - List available particle types
- **⚙️ Configuration System Upgrade**:
  - Added ghost particle effect configuration items (ghost-particle.enabled, etc.)
  - Configuration version upgraded to 14
  - Support for 21 particle type selections
  - Complete RGB color configuration
- **🔐 Permission System Improvement**:
  - Added `gost.admin.ghostparticle` permission node
- **✨ Visual Effect Enhancement**:
  - **Orbiting Effect**: Particles rotate around players
  - **Height Adjustment**: Particles around player's body
  - **Mother Ghost Enhancement**: Mother ghosts have larger orbit radius and additional head particles
  - **Performance Optimization**: Timer task control to avoid performance issues
- **🔄 Smart Integration System**:
  - Seamless integration with existing player role system
  - Automatic particle effect updates when player roles change
  - Support for offline player data updates
  - Automatic data cleanup to prevent memory leaks

### v2.1.1
- **🛡️ New Divine Guardian System**:
  - **Last Human**: Automatically activates when only one human player remains
  - **Infection Immunity**: Has 3 chances to resist infection (configurable)
  - **Random Teleport**: Randomly teleports to safe location when attacked
  - **Special Effects**: Gains Speed I effect and glowing effect
  - **Expiration Mechanism**: Gains invisibility for 10 seconds after charges are depleted
  - **Complete Management**: Administrators can control all divine guardian parameters
- **✨ Divine Guardian Visual Effects**:
  - **Glowing Effect**: Shows divine status
  - **Particle Effects**: Particles for activation, trigger, teleport, and expiration
  - **Sound System**: Special sounds for activation, trigger, and teleport
  - **Screen Titles**: Screen titles for activation, trigger, and expiration
- **🎮 Divine Guardian Management Commands**:
  - `/divineguardian status` - View divine guardian status
  - `/divineguardian enable/disable` - Enable/disable function
  - `/divineguardian setcharges <count>` - Set maximum usage count
  - `/divineguardian setcooldown <seconds>` - Set cooldown time
  - `/divineguardian broadcast <on|off>` - Set broadcast switch
  - `/divineguardian force <playername>` - Force activate divine guardian
  - `/divineguardian clear` - Clear divine guardian data
- **⚙️ Configuration System Upgrade**:
  - Added divine guardian related configuration items
  - Configuration version upgraded to 13
- **🔐 Permission System Improvement**:
  - Added `gost.admin.divineguardian` permission node
- **🔄 Infection System Optimization**:
  - Automatically cancels infection when divine guardian triggers
  - Checks safe location before random teleport
  - Cooldown mechanism to prevent abuse

### v2.1.0
- **🎮 Bonus distribution system fully optimized**:
  - **When humans win**: Human lineup gets 70% of prize pool, Ghost lineup gets 30%
  - **When ghosts win**: Ghost lineup receives 100% of prize pool
  - **Human Bonus Distribution**: 100% proportional to survival time
  - **Ghost bonus distribution**: 70% by ghost survival time, 30% by infection count
  - **Detailed feedback**: Each player receives detailed bonus distribution instructions
- **👻 Conversion Player Bonus Optimization**:
  - **Cumulative Ghost Time Record**: Records player's accumulated time as ghost
  - **Human Bonus Compensation**: Players converted back to human receive 20% extra bonus
  - **Ghost Bonus Inheritance**: Converted players still receive ghost bonuses
  - **Fair Distribution**: Ensures converting players get reasonable double reward
- **Game preparation stage optimization**: Humans no dark effects, only ghosts have dark effects
- **New heartbeat sound system**: Humans hear Warden's heartbeat during game
- **Added admin command**: `/gostadmin heartbeat on/off/status` control heartbeat sound
- **Upgraded item system**: Stinky Steak added glow effects and cooldowns
- **Item cooldown adjustments**: Soul Control (18s), Soul Detector (35s), One Chance (180s), Teleport Pearl (20s)
- **Added ghost to human function**: Convert non-parent ghosts back to humans in last 3 minutes (disabled by default)
- **Configuration System Upgrade**: Added heartbeat, item cooldown, and ghost conversion configs
- **Improved permission system**: `gost.player` permission given to all players by default
- **Plugin version upgrade**: Unified version number to 2.1.0

### v2.0.2
- Fix compilation errors: Fix ItemMeta import and enumeration constant errors
- Fixed functional issues: Fixed the issue of "Soul Detector" props failing in v2.0.1
- Code Optimization: Simplify the role checking logic using isHuman() and isGhost() methods
- New dark effect system: Configurable global blindness effect to enhance the horror atmosphere
- Added the administrator command: `/gostadmin dark on/off/status` to control the dark effect switch

### v2.0.0
- Initial release with core gameplay features
- Basic infection system
- Item system foundation
- Area management system
- Economy system integration

### v1.0.0
- Concept development
- Basic framework
- Initial testing phase

## 🤝 Technical Support & Feedback

If you encounter any issues or have suggestions:

1. **Check the FAQ section** above
2. **Review server logs** for error messages
3. **Verify plugin compatibility** with your server version
4. **Join our community** for support and updates

## 🎉 Start Your Survival Confrontation Journey!

### Tips for Server Owners:
1. **Diverse Maps**: Create different style game areas (city, forest, maze, etc.)
2. **Balance Configuration**: Adjust item effects and game duration based on player feedback
3. **Regular Events**: Host tournaments or events to increase player engagement
4. **Community Building**: Encourage players to share strategies and game recordings

### Tips for Players:
1. **Team Collaboration**: Humans need to watch each other, ghosts need to coordinate hunting
2. **Item Strategy**: Use items wisely, turn the tide at critical moments
3. **Terrain Utilization**: Familiarize with maps, use obstacles and high ground
4. **Timing**: Best escape time for humans is during mother ghost restriction period

**Ready to start this cat-and-mouse game? Join the queue and experience the heart-pounding chase confrontation!** 🏃‍♂️👻

---

*"In the world of Gost, survival requires not only speed, but also wisdom and strategy."*