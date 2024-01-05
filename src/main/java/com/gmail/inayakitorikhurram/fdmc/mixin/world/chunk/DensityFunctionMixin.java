package com.gmail.inayakitorikhurram.fdmc.mixin.world.chunk;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
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
            int x = cir.getReturnValue();
            int w4 = (int)(Math.floor(0.5 + (x + 0d)/ FDMCConstants.STEP_DISTANCE));
            int x4 = x - w4 * FDMCConstants.STEP_DISTANCE;
            cir.setReturnValue(x4);
        }
    }
}
