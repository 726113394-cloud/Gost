package io.Sriptirc_wp_1258.gost.managers;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class PlayerManager {
    
    public enum PlayerRole {
        HUMAN,          // 人类
        GHOST_MOTHER,   // 母体鬼
        GHOST_NORMAL    // 普通鬼
    }
    
    private final Gost plugin;
    
    // 玩家数据存储
    private final Map<UUID, PlayerRole> playerRoles = new HashMap<>();
    private final Map<UUID, PlayerState> savedStates = new HashMap<>();
    private final Map<UUID, Integer> infectionCounts = new HashMap<>();
    private final Map<UUID, Long> survivalTimes = new HashMap<>();
    private final Map<UUID, Long> roleStartTimes = new HashMap<>();
    
    public PlayerManager(Gost plugin) {
        this.plugin = plugin;
    }
    
    // 玩家加入游戏
    public boolean joinGame(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (playerRoles.containsKey(playerId)) {
            return false; // 已经在游戏中
        }
        
        // 保存玩家原始状态
        savePlayerState(player);
        
        // 初始化为人类
        setPlayerRole(playerId, PlayerRole.HUMAN);
        
        // 重置游戏内统计
        infectionCounts.put(playerId, 0);
        survivalTimes.put(playerId, 0L);
        roleStartTimes.put(playerId, System.currentTimeMillis());
        
        // 应用游戏状态
        applyGameState(player);
        
        plugin.getLogger().info("玩家 " + player.getName() + " 加入游戏");
        return true;
    }
    
    // 玩家离开游戏
    public boolean leaveGame(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (!playerRoles.containsKey(playerId)) {
            return false; // 不在游戏中
        }
        
        // 恢复玩家原始状态
        restorePlayerState(player);
        
        // 清理数据
        playerRoles.remove(playerId);
        savedStates.remove(playerId);
        infectionCounts.remove(playerId);
        survivalTimes.remove(playerId);
        roleStartTimes.remove(playerId);
        
        // 从队伍中移除
        plugin.getTeamManager().removeFromAllTeams(player);
        
        player.sendMessage("§a你已离开游戏！");
        plugin.getLogger().info("玩家 " + player.getName() + " 离开游戏");
        return true;
    }
    
    // 设置玩家角色
    public void setPlayerRole(UUID playerId, PlayerRole role) {
        Player player = Bukkit.getPlayer(playerId);
        PlayerRole oldRole = playerRoles.get(playerId);
        
        // 更新角色开始时间
        if (oldRole != null) {
            updateSurvivalTime(playerId);
        }
        roleStartTimes.put(playerId, System.currentTimeMillis());
        
        // 更新角色
        playerRoles.put(playerId, role);
        
        if (player != null && player.isOnline()) {
            // 更新队伍
            plugin.getTeamManager().setPlayerTeam(player, role);
            
            // 更新背包
            updatePlayerInventory(player, role);
            
            // 应用角色特定效果
            applyRoleEffects(player, role);
            
            // 发送角色变更消息
            String roleName = plugin.getTeamManager().getTeamName(role);
            ChatColor color = plugin.getTeamManager().getTeamColor(role);
            player.sendMessage(color + "你的角色已变更为: " + roleName);
        }
    }
    
    // 获取玩家角色
    public PlayerRole getPlayerRole(UUID playerId) {
        return playerRoles.getOrDefault(playerId, PlayerRole.HUMAN);
    }
    
    // 检查是否是鬼
    public boolean isGhost(UUID playerId) {
        PlayerRole role = getPlayerRole(playerId);
        return role == PlayerRole.GHOST_MOTHER || role == PlayerRole.GHOST_NORMAL;
    }
    
    // 检查是否是人类
    public boolean isHuman(UUID playerId) {
        return getPlayerRole(playerId) == PlayerRole.HUMAN;
    }
    
    // 感染玩家
    public void infectPlayer(UUID victimId, UUID infectorId) {
        PlayerRole currentRole = getPlayerRole(victimId);
        
        if (currentRole != PlayerRole.HUMAN) {
            plugin.getLogger().info("感染失败：玩家 " + victimId + " 不是人类");
            return; // 只能感染人类
        }
        
        // 更新感染统计
        if (infectorId != null && isGhost(infectorId)) {
            int count = infectionCounts.getOrDefault(infectorId, 0);
            infectionCounts.put(infectorId, count + 1);
        }
        
        // 决定新鬼的类型
        PlayerRole newRole = PlayerRole.GHOST_NORMAL;
        
        // 检查是否还有母体鬼
        boolean hasMother = playerRoles.values().stream()
            .anyMatch(role -> role == PlayerRole.GHOST_MOTHER);
        
        if (!hasMother) {
            newRole = PlayerRole.GHOST_MOTHER;
        }
        
        plugin.getLogger().info("玩家 " + victimId + " 被感染，新角色: " + newRole);
        
        // 设置新角色
        setPlayerRole(victimId, newRole);
        
        // 更新存活时间
        updateSurvivalTime(victimId);
        
        // 发送全局消息
        Player victim = Bukkit.getPlayer(victimId);
        Player infector = infectorId != null ? Bukkit.getPlayer(infectorId) : null;
        
        if (victim != null) {
            String infectorName = infector != null ? infector.getName() : "未知";
            Bukkit.broadcastMessage("§c" + victim.getName() + " 被 " + infectorName + " 感染了！");
            
            // 显示感染效果
            showInfectionEffects(victim);
        }
        
        // 更新游戏统计
        updateGameStats();
    }
    
    // 显示感染效果
    private void showInfectionEffects(Player player) {
        // 闪电效果
        if (plugin.getConfigManager().isInfectionLightningEnabled()) {
            player.getWorld().strikeLightningEffect(player.getLocation());
        }
        
        // 音效
        if (plugin.getConfigManager().isInfectionSoundEnabled()) {
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
        }
        
        // 粒子效果
        player.getWorld().spawnParticle(org.bukkit.Particle.FLAME, player.getLocation(), 50, 0.5, 1, 0.5, 0.1);
    }
    
    // 更新游戏统计
    private void updateGameStats() {
        int humanCount = 0;
        int ghostCount = 0;
        
        for (PlayerRole role : playerRoles.values()) {
            if (role == PlayerRole.HUMAN) {
                humanCount++;
            } else {
                ghostCount++;
            }
        }
        
        // 更新Boss栏
        plugin.getGameManager().updateBossBarStats(humanCount, ghostCount);
        
        // 广播统计
        Bukkit.broadcastMessage("§a剩余人类: " + humanCount + " §c鬼: " + ghostCount);
    }
    
    // 更新存活时间
    public void updateSurvivalTime(UUID playerId) {
        Long startTime = roleStartTimes.get(playerId);
        if (startTime != null) {
            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - startTime;
            
            Long currentSurvival = survivalTimes.getOrDefault(playerId, 0L);
            survivalTimes.put(playerId, currentSurvival + elapsed);
        }
    }
    
    // 获取存活时间（秒）
    public long getSurvivalTime(UUID playerId) {
        updateSurvivalTime(playerId);
        return survivalTimes.getOrDefault(playerId, 0L) / 1000;
    }
    
    // 获取感染次数
    public int getInfectionCount(UUID playerId) {
        return infectionCounts.getOrDefault(playerId, 0);
    }
    
    // 应用游戏状态
    public void applyGameState(Player player) {
        // 设置游戏模式
        player.setGameMode(GameMode.SURVIVAL);
        
        // 设置生命值（4点生命值）
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(4.0);
        player.setHealth(4.0);
        
        // 清除所有药水效果
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        
        // 设置饱食度
        player.setFoodLevel(20);
        player.setSaturation(20);
        
        // 传送到游戏区域
        if (plugin.getConfigManager().isAutoTeleportEnabled()) {
            plugin.getAreaManager().teleportPlayerToArea(player, plugin.getAreaManager().getSelectedArea());
        }
    }
    
    // 应用角色特定效果
    public void applyRoleEffects(Player player, PlayerRole role) {
        switch (role) {
            case GHOST_MOTHER:
                // 母体鬼失明效果
                int blindnessDuration = plugin.getConfigManager().getMotherGhostBlindnessDuration() * 20;
                player.addPotionEffect(new PotionEffect(
                    PotionEffectType.BLINDNESS,
                    blindnessDuration,
                    0,
                    true,
                    true
                ));
                player.sendMessage("§c你是母体鬼！前20秒你将处于失明状态！");
                break;
        }
    }
    
    // 更新玩家背包
    public void updatePlayerInventory(Player player, PlayerRole role) {
        PlayerInventory inventory = player.getInventory();
        
        // 清空背包
        inventory.clear();
        
        // 根据角色给予初始物品
        switch (role) {
            case HUMAN:
                // 人类获得肾上腺素
                inventory.addItem(plugin.getItemManager().getAdrenaline());
                break;
            case GHOST_MOTHER:
                // 母体鬼获得狂暴药水
                inventory.addItem(plugin.getItemManager().getFrenzyPotion());
                break;
            case GHOST_NORMAL:
                // 普通鬼获得狂暴药水
                inventory.addItem(plugin.getItemManager().getFrenzyPotion());
                break;
        }
        
        player.updateInventory();
    }
    
    // 保存玩家原始状态
    public void savePlayerState(Player player) {
        PlayerState state = new PlayerState(player);
        savedStates.put(player.getUniqueId(), state);
    }
    
    // 恢复玩家原始状态
    public void restorePlayerState(Player player) {
        PlayerState state = savedStates.get(player.getUniqueId());
        if (state != null) {
            state.restore(player, plugin);
            savedStates.remove(player.getUniqueId());
        }
        
        // 清除所有药水效果
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        
        // 恢复最大血量
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
        player.setHealth(20.0);
        
        // 从队伍中移除
        plugin.getTeamManager().removeFromAllTeams(player);
    }
    
    // 获取所有游戏中的玩家
    public List<UUID> getAllPlayers() {
        return new ArrayList<>(playerRoles.keySet());
    }
    
    // 获取所有人类玩家
    public List<UUID> getHumanPlayers() {
        List<UUID> humans = new ArrayList<>();
        for (Map.Entry<UUID, PlayerRole> entry : playerRoles.entrySet()) {
            if (entry.getValue() == PlayerRole.HUMAN) {
                humans.add(entry.getKey());
            }
        }
        return humans;
    }
    
    // 获取所有鬼玩家
    public List<UUID> getGhostPlayers() {
        List<UUID> ghosts = new ArrayList<>();
        for (Map.Entry<UUID, PlayerRole> entry : playerRoles.entrySet()) {
            if (entry.getValue() == PlayerRole.GHOST_MOTHER || entry.getValue() == PlayerRole.GHOST_NORMAL) {
                ghosts.add(entry.getKey());
            }
        }
        return ghosts;
    }
    
    // 清理所有玩家数据
    public void cleanup() {
        // 首先恢复所有在线玩家的状态
        for (UUID playerId : new ArrayList<>(playerRoles.keySet())) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                restorePlayerState(player);
                player.sendMessage(ChatColor.RED + "游戏已结束，你的状态已恢复！");
            }
        }
        
        // 清理所有数据
        playerRoles.clear();
        savedStates.clear();
        infectionCounts.clear();
        survivalTimes.clear();
        roleStartTimes.clear();
        
        // 清理玩家数据完成
        plugin.getLogger().info("清理玩家数据完成");
    }
    
    // 玩家状态存储类
    private static class PlayerState {
        private final GameMode gameMode;
        private final Location location;
        private final ItemStack[] inventory;
        private final ItemStack[] armor;
        private final double health;
        private final int foodLevel;
        private final float saturation;
        private final int level;
        private final float exp;
        private final boolean allowFlight;
        private final boolean flying;
        
        public PlayerState(Player player) {
            this.gameMode = player.getGameMode();
            this.location = player.getLocation();
            this.inventory = player.getInventory().getContents();
            this.armor = player.getInventory().getArmorContents();
            // 安全地保存生命值，考虑当前服务器的限制
            double currentHealth = player.getHealth();
            double maxHealth = player.getMaxHealth();
            this.health = Math.min(currentHealth, maxHealth);
            
            this.foodLevel = player.getFoodLevel();
            this.saturation = player.getSaturation();
            this.level = player.getLevel();
            this.exp = player.getExp();
            this.allowFlight = player.getAllowFlight();
            this.flying = player.isFlying();
        }
        
        public void restore(Player player, Gost plugin) {
            player.setGameMode(gameMode);
            player.teleport(location);
            player.getInventory().setContents(inventory);
            player.getInventory().setArmorContents(armor);
            
            // 安全地设置生命值
            try {
                double maxHealth = player.getMaxHealth();
                double safeHealth = Math.min(health, maxHealth);
                player.setHealth(safeHealth);
            } catch (IllegalArgumentException e) {
                // 如果设置失败，使用默认值
                plugin.getLogger().warning("无法设置玩家 " + player.getName() + " 的生命值: " + e.getMessage());
                player.setHealth(player.getMaxHealth());
            }
            
            player.setFoodLevel(foodLevel);
            player.setSaturation(saturation);
            player.setLevel(level);
            player.setExp(exp);
            player.setAllowFlight(allowFlight);
            player.setFlying(flying);
        }
    }
}