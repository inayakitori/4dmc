package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import org.spongepowered.asm.mixin.Unique;

public interface EntityRenderStateAccess {
    @Unique
    int getDw();

    @Unique
    void setDw(int dw);
}
