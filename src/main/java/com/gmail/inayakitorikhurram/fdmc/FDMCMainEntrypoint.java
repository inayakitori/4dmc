package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.mixin.HasTicketManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class FDMCMainEntrypoint implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("fdmc");

	private ArrayList<ChunkPos> forceLoadedChunks = new ArrayList<>();

	@Override
	public void onInitialize() {



		ServerPlayNetworking.registerGlobalReceiver(FDMCConstants.MOVE_PLAYER_ID, (server, player, handler, bufIn, responseSender) -> {
			int moveDirection = bufIn.getInt(0);
			LOGGER.info("Move player command received by server to move in direction " + (moveDirection == 1 ? "kata" : "ana"));

			Vec3d vel = player.getVelocity();
			PacketByteBuf bufOut = PacketByteBufs.create();
			bufOut.writeDouble(vel.x);
			bufOut.writeDouble(vel.y);
			bufOut.writeDouble(vel.z);
			Vec3d newPos = player.getPos().add(moveDirection * FDMCConstants.STEP_DISTANCE, 0, 0);
			double[] pos4 = FDMCMath.toPos4(newPos);
			player.teleport(newPos.x, newPos.y, newPos.z);
			ServerPlayNetworking.send(player, FDMCConstants.MOVE_PLAYER_ID, bufOut);
			player.sendMessage(Text.of(
					"Moving " + player.getEntityName() + " " + (moveDirection == 1 ? "kata" : "ana") + "\n" +
							pos4[0] + "\n" +
							pos4[1] + "\n" +
							pos4[2] + "\n" +
							pos4[3] + "\n"
			));
			updateChunks(player.getWorld());

		});

		ServerTickEvents.START_WORLD_TICK.register(world -> {
			updateChunks(world);
		});

	}

	public void updateChunks(ServerWorld world){
		ArrayList<ChunkPos> chunksToAdd = new ArrayList<>();
		ArrayList<ChunkPos> chunksToRemove = new ArrayList<>();
		ServerChunkManager cm = world.getChunkManager();
		ChunkTicketManager tm = ((HasTicketManager)cm).getTicketManager();
		int simDistance = 10; //TODO get properly

		//find out what chunks should be loaded
		int[] playerPos4;
		int[] chunkPos4;
		for(ServerPlayerEntity player : world.getPlayers()){
			playerPos4 = FDMCMath.toChunkPos4(player.getChunkPos());
			for(int k = 1; k < simDistance/2; k++){
				int sliceSimDistance = simDistance - 2 * k;
				for(int i = -sliceSimDistance; i <= sliceSimDistance; i++){
					for(int j = -sliceSimDistance; j <= sliceSimDistance; j++){
						if(i * i + j * j < sliceSimDistance * sliceSimDistance){
							chunkPos4 = playerPos4;
							chunkPos4[0] += i;
							chunkPos4[1] += j;
							chunkPos4[2] += k;
							chunksToAdd.add(FDMCMath.toChunkPos3(chunkPos4));
						}
					}
				}
			}
		}

		/**
		for(ChunkPos chunkPos : forceLoadedChunks){
			chunkPos4 = FDMCMath.toChunkPos4(chunkPos);
			int smallestDistanceSquared = Integer.MAX_VALUE;
			for(ServerPlayerEntity player : world.getPlayers()){
				playerPos4 = FDMCMath.toChunkPos4(player.getChunkPos());
				int chunkDistanceSquared =
						    (chunkPos4[0] - playerPos4[0]) * (chunkPos4[0] - playerPos4[0]) +
						    (chunkPos4[1] - playerPos4[1]) * (chunkPos4[1] - playerPos4[1]) +
						4 * (chunkPos4[2] - playerPos4[2]) * (chunkPos4[2] - playerPos4[2]);
				if(chunkDistanceSquared < smallestDistanceSquared){
					smallestDistanceSquared = chunkDistanceSquared;
				}
			}
			tm.removePersistentTickets(); //TODO only remove those unavailable

			//tm.addTicketWithLevel(ChunkTicketType.PLAYER, chunkPos, i, chunkPos);
		}
		 **/
	}


}
