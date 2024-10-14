package me.av306.keybindsgaloreplus.mixin;

import me.av306.keybindsgaloreplus.KeybindSelectorScreen;
import me.av306.keybindsgaloreplus.KeybindsGalorePlus;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.av306.keybindsgaloreplus.KeybindManager;

@Mixin( value = KeyBinding.class, priority = -5000 )
public abstract class KeyBindingMixin
{
    @Shadow
    private InputUtil.Key boundKey;

    @Shadow
    private String translationKey;

    @Shadow private boolean pressed;

    /*
     * 
     */
    @Inject( method = "setKeyPressed", at = @At( "HEAD" ), cancellable = true )
    private static void setKeyPressed( InputUtil.Key key, boolean pressed, CallbackInfo ci ) throws Exception
    {
        // Logik
        // I have no idea how this works anymore
        //if ( pressed && (KeybindManager.hasConflicts( key ) || !KeybindSelectorScreen.LAZY_CONFLICT_CHECK && KeybindManager.checkForConflicts( key )))
        KeybindsGalorePlus.LOGGER.info( "setKeyPressed called for {} with value {}", key.getTranslationKey(), pressed );
        if ( KeybindManager.hasConflicts( key ) )
        {
            if ( !KeybindManager.isSkippedKey( key ) )
            {
                if ( pressed )
                {
                    // Don't skip, open menu
                    KeybindManager.openConflictMenu( key );
                    ci.cancel();
                }
            }
            else if ( KeybindSelectorScreen.USE_KEYBIND_FIX )
            {
                // If the key has no conflicts, and we want to use the keybind conflict fix...
                // update all relevant bindings with the key state
                KeybindManager.getConflicts( key ).forEach( binding ->
                {
                    binding.setPressed( pressed );
                    ((KeyBindingAccessor) binding).setTimesPressed( 1 );
                } );
            }
        }
    }

    // Normally this handles incrementining times pressed
    // Only called when key first goes down
    // Note: `setKeyPressed` is NOT called again when the binding is triggered
    // from the pie menu -- the binding's instance `setPressed` method is called,
    // which does NOT trigget the static `setKeyPressed`
    // This method may have been used to prevent the double-trigger,
    // but now it's not necessary anymore (I think)
    /*@Inject( method = "onKeyPressed", at = @At( "HEAD" ), cancellable = true )
    private static void onKeyPressed( InputUtil.Key key, CallbackInfo ci )
    {
        KeybindsGalorePlus.LOGGER.info( "onKeyPressed called for {}", key.getTranslationKey() );
        if ( KeybindManager.hasConflicts( key ) && !KeybindManager.isSkippedKey( key ) )
            {
                ci.cancel();
                KeybindManager.openConflictMenu( key );
            }
        }
    }*/

    // seems to not be called
    @Inject( method = "setPressed", at = @At("HEAD"), cancellable = true )
    private void setPressed( boolean pressed, CallbackInfo ci )
    {
        KeybindsGalorePlus.LOGGER.info( "setPressed called for keybind {} on physical key {} with value {}", this.translationKey, this.boundKey.getTranslationKey(), pressed );
        if ( KeybindManager.hasConflicts( this.boundKey ) )
            ci.cancel();
    }
}
