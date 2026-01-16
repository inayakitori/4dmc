package com.gmail.inayakitorikhurram.fdmc.mixin.world.chunk;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.ChunkPos4;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.CollisionView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkCache;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.EmptyChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ChunkCache.class)
public abstract class ChunkCacheMixin implements CollisionView {

    // we store the 3D chunks as is just on the first slice. then the rest of the chunks we store in our own Chunk[][][] view

    @Shadow
    @Final
    protected int minX;
    @Shadow
    @Final
    protected int minZ;
    @Shadow
    @Final
    protected World world;
    @Shadow
    @Final
    private Supplier<RegistryEntry<Biome>> plainsEntryGetter;
    @Unique
    protected Chunk[][][] chunksW;
    @Unique
    private int minW;
    @Unique
    private int middleW;

    protected ChunkCacheMixin(Chunk[][][] chunksW) {
        this.chunksW = chunksW;
    }

//    @WrapMethod(method = "<init>")
//    private void fdmc$init(World world, BlockPos minPos, BlockPos maxPos, Operation<Void> init){
//        BlockPos4 minPos4 = BlockPos4.of(minPos);
//        BlockPos4 maxPos4 = BlockPos4.of(maxPos);
//        this.minW = minPos4.getW4();
//        BlockPos4 maxIn3D = maxPos4.withW4(minW);
//
//        init.call(world, minPos, maxIn3D.asBlockPos());
//
//        int maxX = ChunkSectionPos.getSectionCoord(maxPos4.getX4());
//        int maxZ = ChunkSectionPos.getSectionCoord(maxPos4.getZ4());
//        int maxW = maxPos4.getW4();
//
//        this.chunksW = new Chunk[maxX - this.minX + 1][maxZ - this.minZ + 1][maxW - this.minW + 1];
//
//        ChunkManager chunkManager = world.getChunkManager();
//        for (int x = this.minX; x <= maxX; x++) {
//            for (int z = this.minZ; z <= maxZ; z++) {
//                for (int w = this.minW; w <= maxW; w++) {
//                    int x3 = x + FDMCConstants.CHUNK_STEP_DISTANCE * w;
//                    this.chunksW[x - this.minX][z - this.minZ][w - this.minW] =
//                            chunkManager.getWorldChunk(x3, z);
//                }
//            }
//        }
//    }


    // make it so the 3D pathfinding always uses the middle w value
    @ModifyVariable(method = "<init>", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private static BlockPos fdmc$modifyMaxPos(
            BlockPos maxPos, @Local(argsOnly = true, ordinal = 0) BlockPos minPos,
            @Share("minW") LocalIntRef minWRef,
            @Share("middleW") LocalIntRef middleWRef,
            @Share("maxW") LocalIntRef maxWRef){
        BlockPos4 minPos4 = BlockPos4.of(minPos);
        BlockPos4 maxPos4 = BlockPos4.of(maxPos);
        int minW = minPos4.getW4();
        int maxW = maxPos4.getW4();
        minWRef.set(minPos4.getW4());
        maxWRef.set(maxPos4.getW4());

        int middleW = (minW + maxW) / 2;
        BlockPos4 maxIn3D = maxPos4.withW4(middleW);
        middleWRef.set(middleW);

        return maxIn3D.asBlockPos();
    }


    // make it so the 3D pathfinding always uses the middle w value
    @ModifyVariable(method = "<init>", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static BlockPos fdmc$modifyMinPos(
            BlockPos minPos,
            @Share("middleW") LocalIntRef middleWRef){
        BlockPos4 minPos4 = BlockPos4.of(minPos);

        return minPos4.withW4(middleWRef.get()).asBlockPos();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void fdmcModifyCacheEnd(
            World world, BlockPos minPos, BlockPos maxPos, CallbackInfo ci,
            @Share("minW") LocalIntRef minWRef,
            @Share("maxW") LocalIntRef maxWRef,
            @Share("middleW") LocalIntRef middleWRef){
        BlockPos4 minPos4 = BlockPos4.of(minPos);
        BlockPos4 maxIn3D = BlockPos4.of(maxPos);
        BlockPos4 maxPos4 = maxIn3D.withW4(maxWRef.get());

        int minX4 = ChunkSectionPos.getSectionCoord(minPos4.getX4());

        int maxX4 = ChunkSectionPos.getSectionCoord(maxPos4.getX4());
        int maxZ = ChunkSectionPos.getSectionCoord(maxPos4.getZ4());
        int maxW = maxWRef.get();

        this.minW = minWRef.get();
        this.middleW = middleWRef.get();

        this.chunksW = new Chunk[maxX4 - minX4 + 1][maxZ - this.minZ + 1][maxW - this.minW + 1];

        ChunkManager chunkManager = world.getChunkManager();
        for (int x = minX4; x <= maxX4; x++) {
            for (int z = this.minZ; z <= maxZ; z++) {
                for (int w = this.minW; w <= maxW; w++) {
                    int x3 = x + FDMCConstants.CHUNK_STEP_DISTANCE * w;
                    this.chunksW[x - minX4][z - this.minZ][w - this.minW] =
                            chunkManager.getWorldChunk(x3, z);
                }
            }
        }
    }

    @WrapMethod(method = "getChunk(II)Lnet/minecraft/world/chunk/Chunk;")
    private Chunk fdmc$expandedGetChunk(int chunkX3, int chunkZ, Operation<Chunk> original){
        int[] chunkXW = FDMCMath.splitChunkXCoordinate(chunkX3);
        int chunkX = chunkXW[0];
        int chunkW = chunkXW[1];

        int[] minXW = FDMCMath.splitChunkXCoordinate(this.minX);
        int minX4 = minXW[0];

        if(chunkW == this.middleW){
            Chunk chunk = original.call(chunkX3, chunkZ);
            ChunkPos4 cp4 = new ChunkPos4(chunkX, chunkZ, chunkW);
            if(chunk != null && !cp4.toPos3().equals(chunk.getPos())) {
                throw new IllegalStateException("The chunk obtained was the wrong chunk");
            }
            return chunk;
        }
        int dx = chunkX - minX4;
        int dz = chunkZ - this.minZ;
        int dw = chunkW - this.minW;

        if(dx < 0 || dx >= this.chunksW.length ||
                dz < 0 || dz >= this.chunksW[dx].length ||
                dw < 0 || dw >= this.chunksW[dx][dz].length){
            return getEmptyChunk(chunkX3, chunkZ);
        }
        Chunk chunk = this.chunksW[dx][dz][dw];
        ChunkPos4 cp4 = new ChunkPos4(chunkX, chunkZ, chunkW);
        if(chunk != null && !cp4.toPos3().equals(chunk.getPos())) {
            throw new IllegalStateException("The chunk obtained was the wrong chunk");
        }
        return chunk != null ? chunk : getEmptyChunk(chunkX3, chunkZ);
    }

    private Chunk getEmptyChunk(int chunkX, int chunkZ){
        return new EmptyChunk(this.world, new ChunkPos(chunkX, chunkZ), this.plainsEntryGetter.get());
    }

}
