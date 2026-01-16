package com.gmail.inayakitorikhurram.fdmc.mixin.entity.ai.pathing;

import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.NavigateToTargetSliceFirst;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkCache;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(PathNodeNavigator.class)
public class PathNodeNavigatorMixin {

    @Unique private boolean shouldNavigateToTargetSliceFirst;
    @Unique private double targetW = Double.NaN;
    @Inject(method = "findPathToAny(Lnet/minecraft/world/chunk/ChunkCache;Lnet/minecraft/entity/mob/MobEntity;Ljava/util/Set;FIF)Lnet/minecraft/entity/ai/pathing/Path;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/pathing/PathNodeNavigator;findPathToAny(Lnet/minecraft/entity/ai/pathing/PathNode;Ljava/util/Map;FIF)Lnet/minecraft/entity/ai/pathing/Path;"
                ,shift = At.Shift.BEFORE))
    private void fdmc$CaptureMob(
            ChunkCache world, MobEntity mob, Set<BlockPos> positions, float followRange, int distance, float rangeMultiplier, CallbackInfoReturnable<Path> cir){
        if(mob instanceof NavigateToTargetSliceFirst navMob){
            shouldNavigateToTargetSliceFirst = navMob.shouldNavigateToTargetSliceFirst();
            targetW = navMob.getTargetW();
        } else{
            shouldNavigateToTargetSliceFirst = false;
        }
    }

    @WrapOperation(method = "findPathToAny(Lnet/minecraft/entity/ai/pathing/PathNode;Ljava/util/Map;FIF)Lnet/minecraft/entity/ai/pathing/Path;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/pathing/PathMinHeap;pop()Lnet/minecraft/entity/ai/pathing/PathNode;"))
    private PathNode fdmc$capturePathNode1(PathMinHeap instance, Operation<PathNode> original, @Share("pathNode1")LocalRef<PathNode> pathNode1Ref){
        PathNode pathNode1 = original.call(instance);
        pathNode1Ref.set(pathNode1);
        return pathNode1;
    }

    @WrapOperation(method = "findPathToAny(Lnet/minecraft/entity/ai/pathing/PathNode;Ljava/util/Map;FIF)Lnet/minecraft/entity/ai/pathing/Path;"
    , at = @At(value = "FIELD", target = "Lnet/minecraft/entity/ai/pathing/PathNode;penalty:F", opcode = Opcodes.GETFIELD))
    private float fdmc$IncreasePenaltyOnSpecificEntities(
            PathNode pathNode2, Operation<Float> original, @Share("pathNode1")LocalRef<PathNode> pathNode1Ref){

        if (shouldNavigateToTargetSliceFirst && !Double.isNaN(targetW)) {
            PathNode pathNode1 = pathNode1Ref.get();
            double currentW = FDMCMath.splitX3(pathNode1.x)[1];
            double nextW = FDMCMath.splitX3(pathNode2.x)[1];
            // +ve when step towards target, negative when step away from target
            double movementDirTowardsTarget = (nextW - currentW) * MathHelper.sign(targetW - currentW);
            //pathNode2.heapWeight += -12f * (float) (movementDirTowardsTarget);
            //penalise steps not towards the target
            if (movementDirTowardsTarget <= 0) {
                // make sure the entity steps in to the same slice
                pathNode2.penalty += 10f;
            }
        }

        return original.call(pathNode2);
    }
}
