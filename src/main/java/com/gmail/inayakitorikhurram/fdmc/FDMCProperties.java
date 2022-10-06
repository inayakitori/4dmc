package com.gmail.inayakitorikhurram.fdmc;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;

import java.util.Map;

public class FDMCProperties {

    public static final EnumProperty<WireConnection> KATA_WIRE_CONNECTION = EnumProperty.of("kata", WireConnection.class);
    public static final EnumProperty<WireConnection> ANA_WIRE_CONNECTION = EnumProperty.of("ana", WireConnection.class);

    public static final Map<Direction4, EnumProperty<WireConnection>> WIRE_CONNECTION_MAP = Maps.newEnumMap(ImmutableMap.of(
            Direction4.NORTH, Properties.NORTH_WIRE_CONNECTION,
            Direction4.EAST , Properties.EAST_WIRE_CONNECTION,
            Direction4.SOUTH, Properties.SOUTH_WIRE_CONNECTION,
            Direction4.WEST , Properties.WEST_WIRE_CONNECTION,
            Direction4.KATA , KATA_WIRE_CONNECTION,
            Direction4.ANA  , ANA_WIRE_CONNECTION));

}
