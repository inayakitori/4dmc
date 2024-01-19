package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.datagen.FDMCModelGenerator;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.gmail.inayakitorikhurram.fdmc.screen.FDMCContainerScreen;
import com.gmail.inayakitorikhurram.fdmc.screen.FDMCScreenHandler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

public class FDMCClientEntrypoint implements ClientModInitializer {



    public static final EntityModelLayer CHEST_W = EntityModelLayers.registerMain("chest_w");
    public static final EntityModelLayer DOUBLE_CHEST_LEFT_W = EntityModelLayers.registerMain("double_chest_left_w");
    public static final EntityModelLayer DOUBLE_CHEST_RIGHT_W = EntityModelLayers.registerMain("double_chest_right_w");
    public static final EntityModelLayer QUAD_CHEST_W = EntityModelLayers.registerMain("quad_chest_w");

    public static final ScreenHandlerType<FDMCScreenHandler> GENERIC_9X12 = ScreenHandlerType.register("generic_9x12", FDMCScreenHandler::createGeneric9x12);

    static{
        HandledScreens.register(GENERIC_9X12, FDMCContainerScreen::new);
    }

    private static KeyBinding moveKata;
    private static KeyBinding moveAna;
    public static KeyBinding placeW;
    @Override
    public void onInitializeClient() {

        //textures
        for(Block button : FDMCModelGenerator.BUTTONS.keySet()){
            BlockRenderLayerMap.INSTANCE.putBlock(button, RenderLayer.getCutout());
        }

        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.HOPPER, RenderLayer.getCutout());

        //config

        AutoConfig.register(FDMCConfig.class, Toml4jConfigSerializer::new);

        //keybinds
        moveKata = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fdmc.moveKata", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_SEMICOLON, // The keycode of the key
                KeyBinding.MOVEMENT_CATEGORY // The translation key of the keybinding's category.
        ));


        moveAna = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fdmc.moveAna", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_COMMA, // The keycode of the key
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
            Optional<Direction> newPlaceDirection = Optional.empty();
            if (placeW.isPressed()) {
                if (moveKata.isPressed() && !moveAna.isPressed()) {
                    newPlaceDirection = Optional.of(Direction4Constants.KATA);
                } else if (moveAna.isPressed() && !moveKata.isPressed()) {
                    newPlaceDirection = Optional.of(Direction4Constants.ANA);
                }
            }

            //if the placement direction has changed, change it and send a network packet so it changes serverside too
            if (!((CanStep) client.player).getPlacementDirection4().equals(newPlaceDirection)) {
                ((CanStep) client.player).setPlacementDirection4(newPlaceDirection);

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(newPlaceDirection.map(Direction::getId).orElse(-1));
                ClientPlayNetworking.send(FDMCConstants.PLAYER_PLACEMENT_DIRECTION_ID, buf);

            }

            //otherwise, stepping
            if(!placeW.isPressed()) {
                int moveDirection = (moveKata.isPressed() ? -1 : 0) + (moveAna.isPressed() ? 1 : 0);

                if (moveDirection != 0 && client.player != null && ((CanStep) client.player).canStep(moveDirection)) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeInt(moveDirection);
                    boolean shouldPlaceUnderSupport = AutoConfig.getConfigHolder(FDMCConfig.class).getConfig().under_support.create_support;
                    buf.writeBoolean(shouldPlaceUnderSupport);
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
