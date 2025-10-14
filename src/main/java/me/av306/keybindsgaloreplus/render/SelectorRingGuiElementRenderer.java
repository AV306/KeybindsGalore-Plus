package me.av306.keybindsgaloreplus.render;

import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class SelectorRingGuiElementRenderer extends SpecialGuiElementRenderer<SelectorRingGuiRenderState>
{
    public SelectorRingGuiElementRenderer( VertexConsumerProvider.Immediate vertexConsumerProvider )
    {
        super( vertexConsumerProvider );
    }

    @Override
    protected void render( SelectorRingGuiRenderState state, MatrixStack matrices )
    {

    }

    @Override
    protected String getName()
    {
        return "KeybindsGalorePlus Selector Ring Renderer";
    }

    @Override
    public Class<SelectorRingGuiRenderState> getElementClass()
    {
        return SelectorRingGuiRenderState.class;
    }
}