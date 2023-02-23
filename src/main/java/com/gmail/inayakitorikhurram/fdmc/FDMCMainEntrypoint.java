package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public class FDMCMainEntrypoint implements ModInitializer, PreLaunchEntrypoint {

	@Override
	public void onInitialize() {

		ServerPlayNetworking.registerGlobalReceiver(FDMCConstants.MOVING_PLAYER_ID, (server, player, handler, bufIn, responseSender) -> {
			int stepDirection = bufIn.readInt();
			((CanStep)player).scheduleStep(stepDirection);
		});


		//update placement direction serverside
		ServerPlayNetworking.registerGlobalReceiver(FDMCConstants.PLAYER_PLACEMENT_DIRECTION_ID, (server, player, handler, bufIn, responseSender) -> {
			int stepDirection = bufIn.readInt();
			((CanStep)player).setPlacementDirection4(stepDirection < 0? Optional.empty() : Optional.of(Direction.byId(stepDirection))); //jank
		});

	}


	@Override
	public void onPreLaunch() {
		MixinExtrasBootstrap.init();
	}
}
