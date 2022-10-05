package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.Direction4;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public interface AbstractBlockI {
    default BlockState getStateForNeighborUpdate(BlockState state, Direction4 direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos){
        return state;
    }
    default int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction4 dir){
        return 0;
    }
    default int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction4 dir){
        return 0;
    }
}
