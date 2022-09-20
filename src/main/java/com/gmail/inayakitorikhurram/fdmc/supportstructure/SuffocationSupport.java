package com.gmail.inayakitorikhurram.fdmc.supportstructure;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class SuffocationSupport extends SupportStructure{


    protected SuffocationSupport(ServerPlayerEntity player, BlockPos finalPos, BlockPos prevPos) {
        super.world = player.getWorld();
        super.linkedPlayer = player;
        super.activeBox = new Box(finalPos, finalPos.add(1, 2, 1));
        super.finalPos = finalPos.add(0, -1, 0);
        super.prevPos = prevPos.add(0, -1, 0);
        super.stepDirection = ((CanStep) player).getStepDirection();
    }

    @Override
    protected boolean placeSupport() {
        return ((CanStep) linkedPlayer).isStepping();
    }

    @Override
    protected boolean shouldRemove() {
        return (lifetime > MIN_LIFETIME &&
                !linkedPlayer.isInsideWall()) ||
                lifetime > MAX_LIFETIME;
    }

    @Override
    protected boolean forceRemove() {
        ((CanStep)linkedPlayer).setStepping(false);
        return true;
    }
}
