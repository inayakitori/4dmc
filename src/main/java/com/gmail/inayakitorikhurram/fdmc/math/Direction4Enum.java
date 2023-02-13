package com.gmail.inayakitorikhurram.fdmc.math;

import net.minecraft.util.DyeColor;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;


public enum Direction4Enum implements StringIdentifiable {

    DOWN (0, 1, -1, "down",Direction.DOWN , AxisDirection.NEGATIVE, Axis4Enum.Y, Vec4i.newVec4i( 0, -1, 0,  0), DyeColor.BLUE.getColorComponents()),
    UP   (1, 0, -1, "up"  ,Direction.UP   , AxisDirection.POSITIVE, Axis4Enum.Y, Vec4i.newVec4i( 0, 1,  0,  0), DyeColor.LIME.getColorComponents()),
    NORTH(2, 3, 2, "north",Direction.NORTH, AxisDirection.NEGATIVE, Axis4Enum.Z, Vec4i.newVec4i( 0, 0, -1,  0), DyeColor.ORANGE.getColorComponents()),
    SOUTH(3, 2, 0, "south",Direction.SOUTH, AxisDirection.POSITIVE, Axis4Enum.Z, Vec4i.newVec4i( 0, 0,  1,  0), DyeColor.LIGHT_BLUE.getColorComponents()),
    WEST (4, 5, 1, "west" ,Direction.WEST , AxisDirection.NEGATIVE, Axis4Enum.X, Vec4i.newVec4i(-1, 0,  0,  0), DyeColor.CYAN.getColorComponents()),
    EAST (5, 4, 3, "east" ,Direction.EAST , AxisDirection.POSITIVE, Axis4Enum.X, Vec4i.newVec4i( 1, 0,  0,  0), DyeColor.RED.getColorComponents()),
    KATA (6, 7, 4, "kata" ,null  , AxisDirection.NEGATIVE, Axis4Enum.W, Vec4i.newVec4i( 0, 0,  0, -1), DyeColor.GREEN.getColorComponents()),// 94 124 22
    ANA  (7, 6, 5, "ana"  ,null  , AxisDirection.POSITIVE, Axis4Enum.W, Vec4i.newVec4i( 0, 0,  0,  1), DyeColor.PURPLE.getColorComponents());// 127 50 184

    private final int id;
    private final int idOpposite;
    private final int idHorizontal;
    private final String name;
    private final Axis4Enum axis;
    private final AxisDirection direction;
    private final Direction direction3;
    private final Vec4i vec4;
    private final Vec3i vec3;
    private final Vec3d color;
    public static final Direction4Enum[] ALL;
    public static final Direction4Enum[] VALUES;
    public static final Direction4Enum[] HORIZONTAL;
    public static final Direction4Enum[] WDIRECTIONS;
    //private static final Long2ObjectMap<net.minecraft.util.math.Direction> VECTOR_TO_DIRECTION;

    static {
        ALL = Direction4Enum.values();
        VALUES = (Direction4Enum[])Arrays.stream(ALL).sorted(Comparator.comparingInt(direction -> direction.id)).toArray(Direction4Enum[]::new);
        HORIZONTAL = (Direction4Enum[])Arrays.stream(ALL).filter(direction -> direction.getAxis().isHorizontal()).sorted(Comparator.comparingInt(direction -> direction.idHorizontal)).toArray(Direction4Enum[]::new);
        WDIRECTIONS = (Direction4Enum[])Arrays.stream(ALL).filter(direction -> direction.getAxis().isW()).sorted(Comparator.comparingInt(direction -> direction.idHorizontal)).toArray(Direction4Enum[]::new);
        /**
         VECTOR_TO_DIRECTION = Arrays.stream(ALL).collect(Collectors.toMap(direction -> new BlockPos(direction.getVector()).asLong(), direction -> direction, (direction1, direction2) -> {
         throw new IllegalArgumentException("Duplicate keys");
         }, Long2ObjectOpenHashMap::new));
         **/
    }

    Direction4Enum(int id, int idOpposite, int idHorizontal, String name, Direction direction3, AxisDirection direction, Axis4Enum axis, Vec4i vec4, float[] color) {
        this.id = id;
        this.idHorizontal = idHorizontal;
        this.idOpposite = idOpposite;
        this.name = name;
        this.direction3 = direction3;
        this.axis = axis;
        this.direction = direction;
        this.vec4 = vec4;
        this.vec3 = vec4.toPos3();
        this.color = new Vec3d(color[0], color[1], color[2]);
    }

