package com.spinyowl.spinygui.core.font;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * CSS font weight.
 */
@Data
@EqualsAndHashCode(exclude = "name")
public final class FontWeight {

    private static final Map<String, FontWeight> VALUES = new ConcurrentHashMap<>();

    public static final FontWeight THIN = FontWeight.create(100, "thin");

    public static final FontWeight EXTRA_LIGHT =
        FontWeight.create(200, "extra light", "extralight", "ultra light", "ultralight");

    public static final FontWeight LIGHT = FontWeight.create(300, "light");

    public static final FontWeight NORMAL = FontWeight.create(400, "normal");
    public static final FontWeight BOOK = FontWeight.create(400, "book");
    public static final FontWeight REGULAR = FontWeight.create(400, "regular");

    public static final FontWeight MEDIUM = FontWeight.create(500, "medium");

    public static final FontWeight SEMI_BOLD =
        FontWeight.create(600, "semi bold", "semibold", "demi bold", "demibold");
    public static final FontWeight DEMI_BOLD = SEMI_BOLD;

    public static final FontWeight BOLD = FontWeight.create(700, "bold");

    public static final FontWeight EXTRA_BOLD =
        FontWeight.create(800, "extra bold", "extrabold", "extra bold", "extrabold");
    public static final FontWeight ULTRA_LIGHT = EXTRA_LIGHT;
    public static final FontWeight ULTRA_BOLD = EXTRA_BOLD;

    public static final FontWeight BLACK = FontWeight.create(900, "black", "heavy");
    public static final FontWeight HEAVY = BLACK;

    /**
     * Name of font weight type (should be same as in css specification)
     */
    private final String name;

    /**
     * Font weight
     */
    private final int weight;


    /**
     * Creates font weight element with specified name.
     *
     * @param name   name of font weight type (should be same as in css specification)
     * @param weight int value of font weight that associated with font weight name.
     */
    private FontWeight(int weight, String name) {
        this.name = name;
        this.weight = weight;
    }


    /**
     * Used to create new font weight element with specified name. Note that name will be converted
     * to lower case and it should be the same as names of css font weight property in css
     * specification.
     *
     * @param name name of font weight element.
     * @return new font weight element (or existing one).
     */
    public static FontWeight create(int weight, String name, String... aliases) {
        Objects.requireNonNull(name);
        FontWeight fontWeight = VALUES
            .computeIfAbsent(name.toLowerCase(), key -> new FontWeight(weight, key));
        if (aliases != null) {
            for (String alias : aliases) {
                VALUES.put(alias, new FontWeight(weight, alias));
            }
        }
        return fontWeight;
    }

    /**
     * Used to find font weight element with specified name. Note that name will be converted to
     * lower case and it should be the same as names of css font weight property in css
     * specification.
     *
     * @param name name of font weight element.
     * @return existing font weight element or null.
     */
    public static FontWeight find(String name) {
        Objects.requireNonNull(name);
        return VALUES.get(name.toLowerCase());
    }

    public static FontWeight find(int value) {
        int rounded = Math.round(value / 100f);
        switch (rounded) {
            case 1:
                return THIN;
            case 2:
                return EXTRA_LIGHT;
            case 3:
                return LIGHT;

            case 5:
                return MEDIUM;
            case 6:
                return SEMI_BOLD;
            case 7:
                return BOLD;
            case 8:
                return EXTRA_BOLD;
            case 9:
                return BLACK;

            case 4:
            default:
                return NORMAL;
        }
    }

    /**
     * Returns set of all available font weight values.
     *
     * @return set of all available font weight values.
     */
    public static Set<FontWeight> values() {
        return Set.copyOf(VALUES.values());
    }

    /**
     * Returns true there is a font weight value wth specified name.
     *
     * @param name font weight name.
     * @return true there is a font weight value wth specified name.
     */
    public static boolean contains(String name) {
        if (name == null) {
            return false;
        }
        return values().stream().map(FontWeight::name)
            .anyMatch(v -> v.equalsIgnoreCase(name));
    }
}
