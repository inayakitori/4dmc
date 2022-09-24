package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.supportstructure.SupportHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public interface CanStep {

    boolean scheduleStep(int moveDirection);
    boolean step(int moveDirection);
    int getStepDirection();
    boolean isStepping();
    void setSteppingLocally(int stepDirection, Vec3d vel);
    void setSteppingGlobally(ServerPlayerEntity player, int stepDirection, Vec3d vel);
    boolean canStep(int stepDirection);
    SupportHandler getSupportHandler();

}
