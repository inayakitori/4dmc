package com.gmail.inayakitorikhurram.fdmc.mixin.client.render.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCClientConstants;
import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.Box4;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.EntityRenderStateAccess;
import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.joml.Vector4f;
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
    private <T extends Entity, S extends EntityRenderState> double fdmc$setDW(
            double delta, double start, double end, Operation<Double> original,
            @Local(argsOnly = true)T entity, @Local(argsOnly = true) S state){

        if(this.dispatcher.camera == null || !(state instanceof LivingEntityRenderState)) {
            return original.call(delta, start, end);
        }

        Vec4d entityPos = Vec4d.of(entity.getEntityPos());
        Vec4d cameraPos = Vec4d.of(this.dispatcher.camera.getFocusedEntity().getEntityPos());

        double dw = cameraPos.w - entityPos.w;
        if(Math.abs(dw) <= FDMCConstants.ENTITY_RENDER_MAX_DW) {
            // the negative because this is used to show how far out it is from the player
            ((EntityRenderStateAccess) state).setDw(-dw);
        }
        return original.call(delta, start, end);
    }

    @WrapOperation(method = "updateRenderState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(DDD)D", ordinal = 1))
    private <S extends EntityRenderState> double fdmc$modifyEntityYPos(
            double delta, double start, double end, Operation<Double> original,
            @Local(argsOnly = true) S state){
        final double dw =((EntityRenderStateAccess) state).getDw();
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

    @WrapOperation(method = "createHitbox", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList$Builder;add(Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList$Builder;", ordinal = 0))
    <E> ImmutableList.Builder<EntityHitbox> addHitboxesInAllSlices(
        ImmutableList.Builder<EntityHitbox> builder, E originalEntityHitbox, Operation<ImmutableList.Builder<EntityHitbox>> original,
        @Local Box boundingBox3, @Local(argsOnly = true) Entity entity
    ) {
        final Vector4f anaColor = FDMCMath.parseARGB(FDMCClientConstants.ANA_COLOR);
        final Vector4f kataColor = FDMCMath.parseARGB(FDMCClientConstants.KATA_COLOR);
        final Vec4d pos = Vec4d.of(entity.getEntityPos());
        for (Box slice : Box4.converted(boundingBox3).slices()) {
            float red = ((EntityHitbox) originalEntityHitbox).red();
            float green = ((EntityHitbox) originalEntityHitbox).green();
            float blue = ((EntityHitbox) originalEntityHitbox).blue();
            double sliceW = FDMCMath.splitX3(slice.minX)[1];
            if (sliceW > pos.w) {
                red = Math.min(red, anaColor.y);
                green = Math.min(green, anaColor.z);
                blue = Math.min(green, anaColor.w);
            } else if (sliceW < pos.w) {
                red = Math.min(red, kataColor.y);
                green = Math.min(green, kataColor.z);
                blue = Math.min(green, kataColor.w);
            }
            original.call(builder, new EntityHitbox(
                slice.minX - pos.x4, slice.minY - pos.y, slice.minZ - pos.z, slice.maxX - pos.x4, slice.maxY - pos.y, slice.maxZ - pos.z, red, green, blue
            ));
        }
        return builder;
    }
}
