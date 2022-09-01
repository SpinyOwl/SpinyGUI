package com.spinyowl.spinygui.core.style.stylesheet.property.border;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_WIDTH;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.property.border.color.BorderColorProperty;
import com.spinyowl.spinygui.core.style.stylesheet.property.border.style.BorderStyleProperty;
import com.spinyowl.spinygui.core.style.stylesheet.property.border.width.BorderWidthProperty;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermColor;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList.Operator;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BorderProperty extends Property {

  public static final Term<?> DEFAULT_VALUE =
      new TermList(
          Operator.SPACE,
          new TermIdent("medium"),
          new TermIdent("none"),
          new TermIdent("transparent"));

  public BorderProperty() {
    super(
        BORDER,
        DEFAULT_VALUE,
        !INHERITABLE,
        ANIMATABLE,
        BorderProperty::x,
        BorderProperty::test,
        true);
  }

  public static boolean test(Term<?> term) {
    if (term instanceof TermList list) {
      if (list.isEmpty() || list.size() > 3) {
        return false;
      }

      if (list.size() == 1) {
        return BorderStyleProperty.testOne(term);
      } else if (list.size() == 2) {
        return testTwoValues(list.terms());
      } else {
        return testThreeValues(list.terms());
      }
    } else {
      return BorderStyleProperty.testOne(term);
    }
  }

  private static boolean testThreeValues(List<Term<?>> terms) {
    Term<?> first = terms.get(0);
    Term<?> second = terms.get(1);
    Term<?> third = terms.get(2);
    return widthStyleColor(first, second, third)
        || widthStyleColor(first, third, second)
        || widthStyleColor(second, first, third)
        || widthStyleColor(second, third, first)
        || widthStyleColor(third, first, second)
        || widthStyleColor(third, second, first);
  }

  private static boolean widthStyleColor(
      Term<?> probablyWidth, Term<?> probablyStyle, Term<?> probablyColor) {
    return BorderWidthProperty.testOne(probablyWidth)
        && BorderStyleProperty.testOne(probablyStyle)
        && BorderColorProperty.testOne(probablyColor);
  }

  private static boolean testTwoValues(List<Term<?>> terms) {
    return BorderStyleProperty.testOne(terms.get(0))
            && (BorderWidthProperty.testOne(terms.get(1))
                || BorderColorProperty.testOne(terms.get(1)))
        || BorderStyleProperty.testOne(terms.get(1))
            && (BorderWidthProperty.testOne(terms.get(0))
                || BorderColorProperty.testOne(terms.get(0)));
  }

  private static void x(Term<?> term, Map<String, Object> styles) {
    BorderItem i = extract(term);
    if (i.color() != null) {
      styles.putAll(
          StyleUtils.getOneFour(
              new Object[] {i.color()},
              BORDER_TOP_COLOR,
              BORDER_RIGHT_COLOR,
              BORDER_BOTTOM_COLOR,
              BORDER_LEFT_COLOR));
    }
    if (i.width() != null) {
      styles.putAll(
          StyleUtils.getOneFour(
              new Object[] {i.width()},
              BORDER_TOP_WIDTH,
              BORDER_RIGHT_WIDTH,
              BORDER_BOTTOM_WIDTH,
              BORDER_LEFT_WIDTH));
    }
    if (i.style() != null) {
      styles.putAll(
          StyleUtils.getOneFour(
              new Object[] {i.style()},
              BORDER_TOP_STYLE,
              BORDER_RIGHT_STYLE,
              BORDER_BOTTOM_STYLE,
              BORDER_LEFT_STYLE));
    }
  }

  public static void extract(
      Term<?> term,
      String sideStyle,
      String sideWidth,
      String sideColor,
      Map<String, Object> styles) {
    BorderItem borderItem = BorderProperty.extract(term);
    if (borderItem.style() != null) {
      styles.put(sideStyle, borderItem.style());
    }
    if (borderItem.width() != null) {
      styles.put(sideWidth, borderItem.width());
    }
    if (borderItem.color() != null) {
      styles.put(sideColor, borderItem.color());
    }
  }

  public static BorderItem extract(Term<?> term) {
    // collect terms
    List<Term<?>> terms = new ArrayList<>();
    if (term instanceof TermIdent || term instanceof TermLength || term instanceof TermColor) {
      terms.add(term);
    } else if (term instanceof TermList termList) {
      terms.addAll(termList.terms());
    }

    // convert to BorderItem
    var borderItem = new BorderItem();
    if (terms.size() == 1) {
      borderItem.style(BorderStyle.find(((TermIdent) terms.get(0)).value()));
    } else if (terms.size() == 2) {
      extractTwoValues(borderItem, terms);
    } else if (terms.size() == 3) {
      extractThreeValues(borderItem, terms);
    }
    return borderItem;
  }

  private static void extractTwoValues(BorderItem borderItem, List<Term<?>> terms) {
    Term<?> first = terms.get(0);
    Term<?> second = terms.get(1);

    if (BorderStyleProperty.testOne(first)) {
      assignTwoBasedOnFirst(borderItem, (TermIdent) first, second);
    } else if (BorderStyleProperty.testOne(second)) {
      assignTwoBasedOnFirst(borderItem, ((TermIdent) second), first);
    }
  }

  private static void assignTwoBasedOnFirst(
      BorderItem borderItem, TermIdent first, Term<?> second) {
    borderItem.style(BorderStyle.find(first.value()));

    if (BorderWidthProperty.testOne(second)) {
      BorderWidthProperty.extractOne(second).ifPresent(borderItem::width);
    } else if (BorderColorProperty.testOne(second)) {
      BorderColorProperty.extractOne(second).ifPresent(borderItem::color);
    }
  }

  private static void extractThreeValues(BorderItem borderItem, List<Term<?>> terms) {
    Term<?> first = terms.get(0);
    Term<?> second = terms.get(1);
    Term<?> third = terms.get(2);

    if (BorderStyleProperty.testOne(first)) {
      assignThreeBasedOnFirst(borderItem, (TermIdent) first, second, third);
    } else if (BorderStyleProperty.testOne(second)) {
      assignThreeBasedOnFirst(borderItem, (TermIdent) second, first, third);
    } else if (BorderStyleProperty.testOne(third)) {
      assignThreeBasedOnFirst(borderItem, (TermIdent) third, first, second);
    }
  }

  private static void assignThreeBasedOnFirst(
      BorderItem borderItem, TermIdent first, Term<?> second, Term<?> third) {
    borderItem.style(BorderStyle.find(first.value()));
    if (BorderWidthProperty.testOne(second)) {
      BorderWidthProperty.extractOne(second).ifPresent(borderItem::width);
      BorderColorProperty.extractOne(third).ifPresent(borderItem::color);
    } else {
      BorderColorProperty.extractOne(second).ifPresent(borderItem::color);
      BorderWidthProperty.extractOne(third).ifPresent(borderItem::width);
    }
  }
}
