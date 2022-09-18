package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_BOTTOM;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_LEFT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_RIGHT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_TOP;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.checkValue;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.getOneFourLengths;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.setOneFour;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.List;

public class MarginPropertyProvider implements PropertyProvider {

  @Override
  public List<Property> getProperties() {
    return List.of(
        new Property(
            MARGIN,
            new TermLength(Length.ZERO),
            false,
            true,
            (term, styles) ->
                setOneFour(
                    getOneFourLengths(term).toArray(),
                    MARGIN_TOP,
                    MARGIN_RIGHT,
                    MARGIN_BOTTOM,
                    MARGIN_LEFT,
                    styles),
            checkValue(TermList.class, list -> list.stream().allMatch(TermLength.class::isInstance))
                .or(TermLength.class::isInstance),
            true),
        new Property(
            MARGIN_BOTTOM,
            new TermLength(Length.ZERO),
            false,
            true,
            put(MARGIN_BOTTOM, TermLength.class),
            TermLength.class::isInstance),
        new Property(
            MARGIN_LEFT,
            new TermLength(Length.ZERO),
            false,
            true,
            put(MARGIN_LEFT, TermLength.class),
            TermLength.class::isInstance),
        new Property(
            MARGIN_RIGHT,
            new TermLength(Length.ZERO),
            false,
            true,
            put(MARGIN_RIGHT, TermLength.class),
            TermLength.class::isInstance),
        new Property(
            MARGIN_TOP,
            new TermLength(Length.ZERO),
            false,
            true,
            put(MARGIN_TOP, TermLength.class),
            TermLength.class::isInstance));
  }
}
