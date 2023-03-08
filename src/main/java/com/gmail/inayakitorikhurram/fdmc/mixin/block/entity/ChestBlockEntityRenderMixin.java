package com.gmail.inayakitorikhurram.fdmc.mixin.block.entity;

import com.gmail.inayakitorikhurram.fdmc.client.model.ModelCuboidDatas;
import com.gmail.inayakitorikhurram.fdmc.math.ChestAdjacencyAxis;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.DoubleChestType;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.ChestBlockI;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.CHEST_TYPE_2;

@Debug
@Mixin(ChestBlockEntityRenderer.class)
public abstract class ChestBlockEntityRenderMixin<T extends BlockEntity & LidOpenable>
        implements BlockEntityRenderer<T> {

    @Shadow @Final private ModelPart singleChestLid;

    @Shadow @Final private ModelPart singleChestLatch;

    @Shadow @Final private ModelPart singleChestBase;

    @Shadow public abstract void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay);

    @Shadow @Final private ModelPart doubleChestLeftLid;

    @Shadow @Final private ModelPart doubleChestLeftLatch;

    @Shadow @Final private ModelPart doubleChestLeftBase;

    @Shadow @Final private ModelPart doubleChestRightLid;

    @Shadow @Final private ModelPart doubleChestRightLatch;

    @Shadow @Final private ModelPart doubleChestRightBase;

    @Redirect(
            method = "getSingleTexturedModelData",
            at = @At(
                    value = "INVOKE",
                    target =
                            "Lnet/minecraft/client/model/ModelPartData;addChild(Ljava/lang/String;Lnet/minecraft/client/model/ModelPartBuilder;Lnet/minecraft/client/model/ModelTransform;)Lnet/minecraft/client/model/ModelPartData;",
                    ordinal = 0
            )
    )
    private static ModelPartData redirectSingleTextureBase(ModelPartData modelPartData, String name, ModelPartBuilder builder, ModelTransform rotationData){

        ModelPartBuilder modifiedBuilder = ModelPartBuilder.create()
                .uv(builder.textureX, builder.textureY)
                .mirrored(builder.mirror);

        for(int i = 0; i < builder.cuboidData.size(); i++){
            ModelCuboidData data = builder.cuboidData.get(i);
            modifiedBuilder.cuboidData.add(new ModelCuboidDatas.BackMirroredModelCuboidData(
                    data.name,
                    builder.textureX, builder.textureY,
                    data.offset.x, data.offset.y, data.offset.z,
                    data.dimensions.x, data.dimensions.y, data.dimensions.z,
                    Dilation.NONE,
                    builder.mirror,
                    1.0f, 1.0f)
            );
        }

        return modelPartData.addChild(name, modifiedBuilder, rotationData);
    }

    @Inject(
            method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/SpriteIdentifier;getVertexConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Ljava/util/function/Function;)Lnet/minecraft/client/render/VertexConsumer;",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            cancellable = true
    )
    private void renderChest(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci,
                             World world,
                             boolean worldExists,
                             BlockState state,
                             ChestType chestType,
                             Block block,
                             AbstractChestBlock abstractChestBlock,
                             boolean notSingle,
                             float facingRotation,
                             DoubleBlockProperties.PropertySource<Object> propertySource,
                             float openFactor,
                             int renderLight,
                             SpriteIdentifier spriteIdentifier
    ){
//        propertySource = worldExists ? (DoubleBlockProperties.PropertySource<Object>) abstractChestBlock.getBlockEntitySource(state, world, entity.getPos(), true) : DoubleBlockProperties.PropertyRetriever::getFallback;
//        openFactor = ((Float2FloatFunction)propertySource.apply(
//                (DoubleBlockProperties.PropertyRetriever<? super Object, T>)(Object)ChestBlockI.getAnimationProgressRetriever(entity))
//        ).get(tickDelta);

        VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);
        Direction facing = state.get(ChestBlock.FACING);
        Optional<Direction> rightDirection = ChestBlockI.getConnectionDirection(state, ChestAdjacencyAxis.LEFTRIGHT);
        Optional<Direction> kataDirection = ChestBlockI.getConnectionDirection(state, ChestAdjacencyAxis.KATAANA);
        ChestType chestType2 = state.contains(CHEST_TYPE_2) ? state.get(CHEST_TYPE_2) : ChestType.SINGLE;
        DoubleChestType doubleChestType = new DoubleChestType(chestType, chestType2);

        if(doubleChestType.isSingle()){
            if(facing.getAxis() == Direction4Constants.Axis4Constants.W) {//double in 3d facing w
                this.renderWFacing(matrices, vertexConsumer, this.singleChestLid, this.singleChestLatch, this.singleChestBase, openFactor, renderLight, overlay);
            } else{
                this.render(matrices, vertexConsumer, this.singleChestLid, this.singleChestLatch, this.singleChestBase, openFactor, renderLight, overlay);
            }
        } else if(doubleChestType.isDouble()){
            if(facing.getAxis() == Direction4Constants.Axis4Constants.W){//double in 3d facing w
                this.renderWFacing(matrices, vertexConsumer, this.singleChestLid, this.singleChestLatch, this.singleChestBase, openFactor, renderLight, overlay);
            } else if(rightDirection.isPresent() && rightDirection.get().getAxis() != Direction4Constants.Axis4Constants.W){ //double in 3D
                if (chestType == ChestType.LEFT) {
                    this.render(matrices, vertexConsumer, this.doubleChestLeftLid, this.doubleChestLeftLatch, this.doubleChestLeftBase, openFactor, renderLight, overlay);
                } else {
                    this.render(matrices, vertexConsumer, this.doubleChestRightLid, this.doubleChestRightLatch, this.doubleChestRightBase, openFactor, renderLight, overlay);
                }
            } else if(kataDirection.isPresent() && kataDirection.get().getAxis() != Direction4Constants.Axis4Constants.W){//double in 3D
                //rotateMatrices(matrices, -90f);
                if (chestType2 == ChestType.LEFT) {
                    this.render(matrices, vertexConsumer, this.doubleChestLeftLid, this.doubleChestLeftLatch, this.doubleChestLeftBase, openFactor, renderLight, overlay);
                } else {
                    this.render(matrices, vertexConsumer, this.doubleChestRightLid, this.doubleChestRightLatch, this.doubleChestRightBase, openFactor, renderLight, overlay);
                }
            } else{//single in 3D
                this.render(matrices, vertexConsumer, this.singleChestLid, this.singleChestLatch, this.singleChestBase, openFactor, renderLight, overlay);
            }
        } else{//is quad
            this.renderWFacing(matrices, vertexConsumer, this.singleChestLid, this.singleChestLatch, this.singleChestBase, openFactor, renderLight, overlay);
            //quad in 3D

            //double in 3D

            //double in 3D
        }
        matrices.pop();
        ci.cancel();
    }

    //latchless
    private void renderWFacing(MatrixStack matrices, VertexConsumer vertices, ModelPart lid, ModelPart latch,  ModelPart base, float openFactor, int light, int overlay) {
        lid.xScale = 1.0f - openFactor;
        lid.yScale = 1.0f - openFactor;
        lid.zScale = 1.0f - openFactor;
        latch.pitch = lid.pitch = 0;
        lid.render(matrices, vertices, light, overlay);
        base.render(matrices, vertices, light, overlay);
    }

    //override
    private void render(MatrixStack matrices, VertexConsumer vertices, ModelPart lid, ModelPart latch, ModelPart base, float openFactor, int light, int overlay) {
        lid.xScale = 1.0f;
        lid.yScale = 1.0f;
        lid.zScale = 1.0f;
        latch.pitch = lid.pitch = -(openFactor * 1.5707964f);
        lid.render(matrices, vertices, light, overlay);
        latch.render(matrices, vertices, light, overlay);
        base.render(matrices, vertices, light, overlay);
    }

    private void rotateMatrices(MatrixStack matrices, float rot){
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-rot));
        matrices.translate(-0.5f, -0.5f, -0.5f);
    }



}
