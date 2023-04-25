package com.gmail.inayakitorikhurram.fdmc.mixin.world;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.ChunkPos4;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkRegion.class)
public class ChunkRegionMixin {

    @Shadow @Final private ChunkPos lowerCorner;

    @Shadow @Final private ChunkPos upperCorner;

    @Inject(method = "isChunkLoaded", at = @At("HEAD"), cancellable = true)
    public void isChunkLoaded(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        ChunkPos4 chunk = new ChunkPos4(new ChunkPos(chunkX, chunkZ));
        ChunkPos4 lowerCorner = new ChunkPos4(this.lowerCorner);
        ChunkPos4 upperCorner = new ChunkPos4(this.upperCorner);
        cir.setReturnValue(
                chunk.x >= lowerCorner.x && chunk.x <= upperCorner.x &&
                chunk.z >= lowerCorner.z && chunk.z <= upperCorner.z &&
                chunk.w >= lowerCorner.w && chunk.w <= upperCorner.w
        );
        cir.cancel();
    }

}
