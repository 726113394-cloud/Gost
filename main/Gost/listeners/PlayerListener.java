package io.Sriptirc_wp_1258.gost.listeners;

import io.Sriptirc_wp_1258.gost.Gost;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 玩家监听器（含 v2.3.1 背包管理系统）
 *
 * 背包规则：
 * - 对局期间最多 9 个道具（只放物品栏 0-8 格）
 * - 道具不可放入背包（强制移回物品栏）
 * - 道具不堆叠
 * - 不允许两个相同道具（人鬼通用道具除外）
 * - 「收割者」/「神之救赎」强制放第一格
 */
public class PlayerListener implements Listener {

    private final Gost plugin;

    public PlayerListener(Gost plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // v2.3.2_B：掉线即退出对局——无条件执行退出流程，立即还原原始背包。
        // 还原动作在 PlayerQuitEvent 中同步执行（此时玩家数据尚未落盘），
        // 因此即使玩家已经离线，背包也能被正确还原。静默：不向已断线玩家发消息。
        plugin.getPlayerManager().leaveGame(player, true);

        // v2.3.2_B：掉线即退出队列——自动退出队列并退还入场费（静默）
        if (plugin.getGameManager().isPlayerInQueue(playerId)) {
            plugin.getGameManager().leaveQueue(player, true);
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
    public void onFoodLevelChange(org.bukkit.event.entity.FoodLevelChangeEvent event) {
        // v2.3.2：猎魔人阶段所有玩家不会自然掉饥饿值（饱食度锁定满）
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (plugin.getDivineGuardianManager().isInDemonHunterPhase()) {
            event.setCancelled(true);
            player.setFoodLevel(20);
            player.setSaturation(20);
        }
    }

    // ==================== 背包管理系统（v2.3.1）====================

    /** 判断是否为游戏自定义道具（有显示名且材质非普通方块） */
    private boolean isGameItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;
        return item.getItemMeta() != null && item.getItemMeta().hasDisplayName();
    }

    /** 人鬼通用道具（允许重复持有） */
    private boolean isUniversalItem(String name) {
        return name.contains("凝冰球") || name.contains("传送珍珠") ||
               name.contains("漂浮药水") || name.contains("臭牛排") ||
               name.contains("冲刺矛");
    }

    /** 收割者 / 神之救赎 → 强制第一格 */
    private boolean isFirstSlotItem(String name) {
        return name.contains("收割者") || name.contains("神之救赎");
    }

    /** 强制将道具放入第一格（slot 0） */
    private void forceFirstSlot(Player player, ItemStack item, String name) {
        ItemStack first = player.getInventory().getItem(0);
        if (first == null || first.getType() == Material.AIR) {
            player.getInventory().setItem(0, item.clone());
        } else {
            int emptySlot = player.getInventory().firstEmpty();
            if (emptySlot != -1 && emptySlot != 0) {
                player.getInventory().setItem(emptySlot, first.clone());
            }
            // 空位满则直接替换第一格
            player.getInventory().setItem(0, item.clone());
            player.sendMessage(ChatColor.GOLD + "「" + ChatColor.stripColor(name) + "」已强制放置到第一格！");
        }
    }

    /**
     * 检查并阻止重复专属道具
     * @return true 表示检测到重复，应取消操作
     */
    private boolean hasDuplicateExclusive(Player player, String name, ItemStack ignore) {
        if (isUniversalItem(name)) return false;
        for (ItemStack inv : player.getInventory().getContents()) {
            if (inv == null || inv.getType() == Material.AIR) continue;
            if (ignore != null && inv == ignore) continue;
            if (inv.hasItemMeta() && inv.getItemMeta().hasDisplayName() &&
                inv.getItemMeta().getDisplayName().equals(name)) {
                player.sendMessage(ChatColor.RED + "你不允许携带两个相同道具！");
                return true;
            }
        }
        return false;
    }

    /**
     * 将道具强制移回物品栏（0-8），失败则退回原位置/取消
     * @return true 表示已放回物品栏
     */
    private boolean forceToHotbar(Player player, ItemStack item, ItemStack[] snapshotContents) {
        for (int i = 0; i < 9; i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s == null || s.getType() == Material.AIR) {
                player.getInventory().setItem(i, item.clone());
                return true;
            }
        }
        player.sendMessage(ChatColor.RED + "物品栏已满！道具无法放入背包！");
        return false;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (!plugin.getPlayerManager().getAllPlayers().contains(player.getUniqueId())) return;

        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        // 1) 禁止把道具放进背包（storage 区域 slot>=9 的玩家背包）
        boolean isPlayerStorage = event.getClickedInventory() != null
            && event.getClickedInventory().getType() == InventoryType.PLAYER
            && event.getSlotType() == InventoryType.SlotType.CONTAINER
            && event.getSlot() >= 9;

        // 数字键换位 / 点击交换也会把道具送进背包
        boolean isNumberKeySwap = event.getClick() == ClickType.NUMBER_KEY
            && event.getHotbarButton() >= 0 && event.getHotbarButton() < 9;

        // 2) 不堆叠：若两件都是游戏道具且可合并，取消
        if (current != null && cursor != null && isGameItem(current) && isGameItem(cursor)) {
            if (current.isSimilar(cursor) && current.getAmount() + cursor.getAmount() > 1) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "道具不允许堆叠！");
                return;
            }
        }

        // 3) 重复专属道具检查（拿起/交换时）
        ItemStack checkItem = isGameItem(cursor) ? cursor : current;
        if (checkItem != null && isGameItem(checkItem)) {
            String name = checkItem.getItemMeta().getDisplayName();
            if (hasDuplicateExclusive(player, name, checkItem)) {
                event.setCancelled(true);
                return;
            }

            // 4) 收割者/神之救赎强制第一格
            if (isFirstSlotItem(name)) {
                int slot = event.getSlot();
                boolean clickedHotbar = event.getClickedInventory() != null
                    && event.getClickedInventory().getType() == InventoryType.PLAYER
                    && event.getSlotType() == InventoryType.SlotType.QUICKBAR;
                if (!clickedHotbar || slot != 0) {
                    event.setCancelled(true);
                    // 从来源移除后强制放入第一格
                    forceFirstSlot(player, checkItem, name);
                    // 若是点击拿起，清掉 cursor 上的那份（避免复制）
                    if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
                        event.getView().setCursor(null);
                    }
                    return;
                }
            }
        }

        // 5) 背包区域不允许放道具，强制移回物品栏
        if (isPlayerStorage) {
            ItemStack moved = isGameItem(cursor) ? cursor : current;
            if (isGameItem(moved)) {
                event.setCancelled(true);
                ItemStack copy = moved.clone();
                copy.setAmount(1);
                // 从原位置清掉
                if (event.getClickedInventory() != null) {
                    event.getClickedInventory().setItem(event.getSlot(), null);
                }
                event.getView().setCursor(null);
                forceToHotbar(player, copy, null);
                return;
            }
        }

        // 6) 数字键把物品栏道具换进背包时，阻止
        if (isNumberKeySwap && isGameItem(current)) {
            // hotbar 按键目标在 0-8，若当前点击的是背包区，会把 hotbar 的东西换进来
            if (isPlayerStorage) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "道具不允许放入背包！");
            }
        }

        // 7) 强制游戏道具数量为 1（不堆叠）
        if (current != null && isGameItem(current) && current.getAmount() > 1) {
            current.setAmount(1);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (!plugin.getPlayerManager().getAllPlayers().contains(player.getUniqueId())) return;

        // 拖拽到背包（slot >= 9）的游戏道具 → 取消
        for (int slot : event.getRawSlots()) {
            // raw slot 35+ 对应玩家背包（容器视图），这里按是否 > 35 热键栏粗判
            // 使用 InventoryType 更准确：rawSlot 在玩家背包容器内
            if (slot >= 36) { // 玩家背包区域（36 起为 storage）
                ItemStack dragged = event.getOldCursor();
                if (isGameItem(dragged)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "道具不允许放入背包！已阻止拖拽。");
                    return;
                }
            }
        }

        ItemStack newItems = event.getCursor();
        if (isGameItem(newItems) && newItems.getAmount() > 1) {
            // 拖拽不允许堆叠
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "道具不允许堆叠！");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (!plugin.getPlayerManager().getAllPlayers().contains(player.getUniqueId())) return;

        ItemStack item = event.getItem().getItemStack();
        if (!isGameItem(item)) return;

        // 不堆叠
        if (item.getAmount() > 1) {
            item.setAmount(1);
            event.getItem().setItemStack(item);
        }

        String name = item.getItemMeta().getDisplayName();

        // 重复专属道具：取消拾取
        if (hasDuplicateExclusive(player, name, null)) {
            event.setCancelled(true);
            return;
        }

        // 热键栏满 → 不允许拾取进背包
        boolean hotbarFull = true;
        for (int i = 0; i < 9; i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s == null || s.getType() == Material.AIR) {
                hotbarFull = false;
                break;
            }
        }
        if (hotbarFull) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "物品栏已满！无法拾取 " + ChatColor.stripColor(name));
        }
    }
}