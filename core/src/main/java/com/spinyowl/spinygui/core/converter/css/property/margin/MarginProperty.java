package com.spinyowl.spinygui.core.converter.css.property.margin;

import static com.spinyowl.spinygui.core.converter.css.Properties.MARGIN;
import static com.spinyowl.spinygui.core.converter.css.util.StyleUtils.testMultipleValues;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.Margin;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class MarginProperty extends Property<Margin> {

  private static ValueExtractor<Unit> unitValueExtractor = ValueExtractors.of(Unit.class);

  public MarginProperty() {
    super(MARGIN, "0", !INHERITED, ANIMATABLE,
        NodeStyle::margin, NodeStyle::margin,
        MarginProperty::extract, MarginProperty::test);
  }

  private static Margin extract(String value) {
    if (value == null) {
      return null;
    }

    //@formatter:off
    String[] v = value.split("\\s+");
    switch (v.length) {
      case 1: return new Margin(x(v[0]));
      case 2: return new Margin(x(v[0]), x(v[1]));
      case 3: return new Margin(x(v[0]), x(v[1]), x(v[2]));
      case 4: default: return new Margin(x(v[0]), x(v[1]), x(v[2]), x(v[3]));
    }
    //@formatter:on
  }

  private static Unit x(String v) {
    return unitValueExtractor.extract(v);
  }

  public static boolean test(String value) {
    return testMultipleValues(value, "\\s+", 1, 4, unitValueExtractor::isValid);
  }
}
