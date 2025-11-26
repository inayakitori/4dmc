package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonHeadBlock.class)
public class PistonHeadBlockMixin {


    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private static void setWHeadShapes(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir){
        Direction direction = state.get(FacingBlock.FACING);
        if(direction == Direction4Constants.KATA) {
            cir.setReturnValue(VoxelShapes.fullCube());
        } else if(direction == Direction4Constants.ANA) {
            cir.setReturnValue(VoxelShapes.fullCube());
        } else {
            //allow normal function execution
        }
    }
}
