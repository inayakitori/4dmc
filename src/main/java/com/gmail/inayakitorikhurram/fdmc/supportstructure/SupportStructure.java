package com.gmail.inayakitorikhurram.fdmc.supportstructure;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;


abstract class SupportStructure {

    protected int MIN_LIFETIME = 40;
    protected int MAX_LIFETIME = Integer.MAX_VALUE - 1;
    protected ServerWorld world;
    protected Entity linkedEntity;
    protected boolean hasLinkedPlayer;
    protected ServerPlayerEntity linkedPlayer;
    protected BlockPos finalPos;
    protected BlockPos prevPos;
    protected int stepDirection = 1;
    protected Box activeBox;
    protected int lifetime = 0;
    protected long supportTypeId = 0;

    protected int tick(){
        return ++lifetime;
    }

    protected abstract boolean placeSupport();
    //can remove if in
    protected boolean shouldRemove(){
        if(hasLinkedPlayer){
            return (
                    lifetime > MIN_LIFETIME &&
                            !linkedPlayer.isInTeleportationState() &&
                            !activeBox.intersects(linkedPlayer.getBoundingBox())
            ) ||
                    linkedPlayer.isDisconnected() ||
                    lifetime > MAX_LIFETIME;
        } else{
            return (
                    lifetime > MIN_LIFETIME &&
                            !activeBox.intersects(linkedEntity.getBoundingBox())
            ) ||
                    lifetime > MAX_LIFETIME;
        }
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

     @Override
     public boolean equals(Object obj) {
         if (!(SupportStructure.class.isAssignableFrom(obj.getClass()))) {
             return super.equals(obj);
         } else {
             SupportStructure other = ((SupportStructure) obj);
            return this.asLong() == other.asLong() && this.supportTypeId == other.supportTypeId;
         }
     }
 }
