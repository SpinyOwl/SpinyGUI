package com.spinyowl.spinygui.core.style.stylesheet.property.padding;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_BOTTOM;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_LEFT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_RIGHT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_TOP;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.getOneFourLengths;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.setOneFour;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.Map;

public class PaddingProperty extends Property {

  public PaddingProperty() {
    super(
        PADDING,
        new TermLength(Length.ZERO),
        !INHERITABLE,
        ANIMATABLE,
        PaddingProperty::extract,
        PaddingProperty::test,
        true);
  }

  public static void extract(Term<?> term, Map<String, Object> styles) {

    setOneFour(
        getOneFourLengths(term).toArray(),
        PADDING_TOP,
        PADDING_RIGHT,
        PADDING_BOTTOM,
        PADDING_LEFT,
        styles);
  }

  public static boolean test(Term<?> term) {
    return term instanceof TermLength
        || term instanceof TermList list
            && list.terms().stream().allMatch(TermLength.class::isInstance);
  }
}
