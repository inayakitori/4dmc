package com.gmail.inayakitorikhurram.fdmc.mixin.neighourupdaters;


import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import net.minecraft.util.math.Direction;
import net.minecraft.world.block.NeighborUpdater;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


@Mixin(NeighborUpdater.class)
public interface NeighborUpdaterMixin {

    @Redirect(method = "updateNeighbors", at = @At(value = "FIELD", target = "Lnet/minecraft/world/block/NeighborUpdater;UPDATE_ORDER:[Lnet/minecraft/util/math/Direction;", opcode = Opcodes.GETSTATIC))
    private static Direction[] modifyUpdateOrder(){
        return new Direction[]{
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
}


