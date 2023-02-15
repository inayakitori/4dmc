package com.gmail.inayakitorikhurram.fdmc.mixin.math;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4i;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockPos.class)
public abstract class BlockPosMixin implements BlockPos4.BlockPos4Impl, Vec4i.DirectWAccess {
    @Override
    public BlockPos4.BlockPos4Impl newInstance(int x, int y, int z, int w) {
        BlockPos blockPos = new BlockPos(x, y, z);
        ((DirectWAccess) blockPos).directAddW(w);
        return (BlockPos4.BlockPos4Impl)(Object) blockPos;
    }

    @Override
    public BlockPos4.BlockPos4Impl getZeroInstance() {
        return ORIGIN4;
    }

    @Override
    public BlockPos4.BlockPos4Impl self() {
        return this;
    }

    @Override
    public BlockPos add(double x, double y, double z) {
        return this.add4(x, y, z, 0.0).asBlockPos();
    }

    @Override
    public BlockPos add(int x, int y, int z) {
        return this.add4(x, y, z, 0).asBlockPos();
    }

    @Override
    public BlockPos add(Vec3i vec) {
        return this.add4(Vec4i.asVec4i(vec)).asBlockPos();
    }

    @Override
    public BlockPos subtract(Vec3i vec) {
        return this.subtract4(Vec4i.asVec4i(vec)).asBlockPos();
    }

    @Override
    public BlockPos multiply(int scale) {
        return this.multiply4(scale).asBlockPos();
    }

    @Override
    public BlockPos offset(Direction direction, int distance) {
        return this.offset4(Direction4.asDirection4(direction), distance).asBlockPos();
    }

    @Override
    public BlockPos offset(Direction.Axis axis, int distance) {
        return this.offset4(Direction4.Axis4.asAxis4(axis), distance).asBlockPos();
    }

    @Override
    public BlockPos rotate(BlockRotation rotation) {
        switch (rotation) {
            default: {
                return this.asBlockPos();
            }
            case CLOCKWISE_90: {
                return BlockPos4.newBlockPos4(-this.getZ4(), this.getY4(), this.getX4(), this.getW4()).asBlockPos();
            }
            case CLOCKWISE_180: {
                return BlockPos4.newBlockPos4(-this.getX4(), this.getY4(), -this.getZ4(), this.getW4()).asBlockPos();
            }
            case COUNTERCLOCKWISE_90:
                return BlockPos4.newBlockPos4(this.getZ4(), this.getY4(), -this.getX4(), this.getW4()).asBlockPos();
        }
    }

    @Override
    public BlockPos withY(int y) {
        return BlockPos4.newBlockPos4(this.getX4(), y, this.getZ4(), this.getW4()).asBlockPos();
    }

    @Override
    public BlockPos.Mutable mutableCopy() {
        return this.mutableCopy4().asBlockPosMutable();
    }
}
