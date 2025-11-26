package com.gmail.inayakitorikhurram.fdmc.mixin.fluid;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Map;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.EnclosingInstanceAccess;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FlowableFluid.SpreadCache;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.EnumMap;
import java.util.Map;

@Mixin(FlowableFluid.class)
public abstract class FlowableFluidMixin extends Fluid{

    private static final Logger LOGGER = FDMCConstants.LOGGER;

    @Shadow protected abstract Map<Direction, FluidState> getSpread(ServerWorld world, BlockPos pos, BlockState state);
    @Shadow protected abstract FluidState getUpdatedState(ServerWorld world, BlockPos pos, BlockState state);
    @Shadow protected abstract boolean canFlowThrough(BlockView world, Fluid fluid, BlockPos pos, BlockState state, Direction face, BlockPos fromPos, BlockState fromState, FluidState fluidState);
    @Shadow protected abstract boolean canFlowDownTo(BlockView world, BlockPos pos, BlockState state, BlockPos fromPos, BlockState fromState);
    @Shadow public abstract Fluid getFlowing();
    @Shadow protected abstract int getMinFlowDownDistance(WorldView world, BlockPos pos, int i, Direction direction, BlockState state, SpreadCache spreadCache);
    @Shadow protected abstract int getMaxFlowDistance(WorldView var1);
    @Shadow protected abstract int getLevelDecreasePerBlock(WorldView var1);
    @Shadow public abstract FluidState getFlowing(int level, boolean falling);

    //method_15747 does the displacement to short conversion?
    //the issue there is that the max displacement away it'll calculate is 7 blocks in any direction,
    //which is just too much info to store in a short (8 x 3 = 24 bits, need at least an int)
    //so int maps are used everywhere instead
    //at the cost of performance



    @Redirect(
            method = {
                    "getUpdatedState",
                    "countNeighboringSources",
                    "getMinFlowDownDistance",
                    "getSpread"
            },
            at= @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/util/math/Direction$Type;HORIZONTAL:Lnet/minecraft/util/math/Direction$Type;",
                    opcode = Opcodes.GETSTATIC)
    )
    private Direction.Type modifyHorizontalAxis(){
        return Direction4Constants.Type4.HORIZONTAL4;
    }

    @Inject(method = "getSpread", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/fluid/FlowableFluid$SpreadCache;canFlowDownTo(Lnet/minecraft/util/math/BlockPos;)Z",
            shift = At.Shift.BEFORE),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void passEnclosingClass(ServerWorld world, BlockPos pos, BlockState state, CallbackInfoReturnable<Map<Direction, FluidState>> cir, @Local SpreadCache cache){
        FlowableFluid enclosingInstance = (FlowableFluid) (Object) this;

        ((EnclosingInstanceAccess<FlowableFluid>) (Object) cache).setEnclosingInstance(enclosingInstance);
    }

    @Redirect(method = "getSpread", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Maps;newEnumMap(Ljava/lang/Class;)Ljava/util/EnumMap;"))
    protected EnumMap<Direction, Fluid> getFlowSpeedBetween4(Class<Direction> type) {
        return new Direction4Map<>();
    }
//
//    @Unique
//    int worldFluidLevelDecrease;
//    @Inject(method = "getUpdatedState", at = @At("HEAD"))
//    private void storeWorld(ServerWorld world, BlockPos pos, BlockState state, CallbackInfoReturnable<FluidState> cir){
//        worldFluidLevelDecrease = this.getLevelDecreasePerBlock(world);
//    }
//
//    @Unique
//    int dropOffCoefficient = 1;
//    @Redirect(method = "getUpdatedState", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I"))
//    private int storeDropoffAmount(int i_max, int i_new, @Local Direction direction){
//        if (direction.getAxis() == Direction4Constants.Axis4Constants.W) {
//            if (i_new - i_max > worldFluidLevelDecrease) {
//                // fluid would be highest when coming from the w sides
//                dropOffCoefficient = 2;
//                return i_new;
//            } else  {
//                // Don't use this w side
//                dropOffCoefficient = 1;
//                return i_max;
//            }
//        } else {
//            // fluid would be highest when coming from xz sides
//            dropOffCoefficient = 1;
//            return Math.max(i_max, i_new);
//        }
//    }
//
//    @Redirect(method = "getUpdatedState", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FlowableFluid;getLevelDecreasePerBlock(Lnet/minecraft/world/WorldView;)I"))
//    private int ModifyFluidHeight(FlowableFluid fluid, WorldView worldView){
//        return dropOffCoefficient * worldFluidLevelDecrease;
//    }

    @Mixin(targets = "net.minecraft.fluid.FlowableFluid$SpreadCache")
    public abstract static class SpreadCacheMixin implements EnclosingInstanceAccess<FlowableFluid> {
        private FlowableFluid this$0;
        @Shadow
        private final BlockView world;
        @Shadow
        private final BlockPos startPos;
        private final Int2ObjectMap<BlockState> stateCacheInt = new Int2ObjectOpenHashMap<>();
        private final Int2BooleanMap flowDownCacheInt = new Int2BooleanOpenHashMap();

        @Override
        public void setEnclosingInstance(FlowableFluid enclosingInstance) {
            this$0 = enclosingInstance;
        }

        @Override
        public FlowableFluid getEnclosingInstance() {
            return this$0;
        }

        protected SpreadCacheMixin(BlockView world, BlockPos startPos) {
            this.world = world;
            this.startPos = startPos;
        }

        @Inject(method = "<init>", at =@At("TAIL"))
        private void SpreadCache(CallbackInfo ci){

        }

        @Inject(method = "getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", at = @At("HEAD"), cancellable=true)
        private void fdmc$getBlockState(BlockPos pos, CallbackInfoReturnable<BlockState> cir){
            cir.setReturnValue(getBlockState4(pos, pack4(pos)));
            cir.cancel();
        }

        @Unique
        private BlockState getBlockState4(BlockPos pos, int packed) {
            return (BlockState)stateCacheInt.computeIfAbsent(packed, (packedPos) -> world.getBlockState(pos));
        }

        @Inject(method = "canFlowDownTo(Lnet/minecraft/util/math/BlockPos;)Z", at = @At("HEAD"), cancellable=true)
        private void canFlowDownTo(BlockPos pos, @NotNull CallbackInfoReturnable<Boolean> cir) {
            cir.setReturnValue(
                    flowDownCacheInt.computeIfAbsent(pack4(pos), (packed) -> {
                        BlockState blockState = getBlockState4(pos, packed);
                        BlockPos blockPos2 = pos.down();
                        BlockState blockState2 = this.world.getBlockState(blockPos2);
                        return this$0.canFlowDownTo(this.world, pos, blockState, blockPos2, blockState2);
                    })
            );
            cir.cancel();
        }

        @Unique
        private int pack4(BlockPos pos) {
            BlockPos displacement = pos.subtract(startPos);
            int dx = ((BlockPos4<?, ?>)displacement).getX4();
            int dz = ((BlockPos4<?, ?>)displacement).getZ4();
            int dw = ((BlockPos4<?, ?>)displacement).getW4();
            //displacement has 15 values in each direction so needs to be stored in 24 bits
            return
                    (dx + (1<<7) & 0xFF)   << 16  |
                            (dz + (1<<7) & 0xFF)   << 8   |
                            (dw + (1<<7) & 0xFF) /*<< 0*/ ;
        }

    }

}
