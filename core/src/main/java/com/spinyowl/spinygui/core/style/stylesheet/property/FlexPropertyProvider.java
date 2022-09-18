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
        Property.builder()
            .name(ALIGN_CONTENT)
            .defaultValue(new TermIdent(AlignContent.STRETCH.name()))
            .updater(put(ALIGN_CONTENT, TermIdent.class, AlignContent::find))
            .validator(checkValue(TermIdent.class, AlignContent::contains))
            .build(),
        Property.builder()
            .name(ALIGN_ITEMS)
            .defaultValue(new TermIdent(AlignItems.STRETCH.name()))
            .updater(put(ALIGN_ITEMS, TermIdent.class, AlignItems::find))
            .validator(checkValue(TermIdent.class, AlignItems::contains))
            .build(),
        Property.builder()
            .name(ALIGN_SELF)
            .defaultValue(new TermIdent(AlignSelf.AUTO.name()))
            .updater(put(ALIGN_SELF, TermIdent.class, AlignSelf::find))
            .validator(checkValue(TermIdent.class, AlignSelf::contains))
            .build(),
        Property.builder()
            .name(FLEX_BASIS)
            .defaultValue(new TermIdent(AUTO))
            .updater(
                (term, styles) -> {
                  if (term instanceof TermIdent) styles.put(FLEX_BASIS, Unit.AUTO);
                  else if (term instanceof TermLength tl) styles.put(FLEX_BASIS, tl.value());
                })
            .validator(
                checkValue(TermIdent.class, AUTO::equalsIgnoreCase)
                    .or(TermLength.class::isInstance))
            .build(),
        Property.builder()
            .name(FLEX_DIRECTION)
            .defaultValue(new TermIdent(FlexDirection.ROW.name()))
            .updater(put(FLEX_DIRECTION, TermIdent.class, FlexDirection::find))
            .validator(checkValue(TermIdent.class, FlexDirection::contains))
            .build(),
        Property.builder()
            .name(FLEX_GROW)
            .defaultValue(new TermFloat(0F))
            .updater(put(FLEX_GROW, TermFloat.class, t -> t))
            .validator(TermFloat.class::isInstance)
            .build(),
        Property.builder()
            .name(FLEX_SHRINK)
            .defaultValue(new TermFloat(0F))
            .updater(put(FLEX_SHRINK, TermFloat.class, t -> t))
            .validator(TermFloat.class::isInstance)
            .build(),
        Property.builder()
            .name(FLEX_WRAP)
            .defaultValue(new TermIdent(FlexWrap.NOWRAP.name()))
            .updater(put(FLEX_WRAP, TermIdent.class, FlexWrap::find))
            .validator(checkValue(TermIdent.class, FlexWrap::contains))
            .build(),
        Property.builder()
            .name(JUSTIFY_CONTENT)
            .defaultValue(new TermIdent(JustifyContent.FLEX_START.name()))
            .updater(put(JUSTIFY_CONTENT, TermIdent.class, JustifyContent::find))
            .validator(checkValue(TermIdent.class, JustifyContent::contains))
            .build());
  }
}
