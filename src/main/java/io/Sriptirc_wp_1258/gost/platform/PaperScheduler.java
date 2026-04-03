package io.Sriptirc_wp_1258.gost.platform;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * Paper 平台的调度器实现
 * 使用 Bukkit.getScheduler().runTask()
 */
public class PaperScheduler extends PlatformScheduler {
    
    private final Gost plugin = Gost.getInstance();
    
    /**
     * Paper 任务句柄包装
     */
    private static class PaperTaskHandle implements TaskHandle {
        private final BukkitTask task;
        
        PaperTaskHandle(BukkitTask task) {
            this.task = task;
        }
        
        @Override
        public void cancel() {
            task.cancel();
        }
        
        @Override
        public boolean isCancelled() {
            return task.isCancelled();
        }
    }
    
    @Override
    public TaskHandle runTaskLater(Player player, long delay, Runnable task) {
        return new PaperTaskHandle(plugin.getServer().getScheduler().runTaskLater(plugin, task, delay));
    }
    
    @Override
    public TaskHandle runTaskLater(long delay, Runnable task) {
        return new PaperTaskHandle(plugin.getServer().getScheduler().runTaskLater(plugin, task, delay));
    }
    
    @Override
    public TaskHandle runTaskTimer(Player player, long delay, long period, Runnable task) {
        return new PaperTaskHandle(plugin.getServer().getScheduler().runTaskTimer(plugin, task, delay, period));
    }
    
    @Override
    public TaskHandle runTaskTimer(long delay, long period, Runnable task) {
        return new PaperTaskHandle(plugin.getServer().getScheduler().runTaskTimer(plugin, task, delay, period));
    }
}
