package com.gmail.inayakitorikhurram.fdmc;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.data.TextureKey;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.util.Identifier;

import java.util.OptionalDouble;

import static net.minecraft.client.gl.RenderPipelines.RENDERTYPE_LINES_SNIPPET;
import static net.minecraft.client.render.RenderPhase.*;

public class FDMCClientConstants {
    //
    public static final TextureKey W_INDICATOR = TextureKey.of("w_indicator");
    public static final RenderPipeline LINES_NO_CULL = RenderPipelines.register(
            RenderPipeline.builder(RENDERTYPE_LINES_SNIPPET)
                    .withLocation(Identifier.of("fdmc", "pipelines/lines_no_cull"))
                    .withCull(false)
                    .withDepthWrite(false)
                    .build()
    );

    public static final RenderLayer.MultiPhase LINES_NO_CULL_PHASE =
            RenderLayer.of("lines", 1536, LINES_NO_CULL,
                    RenderLayer.MultiPhaseParameters.builder()
                            .lineWidth(new RenderPhase.LineWidth(OptionalDouble.empty()))
                            .layering(VIEW_OFFSET_Z_LAYERING)
                            .target(OUTLINE_TARGET)
                            .build(false)
            );
}
