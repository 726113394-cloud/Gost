package io.Sriptirc_wp_1258.gost.commands;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.ChatColor;
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
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getLanguageManager().sendMessage((Player) sender, "general.player_only");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "join":
                if (!player.hasPermission("gost.player")) {
                    plugin.getLanguageManager().sendMessage(player, "general.no_permission");
                    return true;
                }
                handleJoin(player);
                break;
            case "leave":
                if (!player.hasPermission("gost.player")) {
                    plugin.getLanguageManager().sendMessage(player, "general.no_permission");
                    return true;
                }
                handleLeave(player);
                break;
            case "info":
                if (!player.hasPermission("gost.player")) {
                    plugin.getLanguageManager().sendMessage(player, "general.no_permission");
                    return true;
                }
                handleInfo(player);
                break;
            case "help":
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void handleJoin(Player player) {
        // 检查权限
        if (!player.hasPermission("gost.join")) {
            plugin.getLanguageManager().sendMessage(player, "player.no_permission_join");
            return;
        }
        
        // 检查是否有游戏在进行
        if (plugin.getGameManager().isAnyGameRunning()) {
            plugin.getLanguageManager().sendMessage(player, "player.game_in_progress");
            return;
        }
        
        // 检查最大游戏数
        if (plugin.getConfigManager().getMaxGames() == 0) {
            plugin.getLanguageManager().sendMessage(player, "player.game_not_allowed");
            return;
        }
        
        // 加入队列（GameManager.joinQueue 已处理消息发送）
        plugin.getGameManager().joinQueue(player);
    }
    
    private void handleLeave(Player player) {
        // 检查权限
        if (!player.hasPermission("gost.leave")) {
            plugin.getLanguageManager().sendMessage(player, "player.no_permission_leave");
            return;
        }
        
        // 尝试离开游戏
        if (plugin.getPlayerManager().leaveGame(player)) {
            plugin.getLanguageManager().sendMessage(player, "player.left_game");
            return;
        }
        
        // 尝试离开队列（GameManager.leaveQueue 已处理消息发送）
        if (plugin.getGameManager().leaveQueue(player)) {
            return;
        }
        
        plugin.getLanguageManager().sendMessage(player, "player.not_in_game_or_queue");
    }
    
    private void handleInfo(Player player) {
        // 检查游戏状态
        if (plugin.getGameManager().isGameRunning()) {
            int humanCount = plugin.getPlayerManager().getHumanPlayers().size();
            int ghostCount = plugin.getPlayerManager().getGhostPlayers().size();
            
            plugin.getLanguageManager().sendMessage(player, "game.info_header");
            plugin.getLanguageManager().sendMessage(player, "game.info_status_running");
            plugin.getLanguageManager().sendMessage(player, "game.info_human_count", humanCount);
            plugin.getLanguageManager().sendMessage(player, "game.info_ghost_count", ghostCount);
            
            // 显示奖池信息
            if (plugin.getEconomyManager().isEconomyEnabled()) {
                double prizePool = plugin.getEconomyManager().getPrizePool();
                plugin.getLanguageManager().sendMessage(player, "game.info_prize_pool", prizePool);
            }
        } else if (plugin.getGameManager().getWaitingPlayersCount() > 0) {
            int waitingPlayers = plugin.getGameManager().getWaitingPlayersCount();
            int minPlayers = plugin.getConfigManager().getMinPlayers();
            
            plugin.getLanguageManager().sendMessage(player, "game.info_queue_header");
            plugin.getLanguageManager().sendMessage(player, "game.info_status_waiting");
            plugin.getLanguageManager().sendMessage(player, "game.info_waiting_players", waitingPlayers, minPlayers);
        } else {
            plugin.getLanguageManager().sendMessage(player, "game.info_header");
            plugin.getLanguageManager().sendMessage(player, "game.info_status_not_started");
            plugin.getLanguageManager().sendMessage(player, "game.info_join_hint");
        }
        
        // 显示区域信息
        io.Sriptirc_wp_1258.gost.managers.AreaManager.GameArea selectedArea = plugin.getAreaManager().getSelectedArea();
        if (selectedArea != null) {
            plugin.getLanguageManager().sendMessage(player, "game.info_area_current", selectedArea.getName());
            int[] dims = selectedArea.getDimensions();
            plugin.getLanguageManager().sendMessage(player, "game.info_area_size", dims[0], dims[1], dims[2]);
        } else {
            plugin.getLanguageManager().sendMessage(player, "game.info_area_none");
        }
    }
    
    private void sendHelp(Player player) {
        plugin.getLanguageManager().sendMessage(player, "game.help_header");
        plugin.getLanguageManager().sendMessage(player, "game.help_join");
        plugin.getLanguageManager().sendMessage(player, "game.help_leave");
        plugin.getLanguageManager().sendMessage(player, "game.help_info");
        plugin.getLanguageManager().sendMessage(player, "game.help_help");
        
        if (player.hasPermission("gost.admin")) {
            plugin.getLanguageManager().sendMessage(player, "game.help_admin_hint");
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
        }
        
        return completions;
    }
}