package me.av306.keybindsgaloreplus;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;

public class CustomRenderLayers
{
    public static final RenderLayer.MultiPhase GUI = RenderLayer.of(
            "keybindsgaloreplus:immediate_gui", 2048, false, true,
            CustomRenderPipelines.GUI_TRISTRIP,
            RenderLayer.MultiPhaseParameters.builder()
                    //.layering( RenderPhase.NO_LAYERING )
                    //.target( RenderPhase.MAIN_TARGET )
                    .build( false ) );
}
