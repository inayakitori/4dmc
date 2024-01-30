package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import net.minecraft.block.Block;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonHeadBlock.class)
public class PistonHeadBlockMixin {
    @Redirect(method = "getHeadShapes", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"))
    private static Direction[] includeWHeadShapes(){
        return Direction4Constants.VALUES;
    }

    @Inject(method = "getHeadShape", at = @At("HEAD"), cancellable = true)
    private static void setWHeadShapes(Direction direction, boolean shortHead, CallbackInfoReturnable<VoxelShape> cir){
        if(direction == Direction4Constants.KATA) {
            cir.setReturnValue(VoxelShapes.fullCube());
        } else if(direction == Direction4Constants.ANA) {
            cir.setReturnValue(VoxelShapes.fullCube());
        } else {
            //allow normal function execution
        }
    }
}
