package com.gmail.inayakitorikhurram.fdmc.state.property;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Direction4Property extends DirectionProperty implements Property4<Direction> {
    private final ImmutableSet<Direction> values3d;

    protected Direction4Property(String name, Collection<Direction> values) {
        super(name, values);
        ImmutableSet<Direction> defaultDirections = ImmutableSet.copyOf(Direction.values());
        values3d = values.stream().filter(defaultDirections::contains).collect(ImmutableSet.toImmutableSet());
    }

    public static Direction4Property of(String name) {
        return of(name, (Direction direction) -> true);
    }

    public static Direction4Property of(String name, Predicate<Direction> filter) {
        return of(name, Arrays.stream(Direction4Constants.VALUES).filter(filter).collect(Collectors.toList()));
    }

    public static Direction4Property of(String name, Direction ... values) {
        return of(name, Lists.newArrayList(values));
    }

    public static Direction4Property of(String name, Collection<Direction> values) {
        return new Direction4Property(name, values);
    }

    @Override
    public Collection<Direction> getValues() {
        return values3d;
    }

    public Collection<Direction> getValues4() {
        return super.getValues();
    }
}
