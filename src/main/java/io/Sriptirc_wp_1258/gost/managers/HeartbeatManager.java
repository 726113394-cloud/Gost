package io.Sriptirc_wp_1258.gost.managers;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

/**
 * 心跳声管理器
 * 在游戏过程中为人类玩家循环播放监守者出现时的心跳声
 */
public class HeartbeatManager {
    
    private final Gost plugin;
    private BukkitTask heartbeatTask;
    private boolean heartbeatEnabled = false;
    
    public HeartbeatManager(Gost plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 启动心跳声任务
     */
    public void startHeartbeat() {
        if (heartbeatTask != null) {
            heartbeatTask.cancel();
        }
        
        // 检查配置是否启用心跳声
        if (!plugin.getConfigManager().isHeartbeatEnabled()) {
            return;
        }
        
        heartbeatEnabled = true;
        int interval = plugin.getConfigManager().getHeartbeatInterval();
        
        heartbeatTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!heartbeatEnabled) {
                return;
            }
            
            // 只有在游戏进行中才播放心跳声
            if (plugin.getGameManager().getGameState() != GameManager.GameState.RUNNING) {
                return;
            }
            
            // 给所有人类玩家播放心跳声
            for (UUID playerId : plugin.getPlayerManager().getAllPlayers()) {
                if (plugin.getPlayerManager().isHuman(playerId)) {
                    Player player = Bukkit.getPlayer(playerId);
                    if (player != null && player.isOnline()) {
                        playHeartbeatSound(player);
                    }
                }
            }
        }, 0L, interval * 20L); // 转换为ticks
    }
    
    /**
     * 停止心跳声任务
     */
    public void stopHeartbeat() {
        heartbeatEnabled = false;
        if (heartbeatTask != null) {
            heartbeatTask.cancel();
            heartbeatTask = null;
        }
    }
    
    /**
     * 给玩家播放心跳声
     * @param player 玩家
     */
    private void playHeartbeatSound(Player player) {
        // 使用监守者出现时的心跳声
        // 在1.20.x版本中，监守者的心跳声是ENTITY_WARDEN_HEARTBEAT
        player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 1.0f, 1.0f);
    }
    
    /**
     * 切换心跳声开关
     * @param enabled 是否启用
     */
    public void toggleHeartbeat(boolean enabled) {
        plugin.getConfigManager().setHeartbeatEnabled(enabled);
        
        if (enabled) {
            startHeartbeat();
            plugin.getLanguageManager().broadcastMessage("heartbeat.enabled");
        } else {
            stopHeartbeat();
            plugin.getLanguageManager().broadcastMessage("heartbeat.disabled");
        }
    }
    
    /**
     * 获取心跳声状态
     * @return 是否启用
     */
    public boolean isHeartbeatEnabled() {
        return heartbeatEnabled;
    }
    
    /**
     * 重新加载心跳声配置
     */
    public void reload() {
        stopHeartbeat();
        if (plugin.getConfigManager().isHeartbeatEnabled()) {
            startHeartbeat();
        }
    }
}