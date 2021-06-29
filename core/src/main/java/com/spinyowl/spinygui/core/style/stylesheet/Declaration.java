package com.spinyowl.spinygui.core.style.stylesheet;

import static com.spinyowl.spinygui.core.style.stylesheet.Property.INHERIT;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.INITIAL;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * Declaration is combination of property and it's value.
 *
 * @param <T> type of property value.
 */
@Data
@AllArgsConstructor
public class Declaration<T> {

  /** Property definition. */
  @NonNull private final Property<T> property;

  /** Current value of css property. Could not be null. */
  @NonNull protected String value;

  // TODO: Need to store extracted value to not apply extraction each time. Should be refreshed if
  //       string value changed.

  /** Used to reset property value to default. */
  public void resetToDefault() {
    this.value = property.defaultValue();
  }

  public boolean isInitial() {
    return INITIAL.equalsIgnoreCase(value);
  }

  public boolean isInherit() {
    return INHERIT.equalsIgnoreCase(value);
  }
}
