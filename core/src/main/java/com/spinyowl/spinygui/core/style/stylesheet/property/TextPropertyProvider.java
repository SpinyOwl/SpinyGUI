package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OVERFLOW_WRAP;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TEXT_ALIGN;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.WORD_BREAK;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.WORD_WRAP;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.checkValue;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.OverflowWrap;
import com.spinyowl.spinygui.core.style.types.TextAlign;
import com.spinyowl.spinygui.core.style.types.WordBreak;
import java.util.List;

public class TextPropertyProvider implements PropertyProvider {

  @Override
  public List<Property> getProperties() {
    return List.of(
        Property.builder()
            .name(TEXT_ALIGN)
            .defaultValue(new TermIdent(TextAlign.LEFT.name()))
            .inheritable(true)
            .updater(put(TEXT_ALIGN, TermIdent.class, TextAlign::find))
            .validator(checkValue(TermIdent.class, TextAlign::contains))
            .build(),
        Property.builder()
            .name(OVERFLOW_WRAP)
            .defaultValue(new TermIdent(OverflowWrap.NORMAL.name()))
            .inheritable(true)
            .updater(put(OVERFLOW_WRAP, TermIdent.class, OverflowWrap::find))
            .validator(checkValue(TermIdent.class, OverflowWrap::contains))
            .build(),
        Property.builder()
            .name(WORD_BREAK)
            .defaultValue(new TermIdent(WordBreak.NORMAL.name()))
            .inheritable(true)
            .updater(put(WORD_BREAK, TermIdent.class, WordBreak::find))
            .validator(checkValue(TermIdent.class, WordBreak::contains))
            .build(),
        Property.builder()
            .name(WORD_WRAP)
            .defaultValue(new TermIdent(OverflowWrap.NORMAL.name()))
            .inheritable(true)
            .updater(put(OVERFLOW_WRAP, TermIdent.class, OverflowWrap::find))
            .validator(checkValue(TermIdent.class, OverflowWrap::contains))
            .shorthand(true)
            .build());
  }
}
