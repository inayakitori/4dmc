package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.supportstructure.SupportHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
public interface CanStep {

    boolean scheduleStep(int moveDirection);
    boolean step(int moveDirection);
    int getStepDirection();
    boolean isStepping();
    void setSteppingLocally(int tick, int stepDirection, Vec3d vel);
    void setSteppingGlobally(ServerPlayerEntity player, int stepDirection, Vec3d vel);
    boolean[] getPushableDirections();
    void updatePushableDirectionsLocally(boolean[] pushableDirection);
    void updatePushableDirectionsGlobally(ServerPlayerEntity player);
    boolean canStep(int stepDirection);
    SupportHandler getSupportHandler();

}
