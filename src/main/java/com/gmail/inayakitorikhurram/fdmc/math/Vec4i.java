//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
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
public interface Vec4i<E extends Vec4i<E, T>, T extends Vec3i> {
    Codec<Vec4i<?, ?>> CODEC = Codec.INT_STREAM.comapFlatMap((intStream) -> {
        return Util.toArray(intStream, 4).map((is) -> {
            return newVec4i(is[0], is[1], is[2], is[3]);
        });
    }, (Vec4i) -> {
        return IntStream.of(Vec4i.getX4(), Vec4i.getY4(), Vec4i.getZ4(), Vec4i.getW4());
    });

    Vec4i.Vec4iImpl ZERO4 = (Vec4i.Vec4iImpl) new Vec3i(0, 0, 0);

    private static Function<Vec4i<?, ?>, DataResult<Vec4i<?, ?>>> createRangeValidator(int maxAbsValue) {
        return (vec) -> {
            return Math.abs(vec.getX4()) < maxAbsValue && Math.abs(vec.getY4()) < maxAbsValue && Math.abs(vec.getZ4()) < maxAbsValue && Math.abs(vec.getW4()) < maxAbsValue ? DataResult.success(vec) : DataResult.error("Position out of range, expected at most " + maxAbsValue + ": " + vec);
        };
    }

    static Codec<Vec4i<?, ?>> createOffsetCodec(int maxAbsValue) {
        return CODEC.flatXmap(createRangeValidator(maxAbsValue), createRangeValidator(maxAbsValue));
    }

    static Vec4i<?, ?> newVec4i(int x, int y, int z, int w) {
        return ZERO4.newInstance(x, y, z, w);
    }

    static Vec4i<?, ?>  newVec4i(double x, double y, double z, double w) {
        return ZERO4.newInstance(x, y, z, w);
    }

    static Vec4i<?, ?> asVec4i(Vec3i vec) {
        return (Vec4i<?, ?>)(Object) vec;
    }

    E newInstance(int x, int y, int z, int w);

