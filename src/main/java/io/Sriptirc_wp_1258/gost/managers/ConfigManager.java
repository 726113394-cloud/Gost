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
        config.addDefault("ScriptIrc-config-version", 2);
        config.addDefault("game.duration", 420); // 7分钟，单位秒
        config.addDefault("game.preparation-time", 20); // 准备时间，单位秒
        config.addDefault("game.queue-time", 60); // 队列等待时间，单位秒
        config.addDefault("game.min-players", 2); // 最小玩家数
        config.addDefault("game.max-players", 16); // 最大玩家数
        config.addDefault("game.max-games", 1); // 最大同时游戏数
        
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
        
        // 效果设置
        config.addDefault("effects.mother-ghost-blindness-duration", 20); // 母体失明持续时间，单位秒
        config.addDefault("effects.ghost-sense-duration", 5); // 幽灵感知高亮持续时间，单位秒
        config.addDefault("effects.infection-lightning", true); // 感染时是否显示闪电
        config.addDefault("effects.infection-sound", true); // 感染时是否播放音效
        
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
}