package com.spinyowl.spinygui.core.style.types;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * CSS display.
 */
public final class Display {
    /**
     * Displays an element as a block-level flex container.
     */
    public static final Display FLEX = new Display("flex");
    /**
     * The element is completely removed.
     */
    public static final Display NONE = new Display("none");
    /**
     * Displays an element as a block element. It starts on a new line, and takes up the whole width.
     */
    public static final Display BLOCK = new Display("block");


    private static final Map<String, Display> VALUES = new ConcurrentHashMap<>();

    /**
     * Name of display type (should be same as in css specification)
     */
    private String name;

    /**
     * Creates display element with specified name.
     *
     * @param name name of display type (should be same as in css specification)
     */
    private Display(String name) {
        this.name = name;
    }

    /**
     * Used to create new display element with specified name.
     * Note that name will be converted to lower case
     * and it should be the same as names of css Display property in css specification.
     *
     * @param name name of display element.
     * @return new Display element (or existing one).
     */
    public static Display of(String name) {
        Objects.requireNonNull(name);
        String nameToUse = name.toLowerCase();
        return VALUES.computeIfAbsent(nameToUse, Display::new);
    }

    public static Set<Display> values() {
        return Set.copyOf(VALUES.values());
    }

    public static boolean contains(String name) {
        if (name == null) {
            return false;
        }
        return values().stream().map(Display::getName).collect(Collectors.toSet()).contains(name.toLowerCase());
    }

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
        Display display = (Display) o;
        return Objects.equals(name, display.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Display.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .toString();
    }
}
