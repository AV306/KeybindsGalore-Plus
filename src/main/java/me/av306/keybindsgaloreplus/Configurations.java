package me.av306.keybindsgaloreplus;

import java.util.ArrayList;
import me.av306.keybindsgaloreplus.configmanager.ConfigComment;

@ConfigComment( "Quick rundown of data types:" )
@ConfigComment( " - float: floating-point number, can have decimal places. Use a period as the decimal separator, please, e.g. 3.14" )
@ConfigComment( " - int: integers, whole numbers (no decimals)" )
@ConfigComment( "       Can also represent a color, in which case you can use hex notation:" )
@ConfigComment( "       0x(RR)(GG)(BB) e.g. 0xEED202 for #EED202 (not case sensitive!) ")
@ConfigComment( " - boolean: true/false" )
@ConfigComment( " - hexadecimal (color): \"short\" value (0-255) represented by TWO hexadecimal (base-16) numbers., e.g. 0xFF" )
public class Configurations
{
    @ConfigComment( "Debug mode - enable this when reproducing your bug; disable normally" )
    @ConfigComment( "Prints many messages to the console" )
    public static boolean DEBUG = false;

    // Performance
    //public static boolean LAZY_CONFLICT_CHECK = true;
    public static int CIRCLE_VERTICES = 120;
    public static boolean PIE_MENU_BLEND = false;
    public static boolean DARKENED_BACKGROUND = true;
    public static boolean BLUR_BACKGROUND = true;
    public static boolean LABEL_TEXT_SHADOW = false;

    // Behaviour
    public static boolean ENABLE_ATTACK_WORKAROUND = true;

    // Impl as list of ints to make config easier
    public static ArrayList<Integer> IGNORED_KEYS = new ArrayList<>();
    public static boolean INVERT_IGNORED_KEYS_LIST = false;
    public static boolean USE_KEYBIND_FIX = true;


    // Pie menu customisation

    public static float EXPANSION_FACTOR_WHEN_SELECTED = 0;
    public static int PIE_MENU_MARGIN = 0;
    public static float PIE_MENU_SCALE = 0.6f;
    public static float CANCEL_ZONE_SCALE = 0.25f;
    public static int PIE_MENU_COLOR = 0x00404040; // ARGB
    public static int PIE_MENU_SELECT_COLOR = 0x00FFFFFF;
    public static int PIE_MENU_HIGHLIGHT_COLOR = 0x00EED202;
    public static int PIE_MENU_COLOR_LIGHTEN_FACTOR = 0x191919;
    public static short PIE_MENU_ALPHA = 0x60;
    public static boolean SECTOR_GRADATION = true;
    public static int LABEL_TEXT_INSET = 6;
    public static boolean ANIMATE_PIE_MENU = true;
}
