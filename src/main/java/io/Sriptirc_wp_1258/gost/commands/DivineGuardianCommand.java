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
                    sender.sendMessage(ChatColor.RED + "用法: /divineguardian setcharges <次数>");
                    return true;
                }
                handleSetCharges(sender, args[1]);
                break;
                
            case "setcooldown":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "用法: /divineguardian setcooldown <秒数>");
                    return true;
                }
                handleSetCooldown(sender, args[1]);
                break;
                
            case "broadcast":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "用法: /divineguardian broadcast <on|off>");
                    return true;
                }
                handleBroadcast(sender, args[1]);
                break;
                
            case "info":
                handleInfo(sender);
                break;
                
            case "force":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "用法: /divineguardian force <玩家名>");
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
        sender.sendMessage(ChatColor.GOLD + "════════════════════════════════");
        sender.sendMessage(ChatColor.GOLD + "              ✨ 神圣守护管理 ✨");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "/divineguardian status" + ChatColor.GRAY + " - 查看神圣守护状态");
        sender.sendMessage(ChatColor.YELLOW + "/divineguardian enable" + ChatColor.GRAY + " - 启用神圣守护");
        sender.sendMessage(ChatColor.YELLOW + "/divineguardian disable" + ChatColor.GRAY + " - 禁用神圣守护");
        sender.sendMessage(ChatColor.YELLOW + "/divineguardian reload" + ChatColor.GRAY + " - 重新加载配置");
        sender.sendMessage(ChatColor.YELLOW + "/divineguardian info" + ChatColor.GRAY + " - 查看当前神圣守护信息");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "/divineguardian setcharges <次数>" + ChatColor.GRAY + " - 设置最大使用次数");
        sender.sendMessage(ChatColor.YELLOW + "/divineguardian setcooldown <秒数>" + ChatColor.GRAY + " - 设置冷却时间");
        sender.sendMessage(ChatColor.YELLOW + "/divineguardian broadcast <on|off>" + ChatColor.GRAY + " - 设置广播开关");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "/divineguardian force <玩家名>" + ChatColor.GRAY + " - 强制激活神圣守护");
        sender.sendMessage(ChatColor.YELLOW + "/divineguardian clear" + ChatColor.GRAY + " - 清除神圣守护数据");
        sender.sendMessage(ChatColor.GOLD + "════════════════════════════════");
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
        
        sender.sendMessage(ChatColor.GOLD + "════════════════════════════════");
        sender.sendMessage(ChatColor.GOLD + "              ✨ 神圣守护状态 ✨");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "状态: " + (enabled ? ChatColor.GREEN + "已启用" : ChatColor.RED + "已禁用"));
        sender.sendMessage(ChatColor.YELLOW + "当前神圣守护玩家: " + ChatColor.GREEN + guardianName);
        sender.sendMessage(ChatColor.YELLOW + "剩余使用次数: " + ChatColor.GREEN + remainingCharges);
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "配置信息:");
        sender.sendMessage(ChatColor.GRAY + "  • 最大使用次数: " + ChatColor.GREEN + maxCharges);
        sender.sendMessage(ChatColor.GRAY + "  • 冷却时间: " + ChatColor.GREEN + cooldown + "秒");
        sender.sendMessage(ChatColor.GRAY + "  • 广播消息: " + (broadcast ? ChatColor.GREEN + "开启" : ChatColor.RED + "关闭"));
        sender.sendMessage(ChatColor.GRAY + "  • 失效隐身时间: " + ChatColor.GREEN + invisibilityDuration + "秒");
        sender.sendMessage(ChatColor.GOLD + "════════════════════════════════");
    }
    
    /**
     * 处理启用命令
     */
    private void handleEnable(CommandSender sender) {
        plugin.getConfigManager().setDivineGuardianEnabled(true);
        plugin.getDivineGuardianManager().reload();
        
        sender.sendMessage(ChatColor.GREEN + "✅ 神圣守护功能已启用！");
        sender.sendMessage(ChatColor.YELLOW + "注意：需要重新加载配置或重启游戏才能生效");
    }
    
    /**
     * 处理禁用命令
     */
    private void handleDisable(CommandSender sender) {
        plugin.getConfigManager().setDivineGuardianEnabled(false);
        plugin.getDivineGuardianManager().reload();
        
        sender.sendMessage(ChatColor.GREEN + "✅ 神圣守护功能已禁用！");
        sender.sendMessage(ChatColor.YELLOW + "注意：需要重新加载配置或重启游戏才能生效");
    }
    
    /**
     * 处理重新加载命令
     */
    private void handleReload(CommandSender sender) {
        plugin.getDivineGuardianManager().reload();
        
        sender.sendMessage(ChatColor.GREEN + "✅ 神圣守护配置已重新加载！");
    }
    
    /**
     * 处理设置使用次数命令
     */
    private void handleSetCharges(CommandSender sender, String chargesStr) {
        try {
            int charges = Integer.parseInt(chargesStr);
            
            if (charges < 1 || charges > 10) {
                sender.sendMessage(ChatColor.RED + "❌ 使用次数必须在1-10之间！");
                return;
            }
            
            plugin.getConfigManager().setDivineGuardianMaxCharges(charges);
            
            sender.sendMessage(ChatColor.GREEN + "✅ 神圣守护最大使用次数已设置为: " + charges);
            sender.sendMessage(ChatColor.YELLOW + "注意：需要重新加载配置或重启游戏才能生效");
            
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "❌ 请输入有效的数字！");
        }
    }
    
    /**
     * 处理设置冷却时间命令
     */
    private void handleSetCooldown(CommandSender sender, String cooldownStr) {
        try {
            int cooldown = Integer.parseInt(cooldownStr);
            
            if (cooldown < 1 || cooldown > 60) {
                sender.sendMessage(ChatColor.RED + "❌ 冷却时间必须在1-60秒之间！");
                return;
            }
            
            plugin.getConfigManager().setDivineGuardianCooldown(cooldown);
            
            sender.sendMessage(ChatColor.GREEN + "✅ 神圣守护冷却时间已设置为: " + cooldown + "秒");
            sender.sendMessage(ChatColor.YELLOW + "注意：需要重新加载配置或重启游戏才能生效");
            
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "❌ 请输入有效的数字！");
        }
    }
    
    /**
     * 处理设置广播命令
     */
    private void handleBroadcast(CommandSender sender, String value) {
        boolean enable = value.equalsIgnoreCase("on") || value.equalsIgnoreCase("true") || value.equals("1");
        
        plugin.getConfigManager().setDivineGuardianBroadcastEnabled(enable);
        
        sender.sendMessage(ChatColor.GREEN + "✅ 神圣守护广播消息已" + (enable ? "开启" : "关闭"));
        sender.sendMessage(ChatColor.YELLOW + "注意：需要重新加载配置或重启游戏才能生效");
    }
    
    /**
     * 处理信息命令
     */
    private void handleInfo(CommandSender sender) {
        UUID divineGuardian = plugin.getDivineGuardianManager().getDivineGuardianPlayer();
        
        if (divineGuardian == null) {
            sender.sendMessage(ChatColor.YELLOW + "⚠ 当前没有激活的神圣守护玩家");
            return;
        }
        
        Player player = Bukkit.getPlayer(divineGuardian);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "❌ 神圣守护玩家不在线！");
            return;
        }
        
        int remainingCharges = plugin.getDivineGuardianManager().getRemainingCharges(divineGuardian);
        
        sender.sendMessage(ChatColor.GOLD + "════════════════════════════════");
        sender.sendMessage(ChatColor.GOLD + "              ✨ 神圣守护信息 ✨");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "玩家: " + ChatColor.GREEN + player.getName());
        sender.sendMessage(ChatColor.YELLOW + "剩余使用次数: " + ChatColor.GREEN + remainingCharges);
        sender.sendMessage(ChatColor.YELLOW + "位置: " + ChatColor.GREEN + 
            "X=" + (int)player.getLocation().getX() + 
            ", Y=" + (int)player.getLocation().getY() + 
            ", Z=" + (int)player.getLocation().getZ());
        sender.sendMessage(ChatColor.YELLOW + "世界: " + ChatColor.GREEN + player.getWorld().getName());
        sender.sendMessage(ChatColor.YELLOW + "血量: " + ChatColor.GREEN + player.getHealth() + "/" + player.getMaxHealth());
        sender.sendMessage(ChatColor.YELLOW + "饥饿值: " + ChatColor.GREEN + player.getFoodLevel() + "/20");
        sender.sendMessage(ChatColor.GOLD + "════════════════════════════════");
    }
    
    /**
     * 处理强制激活命令
     */
    private void handleForce(CommandSender sender, String playerName) {
        if (!plugin.getGameManager().isGameRunning()) {
            sender.sendMessage(ChatColor.RED + "❌ 游戏未开始，无法激活神圣守护！");
            return;
        }
        
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "❌ 玩家 " + playerName + " 不在线！");
            return;
        }
        
        if (!plugin.getPlayerManager().isHuman(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "❌ 玩家 " + playerName + " 不是人类，无法激活神圣守护！");
            return;
        }
        
        // 强制激活神圣守护
        List<UUID> humanPlayers = new ArrayList<>();
        humanPlayers.add(target.getUniqueId());
        plugin.getDivineGuardianManager().checkAndActivateDivineGuardian(humanPlayers);
        
        sender.sendMessage(ChatColor.GREEN + "✅ 已强制为玩家 " + target.getName() + " 激活神圣守护！");
        target.sendMessage(ChatColor.GOLD + "✨ 管理员为你激活了神圣守护！");
    }
    
    /**
     * 处理清除命令
     */
    private void handleClear(CommandSender sender) {
        plugin.getDivineGuardianManager().cleanup();
        
        sender.sendMessage(ChatColor.GREEN + "✅ 神圣守护数据已清除！");
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