package io.Sriptirc_wp_1258.gost.managers;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * 服务端平台兼容层（Spigot / Paper / Purpur / Bukkit / Folia）
 *
 * <p>支持矩阵：</p>
 * <ul>
 *   <li><b>Bukkit / Spigot / Paper / Purpur</b>：完整支持（同一套 Bukkit API）</li>
 *   <li><b>Folia</b>：调度兼容（Region/Global/Async），玩法逻辑为单线程假设，属“尽力兼容”</li>
 *   <li><b>Sponge</b>：不支持（另一套 API，需独立移植）</li>
 *   <li><b>BungeeCord / Velocity / Waterfall</b>：不适用（代理端，本插件是游戏后端玩法插件）</li>
 * </ul>
 */
public final class ServerCompat {

    public enum Platform {
        FOLIA,
        PURPUR,
        PAPER,
        SPIGOT,
        BUKKIT,
        UNKNOWN
    }

    private static volatile Platform cachedPlatform;
    private static volatile boolean foliaDetected;

    private ServerCompat() {}

    /** 是否 Folia（多线程区域调度） */
    public static boolean isFolia() {
        if (cachedPlatform == null) {
            detect();
        }
        return foliaDetected;
    }

    /** 当前平台 */
    public static Platform platform() {
        if (cachedPlatform == null) {
            detect();
        }
        return cachedPlatform;
    }

    /** 平台显示名 */
    public static String platformName() {
        return platform().name();
    }

    /** 完整服务端描述（日志用） */
    public static String describe() {
        try {
            return Bukkit.getName() + " " + Bukkit.getVersion() + " [" + platformName() + "]";
        } catch (Throwable t) {
            return platformName();
        }
    }

    private static synchronized void detect() {
        if (cachedPlatform != null) return;
        boolean folia = classExists("io.papermc.paper.threadedregions.RegionizedServer")
                || classExists("io.papermc.paper.threadedregions.RegionizedWorld")
                || hasMethod(Server.class, "getGlobalRegionScheduler");
        foliaDetected = folia;

        String name = "";
        try {
            name = String.valueOf(Bukkit.getName()).toLowerCase(Locale.ROOT);
        } catch (Throwable ignored) {
        }

        if (folia) {
            cachedPlatform = Platform.FOLIA;
        } else if (name.contains("purpur")) {
            cachedPlatform = Platform.PURPUR;
        } else if (name.contains("paper") || name.contains("pufferfish") || name.contains("folia")) {
            cachedPlatform = Platform.PAPER;
        } else if (name.contains("spigot")) {
            cachedPlatform = Platform.SPIGOT;
        } else if (name.contains("bukkit") || name.contains("craftbukkit")) {
            cachedPlatform = Platform.BUKKIT;
        } else if (classExists("com.destroystokyo.paper.PaperConfig") || hasMethod(Server.class, "getPaperConfig")) {
            cachedPlatform = Platform.PAPER;
        } else {
            cachedPlatform = Platform.UNKNOWN;
        }
    }

    private static boolean classExists(String fqcn) {
        try {
            Class.forName(fqcn, false, ServerCompat.class.getClassLoader());
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    private static boolean hasMethod(Class<?> type, String name) {
        try {
            type.getMethod(name);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * 在合适的线程模型上延迟执行。
     * Bukkit/Spigot/Paper/Purpur：BukkitScheduler
     * Folia：GlobalRegionScheduler（反射，避免编译期绑定 Folia API）
     */
    public static void runLater(Plugin plugin, Runnable task, long delayTicks) {
        if (task == null) return;
        if (isFolia() && SchedulerCompat.runOnFoliaLater(plugin, task, delayTicks)) {
            return;
        }
        try {
            Bukkit.getScheduler().runTaskLater(plugin, task, Math.max(0L, delayTicks));
        } catch (Throwable t) {
            // Folia 上 Bukkit scheduler 可能拒绝 → 尝试 Folia 兜底
            SchedulerCompat.runOnFoliaLater(plugin, task, delayTicks);
        }
    }

    /** 立即（下一刻）执行 */
    public static void run(Plugin plugin, Runnable task) {
        runLater(plugin, task, 0L);
    }

    /**
     * 周期任务。
     * @return 可取消的包装；失败返回 null
     */
    public static Cancellable runTimer(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        if (task == null) return null;
        if (isFolia()) {
            Cancellable c = SchedulerCompat.runOnFoliaTimer(plugin, task, delayTicks, periodTicks);
            if (c != null) return c;
        }
        try {
            org.bukkit.scheduler.BukkitTask t =
                Bukkit.getScheduler().runTaskTimer(plugin, task, Math.max(0L, delayTicks), Math.max(1L, periodTicks));
            return new BukkitCancellable(t);
        } catch (Throwable t) {
            Cancellable c = SchedulerCompat.runOnFoliaTimer(plugin, task, delayTicks, periodTicks);
            return c;
        }
    }

    /** 异步执行（网络/IO，不碰世界） */
    public static void runAsync(Plugin plugin, Runnable task) {
        if (task == null) return;
        if (isFolia() && SchedulerCompat.runOnFoliaAsync(plugin, task)) {
            return;
        }
        try {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        } catch (Throwable t) {
            SchedulerCompat.runOnFoliaAsync(plugin, task);
        }
    }

    /**
     * 在玩家所在区域/实体调度器上执行（Folia 必须；Bukkit 直接执行）。
     * 用于 teleport / setGameMode / inventory 等必须在实体线程的操作。
     */
    public static void runForPlayer(org.bukkit.entity.Player player, Runnable task) {
        if (player == null || task == null) return;
        if (isFolia() && SchedulerCompat.runOnEntityScheduler(player, task)) {
            return;
        }
        try {
            task.run();
        } catch (Throwable t) {
            run(player.getServer().getPluginManager().getPlugin("Gost"), task);
        }
    }

    /** 安全传送（Folia 用实体调度器） */
    public static void teleportSafely(org.bukkit.entity.Player player, org.bukkit.Location loc) {
        if (player == null || loc == null) return;
        runForPlayer(player, () -> {
            try {
                player.teleport(loc);
            } catch (Throwable ignored) {
            }
        });
    }

    /** 安全切换游戏模式 */
    public static void setGameModeSafely(org.bukkit.entity.Player player, org.bukkit.GameMode mode) {
        if (player == null || mode == null) return;
        runForPlayer(player, () -> {
            try {
                player.setGameMode(mode);
            } catch (Throwable ignored) {
            }
        });
    }

    /** 可取消句柄 */
    public interface Cancellable {
        void cancel();
        boolean isCancelled();
    }

    private static final class BukkitCancellable implements Cancellable {
        private final org.bukkit.scheduler.BukkitTask task;
        BukkitCancellable(org.bukkit.scheduler.BukkitTask task) { this.task = task; }
        @Override public void cancel() {
            try { if (task != null) task.cancel(); } catch (Throwable ignored) {}
        }
        @Override public boolean isCancelled() {
            try { return task == null || task.isCancelled(); } catch (Throwable t) { return true; }
        }
    }
}
