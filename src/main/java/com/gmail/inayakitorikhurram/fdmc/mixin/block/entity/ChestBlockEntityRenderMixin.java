package com.gmail.inayakitorikhurram.fdmc.mixin.block.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCMainEntrypoint;
import com.gmail.inayakitorikhurram.fdmc.math.ChestAdjacencyAxis;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.math.DoubleChestType;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.ChestBlockI;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

import static com.gmail.inayakitorikhurram.fdmc.FDMCProperties.CHEST_TYPE_2;

@Debug
@Mixin(ChestBlockEntityRenderer.class)
public abstract class ChestBlockEntityRenderMixin<T extends BlockEntity & LidOpenable>
        implements BlockEntityRenderer<T> {

    @Shadow public abstract void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay);

    @Shadow @Final public static String BASE;
    @Shadow @Final public static String LID;
    @Shadow @Final public static String LATCH;

    @Shadow @Final private ModelPart singleChestLid;
    @Shadow @Final private ModelPart singleChestLatch;
    @Shadow @Final private ModelPart singleChestBase;
    @Shadow @Final private ModelPart doubleChestLeftLid;
    @Shadow @Final private ModelPart doubleChestLeftLatch;
    @Shadow @Final private ModelPart doubleChestLeftBase;
    @Shadow @Final private ModelPart doubleChestRightLid;
    @Shadow @Final private ModelPart doubleChestRightLatch;
    @Shadow @Final private ModelPart doubleChestRightBase;
    private ModelPart singleChestLidW;
    private ModelPart singleChestLatchW;
    private ModelPart singleChestBaseW;
    private ModelPart doubleChestLeftLidW;
    private ModelPart doubleChestLeftLatchW;
    private ModelPart doubleChestLeftBaseW;
    private ModelPart doubleChestRightLidW;
    private ModelPart doubleChestRightLatchW;
    private ModelPart doubleChestRightBaseW;
    private ModelPart quadChestLidW;
    private ModelPart quadChestLatchW;
    private ModelPart quadChestBaseW;

    @Shadow private boolean christmas;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initEnd(BlockEntityRendererFactory.Context ctx, CallbackInfo ci){

        ModelPart modelPart = ctx.getLayerModelPart(FDMCMainEntrypoint.CHEST_W);
        this.singleChestBaseW = modelPart.getChild(BASE);
        this.singleChestLidW = modelPart.getChild(LID);
        this.singleChestLatchW = modelPart.getChild(LATCH);
        ModelPart modelPart2 = ctx.getLayerModelPart(FDMCMainEntrypoint.DOUBLE_CHEST_LEFT_W);
        this.doubleChestLeftBaseW = modelPart2.getChild(BASE);
        this.doubleChestLeftLidW = modelPart2.getChild(LID);
        this.doubleChestLeftLatchW = modelPart2.getChild(LATCH);
        ModelPart modelPart3 = ctx.getLayerModelPart(FDMCMainEntrypoint.DOUBLE_CHEST_RIGHT_W);
        this.doubleChestRightBaseW = modelPart3.getChild(BASE);
        this.doubleChestRightLidW = modelPart3.getChild(LID);
        this.doubleChestRightLatchW = modelPart3.getChild(LATCH);
        ModelPart modelPart4 = ctx.getLayerModelPart(FDMCMainEntrypoint.CHEST_W);
        this.quadChestBaseW = modelPart4.getChild(BASE);
        this.quadChestLidW = modelPart4.getChild(LID);
        this.quadChestLatchW = modelPart4.getChild(LATCH);
    }


    @Inject(
            method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/TexturedRenderLayers;getChestTexture(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/block/enums/ChestType;Z)Lnet/minecraft/client/util/SpriteIdentifier;",
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
                             int renderLight
    ){

        Direction facing = state.get(ChestBlock.FACING);
        ChestType chestType2 = state.contains(CHEST_TYPE_2) ? state.get(CHEST_TYPE_2) : ChestType.SINGLE;

        DoubleChestType doubleChestType = new DoubleChestType(chestType, chestType2);

        SpriteIdentifier spriteIdentifier;

        //for the single in 3d chests
        spriteIdentifier = TexturedRenderLayers.getChestTexture(entity, ChestType.SINGLE, christmas);
        VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);

        //single case
        if(doubleChestType.isSingle()){
            renderSingle3Chest(matrices, vertexConsumer, facing, openFactor, renderLight, overlay);
            matrices.pop();
            ci.cancel();
            return;
        }


        Optional<Direction> rightDirection = ChestBlockI.getConnectionDirection(state, ChestAdjacencyAxis.LEFTRIGHT);
        Optional<Direction> kataDirection = ChestBlockI.getConnectionDirection(state, ChestAdjacencyAxis.KATAANA);


        //for the non-single in 3d chests
        spriteIdentifier = TexturedRenderLayers.getChestTexture(entity, chestType == ChestType.SINGLE? chestType2 : chestType, christmas);
        vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);

        if(doubleChestType.isDouble()){
            if(facing.getAxis() == Direction4Constants.Axis4Constants.W){//double in 3d facing w
                if (chestType == ChestType.LEFT) {
                    this.renderWFacing(matrices, vertexConsumer, facing, this.doubleChestLeftLidW, this.doubleChestLeftLatchW, this.doubleChestLeftBaseW, openFactor, renderLight, overlay);
                } else if(chestType == ChestType.RIGHT) {
                    this.renderWFacing(matrices, vertexConsumer, facing, this.doubleChestRightLidW, this.doubleChestRightLatchW, this.doubleChestRightBaseW, openFactor, renderLight, overlay);
                } else{
                    rotateMatrices(matrices, -90f);
                    if(chestType2 == ChestType.LEFT){
                        this.renderWFacing(matrices, vertexConsumer, facing, this.doubleChestLeftLidW, this.doubleChestLeftLatchW, this.doubleChestLeftBaseW, openFactor, renderLight, overlay);
                    } else{
                        this.renderWFacing(matrices, vertexConsumer, facing, this.doubleChestRightLidW, this.doubleChestRightLatchW, this.doubleChestRightBaseW, openFactor, renderLight, overlay);
                    }
                }
            }
            else if(rightDirection.isPresent() && rightDirection.get().getAxis() != Direction4Constants.Axis4Constants.W){ //double in 3D left/right connected
                if (chestType == ChestType.LEFT) {
                    this.render(matrices, vertexConsumer, this.doubleChestLeftLid, this.doubleChestLeftLatch, this.doubleChestLeftBase, openFactor, renderLight, overlay);
                } else {
                    this.render(matrices, vertexConsumer, this.doubleChestRightLid, this.doubleChestRightLatch, this.doubleChestRightBase, openFactor, renderLight, overlay);
                }
            }
            else if(kataDirection.isPresent() && kataDirection.get().getAxis() != Direction4Constants.Axis4Constants.W){//double in 3D kata/ana connected
                if (chestType2 == ChestType.LEFT) {
                    this.render(matrices, vertexConsumer, this.doubleChestLeftLid, this.doubleChestLeftLatch, this.doubleChestLeftBase, openFactor, renderLight, overlay);
                } else {
                    this.render(matrices, vertexConsumer, this.doubleChestRightLid, this.doubleChestRightLatch, this.doubleChestRightBase, openFactor, renderLight, overlay);
                }
            }
            else{//single in 3D
                spriteIdentifier = TexturedRenderLayers.getChestTexture(entity, ChestType.SINGLE, christmas);
                vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);
                renderSingle3Chest(matrices, vertexConsumer, facing, openFactor, renderLight, overlay);
            }
        } else{//is quad
            //quad in 3D
            if(facing.getAxis() == Direction4Constants.Axis4Constants.W){
                if (chestType == ChestType.LEFT) {
                    this.renderWFacing(matrices, vertexConsumer, facing, this.doubleChestLeftLidW, this.doubleChestLeftLatchW, this.doubleChestLeftBaseW, openFactor, renderLight, overlay);
                } else if(chestType == ChestType.RIGHT) {
                    this.renderWFacing(matrices, vertexConsumer, facing, this.doubleChestRightLidW, this.doubleChestRightLatchW, this.doubleChestRightBaseW, openFactor, renderLight, overlay);
                } else{
                    rotateMatrices(matrices, -90f);
                    if(chestType2 == ChestType.LEFT){
                        this.renderWFacing(matrices, vertexConsumer, facing, this.doubleChestLeftLidW, this.doubleChestLeftLatchW, this.doubleChestLeftBaseW, openFactor, renderLight, overlay);
                    } else{
                        this.renderWFacing(matrices, vertexConsumer, facing, this.doubleChestRightLidW, this.doubleChestRightLatchW, this.doubleChestRightBaseW, openFactor, renderLight, overlay);
                    }
                }
            } else{//double in 3D, let the normal one handle it
                if (chestType == ChestType.LEFT) {
                    this.render(matrices, vertexConsumer, this.doubleChestLeftLid, this.doubleChestLeftLatch, this.doubleChestLeftBase, openFactor, renderLight, overlay);
                } else {
                    this.render(matrices, vertexConsumer, this.doubleChestRightLid, this.doubleChestRightLatch, this.doubleChestRightBase, openFactor, renderLight, overlay);
                }
            }
        }
        matrices.pop();
        ci.cancel();
    }

    private void renderSingle3Chest(MatrixStack matrices, VertexConsumer vertexConsumer, Direction facing, float openFactor, int renderLight, int overlay){

        if(facing.getAxis() == Direction4Constants.Axis4Constants.W) {//single in 3d facing w
            this.renderWFacing(matrices, vertexConsumer, facing, this.singleChestLidW, this.singleChestLatchW, this.singleChestBaseW, openFactor, renderLight, overlay);
        } else{
            this.render(matrices, vertexConsumer, this.singleChestLid, this.singleChestLatch, this.singleChestBase, openFactor, renderLight, overlay);
        }
    }

    //latchless
    private void renderWFacing(MatrixStack matrices, VertexConsumer vertices, Direction facing, ModelPart lid, ModelPart latch,  ModelPart base, float openFactor, int light, int overlay) {
        if(facing == Direction4Constants.ANA){
            rotateMatrices(matrices, 180.0f);
        }
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
