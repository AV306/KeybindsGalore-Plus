package me.av306.keybindsgaloreplus.render;

import me.av306.keybindsgaloreplus.Configurations;
import me.av306.keybindsgaloreplus.CustomRenderLayers;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;

import static me.av306.keybindsgaloreplus.KeybindsGalorePlus.LOGGER;
import static me.av306.keybindsgaloreplus.KeybindsGalorePlus.customDataManager;

public class KeybindSelectorElementRenderer extends SpecialGuiElementRenderer<KeybindSelectorElementRenderState>
{
    public KeybindSelectorElementRenderer( VertexConsumerProvider.Immediate vertexConsumerProvider )
    {
        super( vertexConsumerProvider );
    }

    @Override
    protected void render( KeybindSelectorElementRenderState state, MatrixStack matrices )
    {
        VertexConsumer buffer = this.vertexConsumers.getBuffer( CustomRenderLayers.GUI_TRIANGLE_STRIP );
        //VertexConsumer buffer = this.vertexConsumers.getBuffer( RenderLayer.getDebugTriangleFan() );

        int numberOfSectors = state.numberOfSectors();
        float sectorAngle = state.sectorAngle();
        int selectedSectorIndex = state.selectedSectorIndex();
        float delta = state.tickDelta();

        int centreX = state.screenWidth() / 2;
        int centreY = state.screenHeight() / 2;

        float maxRadius = Math.min( (centreX * Configurations.PIE_MENU_SCALE) - Configurations.PIE_MENU_MARGIN,
                (centreY * Configurations.PIE_MENU_SCALE) - Configurations.PIE_MENU_MARGIN );
        float maxExpandedRadius = maxRadius * Configurations.EXPANSION_FACTOR_WHEN_SELECTED;
        float cancelZoneRadius = maxRadius * Configurations.CANCEL_ZONE_SCALE;

        float startAngle = 0;
        int vertices = Configurations.CIRCLE_VERTICES / numberOfSectors; // FP truncation here
        if ( vertices < 1 ) vertices = 1; // Make sure there's always at least 2 vertices for a visible trapezium
        for ( var currentDrawnSectorIndex = 0;
              currentDrawnSectorIndex < numberOfSectors; currentDrawnSectorIndex++ )
        {
            float outerRadius = calculateRadius( state.ticksInScreen(), delta,
                    numberOfSectors, currentDrawnSectorIndex,
                    state.selectedSectorIndex(), maxRadius );
            float innerRadius = cancelZoneRadius;
            int innerColor = Configurations.PIE_MENU_COLOR;
            int outerColor = Configurations.PIE_MENU_COLOR;

            // TODO
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

            this.drawSector( buffer, centreX, centreY, startAngle, sectorAngle,
                    vertices, innerRadius, outerRadius, innerColor, outerColor );

            startAngle += sectorAngle;
        }
        this.vertexConsumers.draw();
    }

    private void drawSector( VertexConsumer buf, int centreX, int centreY, float startAngle, float sectorAngle, int vertices, float innerRadius,
                             float outerRadius, int innerColor, int outerColor )
    {
        for ( var i = 0; i <= vertices; i++ )
        {
            float angle = startAngle + ((float) i / vertices) * sectorAngle;

            // ===== Version dependent =====
            // Inner vertex
            buf.vertex( centreX + MathHelper.cos( angle ) * innerRadius,
                    centreY + MathHelper.sin( angle ) * innerRadius, 0 );
            buf.color( innerColor >> 16 & 0xFF, innerColor >> 8 & 0xFF,
                    innerColor & 0xFF, Configurations.PIE_MENU_ALPHA );

            // Outer vertex
            buf.vertex( centreX + MathHelper.cos( angle ) * outerRadius,
                    centreY + MathHelper.sin( angle ) * outerRadius, 0 );
            buf.color( outerColor >> 16 & 0xFF, outerColor >> 8 & 0xFF,
                    outerColor & 0xFF, Configurations.PIE_MENU_ALPHA );
        }
    }

    private float calculateRadius( int ticksInScreen, float delta, int numberOfSectors, int sectorIndex, int selectedSectorIndex, float maxRadius )
    {
        float radius = Configurations.ANIMATE_PIE_MENU ?
                Math.max( 0f, Math.min( (ticksInScreen + delta - sectorIndex * 6f / numberOfSectors) * 40f, maxRadius ) )
                : maxRadius;

        // Expand the sector if selected
        if ( selectedSectorIndex == sectorIndex ) radius *= Configurations.EXPANSION_FACTOR_WHEN_SELECTED;

        return radius;
    }

    @Override
    protected String getName()
    {
        return "KeybindsGalorePlus Keybind Selector Ring Renderer";
    }

    @Override
    public Class<KeybindSelectorElementRenderState> getElementClass()
    {
        return KeybindSelectorElementRenderState.class;
    }
}