package me.lemonboi439.archeryRevamped.mixin;

import me.lemonboi439.archeryRevamped.screen.FletchingTableScreenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public abstract class FletchingTableBlockMixin {
    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void archeryRevamped$openFletchingTable(
            BlockState state,
            Level world,
            BlockPos pos,
            Player player,
            BlockHitResult hit,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (!state.is(Blocks.FLETCHING_TABLE)) {
            return;
        }

        if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            ContainerLevelAccess context = ContainerLevelAccess.create(world, pos);
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (syncId, playerInventory, ignored) ->
                            new FletchingTableScreenHandler(syncId, playerInventory, context),
                    Component.translatable("container.archery-revamped.fletching_table")
            ));
        }
        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
