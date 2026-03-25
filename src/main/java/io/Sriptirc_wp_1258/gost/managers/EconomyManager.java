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
            player.sendMessage(ChatColor.RED + "经济系统不可用！");
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
        
        player.sendMessage(ChatColor.GREEN + "已支付入场费: " + entryFee + " 金币");
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
        
        player.sendMessage(ChatColor.YELLOW + "入场费已退还: " + contribution + " 金币");
    }
    
    // 分发奖金
    public void distributeRewards(boolean humanWin) {
        if (!isEconomyEnabled()) {
            Bukkit.broadcastMessage(ChatColor.RED + "经济系统不可用，无法分发奖金！");
            return;
        }
        
        // 添加服务器奖金
        double serverBonus = plugin.getConfigManager().getServerBonus();
        prizePool += serverBonus;
        
        Bukkit.broadcastMessage(ChatColor.GOLD + "奖池总额: " + prizePool + " 金币");
        Bukkit.broadcastMessage(ChatColor.GOLD + "（包含服务器奖金: " + serverBonus + " 金币）");
        
        if (prizePool <= 0) {
            Bukkit.broadcastMessage(ChatColor.RED + "奖池为空，无法分发奖金！");
            return;
        }
        
        Map<UUID, Double> rewards = new HashMap<>();
        
        if (humanWin) {
            // 人类胜利：按存活时间比例分配
            distributeBySurvivalTime(rewards);
        } else {
            // 鬼胜利：按感染人数比例分配
            distributeByInfectionCount(rewards);
        }
        
        // 分发奖金
        for (Map.Entry<UUID, Double> entry : rewards.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null && player.isOnline()) {
                double reward = entry.getValue();
                if (reward > 0) {
                    EconomyResponse response = economy.depositPlayer(player, reward);
                    if (response.transactionSuccess()) {
                        player.sendMessage(ChatColor.GREEN + "你获得了 " + String.format("%.2f", reward) + " 金币奖励！");
                    } else {
                        player.sendMessage(ChatColor.RED + "发放奖励失败: " + response.errorMessage);
                    }
                }
            }
        }
        
        // 重置奖池
        prizePool = 0.0;
        playerContributions.clear();
    }
    
    // 按存活时间比例分配
    private void distributeBySurvivalTime(Map<UUID, Double> rewards) {
        PlayerManager playerManager = plugin.getPlayerManager();
        List<UUID> humanPlayers = playerManager.getHumanPlayers();
        
        if (humanPlayers.isEmpty()) {
            return;
        }
        
        // 计算总存活时间
        long totalSurvivalTime = 0;
        Map<UUID, Long> survivalTimes = new HashMap<>();
        
        for (UUID playerId : humanPlayers) {
            long survivalTime = playerManager.getSurvivalTime(playerId);
            // 存活时间必须超过1分钟才有资格获得奖励
            if (survivalTime >= 60) {
                survivalTimes.put(playerId, survivalTime);
                totalSurvivalTime += survivalTime;
            }
        }
        
        if (totalSurvivalTime == 0) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "没有人类存活时间超过1分钟，奖金不发放！");
            return;
        }
        
        // 按比例分配奖金
        for (Map.Entry<UUID, Long> entry : survivalTimes.entrySet()) {
            double proportion = (double) entry.getValue() / totalSurvivalTime;
            double reward = prizePool * proportion;
            rewards.put(entry.getKey(), reward);
            
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "你的存活时间: " + entry.getValue() + "秒，获得比例: " + 
                    String.format("%.1f", proportion * 100) + "%");
            }
        }
    }
    
    // 按感染人数比例分配
    private void distributeByInfectionCount(Map<UUID, Double> rewards) {
        PlayerManager playerManager = plugin.getPlayerManager();
        List<UUID> ghostPlayers = playerManager.getGhostPlayers();
        
        if (ghostPlayers.isEmpty()) {
            return;
        }
        
        // 计算总感染人数
        int totalInfections = 0;
        Map<UUID, Integer> infectionCounts = new HashMap<>();
        
        for (UUID playerId : ghostPlayers) {
            int infections = playerManager.getInfectionCount(playerId);
            if (infections > 0) {
                infectionCounts.put(playerId, infections);
                totalInfections += infections;
            }
        }
        
        if (totalInfections == 0) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "没有鬼感染过人类，奖金不发放！");
            return;
        }
        
        // 按比例分配奖金
        for (Map.Entry<UUID, Integer> entry : infectionCounts.entrySet()) {
            double proportion = (double) entry.getValue() / totalInfections;
            double reward = prizePool * proportion;
            rewards.put(entry.getKey(), reward);
            
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "你的感染人数: " + entry.getValue() + "，获得比例: " + 
                    String.format("%.1f", proportion * 100) + "%");
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