    default E newInstance(double x, double y, double z, double w) {
        return newInstance(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z), MathHelper.floor(w));
    }

    // inherit from Vec3i
    default int getX() {
        return asVec3i().getX();
    }
    // inherit from Vec3i
    default int getY() {
        return asVec3i().getY();
    }
    // inherit from Vec3i
    default int getZ() {
        return asVec3i().getZ();
    }

    int getW();
    int getX4();
    int getY4();
    int getZ4();
    int getW4();

    default Vec3i asVec3i() {
        return (Vec3i)(Object) this;
    }

    E getZeroInstance();

    E self();

    default E add4(double x, double y, double z, double w) {
        return x == 0.0 && y == 0.0 && z == 0.0 && w == 0.0? this.self() : newInstance((double)this.getX4() + x, (double)this.getY4() + y, (double)this.getZ4() + z, (double)this.getW4() + w);
    }

    default E add4(int x, int y, int z, int w) {
        return x == 0 && y == 0 && z == 0 && w == 0 ? this.self() : newInstance(this.getX4() + x, this.getY4() + y, this.getZ4() + z, this.getW4() + w);
    }

    default E add4(Vec4i<?, ?> vec) {
        return this.add4(vec.getX4(), vec.getY4(), vec.getZ4(), vec.getW4());
    }

    default E subtract4(Vec4i<?, ?> vec) {
        return this.add4(-vec.getX4(), -vec.getY4(), -vec.getZ4(), -vec.getW4());
    }

    default E multiply4(int scale) {
        if (scale == 1) {
            return (this.self()); // if this errors then someone is implementing their Vec4i subclass in the wrong way.
        } else {
            return scale == 0 ? getZeroInstance() : newInstance(this.getX4() * scale, this.getY4() * scale, this.getZ4() * scale, this.getW4() * scale);
        }
    }

    default E up4() {
        return this.up4(1);
    }

    default E up4(int distance) {
        return this.offset4(Direction4Constants.UP4, distance);
    }

    default E down4() {
        return this.down4(1);
    }

    default E down4(int distance) {
        return this.offset4(Direction4Constants.DOWN4, distance);
    }

    default E north4() {
        return this.north4(1);
    }

    default E north4(int distance) {
        return this.offset4(Direction4Constants.NORTH4, distance);
    }

    default E south4() {
        return this.south4(1);
    }

    default E south4(int distance) {
        return this.offset4(Direction4Constants.SOUTH4, distance);
    }

    default E west4() {
        return this.west4(1);
    }

    default E west4(int distance) {
        return this.offset4(Direction4Constants.WEST4, distance);
    }

    default E east4() {
        return this.east4(1);
    }

    default E east4(int distance) {
        return this.offset4(Direction4Constants.EAST4, distance);
    }

    default E kata4() {
        return this.kata4(1);
    }

    default E kata4(int distance) {
        return this.offset4(Direction4Constants.KATA4, distance);
    }

    default E ana4() {
        return this.ana4(1);
    }

    default E ana4(int distance) {
        return this.offset4(Direction4Constants.ANA4, distance);
    }

    default E offset4(Direction4 Direction4) {
        return this.offset4(Direction4, 1);
    }

    default E offset4(Direction4 direction4, int distance) {
        return distance == 0 ? (this.self()) : newInstance(
                this.getX4() + direction4.getOffsetX4() * distance,
                this.getY4() + direction4.getOffsetY4() * distance,
                this.getZ4() + direction4.getOffsetZ4() * distance,
                this.getW4() + direction4.getOffsetW4() * distance);
    }

    default E offset4(Direction4.Axis4 axis, int distance) {
        if (distance == 0) {
            return (this.self()); // if this errors then someone is implementing their Vec4i subclass in the wrong way.
        } else {
            int x = 0;
            int y = 0;
            int z = 0;
            int w = 0;
            switch (axis.asEnum()) {
                case X -> {
                    x = distance;
                }
                case Y -> {
                    y = distance;
                }
                case Z -> {
                    z = distance;
                }
                case W -> {
                    w = distance;
                }
            }
            return newInstance(this.getX4() + x, this.getY4() + y, this.getZ4() + z, this.getW4() + w);
        }
    }

    default boolean isWithinDistance4(Vec4i<?, ?> vec4i, double distance) {
        return this.getSquaredDistance4(vec4i) < MathHelper.square(distance);
    }

    default boolean isWithinDistance4(Position4 pos, double distance) {
        return this.getSquaredDistance4(pos) < MathHelper.square(distance);
    }

    default double getSquaredDistance4(Vec4i<?, ?> vec4i) {
        return this.getSquaredDistance4(vec4i.getX4(), vec4i.getY4(), vec4i.getZ4(), vec4i.getW4());
    }

    default double getSquaredDistance4(Position4<Double> pos) {
        return this.getSquaredDistanceFromCenter4(pos.getX(), pos.getY(), pos.getZ(), pos.getW());
    }

    default double getSquaredDistanceFromCenter4(double x, double y, double z, double w) {
        double dx = (double)this.getX4() + 0.5 - x;
        double dy = (double)this.getY4() + 0.5 - y;
        double dz = (double)this.getZ4() + 0.5 - z;
        double dw = (double)this.getW4() + 0.5 - w;
        return dx * dx + dy * dy + dz * dz + dw * dw;
    }

    default double getSquaredDistance4(double x, double y, double z, double w) {
        double dx = (double)this.getX4() - x;
        double dy = (double)this.getY4() - y;
        double dz = (double)this.getZ4() - z;
        double dw = (double)this.getZ4() - w;
        return dx * dx + dy * dy + dz * dz + dw * dw;
    }

    default int getManhattanDistance4(Vec4i<?, ?> vec) {
        float dx = (float)Math.abs(vec.getX4() - this.getX4());
        float dy = (float)Math.abs(vec.getY4() - this.getY4());
        float dz = (float)Math.abs(vec.getZ4() - this.getZ4());
        float dw = (float)Math.abs(vec.getW4() - this.getW4());
        return (int)(dx + dy + dz + dw);
    }

    default int getComponentAlongAxis4(Direction4.Axis4 axis) {
        return axis.choose(this.getX4(), this.getY4(), this.getZ4(), this.getW4());
    }

    // the following ensure that Vec3i methods are exposed
    // inherited from Vec3i
    default Vec3i add(double x, double y, double z) {
        return asVec3i().add(x, y, z);
    }
    // inherited from Vec3i
    default Vec3i add(int x, int y, int z) {
        return asVec3i().add(x, y, z);
    }
    // inherited from Vec3i
    default Vec3i add(Vec3i vec) {
        return asVec3i().add(vec);
    }
    // inherited from Vec3i
    default Vec3i subtract(Vec3i vec) {
        return asVec3i().subtract(vec);
    }
    // inherited from Vec3i
    default Vec3i multiply(int scale) {
        return asVec3i().multiply(scale);
    }
    // inherited from Vec3i
    default Vec3i up() {
        return asVec3i().up();
    }
    // inherited from Vec3i
    default Vec3i up(int distance) {
        return asVec3i().up(distance);
    }
    // inherited from Vec3i
    default Vec3i down() {
        return asVec3i().down();
    }
    // inherited from Vec3i
    default Vec3i down(int distance) {
        return asVec3i().down(distance);
    }
    // inherited from Vec3i
    default Vec3i north() {
        return asVec3i().north();
    }
    // inherited from Vec3i
    default Vec3i north(int distance) {
        return asVec3i().north(distance);
    }
    // inherited from Vec3i
    default Vec3i south() {
        return asVec3i().south();
    }
    // inherited from Vec3i
    default Vec3i south(int distance) {
        return asVec3i().south(distance);
    }
    // inherited from Vec3i
    default Vec3i west() {
        return asVec3i().west();
    }
    // inherited from Vec3i
    default Vec3i west(int distance) {
        return asVec3i().west(distance);
    }
    // inherited from Vec3i
    default Vec3i east() {
        return asVec3i().east();
    }
    // inherited from Vec3i
    default Vec3i east(int distance) {
        return asVec3i().east(distance);
    }
    // inherited from Vec3i
    default Vec3i offset(Direction direction) {
        return asVec3i().offset(direction);
    }
    // inherited from Vec3i
    default Vec3i offset(Direction direction, int distance) {
        return asVec3i().offset(direction, distance);
    }
    // inherited from Vec3i
    default Vec3i offset(Direction.Axis axis, int distance) {
        return asVec3i().offset(axis, distance);
    }
    // inherited from Vec3i
    default Vec3i crossProduct(Vec3i vec) {
        return asVec3i().crossProduct(vec);
    }
    // inherited from Vec3i
    default boolean isWithinDistance(Vec3i vec, double distance) {
        return asVec3i().isWithinDistance(vec, distance);
    }
    // inherited from Vec3i
    default boolean isWithinDistance(Position pos, double distance) {
        return asVec3i().isWithinDistance(pos, distance);
    }
    // inherited from Vec3i
    default double getSquaredDistance(Vec3i vec) {
        return asVec3i().getSquaredDistance(vec);
    }
    // inherited from Vec3i
    default double getSquaredDistance(Position pos) {
        return asVec3i().getSquaredDistance(pos);
    }
    // inherited from Vec3i
    default double getSquaredDistanceFromCenter(double x, double y, double z) {
        return asVec3i().getSquaredDistanceFromCenter(x, y, z);
    }
    // inherited from Vec3i
    default double getSquaredDistance(double x, double y, double z) {
        return asVec3i().getSquaredDistance(x, y, z);
    }
    // inherited from Vec3i
    default int getManhattanDistance(Vec3i vec) {
        return asVec3i().getManhattanDistance(vec);
    }
    // inherited from Vec3i
    default int getComponentAlongAxis(Direction.Axis axis) {
        return asVec3i().getComponentAlongAxis(axis);
    }
    // inherited from Vec3i
    default int compareTo(Vec3i vec3i) {
        return asVec3i().compareTo(vec3i);
    }

    interface Vec4iImpl extends Vec4i<Vec4iImpl, Vec3i> {
        // implemented in mixin
    }

    interface DirectWAccess {
        void directSetW(int w);
        void directAddW(int w);
    }
}
