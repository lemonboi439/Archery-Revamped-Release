package me.lemonboi439.archeryRevamped.burst;

import me.lemonboi439.archeryRevamped.ammo.ArrowAmmoManager;
import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.enchantment.BurstEnchantment;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
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
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> PENDING_BURSTS.clear());
    }

    public static void schedule(ArcheryArrowEntity firstArrow, ServerPlayerEntity shooter,
                                ItemStack weaponStack, int burstLevel) {
        if (burstLevel <= 0 || !(firstArrow.getEntityWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        // Burst I fires two arrows total, Burst II fires three, and Burst III
        // fires four. The first arrow is already in the world, so only the
        // enchantment level needs to be scheduled here.
        int arrowsPerLevel = Math.max(1, ConfigManager.getBurstArrowsPerLevel());
        int totalBurstArrows = Math.max(1, burstLevel * arrowsPerLevel);
        int multishotLevel = serverWorld.getRegistryManager()
                .get(RegistryKeys.ENCHANTMENT)
                .getEntry(Enchantments.MULTISHOT)
                .map(entry -> EnchantmentHelper.getLevel(entry, weaponStack))
                .orElse(0);
        // Multishot already creates three base projectiles. Treat the burst
        // level as the total number of shots for each multishot lane so
        // Burst III + Multishot produces 3 x 3 = 9 arrows rather than 12.
        int additionalArrows = multishotLevel > 0
                ? Math.max(0, totalBurstArrows - 1)
                : totalBurstArrows;
        if (additionalArrows <= 0) {
            return;
        }

        int staggerDelay = Math.max(1, ConfigManager.getBurstStaggerDelayTicks());
        firstArrow.setBurstState(additionalArrows, staggerDelay);
        firstArrow.setBurstScheduled(true);
        shooter.getItemCooldownManager().set(weaponStack.getItem(), additionalArrows * staggerDelay + 1);

        PENDING_BURSTS.add(new PendingBurst(
                firstArrow,
                serverWorld,
                shooter,
                weaponStack,
                additionalArrows,
                staggerDelay
        ));
    }

    /** Rebuilds delayed burst work after an arrow has been loaded from disk. */
    public static void scheduleRestored(ArcheryArrowEntity firstArrow) {
        if (firstArrow.isRemoved() || firstArrow.getBurstArrowsRemaining() <= 0
                || firstArrow.isArrowInGround()
                || !(firstArrow.getOwner() instanceof ServerPlayerEntity shooter)
                || !(firstArrow.getEntityWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        firstArrow.setBurstScheduled(true);
        PENDING_BURSTS.add(new PendingBurst(
                firstArrow,
                serverWorld,
                shooter,
                firstArrow.getBurstWeaponStack(),
                firstArrow.getBurstArrowsRemaining(),
                firstArrow.getBurstStaggerDelay(),
                firstArrow.getBurstStaggerTimer()
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
            pending.template.setBurstStaggerTimer(pending.staggerTimer);
            if (pending.staggerTimer < pending.staggerDelayTicks) {
                continue;
            }

            if (!pending.template.isExtraAmmoFree()
                    && !ArrowAmmoManager.consumeExtraArrows(
                    pending.shooter, pending.template.getItemStack(), 1)) {
                // The first arrow was already paid for by vanilla. Do not
                // create any free burst arrows when no extra ammo remains.
                iterator.remove();
                pending.template.setBurstScheduled(false);
                pending.template.setBurstState(0, pending.staggerDelayTicks);
                continue;
            }

            ArcheryArrowEntity child = pending.template.createBurstChild();
            Vec3d direction = pending.shooter.getRotationVector();
            if (direction.lengthSquared() <= 1.0E-7D) {
                direction = pending.releaseVelocity.normalize();
            }
            direction = direction.normalize();
            // Spawn ahead of the eye, rather than inside the shooter. This
            // prevents Burst children from colliding with the shooter's head
            // on their first collision check.
            Vec3d spawnPosition = pending.shooter.getEyePos().add(direction.multiply(0.45D));
            Vec3d velocity = direction.normalize().multiply(pending.releaseVelocity.length());
            child.setPosition(spawnPosition.x, spawnPosition.y, spawnPosition.z);
            child.setVelocity(velocity);
            // setVelocity(Vec3d) does not update the projectile's render
            // rotation. Set both current and previous rotation before the
            // entity is sent to clients so it never appears sideways first.
            ProjectileUtil.setRotationFromVelocity(child, 0.0F);
            child.setAngles(child.getYaw(), child.getPitch());
            pending.world.spawnEntity(child);
            Vec3d effectPosition = new Vec3d(child.getX(), child.getY(), child.getZ());
            EffectManager.spawnParticles(pending.world, effectPosition, ParticleTypes.END_ROD, 6);
            EffectManager.playSound(pending.world, effectPosition,
                    SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, 0.35F, 1.45F);
            pending.weaponStack.damage(1, pending.shooter, EquipmentSlot.MAINHAND);

            pending.arrowsRemaining--;
            pending.template.setBurstState(pending.arrowsRemaining, pending.staggerDelayTicks);
            pending.staggerTimer = 0;
            pending.template.setBurstStaggerTimer(0);
            if (pending.arrowsRemaining <= 0) {
                pending.template.setBurstScheduled(false);
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
            this(template, world, shooter, weaponStack, arrowsRemaining, staggerDelayTicks, 0);
        }

        private PendingBurst(ArcheryArrowEntity template, ServerWorld world,
                             ServerPlayerEntity shooter, ItemStack weaponStack,
                             int arrowsRemaining, int staggerDelayTicks, int staggerTimer) {
            this.template = template;
            this.world = world;
            this.shooter = shooter;
            this.weaponStack = weaponStack;
            this.arrowsRemaining = arrowsRemaining;
            this.staggerDelayTicks = staggerDelayTicks;
            this.staggerTimer = Math.max(0, staggerTimer);
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
