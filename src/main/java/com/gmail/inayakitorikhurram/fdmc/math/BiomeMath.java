package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.densityfunction.DensityFunction;

import static com.gmail.inayakitorikhurram.fdmc.FDMCConstants.BIOME_W_WEIGHT;
import static com.gmail.inayakitorikhurram.fdmc.FDMCConstants.BIOME_XYZ_WEIGHT;

public class BiomeMath {
    public static MultiNoiseUtil.NoiseValuePoint getMultiNoiseXYZ(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler sampler){

        DensityFunction.UnblendedNoisePos noisePos3d = new DensityFunction.UnblendedNoisePos(x, y, z);

        return MultiNoiseUtil.createNoiseValuePoint(
                (float)(sampler.temperature()     .sample(noisePos3d) ),
                (float)(sampler.humidity()        .sample(noisePos3d) ),
                (float)(sampler.continentalness() .sample(noisePos3d) ),
                (float)(sampler.erosion()         .sample(noisePos3d) ),
                (float)(sampler.depth()           .sample(noisePos3d) ),
                (float)(sampler.weirdness()       .sample(noisePos3d) )
        );
    }

    public static MultiNoiseUtil.NoiseValuePoint getMultiNoiseW(int y, int w, MultiNoiseUtil.MultiNoiseSampler sampler){

        DensityFunction.UnblendedNoisePos noisePos4d = new DensityFunction.UnblendedNoisePos(10000, y, w * FDMCConstants.BIOMESCALEW);

        return MultiNoiseUtil.createNoiseValuePoint(
                (float)(sampler.temperature()     .sample(noisePos4d) ),
                (float)(sampler.humidity()        .sample(noisePos4d) ),
                (float)(sampler.continentalness() .sample(noisePos4d) ),
                (float)(sampler.erosion()         .sample(noisePos4d) ),
                (float)(sampler.depth()           .sample(noisePos4d) ),
                (float)(sampler.weirdness()       .sample(noisePos4d) )
        );
    }

    public static MultiNoiseUtil.NoiseValuePoint combineMultiNoise(MultiNoiseUtil.NoiseValuePoint noiseXYZ, MultiNoiseUtil.NoiseValuePoint noiseW){
        return new MultiNoiseUtil.NoiseValuePoint(
                (long)(noiseXYZ.temperatureNoise()     * BIOME_XYZ_WEIGHT + noiseW.temperatureNoise()     * BIOME_W_WEIGHT),
                (long)(noiseXYZ.humidityNoise()        * BIOME_XYZ_WEIGHT + noiseW.humidityNoise()        * BIOME_W_WEIGHT),
                (long)(noiseXYZ.continentalnessNoise() * BIOME_XYZ_WEIGHT + noiseW.continentalnessNoise() * BIOME_W_WEIGHT),
                (long)(noiseXYZ.erosionNoise()         * BIOME_XYZ_WEIGHT + noiseW.erosionNoise()         * BIOME_W_WEIGHT),
                (long)(noiseXYZ.depth()                * BIOME_XYZ_WEIGHT + noiseW.depth()                * BIOME_W_WEIGHT),
                (long)(noiseXYZ.weirdnessNoise()       * BIOME_XYZ_WEIGHT + noiseW.weirdnessNoise()       * BIOME_W_WEIGHT)
        );
    }

    public static MultiNoiseUtil.NoiseValuePoint sampleMultiNoise(int x3, int y, int z, MultiNoiseUtil.MultiNoiseSampler sampler3, MultiNoiseUtil.MultiNoiseSampler sampler4){

        int[] xw = FDMCMath.splitX3(x3);

        MultiNoiseUtil.NoiseValuePoint noiseXYZ = getMultiNoiseXYZ(xw[0], y, z, sampler3);
        MultiNoiseUtil.NoiseValuePoint noiseW = getMultiNoiseW(y, xw[1], sampler4);

        return combineMultiNoise(noiseXYZ, noiseW);
    }
}
