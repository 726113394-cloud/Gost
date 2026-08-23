package io.Sriptirc_wp_1258.gost.listeners;

import io.Sriptirc_wp_1258.gost.Gost;
import io.Sriptirc_wp_1258.gost.managers.DivineGuardianManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * 收割者监听器
 * 处理猎魔人阶段的击杀机制、死亡拦截、阵营伤害保护
 */
public class DemonHunterPhaseListener implements Listener {
    
    private final Gost plugin;
    
    public DemonHunterPhaseListener(Gost plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 阵营伤害保护：同阵营之间无法造成伤害
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();
        
        if (!plugin.getGameManager().isGameRunning()) {
            return;
        }
        
        DivineGuardianManager dg = plugin.getDivineGuardianManager();
        boolean attackerDH = dg.isDemonHunter(attacker.getUniqueId());
        boolean attackerGhost = plugin.getPlayerManager().isGhost(attacker.getUniqueId());
        boolean victimGhost = plugin.getPlayerManager().isGhost(victim.getUniqueId());
        boolean victimDH = dg.isDemonHunter(victim.getUniqueId());
        
        // 猎魔人攻击非鬼（人类/猎魔人）→ 禁止
        if (attackerDH && !victimGhost) {
            event.setCancelled(true);
            return;
        }
        // 鬼攻击鬼（含母体攻击普通鬼）→ 禁止
        if (attackerGhost && victimGhost) {
            event.setCancelled(true);
            return;
        }
        // 人类攻击人类 → 禁止
        if (!attackerGhost && !attackerDH && !victimGhost && !victimDH) {
            event.setCancelled(true);
        }
    }
    
    /**
     * 死亡拦截：猎魔人阶段鬼/猎魔人"击杀"时不进入真实死亡
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!plugin.getGameManager().isGameRunning()) {
            return;
        }
        
        DivineGuardianManager dg = plugin.getDivineGuardianManager();
        if (dg.isInDemonHunterPhase()) {
            boolean isGhost = plugin.getPlayerManager().isGhost(player.getUniqueId());
            boolean isDH = dg.isDemonHunter(player.getUniqueId());
            if (isGhost || isDH) {
                event.setCancelled(true);
                player.setHealth(player.getMaxHealth());
            }
        }
    }
    
    /**
     * 环境伤害保护：猎魔人阶段鬼/猎魔人免受环境伤害
     * 确保只有收割者"击中次数"能决定击杀
     */
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (!plugin.getGameManager().isGameRunning()) {
            return;
        }
        DivineGuardianManager dg = plugin.getDivineGuardianManager();
        if (!dg.isInDemonHunterPhase()) {
            return;
        }
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK &&
            event.getCause() != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            if (plugin.getPlayerManager().isGhost(player.getUniqueId()) || 
                dg.isDemonHunter(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }
}
