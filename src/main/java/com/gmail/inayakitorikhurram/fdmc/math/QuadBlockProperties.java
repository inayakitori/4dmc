package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.ChestBlockI;
import com.ibm.icu.impl.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.DoubleBlockProperties.Type;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.ChestType;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.slf4j.Logger;

import java.util.EnumMap;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.CHEST_TYPE_2;
import static net.minecraft.block.ChestBlock.CHEST_TYPE;

public class QuadBlockProperties {

    private static final Logger LOGGER = FDMCConstants.LOGGER;

    //gets the block entity associated with these chests
    public static <S extends BlockEntity> DoubleBlockProperties.PropertySource<S> toPropertySource(
            BlockEntityType<S> blockEntityType,
            Function<BlockState, DoubleChestType> stateToType,
            Function<BlockState, EnumMap<ChestAdjacencyAxis, Optional<Direction>>> stateToConnectingDirections,
            DirectionProperty facingProperty,
            BlockState state,
            WorldAccess world,
            BlockPos pos,
            BiPredicate<WorldAccess, BlockPos> fallbackTester) {
        S thisBlockEntity = blockEntityType.get(world, pos);
        if (thisBlockEntity == null) {
            //something pretty messed up must've happened to get here
            return DoubleBlockProperties.PropertyRetriever::getFallback;
        }
        if (fallbackTester.test(world, pos)) {
            return DoubleBlockProperties.PropertyRetriever::getFallback;
        }


        //the connection axes
        DoubleChestType type = stateToType.apply(state);
        ChestType type1 = type.get(ChestAdjacencyAxis.LEFTRIGHT);
        ChestType type2 = type.get(ChestAdjacencyAxis.KATAANA);

        if (type.isSingle()) {
            return new DoubleBlockProperties.PropertySource.Single<S>(thisBlockEntity);
        }
        else if(type.isDoubleOn(ChestAdjacencyAxis.LEFTRIGHT)) {
            return DoubleBlockProperties.toPropertySource(
                    blockEntityType,
                    blockState -> chestTypeToType(blockState.get(CHEST_TYPE)),
                    blockState ->  ((ChestBlockI) (state.getBlock())).getConnectionDirections(blockState).get(ChestAdjacencyAxis.LEFTRIGHT).get(),
                    facingProperty,
                    state, world, pos, fallbackTester);
        }
        else if(type.isDoubleOn(ChestAdjacencyAxis.KATAANA)){
            return DoubleBlockProperties.toPropertySource(
                    blockEntityType,
                    blockState -> chestTypeToType(blockState.get(CHEST_TYPE_2)),
                    blockState -> ((ChestBlockI) (state.getBlock())).getConnectionDirections(blockState).get(ChestAdjacencyAxis.KATAANA).get(),
                    facingProperty,
                    state, world, pos, fallbackTester);
        }
        else { //Quad


            BlockPos neighbor1 = pos.offset(stateToConnectingDirections.apply(state).get(ChestAdjacencyAxis.LEFTRIGHT).orElse(Direction.UP));//just so that non-chests blocks don't break when it's null
            BlockState neighborState1 = world.getBlockState(neighbor1);
            if(neighborState1.getBlock() != state.getBlock()) return DoubleBlockProperties.PropertyRetriever::getFallback;

            BlockPos neighbor2 = pos.offset(stateToConnectingDirections.apply(state).get(ChestAdjacencyAxis.KATAANA).orElse(Direction.UP));
            BlockState neighborState2 = world.getBlockState(neighbor2);
            if(neighborState2.getBlock() != state.getBlock()) return DoubleBlockProperties.PropertyRetriever::getFallback;


            BlockPos neighbor12 = neighbor1.offset(stateToConnectingDirections.apply(neighborState1).get(ChestAdjacencyAxis.KATAANA).orElse(Direction.UP));
            BlockState neighborState12 = world.getBlockState(neighbor12);

            {//don't allow tetris-like things, only 2x2s
                BlockPos neighbor21 = neighbor2.offset(stateToConnectingDirections.apply(neighborState2).get(ChestAdjacencyAxis.LEFTRIGHT).orElse(Direction.UP));
                if(!neighbor12.equals(neighbor21) || !neighborState1.isOf(state.getBlock()) || !neighborState2.isOf(state.getBlock()) || !neighborState12.isOf(state.getBlock())){
                    /*LOGGER.warn("neighbour positions invalid");
                    LOGGER.info("quad chest: \n{} -> {}\n{} -> {}\n{} -> {}\n{} -> {}",
                            pos, neighbor1,
                            pos, neighbor2,
                            neighbor1, neighbor12,
                            neighbor2, neighbor21
                            );*/
                    return DoubleBlockProperties.PropertyRetriever::getFallback;
                }
            }

            ChestType neighbor1Type = stateToType.apply(neighborState1).get(ChestAdjacencyAxis.LEFTRIGHT);
            ChestType neighbor2Type = stateToType.apply(neighborState2).get(ChestAdjacencyAxis.KATAANA);
            ChestType neighbor12Type = stateToType.apply(neighborState12).get(ChestAdjacencyAxis.KATAANA);

            //LOGGER.info("quad chest: \n{}\n{}\n{}\n{}", state, neighborState1, neighborState2, neighborState12);

            //if they're opposingly connected and facing the same way
            if (
                    neighborState1 .isOf(state.getBlock()) && type1 !=  neighbor1Type && neighborState1 .get(facingProperty) == state.get(facingProperty) &&
                    neighborState2 .isOf(state.getBlock()) && type2 !=  neighbor2Type && neighborState2 .get(facingProperty) == state.get(facingProperty) &&
                    neighborState12.isOf(state.getBlock()) && type2 != neighbor12Type && neighborState12.get(facingProperty) == state.get(facingProperty)
            ) {
                if (fallbackTester.test(world, neighbor1)) {
                    return DoubleBlockProperties.PropertyRetriever::getFallback;
                }
                S be00 = thisBlockEntity;
                S be01 = blockEntityType.get(world, neighbor1);
                S be10 = blockEntityType.get(world, neighbor2);
                S be11 = blockEntityType.get(world, neighbor12);

                if(be01 == null || be10 == null || be11 == null) {
                    return DoubleBlockProperties.PropertyRetriever::getFallback;
                }

                S temp; //all the swapping is done w this
                //swap in left/right axis
                if (type1 == ChestType.LEFT) {
                    temp = be00;
                    be00 = be01;
                    be01 = temp;

                    temp = be10;
                    be10 = be11;
                    be11 = temp;
                }


                //swap in kata/ana axis
                if (type2 == ChestType.LEFT) {
                    temp = be00;
                    be00 = be10;
                    be10 = temp;

                    temp = be01;
                    be01 = be11;
                    be11 = temp;
                }

                //LOGGER.info("creating quad of:\n{}\n{}\n{}\n{}", be00, be01, be10, be11);
                return new PropertySource4.Quad<>(be00, be01, be10, be11);
            }
        }
        //fallback
        return DoubleBlockProperties.PropertyRetriever::getFallback;
    }


