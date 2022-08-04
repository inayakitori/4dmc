package com.gmail.inayakitorikhurram.fdmc;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

public class FDMCMath {

    public static double[] toPos4(Vec3d pos3){
        double[] pos4 = new double[4];

        pos4[3] = Math.floor((pos3.getX()/ FDMCConstants.STEP_DISTANCE) + 0.5);
        pos4[0] = pos3.getX() - pos4[3] * FDMCConstants.STEP_DISTANCE;
        pos4[1] = pos3.getY();
        pos4[2] = pos3.getZ();

        return pos4;
    }

    public static Vec3d toPos3(int[] pos4){
        return new Vec3d(
                pos4[0] + FDMCConstants.STEP_DISTANCE * pos4[3],
                pos4[1],
                pos4[2]
        );
    }


    public static int[] toBlockPos4(BlockPos pos3){
        int[] pos4 = new int[4];

        pos4[3] = (int) (Math.floor(pos3.getX()/ FDMCConstants.STEP_DISTANCE) + 0.5);
        pos4[0] = pos3.getX() - pos4[3] * FDMCConstants.STEP_DISTANCE;
        pos4[1] = pos3.getY();
        pos4[2] = pos3.getZ();

        return pos4;
    }

    public static BlockPos toBlockPos3(int[] pos4){
        return new BlockPos(
                pos4[0] + FDMCConstants.STEP_DISTANCE * pos4[3]/4,
                pos4[1],
                pos4[2]
        );
    }

    public static int[] toChunkPos4(ChunkPos pos3){
        int[] pos4 = new int[3];

        pos4[2] = (int) (Math.floor((pos3.x + 0d)/ FDMCConstants.STEP_DISTANCE) + 0.5);
        pos4[0] = pos3.x - pos4[2] * FDMCConstants.STEP_DISTANCE;
        pos4[1] = pos3.z;

        return pos4;
    }

    public static ChunkPos toChunkPos3(int[] pos4){
        return new ChunkPos(
                pos4[0] + FDMCConstants.STEP_DISTANCE * pos4[3]/4,
                pos4[1]
        );
    }

}
