package com.gmail.inayakitorikhurram.fdmc.mixin.entity.player;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Box4;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanPlaceW;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements CanPlaceW {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "canInteractWithBlockAt", at = @At(value = "NEW", target = "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/Box;"))
    private Box makeInteractionBox4(BlockPos pos){
        return new Box4(BlockPos4.of(pos));
    }

    @Unique
    private Direction placementDirection;

    @Override
    public void setPlacementDirection4(@Nullable Direction placementDirection4) {
        this.placementDirection = placementDirection4;
    }

    @Override
    public void setPlacementDirection4(@NotNull Optional<Direction> placementDirection4) {
        this.placementDirection = placementDirection4.orElse(null);
    }

    @Override
    public Optional<Direction> getPlacementDirection4() {
        return Optional.ofNullable(placementDirection);
    }
}
