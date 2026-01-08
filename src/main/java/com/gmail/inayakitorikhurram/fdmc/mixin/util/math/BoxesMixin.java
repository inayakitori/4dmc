package com.gmail.inayakitorikhurram.fdmc.mixin.util.math;

import com.gmail.inayakitorikhurram.fdmc.math.Box4;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Boxes;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Boxes.class)
public class BoxesMixin {
	@Inject(method = "stretch", at = @At(value = "HEAD"), cancellable = true)
	private static void stretch4(Box box, Direction direction, double length, CallbackInfoReturnable<Box> cir){
		if (box instanceof Box4 box4) {
			cir.setReturnValue(box4.stretch(direction, length));
		}
	}
}
