package com.gmail.inayakitorikhurram.fdmc.mixin.neighourupdaters;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
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
    @Shadow        private int currentDirectionIndex = 0;

    int dirIndex4 = 0;

    //TODO use neighbour updater instead
    @Inject(method = "update", at =@At("RETURN"))
    public void onUpdate(World world, CallbackInfoReturnable<Boolean> cir) {
        if(cir.getReturnValueZ()) return;
        //now all 3dirs are done, update kata/ana direction

        BlockPos blockPos = this.pos.add(updateOrder4[dirIndex4++]);
        BlockState blockState = world.getBlockState(blockPos);
        blockState.neighborUpdate(world, blockPos, this.sourceBlock, this.pos, false);

        if(dirIndex4 >= 2){
            cir.setReturnValue(false);
        }
    }




    @Mixin(targets = "net.minecraft.world.block.ChainRestrictedNeighborUpdater$Entry")
    interface EntryMixin {
        @Shadow
        boolean update(World var1);
    }

}

