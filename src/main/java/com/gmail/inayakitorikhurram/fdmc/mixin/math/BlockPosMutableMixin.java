package com.gmail.inayakitorikhurram.fdmc.mixin.math;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4Impl;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(BlockPos.Mutable.class)
public abstract class BlockPosMutableMixin extends BlockPos implements BlockPos4.Mutable4<BlockPos4Impl> {
    private int w = 0;

    public BlockPosMutableMixin(int x, int y, int z) {
        super(x, y, z);
    }

    public int getW() {
        return w;
    }

    public BlockPos.Mutable setW(int w) {
       this.w = w;
       return (BlockPos.Mutable)(Object) this;
    }

    // TODO: implement like a million methods

    public BlockPos4Impl getZeroInstance() {
        return BlockPos4Impl.ORIGIN;
    }

    public BlockPos4Impl self() {
        return newInstance(this.getX(), this.getY(), this.getZ(), this.getW());
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
