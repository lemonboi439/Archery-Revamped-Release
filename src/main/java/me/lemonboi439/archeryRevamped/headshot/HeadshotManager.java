package me.lemonboi439.archeryRevamped.headshot;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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
        Box headBox = new Box(
                target.getX() - radius, eyeY - radius, target.getZ() - radius,
                target.getX() + radius, eyeY + radius, target.getZ() + radius
        );
        // HitResult.getPos() is the exact collision point along the ray in
        // Yarn 1.21.11; this is deliberately not the entity's center/position.
        if (!headBox.contains(hit.getPos())) {
            return 1.0D;
        }

        boolean pvp = target instanceof PlayerEntity;
        double multiplier = getDamageMultiplier(arrow.getHeadshotLevel(), pvp);
        if (ConfigManager.isHeadshotFeedbackEnabled()) {
            playFeedback(arrow.getEntityWorld(), hit.getPos());
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

    private static void playFeedback(World world, Vec3d position) {
        EffectManager.spawnParticles(world, position, ParticleTypes.CRIT, 5);
        EffectManager.playSound(world, position,
                SoundEvents.BLOCK_NOTE_BLOCK_PLING, 0.7F, 1.8F);
    }
}
