package com.gmail.inayakitorikhurram.fdmc.mixin.state.property;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.state.property.EnumProperty4;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnumProperty.class)
public abstract class EnumPropertyMixin<T extends Enum<T>>
        extends Property<T> {

    protected EnumPropertyMixin(String name, Class<T> type) {
        super(name, type);
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/Class;getEnumConstants()[Ljava/lang/Object;"))
    private Object[] useProperty4IfPossible(Class<T> clazz){
        T firstValue = clazz.getEnumConstants()[0];
        if(((Object) this) instanceof EnumProperty4<?>) {
            if(firstValue instanceof Direction) {
                return (T[]) Direction4Constants.VALUES;
            } else if(firstValue instanceof  Direction.AxisDirection){
                return Direction4Constants.Axis4Constants.VALUES;
            }
        }
        return clazz.getEnumConstants();
    }

}
