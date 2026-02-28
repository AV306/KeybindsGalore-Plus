package me.av306.keybindsgaloreplus.mixin;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;

@Mixin( KeyMapping.class )
public interface KeyMappingAccessor
{
    @Accessor( "MAP" )
    static Map<InputConstants.Key, List<KeyMapping>> getMap()
    {
        throw new UnsupportedOperationException();
    }

    @Accessor( "ALL" )
    static Map<InputConstants.Key, List<KeyMapping>> getAll()
    {
        throw new UnsupportedOperationException();
    }

    @Accessor void setClickCount( int timesPressed );
    @Accessor void setIsDown( boolean isDown); // Sets the internal pressed state -- not to be confused with Keybinding#setPressed

    @Accessor InputConstants.Key getKey();

    @Invoker( "release" ) void invokeRelease();
}
