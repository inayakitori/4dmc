package com.gmail.inayakitorikhurram.fdmc.datagen;

import com.google.common.collect.ImmutableList;
import net.minecraft.data.client.PropertiesMap;
import net.minecraft.state.property.Property;

import java.util.List;
import java.util.stream.Collectors;

public class ExtendedPropertiesMap extends PropertiesMap {
    private static final ExtendedPropertiesMap EMPTY = new ExtendedPropertiesMap(List.of());
    protected ExtendedPropertiesMap(List<Property.Value<?>> values) {
        super(values);
    }

    public static ExtendedPropertiesMap empty() {
        return EMPTY;
    }

    public static ExtendedPropertiesMap withValues(Property.Value<?> ... values) {
        return new ExtendedPropertiesMap(ImmutableList.copyOf(values));
    }

    public static ExtendedPropertiesMap of(PropertiesMap propertiesMap) {
        return propertiesMap instanceof ExtendedPropertiesMap ? (ExtendedPropertiesMap) propertiesMap : new ExtendedPropertiesMap(propertiesMap.values);
    }

    public List<Property.Value<?>> getValues() {
        return List.copyOf(this.values);
    }

    public ExtendedPropertiesMap withValue(Property.Value<?> value) {
        return new ExtendedPropertiesMap(ImmutableList.<Property.Value<?>>builder().addAll(this.values).add(value).build());
    }

    public ExtendedPropertiesMap replaceValue(Property.Value<?> value) {
        Property<?> property = value.property();
        return new ExtendedPropertiesMap(this.values.stream().map(val -> val.property() == property ? value : val).collect(Collectors.toList()));
    }
}
