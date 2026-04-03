package io.Sriptirc_wp_1258.gost.managers;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class BotManager {
    
    private final Gost plugin;
    private final Map<UUID, BotData> activeBots = new HashMap<>();
    private final List<String> botNames = Arrays.asList(
        "Bot_Alpha", "Bot_Bravo", "Bot_Charlie", "Bot_Delta", "Bot_Echo",
        "Bot_Foxtrot", "Bot_Golf", "Bot_Hotel", "Bot_India", "Bot_Juliett",
        "Bot_Kilo", "Bot_Lima", "Bot_Mike", "Bot_November", "Bot_Oscar",
        "Bot_Papa", "Bot_Quebec", "Bot_Romeo", "Bot_Sierra", "Bot_Tango"
    );
    private final Random random = new Random();
    
    // 假人状态
    private enum BotState {
        IDLE,      // 闲置
        QUEUED,    // 在队列中
        IN_GAME,   // 在游戏中
        MOVING     // 移动中
    }
    
    // 假人数据类
    private static class BotData {
        String name;
        UUID uuid;
        BotState state;
        Location spawnLocation;
        BukkitTask movementTask;
        
        BotData(String name, UUID uuid) {
            this.name = name;
            this.uuid = uuid;
            this.state = BotState.IDLE;
        }
    }
    
    public BotManager(Gost plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 添加假人到队列
     * @param count 要添加的假人数量
     * @return 成功添加的假人数量
     */
    public int addBotsToQueue(int count) {
        int added = 0;
        List<String> availableNames = new ArrayList<>(botNames);
        
        // 移除已使用的名字
        for (BotData bot : activeBots.values()) {
            availableNames.remove(bot.name);
        }
        
        // 计算可以添加的最大数量
        int maxToAdd = Math.min(count, availableNames.size());
        maxToAdd = Math.min(maxToAdd, plugin.getConfigManager().getMaxPlayers() - plugin.getGameManager().getQueueSize());
        
        if (maxToAdd <= 0) {
            plugin.getLogger().warning(plugin.getLanguageManager().getMessage("bot.queue_full"));
            return 0;
        }
        
        for (int i = 0; i < maxToAdd; i++) {
            String botName = availableNames.get(i);
            UUID botUuid = UUID.randomUUID();
            
            BotData botData = new BotData(botName, botUuid);
            activeBots.put(botUuid, botData);
            
            // 模拟加入队列
            simulateJoinQueue(botData);
            added++;
            
            try {
                Thread.sleep(100); // 稍微延迟一下，模拟真实玩家
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        plugin.getLogger().info(plugin.getLanguageManager().getMessage("bot.added_to_queue", added));
        return added;
    }
    
    /**
     * 模拟假人加入队列
     */
    private void simulateJoinQueue(BotData botData) {
        botData.state = BotState.QUEUED;
        
        // 广播假人加入队列
        int currentPlayers = plugin.getGameManager().getQueueSize();
        int maxPlayers = plugin.getConfigManager().getMaxPlayers();
        plugin.getLanguageManager().broadcastMessage("bot.join_broadcast", botData.name, currentPlayers, maxPlayers);
        
        // 记录日志
        plugin.getLogger().info(plugin.getLanguageManager().getMessage("bot.bot_queued", botData.name));
    }
    
    /**
     * 移除队列中的假人
     * @param count 要移除的假人数量
     * @return 成功移除的假人数量
     */
    public int removeBotsFromQueue(int count) {
        int removed = 0;
        List<BotData> queuedBots = new ArrayList<>();
        
        // 收集在队列中的假人
        for (BotData bot : activeBots.values()) {
            if (bot.state == BotState.QUEUED) {
                queuedBots.add(bot);
            }
        }
        
        // 随机选择要移除的假人
        Collections.shuffle(queuedBots);
        int toRemove = Math.min(count, queuedBots.size());
        
        for (int i = 0; i < toRemove; i++) {
            BotData bot = queuedBots.get(i);
            
            // 模拟离开队列
            simulateLeaveQueue(bot);
            activeBots.remove(bot.uuid);
            removed++;
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        plugin.getLogger().info(plugin.getLanguageManager().getMessage("bot.removed_from_queue", removed));
        return removed;
    }
    
    /**
     * 模拟假人离开队列
     */
    private void simulateLeaveQueue(BotData botData) {
        // 广播假人离开队列
        int currentPlayers = plugin.getGameManager().getQueueSize();
        int maxPlayers = plugin.getConfigManager().getMaxPlayers();
        plugin.getLanguageManager().broadcastMessage("bot.leave_broadcast", botData.name, currentPlayers, maxPlayers);
        
        // 记录日志
        plugin.getLogger().info(plugin.getLanguageManager().getMessage("bot.bot_left_queue", botData.name));
    }
    
    /**
     * 清除所有假人
     */
    public void clearAllBots() {
        int count = activeBots.size();
        
        // 广播所有假人离开
        for (BotData bot : activeBots.values()) {
            if (bot.movementTask != null && !bot.movementTask.isCancelled()) {
                bot.movementTask.cancel();
            }
            
            if (bot.state == BotState.QUEUED) {
                plugin.getLanguageManager().broadcastMessage("bot.leave_simple", bot.name);
            }
        }
        
        activeBots.clear();
        plugin.getLogger().info(plugin.getLanguageManager().getMessage("bot.cleared_all", count));
    }
    
    /**
     * 获取活跃假人数量
     */
    public int getActiveBotCount() {
        return activeBots.size();
    }
    
    /**
     * 获取队列中的假人数量
     */
    public int getQueuedBotCount() {
        int count = 0;
        for (BotData bot : activeBots.values()) {
            if (bot.state == BotState.QUEUED) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * 检查玩家是否是假人
     */
    public boolean isBot(UUID uuid) {
        return activeBots.containsKey(uuid);
    }
    
    /**
     * 检查玩家是否是假人（通过名字）
     */
    public boolean isBot(String playerName) {
        for (BotData bot : activeBots.values()) {
            if (bot.name.equals(playerName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取假人信息
     */
    public String getBotInfo() {
        int total = activeBots.size();
        int queued = getQueuedBotCount();
        int inGame = total - queued;
        
        StringBuilder info = new StringBuilder();
        info.append(plugin.getLanguageManager().getMessage("bot.status_header")).append("\n");
        info.append(plugin.getLanguageManager().getMessage("bot.status_total", total)).append("\n");
        info.append(plugin.getLanguageManager().getMessage("bot.status_queued", queued)).append("\n");
        info.append(plugin.getLanguageManager().getMessage("bot.status_in_game", inGame)).append("\n");
        
        if (total > 0) {
            info.append(plugin.getLanguageManager().getMessage("bot.status_active"));
            List<String> botNames = new ArrayList<>();
            for (BotData bot : activeBots.values()) {
                String stateSymbol = getStateSymbol(bot.state);
                botNames.add(bot.name + stateSymbol);
            }
            info.append(String.join(", ", botNames));
        }
        
        return info.toString();
    }
    
    /**
     * 获取状态符号
     */
    private String getStateSymbol(BotState state) {
        switch (state) {
            case QUEUED: return plugin.getLanguageManager().getMessage("bot.state_queued");
            case IN_GAME: return plugin.getLanguageManager().getMessage("bot.state_in_game");
            case MOVING: return plugin.getLanguageManager().getMessage("bot.state_moving");
            default: return plugin.getLanguageManager().getMessage("bot.state_idle");
        }
    }
    
    /**
     * 更新假人状态到游戏中
     */
    public void updateBotToGame(UUID botUuid) {
        BotData bot = activeBots.get(botUuid);
        if (bot != null) {
            bot.state = BotState.IN_GAME;
            plugin.getLogger().info(plugin.getLanguageManager().getMessage("bot.bot_joined_game", bot.name));
        }
    }
    
    /**
     * 移除游戏中的假人
     */
    public void removeBotFromGame(UUID botUuid) {
        BotData bot = activeBots.get(botUuid);
        if (bot != null) {
            if (bot.movementTask != null && !bot.movementTask.isCancelled()) {
                bot.movementTask.cancel();
            }
            activeBots.remove(botUuid);
            plugin.getLogger().info(plugin.getLanguageManager().getMessage("bot.bot_left_game", bot.name));
        }
    }
}