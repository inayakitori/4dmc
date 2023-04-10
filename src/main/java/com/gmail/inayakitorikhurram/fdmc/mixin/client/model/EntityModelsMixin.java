package com.gmail.inayakitorikhurram.fdmc.mixin.client.model;

import com.gmail.inayakitorikhurram.fdmc.FDMCClientEntrypoint;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.ChestBlockI;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(EntityModels.class)
public class EntityModelsMixin {
    @Inject(method = "getModels", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;", ordinal = 0, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void addCustomModels(CallbackInfoReturnable<Map<EntityModelLayer, TexturedModelData>> cir, ImmutableMap.Builder<EntityModelLayer, TexturedModelData> builder){
        builder.put(FDMCClientEntrypoint.CHEST_W, ChestBlockI.getSingleTexturedModelDataW());
        builder.put(FDMCClientEntrypoint.DOUBLE_CHEST_RIGHT_W, ChestBlockI.getRightDoubleTexturedModelDataW());
        builder.put(FDMCClientEntrypoint.DOUBLE_CHEST_LEFT_W, ChestBlockI.getLeftDoubleTexturedModelDataW());
        builder.put(FDMCClientEntrypoint.QUAD_CHEST_W, ChestBlockI.getQuadTexturedModelDataW());
    }
}
