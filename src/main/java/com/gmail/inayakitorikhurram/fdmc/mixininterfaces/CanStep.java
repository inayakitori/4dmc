package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;

import java.util.Optional;

public interface CanStep extends Perspective4Access{
    /**
     *
     * @param object Something that could implement CanStep
     * @param <T>    the object's type
     * @return an Optional that is emtpy if object does not extend step but returns the object if it does
     */
    static <T> Optional<CanStep> of(T object) {
        if (object instanceof CanStep) {
            return Optional.of((CanStep) object);
        } else {
            return Optional.empty();
        }
    }

    /**
     * This should either send the step to the appropriate logical side or schedule the step so
     * applyScheduledStep can use it
     *
     * @param moveDirection
     */
    void scheduleStep(int moveDirection, boolean retryOnFail);

    /**
     * This should handle actually doing the step action, which will look different for {@link ClientPlayerEntity}
     * vs a normal {@link Entity}. This should be called during an entities move command
     *
     */
    void applyScheduledStep();

    int getCurrentStepDirection();

    int stepCooldown();

}
