package com.gmail.inayakitorikhurram.fdmc.mixin.client.render.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Box4;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.EntityRenderStateAccess;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Shadow
    @Final
    protected EntityRenderManager dispatcher;

    @WrapOperation(method = "updateRenderState",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(DDD)D", ordinal = 0))
    private <T extends Entity, S extends EntityRenderState> double fdmc$modifyEntityXPos(
            double delta, double start, double end, Operation<Double> original,
            @Local(argsOnly = true)T entity, @Local(argsOnly = true) S state){

        if(this.dispatcher.camera == null || !(state instanceof LivingEntityRenderState)) {
            return original.call(delta, start, end);
        }

        BlockPos4 entityPos = BlockPos4.of(entity.blockPos);
        BlockPos4 cameraPos = BlockPos4.of(this.dispatcher.camera.getBlockPos());

        int dw = cameraPos.getW4() - entityPos.getW4();
        if(MathHelper.abs(dw) <= FDMCConstants.ENTITY_RENDER_MAX_DW) {
            // the negative because this is used to show how far out it is from the player
            ((EntityRenderStateAccess) state).setDw(-dw);
            return original.call(delta, start, end) + FDMCMath.getOffsetX(dw);
        } else {
            return original.call(delta, start, end);
        }
    }
    @WrapOperation(method = "updateRenderState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(DDD)D", ordinal = 1))
    private <T extends Entity, S extends EntityRenderState> double fdmc$modifyEntityYPos(
            double delta, double start, double end, Operation<Double> original,
            @Local(argsOnly = true) S state){
        int dw =((EntityRenderStateAccess) state).getDw();
        if(dw != 0) {
            // render slightly higher if offset in w to prevent clipping
            return original.call(delta, start, end) + 0.0001 * (1 + Math.abs(dw) + 0.5 * dw);
        } else {
            return original.call(delta, start, end);
        }
    }

    @WrapOperation(method = "shouldRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;expand(D)Lnet/minecraft/util/math/Box;"))
    private Box fdmc$expandBoxForFrustrum(Box instance, double value, Operation<Box> original){
        return Box4.converted(original.call(instance, value)).expand(0,FDMCConstants.ENTITY_RENDER_MAX_DW);
    }
}
