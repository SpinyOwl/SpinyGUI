package org.liquidengine.legui.core.system.component;


import java.util.Collections;
import java.util.Map;

public class TextNode extends Node {
    public static final String ATTRIBUTE_OPERATIONS_ARE_NOT_SUPPORTED
            = "Attribute operations are not supported for TextNode";
    public static final String CHILD_OPERATIONS_ARE_NOT_SUPPORTED
            = "Child operations are not supported for TextNode";


    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * Child operations are not supported for TextNode.
     *
     * @param node node.
     * @throws UnsupportedOperationException because child operations are not supported for TextNode.
     */
    @Override
    public void removeChild(Node node) {
        throw new UnsupportedOperationException(CHILD_OPERATIONS_ARE_NOT_SUPPORTED);
    }

    /**
     * Child operations are not supported for TextNode.
     *
     * @param node node.
     * @throws UnsupportedOperationException because child operations are not supported for TextNode.
     */
    @Override
    public void addChild(Node node) {
        throw new UnsupportedOperationException(CHILD_OPERATIONS_ARE_NOT_SUPPORTED);
    }

    /**
     * Attribute operations are not supported for TextNode.
     *
     * @return attribute values.
     */
    @Override
    public Map<String, String> getAttributes() {
        return Collections.EMPTY_MAP;
    }

    /**
     * Attribute operations are not supported for TextNode.
     *
     * @param key   attribute name.
     * @param value attribute value.
     * @throws UnsupportedOperationException because attribute operations are not supported for TextNode.
     */
    @Override
    public void setAttribute(String key, String value) {
        throw new UnsupportedOperationException(ATTRIBUTE_OPERATIONS_ARE_NOT_SUPPORTED);
    }

    /**
     * Attribute operations are not supported for TextNode.
     *
     * @param key attribute name.
     * @return attribute value.
     * @throws UnsupportedOperationException because attribute operations are not supported for TextNode.
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
}
