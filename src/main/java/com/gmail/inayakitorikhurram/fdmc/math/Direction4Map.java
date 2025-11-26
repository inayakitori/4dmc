package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import com.google.common.collect.Maps;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Direction4Map<V> extends EnumMap<Direction, V> {

    private final Map<Direction,V> innerMap;

    public Direction4Map() {
        super(Maps.newEnumMap(Direction.class));
        super.clear();
        innerMap = new HashMap<>();
    }

    public static <V> Direction4Map<V> of(Map<Direction, V> givenMap) {
        Direction4Map<V> map = new Direction4Map<>();
        map.putAll(givenMap);
        return map;
    }

    public static <V> Direction4Map<V> of(Map<Direction, V> givenMap, V defaultValue) {
        Direction4Map<V> map = new Direction4Map<>();
        map.putAll(givenMap);
        map.put(Direction4Constants.ANA ,defaultValue);
        map.put(Direction4Constants.KATA ,defaultValue);
        return map;
    }

    @Override
    public void clear() {
        //super.clear();
        innerMap.clear();
    }

    @Override
    public V put(Direction key, V value) {
        return innerMap.put(key, value);
    }

    @Override
    public V get(Object key) {
        return innerMap.get(key);
    }

    @Override
    public @NotNull Set<Entry<Direction, V>> entrySet() {
        return innerMap.entrySet();
    }
}
