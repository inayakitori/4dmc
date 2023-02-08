package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4;
import com.gmail.inayakitorikhurram.fdmc.math.OptionalDirection4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.*;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.FACING4;
import static net.minecraft.block.AbstractRedstoneGateBlock.POWERED;

@Mixin(AbstractRedstoneGateBlock.class)
public abstract class AbstractRedstoneGateBlockMixin extends HorizontalFacingBlockMixin {

    @Shadow protected abstract int getOutputLevel(BlockView world, BlockPos pos, BlockState state);
    @Shadow
    public static boolean isRedstoneGate(BlockState state) {
        return false;
    }
    @Shadow protected abstract void updateTarget(World world, BlockPos pos, BlockState state);
    @Shadow protected abstract boolean isValidInput(BlockState state);


    @Shadow protected abstract int getPower(World world, BlockPos pos, BlockState state);

    @Inject(method = "onBlockAdded", at =@At("TAIL"))
    public void updateWAdjacentOnBlockAdd(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
        for(Direction4 dir4 : Direction4.WDIRECTIONS){
            world.updateNeighborsAlways(pos.add(dir4.getVec3()), this.asBlock());
        }

    }


    @Inject(method = "neighborUpdate", at = @At("TAIL"))
    public void neighborUpdateAlsoWSides(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify, CallbackInfo ci) {
        for(Direction4 dir4 : Direction4.WDIRECTIONS){
            world.updateNeighborsAlways(pos.add(dir4.getVec3()), this.asBlock());
        }
    }
    @Inject(method = "neighborUpdate", at=@At(value = "INVOKE", target = "Lnet/minecraft/world/World;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z", shift = At.Shift.AFTER), cancellable = true)
    public void neighborUpdate4(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify, CallbackInfo ci) {
        for(Direction4 dir4 : Direction4.ALL){
            world.updateNeighborsAlways(pos.add(dir4.getVec3()), this.asBlock());
        }
        ci.cancel();
    }

    //redirect dir3 calls to dir4 ones
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return getStrongRedstonePower4(state, world, pos, Direction4.fromDirection3(direction));
    }

    public int getWeakRedstonePower3(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return getWeakRedstonePower4(state, world, pos, Direction4.fromDirection3(direction));
    }
    @Override
    public int getStrongRedstonePower4(BlockState state, BlockView world, BlockPos pos, Direction4 dir) {
        return this.getWeakRedstonePower4(state, world, pos, dir);
    }

    @Override
    public int getWeakRedstonePower4(BlockState state, BlockView world, BlockPos pos, Direction4 dir) {
        if (!(Boolean)state.get(POWERED)) {
            return 0;
        } else {
            return state.get(FACING4).toDir4(state.get(FACING)) == dir ? this.getOutputLevel(world, pos, state) : 0;
        }
    }

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    public void getPlacementState4(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir){
        OptionalDirection4 optionalDirection4 = ((CanStep)ctx.getPlayer()).getPlacementDirection4();
        //if should place ana/kata do that
        optionalDirection4.ifPresent(direction4 -> {
            BlockState state = cir.getReturnValue();
            cir.setReturnValue(state
                    .with(FACING, Direction.NORTH)//allows for horizontal interaction e.g furnace n stuff
                    .with(FACING4, optionalDirection4.getOpposite())
            );
        });
    }

    @Inject(method = "updateTarget", at = @At("HEAD"), cancellable = true)
    protected void updateTarget4(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
        Direction4 direction4 = state.get(FACING4).toDir4(state.get(FACING));
        BlockPos neighborBlockPos = pos.add(direction4.getOpposite().getVec3());
        world.updateNeighbor(neighborBlockPos, this.asBlock(), pos);
        ((WorldAccessI)world).updateNeighborsExcept4(neighborBlockPos, this.asBlock(), null);
        ci.cancel();
    }

    //TODO optimise
    @Inject(method = "getPower", at = @At("HEAD"), cancellable = true)
    protected void getPower(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Integer> cir) {
        Direction4 dir4 = state.get(FACING4).toDir4(state.get(FACING));
        BlockPos blockPos = pos.add(dir4.getVec3());
        int i = ((WorldI)world).getEmittedRedstonePower(blockPos, dir4);
        if (i >= 15) {
            cir.setReturnValue(i);
        } else {
            BlockState blockState = world.getBlockState(blockPos);
            cir.setReturnValue(Math.max(
                    i,
                    blockState.isOf(Blocks.REDSTONE_WIRE) ?
                            (Integer)blockState.get(RedstoneWireBlock.POWER)
                            : 0
            ));
        }
        cir.cancel();
    }

    @Inject(method = "getMaxInputLevelSides", at = @At("HEAD"), cancellable = true)
    protected void getMaxInputLevelSides(WorldView world, BlockPos pos, BlockState state, CallbackInfoReturnable<Integer> cir) {
        Direction4[] perpendicularDirections = state.get(FACING4).toDir4(state.get(FACING)).getPerpendicularHorizontal();
        int max_val = 0;
        for (Direction4 dir4 : perpendicularDirections){
            max_val = Math.max(
                    max_val,
                    this.getInputLevelFromDir4(world, pos.add(dir4.getVec3()), dir4)
            );
        }
        cir.setReturnValue(max_val);
        cir.cancel();
    }
    protected int getInputLevelFromDir4(WorldView world, BlockPos pos, Direction4 dir4) {
        BlockState blockState = world.getBlockState(pos);
        if (this.isValidInput(blockState)) {
            if (blockState.isOf(Blocks.REDSTONE_BLOCK)) {
                return 15;
            } else {
                return blockState.isOf(Blocks.REDSTONE_WIRE) ?
                        blockState.get(RedstoneWireBlock.POWER) :
                        ((WorldViewI)world).getStrongRedstonePower(pos, dir4);
            }
        } else {
            return 0;
        }
    }

    @Inject(method = "isTargetNotAligned", at = @At("HEAD"), cancellable = true)
    public void include4SidesinTargetAlignment(BlockView world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        Direction4 dir4 = (state.get(FACING4).toDir4(state.get(FACING))).getOpposite();
        BlockState blockState = world.getBlockState(pos.add(dir4.getVec3()));
         cir.setReturnValue(
                 isRedstoneGate(blockState) && blockState.get(FACING4).toDir4(blockState.get(FACING)) != dir4
         );
        cir.cancel();
    }
}
