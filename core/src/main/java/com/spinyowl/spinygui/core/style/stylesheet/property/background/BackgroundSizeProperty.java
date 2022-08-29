package com.spinyowl.spinygui.core.style.stylesheet.property.background;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_SIZE;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.contains;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.types.background.BackgroundSize;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.List;
import java.util.Map;

public class BackgroundSizeProperty extends Property {

  private static final String AUTO = "auto";
  private static final String COVER = "cover";
  private static final String CONTAIN = "contain";
  private static final List<String> values = List.of(AUTO, COVER, CONTAIN);

  public BackgroundSizeProperty() {
    super(
        BACKGROUND_SIZE,
        new TermIdent(AUTO),
        !INHERITABLE,
        ANIMATABLE,
        BackgroundSizeProperty::update,
        getValidator());
  }

  private static Validator getValidator() {
    return check(TermIdent.class, v -> contains(v, values))
        .or(TermLength.class::isInstance)
        .or(
            check(
                TermList.class,
                termList -> {
                  if (termList.isEmpty()) return false;
                  if (termList.size() > 2) return false;
                  if (termList.size() == 1)
                    return check(TermIdent.class, v -> contains(v, values))
                        .or(TermLength.class::isInstance)
                        .test(termList.get(0));
                  return testOne(termList.get(0)) && testOne(termList.get(1));
                }));
  }

  private static boolean testOne(Term<?> term) {
    return check(TermIdent.class, AUTO::equalsIgnoreCase)
        .or(TermLength.class::isInstance)
        .test(term);
  }

  private static void update(Term<?> term, Map<String, Object> styles) {
    updateByOneTerm(term, styles);
    if (term instanceof TermList termList) {
      if (termList.size() == 1) {
        updateByOneTerm(termList.get(0), styles);
      } else {
        Unit first = Unit.AUTO;
        Unit second = Unit.AUTO;
        if (termList.get(0) instanceof TermLength termLength) {
          first = termLength.value();
        }
        if (termList.get(1) instanceof TermLength termLength) {
          second = termLength.value();
        }
        styles.put(BACKGROUND_SIZE, BackgroundSize.createSize(first, second));
      }
    }
  }

  private static void updateByOneTerm(Term<?> term, Map<String, Object> styles) {
    if (term instanceof TermIdent termIdent) {
      String value = termIdent.value();
      if (COVER.equalsIgnoreCase(value)) {
        styles.put(BACKGROUND_SIZE, BackgroundSize.createCover());
      } else if (CONTAIN.equalsIgnoreCase(value)) {
        styles.put(BACKGROUND_SIZE, BackgroundSize.createContain());
      } else if (AUTO.equalsIgnoreCase(value)) {
        styles.put(BACKGROUND_SIZE, BackgroundSize.createSize(Unit.AUTO));
      }
    } else if (term instanceof TermLength termLength) {
      styles.put(BACKGROUND_SIZE, BackgroundSize.createSize(termLength.value()));
    }
  }
}
