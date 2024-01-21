package com.gmail.inayakitorikhurram.fdmc.mixin.server.network;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.ChunkPos4;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import net.minecraft.server.network.ChunkFilter;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

import static com.gmail.inayakitorikhurram.fdmc.FDMCConstants.LOGGER;

@Mixin(ChunkFilter.class)
public interface ChunkFilterMixin{
    @ModifyVariable(method = "isWithinDistance(IIIIIZ)Z", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static int modifyCentreX(int value){
        return FDMCMath.splitChunkXCoordinate(value)[0];
    }

    @ModifyVariable(method = "isWithinDistance(IIIIIZ)Z", at = @At("HEAD"), ordinal = 3, argsOnly = true)
    private static int modifyX(int value){
        return FDMCMath.splitChunkXCoordinate(value)[0];
    }

    @Inject(method = "isWithinDistance(IIIIIZ)Z", at = @At("RETURN"), cancellable = true)
    private static void cullFarWChunks(int centerX, int centerZ, int viewDistance, int x, int z, boolean includeEdge, CallbackInfoReturnable<Boolean> cir){
        int w = FDMCMath.splitChunkXCoordinate(x)[1];
        int centreW = FDMCMath.splitChunkXCoordinate(centerX)[1];
        if (cir.getReturnValue()) {
            cir.setReturnValue(Math.abs(centreW - w) < viewDistance + (includeEdge ? 1 : 0));
        }
    }
    @Inject(method = "forEachChangedChunk", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I", ordinal = 0), cancellable = true)
    private static void modifiedChunkChange(ChunkFilter oldFilter, ChunkFilter newFilter,
                                            Consumer<ChunkPos> newlyIncluded, Consumer<ChunkPos> justRemoved,
                                            CallbackInfo ci){
        if((!(oldFilter instanceof ChunkFilter.Cylindrical oldCylinder))) return;
        if((!(newFilter instanceof ChunkFilter.Cylindrical newCylinder))) return;
        ChunkPos4 oldPos = new ChunkPos4(oldCylinder.center());
        ChunkPos4 newPos = new ChunkPos4(newCylinder.center());
        int oldView = oldCylinder.viewDistance();
        int newView = oldCylinder.viewDistance();
        LOGGER.info("updating position {} -> {}", oldPos, newPos);

        int x0 = Math.min(oldPos.x - oldView - 1, newPos.x - newView - 1);
        int z0 = Math.min(oldPos.z - oldView - 1, newPos.z - newView - 1);
        int w0 = Math.min(oldPos.w - oldView - 1, newPos.w - newView - 1);
        int x1 = Math.min(oldPos.x + oldView + 1, newPos.x + newView + 1);
        int z1 = Math.min(oldPos.z + oldView + 1, newPos.z + newView + 1);
        int w1 = Math.min(oldPos.w + oldView + 1, newPos.w + newView + 1);
        for (int chunkX = x0; chunkX <= x1; ++chunkX) {
            for (int chunkZ = z0; chunkZ <= z1; ++chunkZ) {
                for (int chunkW = w0; chunkW <= w1; ++chunkW) {
                    ChunkPos4 pos4 = new ChunkPos4(chunkX, chunkZ, chunkW);
                    ChunkPos pos3 = pos4.toPos3();
                    boolean inOld = oldCylinder.isWithinDistance(pos3.x, pos3.z);
                    boolean inNew = newCylinder.isWithinDistance(pos3.x, pos3.z);
                    if (inOld == inNew) continue;
                    if (inNew) {
                        newlyIncluded.accept(pos3);
                        LOGGER.info("adding {}", pos4);
                        continue;
                    }
                    justRemoved.accept(pos3);
                    LOGGER.info("removing {}", pos4);
                }
            }
        }
        ci.cancel();
    }

    @Mixin(ChunkFilter.Cylindrical.class)
    abstract class CyclindricalMixin implements ChunkFilter{
        @Shadow @Final private ChunkPos center;

        @Shadow @Final private int viewDistance;

        @Override
        public void forEach(Consumer<ChunkPos> consumer) {
            ChunkPos4 center4 = new ChunkPos4(this.center);
            int x0 = center4.x - viewDistance - 1;
            int z0 = center4.z - viewDistance - 1;
            int w0 = center4.w - viewDistance - 1;
            int x1 = center4.x + viewDistance + 1;
            int z1 = center4.z + viewDistance + 1;
            int w1 = center4.w + viewDistance + 1;
            for (int chunkX = x0; chunkX <= x1; ++chunkX) {
                for (int chunkZ = z0; chunkZ <= z1; ++chunkZ) {
                    for (int chunkW = w0; chunkW <= w1; ++chunkW) {
                        ChunkPos4 pos4 = new ChunkPos4(chunkX, chunkZ, chunkW);
                        ChunkPos pos3 = pos4.toPos3();
                        if (!this.isWithinDistance(pos3.x, pos3.z)) continue;
                        consumer.accept(pos3);
                    }
                }
            }
        }
    }

}
