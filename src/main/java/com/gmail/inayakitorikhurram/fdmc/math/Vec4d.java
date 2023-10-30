//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import com.mojang.serialization.Codec;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Enum.Axis4Enum;
import net.minecraft.util.math.Vec3d;

public class Vec4d implements Position4<Double>, Pos3Equivalent<Vec3d> {
    public static final Codec<Vec4d> CODEC;
    public static final Vec4d ZERO;
    public final double x;
    public final double y;
    public final double z;
    public final double w;

    public static Vec4d ofCenter(Vec4i<?, ?> vec) {
        return new Vec4d((double)vec.getX4() + 0.5, (double)vec.getY4() + 0.5, (double)vec.getZ4() + 0.5, (double)vec.getW4() + 0.5);
    }

    public static Vec4d of(Vec4i<?, ?> vec) {
        return new Vec4d((double)vec.getX4(), (double)vec.getY4(), (double)vec.getZ4(), (double)vec.getW4());
    }

    public static Vec4d ofBottomCenter(Vec4i<?, ?> vec) {
        return new Vec4d((double)vec.getX4() + 0.5, (double)vec.getY4(), (double)vec.getZ4() + 0.5, (double)vec.getW4() + 0.5);
    }

    public static Vec4d ofCenter(Vec4i<?, ?> vec, double deltaY) {
        return new Vec4d((double)vec.getX4() + 0.5, (double)vec.getY4() + deltaY, (double)vec.getZ4() + 0.5, (double)vec.getW4() + 0.5);
    }

    public Vec4d(Vec3d pos3) {
        this(pos3.getX(), pos3.getY(), pos3.getZ());
    }

    public Vec4d(double x, double y, double z) {
        this.w = Math.floor(0.5 + (x/FDMCConstants.STEP_DISTANCE) );
        this.x = x - this.w * FDMCConstants.STEP_DISTANCE;
        this.y = y;
        this.z = z;
    }

    public Vec4d(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Override
    public Vec3d toPos3() {
        double x = this.x + FDMCConstants.STEP_DISTANCE * this.y;
        double y = this.y;
        double z = this.z;
        return new Vec3d(x, y, z);
    }

    public Vec4d relativize(Vec4d vec) {
        return new Vec4d(vec.x - this.x, vec.y - this.y, vec.z - this.z, vec.w - this.w);
    }

    public Vec4d normalize() {
        double d = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
        return d < 1.0E-4 ? ZERO : new Vec4d(this.x / d, this.y / d, this.z / d, this.w / d);
    }

    public double dotProduct(Vec4d vec) {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z + this.w * vec.w;
    }

    public Vec4d subtract(Vec4d vec) {
        return this.subtract(vec.x, vec.y, vec.z);
    }

    public Vec4d subtract(double x, double y, double z) {
        return this.add(-x, -y, -z);
    }

    public Vec4d add(Vec4d vec) {
        return this.add(vec.x, vec.y, vec.z);
    }

    public Vec4d add(double x, double y, double z) {
        return new Vec4d(this.x + x, this.y + y, this.z + z, this.w + w);
    }

    public boolean isInRange(Position4<Double> pos, double radius) {
        return this.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ(), pos.getW()) < radius * radius;
    }

    public double distanceTo(Vec4d vec) {
        double dx = vec.x - this.x;
        double dy = vec.y - this.y;
        double dz = vec.z - this.z;
        double dw = vec.w - this.w;
        return Math.sqrt(dx*dx + dy*dy + dz*dz + dw*dw);
    }

    public double squaredDistanceTo(Vec4d vec) {
        double dx = vec.x - this.x;
        double dy = vec.y - this.y;
        double dz = vec.z - this.z;
        double dw = vec.w - this.w;
        return dx*dx + dy*dy + dz*dz + dw*dw;
    }

    public double squaredDistanceTo(double x, double y, double z, double w) {
        double dx = x - this.x;
        double dy = y - this.y;
        double dz = z - this.z;
        double dw = w - this.w;
        return dx*dx + dy*dy + dz*dz + dw*dw;
    }

    public Vec4d multiply(double value) {
        return this.multiply(value, value, value, value);
    }

    public Vec4d negate() {
        return this.multiply(-1.0);
    }

    public Vec4d multiply(Vec4d vec) {
        return this.multiply(vec.x, vec.y, vec.z, vec.w);
    }

    public Vec4d multiply(double x, double y, double z, double w) {
        return new Vec4d(this.x * x, this.y * y, this.z * z, this.w * w);
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
    }

