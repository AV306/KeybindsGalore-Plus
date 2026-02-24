package me.av306.keybindsgaloreplus.mixin;

import me.av306.keybindsgaloreplus.KeybindsGalorePlus;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.av306.keybindsgaloreplus.KeybindManager;

@Mixin( value = KeyMapping.class )
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
        KeybindsGalorePlus.debugLog( "setKeyPressed( {}, {} ) called", key.getName(), pressed );

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

        if ( KeybindManager.hasConflictsExcludingDebug( key ) /*&& !KeybindManager.isSkippedKey( key )*/ )
        {
            KeybindsGalorePlus.debugLog( "\tCancelling sub-tick accumulation" );

            ci.cancel(); // Cancel, because we've sorted out sub-tick presses (by setting it to 1)
        }
    }


    // Very thankfully, this is gone now!
    // Theoretically, this should be called ALL THE TIME
    // which it *is*, but ONLY IN A NON-DEV ENVIRONMENT, somehow
//    @Inject( method = "setPressed", at = @At( "HEAD" ), cancellable = true )
//    private void setPressed( boolean pressed, CallbackInfo ci )
//    {
//        //KeybindsGalorePlus.debugLog( "setPressed( {} ) called for keybind {} on physical key {}", pressed, this.translationKey, this.boundKey.getTranslationKey() );
//
//        // I can't demonstrate that this actually causes issues (setPressed( true ) only happened for the mouse when I tried)
//        // but it has potential for duplicating the handleKeyPress call, since setKeyPressed is *supposed* to call setPressed...
//        // Not calling handleKeyPress may cause https://github.com/AV306/KeybindsGalore-Plus/issues/10 though
//        //KeybindManager.handleKeyPress( this.boundKey, pressed, ci );
//    }


    @Inject( method = "setDown", at = @At( "HEAD" ), cancellable = true )
    private void onSetDown( boolean down, CallbackInfo ci )
    {
        if ( KeybindManager.isClickHoldKey( ((KeyMappingAccessor) this).getKey() ) )
            ci.cancel();
    }
}
