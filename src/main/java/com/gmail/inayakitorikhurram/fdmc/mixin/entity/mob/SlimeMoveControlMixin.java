package com.gmail.inayakitorikhurram.fdmc.mixin.entity.mob;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.SidewaysSpeedW;
import net.minecraft.entity.mob.SlimeEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/entity/mob/SlimeEntity$SlimeMoveControl")
public abstract class SlimeMoveControlMixin {
	@Shadow
	@Final
	private SlimeEntity slime;

	@Inject(
		method = "tick",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/entity/mob/SlimeEntity;sidewaysSpeed:F",
			shift = At.Shift.AFTER,
			opcode = Opcodes.PUTFIELD
		)
	)
	void fdmc$immobileSlime(CallbackInfo ci){
		((SidewaysSpeedW) this.slime).setSidewaysSpeedW(0f);
	}
}
