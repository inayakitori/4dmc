package com.gmail.inayakitorikhurram.fdmc.mixin.client.gui.screen;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.world.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelLoadingScreen.class)
public class LevelLoadingScreenMixin {
    @Shadow @Final private static Object2IntMap<ChunkStatus> STATUS_TO_COLOR;

    @Inject(method = "drawChunkMap", at = @At("HEAD"), cancellable = true)
    private static void drawAllChunkMaps(DrawContext context, WorldGenerationProgressTracker progressProvider, int centerX, int centerY, int pixelSize, int pixelMargin, CallbackInfo ci){

        int pixelSpacing = pixelSize + pixelMargin;
        int fullyLoadedChunksSize = progressProvider.getCenterSize();
        int fullyLoadedChunksPixelSize = fullyLoadedChunksSize * pixelSpacing - pixelMargin;
        int loadedChunksSize = progressProvider.getSize();
        int loadedChunksPixelSize = loadedChunksSize * pixelSpacing - pixelMargin;
        int LoadedChunksOffsetX = centerX - loadedChunksPixelSize / 2;
        int LoadedChunksOffsetY = centerY - loadedChunksPixelSize / 2;
        int fullyLoadedChunksPixelRadius = fullyLoadedChunksPixelSize / 2 + 1;

        int sliceMargin = pixelSpacing * 2;
        int fullyLoadedChunksRadius = (fullyLoadedChunksSize - 1) / 2;
        int loadedChunksRadius = (loadedChunksSize - 1) / 2;

        context.draw(() -> {
            if (pixelMargin != 0) {
                context.fill(centerX - fullyLoadedChunksPixelRadius    , centerY - fullyLoadedChunksPixelRadius    , centerX - fullyLoadedChunksPixelRadius + 1, centerY + fullyLoadedChunksPixelRadius    , -16772609);
                context.fill(centerX + fullyLoadedChunksPixelRadius - 1, centerY - fullyLoadedChunksPixelRadius    , centerX + fullyLoadedChunksPixelRadius    , centerY + fullyLoadedChunksPixelRadius    , -16772609);
                context.fill(centerX - fullyLoadedChunksPixelRadius    , centerY - fullyLoadedChunksPixelRadius    , centerX + fullyLoadedChunksPixelRadius    , centerY - fullyLoadedChunksPixelRadius + 1, -16772609);
                context.fill(centerX - fullyLoadedChunksPixelRadius    , centerY + fullyLoadedChunksPixelRadius - 1, centerX + fullyLoadedChunksPixelRadius    , centerY + fullyLoadedChunksPixelRadius    , -16772609);
            }
            for(int chunkW = -fullyLoadedChunksRadius - 1; chunkW <= fullyLoadedChunksRadius + 1; ++chunkW) {
                int absw = Math.abs(chunkW);
                int sliceRenderOffset = (loadedChunksPixelSize + sliceMargin) * chunkW - (int) Math.signum(chunkW) * pixelSpacing * (absw + 1) * absw;
                for (int chunkX = absw; chunkX < loadedChunksSize - absw; ++chunkX) {
                    for (int chunkZ = absw; chunkZ < loadedChunksSize - absw; ++chunkZ) {
                        ChunkStatus chunkStatus = progressProvider.getChunkStatus(chunkX + chunkW * FDMCConstants.CHUNK_STEP_DISTANCE, chunkZ);
                        int chunkStartX = LoadedChunksOffsetX + chunkX * pixelSpacing + sliceRenderOffset;
                        int chunkStartY = LoadedChunksOffsetY + chunkZ * pixelSpacing;
                        context.fill(chunkStartX, chunkStartY, chunkStartX + pixelSize, chunkStartY + pixelSize, STATUS_TO_COLOR.getInt(chunkStatus) | 0xFF000000);
                    }
                }
            }
        });
        ci.cancel();
    }
}
