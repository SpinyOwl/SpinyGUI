package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_DIRECTION;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.flex.FlexDirection;

public class FlexDirectionProperty extends Property {

  public FlexDirectionProperty() {
    super(
        FLEX_DIRECTION,
        new TermIdent(FlexDirection.ROW.name()),
        !INHERITABLE,
        !ANIMATABLE,
        put(FLEX_DIRECTION, TermIdent.class, FlexDirection::find),
        check(TermIdent.class, FlexDirection::contains));
  }
}
