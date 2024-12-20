package me.av306.keybindsgaloreplus;

import me.av306.keybindsgaloreplus.configmanager.ConfigManager;
import me.av306.keybindsgaloreplus.customdata.DataManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;

import java.io.IOException;

import net.minecraft.util.Formatting;
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

            // There's no good, easy way to enable DEBUG level so I'm just gonna
            // cram a bunch of if statements around
            LOGGER.info( "Debug mode: {}", KeybindSelectorScreen.DEBUG );

            //LOGGER.info( KeybindSelectorScreen.SKIPPED_KEYS.getClass().getName() );

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
                        //client.player.sendMessage( Text.translatable( "text.keybindsgaloreplus.configreloadfail" ) );
                        client.player.sendMessage( Text.translatable( "text.keybindsgaloreplus.configreloadfail" ), false );
                        try
                        {
                            configManager.checkConfigFile();
                        }
                        catch ( IOException ioe2 )
                        {
                            client.player.sendMessage(
                                    Text.translatable( "text.keybindsgaloreplus.configreloadfailagain" ).append(
                                            Text.literal( "https://github.com/AV306/KeybindsGalore-Plus/blob/master/src/main/resources/keybindsgaloreplus_config.properties" )
                                                    .formatted( Formatting.YELLOW )
                                                    .styled( style -> style.withClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, "https://github.com/AV306/KeybindsGalore-Plus/blob/master/src/main/resources/keybindsgaloreplus_config.properties" ) ) )
                                    )
                            );
                        }
                        return;
                    }

                    //client.player.sendMessage( Text.translatable( "text.keybindsgaloreplus.configreloaded" ) );
                    client.player.sendMessage( Text.translatable( "text.keybindsgaloreplus.configreloaded" ), false );

                }

            } );
        }
        catch ( IOException ioe )
        {
            LOGGER.error( "IOException while reading config file!" );
            ioe.printStackTrace();
        }

        // Find conflicts on first world join
        ClientPlayConnectionEvents.JOIN.register( (handler, sender, client) ->
        {
            KeybindManager.getAllConflicts();
        } );
    }
}
