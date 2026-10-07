package io.Sriptirc_wp_1258.gost.managers;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 多语言管理器（v2.3.3）
 *
 * <p>支持语言文件：zh_CN / en_US / ja_JP / es_ES / fr_FR / de_DE</p>
 * <p>文件位置：plugins/Gost/lang/&lt;locale&gt;.yml —— 用户可自行修改，/gost reload 生效。</p>
 * <p>config.yml -> language.default 选择语言。</p>
 */
public class LanguageManager {

    public static final String[] SUPPORTED = {"zh_CN", "en_US", "ja_JP", "es_ES", "fr_FR", "de_DE"};

    private final Gost plugin;
    private final Map<String, String> messages = new HashMap<>();
    private String currentLanguage = "zh_CN";

    public LanguageManager(Gost plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    /** 当前语言代码 */
    public String getCurrentLanguage() {
        return currentLanguage;
    }

    /** 是否英文（兼容旧调用） */
    public boolean isEnglish() {
        return "en_US".equalsIgnoreCase(currentLanguage);
    }

    /** 是否中文 */
    public boolean isChinese() {
        return currentLanguage.toLowerCase(Locale.ROOT).startsWith("zh");
    }

    /**
     * 加载语言：优先 plugins/Gost/lang/&lt;locale&gt;.yml，缺失则从 jar 内置资源释放并加载。
     */
    private void loadMessages() {
        messages.clear();
        currentLanguage = normalize(plugin.getConfigManager().getDefaultLanguage());
        ensureLanguageFiles();

        File file = new File(plugin.getDataFolder(), "lang" + File.separator + currentLanguage + ".yml");
        int loaded = 0;
        if (file.isFile()) {
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            for (String key : yml.getKeys(true)) {
                if (!yml.isConfigurationSection(key)) {
                    String val = yml.getString(key);
                    if (val != null) {
                        messages.put(key, val);
                        loaded++;
                    }
                }
            }
        }

        // 缺失键从内置默认补齐（zh_CN / en_US）
        if (loaded < 20 || !messages.containsKey("game.starting")) {
            loadBuiltinFallback();
            loaded = messages.size();
        }

        plugin.getLogger().info("语言: " + currentLanguage + "，已加载 " + loaded + " 条消息"
            + (file.isFile() ? " (来自 " + file.getName() + ")" : " (内置)"));
    }

    /** 确保 lang 目录下有全部语言文件（从 jar 复制，不覆盖用户已改文件） */
    private void ensureLanguageFiles() {
        File dir = new File(plugin.getDataFolder(), "lang");
        if (!dir.exists() && !dir.mkdirs()) {
            plugin.getLogger().warning("无法创建语言目录: " + dir.getPath());
            return;
        }
        for (String loc : SUPPORTED) {
            File out = new File(dir, loc + ".yml");
            if (out.isFile()) continue;
            try (InputStream in = plugin.getResource("lang/" + loc + ".yml")) {
                if (in != null) {
                    java.nio.file.Files.copy(in, out.toPath());
                    plugin.getLogger().info("已释放语言文件: lang/" + loc + ".yml");
                }
            } catch (Exception e) {
                plugin.getLogger().warning("释放语言文件失败 " + loc + ": " + e.getMessage());
            }
        }
    }

    private String normalize(String lang) {
        if (lang == null || lang.trim().isEmpty()) return "zh_CN";
        String l = lang.trim().replace('-', '_');
        for (String s : SUPPORTED) {
            if (s.equalsIgnoreCase(l)) return s;
        }
        // 常见别名
        if (l.toLowerCase(Locale.ROOT).startsWith("zh")) return "zh_CN";
        if (l.toLowerCase(Locale.ROOT).startsWith("en")) return "en_US";
        if (l.toLowerCase(Locale.ROOT).startsWith("ja")) return "ja_JP";
        if (l.toLowerCase(Locale.ROOT).startsWith("es")) return "es_ES";
        if (l.toLowerCase(Locale.ROOT).startsWith("fr")) return "fr_FR";
        if (l.toLowerCase(Locale.ROOT).startsWith("de")) return "de_DE";
        return "zh_CN";
    }

    /** 内置兜底（jar 内 zh_CN/en_US） */
    private void loadBuiltinFallback() {
        String[] candidates = {"lang/" + currentLanguage + ".yml", "lang/zh_CN.yml", "lang/en_US.yml"};
        for (String path : candidates) {
            try (InputStream in = plugin.getResource(path)) {
                if (in == null) continue;
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
                for (String key : yml.getKeys(true)) {
                    if (!yml.isConfigurationSection(key)) {
                        String val = yml.getString(key);
                        if (val != null) {
                            messages.putIfAbsent(key, val);
                        }
                    }
                }
                return;
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 获取消息
     */
    public String getMessage(String key) {
        String message = messages.get(key);
        if (message != null) {
            return message;
        }
        plugin.getLogger().warning("找不到语言消息: " + key);
        if (key.startsWith("game.")) return isEnglish() ? "§eGame Tip" : "§e游戏提示";
        if (key.startsWith("role.")) return isEnglish() ? "§cRole Status" : "§c角色状态";
        if (key.startsWith("item.")) return isEnglish() ? "§aItem Effect" : "§a道具效果";
        if (key.startsWith("broadcast.")) return isEnglish() ? "§6Broadcast" : "§6广播消息";
        if (key.startsWith("queue.")) return isEnglish() ? "§bQueue" : "§b队列信息";
        if (key.startsWith("economy.")) return isEnglish() ? "§eEconomy" : "§e经济提示";
        if (key.startsWith("error.")) return isEnglish() ? "§cFailed" : "§c操作失败";
        if (key.startsWith("area.")) return isEnglish() ? "§aArea" : "§a区域信息";
        return isEnglish() ? "§7System" : "§7系统提示";
    }

    public String getMessage(String key, Object... args) {
        String message = getMessage(key);
        try {
            return MessageFormat.format(message, args);
        } catch (Exception e) {
            plugin.getLogger().warning("格式化消息失败: " + key + " - " + e.getMessage());
            return message;
        }
    }

    public void sendMessage(Player player, String key, Object... args) {
        player.sendMessage(getMessage(key, args));
    }

    public void sendTitle(Player player, String titleKey, String subtitleKey,
                          Object[] titleArgs, Object[] subtitleArgs) {
        String title = titleKey != null ? getMessage(titleKey, titleArgs) : "";
        String subtitle = subtitleKey != null ? getMessage(subtitleKey, subtitleArgs) : "";
        player.sendTitle(title, subtitle, 10, 70, 20);
    }

    public void sendTitle(Player player, String titleKey, String subtitleKey) {
        sendTitle(player, titleKey, subtitleKey, new Object[0], new Object[0]);
    }

    public void sendActionBar(Player player, String key, Object... args) {
        player.sendMessage(getMessage(key, args));
    }

    public void loadLanguage() {
        loadMessages();
    }

    /** /gost reload 或改配置后重载 */
    public void reload() {
        loadMessages();
    }

    public void save() {
        // 语言文件由用户编辑，无需回写
    }

    /** 列出可用语言（供命令提示） */
    public String listSupported() {
        return String.join(", ", SUPPORTED);
    }
}
