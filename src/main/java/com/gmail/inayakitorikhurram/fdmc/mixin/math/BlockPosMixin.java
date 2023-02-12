package com.gmail.inayakitorikhurram.fdmc.mixin.math;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockPos.class)
class BlockPosMixin {
    @Inject(method="offset(Lnet/minecraft/util/math/Direction;)Lnet/minecraft/util/math/BlockPos;", at= @At("HEAD"), cancellable = true)
    private void fdmc$offsetUseBlockPos4(Direction direction, CallbackInfoReturnable<Vec3i> cir){
        BlockPos offsetPos = ((BlockPos)(Object)this).add(
                ((Direction4)(Object)direction).getVector4().toPos3()
        );
        cir.setReturnValue(offsetPos);
        cir.cancel();
    }
    @Inject(method="offset(Lnet/minecraft/util/math/Direction;I)Lnet/minecraft/util/math/BlockPos;", at= @At("HEAD"), cancellable = true)
    private void fdmc$offsetWithDistanceUseBlockPos4(Direction direction, int i, CallbackInfoReturnable<BlockPos> cir){
        BlockPos offsetPos = ((BlockPos)(Object)this).add(
                ((Direction4)(Object)direction).getVector4().toPos3()
        );
        cir.setReturnValue(offsetPos);
        cir.cancel();
    }
    //TODO there are more of these
}
