package io.Sriptirc_wp_1258.gost.managers;

import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;

/**
 * 跨服务端 API 兼容层（Spigot / Paper / Purpur / 其他 Bukkit 系）
 *
 * <p>不同服务端/版本对下列 API 的字段名不一致：</p>
 * <ul>
 *   <li>Attribute.MAX_HEALTH / GENERIC_MAX_HEALTH</li>
 *   <li>Attribute.MOVEMENT_SPEED / GENERIC_MOVEMENT_SPEED</li>
 *   <li>PotionEffectType.SLOWNESS / SLOW</li>
 *   <li>PotionEffectType.MINING_FATIGUE / SLOW_DIGGING</li>
 *   <li>Enchantment.UNBREAKING / DURABILITY</li>
 * </ul>
 *
 * <p>本类通过反射按候选字段名解析，同一套字节码可在各常见服务端运行，
 * 避免编译期内联枚举常量导致的 NoSuchFieldError。</p>
 */
public final class ApiCompat {

    private ApiCompat() {}

    // ==================== 反射解析 ====================

    private static Object resolveStatic(Class<?> type, String... names) {
        for (String name : names) {
            try {
                Field f = type.getField(name);
                Object v = f.get(null);
                if (v != null && type.isInstance(v)) {
                    return v;
                }
            } catch (Exception ignored) {
            }
        }
        // 枚举 valueOf 兜底
        for (String name : names) {
            try {
                @SuppressWarnings({"unchecked", "rawtypes"})
                Object v = Enum.valueOf((Class<? extends Enum>) type, name);
                return v;
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    // ==================== Attribute ====================

    /** 最大生命属性（跨版本） */
    public static Attribute maxHealth() {
        return (Attribute) resolveStatic(Attribute.class, "GENERIC_MAX_HEALTH", "MAX_HEALTH");
    }

    /** 移动速度属性（跨版本） */
    public static Attribute movementSpeed() {
        return (Attribute) resolveStatic(Attribute.class, "GENERIC_MOVEMENT_SPEED", "MOVEMENT_SPEED");
    }

    /** 安全读取玩家最大生命 */
    public static double getMaxHealthValue(Player player, double fallback) {
        try {
            Attribute attr = maxHealth();
            if (attr != null && player.getAttribute(attr) != null) {
                return player.getAttribute(attr).getValue();
            }
        } catch (Throwable ignored) {
        }
        return fallback;
    }

    /** 安全设置玩家最大生命 */
    public static boolean setMaxHealthValue(Player player, double value) {
        try {
            Attribute attr = maxHealth();
            if (attr != null && player.getAttribute(attr) != null) {
                player.getAttribute(attr).setBaseValue(value);
                return true;
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    /** 安全设置玩家移动速度基准值 */
    public static boolean setMovementSpeedBase(Player player, double value) {
        try {
            Attribute attr = movementSpeed();
            if (attr != null && player.getAttribute(attr) != null) {
                player.getAttribute(attr).setBaseValue(value);
                return true;
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    /** 读取玩家属性实例（可为 null） */
    public static org.bukkit.attribute.AttributeInstance getAttributeInstance(Player player, Attribute attr) {
        if (attr == null) return null;
        try {
            return player.getAttribute(attr);
        } catch (Throwable ignored) {
            return null;
        }
    }

    // ==================== PotionEffectType ====================

    private static PotionEffectType potion(String... names) {
        PotionEffectType t = (PotionEffectType) resolveStatic(PotionEffectType.class, names);
        return t;
    }

    /** 缓慢（SLOWNESS / SLOW） */
    public static PotionEffectType slowness() {
        PotionEffectType t = potion("SLOW", "SLOWNESS");
        return t != null ? t : PotionEffectType.SLOW;
    }

    /** 挖掘疲劳（MINING_FATIGUE / SLOW_DIGGING） */
    public static PotionEffectType miningFatigue() {
        PotionEffectType t = potion("SLOW_DIGGING", "MINING_FATIGUE");
        return t != null ? t : PotionEffectType.SLOW_DIGGING;
    }

    /** 速度 */
    public static PotionEffectType speed() {
        PotionEffectType t = potion("SPEED");
        return t != null ? t : PotionEffectType.SPEED;
    }

    /** 发光 */
    public static PotionEffectType glowing() {
        PotionEffectType t = potion("GLOWING");
        return t != null ? t : PotionEffectType.GLOWING;
    }

    /** 隐身 */
    public static PotionEffectType invisibility() {
        PotionEffectType t = potion("INVISIBILITY");
        return t != null ? t : PotionEffectType.INVISIBILITY;
    }

    /** 漂浮 */
    public static PotionEffectType levitation() {
        PotionEffectType t = potion("LEVITATION");
        return t != null ? t : PotionEffectType.LEVITATION;
    }

    /** 失明 */
    public static PotionEffectType blindness() {
        return potion("BLINDNESS", "DARKNESS");
    }

    /** 黑暗（1.19+），低版本回退失明 */
    public static PotionEffectType darkness() {
        PotionEffectType t = potion("DARKNESS");
        return t != null ? t : blindness();
    }

    /** 夜视 */
    public static PotionEffectType nightVision() {
        return potion("NIGHT_VISION");
    }

    // ==================== Enchantment ====================

    /** 耐久（UNBREAKING / DURABILITY） */
    public static Enchantment unbreaking() {
        Enchantment e = (Enchantment) resolveStatic(Enchantment.class, "UNBREAKING", "DURABILITY");
        return e != null ? e : Enchantment.DURABILITY;
    }

    /** 诱饵/饵钓（用于伪附魔光效） */
    public static Enchantment lure() {
        Enchantment e = (Enchantment) resolveStatic(Enchantment.class, "LURE");
        return e != null ? e : Enchantment.LURE;
    }

    /** 给物品添加伪附魔光效（隐藏附魔标记） */
    public static void addGlow(ItemStack item) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        try {
            meta.addEnchant(lure(), 1, true);
        } catch (Throwable ignored) {
            try {
                meta.addEnchant(unbreaking(), 1, true);
            } catch (Throwable ignored2) {
            }
        }
        try {
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        } catch (Throwable ignored) {
        }
        item.setItemMeta(meta);
    }

    // ==================== 便捷方法 ====================

    /** 给玩家添加药水效果（自动解析类型） */
    public static boolean addEffect(Player player, PotionEffectType type, int durationTicks, int amplifier) {
        if (player == null || type == null) return false;
        try {
            return player.addPotionEffect(new org.bukkit.potion.PotionEffect(type, durationTicks, amplifier, true, true));
        } catch (Throwable ignored) {
            return false;
        }
    }

    /** 移除药水效果 */
    public static void removeEffect(Player player, PotionEffectType type) {
        if (player == null || type == null) return;
        try {
            player.removePotionEffect(type);
        } catch (Throwable ignored) {
        }
    }

    /** 服务端名称（日志用） */
    public static String serverBrand() {
        try {
            return org.bukkit.Bukkit.getName() + " " + org.bukkit.Bukkit.getVersion();
        } catch (Throwable t) {
            return "unknown";
        }
    }
}
