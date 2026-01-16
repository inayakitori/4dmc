package com.gmail.inayakitorikhurram.fdmc.mixin.entity.ai.goal;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.util.MixinUtil;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InfestedBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//common occurences
@Mixin(targets = {
        "net.minecraft.entity.ai.goal.FollowParentGoal",
        "net.minecraft.entity.passive.PandaEntity$PickUpFoodGoal",
        "net.minecraft.entity.ai.goal.FollowGroupLeaderGoal",
        "net.minecraft.entity.passive.DolphinEntity$PlayWithItemsGoal",
        "net.minecraft.entity.passive.FoxEntity$PickupItemGoal",
        "net.minecraft.entity.mob.PhantomEntity$FindTargetGoal",
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

@Mixin(LookAtEntityGoal.class)
class LookAtEntityGoalMixin{

    @Shadow
    @Final
    protected MobEntity mob;

    @WrapOperation(method = "tick", at  = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getX()D"))
    private double fdmc$moveToSameSlice(Entity target, Operation<Double> original){
        // essentially projects to the target's slice
        return original.call(target) + FDMCMath.getOffsetX(new Vec4d(mob.pos).w - new Vec4d(target.pos).w);
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

//TODO creeper explosion raycast
@Mixin(CreeperIgniteGoal.class)
class CreeperIgniteGoalMixin{

    @Shadow
    private @Nullable LivingEntity target;

    @Shadow
    @Final
    private CreeperEntity creeper;

    @WrapMethod(method = "canStart")
    private boolean fdmc$expandBox(Operation<Boolean> original){
        // will instantly return false if not in the same slice
        LivingEntity target = this.creeper.getTarget();
        return target != null &&
                new Vec4d(creeper.pos).w == new Vec4d(target.pos).w &&
                original.call();
    }

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

@Mixin(BowAttackGoal.class)
class BowAttackGoalMixin<T extends HostileEntity>{
    @Shadow
    @Final
    private T actor;

    //can't start attacking an entity out of da slice
    @WrapOperation(method = "tick", at  = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobVisibilityCache;canSee(Lnet/minecraft/entity/Entity;)Z"))
    private boolean fdmc$moveToSameSlice(MobVisibilityCache instance, Entity target, Operation<Boolean> original){
        return new Vec4d(actor.pos).w == new Vec4d(target.pos).w && original.call(instance, target);
    }

    //also move to player if out of slice
    @Definition(id = "squaredRange", field = "Lnet/minecraft/entity/ai/goal/BowAttackGoal;squaredRange:F")
    @Expression("? > (double) this.squaredRange")
    @WrapOperation(method = "tick", at  =@At("MIXINEXTRAS:EXPRESSION"))
    private boolean fdmc$moveToSameSlice(double left, double right, Operation<Boolean> original, @Local LivingEntity target){
        assert target != null;
        boolean inSameSlice = new Vec4d(target.pos).w - new Vec4d(actor.pos).w == 0;
        return original.call(left, right) || !inSameSlice;
    }

}

@Mixin(ProjectileAttackGoal.class)
class ProjectileAttackGoalMixin{
    @Shadow
    @Final
    private MobEntity mob;

    @Shadow
    private @Nullable LivingEntity target;

    //can't start attacking an entity out of da slice
    @WrapOperation(method = "tick", at  = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobVisibilityCache;canSee(Lnet/minecraft/entity/Entity;)Z"))
    private boolean fdmc$moveToSameSlice(MobVisibilityCache instance, Entity target, Operation<Boolean> original){
        return new Vec4d(mob.pos).w == new Vec4d(target.pos).w && original.call(instance, target);
    }

    //also move to player if out of slice
    @Definition(id = "squaredRange", field = "Lnet/minecraft/entity/ai/goal/ProjectileAttackGoal;squaredMaxShootRange:F")
    @Expression("? > (double) this.squaredRange")
    @WrapOperation(method = "tick", at  =@At("MIXINEXTRAS:EXPRESSION"))
    private boolean fdmc$moveToSameSlice(double left, double right, Operation<Boolean> original){
        assert this.target != null;
        boolean inSameSlice = new Vec4d(target.pos).w - new Vec4d(mob.pos).w == 0;
        return original.call(left, right) || !inSameSlice;
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

//TODO enderman pick up
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

@Mixin(targets = "net.minecraft.entity.mob.SilverfishEntity$CallForHelpGoal")
abstract class SilverfishEntity$CallForHelpGoalMixin extends Goal{
    @Shadow
    @Final
    private SilverfishEntity silverfish;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", shift = At.Shift.AFTER), cancellable = true)
    private void fdmc$loopSilverfishBreakingForW(CallbackInfo ci, @Local World world, @Local Random random, @Local(ordinal = 1) BlockPos pos2 ){
        int w = 1;
        while (w <= 2 && w >= -2) { // skips w=0, that's in base fn
            BlockPos pos3 = pos2.add(FDMCMath.getOffsetX(w), 0, 0);
            BlockState blockState = world.getBlockState(pos3);
            Block block = blockState.getBlock();
            if (block instanceof InfestedBlock) {
                if (castToServerWorld(world).getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                    world.breakBlock(pos3, true, silverfish);
                } else {
                    world.setBlockState(pos3, ((InfestedBlock)block).toRegularState(world.getBlockState(pos3)), Block.NOTIFY_ALL);
                }
                if (random.nextBoolean()) ci.cancel();
            }
            w = (w <= 0 ? 1 : 0) - w;
        }
    }
}

//TODO panda {@link net.minecraft.entity.passive.PandaEntity.PandaMateGoal}::isBambooClose PROPERLY
@Mixin(targets = "net.minecraft.entity.passive.PandaEntity$PandaMateGoal")
class PandaEntity$PandaMateGoalMixin {
    // a bit cheese but we just repease the method multiple times across slices rn
    @WrapMethod(method = "isBambooClose")
    private boolean fdmc$isBambooCloseForEachSlice(Operation<Boolean> original, @Share("offset") LocalIntRef dw){
        for(int w = -3; w <= 3 ; w++) {
            dw.set(w);
            if(original.call()) return true;
        }
        return false;
    }
    @ModifyVariable(method = "isBambooClose", at  = @At(value = "STORE"), ordinal = 0)
    private BlockPos fdmc$offsetPos(BlockPos value, @Share("offset") LocalIntRef dw){
        return value.offset(Direction4Constants.ANA, dw.get());
    }
}


