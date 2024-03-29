package com.gmail.inayakitorikhurram.fdmc.mixin;


import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {


    @Final
    @Shadow
    public static final int START_TICKET_CHUNK_RADIUS = 5;

    @Shadow @Final private static int START_TICKET_CHUNKS = FDMCMath.chunkCountInRadius(4);

    @Shadow public abstract int getSpawnRadius(@Nullable ServerWorld world);

    @Shadow public abstract ServerWorld getOverworld();

    @ModifyConstant(method = "prepareStartRegion", constant = @Constant(intValue = 11))
    private int injectedRangeWhenPreparing(int value) {
        return START_TICKET_CHUNK_RADIUS;
    }

    @Redirect(method = "prepareStartRegion", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkManager;getTotalChunksLoadedCount()I"))
    private int clampChunkCount(ServerChunkManager serverChunkManager){
        int loadedTotalChunksCount = START_TICKET_CHUNKS;
        int loadedChunksCount = FDMCMath.chunkCountInRadius(START_TICKET_CHUNK_RADIUS+12);

        //this helps if the step distance is too small
        if(FDMCConstants.CHUNK_STEP_DISTANCE < 6) return serverChunkManager.getTotalChunksLoadedCount() >= 30 ? 441 : 0;

        //if we have loaded enough chunks, stop the while loop
        return
                serverChunkManager.getTotalChunksLoadedCount() >= loadedTotalChunksCount &&
                        serverChunkManager.getLoadedChunkCount() >= loadedChunksCount?
                        441 : 0;
    }

    @ModifyConstant(method = "loadWorld", constant = @Constant(intValue = 11))
    private int injectedRangeWhenLoading(int value) {
        return START_TICKET_CHUNK_RADIUS;
    }

    @ModifyConstant(method = "setupSpawn", constant = @Constant(intValue = 11))
    private static int injectedRangeWhenSpawning(int value) {
        return START_TICKET_CHUNK_RADIUS;
    }
}
