package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.item.ItemPlacementContext4;
import com.gmail.inayakitorikhurram.fdmc.math.*;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.ChestBlockI;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import com.gmail.inayakitorikhurram.fdmc.screen.FDMCScreenHandler;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumMap;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.CHEST_TYPE_2;
import static net.minecraft.block.ChestBlock.WATERLOGGED;
import static net.minecraft.block.ChestBlock.getFacing;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin
        extends AbstractChestBlock<ChestBlockEntity> implements Waterloggable, ChestBlockI {

    private static final VoxelShape DOUBLE_KATA_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    private static final VoxelShape DOUBLE_ANA_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);


    @Shadow @Final protected static VoxelShape SINGLE_SHAPE;

    protected ChestBlockMixin(Settings settings, Supplier<BlockEntityType<? extends ChestBlockEntity>> blockEntityTypeSupplier) {
        super(settings, blockEntityTypeSupplier);
    }

    @Shadow @Nullable protected abstract Direction getNeighborChestDirection(ItemPlacementContext ctx, Direction dir);

    @Shadow @Final public static DirectionProperty FACING;

    @Shadow @Final public static EnumProperty<ChestType> CHEST_TYPE;

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {

        VoxelShape shape = SINGLE_SHAPE;

        EnumMap<ChestAdjacencyAxis, Optional<Direction>> connectionDirections = ChestBlockI.getConnectionDirections(state);
        for(ChestAdjacencyAxis connectionAxis : ChestAdjacencyAxis.values()) {
            if (connectionDirections.get(connectionAxis).isPresent()) {
                switch (Direction4Enum.byId(connectionDirections.get(connectionAxis).get().getId())) {
                    case DOWN -> {
                    }
                    case UP -> {
                    }
                    case NORTH -> {
                        shape = VoxelShapes.union(shape, DOUBLE_NORTH_SHAPE);
                    }
                    case SOUTH -> {
                        shape = VoxelShapes.union(shape, DOUBLE_SOUTH_SHAPE);
                    }
                    case WEST -> {
                        shape = VoxelShapes.union(shape, DOUBLE_WEST_SHAPE);
                    }
                    case EAST -> {
                        shape = VoxelShapes.union(shape, DOUBLE_EAST_SHAPE);
                    }
                    case KATA -> {
                        shape = VoxelShapes.union(shape, DOUBLE_KATA_SHAPE);
                    }
                    case ANA -> {
                        shape = VoxelShapes.union(shape, DOUBLE_ANA_SHAPE);
                    }
                }
            }
        }
        return shape;
    }

    //this gets the inventory
    @Shadow @Final @Mutable
    private static final DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<Inventory>> INVENTORY_RETRIEVER =
            new QuadBlockProperties.PropertyRetriever4<>() {

                @Override
                public Optional<Inventory> getFromQuad(ChestBlockEntity be00, ChestBlockEntity be01, ChestBlockEntity be10, ChestBlockEntity be11) {
                    return Optional.of(new MultiInventory(be00, be01, be10, be11));
                }

                public Optional<Inventory> getFromBoth(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
                    return Optional.of(new DoubleInventory(chestBlockEntity, chestBlockEntity2));
                }

                public Optional<Inventory> getFrom(ChestBlockEntity chestBlockEntity) {
                    return Optional.of(chestBlockEntity);
                }

                public Optional<Inventory> getFallback() {
                    return Optional.empty();
                }
            };

    //This gets the container screen
    @Shadow @Final @Mutable
    private static final DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<NamedScreenHandlerFactory>> NAME_RETRIEVER =
            new QuadBlockProperties.PropertyRetriever4<>() {

                @Override
                public Optional<NamedScreenHandlerFactory> getFromQuad(ChestBlockEntity be00, ChestBlockEntity be01, ChestBlockEntity be10, ChestBlockEntity be11) {
                    Inventory inventory = new MultiInventory(be00, be01, be10, be11);
                    return Optional.of(getScreenHandlerFactoryQuad(be00, be01, be10, be11, inventory));
                }

                public Optional<NamedScreenHandlerFactory> getFromBoth(final ChestBlockEntity chestBlockEntity, final ChestBlockEntity chestBlockEntity2) {
                    final DoubleInventory inventory = new DoubleInventory(chestBlockEntity, chestBlockEntity2);
                    return Optional.of(getScreenHandlerFactoryDouble(chestBlockEntity, chestBlockEntity2, inventory));
                }

                public Optional<NamedScreenHandlerFactory> getFrom(ChestBlockEntity chestBlockEntity) {
                    return Optional.of(chestBlockEntity);
                }

                public Optional<NamedScreenHandlerFactory> getFallback() {
                    return Optional.empty();
                }
            };


    @Shadow @Final protected static VoxelShape DOUBLE_NORTH_SHAPE;

    @Shadow @Final protected static VoxelShape DOUBLE_SOUTH_SHAPE;

    @Shadow @Final protected static VoxelShape DOUBLE_WEST_SHAPE;

    @Shadow @Final protected static VoxelShape DOUBLE_EAST_SHAPE;

    //Mixin doesn't allow double-nested classes so this has to be outside the class
    @NotNull
    private static NamedScreenHandlerFactory getScreenHandlerFactoryQuad(final ChestBlockEntity be00, final ChestBlockEntity be01, final ChestBlockEntity be10, final ChestBlockEntity be11, Inventory inventory) {
        return new NamedScreenHandlerFactory() {
            @Nullable
            public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                if (be00.checkUnlocked(playerEntity) && be01.checkUnlocked(playerEntity) && be10.checkUnlocked(playerEntity) && be11.checkUnlocked(playerEntity)) {
                    be00.checkLootInteraction(playerInventory.player);
                    be01.checkLootInteraction(playerInventory.player);
                    be10.checkLootInteraction(playerInventory.player);
                    be11.checkLootInteraction(playerInventory.player);
                    return FDMCScreenHandler.createGeneric9x12(i, playerInventory, inventory);
                } else {
                    return null;
                }
            }

            public Text getDisplayName() {
                if       (be00.hasCustomName()) {
                    return be00.getDisplayName();
                } else if(be01.hasCustomName()){
                    return be01.getDisplayName();
                } else if(be10.hasCustomName()){
                    return be10.getDisplayName();
                } else if(be11.hasCustomName()){
                    return be11.getDisplayName();
                } else{
                    return Text.translatable("container.chestQuadruple");
                }
            }
        };
    }

    @NotNull
    private static NamedScreenHandlerFactory getScreenHandlerFactoryDouble(final ChestBlockEntity chestBlockEntity, final ChestBlockEntity chestBlockEntity2, Inventory inventory) {
        return new NamedScreenHandlerFactory() {
            @Override
            @Nullable
            public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                if (chestBlockEntity.checkUnlocked(playerEntity) && chestBlockEntity2.checkUnlocked(playerEntity)) {
                    chestBlockEntity.checkLootInteraction(playerInventory.player);
                    chestBlockEntity2.checkLootInteraction(playerInventory.player);
                    return GenericContainerScreenHandler.createGeneric9x6(i, playerInventory, inventory);
                }
                return null;
            }

            @Override
            public Text getDisplayName() {
                if (chestBlockEntity.hasCustomName()) {
                    return chestBlockEntity.getDisplayName();
                }
                if (chestBlockEntity2.hasCustomName()) {
                    return chestBlockEntity2.getDisplayName();
                }
                return Text.translatable("container.chestDouble");
            }
        };
    }

    @Override
    public DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> getBlockEntitySource(BlockState state, World world2, BlockPos pos2, boolean ignoreBlocked) {
        BiPredicate<WorldAccess, BlockPos> biPredicate = ignoreBlocked ? (world, pos) -> false : ChestBlock::isChestBlocked;
        //ignore warnings
        return QuadBlockProperties.toPropertySource(
                (BlockEntityType)this.entityTypeRetriever.get(),
                ChestBlockI::getConnections,
                ChestBlockI::getConnectionDirections,
                FACING,
                state, world2, pos2, biPredicate);
    }
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

    @Inject(method = "getFacing", at = @At("HEAD"), cancellable = true)
    private static void getConnectionDirection1(BlockState state, CallbackInfoReturnable<Direction> cir){
        cir.setReturnValue(
                ChestBlockI.getConnectionDirection(state, ChestAdjacencyAxis.LEFTRIGHT)
                .orElse(ChestBlockI.getConnectionDirection(state.get(FACING), ChestType.RIGHT, ChestAdjacencyAxis.LEFTRIGHT).get())
        );
        cir.cancel();
    }

    //include updates on the second adjacency axis
    //literally just a copy of the code but replacing CHEST_TYPE w/ CHEST_TYPE_2
    @Inject(method = "getStateForNeighborUpdate", at = @At("RETURN"), cancellable = true)
    private void neighbourUpdateAxis2(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir){
        //working off current state
        state = cir.getReturnValue();

        if (neighborState.isOf(this.asBlock()) && direction.getAxis().isHorizontal()) {
            ChestType chestType = neighborState.get(CHEST_TYPE_2);
            if (
                state.get(CHEST_TYPE_2) == ChestType.SINGLE &&
                chestType != ChestType.SINGLE &&
                state.get(FACING) == neighborState.get(FACING) &&
                ChestBlockI.getConnectionDirection(neighborState, ChestAdjacencyAxis.KATAANA).orElse(null) == direction.getOpposite()
            ) {
                cir.setReturnValue(
                        state.with(CHEST_TYPE_2, chestType.getOpposite())
                );
            }
        } else if (ChestBlockI.getConnectionDirection(state, ChestAdjacencyAxis.KATAANA).orElse(null) == direction) {
            cir.setReturnValue(
                state.with(CHEST_TYPE_2, ChestType.SINGLE)
            );
        }
        cir.cancel(); //I feel like this doesn't do anything?
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        ItemPlacementContext4 ctx4 = (ItemPlacementContext4) ctx;

        Direction direction = ctx4.getHorizontalPlayerFacing().getOpposite();

        ChestType chestType = getChestTypeFor1Axis(
                (ChestBlock) (Object) this, ChestAdjacencyAxis.LEFTRIGHT,
                (ItemPlacementContext4) ctx
        ).get(ChestAdjacencyAxis.LEFTRIGHT);


        ChestType chestType2 = getChestTypeFor1Axis(
                (ChestBlock) (Object) this, ChestAdjacencyAxis.KATAANA,
                (ItemPlacementContext4) ctx
        ).get(ChestAdjacencyAxis.KATAANA);

        FluidState fluidState = ctx4.getWorld().getFluidState(ctx4.getBlockPos());

        return this.getDefaultState()
                .with(FACING, direction)
                .with(CHEST_TYPE, chestType)
                .with(CHEST_TYPE_2, chestType2)
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    //get the chest type compared to the side axis it's connected to
    //returning a DoubleChestType may be redundant but also it is kinda useful and
    //I'd rather double-emphasise which axis it's gonna be on
    private static DoubleChestType getChestTypeFor1Axis(ChestBlock state, ChestAdjacencyAxis adjRotation, ItemPlacementContext4 ctx){

        Direction facing = ctx.getHorizontalPlayerFacing().getOpposite();
        Direction neighbourDirection = ctx.getSide();
        Direction neighbourFacing;
        Direction.Axis adjAxis = DIRECTION_TO_CHEST_DIRECTIONS.get(facing.getAxis()).get(adjRotation);
        //either right or kata
        Direction sideDirection = ((Direction4)(Object)facing).rotateCounterclockwiseInto(adjAxis);
        //not actually is sneaking, specifically "should cancel interaction" but that only happens when sneaking in vanilla
        boolean isSneaking = ctx.shouldCancelInteraction();

/*

        LOGGER.info("adjRotation {}, adjAxis: {}, ctx: {}, facing: {}, neighbourDirection: {}, sideDirection: {}",
                adjRotation,
                adjAxis,
                ctx,
                facing,
                neighbourDirection,
                sideDirection
        );
*/

        ChestType chestType = ChestType.SINGLE;

        //when placing vertically
        if(neighbourDirection.getAxis() == Direction.Axis.Y){
            neighbourDirection = sideDirection;
            neighbourFacing = ChestBlockI.getNeighborChestDirection(state, ctx, neighbourDirection, adjRotation);
            //if on that side it's null try the other side
            if(neighbourFacing == null || neighbourFacing == facing.getOpposite()) {
                neighbourDirection = neighbourDirection.getOpposite();
                neighbourFacing = ChestBlockI.getNeighborChestDirection(state, ctx, neighbourDirection, adjRotation);
                //if neither sides have it, give up
                if(neighbourFacing == null || neighbourFacing == facing.getOpposite()){
                    return DoubleChestType.of(adjRotation, ChestType.SINGLE);
                }
            }
        }
        neighbourFacing = ChestBlockI.getNeighborChestDirection((ChestBlock) (Object) state, ctx, neighbourDirection, adjRotation);
        //don't connect if neighbour is facing differently or already connected
        if(neighbourFacing != facing){
            return DoubleChestType.of(adjRotation, ChestType.SINGLE);
        }

        //now try to connect to the adjacent ones
        if(neighbourDirection == sideDirection) {
            chestType = ChestType.RIGHT;
        } else if(neighbourDirection == sideDirection.getOpposite()){
            chestType = ChestType.LEFT;
        }

        return DoubleChestType.of(adjRotation, chestType);
    }

    //override
    @Inject(method = "getAnimationProgressRetriever", at = @At("HEAD"), cancellable = true)
    private static void getAnimationProgressRetriever(LidOpenable progress, CallbackInfoReturnable<DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Float2FloatFunction>> cir) {
        cir.setReturnValue(
            ChestBlockI.getAnimationProgressRetriever(progress)
        );
        cir.cancel();
    }

}
