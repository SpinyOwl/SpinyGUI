package com.spinyowl.spinygui.core.style.stylesheet.property.position;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BOTTOM;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.node.style.NodeStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.node.style.types.length.Unit;

public class BottomProperty extends Property<Unit> {

  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public BottomProperty() {
    super(BOTTOM, "auto", !INHERITED, ANIMATABLE,
        NodeStyle::bottom, NodeStyle::bottom,
        extractor::extract, extractor::isValid);
  }
}
