package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4;
import com.gmail.inayakitorikhurram.fdmc.math.OptionalDirection4;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.FACING4;
import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.WIRE_CONNECTION_MAP;

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


    public static boolean redstoneWireConnectsTo4(BlockState state, @Nullable Direction4 dir4){

        Optional<Direction> dir3 = dir4 == null? Optional.empty() : dir4.getDirection3();

        if (state.isOf(Blocks.REDSTONE_WIRE)) {
            return true;
        }
        if (state.isOf(Blocks.REPEATER)) {
            Direction4 repeaterDir4 = state.get(FACING4).toDir4(state.get(RepeaterBlock.FACING));
            return repeaterDir4 == dir4 || repeaterDir4.getOpposite() == dir4;
        }
        if (state.isOf(Blocks.OBSERVER)) { //once 4D observers work this'll be better
            return dir3.filter(direction ->
                    direction == state.get(ObserverBlock.FACING)
            ).isPresent();
        }
        return state.emitsRedstonePower() && dir4 != null;
    }

    public static int getRedstoneColorIndex(BlockState state){
        if(!state.isOf(Blocks.REDSTONE_WIRE)) return -1;
        int power = state.get(Properties.POWER);
        power += state.get(WIRE_CONNECTION_MAP.get(Direction4.KATA)).isConnected() ? 1 << 4 : 0;
        power += state.get(WIRE_CONNECTION_MAP.get(Direction4.ANA)).isConnected() ? 1 << 5 : 0;
        return power;
    }

}
