package com.gmail.inayakitorikhurram.fdmc.math;

import net.minecraft.block.enums.ChestType;


//first axis is the left/right axis, second is the kata/ana axis
public record DoubleChestType(ChestType axis1, ChestType axis2) {
    public static final DoubleChestType SINGLE = new DoubleChestType(ChestType.SINGLE, ChestType.SINGLE);
}

