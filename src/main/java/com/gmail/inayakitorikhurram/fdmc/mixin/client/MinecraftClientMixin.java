package com.gmail.inayakitorikhurram.fdmc.mixin.client;

import com.gmail.inayakitorikhurram.fdmc.FDMCConfig;
import com.gmail.inayakitorikhurram.fdmc.client.option.GameOptions4;
import com.gmail.inayakitorikhurram.fdmc.client.option.Perspective4;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.IDebugHudMixin;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;

import static com.gmail.inayakitorikhurram.fdmc.FDMCClientEntrypoint.placeW;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @WrapMethod(method = "handleInputEvents")
    private void handleInputEvents(Operation<Void> original){
        if (placeW.isPressed()) {
            MinecraftClient client = (MinecraftClient)(Object) this;
            Entity camera = client.getCameraEntity();
            IDebugHudMixin debugHud = (IDebugHudMixin) client.getDebugHud();
            FDMCConfig config = AutoConfig.getConfigHolder(FDMCConfig.class).getConfig();

            while (client.options.togglePerspectiveKey.wasPressed()) {
                if (camera == null) continue;
                // rotate 4d perspective
                // this is done via a mixin, otherwise keybinding flips vanilla perspective too
                GameOptions4 options4 = (GameOptions4) client.options;
                Perspective4 perspective4 = options4.getPerspective4();

                Direction fixedRenderDirection = switch(config.slice_rotation.fixed_direction) {
                    case FORWARD -> camera.getFacing();
                    case RIGHT -> camera.getHorizontalFacing().rotateYClockwise();
                };
                Direction4 facingLogicalDirection4 = perspective4.projectInverse(fixedRenderDirection);

                options4.setPerspective4(perspective4.rotateAround(
                    facingLogicalDirection4,
                    perspective4.renderY()
                ));

                debugHud.fdmc$refreshDebugCrosshairBuffer();
                client.worldRenderer.reload();
            }
        }
        original.call();
    }
}
