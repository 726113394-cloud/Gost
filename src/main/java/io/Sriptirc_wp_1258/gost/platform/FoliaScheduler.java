package io.Sriptirc_wp_1258.gost.platform;

import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * Folia 平台的调度器实现
 * 使用 Folia 的 RegionScheduler 进行区域化调度
 */
public class FoliaScheduler extends PlatformScheduler {
    
    private final Gost plugin = Gost.getInstance();
    
    /**
     * Folia 任务句柄包装
     */
    private static class FoliaTaskHandle implements TaskHandle {
        private final ScheduledTask task;
        
        FoliaTaskHandle(ScheduledTask task) {
            this.task = task;
        }
        
        @Override
        public void cancel() {
            task.cancel();
        }
        
        @Override
        public boolean isCancelled() {
            return task.getExecutionState() == ScheduledTask.ExecutionState.CANCELLED ||
                   task.getExecutionState() == ScheduledTask.ExecutionState.CANCELLED_RUNNING;
        }
    }
    
    @Override
    public TaskHandle runTaskLater(Player player, long delay, Runnable task) {
        ScheduledTask st = plugin.getServer().getRegionScheduler().runDelayed(
            plugin, 
            player.getLocation().getWorld(),
            player.getLocation().getBlockX(),
            player.getLocation().getBlockZ(),
            ts -> task.run(),
            delay
        );
        return new FoliaTaskHandle(st);
    }
    
    @Override
    public TaskHandle runTaskLater(long delay, Runnable task) {
        ScheduledTask st = plugin.getServer().getGlobalRegionScheduler().runDelayed(
            plugin,
            ts -> task.run(),
            delay
        );
        return new FoliaTaskHandle(st);
    }
    
    @Override
    public TaskHandle runTaskTimer(Player player, long delay, long period, Runnable task) {
        ScheduledTask st = plugin.getServer().getRegionScheduler().runAtFixedRate(
            plugin,
            player.getLocation().getWorld(),
            player.getLocation().getBlockX(),
            player.getLocation().getBlockZ(),
            ts -> task.run(),
            delay,
            period
        );
        return new FoliaTaskHandle(st);
    }
    
    @Override
    public TaskHandle runTaskTimer(long delay, long period, Runnable task) {
        ScheduledTask st = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(
            plugin,
            ts -> task.run(),
            delay,
            period
        );
        return new FoliaTaskHandle(st);
    }
}
