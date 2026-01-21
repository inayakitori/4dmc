package com.gmail.inayakitorikhurram.fdmc.mixin.block.entity;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Box4;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants.ANA;
import static com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants.KATA;

@Mixin(PistonBlockEntity.class)
public abstract class PistonBlockEntityMixin {
    @Inject(method = "offsetHeadBox", at = @At(value = "TAIL"), cancellable = true)
    private static void offsetHeadBox4(BlockPos pos, Box box, PistonBlockEntity blockEntity, CallbackInfoReturnable<Box> cir, @Local double progress){
        Direction4 facing = Direction4.asDirection4(blockEntity.getFacing());
        BlockPos4<?, ?> pos4 = BlockPos4.of(pos);
        cir.setReturnValue(
            Box4.converted(box).offset(
                pos4.getX4() + progress * (double)facing.getOffsetX4(),
                pos4.getY4() + progress * (double)facing.getOffsetY4(),
                pos4.getZ4() + progress * (double)facing.getOffsetZ4(),
                pos4.getW4() + progress * (double)facing.getOffsetW4()
            )
        );
    }

    @Redirect(method = "moveEntity", at = @At(value = "NEW", target = "(DDD)Lnet/minecraft/util/math/Vec3d;"))
    private static Vec3d fdmc$fixPistonMovement(
        double x, double y, double z,
        @Local(argsOnly = true) double distance,
        @Local(argsOnly = true, ordinal = 1) Direction movementDirection
    ){
        return Vec4d.of(Direction4.asDirection4(movementDirection).getVector4()).multiply(distance);
    }

    @WrapMethod(method = "getIntersectionSize")
    private static double anaKataDirections(Box box1, Direction direction, Box box2, Operation<Double> original){
        if (ANA.equals(direction))
            return Box4.converted(box1).maxW - Box4.converted(box2).minW;
        else if (KATA.equals(direction))
            return Box4.converted(box2).maxW - Box4.converted(box1).minW;
        else
            return original.call(box1, direction, box2);
    }

    @ModifyExpressionValue(method = "pushEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Direction;getAxis()Lnet/minecraft/util/math/Direction$Axis;"))
    private static Direction.Axis pushEntities$stopCrashByNotUsingWAxis(Direction.Axis original){
        return Direction.Axis.X;
    }

    @Redirect(method = "pushEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocity(DDD)V"))
    private static void pushEntities$setVelocity4(Entity entity, double x, double y, double z, @Local(argsOnly = true) PistonBlockEntity blockEntity){
        Direction4 direction = Direction4.asDirection4(blockEntity.getMovementDirection());
        entity.setVelocity(
            entity.getVelocity().withAxis(
                direction.getAxis(),
                direction.getVector4().getComponentAlongAxis4(direction.getAxis4())
            )
        );
    }
}
