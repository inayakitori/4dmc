//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public final class Vec4f implements Pos3Equivalent<Vec3f> {
    public static final Codec<Vec4f> CODEC;
    public static Vec4f NEGATIVE_X;
    public static Vec4f POSITIVE_X;
    public static Vec4f NEGATIVE_Y;
    public static Vec4f POSITIVE_Y;
    public static Vec4f NEGATIVE_Z;
    public static Vec4f POSITIVE_Z;
    public static Vec4f NEGATIVE_W;
    public static Vec4f POSITIVE_W;
    public static Vec4f ZERO;
    private float x;
    private float y;
    private float z;
    private float w;

    public Vec4f(Vec3f pos3){
        this(pos3.getX(), pos3.getY(), pos3.getZ());
    }

    public Vec4f(float x, float y, float z) {
        this.w = (float) Math.floor(0.5 + (x/FDMCConstants.STEP_DISTANCE) );
        this.x = x - this.w * FDMCConstants.STEP_DISTANCE;
        this.y = y;
        this.z = z;
    }

    public Vec4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }


    public Vec4f(Vec4d other) {
        this(
                (float)other.x,
                (float)other.y,
                (float)other.z,
                (float)other.w);
    }


    @Override
    public Vec3f toPos3() {
        float x = this.x + FDMCConstants.STEP_DISTANCE * this.y;
        float y = this.y;
        float z = this.z;
        return new Vec3f(x, y, z);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Vec4f Vec4f = (Vec4f)o;
            if (Float.compare(Vec4f.x, this.x) != 0) {
                return false;
            } else if (Float.compare(Vec4f.y, this.y) != 0) {
                return false;
            } else if (Float.compare(Vec4f.w, this.w) != 0) {
                return false;
            } else {
                return Float.compare(Vec4f.z, this.z) == 0;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.x);
        i = 31 * i + Float.floatToIntBits(this.y);
        i = 31 * i + Float.floatToIntBits(this.z);
        i = 31 * i + Float.floatToIntBits(this.w);
        return i;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }
    public float getW() {
        return this.z;
    }

    public void scale(float scale) {
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
        this.w *= scale;
    }

    public void multiplyComponentwise(float x, float y, float z, float w) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        this.z *= w;
    }

    public void clamp(Vec4f min, Vec4f max) {
        this.x = MathHelper.clamp(this.x, min.getX(), max.getX());
        this.y = MathHelper.clamp(this.y, min.getX(), max.getY());
        this.z = MathHelper.clamp(this.z, min.getZ(), max.getZ());
        this.w = MathHelper.clamp(this.w, min.getW(), max.getW());
    }

    public void clamp(float min, float max) {
        this.x = MathHelper.clamp(this.x, min, max);
        this.y = MathHelper.clamp(this.y, min, max);
        this.z = MathHelper.clamp(this.z, min, max);
        this.w = MathHelper.clamp(this.w, min, max);
    }

    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void set(Vec4f vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        this.w = vec.w;
    }

    public void add(float x, float y, float z, float w) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
    }

    public void add(Vec4f vector) {
        this.x += vector.x;
        this.y += vector.y;
        this.z += vector.z;
        this.w += vector.w;
    }

    public void subtract(Vec4f other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        this.w -= other.w;
    }

    public float dot(Vec4f other) {
        return this.x * other.x
                + this.y * other.y
                + this.z * other.z
                + this.w * other.w;
    }

    public boolean normalize() {
        float f = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
        if ((double)f < 1.0E-5) {
            return false;
        } else {
            float g = MathHelper.fastInverseSqrt(f);
            this.x *= g;
            this.y *= g;
            this.z *= g;
            this.w *= g;
            return true;
        }
    }

    public void lerp(Vec4f vector, float delta) {
        float f = 1.0F - delta;
        this.x = this.x * f + vector.x * delta;
        this.y = this.y * f + vector.y * delta;
        this.z = this.z * f + vector.z * delta;
        this.w = this.w * f + vector.w * delta;
    }

    public Vec4f copy() {
        return new Vec4f(this.x, this.y, this.z, this.w);
    }

    public void modify(Float2FloatFunction function) {
        this.x = function.get(this.x);
        this.y = function.get(this.y);
        this.z = function.get(this.z);
        this.w = function.get(this.w);
    }

    public String toString() {
        return "[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
    }

    static {
        CODEC = Codec.FLOAT.listOf().comapFlatMap((vec) -> {
            return Util.toArray(vec, 4).map((vecx) -> {
                return new Vec4f((Float)vecx.get(0), (Float)vecx.get(1), (Float)vecx.get(2), (Float)vecx.get(3));
            });
        }, (vec) -> {
            return ImmutableList.of(vec.x, vec.y, vec.z);
        });
        NEGATIVE_X = new Vec4f(-1.0F,  0.0F,  0.0F,  0.0F);
        POSITIVE_X = new Vec4f( 1.0F,  0.0F,  0.0F,  0.0F);
        NEGATIVE_Y = new Vec4f( 0.0F, -1.0F,  0.0F,  0.0F);
        POSITIVE_Y = new Vec4f( 0.0F,  1.0F,  0.0F,  0.0F);
        NEGATIVE_Z = new Vec4f( 0.0F,  0.0F, -1.0F,  0.0F);
        POSITIVE_Z = new Vec4f( 0.0F,  0.0F,  1.0F,  0.0F);
        NEGATIVE_W = new Vec4f( 0.0F,  0.0F,  0.0F,  -1.0F);
        POSITIVE_W = new Vec4f( 0.0F,  0.0F,  0.0F,  1.0F);
        ZERO       = new Vec4f( 0.0F,  0.0F,  0.0F,  0.0F);
    }

}
