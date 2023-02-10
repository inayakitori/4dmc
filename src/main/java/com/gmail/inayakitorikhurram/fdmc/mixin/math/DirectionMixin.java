package com.gmail.inayakitorikhurram.fdmc.mixin.math;

import com.gmail.inayakitorikhurram.fdmc.math.Vec4i;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

@Mixin(Direction.class)
public abstract class DirectionMixin implements Direction4 {

    @Shadow @Final @Mutable
    private static Direction[] field_11037;
    @Shadow @Final @Mutable
    public static StringIdentifiable.Codec<Direction> CODEC;
    @Shadow @Final @Mutable
    public static Codec VERTICAL_CODEC;
    // TODO: ensure this is sorted by id
    private static Direction[] VALUES4 = field_11037;

    private static final Direction KATA = fdmc$addDirection("KATA", 6, 7, -1, "kata", Direction.AxisDirection.NEGATIVE, Direction.Axis.fromName("w"), Vec4i.newVec4i(0, 0, 0, -1));
    private static final Direction ANA = fdmc$addDirection("ANA", 7, 6, -1, "ana", Direction.AxisDirection.POSITIVE, Direction.Axis.fromName("w"), Vec4i.newVec4i(0, 0, 0, 1));
    private static final Direction.Axis W = Direction.Axis.fromName("w");

    static {
        // TODO: check if this happens early enough to not cause any problems
        CODEC = StringIdentifiable.createCodec(() -> VALUES4);
        VERTICAL_CODEC = CODEC.flatXmap(Direction::validateVertical, Direction::validateVertical);
    }

    // intentionally don't add kata/ ana to Direction.values()
    private static Direction fdmc$addDirection(String internalName, int id, int idOpposite, int idHorizontal, String name, Direction.AxisDirection direction, Direction.Axis axis, Vec3i vector) {
        Direction dir = fdmc$invokeInit(internalName, VALUES4.length, id, idOpposite, idHorizontal, name, direction, axis, vector);
        VALUES4 = ArrayUtils.add(VALUES4, dir);
        return dir;
    }

    @Invoker("<init>")
    public static Direction fdmc$invokeInit(String internalName, int internalId, int id, int idOpposite, int idHorizontal, String name, Direction.AxisDirection direction, Direction.Axis axis, Vec3i vector) {
        throw new AssertionError();
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
                }
                cir.setReturnValue(Direction.WEST);
            }
            case "y" -> { // Y
                if (direction == Direction.AxisDirection.POSITIVE) {
                    cir.setReturnValue(Direction.UP);
                }
                cir.setReturnValue(Direction.DOWN);
            }
            case "z" -> { // Z
                if (direction == Direction.AxisDirection.POSITIVE) {
                    cir.setReturnValue(Direction.SOUTH);
                }
                cir.setReturnValue(Direction.NORTH);
            }
            case "w" -> { // W
                if (direction == Direction.AxisDirection.POSITIVE) {
                    cir.setReturnValue(ANA);
                }
                cir.setReturnValue(KATA);
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
        private static final Unsafe UNSAFE;
        static {
            try {
                Field f =Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                UNSAFE = (Unsafe) f.get(null);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException();
            }
        }
        // TODO: since this is the same class as X we need to inject into Axis$1 and make choose behave differently with W
        private static final Direction.Axis W = fdmc$addAxis("w");

        static {
            // TODO: check if this happens early enough to not cause any problems
            CODEC = StringIdentifiable.createCodec(() -> VALUES4);
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
    }
}
