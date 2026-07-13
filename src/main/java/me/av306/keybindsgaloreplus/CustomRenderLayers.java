package me.av306.keybindsgaloreplus;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.CullStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;

public class CustomRenderLayers
{
    public static final RenderType GUI_TRIANGLE_STRIP = RenderType.create(
            "keybindsgaloreplus:gui_triangle_strip",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.TRIANGLE_STRIP,
            16777216,
            CompositeState.builder()
                    .setCullState( CullStateShard.NO_CULL )
                    .createCompositeState( false )

    );
}
