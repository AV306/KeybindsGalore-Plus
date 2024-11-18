package me.av306.keybindsgaloreplus.mixin;

import me.av306.keybindsgaloreplus.KeybindSelectorScreen;
import me.av306.keybindsgaloreplus.KeybindsGalorePlus;
import net.minecraft.client.Keyboard;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.av306.keybindsgaloreplus.KeybindManager;

@Mixin( value = KeyBinding.class )
public abstract class KeyBindingMixin
{
    @Shadow
    private InputUtil.Key boundKey;

    @Shadow
    private String translationKey;

    @Shadow private boolean pressed;


    @Inject( method = "setKeyPressed", at = @At( "HEAD" ), cancellable = true )
    private static void setKeyPressed( InputUtil.Key key, boolean pressed, CallbackInfo ci ) throws Exception
    {
        //throw new RuntimeException();
        if ( KeybindManager.hasConflicts( key ) )
        {
            ci.cancel(); // Cancel to prevent any state change not done by us

            if ( !KeybindManager.isSkippedKey( key ) )
            {
                if ( pressed )
                {
                    // Open menu
                    // Fun fact: changing Screens (which this method does) resets all bindings to "unpressed",
                    // so zoom mods work absolutely fine with us :)
                    KeybindManager.openConflictMenu( key );
                }
                // If released, do nothing
            }
            else if ( KeybindSelectorScreen.USE_KEYBIND_FIX )
            {
                // If the key has no conflicts, and we want to use the keybind conflict fix,
                // transfer the key state to all bindings
                KeybindManager.getConflicts( key ).forEach( binding ->
                {
                    ((KeyBindingAccessor) binding).setPressed( pressed );
                    ((KeyBindingAccessor) binding).setTimesPressed( 1 );
                } );
            }
        }
    }

    // Normally this handles incrementing times pressed
    // Only called when key first goes down
    // "times pressed" is probably used for input accumulation so we prob don't want to mess with that
    @Inject( method = "onKeyPressed", at = @At( "HEAD" ), cancellable = true )
    private static void onKeyPressed( InputUtil.Key key, CallbackInfo ci )
    {
        //KeybindsGalorePlus.LOGGER.info( "onKeyPressed called for {}", key.getTranslationKey() );
        if ( KeybindManager.hasConflicts( key ) /*&& !KeybindManager.isSkippedKey( key )*/ )
            ci.cancel(); // Cancel, because we've sorted out the sub-tick presses (by forcing it to 1)
    }

    // seems to not be called, somehow
    @Inject( method = "setPressed", at = @At("HEAD"), cancellable = true )
    private void setPressed( boolean pressed, CallbackInfo ci )
    {
        KeybindsGalorePlus.LOGGER.info( "setPressed called for keybind {} on physical key {} with value {}", this.translationKey, this.boundKey.getTranslationKey(), pressed );
        if ( KeybindManager.hasConflicts( this.boundKey ) )
            ci.cancel();
    }
}
