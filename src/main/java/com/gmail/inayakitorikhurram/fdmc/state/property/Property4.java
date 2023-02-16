package com.gmail.inayakitorikhurram.fdmc.state.property;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.BlockSettings4Access;
import net.minecraft.state.property.Property;

import java.util.Collection;
@SuppressWarnings("unchecked")
public interface Property4<T extends Comparable<T>> {
    static <T extends Comparable<T>> Collection<T> getValues(Property<T> property, Object owner) {
        if (owner instanceof BlockSettings4Access && ((BlockSettings4Access) owner).uses4DProperties()) {
            return getValues(property);
        }
        return property.getValues();
    }

    static <T extends Comparable<T>> Collection<T> getValues(Property<T> property) {
        if (property instanceof Property4<?>) {
            return ((Property4<T>) property).getValues4();
        }
        return property.getValues();
    }

    Collection<T> getValues4();
}
