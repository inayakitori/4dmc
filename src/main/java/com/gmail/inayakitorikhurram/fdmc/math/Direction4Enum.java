package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;


public enum Direction4Enum implements StringIdentifiable {
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
        return Direction4.byIndex(id);
    }

    public Direction asDirection() {
        return asDirection4().asDirection();
    }

    @Override
    public String asString() {
        return this.name();
    }

    public enum Axis4Enum {
        X("X"),
        Y("Y"),
        Z("Z"),
        W("W");

        private final String name;

        Axis4Enum(String name) {
            this.name = name;
        }

        public static @NotNull Axis4Enum fromId(String name) {
            return switch (name) {
                case "X" -> X;
                case "Y" -> Y;
                case "Z" -> Z;
                case "W" -> W;
                default -> throw new IllegalArgumentException();
            };
        }

        public Axis4Enum next(){
            return switch (this){
                case X -> Y;
                case Y -> Z;
                case Z -> W;
                case W -> X;
            };
        }

        public Axis4Enum nextHorizontal(){
            return switch (this){
                case X -> Z;
                case Y -> Y;
                case Z -> W;
                case W -> X;
            };
        }

        public Direction4.Axis4 asAxis4() {
            return Direction4.Axis4.asAxis4(asAxis());
        }

        public Direction.Axis asAxis() {
            return Direction.Axis.fromId(name);
        }
    }
}