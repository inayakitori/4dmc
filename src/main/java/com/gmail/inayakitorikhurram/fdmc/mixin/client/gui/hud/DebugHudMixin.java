package com.gmail.inayakitorikhurram.fdmc.mixin.client.gui.hud;

import com.gmail.inayakitorikhurram.fdmc.math.ChunkPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Perspective4;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4i;
import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.IDebugHudMixin;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ChunkSectionPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Locale;

@Mixin(DebugHud.class)
public abstract class DebugHudMixin<E> implements IDebugHudMixin {

    @Shadow @Final private MinecraftClient client;

    @Mutable
    @Shadow @Final private GpuBuffer debugCrosshairBuffer;

    // TODO
    //@Redirect(method = "getLeftText", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 3) )
    public boolean FdmcLeftText(List<String> list, E originalMessage){
        //new
        //pos
        Entity camera = client.getCameraEntity();
        Vec4d camPos4 = new Vec4d(camera.pos);
        Vec4i blockPos4 = Vec4i.of(camera.getBlockPos());
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

    public void fdmc$refreshDebugCrosshairBuffer(Perspective4 perspective4) {
        this.debugCrosshairBuffer.close();

        int colorX = perspective4.renderX().getAxis4().asEnum().debugColor;
        int colorY = perspective4.renderY().getAxis4().asEnum().debugColor;
        int colorZ = perspective4.renderZ().getAxis4().asEnum().debugColor;
        float directionX = (float) perspective4.renderX().getDirection().offset();
        float directionY = (float) perspective4.renderY().getDirection().offset();
        float directionZ = (float) perspective4.renderZ().getDirection().offset();

        try (BufferAllocator bufferAllocator = BufferAllocator.fixedSized(VertexFormats.POSITION_COLOR_NORMAL.getVertexSize() * 6 * 2)){
            BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR_NORMAL);
            bufferBuilder.vertex(   0.0f, 0.0f, 0.0f).color(colorX).normal(directionX, 0.0f, 0.0f);
            bufferBuilder.vertex(directionX, 0.0f, 0.0f).color(colorX).normal(directionX, 0.0f, 0.0f);
            bufferBuilder.vertex(0.0f,    0.0f, 0.0f).color(colorY).normal(0.0f, directionY, 0.0f);
            bufferBuilder.vertex(0.0f, directionY, 0.0f).color(colorY).normal(0.0f, directionY, 0.0f);
            bufferBuilder.vertex(0.0f, 0.0f,    0.0f).color(colorZ).normal(0.0f, 0.0f, directionZ);
            bufferBuilder.vertex(0.0f, 0.0f, directionZ).color(colorZ).normal(0.0f, 0.0f, directionZ);
            try (BuiltBuffer builtBuffer = bufferBuilder.end()){
                this.debugCrosshairBuffer = RenderSystem
                    .getDevice()
                    .createBuffer(() -> "Crosshair vertex buffer", GpuBuffer.USAGE_VERTEX, builtBuffer.getBuffer());
            }
        }
    }
}
