package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.supportstructure.SupportHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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
    boolean doesCollideWithBlocksAt(BlockPos pos);

    boolean doesCollideWithBlocksAt(BlockPos offset, boolean fromOffset);
    boolean doesCollideWithBlocks();



    void setPlacementDirection4(@Nullable Direction placementDirection4);
    void setPlacementDirection4(@NotNull Optional<Direction> placementDirection4);

    Optional<Direction> getPlacementDirection4();
}
