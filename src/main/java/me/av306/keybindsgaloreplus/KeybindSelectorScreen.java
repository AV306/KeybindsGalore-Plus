/*
 * This class is modified from the PSI mod created by Vazkii
 * Psi Source Code: https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 */
package me.av306.keybindsgaloreplus;

import static me.av306.keybindsgaloreplus.KeybindsGalorePlus.customDataManager;
import static me.av306.keybindsgaloreplus.render.KeybindSelectorElementRenderer.calculateRadius;

import me.av306.keybindsgaloreplus.mixin.KeyMappingAccessor;
import me.av306.keybindsgaloreplus.mixin.MinecraftAccessor;
import me.av306.keybindsgaloreplus.render.KeybindSelectorElementRenderState;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.KeyMapping;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.util.Mth;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

// FIXME: pretty much all of this goes into KeybindSelectorElementRenderer
public class KeybindSelectorScreen extends Screen
{
    // Instance variables
    private int ticksInScreen = 0;
    private int selectedSectorIndex = -1;
    private boolean mouseDown = false;

    private final InputConstants.Key conflictedKey;

    private int centreX = 0, centreY = 0;

    private float maxRadius = 0;
    private float maxExpandedRadius = 0;
    private float cancelZoneRadius = 0;

    private boolean isFirstFrame = true;

    private List<KeyMapping> conflicts;

    /*public KeybindSelectorScreen()
    {
        super( NarratorManager.EMPTY );
        this.mc = MinecraftClient.getInstance();

        // Debug -- print all fields
        for ( var f : this.getClass().getFields() )
        {
            try
            {
                KeybindsGalorePlus.LOGGER.info( "{}: {}", f.getName(), f.get( this ) );
            }
            catch ( IllegalAccessException e )
            {
                KeybindsGalorePlus.LOGGER.warn( e.getMessage() );
            }
        }
    }*/

    public KeybindSelectorScreen( InputConstants.Key key, List<KeyMapping> mappings )
    {
        //this();
        super( Component.translatable( "keybindsgaloreplus.conflict_selector_title",
                key.getDisplayName() ) );

        this.conflictedKey = key;
        this.conflicts = mappings;
    }

    @Override
    protected void init()
    {
        // Set centre of screen
        this.centreX = this.width / 2;
        this.centreY = this.height / 2;

        // These are computed twice, once in here (SCALED window coords) and once in the selector renderer (ABSOLUTE window coords)
        this.maxRadius = Math.min( (this.centreX * Configurations.PIE_MENU_SCALE) - Configurations.PIE_MENU_MARGIN, (this.centreY * Configurations.PIE_MENU_SCALE) - Configurations.PIE_MENU_MARGIN );
        this.maxExpandedRadius = this.maxRadius * Configurations.EXPANSION_FACTOR_WHEN_SELECTED;
        this.cancelZoneRadius = maxRadius * Configurations.CANCEL_ZONE_SCALE;

        //KeybindsGalorePlus.debugLog( "Scaled centre: ({}, {})", this.centreX, this.centreY );
    }

    @Override
    public void extractRenderState( @NotNull GuiGraphicsExtractor context, int mouseX, int mouseY, float tickDelta )
    {
        // Angle of mouse, in radians from +X-axis, centred on the origin
        double mouseAngle = mouseAngle( this.centreX, this.centreY, mouseX, mouseY );

        float mouseDistanceFromCentre = Mth.sqrt( (mouseX - this.centreX) * (mouseX - this.centreX) +
                        (mouseY - this.centreY) * (mouseY - this.centreY) );


        int numberOfSectors = this.conflicts.size(); // How many sectors to make for the pie menu?
        float sectorAngle = (Mth.TWO_PI) / numberOfSectors; // Angle occupied by each sector

        // Exact index of selected sector
        this.selectedSectorIndex = (int) (mouseAngle / sectorAngle);

        // Deselect slot if mouse is within cancel zone
        if ( mouseDistanceFromCentre <= this.cancelZoneRadius )
            this.selectedSectorIndex = -1;

        // Need real dimensions of window, not scaled dimensions provided by this.width/height
        context.guiRenderState.addPicturesInPictureState( new KeybindSelectorElementRenderState(
                tickDelta, numberOfSectors, sectorAngle, this.selectedSectorIndex,
                this.mouseDown, this.ticksInScreen,
                0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(),
                null
        ) ); // FIXME: getWidth() vs getFrameBufferWidth()?

        this.renderLabelTexts( context, tickDelta, numberOfSectors, sectorAngle );

        super.extractRenderState( context, mouseX, mouseY, tickDelta );
    }


