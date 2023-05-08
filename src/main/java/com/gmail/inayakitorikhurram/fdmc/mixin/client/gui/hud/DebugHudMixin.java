package com.gmail.inayakitorikhurram.fdmc.mixin.client.gui.hud;

import com.gmail.inayakitorikhurram.fdmc.math.ChunkPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4i;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ChunkSectionPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Locale;

@Mixin(DebugHud.class)
public abstract class DebugHudMixin<E> {

    @Shadow @Final private MinecraftClient client;

    @Redirect(method = "getLeftText", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 3) )
    public boolean FdmcLeftText(List<String> list, E originalMessage){
        //new
        //pos
        Entity camera = client.getCameraEntity();
        Vec4d camPos4 = new Vec4d(camera.getPos());
        Vec4i blockPos4 = Vec4i.asVec4i(camera.getBlockPos());
        ChunkPos4 chunkPos4 = new ChunkPos4(camera.getChunkPos());
        int w = blockPos4.getW4();
        list.add("4 Position: W = " + w);
        list.add(String.format(Locale.ROOT,
                "XYZ: %.3f / %.5f / %.3f",
                camPos4.getX(),
                camPos4.getY(),
                camPos4.getZ())
        );

        //block
        list.add(String.format(
                "Block: %d %d %d [%2d %2d %2d]",
                blockPos4.getX4(),
                blockPos4.getY4(),
                blockPos4.getZ4(),
                blockPos4.getX() & 0xF,
                blockPos4.getY() & 0xF,
                blockPos4.getZ() & 0xF)
        );

        //chunk
        list.add(String.format(
                "Chunk: %d %d %d",
                chunkPos4.x,
                ChunkSectionPos.getSectionCoord(camera.getY()),
                chunkPos4.z)
        );

        //original
        list.add("");
        list.add("3 Position:");
        list.add((String)originalMessage);
        return true;
    }

}
