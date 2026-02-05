package me.av306.keybindsgaloreplus.mixin;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin( KeyMapping.class )
public interface KeyMappingAccessor
{
    @Accessor void setClickCount( int timesPressed );
    @Accessor void setIsDown( boolean pressed ); // Sets the internal pressed state -- not to be confused with Keybinding#setPressed

    @Accessor InputConstants.Key getKey();

    @Invoker( "release" ) void invokeRelease();
}
