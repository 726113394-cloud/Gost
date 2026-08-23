package io.Sriptirc_wp_1258.gost.managers;

import org.bukkit.Particle;

/**
 * 粒子兼容工具类
 * 解决 1.20.5 前后粒子枚举名差异（Java 枚举编译期内联导致低版本 NoSuchFieldError）
 * 通过 Particle.valueOf() 运行时解析，兼容 1.20.x 与 1.21.x 全版本
 */
public final class ParticleCompat {

    private ParticleCompat() {}

    private static Particle resolve(String modern, String legacy) {
        try {
            return Particle.valueOf(modern);
        } catch (IllegalArgumentException e) {
            return Particle.valueOf(legacy);
        }
    }

    /** EXPLOSION(新) / EXPLOSION_LARGE(旧) */
    public static Particle explosion() { return resolve("EXPLOSION", "EXPLOSION_LARGE"); }

    /** EXPLOSION_EMITTER(新) / EXPLOSION_HUGE(旧) */
    public static Particle explosionEmitter() { return resolve("EXPLOSION_EMITTER", "EXPLOSION_HUGE"); }

    /** TOTEM_OF_UNDYING(新) / TOTEM(旧) */
    public static Particle totem() { return resolve("TOTEM_OF_UNDYING", "TOTEM"); }

    /** DUST(新) / REDSTONE(旧) */
    public static Particle dust() { return resolve("DUST", "REDSTONE"); }

    /** ENCHANT(新) / ENCHANTMENT_TABLE(旧) */
    public static Particle enchant() { return resolve("ENCHANT", "ENCHANTMENT_TABLE"); }

    /** FIREWORK(新) / FIREWORKS_SPARK(旧) */
    public static Particle firework() { return resolve("FIREWORK", "FIREWORKS_SPARK"); }

    /** ENCHANTED_HIT(新) / CRIT_MAGIC(旧) */
    public static Particle enchantedHit() { return resolve("ENCHANTED_HIT", "CRIT_MAGIC"); }
}
