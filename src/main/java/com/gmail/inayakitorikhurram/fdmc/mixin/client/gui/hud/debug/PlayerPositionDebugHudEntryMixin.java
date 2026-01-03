package com.gmail.inayakitorikhurram.fdmc.mixin.client.gui.hud.debug;

import com.gmail.inayakitorikhurram.fdmc.FDMCConfig;
import com.gmail.inayakitorikhurram.fdmc.client.option.GameOptions4;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.client.gui.hud.debug.PlayerPositionDebugHudEntry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerPositionDebugHudEntry.class)
public abstract class PlayerPositionDebugHudEntryMixin {
    @Inject(
        method = "render",
        at = @At(value = "TAIL")
    )
    private void render(DebugHudLines lines, World world, WorldChunk clientChunk, WorldChunk chunk, CallbackInfo ci) {
        GameOptions4 options4 = (GameOptions4) MinecraftClient.getInstance().options;
        boolean useShortForm = AutoConfig.getConfigHolder(FDMCConfig.class).get().slice_rotation.shorthand_slice_notation;

        lines.addLinesToSection(PlayerPositionDebugHudEntry.SECTION_ID, List.of(
            "Visible hyperplane: " + (useShortForm ?
                    options4.getPerspective4().toShortString() :
                    options4.getPerspective4().toString())
        ));
    }
}
