package me.av306.keybindsgaloreplus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.lwjgl.glfw.GLFW;
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


    /**
     * Maps physical keys to a list of bindings they can trigger.
     * Only contains keys bound to more than one binding.
     * <br>
     * Compatibility mods may add other bindings (e.g. from another mod's keybind manager) here,
     * but must not make changes to existing values.
     */
    // TODO: remove this; we can use vanilla's
    public static final Hashtable<InputConstants.Key, List<KeyMapping>> conflictTable = new Hashtable<>();

    public static final HashMap<Integer, KeyMapping> clickHoldKeys = new HashMap<>();

    /**
     * FInd all conflicts on all keys known to the vanilla keybind manager
     */
    // TODO: this is no longer necessary
    public static void findAllConflicts()
    {
        KeybindsGalorePlus.LOGGER.info( "(KBG+) Performing lazy conflict check" );

        Minecraft client = Minecraft.getInstance();

        // Clear map
        conflictTable.clear();

        // Iterate over all bindings, adding them to the list under its assigned physical key
        for ( KeyMapping keybinding : client.options.keyMappings )
        {
            InputConstants.Key physicalKey = ((KeyMappingAccessor) keybinding).getKey();

            // Skip unbound keys — keys are usually only bound to KEY_UNKNOWN when they are "unbound"
            if ( physicalKey.getValue() == GLFW.GLFW_KEY_UNKNOWN ) continue;

            //KeybindsGalorePlus.LOGGER.info( "Adding {} to list for physical key {}", keybinding.getName(), physicalKey.getName() );

            // Create a new list if the key doesn't have one
            conflictTable.computeIfAbsent( physicalKey, key -> new ArrayList<>() );

            // Add the binding to the list held by the physical key
            conflictTable.get( physicalKey ).add( keybinding );
        }

        // Prune the hashtable, copying its keys before pruning
        new HashSet<>( conflictTable.keySet() ).forEach( key ->
        {
            // Remove all entries for physical keys with less than 2 bindings (they don't have conflicts)
            if ( conflictTable.get( key ).size() < 2 )
                conflictTable.remove( key );
        } );

        // Debug -- prints the resulting hashtable
        if ( Configurations.DEBUG )
        {
            KeybindsGalorePlus.LOGGER.info( "Dumping key conflict table" );
            conflictTable.values().forEach( list -> list.forEach( binding -> KeybindsGalorePlus.LOGGER.info( "\t{} bound to physical key {}", binding.getName(), ((KeyMappingAccessor) binding).getKey() ) ) );
        }
    }

    /**
     * Does a given key NOT open a pie menu? (
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
     * Checks if there is a binding conflict on this key
     * @param key: The key to check
     */
    public static boolean hasConflicts( InputConstants.Key key )
    {
        return conflictTable.containsKey( key );
    }

    /**
     * Initializes and open the pie menu for the given conflicted key
     */
    public static void openConflictMenu( InputConstants.Key key )
    {
        KeybindSelectorScreen screen = new KeybindSelectorScreen( key );   
        Minecraft.getInstance().setScreen( screen );
    }

    /**
     * Shortcut method to get conflicts on a key
     */
    public static List<KeyMapping> getConflicts( InputConstants.Key key )
    {
        return conflictTable.get( key );
    }

    /**
     * Handle mixin method cancellation and related logic when a conflicted key is pressed
     * @param key: the physical key that was pressed
     * @param pressed: the pressed state of the conflicted key
     * @param ci: CallbackInfo for the mixin
     */
    public static void handleKeyPress( InputConstants.Key key, boolean pressed, CallbackInfo ci )
    {
        if ( hasConflicts( key ) )
        {
            if ( isClickHoldKey( key ) )
            {
                ci.cancel();

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
            else if ( !isIgnoredKey( key ) )
            {
                // Key has conflicts, and shouldn't be ignored

                ci.cancel();

                if ( pressed )
                {
                    // Conflicts to handle, and was pressed -- open pie menu

                    // Changing Screens (which this method does) resets all bindings to "unpressed",
                    // so zoom mods should work absolutely fine with us :)
                    KeybindsGalorePlus.debugLog( "\tOpening pie menu" );

                    openConflictMenu( key );
                }
                // Conflicts to handle, but key was released -- do nothing
            }
            else if ( Configurations.USE_KEYBIND_FIX )
            {
                // Key conflicts ignored, and should use fixed behaviour
                ci.cancel();

                // Transfer key state to all bindings on the key
                getConflicts( key ).forEach( binding ->
                {
                    KeybindsGalorePlus.debugLog( "\tVanilla fix, {} key {}", pressed ? "enabling" : "disabling", binding.getName() );

                    if ( pressed )
                    {
                        ((KeyMappingAccessor) binding).setIsDown( true );
                        ((KeyMappingAccessor) binding).setClickCount( 1 );
                    }
                    // We can't simply pass "false" to the previous branch, becuase wasPressed() will return true
                    // as long as timesPressed > 0, even if pressed == false.
                    else ((KeyMappingAccessor) binding).invokeRelease();

                } );
            }
            //else {}
            // Conflicts ignored, and vanilla behaviour is ok -- proceed as per vanilla
        }
        // else {}
        // No conflicts -- proceed as per vanilla
    }
}
