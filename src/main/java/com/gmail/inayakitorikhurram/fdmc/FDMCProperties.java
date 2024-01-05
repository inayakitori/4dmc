package com.gmail.inayakitorikhurram.fdmc;

import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.state.property.EnumProperty;

public class FDMCProperties {

    public static final EnumProperty<WireConnection> KATA_WIRE_CONNECTION = EnumProperty.of("kata", WireConnection.class);
    public static final EnumProperty<WireConnection> ANA_WIRE_CONNECTION = EnumProperty.of("ana", WireConnection.class);


    public static final EnumProperty<ChestType> CHEST_TYPE_2 = EnumProperty.of("type2", ChestType.class);;

}
