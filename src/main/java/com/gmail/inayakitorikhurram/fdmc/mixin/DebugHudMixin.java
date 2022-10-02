package com.gmail.inayakitorikhurram.fdmc.mixin;

import com.gmail.inayakitorikhurram.fdmc.FDMCMath;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Locale;

@Mixin(DebugHud.class)
public abstract class DebugHudMixin<E> extends DrawableHelper {

    @Shadow @Final private MinecraftClient client;

    @Redirect(method = "getLeftText", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 3) )
    public boolean FdmcLeftText(List<String> list, E originalMessage){
        //new
        //pos
        Entity camera = client.getCameraEntity();
        double[] camPos4 = FDMCMath.toPos4(camera.getPos());
        int[] blockPos4 = FDMCMath.toBlockPos4(camera.getBlockPos());
        int[] chunkPos4 = FDMCMath.toChunkPos4(camera.getChunkPos());
        int w = blockPos4[3];
        list.add("4 Position: W = " + w);
        list.add(String.format(Locale.ROOT,
                "XYZ: %.3f / %.5f / %.3f",
                camPos4[0],
                camPos4[1],
                camPos4[2])
        );

        //block
        list.add(String.format(
                "Block: %d %d %d [%2d %2d %2d]",
                blockPos4[0],
                blockPos4[1],
                blockPos4[2],
                blockPos4[0] & 0xF,
                blockPos4[1] & 0xF,
                blockPos4[2] & 0xF)
        );

        //chunk
        list.add(String.format(
                "Chunk: %d %d %d",
                chunkPos4[0],
                ChunkSectionPos.getSectionCoord(camera.getY()),
                chunkPos4[1])
        );

        //original
        list.add("");
        list.add("3 Position:");
        list.add((String)originalMessage);
        return true;
    }

}
