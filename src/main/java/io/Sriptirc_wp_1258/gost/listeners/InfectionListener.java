package io.Sriptirc_wp_1258.gost.listeners;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class InfectionListener implements Listener {
    
    private final Gost plugin;
    
    public InfectionListener(Gost plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        // 检查是否是玩家攻击玩家
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();
        
        // 检查游戏是否在进行中
        if (!plugin.getGameManager().isGameRunning()) {
            return;
        }
        
        // 检查攻击者是否是鬼
        if (!plugin.getPlayerManager().isGhost(attacker.getUniqueId())) {
            return;
        }
        
        // 检查受害者是否是人类
        if (!plugin.getPlayerManager().isHuman(victim.getUniqueId())) {
            return;
        }
        
        // 执行感染
        plugin.getPlayerManager().infectPlayer(victim.getUniqueId(), attacker.getUniqueId());
        
        // 取消伤害（感染不造成伤害）
        event.setCancelled(true);
        
        // 发送消息
        attacker.sendMessage("§a你成功感染了 " + victim.getName() + "！");
        victim.sendMessage("§c你被 " + attacker.getName() + " 感染了！现在你变成了鬼！");
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        // 检查是否是玩家右键点击玩家
        if (!(event.getRightClicked() instanceof Player)) {
            return;
        }
        
        Player attacker = event.getPlayer();
        Player victim = (Player) event.getRightClicked();
        
        // 检查游戏是否在进行中
        if (!plugin.getGameManager().isGameRunning()) {
            return;
        }
        
        // 检查攻击者是否是鬼
        if (!plugin.getPlayerManager().isGhost(attacker.getUniqueId())) {
            return;
        }
        
        // 检查受害者是否是人类
        if (!plugin.getPlayerManager().isHuman(victim.getUniqueId())) {
            return;
        }
        
        // 执行感染
        plugin.getPlayerManager().infectPlayer(victim.getUniqueId(), attacker.getUniqueId());
        
        // 发送消息
        attacker.sendMessage("§a你成功感染了 " + victim.getName() + "！");
        victim.sendMessage("§c你被 " + attacker.getName() + " 感染了！现在你变成了鬼！");
    }
}