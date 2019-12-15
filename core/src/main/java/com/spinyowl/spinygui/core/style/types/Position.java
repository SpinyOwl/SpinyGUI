package com.spinyowl.spinygui.core.style.types;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * CSS position.
 */
public final class Position {
    private static final Map<String, Position> VALUES = new ConcurrentHashMap<>();

    /**
     * The element is positioned relative to its first positioned (not static) ancestor element.
     */
    public static final Position ABSOLUTE = Position.of("absolute");
    /**
     * The element is positioned relative to its normal position, so "left:20px" adds 20 pixels to the element's LEFT position.
     */
    public static final Position RELATIVE = Position.of("relative");

//    /** Default value. Elements render in order, as they appear in the document flow. */
//    STATIC,
//    /** The element is positioned relative to the browser window. */
//    FIXED,
//    /** The element is positioned based on the user's scroll position
//     *
//     *  A sticky element toggles between relative and fixed, depending on the scroll position. It is positioned relative until a given offset position is met in the viewport - then it "sticks" in place (like position:fixed).
//     *  Note: Not supported in IE/Edge 15 or earlier. Supported in Safari from version 6.1 with a -webkit- prefix.
//     */
//    STICKY

    /**
     * Name of position type (should be same as in css specification)
     */
    private String name;

    /**
     * Creates position element with specified name.
     *
     * @param name name of position type (should be same as in css specification)
     */
    private Position(String name) {
        this.name = name;
    }

    /**
     * Used to create new position element with specified name.
     * Note that name will be converted to lower case
     * and it should be the same as names of css position property in css specification.
     *
     * @param name name of position element.
     * @return new position element (or existing one).
     */
    public static Position of(String name) {
        Objects.requireNonNull(name);
        return VALUES.computeIfAbsent(name.toLowerCase(), Position::new);
    }

    /**
     * Returns set of all available position values.
     *
     * @return set of all available position values.
     */
    public static Set<Position> values() {
        return Set.copyOf(VALUES.values());
    }

    /**
     * Returns true there is a position value wth specified name.
     *
     * @param name position name.
     * @return true there is a position value wth specified name.
     */
    public static boolean contains(String name) {
        if (name == null) {
            return false;
        }
        return values().stream().map(Position::getName).collect(Collectors.toSet()).contains(name.toLowerCase());
    }

    /**
     * Name of position.
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
        Position position = (Position) o;
        return Objects.equals(name, position.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Position.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .toString();
    }
}
