package com.gmail.inayakitorikhurram.fdmc.mixin.client.render;

import com.gmail.inayakitorikhurram.fdmc.FDMCClientConstants;
import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanPlaceW;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.state.OutlineRenderState;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.util.Handle;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @WrapOperation(method = "renderTargetBlockOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawBlockOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;DDDLnet/minecraft/client/render/state/OutlineRenderState;I)V"))
    private void fdmc$renderBlockOutlineInFrontOfPlayer(
            WorldRenderer worldRenderer, MatrixStack matrices, VertexConsumer vertexConsumer, double x, double y, double z,
            OutlineRenderState state, int i, Operation<Void> original,
            @Local(argsOnly = true) VertexConsumerProvider.Immediate vertexConsumerProvider,
            @Local(argsOnly = true) WorldRenderState worldRenderState
    ){
        int dw = BlockPos4.of(state.pos()).getW4() - BlockPos4.of(worldRenderState.cameraRenderState.blockPos).getW4();
        if(dw == 0) {
            original.call(worldRenderer, matrices, vertexConsumer, x, y, z, state, i);
            return;
        }
        // This one has no depth buffer
        vertexConsumer = vertexConsumerProvider.getBuffer(FDMCClientConstants.LINES_NO_CULL_PHASE);
        OutlineRenderState newState =
                        new OutlineRenderState(
                                state.pos().offset(Direction4Constants.ANA, -dw),
                                state.isTranslucent(),
                                state.highContrast(),
                                state.shape(),
                                state.collisionShape(),
                                state.occlusionShape(),
                                state.interactionShape());
        int color = dw > 0  ? 0xAA884488 : 0xAA226622;
        original.call(worldRenderer, matrices, vertexConsumer, x, y, z, newState, color);
    }


}
