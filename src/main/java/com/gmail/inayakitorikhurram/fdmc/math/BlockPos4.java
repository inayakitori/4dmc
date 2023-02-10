package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public class BlockPos4 extends BlockPos implements Vec4i<BlockPos4> {
    public static final BlockPos4 ORIGIN;
    private int w;

    //util for doing some calculations before super constructor call
    private static int convertX(Vec3i vec3i) {
        int x = vec3i.getX();
        if (vec3i instanceof Vec4i<?>) {
            return x;
        }
        int w = (int)(Math.floor(0.5 + (x + 0d)/ FDMCConstants.STEP_DISTANCE));
        return x - w * FDMCConstants.STEP_DISTANCE;
    }

    public BlockPos4(int x, int y, int z, int w) {
        super(x, y, z);
        this.w = w;
    }

    public BlockPos4(double x, double y, double z, double w) {
        this(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z), MathHelper.floor(w));
    }

    public BlockPos4(Vec4d pos) {
        this(pos.x, pos.y, pos.z, pos.w);
    }

    public BlockPos4(Position4<Integer> pos) {
        this(pos.getX(), pos.getY(), pos.getZ(), pos.getW());
    }

    public BlockPos4(Vec4i<?> pos) {
        this(pos.getX(), pos.getY(), pos.getZ(), pos.getW());
    }

    public BlockPos4(Vec3i vec3i) {
        super(convertX(vec3i), vec3i.getY(), vec3i.getZ());
        if (vec3i instanceof Vec4i<?>) {
            this.w = ((Vec4i<?>) vec3i).getW();
        } else {
            this.w = (int)(Math.floor(0.5 + (vec3i.getX() + 0d)/ FDMCConstants.STEP_DISTANCE));
        }
    }

    @Override
    public BlockPos4 newInstance(int x, int y, int z, int w) {
        return new BlockPos4(x, y, z, w);
    }

    @Override
    public BlockPos toPos3() {
        return new BlockPos(Vec4i.super.toPos3());
    }

    @Override
    public int getW() {
        return w;
    }

    @Override
    public BlockPos4 getZeroInstance() {
        return ORIGIN;
    }

    @Override
    public BlockPos4 self() {
        return this;
    }

    static {
        ORIGIN = new BlockPos4(0, 0, 0, 0);
    }
}
