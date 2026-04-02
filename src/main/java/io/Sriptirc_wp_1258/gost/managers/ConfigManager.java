package io.Sriptirc_wp_1258.gost.managers;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    
    private final Gost plugin;
    private FileConfiguration config;
    
    public ConfigManager(Gost plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    private void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        
        // 设置默认值
        config.addDefault("ScriptIrc-config-version", 9);
        config.addDefault("game.duration", 420); // 7分钟，单位秒
        config.addDefault("game.preparation-time", 20); // 准备时间，单位秒
        config.addDefault("game.queue-time", 60); // 队列等待时间，单位秒
        config.addDefault("game.min-players", 2); // 最小玩家数
        config.addDefault("game.max-players", 16); // 最大玩家数
        config.addDefault("game.max-games", 1); // 最大同时游戏数
        config.addDefault("game.match-queue-time", 30); // 匹配队列时间，队列满员后等待多久开始游戏
        
        // 经济设置
        config.addDefault("economy.entry-fee", 100.0); // 入场费
        config.addDefault("economy.server-bonus", 5000.0); // 服务器奖金
        
        // 道具设置
        config.addDefault("items.adrenaline.duration", 10); // 肾上腺素持续时间，单位秒
        config.addDefault("items.adrenaline.speed-level", 2); // 肾上腺素速度等级
        config.addDefault("items.frenzy.duration", 10); // 狂暴持续时间，单位秒
        config.addDefault("items.frenzy.speed-level", 2); // 狂暴速度等级
        config.addDefault("items.ice-ball.slow-duration", 4); // 凝冰球减速持续时间，单位秒
        config.addDefault("items.ice-ball.slow-level", 4); // 凝冰球减速等级
        config.addDefault("items.soul-control.freeze-duration", 6); // 控魂术冻结持续时间，单位秒
        config.addDefault("items.soul-control.cooldown", 18); // 控魂术冷却时间，单位秒
        config.addDefault("items.teleport-pearl.cooldown", 20); // 传送珍珠冷却时间，单位秒
        config.addDefault("items.stinky-steak.speed-duration", 14); // 臭牛排速度效果持续时间，单位秒
        config.addDefault("items.stinky-steak.speed-level", 1); // 臭牛排速度效果等级（1=速度II）
        config.addDefault("items.stinky-steak.glowing-duration", 10); // 臭牛排发光效果持续时间，单位秒
        config.addDefault("items.stinky-steak.cooldown", 30); // 臭牛排冷却时间，单位秒
        config.addDefault("items.soul-detector.duration", 25); // 灵魂探测器暴露持续时间，单位秒
        config.addDefault("items.soul-detector.cooldown", 35); // 灵魂探测器冷却时间，单位秒
        config.addDefault("items.second-chance.cooldown", 180); // 一次机会冷却时间，单位秒
        config.addDefault("items.second-chance.human-speed-duration", 10); // 人类玩家速度效果持续时间，单位秒
        config.addDefault("items.second-chance.human-speed-level", 2); // 人类玩家速度效果等级
        config.addDefault("items.second-chance.human-glowing-duration", 10); // 人类玩家高亮效果持续时间，单位秒
        config.addDefault("items.second-chance.ghost-slow-duration", 7); // 鬼玩家缓慢效果持续时间，单位秒
        config.addDefault("items.second-chance.ghost-slow-level", 1); // 鬼玩家缓慢效果等级
        
        // 效果设置
        config.addDefault("effects.mother-ghost-blindness-duration", 20); // 母体失明持续时间，单位秒
        config.addDefault("effects.ghost-immobilize-duration", 20); // 母体鬼固定时间，单位秒
        config.addDefault("effects.ghost-sense-duration", 5); // 幽灵感知高亮持续时间，单位秒
        config.addDefault("effects.infection-lightning", true); // 感染时是否显示闪电
        config.addDefault("effects.infection-sound", true); // 感染时是否播放音效
        config.addDefault("effects.minute-glowing.enabled", true); // 是否启用每分钟高亮效果
        config.addDefault("effects.minute-glowing.duration", 5); // 高亮持续时间，单位秒
        config.addDefault("effects.minute-glowing.interval", 60); // 触发间隔时间，单位秒
        
        // 黑暗效果设置
        config.addDefault("dark-effect.enabled", false); // 是否启用黑暗效果
        config.addDefault("dark-effect.duration", 999999); // 黑暗效果持续时间（秒）
        config.addDefault("dark-effect.amplifier", 0); // 黑暗效果等级
        
        // 心跳声设置
        config.addDefault("heartbeat.enabled", true); // 是否启用心跳声
        config.addDefault("heartbeat.interval", 10); // 心跳声播放间隔（秒）
        
        // 转化功能设置
        config.addDefault("conversion.enabled", false); // 是否启用转化功能
        config.addDefault("conversion.activate-time", 120); // 转化激活时间（游戏剩余时间，秒）
        config.addDefault("conversion.cooldown", 30); // 转化冷却时间（秒）
        config.addDefault("conversion.cost", 15); // 转化消耗宝石数量
        
        // 道具刷新设置
        config.addDefault("item-spawn.enabled", true); // 是否启用道具刷新
        config.addDefault("item-spawn.interval", 60); // 刷新间隔（秒）
        config.addDefault("item-spawn.max-per-refresh", 3); // 每次刷新数量上限
        config.addDefault("item-spawn.max-per-player", 1); // 每位玩家最多获得数量
        config.addDefault("item-spawn.max-item-types-per-player", 6); // 玩家最多拥有的道具种类数量
        
        // 货币发放设置
        config.addDefault("currency.distribution-interval", 25); // 发放间隔（秒）
        config.addDefault("currency.distribution-amount", 1); // 每次发放数量
        config.addDefault("currency.enchanted", true); // 是否带有附魔光效
        
        // 观战系统设置
        config.addDefault("spectator.enabled", true); // 是否启用观战系统
        config.addDefault("spectator.confirmation-required", true); // 是否需要确认观战
        
        // NPC设置
        config.addDefault("npc.enabled", true); // 是否启用NPC系统
        config.addDefault("npc.entity-type", "VILLAGER"); // NPC实体类型
        config.addDefault("npc.name", "&e&lGost商人"); // NPC名称
        
        // 服务器模组设置（预留）
        
        // 区域选择设置
        config.addDefault("area.selection-tool", "MAGMA_CREAM"); // 选区工具物品
        config.addDefault("area.max-areas", 20); // 最大存档区域数量
        config.addDefault("area.auto-teleport", true); // 是否自动传送到区域
        
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    
    // 获取配置值的方法
    public int getConfigVersion() {
        return config.getInt("ScriptIrc-config-version", 2);
    }
    
    public int getGameDuration() {
        return config.getInt("game.duration", 420);
    }
    
    public int getPreparationTime() {
        return config.getInt("game.preparation-time", 20);
    }
    
    public int getQueueTime() {
        return config.getInt("game.queue-time", 60);
    }
    
    public int getMinPlayers() {
        return config.getInt("game.min-players", 2);
    }
    
    public int getMaxPlayers() {
        return config.getInt("game.max-players", 16);
    }
    
    public int getMaxGames() {
        return config.getInt("game.max-games", 1);
    }
    
    public double getEntryFee() {
        return config.getDouble("economy.entry-fee", 100.0);
    }
    
    public double getServerBonus() {
        return config.getDouble("economy.server-bonus", 5000.0);
    }
    
    public int getAdrenalineDuration() {
        return config.getInt("items.adrenaline.duration", 10);
    }
    
    public int getAdrenalineSpeedLevel() {
        return config.getInt("items.adrenaline.speed-level", 2);
    }
    
    public int getFrenzyDuration() {
        return config.getInt("items.frenzy.duration", 10);
    }
    
    public int getFrenzySpeedLevel() {
        return config.getInt("items.frenzy.speed-level", 2);
    }
    
    public int getIceBallSlowDuration() {
        return config.getInt("items.ice-ball.slow-duration", 4);
    }
    
    public int getIceBallSlowLevel() {
        return config.getInt("items.ice-ball.slow-level", 4);
    }
    
    public int getSoulControlFreezeDuration() {
        return config.getInt("items.soul-control.freeze-duration", 6);
    }
    
    public int getSoulControlCooldown() {
        return config.getInt("items.soul-control.cooldown", 18);
    }
    
    public int getMotherGhostBlindnessDuration() {
        return config.getInt("effects.mother-ghost-blindness-duration", 20);
    }
    
    public int getGhostSenseDuration() {
        return config.getInt("effects.ghost-sense-duration", 5);
    }
    
    public boolean isInfectionLightningEnabled() {
        return config.getBoolean("effects.infection-lightning", true);
    }
    
    public boolean isInfectionSoundEnabled() {
        return config.getBoolean("effects.infection-sound", true);
    }
    
    public boolean isAutoTeleportEnabled() {
        return config.getBoolean("area.auto-teleport", true);
    }
    
    public String getSelectionTool() {
        return config.getString("area.selection-tool", "MAGMA_CREAM");
    }
    
    public int getMaxAreas() {
        return config.getInt("area.max-areas", 20);
    }
    
    // 新功能配置方法
    
    public int getGhostImmobilizeDuration() {
        return config.getInt("effects.ghost-immobilize-duration", 20);
    }
    
    public boolean isConversionEnabled() {
        return config.getBoolean("conversion.enabled", false);
    }
    
    public int getConversionActivateTime() {
        return config.getInt("conversion.activate-time", 120);
    }
    
    public int getConversionCooldown() {
        return config.getInt("conversion.cooldown", 30);
    }
    
    public int getConversionCost() {
        return config.getInt("conversion.cost", 15);
    }
    
    public boolean isItemSpawnEnabled() {
        return config.getBoolean("item-spawn.enabled", true);
    }
    
    public int getItemSpawnInterval() {
        return config.getInt("item-spawn.interval", 60);
    }
    
    public int getItemSpawnMaxPerRefresh() {
        return config.getInt("item-spawn.max-per-refresh", 3);
    }
    
    public int getItemSpawnMaxPerPlayer() {
        return config.getInt("item-spawn.max-per-player", 1);
    }
    
    public int getCurrencyDistributionInterval() {
        return config.getInt("currency.distribution-interval", 25);
    }
    
    public int getCurrencyDistributionAmount() {
        return config.getInt("currency.distribution-amount", 1);
    }
    
    public boolean getCurrencyEnchanted() {
        return config.getBoolean("currency.enchanted", true);
    }
    
    public boolean isSpectatorEnabled() {
        return config.getBoolean("spectator.enabled", true);
    }
    
    public boolean isSpectatorConfirmationRequired() {
        return config.getBoolean("spectator.confirmation-required", true);
    }
    
    public boolean isNpcEnabled() {
        return config.getBoolean("npc.enabled", true);
    }
    
    public String getNpcEntityType() {
        return config.getString("npc.entity-type", "VILLAGER");
    }
    
    public String getNpcName() {
        return config.getString("npc.name", "&e&lGost商人");
    }
    
    public int getMatchQueueTime() {
        return config.getInt("game.match-queue-time", 30);
    }
    
    public int getSoulDetectorDuration() {
        return config.getInt("items.soul-detector.duration", 25);
    }
    
    public int getSoulDetectorCooldown() {
        return config.getInt("items.soul-detector.cooldown", 30);
    }
    
    public int getTeleportPearlCooldown() {
        return config.getInt("items.teleport-pearl.cooldown", 10);
    }
    
    public int getStinkySteakSpeedDuration() {
        return config.getInt("items.stinky-steak.speed-duration", 14);
    }
    
    public int getStinkySteakSpeedLevel() {
        return config.getInt("items.stinky-steak.speed-level", 1);
    }
    
    public int getStinkySteakGlowingDuration() {
        return config.getInt("items.stinky-steak.glowing-duration", 10);
    }
    
    public int getStinkySteakCooldown() {
        return config.getInt("items.stinky-steak.cooldown", 30);
    }
    
    public int getSecondChanceCooldown() {
        return config.getInt("items.second-chance.cooldown", 60);
    }
    
    public int getSecondChanceHumanSpeedDuration() {
        return config.getInt("items.second-chance.human-speed-duration", 10);
    }
    
    public int getSecondChanceHumanSpeedLevel() {
        return config.getInt("items.second-chance.human-speed-level", 2);
    }
    
    public int getSecondChanceHumanGlowingDuration() {
        return config.getInt("items.second-chance.human-glowing-duration", 10);
    }
    
    public int getSecondChanceGhostSlowDuration() {
        return config.getInt("items.second-chance.ghost-slow-duration", 7);
    }
    
    public int getSecondChanceGhostSlowLevel() {
        return config.getInt("items.second-chance.ghost-slow-level", 1);
    }
    
    public int getMaxItemTypesPerPlayer() {
        return config.getInt("item-spawn.max-item-types-per-player", 6);
    }
    
    public boolean isMinuteGlowingEnabled() {
        return config.getBoolean("effects.minute-glowing.enabled", true);
    }
    
    public int getMinuteGlowingDuration() {
        return config.getInt("effects.minute-glowing.duration", 5);
    }
    
    public int getMinuteGlowingInterval() {
        return config.getInt("effects.minute-glowing.interval", 60);
    }
    
    // 黑暗效果配置
    public boolean isDarkEffectEnabled() {
        return config.getBoolean("dark-effect.enabled", false);
    }
    
    public int getDarkEffectDuration() {
        return config.getInt("dark-effect.duration", 999999);
    }
    
    public int getDarkEffectAmplifier() {
        return config.getInt("dark-effect.amplifier", 0);
    }
    
    // 设置黑暗效果开关
    public void setDarkEffectEnabled(boolean enabled) {
        config.set("dark-effect.enabled", enabled);
        plugin.saveConfig();
    }
    
    // 心跳声配置
    public boolean isHeartbeatEnabled() {
        return config.getBoolean("heartbeat.enabled", true);
    }
    
    public int getHeartbeatInterval() {
        return config.getInt("heartbeat.interval", 10);
    }
    
    // 设置心跳声开关
    public void setHeartbeatEnabled(boolean enabled) {
        config.set("heartbeat.enabled", enabled);
        plugin.saveConfig();
    }
}