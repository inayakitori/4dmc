package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.ArrayUtils;

public interface Direction4Constants {
    Direction[] VALUES = ArrayUtils.addAll(Direction.values(), Direction.byId(6), Direction.byId(7));
    Direction4[] VALUES4 = (Direction4[])(Object[]) VALUES;

    Direction DOWN = Direction.DOWN;
    Direction UP = Direction.UP;
    Direction NORTH = Direction.NORTH;
    Direction SOUTH = Direction.SOUTH;
    Direction WEST = Direction.WEST;
    Direction EAST = Direction.EAST;
    Direction KATA = Direction.byId(6);
    Direction ANA = Direction.byId(7);

    Direction4 DOWN4 = Direction4.asDirection4(DOWN);
    Direction4 UP4 = Direction4.asDirection4(UP);
    Direction4 NORTH4 = Direction4.asDirection4(NORTH);
    Direction4 SOUTH4 = Direction4.asDirection4(SOUTH);
    Direction4 WEST4 = Direction4.asDirection4(WEST);
    Direction4 EAST4 = Direction4.asDirection4(EAST);
    Direction4 KATA4 = Direction4.asDirection4(KATA);
    Direction4 ANA4 = Direction4.asDirection4(ANA);

    interface Axis4 {
        Direction.Axis[] VALUES = ArrayUtils.add(Direction.Axis.values(), Direction.Axis.fromName("w"));
        Direction4.Axis4[] VALUES4 = (Direction4.Axis4[]) VALUES;

        Direction.Axis X = Direction.Axis.X;
        Direction.Axis Y = Direction.Axis.Y;
        Direction.Axis Z = Direction.Axis.Z;
        Direction.Axis W = Direction.Axis.fromName("w");

        Direction4.Axis4 X4 = Direction4.Axis4.asAxis4(X);
        Direction4.Axis4 Y4 = Direction4.Axis4.asAxis4(Y);
        Direction4.Axis4 Z4 = Direction4.Axis4.asAxis4(Z);
        Direction4.Axis4 W4 = Direction4.Axis4.asAxis4(W);
    }
}
