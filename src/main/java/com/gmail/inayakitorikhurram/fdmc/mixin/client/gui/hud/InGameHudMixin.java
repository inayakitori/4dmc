package com.gmail.inayakitorikhurram.fdmc.mixin.client.gui.hud;

import com.gmail.inayakitorikhurram.fdmc.FDMCConfig;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderSubtitlesHud(Lnet/minecraft/client/gui/DrawContext;Z)V",
    shift = At.Shift.AFTER))
    private void addSliceGuiLayer(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci){
        this.renderSliceGui(context, tickCounter);
    }


    @Unique
    private void renderSliceGui(DrawContext context, RenderTickCounter tickCounter) {
        FDMCConfig config = AutoConfig.getConfigHolder(FDMCConfig.class).getConfig();
        if(!config.slice_gui.render_gui) return;

        context.createNewRootLayer();
        Profiler profiler = Profilers.get();
        profiler.push("fdmcSliceHud");

        Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();
        final float scale = config.slice_gui.gui_scale;
        matrices.scale(scale, scale);

        if(this.client.player == null){
            profiler.pop();
            return;
        }
        // TODO reintroduce

        Vec4d position = Vec4d.of(this.client.player.getEntityPos());

        context.drawCenteredTextWithShadow(
                this.client.textRenderer,
                String.format("%+.3f", position.w),
                (int) (context.getScaledWindowWidth() / (2f*scale)),
                (int) (20f / scale),
                -1
        );
        matrices.popMatrix();
        profiler.pop();
    }

}
