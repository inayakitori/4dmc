package com.gmail.inayakitorikhurram.fdmc.mixin.entity.ai.goal;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Box4;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;


// some may consider this jank. I consider this art
@Mixin(ActiveTargetGoal.class)
class ActiveTargetGoalMixin{
    @WrapOperation(method = "getSearchBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"))
    private Box fdmc$expandBox(Box instance, double x, double y, double z, Operation<Box> original){
        return Box4.converted(original.call(instance, x, y, z)).expand(0, 0.5f + x / FDMCConstants.FOLLOW_RANGE_W_SCALE);
    }
}

@Mixin(PounceAtTargetGoal.class)
class PounceAtTargetGoalMixin{
    @Shadow
    private LivingEntity target;

    @Shadow
    @Final
    private MobEntity mob;

    @WrapMethod(method = "canStart")
    private boolean fdmc$expandBox(Operation<Boolean> original){
        // will instantly return false if not in the same slice
        LivingEntity target = this.mob.getTarget();
        return target != null &&
                Vec4d.of(this.mob.pos).w == Vec4d.of(target.pos).w &&
                original.call();
    }
}

@Mixin(AnimalMateGoal.class)
class AnimalMateGoalMixin{
    @WrapOperation(method = "findMate", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(D)Lnet/minecraft/util/math/Box;"))
    private Box fdmc$expandBox(Box instance, double value, Operation<Box> original){
        return Box4.converted(original.call(instance, value)).expand(0, 0.5f + value / FDMCConstants.FOLLOW_RANGE_W_SCALE);
    }
}

@Mixin(FollowParentGoal.class)
class FollowParentGoalMixin{
    @WrapOperation(method = "canStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"))
    private Box fdmc$expandBox(Box instance, double x, double y, double z, Operation<Box> original){
        return Box4.converted(original.call(instance, x, y, z)).expand(0, 0.5f + x / FDMCConstants.FOLLOW_RANGE_W_SCALE);
    }
}


// some may consider this jank. I consider this art
@Mixin(targets = "net.minecraft.entity.passive.BeeEntity$PollinateGoal")
class MoveToFlowerGoalMixin{
    @WrapOperation(method = "getFlower", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;iterateOutwards(Lnet/minecraft/util/math/BlockPos;III)Ljava/lang/Iterable;"))
    private Iterable<BlockPos> fdmc$expandBox(BlockPos center, int rangeX, int rangeY, int rangeZ, Operation<Iterable<BlockPos>> original){
        return BlockPos4.iterateOutwardsModification(BlockPos4.of(center),
                w -> original.call(
                        center.offset(Direction4Constants.ANA, (Integer) w),
                        rangeX, rangeY, rangeZ
                ).iterator(),
                rangeX);
    }
}
