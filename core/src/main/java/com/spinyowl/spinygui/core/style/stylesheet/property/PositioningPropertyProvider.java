package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BOTTOM;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.LEFT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.RIGHT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TOP;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.checkValue;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.List;

public class PositioningPropertyProvider implements PropertyProvider {

  public static final String AUTO = "auto";

  @Override
  public List<Property> getProperties() {
    return List.of(
        new Property(
                BOTTOM,
                new TermIdent(AUTO),
                false,
                true,
                put(BOTTOM, TermIdent.class, AUTO::equalsIgnoreCase, v -> Unit.AUTO)
                    .or(put(BOTTOM, TermLength.class)),
                checkValue(TermIdent.class, AUTO::equalsIgnoreCase)
                    .or(TermLength.class::isInstance)),
            new Property(
                LEFT,
                new TermIdent(AUTO),
                false,
                true,
                put(LEFT, TermIdent.class, AUTO::equalsIgnoreCase, v -> Unit.AUTO)
                    .or(put(LEFT, TermLength.class)),
                checkValue(TermIdent.class, AUTO::equalsIgnoreCase)
                    .or(TermLength.class::isInstance)),
        new Property(
                RIGHT,
                new TermIdent(AUTO),
                false,
                true,
                put(RIGHT, TermIdent.class, AUTO::equalsIgnoreCase, v -> Unit.AUTO)
                    .or(put(RIGHT, TermLength.class)),
                checkValue(TermIdent.class, AUTO::equalsIgnoreCase)
                    .or(TermLength.class::isInstance)),
            new Property(
                TOP,
                new TermIdent(AUTO),
                false,
                true,
                put(TOP, TermIdent.class, AUTO::equalsIgnoreCase, v -> Unit.AUTO)
                    .or(put(TOP, TermLength.class)),
                checkValue(TermIdent.class, AUTO::equalsIgnoreCase)
                    .or(TermLength.class::isInstance)));
  }
}
