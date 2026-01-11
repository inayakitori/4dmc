//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.EnumSet;
import java.util.List;

public class Vec4d extends Vec3d implements Position4d, Pos3Equivalent<Vec3d> {
    public static final Codec<Vec4d> CODEC = Codec.DOUBLE.listOf()
        .comapFlatMap(
            (list) -> Util
                .decodeFixedLengthList(list, 4)
                .map((list4) -> new Vec4d(list4.getFirst(), list4.get(1), list4.get(2), list4.get(3))),
            (vec4d) -> List.of(vec4d.getX4(), vec4d.getY(), vec4d.getZ(), vec4d.getW())
        );
    public static final PacketCodec<ByteBuf, Vec3d> PACKET_CODEC = new PacketCodec<>() {
	    @Override
	    public Vec3d decode(ByteBuf buf) {
		    return new Vec4d(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
	    }

	    @Override
	    public void encode(ByteBuf buf, Vec3d vec) {
            if (vec instanceof Vec4d vec4d) {
                buf.writeDouble(vec4d.x4);
                buf.writeDouble(vec4d.y);
                buf.writeDouble(vec4d.z);
                buf.writeDouble(vec4d.w);
            } else {
                throw new IllegalArgumentException("bluff failed: tried to encode not a Vec4d");
            }
	    }
    };

    public static final Vec4d ZERO = new Vec4d(0d, 0d, 0d, 0d);
    public static final Vec4d X = new Vec4d(1d, 0d, 0d, 0d);
    public static final Vec4d Y = new Vec4d(0d, 1d, 0d, 0d);
    public static final Vec4d Z = new Vec4d(0d, 0d, 1d, 0d);
    public static final Vec4d W = new Vec4d(0d, 0d, 0d, 1d);

    public final double x4;
    public final double w;

    public static Vec4d ofCenter(Vec4i<?, ?> vec) {
        return new Vec4d((double)vec.getX4() + 0.5, (double)vec.getY4() + 0.5, (double)vec.getZ4() + 0.5, (double)vec.getW4() + 0.5);
    }

    public static Vec4d of(Vec4i<?, ?> vec) {
        return new Vec4d(vec.getX4(), vec.getY4(), vec.getZ4(), vec.getW4());
    }

    public static Vec4d of(Vec3d vec3d) {
        return vec3d instanceof Vec4d vec4d
            ? vec4d
            : new Vec4d(vec3d.x, vec3d.y, vec3d.z);
    }

    public static Vec4d ofBottomCenter(Vec4i<?, ?> vec) {
        return new Vec4d((double)vec.getX4() + 0.5, vec.getY4(), (double)vec.getZ4() + 0.5, (double)vec.getW4() + 0.5);
    }

    public static Vec4d ofCenter(Vec4i<?, ?> vec, double deltaY) {
        return new Vec4d((double)vec.getX4() + 0.5, (double)vec.getY4() + deltaY, (double)vec.getZ4() + 0.5, (double)vec.getW4() + 0.5);
    }

    public Vec4d(double x, double y, double z) {
	    super(x, y, z);
	    double[] xw = FDMCMath.splitX3(x);
        this.w = xw[1];
        this.x4 = xw[0];
    }

    public Vec4d(double x4, double y, double z, double w) {
        // remove Math.round when w becomes truly fractional
	    super(x4 + FDMCMath.getOffsetX(Math.round(w)), y, z);
	    this.x4 = x4;
        this.w = w;
    }

    public Vec4d(Vec4d pos4) {
        super(pos4.x, pos4.y, pos4.z);
        this.x4 = pos4.x4;
        this.w = pos4.w;
    }

    @Override
    public Vec3d toPos3() {
        return new Vec3d(x, y, z);
    }

    @Override
    public Vec3d flatten() {
        return new Vec3d(x4, y, z);
    }

    public Vec4d relativize(Vec4d vec) {
        return new Vec4d(vec.x4 - this.x4, vec.y - this.y, vec.z - this.z, vec.w - this.w);
    }
    @Override
    public Vec4d relativize(Vec3d vec) {
        return this.relativize(Vec4d.of(vec));
    }

    @Override
    public Vec4d normalize() {
        double len = this.length();
        return len < MathHelper.EPSILON ? ZERO : new Vec4d(this.x4 / len, this.y / len, this.z / len, this.w / len);
    }

    public double dotProduct(Vec4d vec) {
        return this.x4 * vec.x4 + this.y * vec.y + this.z * vec.z + this.w * vec.w;
    }
    @Override
    public double dotProduct(Vec3d vec) {
        return this.dotProduct(Vec4d.of(vec));
    }

    // No cross product in 4d

    public Vec4d subtract(Vec4d vec) {
        return this.subtract(vec.x4, vec.y, vec.z, vec.w);
    }
    @Override
    public Vec4d subtract(Vec3d vec) {
        return this.subtract(Vec4d.of(vec));
    }

    @Override
    public Vec4d subtract(double value) {
        return this.subtract(value, value, value, value);
    }

    public Vec4d subtract(double x, double y, double z, double w) {
        return this.add(-x, -y, -z, -w);
    }
    @Override
    public Vec4d subtract(double x, double y, double z) {
        if (x == 0)
            return this.subtract(0, y, z, 0);
        return Vec4d.of(super.subtract(x, y, z));
    }

    @Override
    public Vec4d add(double value) {
        return this.add(value, value, value, value);
    }

    public Vec4d add(Vec4d vec) {
        return this.add(vec.x4, vec.y, vec.z, vec.w);
    }
    @Override
    public Vec4d add(Vec3d vec) {
        return this.add(Vec4d.of(vec));
    }

    public Vec4d add(double x, double y, double z, double w) {
        return new Vec4d(this.x4 + x, this.y + y, this.z + z, this.w + w);
    }
    @Override
    public Vec4d add(double x, double y, double z) {
        if (x == 0)
            return this.add(0, y, z, 0);
        return Vec4d.of(super.add(x, y, z));
    }

    public boolean isInRange(Position4d pos, double radius) {
        return this.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ(), pos.getW()) < radius * radius;
    }
    @Override
    public boolean isInRange(Position pos, double radius) {
        return this.isInRange((Position4d) new Vec4d(pos.getX(), pos.getY(), pos.getZ()), radius);
    }

