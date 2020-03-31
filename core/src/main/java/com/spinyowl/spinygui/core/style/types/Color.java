package com.spinyowl.spinygui.core.style.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Color {

    //@formatter:off
    public static final Color BLACK        = new Color(0    , 0    , 0          );
    public static final Color SILVER       = new Color(192  , 192  , 192        );
    public static final Color GRAY         = new Color(128  , 128  , 128        );
    public static final Color WHITE        = new Color(255  , 255  , 255        );
    public static final Color MAROON       = new Color(128  , 0    , 0          );
    public static final Color RED          = new Color(255  , 0    , 0          );
    public static final Color PURPLE       = new Color(128  , 0    , 128        );
    public static final Color FUCHSIA      = new Color(255  , 0    , 255        );
    public static final Color GREEN        = new Color(0    , 128  , 0          );
    public static final Color LIME         = new Color(0    , 255  , 0          );
    public static final Color OLIVE        = new Color(128  , 128  , 0          );
    public static final Color YELLOW       = new Color(255  , 255  , 0          );
    public static final Color NAVY         = new Color(0    , 0    , 128        );
    public static final Color BLUE         = new Color(0    , 0    , 255        );
    public static final Color TEAL         = new Color(0    , 128  , 128        );
    public static final Color AQUA         = new Color(0    , 255  , 255        );
    public static final Color TRANSPARENT  = new Color(0f   , 0f   , 0f    , 0f );
    private static Map<String, Color> colors = new HashMap<>();
    //@formatter:on

    static {
        colors.put("black", BLACK);
        colors.put("silver", SILVER);
        colors.put("gray", GRAY);
        colors.put("white", WHITE);
        colors.put("maroon", MAROON);
        colors.put("red", RED);
        colors.put("purple", PURPLE);
        colors.put("fuchsia", FUCHSIA);
        colors.put("green", GREEN);
        colors.put("lime", LIME);
        colors.put("olive", OLIVE);
        colors.put("yellow", YELLOW);
        colors.put("navy", NAVY);
        colors.put("blue", BLUE);
        colors.put("teal", TEAL);
        colors.put("aqua", AQUA);
        colors.put("transparent", TRANSPARENT);
    }

    private final float r;
    private final float g;
    private final float b;
    private final float a;

    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(int r, int g, int b, int a) {
        this(r, g, b, a / 255f);
    }

    public Color(int r, int g, int b, float a) {
        this(r / 255f, g / 255f, b / 255f, a);
    }

    public Color(float r, float g, float b) {
        this(r, g, b, 1f);
    }

    public Color(int r, int g, int b) {
        this(r / 255f, g / 255f, b / 255f, 1f);
    }

    public static Color getColorByName(String name) {
        return colors.get(name.toLowerCase());
    }

    /**
     * Allowed to parse hex strings in RGB/RGBA/RRGGBB/RRGGBBAA format
     *
     * @param value hex value
     * @return color
     */
    public static Color parseHexString(String value) {
        if (value.startsWith("#")) {
            value = value.substring(1);
        }
        int hex = Integer.parseInt(value, 16);
        switch (value.length()) {
            case 8:
                return new Color((hex >> 24) & 0xFF,
                    (hex >> 16) & 0xFF,
                    (hex >> 8) & 0xFF,
                    hex & 0xFF);
            case 6:
                return new Color(
                    (hex >> 16) & 0xFF,
                    (hex >> 8) & 0xFF,
                    hex & 0xFF);
            case 4:
                return new Color(
                    (hex >> 12) & 0xF,
                    (hex >> 8) & 0xF,
                    (hex >> 4) & 0xF,
                    hex & 0xF);
            case 3:
                return new Color(
                    (hex >> 8) & 0xF,
                    (hex >> 4) & 0xF,
                    hex & 0xF);
            default:
                return null;
        }
    }

    /**
     * Color expression is color represented by three or four integer values divided by comma 'R, G,
     * B' or 'R, G, B, A'
     *
     * @param colorExpression color expression
     * @return color if able to parse or null.
     */
    public static Color parseRGBAColorString(String colorExpression) {
        var values = colorExpression.split(",");
        int length = values.length;
        if (length == 3 || length == 4) {
            int r = Integer.parseInt(values[0].trim());
            int g = Integer.parseInt(values[1].trim());
            int b = Integer.parseInt(values[2].trim());
            if (length == 3) {
                return new Color(r, g, b);
            } else {
                int a = Integer.parseInt(values[3].trim());
                return new Color(r, g, b, a);
            }

        }
        throw new IllegalArgumentException(
            "Color expression should look like 'R, G, B' or 'R, G, B, A' but was '"
                + colorExpression + "'");
    }

    public static boolean exists(String colorName) {
        return colors.containsKey(colorName.toLowerCase());
    }

    public float getR() {
        return r;
    }

    public float getG() {
        return g;
    }

    public float getB() {
        return b;
    }

    public float getA() {
        return a;
    }

    @Override
    public String toString() {
        if (a != 1) {
            return String.format("Color(%f, %f, %f, %f)", r, g, b, a);
        }
        return String.format("Color(%f, %f, %f)", r, g, b);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Color color = (Color) o;
        return Float.compare(color.r, r) == 0 &&
            Float.compare(color.g, g) == 0 &&
            Float.compare(color.b, b) == 0 &&
            Float.compare(color.a, a) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b, a);
    }
}
