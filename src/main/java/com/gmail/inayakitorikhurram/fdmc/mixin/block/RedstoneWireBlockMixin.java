package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.RedstoneWireBlockI;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.*;
import static net.minecraft.state.property.Properties.*;

@Mixin(RedstoneWireBlock.class)
abstract
class RedstoneWireBlockMixin
        extends BlockMixin {

    @Shadow protected abstract int increasePower(BlockState state);
    @Shadow private boolean wiresGivePower;
    @Shadow
    protected static boolean connectsTo(BlockState state) {
        return false;
    }
    @Shadow protected abstract boolean canRunOnTop(BlockView world, BlockPos pos, BlockState floor);
    @Shadow protected abstract BlockState getDefaultWireState(BlockView world, BlockState state, BlockPos pos);
    @Mutable
    @Shadow @Final private BlockState dotState;
    @Shadow protected abstract void updateNeighbors(World world, BlockPos pos);
    @Shadow protected abstract BlockState getPlacementState(BlockView world, BlockState state, BlockPos pos);
    @Shadow protected abstract void updateForNewState(World world, BlockPos pos, BlockState oldState, BlockState newState);

    @Shadow @Final @Mutable
    private static final Map<Direction, VoxelShape> DIRECTION_TO_SIDE_SHAPE = Maps.newHashMap(ImmutableMap.of(
            Direction4Constants.NORTH, Block.createCuboidShape( 3.0,  0.0,  0.0, 13.0, 1.0, 13.0),
            Direction4Constants.SOUTH, Block.createCuboidShape( 3.0,  0.0,  3.0, 13.0, 1.0, 16.0),
            Direction4Constants.EAST , Block.createCuboidShape( 3.0,  0.0,  3.0, 16.0, 1.0, 13.0),
            Direction4Constants.WEST , Block.createCuboidShape( 0.0,  0.0,  3.0, 13.0, 1.0, 13.0),
            Direction4Constants.KATA , Block.createCuboidShape( 0.0,  0.0,  0.0,  4.0, 1.0,  4.0),
            Direction4Constants.ANA  , Block.createCuboidShape( 12.0,  0.0, 12.0,16.0, 1.0, 16.0)
    ));

    @Shadow @Final @Mutable
    private static final Map<Direction, VoxelShape> DIRECTION_TO_UP_SHAPE = Maps.newHashMap(ImmutableMap.of(
            Direction4Constants.NORTH, VoxelShapes.union(DIRECTION_TO_SIDE_SHAPE.get(Direction4Constants.NORTH), Block.createCuboidShape( 3.0, 0.0,  0.0, 13.0, 16.0,  1.0 )),
            Direction4Constants.SOUTH, VoxelShapes.union(DIRECTION_TO_SIDE_SHAPE.get(Direction4Constants.SOUTH), Block.createCuboidShape( 3.0, 0.0, 15.0, 13.0, 16.0, 16.0 )),
            Direction4Constants.EAST , VoxelShapes.union(DIRECTION_TO_SIDE_SHAPE.get(Direction4Constants.EAST ), Block.createCuboidShape(15.0, 0.0,  3.0, 16.0, 16.0, 13.0 )),
            Direction4Constants.WEST , VoxelShapes.union(DIRECTION_TO_SIDE_SHAPE.get(Direction4Constants.WEST ), Block.createCuboidShape( 0.0, 0.0,  3.0,  1.0, 16.0, 13.0 )),
            Direction4Constants.KATA , VoxelShapes.union(DIRECTION_TO_SIDE_SHAPE.get(Direction4Constants.KATA ), Block.createCuboidShape( 0.0, 0.0,  0.0,  4.0, 8.0,  4.0  )),
            Direction4Constants.ANA  , VoxelShapes.union(DIRECTION_TO_SIDE_SHAPE.get(Direction4Constants.ANA  ), Block.createCuboidShape(12.0, 0.0, 12.0, 16.0, 8.0,  16.0 ))
    ));

    //make **everything** use this repeater property instead
    @Redirect(method = "connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z", at=@At(value = "FIELD", target = "Lnet/minecraft/block/RepeaterBlock;FACING:Lnet/minecraft/state/property/DirectionProperty;"))
    private static DirectionProperty fdmc$redirectFacingProperty(){
        return HORIZONTAL_FACING4;
    }
    //use HORIZONTAL4
    @Redirect(
            method = {
                    "getDefaultWireState",
                    "prepare",
                    "getReceivedRedstonePower",
                    "updateOffsetNeighbors",
                    "updateForNewState",
            },
            at=@At(
                    value = "FIELD",
                    target = "Lnet/minecraft/util/math/Direction$Type;HORIZONTAL:Lnet/minecraft/util/math/Direction$Type;"
            )
    )
    private Direction.Type fdmc$redirectToHorizontal4(){
        return Direction4Constants.Type4.HORIZONTAL4;
    }

    @Redirect(
            method = {
                    "update",
                    "onStateReplaced",
                    "updateNeighbors",
            },
            at=@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"
            )
    )
    //use HORIZONTAL4
    private Direction[] fdmc$redirectToValues4(){
        return Direction4Constants.VALUES;
    }


    //TODO use different textures

    //this is literally just the minecraft one but doesn't get messed up by the new variants
    @Shadow @Final @Mutable
    private static final Vec3d[] COLORS =
            Util.make(new Vec3d[64], colors -> {
                for (int i = 0; i < 64; ++i) {
                    float f  = (float) (i & 0xF) / 15.0f;
                    float g = f * 0.6f + (f > 0.0f ? 0.4f : 0.3f);
                    float h = MathHelper.clamp(f * f * 0.7f - 0.5f, 0.0f, 1.0f);
                    float j = MathHelper.clamp(f * f * 0.6f - 0.7f, 0.0f, 1.0f);
                    colors[i] = new Vec3d(g, h, j);
                }
            });
    /* old code
            Util.make(new Vec3d[64], vec3ds -> {
        for(int kata = 0; kata < 2; kata++) {
            for(int ana = 0; ana < 2; ana++) {
                for (int i = 0; i <= 15; ++i) {

                    float hue = kata == 1? (ana == 1? 0.7f : 0.85f) : (ana == 1? 0.6f : 0f);
                    float brightness = (kata + ana) * 0.1f;

                    Vec3d connection_based_color;
                    if(kata == 0) { //no kata connection
                        if(ana == 0) {//no ana connection -> default
                            connection_based_color = Direction4Constants.EAST4.getColor();
                        } else{ //ana only
                            connection_based_color = Direction4Constants.ANA4.getColor();
                        }
                    } else{
                        if(ana == 0) {//kata only
                            connection_based_color = Direction4Constants.KATA4.getColor();
                        } else{ //both
                            connection_based_color = Direction4Constants.WEST4.getColor();
                        }
                    }

                    double power_factor = i / 15.0;

                    connection_based_color = connection_based_color.multiply(power_factor * 0.5 + 1.0);

                    Vec3d color_added_from_power = new Vec3d(0.2f, 0.2f, 0.2f).multiply(power_factor * power_factor);

                    Vec3d power_included_color = i > 0? connection_based_color.add(color_added_from_power) : connection_based_color.subtract(0.4, 0.4, 0.4);

                    power_included_color = power_included_color.withAxis(Direction.Axis.X, MathHelper.clamp(power_included_color.x, 0, 255./256.));
                    power_included_color = power_included_color.withAxis(Direction.Axis.Y, MathHelper.clamp(power_included_color.y, 0, 255./256.));
                    power_included_color = power_included_color.withAxis(Direction.Axis.Z, MathHelper.clamp(power_included_color.z, 0, 255./256.));

                    vec3ds[i | (kata<<4) | (ana<<5)] = power_included_color;
                }
            }
        }
    });
    */

    @Shadow @Final public static IntProperty POWER;

    @Shadow protected abstract WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction);

    @Shadow
    private static boolean isNotConnected(BlockState state) {
        return false;
    }

    @Shadow
    private static boolean isFullyConnected(BlockState state) {
        return false;
    }

    @Mutable
    @Shadow @Final public static Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY = Maps.newHashMap(ImmutableMap.of(
            Direction4Constants.NORTH, NORTH_WIRE_CONNECTION,
            Direction4Constants.EAST,  EAST_WIRE_CONNECTION,
            Direction4Constants.SOUTH, SOUTH_WIRE_CONNECTION,
            Direction4Constants.WEST,  WEST_WIRE_CONNECTION,
            Direction4Constants.KATA,  KATA_WIRE_CONNECTION,
            Direction4Constants.ANA,   ANA_WIRE_CONNECTION
    ));


    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;setDefaultState(Lnet/minecraft/block/BlockState;)V"))//set default state to not have kata/ana up
    private BlockState injectedDefaultState(BlockState defaultState){
        return defaultState.with(KATA_WIRE_CONNECTION, WireConnection.NONE).with(ANA_WIRE_CONNECTION, WireConnection.NONE);
    }
    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/block/RedstoneWireBlock;dotState:Lnet/minecraft/block/BlockState;", opcode = Opcodes.PUTFIELD))//set default state to not have kata/ana up
    private void injectedDotState(RedstoneWireBlock instance, BlockState state){
        dotState = state.with(KATA_WIRE_CONNECTION, WireConnection.SIDE).with(ANA_WIRE_CONNECTION, WireConnection.SIDE);
    }

    //look this could be injected but have you considered I'm really lazy and this is easier k thnx <3
    @Inject(method = "getPlacementState(Lnet/minecraft/world/BlockView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", at = @At("HEAD"), cancellable = true)
    private void getPlacementState(BlockView world, BlockState state, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        boolean wasNotConnected = RedstoneWireBlockI.isNotConnected4(state);
        state = getDefaultWireState(world, getDefaultState().with(POWER, state.get(POWER)), pos);
        if (wasNotConnected && RedstoneWireBlockI.isNotConnected4(state)) {
            cir.setReturnValue(state);
            cir.cancel();
            return;
        }
        boolean connectedNorth = state.get(NORTH_WIRE_CONNECTION).isConnected();
        boolean connectedSouth = state.get(SOUTH_WIRE_CONNECTION).isConnected();
        boolean connectedEast = state.get(EAST_WIRE_CONNECTION).isConnected();
        boolean connectedWest = state.get(WEST_WIRE_CONNECTION).isConnected();
        boolean connectedAna = state.get(ANA_WIRE_CONNECTION).isConnected();
        boolean connectedKata = state.get(KATA_WIRE_CONNECTION).isConnected();
        boolean shouldConnectByDefaultZ = !connectedKata  && !connectedAna   && !connectedEast  && !connectedWest;
        boolean shouldConnectByDefaultX = !connectedNorth && !connectedSouth && !connectedAna   && !connectedKata;
        boolean shouldConnectByDefaultW = !connectedEast  && !connectedWest  && !connectedNorth && !connectedSouth;
        if(shouldConnectByDefaultZ) {
            if (!connectedNorth) {
                state = state.with(NORTH_WIRE_CONNECTION, WireConnection.SIDE);
            }
            if (!connectedSouth) {
                state = state.with(SOUTH_WIRE_CONNECTION, WireConnection.SIDE);
            }
        }
        if(shouldConnectByDefaultX){
            if (!connectedWest) {
                state = state.with(WEST_WIRE_CONNECTION, WireConnection.SIDE);
            }
            if (!connectedEast) {
                state = state.with(EAST_WIRE_CONNECTION, WireConnection.SIDE);
            }
        }
        if(shouldConnectByDefaultW) {
            if (!connectedAna) {
                state = state.with(ANA_WIRE_CONNECTION, WireConnection.SIDE);
            }
            if (!connectedKata) {
                state = state.with(KATA_WIRE_CONNECTION, WireConnection.SIDE);
            }
        }
        cir.setReturnValue(state);
        cir.cancel();
    }

    @Inject(method = "isFullyConnected", at = @At("HEAD"), cancellable = true)
    private static void isFullyConnected(BlockState state, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(RedstoneWireBlockI.isFullyConnected4(state));
        cir.cancel();
    }


    @Inject(method = "isNotConnected", at = @At("HEAD"), cancellable = true)
    private static void isNotConnected(BlockState state, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(RedstoneWireBlockI.isNotConnected4(state));
        cir.cancel();
    }

    private WireConnection getRenderConnectionType4(BlockView world, BlockPos pos, Direction direction, boolean isEmptyAbove) {
        BlockPos blockPos = pos.offset(direction);
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


    @Inject(method = "appendProperties", at = @At("TAIL"))
    protected void appendProperties4D(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(KATA_WIRE_CONNECTION, ANA_WIRE_CONNECTION);
    }


    @Inject(method = "randomDisplayTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;get(Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    private void addPoweredParticles(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        if(state.get(POWER) == 0){
            ci.cancel();
        }
    }

    @Redirect(method = "randomDisplayTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;get(Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;", ordinal = 0))
    private Comparable changedColorIndex(BlockState state, Property property) {
        return FDMCMath.getRedstoneColorIndex(state);
    }

}