    public static Stream<Direction4Enum> stream() {
        return Stream.of(ALL);
    }

    public int getId() {
        return this.id;
    }

    public int getHorizontal() {
        return this.idHorizontal;
    }

    public Optional<Direction> getDirection3(){
        return Optional.ofNullable(direction3);
    }

    public AxisDirection getDirection() {
        return this.direction;
    }


    public Direction toDirection(){
        switch (this){

            case DOWN -> {
                return Direction4Constants.DOWN;
            }
            case UP -> {
                return Direction4Constants.UP;
            }
            case NORTH -> {
                return Direction4Constants.NORTH;
            }
            case SOUTH -> {
                return Direction4Constants.SOUTH;
            }
            case WEST -> {
                return Direction4Constants.WEST;
            }
            case EAST -> {
                return Direction4Constants.EAST;
            }
            case KATA -> {
                return Direction4Constants.KATA;
            }
            case ANA -> {
                return Direction4Constants.ANA;
            }
        }
        throw  new IllegalArgumentException("invalid enum variant");
    }

    public Direction4Enum getOpposite() {
        return byId(this.idOpposite);
    }

    public int getOffsetX() {
        return vec4.getX();
    }

    public int getOffsetY() {
        return vec4.getY();
    }

    public int getOffsetZ() {
        return vec4.getZ();
    }
    public int getOffsetW() {
        return vec4.getW();
    }

    public int getOffsetX3(){
        return (int) this.vec3.getX();
    }

    public float[] getUnitVector() {
        return new float[]{this.getOffsetX(), this.getOffsetY(), this.getOffsetZ(), this.getOffsetW()};
    }
    public Vec3d getUnitVector3() {
        return new Vec3d(this.getOffsetX3(), this.getOffsetY(), this.getOffsetZ());
    }

    public Direction4Enum[] getParallel(){
        return new Direction4Enum[]{Direction4Enum.from(this.axis, AxisDirection.NEGATIVE), Direction4Enum.from(this.axis, AxisDirection.POSITIVE)};
    }
    public Direction4Enum[] getPerpendicular(){
        Direction4Enum[] perpendicularDirection = new Direction4Enum[6];
        Axis4Enum axisCycle = this.axis;
        for(int i = 0; i < 3; i++) {
            axisCycle = axisCycle.next(); //will increment through the other 3 axis and add their directions
            perpendicularDirection[2*i] = Direction4Enum.from(axisCycle, AxisDirection.NEGATIVE);
            perpendicularDirection[2*i+1] = Direction4Enum.from(axisCycle, AxisDirection.POSITIVE);
        }
        return perpendicularDirection;
    }
    public Direction4Enum[] getPerpendicularHorizontal(){
        if(this.axis == Axis4Enum.Y) {
            return getPerpendicular();
        }
        Direction4Enum[] perpendicularDirection = new Direction4Enum[4];
        Axis4Enum axisCycle = this.axis;
        for(int i = 0; i < 2; i++) {
            axisCycle = axisCycle.next(); //will increment through the other 3 axis and add their directions
            if(axisCycle == Axis4Enum.Y) axisCycle = axisCycle.next(); //skip y axis
            perpendicularDirection[2*i] = Direction4Enum.from(axisCycle, AxisDirection.NEGATIVE);
            perpendicularDirection[2*i+1] = Direction4Enum.from(axisCycle, AxisDirection.POSITIVE);
        }
        return perpendicularDirection;

    }

    public String getName() {
        return this.name;
    }

    public Axis4Enum getAxis() {
        return this.axis;
    }

    public static Direction4Enum byId(int id) {
        return VALUES[MathHelper.abs(id % VALUES.length)];
    }

    public static Direction4Enum fromHorizontal(int value) {
        return HORIZONTAL[MathHelper.abs(value % HORIZONTAL.length)];
    }

