package me.av306.keybindsgaloreplus;

import net.minecraft.client.render.RenderLayer;

public class CustomRenderLayers
{
    public static final RenderLayer.MultiPhase GUI_TRIANGLE_STRIP = RenderLayer.of(
            "keybindsgaloreplus:gui_triangle_strip", RenderLayer.CUTOUT_BUFFER_SIZE,
            false, true,
            CustomRenderPipelines.GUI_TRIANGLE_STRIP,
            RenderLayer.MultiPhaseParameters.builder()
                    //.layering( RenderPhase.NO_LAYERING )
                    //.target( RenderPhase.MAIN_TARGET )
                    .build( false ) );
}
