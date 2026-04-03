package io.Sriptirc_wp_1258.gost.managers;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ItemSpawnManager {
    
    private final Gost plugin;
    private CancellableTask spawnTask;
    private final Map<UUID, Integer> playerItemCounts = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    // 随机道具配置
    private final List<RandomItemConfig> randomItems = new ArrayList<>();
    
    public ItemSpawnManager(Gost plugin) {
        this.plugin = plugin;
        loadRandomItems();
    }
    
    /**
     * 加载随机道具配置
     */
    private void loadRandomItems() {
        randomItems.clear();
        
        // 臭牛排 - 速度2效果14秒 + 发光效果10秒 + 冷却30秒
        int stinkySteakSpeedDuration = 14;
        int stinkySteakGlowDuration = 10;
        int stinkySteakCooldown = 30;
        randomItems.add(new RandomItemConfig(
            "stinky-steak",
            Material.COOKED_BEEF,
            plugin.getLanguageManager().getMessage("item_names.stinky_steak"),
            Arrays.asList(
                plugin.getLanguageManager().getMessage("item_names.stinky_steak_lore1"),
                plugin.getLanguageManager().getMessage("item_names.stinky_steak_lore2", stinkySteakSpeedDuration),
                plugin.getLanguageManager().getMessage("item_names.stinky_steak_lore3", stinkySteakGlowDuration),
                plugin.getLanguageManager().getMessage("item_names.stinky_steak_lore4", stinkySteakCooldown)
            ),
            15,
            Arrays.asList(
                new PotionEffect(PotionEffectType.SPEED, 280, 1), // 14秒 * 20 = 280 ticks
                new PotionEffect(PotionEffectType.GLOWING, 200, 0) // 10秒 * 20 = 200 ticks
            )
        ));
        
        // 传送珍珠 - 双方可用
        int teleportPearlCooldown = 20;
        randomItems.add(new RandomItemConfig(
            "teleport-pearl",
            Material.ENDER_PEARL,
            plugin.getLanguageManager().getMessage("item_names.teleport_pearl"),
            Arrays.asList(
                plugin.getLanguageManager().getMessage("item_names.teleport_pearl_lore1"),
                plugin.getLanguageManager().getMessage("item_names.teleport_pearl_lore2", teleportPearlCooldown)
            ),
            20,
            null // 传送珍珠没有药水效果，只有传送功能
        ));
        
        // 灵魂探测器 - 鬼专属道具
        int soulDetectorCooldown = 35;
        randomItems.add(new RandomItemConfig(
            "soul-detector",
            Material.COMPASS,
            plugin.getLanguageManager().getMessage("item_names.soul_detector"),
            Arrays.asList(
                plugin.getLanguageManager().getMessage("item_names.soul_detector_lore1"),
                plugin.getLanguageManager().getMessage("item_names.soul_detector_lore2", soulDetectorCooldown),
                plugin.getLanguageManager().getMessage("item_names.soul_detector_lore3")
            ),
            12,
            Collections.singletonList(new PotionEffect(PotionEffectType.GLOWING, 500, 0)), // 25秒 * 20 = 500 ticks
            false, true // ghostOnly
        ));
        
        // 一次机会 - 人类专属道具
        int secondChanceHumanSpeed = 10;
        int secondChanceHumanGlow = 10;
        int secondChanceGhostSlow = 7;
        int secondChanceCooldown = 180;
        randomItems.add(new RandomItemConfig(
            "second-chance",
            Material.TOTEM_OF_UNDYING,
            plugin.getLanguageManager().getMessage("item_names.one_chance_name"),
            Arrays.asList(
                plugin.getLanguageManager().getMessage("item_names.one_chance_lore1"),
                plugin.getLanguageManager().getMessage("item_names.one_chance_lore2"),
                plugin.getLanguageManager().getMessage("item_names.one_chance_lore3"),
                plugin.getLanguageManager().getMessage("item_names.one_chance_lore4"),
                "",
                plugin.getLanguageManager().getMessage("item_names.second_chance_lore1"),
                plugin.getLanguageManager().getMessage("item_names.second_chance_lore2", secondChanceHumanSpeed),
                plugin.getLanguageManager().getMessage("item_names.second_chance_lore3", secondChanceHumanGlow),
                plugin.getLanguageManager().getMessage("item_names.second_chance_lore4", secondChanceGhostSlow),
                "",
                plugin.getLanguageManager().getMessage("item_names.second_chance_lore5", secondChanceCooldown)
            ),
            10,
            null, // 被动触发，没有直接药水效果
            true, false // humanOnly
        ));
        
        plugin.getLogger().info(plugin.getLanguageManager().getMessage("log.item_spawn_loaded", randomItems.size()));
    }
    
    /**
     * 开始道具刷新任务
     */
    public void startSpawning() {
        if (!plugin.getConfigManager().isItemSpawnEnabled()) {
            return;
        }
        
        if (spawnTask != null && !spawnTask.isCancelled()) {
            spawnTask.cancel();
        }
        
        spawnTask = new CancellableTask(plugin) {
            @Override
            public boolean execute() {
                if (!plugin.getGameManager().isGameRunning()) {
                    return false;
                }
                
                refreshItems();
                return true;
            }
        };
        spawnTask.startTimer(plugin.getConfigManager().getItemSpawnInterval() * 20L);
        
        plugin.getLogger().info("道具刷新系统已启动，间隔: " + 
            plugin.getConfigManager().getItemSpawnInterval() + "秒，最大每次刷新: " + 
            plugin.getConfigManager().getItemSpawnMaxPerRefresh() + "，最大每人: " + 
            plugin.getConfigManager().getItemSpawnMaxPerPlayer());
    }
    
    /**
     * 刷新道具
     */
    private void refreshItems() {
        if (!plugin.getGameManager().isGameRunning()) {
            return;
        }
        
        // 获取所有游戏中的玩家
        List<UUID> allPlayers = plugin.getPlayerManager().getAllPlayers();
        if (allPlayers.isEmpty()) {
            plugin.getLogger().info("道具刷新: 没有游戏中的玩家");
            return;
        }
        
        plugin.getLogger().info("道具刷新: 当前游戏中有 " + allPlayers.size() + " 名玩家");
        
        int maxPerRefresh = plugin.getConfigManager().getItemSpawnMaxPerRefresh();
        int actualSpawnCount = Math.min(maxPerRefresh, allPlayers.size());
        
        plugin.getLogger().info("道具刷新: 最大每次刷新=" + maxPerRefresh + ", 实际刷新数量=" + actualSpawnCount);
        
        // 重置玩家道具计数
        playerItemCounts.clear();
        
        // 广播刷新消息
        String refreshMessage = plugin.getLanguageManager().getMessage("item-spawn.refresh");
        Bukkit.broadcastMessage(refreshMessage);
        
        // 随机选择玩家发放道具
        Collections.shuffle(allPlayers);
        int spawned = 0;
        
        for (UUID playerId : allPlayers) {
            if (spawned >= actualSpawnCount) {
                break;
            }
            
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                giveRandomItem(player);
                spawned++;
            }
        }
        
        plugin.getLogger().info("道具刷新完成，共发放给 " + spawned + " 名玩家 (臭牛排/传送珍珠/灵魂探测器)");
        
        // 发送游戏剩余时间提示
        if (plugin.getGameManager().isGameRunning()) {
            int remainingTime = plugin.getGameManager().getRemainingGameTime();
            if (remainingTime > 0) {
                int minutes = remainingTime / 60;
                int seconds = remainingTime % 60;
                String timeMessage = ChatColor.YELLOW + "距离游戏结束还有 " + 
                    (minutes > 0 ? minutes + "分钟" : "") + 
                    (seconds > 0 || minutes == 0 ? seconds + "秒" : "");
                Bukkit.broadcastMessage(timeMessage);
            }
        }
    }
    
    /**
     * 给予玩家随机道具
     */
    private void giveRandomItem(Player player) {
        // 检查玩家是否已达到最大数量限制
        int currentCount = playerItemCounts.getOrDefault(player.getUniqueId(), 0);
        int maxPerPlayer = plugin.getConfigManager().getItemSpawnMaxPerPlayer();
        
        if (currentCount >= maxPerPlayer) {
            return;
        }
        
        // 检查玩家是否已达到最大道具种类数量
        if (plugin.getPlayerManager().hasReachedMaxItemTypes(player)) {
            int currentTypes = plugin.getPlayerManager().getPlayerItemTypesCount(player);
            int maxTypes = plugin.getConfigManager().getMaxItemTypesPerPlayer();
            plugin.getLanguageManager().sendMessage(player, "item_spawn.max_types_reached", currentTypes, maxTypes);
            return;
        }
        
        // 获取玩家角色
        boolean isHuman = plugin.getPlayerManager().isHuman(player.getUniqueId());
        boolean isGhost = plugin.getPlayerManager().isGhost(player.getUniqueId());
        
        // 随机选择道具（考虑阵营限制）
        RandomItemConfig itemConfig = null;
        int attempts = 0;
        int maxAttempts = 10;
        
        while (attempts < maxAttempts) {
            RandomItemConfig candidate = getRandomItem();
            if (candidate == null) {
                break;
            }
            
            // 检查阵营限制
            boolean valid = true;
            if (candidate.humanOnly && !isHuman) {
                valid = false;
            }
            if (candidate.ghostOnly && !isGhost) {
                valid = false;
            }
            
            if (valid) {
                itemConfig = candidate;
                plugin.getLogger().info("为玩家 " + player.getName() + " 选择道具: " + candidate.id + 
                    " (阵营: " + (isHuman ? "人类" : isGhost ? "鬼" : "未知") + ")");
                break;
            }
            
            attempts++;
            if (attempts >= maxAttempts) {
                plugin.getLogger().warning("为玩家 " + player.getName() + " 选择道具失败，达到最大尝试次数");
            }
        }
        
        if (itemConfig == null) {
            plugin.getLogger().warning("无法为玩家 " + player.getName() + " 选择道具，返回");
            return;
        }
        
        // 创建道具
        ItemStack item = createRandomItem(itemConfig);
        
        // 给予玩家
        player.getInventory().addItem(item);
        
        // 更新计数
        playerItemCounts.put(player.getUniqueId(), currentCount + 1);
        
        // 发送消息
        plugin.getLanguageManager().sendMessage(player, "item-spawn.received", 
            ChatColor.stripColor(itemConfig.name));
        
        plugin.getLogger().info("玩家 " + player.getName() + " 获得了随机道具: " + itemConfig.id + " (阵营: " + 
            (isHuman ? "人类" : isGhost ? "鬼" : "未知") + ")");
    }
    
    /**
     * 随机选择道具（根据权重）
     */
    private RandomItemConfig getRandomItem() {
        if (randomItems.isEmpty()) {
            return null;
        }
        
        // 计算总权重
        int totalWeight = randomItems.stream().mapToInt(item -> item.weight).sum();
        int randomValue = random.nextInt(totalWeight);
        
        // 根据权重选择
        int currentWeight = 0;
        for (RandomItemConfig item : randomItems) {
            currentWeight += item.weight;
            if (randomValue < currentWeight) {
                return item;
            }
        }
        
        return randomItems.get(0);
    }
    
    /**
     * 创建随机道具
     */
    private ItemStack createRandomItem(RandomItemConfig config) {
        ItemStack item = new ItemStack(config.material);
        
        if (item.getItemMeta() instanceof PotionMeta) {
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.name));
            meta.setLore(config.lore.stream()
                .map(lore -> ChatColor.translateAlternateColorCodes('&', lore))
                .toList());
            
            // 添加药水效果
            for (PotionEffect effect : config.effects) {
                meta.addCustomEffect(effect, true);
            }
            
            item.setItemMeta(meta);
        } else {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.name));
                meta.setLore(config.lore.stream()
                    .map(lore -> ChatColor.translateAlternateColorCodes('&', lore))
                    .toList());
                item.setItemMeta(meta);
            }
        }
        
        return item;
    }
    
    /**
     * 创建传送珍珠（公开方法，供ItemManager调用）
     */
    public ItemStack createTeleportPearl() {
        // 查找传送珍珠配置
        for (RandomItemConfig config : randomItems) {
            if (config.id.equals("teleport-pearl")) {
                return createRandomItem(config);
            }
        }
        
        // 如果找不到配置，创建默认的传送珍珠
        ItemStack teleportPearl = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = teleportPearl.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "传送珍珠");
            meta.setLore(Arrays.asList(
                ChatColor.GRAY + "右键投掷传送",
                ChatColor.GRAY + "冷却时间: 10秒"
            ));
            teleportPearl.setItemMeta(meta);
        }
        return teleportPearl;
    }
    
    /**
     * 停止道具刷新
     */
    public void stopSpawning() {
        if (spawnTask != null && !spawnTask.isCancelled()) {
            spawnTask.cancel();
            spawnTask = null;
        }
        
        playerItemCounts.clear();
        plugin.getLogger().info("道具刷新系统已停止");
    }
    
    /**
     * 清理玩家数据
     */
    public void cleanupPlayer(UUID playerId) {
        playerItemCounts.remove(playerId);
    }
    
    /**
     * 清理所有数据
     */
    public void cleanup() {
        playerItemCounts.clear();
        if (spawnTask != null && !spawnTask.isCancelled()) {
            spawnTask.cancel();
        }
        spawnTask = null;
    }
    
    /**
     * 随机道具配置类
     */
    private static class RandomItemConfig {
        final String id;
        final Material material;
        final String name;
        final List<String> lore;
        final int weight;
        final List<PotionEffect> effects;
        final boolean humanOnly;
        final boolean ghostOnly;
        
        RandomItemConfig(String id, Material material, String name, List<String> lore, 
                        int weight, List<PotionEffect> effects) {
            this(id, material, name, lore, weight, effects, false, false);
        }
        
        RandomItemConfig(String id, Material material, String name, List<String> lore, 
                        int weight, List<PotionEffect> effects, boolean humanOnly, boolean ghostOnly) {
            this.id = id;
            this.material = material;
            this.name = name;
            this.lore = lore;
            this.weight = weight;
            this.effects = effects;
            this.humanOnly = humanOnly;
            this.ghostOnly = ghostOnly;
        }
    }
}