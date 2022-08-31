package com.gmail.inayakitorikhurram.fdmc.supportstructure;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.HashMap;

public class SupportHandler{

    private final HashMap<Long, SupportStructure> supports;

    public SupportHandler(){
        supports = new HashMap<>();
    }

    public boolean tryAddingSupport(Class<? extends SupportStructure> supportClass, ServerPlayerEntity player){
        UnderSupport support = null;
        if(supportClass.equals(UnderSupport.class)){
            support = new UnderSupport(player);
        }


        if(support != null){
            supports.put(support.asLong(), support);
            return support.placeSupport();
        }

        return false;
    }

    public void tickSupports(){
        ArrayList<SupportStructure> supportsToRemove = new ArrayList<>();
        for(SupportStructure support : supports.values()){
            if(support.tryRemove()){
                supportsToRemove.add(support);
            } else{
                support.tick();
            }
        }

        for(SupportStructure support : supportsToRemove){
            supports.remove(support.asLong());
        }
    }

}
