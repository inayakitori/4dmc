package com.gmail.inayakitorikhurram.fdmc.mixin.client.gui.hud;

import com.gmail.inayakitorikhurram.fdmc.FDMCConfig;
import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/BossBarHud;render(Lnet/minecraft/client/gui/DrawContext;)V"))
    private void renderSliceGui(DrawContext context, float tickDelta, CallbackInfo ci){
        //if not enabled don't render
        FDMCConfig config = AutoConfig.getConfigHolder(FDMCConfig.class).getConfig();
        if(!config.slice_gui.render_gui) return;

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        final float scale = config.slice_gui.gui_scale;
        matrices.scale(scale, scale, scale);

        if(this.client.player == null) return;
        this.client.getProfiler().pop();
        this.client.getProfiler().push("fdmcSliceHud");

        BlockPos4<?, ?> position = BlockPos4.asBlockPos4(this.client.player.getBlockPos());

        context.drawCenteredTextWithShadow(
                this.client.textRenderer,
                String.format("%+d", position.getW4()),
                (int) (context.getScaledWindowWidth() / (2f*scale)),
                (int) (20f / scale),
                0xFFFFFF
                );
        matrices.pop();
    }
}
