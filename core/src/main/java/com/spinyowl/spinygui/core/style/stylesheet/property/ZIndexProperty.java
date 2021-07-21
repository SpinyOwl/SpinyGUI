package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.Z_INDEX;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import java.util.Map;

public class ZIndexProperty extends Property {

  private static final ValueExtractor<Integer> extractor = ValueExtractors.of(Integer.class);
  private static final String AUTO = "auto";

  public ZIndexProperty() {
    super(
        Z_INDEX,
        AUTO,
        !INHERITED,
        !ANIMATABLE,
        v -> Map.of(Z_INDEX, AUTO.equals(v) ? 0 : extractor.extract(v)),
        value -> AUTO.equals(value) || extractor.isValid(value));
  }
}
