package me.av306.keybindsgaloreplus.render;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class CustomRenderPipelines
{
    public static final RenderPipeline GUI_TRIANGLE_STRIP
                = RenderPipelines.register( RenderPipeline.builder( RenderPipelines.GUI_SNIPPET )
                    .withLocation( Identifier.parse( "keybindsgaloreplus:pipeline/gui_tristrip" ) )
                    .withVertexBinding( 0, DefaultVertexFormat.POSITION_COLOR )
                    .withPrimitiveTopology( PrimitiveTopology.TRIANGLE_STRIP )
                    .withCull( false )
                    /*.withDepthWrite( false )
                    .withDepthTestFunction( DepthTestFunction.NO_DEPTH_TEST )*/
                    .build()
            );
}