    public double distanceTo(Vec4d vec) {
        return Math.sqrt(this.squaredDistanceTo(vec));
    }
    @Override
    public double distanceTo(Vec3d vec) {
        return this.distanceTo(Vec4d.of(vec));
    }

    public double squaredDistanceTo(Vec4d vec) {
        return this.squaredDistanceTo(vec.x4, vec.y, vec.z, vec.w);
    }
    @Override
    public double squaredDistanceTo(Vec3d vec) {
        return this.squaredDistanceTo(Vec4d.of(vec));
    }

    public double squaredDistanceTo(double x, double y, double z, double w) {
        double dx = x - this.x4;
        double dy = y - this.y;
        double dz = z - this.z;
        double dw = w - this.w;
        return dx*dx + dy*dy + dz*dz + dw*dw;
    }
    @Override
    public double squaredDistanceTo(double x, double y, double z) {
        return this.squaredDistanceTo(new Vec4d(x, y, z));
    }

    public boolean isWithinRangeOf(Vec4d vec, double horizontalRange, double verticalRange) {
        double dx = vec.x4 - this.x4;
        double dy = vec.y - this.y;
        double dz = vec.z - this.z;
        double dw = vec.w - this.z;
        return Math.abs(dy) < verticalRange && (dx*dx + dz*dz + dw*dw) < horizontalRange * horizontalRange;
    }
    @Override
    public boolean isWithinRangeOf(Vec3d vec, double horizontalRange, double verticalRange) {
        return this.isWithinRangeOf(Vec4d.of(vec), horizontalRange, verticalRange);
    }

    @Override
    public Vec4d multiply(double value) {
        return this.multiply(value, value, value, value);
    }

    @Override
    public Vec4d negate() {
        return this.multiply(-1.0);
    }

    public Vec4d multiply(Vec4d vec) {
        return this.multiply(vec.x4, vec.y, vec.z, vec.w);
    }
    @Override
    public Vec4d multiply(Vec3d vec) {
        return this.multiply(Vec4d.of(vec));
    }

    public Vec4d multiply(double x, double y, double z, double w) {
        return new Vec4d(this.x4 * x, this.y * y, this.z * z, this.w * w);
    }
    @Override
    public Vec4d multiply(double x, double y, double z) {
        return this.multiply(new Vec4d(x, y, z));
    }

    @Override
    public Vec4d getHorizontal() {
        return new Vec4d(this.x4, 0, this.z, this.w);
    }

    @Override
    public Vec4d addRandom(Random random, float multiplier) {
        return this.add(
            (random.nextFloat() - 0.5f) * multiplier,
            (random.nextFloat() - 0.5f) * multiplier,
            (random.nextFloat() - 0.5f) * multiplier,
            (random.nextFloat() - 0.5f) * multiplier
        );
    }

    @Override
    public double length() {
        return Math.sqrt(this.x4 * this.x4 + this.y * this.y + this.z * this.z + this.w * this.w);
    }

    @Override
    public double lengthSquared() {
        return this.x4 * this.x4 + this.y * this.y + this.z * this.z + this.w * this.w;
    }

    @Override
    public double horizontalLength() {
        return Math.sqrt(this.x4 * this.x4 + this.z * this.z + this.w * this.w);
    }

    @Override
    public double horizontalLengthSquared() {
        return this.x4 * this.x4 + this.z * this.z + this.w * this.w;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Vec4d vec4d) {
            if (Double.compare(vec4d.x4, this.x4) != 0) {
                return false;
            } else if (Double.compare(vec4d.y, this.y) != 0) {
                return false;
            } else if (Double.compare(vec4d.w, this.w) != 0) {
                return false;
            } else {
                return Double.compare(vec4d.z, this.z) == 0;
            }
        } else if (o instanceof Vec3d){
            return super.equals(o);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        // Hashes of `vec3` and `new Vec4d(vec3)` should match
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "(" + this.x4 + ", " + this.y + ", " + this.z + ", " + this.w + ")";
    }

