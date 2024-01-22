package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.ShapeContext;
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

import static com.gmail.inayakitorikhurram.fdmc.FDMCConstants.LOGGER;
import static com.gmail.inayakitorikhurram.fdmc.mixin.state.property.PropertiesMixin.FACING;

@Mixin(PistonBlock.class)
public class PistonBlockMixin {
    @Redirect(method = "shouldExtend", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"))
    private Direction[] fdmc$shouldExtendfromAnyDirectionPowered(){
        return Direction4Constants.VALUES;
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void fdmc$getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir){
        if (state.get(FACING).getAxis() != Direction4Constants.Axis4Constants.W) return;
        //TODO new outlines
        cir.setReturnValue(VoxelShapes.fullCube());
    }

    //jumps in right before the getOpposite and ignores it if placing W
    @Redirect(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Direction;getOpposite()Lnet/minecraft/util/math/Direction;"))
    private Direction dontGetOppositeOnWPlacement(Direction dir){
        if(dir.getAxis() == Direction4Constants.Axis4Constants.W) {
            return dir;
        } else {
            return dir.getOpposite();
        }
    }

}
