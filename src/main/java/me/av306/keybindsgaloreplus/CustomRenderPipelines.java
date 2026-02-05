package me.av306.keybindsgaloreplus;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.resources.Identifier;

public class CustomRenderPipelines
{
    public static final RenderPipeline GUI_TRIANGLE_STRIP = RenderPipeline.builder(
            RenderPipelines.GUI_SNIPPET )
            .withLocation( Identifier.parse( "keybindsgaloreplus:pipeline/gui_tristrip" ) )
            .withVertexFormat( DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP )
            .withCull( false )
            /*.withDepthWrite( false )
            .withDepthTestFunction( DepthTestFunction.NO_DEPTH_TEST )*/
            .build();
}
