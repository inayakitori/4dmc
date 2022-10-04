package com.gmail.inayakitorikhurram.fdmc.mixin.neighourupdaters;

import com.gmail.inayakitorikhurram.fdmc.Direction4;
import com.gmail.inayakitorikhurram.fdmc.mixin.block.AbstractBlockStateMixinI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public interface NeighbourUpdaterMixinI {

    static void replaceWithStateForNeighborUpdate(WorldAccess world, Direction4 direction4, BlockState neighborState, BlockPos pos, BlockPos neighborPos, int flags, int maxUpdateDepth) {
        BlockState blockState = world.getBlockState(pos);
        BlockState blockState2 = ((AbstractBlockStateMixinI)blockState).getStateForNeighborUpdate(world.getBlockState(), direction4, neighborState, world, pos, neighborPos);
        Block.replace(blockState, blockState2, world, pos, flags, maxUpdateDepth);
    }
}