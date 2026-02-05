package me.av306.keybindsgaloreplus.configmanager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Containing class for the ConfigComment repeatable annotation.
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( {ElementType.FIELD, ElementType.TYPE} )
public @interface ConfigComments
{
    ConfigComment[] value();
}