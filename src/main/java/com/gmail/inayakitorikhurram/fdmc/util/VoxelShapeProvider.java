package com.gmail.inayakitorikhurram.fdmc.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.shape.VoxelShape;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class VoxelShapeProvider implements Function<BlockState, Optional<VoxelShape>>{
    private Builder builder;
    private boolean built = false;
    private final Map<BlockState, VoxelShape> voxelShapeMap = Maps.newHashMap();

    protected VoxelShapeProvider(Builder builder) {
        this.builder = builder;
    }

    @Override
    public Optional<VoxelShape> apply(BlockState blockState) {
        // We build lazily to ensure the state set of the block is built
        if (!built) {
            // intentionally set to true early so recursive calls can fall through
            built = true;
            builder.buildMap(voxelShapeMap);
            // dereference the builder
            builder = null;
        }
        return voxelShapeMap.containsKey(blockState) ? Optional.of(voxelShapeMap.get(blockState)) : Optional.empty();
    }

    public static Builder builder(Block block) {
        return new Builder(block);
    }

    public static <P extends Property<V>, V extends Comparable<V>> Switch<P, V> newSwitch(P property) {
        return new Switch<>(property);
    }

    public static <P extends BooleanProperty> Switch<P, Boolean> boolSwitch(P property, Function<BlockState, VoxelShape> ifTrue, Function<BlockState, VoxelShape> ifFalse) {
        return newSwitch(property).addCase(true, ifTrue).addCase(false, ifFalse);
    }

    public static Function<BlockState, VoxelShape> supplyNull() {
        return (state) -> null;
    }

    public static Function<BlockState, VoxelShape> supply(VoxelShape shape) {
        return (state) -> shape;
    }

    public static class Switch<P extends Property<V>, V extends Comparable<V>> implements Function<BlockState, VoxelShape> {
        private final List<Case<V>> cases = Lists.newLinkedList();
        @SuppressWarnings("unused")
		private Supplier<VoxelShape> defaultCase = () -> null;
        private final P property;

        protected Switch(P property) {
            this.property = property;
        }

        private Switch<P, V> addCase(Case<V> caze) {
            cases.add(caze);
            return this;
        }

        public Switch<P, V> addCase(V value, Function<BlockState, VoxelShape> function) {
            this.addCase(function, value);
            return this;
        }

        public Switch<P, V> addCase(V value1, V value2, Function<BlockState, VoxelShape> function) {
            this.addCase(function, value1, value2);
            return this;
        }

        public Switch<P, V> addCase(V value1, V value2, V value3, Function<BlockState, VoxelShape> function) {
            this.addCase(function, value1, value2, value3);
            return this;
        }

        public Switch<P, V> addCase(Set<V> values, Function<BlockState, VoxelShape> function) {
            this.addCase(values::contains, function);
            return this;
        }

        @SafeVarargs
        public final Switch<P, V> addCase(Function<BlockState, VoxelShape> function, V... values) {
            this.addCase(Set.of(values), function);
            return this;
        }

        public Switch<P, V> addCase(Predicate<V> predicate, Function<BlockState, VoxelShape> function) {
            this.addCase(new Case<>(predicate, function));
            return this;
        }

        public Switch<P, V> setDefault(VoxelShape voxelShape) {
            defaultCase = () -> voxelShape;
            return this;
        }

        public Switch<P, V> setDefault(Supplier<VoxelShape> voxelShapeSupplier) {
            defaultCase = voxelShapeSupplier;
            return this;
        }

        @Override
        public VoxelShape apply(BlockState blockState) {
            V value = blockState.get(property);
            return cases.stream()
                    .dropWhile((caze) -> !caze.test(value))
                    .findFirst()
                    .map(caze -> caze.apply(blockState))
                    .orElse(null);
        }
    }

    protected static class Case<V extends Comparable<V>> implements Predicate<V>, Function<BlockState, VoxelShape> {
        private final Predicate<V> predicate;
        private final Function<BlockState, VoxelShape> function;

        protected Case(Predicate<V> predicate, Function<BlockState, VoxelShape> function) {
            this.predicate = predicate;
            this.function = function;
        }

        @Override
        public boolean test(V value) {
            return predicate.test(value);
        }


        @Override
        public VoxelShape apply(BlockState blockState) {
            return function.apply(blockState);
        }
    }

    public static class Builder {
        private final Block block;
        private Function<BlockState, Optional<VoxelShape>> function;

        protected Builder(Block block) {
            this.block = block;
        }

        public Builder set(Switch<?, ?> zwitch) {
            this.set(zwitch.andThen(Optional::ofNullable));
            return this;
        }

        public Builder set(Function<BlockState, Optional<VoxelShape>> function) {
            this.function = function;
            return this;
        }

        public VoxelShapeProvider build() {
            return new VoxelShapeProvider(this);
        }

        protected void buildMap(Map<BlockState, VoxelShape> map) {
            block.getStateManager().getStates()
                    .forEach(state -> function.apply(state)
                            .ifPresent(shape -> map.put(state, shape)));
        }
    }
}
