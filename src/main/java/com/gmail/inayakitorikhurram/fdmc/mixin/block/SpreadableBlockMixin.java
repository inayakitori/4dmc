package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import net.minecraft.block.BlockState;
import net.minecraft.block.SpreadableBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpreadableBlock.class)
public class SpreadableBlockMixin {

    private Random random;

    @Inject(method = "randomTick", at = @At("HEAD"))
    private void storingRandom(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci){
        this.random = random;
    }

    @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;add(III)Lnet/minecraft/util/math/BlockPos;"))
    private BlockPos modifiedRandomPos(BlockPos pos, int i, int j, int k){
        return (BlockPos) ((BlockPos4<?, ?>)pos).add4(i, j, k, this.random.nextInt(3) - 1);
    }
}
