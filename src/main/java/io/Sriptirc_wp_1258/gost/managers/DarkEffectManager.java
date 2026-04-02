package io.Sriptirc_wp_1258.gost.managers;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class DarkEffectManager {
    
    private final Gost plugin;
    
    public DarkEffectManager(Gost plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 给所有游戏中的玩家应用黑暗效果
     */
    public void applyDarkEffectToAllPlayers() {
        if (!plugin.getConfigManager().isDarkEffectEnabled()) {
            return;
        }
        
        int duration = plugin.getConfigManager().getDarkEffectDuration();
        int amplifier = plugin.getConfigManager().getDarkEffectAmplifier();
        
        // 检查是否在准备阶段
        boolean isPreparationPhase = plugin.getGameManager().isPreparationPhase();
        
        for (UUID playerId : plugin.getPlayerManager().getAllPlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                // 如果在准备阶段，只给鬼应用黑暗效果
                if (isPreparationPhase) {
                    if (plugin.getPlayerManager().isGhost(playerId)) {
                        applyDarkEffect(player, duration, amplifier);
                    }
                } else {
                    // 游戏阶段，给所有玩家应用黑暗效果
                    applyDarkEffect(player, duration, amplifier);
                }
            }
        }
    }
    
    /**
     * 给指定玩家应用黑暗效果
     * @param player 玩家
     */
    public void applyDarkEffect(Player player) {
        if (!plugin.getConfigManager().isDarkEffectEnabled()) {
            return;
        }
        
        int duration = plugin.getConfigManager().getDarkEffectDuration();
        int amplifier = plugin.getConfigManager().getDarkEffectAmplifier();
        
        // 检查是否在准备阶段
        boolean isPreparationPhase = plugin.getGameManager().isPreparationPhase();
        
        // 如果在准备阶段，只给鬼应用黑暗效果
        if (isPreparationPhase) {
            UUID playerId = player.getUniqueId();
            if (plugin.getPlayerManager().isGhost(playerId)) {
                applyDarkEffect(player, duration, amplifier);
            }
        } else {
            // 游戏阶段，给所有玩家应用黑暗效果
            applyDarkEffect(player, duration, amplifier);
        }
    }
    
    /**
     * 给指定玩家应用黑暗效果
     * @param player 玩家
     * @param duration 持续时间（秒）
     * @param amplifier 效果等级
     */
    private void applyDarkEffect(Player player, int duration, int amplifier) {
        // 黑暗效果使用BLINDNESS（失明）药水效果
        PotionEffect darkEffect = new PotionEffect(
            PotionEffectType.BLINDNESS,
            duration * 20, // 转换为tick（1秒=20tick）
            amplifier,
            true, // 环境效果
            false, // 不显示粒子
            false // 不显示图标
        );
        
        player.addPotionEffect(darkEffect);
    }
    
    /**
     * 移除所有玩家的黑暗效果
     */
    public void removeDarkEffectFromAllPlayers() {
        for (UUID playerId : plugin.getPlayerManager().getAllPlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                removeDarkEffect(player);
            }
        }
    }
    
    /**
     * 移除指定玩家的黑暗效果
     * @param player 玩家
     */
    public void removeDarkEffect(Player player) {
        player.removePotionEffect(PotionEffectType.BLINDNESS);
    }
    
    /**
     * 切换黑暗效果开关
     * @param enabled 是否启用
     */
    public void toggleDarkEffect(boolean enabled) {
        plugin.getConfigManager().setDarkEffectEnabled(enabled);
        
        if (enabled) {
            // 如果启用，给所有玩家应用黑暗效果
            applyDarkEffectToAllPlayers();
            Bukkit.broadcastMessage("§8[§cGost§8] §7黑暗效果已启用！所有玩家获得失明效果。");
        } else {
            // 如果禁用，移除所有玩家的黑暗效果
            removeDarkEffectFromAllPlayers();
            Bukkit.broadcastMessage("§8[§cGost§8] §7黑暗效果已禁用！所有玩家的失明效果已移除。");
        }
    }
    
    /**
     * 检查黑暗效果是否启用
     * @return 是否启用
     */
    public boolean isDarkEffectEnabled() {
        return plugin.getConfigManager().isDarkEffectEnabled();
    }
}