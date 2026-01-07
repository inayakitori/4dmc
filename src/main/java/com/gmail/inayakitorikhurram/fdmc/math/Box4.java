package com.gmail.inayakitorikhurram.fdmc.math;

import net.minecraft.util.math.*;
import org.apache.commons.lang3.NotImplementedException;
import org.joml.Vector3f;
import org.spongepowered.include.com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;

public class Box4 extends Box {
    public final double minW;
    public final double maxW;

    public Box4(double x1, double y1, double z1, double w1, double x2, double y2, double z2, double w2) {
        super(x1, y1, z1, x2, y2, z2);
        this.minW = w1;
        this.maxW = w2;
    }

    public Box4(Box box3, double w1, double w2) {
        super(box3.minX, box3.minY, box3.minZ, box3.maxX, box3.maxY, box3.maxZ);
        this.minW = w1;
        this.maxW = w2;
    }

    public static Box4 converted(Box box){
        Vec4d min = Vec4d.of(box.getMinPos());
        Vec4d max = Vec4d.of(box.getMaxPos()).offset(Direction4Constants.ANA4, 0.99f);
        return new Box4(min, max);
    }

    /**
     * For ease with working with situations that only check box3 values, will shift those values to be in the appropriate w range
     */
    public Box getSlice(int w){
        Vec3d min = getMinPos4().withAxis(Direction4Constants.Axis4Constants.W, w).toPos3();
        Vec3d max = getMaxPos4().withAxis(Direction4Constants.Axis4Constants.W, w).toPos3();
        return new Box(min, max);
    }

    public ImmutableList<Box> slices(){
        int minW = MathHelper.floor(this.minW);
        int maxW = MathHelper.ceil(this.maxW);
        //FDMCConstants.LOGGER.info("checking box4 slices between {} <= w < {}", minW, maxW);
        ImmutableList.Builder<Box> list = ImmutableList.builder();
        for (int w = minW; w < maxW; w++) {
            // This function only looks at the
            list.add(this.getSlice(w));
        }
        return list.build();
    }

    public Box4(BlockPos4 pos4) {
        this(pos4.getX4(), pos4.getY4(), pos4.getZ4(), pos4.getW4(), pos4.getX4() + 1, pos4.getY4() + 1, pos4.getZ4() + 1, pos4.getW4() + 1);
    }

    public Box4(Vec4d pos1, Vec4d pos2) {
        this(pos1.x4, pos1.y, pos1.z, pos1.w, pos2.x4, pos2.y, pos2.z, pos2.w);
    }

    public Box withMinW(double minWNew) {
        return new Box4(minX, minY, minZ, minWNew, maxX, maxY, maxZ, maxW);
    }

    public Box withMaxW(double maxWNew) {
        return new Box4(minX, minY, minZ, minW, maxX, maxY, maxZ, maxWNew);
    }

    @Override
    public double getMin(Direction.Axis axis) {
        if(axis == Direction4Constants.Axis4Constants.W){
            return minW;
        } else{
            return super.getMin(axis);
        }
    }

    @Override
    public double getMax(Direction.Axis axis) {
        if(axis == Direction4Constants.Axis4Constants.W){
            return minW;
        } else{
            return super.getMin(axis);
        }
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        int i = super.hashCode();
        i = 31 * i + Double.hashCode(minW);
        i = 31 * i + Double.hashCode(maxW);
        return i;
    }

    public Box4 shrink(Vec4d scale) {
        return this.shrink(scale.x4, scale.y, scale.z, scale.w);
    }

    public Box4 shrink(double x, double y, double z, double w) {
        Box box3 = super.shrink(x, y, z);
        double minW = this.minW;
        double maxW = this.maxW;
        if(w < 0.0){
            minW -=w;
        } else {
            maxW -=w;
        }
        return new Box4(box3, minW, maxW);
    }

    @Override
    public Box shrink(double x, double y, double z) {
        double[] xw = FDMCMath.splitX3(x);
        return this.shrink(xw[0], y, z, xw[1]);
    }

    public Box4 stretch(Vec4d scale) {
        return this.stretch(scale.x4, scale.y, scale.z, scale.w);
    }

    public Box4 stretch(double x, double y, double z, double w) {
        return new Box4(super.shrink(x, y, z), this.minW - w, this.maxW + w);
    }

    @Override
    public Box stretch(double x, double y, double z) {
        double[] xw = FDMCMath.splitX3(x);
        return this.stretch(xw[0], y, z, xw[1]);
    }

    public Box4 expand(double x, double y, double z, double w) {
        return new Box4(super.expand(x, y, z), minW - w, maxW + w);
    }

    public Box4 expand(double s, double w) {
        return new Box4(super.expand(s), minW - w, maxW + w);
    }

    @Override
    public Box4 intersection(Box box) {
        if(box instanceof Box4 box4){
            return new Box4(super.intersection(box), Math.max(minW, box4.minW), Math.min(this.maxW, box4.maxW));
        } else {
            return intersection(Box4.converted(box));
        }
    }

