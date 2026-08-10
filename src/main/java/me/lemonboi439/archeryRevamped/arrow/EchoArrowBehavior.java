package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import me.lemonboi439.archeryRevamped.config.ConfigManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Applies darkness and seeds nearby sculk when its hit kills an entity. */
public final class EchoArrowBehavior implements ArrowBehavior {
    @Override
    public void onTick(ArcheryArrowEntity arrow) {
    }

    @Override
    public void onBlockHit(ArcheryArrowEntity arrow, BlockHitResult hit) {
        EffectManager.spawnParticles(arrow.level(), hit.getLocation(), ParticleTypes.SCULK_SOUL, 8);
        EffectManager.playSound(arrow.level(), hit.getLocation(),
                SoundEvents.SCULK_CLICKING, 0.65F, 0.7F);
    }

    @Override
    public void onEntityHit(ArcheryArrowEntity arrow, EntityHitResult hit) {
        Entity target = hit.getEntity();
        if (target instanceof LivingEntity living && !arrow.level().isClientSide()) {
            living.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0));
            trySonicBoom((ServerLevel) arrow.level(), arrow, hit.getLocation(), living);
            if (!living.isAlive()) {
                spreadSculk((ServerLevel) arrow.level(), hit.getLocation(), living, arrow.getOwner());
            }
        }
        EffectManager.spawnParticles(arrow.level(), hit.getLocation(), ParticleTypes.SCULK_SOUL, 12);
        EffectManager.playSound(arrow.level(), hit.getLocation(),
                SoundEvents.SCULK_SHRIEKER_SHRIEK, 0.7F, 1.25F);
    }

    /** An occasional Warden-style sonic beam layered on top of the normal arrow hit. */
    private static void trySonicBoom(ServerLevel world, ArcheryArrowEntity arrow, Vec3 impact,
                                     LivingEntity target) {
        if (world.getRandom().nextDouble() * 100.0D >= ConfigManager.getEchoSonicBoomChancePercent()) {
            return;
        }

        Vec3 start = arrow.getOwner() instanceof LivingEntity owner ? owner.getEyePosition() : impact;
        Vec3 end = target.getEyePosition();
        Vec3 delta = end.subtract(start);
        int particleSteps = Math.max(1, (int) Math.ceil(delta.length() * 2.0D));
        for (int step = 0; step <= particleSteps; step++) {
            Vec3 position = start.lerp(end, step / (double) particleSteps);
            world.sendParticles(ParticleTypes.SONIC_BOOM,
                    position.x, position.y, position.z, 1,
                    0.0D, 0.0D, 0.0D, 0.0D);
        }

        target.hurtServer(world, world.damageSources().sonicBoom(arrow),
                (float) ConfigManager.getEchoSonicBoomDamage());
        EffectManager.playSound(world, start, SoundEvents.WARDEN_SONIC_CHARGE, 1.2F, 1.0F);
        EffectManager.playSound(world, end, SoundEvents.WARDEN_SONIC_BOOM, 1.2F, 1.0F);
    }

    /**
     * Uses the target's vanilla experience value as the spread budget, mirroring
     * the value a Sculk Catalyst reacts to when an entity dies nearby.
     */
    private static void spreadSculk(ServerLevel serverWorld, Vec3 impact, LivingEntity target, Entity killer) {
        BlockPos center = BlockPos.containing(impact);
        int experience = Math.max(1, target.getExperienceReward(serverWorld, killer));
        int spreadBudget = Math.clamp(experience, 6, 64);
        int radius = Math.clamp(2 + (int) Math.ceil(Math.sqrt(experience) / 1.5D), 3, 10);
        Set<BlockPos> converted = new HashSet<>();

        // The original short disk was identical for every mob. Draw positions
        // randomly from an XP-scaled catalyst-like budget instead, so larger
        // creatures that award more experience leave a broader sculk patch.
        for (int attempt = 0; attempt < spreadBudget * 5 && converted.size() < spreadBudget; attempt++) {
            int x = serverWorld.getRandom().nextIntBetweenInclusive(-radius, radius);
            int z = serverWorld.getRandom().nextIntBetweenInclusive(-radius, radius);
            if (x * x + z * z > radius * radius) {
                continue;
            }

            BlockPos position = findInfectableGround(serverWorld, center.offset(x, 0, z));
            if (position != null && converted.add(position)) {
                // Converts an existing solid surface only—never air, grass, or flowers.
                serverWorld.setBlockAndUpdate(position, Blocks.SCULK.defaultBlockState());
                maybePlaceVein(serverWorld, position);
            }
        }

        maybePlaceRareSculkStructure(serverWorld, converted);
        EffectManager.spawnParticles(serverWorld, impact, ParticleTypes.SCULK_CHARGE_POP, 16);
    }

    /** Sparse veins sit on the top face of some converted blocks. */
    private static void maybePlaceVein(ServerLevel world, BlockPos sculk) {
        if (world.getRandom().nextInt(8) != 0) {
            return;
        }
        BlockPos above = sculk.above();
        if (!world.getBlockState(above).isAir()) {
            return;
        }
        BlockState vein = ((MultifaceBlock) Blocks.SCULK_VEIN).getStateForPlacement(
                Blocks.SCULK_VEIN.defaultBlockState(), world, above, Direction.DOWN
        );
        if (vein != null) {
            world.setBlockAndUpdate(above, vein);
        }
    }

    /** One percent per lethal Echo Arrow: a real, functional sculk block. */
    private static void maybePlaceRareSculkStructure(ServerLevel world, Set<BlockPos> converted) {
        if (converted.isEmpty() || world.getRandom().nextInt(100) != 0) {
            return;
        }

        List<BlockPos> positions = new ArrayList<>(converted);
        BlockPos above = positions.get(world.getRandom().nextInt(positions.size())).above();
        if (!world.getBlockState(above).isAir()) {
            return;
        }

        BlockState structure = switch (world.getRandom().nextInt(3)) {
            case 0 -> Blocks.SCULK_SHRIEKER.defaultBlockState().setValue(SculkShriekerBlock.CAN_SUMMON, true);
            case 1 -> Blocks.SCULK_SENSOR.defaultBlockState();
            default -> Blocks.SCULK_CATALYST.defaultBlockState();
        };
        world.setBlockAndUpdate(above, structure);
    }

    private static BlockPos findInfectableGround(ServerLevel world, BlockPos start) {
        // An entity can die several blocks above its footing. Search down to the nearby
        // terrain, but only ever replace a real solid block already in the world.
        for (int yOffset = 0; yOffset <= 5; yOffset++) {
            BlockPos position = start.below(yOffset);
            BlockState state = world.getBlockState(position);
            if (state.is(Blocks.SCULK)) {
                return null;
            }
            if (!state.isAir() && !state.canBeReplaced() && state.isRedstoneConductor(world, position)) {
                return position;
            }
        }
        return null;
    }
}
