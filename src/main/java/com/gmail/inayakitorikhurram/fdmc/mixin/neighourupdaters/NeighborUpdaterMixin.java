package com.gmail.inayakitorikhurram.fdmc.mixin.neighourupdaters;


import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.FDMCMath;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.block.NeighborUpdater;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NeighborUpdater.class)
public interface NeighborUpdaterMixin {

    @Inject(method = "updateNeighbors", at = @At("TAIL"))
    default void updateNeighborsEnd(BlockPos pos, Block sourceBlock, Direction except, CallbackInfo ci) {

        for(int dw = -1; dw <= 1; dw+= 2) {
            this.updateNeighbor(pos.add(FDMCMath.getOffset(dw)), sourceBlock, pos);
        }
    }


    @Shadow
    void updateNeighbor(BlockPos var1, Block var2, BlockPos var3);


}


