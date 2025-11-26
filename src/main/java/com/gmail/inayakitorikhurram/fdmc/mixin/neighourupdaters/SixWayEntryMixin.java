package com.gmail.inayakitorikhurram.fdmc.mixin.neighourupdaters;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.block.NeighborUpdater;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.block.ChainRestrictedNeighborUpdater$SixWayEntry")
public class SixWayEntryMixin implements EntryMixin {

    private static final Vec3i[] updateOrder4 = new Vec3i[]{
            new Vec3i(FDMCMath.getOffsetX(-1), 0, 0),
            new Vec3i(FDMCMath.getOffsetX(+1), 0, 0)
    };

    @Shadow @Final private BlockPos pos;
    @Shadow @Final private Block sourceBlock;
    @Nullable
    @Shadow @Final private Direction except;
    @Shadow        private int currentDirectionIndex;

    //TODO use neighbour updater instead
    @Inject(method = "update", at =@At("HEAD"), cancellable = true)
    public void onUpdateStart(World world, CallbackInfoReturnable<Boolean> cir) {
        if(currentDirectionIndex < 6){
            //FDMCConstants.LOGGER.info("updating 3D source {} to {} currentDirection {}", sourceBlock, pos, currentDirectionIndex);
            return;
            /* in base method:
            Direction direction = NeighborUpdater.UPDATE_ORDER[this.currentDirectionIndex++];
            BlockPos blockPos = this.pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            WireOrientation wireOrientation = null;
            if (world.getEnabledFeatures().contains(FeatureFlags.REDSTONE_EXPERIMENTS)) {
                if (this.orientation == null) {
                    this.orientation = OrientationHelper.getEmissionOrientation(world, this.except == null ? null : this.except.getOpposite(), null);
                }
                wireOrientation = this.orientation.withFront(direction);
            }
            NeighborUpdater.tryNeighborUpdate(world, blockState, blockPos, this.sourceBlock, wireOrientation, false);
            if (this.currentDirectionIndex < NeighborUpdater.UPDATE_ORDER.length && NeighborUpdater.UPDATE_ORDER[this.currentDirectionIndex] == this.except) {
                ++this.currentDirectionIndex;
            }
            return this.currentDirectionIndex < NeighborUpdater.UPDATE_ORDER.length;
             */
        }
        //now all 3dirs are done, update kata/ana direction

        //FDMCConstants.LOGGER.info("updating 4D source {} to {} currentDirection {}", sourceBlock, pos, currentDirectionIndex);
        BlockPos blockPos = this.pos.add(updateOrder4[currentDirectionIndex - 6]);
        currentDirectionIndex++;
        BlockState blockState = world.getBlockState(blockPos);
        NeighborUpdater.tryNeighborUpdate(world, blockState, blockPos, this.sourceBlock,null, false);
        onUpdateEnd(world, cir);
        cir.cancel();// if completed all 3dirs, don't try to update them again
    }


    @Inject(method = "update", at =@At("RETURN"), cancellable = true)
    public void onUpdateEnd(World world, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(currentDirectionIndex < 8);
    }


}


@Mixin(targets = "net.minecraft.world.block.ChainRestrictedNeighborUpdater$Entry")
interface EntryMixin {
}