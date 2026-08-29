package io.Sriptirc_wp_1258.gost.managers;

import io.Sriptirc_wp_1258.gost.Gost;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 局内饱食度管理器（v2.3.2）
 *
 * 功能：
 * 1. 游戏开局恢复所有玩家满饱食度（由 GameManager 开局逻辑完成）
 * 2. 阵容切换时饱食度回满（由 PlayerManager.setPlayerRole 完成）
 * 3. 猎魔人阶段所有玩家饱食度回满且不自然掉饥饿（由 enterDemonHunterPhase / PlayerListener 完成）
 * 4. 按住 Shift 蹲下恢复饱食度：
 *    - 必须持续按住蹲下 1 秒后才能开始恢复（不支持秒蹲）
 *    - 人类：每秒恢复 food.regenerate-per-second 点（默认1）
 *    - 鬼：每 food.ghost-regenerate-interval 秒恢复1点（默认0.8秒，比人类快）
 *    - 开始恢复时聊天框提示"开始恢复饱食度"
 */
public class SaturationManager implements Listener {

    private final Gost plugin;

    /** 玩家开始潜行的时间戳 */
    private final Map<UUID, Long> sneakStartTimes = new ConcurrentHashMap<>();
    /** 玩家下次恢复饱食度的时间戳 */
    private final Map<UUID, Long> nextRegenTimes = new ConcurrentHashMap<>();
    /** 已提示过"开始恢复饱食度"的玩家（防止重复提示） */
    private final Set<UUID> notifiedPlayers = ConcurrentHashMap.newKeySet();

    /** 持续潜行达到该毫秒数后才开始恢复（1秒，硬性要求不支持秒蹲） */
    private static final long SNEAK_HOLD_MS = 1000L;

    /** 定时任务执行间隔（0.2秒 = 4 tick），用于支持 0.8 秒的亚秒级恢复间隔 */
    private static final long TICK_MS = 200L;

    private BukkitRunnable task;

    public SaturationManager(Gost plugin) {
        this.plugin = plugin;
        startTask();
    }

    private void startTask() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        };
        task.runTaskTimer(plugin, TICK_MS / 50L, TICK_MS / 50L);
    }

    private void tick() {
        if (!plugin.getConfigManager().isFoodSystemEnabled()) {
            return;
        }
        if (sneakStartTimes.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();

        for (Map.Entry<UUID, Long> entry : sneakStartTimes.entrySet()) {
            UUID playerId = entry.getKey();
            Player player = Bukkit.getPlayer(playerId);
            if (player == null || !player.isOnline()) {
                continue;
            }
            // 不在对局中不生效
            if (!plugin.getPlayerManager().getAllPlayers().contains(playerId)) {
                continue;
            }
            // 已经松开蹲下（防呆，正常会通过事件移除）
            if (!player.isSneaking()) {
                sneakStartTimes.remove(playerId);
                nextRegenTimes.remove(playerId);
                continue;
            }
            // 未按住满 1 秒，不恢复（不支持秒蹲）
            long elapsed = now - entry.getValue();
            if (elapsed < SNEAK_HOLD_MS) {
                continue;
            }
            // 首次达到 1 秒：提示"开始恢复饱食度"
            if (notifiedPlayers.add(playerId)) {
                player.sendMessage(ChatColor.GREEN + "§a开始恢复饱食度");
            }
            // 按阵营计算恢复间隔与单次恢复量
            long interval = getRegenInterval(playerId);
            int amount = getRegenAmount(playerId);

            long next = nextRegenTimes.getOrDefault(playerId, 0L);
            if (now >= next) {
                int current = player.getFoodLevel();
                if (current < 20) {
                    player.setFoodLevel(Math.min(20, current + amount));
                }
                nextRegenTimes.put(playerId, now + interval);
            }
        }
    }

    /** 获取该玩家的恢复间隔（毫秒） */
    private long getRegenInterval(UUID playerId) {
        if (plugin.getPlayerManager().isGhost(playerId)) {
            // 鬼：每 ghost-regenerate-interval 秒恢复1点（默认0.8秒）
            double interval = plugin.getConfigManager().getFoodGhostRegenerateInterval();
            return (long) (interval * 1000L);
        }
        // 人类：每秒恢复 regenerate-per-second 点
        int perSecond = plugin.getConfigManager().getFoodRegeneratePerSecond();
        return 1000L / Math.max(1, perSecond);
    }

    /** 获取该玩家的单次恢复量 */
    private int getRegenAmount(UUID playerId) {
        if (plugin.getPlayerManager().isGhost(playerId)) {
            return 1; // 鬼每 0.8 秒固定恢复 1 点
        }
        return plugin.getConfigManager().getFoodRegeneratePerSecond(); // 人类每秒恢复 N 点
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        if (event.isSneaking()) {
            // 开始潜行：记录开始时间
            sneakStartTimes.put(playerId, System.currentTimeMillis());
        } else {
            // 松开潜行：清除记录，重置提示状态
            sneakStartTimes.remove(playerId);
            nextRegenTimes.remove(playerId);
            notifiedPlayers.remove(playerId);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        sneakStartTimes.remove(playerId);
        nextRegenTimes.remove(playerId);
        notifiedPlayers.remove(playerId);
    }

    /** 游戏结束时清理数据 */
    public void cleanup() {
        sneakStartTimes.clear();
        nextRegenTimes.clear();
        notifiedPlayers.clear();
    }

    /** 插件禁用时停止任务 */
    public void shutdown() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        cleanup();
    }
}
