package me.av306.keybindsgaloreplus.configmanager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Repeatable annotation representing a comment for the config entry it is
 * attached to.
 */
@Repeatable( ConfigComments.class )
@Target( {ElementType.FIELD, ElementType.TYPE} )
@Retention( RetentionPolicy.RUNTIME )
public @interface ConfigComment
{
    String value();
}