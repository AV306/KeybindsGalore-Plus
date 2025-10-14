package me.av306.keybindsgaloreplus.render;

import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import org.jetbrains.annotations.Nullable;

// TODO: maybe get the element renderer to handle the label texts too?
public record KeybindSelectorElementRenderState( float tickDelta,
                                                 int numberOfSectors, float sectorAngle, int selectedSectorIndex,
                                                 boolean mouseDown, int ticksInScreen,
                                                 int x1, int y1, int x2, int y2,
                                                 @Nullable ScreenRect scissorArea,
                                                 @Nullable ScreenRect bounds )
        implements SpecialGuiElementRenderState
{
    public KeybindSelectorElementRenderState( float tickDelta,
                                              int numberOfSectors, float sectorAngle, int selectedSectorIndex,
                                              boolean mouseDown, int ticksInScreen,
                                              int x1, int y1, int x2, int y2,
                                              @Nullable ScreenRect scissorArea )
    {
        this( tickDelta, numberOfSectors, sectorAngle, selectedSectorIndex, mouseDown,
                ticksInScreen, x1, y1, x2, y2, scissorArea,
                SpecialGuiElementRenderState.createBounds( x1, y1, x2, y2, scissorArea ) );
    }
    @Override
    public float scale()
    {
        return 1.0f;
    }
}