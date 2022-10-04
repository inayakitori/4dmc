package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.Direction4;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public interface AbstractBlockStateMixinI {
     BlockState getStateForNeighborUpdate(BlockState state, Direction4 direction4, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos);
}
