package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;

public interface Entity4 {
	void setPosition(Vec4d position);
	void setPos(Vec4d newPos);
	void updatePosition(Vec4d position);
	void updatePositionAndAngles(Vec4d position, float yaw, float pitch);
}
