package io.Sriptirc_wp_1258.gost.managers;

import io.Sriptirc_wp_1258.gost.Gost;
import io.Sriptirc_wp_1258.gost.platform.PlatformScheduler;

/**
 * 可取消的游戏任务
 * 用于替代 BukkitRunnable，支持 Folia 的 RegionScheduler
 */
public abstract class CancellableTask {
    
    private final Gost plugin;
    private PlatformScheduler.TaskHandle handle;
    
    public CancellableTask(Gost plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 执行任务逻辑
     * @return true 继续执行，false 停止任务
     */
    public abstract boolean execute();
    
    /**
     * 启动定时任务（每秒一次）
     */
    public void startTimer(long initialDelay) {
        handle = plugin.getPlatformScheduler().runTaskTimer(initialDelay, 20, this::run);
    }
    
    /**
     * 启动延迟任务
     */
    public void startLater(long delay) {
        handle = plugin.getPlatformScheduler().runTaskLater(delay, this::run);
    }
    
    /**
     * 运行任务
     */
    private void run() {
        if (!execute()) {
            cancel();
        }
    }
    
    /**
     * 取消任务
     */
    public void cancel() {
        if (handle != null) {
            handle.cancel();
        }
    }
    
    /**
     * 检查是否已取消
     */
    public boolean isCancelled() {
        return handle == null || handle.isCancelled();
    }
}
