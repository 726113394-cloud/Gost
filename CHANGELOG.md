# Gost Changelog / 更新日志

## 2.4.0 — 2026-11

<details>
<summary>点击看中文</summary>

#### ✨ 2.4.0 更新日志

本版本**不新增玩法**，专注把此前「已声明但未落地」的能力补齐：可复现构建、Folia 兼容、ActionBar 兼容、六语言真正生效。

**🔧 构建可复现**
- 修复 `build.gradle`：源码在 `main/` 与 `resources/`，不是 Gradle 默认的 `src/main/java`；补上此前缺失的 Vault 依赖
- 此前 `gradle build` **产不出可用 jar**，发版依赖手工命令。现在一条命令复现，且构建出的 class 清单与原 2.3.3 jar **完全一致**

**⚙️ Folia 兼容修复**
- 根因：`BukkitRunnable.runTask*()` 内部同样调 `Bukkit.getScheduler()`，Folia 上一律抛异常 —— 此前只修了直接调用，**全项目 17 处（`GameManager` 独占 7 处核心循环）实际都不通**
- 新增 `ServerCompat.runLaterForEntity` / `runTimerForEntity`，反射 Folia 的 `EntityScheduler`（`runDelayed` / `runAtFixedRate`）
- 母体水晶发光、旁观边界检查、守护与猎魔人特效循环、烟花引爆、ActionBar 改为**绑定实体所属区域线程**，不再走全局调度
- 修正平台探测：不再用 `getGlobalRegionScheduler` 判断（Paper 系后来也加了这个方法，会把 Purpur 26.x 误判成 Folia），改用 `RegionizedServer` 类作为强信号

**💬 ActionBar 修复（Spigot 上原为坏的）**
- 原代码直调 Paper 专有的 `player.sendActionBar(String)`，Spigot 抛 `NoSuchMethodError`；而兜底写的是 `catch (Exception)` —— **抓不到 `Error`**
- 改为按优先级**探测并缓存通道**：Adventure → BungeeCord 聊天通道 → 聊天栏兜底。这一层是为新版服务端准备的，Bungee 聊天 API 正在被移除
- 影响：2.3.3 的「旁观复活倒计时」在 Spigot 上此前实际不可用

**🌐 多语言（六语言真正生效）**
- 语言 key **66 → 599**，全项目**玩家可见文案不再有硬编码中文**
- 新增 `/gost lang <locale>` 与 `/gost lang reset`，**立即生效，无需 reload**
- 缺失 key 四级回退：玩家语言 → 服务器默认 → 内置中文 → key 本身（暴露漏配而不是静默吞掉）
- **语言文件自动补齐**：新增 key 自动追加到 `plugins/Gost/lang/*.yml`，保留你已改的译文与注释，只追加不重写
- 修复 `en_US.yml` 一处未转义引号导致的 YAML 解析失败 —— 曾使英文只剩旧版 66 条
- 新增 `tools/check_lang.py`：六语言键集一致 + 行级语法校验 + 代码 key 覆盖，已接入 CI

**🎲 随机地图**
- 每局从**已启用区域**中随机选图（`area.random-each-game`，默认开启）
- `/gostadmin load <名称>` 指定的区域**只对下一局生效**，之后自动恢复随机
- 旧版实际上只在「没有选中区域」时随机一次，之后每局都复用同一张图

**💀 死亡状态可配（默认改回隐身）**
- 新增 `death-state.mode`：`invisible`（**默认**，隐身）/ `spectator`（旁观）
- 隐身态：保持生存、回满血、施加隐身，不限制移动 —— 即 2.3.3 之前的行为
- 旁观态：切入旁观模式并启用边界检查 —— 即 2.3.3 的行为
- 两种状态都会登记为「已淘汰」，不可被感染、不会被选为目标，复活流程一致
- 玩家真实死亡并重生时，隐身态会重新施加隐身；旁观态保持旁观

**🩺 运行期自检**
- 新增 `/gostadmin selftest`：单人、不用开局即可验证平台探测、语言加载与六语言一致性、配置版本、Vault 经济、区域配置、道具工厂、ActionBar 通道、以及全局与实体两种调度
- 逐项打勾，末尾给出「通过 / 失败 / 警告」统计

**📊 运维**
- 接入 **bStats** 匿名统计（依赖已重定位进插件自己的包，不与其他插件冲突）。插件 ID 为 `0` 时**不上报任何数据**，注册后填入真实 ID 即生效
- 启动时异步检查 Modrinth 版本，有新版本提示在线管理员（`update-check.enabled` 可关闭）
- 新增 CI：push 自动校验语言文件 + 构建，打 `v*` tag 自动建 Release

**🐛 其它修复**
- 选区粒子任务此前**永不取消**，每 5 tick 空转到服务器关闭
- 控制台现在可以执行 `/gost start` 等管理员命令
- `/gostadmin load <名称> enable` 在区域已启用时不再报含糊的「已经启用或不存在」
- 各子命令的重复文案（如「区域 X 不存在」出现 4 次）收敛为带参数的单一 key

**📦 配置**
- config version **34**
- 新增 `area.random-each-game`、`death-state.mode`、`update-check.enabled`

