package me.av306.keybindsgaloreplus.mixin;

import me.av306.keybindsgaloreplus.Configurations;
import me.av306.keybindsgaloreplus.KeybindManager;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static me.av306.keybindsgaloreplus.KeybindsGalorePlus.LOGGER;

import java.util.ArrayList;
import java.util.HashSet;

@Mixin( KeyBindsScreen.class )
public abstract class KeyBindsScreenMixin extends OptionsSubScreen
{
    public KeyBindsScreenMixin( Screen parent, Options gameOptions, Component title )
    {
        super( parent, gameOptions, title );
    }

    @Override
    public void onClose()
    {
        KeybindManager.findAllConflicts( this.options );

        if ( Configurations.DEBUG )
        {
            //LOGGER.info(
            KeybindManager.keysToMappings.forEach( (key1, keyMappings) ->
            {
                LOGGER.info( "{}:", key1.getName() );
                keyMappings.forEach( keyMapping -> LOGGER.info( "\t{}", keyMapping.getName() ) );
            } );
        }
        super.onClose();
    }
}
