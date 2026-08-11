package me.lemonboi439.archeryRevamped.item;

import me.lemonboi439.archeryRevamped.arrow.ArrowReplacement;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

/** Fires a real Archery Revamped projectile while leaving launch velocity to vanilla dispensers. */
final class SpecialArrowDispenserBehavior extends ProjectileDispenserBehavior {
    @Override
    protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
        return ArrowReplacement.createForDispenser(world, stack);
    }
}
