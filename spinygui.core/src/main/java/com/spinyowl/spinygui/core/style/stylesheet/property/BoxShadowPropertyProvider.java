package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BOX_SHADOW;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermColor;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList.Operator;
import com.spinyowl.spinygui.core.style.types.BoxShadow;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BoxShadowPropertyProvider implements PropertyProvider {

  private static final String INSET = "INSET";
  private static final String NONE = "none";

  @Override
  public List<Property> getProperties() {
    return List.of(
        Property.builder()
            .name(BOX_SHADOW)
            .defaultValue(new TermIdent(NONE))
            .animatable(true)
            .updater(BoxShadowPropertyProvider::extract)
            .validator(BoxShadowPropertyProvider::validate)
            .build());
  }

  private static boolean validate(Term<?> term) {
    if (term instanceof TermIdent termIdent && NONE.equalsIgnoreCase(termIdent.value())) {
      return true;
    }

    if (term instanceof TermList termList) {
      if (termList.operator() == Operator.COMMA) {
        return termList.terms().stream().allMatch(BoxShadowPropertyProvider::validate);
      } else if (termList.operator() == Operator.SPACE) {
        return validateSpaceSeparatedList(termList);
      }
      return false;
    }
    return false;
  }

  private static boolean validateSpaceSeparatedList(TermList termList) {
    if (termList.terms().size() < 2 || termList.terms().size() > 6) {
      return false;
    }

    var lengthArgs = 0;
    var insetArgs = 0;
    var colorArgs = 0;

    for (Term<?> arg : termList.terms()) {
      boolean isInset =
          arg instanceof TermIdent termIdent && INSET.equalsIgnoreCase(termIdent.value());
      boolean isLength = arg instanceof TermLength;
      boolean isColor = arg instanceof TermColor;
      if (!isInset && !isColor && !isLength) {
        return false;
      }
      // @formatter:off
      if (isInset) insetArgs++;
      if (isColor) colorArgs++;
      if (isLength) lengthArgs++;
      // @formatter:on
    }
    return validateArgNumbers(lengthArgs, insetArgs, colorArgs);
  }

  private static boolean validateArgNumbers(int lengthArgs, int insetArgs, int colorArgs) {
    return lengthArgs >= 2 && lengthArgs <= 4 && insetArgs <= 1 && colorArgs <= 1;
  }

  private static void extract(Term<?> term, Map<String, Object> styles) {
    if (term instanceof TermIdent termIdent && NONE.equalsIgnoreCase(termIdent.value())) {
      styles.put(BOX_SHADOW, Set.of(BoxShadow.NO_SHADOW));
    } else if (term instanceof TermList list) {
      Set<BoxShadow> shadows = new HashSet<>();
      if (list.operator() == Operator.COMMA) {
        shadows =
            list.terms().stream()
                .filter(TermList.class::isInstance)
                .map(TermList.class::cast)
                .map(BoxShadowPropertyProvider::extractOneShadowSet)
                .collect(Collectors.toSet());
      } else if (list.operator() == Operator.SPACE) {
        shadows.add(extractOneShadowSet(list));
      }
      styles.put(BOX_SHADOW, shadows);
    }
  }

  private static BoxShadow extractOneShadowSet(TermList list) {
    var shadow = new BoxShadow();
    var shadowArgs = new Length<?>[4];
    int shadowArgIndex = -1;
    Color color = null;
    Boolean inset = null;

    for (Term<?> arg : list.terms()) {
      if (arg instanceof TermIdent termIdent && INSET.equalsIgnoreCase(termIdent.value())) {
        inset = Boolean.TRUE;
      } else if (arg instanceof TermLength termLength) {
        shadowArgs[++shadowArgIndex] = termLength.value();
      } else if (arg instanceof TermColor termColor) {
        color = termColor.value();
      }
    }
    // @formatter:off
    if (color == null) color = Color.BLACK;
    if (inset == null) inset = Boolean.FALSE;
    // @formatter:on
    shadow.hOffset(shadowArgs[0]);
    shadow.vOffset(shadowArgs[1]);
    shadow.blur(shadowArgs[2] == null ? Length.ZERO : shadowArgs[2]);
    shadow.spread(shadowArgs[3]);
    shadow.color(color);
    shadow.inset(inset);

    return shadow;
  }
}
