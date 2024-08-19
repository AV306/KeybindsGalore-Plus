# KeybindsGalore Plus

> [!NOTE]<br>
> This project is a fork of KeybindsGalore, originally by Cael and updated to 1.20 by HVB007.
> <br>[HVB007's project is here](https://github.com/HVB007og/KeybindsGalore_HVB007_1.20.x), and Cael's [original project is here](https://github.com/CaelTheColher/KeybindsGalore).

<br>

This mod opens a pie menu (aka pizza menu, circle selector), letting you choose the desired action when a key with multiple bindings is pressed! No more weird behaviour on conflicting keys :D

Supports 1.21!

<div style="display: flex; justify-content: center; align-items: center;">
  <img src="https://github.com/AV306/MultiBind/blob/6d65ebe0942862e86e7fc73cd2a60b860edccd15/images/pie_menu_1.gif?raw=true" />
</div>

<br>

## Modifications to Original

- Reduced the list of keys which will never open a pie menu (more details [here](https://github.com/AV306/MultiBind/blob/master/docs.md))
- Optimised conflict searching and rendering (only checks for conflicts when the keybind setting menu is closed)
- Keybind labels now show their category along with their name, for easier identification ([example](https://github.com/AV306/MultiBind/blob/master/docs.md))
- Keybind labels can be customised! (see [this issue](https://github.com/AV306/KeybindsGalore-Plus/issues/3))
- Added a small "cancel zone" at the centre of the pie menu that will not activate any binding
- Label texts no longer run off the screen
- Made pie menu size, color, etc. configurable (config file automatically created in `.minecraft/config/multibind_config.properties`)
  

<br>
<br>

## [[ Old README below ]]

# KeybindsGalore_HVB007_1.20.x
Updated to 1.20 by HVB007.

>Github : https://github.com/HVB007og/KeybindsGalore_HVB007_1.20.x 
>Fabric mod Which opens an popup when there are multiple actions bound to the same key in the Minecraft>controls>Keybinds settings. then choose one of the options to use.

>Changelog keybindsgalore-0.2-1.20:

Works with 1.20.2

Added Feature: Will not open the menu when pressing certain keys (Due to keys compatibility with other mods) as follows: 
1.tab 
2.caps lock 
3.left shift 
4.left control 
5.space 
6.left alt 
7.w 
8.a 
9.s 
10.d

Future Feature: Add mod setting to configure the keys to disable.

>Does not support conflicting Keybinds not using the Minecraft Keybinds settings.

Updated to 1.20.x by HVB007

Updated version of keybindsgalore by Cael : https://github.com/CaelTheColher/KeybindsGalore
