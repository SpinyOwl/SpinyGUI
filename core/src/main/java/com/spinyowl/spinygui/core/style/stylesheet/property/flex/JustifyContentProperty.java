package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.JUSTIFY_CONTENT;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.flex.JustifyContent;

public class JustifyContentProperty extends Property {

  public JustifyContentProperty() {
    super(
        JUSTIFY_CONTENT,
        new TermIdent(JustifyContent.FLEX_START.name()),
        !INHERITABLE,
        !ANIMATABLE,
        put(JUSTIFY_CONTENT, TermIdent.class, JustifyContent::find),
        check(TermIdent.class, JustifyContent::contains));
  }
}
