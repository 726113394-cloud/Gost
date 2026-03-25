package io.Sriptirc_wp_1258.gost.managers;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class GameManager {
    
    public enum GameState {
        WAITING,     // 等待玩家加入
        STARTING,    // 倒计时中
        RUNNING,     // 游戏进行中
        ENDING,      // 游戏结束中
        STOPPED      // 游戏已停止
    }
    
    private final Gost plugin;
    
    // 游戏状态
    private GameState gameState = GameState.STOPPED;
    private UUID currentGameId;
    
    // 玩家队列
    private Set<UUID> waitingPlayers = new HashSet<>();
    
    // 游戏时间
    private long gameStartTime;
    private int gameDuration;
    private int preparationTime;
    private int queueTime;
    private boolean preparationPhase = false;
    
    // Boss栏
    private BossBar queueBossBar;
    private BossBar gameBossBar;
    
    // 任务
    private BukkitTask queueTask;
    private BukkitTask preparationTask;
    private BukkitTask gameTask;
    private BukkitTask itemDistributionTask;
    private BukkitTask ghostSenseTask;
    
    public GameManager(Gost plugin) {
        this.plugin = plugin;
        initializeBossBars();
    }
    
    private void initializeBossBars() {
        // 队列Boss栏
        queueBossBar = Bukkit.createBossBar(
            ChatColor.YELLOW + "等待玩家加入...",
            BarColor.YELLOW,
            BarStyle.SOLID
        );
        queueBossBar.setVisible(false);
        
        // 游戏Boss栏
        gameBossBar = Bukkit.createBossBar(
            ChatColor.GREEN + "游戏进行中",
            BarColor.GREEN,
            BarStyle.SOLID
        );
        gameBossBar.setVisible(false);
    }
    
    // 玩家加入队列
    public boolean joinQueue(Player player) {
        UUID playerId = player.getUniqueId();
        
        // 检查是否已经在游戏中
        if (plugin.getPlayerManager().getAllPlayers().contains(playerId)) {
            player.sendMessage(ChatColor.RED + "你已经在游戏中！");
            return false;
        }
        
        // 检查是否已经在队列中
        if (waitingPlayers.contains(playerId)) {
            player.sendMessage(ChatColor.RED + "你已经在队列中！");
            return false;
        }
        
        // 检查最大玩家数
        if (waitingPlayers.size() >= plugin.getConfigManager().getMaxPlayers()) {
            player.sendMessage(ChatColor.RED + "队列已满！");
            return false;
        }
        
        // 检查经济
        if (!plugin.getEconomyManager().chargeEntryFee(player)) {
            player.sendMessage(ChatColor.RED + "金币不足！需要 " + plugin.getConfigManager().getEntryFee() + " 金币");
            return false;
        }
        
        // 加入队列
        waitingPlayers.add(playerId);
        player.sendMessage(ChatColor.GREEN + "你已加入游戏队列！");
        
        // 广播加入队列消息
        Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + " 加入了游戏队列 (" + waitingPlayers.size() + "/" + plugin.getConfigManager().getMaxPlayers() + ")");
        
        // 更新队列Boss栏
        updateQueueBossBar();
        
        // 如果这是第一个玩家，开始队列倒计时
        if (waitingPlayers.size() == 1) {
            startQueueTimer();
        }
        
        return true;
    }
    
    // 玩家离开队列
    public boolean leaveQueue(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (!waitingPlayers.contains(playerId)) {
            player.sendMessage(ChatColor.RED + "你不在队列中！");
            return false;
        }
        
        // 退还金币
        plugin.getEconomyManager().refundEntryFee(player);
        
        // 移除队列
        waitingPlayers.remove(playerId);
        player.sendMessage(ChatColor.YELLOW + "你已离开游戏队列！");
        
        // 广播离开队列消息
        Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + " 离开了游戏队列 (" + waitingPlayers.size() + "/" + plugin.getConfigManager().getMaxPlayers() + ")");
        
        // 更新队列Boss栏
        updateQueueBossBar();
        
        // 如果队列为空，取消队列任务
        if (waitingPlayers.isEmpty() && queueTask != null) {
            queueTask.cancel();
            queueTask = null;
            queueBossBar.setVisible(false);
        }
        
        return true;
    }
    
    // 开始队列倒计时
    private void startQueueTimer() {
        queueTime = plugin.getConfigManager().getQueueTime();
        queueBossBar.setVisible(true);
        
        queueTask = new BukkitRunnable() {
            int timeLeft = queueTime;
            
            @Override
            public void run() {
                if (waitingPlayers.isEmpty()) {
                    queueBossBar.setVisible(false);
                    this.cancel();
                    return;
                }
                
                // 更新Boss栏
                double progress = (double) timeLeft / queueTime;
                queueBossBar.setProgress(progress);
                queueBossBar.setTitle(ChatColor.YELLOW + "等待玩家 " + waitingPlayers.size() + "/" + 
                    plugin.getConfigManager().getMaxPlayers() + " | 剩余: " + timeLeft + "秒");
                
                // 显示给队列中的玩家
                for (UUID playerId : waitingPlayers) {
                    Player player = Bukkit.getPlayer(playerId);
                    if (player != null && player.isOnline()) {
                        queueBossBar.addPlayer(player);
                    }
                }
                
                // 检查是否可以开始游戏
                if (timeLeft <= 0) {
                    if (waitingPlayers.size() >= plugin.getConfigManager().getMinPlayers()) {
                        startGame();
                    } else {
                        // 玩家不足，取消队列
                        Bukkit.broadcastMessage(ChatColor.RED + "玩家不足，游戏取消！");
                        for (UUID playerId : waitingPlayers) {
                            Player player = Bukkit.getPlayer(playerId);
                            if (player != null) {
                                plugin.getEconomyManager().refundEntryFee(player);
                                player.sendMessage(ChatColor.YELLOW + "游戏取消，金币已退还！");
                            }
                        }
                        waitingPlayers.clear();
                        queueBossBar.setVisible(false);
                    }
                    this.cancel();
                    return;
                }
                
                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L); // 每秒执行一次
    }
    
    // 开始游戏
    public boolean startGame() {
        if (gameState != GameState.STOPPED && gameState != GameState.WAITING) {
            return false;
        }
        
        // 检查游戏区域
        if (plugin.getAreaManager().getSelectedArea() == null) {
            Bukkit.broadcastMessage(ChatColor.RED + "游戏区域未设置！请管理员先选择游戏区域。");
            return false;
        }
        
        // 生成游戏ID
        currentGameId = UUID.randomUUID();
        gameState = GameState.STARTING;
        
        // 获取游戏时长配置
        gameDuration = plugin.getConfigManager().getGameDuration();
        preparationTime = plugin.getConfigManager().getPreparationTime();
        
        // 隐藏队列Boss栏
        queueBossBar.setVisible(false);
        
        // 将所有等待玩家加入游戏
        for (UUID playerId : waitingPlayers) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                plugin.getPlayerManager().joinGame(player);
            }
        }
        
        // 清空等待队列
        waitingPlayers.clear();
        
        // 开始准备阶段
        startPreparationPhase();
        
        Bukkit.broadcastMessage(ChatColor.GREEN + "游戏开始！准备阶段 " + preparationTime + " 秒");
        return true;
    }
    
    // 开始准备阶段
    private void startPreparationPhase() {
        preparationPhase = true;
        gameBossBar.setVisible(true);
        gameBossBar.setColor(BarColor.YELLOW);
        
        preparationTask = new BukkitRunnable() {
            int timeLeft = preparationTime;
            
            @Override
            public void run() {
                if (!preparationPhase) {
                    this.cancel();
                    return;
                }
                
                // 更新Boss栏
                double progress = (double) timeLeft / preparationTime;
                gameBossBar.setProgress(progress);
                gameBossBar.setTitle(ChatColor.YELLOW + "准备阶段 " + timeLeft + "秒");
                
                // 显示给所有游戏中的玩家
                for (UUID playerId : plugin.getPlayerManager().getAllPlayers()) {
                    Player player = Bukkit.getPlayer(playerId);
                    if (player != null && player.isOnline()) {
                        gameBossBar.addPlayer(player);
                    }
                }
                
                // 准备阶段结束
                if (timeLeft <= 0) {
                    preparationPhase = false;
                    startGamePhase();
                    this.cancel();
                    return;
                }
                
                // 每5秒提醒一次
                if (timeLeft % 5 == 0 || timeLeft <= 3) {
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "准备阶段剩余: " + timeLeft + "秒");
                }
                
                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
    
    // 随机选择初始母体鬼
    private void selectInitialMotherGhost() {
        List<UUID> allPlayers = plugin.getPlayerManager().getAllPlayers();
        
        if (allPlayers.isEmpty()) {
            plugin.getLogger().warning("无法选择母体鬼：游戏中没有玩家！");
            return;
        }
        
        // 随机选择一名玩家
        Collections.shuffle(allPlayers);
        UUID motherGhostId = allPlayers.get(0);
        
        // 设置该玩家为母体鬼
        plugin.getPlayerManager().setPlayerRole(motherGhostId, PlayerManager.PlayerRole.GHOST_MOTHER);
        
        // 获取玩家对象
        Player motherGhost = Bukkit.getPlayer(motherGhostId);
        if (motherGhost != null && motherGhost.isOnline()) {
            // 广播消息
            Bukkit.broadcastMessage(ChatColor.DARK_RED + motherGhost.getName() + " 被选为母体鬼！");
            
            // 更新队伍显示
            plugin.getTeamManager().setPlayerTeam(motherGhost, PlayerManager.PlayerRole.GHOST_MOTHER);
            
            // 应用母体鬼效果
            plugin.getPlayerManager().applyRoleEffects(motherGhost, PlayerManager.PlayerRole.GHOST_MOTHER);
            
            // 更新玩家背包（母体鬼需要狂暴药水）
            plugin.getPlayerManager().updatePlayerInventory(motherGhost, PlayerManager.PlayerRole.GHOST_MOTHER);
        }
    }
    
    // 开始游戏阶段
    private void startGamePhase() {
        gameState = GameState.RUNNING;
        gameStartTime = System.currentTimeMillis();
        gameBossBar.setColor(BarColor.GREEN);
        
        Bukkit.broadcastMessage(ChatColor.GREEN + "游戏正式开始！");
        
        // 随机选择一名玩家作为母体鬼
        selectInitialMotherGhost();
        
        // 开始游戏倒计时
        startGameTimer();
        
        // 开始道具分发任务（每分钟）
        startItemDistributionTask();
        
        // 开始幽灵感知任务（每分钟）
        startGhostSenseTask();
    }
    
    // 开始游戏倒计时
    private void startGameTimer() {
        gameTask = new BukkitRunnable() {
            int timeLeft = gameDuration;
            
            @Override
            public void run() {
                if (gameState != GameState.RUNNING) {
                    this.cancel();
                    return;
                }
                
                // 更新Boss栏
                double progress = (double) timeLeft / gameDuration;
                gameBossBar.setProgress(progress);
                
                // 格式化时间
                int minutes = timeLeft / 60;
                int seconds = timeLeft % 60;
                String timeString = String.format("%02d:%02d", minutes, seconds);
                
                // 获取游戏统计
                int humanCount = plugin.getPlayerManager().getHumanPlayers().size();
                int ghostCount = plugin.getPlayerManager().getGhostPlayers().size();
                
                gameBossBar.setTitle(ChatColor.GREEN + "游戏时间: " + timeString + 
                    " §a人类: " + humanCount + " §c鬼: " + ghostCount);
                
                // 检查游戏结束条件
                if (timeLeft <= 0) {
                    endGame(true); // 人类胜利
                    this.cancel();
                    return;
                }
                
                // 检查是否所有人类都被感染
                if (humanCount == 0) {
                    endGame(false); // 鬼胜利
                    this.cancel();
                    return;
                }
                
                // 最后2分钟：随机将一名鬼变回人类
                if (timeLeft == 120) { // 2分钟 = 120秒
                    plugin.getItemManager().randomGhostToHuman();
                }
                
                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
    
    // 开始道具分发任务
    private void startItemDistributionTask() {
        itemDistributionTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState != GameState.RUNNING) {
                    this.cancel();
                    return;
                }
                
                // 分发道具
                plugin.getItemManager().distributeItems();
            }
        }.runTaskTimer(plugin, 1200L, 1200L); // 每分钟执行一次（20 ticks * 60 = 1200）
    }
    
    // 开始幽灵感知任务
    private void startGhostSenseTask() {
        ghostSenseTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState != GameState.RUNNING) {
                    this.cancel();
                    return;
                }
                
                // 获取所有游戏中的玩家
                List<Player> allPlayers = new ArrayList<>();
                for (UUID playerId : plugin.getPlayerManager().getAllPlayers()) {
                    Player player = Bukkit.getPlayer(playerId);
                    if (player != null && player.isOnline()) {
                        allPlayers.add(player);
                    }
                }
                
                // 应用幽灵感知效果
                plugin.getItemManager().applyGhostSenseEffect(allPlayers);
            }
        }.runTaskTimer(plugin, 1200L, 1200L); // 每分钟执行一次
    }
    
    // 更新Boss栏统计
    public void updateBossBarStats(int humanCount, int ghostCount) {
        if (gameState == GameState.RUNNING && gameBossBar != null) {
            // 获取剩余时间
            long elapsed = (System.currentTimeMillis() - gameStartTime) / 1000;
            int timeLeft = Math.max(0, gameDuration - (int) elapsed);
            
            int minutes = timeLeft / 60;
            int seconds = timeLeft % 60;
            String timeString = String.format("%02d:%02d", minutes, seconds);
            
            gameBossBar.setTitle(ChatColor.GREEN + "游戏时间: " + timeString + 
                " §a人类: " + humanCount + " §c鬼: " + ghostCount);
        }
    }
    
    // 更新队列Boss栏
    private void updateQueueBossBar() {
        if (queueBossBar != null) {
            queueBossBar.setTitle(ChatColor.YELLOW + "等待玩家: " + waitingPlayers.size() + "/" + 
                plugin.getConfigManager().getMaxPlayers());
        }
    }
    
    // 结束游戏
    public void endGame(boolean humanWin) {
        gameState = GameState.ENDING;
        
        // 取消所有任务
        if (preparationTask != null) {
            preparationTask.cancel();
        }
        if (gameTask != null) {
            gameTask.cancel();
        }
        if (itemDistributionTask != null) {
            itemDistributionTask.cancel();
        }
        if (ghostSenseTask != null) {
            ghostSenseTask.cancel();
        }
        
        // 隐藏Boss栏
        gameBossBar.setVisible(false);
        
        // 广播胜利消息
        if (humanWin) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "游戏结束！人类胜利！");
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + "游戏结束！鬼胜利！");
        }
        
        // 计算并分发奖金
        plugin.getEconomyManager().distributeRewards(humanWin);
        
        // 清理数据并恢复所有玩家状态
        plugin.getPlayerManager().cleanup();
        
        // 重置游戏状态
        gameState = GameState.STOPPED;
        currentGameId = null;
        preparationPhase = false;
        
        Bukkit.broadcastMessage(ChatColor.YELLOW + "游戏已结束，可以开始新的游戏！");
    }
    
    // 强制停止游戏
    public void forceStopGame() {
        if (gameState == GameState.STOPPED) {
            return;
        }
        
        // 取消所有任务
        if (queueTask != null) {
            queueTask.cancel();
        }
        if (preparationTask != null) {
            preparationTask.cancel();
        }
        if (gameTask != null) {
            gameTask.cancel();
        }
        if (itemDistributionTask != null) {
            itemDistributionTask.cancel();
        }
        if (ghostSenseTask != null) {
            ghostSenseTask.cancel();
        }
        
        // 隐藏Boss栏
        queueBossBar.setVisible(false);
        gameBossBar.setVisible(false);
        
        // 退还队列玩家金币
        for (UUID playerId : waitingPlayers) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                plugin.getEconomyManager().refundEntryFee(player);
                player.sendMessage(ChatColor.YELLOW + "游戏强制停止，金币已退还！");
            }
        }
        waitingPlayers.clear();
        
        // 先通知所有游戏玩家
        for (UUID playerId : new ArrayList<>(plugin.getPlayerManager().getAllPlayers())) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.sendMessage(ChatColor.RED + "游戏被管理员强制停止！");
            }
        }
        
        // 然后清理数据并恢复所有玩家状态
        plugin.getPlayerManager().cleanup();
        
        // 重置游戏状态
        gameState = GameState.STOPPED;
        currentGameId = null;
        preparationPhase = false;
        
        Bukkit.broadcastMessage(ChatColor.RED + "游戏已被管理员强制停止！");
    }
    
    // 检查游戏是否在进行中
    public boolean isGameRunning() {
        return gameState == GameState.RUNNING || gameState == GameState.STARTING;
    }
    
    // 检查是否有游戏在进行
    public boolean isAnyGameRunning() {
        return gameState != GameState.STOPPED;
    }
    
    // 获取游戏状态
    public GameState getGameState() {
        return gameState;
    }
    
    // 获取等待玩家数量
    public int getWaitingPlayersCount() {
        return waitingPlayers.size();
    }
    
    // 获取队列大小（别名方法）
    public int getQueueSize() {
        return waitingPlayers.size();
    }
    
    // 获取游戏ID
    public UUID getCurrentGameId() {
        return currentGameId;
    }
}