package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class FDMCClientEntrypoint implements ClientModInitializer {

    private static KeyBinding moveKata;
    private static KeyBinding moveAna;
    @Override
    public void onInitializeClient() {

        moveKata = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fdmc.moveKata", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_Q, // The keycode of the key
                KeyBinding.MOVEMENT_CATEGORY // The translation key of the keybinding's category.
        ));


        moveAna = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fdmc.moveAna", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_E, // The keycode of the key
                KeyBinding.MOVEMENT_CATEGORY // The translation key of the keybinding's category.
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            int moveDirection = (moveKata.wasPressed() ? -1 :0) +  (moveAna.wasPressed() ? 1 : 0);

            if(moveDirection != 0 && client.player != null && ((CanStep)client.player).canStep(moveDirection)){
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(moveDirection);
                ClientPlayNetworking.send(FDMCConstants.MOVING_PLAYER_ID, buf);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(FDMCConstants.MOVING_PLAYER_ID, (client, handler, buf, responseSender) -> {
            int serverTick = buf.readInt();
            int stepDirection = buf.readInt();
            if(stepDirection != 0) {
            Vec3d vel = new Vec3d(
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble());
                client.execute(() -> {
                    ((CanStep)client.player).setSteppingLocally(stepDirection, vel);
                });
            } else{
                ((CanStep)client.player).setSteppingLocally(0, null);
            }
        });


    }
}
