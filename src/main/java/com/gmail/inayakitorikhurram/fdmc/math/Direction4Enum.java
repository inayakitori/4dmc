package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.Nullable;


public enum Direction4Enum {
    DOWN (0),
    UP   (1),
    NORTH(2),
    SOUTH(3),
    WEST (4),
    EAST (5),
    KATA (6),
    ANA  (7);

    private final int id;

    Direction4Enum(int id) {
        this.id = id;
    }

    public static Direction4Enum byId(int id) {
        return values()[MathHelper.abs(id % values().length)];
    }

    public Direction4 asDirection4() {
        return Direction4.byId(id);
    }

    public Direction asDirection() {
        return asDirection4().asDirection();
    }

    public enum Axis4Enum {
        X("x"),
        Y("y"),
        Z("z"),
        W("w");

        private final String name;

        Axis4Enum(String name) {
            this.name = name;
        }

        @Nullable
        public static Axis4Enum fromName(String name) {
            return switch (name) {
                case "x" -> X;
                case "y" -> Y;
                case "z" -> Z;
                case "w" -> W;
                default -> throw new IllegalArgumentException();
            };
        }

        public Direction4.Axis4 asAxis4() {
            return Direction4.Axis4.asAxis4(asAxis());
        }

        public Direction.Axis asAxis() {
            return Direction.Axis.fromName(name);
        }
    }
}