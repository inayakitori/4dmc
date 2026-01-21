package com.gmail.inayakitorikhurram.fdmc.mixin.client.input;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.PlayerInput4;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.input.Input;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Input.class)
public class InputMixin {
	@Shadow
	public PlayerInput playerInput;

	@WrapOperation(
		method = "jump",
		at = @At(
			value = "NEW",
			target = "(ZZZZZZZ)Lnet/minecraft/util/PlayerInput;"
		)
	)
	PlayerInput addPlayerInput4(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean sneak, boolean sprint, Operation<PlayerInput> original){
		PlayerInput playerInput = original.call(forward, backward, left, right, jump, sneak, sprint);
		PlayerInput4 playerInput4 = (PlayerInput4) (Object) playerInput;
		assert playerInput4 != null;
		PlayerInput4 oldPlayerInput4 = (PlayerInput4) (Object) this.playerInput;
		playerInput4.setFlags4(
			oldPlayerInput4.anth(),
			oldPlayerInput4.kenth()
		);
		return playerInput;
	}
}
