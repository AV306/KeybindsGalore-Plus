package me.av306.keybindsgaloreplus.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.InputConstants;
import me.av306.keybindsgaloreplus.Configurations;
import me.av306.keybindsgaloreplus.KeybindManager;
import me.av306.keybindsgaloreplus.KeybindsGalorePlus;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin( KeyboardHandler.class )
public class KeyboardHandlerMixin
{
    @WrapOperation(
            method = "keyPress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z"
            )
    )
    public boolean wrapScreenKeyPressed( Screen instance, KeyEvent keyEvent, Operation<Boolean> original )
    {
        // We only get key-down events because keyPress handler filters them for us
        // Stop hardware repeat events from closing a screen opened by click-hold
        if ( KeybindManager.isClickHoldKey( InputConstants.getKey( keyEvent ) ) )
        {
            KeybindsGalorePlus.debugLog( "\tBlocking hardware key repeat in screen" );
            return false;
        }
        else return original.call( instance, keyEvent );
    }

    @WrapOperation(
            method = "keyPress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;keyReleased(Lnet/minecraft/client/input/KeyEvent;)Z"
            )
    )
    public boolean wrapScreenKeyReleased( Screen instance, KeyEvent keyEvent, Operation<Boolean> original )
    {
        // Clear the click-hold state on (ANY) key release in the screen, because screens
        // consume all the key events while they're open
        if ( KeybindManager.isClickHoldKey( InputConstants.getKey( keyEvent ) ) )
        {
            if ( Configurations.DEBUG )
            {
                KeybindsGalorePlus.LOGGER.info( "\tClearing click-hold state for key {} from Screen {}",
                        InputConstants.getKey( keyEvent ).getName(), instance.getTitle().getString() );
            }

            KeybindManager.clickHoldKeys.remove( InputConstants.getKey( keyEvent ) );
        }

        return original.call( instance, keyEvent );
    }
}
