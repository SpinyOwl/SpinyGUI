package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_BASIS;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class FlexBasisProperty extends Property {

  public static final String AUTO = "auto";

  public FlexBasisProperty() {
    super(
        FLEX_BASIS,
        new TermIdent(AUTO),
        !INHERITABLE,
        !ANIMATABLE,
        (term, styles) -> {
          if (term instanceof TermIdent) styles.put(FLEX_BASIS, Unit.AUTO);
          else if (term instanceof TermLength tl) styles.put(FLEX_BASIS, tl.value());
        },
        check(TermIdent.class, AUTO::equalsIgnoreCase).or(TermLength.class::isInstance));
  }
}
