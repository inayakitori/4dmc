package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import com.gmail.inayakitorikhurram.fdmc.util.UtilConstants;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.*;

public interface BlockPos4<E extends BlockPos4<E, T>, T extends BlockPos> extends Vec4i<E, T> {


    static BlockPos4<?, ?> newBlockPos4(int x, int y, int z, int w) {
        return UtilConstants.ORIGIN4.newInstance(x, y, z, w);
    }

    static BlockPos4<?, ?> newBlockPos4(double x, double y, double z, double w) {
        return UtilConstants.ORIGIN4.newInstance(x, y, z, w);
    }

    static BlockPos4<?, ?> from3i(int x, int y, int z) {
        int w = (int)(Math.floor(0.5 + (x + 0d)/ FDMCConstants.STEP_DISTANCE));
        x = x - w * FDMCConstants.STEP_DISTANCE;

        return newBlockPos4(x, y, z, w);
    }

    static BlockPos4<?, ?> fromVec3i(Vec3i vec3i) {
        return fromVec4i(Vec4i.asVec4i(vec3i));
    }

    static BlockPos4<?, ?> fromVec4i(Vec4i<?, ?> vec4i) {
        return newBlockPos4(vec4i.getX4(), vec4i.getY4(), vec4i.getZ4(), vec4i.getW4());
    }

    static BlockPos4<?, ?> asBlockPos4(BlockPos pos) {
        return (BlockPos4<?, ?>)(Object) pos;
    }

    default BlockPos asBlockPos() {
        return (BlockPos)(Object) this;
    }

    default Vec4d toCenterPos4() {
        return Vec4d.ofCenter(this);
    }

    default E rotate4(BlockRotation rotation) {
        switch (rotation) {
            default: {
                return self();
            }
            case CLOCKWISE_90: {
                return newInstance(-this.getZ4(), this.getY4(), this.getX4(), this.getW4());
            }
            case CLOCKWISE_180: {
                return newInstance(-this.getX4(), this.getY4(), -this.getZ4(), this.getW4());
            }
            case COUNTERCLOCKWISE_90:
                return newInstance(this.getZ4(), this.getY4(), -this.getX4(), this.getW4());
        }
    }

    default E withY4(int y) {
        return newInstance(this.getX4(), y, this.getZ4(), this.getW4());
    }

    default BlockPos4<?, ?> toImmutable4() {
        return (BlockPos4<?, ?>)(Object) toImmutable();
    }

    default BlockPos4.Mutable4 mutableCopy4() {
        return Mutable4.newMutable4(this.getX4(), this.getY4(), this.getZ4(), this.getW4());
    }

    // the following ensure that BlockPos methods are exposed
    // inherited from BlockPos
    default Vec3d toCenterPos() {
        return asBlockPos().toCenterPos();
    }
    // inherited from BlockPos
    default BlockPos rotate(BlockRotation rotation) {
        return asBlockPos().rotate(rotation);
    }
    // inherited from BlockPos
    default BlockPos withY(int y) {
        return asBlockPos().withY(y);
    }
    // inherited from BlockPos
    default BlockPos toImmutable() {
        return asBlockPos().toImmutable();
    }
    // inherited from BlockPos
    default BlockPos.Mutable mutableCopy() {
        return asBlockPos().mutableCopy();
    }

    interface BlockPos4Impl extends BlockPos4<BlockPos4Impl, BlockPos> {
        // implemented in mixin
    }

    interface Mutable4 extends BlockPos4<BlockPos4Impl, BlockPos>{
        static Mutable4 newMutable4() {
            return asMutable4(new BlockPos.Mutable());
        }

        static Mutable4 newMutable4(int x, int y, int z, int w) {
            return asMutable4(new BlockPos.Mutable(x, y, z)).setW4(w);
        }

        static Mutable4 newMutable4(double x, double y, double z, double w) {
            return asMutable4(new BlockPos.Mutable(x, y, z)).setW4(MathHelper.floor(w));
        }

        static Mutable4 asMutable4(BlockPos.Mutable mutable) {
            return (Mutable4)(Object) mutable;
        }

        default BlockPos.Mutable asBlockPosMutable() {
            return (BlockPos.Mutable)(Object) this;
        }

        BlockPos.Mutable setW(int w);

        Mutable4 setX4(int x);

        Mutable4 setY4(int y);

        Mutable4 setZ4(int z);

        Mutable4 setW4(int w);

        Mutable4 set4(int x, int y, int z, int w);

        Mutable4 set4(double x, double y, double z, double w);

        Mutable4 set4(Vec4i<?, ?> pos);
        // inherited from BlockPos.Mutable //TODO: wtf does this even do?
        //BlockPos.Mutable set4(AxisCycleDirection axis, int x, int y, int z);

        Mutable4 set4(Vec4i<?, ?> pos, Direction4 direction);

        Mutable4 set4(Vec4i<?, ?> pos, int x, int y, int z, int w);

        Mutable4 set4(Vec4i<?, ?> vec1, Vec4i<?, ?> vec2);

        Mutable4 move4(Direction4 direction);

        Mutable4 move4(Direction4 direction, int distance);

        Mutable4 move4(int dx, int dy, int dz, int dw);

        Mutable4 move4(Vec4i<?, ?> vec);

        Mutable4 clamp4(Direction4.Axis4 axis, int min, int max);

        // the following ensure that BlockPos.Mutable methods are exposed
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable set(int x, int y, int z) {
            return asBlockPosMutable().set(x, y, z);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable set(double x, double y, double z) {
            return asBlockPosMutable().set(x, y, z);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable set(Vec3i pos) {
            return asBlockPosMutable().set(pos);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable set(long pos) {
            return asBlockPosMutable().set(pos);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable set(AxisCycleDirection axis, int x, int y, int z) {
            return asBlockPosMutable().set(axis, x, y, z);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable set(Vec3i pos, Direction direction) {
            return asBlockPosMutable().set(pos, direction);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable set(Vec3i pos, int x, int y, int z) {
            return asBlockPosMutable().set(pos, x, y, z);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable set(Vec3i vec1, Vec3i vec2) {
            return asBlockPosMutable().set(vec1, vec2);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable move(Direction direction) {
            return asBlockPosMutable().move(direction);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable move(Direction direction, int distance) {
            return asBlockPosMutable().move(direction, distance);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable move(int dx, int dy, int dz) {
            return asBlockPosMutable().move(dx, dy, dz);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable move(Vec3i vec) {
            return asBlockPosMutable().move(vec);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable clamp(Direction.Axis axis, int min, int max) {
            return asBlockPosMutable().clamp(axis, min, max);
        }
    }
}