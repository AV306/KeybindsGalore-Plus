package me.av306.keybindsgaloreplus.render;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;

// TODO: maybe get the element renderer to handle the label texts too?
public record KeybindSelectorElementRenderState( float tickDelta,
                                                 int numberOfSectors, float sectorAngle, int selectedSectorIndex,
                                                 boolean mouseDown, int ticksInScreen,
                                                 int x0, int y0, int x1, int y1,
                                                 @Nullable ScreenRectangle scissorArea,
                                                 @Nullable ScreenRectangle bounds )
        implements PictureInPictureRenderState
{
    public KeybindSelectorElementRenderState( float tickDelta,
                                              int numberOfSectors, float sectorAngle, int selectedSectorIndex,
                                              boolean mouseDown, int ticksInScreen,
                                              int x0, int y0, int x1, int y1,
                                              @Nullable ScreenRectangle scissorArea )
    {
        this( tickDelta, numberOfSectors, sectorAngle, selectedSectorIndex, mouseDown,
                ticksInScreen, x0, y0, x1, y1, scissorArea,
                PictureInPictureRenderState.getBounds( x1, y1, x1, y1, scissorArea ) );
    }
    @Override
    public float scale()
    {
        return 1.0f;
    }
}