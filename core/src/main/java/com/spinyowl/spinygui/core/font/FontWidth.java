package com.spinyowl.spinygui.core.font;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CSS font width.
 */
public final class FontWidth {

    private static final Map<String, FontWidth> VALUES = new ConcurrentHashMap<>();

    public static final FontWidth ULTRA_CONDENSED = FontWidth.create("ultra-condensed");
    public static final FontWidth EXTRA_CONDENSED = FontWidth.create("extra-condensed");
    public static final FontWidth CONDENSED = FontWidth.create("condensed");
    public static final FontWidth SEMI_CONDENSED = FontWidth.create("semi-condensed");
    public static final FontWidth NORMAL = FontWidth.create("normal");
    public static final FontWidth SEMI_EXPANDED = FontWidth.create("semi-expanded");
    public static final FontWidth EXPANDED = FontWidth.create("expanded");
    public static final FontWidth EXTRA_EXPANDED = FontWidth.create("extra-expanded");
    public static final FontWidth ULTRA_EXPANDED = FontWidth.create("ultra-expanded");

    /**
     * Name of font width type (should be same as in css specification)
     */
    private final String name;

    /**
     * Creates font width element with specified name.
     *
     * @param name name of font width type (should be same as in css specification)
     */
    private FontWidth(String name) {
        this.name = name;
    }


    /**
     * Used to create new font width element with specified name. Note that name will be converted
     * to lower case and it should be the same as names of css font width property in css
     * specification.
     *
     * @param name name of font width element.
     * @return new font width element (or existing one).
     */
    public static FontWidth create(String name) {
        Objects.requireNonNull(name);
        return VALUES.computeIfAbsent(name.toLowerCase(), FontWidth::new);
    }

    /**
     * Used to find font width element with specified name. Note that name will be converted to
     * lower case and it should be the same as names of css font width property in css
     * specification.
     *
     * @param name name of font width element.
     * @return existing font width element or null.
     */
    public static FontWidth find(String name) {
        Objects.requireNonNull(name);
        return VALUES.get(name.toLowerCase());
    }

    /**
     * Returns set of all available font width values.
     *
     * @return set of all available font width values.
     */
    public static Set<FontWidth> values() {
        return Set.copyOf(VALUES.values());
    }

    /**
     * Returns true there is a font width value wth specified name.
     *
     * @param name font width name.
     * @return true there is a font width value wth specified name.
     */
    public static boolean contains(String name) {
        if (name == null) {
            return false;
        }
        return values().stream().map(FontWidth::getName)
            .anyMatch(v -> v.equalsIgnoreCase(name));
    }

    /**
     * Name of font width.
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
        FontWidth fontWidth = (FontWidth) o;
        return Objects.equals(name, fontWidth.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FontWidth.class.getSimpleName() + "[", "]")
            .add("name='" + name + "'")
            .toString();
    }
}
