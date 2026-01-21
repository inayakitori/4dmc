package com.gmail.inayakitorikhurram.fdmc.mixin.server;

import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.entity.EntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "sendToAround", at = @At("HEAD"))
    private void fdmc$setX(@Nullable PlayerEntity player,
                           double x, double y, double z, double distance,
                           RegistryKey<World> worldKey, Packet<?> packet, CallbackInfo ci,
                           @Share("SoundX") LocalDoubleRef soundX){
        soundX.set(x);
    }


    // currently just shifts a sound as if there is no w distance
    @WrapOperation(method = "sendToAround", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getX()D"))
    private double fdmc$modifyXDistance(ServerPlayerEntity player, Operation<Double> player$getX, @Share("SoundX") LocalDoubleRef soundX){
        double wValSource = soundX.get();
        double xVal = FDMCMath.splitX3(player.getX())[0];
        double wVal = FDMCMath.splitX3(wValSource)[1];
        return xVal + FDMCMath.getOffsetX(wVal);
    }

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;requestTeleport(DDDFF)V"))
    void onPlayerConnect$requestTeleport(ServerPlayNetworkHandler instance, double x, double y, double z, float yaw, float pitch, @Local(argsOnly = true) ServerPlayerEntity player){
        instance.requestTeleport(new EntityPosition(player.getEntityPos(), Vec4d.ZERO, yaw, pitch), Collections.emptySet());
    }

    @Redirect(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;requestTeleport(DDDFF)V"))
    void respawnPlayer$requestTeleport4(ServerPlayNetworkHandler instance, double x, double y, double z, float yaw, float pitch, @Local(ordinal = 1) ServerPlayerEntity newPlayer){
        instance.requestTeleport(new EntityPosition(newPlayer.getEntityPos(), Vec4d.ZERO, yaw, pitch), Collections.emptySet());
    }
}
