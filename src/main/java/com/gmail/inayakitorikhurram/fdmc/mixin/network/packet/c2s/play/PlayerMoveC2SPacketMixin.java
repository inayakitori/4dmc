package com.gmail.inayakitorikhurram.fdmc.mixin.network.packet.c2s.play;

import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Pos4Extension;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerMoveC2SPacket.class)
public abstract class PlayerMoveC2SPacketMixin implements Pos4Extension {
	@Shadow
	@Final
	protected boolean changePosition;
	@Unique	protected double x4 = 0d, w = 0d;

	@Override public double getX4() { return x4; }
	@Override public double getW() { return w; }

	@Override
	public double getX4(double currentX4) {
		return this.changePosition ? this.x4 : currentX4;
	}
	@Override
	public double getW(double currentW) {
		return this.changePosition ? this.w : currentW;
	}

	@Override public void setX4(double x4) { this.x4 = x4; }
	@Override public void setW(double w) { this.w = w; }

	@Mixin(PlayerMoveC2SPacket.PositionAndOnGround.class)
	public static class PositionAndOnGroundMixin {
		@Inject(method = "<init>(Lnet/minecraft/util/math/Vec3d;ZZ)V", at = @At("TAIL"))
		void storeFullPosFromInit(Vec3d pos, boolean onGround, boolean horizontalCollision, CallbackInfo ci){
			Pos4Extension this4 = (Pos4Extension) this;
			Vec4d pos4 = Vec4d.of(pos);
			this4.setX4(pos4.x4);
			this4.setW(pos4.w);
		}

		@Inject(method = "write", at = @At("TAIL"))
		void writeAdditionalFields(PacketByteBuf buf, CallbackInfo ci){
			Pos4Extension this4 = (Pos4Extension) this;
			buf.writeDouble(this4.getX4());
			buf.writeDouble(this4.getW());
		}

		@Inject(method = "read", at = @At("TAIL"))
		static private void readAdditionalFields(PacketByteBuf buf, CallbackInfoReturnable<PlayerMoveC2SPacket.PositionAndOnGround> cir){
			Pos4Extension this4 = (Pos4Extension) cir.getReturnValue();
			this4.setX4(buf.readDouble());
			this4.setW(buf.readDouble());
		}
	}

	@Mixin(PlayerMoveC2SPacket.Full.class)
	public static class FullMixin {
		@Inject(method = "<init>(Lnet/minecraft/util/math/Vec3d;FFZZ)V", at = @At("TAIL"))
		void storeFullPosFromInit(Vec3d pos, float yaw, float pitch, boolean onGround, boolean horizontalCollision, CallbackInfo ci){
			Pos4Extension this4 = (Pos4Extension) this;
			Vec4d pos4 = Vec4d.of(pos);
			this4.setX4(pos4.x4);
			this4.setW(pos4.w);
		}

		@Inject(method = "write", at = @At("TAIL"))
		void writeAdditionalFields(PacketByteBuf buf, CallbackInfo ci){
			Pos4Extension this4 = (Pos4Extension) this;
			buf.writeDouble(this4.getX4());
			buf.writeDouble(this4.getW());
		}

		@Inject(method = "read", at = @At("TAIL"))
		static private void readAdditionalFields(PacketByteBuf buf, CallbackInfoReturnable<PlayerMoveC2SPacket.PositionAndOnGround> cir){
			Pos4Extension this4 = (Pos4Extension) cir.getReturnValue();
			this4.setX4(buf.readDouble());
			this4.setW(buf.readDouble());
		}
	}
}
