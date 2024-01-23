package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface CanStep {

    /**
     *
     * @param object Something that could implement CanStep
     * @return an Optional that is emtpy if object does not extend step but returns the object if it does
     * @param <T> the object's type
     */
    static <T> Optional<CanStep> of(T object){
        if(object instanceof CanStep) {
            return Optional.of((CanStep) object);
        } else{
            return Optional.empty();
        }
    }
    void scheduleStep(int moveDirection);
    int getCurrentStepDirection();
    boolean canStep(int stepDirection);

    void setPlacementDirection4(@Nullable Direction placementDirection4);
    void setPlacementDirection4(@NotNull Optional<Direction> placementDirection4);
    Optional<Direction> getPlacementDirection4();
}
