package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface CanPlaceW {

    /**
     *
     * @param object Something that could implement CanStep
     * @param <T>    the object's type
     * @return an Optional that is emtpy if object does not extend step but returns the object if it does
     */
    static <T> Optional<CanPlaceW> of(T object) {
        if (object instanceof CanPlaceW) {
            return Optional.of((CanPlaceW) object);
        } else {
            return Optional.empty();
        }
    }

    void setPlacementDirection4(@Nullable Direction placementDirection4);
    void setPlacementDirection4(@NotNull Optional<Direction> placementDirection4);
    Optional<Direction> getPlacementDirection4();
}
