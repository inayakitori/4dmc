package com.gmail.inayakitorikhurram.fdmc.mixin.util;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.PlayerInput4;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.util.PlayerInput$1")
public class PlayerInput$1Mixin {
	@Unique	private static final byte ANTH = 0b01;
	@Unique	private static final byte KENTH = 0b10;
	
	@Inject(method = "encode(Lnet/minecraft/network/PacketByteBuf;Lnet/minecraft/util/PlayerInput;)V", at = @At(value = "TAIL"))
	void encode4(PacketByteBuf packetByteBuf, PlayerInput playerInput, CallbackInfo ci) {
		PlayerInput4 playerInput4 = (PlayerInput4) (Object) playerInput;
		assert playerInput4 != null;

		byte b = 0;
		b |= playerInput4.anth() ? ANTH : 0;
		b |= playerInput4.kenth() ? KENTH : 0;
		packetByteBuf.writeByte(b);
	}

	@Inject(method = "decode(Lnet/minecraft/network/PacketByteBuf;)Lnet/minecraft/util/PlayerInput;", at = @At(value = "TAIL"))
	void decode4(PacketByteBuf packetByteBuf, CallbackInfoReturnable<PlayerInput> cir) {
		byte b = packetByteBuf.readByte();
		boolean anth = (b & ANTH) != 0;
		boolean kenth = (b & KENTH) != 0;

		PlayerInput4 playerInput4 = (PlayerInput4) (Object) cir.getReturnValue();
		assert playerInput4 != null;
		playerInput4.setFlags4(anth, kenth);
	}
}
