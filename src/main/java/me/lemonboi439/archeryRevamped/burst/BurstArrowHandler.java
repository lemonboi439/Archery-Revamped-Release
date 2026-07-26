package me.lemonboi439.archeryRevamped.burst;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.enchantment.BurstEnchantment;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class BurstArrowHandler {
    private static final List<PendingBurst> PENDING_BURSTS = new ArrayList<>();

    private BurstArrowHandler() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(BurstArrowHandler::tick);
    }

    public static void schedule(ArcheryArrowEntity firstArrow, ServerPlayerEntity shooter,
                                ItemStack weaponStack, int burstLevel) {
        if (!(firstArrow.getEntityWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        // Burst I fires two arrows total, Burst II fires three, and Burst III
        // fires four. The first arrow is already in the world, so only the
        // enchantment level needs to be scheduled here.
        int additionalArrows = Math.max(0, Math.min(burstLevel, BurstEnchantment.MAX_LEVEL));
        if (additionalArrows <= 0) {
            return;
        }

        PENDING_BURSTS.add(new PendingBurst(
                firstArrow,
                serverWorld,
                shooter,
                weaponStack,
                additionalArrows,
                Math.max(1, ConfigManager.getBurstStaggerDelayTicks())
        ));
    }

    private static void tick(MinecraftServer server) {
        Iterator<PendingBurst> iterator = PENDING_BURSTS.iterator();
        while (iterator.hasNext()) {
            PendingBurst pending = iterator.next();
            if (!pending.shooter.isAlive() || pending.shooter.getEntityWorld() != pending.world) {
                iterator.remove();
                continue;
            }

            // createArrowEntity runs before vanilla applies the bow's release
            // velocity. Wait until the first arrow has recorded that velocity
            // instead of falling back to a hard-coded full-power shot.
            if (!pending.captureReleaseVelocity()) {
                continue;
            }

            pending.staggerTimer++;
            if (pending.staggerTimer < pending.staggerDelayTicks) {
                continue;
            }

            ArcheryArrowEntity child = pending.template.createBurstChild();
            Vec3d spawnPosition = pending.shooter.getEyePos().subtract(0.0D, 0.1D, 0.0D);
            Vec3d direction = pending.shooter.getRotationVector();
            if (direction.lengthSquared() <= 1.0E-7D) {
                direction = pending.releaseVelocity.normalize();
            }
            Vec3d velocity = direction.normalize().multiply(pending.releaseVelocity.length());
            child.setPosition(spawnPosition.x, spawnPosition.y, spawnPosition.z);
            child.setVelocity(velocity);
            // setVelocity(Vec3d) does not update the projectile's render
            // rotation. Set both current and previous rotation before the
            // entity is sent to clients so it never appears sideways first.
            ProjectileUtil.setRotationFromVelocity(child, 0.0F);
            child.setAngles(child.getYaw(), child.getPitch());
            pending.world.spawnEntity(child);
            pending.weaponStack.damage(1, pending.shooter, EquipmentSlot.MAINHAND);

            pending.arrowsRemaining--;
            pending.staggerTimer = 0;
            if (pending.arrowsRemaining <= 0) {
                iterator.remove();
            }
        }
    }

    private static final class PendingBurst {
        private final ArcheryArrowEntity template;
        private final ServerWorld world;
        private final ServerPlayerEntity shooter;
        private final ItemStack weaponStack;
        private final int staggerDelayTicks;
        private Vec3d releaseVelocity;
        private int arrowsRemaining;
        private int staggerTimer;

        private PendingBurst(ArcheryArrowEntity template, ServerWorld world,
                             ServerPlayerEntity shooter, ItemStack weaponStack,
                             int arrowsRemaining, int staggerDelayTicks) {
            this.template = template;
            this.world = world;
            this.shooter = shooter;
            this.weaponStack = weaponStack;
            this.arrowsRemaining = arrowsRemaining;
            this.staggerDelayTicks = staggerDelayTicks;
        }

        private boolean captureReleaseVelocity() {
            if (this.releaseVelocity == null) {
                Vec3d candidate = this.template.getReleaseVelocity();
                if (candidate == null) {
                    return false;
                }
                if (candidate.lengthSquared() <= 1.0E-7D) {
                    return false;
                }
                this.releaseVelocity = candidate;
            }
            return true;
        }
    }
}
