package me.av306.keybindsgaloreplus;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class CustomRenderPipelines
{
    public static final RenderPipeline GUI_TRIANGLE_STRIP = RenderPipeline.builder(
            RenderPipelines.GUI_SNIPPET )
            .withLocation( Identifier.of( "keybindsgaloreplus:pipeline/gui_tristrip" ) )
            .withVertexFormat( VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP )
            .withCull( false )
            .withDepthWrite( false )
            .withDepthTestFunction( DepthTestFunction.NO_DEPTH_TEST )
            .build();
}
