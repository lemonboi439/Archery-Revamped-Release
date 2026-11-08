package me.lemonboi439.archeryRevamped.arrow;

import me.lemonboi439.archeryRevamped.config.ConfigManager;
import me.lemonboi439.archeryRevamped.effect.EffectManager;
import me.lemonboi439.archeryRevamped.entity.ArcheryArrowEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MultifaceGrowthBlock;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

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
        EffectManager.spawnParticles(arrow.getEntityWorld(), hit.getPos(), ParticleTypes.SCULK_SOUL, 8);
        EffectManager.playSound(arrow.getEntityWorld(), hit.getPos(),
                SoundEvents.BLOCK_SCULK_SENSOR_CLICKING, 0.65F, 0.7F);
    }

    @Override
    public void onEntityHit(ArcheryArrowEntity arrow, EntityHitResult hit) {
        Entity target = hit.getEntity();
        if (target instanceof LivingEntity living && arrow.getEntityWorld() instanceof ServerWorld serverWorld) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 100, 0));
            trySonicBoom(serverWorld, arrow, hit.getPos(), living);
            if (!living.isAlive()) {
                spreadSculk(serverWorld, hit.getPos(), living);
            }
        }
        EffectManager.spawnParticles(arrow.getEntityWorld(), hit.getPos(), ParticleTypes.SCULK_SOUL, 12);
        EffectManager.playSound(arrow.getEntityWorld(), hit.getPos(),
                SoundEvents.BLOCK_SCULK_SHRIEKER_SHRIEK, 0.7F, 1.25F);
    }

    /** An occasional Warden-style sonic beam layered on top of the normal arrow hit. */
    private static void trySonicBoom(ServerWorld world, ArcheryArrowEntity arrow, Vec3d impact,
                                     LivingEntity target) {
        if (world.getRandom().nextDouble() * 100.0D >= ConfigManager.getEchoSonicBoomChancePercent()) {
            return;
        }

        Vec3d start = arrow.getOwner() instanceof LivingEntity owner ? owner.getEyePos() : impact;
        Vec3d end = target.getEyePos();
        Vec3d delta = end.subtract(start);
        int particleSteps = Math.max(1, (int) Math.ceil(delta.length() * 2.0D));
        for (int step = 0; step <= particleSteps; step++) {
            Vec3d position = start.lerp(end, step / (double) particleSteps);
            world.spawnParticles(ParticleTypes.SONIC_BOOM,
                    position.x, position.y, position.z, 1,
                    0.0D, 0.0D, 0.0D, 0.0D);
        }

        target.damage(world.getDamageSources().sonicBoom(arrow),
                (float) ConfigManager.getEchoSonicBoomDamage());
        EffectManager.playSound(world, start, SoundEvents.ENTITY_WARDEN_SONIC_CHARGE, 1.2F, 1.0F);
        EffectManager.playSound(world, end, SoundEvents.ENTITY_WARDEN_SONIC_BOOM, 1.2F, 1.0F);
    }

    /**
     * Uses the target's vanilla experience value as the spread budget, mirroring
     * the value a Sculk Catalyst reacts to when an entity dies nearby.
     */
    private static void spreadSculk(ServerWorld world, Vec3d impact, LivingEntity target) {
        BlockPos center = BlockPos.ofFloored(impact);
        int experience = Math.max(1, target.getXpToDrop());
        int spreadBudget = Math.max(6, Math.min(64, experience));
        int radius = Math.max(3, Math.min(10, 2 + (int) Math.ceil(Math.sqrt(experience) / 1.5D)));
        Set<BlockPos> converted = new HashSet<>();

        // The original short disk was identical for every mob. Draw positions
        // randomly from an XP-scaled catalyst-like budget instead, so larger
        // creatures that award more experience leave a broader sculk patch.
        for (int attempt = 0; attempt < spreadBudget * 5 && converted.size() < spreadBudget; attempt++) {
            int x = world.getRandom().nextBetween(-radius, radius);
            int z = world.getRandom().nextBetween(-radius, radius);
            if (x * x + z * z > radius * radius) {
                continue;
            }

            BlockPos position = findInfectableGround(world, center.add(x, 0, z));
            if (position != null && converted.add(position)) {
                // Converts an existing solid surface only—never air, grass, or flowers.
                world.setBlockState(position, Blocks.SCULK.getDefaultState());
                maybePlaceVein(world, position);
            }
        }

        maybePlaceRareSculkStructure(world, converted);
        EffectManager.spawnParticles(world, impact, ParticleTypes.SCULK_CHARGE_POP, 16);
    }

    /** Sparse veins sit on the top face of some converted blocks. */
    private static void maybePlaceVein(ServerWorld world, BlockPos sculk) {
        if (world.getRandom().nextInt(8) != 0) {
            return;
        }
        BlockPos above = sculk.up();
        if (!world.getBlockState(above).isAir()) {
            return;
        }
        BlockState vein = ((MultifaceGrowthBlock) Blocks.SCULK_VEIN).withDirection(
                Blocks.SCULK_VEIN.getDefaultState(), world, above, Direction.DOWN
        );
        if (vein != null) {
            world.setBlockState(above, vein);
        }
    }

    /** One percent per lethal Echo Arrow: a real, functional sculk block. */
    private static void maybePlaceRareSculkStructure(ServerWorld world, Set<BlockPos> converted) {
        if (converted.isEmpty() || world.getRandom().nextInt(100) != 0) {
            return;
        }

        List<BlockPos> positions = new ArrayList<>(converted);
        BlockPos above = positions.get(world.getRandom().nextInt(positions.size())).up();
        if (!world.getBlockState(above).isAir()) {
            return;
        }

        BlockState structure = switch (world.getRandom().nextInt(3)) {
            case 0 -> Blocks.SCULK_SHRIEKER.getDefaultState().with(SculkShriekerBlock.CAN_SUMMON, true);
            case 1 -> Blocks.SCULK_SENSOR.getDefaultState();
            default -> Blocks.SCULK_CATALYST.getDefaultState();
        };
        world.setBlockState(above, structure);
    }

    private static BlockPos findInfectableGround(ServerWorld world, BlockPos start) {
        // An entity can die several blocks above its footing. Search down to the nearby
        // terrain, but only ever replace a real solid block already in the world.
        for (int yOffset = 0; yOffset <= 5; yOffset++) {
            BlockPos position = start.down(yOffset);
            BlockState state = world.getBlockState(position);
            if (state.isOf(Blocks.SCULK)) {
                return null;
            }
            if (!state.isAir() && !state.isReplaceable() && state.isSolidBlock(world, position)) {
                return position;
            }
        }
        return null;
    }
}
