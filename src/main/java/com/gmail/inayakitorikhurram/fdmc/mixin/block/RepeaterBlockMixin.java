package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.BlockI;
import net.minecraft.block.*;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.HORIZONTAL_FACING4;
import static net.minecraft.block.AbstractRedstoneGateBlock.POWERED;
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
                        .with(HORIZONTAL_FACING4, Direction.NORTH)
                        .with(DELAY, 1)
                        .with(LOCKED, false)
                        .with(POWERED, false)
        );
    }

    //this 4-property doesn't actually do anything unless it's set to kata or ana, the rest are a "None" type which just allows the block to take control
    @Redirect(method = "appendProperties", at = @At(value = "INVOKE", target = "Lnet/minecraft/state/StateManager$Builder;add([Lnet/minecraft/state/property/Property;)Lnet/minecraft/state/StateManager$Builder;"))
    protected StateManager.Builder<Block, BlockState> appendProperties4D(StateManager.Builder<Block, BlockState> builder, Property<?>[] properties) {
       return builder.add(HORIZONTAL_FACING4, DELAY, LOCKED, POWERED);
    }

    //make **everything** use this property
    @Redirect(method = "*", at=@At(value = "FIELD", target = "Lnet/minecraft/block/RepeaterBlock;FACING:Lnet/minecraft/state/property/DirectionProperty;"))
    private DirectionProperty fdmc$redirectFacingProperty(){
        return HORIZONTAL_FACING4;
    }

    //@Override from base class. should put the particles in a different location for Direction4
    @Inject(method = "randomDisplayTick", at = @At("HEAD"), cancellable = true)
    public void randomDisplayTick4(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        if (!state.get(POWERED)) {
            ci.cancel();
            return;
        }
        Direction dir = state.get(HORIZONTAL_FACING4);

        if(dir.getAxis() == Direction4Constants.Axis4.W){
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
    }

}
