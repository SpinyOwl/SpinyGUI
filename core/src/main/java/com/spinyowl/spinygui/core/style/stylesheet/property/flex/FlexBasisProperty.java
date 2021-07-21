package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_BASIS;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.Map;

public class FlexBasisProperty extends Property {

  public static final String AUTO = "auto";
  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public FlexBasisProperty() {
    super(
        FLEX_BASIS,
        AUTO,
        !INHERITED,
        !ANIMATABLE,
        value -> Map.of(FLEX_BASIS, extractor.extract(value)),
        extractor::isValid);
  }
}
