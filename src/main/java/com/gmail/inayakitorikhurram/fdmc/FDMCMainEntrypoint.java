package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.commands.FDMCCommands;
import com.gmail.inayakitorikhurram.fdmc.commands.argument.Perspective4ArgumentType;
import com.gmail.inayakitorikhurram.fdmc.math.Perspective4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanPlaceW;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Perspective4Access;
import com.gmail.inayakitorikhurram.fdmc.network.packet.MovingPlayerC2SPayload;
import com.gmail.inayakitorikhurram.fdmc.network.packet.Perspective4C2SPacket;
import com.gmail.inayakitorikhurram.fdmc.network.packet.PlayerPlacementC2SPacket;
import com.gmail.inayakitorikhurram.fdmc.screen.FDMCScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricTrackedDataRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public class FDMCMainEntrypoint implements ModInitializer{
    public static final ScreenHandlerType<FDMCScreenHandler> GENERIC_9X12 = ScreenHandlerType.register("generic_9x12", FDMCScreenHandler::createGeneric9x12);


    public static final TrackedDataHandler<Perspective4> PERSPECTIVE_TRACKED_DATA_HANDLER = TrackedDataHandler.create(Perspective4.PACKET_CODEC);


	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playC2S().register(MovingPlayerC2SPayload.ID, MovingPlayerC2SPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(PlayerPlacementC2SPacket.ID, PlayerPlacementC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(Perspective4C2SPacket.ID, Perspective4C2SPacket.CODEC);

        FabricTrackedDataRegistry.register(Identifier.of("fdmc", "perspective"), PERSPECTIVE_TRACKED_DATA_HANDLER);
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

        ArgumentTypeRegistry.registerArgumentType(
                Identifier.of("fdmc", "perspective4"),
                Perspective4ArgumentType.class, ConstantArgumentSerializer.of(Perspective4ArgumentType::perspective4));

        CommandRegistrationCallback.EVENT.register(FDMCCommands::registerAll);

	}
}
