package com.gmail.inayakitorikhurram.fdmc.mixin.world.biome.source.util;


import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.BiomeMath;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Objects;
import java.util.Optional;

@Mixin(MultiNoiseUtil.class)
public class MultiNoiseUtilMixin {

    @Mixin(MultiNoiseUtil.MultiNoiseSampler.class)
    static abstract class MultiNoiseSamplerMixin{
        @Shadow @Final private DensityFunction temperature;

        @Shadow @Final private DensityFunction humidity;

        @Shadow @Final private DensityFunction continentalness;

        @Shadow @Final private DensityFunction erosion;

        @Shadow @Final private DensityFunction depth;

        @Shadow @Final private DensityFunction weirdness;

        @Shadow public abstract DensityFunction temperature();

        @Shadow public abstract DensityFunction humidity();

        @Shadow public abstract DensityFunction continentalness();

        @Shadow public abstract DensityFunction erosion();

        @Shadow public abstract DensityFunction depth();

        @Shadow public abstract DensityFunction weirdness();

        @Inject(method = "sample", at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/world/gen/densityfunction/DensityFunction$UnblendedNoisePos;<init>(III)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
        private void createNoiseValuePoint(int biomex, int biomey, int biomez, CallbackInfoReturnable<MultiNoiseUtil.NoiseValuePoint> cir, int x3, int y, int z){

            cir.setReturnValue(
                    BiomeMath.sampleMultiNoise(x3, y, z, (MultiNoiseUtil.MultiNoiseSampler) (Object) this, (MultiNoiseUtil.MultiNoiseSampler) (Object) this)
            );
        }
    }

}
