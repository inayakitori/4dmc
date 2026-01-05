package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.math.Perspective4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanPlaceW;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Perspective4Access;
import com.gmail.inayakitorikhurram.fdmc.network.packet.MovingPlayerC2SPayload;
import com.gmail.inayakitorikhurram.fdmc.network.packet.Perspective4C2SPacket;
import com.gmail.inayakitorikhurram.fdmc.network.packet.PlayerPlacementC2SPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricTrackedDataRegistry;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public class FDMCMainEntrypoint implements ModInitializer{



    public static final TrackedDataHandler<Perspective4> PERSPECTIVE_TRACKED_DATA = TrackedDataHandler.create(Perspective4.PACKET_CODEC);


	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playC2S().register(MovingPlayerC2SPayload.ID, MovingPlayerC2SPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(PlayerPlacementC2SPacket.ID, PlayerPlacementC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(Perspective4C2SPacket.ID, Perspective4C2SPacket.CODEC);

        FabricTrackedDataRegistry.register(Identifier.of("fdmc", "perspective"), PERSPECTIVE_TRACKED_DATA);
        Registry.register(Registries.CUSTOM_STAT, FDMCConstants.STAT_STEP_COUNT.getPath(), FDMCConstants.STAT_STEP_COUNT);
        Stats.CUSTOM.getOrCreateStat(FDMCConstants.STAT_STEP_COUNT, StatFormatter.DISTANCE);

		ServerPlayNetworking.registerGlobalReceiver(MovingPlayerC2SPayload.ID, (payload, context) -> {
			((CanStep)context.player()).scheduleStep(payload.stepDirection(), false);
		});

		//update placement direction serverside
		ServerPlayNetworking.registerGlobalReceiver(PlayerPlacementC2SPacket.ID, (payload, context) -> {
			int stepDirection = payload.stepDirection();
			((CanPlaceW)context.player()).setPlacementDirection4(stepDirection < 0? Optional.empty() : Optional.of(Direction.byIndex(stepDirection))); //jank
		});

        ServerPlayNetworking.registerGlobalReceiver(Perspective4C2SPacket.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            ((Perspective4Access)player).setPerspective4(payload.perspective());
            // tracked data will handle sending information to other clients
        });

	}
}
