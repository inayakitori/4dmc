package com.gmail.inayakitorikhurram.fdmc.mixin.math;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Enum;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4i;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import net.minecraft.util.DyeColor;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Predicate;

@Mixin(value = Direction.class, priority = 900)
public abstract class DirectionMixin implements Direction4 {

    @Shadow @Final @Mutable
    private static Direction[] field_11037;
    @Shadow @Final @Mutable
    public static StringIdentifiable.Codec<Direction> CODEC;
    @Shadow @Final @Mutable
    public static com.mojang.serialization.Codec<Direction> VERTICAL_CODEC;
    @Shadow @Final private String name;

    @Shadow public abstract String getName();

    @Shadow public abstract int getId();

    // TODO: ensure this is sorted by id
    private static Direction[] VALUES4 = field_11037;

    private static final Direction KATA = fdmc$addDirection("KATA", 6, 7, 4, "kata", Direction.AxisDirection.NEGATIVE, Direction.Axis.fromName("w"), Vec4i.newVec4i(0, 0, 0, -1).asVec3i());
    private static final Direction ANA = fdmc$addDirection("ANA", 7, 6, 5, "ana", Direction.AxisDirection.POSITIVE, Direction.Axis.fromName("w"), Vec4i.newVec4i(0, 0, 0, 1).asVec3i());
    private static final Direction.Axis W = Direction.Axis.fromName("w");

    static {
        // TODO: check if this happens early enough to not cause any problems
        CODEC = StringIdentifiable.createCodec(() -> VALUES4);
        VERTICAL_CODEC = CODEC.flatXmap(Direction::validateVertical, Direction::validateVertical);
    }

    @Mutable @Final
    private Direction4Enum enumEquivalent;

