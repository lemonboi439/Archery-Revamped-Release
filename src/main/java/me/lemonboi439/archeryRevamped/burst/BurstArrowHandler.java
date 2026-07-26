package me.lemonboi439.archeryRevamped.burst;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
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

        int totalArrows = Math.max(1, Math.min(burstLevel, 3));
        int additionalArrows = totalArrows - 1;
        if (additionalArrows <= 0) {
            return;
        }

        PENDING_BURSTS.add(new PendingBurst(
                firstArrow,
                serverWorld,
                shooter,
                weaponStack,
                new Vec3d(firstArrow.getX(), firstArrow.getY(), firstArrow.getZ()),
                new Vec3d(shooter.getX(), shooter.getY(), shooter.getZ()),
                shooter.getRotationVector(),
                firstArrow.getVelocity(),
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

            pending.staggerTimer++;
            if (pending.staggerTimer < pending.staggerDelayTicks) {
                continue;
            }

            ArcheryArrowEntity child = pending.template.createBurstChild();
            Vec3d velocity = pending.velocity.lengthSquared() > 1.0E-7D
                    ? pending.velocity
                    : pending.lookDirection.multiply(3.0D);
            child.setPosition(pending.arrowPosition.x, pending.arrowPosition.y, pending.arrowPosition.z);
            child.setVelocity(velocity);
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
        private final Vec3d arrowPosition;
        private final Vec3d playerPosition;
        private final Vec3d lookDirection;
        private final Vec3d velocity;
        private final int staggerDelayTicks;
        private int arrowsRemaining;
        private int staggerTimer;

        private PendingBurst(ArcheryArrowEntity template, ServerWorld world,
                             ServerPlayerEntity shooter, ItemStack weaponStack,
                             Vec3d arrowPosition, Vec3d playerPosition,
                             Vec3d lookDirection, Vec3d velocity,
                             int arrowsRemaining, int staggerDelayTicks) {
            this.template = template;
            this.world = world;
            this.shooter = shooter;
            this.weaponStack = weaponStack;
            this.arrowPosition = arrowPosition;
            this.playerPosition = playerPosition;
            this.lookDirection = lookDirection;
            this.velocity = velocity;
            this.arrowsRemaining = arrowsRemaining;
            this.staggerDelayTicks = staggerDelayTicks;
        }
    }
}
