package io.Sriptirc_wp_1258.gost.managers;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * 神圣守护管理器 - 为最后一位人类玩家提供特殊能力
 */
public class DivineGuardianManager {
    
    private final Gost plugin;
    private final Random random = new Random();
    
    // 神圣守护数据存储
    private final Map<UUID, DivineGuardianData> guardianData = new HashMap<>();
    
    // 神圣守护状态
    private boolean enabled = false;
    private UUID lastHumanPlayer = null;
    private boolean isActive = false;
    
    public DivineGuardianManager(Gost plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 神圣守护数据类
     */
    private static class DivineGuardianData {
        int remainingCharges;      // 剩余使用次数
        long lastUseTime;          // 上次使用时间
        boolean isActive;          // 是否激活
        
        DivineGuardianData(int maxCharges) {
            this.remainingCharges = maxCharges;
            this.lastUseTime = 0;
            this.isActive = true;
        }
    }
    
    /**
     * 加载配置
     */
    public void loadConfig() {
        enabled = plugin.getConfigManager().isDivineGuardianEnabled();
        plugin.getLogger().info("神圣守护功能: " + (enabled ? "已启用" : "已禁用"));
    }
    
    /**
     * 检查并激活神圣守护
     * @param humanPlayers 当前人类玩家列表
     */
    public void checkAndActivateDivineGuardian(java.util.List<UUID> humanPlayers) {
        if (!enabled || !plugin.getGameManager().isGameRunning()) {
            return;
        }
        
        // 如果只剩下一个人类玩家，激活神圣守护
        if (humanPlayers.size() == 1) {
            UUID lastHuman = humanPlayers.get(0);
            
            // 如果已经激活了神圣守护，检查是否还是同一个玩家
            if (isActive && lastHumanPlayer != null && lastHumanPlayer.equals(lastHuman)) {
                return; // 同一个玩家，神圣守护已激活
            }
            
            // 激活神圣守护
            activateDivineGuardian(lastHuman);
        } else {
            // 如果有多于一个人类玩家，取消神圣守护
            if (isActive) {
                deactivateDivineGuardian();
            }
        }
    }
    
    /**
     * 激活神圣守护
     */
    private void activateDivineGuardian(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null || !player.isOnline()) {
            return;
        }
        
        // 设置神圣守护数据
        int maxCharges = plugin.getConfigManager().getDivineGuardianMaxCharges();
        guardianData.put(playerId, new DivineGuardianData(maxCharges));
        lastHumanPlayer = playerId;
        isActive = true;
        
        // 应用神圣守护效果
        applyDivineGuardianEffects(player);
        
        // 发送激活消息
        sendActivationMessages(player);
        
        plugin.getLogger().info("神圣守护已激活，玩家: " + player.getName());
    }
    
