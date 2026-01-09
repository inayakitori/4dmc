package com.gmail.inayakitorikhurram.fdmc.mixin.client.network;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	@Redirect(
		method = {"onEntityPosition", "onPlayerPositionLook"},
		at = @At(
			value = "NEW",
			target = "(DDDFFZZ)Lnet/minecraft/network/packet/c2s/play/PlayerMoveC2SPacket$Full;"
		)
	)
	PlayerMoveC2SPacket.Full fdmc$writePositionW1(double x, double y, double z, float yaw, float pitch, boolean onGround, boolean horizontalCollision){
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		assert player != null;
		return new PlayerMoveC2SPacket.Full(player.getEntityPos(), yaw, pitch, onGround, horizontalCollision);
	}
}
