package com.gmail.inayakitorikhurram.fdmc;

import net.minecraft.util.math.*;

public class FDMCMath {

    
    public static BlockPos getOffset(int stepDirection){
        return new BlockPos(stepDirection * FDMCConstants.STEP_DISTANCE, 0, 0);
    }

    public static double mod(double a, double b){
        return ((a%b) + b) % b;
    }

    //xyz -> xyzw
    public static double[] toPos4(Vec3d pos3){
        double[] pos4 = new double[4];

        pos4[3] = Math.floor(0.5 + (pos3.getX()/FDMCConstants.STEP_DISTANCE) );
        pos4[0] = pos3.getX() - pos4[3] * FDMCConstants.STEP_DISTANCE;
        pos4[1] = pos3.getY();
        pos4[2] = pos3.getZ();

        return pos4;
    }

    public static double[] justXToPos4(double x){
        double[] pos4 = new double[2];
        pos4[1] = Math.floor(0.5 + (x/FDMCConstants.STEP_DISTANCE) );
        pos4[0] = x - pos4[1] * FDMCConstants.STEP_DISTANCE;
        return pos4;
    }

    public static Vec3d toPos3(double[] pos4){
        return new Vec3d(
                pos4[0] + FDMCConstants.STEP_DISTANCE * pos4[3],
                pos4[1],
                pos4[2]
        );
    }

    public static Vec3i toPos3(int[] pos4){
        return new Vec3i(
                pos4[0] + FDMCConstants.STEP_DISTANCE * pos4[3],
                pos4[1],
                pos4[2]
        );
    }

    //xyz -> xyzw
    public static int[] toBlockPos4(BlockPos pos3){
        int[] pos4 = new int[4];

        pos4[3] = (int)(Math.floor(0.5 + (pos3.getX() + 0d)/FDMCConstants.STEP_DISTANCE));
        pos4[0] = pos3.getX() - pos4[3] * FDMCConstants.STEP_DISTANCE;
        pos4[1] = pos3.getY();
        pos4[2] = pos3.getZ();

        return pos4;
    }

    //xyzw --> xyz
    public static BlockPos toBlockPos3(int[] pos4){
        return new BlockPos(
                pos4[0] + FDMCConstants.STEP_DISTANCE * pos4[3]/4,
                pos4[1],
                pos4[2]
        );
    }

    //xz --> xzw
    public static int[] toChunkPos4(ChunkPos pos3){
        int[] pos4 = new int[3];

        pos4[2] = (int)(Math.floor(0.5 + (pos3.x + 0d)/FDMCConstants.CHUNK_STEP_DISTANCE));
        pos4[0] = pos3.x - pos4[2] * FDMCConstants.CHUNK_STEP_DISTANCE;
        pos4[1] = pos3.z;

        return pos4;
    }

    public static ChunkPos toChunkPos3(int[] pos4){
        return new ChunkPos(
                pos4[0] + FDMCConstants.CHUNK_STEP_DISTANCE * pos4[2],
                pos4[1]
        );
    }

}
