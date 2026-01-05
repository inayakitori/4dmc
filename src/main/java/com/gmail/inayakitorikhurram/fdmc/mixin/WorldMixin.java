package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Perspective4Access;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(net.minecraft.world.World.class)
public abstract class WorldMixin implements WorldAccess {
    @Shadow
    @Final
    private boolean isClient;

    //use HORIZONTAL4
    @Redirect(
            method = {
                    "updateComparators"
            },
            at=@At(
                    value = "FIELD",
                    target = "Lnet/minecraft/util/math/Direction$Type;HORIZONTAL:Lnet/minecraft/util/math/Direction$Type;",
                    opcode = Opcodes.GETSTATIC)
    )
    private Direction.Type fdmc$redirectToHorizontal4(){
        return Direction4Constants.Type4.HORIZONTAL4;
    }

    @WrapMethod(method = "getBlockState")
    BlockState fdmc$getBlockState(BlockPos pos, Operation<BlockState> original){
        if (!isClient) return original.call(pos);
        Perspective4Access camera = (Perspective4Access) MinecraftClient.getInstance().getCameraEntity();
        return original.call(
            camera == null ? pos : camera.getPerspective4().projectInverse(pos)
        );
    }

    @WrapMethod(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z")
    boolean fdmc$setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, Operation<Boolean> original){
        if (!isClient) return original.call(pos, state, flags, maxUpdateDepth);
        Perspective4Access camera = (Perspective4Access) MinecraftClient.getInstance().getCameraEntity();
        return original.call(
            camera == null ? pos : camera.getPerspective4().projectInverse(pos),
            state,
            flags,
            maxUpdateDepth
        );
    }
}
