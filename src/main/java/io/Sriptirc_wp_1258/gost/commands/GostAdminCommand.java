package io.Sriptirc_wp_1258.gost.commands;

import io.Sriptirc_wp_1258.gost.Gost;
import io.Sriptirc_wp_1258.gost.managers.AreaManager;
import io.Sriptirc_wp_1258.gost.managers.SelectionManager;
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
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("gost.admin")) {
            sender.sendMessage(ChatColor.RED + "你没有权限使用此命令！");
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
            case "bot":
                return handleBot(sender, args);
            case "dark":
                return handleDark(sender, args);
            case "help":
                sendHelp(sender);
                return true;
            default:
                sender.sendMessage(ChatColor.RED + "未知命令！使用 /gostadmin help 查看帮助");
                return true;
        }
    }
    
    private boolean handleStart(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "用法: /gostadmin start <区域名称>");
            sender.sendMessage(ChatColor.YELLOW + "可用区域: " + String.join(", ", plugin.getAreaManager().getAreaNames()));
            return true;
        }
        
        String areaName = args[1];
        
        // 选择区域
        if (!plugin.getAreaManager().selectArea(areaName)) {
            sender.sendMessage(ChatColor.RED + "区域 '" + areaName + "' 不存在！");
            return true;
        }
        
        // 开始游戏
        if (!plugin.getGameManager().startGame()) {
            sender.sendMessage(ChatColor.RED + "无法开始游戏！");
        } else {
            sender.sendMessage(ChatColor.GREEN + "游戏已开始！使用区域: " + areaName);
        }
        return true;
    }
    
    private boolean handleStop(CommandSender sender) {
        if (!plugin.getGameManager().isAnyGameRunning()) {
            sender.sendMessage(ChatColor.RED + "当前没有游戏在进行！");
            return true;
        }
        
        // 强制停止游戏
        plugin.getGameManager().forceStopGame();
        sender.sendMessage(ChatColor.GREEN + "游戏已强制停止！");
        return true;
    }
    
    private boolean handlePos1(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "只有玩家可以使用此命令！");
            return true;
        }
        
        Player player = (Player) sender;
        player.sendMessage(ChatColor.YELLOW + "请使用选区工具左键点击方块设置第一个点");
        return true;
    }
    
    private boolean handlePos2(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "只有玩家可以使用此命令！");
            return true;
        }
        
        Player player = (Player) sender;
        player.sendMessage(ChatColor.YELLOW + "请使用选区工具右键点击方块设置第二个点");
        return true;
    }
    
    private boolean handleSave(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "只有玩家可以使用此命令！");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "用法: /gostadmin save <区域名称>");
            return true;
        }
        
        Player player = (Player) sender;
        String areaName = args[1];
        
        // 检查选区是否完整
        SelectionManager.PlayerSelection selection = plugin.getSelectionManager().getSelection(player);
        if (selection == null || !selection.isComplete()) {
            player.sendMessage(ChatColor.RED + "请先设置两个点！");
            player.sendMessage(ChatColor.YELLOW + "使用选区工具：左键设置第一个点，右键设置第二个点");
            return true;
        }
        
        // 检查是否在同一世界
        if (!selection.isValid()) {
            player.sendMessage(ChatColor.RED + "错误：两个点必须在同一世界！");
            return true;
        }
        
        // 保存区域
        if (plugin.getAreaManager().saveArea(areaName, selection.getPos1(), selection.getPos2())) {
            player.sendMessage(ChatColor.GREEN + "区域 '" + areaName + "' 已保存！");
        } else {
            player.sendMessage(ChatColor.RED + "保存失败！区域名称可能已存在或达到最大数量限制。");
        }
        
        return true;
    }
    
    private boolean handleList(CommandSender sender) {
        List<AreaManager.GameArea> areas = plugin.getAreaManager().getAllAreas();
        
        if (areas.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "暂无存档区域");
            return true;
        }
        
        sender.sendMessage(ChatColor.GOLD + "========== 存档区域列表 ==========");
        for (AreaManager.GameArea area : areas) {
            int[] dims = area.getDimensions();
            String selected = area.getName().equals(plugin.getAreaManager().getSelectedAreaName()) ? "✓" : " ";
            String enabled = plugin.getAreaManager().isAreaEnabled(area.getName()) ? "✔" : "✘";
            sender.sendMessage(ChatColor.YELLOW + "[" + selected + "] " + 
                ChatColor.GREEN + "[" + enabled + "] " + 
                ChatColor.WHITE + area.getName() + 
                ChatColor.GRAY + " - " + dims[0] + "×" + dims[1] + "×" + dims[2] + 
                " (" + area.getUsageCount() + "次使用)");
        }
        sender.sendMessage(ChatColor.GOLD + "==================================");
        return true;
    }
    
    private boolean handleLoad(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "用法: /gostadmin load <区域名称> [enable|disable]");
            sender.sendMessage(ChatColor.GRAY + "示例:");
            sender.sendMessage(ChatColor.GRAY + "  /gostadmin load area1 - 选择区域");
            sender.sendMessage(ChatColor.GRAY + "  /gostadmin load area1 enable - 启用区域");
            sender.sendMessage(ChatColor.GRAY + "  /gostadmin load area1 disable - 禁用区域");
            return true;
        }
        
        String areaName = args[1];
        
        // 检查区域是否存在
        if (plugin.getAreaManager().getArea(areaName) == null) {
            sender.sendMessage(ChatColor.RED + "区域 '" + areaName + "' 不存在！");
            return true;
        }
        
        // 如果有第三个参数，处理启用/禁用
        if (args.length >= 3) {
            String action = args[2].toLowerCase();
            switch (action) {
                case "enable":
                    if (plugin.getAreaManager().enableArea(areaName)) {
                        sender.sendMessage(ChatColor.GREEN + "区域 '" + areaName + "' 已启用！");
                    } else {
                        sender.sendMessage(ChatColor.YELLOW + "区域 '" + areaName + "' 已经启用或不存在");
                    }
                    break;
                    
                case "disable":
                    if (plugin.getAreaManager().disableArea(areaName)) {
                        sender.sendMessage(ChatColor.GREEN + "区域 '" + areaName + "' 已禁用！");
                    } else {
                        sender.sendMessage(ChatColor.YELLOW + "区域 '" + areaName + "' 未启用或不存在");
                    }
                    break;
                    
                default:
                    sender.sendMessage(ChatColor.RED + "未知操作！使用 enable 或 disable");
                    return true;
            }
        } else {
            // 没有第三个参数，选择区域
            if (plugin.getAreaManager().selectArea(areaName)) {
                sender.sendMessage(ChatColor.GREEN + "已选择区域: " + areaName);
            } else {
                sender.sendMessage(ChatColor.RED + "选择区域失败！");
            }
        }
        
        return true;
    }
    
    private boolean handleDelete(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "用法: /gostadmin delete <区域名称>");
            return true;
        }
        
        String areaName = args[1];
        
        if (plugin.getAreaManager().deleteArea(areaName)) {
            sender.sendMessage(ChatColor.GREEN + "区域 '" + areaName + "' 已删除！");
        } else {
            sender.sendMessage(ChatColor.RED + "区域 '" + areaName + "' 不存在！");
        }
        
        return true;
    }
    
    private boolean handleInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "用法: /gostadmin info <区域名称>");
            return true;
        }
        
        String areaName = args[1];
        AreaManager.GameArea area = plugin.getAreaManager().getArea(areaName);
        
        if (area == null) {
            sender.sendMessage(ChatColor.RED + "区域 '" + areaName + "' 不存在！");
            return true;
        }
        
        int[] dims = area.getDimensions();
        int volume = area.getVolume();
        
        sender.sendMessage(ChatColor.GOLD + "========== 区域信息 ==========");
        sender.sendMessage(ChatColor.YELLOW + "名称: " + ChatColor.WHITE + area.getName());
        sender.sendMessage(ChatColor.YELLOW + "世界: " + ChatColor.WHITE + area.getPos1().getWorld().getName());
        sender.sendMessage(ChatColor.YELLOW + "点1: " + ChatColor.WHITE + formatLocation(area.getPos1()));
        sender.sendMessage(ChatColor.YELLOW + "点2: " + ChatColor.WHITE + formatLocation(area.getPos2()));
        sender.sendMessage(ChatColor.YELLOW + "尺寸: " + ChatColor.WHITE + dims[0] + "×" + dims[1] + "×" + dims[2]);
        sender.sendMessage(ChatColor.YELLOW + "体积: " + ChatColor.WHITE + volume + " 方块");
        sender.sendMessage(ChatColor.YELLOW + "创建时间: " + ChatColor.WHITE + dateFormat.format(area.getCreatedDate()));
        sender.sendMessage(ChatColor.YELLOW + "最后使用: " + ChatColor.WHITE + dateFormat.format(area.getLastUsedDate()));
        sender.sendMessage(ChatColor.YELLOW + "使用次数: " + ChatColor.WHITE + area.getUsageCount());
        sender.sendMessage(ChatColor.GOLD + "==============================");
        
        return true;
    }
    
    private boolean handleTool(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "只有玩家可以使用此命令！");
            return true;
        }
        
        Player player = (Player) sender;
        plugin.getSelectionManager().giveSelectionTool(player);
        return true;
    }
    
    private boolean handleClear(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "只有玩家可以使用此命令！");
            return true;
        }
        
        Player player = (Player) sender;
        if (plugin.getSelectionManager().clearSelection(player)) {
            player.sendMessage(ChatColor.YELLOW + "选区已清除");
        } else {
            player.sendMessage(ChatColor.YELLOW + "没有选区需要清除");
        }
        return true;
    }
    
    private boolean handleReload(CommandSender sender) {
        plugin.getConfigManager().reloadConfig();
        plugin.getAreaManager().reload();
        sender.sendMessage(ChatColor.GREEN + "配置和区域数据已重新加载！");
        return true;
    }
    
    private boolean handleStatus(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "=== Gost 管理信息 ===");
        
        // 游戏状态
        String gameState;
        switch (plugin.getGameManager().getGameState()) {
            case WAITING:
                gameState = ChatColor.YELLOW + "等待玩家";
                break;
            case STARTING:
                gameState = ChatColor.GOLD + "准备中";
                break;
            case RUNNING:
                gameState = ChatColor.GREEN + "进行中";
                break;
            case ENDING:
                gameState = ChatColor.RED + "结束中";
                break;
            default:
                gameState = ChatColor.GRAY + "未开始";
                break;
        }
        sender.sendMessage(ChatColor.YELLOW + "游戏状态: " + gameState);
        
        // 玩家统计
        int waitingPlayers = plugin.getGameManager().getWaitingPlayersCount();
        int gamePlayers = plugin.getPlayerManager().getAllPlayers().size();
        int humanPlayers = plugin.getPlayerManager().getHumanPlayers().size();
        int ghostPlayers = plugin.getPlayerManager().getGhostPlayers().size();
        
        sender.sendMessage(ChatColor.YELLOW + "等待玩家: " + ChatColor.GOLD + waitingPlayers);
        sender.sendMessage(ChatColor.YELLOW + "游戏玩家: " + ChatColor.GOLD + gamePlayers);
        sender.sendMessage(ChatColor.YELLOW + "人类玩家: " + ChatColor.GREEN + humanPlayers);
        sender.sendMessage(ChatColor.YELLOW + "鬼玩家: " + ChatColor.RED + ghostPlayers);
        
        // 区域信息
        AreaManager.GameArea selectedArea = plugin.getAreaManager().getSelectedArea();
        if (selectedArea != null) {
            sender.sendMessage(ChatColor.YELLOW + "当前区域: " + ChatColor.GREEN + selectedArea.getName());
            int[] dims = selectedArea.getDimensions();
            sender.sendMessage(ChatColor.YELLOW + "区域尺寸: " + ChatColor.WHITE + dims[0] + "×" + dims[1] + "×" + dims[2]);
        } else {
            sender.sendMessage(ChatColor.YELLOW + "当前区域: " + ChatColor.RED + "未选择");
        }
        
        // 存档区域数量
        int areaCount = plugin.getAreaManager().getAreaNames().size();
        sender.sendMessage(ChatColor.YELLOW + "存档区域: " + ChatColor.GOLD + areaCount + " 个");
        
        // 经济信息
        if (plugin.getEconomyManager().isEconomyEnabled()) {
            double prizePool = plugin.getEconomyManager().getPrizePool();
            sender.sendMessage(ChatColor.YELLOW + "当前奖池: " + ChatColor.GOLD + prizePool + " 金币");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "经济系统: " + ChatColor.RED + "不可用");
        }
        
        // 配置信息
        sender.sendMessage(ChatColor.YELLOW + "最小玩家: " + ChatColor.GOLD + 
            plugin.getConfigManager().getMinPlayers());
        sender.sendMessage(ChatColor.YELLOW + "最大玩家: " + ChatColor.GOLD + 
            plugin.getConfigManager().getMaxPlayers());
        sender.sendMessage(ChatColor.YELLOW + "游戏时长: " + ChatColor.GOLD + 
            plugin.getConfigManager().getGameDuration() + "秒");
        
        return true;
    }
    
    private boolean handleBot(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "用法: /gostadmin bot <add|remove|clear|info|count> [数量]");
            sender.sendMessage(ChatColor.YELLOW + "  add <数量> - 添加指定数量的假人到队列");
            sender.sendMessage(ChatColor.YELLOW + "  remove <数量> - 从队列移除指定数量的假人");
            sender.sendMessage(ChatColor.YELLOW + "  clear - 清除所有假人");
            sender.sendMessage(ChatColor.YELLOW + "  info - 查看假人信息");
            sender.sendMessage(ChatColor.YELLOW + "  count - 查看假人数量");
            return true;
        }
        
        String action = args[1].toLowerCase();
        
        switch (action) {
            case "add":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "用法: /gostadmin bot add <数量>");
                    return true;
                }
                try {
                    int count = Integer.parseInt(args[2]);
                    if (count <= 0) {
                        sender.sendMessage(ChatColor.RED + "数量必须大于0！");
                        return true;
                    }
                    int added = plugin.getBotManager().addBotsToQueue(count);
                    sender.sendMessage(ChatColor.GREEN + "已添加 " + added + " 个假人到队列");
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "无效的数量！");
                }
                break;
                
            case "remove":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "用法: /gostadmin bot remove <数量>");
                    return true;
                }
                try {
                    int count = Integer.parseInt(args[2]);
                    if (count <= 0) {
                        sender.sendMessage(ChatColor.RED + "数量必须大于0！");
                        return true;
                    }
                    int removed = plugin.getBotManager().removeBotsFromQueue(count);
                    sender.sendMessage(ChatColor.GREEN + "已从队列移除 " + removed + " 个假人");
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "无效的数量！");
                }
                break;
                
            case "clear":
                plugin.getBotManager().clearAllBots();
                sender.sendMessage(ChatColor.GREEN + "已清除所有假人");
                break;
                
            case "info":
                sender.sendMessage(plugin.getBotManager().getBotInfo());
                break;
                
            case "count":
                int total = plugin.getBotManager().getActiveBotCount();
                int queued = plugin.getBotManager().getQueuedBotCount();
                sender.sendMessage(ChatColor.YELLOW + "假人统计:");
                sender.sendMessage(ChatColor.GRAY + "总假人数: " + ChatColor.WHITE + total);
                sender.sendMessage(ChatColor.GRAY + "队列中: " + ChatColor.WHITE + queued);
                sender.sendMessage(ChatColor.GRAY + "游戏中: " + ChatColor.WHITE + (total - queued));
                break;
                
            default:
                sender.sendMessage(ChatColor.RED + "未知操作！使用 /gostadmin bot 查看帮助");
                break;
        }
        
        return true;
    }
    
    private boolean handleDark(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "用法: /gostadmin dark <on|off|status>");
            sender.sendMessage(ChatColor.YELLOW + "  on - 启用黑暗效果（给予所有玩家失明效果）");
            sender.sendMessage(ChatColor.YELLOW + "  off - 禁用黑暗效果（移除所有玩家的失明效果）");
            sender.sendMessage(ChatColor.YELLOW + "  status - 查看黑暗效果状态");
            return true;
        }
        
        String action = args[1].toLowerCase();
        
        switch (action) {
            case "on":
                plugin.getDarkEffectManager().toggleDarkEffect(true);
                sender.sendMessage(ChatColor.GREEN + "黑暗效果已启用！所有玩家获得失明效果。");
                break;
                
            case "off":
                plugin.getDarkEffectManager().toggleDarkEffect(false);
                sender.sendMessage(ChatColor.GREEN + "黑暗效果已禁用！所有玩家的失明效果已移除。");
                break;
                
            case "status":
                boolean enabled = plugin.getDarkEffectManager().isDarkEffectEnabled();
                sender.sendMessage(ChatColor.YELLOW + "黑暗效果状态: " + 
                    (enabled ? ChatColor.GREEN + "已启用" : ChatColor.RED + "已禁用"));
                break;
                
            default:
                sender.sendMessage(ChatColor.RED + "未知操作！使用 /gostadmin dark 查看帮助");
                break;
        }
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "========== Gost 管理员命令 ==========");
        sender.sendMessage(ChatColor.YELLOW + "/gostadmin start <区域> - 使用指定区域开始游戏");
        sender.sendMessage(ChatColor.YELLOW + "/gostadmin stop - 停止游戏");
        sender.sendMessage(ChatColor.YELLOW + "/gostadmin tool - 获取选区工具");
        sender.sendMessage(ChatColor.YELLOW + "/gostadmin pos1 - 设置第一个点（使用工具左键）");
        sender.sendMessage(ChatColor.YELLOW + "/gostadmin pos2 - 设置第二个点（使用工具右键）");
        sender.sendMessage(ChatColor.YELLOW + "/gostadmin save <名称> - 保存当前选区");
        sender.sendMessage(ChatColor.YELLOW + "/gostadmin list - 列出所有存档区域");
        sender.sendMessage(ChatColor.YELLOW + "/gostadmin load <名称> [enable|disable] - 选择/启用/禁用区域");
        sender.sendMessage(ChatColor.YELLOW + "/gostadmin delete <名称> - 删除区域");
        sender.sendMessage(ChatColor.YELLOW + "/gostadmin info <名称> - 查看区域信息");
        sender.sendMessage(ChatColor.YELLOW + "/gostadmin clear - 清除当前选区");
        sender.sendMessage(ChatColor.YELLOW + "/gostadmin reload - 重新加载配置");
        sender.sendMessage(ChatColor.YELLOW + "/gostadmin status - 查看游戏状态");
        sender.sendMessage(ChatColor.YELLOW + "/gostadmin bot <add|remove|clear|info|count> - 假人系统管理");
        sender.sendMessage(ChatColor.YELLOW + "/gostadmin dark <on|off|status> - 黑暗效果管理");
        sender.sendMessage(ChatColor.YELLOW + "/gostadmin help - 显示此帮助");
        sender.sendMessage(ChatColor.GOLD + "==================================");
    }
    
    private String formatLocation(org.bukkit.Location location) {
        if (location == null) return "未设置";
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
            String[] subCommands = {"start", "stop", "pos1", "pos2", "save", "list", "load", "delete", "info", "tool", "clear", "reload", "status", "bot", "dark", "help"};
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
            } else if (subCommand.equals("bot")) {
                // bot子命令自动补全
                String[] botSubCommands = {"add", "remove", "clear", "info", "count"};
                for (String botSubCommand : botSubCommands) {
                    if (botSubCommand.startsWith(args[1].toLowerCase())) {
                        completions.add(botSubCommand);
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
            }
        }
        
        return completions;
    }
}