package com.gmail.inayakitorikhurram.fdmc.mixin.server.network;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanPlaceW;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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

    @WrapMethod(method = "increaseTravelMotionStats")
    private void modifiedXStat(double deltaX3, double deltaY, double deltaZ, Operation<Void> original){
        double[] deltaXW = FDMCMath.splitX3(deltaX3);
        double deltaX = deltaXW[0];
        double deltaW = deltaXW[1];
        original.call(deltaX, deltaY, deltaZ);
        int absw = Math.round((float) Math.abs(100. * deltaW));
        if(absw > 0) {
            this.increaseStat(FDMCConstants.STAT_STEP_COUNT, absw);
            this.addExhaustion(0.01f * (float)absw * 0.2f * FDMCConstants.STEP_HUNGER_MULTIPLIER);
        }
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
