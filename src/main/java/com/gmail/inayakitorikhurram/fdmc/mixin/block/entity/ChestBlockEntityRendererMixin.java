package com.gmail.inayakitorikhurram.fdmc.mixin.block.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCClientEntrypoint;
import com.gmail.inayakitorikhurram.fdmc.client.render.block.entity.model.ChestWBlockModel;
import com.gmail.inayakitorikhurram.fdmc.math.ChestAdjacencyAxis;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.DoubleChestType;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CBRenderStateI;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.ChestBlockI;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.render.block.entity.model.ChestBlockModel;
import net.minecraft.client.render.block.entity.state.ChestBlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.CHEST_TYPE_2;

@Debug
@Mixin(ChestBlockEntityRenderer.class)
public abstract class ChestBlockEntityRendererMixin<T extends BlockEntity & LidOpenable>
        implements BlockEntityRenderer<T, ChestBlockEntityRenderState> {



    @Shadow @Final private ChestBlockModel singleChest;
    @Shadow @Final private ChestBlockModel doubleChestLeft;
    @Shadow @Final private ChestBlockModel doubleChestRight;

    private ChestBlockModel singleChestW;
    private ChestBlockModel doubleChestLeftW;
    private ChestBlockModel doubleChestRightW;
    private ChestBlockModel quadChestW;

    @Shadow private boolean christmas;

    @Shadow
    @Final
    private SpriteHolder materials;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initEnd(BlockEntityRendererFactory.Context ctx, CallbackInfo ci){

        singleChestW = new ChestWBlockModel(ctx.getLayerModelPart(FDMCClientEntrypoint.CHEST_W));
        doubleChestLeftW = new ChestWBlockModel(ctx.getLayerModelPart(FDMCClientEntrypoint.DOUBLE_CHEST_LEFT_W));
        doubleChestRightW = new ChestWBlockModel(ctx.getLayerModelPart(FDMCClientEntrypoint.DOUBLE_CHEST_RIGHT_W));
        quadChestW = new ChestWBlockModel(ctx.getLayerModelPart(FDMCClientEntrypoint.QUAD_CHEST_W));

    }

    @Inject(method = "updateRenderState(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/client/render/block/entity/state/ChestBlockEntityRenderState;FLnet/minecraft/util/math/Vec3d;Lnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"
    , at = @At("HEAD"), cancellable = true)
    private void fdmc$updateRenderState(T blockEntity, ChestBlockEntityRenderState chestBlockEntityRenderState, float f, Vec3d vec3d, ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand, CallbackInfo ci){

        BlockState state = blockEntity.getCachedState();

        Direction facing = state.get(ChestBlock.FACING);
        ChestType chestType = state.contains(ChestBlock.CHEST_TYPE) ? state.get(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
        ChestType chestType2 = state.contains(CHEST_TYPE_2) ? state.get(CHEST_TYPE_2) : ChestType.SINGLE;

        // follow original logic for state if not facing in w
        if(facing.getAxis() != Direction4Constants.Axis4Constants.W) return;

        int wDirection = facing == Direction4Constants.ANA ? +1 : -1;

        chestBlockEntityRenderState.chestType = chestType;

        CBRenderStateI extendedRenderState = CBRenderStateI.of(chestBlockEntityRenderState);
        extendedRenderState.setWDirection(wDirection);
        extendedRenderState.setChestType2(chestType2);
    }


    //With tissue paper and duct tape, this function is
    @Inject(
            method = "render(Lnet/minecraft/client/render/block/entity/state/ChestBlockEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/SpriteIdentifier;getRenderLayer(Ljava/util/function/Function;)Lnet/minecraft/client/render/RenderLayer;",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void renderChest(ChestBlockEntityRenderState chestBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci, float openFactor){
        // meow
        CBRenderStateI extendedRenderState = CBRenderStateI.of(chestBlockEntityRenderState);
        int wDirection = extendedRenderState.getWDirection();
        if(wDirection == 0) return;

        SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getChestTextureId(chestBlockEntityRenderState.variant, ChestType.SINGLE);
        RenderLayer renderLayer = spriteIdentifier.getRenderLayer(RenderLayer::getEntityCutout);
        Sprite sprite = this.materials.getSprite(spriteIdentifier);
        float yaw = chestBlockEntityRenderState.yaw;

        ChestType typeX = chestBlockEntityRenderState.chestType;
        boolean flipX = typeX == ChestType.RIGHT ^ wDirection == -1;
        if(typeX != ChestType.SINGLE) {
            if(flipX) {
                ApplyYRotation(matrixStack, 180f);
            }
            ApplyYRotation(matrixStack, -yaw);
            matrixStack.scale(16f/15f, 1, 1);
            ApplyYRotation(matrixStack, yaw);
            if(flipX) {
                ApplyYRotation(matrixStack, -180f);
            }
        }

        ChestType typeZ = extendedRenderState.getChestType2();
        boolean flipZ = typeZ == ChestType.RIGHT ^ wDirection == 1;
        if(typeZ != ChestType.SINGLE) {
            if(flipZ) {
                ApplyYRotation(matrixStack, 180f);
            }
            ApplyYRotation(matrixStack, -yaw);
            matrixStack.scale(1, 1, 16f/15f);
            ApplyYRotation(matrixStack, yaw);
            if(flipZ) {
                ApplyYRotation(matrixStack, 180f);
            }
        }

        orderedRenderCommandQueue.submitModel(quadChestW, Float.valueOf(openFactor), matrixStack, renderLayer, chestBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, -1, sprite, 0, chestBlockEntityRenderState.crumblingOverlay);

        matrixStack.pop();
        ci.cancel();
    }

    private void ApplyYRotation(MatrixStack matrices, float rot){
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-rot));
        matrices.translate(-0.5f, -0.5f, -0.5f);
    }



}
