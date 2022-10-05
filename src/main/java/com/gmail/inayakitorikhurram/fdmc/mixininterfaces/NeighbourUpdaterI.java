package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.Direction4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.AbstractBlockStateI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public interface NeighbourUpdaterI{

    static void replaceWithStateForNeighborUpdate(WorldAccess world, Direction4 direction4, BlockState neighborState, BlockPos pos, BlockPos neighborPos, int flags, int maxUpdateDepth) {
        BlockState blockState = world.getBlockState(pos);
        BlockState blockState2 = ((AbstractBlockStateI)blockState).getStateForNeighborUpdate(direction4, neighborState, world, pos, neighborPos);
        Block.replace(blockState, blockState2, world, pos, flags, maxUpdateDepth);
    }

    void updateNeighbors(BlockPos pos, Block sourceBlock, Direction4 except);

}
