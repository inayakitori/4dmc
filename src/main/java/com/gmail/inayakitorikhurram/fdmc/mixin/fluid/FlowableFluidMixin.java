package com.gmail.inayakitorikhurram.fdmc.mixin.fluid;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(FlowableFluid.class)
public abstract class FlowableFluidMixin {

    private static final Logger LOGGER = FDMCConstants.LOGGER;

    @Shadow protected abstract Map<Direction, FluidState> getSpread(World world, BlockPos pos, BlockState state);
    @Shadow protected abstract FluidState getUpdatedState(World world, BlockPos pos, BlockState state);
    @Shadow protected abstract boolean canFlowThrough(BlockView world, Fluid fluid, BlockPos pos, BlockState state, Direction face, BlockPos fromPos, BlockState fromState, FluidState fluidState);
    @Shadow protected abstract boolean canFlowDownTo(BlockView world, Fluid fluid, BlockPos pos, BlockState state, BlockPos fromPos, BlockState fromState);
    @Shadow public abstract Fluid getFlowing();
    @Shadow protected abstract int getFlowSpeedBetween(WorldView world, BlockPos blockPos, int i, Direction direction, BlockState blockState, BlockPos blockPos2, Short2ObjectMap<Pair<BlockState, FluidState>> short2ObjectMap, Short2BooleanMap short2BooleanMap);
    @Shadow protected abstract int getFlowSpeed(WorldView var1);
    @Shadow protected abstract int getLevelDecreasePerBlock(WorldView var1);
    @Shadow public abstract FluidState getFlowing(int level, boolean falling);
    //useful for gamerules

    private World world;

    //method_15747 does the displacement to short conversion?
    //the issue there is that the max displacement away it'll calculate is 7 blocks in any direction,
    //which is just too much info to store in a short (8 x 3 = 24 bits, need at least an int)
    //so int maps are used everywhere instead
    //at the cost of performance
    private static int displacementToInt(BlockPos blockPos, BlockPos blockPos2) {
        BlockPos displacement = blockPos2.subtract(blockPos);
        int dx = ((BlockPos4<?, ?>)displacement).getX4();
        int dz = ((BlockPos4<?, ?>)displacement).getZ4();
        int dw = ((BlockPos4<?, ?>)displacement).getW4();
        //displacement has 15 values in each direction so needs to be stored in 24 bits
        return
                (dx + (1<<7) & 0xFF)   << 16  |
                        (dz + (1<<7) & 0xFF)   << 8   |
                        (dw + (1<<7) & 0xFF) /*<< 0*/ ;
    }

    //if gamerule wFluidFlow is false just do normal directions
    private Direction.Type getFlowDirections(World world) {
        if(this.world == null || this.world.getGameRules().getBoolean(FDMCConstants.FLUID_SCALE_W)) {
            return Direction4Constants.Type4.HORIZONTAL4;
        } else{
            return Direction.Type.HORIZONTAL;
        }
    }

