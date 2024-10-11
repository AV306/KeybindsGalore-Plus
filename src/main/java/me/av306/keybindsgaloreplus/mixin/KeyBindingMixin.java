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
        if ( pressed && KeybindManager.hasConflicts( key ) )
                ci.cancel();
    }

    @Inject( method = "onKeyPressed", at = @At( "HEAD" ), cancellable = true )
    private static void onKeyPressed( InputUtil.Key key, CallbackInfo ci )
    {
        KeybindsGalorePlus.LOGGER.info( "onKeyPressed called for {}", key.getTranslationKey() );
        if ( !KeybindManager.isSkippedKey( key ) && KeybindManager.hasConflicts( key ) )
        {
            ci.cancel();
            KeybindManager.openConflictMenu( key );
        }

        if ( KeybindSelectorScreen.USE_KEYBIND_FIX )
        {
            // Activate all relevant bindings

        }
    }

    @Inject( method = "setPressed", at = @At("HEAD"), cancellable = true )
    private void setPressed( boolean pressed, CallbackInfo ci )
    {
        KeybindsGalorePlus.LOGGER.info( "setPressed called for keybind {} on physical key {} with value {}", this.translationKey, this.boundKey.getTranslationKey(), pressed );
        if ( KeybindManager.hasConflicts( this.boundKey ) )
            ci.cancel();
    }
}
