package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.ChunkPos4;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.light.LevelPropagator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.ChunkPosDistanceLevelPropagator.class)
public abstract class ChunkPosDistanceLevelPropagatorMixin extends LevelPropagator {

    @Shadow protected abstract int getPropagatedLevel(long sourceId, long targetId, int level);

    protected ChunkPosDistanceLevelPropagatorMixin(int levelCount, int expectedLevelSize, int expectedTotalSize) {
        super(levelCount, expectedLevelSize, expectedTotalSize);
    }

    @Inject(method = "propagateLevel(JIZ)V", at = @At("RETURN"))
    protected void propagateLevel(long id, int level, boolean decrease, CallbackInfo ci) {
        ChunkPos pos3 = new ChunkPos(id);
        ChunkPos4 pos4 = new ChunkPos4(pos3);
        int x = pos4.x;
        int z = pos4.z;
        int w = pos4.w;
        for (int dw = -1; dw <= 1; dw = dw + 2) {
            long m = new ChunkPos4(x, z, w + dw).toPos3().toLong();
            if (m == id) continue;
            this.propagateLevel(id, m, level, decrease);
        }
    }

    @Inject(method = "recalculateLevel(JJI)I", at = @At("RETURN"), cancellable = true)
    protected void recalculateLevel(long id, long excludedId, int maxLevel, CallbackInfoReturnable<Integer> cir) {
        int currentLevel = Math.min(maxLevel, cir.getReturnValue());

        ChunkPos chunkPos3 = new ChunkPos(id);
        int x = chunkPos3.x;
        int z = chunkPos3.z;
        for(int dw = -1; dw <= 1; dw = dw+2) {
            long adjacentChunkPos3 = ChunkPos.toLong(x + dw * FDMCConstants.CHUNK_STEP_DISTANCE, z);
            if (adjacentChunkPos3 == id) {
                adjacentChunkPos3 = ChunkPos.MARKER;
            }
            if (adjacentChunkPos3 == excludedId) continue;
            int levelFromAdjChunk = this.getPropagatedLevel(adjacentChunkPos3, id, this.getLevel(adjacentChunkPos3));
            if (currentLevel > levelFromAdjChunk) {
                currentLevel = levelFromAdjChunk;
            }
            if (currentLevel != 0) continue;
            cir.setReturnValue(currentLevel);
        }
        cir.setReturnValue(currentLevel);
    }

}
