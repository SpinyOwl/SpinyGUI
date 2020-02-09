package com.spinyowl.spinygui.core.node.base;

import com.google.common.base.Objects;

import java.util.StringJoiner;

public final class Text extends Node {
    public static final String ATTRIBUTE_OPERATIONS_ARE_NOT_SUPPORTED
            = "Attribute operations are not supported for Text";
    public static final String CHILD_OPERATIONS_ARE_NOT_SUPPORTED
            = "Child operations are not supported for Text";
    private             String content;

    public Text() {
    }

    public Text(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("text='" + content + "'")
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
        return Objects.equal(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(content);
    }
}
