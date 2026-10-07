package io.Sriptirc_wp_1258.gost.managers;

import org.bukkit.Particle;

import java.lang.reflect.Field;

/**
 * 粒子兼容工具类（Spigot / Paper / Purpur 全兼容）
 * 纯反射按候选名解析，不在编译期引用新枚举常量，
 * 同一字节码可在 1.20.x ~ 1.21.x 各服务端运行。
 */
public final class ParticleCompat {

    private ParticleCompat() {}

    /** 运行时按候选名解析 Particle（valueOf + 字段反射） */
    private static Particle resolve(String... names) {
        for (String name : names) {
            if (name == null || name.isEmpty()) continue;
            try {
                return Particle.valueOf(name);
            } catch (Throwable ignored) {
            }
            try {
                Field f = Particle.class.getField(name);
                Object v = f.get(null);
                if (v instanceof Particle) {
                    return (Particle) v;
                }
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    /** 全版本都存在的粒子作最终兜底 */
    private static Particle any() {
        Particle p = resolve("CLOUD", "SPELL_MOB", "HEART", "NOTE", "FLAME");
        return p != null ? p : Particle.CLOUD;
    }

    private static Particle orAny(Particle p) {
        return p != null ? p : any();
    }

    /** 附魔粒子：ENCHANT / ENCHANTMENT_TABLE / SPELL_MOB */
    public static Particle enchant() {
        return orAny(resolve("ENCHANT", "ENCHANTMENT_TABLE", "SPELL_MOB"));
    }

    /** 烟花粒子：FIREWORK / FIREWORKS_SPARK */
    public static Particle firework() {
        return orAny(resolve("FIREWORK", "FIREWORKS_SPARK"));
    }

    /** 附魔暴击：ENCHANTED_HIT / CRIT_MAGIC */
    public static Particle enchantedHit() {
        return orAny(resolve("ENCHANTED_HIT", "CRIT_MAGIC"));
    }

    /** 爆炸：EXPLOSION / EXPLOSION_LARGE */
    public static Particle explosion() {
        return orAny(resolve("EXPLOSION", "EXPLOSION_LARGE"));
    }

    /** 大爆炸：EXPLOSION_EMITTER / EXPLOSION_HUGE */
    public static Particle explosionEmitter() {
        return orAny(resolve("EXPLOSION_EMITTER", "EXPLOSION_HUGE"));
    }

    /** 图腾：TOTEM / TOTEM_OF_UNDYING */
    public static Particle totem() {
        return orAny(resolve("TOTEM", "TOTEM_OF_UNDYING"));
    }

    /** 红石粉尘：DUST / REDSTONE */
    public static Particle dust() {
        return orAny(resolve("DUST", "REDSTONE"));
    }

    /** 灵魂火焰 */
    public static Particle soulFireFlame() {
        return orAny(resolve("SOUL_FIRE_FLAME", "SOUL"));
    }

    /** 末影气息 */
    public static Particle dragonBreath() {
        return orAny(resolve("DRAGON_BREATH"));
    }

    /** 伤害指示 */
    public static Particle damageIndicator() {
        return orAny(resolve("DAMAGE_INDICATOR"));
    }

    /** 音符 */
    public static Particle note() {
        return orAny(resolve("NOTE"));
    }

    /** 心心 */
    public static Particle heart() {
        return orAny(resolve("HEART"));
    }

    /** 云 */
    public static Particle cloud() {
        return orAny(resolve("CLOUD"));
    }

    /** 电火花 */
    public static Particle electricSpark() {
        return orAny(resolve("ELECTRIC_SPARK"));
    }

    /** 冲击波（低版本可能不存在，返回任意粒子保证不空） */
    public static Particle sonicBoom() {
        Particle p = resolve("SONIC_BOOM");
        return p != null ? p : any();
    }

    /** 按配置名解析（兼容 REDSTONE / DUST 等旧名） */
    public static Particle byName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return dust();
        }
        String key = name.trim().toUpperCase().replace(' ', '_');
        Particle p = resolve(key);
        if (p != null) return p;
        // 常见旧名映射
        switch (key) {
            case "REDSTONE":
                return dust();
            case "ENCHANTMENT_TABLE":
                return enchant();
            case "FIREWORKS_SPARK":
                return firework();
            case "CRIT_MAGIC":
                return enchantedHit();
            case "EXPLOSION_LARGE":
                return explosion();
            case "EXPLOSION_HUGE":
                return explosionEmitter();
            case "TOTEM_OF_UNDYING":
                return totem();
            default:
                return dust();
        }
    }
}
