package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_FAMILY;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_SIZE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_STRETCH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_WEIGHT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.LINE_HEIGHT;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.checkValue;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;

import com.spinyowl.spinygui.core.Configuration;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontSize;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermFloat;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FontPropertyProvider implements PropertyProvider {
  public static final String NORMAL = "normal";

  @Override
  public List<Property> getProperties() {
    return List.of(
        Property.builder()
            .name(FONT_FAMILY)
            .defaultValue(new TermIdent("Roboto"))
            .inheritable(true)
            .updater(
                put(FONT_FAMILY, TermIdent.class, value -> Set.of(trimAndUnwrap(value)))
                    .or(
                        put(
                            FONT_FAMILY,
                            TermList.class,
                            list ->
                                list.stream()
                                    .filter(TermIdent.class::isInstance)
                                    .map(termIdent -> trimAndUnwrap(termIdent.value().toString()))
                                    .collect(Collectors.toSet()))))
            .validator(
                checkValue(TermIdent.class, Font::hasFont)
                    .or(
                        checkValue(
                            TermList.class,
                            list -> list.stream().allMatch(TermIdent.class::isInstance))))
            .build(),
        Property.builder()
            .name(FONT_SIZE)
            .defaultValue(new TermIdent(FontSize.MEDIUM.name()))
            .inheritable(true)
            .animatable(true)
            .updater(
                put(
                        FONT_SIZE,
                        TermIdent.class,
                        FontSize::contains,
                        name1 -> Length.pixel(FontSize.find(name1).size()))
                    .or(put(FONT_SIZE, TermLength.class)))
            .validator(
                checkValue(TermIdent.class, FontSize::contains).or(TermLength.class::isInstance))
            .build(),
        Property.builder()
            .name(FONT_STRETCH)
            .defaultValue(new TermIdent(FontStretch.NORMAL.name()))
            .inheritable(true)
            .animatable(true)
            .updater(put(FONT_STRETCH, TermIdent.class, FontStretch::find))
            .validator(checkValue(TermIdent.class, FontStretch::contains))
            .build(),
        Property.builder()
            .name(FONT_STYLE)
            .defaultValue(new TermIdent(FontStyle.NORMAL.name()))
            .inheritable(true)
            .updater(put(FONT_STYLE, TermIdent.class, FontStyle::find))
            .validator(checkValue(TermIdent.class, FontStyle::contains))
            .build(),
        Property.builder()
            .name(FONT_WEIGHT)
            .defaultValue(new TermIdent(FontWeight.NORMAL.name()))
            .inheritable(true)
            .animatable(true)
            .updater(put(FONT_WEIGHT, TermIdent.class, FontWeight::find))
            .validator(checkValue(TermIdent.class, FontWeight::contains))
            .build(),
        Property.builder()
            .name(LINE_HEIGHT)
            .defaultValue(new TermIdent(NORMAL))
            .inheritable(true)
            .animatable(true)
            .updater(
                put(
                        LINE_HEIGHT,
                        TermIdent.class,
                        NORMAL::equalsIgnoreCase,
                        v -> Configuration.LINE_HEIGHT.value())
                    .or(put(LINE_HEIGHT, TermFloat.class)))
            .validator(
                checkValue(TermIdent.class, NORMAL::equalsIgnoreCase)
                    .or(TermFloat.class::isInstance))
            .build());
  }

  private static String trimAndUnwrap(String value) {
    return value.trim().replace("\"", "");
  }
}
