package com.gmail.inayakitorikhurram.fdmc.mixin.client.render;

import com.gmail.inayakitorikhurram.fdmc.math.Box4;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Redirect(method = "findCrosshairTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"))
	Box expand4(Box box, double x, double y, double z) {
		return Box4.converted(box).expand(x, y, z, (x+z)*0.5);
	}
}
