package me.av306.keybindsgaloreplus;

import me.av306.liteconfig.annotations.ConfigComment;
import me.av306.liteconfig.annotations.IgnoreConfig;

import java.util.ArrayList;
import java.util.Arrays;

import org.spongepowered.asm.launch.GlobalProperties.Keys;

import com.mojang.blaze3d.systems.RenderPass.Draw;

@ConfigComment( "Quick rundown of data types:" )
@ConfigComment( "- float: \"floating-point\" number, can have decimal places. Use a period as the decimal separator, please, e.g. 3.14" )
@ConfigComment( "- int: \"integers\", whole numbers (no decimals)" )
@ConfigComment( "       Can also represent a color, in which case you can use hex notation:" )
@ConfigComment( "       0x(RR)(GG)(BB) e.g. 0xEED202 for #EED202 (not case sensitive!)" )
@ConfigComment( "- boolean: true/false" )
@ConfigComment( "- hexadecimal (color): \"short\" value (0-255) represented by TWO hexadecimal (base-16) numbers., e.g. 0xFF" )
public class Configurations
{
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
    @ConfigComment( "I picked 60 because it's divisible by many possible sector numbers and gives reasonable results for others" )
    @ConfigComment( "   Best not to change this, because the pie menu won't have *exactly* this many vertices (the code is complicated)" )
    @ConfigComment( "   If you make it too low, you'll know because the edges of the menu will be very flat" )
    @ConfigComment( "   Can *technically* help performance, but probably not much unless your hardware is VERY slow" )
    public static int CIRCLE_VERTICES = 120;

    @ConfigComment( "If transparency should be enabled for the pie menu (boolean)" )
    @ConfigComment( "Can slightly increase performance by avoiding blend calculations" )
    public static boolean PIE_MENU_BLEND = true;

    @ConfigComment( "Whether there should be a darkened background on the pie menu screen (boolean)" )
    @ConfigComment( "May have a decent impact on performance, because of post-processing" )
    public static boolean DARKENED_BACKGROUND = true;

    // FIXME: currently unused?
    @IgnoreConfig
    public static boolean BLUR_BACKGROUND = true;

    @ConfigComment( "Draw label texts with shadow (boolean)" )
    @ConfigComment( "Disabling this may increase performance" )
    public static boolean LABEL_TEXT_SHADOW = false;

    // Behaviour

    @ConfigComment( "#####################" )
    @ConfigComment( "# Behaviour options #" )
    @ConfigComment( "#####################" )
    @ConfigComment( "" )
    @ConfigComment( "Attack workaround (boolean)" )
    @ConfigComment( "Makes Attack/Break work properly on the pie menu" )
    @ConfigComment( "(messes with the attack cooldown; may cause problems)" )
    @ConfigComment( "(IMPORTANT) Leave this as \"true\" if you're using EpicFight!" )
    public static boolean ENABLE_ATTACK_WORKAROUND = true;

    @ConfigComment( "!!! NOTE: Changed from \"skipped_keys\" !!!" )
    @ConfigComment( "Keys that should not open a pie menu (list of keycode ints)" )
    @ConfigComment( "See https://www.glfw.org/docs/3.4/group__keys.html for keycodes" )
    @ConfigComment( "Defaults to WASD" )
    public static ArrayList<Integer> IGNORED_KEYS = new ArrayList<>( Arrays.asList( 87, 65, 83, 68 ) );

    @ConfigComment( "Makes the mod ignore all keys except those in \"ignored_keys\"")
    public static boolean INVERT_IGNORED_KEYS_LIST = false;

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
