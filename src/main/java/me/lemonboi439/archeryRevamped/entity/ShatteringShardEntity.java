package me.lemonboi439.archeryRevamped.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

/** A short-lived individual shard created by a Shattering Arrow. */
public final class ShatteringShardEntity extends PersistentProjectileEntity {
    public ShatteringShardEntity(World world) {
        super(ModEntities.SHATTERING_SHARD, world);
        this.setDamage(1.5D);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    public ShatteringShardEntity(EntityType<? extends ShatteringShardEntity> type, World world) {
        super(type, world);
        this.setDamage(1.5D);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(Items.AMETHYST_SHARD);
    }

    @Override
    protected void onBlockHit(net.minecraft.util.hit.BlockHitResult hit) {
        this.discard();
    }

    @Override
    protected void onEntityHit(net.minecraft.util.hit.EntityHitResult hit) {
        super.onEntityHit(hit);
        this.discard();
    }
}
