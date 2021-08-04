package com.spinyowl.spinygui.core.style.stylesheet;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Root class that describes property. Should be used to create new classes which implement {@link
 * Property#compute(Element, String, Map)} )}}.
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

  /** Used to extract and compute value from string representation and put to style map. */
  @NonNull protected final BiConsumer<String, Map<String, Object>> valueExtractor;

  /** Used to validate string value before extraction. */
  @NonNull protected final Predicate<String> valueValidator;

  protected final boolean shorthand;


  public Property(
      String name,
      String defaultValue,
      boolean inherited,
      boolean animatable,
      BiConsumer<String, Map<String, Object>> valueExtractor,
      Predicate<String> valueValidator) {
    this(name, defaultValue, inherited, animatable, valueExtractor, valueValidator, false);
  }

  /**
   * Used to compute css style property value for specified element.
   *
   * @param element element to update calculated style.
   * @param value string representation of css property value.
   */
  public void compute(@NonNull Element element, String value, @NonNull Map<String, Object> styles) {
    if (value == null) {
      computeAbsent(element, styles);
    } else if (INITIAL.equalsIgnoreCase(value)) {
      valueExtractor.accept(defaultValue, styles);
    } else if (INHERIT.equalsIgnoreCase(value)) {
      inheritedValue(element, styles);
    } else if (valueValidator.test(value)) {
      try {
        valueExtractor.accept(value, styles);
      } catch (Exception t) {
        log.error(
            "Error during extracting value from '{}' with '{}' extractor. {}",
            value,
            valueExtractor,
            t.getMessage());

        // in case if we have exception - recalculating value.
        computeAbsent(element, styles);
      }
    }
  }

  public void computeAbsent(Element element, @NonNull Map<String, Object> styles) {
    if (inherited()) {
      this.inheritedValue(element, styles);
    } else {
      defaultComputedValue(styles);
    }
  }

  public void inheritedValue(Element element, @NonNull Map<String, Object> styles) {
    var parentStyle = StyleUtils.getParentCalculatedStyle(element);
    if (!shorthand) {
      if (parentStyle != null) {
        styles.put(this.name, parentStyle.getSafe(this.name));
      } else {
        defaultComputedValue(styles);
      }
    }
  }

  public void defaultComputedValue(@NonNull Map<String, Object> styles) {
    if (!shorthand) {
      valueExtractor.accept(defaultValue, styles);
    }
  }
}
