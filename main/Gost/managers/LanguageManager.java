package io.Sriptirc_wp_1258.gost.managers;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多语言管理器（v2.4.0）
 *
 * <p>支持语言文件：zh_CN / en_US / ja_JP / es_ES / fr_FR / de_DE</p>
 * <p>文件位置：plugins/Gost/lang/&lt;locale&gt;.yml —— 用户可自行修改，/gost reload 生效。</p>
 * <p>语言选择：config.yml -> language.default 作为服务器默认语言；
 * 玩家可用 /gost lang &lt;locale&gt; 单独切换（按 UUID 记忆，不落盘）。</p>
 *
 * <p>v2.4.0 起一次性加载全部语言，因此：</p>
 * <ul>
 *   <li>玩家切语言立即生效，不需要重载</li>
 *   <li>缺失的 key 会按「玩家语言 → 服务器默认语言 → key 本身」逐级回退</li>
 * </ul>
 */
public class LanguageManager {

    public static final String[] SUPPORTED = {"zh_CN", "en_US", "ja_JP", "es_ES", "fr_FR", "de_DE"};

    private static final String FALLBACK_LOCALE = "zh_CN";

    private final Gost plugin;

    /** locale → (key → 消息) */
    private final Map<String, Map<String, String>> byLocale = new LinkedHashMap<>();
    /** 玩家单独选择的语言 */
    private final Map<UUID, String> playerLocales = new ConcurrentHashMap<>();

    private String defaultLocale = FALLBACK_LOCALE;

