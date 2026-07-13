package me.av306.keybindsgaloreplus.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import me.av306.keybindsgaloreplus.Configurations;
import me.av306.keybindsgaloreplus.KeybindManager;
import me.av306.keybindsgaloreplus.KeybindsGalorePlus;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( Screen.class )
public abstract class ScreenMixin extends AbstractContainerEventHandler implements Renderable
{
    @Inject(
            method = "keyPressed",
            at = @At( "HEAD" ),
            cancellable = true
    )
    public void onKeyPressed( int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir )
    {
        // From KeyboardHandlerMixin

        // We only receive key-down events here because keyPress handler filters them for us

        // Stop hardware repeat events from closing a screen opened by click-hold
        if ( KeybindManager.isClickHoldKey( InputConstants.getKey( keyCode, scanCode ) ) )
        {
            KeybindsGalorePlus.debugLog( "\tBlocking hardware key repeat in screen" );
            cir.setReturnValue( false );
        }
    }


    @Shadow
    public Component getTitle() { return null; }

    @Override
    public boolean keyReleased( int keyCode, int scanCode, int modifiers )
    {
        // Clear the click-hold state on (ANY) key release in the screen, because screens
        // consume all the key events while they're open
        if ( KeybindManager.isClickHoldKey( InputConstants.getKey( keyCode, scanCode ) ) )
        {
            if ( Configurations.DEBUG )
            {
                KeybindsGalorePlus.LOGGER.info( "\tClearing click-hold state for key {} from Screen {}",
                        InputConstants.getKey( keyCode, scanCode ).getName(), getTitle().getString() );
            }

            KeybindManager.clearClickHoldKey( InputConstants.getKey( keyCode, scanCode ) );
        }

        return super.keyReleased( keyCode, scanCode, modifiers );
    }
}
