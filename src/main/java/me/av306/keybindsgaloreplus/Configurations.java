package me.av306.keybindsgaloreplus;

import me.av306.liteconfig.annotations.ConfigComment;
import me.av306.liteconfig.annotations.IgnoreConfig;

import java.util.ArrayList;
import java.util.Arrays;

@ConfigComment( "Quick rundown of data types:" )
@ConfigComment( "- float: \"floating-point\" number, can have decimal places. Use a period as the decimal separator, please, e.g. 3.14" )
@ConfigComment( "- int: \"integers\", whole numbers (no decimals)" )
@ConfigComment( "       Can also represent a color, in which case you can use hex notation:" )
@ConfigComment( "       0x(RR)(GG)(BB) e.g. 0xEED202 for #EED202 (not case sensitive!)" )
@ConfigComment( "       (Advanced users may notice that integers have space for an alpha component. This is ignored, and replaced by PIE_MENU_ALPHA.)" )
@ConfigComment( "- boolean: true/false" )
@ConfigComment( "- hexadecimal (color): \"short\" value (0-255) represented by TWO hexadecimal (base-16) numbers., e.g. 0xFF" )
public class Configurations
{
    public static final int CLICK_HOLD_REPEAT_COOLDOWN = 20;
    @ConfigComment( "Debug mode -- enable this when reproducing your bug; disable normally" )
    public static boolean DEBUG = false;


    // Performance
    
    @ConfigComment( "#######################" )
    @ConfigComment( "# Performance options #" )
    @ConfigComment( "#######################" )
    @ConfigComment( "" )
    @ConfigComment( "!!! NOTE: This option was removed in 1.4.0 !!!" )
    @ConfigComment( "lazy_conflict_check=true" )
    @ConfigComment( "" )
    @ConfigComment( "How many vertices/facets the pie menu circle should have in total (int)" )
    @ConfigComment( "I picked 60 for default because it's divisible by many possible sector numbers and gives reasonable results for others" )
    @ConfigComment( "   Increase if the pie menu looks too jagged, e.g. on large, high-resolution displays" )
    @ConfigComment( "   Can *technically* help performance, but probably not much unless your hardware is VERY slow" )
    public static int CIRCLE_VERTICES = 60;

    @ConfigComment( "If transparency should be enabled for the pie menu (boolean)" )
    @ConfigComment( "Can slightly increase performance by avoiding blend calculations" )
    public static boolean PIE_MENU_BLEND = true;

    @ConfigComment( "Whether there should be a darkened background on the pie menu screen (boolean)" )
    @ConfigComment( "May have a decent impact on performance, because of post-processing" )
    public static boolean DARKENED_BACKGROUND = true;

    @ConfigComment( "Whether the background of the pie menu screen should be blurred (boolean)" )
    @ConfigComment( "May have a decent impact on performance, because of post-processing" )
    public static boolean BLUR_BACKGROUND = true;

    @ConfigComment( "Draw label texts with shadow (boolean)" )
    @ConfigComment( "Disabling this can increase performance in some cases" )
    public static boolean LABEL_TEXT_SHADOW = false;


    // Behaviour

    @ConfigComment( "#####################" )
    @ConfigComment( "# Behaviour options #" )
    @ConfigComment( "#####################" )
    @ConfigComment( "" )
    @ConfigComment( "Attack workaround (boolean)" )
    @ConfigComment( "Makes Attack/Break work properly in the pie menu" )
    @ConfigComment( "(messes with the attack cooldown; may cause problems, e.g. with anti-cheat)" )
    @ConfigComment( "(IMPORTANT) Leave this as \"true\" if you're using EpicFight!" )
    public static boolean ENABLE_ATTACK_WORKAROUND = true;

    @ConfigComment( "Keys that should not open a pie menu (list of keycode ints)" )
    @ConfigComment( "See https://www.glfw.org/docs/3.4/group__keys.html for keycodes" )
    @ConfigComment( "Defaults to WASD" )
    public static ArrayList<Integer> IGNORED_KEYS = new ArrayList<>( Arrays.asList( 87, 65, 83, 68 ) );

    @ConfigComment( "Makes the mod ignore all keys except those in \"ignored_keys\"")
    public static boolean INVERT_IGNORED_KEYS_LIST = false;


    // Pie menu customisation

    @ConfigComment( "##########################" )
    @ConfigComment( "# Pie menu customisation #" )
    @ConfigComment( "##########################" )
    @ConfigComment( "" )
    @ConfigComment( "How much the selected sector should expand by (float)" )
    public static float EXPANSION_FACTOR_WHEN_SELECTED = 1.15f;

    @ConfigComment( "Minimum amount of space to leave around the pie menu (int, pixels)" )
    public static int PIE_MENU_MARGIN = 0;
    
    @ConfigComment( "Offset of the pie menu labels relative to the menu edge (int, scaled screen units)" )
    public static int LABEL_TEXT_INSET = 6;

    @ConfigComment( "Scale of the pie menu relative to the screen (float)" )
    public static float PIE_MENU_SCALE = 0.6f;
    
    @ConfigComment( "Scale of the pie menu's cancel zone relative to the screen (float)" )
    public static float CANCEL_ZONE_SCALE = 0.25f;

    @ConfigComment( "Color of the pie menu (ARGB int)" )
    public static int PIE_MENU_COLOR = 0x00404040; // ARGB
    
    @ConfigComment( "Color of the selected sector of the pie menu (RGB int)" )
    public static int PIE_MENU_SELECT_COLOR = 0x00FFFFFF;

    @ConfigComment( "Color of the clicked sector of the pie menu (RGB int)" )
    public static int PIE_MENU_HIGHLIGHT_COLOR = 0x00EED202;

    @ConfigComment( "Color of alternate sectors of the pie menu (RGB int)" )
    public static int PIE_MENU_COLOR_LIGHTEN_FACTOR = 0x191919;

    @ConfigComment( "Transparency of the pie menu (short, 0-255)" )
    public static short PIE_MENU_ALPHA = 0x90;

    @ConfigComment( "Gradient coloring for the pie menu sectors (boolean)" )
    public static boolean SECTOR_GRADATION = true;

    @ConfigComment( "Enable animation when opening the pie menu (boolean)" )
    public static boolean ANIMATE_PIE_MENU = true;
}
