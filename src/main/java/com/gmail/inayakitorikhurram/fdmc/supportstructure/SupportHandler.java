package com.gmail.inayakitorikhurram.fdmc.supportstructure;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

public class SupportHandler{

    private final HashMap<Long, SupportStructure> supports;
    private final HashMap<Long, SupportStructure> supportsToAdd;
    private final HashMap<Long, SupportStructure> supportsToRemove;


    public SupportHandler(){
        supports = new HashMap<>();
        supportsToAdd = new HashMap<>();
        supportsToRemove = new HashMap<>();
    }

    public void queueSupport(Class<? extends SupportStructure> supportClass, ServerPlayerEntity player, BlockPos playerPos, BlockPos prevPlayerPos){
        SupportStructure support = null;
        if(supportClass.equals(UnderSupport.class)){
            support = new UnderSupport(player, playerPos, prevPlayerPos);
        } else if(supportClass.equals(SuffocationSupport.class)){
            support = new SuffocationSupport(player, playerPos, prevPlayerPos);
        }

        synchronized (supportsToAdd) {
            if (support != null) {
                supportsToAdd.put(support.asLong(), support);
            }
        }
    }

    //creating and adding a support
    public void addQueuedSupports(){
            for (SupportStructure supportToAdd : supportsToAdd.values()) {
                tryPlacingSupport(supportToAdd);
            }
            supportsToAdd.clear();
    }

    private void tryPlacingSupport(SupportStructure support){
        synchronized (supports) {
            if (support.placeSupport()) {
                supports.put(support.asLong(), support);
            }
        }
    }

    public void tickSupports(){
        synchronized (supportsToAdd) {
            synchronized (supportsToRemove) {
                addQueuedSupports();
                //check if support should be removed or ticked
                for (SupportStructure support : supports.values()) {
                    if (support.tryRemove()) {
                        supportsToRemove.put(support.asLong(), support);
                    } else {
                        support.tick();
                    }
                }

                //remove supports to remove
                for (SupportStructure support : supportsToRemove.values()) {
                    supports.remove(support.asLong(), support);
                }
                supportsToRemove.clear();
            }
        }
    }

}
