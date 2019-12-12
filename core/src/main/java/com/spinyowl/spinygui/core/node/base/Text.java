package com.spinyowl.spinygui.core.node.base;
import com.google.common.base.Objects;

import java.util.StringJoiner;

public final class Text extends Node {
    public static final String ATTRIBUTE_OPERATIONS_ARE_NOT_SUPPORTED
            = "Attribute operations are not supported for Text";
    public static final String CHILD_OPERATIONS_ARE_NOT_SUPPORTED
            = "Child operations are not supported for Text";
private String text;

    public Text() {
    }

    public Text(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

//    /**
//     * Child operations are not supported for Text.
//     *
//     * @param node node.
//     * @throws UnsupportedOperationException because child operations are not supported for Text.
//     */
//    @Override
//    public void removeChild(Node node) {
//        throw new UnsupportedOperationException(CHILD_OPERATIONS_ARE_NOT_SUPPORTED);
//    }
//
//    /**
//     * Child operations are not supported for Text.
//     *
//     * @param node node.
//     * @throws UnsupportedOperationException because child operations are not supported for Text.
//     */
//    @Override
//    public void addChild(Node node) {
//        throw new UnsupportedOperationException(CHILD_OPERATIONS_ARE_NOT_SUPPORTED);
//    }
//
//    @Override
//    public List<Node> getChildNodes() {
//        return Collections.EMPTY_LIST;
//    }
//
//    /**
//     * Attribute operations are not supported for Text.
//     *
//     * @return attribute values.
//     */
//    @Override
//    public Map<String, String> getAttributes() {
//        return Collections.EMPTY_MAP;
//    }
//
//    /**
//     * Attribute operations are not supported for Text.
//     *
//     * @param key   attribute name.
//     * @param value attribute value.
//     * @throws UnsupportedOperationException because attribute operations are not supported for Text.
//     */
//    @Override
//    public void setAttribute(String key, String value) {
//        throw new UnsupportedOperationException(ATTRIBUTE_OPERATIONS_ARE_NOT_SUPPORTED);
//    }

//    /**
//     * Attribute operations are not supported for Text.
//     *
//     * @param key attribute name.
//     * @return attribute value.
//     * @throws UnsupportedOperationException because attribute operations are not supported for Text.
//     */
//    @Override
//    public String getAttribute(String key) {
//        throw new UnsupportedOperationException(ATTRIBUTE_OPERATIONS_ARE_NOT_SUPPORTED);
//    }
//
//    /**
//     * Used to remove attribute.
//     *
//     * @param key attribute name.
//     */
//    @Override
//    public void removeAttribute(String key) {
//        throw new UnsupportedOperationException(ATTRIBUTE_OPERATIONS_ARE_NOT_SUPPORTED);
//    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("text='" + text + "'")
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Text that = (Text) o;
        return Objects.equal(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(text);
    }
}
