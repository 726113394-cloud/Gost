package io.Sriptirc_wp_1258.gost.managers;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    
    private final Gost plugin;
    private FileConfiguration langConfig;
    private File langFile;
    private final Map<String, String> messages = new HashMap<>();
    
    public LanguageManager(Gost plugin) {
        this.plugin = plugin;
        this.langFile = new File(plugin.getDataFolder(), "lang.yml");
        loadMessages();
    }
    
    /**
     * 加载语言文件
     */
    private void loadMessages() {
        try {
            // 如果文件不存在，从jar复制默认配置
            if (!langFile.exists()) {
                plugin.saveResource("lang.yml", false);
            }
            
            langConfig = YamlConfiguration.loadConfiguration(langFile);
            messages.clear();
            
            // 加载所有消息
            loadSection("", langConfig);
            
            plugin.getLogger().info("已加载 " + messages.size() + " 条语言消息");
        } catch (Exception e) {
            plugin.getLogger().severe("加载语言文件失败: " + e.getMessage());
            e.printStackTrace();
            loadDefaultMessages();
        }
    }
    
    /**
     * 递归加载配置节
     */
    private void loadSection(String prefix, FileConfiguration config) {
        loadSectionInternal(prefix, config);
    }
    
    /**
     * 递归加载配置节内部实现
     */
    private void loadSectionInternal(String prefix, org.bukkit.configuration.ConfigurationSection config) {
        for (String key : config.getKeys(false)) {
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
            
            if (config.isConfigurationSection(key)) {
                loadSectionInternal(fullKey, config.getConfigurationSection(key));
            } else {
                String value = config.getString(key);
                if (value != null) {
                    messages.put(fullKey, ChatColor.translateAlternateColorCodes('&', value));
                }
            }
        }
    }
    
    /**
     * 加载默认消息（当文件加载失败时）
     * 所有消息现在从 yml 文件加载，此方法保留为空实现作为后备
     */
    private void loadDefaultMessages() {
        // 所有默认消息现在从 messages_zh_CN.yml / messages_en_US.yml 加载
        // 此方法仅作为后备，不应再有硬编码消息
    }
    
    /**
     * 获取消息
     * @param key 消息键
     * @return 格式化后的消息
     */
    public String getMessage(String key) {
        return messages.getOrDefault(key, "&cMissing message: " + key);
    }
    
    /**
     * 获取消息并替换参数
     * @param key 消息键
     * @param args 参数
     * @return 格式化后的消息
     */
    public String getMessage(String key, Object... args) {
        String message = getMessage(key);
        try {
            return MessageFormat.format(message, args);
        } catch (Exception e) {
            plugin.getLogger().warning("格式化消息失败: " + key + " - " + e.getMessage());
            return message;
        }
    }
    
    /**
     * 向玩家发送消息
     * @param player 玩家
     * @param key 消息键
     * @param args 参数
     */
    public void sendMessage(Player player, String key, Object... args) {
        player.sendMessage(getMessage(key, args));
    }
    
    /**
     * 向玩家发送标题消息
     * @param player 玩家
     * @param titleKey 标题消息键
     * @param subtitleKey 副标题消息键
     * @param titleArgs 标题参数
     * @param subtitleArgs 副标题参数
     */
    public void sendTitle(Player player, String titleKey, String subtitleKey, 
                          Object[] titleArgs, Object[] subtitleArgs) {
        String title = titleKey != null ? getMessage(titleKey, titleArgs) : "";
        String subtitle = subtitleKey != null ? getMessage(subtitleKey, subtitleArgs) : "";
        
        player.sendTitle(title, subtitle, 10, 70, 20);
    }
    
    /**
     * 向玩家发送标题消息（简版）
     * @param player 玩家
     * @param titleKey 标题消息键
     * @param subtitleKey 副标题消息键
     */
    public void sendTitle(Player player, String titleKey, String subtitleKey) {
        sendTitle(player, titleKey, subtitleKey, new Object[0], new Object[0]);
    }
    
    /**
     * 向玩家发送行动栏消息
     * @param player 玩家
     * @param key 消息键
     * @param args 参数
     */
    public void sendActionBar(Player player, String key, Object... args) {
        String message = getMessage(key, args);
        plugin.getActionBarManager().sendActionBar(player, message, 100); // 5秒持续时间
    }
    
    /**
     * 向所有在线玩家广播消息
     * @param key 消息键
     * @param args 参数
     */
    public void broadcastMessage(String key, Object... args) {
        String message = getMessage(key, args);
        org.bukkit.Bukkit.broadcastMessage(message);
    }
    
    /**
     * 重新加载语言文件
     */
    public void reload() {
        loadMessages();
    }
    
    /**
     * 保存语言文件（如果需要修改）
     */
    public void save() {
        try {
            langConfig.save(langFile);
        } catch (IOException e) {
            plugin.getLogger().severe("保存语言文件失败: " + e.getMessage());
        }
    }
}