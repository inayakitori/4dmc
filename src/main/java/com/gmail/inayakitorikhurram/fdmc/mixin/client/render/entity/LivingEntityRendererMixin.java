package com.gmail.inayakitorikhurram.fdmc.mixin.client.render.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCClientConstants;
import com.gmail.inayakitorikhurram.fdmc.FDMCConstants;
import com.gmail.inayakitorikhurram.fdmc.math.FDMCMath;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.EntityRenderStateAccess;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {


    @WrapMethod(method = "getRenderLayer")
    private <S extends LivingEntityRenderState> @Nullable RenderLayer modifiedRenderLayer(S state, boolean showBody, boolean translucent, boolean showOutline, Operation<RenderLayer> original){
        RenderLayer originalLayer = original.call(state, showBody, translucent, showOutline);
        double dw = ((EntityRenderStateAccess)state).getDw();
        if(originalLayer != null && dw != 0) {
            return original.call(state, showBody, true, showOutline);
        }
        return originalLayer;
    }

    @WrapMethod(method = "getMixColor")
    private <S extends LivingEntityRenderState> int fdmc$modifyRenderColor(S state, Operation<Integer> original){
        final double dw = ((EntityRenderStateAccess)state).getDw();
        if(dw == 0) return original.call(state);

        // Get ANA/KATA tint
        Vector4f color = FDMCMath.parseARGB(dw > 0 ? FDMCClientConstants.ANA_COLOR : FDMCClientConstants.KATA_COLOR);
        color.lerp(
            // Lerp tint with original color
            FDMCMath.parseARGB(original.call(state)),
            // At max entity render this has value of 0, and at dw=0 it has value of 1
            1f - (float) MathHelper.square(dw / FDMCConstants.ENTITY_RENDER_MAX_DW)
        );
        // Setting opacity; same function as before, just more flat around dw=0
        color.x *= 1f - (float) MathHelper.square(MathHelper.square(dw / FDMCConstants.ENTITY_RENDER_MAX_DW));
        return ColorHelper.fromFloats(color.x, color.y, color.z, color.w);
    }

}
