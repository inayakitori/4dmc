package com.gmail.inayakitorikhurram.fdmc;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;


public enum Direction4 {

    DOWN (0, 1, -1, "down",Direction.DOWN , AxisDirection.NEGATIVE, Axis.Y, new int[]{ 0, -1, 0,  0}),
    UP   (1, 0, -1, "up"  ,Direction.UP   , AxisDirection.POSITIVE, Axis.Y, new int[]{ 0, 1,  0,  0}),
    NORTH(2, 3, 2, "north",Direction.NORTH, AxisDirection.NEGATIVE, Axis.Z, new int[]{ 0, 0, -1,  0}),
    SOUTH(3, 2, 0, "south",Direction.SOUTH, AxisDirection.POSITIVE, Axis.Z, new int[]{ 0, 0,  1,  0}),
    WEST (4, 5, 1, "west" ,Direction.WEST , AxisDirection.NEGATIVE, Axis.X, new int[]{-1, 0,  0,  0}),
    EAST (5, 4, 3, "east" ,Direction.EAST , AxisDirection.POSITIVE, Axis.X, new int[]{ 1, 0,  0,  0}),
    KATA (6, 7, 4, "kata" ,null  , AxisDirection.NEGATIVE, Axis.W, new int[]{ 0, 0,  0, -1}),
    ANA  (7, 6, 5, "ana"  ,null  , AxisDirection.POSITIVE, Axis.W, new int[]{ 0, 0,  0,  1});

    private final int id;
    private final int idOpposite;
    private final int idHorizontal;
    private final String name;
    private final Axis axis;
    private final AxisDirection direction;
    private final Direction direction3;
    private final int[] vec4;
    private final Vec3i vec3;
    public static final Direction4[] ALL;
    public static final Direction4[] VALUES;
    public static final Direction4[] HORIZONTAL;
    public static final Direction4[] WDIRECTIONS;
    //private static final Long2ObjectMap<net.minecraft.util.math.Direction> VECTOR_TO_DIRECTION;

    static {
        ALL = Direction4.values();
        VALUES = (Direction4[])Arrays.stream(ALL).sorted(Comparator.comparingInt(direction -> direction.id)).toArray(Direction4[]::new);
        HORIZONTAL = (Direction4[])Arrays.stream(ALL).filter(direction -> direction.getAxis().isHorizontal()).sorted(Comparator.comparingInt(direction -> direction.idHorizontal)).toArray(Direction4[]::new);
        WDIRECTIONS = (Direction4[])Arrays.stream(ALL).filter(direction -> direction.getAxis().isW()).sorted(Comparator.comparingInt(direction -> direction.idHorizontal)).toArray(Direction4[]::new);
        /**
        VECTOR_TO_DIRECTION = Arrays.stream(ALL).collect(Collectors.toMap(direction -> new BlockPos(direction.getVector()).asLong(), direction -> direction, (direction1, direction2) -> {
            throw new IllegalArgumentException("Duplicate keys");
        }, Long2ObjectOpenHashMap::new));
         **/
    }

    Direction4(int id, int idOpposite, int idHorizontal, String name, Direction direction3, AxisDirection direction, Axis axis, int[] vec4) {
        this.id = id;
        this.idHorizontal = idHorizontal;
        this.idOpposite = idOpposite;
        this.name = name;
        this.direction3 = direction3;
        this.axis = axis;
        this.direction = direction;
        this.vec4 = Arrays.copyOf(vec4, 4);
        this.vec3 = FDMCMath.toPos3(this.vec4);
    }

    public static Stream<Direction4> stream() {
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


    public Direction4 getOpposite() {
        return byId(this.idOpposite);
    }

    public int getOffsetX() {
        return this.vec4[0];
    }

    public int getOffsetY() {
        return this.vec4[1];
    }

    public int getOffsetZ() {
        return this.vec4[2];
    }
    public int getOffsetW() {
        return this.vec4[3];
    }

    public int getOffsetX3(){
        return this.vec3.getX();
    }

    public float[] getUnitVector() {
        return new float[]{this.getOffsetX(), this.getOffsetY(), this.getOffsetZ(), this.getOffsetW()};
    }

    public Vec3f getUnitVector3() {
        return new Vec3f(this.getOffsetX3(), this.getOffsetY(), this.getOffsetZ());
    }

    public String getName() {
        return this.name;
    }

    public Axis getAxis() {
        return this.axis;
    }

    public static Direction4 byId(int id) {
        return VALUES[MathHelper.abs(id % VALUES.length)];
    }

    public static Direction4 fromHorizontal(int value) {
        return HORIZONTAL[MathHelper.abs(value % HORIZONTAL.length)];
    }

    public static Direction4 from(Axis axis, AxisDirection direction) {
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

    public float asRotation() {
        return (this.idHorizontal & 3) * 90;
    }

    public static Direction4 random(Random random) {
        return Util.getRandom(ALL, random);
    }


    public String toString() {
        return this.name;
    }

    public static Direction4 get(AxisDirection direction, Axis axis) {
        for (Direction4 direction2 : ALL) {
            if (direction2.getDirection() != direction || direction2.getAxis() != axis) continue;
            return direction2;
        }
        throw new IllegalArgumentException("No such direction: " + direction + " " + axis);
    }

    public int[] getVec4() {
            return this.vec4;
    }

    public Vec3i getVec3() {
        return this.vec3;
    }

    public enum Axis implements StringIdentifiable,
            Predicate<Direction4>
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

        public static final Axis[] VALUES;
        private final String name;

        Axis(String name) {
            this.name = name;
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

        public static Axis pickRandomAxis(Random random) {
            return Util.getRandom(VALUES, random);
        }

        @Override
        public boolean test(@Nullable Direction4 direction) {
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


        static {
            VALUES = Axis.values();
        }
    }

}


