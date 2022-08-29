package com.gmail.inayakitorikhurram.fdmc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
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

	private HashMap<Long, ChunkPos> forceLoadedChunks = new HashMap<Long, ChunkPos>();

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
		});

		ServerTickEvents.START_WORLD_TICK.register(world -> {
			updateChunks(world);
		});

	}

	public void updateChunks(ServerWorld world){
		ServerChunkManager cm = world.getChunkManager();
		ChunkTicketManager tm = ((HasTicketManager)cm).getTicketManager();
		int simDistance = 0; //TODO get properly

		ArrayList<ChunkPos> chunksToAdd = new ArrayList<>();
		{//find out what chunks should be loaded and load them
			int[] playerPos4;
			int[] chunkPos4;
			for(ServerPlayerEntity player : world.getPlayers()){
				playerPos4 = FDMCMath.toChunkPos4(player.getChunkPos());
				for(int k = simDistance/2; k > -simDistance/2; k--){
					int dw = Math.abs(k);
					int sliceSimDistance = simDistance - 2 * dw;
					for(int dx = sliceSimDistance; dx > -sliceSimDistance; dx--){
						for(int dz = sliceSimDistance; dz > -sliceSimDistance; dz--){
							//if(i * i + j * j < sliceSimDistance * sliceSimDistance){
								chunkPos4 = Arrays.copyOf(playerPos4, 3);
								chunkPos4[0] += dx;
								chunkPos4[1] += dz;
								chunkPos4[2] += dw;
								ChunkPos chunkPos = FDMCMath.toChunkPos3(chunkPos4);
								chunksToAdd.add(chunkPos);
							//}
						}
					}
				}
			}

			for(ChunkPos chunkToAdd : chunksToAdd){
				//if(!forceLoadedChunks.contains(chunkToAdd)) {
					tm.addTicketWithLevel(ChunkTicketType.FORCED, chunkToAdd, 31, chunkToAdd);
				//}
				forceLoadedChunks.put(chunkToAdd.toLong(), chunkToAdd);
			}
		}

		{//now find those player ones further away that should be removed
			int[] playerPos4;
			int[] chunkPos4;
			ArrayList<ChunkPos> chunksToRemove = new ArrayList<>();
			for (ChunkPos forceLoadedChunk : forceLoadedChunks.values()) {
				boolean shouldRemove = true;
				chunkPos4 = FDMCMath.toChunkPos4(forceLoadedChunk);
				for (ServerPlayerEntity player : world.getPlayers()) {
					playerPos4 = FDMCMath.toChunkPos4(player.getChunkPos());
					int dx = Math.abs(chunkPos4[0] - playerPos4[0]);
					int dz = Math.abs(chunkPos4[1] - playerPos4[1]);
					int dw = Math.abs(chunkPos4[2] - playerPos4[2]);
					if(dx + dz + 2 * dw < simDistance || dw == 0){
						shouldRemove = false;
					}
				}
				if(shouldRemove){
					chunksToRemove.add(forceLoadedChunk);
				}
			}

			//since no players are close to it, remove that ticket
			for(ChunkPos chunkToRemove : chunksToRemove){
				tm.removeTicketWithLevel(ChunkTicketType.FORCED, chunkToRemove, 31, chunkToRemove);
				forceLoadedChunks.remove(chunkToRemove.toLong());
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
