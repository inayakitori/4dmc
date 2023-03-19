package com.gmail.inayakitorikhurram.fdmc.mixin.math;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4i;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import com.google.common.base.MoreObjects;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Vec3i.class)
public abstract class Vec3iMixin implements Vec4i.Vec4iImpl, Vec4i.DirectWAccess {
    @Shadow private int x;
    @Shadow @Final
    public static Vec3i ZERO = (Vec3i) Vec4i.ZERO4;

    @Shadow public abstract int getY();

    @Shadow public abstract int getZ();

    private int w;

    @Inject(method = "<init>(III)V", at = @At("RETURN"))
    private void initW(int x, int y, int z, CallbackInfo ci) {
        w = (int)(Math.floor(0.5 + (x + 0d)/ FDMCConstants.STEP_DISTANCE));
        this.x -= w * FDMCConstants.STEP_DISTANCE;
    }

    @Override
    public Vec4iImpl newInstance(int x, int y, int z, int w) {
        Vec3iMixin vec3i = (Vec3iMixin)(Object) new Vec3i(x, y, z);
        vec3i.directAddW(w);
        return vec3i;
    }

    @Override
    public void directSetW(int w) {
        this.w = w;
    }

    @Override
    public void directAddW(int w) {
        this.w += w;
    }

    //TODO eventually remove once 4D worlds are implemented
    @Inject(method = "getX", at = @At("HEAD"), cancellable = true)
    public void getX(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(this.getX4() + FDMCConstants.STEP_DISTANCE * this.getW4());
        cir.cancel();
    }

    @Override
    public int getW() {
        return 0; // since w is currently encoded in X, use getW4() instead
    }

    @Override
    public int getX4() {
        return x;
    }

    @Override
    public int getY4() {
        return this.getY();
    }

    @Override
    public int getZ4() {
        return this.getZ();
    }

    @Override
    public int getW4() {
        return w;
    }

    @Override
    public Vec4iImpl getZeroInstance() {
        return ZERO4;
    }

    @Override
    public Vec4iImpl self() {
        return this;
    }

    @Override
    public Vec3i add(double x, double y, double z) {
        return this.add4(x, y, z, 0.0).asVec3i();
    }

    @Override
    public Vec3i add(int x, int y, int z) {
        return this.add4(x, y, z, 0).asVec3i();
    }

    @Override
    public Vec3i add(Vec3i vec) {
        return this.add4(Vec4i.asVec4i(vec)).asVec3i();
    }

    @Override
    public Vec3i subtract(Vec3i vec) {
        return this.subtract4(Vec4i.asVec4i(vec)).asVec3i();
    }

    @Override
    public Vec3i multiply(int scale) {
        return this.multiply4(scale).asVec3i();
    }

    @Override
    public Vec3i offset(Direction direction, int distance) {
        return this.offset4(Direction4.asDirection4(direction), distance).asVec3i();
    }

    @Override
    public Vec3i offset(Direction.Axis axis, int distance) {
        return this.offset4(Direction4.Axis4.asAxis4(axis), distance).asVec3i();
    }

    @Override
    public boolean isWithinDistance(Vec3i vec, double distance) {
        return this.isWithinDistance4(Vec4i.asVec4i(vec), distance);
    }

    /* //TODO: Make Position4 extend Position
    @Override
    public boolean isWithinDistance(Position pos, double distance) {
        if (vec instanceof Position4) {
            return isWithinDistance4((Position4) pos, distance);
        }
        return super.isWithinDistance(pos, distance);
    }
     */

    @Override
    public double getSquaredDistance(Vec3i vec) {
        return this.getSquaredDistance4(Vec4i.asVec4i(vec));
    }

    /* //TODO: Make Position4 extend Position
    @Override
    public double getSquaredDistance(Position pos) {
        if (pos instanceof Position4) {
            return getSquaredDistance4((Position4) pos);
        }
        return super.getSquaredDistance(pos);
    }
     */

    @Override
    public int getManhattanDistance(Vec3i vec) {
        return getManhattanDistance4(Vec4i.asVec4i(vec));
    }

    @Override
    public int getComponentAlongAxis(Direction.Axis axis) {
        return this.getComponentAlongAxis4(Direction4.Axis4.asAxis4(axis));
    }

    @Override
    public int compareTo(Vec3i vec3i) {
        Vec4i<?, ?> vec4i = Vec4i.asVec4i(vec3i);
        if(this.getW4() != vec4i.getW4()) {
            return this.getW4() - vec4i.getW4();
        } else if(this.getY4() != vec4i.getY4()) {
            return this.getY4() - vec4i.getY4();
        } else if(this.getZ4() != vec4i.getZ4()) {
            return this.getZ4() - vec4i.getZ4();
        } else {
            return this.getX4() - vec4i.getX4();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Vec4i<?, ?>)) {
            return false;
        }
        Vec4i<?, ?> vec4i = (Vec4i<?, ?>) o;
        return this.getX() == vec4i.getX()
                && this.getY() == vec4i.getY()
                && this.getZ() == vec4i.getZ()
                && this.getW() == vec4i.getW();
    }

    @Override
    public int hashCode() {
        return this.getX4() + (this.getY4() + (this.getZ4() + this.getW4() * 31) * 31) * 31;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("x", this.getX4())
                .add("y", this.getY4())
                .add("z", this.getZ4())
                .add("w", this.getW4())
                .toString();
    }

    public String toShortString() {
        return "" + this.getX4() + ", " + this.getY4() + ", " + this.getZ4() + ", " + this.getW4();
    }
}
