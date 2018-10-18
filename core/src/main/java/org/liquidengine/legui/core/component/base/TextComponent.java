package org.liquidengine.legui.core.component.base;


import com.google.common.base.Objects;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class TextComponent extends ComponentBase {
    public static final String ATTRIBUTE_OPERATIONS_ARE_NOT_SUPPORTED
            = "Attribute operations are not supported for TextComponent";
    public static final String CHILD_OPERATIONS_ARE_NOT_SUPPORTED
            = "Child operations are not supported for TextComponent";


    private String text;

    public TextComponent() {
    }

    public TextComponent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * Child operations are not supported for TextComponent.
     *
     * @param component component.
     * @throws UnsupportedOperationException because child operations are not supported for TextComponent.
     */
    @Override
    public void removeChild(ComponentBase component) {
        throw new UnsupportedOperationException(CHILD_OPERATIONS_ARE_NOT_SUPPORTED);
    }

    /**
     * Child operations are not supported for TextComponent.
     *
     * @param component component.
     * @throws UnsupportedOperationException because child operations are not supported for TextComponent.
     */
    @Override
    public void addChild(ComponentBase component) {
        throw new UnsupportedOperationException(CHILD_OPERATIONS_ARE_NOT_SUPPORTED);
    }

    @Override
    public List<ComponentBase> getChildBaseComponents() {
        return Collections.EMPTY_LIST;
    }

    /**
     * Attribute operations are not supported for TextComponent.
     *
     * @return attribute values.
     */
    @Override
    public Map<String, String> getAttributes() {
        return Collections.EMPTY_MAP;
    }

    /**
     * Attribute operations are not supported for TextComponent.
     *
     * @param key   attribute name.
     * @param value attribute value.
     * @throws UnsupportedOperationException because attribute operations are not supported for TextComponent.
     */
    @Override
    public void setAttribute(String key, String value) {
        throw new UnsupportedOperationException(ATTRIBUTE_OPERATIONS_ARE_NOT_SUPPORTED);
    }

    /**
     * Attribute operations are not supported for TextComponent.
     *
     * @param key attribute name.
     * @return attribute value.
     * @throws UnsupportedOperationException because attribute operations are not supported for TextComponent.
     */
    @Override
    public String getAttribute(String key) {
        throw new UnsupportedOperationException(ATTRIBUTE_OPERATIONS_ARE_NOT_SUPPORTED);
    }

    /**
     * Used to remove attribute.
     *
     * @param key attribute name.
     */
    @Override
    public void removeAttribute(String key) {
        throw new UnsupportedOperationException(ATTRIBUTE_OPERATIONS_ARE_NOT_SUPPORTED);
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextComponent that = (TextComponent) o;
        return Objects.equal(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(text);
    }
}
