package com.gmail.inayakitorikhurram.fdmc.mixin.neighourupdaters;


import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import net.minecraft.util.math.Direction;
import net.minecraft.world.block.NeighborUpdater;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NeighborUpdater.class)
public interface NeighborUpdaterMixin {
    @Shadow @Final
    Direction[] UPDATE_ORDER = new Direction[]{
            Direction4Constants.WEST  ,
            Direction4Constants.EAST  ,
            Direction4Constants.DOWN  ,
            Direction4Constants.UP    ,
            Direction4Constants.NORTH ,
            Direction4Constants.SOUTH ,
            Direction4Constants.KATA  ,
            Direction4Constants.ANA
    };

}


