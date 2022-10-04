package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.Direction4;
import com.gmail.inayakitorikhurram.fdmc.FDMCProperties;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

import static net.minecraft.block.RedstoneWireBlock.POWER;

@Mixin(RedstoneWireBlock.class)
abstract
class RedstoneWireBlockMixin
        extends Block {

    public RedstoneWireBlockMixin(Settings settings) {
        super(settings);
    }

    @Shadow protected abstract int increasePower(BlockState state);

    @Shadow private boolean wiresGivePower;

    @Shadow @Final public static EnumProperty<WireConnection> WIRE_CONNECTION_NORTH;
    @Shadow @Final public static EnumProperty<WireConnection> WIRE_CONNECTION_EAST;
    @Shadow @Final public static EnumProperty<WireConnection> WIRE_CONNECTION_SOUTH;
    @Shadow @Final public static EnumProperty<WireConnection> WIRE_CONNECTION_WEST;


    @Shadow
    protected static boolean connectsTo(BlockState state) {
        return false;
    }

    @Shadow protected abstract boolean canRunOnTop(BlockView world, BlockPos pos, BlockState floor);

    @Shadow
    private static boolean isNotConnected(BlockState state) {
        return false;
    }

    @Shadow protected abstract BlockState getDefaultWireState(BlockView world, BlockState state, BlockPos pos);

    private static final EnumProperty<WireConnection> WIRE_CONNECTION_KATA = FDMCProperties.KATA_WIRE_CONNECTION;
    private static final EnumProperty<WireConnection> WIRE_CONNECTION_ANA = FDMCProperties.ANA_WIRE_CONNECTION;


    private static final Map<Direction4, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY4 = Maps.newEnumMap(ImmutableMap.of(
            Direction4.NORTH, WIRE_CONNECTION_NORTH,
            Direction4.EAST, WIRE_CONNECTION_EAST,
            Direction4.SOUTH, WIRE_CONNECTION_SOUTH,
            Direction4.WEST, WIRE_CONNECTION_WEST,
            Direction4.KATA, WIRE_CONNECTION_KATA,
            Direction4.ANA, WIRE_CONNECTION_ANA));

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;setDefaultState(Lnet/minecraft/block/BlockState;)V"))//set default state to not have kata/ana up
    private BlockState injectedDefaultState(BlockState defaultState){
        return defaultState.with(WIRE_CONNECTION_KATA, WireConnection.NONE).with(WIRE_CONNECTION_ANA, WireConnection.NONE);
    }

    @Inject(method = "getDefaultWireState", at = @At("RETURN"), cancellable = true)
    private void getDefaultWireState(BlockView world, BlockState state, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        state = cir.getReturnValue();
        boolean emptyAbove = !world.getBlockState(pos.up()).isSolidBlock(world, pos);
        for (Direction4 direction : Direction4.WDIRECTIONS) {
            if (state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY4.get(direction)).isConnected()) continue;
            WireConnection wireConnection = getRenderConnectionType4(world, pos, direction, emptyAbove);
            state = state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY4.get(direction), wireConnection);
        }

        cir.setReturnValue(state);
    }

    //look this could be injected but have you considered I'm really lazy and this is easier k thnx <3
    @Inject(method = "getPlacementState(Lnet/minecraft/world/BlockView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", at = @At("HEAD"), cancellable = true)
    private void getPlacementState(BlockView world, BlockState state, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        boolean wasNotConnected = isNotConnected(state);
        state = getDefaultWireState(world, getDefaultState().with(POWER, state.get(POWER)), pos);
        if (wasNotConnected && isNotConnected(state)) {
            cir.setReturnValue(state);
        }
        boolean connectedNorth = state.get(WIRE_CONNECTION_NORTH).isConnected();
        boolean connectedSouth = state.get(WIRE_CONNECTION_SOUTH).isConnected();
        boolean connectedEast = state.get(WIRE_CONNECTION_EAST).isConnected();
        boolean connectedWest = state.get(WIRE_CONNECTION_WEST).isConnected();
        boolean connectedAna = state.get(WIRE_CONNECTION_ANA).isConnected();
        boolean connectedKata = state.get(WIRE_CONNECTION_KATA).isConnected();
        boolean shouldConnectByDefaultZ = !connectedKata  && !connectedAna   && !connectedEast  && !connectedWest;
        boolean shouldConnectByDefaultX = !connectedNorth && !connectedSouth && !connectedAna   && !connectedKata;
        boolean shouldConnectByDefaultW = !connectedEast  && !connectedWest  && !connectedNorth && !connectedSouth;
        if(shouldConnectByDefaultZ) {
            if (!connectedNorth) {
                state = state.with(WIRE_CONNECTION_NORTH, WireConnection.SIDE);
            }
            if (!connectedSouth) {
                state = state.with(WIRE_CONNECTION_SOUTH, WireConnection.SIDE);
            }
        }
        if(shouldConnectByDefaultX){
            if (!connectedWest) {
                state = state.with(WIRE_CONNECTION_WEST, WireConnection.SIDE);
            }
            if (!connectedEast) {
                state = state.with(WIRE_CONNECTION_EAST, WireConnection.SIDE);
            }
        }
        if(shouldConnectByDefaultW) {
            if (!connectedAna) {
                state = state.with(WIRE_CONNECTION_ANA, WireConnection.SIDE);
            }
            if (!connectedKata) {
                state = state.with(WIRE_CONNECTION_KATA, WireConnection.SIDE);
            }
        }
        cir.setReturnValue(state);
    }


    @Inject(method = "isFullyConnected", at = @At("RETURN"), cancellable = true)
    private static void isFullyConnected(BlockState state, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(cir.getReturnValue() && state.get(WIRE_CONNECTION_KATA).isConnected() && state.get(WIRE_CONNECTION_ANA).isConnected());
    }

    @Inject(method = "isNotConnected", at = @At("RETURN"), cancellable = true)
    private static void isNotConnected(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() && !state.get(WIRE_CONNECTION_KATA).isConnected() && !state.get(WIRE_CONNECTION_ANA).isConnected());
    }

    private WireConnection getRenderConnectionType4(BlockView world, BlockPos pos, Direction4 direction, boolean isEmptyAbove) {
        BlockPos blockPos = pos.add(direction.getVec3());
        BlockState blockState = world.getBlockState(blockPos);
        boolean canRunOnTop = canRunOnTop(world, blockPos, blockState);
        if (isEmptyAbove && canRunOnTop && connectsTo(world.getBlockState(blockPos.up()))) {
            //if (blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite())) {
                return WireConnection.UP;
            //}
            //return WireConnection.SIDE;
        }
        if (connectsTo(blockState) || !blockState.isSolidBlock(world, blockPos) && connectsTo(world.getBlockState(blockPos.down()))) {
            return WireConnection.SIDE;
        }
        return WireConnection.NONE;
    }

    @Inject(method = "getReceivedRedstonePower", at = @At("RETURN"), cancellable = true)
    private void afterGetReceivedRedstonePower(World world, BlockPos pos, CallbackInfoReturnable<Integer> cir){
        wiresGivePower = false;
        int i = world.getReceivedRedstonePower(pos);
        wiresGivePower = true;
        int j = cir.getReturnValueI() + 1;
        if(i >= 15){
            return;
        }

        for(Direction4 dir : Direction4.WDIRECTIONS){
            BlockPos blockPos = pos.add(dir.getVec3());
            BlockState blockState = world.getBlockState(blockPos);
            j = Math.max(j, increasePower(blockState));
            BlockPos blockPos2 = pos.up();
            if (blockState.isSolidBlock(world, blockPos) && !world.getBlockState(blockPos2).isSolidBlock(world, blockPos2)) {
                j = Math.max(j, this.increasePower(world.getBlockState(blockPos.up())));
                continue;
            }
            if (blockState.isSolidBlock(world, blockPos)) continue;
            j = Math.max(j, this.increasePower(world.getBlockState(blockPos.down())));
        }
        cir.setReturnValue(Math.max(i, j - 1));
    }

    @Inject(method = "getWireColor", at = @At("RETURN"), cancellable = true)
    private static void getWireColor(int powerLevel, CallbackInfoReturnable<Integer> cir) {

    }

    @Inject(method = "appendProperties", at = @At("TAIL"))
    protected void appendProperties4D(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(WIRE_CONNECTION_KATA, WIRE_CONNECTION_ANA);
    }

}
