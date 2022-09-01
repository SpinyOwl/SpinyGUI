package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TAB_SIZE;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermInteger;

public class TabSizeProperty extends Property {

  private static final ValueExtractor<Integer> extractor = ValueExtractors.of(Integer.class);

  public TabSizeProperty() {
    super(
        TAB_SIZE,
        new TermInteger(4),
        INHERITABLE,
        !ANIMATABLE,
        put(TAB_SIZE, TermInteger.class),
        TermInteger.class::isInstance);
  }
}
