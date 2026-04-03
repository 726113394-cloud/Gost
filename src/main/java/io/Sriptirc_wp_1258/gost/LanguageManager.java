package io.Sriptirc_wp_1258.gost;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 语言管理器 - 支持多语言文本
 */
public class LanguageManager {
    
    private final Gost plugin;
    private final Map<String, String> messages = new HashMap<>();
    private String currentLanguage = "zh_CN"; // 默认语言
    
    public LanguageManager(Gost plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 加载语言文件
     */
    public void loadLanguage() {
        messages.clear();
        
        // 从配置获取语言设置
        String configLanguage = plugin.getConfig().getString("language", "zh_CN");
        setLanguage(configLanguage);
        
        plugin.getLogger().info("已加载语言: " + currentLanguage);
    }
    
    /**
     * 设置语言
     * @param language 语言代码 (zh_CN, en_US)
     */
    public void setLanguage(String language) {
        this.currentLanguage = language;
        loadLanguageFile(language);
    }
    
    /**
     * 加载指定语言文件
     */
    private void loadLanguageFile(String language) {
        String fileName = "messages_" + language + ".yml";
        
        // 先尝试从插件JAR中加载默认文件
        loadDefaultLanguageFile(fileName);
        
        // 然后加载用户自定义文件（如果存在）
        loadCustomLanguageFile(fileName);
        
        plugin.getLogger().info("语言文件 " + fileName + " 已加载，共 " + messages.size() + " 条消息");
    }
    
    /**
     * 从插件JAR加载默认语言文件
     */
    private void loadDefaultLanguageFile(String fileName) {
        try (InputStream inputStream = plugin.getResource(fileName)) {
            if (inputStream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8)
                );
                
                for (String key : defaultConfig.getKeys(true)) {
                    if (!defaultConfig.isConfigurationSection(key)) {
                        messages.put(key, defaultConfig.getString(key));
                    }
                }
                
                plugin.getLogger().info("已加载默认语言文件: " + fileName);
            } else {
                plugin.getLogger().warning("默认语言文件不存在: " + fileName);
                // 如果默认文件不存在，尝试加载中文作为后备
                if (!fileName.equals("messages_zh_CN.yml")) {
                    loadDefaultLanguageFile("messages_zh_CN.yml");
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("加载默认语言文件失败: " + fileName);
            e.printStackTrace();
        }
    }
    
    /**
     * 加载用户自定义语言文件
     */
    private void loadCustomLanguageFile(String fileName) {
        File customFile = new File(plugin.getDataFolder(), fileName);
        
        if (customFile.exists()) {
            try {
                YamlConfiguration customConfig = YamlConfiguration.loadConfiguration(customFile);
                
                for (String key : customConfig.getKeys(true)) {
                    if (!customConfig.isConfigurationSection(key)) {
                        messages.put(key, customConfig.getString(key));
                    }
                }
                
                plugin.getLogger().info("已加载自定义语言文件: " + fileName);
            } catch (Exception e) {
                plugin.getLogger().severe("加载自定义语言文件失败: " + fileName);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 获取消息
     * @param key 消息键
     * @return 消息内容
     */
    public String getMessage(String key) {
        return messages.getOrDefault(key, "[" + key + "]");
    }
    
    /**
     * 获取带参数的消息
     * @param key 消息键
     * @param args 参数
     * @return 格式化后的消息
     */
    public String getMessage(String key, Object... args) {
        String message = getMessage(key);
        return String.format(message, args);
    }
    
    /**
     * 获取当前语言
     */
    public String getCurrentLanguage() {
        return currentLanguage;
    }
    
    /**
     * 获取支持的语言列表
     */
    public String[] getAvailableLanguages() {
        return new String[]{"zh_CN", "en_US"};
    }
    
    /**
     * 重新加载语言
     */
    public void reload() {
        loadLanguage();
    }
    
    /**
     * 发送标题消息给玩家
     * @param player 玩家
     * @param titleKey 标题键
     * @param subtitleKey 副标题键
     */
    public void sendTitle(org.bukkit.entity.Player player, String titleKey, String subtitleKey) {
        String title = getMessage(titleKey);
        String subtitle = getMessage(subtitleKey);
        
        if (title != null && !title.isEmpty()) {
            player.sendTitle(title, subtitle, 10, 70, 20);
        }
    }
    
    /**
     * 发送标题消息（带时间参数）
     * @param player 玩家
     * @param titleKey 标题键
     * @param subtitleKey 副标题键
     * @param fadeIn 淡入时间
     * @param stay 持续时间
     * @param fadeOut 淡出时间
     */
    public void sendTitle(org.bukkit.entity.Player player, String titleKey, String subtitleKey, 
                          int fadeIn, int stay, int fadeOut) {
        String title = getMessage(titleKey);
        String subtitle = getMessage(subtitleKey);
        
        if (title != null && !title.isEmpty()) {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }
    
    /**
     * 发送消息给玩家
     * @param player 玩家
     * @param messageKey 消息键
     */
    public void sendMessage(org.bukkit.entity.Player player, String messageKey) {
        String message = getMessage(messageKey);
        if (message != null && !message.isEmpty()) {
            player.sendMessage(message);
        }
    }
    
    /**
     * 发送消息给玩家（带参数）
     * @param player 玩家
     * @param messageKey 消息键
     * @param args 参数
     */
    public void sendMessage(org.bukkit.entity.Player player, String messageKey, Object... args) {
        String message = getMessage(messageKey, args);
        if (message != null && !message.isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }
    
    /**
     * 向所有在线玩家广播消息
     * @param key 消息键
     * @param args 参数
     */
    public void broadcastMessage(String key, Object... args) {
        String message = getMessage(key, args);
        if (message != null && !message.isEmpty()) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }
}