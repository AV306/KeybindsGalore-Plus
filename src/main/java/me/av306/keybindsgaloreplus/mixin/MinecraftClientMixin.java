package me.av306.keybindsgaloreplus.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( MinecraftClient.class )
public class MinecraftClientMixin
{
    @Inject(
            at = @At( "HEAD" ),
            method = "handleInputEvents()V"
    )
    private void onHandleInputEvents( CallbackInfo ci )
    {
        // Open Inventory, among other things, is handled here, away from the rest of the keybinds
        // On my laptop, open inventory overrides whatever is chosen, but on my pc, it works fine...
        // Testing purposes only
        //throw new RuntimeException();
    }
}
