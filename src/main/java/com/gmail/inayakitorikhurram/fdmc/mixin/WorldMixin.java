package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.WorldAccessI;
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
public abstract class WorldMixin implements WorldAccess, AutoCloseable, WorldAccessI {

    //TODO rn this only affects blocks that don't override their checking-redstone functions, so would need to modify each of those individually

    @Inject(method = "getReceivedRedstonePower", at = @At("RETURN"), cancellable = true)
    public void getReceivedRedstonePower(BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if(cir.getReturnValueI() == 15) return;//don't care if already maxed out

        int currentMax = cir.getReturnValue();

        for(int dw = -1; dw <= 1; dw = dw+2){
            int valFromDirection = this.getEmittedRedstonePower(pos.add(dw * FDMCConstants.STEP_DISTANCE, 0, 0), dw);
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

    //TODO looks at weak power in X, but should instead either have
    // own directions or just max/min of all sides
    private int getEmittedRedstonePower(BlockPos pos, int dir) {
        Direction.AxisDirection axDir = dir == 1 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
        BlockState blockState = this.getBlockState(pos);
        int i = blockState.getWeakRedstonePower(this, pos, Direction.get(axDir, Direction.Axis.X));
        return blockState.isSolidBlock(this, pos) ? Math.max(i, this.getReceivedStrongRedstonePower(pos)) : i;
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
