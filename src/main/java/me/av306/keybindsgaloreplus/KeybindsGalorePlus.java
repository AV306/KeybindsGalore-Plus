package me.av306.keybindsgaloreplus;

import java.io.IOException;
import java.net.URI;

import me.av306.liteconfig.ConfigManager;
import me.av306.liteconfig.exceptions.InvalidConfigurationEntryException;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.blaze3d.platform.InputConstants;

import me.av306.keybindsgaloreplus.customdata.DataManager;
import me.av306.keybindsgaloreplus.render.KeybindSelectorElementRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class KeybindsGalorePlus implements ClientModInitializer
{
    public static ConfigManager CONFIG_MANAGER;
    public static DataManager customDataManager;

    public static final Logger LOGGER = LoggerFactory.getLogger( "keybingsgaloreplus" );

    private static KeyMapping CONFIG_RELOAD_KEYBIND;
    // The final translation key will be "key.category.keybindsgaloreplus.keybinds"
    private static final KeyMapping.Category MOD_KEYBIND_CATEGORY
            = KeyMapping.Category.register( Identifier.fromNamespaceAndPath( "keybindsgaloreplus", "keybinds" ) );

    @Override
    public void onInitializeClient()
    {
        LOGGER.info( "KeybindsGalore Plus initialising..." );

        // Initialise ConfigManager and load config file
        CONFIG_MANAGER = new ConfigManager(
            FabricLoader.getInstance().getConfigDir().resolve( "keybindsgaloreplus_config.properties" ),
            Configurations.class,
            null
        );

        KeybindsGalorePlus.LOGGER.info( "Loading configuration file..." );
        try
        {
            if ( CONFIG_MANAGER.deserialiseConfigurationFileOrElseCreateNew() )
                KeybindsGalorePlus.LOGGER.info( "Created new configuration file!" );
            else KeybindsGalorePlus.LOGGER.info( "Loaded existing configuration file!" );
        }
        catch ( IOException e )
        {
            KeybindsGalorePlus.LOGGER.error( "Failed to load configuration file: {}", e.getLocalizedMessage() );
            KeybindsGalorePlus.LOGGER.warn( "Will use default configurations" );
        }

        // There's no good, easy way to enable DEBUG level, so I'm just gonna
        // cram a bunch of if statements around
        LOGGER.info( "Debug mode: {}", Configurations.DEBUG );

        // (debug) Print all config fields
        if ( Configurations.DEBUG )
        {
            CONFIG_MANAGER.printAllConfigs();
        }

        // Initialise custom data manager and read data file
        customDataManager = new DataManager(
                FabricLoader.getInstance().getConfigDir(),
                "keybindsgaloreplus_customdata.data"
        );

        // Set config reload key
        CONFIG_RELOAD_KEYBIND = KeyBindingHelper.registerKeyBinding( new KeyMapping(
                    "key.keybindsgaloreplus.reloadconfigs",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                MOD_KEYBIND_CATEGORY
        ) );

        // Bind action to config reload key
        ClientTickEvents.END_CLIENT_TICK.register( client ->
        {
            while ( CONFIG_RELOAD_KEYBIND.consumeClick() )
            {
                try
                {
                    if ( CONFIG_MANAGER.deserialiseConfigurationFileOrElseCreateNew() )
                    {
                        client.player.displayClientMessage(
                                Component.translatable( "text.keybindsgaloreplus.configfilecreated" ), false );
                    }
                    else client.player.displayClientMessage(
                            Component.translatable( "text.keybindsgaloreplus.configfileloaded" ), false );
                }
                catch ( IOException e )
                {
                    client.player.displayClientMessage(
                            Component.translatable( "text.keybindsgaloreplus.configreloadfail", e.getMessage() ),
                            false
                    );
                }
                catch ( InvalidConfigurationEntryException | NumberFormatException e )
                {
                    client.player.displayClientMessage(
                            Component.translatable( "text.keybindsgaloreplus.configfileerror", e.getMessage() )
                                    .withStyle( ChatFormatting.RED ),
                            false
                    );
                }

                customDataManager.readDataFile();
                if ( customDataManager.hasCustomData ) client.player.displayClientMessage( Component.translatable( "text.keybindsgaloreplus.customdatafound" ), false );

                if ( Configurations.DEBUG )
                {
                    // Print all config fields
                    CONFIG_MANAGER.printAllConfigs();
                }
            }
        } );

        // Register our fancy circle renderer
        SpecialGuiElementRegistry.register(
                ctx -> new KeybindSelectorElementRenderer( ctx.vertexConsumers() ) );
    }


    public static void debugLog( String message )
    {
        if ( Configurations.DEBUG ) LOGGER.info( "(KBG+ DEBUG) " + message );
    }

    public static void debugLog( String message, Object... objects )
    {
        if ( Configurations.DEBUG ) LOGGER.info( "(KBG+ DEBUG) " + message, objects );
    }

    public static Component createHyperlinkText( URI url )
    {
        return Component.literal( url.toString() )
                .withStyle( ChatFormatting.YELLOW )
                .withStyle( style -> style.withClickEvent( new ClickEvent.OpenUrl( url ) ) );
    }
}
