package com.gmail.inayakitorikhurram.fdmc.mixin.client.input;

import com.gmail.inayakitorikhurram.fdmc.FDMCClientEntrypoint;
import com.gmail.inayakitorikhurram.fdmc.math.Vec3f;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.PlayerInput4;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.math.Vec2f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {
	@Shadow
	private static float getMovementMultiplier(boolean positive, boolean negative) { return 0f; }

	@Inject(
		method = "tick",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/input/KeyboardInput;playerInput:Lnet/minecraft/util/PlayerInput;",
			opcode = Opcodes.PUTFIELD,
			shift = At.Shift.AFTER
		)
	)
	void addPlayerInput4(CallbackInfo ci){
		PlayerInput4 playerInput4 = (PlayerInput4) (Object) this.playerInput;
		assert playerInput4 != null;
		playerInput4.setFlags4(
			FDMCClientEntrypoint.moveAna.isPressed(),
			FDMCClientEntrypoint.moveKata.isPressed()
		);
	}

	@Redirect(
		method = "tick",
		at = @At(
			value = "NEW",
			target = "(FF)Lnet/minecraft/util/math/Vec2f;"
		)
	)
	Vec2f fdmc$(float x, float y){
		PlayerInput4 playerInput4 = (PlayerInput4) (Object) this.playerInput;
		assert playerInput4 != null;
		return new Vec3f(x, y, getMovementMultiplier(playerInput4.anth(), playerInput4.kenth()));
	}
}
