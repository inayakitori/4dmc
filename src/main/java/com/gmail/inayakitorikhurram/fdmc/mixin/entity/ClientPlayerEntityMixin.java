package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4i;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.BlockState;
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

import static com.gmail.inayakitorikhurram.fdmc.FDMCConstants.LOGGER;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements CanStep{
    @Shadow @Final protected MinecraftClient client;
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
            Vec3d newPos = this.pos.offset(Direction4Constants.ANA, moveDirection);
            this.refreshPositionAndAngles(
                    newPos.x, newPos.y, newPos.z,
                    this.getYaw(), this.getPitch()
            );
            this.client.gameRenderer.reset();
            ticksSinceLastStep = 0;
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void incrementTickCount(CallbackInfo ci){
        ticksSinceLastStep++;
    }


}
