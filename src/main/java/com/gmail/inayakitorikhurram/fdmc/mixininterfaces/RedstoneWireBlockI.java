package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import net.minecraft.block.BlockState;

import static net.minecraft.block.RedstoneWireBlock.DIRECTION_TO_WIRE_CONNECTION_PROPERTY;

public interface RedstoneWireBlockI{

    static boolean isFullyConnected4(BlockState state){
        return
                state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(Direction4Constants.NORTH)).isConnected() &&
                state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(Direction4Constants.SOUTH)).isConnected() &&
                state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(Direction4Constants.EAST) ).isConnected() &&
                state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(Direction4Constants.WEST) ).isConnected() &&
                state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(Direction4Constants.ANA)  ).isConnected() &&
                state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(Direction4Constants.KATA) ).isConnected();
    }

    static boolean isNotConnected4(BlockState state){
        return
                !state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(Direction4Constants.NORTH)).isConnected() &&
                !state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(Direction4Constants.SOUTH)).isConnected() &&
                !state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(Direction4Constants.EAST) ).isConnected() &&
                !state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(Direction4Constants.WEST) ).isConnected() &&
                !state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(Direction4Constants.ANA)  ).isConnected() &&
                !state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(Direction4Constants.KATA) ).isConnected();
    }

}
