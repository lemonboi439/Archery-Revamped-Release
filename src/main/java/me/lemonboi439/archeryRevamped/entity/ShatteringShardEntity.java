package me.lemonboi439.archeryRevamped.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** A short-lived individual shard created by a Shattering Arrow. */
public final class ShatteringShardEntity extends AbstractArrow {
    public ShatteringShardEntity(Level world) {
        super(ModEntities.SHATTERING_SHARD, world);
        this.setBaseDamage(1.5D);
        this.pickup = Pickup.DISALLOWED;
    }

    public ShatteringShardEntity(EntityType<? extends ShatteringShardEntity> type, Level world) {
        super(type, world);
        this.setBaseDamage(1.5D);
        this.pickup = Pickup.DISALLOWED;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(Items.AMETHYST_SHARD);
    }

    @Override
    protected void onHitBlock(net.minecraft.world.phys.BlockHitResult hit) {
        this.discard();
    }

    @Override
    protected void onHitEntity(net.minecraft.world.phys.EntityHitResult hit) {
        super.onHitEntity(hit);
        this.discard();
    }
}
