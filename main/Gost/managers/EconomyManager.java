package io.Sriptirc_wp_1258.gost.managers;

import io.Sriptirc_wp_1258.gost.Gost;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
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
            player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.unavailable"));
            return false;
        }
        
        double entryFee = plugin.getConfigManager().getEntryFee();
        if (entryFee <= 0) {
            return true; // 免费入场
        }
        
        // 兼容多种Vault版本：先尝试 OfflinePlayer，失败则回退到 Player
        if (!checkBalance(player, entryFee)) {
            return false;
        }
        
        // 扣除金币
        if (!withdraw(player, entryFee)) {
            return false;
        }
        
        // 记录玩家贡献
        playerContributions.put(player.getUniqueId(), entryFee);
        
        // 更新奖池
        prizePool += entryFee;
        
        player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.entry-fee-paid", entryFee));
        return true;
    }
    
    // 兼容性：检查余额
    private boolean checkBalance(Player player, double amount) {
        try {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
            return economy.has(offlinePlayer, amount);
        } catch (AbstractMethodError | NoSuchMethodError | Exception e1) {
            try {
                // 回退：使用 Player 参数
                return economy.has(player, amount);
            } catch (AbstractMethodError | NoSuchMethodError | Exception e2) {
                // 回退：使用玩家名字符串
                return economy.has(player.getName(), amount);
            }
        }
    }
    
    // 兼容性：扣款
    private boolean withdraw(Player player, double amount) {
        try {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
            EconomyResponse response = economy.withdrawPlayer(offlinePlayer, amount);
            return response != null && response.transactionSuccess();
        } catch (AbstractMethodError | NoSuchMethodError | Exception e1) {
            try {
                EconomyResponse response = economy.withdrawPlayer(player, amount);
                return response != null && response.transactionSuccess();
            } catch (AbstractMethodError | NoSuchMethodError | Exception e2) {
                EconomyResponse response = economy.withdrawPlayer(player.getName(), amount);
                return response != null && response.transactionSuccess();
            }
        }
    }
    
    // 兼容性：存款
    private void deposit(Player player, double amount) {
        try {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
            economy.depositPlayer(offlinePlayer, amount);
        } catch (AbstractMethodError | NoSuchMethodError | Exception e1) {
            try {
                economy.depositPlayer(player, amount);
            } catch (AbstractMethodError | NoSuchMethodError | Exception e2) {
                economy.depositPlayer(player.getName(), amount);
            }
        }
    }
    
    /**
     * 发放服务器额外奖励（与奖池分离，由服务器支出）
     */
    public void depositServerReward(Player player, double amount) {
        if (!isEconomyEnabled() || amount <= 0) {
            return;
        }
        deposit(player, amount);
    }
    
    // 退还入场费
    public void refundEntryFee(Player player) {
        refundEntryFee(player, false);
    }

    /**
     * 退还入场费（v2.3.2_B：silent=true 时不向玩家发消息，仍执行退款）
     */
    public void refundEntryFee(Player player, boolean silent) {
        if (!isEconomyEnabled()) {
            return;
        }

        Double contribution = playerContributions.get(player.getUniqueId());
        if (contribution == null || contribution <= 0) {
            return;
        }

        // 退还金币
        deposit(player, contribution);

        // 从奖池中扣除
        prizePool -= contribution;
        playerContributions.remove(player.getUniqueId());

        if (!silent) {
            player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.refund-received", contribution));
        }
    }
    
    // 分发奖金
    public void distributeRewards(boolean humanWin) {
        if (!isEconomyEnabled()) {
            Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("economy.unavailable-broadcast"));
            return;
        }
        
        // 添加服务器奖金
        double serverBonus = plugin.getConfigManager().getServerBonus();
        prizePool += serverBonus;
        
        Bukkit.broadcastMessage(ChatColor.GOLD + "════════════════════════════════");
        Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("economy.pool-total", prizePool));
        Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("economy.pool-server-bonus", serverBonus));
        Bukkit.broadcastMessage(ChatColor.GOLD + "════════════════════════════════");
        
        if (prizePool <= 0) {
            Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("economy.pool-empty"));
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
        
        // 分发奖金并发送突出显示消息
        Bukkit.broadcastMessage(ChatColor.GOLD + "════════════════════════════════");
        Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("economy.settle-header"));
        Bukkit.broadcastMessage(ChatColor.GOLD + "════════════════════════════════");
        
        for (Map.Entry<UUID, Double> entry : rewards.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null && player.isOnline()) {
                double reward = entry.getValue();
                if (reward > 0) {
                    deposit(player, reward);
                    // 发送突出显示的个人奖金消息
                    player.sendMessage(ChatColor.GOLD + "════════════════════════════════");
                    player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.reward-credited"));
                    player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.reward-final", String.format("%.2f", reward)));
                    player.sendMessage(ChatColor.GOLD + "════════════════════════════════");
                    
                    // 额外提示音效
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.8f, 1.2f);
                }
            }
        }
        
        // 广播奖金分发完成
        Bukkit.broadcastMessage(ChatColor.GOLD + "════════════════════════════════");
        Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("economy.all-paid"));
        Bukkit.broadcastMessage(ChatColor.GOLD + "════════════════════════════════");
        
        // 显示奖金排行榜
        showRewardsLeaderboard(rewards);
        
        // 重置奖池
        prizePool = 0.0;
        playerContributions.clear();
    }
    
    private void showRewardsLeaderboard(Map<UUID, Double> rewards) {
        if (rewards.isEmpty()) return;
        List<Map.Entry<UUID, Double>> sorted = new ArrayList<>(rewards.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("economy.leaderboard-header"));
        Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("economy.leaderboard-title"));
        Bukkit.broadcastMessage("");
        int rank = 1;
        for (Map.Entry<UUID, Double> entry : sorted) {
            Player player = Bukkit.getPlayer(entry.getKey());
            String playerName = (player != null) ? player.getName() : plugin.getLanguageManager().getMessage("economy.unknown-player");
            double reward = entry.getValue();
            String medal;
            switch (rank) {
                case 1: medal = "§6🥇"; break;
                case 2: medal = "§7🥈"; break;
                case 3: medal = "§e🥉"; break;
                default: medal = "§f" + rank + "."; break;
            }
            Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("economy.leaderboard-entry",
                medal, playerName, String.format("%.2f", reward)));
            rank++;
        }
        Bukkit.broadcastMessage(ChatColor.GOLD + "═══════════════════════");
        Bukkit.broadcastMessage("");
    }
    
    // 人类胜利时的奖金分配
    private void distributeRewardsHumanWin(Map<UUID, Double> rewards) {
        PlayerManager playerManager = plugin.getPlayerManager();
        
        // 获取所有玩家
        List<UUID> humanPlayers = playerManager.getHumanPlayers();
        List<UUID> ghostPlayers = playerManager.getGhostPlayers();
        
        Bukkit.broadcastMessage(ChatColor.GREEN + "════════════════════════════════");
        Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("economy.human-win-header"));
        Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("economy.human-share", 70));
        Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("economy.human-to-ghost-share", 30));
        Bukkit.broadcastMessage(ChatColor.GREEN + "════════════════════════════════");
        
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
        
        Bukkit.broadcastMessage(ChatColor.RED + "════════════════════════════════");
        Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("economy.ghost-win-header"));
        Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("economy.ghost-share-all", 100));
        Bukkit.broadcastMessage(ChatColor.RED + "════════════════════════════════");
        
        // 计算鬼阵容奖金（100%）
        double ghostPrizePool = prizePool;
        distributeGhostRewards(ghostPlayers, ghostPrizePool, rewards);
    }
    
    // 分配人类奖金（100%按存活时间比例）
    private void distributeHumanRewards(List<UUID> humanPlayers, double humanPrizePool, Map<UUID, Double> rewards) {
        if (humanPlayers.isEmpty() || humanPrizePool <= 0) {
            Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("economy.human-none"));
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
            Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("economy.human-no-survivor"));
            return;
        }
        
        Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("economy.human-pool-header", String.format("%.2f", humanPrizePool)));
        Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("economy.human-method"));
        
        // 按比例分配奖金
        for (Map.Entry<UUID, Long> entry : survivalTimes.entrySet()) {
            double proportion = (double) entry.getValue() / totalSurvivalTime;
            double baseReward = humanPrizePool * proportion;
            
            // 检查玩家是否有累计鬼时间（曾被转换回人类）
            long ghostAccumulatedTime = plugin.getPlayerManager().getGhostAccumulatedTime(entry.getKey());
            double totalReward = baseReward;
            
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "════════════════════════════════");
                player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.human-survival", entry.getValue()));
                player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.human-proportion", String.format("%.1f", proportion * 100)));
                player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.human-base-reward", String.format("%.2f", baseReward)));
                
                // 如果有累计鬼时间，添加转换补偿奖金
                if (ghostAccumulatedTime > 0) {
                    // 转换补偿：按累计鬼时间给予额外奖励（20%的额外奖金）
                    double conversionBonus = baseReward * 0.2;
                    totalReward = baseReward + conversionBonus;
                    
                    player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.ghost-accumulated", ghostAccumulatedTime));
                    player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.conversion-bonus", String.format("%.2f", conversionBonus)));
                    player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.human-total", String.format("%.2f", totalReward)));
                }
            }
            
            rewards.put(entry.getKey(), totalReward);
        }
    }
    
    // 分配鬼奖金（70%按鬼存活时间，30%按感染人数）
    private void distributeGhostRewards(List<UUID> ghostPlayers, double ghostPrizePool, Map<UUID, Double> rewards) {
        if (ghostPrizePool <= 0) {
            Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("economy.ghost-none"));
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
        
        Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("economy.ghost-pool-header", String.format("%.2f", ghostPrizePool)));
        Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("economy.ghost-method"));
        
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
                    player.sendMessage(ChatColor.RED + "════════════════════════════════");
                    player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.ghost-survival", entry.getValue()));
                    player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.ghost-time-proportion", String.format("%.1f", timeProportion * 100)));
                    player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.ghost-time-reward", String.format("%.2f", timeReward)));
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
                        player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.ghost-no-survival"));
        player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.ghost-base-time-reward", String.format("%.2f", timeRewardPerPlayer)));
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
                    player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.infection-count", entry.getValue()));
                    player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.infection-proportion", String.format("%.1f", infectionProportion * 100)));
                    player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.infection-reward", String.format("%.2f", infectionReward)));
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
                        player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.infection-no-data"));
        player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.infection-base-reward", String.format("%.2f", infectionRewardPerPlayer)));
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
                    player.sendMessage(ChatColor.GOLD + "════════════════════════════════");
                    player.sendMessage(plugin.getLanguageManager().getMessage(player, "economy.ghost-total", String.format("%.2f", ghostReward)));
                }
            }
        }
    }
    
    // 获取当前奖池
    public double getPrizePool() {
        return prizePool;
    }
    
    public double getPlayerContribution(UUID playerId) {
        return playerContributions.getOrDefault(playerId, 0.0);
    }
    
    public boolean giveMoney(Player player, double amount) {
        if (!isEconomyEnabled() || player == null || amount <= 0) {
            return false;
        }
        
        EconomyResponse response = economy.depositPlayer(player, amount);
        return response.transactionSuccess();
    }
    
    public double getHumanPrizePool() {
        return prizePool * 0.7;
    }
    
    public double getTotalPrizePool() {
        return prizePool;
    }
}