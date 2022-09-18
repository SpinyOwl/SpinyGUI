package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_BOTTOM;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_LEFT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_RIGHT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_TOP;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.getOneFourLengths;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.setOneFour;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.List;

public class PaddingPropertyProvider implements PropertyProvider {

  @Override
  public List<Property> getProperties() {
    return List.of(
        new Property(
            PADDING,
            new TermLength(Length.ZERO),
            false,
            true,
            (term, styles) ->
                setOneFour(
                    getOneFourLengths(term).toArray(),
                    PADDING_TOP,
                    PADDING_RIGHT,
                    PADDING_BOTTOM,
                    PADDING_LEFT,
                    styles),
            term1 ->
                term1 instanceof TermLength
                    || term1 instanceof TermList list
                        && list.terms().stream().allMatch(TermLength.class::isInstance),
            true),
        new Property(
            PADDING_BOTTOM,
            new TermLength(Length.ZERO),
            false,
            true,
            put(PADDING_BOTTOM, TermLength.class),
            TermLength.class::isInstance),
        new Property(
            PADDING_LEFT,
            new TermLength(Length.ZERO),
            false,
            true,
            put(PADDING_LEFT, TermLength.class),
            TermLength.class::isInstance),
        new Property(
            PADDING_RIGHT,
            new TermLength(Length.ZERO),
            false,
            true,
            put(PADDING_RIGHT, TermLength.class),
            TermLength.class::isInstance),
        new Property(
            PADDING_TOP,
            new TermLength(Length.ZERO),
            false,
            true,
            put(PADDING_TOP, TermLength.class),
            TermLength.class::isInstance));
  }
}