    // ==================== Rendering methods ====================

    // At least this works fine in 1.21.6.
    private void renderLabelTexts( GuiGraphicsExtractor context, float delta, int numberOfSectors, float sectorAngle )
    {
        for ( var sectorIndex = 0; sectorIndex < numberOfSectors; sectorIndex++ )
        {
            float radius = calculateRadius( this.ticksInScreen, delta, numberOfSectors, sectorIndex, this.selectedSectorIndex, this.maxRadius );
            
            float angle = (sectorIndex + 0.5f) * sectorAngle;

            // Position in the middle of the arc
            float xPos = this.centreX + Mth.cos( angle ) * radius;
            float yPos = this.centreY + Mth.sin( angle ) * radius;

            KeyMapping action = this.conflicts.get( sectorIndex );

            // The biggest nagging bug for me
            // Tells you which control category the action goes in
            // TODO: configurable

            String id = action.getName();
            String actionName = Component.translatable( action.getCategory().id().toLanguageKey( "key.category" ) ).getString()
                    + ": " + Component.translatable( action.getName() ).getString();

            // Read custom data for this keybind, only if present
            if ( customDataManager.hasCustomData )
            {
                try
                {
                    if ( customDataManager.customData.get( id ).hideCategory )
                        actionName = Component.translatable( action.getName() ).getString();
                }
                catch ( NullPointerException npe )
                {
                    //KeybindsGalorePlus.debugLog( "No hideCategory setting for {}", id );
                }

                try
                {
                    // Assigning `null` doesn't throw an NPE, so we wrap with this to throw one
                    actionName = Objects.requireNonNull( customDataManager.customData.get( id ).displayName );
                }
                catch ( NullPointerException npe )
                {
                    //KeybindsGalorePlus.debugLog( "No custom name for {}", id );
                }
            }

            int textWidth = this.font.width( actionName );

            // Which side of the screen are we on?
            if ( xPos > this.centreX )
            {
                // Right side
                xPos -= Configurations.LABEL_TEXT_INSET;

                // Check text going off-screen
                if ( this.width - xPos < textWidth )
                    xPos -= textWidth - this.width + xPos;
            }
            else
            {
                // Left side
                xPos -= textWidth - Configurations.LABEL_TEXT_INSET;

                // Check text going off-screen
                if ( xPos < 0 ) xPos = Configurations.LABEL_TEXT_INSET;
            }

            // Move the text closer to the centre of the circle
            yPos -= Configurations.LABEL_TEXT_INSET;

            actionName = (this.selectedSectorIndex == sectorIndex ? ChatFormatting.UNDERLINE : ChatFormatting.RESET) + actionName;

            context.text( this.font, actionName, (int) xPos, (int) yPos, 0xFFFFFFFF,
                    Configurations.LABEL_TEXT_SHADOW );
        }
    }


    // ==================== Others ====================

    // Returns the angle of the line bounded by the given coordinates and the mouse position from the vertical axis
    // This is why we study trigo, guys
    private static double mouseAngle( int x, int y, int mx, int my )
    {
        return (Mth.atan2(my - y, mx - x) + Math.PI * 2) % (Math.PI * 2);
    }


    // ==================== Overrides ====================

    @Override
    public void tick()
    {
        // There's literally nothing there. Avoid the jump instructions.
        // super.tick();
        this.ticksInScreen++;
    }

    public void closePieMenu()
    {
        // Nulling the screen also causes all keymappings to be directly set to match physical keyboard state,
        // so this must be done before our overrides
        //this.minecraft.setScreenAndShow( null );
        this.minecraft.gui.setScreen( null );

        // Activate the selected binding
        if ( this.selectedSectorIndex != -1 )
        {
            KeyMapping selectedKeyBinding = this.conflicts.get( this.selectedSectorIndex );

            KeybindsGalorePlus.debugLog( "Activated {} from pie menu", selectedKeyBinding.getName() );

            // Technically, we could use the public setDown method, but I'd rather not,
            // in case Mojang adds extra stuff to it
            KeybindManager.setKeyMapping( selectedKeyBinding, true );
            //((KeyBindingAccessor) bind).invokeSetPressed( true );

            // Attack workaround (very hacky)
            // Abusable??? Might trigger anticheat??? FIXME
            if ( selectedKeyBinding.same( this.minecraft.options.keyAttack ) && Configurations.ENABLE_ATTACK_WORKAROUND )
            {
                KeybindsGalorePlus.debugLog( "\tAttack workaround enabled" );
                ((MinecraftAccessor) this.minecraft).setMissTime( 0 );
            }
        }
        else
        {
            KeybindsGalorePlus.debugLog( "Pie menu closed with no selection" );
        }
    }

