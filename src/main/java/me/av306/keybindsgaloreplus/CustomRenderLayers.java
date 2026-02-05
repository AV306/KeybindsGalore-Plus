package me.av306.keybindsgaloreplus;

import net.minecraft.client.renderer.rendertype.RenderType;

public class CustomRenderLayers
{
    public static final RenderType.MultiPhase GUI_TRIANGLE_STRIP = RenderType.create(
            "keybindsgaloreplus:gui_triangle_strip", RenderType.CUTOUT_BUFFER_SIZE,
            false, true,
            CustomRenderPipelines.GUI_TRIANGLE_STRIP,
            RenderType.MultiPhaseParameters.builder()
                    //.layering( RenderPhase.NO_LAYERING )
                    //.target( RenderPhase.MAIN_TARGET )
                    .build( false ) );
}
