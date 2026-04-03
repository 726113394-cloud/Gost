package io.Sriptirc_wp_1258.gost.managers;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

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
    private int remainingGameTime;
    private boolean preparationPhase = false;
    
    // Boss栏
    private BossBar queueBossBar;
    private BossBar gameBossBar;
    
    // 任务
    private CancellableTask queueTask;
    private CancellableTask preparationTask;
    private CancellableTask gameTask;
    private CancellableTask itemDistributionTask;
    private CancellableTask ghostSenseTask;
    private CancellableTask minuteGlowingTask;
    private CancellableTask ghostToHumanTask;
    
    public GameManager(Gost plugin) {
        this.plugin = plugin;
        initializeBossBars();
    }
    
    private void initializeBossBars() {
        // 队列Boss栏
        queueBossBar = Bukkit.createBossBar(
            plugin.getLanguageManager().getMessage("game.waiting_for_players", 0, plugin.getConfigManager().getMaxPlayers()),
            BarColor.YELLOW,
            BarStyle.SOLID
        );
        queueBossBar.setVisible(false);
        
        // 游戏Boss栏
        gameBossBar = Bukkit.createBossBar(
            plugin.getLanguageManager().getMessage("game.started"),
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
            plugin.getLanguageManager().sendMessage(player, "game.already_in_game");
            return false;
        }
        
        // 检查是否已经在队列中
        if (waitingPlayers.contains(playerId)) {
            plugin.getLanguageManager().sendMessage(player, "player.already_in_queue");
            return false;
        }
        
        // 检查最大玩家数
        if (waitingPlayers.size() >= plugin.getConfigManager().getMaxPlayers()) {
            plugin.getLanguageManager().sendMessage(player, "game.queue_full");
            return false;
        }
        
        // 检查经济
        if (!plugin.getEconomyManager().chargeEntryFee(player)) {
            plugin.getLanguageManager().sendMessage(player, "economy.insufficient_funds", (int) plugin.getConfigManager().getEntryFee());
            return false;
        }
        
        // 加入队列
        waitingPlayers.add(playerId);
        plugin.getLanguageManager().sendMessage(player, "player.joined_queue");
        
        // 广播加入队列消息
        plugin.getLanguageManager().broadcastMessage("broadcast.player_joined", player.getName(), waitingPlayers.size(), plugin.getConfigManager().getMaxPlayers());
        
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
            plugin.getLanguageManager().sendMessage(player, "player.not_in_queue");
            return false;
        }
        
        // 退还金币
        plugin.getEconomyManager().refundEntryFee(player);
        
        // 移除队列
        waitingPlayers.remove(playerId);
        plugin.getLanguageManager().sendMessage(player, "player.left_queue");
        
        // 广播离开队列消息
        plugin.getLanguageManager().broadcastMessage("broadcast.player_left", player.getName(), waitingPlayers.size(), plugin.getConfigManager().getMaxPlayers());
        
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
        
        queueTask = new CancellableTask(plugin) {
            int timeLeft = queueTime;
            boolean matchQueueStarted = false;
            int matchQueueTimeLeft = 0;
            
            @Override
            public boolean execute() {
                if (waitingPlayers.isEmpty()) {
                    queueBossBar.setVisible(false);
                    return false;
                }
                
                // 检查是否达到最大玩家数，开始匹配队列倒计时
                if (!matchQueueStarted && waitingPlayers.size() >= plugin.getConfigManager().getMaxPlayers()) {
                    matchQueueStarted = true;
                    matchQueueTimeLeft = plugin.getConfigManager().getMatchQueueTime();
                    Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("game.queue_full_countdown", matchQueueTimeLeft));
                }
                
                // 处理匹配队列倒计时
                if (matchQueueStarted) {
                    if (matchQueueTimeLeft > 0) {
                        // 只在最后10秒发送居中大文本倒计时提示
                        if (matchQueueTimeLeft <= 10) {
                            for (UUID playerId : waitingPlayers) {
                                Player player = Bukkit.getPlayer(playerId);
                                if (player != null && player.isOnline()) {
                                    player.sendTitle(
                                        plugin.getLanguageManager().getMessage("game.game_starting_title"),
                                        plugin.getLanguageManager().getMessage("game.game_starting_subtitle", matchQueueTimeLeft),
                                        0, 20, 0
                                    );
                                }
                            }
                        }
                        
                        // 每10秒或最后10秒发送聊天提示
                        if (matchQueueTimeLeft == 10 || matchQueueTimeLeft == 5 || matchQueueTimeLeft <= 3) {
                            plugin.getLanguageManager().broadcastMessage("game.game_start_countdown", matchQueueTimeLeft);
                        }
                        
                        matchQueueTimeLeft--;
                    } else {
                        // 匹配队列倒计时结束，开始游戏
                        startGame();
                        return false;
                    }
                }
                
                // 更新Boss栏
                double progress = (double) timeLeft / queueTime;
                queueBossBar.setProgress(progress);
                String title = plugin.getLanguageManager().getMessage("game.waiting_for_players", waitingPlayers.size(), plugin.getConfigManager().getMaxPlayers());
                if (matchQueueStarted) {
                    title += " " + plugin.getLanguageManager().getMessage("game.queue_full_countdown", matchQueueTimeLeft);
                }
                queueBossBar.setTitle(title);
                
                // 显示给队列中的玩家
                for (UUID playerId : waitingPlayers) {
                    Player player = Bukkit.getPlayer(playerId);
                    if (player != null && player.isOnline()) {
                        queueBossBar.addPlayer(player);
                    }
                }
                
                // 检查是否可以开始游戏（普通队列时间结束）
                if (timeLeft <= 0) {
                    if (waitingPlayers.size() >= plugin.getConfigManager().getMinPlayers()) {
                        startGame();
                    } else {
                        // 玩家不足，取消队列
                        plugin.getLanguageManager().broadcastMessage("game.not_enough_players_cancel");
                        for (UUID playerId : waitingPlayers) {
                            Player player = Bukkit.getPlayer(playerId);
                            if (player != null) {
                                plugin.getEconomyManager().refundEntryFee(player);
                                plugin.getLanguageManager().sendMessage(player, "game.game_cancel_refund");
                            }
                        }
                        waitingPlayers.clear();
                        queueBossBar.setVisible(false);
                    }
                    return false;
                }
                
                timeLeft--;
                return true;
            }
        };
        queueTask.startTimer(0L);
    }
    
    // 开始游戏
    public boolean startGame() {
        if (gameState != GameState.STOPPED && gameState != GameState.WAITING) {
            return false;
        }
        
        // 检查游戏区域 - 自动选择启用的区域
        AreaManager.GameArea selectedArea = plugin.getAreaManager().getSelectedArea();
        if (selectedArea == null) {
            // 尝试自动选择区域
            selectedArea = plugin.getAreaManager().autoSelectArea();
            if (selectedArea == null) {
                plugin.getLanguageManager().broadcastMessage("game.no_enabled_area");
                return false;
            }
            plugin.getLanguageManager().broadcastMessage("game.area_auto_selected", selectedArea.getName());
        } else {
            // 检查选中的区域是否已启用
            if (!plugin.getAreaManager().isAreaEnabled(selectedArea.getName())) {
                plugin.getLanguageManager().broadcastMessage("game.area_not_enabled");
                return false;
            }
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
        
        plugin.getLanguageManager().broadcastMessage("game.game_start_broadcast", preparationTime);
        return true;
    }
    
    // 开始准备阶段
    private void startPreparationPhase() {
        preparationPhase = true;
        gameBossBar.setVisible(true);
        gameBossBar.setColor(BarColor.YELLOW);
        
        preparationTask = new CancellableTask(plugin) {
            int timeLeft = preparationTime;
            
            @Override
            public boolean execute() {
                if (!preparationPhase) {
                    return false;
                }
                
                // 更新Boss栏
                double progress = (double) timeLeft / preparationTime;
                gameBossBar.setProgress(progress);
                gameBossBar.setTitle(plugin.getLanguageManager().getMessage("stage.preparation_title", timeLeft));
                
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
                    return false;
                }
                
                // 每5秒提醒一次
                if (timeLeft % 5 == 0 || timeLeft <= 3) {
                    plugin.getLanguageManager().broadcastMessage("game.preparation_time_left", timeLeft);
                    
                    // 发送准备阶段标题
                    sendPreparationTitle(timeLeft);
                }
                
                // 最后3秒每1秒发送标题
                if (timeLeft <= 3) {
                    sendPreparationTitle(timeLeft);
                }
                
                timeLeft--;
                return true;
            }
        };
        preparationTask.startTimer(0L);
        
        // 清理所有玩家的游戏效果，确保公平性
        cleanupPlayerEffects();
        
        // 在准备阶段开始时随机选择母体鬼并启动禁足倒计时
        selectInitialMotherGhost();
    }
    
    // 清理所有玩家的游戏效果，确保公平性
    private void cleanupPlayerEffects() {
        plugin.getLogger().info("清理所有玩家的游戏效果，确保管理员和普通玩家公平游戏...");
        
        for (UUID playerId : plugin.getPlayerManager().getAllPlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                // 清除所有可能影响游戏平衡的效果
                player.removePotionEffect(PotionEffectType.BLINDNESS);
                player.removePotionEffect(PotionEffectType.SLOW);
                player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                player.removePotionEffect(PotionEffectType.SPEED);
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                player.removePotionEffect(PotionEffectType.GLOWING);
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                
                // 如果是管理员或创造模式玩家，发送提示消息
                boolean isAdmin = player.hasPermission("gost.admin");
                boolean wasCreative = player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR;
                
                if (isAdmin || wasCreative) {
                    player.sendMessage(plugin.getLanguageManager().getMessage("general.game_reset_fair"));
                    String role = isAdmin ? plugin.getLanguageManager().getMessage("general.admin_role") : plugin.getLanguageManager().getMessage("general.creative_role");
                    player.sendMessage(plugin.getLanguageManager().getMessage("general.game_reset_admin", role));
                }
                
                plugin.getLogger().info("已清理玩家效果: " + player.getName() + 
                    (isAdmin ? " (管理员)" : "") + (wasCreative ? " (创造模式)" : ""));
            }
        }
        
        plugin.getLogger().info("游戏效果清理完成！");
    }
    
    // 随机选择初始母体鬼
    private void selectInitialMotherGhost() {
        List<UUID> allPlayers = plugin.getPlayerManager().getAllPlayers();
        
        if (allPlayers.isEmpty()) {
            plugin.getLogger().warning(plugin.getLanguageManager().getMessage("log.no_players_mother_ghost"));
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
            plugin.getLanguageManager().broadcastMessage("game.mother_ghost_selected", motherGhost.getName());
            
            // 更新队伍显示
            plugin.getTeamManager().setPlayerTeam(motherGhost, PlayerManager.PlayerRole.GHOST_MOTHER);
            
            // 应用母体鬼效果
            plugin.getPlayerManager().applyRoleEffects(motherGhost, PlayerManager.PlayerRole.GHOST_MOTHER);
            
            // 更新玩家背包（母体鬼需要狂暴药水）
            plugin.getPlayerManager().updatePlayerInventory(motherGhost, PlayerManager.PlayerRole.GHOST_MOTHER);
            
            // 启动母体禁足倒计时
            startMotherGhostImmobilizeCountdown(motherGhost);
        }
    }
    
    // 启动母体禁足倒计时
    private void startMotherGhostImmobilizeCountdown(Player motherGhost) {
        int immobilizeDuration = plugin.getConfigManager().getGhostImmobilizeDuration();
        
        new CancellableTask(plugin) {
            int timeLeft = immobilizeDuration;
            
            @Override
            public boolean execute() {
                if (!motherGhost.isOnline() || 
                    plugin.getPlayerManager().getPlayerRole(motherGhost.getUniqueId()) != PlayerManager.PlayerRole.GHOST_MOTHER) {
                    return false;
                }
                
                if (timeLeft > 0) {
                    // 只在最后10秒显示居中大文本倒计时提示
                    if (timeLeft <= 10) {
                        motherGhost.sendTitle(
                            plugin.getLanguageManager().getMessage("game.immobilize_title"),
                            plugin.getLanguageManager().getMessage("game.immobilize_subtitle", timeLeft),
                            0, 20, 0);
                    }
                    
                    // 最后10秒发送聊天提示
                    if (timeLeft == 10 || timeLeft == 5 || timeLeft <= 3) {
                        motherGhost.sendMessage(plugin.getLanguageManager().getMessage("game.immobilize_countdown", timeLeft));
                    }
                    
                    timeLeft--;
                } else {
                    // 禁足结束
                    motherGhost.sendTitle(
                        plugin.getLanguageManager().getMessage("game.immobilize_end_title"),
                        plugin.getLanguageManager().getMessage("game.immobilize_end_subtitle"),
                        10, 40, 10);
                    motherGhost.sendMessage(plugin.getLanguageManager().getMessage("game.immobilize_end_message"));
                    return false;
                }
                return true;
            }
        }.startTimer(0L);
    }
    
    // 开始游戏阶段
    private void startGamePhase() {
        gameState = GameState.RUNNING;
        gameStartTime = System.currentTimeMillis();
        gameBossBar.setColor(BarColor.GREEN);
        
        plugin.getLanguageManager().broadcastMessage("general.game_official_start");
        
        // 注意：母体鬼已经在准备阶段选择并禁足，这里不再重复选择
        
        // 开始游戏倒计时
        startGameTimer();
        
        // 开始道具分发任务（每分钟）
        startItemDistributionTask();
        
        // 开始幽灵感知任务（每分钟）
        startGhostSenseTask();
        
        // 开始道具刷新系统
        plugin.getItemSpawnManager().startSpawning();
        
        // 开始每分钟高亮效果任务
        startMinuteGlowingTask();
        
        // 应用黑暗效果（如果启用）
        plugin.getDarkEffectManager().applyDarkEffectToAllPlayers();
        
        // 开始心跳声效果（如果启用）
        plugin.getHeartbeatManager().startHeartbeat();
        
        // 开始鬼转人类功能（如果启用）
        startGhostToHumanTask();
        
        // 开始货币发放系统（暂时取消）
        // plugin.getCurrencyManager().startDistribution();
        
        // 发送游戏开始标题
        sendGameStartTitles();
    }
    
    // 开始游戏倒计时
    private void startGameTimer() {
        remainingGameTime = gameDuration;
        
        gameTask = new CancellableTask(plugin) {
            int timeLeft = gameDuration;
            
            @Override
            public boolean execute() {
                remainingGameTime = timeLeft;
                
                if (gameState != GameState.RUNNING) {
                    return false;
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
                
                gameBossBar.setTitle(plugin.getLanguageManager().getMessage("game.bossbar_running", timeString, humanCount, ghostCount));
                
                // 检查游戏结束条件
                if (timeLeft <= 0) {
                    endGame(true); // 人类胜利
                    return false;
                }
                
                // 检查是否所有人类都被感染
                if (humanCount == 0) {
                    endGame(false); // 鬼胜利
                    return false;
                }
                
                // 检查鬼数量是否为0
                if (ghostCount == 0) {
                    // 鬼数量为0，人类自动胜利，但不发放奖金，退还入场金
                    endGameWithNoGhosts();
                    return false;
                }
                
                // 最后2分钟：随机将一名鬼变回人类（如果转化功能启用）
                if (plugin.getConfigManager().isConversionEnabled() && timeLeft == plugin.getConfigManager().getConversionActivateTime()) {
                    // 发送转化提示
                    plugin.getLanguageManager().broadcastMessage("general.conversion_activated");
                    // 这里可以添加转化逻辑，比如给予玩家转化物品
                }
                
                timeLeft--;
                return true;
            }
        };
        gameTask.startTimer(0L);
    }
    
    // 开始道具分发任务
    private void startItemDistributionTask() {
        itemDistributionTask = new CancellableTask(plugin) {
            @Override
            public boolean execute() {
                if (gameState != GameState.RUNNING) {
                    return false;
                }
                
                // 分发道具
                plugin.getItemManager().distributeItems();
                return true;
            }
        };
        itemDistributionTask.startTimer(1200L); // 每分钟执行一次
    }
    
    // 开始幽灵感知任务
    private void startGhostSenseTask() {
        ghostSenseTask = new CancellableTask(plugin) {
            @Override
            public boolean execute() {
                if (gameState != GameState.RUNNING) {
                    return false;
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
                return true;
            }
        };
        ghostSenseTask.startTimer(1200L); // 每分钟执行一次
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
            
            gameBossBar.setTitle(plugin.getLanguageManager().getMessage("game.bossbar_running", timeString, humanCount, ghostCount));
        }
    }
    
    // 更新队列Boss栏
    private void updateQueueBossBar() {
        if (queueBossBar != null) {
            queueBossBar.setTitle(plugin.getLanguageManager().getMessage("game.bossbar_waiting", waitingPlayers.size(),
                plugin.getConfigManager().getMaxPlayers()));
        }
    }
    
    // 结束游戏
    public void endGame(boolean humanWin) {
        gameState = GameState.ENDING;
        remainingGameTime = 0;
        
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
        if (minuteGlowingTask != null) {
            minuteGlowingTask.cancel();
        }
        if (ghostToHumanTask != null) {
            ghostToHumanTask.cancel();
        }
        
        // 隐藏Boss栏
        gameBossBar.setVisible(false);
        
        // 广播胜利消息
        if (humanWin) {
            plugin.getLanguageManager().broadcastMessage("general.human_win");
        } else {
            plugin.getLanguageManager().broadcastMessage("general.ghost_win");
        }
        
        // 发送游戏结束标题
        sendGameEndTitle(humanWin);
        
        // 计算并分发奖金
        plugin.getEconomyManager().distributeRewards(humanWin);
        
        // 停止心跳声效果
        plugin.getHeartbeatManager().stopHeartbeat();
        
        // 清理数据并恢复所有玩家状态
        plugin.getPlayerManager().cleanup();
        
        // 重置游戏状态
        gameState = GameState.STOPPED;
        currentGameId = null;
        preparationPhase = false;
        
        plugin.getLanguageManager().broadcastMessage("general.game_over_new_game");
    }
    
    /**
     * 鬼数量为0时结束游戏（不发放奖金，退还入场金）
     */
    private void endGameWithNoGhosts() {
        gameState = GameState.ENDING;
        remainingGameTime = 0;
        
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
        if (minuteGlowingTask != null) {
            minuteGlowingTask.cancel();
        }
        if (ghostToHumanTask != null) {
            ghostToHumanTask.cancel();
        }
        
        // 隐藏Boss栏
        gameBossBar.setVisible(false);
        
        // 广播消息
        plugin.getLanguageManager().broadcastMessage("general.ghost_zero_bonus_none");
        plugin.getLanguageManager().broadcastMessage("general.ghost_zero_no_server_bonus");
        plugin.getLanguageManager().broadcastMessage("general.ghost_zero_refund");
        
        // 发送游戏结束标题（人类胜利）
        sendGameEndTitle(true);
        
        // 退还所有玩家的入场金
        List<UUID> allPlayers = plugin.getPlayerManager().getAllPlayers();
        for (UUID playerId : allPlayers) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                plugin.getEconomyManager().refundEntryFee(player);
                player.sendMessage(plugin.getLanguageManager().getMessage("general.entry_refund"));
            }
        }
        
        // 停止心跳声效果
        plugin.getHeartbeatManager().stopHeartbeat();
        
        // 清理数据并恢复所有玩家状态
        plugin.getPlayerManager().cleanup();
        
        // 重置游戏状态
        gameState = GameState.STOPPED;
        currentGameId = null;
        preparationPhase = false;
        
        plugin.getLanguageManager().broadcastMessage("general.game_over_new_game");
    }
    
    // 强制停止游戏
    public void forceStopGame() {
        remainingGameTime = 0;
        
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
        if (minuteGlowingTask != null) {
            minuteGlowingTask.cancel();
        }
        if (ghostToHumanTask != null) {
            ghostToHumanTask.cancel();
        }
        
        // 隐藏Boss栏
        queueBossBar.setVisible(false);
        gameBossBar.setVisible(false);
        
        // 退还队列玩家金币
        for (UUID playerId : waitingPlayers) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                plugin.getEconomyManager().refundEntryFee(player);
                player.sendMessage(plugin.getLanguageManager().getMessage("general.force_stop_refund"));
            }
        }
        waitingPlayers.clear();
        
        // 先通知所有游戏玩家
        for (UUID playerId : new ArrayList<>(plugin.getPlayerManager().getAllPlayers())) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.sendMessage(plugin.getLanguageManager().getMessage("general.force_stop_broadcast"));
            }
        }
        
        // 停止心跳声效果
        plugin.getHeartbeatManager().stopHeartbeat();
        
        // 然后清理数据并恢复所有玩家状态
        plugin.getPlayerManager().cleanup();
        
        // 重置游戏状态
        gameState = GameState.STOPPED;
        currentGameId = null;
        preparationPhase = false;
        
        plugin.getLanguageManager().broadcastMessage("general.force_stop_admin");
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
    
    /**
     * 发送游戏开始标题
     */
    private void sendGameStartTitles() {
        // 获取当前区域名称
        String areaName = plugin.getAreaManager().getSelectedAreaName();
        String welcomeMessage = "";
        
        if (areaName != null && !areaName.isEmpty()) {
            welcomeMessage = plugin.getLanguageManager().getMessage("game.welcome_area", areaName);
        }
        
        List<UUID> allPlayers = plugin.getPlayerManager().getAllPlayers();
        
        for (UUID playerId : allPlayers) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                PlayerManager.PlayerRole role = plugin.getPlayerManager().getPlayerRole(playerId);
                
                if (role == PlayerManager.PlayerRole.HUMAN) {
                    plugin.getLanguageManager().sendTitle(player, "game.started-human", welcomeMessage);
                } else if (role == PlayerManager.PlayerRole.GHOST_MOTHER || role == PlayerManager.PlayerRole.GHOST_NORMAL) {
                    plugin.getLanguageManager().sendTitle(player, "game.started-ghost", welcomeMessage);
                }
            }
        }
    }
    
    /**
     * 发送感染标题
     */
    public void sendInfectedTitle(Player player) {
        plugin.getLanguageManager().sendTitle(player, "role.infected", "");
    }
    
    /**
     * 发送转化标题
     */
    public void sendConversionTitle(Player player) {
        plugin.getLanguageManager().sendTitle(player, "role.converted", "");
    }
    
    /**
     * 发送游戏结束标题
     */
    public void sendGameEndTitle(boolean humanWin) {
        String titleKey = humanWin ? "broadcast.human-win" : "broadcast.ghost-win";
        String subtitleKey = "game.ended";
        
        List<UUID> allPlayers = plugin.getPlayerManager().getAllPlayers();
        
        for (UUID playerId : allPlayers) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                plugin.getLanguageManager().sendTitle(player, titleKey, subtitleKey);
            }
        }
        
        // 同时发送给观战者（暂时取消观战系统）
        // for (UUID spectatorId : plugin.getSpectatorManager().getSpectators()) {
        //     Player spectator = Bukkit.getPlayer(spectatorId);
        //     if (spectator != null && spectator.isOnline()) {
        //         plugin.getLanguageManager().sendTitle(spectator, titleKey, subtitleKey);
        //     }
        // }
    }
    
    /**
     * 发送准备阶段倒计时标题
     */
    public void sendPreparationTitle(int timeLeft) {
        // 获取当前区域名称
        String areaName = plugin.getAreaManager().getSelectedAreaName();
        String welcomeMessage = "";
        
        if (areaName != null && !areaName.isEmpty()) {
            welcomeMessage = plugin.getLanguageManager().getMessage("game.welcome_area", areaName) + "\n" +
                    plugin.getLanguageManager().getMessage("game.welcome_hint");
        }
        
        String title = plugin.getLanguageManager().getMessage("game.starting");
        String subtitle = welcomeMessage + plugin.getLanguageManager().getMessage("time.preparation", timeLeft);
        
        List<UUID> allPlayers = plugin.getPlayerManager().getAllPlayers();
        
        for (UUID playerId : allPlayers) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.sendTitle(title, subtitle, 10, 70, 20);
            }
        }
    }
    
    /**
     * 获取游戏剩余时间（秒）
     */
    public int getRemainingGameTime() {
        return remainingGameTime;
    }
    
    /**
     * 检查是否在准备阶段
     */
    public boolean isPreparationPhase() {
        return preparationPhase;
    }
    
    /**
     * 开始每分钟高亮效果任务
     */
    private void startMinuteGlowingTask() {
        // 检查是否启用每分钟高亮效果
        if (!plugin.getConfigManager().isMinuteGlowingEnabled()) {
            plugin.getLogger().info("每分钟高亮效果已禁用");
            return;
        }
        
        int interval = plugin.getConfigManager().getMinuteGlowingInterval();
        int duration = plugin.getConfigManager().getMinuteGlowingDuration();
        
        plugin.getLogger().info("开始每分钟高亮效果任务，间隔: " + interval + "秒，持续时间: " + duration + "秒");
        
        minuteGlowingTask = new CancellableTask(plugin) {
            @Override
            public boolean execute() {
                if (gameState != GameState.RUNNING) {
                    return false;
                }
                
                // 给所有玩家应用高亮效果
                applyMinuteGlowingEffect();
                return true;
            }
        };
        minuteGlowingTask.startTimer(interval * 20L); // 转换为ticks
    }
    
    /**
     * 应用每分钟高亮效果
     */
    private void applyMinuteGlowingEffect() {
        int duration = plugin.getConfigManager().getMinuteGlowingDuration();
        int durationTicks = duration * 20;
        
        // 获取所有游戏中的玩家
        List<UUID> allPlayers = plugin.getPlayerManager().getAllPlayers();
        
        if (allPlayers.isEmpty()) {
            return;
        }
        
        plugin.getLogger().info("应用每分钟高亮效果，持续时间: " + duration + "秒，玩家数量: " + allPlayers.size());
        
        // 给所有玩家应用高亮效果
        for (UUID playerId : allPlayers) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.GLOWING,
                    durationTicks,
                    0, // 等级0
                    true, // 环境效果
                    true // 显示粒子
                ));
                
                // 发送提示消息
                player.sendMessage(plugin.getLanguageManager().getMessage("general.minute_glowing_get", duration));
            }
        }
        
        // 广播提示
        plugin.getLanguageManager().broadcastMessage("general.minute_glowing_broadcast", duration);
        
        // 发送屏幕居中字幕
        for (UUID playerId : allPlayers) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.sendTitle(
                    plugin.getLanguageManager().getMessage("general.minute_glowing_title"),
                    plugin.getLanguageManager().getMessage("general.minute_glowing_subtitle", duration),
                    10, 40, 10
                );
            }
        }
    }
    
    /**
     * 开始鬼转人类功能任务
     */
    private void startGhostToHumanTask() {
        // 检查是否启用鬼转人类功能
        if (!plugin.getConfigManager().isGhostToHumanEnabled()) {
            plugin.getLogger().info("鬼转人类功能已禁用");
            return;
        }
        
        int remainingTime = plugin.getConfigManager().getGhostToHumanRemainingTime();
        int gameDuration = plugin.getConfigManager().getGameDuration();
        int triggerTime = gameDuration - remainingTime;
        
        if (triggerTime <= 0) {
            plugin.getLogger().warning("鬼转人类触发时间无效，游戏时长: " + gameDuration + "秒，剩余时间: " + remainingTime + "秒");
            return;
        }
        
        plugin.getLogger().info("开始鬼转人类功能任务，将在游戏剩余 " + remainingTime + " 秒时触发");
        
        ghostToHumanTask = new CancellableTask(plugin) {
            @Override
            public boolean execute() {
                // 检查游戏是否还在进行中
                if (gameState != GameState.RUNNING) {
                    return false;
                }
                
                // 获取游戏剩余时间
                long elapsedTime = (System.currentTimeMillis() - gameStartTime) / 1000;
                long remainingSeconds = gameDuration - elapsedTime;
                
                // 检查是否到达触发时间
                if (remainingSeconds <= remainingTime) {
                    plugin.getLogger().info("触发鬼转人类功能，游戏剩余时间: " + remainingSeconds + "秒");
                    convertGhostsToHumans();
                    return false; // 只触发一次
                }
                return true;
            }
        };
        ghostToHumanTask.startTimer(20L); // 每秒检查一次
    }
    
    /**
     * 将鬼玩家转换为人类
     */
    private void convertGhostsToHumans() {
        // 获取所有非母体的鬼玩家
        List<UUID> ghostPlayers = new ArrayList<>();
        List<UUID> allPlayers = plugin.getPlayerManager().getAllPlayers();
        
        for (UUID playerId : allPlayers) {
            if (plugin.getPlayerManager().isGhost(playerId) && 
                !plugin.getPlayerManager().isMotherGhost(playerId)) {
                ghostPlayers.add(playerId);
            }
        }
        
        if (ghostPlayers.isEmpty()) {
            plugin.getLogger().info("没有可转换的非母体鬼玩家");
            return;
        }
        
        int convertCount = plugin.getConfigManager().getGhostToHumanCount();
        convertCount = Math.min(convertCount, ghostPlayers.size());
        
        // 随机选择要转换的鬼玩家
        Collections.shuffle(ghostPlayers);
        List<UUID> selectedGhosts = ghostPlayers.subList(0, convertCount);
        
        plugin.getLogger().info("准备转换 " + selectedGhosts.size() + " 名鬼玩家为人类");
        
        for (UUID ghostId : selectedGhosts) {
            Player player = Bukkit.getPlayer(ghostId);
            if (player != null && player.isOnline()) {
                convertGhostToHuman(player);
            }
        }
    }
    
    /**
     * 将单个鬼玩家转换为人类
     */
    private void convertGhostToHuman(Player player) {
        UUID playerId = player.getUniqueId();
        String playerName = player.getName();
        
        plugin.getLogger().info("开始转换鬼玩家 " + playerName + " 为人类");
        
        // 1. 将玩家角色设置为人类
        plugin.getPlayerManager().setPlayerRole(playerId, PlayerManager.PlayerRole.HUMAN);
        
        // 2. 发送消息给玩家
        player.sendMessage(plugin.getLanguageManager().getMessage("game.conversion_header"));
        player.sendMessage(plugin.getLanguageManager().getMessage("game.conversion_success", playerName));
        player.sendMessage(plugin.getLanguageManager().getMessage("game.conversion_adrenaline"));
        player.sendMessage(plugin.getLanguageManager().getMessage("game.conversion_items_cleared"));
        player.sendMessage(plugin.getLanguageManager().getMessage("game.conversion_footer"));
        
        // 3. 广播消息给所有玩家
        plugin.getLanguageManager().broadcastMessage("general.conversion_broadcast", playerName);
        
        // 4. 发送屏幕居中字幕
        player.sendTitle(
            plugin.getLanguageManager().getMessage("game.conversion_title"),
            plugin.getLanguageManager().getMessage("game.conversion_subtitle"),
            10, 60, 10
        );
        
        plugin.getLogger().info("成功转换鬼玩家 " + playerName + " 为人类");
    }
}