package com.gmail.inayakitorikhurram.fdmc.mixin;

import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(InGameOverlayRenderer.class)
public abstract class InGameOverlayRendererMixin {

    @ModifyConstant(method = "getInWallBlockState", constant = @Constant(intValue = 8))
    private static int injectedBlockCheckCount(int prevVal){
        return 2;
    }

    @ModifyVariable(method = "getInWallBlockState", at = @At("STORE"), ordinal = 1)
    private static double modifyIndexForEye(double in) {
        return in;
    }

    @Redirect(method = "getInWallBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getWidth()F"))
    private static float getPlayerWidth(PlayerEntity instance){
        return 0;
    }

}
