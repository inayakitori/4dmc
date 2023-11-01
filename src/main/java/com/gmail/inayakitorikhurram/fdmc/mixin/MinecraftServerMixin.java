package com.gmail.inayakitorikhurram.fdmc.mixin;


import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
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

    @Shadow @Final private static int START_TICKET_CHUNKS = 249;

    @Shadow public abstract int getSpawnRadius(@Nullable ServerWorld world);

    @Shadow public abstract ServerWorld getOverworld();

    @Inject(method = "getSpawnRadius", at = @At("HEAD"), cancellable = true)
    private void modifySpawnRadius(ServerWorld world, CallbackInfoReturnable<Integer> cir){
       if (world == null) cir.setReturnValue(5);
    }

    @ModifyConstant(method = "prepareStartRegion", constant = @Constant(intValue = 11))
    private int injectedRangeWhenPreparing(int value) {
        return START_TICKET_CHUNK_RADIUS;
    }

    //if we have loaded enough chunks, stop the while loop
    @Redirect(method = "prepareStartRegion", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkManager;getTotalChunksLoadedCount()I"))
    private int clampChunkCount(ServerChunkManager serverChunkManager){
        //FDMCConstants.LOGGER.info("spawn radius: {}", this.getSpawnRadius((ServerWorld) serverChunkManager.getWorld()));
        //FDMCConstants.LOGGER.info("chunk count: {}", serverChunkManager.getTotalChunksLoadedCount());
        return serverChunkManager.getTotalChunksLoadedCount() >= START_TICKET_CHUNKS? 441 : serverChunkManager.getLoadedChunkCount();
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
