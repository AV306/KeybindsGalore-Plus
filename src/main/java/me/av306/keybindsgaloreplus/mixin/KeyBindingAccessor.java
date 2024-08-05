package me.av306.keybindsgaloreplus.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin( KeyBinding.class )
public interface KeyBindingAccessor
{
    @Accessor void setTimesPressed( int timesPressed );
    @Accessor void setPressed( boolean pressed );

    @Accessor InputUtil.Key getBoundKey();
}
