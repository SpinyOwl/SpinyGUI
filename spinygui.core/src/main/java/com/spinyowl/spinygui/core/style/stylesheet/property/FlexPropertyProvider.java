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
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.JUSTIFY_ITEMS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.JUSTIFY_SELF;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PLACE_CONTENT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PLACE_ITEMS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PLACE_SELF;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.checkValue;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermFloat;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList.Operator;
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
            .build(),
        Property.builder()
            .name(JUSTIFY_ITEMS)
            .defaultValue(new TermIdent(AlignItems.STRETCH.name()))
            .updater(put(JUSTIFY_ITEMS, TermIdent.class, AlignItems::find))
            .validator(checkValue(TermIdent.class, AlignItems::contains))
            .build(),
        Property.builder()
            .name(JUSTIFY_SELF)
            .defaultValue(new TermIdent(AlignSelf.AUTO.name()))
            .updater(put(JUSTIFY_SELF, TermIdent.class, AlignSelf::find))
            .validator(checkValue(TermIdent.class, AlignSelf::contains))
            .build(),
        Property.builder()
            .name(PLACE_CONTENT)
            .defaultValue(new TermIdent(AlignContent.STRETCH.name()))
            .updater(
                (term, styles) -> {
                  List<TermIdent> values = alignmentValues(term);
                  styles.put(ALIGN_CONTENT, AlignContent.find(values.get(0).value()));
                  styles.put(JUSTIFY_CONTENT, JustifyContent.find(values.size() > 1 ? values.get(1).value() : values.get(0).value()));
                })
            .validator(FlexPropertyProvider::validPlaceContent)
            .shorthand(true)
            .build(),
        Property.builder()
            .name(PLACE_ITEMS)
            .defaultValue(new TermIdent(AlignItems.STRETCH.name()))
            .updater(
                (term, styles) -> {
                  List<TermIdent> values = alignmentValues(term);
                  styles.put(ALIGN_ITEMS, AlignItems.find(values.get(0).value()));
                  styles.put(JUSTIFY_ITEMS, AlignItems.find(values.size() > 1 ? values.get(1).value() : values.get(0).value()));
                })
            .validator(FlexPropertyProvider::validPlaceItems)
            .shorthand(true)
            .build(),
        Property.builder()
            .name(PLACE_SELF)
            .defaultValue(new TermIdent(AlignSelf.AUTO.name()))
            .updater(
                (term, styles) -> {
                  List<TermIdent> values = alignmentValues(term);
                  styles.put(ALIGN_SELF, AlignSelf.find(values.get(0).value()));
                  styles.put(JUSTIFY_SELF, AlignSelf.find(values.size() > 1 ? values.get(1).value() : values.get(0).value()));
                })
            .validator(FlexPropertyProvider::validPlaceSelf)
            .shorthand(true)
            .build());
  }

  private static boolean validPlaceContent(com.spinyowl.spinygui.core.style.stylesheet.Term<?> term) {
    List<TermIdent> values = alignmentValues(term);
    return !values.isEmpty()
        && values.stream().allMatch(value -> AlignContent.contains(value.value()))
        && (values.size() == 1 || JustifyContent.contains(values.get(1).value()));
  }

  private static boolean validPlaceItems(com.spinyowl.spinygui.core.style.stylesheet.Term<?> term) {
    List<TermIdent> values = alignmentValues(term);
    return !values.isEmpty() && values.stream().allMatch(value -> AlignItems.contains(value.value()));
  }

  private static boolean validPlaceSelf(com.spinyowl.spinygui.core.style.stylesheet.Term<?> term) {
    List<TermIdent> values = alignmentValues(term);
    return !values.isEmpty() && values.stream().allMatch(value -> AlignSelf.contains(value.value()));
  }

  private static List<TermIdent> alignmentValues(com.spinyowl.spinygui.core.style.stylesheet.Term<?> term) {
    if (term instanceof TermIdent ident) {
      return List.of(ident);
    }
    if (term instanceof TermList list && Operator.SPACE.equals(list.operator()) && list.size() == 2) {
      return list.terms().stream().filter(TermIdent.class::isInstance).map(TermIdent.class::cast).toList();
    }
    return List.of();
  }
}
