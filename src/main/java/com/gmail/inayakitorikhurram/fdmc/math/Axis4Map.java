package com.gmail.inayakitorikhurram.fdmc.math;

import com.google.common.collect.Maps;
import net.minecraft.util.math.Direction.Axis;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Axis4Map<V> extends EnumMap<Axis, V> {

    private final Map<Axis,V> innerMap;

    public Axis4Map() {
        super(Maps.newEnumMap(Axis.class));
        super.clear();
        innerMap = new HashMap<>();
    }

    public static <V> Axis4Map<V> of(Map<Axis, V> givenMap) {
        Axis4Map<V> map = new Axis4Map<>();
        map.putAll(givenMap);
        return map;
    }

    public static <V> Axis4Map<V> of(Map<Axis, V> givenMap, V defaultValue) {
        Axis4Map<V> map = new Axis4Map<>();
        map.putAll(givenMap);
        map.put(Direction4Constants.Axis4Constants.W ,defaultValue);
        return map;
    }

    @Override
    public void clear() {
        //super.clear();
        innerMap.clear();
    }

    @Override
    public V put(Axis key, V value) {
        return innerMap.put(key, value);
    }

    @Override
    public V get(Object key) {
        return innerMap.get(key);
    }

    @Override
    public @NotNull Set<Entry<Axis, V>> entrySet() {
        return innerMap.entrySet();
    }
}
