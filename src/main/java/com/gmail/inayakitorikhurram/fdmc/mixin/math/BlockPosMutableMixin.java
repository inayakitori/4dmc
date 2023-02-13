package com.gmail.inayakitorikhurram.fdmc.mixin.math;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4Impl;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(BlockPos.Mutable.class)
public abstract class BlockPosMutableMixin extends BlockPos implements BlockPos4.Mutable4<BlockPos4Impl> {
    @Shadow public abstract Mutable setX(int i);

    @Shadow public abstract Mutable setZ(int i);

    private int w = 0;

    public BlockPosMutableMixin(int x, int y, int z) {
        super(x, y, z);
    }

    @Override
    public int getW() {
        return w;
    }

    @Override
    public BlockPos.Mutable setW(int w) {
       this.w = w;
       return (BlockPos.Mutable)(Object) this;
    }

    @Override
    public BlockPos4Impl getZeroInstance() {
        return BlockPos4Impl.ORIGIN;
    }

    @Override
    public BlockPos4Impl self() {
        return newInstance(this.getX(), this.getY(), this.getZ(), this.getW());
    }

    @Override
    public Mutable4<?> setX4(int x) {
        this.setX(x);
        return this;
    }

    @Override
    public Mutable4<?> setY4(int y) {
        this.setY(y);
        return this;
    }

    @Override
    public Mutable4<?> setZ4(int z) {
        this.setZ(z);
        return this;
    }

    @Override
    public Mutable4<?> setW4(int w) {
        this.w = w;
        return this;
    }

    @Override
    public Mutable4<?> set4(int x, int y, int z, int w) {
        return this.setX4(x).setY4(y).setZ4(z).setW4(w);
    }

    @Override
    public Mutable4<?> set4(double x, double y, double z, double w) {
        return set4(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z), MathHelper.floor(w));
    }

    @Override
    public Mutable4<?> set4(Vec4i<?, ?> pos) {
        return this.set4(pos.getX(), pos.getY(), pos.getZ(), pos.getW());
    }

    @Override
    public Mutable4<?> set4(Vec4i<?, ?> pos, Direction4 direction) {
        return this.set4(pos.getX() + direction.getOffsetX(), pos.getY() + direction.getOffsetY(), pos.getZ() + direction.getOffsetZ(), pos.getW() + direction.getOffsetW());
    }

    @Override
    public Mutable4<?> set4(Vec4i<?, ?> pos, int x, int y, int z, int w) {
        return this.set4(pos.getX() + x, pos.getY() + y, pos.getZ() + z, pos.getW() + w);
    }

    @Override
    public Mutable4<?> set4(Vec4i<?, ?> vec1, Vec4i<?, ?> vec2) {
        return this.set4(vec1.getX() + vec2.getX(), vec1.getY() + vec2.getY(), vec1.getZ() + vec2.getZ(), vec1.getW() + vec2.getW());
    }

    @Override
    public Mutable4<?> move4(Direction4 direction) {
        return this.move4(direction, 1);
    }

    @Override
    public Mutable4<?> move4(Direction4 direction, int distance) {
        return this.set4(this.getX() + direction.getOffsetX() * distance, this.getY() + direction.getOffsetY() * distance, this.getZ() + direction.getOffsetZ() * distance, this.getW() + direction.getOffsetW() * distance);
    }

    @Override
    public Mutable4<?> move4(int dx, int dy, int dz, int dw) {
        return this.set4(this.getX() + dx, this.getY() + dy, this.getZ() + dz, this.getW() + dw);
    }

    @Override
    public Mutable4<?> move4(Vec4i<?, ?> vec) {
        return this.move4(vec.getX(), vec.getY(), vec.getZ(), vec.getW());
    }

    @Override
    public Mutable4<?> clamp4(Direction4.Axis4 axis, int min, int max) {
        return switch (axis) {
            case X -> this.setX4(MathHelper.clamp(this.getX(), min, max));
            case Y -> this.setY4(MathHelper.clamp(this.getY(), min, max));
            case Z -> this.setZ4(MathHelper.clamp(this.getZ(), min, max));
            case W -> this.setW4(MathHelper.clamp(this.getW(), min, max));
        };
    }

    @Inject(method = "set(Lnet/minecraft/util/math/Vec3i;)Lnet/minecraft/util/math/BlockPos$Mutable;", at = @At("HEAD"))
    private void setMixin(Vec3i pos, CallbackInfoReturnable<Mutable> cir) {
        if (pos instanceof Vec4i<?, ?>) {
            this.setW(((Vec4i<?, ?>) pos).getW());
        }
    }

    @Inject(method = "set(Lnet/minecraft/util/math/Vec3i;Lnet/minecraft/util/math/Direction;)Lnet/minecraft/util/math/BlockPos$Mutable;", at = @At("HEAD"))
    private void setMixin(Vec3i pos, Direction direction, CallbackInfoReturnable<Mutable> cir) {
        // TODO: Direction4 compat
        if (pos instanceof Vec4i<?, ?>) {
            this.setW(((Vec4i<?, ?>) pos).getW());
        }
    }

    @Inject(method = "set(Lnet/minecraft/util/math/Vec3i;III)Lnet/minecraft/util/math/BlockPos$Mutable;", at = @At("HEAD"))
    private void setMixin(Vec3i pos, int x, int y, int z, CallbackInfoReturnable<Mutable> cir) {
        if (pos instanceof Vec4i<?, ?>) {
            this.setW(((Vec4i<?, ?>) pos).getW());
        }
    }

    @Inject(method = "set(Lnet/minecraft/util/math/Vec3i;Lnet/minecraft/util/math/Vec3i;)Lnet/minecraft/util/math/BlockPos$Mutable;", at = @At("HEAD"))
    private void setMixin(Vec3i vec1, Vec3i vec2, CallbackInfoReturnable<Mutable> cir) {
        int val = 0;
        if (vec1 instanceof Vec4i<?, ?>) {
            val += ((Vec4i<?, ?>) vec1).getW();
        }
        if (vec2 instanceof Vec4i<?, ?>) {
            val += ((Vec4i<?, ?>) vec2).getW();
        }
        this.setW(val);
    }
    /* TODO: Direction4 compat
    @Inject(method = "move(Lnet/minecraft/util/math/Direction;I)Lnet/minecraft/util/math/BlockPos$Mutable;", at = @At("HEAD"))
    private void moveMixin(Direction direction, int distance, CallbackInfoReturnable<Mutable> cir) {

    }
     */

    @Inject(method = "move(Lnet/minecraft/util/math/Vec3i;)Lnet/minecraft/util/math/BlockPos$Mutable;", at = @At("HEAD"))
    private void moveMixin(Vec3i vec, CallbackInfoReturnable<Mutable> cir) {
        if (vec instanceof Vec4i<?, ?>) {
            this.setW(this.getW() + ((Vec4i<?, ?>) vec).getW());
        }
    }

    /* TODO Direction4.Axis4 compat
    @Inject(method = "clamp(Lnet/minecraft/util/math/Direction$Axis;II)Lnet/minecraft/util/math/BlockPos$Mutable;", at = @At("HEAD"))
    private void clampMixin(Direction.Axis axis, int min, int max, CallbackInfoReturnable<Mutable> cir) {

    }
     */
}
