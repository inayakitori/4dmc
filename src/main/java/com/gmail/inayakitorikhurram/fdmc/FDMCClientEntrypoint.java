package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class FDMCClientEntrypoint implements ClientModInitializer {

    private static KeyBinding moveAna;
    private static KeyBinding moveKata;
    @Override
    public void onInitializeClient() {

        moveAna = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fdmc.moveAna", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_Q, // The keycode of the key
                KeyBinding.MOVEMENT_CATEGORY // The translation key of the keybinding's category.
        ));


        moveKata = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fdmc.moveKata", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_E, // The keycode of the key
                KeyBinding.MOVEMENT_CATEGORY // The translation key of the keybinding's category.
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            int moveDirection = (moveAna.wasPressed() ? -1 :0) +  (moveKata.wasPressed() ? 1 : 0);

            if(moveDirection != 0 && client.player != null && ((CanStep)client.player).canStep(moveDirection)){
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(moveDirection);
                ClientPlayNetworking.send(FDMCConstants.MOVING_PLAYER_ID, buf);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(FDMCConstants.MOVING_PLAYER_ID, (client, handler, buf, responseSender) -> {
            Vec3d vel = new Vec3d(
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble());
            int stepDirection = buf.readInt();
            if(stepDirection != 0) {
                client.execute(() -> {
                    ((CanStep)client.player).setStepDirection(stepDirection);
                    ((CanStep)client.player).setStepping(true);
                    client.player.setVelocity(vel);
                });
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(FDMCConstants.MOVED_PLAYER_ID, (client, handler, buf, responseSender) -> {
            ((CanStep)client.player).setStepping(false);
        });

    }
}
