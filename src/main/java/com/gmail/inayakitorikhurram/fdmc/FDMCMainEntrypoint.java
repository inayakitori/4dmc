package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanPlaceW;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.gmail.inayakitorikhurram.fdmc.network.packet.MovingPlayerC2SPayload;
import com.gmail.inayakitorikhurram.fdmc.network.packet.PlayerPlacementC2SPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public class FDMCMainEntrypoint implements ModInitializer{


	@Override
	public void onInitialize() {


		PayloadTypeRegistry.playC2S().register(MovingPlayerC2SPayload.ID, MovingPlayerC2SPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(PlayerPlacementC2SPacket.ID, PlayerPlacementC2SPacket.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(MovingPlayerC2SPayload.ID, (payload, context) -> {
			((CanStep)context.player()).scheduleStep(payload.stepDirection(), false);
		});


		//update placement direction serverside
		ServerPlayNetworking.registerGlobalReceiver(PlayerPlacementC2SPacket.ID, (payload, context) -> {
			int stepDirection = payload.stepDirection();
			((CanPlaceW)context.player()).setPlacementDirection4(stepDirection < 0? Optional.empty() : Optional.of(Direction.byIndex(stepDirection))); //jank
		});

	}
}
