package me.lemonboi439.archeryRevamped.fracture;

import me.lemonboi439.archeryRevamped.ammo.ArrowAmmoManager;
import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class FractureScheduler {
    private static final List<PendingSplit> PENDING_SPLITS = new ArrayList<>();

    private FractureScheduler() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(FractureScheduler::tick);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> PENDING_SPLITS.clear());
    }

    public static void schedule(ArcheryArrowEntity parent) {
        if (parent.isRemoved() || parent.isArrowInGround()
                || !(parent.level() instanceof ServerLevel serverWorld)) {
            parent.setFractureScheduled(false);
            return;
        }

        int childCount = Math.max(2, Math.min(256, parent.getFractureLevel() + 1));
        double angleDegrees = ConfigManager.getFractureSplitAngleDegrees();
        if (childCount > 240) {
            angleDegrees = Math.min(angleDegrees, 5.0D);
        } else if (childCount > 120) {
            angleDegrees = Math.min(angleDegrees, 10.0D);
        }
        // Keep the split asynchronous, but make the child arrows appear
        // almost immediately after the speed-scaled split timer expires.
        int delay = 1 + serverWorld.getRandom().nextInt(2);
        PENDING_SPLITS.add(new PendingSplit(parent, delay, childCount, angleDegrees));
    }

    private static void tick(MinecraftServer server) {
        Iterator<PendingSplit> iterator = PENDING_SPLITS.iterator();
        while (iterator.hasNext()) {
            PendingSplit pending = iterator.next();
            pending.ticksRemaining--;
            if (pending.ticksRemaining > 0) {
                continue;
            }

            ArcheryArrowEntity parent = pending.parent;
            if (!parent.isRemoved() && !parent.isArrowInGround()
                    && parent.level() instanceof ServerLevel serverWorld) {
                if (parent.getOwner() instanceof ServerPlayer shooter
                        && !parent.isExtraAmmoFree()
                        && !ArrowAmmoManager.consumeExtraArrows(
                        shooter, parent.getPickupItemStackOrigin(), pending.childCount - 1)) {
                    // Keep the original arrow alive and paid for. A split is
                    // cancelled rather than producing uncharged children. It
                    // is marked complete so it cannot retry every tick.
                    parent.setHasSpread(true);
                    parent.setFractureScheduled(false);
                    iterator.remove();
                    continue;
                }

                // The parent is the original paid-for arrow. A successful
                // fracture replaces it with exactly childCount arrows.
                Vec3 splitPosition = new Vec3(parent.getX(), parent.getY(), parent.getZ());
                EffectManager.spawnParticles(serverWorld, splitPosition, ParticleTypes.PORTAL, 24);
                EffectManager.playSound(serverWorld, splitPosition,
                        SoundEvents.CHICKEN_EGG, 0.45F, 1.35F);
                parent.setHasSpread(true);
                parent.setFractureScheduled(false);
                parent.discard();
                for (int index = 0; index < pending.childCount; index++) {
                    double spread = pending.childCount == 1 ? 0.0D
                            : (index / (double) (pending.childCount - 1) * 2.0D - 1.0D);
                    spawnChild(serverWorld, parent, spread * pending.angleDegrees);
                }
            }
            iterator.remove();
        }
    }

    private static void spawnChild(ServerLevel world, ArcheryArrowEntity parent, double angleDegrees) {
        world.addFreshEntity(parent.createFractureChild(angleDegrees));
    }

    private static final class PendingSplit {
        private final ArcheryArrowEntity parent;
        private int ticksRemaining;
        private final int childCount;
        private final double angleDegrees;

        private PendingSplit(ArcheryArrowEntity parent, int ticksRemaining,
                             int childCount, double angleDegrees) {
            this.parent = parent;
            this.ticksRemaining = ticksRemaining;
            this.childCount = childCount;
            this.angleDegrees = angleDegrees;
        }
    }
}
