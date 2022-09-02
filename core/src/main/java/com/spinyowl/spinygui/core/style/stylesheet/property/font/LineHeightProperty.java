package com.spinyowl.spinygui.core.style.stylesheet.property.font;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.LINE_HEIGHT;

import com.spinyowl.spinygui.core.Configuration;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermFloat;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;

public class LineHeightProperty extends Property {

  public static final String NORMAL = "normal";

  public LineHeightProperty() {
    super(
        LINE_HEIGHT,
        new TermIdent(NORMAL),
        INHERITABLE,
        ANIMATABLE,
        put(
                LINE_HEIGHT,
                TermIdent.class,
                NORMAL::equalsIgnoreCase,
                v -> Configuration.LINE_HEIGHT.value())
            .or(put(LINE_HEIGHT, TermFloat.class)),
        check(TermIdent.class, NORMAL::equalsIgnoreCase).or(TermFloat.class::isInstance));
  }
}
