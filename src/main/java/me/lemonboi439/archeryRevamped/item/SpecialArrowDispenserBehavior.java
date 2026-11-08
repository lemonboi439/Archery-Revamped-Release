package me.lemonboi439.archeryRevamped.item;

import me.lemonboi439.archeryRevamped.arrow.ArrowReplacement;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Fires a real Archery Revamped projectile while leaving launch velocity to vanilla dispensers. */
final class SpecialArrowDispenserBehavior extends AbstractProjectileDispenseBehavior {
    @Override
    protected Projectile getProjectile(Level world, Position position, ItemStack stack) {
        return ArrowReplacement.createForDispenser(world, stack);
    }
}
