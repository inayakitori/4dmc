package com.gmail.inayakitorikhurram.fdmc.state.property;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;

import java.util.Collection;
import java.util.List;

public class EnumProperty4<T extends Enum<T> & StringIdentifiable> extends EnumProperty<T> implements Property4<T> {
    private final ImmutableList<T> values3d;

    protected EnumProperty4(String name, Class<T> type, List<T> values3d, List<T> values) {
        super(name, type, values);
        this.values3d = ImmutableList.copyOf(values3d);
    }

    public static <T extends Enum<T> & StringIdentifiable> EnumProperty4<T> of(String name, Class<T> type, List<T> values3d, List<T> values) {
        return new EnumProperty4<>(name, type, values3d, values);
    }

//    @Override
//    public int ordinal(T enum_) {
//        return enumOrdinalToPropertyOrdinal[enum_.ordinal()];
//    }

    @Override
    public List<T> getValues() {
        return values3d;
    }

    public List<T> getValues4() {
        return super.getValues();
    }
}
