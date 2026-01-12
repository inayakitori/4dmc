package com.gmail.inayakitorikhurram.fdmc.mixin.entity.state;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.EntityRenderStateAccess;
import net.minecraft.client.render.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements EntityRenderStateAccess {
    @Unique
    private double dw;

    @Unique
    @Override
    public double getDw() {
        return dw;
    }

    @Unique
    @Override
    public void setDw(double dw) {
        this.dw = dw;
    }
}
