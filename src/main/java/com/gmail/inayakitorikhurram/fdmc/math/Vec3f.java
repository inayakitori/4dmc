//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.gmail.inayakitorikhurram.fdmc.math;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

/**
 * {@link Vec2f} is used to represent horizontal movement and rotation in vanilla, but it is limited to 2 dimensions.
 * This class extends it to have 3 dimensions instead.
 */
public class Vec3f extends Vec2f {
    public static final Vec3f ZERO = new Vec3f(0f, 0f, 0f);

    public final float z;

    public Vec3f(float x, float y, float z) {
        super(x, y);
        this.z = z;
    }

    public static Vec3f of(Vec2f vec2f) {
        return vec2f instanceof Vec3f vec3f
            ? vec3f
            : new Vec3f(vec2f.x, vec2f.y, 0f);
    }

    @Override
    public Vec3f multiply(float value) {
        return new Vec3f(this.x * value, this.y * value, this.z * value);
    }

    @Override
    public float dot(Vec2f vec2f) {
        Vec3f vec = Vec3f.of(vec2f);
        return this.x * vec.x + this.y * vec.y + this.z * vec.z;
    }

    @Override
    public Vec3f add(Vec2f vec2f) {
        Vec3f vec = Vec3f.of(vec2f);
        return new Vec3f(this.x + vec.x, this.y + vec.y, this.z + vec.z);
    }

    @Override
    public Vec3f add(float value) {
        return new Vec3f(this.x + value, this.y + value, this.z + value);
    }

    @Override
    public boolean equals(Vec2f other) {
        return super.equals(other) && this.z == Vec3f.of(other).z;
    }

    @Override
    public Vec3f normalize() {
        float len = this.length();
        return len < 1.0E-4f ? ZERO : new Vec3f(this.x / len, this.y / len, this.z / len);
    }

    @Override
    public float length() {
        return MathHelper.sqrt(lengthSquared());
    }

    @Override
    public float lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    @Override
    public float distanceSquared(Vec2f vec2f) {
        Vec3f vec = Vec3f.of(vec2f);
        float dx = vec.x - this.x;
        float dy = vec.y - this.y;
        float dz = vec.z - this.z;
        return dx * dx + dy * dy + dz * dz;
    }

    @Override
    public Vec3f negate() {
        return new Vec3f(-x, -y, -z);
    }
}
