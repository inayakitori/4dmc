package com.gmail.inayakitorikhurram.fdmc.mixin.server.network;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanPlaceW;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements CanPlaceW {
    public ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @WrapMethod(method = "increaseTravelMotionStats")
    private void modifiedXStat(double deltaX3, double deltaY, double deltaZ, Operation<Void> original){
        double[] deltaXW = FDMCMath.splitX3(deltaX3);
        double deltaX = deltaXW[0];
        double deltaW = deltaXW[1];
        original.call(deltaX, deltaY, deltaZ);
        int absw = Math.round((float) Math.abs(100. * deltaW));
        if(absw > 0) {
            this.increaseStat(FDMCConstants.STAT_STEP_COUNT, absw);
            this.addExhaustion(0.01f * (float)absw * 0.2f * FDMCConstants.STEP_HUNGER_MULTIPLIER);
        }
    }
}
