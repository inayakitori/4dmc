//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
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

/**
 * A RelativeVec4d simply stores the x,y,z, and w values but does not do operations to convert
 * between slices:
 *  new RelativeVec4d(1.2, 0, 0, -0.3) will have x = 1.2, w = -0.3
 *  contrast this with the more compelx class Vec4d:
 *  new Vec4d(1.2, 0, 0, -0.3) will have x = 1.2 - 2^18, w = -0.3
 *  because the negative x3 value will be floored
 */
public class RelativeVec4d extends Vec3d implements Position4d, Pos3Equivalent<Vec3d> {
    // this could be optimised to be 4 doubles and a bool for
    public static final Codec<RelativeVec4d> CODEC = Codec.DOUBLE.listOf()
        .comapFlatMap(
            (list) -> list.size() == 4 ? Util
                .decodeFixedLengthList(list, 4)
                .map((list4) -> new RelativeVec4d(list4.getFirst(), list4.get(1), list4.get(2), list4.get(3)))
                : Util
                    .decodeFixedLengthList(list, 3)
                    .map((list4) -> RelativeVec4d.converted(list4.getFirst(), list4.get(1), list4.get(2))),
            (vec4d) -> List.of(vec4d.x, vec4d.y, vec4d.z, vec4d.w)
        );
    public static final PacketCodec<ByteBuf, Vec3d> PACKET_CODEC = new PacketCodec<>() {
	    @Override
	    public Vec3d decode(ByteBuf buf) {
		    return new RelativeVec4d(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
	    }

	    @Override
	    public void encode(ByteBuf buf, Vec3d vec) {
            if (vec instanceof RelativeVec4d vec4d) {
                buf.writeDouble(vec4d.x);
                buf.writeDouble(vec4d.y);
                buf.writeDouble(vec4d.z);
                buf.writeDouble(vec4d.w);
            } else {
                throw new IllegalArgumentException("bluff failed: tried to encode not a RelativeVec4d");
            }
	    }
    };

    public static final RelativeVec4d ZERO = new RelativeVec4d(0d, 0d, 0d, 0d);
    public static final RelativeVec4d X = new RelativeVec4d(1d, 0d, 0d, 0d);
    public static final RelativeVec4d Y = new RelativeVec4d(0d, 1d, 0d, 0d);
    public static final RelativeVec4d Z = new RelativeVec4d(0d, 0d, 1d, 0d);
    public static final RelativeVec4d W = new RelativeVec4d(0d, 0d, 0d, 1d);

    public final double w;

    public RelativeVec4d(double x, double y, double z, double w) {
	    super(x, y, z);
        if(Math.abs(x) > FDMCConstants.STEP_DISTANCE/2 && !(this instanceof Vec4d)){
            throw new IllegalArgumentException("can't have a relative x value that large");
        }
        this.w = w;
    }

    public RelativeVec4d(RelativeVec4d pos4) {
        super(pos4.x, pos4.y, pos4.z);
        if(Math.abs(x) > FDMCConstants.STEP_DISTANCE/2 && !(this instanceof Vec4d)){
            throw new IllegalArgumentException("can't have a relative x value that large");
        }
        this.w = pos4.w;
    }

    public static RelativeVec4d of(Vec3d vec3d) {

        if (!(vec3d instanceof RelativeVec4d relativeVec4d)) {
            double[] xw = FDMCMath.splitX3(vec3d.x);
            return new RelativeVec4d(xw[0], vec3d.y, vec3d.z, xw[1]);
        }
        if (vec3d instanceof Vec4d vec4d)
            return new RelativeVec4d(vec4d.x4, vec4d.y, vec4d.z, vec4d.w);
        return relativeVec4d;
    }

    public static RelativeVec4d converted(double x, double y, double z) {

            double[] xw = FDMCMath.splitX3(x);
            return new RelativeVec4d(xw[0], y, z, xw[1]);
    }

    @Override
    public Vec3d toPos3() {
        return new Vec3d(x, y, z);
    }

    @Override
    public Vec3d flatten() {
        return new Vec3d(x, y, z);
    }

    public RelativeVec4d relativize(RelativeVec4d vec) {
        return new RelativeVec4d(vec.x - this.x, vec.y - this.y, vec.z - this.z, vec.w - this.w);
    }
    @Override
    public RelativeVec4d relativize(Vec3d vec) {
        return this.relativize(RelativeVec4d.of(vec));
    }

    @Override
    public RelativeVec4d normalize() {
        double len = this.length();
        return len < MathHelper.EPSILON ? ZERO : new RelativeVec4d(this.x / len, this.y / len, this.z / len, this.w / len);
    }

    public double dotProduct(RelativeVec4d vec) {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z + this.w * vec.w;
    }
    @Override
    public double dotProduct(Vec3d vec) {
        return this.dotProduct(RelativeVec4d.of(vec));
    }

    // No cross product in 4d

    public RelativeVec4d subtract(RelativeVec4d vec) {
        return this.subtract(vec.x, vec.y, vec.z, vec.w);
    }
    @Override
    public RelativeVec4d subtract(Vec3d vec) {
        return this.subtract(RelativeVec4d.of(vec));
    }

    @Override
    public RelativeVec4d subtract(double value) {
        return this.subtract(value, value, value, value);
    }

    public RelativeVec4d subtract(double x, double y, double z, double w) {
        return this.add(-x, -y, -z, -w);
    }
    @Override
    public RelativeVec4d subtract(double x, double y, double z) {
        return RelativeVec4d.of(super.subtract(x, y, z));
    }

    @Override
    public RelativeVec4d add(double value) {
        return this.add(value, value, value, value);
    }

    public RelativeVec4d add(RelativeVec4d vec) {
        return this.add(vec.x, vec.y, vec.z, vec.w);
    }
    @Override
    public RelativeVec4d add(Vec3d vec) {
        return this.add(RelativeVec4d.of(vec));
    }

    public RelativeVec4d add(double x, double y, double z, double w) {
        return new RelativeVec4d(this.x + x, this.y + y, this.z + z, this.w + w);
    }
    @Override
    public RelativeVec4d add(double x, double y, double z) {
        return RelativeVec4d.of(super.add(x, y, z));
    }

    public boolean isInRange(Position4d pos, double radius) {
        return this.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ(), pos.getW()) < radius * radius;
    }
    @Override
    public boolean isInRange(Position pos, double radius) {
        return this.isInRange((Position4d) RelativeVec4d.converted(pos.getX(), pos.getY(), pos.getZ()), radius);
    }

