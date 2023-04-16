package com.gmail.inayakitorikhurram.fdmc.mixin.world;

import net.minecraft.world.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Heightmap.class)
public class HeightmapMixin {

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "", opcode = Opcodes.PUTFIELD))
    private void injected(Something something, int x) {
        something.aaa = x + doSomething5();
    }
}