    private static Type chestTypeToType(ChestType chestType){
        return switch (chestType){
            case SINGLE -> Type.SINGLE;
            case RIGHT -> Type.FIRST;
            case LEFT -> Type.SECOND;
        };
    }

    private static ChestType typeToChestType(Type type){
        return switch (type){
            case SINGLE -> ChestType.SINGLE;
            case FIRST -> ChestType.RIGHT;
            case SECOND -> ChestType.LEFT;
        };
    }

    public interface PropertySource4<S> extends DoubleBlockProperties.PropertySource<S> {
        final class Quad<S>
                implements PropertySource4<S> {
            private final S be00;
            private final S be01;//right == second
            private final S be10;//kata
            private final S be11;//right + kata

            public Quad(S be00, S be01, S be10, S be11) {
                this.be00 = be00;
                this.be01 = be01;
                this.be10 = be10;
                this.be11 = be11;
            }

            @Override
            public <T> T apply(DoubleBlockProperties.PropertyRetriever<? super S, T> propertyRetriever) {
                if(propertyRetriever instanceof PropertyRetriever4 propertyRetriever4){
                    return (T) propertyRetriever4.getFromQuad(
                            this.be00,
                            this.be01,
                            this.be10,
                            this.be11
                    );
                } else{
                    return propertyRetriever.getFromBoth(
                            this.be00,
                            this.be01
                    );
                }

            }
        }
    }

    public interface PropertyRetriever4<S, T> extends DoubleBlockProperties.PropertyRetriever<S,T> {
        T getFromQuad(S be00, S be01, S be10, S be11);
    }

}
