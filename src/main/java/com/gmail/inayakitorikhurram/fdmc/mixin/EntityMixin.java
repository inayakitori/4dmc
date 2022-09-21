package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.Nameable;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements Nameable, EntityLike, CommandOutput, CanStep {

    int stepDirection;
    boolean isStepping;

    @Shadow
    private Vec3d velocity;
    public Entity getEntity(){
        return (Entity) (Object) this;
    }

    @Shadow
    public abstract boolean isInvulnerableTo(DamageSource damageSource);

    @Inject(method = "isInvulnerableTo(Lnet/minecraft/entity/damage/DamageSource;)Z", at = @At("RETURN"), cancellable = true)
    public void afterIsInvulnerableTo(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir){
        if(isStepping && damageSource == DamageSource.IN_WALL) cir.setReturnValue(true);
    }

    //if player is stepping, don't allow movement
    @Inject(method = "getVelocity", at = @At("RETURN"), cancellable = true)
    public void modifiedGetVelocity(CallbackInfoReturnable<Vec3d> cir){
        if(((CanStep)this).isStepping()) {
            cir.setReturnValue(Vec3d.ZERO);
        }
    }

    @Override
    public int getStepDirection() {
        return stepDirection;
    }

    @Override
    public void setStepDirection(int stepDirection) {
        this.stepDirection = stepDirection;
    }

    @Override
    public boolean isStepping() {
        return isStepping;
    }

    @Override
    public void setStepping(boolean isStepping) {
        this.isStepping = isStepping && stepDirection != 0;
    }

    @Override
    public boolean canStep(int stepDirection) {
        return !isStepping || this.stepDirection != stepDirection;
    }
}
