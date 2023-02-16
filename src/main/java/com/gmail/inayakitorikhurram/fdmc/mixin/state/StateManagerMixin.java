package com.gmail.inayakitorikhurram.fdmc.mixin.state;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.BlockSettings4Access;
import com.gmail.inayakitorikhurram.fdmc.state.property.Property4;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(StateManager.class)
public abstract class StateManagerMixin {
    @Shadow public abstract Object getOwner();

    @Shadow @Final private ImmutableSortedMap<String, Property<?>> properties;

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;of(Ljava/lang/Object;)Ljava/util/stream/Stream;", ordinal = 0))
    private Stream<List<List<Object>>> getValues4D(Object t) {
        Stream<List<List<Object>>> stream = Stream.of(Collections.emptyList());
        for (Property property : this.properties.values()) {
            stream = stream.flatMap(list -> {
                Object owner = this.getOwner();
                return (owner instanceof BlockSettings4Access && ((BlockSettings4Access) owner).uses4DProperties() && property instanceof Property4 ? ((Property4) property).getValues4() : property.getValues()).stream().map(comparable -> {
                    ArrayList<Object> list2 = Lists.newArrayList(list);
                    list2.add(Pair.of(property, comparable));
                    return list2;
                });
            });
        }
        return stream;
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;flatMap(Ljava/util/function/Function;)Ljava/util/stream/Stream;", ordinal = 0))
    private Stream<List<List<Object>>> cancelFlatMap(Stream instance, Function<List<List<Object>>, List<List<Object>>> function) {
        return instance;
    }
}
