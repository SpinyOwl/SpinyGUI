package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OVERFLOW;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OVERFLOW_X;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OVERFLOW_Y;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.checkValue;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.Property.Validator;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.types.Overflow;
import java.util.List;
import java.util.Map;

public class OverflowPropertyProvider implements PropertyProvider {

  private static void update(Term<?> term, Map<String, Object> styles) {
    if (term instanceof TermIdent termIdent) {
      var value = Overflow.find(termIdent.value());
      styles.put(OVERFLOW_X, value);
      styles.put(OVERFLOW_Y, value);
    } else if (term instanceof TermList list) {
      styles.put(OVERFLOW_X, Overflow.find(((TermIdent) list.get(0)).value()));
      styles.put(OVERFLOW_Y, Overflow.find(((TermIdent) list.get(1)).value()));
    }
  }

  @Override
  public List<Property> getProperties() {
    return List.of(
        Property.builder()
            .name(OVERFLOW_X)
            .defaultValue(new TermIdent(Overflow.VISIBLE.name()))
            .validator(checkValue(TermIdent.class, Overflow::contains))
            .updater(put(OVERFLOW_X, TermIdent.class, Overflow::find))
            .build(),
        Property.builder()
            .name(OVERFLOW_Y)
            .defaultValue(new TermIdent(Overflow.VISIBLE.name()))
            .validator(checkValue(TermIdent.class, Overflow::contains))
            .updater(put(OVERFLOW_Y, TermIdent.class, Overflow::find))
            .build(),
        Property.builder()
            .name(OVERFLOW)
            .defaultValue(new TermIdent(Overflow.VISIBLE.name()))
            .validator(validator())
            .updater(OverflowPropertyProvider::update)
            .build());
  }

  private static Validator validator() {
    return checkValue(TermIdent.class, Overflow::contains)
        .or(
            checkValue(
                TermList.class,
                list ->
                    list.size() == 2
                        && list.stream().allMatch(TermIdent.class::isInstance)
                        && list.stream()
                            .map(TermIdent.class::cast)
                            .allMatch(t -> Overflow.contains(t.value()))));
  }
}