    public double horizontalLength() {
        return Math.sqrt(this.x * this.x + this.z * this.z + this.w * this.w);
    }

    public double horizontalLengthSquared() {
        return this.x * this.x + this.z * this.z + this.w * this.w;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Vec4d)) {
            return false;
        } else {
            Vec4d vec4d = (Vec4d)o;
            if (Double.compare(vec4d.x, this.x) != 0) {
                return false;
            } else if (Double.compare(vec4d.y, this.y) != 0) {
                return false;
            } else if (Double.compare(vec4d.w, this.w) != 0) {
                return false;
            } else {
                return Double.compare(vec4d.z, this.z) == 0;
            }
        }
    }

    public int hashCode() {
        long l = Double.doubleToLongBits(this.x);
        int i = (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.y);
        i = 31 * i + (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.z);
        i = 31 * i + (int)(l ^ l >>> 32);
        l = Double.doubleToLongBits(this.w);
        i = 31 * i + (int)(l ^ l >>> 32);
        return i;
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public Vec4d lerp(Vec4d to, double delta) {
        return new Vec4d(MathHelper.lerp(delta, this.x, to.x), MathHelper.lerp(delta, this.y, to.y), MathHelper.lerp(delta, this.z, to.z), MathHelper.lerp(delta, this.w, to.w));
    }

    public Vec4d rotateZY(float angle) {
        float f = MathHelper.cos(angle);
        float g = MathHelper.sin(angle);
        double x = this.x;
        double y = this.y * (double)f + this.z * (double)g;
        double z = this.z * (double)f - this.y * (double)g;
        double w = this.w;
        return new Vec4d(x, y, z, w);
    }

    public Vec4d rotateXZ(float angle) {
        float f = MathHelper.cos(angle);
        float g = MathHelper.sin(angle);
        double x = this.x * (double)f + this.z * (double)g;
        double y = this.y;
        double z = this.z * (double)f - this.x * (double)g;
        double w = this.w;
        return new Vec4d(x, y, z, w);
    }

    public Vec4d rotateXY(float angle) {
        float f = MathHelper.cos(angle);
        float g = MathHelper.sin(angle);
        double d = this.x * (double)f + this.y * (double)g;
        double e = this.y * (double)f - this.x * (double)g;
        double h = this.z;
        double w = this.w;
        return new Vec4d(x, y, z, w);
    }

    public Vec4d floorAlongAxes(EnumSet<Axis4Enum> axes) {
        double x = axes.contains(Axis4Enum.X) ? (double)MathHelper.floor(this.x) : this.x;
        double y = axes.contains(Axis4Enum.Y) ? (double)MathHelper.floor(this.y) : this.y;
        double z = axes.contains(Axis4Enum.Z) ? (double)MathHelper.floor(this.z) : this.z;
        double w = axes.contains(Axis4Enum.W) ? (double)MathHelper.floor(this.w) : this.w;
        return new Vec4d(x, y, z, w);
    }

    public double getComponentAlongAxis(Direction4.Axis4 axis) {
        return axis.choose(this.x, this.y, this.z, this.w);
    }

    public Vec4d withAxis(Axis4Enum axis, double value) {
        double x = axis == Axis4Enum.X ? value : this.x;
        double y = axis == Axis4Enum.Y ? value : this.y;
        double z = axis == Axis4Enum.Z ? value : this.z;
        double w = axis == Axis4Enum.W ? value : this.z;
        return new Vec4d(x, y, z, w);
    }

    public Vec4d withBias(Direction4 direction, double value) {
        Vec4i<?, ?> vec4i = direction.getVector4();
        return new Vec4d(this.x + value * (double)vec4i.getX4(), this.y + value * (double)vec4i.getY4(), this.z + value * (double)vec4i.getZ4(), this.w + value * (double)vec4i.getW4());
    }

    public final Double getX() {
        return this.x;
    }

    public final Double getY() {
        return this.y;
    }

    public final Double getZ() {
        return this.z;
    }

    public final Double getW() {
        return this.w;
    }

    static {
        CODEC = Codec.DOUBLE.listOf().comapFlatMap((list) -> {
            return Util.decodeFixedLengthList(list, 4).map((listx) -> {
                return new Vec4d((Double)listx.get(0), (Double)listx.get(1), (Double)listx.get(2), (Double)listx.get(3));
            });
        }, (vec4d) -> {
            return List.of(vec4d.getX(), vec4d.getY(), vec4d.getZ());
        });
        ZERO = new Vec4d(0.0, 0.0, 0.0, 0.0);
    }
}
