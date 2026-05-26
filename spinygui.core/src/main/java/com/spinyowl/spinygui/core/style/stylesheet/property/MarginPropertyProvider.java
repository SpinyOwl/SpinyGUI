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
        Property.builder()
            .name(MARGIN)
            .defaultValue(new TermLength(Length.ZERO))
            .animatable(true)
            .updater(
                (term, styles) ->
                    setOneFour(
                        getOneFourLengths(term).toArray(),
                        MARGIN_TOP,
                        MARGIN_RIGHT,
                        MARGIN_BOTTOM,
                        MARGIN_LEFT,
                        styles))
            .validator(
                checkValue(
                        TermList.class,
                        list -> list.stream().allMatch(TermLength.class::isInstance))
                    .or(TermLength.class::isInstance))
            .shorthand(true)
            .build(),
        Property.builder()
            .name(MARGIN_BOTTOM)
            .defaultValue(new TermLength(Length.ZERO))
            .animatable(true)
            .updater(put(MARGIN_BOTTOM, TermLength.class))
            .validator(TermLength.class::isInstance)
            .build(),
        Property.builder()
            .name(MARGIN_LEFT)
            .defaultValue(new TermLength(Length.ZERO))
            .animatable(true)
            .updater(put(MARGIN_LEFT, TermLength.class))
            .validator(TermLength.class::isInstance)
            .build(),
        Property.builder()
            .name(MARGIN_RIGHT)
            .defaultValue(new TermLength(Length.ZERO))
            .animatable(true)
            .updater(put(MARGIN_RIGHT, TermLength.class))
            .validator(TermLength.class::isInstance)
            .build(),
        Property.builder()
            .name(MARGIN_TOP)
            .defaultValue(new TermLength(Length.ZERO))
            .animatable(true)
            .updater(put(MARGIN_TOP, TermLength.class))
            .validator(TermLength.class::isInstance)
            .build());
  }
}
