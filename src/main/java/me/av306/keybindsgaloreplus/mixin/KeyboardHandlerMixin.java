package me.av306.keybindsgaloreplus.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.InputConstants;
import me.av306.keybindsgaloreplus.Configurations;
import me.av306.keybindsgaloreplus.KeybindManager;
import me.av306.keybindsgaloreplus.KeybindsGalorePlus;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.gui.screens.Screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin( KeyboardHandler.class )
public class KeyboardHandlerMixin
{
    // FIXME: These mixins all don't apply and I'm not sure why
    // I think it has something to do with the fact that the keyPressed/Released calls
    // are being used to set a boolean *array* in 1.21.1, and MixinExtras isn't exactly clear
    // on how to write the callback signature for that case.
    // I'm going to mixin the Screen super method directly.
//    @WrapOperation(
//            method = "keyPress",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/client/gui/screens/Screen;keyPressed(III)Z"
//            )
//    )
//    private void wrapScreenKeyPressed( Screen instance, int keyCode, int scanCode, int modifiers, Operation<Boolean> original )
//    {
//        // We only get key-down events because keyPress handler filters them for us
//
//        // Stop hardware repeat events from closing a screen opened by click-hold
//        if ( KeybindManager.isClickHoldKey( InputConstants.getKey( keyCode, scanCode ) ) )
//        {
//            KeybindsGalorePlus.debugLog( "\tBlocking hardware key repeat in screen" );
//            return false;
//        }
//        else return original.call( instance, keyCode, scanCode, modifiers );
//    }
//
//    @WrapOperation(
//            method = "keyPress",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/client/gui/screens/Screen;keyReleased(III)Z"
//            )
//    )
//    private boolean wrapScreenKeyReleased( Screen instance, int keyCode,
//            int scanCode, int modifiers, Operation<Boolean> original )
//    {
//        // Clear the click-hold state on (ANY) key release in the screen, because screens
//        // consume all the key events while they're open
//        if ( KeybindManager.isClickHoldKey( InputConstants.getKey( keyCode, scanCode ) ) )
//        {
//            if ( Configurations.DEBUG )
//            {
//                KeybindsGalorePlus.LOGGER.info( "\tClearing click-hold state for key {} from Screen {}",
//                        InputConstants.getKey( keyCode, scanCode ).getName(), instance.getTitle().getString() );
//            }
//
//            KeybindManager.clearClickHoldKey( InputConstants.getKey( keyCode, scanCode ) );
//        }
//
//        return original.call( instance, keyCode, scanCode, modifiers );
//    }
}
