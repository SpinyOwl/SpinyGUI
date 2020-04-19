package com.spinyowl.spinygui.core.converter.css.parser.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PseudoSelector {

  /**
   * Returns string representation of pseudo selector.
   *
   * @return string representation of pseudo selector.
   */
  String value();

}