    @Override
    public Box4 union(Box box) {
        if(box instanceof Box4 box4){
            return new Box4(super.union(box), Math.min(minW, box4.minW), Math.max(this.maxW, box4.maxW));
        } else {
            return union(Box4.converted(box));
        }
    }

    public Box4 offset(double x, double y, double z, double w) {
        return new Box4(super.offset(x, y, z), minW + w, maxW + w);
    }

    @Override
    public Box4 offset(BlockPos blockPos) {
        BlockPos4<?, ?> pos4 = BlockPos4.of(blockPos);
        return offset(pos4.getX4(), pos4.getY4(), pos4.getZ4(), pos4.getW4());
    }

    @Override
    public Box4 offset(Vec3d vec) {
        Vec4d vec4 = Vec4d.of(vec);
        return offset(vec4.x4, vec4.y, vec4.z, vec4.w);
    }

    @Override
    public Box4 offset(Vector3f offset) {
        throw new NotImplementedException("don't.");
    }

    @Override
    public boolean intersects(Box box) {
        if(box instanceof Box4 box4){
            return intersects(box4.minX, box4.minY, box4.minZ, box4.minW, box4.maxX, box4.maxY, box4.maxZ, box4.maxW);
        } else {
            return intersects(Box4.converted(box));
        }
    }

    public boolean intersects(double minX, double minY, double minZ, double minW, double maxX, double maxY, double maxZ, double maxW) {
        return super.intersects(minX, minY, minZ, maxX, maxY, maxZ) && this.minW < minW && this.maxW > maxW;
    }

    @Override
    public boolean intersects(Vec3d pos1, Vec3d pos2) {
        Vec4d pos14 = Vec4d.of(pos1);
        Vec4d pos24 = Vec4d.of(pos2);
        return this.intersects(
                Math.min(pos14.x4, pos24.x4),
                Math.min(pos14.y, pos24.y),
                Math.min(pos14.z, pos24.z),
                Math.max(pos14.x4, pos24.x4),
                Math.max(pos14.y, pos24.y),
                Math.max(pos14.z, pos24.z)
        );
    }

    @Override
    public boolean contains(Vec3d pos) {
        Vec4d vec4 = Vec4d.of(pos);
        return contains(vec4.x4, vec4.y, vec4.z, vec4.w);
    }

    public boolean contains(double x, double y, double z, double w) {
        return super.contains(x, y, z) && w >= minW && w < maxW;
    }

    @Override
    public double getAverageSideLength() {
        return (getLengthX() + getLengthY() + getLengthZ() + getLengthW()) / 4;
    }

    public double getLengthW() {
        return this.maxW - this.minW;
    }

    public Box contract(double x, double y, double z, double w) {
        return expand(-x, -y, -z, -w);
    }

    @Override
    public Box contract(double value) {
        return expand(-value);
    }

    @Override
    public Optional<Vec3d> raycast(Vec3d from, Vec3d to) {
        throw new NotImplementedException("don't.");
    }

    @Override
    public boolean collides(Vec3d vec3d, List<Box> boundingBoxes) {
        return super.collides(vec3d, boundingBoxes);
    }


    //could use super but so much easier to just redo it
    @Override
    public double squaredMagnitude(Vec3d pos) {
        Vec4d pos4 = Vec4d.of(pos);
        double dx = Math.max(Math.max(this.minX - pos4.x4, pos4.x4 - this.maxX), 0.0);
        double dy = Math.max(Math.max(this.minY - pos4.y, pos4.y - this.maxY), 0.0);
        double dz = Math.max(Math.max(this.minZ - pos4.z, pos4.z - this.maxZ), 0.0);
        double dw = Math.max(Math.max(this.minW - pos4.w, pos4.w - this.maxW), 0.0);
        return dx*dx + dy*dy + dz*dz + dw*dw;
    }

    @Override
    public String toString() {
        return "AABB4[" + this.minX + ", " + this.minY + ", " + this.minZ + ", " + this.minW + "] -> [" + this.maxX + ", " + this.maxY + ", " + this.maxZ + ", " + this.maxW + "]";
    }

    @Override
    public boolean isNaN() {
        return super.isNaN() || Double.isNaN(minW) || Double.isNaN(maxW);
    }

    public Vec4d getCenter4() {
        return new Vec4d(
                MathHelper.lerp(0.5, this.minX, this.maxX),
                MathHelper.lerp(0.5, this.minY, this.maxY),
                MathHelper.lerp(0.5, this.minZ, this.maxZ),
                MathHelper.lerp(0.5, this.minW, this.maxW));
    }

    public Vec4d getHorizontalCenter4() {
        return new Vec4d(
                MathHelper.lerp(0.5, this.minX, this.maxX),
                this.minY,
                MathHelper.lerp(0.5, this.minZ, this.maxZ),
                MathHelper.lerp(0.5, this.minW, this.maxW));
    }

    public Vec4d getMinPos4() {
        return new Vec4d(
                this.minX,
                this.minY,
                this.minZ,
                this.minW);
    }

    public Vec4d getMaxPos4() {
        return new Vec4d(
                this.maxX,
                this.maxY,
                this.maxZ,
                this.maxW);
    }
}
