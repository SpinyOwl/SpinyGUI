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
        Property.builder()
            .name(PADDING)
            .defaultValue(new TermLength(Length.ZERO))
            .animatable(true)
            .updater(
                (term, styles) ->
                    setOneFour(
                        getOneFourLengths(term).toArray(),
                        PADDING_TOP,
                        PADDING_RIGHT,
                        PADDING_BOTTOM,
                        PADDING_LEFT,
                        styles))
            .validator(
                term1 ->
                    term1 instanceof TermLength
                        || term1 instanceof TermList list
                            && list.terms().stream().allMatch(TermLength.class::isInstance))
            .shorthand(true)
            .build(),
        Property.builder()
            .name(PADDING_BOTTOM)
            .defaultValue(new TermLength(Length.ZERO))
            .animatable(true)
            .updater(put(PADDING_BOTTOM, TermLength.class))
            .validator(TermLength.class::isInstance)
            .build(),
        Property.builder()
            .name(PADDING_LEFT)
            .defaultValue(new TermLength(Length.ZERO))
            .animatable(true)
            .updater(put(PADDING_LEFT, TermLength.class))
            .validator(TermLength.class::isInstance)
            .build(),
        Property.builder()
            .name(PADDING_RIGHT)
            .defaultValue(new TermLength(Length.ZERO))
            .animatable(true)
            .updater(put(PADDING_RIGHT, TermLength.class))
            .validator(TermLength.class::isInstance)
            .build(),
        Property.builder()
            .name(PADDING_TOP)
            .defaultValue(new TermLength(Length.ZERO))
            .animatable(true)
            .updater(put(PADDING_TOP, TermLength.class))
            .validator(TermLength.class::isInstance)
            .build());
  }
}
