//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4.Axis4;
import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;
import java.util.stream.IntStream;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public class Vec4i implements Comparable<Vec4i>, Pos3Equivalent<Vec3i> {
    public static final Codec<Vec4i> CODEC;
    public static final Vec4i ZERO;
    private int x;
    private int y;
    private int z;
    private int w;
    private static Function<Vec4i, DataResult<Vec4i>> createRangeValidator(int maxAbsValue) {
        return (vec) -> {
            return Math.abs(vec.getX()) < maxAbsValue && Math.abs(vec.getY()) < maxAbsValue && Math.abs(vec.getZ()) < maxAbsValue && Math.abs(vec.getW()) < maxAbsValue ? DataResult.success(vec) : DataResult.error("Position out of range, expected at most " + maxAbsValue + ": " + vec);
        };
    }

    public static Codec<Vec4i> createOffsetCodec(int maxAbsValue) {
        return CODEC.flatXmap(createRangeValidator(maxAbsValue), createRangeValidator(maxAbsValue));
    }

    public Vec4i(int x, int y, int z, int w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vec4i(double x, double y, double z, double w) {
        this(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z), MathHelper.floor(w));
    }

    public Vec4i(Vec3i pos3) {
        this(pos3.getX(), pos3.getY(), pos3.getZ());
    }

    public Vec4i(int x, int y, int z) {
        this.w = (int)(Math.floor(0.5 + (x + 0d)/FDMCConstants.STEP_DISTANCE));
        this.x = x - w * FDMCConstants.STEP_DISTANCE;
        this.y = y;
        this.z = z;
    }

    @Override
    public Vec3i toPos3() {
        int x = this.x + FDMCConstants.STEP_DISTANCE * this.w;
        int y = this.y;
        int z = this.z;
        return new Vec3i(x, y, z);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Vec4i)) {
            return false;
        } else {
            Vec4i vec4i = (Vec4i)o;
            if (this.getX() != vec4i.getX()) {
                return false;
            } else if (this.getY() != vec4i.getY()) {
                return false;
            } else if (this.getZ() != vec4i.getZ()){
                return false;
            } else{
                return this.getW() != vec4i.getW();
            }
        }
    }

    public int hashCode() {
        return this.getX() + (this.getY() + (this.getZ() + this.getW() * 31) * 31) * 31;
    }

    public int compareTo(Vec4i vec4i) {
        if(this.getW() != vec4i.getW()) {
            return this.getW() - vec4i.getW();
        } else if(this.getY() != vec4i.getY()) {
            return this.getY() - vec4i.getY();
        } else if(this.getZ() != vec4i.getZ()) {
            return this.getZ() - vec4i.getZ();
        } else {
            return this.getX() - vec4i.getX();
        }
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }
    public int getW() {
        return this.w;
    }

    protected Vec4i setX(int x) {
        this.x = x;
        return this;
    }

    protected Vec4i setY(int y) {
        this.y = y;
        return this;
    }

    protected Vec4i setZ(int z) {
        this.z = z;
        return this;
    }

    protected Vec4i setW(int w) {
        this.w = w;
        return this;
    }

    public Vec4i add(double x, double y, double z, double w) {
        return x == 0.0 && y == 0.0 && z == 0.0 ? this : new Vec4i((double)this.getX() + x, (double)this.getY() + y, (double)this.getZ() + z, (double)this.getW() + w);
    }

    public Vec4i add(int x, int y, int z, int w) {
        return x == 0 && y == 0 && z == 0 ? this : new Vec4i(this.getX() + x, this.getY() + y, this.getZ() + z, this.getW() + w);
    }

    public Vec4i add(Vec4i vec) {
        return this.add(vec.getX(), vec.getY(), vec.getZ(), vec.getW());
    }

    public Vec4i subtract(Vec4i vec) {
        return this.add(-vec.getX(), -vec.getY(), -vec.getZ(), -vec.getW());
    }

    public Vec4i multiply(int scale) {
        if (scale == 1) {
            return this;
        } else {
            return scale == 0 ? ZERO : new Vec4i(this.getX() * scale, this.getY() * scale, this.getZ() * scale, this.getW() * scale);
        }
    }

    public Vec4i up() {
        return this.up(1);
    }

    public Vec4i up(int distance) {
        return this.offset(Direction4.UP, distance);
    }

    public Vec4i down() {
        return this.down(1);
    }

    public Vec4i down(int distance) {
        return this.offset(Direction4.DOWN, distance);
    }

    public Vec4i north() {
        return this.north(1);
    }

    public Vec4i north(int distance) {
        return this.offset(Direction4.NORTH, distance);
    }

    public Vec4i south() {
        return this.south(1);
    }

    public Vec4i south(int distance) {
        return this.offset(Direction4.SOUTH, distance);
    }

    public Vec4i west() {
        return this.west(1);
    }

    public Vec4i west(int distance) {
        return this.offset(Direction4.WEST, distance);
    }

    public Vec4i east() {
        return this.east(1);
    }

    public Vec4i east(int distance) {
        return this.offset(Direction4.EAST, distance);
    }

    public Vec4i kata() {
        return this.kata(1);
    }

    public Vec4i kata(int distance) {
        return this.offset(Direction4.KATA, distance);
    }

    public Vec4i ana() {
        return this.ana(1);
    }

    public Vec4i ana(int distance) {
        return this.offset(Direction4.ANA, distance);
    }

    public Vec4i offset(Direction4 Direction4) {
        return this.offset((Direction4)Direction4, 1);
    }

    public Vec4i offset(Direction4 Direction4, int distance) {
        return distance == 0 ? this : new Vec4i(
                this.getX() + Direction4.getOffsetX() * distance,
                this.getY() + Direction4.getOffsetY() * distance,
                this.getZ() + Direction4.getOffsetZ() * distance,
                this.getW() + Direction4.getOffsetW() * distance);
    }

    public Vec4i offset(Axis4 axis, int distance) {
        if (distance == 0) {
            return this;
        } else {
            int i = axis == Axis4.X ? distance : 0;
            int j = axis == Axis4.Y ? distance : 0;
            int k = axis == Axis4.Z ? distance : 0;
            int l = axis == Axis4.W ? distance : 0;
            return new Vec4i(this.getX() + i, this.getY() + j, this.getZ() + k, this.getW() + l);
        }
    }

    public boolean isWithinDistance(Vec4i vec, double distance) {
        return this.getSquaredDistance(vec) < MathHelper.square(distance);
    }

    public boolean isWithinDistance(Position4 pos, double distance) {
        return this.getSquaredDistance(pos) < MathHelper.square(distance);
    }

    public double getSquaredDistance(Vec4i vec) {
        return this.getSquaredDistance((double)vec.getX(), (double)vec.getY(), (double)vec.getZ(), (double)vec.getW());
    }

    public double getSquaredDistance(Position4<Double> pos) {
        return this.getSquaredDistanceFromCenter(pos.getX(), pos.getY(), pos.getZ(), pos.getW());
    }

    public double getSquaredDistanceFromCenter(double x, double y, double z, double w) {
        double dx = (double)this.getX() + 0.5 - x;
        double dy = (double)this.getY() + 0.5 - y;
        double dz = (double)this.getZ() + 0.5 - z;
        double dw = (double)this.getW() + 0.5 - w;
        return dx * dx + dy * dy + dz * dz + dw * dw;
    }

    public double getSquaredDistance(double x, double y, double z, double w) {
        double dx = (double)this.getX() - x;
        double dy = (double)this.getY() - y;
        double dz = (double)this.getZ() - z;
        double dw = (double)this.getZ() - w;
        return dx * dx + dy * dy + dz * dz + dw * dw;
    }

    public int getManhattanDistance(Vec4i vec) {
        float dx = (float)Math.abs(vec.getX() - this.getX());
        float dy = (float)Math.abs(vec.getY() - this.getY());
        float dz = (float)Math.abs(vec.getZ() - this.getZ());
        float dw = (float)Math.abs(vec.getW() - this.getW());
        return (int)(dx + dy + dz + dw);
    }

    public int getComponentAlongAxis(Direction4.Axis4 Axis4) {
        return Axis4.choose(this.x, this.y, this.z, this.w);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("x", this.getX())
                .add("y", this.getY())
                .add("z", this.getZ())
                .add("w", this.getW())
                .toString();
    }

    public String toShortString() {
        int var10000 = this.getX();
        return "" + var10000 + ", " + this.getY() + ", " + this.getZ() + ", " + this.getW();
    }

    static {
        CODEC = Codec.INT_STREAM.comapFlatMap((intStream) -> {
            return Util.toArray(intStream, 4).map((is) -> {
                return new Vec4i(is[0], is[1], is[2], is[3]);
            });
        }, (Vec4i) -> {
            return IntStream.of(new int[]{Vec4i.getX(), Vec4i.getY(), Vec4i.getZ(), Vec4i.getW()});
        });
        ZERO = new Vec4i(0, 0, 0, 0);
    }
}
