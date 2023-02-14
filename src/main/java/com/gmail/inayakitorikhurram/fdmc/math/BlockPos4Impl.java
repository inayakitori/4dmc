package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public class BlockPos4Impl extends BlockPos implements BlockPos4<BlockPos4Impl, BlockPos> {
    public static final BlockPos4Impl ORIGIN;
    private int w;

    // util for doing some calculations before super constructor call
    private static int convertX(Vec3i vec3i) {
        int x = vec3i.getX();
        if (vec3i instanceof Vec4i<?, ?>) {
            return x;
        }
        int w = (int)(Math.floor(0.5 + (x + 0d)/ FDMCConstants.STEP_DISTANCE));
        return x - w * FDMCConstants.STEP_DISTANCE;
    }

    protected BlockPos4Impl(int x, int y, int z, int w) {
        super(x, y, z);
        this.w = w;
    }

    protected BlockPos4Impl(double x, double y, double z, double w) {
        this(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z), MathHelper.floor(w));
    }

    protected BlockPos4Impl(Vec4d pos) {
        this(pos.x, pos.y, pos.z, pos.w);
    }

    protected BlockPos4Impl(Position4<Integer> pos) {
        this(pos.getX(), pos.getY(), pos.getZ(), pos.getW());
    }

    protected BlockPos4Impl(Vec4i<?, ?> pos) {
        this(pos.getX(), pos.getY(), pos.getZ(), pos.getW());
    }

    protected BlockPos4Impl(Vec3i vec3i) {
        super(convertX(vec3i), vec3i.getY(), vec3i.getZ());
        if (vec3i instanceof Vec4i<?, ?>) {
            this.w = ((Vec4i<?, ?>) vec3i).getW();
        } else {
            this.w = (int)(Math.floor(0.5 + (vec3i.getX() + 0d)/ FDMCConstants.STEP_DISTANCE));
        }
    }

    @Override
    public BlockPos4Impl newInstance(int x, int y, int z, int w) {
        return new BlockPos4Impl(x, y, z, w);
    }

    @Override
    public BlockPos newSuperInstance(int x, int y, int z) {
        return new BlockPos(x, y, z);
    }

    //TODO eventually remove once 4D worlds are implemented
    @Override
    public int getX() {
        return super.getX() + FDMCConstants.STEP_DISTANCE * getW();
    }
    @Override
    public int getW() {
        return w;
    }

    @Override
    public BlockPos4Impl getZeroInstance() {
        return ORIGIN;
    }

    @Override
    public BlockPos4Impl self() {
        return this;
    }

    static {
        ORIGIN = new BlockPos4Impl(0, 0, 0, 0);
    }
}
