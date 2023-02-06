package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4;
import net.minecraft.util.math.BlockPos;

public interface WorldI{
    boolean isEmittingRedstonePower(BlockPos add, Direction4 dir);
    int getEmittedRedstonePower(BlockPos pos, Direction4 dir);
}
