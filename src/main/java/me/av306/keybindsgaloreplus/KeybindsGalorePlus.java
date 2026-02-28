package me.av306.keybindsgaloreplus;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import me.av306.liteconfig.ConfigManager;

//import org.jetbrains.annotations.NonNull;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.blaze3d.platform.InputConstants;

import me.av306.keybindsgaloreplus.customdata.DataManager;
import me.av306.keybindsgaloreplus.render.KeybindSelectorElementRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class KeybindsGalorePlus implements ClientModInitializer
{
    public static ConfigManager CONFIG_MANAGER;
    public static DataManager customDataManager;

    public static final String MODID = "keybindsgaloreplus";
    public static final Logger LOGGER = LoggerFactory.getLogger( MODID );

    private static KeyMapping CONFIG_RELOAD_KEYBIND;
    // The final translation key will be "key.category.keybindsgaloreplus.keybinds"
    private static final KeyMapping.Category MOD_KEYBIND_CATEGORY
            = KeyMapping.Category.register( Identifier.fromNamespaceAndPath( MODID, "keybinds" ) );

    public static ChatFormatting ERROR_FORMATTING = ChatFormatting.RED;

    @Override
    public void onInitializeClient()
    {
        LOGGER.info( "KeybindsGalore Plus initialising..." );

        // Check for old configuration file and migrate (?)
        if ( Files.exists( FabricLoader.getInstance().getConfigDir().resolve( "keybindsgaloreplus_config.properties" ) ) )
        {
            LOGGER.info( "Found old config file; will migrate it to the new format." );
            try
            {
                Path oldConfigPath = FabricLoader.getInstance().getConfigDir().resolve( "keybindsgaloreplus_config.properties" );
                Path newConfigPath = FabricLoader.getInstance().getConfigDir().resolve( "keybindsgaloreplus.properties" );

                var lines = Files.readAllLines( oldConfigPath );
                
                for ( int i = 0; i < lines.size(); i++ )
                {
                    String line = lines.get( i );

                    if ( !line.startsWith( "#" ) && line.contains( "=" ) )
                    {
                        String[] parts = line.split( "=", 2 );
                        if ( parts.length == 2 )
                        {
                            String newLine = parts[0].toUpperCase() + "=" + parts[1];
                            LOGGER.info( "Migrating config entry: {} -> {}", line, newLine );
                            lines.set( i, newLine );
                        }
                        else
                        {
                            LOGGER.warn( "Skipping invalid config line during migration: {}", line );
                        }
                    }
                }
                
                Files.write( newConfigPath, lines );
                Files.delete( oldConfigPath );

                LOGGER.info( "Successfully migrated old configuration file!" );
            }
            catch ( IOException e )
            {
                LOGGER.warn( "Failed to migrate old configuration file: {}", e.getLocalizedMessage() );
            }
        }

        // Initialise ConfigManager and load config file
        CONFIG_MANAGER = new ConfigManager(
            FabricLoader.getInstance().getConfigDir().resolve( "keybindsgaloreplus.properties" ),
            Configurations.class,
            null
        );
        KeybindsGalorePlus.LOGGER.info( "Loading configuration file..." );
        reloadConfigurations( Minecraft.getInstance() );

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
            // Every tick, decrement all click-hold cooldowns
            KeybindManager.clickHoldKeysCooldowns.replaceAll( ((key, cooldown) ->
            {
                if ( cooldown > 0 ) return cooldown - 1;
                else return cooldown;
            } ) );

            // Handle configuration reload keybind
            while ( CONFIG_RELOAD_KEYBIND.consumeClick() )
            {
                reloadConfigurations( client );
                reloadCustomData( client );

                if ( Configurations.DEBUG )
                {
                    client.player.displayClientMessage( Component.translatable( "text.keybindsgaloreplus.debugmode.enabled" ), false );
                    // Log all config fields in debug mode
                    CONFIG_MANAGER.printAllConfigs();
                }
            }
        } );

        // Register commands
        ClientCommandRegistrationCallback.EVENT.register( (dispatcher, registryAccess) ->
        {
            dispatcher.register(
                ClientCommandManager.literal( MODID )
                        .executes( context -> 
                        {
                            // Fancy version text :D
                            var metadata = FabricLoader.getInstance().getModContainer( MODID )
                                    .orElseThrow().getMetadata();
                            String versionString = metadata.getVersion().getFriendlyString();

                            // FIXME: sendFeedback vs displayMessage?
                            context.getSource().sendFeedback( Component.translatable( "text.keybindsgaloreplus.command.main.title" )
                                    .withStyle( ChatFormatting.GOLD, ChatFormatting.BOLD ) );
                            context.getSource().sendFeedback( Component.translatable( "text.keybindsgaloreplus.command.main.version", versionString )
                                    .withStyle( ChatFormatting.GRAY, ChatFormatting.ITALIC ) );
                            context.getSource().sendFeedback( Component.translatable( "text.keybindsgaloreplus.command.main.debug", Configurations.DEBUG )
                                    .withStyle( ChatFormatting.GRAY, ChatFormatting.ITALIC ) );
                            return 1;
                        } )
                        .then( ClientCommandManager.literal( "reload_configs" )
                                .executes( context ->
                                {
                                    reloadConfigurations( context.getSource().getClient() );
                                    reloadCustomData( context.getSource().getClient() );
                                    return 1;
                                } )
                        )
            );
        } );

        // Register our fancy circle renderer
        SpecialGuiElementRegistry.register(
                ctx -> new KeybindSelectorElementRenderer( ctx.vertexConsumers() ) );
    }

    private void reloadCustomData( @NonNull Minecraft client )
    {
        customDataManager.readDataFile();
        if ( customDataManager.hasCustomData )
        {
            LOGGER.info( "Successfully loaded custom keybind data!" );
            if ( client.player != null ) client.player.displayClientMessage( Component.translatable( "text.keybindsgaloreplus.customdata.found" ), false );
        }
    }

    private void reloadConfigurations( @NonNull Minecraft client )
    {
        try
        {
            // FIXME: liteconfig needs to also return the number of errors for this method
            if ( CONFIG_MANAGER.deserialiseConfigurationFileOrElseCreateNew() )
            {
                LOGGER.info( "Created new configuration file with default values." );
                if ( client.player != null ) client.player.displayClientMessage(
                        Component.translatable( "text.keybindsgaloreplus.configurations.load.created_new" ), false );
            }
            else
            {
                LOGGER.info( "Successfully loaded configuration file!" );
                if ( client.player != null ) client.player.displayClientMessage(
                       Component.translatable( "text.keybindsgaloreplus.configurations.load.success" ), false );
            }
        }
        catch ( IOException e )
        {
            LOGGER.error( "IOException while loading configuration file: {}", e.getLocalizedMessage() );
            LOGGER.warn( "Will use default configuration values." );

            if ( client.player != null ) client.player.displayClientMessage(
                    Component.translatable( "text.keybindsgaloreplus.configurations.load.fail.ioexception" )
                            .withStyle( ERROR_FORMATTING )
                            .append( Component.literal( e.getLocalizedMessage() ) ),
                    false
            );
        }
    }


    public static void debugLog( String message )
    {
        if ( Configurations.DEBUG ) LOGGER.info( message );
    }

    public static void debugLog( String message, Object... objects )
    {
        if ( Configurations.DEBUG ) LOGGER.info( message, objects );
    }

    public static Component createHyperlinkText( URI url )
    {
        return Component.literal( url.toString() )
                .withStyle( ChatFormatting.YELLOW )
                .withStyle( style -> style.withClickEvent( new ClickEvent.OpenUrl( url ) ) );
    }
}
