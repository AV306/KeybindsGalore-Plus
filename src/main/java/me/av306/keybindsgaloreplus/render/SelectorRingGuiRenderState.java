package me.av306.keybindsgaloreplus.render;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;

// TODO: maybe get the element renderer to handle the label texts too?
public record SelectorRingGuiRenderState( int numberOfSectors, int x1, int y1, int x2, int y2, ScreenRect scissorArea, ScreenRect bounds ) implements SpecialGuiElementRenderState
{
    @Override
    public float scale()
    {
        return 1.0f;
    }
}