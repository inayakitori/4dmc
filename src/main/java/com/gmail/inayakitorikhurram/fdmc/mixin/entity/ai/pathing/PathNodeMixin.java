package com.gmail.inayakitorikhurram.fdmc.mixin.entity.ai.pathing;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.*;

import static com.gmail.inayakitorikhurram.fdmc.FDMCConstants.PATHFINDING_W_SCALE;

@Mixin(PathNode.class)
public abstract class PathNodeMixin {


    @Shadow
    @Final
    @Mutable
    public int x;

    @Shadow
    @Final
    @Mutable
    public int y;

    @Shadow
    @Final
    @Mutable
    public int z;


    @Shadow
    public abstract float getSquaredDistance(PathNode node);

    @Shadow
    public abstract float getSquaredDistance(BlockPos pos);

    /**
     * @author inayakitori
     * @reason Making this a string that uses w would break other overwrites
     */
    @Overwrite
    public String toString() {
        int[] xw = FDMCMath.splitX3(this.x);
        return "Node{x=" + xw[0] + ", y=" + this.y + ", z=" + this.z + ", w=" + xw[1] + "}";
    }

    @WrapMethod(method = "hash")
    private static int fdmc$modifiedHash(int x3, int y, int z, Operation<Integer> original){

        // put the w bits (after 18) in the top part of the x value
        // The original hash has 8 bits y, 16 bits x, 8 bits z.
        // The z bits get cut off bc max 32 bits for int
        int newX = (x3 & Byte.MAX_VALUE) | ((x3 >> FDMCConstants.STEP_DISTANCE_BITS) << 8);

        int hash = original.call(newX, y, z);

        return hash;
    }


    // for the distance methods, temporarily change the x values then set them back
    @WrapMethod(method = "getDistance(Lnet/minecraft/entity/ai/pathing/PathNode;)F")
    private float fdmc$getNodeDistance(PathNode other, Operation<Float> original){
        return MathHelper.sqrt(this.getSquaredDistance(other));
    }

    @WrapMethod(method = "getHorizontalDistance")
    private float fdmc$getNodeHorizontalDistance(PathNode other, Operation<Float> original){
        int[] this$xw = FDMCMath.splitX3(this.x);
        int[] other$xw = FDMCMath.splitX3(other.x);
        int dx = this$xw[0] - other$xw[0];
        int dz = this.z - other.z;
        int dw = this$xw[1] - other$xw[1];

        float dw_s = dw * PATHFINDING_W_SCALE;
        return MathHelper.sqrt(dx * dx + dz * dz + dw_s * dw_s);
    }

    @WrapMethod(method = "getDistance(Lnet/minecraft/util/math/BlockPos;)F")
    private float fdmc$getBlockPosDistance(BlockPos pos, Operation<Float> original){
        return MathHelper.sqrt(this.getSquaredDistance(pos));
    }

    @WrapMethod(method = "getSquaredDistance(Lnet/minecraft/entity/ai/pathing/PathNode;)F")
    private float fdmc$getNodeSquaredDistance(PathNode other, Operation<Float> original){
        int[] this$xw = FDMCMath.splitX3(this.x);
        int[] other$xw = FDMCMath.splitX3(other.x);
        int dx = this$xw[0] - other$xw[0];
        int dy = this.y - other.y;
        int dz = this.z - other.z;
        int dw = this$xw[1] - other$xw[1];
        float dw_s = dw * PATHFINDING_W_SCALE;
        return dx*dx + dy*dy + dz*dz + dw_s*dw_s;
    }

    @WrapMethod(method = "getSquaredDistance(Lnet/minecraft/util/math/BlockPos;)F")
    private float fdmc$getBlockPosSquaredDistance(BlockPos pos, Operation<Float> original){
        int[] this$xw = FDMCMath.splitX3(this.x);
        BlockPos4 other = BlockPos4.of(pos);
        int dx = this$xw[0] - other.getX4();
        int dy = this.y - other.getY4();
        int dz = this.z - other.getZ4();
        int dw = this$xw[1] - other.getW4();
        float dw_s = dw * PATHFINDING_W_SCALE;
        return dx*dx + dy*dy + dz*dz + dw_s*dw_s;
    }

    @WrapMethod(method = "getManhattanDistance(Lnet/minecraft/entity/ai/pathing/PathNode;)F")
    private float fdmc$getNodeManhattanDistance(PathNode other, Operation<Float> original){
        int[] this$xw = FDMCMath.splitX3(this.x);
        int[] other$xw = FDMCMath.splitX3(other.x);
        int dx = Math.abs(this$xw[0] - other$xw[0]);
        int dy = Math.abs(this.y - other.y);
        int dz = Math.abs(this.z - other.z);
        int dw = Math.abs(this$xw[1] - other$xw[1]);
        float dw_s = dw * PATHFINDING_W_SCALE;
        return dx + dy + dz + dw + dw_s;
    }

    @WrapMethod(method = "getManhattanDistance(Lnet/minecraft/util/math/BlockPos;)F")
    private float fdmc$getBlockPosManhattanDistance(BlockPos pos, Operation<Float> original){
        int[] this$xw = FDMCMath.splitX3(this.x);
        BlockPos4 other = BlockPos4.of(pos);
        int dx = Math.abs(this$xw[0] - other.getX4());
        int dy = Math.abs(this.y - other.getY4());
        int dz = Math.abs(this.z - other.getZ4());
        int dw = Math.abs(this$xw[1] - other.getW4());
        float dw_s = dw * PATHFINDING_W_SCALE;
        return dx + dy + dz + dw_s;
    }

}
