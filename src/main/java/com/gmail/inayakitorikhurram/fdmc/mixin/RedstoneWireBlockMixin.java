package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.FDMCMath;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedstoneWireBlock.class)
abstract
class RedstoneWireBlockMixin {

    @Shadow protected abstract int increasePower(BlockState state);

    @Shadow private boolean wiresGivePower;

    @Inject(method = "getReceivedRedstonePower", at = @At("RETURN"), cancellable = true)
    private void afterGetReceivedRedstonePower(World world, BlockPos pos, CallbackInfoReturnable<Integer> cir){
        wiresGivePower = false;
        int i = world.getReceivedRedstonePower(pos);
        wiresGivePower = true;
        int j = cir.getReturnValueI() + 1;
        if(i >= 15){
            return;
        }

        for(int dw = -1; dw <= 2; dw+=2){
            BlockPos blockPos = pos.add(FDMCMath.getOffset(dw));
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

}
