package com.gmail.inayakitorikhurram.fdmc.mixin.block;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin {
    @Redirect(
        method = "createTeleportTarget",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/border/WorldBorder;clampFloored(DDD)Lnet/minecraft/util/math/BlockPos;"
        )
    )
    private static BlockPos fdmc$clampFlooredInW(
        WorldBorder This,
        double x, double y, double z,
        @Local double dimensionScaleFactor,
        @Local(argsOnly = true) Entity entity
    ){
        Vec4d pos = Vec4d.of(This.clamp(entity.pos));
        Vec4d posFitToW = new Vec4d(
            pos.x4 * dimensionScaleFactor,
            pos.y,
            pos.z * dimensionScaleFactor,
            Math.floor(pos.w * dimensionScaleFactor) // W is fractional at the moment?
        );
        return BlockPos4.newBlockPos4(posFitToW.x4, posFitToW.y, posFitToW.z, posFitToW.w).asBlockPos();
    }
}
