package com.gmail.inayakitorikhurram.fdmc.mixin.entity.ai.goal;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.util.MixinUtil;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

//common occurences
@Mixin(targets = {
        "net.minecraft.entity.ai.goal.FollowParentGoal",
        "net.minecraft.entity.passive.PandaEntity$PickUpFoodGoal",
        "net.minecraft.entity.ai.goal.FollowGroupLeaderGoal",
        "net.minecraft.entity.passive.DolphinEntity$PlayWithItemsGoal",
        "net.minecraft.entity.passive.FoxEntity$PickupItemGoal",
})
class GeneralCanStartDDDGoalMixin {
    @WrapOperation(method = "canStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"))
    private Box fdmc$expandBox(Box instance, double x, double y, double z, Operation<Box> original){
        return MixinUtil.expandBoundingBoxWrap(instance, x, y, z, original);
    }
}

@Mixin(targets = {
        "net.minecraft.entity.passive.PandaEntity$PickUpFoodGoal",
        "net.minecraft.entity.passive.DolphinEntity$PlayWithItemsGoal",
        "net.minecraft.entity.passive.FoxEntity$PickupItemGoal",
})
class GeneralStartDDDGoalMixin {
    @WrapOperation(method = "start", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"))
    private Box fdmc$expandBox(Box instance, double x, double y, double z, Operation<Box> original){
        return MixinUtil.expandBoundingBoxWrap(instance, x, y, z, original);
    }
}

@Mixin(targets = {
        "net.minecraft.entity.passive.DolphinEntity$PlayWithItemsGoal",
        "net.minecraft.entity.passive.FoxEntity$PickupItemGoal",
})
class GeneralTickDDDGoalMixin {
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"))
    private Box fdmc$expandBox(Box instance, double x, double y, double z, Operation<Box> original){
        return MixinUtil.expandBoundingBoxWrap(instance, x, y, z, original);
    }
}


@Mixin(targets = {
        "net.minecraft.entity.ai.goal.ChaseBoatGoal",
        "net.minecraft.entity.ai.goal.FollowMobGoal"
})
class GeneralCanStartDGoalMixin{
    @WrapOperation(method = "canStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(D)Lnet/minecraft/util/math/Box;"))
    private Box fdmc$expandBox(Box instance, double value, Operation<Box> original){
        return MixinUtil.expandBoundingBoxWrap(instance, value, original);
    }
}

@Mixin(targets = {
        "net.minecraft.entity.ai.goal.ChaseBoatGoal"
})
class GeneralStartDGoalMixin{
    @WrapOperation(method = "start", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(D)Lnet/minecraft/util/math/Box;"))
    private Box fdmc$expandBox(Box instance, double value, Operation<Box> original){
        return MixinUtil.expandBoundingBoxWrap(instance, value, original);
    }
}


//All of the rest goals in the goals package
@Mixin(ActiveTargetGoal.class)
class ActiveTargetGoalMixin{
    @WrapOperation(method = "getSearchBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"))
    private Box fdmc$expandBox(Box instance, double x, double y, double z, Operation<Box> original){
        return MixinUtil.expandBoundingBoxWrap(instance, x, y, z, original);
    }
}

@Mixin(AnimalMateGoal.class)
class AnimalMateGoalMixin{
    @WrapOperation(method = "findMate", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(D)Lnet/minecraft/util/math/Box;"))
    private Box fdmc$expandBox(Box instance, double value, Operation<Box> original){
        return MixinUtil.expandBoundingBoxWrap(instance, value, original);
    }
}

@Mixin(EscapeSunlightGoal.class)
class EscapeSunlightGoalMixin {
    @WrapOperation(method = "locateShadedPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I"))
    private int fdmc$expandBox(Random random, int spread, Operation<Integer> original){
        return MixinUtil.modifyRandomBlockPosWrap(random, spread, original);
    }
}

@Mixin(PounceAtTargetGoal.class)
class PounceAtTargetGoalMixin{

    @Shadow
    @Final
    private MobEntity mob;

    @WrapMethod(method = "canStart")
    private boolean fdmc$expandBox(Operation<Boolean> original){
        // will instantly return false if not in the same slice
        LivingEntity target = this.mob.getTarget();
        return target != null &&
                new Vec4d(this.mob.pos).w == new Vec4d(target.pos).w &&
                original.call();
    }
}

@Mixin(targets = "net.minecraft.entity.mob.VexEntity$ChargeTargetGoal")
class VexEntity$ChargeTargetGoalMixin{

    @Shadow
    @Final
    VexEntity field_7412; // VexEntity.this

    @WrapMethod(method = "canStart")
    private boolean fdmc$expandBox(Operation<Boolean> original){
        // will instantly return false if not in the same slice
        LivingEntity target = field_7412.getTarget();
        return target != null &&
                new Vec4d(field_7412.pos).w == new Vec4d(target.pos).w &&
                original.call();
    }
}

@Mixin(CreeperIgniteGoal.class)
class CreeperIgniteGoalMixin{

    @Shadow
    private @Nullable LivingEntity target;


    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/CreeperEntity;setFuseSpeed(I)V", ordinal = 3))
    private void fdmc$modifyFuseSpeedOutOfSlice(CreeperEntity creeper, int fuseSpeed, Operation<Void> original){
        assert target != null;
        if(new Vec4d(creeper.pos).w == new Vec4d(target.pos).w){
            original.call(creeper, fuseSpeed);
        } else {
            original.call(creeper, -1);
        }
    }
}


@Mixin(UniversalAngerGoal.class)
class UniversalAngerGoalMixin{
    @WrapOperation(method = "getOthersInRange", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(DDD)Lnet/minecraft/util/math/Box;"))
    private Box fdmc$expandBox(Box instance, double x, double y, double z, Operation<Box> original){
        return MixinUtil.expandBoundingBoxWrap(instance, x, y, z, original);
    }
}

// other private goals

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

@Mixin(targets = "net.minecraft.entity.mob.DrownedEntity$WanderAroundOnSurfaceGoal")
class WanderAroundOnSurfaceGoalMixin {
    @WrapOperation(method = "getWanderTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I"))
    private int fdmc$expandBox(Random random, int spread, Operation<Integer> original){
        return MixinUtil.modifyRandomBlockPosWrap(random, spread, original);
    }
}

@Mixin(targets = {
        //"net.minecraft.entity.mob.EndermanEntity$PickUpBlockGoal", TODO this is broken bc raycasts
        "net.minecraft.entity.mob.EndermanEntity$PlaceBlockGoal",
})
class EndermanEntity$PickupPlaceBlockGoalMixin {
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/EndermanEntity;getX()D"))
    private double fdmc$randomBlockPos(EndermanEntity instance, Operation<Double> original, @Local Random random){
        return original.call(instance) + FDMCMath.getOffsetX(Math.round(-1.4 + random.nextDouble() * 2.8));
    }
}

//TODO enderman pick up
//TODO panda {@link net.minecraft.entity.passive.PandaEntity.PandaMateGoal}::isBambooClose
///TODO silverfish {@link net.minecraft.entity.mob.SilverfishEntity.CallForHelpGoal}
//TODO silverfish {@link net.minecraft.entity.mob.PhantomEntity} movement