package com.gmail.inayakitorikhurram.fdmc.mixin.client.render.chunk;

import com.gmail.inayakitorikhurram.fdmc.FDMCClientConstants;
import com.gmail.inayakitorikhurram.fdmc.client.option.GameOptions4;
import com.gmail.inayakitorikhurram.fdmc.client.option.Perspective4;
import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4i;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.RenderedChunkMixinI;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.chunk.RenderedChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;

@Mixin(net.minecraft.client.render.chunk.RenderedChunk.class)
public class RenderedChunkMixin implements com.gmail.inayakitorikhurram.fdmc.mixininterfaces.RenderedChunkMixinI {

    @Unique
    private Map<Long, RenderedChunk> renderChunksMap = new HashMap<>();
    @Unique
    private World world;

    @SuppressWarnings("rawtypes")
    @WrapMethod(method = "getBlockState")
    private BlockState fdmc$followCurrent4DPerspective(BlockPos renderPos, Operation<BlockState> original){
        BlockPos4 pos4 = (BlockPos4) renderPos;
        BlockPos4 cameraPos = (BlockPos4) MinecraftClient.getInstance().gameRenderer.getCamera().getBlockPos();

        // it lies. this is fine

        Vec4i delta = pos4.subtract4(cameraPos);
        Perspective4 perspective = ((GameOptions4)MinecraftClient.getInstance().options).getPerspective4();
        Vec4i rotatedDelta = perspective.getRenderVector(delta);
        BlockPos4 logicalPos = (BlockPos4) cameraPos.add4(rotatedDelta);
        //Direction renderY = perspective.renderY().asDirection();

        int maxW = FDMCClientConstants.MAX_W_SLICES;
        if(MathHelper.abs(rotatedDelta.getW4()) > maxW) {
            return Blocks.VOID_AIR.getDefaultState();
        }
        ChunkSectionPos newSectionPos = ChunkSectionPos.from(logicalPos.asBlockPos());
        RenderedChunk chunk = renderChunksMap.get(newSectionPos.asLong());
        if(chunk != null && chunk.blockPalette != null){
            BlockState state = chunk.blockPalette.get(
                    logicalPos.getX4() & 0xF,
                    logicalPos.getY4() & 0xF,
                    logicalPos.getZ4() & 0xF
            );
            return state;
        }
        return Blocks.VOID_AIR.getDefaultState();
    }

    @Unique
    @Override
    public Map<Long, RenderedChunk> getRenderChunksMap() {
        return renderChunksMap;
    }

    @Unique
    @Override
    public void setRenderChunksMap(Map<Long, RenderedChunk> map) {
        this.renderChunksMap = map;
    }

    @Unique
    @Override
    public World getWorld() {
        return world;
    }

    @Override
    @Unique
    public void setWorld(World world) {
        this.world = world;
    }

}
