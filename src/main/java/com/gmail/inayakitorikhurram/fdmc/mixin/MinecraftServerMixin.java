package com.gmail.inayakitorikhurram.fdmc.mixin;


import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow
    public abstract GameRules getGameRules();

    // TODO
    //@Redirect(method = "prepareStartRegion", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkManager;getTotalChunksLoadedCount()I"))
    private int clampChunkCount(ServerChunkManager serverChunkManager){
        int spawnRadius = getGameRules().getInt(GameRules.SPAWN_RADIUS);
        int loadedTotalChunksCount = FDMCMath.chunkCountInRadius(spawnRadius);
        int loadedChunksCount = FDMCMath.chunkCountInRadius(spawnRadius+12);

        //this helps if the step distance is too small
        if(FDMCConstants.CHUNK_STEP_DISTANCE < 6) return serverChunkManager.getLoadedChunkCount() >= 30 ? 441 : 0;

        //if we have loaded enough chunks, stop the while loop
        return
                serverChunkManager.getLoadedChunkCount() >= loadedTotalChunksCount &&
                        serverChunkManager.getLoadedChunkCount() >= loadedChunksCount?
                        441 : 0;
    }

}
