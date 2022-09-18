package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.HEIGHT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MAX_HEIGHT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MAX_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MIN_HEIGHT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MIN_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.checkValue;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.isAuto;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.List;

public class DimensionPropertyProvider implements PropertyProvider {

  public static final String AUTO = "auto";
  public static final TermIdent TERM_AUTO = new TermIdent(AUTO);
  public static final TermIdent TERM_NONE = new TermIdent("none");

  @Override
  public List<Property> getProperties() {
    return List.of(
        Property.builder()
            .name(WIDTH)
            .defaultValue(TERM_AUTO)
            .animatable(true)
            .updater(
                put(WIDTH, TermIdent.class, AUTO::equalsIgnoreCase, v -> Unit.AUTO)
                    .or(put(WIDTH, TermLength.class)))
            .validator(isAuto().or(TermLength.class::isInstance))
            .build(),
        Property.builder()
            .name(HEIGHT)
            .defaultValue(TERM_AUTO)
            .animatable(true)
            .updater(
                put(HEIGHT, TermIdent.class, AUTO::equalsIgnoreCase, v -> Unit.AUTO)
                    .or(put(HEIGHT, TermLength.class)))
            .validator(isAuto().or(TermLength.class::isInstance))
            .build(),
        Property.builder()
            .name(MAX_HEIGHT)
            .defaultValue(TERM_NONE)
            .animatable(true)
            .updater(
                put(
                        MAX_HEIGHT,
                        TermIdent.class,
                        "none"::equalsIgnoreCase,
                        v -> Length.pixel(Integer.MAX_VALUE))
                    .or(put(MAX_HEIGHT, TermLength.class)))
            .validator(
                checkValue(TermIdent.class, "none"::equalsIgnoreCase)
                    .or(TermLength.class::isInstance))
            .build(),
        Property.builder()
            .name(MAX_WIDTH)
            .defaultValue(TERM_NONE)
            .animatable(true)
            .updater(
                put(
                        MAX_WIDTH,
                        TermIdent.class,
                        "none"::equalsIgnoreCase,
                        v -> Length.pixel(Integer.MAX_VALUE))
                    .or(put(MAX_WIDTH, TermLength.class)))
            .validator(
                checkValue(TermIdent.class, "none"::equalsIgnoreCase)
                    .or(TermLength.class::isInstance))
            .build(),
        Property.builder()
            .name(MIN_HEIGHT)
            .defaultValue(new TermLength(Length.ZERO))
            .animatable(true)
            .updater(put(MIN_HEIGHT, TermLength.class))
            .validator(TermLength.class::isInstance)
            .build(),
        Property.builder()
            .name(MIN_WIDTH)
            .defaultValue(new TermLength(Length.ZERO))
            .animatable(true)
            .updater(put(MIN_WIDTH, TermLength.class))
            .validator(TermLength.class::isInstance)
            .build());
  }
}
