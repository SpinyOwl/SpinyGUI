package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ALIGN_CONTENT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ALIGN_ITEMS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.ALIGN_SELF;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_BASIS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_DIRECTION;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_FLOW;
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

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermFloat;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermInteger;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermList.Operator;
import com.spinyowl.spinygui.core.style.types.flex.AlignContent;
import com.spinyowl.spinygui.core.style.types.flex.AlignItems;
import com.spinyowl.spinygui.core.style.types.flex.AlignSelf;
import com.spinyowl.spinygui.core.style.types.flex.FlexDirection;
import com.spinyowl.spinygui.core.style.types.flex.FlexWrap;
import com.spinyowl.spinygui.core.style.types.flex.JustifyContent;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Registers flex longhands and expands validated shorthands into their components. */
public class FlexPropertyProvider implements PropertyProvider {
  public static final String AUTO = "auto";

  @Override
  public List<Property> getProperties() {
    return List.of(
        new FlexShorthand(FLEX,
            new TermList(Operator.SPACE, new TermFloat(0F), new TermFloat(1F), new TermIdent(AUTO)),
            FlexPropertyProvider::flexValues, List.of(FLEX_GROW, FLEX_SHRINK, FLEX_BASIS)),
        new FlexShorthand(FLEX_FLOW, new TermIdent("row"),
            FlexPropertyProvider::flowValues, List.of(FLEX_DIRECTION, FLEX_WRAP)),
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
            .updater((term, styles) -> styles.put(FLEX_GROW, flexFactor(term)))
            .validator(FlexPropertyProvider::validFactor)
            .build(),
        Property.builder()
            .name(FLEX_SHRINK)
            .defaultValue(new TermFloat(0F))
            .updater((term, styles) -> styles.put(FLEX_SHRINK, flexFactor(term)))
            .validator(FlexPropertyProvider::validFactor)
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

  /** Returns complete flex components, or null when the shorthand is invalid. */
  private static Map<String, Object> flexValues(Term<?> term) {
    if (term instanceof TermIdent ident && "none".equalsIgnoreCase(ident.value())) {
      return Map.of(FLEX_GROW, 0F, FLEX_SHRINK, 0F, FLEX_BASIS, Unit.AUTO);
    }
    List<Term<?>> values = shorthandValues(term);
    if (values.isEmpty() || values.size() > 3) {
      return null;
    }
    float grow = 1F;
    float shrink = 1F;
    Unit basis = Length.pixel(0);
    boolean hasBasis = false;
    int factors = 0;
    int firstFactor = -1;
    for (int index = 0; index < values.size(); index++) {
      Term<?> value = values.get(index);
      Float number = flexFactor(value);
      if (number != null && factors < 2) {
        if (!Float.isFinite(number) || number < 0
            || factors == 1 && index != firstFactor + 1) {
          return null;
        }
        if (factors == 0) {
          grow = number;
          firstFactor = index;
        } else {
          shrink = number;
        }
        factors++;
      } else {
        if (hasBasis) {
          return null;
        }
        if (value instanceof TermIdent ident && AUTO.equalsIgnoreCase(ident.value())) {
          basis = Unit.AUTO;
        } else if (value instanceof TermLength length
            && Float.isFinite(length.value().value().floatValue())
            && length.value().value().floatValue() >= 0) {
          basis = length.value();
        } else if (number != null && number == 0 && factors == 2) {
          basis = Length.pixel(0);
        } else {
          return null;
        }
        hasBasis = true;
      }
    }
    return Map.of(FLEX_GROW, grow, FLEX_SHRINK, shrink, FLEX_BASIS, basis);
  }

  /** Rejects negative and non-finite flex factors before applying a longhand. */
  private static boolean validFactor(Term<?> term) {
    Float value = flexFactor(term);
    return value != null && Float.isFinite(value) && value >= 0;
  }

  /** Reads both numeric representations emitted by the CSS parser. */
  private static Float flexFactor(Term<?> term) {
    if (term instanceof TermFloat number) {
      return number.value();
    }
    if (term instanceof TermInteger number) {
      return number.value().floatValue();
    }
    return null;
  }

  /** Resolves direction and wrapping in either order, resetting omitted components. */
  private static Map<String, Object> flowValues(Term<?> term) {
    List<Term<?>> values = shorthandValues(term);
    if (values.isEmpty() || values.size() > 2) {
      return null;
    }
    FlexDirection direction = null;
    FlexWrap wrap = null;
    for (Term<?> value : values) {
      if (!(value instanceof TermIdent ident)) {
        return null;
      }
      if (direction == null && FlexDirection.contains(ident.value())) {
        direction = FlexDirection.find(ident.value());
      } else if (wrap == null && FlexWrap.contains(ident.value())) {
        wrap = FlexWrap.find(ident.value());
      } else {
        return null;
      }
    }
    return Map.of(FLEX_DIRECTION, direction == null ? FlexDirection.ROW : direction,
        FLEX_WRAP, wrap == null ? FlexWrap.NOWRAP : wrap);
  }

  /** Accepts only a single term or a space-separated list. */
  private static List<Term<?>> shorthandValues(Term<?> term) {
    if (term instanceof TermList list) {
      return Operator.SPACE.equals(list.operator()) ? list.terms() : List.of();
    }
    return term == null ? List.of() : List.of(term);
  }

  /** Keeps numeric zeros unambiguous and inherits each expanded component independently. */
  private static final class FlexShorthand extends Property {
    /** Immutable component names used for explicit inheritance. */
    private final List<String> components;

    /** Binds a shorthand to its validated expansion and initial value. */
    private FlexShorthand(String name, Term<?> initial,
        Function<Term<?>, Map<String, Object>> expansion,
        List<String> components) {
      super(name, initial,
          (term, styles) -> styles.putAll(expansion.apply(term)),
          term -> expansion.apply(term) != null);
      this.shorthand = true;
      this.components = components;
    }

    /** Rejects malformed shorthands before the generic unitless-zero length fallback. */
    @Override
    public void apply(Element element, Term<?> value, ResolvedStyle targetStyle) {
      if (value == null || TermIdent.INITIAL.equals(value) || TermIdent.INHERIT.equals(value)
          || validator.test(value)) {
        super.apply(element, value, targetStyle);
      }
    }

    /** Copies resolved longhands, using the shorthand initial value when there is no parent. */
    @Override
    public void inheritedValue(Element element, Map<String, Object> styles) {
      updater.accept(defaultValue, styles);
      if (element.parent() != null) {
        Map<String, Object> parentStyles = element.parent().resolvedStyle().styles();
        for (String component : components) {
          if (parentStyles.containsKey(component)) {
            styles.put(component, parentStyles.get(component));
          }
        }
      }
    }
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
