package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.item.ItemPlacementContext4;
import com.gmail.inayakitorikhurram.fdmc.math.ChestAdjacencyAxis;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.DoubleChestType;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.CHEST_TYPE_2;
import static net.minecraft.block.ChestBlock.*;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin extends BlockMixin{
    private static final Logger LOGGER = FDMCConstants.LOGGER;
    @Shadow
    public static Direction getFacing(BlockState state) {
        return null;
    }


    private static final VoxelShape DOUBLE_KATA_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    private static final VoxelShape DOUBLE_ANA_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);


    //get(dir)[0] is the first direction (left/right), get(dir)[1] is the second direction (kata/ana)
    private static final HashMap<Direction.Axis, EnumMap<ChestAdjacencyAxis, Direction.Axis>> DIRECTION_TO_CHEST_DIRECTIONS = Maps.newHashMap(Map.of(
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

    //get the chest type compared to the side axis it's connected to
    private DoubleChestType getChestTypeFor1Axis(ChestAdjacencyAxis adjRotation, ItemPlacementContext4 ctx){

        Direction facing = ctx.getPlayerFacing().getOpposite();
        Direction neighbourDirection = ctx.getSide();
        Direction neighbourFacing;
        Direction.Axis adjAxis = DIRECTION_TO_CHEST_DIRECTIONS.get(facing.getAxis()).get(adjRotation);
        //either right or kata
        Direction sideDirection = ((Direction4)(Object)facing).rotateCounterclockwiseInto(adjAxis);
        //not actually is sneaking, specifically "should cancel interaction" but that only happens when sneaking in vanilla
        boolean isSneaking = ctx.shouldCancelInteraction();

        ChestType chestType = ChestType.SINGLE;

        //when placing vertically
        if(neighbourDirection.getAxis() == Direction.Axis.Y){
            neighbourFacing = this.getNeighborChestDirection(ctx, neighbourDirection);
            //if on that side it's null try the other side
            if(neighbourFacing == null) {
                neighbourDirection = neighbourDirection.getOpposite();
                neighbourFacing = this.getNeighborChestDirection(ctx, neighbourDirection);
                //if neither sides have it, give up
                if(neighbourFacing == null){
                    LOGGER.info("no side direction, defaulting to single");
                    return DoubleChestType.of(adjRotation, ChestType.SINGLE);
                }
            }
        }

        //first getting which direction we should be looking in
        if(neighbourDirection == sideDirection) {
            chestType = ChestType.LEFT;
        } else if(neighbourDirection == sideDirection.getOpposite()){
            chestType = ChestType.RIGHT;
        }

        LOGGER.info("adjRotation {}, adjAxis: {}, ctx: {}, facing: {}, neighbourDirection: {}, sideDirection: {}",
                adjRotation,
                adjAxis,
                ctx,
                facing,
                neighbourDirection,
                sideDirection
        );

        return DoubleChestType.of(adjRotation, chestType);
    }

    //jank, will make enum later
    private static ChestType getConnection(BlockState state, int axis){
        if(axis == 1){
            return state.get(CHEST_TYPE);
        } else if(axis == 2) {
            return state.get(CHEST_TYPE_2);
        } else{
            throw new IllegalArgumentException("axis must be 0 or 1");
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        ItemPlacementContext4 ctx4 = (ItemPlacementContext4) ctx;

        ChestType chestType = ChestType.SINGLE;
        ChestType chestType2 = ChestType.SINGLE;
        Direction direction = ctx4.getPlayerFacing().getOpposite();

        chestType = getChestTypeFor1Axis(
                ChestAdjacencyAxis.LEFTRIGHT,
                (ItemPlacementContext4) ctx
        ).get(ChestAdjacencyAxis.LEFTRIGHT);



        FluidState fluidState = ctx4.getWorld().getFluidState(ctx4.getBlockPos());

        return this.getDefaultState()
                .with(FACING, direction)
                .with(CHEST_TYPE, chestType)
                .with(CHEST_TYPE_2, chestType2)
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Shadow @Final protected static VoxelShape SINGLE_SHAPE;

    @Shadow @Nullable protected abstract Direction getNeighborChestDirection(ItemPlacementContext ctx, Direction dir);

    @Shadow @Final public static DirectionProperty FACING;

    @Shadow @Final public static EnumProperty<ChestType> CHEST_TYPE;

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void modifyOutline(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir){
        if(getFacing(state).getAxis() == Direction4Constants.Axis4Constants.W){
            cir.setReturnValue(SINGLE_SHAPE);
            cir.cancel();
        }
    }

    @Inject(method = "<init>", at= @At(value = "TAIL"))
    private void modifyDefaultState(AbstractBlock.Settings settings, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier, CallbackInfo ci){
        this.setDefaultState(this.getDefaultState().with(CHEST_TYPE_2, ChestType.SINGLE));
    }

    @Inject(method = "appendProperties", at = @At("RETURN"))
    private void appendProperty(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci){
        builder.add(CHEST_TYPE_2);
    }

}
