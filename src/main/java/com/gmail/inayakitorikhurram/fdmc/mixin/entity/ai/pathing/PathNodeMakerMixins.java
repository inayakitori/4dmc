package com.gmail.inayakitorikhurram.fdmc.mixin.entity.ai.pathing;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LandPathNodeMaker.class)
abstract class LandPathNodeMakerMixin extends PathNodeMaker {
    @Shadow
    @Final
    @Mutable
    private PathNode[] successors = new PathNode[6];

    @Shadow
    protected abstract @Nullable PathNode getPathNode(int x, int y, int z, int maxYStep, double lastFeetY, Direction direction, PathNodeType nodeType);

    @Shadow
    protected abstract boolean isValidAdjacentSuccessor(@Nullable PathNode node, PathNode successor);

    @Shadow
    protected abstract PathNodeType getNodeType(int x, int y, int z);

    @Shadow
    protected abstract double getFeetY(BlockPos pos);



    @Inject(method = "getSuccessors", at = @At("RETURN"), cancellable = true)
    private void fdmc$addSuccessors(PathNode[] successors, PathNode node, CallbackInfoReturnable<Integer> cir){
        int i = cir.getReturnValueI();
        PathNodeType pathNodeType = this.getNodeType(node.x, node.y + 1, node.z);
        PathNodeType pathNodeType2 = this.getNodeType(node.x, node.y, node.z);
        int j = 0;
        if (this.entity.getPathfindingPenalty(pathNodeType) >= 0.0f && pathNodeType2 != PathNodeType.STICKY_HONEY) {
            j = MathHelper.floor(Math.max(1.0f, this.entity.getStepHeight()));
        }
        double d = this.getFeetY(new BlockPos(node.x, node.y, node.z));


        for (Direction direction : new Direction[]{Direction4Constants.KATA, Direction4Constants.ANA}) {
            PathNode pathNode = this.getPathNode(node.x + direction.getOffsetX(), node.y, node.z + direction.getOffsetZ(), j, d, direction, pathNodeType2);
            this.successors[direction.getHorizontalQuarterTurns()] = pathNode;
            if (!this.isValidAdjacentSuccessor(pathNode, node)) continue;
//            if(pathNode == null) continue;
            successors[i++] = pathNode;
        }

        cir.setReturnValue(i);
    }
}

@Mixin(BirdPathNodeMaker.class)
abstract class BirdPathNodeMakerMixin{
    @Shadow
    protected abstract @Nullable PathNode getPassableNode(int x, int y, int z);

    @Shadow
    protected abstract boolean unvisited(@Nullable PathNode node);

    @Inject(method = "getSuccessors", at = @At("RETURN"), cancellable = true)
    private void fdmc$addSuccessors(PathNode[] successors, PathNode node, CallbackInfoReturnable<Integer> cir){
        int i = cir.getReturnValueI();
        PathNode pathNode = this.getPassableNode(node.x + FDMCMath.getOffsetX(1), node.y, node.z);
        if (this.unvisited(pathNode)) {
            successors[i++] = pathNode;
        }
        PathNode pathNode2 = this.getPassableNode(node.x + FDMCMath.getOffsetX(-1), node.y, node.z);
        if (this.unvisited(pathNode2)) {
            successors[i++] = pathNode2;
        }
        cir.setReturnValue(i);
    }
}
