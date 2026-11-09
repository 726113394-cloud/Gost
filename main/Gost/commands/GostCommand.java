package io.Sriptirc_wp_1258.gost.commands;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GostCommand implements CommandExecutor, TabCompleter {
    
    private final Gost plugin;
    
    public GostCommand(Gost plugin) {
        this.plugin = plugin;
    }
    
    /** 按玩家自己的语言发消息；控制台用服务器默认语言 */
    private void send(CommandSender sender, String key, Object... args) {
        Player player = sender instanceof Player ? (Player) sender : null;
        sender.sendMessage(plugin.getLanguageManager().getMessage(player, key, args));
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "join":
                if (!requirePlayer(sender)) return true;
                if (!sender.hasPermission("gost.player")) {
                    send(sender, "error.no-permission");
                    return true;
                }
                handleJoin((Player) sender);
                break;
            case "leave":
                if (!requirePlayer(sender)) return true;
                if (!sender.hasPermission("gost.player")) {
                    send(sender, "error.no-permission");
                    return true;
                }
                handleLeave((Player) sender);
                break;
            case "info":
                if (!requirePlayer(sender)) return true;
                if (!sender.hasPermission("gost.player")) {
                    send(sender, "error.no-permission");
                    return true;
                }
                handleInfo((Player) sender);
                break;
            case "lang":
                handleLang(sender, args);
                break;
            case "help":
                sendHelp(sender);
                break;
            case "start":
                if (!sender.hasPermission("gost.admin")) {
                    send(sender, "error.no-permission");
                    return true;
                }
                handleAdminStart(sender);
                break;
            default:
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    /** 仅玩家可用的子命令，控制台执行时给出提示 */
    private boolean requirePlayer(CommandSender sender) {
        if (sender instanceof Player) return true;
        send(sender, "error.player-only");
        return false;
    }
    
    private void handleJoin(Player player) {
        // 检查权限
        if (!player.hasPermission("gost.join")) {
            send(player, "command.join.no-permission");
            return;
        }
        
        // 检查是否有游戏在进行
        if (plugin.getGameManager().isAnyGameRunning()) {
            send(player, "error.game-in-progress");
            return;
        }
        
        // 检查最大游戏数
        if (plugin.getConfigManager().getMaxGames() == 0) {
            send(player, "error.cannot-start");
            return;
        }
        
        // 加入队列
        if (plugin.getGameManager().joinQueue(player)) {
            send(player, "command.join.success");
        }
    }
    
    private void handleLeave(Player player) {
        // 检查权限
        if (!player.hasPermission("gost.leave")) {
            send(player, "command.leave.no-permission");
            return;
        }
        
        // 尝试离开游戏
        if (plugin.getPlayerManager().leaveGame(player)) {
            send(player, "command.leave.success");
            return;
        }
        
        // 尝试离开队列
        if (plugin.getGameManager().leaveQueue(player)) {
            send(player, "command.leave.queue-success");
            return;
        }
        
        send(player, "command.leave.not-in");
    }
    
    private void handleInfo(Player player) {
        // 检查游戏状态
        if (plugin.getGameManager().isGameRunning()) {
            int humanCount = plugin.getPlayerManager().getHumanPlayers().size();
            int ghostCount = plugin.getPlayerManager().getGhostPlayers().size();
            
            send(player, "command.info.header");
            send(player, "command.info.state", plugin.getLanguageManager().getMessage(player, "command.info.state-running"));
            send(player, "command.info.humans", humanCount);
            send(player, "command.info.ghosts", ghostCount);
            
            // 显示奖池信息
            if (plugin.getEconomyManager().isEconomyEnabled()) {
                double prizePool = plugin.getEconomyManager().getPrizePool();
                send(player, "command.info.prize-pool", prizePool);
            }
        } else if (plugin.getGameManager().getWaitingPlayersCount() > 0) {
            int waitingPlayers = plugin.getGameManager().getWaitingPlayersCount();
            int minPlayers = plugin.getConfigManager().getMinPlayers();
            
            send(player, "command.info.queue-header");
            send(player, "command.info.state", plugin.getLanguageManager().getMessage(player, "command.info.state-waiting"));
            send(player, "command.info.waiting", waitingPlayers, minPlayers);
        } else {
            send(player, "command.info.header");
            send(player, "command.info.state", plugin.getLanguageManager().getMessage(player, "command.info.state-idle"));
            send(player, "command.info.hint-join");
        }
        
        // 显示区域信息
        io.Sriptirc_wp_1258.gost.managers.AreaManager.GameArea selectedArea = plugin.getAreaManager().getSelectedArea();
        if (selectedArea != null) {
            send(player, "command.info.area", selectedArea.getName());
            int[] dims = selectedArea.getDimensions();
            send(player, "command.info.area-size", dims[0], dims[1], dims[2]);
        } else {
            send(player, "command.info.area-none");
        }
    }
    
    private void handleLang(CommandSender sender, String[] args) {
        if (args.length < 2) {
            send(sender, "command.lang.usage", plugin.getLanguageManager().listSupported());
            return;
        }
        
        String requested = args[1];
        if ("reset".equalsIgnoreCase(requested)) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                plugin.getLanguageManager().clearLanguage(player);
                send(player, "command.lang.reset", plugin.getLanguageManager().getCurrentLanguage());
            } else {
                send(sender, "command.lang.usage", plugin.getLanguageManager().listSupported());
            }
            return;
        }
        
        String locale = plugin.getLanguageManager().resolveLocale(requested);
        if (locale == null) {
            send(sender, "command.lang.invalid", requested, plugin.getLanguageManager().listSupported());
            return;
        }
        
        if (sender instanceof Player) {
            Player player = (Player) sender;
            plugin.getLanguageManager().setLanguage(player, locale);
            send(player, "command.lang.set", locale);
            return;
        }
        
        // 控制台无法单独设语言，提示用法即可
        send(sender, "command.lang.usage", plugin.getLanguageManager().listSupported());
    }
    
    private void sendHelp(CommandSender sender) {
        send(sender, "command.help.header");
        send(sender, "command.help.join");
        send(sender, "command.help.leave");
        send(sender, "command.help.info");
        send(sender, "command.help.lang");
        send(sender, "command.help.help");
        
        if (sender.hasPermission("gost.admin")) {
            send(sender, "command.help.admin-header");
            send(sender, "command.help.admin-start");
            send(sender, "command.help.admin-more");
        }
    }
    
    private void handleAdminStart(CommandSender sender) {
        // 检查是否有游戏正在进行
        if (plugin.getGameManager().isAnyGameRunning()) {
            send(sender, "error.game-in-progress");
            return;
        }
        
        // 管理员不在队列中则自动加入队列（控制台没有玩家身份，跳过）
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!plugin.getGameManager().isInQueue(player.getUniqueId())) {
                plugin.getGameManager().joinQueue(player);
                send(player, "command.join.success");
            }
        }
        
        // 管理员强制开局
        boolean started = plugin.getGameManager().startGame(true);
        if (started) {
            String name = sender instanceof Player ? sender.getName() : "Console";
            Bukkit.broadcastMessage(plugin.getLanguageManager().getMessage("command.admin.started", name));
        } else {
            send(sender, "command.admin.start-failed");
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.add("join");
            completions.add("leave");
            completions.add("info");
            completions.add("help");
            completions.add("lang");
            if (sender.hasPermission("gost.admin")) {
                completions.add("start");
            }
        } else if (args.length == 2 && "lang".equalsIgnoreCase(args[0])) {
            for (String locale : io.Sriptirc_wp_1258.gost.managers.LanguageManager.SUPPORTED) {
                completions.add(locale);
            }
        }
        
        return completions;
    }
}
