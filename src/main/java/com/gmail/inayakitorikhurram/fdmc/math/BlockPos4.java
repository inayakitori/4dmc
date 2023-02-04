package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4.Axis4;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public class BlockPos4 extends Vec4i {
    public static final BlockPos4 ORIGIN;

    public BlockPos4(int i, int j, int k, int l) {
        super(i, j, k, l);
    }

    public BlockPos4(double d, double e, double f, double g) {
        super(d, e, f, g);
    }

    public BlockPos4(Vec4d pos) {
        this(pos.x, pos.y, pos.z, pos.w);
    }

    public BlockPos4(Position4<Integer> pos) {
        this(pos.getX(), pos.getY(), pos.getZ(), pos.getW());
    }

    public BlockPos4(Vec4i pos) {
        this(pos.getX(), pos.getY(), pos.getZ(), pos.getW());
    }

    public BlockPos4(BlockPos pos3) {
        super(pos3.getX(), pos3.getY(), pos3.getZ());
    }

    public BlockPos toPos3() {
        return new BlockPos(super.toPos3());
    }

    public static long removeChunkSectionLocalY(long y) {
        return y & -16L;
    }

    public BlockPos4 add(double dx, double dy, double dz, double dw) {
        return dx == 0.0 && dy == 0.0 && dz == 0.0 && dw == 0.0 ? this :
                new BlockPos4(
                        (double) this.getX() + dx,
                        (double) this.getY() + dy,
                        (double) this.getZ() + dz,
                        (double) this.getW() + dw
                );
    }

    public BlockPos4 add(int dx, int dy, int dz, int dw) {
        return dx == 0 && dy == 0 && dz == 0 && dw == 0 ? this:
                new BlockPos4(
                        this.getX() + dx,
                        this.getY() + dy,
                        this.getZ() + dz,
                        this.getW() + dw
                );
    }

    public BlockPos4 add(Vec4i vec4i) {
        return this.add(
                vec4i.getX(),
                vec4i.getY(),
                vec4i.getZ(),
                vec4i.getW()
        );
    }

    public BlockPos4 subtract(Vec4i vec4i) {
        return this.add(
                -vec4i.getX(),
                -vec4i.getY(),
                -vec4i.getZ(),
                -vec4i.getW()
        );
    }

    public BlockPos4 multiply(int i) {
        if (i == 1) {
            return this;
        } else {
            return i == 0 ? ORIGIN : new BlockPos4(
                    this.getX() * i,
                    this.getY() * i,
                    this.getZ() * i,
                    this.getW() * i
            );
        }
    }

    public BlockPos4 up() {
        return this.offset(Direction4.UP);
    }

    public BlockPos4 up(int distance) {
        return this.offset(Direction4.UP, distance);
    }

    public BlockPos4 down() {
        return this.offset(Direction4.DOWN);
    }

    public BlockPos4 down(int i) {
        return this.offset(Direction4.DOWN, i);
    }

    public BlockPos4 north() {
        return this.offset(Direction4.NORTH);
    }

    public BlockPos4 north(int distance) {
        return this.offset(Direction4.NORTH, distance);
    }

    public BlockPos4 south() {
        return this.offset(Direction4.SOUTH);
    }

    public BlockPos4 south(int distance) {
        return this.offset(Direction4.SOUTH, distance);
    }

    public BlockPos4 west() {
        return this.offset(Direction4.WEST);
    }

    public BlockPos4 west(int distance) {
        return this.offset(Direction4.WEST, distance);
    }

    public BlockPos4 east() {
        return this.offset(Direction4.EAST);
    }

    public BlockPos4 east(int distance) {
        return this.offset(Direction4.EAST, distance);
    }

    public BlockPos4 kata() {
        return this.offset(Direction4.KATA);
    }

    public BlockPos4 kata(int distance) {
        return this.offset(Direction4.KATA, distance);
    }

    public BlockPos4 ana() {
        return this.offset(Direction4.ANA);
    }

    public BlockPos4 ana(int distance) {
        return this.offset(Direction4.ANA, distance);
    }

    public BlockPos4 offset(Direction4 Direction4) {
        return new BlockPos4(
                this.getX() + Direction4.getOffsetX(),
                this.getY() + Direction4.getOffsetY(),
                this.getZ() + Direction4.getOffsetZ(),
                this.getW() + Direction4.getOffsetW()
        );
    }

    public BlockPos4 offset(Direction4 Direction4, int i) {
        return i == 0 ? this : new BlockPos4(
                this.getX() + Direction4.getOffsetX() * i,
                this.getY() + Direction4.getOffsetY() * i,
                this.getZ() + Direction4.getOffsetZ() * i,
                this.getW() + Direction4.getOffsetW() * i);
    }

    public BlockPos4 offset(Axis4 axis, int i) {
        if (i == 0) {
            return this;
        } else {
            int j = axis == Axis4.X ? i : 0;
            int k = axis == Axis4.Y ? i : 0;
            int l = axis == Axis4.Z ? i : 0;
            int m = axis == Axis4.W ? i : 0;
            return new BlockPos4(this.getX() + j, this.getY() + k, this.getZ() + l, this.getZ() + m);
        }
    }
    static {
        ORIGIN = new BlockPos4(0, 0, 0, 0);
    }
}
