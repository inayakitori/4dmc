package com.gmail.inayakitorikhurram.fdmc.mixin.entity.player;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Box4;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanPlaceW;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
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

    @Unique
    final boolean isSpaceAroundPlayerEmpty(double offsetX, double offsetZ, double offsetW, double stepHeight) {
        Box4 box = Box4.converted(this.getBoundingBox());
        return this.getEntityWorld().isSpaceEmpty(
            this,
            new Box4(
                box.minX + 1.0E-7 + offsetX, box.minY - stepHeight - 1.0E-7, box.minZ + 1.0E-7 + offsetZ, box.minW + 1.0E-7 + offsetW,
                box.maxX - 1.0E-7 + offsetX, box.minY, box.maxZ - 1.0E-7 + offsetZ, box.maxW - 1.0E-7 + offsetW
            )
        );
    }

    @Redirect(method = "adjustMovementForSneaking", at = @At(value = "NEW", target = "(DDD)Lnet/minecraft/util/math/Vec3d;"))
    Vec3d sneaking4D(double x3, double y, double z, @Local(argsOnly = true) Vec3d movement3) {
        float stepHeight = this.getStepHeight();
        Vec4d movement = Vec4d.of(movement3);

        double x = movement.x4;
        // double z = z; // Already calculated by vanilla but for 3D
        double w = movement.w;
        double dx = Math.signum(x) * 0.05;
        double dz = Math.signum(z) * 0.05;
        double dw = Math.signum(z) * 0.05;

        while (x != 0.0 && this.isSpaceAroundPlayerEmpty(x, 0.0, 0.0, stepHeight)) {
            if (Math.abs(x) <= 0.05) {
                x = 0.0;
                break;
            };
            x -= dx;
        }
        while (z != 0.0 && this.isSpaceAroundPlayerEmpty(0.0, z, 0.0, stepHeight)) {
            if (Math.abs(z) <= 0.05) {
                z = 0.0;
                break;
            }
            z -= dz;
        }
        while (w != 0.0 && this.isSpaceAroundPlayerEmpty(0.0, 0.0, w, stepHeight)) {
            if (Math.abs(w) <= 0.05) {
                w = 0.0;
                break;
            }
            w -= dw;
        }
        while (x != 0.0 && z != 0.0 && w != 0.0 && this.isSpaceAroundPlayerEmpty(x, z, w, stepHeight)) {
            if (Math.abs(x) <= 0.05) {
                x = 0.0;
            } else {
                x -= dx;
            }
            if (Math.abs(z) <= 0.05) {
                z = 0.0;
            } else {
                z -= dz;
            }
            if (Math.abs(w) <= 0.05) {
                w = 0.0;
            } else {
                w -= dw;
            }
        }
        return new Vec4d(x, y, z, w);
    }
}
