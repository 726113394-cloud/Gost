# Gost Changelog / 更新日志

## 2.3.3 — 2026-10

### 🇨🇳 中文

本版本在 **2.3.2_B**（背包返还与掉线处理补丁）基础上完成 **2.3.3** 全量更新，并新增 Folia 调度兼容与六国语言文件。

---

#### 🐛 2.3.2_B 补丁（已并入 2.3.3 源码）

**背包返还失效修复**
- 快照深拷贝：保存/返还均 `ItemStack.clone()`，`Location` 同步克隆，杜绝游戏道具污染原始背包
- 防重复覆盖：`savePlayerState` 已有快照则跳过
- 清空前兜底：清空背包前若无快照则立即补存
- 顺序固化：先保存原始背包，再清空/发放
- 诊断日志：保存/返还打印物品数量

**玩家掉线处理**
- 掉线即退出对局，`PlayerQuitEvent` 同步还原背包
- 掉线即退队并退还入场费
- 静默处理（不向断线玩家发包），广播/Boss 栏照常
- 遗留快照兜底还原

---

#### ✨ 2.3.3 新增与修复

**👻 鬼死亡 → 旁观 → 复活（正式启用）**
- 死亡进入 **旁观模式**（不再用隐身妥协）
- ActionBar 复活倒计时
- 倒计时结束在游戏区域内随机坐标换回 **生存模式** 复活
- 根因修复：原先击杀路径只加隐身、复活时未 `setGameMode(SURVIVAL)`

**🎯 猎魔人死亡 → 普通鬼**
- 被母体击杀后立刻转为 **普通鬼**
- 旁观 + 复活倒计时结束后以普通鬼身份继续对局

**💎 母体进化系统（阶段调整）**
- 进化水晶改为在 **神圣守护阶段** 出现（原先猎魔人阶段）
- 固定/中心/随机坐标可配
- 普通鬼拾取后进化为母体

**🗡️ 收割者（伤害值判定）**
- 左键按配置伤害扣血，显示伤害量与剩余血量
- 血量归零进入旁观 + 复活倒计时
- 左键冷却 2 秒；右键「收割」4 格 AOE，伤害同左键，冲击粒子 + 凋零死亡音效，冷却 10 秒

**🎒 背包管理系统**
- 对局最多 9 道具；不可入背包；不堆叠
- 专属道具不可重复（通用道具除外）
- 收割者 / 神之救赎强制第一格

**🧰 道具规格对齐**
- 神之救赎：单次使用、转化鬼回人类、救赎者随机传送、独立守护 2 次
- 第二次机会：被动保留 + 180s 冷却（可配置是否消耗）
- 凝冰球：对所有人缓慢 Ⅳ / 4 秒
- 臭牛排：速度 Ⅲ / 14 秒 + 发光 10 秒
- 控魂术：时钟材质，人类专用，全场鬼定身 6 秒
- 灵魂探测器：母体专属，揭示 25 秒
- 冲刺矛：5.5 米，凋零死亡音效
- 其余道具效果/冷却/音效按规格修正

**⚙️ 跨服务端兼容层**
- `ApiCompat`：属性/药水/附魔运行时解析（Spigot/Paper/Purpur/Bukkit）
- `ParticleCompat`：新旧粒子名兼容
- `SoundCompat`：音效跨版本
- `ServerCompat` + `SchedulerCompat`：平台检测 + **Folia** 调度/实体线程适配

**🌐 六国语言文件（用户可改）**
- 支持：中文 / English / 日本語 / Español / Français / Deutsch
- 路径：`plugins/Gost/lang/<locale>.yml`
- `config.yml -> language.default` 选择语言，`/gost reload` 重载

**📦 配置**
- config version **31**
- 新增大量可配置项：道具全套、音效/字幕/广播/粒子开关、母体水晶、猎魔人死亡、复活边界、第二次机会行为等

---

### 🇬🇧 English

