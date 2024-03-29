package com.spinyowl.spinygui.core.style.stylesheet;

import static com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent.INHERIT;
import static com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent.INITIAL;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * Root class that describes property. Should be used to create new classes which implement {@link
 * Property#apply(Element, Term)} )}}.
 */
@Slf4j
@Getter
@SuperBuilder
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Property {

  /** Name of css property. */
  @NonNull protected String name;

  /**
   * Default value of css property. Could not be null. This value used for next cases:
   *
   * <ul>
   *   <li>element is root element in element tree.
   *   <li>element value is {@link TermIdent#INITIAL}
   *   <li>there was an issue during computing value using value extractor - treats as no property
   *       specified in style.
   * </ul>
   */
  @NonNull protected Term<?> defaultValue;

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
  protected boolean inheritable;

  /** Defines if css property could be animated. */
  protected boolean animatable;

  /** Used to compute value from term and update style map with it. */
  @NonNull protected Updater updater;

  /** Used to validate term value before extraction. */
  @NonNull protected Validator validator;

  /** Defines if this property is shorthand or not. */
  protected boolean shorthand;

  /**
   * Creates validator that checks if term is instance of specified class and if so, casts to this
   * class and applies provided predicate.
   *
   * @param clazz class to check.
   * @param predicate predicate to apply.
   * @return validator.
   */
  public static <T> Validator checkValue(Class<? extends Term<T>> clazz, Predicate<T> predicate) {
    return term -> clazz.isInstance(term) && predicate.test(clazz.cast(term).value);
  }

  /** Casts term to provided term class and applies function to its value. */
  public static <T, O> O convert(
      Term<?> term, Class<? extends Term<T>> clazz, Function<T, O> function) {
    return function.apply(clazz.cast(term).value());
  }

  /**
   * Creates conditional updater that checks if term is instance of specified class and if so, casts
   * to this class and applies provided function.
   *
   * @param name name of property.
   * @param clazz class of term.
   * @param predicate predicate to check term value.
   * @param function function to apply to term value.
   * @return updater.
   */
  public static <T> Updater put(
      String name,
      Class<? extends Term<T>> clazz,
      Predicate<T> predicate,
      Function<T, ?> function) {
    return (term, styles) -> {
      if (clazz.isInstance(term) && predicate.test(clazz.cast(term).value())) {
        styles.put(name, function.apply(clazz.cast(term).value()));
      }
    };
  }

  /**
   * Creates conditional updater that checks if term is instance of specified class and if so,
   * updates style with term value.
   *
   * @param name name of property.
   * @param clazz class of term.
   * @return updater.
   */
  public static <T> Updater put(String name, Class<? extends Term<T>> clazz) {
    return (term, styles) -> {
      if (clazz.isInstance(term)) styles.put(name, term.value());
    };
  }

  /**
   * Creates conditional updater that checks if term is instance of specified class and if so, casts
   * to this class and applies provided function to its value. The result is used to update style
   * map with specified name.
   *
   * @param name name of property.
   * @param clazz class of term.
   * @param function function to apply to term value.
   * @return updater.
   */
  public static <T> Updater put(
      String name, Class<? extends Term<T>> clazz, Function<T, ?> function) {
    return (term, styles) -> {
      if (clazz.isInstance(term)) styles.put(name, convert(term, clazz, function));
    };
  }

  /**
   * Used to compute css style property value for specified element.
   *
   * @param element element to update calculated style.
   * @param value string representation of css property value.
   */
  public void apply(@NonNull Element element, Term<?> value) {
    @NonNull Map<String, Object> styles = element.resolvedStyle().styles();
    if (value == null) {
      computeAbsent(element, styles);
    } else if (INITIAL.equals(value)) {
      updater.accept(defaultValue, styles);
    } else if (INHERIT.equals(value)) {
      inheritedValue(element, styles);
    } else if (validator.test(value)) {
      try {
        updater.accept(value, styles);
      } catch (Exception t) {
        if (log.isErrorEnabled()) {
          log.error(
              "Error during extracting value from '{}' with '{}' extractor. {}",
              value,
              updater,
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
      updater.accept(defaultValue, styles);
    }
  }

  /** Used to compute value from term and update style map with it. */
  @FunctionalInterface
  public interface Updater extends BiConsumer<Term<?>, Map<String, Object>> {

    /** Creates updater from {@link BiConsumer}. */
    static Updater from(BiConsumer<Term<?>, Map<String, Object>> biConsumer) {
      return biConsumer::accept;
    }

    @Override
    default void accept(Term<?> term, Map<String, Object> stringObjectMap) {
      update(term, stringObjectMap);
    }

    /**
     * Used to resolve term to style map.
     *
     * @param term term to resolve.
     * @param resolvedStyles resolved style map.
     */
    void update(Term<?> term, Map<String, Object> resolvedStyles);

    default Updater or(Updater after) {
      return (term, styles) -> {
        this.update(term, styles);
        after.update(term, styles);
      };
    }
  }

  /** Validator used to check if term is valid for this property. */
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

    default Validator or(@NonNull Validator other) {
      return t -> validate(t) || other.validate(t);
    }

    default Validator and(@NonNull Validator other) {
      return t -> validate(t) && other.validate(t);
    }

    @Override
    default Validator negate() {
      return t -> !validate(t);
    }
  }

  public static Validator isAuto() {
    return checkValue(TermIdent.class, "auto"::equalsIgnoreCase);
  }
}
