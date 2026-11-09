package io.Sriptirc_wp_1258.gost.managers;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 版本更新检查（v2.4.0）
 *
 * <p>启动后在异步线程查一次 Modrinth 的版本接口，发现新版本就提示在线管理员。
 * 全程不阻塞主线程；任何异常都静默处理，绝不影响插件启动。</p>
 *
 * <p>可用 {@code config.yml -> update-check.enabled: false} 关闭。</p>
 */
public final class UpdateChecker {

    /** Modrinth 项目 slug（https://modrinth.com/plugin/gost） */
    private static final String PROJECT_SLUG = "gost";

    private static final String API = "https://api.modrinth.com/v2/project/" + PROJECT_SLUG + "/version";

    /** 只取第一个 version_number —— Modrinth 按其发布时间倒序返回 */
    private static final Pattern VERSION_PATTERN =
        Pattern.compile("\"version_number\"\\s*:\\s*\"([^\"]+)\"");

    private static final int CONNECT_TIMEOUT_MS = 8000;
    private static final int READ_TIMEOUT_MS = 8000;

    private UpdateChecker() {
    }

    /** 异步检查一次；结果只写日志并提示在线管理员 */
    public static void checkAsync(Gost plugin) {
        if (plugin == null) return;
        try {
            ServerCompat.runAsync(plugin, () -> {
                try {
                    String latest = fetchLatestVersion();
                    if (latest == null) return;
                    String current = plugin.getDescription().getVersion();
                    if (!isNewer(latest, current)) return;
                    announce(plugin, current, latest);
                } catch (Throwable ignored) {
                    // 版本检查属于增强功能，任何失败都不应影响服务器
                }
            });
        } catch (Throwable ignored) {
        }
    }

    private static String fetchLatestVersion() throws Exception {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(API).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
            conn.setReadTimeout(READ_TIMEOUT_MS);
            // Modrinth 要求带一个可识别的 User-Agent
            conn.setRequestProperty("User-Agent",
                "Gost-UpdateChecker/" + PROJECT_SLUG + " (Minecraft plugin)");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) return null;

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    // 只需要开头的几条版本记录，不必读完整个响应
                    if (sb.length() > 60000) break;
                }
            }

            Matcher m = VERSION_PATTERN.matcher(sb.toString());
            return m.find() ? m.group(1) : null;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    /** 语义化版本比较：2.4.1 > 2.4.0；解析不出来的部分按 0 处理 */
    static boolean isNewer(String candidate, String current) {
        int[] a = parse(candidate);
        int[] b = parse(current);
        int len = Math.max(a.length, b.length);
        for (int i = 0; i < len; i++) {
            int x = i < a.length ? a[i] : 0;
            int y = i < b.length ? b[i] : 0;
            if (x != y) return x > y;
        }
        return false;
    }

    private static int[] parse(String version) {
        if (version == null) return new int[0];
        // 去掉可能的 v 前缀与 -beta 之类的后缀
        String cleaned = version.trim().replaceFirst("^[vV]", "");
        int cut = cleaned.indexOf('-');
        if (cut > 0) cleaned = cleaned.substring(0, cut);
        String[] parts = cleaned.split("\\.");
        int[] out = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                out[i] = Integer.parseInt(parts[i].replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                out[i] = 0;
            }
        }
        return out;
    }

    private static void announce(Gost plugin, String current, String latest) {
        plugin.getLogger().warning("发现新版本: " + latest + "（当前 " + current + "）");
        plugin.getLogger().warning("下载: https://modrinth.com/plugin/" + PROJECT_SLUG);
        try {
            ServerCompat.run(plugin, () -> {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (player.hasPermission("gost.admin")) {
                        plugin.getLanguageManager().sendMessage(player, "update.available", current, latest);
                    }
                }
            });
        } catch (Throwable ignored) {
        }
    }
}