**⚠️ 升级提示**
- 旧配置自动备份为 `config_old_vXX.yml` 并迁移，原有设置不受影响
- **道具名称与队伍名仍为中文**：它们同时被用作道具身份标识（`displayName.contains("中文名")`），直接翻译会让监听器全部失配，需先做 PersistentDataContainer 改造
- 手改过 `plugins/Gost/lang/*.yml` 的话无需删除重建，新 key 会自动追加到文件末尾

</details>

#### ✨ 2.4.0 New & Fixed

This release adds **no new gameplay**. It closes the gap between what was claimed and what actually worked: reproducible builds, Folia support, ActionBar compatibility, and six languages that really take effect.

**🔧 Reproducible build**
- Fixed `build.gradle`: sources live in `main/` and `resources/`, not Gradle's default `src/main/java`; added the missing Vault dependency
- `gradle build` **could not produce a usable jar** before, so releases relied on manual commands. One command now reproduces the build, and the resulting class list matches the released 2.3.3 jar exactly

**⚙️ Folia compatibility**
- Root cause: `BukkitRunnable.runTask*()` internally calls `Bukkit.getScheduler()` too and throws on Folia — only direct calls had been fixed, so **all 17 sites (7 core loops in `GameManager`) were broken**
- Added `ServerCompat.runLaterForEntity` / `runTimerForEntity`, reflecting Folia's `EntityScheduler` (`runDelayed` / `runAtFixedRate`)
- Mother emerald glow, spectator boundary checks, guardian and hunter effect loops, firework detonation and ActionBar now **bind to the owning entity's region thread** instead of the global scheduler
- Fixed platform detection: no longer relies on `getGlobalRegionScheduler` (Paper-family servers added it too, which misdetected Purpur 26.x as Folia); now requires the `RegionizedServer` class

**💬 ActionBar fix (was broken on Spigot)**
- Code called Paper-only `player.sendActionBar(String)`; Spigot throws `NoSuchMethodError`, and the fallback used `catch (Exception)` — **which cannot catch an `Error`**
- Now **probes and caches a channel**: Adventure → BungeeCord chat channel → chat fallback. This exists because the Bungee chat API is being removed on newer Paper
- Impact: the 2.3.3 spectator respawn countdown did not actually work on Spigot

**🌐 Localization (six languages now effective)**
- Keys grew **66 → 599**; **no hardcoded Chinese remains in player-facing text**
- `/gost lang <locale>` and `/gost lang reset`, **effective immediately without a reload**
- Four-level fallback for missing keys: player locale → server default → builtin Chinese → the key itself
- **Automatic file completion**: new keys are appended to `plugins/Gost/lang/*.yml`, preserving your translations and comments — append-only, never rewritten
- Fixed an unescaped quote in `en_US.yml` that broke YAML parsing (English used to fall back to 66 legacy keys)
- New `tools/check_lang.py`: key-set equality, line-level syntax validation and code coverage, wired into CI

**🎲 Random map**
- Every game randomly picks from **enabled areas** (`area.random-each-game`, on by default)
- An area chosen with `/gostadmin load <name>` applies to the **next game only**, then randomization resumes
- Previously randomization happened once and the same map was reused forever

**💀 Configurable death state (invisibility restored as default)**
- New `death-state.mode`: `invisible` (**default**) or `spectator`
- Invisible: stay in survival, full health, invisibility applied, movement unrestricted — the pre-2.3.3 behaviour
- Spectator: enter spectator mode with boundary checks — the 2.3.3 behaviour
- Both register as "eliminated", so they cannot be infected or targeted, and share the same respawn flow
- If a player really dies and respawns, invisibility is re-applied; spectator stays spectator

**🩺 Runtime self test**
- New `/gostadmin selftest`: verifies platform detection, language loading and cross-language consistency, config version, Vault economy, area setup, item factories, the ActionBar channel, and both scheduler paths — without starting a game
- Prints per-item results plus a passed / failed / warned summary

**📊 Operations**
- **bStats** anonymous metrics (relocated into the plugin's own package so it cannot clash with other plugins). While the ID is `0`, **no data is sent**; set a real ID to enable
- Async Modrinth version check on startup, notifying online admins (`update-check.enabled` to disable)
- CI: validate language files + build on push, automatic Release on `v*` tags

**🐛 Other fixes**
- The selection particle task **never cancelled itself**, spinning every 5 ticks until server shutdown
- The console can now run admin commands such as `/gost start`
- `load <name> enable` no longer reports the vague "already enabled or does not exist"
- Duplicated subcommand messages (e.g. "area X does not exist" appeared 4 times) collapsed into single parameterized keys

**📦 Config**
- config version **34**
- New: `area.random-each-game`, `death-state.mode`, `update-check.enabled`

**⚠️ Upgrade notes**
- The old config is backed up as `config_old_vXX.yml` and migrated automatically; existing settings are untouched
- **Item names and team names remain Chinese**: they double as item identity (`displayName.contains("name")`), so translating them would break every listener. A PersistentDataContainer refactor is required first
- Hand-edited `plugins/Gost/lang/*.yml` files need no regeneration — new keys are appended automatically

---

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
