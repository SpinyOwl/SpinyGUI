package com.spinyowl.spinygui.core.converter.css;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.converter.css.util.StyleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Root class that describes property.
 * Should be used to create new classes which implement {@link Property#updateElementStyle(Element)} and {@link Property#isValid()}.
 */
public abstract class Property {
    public static final String INHERIT = "inherit";
    public static final String INITIAL = "initial";

    private static final Logger LOGGER = LoggerFactory.getLogger(Property.class);
    /**
     * Name of css property.
     */
    protected final      String name;

    /**
     * Default value of css property. Could be null if there is no default value.
     */
    protected final String defaultValue;

    /**
     * Defines if css property for element should be inherited from parent element.
     * <br>
     * When no value for an inherited property has been specified on an element,
     * the element gets the computed value of that property on its parent element.
     * Only the root element of the document gets the initial value given in the property's summary.
     * <br>
     * When no value for a non-inherited property has been specified on an element,
     * the element gets the initial value of that property (as specified in the property's summary).
     * <br>
     * The <b>inherit</b> keyword allows authors to explicitly specify inheritance.
     * It works on both inherited and non-inherited properties.
     */
    protected final boolean inherited;

    /**
     * Defines if css property could be animated.
     */
    protected final boolean animatable;

    /**
     * Current value of css property. Could not be null.
     */
    protected String value;

    /**
     * Constructor that should be used by implementations to initialize css property.
     * All implementations should provide at least no-argument constructor.
     *
     * @param name         name of css property.
     * @param defaultValue default value of css property. Could be null if there is no default value.
     * @param inherited    inheritance property. Defines if css property for element should be inherited from parent element.
     *                     <br>
     *                     When no value for an inherited property has been specified on an element,
     *                     the element gets the computed value of that property on its parent element.
     *                     Only the root element of the document gets the initial value given in the property's summary.
     *                     <br>
     *                     When no value for a non-inherited property has been specified on an element,
     *                     the element gets the initial value of that property (as specified in the property's summary).
     *                     <br>
     *                     The <b>inherit</b> keyword allows authors to explicitly specify inheritance.
     *                     It works on both inherited and non-inherited properties.
     * @param animatable   defines if css property could be animated.
     */
    protected Property(String name, String defaultValue, boolean inherited, boolean animatable) {
        this(name, defaultValue, inherited, animatable, defaultValue);
    }

    /**
     * Constructor that should be used by implementations to initialize css property.
     * All implementations should provide at least no-argument constructor.
     *
     * @param name         name of css property.
     * @param defaultValue default value of css property. Could be null if there is no default value.
     * @param inherited    inheritance property. Defines if css property for element should be inherited from parent element.
     *                     <br>
     *                     When no value for an inherited property has been specified on an element,
     *                     the element gets the computed value of that property on its parent element.
     *                     Only the root element of the document gets the initial value given in the property's summary.
     *                     <br>
     *                     When no value for a non-inherited property has been specified on an element,
     *                     the element gets the initial value of that property (as specified in the property's summary).
     *                     <br>
     *                     The <b>inherit</b> keyword allows authors to explicitly specify inheritance.
     *                     It works on both inherited and non-inherited properties.
     * @param animatable   defines if css property could be animated.
     * @param value        current property value.
     */
    protected Property(String name, String defaultValue, boolean inherited, boolean animatable, String value) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.inherited = inherited;
        this.animatable = animatable;
        setValue(value);
    }

    /**
     * Returns true if {@code INHERIT.equalsIgnoreCase(value)}.
     *
     * @return true if {@code INHERIT.equalsIgnoreCase(value)}.
     */
    public boolean equalsInherit() {
        return INHERIT.equalsIgnoreCase(value);
    }

    /**
     * Returns true if {@code INITIAL.equalsIgnoreCase(value)}.
     *
     * @return true if {@code INITIAL.equalsIgnoreCase(value)}.
     */
    public boolean equalsInitial() {
        return INITIAL.equalsIgnoreCase(value);
    }

    public String getValue() {
        return value;
    }

    /**
     * Used to set value to property.
     *
     * @param value value to set.
     */
    public void setValue(String value) {
        if (value != null) {
            this.value = value;
        }
    }

    /**
     * Returns a set of constant property values allowed to use or an empty set (if there are no static values for propery).
     *
     * @return set of constant values or null.
     */
    public Set<String> allowedValues() {
        return Collections.emptySet();
    }

    /**
     * Used to update node style with this property if value is valid.
     *
     * @param element element to update calculated style
     */
    public void updateElementStyle(Element element) {
        if (isValid()) {
            updateNodeStyle(element);
        } else {
            LOGGER.warn("Can't apply '{}' property with value '{}' to an element.", this.getName(), this.getValue());
        }
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    protected abstract void updateNodeStyle(Element element);

    /**
     * Used to update calculated node style of specified element for INITIAL and INHERIT values. No logic for other cases.
     * If {@code INHERIT.equals(value)} and parent style contains null value - uses initial value supplier to set value.
     *
     * @param element              element to update calculated style.
     * @param initialValueSupplier initial value supplier.
     * @param valueSetter          node style value setter.
     * @param valueGetter          node style value getter, used to get style value from parent style.
     * @param <T>                  type of NodeStyle value.
     */
    protected <T> void update(Element element,
                              Supplier<T> initialValueSupplier,
                              BiConsumer<NodeStyle, T> valueSetter,
                              Function<NodeStyle, T> valueGetter) {
        update(element, initialValueSupplier, initialValueSupplier, valueSetter, valueGetter);
    }

    /**
     * Used to update calculated node style of specified element for INITIAL and INHERIT values. No logic for other cases.
     *
     * @param element              element to update calculated style.
     * @param initialValueSupplier initial value supplier.
     * @param nullParentSupplier   value supplier for case when there is null parent style.
     * @param valueSetter          node style value setter.
     * @param valueGetter          node style value getter, used to get style value from parent style.
     * @param <T>                  type of NodeStyle value.
     */
    protected <T> void update(Element element,
                              Supplier<T> initialValueSupplier,
                              Supplier<T> nullParentSupplier,
                              BiConsumer<NodeStyle, T> valueSetter,
                              Function<NodeStyle, T> valueGetter) {
        update(element, initialValueSupplier, nullParentSupplier, valueSetter, valueGetter, null, false);
    }

    /**
     * Used to update calculated node style of specified element for INITIAL, INHERIT and other values.
     * If {@code INHERIT.equals(value)} and parent style contains null value - uses initial value supplier to set value.
     * For other cases initially uses declarationExtractor and if extracted value is null - uses value extractor (when string value!=null).
     *
     * @param element              element to update calculated style.
     * @param initialValueSupplier initial value supplier.
     * @param valueSetter          node style value setter.
     * @param valueGetter          node style value getter, used to get style value from parent style.
     * @param declarationExtractor function to extract value from known declaration.
     * @param <T>                  type of NodeStyle value.
     */
    protected <T> void update(Element element,
                              Supplier<T> initialValueSupplier,
                              BiConsumer<NodeStyle, T> valueSetter,
                              Function<NodeStyle, T> valueGetter,
                              Function<String, T> declarationExtractor) {
        update(element, initialValueSupplier, initialValueSupplier, valueSetter, valueGetter, declarationExtractor);
    }

    /**
     * Used to update calculated node style of specified element for INITIAL, INHERIT and other values.
     * If {@code INHERIT.equals(value)} and parent style contains null value - uses initial value supplier to set value.
     * For other cases initially uses declarationExtractor and if extracted value is null - uses value extractor (when string value!=null).
     *
     * @param element              element to update calculated style.
     * @param initialValueSupplier initial value supplier.
     * @param valueSetter          node style value setter.
     * @param valueGetter          node style value getter, used to get style value from parent style.
     * @param declarationExtractor function to extract value from known declaration.
     * @param setOnNull            used to set value using setter if extractor returned null.
     * @param <T>                  type of NodeStyle value.
     */
    protected <T> void update(Element element,
                              Supplier<T> initialValueSupplier,
                              BiConsumer<NodeStyle, T> valueSetter,
                              Function<NodeStyle, T> valueGetter,
                              Function<String, T> declarationExtractor, boolean setOnNull) {
        update(element, initialValueSupplier, initialValueSupplier, valueSetter, valueGetter, declarationExtractor, setOnNull);
    }

    /**
     * Used to update calculated node style of specified element for INITIAL, INHERIT and other values.
     * For other cases initially uses declarationExtractor and if extracted value is null - uses value extractor (when string value!=null).
     *
     * @param element              element to update calculated style.
     * @param initialValueSupplier initial value supplier.
     * @param nullParentSupplier   value supplier for case when there is null parent style.
     * @param valueSetter          node style value setter.
     * @param valueGetter          node style value getter, used to get style value from parent style.
     * @param declarationExtractor function to extract value from known declaration.
     * @param <T>                  type of NodeStyle value.
     */
    protected <T> void update(Element element,
                              Supplier<T> initialValueSupplier,
                              Supplier<T> nullParentSupplier,
                              BiConsumer<NodeStyle, T> valueSetter,
                              Function<NodeStyle, T> valueGetter,
                              Function<String, T> declarationExtractor) {
        update(element, initialValueSupplier, nullParentSupplier, valueSetter, valueGetter, declarationExtractor, true);
    }

    /**
     * Used to update calculated node style of specified element for INITIAL, INHERIT and other values.
     * For other cases initially uses declarationExtractor and if extracted value is null - uses value extractor (when string value!=null).
     *
     * @param element              element to update calculated style.
     * @param initialValueSupplier initial value supplier.
     * @param nullParentSupplier   value supplier for case when there is null parent style.
     * @param valueSetter          node style value setter.
     * @param valueGetter          node style value getter, used to get style value from parent style.
     * @param declarationExtractor function to extract value from known declaration.
     * @param setOnNull            used to set value using setter if extractor returned null.
     * @param <T>                  type of NodeStyle value.
     */
    protected <T> void update(Element element,
                              Supplier<T> initialValueSupplier,
                              Supplier<T> nullParentSupplier,
                              BiConsumer<NodeStyle, T> valueSetter,
                              Function<NodeStyle, T> valueGetter,
                              Function<String, T> declarationExtractor, boolean setOnNull) {
        NodeStyle nodeStyle = element.getCalculatedStyle();
        if (INITIAL.equalsIgnoreCase(value)) {
            valueSetter.accept(nodeStyle, initialValueSupplier.get());
        } else if (INHERIT.equalsIgnoreCase(value)) {
            NodeStyle parentStyle = StyleUtils.getParentCalculatedStyle(element);
            if (parentStyle != null) {
                valueSetter.accept(nodeStyle, valueGetter.apply(parentStyle));
            } else {
                valueSetter.accept(nodeStyle, nullParentSupplier.get());
            }
        } else if (value != null && declarationExtractor != null) {
            valueSetter.accept(nodeStyle, declarationExtractor.apply(value));
        }
    }

    /**
     * Used to update calculated node style of specified element for INITIAL and INHERIT values. No logic for other cases.
     * If {@code INHERIT.equals(value)} and parent style contains null value - uses initial value supplier to set value.
     *
     * @param element      element to update calculated style.
     * @param initialValue initial value.
     * @param valueSetter  node style value setter.
     * @param valueGetter  node style value getter, used to get style value from parent style.
     * @param <T>          type of NodeStyle value.
     */
    protected <T> void update(Element element,
                              T initialValue,
                              BiConsumer<NodeStyle, T> valueSetter,
                              Function<NodeStyle, T> valueGetter) {
        update(element, initialValue, initialValue, valueSetter, valueGetter);
    }

    /**
     * Used to update calculated node style of specified element for INITIAL and INHERIT values. No logic for other cases.
     *
     * @param element         element to update calculated style.
     * @param initialValue    initial value.
     * @param nullParentValue value for case when there is null parent style.
     * @param valueSetter     node style value setter.
     * @param valueGetter     node style value getter, used to get style value from parent style.
     * @param <T>             type of NodeStyle value.
     */
    protected <T> void update(Element element,
                              T initialValue,
                              T nullParentValue,
                              BiConsumer<NodeStyle, T> valueSetter,
                              Function<NodeStyle, T> valueGetter) {
        this.update(element, initialValue, nullParentValue, valueSetter, valueGetter, null);
    }

    /**
     * Used to update calculated node style of specified element for INITIAL, INHERIT and other values.
     * If {@code INHERIT.equals(value)} and parent style contains null value - uses initial value supplier to set value.
     * For other cases initially uses declarationExtractor and if extracted value is null - uses value extractor (when string value!=null).
     *
     * @param element              element to update calculated style.
     * @param initialValue         initial value.
     * @param valueSetter          node style value setter.
     * @param valueGetter          node style value getter, used to get style value from parent style.
     * @param declarationExtractor function to extract value from known declaration.
     * @param <T>                  type of NodeStyle value.
     */
    protected <T> void update(Element element,
                              T initialValue,
                              BiConsumer<NodeStyle, T> valueSetter,
                              Function<NodeStyle, T> valueGetter,
                              Function<String, T> declarationExtractor) {
        update(element, initialValue, initialValue, valueSetter, valueGetter, declarationExtractor);
    }

    /**
     * Used to update calculated node style of specified element for INITIAL, INHERIT and other values.
     * If {@code INHERIT.equals(value)} and parent style contains null value - uses initial value supplier to set value.
     * For other cases initially uses declarationExtractor and if extracted value is null - uses value extractor (when string value!=null).
     *
     * @param element              element to update calculated style.
     * @param initialValue         initial value.
     * @param valueSetter          node style value setter.
     * @param valueGetter          node style value getter, used to get style value from parent style.
     * @param declarationExtractor function to extract value from known declaration.
     * @param setOnNull            used to set value using setter if extractor returned null.
     * @param <T>                  type of NodeStyle value.
     */
    protected <T> void update(Element element,
                              T initialValue,
                              BiConsumer<NodeStyle, T> valueSetter,
                              Function<NodeStyle, T> valueGetter,
                              Function<String, T> declarationExtractor, boolean setOnNull) {
        update(element, initialValue, initialValue, valueSetter, valueGetter, declarationExtractor, setOnNull);
    }

    /**
     * Used to update calculated node style of specified element for INITIAL, INHERIT and other values.
     * For other cases initially uses declarationExtractor and if extracted value is null - uses value extractor (when string value!=null).
     *
     * @param element              element to update calculated style.
     * @param initialValue         initial value.
     * @param nullParentValue      value for case when there is null parent style.
     * @param valueSetter          node style value setter.
     * @param valueGetter          node style value getter, used to get style value from parent style.
     * @param declarationExtractor function to extract value from known declaration.
     * @param <T>                  type of NodeStyle value.
     */
    protected <T> void update(Element element,
                              T initialValue,
                              T nullParentValue,
                              BiConsumer<NodeStyle, T> valueSetter,
                              Function<NodeStyle, T> valueGetter,
                              Function<String, T> declarationExtractor) {
        this.<T>update(element, () -> initialValue, () -> nullParentValue, valueSetter, valueGetter, declarationExtractor);
    }

    /**
     * Used to update calculated node style of specified element for INITIAL, INHERIT and other values.
     * For other cases initially uses declarationExtractor and if extracted value is null - uses value extractor (when string value!=null).
     *
     * @param element              element to update calculated style.
     * @param initialValue         initial value.
     * @param nullParentValue      value for case when there is null parent style.
     * @param valueSetter          node style value setter.
     * @param valueGetter          node style value getter, used to get style value from parent style.
     * @param declarationExtractor function to extract value from known declaration.
     * @param setOnNull            used to set value using setter if extractor returned null.
     * @param <T>                  type of NodeStyle value.
     */
    protected <T> void update(Element element,
                              T initialValue,
                              T nullParentValue,
                              BiConsumer<NodeStyle, T> valueSetter,
                              Function<NodeStyle, T> valueGetter,
                              Function<String, T> declarationExtractor, boolean setOnNull) {
        this.<T>update(element, () -> initialValue, () -> nullParentValue, valueSetter, valueGetter, declarationExtractor, setOnNull);
    }

    /**
     * Used to check if value is valid or not.
     *
     * @return true if value is valid. By default returns false.
     */
    public boolean isValid() {
        return INHERIT.equalsIgnoreCase(value) || INITIAL.equalsIgnoreCase(value);
    }

    public boolean isInitial() {
        return INITIAL.equalsIgnoreCase(value);
    }

    public boolean isInherit() {
        return INHERIT.equalsIgnoreCase(value);
    }

    /**
     * Returns property name.
     *
     * @return property name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns property default value.
     *
     * @return property default value.
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Defines if css property for element should be inherited from parent element.
     * <br>
     * When no value for an inherited property has been specified on an element,
     * the element gets the computed value of that property on its parent element.
     * Only the root element of the document gets the initial value given in the property's summary.
     * <br>
     * When no value for a non-inherited property has been specified on an element,
     * the element gets the initial value of that property (as specified in the property's summary).
     * <br>
     * The <b>inherit</b> keyword allows authors to explicitly specify inheritance.
     * It works on both inherited and non-inherited properties.
     *
     * @return true if this property inherit value from parent by default.
     */
    public boolean isInherited() {
        return inherited;
    }

    /**
     * Returns true if css property could be animated.
     *
     * @return true if css property could be animated.
     */
    public boolean isAnimatable() {
        return animatable;
    }

    /**
     * Used to reset property value to default.
     */
    public void resetToDefault() {
        this.value = defaultValue;
    }

    public interface NodeStyleUpdater<T> {
        void update(NodeStyle nodeStyle, Supplier<T> supplier);
    }
}
