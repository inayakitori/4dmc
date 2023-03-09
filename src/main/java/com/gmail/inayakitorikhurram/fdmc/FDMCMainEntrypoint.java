package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.gmail.inayakitorikhurram.fdmc.screen.FDMCContainerScreen;
import com.gmail.inayakitorikhurram.fdmc.screen.FDMCScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public class FDMCMainEntrypoint implements ModInitializer{

	public static final ScreenHandlerType<FDMCScreenHandler> GENERIC_9X12 = ScreenHandlerType.register("generic_9x12", FDMCScreenHandler::createGeneric9x12);

	public static final EntityModelLayer CHEST_W = EntityModelLayers.registerMain("chest_w");
	public static final EntityModelLayer DOUBLE_CHEST_LEFT_W = EntityModelLayers.registerMain("double_chest_left_w");
	public static final EntityModelLayer DOUBLE_CHEST_RIGHT_W = EntityModelLayers.registerMain("double_chest_right_w");
	public static final EntityModelLayer QUAD_CHEST_W = EntityModelLayers.registerMain("quad_chest_w");

	static{
		HandledScreens.register(GENERIC_9X12, FDMCContainerScreen::new);
	}

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
}
