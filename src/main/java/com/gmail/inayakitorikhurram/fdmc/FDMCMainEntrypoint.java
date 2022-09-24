package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.gmail.inayakitorikhurram.fdmc.supportstructure.SuffocationSupport;
import com.gmail.inayakitorikhurram.fdmc.supportstructure.SupportHandler;
import com.gmail.inayakitorikhurram.fdmc.supportstructure.UnderSupport;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class FDMCMainEntrypoint implements ModInitializer {
	private SupportHandler supportHandler;

	@Override
	public void onInitialize() {

		supportHandler = new SupportHandler();

		ServerPlayNetworking.registerGlobalReceiver(FDMCConstants.MOVING_PLAYER_ID, (server, player, handler, bufIn, responseSender) -> {
			int moveDirection = bufIn.readInt();

			if(((CanStep)player).canStep(moveDirection)) {

				((CanStep)player).setStepDirection(moveDirection);
				((CanStep)player).setStepping(true);

				Vec3d vel = player.getVelocity();
				//write to client-side buffer
				PacketByteBuf bufOut = writeS2CStepBuffer(vel, moveDirection);
				Vec3d oldPos = player.getPos();
				Vec3d newPos = oldPos.add(moveDirection * FDMCConstants.STEP_DISTANCE, 0, 0);
				//place a block underneath player and clear stone
				supportHandler.queueSupport(UnderSupport.class, player, new BlockPos(newPos), new BlockPos(oldPos));
				supportHandler.queueSupport(SuffocationSupport.class, player, new BlockPos(newPos), new BlockPos(oldPos));

				//actually tp player
				double[] pos4 = FDMCMath.toPos4(newPos);
				player.teleport(newPos.x, newPos.y, newPos.z);
				ServerPlayNetworking.send(player, FDMCConstants.MOVING_PLAYER_ID, bufOut);
				player.sendMessage(Text.of(
						"Moving " + player.getEntityName() + " " + (moveDirection == 1 ? "ana" : "kata") + " to:\n(" +
								(int) pos4[3] + "," +
								(int) pos4[0] + "," +
								(int) pos4[1] + "," +
								(int) pos4[2] + ")"
				));
			} else{
				//write to client-side buffer, don;t actually need vel
				PacketByteBuf bufOut = writeS2CStepBuffer(player.getVelocity(), 0);
				ServerPlayNetworking.send(player, FDMCConstants.MOVING_PLAYER_ID, bufOut);
			}
		});

		ServerTickEvents.START_WORLD_TICK.register(world -> {
			supportHandler.tickSupports();

			//update player positions

		});

	}

	private PacketByteBuf writeS2CStepBuffer(Vec3d vel, int stepDirection){
		PacketByteBuf bufOut = PacketByteBufs.create();
		bufOut.writeDouble(vel.x);
		bufOut.writeDouble(vel.y);
		bufOut.writeDouble(vel.z);
		bufOut.writeInt(stepDirection);
		return bufOut;
	}

}
