package com.gmail.inayakitorikhurram.fdmc.mixin.state;

import com.gmail.inayakitorikhurram.fdmc.state.property.Property4;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;

@Mixin(State.class)
public abstract class StateMixin {
    @Shadow @Final protected Object owner;

    @Redirect(method = "createWithTable(Ljava/util/Map;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/state/property/Property;getValues()Ljava/util/Collection;"))
    private <T extends Comparable<T>> Collection<T> method(Property<T> instance) {
        return Property4.getValues(instance, owner);
    }
}
