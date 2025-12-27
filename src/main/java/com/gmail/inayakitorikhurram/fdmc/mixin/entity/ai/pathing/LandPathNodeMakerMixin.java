package com.gmail.inayakitorikhurram.fdmc.mixin.entity.ai.pathing;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LandPathNodeMaker.class)
public abstract class LandPathNodeMakerMixin extends PathNodeMaker {
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

        boolean flipDirections = this.entity.getRandom().nextBoolean();

        for (Direction direction : new Direction[]{Direction4Constants.KATA, Direction4Constants.ANA}) {
            // don't prefer a specific direction for pathfinding
            if(flipDirections){
                direction = direction.getOpposite();
            }
            PathNode pathNode = this.getPathNode(node.x + direction.getOffsetX(), node.y, node.z + direction.getOffsetZ(), j, d, direction, pathNodeType2);
            this.successors[direction.getHorizontalQuarterTurns()] = pathNode;
            if (!this.isValidAdjacentSuccessor(pathNode, node)) continue;
//            if(pathNode == null) continue;
            successors[i++] = pathNode;
        }

        cir.setReturnValue(i);
    }
}
