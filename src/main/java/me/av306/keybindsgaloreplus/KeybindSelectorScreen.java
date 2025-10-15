/*
 * This class is modified from the PSI mod created by Vazkii
 * Psi Source Code: https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 *
 * HVB007: IDK What Part This credit refers to, if you want to know contact https://github.com/CaelTheColher as he is the maker of this mod
 * I am just updating it to 1.20.x
 */
package me.av306.keybindsgaloreplus;

import static me.av306.keybindsgaloreplus.KeybindsGalorePlus.customDataManager;
import static me.av306.keybindsgaloreplus.render.KeybindSelectorElementRenderer.calculateRadius;

import me.av306.keybindsgaloreplus.mixin.KeyBindingAccessor;
import me.av306.keybindsgaloreplus.mixin.MinecraftClientAccessor;
//import net.minecraft.client.gl.ShaderProgramKeys;
import me.av306.keybindsgaloreplus.render.KeybindSelectorElementRenderState;
import net.minecraft.block.WoodType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.special.SignGuiElementRenderState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.model.Model;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Objects;

// FIXME: pretty much all of this goes into KeybindSelectorElementRenderer
public class KeybindSelectorScreen extends Screen
{
    // Instance variables
    private int ticksInScreen = 0;
    private int selectedSectorIndex = -1;
    private boolean mouseDown = false;

    private final InputUtil.Key conflictedKey;

    private int centreX = 0, centreY = 0;

    private float maxRadius = 0;
    private float maxExpandedRadius = 0;
    private float cancelZoneRadius = 0;

    private boolean isFirstFrame = true;

    private VertexConsumerProvider.Immediate vertexConsumerProvider;

    /** This is probably not going to change while the screen is open, so maybe this optimisation helps? */
    private final ArrayList<KeyBinding> conflicts = new ArrayList<>();

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

    public KeybindSelectorScreen( InputUtil.Key key )
    {
        //this();
        super( NarratorManager.EMPTY );

        this.conflictedKey = key;

        this.conflicts.addAll( KeybindManager.getConflicts( key ) );
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

        if ( Configurations.DEBUG )
        {
            KeybindsGalorePlus.debugLog( "Centre: ({}, {})", this.centreX, this.centreY );
        }
    }

    @Override
    public void render( DrawContext context, int mouseX, int mouseY, float tickDelta )
    {
        // Angle of mouse, in radians from +X-axis, centred on the origin
        double mouseAngle = mouseAngle( this.centreX, this.centreY, mouseX, mouseY );

        float mouseDistanceFromCentre = MathHelper.sqrt( (mouseX - this.centreX) * (mouseX - this.centreX) +
                        (mouseY - this.centreY) * (mouseY - this.centreY) );


        int numberOfSectors = this.conflicts.size(); // How many sectors to make for the pie menu?
        float sectorAngle = (MathHelper.TAU) / numberOfSectors; // Angle occupied by each sector

        // Exact index of selected sector
        this.selectedSectorIndex = (int) (mouseAngle / sectorAngle);

        // Deselect slot if mouse is within cancel zone
        if ( mouseDistanceFromCentre <= this.cancelZoneRadius )
            this.selectedSectorIndex = -1;

        // Need real dimensions of window, not scaled dimensions provided by this.width/height
        context.state.addSpecialElement( new KeybindSelectorElementRenderState(
                tickDelta, numberOfSectors, sectorAngle, this.selectedSectorIndex,
                this.mouseDown, this.ticksInScreen,
                0, 0, this.client.getWindow().getWidth(), this.client.getWindow().getHeight(),
                null
        ) ); // FIXME: getWidth() vs getFrameBufferWidth()?

        this.renderLabelTexts( context, tickDelta, numberOfSectors, sectorAngle );
    }


    // ==================== Rendering methods ====================

