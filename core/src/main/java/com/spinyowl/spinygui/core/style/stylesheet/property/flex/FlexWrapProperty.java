package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_WRAP;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.flex.FlexWrap;

public class FlexWrapProperty extends Property {

  public FlexWrapProperty() {
    super(
        FLEX_WRAP,
        new TermIdent(FlexWrap.NOWRAP.name()),
        !INHERITABLE,
        !ANIMATABLE,
        put(FLEX_WRAP, TermIdent.class, FlexWrap::find),
        check(TermIdent.class, FlexWrap::contains));
  }
}
