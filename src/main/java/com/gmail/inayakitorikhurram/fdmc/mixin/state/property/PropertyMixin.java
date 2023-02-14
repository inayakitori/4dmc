package com.gmail.inayakitorikhurram.fdmc.mixin.state.property;

import com.gmail.inayakitorikhurram.fdmc.state.property.Property4;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;

@Mixin(Property.class)
public abstract class PropertyMixin {
    @Redirect(method = "toString()Ljava/lang/String;", at = @At(value = "INVOKE", target = "Lnet/minecraft/state/property/Property;getValues()Ljava/util/Collection;"))
    private <T extends Comparable<T>> Collection<T> toStringUseProperty4(Property<T> instance) {
        return Property4.getValues(instance);
    }

    @Mixin(Property.Value.class)
    public static abstract class ValueMixin {
        @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/state/property/Property;getValues()Ljava/util/Collection;"))
        private <T extends Comparable<T>> Collection<T> constructorUseProperty4(Property<T> instance) {
            return Property4.getValues(instance);
        }
    }
}
