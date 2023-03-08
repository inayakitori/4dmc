package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.math.ChestAdjacencyAxis;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.DoubleChestType;
import com.gmail.inayakitorikhurram.fdmc.math.QuadBlockProperties;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.enums.ChestType;
import net.minecraft.item.ItemPlacementContext;
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

    static Direction getNeighborChestDirection(ChestBlock chestBlock, ItemPlacementContext ctx, Direction dir, ChestAdjacencyAxis adjAxis) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(dir));
        return blockState.isOf(chestBlock) && ChestBlockI.getConnection(blockState, adjAxis) == ChestType.SINGLE ? blockState.get(FACING) : null;
    }

    static EnumMap<ChestAdjacencyAxis, Optional<Direction>> getConnectionDirections(BlockState state) {
        return new EnumMap<>(Map.of(
                ChestAdjacencyAxis.LEFTRIGHT, getConnectionDirection(state, ChestAdjacencyAxis.LEFTRIGHT),
                ChestAdjacencyAxis.KATAANA,  getConnectionDirection(state, ChestAdjacencyAxis.KATAANA)
        ));
    }

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
    static QuadBlockProperties.PropertyRetriever4<ChestBlockEntity, Float2FloatFunction> getAnimationProgressRetriever(final LidOpenable progress) {
        return new QuadBlockProperties.PropertyRetriever4<>() {

            @Override
            public Float2FloatFunction getFromQuad(ChestBlockEntity be00, ChestBlockEntity be01, ChestBlockEntity be10, ChestBlockEntity be11) {
                return tickDelta -> Math.max(Math.max(Math.max(
                                        be00.getAnimationProgress(tickDelta),
                                        be01.getAnimationProgress(tickDelta)),
                                        be10.getAnimationProgress(tickDelta)),
                                        be11.getAnimationProgress(tickDelta));
            }

            @Override
            public Float2FloatFunction getFromBoth(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
                return tickDelta -> Math.max(chestBlockEntity.getAnimationProgress(tickDelta), chestBlockEntity2.getAnimationProgress(tickDelta));
            }

            @Override
            public Float2FloatFunction getFrom(ChestBlockEntity chestBlockEntity) {
                return chestBlockEntity::getAnimationProgress;
            }

            @Override
            public Float2FloatFunction getFallback() {
                return progress::getAnimationProgress;
            }

        };
    }

}
