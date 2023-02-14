package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
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

import java.util.Optional;

public class FDMCClientEntrypoint implements ClientModInitializer {

    private static KeyBinding moveKata;
    private static KeyBinding moveAna;
    private static KeyBinding placeW;
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

        placeW = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fdmc.placeW", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_LEFT_ALT, // The keycode of the key
                KeyBinding.GAMEPLAY_CATEGORY // The translation key of the keybinding's category.
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if(client.player == null) return;


            //placement
            Optional<Direction4> newPlaceDirection = Optional.empty();
            if (placeW.isPressed()) {
                if (moveKata.isPressed() && !moveAna.isPressed()) {
                    newPlaceDirection = Optional.of(Direction4Constants.KATA4);
                } else if (moveAna.isPressed() && !moveKata.isPressed()) {
                    newPlaceDirection = Optional.of(Direction4Constants.ANA4);
                }
            }

            //if the placement direction has changed, change it and send a network packet so it changes serverside too
            if (!((CanStep) client.player).getPlacementDirection4().equals(newPlaceDirection)) {
                ((CanStep) client.player).setPlacementDirection4(newPlaceDirection);

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(newPlaceDirection.map(Direction4::getId).orElse(-1));
                ClientPlayNetworking.send(FDMCConstants.PLAYER_PLACEMENT_DIRECTION_ID, buf);

            }

            //otherwise, stepping
            if(!placeW.isPressed()) {
                int moveDirection = (moveKata.isPressed() ? -1 : 0) + (moveAna.isPressed() ? 1 : 0);

                if (moveDirection != 0 && client.player != null && ((CanStep) client.player).canStep(moveDirection)) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeInt(moveDirection);
                    ClientPlayNetworking.send(FDMCConstants.MOVING_PLAYER_ID, buf);
                }
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(FDMCConstants.MOVING_PLAYER_ID, (client, handler, buf, responseSender) -> {
            CanStep steppingPlayer =  (CanStep) client.player;
            int serverTick = buf.readInt();
            int stepDirection = buf.readInt();
            if(stepDirection != 0) { //start stepping tick
            Vec3d vel = new Vec3d(
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble());
                client.execute(() -> {
                    steppingPlayer.setSteppingLocally(serverTick, stepDirection, vel);
                });
            } else{
                steppingPlayer.setSteppingLocally(serverTick ,0, null);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(FDMCConstants.UPDATE_COLLISION_MOVEMENT, (client, handler, buf, responseSender) -> {
            CanStep steppingPlayer =  (CanStep) client.player;
            boolean[] pushableDirection = new boolean[6];
            for(int i = 0; i < 6; i++) {
                pushableDirection[i] = buf.readBoolean();
            }
            steppingPlayer.updatePushableDirectionsLocally(pushableDirection);
        });

    }
}
