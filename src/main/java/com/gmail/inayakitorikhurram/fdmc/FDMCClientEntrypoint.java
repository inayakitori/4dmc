package com.gmail.inayakitorikhurram.fdmc;

import com.gmail.inayakitorikhurram.fdmc.datagen.FDMCModelGenerator;
import com.gmail.inayakitorikhurram.fdmc.math.Direction4Constants;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanPlaceW;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.CanStep;
import com.gmail.inayakitorikhurram.fdmc.network.packet.PlayerPlacementC2SPacket;
import com.gmail.inayakitorikhurram.fdmc.screen.FDMCContainerScreen;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.Direction;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

public class FDMCClientEntrypoint implements ClientModInitializer {

    public static final EntityModelLayer CHEST_W = EntityModelLayers.registerMain("chest_w");
    public static final EntityModelLayer DOUBLE_CHEST_LEFT_W = EntityModelLayers.registerMain("double_chest_left_w");
    public static final EntityModelLayer DOUBLE_CHEST_RIGHT_W = EntityModelLayers.registerMain("double_chest_right_w");
    public static final EntityModelLayer QUAD_CHEST_W = EntityModelLayers.registerMain("quad_chest_w");

    static{
        HandledScreens.register(FDMCConstants.GENERIC_9X12, FDMCContainerScreen::new);
    }

    private static KeyBinding moveKata;
    private static KeyBinding moveAna;
    public static KeyBinding placeW;
    @Override
    public void onInitializeClient() {

        //textures
        for(Block button : FDMCModelGenerator.BUTTONS.keySet()){
            BlockRenderLayerMap.putBlock(button, BlockRenderLayer.CUTOUT);
        }

        BlockRenderLayerMap.putBlock(Blocks.HOPPER, BlockRenderLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PISTON, BlockRenderLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.STICKY_PISTON, BlockRenderLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.PISTON_HEAD, BlockRenderLayer.CUTOUT);

        //config

        AutoConfig.register(FDMCConfig.class, Toml4jConfigSerializer::new);

        //keybinds
        moveKata = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fdmc.moveKata", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_SEMICOLON, // The keycode of the key
                KeyBinding.Category.MOVEMENT // The translation key of the keybinding's category.
        ));


        moveAna = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fdmc.moveAna", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_COMMA, // The keycode of the key
                KeyBinding.Category.MOVEMENT // The translation key of the keybinding's category.
        ));

        placeW = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fdmc.placeW", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_LEFT_ALT, // The keycode of the key
                KeyBinding.Category.GAMEPLAY // The translation key of the keybinding's category.
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
            if (!((CanPlaceW) client.player).getPlacementDirection4().equals(newPlaceDirection)) {
                ((CanPlaceW) client.player).setPlacementDirection4(newPlaceDirection);
                PlayerPlacementC2SPacket packet = new PlayerPlacementC2SPacket(newPlaceDirection.map(Direction::getIndex).orElse(-1));
                ClientPlayNetworking.send(packet);

            }

            //otherwise, stepping
//            int moveDirection = getSteppingInput();
//            if (moveDirection != 0 && client.player != null) {
//                ((CanStep) client.player).scheduleStep(moveDirection, false);
//            }
        });


    }

    public static int getSteppingInput(){
        if(placeW.isPressed()) return 0;
        return (moveKata.isPressed() ? -1 : 0) + (moveAna.isPressed() ? 1 : 0);
    }

}
