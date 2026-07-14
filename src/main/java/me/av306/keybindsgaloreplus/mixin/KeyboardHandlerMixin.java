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
    // Big thanks to Ellie, just tweh, and TorNato on the Fabric Discord
    // for help with the mixins!

    // Also for reference -- how to find synthetic methods in VSCode:
    // 1. genSources
    // 2. javap -cp ... -c -v -l -p ...
    // 3. search for target instructions
    // 4. synthetic method will have an intermediary name, don't be surprised

    @WrapOperation(
            // 1.21.1 has the stuff in a lambda so we don't target keyPress directly
            method = "method_1454",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;keyPressed(III)Z"
            )
    )
    private static boolean wrapScreenKeyPressed( Screen instance,
            int keyCode, int scanCode, int modifiers, Operation<Boolean> original )
    {
        // We only get key-down events because keyPress handler filters them for us

        // Stop hardware repeat events from closing a screen opened by click-hold
        if ( KeybindManager.isClickHoldKey( InputConstants.getKey( keyCode, scanCode ) ) )
        {
            KeybindsGalorePlus.debugLog( "\tBlocking hardware key repeat in screen" );
            return false;
        }
        else return original.call( instance, keyCode, scanCode, modifiers );
    }
    
    @WrapOperation(
            method = "method_1454",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;keyReleased(III)Z"
            )
    )
    private static boolean wrapScreenKeyReleased( Screen instance, int keyCode,
            int scanCode, int modifiers, Operation<Boolean> original )
    {
        // Clear the click-hold state on (ANY) key release in the screen, because screens
        // consume all the key events while they're open
        if ( KeybindManager.isClickHoldKey( InputConstants.getKey( keyCode, scanCode ) ) )
        {
            if ( Configurations.DEBUG )
            {
                KeybindsGalorePlus.LOGGER.info( "\tClearing click-hold state for key {} from Screen {}",
                        InputConstants.getKey( keyCode, scanCode ).getName(), instance.getTitle().getString() );
            }

            KeybindManager.clearClickHoldKey( InputConstants.getKey( keyCode, scanCode ) );
        }

        return original.call( instance, keyCode, scanCode, modifiers );
    }
}
