package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.google.common.base.MoreObjects;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

public class Vec4iImpl extends Vec3i implements Vec4i<Vec4iImpl, Vec3i> {
    private final int w;

    protected Vec4iImpl(int x, int y, int z, int w) {
        super(x, y, z);
        this.w = w;
    }

    @Override
    public Vec4iImpl newInstance(int x, int y, int z, int w) {
        return new Vec4iImpl(x, y, z, w);
    }

    //TODO eventually remove once 4D worlds are implemented
    @Override
    public int getX() {
        return super.getX() + FDMCConstants.STEP_DISTANCE * getW();
    }

    @Override
    public Vec3i newSuperInstance(int x, int y, int z) {
        return new Vec3i(x, y, z);
    }

    @Override
    public int getW() {
        return w;
    }

    @Override
    public Vec4iImpl getZeroInstance() {
        return null;
    }

    @Override
    public Vec4iImpl self() {
        return this;
    }

    @Override
    public Vec4iImpl add(double x, double y, double z) {
        return add4(x, y, z, 0.0);
    }

    @Override
    public Vec4iImpl add(int x, int y, int z) {
        return  add4(x, y, z, 0);
    }

    @Override
    public Vec4iImpl multiply(int scale) {
        return multiply4(scale);
    }

    @Override
    public Vec4iImpl offset(Direction direction, int distance) {
        return offset4(Direction4.fromDirection3(direction), distance);
    }

    @Override
    public Vec4iImpl offset(Direction.Axis axis, int distance) {
        return offset4(Direction4.Axis4.fromAxis(axis), distance);
    }

    @Override
    public boolean isWithinDistance(Vec3i vec, double distance) {
        if (vec instanceof Vec4i<?, ?>) {
            return isWithinDistance4((Vec4i<?, ?>) vec, distance);
        }
        return super.isWithinDistance(vec, distance);
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
        if (vec instanceof Vec4i<?, ?>) {
            return getSquaredDistance4((Vec4i<?, ?>) vec);
        }
        return super.getSquaredDistance(vec);
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
        if (vec instanceof Vec4i<?, ?>) {
            return getManhattanDistance4((Vec4i<?, ?>) vec);
        }
        return super.getManhattanDistance(vec);
    }

    @Override
    public int compareTo(Vec3i vec3i) {
        if(vec3i instanceof Vec4i<?, ?> && this.getW() != ((Vec4i<?, ?>) vec3i).getW()) {
            return this.getW() - ((Vec4i<?, ?>) vec3i).getW();
        } else if(this.getY() != vec3i.getY()) {
            return this.getY() - vec3i.getY();
        } else if(this.getZ() != vec3i.getZ()) {
            return this.getZ() - vec3i.getZ();
        } else {
            return this.getX() - vec3i.getX();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Vec4i)) {
            return false;
        }
        Vec4i<?, ?> vec4i = (Vec4i<?, ?>)o;
        return this.getX() == vec4i.getX()
                && this.getY() == vec4i.getY()
                && this.getZ() == vec4i.getZ()
                && this.getW() == vec4i.getW();
    }

    @Override
    public int hashCode() {
        return this.getX() + (this.getY() + (this.getZ() + this.getW() * 31) * 31) * 31;
    }

    @Override
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
}
