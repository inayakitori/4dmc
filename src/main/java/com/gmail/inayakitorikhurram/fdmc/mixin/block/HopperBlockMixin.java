package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4;
import com.gmail.inayakitorikhurram.fdmc.FDMCProperties;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.OptionalDirection4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.BlockI;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;


@Mixin(HopperBlock.class)
public abstract class HopperBlockMixin {
    @Shadow @Final private static VoxelShape DEFAULT_SHAPE;

    @Shadow @Final private static VoxelShape OUTSIDE_SHAPE;

    @Shadow @Final private static VoxelShape EAST_RAYCAST_SHAPE;


    @Shadow @Final public static DirectionProperty FACING;

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    public void getOutlineShape4(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        Direction dir = state.get(FACING);
        if (dir.getAxis() == Direction4Constants.Axis4.W) {
            cir.setReturnValue(DEFAULT_SHAPE);
            cir.cancel();
        }
    }


    //TODO doesn't work? regardless of what cir.setReturnValue(----) is
    @Inject(method = "getRaycastShape", at = @At("HEAD"), cancellable = true)
    public void getRaycastShape4(BlockState state, BlockView world, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        if(state.get(FACING).getAxis() == Direction4Constants.Axis4.W) {
            cir.setReturnValue(Hopper.INSIDE_SHAPE);
            cir.cancel();
        }
    }

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    public void getPlacementState4(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir){
        OptionalDirection4  optionalDirection4 = ((CanStep)ctx.getPlayer()).getPlacementDirection4();
        //if should place ana/kata do that
        optionalDirection4.ifPresent(direction4 -> {
            BlockState state = cir.getReturnValue();
            cir.setReturnValue(state
                        .with(HopperBlock.FACING, direction4.toDirection())//allows for horizontal interaction e.g furnace n stuff
            );
        });
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
