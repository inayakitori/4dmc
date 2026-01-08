package com.gmail.inayakitorikhurram.fdmc.mixin.client.input;

import com.gmail.inayakitorikhurram.fdmc.FDMCClientEntrypoint;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.PlayerInput4;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends Input {
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
}
