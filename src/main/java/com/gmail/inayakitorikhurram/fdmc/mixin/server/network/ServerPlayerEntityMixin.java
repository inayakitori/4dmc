package com.gmail.inayakitorikhurram.fdmc.mixin.server.network;

import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanPlaceW;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements CanPlaceW {

    @Shadow @Final public MinecraftServer server;

    public ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    // TODO reimplement
    //@Redirect(method = "getWorldSpawnPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/SpawnLocating;findOverworldSpawn(Lnet/minecraft/server/world/ServerWorld;II)Lnet/minecraft/util/math/BlockPos;"))
    private BlockPos modifiedSpawnLocation(ServerWorld world, int x, int z){
        int spawnRadius = Math.min(
                this.server.getGameRules().getInt(GameRules.SPAWN_RADIUS) >> 2,
                this.server.getPlayerManager().getSimulationDistance()
        );
        int length = spawnRadius*2+1;
        int start_w = Random.create().nextInt(length + 1);

        for(int dw = 0; dw <= length; dw++) {
            int w = ((start_w + dw) % length) - spawnRadius;
            BlockPos playerSpawnPos = SpawnLocating.findOverworldSpawn(world,
                    x + FDMCMath.getOffsetX(w),
                    z
            );
            if (playerSpawnPos == null) continue;
            this.refreshPositionAndAngles(playerSpawnPos, 0.0f, 0.0f);
            if (!world.isSpaceEmpty(this)) {
                continue;
            }
            break;
        }
        return null;
    }

}
