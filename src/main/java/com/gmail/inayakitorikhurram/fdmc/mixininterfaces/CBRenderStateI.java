package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.block.entity.state.ChestBlockEntityRenderState;

import java.util.Optional;

public interface CBRenderStateI {

    static CBRenderStateI of(ChestBlockEntityRenderState chestBlockEntityRenderState){
        return (CBRenderStateI) (Object) chestBlockEntityRenderState;
    }

    ChestType getChestType2();

    void setChestType2(ChestType chestType2);

    int getWDirection();

    void setWDirection(int wDirection);
}
