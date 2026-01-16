package com.gmail.inayakitorikhurram.fdmc.mixin.entity;

import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.NavigateToTargetSliceFirst;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractSkeletonEntity.class)
abstract class AbstractSkeletonEntityNavMixin extends HostileEntity implements NavigateToTargetSliceFirst {

    protected AbstractSkeletonEntityNavMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean shouldNavigateToTargetSliceFirst() {
        return this.getTarget() != null;
    }

    @Override
    public double getTargetW() {
        LivingEntity target = this.getTarget();
        if(target == null) return Double.NaN;
        return FDMCMath.splitX3(this.getTarget().pos.x)[1];
    }
}

@Mixin(DrownedEntity.class)
abstract class DrownedEntityNavMixin extends HostileEntity implements NavigateToTargetSliceFirst {

    protected DrownedEntityNavMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean shouldNavigateToTargetSliceFirst() {
        return this.getTarget() != null && this.getMainHandStack().isOf(Items.TRIDENT);
    }

    @Override
    public double getTargetW() {
        LivingEntity target = this.getTarget();
        if(target == null) return Double.NaN;
        return FDMCMath.splitX3(this.getTarget().pos.x)[1];
    }
}
