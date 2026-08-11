package me.lemonboi439.archeryRevamped.mixin;

import me.lemonboi439.archeryRevamped.screen.FletchingTableScreenHandler;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public abstract class FletchingTableBlockMixin {
    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void archeryRevamped$openFletchingTable(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            Hand hand,
            BlockHitResult hit,
            CallbackInfoReturnable<ActionResult> cir
    ) {
        if (!state.isOf(Blocks.FLETCHING_TABLE)) {
            return;
        }

        if (!world.isClient() && player instanceof ServerPlayerEntity serverPlayer) {
            ScreenHandlerContext context = ScreenHandlerContext.create(world, pos);
            serverPlayer.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                    (syncId, playerInventory, ignored) ->
                            new FletchingTableScreenHandler(syncId, playerInventory, context),
                    Text.translatable("container.archery-revamped.fletching_table")
            ));
        }
        cir.setReturnValue(ActionResult.SUCCESS);
    }
}
