package com.spinyowl.spinygui.core.style.stylesheet.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark property provider to add properties to property store with higher priority. By
 * default, all tags added with 0 priority.
 *
 * <p>Property providers with higher priority will be used to add properties to property store later
 * which means that properties with higher priority will override properties with lower priority.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Priority {

  /**
   * Allows to overload some property / tag handler.
   *
   * @return priority - used only in case if there is more than one element with the same name.
   */
  int value() default 0;
}
