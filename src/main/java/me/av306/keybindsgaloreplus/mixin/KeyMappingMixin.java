package me.av306.keybindsgaloreplus.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.av306.keybindsgaloreplus.KeybindsGalorePlus;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.av306.keybindsgaloreplus.KeybindManager;

@Mixin( KeyMapping.class )
public abstract class KeyMappingMixin
{
    @Shadow
    protected InputConstants.Key key;

    @Shadow private boolean isDown;

    // Call trace is:
    // GLFWKeyCallbackI.callback -> ... -> KeyboardHandler.method_22678 -> KeyboardHandler.keyPress -> KeyMapping.set
    // Screens only get key up events (see KeyboardHandler#L504
    @Inject( method = "set", at = @At( "HEAD" ), cancellable = true )
    private static void setKeyPressed( InputConstants.Key key, boolean pressed, CallbackInfo ci ) throws Exception
    {
        // KeyboardHandler L583 calls this with "true" during regular gameplay;
        // L548 calls this with false;
        // L515 calls this with false (related to the click-hold gui bug.)
        // As best as I can tell, a duplicate key pressed event is sent to the screen on click-hold (L512),
        // which the screen then interprets as telling it to close => screen set to null => mouse grabbed (and screen set to null again
        // then the rest of the KeyboardHandler keyPress handler (L515) (for the same pressed event) calls set( false ).
        // This duplicate event is most likely the hardware key repeat.
        KeybindsGalorePlus.debugLog( "set( key: {}, pressed: {} ) called", key.getName(), pressed );

        // Handle key
        KeybindManager.handleKeyPress( key, pressed, ci );
    }

    // Call trace is:
    // GLFWKeyCallbackI.callback -> ... -> KeyboardHandler.method_22678 -> KeyboardHandler.keyPress -> KeyMapping.click
    // Normally this handles incrementing times pressed; only called when key first goes down
    // "times pressed" is used for sub-tick input accumulation
    @Inject( method = "click", at = @At( "HEAD" ), cancellable = true )
    private static void onKeyPressed( InputConstants.Key key, CallbackInfo ci )
    {
        KeybindsGalorePlus.debugLog( "onKeyPressed( {} ) called", key.getName() );

        if ( KeybindManager.hasConflicts( key ) /*&& !KeybindManager.isSkippedKey( key )*/ )
        {
            KeybindsGalorePlus.debugLog( "\tCancelling sub-tick accumulation" );

            ci.cancel(); // Cancel, because we've sorted out sub-tick presses (by setting it to 1)
        }
    }

//    @Inject( method = "setDown", at = @At( "HEAD" ) )
//    private void onSetDown( boolean down, CallbackInfo ci )
//    {
//        KeybindsGalorePlus.LOGGER.info( "{} setDown {} from below stacktrace:", ((KeyMapping) (Object) this).getName(), down );
//        Thread.dumpStack();
//    }

    @WrapWithCondition(
            method = "setAll",
            at = @At( value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;setDown(Z)V" )
    )
    private static boolean setAllNonConflictingKeys( KeyMapping keyMapping, boolean down )
    {
        // FIXME: might be better to wrap shouldSetOnIngameFocus?
        // Only called in MouseHandler.grabMouse() so cost should be fine
        // Prevent conflicted keymappings from being (re-)set to match physical key state
        // when screens close and key states are restored (See KeybindManager)
        InputConstants.Key targetKey = ((KeyMappingAccessor) keyMapping).getKey();
        // Allow mappings on click-hold keys to be updated (see below)

        //KeybindsGalorePlus.LOGGER.info( "KeyMapping {} {} be allowed to be set to match physical state",
        //        keyMapping.getName(), result ? "will" : "won't" );

        // For click-hold keys, we could technically rely on hardware repeat to call set() and update the desired keymapping
        // but since vanilla doesn't do that (when mouse is grabbed, all keymappings are updated to match
        // physical key state though they could also have waited for hardware repeats), we shan't either
        return KeybindManager.isIgnoredKey( targetKey ) // Allow mappings on ignored keys to be updated
                || !KeybindManager.hasConflicts( targetKey ) // Prevent mappings on conflicted keys from being updated
                || KeybindManager.clickHoldKeys.containsValue( keyMapping );
    }
}
