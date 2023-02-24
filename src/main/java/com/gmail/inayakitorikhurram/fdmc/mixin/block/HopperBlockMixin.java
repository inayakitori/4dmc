package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.block.HopperBlock$1")
public abstract class HopperBlockMixin {
    @Shadow @Final @Mutable
    static int[] field_11136;

    static {
        field_11136 = ArrayUtils.addAll(field_11136, 0, 0);
    }
}
