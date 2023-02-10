package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import net.minecraft.util.math.Direction;

public interface Axis4 {
    static Axis4 from(Direction.Axis axis) {
        return (Axis4)(Object) axis;
    }

    int choose(int x, int y, int z, int w);

    double choose(double x, double y, double z, int w);


}
