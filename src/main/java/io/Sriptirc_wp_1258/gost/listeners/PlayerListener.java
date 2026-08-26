package io.Sriptirc_wp_1258.gost.listeners;

import io.Sriptirc_wp_1258.gost.Gost;
import io.Sriptirc_wp_1258.gost.managers.PlayerManager;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerListener implements Listener {
    
    private final Gost plugin;
    
    public PlayerListener(Gost plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // 如果玩家在游戏中，强制离开
        if (plugin.getPlayerManager().getAllPlayers().contains(player.getUniqueId())) {
            plugin.getPlayerManager().leaveGame(player);
        }
        
        // 如果玩家在队列中，强制离开队列
        if (plugin.getGameManager().getWaitingPlayersCount() > 0) {
            plugin.getGameManager().leaveQueue(player);
        }
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        // 检查玩家是否是旁观者
        if (plugin.getDivineGuardianManager().isSpectator(playerId)) {
            // 保持旁观模式
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage("§c§l[游戏结束] §c你已被淘汰，处于旁观模式");
            return;
        }
        
        // 如果玩家在游戏中，传送到游戏区域
        if (plugin.getPlayerManager().getAllPlayers().contains(playerId)) {
            if (plugin.getConfigManager().isAutoTeleportEnabled()) {
                io.Sriptirc_wp_1258.gost.managers.AreaManager.GameArea selectedArea = plugin.getAreaManager().getSelectedArea();
                if (selectedArea != null) {
                    plugin.getAreaManager().teleportPlayerToArea(player, selectedArea);
                }
            }
            
            // 重新应用游戏状态
            plugin.getPlayerManager().applyGameState(player);
        }
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        
        // 如果玩家在游戏中，禁止丢弃物品
        if (plugin.getPlayerManager().getAllPlayers().contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "游戏期间禁止丢弃物品！");
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        
        // 如果玩家在游戏中，检查伤害来源
        if (plugin.getPlayerManager().getAllPlayers().contains(player.getUniqueId())) {
            // 允许PVP伤害（用于感染判定）
            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK ||
                event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
                // PVP伤害由感染监听器处理
                return;
            }
            
            // 禁止其他类型的伤害
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        
        // 如果玩家在游戏中，锁定饱食度
        if (plugin.getPlayerManager().getAllPlayers().contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.setFoodLevel(20);
            player.setSaturation(20);
        }
    }
    
    @EventHandler
    public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (!plugin.getPlayerManager().getAllPlayers().contains(player.getUniqueId())) return;
        
        org.bukkit.inventory.ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == org.bukkit.Material.AIR || !item.hasItemMeta()) return;
        
        String name = item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : "";
        if (name.isEmpty()) return;
        
        // 特殊道具强制放第一格（收割者、神之救赎）
        if (name.contains("收割者") || name.contains("神之救赎")) {
            int slot = event.getSlot();
            if (slot != 0) {
                org.bukkit.inventory.ItemStack first = player.getInventory().getItem(0);
                if (first == null || first.getType() == org.bukkit.Material.AIR) {
                    // 第一格为空，直接放
                    player.getInventory().setItem(0, item);
                    event.setCancelled(true);
                } else {
                    // 第一格有道具：尝试将原道具移到空闲格，满则替换
                    int emptySlot = player.getInventory().firstEmpty();
                    if (emptySlot != -1 && emptySlot != 0) {
                        player.getInventory().setItem(emptySlot, first);
                    }
                    player.getInventory().setItem(0, item);
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.GOLD + "特殊道具已强制放置到第一格！");
                }
            }
            return;
        }
        
        // 重复检查：人鬼通用道具允许重复，专属道具不允许重复
        boolean isUniversal = name.contains("凝冰球") || name.contains("传送珍珠") || 
                              name.contains("漂浮药水") || name.contains("臭牛排") || 
                              name.contains("冲刺矛");
        // 不可积攒道具（专属道具）：肾上腺素、狂暴药水、控魂术、灵魂探测器、第二次机会
        if (!isUniversal) {
            for (org.bukkit.inventory.ItemStack inv : player.getInventory().getContents()) {
                if (inv != null && inv.hasItemMeta() && inv.getItemMeta().hasDisplayName() &&
                    inv.getItemMeta().getDisplayName().equals(name) && inv != item) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "你不允许携带两个相同道具！");
                    return;
                }
            }
        }
        
        // 背包区域（slot >= 9）不允许放道具，强制移回物品栏
        int slot = event.getSlot();
        if (slot >= 9) {
            event.setCancelled(true);
            boolean placed = false;
            for (int i = 0; i < 9; i++) {
                org.bukkit.inventory.ItemStack s = player.getInventory().getItem(i);
                if (s == null || s.getType() == org.bukkit.Material.AIR) {
                    player.getInventory().setItem(i, item);
                    event.getClickedInventory().setItem(slot, null);
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                player.sendMessage(ChatColor.RED + "物品栏已满！道具无法放入背包！");
            }
        }
    }
}