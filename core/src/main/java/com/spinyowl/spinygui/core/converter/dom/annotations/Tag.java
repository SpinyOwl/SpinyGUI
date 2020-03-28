package com.spinyowl.spinygui.core.converter.dom.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark element to add it to tag mapping for converting to/from xml.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Tag {

    /**
     * Tag name. If not specified - tag mapping will use lower case of class name.
     *
     * @return tag name.
     */
    String value() default "";

}
