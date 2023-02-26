package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.math.ChestAdjacencyAxis;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.DoubleChestType;
import com.gmail.inayakitorikhurram.fdmc.mixin.block.ChestBlockMixin;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.ChestType;
import net.minecraft.util.math.Direction;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.CHEST_TYPE_2;
import static net.minecraft.block.ChestBlock.CHEST_TYPE;
import static net.minecraft.block.ChestBlock.FACING;

public interface ChestBlockI {


    //get(dir)[0] is the first direction (left/right), get(dir)[1] is the second direction (kata/ana)
    HashMap<Direction.Axis, EnumMap<ChestAdjacencyAxis, Direction.Axis>> DIRECTION_TO_CHEST_DIRECTIONS = Maps.newHashMap(Map.of(
            Direction4Constants.Axis4Constants.X, Maps.newEnumMap(Map.of(
                    ChestAdjacencyAxis.LEFTRIGHT, Direction4Constants.Axis4Constants.Z,
                    ChestAdjacencyAxis.KATAANA, Direction4Constants.Axis4Constants.W
            ))
            ,
            Direction4Constants.Axis4Constants.Z, Maps.newEnumMap(Map.of(//this one breaks the order just for the sake of mod compat
                    ChestAdjacencyAxis.LEFTRIGHT, Direction4Constants.Axis4Constants.X,
                    ChestAdjacencyAxis.KATAANA, Direction4Constants.Axis4Constants.W
            ))
            ,
            Direction4Constants.Axis4Constants.W, Maps.newEnumMap(Map.of(
                    ChestAdjacencyAxis.LEFTRIGHT, Direction4Constants.Axis4Constants.X,
                    ChestAdjacencyAxis.KATAANA, Direction4Constants.Axis4Constants.Z
            ))
    ));

    EnumMap<ChestAdjacencyAxis, Optional<Direction>> getConnectionDirections(BlockState state);

    static Optional<Direction> getConnectionDirection(Direction chestForwardDirection, ChestType adjDir, ChestAdjacencyAxis adjAxis){
        Direction.Axis sideAxis = DIRECTION_TO_CHEST_DIRECTIONS.get(chestForwardDirection.getAxis()).get(adjAxis);
        return switch(adjDir) {
            case SINGLE -> Optional.empty();
            case LEFT -> Optional.of(((Direction4)(Object)chestForwardDirection).rotateClockwiseInto(sideAxis));
            case RIGHT -> Optional.of(((Direction4)(Object)chestForwardDirection).rotateCounterclockwiseInto(sideAxis));
        };
    }

    static Optional<Direction> getConnectionDirection(BlockState state, ChestAdjacencyAxis adjAxis){
        return getConnectionDirection(state.get(FACING), ChestBlockI.getConnection(state, adjAxis), adjAxis);
    }

    static ChestType getConnection(BlockState state, ChestAdjacencyAxis axis){
        return switch (axis){
            case LEFTRIGHT -> state.get(CHEST_TYPE);
            case KATAANA -> state.get(CHEST_TYPE_2);
        };
    }

    static DoubleChestType getConnections(BlockState state){
        return new DoubleChestType(
                state.get(CHEST_TYPE),
                state.get(CHEST_TYPE_2)
        );
    }
}
