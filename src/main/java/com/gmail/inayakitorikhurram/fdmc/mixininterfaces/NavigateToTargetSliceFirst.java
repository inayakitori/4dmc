package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

/**
 * Is implemented on mobs that should first navigate to the same slice as the player before attacking them.
 * Enemies such as skeletons, Drowned + Trident and stuff
 */
public interface NavigateToTargetSliceFirst {
    boolean shouldNavigateToTargetSliceFirst();
    double getTargetW();
}
