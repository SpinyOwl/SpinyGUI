package com.spinyowl.spinygui.core.style.types.flex;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Specifies the alignment between the items inside a flexible container when the items do not use
 * all available space.
 */
public final class JustifyContent {

    private static final Map<String, JustifyContent> VALUES = new ConcurrentHashMap<>();
    /**
     * Default value. Items are positioned at the beginning of the container.
     */
    public static final JustifyContent FLEX_START = JustifyContent.create("flex-start");
    /**
     * Items are positioned at the end of the container.
     */
    public static final JustifyContent FLEX_END = JustifyContent.create("flex-end");
    /**
     * Items are positioned at the center of the container.
     */
    public static final JustifyContent CENTER = JustifyContent.create("center");
    /**
     * Items are positioned with space between the lines.
     */
    public static final JustifyContent SPACE_BETWEEN = JustifyContent.create("space-between");
    /**
     * Items are positioned with space before, between, and after the lines.
     */
    public static final JustifyContent SPACE_AROUND = JustifyContent.create("space-around");
    /**
     * Distribute items evenly. Items have equal space around them.
     */
    public static final JustifyContent SPACE_EVENLY = JustifyContent.create("space-evenly");
    /**
     * Sets this property to its default value.
     */
    public static final JustifyContent INITIAL = JustifyContent.create("initial");
    /**
     * Inherits this property from its parent element..
     */
    public static final JustifyContent INHERIT = JustifyContent.create("inherit");

    private String name;

    private JustifyContent(String name) {
        this.name = name;
    }

    private static JustifyContent create(String name) {
        Objects.requireNonNull(name);
        return VALUES.computeIfAbsent(name.toLowerCase(), JustifyContent::new);
    }

    /**
     * Used to find justify-content element with specified name. Note that name will be converted to
     * lower case and it should be the same as names of css justify-content property in css
     * specification.
     *
     * @param name name of justify-content element.
     * @return existing align-content element or null.
     */
    public static JustifyContent find(String name) {
        Objects.requireNonNull(name);
        return VALUES.get(name.toLowerCase());
    }

    /**
     * Returns set of all available justify-content values.
     *
     * @return set of all available justify-content values.
     */
    public static Set<JustifyContent> values() {
        return Set.copyOf(VALUES.values());
    }

    /**
     * Returns true there is a justify-content value wth specified name.
     *
     * @param name justify-content name.
     * @return true there is a justify-content value wth specified name.
     */
    public static boolean contains(String name) {
        if (name == null) {
            return false;
        }
        return values().stream().map(JustifyContent::getName)
            .anyMatch(v -> v.equalsIgnoreCase(name));
    }

    /**
     * Name of justify-content.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JustifyContent justifyContent = (JustifyContent) o;
        return Objects.equals(name, justifyContent.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JustifyContent.class.getSimpleName() + "[", "]")
            .add("name='" + name + "'")
            .toString();
    }


}
