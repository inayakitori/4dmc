package com.gmail.inayakitorikhurram.fdmc.util;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import net.minecraft.util.math.BlockPos;

// just moved some constants out to avoid load order issues
public abstract class UtilConstants {
    public static BlockPos4.BlockPos4Impl ORIGIN4 = (BlockPos4.BlockPos4Impl) BlockPos.ORIGIN;
}
