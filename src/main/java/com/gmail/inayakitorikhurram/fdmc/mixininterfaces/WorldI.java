package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.Direction4;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public interface WorldI{
    boolean isEmittingRedstonePower(BlockPos add, Direction4 dir);
    int getEmittedRedstonePower(BlockPos pos, Direction4 dir);
}
