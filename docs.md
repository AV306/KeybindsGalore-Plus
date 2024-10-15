# KeybindsGalore-Plus Docs

## Ignored keys

These are keys where a pie menu would obstruct basic function, and as such are (currently) hardcoded not to open a pie menu, even if they are bound to multiple actions.

These keys are currently WASD and Space, and are configurable by the `ignored_keys` entry in the config file.

<br>

## Pie Menu Labels

[TODO: pictures]

Previously, KeybindsGalore would label each sector of the pie menu with the name (but not the category) of the action it represented. Example: if you bound `G` to `Fullbright` under `Xenon Features`, `Some Action` from `Some Mod`, and `Pick Block` from vanilla, the pie menu labels would be `Fullbright`, `Some Action` and `Pick Block`. This is fine for most things.

However, most mods have a key to open their settings menu, and this is often just named `Settings`, `Configurations` or something along those lines. If you're like me and you bound a single key to all the config menus, the entire pie menu would just be `Settings`, `Configurations`, `Open Settings`, etc and it would be really hard to tell which mod's menu corresponds to which sector.

In this fork, the *category* of the action (The "heading" of the section in the keybinds menu, like `Gameplay` or `Xenon Features`) is also displayed along with the name. So, using the settings menu example from before, the pie menu is now `Some mod: Settings`, `Some other mod: Configurations`, etc, and now you can actually tell which menu you're opening!

Also, labels in KeybindsGalore would run off the screen if they are too long; I fixed it so they don't anymore :)

<sup>(full disclaimer: The "[Xenon](https://github.com/AV306/xenon)" mentioned here is another mod of mine and it's got [a bunch of neat things you might like](https://github.com/AV306/xenon/blob/1.20-DEV/docs/FEATURES.md)!)</sup>

<br>

## Pie menu cancel zone

> The [original KeybindsGalore](https://github.com/HVB007og/KeybindsGalore_HVB007_1.20.x) has this too, plus a circle showing the deadzone!
> <sup>Just in case someone claims plagiarism: [I've been intending to do it for quite a long time, but was just lazy to do it first](https://github.com/AV306/KeybindsGalore-Plus/commit/331f085476f8c2a64330d0720e0e149ec7aa4d5a)</sup>

If you accidentally press a pie menu key, there is a ~20px (configurable) zone in the centre where no sector will be highlighted, and you can release the key without anything triggering.

<br>

## Configurations

The default config file + comments can be found [here](https://github.com/AV306/KeybindsGalore-Plus/blob/master/src/main/resources/keybindsgaloreplus_config.properties)
