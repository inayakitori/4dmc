package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4;
import net.minecraft.block.BlockState;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.WIRE_CONNECTION_MAP;

public interface RedstoneWireBlockI{

    static boolean isFullyConnected4(BlockState state){
        return
                state.get(WIRE_CONNECTION_MAP.get(Direction4.NORTH)).isConnected() &&
                        state.get(WIRE_CONNECTION_MAP.get(Direction4.SOUTH)).isConnected() &&
                        state.get(WIRE_CONNECTION_MAP.get(Direction4.EAST) ).isConnected() &&
                        state.get(WIRE_CONNECTION_MAP.get(Direction4.WEST) ).isConnected() &&
                        state.get(WIRE_CONNECTION_MAP.get(Direction4.ANA)  ).isConnected() &&
                        state.get(WIRE_CONNECTION_MAP.get(Direction4.KATA) ).isConnected();
    }

    static boolean isNotConnected4(BlockState state){
        return
                !state.get(WIRE_CONNECTION_MAP.get(Direction4.NORTH)).isConnected() &&
                        !state.get(WIRE_CONNECTION_MAP.get(Direction4.SOUTH)).isConnected() &&
                        !state.get(WIRE_CONNECTION_MAP.get(Direction4.EAST) ).isConnected() &&
                        !state.get(WIRE_CONNECTION_MAP.get(Direction4.WEST) ).isConnected() &&
                        !state.get(WIRE_CONNECTION_MAP.get(Direction4.ANA)  ).isConnected() &&
                        !state.get(WIRE_CONNECTION_MAP.get(Direction4.KATA) ).isConnected();
    }

}
