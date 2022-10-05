package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.Direction4;
import com.gmail.inayakitorikhurram.fdmc.mixin.WorldMixin;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public interface AbstractBlockStateI {

    BlockState getStateForNeighborUpdate(Direction4 direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos);

    int getWeakRedstonePower(BlockView world, BlockPos pos, Direction4 dir);
}
