package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import net.minecraft.entity.mob.MobEntity;

//means the code looks the same for the multiple mixins.
// otherwise sometimes it's this.targetX and sometimes its super.targetX
public interface MoveControl4 {
    Vec4d getTargetPos();
    MobEntity getEntity();
    double getSpeedWithAttribute();
}
