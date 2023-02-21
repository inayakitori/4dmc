package com.gmail.inayakitorikhurram.fdmc.mixin.math;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4i;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import com.gmail.inayakitorikhurram.fdmc.util.UtilConstants;
import com.google.common.collect.AbstractIterator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockPos.class)
public abstract class BlockPosMixin implements BlockPos4.BlockPos4Impl, Vec4i.DirectWAccess {
    @Inject(method = "iterate(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Ljava/lang/Iterable;", at = @At("HEAD"), cancellable = true)
    private static void iterate4d(BlockPos start, BlockPos end, CallbackInfoReturnable<Iterable<BlockPos>> cir) {
        BlockPos4<?, ?> start4 = BlockPos4.asBlockPos4(start);
        BlockPos4<?, ?> end4 = BlockPos4.asBlockPos4(end);
        cir.setReturnValue(iterate4d(Math.min(start4.getX4(), end4.getX4()), Math.min(start4.getY4(), end4.getY4()), Math.min(start4.getZ4(), end4.getZ4()), Math.min(start4.getW4(), end4.getW4()), Math.max(start4.getX4(), end4.getX4()), Math.max(start4.getY4(), end4.getY4()), Math.max(start4.getZ4(), end4.getZ4()), Math.max(start4.getW4(), end4.getW4())));
        cir.cancel();
    }

    private static Iterable<BlockPos> iterate4d(int startX, int startY, int startZ, int startW, int endX, int endY, int endZ, int endW) {
        int widthX = endX - startX + 1;
        int widthY = endY - startY + 1;
        int widthZ = endZ - startZ + 1;
        int widthW = endW - startW + 1;
        int volume = widthX * widthY * widthZ * widthW;
        return () -> new AbstractIterator<>() {
            private final BlockPos4.Mutable4 mutable4 = BlockPos4.Mutable4.newMutable4();
            private final BlockPos.Mutable mutable = mutable4.asBlockPosMutable();
            private int index = 0;

            @Override
            protected BlockPos computeNext() {
                if (this.index == volume) {
                    return this.endOfData();
                }
                int xOffset = this.index % widthX;
                int reducedVolume = this.index / widthX;
                int yOffset = reducedVolume % widthY;
                reducedVolume = reducedVolume / widthY;
                int zOffset = reducedVolume % widthZ;
                int wOffset = reducedVolume / widthZ;
                this.index++;
                mutable4.set4(startX + xOffset, startY + yOffset, startZ + zOffset, startW + wOffset);
                return mutable;
            }
        };
    }

    @Override
    public BlockPos4.BlockPos4Impl newInstance(int x, int y, int z, int w) {
        BlockPos blockPos = new BlockPos(x, y, z);
        ((DirectWAccess) blockPos).directAddW(w);
        return (BlockPos4.BlockPos4Impl)(Object) blockPos;
    }

    @Override
    public BlockPos4.BlockPos4Impl getZeroInstance() {
        return UtilConstants.ORIGIN4;
    }

    @Override
    public BlockPos4.BlockPos4Impl self() {
        return this;
    }

    @Override
    public Vec3i add(double x, double y, double z) {
        return this.add4(x, y, z, 0.0).asBlockPos();
    }

    @Override
    public Vec3i add(int x, int y, int z) {
        return this.add4(x, y, z, 0).asBlockPos();
    }

    @Override
    public Vec3i add(Vec3i vec) {
        return this.add4(Vec4i.asVec4i(vec)).asBlockPos();
    }

    @Override
    public Vec3i subtract(Vec3i vec) {
        return this.subtract4(Vec4i.asVec4i(vec)).asBlockPos();
    }

    @Override
    public Vec3i multiply(int scale) {
        return this.multiply4(scale).asBlockPos();
    }

    @Override
    public Vec3i offset(Direction direction, int distance) {
        return this.offset4(Direction4.asDirection4(direction), distance).asBlockPos();
    }

    @Override
    public Vec3i offset(Direction.Axis axis, int distance) {
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
