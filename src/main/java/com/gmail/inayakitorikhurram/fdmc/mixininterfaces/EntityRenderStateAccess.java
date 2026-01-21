package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import org.spongepowered.asm.mixin.Unique;

public interface EntityRenderStateAccess {
    @Unique
    double getDw();

    @Unique
    void setDw(double dw);
}