    public LanguageManager(Gost plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    /** 服务器默认语言代码 */
    public String getCurrentLanguage() {
        return defaultLocale;
    }

    /** 是否英文（兼容旧调用；基于服务器默认语言） */
    public boolean isEnglish() {
        return "en_US".equalsIgnoreCase(defaultLocale);
    }

    /** 是否中文（兼容旧调用；基于服务器默认语言） */
    public boolean isChinese() {
        return defaultLocale.toLowerCase(Locale.ROOT).startsWith("zh");
    }

    /** 取某个玩家实际生效的语言（未单独设置则用服务器默认） */
    public String getLanguage(Player player) {
        if (player == null) return defaultLocale;
        return playerLocales.getOrDefault(player.getUniqueId(), defaultLocale);
    }

    /** 取某个玩家实际生效的语言（按 UUID） */
    public String getLanguage(UUID playerId) {
        if (playerId == null) return defaultLocale;
        return playerLocales.getOrDefault(playerId, defaultLocale);
    }

    /**
     * 为玩家单独设置语言。
     * @return 语言代码非法时返回 false
     */
    public boolean setLanguage(Player player, String locale) {
        if (player == null || locale == null) return false;
        String normalized = normalizeOrNull(locale);
        if (normalized == null) return false;
        playerLocales.put(player.getUniqueId(), normalized);
        return true;
    }

    /** 清除玩家单独设置，回到服务器默认语言 */
    public void clearLanguage(Player player) {
        if (player == null) return;
        playerLocales.remove(player.getUniqueId());
    }

    /**
     * 加载全部语言：优先 plugins/Gost/lang/&lt;locale&gt;.yml，
     * 文件缺失的键从 jar 内置资源补齐，并**回写**到用户文件里。
     *
     * <p>回写只做追加：用户已改过的译文、自己加的注释、键顺序全部保留，
     * 只在文件末尾补上这一版新增而文件里还没有的键。这样升级后新增的文案
     * 会出现在用户文件里，可被翻译，而不需要用户删掉文件重新生成。</p>
     */
    private void loadMessages() {
        byLocale.clear();
        defaultLocale = normalize(plugin.getConfigManager().getDefaultLanguage());
        ensureLanguageFiles();

        for (String locale : SUPPORTED) {
            Map<String, String> messages = new HashMap<>();
            File file = new File(plugin.getDataFolder(), "lang" + File.separator + locale + ".yml");

            // 1) 用户文件（保留用户改动）
            if (file.isFile()) {
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                collect(yml, messages);
            }

            // 2) 内置资源
            Map<String, String> builtin = loadBuiltin(locale);

            // 3) 把内置里有、用户文件里没有的键追加回文件（只追加，不重写）
            if (file.isFile() && !builtin.isEmpty()) {
                Map<String, String> missing = new LinkedHashMap<>();
                for (Map.Entry<String, String> e : builtin.entrySet()) {
                    if (!messages.containsKey(e.getKey())) {
                        missing.put(e.getKey(), e.getValue());
                    }
                }
                if (!missing.isEmpty() && appendKeys(file, missing)) {
                    plugin.getLogger().info("语言文件 " + locale + ".yml 已自动补齐 "
                        + missing.size() + " 个新增键");
                }
            }

            // 4) 内置兜底（不覆盖用户已有的值）
            for (Map.Entry<String, String> e : builtin.entrySet()) {
                messages.putIfAbsent(e.getKey(), e.getValue());
            }

            byLocale.put(locale, messages);
        }

        int total = byLocale.getOrDefault(defaultLocale, java.util.Collections.emptyMap()).size();
        plugin.getLogger().info("语言: " + defaultLocale + "，已加载 " + total + " 条消息（共 "
            + byLocale.size() + " 种语言）");
    }

    /** 读取 jar 内置的某种语言 */
    private Map<String, String> loadBuiltin(String locale) {
        Map<String, String> builtin = new HashMap<>();
        try (InputStream in = plugin.getResource("lang/" + locale + ".yml")) {
            if (in != null) {
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
                collect(yml, builtin);
            } else {
                plugin.getLogger().warning("内置语言资源缺失: lang/" + locale + ".yml");
            }
        } catch (Exception e) {
            // 不能静默：内置文件解析失败会导致该语种只剩用户文件里的旧键，
            // 玩家会看到大段 key 或回退到默认语言
            plugin.getLogger().warning("解析内置语言文件失败 lang/" + locale + ".yml: " + e.getMessage());
        }
        return builtin;
    }

    /** 各语言已加载的键数，形如 "zh_CN=598, en_US=66"（自检诊断用） */
    public String describeLocaleCounts() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Map<String, String>> e : byLocale.entrySet()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(e.getKey()).append('=').append(e.getValue().size());
        }
        return sb.toString();
    }

    /**
     * 把缺失的键追加到语言文件末尾。
     * 保持用户原有内容与换行风格，只往末尾追加一段带说明的键块。
     *
     * @return 是否真的写入成功
     */
    private boolean appendKeys(File file, Map<String, String> missing) {
        try {
            String original = new String(java.nio.file.Files.readAllBytes(file.toPath()),
                StandardCharsets.UTF_8);
            String newline = original.contains("\r\n") ? "\r\n" : "\n";

            StringBuilder sb = new StringBuilder(original);
            if (!original.endsWith("\n") && !original.endsWith("\r\n")) {
                sb.append(newline);
            }
            sb.append(newline)
              .append("# ---- 以下 ").append(missing.size())
              .append(" 个键由 Gost ").append(plugin.getDescription().getVersion())
              .append(" 自动补齐（可自行翻译/修改）----").append(newline);
            for (Map.Entry<String, String> e : missing.entrySet()) {
                sb.append(e.getKey()).append(": \"").append(escapeYaml(e.getValue())).append("\"").append(newline);
            }

            java.nio.file.Files.write(file.toPath(), sb.toString().getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("补齐语言文件失败 " + file.getName() + ": " + e.getMessage());
            return false;
        }
    }

    /** 双引号 YAML 字符串转义 */
    private String escapeYaml(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void collect(YamlConfiguration yml, Map<String, String> into) {
        for (String key : yml.getKeys(true)) {
            if (yml.isConfigurationSection(key)) continue;
            String val = yml.getString(key);
            if (val == null) continue;
            into.put(key, val);
        }
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
        String normalized = normalizeOrNull(lang);
        return normalized != null ? normalized : FALLBACK_LOCALE;
    }

    /** 归一化语言代码；无法识别返回 null */
    private String normalizeOrNull(String lang) {
        if (lang == null || lang.trim().isEmpty()) return null;
        String l = lang.trim().replace('-', '_');
        for (String s : SUPPORTED) {
            if (s.equalsIgnoreCase(l)) return s;
        }
        String lower = l.toLowerCase(Locale.ROOT);
        if (lower.startsWith("zh")) return "zh_CN";
        if (lower.startsWith("en")) return "en_US";
        if (lower.startsWith("ja")) return "ja_JP";
        if (lower.startsWith("es")) return "es_ES";
        if (lower.startsWith("fr")) return "fr_FR";
        if (lower.startsWith("de")) return "de_DE";
        return null;
    }

    // ---------------------------------------------------------------- 取值

    private String resolve(String locale, String key) {
        Map<String, String> messages = byLocale.get(locale);
        if (messages != null) {
            String message = messages.get(key);
            if (message != null) return message;
        }
        // 回退到服务器默认语言
        if (!locale.equals(defaultLocale)) {
            Map<String, String> defaults = byLocale.get(defaultLocale);
            if (defaults != null) {
                String message = defaults.get(key);
                if (message != null) return message;
            }
        }
        // 再回退到内置中文，最后暴露 key 本身，便于发现漏配
        Map<String, String> fallback = byLocale.get(FALLBACK_LOCALE);
        if (fallback != null) {
            String message = fallback.get(key);
            if (message != null) return message;
        }
        plugin.getLogger().warning("找不到语言消息: " + key);
        return key;
    }

    /** 按服务器默认语言取消息 */
    public String getMessage(String key) {
        return resolve(defaultLocale, key);
    }

    public String getMessage(String key, Object... args) {
        return format(resolve(defaultLocale, key), key, args);
    }

    /** 按玩家自己的语言取消息 */
    public String getMessage(Player player, String key) {
        return resolve(getLanguage(player), key);
    }

    public String getMessage(Player player, String key, Object... args) {
        return format(resolve(getLanguage(player), key), key, args);
    }

    private String format(String message, String key, Object... args) {
        if (args == null || args.length == 0) return message;
        try {
            return MessageFormat.format(message, args);
        } catch (Exception e) {
            plugin.getLogger().warning("格式化消息失败: " + key + " - " + e.getMessage());
            return message;
        }
    }

    // ---------------------------------------------------------------- 发送

    public void sendMessage(Player player, String key, Object... args) {
        if (player == null) return;
        player.sendMessage(getMessage(player, key, args));
    }

    public void sendTitle(Player player, String titleKey, String subtitleKey,
                          Object[] titleArgs, Object[] subtitleArgs) {
        if (player == null) return;
        String title = titleKey != null ? getMessage(player, titleKey, titleArgs) : "";
        String subtitle = subtitleKey != null ? getMessage(player, subtitleKey, subtitleArgs) : "";
        player.sendTitle(title, subtitle, 10, 70, 20);
    }

    public void sendTitle(Player player, String titleKey, String subtitleKey) {
        sendTitle(player, titleKey, subtitleKey, new Object[0], new Object[0]);
    }

    public void sendActionBar(Player player, String key, Object... args) {
        ActionBarManager.sendNow(player, getMessage(player, key, args));
    }

    // ---------------------------------------------------------------- 生命周期

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

    /** 玩家退出时清理其语言选择，避免 UUID 表无限增长 */
    public void forget(Player player) {
        if (player != null) playerLocales.remove(player.getUniqueId());
    }

    /** 列出可用语言（供命令提示） */
    public String listSupported() {
        return String.join(", ", SUPPORTED);
    }

    /** 某个语言代码是否受支持 */
    public boolean isSupported(String locale) {
        return normalizeOrNull(locale) != null;
    }

    /** 某语言已加载的消息条数（自检用） */
    public int getMessageCount(String locale) {
        Map<String, String> messages = byLocale.get(locale);
        return messages == null ? 0 : messages.size();
    }

    /**
     * 相对基准语言，各语言键集的最大差异数量（自检用）。
     * 0 表示六语言键集完全一致。
     */
    public int getMaxKeySetDiff(String reference) {
        Map<String, String> ref = byLocale.get(reference);
        if (ref == null || ref.isEmpty()) return -1;
        int max = 0;
        for (Map.Entry<String, Map<String, String>> e : byLocale.entrySet()) {
            if (e.getKey().equals(reference)) continue;
            java.util.Set<String> diff = new java.util.HashSet<>(ref.keySet());
            diff.removeAll(e.getValue().keySet());
            java.util.Set<String> extra = new java.util.HashSet<>(e.getValue().keySet());
            extra.removeAll(ref.keySet());
            max = Math.max(max, diff.size() + extra.size());
        }
        return max;
    }

    /** 归一化语言代码（供命令使用）；非法返回 null */
    public String resolveLocale(String locale) {
        return normalizeOrNull(locale);
    }
}
