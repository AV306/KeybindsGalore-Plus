package me.av306.keybindsgaloreplus.render;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public class CustomRenderLayers
{
    public static final RenderType GUI_TRIANGLE_STRIP = RenderType.create(
            "keybindsgaloreplus:gui_triangle_strip",
            RenderSetup.builder( CustomRenderPipelines.GUI_TRIANGLE_STRIP )
                    .createRenderSetup()
    );
}
