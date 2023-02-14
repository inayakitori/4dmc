package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.*;

import static net.minecraft.block.RedstoneWireBlock.DIRECTION_TO_WIRE_CONNECTION_PROPERTY;


public class FDMCMath {

    public static int max(int... vals) {
        int max_val = 0;
        for(int val: vals){
            max_val = Math.max(val, max_val);
        }
        return max_val;
    }
    public static int min(int... vals) {
        int min_val = 0;
        for(int val: vals){
            min_val = Math.min(val, min_val);
        }
        return min_val;
    }
    public static BlockPos getOffset(int stepDirection){
        return new BlockPos(stepDirection * FDMCConstants.STEP_DISTANCE, 0, 0);
    }
    public static int getRedstoneColorIndex(BlockState state){
        if(!state.isOf(Blocks.REDSTONE_WIRE)) return -1;
        int power = state.get(Properties.POWER);
        power += state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(Direction4Constants.KATA)).isConnected() ? 1 << 4 : 0;
        power += state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(Direction4Constants.ANA)).isConnected() ? 1 << 5 : 0;
        return power;
    }

}
