package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class FDMCMath {

    
    public static BlockPos getOffset(int stepDirection){
        return new BlockPos(stepDirection * FDMCConstants.STEP_DISTANCE, 0, 0);
    }

    public static double mod(double a, double b){
        return ((a%b) + b) % b;
    }


    public static int chunkCountInRadius(int radius) {
        return (2*radius + 1) * (4 * radius * radius + 4 * radius + 3) / 3;
    }
}
