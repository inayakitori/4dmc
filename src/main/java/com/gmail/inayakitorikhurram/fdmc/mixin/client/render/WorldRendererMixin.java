package com.gmail.inayakitorikhurram.fdmc.mixin.client.render;

import com.gmail.inayakitorikhurram.fdmc.FDMCClientConstants;
import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Perspective4Access;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.state.OutlineRenderState;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldRenderer.BrightnessGetter.class)
interface WorldRenderer$BrightnessGetterMixin{
    @WrapMethod(method = "method_68890")
    private static int fdmc$defaultLightGetter(BlockRenderView world, BlockPos pos, Operation<Integer> original){
        return original.call(world,
                ((Perspective4Access)MinecraftClient.getInstance().getCameraEntity())
                        .getPerspective4().projectInverse(pos)
        );
    }
}

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
        int color = dw > 0  ? FDMCClientConstants.ANA_COLOR : FDMCClientConstants.KATA_COLOR; // rgb
        color &= 0xAAFFFFFF;
        color = ColorHelper.withBrightness(color, 0.6f);
        original.call(worldRenderer, matrices, vertexConsumer, x, y, z, newState, color);
    }

    @WrapOperation(method = "fillEntityRenderStates", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;getBlockPos()Lnet/minecraft/util/math/BlockPos;"))
    private BlockPos fdmc$modifiedBlockPos(Entity instance, Operation<BlockPos> original, @Local(argsOnly = true) Camera camera){
        BlockPos4 entityPos = BlockPos4.of(original.call(instance));
        BlockPos4 cameraPos = BlockPos4.of(camera.getBlockPos());

        int dw = cameraPos.getW4() - entityPos.getW4();
        if(MathHelper.abs(dw) <= FDMCConstants.ENTITY_RENDER_MAX_DW) {
            return original.call(instance).offset(Direction4Constants.ANA, dw);
        } else {
            return original.call(instance);
        }
    }
}
