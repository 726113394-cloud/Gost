package io.Sriptirc_wp_1258.gost.commands;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 神圣守护管理命令
 */
public class DivineGuardianCommand implements CommandExecutor, TabCompleter {
    
    private final Gost plugin;
    
    public DivineGuardianCommand(Gost plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "status":
                handleStatus(sender);
                break;
                
            case "enable":
                handleEnable(sender);
                break;
                
            case "disable":
                handleDisable(sender);
                break;
                
            case "reload":
                handleReload(sender);
                break;
                
            case "setcharges":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.usage_setcharges"));
                    return true;
                }
                handleSetCharges(sender, args[1]);
                break;
                
            case "setcooldown":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.usage_setcooldown"));
                    return true;
                }
                handleSetCooldown(sender, args[1]);
                break;
                
            case "broadcast":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.usage_broadcast"));
                    return true;
                }
                handleBroadcast(sender, args[1]);
                break;
                
            case "info":
                handleInfo(sender);
                break;
                
            case "force":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.usage_force"));
                    return true;
                }
                handleForce(sender, args[1]);
                break;
                
            case "clear":
                handleClear(sender);
                break;
                
            default:
                sendUsage(sender);
                break;
        }
        
        return true;
    }
    
    /**
     * 发送命令用法
     */
    private void sendUsage(CommandSender sender) {
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_usage_header"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_usage_title"));
        sender.sendMessage("");
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_usage_status"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_usage_enable"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_usage_disable"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_usage_reload"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_usage_info"));
        sender.sendMessage("");
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_usage_setcharges"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_usage_setcooldown"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_usage_broadcast"));
        sender.sendMessage("");
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_usage_force"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_usage_clear"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_usage_separator"));
    }
    
    /**
     * 处理状态命令
     */
    private void handleStatus(CommandSender sender) {
        boolean enabled = plugin.getConfigManager().isDivineGuardianEnabled();
        int maxCharges = plugin.getConfigManager().getDivineGuardianMaxCharges();
        int cooldown = plugin.getConfigManager().getDivineGuardianCooldown();
        boolean broadcast = plugin.getConfigManager().isDivineGuardianBroadcastEnabled();
        int invisibilityDuration = plugin.getConfigManager().getDivineGuardianInvisibilityDuration();
        
        UUID divineGuardian = plugin.getDivineGuardianManager().getDivineGuardianPlayer();
        String guardianName = divineGuardian != null ? Bukkit.getOfflinePlayer(divineGuardian).getName() : "无";
        int remainingCharges = divineGuardian != null ? 
            plugin.getDivineGuardianManager().getRemainingCharges(divineGuardian) : 0;
        
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_status_header"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_status_title"));
        sender.sendMessage("");
        sender.sendMessage(enabled ? plugin.getLanguageManager().getMessage("dg_command.dg_status_enabled") : plugin.getLanguageManager().getMessage("dg_command.dg_status_disabled"));
        sender.sendMessage(guardianName.equals("无") ? 
            plugin.getLanguageManager().getMessage("dg_command.dg_status_player_none") : 
            plugin.getLanguageManager().getMessage("dg_command.dg_status_player", guardianName));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_status_remaining", remainingCharges));
        sender.sendMessage("");
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_status_config"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_status_max_charges", maxCharges));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_status_cooldown", cooldown));
        sender.sendMessage(broadcast ? plugin.getLanguageManager().getMessage("dg_command.dg_status_broadcast_on") : plugin.getLanguageManager().getMessage("dg_command.dg_status_broadcast_off"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_status_invisibility", invisibilityDuration));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_usage_separator"));
    }
    
    /**
     * 处理启用命令
     */
    private void handleEnable(CommandSender sender) {
        plugin.getConfigManager().setDivineGuardianEnabled(true);
        plugin.getDivineGuardianManager().reload();
        
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_enabled"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_reload_hint"));
    }
    
    /**
     * 处理禁用命令
     */
    private void handleDisable(CommandSender sender) {
        plugin.getConfigManager().setDivineGuardianEnabled(false);
        plugin.getDivineGuardianManager().reload();
        
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_disabled"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_reload_hint"));
    }
    
    /**
     * 处理重新加载命令
     */
    private void handleReload(CommandSender sender) {
        plugin.getDivineGuardianManager().reload();
        
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_reloaded"));
    }
    
    /**
     * 处理设置使用次数命令
     */
    private void handleSetCharges(CommandSender sender, String chargesStr) {
        try {
            int charges = Integer.parseInt(chargesStr);
            
            if (charges < 1 || charges > 10) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_setcharges_invalid"));
                return;
            }
            
            plugin.getConfigManager().setDivineGuardianMaxCharges(charges);
            
            sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_setcharges_success", charges));
            sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_reload_hint"));
            
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_not_number"));
        }
    }
    
    /**
     * 处理设置冷却时间命令
     */
    private void handleSetCooldown(CommandSender sender, String cooldownStr) {
        try {
            int cooldown = Integer.parseInt(cooldownStr);
            
            if (cooldown < 1 || cooldown > 60) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_setcooldown_invalid"));
                return;
            }
            
            plugin.getConfigManager().setDivineGuardianCooldown(cooldown);
            
            sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_setcooldown_success", cooldown));
            sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_reload_hint"));
            
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_not_number"));
        }
    }
    
    /**
     * 处理设置广播命令
     */
    private void handleBroadcast(CommandSender sender, String value) {
        boolean enable = value.equalsIgnoreCase("on") || value.equalsIgnoreCase("true") || value.equals("1");
        
        plugin.getConfigManager().setDivineGuardianBroadcastEnabled(enable);
        
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_broadcast_success", enable ? "开启" : "关闭"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_reload_hint"));
    }
    
    /**
     * 处理信息命令
     */
    private void handleInfo(CommandSender sender) {
        UUID divineGuardian = plugin.getDivineGuardianManager().getDivineGuardianPlayer();
        
        if (divineGuardian == null) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_no_active_guardian"));
            return;
        }
        
        Player player = Bukkit.getPlayer(divineGuardian);
        if (player == null) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_player_not_online"));
            return;
        }
        
        int remainingCharges = plugin.getDivineGuardianManager().getRemainingCharges(divineGuardian);
        
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_info_header"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_info_title"));
        sender.sendMessage("");
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_info_player", player.getName()));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_info_remaining", remainingCharges));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_info_location",
            (int)player.getLocation().getX(),
            (int)player.getLocation().getY(),
            (int)player.getLocation().getZ()));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_info_world", player.getWorld().getName()));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_info_health", (int)player.getHealth(), (int)player.getMaxHealth()));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_info_food", player.getFoodLevel()));
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_usage_separator"));
    }
    
    /**
     * 处理强制激活命令
     */
    private void handleForce(CommandSender sender, String playerName) {
        if (!plugin.getGameManager().isGameRunning()) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_force_no_game"));
            return;
        }
        
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_force_player_offline", playerName));
            return;
        }
        
        if (!plugin.getPlayerManager().isHuman(target.getUniqueId())) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_force_not_human", playerName));
            return;
        }
        
        // 强制激活神圣守护
        List<UUID> humanPlayers = new ArrayList<>();
        humanPlayers.add(target.getUniqueId());
        plugin.getDivineGuardianManager().checkAndActivateDivineGuardian(humanPlayers);
        
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_force_success", target.getName()));
        target.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_force_target"));
    }
    
    /**
     * 处理清除命令
     */
    private void handleClear(CommandSender sender) {
        plugin.getDivineGuardianManager().cleanup();
        
        sender.sendMessage(plugin.getLanguageManager().getMessage("dg_command.dg_cleared"));
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // 主命令补全
            completions.addAll(Arrays.asList(
                "status", "enable", "disable", "reload", 
                "setcharges", "setcooldown", "broadcast", 
                "info", "force", "clear"
            ));
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            
            switch (subCommand) {
                case "setcharges":
                    completions.addAll(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
                    break;
                    
                case "setcooldown":
                    completions.addAll(Arrays.asList("1", "3", "5", "10", "15", "20", "30", "60"));
                    break;
                    
                case "broadcast":
                    completions.addAll(Arrays.asList("on", "off", "true", "false"));
                    break;
                    
                case "force":
                    // 在线玩家补全
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        completions.add(player.getName());
                    }
                    break;
            }
        }
        
        // 过滤匹配的补全
        String currentArg = args[args.length - 1].toLowerCase();
        completions.removeIf(s -> !s.toLowerCase().startsWith(currentArg));
        
        return completions;
    }
}