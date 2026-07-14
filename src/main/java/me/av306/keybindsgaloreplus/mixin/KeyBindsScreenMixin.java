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
        // Check for conflicting keybinds on screen close
        KeybindManager.keysToMappings.clear();
        for ( KeyMapping mapping : this.options.keyMappings )
        {
            KeybindManager.keysToMappings.computeIfAbsent( ((KeyMappingAccessor) mapping).getKey(), (key) -> new ArrayList<>() );
            KeybindManager.keysToMappings.get( ((KeyMappingAccessor) mapping).getKey() ).add( mapping );
        }

        // Prune the hashmap using a copy of its keyset (ensures item removal doesn't affect the list we're iterating over)
        new HashSet<>( KeybindManager.keysToMappings.keySet() ).forEach( ( key) ->
        {
            if ( KeybindManager.keysToMappings.get( key ).size() < 2 )
                KeybindManager.keysToMappings.remove( key );
        } );


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
