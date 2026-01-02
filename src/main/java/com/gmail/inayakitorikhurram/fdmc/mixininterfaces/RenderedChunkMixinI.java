package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import net.minecraft.client.render.chunk.ChunkRendererRegionBuilder;
import net.minecraft.client.render.chunk.RenderedChunk;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

public interface RenderedChunkMixinI {

    Map<Long, RenderedChunk> getRenderChunksMap();
    void setRenderChunksMap(Map<Long, RenderedChunk> map);

    World getWorld();
    void setWorld(World world);

}
