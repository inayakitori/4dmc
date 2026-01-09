package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;

public interface Entity4 {
	void setPosition(double x, double y, double z, double w);
	void setPos(double x, double y, double z, double w);
	void updatePosition(Vec4d position);
	void updatePositionAndAngles(Vec4d position, float yaw, float pitch);
}
