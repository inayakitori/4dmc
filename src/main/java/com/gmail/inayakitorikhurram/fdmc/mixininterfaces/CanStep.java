package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

public interface CanStep {

    int getStepDirection();
    void setStepDirection(int stepDirection);
    boolean isStepping();
    void setStepping(boolean isStepping);
    boolean canStep(int stepDirection);

}
