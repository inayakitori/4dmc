package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanPlaceW;
import com.gmail.inayakitorikhurram.fdmc.network.packet.PlayerPlacementC2SPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public class FDMCMainEntrypoint implements ModInitializer{
	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playC2S().register(PlayerPlacementC2SPacket.ID, PlayerPlacementC2SPacket.CODEC);

        Registry.register(Registries.CUSTOM_STAT, FDMCConstants.STAT_STEP_COUNT.getPath(), FDMCConstants.STAT_STEP_COUNT);
        Stats.CUSTOM.getOrCreateStat(FDMCConstants.STAT_STEP_COUNT, StatFormatter.DISTANCE);

		//update placement direction serverside
		ServerPlayNetworking.registerGlobalReceiver(PlayerPlacementC2SPacket.ID, (payload, context) -> {
			int stepDirection = payload.stepDirection();
			((CanPlaceW)context.player()).setPlacementDirection4(stepDirection < 0? Optional.empty() : Optional.of(Direction.byIndex(stepDirection))); //jank
		});
	}
}