    public Vec4d lerp(Vec4d to, double delta) {
        return new Vec4d(
            MathHelper.lerp(delta, this.x4, to.x4),
            MathHelper.lerp(delta, this.y, to.y),
            MathHelper.lerp(delta, this.z, to.z),
            MathHelper.lerp(delta, this.w, to.w)
        );
    }
    @Override
    public Vec4d lerp(Vec3d to, double delta) {
        return this.lerp(Vec4d.of(to), delta);
    }

    @Override
    public Vec4d rotateX(float angle) {
        // Just assume keeping W axis the same as well ¯\_(ツ)_/¯
        return this.rotateXW(angle);
    }

    @Override
    public Vec4d rotateY(float angle) {
        // Just assume keeping W axis the same as well ¯\_(ツ)_/¯
        return this.rotateYW(angle);
    }

    @Override
    public Vec4d rotateZ(float angle) {
        // Just assume keeping W axis the same as well ¯\_(ツ)_/¯
        return this.rotateZW(angle);
    }

    /** Rotation AROUND Xw */
    public Vec4d rotateXW(float angle) {
        float f = MathHelper.cos(angle);
        float g = MathHelper.sin(angle);
	    double e = this.y * (double)f + this.z * (double)g;
        double h = this.z * (double)f - this.y * (double)g;
        return new Vec4d(x4, e, h, w);
    }

    /** Rotation AROUND Yw */
    public Vec4d rotateYW(float angle) {
        float f = MathHelper.cos(angle);
        float g = MathHelper.sin(angle);
        double d = this.x4 * (double)f + this.z * (double)g;
	    double h = this.z * (double)f - this.x4 * (double)g;
        return new Vec4d(d, y, h, w);
    }

    /** Rotation AROUND Zw */
    public Vec4d rotateZW(float angle) {
        float f = MathHelper.cos(angle);
        float g = MathHelper.sin(angle);
        double d = this.x4 * (double)f + this.y * (double)g;
        double e = this.y * (double)f - this.x4 * (double)g;
	    return new Vec4d(d, e, z, w);
    }

    @Override
    public Vec4d rotateYClockwise() {
        // Just assume keeping W axis the same as well ¯\_(ツ)_/¯
        return new Vec4d(-z, y, x, w);
    }

    @Override
    public Vec4d floorAlongAxes(EnumSet<Direction.Axis> axes) {
        double d = axes.contains(Direction4Constants.Axis4Constants.X) ? (double)MathHelper.floor(this.x4) : this.x4;
        double e = axes.contains(Direction4Constants.Axis4Constants.Y) ? (double)MathHelper.floor(this.y) : this.y;
        double f = axes.contains(Direction4Constants.Axis4Constants.Z) ? (double)MathHelper.floor(this.z) : this.z;
        double g = axes.contains(Direction4Constants.Axis4Constants.W) ? (double)MathHelper.floor(this.w) : this.w;
        return new Vec4d(d, e, f, g);
    }

    public double getComponentAlongAxis(Direction4.Axis4 axis) {
        return axis.choose(x4, y, z, w);
    }
    @Override
    public double getComponentAlongAxis(Direction.Axis axis) {
        return getComponentAlongAxis(Direction4.Axis4.asAxis4(axis));
    }

    @Override
    public Vec4d withAxis(Direction.Axis axis, double value) {
        double x = axis == Direction4Constants.Axis4Constants.X ? value : this.x4;
        double y = axis == Direction4Constants.Axis4Constants.Y ? value : this.y;
        double z = axis == Direction4Constants.Axis4Constants.Z ? value : this.z;
        double w = axis == Direction4Constants.Axis4Constants.W ? value : this.w;
        return new Vec4d(x, y, z, w);
    }

    public Vec4d offset(Direction4 direction, double value) {
        Vec4i<?, ?> vec4i = direction.getVector4();
        return new Vec4d(
            this.x4 + value * (double)vec4i.getX4(),
            this.y + value * (double)vec4i.getY4(),
            this.z + value * (double)vec4i.getZ4(),
            this.w + value * (double)vec4i.getW4()
        );
    }
    @Override
    public Vec4d offset(Direction direction, double value) {
        return this.offset(Direction4.asDirection4(direction), value);
    }

    public final double getX4() {
        return this.x4;
    }

    public final double getW() {
        return this.w;
    }

    public Vec4d projectOnto(Vec4d vec) {
        if (vec.lengthSquared() == 0.0) {
            return vec;
        }
        return vec.multiply(this.dotProduct(vec)).multiply(1.0 / vec.lengthSquared());
    }
    @Override
    public Vec4d projectOnto(Vec3d vec) {
        return this.projectOnto(Vec4d.of(vec));
    }
}
