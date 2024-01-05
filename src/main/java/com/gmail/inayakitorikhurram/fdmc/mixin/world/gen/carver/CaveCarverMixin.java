package com.gmail.inayakitorikhurram.fdmc.mixin.world.gen.carver;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.*;
import net.minecraft.world.gen.chunk.AquiferSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;

@Mixin(CaveCarver.class)
public class CaveCarverMixin {

    @Redirect(
            method = {"carveCave","carveTunnels"},
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/gen/carver/CaveCarver;carveRegion(Lnet/minecraft/world/gen/carver/CarverContext;Lnet/minecraft/world/gen/carver/CarverConfig;Lnet/minecraft/world/chunk/Chunk;Ljava/util/function/Function;Lnet/minecraft/world/gen/chunk/AquiferSampler;DDDDDLnet/minecraft/world/gen/carver/CarvingMask;Lnet/minecraft/world/gen/carver/Carver$SkipPredicate;)Z"))
    private boolean carveRegion4(CaveCarver instance, CarverContext context, CarverConfig config, Chunk chunk, Function<BlockPos, RegistryEntry<Biome>> posToBiome, AquiferSampler aquiferSampler, double x, double y, double z, double horizontalRadius, double verticalRadius, CarvingMask mask, Carver.SkipPredicate skipPredicate){
        @SuppressWarnings("unused")
		double wRadius = horizontalRadius / FDMCConstants.FDMC_CAVE_SCALE;
        boolean allComplete = instance.carveRegion(context, (CaveCarverConfig) config, chunk, posToBiome, aquiferSampler, x, y, z, horizontalRadius, verticalRadius, mask, skipPredicate);
        for(int dw = 1; dw < 2; dw++){
            double scale = 1;
            allComplete &= instance.carveRegion(context, (CaveCarverConfig) config, chunk, posToBiome, aquiferSampler, x - FDMCConstants.STEP_DISTANCE * dw, y, z, horizontalRadius * scale, verticalRadius * scale, mask, skipPredicate);
            allComplete &= instance.carveRegion(context, (CaveCarverConfig) config, chunk, posToBiome, aquiferSampler, x + FDMCConstants.STEP_DISTANCE * dw, y, z, horizontalRadius * scale, verticalRadius * scale, mask, skipPredicate);
        }

        return allComplete;
    }

}
