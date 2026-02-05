package me.av306.keybindsgaloreplus.mixin;

import me.av306.keybindsgaloreplus.KeybindManager;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

@Mixin( KeyBindsScreen.class )
public abstract class KeyBindsScreenMixin extends OptionsSubScreen
{
    public KeyBindsScreenMixin( Screen parent, Options gameOptions, Component title )
    {
        super( parent, gameOptions, title );
    }

    @Override
    public void onClose()
    {
        super.onClose();

        // Check for conflicting keybinds on screen close
        KeybindManager.findAllConflicts();
    }
}
