package com.spinyowl.spinygui.core.converter.css.model;

import static com.spinyowl.spinygui.core.converter.css.model.Property.INHERIT;
import static com.spinyowl.spinygui.core.converter.css.model.Property.INITIAL;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Declaration<T> {

  /**
   * Property definition.
   */
  @NonNull
  private Property<T> property;

  /**
   * Current value of css property. Could not be null.
   */
  @NonNull
  protected String value;

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
