package com.gmail.inayakitorikhurram.fdmc.mixin.server;

import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
        int wVal = MathHelper.floor(FDMCMath.splitX3(wValSource)[1]);
        return xVal + FDMCMath.getOffsetX(wVal);
    }
}
