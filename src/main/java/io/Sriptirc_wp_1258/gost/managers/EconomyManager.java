package io.Sriptirc_wp_1258.gost.managers;

import io.Sriptirc_wp_1258.gost.Gost;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.*;

public class EconomyManager {
    
    private final Gost plugin;
    private Economy economy;
    private boolean economyEnabled = false;
    
    // 奖池数据
    private double prizePool = 0.0;
    private final Map<UUID, Double> playerContributions = new HashMap<>();
    
    public EconomyManager(Gost plugin) {
        this.plugin = plugin;
        setupEconomy();
    }
    
    private void setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().warning("Vault插件未找到，经济系统将不可用！");
            return;
        }
        
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.getLogger().warning("经济服务未找到，经济系统将不可用！");
            return;
        }
        
        economy = rsp.getProvider();
        economyEnabled = true;
        plugin.getLogger().info("已连接到经济系统: " + economy.getName());
    }
    
    // 检查经济系统是否可用
    public boolean isEconomyEnabled() {
        return economyEnabled && economy != null;
    }
    
    // 收取入场费
    public boolean chargeEntryFee(Player player) {
        if (!isEconomyEnabled()) {
            plugin.getLanguageManager().sendMessage(player, "economy.system_disabled");
            return false;
        }
        
        double entryFee = plugin.getConfigManager().getEntryFee();
        
        // 检查玩家余额
        if (!economy.has(player, entryFee)) {
            return false;
        }
        
        // 扣除金币
        EconomyResponse response = economy.withdrawPlayer(player, entryFee);
        if (!response.transactionSuccess()) {
            plugin.getLogger().warning("扣除玩家 " + player.getName() + " 入场费失败: " + response.errorMessage);
            return false;
        }
        
        // 记录玩家贡献
        playerContributions.put(player.getUniqueId(), entryFee);
        
        // 更新奖池
        prizePool += entryFee;
        
        plugin.getLanguageManager().sendMessage(player, "economy.entry_fee_paid", entryFee);
        return true;
    }
    
    // 退还入场费
    public void refundEntryFee(Player player) {
        if (!isEconomyEnabled()) {
            return;
        }
        
        Double contribution = playerContributions.get(player.getUniqueId());
        if (contribution == null || contribution <= 0) {
            return;
        }
        
        // 退还金币
        EconomyResponse response = economy.depositPlayer(player, contribution);
        if (!response.transactionSuccess()) {
            plugin.getLogger().warning("退还玩家 " + player.getName() + " 入场费失败: " + response.errorMessage);
            return;
        }
        
        // 从奖池中扣除
        prizePool -= contribution;
        playerContributions.remove(player.getUniqueId());
        
        plugin.getLanguageManager().sendMessage(player, "economy.entry_fee_refunded", contribution);
    }
    
    // 分发奖金
    public void distributeRewards(boolean humanWin) {
        if (!isEconomyEnabled()) {
            plugin.getLanguageManager().broadcastMessage("economy.system_disabled");
            return;
        }
        
        // 添加服务器奖金
        double serverBonus = plugin.getConfigManager().getServerBonus();
        prizePool += serverBonus;
        
        plugin.getLanguageManager().broadcastMessage("economy.prize_pool_header");
        plugin.getLanguageManager().broadcastMessage("economy.prize_pool_total", prizePool);
        plugin.getLanguageManager().broadcastMessage("economy.prize_pool_server", serverBonus);
        plugin.getLanguageManager().broadcastMessage("economy.prize_pool_header");
        
        if (prizePool <= 0) {
            plugin.getLanguageManager().broadcastMessage("economy.prize_pool_empty");
            return;
        }
        
        Map<UUID, Double> rewards = new HashMap<>();
        
        if (humanWin) {
            // 人类胜利：人类获得70%，鬼获得30%
            distributeRewardsHumanWin(rewards);
        } else {
            // 鬼胜利：鬼获得100%
            distributeRewardsGhostWin(rewards);
        }
        
        // 分发奖金
        for (Map.Entry<UUID, Double> entry : rewards.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null && player.isOnline()) {
                double reward = entry.getValue();
                if (reward > 0) {
                    EconomyResponse response = economy.depositPlayer(player, reward);
                    if (response.transactionSuccess()) {
                        plugin.getLanguageManager().sendMessage(player, "economy.reward_received", String.format("%.2f", reward));
                    } else {
                        plugin.getLanguageManager().sendMessage(player, "economy.reward_failed", response.errorMessage);
                    }
                }
            }
        }
        
        // 重置奖池
        prizePool = 0.0;
        playerContributions.clear();
    }
    
    // 人类胜利时的奖金分配
    private void distributeRewardsHumanWin(Map<UUID, Double> rewards) {
        PlayerManager playerManager = plugin.getPlayerManager();
        
        // 获取所有玩家
        List<UUID> humanPlayers = playerManager.getHumanPlayers();
        List<UUID> ghostPlayers = playerManager.getGhostPlayers();
        
        plugin.getLanguageManager().broadcastMessage("economy.human_win_header");
        plugin.getLanguageManager().broadcastMessage("economy.human_win_title");
        plugin.getLanguageManager().broadcastMessage("economy.human_win_human_share");
        plugin.getLanguageManager().broadcastMessage("economy.human_win_ghost_share");
        plugin.getLanguageManager().broadcastMessage("economy.human_win_header");
        
        // 计算人类阵容奖金（70%）
        double humanPrizePool = prizePool * 0.7;
        distributeHumanRewards(humanPlayers, humanPrizePool, rewards);
        
        // 计算鬼阵容奖金（30%）
        double ghostPrizePool = prizePool * 0.3;
        distributeGhostRewards(ghostPlayers, ghostPrizePool, rewards);
    }
    
    // 鬼胜利时的奖金分配
    private void distributeRewardsGhostWin(Map<UUID, Double> rewards) {
        PlayerManager playerManager = plugin.getPlayerManager();
        List<UUID> ghostPlayers = playerManager.getGhostPlayers();
        
        plugin.getLanguageManager().broadcastMessage("economy.ghost_win_header");
        plugin.getLanguageManager().broadcastMessage("economy.ghost_win_title");
        plugin.getLanguageManager().broadcastMessage("economy.ghost_win_ghost_share");
        plugin.getLanguageManager().broadcastMessage("economy.ghost_win_header");
        
        // 计算鬼阵容奖金（100%）
        double ghostPrizePool = prizePool;
        distributeGhostRewards(ghostPlayers, ghostPrizePool, rewards);
    }
    
    // 分配人类奖金（100%按存活时间比例）
    private void distributeHumanRewards(List<UUID> humanPlayers, double humanPrizePool, Map<UUID, Double> rewards) {
        if (humanPlayers.isEmpty() || humanPrizePool <= 0) {
            plugin.getLanguageManager().broadcastMessage("economy.human_no_players");
            return;
        }
        
        // 计算总存活时间
        long totalSurvivalTime = 0;
        Map<UUID, Long> survivalTimes = new HashMap<>();
        
        for (UUID playerId : humanPlayers) {
            long survivalTime = plugin.getPlayerManager().getSurvivalTime(playerId);
            // 存活时间必须超过10秒才有资格获得奖励
            if (survivalTime >= 10) {
                survivalTimes.put(playerId, survivalTime);
                totalSurvivalTime += survivalTime;
            }
        }
        
        if (totalSurvivalTime == 0) {
            plugin.getLanguageManager().broadcastMessage("economy.human_no_survival");
            return;
        }
        
        plugin.getLanguageManager().broadcastMessage("economy.human_distribution_header", String.format("%.2f", humanPrizePool));
        plugin.getLanguageManager().broadcastMessage("economy.human_distribution_method");
        
        // 按比例分配奖金
        for (Map.Entry<UUID, Long> entry : survivalTimes.entrySet()) {
            double proportion = (double) entry.getValue() / totalSurvivalTime;
            double baseReward = humanPrizePool * proportion;
            
            // 检查玩家是否有累计鬼时间（曾被转换回人类）
            long ghostAccumulatedTime = plugin.getPlayerManager().getGhostAccumulatedTime(entry.getKey());
            double totalReward = baseReward;
            
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                plugin.getLanguageManager().sendMessage(player, "economy.prize_pool_header");
                plugin.getLanguageManager().sendMessage(player, "economy.player_survival_time", entry.getValue());
                plugin.getLanguageManager().sendMessage(player, "economy.player_survival_ratio", String.format("%.1f", proportion * 100));
                plugin.getLanguageManager().sendMessage(player, "economy.player_base_reward", String.format("%.2f", baseReward));
                
                // 如果有累计鬼时间，添加转换补偿奖金
                if (ghostAccumulatedTime > 0) {
                    // 转换补偿：按累计鬼时间给予额外奖励（20%的额外奖金）
                    double conversionBonus = baseReward * 0.2;
                    totalReward = baseReward + conversionBonus;
                    
                    plugin.getLanguageManager().sendMessage(player, "economy.player_ghost_time", ghostAccumulatedTime);
                    plugin.getLanguageManager().sendMessage(player, "economy.player_conversion_bonus", String.format("%.2f", conversionBonus));
                    plugin.getLanguageManager().sendMessage(player, "economy.player_total_reward", String.format("%.2f", totalReward));
                }
            }
            
            rewards.put(entry.getKey(), totalReward);
        }
    }
    
    // 分配鬼奖金（70%按鬼存活时间，30%按感染人数）
    private void distributeGhostRewards(List<UUID> ghostPlayers, double ghostPrizePool, Map<UUID, Double> rewards) {
        if (ghostPrizePool <= 0) {
            plugin.getLanguageManager().broadcastMessage("economy.ghost_zero_prize");
            return;
        }
        
        // 获取所有玩家（包括当前鬼和曾经是鬼的玩家）
        List<UUID> allPlayers = plugin.getPlayerManager().getAllPlayers();
        
        // 计算所有玩家的累计鬼时间和感染人数
        long totalGhostTime = 0;
        Map<UUID, Long> ghostTimes = new HashMap<>();
        
        // 计算总感染人数
        int totalInfections = 0;
        Map<UUID, Integer> infectionCounts = new HashMap<>();
        
        for (UUID playerId : allPlayers) {
            // 获取累计鬼时间（包括被转换回人类的玩家）
            long ghostAccumulatedTime = plugin.getPlayerManager().getGhostAccumulatedTime(playerId);
            
            // 如果是当前鬼玩家，加上当前鬼时间
            if (ghostPlayers.contains(playerId)) {
                long currentGhostTime = plugin.getPlayerManager().getGhostTime(playerId);
                ghostAccumulatedTime += currentGhostTime;
            }
            
            if (ghostAccumulatedTime >= 10) { // 至少存活10秒
                ghostTimes.put(playerId, ghostAccumulatedTime);
                totalGhostTime += ghostAccumulatedTime;
            }
            
            // 获取感染人数（所有玩家，包括被转换回人类的）
            int infections = plugin.getPlayerManager().getInfectionCount(playerId);
            if (infections > 0) {
                infectionCounts.put(playerId, infections);
                totalInfections += infections;
            }
        }
        
        plugin.getLanguageManager().broadcastMessage("economy.ghost_distribution_header", String.format("%.2f", ghostPrizePool));
        plugin.getLanguageManager().broadcastMessage("economy.ghost_distribution_method");
        
        // 计算时间部分奖金（70%）
        double timePrizePool = ghostPrizePool * 0.7;
        // 计算感染部分奖金（30%）
        double infectionPrizePool = ghostPrizePool * 0.3;
        
        // 分配时间部分奖金
        if (totalGhostTime > 0) {
            for (Map.Entry<UUID, Long> entry : ghostTimes.entrySet()) {
                double timeProportion = (double) entry.getValue() / totalGhostTime;
                double timeReward = timePrizePool * timeProportion;
                
                // 累加到总奖励
                double currentReward = rewards.getOrDefault(entry.getKey(), 0.0);
                rewards.put(entry.getKey(), currentReward + timeReward);
                
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null) {
                    plugin.getLanguageManager().sendMessage(player, "economy.prize_pool_header");
                    plugin.getLanguageManager().sendMessage(player, "economy.ghost_time", entry.getValue());
                    plugin.getLanguageManager().sendMessage(player, "economy.ghost_time_ratio", String.format("%.1f", timeProportion * 100));
                    plugin.getLanguageManager().sendMessage(player, "economy.ghost_time_reward", String.format("%.2f", timeReward));
                }
            }
        } else {
            // 如果没有鬼存活时间数据，时间部分奖金平均分配给所有有累计鬼时间的玩家
            List<UUID> playersWithGhostTime = new ArrayList<>(ghostTimes.keySet());
            if (playersWithGhostTime.isEmpty()) {
                playersWithGhostTime = new ArrayList<>(ghostPlayers);
            }
            
            if (!playersWithGhostTime.isEmpty()) {
                double timeRewardPerPlayer = timePrizePool / playersWithGhostTime.size();
                for (UUID playerId : playersWithGhostTime) {
                    double currentReward = rewards.getOrDefault(playerId, 0.0);
                    rewards.put(playerId, currentReward + timeRewardPerPlayer);
                    
                    Player player = Bukkit.getPlayer(playerId);
                    if (player != null) {
                        plugin.getLanguageManager().sendMessage(player, "economy.ghost_no_time");
                        plugin.getLanguageManager().sendMessage(player, "economy.ghost_base_time_reward", String.format("%.2f", timeRewardPerPlayer));
                    }
                }
            }
        }
        
        // 分配感染部分奖金
        if (totalInfections > 0) {
            for (Map.Entry<UUID, Integer> entry : infectionCounts.entrySet()) {
                double infectionProportion = (double) entry.getValue() / totalInfections;
                double infectionReward = infectionPrizePool * infectionProportion;
                
                // 累加到总奖励
                double currentReward = rewards.getOrDefault(entry.getKey(), 0.0);
                rewards.put(entry.getKey(), currentReward + infectionReward);
                
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null) {
                    plugin.getLanguageManager().sendMessage(player, "economy.infection_count", entry.getValue());
                    plugin.getLanguageManager().sendMessage(player, "economy.infection_ratio", String.format("%.1f", infectionProportion * 100));
                    plugin.getLanguageManager().sendMessage(player, "economy.infection_reward", String.format("%.2f", infectionReward));
                }
            }
        } else {
            // 如果没有感染数据，感染部分奖金平均分配给所有有感染记录的玩家
            List<UUID> playersWithInfections = new ArrayList<>(infectionCounts.keySet());
            if (playersWithInfections.isEmpty()) {
                playersWithInfections = new ArrayList<>(ghostPlayers);
            }
            
            if (!playersWithInfections.isEmpty()) {
                double infectionRewardPerPlayer = infectionPrizePool / playersWithInfections.size();
                for (UUID playerId : playersWithInfections) {
                    double currentReward = rewards.getOrDefault(playerId, 0.0);
                    rewards.put(playerId, currentReward + infectionRewardPerPlayer);
                    
                    Player player = Bukkit.getPlayer(playerId);
                    if (player != null) {
                        plugin.getLanguageManager().sendMessage(player, "economy.infection_no_data");
                        plugin.getLanguageManager().sendMessage(player, "economy.infection_base_reward", String.format("%.2f", infectionRewardPerPlayer));
                    }
                }
            }
        }
        
        // 显示每个有鬼奖金的玩家的总奖金
        for (UUID playerId : allPlayers) {
            double ghostReward = rewards.getOrDefault(playerId, 0.0);
            if (ghostReward > 0) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null) {
                    plugin.getLanguageManager().sendMessage(player, "economy.prize_pool_header");
                    plugin.getLanguageManager().sendMessage(player, "economy.ghost_total_reward", String.format("%.2f", ghostReward));
                }
            }
        }
    }
    
    // 获取当前奖池
    public double getPrizePool() {
        return prizePool;
    }
    
    // 获取玩家贡献
    public double getPlayerContribution(UUID playerId) {
        return playerContributions.getOrDefault(playerId, 0.0);
    }
}