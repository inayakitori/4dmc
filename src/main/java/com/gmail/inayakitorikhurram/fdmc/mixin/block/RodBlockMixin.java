package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.util.MixinUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.RodBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RodBlock.class)
public abstract class RodBlockMixin {
    @Shadow @Final protected static VoxelShape Z_SHAPE;
    private static final VoxelShape W_SHAPE = MixinUtil.constructWFacingVoxelShapes(Z_SHAPE, Direction.NORTH).getLeft();

    @Inject(method = "getOutlineShape", at = @At("RETURN"), cancellable = true)
    private void getOutlineShapeW(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (state.get(FacingBlock.FACING).getAxis() == Direction4Constants.Axis4Constants.W) {
            cir.setReturnValue(W_SHAPE);
            cir.cancel();
        }
    }
}
