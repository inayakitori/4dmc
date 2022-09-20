package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements CanStep {

    int stepDirection;
    boolean isStepping;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }


    public PlayerEntity getPlayerEntity(){
        return (PlayerEntity) (Object) this;
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
        this.isStepping = isStepping;
    }

    @Override
    public boolean canStep(int stepDirection) {
        return !isStepping || this.stepDirection != stepDirection;
    }
}
