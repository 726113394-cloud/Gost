package io.Sriptirc_wp_1258.gost.commands;

import io.Sriptirc_wp_1258.gost.Gost;
import io.Sriptirc_wp_1258.gost.managers.AreaManager;
import io.Sriptirc_wp_1258.gost.managers.SelectionManager;
import io.Sriptirc_wp_1258.gost.managers.ServerCompat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GostAdminCommand implements CommandExecutor, TabCompleter {
    
    private final Gost plugin;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public GostAdminCommand(Gost plugin) {
        this.plugin = plugin;
    }
    
    /** 按玩家自己的语言发消息；控制台用服务器默认语言 */
    private void send(CommandSender sender, String key, Object... args) {
        Player player = sender instanceof Player ? (Player) sender : null;
        sender.sendMessage(plugin.getLanguageManager().getMessage(player, key, args));
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("gost.admin")) {
            send(sender, "error.no-permission");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "start":
                return handleStart(sender, args);
            case "stop":
                return handleStop(sender);
            case "pos1":
                return handlePos1(sender);
            case "pos2":
                return handlePos2(sender);
            case "save":
                return handleSave(sender, args);
            case "list":
                return handleList(sender);
            case "load":
                return handleLoad(sender, args);
            case "delete":
                return handleDelete(sender, args);
            case "info":
                return handleInfo(sender, args);
            case "tool":
                return handleTool(sender);
            case "clear":
                return handleClear(sender);
            case "reload":
                return handleReload(sender);
            case "status":
                return handleStatus(sender);
            case "selftest":
                return handleSelfTest(sender);

            case "dark":
                return handleDark(sender, args);
            case "heartbeat":
                return handleHeartbeat(sender, args);
            case "economy":
                return handleEconomy(sender, args);
            case "testmode":
                return handleTestMode(sender);
            case "giveitem":
                return handleGiveItem(sender, args);
            case "lang":
            case "language":
                return handleLanguage(sender, args);
            case "divine":
                return handleDivineSub(sender, args);
            case "particle":
                return handleParticleSub(sender, args);
            case "help":
                sendHelp(sender);
                return true;
            default:
                send(sender, "admin.usage.unknown");
                return true;
        }
    }
    
    private boolean handleStart(CommandSender sender, String[] args) {
        if (args.length < 2) {
            send(sender, "admin.start.usage");
            send(sender, "admin.start.available", String.join(", ", plugin.getAreaManager().getAreaNames()));
            return true;
        }
        
        String areaName = args[1];
        
        // 选择区域
        if (!plugin.getAreaManager().selectArea(areaName)) {
            send(sender, "admin.area.not-exist", areaName);
            return true;
        }
        
        // 开始游戏
        if (!plugin.getGameManager().startGame()) {
            send(sender, "admin.start.failed");
        } else {
            send(sender, "admin.start.success", areaName);
        }
        return true;
    }
    
    private boolean handleStop(CommandSender sender) {
        if (!plugin.getGameManager().isAnyGameRunning()) {
            send(sender, "admin.stop.none");
            return true;
        }
        
        // 强制停止游戏
        plugin.getGameManager().forceStopGame();
        send(sender, "admin.stop.success");
        return true;
    }
    
    private boolean handlePos1(CommandSender sender) {
        if (!(sender instanceof Player)) {
            send(sender, "error.player-only");
            return true;
        }
        
        Player player = (Player) sender;
        send(player, "admin.pos1.hint");
        return true;
    }
    
    private boolean handlePos2(CommandSender sender) {
        if (!(sender instanceof Player)) {
            send(sender, "error.player-only");
            return true;
        }
        
        Player player = (Player) sender;
        send(player, "admin.pos2.hint");
        return true;
    }
    
    private boolean handleSave(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            send(sender, "error.player-only");
            return true;
        }
        
        if (args.length < 2) {
            send(sender, "admin.save.usage");
            return true;
        }
        
        Player player = (Player) sender;
        String areaName = args[1];
        
        // 检查选区是否完整
        SelectionManager.PlayerSelection selection = plugin.getSelectionManager().getSelection(player);
        if (selection == null || !selection.isComplete()) {
            send(player, "admin.save.no-selection");
            send(player, "admin.save.howto");
            return true;
        }
        
        // 检查是否在同一世界
        if (!selection.isValid()) {
            send(player, "admin.save.different-world");
            return true;
        }
        
        // 保存区域
        if (plugin.getAreaManager().saveArea(areaName, selection.getPos1(), selection.getPos2())) {
            send(player, "admin.save.success", areaName);
        } else {
            send(player, "admin.save.failed");
        }
        
        return true;
    }
    
    private boolean handleList(CommandSender sender) {
        List<AreaManager.GameArea> areas = plugin.getAreaManager().getAllAreas();
        
        if (areas.isEmpty()) {
            send(sender, "admin.list.empty");
            return true;
        }
        
        send(sender, "admin.list.header");
        for (AreaManager.GameArea area : areas) {
            int[] dims = area.getDimensions();
            String selected = area.getName().equals(plugin.getAreaManager().getSelectedAreaName()) ? "✓" : " ";
            String enabled = plugin.getAreaManager().isAreaEnabled(area.getName()) ? "✔" : "✘";
            send(sender, "admin.list.entry", selected, enabled, area.getName(), dims[0], dims[1], dims[2], area.getUsageCount());
        }
        send(sender, "admin.separator");
        return true;
    }
    
    private boolean handleLoad(CommandSender sender, String[] args) {
        if (args.length < 2) {
            send(sender, "admin.load.usage");
            send(sender, "admin.load.example-header");
            send(sender, "admin.load.example-select");
            send(sender, "admin.load.example-enable");
            send(sender, "admin.load.example-disable");
            return true;
        }
        
        String areaName = args[1];
        
        // 检查区域是否存在
        if (plugin.getAreaManager().getArea(areaName) == null) {
            send(sender, "admin.area.not-exist", areaName);
            return true;
        }
        
        // 如果有第三个参数，处理启用/禁用
        if (args.length >= 3) {
            String action = args[2].toLowerCase();
            switch (action) {
                case "enable":
                    if (plugin.getAreaManager().enableArea(areaName)) {
                        send(sender, "admin.load.enabled", areaName);
                    } else if (plugin.getAreaManager().isAreaEnabled(areaName)) {
                        // 已启用：幂等提示，不要和「不存在」混成一句
                        send(sender, "admin.load.already-enabled", areaName);
                    } else {
                        send(sender, "admin.area.not-exist", areaName);
                    }
                    break;
                    
                case "disable":
                    if (plugin.getAreaManager().disableArea(areaName)) {
                        send(sender, "admin.load.disabled", areaName);
                    } else if (!plugin.getAreaManager().isAreaEnabled(areaName)) {
                        send(sender, "admin.load.not-enabled", areaName);
                    } else {
                        send(sender, "admin.area.not-exist", areaName);
                    }
                    break;
                    
                default:
                    send(sender, "admin.load.unknown-action");
                    return true;
            }
        } else {
            // 没有第三个参数，选择区域
            if (plugin.getAreaManager().selectArea(areaName)) {
                send(sender, "admin.load.selected", areaName);
            } else {
                send(sender, "admin.load.select-failed");
            }
        }
        
        return true;
    }
    
    private boolean handleDelete(CommandSender sender, String[] args) {
        if (args.length < 2) {
            send(sender, "admin.delete.usage");
            return true;
        }
        
        String areaName = args[1];
        
        if (plugin.getAreaManager().deleteArea(areaName)) {
            send(sender, "admin.delete.success", areaName);
        } else {
            send(sender, "admin.area.not-exist", areaName);
        }
        
        return true;
    }
    
    private boolean handleInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            send(sender, "admin.info.usage");
            return true;
        }
        
        String areaName = args[1];
        AreaManager.GameArea area = plugin.getAreaManager().getArea(areaName);
        
        if (area == null) {
            send(sender, "admin.area.not-exist", areaName);
            return true;
        }
        
        int[] dims = area.getDimensions();
        int volume = area.getVolume();
        
        send(sender, "admin.info.header");
        send(sender, "admin.info.name", area.getName());
        send(sender, "admin.info.world", area.getPos1().getWorld().getName());
        send(sender, "admin.info.pos1", formatLocation(area.getPos1()));
        send(sender, "admin.info.pos2", formatLocation(area.getPos2()));
        send(sender, "admin.info.size", dims[0], dims[1], dims[2]);
        send(sender, "admin.info.volume", volume);
        send(sender, "admin.info.created", dateFormat.format(area.getCreatedDate()));
        send(sender, "admin.info.last-used", dateFormat.format(area.getLastUsedDate()));
        send(sender, "admin.info.usage-count", area.getUsageCount());
        send(sender, "admin.separator");
        
        return true;
    }
    
    private boolean handleTool(CommandSender sender) {
        if (!(sender instanceof Player)) {
            send(sender, "error.player-only");
            return true;
        }
        
        Player player = (Player) sender;
        plugin.getSelectionManager().giveSelectionTool(player);
        return true;
    }
    
    private boolean handleClear(CommandSender sender) {
        if (!(sender instanceof Player)) {
            send(sender, "error.player-only");
            return true;
        }
        
        Player player = (Player) sender;
        if (plugin.getSelectionManager().clearSelection(player)) {
            send(player, "admin.clear.success");
        } else {
            send(player, "admin.clear.nothing");
        }
        return true;
    }
    

    /** 切换/查看语言：/gostadmin lang [zh_CN|en_US|ja_JP|es_ES|fr_FR|de_DE] */
    private boolean handleLanguage(CommandSender sender, String[] args) {
        if (args.length < 2) {
            send(sender, "admin.lang.current", plugin.getLanguageManager().getCurrentLanguage());
            send(sender, "admin.lang.available", plugin.getLanguageManager().listSupported());
            send(sender, "admin.lang.usage");
            send(sender, "admin.lang.file-hint");
            return true;
        }
        String loc = args[1];
        String[] supported = io.Sriptirc_wp_1258.gost.managers.LanguageManager.SUPPORTED;
        String matched = null;
        for (String s : supported) {
            if (s.equalsIgnoreCase(loc) || s.replace("_", "").equalsIgnoreCase(loc.replace("_", ""))) {
                matched = s;
                break;
            }
        }
        if (matched == null) {
            send(sender, "admin.lang.unsupported", loc);
            send(sender, "admin.lang.available-short", plugin.getLanguageManager().listSupported());
            return true;
        }
        plugin.getConfigManager().setDefaultLanguage(matched);
        plugin.getLanguageManager().reload();
        send(sender, "admin.lang.changed", matched);
        send(sender, "admin.lang.edit-hint", matched);
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        plugin.getConfigManager().reloadConfig();
        plugin.getAreaManager().reload();
        send(sender, "admin.reload.success");
        return true;
    }
    
    private boolean handleStatus(CommandSender sender) {
        send(sender, "admin.status.header");
        
        // 游戏状态
        String gameState;
        switch (plugin.getGameManager().getGameState()) {
            case WAITING:
                gameState = plugin.getLanguageManager().getMessage("admin.status.state-waiting");
                break;
            case STARTING:
                gameState = plugin.getLanguageManager().getMessage("admin.status.state-starting");
                break;
            case RUNNING:
                gameState = plugin.getLanguageManager().getMessage("admin.status.state-running");
                break;
            case ENDING:
                gameState = plugin.getLanguageManager().getMessage("admin.status.state-ending");
                break;
            default:
                gameState = plugin.getLanguageManager().getMessage("admin.status.state-idle");
                break;
        }
        send(sender, "admin.status.state", gameState);
        
        // 玩家统计
        int waitingPlayers = plugin.getGameManager().getWaitingPlayersCount();
        int gamePlayers = plugin.getPlayerManager().getAllPlayers().size();
        int humanPlayers = plugin.getPlayerManager().getHumanPlayers().size();
        int ghostPlayers = plugin.getPlayerManager().getGhostPlayers().size();
        
        send(sender, "admin.status.waiting", waitingPlayers);
        send(sender, "admin.status.players", gamePlayers);
        send(sender, "admin.status.humans", humanPlayers);
        send(sender, "admin.status.ghosts", ghostPlayers);
        
        // 区域信息
        AreaManager.GameArea selectedArea = plugin.getAreaManager().getSelectedArea();
        if (selectedArea != null) {
            send(sender, "admin.status.area", selectedArea.getName());
            int[] dims = selectedArea.getDimensions();
            send(sender, "admin.status.area-size", dims[0], dims[1], dims[2]);
        } else {
            send(sender, "admin.status.area-none");
        }
        
        // 存档区域数量
        int areaCount = plugin.getAreaManager().getAreaNames().size();
        send(sender, "admin.status.area-count", areaCount);
        
        // 经济信息
        if (plugin.getEconomyManager().isEconomyEnabled()) {
            double prizePool = plugin.getEconomyManager().getPrizePool();
            send(sender, "admin.status.prize-pool", prizePool);
        } else {
            send(sender, "admin.status.economy-off");
        }
        
        // 配置信息
        send(sender, "admin.status.min-players", plugin.getConfigManager().getMinPlayers());
        send(sender, "admin.status.max-players", plugin.getConfigManager().getMaxPlayers());
        send(sender, "admin.status.duration", plugin.getConfigManager().getGameDuration());
        
        return true;
    }
    

    
    /**
     * /gostadmin selftest —— 运行期自检。
     *
     * <p>目的是让「必须跑起来才知道」的东西可以单人验证：平台探测、语言加载、
     * 道具能否生成、ActionBar 通道、以及全局/实体两种调度能不能真正执行。
     * 不依赖开一局游戏。</p>
     */
    private boolean handleSelfTest(CommandSender sender) {
        final int[] stats = {0, 0, 0}; // 通过 / 失败 / 警告
        final Player player = sender instanceof Player ? (Player) sender : null;
        final String locale = plugin.getLanguageManager().getCurrentLanguage();

        send(sender, "selftest.header");

        // ---- 平台 ----
        stPass(sender, stats, lbl(player, "selftest.platform", ServerCompat.describe()));
        boolean folia = ServerCompat.isFolia();
        stPass(sender, stats, lbl(player, "selftest.folia",
            lbl(player, folia ? "selftest.yes" : "selftest.no")));

        // ---- 语言 ----
        int msgCount = plugin.getLanguageManager().getMessageCount(locale);
        if (msgCount > 0) {
            stPass(sender, stats, lbl(player, "selftest.lang", locale, String.valueOf(msgCount)));
        } else {
            stFail(sender, stats, lbl(player, "selftest.lang", locale, "0"), "no messages loaded");
        }
        int diff = plugin.getLanguageManager().getMaxKeySetDiff("zh_CN");
        String localeDetail = plugin.getLanguageManager().describeLocaleCounts();
        if (diff == 0) {
            stPass(sender, stats, lbl(player, "selftest.lang-missing", "0"));
        } else if (diff < 0) {
            stWarn(sender, stats, lbl(player, "selftest.lang-missing", "?"), "zh_CN not loaded");
        } else {
            stFail(sender, stats, lbl(player, "selftest.lang-missing", String.valueOf(diff)), localeDetail);
        }

        // ---- Vault 经济 ----
        if (plugin.getEconomyManager().isEconomyEnabled()) {
            stPass(sender, stats, lbl(player, "selftest.vault"));
        } else {
            stWarn(sender, stats, lbl(player, "selftest.vault"), lbl(player, "selftest.disabled"));
        }

        // ---- 区域 ----
        java.util.List<String> areaNames = plugin.getAreaManager().getAreaNames();
        int enabledAreas = 0;
        for (String n : areaNames) {
            if (plugin.getAreaManager().isAreaEnabled(n)) enabledAreas++;
        }
        if (areaNames.isEmpty()) {
            stFail(sender, stats, lbl(player, "selftest.areas", "0", "0"), "no saved area");
        } else if (enabledAreas == 0) {
            stFail(sender, stats, lbl(player, "selftest.areas", String.valueOf(areaNames.size()), "0"),
                "no enabled area");
        } else {
            stPass(sender, stats, lbl(player, "selftest.areas",
                String.valueOf(areaNames.size()), String.valueOf(enabledAreas)));
        }

        String selected = plugin.getAreaManager().getSelectedAreaName();
        if (selected != null && !selected.isEmpty()) {
            stPass(sender, stats, lbl(player, "selftest.area", selected));
        } else {
            stWarn(sender, stats, lbl(player, "selftest.area-none"), lbl(player, "selftest.disabled"));
        }

        // ---- 配置版本 ----
        int cfgVersion = plugin.getConfig().getInt("ScriptIrc-config-version", -1);
        int expected = plugin.getConfigManager().getCurrentConfigVersion();
        if (cfgVersion == expected) {
            stPass(sender, stats, lbl(player, "selftest.config-version",
                String.valueOf(cfgVersion), String.valueOf(expected)));
        } else {
            stFail(sender, stats, lbl(player, "selftest.config-version",
                String.valueOf(cfgVersion), String.valueOf(expected)), "run /gostadmin reload");
        }

        // ---- 道具工厂 ----
        java.util.List<org.bukkit.inventory.ItemStack> made = new java.util.ArrayList<>();
        made.add(plugin.getItemManager().getAdrenaline());
        made.add(plugin.getItemManager().getFrenzyPotion());
        made.add(plugin.getItemManager().getIceBall());
        made.add(plugin.getItemManager().getSoulControl());
        made.add(plugin.getItemManager().getSecondChance());
        made.add(plugin.getItemManager().getLevitationPotion());
        made.add(plugin.getItemManager().getSpearRush());
        made.add(plugin.getItemSpawnManager().createTeleportPearl());
        made.add(plugin.getItemSpawnManager().createStinkySteak());
        made.add(plugin.getItemSpawnManager().createSoulDetector());
        int madeOk = 0;
        for (org.bukkit.inventory.ItemStack it : made) {
            if (it != null && it.getType() != org.bukkit.Material.AIR) madeOk++;
        }
        String itemText = lbl(player, "selftest.items",
            String.valueOf(madeOk), String.valueOf(made.size()));
        if (madeOk == made.size()) {
            stPass(sender, stats, itemText);
        } else {
            stFail(sender, stats, itemText, "item factory returned empty");
        }

        // ---- 更新检查开关 ----
        boolean updateCheck = plugin.getConfig().getBoolean("update-check.enabled", true);
        stPass(sender, stats, lbl(player, "selftest.update-check",
            lbl(player, updateCheck ? "selftest.enabled" : "selftest.disabled")));

        // ---- ActionBar 通道（只有玩家能测）----
        if (player != null) {
            try {
                io.Sriptirc_wp_1258.gost.managers.ActionBarManager.sendNow(
                    player, lbl(player, "selftest.actionbar") + " ✓");
                String strategy = io.Sriptirc_wp_1258.gost.managers.ActionBarManager.activeStrategy();
                String text = lbl(player, "selftest.actionbar") + " [" + strategy + "]";
                if ("chat-fallback".equals(strategy)) {
                    stWarn(sender, stats, text, "degraded to chat");
                } else {
                    stPass(sender, stats, text);
                }
            } catch (Throwable t) {
                stFail(sender, stats, lbl(player, "selftest.actionbar"), t.getClass().getSimpleName());
            }
        }

        if (player == null) {
            send(sender, "selftest.console-hint");
            finishSelfTest(sender, stats);
            return true;
        }

        // ---- 调度：延迟验证任务是否真的执行 ----
        final boolean[] ranLater = {false};
        ServerCompat.runLater(plugin, () -> ranLater[0] = true, 1L);

        final boolean[] timerRan = {false};
        final ServerCompat.Cancellable[] holder = new ServerCompat.Cancellable[1];
        holder[0] = ServerCompat.runTimerForEntity(player, plugin, () -> {
            timerRan[0] = true;
            if (holder[0] != null) holder[0].cancel();
        }, 0L, 2L);

        ServerCompat.runLater(plugin, () -> {
            if (ranLater[0]) {
                stPass(sender, stats, lbl(player, "selftest.scheduler"));
            } else {
                stFail(sender, stats, lbl(player, "selftest.scheduler"), "task never ran");
            }
            if (timerRan[0]) {
                stPass(sender, stats, lbl(player, "selftest.entity-timer"));
            } else {
                stFail(sender, stats, lbl(player, "selftest.entity-timer"), "timer never fired");
            }
            finishSelfTest(sender, stats);
        }, 30L);

        return true;
    }

    /** 取文案（玩家可为 null，此时用服务器默认语言） */
    private String lbl(Player player, String key, Object... args) {
        return plugin.getLanguageManager().getMessage(player, key, args);
    }

    private void stPass(CommandSender sender, int[] stats, String text) {
        stats[0]++;
        send(sender, "selftest.pass", text);
    }

    private void stFail(CommandSender sender, int[] stats, String text, String detail) {
        stats[1]++;
        send(sender, "selftest.fail", text, detail);
    }

    private void stWarn(CommandSender sender, int[] stats, String text, String detail) {
        stats[2]++;
        send(sender, "selftest.warn", text, detail);
    }

    private void finishSelfTest(CommandSender sender, int[] stats) {
        send(sender, "selftest.footer");
        send(sender, "selftest.summary", stats[0], stats[1], stats[2]);
        send(sender, stats[1] == 0 ? "selftest.ready" : "selftest.blocked");
    }

    private boolean handleDark(CommandSender sender, String[] args) {
        if (args.length < 2) {
            send(sender, "admin.dark.usage");
            send(sender, "admin.dark.help-on");
            send(sender, "admin.dark.help-off");
            send(sender, "admin.dark.help-status");
            return true;
        }
        
        String action = args[1].toLowerCase();
        
        switch (action) {
            case "on":
                plugin.getDarkEffectManager().toggleDarkEffect(true);
                send(sender, "admin.dark.enabled");
                break;
                
            case "off":
                plugin.getDarkEffectManager().toggleDarkEffect(false);
                send(sender, "admin.dark.disabled");
                break;
                
            case "status":
                boolean enabled = plugin.getDarkEffectManager().isDarkEffectEnabled();
                send(sender, "admin.dark.status", plugin.getLanguageManager().getMessage(enabled ? "common.enabled" : "common.disabled"));
                break;
                
            default:
                send(sender, "admin.dark.unknown");
                break;
        }
        
        return true;
    }
    
    private boolean handleHeartbeat(CommandSender sender, String[] args) {
        if (args.length < 2) {
            send(sender, "admin.heartbeat.usage");
            send(sender, "admin.heartbeat.help-on");
            send(sender, "admin.heartbeat.help-off");
            send(sender, "admin.heartbeat.help-status");
            return true;
        }
        
        String action = args[1].toLowerCase();
        
        switch (action) {
            case "on":
                plugin.getHeartbeatManager().toggleHeartbeat(true);
                break;
                
            case "off":
                plugin.getHeartbeatManager().toggleHeartbeat(false);
                break;
                
            case "status":
                boolean enabled = plugin.getConfigManager().isHeartbeatEnabled();
                boolean running = plugin.getHeartbeatManager().isHeartbeatEnabled();
                send(sender, "admin.heartbeat.header");
                send(sender, "admin.heartbeat.config", plugin.getLanguageManager().getMessage(enabled ? "common.enabled" : "common.disabled"));
                send(sender, "admin.heartbeat.running", plugin.getLanguageManager().getMessage(running ? "common.running" : "common.disabled"));
                send(sender, "admin.heartbeat.interval", plugin.getConfigManager().getHeartbeatInterval());
                break;
                
            default:
                send(sender, "admin.heartbeat.unknown");
                break;
        }
        
        return true;
    }
    
    private boolean handleEconomy(CommandSender sender, String[] args) {
        if (args.length < 2) {
            send(sender, "admin.economy.usage");
            send(sender, "admin.economy.help-set-fee");
            send(sender, "admin.economy.help-set-bonus");
            send(sender, "admin.economy.help-status");
            return true;
        }
        
        String action = args[1].toLowerCase();
        
        switch (action) {
            case "set":
                return handleEconomySet(sender, args);
                
            case "status":
                double entryFee = plugin.getConfigManager().getEntryFee();
                double serverBonus = plugin.getConfigManager().getServerBonus();
                
                send(sender, "admin.economy.header");
                send(sender, "admin.economy.entry-fee", entryFee);
                send(sender, "admin.economy.server-bonus", serverBonus);
                send(sender, "admin.economy.formula");
                break;
                
            default:
                send(sender, "admin.economy.unknown");
                break;
        }
        
        return true;
    }
    
    private boolean handleEconomySet(CommandSender sender, String[] args) {
        if (args.length < 4) {
            send(sender, "admin.economy.set-usage");
            send(sender, "admin.economy.set-example-fee");
            send(sender, "admin.economy.set-example-bonus");
            return true;
        }
        
        String setting = args[2].toLowerCase();
        String valueStr = args[3];
        
        try {
            double value = Double.parseDouble(valueStr);
            
            if (value < 0) {
                send(sender, "admin.economy.negative");
                return true;
            }
            
            switch (setting) {
                case "entryfee":
                case "entry-fee":
                case "entry_fee":
                    plugin.getConfigManager().setEntryFee(value);
                    send(sender, "admin.economy.fee-set", value);
                    send(sender, "admin.economy.fee-set-hint");
                    break;
                    
                case "serverbonus":
                case "server-bonus":
                case "server_bonus":
                    plugin.getConfigManager().setServerBonus(value);
                    send(sender, "admin.economy.bonus-set", value);
                    send(sender, "admin.economy.bonus-set-hint");
                    break;
                    
                default:
                    send(sender, "admin.economy.unknown-key");
                    return true;
            }
            
        } catch (NumberFormatException e) {
            send(sender, "admin.economy.invalid-number");
            return true;
        }
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        send(sender, "admin.help.header");
        send(sender, "admin.help.start");
        send(sender, "admin.help.stop");
        send(sender, "admin.help.testmode");
        send(sender, "admin.help.giveitem");
        send(sender, "admin.help.tool");
        send(sender, "admin.help.pos1");
        send(sender, "admin.help.pos2");
        send(sender, "admin.help.save");
        send(sender, "admin.help.list");
        send(sender, "admin.help.load");
        send(sender, "admin.help.delete");
        send(sender, "admin.help.info");
        send(sender, "admin.help.clear");
        send(sender, "admin.help.reload");
        send(sender, "admin.help.lang", plugin.getLanguageManager().listSupported());
        send(sender, "admin.help.status");
        send(sender, "selftest.help");
        send(sender, "admin.help.dark");
        send(sender, "admin.help.heartbeat");
        send(sender, "admin.help.economy");
        send(sender, "admin.help.help");
        send(sender, "admin.separator");
    }
    
    private String formatLocation(org.bukkit.Location location) {
        if (location == null) return plugin.getLanguageManager().getMessage("admin.location.unset");
        return String.format("(%d, %d, %d)", 
            (int) location.getX(), 
            (int) location.getY(), 
            (int) location.getZ()
        );
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String[] subCommands = {"start", "stop", "testmode", "giveitem", "lang", "language", "divine", "particle", "pos1", "pos2", "save", "list", "load", "delete", "info", "tool", "clear", "reload", "status", "selftest", "dark", "heartbeat", "economy", "help"};
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            
            if (subCommand.equals("start") || subCommand.equals("load") || 
                subCommand.equals("delete") || subCommand.equals("info")) {
                // 区域名称自动补全
                for (String areaName : plugin.getAreaManager().getAreaNames()) {
                    if (areaName.toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(areaName);
                    }
                }

            } else if (subCommand.equals("dark")) {
                // dark子命令自动补全
                String[] darkSubCommands = {"on", "off", "status"};
                for (String darkSubCommand : darkSubCommands) {
                    if (darkSubCommand.startsWith(args[1].toLowerCase())) {
                        completions.add(darkSubCommand);
                    }
                }
            } else if (subCommand.equals("heartbeat")) {
                // heartbeat子命令自动补全
                String[] heartbeatSubCommands = {"on", "off", "status"};
                for (String heartbeatSubCommand : heartbeatSubCommands) {
                    if (heartbeatSubCommand.startsWith(args[1].toLowerCase())) {
                        completions.add(heartbeatSubCommand);
                    }
                }
            } else if (subCommand.equals("economy")) {
                // economy子命令自动补全
                String[] economySubCommands = {"set", "status"};
                for (String economySubCommand : economySubCommands) {
                    if (economySubCommand.startsWith(args[1].toLowerCase())) {
                        completions.add(economySubCommand);
                    }
                }
            }
        } else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            
            if (subCommand.equals("load")) {
                // load命令的第三个参数补全：enable/disable
                String[] actions = {"enable", "disable"};
                for (String action : actions) {
                    if (action.startsWith(args[2].toLowerCase())) {
                        completions.add(action);
                    }
                }
            } else if (subCommand.equals("economy") && args.length >= 2 && args[1].equalsIgnoreCase("set")) {
                // economy set命令的第三个参数补全：entryfee, serverbonus
                String[] settings = {"entryfee", "serverbonus"};
                for (String setting : settings) {
                    if (setting.startsWith(args[2].toLowerCase())) {
                        completions.add(setting);
                    }
                }
            }
        }
        
        return completions;
    }
    
    private boolean handleTestMode(CommandSender sender) {
        if (!(sender instanceof Player)) {
            send(sender, "error.player-only");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (plugin.getGameManager().isAnyGameRunning()) {
            send(player, "admin.testmode.in-progress");
            return true;
        }
        
        if (!plugin.getGameManager().isInQueue(player.getUniqueId())) {
            plugin.getGameManager().joinQueue(player);
            send(player, "command.join.success");
        }
        
        boolean started = plugin.getGameManager().startGame(true, true);
        if (started) {
            Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("admin.testmode.started", player.getName()));
            Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("admin.testmode.note-1"));
            Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("admin.testmode.note-2"));
        } else {
            send(player, "admin.testmode.failed");
        }
        
        return true;
    }
    
    private boolean handleGiveItem(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            send(sender, "error.player-only");
            return true;
        }
        
        if (args.length < 2) {
            send(sender, "admin.giveitem.usage");
            send(sender, "admin.giveitem.available", "肾上腺素, 狂暴药水, 凝冰球, 控魂术, 臭牛排, 传送珍珠, 灵魂探测器, 第二次机会, 漂浮药水, 冲刺矛");
            return true;
        }
        
        Player player = (Player) sender;
        String itemName = args[1];
        org.bukkit.inventory.ItemStack item = null;
        
        if (itemName.contains("肾上腺素")) {
            item = plugin.getItemManager().getAdrenaline();
        } else if (itemName.contains("狂暴药水")) {
            item = plugin.getItemManager().getFrenzyPotion();
        } else if (itemName.contains("凝冰球")) {
            item = plugin.getItemManager().getIceBall();
        } else if (itemName.contains("控魂术")) {
            item = plugin.getItemManager().getSoulControl();
        } else if (itemName.contains("臭牛排")) {
            item = plugin.getItemSpawnManager().createStinkySteak();
        } else if (itemName.contains("传送珍珠")) {
            item = plugin.getItemSpawnManager().createTeleportPearl();
        } else if (itemName.contains("灵魂探测器")) {
            item = plugin.getItemSpawnManager().createSoulDetector();
        } else if (itemName.contains("第二次机会")) {
            item = plugin.getItemManager().getSecondChance();
        } else if (itemName.contains("冲刺矛") && plugin.isSpearRushEnabled()) {
            item = plugin.getItemManager().getSpearRush();
        } else if (itemName.contains("漂浮药水")) {
            item = plugin.getItemManager().getLevitationPotion();
        }
        
        if (item == null || item.getType() == org.bukkit.Material.AIR) {
            if (itemName.contains("冲刺矛")) {
                plugin.getLanguageManager().sendMessage(player, "item.spear-unavailable");
            } else {
                send(player, "admin.giveitem.unknown", "肾上腺素, 狂暴药水, 凝冰球, 控魂术, 臭牛排, 传送珍珠, 灵魂探测器, 第二次机会, 漂浮药水" + (plugin.isSpearRushEnabled() ? ", 冲刺矛" : ""));
            }
            return true;
        }
        
        player.getInventory().addItem(item);
        send(player, "admin.giveitem.received", itemName);
        return true;
    }
    
    // 神圣守护管理子命令（整合原/divineguardian）
    private boolean handleDivineSub(CommandSender sender, String[] args) {
        if (args.length < 2) {
            send(sender, "admin.divine.header");
            send(sender, "admin.divine.help-status");
            send(sender, "admin.divine.help-clear");
            return true;
        }
        String sub = args[1].toLowerCase();
        if (sub.equals("status")) {
            io.Sriptirc_wp_1258.gost.managers.DivineGuardianManager dg = plugin.getDivineGuardianManager();
            send(sender, "admin.divine.status-header");
            send(sender, "admin.divine.redeemers", dg.getRedeemerCount());
            send(sender, "admin.divine.dh-phase", plugin.getLanguageManager().getMessage(dg.isInDemonHunterPhase() ? "common.in-progress" : "common.not-started"));
            return true;
        } else if (sub.equals("clear")) {
            send(sender, "admin.divine.cleared");
            return true;
        }
        send(sender, "admin.divine.unknown");
        return true;
    }
    
    // 鬼粒子管理子命令（整合原/ghostparticle）
    private boolean handleParticleSub(CommandSender sender, String[] args) {
        if (args.length < 2) {
            send(sender, "admin.particle.header");
            send(sender, "admin.particle.help-status");
            send(sender, "admin.particle.help-toggle");
            return true;
        }
        String sub = args[1].toLowerCase();
        io.Sriptirc_wp_1258.gost.managers.GhostParticleManager gp = plugin.getGhostParticleManager();
        if (sub.equals("status")) {
            send(sender, "admin.particle.status-header");
            send(sender, "admin.particle.status", plugin.getLanguageManager().getMessage(gp != null ? "common.running" : "common.not-running"));
            return true;
        } else if (sub.equals("enable")) {
            send(sender, "admin.particle.enabled");
            return true;
        } else if (sub.equals("disable")) {
            send(sender, "admin.particle.disabled");
            return true;
        }
        send(sender, "admin.particle.unknown");
        return true;
    }
}