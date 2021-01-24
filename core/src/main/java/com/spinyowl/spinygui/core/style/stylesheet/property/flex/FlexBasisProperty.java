package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_BASIS;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.node.style.types.length.Unit;

public class FlexBasisProperty extends Property<Unit> {

  public static final String AUTO = "auto";
  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public FlexBasisProperty() {
    super(FLEX_BASIS, AUTO, !INHERITED, !ANIMATABLE,
        (s, v) -> s.flex().flexBasis(v), s -> s.flex().flexBasis(),
        extractor::extract, extractor::isValid);
  }

}
