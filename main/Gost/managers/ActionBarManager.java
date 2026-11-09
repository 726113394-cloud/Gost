package io.Sriptirc_wp_1258.gost.managers;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActionBarManager {
    
    private final Gost plugin;
    private final Map<UUID, String> actionBarMessages = new HashMap<>();
    private final Map<UUID, ServerCompat.Cancellable> actionBarTasks = new HashMap<>();
    
    public ActionBarManager(Gost plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 发送ActionBar消息给玩家
     * @param player 目标玩家
     * @param message 消息内容
     * @param duration 持续时间（秒），0表示只发送一次
     */
    public void sendActionBar(Player player, String message, int duration) {
        UUID playerId = player.getUniqueId();
        
        // 取消之前的任务
        cancelActionBar(playerId);
        
        // 设置消息
        actionBarMessages.put(playerId, message);
        
        // 立即发送一次
        sendNow(player, message);
        
        // 如果需要持续显示，创建定时任务
        if (duration > 0) {
            final int[] remaining = {duration};
            final ServerCompat.Cancellable[] holder = new ServerCompat.Cancellable[1];
            holder[0] = ServerCompat.runTimerForEntity(player, plugin, () -> {
                if (!player.isOnline() || !actionBarMessages.containsKey(playerId)) {
                    stop(playerId, holder[0]);
                    return;
                }
                
                // 每秒发送一次
                sendNow(player, actionBarMessages.get(playerId));
                
                remaining[0]--;
                if (remaining[0] <= 0) {
                    actionBarMessages.remove(playerId);
                    stop(playerId, holder[0]);
                }
            }, 20L, 20L);
            
            if (holder[0] != null) {
                actionBarTasks.put(playerId, holder[0]);
            }
        }
    }
    
    private void stop(UUID playerId, ServerCompat.Cancellable task) {
        if (task != null) task.cancel();
        actionBarTasks.remove(playerId);
    }
    
    /** 尚未探测 */
    private static final int STRATEGY_UNKNOWN = 0;
    /** Paper / Adventure：Player#sendActionBar(Component) */
    private static final int STRATEGY_ADVENTURE = 1;
    /** BungeeCord 聊天通道：Player.Spigot#sendMessage(ChatMessageType.ACTION_BAR, ...) */
    private static final int STRATEGY_BUNGEE = 2;
    /** 兜底：退化为聊天栏消息 */
    private static final int STRATEGY_CHAT = 3;

    private static volatile int actionBarStrategy = STRATEGY_UNKNOWN;

    /**
     * 当前实际使用的 ActionBar 通道名（供自检显示）。
     */
    public static String activeStrategy() {
        switch (actionBarStrategy) {
            case STRATEGY_ADVENTURE: return "adventure";
            case STRATEGY_BUNGEE: return "bungee";
            case STRATEGY_CHAT: return "chat-fallback";
            default: return "not-probed";
        }
    }

    /**
     * 立即发送ActionBar消息（不存储、不改状态）。
     *
     * <p>按优先级探测并缓存通道，避免像 2.3.3 那样直接调 Paper 专有方法导致
     * Spigot 上抛 {@code NoSuchMethodError}：</p>
     * <ol>
     *   <li>Adventure（Paper/Purpur 新版）</li>
     *   <li>BungeeCord 聊天通道（Spigot 传统路径，新版 Paper 正在移除）</li>
     *   <li>聊天栏兜底</li>
     * </ol>
     * <p>探测只在首次调用做一次，之后直接用缓存结果。</p>
     */
    public static void sendNow(Player player, String message) {
        if (player == null || message == null || message.isEmpty()) return;
        if (!player.isOnline()) return;

        switch (actionBarStrategy) {
            case STRATEGY_ADVENTURE:
                sendAdventure(player, message);
                return;
            case STRATEGY_BUNGEE:
                sendBungee(player, message);
                return;
            case STRATEGY_CHAT:
                player.sendMessage(message);
                return;
            default:
                break;
        }

        // 首次调用：按优先级探测
        if (tryAdventure(player, message)) {
            actionBarStrategy = STRATEGY_ADVENTURE;
            return;
        }
        if (tryBungee(player, message)) {
            actionBarStrategy = STRATEGY_BUNGEE;
            return;
        }
        actionBarStrategy = STRATEGY_CHAT;
        player.sendMessage(message);
    }

    /** Adventure 通道（反射，避免编译期依赖新版 API） */
    private static boolean tryAdventure(Player player, String message) {
        try {
            Class<?> componentClass = Class.forName("net.kyori.adventure.text.Component");
            Class<?> serializerClass =
                Class.forName("net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer");
            Object serializer = serializerClass.getMethod("legacySection").invoke(null);
            Object component = serializerClass
                .getMethod("deserialize", String.class)
                .invoke(serializer, message);
            player.getClass()
                .getMethod("sendActionBar", componentClass)
                .invoke(player, component);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    private static void sendAdventure(Player player, String message) {
        if (!tryAdventure(player, message)) {
            // 运行期退化了（极罕见），改为 Bungee 或聊天
            if (tryBungee(player, message)) {
                actionBarStrategy = STRATEGY_BUNGEE;
                return;
            }
            actionBarStrategy = STRATEGY_CHAT;
            player.sendMessage(message);
        }
    }

    private static boolean tryBungee(Player player, String message) {
        try {
            player.spigot().sendMessage(
                net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                net.md_5.bungee.api.chat.TextComponent.fromLegacyText(message));
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    private static void sendBungee(Player player, String message) {
        if (!tryBungee(player, message)) {
            if (tryAdventure(player, message)) {
                actionBarStrategy = STRATEGY_ADVENTURE;
                return;
            }
            actionBarStrategy = STRATEGY_CHAT;
            player.sendMessage(message);
        }
    }
    
    /**
     * 取消玩家的ActionBar显示
     */
    public void cancelActionBar(UUID playerId) {
        ServerCompat.Cancellable task = actionBarTasks.remove(playerId);
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
        actionBarMessages.remove(playerId);
    }
    
    /**
     * 发送道具使用提示
     */
    public void sendItemUsageHint(Player player, String itemName, String usage) {
        String message = ChatColor.YELLOW + "道具: " + ChatColor.GOLD + itemName + 
                        ChatColor.YELLOW + " - " + ChatColor.GRAY + usage;
        sendActionBar(player, message, 5); // 显示5秒
    }
    
    /**
     * 发送肾上腺素使用提示
     */
    public void sendAdrenalineHint(Player player) {
        sendItemUsageHint(player, "肾上腺素", "右键使用获得速度提升");
    }
    
    /**
     * 发送狂暴药水使用提示
     */
    public void sendFrenzyPotionHint(Player player) {
        sendItemUsageHint(player, "狂暴药水", "右键使用获得速度提升");
    }
    
    /**
     * 发送凝冰球使用提示
     */
    public void sendIceBallHint(Player player) {
        sendItemUsageHint(player, "凝冰球", "右键投掷减速鬼玩家");
    }
    
    /**
     * 发送控魂术使用提示
     */
    public void sendSoulControlHint(Player player) {
        sendItemUsageHint(player, "控魂术", "右键使用冻结所有鬼");
    }
    
    /**
     * 发送灵魂探测器使用提示
     */
    public void sendSoulDetectorHint(Player player) {
        sendItemUsageHint(player, "灵魂探测器", "右键使用使所有玩家发光25秒");
    }
    
    public void sendStinkySteakHint(Player player) {
        sendItemUsageHint(player, "臭牛排", "右键食用获得速度II效果和发光效果");
    }
    
    public void sendTeleportPearlHint(Player player) {
        sendItemUsageHint(player, "传送珍珠", "右键投掷进行传送");
    }
    
    /**
     * 清理所有ActionBar
     */
    public void cleanup() {
        for (ServerCompat.Cancellable task : actionBarTasks.values()) {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
        }
        actionBarTasks.clear();
        actionBarMessages.clear();
    }
}
