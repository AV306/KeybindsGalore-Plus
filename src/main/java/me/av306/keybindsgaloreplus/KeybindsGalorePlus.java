package me.av306.keybindsgaloreplus;

import me.av306.keybindsgaloreplus.customdata.DataManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class KeybindsGalorePlus implements ClientModInitializer
{
    public static ConfigManager configManager;
    public static DataManager customDataManager;

    public static final Logger LOGGER = LoggerFactory.getLogger( "keybingsgaloreplus" );

    private static KeyBinding configreloadKeybind;
    @Override
    public void onInitializeClient()
    {
        LOGGER.info( "KeybindsGalore Plus initialising..." );

        try
        {
            // Read configs
            configManager = new ConfigManager(
                "KeybindsGalorePlus",    
                FabricLoader.getInstance().getConfigDir(),
                "keybindsgaloreplus_config.properties",
                KeybindSelectorScreen.class,
                null
            );

            // Read custom data
            customDataManager = new DataManager(
                    FabricLoader.getInstance().getConfigDir(),
                    "keybindsgaloreplus_customdata.data"
            );

            // Set config reload key
            configreloadKeybind = KeyBindingHelper.registerKeyBinding( new KeyBinding(
                    "key.keybindsgaloreplus.reloadconfigs",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "category.keybindsgaloreplus.keybinds"
            ) );

            ClientTickEvents.END_CLIENT_TICK.register( client ->
            {
                while ( configreloadKeybind.wasPressed() )
                {
                    try
                    {
                        configManager.readConfigFile();
                        customDataManager.readDataFile();
                    }
                    catch ( IOException ioe )
                    {
                        client.player.sendMessage( Text.translatable( "text.keybindsgaloreplus.configreloadfail" ) );
                        return;
                    }

                    client.player.sendMessage( Text.translatable( "text.keybindsgaloreplus.configreloaded" ) );
                }

            } );
        }
        catch ( IOException ioe )
        {
            LOGGER.error( "IOException while reading config file!" );
            ioe.printStackTrace();
        }

        // Find conflicts
        ClientPlayConnectionEvents.JOIN.register( (handler, sender, client) ->
        {
            KeybindManager.getAllConflicts();
        } );
    }
}
