package com.gmail.inayakitorikhurram.fdmc.client.option;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Enum;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public enum Perspective4Enum {
	// Notice that only one axis changes between each item
	XYW(Direction4Enum.Axis4Enum.X, Direction4Enum.Axis4Enum.W), // Rotation around YX
	ZYW(Direction4Enum.Axis4Enum.Z, Direction4Enum.Axis4Enum.W), // Rotation around YW
	ZYX(Direction4Enum.Axis4Enum.Z, Direction4Enum.Axis4Enum.X), // Rotation around YZ // Rotated too much and flipped @_@, happens
	WYX(Direction4Enum.Axis4Enum.W, Direction4Enum.Axis4Enum.X), // Rotation around YX
	WYZ(Direction4Enum.Axis4Enum.W, Direction4Enum.Axis4Enum.Z), // Rotation around YW
	XYZ(Direction4Enum.Axis4Enum.X, Direction4Enum.Axis4Enum.Z); // Rotation around YZ // This is vanilla!

	public final Direction4.Axis4 axisGround0;
	public final Direction4.Axis4 axisTop = Direction4Enum.Axis4Enum.Y.asAxis4();
	public final Direction4.Axis4 axisGround1;

	Perspective4Enum(Direction4Enum.Axis4Enum axisGround0, Direction4Enum.Axis4Enum axisGround1) {
		this.axisGround0 = axisGround0.asAxis4();
		this.axisGround1 = axisGround1.asAxis4();
	}

	private static final Perspective4Enum[] VALUES = Perspective4Enum.values();
	public Perspective4Enum next() {
		return VALUES[(this.ordinal() + 1) % VALUES.length];
	}
}
