package me.av306.keybindsgaloreplus.mixin;

import me.av306.keybindsgaloreplus.KeybindSelectorScreen;
import static me.av306.keybindsgaloreplus.KeybindSelectorScreen.DEBUG;
import me.av306.keybindsgaloreplus.KeybindsGalorePlus;
import static me.av306.keybindsgaloreplus.KeybindsGalorePlus.LOGGER;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
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

    @Shadow @Final
    private String translationKey;

    @Shadow private boolean pressed;

    @Shadow
    public abstract void setPressed( boolean pressed );


    @Inject( method = "setKeyPressed", at = @At( "HEAD" ), cancellable = true )
    private static void setKeyPressed( InputUtil.Key key, boolean pressed, CallbackInfo ci ) throws Exception
    {
        if ( DEBUG )
            LOGGER.info( "setKeyPressed called for key {} with pressed state {}", key.getTranslationKey(), pressed );

        if ( KeybindManager.hasConflicts( key ) )
        {
            if ( !KeybindManager.isSkippedKey( key ) )
            {
                ci.cancel();
                if ( pressed )
                {
                    // Open menu
                    // Fun fact: changing Screens (which this method does) resets all bindings to "unpressed",
                    // so zoom mods work absolutely fine with us :)
                    if ( KeybindSelectorScreen.DEBUG )
                        LOGGER.info( "\tOpened pie menu" );

                    KeybindManager.openConflictMenu( key );
                }
                // If released, do nothing
            }
            else if ( KeybindSelectorScreen.USE_KEYBIND_FIX )
            {
                ci.cancel();

                // If the key has no conflicts, and we want to use the keybind conflict fix,
                // transfer the key state to all bindings
                KeybindManager.getConflicts( key ).forEach( binding ->
                {
                    if ( KeybindSelectorScreen.DEBUG )
                        KeybindsGalorePlus.LOGGER.info( "\tVanilla fix, enabling key {}", binding.getTranslationKey() );


                    ((KeyBindingAccessor) binding).setPressed( pressed );
                    ((KeyBindingAccessor) binding).setTimesPressed( 1 );
                } );
            }
            //else {}
            // Otherwise, proceed as per vanilla
        }
        // else {}
        // No conflicts -- proceed as per vanilla
    }

    // Normally this handles incrementing times pressed
    // Only called when key first goes down
    // "times pressed" is probably used for input accumulation so we prob don't want to mess with that
    @Inject( method = "onKeyPressed", at = @At( "HEAD" ), cancellable = true )
    private static void onKeyPressed( InputUtil.Key key, CallbackInfo ci )
    {
        if ( DEBUG ) LOGGER.info( "onKeyPressed called for {}", key.getTranslationKey() );

        if ( KeybindManager.hasConflicts( key ) /*&& !KeybindManager.isSkippedKey( key )*/ )
        {
            if ( DEBUG ) LOGGER.info( "\tCancelling sub-tick accumulation" );

            ci.cancel(); // Cancel, because we've sorted out the sub-tick presses (by forcing it to 1)
        }
    }

    // seems to not be called, somehow
    // except for this one guy https://github.com/AV306/KeybindsGalore-Plus/issues/10
    @Inject( method = "setPressed", at = @At("HEAD"), cancellable = true )
    private void setPressed( boolean pressed, CallbackInfo ci )
    {
        KeybindsGalorePlus.LOGGER.warn( "setPressed called for keybind {} on physical key {} with value {}", this.translationKey, this.boundKey.getTranslationKey(), pressed );
        MinecraftClient.getInstance().player.sendMessage(
                Text.translatable( "text.keybindsgaloreplus.setpressedhappened" ).append( Text.literal( "https://github.com/AV306/KeybindsGalore-Plus" )
                        .formatted( Formatting.AQUA )
                        .styled( style -> style.withClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, "https://github.com/AV306/KeybindsGalore-Plus" ) ) )
                )

        );


        if ( KeybindManager.hasConflicts( this.boundKey ) )
        {
            if ( DEBUG ) LOGGER.info( "\tCancelled" );
            ci.cancel();
        }
    }
}
