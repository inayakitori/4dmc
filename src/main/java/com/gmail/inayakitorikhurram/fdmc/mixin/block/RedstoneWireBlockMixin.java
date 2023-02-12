package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.RedstoneWireBlockI;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
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


    //TODO use different textures
    @Shadow @Final private static final Vec3d[] COLORS = Util.make(new Vec3d[64], vec3ds -> {
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

    @Redirect(method = {"getDefaultWireState", "getReceivedRedstonePower"}, at = @At(value= "FIELD", target = "Lnet/minecraft/util/math/Direction$Type;HORIZONTAL:Lnet/minecraft/util/math/Direction$Type;"))
    private Direction.Type fdmc$getDefaultWireStateModifyHorizontal() {
        return Direction4Constants.Type4.HORIZONTAL4;
    }


//    @Override
//    public BlockState getStateForNeighborUpdate(BlockState state, Direction4 dir, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
//        if (dir == Direction4Constants.DOWN) {
//            return state;
//        }
//        if (dir == Direction4Constants.UP) {
//            return this.getPlacementState(world, state, pos);
//        }
//        WireConnection wireConnection = getRenderConnectionType4(world, pos, dir);
//        if (wireConnection.isConnected() == state.get(WIRE_CONNECTION_MAP.get(dir)).isConnected() && !RedstoneWireBlockI.isFullyConnected4(state)) {
//            return state.with(WIRE_CONNECTION_MAP.get(dir), wireConnection);
//        }
//        return this.getPlacementState(world, this.dotState.with(POWER, state.get(POWER)).with(WIRE_CONNECTION_MAP.get(dir), wireConnection), pos);
//    }

    //look this could be injected but have you considered I'm really lazy and this is easier k thnx <3
    @Inject(method = "getPlacementState(Lnet/minecraft/world/BlockView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", at = @At("HEAD"), cancellable = true)
    private void getPlacementState(BlockView world, BlockState state, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        boolean wasNotConnected = isNotConnected(state);
        state = getDefaultWireState(world, getDefaultState().with(POWER, state.get(POWER)), pos);
        if (wasNotConnected && isFullyConnected(state)) {
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
//    @Inject(method = "update", at = @At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z", ordinal = 0, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
//    private void injected(World world, BlockPos pos, BlockState state, CallbackInfo ci, int i, Set<BlockPos> set) {
//        for (Direction4 direction : Direction4Constants.WDIRECTIONS) {
//            set.add(pos.add(direction.getVec3()));
//        }
//    }


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
//
//    @Inject(method = "prepare", at = @At("RETURN"))
//    public void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth, CallbackInfo ci) {
//        BlockPos.Mutable mutable = new BlockPos.Mutable();
//        for (Direction4 dir : Direction4Constants.WDIRECTIONS) {
//            WireConnection wireConnection = state.get(WIRE_CONNECTION_MAP.get(dir));
//            boolean isClient = world.getBlockState(mutable).isOf(Blocks.VOID_AIR);
//            mutable.set(pos, dir.getVec3());
//            if (wireConnection == WireConnection.NONE || world.getBlockState(mutable).isOf((Block)(Object)this)) continue;
//            mutable.move(Direction.DOWN);
//            BlockState blockState = world.getBlockState(mutable);
//            if (blockState.isOf((Block)(Object)this) || isClient) {
//                BlockPos blockPos = mutable.add(dir.getOpposite().getVec3());
//                ((WorldAccessI)world).replaceWithStateForNeighborUpdate(dir.getOpposite(), world.getBlockState(blockPos), mutable, blockPos, flags, maxUpdateDepth);
//            }
//            mutable.set(pos, dir.getVec3()).move(Direction.UP);
//            BlockState blockState2 = world.getBlockState(mutable);
//            if (!blockState2.isOf((Block)(Object)this) || !isClient) continue;
//            BlockPos blockPos2 = mutable.add(dir.getOpposite().getVec3());
//            ((WorldAccessI)world).replaceWithStateForNeighborUpdate(dir.getOpposite(), world.getBlockState(blockPos2), mutable, blockPos2, flags, maxUpdateDepth);
//        }
//    }

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

//    @Inject(method = "onStateReplaced", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/RedstoneWireBlock;update(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", shift = At.Shift.BEFORE))
//    private void after3DirectionStatesReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved, CallbackInfo ci){
//        for (Direction4 dir : Direction4Constants.WDIRECTIONS) {
//            world.updateNeighborsAlways(pos.add(dir.getVec3()), (Block)(Object)this);
//        }
//    }
//
//    @Inject(method = "updateNeighbors", at = @At("TAIL"))
//    private void updateNeighbors(World world, BlockPos pos, CallbackInfo ci) {
//        for (Direction4 dir : Direction4Constants.WDIRECTIONS) {
//            world.updateNeighborsAlways(pos.add(dir.getVec3()), (Block)(Object)this);
//        }
//    }
//
//
//    @Inject(method = "updateOffsetNeighbors", at = @At("HEAD"))
//    private void updateOffsetNeighborsStart(World world, BlockPos pos, CallbackInfo ci) {
//        for (Direction4 dir : Direction4Constants.WDIRECTIONS) {
//            updateNeighbors(world, pos.add(dir.getVec3()));
//        }
//    }
//
//
//    @Inject(method = "updateOffsetNeighbors", at = @At("TAIL"))
//    private void updateOffsetNeighborsEnd(World world, BlockPos pos, CallbackInfo ci) {
//        for (Direction4 dir : Direction4Constants.WDIRECTIONS) {
//            BlockPos blockPos = pos.add(dir.getVec3());
//            if (world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
//                this.updateNeighbors(world, blockPos.up());
//                continue;
//            }
//            this.updateNeighbors(world, blockPos.down());
//        }
//    }


    @Inject(method = "appendProperties", at = @At("TAIL"))
    protected void appendProperties4D(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(KATA_WIRE_CONNECTION, ANA_WIRE_CONNECTION);
    }

//    @Inject(method = "updateForNewState",at = @At("RETURN"))
//    private void updateForNewState(World world, BlockPos pos, BlockState oldState, BlockState newState, CallbackInfo ci) {
//        for (Direction4 dir : Direction4Constants.WDIRECTIONS) {
//            BlockPos blockPos = pos.add(dir.getVec3());
//            if (oldState.get(WIRE_CONNECTION_MAP.get(dir)).isConnected() == newState.get(WIRE_CONNECTION_MAP.get(dir)).isConnected() || !world.getBlockState(blockPos).isSolidBlock(world, blockPos)) continue;
//            ((WorldAccessI)world).updateNeighborsExcept(blockPos, newState.getBlock(), dir.getOpposite());
//        }
//    }

//    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
//    public void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
//        if (!player.getAbilities().allowModifyWorld) {
//            cir.setReturnValue(ActionResult.PASS);
//            cir.cancel();
//            return;
//        }
//        if(world.isClient){
//            cir.setReturnValue(ActionResult.SUCCESS);
//            cir.cancel();
//            return;
//        }
//        if (RedstoneWireBlockI.isFullyConnected4(state) || RedstoneWireBlockI.isNotConnected4(state)) {
//            BlockState blockState = RedstoneWireBlockI.isFullyConnected4(state) ? getDefaultState() : this.dotState;
//            blockState = blockState.with(POWER, state.get(POWER));
//            //blockState = this.getPlacementState(world, blockState, pos);
//            if (blockState != state) {
//                world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
//                updateForNewState(world, pos, state, blockState);
//                cir.setReturnValue(ActionResult.SUCCESS);
//                cir.cancel();
//                return;
//            }
//        }
//        cir.setReturnValue(ActionResult.PASS);
//        cir.cancel();
//    }

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
