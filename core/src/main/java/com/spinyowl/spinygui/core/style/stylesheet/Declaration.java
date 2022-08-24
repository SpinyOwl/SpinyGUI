package com.spinyowl.spinygui.core.style.stylesheet;

import static com.spinyowl.spinygui.core.style.stylesheet.Property.INHERIT;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.INITIAL;
import com.spinyowl.spinygui.core.node.Element;
import java.util.Map;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Declaration is combination of property and it's value. */
@Data
@RequiredArgsConstructor
public class Declaration {

  /** Property definition. */
  @NonNull private final Property property;

  /** Current value of css property. Could not be null. */
  @NonNull protected String stringValue;

  protected Term<?> term;

  private boolean enabled = true;

  public Declaration(@NonNull Property property, @NonNull String stringValue, Term<?> term) {
    this.property = property;
    this.stringValue = stringValue;
    this.term = term;
  }

  /** Used to reset property value to default. */
  public void resetToDefault() {
    this.stringValue = property.defaultValue();
  }

  public boolean isInitial() {
    return INITIAL.equalsIgnoreCase(stringValue);
  }

  public boolean isInherit() {
    return INHERIT.equalsIgnoreCase(stringValue);
  }

  public void compute(Element element, Map<String, Object> styles) {
    property.compute(element, stringValue, styles);
  }

  @Override
  public String toString() {
    return property.name() + ": " + stringValue;
  }
}