This release builds on **2.3.2_B** (backpack restore & disconnect patch) with the full **2.3.3** update, plus Folia scheduling compatibility and six-language files.

---

#### 🐛 2.3.2_B Patches (included in 2.3.3)

**Backpack restoration fix**
- Deep-copy snapshots (`ItemStack.clone()` + cloned `Location`) so game items never contaminate the original inventory
- Skip duplicate `savePlayerState` overwrites
- Backup before clearing inventory
- Fixed order: save original inventory first, then clear/init
- Diagnostic logs with item counts

**Player disconnect handling**
- Disconnect = leave match; restore inventory synchronously in `PlayerQuitEvent`
- Disconnect = leave queue and refund entry fee
- Silent to the disconnected player; broadcasts / boss bar still run
- Leftover snapshot fallback restore

---

#### ✨ 2.3.3 New & Fixed

**👻 Ghost death → spectator → respawn (fully enabled)**
- Death enters **Spectator mode** (no more invisibility compromise)
- ActionBar respawn countdown
- After countdown: random location in game area → **Survival** mode
- Root cause fixed: kill path only added invisibility; respawn never called `setGameMode(SURVIVAL)`

**🎯 Demon Hunter death → normal ghost**
- Killed by Mother Ghost → immediately becomes a **normal ghost**
- Spectator + countdown, then continues as a normal ghost

**💎 Mother Evolution (phase change)**
- Evolution crystal now spawns in the **Holy Guardian phase** (was Demon Hunter phase)
- Location mode: fixed / center / random
- Normal ghosts pick it up to evolve into Mother Ghost

**🗡️ Reaper (damage-based)**
- Left-click deals configured damage; shows damage + remaining HP
- HP reaches 0 → spectator + respawn countdown
- Left-click CD 2s; right-click “Harvest” 4-block AOE, same damage, shockwave particles + Wither death sound, CD 10s

**🎒 Inventory management**
- Max 9 items; cannot enter backpack; no stacking
- No duplicate exclusive items (universal items exempt)
- Reaper / Holy Redemption forced to first slot

**🧰 Item specs aligned**
- Holy Redemption: single use, convert ghost to human, redeemer teleport, independent guardian x2
- Second Chance: passive kept + 180s cooldown (consume toggle configurable)
- Ice Ball: Slowness IV / 4s on all players
- Stinky Steak: Speed III / 14s + Glowing 10s
- Soul Control: clock item, human-only, freeze all ghosts 6s
- Soul Detector: Mother-only, reveal 25s
- Spear Rush: 5.5 blocks, Wither death sound
- Other item effects / cooldowns / sounds corrected to spec

**⚙️ Cross-server compatibility**
- `ApiCompat`: runtime attribute/potion/enchant resolution (Spigot/Paper/Purpur/Bukkit)
- `ParticleCompat`: old/new particle names
- `SoundCompat`: cross-version sounds
- `ServerCompat` + `SchedulerCompat`: platform detect + **Folia** scheduling / entity-thread safety

**🌐 Six language files (user-editable)**
- Chinese / English / Japanese / Spanish / French / German
- Path: `plugins/Gost/lang/<locale>.yml`
- Select via `config.yml -> language.default`; `/gost reload` reloads

**📦 Config**
- Config version **31**
- Many new options: full item tuning, sound/title/broadcast/particle toggles, mother crystal, DH death, respawn boundary, second-chance behavior, etc.

---

## 2.3.2_B

See above (included in 2.3.3). Focus: backpack restore reliability + disconnect handling. No new gameplay; plugin/config versions unchanged at the time of 2.3.2_B.

## 2.3.2

Hunger system (configurable), Sprint Spear full version compatibility (<1.21.4 uses trident).

---

*Plugin version: 2.3.3 · Config version: 31 · Supported: Bukkit/Spigot/Paper/Purpur (full), Folia (scheduler), not applicable: Sponge / proxies (BungeeCord, Velocity, Waterfall).*
