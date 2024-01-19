package com.gmail.inayakitorikhurram.fdmc.mixin.client.world;

import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicReferenceArray;

@Mixin(ClientChunkManager.class)
public class ClientChunkManagerMixin {
    @Mixin(ClientChunkManager.ClientChunkMap.class)
    private static class ClientChunkMapMixin{

        @Shadow
        volatile int centerChunkX;
        @Shadow @Final
        int radius;
        @Shadow
        volatile int centerChunkZ;
        @Mutable
        @Shadow @Final
        AtomicReferenceArray<WorldChunk> chunks;
        @Shadow @Final private int diameter;

        @Inject(method = "<init>", at = @At("TAIL"))
        private void modifyChunkArray(ClientChunkManager clientChunkManager, int radius, CallbackInfo ci){
            this.chunks = new AtomicReferenceArray<>(this.diameter * this.diameter * this.diameter);
        }
        @Inject(method = "getIndex", at = @At("HEAD"), cancellable = true)
        private void modifiedIndex(int chunkX, int chunkZ, CallbackInfoReturnable<Integer> cir){
            int[] xw = FDMCMath.splitChunkXCoordinate(chunkX);
            cir.setReturnValue(
                    Math.floorMod(xw[1] , this.diameter) * this.diameter * this.diameter +
                    Math.floorMod(chunkZ, this.diameter) * this.diameter +
                    Math.floorMod(xw[0] , this.diameter)
            );
        }

        @Inject(method = "isInRadius" , at = @At("HEAD"), cancellable = true)
        private void modifyClientSideChunkRange(int chunkX3, int chunkZ, CallbackInfoReturnable<Boolean> cir){
            int[] centreXW = FDMCMath.splitChunkXCoordinate(this.centerChunkX);
            int centreChunkX4 = centreXW[0];
            int centreChunkW4 = centreXW[1];
            int[] xw = FDMCMath.splitChunkXCoordinate(chunkX3);
            int chunkX4 = xw[0];
            int chunkW4 = xw[1];
            cir.setReturnValue(
                    Math.abs(chunkX4 - centreChunkX4) <= this.radius &&
                    Math.abs(chunkZ  - this.centerChunkZ) <= this.radius &&
                    Math.abs(chunkW4 - centreChunkW4) <= this.radius
            );
        }
    }
}
