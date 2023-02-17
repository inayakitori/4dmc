package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.item.ItemPlacementContext4;
import com.gmail.inayakitorikhurram.fdmc.util.MixinUtil;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WallMountedBlock.class)
public class WallMountedBlockMixin {

//    @Redirect(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemPlacementContext;getPlacementDirections()[Lnet/minecraft/util/math/Direction;"))
//    public Direction[] getPlacementState4Directions(ItemPlacementContext ctx){
//        return MixinUtil.;
//    }



    //for some reasone need this???
    @ModifyVariable(
            method = "getPlacementState",
            at = @At(
                    value = "HEAD"
            ),
            argsOnly = true)
    private ItemPlacementContext modifyPlacementContext(ItemPlacementContext ctx){
        //FDMCConstants.LOGGER.info("placement directions: {}, {}, {}, {}, {}, {}, {}, {}, {}", (Object[]) ctx.getPlacementDirections());
        ctx = new ItemPlacementContext4(ctx);
        //FDMCConstants.LOGGER.info("final placement direction: {}, {}, {}, {}, {}, {}, {}, {}, {}", (Object[]) ctx.getPlacementDirections());
        return ctx;
    }

    @Redirect(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemPlacementContext;getPlayerFacing()Lnet/minecraft/util/math/Direction;"))
    public Direction getPlacementState4Facing(ItemPlacementContext ctx){
        return MixinUtil.modifyPlacementDirection(ctx, ctx::getPlayerFacing);
    }

    @Inject(
            method = "canPlaceAt(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private static void allow4Placement(WorldView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir){
        BlockPos neighborPos = pos.offset(direction);
        if(MixinUtil.areWSidesSolidFullSquares(world, neighborPos)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
        //else normal placement check
    }

}
