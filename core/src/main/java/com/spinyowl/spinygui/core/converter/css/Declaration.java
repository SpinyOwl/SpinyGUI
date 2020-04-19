package com.spinyowl.spinygui.core.converter.css;

import static com.spinyowl.spinygui.core.converter.css.Property.INHERIT;
import static com.spinyowl.spinygui.core.converter.css.Property.INITIAL;

import java.util.Objects;

public class Declaration<T> {

  /**
   * Current value of css property. Could not be null.
   */
  protected String value;

  private Property<T> property;

  public Declaration(Property<T> property, String value) {
    this.property = Objects.requireNonNull(property);
    this.value = Objects.requireNonNull(value);
  }

  public Property<T> getProperty() {
    return property;
  }

  public void setProperty(Property<T> property) {
    this.property = property;
  }

  public String getValue() {
    return value;
  }

  /**
   * Used to set value to property.
   *
   * @param value value to set.
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Used to reset property value to default.
   */
  public void resetToDefault() {
    this.value = property.defaultValue;
  }

  public boolean isInitial() {
    return INITIAL.equalsIgnoreCase(value);
  }

  public boolean isInherit() {
    return INHERIT.equalsIgnoreCase(value);
  }

}
