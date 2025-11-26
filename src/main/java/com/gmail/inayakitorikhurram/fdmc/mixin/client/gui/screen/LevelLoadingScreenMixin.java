package com.gmail.inayakitorikhurram.fdmc.mixin.client.gui.screen;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.chunk.ChunkLoadMap;
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
    private static void drawAllChunkMaps(DrawContext context, int centerX, int centerY, int pixelSize, int pixelMargin, ChunkLoadMap map, CallbackInfo ci){

        int pixelSpacing = pixelSize + pixelMargin;
        int loadedChunksRadius =  map.getRadius()  ;
        int loadedChunksSize = loadedChunksRadius * 2 + 1;
        int loadedChunksPixelSize = loadedChunksSize * pixelSpacing - pixelMargin;
        int LoadedChunksOffsetX = centerX - loadedChunksPixelSize / 2;
        int LoadedChunksOffsetY = centerY - loadedChunksPixelSize / 2;

        int sliceMargin = pixelSpacing * 2;

        if (pixelMargin != 0) {
            context.fill(centerX - loadedChunksPixelSize    , centerY - loadedChunksPixelSize    , centerX - loadedChunksPixelSize + 1, centerY + loadedChunksPixelSize    , -16772609);
            context.fill(centerX + loadedChunksPixelSize - 1, centerY - loadedChunksPixelSize    , centerX + loadedChunksPixelSize    , centerY + loadedChunksPixelSize    , -16772609);
            context.fill(centerX - loadedChunksPixelSize    , centerY - loadedChunksPixelSize    , centerX + loadedChunksPixelSize    , centerY - loadedChunksPixelSize + 1, -16772609);
            context.fill(centerX - loadedChunksPixelSize    , centerY + loadedChunksPixelSize - 1, centerX + loadedChunksPixelSize    , centerY + loadedChunksPixelSize    , -16772609);
        }
        for(int chunkW = -loadedChunksRadius - 1; chunkW <= loadedChunksRadius + 1; ++chunkW) {
            int absw = Math.abs(chunkW);
            int sliceRenderOffset = (loadedChunksPixelSize + sliceMargin) * chunkW - (int) Math.signum(chunkW) * pixelSpacing * (absw + 1) * absw;
            for (int chunkX = absw; chunkX < loadedChunksSize - absw; ++chunkX) {
                for (int chunkZ = absw; chunkZ < loadedChunksSize - absw; ++chunkZ) {
                    ChunkStatus chunkStatus = map.getStatus(chunkX + chunkW * FDMCConstants.CHUNK_STEP_DISTANCE, chunkZ);
                    int chunkStartX = LoadedChunksOffsetX + chunkX * pixelSpacing + sliceRenderOffset;
                    int chunkStartY = LoadedChunksOffsetY + chunkZ * pixelSpacing;
                    context.fill(chunkStartX, chunkStartY, chunkStartX + pixelSize, chunkStartY + pixelSize, ColorHelper.fullAlpha(STATUS_TO_COLOR.getInt(chunkStatus)));
                }
            }
        }
        ci.cancel();
    }
}
