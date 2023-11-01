package com.gmail.inayakitorikhurram.fdmc.mixin.world.biome.source.util;


import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MultiNoiseUtil.class)
public class MultiNoiseUtilMixin {

    @Mixin(MultiNoiseUtil.MultiNoiseSampler.class)
    static class MultiNoiseSamplerMixin{
        @Shadow @Final private DensityFunction temperature;

        @Shadow @Final private DensityFunction humidity;

        @Shadow @Final private DensityFunction continentalness;

        @Shadow @Final private DensityFunction erosion;

        @Shadow @Final private DensityFunction depth;

        @Shadow @Final private DensityFunction weirdness;

        @Inject(method = "sample", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/world/gen/densityfunction/DensityFunction$UnblendedNoisePos;<init>(III)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
        private void createNoiseValuePoint(int biomex, int biomey, int biomez, CallbackInfoReturnable<MultiNoiseUtil.NoiseValuePoint> cir, int x3, int y, int z){


            int w4 = (int)(Math.floor(0.5 + (x3 + 0d)/ FDMCConstants.STEP_DISTANCE));
            int x4 = x3 - w4 * FDMCConstants.STEP_DISTANCE;

            DensityFunction.UnblendedNoisePos noisePos3d = new DensityFunction.UnblendedNoisePos(x4, y, z);
            DensityFunction.UnblendedNoisePos noisePos4d = new DensityFunction.UnblendedNoisePos(0, y, w4 * FDMCConstants.BIOMESCALEW);

            cir.setReturnValue(MultiNoiseUtil.createNoiseValuePoint(
                    (float)(1.0 * this.temperature.sample(noisePos3d)      + 0.0 * this.temperature.sample(noisePos4d)     ),
                    (float)(1.0 * this.humidity.sample(noisePos3d)         + 0.0 * this.humidity.sample(noisePos4d)        ),
                    (float)(1.0 * this.continentalness.sample(noisePos3d)  + 0.0 * this.continentalness.sample(noisePos4d) ),
                    (float)(1.0 * this.erosion.sample(noisePos3d)          + 0.0 * this.erosion.sample(noisePos4d)         ),
                    (float)(1.0 * this.depth.sample(noisePos3d)            + 0.0 * this.depth.sample(noisePos4d)           ),
                    (float)(1.0 * this.weirdness.sample(noisePos3d)        + 0.0 * this.weirdness.sample(noisePos4d)       )
            ));
        }
    }

}