    public double distanceTo(RelativeVec4d vec) {
        return Math.sqrt(this.squaredDistanceTo(vec));
    }
    @Override
    public double distanceTo(Vec3d vec) {
        return this.distanceTo(RelativeVec4d.of(vec));
    }

    public double squaredDistanceTo(RelativeVec4d vec) {
        return this.squaredDistanceTo(vec.x, vec.y, vec.z, vec.w);
    }
    @Override
    public double squaredDistanceTo(Vec3d vec) {
        return this.squaredDistanceTo(RelativeVec4d.of(vec));
    }

    public double squaredDistanceTo(double x, double y, double z, double w) {
        double dx = x - this.x;
        double dy = y - this.y;
        double dz = z - this.z;
        double dw = w - this.w;
        return dx*dx + dy*dy + dz*dz + dw*dw;
    }
    @Override
    public double squaredDistanceTo(double x, double y, double z) {
        return this.squaredDistanceTo(RelativeVec4d.converted(x, y, z));
    }

    public boolean isWithinRangeOf(RelativeVec4d vec, double horizontalRange, double verticalRange) {
        double dx = vec.x - this.x;
        double dy = vec.y - this.y;
        double dz = vec.z - this.z;
        double dw = vec.w - this.z;
        return Math.abs(dy) < verticalRange && (dx*dx + dz*dz + dw*dw) < horizontalRange * horizontalRange;
    }
    @Override
    public boolean isWithinRangeOf(Vec3d vec, double horizontalRange, double verticalRange) {
        return this.isWithinRangeOf(RelativeVec4d.of(vec), horizontalRange, verticalRange);
    }

    @Override
    public RelativeVec4d multiply(double value) {
        return this.multiply(value, value, value, value);
    }

    @Override
    public RelativeVec4d negate() {
        return this.multiply(-1.0);
    }

    public RelativeVec4d multiply(RelativeVec4d vec) {
        return this.multiply(vec.x, vec.y, vec.z, vec.w);
    }
    @Override
    public RelativeVec4d multiply(Vec3d vec) {
        return this.multiply(RelativeVec4d.of(vec));
    }

    public RelativeVec4d multiply(double x, double y, double z, double w) {
        return new RelativeVec4d(this.x * x, this.y * y, this.z * z, this.w * w);
    }
    @Override
    public RelativeVec4d multiply(double x, double y, double z) {
        return this.multiply(RelativeVec4d.converted(x, y, z));
    }

    @Override
    public RelativeVec4d getHorizontal() {
        return new RelativeVec4d(this.x, 0, this.z, this.w);
    }

    @Override
    public RelativeVec4d addRandom(Random random, float multiplier) {
        return this.add(
            (random.nextFloat() - 0.5f) * multiplier,
            (random.nextFloat() - 0.5f) * multiplier,
            (random.nextFloat() - 0.5f) * multiplier,
            (random.nextFloat() - 0.5f) * multiplier
        );
    }

