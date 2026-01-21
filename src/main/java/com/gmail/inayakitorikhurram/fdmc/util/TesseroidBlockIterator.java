package com.gmail.inayakitorikhurram.fdmc.util;

import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import net.minecraft.util.CuboidBlockIterator;

public class TesseroidBlockIterator extends CuboidBlockIterator {
	private final int startX;
	private final int startY;
	private final int startZ;
	private final int startW;

	private final int sizeX;
	private final int sizeY;
	private final int sizeZ;
	private final int sizeW;

	private final int totalSize;
	private int blocksIterated;
	private int x;
	private int y;
	private int z;
	private int w;

	public TesseroidBlockIterator(int startX, int startY, int startZ, int startW, int endX, int endY, int endZ, int endW) {
		super(startX, startY, startZ, endX, endY, endZ);
		this.startX = startX;
		this.startY = startY;
		this.startZ = startZ;
		this.startW = startW;
		this.sizeX = endX - startX + 1;
		this.sizeY = endY - startY + 1;
		this.sizeZ = endZ - startZ + 1;
		this.sizeW = endW - startW + 1;
		this.totalSize = this.sizeX * this.sizeY * this.sizeZ * this.sizeW;
	}

	public boolean step() {
		if (this.blocksIterated == this.totalSize) {
			return false;
		}

		this.x  = this.blocksIterated % this.sizeX;
		int yzw = this.blocksIterated / this.sizeX;

		this.y = yzw % this.sizeY;
		int zw = yzw / this.sizeY;

		this.z = zw % this.sizeZ;
		this.w = zw / this.sizeZ;

		++this.blocksIterated;
		return true;
	}

	@Override
	public int getX() {
		return getX4() + FDMCMath.getOffsetX(getW());
	}
	@Override
	public int getY() {
		return this.startY + this.y;
	}
	@Override
	public int getZ() {
		return this.startZ + this.z;
	}

	public int getX4() {
		return this.startX + this.x;
	}
	public int getW() {
		return this.startW + this.w;
	}

	@Override public int getEdgeCoordinatesCount() {
		int i = 0;
		if (this.x == 0 || this.x == this.sizeX - 1) {
			++i;
		}
		if (this.y == 0 || this.y == this.sizeY - 1) {
			++i;
		}
		if (this.z == 0 || this.z == this.sizeZ - 1) {
			++i;
		}
		if (this.w == 0 || this.w == this.sizeW - 1) {
			++i;
		}
		return i;
	}
}
