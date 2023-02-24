package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.FDMCProperties;
import com.gmail.inayakitorikhurram.fdmc.item.ItemPlacementContext4;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Enum;
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
import net.minecraft.state.property.Property;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.CHEST_TYPE_2;
import static net.minecraft.block.ChestBlock.*;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin extends BlockMixin{

    @Shadow
    public static Direction getFacing(BlockState state) {
        return null;
    }


    private static final VoxelShape DOUBLE_KATA_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    private static final VoxelShape DOUBLE_ANA_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);


    //get(dir)[0] is the first direction (left/right), get(dir)[1] is the second direction (kata/ana)
    private static final HashMap<Direction.Axis, Direction.Axis[]> DIRECTION_TO_CHEST_DIRECTIONS = Maps.newHashMap(Map.of(
            Direction4Constants.Axis4Constants.X, new Direction.Axis[]{
                    Direction4Constants.Axis4Constants.Z,
                    Direction4Constants.Axis4Constants.W,
            },
            Direction4Constants.Axis4Constants.Z, new Direction.Axis[]{//this one breaks the order just for the sake of mod compat
                    Direction4Constants.Axis4Constants.X,
                    Direction4Constants.Axis4Constants.W,
            },
            Direction4Constants.Axis4Constants.W, new Direction.Axis[]{
                    Direction4Constants.Axis4Constants.X,
                    Direction4Constants.Axis4Constants.Z,
            }
    ));

    private DoubleChestType getChestType(Direction facing, Direction neighbourDirection){

        if(neighbourDirection.getAxis() == Direction.Axis.Y){
            throw new IllegalArgumentException("chests can't be connected vertically");
        }

        if(neighbourDirection == facing){
            //illegal
            return DoubleChestType.SINGLE;
        }

        Direction.Axis[] dirs = DIRECTION_TO_CHEST_DIRECTIONS.get(facing.getAxis());

        Direction rightDirection = ((Direction4)(Object)facing).rotateCounterclockwiseInto(dirs[0]);
        Direction kataDirection = ((Direction4)(Object)facing).rotateCounterclockwiseInto(dirs[1]);

        if(neighbourDirection == rightDirection) {
            return new DoubleChestType(ChestType.RIGHT, ChestType.SINGLE);
        } else if(neighbourDirection == rightDirection.getOpposite()){
            return new DoubleChestType(ChestType.LEFT, ChestType.SINGLE);
        } else if(neighbourDirection == kataDirection){
            return new DoubleChestType(ChestType.SINGLE, ChestType.RIGHT);
        } else if(neighbourDirection == kataDirection.getOpposite()){
            return new DoubleChestType(ChestType.SINGLE, ChestType.LEFT);
        } else{
            throw new IllegalArgumentException("this should be unreachable, all chest directions should've been considered");
        }

    }

    //@Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        ItemPlacementContext4 ctx4 = (ItemPlacementContext4) ctx;

        ChestType chestType = ChestType.SINGLE;
        ChestType chestType2 = ChestType.SINGLE;
        Direction direction = ctx4.getPlayerFacing().getOpposite();
        //not actually is sneaking, specifically "should cancel interaction" but that only happens when sneaking in vanilla
        boolean isSneaking = ctx4.shouldCancelInteraction();

        Direction placementSide = ctx4.getSide();
        if (placementSide.getAxis().isHorizontal() && isSneaking) {
            Direction neighbourFaceDirection = this.getNeighborChestDirection(ctx4, placementSide.getOpposite());
            if (neighbourFaceDirection != null && neighbourFaceDirection.getAxis() != placementSide.getAxis()) {
                direction = neighbourFaceDirection;

                //getting whether it should be a left or right chest
                if (neighbourFaceDirection.rotateYCounterclockwise() == placementSide.getOpposite()) {
                    chestType = ChestType.RIGHT;
                }
                else if(neighbourFaceDirection.rotateYClockwise() == placementSide.getOpposite()){
                    chestType = ChestType.LEFT;
                } else if (((Direction4)(Object)neighbourFaceDirection).rotateClockwise(Direction.Axis.Y, neighbourFaceDirection.getAxis()) == placementSide.getOpposite()) {
                    chestType2 = ChestType.RIGHT;
                }
                else if(neighbourFaceDirection.rotateYClockwise() == placementSide.getOpposite()){
                    chestType2 = ChestType.LEFT;
                }
            }
        }

        if (chestType == ChestType.SINGLE && !isSneaking) {
            if (direction == this.getNeighborChestDirection(ctx4, direction.rotateYClockwise())) {
                chestType = ChestType.LEFT;
            } else if (direction == this.getNeighborChestDirection(ctx4, direction.rotateYCounterclockwise())) {
                chestType = ChestType.RIGHT;
            }
        }


        FluidState fluidState = ctx4.getWorld().getFluidState(ctx4.getBlockPos());

        return this.getDefaultState()
                .with(FACING, direction)
                .with(CHEST_TYPE, chestType)
                .with(CHEST_TYPE_2, chestType2)
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Shadow @Final protected static VoxelShape SINGLE_SHAPE;

    @Shadow @Nullable protected abstract Direction getNeighborChestDirection(ItemPlacementContext ctx, Direction dir);

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
