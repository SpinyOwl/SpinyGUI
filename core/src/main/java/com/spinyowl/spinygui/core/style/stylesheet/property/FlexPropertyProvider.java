package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ALIGN_CONTENT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ALIGN_ITEMS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ALIGN_SELF;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_BASIS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_DIRECTION;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_GROW;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_SHRINK;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_WRAP;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.JUSTIFY_CONTENT;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.checkValue;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermFloat;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.flex.AlignContent;
import com.spinyowl.spinygui.core.style.types.flex.AlignItems;
import com.spinyowl.spinygui.core.style.types.flex.AlignSelf;
import com.spinyowl.spinygui.core.style.types.flex.FlexDirection;
import com.spinyowl.spinygui.core.style.types.flex.FlexWrap;
import com.spinyowl.spinygui.core.style.types.flex.JustifyContent;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.List;

public class FlexPropertyProvider implements PropertyProvider {
  public static final String AUTO = "auto";

  @Override
  public List<Property> getProperties() {
    return List.of(
        new Property(
            ALIGN_CONTENT,
            new TermIdent(AlignContent.STRETCH.name()),
            false,
            false,
            put(ALIGN_CONTENT, TermIdent.class, AlignContent::find),
            checkValue(TermIdent.class, AlignContent::contains)),
        new Property(
            ALIGN_ITEMS,
            new TermIdent(AlignItems.STRETCH.name()),
            false,
            false,
            put(ALIGN_ITEMS, TermIdent.class, AlignItems::find),
            checkValue(TermIdent.class, AlignItems::contains)),
        new Property(
            ALIGN_SELF,
            new TermIdent(AlignSelf.AUTO.name()),
            false,
            false,
            put(ALIGN_SELF, TermIdent.class, AlignSelf::find),
            checkValue(TermIdent.class, AlignSelf::contains)),
        new Property(
            FLEX_BASIS,
            new TermIdent(AUTO),
            false,
            false,
            (term, styles) -> {
              if (term instanceof TermIdent) styles.put(FLEX_BASIS, Unit.AUTO);
              else if (term instanceof TermLength tl) styles.put(FLEX_BASIS, tl.value());
            },
            checkValue(TermIdent.class, AUTO::equalsIgnoreCase).or(TermLength.class::isInstance)),
        new Property(
            FLEX_DIRECTION,
            new TermIdent(FlexDirection.ROW.name()),
            false,
            false,
            put(FLEX_DIRECTION, TermIdent.class, FlexDirection::find),
            checkValue(TermIdent.class, FlexDirection::contains)),
        new Property(
            FLEX_GROW,
            new TermFloat(0F),
            false,
            false,
            put(FLEX_GROW, TermFloat.class, t -> t),
            TermFloat.class::isInstance),
        new Property(
            FLEX_SHRINK,
            new TermFloat(0F),
            false,
            false,
            put(FLEX_SHRINK, TermFloat.class, t -> t),
            TermFloat.class::isInstance),
        new Property(
            FLEX_WRAP,
            new TermIdent(FlexWrap.NOWRAP.name()),
            false,
            false,
            put(FLEX_WRAP, TermIdent.class, FlexWrap::find),
            checkValue(TermIdent.class, FlexWrap::contains)),
        new Property(
            JUSTIFY_CONTENT,
            new TermIdent(JustifyContent.FLEX_START.name()),
            false,
            false,
            put(JUSTIFY_CONTENT, TermIdent.class, JustifyContent::find),
            checkValue(TermIdent.class, JustifyContent::contains)));
  }
}
