package me.lemonboi439.archeryRevamped.headshot;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/** Head-region detection and cosmetic feedback for Headshot arrows. */
public final class HeadshotManager {
    private HeadshotManager() {
    }

    public static double prepareHeadshot(ArcheryArrowEntity arrow, EntityHitResult hit) {
        if (!ConfigManager.isHeadshotEnabled() || arrow.getHeadshotLevel() <= 0
                || !(hit.getEntity() instanceof LivingEntity target)) {
            return 1.0D;
        }

        double radius = ConfigManager.getHeadshotBoxRadius();
        double eyeY = target.getEyeY();
        AABB headBox = new AABB(
                target.getX() - radius, eyeY - radius, target.getZ() - radius,
                target.getX() + radius, eyeY + radius, target.getZ() + radius
        );
        // HitResult.getPos() is the exact collision point along the ray in
        // Yarn 1.21.11; this is deliberately not the entity's center/position.
        if (!headBox.contains(hit.getLocation())) {
            return 1.0D;
        }

        boolean pvp = target instanceof Player;
        double multiplier = getDamageMultiplier(arrow.getHeadshotLevel(), pvp);
        if (ConfigManager.isHeadshotFeedbackEnabled()) {
            playFeedback(arrow.level(), hit.getLocation());
        }
        return multiplier;
    }

    private static double getDamageMultiplier(int level, boolean pvp) {
        int effectiveLevel = Math.min(Math.max(level, 1), 3);
        double bonus = switch (effectiveLevel) {
            case 1 -> pvp
                    ? ConfigManager.getHeadshotPvpDamageBonusI()
                    : ConfigManager.getHeadshotDamageBonusI();
            case 2 -> pvp
                    ? ConfigManager.getHeadshotPvpDamageBonusII()
                    : ConfigManager.getHeadshotDamageBonusII();
            default -> pvp
                    ? ConfigManager.getHeadshotPvpDamageBonusIII()
                    : ConfigManager.getHeadshotDamageBonusIII();
        };
        return 1.0D + bonus / 100.0D;
    }

    private static void playFeedback(Level world, Vec3 position) {
        EffectManager.spawnParticles(world, position, ParticleTypes.CRIT, 5);
        EffectManager.playSound(world, position,
                SoundEvents.NOTE_BLOCK_PLING, 0.7F, 1.8F);
    }
}
