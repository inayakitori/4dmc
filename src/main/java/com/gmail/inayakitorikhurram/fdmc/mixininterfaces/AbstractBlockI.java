package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.Direction4;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public interface AbstractBlockI {
    BlockState getStateForNeighborUpdate(BlockState state, Direction4 direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos);
}
