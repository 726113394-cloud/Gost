package io.Sriptirc_wp_1258.gost.listeners;

import io.Sriptirc_wp_1258.gost.Gost;
import io.Sriptirc_wp_1258.gost.managers.SelectionManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 选区监听器
 * 监听玩家使用选区工具的操作
 */
public class SelectionListener implements Listener {
    
    private final Gost plugin;
    
    public SelectionListener(Gost plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        // 检查是否持有选区工具
        if (item == null || !isSelectionTool(item)) {
            return;
        }
        
        // 取消默认行为
        event.setCancelled(true);
        
        // 检查是否点击了方块
        if (event.getClickedBlock() == null) {
            return;
        }
        
        Block clickedBlock = event.getClickedBlock();
        
        // 根据点击类型设置不同的点
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            // 左键设置第一个点
            plugin.getSelectionManager().setPos1(player, clickedBlock.getLocation());
            player.sendMessage(ChatColor.GREEN + "已设置第一个点！");
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // 右键设置第二个点
            plugin.getSelectionManager().setPos2(player, clickedBlock.getLocation());
            player.sendMessage(ChatColor.GREEN + "已设置第二个点！");
        }
        
        // 显示选区粒子框（持续30秒）
        showSelectionParticles(player);
    }
    
    /**
     * 显示选区框定粒子效果（30秒）
     */
    private void showSelectionParticles(Player player) {
        io.Sriptirc_wp_1258.gost.managers.SelectionManager.PlayerSelection sel = plugin.getSelectionManager().getSelection(player);
        if (sel == null) return;
        org.bukkit.Location pos1 = sel.getPos1();
        org.bukkit.Location pos2 = sel.getPos2();
        if (pos1 == null || pos2 == null) return;
        
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        
        // 每5 tick刷新一次，持续30秒（600 ticks）
        int totalTicks = 600;
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= totalTicks || !player.isOnline()) return;
                // 沿12条边生成粒子
                for (int i = 0; i <= 8; i++) {
                    double t = i / 8.0;
                    // X方向边
                    player.spawnParticle(org.bukkit.Particle.FLAME, 
                        minX + (maxX - minX) * t, minY, minZ, 1, 0, 0, 0, 0);
                    player.spawnParticle(org.bukkit.Particle.FLAME,
                        minX + (maxX - minX) * t, maxY, maxZ, 1, 0, 0, 0, 0);
                    // Z方向边
                    player.spawnParticle(org.bukkit.Particle.FLAME,
                        minX, minY, minZ + (maxZ - minZ) * t, 1, 0, 0, 0, 0);
                    player.spawnParticle(org.bukkit.Particle.FLAME,
                        maxX, maxY, minZ + (maxZ - minZ) * t, 1, 0, 0, 0, 0);
                }
                ticks += 5;
                if (ticks >= totalTicks) {
                    player.sendMessage(ChatColor.GRAY + "选区粒子效果已结束");
                }
            }
        }, 0L, 5L);
    }
    
    /**
     * 检查物品是否是选区工具
     */
    private boolean isSelectionTool(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        
        // 获取配置的选区工具物品
        Material configMaterial;
        try {
            configMaterial = plugin.getSelectionManager().getSelectionTool().getType();
        } catch (Exception e) {
            // 如果选区管理器未完全初始化，使用默认值
            configMaterial = Material.MAGMA_CREAM;
        }
        
        // 检查物品类型
        if (item.getType() != configMaterial) {
            return false;
        }
        
        // 检查物品名称（可选）
        if (item.getItemMeta().hasDisplayName()) {
            String displayName = item.getItemMeta().getDisplayName();
            return displayName.contains("区域选择工具");
        }
        
        return true;
    }
}