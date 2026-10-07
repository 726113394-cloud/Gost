package io.Sriptirc_wp_1258.gost.managers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Folia 调度反射适配（避免编译期依赖 Folia API）。
 * 仅在 ServerCompat.isFolia() 时调用；失败返回 false 交给上层回退。
 */
final class SchedulerCompat {

    private SchedulerCompat() {}

    private static Object globalScheduler() {
        try {
            Method m = Bukkit.getServer().getClass().getMethod("getGlobalRegionScheduler");
            return m.invoke(Bukkit.getServer());
        } catch (Throwable t) {
            return null;
        }
    }

    /** Folia EntityScheduler（挂在 Player 上） */
    static boolean runOnEntityScheduler(org.bukkit.entity.Player player, Runnable task) {
        try {
            Method getScheduler = player.getClass().getMethod("getScheduler");
            Object entitySched = getScheduler.invoke(player);
            if (entitySched == null) return false;
            // EntityScheduler.run(Plugin, Consumer, Runnable retired)
            Method run = entitySched.getClass().getMethod("run", org.bukkit.plugin.Plugin.class,
                java.util.function.Consumer.class, Runnable.class);
            Plugin plugin = org.bukkit.Bukkit.getPluginManager().getPlugin("Gost");
            if (plugin == null) return false;
            run.invoke(entitySched, plugin,
                (java.util.function.Consumer<Object>) ignored -> task.run(),
                null);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    private static Object asyncScheduler() {
        try {
            Method m = Bukkit.getServer().getClass().getMethod("getAsyncScheduler");
            return m.invoke(Bukkit.getServer());
        } catch (Throwable t) {
            return null;
        }
    }

    /** Folia GlobalRegionScheduler.runDelayed */
    static boolean runOnFoliaLater(Plugin plugin, Runnable task, long delayTicks) {
        try {
            Object sched = globalScheduler();
            if (sched == null) return false;
            Method m = sched.getClass().getMethod("runDelayed", Plugin.class,
                java.util.function.Consumer.class, long.class);
            m.invoke(sched, plugin, (java.util.function.Consumer<Object>) ignored -> task.run(),
                Math.max(1L, delayTicks));
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    /** Folia GlobalRegionScheduler.runAtFixedRate → 返回可取消包装 */
    static ServerCompat.Cancellable runOnFoliaTimer(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        try {
            Object sched = globalScheduler();
            if (sched == null) return null;
            Method m = sched.getClass().getMethod("runAtFixedRate", Plugin.class,
                java.util.function.Consumer.class, long.class, long.class);
            Object handle = m.invoke(sched, plugin,
                (java.util.function.Consumer<Object>) ignored -> task.run(),
                Math.max(1L, delayTicks),
                Math.max(1L, periodTicks));
            return new ReflectCancellable(handle);
        } catch (Throwable t) {
            return null;
        }
    }

    /** Folia AsyncScheduler.runNow */
    static boolean runOnFoliaAsync(Plugin plugin, Runnable task) {
        try {
            Object sched = asyncScheduler();
            if (sched == null) return false;
            Method m = sched.getClass().getMethod("runNow", Plugin.class,
                java.util.function.Consumer.class);
            m.invoke(sched, plugin, (java.util.function.Consumer<Object>) ignored -> task.run());
            return true;
        } catch (Throwable first) {
            try {
                Object sched = asyncScheduler();
                if (sched == null) return false;
                Method m = sched.getClass().getMethod("runDelayed", Plugin.class,
                    java.util.function.Consumer.class, long.class, TimeUnit.class);
                m.invoke(sched, plugin, (java.util.function.Consumer<Object>) ignored -> task.run(),
                    1L, TimeUnit.MILLISECONDS);
                return true;
            } catch (Throwable second) {
                return false;
            }
        }
    }

    private static final class ReflectCancellable implements ServerCompat.Cancellable {
        private final Object handle;
        ReflectCancellable(Object handle) { this.handle = handle; }

        @Override
        public void cancel() {
            if (handle == null) return;
            try {
                Method m = handle.getClass().getMethod("cancel");
                m.invoke(handle);
            } catch (Throwable ignored) {
            }
        }

        @Override
        public boolean isCancelled() {
            if (handle == null) return true;
            try {
                Method m = handle.getClass().getMethod("isCancelled");
                Object v = m.invoke(handle);
                return Boolean.TRUE.equals(v);
            } catch (Throwable t) {
                return false;
            }
        }
    }
}
