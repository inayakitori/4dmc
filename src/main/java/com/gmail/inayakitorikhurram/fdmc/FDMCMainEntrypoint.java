package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public class FDMCMainEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {

		ServerPlayNetworking.registerGlobalReceiver(FDMCConstants.MOVING_PLAYER_ID, (server, player, handler, bufIn, responseSender) -> {
			int moveDirection = bufIn.readInt();
			((CanStep)player).scheduleStep(moveDirection);
		});

	}



	public static PacketByteBuf writeS2CStepBuffer(int stepDirection, Vec3d vel ){
		PacketByteBuf bufOut = PacketByteBufs.create();
		bufOut.writeInt(stepDirection);
		if(vel!=null) {
			bufOut.writeDouble(vel.x);
			bufOut.writeDouble(vel.y);
			bufOut.writeDouble(vel.z);
		}
		return bufOut;
	}

}