    @Override
    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
    }

    @Override
    public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
    }

    @Override
    public double horizontalLength() {
        return Math.sqrt(this.x * this.x + this.z * this.z + this.w * this.w);
    }

    @Override
    public double horizontalLengthSquared() {
        return this.x * this.x + this.z * this.z + this.w * this.w;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof RelativeVec4d vec4d) {
            if (Double.compare(vec4d.x, this.x) != 0) {
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
        return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
    }

    public RelativeVec4d lerp(RelativeVec4d to, double delta) {
        return new RelativeVec4d(
            MathHelper.lerp(delta, this.x, to.x),
            MathHelper.lerp(delta, this.y, to.y),
            MathHelper.lerp(delta, this.z, to.z),
            MathHelper.lerp(delta, this.w, to.w)
        );
    }
    @Override
    public RelativeVec4d lerp(Vec3d to, double delta) {
        return this.lerp(RelativeVec4d.of(to), delta);
    }

    @Override
    public RelativeVec4d rotateX(float angle) {
        // Just assume keeping W axis the same as well ¯\_(ツ)_/¯
        return this.rotateXW(angle);
    }

    @Override
    public RelativeVec4d rotateY(float angle) {
        // Just assume keeping W axis the same as well ¯\_(ツ)_/¯
        return this.rotateYW(angle);
    }

    @Override
    public RelativeVec4d rotateZ(float angle) {
        // Just assume keeping W axis the same as well ¯\_(ツ)_/¯
        return this.rotateZW(angle);
    }

    /** Rotation AROUND Xw */
    public RelativeVec4d rotateXW(float angle) {
        float f = MathHelper.cos(angle);
        float g = MathHelper.sin(angle);
	    double e = this.y * (double)f + this.z * (double)g;
        double h = this.z * (double)f - this.y * (double)g;
        return new RelativeVec4d(x, e, h, w);
    }

    /** Rotation AROUND Yw */
    public RelativeVec4d rotateYW(float angle) {
        float f = MathHelper.cos(angle);
        float g = MathHelper.sin(angle);
        double d = this.x * (double)f + this.z * (double)g;
	    double h = this.z * (double)f - this.x * (double)g;
        return new RelativeVec4d(d, y, h, w);
    }

    /** Rotation AROUND Zw */
    public RelativeVec4d rotateZW(float angle) {
        float f = MathHelper.cos(angle);
        float g = MathHelper.sin(angle);
        double d = this.x * (double)f + this.y * (double)g;
        double e = this.y * (double)f - this.x * (double)g;
	    return new RelativeVec4d(d, e, z, w);
    }

    @Override
    public RelativeVec4d rotateYClockwise() {
        // Just assume keeping W axis the same as well ¯\_(ツ)_/¯
        return new RelativeVec4d(-z, y, x, w);
    }

    @Override
    public RelativeVec4d floorAlongAxes(EnumSet<Direction.Axis> axes) {
        double d = axes.contains(Direction4Constants.Axis4Constants.X) ? (double)MathHelper.floor(this.x) : this.x;
        double e = axes.contains(Direction4Constants.Axis4Constants.Y) ? (double)MathHelper.floor(this.y) : this.y;
        double f = axes.contains(Direction4Constants.Axis4Constants.Z) ? (double)MathHelper.floor(this.z) : this.z;
        double g = axes.contains(Direction4Constants.Axis4Constants.W) ? (double)MathHelper.floor(this.w) : this.w;
        return new RelativeVec4d(d, e, f, g);
    }

    public double getComponentAlongAxis(Direction4.Axis4 axis) {
        return axis.choose(x, y, z, w);
    }
    @Override
    public double getComponentAlongAxis(Direction.Axis axis) {
        return getComponentAlongAxis(Direction4.Axis4.asAxis4(axis));
    }

    @Override
    public RelativeVec4d withAxis(Direction.Axis axis, double value) {
        double x = axis == Direction4Constants.Axis4Constants.X ? value : this.x;
        double y = axis == Direction4Constants.Axis4Constants.Y ? value : this.y;
        double z = axis == Direction4Constants.Axis4Constants.Z ? value : this.z;
        double w = axis == Direction4Constants.Axis4Constants.W ? value : this.w;
        return new RelativeVec4d(x, y, z, w);
    }

    public RelativeVec4d offset(Direction4 direction, double value) {
        Vec4i<?, ?> vec4i = direction.getVector4();
        return new RelativeVec4d(
            this.x + value * (double)vec4i.getX(),
            this.y + value * (double)vec4i.getY4(),
            this.z + value * (double)vec4i.getZ4(),
            this.w + value * (double)vec4i.getW4()
        );
    }
    @Override
    public RelativeVec4d offset(Direction direction, double value) {
        return this.offset(Direction4.asDirection4(direction), value);
    }

    public final double getW() {
        return this.w;
    }

    public RelativeVec4d projectOnto(RelativeVec4d vec) {
        if (vec.lengthSquared() == 0.0) {
            return vec;
        }
        return vec.multiply(this.dotProduct(vec)).multiply(1.0 / vec.lengthSquared());
    }
    @Override
    public RelativeVec4d projectOnto(Vec3d vec) {
        return this.projectOnto(RelativeVec4d.of(vec));
    }
}
