package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.ArrayUtils;

public interface Direction4 {
    static Direction asDirection(Direction4 direction4) {
        return (Direction)(Object) direction4;
    }

    static Direction4 asDirection4(Direction direction) {
        return (Direction4)(Object) direction;
    }

    interface Axis4 {
        static Direction.Axis asAxis(Axis4 axis4) {
            return (Direction.Axis)(Object) axis4;
        }

        static Axis4 asAxis4(Direction.Axis axis) {
            return (Axis4)(Object) axis;
        }

        default int choose(int x, int y, int z) {
            return 0; // since w is missing it returns 0
        }

        default double choose(double x, double y, double z) {
            return 0; // since w is missing it returns 0
        }
    }
}
