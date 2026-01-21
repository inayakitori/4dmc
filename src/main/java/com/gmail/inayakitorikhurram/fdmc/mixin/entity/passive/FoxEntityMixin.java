package com.gmail.inayakitorikhurram.fdmc.mixin.entity.passive;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.SidewaysSpeedW;
import net.minecraft.entity.passive.FoxEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoxEntity.class)
public abstract class FoxEntityMixin implements SidewaysSpeedW {
	@Inject(
		method = "tickMovement",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/entity/passive/FoxEntity;sidewaysSpeed:F",
			shift = At.Shift.AFTER,
			opcode = Opcodes.PUTFIELD
		)
	)
	void fdmc$immobileFox(CallbackInfo ci){
		setSidewaysSpeedW(0f);
	}
}
