package com.gmail.inayakitorikhurram.fdmc.mixin.util;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.PlayerInput4;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInput.class)
public class PlayerInputMixin implements PlayerInput4 {
	@Unique	private boolean anth;
	@Unique	private boolean kenth;

	@Inject(method = "<init>", at = @At(value = "TAIL"))
	void init(boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, boolean bl6, boolean bl7, CallbackInfo ci){
		this.anth = false;
		this.kenth = false;
	}

	@Override public boolean anth() { return anth; }
	@Override public boolean kenth() { return kenth; }

	@Override
	public void setFlags4(boolean anth, boolean kenth) {
		this.anth = anth;
		this.kenth = kenth;
	}

	@WrapMethod(method = "toString")
	String toString(Operation<String> original){
		String originalString = original.call();
		return originalString.substring(0, originalString.length() - 1)
			+ ", anth="+anth + ", kenth="+kenth + "]";
	}

	@WrapMethod(method = "hashCode")
	int hashCode(Operation<Integer> original){
		return original.call()
			+ (anth ? 1<<8 : 0)
			+ (kenth ? 1<<9 : 0);
	}

	@WrapMethod(method = "equals")
	boolean equals(Object object, Operation<Boolean> original) {
		return original.call(object)
			&& ((PlayerInput4) object).anth() == anth
			&& ((PlayerInput4) object).kenth() == kenth;
	}


}

