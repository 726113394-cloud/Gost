package io.Sriptirc_wp_1258.gost.platform;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * 平台调度器抽象
 * 统一处理 Paper/Folia 的调度器差异
 */
public abstract class PlatformScheduler {
    
    /**
     * 任务句柄，用于取消任务
     */
    public interface TaskHandle {
        void cancel();
        boolean isCancelled();
    }
    
    /**
     * 检测当前平台
     * @return "paper" 或 "folia"
     */
    public static String getPlatform() {
        String name = Bukkit.getName();
        if (name.equalsIgnoreCase("Folia")) {
            return "folia";
        }
        return "paper";
    }
    
    /**
     * 检测是否运行在 Folia
     */
    public static boolean isFolia() {
        return "folia".equals(getPlatform());
    }
    
    /**
     * 创建调度器实例
     */
    public static PlatformScheduler create() {
        if (isFolia()) {
            return new FoliaScheduler();
        }
        return new PaperScheduler();
    }
    
    /**
     * 延迟执行任务（玩家区域）
     */
    public abstract TaskHandle runTaskLater(Player player, long delay, Runnable task);
    
    /**
     * 延迟执行任务（全局）
     */
    public abstract TaskHandle runTaskLater(long delay, Runnable task);
    
    /**
     * 定时执行任务（玩家区域）
     */
    public abstract TaskHandle runTaskTimer(Player player, long delay, long period, Runnable task);
    
    /**
     * 定时执行任务（全局）
     */
    public abstract TaskHandle runTaskTimer(long delay, long period, Runnable task);
}
