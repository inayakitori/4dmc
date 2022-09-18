package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.supportstructure.SupportHandler;
import com.gmail.inayakitorikhurram.fdmc.supportstructure.UnderSupport;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class FDMCMainEntrypoint implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("fdmc");

	private SupportHandler supportHandler;

	@Override
	public void onInitialize() {

		supportHandler = new SupportHandler();

		ServerPlayNetworking.registerGlobalReceiver(FDMCConstants.MOVE_PLAYER_ID, (server, player, handler, bufIn, responseSender) -> {
			int moveDirection = bufIn.getInt(0);
			LOGGER.info("Move player command received by server to move in direction " + (moveDirection == 1 ? "kata" : "ana"));

			Vec3d vel = player.getVelocity();
			//write to client-side buffer
			PacketByteBuf bufOut = PacketByteBufs.create();
			bufOut.writeDouble(vel.x);
			bufOut.writeDouble(vel.y);
			bufOut.writeDouble(vel.z);
			Vec3d oldPos= player.getPos();
			Vec3d newPos = oldPos.add(moveDirection * FDMCConstants.STEP_DISTANCE, 0, 0);
			//place a block underneath player and clear stone
			supportHandler.queueSupport(UnderSupport.class, player, new BlockPos(newPos), new BlockPos(oldPos), true);

			//actually tp player
			double[] pos4 = FDMCMath.toPos4(newPos);
			player.teleport(newPos.x, newPos.y, newPos.z);
			ServerPlayNetworking.send(player, FDMCConstants.MOVE_PLAYER_ID, bufOut);
			player.sendMessage(Text.of(
					"Moving " + player.getEntityName() + " " + (moveDirection == 1 ? "kata" : "ana") + " to:\n(" +
							(int)pos4[3] + "," +
							(int)pos4[0] + "," +
							(int)pos4[1] + "," +
							(int)pos4[2] + ")"
			));
		});

		ServerTickEvents.START_WORLD_TICK.register(world -> {
			supportHandler.tickSupports();
		});

	}


}
