package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.*;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4;
import com.gmail.inayakitorikhurram.fdmc.math.OptionalDirection4;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

import java.util.Map;

public class FDMCProperties {

    public static final EnumProperty<WireConnection> KATA_WIRE_CONNECTION = EnumProperty.of("kata", WireConnection.class);
    public static final EnumProperty<WireConnection> ANA_WIRE_CONNECTION = EnumProperty.of("ana", WireConnection.class);

}
