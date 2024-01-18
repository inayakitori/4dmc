package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;

public class ChunkPos4 implements Pos3Equivalent<ChunkPos> {

    public final int x;
    public final int z;
    public final int w;
    
    public ChunkPos4(int x, int z, int w) {
        this.x = x;
        this.z = z;
        this.w = w;
    }

    public ChunkPos4(BlockPos4<?, ?> pos) {
        this.x = ChunkSectionPos.getSectionCoord(pos.getX4());
        this.z = ChunkSectionPos.getSectionCoord(pos.getZ4());
        this.w = pos.getW4();
    }

    public ChunkPos4(ChunkPos pos3) {
        int[] xw = FDMCMath.splitChunkXCoordinate(pos3.x);
        this.x = xw[0];
        this.w = xw[1];
        this.z = pos3.z;
    }

    @Override
    public ChunkPos toPos3() {
        int x = this.x + FDMCConstants.CHUNK_STEP_DISTANCE * this.w;
        int z = this.z;
        return new ChunkPos(x, z);
    }

    public static ChunkPos4 fromRegion(int x, int z, int w) {
        return new ChunkPos4(x << 5, z << 5, w);
    }

    public static ChunkPos4 fromRegionCenter(int x, int z, int w) {
        return new ChunkPos4((x << 5) + 31, (z << 5) + 31, w);
    }


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof ChunkPos4)) {
            return false;
        } else {
            ChunkPos4 ChunkPos4 = (ChunkPos4)o;
            return this.x == ChunkPos4.x && this.z == ChunkPos4.z;
        }
    }

    public int getCenterX() {
        return this.getOffsetX(8);
    }

    public int getCenterZ() {
        return this.getOffsetZ(8);
    }

    public int getStartX() {
        return ChunkSectionPos.getBlockCoord(this.x);
    }

    public int getStartZ() {
        return ChunkSectionPos.getBlockCoord(this.z);
    }

    public int getEndX() {
        return this.getOffsetX(15);
    }

    public int getEndZ() {
        return this.getOffsetZ(15);
    }

    public int getRegionX() {
        return this.x >> 5;
    }

    public int getRegionZ() {
        return this.z >> 5;
    }

    public int getRegionRelativeX() {
        return this.x & 31;
    }

    public int getRegionRelativeZ() {
        return this.z & 31;
    }

    public BlockPos4<?, ?> getBlockPos(int offsetX, int y, int offsetZ) {
        return BlockPos4.newBlockPos4(this.getOffsetX(offsetX), y, this.getOffsetZ(offsetZ), this.w);
    }

    public int getOffsetX(int offset) {
        return ChunkSectionPos.getOffsetPos(this.x, offset);
    }

    public int getOffsetZ(int offset) {
        return ChunkSectionPos.getOffsetPos(this.z, offset);
    }

    public BlockPos getCenterAtY(int y) {
        return new BlockPos(this.getCenterX(), y, this.getCenterZ());
    }

    public String toString() {
        return "[" + this.x + ", " + this.z + ", " + this.w + "]";
    }

    public BlockPos4<?, ?> getStartPos() {
        return BlockPos4.newBlockPos4(this.getStartX(), 0, this.getStartZ(), this.w);
    }

    public int getChebyshevDistance(ChunkPos4 pos) {
        return Math.max(Math.abs(this.x - pos.x), Math.max(Math.abs(this.z - pos.z), Math.abs(this.w - pos.w)));
    }
}
