package io.Sriptirc_wp_1258.gost.managers;

import org.bukkit.Sound;

/**
 * 音效兼容工具类
 * 通过 Sound.valueOf() 运行时解析，避免 Java 枚举编译期内联导致的低版本 NoSuchFieldError
 * 兼容 1.20.x 与 1.21.x 全版本音效名称差异
 */
public final class SoundCompat {

    private SoundCompat() {}

    private static Sound resolve(String primary, String... fallbacks) {
        try {
            return Sound.valueOf(primary);
        } catch (IllegalArgumentException e) {
            for (String fb : fallbacks) {
                try {
                    return Sound.valueOf(fb);
                } catch (IllegalArgumentException ignore) {}
            }
        }
        return null;
    }

    /** 传送音效（ENDERMAN/ENDERMEN 版本差异） */
    public static Sound endermanTeleport() {
        Sound s = resolve("ENTITY_ENDERMAN_TELEPORT", "ENTITY_ENDERMEN_TELEPORT");
        return s != null ? s : Sound.ENTITY_ENDERMAN_TELEPORT;
    }

    /** 凋零骷髅死亡（1.21.x 新枚举，1.20.x 无，用凋零死亡替代） */
    public static Sound witherSkeletonDeath() {
        Sound s = resolve("ENTITY_WITHER_SKELETON_DEATH");
        return s != null ? s : Sound.ENTITY_WITHER_DEATH;
    }

    /** 凋零射击 */
    public static Sound witherShoot() {
        Sound s = resolve("ENTITY_WITHER_SHOOT");
        return s != null ? s : Sound.ENTITY_WITHER_DEATH;
    }

    /** 玩家受伤 */
    public static Sound playerHurt() {
        return resolve("ENTITY_PLAYER_HURT", "ENTITY_PLAYER_HURT");
    }

    /** 铁傀儡受伤 */
    public static Sound ironGolemDamage() {
        Sound s = resolve("ENTITY_IRON_GOLEM_DAMAGE");
        return s != null ? s : Sound.ENTITY_IRON_GOLEM_HURT;
    }

    /** 末影龙死亡 */
    public static Sound enderDragonDeath() {
        return resolve("ENTITY_ENDER_DRAGON_DEATH");
    }

    /** 末影龙低吼 */
    public static Sound enderDragonGrowl() {
        return resolve("ENTITY_ENDER_DRAGON_GROWL");
    }

    /** 末影龙振翅 */
    public static Sound enderDragonFlap() {
        return resolve("ENTITY_ENDER_DRAGON_FLAP");
    }

    /** 玩家升级 */
    public static Sound playerLevelup() {
        return resolve("ENTITY_PLAYER_LEVELUP");
    }

    /** 玩家攻击横扫 */
    public static Sound playerAttackSweep() {
        return resolve("ENTITY_PLAYER_ATTACK_SWEEP");
    }

    /** 玩家暴击 */
    public static Sound playerAttackCrit() {
        return resolve("ENTITY_PLAYER_ATTACK_CRIT");
    }

    /** 不死图腾使用 */
    public static Sound totemUse() {
        return resolve("ITEM_TOTEM_USE");
    }

    /** 挑战完成（胜利提示） */
    public static Sound toastChallengeComplete() {
        return resolve("UI_TOAST_CHALLENGE_COMPLETE");
    }

    /** 凋零死亡 */
    public static Sound witherDeath() {
        return resolve("ENTITY_WITHER_DEATH");
    }

    /** 音符块音效（BELL/PLING/HAT） */
    public static Sound noteBell() { return resolve("BLOCK_NOTE_BLOCK_BELL"); }
    public static Sound notePling() { return resolve("BLOCK_NOTE_BLOCK_PLING"); }
    public static Sound noteHat() { return resolve("BLOCK_NOTE_BLOCK_HAT"); }

    /** 物品拾取 */
    public static Sound itemPickup() { return resolve("ENTITY_ITEM_PICKUP"); }

    /** 闪电 */
    public static Sound lightningThunder() { return resolve("ENTITY_LIGHTNING_BOLT_THUNDER"); }

    /** 幻术师施法 */
    public static Sound illusionerCast() { return resolve("ENTITY_ILLUSIONER_CAST_SPELL"); }

    /** 幻术师准备镜像 */
    public static Sound illusionerMirror() { return resolve("ENTITY_ILLUSIONER_PREPARE_MIRROR"); }

    /** 远古守卫者诅咒 */
    public static Sound elderCurse() { return resolve("ENTITY_ELDER_GUARDIAN_CURSE"); }

    /** 玩家泼水 */
    public static Sound playerSplash() { return resolve("ENTITY_PLAYER_SPLASH"); }

    /** 雪球投掷 */
    public static Sound snowballThrow() { return resolve("ENTITY_SNOWBALL_THROW"); }

    /** 吃食物 */
    public static Sound genericEat() { return resolve("ENTITY_GENERIC_EAT"); }

    /** 打嗝 */
    public static Sound playerBurp() { return resolve("ENTITY_PLAYER_BURP"); }

    /** 末影珍珠投掷 */
    public static Sound enderPearlThrow() { return resolve("ENTITY_ENDER_PEARL_THROW"); }
}
