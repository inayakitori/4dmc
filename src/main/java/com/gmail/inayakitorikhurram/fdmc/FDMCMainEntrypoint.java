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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class FDMCMainEntrypoint implements ModInitializer {
	private SupportHandler supportHandler;

	@Override
	public void onInitialize() {

		supportHandler = new SupportHandler();

		ServerPlayNetworking.registerGlobalReceiver(FDMCConstants.MOVING_PLAYER_ID, (server, player, handler, bufIn, responseSender) -> {
			int moveDirection = bufIn.readInt();

			if(((CanStep)player).canStep(moveDirection)) {


				Vec3d vel = player.getVelocity();
				//write to client-side buffer
				Vec3d oldPos = player.getPos();
				Vec3d newPos = oldPos.add(moveDirection * FDMCConstants.STEP_DISTANCE, 0, 0);
				//place a block underneath player and clear stone
				supportHandler.queueSupport(UnderSupport.class, player, new BlockPos(newPos), new BlockPos(oldPos));
				supportHandler.queueSupport(SuffocationSupport.class, player, new BlockPos(newPos), new BlockPos(oldPos));

				//actually tp player
				double[] pos4 = FDMCMath.toPos4(newPos);
				player.teleport(newPos.x, newPos.y, newPos.z);
				((CanStep)player).setSteppingGlobally(player, moveDirection, vel);
				player.sendMessage(Text.of(
						"Moving " + player.getEntityName() + " " + (moveDirection == 1 ? "ana" : "kata") + " to:\n(" +
								(int) pos4[3] + "," +
								(int) pos4[0] + "," +
								(int) pos4[1] + "," +
								(int) pos4[2] + ")"
				));
			}
		});

		ServerTickEvents.START_WORLD_TICK.register(world -> {
			supportHandler.tickSupports();
		});

	}

	public static PacketByteBuf writeS2CStepBuffer(int stepDirection, Vec3d vel ){
		PacketByteBuf bufOut = PacketByteBufs.create();
		bufOut.writeInt(stepDirection);
		if(vel!=null) {
		bufOut.writeInt(stepDirection);
			bufOut.writeDouble(vel.x);
			bufOut.writeDouble(vel.y);
			bufOut.writeDouble(vel.z);
		}
		return bufOut;
	}

}
