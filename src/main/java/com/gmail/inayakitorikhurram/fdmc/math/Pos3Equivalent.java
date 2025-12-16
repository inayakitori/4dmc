package com.gmail.inayakitorikhurram.fdmc.math;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

interface Pos3Equivalent<P3>{
    P3 toPos3();

    /**
     * @return The vector without the original w component. Good for when mixing in to separate the w component and act on it and then return the rest
     */
    P3 flatten();
}
