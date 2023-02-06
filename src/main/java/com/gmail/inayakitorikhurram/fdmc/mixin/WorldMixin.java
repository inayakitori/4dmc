package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.*;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.block.NeighborUpdater;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.World.class)
public abstract class WorldMixin implements WorldAccessI, AutoCloseable, HasNeighbourUpdater, WorldViewI, WorldI {

    @Inject(method = "getReceivedStrongRedstonePower", at = @At("TAIL"), cancellable = true)
    public void getReceivedStrongRedstonePower(BlockPos pos, CallbackInfoReturnable<Integer> cir) {

        int i = cir.getReturnValueI();
        if(i >= 15){
            return;
        }

        i = Math.max(i, getStrongRedstonePower(pos.add(Direction4.KATA.getVec3()), Direction4.KATA));
        if(i >= 15){
            cir.setReturnValue(i);
            return;
        }

        i = Math.max(i, getStrongRedstonePower(pos.add(Direction4.ANA.getVec3()), Direction4.ANA));
        cir.setReturnValue(i);
    }

    @Override
    public int getStrongRedstonePower(BlockPos pos, Direction4 dir) {
        return ((AbstractBlockStateI)getBlockState(pos)).getStrongRedstonePower(this, pos, dir);
    }

    @Inject(method = "isReceivingRedstonePower", at = @At("TAIL"), cancellable = true)
    public void isReceivingRedstonePower(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (getEmittedRedstonePower(pos.add(Direction4.KATA.getVec3()), Direction4.KATA) > 0) {
            cir.setReturnValue(true);
            return;
        }
        cir.setReturnValue(getEmittedRedstonePower(pos.add(Direction4.ANA.getVec3()), Direction4.ANA) > 0);;
    }

    @Inject(method = "getReceivedRedstonePower", at = @At("RETURN"), cancellable = true)
    public void getReceivedRedstonePower(BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if(cir.getReturnValueI() == 15) return;//don't care if already maxed out

        int currentMax = cir.getReturnValue();

        for(Direction4 dir : Direction4.WDIRECTIONS){
            int valFromDirection = this.getEmittedRedstonePower(pos.add(dir.getVec3()), dir);
            if(valFromDirection >= 15){
                cir.setReturnValue(15);
                return;
            }
            if(valFromDirection > currentMax){
                currentMax = valFromDirection;
            }
        }

        cir.setReturnValue(currentMax);
    }

    @Override
    public boolean isEmittingRedstonePower(BlockPos pos, Direction4 dir) {
        return this.getEmittedRedstonePower(pos, dir) > 0;
    }

    @Override
    public int getEmittedRedstonePower(BlockPos pos, Direction4 dir) {
        BlockState blockState = this.getBlockState(pos);
        int i = ((AbstractBlockStateI)blockState).getWeakRedstonePower(this, pos, dir);
        if (blockState.isSolidBlock(this, pos)) {
            return Math.max(i, this.getReceivedStrongRedstonePower(pos));
        }
        return i;
    }

    @Shadow
    public abstract int getReceivedStrongRedstonePower(BlockPos pos);

    @Shadow
    public abstract BlockState getBlockState(BlockPos pos);

    @Shadow @Final protected NeighborUpdater neighborUpdater;

    @Override
    public NeighborUpdater getNeighbourUpdater() {
        return neighborUpdater;
    }
}
