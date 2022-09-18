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
        new Property(
            FONT_FAMILY,
            new TermIdent("Roboto"),
            true,
            false,
            put(FONT_FAMILY, TermIdent.class, value -> Set.of(trimAndUnwrap(value)))
                .or(
                    put(
                        FONT_FAMILY,
                        TermList.class,
                        list ->
                            list.stream()
                                .filter(TermIdent.class::isInstance)
                                .map(termIdent -> trimAndUnwrap(termIdent.value().toString()))
                                .collect(Collectors.toSet()))),
            checkValue(TermIdent.class, Font::hasFont)
                .or(
                    checkValue(
                        TermList.class,
                        list -> list.stream().allMatch(TermIdent.class::isInstance)))),
        new Property(
            FONT_SIZE,
            new TermIdent(FontSize.MEDIUM.name()),
            true,
            true,
            put(
                    FONT_SIZE,
                    TermIdent.class,
                    FontSize::contains,
                    name1 -> Length.pixel(FontSize.find(name1).size()))
                .or(put(FONT_SIZE, TermLength.class)),
            checkValue(TermIdent.class, FontSize::contains).or(TermLength.class::isInstance)),
        new Property(
            FONT_STRETCH,
            new TermIdent(FontStretch.NORMAL.name()),
            true,
            true,
            put(FONT_STRETCH, TermIdent.class, FontStretch::find),
            checkValue(TermIdent.class, FontStretch::contains)),
        new Property(
            FONT_STYLE,
            new TermIdent(FontStyle.NORMAL.name()),
            true,
            false,
            put(FONT_STYLE, TermIdent.class, FontStyle::find),
            checkValue(TermIdent.class, FontStyle::contains)),
        new Property(
            FONT_WEIGHT,
            new TermIdent(FontWeight.NORMAL.name()),
            true,
            true,
            put(FONT_WEIGHT, TermIdent.class, FontWeight::find),
            checkValue(TermIdent.class, FontWeight::contains)),
        new Property(
            LINE_HEIGHT,
            new TermIdent(NORMAL),
            true,
            true,
            put(
                    LINE_HEIGHT,
                    TermIdent.class,
                    NORMAL::equalsIgnoreCase,
                    v -> Configuration.LINE_HEIGHT.value())
                .or(put(LINE_HEIGHT, TermFloat.class)),
            checkValue(TermIdent.class, NORMAL::equalsIgnoreCase).or(TermFloat.class::isInstance)));
  }

  private static String trimAndUnwrap(String value) {
    return value.trim().replace("\"", "");
  }
}
