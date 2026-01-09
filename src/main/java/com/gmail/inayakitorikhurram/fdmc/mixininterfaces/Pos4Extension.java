package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

/**
 * Adds X4 and W coordinates to something that already has X, Y and Z fields
 */
public interface Pos4Extension {
	double getX4();
	double getW();
	double getX4(double currentX4);
	double getW(double currentW);

	void setX4(double x4);
	void setW(double w);
}
