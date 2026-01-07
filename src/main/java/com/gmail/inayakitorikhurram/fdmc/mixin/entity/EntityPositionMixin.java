package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityPosition;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Vec3d;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPosition.class)
public class EntityPositionMixin {
	@Mutable
	@Shadow
	@Final
	private Vec3d position;

	@Redirect(
		method = "<clinit>",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/util/math/Vec3d;PACKET_CODEC:Lnet/minecraft/network/codec/PacketCodec;",
			opcode = Opcodes.GETSTATIC,
			ordinal = 0
		)
	)
	private static PacketCodec<ByteBuf, Vec3d> fdmc$positionVec4d() {
		return Vec4d.PACKET_CODEC;
	}

	@Inject(method = "<init>", at = @At(value = "TAIL"))
	void fdmc$positionVec4d(Vec3d position, Vec3d deltaMovement, float yaw, float pitch, CallbackInfo ci) {
		this.position = Vec4d.of(position);
	}
}
