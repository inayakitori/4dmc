package com.gmail.inayakitorikhurram.fdmc.mixin.server.network;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    private static double clampHorizontal(double d) {
        return 0;
    }

    @Shadow
    private static double clampVertical(double d) {
        return 0;
    }

    @Shadow private double lastTickX;

    @Shadow private double lastTickY;

    @Shadow private double lastTickZ;

    @Shadow public ServerPlayerEntity player;

    // If the player is stepping, skip some movement checks
    @ModifyVariable(method = "onPlayerMove(Lnet/minecraft/network/packet/c2s/play/PlayerMoveC2SPacket;)V", at = @At(value = "STORE"), ordinal = 10)
    private double modifyDeltaMovement(double old_delta, @Local(ordinal=9) double velocity_squared){
        double movement_difference = old_delta - velocity_squared;
        if(Math.pow(FDMCConstants.STEP_DISTANCE, 2) * 0.99 < movement_difference && movement_difference < Math.pow(FDMCConstants.STEP_DISTANCE, 2) * 1.01){
            //FDMCConstants.LOGGER.info("anticheat skipped due to stepping: delta_pos: {}, velocity: {}", old_delta, velocity_squared);
            return 0;
        } else{
            return old_delta;
        }
    }
}
