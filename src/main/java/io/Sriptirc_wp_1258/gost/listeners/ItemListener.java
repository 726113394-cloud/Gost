package io.Sriptirc_wp_1258.gost.listeners;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemListener implements Listener {
    
    private final Gost plugin;
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();
    
    public ItemListener(Gost plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        // 检查玩家是否在游戏中
        if (!plugin.getPlayerManager().getAllPlayers().contains(player.getUniqueId())) {
            return;
        }
        
        // 检查是否是右键操作
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) {
            return;
        }
        
        String displayName = meta.getDisplayName();
        event.setCancelled(true);
        
        // 处理不同物品
        if (displayName.contains("肾上腺素")) {
            handleAdrenaline(player, item);
        } else if (displayName.contains("狂暴药水")) {
            handleFrenzyPotion(player, item);
        } else if (displayName.contains("凝冰球")) {
            handleIceBall(player, event);
        } else if (displayName.contains("控魂术")) {
            handleSoulControl(player, item);
        } else if (displayName.contains("闪现珍珠")) {
            handleBlinkPearl(player, item, event);
        } else if (displayName.contains("臭牛排")) {
            handleStinkySteak(player, item);
        } else if (displayName.contains("传送珍珠")) {
            handleTeleportPearl(player, item, event);
        }
    }
    
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        // 检查是否是雪球（凝冰球）
        if (!(event.getEntity() instanceof Snowball)) {
            return;
        }
        
        Snowball snowball = (Snowball) event.getEntity();
        if (!(snowball.getShooter() instanceof Player)) {
            return;
        }
        
        Player shooter = (Player) snowball.getShooter();
        
        // 检查射击者是否在游戏中
        if (!plugin.getPlayerManager().getAllPlayers().contains(shooter.getUniqueId())) {
            return;
        }
        
        // 检查被击中的实体是否是玩家
        if (event.getHitEntity() instanceof Player) {
            Player target = (Player) event.getHitEntity();
            
            // 检查目标是否是鬼
            if (plugin.getPlayerManager().isGhost(target.getUniqueId())) {
                // 应用凝冰球效果
                plugin.getItemManager().applyIceBallEffect(target);
                shooter.sendMessage(ChatColor.AQUA + "你成功击中了 " + target.getName() + "！");
            }
        }
    }
    
    private void handleAdrenaline(Player player, ItemStack item) {
        // 检查玩家是否是人类
        if (!plugin.getPlayerManager().isHuman(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "只有人类可以使用肾上腺素！");
            return;
        }
        
        // 发送ActionBar提示
        plugin.getActionBarManager().sendAdrenalineHint(player);
        
        // 应用肾上腺素效果
        plugin.getItemManager().applyAdrenalineEffect(player);
        
        // 消耗物品
        consumeItem(player, item);
    }
    
    private void handleFrenzyPotion(Player player, ItemStack item) {
        // 检查玩家是否是鬼
        if (!plugin.getPlayerManager().isGhost(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "只有鬼可以使用狂暴药水！");
            return;
        }
        
        // 发送ActionBar提示
        plugin.getActionBarManager().sendFrenzyPotionHint(player);
        
        // 应用狂暴药水效果
        plugin.getItemManager().applyFrenzyEffect(player);
        
        // 消耗物品
        consumeItem(player, item);
    }
    
    private void handleIceBall(Player player, PlayerInteractEvent event) {
        // 发送ActionBar提示
        plugin.getActionBarManager().sendIceBallHint(player);
        
        // 允许投掷雪球（不消耗物品，由事件处理）
        // 这里不消耗物品，让雪球正常投掷
        event.setCancelled(false);
    }
    
    private void handleSoulControl(Player player, ItemStack item) {
        // 检查玩家是否是人类
        if (!plugin.getPlayerManager().isHuman(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "只有人类可以使用控魂术！");
            return;
        }
        
        // 获取所有鬼玩家
        List<Player> ghostPlayers = new ArrayList<>();
        io.Sriptirc_wp_1258.gost.managers.AreaManager.GameArea selectedArea = plugin.getAreaManager().getSelectedArea();
        if (selectedArea != null) {
            ghostPlayers = plugin.getAreaManager().getPlayersInArea(selectedArea);
            ghostPlayers.removeIf(p -> !plugin.getPlayerManager().isGhost(p.getUniqueId()));
        }
        
        if (ghostPlayers.isEmpty()) {
            player.sendMessage(ChatColor.RED + "附近没有鬼玩家！");
            return;
        }
        
        // 发送ActionBar提示
        plugin.getActionBarManager().sendSoulControlHint(player);
        
        // 应用控魂术效果
        plugin.getItemManager().applySoulControlEffect(ghostPlayers);
        
        // 消耗物品
        consumeItem(player, item);
    }
    
    private void handleBlinkPearl(Player player, ItemStack item, PlayerInteractEvent event) {
        // 检查玩家是否在游戏中
        if (!plugin.getGameManager().isGameRunning()) {
            player.sendMessage(ChatColor.RED + "游戏未开始，无法使用闪现珍珠！");
            return;
        }
        
        // 发送ActionBar提示
        plugin.getActionBarManager().sendBlinkPearlHint(player);
        
        // 允许使用末影珍珠（不取消事件）
        event.setCancelled(false);
        
        // 末影珍珠是消耗品，使用后会自然消耗
        // 不需要设置冷却时间，因为每个珍珠只能使用一次
    }
    
    private void consumeItem(Player player, ItemStack item) {
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().remove(item);
        }
        player.updateInventory();
    }
    
    // 冷却时间管理方法
    private boolean isOnCooldown(Player player, String itemType) {
        UUID playerId = player.getUniqueId();
        if (!cooldowns.containsKey(playerId)) {
            return false;
        }
        
        Map<String, Long> playerCooldowns = cooldowns.get(playerId);
        if (!playerCooldowns.containsKey(itemType)) {
            return false;
        }
        
        long cooldownEnd = playerCooldowns.get(itemType);
        return System.currentTimeMillis() < cooldownEnd;
    }
    
    private int getRemainingCooldown(Player player, String itemType) {
        UUID playerId = player.getUniqueId();
        if (!cooldowns.containsKey(playerId)) {
            return 0;
        }
        
        Map<String, Long> playerCooldowns = cooldowns.get(playerId);
        if (!playerCooldowns.containsKey(itemType)) {
            return 0;
        }
        
        long cooldownEnd = playerCooldowns.get(itemType);
        long remaining = cooldownEnd - System.currentTimeMillis();
        return (int) Math.ceil(remaining / 1000.0);
    }
    
    private void setCooldown(Player player, String itemType, int seconds) {
        UUID playerId = player.getUniqueId();
        Map<String, Long> playerCooldowns = cooldowns.computeIfAbsent(playerId, k -> new HashMap<>());
        playerCooldowns.put(itemType, System.currentTimeMillis() + (seconds * 1000L));
    }
    
    // 清理玩家的冷却时间
    public void clearPlayerCooldowns(UUID playerId) {
        cooldowns.remove(playerId);
    }
    
    // 清理所有冷却时间
    public void clearAllCooldowns() {
        cooldowns.clear();
    }
    
    private void handleStinkySteak(Player player, ItemStack item) {
        // 发送ActionBar提示
        plugin.getActionBarManager().sendStinkySteakHint(player);
        
        // 保存当前饱食度
        float savedFoodLevel = player.getFoodLevel();
        float savedSaturation = player.getSaturation();
        
        // 临时设置饱食度为20，确保可以食用
        player.setFoodLevel(20);
        player.setSaturation(20);
        
        // 应用臭牛排效果 - 速度II效果14秒
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 280, 1)); // 14秒 * 20 = 280 ticks
        
        // 消耗物品
        consumeItem(player, item);
        
        // 恢复原来的饱食度
        player.setFoodLevel((int) savedFoodLevel);
        player.setSaturation(savedSaturation);
        
        // 发送使用提示
        player.sendMessage(ChatColor.GREEN + "你食用了臭牛排，获得了速度II效果！");
    }
    
    private void handleTeleportPearl(Player player, ItemStack item, PlayerInteractEvent event) {
        // 检查冷却时间
        if (isOnCooldown(player, "teleport-pearl")) {
            int remaining = getRemainingCooldown(player, "teleport-pearl");
            player.sendMessage(ChatColor.RED + "传送珍珠冷却中，剩余 " + remaining + " 秒");
            return;
        }
        
        // 发送ActionBar提示
        plugin.getActionBarManager().sendTeleportPearlHint(player);
        
        // 允许使用末影珍珠（不取消事件）
        event.setCancelled(false);
        
        // 设置冷却时间
        int cooldown = plugin.getConfigManager().getTeleportPearlCooldown();
        setCooldown(player, "teleport-pearl", cooldown);
        
        // 发送使用提示
        player.sendMessage(ChatColor.GREEN + "你使用了传送珍珠，冷却时间 " + cooldown + " 秒");
    }
}