package com.gmail.inayakitorikhurram.fdmc.mixin.neighourupdaters;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.block.NeighborUpdater;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.block.ChainRestrictedNeighborUpdater$SixWayEntry")
public class SixWayEntryMixin {

    private static final Vec3i[] updateOrder4 = new Vec3i[]{
            new Vec3i(-FDMCConstants.STEP_DISTANCE, 0, 0),
            new Vec3i( FDMCConstants.STEP_DISTANCE, 0, 0)
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
            return;
            /** in base method:
             BlockPos blockPos = this.pos.offset(NeighborUpdater.UPDATE_ORDER[this.currentDirectionIndex++]);
             BlockState blockState = world.getBlockState(blockPos);
             blockState.neighborUpdate(world, blockPos, this.sourceBlock, this.pos, false);
             if (this.currentDirectionIndex < NeighborUpdater.UPDATE_ORDER.length && NeighborUpdater.UPDATE_ORDER[this.currentDirectionIndex] == this.except) {
             ++this.currentDirectionIndex;
             }
             return currentDirectionIndex < 8; <--from onUpdateEnd
             */
        }
        //now all 3dirs are done, update kata/ana direction

        BlockPos blockPos = this.pos.add(updateOrder4[currentDirectionIndex - 6]);
        currentDirectionIndex++;
        BlockState blockState = world.getBlockState(blockPos);
        blockState.neighborUpdate(world, blockPos, this.sourceBlock, this.pos, false);

        onUpdateEnd(world, cir);
        cir.cancel();// if completed all 3dirs, don't try to update them again
    }


    @Inject(method = "update", at =@At("RETURN"), cancellable = true)
    public void onUpdateEnd(World world, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(currentDirectionIndex < 8);
    }



    @Mixin(targets = "net.minecraft.world.block.ChainRestrictedNeighborUpdater$Entry")
    interface EntryMixin {
        @Shadow
        boolean update(World var1);
    }

}

