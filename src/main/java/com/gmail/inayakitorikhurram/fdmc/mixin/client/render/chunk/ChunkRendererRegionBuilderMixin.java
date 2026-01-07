package com.gmail.inayakitorikhurram.fdmc.mixin.client.render.chunk;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.RenderedChunkMixinI;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.render.chunk.ChunkRendererRegionBuilder;
import net.minecraft.client.render.chunk.RenderedChunk;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;

import static com.gmail.inayakitorikhurram.fdmc.FDMCClientConstants.MAX_W_SLICES;

@Mixin(ChunkRendererRegionBuilder.class)
public class ChunkRendererRegionBuilderMixin {

    @Unique
    private Map<Long, RenderedChunk> renderChunksMap = new HashMap<>();

    @WrapOperation(method = "build", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/chunk/ChunkRendererRegionBuilder;getRenderedChunk(Lnet/minecraft/world/World;III)Lnet/minecraft/client/render/chunk/RenderedChunk;"))
    private RenderedChunk fdmc$alsoRenderWChunks(
            ChunkRendererRegionBuilder instance, World world, int sectionX, int sectionY, int sectionZ,
            Operation<RenderedChunk> original){
        RenderedChunk baseChunk = null;
        int maxW = MAX_W_SLICES;
        for(int dw = -maxW; dw <= maxW; dw++){
            int newSectionX = sectionX + FDMCConstants.CHUNK_STEP_DISTANCE*dw;

            if(dw == 0){
                baseChunk = original.call(instance, world, newSectionX, sectionY, sectionZ);
                renderChunksMap.put(ChunkSectionPos.asLong(newSectionX, sectionY, sectionZ), baseChunk);
            } else {
                renderChunksMap.computeIfAbsent(ChunkSectionPos.asLong(newSectionX, sectionY, sectionZ), pos -> {
                    WorldChunk worldChunk = world.getChunk(newSectionX, sectionZ);
                    return new RenderedChunk(worldChunk, worldChunk.sectionCoordToIndex(sectionY));
                });
            }
        }

        return baseChunk;
    }

    @WrapMethod(method = "getRenderedChunk")
    private RenderedChunk fdmc$passInformationToRenderedChunk(World world, int sectionX, int sectionY, int sectionZ, Operation<RenderedChunk> original){
        RenderedChunk chunk = original.call(world, sectionX, sectionY, sectionZ);
        RenderedChunkMixinI chunkI = (RenderedChunkMixinI) chunk;
        if(chunkI != null) {
            chunkI.setRenderChunksMap(this.renderChunksMap);
            chunkI.setWorld(world);
        }
        return chunk;
    }
}
