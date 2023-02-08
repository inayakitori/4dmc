package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.FDMCProperties;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4;
import com.gmail.inayakitorikhurram.fdmc.math.OptionalDirection4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.AbstractBlockI;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.AbstractBlockStateI;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.BlockI;
import net.minecraft.block.*;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.FACING4;
import static net.minecraft.block.AbstractRedstoneGateBlock.POWERED;
import static net.minecraft.block.HorizontalFacingBlock.FACING;
import static net.minecraft.block.RepeaterBlock.DELAY;
import static net.minecraft.block.RepeaterBlock.LOCKED;

@Mixin(RepeaterBlock.class)
public abstract class RepeaterBlockMixin extends AbstractRedstoneGateBlockMixin {

    @Shadow public abstract boolean isLocked(WorldView world, BlockPos pos, BlockState state);

    @Shadow public abstract void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random);

    @Redirect(method="<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RepeaterBlock;setDefaultState(Lnet/minecraft/block/BlockState;)V"))
    private void insertDirection4Property(RepeaterBlock instance, BlockState blockState){
        ((BlockI)instance).setDefaultBlockState(
                ((BlockI)instance).getStateManager().getDefaultState()
                        .with(FACING, Direction.NORTH)
                        .with(FACING4, OptionalDirection4.NONE)
                        .with(DELAY, 1)
                        .with(LOCKED, false)
                        .with(POWERED, false)
        );
    }

    //this 4-property doesn't actually do anything unless it's set to kata or ana, the rest are a "None" type which just allows the block to take control
    @Inject(method = "appendProperties", at = @At("TAIL"))
    protected void appendProperties4D(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(FDMCProperties.FACING4);
    }
    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction4 direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        Direction4 fullFacing4 = state.get(FACING4).toDir4(state.get(FACING));
        if (!world.isClient() && direction.getAxis() != fullFacing4.getAxis()) {
            return state.with(LOCKED, this.isLocked(world, pos, state));
        }
        return state;// should == AbstractBlock.getStateForNeightbourUpdate()
    }

    //@Override from base class
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return getStateForNeighborUpdate(state, Direction4.fromDirection3(direction), neighborState, world, pos, neighborPos);
    }

    //@Override from base class. should put the particles in a different location for Direction4
    @Inject(method = "randomDisplayTick", at = @At("HEAD"), cancellable = true)
    public void randomDisplayTick4(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        if (!state.get(POWERED)) {
            ci.cancel();
            return;
        }
        OptionalDirection4 optionalDirection4 = state.get(FACING4);

        optionalDirection4.ifPresent(
            dir4 -> {
                float torchIndexOffset = 0.0f;//bottom torch x pos
                if (random.nextBoolean()) {//change to top torch
                    torchIndexOffset = state.get(DELAY) * 2 + 2;
                }

                double particleX = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
                double particleY = (double)pos.getY() + (1./8.) + (random.nextDouble() - 0.5) * 0.2 + (torchIndexOffset / 16.0);
                double particleZ = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;


                world.addParticle(
                        DustParticleEffect.DEFAULT,
                        particleX,
                        particleY,
                        particleZ,
                        0.0, 0.0, 0.0);
                ci.cancel();
            }
        );
    }

}
