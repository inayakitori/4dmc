package com.gmail.inayakitorikhurram.fdmc.mixin.client.render.block.entity.state;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CBRenderStateI;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.block.entity.state.ChestBlockEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChestBlockEntityRenderState.class)
public class ChestBlockEntityRenderStateMixin implements CBRenderStateI {

    @Unique
    private ChestType chestType2 = ChestType.SINGLE;
    @Unique
    private int wDirection = 0;

    @Override
    public ChestType getChestType2() {
        return chestType2;
    }

    @Override
    public void setChestType2(ChestType chestType2) {
        this.chestType2 = chestType2;
    }

    @Override
    public int getWDirection() {
        return wDirection;
    }

    @Override
    public void setWDirection(int wDirection) {
        this.wDirection = wDirection;
    }
}
