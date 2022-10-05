package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.Direction4;
import net.minecraft.util.math.BlockPos;

public interface WorldViewI {
    int getStrongRedstonePower(BlockPos pos, Direction4 dir);
}
