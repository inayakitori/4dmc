package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.AbstractBlockI;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.AbstractBlockStateI;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.BlockI;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.block.RedstoneTorchBlock.LIT;

@Mixin(RedstoneTorchBlock.class)
public abstract class RedstoneTorchBlockMixin extends TorchBlockMixin{

    @Inject(method = "onBlockAdded", at =@At("TAIL"))
    public void updateWAdjacentOnBlockAdd(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
        for(Direction4 dir4 : Direction4.WDIRECTIONS){
            world.updateNeighborsAlways(pos.add(dir4.getVec3()), this.asBlock());
        }

    }

    @Override
    public int getWeakRedstonePower4(BlockState state, BlockView world, BlockPos pos, Direction4 dir4) {
        return state.get(LIT) && dir4 != Direction4.UP ? 15 : 0;
    }

    @Override
    public int getStrongRedstonePower4(BlockState state, BlockView world, BlockPos pos, Direction4 dir4) {
        return dir4 == Direction4.DOWN ? ((AbstractBlockStateI)state).getWeakRedstonePower(world, pos, dir4) : 0;
    }
}
