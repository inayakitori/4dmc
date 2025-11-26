package com.gmail.inayakitorikhurram.fdmc.mixin.world;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RedstoneController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RedstoneController.class)
public class RedstoneControllerMixin {
    @Redirect(method = "calculateWirePowerAt", at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/Direction$Type;HORIZONTAL:Lnet/minecraft/util/math/Direction$Type;"))
    private Direction.Type fdmc$useValues4(){
        return Direction4Constants.Type4.HORIZONTAL4;
    }
}
