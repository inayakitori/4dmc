package com.gmail.inayakitorikhurram.fdmc.mixin.client.render.entity;

import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderManager.class)
public class EntityRenderManagerMixin {
	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V"))
	void renderAsIfInTheSameWAsCamera(EntityRenderer<?, ?> entityRenderer, EntityRenderState entityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue queue, CameraRenderState cameraState, Operation<Void> original) {
		double offset = FDMCMath.getOffsetX(FDMCMath.splitX3(cameraState.pos.x)[1] - FDMCMath.splitX3(entityRenderState.x)[1]);
		matrixStack.translate(offset, 0, 0);
		original.call(entityRenderer, entityRenderState, matrixStack, queue, cameraState);
		matrixStack.translate(-offset, 0, 0);
	}
}
