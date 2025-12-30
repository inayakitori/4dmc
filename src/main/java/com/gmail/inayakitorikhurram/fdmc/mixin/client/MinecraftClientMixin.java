package com.gmail.inayakitorikhurram.fdmc.mixin.client;

import com.gmail.inayakitorikhurram.fdmc.client.option.GameOptions4;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;

import static com.gmail.inayakitorikhurram.fdmc.FDMCClientEntrypoint.placeW;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @WrapMethod(method = "handleInputEvents")
    private void handleInputEvents(Operation<Void> original){
        if (placeW.isPressed()) {
            MinecraftClient client = (MinecraftClient)(Object) this;
            while (client.options.togglePerspectiveKey.wasPressed()) {
                // rotate 4d perspective
                // this is done via a mixin, otherwise keybinding flips vanilla perspective too
                GameOptions4 options4 = (GameOptions4) client.options;
                options4.setPerspective4(options4.getPerspective4().next());
                // TODO reload world or whatever
            }
        }
        original.call();
    }
}
