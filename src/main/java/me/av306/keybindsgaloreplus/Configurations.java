package me.av306.keybindsgaloreplus;

import java.util.ArrayList;

public class Configurations
{
    public static boolean DEBUG = false;

    public static boolean LAZY_CONFLICT_CHECK = true;

    // Impl as list of ints to make config easier
    public static ArrayList<Integer> SKIPPED_KEYS = new ArrayList<>();

    public static boolean USE_KEYBIND_FIX = true;

    public static float EXPANSION_FACTOR_WHEN_SELECTED = 1.15f;

    public static int PIE_MENU_MARGIN = 20;

    public static float PIE_MENU_SCALE = 0.6f;

    public static float CANCEL_ZONE_SCALE = 0.3f;

    public static int CIRCLE_VERTICES = 64;

    public static short PIE_MENU_COLOR = 0x40;

    //public static short PIE_MENU_COLOR_RED = 0x40;
    //public static short PIE_MENU_COLOR_GREEN = 0x40;
    //public static short PIE_MENU_COLOR_BLUE = 0x40;
    public static short PIE_MENU_HIGHLIGHT_COLOR = 0xFF;

    public static short PIE_MENU_COLOR_LIGHTEN_FACTOR = 0x19;

    public static short PIE_MENU_ALPHA = 0x60;

    public static boolean SECTOR_GRADATION = true;

    public static int LABEL_TEXT_INSET = 4;

    public static boolean ANIMATE_PIE_MENU = true;

    public static boolean DARKENED_BACKGROUND = true;
}
