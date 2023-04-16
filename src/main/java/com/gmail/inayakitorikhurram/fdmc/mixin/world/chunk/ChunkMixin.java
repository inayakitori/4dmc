package com.gmail.inayakitorikhurram.fdmc.mixin.world.chunk;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Chunk.class)
public class ChunkMixin {

    //the original slice's chunk
    private Chunk baseChunk;

    @ModifyArgs(method = "populateBiomes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/source/BiomeCoords;fromBlock(I)I", ordinal = 0))
    private void modifiedBiomePosition(Args args){
        int x = args.get(0);
        int w4 = (int)(Math.floor(0.5 + (x + 0d)/ FDMCConstants.STEP_DISTANCE));
        int x4 = x - w4 * FDMCConstants.STEP_DISTANCE;
        args.set(0, x4);
    }

}
