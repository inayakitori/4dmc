package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.state.property.Property4Owner;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import net.minecraft.block.*;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.HORIZONTAL_FACING4;
import static net.minecraft.block.AbstractRedstoneGateBlock.POWERED;
import static net.minecraft.block.RepeaterBlock.DELAY;

@Mixin(RepeaterBlock.class)
public abstract class RepeaterBlockMixin extends AbstractRedstoneGateBlockMixin implements Property4Owner {

    

    @Shadow public abstract boolean isLocked(WorldView world, BlockPos pos, BlockState state);

    @Shadow public abstract void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random);

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
