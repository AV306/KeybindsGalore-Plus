# KeybindsGalore Plus

> [!WARNING]<br>
> Updates to this mod are paused until 2027 as I prepare for national exams.
> <br>Bugfixes will probably come around June/December, but I can't promise more than that :(

> [!NOTE]<br>
> This project is a fork of KeybindsGalore, originally by Cael and updated to 1.20 by HVB007.
> <br>[HVB007's project is here](https://github.com/HVB007og/KeybindsGalore_HVB007_1.20.x), and Cael's [original project is here](https://github.com/CaelTheColher/KeybindsGalore).

<br>

A keybind conflict management and general quality-of-life mod!

- Provides a **pie menu** for conflicting keybinds
- **Corrects vanilla conflict handling**, similar to [Keybinds Fix](https://www.curseforge.com/minecraft/mc-mods/keybind-fix)
- Supports **mouse buttons**!
- Supports **1.20.x** and **1.21.1-1.21.6**
- Supports **Fabric** and **Forge/NeoForge (via Sinytra Connector)**

<div style="display: flex; justify-content: center; align-items: center;">
  <img src="https://github.com/AV306/KeybindsGalore-Plus/blob/14b7001f913c9bf089ef4fc41934c60dcf0db275/images/kbg_plus_demo.gif?raw=true" max-height=400 />
</div>

## How to Use

KeybindsGalore Plus will automatically detect conflicting keybinds!

- **Pressing down** a conflicted key will **open the pie menu**
- **Releasing** a conflicted key (when the pie menu is displayed) will **activate the highlighted action, if any** and **close** the pie menu
- **Clicking** on a section of the pie menu *without releasing the key* will **activate the highlighted action** and **deactivate** it only when the key is **released**

NB: When the pie menu opens, all actions currently activated (e.g. from holding another key down) will be deactivated.

## Modifications to Original

- Optimised conflict searching
- Keybind labels now show their category along with their name
- Customisable keybind labels (see [here](https://github.com/AV306/KeybindsGalore-Plus/issues/3))
- Prevented label texts from runing off the screen
- Fully customisable pie menu
- Allows compatibility with non-vanilla keybinds (insert your mod's keybinds into the [conflict table](https://github.com/AV306/KeybindsGalore-Plus/blob/1.21/src/main/java/me/av306/keybindsgaloreplus/KeybindManager.java) whenever convenient!)
- **And more...**

## Bug-busters :heart:

Bug reports and feature requests VERY welcome!

- lightmcxx
- mo9713
- Poopooracoocoo
- GabanKillasta
- Tgaisen
- GhostIsBeHere
- Alwis2000
- StarsShine11904
- ClutchMasterYT
- GuardedHoney53
- BumbleTree
- Mideks
- UNI717
- IG114514
- WxAaRoNxW

(let me know if I missed you!)

## Roadmap

### 1.5.0

- Per-keybind overrides in custom data for:
  - Label text format (category + name / name only / custom text)
  - sector colour
  - sector opacity
 
### Future

- In-game config menu
- Configurable key repeat delay for click-hold
- 1.21.7+ support

(need more features? make a [feature request](https://github.com/AV306/KeybindsGalore-Plus/issues)!)

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
