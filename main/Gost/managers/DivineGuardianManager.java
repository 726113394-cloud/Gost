package io.Sriptirc_wp_1258.gost.managers;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class DivineGuardianManager {
    
    private final Gost plugin;
    private final Random random = new Random();
    
    // 神圣守护玩家数据
    private final Set<UUID> holyGuardianPlayers = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Long> holyGuardianActivationTime = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> holyGuardianHitCount = new ConcurrentHashMap<>(); // 记录神圣守护被攻击次数
    private final Map<UUID, Integer> redeemerGuardianMaxCharges = new ConcurrentHashMap<>(); // 救赎者独立守护次数（默认2）
    
    // 猎魔人数据
    private final Set<UUID> demonHunterPlayers = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Integer> demonHunterKillCount = new ConcurrentHashMap<>();
    private final Map<UUID, Long> reaperAttackCooldown = new ConcurrentHashMap<>();
    private final Map<UUID, Long> reaperHarvestCooldown = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> ghostHitCount = new ConcurrentHashMap<>(); // 记录鬼被攻击次数
    private final Map<UUID, Integer> motherHitCount = new ConcurrentHashMap<>(); // 记录母体被攻击次数
    private final Map<UUID, Integer> demonHunterHitCount = new ConcurrentHashMap<>(); // 记录猎魔人被母体击中次数（4次感染）
    
    // 神之救赎道具数据
    private final Map<UUID, Integer> holyRedemptionUses = new ConcurrentHashMap<>();
    private final Map<UUID, Long> holyRedemptionCooldown = new ConcurrentHashMap<>();
    private final Set<UUID> redeemerPlayers = ConcurrentHashMap.newKeySet(); // 常驻救赎者（最多2名）
    
    // 母体数据
    private final Set<UUID> additionalMothers = ConcurrentHashMap.newKeySet(); // 新增的母体
    
    // 状态标志
    private boolean isDemonHunterPhase = false;
    private boolean hasAdditionalMotherSpawned = false;
    
    // 旁观者数据
    private final Set<UUID> spectatorPlayers = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Location> spectatorOriginalLocations = new ConcurrentHashMap<>();
    private final Map<UUID, org.bukkit.scheduler.BukkitTask> spectatorBoundaryTasks = new ConcurrentHashMap<>();
    
    // 复活机制数据
    private final Map<UUID, Long> respawnTimers = new ConcurrentHashMap<>(); // 玩家ID -> 复活时间戳
    private final Map<UUID, org.bukkit.scheduler.BukkitTask> respawnTasks = new ConcurrentHashMap<>(); // 复活任务
    private final Map<UUID, Location> deathLocations = new ConcurrentHashMap<>(); // 鬼死亡时坐标（用于记录）
    
    // 额外奖励点数（结算时发放，与奖池分离）
    private final Map<UUID, Integer> rewardPoints = new ConcurrentHashMap<>();
    
    // 母体升级绿宝石数据
    private org.bukkit.entity.Item motherEmerald = null; // 场上发光绿宝石实体
    private org.bukkit.scheduler.BukkitTask motherEmeraldGlowTask = null; // 发光标记任务
    
    public DivineGuardianManager(Gost plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 加载配置
     */
    public void loadConfig() {
        plugin.getLogger().info("神圣守护管理器 v2.2.2 已加载");
    }
    
    /**
     * 检查并激活神圣守护
     * @param humanPlayers 当前人类玩家列表
     */
    public void checkAndActivateHolyGuardian(List<UUID> humanPlayers) {
        if (!plugin.getConfigManager().isDivineGuardianSystemEnabled()) {
            return;
        }
        
        if (!plugin.getGameManager().isGameRunning()) {
            return;
        }
        
        int triggerCount = plugin.getConfigManager().getDivineGuardianTriggerHumanCount();
        
        // 如果人类玩家数量等于或少于触发数量，激活神圣守护
        if (humanPlayers.size() <= triggerCount) {
            // v2.3.3：神圣守护阶段出现母体进化水晶（固定坐标发光绿宝石）
            trySpawnMotherEmeraldOnHolyGuardianPhase();
            // 为所有剩余人类玩家激活神圣守护（排除被神之救赎转化的人类）
            for (UUID playerId : humanPlayers) {
                // 检查是否是神之救赎转化的玩家
                boolean isConvertedByRedemption = plugin.getPlayerManager().isConvertedByRedemption(playerId);
                if (!isConvertedByRedemption && !holyGuardianPlayers.contains(playerId)) {
                    activateHolyGuardian(playerId);
                }
            }
            
            // 移除不再是人类的神圣守护玩家和被神之救赎转化的玩家
            holyGuardianPlayers.removeIf(playerId -> {
                if (!humanPlayers.contains(playerId)) {
                    return true; // 不再是人类
                }
                // 检查是否被神之救赎转化
                return plugin.getPlayerManager().isConvertedByRedemption(playerId);
            });
        } else {
            // 如果人类数量超过触发数量，清除所有神圣守护
            holyGuardianPlayers.clear();
            holyGuardianActivationTime.clear();
        }
    }
    
    /**
     * 激活神圣守护
     */
    private void activateHolyGuardian(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null || !player.isOnline()) {
            return;
        }
        
        holyGuardianPlayers.add(playerId);
        holyGuardianActivationTime.put(playerId, System.currentTimeMillis());
        holyGuardianHitCount.put(playerId, 0); // 初始化攻击计数为0
        
        // 应用视觉效果
        applyHolyGuardianEffects(player);
        
        // 给予神之救赎道具
        giveHolyRedemptionItem(player);
        
        // 广播消息
        if (plugin.getConfigManager().isDivineGuardianBroadcastEnabled()) {
            String message = String.format("§6§l[神圣守护] §e玩家 §a%s §e获得了神圣守护！", player.getName());
            Bukkit.broadcastMessage(message);
        }
        
        player.sendMessage("§6§l[神圣守护] §a你获得了神圣守护效果！");
        player.sendMessage("§e✨ 你被白色粒子特效环绕");
        player.sendMessage("§7• 鬼玩家尝试感染你时会被随机传送");
        player.sendMessage("§7• 游戏最后90秒你将变为§6猎魔人§7（金色粒子特效）");
        player.sendMessage("§7• 你获得了§6神之救赎§7道具（右键点击鬼玩家转化）");
        player.sendMessage("§7• 作为最后的人类，你有机会获得额外奖金");
    }
    
    /**
     * 应用神圣守护视觉效果
     */
    private void applyHolyGuardianEffects(Player player) {
        // 发光效果
        player.addPotionEffect(new PotionEffect(
            PotionEffectType.GLOWING, 
            plugin.getConfigManager().getHolyGuardianEffectDuration() * 20, 
            0, 
            true, 
            true
        ));
        
        // 神圣守护白色粒子特效
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!holyGuardianPlayers.contains(player.getUniqueId()) || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                
                Location loc = player.getLocation();
                
                // 白色附魔台粒子（主要效果）
                player.getWorld().spawnParticle(
                    ParticleCompat.enchant(), 
                    loc.clone().add(0, 2.2, 0), 
                    12, 
                    0.5, 0.3, 0.5, 
                    0.05
                );
                
                // 白色灰烬粒子环绕
                player.getWorld().spawnParticle(
                    Particle.WHITE_ASH, 
                    loc.clone().add(0, 1.8, 0), 
                    8, 
                    0.7, 0.2, 0.7, 
                    0.02
                );
                
                // 雪花粒子（神圣感）
                player.getWorld().spawnParticle(
                    Particle.SNOWFLAKE, 
                    loc.clone().add(0, 2.5, 0), 
                    6, 
                    0.4, 0.1, 0.4, 
                    0.03
                );
                
                // 白色云朵粒子（柔和效果）
                player.getWorld().spawnParticle(
                    ParticleCompat.cloud(), 
                    loc.clone().add(0, 1.5, 0), 
                    4, 
                    0.3, 0.1, 0.3, 
                    0.01
                );
                
                // 如果是猎魔人，添加金色粒子叠加
                if (demonHunterPlayers.contains(player.getUniqueId())) {
                    // 猎魔人金色粒子效果会在applyDemonHunterEffects中单独处理
                }
            }
        }.runTaskTimer(plugin, 0L, 15L); // 每0.75秒一次（15 ticks）
        
        // 额外：激活时的爆发效果
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!holyGuardianPlayers.contains(player.getUniqueId()) || !player.isOnline()) {
                    return;
                }
                
                Location loc = player.getLocation();
                
                // 激活时的白色爆发效果
                for (int i = 0; i < 2; i++) {
                    player.getWorld().spawnParticle(
                        ParticleCompat.firework(), 
                        loc.clone().add(0, 1, 0), 
                        25, 
                        1.0, 0.5, 1.0, 
                        0.1
                    );
                }
            }
        }.runTaskLater(plugin, 5L); // 激活后5 ticks执行
    }
    
    /**
     * 给予神之救赎道具
     */
    private void giveHolyRedemptionItem(Player player) {
        int maxUses = Math.max(1, plugin.getConfigManager().getHolyRedemptionUses());
        holyRedemptionUses.put(player.getUniqueId(), maxUses);
        
        // 防重复：背包/物品栏已有神之救赎则不重复发放
        for (ItemStack inv : player.getInventory().getContents()) {
            if (inv != null && inv.hasItemMeta() && inv.getItemMeta().hasDisplayName() &&
                inv.getItemMeta().getDisplayName().contains("神之救赎")) {
                return;
            }
        }
        
        ItemStack holyRedemption = createHolyRedemptionItem(maxUses);
        
        // 神之救赎强制放置第一格（slot 0）
        int preferredSlot = 0;
        ItemStack currentItem = player.getInventory().getItem(preferredSlot);
        if (currentItem != null && currentItem.getType() != Material.AIR) {
            // 第一格已有物品：尝试移到空闲格，满则直接替换
            int emptySlot = player.getInventory().firstEmpty();
            if (emptySlot != -1 && emptySlot != 0) {
                player.getInventory().setItem(emptySlot, currentItem);
                player.getInventory().setItem(preferredSlot, holyRedemption);
                player.sendMessage("§6§l[神圣守护] §a神之救赎已放置在第一格，原有物品已移动到其他位置");
            } else {
                // 没有空位，直接替换
                player.getInventory().setItem(preferredSlot, holyRedemption);
                player.sendMessage("§6§l[神圣守护] §a神之救赎已放置在第一格，替换了原有物品");
            }
        } else {
            // 第一格为空，直接放置
            player.getInventory().setItem(preferredSlot, holyRedemption);
        }
        
        player.sendMessage("§6§l[神圣守护] §a你获得了神之救赎道具！");
        player.sendMessage("§e✨ 右键点击鬼玩家将其转化回人类");
        player.sendMessage("§7• 转化成功后你会被随机传送");
        player.sendMessage("§7• 每局最多使用: §e" + maxUses + "次");
        player.sendMessage("§7• 使用冷却: §e" + plugin.getConfigManager().getDemonHunterHolyRedemptionCooldown() + "秒");
        player.sendMessage("§7• 转化鬼玩家可获得额外奖金");
        player.sendMessage("§e提示: 将神之救赎拿在手中，右键点击鬼玩家使用");
    }
    
    /**
     * 创建神之救赎道具
     */
    private ItemStack createHolyRedemptionItem(int remainingUses) {
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6§l神之救赎");
            List<String> lore = new ArrayList<>();
            lore.add("§7右键点击鬼玩家将其转化回人类");
            lore.add("§7使用后你会被随机传送");
            lore.add("§e剩余使用次数: " + remainingUses);
            lore.add("§8神圣守护专属道具");
            meta.setLore(lore);
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    /**
     * 处理鬼玩家尝试感染神圣守护玩家
     * @param attacker 攻击者（鬼玩家）
     * @param target 目标（神圣守护玩家）
     * @return 是否阻止感染
     */
    public boolean handleGhostAttack(Player attacker, Player target) {
        UUID targetId = target.getUniqueId();
        
        // 获取攻击者角色（母体或普通鬼）
        boolean isMotherGhost = plugin.getPlayerManager().getPlayerRole(attacker.getUniqueId()) == 
            io.Sriptirc_wp_1258.gost.managers.PlayerManager.PlayerRole.GHOST_MOTHER;
        
        // ========== 猎魔人阶段特殊规则 ==========
        if (isDemonHunterPhase) {
            // 如果目标不是猎魔人（普通人类）
            if (!demonHunterPlayers.contains(targetId)) {
                // 猎魔人阶段，普通鬼不能感染人类，只能躲避猎杀
                if (!isMotherGhost) {
                    attacker.sendMessage("§c§l[猎魔人阶段] §c猎魔人阶段普通鬼不能感染人类，只能躲避猎杀！");
                    return true; // 阻止感染
                }
                // 母体可以感染普通人类，继续后续检查
            }
            // 如果目标是猎魔人
            else {
                // 猎魔人阶段，只有母体可以攻击猎魔人（无论是否有神圣守护）
                if (!isMotherGhost) {
                    attacker.sendMessage("§c§l[猎魔人] §c只有母体可以攻击猎魔人！");
                    return true; // 阻止攻击
                }
                
                // 检查猎魔人是否有神圣守护
                if (!holyGuardianPlayers.contains(targetId)) {
                    // 无神圣守护的猎魔人，母体击中4次感染（用击中次数判定，不依赖血量）
                    int hits = demonHunterHitCount.getOrDefault(targetId, 0) + 1;
                    demonHunterHitCount.put(targetId, hits);
                    attacker.sendMessage(String.format("§6§l[母体] §e你击中了猎魔人！(§c%d§e/§c4§e)", hits));
                    target.sendMessage(String.format("§c§l[猎魔人] §c你被母体击中！(§4%d§c/§4%d§c)", hits, 4));
                    // 视觉效果
                    Location loc = target.getLocation();
                    loc.getWorld().spawnParticle(ParticleCompat.damageIndicator(), loc, 10, 0.5, 0.5, 0.5, 0.5);
                    loc.getWorld().playSound(loc, SoundCompat.playerHurt(), 1.0f, 1.0f);
                    if (hits >= 4) {
                        // 4次击中 → 感染（进入旁观）
                        demonHunterHitCount.remove(targetId);
                        killDemonHunter(target, attacker);
                    }
                    return true; // 阻止后续处理
                }
                // 有神圣守护的猎魔人，母体需要攻击3次才能破除，继续后续处理
            }
        }
        
        // ========== 神圣守护检查 ==========
        // 检查目标是否拥有神圣守护（检查是否过期）
        if (!hasActiveHolyGuardian(targetId)) {
            return false; // 不阻止感染
        }
        
        // ========== 神圣守护抵挡逻辑 ==========
        // 无论是否是母体，神圣守护都可以抵挡3次攻击
        int maxCharges = plugin.getConfigManager().getHolyGuardianDefenseCharges();
        // 救赎者独立守护：固定2次防御
        if (redeemerGuardianMaxCharges.containsKey(targetId)) {
            maxCharges = redeemerGuardianMaxCharges.get(targetId);
        }
        int currentHits = holyGuardianHitCount.getOrDefault(targetId, 0);
        
        // 增加攻击计数
        currentHits++;
        holyGuardianHitCount.put(targetId, currentHits);
        
        // 检查是否达到最大抵挡次数
        if (currentHits < maxCharges) {
            // 还有剩余次数，随机传送攻击者
            if (plugin.getConfigManager().isHolyGuardianTeleportAttackerEnabled()) {
                teleportAttackerRandomly(attacker);
                String messagePrefix = isDemonHunterPhase && demonHunterPlayers.contains(targetId) 
                    ? "§c§l[猎魔人]" : "§c§l[神圣守护]";
                attacker.sendMessage(String.format("%s §c目标受到神圣守护保护！剩余抵挡次数: §e%d§c/§e%d", 
                    messagePrefix, maxCharges - currentHits, maxCharges));
                target.sendMessage(String.format("§6§l[神圣守护] §a神圣守护保护了你！剩余抵挡次数: §e%d§a/§e%d", 
                    maxCharges - currentHits, maxCharges));
            }
            return true; // 阻止感染
        } else {
            // 达到最大抵挡次数，破除神圣守护
            if (breakHolyGuardian(target)) {
                String messagePrefix = isDemonHunterPhase && demonHunterPlayers.contains(targetId) 
                    ? "§6§l[猎魔人]" : "§6§l[神圣守护]";
                attacker.sendMessage(String.format("%s §e你成功破除了目标的神圣守护！", messagePrefix));
                target.sendMessage("§c§l[神圣守护] §c你的神圣守护已被破除！");
            }
            return true; // 阻止本次感染，但神圣守护已被破除
        }
    }
    
    /**
     * 随机传送攻击者
     */
    private void teleportAttackerRandomly(Player attacker) {
        double radius = plugin.getConfigManager().getHolyGuardianTeleportRadius();
        Location currentLoc = attacker.getLocation();
        
        // 在半径范围内随机生成新位置
        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = random.nextDouble() * radius;
        
        double newX = currentLoc.getX() + Math.cos(angle) * distance;
        double newZ = currentLoc.getZ() + Math.sin(angle) * distance;
        
        // 获取安全高度
        World world = currentLoc.getWorld();
        if (world != null) {
            int newY = world.getHighestBlockYAt((int) newX, (int) newZ) + 1;
            Location newLoc = new Location(world, newX, newY, newZ);
            
            // 安全传送
            attacker.teleport(newLoc);
            
            // 视觉效果
            world.spawnParticle(Particle.PORTAL, currentLoc, 50, 0.5, 0.5, 0.5, 0.5);
            world.playSound(currentLoc, SoundCompat.endermanTeleport(), 1.0f, 1.0f);
            world.spawnParticle(Particle.PORTAL, newLoc, 50, 0.5, 0.5, 0.5, 0.5);
            world.playSound(newLoc, SoundCompat.endermanTeleport(), 1.0f, 1.0f);
        }
    }
    
    /**
     * 破除神圣守护
     */
    private boolean breakHolyGuardian(Player player) {
        UUID playerId = player.getUniqueId();
        
        // 检查是否已经破除
        if (!holyGuardianPlayers.contains(playerId)) {
            return false; // 已经破除
        }
        
        // 移除神圣守护效果
        holyGuardianPlayers.remove(playerId);
        holyGuardianActivationTime.remove(playerId);
        holyGuardianHitCount.remove(playerId); // 清理攻击计数
        
        // 清除发光效果
        player.removePotionEffect(PotionEffectType.GLOWING);
        
        // 视觉效果
        Location loc = player.getLocation();
        loc.getWorld().spawnParticle(ParticleCompat.enchantedHit(), loc, 30, 0.5, 0.5, 0.5, 0.5);
        loc.getWorld().playSound(loc, SoundCompat.ironGolemDamage(), 1.0f, 0.5f);
        
        return true;
    }
    
    /**
     * 检查并进入猎魔人阶段
     */
    public void checkAndEnterDemonHunterPhase(int remainingTime) {
        if (!plugin.getConfigManager().isDivineGuardianSystemEnabled()) {
            return;
        }
        
        if (!plugin.getGameManager().isGameRunning()) {
            return;
        }
        
        int phaseStartTime = plugin.getConfigManager().getDemonHunterPhaseStartTime();
        
        // 检查是否应该进入猎魔人阶段
        if (!isDemonHunterPhase && remainingTime <= phaseStartTime) {
            enterDemonHunterPhase();
        }
    }
    
    /**
     * 进入猎魔人阶段
     */
    private void enterDemonHunterPhase() {
        isDemonHunterPhase = true;
        
        // v2.3.2：猎魔人阶段所有玩家饱食度回满（阶段内不再自然掉饥饿，由 PlayerListener 锁定）
        for (UUID pid : plugin.getPlayerManager().getAllPlayers()) {
            Player p = Bukkit.getPlayer(pid);
            if (p != null && p.isOnline()) {
                p.setFoodLevel(20);
                p.setSaturation(20);
            }
        }
        
        // 清除所有的神之救赎道具
        clearAllHolyRedemptionItems();
        
        // 在猎魔人阶段，为所有剩余人类玩家获得/刷新神圣守护（配置开关控制）
        List<UUID> humanPlayers = plugin.getPlayerManager().getHumanPlayers();
        boolean holyGuardianOnPhase = plugin.getConfigManager().isDemonHunterHolyGuardianEnabled();
        
        // 清理：移除不再是人类的玩家
        holyGuardianPlayers.removeIf(playerId -> !humanPlayers.contains(playerId));
        
        if (holyGuardianOnPhase) {
            // 开关开启：所有剩余人类（猎魔人）获得/刷新神圣守护
            for (UUID playerId : humanPlayers) {
                if (holyGuardianPlayers.contains(playerId)) {
                    // 刷新：重置攻击计数与激活时间，重新应用效果
                    holyGuardianActivationTime.put(playerId, System.currentTimeMillis());
                    holyGuardianHitCount.put(playerId, 0);
                    Player p = Bukkit.getPlayer(playerId);
                    if (p != null && p.isOnline()) {
                        applyHolyGuardianEffects(p);
                        p.sendMessage("§6§l[神圣守护] §a猎魔人阶段神圣守护已刷新！");
                    }
                } else {
                    activateHolyGuardian(playerId);
                }
            }
        } else {
            // 开关关闭：仅按原逻辑激活（排除被神之救赎转化的人类，已有不刷新）
            holyGuardianPlayers.removeIf(playerId -> plugin.getPlayerManager().isConvertedByRedemption(playerId));
            for (UUID playerId : humanPlayers) {
                boolean isConvertedByRedemption = plugin.getPlayerManager().isConvertedByRedemption(playerId);
                if (!isConvertedByRedemption && !holyGuardianPlayers.contains(playerId)) {
                    activateHolyGuardian(playerId);
                }
            }
        }
        
        // 检查是否有神圣守护玩家
        if (holyGuardianPlayers.isEmpty()) {
            // 没有玩家拥有神圣守护
            if (plugin.getConfigManager().isDivineGuardianBroadcastEnabled()) {
                Bukkit.broadcastMessage("§6§l[猎魔人阶段] §c猎魔人阶段开始！");
                Bukkit.broadcastMessage("§7• 没有玩家拥有神圣守护，无法产生猎魔人");
                Bukkit.broadcastMessage("§7• 神之救赎道具已被清除");
                Bukkit.broadcastMessage("§7• 游戏继续，但无猎魔人参与");
            }
        } else {
            // 将所有拥有神圣守护的玩家变为猎魔人
            for (UUID playerId : holyGuardianPlayers) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    // 重置神圣守护攻击计数（猎魔人阶段重新计算）
                    holyGuardianHitCount.put(playerId, 0);
                    activateDemonHunter(player);
                }
            }
            
            // 广播消息
            if (plugin.getConfigManager().isDivineGuardianBroadcastEnabled()) {
                Bukkit.broadcastMessage("§6§l[猎魔人阶段] §c猎魔人阶段开始！");
                Bukkit.broadcastMessage("§7• 拥有神圣守护的人类变为猎魔人");
                Bukkit.broadcastMessage("§7• 猎魔人可以使用收割者道具击杀鬼玩家");
                Bukkit.broadcastMessage("§7• 只有母体可以攻击猎魔人");
                Bukkit.broadcastMessage("§7• 神之救赎道具已被清除");
            }
        }
        
        // 调整所有鬼玩家的血量
        adjustGhostHealthInDemonHunterPhase();
        
        // 检查是否需要新增母体
        checkAndAddAdditionalMother();
        
        // v2.3.3：母体进化水晶已改为在「神圣守护阶段」出现，见 checkAndActivateHolyGuardian
    }
    
    private boolean hasSpawnedMotherEmeraldThisGame = false;
    
    /**
     * 解析母体进化水晶生成坐标（v2.3.3：支持固定坐标 / 区域中心 / 区域随机）
     */
    private Location resolveMotherEmeraldLocation(
            io.Sriptirc_wp_1258.gost.managers.AreaManager.GameArea area,
            Location pos1, Location pos2) {
        String mode = plugin.getConfigManager().getMotherEmeraldLocationMode();
        org.bukkit.World world = pos1.getWorld();
        if ("fixed".equalsIgnoreCase(mode)) {
            if (plugin.getConfigManager().hasMotherEmeraldFixedLocation()) {
                return plugin.getConfigManager().getMotherEmeraldFixedLocation(world);
            }
        }
        if ("center".equalsIgnoreCase(mode) || "fixed".equalsIgnoreCase(mode)) {
            // fixed 未配置坐标时回退中心
            double cx = (pos1.getX() + pos2.getX()) / 2.0;
            double cy = Math.max(pos1.getY(), pos2.getY());
            double cz = (pos1.getZ() + pos2.getZ()) / 2.0;
            return new Location(world, cx, cy, cz);
        }
        // random（默认）：区域内随机
        double rx = pos1.getX() + random.nextDouble() * (pos2.getX() - pos1.getX());
        double rz = pos1.getZ() + random.nextDouble() * (pos2.getZ() - pos1.getZ());
        double ry = Math.max(pos1.getY(), pos2.getY());
        return new Location(world, rx, ry, rz);
    }
    
    /**
     * 神圣守护阶段：按配置生成母体进化水晶
     */
    private void trySpawnMotherEmeraldOnHolyGuardianPhase() {
        if (!plugin.getConfigManager().isMotherEmeraldEnabled()) {
            return;
        }
        String phase = plugin.getConfigManager().getMotherEmeraldSpawnPhase();
        if (!"holy-guardian".equalsIgnoreCase(phase) && !"both".equalsIgnoreCase(phase)) {
            return;
        }
        if (hasSpawnedMotherEmeraldThisGame) {
            return;
        }
        if (motherEmerald != null && motherEmerald.isValid() && !motherEmerald.isDead()) {
            return;
        }
        int threshold = plugin.getConfigManager().getMotherEmeraldPlayerThreshold();
        if (plugin.getPlayerManager().getAllPlayers().size() < threshold) {
            return;
        }
        hasSpawnedMotherEmeraldThisGame = true;
        spawnMotherEmerald();
    }
    
    /**
     * 在游戏区域放置发光绿宝石（普通鬼拾取可变为母体）
     */
    private void spawnMotherEmerald() {
        removeMotherEmerald(); // 清理旧的
        
        io.Sriptirc_wp_1258.gost.managers.AreaManager.GameArea area = plugin.getAreaManager().getSelectedArea();
        if (area == null || area.getPos1() == null || area.getPos2() == null) {
            return;
        }
        Location pos1 = area.getPos1();
        Location pos2 = area.getPos2();
        Location spawnLoc = resolveMotherEmeraldLocation(area, pos1, pos2);
        
        // 创建发光绿宝石实体（不可拾取进入背包，防止被当普通物品拿走）
        ItemStack emerald = new ItemStack(Material.EMERALD);
        ItemMeta meta = emerald.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(isEnglish() ? "§a§lMother Upgrade Emerald" : "§a§l母体升级宝石");
            meta.setLore(java.util.Arrays.asList(
                isEnglish() ? "§7Pick up to become the Mother Ghost!" : "§7拾取后变为母体鬼！",
                isEnglish() ? "§7Only normal ghosts can use it" : "§7仅普通鬼可拾取"
            ));
            emerald.setItemMeta(meta);
        }
        
        motherEmerald = (spawnLoc.getWorld() != null ? spawnLoc.getWorld() : pos1.getWorld()).dropItem(spawnLoc, emerald);
        motherEmerald.setPickupDelay(plugin.getConfigManager().getMotherEmeraldPickupDelayTicks());
        motherEmerald.setCustomName(isEnglish() ? "§a§l✦ Mother Emerald ✦" : "§a§l✦ 母体绿宝石 ✦");
        motherEmerald.setCustomNameVisible(true);
        motherEmerald.setGlowing(true); // 发光效果
        
        // 全服公告
        boolean en = isEnglish();
        if (plugin.getConfigManager().isDivineGuardianBroadcastEnabled()) {
            if (en) {
                Bukkit.broadcastMessage("§a§l✦ A Mother Upgrade Emerald has appeared in the area! §7Normal ghosts: pick it up to become the Mother Ghost!");
            } else {
                Bukkit.broadcastMessage("§a§l✦ 游戏区域出现母体升级绿宝石！§7普通鬼拾取后可变身为母体鬼！");
            }
        }
        
        // 音效提示
        Bukkit.getOnlinePlayers().forEach(p -> 
            p.playSound(p.getLocation(), SoundCompat.toastChallengeComplete(), 0.5f, 1.2f));
        
        // 持续发光标记任务（每隔一段时间刷新，防止实体发光失效）
        motherEmeraldGlowTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (motherEmerald != null && motherEmerald.isValid() && !motherEmerald.isDead()) {
                motherEmerald.setGlowing(true);
                motherEmerald.getWorld().spawnParticle(ParticleCompat.enchant(), 
                    motherEmerald.getLocation().add(0, 0.5, 0), 5, 0.3, 0.3, 0.3, 0.05);
            } else {
                motherEmeraldGlowTask.cancel();
            }
        }, 0L, 20L);
    }
    
    /**
     * 处理普通鬼拾取母体绿宝石 → 变为母体
     */
    public boolean handleMotherEmeraldPickup(Player player) {
        if (motherEmerald == null || motherEmerald.isDead() || !motherEmerald.isValid()) {
            return false;
        }
        UUID pid = player.getUniqueId();
        // 仅普通鬼可拾取
        if (!plugin.getPlayerManager().isGhost(pid) || plugin.getPlayerManager().isMotherGhost(pid)) {
            return false;
        }
        
        // 转变为母体
        plugin.getPlayerManager().setPlayerRole(pid, io.Sriptirc_wp_1258.gost.managers.PlayerManager.PlayerRole.GHOST_MOTHER);
        
        // 移除绿宝石
        removeMotherEmerald();
        
        // 提示与音效
        boolean en = isEnglish();
        if (en) {
            player.sendTitle("§4👑 You are the Mother Ghost!", "§7Hunt the Demon Hunters!", 10, 40, 10);
            Bukkit.broadcastMessage("§4§l" + player.getName() + " §cpicked up the Mother Emerald and became the MOTHER GHOST!");
        } else {
            player.sendTitle("§4👑 你成为了母体鬼!", "§7去猎杀猎魔人吧!", 10, 40, 10);
            Bukkit.broadcastMessage("§4§l" + player.getName() + " §c拾取了母体绿宝石，变身为母体鬼！");
        }
        player.playSound(player.getLocation(), SoundCompat.enderDragonGrowl(), 1.0f, 0.8f);
        
        // 调整血量
        player.setHealth(plugin.getConfigManager().getGhostMotherHealth());
        return true;
    }
    
    /**
     * 移除母体绿宝石及其发光任务
     */
    private void removeMotherEmerald() {
        if (motherEmerald != null) {
            motherEmerald.remove();
            motherEmerald = null;
        }
        if (motherEmeraldGlowTask != null) {
            motherEmeraldGlowTask.cancel();
            motherEmeraldGlowTask = null;
        }
    }
    
    /**
     * 检查实体是否是该玩家拾取时对应的母体绿宝石
     */
    public boolean isMotherEmerald(org.bukkit.entity.Item item) {
        return motherEmerald != null && motherEmerald.isValid() && motherEmerald.equals(item);
    }
    
    /**
     * 记录额外奖励点数（结算时发放，与奖池分离）
     */
    public void addRewardPoints(UUID playerId, int points) {
        rewardPoints.merge(playerId, points, Integer::sum);
    }
    
    /**
     * 结算额外奖励（游戏结束时调用）：由服务器额外发放游戏币，与奖池分离
     */
    public void settleRewardPoints() {
        if (rewardPoints.isEmpty()) return;
        boolean en = isEnglish();
        for (Map.Entry<UUID, Integer> e : rewardPoints.entrySet()) {
            Player p = Bukkit.getPlayer(e.getKey());
            if (p != null && p.isOnline() && e.getValue() > 0) {
                plugin.getEconomyManager().depositServerReward(p, e.getValue());
                p.sendMessage(en
                    ? "§6§l[Reward] §aBonus reward: §e" + e.getValue() + " §acoins"
                    : "§6§l[奖励] §a额外奖励: §e" + e.getValue() + " §a游戏币");
            }
        }
        rewardPoints.clear();
    }
    
    /**
     * 激活猎魔人
     */
    private void activateDemonHunter(Player player) {
        UUID playerId = player.getUniqueId();
        
        demonHunterPlayers.add(playerId);
        demonHunterKillCount.put(playerId, 0);
        
        // 给予收割者道具
        giveReaperWeapon(player);
        
        // 应用猎魔人效果
        applyDemonHunterEffects(player);
        
        // 设置猎魔人血量
        double demonHunterHealth = plugin.getConfigManager().getDemonHunterHealth();
        player.setHealth(demonHunterHealth);
        
        player.sendMessage("§6§l[猎魔人] §a你成为了猎魔人！");
        player.sendMessage("§e✨ 你被金色粒子特效环绕");
        player.sendMessage(String.format("§7• 血量调整为: §c%.1f❤", demonHunterHealth));
        player.sendMessage("§7• 你获得了§4收割者§7道具（左键伤害攻击，右键范围收割）");
        player.sendMessage("§7• 攻击冷却: §e" + plugin.getConfigManager().getReaperWeaponAttackCooldown() + "秒");
        player.sendMessage("§7• 只有§c母体§7可以攻击你");
        player.sendMessage("§7• 击杀鬼玩家可获得§e30%§7人类奖池奖金");
        player.sendMessage("§7• 被母体击杀后进入旁观模式");
    }
    
    /**
     * 给予收割者道具
     */
    private void giveReaperWeapon(Player player) {
        ItemStack reaperWeapon = createReaperWeapon();
        
        // 强制放在第一个物品栏（slot 0）
        ItemStack currentItem = player.getInventory().getItem(0);
        if (currentItem != null && currentItem.getType() != Material.AIR) {
            // 如果第一个物品栏已有物品，尝试寻找其他空位
            int emptySlot = player.getInventory().firstEmpty();
            if (emptySlot != -1) {
                player.getInventory().setItem(emptySlot, currentItem);
                player.getInventory().setItem(0, reaperWeapon);
                player.sendMessage("§6§l[猎魔人] §a收割者已放置在第一个物品栏，原有物品已移动到其他位置");
            } else {
                // 没有空位，直接替换
                player.getInventory().setItem(0, reaperWeapon);
                player.sendMessage("§6§l[猎魔人] §a收割者已放置在第一个物品栏，替换了原有物品");
            }
        } else {
            // 第一个物品栏为空，直接放置
            player.getInventory().setItem(0, reaperWeapon);
        }
        
        player.sendMessage("§6§l[猎魔人] §a你获得了收割者！");
        player.sendMessage("§e✨ 专属武器 - §4收割者");
        player.sendMessage("§7• 攻击鬼玩家造成伤害，血量归零即击杀");
        player.sendMessage("§7• 每次攻击伤害: §c" + plugin.getConfigManager().getReaperWeaponDamagePerHit() + "❤");
        player.sendMessage("§7• 攻击冷却: §e" + plugin.getConfigManager().getReaperWeaponAttackCooldown() + "秒");
        player.sendMessage("§7• 每击杀一名鬼玩家获得§e30%§7人类奖池奖金");
        player.sendMessage("§7• 只有§c母体§7可以对抗你");
        player.sendMessage("§e提示: 可以使用左键或右键攻击鬼玩家");
    }
    
    /**
     * 创建收割者道具
     */
    private ItemStack createReaperWeapon() {
        ItemStack item = new ItemStack(Material.NETHERITE_HOE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§4§l收割者");
            List<String> lore = new ArrayList<>();
            lore.add("§7猎魔人专属武器");
            lore.add("§c攻击鬼玩家两次即可击杀");
            lore.add("§e攻击冷却: " + plugin.getConfigManager().getReaperWeaponAttackCooldown() + "秒");
            lore.add("§8只有母体可以对抗猎魔人");
            meta.setLore(lore);
            
            // 附魔光效
            if (plugin.getConfigManager().isReaperWeaponEnchantGlowEnabled()) {
                meta.addEnchant(ApiCompat.unbreaking(), 1, true);
            }
            
            // 设置不可破坏
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    /**
     * 应用猎魔人效果
     */
    private void applyDemonHunterEffects(Player player) {
        // 速度效果
        player.addPotionEffect(new PotionEffect(
            PotionEffectType.SPEED, 
            999999, 
            1, // 速度II
            true, 
            true
        ));
        
        // 更明显的发光效果
        player.addPotionEffect(new PotionEffect(
            PotionEffectType.GLOWING, 
            999999, 
            0, 
            true, 
            true
        ));
        
        // 猎魔人金色粒子特效
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!demonHunterPlayers.contains(player.getUniqueId()) || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                
                Location loc = player.getLocation();
                
                // 金色火焰粒子（主要效果）
                player.getWorld().spawnParticle(
                    Particle.FLAME, 
                    loc.clone().add(0, 2.3, 0), 
                    15, 
                    0.6, 0.4, 0.6, 
                    0.05
                );
                
                // 金色火花粒子
                player.getWorld().spawnParticle(
                    ParticleCompat.firework(), 
                    loc.clone().add(0, 2.0, 0), 
                    10, 
                    0.8, 0.3, 0.8, 
                    0.08
                );
                
                // 金色附魔粒子
                player.getWorld().spawnParticle(
                    ParticleCompat.enchant(), 
                    loc.clone().add(0, 2.5, 0), 
                    8, 
                    0.4, 0.2, 0.4, 
                    0.03
                );
                
                // 金色发光粒子
                player.getWorld().spawnParticle(
                    Particle.GLOW, 
                    loc.clone().add(0, 1.8, 0), 
                    12, 
                    0.5, 0.2, 0.5, 
                    0.04
                );
                
                // 金色烟雾粒子（底部）
                player.getWorld().spawnParticle(
                    Particle.CAMPFIRE_COSY_SMOKE, 
                    loc.clone().add(0, 0.5, 0), 
                    6, 
                    0.3, 0.1, 0.3, 
                    0.02
                );
            }
        }.runTaskTimer(plugin, 0L, 10L); // 每0.5秒一次（10 ticks），更频繁
        
        // 激活时的金色爆发效果
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!demonHunterPlayers.contains(player.getUniqueId()) || !player.isOnline()) {
                    return;
                }
                
                Location loc = player.getLocation();
                
                // 金色爆炸效果
                for (int i = 0; i < 3; i++) {
                    player.getWorld().spawnParticle(
                        ParticleCompat.explosion(), 
                        loc.clone().add(0, 1.5 + i * 0.5, 0), 
                        8, 
                        1.2, 0.3, 1.2, 
                        0.15
                    );
                }
                
                // 金色闪电效果
                player.getWorld().spawnParticle(
                    ParticleCompat.electricSpark(), 
                    loc.clone().add(0, 2.0, 0), 
                    20, 
                    1.0, 0.5, 1.0, 
                    0.1
                );
                
                player.playSound(loc, SoundCompat.lightningThunder(), 1.0f, 0.8f);
            }
        }.runTaskLater(plugin, 2L); // 激活后2 ticks执行
    }
    
    /**
     * 处理猎魔人攻击（v2.3.3：改为伤害值判定 + 显示伤害量和剩余血量）
     */
    public boolean handleDemonHunterAttack(Player demonHunter, Player ghost) {
        UUID demonHunterId = demonHunter.getUniqueId();
        UUID ghostId = ghost.getUniqueId();
        
        // 检查攻击者是否是猎魔人
        if (!demonHunterPlayers.contains(demonHunterId)) {
            return false;
        }
        
        // 旁观中的鬼不可被攻击
        if (spectatorPlayers.contains(ghostId)) {
            return true;
        }
        
        // 检查冷却
        Long lastAttack = reaperAttackCooldown.get(demonHunterId);
        double cooldown = plugin.getConfigManager().getReaperWeaponAttackCooldown();
        if (lastAttack != null && System.currentTimeMillis() - lastAttack < cooldown * 1000) {
            demonHunter.sendMessage("§c§l[收割者] §c攻击冷却中！");
            return true;
        }
        
        // 更新冷却
        reaperAttackCooldown.put(demonHunterId, System.currentTimeMillis());
        
        // 获取配置伤害值
        double damage = plugin.getConfigManager().getReaperWeaponDamagePerHit();
        
        // v2.3.3：按伤害值扣血，血量归零进入旁观 + 复活倒计时
        applyReaperDamage(ghost, demonHunter, damage, false);
        
        return true;
    }
    
    /**
     * 对鬼玩家施加收割者伤害（v2.3.3 伤害值判定）
     * @param isHarvest 是否为「收割」范围技能
     */
    private void applyReaperDamage(Player ghost, Player demonHunter, double damage, boolean isHarvest) {
        double maxHealth = 20.0;
        try {
            if (ghost.getAttribute(ApiCompat.maxHealth()) != null) {
                maxHealth = ghost.getAttribute(ApiCompat.maxHealth()).getValue();
            }
        } catch (Exception ignored) {}
        
        double currentHealth = ghost.getHealth();
        double newHealth = Math.max(0.0, currentHealth - damage);
        
        // 显示伤害量和剩余血量
        String hitLabel = isHarvest ? "§6§l[收割]" : "§6§l[收割者]";
        demonHunter.sendMessage(String.format("%s §e命中！ 造成伤害 §c%.1f§e，剩余血量 §c%.1f§e/§c%.1f",
            hitLabel, damage, newHealth, maxHealth));
        ghost.sendMessage(String.format("§c§l[收割者] §c你被猎魔人攻击！受到 §4%.1f §c伤害，剩余血量 §4%.1f§c/§4%.1f",
            damage, newHealth, maxHealth));
        
        if (newHealth <= 0.0) {
            // 血量归零 → 旁观 + 复活倒计时
            try {
                ghost.setHealth(Math.min(1.0, maxHealth));
            } catch (Exception e) {
                ghost.setHealth(maxHealth);
            }
            killGhostWithReaper(ghost, demonHunter);
        } else {
            ghost.setHealth(newHealth);
            Vector direction = ghost.getLocation().toVector().subtract(demonHunter.getLocation().toVector()).normalize();
            ghost.setVelocity(direction.multiply(0.5).setY(0.3));
            ghost.getWorld().spawnParticle(Particle.CRIT, ghost.getLocation(), 10, 0.5, 0.5, 0.5, 0.5);
            ghost.playSound(ghost.getLocation(), SoundCompat.playerAttackCrit(), 1.0f, 1.0f);
        }
    }
    
    /**
     * 收割者右键范围攻击 - 收割（v2.3.3：伤害与普通攻击一致，4格范围，凋零死亡音效）
     */
    public boolean handleDemonHunterHarvest(Player demonHunter) {
        UUID demonHunterId = demonHunter.getUniqueId();
        if (!demonHunterPlayers.contains(demonHunterId)) return false;
        Long last = reaperHarvestCooldown.get(demonHunterId);
        int harvestCooldown = plugin.getConfigManager().getReaperHarvestCooldown();
        if (last != null && System.currentTimeMillis() - last < harvestCooldown * 1000L) {
            long rem = harvestCooldown - (System.currentTimeMillis() - last) / 1000;
            demonHunter.sendMessage("§c§l[收割] §c技能冷却中，剩余 §e" + rem + "§c 秒");
            return true;
        }
        reaperHarvestCooldown.put(demonHunterId, System.currentTimeMillis());
        Location center = demonHunter.getLocation();
        double range = plugin.getConfigManager().getReaperHarvestRange();
        double damage = plugin.getConfigManager().getReaperWeaponDamagePerHit();
        int hitCount = 0;
        
        // 冲击粒子特效
        center.getWorld().spawnParticle(ParticleCompat.explosionEmitter(), center, 1);
        center.getWorld().spawnParticle(ParticleCompat.cloud(), center, 40, 1.5, 1.0, 1.5, 0.1);
        
        for (Entity entity : center.getWorld().getNearbyEntities(center, range, range, range)) {
            if (!(entity instanceof Player)) continue;
            Player target = (Player) entity;
            if (target.getUniqueId().equals(demonHunterId)) continue;
            if (!plugin.getPlayerManager().isGhost(target.getUniqueId())) continue;
            if (spectatorPlayers.contains(target.getUniqueId())) continue;
            
            // 伤害与普通攻击一致
            applyReaperDamage(target, demonHunter, damage, true);
            hitCount++;
        }
        
        // 收割音效：凋零死亡音效
        center.getWorld().playSound(center, SoundCompat.witherDeath(), 1.0f, 1.0f);
        demonHunter.sendMessage("§6§l[收割] §a收割完成！命中 §c" + hitCount + " §a名鬼玩家");
        sendBilingualTitle(demonHunter,
            "§d⚔ 收割!", "§7命中 " + hitCount + " 名鬼玩家",
            "§d⚔ Harvest!", "§7Hit " + hitCount + " ghost(s)");
        return true;
    }
    
    /**
     * 用收割者击杀鬼玩家
     */
    private void killGhostWithReaper(Player ghost, Player demonHunter) {
        UUID ghostId = ghost.getUniqueId();
        UUID demonHunterId = demonHunter.getUniqueId();

        // v2.3.3：鬼被击杀 → 进入旁观模式 + 复活倒计时
        // （此前只加隐身导致旁观模式从未生效，现改为完整链路）
        startGhostDeathSequence(ghost);

        // 更新击杀计数
        int kills = demonHunterKillCount.getOrDefault(demonHunterId, 0) + 1;
        demonHunterKillCount.put(demonHunterId, kills);

        // 记录击杀奖励（结算时发放，与奖池分离）：普通鬼50 / 母体100
        boolean isMotherGhost = plugin.getPlayerManager().isMotherGhost(ghostId);
        addRewardPoints(demonHunterId, isMotherGhost
            ? plugin.getConfigManager().getRewardKillMother()
            : plugin.getConfigManager().getRewardKillNormal());

        // 奖励分配
        distributeDemonHunterKillReward(demonHunter, ghost);

        // 广播消息
        if (plugin.getConfigManager().isDivineGuardianBroadcastEnabled()) {
            String message = String.format("§6§l[猎魔人] §e猎魔人 §a%s §e击杀了鬼玩家 §c%s§e！",
                demonHunter.getName(), ghost.getName());
            Bukkit.broadcastMessage(message);

            // 如果启用复活，显示复活倒计时
            if (plugin.getConfigManager().isRespawnEnabled() && isDemonHunterPhase) {
                int respawnTime = plugin.getConfigManager().getRespawnTime();
                Bukkit.broadcastMessage(String.format("§7%s 将在 §e%d秒 §7后复活", ghost.getName(), respawnTime));
            }
        }

        // 视觉效果：在击杀处放烟花 + 爆炸粒子 + 音效
        Location loc = ghost.getLocation();
        loc.getWorld().spawnParticle(ParticleCompat.explosionEmitter(), loc, 1);
        loc.getWorld().playSound(loc, SoundCompat.witherDeath(), 1.0f, 1.0f);
        spawnKillFirework(loc);

        // 击杀字幕提醒（中英）
        sendBilingualTitle(demonHunter,
            "§a💥 击杀成功!",
            "§7你击杀了 §c" + ghost.getName(),
            "§a💥 Elimination!",
            "§7You eliminated §c" + ghost.getName());

        demonHunter.sendMessage("§6§l[猎魔人] §a你击杀了一名鬼玩家！");
        demonHunter.sendMessage("§7累计击杀: §e" + kills);

        // 检查游戏是否结束
        checkGameEnd();
    }
    
    /**
     * 在指定位置放烟花（击杀反馈）
     */
    private void spawnKillFirework(Location loc) {
        org.bukkit.entity.Firework fw = loc.getWorld().spawn(loc.clone().add(0, 1, 0), org.bukkit.entity.Firework.class);
        org.bukkit.inventory.meta.FireworkMeta meta = fw.getFireworkMeta();
        meta.addEffect(org.bukkit.FireworkEffect.builder()
            .with(org.bukkit.FireworkEffect.Type.BURST)
            .withColor(org.bukkit.Color.RED, org.bukkit.Color.YELLOW)
            .withFade(org.bukkit.Color.ORANGE)
            .trail(true)
            .build());
        meta.setPower(0);
        fw.setFireworkMeta(meta);
        // 立即引爆
        org.bukkit.scheduler.BukkitRunnable run = new org.bukkit.scheduler.BukkitRunnable() {
            @Override public void run() { fw.detonate(); }
        };
        run.runTaskLater(plugin, 1L);
    }
    
    /**
     * 击杀猎魔人
     */
    private void killDemonHunter(Player demonHunter, Player motherGhost) {
        UUID demonHunterId = demonHunter.getUniqueId();
        
        // v2.3.3：被母体击杀的猎魔人立刻转为「死掉的普通鬼」
        // 死亡 → 旁观 → 复活倒计时 → 以普通鬼身份在游戏区域内复活
        demonHunterPlayers.remove(demonHunterId);
        holyGuardianPlayers.remove(demonHunterId);
        plugin.getPlayerManager().setPlayerRole(demonHunterId,
            io.Sriptirc_wp_1258.gost.managers.PlayerManager.PlayerRole.GHOST_NORMAL);
        
        // 进入旁观 + 复活倒计时（倒计时结束变回普通鬼继续游戏）
        startGhostDeathSequence(demonHunter);
        
        // 奖励分配
        distributeMotherKillDemonHunterReward(motherGhost, demonHunter);
        
        // 广播消息
        if (plugin.getConfigManager().isDivineGuardianBroadcastEnabled()) {
            String message = String.format("§6§l[猎魔人] §c母体 §4%s §c击杀了猎魔人 §6%s§c！", 
                motherGhost.getName(), demonHunter.getName());
            Bukkit.broadcastMessage(message);
        }
        
        // 视觉效果
        Location loc = demonHunter.getLocation();
        loc.getWorld().spawnParticle(ParticleCompat.dragonBreath(), loc, 50, 0.5, 0.5, 0.5, 0.5);
        loc.getWorld().playSound(loc, SoundCompat.enderDragonDeath(), 1.0f, 1.0f);
        
        motherGhost.sendMessage("§6§l[母体] §a你成功击杀了猎魔人！");
        
        // 检查游戏是否结束
        checkGameEnd();
    }
    
    /**
     * 将玩家设置为旁观者
     */
    private void setPlayerToSpectator(Player player) {
        UUID playerId = player.getUniqueId();
        
        spectatorPlayers.add(playerId);
        spectatorOriginalLocations.put(playerId, player.getLocation().clone());
        
        // 设置为旁观模式
        ServerCompat.setGameModeSafely(player, GameMode.SPECTATOR);
        
        // 清除所有效果
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        
        // 防止离开游戏区域
        startSpectatorBoundaryCheck(player);
        
        player.sendMessage("§c§l[游戏结束] §c你已被淘汰，进入旁观模式");
        
        // 检查是否在猎魔人阶段且启用复活机制
        if (isDemonHunterPhase && plugin.getConfigManager().isRespawnEnabled() && 
            plugin.getPlayerManager().isGhost(playerId)) {
            // 启动复活计时器
            startRespawnTimer(player);
            int respawnTime = plugin.getConfigManager().getRespawnTime();
            player.sendMessage(String.format("§e你将在 §6%d秒 §e后复活", respawnTime));
        } else {
            player.sendMessage("§7请等待游戏结束");
        }
    }
    
    /**
     * 检查并新增母体
     */
    private void checkAndAddAdditionalMother() {
        if (!plugin.getConfigManager().isAdditionalMotherEnabled()) {
            return;
        }
        
        if (!isDemonHunterPhase) {
            return;
        }
        
        if (hasAdditionalMotherSpawned) {
            return;
        }
        
        // 获取所有玩家
        List<UUID> allPlayers = plugin.getPlayerManager().getAllPlayers();
        int playerThreshold = plugin.getConfigManager().getAdditionalMotherPlayerThreshold();
        
        if (allPlayers.size() >= playerThreshold) {
            // 从普通鬼玩家中随机选择一位变为母体
            List<UUID> normalGhosts = new ArrayList<>();
            for (UUID playerId : allPlayers) {
                if (plugin.getPlayerManager().getPlayerRole(playerId) == 
                    io.Sriptirc_wp_1258.gost.managers.PlayerManager.PlayerRole.GHOST_NORMAL) {
                    normalGhosts.add(playerId);
                }
            }
            
            if (!normalGhosts.isEmpty()) {
                UUID newMotherId = normalGhosts.get(random.nextInt(normalGhosts.size()));
                Player newMother = Bukkit.getPlayer(newMotherId);
                
                if (newMother != null && newMother.isOnline()) {
                    // 设置为母体
                    plugin.getPlayerManager().setPlayerRole(newMotherId, 
                        io.Sriptirc_wp_1258.gost.managers.PlayerManager.PlayerRole.GHOST_MOTHER);
                    
                    additionalMothers.add(newMotherId);
                    hasAdditionalMotherSpawned = true;
                    

                    // 广播消息
                    if (plugin.getConfigManager().isDivineGuardianBroadcastEnabled()) {
                        String message = String.format("§6§l[猎魔人阶段] §c玩家 §4%s §c被选为新增母体！", 
                            newMother.getName());
                        Bukkit.broadcastMessage(message);
                        Bukkit.broadcastMessage("§7• 只有母体可以攻击猎魔人");
                    }
                    
                    newMother.sendMessage("§6§l[母体] §a你被选为新增母体！");
                    newMother.sendMessage("§7• 你现在可以攻击猎魔人");
                    newMother.sendMessage("§7• 破除神圣守护后一击即可击杀猎魔人");
                }
            }
        }
    }
    
    /**
     * 分配猎魔人击杀奖励
     */
    private void distributeDemonHunterKillReward(Player demonHunter, Player ghost) {
        if (!plugin.getConfigManager().isVaultEnabled()) {
            return;
        }
        
        double rewardRatio = plugin.getConfigManager().getDemonHunterKillRewardRatio();
        double humanPool = plugin.getEconomyManager().getHumanPrizePool();
        double reward = humanPool * rewardRatio;
        
        if (reward > 0) {
            plugin.getEconomyManager().giveMoney(demonHunter, reward);
            demonHunter.sendMessage("§6════════════════════════════════");
            demonHunter.sendMessage("§e💰 猎魔人击杀奖励 💰");
            demonHunter.sendMessage(String.format("§a你获得了 §e§l%.2f §a金币奖励！", reward));
            demonHunter.sendMessage(String.format("§7（基于人类奖池的 §e%.0f%%§7 分配）", rewardRatio * 100));
            demonHunter.sendMessage("§6════════════════════════════════");
            demonHunter.playSound(demonHunter.getLocation(), SoundCompat.playerLevelup(), 1.0f, 1.0f);
        }
    }
    
    /**
     * 分配母体击杀猎魔人奖励
     */
    private void distributeMotherKillDemonHunterReward(Player mother, Player demonHunter) {
        if (!plugin.getConfigManager().isVaultEnabled()) {
            return;
        }
        
        double rewardRatio = plugin.getConfigManager().getMotherKillDemonHunterRewardRatio();
        double totalPool = plugin.getEconomyManager().getTotalPrizePool();
        double reward = totalPool * rewardRatio;
        
        if (reward > 0) {
            plugin.getEconomyManager().giveMoney(mother, reward);
            mother.sendMessage("§6════════════════════════════════");
            mother.sendMessage("§c💰 母体击杀猎魔人奖励 💰");
            mother.sendMessage(String.format("§a你获得了 §e§l%.2f §a金币奖励！", reward));
            mother.sendMessage(String.format("§7（基于总奖池的 §e%.0f%%§7 分配）", rewardRatio * 100));
            mother.sendMessage("§7（高于普通感染奖励）");
            mother.sendMessage("§6════════════════════════════════");
            mother.playSound(mother.getLocation(), SoundCompat.enderDragonGrowl(), 0.8f, 0.9f);
            mother.playSound(mother.getLocation(), SoundCompat.playerLevelup(), 1.0f, 0.8f);
        }
    }
    
    /**
     * 检查游戏是否应该结束
     */
    private void checkGameEnd() {
        // 检查是否所有猎魔人都被淘汰
        boolean allDemonHuntersEliminated = true;
        for (UUID demonHunterId : demonHunterPlayers) {
            Player player = Bukkit.getPlayer(demonHunterId);
            if (player != null && player.isOnline() && player.getGameMode() != GameMode.SPECTATOR) {
                allDemonHuntersEliminated = false;
                break;
            }
        }
        
        // 检查是否所有鬼都被淘汰
        boolean allGhostsEliminated = true;
        List<UUID> allPlayers = plugin.getPlayerManager().getAllPlayers();
        for (UUID playerId : allPlayers) {
            if (spectatorPlayers.contains(playerId)) {
                continue; // 旁观者不计入
            }
            
            io.Sriptirc_wp_1258.gost.managers.PlayerManager.PlayerRole role = 
                plugin.getPlayerManager().getPlayerRole(playerId);
            if (role == io.Sriptirc_wp_1258.gost.managers.PlayerManager.PlayerRole.GHOST_NORMAL ||
                role == io.Sriptirc_wp_1258.gost.managers.PlayerManager.PlayerRole.GHOST_MOTHER) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null && player.isOnline() && player.getGameMode() != GameMode.SPECTATOR) {
                    allGhostsEliminated = false;
                    break;
                }
            }
        }
        
        // 如果所有猎魔人或所有鬼都被淘汰，结束游戏
        if (allDemonHuntersEliminated || allGhostsEliminated) {
            plugin.getGameManager().endGame(true);
        }
    }
    
    /**
     * 开始旁观者边界检查
     */
    private void startSpectatorBoundaryCheck(Player spectator) {
        UUID playerId = spectator.getUniqueId();
        
        // 如果已有任务，先取消
        if (spectatorBoundaryTasks.containsKey(playerId)) {
            org.bukkit.scheduler.BukkitTask existingTask = spectatorBoundaryTasks.get(playerId);
            if (existingTask != null && !existingTask.isCancelled()) {
                existingTask.cancel();
            }
        }
        
        org.bukkit.scheduler.BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!spectator.isOnline() || spectator.getGameMode() != GameMode.SPECTATOR) {
                    this.cancel();
                    spectatorBoundaryTasks.remove(playerId);
                    return;
                }
                
                // 检查是否离开游戏区域
                io.Sriptirc_wp_1258.gost.managers.AreaManager.GameArea area = 
                    plugin.getAreaManager().getSelectedArea();
                if (area != null) {
                    Location originalLoc = spectatorOriginalLocations.get(spectator.getUniqueId());
                    if (originalLoc != null) {
                        // 如果距离原始位置太远，传送回去
                        if (spectator.getLocation().distance(originalLoc) > 50) {
                            spectator.teleport(originalLoc);
                            spectator.sendMessage("§c§l[旁观] §c你不能离开游戏区域！");
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // 每秒检查一次
        
        spectatorBoundaryTasks.put(playerId, task);
    }
    
    /**
     * 取消旁观者边界检查任务
     */
    private void cancelSpectatorBoundaryCheck(UUID playerId) {
        org.bukkit.scheduler.BukkitTask task = spectatorBoundaryTasks.remove(playerId);
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }
    
    /**
     * 清除所有玩家的神之救赎道具
     */
    private void clearAllHolyRedemptionItems() {
        // 遍历所有在线玩家，移除神之救赎道具
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != null && player.isOnline()) {
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null && item.getType() != Material.AIR) {
                        ItemMeta meta = item.getItemMeta();
                        if (meta != null && meta.hasDisplayName()) {
                            String displayName = meta.getDisplayName();
                            // 检查是否是神之救赎道具
                            if (displayName.equals("§6§l神之救赎") || displayName.contains("神之救赎")) {
                                player.getInventory().remove(item);
                                player.sendMessage("§6§l[猎魔人阶段] §c神之救赎道具已被清除");
                            }
                        }
                    }
                }
            }
        }
        
        // 清除神之救赎使用次数
        holyRedemptionUses.clear();
        holyRedemptionCooldown.clear();
        redeemerPlayers.clear();
        rewardPoints.clear();
    }
    
    /**
     * 清理游戏数据
     */
    public void cleanup() {
        // 恢复所有旁观者为生存模式
        for (UUID playerId : spectatorPlayers) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                ServerCompat.setGameModeSafely(player, GameMode.SURVIVAL);
                player.sendMessage("§a游戏结束，你已恢复为生存模式！");
            }
        }
        
        // 取消所有旁观者边界检查任务
        for (org.bukkit.scheduler.BukkitTask task : spectatorBoundaryTasks.values()) {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
        }
        
        holyGuardianPlayers.clear();
        holyGuardianActivationTime.clear();
        holyGuardianHitCount.clear();
        redeemerGuardianMaxCharges.clear();
        demonHunterPlayers.clear();
        demonHunterKillCount.clear();
        reaperAttackCooldown.clear();
        reaperHarvestCooldown.clear();
        ghostHitCount.clear();
        motherHitCount.clear();
        demonHunterHitCount.clear();
        holyRedemptionUses.clear();
        holyRedemptionCooldown.clear();
        additionalMothers.clear();
        spectatorPlayers.clear();
        spectatorOriginalLocations.clear();
        spectatorBoundaryTasks.clear();
        
        hasSpawnedMotherEmeraldThisGame = false;
        // 清理母体绿宝石
        removeMotherEmerald();
        
        // 清理复活数据
        cleanupRespawnData();
        
        isDemonHunterPhase = false;
        hasAdditionalMotherSpawned = false;
    }
    
    /**
     * 重置游戏数据
     */
    public void resetGame() {
        cleanup();
    }
    
    // ==================== 公共方法 ====================
    
    public boolean isHolyGuardian(UUID playerId) {
        return holyGuardianPlayers.contains(playerId);
    }
    
    public boolean isDemonHunter(UUID playerId) {
        return demonHunterPlayers.contains(playerId);
    }
    
    public boolean isInDemonHunterPhase() {
        return isDemonHunterPhase;
    }
    
    public int getDemonHunterKillCount(UUID playerId) {
        return demonHunterKillCount.getOrDefault(playerId, 0);
    }
    
    public int getHolyRedemptionRemainingUses(UUID playerId) {
        return holyRedemptionUses.getOrDefault(playerId, 0);
    }
    
    public boolean isSpectator(UUID playerId) {
        return spectatorPlayers.contains(playerId);
    }
    
    // ==================== 复活机制相关方法 ====================

    /**
     * v2.3.3：鬼死亡完整链路
     * 死亡 → 进入旁观模式 → 倒计时 → 倒计时结束 → 游戏区域内随机坐标换回生存模式复活
     */
    private void startGhostDeathSequence(Player ghost) {
        UUID playerId = ghost.getUniqueId();

        // 1) 进入旁观模式（替代此前妥协使用的隐身）
        enterSpectatorForDeath(ghost);

        // 2) 启动复活倒计时
        if (isDemonHunterPhase && plugin.getConfigManager().isRespawnEnabled()) {
            startRespawnTimer(ghost);
        } else {
            // 未启用复活：保持旁观直到游戏结束
            ghost.sendMessage("§c§l[淘汰] §c你已被淘汰，进入旁观模式");
        }
    }

    /**
     * 将死亡玩家送入旁观模式（v2.3.3 正式启用）
     *
     * 此前失败原因排查：
     * 1) killGhostWithReaper 只调用了 startRespawnTimer，而该方法只加 INVISIBILITY，从未切 SPECTATOR
     * 2) setPlayerToSpectator 仅用于「猎魔人被母体击杀」路径，鬼被收割者击杀走不到这里
     * 3) respawnPlayer 没有 setGameMode(SURVIVAL)，即便切了旁观也会卡死在旁观
     * 4) PlayerListener.onPlayerRespawn 会把旁观者继续按旁观处理，进一步锁死
     * 现改为统一链路：旁观 → 倒计时 → 显式切回生存 → 随机坐标复活
     */
    private void enterSpectatorForDeath(Player player) {
        UUID playerId = player.getUniqueId();

        spectatorPlayers.add(playerId);
        spectatorOriginalLocations.put(playerId, player.getLocation().clone());
        deathLocations.put(playerId, player.getLocation().clone());

        // 清除所有药水效果（含可能残留的隐身）
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.removePotionEffect(PotionEffectType.INVISIBILITY);

        // 正式进入旁观模式
        ServerCompat.setGameModeSafely(player, GameMode.SPECTATOR);

        // 防止旁观时飞出游戏区域
        startSpectatorBoundaryCheck(player);
    }

    /**
     * 启动复活计时器
     */
    private void startRespawnTimer(Player player) {
        UUID playerId = player.getUniqueId();

        // 取消现有的复活任务
        cancelRespawnTask(playerId);

        int respawnTime = plugin.getConfigManager().getRespawnTime();

        // 记录死亡坐标
        deathLocations.put(playerId, player.getLocation().clone());

        // 死亡字幕提醒（中英）
        sendBilingualTitle(player,
            "§c💀 你被击杀了!", "§7等待复活中...",
            "§c💀 You were eliminated!", "§7Waiting to respawn...");
        player.sendActionBar(isEnglish() ? "§eRespawning in §6" + respawnTime + "§e seconds" : "§e将在 §6" + respawnTime + "§e 秒后复活");

        // v2.3.3：死亡状态 = 旁观模式（不再使用隐身妥协方案）
        if (player.getGameMode() != GameMode.SPECTATOR) {
            ServerCompat.setGameModeSafely(player, GameMode.SPECTATOR);
        }
        player.removePotionEffect(PotionEffectType.INVISIBILITY);

        long respawnTimestamp = System.currentTimeMillis() + (respawnTime * 1000L);
        respawnTimers.put(playerId, respawnTimestamp);

        // 创建复活任务
        org.bukkit.scheduler.BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            respawnPlayer(playerId);
        }, respawnTime * 20L); // BukkitTask 保留；Folia 下由 ServerCompat 兜底 // 转换为游戏刻

        respawnTasks.put(playerId, task);

        // 发送倒计时消息
        sendRespawnCountdown(player, respawnTime);
    }
    
    /**
     * 发送复活倒计时消息
     */
    private void sendRespawnCountdown(Player player, int totalSeconds) {
        for (int i = 1; i <= totalSeconds; i++) {
            final int remaining = totalSeconds - i;
            ServerCompat.runLater(plugin, () -> {
                if (player.isOnline() && respawnTimers.containsKey(player.getUniqueId())) {
                    if (remaining > 0) {
                        player.sendActionBar(isEnglish() 
                            ? String.format("§eRespawning: §6%d§es", remaining)
                            : String.format("§e复活倒计时: §6%d§e秒", remaining));
                    } else {
                        player.sendActionBar(isEnglish() ? "§aRespawning..." : "§a正在复活...");
                    }
                }
            }, i * 20L);
        }
    }
    
    /**
     * 复活玩家
     * v2.3.3：倒计时结束 → 游戏区域内随机坐标 → 显式换回生存模式
     */
    private void respawnPlayer(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null || !player.isOnline()) {
            // 玩家不在线，清理数据
            respawnTimers.remove(playerId);
            cancelRespawnTask(playerId);
            return;
        }

        // 检查玩家是否在复活列表中
        if (!respawnTimers.containsKey(playerId)) {
            respawnTimers.remove(playerId);
            cancelRespawnTask(playerId);
            return;
        }

        // 死亡坐标已不再用于锁定，清理记录
        deathLocations.remove(playerId);

        // 【先】随机地点复活（游戏区域内随机坐标）
        teleportToRandomAreaLocation(player);

        // 【后】显式换回生存模式（v2.3.3 关键修复：此前遗漏导致卡在旁观）
        ServerCompat.setGameModeSafely(player, GameMode.SURVIVAL);

        // 清除隐身（兼容旧逻辑残留）
        player.removePotionEffect(PotionEffectType.INVISIBILITY);

        // 移除旁观者数据
        spectatorPlayers.remove(playerId);
        spectatorOriginalLocations.remove(playerId);

        // 取消边界检查任务
        cancelSpectatorBoundaryCheck(playerId);

        // 设置血量（根据配置）
        double health = plugin.getConfigManager().getGhostNormalHealth();
        if (plugin.getPlayerManager().isMotherGhost(playerId)) {
            health = plugin.getConfigManager().getGhostMotherHealth();
        }
        // 安全设置血量
        try {
            double maxHealth = player.getAttribute(ApiCompat.maxHealth()).getValue();
            player.setHealth(Math.min(health, maxHealth));
        } catch (Exception e) {
            player.setHealth(player.getAttribute(ApiCompat.maxHealth()) != null
                ? player.getAttribute(ApiCompat.maxHealth()).getValue() : 20.0);
        }

        // 清除复活数据
        respawnTimers.remove(playerId);
        cancelRespawnTask(playerId);

        // 发送消息
        player.sendMessage(isEnglish() ? "§a§l[Respawn] §aYou have respawned!" : "§a§l[复活] §a你已复活！");
        player.sendMessage(isEnglish()
            ? String.format("§7Current health: §c%.1f❤", health)
            : String.format("§7当前血量: §c%.1f❤", health));

        // 重生字幕提醒（中英）
        sendBilingualTitle(player,
            "§a✨ 你已重生!", "§7继续战斗!",
            "§a✨ You have respawned!", "§7Keep fighting!");
        player.sendActionBar(isEnglish() ? "§aBack in action!" : "§a你已重返战场!");

        // 广播消息
        if (plugin.getConfigManager().isDivineGuardianBroadcastEnabled()) {
            Bukkit.broadcastMessage(isEnglish()
                ? String.format("§6§l[Hunter] §eGhost §c%s §ehas respawned!", player.getName())
                : String.format("§6§l[猎魔人] §e鬼玩家 §c%s §e已复活！", player.getName()));
        }

        // 视觉效果
        Location loc = player.getLocation();
        loc.getWorld().spawnParticle(ParticleCompat.totem(), loc, 30, 0.5, 0.5, 0.5, 0.5);
        loc.getWorld().playSound(loc, SoundCompat.totemUse(), 1.0f, 1.0f);
    }
    
    /**
     * 随机传送玩家到游戏区域内随机坐标（用于随机复活）
     */
    private void teleportToRandomAreaLocation(Player player) {
        io.Sriptirc_wp_1258.gost.managers.AreaManager.GameArea area = plugin.getAreaManager().getSelectedArea();
        if (area != null && area.getPos1() != null && area.getPos2() != null) {
            Location pos1 = area.getPos1();
            Location pos2 = area.getPos2();
            double rx = pos1.getX() + random.nextDouble() * (pos2.getX() - pos1.getX());
            double rz = pos1.getZ() + random.nextDouble() * (pos2.getZ() - pos1.getZ());
            double ry = Math.max(pos1.getY(), pos2.getY());
            ServerCompat.teleportSafely(player, new Location(pos1.getWorld(), rx, ry, rz));
        }
    }
    
    /**
     * 取消复活任务
     */
    private void cancelRespawnTask(UUID playerId) {
        org.bukkit.scheduler.BukkitTask task = respawnTasks.remove(playerId);
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }
    
    /**
     * 清理复活数据
     */
    private void cleanupRespawnData() {
        // 取消所有复活任务
        for (org.bukkit.scheduler.BukkitTask task : respawnTasks.values()) {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
        }
        
        respawnTimers.clear();
        respawnTasks.clear();
        deathLocations.clear();
    }
    
    /**
     * 检查玩家是否在复活倒计时中
     */
    public boolean isInRespawnTimer(UUID playerId) {
        return respawnTimers.containsKey(playerId);
    }
    
    /**
     * 获取剩余复活时间（秒）
     */
    public int getRemainingRespawnTime(UUID playerId) {
        Long timestamp = respawnTimers.get(playerId);
        if (timestamp == null) {
            return 0;
        }
        
        long remaining = timestamp - System.currentTimeMillis();
        return Math.max(0, (int) (remaining / 1000));
    }
    
    /**
     * 在猎魔人阶段调整所有鬼玩家的血量
     */
    private void adjustGhostHealthInDemonHunterPhase() {
        for (UUID playerId : plugin.getPlayerManager().getGhostPlayers()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline() && !spectatorPlayers.contains(playerId)) {
                double health;
                if (plugin.getPlayerManager().isMotherGhost(playerId)) {
                    health = plugin.getConfigManager().getGhostMotherHealth();
                } else {
                    health = plugin.getConfigManager().getGhostNormalHealth();
                }
                
                player.setHealth(health);
                player.sendMessage(String.format("§6§l[猎魔人阶段] §e你的血量已调整为 §c%.1f❤", health));
            }
        }
    }
    
    // ==================== 兼容旧命令系统的方法 ====================
    
    /**
     * 重新加载配置
     */
    public void reload() {
        // 空实现，兼容旧命令
    }
    
    /**
     * 设置守护模式
     */
    public boolean setGuardianMode(String mode) {
        // 空实现，兼容旧命令
        return true;
    }
    
    /**
     * 获取神圣守护玩家
     */
    public UUID getDivineGuardianPlayer() {
        // 返回第一个神圣守护玩家或null
        if (!holyGuardianPlayers.isEmpty()) {
            return holyGuardianPlayers.iterator().next();
        }
        return null;
    }
    
    /**
     * 获取剩余次数
     */
    public int getRemainingCharges(UUID playerId) {
        // 返回0，兼容旧命令
        return 0;
    }
    
    /**
     * 获取模式显示名称
     */
    public String getModeDisplayName() {
        return "默认模式";
    }
    
    /**
     * 获取救赎者剩余使用次数
     */
    public int getRedeemerRemainingUses(UUID playerId) {
        return holyRedemptionUses.getOrDefault(playerId, 0);
    }
    
    /**
     * 检查并激活神圣守护
     * 兼容旧命令调用
     */
    public void checkAndActivateDivineGuardian(List<UUID> humanPlayers) {
        // 调用实际实现方法
        checkAndActivateHolyGuardian(humanPlayers);
    }
    
    /**
     * 检查玩家是否有活跃的神圣守护（未过期）
     */
    public boolean hasActiveHolyGuardian(UUID playerId) {
        if (!holyGuardianPlayers.contains(playerId)) {
            return false;
        }
        
        // 检查持续时间
        Long activationTime = holyGuardianActivationTime.get(playerId);
        if (activationTime == null) {
            holyGuardianPlayers.remove(playerId);
            return false;
        }
        
        int duration = plugin.getConfigManager().getHolyGuardianEffectDuration();
        long elapsed = System.currentTimeMillis() - activationTime;
        
        if (elapsed > duration * 1000L) {
            // 神圣守护已过期
            holyGuardianPlayers.remove(playerId);
            holyGuardianActivationTime.remove(playerId);
            
            // 清除发光效果
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.removePotionEffect(PotionEffectType.GLOWING);
            }
            
            return false;
        }
        
        return true;
    }
    
    /**
     * 检查玩家是否是救赎者
     */
    public boolean isRedeemer(UUID playerId) {
        // 检查玩家是否有神之救赎使用次数
        return redeemerPlayers.contains(playerId) || 
            (holyRedemptionUses.containsKey(playerId) && holyRedemptionUses.get(playerId) > 0);
    }
    
    /**
     * 随机绑定救赎者（每局最多2名），神之救赎道具发放时调用
     */
    public boolean assignRedeemer(Player player) {
        UUID playerId = player.getUniqueId();
        if (redeemerPlayers.contains(playerId)) return true;
        if (redeemerPlayers.size() >= 2) return false;
        if (plugin.getPlayerManager().isGhost(playerId)) return false;
        redeemerPlayers.add(playerId);
        holyRedemptionUses.put(playerId, 1);
        // v2.3.3/2.3.1：救赎者直接获得独立神圣守护（2次防御），不影响原有神圣守护系统
        redeemerGuardianMaxCharges.put(playerId, 2);
        activateHolyGuardian(playerId);
        player.sendMessage("§6§l[救赎者] §a你获得了独立神圣守护（2次防御）！");
        // 直接发放神之救赎道具（单次使用，强制第一格）
        giveHolyRedemptionItem(player);
        // 救赎者绑定字幕提醒（中英）
        sendBilingualTitle(player,
            "§6✝ 你被选为救赎者!",
            "§7获得神之救赎道具（1次）",
            "§6✝ You are the Redeemer!",
            "§7Holy Redemption x1 granted");
        if (isEnglish()) {
            player.sendMessage("§6§l[Redeemer] §eYou have been chosen as the Redeemer!");
            player.sendMessage("§7• Holy Redemption x1 — right-click a ghost to convert them");
        } else {
            player.sendMessage("§6§l[救赎者] §e你被选为救赎者！");
            player.sendMessage("§7• 获得神之救赎道具（1次），可将鬼转化回人类");
        }
        return true;
    }
    
    public int getRedeemerCount() {
        return redeemerPlayers.size();
    }
    
    private boolean isEnglish() {
        return "en_US".equals(plugin.getConfigManager().getDefaultLanguage());
    }
    
    private void sendBilingualTitle(Player p, String zhTitle, String zhSub, String enTitle, String enSub) {
        if (isEnglish()) p.sendTitle(enTitle, enSub, 10, 40, 10);
        else p.sendTitle(zhTitle, zhSub, 10, 40, 10);
    }

    
    /**
     * 使用神之救赎
     */
    public boolean useHolyRedemption(Player redeemer, Player target) {
        UUID redeemerId = redeemer.getUniqueId();
        UUID targetId = target.getUniqueId();
        
        // 检查是否是救赎者（有神之救赎使用次数）
        if (!holyRedemptionUses.containsKey(redeemerId) || holyRedemptionUses.get(redeemerId) <= 0) {
            redeemer.sendMessage("§c§l[神之救赎] §c你不是救赎者或已无使用次数！");
            return false;
        }
        
        // 检查冷却时间
        Long lastUse = holyRedemptionCooldown.get(redeemerId);
        int cooldown = plugin.getConfigManager().getDemonHunterHolyRedemptionCooldown();
        if (lastUse != null && System.currentTimeMillis() - lastUse < cooldown * 1000L) {
            long remaining = cooldown - (System.currentTimeMillis() - lastUse) / 1000L;
            redeemer.sendMessage(String.format("§c§l[神之救赎] §c道具冷却中，剩余 §e%d§c 秒", remaining));
            return false;
        }
        
        // 检查目标是否是鬼玩家
        if (!plugin.getPlayerManager().isGhost(targetId)) {
            redeemer.sendMessage("§c§l[神之救赎] §c只能对鬼玩家使用！");
            return false;
        }
        
        // 检查目标是否是母体（母体不能被转化）
        if (plugin.getPlayerManager().isMotherGhost(targetId)) {
            redeemer.sendMessage("§c§l[神之救赎] §c无法转化母体鬼！");
            return false;
        }
        
        // 执行转化
        boolean success = plugin.getPlayerManager().convertGhostToHuman(targetId);
        if (!success) {
            redeemer.sendMessage("§c§l[神之救赎] §c转化失败！");
            return false;
        }
        
        // 移除目标的神圣守护状态（如果有）
        holyGuardianPlayers.remove(targetId);
        holyGuardianActivationTime.remove(targetId);
        
        // 更新使用次数
        int remainingUses = holyRedemptionUses.get(redeemerId) - 1;
        holyRedemptionUses.put(redeemerId, remainingUses);
        
        // 设置冷却时间
        holyRedemptionCooldown.put(redeemerId, System.currentTimeMillis());
        
        // 移除道具（如果手中还有）
        ItemStack itemInHand = redeemer.getInventory().getItemInMainHand();
        if (itemInHand != null && itemInHand.getType() != Material.AIR) {
            ItemMeta meta = itemInHand.getItemMeta();
            if (meta != null && meta.hasDisplayName() && 
                meta.getDisplayName().equals("§6§l神之救赎")) {
                redeemer.getInventory().setItemInMainHand(null);
            }
        }
        
        // 记录救赎奖励（结算时发放）
        addRewardPoints(redeemerId, plugin.getConfigManager().getRewardRedeemGhost());
        
        // 单次使用：道具用完即消失，不重新发放
        // 发送消息（中英）
        boolean en = isEnglish();
        if (en) {
            redeemer.sendMessage("§6§l[Holy Redemption] §aYou converted §e" + target.getName() + " §aback to human!");
            redeemer.sendMessage(remainingUses > 0
                ? String.format("§7Remaining uses: §e%d", remainingUses)
                : "§7Holy Redemption item has been used up");
            target.sendMessage("§6§l[Holy Redemption] §aYou were redeemed by §e" + redeemer.getName() + "§a!");
        } else {
            redeemer.sendMessage("§6§l[神之救赎] §a你成功将 §e" + target.getName() + " §a转化回人类！");
            redeemer.sendMessage(remainingUses > 0
                ? String.format("§7剩余使用次数: §e%d", remainingUses)
                : "§7神之救赎道具已用完");
            target.sendMessage("§6§l[神之救赎] §a你被 §e" + redeemer.getName() + " §a使用神之救赎转化回人类！");
        }
        
        // 居中字幕提示（中英）
        if (en) {
            redeemer.sendTitle("§6✝ Redemption Success! ✝", "§a" + target.getName() + " is human again!", 10, 40, 10);
            target.sendTitle("§6✝ You are redeemed! ✝", "§a" + redeemer.getName() + " saved you!", 10, 40, 10);
        } else {
            redeemer.sendTitle("§6✝ 神之救赎成功! ✝", "§a" + target.getName() + " 已转化回人类!", 10, 40, 10);
            target.sendTitle("§6✝ 你被救赎了! ✝", "§a" + redeemer.getName() + " 将你转化回人类!", 10, 40, 10);
        }
        
        // 广播消息
        if (plugin.getConfigManager().isDivineGuardianBroadcastEnabled()) {
            if (en) {
                Bukkit.broadcastMessage(String.format("§6§l[Holy Redemption] §eRedeemer §a%s §econverted §a%s §eback to human!",
                    redeemer.getName(), target.getName()));
            } else {
                Bukkit.broadcastMessage(String.format("§6§l[神之救赎] §e救赎者 §a%s §e使用神之救赎将 §a%s §e转化回人类！", 
                    redeemer.getName(), target.getName()));
            }
        }
        
        // 视觉效果
        Location loc = target.getLocation();
        loc.getWorld().spawnParticle(ParticleCompat.totem(), loc, 30, 0.5, 0.5, 0.5, 0.5);
        loc.getWorld().playSound(loc, SoundCompat.totemUse(), 1.0f, 1.0f);
        
        // 随机传送救赎者（防止被报复）
        if (plugin.getConfigManager().isHolyRedemptionTeleportOnUse()) {
            teleportAttackerRandomly(redeemer);
        }
        redeemer.sendMessage(en 
            ? "§6§l[Holy Redemption] §eYou were teleported to safety!"
            : "§6§l[神之救赎] §e你被随机传送以保护安全！");
        
        // 更新神圣守护检查
        List<UUID> humanPlayers = plugin.getPlayerManager().getHumanPlayers();
        checkAndActivateHolyGuardian(humanPlayers);
        
        return true;
    }
}