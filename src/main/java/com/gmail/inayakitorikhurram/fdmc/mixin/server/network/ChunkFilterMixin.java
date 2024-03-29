package com.gmail.inayakitorikhurram.fdmc.mixin.server.network;

import com.gmail.inayakitorikhurram.fdmc.math.ChunkPos4;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.google.common.annotations.VisibleForTesting;
import net.minecraft.server.network.ChunkFilter;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

import static com.gmail.inayakitorikhurram.fdmc.FDMCConstants.LOGGER;
import static com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants.*;

@Mixin(ChunkFilter.class)
public interface ChunkFilterMixin{

    /**
     * @author Inaya Khurram
     * @reason This would take 3 injects
     */
    @Overwrite
    static boolean isWithinDistance(int centerX3, int centerZ, int viewDistance, int x3, int z, boolean includeEdge) {
        int[] centreXW = FDMCMath.splitChunkXCoordinate(centerX3);
        int[] xw = FDMCMath.splitChunkXCoordinate(x3);
        int dx = Math.max(0, Math.abs(xw[0] - centreXW[0]) - 1);
        int dz = Math.max(0, Math.abs(z - centerZ) - 1);
        int dw = Math.max(0, Math.abs(xw[1] - centreXW[1]) - 1);
        long long_side = Math.max(0, Math.max(dx, dz) - (includeEdge ? 1 : 0));
        long short_side = Math.min(dx, dz);
        long squaredDistance3 = short_side * short_side + long_side * long_side;
        int squaredViewDistance = viewDistance * viewDistance;
        return squaredDistance3 < (long)squaredViewDistance && dw < viewDistance;
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

        int x0 = Math.min(oldPos.x - oldView - 1, newPos.x - newView - 1);
        int z0 = Math.min(oldPos.z - oldView - 1, newPos.z - newView - 1);
        int w0 = Math.min(oldPos.w - oldView - 1, newPos.w - newView - 1);
        int x1 = Math.max(oldPos.x + oldView + 1, newPos.x + newView + 1);
        int z1 = Math.max(oldPos.z + oldView + 1, newPos.z + newView + 1);
        int w1 = Math.max(oldPos.w + oldView + 1, newPos.w + newView + 1);
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
                        continue;
                    }
                    justRemoved.accept(pos3);
                }
            }
        }
        ci.cancel();
    }

    @Mixin(ChunkFilter.Cylindrical.class)
    abstract class CylindricalMixin implements ChunkFilter{
        @Shadow @Final private ChunkPos center;

        @Shadow @Final private int viewDistance;

        /**
         * @author Inaya Khurram
         * @reason TODO make inject + cancel
         */
        @VisibleForTesting
        @Overwrite
        public boolean overlaps(Cylindrical o) {
            ChunkPos4 centre4T = new ChunkPos4(this.center);
            ChunkPos4 centre4O = new ChunkPos4(o.center());
            int viewT = this.viewDistance;
            int viewO = o.viewDistance();

            return
                    centre4T.x - viewT - 1 <= centre4O.x + viewO + 1 &&
                    centre4T.x + viewT + 1 >= centre4O.x - viewO - 1 &&
                    centre4T.z - viewT - 1 <= centre4O.z + viewO + 1 &&
                    centre4T.z + viewT + 1 >= centre4O.z - viewO - 1 &&
                    centre4T.w - viewT - 1 <= centre4O.w + viewO + 1 &&
                    centre4T.w + viewT + 1 >= centre4O.w - viewO - 1
                    ;
        }

        //index by enum ID
        private int[] getBounds(){
            ChunkPos4 centre4 = new ChunkPos4(this.center);
            return new int[]{
                    centre4.z + this.viewDistance + 1,
                    centre4.x - this.viewDistance - 1,
                    centre4.z - this.viewDistance - 1,
                    centre4.x + this.viewDistance + 1,
                    centre4.w - this.viewDistance - 1,
                    centre4.w + this.viewDistance + 1,
            };
        }

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
