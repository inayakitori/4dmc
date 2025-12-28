package com.gmail.inayakitorikhurram.fdmc.mixin.entity.state;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.EntityRenderStateAccess;
import net.minecraft.client.render.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements EntityRenderStateAccess {
    @Unique
    private int dw;

    @Unique
    @Override
    public int getDw() {
        return dw;
    }

    @Unique
    @Override
    public void setDw(int dw) {
        this.dw = dw;
    }
}