    // intentionally don't add kata/ ana to Direction.values()
    private static Direction fdmc$addDirection(String internalName, int id, int idOpposite, int idHorizontal, String name, Direction.AxisDirection direction, Direction.Axis axis, Vec3i vector) {
        Direction dir = fdmc$invokeInit(internalName, VALUES4.length, id, idOpposite, idHorizontal, name, direction, axis, vector);
        VALUES4 = ArrayUtils.add(VALUES4, dir);
        return dir;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initEnumEquivalent(String string, int i, int id, int idOpposite, int idHorizontal, String name, Direction.AxisDirection direction, Direction.Axis axis, Vec3i vector, CallbackInfo ci) {
        enumEquivalent = Direction4Enum.byId(this.getId());
    }

    @Invoker("<init>")
    public static Direction fdmc$invokeInit(String internalName, int internalId, int id, int idOpposite, int idHorizontal, String name, Direction.AxisDirection direction, Direction.Axis axis, Vec3i vector) {
        throw new AssertionError();
    }

    @Override
    public Direction4Enum asEnum() {
        return enumEquivalent;
    }

    @Override
    public Vec3d getColor() {
        float[] color;
        switch (this.asEnum()) {
            case DOWN  -> {
                color = DyeColor.BLUE.getColorComponents();
            }
            case UP    -> {
                color = DyeColor.LIME.getColorComponents();
            }
            case NORTH -> {
                color = DyeColor.ORANGE.getColorComponents();
            }
            case SOUTH -> {
                color = DyeColor.LIGHT_BLUE.getColorComponents();
            }
            case WEST  -> {
                color = DyeColor.CYAN.getColorComponents();
            }
            case EAST  -> {
                color = DyeColor.RED.getColorComponents();
            }
            case KATA  -> {
                color = DyeColor.GREEN.getColorComponents();
            }
            case ANA   -> {
                color = DyeColor.PURPLE.getColorComponents();
            }
            default -> {
                color = new float[]{0.f, 0.f, 0.f};
            }
        }
        return new Vec3d(
                color[0],
                color[1],
                color[2]
        );
    }

    @Override
    public Direction[] getParallel() {
        return Arrays.stream(Direction4Constants.VALUES).filter(direction -> direction.getAxis() == getAxis()).toArray(Direction[]::new);
    }

    @Override
    public Direction[] getPerpendicular() {
        return Arrays.stream(Direction4Constants.VALUES).filter(direction -> direction.getAxis() != getAxis()).toArray(Direction[]::new);
    }
    @Override
    public Direction[] getPerpendicularHorizontal() {
        return Arrays.stream(Direction4Constants.VALUES).filter(direction -> direction.getAxis() != getAxis() && direction.getAxis() != Direction.Axis.Y).toArray(Direction[]::new);
    }

    @Override
    public Direction rotateYClockwise() {//rotate YW
        return switch (this.asEnum()) {
            case NORTH  -> Direction4Constants.EAST;
            case EAST   -> Direction4Constants.SOUTH;
            case SOUTH  -> Direction4Constants.WEST;
            case WEST   -> Direction4Constants.NORTH;
            case KATA  ->  Direction4Constants.KATA;
            case ANA   ->  Direction4Constants.ANA;
            default -> throw new IllegalStateException("Unable to get CCW facing of " + this);
        };
    }

    @Override
    public Direction rotateYCounterclockwise() {//rotate YW
        return switch (this.asEnum()) {
            case NORTH  -> Direction4Constants.WEST;
            case EAST   -> Direction4Constants.NORTH;
            case SOUTH  -> Direction4Constants.EAST;
            case WEST   -> Direction4Constants.SOUTH;
            case KATA  ->  Direction4Constants.KATA;
            case ANA   ->  Direction4Constants.ANA;
            default -> throw new IllegalStateException("Unable to get CCW facing of " + this);
        };
    }

    @Inject(method = "byId", at = @At("HEAD"), cancellable = true)
    private static void fdmc$byId(int id, CallbackInfoReturnable<Direction> cir) {
        cir.setReturnValue(VALUES4[MathHelper.abs(id % VALUES4.length)]);
        cir.cancel();
    }

    @Inject(method = "from", at = @At("HEAD"), cancellable = true)
    private static void fdmc$from(Direction.Axis axis, Direction.AxisDirection direction, CallbackInfoReturnable<Direction> cir) {
        switch (axis.getName()) {
            case "x" -> { // X
                if (direction == Direction.AxisDirection.POSITIVE) {
                    cir.setReturnValue(Direction.EAST);
                }else {
                    cir.setReturnValue(Direction.WEST);
                }
            }
            case "y" -> { // Y
                if (direction == Direction.AxisDirection.POSITIVE) {
                    cir.setReturnValue(Direction.UP);
                }else {
                    cir.setReturnValue(Direction.DOWN);
                }
            }
            case "z" -> { // Z
                if (direction == Direction.AxisDirection.POSITIVE) {
                    cir.setReturnValue(Direction.SOUTH);
                }else {
                    cir.setReturnValue(Direction.NORTH);
                }
            }
            case "w" -> { // W
                if (direction == Direction.AxisDirection.POSITIVE) {
                    cir.setReturnValue(ANA);
                }else {
                    cir.setReturnValue(KATA);
                }
            }
            default -> throw new IncompatibleClassChangeError();
        }
        cir.cancel();
    }

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private static void fdmc$get(Direction.AxisDirection direction, Direction.Axis axis, CallbackInfoReturnable<Direction> cir) {
        fdmc$from(axis, direction, cir);
    }

    @Mixin(Direction.Axis.class)
    public static abstract class AxisMixin implements Axis4 {
        @Shadow @Final @Mutable
        private static Direction.Axis[] field_11049;
        private static Direction.Axis[] VALUES4 = field_11049;
        @Shadow @Final @Mutable
        public static StringIdentifiable.Codec<Direction.Axis> CODEC;

        @Shadow public abstract String getName();

        private static final Unsafe UNSAFE;
        static {
            try {
                Field unsafe = Unsafe.class.getDeclaredField("theUnsafe");
                unsafe.setAccessible(true);
                UNSAFE = (Unsafe) unsafe.get(null);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException();
            }
        }
        private static final Direction.Axis W = fdmc$addAxis("w");

        static {
            // TODO: check if this happens early enough to not cause any problems
            CODEC = StringIdentifiable.createCodec(() -> VALUES4);
        }

        @Mutable @Final
        private Direction4Enum.Axis4Enum enumEquivalent;

        @Inject(method = "<init>", at = @At("TAIL"))
        private void initEnumEquivalent(String string, int i, String name, CallbackInfo ci) {
            enumEquivalent = Direction4Enum.Axis4Enum.fromName(this.getName());
        }

        @Override
        public Direction4Enum.Axis4Enum asEnum() {
            return enumEquivalent;
        }

        private static Direction.Axis fdmc$addAxis(String name)  {
            try {
                Direction.Axis axis = (Direction.Axis) UNSAFE.allocateInstance(Direction.Axis.X.getClass());
                axis.name = name;
                VALUES4 = ArrayUtils.add(VALUES4, axis);
                return axis;
            } catch (InstantiationException e) {
                throw new RuntimeException();
            }
        }

        @Inject(method = "isHorizontal", at = @At("RETURN"), cancellable = true)
        private void fdmc$isHorizontalIncludeW(CallbackInfoReturnable<Boolean> cir){
            cir.setReturnValue(
                    cir.getReturnValueZ() || (Direction.Axis) (Object) this == Direction4Constants.Axis4Constants.W
            );
        }

        @Mixin(targets = "net/minecraft/util/math/Direction$Axis$1") @Pseudo
        public static class AxisXMixin {
            @Inject(method = "choose(III)I", at = @At("HEAD"), cancellable = true)
            public void choose(int x, int y, int z, CallbackInfoReturnable<Integer> cir) {
                if ((Object) this == W) {
                    cir.setReturnValue(0);
                    cir.cancel();
                }
            }

            @Inject(method = "choose(DDD)D", at = @At("HEAD"), cancellable = true)
            public void choose(double x, double y, double z, CallbackInfoReturnable<Double> cir) {
                if ((Object) this == W) {
                    cir.setReturnValue(0D);
                    cir.cancel();
                }
            }
        }

    }



    @Mixin(Direction.Type.class)
    public static abstract class TypeMixin implements Iterable<Direction>, Predicate<Direction>, Type4 {
        @Shadow @Final private Direction[] facingArray;
        @Mutable
        @Shadow @Final private static Direction.Type[] field_11063; //VALUES

        private static final Direction.Type HORIZONTAL4 = fdmc$addType(
                "HORIZONTAL4",
                2,
                new Direction[]{//this ordering seems to matter for fluid flow
                        Direction4Constants.KATA,
                        Direction.NORTH,
                        Direction.EAST,
                        Direction.SOUTH,
                        Direction.WEST,
                        Direction4Constants.ANA
                },
                new Direction.Axis[]{
                        Direction.Axis.X,
                        Direction.Axis.Z,
                        Direction4Constants.Axis4Constants.W
                }
        );
        private static Direction.Type fdmc$addType(String internalName, int internalId, Direction[] facingArray, Direction.Axis[] axisArray){
            Direction.Type type = fdmc$invokeInit(internalName, internalId, facingArray, axisArray);
            field_11063 = ArrayUtils.add(field_11063, type); //should be ok to add to since there's like no way anything ever iterates over this
            return type;
        }

        @Invoker("<init>")
        public static Direction.Type fdmc$invokeInit(String internalName, int internalId, Direction[] facingArray, Direction.Axis[] axisArray) {
            throw new AssertionError(); //should be unreachable
        }
    }

}
