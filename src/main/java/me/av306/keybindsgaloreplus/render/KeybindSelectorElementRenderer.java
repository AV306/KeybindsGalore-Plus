package me.av306.keybindsgaloreplus.render;

import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import me.av306.keybindsgaloreplus.Configurations;
import me.av306.keybindsgaloreplus.KeybindsGalorePlus;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.Mth;

public class KeybindSelectorElementRenderer extends PictureInPictureRenderer<KeybindSelectorElementRenderState>
{
    @Override
    protected void renderToTexture( KeybindSelectorElementRenderState state,
                                    @NotNull PoseStack matrices,
                                    @NotNull SubmitNodeCollector nodeCollector )
    {
        //VertexConsumer buffer = this.bufferSourcegetBuffer( CustomRenderLayers.GUI_TRIANGLE_STRIP );

        nodeCollector.submitCustomGeometry( matrices, CustomRenderLayers.GUI_TRIANGLE_STRIP, (pose, buffer) ->
        {
            int centreX = (state.x1() - state.x0()) / 2;
            int centreY = (state.y1() - state.y0()) / 2;

            //KeybindsGalorePlus.debugLog( "Absolute size: ({}, {})", s, centreY );
            //KeybindsGalorePlus.debugLog( "Absolute centre: ({}, {})", centreX, centreY );

            int numberOfSectors = state.numberOfSectors();
            float sectorAngle = state.sectorAngle();
            int selectedSectorIndex = state.selectedSectorIndex();
            float delta = state.tickDelta();

            // TODO: document the fact that these configs are defined in absolute pixel coords, not scaled coords (like text)
            // These are computed twice, once in here (ABSOLUTE window coords) and once in the screen (SCALED window coords)
            float maxRadius = Math.min( (centreX * Configurations.PIE_MENU_SCALE) - Configurations.PIE_MENU_MARGIN,
                    (centreY * Configurations.PIE_MENU_SCALE) - Configurations.PIE_MENU_MARGIN );
            //float maxExpandedRadius = maxRadius * Configurations.EXPANSION_FACTOR_WHEN_SELECTED;
            float cancelZoneRadius = maxRadius * Configurations.CANCEL_ZONE_SCALE;

            float currentAngle = 0;
            int numberOfVerticesPerSector = Configurations.CIRCLE_VERTICES / numberOfSectors; // FP truncation here
            if ( numberOfVerticesPerSector < 1 ) numberOfVerticesPerSector = 1; // Make sure there's always at least 2 vertices for a visible trapezium

            // Render each sector in turn
            for ( var currentDrawnSectorIndex = 0; currentDrawnSectorIndex < numberOfSectors; currentDrawnSectorIndex++ )
            {
                float outerRadius = calculateRadius( state.ticksInScreen(), delta,
                        numberOfSectors, currentDrawnSectorIndex,
                        state.selectedSectorIndex(), maxRadius );

                float innerRadius = cancelZoneRadius;
                int innerColor = Configurations.PIE_MENU_COLOR;
                int outerColor = Configurations.PIE_MENU_COLOR;

                // TODO: read custom data
                /*if ( customDataManager.hasCustomData )
                {
                    try
                    {
                        outerColor = customDataManager.customData.get( this.conflicts.get( sectorIndex ).getTranslationKey() ).sectorColor;
                    }
                    catch ( NullPointerException ignored )
                    {
                        //KeybindsGalorePlus.debugLog( "No custom sector colour for {}", this.conflicts.get( sectorIndex ).getTranslationKey() );
                    }
                }*/

                // Lighten every other sector
                if ( currentDrawnSectorIndex % 2 == 0 )
                    innerColor = outerColor += Configurations.PIE_MENU_COLOR_LIGHTEN_FACTOR;

                if ( selectedSectorIndex == currentDrawnSectorIndex )
                {
                    innerRadius *= Configurations.EXPANSION_FACTOR_WHEN_SELECTED;
                    outerColor = state.mouseDown()
                            ? Configurations.PIE_MENU_HIGHLIGHT_COLOR
                            : Configurations.PIE_MENU_SELECT_COLOR;
                }

                if ( !Configurations.SECTOR_GRADATION ) innerColor = outerColor;

                this.writeSectorVertices( buffer, centreX, centreY, currentAngle, sectorAngle,
                        numberOfVerticesPerSector, innerRadius, outerRadius, innerColor, outerColor );

                currentAngle += sectorAngle;
            }
        } );
    }

    private void writeSectorVertices( VertexConsumer buf, int centreX, int centreY, float startAngle, float sectorAngle, int vertices, float innerRadius,
                                      float outerRadius, int innerColor, int outerColor )
    {
        for ( var i = 0; i <= vertices; i++ )
        {
            float angle = startAngle + ((float) i / vertices) * sectorAngle;

            // Inner vertex
            buf.addVertex( centreX + Mth.cos( angle ) * innerRadius,
                    centreY + Mth.sin( angle ) * innerRadius, 0 );
            buf.setColor( innerColor >> 16 & 0xFF, innerColor >> 8 & 0xFF,
                    innerColor & 0xFF, Configurations.PIE_MENU_ALPHA );

            // Outer vertex
            buf.addVertex( centreX + Mth.cos( angle ) * outerRadius,
                    centreY + Mth.sin( angle ) * outerRadius, 0 );
            buf.setColor( outerColor >> 16 & 0xFF, outerColor >> 8 & 0xFF,
                    outerColor & 0xFF, Configurations.PIE_MENU_ALPHA );
        }
    }

    public static float calculateRadius( int ticksInScreen, float delta, int numberOfSectors, int sectorIndex, int selectedSectorIndex, float maxRadius )
    {
        float radius = Configurations.ANIMATE_PIE_MENU ?
                Math.max( 0f, Math.min( (ticksInScreen + delta - sectorIndex * 6f / numberOfSectors) * 40f, maxRadius ) )
                : maxRadius;

        // Expand the sector if selected
        if ( selectedSectorIndex == sectorIndex ) radius *= Configurations.EXPANSION_FACTOR_WHEN_SELECTED;

        return radius;
    }

    @Override
    protected String getTextureLabel()
    {
        return "KeybindsGalorePlus Keybind Selector Renderer";
    }

    @Override
    public Class<KeybindSelectorElementRenderState> getRenderStateClass()
    {
        return KeybindSelectorElementRenderState.class;
    }
}