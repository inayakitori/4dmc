package com.gmail.inayakitorikhurram.fdmc.state.property;

import com.google.common.collect.ImmutableSet;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;

import java.util.Collection;

public class EnumProperty4<T extends Enum<T> & StringIdentifiable> extends EnumProperty<T> implements Property4<T> {
    private final ImmutableSet<T> values3d;

    protected EnumProperty4(String name, Class<T> type, Collection<T> values3d, Collection<T> values) {
        super(name, type, values);
        this.values3d = ImmutableSet.copyOf(values3d);
    }

    public static <T extends Enum<T> & StringIdentifiable> EnumProperty4<T> of(String name, Class<T> type, Collection<T> values3d, Collection<T> values) {
        return new EnumProperty4<>(name, type, values3d, values);
    }

    @Override
    public Collection<T> getValues() {
        return values3d;
    }

    public Collection<T> getValues4() {
        return super.getValues();
    }
}