    // At least this works fine in 1.21.6.
    private void renderLabelTexts( DrawContext context, float delta, int numberOfSectors, float sectorAngle )
    {
        for ( var sectorIndex = 0; sectorIndex < numberOfSectors; sectorIndex++ )
        {
            float radius = calculateRadius( this.ticksInScreen, delta, numberOfSectors, sectorIndex, this.selectedSectorIndex, this.maxRadius );
            
            float angle = (sectorIndex + 0.5f) * sectorAngle;

            // Position in the middle of the arc
            float xPos = this.centreX + MathHelper.cos( angle ) * radius;
            float yPos = this.centreY + MathHelper.sin( angle ) * radius;

            KeyBinding action = this.conflicts.get( sectorIndex );

            // The biggest nagging bug for me
            // Tells you which control category the action goes in
            // TODO: configurable

            String id = action.getTranslationKey();
            String actionName = Text.translatable( action.getCategory() ).getString() + ": " + Text.translatable( action.getTranslationKey() ).getString();

            // Read custom data for this keybind, only if present
            if ( customDataManager.hasCustomData )
            {
                try
                {
                    if ( customDataManager.customData.get( id ).hideCategory )
                        actionName = Text.translatable( action.getTranslationKey() ).getString();
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

            int textWidth = this.textRenderer.getWidth( actionName );

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

            actionName = (this.selectedSectorIndex == sectorIndex ? Formatting.UNDERLINE : Formatting.RESET) + actionName;

            context.drawText( this.textRenderer, actionName, (int) xPos, (int) yPos, 0xFFFFFFFF,
                    Configurations.LABEL_TEXT_SHADOW );
        }
    }


    // ==================== Others ====================

    // Returns the angle of the line bounded by the given coordinates and the mouse position from the vertical axis
    // This is why we study trigo, guys
    private static double mouseAngle( int x, int y, int mx, int my )
    {
        return (MathHelper.atan2(my - y, mx - x) + Math.PI * 2) % (Math.PI * 2);
    }

    private void closePieMenu()
    {
        this.client.setScreen( null );

        // Activate the selected binding
        if ( this.selectedSectorIndex != -1 )
        {
            KeyBinding selectedKeyBinding = this.conflicts.get( this.selectedSectorIndex );

            KeybindsGalorePlus.debugLog( "Activated {} from pie menu", selectedKeyBinding.getTranslationKey() );

            ((KeyBindingAccessor) selectedKeyBinding).setPressed( true );
            ((KeyBindingAccessor) selectedKeyBinding).setTimesPressed( 1 );
            //((KeyBindingAccessor) bind).invokeSetPressed( true );

            // Attack workaround (very hacky)
            // Abusable??? (FIXME)
            if ( selectedKeyBinding.equals( this.client.options.attackKey ) && Configurations.ENABLE_ATTACK_WORKAROUND )
            {
                KeybindsGalorePlus.debugLog( "\tAttack workaround enabled" );
                ((MinecraftClientAccessor) this.client).setAttackCooldown( 0 );
            }
        }
        else
        {
            KeybindsGalorePlus.debugLog( "Pie menu closed with no selection" );
        }
    }


    // ==================== Overrides // ====================

    @Override
    public void tick()
    {
        // There's literally nothing there. Avoid the jump instructions.
        // super.tick();
        this.ticksInScreen++;
    }

    // These two callbacks work the same as handling it in tick(), plus we get differentiated mouse/keyboard handling
    // Previously, InputUtil.isKeyPressed would throw a GL error when called for a mouse code (0, 1, 2) and return a meaningless value

    @Override
    public boolean keyReleased( int keyCode, int scanCode, int modifiers )
    {
        if ( keyCode == this.conflictedKey.getCode() ) this.closePieMenu();

        return super.keyReleased( keyCode, scanCode, modifiers );
    }

    @Override
    public boolean mouseReleased( double mouseX, double mouseY, int button )
    {
        //this.mouseDown = false;

        if ( button == this.conflictedKey.getCode() )
        {
            // Close menu and activate selection normally – click-hold not applicable
            this.closePieMenu();
        }
        else
        {
            // Click-hold selected binding
            this.client.setScreen( null );
            KeyBinding.unpressAll(); // This stops the other actions from triggering. Not sure why they do in the first place, though.

            if ( this.selectedSectorIndex != -1 )
            {
                KeyBinding binding = this.conflicts.get( this.selectedSectorIndex );

                // Clicked on a sector; add its binding to the click-hold map
                //KeybindsGalorePlus.debugLog( "Activated sector {} (key {}) (click-hold) via pie menu", this.selectedSectorIndex, this.conflictedKey.getCategory() );
                KeybindsGalorePlus.debugLog( "Pie menu closed with click-hold" );
                KeybindManager.clickHoldKeys.put(
                        this.conflictedKey.getCode(),
                        binding
                );

                // Key events are generated repeatedly for keyboard keys held down, but not for mouse buttons,
                // so we have to make one manually
                if ( this.conflictedKey.getCode() <= GLFW.GLFW_MOUSE_BUTTON_LAST )
                    binding.setPressed( true );
            }
            else
            {
                KeybindsGalorePlus.debugLog( "Pie menu closed via click-hold with no selection" );
                // No sector clicked; add null to the click-hold map to signal a cancel
                KeybindManager.clickHoldKeys.put( this.conflictedKey.getCode(), null );
            }
        }

        return super.mouseReleased( mouseX, mouseY, button );
    }

    @Override
    public boolean mouseClicked( double mouseX, double mouseY, int button )
    {
        this.mouseDown = true;

        return super.mouseClicked( mouseX, mouseY, button );
    }

    @Override
    // Don't pause the game when this screen is open
    // actually why not
    public boolean shouldPause() { return false; }


    //* >=1.20.2
    @Override
    public void renderBackground( DrawContext context, int mouseX, int mouseY, float deltaTicks )
    {
        if ( this.client.world == null ) this.renderPanoramaBackground( context, deltaTicks );

        if ( Configurations.BLUR_BACKGROUND ) this.applyBlur( context );
        if ( Configurations.DARKENED_BACKGROUND ) this.renderDarkening( context );
    }

    //* <1.20.2
    // Annoyingly, we can have the method in >1.20.2 but not the super call :(
    //@Override
    // public void renderBackground( DrawContext context ) //* <1.20.2
    // {
    //     //* // ===== Version dependent =====
    //     // Remove the darkened background if needed
    //     // This can help performance, as with all post-processing
    //     if ( Configurations.DARKENED_BACKGROUND ) super.renderBackground( context ); //* <1.20.2
    // }
}
