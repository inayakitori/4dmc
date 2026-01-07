package com.gmail.inayakitorikhurram.fdmc.mixin.client.gui.hud.debug;

import com.gmail.inayakitorikhurram.fdmc.math.Perspective4;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Perspective4Access;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.client.gui.hud.debug.PlayerPositionDebugHudEntry;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Locale;

@Mixin(PlayerPositionDebugHudEntry.class)
public abstract class PlayerPositionDebugHudEntryMixin {
    @Inject(
        method = "render",
        at = @At(value = "TAIL")
    )
    private void render(DebugHudLines lines, World world, WorldChunk clientChunk, WorldChunk chunk, CallbackInfo ci,
                        @Local Entity cameraEntity) {
        Perspective4 perspective4 = ((Perspective4Access) cameraEntity).getPerspective4();
        Vec3d renderPos = perspective4.project(new Vec4d(cameraEntity.pos)).toPos3();

        lines.addLinesToSection(PlayerPositionDebugHudEntry.SECTION_ID, List.of(
            String.format(Locale.ROOT, "XYZ (Render): %.3f / %.5f / %.3f", renderPos.x, renderPos.y, renderPos.z),
            "Visible hyperplane: (" + Perspective4.DEFAULT + ") <- (" + perspective4 + ")")
        );
    }
}
