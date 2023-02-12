package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DirectionProperty.class)
public class DirectionPropertyMixin {

//    @Redirect(method = "of(Ljava/lang/String;Ljava/util/function/Predicate;)Lnet/minecraft/state/property/DirectionProperty;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"))
//    private static Direction[] fdmc$extendDirectionValues(){
//        return Direction4Constants.VALUES;
//    }
}
