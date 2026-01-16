package com.gmail.inayakitorikhurram.fdmc.mixin.entity.ai.pathing;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntityNavigation.class)
public class EntityNavigationMixin {
    @ModifyArgs(method = "findPathToAny(Ljava/util/Set;IZIF)Lnet/minecraft/entity/ai/pathing/Path;",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/ChunkCache;<init>(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)V"))
    private void fdmc$widenChunkCache(
            Args args, @Local(argsOnly = true, ordinal = 0) int range,
            @Local(argsOnly = true, ordinal = 0) float followRange){

        int offset = (int)(followRange + (float)range);
        int offsetW = Math.max(offset >> 3, 1);

        args.set(1, ((BlockPos)args.get(1)).offset(Direction4Constants.KATA, offsetW));
        args.set(2, ((BlockPos)args.get(2)).offset(Direction4Constants.ANA, offsetW));

    }

}
