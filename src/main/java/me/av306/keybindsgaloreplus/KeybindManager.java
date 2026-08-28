package me.av306.keybindsgaloreplus;

import com.mojang.blaze3d.platform.InputConstants;
import me.av306.keybindsgaloreplus.mixin.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.GameType;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class KeybindManager
{
    public static final HashMap<InputConstants.Key, KeyMapping> clickHoldKeys = new HashMap<>();
    public static final HashMap<InputConstants.Key, Integer> clickHoldKeysCooldowns = new HashMap<>();

    /**
     * Does a given key NOT open a pie menu?
     */
    public static boolean isIgnoredKey( InputConstants.Key key )
    {
        return Configurations.IGNORED_KEYS.contains( key.getValue() ) ^ Configurations.INVERT_IGNORED_KEYS_LIST;
    }

    public static boolean isClickHoldKey( InputConstants.Key key )
    {
        return clickHoldKeys.containsKey( key );
    }

    public static void registerClickHoldKey( InputConstants.Key key, @Nullable KeyMapping keyMapping )
    {
        clickHoldKeys.put( key, keyMapping );
        clickHoldKeysCooldowns.put( key, Configurations.CLICK_HOLD_REPEAT_COOLDOWN );
    }

    public static void registerClickHoldKeyNoCooldown( InputConstants.Key key, @Nullable KeyMapping keyMapping )
    {
        clickHoldKeys.put( key, keyMapping );
        clickHoldKeysCooldowns.put( key, 0 );
    }

    public static void clearClickHoldKey( InputConstants.Key key )
    {
        clickHoldKeys.remove( key );
        // There is technically no need to remove the cooldown,
        // since it'll be set back to maximum when the click-hold key is registered again
        // However, iteration over the map when checking the cooldown is O(n)
        // so we should keep n small
        clickHoldKeysCooldowns.remove( key );
    }

    /**
     * Checks if there is a binding conflict on this key, excluding debug keys
     * @param key The key to check
     */
    public static boolean hasConflicts( InputConstants.Key key )
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
        // I get a noticeable FPS drop on the first frame of the menu opening...
        // We could possibly optimise by caching each gamemode context's mapping list when the keybind menu closes
        // or if we're daring, have only one cache and update it when gamemode changes (but that involves more hooks)
        // Neither method is sustainable if we want to add more contexts :/
        // FIXME: measure
        // Another caveat is that we must maintain the order of keymappings, in order to get deterministic and consistent
        // ordering of sectors in the menu. So either we use only sequential streams, or we sort after a parallel stream...
        if ( Minecraft.getInstance().player == null ) return Collections.emptyList();

        // player.gameMode() comes from the tab list and can be null (e.g. proxied servers)
        final GameType gameType;
        if ( Minecraft.getInstance().player.gameMode() != null )
            gameType = Minecraft.getInstance().player.gameMode();
        else if ( Minecraft.getInstance().gameMode != null )
            gameType = Minecraft.getInstance().gameMode.getPlayerMode();
        else
            return Collections.emptyList();

        return KeyMappingAccessor.getMap().getOrDefault( key, new ArrayList<>() ).stream()
                .filter( keyMapping -> keyMapping.getCategory() != KeyMapping.Category.DEBUG )
                .filter( keyMapping ->
                        (gameType.isSurvival()
                                && keyMapping.getCategory() != KeyMapping.Category.CREATIVE
                                && keyMapping.getCategory() != KeyMapping.Category.SPECTATOR)
                        || (gameType.isCreative()
                                && keyMapping.getCategory() != KeyMapping.Category.SPECTATOR)
                        || (gameType.isBlockPlacingRestricted()
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
        //KeybindsGalorePlus.debugLog( key.getName() + " pressed: " + pressed );

        if ( !isIgnoredKey( key ) )
        {
            // Not ignored, process it
            // DON'T CANCEL YET -- may still need vanilla behaviour (not click-hold and no conflicts)

            if ( isClickHoldKey( key ) )
            {
                ci.cancel();
                // Skip the expensive stream filter
                KeyMapping clickHoldMapping = clickHoldKeys.get( key );

                if ( Configurations.DEBUG ) KeybindsGalorePlus.LOGGER.info(
                        "Attempting to set mapping {} to {} (click-hold)",
                        clickHoldMapping.getName(), pressed ? "pressed" : "released"
                );

                if ( pressed )
                {
                    if ( clickHoldMapping != null && Objects.requireNonNull( clickHoldKeysCooldowns.get( key ) ) == 0 )
                    {
                        // Has mapping, and cooldown is over -- transfer pressed state
                        ((KeyMappingAccessor) clickHoldMapping).setIsDown( true );
                        ((KeyMappingAccessor) clickHoldMapping).setClickCount( 1 );
                    }
                    // else -- no mapping or cooldown in progress; do nothing
                }
                else
                {
                    // If the click-hold key was released, remove it from the list and cancel the cooldown
                    // (after transferring the state) regardless of cooldown.
                    // For GUIs (without the click-hold bug fix), flow reaches here because
                    // key presses are consumed by the screen and reduced to a set( false ) call
                    
                    KeybindsGalorePlus.debugLog( "Deactivating key {} (click-hold)", key.getName() );
                    clearClickHoldKey( key );
                    ((KeyMappingAccessor) clickHoldMapping).setIsDown( false );
                    ((KeyMappingAccessor) clickHoldMapping).setClickCount( 0 );
                }
            }
            else
            {
                // Not a click-hold key; we need to do the expensive filter
                List<KeyMapping> mappings = getMappingsForContext( key );

                if ( mappings.size() > 1 )
                {
                    ci.cancel();
                    // Has conflicts -- open pie menu

                    // Key has conflicts, and shouldn't be ignored
                    if ( pressed )
                    {
                        // Conflicts to handle, and was pressed -- open pie menu

                        // Changing Screens (which this method does) resets all bindings to "unpressed",
                        // so zoom mods should work absolutely fine with us :)
                        KeybindsGalorePlus.debugLog( "\tOpening pie menu" );

                        // When any screen is closed and mouse is grabbed, and we're NOT on macOS,
                        // all keymapping states are updated to match the real keyboard
                        // (MouseHandler.grabMouse() -> KeyMapping.setAll())
                        // This causes all our conflicting mkeymappings to activate,
                        // which we obviously don't want.
                        // This actually doesn't manifest unless exiting a gui opened by click-hold.
                        // Notable instances when screens are closed:
                        // - click-hold closing of pie menu
                        // - GUI closing
                        // The fix is to
                        openConflictMenu( key, mappings );
                    }
                    // Conflicts to handle, but key was released -- do nothing; the screen handles it
                }
                // No conflicts -- proceed as per vanilla
            }
        }
        // Ignored key -- proceed as per vanilla
    }
}
