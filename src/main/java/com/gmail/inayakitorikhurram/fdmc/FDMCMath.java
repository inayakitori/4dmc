package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.*;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.WIRE_CONNECTION_MAP;

public class FDMCMath {


    public static BlockPos getOffset(int stepDirection){
        return new BlockPos(stepDirection * FDMCConstants.STEP_DISTANCE, 0, 0);
    }
    public static int getRedstoneColorIndex(BlockState state){
        if(!state.isOf(Blocks.REDSTONE_WIRE)) return -1;
        int power = state.get(Properties.POWER);
        power += state.get(WIRE_CONNECTION_MAP.get(Direction4.KATA)).isConnected() ? 1 << 4 : 0;
        power += state.get(WIRE_CONNECTION_MAP.get(Direction4.ANA)).isConnected() ? 1 << 5 : 0;
        return power;
    }

}