    public static Direction4Enum fromDirection3(Direction dir3){
        return switch (dir3) {

            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    public static Direction4Enum from(Axis4Enum axis, AxisDirection direction) {
        return switch (axis) {
            default -> throw new IncompatibleClassChangeError();
            case X -> {
                if (direction == AxisDirection.POSITIVE) {
                    yield EAST;
                }
                yield WEST;
            }
            case Y -> {
                if (direction == AxisDirection.POSITIVE) {
                    yield UP;
                }
                yield DOWN;
            }
            case Z -> {
                if (direction == AxisDirection.POSITIVE) {
                    yield SOUTH;
                }
                yield NORTH;
            }
            case W -> {
                if (direction == AxisDirection.POSITIVE) {
                    yield ANA;
                }
                yield KATA;
            }
        };
    }


    public static Direction4Enum random(Random random) {
        return Util.getRandom(ALL, random);
    }


    public String toString() {
        return this.name;
    }

    public static Direction4Enum get(AxisDirection direction, Axis4Enum axis) {
        for (Direction4Enum direction2 : ALL) {
            if (direction2.getDirection() != direction || direction2.getAxis() != axis) continue;
            return direction2;
        }
        throw new IllegalArgumentException("No such direction: " + direction + " " + axis);
    }

    public Vec4i getVec4() {
        return this.vec4;
    }

    public Vec3i getVec3() {
        return this.vec3;
    }
    public Vec3d getColor() {
        return this.color;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public boolean equals(Direction other) {
        Optional<Direction> dir3 = this.getDirection3();
        if(dir3.isEmpty()) {
            return false;
        }
        return dir3.get() == other;
    }

    public enum Axis4Enum implements StringIdentifiable,
            Predicate<Direction4Enum>
    {
        X("x"){

            @Override
            public int choose(int x, int y, int z, int w) {
                return x;
            }

            @Override
            public double choose(double x, double y, double z, double w) {
                return x;
            }


        }
        ,
        Y("y"){

            @Override
            public int choose(int x, int y, int z, int w) {
                return y;
            }

            @Override
            public double choose(double x, double y, double z, double w) {
                return y;
            }

        }
        ,
        Z("z"){

            @Override
            public int choose(int x, int y, int z, int w) {
                return z;
            }

            @Override
            public double choose(double x, double y, double z, double w) {
                return z;
            }

        },
        W("w"){

            @Override
            public int choose(int x, int y, int z, int w) {
                return w;
            }

            @Override
            public double choose(double x, double y, double z, double w) {
                return w;
            }

        };

        public static final Axis4Enum[] VALUES;
        public static final StringIdentifiable.Codec<Axis4Enum> CODEC = StringIdentifiable.createCodec(Axis4Enum::values);
        private final String name;

        Axis4Enum(String name) {
            this.name = name;
        }

        @Nullable
        public static Axis4Enum fromName(String name) {
            return CODEC.byId(name);
        }

        public String getName() {
            return this.name;
        }

        public boolean isVertical() {
            return this == Y;
        }

        public boolean isHorizontal() {
            return this == X || this == Z || this == W;
        }

        public boolean isW(){
            return this == W;
        }

        public String toString() {
            return this.name;
        }

        public static Axis4Enum pickRandomAxis(Random random) {
            return Util.getRandom(VALUES, random);
        }

        @Override
        public boolean test(@Nullable Direction4Enum direction) {
            return direction != null && direction.getAxis() == this;
        }

        public Direction.Type getType() {
            return switch (this) {
                default -> throw new IncompatibleClassChangeError();
                case X, Z, W -> Direction.Type.HORIZONTAL;
                case Y -> Direction.Type.VERTICAL;
            };
        }

        @Override
        public String asString() {
            return this.name;
        }

        public abstract int choose(int x, int y, int z, int w);

        public abstract double choose(double x, double y, double z, double w);

        public Axis4Enum next(){
            switch (this) {

                case X -> {
                    return Y;
                }
                case Y -> {
                    return Z;
                }
                case Z -> {
                    return W;
                }
                case W -> {
                    return X;
                }
            }
            throw new IllegalArgumentException("invalid enum state");
        }
        public Axis4Enum previous(){
            switch (this) {

                case X -> {
                    return W;
                }
                case Y -> {
                    return X;
                }
                case Z -> {
                    return Y;
                }
                case W -> {
                    return Z;
                }
            }
            throw new IllegalArgumentException("invalid enum state");
        }
        public static Axis4Enum fromAxis(Direction.Axis axis3){
            return switch (axis3) {
                case X -> X;
                case Y -> Y;
                case Z -> Z;
            };
        }

        static {
            VALUES = Axis4Enum.values();
        }
    }

    public enum AxisDirection {
        POSITIVE(1, "Towards positive"),
        NEGATIVE(-1, "Towards negative");

        private final int offset;
        private final String description;

        AxisDirection(int offset, String description) {
            this.offset = offset;
            this.description = description;
        }

        public int offset() {
            return this.offset;
        }

        public String getDescription() {
            return this.description;
        }

        public String toString() {
            return this.description;
        }

        public Direction4Enum.AxisDirection getOpposite() {
            return this == POSITIVE ? NEGATIVE : POSITIVE;
        }
    }
}