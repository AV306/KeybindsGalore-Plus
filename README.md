# KeybindsGalore Plus

> [!WARNING]<br>
> Feature parity between versions is a little weird for now. All versions are identical in core functionality but may be missing some minor bugfixes and auxiliaries. It should get all cleared up by the end of the year.

> [!NOTE]<br>
> This project is a fork of KeybindsGalore, originally by Cael and updated to 1.20 by HVB007.
> <br>[HVB007's project is here](https://github.com/HVB007og/KeybindsGalore_HVB007_1.20.x), and Cael's [original project is here](https://github.com/CaelTheColher/KeybindsGalore).

<br>

Too many keybinds? Bind them all to one key and activate one at a time from a menu!

- Choose actions from a **[pie menu](https://en.wikipedia.org/wiki/Pie_menu)**
- Supports **mouse buttons**!
- Supports **1.20.x** and **1.21.x**
- Supports **Fabric** and **NeoForge (via Sinytra Connector)**

<div style="display: flex; justify-content: center; align-items: center;">
  <img src="https://github.com/AV306/KeybindsGalore-Plus/blob/14b7001f913c9bf089ef4fc41934c60dcf0db275/images/kbg_plus_demo.gif?raw=true" max-height=400 />
</div>

<br>

## How to Use

KeybindsGalore Plus is powered by the vanilla keybinds data!

- **Pressing down** a key with multiple mappings **opens a pie menu**
- **Releasing** the key while the pie menu is displayed will **activate the highlighted action** and **close** the pie menu
- **Clicking** on a section of the pie menu, while holding the key, **activates the highlighted action** for **as long as the key is held**

When the pie menu *opens*, all actions currently activated will be deactivated, identical to vanilla behaviour.
<br>

## Configuration options

The configuration file is created at `<minecraft game directory>/config/keybindsgaloreplus_config.properties` if it does not exist.

See [the example config file](https://github.com/AV306/KeybindsGalore-Plus/blob/1.21.11/src/main/resources/keybindsgaloreplus_config.properties) for reference.

## Modifications to Original

- Optimised conflict searching
- Keybind labels now show their category along with their name
- Customisable keybind labels (see [here](https://github.com/AV306/KeybindsGalore-Plus/issues/3))
- Label texts no longer run off the screen
- Fully customisable pie menu
- **And more...**

<br>

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
- Warp159

(let me know if I missed you!)

<br>

## Roadmap

### 1.6

- Key repeat delay ([#28](https://github.com/AV306/KeybindsGalore-Plus/issues/28))
- Deterministic keybind ordering in menu 
- Per-keybind overrides in custom data for:
  - Label text format (category + name / name only / custom text)
  - sector colour
  - sector opacity
- Removal of non-lazy conflict check

### Future

- AMECS support

(need more features? make a [feature request](https://github.com/AV306/KeybindsGalore-Plus/issues)!)
