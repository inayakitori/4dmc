package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.state.property.Property4Owner;
import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.gmail.inayakitorikhurram.fdmc.FDMCConstants.*;
import static net.minecraft.block.HorizontalFacingBlock.FACING;
import static net.minecraft.block.WallMountedBlock.FACE;

@Mixin(ButtonBlock.class)
public class ButtonBlockMixin implements Property4Owner {

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    public void fdmc$getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        Direction direction = state.get(FACING);
        if(direction.getAxis() == Direction4Constants.Axis4Constants.W) {

            cir.setReturnValue(

                    switch (state.get(FACE)){
                        case FLOOR -> FLOOR_W_PRESSED_SHAPE;
                        case WALL -> direction == Direction4Constants.ANA ? ANA_PRESSED_SHAPE : KATA_PRESSED_SHAPE; //should never be anything else since it's on the W axis
                        case CEILING -> CEILING_W_SHAPE;
                    }

            );

            cir.cancel();
        }
    }

}
