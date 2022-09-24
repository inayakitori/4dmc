package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface CanStep {

    int getStepDirection();
    void setStepDirection(int stepDirection);
    boolean isStepping();
    void setStepping(boolean isStepping);
    boolean canStep(int stepDirection);
    void updateMoveDirections();
    boolean[] getMoveDirections();

}
