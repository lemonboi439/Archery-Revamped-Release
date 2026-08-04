package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import me.lemonboi439.archeryRevamped.entity.ModEntities;
import me.lemonboi439.archeryRevamped.entity.ShatteringShardEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.ThreadLocalRandom;

/** Splits one arrow into individual, damage-dealing amethyst shard projectiles. */
public final class ShatteringArrowBehavior implements ArrowBehavior {
    private static final int SHARD_COUNT = 8;

    @Override
    public void onTick(ArcheryArrowEntity arrow) {
    }

    @Override
    public void onBlockHit(ArcheryArrowEntity arrow, BlockHitResult hit) {
        shatter(arrow, hit.getPos());
        // The stuck projectile remains a normal collectible arrow.
        arrow.setProjectileStack(new ItemStack(Items.ARROW));
    }

    @Override
    public void onEntityHit(ArcheryArrowEntity arrow, EntityHitResult hit) {
        shatter(arrow, hit.getPos());
        arrow.discard();
    }

    private static void shatter(ArcheryArrowEntity arrow, Vec3d impact) {
        if (!(arrow.getEntityWorld() instanceof ServerWorld world)) {
            return;
        }

        EffectManager.spawnParticles(world, impact, ParticleTypes.CRIT, 14);
        EffectManager.playSound(world, impact, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
                0.9F, 1.15F);
        for (int i = 0; i < SHARD_COUNT; i++) {
            ShatteringShardEntity shard = new ShatteringShardEntity(world);
            shard.setOwner(arrow.getOwner());
            shard.setPosition(impact.x, impact.y, impact.z);

            double angle = ThreadLocalRandom.current().nextDouble(0.0D, Math.PI * 2.0D);
            double horizontal = ThreadLocalRandom.current().nextDouble(0.18D, 0.42D);
            Vec3d velocity = new Vec3d(
                    Math.cos(angle) * horizontal,
                    ThreadLocalRandom.current().nextDouble(-0.08D, 0.24D),
                    Math.sin(angle) * horizontal
            );
            shard.setVelocity(velocity);
            world.spawnEntity(shard);
        }
    }
}
