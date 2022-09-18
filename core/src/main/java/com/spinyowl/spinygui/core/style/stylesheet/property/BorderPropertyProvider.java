package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_WIDTH;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermColor;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList.Operator;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

public class BorderPropertyProvider implements PropertyProvider {

  public static final Term<?> DEFAULT_VALUE =
      new TermList(
          Operator.SPACE,
          new TermIdent("medium"),
          new TermIdent("none"),
          new TermIdent("transparent"));

  @Override
  public List<Property> getProperties() {
    return List.of(
        new Property(
            BORDER,
            DEFAULT_VALUE,
            false,
            true,
            BorderPropertyProvider::update,
            BorderPropertyProvider::validate,
            true),
        new Property(
            BORDER_BOTTOM,
            BorderPropertyProvider.DEFAULT_VALUE,
            false,
            true,
            (term, styles) ->
                BorderPropertyProvider.extract(
                    term, BORDER_BOTTOM_STYLE, BORDER_BOTTOM_WIDTH, BORDER_BOTTOM_COLOR, styles),
            BorderPropertyProvider::validate,
            true),
        new Property(
            BORDER_LEFT,
            BorderPropertyProvider.DEFAULT_VALUE,
            false,
            true,
            (value, styles) ->
                BorderPropertyProvider.extract(
                    value, BORDER_LEFT_STYLE, BORDER_LEFT_WIDTH, BORDER_LEFT_COLOR, styles),
            BorderPropertyProvider::validate,
            true),
        new Property(
            BORDER_RIGHT,
            BorderPropertyProvider.DEFAULT_VALUE,
            false,
            true,
            (value, styles) ->
                BorderPropertyProvider.extract(
                    value, BORDER_RIGHT_STYLE, BORDER_RIGHT_WIDTH, BORDER_RIGHT_COLOR, styles),
            BorderPropertyProvider::validate,
            true),
        new Property(
            BORDER_TOP,
            BorderPropertyProvider.DEFAULT_VALUE,
            false,
            true,
            (value, styles) ->
                BorderPropertyProvider.extract(
                    value, BORDER_TOP_STYLE, BORDER_TOP_WIDTH, BORDER_TOP_COLOR, styles),
            BorderPropertyProvider::validate,
            true));
  }

  public static boolean validate(Term<?> term) {
    if (term instanceof TermList list) {
      if (list.isEmpty() || list.size() > 3) {
        return false;
      }

      if (list.size() == 1) {
        return BorderStylePropertyProvider.testOne(term);
      } else if (list.size() == 2) {
        return testTwoValues(list.terms());
      } else {
        return testThreeValues(list.terms());
      }
    } else {
      return BorderStylePropertyProvider.testOne(term);
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
    return BorderWidthPropertyProvider.validateOne(probablyWidth)
        && BorderStylePropertyProvider.testOne(probablyStyle)
        && BorderColorPropertyProvider.testOne(probablyColor);
  }

  private static boolean testTwoValues(List<Term<?>> terms) {
    return BorderStylePropertyProvider.testOne(terms.get(0))
            && (BorderWidthPropertyProvider.validateOne(terms.get(1))
                || BorderColorPropertyProvider.testOne(terms.get(1)))
        || BorderStylePropertyProvider.testOne(terms.get(1))
            && (BorderWidthPropertyProvider.validateOne(terms.get(0))
                || BorderColorPropertyProvider.testOne(terms.get(0)));
  }

  private static void update(Term<?> term, Map<String, Object> styles) {
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
    BorderItem borderItem = BorderPropertyProvider.extract(term);
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

    if (BorderStylePropertyProvider.testOne(first)) {
      assignTwoBasedOnFirst(borderItem, (TermIdent) first, second);
    } else if (BorderStylePropertyProvider.testOne(second)) {
      assignTwoBasedOnFirst(borderItem, ((TermIdent) second), first);
    }
  }

  private static void assignTwoBasedOnFirst(
      BorderItem borderItem, TermIdent first, Term<?> second) {
    borderItem.style(BorderStyle.find(first.value()));

    if (BorderWidthPropertyProvider.validateOne(second)) {
      BorderWidthPropertyProvider.extractOne(second).ifPresent(borderItem::width);
    } else if (BorderColorPropertyProvider.testOne(second)) {
      BorderColorPropertyProvider.extractOne(second).ifPresent(borderItem::color);
    }
  }

  private static void extractThreeValues(BorderItem borderItem, List<Term<?>> terms) {
    Term<?> first = terms.get(0);
    Term<?> second = terms.get(1);
    Term<?> third = terms.get(2);

    if (BorderStylePropertyProvider.testOne(first)) {
      assignThreeBasedOnFirst(borderItem, (TermIdent) first, second, third);
    } else if (BorderStylePropertyProvider.testOne(second)) {
      assignThreeBasedOnFirst(borderItem, (TermIdent) second, first, third);
    } else if (BorderStylePropertyProvider.testOne(third)) {
      assignThreeBasedOnFirst(borderItem, (TermIdent) third, first, second);
    }
  }

  private static void assignThreeBasedOnFirst(
      BorderItem borderItem, TermIdent first, Term<?> second, Term<?> third) {
    borderItem.style(BorderStyle.find(first.value()));
    if (BorderWidthPropertyProvider.validateOne(second)) {
      BorderWidthPropertyProvider.extractOne(second).ifPresent(borderItem::width);
      BorderColorPropertyProvider.extractOne(third).ifPresent(borderItem::color);
    } else {
      BorderColorPropertyProvider.extractOne(second).ifPresent(borderItem::color);
      BorderWidthPropertyProvider.extractOne(third).ifPresent(borderItem::width);
    }
  }

  @Data
  @NoArgsConstructor
  public static class BorderItem {

    private Color color;
    private BorderStyle style;
    private Length<?> width;
  }
}
