package me.av306.keybindsgaloreplus.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( InventoryScreen.class )
public class InventoryScreenMixin
{
    @Inject(
            at = @At( "TAIL" ),
            method = "<init>"
    )
    private void init( PlayerEntity playerEntity, CallbackInfo ci )
    {
        // Testing purposes only
        //MinecraftClient
        //throw new RuntimeException();
    }
}
