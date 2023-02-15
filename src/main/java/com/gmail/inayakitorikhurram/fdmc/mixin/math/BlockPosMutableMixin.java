package com.gmail.inayakitorikhurram.fdmc.mixin.math;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4i;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(BlockPos.Mutable.class)
public abstract class BlockPosMutableMixin extends BlockPos implements BlockPos4.Mutable4, BlockPos4.BlockPos4Impl, Vec4i.DirectWAccess {

    @Shadow public abstract Mutable setZ(int i);

    private BlockPosMutableMixin(int x, int y, int z) {
        super(x, y, z);
    }

    @Override
    public BlockPos.Mutable setX(int x) {
        int w = (int)(Math.floor(0.5 + (x + 0d)/ FDMCConstants.STEP_DISTANCE));
        this.setW4(w);
        super.setX(x - w * FDMCConstants.STEP_DISTANCE);
        return this.asBlockPosMutable();
    }

    @Override
    public BlockPos.Mutable setW(int w) {
        this.directAddW(w);
        return this.asBlockPosMutable();
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
    public Mutable4 setX4(int x) {
        super.setX(x);
        return this;
    }

    @Override
    public Mutable4 setY4(int y) {
        this.setY(y);
        return this;
    }

    @Override
    public Mutable4 setZ4(int z) {
        this.setZ(z);
        return this;
    }

    @Override
    public Mutable4 setW4(int w) {
        this.directSetW(w);
        return this;
    }

    @Override
    public Mutable4 set4(int x, int y, int z, int w) {
        return this.setX4(x).setY4(y).setZ4(z).setW4(w);
    }

    @Override
    public Mutable4 set4(double x, double y, double z, double w) {
        return set4(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z), MathHelper.floor(w));
    }

    @Override
    public Mutable4 set4(Vec4i<?, ?> pos) {
        return this.set4(pos.getX4(), pos.getY4(), pos.getZ4(), pos.getW4());
    }

    @Override
    public Mutable4 set4(Vec4i<?, ?> pos, Direction4 direction) {
        return this.set4(pos.getX4() + direction.getOffsetX(), pos.getY4() + direction.getOffsetY(), pos.getZ4() + direction.getOffsetZ(), pos.getW4() + direction.getOffsetW());
    }

    @Override
    public Mutable4 set4(Vec4i<?, ?> pos, int x, int y, int z, int w) {
        return this.set4(pos.getX4() + x, pos.getY4() + y, pos.getZ4() + z, pos.getW4() + w);
    }

    @Override
    public Mutable4 set4(Vec4i<?, ?> vec1, Vec4i<?, ?> vec2) {
        return this.set4(vec1.getX4() + vec2.getX4(), vec1.getY4() + vec2.getY4(), vec1.getZ4() + vec2.getZ4(), vec1.getW4() + vec2.getW4());
    }

    @Override
    public Mutable4 move4(Direction4 direction) {
        return this.move4(direction, 1);
    }

    @Override
    public Mutable4 move4(Direction4 direction, int distance) {
        return this.set4(this.getX4() + direction.getOffsetX() * distance, this.getY4() + direction.getOffsetY() * distance, this.getZ4() + direction.getOffsetZ() * distance, this.getW4() + direction.getOffsetW() * distance);
    }

    @Override
    public Mutable4 move4(int dx, int dy, int dz, int dw) {
        return this.set4(this.getX4() + dx, this.getY4() + dy, this.getZ4() + dz, this.getW4() + dw);
    }

    @Override
    public Mutable4 move4(Vec4i<?, ?> vec) {
        return this.move4(vec.getX4(), vec.getY4(), vec.getZ4(), vec.getW4());
    }

    @Override
    public Mutable4 clamp4(Direction4.Axis4 axis, int min, int max) {
        return switch (axis.asEnum()) {
            case X -> this.setX4(MathHelper.clamp(this.getX4(), min, max));
            case Y -> this.setY4(MathHelper.clamp(this.getY4(), min, max));
            case Z -> this.setZ4(MathHelper.clamp(this.getZ4(), min, max));
            case W -> this.setW4(MathHelper.clamp(this.getW4(), min, max));
        };
    }

    @Override
    public Mutable set(Vec3i pos) {
        return this.set4(Vec4i.asVec4i(pos)).asBlockPosMutable();
    }
    /*
    @Override
    public Mutable set(AxisCycleDirection axis, int x, int y, int z) {
        // TODO: AxisCycleDirection4?
    }
     */

    @Override
    public Mutable set(Vec3i pos, Direction direction) {
        return this.set(pos, direction.getVector());
    }
    /*
    @Override
    public Mutable set(Vec3i pos, int x, int y, int z) {
        // TODO: Override when x and w are properly separated
    }
     */
    @Override
    public Mutable set(Vec3i vec1, Vec3i vec2) {
        return this.set4(Vec4i.asVec4i(vec1), Vec4i.asVec4i(vec2)).asBlockPosMutable();
    }


    @Override
    public Mutable move(Direction direction, int distance) {
        if (distance == 0) {
            return this.asBlockPosMutable();
        }
        Direction4 direction4 = Direction4.asDirection4(direction);
        return this.move4(direction4.getOffsetX4() * distance, direction4.getOffsetY4() * distance, direction4.getOffsetZ4() * distance, direction4.getOffsetW4() * distance).asBlockPosMutable();
    }

    @Override
    public Mutable move(Vec3i vec) {
        return this.set4(this, Vec4i.asVec4i(vec)).asBlockPosMutable();
    }

    @Override
    public Mutable clamp(Direction.Axis axis, int min, int max) {
        return this.clamp4(Direction4.Axis4.asAxis4(axis), min, max).asBlockPosMutable();
    }
    // TODO: toImmutable mixin then x/w are separated.
}