    @Redirect(
            method = {
                    "getUpdatedState"
            },
            at= @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/util/math/Direction$Type;HORIZONTAL:Lnet/minecraft/util/math/Direction$Type;"
            )
    )
    private Direction.Type modifyHorizontalAxis(){
        return getFlowDirections(world);
    }

    //this could be like 7 micro-injections but it's easier to just rewrite the whole thing :p
    @Inject(method = "getSpread", at = @At("HEAD"), cancellable = true)
    protected void getSpread(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Map<Direction, FluidState>> cir) {
        if(this.world == null) {
            this.world = world;
        }
        int i = 1000;
        //have to use the hashmap instead of direction enum map
        HashMap<Direction, FluidState> map = Maps.newHashMap();
        Int2ObjectOpenHashMap<Pair<BlockState, FluidState>> int2StateMap = new Int2ObjectOpenHashMap<>();
        Int2BooleanOpenHashMap int2ShouldFlowMap = new Int2BooleanOpenHashMap();
        for (Direction direction : getFlowDirections(world)) {
            BlockPos blockPos = pos.offset(direction);
            int displacementHashed = displacementToInt(pos, blockPos);
            Pair<BlockState, FluidState> pair = int2StateMap.computeIfAbsent(displacementHashed, s -> {
                BlockState blockState = world.getBlockState(blockPos);
                return Pair.of(blockState, blockState.getFluidState());
            });
            BlockState blockState = pair.getFirst();
            FluidState fluidState = pair.getSecond();
            FluidState fluidState2 = getUpdatedState(world, blockPos, blockState);
            if (!canFlowThrough(world, fluidState2.getFluid(), pos, state, direction, blockPos, blockState, fluidState)) continue;
            BlockPos blockPos2 = blockPos.down();
            boolean shouldFlow = int2ShouldFlowMap.computeIfAbsent(displacementHashed, s -> {
                BlockState blockState2 = world.getBlockState(blockPos2);
                return canFlowDownTo(world, getFlowing(), blockPos, blockState, blockPos2, blockState2);
            });
            int j = shouldFlow ? 0 : getFlowSpeedBetween4(world, blockPos, 1, direction.getOpposite(), blockState, pos, int2StateMap, int2ShouldFlowMap);
            if (j < i) {
                map.clear();
            }
            if (j > i) continue;
            map.put(direction, fluidState2);
            i = j;
        }
        cir.setReturnValue(map);
        cir.cancel();
    }


    //base method should move into method4
    @Inject(method = "getFlowSpeedBetween", at = @At("HEAD"), cancellable = true)
    private void getFlowSpeedBetween3(WorldView world, BlockPos blockPos, int i, Direction direction, BlockState blockState, BlockPos blockPos2, Short2ObjectMap<Pair<BlockState, FluidState>> short2ObjectMap, Short2BooleanMap short2BooleanMap, CallbackInfoReturnable<Integer> cir){

        //mapping to int maps
        Int2ObjectOpenHashMap<Pair<BlockState, FluidState>> int2StateMap = new Int2ObjectOpenHashMap<>();
        short2ObjectMap.forEach(int2StateMap::put);
        Int2BooleanOpenHashMap int2ShouldFlowMap = new Int2BooleanOpenHashMap();
        short2BooleanMap.forEach(int2ShouldFlowMap::put);
        cir.setReturnValue(getFlowSpeedBetween4(world, blockPos, i, direction, blockState, blockPos2, int2StateMap, int2ShouldFlowMap));
    }

    protected int getFlowSpeedBetween4(WorldView world, BlockPos startPos, int i, Direction direction, BlockState endState, BlockPos endPos, Int2ObjectMap<Pair<BlockState, FluidState>> int2StateMap, Int2BooleanMap int2ShouldFlowMap) {
        int j = 1000;
        for (Direction direction2 : Direction4Constants.Type4.HORIZONTAL4) {
            int k;
            if (direction2 == direction) continue;
            BlockPos blockPos3 = startPos.offset(direction2);
            int displacementHashed = displacementToInt(endPos, blockPos3);
            Pair<BlockState, FluidState> pair = int2StateMap.computeIfAbsent(displacementHashed, s -> {
                BlockState blockState = world.getBlockState(blockPos3);
                return Pair.of(blockState, blockState.getFluidState());
            });
            BlockState newBlockState = pair.getFirst();
            FluidState newFluidState = pair.getSecond();
            if (!this.canFlowThrough(world, this.getFlowing(), startPos, endState, direction2, blockPos3, newBlockState, newFluidState)) continue;
            boolean shouldFlow = int2ShouldFlowMap.computeIfAbsent(displacementHashed, s -> {
                BlockPos blockPos2 = blockPos3.down();
                BlockState blockState2 = world.getBlockState(blockPos2);
                return this.canFlowDownTo(world, this.getFlowing(), blockPos3, blockState2, blockPos2, blockState2);
            });
            if (shouldFlow) {
                return i;
            }
            if (i >= getFlowSpeed(world) || (k = this.getFlowSpeedBetween4(world, blockPos3, i + 1, direction2.getOpposite(), newBlockState, endPos, int2StateMap, int2ShouldFlowMap)) >= j) continue;
            j = k;
        }
        return j;
    }


}
