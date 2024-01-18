package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import net.minecraft.util.math.BlockPos;

import static com.gmail.inayakitorikhurram.fdmc.FDMCConstants.CHUNK_STEP_DISTANCE_BITS;
import static com.gmail.inayakitorikhurram.fdmc.FDMCConstants.STEP_DISTANCE_BITS;

public class FDMCMath {

    public static int[] splitX3(int x3){
        int centre_offset = 1<<(STEP_DISTANCE_BITS-1);
        int shifted_x3 = x3 + centre_offset;
        int shifted_x4 = shifted_x3 & (1<<(STEP_DISTANCE_BITS)) - 1;
        int x4 = shifted_x4 - centre_offset;
        int w4 = shifted_x3 >> STEP_DISTANCE_BITS;
        return new int[]{x4, w4};
    }

    public static int getX4(int x3){
        int centre_offset = 1<<(STEP_DISTANCE_BITS-1);
        int shifted_x3 = x3 + centre_offset;
        int shifted_x4 = shifted_x3 & (1<<(STEP_DISTANCE_BITS)) - 1;
        return shifted_x4 - centre_offset;
    }

    public static double[] splitX3(double x3){
        double w = Math.floor(0.5 + (x3/FDMCConstants.STEP_DISTANCE) );
        double x4 = x3 - w * FDMCConstants.STEP_DISTANCE;
        return new double[]{x4, w};
    }

    public static int[] splitChunkXCoordinate(int x3){
        int centre_offset = 1<<(CHUNK_STEP_DISTANCE_BITS-1);
        int shifted_x3 = x3 + centre_offset;
        int shifted_x4 = shifted_x3 & (1<<(CHUNK_STEP_DISTANCE_BITS)) - 1;
        int x4 = shifted_x4 - centre_offset;
        int w4 = shifted_x3 >> CHUNK_STEP_DISTANCE_BITS;
        return new int[]{x4, w4};
    }

    @Deprecated
    public static BlockPos getOffset(int stepDirection){
        return new BlockPos(stepDirection * FDMCConstants.STEP_DISTANCE, 0, 0);
    }

    public static int getOffsetX(int dw){
        return dw * FDMCConstants.STEP_DISTANCE;
    }

    public static double getOffsetX(double dw){
        return dw * FDMCConstants.STEP_DISTANCE;
    }

    public static double mod(double a, double b){
        return ((a%b) + b) % b;
    }

    public static int chunkCountInRadius(int radius) {
        return (2*radius + 1) * (4 * radius * radius + 4 * radius + 3) / 3;
    }
}
