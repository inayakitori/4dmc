package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import net.minecraft.block.Block;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Shadow;

public interface BlockColorsI {
    void registerColorProperty(Property<?> property, Block... blocks);
}