    /**
     * 应用神圣守护效果
     */
    private void applyDivineGuardianEffects(Player player) {
        // 速度I效果（持续整个神圣守护期间）
        player.addPotionEffect(new PotionEffect(
            PotionEffectType.SPEED,
            20 * 60 * 10, // 10分钟（足够长的时间）
            0, // 速度I
            true,
            true
        ));
        
        // 发光效果（显示神圣状态）
        player.addPotionEffect(new PotionEffect(
            PotionEffectType.GLOWING,
            20 * 60 * 10, // 10分钟
            0,
            true,
            true
        ));
        
        // 粒子效果
        spawnActivationParticles(player.getLocation());
        
        // 音效
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        Bukkit.getOnlinePlayers().forEach(p -> 
            p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.5f, 1.0f)
        );
    }
    
    /**
     * 发送激活消息
     */
    private void sendActivationMessages(Player player) {
        // 屏幕标题
        player.sendTitle(
            ChatColor.GOLD + "✨ 神圣守护激活！",
            ChatColor.YELLOW + "你获得了神圣守护能力",
            10, 60, 10
        );
        
        // 个人消息
        player.sendMessage(ChatColor.GOLD + "════════════════════════════════");
        player.sendMessage(ChatColor.GOLD + "              ✨ 神圣守护 ✨");
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "✓ 你是最后一位人类玩家！");
        player.sendMessage(ChatColor.YELLOW + "✓ 获得神圣守护能力：");
        player.sendMessage(ChatColor.GREEN + "  • 免疫感染 ×" + plugin.getConfigManager().getDivineGuardianMaxCharges());
        player.sendMessage(ChatColor.GREEN + "  • 被攻击时随机传送");
        player.sendMessage(ChatColor.GREEN + "  • 速度I效果");
        player.sendMessage(ChatColor.GREEN + "  • 冷却时间: " + plugin.getConfigManager().getDivineGuardianCooldown() + "秒");
        player.sendMessage(ChatColor.GOLD + "════════════════════════════════");
        
        // 广播消息
        if (plugin.getConfigManager().isDivineGuardianBroadcastEnabled()) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "════════════════════════════════");
            Bukkit.broadcastMessage(ChatColor.GOLD + "✨ " + player.getName() + " 是最后一位人类玩家！");
            Bukkit.broadcastMessage(ChatColor.YELLOW + "✨ 神圣守护已激活！");
            Bukkit.broadcastMessage(ChatColor.YELLOW + "✨ 他获得了特殊能力：免疫感染×" + 
                plugin.getConfigManager().getDivineGuardianMaxCharges() + "，随机传送");
            Bukkit.broadcastMessage(ChatColor.GOLD + "════════════════════════════════");
        }
    }
    
    /**
     * 处理神圣守护触发
     * @param humanPlayer 人类玩家
     * @param ghostPlayer 鬼玩家
     * @return 是否成功触发神圣守护
     */
    public boolean handleDivineGuardianTrigger(Player humanPlayer, Player ghostPlayer) {
        if (!enabled || !isActive || lastHumanPlayer == null) {
            return false;
        }
        
        // 检查是否是神圣守护玩家
        if (!humanPlayer.getUniqueId().equals(lastHumanPlayer)) {
            return false;
        }
        
        DivineGuardianData data = guardianData.get(lastHumanPlayer);
        if (data == null || !data.isActive || data.remainingCharges <= 0) {
            return false;
        }
        
        // 检查冷却时间
        long currentTime = System.currentTimeMillis();
        long cooldownTime = plugin.getConfigManager().getDivineGuardianCooldown() * 1000L;
        
        if (currentTime - data.lastUseTime < cooldownTime) {
            long timeLeft = (data.lastUseTime + cooldownTime - currentTime) / 1000;
            humanPlayer.sendMessage(ChatColor.RED + "神圣守护冷却中，剩余: " + timeLeft + "秒");
            return false;
        }
        
        // 触发神圣守护
        return triggerDivineGuardian(humanPlayer, ghostPlayer, data);
    }
    
    /**
     * 触发神圣守护
     */
    private boolean triggerDivineGuardian(Player humanPlayer, Player ghostPlayer, DivineGuardianData data) {
        // 减少使用次数
        data.remainingCharges--;
        data.lastUseTime = System.currentTimeMillis();
        
        // 随机传送玩家
        teleportPlayerRandomly(humanPlayer);
        
        // 发送触发消息
        sendTriggerMessages(humanPlayer, ghostPlayer, data.remainingCharges);
        
        // 视觉效果
        spawnTriggerEffects(humanPlayer.getLocation());
        
        // 检查是否用完次数
        if (data.remainingCharges <= 0) {
            deactivateDivineGuardian();
        }
        
        plugin.getLogger().info("神圣守护触发，玩家: " + humanPlayer.getName() + 
                              ", 剩余次数: " + data.remainingCharges);
        
        return true;
    }
    
    /**
     * 随机传送玩家
     */
    private void teleportPlayerRandomly(Player player) {
        try {
            // 获取游戏区域
            Location areaMin = plugin.getAreaManager().getAreaMin();
            Location areaMax = plugin.getAreaManager().getAreaMax();
            
            if (areaMin == null || areaMax == null) {
                plugin.getLogger().warning("无法随机传送玩家: 游戏区域未设置");
                return;
            }
            
            World world = areaMin.getWorld();
            if (world == null) {
                plugin.getLogger().warning("无法随机传送玩家: 世界未加载");
                return;
            }
            
            // 计算区域边界
            int minX = (int) Math.min(areaMin.getX(), areaMax.getX());
            int maxX = (int) Math.max(areaMin.getX(), areaMax.getX());
            int minZ = (int) Math.min(areaMin.getZ(), areaMax.getZ());
            int maxZ = (int) Math.max(areaMin.getZ(), areaMax.getZ());
            
            // 尝试寻找安全位置
            Location safeLocation = null;
            int attempts = 0;
            int maxAttempts = 30;
            
            while (safeLocation == null && attempts < maxAttempts) {
                attempts++;
                
                // 生成随机坐标
                int randomX = minX + random.nextInt(maxX - minX + 1);
                int randomZ = minZ + random.nextInt(maxZ - minZ + 1);
                
                // 获取该位置的地面Y坐标
                int groundY = findGroundY(world, randomX, randomZ);
                
                if (groundY > world.getMinHeight() && groundY < world.getMaxHeight()) {
                    // 创建安全位置
                    safeLocation = new Location(world, randomX + 0.5, groundY + 1, randomZ + 0.5);
                    
                    // 检查位置是否安全
                    if (isLocationSafe(safeLocation)) {
                        break;
                    } else {
                        safeLocation = null;
                    }
                }
            }
            
            if (safeLocation != null) {
                // 执行传送
                player.teleport(safeLocation);
                
                // 传送粒子效果
                spawnTeleportParticles(player.getLocation());
                spawnTeleportParticles(safeLocation);
                
                // 传送音效
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                
                // 发送传送消息
                player.sendMessage(ChatColor.GOLD + "✨ 神圣守护将你传送到安全位置！");
                player.sendMessage(ChatColor.YELLOW + "坐标: X=" + (int)safeLocation.getX() + 
                                 ", Y=" + (int)safeLocation.getY() + 
                                 ", Z=" + (int)safeLocation.getZ());
                
                plugin.getLogger().info("神圣守护传送玩家 " + player.getName() + " 到: " + safeLocation);
            } else {
                plugin.getLogger().warning("无法为玩家 " + player.getName() + " 找到安全传送位置");
                player.sendMessage(ChatColor.RED + "⚠ 神圣守护传送失败，无法找到安全位置");
            }
            
        } catch (Exception e) {
            plugin.getLogger().severe("神圣守护传送玩家时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 发送触发消息
     */
    private void sendTriggerMessages(Player humanPlayer, Player ghostPlayer, int remainingCharges) {
        // 屏幕标题
        humanPlayer.sendTitle(
            ChatColor.GOLD + "✨ 神圣守护触发！",
            ChatColor.YELLOW + "免疫感染，随机传送",
            10, 40, 10
        );
        
        ghostPlayer.sendTitle(
            ChatColor.RED + "⚠ 神圣守护！",
            ChatColor.YELLOW + "目标免疫感染并被传送",
            10, 40, 10
        );
        
        // 个人消息
        humanPlayer.sendMessage(ChatColor.GOLD + "════════════════════════════════");
        humanPlayer.sendMessage(ChatColor.GOLD + "              ✨ 神圣守护触发 ✨");
        humanPlayer.sendMessage("");
        humanPlayer.sendMessage(ChatColor.GREEN + "✓ 成功免疫了 " + ghostPlayer.getName() + " 的感染！");
        humanPlayer.sendMessage(ChatColor.GREEN + "✓ 你被随机传送到安全位置");
        humanPlayer.sendMessage(ChatColor.YELLOW + "⏱️ 剩余神圣守护次数: " + remainingCharges);
        humanPlayer.sendMessage(ChatColor.GOLD + "════════════════════════════════");
        
        ghostPlayer.sendMessage(ChatColor.RED + "════════════════════════════════");
        ghostPlayer.sendMessage(ChatColor.RED + "              ⚠ 神圣守护 ⚠");
        ghostPlayer.sendMessage("");
        ghostPlayer.sendMessage(ChatColor.RED + "✗ 对 " + humanPlayer.getName() + " 的感染被神圣守护抵挡");
        ghostPlayer.sendMessage(ChatColor.RED + "✗ 目标被随机传送");
        ghostPlayer.sendMessage(ChatColor.YELLOW + "⏱️ 目标剩余神圣守护次数: " + remainingCharges);
        ghostPlayer.sendMessage(ChatColor.RED + "════════════════════════════════");
        
        // 广播消息
        if (plugin.getConfigManager().isDivineGuardianBroadcastEnabled()) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "════════════════════════════════");
            Bukkit.broadcastMessage(ChatColor.GOLD + "✨ " + humanPlayer.getName() + " 的神圣守护触发！");
            Bukkit.broadcastMessage(ChatColor.YELLOW + "✨ 免疫了 " + ghostPlayer.getName() + " 的感染");
            Bukkit.broadcastMessage(ChatColor.YELLOW + "✨ 被随机传送到安全位置");
            Bukkit.broadcastMessage(ChatColor.YELLOW + "⏱️ 剩余神圣守护次数: " + remainingCharges);
            Bukkit.broadcastMessage(ChatColor.GOLD + "════════════════════════════════");
        }
    }
    
    /**
     * 取消神圣守护
     */
    private void deactivateDivineGuardian() {
        if (lastHumanPlayer == null) {
            return;
        }
        
        Player player = Bukkit.getPlayer(lastHumanPlayer);
        if (player != null && player.isOnline()) {
            // 移除神圣守护效果
            removeDivineGuardianEffects(player);
            
            // 应用失效效果（隐身10秒）
            applyDeactivationEffects(player);
            
            // 发送失效消息
            sendDeactivationMessages(player);
        }
        
        // 清理数据
        guardianData.remove(lastHumanPlayer);
        lastHumanPlayer = null;
        isActive = false;
        
        plugin.getLogger().info("神圣守护已失效");
    }
    
    /**
     * 移除神圣守护效果
     */
    private void removeDivineGuardianEffects(Player player) {
        // 移除速度效果
        player.removePotionEffect(PotionEffectType.SPEED);
        
        // 移除发光效果
        player.removePotionEffect(PotionEffectType.GLOWING);
        
        // 失效粒子效果
        spawnDeactivationParticles(player.getLocation());
        
        // 失效音效
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1.0f, 1.0f);
    }
    
    /**
     * 应用失效效果
     */
    private void applyDeactivationEffects(Player player) {
        // 隐身10秒
        player.addPotionEffect(new PotionEffect(
            PotionEffectType.INVISIBILITY,
            20 * 10, // 10秒
            0,
            true,
            true
        ));
        
        // 失效粒子效果
        spawnDeactivationParticles(player.getLocation());
    }
    
    /**
     * 发送失效消息
     */
    private void sendDeactivationMessages(Player player) {
        // 屏幕标题
        player.sendTitle(
            ChatColor.RED + "⚠ 神圣守护失效！",
            ChatColor.YELLOW + "获得隐身10秒效果",
            10, 60, 10
        );
        
        // 个人消息
        player.sendMessage(ChatColor.RED + "════════════════════════════════");
        player.sendMessage(ChatColor.RED + "              ⚠ 神圣守护失效 ⚠");
        player.sendMessage("");
        player.sendMessage(ChatColor.RED + "✗ 神圣守护使用次数已耗尽");
        player.sendMessage(ChatColor.YELLOW + "✓ 获得隐身效果10秒");
        player.sendMessage(ChatColor.YELLOW + "⚠ 注意：你现在可以被感染了！");
        player.sendMessage(ChatColor.RED + "════════════════════════════════");
        
        // 广播消息
        if (plugin.getConfigManager().isDivineGuardianBroadcastEnabled()) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "════════════════════════════════");
            Bukkit.broadcastMessage(ChatColor.RED + "⚠ " + player.getName() + " 的神圣守护已失效！");
            Bukkit.broadcastMessage(ChatColor.YELLOW + "⚠ 他现在可以被感染了");
            Bukkit.broadcastMessage(ChatColor.YELLOW + "✨ 获得隐身效果10秒");
            Bukkit.broadcastMessage(ChatColor.GOLD + "════════════════════════════════");
        }
    }
    
    /**
     * 查找地面Y坐标
     */
    private int findGroundY(World world, int x, int z) {
        int maxY = world.getMaxHeight() - 1;
        
        for (int y = maxY; y > world.getMinHeight(); y--) {
            Location loc = new Location(world, x, y, z);
            
            // 检查当前位置是否是固体方块
            if (!loc.getBlock().getType().isAir() && 
                !loc.getBlock().isLiquid() && 
                loc.getBlock().getType().isSolid()) {
                
                // 检查上方是否有足够空间
                if (y + 2 < world.getMaxHeight()) {
                    Location above1 = new Location(world, x, y + 1, z);
                    Location above2 = new Location(world, x, y + 2, z);
                    
                    if (above1.getBlock().getType().isAir() && 
                        above2.getBlock().getType().isAir()) {
                        return y;
                    }
                }
            }
        }
        
        return world.getMinHeight();
    }
    
    /**
     * 检查位置是否安全
     */
    private boolean isLocationSafe(Location location) {
        World world = location.getWorld();
        if (world == null) return false;
        
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        
        // 检查当前位置是否是空气
        if (!world.getBlockAt(x, y, z).getType().isAir()) {
            return false;
        }
        
        // 检查脚下是否是固体方块
        if (!world.getBlockAt(x, y - 1, z).getType().isSolid()) {
            return false;
        }
        
        // 检查上方是否有足够空间
        if (!world.getBlockAt(x, y + 1, z).getType().isAir()) {
            return false;
        }
        
        // 检查是否在液体中
        if (world.getBlockAt(x, y, z).isLiquid() || 
            world.getBlockAt(x, y - 1, z).isLiquid()) {
            return false;
        }
        
        // 检查是否在危险方块上
        Material floorMaterial = world.getBlockAt(x, y - 1, z).getType();
        if (floorMaterial == Material.LAVA || 
            floorMaterial == Material.FIRE || 
            floorMaterial == Material.CAMPFIRE || 
            floorMaterial == Material.SOUL_CAMPFIRE) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 生成激活粒子效果
     */
    private void spawnActivationParticles(Location location) {
        World world = location.getWorld();
        if (world == null) return;
        
        for (int i = 0; i < 50; i++) {
            double angle = 2 * Math.PI * i / 50;
            double x = Math.cos(angle) * 2;
            double z = Math.sin(angle) * 2;
            
            world.spawnParticle(
                Particle.FIREWORKS_SPARK,
                location.clone().add(x, 0.5, z),
                1,
                0, 0, 0,
                0
            );
            
            world.spawnParticle(
                Particle.END_ROD,
                location.clone().add(0, i * 0.1, 0),
                3,
                0.2, 0.2, 0.2,
                0
            );
        }
    }
    
    /**
     * 生成触发粒子效果
     */
    private void spawnTriggerEffects(Location location) {
        World world = location.getWorld();
        if (world == null) return;
        
        // 圆形粒子效果
        for (int i = 0; i < 30; i++) {
            double angle = 2 * Math.PI * i / 30;
            double x = Math.cos(angle) * 1.5;
            double z = Math.sin(angle) * 1.5;
            
            world.spawnParticle(
                Particle.SPELL_INSTANT,
                location.clone().add(x, 1, z),
                2,
                0, 0, 0,
                0
            );
        }
        
        // 向上喷射粒子
        for (int i = 0; i < 20; i++) {
            world.spawnParticle(
                Particle.CLOUD,
                location.clone().add(0, i * 0.2, 0),
                2,
                0.1, 0, 0.1,
                0.05
            );
        }
    }
    
    /**
     * 生成传送粒子效果
     */
    private void spawnTeleportParticles(Location location) {
        World world = location.getWorld();
        if (world == null) return;
        
        // 传送门粒子效果
        for (int i = 0; i < 20; i++) {
            world.spawnParticle(
                Particle.PORTAL,
                location.clone().add(0, 1, 0),
                10,
                0.5, 0.5, 0.5,
                0.1
            );
        }
        
        // 紫光粒子
        world.spawnParticle(
            Particle.DRAGON_BREATH,
            location.clone().add(0, 1, 0),
            15,
            0.3, 0.5, 0.3,
            0.05
        );
    }
    
    /**
     * 生成失效粒子效果
     */
    private void spawnDeactivationParticles(Location location) {
        World world = location.getWorld();
        if (world == null) return;
        
        // 黑色烟雾粒子
        for (int i = 0; i < 30; i++) {
            world.spawnParticle(
                Particle.SMOKE_LARGE,
                location.clone().add(0, 1, 0),
                5,
                0.5, 0.5, 0.5,
                0.1
            );
        }
        
        // 红色粒子（表示危险）
        for (int i = 0; i < 20; i++) {
            world.spawnParticle(
                Particle.REDSTONE,
                location.clone().add(0, i * 0.2, 0),
                3,
                0.2, 0, 0.2,
                0,
                new org.bukkit.Particle.DustOptions(org.bukkit.Color.RED, 1)
            );
        }
    }
    
    /**
     * 清理数据
     */
    public void cleanup() {
        if (isActive && lastHumanPlayer != null) {
            Player player = Bukkit.getPlayer(lastHumanPlayer);
            if (player != null && player.isOnline()) {
                removeDivineGuardianEffects(player);
            }
        }
        
        guardianData.clear();
        lastHumanPlayer = null;
        isActive = false;
        
        plugin.getLogger().info("神圣守护数据已清理");
    }
    
    /**
     * 获取神圣守护玩家
     */
    public UUID getDivineGuardianPlayer() {
        return lastHumanPlayer;
    }
    
    /**
     * 检查玩家是否有神圣守护
     */
    public boolean hasDivineGuardian(UUID playerId) {
        return isActive && lastHumanPlayer != null && lastHumanPlayer.equals(playerId);
    }
    
    /**
     * 获取剩余使用次数
     */
    public int getRemainingCharges(UUID playerId) {
        DivineGuardianData data = guardianData.get(playerId);
        return data != null ? data.remainingCharges : 0;
    }
    
    /**
     * 重新加载配置
     */
    public void reload() {
        loadConfig();
        cleanup();
    }
}