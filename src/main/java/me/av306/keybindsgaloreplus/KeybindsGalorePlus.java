package me.av306.keybindsgaloreplus;

import me.av306.keybindsgaloreplus.configmanager.ConfigManager;
import me.av306.keybindsgaloreplus.customdata.DataManager;
import me.av306.keybindsgaloreplus.mixin.KeyBindingAccessor;
import me.av306.keybindsgaloreplus.render.KeybindSelectorElementRenderState;
import me.av306.keybindsgaloreplus.render.KeybindSelectorElementRenderer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;

import java.io.IOException;
import java.net.URI;

import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
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
    private static KeyBinding keyStateReloadKeybind;

    @Override
    public void onInitializeClient()
    {
        LOGGER.info( "KeybindsGalore Plus initialising..." );

        try
        {
            // Initialise ConfigManager and load config file
            configManager = new ConfigManager(
                "KeybindsGalorePlus",    
                FabricLoader.getInstance().getConfigDir(),
                "keybindsgaloreplus_config.properties",
                Configurations.class,
                null
            );

            // There's no good, easy way to enable DEBUG level, so I'm just gonna
            // cram a bunch of if statements around
            LOGGER.info( "Debug mode: {}", Configurations.DEBUG );

            // (debug) Print all config fields
            if ( Configurations.DEBUG )
            {
                this.configManager.printAllConfigs();
            }

            // Initialise custom data manager and read data file
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

            keyStateReloadKeybind = KeyBindingHelper.registerKeyBinding( new KeyBinding(
                    "key.keybindsgaloreplus.reloadkeystate",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    "category.keybindsgaloreplus.keybinds"
            ) );

            // Bind action to config reload key
            ClientTickEvents.END_CLIENT_TICK.register( client ->
            {
                while ( configreloadKeybind.wasPressed() )
                {
                    try
                    {
                        configManager.readConfigFile();
                        customDataManager.readDataFile();
                    }
                    catch ( IOException firstIoe )
                    {
                        client.player.sendMessage( Text.translatable( "text.keybindsgaloreplus.configreloadfail", firstIoe.getMessage() ), false );

                        return;
                    }

                    if ( configManager.errorFlag ) client.player.sendMessage( Text.translatable( "text.keybindsgaloreplus.configerrors" ).formatted( Formatting.RED ), false );
                    if ( customDataManager.hasCustomData ) client.player.sendMessage( Text.translatable( "text.keybindsgaloreplus.customdatafound" ), false );

                    client.player.sendMessage( Text.translatable( "text.keybindsgaloreplus.configreloaded" ), false );

                    if ( Configurations.DEBUG )
                    {
                        // Print all config fields
                        this.configManager.printAllConfigs();                        
                    }
                }

                while ( keyStateReloadKeybind.wasPressed() )
                {
                    KeybindManager.findAllConflicts();
                    client.player.sendMessage(
                            Text.translatable( "text.keybindsgaloreplus.keystatereloaded" ), false );
                }
            } );


        }
        catch ( IOException ioe )
        {
            LOGGER.error( "(KBG+) IOException while reading config file on init!" );
            ioe.printStackTrace();
        }

        // Find conflicts on first world join
        ClientPlayConnectionEvents.JOIN.register( (handler, sender, client) -> KeybindManager.findAllConflicts() );

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

    public static Text createHyperlinkText( URI url )
    {
        return Text.literal( url.toString() )
                .formatted( Formatting.YELLOW )
                .styled( style -> style.withClickEvent( new ClickEvent.OpenUrl( url ) ) );
    }
}
