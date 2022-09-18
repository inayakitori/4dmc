package com.gmail.inayakitorikhurram.fdmc.supportstructure;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

 abstract class SupportStructure {

    public static final int MIN_LIFETIME = 40;
    public static final int MAX_LIFETIME = Integer.MAX_VALUE - 1;
    protected ServerWorld world;
    protected ServerPlayerEntity linkedPlayer;
    protected BlockPos finalPos;
     protected BlockPos prevPos;
    protected Box activeBox;
    private int lifetime = 0;

    protected int tick(){
        return ++lifetime;
    }



    protected abstract boolean placeSupport();
    //can remove if in
    protected boolean shouldRemove(){
        return (lifetime > MIN_LIFETIME &&
                !intersectsPlayer() ) ||
                lifetime > MAX_LIFETIME;
    }


    private boolean intersectsPlayer(){
        if(!linkedPlayer.isInTeleportationState() && !linkedPlayer.isDisconnected()){
            return activeBox.intersects(linkedPlayer.getBoundingBox());
        }
        return true;
    }

    protected abstract boolean forceRemove();



    protected boolean tryRemove() {
        if (shouldRemove()) {
            return forceRemove();
        }
        return false;
    }

    public long asLong(){
        return finalPos.asLong();
    }

}
