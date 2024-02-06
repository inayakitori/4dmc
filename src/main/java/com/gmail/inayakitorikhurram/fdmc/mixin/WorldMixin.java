package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

@Mixin(net.minecraft.world.World.class)
public abstract class WorldMixin implements WorldAccess {
    //use HORIZONTAL4
    @Inject(method = {"updateComparators"}, at = @At(value = "HEAD"), cancellable = true)
    private void fdmc$redirectToHorizontal4(BlockPos pos, Block block, CallbackInfo ci) {
        Iterator<Direction> var3 = Direction4Constants.Type4.HORIZONTAL4.stream().iterator();

        while (var3.hasNext()) {
            Direction direction = var3.next();
            BlockPos blockPos = pos.offset(direction);
            if (this.isChunkLoaded(blockPos)) {
                BlockState blockState = this.getBlockState(blockPos);
                if (blockState.isOf(Blocks.COMPARATOR)) {
                    ((World) (Object) this).updateNeighbor(blockState, blockPos, block, pos, false);
                } else if (blockState.isSolidBlock(this, blockPos)) {
                    blockPos = blockPos.offset(direction);
                    blockState = this.getBlockState(blockPos);
                    if (blockState.isOf(Blocks.COMPARATOR)) {
                        ((World) (Object) this).updateNeighbor(blockState, blockPos, block, pos, false);
                    }
                }
            }
        }
    }
}
