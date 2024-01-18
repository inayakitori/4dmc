package com.gmail.inayakitorikhurram.fdmc.mixin.world.biome.source;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.BiomeMath;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(MultiNoiseBiomeSource.class)
public class MultiNoiseBiomeSourceMixin {
    @Inject(method = "addDebugInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/source/util/MultiNoiseUtil;toFloat(J)F", ordinal = 0, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addDebugInfo4(List<String> info, BlockPos pos, MultiNoiseUtil.MultiNoiseSampler noiseSampler, CallbackInfo ci, int biomeX, int biomeY, int biomeZ, MultiNoiseUtil.NoiseValuePoint noiseValuePoint){

        int x3 = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        int[] xw = FDMCMath.splitX3(x3);
        int w = xw[1];
        int x4 = xw[0];

        MultiNoiseUtil.NoiseValuePoint noiseXYZ = BiomeMath.getMultiNoiseXYZ(x4, y, z, noiseSampler);
        MultiNoiseUtil.NoiseValuePoint noiseW = BiomeMath.getMultiNoiseW(y, w, noiseSampler);
        MultiNoiseUtil.NoiseValuePoint noiseFinal = BiomeMath.combineMultiNoise(noiseXYZ, noiseW);
        info.add("W noise:     " + getNoiseText(noiseW));
        info.add("XYZ noise: " + getNoiseText(noiseXYZ));
        info.add("combined:  " + getNoiseText(noiseFinal) + " (" + FDMCConstants.BIOME_W_WEIGHT + "*W, " + FDMCConstants.BIOME_XYZ_WEIGHT + "*XYZ)");

    }

    private static String getNoiseText(MultiNoiseUtil.NoiseValuePoint noise) {
        return
                "T: " + noise.temperatureNoise() +
                ", H: " + noise.humidityNoise() +
                ", C: " + noise.continentalnessNoise() +
                ", E: " + noise.erosionNoise() +
                ", D: " + noise.depth() +
                ", W: " + noise.weirdnessNoise();
    }
}
