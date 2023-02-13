package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Enum;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public interface AbstractBlockStateI {

    BlockState getStateForNeighborUpdate(Direction4Enum direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos);

    int getWeakRedstonePower(BlockView world, BlockPos pos, Direction4Enum dir);

    int getStrongRedstonePower(BlockView world, BlockPos pos, Direction4Enum dir);
}
