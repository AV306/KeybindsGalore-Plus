package me.av306.keybindsgaloreplus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.InputConstants;

import me.av306.keybindsgaloreplus.mixin.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;


public class KeybindManager
{
    // To HBV007og:
    // I can't thank you enough for all the comments in the code,
    // I was worried I'd have to actually understand every line in every file
    // to do anything!
    // I hope you have fun on your modding/programming travels! :D
    // - Blender (AV306)

    public static final HashMap<Integer, KeyMapping> clickHoldKeys = new HashMap<>();
    public static final HashMap<KeyMapping, Integer> clickHoldRepeatCooldown = new HashMap<>();

    /**
     * Does a given key NOT open a pie menu?
     */
    public static boolean isIgnoredKey( InputConstants.Key key )
    {
        return Configurations.IGNORED_KEYS.contains( key.getValue() ) ^ Configurations.INVERT_IGNORED_KEYS_LIST;
    }

    public static boolean isClickHoldKey( InputConstants.Key key )
    {
        return clickHoldKeys.containsKey( key.getValue() );
    }

    /**
     * Checks if there is a binding conflict on this key, excluding debug keys
     * @param key: The key to check
     */
    public static boolean hasConflictsExcludingDebug( InputConstants.Key key )
    {
        return getMappingsForContext( key ).size() > 1;
    }

    /**
     * Initializes and open the pie menu for the given conflicted key
     */
    public static void openConflictMenu( InputConstants.Key key, List<KeyMapping> mappings )
    {
        KeybindSelectorScreen screen = new KeybindSelectorScreen( key, mappings );
        Minecraft.getInstance().setScreen( screen );
    }

    /**
     * Shortcut method to get conflicts on a key, excluding debug
     */
    public static List<KeyMapping> getMappingsForContext( InputConstants.Key key )
    {
        // Stream-based method takes 7-65 us... good enough?
        // https://stackoverflow.com/questions/24054773/java-8-streams-multiple-filters-vs-complex-condition
        // https://stackoverflow.com/questions/78460866/improve-response-time-java-stream-filter
        return KeyMappingAccessor.getMap().getOrDefault( key, new ArrayList<>() ).stream()
                .filter( keyMapping -> keyMapping.getCategory() != KeyMapping.Category.DEBUG )
                .filter( keyMapping ->
                        (Minecraft.getInstance().player.gameMode().isSurvival()
                                && keyMapping.getCategory() != KeyMapping.Category.CREATIVE
                                && keyMapping.getCategory() != KeyMapping.Category.SPECTATOR)
                        || (Minecraft.getInstance().player.gameMode().isCreative()
                                && keyMapping.getCategory() != KeyMapping.Category.SPECTATOR)
                        || (Minecraft.getInstance().player.gameMode().isBlockPlacingRestricted()
                                && keyMapping.getCategory() != KeyMapping.Category.CREATIVE)
                )
                .toList();
    }

    /**
     * Handle mixin method cancellation and related logic when a conflicted key is pressed
     * @param key: the physical key that was pressed
     * @param pressed: the pressed state of the conflicted key
     * @param ci: CallbackInfo for the mixin
     */
    public static void handleKeyPress( InputConstants.Key key, boolean pressed, CallbackInfo ci )
    {
        KeybindsGalorePlus.debugLog( key.getName() + " pressed: " + pressed );
        if ( !isIgnoredKey( key ) )
        {
            List<KeyMapping> mappings = getMappingsForContext( key );
            if ( mappings.size() > 1 )
            {
                ci.cancel();
                if ( isClickHoldKey( key ) )
                {
                    // TODO: cooldown

                    KeyMapping clickHoldBinding = clickHoldKeys.get( key.getValue() );

                    if ( clickHoldBinding != null )
                    {
                        KeybindsGalorePlus.debugLog( "Activating {} (click-hold)", clickHoldBinding.getName() );
                        ((KeyMappingAccessor) clickHoldBinding).setIsDown( pressed );
                        ((KeyMappingAccessor) clickHoldBinding).setClickCount( pressed ? 1 : 0 );
                    }

                    if ( !pressed )
                    {
                        KeybindsGalorePlus.debugLog( "Deactivating key {} (click-hold)", key.getName() );
                        clickHoldKeys.remove( key.getValue() );
                    }
                }
                else
                {
                    // Key has conflicts, and shouldn't be ignored
                    if ( pressed )
                    {
                        // Conflicts to handle, and was pressed -- open pie menu

                        // Changing Screens (which this method does) resets all bindings to "unpressed",
                        // so zoom mods should work absolutely fine with us :)
                        KeybindsGalorePlus.debugLog( "\tOpening pie menu" );
                        openConflictMenu( key, mappings );
                    }
                    // Conflicts to handle, but key was released -- do nothing
                }
            }
            // else {}
            // No conflicts -- proceed as per vanilla
        }
        // Ignored key -- proceed as per vanilla
    }
}
