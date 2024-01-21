package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements CanStep{
    @Shadow @Final protected MinecraftClient client;
    int clientScheduledStepDirection;
    int ticksSinceLastStep = 0;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Override
    public void scheduleStep(int moveDirection) {
        if(ticksSinceLastStep > 5) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(moveDirection);
            ClientPlayNetworking.send(FDMCConstants.MOVING_PLAYER_ID, buf);
            this.clientScheduledStepDirection = moveDirection;
            this.setStillStepping(moveDirection != 0);
            this.resetPosition();
            this.client.gameRenderer.reset();
        }
        ticksSinceLastStep = 0;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void incrementTickCount(CallbackInfo ci){
        ticksSinceLastStep++;
    }




}
