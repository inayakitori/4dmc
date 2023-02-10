//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4.Axis4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;
import java.util.stream.IntStream;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
@SuppressWarnings("rawtypes")//all of there are unparameterised but it shuld be fiiiine
public interface Vec4i<E extends Vec3i & Vec4i<E>> {
    Codec<Vec4i> CODEC = Codec.INT_STREAM.comapFlatMap((intStream) -> {
        return Util.toArray(intStream, 4).map((is) -> {
            return newVec4i(is[0], is[1], is[2], is[3]);
        });
    }, (Vec4i) -> {
        return IntStream.of(Vec4i.getX(), Vec4i.getY(), Vec4i.getZ(), Vec4i.getW());
    });

    private static Function<Vec4i, DataResult<Vec4i>> createRangeValidator(int maxAbsValue) {
        return (vec) -> {
            return Math.abs(vec.getX()) < maxAbsValue && Math.abs(vec.getY()) < maxAbsValue && Math.abs(vec.getZ()) < maxAbsValue && Math.abs(vec.getW()) < maxAbsValue ? DataResult.success(vec) : DataResult.error("Position out of range, expected at most " + maxAbsValue + ": " + vec);
        };
    }

    static Codec<Vec4i> createOffsetCodec(int maxAbsValue) {
        return CODEC.flatXmap(createRangeValidator(maxAbsValue), createRangeValidator(maxAbsValue));
    }

    static Vec4iImpl newVec4i(int x, int y, int z, int w) {
        return new Vec4iImpl(x, y, z, w);
    }

