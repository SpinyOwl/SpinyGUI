package com.spinyowl.spinygui.core.style.stylesheet.property.margin;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_BOTTOM;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_LEFT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_RIGHT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_TOP;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.getOneFourLengths;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.setOneFour;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.Map;

public class MarginProperty extends Property {

  public MarginProperty() {
    super(
        MARGIN,
        new TermLength(Length.ZERO),
        !INHERITABLE,
        ANIMATABLE,
        MarginProperty::extract,
        MarginProperty::test,
        true);
  }

  public static void extract(Term<?> term, Map<String, Object> styles) {
    setOneFour(
        getOneFourLengths(term).toArray(),
        MARGIN_TOP,
        MARGIN_RIGHT,
        MARGIN_BOTTOM,
        MARGIN_LEFT,
        styles);
  }

  public static boolean test(Term<?> term) {
    return term instanceof TermLength
        || term instanceof TermList list
            && list.terms().stream().allMatch(TermLength.class::isInstance);
  }
}
