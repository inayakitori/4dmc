package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

/**
 * Adds X4 and W coordinates to something that already has X, Y and Z fields
 */
public interface Pos4Extension {
	double getW();

	void setW(double w);

	double getW(double currentW);
}
