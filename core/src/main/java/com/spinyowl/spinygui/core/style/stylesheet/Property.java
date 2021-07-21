package com.spinyowl.spinygui.core.style.stylesheet;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Root class that describes property. Should be used to create new classes which implement {@link
 * Property#compute(Element, String)}}.
 */
@Slf4j
@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Property {

  public static final String INHERIT = "inherit";
  public static final String INITIAL = "initial";

  public static final boolean ANIMATABLE = true;
  public static final boolean INHERITED = true;

  /** Name of css property. */
  @NonNull protected final String name;

  /**
   * Default value of css property. Could not be null. This value used for next cases:
   *
   * <ul>
   *   <li>element is root element in element tree.
   *   <li>element value is {@link Property#INITIAL}
   *   <li>there was an issue during computing value using value extractor - treats as no property
   *       specified in style.
   * </ul>
   */
  @NonNull protected final String defaultValue;

  /**
   * Defines if css property for element should be inherited from parent element. <br>
   * When no value for an inherited property has been specified on an element, the element gets the
   * computed value of that property on its parent element. Only the root element of the document
   * gets the initial value given in the property's summary. <br>
   * When no value for a non-inherited property has been specified on an element, the element gets
   * the initial value of that property (as specified in the property's summary). <br>
   * The <b>inherit</b> keyword allows authors to explicitly specify inheritance. It works on both
   * inherited and non-inherited properties.
   */
  protected final boolean inherited;

  /** Defines if css property could be animated. */
  protected final boolean animatable;

  /** Used to extract and compute value from string representation. */
  @NonNull protected final Function<String, Map<String, Object>> valueExtractor;

  /** Used to validate string value before extraction. */
  @NonNull protected final Predicate<String> valueValidator;

  protected final boolean shorthand;

  public Property(
      String name,
      String defaultValue,
      boolean inherited,
      boolean animatable,
      Function<String, Map<String, Object>> valueExtractor,
      Predicate<String> valueValidator) {
    this(name, defaultValue, inherited, animatable, valueExtractor, valueValidator, false);
  }

  /**
   * Used to compute css style property value for specified element.
   *
   * @param element element to update calculated style.
   * @param value string representation of css property value.
   * @return computed value.
   */
  public Map<String, Object> compute(Element element, String value) {
    Objects.requireNonNull(element);

    Map<String, Object> computedValue = null;
    if (value == null) {
      computedValue = computeAbsent(element);
    } else {

      if (INITIAL.equalsIgnoreCase(value)) {
        computedValue = valueExtractor.apply(defaultValue);
      } else if (INHERIT.equalsIgnoreCase(value)) {
        computedValue = inheritedValue(element);
      } else if (valueValidator.test(value)) {
        try {
          computedValue = valueExtractor.apply(value);
        } catch (Exception t) {
          log.error(
              "Error during extracting value from '{}' with '{}' extractor. {}",
              value,
              valueExtractor,
              t.getMessage());
          computedValue = null;
        }
      }
      // in case if we have some exception or value extractor returned null - recalculating value.
      if (computedValue == null) {
        computedValue = computeAbsent(element);
      }
    }
    return computedValue;
  }

  public Map<String, Object> computeAbsent(Element element) {
    return inherited() ? this.inheritedValue(element) : defaultComputedValue();
  }

  public Map<String, Object> inheritedValue(Element element) {
    var parentStyle = StyleUtils.getParentCalculatedStyle(element);
    if (shorthand) {
      return Map.of();
    }
    return parentStyle != null
        ? Map.of(this.name, parentStyle.get(this.name))
        : defaultComputedValue();
  }

  public Map<String, Object> defaultComputedValue() {
    return shorthand ? Map.of() : valueExtractor.apply(defaultValue);
  }
}