    static Vec4iImpl newVec4i(double x, double y, double z, double w) {
        return newVec4i(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z), MathHelper.floor(w));
    }

    static Vec4iImpl from3i(int x, int y, int z) {
        int w = (int)(Math.floor(0.5 + (x + 0d)/FDMCConstants.STEP_DISTANCE));
        x = x - w * FDMCConstants.STEP_DISTANCE;

        return newVec4i(x, y, z, w);
    }

    static Vec4i<?> fromVec3i(Vec3i vec3i) {
        if (vec3i instanceof Vec4i<?>) {
            return (Vec4i<?>) vec3i;
        }
        return from3i(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }

    E newInstance(int x, int y, int z, int w);

    default E newInstance(double x, double y, double z, double w) {
        return newInstance(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z), MathHelper.floor(w));
    }

    default Vec3i toPos3() {
        int x = getX() + FDMCConstants.STEP_DISTANCE * getW();
        int y = getY();
        int z = getZ();
        return new Vec3i(x, y, z);
    }

    // inherit from Vec3i
    int getX();
    // inherit from Vec3i
    int getY();
    // inherit from Vec3i
    int getZ();

    int getW();

    E getZeroInstance();

    E self();

    default E add4(double x, double y, double z, double w) {
        return x == 0.0 && y == 0.0 && z == 0.0 && w == 0.0? (E) this : newInstance((double)this.getX() + x, (double)this.getY() + y, (double)this.getZ() + z, (double)this.getW() + w);
    }

    default E add4(int x, int y, int z, int w) {
        return x == 0 && y == 0 && z == 0 && w == 0 ? (E) this : newInstance(this.getX() + x, this.getY() + y, this.getZ() + z, this.getW() + w);
    }

    default E add4(Vec4i<?> vec) {
        return this.add4(vec.getX(), vec.getY(), vec.getZ(), vec.getW());
    }

    default E subtract4(Vec4i vec) {
        return this.add4(-vec.getX(), -vec.getY(), -vec.getZ(), -vec.getW());
    }

    default E multiply4(int scale) {
        if (scale == 1) {
            return ((E) this); // if this errors then someone is implementing their Vec4i subclass in the wrong way.
        } else {
            return scale == 0 ? getZeroInstance() : newInstance(this.getX() * scale, this.getY() * scale, this.getZ() * scale, this.getW() * scale);
        }
    }

    default E up4() {
        return this.up4(1);
    }

    default E up4(int distance) {
        return this.offset4(Direction4.UP, distance);
    }

    default E down4() {
        return this.down4(1);
    }

    default E down4(int distance) {
        return this.offset4(Direction4.DOWN, distance);
    }

    default E north4() {
        return this.north4(1);
    }

    default E north4(int distance) {
        return this.offset4(Direction4.NORTH, distance);
    }

    default E south4() {
        return this.south4(1);
    }

    default E south4(int distance) {
        return this.offset4(Direction4.SOUTH, distance);
    }

    default E west4() {
        return this.west4(1);
    }

    default E west4(int distance) {
        return this.offset4(Direction4.WEST, distance);
    }

    default E east4() {
        return this.east4(1);
    }

    default E east4(int distance) {
        return this.offset4(Direction4.EAST, distance);
    }

    default E kata4() {
        return this.kata4(1);
    }

    default E kata4(int distance) {
        return this.offset4(Direction4.KATA, distance);
    }

    default E ana4() {
        return this.ana4(1);
    }

    default E ana4(int distance) {
        return this.offset4(Direction4.ANA, distance);
    }

    default E offset4(Direction4 Direction4) {
        return this.offset4((Direction4)Direction4, 1);
    }

    default E offset4(Direction4 Direction4, int distance) {
        return distance == 0 ? ((E) this) : newInstance(
                this.getX() + Direction4.getOffsetX() * distance,
                this.getY() + Direction4.getOffsetY() * distance,
                this.getZ() + Direction4.getOffsetZ() * distance,
                this.getW() + Direction4.getOffsetW() * distance);
    }

    default E offset4(Axis4 axis, int distance) {
        if (distance == 0) {
            return ((E) this); // if this errors then someone is implementing their Vec4i subclass in the wrong way.
        } else {
            int i = axis == Axis4.X ? distance : 0;
            int j = axis == Axis4.Y ? distance : 0;
            int k = axis == Axis4.Z ? distance : 0;
            int l = axis == Axis4.W ? distance : 0;
            return newInstance(this.getX() + i, this.getY() + j, this.getZ() + k, this.getW() + l);
        }
    }

    default boolean isWithinDistance4(Vec4i vec4i, double distance) {
        return this.getSquaredDistance4(vec4i) < MathHelper.square(distance);
    }

    default boolean isWithinDistance4(Position4 pos, double distance) {
        return this.getSquaredDistance4(pos) < MathHelper.square(distance);
    }

    default double getSquaredDistance4(Vec4i vec4i) {
        return this.getSquaredDistance4(vec4i.getX(), vec4i.getY(), vec4i.getZ(), vec4i.getW());
    }

    default double getSquaredDistance4(Position4<Double> pos) {
        return this.getSquaredDistanceFromCenter4(pos.getX(), pos.getY(), pos.getZ(), pos.getW());
    }

    default double getSquaredDistanceFromCenter4(double x, double y, double z, double w) {
        double dx = (double)this.getX() + 0.5 - x;
        double dy = (double)this.getY() + 0.5 - y;
        double dz = (double)this.getZ() + 0.5 - z;
        double dw = (double)this.getW() + 0.5 - w;
        return dx * dx + dy * dy + dz * dz + dw * dw;
    }

    default double getSquaredDistance4(double x, double y, double z, double w) {
        double dx = (double)this.getX() - x;
        double dy = (double)this.getY() - y;
        double dz = (double)this.getZ() - z;
        double dw = (double)this.getZ() - w;
        return dx * dx + dy * dy + dz * dz + dw * dw;
    }

    default int getManhattanDistance4(Vec4i vec) {
        float dx = (float)Math.abs(vec.getX() - this.getX());
        float dy = (float)Math.abs(vec.getY() - this.getY());
        float dz = (float)Math.abs(vec.getZ() - this.getZ());
        float dw = (float)Math.abs(vec.getW() - this.getW());
        return (int)(dx + dy + dz + dw);
    }

    default int getComponentAlongAxis4(Direction4.Axis4 Axis4) {
        return Axis4.choose(this.getX(), this.getY(), this.getZ(), this.getW());
    }

    // the following ensure that Vec3i methods are exposed
    // inherited from Vec3i
    Vec3i add(double x, double y, double z);
    // inherited from Vec3i
    Vec3i add(int x, int y, int z);
    // inherited from Vec3i
    Vec3i add(Vec3i vec);
    // inherited from Vec3i
    Vec3i subtract(Vec3i vec);
    // inherited from Vec3i
    Vec3i multiply(int scale);
    // inherited from Vec3i
    Vec3i up();
    // inherited from Vec3i
    Vec3i up(int distance);
    // inherited from Vec3i
    Vec3i down();
    // inherited from Vec3i
    Vec3i down(int distance);
    // inherited from Vec3i
    Vec3i north();
    // inherited from Vec3i
    Vec3i north(int distance);
    // inherited from Vec3i
    Vec3i south();
    // inherited from Vec3i
    Vec3i south(int distance);
    // inherited from Vec3i
    Vec3i west();
    // inherited from Vec3i
    Vec3i west(int distance);
    // inherited from Vec3i
    Vec3i east();
    // inherited from Vec3i
    Vec3i east(int distance);
    // inherited from Vec3i
    Vec3i offset(Direction direction);
    // inherited from Vec3i
    Vec3i offset(Direction direction, int distance);
    // inherited from Vec3i
    Vec3i offset(Direction.Axis axis, int distance);
    // inherited from Vec3i
    Vec3i crossProduct(Vec3i vec);
    // inherited from Vec3i
    boolean isWithinDistance(Vec3i vec, double distance);
    // inherited from Vec3i
    boolean isWithinDistance(Position pos, double distance);
    // inherited from Vec3i
    double getSquaredDistance(Vec3i vec);
    // inherited from Vec3i
    double getSquaredDistance(Position pos);
    // inherited from Vec3i
    double getSquaredDistanceFromCenter(double x, double y, double z);
    // inherited from Vec3i
    double getSquaredDistance(double x, double y, double z);
    // inherited from Vec3i
    int getManhattanDistance(Vec3i vec);
    // inherited from Vec3i
    int getComponentAlongAxis(Direction.Axis axis);
}
