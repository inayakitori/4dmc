package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.util.MixinUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.Hopper;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(HopperBlock.class)
public abstract class HopperBlockMixin {
    @Shadow @Final private static VoxelShape DEFAULT_SHAPE;

    @Shadow @Final private static VoxelShape OUTSIDE_SHAPE;

    @Shadow @Final private static VoxelShape EAST_RAYCAST_SHAPE;


    @Shadow @Final public static DirectionProperty FACING;

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    public void getOutlineShape4(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        Direction dir = state.get(FACING);
        if (dir.getAxis() == Direction4Constants.Axis4Constants.W) {
            cir.setReturnValue(DEFAULT_SHAPE);
            cir.cancel();
        }
    }


    //TODO doesn't work? regardless of what cir.setReturnValue(----) is
    @Inject(method = "getRaycastShape", at = @At("HEAD"), cancellable = true)
    public void getRaycastShape4(BlockState state, BlockView world, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        if(state.get(FACING).getAxis() == Direction4Constants.Axis4Constants.W) {
            cir.setReturnValue(Hopper.INSIDE_SHAPE);
            cir.cancel();
        }
    }

    @Redirect(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemPlacementContext;getSide()Lnet/minecraft/util/math/Direction;"))
    public Direction getPlacementState4(ItemPlacementContext ctx){
        return MixinUtil.modifyPlacementDirection(ctx, ctx::getSide, Direction::getOpposite);
    }


    //indicator particles which I don't think we need but this code is useful
    /*
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        OptionalDirection4 optionalDirection4 = state.get(FDMCProperties.FACING4);
        optionalDirection4.ifPresent(dir4 -> {
            double dh = 0.1;

            //top
            world.addParticle(new DustParticleEffect(dir4.getColor(), 1.0F), (double)pos.getX() + 0.5, (double)pos.getY() + 0.9, (double)pos.getZ() + 0.5, 0.0, 0.0, 0.0);
            //under
            world.addParticle(new DustParticleEffect(dir4.getColor(), 1.0F), (double)pos.getX() + 0.5 + dh, (double)pos.getY() + 0.05, (double)pos.getZ() + 0.5 + dh, 0.0, 0.0, 0.0);
            world.addParticle(new DustParticleEffect(dir4.getColor(), 1.0F), (double)pos.getX() + 0.5 - dh, (double)pos.getY() + 0.05, (double)pos.getZ() + 0.5 + dh, 0.0, 0.0, 0.0);
            world.addParticle(new DustParticleEffect(dir4.getColor(), 1.0F), (double)pos.getX() + 0.5 + dh, (double)pos.getY() + 0.05, (double)pos.getZ() + 0.5 - dh, 0.0, 0.0, 0.0);
            world.addParticle(new DustParticleEffect(dir4.getColor(), 1.0F), (double)pos.getX() + 0.5 - dh, (double)pos.getY() + 0.05, (double)pos.getZ() + 0.5 - dh, 0.0, 0.0, 0.0);
        });
    }
    */
}
