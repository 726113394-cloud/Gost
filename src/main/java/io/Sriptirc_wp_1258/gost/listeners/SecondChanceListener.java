package io.Sriptirc_wp_1258.gost.listeners;

import io.Sriptirc_wp_1258.gost.Gost;
import io.Sriptirc_wp_1258.gost.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SecondChanceListener implements Listener {
    
    private final Gost plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    
    public SecondChanceListener(Gost plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // 检查是否是人类玩家被鬼玩家攻击
        if (!(event.getEntity() instanceof Player humanPlayer)) {
            return;
        }
        
        if (!(event.getDamager() instanceof Player ghostPlayer)) {
            return;
        }
        
        // 检查玩家角色
        PlayerManager.PlayerRole humanRole = plugin.getPlayerManager().getPlayerRole(humanPlayer.getUniqueId());
        PlayerManager.PlayerRole ghostRole = plugin.getPlayerManager().getPlayerRole(ghostPlayer.getUniqueId());
        
        if (humanRole != PlayerManager.PlayerRole.HUMAN || ghostRole != PlayerManager.PlayerRole.GHOST) {
            return;
        }
        
        // 检查人类玩家是否拥有"一次机会"道具
        ItemStack secondChanceItem = findSecondChanceItem(humanPlayer);
        if (secondChanceItem == null) {
            return;
        }
        
        // 检查冷却时间
        UUID humanId = humanPlayer.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long cooldownTime = plugin.getConfigManager().getSecondChanceCooldown() * 1000L;
        
        if (cooldowns.containsKey(humanId)) {
            long lastUseTime = cooldowns.get(humanId);
            long timeLeft = (lastUseTime + cooldownTime) - currentTime;
            
            if (timeLeft > 0) {
                // 还在冷却中
                humanPlayer.sendMessage(ChatColor.RED + "一次机会还在冷却中！剩余时间: " + (timeLeft / 1000) + "秒");
                return;
            }
        }
        
        // 触发一次机会效果
        triggerSecondChance(humanPlayer, ghostPlayer, secondChanceItem);
        
        // 设置冷却时间
        cooldowns.put(humanId, currentTime);
        
        // 取消伤害事件（抵挡感染）
        event.setCancelled(true);
        
        // 发送屏幕居中字幕
        sendTitleMessages(humanPlayer, ghostPlayer);
    }
    
    private ItemStack findSecondChanceItem(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.TOTEM_OF_UNDYING) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasDisplayName() && 
                    meta.getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&6一次机会"))) {
                    return item;
                }
            }
        }
        return null;
    }
    
    private void triggerSecondChance(Player humanPlayer, Player ghostPlayer, ItemStack secondChanceItem) {
        // 应用效果
        plugin.getItemManager().applySecondChanceEffect(humanPlayer, ghostPlayer);
        
        // 移除道具（因为是一次性道具）
        removeSecondChanceItem(humanPlayer, secondChanceItem);
        
        // 发送对话框消息
        sendDialogMessages(humanPlayer, ghostPlayer);
        
        // 发送公告
        Bukkit.broadcastMessage(ChatColor.YELLOW + "════════════════════════════════");
        Bukkit.broadcastMessage(ChatColor.GOLD + "✨ " + humanPlayer.getName() + " 使用了一次机会！");
        Bukkit.broadcastMessage(ChatColor.GOLD + "✨ 成功抵挡了 " + ghostPlayer.getName() + " 的感染！");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "════════════════════════════════");
    }
    
    private void removeSecondChanceItem(Player player, ItemStack itemToRemove) {
        // 找到并移除道具
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && item.isSimilar(itemToRemove)) {
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    player.getInventory().setItem(i, null);
                }
                break;
            }
        }
    }
    
    private void sendTitleMessages(Player humanPlayer, Player ghostPlayer) {
        // 给人类玩家发送标题
        humanPlayer.sendTitle(
            ChatColor.GOLD + "✨ 一次机会触发！",
            ChatColor.GREEN + "成功抵挡感染！获得速度和高亮效果",
            10, 40, 10
        );
        
        // 给鬼玩家发送标题
        ghostPlayer.sendTitle(
            ChatColor.RED + "⚠ 感染被抵挡！",
            ChatColor.YELLOW + "目标使用了一次机会！你被减速了",
            10, 40, 10
        );
    }
    
    private void sendDialogMessages(Player humanPlayer, Player ghostPlayer) {
        // 给人类玩家的对话框消息
        humanPlayer.sendMessage(ChatColor.YELLOW + "════════════════════════════════");
        humanPlayer.sendMessage(ChatColor.GOLD + "             一次机会触发！");
        humanPlayer.sendMessage("");
        humanPlayer.sendMessage(ChatColor.GREEN + "✓ 成功抵挡了 " + ghostPlayer.getName() + " 的感染");
        humanPlayer.sendMessage(ChatColor.GREEN + "✓ 获得速度II效果 " + plugin.getConfigManager().getSecondChanceHumanSpeedDuration() + "秒");
        humanPlayer.sendMessage(ChatColor.GREEN + "✓ 获得高亮效果 " + plugin.getConfigManager().getSecondChanceHumanGlowingDuration() + "秒");
        humanPlayer.sendMessage(ChatColor.YELLOW + "════════════════════════════════");
        
        // 给鬼玩家的对话框消息
        ghostPlayer.sendMessage(ChatColor.YELLOW + "════════════════════════════════");
        ghostPlayer.sendMessage(ChatColor.RED + "             感染被抵挡！");
        ghostPlayer.sendMessage("");
        ghostPlayer.sendMessage(ChatColor.RED + "✗ 对 " + humanPlayer.getName() + " 的感染被抵挡");
        ghostPlayer.sendMessage(ChatColor.RED + "✗ 获得缓慢I效果 " + plugin.getConfigManager().getSecondChanceGhostSlowDuration() + "秒");
        ghostPlayer.sendMessage(ChatColor.YELLOW + "════════════════════════════════");
    }
    
    // 清理冷却时间
    public void clearCooldowns() {
        cooldowns.clear();
    }
    
    // 获取剩余冷却时间
    public long getRemainingCooldown(UUID playerId) {
        if (!cooldowns.containsKey(playerId)) {
            return 0;
        }
        
        long lastUseTime = cooldowns.get(playerId);
        long currentTime = System.currentTimeMillis();
        long cooldownTime = plugin.getConfigManager().getSecondChanceCooldown() * 1000L;
        long timeLeft = (lastUseTime + cooldownTime) - currentTime;
        
        return Math.max(0, timeLeft);
    }
}