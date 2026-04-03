package io.Sriptirc_wp_1258.gost.commands;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 鬼玩家粒子效果管理命令
 */
public class GhostParticleCommand implements CommandExecutor, TabCompleter {
    
    private final Gost plugin;
    
    public GhostParticleCommand(Gost plugin) {
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
                
            case "settype":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_settype"));
                    return true;
                }
                handleSetType(sender, args[1]);
                break;
                
            case "setcount":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_setcount"));
                    return true;
                }
                handleSetCount(sender, args[1]);
                break;
                
            case "setinterval":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_setinterval"));
                    return true;
                }
                handleSetInterval(sender, args[1]);
                break;
                
            case "setmothercolor":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_setmothercolor"));
                    return true;
                }
                handleSetMotherColor(sender, args[1]);
                break;
                
            case "setnormalcolor":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_setnormalcolor"));
                    return true;
                }
                handleSetNormalColor(sender, args[1]);
                break;
                
            case "setsize":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_setsize"));
                    return true;
                }
                handleSetSize(sender, args[1]);
                break;
                
            case "setpreparation":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_setpreparation"));
                    return true;
                }
                handleSetPreparation(sender, args[1]);
                break;
                
            case "test":
                handleTest(sender);
                break;
                
            case "listtypes":
                handleListTypes(sender);
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
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_header"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_title"));
        sender.sendMessage("");
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_status"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_enable"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_disable"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_reload"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_test"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_listtypes"));
        sender.sendMessage("");
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_settype"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_setcount"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_setinterval"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_setmothercolor"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_setnormalcolor"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_setsize"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_setpreparation"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_header"));
    }
    
    /**
     * 处理状态命令
     */
    private void handleStatus(CommandSender sender) {
        boolean enabled = plugin.getConfigManager().isGhostParticleEnabled();
        String type = plugin.getConfigManager().getGhostParticleType();
        int count = plugin.getConfigManager().getGhostParticleCount();
        int interval = plugin.getConfigManager().getGhostParticleInterval();
        String motherColor = plugin.getConfigManager().getGhostParticleMotherColor();
        String normalColor = plugin.getConfigManager().getGhostParticleNormalColor();
        double size = plugin.getConfigManager().getGhostParticleSize();
        boolean showInPreparation = plugin.getConfigManager().isGhostParticleShowInPreparation();
        
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.status_header"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.status_title"));
        sender.sendMessage("");
        sender.sendMessage(enabled ? plugin.getLanguageManager().getMessage("ghost_particle_command.status_enabled") : plugin.getLanguageManager().getMessage("ghost_particle_command.status_disabled"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.status_type", type));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.status_count", count));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.status_interval", interval, interval / 20.0));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.status_mother_color", motherColor));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.status_normal_color", normalColor));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.status_size", size));
        sender.sendMessage(showInPreparation ? plugin.getLanguageManager().getMessage("ghost_particle_command.status_preparation_on") : plugin.getLanguageManager().getMessage("ghost_particle_command.status_preparation_off"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_header"));
    }
    
    /**
     * 处理启用命令
     */
    private void handleEnable(CommandSender sender) {
        plugin.getConfigManager().setGhostParticleEnabled(true);
        plugin.getGhostParticleManager().reload();
        
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.enabled"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.reload_hint"));
    }
    
    /**
     * 处理禁用命令
     */
    private void handleDisable(CommandSender sender) {
        plugin.getConfigManager().setGhostParticleEnabled(false);
        plugin.getGhostParticleManager().reload();
        
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.disabled"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.reload_hint"));
    }
    
    /**
     * 处理重新加载命令
     */
    private void handleReload(CommandSender sender) {
        plugin.getGhostParticleManager().reload();
        
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.reloaded"));
    }
    
    /**
     * 处理设置粒子类型命令
     */
    private void handleSetType(CommandSender sender, String type) {
        try {
            // 验证粒子类型是否有效
            Particle particle = Particle.valueOf(type.toUpperCase());
            plugin.getConfigManager().setGhostParticleType(type.toUpperCase());
            
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.settype_success", type.toUpperCase()));
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.reload_hint"));
            
        } catch (IllegalArgumentException e) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.settype_invalid"));
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.settype_hint"));
        }
    }
    
    /**
     * 处理设置粒子数量命令
     */
    private void handleSetCount(CommandSender sender, String countStr) {
        try {
            int count = Integer.parseInt(countStr);
            
            if (count < 1 || count > 20) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.setcount_invalid"));
                return;
            }
            
            plugin.getConfigManager().setGhostParticleCount(count);
            
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.setcount_success", count));
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.reload_hint"));
            
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.gp_not_number"));
        }
    }
    
    /**
     * 处理设置生成间隔命令
     */
    private void handleSetInterval(CommandSender sender, String intervalStr) {
        try {
            int interval = Integer.parseInt(intervalStr);
            
            if (interval < 1 || interval > 100) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.setinterval_invalid"));
                return;
            }
            
            plugin.getConfigManager().setGhostParticleInterval(interval);
            
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.setinterval_success", interval, interval / 20.0));
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.reload_hint"));
            
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.gp_not_number"));
        }
    }
    
    /**
     * 处理设置母体鬼颜色命令
     */
    private void handleSetMotherColor(CommandSender sender, String colorStr) {
        if (isValidColorFormat(colorStr)) {
            plugin.getConfigManager().setGhostParticleMotherColor(colorStr);
            
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.setmothercolor_success", colorStr));
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.reload_hint"));
        } else {
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.setmothercolor_invalid"));
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.setmothercolor_hint"));
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.setmothercolor_range"));
        }
    }
    
    /**
     * 处理设置普通鬼颜色命令
     */
    private void handleSetNormalColor(CommandSender sender, String colorStr) {
        if (isValidColorFormat(colorStr)) {
            plugin.getConfigManager().setGhostParticleNormalColor(colorStr);
            
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.setnormalcolor_success", colorStr));
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.reload_hint"));
        } else {
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.setnormalcolor_invalid"));
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.setnormalcolor_hint"));
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.setnormalcolor_range"));
        }
    }
    
    /**
     * 检查颜色格式是否有效
     */
    private boolean isValidColorFormat(String colorStr) {
        String[] rgb = colorStr.split(",");
        if (rgb.length != 3) return false;
        
        try {
            int r = Integer.parseInt(rgb[0].trim());
            int g = Integer.parseInt(rgb[1].trim());
            int b = Integer.parseInt(rgb[2].trim());
            
            return r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 处理设置粒子大小命令
     */
    private void handleSetSize(CommandSender sender, String sizeStr) {
        try {
            double size = Double.parseDouble(sizeStr);
            
            if (size < 0.1 || size > 5.0) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.setsize_invalid"));
                return;
            }
            
            plugin.getConfigManager().setGhostParticleSize(size);
            
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.setsize_success", size));
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.reload_hint"));
            
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.gp_not_number"));
        }
    }
    
    /**
     * 处理设置准备阶段显示命令
     */
    private void handleSetPreparation(CommandSender sender, String value) {
        boolean enable = value.equalsIgnoreCase("on") || value.equalsIgnoreCase("true") || value.equals("1");
        
        plugin.getConfigManager().setGhostParticleShowInPreparation(enable);
        
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.setpreparation_success", enable ? "开启" : "关闭"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.reload_hint"));
    }
    
    /**
     * 处理测试命令
     */
    private void handleTest(CommandSender sender) {
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.test_success"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.test_hint"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.test_step1"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.test_step2"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.test_step3"));
    }
    
    /**
     * 处理列出粒子类型命令
     */
    private void handleListTypes(CommandSender sender) {
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.listtypes_header"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.listtypes_title"));
        sender.sendMessage("");
        
        // 常用粒子类型
        String[] commonTypes = {
            "REDSTONE", "FLAME", "SOUL_FIRE_FLAME", "DRAGON_BREATH", "PORTAL",
            "DUST_COLOR_TRANSITION", "SPELL_MOB", "SPELL_WITCH", "ENCHANTMENT_TABLE", "CRIT_MAGIC",
            "FIREWORKS_SPARK", "HEART", "NOTE", "VILLAGER_ANGRY", "VILLAGER_HAPPY",
            "TOTEM_OF_UNDYING", "COMPOSTER", "SQUID_INK", "DRIPPING_OBSIDIAN_TEAR",
            "FALLING_OBSIDIAN_TEAR", "LANDING_OBSIDIAN_TEAR"
        };
        
        for (int i = 0; i < commonTypes.length; i++) {
            if (i % 3 == 0) {
                sender.sendMessage("");
            }
            sender.sendMessage(ChatColor.YELLOW + "• " + ChatColor.GREEN + commonTypes[i] + ChatColor.GRAY + 
                (i % 3 == 2 ? "" : "   "));
        }
        
        sender.sendMessage("");
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.listtypes_recommend", "REDSTONE"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.listtypes_flame", "FLAME, SOUL_FIRE_FLAME"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.listtypes_magic", "SPELL_MOB, SPELL_WITCH"));
        sender.sendMessage(plugin.getLanguageManager().getMessage("ghost_particle_command.usage_header"));
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // 主命令补全
            completions.addAll(Arrays.asList(
                "status", "enable", "disable", "reload", 
                "settype", "setcount", "setinterval", 
                "setmothercolor", "setnormalcolor", "setsize",
                "setpreparation", "test", "listtypes"
            ));
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            
            switch (subCommand) {
                case "settype":
                    // 常用粒子类型补全
                    completions.addAll(Arrays.asList(
                        "REDSTONE", "FLAME", "SOUL_FIRE_FLAME", "DRAGON_BREATH", 
                        "PORTAL", "DUST", "SPELL_MOB", "SPELL_WITCH"
                    ));
                    break;
                    
                case "setcount":
                    completions.addAll(Arrays.asList("1", "3", "5", "8", "10", "15", "20"));
                    break;
                    
                case "setinterval":
                    completions.addAll(Arrays.asList("5", "10", "15", "20", "30", "40", "60"));
                    break;
                    
                case "setmothercolor":
                    completions.addAll(Arrays.asList("255,0,0", "255,100,100", "200,0,0", "255,50,50"));
                    break;
                    
                case "setnormalcolor":
                    completions.addAll(Arrays.asList("0,255,0", "100,255,100", "0,200,0", "50,255,50"));
                    break;
                    
                case "setsize":
                    completions.addAll(Arrays.asList("0.5", "1.0", "1.5", "2.0", "2.5", "3.0"));
                    break;
                    
                case "setpreparation":
                    completions.addAll(Arrays.asList("on", "off", "true", "false"));
                    break;
            }
        }
        
        // 过滤匹配的补全
        String currentArg = args[args.length - 1].toLowerCase();
        completions.removeIf(s -> !s.toLowerCase().startsWith(currentArg));
        
        return completions;
    }
}