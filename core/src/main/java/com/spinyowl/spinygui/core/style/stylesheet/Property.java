package com.spinyowl.spinygui.core.style.stylesheet;

import com.spinyowl.spinygui.core.node.Element;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Root class that describes property. Should be used to create new classes which implement {@link
 * Property#compute(Element, String, Map)} )}}.
 */
@Slf4j
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Property {

  public static final String INHERIT = "inherit";
  public static final String INITIAL = "initial";

  public static final boolean ANIMATABLE = true;
  public static final boolean INHERITABLE = true;

  /** Name of css property. */
  @NonNull protected String name;

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
  @NonNull protected String defaultValue;

  protected Term<?> defaultValueTerm;

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
  @NonNull protected boolean inheritable;

  /** Defines if css property could be animated. */
  @NonNull protected boolean animatable;

  /** Used to extract and compute value from string representation and put to style map. */
  @NonNull protected BiConsumer<String, Map<String, Object>> valueExtractor;

  protected Extractor extractor;

  /** Used to validate string value before extraction. */
  @NonNull protected Predicate<String> valueValidator;

  protected Validator validator;

  @NonNull protected boolean shorthand;

  public Property(
      @NonNull String name,
      @NonNull Term<?> defaultValueTerm,
      boolean inheritable,
      boolean animatable,
      @NonNull Extractor extractor,
      @NonNull Validator validator) {
    this.name = name;
    this.defaultValueTerm = defaultValueTerm;
    this.inheritable = inheritable;
    this.animatable = animatable;
    this.extractor = extractor;
    this.validator = validator;
  }

  public Property(
      String name,
      String defaultValue,
      boolean inheritable,
      boolean animatable,
      BiConsumer<String, Map<String, Object>> valueExtractor,
      Predicate<String> valueValidator) {
    this(name, defaultValue, inheritable, animatable, valueExtractor, valueValidator, false);
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
        if (log.isErrorEnabled()) {
          log.error(
              "Error during extracting value from '{}' with '{}' extractor. {}",
              value,
              valueExtractor,
              t.getMessage());
        }

        // in case if we have exception - recalculating value.
        computeAbsent(element, styles);
      }
    }
  }

  public void computeAbsent(Element element, @NonNull Map<String, Object> styles) {
    if (inheritable()) {
      this.inheritedValue(element, styles);
    } else {
      defaultComputedValue(styles);
    }
  }

  public void inheritedValue(Element element, @NonNull Map<String, Object> styles) {
    var parentStyle = element.parent().resolvedStyle();
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

  @FunctionalInterface
  public interface Extractor extends BiConsumer<Term<?>, Map<String, Object>> {

    static Extractor from(BiConsumer<Term<?>, Map<String, Object>> biConsumer) {
      return biConsumer::accept;
    }

    @Override
    default void accept(Term<?> term, Map<String, Object> stringObjectMap) {
      extract(term, stringObjectMap);
    }

    /**
     * Used to resolve term to style map.
     *
     * @param term term to resolve.
     * @param resolvedStyles resolved style map.
     */
    void extract(Term<?> term, Map<String, Object> resolvedStyles);
  }

  @FunctionalInterface
  public interface Validator extends Predicate<Term<?>> {
    static Validator from(Predicate<Term<?>> predicate) {
      return predicate::test;
    }

    @Override
    default boolean test(Term<?> term) {
      return validate(term);
    }

    boolean validate(Term<?> term);
  }
}
