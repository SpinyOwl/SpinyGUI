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
        Property.builder()
            .name(BOTTOM)
            .defaultValue(new TermIdent(AUTO))
            .animatable(true)
            .updater(
                put(BOTTOM, TermIdent.class, AUTO::equalsIgnoreCase, v -> Unit.AUTO)
                    .or(put(BOTTOM, TermLength.class)))
            .validator(
                checkValue(TermIdent.class, AUTO::equalsIgnoreCase)
                    .or(TermLength.class::isInstance))
            .build(),
        Property.builder()
            .name(LEFT)
            .defaultValue(new TermIdent(AUTO))
            .animatable(true)
            .updater(
                put(LEFT, TermIdent.class, AUTO::equalsIgnoreCase, v -> Unit.AUTO)
                    .or(put(LEFT, TermLength.class)))
            .validator(
                checkValue(TermIdent.class, AUTO::equalsIgnoreCase)
                    .or(TermLength.class::isInstance))
            .build(),
        Property.builder()
            .name(RIGHT)
            .defaultValue(new TermIdent(AUTO))
            .animatable(true)
            .updater(
                put(RIGHT, TermIdent.class, AUTO::equalsIgnoreCase, v -> Unit.AUTO)
                    .or(put(RIGHT, TermLength.class)))
            .validator(
                checkValue(TermIdent.class, AUTO::equalsIgnoreCase)
                    .or(TermLength.class::isInstance))
            .build(),
        Property.builder()
            .name(TOP)
            .defaultValue(new TermIdent(AUTO))
            .animatable(true)
            .updater(
                put(TOP, TermIdent.class, AUTO::equalsIgnoreCase, v -> Unit.AUTO)
                    .or(put(TOP, TermLength.class)))
            .validator(
                checkValue(TermIdent.class, AUTO::equalsIgnoreCase)
                    .or(TermLength.class::isInstance))
            .build());
  }
}
