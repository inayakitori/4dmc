package com.gmail.inayakitorikhurram.fdmc.mixin.world.chunk;

import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DensityFunction.class)
public interface DensityFunctionMixin {

    @Mixin(DensityFunction.UnblendedNoisePos.class)
    class UnblendedNoisePosMixin{
        @Inject(method = "blockX", at = @At("RETURN"), cancellable = true)
        private void modifiedBlockX(CallbackInfoReturnable<Integer> cir){
            int x3 = cir.getReturnValue();
            int x4 = FDMCMath.getX4(x3);
            cir.setReturnValue(x4);
        }
    }
}