    // These two callbacks work the same as handling it in tick(), plus we get differentiated mouse/keyboard handling
    // Previously, InputUtil.isKeyPressed would throw a GL error when called for a mouse code (0, 1, 2) and return a meaningless valu


    @Override
    public boolean keyReleased( @NotNull KeyEvent keyEvent )
    {
        if ( InputConstants.getKey( keyEvent ) == this.conflictedKey )
        {
            this.closePieMenu();
            //return true;
        }

        return super.keyReleased( keyEvent );
    }

    @Override
    public boolean mouseClicked( @NotNull MouseButtonEvent mouseButtonEvent, boolean bl )
    {
        this.mouseDown = true;

        return super.mouseClicked( mouseButtonEvent, bl );
    }

    @Override
    public boolean mouseReleased( @NotNull MouseButtonEvent mouseButtonEvent )
    {
        if ( mouseButtonEvent.button() == this.conflictedKey.getValue() )
        {
            // Close menu and activate selection normally - click-hold not applicable
            this.closePieMenu();
        }
        else
        {
            // Click-hold selected binding

            // Null the screen (keymappings are updated to match physical state as a side effect,
            // so all conflicts on this key will be set to "down"
            this.minecraft.gui.setScreen( null );

            // This line unsets the conflicts on this key (see above), but isn't needed now that the root cause
            // (call to KeyMapping.setAll() when mouse is grabbed) is fixed to ignore conflicts
            //KeyMapping.releaseAll(); // This stops the other actions from triggering. Not sure why they do in the first place, though.

            if ( this.selectedSectorIndex != -1 )
            {
                KeyMapping binding = this.conflicts.get( this.selectedSectorIndex );

                // Clicked on a sector; add its binding to the click-hold map
                //KeybindsGalorePlus.debugLog( "Activated sector {} (key {}) (click-hold) via pie menu", this.selectedSectorIndex, this.conflictedKey.getCategory() );
                KeybindsGalorePlus.debugLog( "Pie menu closed with click-hold for {}", binding.getName() );

                // This adds the key and mapping to the click-hold map, and also sets the cooldown
                KeybindManager.registerClickHoldKey( this.conflictedKey, binding );

                // Hardware repeat events are generated repeatedly for keyboard keys held down, but not for mouse buttons

                // This is technically unnecessary now that the KeyMapping.setAll() call in when mouse is grabbed after we close
                // is fixed to set ONLY our desired mapping to match physical key state (pressed).

                // HOWEVER, setAll() only applies for mappings with type KEYSYM (see KeyMapping.shouldRestoreState())
                // This ALSO doesn't work for KEYSYM mappings that are consumed via consumeClick() because setAll()
                // only does setDown() but consumeClick() needs a click() call
                
                // Net result is that we'll just call setDown() and click() for everything here,
                // even though setDown() will be called again later when the screen closes
                // and then again when the hardware repeat comes in.

                //if ( this.conflictedKey.getValue() <= GLFW.GLFW_MOUSE_BUTTON_LAST )
                //binding.setDown( true );
                KeybindManager.setKeyMapping( binding, true );
            }
            else
            {
                KeybindsGalorePlus.debugLog( "Pie menu closed via click-hold with no selection" );
                
                // No sector clicked; add null to the click-hold map to signal a cancel
                // This prevents the pie menu from opening again till the key is released
                // No cooldown here since the user should be able to just press again
                // to open the menu

                // By right, we should also set the cooldown,
                // but by left, the handler ignores null entries and doesn't check the cooldown
                KeybindManager.clickHoldKeys.put( this.conflictedKey, null );
            }
        }

        return super.mouseReleased( mouseButtonEvent );
    }

    @Override
    public boolean isPauseScreen() { return false; }

    // @Override
    // public boolean isInGameUi()
    // {
    //     return !Configurations.BLUR_BACKGROUND;
    // }

    /*@Override
    protected void extractBlurredBackground( final GuiGraphicsExtractor graphics )
    {
        if ( Configurations.BLUR_BACKGROUND )
            super.extractBlurredBackground( graphics );
    }*/
}
