package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.FDMCMath;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }



    //If in 4-range, don't despawn self once certain distance away
    @Inject(
            method = "checkDespawn()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/mob/MobEntity;discard()V",
                    ordinal = 1
            ),
            cancellable = true
        )
    public void onFarDiscard(CallbackInfo ci){
        PlayerEntity player = this.world.getClosestPlayer(this, -1.0);
        double[] playerPos4 = FDMCMath.toPos4(player.getPos());
        double[] thisPos4 = FDMCMath.toPos4(this.getPos());

        double distanceSquared = thisPos4[0] * playerPos4[0] +
                                 thisPos4[1] * playerPos4[1] +
                                 thisPos4[2] * playerPos4[2] +
                                 thisPos4[3] * playerPos4[3];
        double range = this.getType().getSpawnGroup().getImmediateDespawnRange() / (FDMCConstants.FDMC_BLOCK_SCALE +0d);
        if(distanceSquared <= range * range){
            ci.cancel();

        }
    }


    //If in 4-range, don't despawn self once certain distance away
    @Inject(
            method = "checkDespawn()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/mob/MobEntity;discard()V",
                    ordinal = 2
            ),
            cancellable = true
    )
    public void onNearDiscard(CallbackInfo ci){
        PlayerEntity player = this.world.getClosestPlayer(this, -1.0);
        double[] playerPos4 = FDMCMath.toPos4(player.getPos());
        double[] thisPos4 = FDMCMath.toPos4(this.getPos());

        double distanceSquared = thisPos4[0] * playerPos4[0] +
                thisPos4[1] * playerPos4[1] +
                thisPos4[2] * playerPos4[2] +
                thisPos4[3] * playerPos4[3];
        double range = this.getType().getSpawnGroup().getDespawnStartRange() / (FDMCConstants.FDMC_BLOCK_SCALE +0d);
        if(distanceSquared <= range * range){
            ci.cancel();
        }
    }

}
