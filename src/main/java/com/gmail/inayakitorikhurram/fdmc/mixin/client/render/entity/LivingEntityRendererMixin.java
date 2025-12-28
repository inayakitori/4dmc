package com.gmail.inayakitorikhurram.fdmc.mixin.client.render.entity;

import com.gmail.inayakitorikhurram.fdmc.FDMCClientConstants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.EntityRenderStateAccess;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {


    @WrapMethod(method = "getRenderLayer")
    private <S extends LivingEntityRenderState> @Nullable RenderLayer modifiedRenderLayer(S state, boolean showBody, boolean translucent, boolean showOutline, Operation<RenderLayer> original){
        RenderLayer originalLayer = original.call(state, showBody, translucent, showOutline);
        int dw = ((EntityRenderStateAccess)state).getDw();
        if(originalLayer != null && dw != 0) {
            return original.call(state, showBody, true, showOutline);
        }
        return originalLayer;
    }

    @WrapMethod(method = "getMixColor")
    private <S extends LivingEntityRenderState> int fdmc$modifyRenderColor(S state, Operation<Integer> original){
        int dw = ((EntityRenderStateAccess)state).getDw();
        if(dw == 0) return original.call(state);
        int absW = MathHelper.abs(dw);
        int opacity = 0xFF - 0x44 * absW;
        int desaturation = 0x44 - 0x11 * absW;


        int color = dw > 0 ? FDMCClientConstants.ANA_COLOR : FDMCClientConstants.KATA_COLOR;
        color += dw > 0 ?
                ColorHelper.getArgb(0, desaturation, 0) :
                ColorHelper.getArgb(desaturation, 0, desaturation);
        color &= 0xFFFFFF; // clear opacity
        color |= (opacity << 24);

        return ColorHelper.mix(color, original.call(state));
    }

}
