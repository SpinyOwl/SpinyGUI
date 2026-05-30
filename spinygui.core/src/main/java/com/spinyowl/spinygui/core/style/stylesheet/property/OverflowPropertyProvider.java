package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OVERFLOW;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OVERFLOW_X;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OVERFLOW_Y;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.checkValue;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList.Operator;
import com.spinyowl.spinygui.core.style.types.Overflow;
import java.util.List;
import java.util.Map;

public class OverflowPropertyProvider implements PropertyProvider {

  private static final Term<?> DEFAULT_VALUE = new TermIdent(Overflow.VISIBLE.name());

  @Override
  public List<Property> getProperties() {
    return List.of(
        Property.builder()
            .name(OVERFLOW)
            .defaultValue(DEFAULT_VALUE)
            .updater(OverflowPropertyProvider::update)
            .validator(OverflowPropertyProvider::test)
            .shorthand(true)
            .build(),
        Property.builder()
            .name(OVERFLOW_X)
            .defaultValue(DEFAULT_VALUE)
            .updater(put(OVERFLOW_X, TermIdent.class, Overflow::find))
            .validator(checkValue(TermIdent.class, Overflow::contains))
            .build(),
        Property.builder()
            .name(OVERFLOW_Y)
            .defaultValue(DEFAULT_VALUE)
            .updater(put(OVERFLOW_Y, TermIdent.class, Overflow::find))
            .validator(checkValue(TermIdent.class, Overflow::contains))
            .build());
  }

  private static void update(Term<?> term, Map<String, Object> styles) {
    List<Overflow> values = values(term);
    Overflow overflowX = values.get(0);
    Overflow overflowY = values.size() == 1 ? overflowX : values.get(1);
    styles.put(OVERFLOW_X, overflowX);
    styles.put(OVERFLOW_Y, overflowY);
  }

  private static boolean test(Term<?> term) {
    if (term instanceof TermIdent termIdent) {
      return Overflow.contains(termIdent.value());
    }
    if (term instanceof TermList termList) {
      return termList.size() >= 1
          && termList.size() <= 2
          && Operator.SPACE.equals(termList.operator())
          && termList.terms().stream().allMatch(OverflowPropertyProvider::test);
    }
    return false;
  }

  private static List<Overflow> values(Term<?> term) {
    if (term instanceof TermIdent termIdent) {
      return List.of(Overflow.find(termIdent.value()));
    }
    TermList termList = (TermList) term;
    return termList.terms().stream()
        .map(TermIdent.class::cast)
        .map(TermIdent::value)
        .map(Overflow::find)
        .toList();
  }
}
