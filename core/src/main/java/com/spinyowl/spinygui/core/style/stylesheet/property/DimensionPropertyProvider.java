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
        new Property(
            WIDTH,
            TERM_AUTO,
            false,
            true,
            put(WIDTH, TermIdent.class, AUTO::equalsIgnoreCase, v -> Unit.AUTO)
                .or(put(WIDTH, TermLength.class)),
            isAuto().or(TermLength.class::isInstance)),
        new Property(
            HEIGHT,
            TERM_AUTO,
            false,
            true,
            put(HEIGHT, TermIdent.class, AUTO::equalsIgnoreCase, v -> Unit.AUTO)
                .or(put(HEIGHT, TermLength.class)),
            isAuto().or(TermLength.class::isInstance)),
        new Property(
            MAX_HEIGHT,
            TERM_NONE,
            false,
            true,
            put(
                    MAX_HEIGHT,
                    TermIdent.class,
                    "none"::equalsIgnoreCase,
                    v -> Length.pixel(Integer.MAX_VALUE))
                .or(put(MAX_HEIGHT, TermLength.class)),
            checkValue(TermIdent.class, "none"::equalsIgnoreCase).or(TermLength.class::isInstance)),
        new Property(
            MAX_WIDTH,
            TERM_NONE,
            false,
            true,
            put(
                    MAX_WIDTH,
                    TermIdent.class,
                    "none"::equalsIgnoreCase,
                    v -> Length.pixel(Integer.MAX_VALUE))
                .or(put(MAX_WIDTH, TermLength.class)),
            checkValue(TermIdent.class, "none"::equalsIgnoreCase).or(TermLength.class::isInstance)),
        new Property(
            MIN_HEIGHT,
            new TermLength(Length.ZERO),
            false,
            true,
            put(MIN_HEIGHT, TermLength.class),
            TermLength.class::isInstance),
        new Property(
            MIN_WIDTH,
            new TermLength(Length.ZERO),
            false,
            true,
            put(MIN_WIDTH, TermLength.class),
            TermLength.class::isInstance));
  }
}
