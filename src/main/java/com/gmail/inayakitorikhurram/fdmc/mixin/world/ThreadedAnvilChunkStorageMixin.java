package com.gmail.inayakitorikhurram.fdmc.mixin.world;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.ChunkPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4i;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntFunction;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin {


    @Shadow protected abstract @Nullable ChunkHolder getCurrentChunkHolder(long pos);

    //all of these modify the squared chunk distance so random ticks and other stuff actually works
    @ModifyVariable(method = "getSquaredDistance", at = @At("STORE"), ordinal = 0)
    private static double calculateCorrectChunkX(double x3){
        double w4 = Math.floor(0.5 + (x3/FDMCConstants.STEP_DISTANCE) );
        return x3 - w4 * FDMCConstants.STEP_DISTANCE;
    }
    @ModifyVariable(method = "getSquaredDistance", at = @At("STORE"), ordinal = 2)
    private static double calculateCorrectEntityX(double x3){
        double w4 = Math.floor(0.5 + (x3/FDMCConstants.STEP_DISTANCE) );
        return x3 - w4 * FDMCConstants.STEP_DISTANCE;
    }
    @Inject(method = "getSquaredDistance", at = @At("RETURN"), cancellable = true)
    private static void getSquaredDistance4(ChunkPos pos, Entity entity, CallbackInfoReturnable<Double> cir){
        double posW = new ChunkPos4(pos).w;
        double entityW = new Vec4d(entity.getPos()).w;

        double dw = posW - entityW;
        dw = dw * 16; //so it's chunk based

        cir.setReturnValue(cir.getReturnValue() + dw * dw);
    }

    //change chunk loading on feature placement (at least 1 chunk adjacent)
    @Inject(method = "getRegion", at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/ChunkPos;x:I", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void makeFeaturePlacementWork(ChunkPos centerChunk, int margin, IntFunction<ChunkStatus> distanceToStatus, CallbackInfoReturnable<CompletableFuture<Either<List<Chunk>, ChunkHolder.Unloaded>>> cir, List<CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>>> potentialChunks, List<ChunkHolder> chunkHolders){

        ChunkPos4 pos4= new ChunkPos4(centerChunk);

        int x = pos4.x;
        int z = pos4.z;
        int w = pos4.w;

        for(int dw = -margin; dw <= margin; ++dw) {
            for(int dz = -margin; dz <= margin; ++dz) {
                for(int dx = -margin; dx <= margin; ++dx) {
                    int m = Math.max(Math.abs(dx), Math.abs(dz));
                    final ChunkPos chunkPos = new ChunkPos4(x + dx, z + dz, w + dw).toPos3();
                    long n = chunkPos.toLong();
                    ChunkHolder chunkHolder = this.getCurrentChunkHolder(n);
                    if (chunkHolder == null) {
                        cir.setReturnValue(
                                CompletableFuture.completedFuture(Either.right(new ChunkHolder.Unloaded() {
                                    public String toString() {
                                        return "Unloaded " + chunkPos;
                                    }
                                }))
                        );
                        return;
                    }

                    ChunkStatus chunkStatus = distanceToStatus.apply(m);
                    CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> completableFuture = chunkHolder.getChunkAt(chunkStatus, (ThreadedAnvilChunkStorage) (Object) this);
                    chunkHolders.add(chunkHolder);
                    potentialChunks.add(completableFuture);
                }
            }
        }
    }

}
