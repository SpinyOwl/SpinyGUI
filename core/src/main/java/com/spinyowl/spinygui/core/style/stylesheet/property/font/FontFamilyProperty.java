package com.spinyowl.spinygui.core.style.stylesheet.property.font;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_FAMILY;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import java.util.Set;
import java.util.stream.Collectors;

public class FontFamilyProperty extends Property {

  public FontFamilyProperty() {
    super(
        FONT_FAMILY,
        new TermIdent("Roboto"),
        INHERITABLE,
        !ANIMATABLE,
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
        check(TermIdent.class, Font::hasFont)
            .or(
                check(
                    TermList.class, list -> list.stream().allMatch(TermIdent.class::isInstance))));
  }

  private static String trimAndUnwrap(String value) {
    return value.trim().replace("\"", "");
  }
}
